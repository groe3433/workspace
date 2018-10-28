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
import java.util.Calendar;
import java.util.Properties;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * 
 * @author Oxford Consulting Group
 * @version 1.0
 * @date April 15, 2013
 */
public class NWCGRefurbStatusUpdate implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGRefurbStatusUpdate.class);
	
	/**
	 * 
	 * @param arg0
	 * @throws Exception
	 */
	public void setProperties(Properties arg0) throws Exception {

	}

	private String strRevisionNo;

	/**
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 * @throws DOMException
	 * @throws Exception
	 */
	public Document updateRefurbStatus(YFSEnvironment env, Document inXML) throws DOMException, Exception {
		Element elemWO = inXML.getDocumentElement();

		NodeList nlNWCGMasterWorkOrderLine = elemWO.getElementsByTagName("NWCGMasterWorkOrderLine");
		String strDestinationInventoryStatus = "";
		String strSourceLocationID = "";
		String strShipBydate = "";
		Element elemnlNWCGMasterWorkOrderLine = null;
		String strSerialNo = "";
		
		// BEGIN CR576
		String updateDLT = "";
		String flagDLT = "";
		String oldRevisionNo = "";
		// END CR576
		
		String strMWOKey = "";
		String strMWOLineKey = "";
		String strRefurbQty = "";
		
		// BEGIN CR606
		String strIsReplacedItem = "";
		// END CR606
		
		if (nlNWCGMasterWorkOrderLine != null && nlNWCGMasterWorkOrderLine.getLength() > 0) {
			// just one NWCGMasterWorkOrderLine element
			elemnlNWCGMasterWorkOrderLine = (Element) nlNWCGMasterWorkOrderLine.item(0);
			strDestinationInventoryStatus = elemnlNWCGMasterWorkOrderLine.getAttribute("DestinationInventoryStatus");
			strSourceLocationID = elemnlNWCGMasterWorkOrderLine.getAttribute("LocationID");
			strShipBydate = elemnlNWCGMasterWorkOrderLine.getAttribute("ShipByDate");
			strSerialNo = elemnlNWCGMasterWorkOrderLine.getAttribute("PrimarySerialNo");
			strMWOKey = elemnlNWCGMasterWorkOrderLine.getAttribute("MasterWorkOrderKey");
			strMWOLineKey = elemnlNWCGMasterWorkOrderLine.getAttribute("MasterWorkOrderLineKey");

			// BEGIN CR502
			// retrieve strRevisionNo from elemWO
			// strRevisionNo =
			// elemnlNWCGMasterWorkOrderLine.getAttribute("RevisionNo");
			// END CR502
			
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			strRevisionNo = dateFormat.format(calendar.getTime());

			// BEGIN CR576
			// retrieve oldRevisionNo from elemWO
			// oldRevisionNo = elemnlNWCGMasterWorkOrderLine.getAttribute("OldRevisionNo");

			// retrieve updateDLT from elemWO
			updateDLT = elemnlNWCGMasterWorkOrderLine.getAttribute("UpdateDLT");
			// END CR576

			// BEGIN CR606
			strIsReplacedItem = elemnlNWCGMasterWorkOrderLine.getAttribute("IsReplacedItem");
			// END CR606
		}
		
		strSerialNo = strSerialNo.trim();
		String strNode = elemWO.getAttribute("Node");
		String strItemID = elemWO.getAttribute("ItemID");
		String strPC = elemWO.getAttribute("ProductClass");
		strRefurbQty = elemWO.getAttribute("QuantityRequested");
		String strUOM = elemWO.getAttribute("Uom");
		String strUserID = elemWO.getAttribute("UserID");
		String UpdateStr = "";

		if (strDestinationInventoryStatus.equals("RFI")) {
			UpdateStr = "QuantityRFIRefurb";
		}
		if (strDestinationInventoryStatus.equals("UNSERVICE")) {
			UpdateStr = "QuantityUnsRefurb";
		}
		if (strDestinationInventoryStatus.equals("UNSRV-NWT")) {
			UpdateStr = "QuantityUnsNwtRefurb";
		}
		// BEGIN CR671
		if (strDestinationInventoryStatus.equals("MISSING")) {
			UpdateStr = "QuantityMissingRefurb";
		}
		// END CR671

		Document MWODetails = getMWODetails(env, strMWOKey);
		Element MWOElem = MWODetails.getDocumentElement();
		String IncidentNo = MWOElem.getAttribute("IncidentNo");
		String IncidentYear = MWOElem.getAttribute("IncidentYear");
		String ReceiptNo = MWOElem.getAttribute("MasterWorkOrderNo");
		String ReceiptPrice = GetReceiptPrice(env, ReceiptNo, strItemID, strMWOKey, strMWOLineKey);
		NodeList rtnEntries = null;
		if (!strIsReplacedItem.equalsIgnoreCase("Y")) {
			Document rtnEntryList = getEntryForIncidentItem(env, IncidentNo, IncidentYear, strNode, strItemID, strSerialNo, ReceiptPrice);
			rtnEntries = rtnEntryList.getDocumentElement().getElementsByTagName("NWCGIncidentReturn");
		}
		
		// Trackable Items
		if (strSerialNo.length() > 0) {
			boolean CompFlag = false;
			if (rtnEntries != null && rtnEntries.getLength() > 0) {
				Element rtnElem = (Element) rtnEntries.item(0);
				String incidentRtnKey = rtnElem.getAttribute("IncidentReturnKey");
				int updqty = 1;
				updateIncidentItem(env, IncidentNo, IncidentYear, strNode, strItemID, strSerialNo, incidentRtnKey, UpdateStr, updqty);
			}
			// Update parent (kit) item
			/*if (updateDLT.equals("Y")) { 
				if (oldRevisionNo.equals("")) { 
					// this means that the (parent) Item's DLT is NOT required flagDLT = "N"; 
					// thus, set the DLT flag to "N" - no update 
				} else {
					flagDLT = "Y"; 
					// oldRevisionNo is set  
				}
			} else { 
				// no update
				flagDLT = "N"; 
			}*/
			 
			flagDLT = updateDLT;
			updateTrackableItem(env, strItemID, strSerialNo, strDestinationInventoryStatus, CompFlag, flagDLT);

			// Now update all child trackable components below 
			CompFlag = true;
			// flagDLT = updateDLT;
			Document opgetAllChildSerialComps = getAllChildSerialComps(env, strSerialNo, strItemID);
			NodeList serialNodeList = opgetAllChildSerialComps.getDocumentElement().getElementsByTagName("Serial");
			if (serialNodeList.getLength() > 0) {
				for (int cnt1 = 0; cnt1 < serialNodeList.getLength(); cnt1++) {
					Element SerialElem = (Element) serialNodeList.item(cnt1);
					String CompSerialNo = SerialElem.getAttribute("SerialNo");
					String InvItemKey = SerialElem.getAttribute("InventoryItemKey");
					String CompItemID = getItemID(env, InvItemKey);
					updateTrackableItem(env, CompItemID, CompSerialNo, strDestinationInventoryStatus, CompFlag, flagDLT);
				}
			}
			return inXML;
		}

		// Non-Trackable Items
		boolean bUpdtQty = false;
		int confirmQty = new Double(strRefurbQty).intValue();
		if (rtnEntries != null && rtnEntries.getLength() > 0) {
			for (int i = 0; i < rtnEntries.getLength() && !bUpdtQty; i++) {
				Element rtnEntry = (Element) rtnEntries.item(i);
				confirmQty = processRefurbEntryUpdation(env, rtnEntry, IncidentNo, IncidentYear, strNode, strItemID, UpdateStr, confirmQty);
				if (confirmQty < 1) {
					bUpdtQty = true;
				} else if ((confirmQty > 0) && (i == rtnEntries.getLength() - 1)) {
				}
			}
		}
		
		return inXML;
	}

	/**
	 * Call NWCGGetMasterWorkOrderLineListService service to get the price:
	 * 
	 * Sample NWCGGetMasterWorkOrderLineListService XML:
	 * <NWCGMasterWorkOrder MasterWorkOrderKey="20130422090317118764964" MasterWorkOrderLineKey="20130422090317118764968"/>
	 * 
	 * @param env
	 * @param ReceiptNo
	 * @param ItemID
	 * @return
	 */
	public String GetReceiptPrice(YFSEnvironment env, String ReceiptNo, String ItemID, String strMWOKey, String strMWOLineKey) throws Exception {
		// get the receipt line details on the MWO line
		Document OutXML_getMWOLine = getMasterWorkOrderLine(env, strMWOKey, strMWOLineKey);
		String strReceivingPrice = "";
		String strReceiptHeaderKey = "";
		String strReceiptLineKey = "";
		boolean isPriceSet = true;
		int retcnt = 0;
		String Rprice = "0.00";
		Document ReceiptDtls = null;
		Document ReceiptInput = null;
		if(OutXML_getMWOLine != null) {
			NodeList listOutXML_getMWOLine = OutXML_getMWOLine.getElementsByTagName("NWCGMasterWorkOrderLine");
			Node nodeOutXML_getMWOLine = listOutXML_getMWOLine.item(0);
			Element elemOutXML_getMWOLine = (Element)nodeOutXML_getMWOLine;
		
			// SETTING VALUES!
			// This value comes from the NWCGGetMasterWorkOrderLineListService service call. 
			strReceiptLineKey = elemOutXML_getMWOLine.getAttribute("ReceiptLineKey");
			// This value comes from the NWCGGetMasterWorkOrderLineListService service call. 
			strReceiptHeaderKey = elemOutXML_getMWOLine.getAttribute("ReceiptHeaderKey");
			// This value comes from the NWCGGetMasterWorkOrderLineListService service call. 
			strReceivingPrice = elemOutXML_getMWOLine.getAttribute("ReceivingPrice");
			
			// if it is a new MWO generated with the Receipt Details, POST JB2, it will have this ReceivingPrice on the MWO Line
			// NOTE: the new Receiving Price value is defaulted to 0 for all records, therefore must validate based on Receipt Keys
			if(strReceiptLineKey != null && !strReceiptLineKey.trim().equals("") && strReceiptHeaderKey != null && !strReceiptHeaderKey.trim().equals("")) {
				strReceivingPrice = strReceivingPrice;
				return strReceivingPrice;
			} 
			isPriceSet = false;
		}
		// if the MWO details are null OR if the Receiving Price is NOT set, then do it the OLD way...
		if(OutXML_getMWOLine == null || isPriceSet == false) {
			try {
				ReceiptInput = XMLUtil.createDocument("Receipt");
				ReceiptInput.getDocumentElement().setAttribute("ReceiptNo", ReceiptNo);
				ReceiptDtls = CommonUtilities.invokeAPI(env, "getReceiptList", ReceiptInput);
			} catch (Exception e) {
			}

			NodeList ReceiptElmList = ReceiptDtls.getDocumentElement().getElementsByTagName("ReceiptLine");
			retcnt = ReceiptElmList.getLength();
			for (int r = 0; r < retcnt; r++) {
				Element rtnElem = (Element) ReceiptElmList.item(r);
				String RItemID = rtnElem.getAttribute("ItemID");
				if (RItemID.equals(ItemID)) {
					Element ExtnElem = (Element) rtnElem.getElementsByTagName("Extn").item(0);
					Rprice = ExtnElem.getAttribute("ExtnReceivingPrice");
					return Rprice;
				}
			}
		}
		return Rprice;
	}

	/**
	 * Call NWCGGetMasterWorkOrderLineListService Service with the MasterWorkOrderKey & MasterWorkOrderLineKey. 
	 * Example XML Here: <NWCGMasterWorkOrder MasterWorkOrderKey="20130416145019118756303" MasterWorkOrderLineKey="20130416145019118756307"/>
	 * Retrieve ReceiptHeaderKey & ReceiptLineKey from the output XML document. 
	 * 
	 * This Method Created During JB2 development. 
	 * 
	 * @param env
	 * @param MWOKey
	 * @param ItemID
	 * @param ItemQryType
	 * @return
	 * @throws Exception
	 */
	public Document getMasterWorkOrderLine(YFSEnvironment env, String strMasterWorkOrderKey, String strMasterWorkOrderLineKey) throws Exception {
		// create the NWCGMasterWorkOrder using MasterWorkOrderKey & MasterWorkOrderLineKey to retrieve the Receipt Line details that were previously set on the MWO Line. 
		Document getMWOLineIP = XMLUtil.createDocument("NWCGMasterWorkOrder");
		Element GetMWOLineIPElem = getMWOLineIP.getDocumentElement();
		GetMWOLineIPElem.setAttribute("MasterWorkOrderKey", strMasterWorkOrderKey);
		GetMWOLineIPElem.setAttribute("MasterWorkOrderLineKey", strMasterWorkOrderLineKey);
		Document getMWOLineOP = null;
		try {
			if (getMWOLineIP != null) {
				// Invoke the NWCGGetMasterWorkOrderLineListService that will retrieve the Receipt Line details that were previously set on the MWO Line. 
				getMWOLineOP = CommonUtilities.invokeService(env, "NWCGGetMasterWorkOrderLineListService", getMWOLineIP);
			}
		} catch (Exception e) {
			//log.printStackTrace(e);
		}

		return getMWOLineOP;
	}
	
	/**
	 * 
	 * @param env
	 * @param MWOKey
	 * @return
	 * @throws Exception
	 */
	public Document getMWODetails(YFSEnvironment env, String MWOKey) throws Exception {
		Document getMWODetailsIP = XMLUtil.createDocument("NWCGMasterWorkOrder");
		Element GetMWODetailsIPElem = getMWODetailsIP.getDocumentElement();
		GetMWODetailsIPElem.setAttribute("MasterWorkOrderKey", MWOKey);
		Document getMWODetailsOP = null;
		try {
			getMWODetailsOP = CommonUtilities.invokeService(env, "NWCGGetMasterWorkOrderDetailsService", getMWODetailsIP);
		} catch (Exception e) {
			//log.printStackTrace(e);
		}
		return getMWODetailsOP;
	}

	/**
	 * This method updates the refurb quantity
	 * 
	 * @param env
	 * @param rtnEntry
	 * @param qtyBeingConf
	 * @return
	 * @throws NWCGException
	 */
	private int processRefurbEntryUpdation(YFSEnvironment env, Element rtnEntry, String incidentNum, String incidentYear, String cacheID, String itemID, String varToUpdate, int confirmQty) throws Exception {
		int result = 0;
		String strNrfiQty = rtnEntry.getAttribute("QuantityNRFI");
		String strRfiRfbQty = rtnEntry.getAttribute("QuantityRFIRefurb");
		String strUnsRfbQty = rtnEntry.getAttribute("QuantityUnsRefurb");
		String strUnsNwtRfbQty = rtnEntry.getAttribute("QuantityUnsNwtRefurb");
		if (strNrfiQty == null || strRfiRfbQty == null || strUnsRfbQty == null || strUnsNwtRfbQty == null) {
		}

		int nrfiQty = new Double(strNrfiQty).intValue();
		int rfiRfbQty = new Double(strRfiRfbQty).intValue();
		int unsRfbQty = new Double(strUnsRfbQty).intValue();
		int unsNwtRfbQty = new Double(strUnsNwtRfbQty).intValue();
		// available to confirm
		int availQtyToUpdate = nrfiQty - (rfiRfbQty + unsRfbQty + unsNwtRfbQty); 

		if (availQtyToUpdate < 1) {
			result = confirmQty;
		} else {
			int updtQty = 0;
			if (confirmQty <= availQtyToUpdate) {
				updtQty = confirmQty;
				result = 0;
			} else if (confirmQty > availQtyToUpdate) {
				updtQty = availQtyToUpdate;
				result = confirmQty - availQtyToUpdate;
			}

			// Until now, updtQty is the possible quantity that we can update. If we do a blind update, then we will loose the existing data. So, we need to get the appropriate variable and increment the updtQty to that variable.
			if (varToUpdate.equalsIgnoreCase("QuantityRFIRefurb")) {
				updtQty = updtQty + rfiRfbQty;
			} else if (varToUpdate.equalsIgnoreCase("QuantityUnsRefurb")) {
				updtQty = updtQty + unsRfbQty;
			} else if (varToUpdate.equalsIgnoreCase("QuantityUnsNwtRefurb")) {
				updtQty = updtQty + unsNwtRfbQty;
			}

			String incidentRtnKey = rtnEntry.getAttribute("IncidentReturnKey");
			updateIncidentItem(env, incidentNum, incidentYear, cacheID, itemID, "", incidentRtnKey, varToUpdate, updtQty);
		}

		return result;
	}

	/**
	 * This method gets an entry for the given input from NWCG_INCIDENT_RETURN
	 * table
	 * 
	 * @param env
	 * @param incidentNum
	 * @param cacheID
	 * @param itemID
	 * @param serialNum
	 * @return
	 */
	private Document getEntryForIncidentItem(YFSEnvironment env, String incidentNum, String incidentYear, String cacheID, String itemID, String serialNum, String rprice) {
		Document result = null;
		try {
			Document inputDoc = XMLUtil.getDocument();
			Element incidentElem = inputDoc.createElement("NWCGIncidentReturn");
			inputDoc.appendChild(incidentElem);
			incidentElem.setAttribute("CacheID", cacheID);
			incidentElem.setAttribute("IncidentNo", incidentNum);
			incidentElem.setAttribute("IncidentYear", incidentYear);
			incidentElem.setAttribute("ItemID", itemID);
			if (serialNum.length() > 0) {
				incidentElem.setAttribute("QuantityRFIRefurb", "0");
				incidentElem.setAttribute("QuantityNRFI", "1");
				incidentElem.setAttribute("QuantityUnsRefurb", "0");
				incidentElem.setAttribute("QuantityUnsNwtRefurb", "0");
			}
			if (serialNum != null && serialNum.length() > 1) {
				incidentElem.setAttribute("TrackableID", serialNum);
			}
			if (rprice.length() > 1) {
				incidentElem.setAttribute("UnitPrice", rprice);
			}
			result = CommonUtilities.invokeService(env, NWCGConstants.NWCG_RFB_GET_INC_RTN_SERVICE, inputDoc);
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * This method updates the incident item as part of refurb process
	 * 
	 * @param env
	 * @param incidentNum
	 * @param cacheID
	 * @param itemID
	 * @param serialNum
	 * @param incidentRtnKey
	 * @param varToUpdate
	 * @param updtQty
	 * @return
	 */
	private Document updateIncidentItem(YFSEnvironment env, String incidentNum, String incidentYear, String cacheID, String itemID, String serialNum, String incidentRtnKey, String varToUpdate, int updtQty) {
		Document result = null;
		try {
			Document inputDoc = XMLUtil.getDocument();
			Element incidentElem = inputDoc.createElement("NWCGIncidentReturn");
			inputDoc.appendChild(incidentElem);
			incidentElem.setAttribute("CacheID", cacheID);
			incidentElem.setAttribute("IncidentNo", incidentNum);
			incidentElem.setAttribute("IncidentYear", incidentYear);
			incidentElem.setAttribute("ItemID", itemID);
			if (incidentRtnKey != null && incidentRtnKey.length() > 1) {
				incidentElem.setAttribute("IncidentReturnKey", incidentRtnKey);
			}
			if (serialNum != null && serialNum.length() > 1) {
				incidentElem.setAttribute("TrackableID", serialNum);
			}
			incidentElem.setAttribute(varToUpdate, new Integer(updtQty).toString());
			//if (log.isVerboseEnabled()) {
			//}
			result = CommonUtilities.invokeService(env, NWCGConstants.NWCG_RFB_MOD_INC_RTN_SERVICE, inputDoc);
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * 
	 * @param env
	 * @param itemID
	 * @param serialNum
	 * @param DestInvStatus
	 * @param CFlag
	 * @param flagDLT
	 * @return
	 */
	private Document updateTrackableItem(YFSEnvironment env, String itemID, String serialNum, String DestInvStatus, boolean CFlag, String flagDLT) {
		Document result = null;
		try {
			Document inputDoc = XMLUtil.getDocument();
			Element trackableElem = inputDoc.createElement("NWCGTrackableItem");
			inputDoc.appendChild(trackableElem);
			
			trackableElem.setAttribute("ItemID", itemID);
			trackableElem.setAttribute("SerialNo", serialNum);
			trackableElem.setAttribute("Type", "REFURB");
			// update RevisionNo only if the flag is set
			if (flagDLT.equals("Y")) { 
				trackableElem.setAttribute("RevisionNo", strRevisionNo);
			}
			if (DestInvStatus.equals(NWCGConstants.DISP_MISSING)) {
				trackableElem.setAttribute("SerialStatus", NWCGConstants.SERIAL_STATUS_MISSING);
				trackableElem.setAttribute("SerialStatusDesc", NWCGConstants.SERIAL_STATUS_NMISSING_DESC);
			} else if (DestInvStatus.equals(NWCGConstants.DISP_UNSERVICE)) {
				trackableElem.setAttribute("SerialStatus", NWCGConstants.SERIAL_STATUS_UNS);
				trackableElem.setAttribute("SerialStatusDesc", NWCGConstants.SERIAL_STATUS_UNS_DESC);
			} else if (DestInvStatus.equals(NWCGConstants.DISP_UNSERVICE_NWT)) {
				trackableElem.setAttribute("SerialStatus", NWCGConstants.SERIAL_STATUS_UNS);
				trackableElem.setAttribute("SerialStatusDesc",NWCGConstants.SERIAL_STATUS_UNSNWT_DESC);
			} else {
				if (CFlag) {
					trackableElem.setAttribute("SerialStatus", NWCGConstants.SERIAL_STATUS_AVAILABLE_IN_KIT);
					trackableElem.setAttribute("SerialStatusDesc", NWCGConstants.SERIAL_STATUS_AVAILABLE_IN_KIT_DESC);
				} else {
					trackableElem.setAttribute("SerialStatus", NWCGConstants.SERIAL_STATUS_AVAILABLE);
					trackableElem.setAttribute("SerialStatusDesc", NWCGConstants.SERIAL_STATUS_AVAILABLE_DESC);
				}
			}
			trackableElem.setAttribute("SecondarySerial", CommonUtilities.getSecondarySerial(env, serialNum, itemID));
			result = CommonUtilities.invokeService(env, NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE, inputDoc);
		} catch (Exception e) {
		}
		return result;
	}
	
	/**
	 * 
	 * @param env
	 * @param strSerialNo
	 * @param strItemID
	 * @return
	 * @throws Exception
	 */
	private Document getAllChildSerialComps(YFSEnvironment env, String strSerialNo, String strItemID) throws Exception {
		String strSerialKey = "";
		Document docGetSerialListOP = null;
		Document docGetSerialListOP1 = null;
		Document docGetSerialListIP = null;
		Document docGetSerialListIP1 = null;
		Element elemGetSerialListIP = null;
		Element elemGetSerialListIP1 = null;

		docGetSerialListIP = XMLUtil.createDocument("Serial");
		elemGetSerialListIP = docGetSerialListIP.getDocumentElement();
		elemGetSerialListIP.setAttribute("SerialNo", strSerialNo);
		Element el_InvItem = docGetSerialListIP.createElement("InventoryItem");
		elemGetSerialListIP.appendChild(el_InvItem);
		el_InvItem.setAttribute("ItemID", strItemID);
		env.setApiTemplate("getSerialList", "NWCGProcessBlindReturn_getSerialList_GetKey");
		docGetSerialListOP = CommonUtilities.invokeAPI(env, "getSerialList", docGetSerialListIP);
		env.clearApiTemplate("getSerialList");
		
		// should return only one record
		strSerialKey = StringUtil.nonNull(XPathUtil.getString(docGetSerialListOP, "SerialList/Serial/@GlobalSerialKey"));

		// have to reset the api actually midifies the input document
		docGetSerialListIP1 = XMLUtil.createDocument("Serial");
		elemGetSerialListIP1 = docGetSerialListIP1.getDocumentElement();
		
		// get all the serial numbers whoes parent is this serial number will get all the child components clearing off all the child elements
		elemGetSerialListIP1.setAttribute("SerialNo", "");
		
		// getting all the serials whoes parent is this serial
		elemGetSerialListIP1.setAttribute("ParentSerialKey", strSerialKey);
		env.setApiTemplate("getSerialList", "NWCGProcessBlindReturn_getSerialList");

		docGetSerialListOP1 = CommonUtilities.invokeAPI(env, "getSerialList", docGetSerialListIP1);
		env.clearApiTemplate("getSerialList");
		docGetSerialListOP1 = getSubKitComponents(env, docGetSerialListOP1, docGetSerialListOP1);
		
		return docGetSerialListOP1;
	}

	/**
	 * 
	 * @param env
	 * @param SerialDoc
	 * @param SerialInDoc
	 * @return
	 * @throws Exception
	 */
	private Document getSubKitComponents(YFSEnvironment env, Document SerialDoc, Document SerialInDoc) throws Exception {	
		NodeList serialNodeList = SerialDoc.getDocumentElement().getElementsByTagName("Serial");
		for (int count = 0; count < serialNodeList.getLength(); count++) {
			Element SerialElem = (Element) serialNodeList.item(count);
			String serialKey = SerialElem.getAttribute("GlobalSerialKey");
			Document docGetSerialListOP1 = getComponents(env, serialKey);
			NodeList serialNodeList1 = docGetSerialListOP1.getDocumentElement().getElementsByTagName("Serial");
			if (serialNodeList1.getLength() > 0) {
				for (int cnt1 = 0; cnt1 < serialNodeList1.getLength(); cnt1++) {
					Element SerialElem1 = (Element) serialNodeList1.item(cnt1);
					Element ele1 = SerialInDoc.createElement("Serial");
					SerialInDoc.getDocumentElement().appendChild(ele1);
					XMLUtil.copyElement(SerialInDoc, SerialElem1, ele1);
				}
				SerialInDoc = getSubKitComponents(env, docGetSerialListOP1, SerialInDoc);
			}
		}
		return SerialInDoc;
	}

	/**
	 * 
	 * @param env
	 * @param strSerialKey
	 * @return
	 * @throws Exception
	 */
	private Document getComponents(YFSEnvironment env, String strSerialKey) throws Exception {
		Document docGetSerialListIP2 = null;
		Document docGetSerialListOP2 = null;
		Element elemGetSerialListIP2 = null;

		docGetSerialListIP2 = XMLUtil.createDocument("Serial");
		elemGetSerialListIP2 = docGetSerialListIP2.getDocumentElement();

		// getting all the serials whoes parent is this serial
		elemGetSerialListIP2.setAttribute("ParentSerialKey", strSerialKey);
		env.setApiTemplate("getSerialList", "NWCGProcessBlindReturn_getSerialList");

		docGetSerialListOP2 = CommonUtilities.invokeAPI(env, "getSerialList", docGetSerialListIP2);
		
		return docGetSerialListOP2;
	}
	/**
	 * 
	 * @param env
	 * @param strInventoryItemKey
	 * @return
	 * @throws Exception
	 */
	private String getItemID(YFSEnvironment env, String strInventoryItemKey) throws Exception {
		Document docGetInventoryItemIP = XMLUtil.createDocument("InventoryItem");
		Element elemGetInventoryItemIP = docGetInventoryItemIP.getDocumentElement();
		elemGetInventoryItemIP.setAttribute("OrganizationCode", NWCGConstants.ENTERPRISE_CODE);
		elemGetInventoryItemIP.setAttribute("InventoryItemKey", strInventoryItemKey);
		env.setApiTemplate("getInventoryItemList", "NWCGProcessBlindReturn_getInventoryItemList");
		Document docGetInventoryItemOP = CommonUtilities.invokeAPI(env, "getInventoryItemList", docGetInventoryItemIP);
		env.clearApiTemplate("getInventoryItemList");
		Element elemGetInventoryItemOP = docGetInventoryItemOP.getDocumentElement();
		NodeList nlInvItem = elemGetInventoryItemOP.getElementsByTagName("InventoryItem");
		String strItemID = "";
		String strUOM = "";
		if (nlInvItem != null) {
			// should have only one element as we are passing the key
			Element elemInvItem = (Element) nlInvItem.item(0);
			strItemID = elemInvItem.getAttribute("ItemID");
		}
		
		return strItemID;
	}
}
