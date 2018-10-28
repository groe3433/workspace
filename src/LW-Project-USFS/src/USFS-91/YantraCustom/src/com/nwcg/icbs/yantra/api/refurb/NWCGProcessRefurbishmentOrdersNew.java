/* Copyright 2010, Sterling Commerce, Inc. All rights reserved. */
/*
 LIMITATION OF LIABILITY
 THIS SOFTWARE SAMPLE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED 
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 IN NO EVENT SHALL STERLING COMMERCE, Inc. BE LIABLE UNDER ANY THEORY OF 
 LIABILITY (INCLUDING, BUT NOT LIMITED TO, BREACH OF CONTRACT, BREACH 
 OF WARRANTY, TORT, NEGLIGENCE, STRICT LIABILITY, OR ANY OTHER THEORY 
 OF LIABILITY) FOR (i) DIRECT DAMAGES OR INDIRECT, SPECIAL, INCIDENTAL, 
 OR CONSEQUENTIAL DAMAGES SUCH AS, BUT NOT LIMITED TO, EXEMPLARY OR 
 PUNITIVE DAMAGES, OR ANY OTHER SIMILAR DAMAGES, WHETHER OR NOT 
 FORESEEABLE AND WHETHER OR NOT STERLING OR ITS REPRESENTATIVES HAVE 
 BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES, OR (ii) ANY OTHER 
 CLAIM, DEMAND OR DAMAGES WHATSOEVER RESULTING FROM OR ARISING OUT OF
 OR IN CONNECTION THE DELIVERY OR USE OF THIS INFORMATION.
 */

package com.nwcg.icbs.yantra.api.refurb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This API handles refurbishment of items which are returned as NRFI(not ready
 * for issue). If item is a Trackable Kit and it's component(s) are trackable
 * and replaced , then we Dekit the item and rekit with replacing component. If
 * item is a Trackable Kit and it's component(s) are non trackable and replaced,
 * then quantity is adjusted from source location assuming it is consumed. If
 * item is non-trackable kit and it's component is consumed then that quantity
 * is adjusted from source location.
 * 
 * @author Oxford Consulting Group
 * @version 1.0
 * @date August 26, 2013
 */
public class NWCGProcessRefurbishmentOrdersNew implements YIFCustomApi {
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGProcessRefurbishmentOrdersNew.class);

	Boolean isReplacementDone = false;

	Boolean updateDLT = false;

	String strDestinationInventoryStatus = "";

	String strSourceLocationID = "";

	String strShipBydate = "";

	String strParentSerialNo = "";

	String strMasterOrderKey = "";

	String strNode = "";

	String strItemID = "";

	String strPC = "";

	String strRequestQty = "";

	String strUOM = "";

	String strUserID = "";

	String RRPFlag = "";

	String strDestinationStatus = "RFI";

	List<Element> lsReplacedComponents = new ArrayList<Element>();

	List<Element> lsAdjInvComponents = new ArrayList<Element>();

	Map<String, String> hsDekittingMap = new HashMap<String, String>();

	Map<String, Element> hskittingMap = new HashMap<String, Element>();

	/**
	 * 
	 * @param arg0
	 * @throws Exception
	 */
	public void setProperties(Properties arg0) throws Exception {
		// do nothing...
	}

	/**
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 * @throws DOMException
	 * @throws Exception
	 */
	public Document processRefurbishment(YFSEnvironment env, Document inXML) throws DOMException, Exception {
		Element elemWO = inXML.getDocumentElement();
		strNode = elemWO.getAttribute("Node");
		strItemID = elemWO.getAttribute("ItemID");
		strPC = elemWO.getAttribute("ProductClass");
		strRequestQty = elemWO.getAttribute("QuantityRequested");
		strUOM = elemWO.getAttribute("Uom");
		strUserID = elemWO.getAttribute("UserID");
		RRPFlag = NWCGRefurbHelper.get_RRP_Flag(env, strNode);
		Element elemnlNWCGMasterWorkOrderLine = null;
		Document docAdjust = null;

		/*
		 * First check if any replacement of component is done during
		 * refurbishment, so get the list of all the components in the input.
		 */
		NodeList ls = inXML.getElementsByTagName("WorkOrderComponent");
		if (ls.getLength() > 0) {
			// it means item is a kit and components may have been replaced
			for (int i = 0; i < ls.getLength(); i++) {
				// for each component check the price .
				Element eleComponent = (Element) ls.item(i);
				String strComponentQuantity = eleComponent.getAttribute("ComponentQuantity");
				try {
					if (!strComponentQuantity.equals("") && !strComponentQuantity.equals(null)) {
						eleComponent = getItemUnitCostandPopulateValues(env, eleComponent, strComponentQuantity, inXML);						
						/**
						 * need to adjust the inXML with the Total RefurbCost here...
						 * In the example, we need to calculate the RefurbCost (where it equals 74.66)
						 * 
						 * Example: 
						 *      <NWCGMasterWorkOrderLine ActualQuantity="100.0"
				        			DestinationInventoryStatus="RFI" IsReplacedItem="N"
				        			LocationID="GENERAL-REFURB-1"
				        			MasterWorkOrderKey="20130927164122125930904"
				        			MasterWorkOrderLineKey="20130927164123125930906"
				        			RFIRefurbQuantity="2.00" RefurbCost="74.66"
				        			RefurbishedQuantity="3" UNSNWTRefurbQuantity="" UNSRefurbQuantity=""/>
				        */
						inXML = populateTotalRefurbCost(env, eleComponent, inXML);
					} else {
						continue;
					}
				} catch (Exception ex) {
					String message = "!!!!! ERROR: Could not adjust xml element...Please contact support...";
					throw new YFSException(message + ex);
				}
				String strItemUnitPrice = eleComponent.getAttribute("ItemUnitPrice");				
				if (strItemUnitPrice != null && !strItemUnitPrice.equals("")) {
					Double dlItemUnitPrice = Double.parseDouble(strItemUnitPrice);
					// BEGIN - CR865 - Jan. 31, 2013
					/*
					 * for CR865 - solution 1 is to allow non-trackable
					 * components with a 0 Unit Price to be decremented in
					 * inventory when used during the refurbishment process.
					 */
					if (dlItemUnitPrice >= 0) {
						// END - CR865 - Jan. 31, 2013
						String strCompSerialNo = eleComponent.getAttribute("SerialNo");
						if ((strCompSerialNo != null && !strCompSerialNo.equals(""))) {
							/*
							 * if refurb price is present , it means component
							 * is replaced, mark replacement flag to yes and
							 * store the component in a list.
							 */
							lsReplacedComponents.add(eleComponent);
							isReplacementDone = true;
						} else {

							// BEGIN - Production Issue - CR 930 - May 29, 2013

							// check for a valid component quantity and try to parse it.
							if (!strComponentQuantity.trim().equals("")) {
								try {
									Double dQtyVerification = Double.parseDouble(strComponentQuantity);
									lsAdjInvComponents.add(eleComponent);
								} catch (Exception ex) {
									String message = "!!!!! ERROR: Failed in the process to add this Component to the Adjustment list...Please contact support...";
									throw new YFSException(message);
								}
							} else {
								String message = "!!!!! ERROR: Failed to check for a valid Component Qty...Please contact support...";
								throw new YFSException(message);
							}

							// END - Production Issue - CR 930 - May 29, 2013

						}
					} else {
						String message = "!!!!! ERROR: Item Unit Price is NOT Greater Than OR Equal To 0...Please contact support...";
						throw new YFSException(message);
					}
				} else {
					String message = "!!!!! ERROR: Item Unit Price was NOT populated...Please contact support...";
					throw new YFSException(message);
				}
			}
		}

		NodeList lsMasterWorkOrderLine = inXML.getElementsByTagName("NWCGMasterWorkOrderLine");
		if (lsMasterWorkOrderLine.getLength() > 0) {
			elemnlNWCGMasterWorkOrderLine = (Element) lsMasterWorkOrderLine.item(0);
			strDestinationInventoryStatus = elemnlNWCGMasterWorkOrderLine.getAttribute("DestinationInventoryStatus");

			// Added by gaurav for missing status change CR-671
			if (strDestinationInventoryStatus.equals(NWCGConstants.DISP_MISSING))
				strDestinationInventoryStatus = NWCGConstants.DISP_UNSERVICE;

			strSourceLocationID = elemnlNWCGMasterWorkOrderLine.getAttribute("LocationID");
			strShipBydate = elemnlNWCGMasterWorkOrderLine.getAttribute("ShipByDate");
			strParentSerialNo = elemnlNWCGMasterWorkOrderLine.getAttribute("PrimarySerialNo");
			// added for CR 462 starts here
			elemnlNWCGMasterWorkOrderLine.setAttribute("QuantityRequested", elemWO.getAttribute("QuantityRequested"));
			// added for CR 462 ends here
			if (elemnlNWCGMasterWorkOrderLine.getAttribute("UpdateDLT").equalsIgnoreCase("Y"))
				;
			updateDLT = true;
			// Gaurav added for CR 606
			strMasterOrderKey = elemnlNWCGMasterWorkOrderLine.getAttribute("MasterWorkOrderKey");
		}

		// if no component replacement is happening, then just move the item to
		// another location and change the status to RFI
		if (isReplacementDone && lsReplacedComponents.size() > 0 && strDestinationInventoryStatus.equals(NWCGConstants.RFI_STATUS) && strUOM.equals("KT") && strParentSerialNo != null && !strParentSerialNo.equals("")) {
			for (int k = 0; k < lsReplacedComponents.size(); k++) {
				Element eleReplacedComponent = lsReplacedComponents.get(k);
				String strSerialNo = eleReplacedComponent.getAttribute("SerialNo");
				if (strSerialNo != null && !strSerialNo.equals("")) {
					// if component that is replaced is trackable then create a
					// hashmap with component itemId and Component
					createKittingHashMap(env, eleReplacedComponent);
				}
			}

			// ************************* DEKITTING STARTED *******************************************************
			Document docDekittingWorkorder = createDekittingWorkorder(env, elemnlNWCGMasterWorkOrderLine);
			stampIncidentAndOtherDetailsOnWorkOrder(env, elemnlNWCGMasterWorkOrderLine, docDekittingWorkorder);
			NWCGConfirmWorkOrderMultipleItemWrapper oDekitting = new NWCGConfirmWorkOrderMultipleItemWrapper(docDekittingWorkorder, true);
			Document docCreateWO = createWorkOrder(env, oDekitting.getCreateWorkOrderXML());
			// Document docCreateWO = CommonUtilities.invokeAPI(env,"createWorkOrder",docDekittingWorkorder);
			Document confrmWODoc = NWCGRefurbHelper.prepareConfirmWorkderOrderActivityXML(docDekittingWorkorder, elemnlNWCGMasterWorkOrderLine, docCreateWO, "REFURBISHMENT");
			HashMap hmItemComponentKey = NWCGRefurbHelper.prepareMapOfComponentItemAndComponentKey(confrmWODoc);
			Element elemWOCs = (Element) XPathUtil.getNode(confrmWODoc, "WorkOrder/WorkOrderActivityDtl/WorkOrderComponents");
			if (elemWOCs != null)
				NWCGRefurbHelper.replaceChildElementsFromArray(elemWOCs, oDekitting.prepareWorkOrderComponentListForConfirmOrder(hmItemComponentKey), "WorkOrderComponent");

			CommonUtilities.invokeAPI(env, "confirmWorkOrderActivity", confrmWODoc);
			// ************************* DEKITTING COMPLETED *******************************************************

			// ************************* KITTING STARTED *******************************************************
			Document docKittingWorkOrder = createkittingWorkorder(env, elemnlNWCGMasterWorkOrderLine);
			stampIncidentAndOtherDetailsOnWorkOrder(env, elemnlNWCGMasterWorkOrderLine, docKittingWorkOrder);
			// Document docCreateWOKitting = CommonUtilities.invokeAPI(env,"createWorkOrder",docKittingWorkOrder);

			NWCGConfirmWorkOrderMultipleItemWrapper rfbKitting = new NWCGConfirmWorkOrderMultipleItemWrapper(docKittingWorkOrder, true);
			Document docCreateWOKitting = createWorkOrder(env, rfbKitting.getCreateWorkOrderXML());
			Document confrmKittingWODoc = NWCGRefurbHelper.prepareConfirmWorkderOrderActivityXML(docKittingWorkOrder, elemnlNWCGMasterWorkOrderLine, docCreateWOKitting, "REFURBISHMENT");
			hmItemComponentKey = NWCGRefurbHelper.prepareMapOfComponentItemAndComponentKey(confrmKittingWODoc);
			elemWOCs = (Element) XPathUtil.getNode(confrmKittingWODoc, "WorkOrder/WorkOrderActivityDtl/WorkOrderComponents");
			if (elemWOCs != null)
				NWCGRefurbHelper.replaceChildElementsFromArray(elemWOCs, rfbKitting.prepareWorkOrderComponentListForConfirmOrder(hmItemComponentKey), "WorkOrderComponent");
			CommonUtilities.invokeAPI(env, "confirmWorkOrderActivity", confrmKittingWODoc);
			// ************************* KITTING COMPLETED *******************************************************

			for (int m = 0; m < lsReplacedComponents.size(); m++) {
				Element eleReplacedComponent = lsReplacedComponents.get(m);
				String strSerialNo = eleReplacedComponent.getAttribute("OldSerialNo");
				if (strSerialNo != null && !strSerialNo.equals("")) {
					// change the inventory status of replaced component item to NRFI
					docAdjust = NWCGRefurbHelper.getChangeLocationInventoryAttributesIP(env, eleReplacedComponent, NWCGConstants.RFI_STATUS, strSourceLocationID, true, NWCGConstants.NWCG_NRFI_DISPOSITION_CODE, strNode);
					changeInventoryStatus(env, docAdjust);
					// Create new master work order line for the replaced component item so that it can be refurbished.
					createMasterWorkOrderLine(env, eleReplacedComponent, strMasterOrderKey, strNode);
				}
			}

			// move refurbished item to new location.
			moveReburbishedItems(env, elemWO, inXML, NWCGConstants.RFI_STATUS);
		} else {
			moveReburbishedItems(env, elemWO, inXML, NWCGConstants.NRFI_RFB_STATUS);
		}

		// update master workorder line with new status and refurb cost and if DLT is upadted it is updated here.
		updateMasterWorkOrderLineCostAndQuantity(env, elemnlNWCGMasterWorkOrderLine);
		if (lsAdjInvComponents.size() > 0) {
			for (int i = 0; i < lsAdjInvComponents.size(); i++) {
				Element eleComponent = lsAdjInvComponents.get(i);
				adjustLocationInventory(env, eleComponent);
			}
		}
		return inXML;
	}

	/**
	 * (PI 953) Due to inventory not being decremented properly because the JSP
	 * page was not passing all the values. We will take in the
	 * ComponentQuantity the user entered, and use it to obtain the other values
	 * here. ComponentQuantity should come if the JSP onblur method is NOT
	 * called.
	 * 
	 * @param eleComponent
	 * @param strComponentQuantity
	 * @return
	 */
	private Element getItemUnitCostandPopulateValues(YFSEnvironment env, Element eleComponent, String strComponentQuantity, Document inXML) throws Exception {
		try {
			/**
			 * Get current price list. 
			 * Sample Input XML:
			 * <PriceSet ActiveFlag="Y"/>
			 */
			Document docInputPriceList = null;
			docInputPriceList = XMLUtil.newDocument();
			Element elemInputPriceList = docInputPriceList.createElement("PriceSet");
			docInputPriceList.appendChild(elemInputPriceList);
			elemInputPriceList.setAttribute("ActiveFlag", "Y");
			Document docOutputPriceList = CommonUtilities.invokeAPI(env, "getPriceSetList_CurrentPriceList", "getPriceSetList", docInputPriceList);

			/**
			 * Extract details. 
			 * Sample Output XML:
			 * <PriceSetList> <PriceSet
			 * Description="NWCG_2013_PRICE_LIST"
			 * PriceSetKey="20130304160906117351021"/> </PriceSetList>
			 */
			NodeList listOutputPriceList = docOutputPriceList.getElementsByTagName("PriceSet");
			String strPriceSetKey = "", strDescription = "";
			if (listOutputPriceList.getLength() == 1) {
				Node nodeOutputPriceList = listOutputPriceList.item(0);
				Element elemOutputPriceList = (Element) nodeOutputPriceList;
				strPriceSetKey = elemOutputPriceList.getAttribute("PriceSetKey");
				strDescription = elemOutputPriceList.getAttribute("Description");
			}
			// extract details from original element
			String strItemID = eleComponent.getAttribute("ItemID");
			String strPC = eleComponent.getAttribute("ProductClass");
			String strUOM = eleComponent.getAttribute("Uom");

			/**
			 * Get item's price. 
			 * Sample Input XML:
			 * <PriceSet ActiveFlag="Y" OrganizationCode="NWCG" 
			 * PriceSetKey="20130304160906117351021" Description="NWCG_2013_PRICE_LIST" > 
			 * <ItemPriceSetList>
			 * <ItemPriceSet ItemID="000212" ProductClass="Supply" Uom="EA"/>
			 * </ItemPriceSetList> 
			 * </PriceSet>
			 */
			Document docInputPriceDetails = null;
			docInputPriceDetails = XMLUtil.newDocument();
			Element elemInputPriceDetails = docInputPriceDetails.createElement("PriceSet");
			docInputPriceDetails.appendChild(elemInputPriceDetails);
			elemInputPriceDetails.setAttribute("ActiveFlag", "Y");
			elemInputPriceDetails.setAttribute("OrganizationCode", "NWCG");
			elemInputPriceDetails.setAttribute("PriceSetKey", strPriceSetKey);
			elemInputPriceDetails.setAttribute("Description", strDescription);
			Element elemInputPriceDetails_ItemPriceSetList = docInputPriceDetails.createElement("ItemPriceSetList");
			elemInputPriceDetails.appendChild(elemInputPriceDetails_ItemPriceSetList);
			Element elemInputPriceDetails_ItemPriceSetList_ItemPriceSet = docInputPriceDetails.createElement("ItemPriceSet");
			elemInputPriceDetails_ItemPriceSetList.appendChild(elemInputPriceDetails_ItemPriceSetList_ItemPriceSet);
			elemInputPriceDetails_ItemPriceSetList_ItemPriceSet.setAttribute("ItemID", strItemID);
			elemInputPriceDetails_ItemPriceSetList_ItemPriceSet.setAttribute("ProductClass", strPC);
			elemInputPriceDetails_ItemPriceSetList_ItemPriceSet.setAttribute("Uom", strUOM);
			Document docOutputPriceDetails = CommonUtilities.invokeAPI(env, "getPriceSetDetails_CurrentPriceDetails", "getPriceSetDetails", docInputPriceDetails);

			/**
			 * Extract details. 
			 * Sample Output XML:
			 * <PriceSet ActiveFlag="Y" Createprogid=".OMPMasterData"
    		 *	Createts="2013-03-04T16:09:06-05:00" Createuserid="MKumbhare"
    		 *	Currency="USD" Description="NWCG_2013_PRICE_LIST"
    		 *	ItemGroupCode="PROD" Lockid="1" Modifyprogid="ItemPLLoader"
    		 *	Modifyts="2013-03-04T19:55:58-05:00" Modifyuserid="ItemPLLoader"
    		 *	OrganizationCode="NWCG" PriceSetKey="20130304160906117351021"
    		 *  PriceSetName="NWCG_2013_PRICE_LIST" ValidTillDate="2014-03-31">
    		 *	<ItemPriceSetList>
        	 *		<ItemPriceSet RetailPrice="3.52"/>
    		 *	</ItemPriceSetList>
			 *	</PriceSet>
			 */
			String strItemUnitPrice = "";
			NodeList listItemPriceSetList = docOutputPriceDetails.getElementsByTagName("ItemPriceSetList");
			if (listItemPriceSetList.getLength() == 1) {
				Node nodeItemPriceSetList = listItemPriceSetList.item(0);
				Element elemItemPriceSetList = (Element) nodeItemPriceSetList;
				NodeList listItemPriceSet = elemItemPriceSetList.getElementsByTagName("ItemPriceSet");
				if (listItemPriceSet.getLength() == 1) {
					Node nodeItemPriceSet = listItemPriceSet.item(0);
					Element elemItemPriceSet = (Element) nodeItemPriceSet;
					strItemUnitPrice = elemItemPriceSet.getAttribute("RetailPrice");
				}
			}

			// calculate RefurbCost using ItemUnitPrice
			String strRefurbCost = "";
			Double dlRefurbCost = Double.parseDouble(strComponentQuantity) * Double.parseDouble(strItemUnitPrice);
			strRefurbCost = dlRefurbCost.toString();

			/**
			 * Set /Extn/@RefurbCost and /@ItemUnitPrice in ORIGINAL eleComponent.
			 * 
			 * Sample WorkOrderComponent Element: 
			 * <WorkOrderComponent CheckBox="N" ComponentQuantity="3.0" Count="1" ItemID="000035"
			 * ItemUnitPrice="3.52" KitQuantity="4.00" ProductClass="Supply"
			 * ReplaceComponent="NA" Uom="EA" YFC_NODE_NUMBER="1"> 
			 * 		<Extn RefurbCost="10.56"/> 
			 * </WorkOrderComponent>
			 */
			eleComponent.setAttribute("ItemUnitPrice", strItemUnitPrice);
			
			Element elemeleComponent_Extn = inXML.createElement("Extn");
			eleComponent.appendChild(elemeleComponent_Extn);
			elemeleComponent_Extn.setAttribute("RefurbCost", strRefurbCost);
		} catch (Exception ex) {
			throw ex;
		}
		return eleComponent;
	}
	
	/**
	 * populate Total Master Work Order Line RefurbCost
	 * 
	 * @param env
	 * @param eleComponent
	 * @param inXML
	 * @return
	 * @throws Exception
	 */
	private Document populateTotalRefurbCost(YFSEnvironment env, Element eleComponent, Document inXML) throws Exception {
		String strTotalRefurbCost = "";
		double dlTotalRefurbCost = 0;
		if (inXML != null) {
			NodeList listInXML_NWCGMasterWorkOrderLine = inXML.getElementsByTagName("NWCGMasterWorkOrderLine");
			if(listInXML_NWCGMasterWorkOrderLine.getLength() == 1) {
				Node nodeInXML_NWCGMasterWorkOrderLine = listInXML_NWCGMasterWorkOrderLine.item(0);
				Element elemInXML_NWCGMasterWorkOrderLine = (Element)nodeInXML_NWCGMasterWorkOrderLine;
				strTotalRefurbCost = elemInXML_NWCGMasterWorkOrderLine.getAttribute("RefurbCost");
				dlTotalRefurbCost = Double.parseDouble(strTotalRefurbCost);
				
				// sum refurb cost
				if (eleComponent != null) {
					NodeList listExtn = eleComponent.getElementsByTagName("Extn");
					for(int i = 0; i < listExtn.getLength(); i++) {
						Node nodeExtn = listExtn.item(i);
						Element elemExtn = (Element)nodeExtn;
						String strRefurbCost = elemExtn.getAttribute("RefurbCost");
						double dlRefurbCost = Double.parseDouble(strRefurbCost);
						dlTotalRefurbCost = dlTotalRefurbCost + dlRefurbCost;
					}
				}
				
				strTotalRefurbCost = "" + dlTotalRefurbCost;
				elemInXML_NWCGMasterWorkOrderLine.setAttribute("RefurbCost", strTotalRefurbCost);
			}
		}
		
		return inXML;
	}

	/**
	 * 
	 * @param env
	 * @param elemWO
	 * @param inXML
	 * @param strCurrentStatus
	 * @throws Exception
	 */
	private void moveReburbishedItems(YFSEnvironment env, Element elemWO, Document inXML, String strCurrentStatus) throws Exception {
		String strDest = NWCGRefurbHelper.deriveTargetLocationFromSourceLocation(env, strSourceLocationID);
		String strMasterOrderKey = "";
		String strMasterWorkOrderNo = "";
		String strShipmentNo = "";
		String strShipmentKey = "";

		// CR46 - BEGIN For NONRRP: Need to get the MasterWorkOrder number via
		// the MWO Key, prepend a "S" to make the shipment No. then get the
		// Shipment Key and pass it to getMoverequestInput.
		NodeList lsMWOL = inXML.getElementsByTagName("NWCGMasterWorkOrderLine");
		if (lsMWOL.getLength() > 0) {
			Element elemnlNWCGMasterWorkOrderLine = (Element) lsMWOL.item(0);
			strMasterOrderKey = elemnlNWCGMasterWorkOrderLine.getAttribute("MasterWorkOrderKey");
		}
		// CR46 - END

		if (!strDestinationInventoryStatus.equals(NWCGConstants.RFI_STATUS)) {
			if (strDestinationInventoryStatus.equals(NWCGConstants.DISP_UNSERVICE)) {
				strDest = "UNS-1";
			}
			if (strDestinationInventoryStatus.equals(NWCGConstants.DISP_UNSERVICE_NWT)) {
				strDest = "UNSNWT-1";
			}
		}

		Document doc = getMoveLocationXML(env, strNode, strSourceLocationID, strCurrentStatus, strItemID, strPC, strRequestQty, strUOM, strShipBydate, strParentSerialNo, strDest);
		CommonUtilities.invokeAPI(env, "moveLocationInventory", doc);

		if (!isReplacementDone) {
			Document docAdjust = NWCGRefurbHelper.getChangeLocationInventoryAttributesIP(env, elemWO, NWCGConstants.NRFI_RFB_STATUS, strDest, false, strDestinationInventoryStatus, strNode);
			changeInventoryStatus(env, docAdjust);
		}

		if (RRPFlag.equals("NO") && strDestinationInventoryStatus.equals(NWCGConstants.RFI_STATUS)) {
			// CR 46 - BEGIN ML
			Document inDocNWCGGetMasterWorkOrderDetails = XMLUtil.getDocument();
			Element el_NWCGGetMasterWorkOrder = inDocNWCGGetMasterWorkOrderDetails.createElement("NWCGMasterWorkOrder");
			inDocNWCGGetMasterWorkOrderDetails.appendChild(el_NWCGGetMasterWorkOrder);
			el_NWCGGetMasterWorkOrder.setAttribute("MasterWorkOrderKey", strMasterOrderKey);

			Document outDocNWCGGetMasterWorkOrderDetails = CommonUtilities.invokeService(env, "NWCGGetMasterWorkOrderDetailsService", inDocNWCGGetMasterWorkOrderDetails);

			Element outDocRoot = outDocNWCGGetMasterWorkOrderDetails.getDocumentElement();
			strMasterWorkOrderNo = outDocRoot.getAttribute("MasterWorkOrderNo");
			String mwoType = outDocRoot.getAttribute("MasterWorkOrderType");
			String mwoSourceNode = outDocRoot.getAttribute("SourceNode");
			strShipmentNo = (mwoType.equals(NWCGConstants.MASTER_WORKORDER_TYPE_REFURB_TRANSFER)) ? strMasterWorkOrderNo.substring(0, strMasterWorkOrderNo.lastIndexOf("-")) : "S" + strMasterWorkOrderNo;

			Document inGetShipmentDetail = XMLUtil.getDocument();
			Element el_GetShipmentDetail = inGetShipmentDetail.createElement("Shipment");
			inGetShipmentDetail.appendChild(el_GetShipmentDetail);
			el_GetShipmentDetail.setAttribute("SellerOrganizationCode", NWCGConstants.ENTERPRISE_CODE);
			el_GetShipmentDetail.setAttribute("ShipmentNo", strShipmentNo);
			el_GetShipmentDetail.setAttribute("ShipNode", mwoSourceNode);

			Document outDocGetShipmentDetails = CommonUtilities.invokeAPI(env, "getShipmentDetails", inGetShipmentDetail);

			Element outRoot = outDocGetShipmentDetails.getDocumentElement();
			strShipmentKey = outRoot.getAttribute("ShipmentKey");
			// CR 46 END - ML

			String strNewSourceLocationID = NWCGRefurbHelper.deriveTargetLocationFromSourceLocation(env, strSourceLocationID);
			doc = getMoveRequestInputXML(env, strNode, strNewSourceLocationID, strDestinationInventoryStatus, strItemID, strPC, strRequestQty, strUOM, strShipBydate, strShipmentKey, strShipmentNo);

			Element rootMoveElem = doc.getDocumentElement();
			rootMoveElem.setAttribute("ForActivityCode", "STORAGE");
			doc = CommonUtilities.invokeAPI(env, "createMoveRequest", doc);
		}

		// this service is used to update the status of all the trackabe items(both parent and children)
		CommonUtilities.invokeService(env, "NWCGRefurbStatusUpdateService", inXML);
		CommonUtilities.invokeService(env, "NWCGPostCreateRefurbishmentIssueFromRefurbWOMessageService", inXML);
	}

	/**
	 * 
	 * @param env
	 * @param strNode
	 * @param strSourceLocationID
	 * @param strCurrentInvStatus
	 * @param strItemID
	 * @param strPC
	 * @param strRequestQty
	 * @param strUOM
	 * @param strShipBydate
	 * @param strSerialNo
	 * @param strDest
	 * @return
	 * @throws DOMException
	 * @throws Exception
	 */
	private Document getMoveLocationXML(YFSEnvironment env, String strNode, String strSourceLocationID, String strCurrentInvStatus, String strItemID, String strPC, String strRequestQty, String strUOM, String strShipBydate, String strSerialNo, String strDest) throws DOMException, Exception {
		String strDestinationLoc = "";
		Document rt_MoveRequest = XMLUtil.getDocument();
		Element el_MoveRequest = rt_MoveRequest.createElement("MoveLocationInventory");
		// if(strDest.equals("")) { strDestinationLoc =
		// NWCGRefurbHelper.deriveTargetLocationFromSourceLocation(env,strSourceLocationID);
		// }
		strDestinationLoc = strDest;
		rt_MoveRequest.appendChild(el_MoveRequest);
		el_MoveRequest.setAttribute("Node", strNode);
		el_MoveRequest.setAttribute("EnterpriseCode", NWCGConstants.ENTERPRISE_CODE);

		Element ElemSource = rt_MoveRequest.createElement("Source");
		el_MoveRequest.appendChild(ElemSource);
		Element ElemDest = rt_MoveRequest.createElement("Destination");
		el_MoveRequest.appendChild(ElemDest);

		ElemSource.setAttribute("LocationId", strSourceLocationID);
		Element ElemInv = rt_MoveRequest.createElement("Inventory");
		ElemSource.appendChild(ElemInv);
		ElemInv.setAttribute("ShipByDate", strShipBydate);
		ElemInv.setAttribute("InventoryStatus", strCurrentInvStatus);
		ElemInv.setAttribute("Quantity", strRequestQty);
		ElemDest.setAttribute("LocationId", strDestinationLoc);

		Element ElemInvItem = rt_MoveRequest.createElement("InventoryItem");
		ElemInv.appendChild(ElemInvItem);
		ElemInvItem.setAttribute("ItemID", strItemID);
		ElemInvItem.setAttribute("ProductClass", strPC);
		ElemInvItem.setAttribute("UnitOfMeasure", strUOM);

		if (strSerialNo != "") {
			Element ElemSerialList = rt_MoveRequest.createElement("SerialList");
			ElemInv.appendChild(ElemSerialList);
			Element ElemSerialDetail = rt_MoveRequest.createElement("SerialDetail");
			ElemSerialList.appendChild(ElemSerialDetail);
			ElemSerialDetail.setAttribute("SerialNo", strSerialNo);
			ElemInv.setAttribute("Quantity", "1.00");
		}
		return rt_MoveRequest;
	}

	/**
	 * 
	 * @param env
	 * @param inXML
	 * @throws Exception
	 */
	private void changeInventoryStatus(YFSEnvironment env, Document inXML) throws Exception {
		String strItemId = "";
		String strSerialNo = "";
		Element fromInventoryElem = (Element) inXML.getDocumentElement().getElementsByTagName("FromInventory").item(0);
		Element tagDetailElem = (Element) fromInventoryElem.getElementsByTagName("TagDetail").item(0);
		Element eleInventoryItem = (Element) inXML.getDocumentElement().getElementsByTagName("InventoryItem").item(0);
		if (eleInventoryItem != null) {
			strItemId = eleInventoryItem.getAttribute("ItemID");
		}
		Element eleSerialDetail = (Element) inXML.getDocumentElement().getElementsByTagName("SerialDetail").item(0);
		if (eleSerialDetail != null) {
			strSerialNo = eleSerialDetail.getAttribute("SerialNo");
		}
		if (null != tagDetailElem) {
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			String strRevisionNo = dateFormat.format(calendar.getTime());
			if (updateDLT) {
				Element toInventoryElem = (Element) inXML.getDocumentElement().getElementsByTagName("ToInventory").item(0);
				Element tagDetailToElem = (Element) tagDetailElem.cloneNode(true);
				tagDetailToElem.setAttribute("RevisionNo", strRevisionNo);
				toInventoryElem.appendChild(tagDetailToElem);
				if (!strItemId.equals("") && !strSerialNo.equals("")) {
					// update NWCG trackable item table for replaced component item.
					updateTrackableItem(env, strItemId, strSerialNo, strRevisionNo);
				}
			}
		}
		// CR 502 change ends here
		CommonUtilities.invokeAPI(env, "changeLocationInventoryAttributes", inXML);
	}

	/**
	 * 
	 * @param env
	 * @param strNode
	 * @param strSourceLocationID
	 * @param strCurrentInvStatus
	 * @param strItemID
	 * @param strPC
	 * @param strRequestQty
	 * @param strUOM
	 * @param strShipBydate
	 * @param strShipmentKey
	 * @param strShipmentNo
	 * @return
	 * @throws DOMException
	 * @throws Exception
	 */
	private Document getMoveRequestInputXML(YFSEnvironment env, String strNode, String strSourceLocationID, String strCurrentInvStatus, String strItemID, String strPC, String strRequestQty, String strUOM, String strShipBydate, String strShipmentKey, String strShipmentNo) throws DOMException, Exception {
		Document rt_MoveRequest = XMLUtil.getDocument();
		Element el_MoveRequest = rt_MoveRequest.createElement("MoveRequest");
		rt_MoveRequest.appendChild(el_MoveRequest);
		el_MoveRequest.setAttribute("EnterpriseCode", NWCGConstants.ENTERPRISE_CODE);
		el_MoveRequest.setAttribute("ForActivityCode", "VAS");
		el_MoveRequest.setAttribute("FromActivityGroup", "RECEIPT");
		el_MoveRequest.setAttribute("IgnoreOrdering", "Y");
		el_MoveRequest.setAttribute("Node", strNode);
		el_MoveRequest.setAttribute("Priority", "3");
		el_MoveRequest.setAttribute("Release", "Y");
		el_MoveRequest.setAttribute("SourceLocationId", strSourceLocationID);
		// CR46 BEGIN - ML
		el_MoveRequest.setAttribute("ShipmentKey", strShipmentKey);
		// CR46 END - M
		String strDestinationLoc = NWCGRefurbHelper.deriveTargetLocationFromSourceLocation(env, strSourceLocationID);
		el_MoveRequest.setAttribute("TargetLocationId", strDestinationLoc);

		// CR46 BEGIN - ML
		Element el_Shipment = rt_MoveRequest.createElement("Shipment");
		el_MoveRequest.appendChild(el_Shipment);
		el_Shipment.setAttribute("ShipmentNo", strShipmentNo);
		el_Shipment.setAttribute("SellerOrganizationCode", NWCGConstants.ENTERPRISE_CODE);
		// CR46 END - ML

		Element el_MoveRequestLines = rt_MoveRequest.createElement("MoveRequestLines");
		el_MoveRequest.appendChild(el_MoveRequestLines);

		Element el_MoveRequestLine = rt_MoveRequest.createElement("MoveRequestLine");
		el_MoveRequestLines.appendChild(el_MoveRequestLine);
		el_MoveRequestLine.setAttribute("EnterpriseCode", NWCGConstants.ENTERPRISE_CODE);
		el_MoveRequestLine.setAttribute("ShipByDate", strShipBydate);
		el_MoveRequestLine.setAttribute("InventoryStatus", strCurrentInvStatus);
		el_MoveRequestLine.setAttribute("ItemId", strItemID);
		el_MoveRequestLine.setAttribute("ProductClass", strPC);
		el_MoveRequestLine.setAttribute("RequestQuantity", strRequestQty);
		el_MoveRequestLine.setAttribute("SourceLocationId", strSourceLocationID);
		el_MoveRequestLine.setAttribute("TargetLocationId", strDestinationLoc);
		el_MoveRequestLine.setAttribute("UnitOfMeasure", strUOM);

		return rt_MoveRequest;
	}

	/**
	 * 
	 * @param env
	 * @param eleReplacedComponent
	 * @throws Exception
	 */
	private void createKittingHashMap(YFSEnvironment env, Element eleReplacedComponent) throws Exception {
		String strOldSerialNo = eleReplacedComponent.getAttribute("OldSerialNo");
		hskittingMap.put(strOldSerialNo, eleReplacedComponent);
	}

	/**
	 * 
	 * @param env
	 * @param elemnlNWCGMasterWorkOrderLine
	 * @return
	 * @throws Exception
	 */
	private Document createDekittingWorkorder(YFSEnvironment env, Element elemnlNWCGMasterWorkOrderLine) throws Exception {
		Document doc = NWCGRefurbHelper.getRefurbishmentDekittingWorkOrderOrderXML(env, elemnlNWCGMasterWorkOrderLine, strItemID, strUOM, strPC, strRequestQty, strNode);
		return doc;
	}

	/**
	 * 
	 * @param env
	 * @param elemnlNWCGMasterWorkOrderLine
	 * @return
	 * @throws Exception
	 */
	private Document createkittingWorkorder(YFSEnvironment env, Element elemnlNWCGMasterWorkOrderLine) throws Exception {
		Document doc = NWCGRefurbHelper.getRefurbishmentWorkOrderOrderXML(env, elemnlNWCGMasterWorkOrderLine, strItemID, strUOM, strPC, strRequestQty, strNode);
		Element elemConfrmWO = doc.getDocumentElement();
		elemConfrmWO.setAttribute("QuantityRequested", "1.0");
		elemConfrmWO.setAttribute("ServiceItemGroupCode", "KIT");
		elemConfrmWO.setAttribute("ServiceItemID", "REFURB-KITTING");

		Element elemWorkOrderActivities = doc.createElement("WorkOrderActivities");
		elemConfrmWO.appendChild(elemWorkOrderActivities);

		Element elemWorkOrderActivity = doc.createElement("WorkOrderActivity");
		elemWorkOrderActivities.appendChild(elemWorkOrderActivity);

		elemWorkOrderActivity.setAttribute("ActivityCode", "REFURBISHMENT");
		elemWorkOrderActivity.setAttribute("ActivitySeqNo", "1");
		NodeList lsComponentList = doc.getElementsByTagName("WorkOrderComponent");
		if (lsComponentList.getLength() > 0) {
			for (int i = 0; i < lsComponentList.getLength(); i++) {
				Element eleWorkOrderComponent = (Element) lsComponentList.item(i);
				String strSerialNo = eleWorkOrderComponent.getAttribute("SerialNo");
				if (strSerialNo != null && !strSerialNo.equals("") && hskittingMap.containsKey(strSerialNo)) {
					Element eleReplacingComponent = hskittingMap.get(strSerialNo);
					String strOldSerialNo = eleReplacingComponent.getAttribute("OldSerialNo");
					if (strOldSerialNo.equals(strSerialNo)) {
						String strNewSerialNo = eleReplacingComponent.getAttribute("SerialNo");
						String strNewSecondryserialNo = eleReplacingComponent.getAttribute("SecondarySerialNo0");
						Element eleReplacingWorkOrderComponentTag = (Element) eleReplacingComponent.getElementsByTagName("WorkOrderComponentTag").item(0);
						String strNewLotAttribute1 = eleReplacingWorkOrderComponentTag.getAttribute("LotAttribute1");
						String strNewLotAttribute2 = eleReplacingWorkOrderComponentTag.getAttribute("LotAttribute2");
						String strNewLotAttribute3 = eleReplacingWorkOrderComponentTag.getAttribute("LotAttribute3");
						String strNewLotNumber = eleReplacingWorkOrderComponentTag.getAttribute("LotNumber");
						String strManufacturingDate = eleReplacingWorkOrderComponentTag.getAttribute("ManufacturingDate");
						String strRevisionNo = eleReplacingWorkOrderComponentTag.getAttribute("RevisionNo");

						eleWorkOrderComponent.setAttribute("SerialNo", strNewSerialNo);
						eleWorkOrderComponent.setAttribute("SecondarySerialNo0", strNewSecondryserialNo);
						Element eleWorkOrderComponentTag = (Element) eleWorkOrderComponent.getElementsByTagName("WorkOrderComponentTag").item(0);
						eleWorkOrderComponentTag.setAttribute("LotAttribute1", strNewLotAttribute1);
						eleWorkOrderComponentTag.setAttribute("LotAttribute2", strNewLotAttribute2);
						eleWorkOrderComponentTag.setAttribute("LotAttribute3", strNewLotAttribute3);
						eleWorkOrderComponentTag.setAttribute("LotNumber", strNewLotNumber);
						eleWorkOrderComponentTag.setAttribute("ManufacturingDate", strManufacturingDate);
						eleWorkOrderComponentTag.setAttribute("RevisionNo", strRevisionNo);

						NodeList lsSerialDetail = eleWorkOrderComponent.getElementsByTagName("SerialDetail");
						if (lsSerialDetail != null && lsSerialDetail.getLength() > 0) {
							Element eleSerialDetail = (Element) lsSerialDetail.item(0);
							eleSerialDetail.setAttribute("SecondarySerial1", strNewSecondryserialNo);
						}
					}
				}
			}
		}
		return doc;
	}

	/**
	 * 
	 * @param env
	 * @param elemMWOLine
	 * @param docCreateWOInput
	 * @throws Exception
	 */
	private void stampIncidentAndOtherDetailsOnWorkOrder(YFSEnvironment env, Element elemMWOLine, Document docCreateWOInput) throws Exception {
		Document inDoc = XMLUtil.createDocument("NWCGMasterWorkOrder");
		Element elem = inDoc.getDocumentElement();

		elem.setAttribute("MasterWorkOrderKey", elemMWOLine.getAttribute("MasterWorkOrderKey"));
		Document docMWOD = CommonUtilities.invokeService(env, "NWCGGetMasterWorkOrderService", inDoc);

		Element elemExtn = docCreateWOInput.createElement("Extn");
		docCreateWOInput.getDocumentElement().appendChild(elemExtn);

		elemExtn.setAttribute("ExtnRefurbCost", StringUtil.nonNull(elemMWOLine.getAttribute("RefurbCost")));
		elemExtn.setAttribute("ExtnMasterWorkOrderKey", StringUtil.nonNull(elemMWOLine.getAttribute("MasterWorkOrderKey")));
		elemExtn.setAttribute("ExtnMasterWorkOrderLineKey", StringUtil.nonNull(elemMWOLine.getAttribute("MasterWorkOrderLineKey")));

		Element elemMWOD = docMWOD.getDocumentElement();

		elemExtn.setAttribute("ExtnIncidentNo", StringUtil.nonNull(elemMWOD.getAttribute("IncidentNo")));
		elemExtn.setAttribute("ExtnIncidentYear", StringUtil.nonNull(elemMWOD.getAttribute("IncidentYear")));
		elemExtn.setAttribute("ExtnFsAcctCode", StringUtil.nonNull(elemMWOD.getAttribute("FSAccountCode")));
		elemExtn.setAttribute("ExtnBlmAcctCode", StringUtil.nonNull(elemMWOD.getAttribute("BLMAccountCode")));
		elemExtn.setAttribute("ExtnOtherAcctCode", StringUtil.nonNull(elemMWOD.getAttribute("OtherAccountCode")));
		elemExtn.setAttribute("ExtnIsRefurb", "Y");
		elemExtn.setAttribute("ExtnOverrideCode", StringUtil.nonNull(elemMWOD.getAttribute("OverrideCode")));
		elemExtn.setAttribute("ExtnIncidentName", StringUtil.nonNull(elemMWOD.getAttribute("IncidentName")));
	}

	/**
	 * 
	 * @param env
	 * @param elemnlNWCGMasterWorkOrderLine
	 * @throws Exception
	 */
	private void updateMasterWorkOrderLineCostAndQuantity(YFSEnvironment env, Element elemnlNWCGMasterWorkOrderLine) throws Exception {
		Document doc = XMLUtil.getDocument();

		// added for CR 462 starts here
		String strRequestedQuantity = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine.getAttribute("QuantityRequested"));
		String strDestinationInventoryStatus = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine.getAttribute("DestinationInventoryStatus"));
		String strAppendQuantity = "";
		double dAppendQuantity = 0.0;
		double dRequestedQuantity = 0.0;
		if (strRequestedQuantity.equals(""))
			strRequestedQuantity = "0.0";

		dRequestedQuantity = Double.parseDouble(strRequestedQuantity);

		if (strDestinationInventoryStatus.equalsIgnoreCase(NWCGConstants.NWCG_RFI_DISPOSITION_CODE)) {
			strAppendQuantity = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine.getAttribute("RFIRefurbQuantity"));
			if (strAppendQuantity.equals(""))
				strAppendQuantity = "0.0";
			dAppendQuantity = Double.parseDouble(strAppendQuantity);
			dAppendQuantity = dAppendQuantity + dRequestedQuantity;
			strAppendQuantity = Double.toString(dAppendQuantity);
			elemnlNWCGMasterWorkOrderLine.setAttribute("RFIRefurbQuantity", strAppendQuantity);
		} else if (strDestinationInventoryStatus.equalsIgnoreCase(NWCGConstants.SERIAL_STATUS_UNS_DESC)) {
			strAppendQuantity = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine.getAttribute("UNSRefurbQuantity"));
			if (strAppendQuantity.equals(""))
				strAppendQuantity = "0.0";
			dAppendQuantity = Double.parseDouble(strAppendQuantity);
			dAppendQuantity = dAppendQuantity + dRequestedQuantity;
			strAppendQuantity = Double.toString(dAppendQuantity);
			elemnlNWCGMasterWorkOrderLine.setAttribute("UNSRefurbQuantity", strAppendQuantity);
		} else if (strDestinationInventoryStatus.equalsIgnoreCase(NWCGConstants.DISP_UNSERVICE_NWT)) {
			strAppendQuantity = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine.getAttribute("UNSNWTRefurbQuantity"));
			if (strAppendQuantity.equals(""))
				strAppendQuantity = "0.0";
			dAppendQuantity = Double.parseDouble(strAppendQuantity);
			dAppendQuantity = dAppendQuantity + dRequestedQuantity;
			strAppendQuantity = Double.toString(dAppendQuantity);
			elemnlNWCGMasterWorkOrderLine.setAttribute("UNSNWTRefurbQuantity", strAppendQuantity);
		}
		// added for CR 462 ends here

		String strRefurbishedQuantity = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine.getAttribute("RefurbishedQuantity"));
		String strActualQuantity = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine.getAttribute("ActualQuantity"));
		String strTransferQuantity = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine.getAttribute("TransferQty"));

		if (strRefurbishedQuantity.equals(""))
			strRefurbishedQuantity = "0.0";

		if (strActualQuantity.equals(""))
			strActualQuantity = "0.0";

		if (strTransferQuantity.equals(""))
			strTransferQuantity = "0.0";

		double dRefurbishedQuantity = Double.parseDouble(strRefurbishedQuantity);
		double dActualQuantity = Double.parseDouble(strActualQuantity);
		double dTransferQuantity = Double.parseDouble(strTransferQuantity);
		double dRemainingQty = dActualQuantity - dRefurbishedQuantity - dTransferQuantity;
		String lineStatus = (dRemainingQty > 0) ? NWCGConstants.NWCG_REFURB_MWOL_INTERMEDTIATE_STATUS : (dTransferQuantity == dActualQuantity) ? NWCGConstants.NWCG_REFURB_MWOL_TRANSFERRED_STATUS : NWCGConstants.NWCG_REFURB_MWOL_FINAL_STATUS;

		elemnlNWCGMasterWorkOrderLine.setAttribute("Status", lineStatus);

		Element rootElem = (Element) doc.importNode(elemnlNWCGMasterWorkOrderLine, true);
		doc.appendChild(rootElem);

		CommonUtilities.invokeService(env, "NWCGChangeMasterWorkOrderLineService", doc);
	}

	/**
	 * 
	 * @param env
	 * @param elemWO
	 * @param strMasterWorkOrderKey
	 * @param strNode
	 * @throws Exception
	 */
	private static void createMasterWorkOrderLine(YFSEnvironment env, Element elemWO, String strMasterWorkOrderKey, String strNode) throws Exception {
		String strSecondrySerialNo1 = "";
		String strManufactureName = "";
		String strManufacturerModel = "";
		String strOwnerUnitId = "";
		String strLotNo = "";
		String strRevisionNo = "";
		String strShortDescription = "";
		String strManufacturingDate = "";
		String strOldSerialNo = elemWO.getAttribute("OldSerialNo");
		String strItemId = elemWO.getAttribute("ItemID");

		Document serialDoc = XMLUtil.createDocument("Serial");
		Element serialRoot = serialDoc.getDocumentElement();
		serialRoot.setAttribute("SerialNo", strOldSerialNo);

		Document resultDoc = CommonUtilities.invokeAPI(env, "getSerialList", serialDoc);
		NodeList serial = resultDoc.getElementsByTagName("Serial");
		if (serial != null && serial.getLength() > 0) {
			Element eleSerial = (Element) serial.item(0);
			strSecondrySerialNo1 = eleSerial.getAttribute("SecondarySerial1");
			NodeList tag = eleSerial.getElementsByTagName("TagDetail");
			if (tag != null && tag.getLength() > 0) {
				Element eleTag = (Element) tag.item(0);
				strManufactureName = eleTag.getAttribute("LotAttribute1");
				strManufacturerModel = eleTag.getAttribute("LotAttribute3");
				strOwnerUnitId = eleTag.getAttribute("LotAttribute2");
				strLotNo = eleTag.getAttribute("LotNumber");
				strRevisionNo = eleTag.getAttribute("RevisionNo");
				strManufacturingDate = eleTag.getAttribute("ManufacturingDate");
			}
		}
		Document itemDoc = XMLUtil.createDocument("Item");
		Element itemRoot = itemDoc.getDocumentElement();
		itemRoot.setAttribute("ItemID", strItemId);
		itemRoot.setAttribute("UnitOfMeasure", elemWO.getAttribute("Uom"));
		itemRoot.setAttribute("OrganizationCode", "NWCG");
		Document itemResultDoc = CommonUtilities.invokeAPI(env, "getItemDetails", itemDoc);
		NodeList primaryInfo = itemResultDoc.getElementsByTagName("PrimaryInformation");
		if (primaryInfo != null && primaryInfo.getLength() > 0) {
			Element elePrimaryInfo = (Element) primaryInfo.item(0);
			strShortDescription = elePrimaryInfo.getAttribute("ShortDescription");
		}
		Document retDoc = XMLUtil.createDocument("NWCGMasterWorkOrderLine");
		Element eleRoot = retDoc.getDocumentElement();
		eleRoot.setAttribute("MasterWorkOrderKey", strMasterWorkOrderKey);
		eleRoot.setAttribute("Node", strNode);
		eleRoot.setAttribute("ItemID", strItemId);
		eleRoot.setAttribute("ItemDesc", strShortDescription);
		eleRoot.setAttribute("PrimarySerialNo", strOldSerialNo);
		eleRoot.setAttribute("ProductClass", elemWO.getAttribute("ProductClass"));
		eleRoot.setAttribute("UnitOfMeasure", elemWO.getAttribute("Uom"));
		eleRoot.setAttribute("Status", "Awaiting Work Order Creation");
		eleRoot.setAttribute("ActualQuantity", elemWO.getAttribute("ComponentQuantity"));
		eleRoot.setAttribute("SecondrySerialNo1", strSecondrySerialNo1);
		eleRoot.setAttribute("ManufacturerName", strManufactureName);
		eleRoot.setAttribute("ManufacturerModel", strManufacturerModel);
		eleRoot.setAttribute("OwnerUnitID", strOwnerUnitId);
		eleRoot.setAttribute("LotNo", strLotNo);
		eleRoot.setAttribute("RevisionNo", strRevisionNo);
		eleRoot.setAttribute("ManufacturingDate", strManufacturingDate);
		eleRoot.setAttribute("IsReplacedItem", "Y");
		CommonUtilities.invokeService(env, "NWCGCreateMasterWorkOrderLineService", retDoc);

		// Updating status of the new WorkOrderline serial no
		Document updateDoc = XMLUtil.createDocument("NWCGTrackableItem");
		Element eleUpdate = updateDoc.getDocumentElement();
		eleUpdate.setAttribute("ItemID", strItemId);
		eleUpdate.setAttribute("SerialNo", strOldSerialNo);
		eleUpdate.setAttribute("SecondarySerial", strSecondrySerialNo1);
		eleUpdate.setAttribute("SerialStatus", "W");
		eleUpdate.setAttribute("SerialStatusDesc", "Workordered");
		CommonUtilities.invokeService(env, NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE, updateDoc);

		// Updating kit information for newly replaced item
		Document inDoc = XMLUtil.createDocument("NWCGTrackableItem");
		Element inDocRoot = inDoc.getDocumentElement();
		inDocRoot.setAttribute("ItemID", strItemId);
		inDocRoot.setAttribute("SerialNo", strOldSerialNo);
		inDocRoot.setAttribute("SecondarySerial", strSecondrySerialNo1);
		Document outDoc = CommonUtilities.invokeService(env, "NWCGGetTrackableInventoryRecordService", inDoc);

		Element resultRoot = outDoc.getDocumentElement();
		String strKitItemId = resultRoot.getAttribute("KitItemID");
		String strNewSerialNo = elemWO.getAttribute("SerialNo");
		String strKitSerialNo = resultRoot.getAttribute("KitSerialNo");
		Document trackDoc = XMLUtil.createDocument("NWCGTrackableItem");
		Element eleTrackableItemRoot = trackDoc.getDocumentElement();
		eleTrackableItemRoot.setAttribute("ItemID", strItemId);
		eleTrackableItemRoot.setAttribute("SerialNo", strNewSerialNo);
		eleTrackableItemRoot.setAttribute("SecondarySerial", elemWO.getAttribute("SecondarySerialNo0"));
		eleTrackableItemRoot.setAttribute("KitItemID", strKitItemId);
		eleTrackableItemRoot.setAttribute("KitSerialNo", strKitSerialNo);
		CommonUtilities.invokeService(env, NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE, trackDoc);
	}

	// Gaurav added for CR 606 END

	/**
	 * 
	 * @param env
	 * @param itemID
	 * @param serialNum
	 * @param strRevisionNo
	 * @throws Exception
	 */
	private void updateTrackableItem(YFSEnvironment env, String itemID, String serialNum, String strRevisionNo) throws Exception {
		Document inputDoc = XMLUtil.getDocument();
		Element trackableElem = inputDoc.createElement("NWCGTrackableItem");
		inputDoc.appendChild(trackableElem);
		trackableElem.setAttribute("ItemID", itemID);
		trackableElem.setAttribute("SerialNo", serialNum);
		trackableElem.setAttribute("Type", "REFURB");
		trackableElem.setAttribute("RevisionNo", strRevisionNo);
		trackableElem.setAttribute("SecondarySerial", CommonUtilities.getSecondarySerial(env, serialNum, itemID));
		CommonUtilities.invokeService(env, NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE, inputDoc);
	}

	/**
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 * @throws Exception
	 */
	private Document createWorkOrder(YFSEnvironment env, Document inXML) throws Exception {
		if (logger.isVerboseEnabled())
			logger.verbose("NWCGProcessRefurbishmentOrders::createWorkOrder :: IP " + XMLUtil.getXMLString(inXML));
		return CommonUtilities.invokeAPI(env, "createWorkOrder", inXML);
	}

	/**
	 * 
	 * @param env
	 * @param eleComponent
	 * @throws Exception
	 */
	private void adjustLocationInventory(YFSEnvironment env, Element eleComponent) throws Exception {
		Document docAdjustInv = XMLUtil.createDocument("AdjustLocationInventory");
		Element eleRoot = docAdjustInv.getDocumentElement();
		eleRoot.setAttribute("EnterpriseCode", "NWCG");
		eleRoot.setAttribute("Node", strNode);

		Element eleSource = docAdjustInv.createElement("Source");
		eleSource.setAttribute("LocationId", strSourceLocationID);
		eleRoot.appendChild(eleSource);

		Element eleInventory = docAdjustInv.createElement("Inventory");
		String strCompQuant = "-" + eleComponent.getAttribute("ComponentQuantity");
		eleInventory.setAttribute("Quantity", strCompQuant);
		//CR 755 - To Correct Muliple Status of Consumables during Process refurb
		eleInventory.setAttribute("InventoryStatus", NWCGConstants.RFI_STATUS);
		eleSource.appendChild(eleInventory);

		Element eleInventoryItem = docAdjustInv.createElement("InventoryItem");
		eleInventoryItem.setAttribute("ItemID", eleComponent.getAttribute("ItemID"));
		eleInventoryItem.setAttribute("ProductClass", eleComponent.getAttribute("ProductClass"));
		eleInventoryItem.setAttribute("UnitOfMeasure", eleComponent.getAttribute("Uom"));
		eleInventory.appendChild(eleInventoryItem);

		Element el_Audit = docAdjustInv.createElement("Audit");
		eleRoot.appendChild(el_Audit);
		el_Audit.setAttribute("ReasonCode", "REFURB-ADJ");
		el_Audit.setAttribute("ReasonText", "QUANTITY CONSUMED DUE TO REFURBISHMENT");

		CommonUtilities.invokeAPI(env, "adjustLocationInventory", docAdjustInv);
	}
}