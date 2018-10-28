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

package com.nwcg.icbs.yantra.ue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathWrapper;
import com.yantra.wms.japi.ue.WMSBeforeAdjustInventoryUE;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;

/**
 * 
 * @author Oxford Consulting Group
 * @version 2.0
 * @date March 13, 2014
 */
public class NWCGValidateTrackableIDUE implements WMSBeforeAdjustInventoryUE {

	private static Logger log = Logger.getLogger(NWCGValidateTrackableIDUE.class.getName());

	public Document beforeAdjustInventory(YFSEnvironment env, Document inXML) throws YFSUserExitException {
		System.out.println("@@@@@ Entering NWCGValidateTrackableIDUE::beforeAdjustInventory @@@@@");
		System.out.println("@@@@@ inXML " + XMLUtil.getXMLString(inXML));
		
		Element root = inXML.getDocumentElement();
		
		NodeList InvAuditList = inXML.getDocumentElement().getElementsByTagName("Audit");
		Element InvAuditEl = (Element) InvAuditList.item(0);
		String CountRequestKey = InvAuditEl.getAttribute("CountRequestKey");
		String ReasonCode = InvAuditEl.getAttribute("ReasonCode");

		Element inventoryElement = (Element) inXML.getDocumentElement().getElementsByTagName("Inventory").item(0);
		String inventoryStatus = inventoryElement.getAttribute("InventoryStatus");

		// BEGIN - CR 904 - March 13, 2014 - The AdjustInvalidInventory Method is brand new as of this CR 904. 
		inXML = AdjustInvalidInventory(env, inXML);
		// END - CR 904 - March 13, 2014
		
		try {
			if (ReasonCode.equals("PC") && CountRequestKey.length() > 0) {
				if (inventoryStatus.equals("NRFI-RFB")) {
					throw new YFSUserExitException("Cannot accept variance in " + inventoryStatus + " status on this inventory. Use return/refurb process to adjudicate any discrepancies.");
				}
				Document inDoc = XMLUtil.newDocument();
				Document outDoc = XMLUtil.newDocument();
				String SerialNo = "";
				String ItemID = "";
				NodeList InvSerialOutList = null;
				NodeList InvSerialList = inXML.getDocumentElement().getElementsByTagName("SerialDetail");
				if (InvSerialList.getLength() > 0) {
					Element InvSerialEl = (Element) InvSerialList.item(0);
					SerialNo = InvSerialEl.getAttribute("SerialNo");
					System.out.println("@@@@@ SerialNo " + SerialNo);
				}
				System.out.println("SerialNo " + SerialNo);
				NodeList InvItemList = inXML.getDocumentElement().getElementsByTagName("InventoryItem");
				if (InvItemList.getLength() > 0) {
					Element InvItemEl = (Element) InvItemList.item(0);
					ItemID = InvItemEl.getAttribute("ItemID");
					System.out.println("@@@@@ ItemID " + ItemID);
				}
				String CacheID = root.getAttribute("Node");
				System.out.println("@@@@@ ItemID " + ItemID);
				if (SerialNo.length() > 0 && ItemID.length() > 0) {
					Element el_PSerial = inDoc.createElement("Serial");
					inDoc.appendChild(el_PSerial);
					el_PSerial.setAttribute("SerialNo", SerialNo);
					el_PSerial.setAttribute("ShipNode", CacheID);
					Element el_InvItem = inDoc.createElement("InventoryItem");
					el_PSerial.appendChild(el_InvItem);
					el_InvItem.setAttribute("ItemID", ItemID);
					
					System.out.println("@@@@@ inDoc " + XMLUtil.getXMLString(inDoc));
					outDoc = CommonUtilities.invokeAPI(env, "getSerialList", inDoc);
					System.out.println("@@@@@ outDoc " + XMLUtil.getXMLString(outDoc));
					
					InvSerialOutList = outDoc.getDocumentElement().getElementsByTagName("Serial");
					System.out.println("@@@@@ InvSerialOutList : " + InvSerialOutList.getLength());
					if (InvSerialOutList.getLength() == 0) {
						System.out.println("!!!!! Before Exception");
						throw new YFSUserExitException("Invalid Trackable ID !!!");
					} else {
						System.out.println("@@@@@ Exiting NWCGValidateTrackableIDUE::beforeAdjustInventory (1) @@@@@");
						return inXML;
					}
				} else {
					System.out.println("@@@@@ Exiting NWCGValidateTrackableIDUE::beforeAdjustInventory (2) @@@@@");
					return inXML;
				}
			} else {
				System.out.println("@@@@@ Exiting NWCGValidateTrackableIDUE::beforeAdjustInventory (3) @@@@@");
				return inXML;
			}
		} catch (Exception E) {
			System.out.println("!!!!! Generated Exception: " + E.toString());
			throw new YFSUserExitException(E.toString());
		}
	}
	
	/**
	 * @author Oxford Consulting Group
	 * @version 2.0
	 * @date March 13, 2014
	 * 
	 * Description - This method will adjust invalid NRFI-RFB inventory for the refurbishment location so that 
	 * inventory consumed during htis process can be "uniquely detemined". 
	 * 
	 * Sudo Code ::
	 * if WorkOrderKey is valid and not null
	 * 		call getWorkOrderDetails API and get the type of VAS Process (KITTING/DEKITTING/ETC...)
	 * 		if the type is "KITTING"
	 * 			then call getNodeInventory API for that item through the API input xml
	 * 			check if the item has inventory in "NRFI-RFB" status
	 * 			then this inventory using AdjustLocationInventory API with InventoryStatus as "RFI"
	 * 
	 * Sample of an inXML before anything happens to it:
	 * <AdjustLocationInventory EnterpriseCode="NWCG" Node="IDGBK">
	 * 	<Audit DocumentType="7001" WorkOrderKey="20140313110243154247664"/>
	 * 	<Source LocationId="GENERAL-REFURB-1">
	 * 		<Inventory Quantity="-1.00" Segment="" SegmentType="" ShipByDate="2500-01-01">
	 * 			<InventoryItem ItemID="000045" ProductClass="Supply" UnitOfMeasure="PR"/>
	 * 		</Inventory>
	 * 	</Source>
	 * </AdjustLocationInventory>
	 * 
	 * Sample of the getWorkOrderDetails input XML:
	 * <WorkOrder WorkOrderNo="20140313110243154247664" />
	 * 
	 * Sample of the getNodeInventory input XML:
	 * <NodeInventory LocationId="GENERAL-REFURB-1" Node="IDGBK">
	 * 	<Inventory>
	 * 		<InventoryItem ItemID="000045" ProductClass="Supply" UnitOfMeasure="PR" /> 
	 * 	</Inventory>
	 * </NodeInventory>
	 * 
	 * Sample of an inXML after we had to modify it by adding the InventoryStatus attribute to the Inventory element:
	 * <AdjustLocationInventory EnterpriseCode="NWCG" Node="IDGBK">
	 * 	<Audit DocumentType="7001" WorkOrderKey="20140313144235154251284"/>
	 * 	<Source LocationId="GENERAL-REFURB-1">
	 * 		<Inventory InventoryStatus="RFI" Quantity="-1.00" Segment="" SegmentType="" ShipByDate="2500-01-01">
	 * 			<InventoryItem ItemID="000045" ProductClass="Supply" UnitOfMeasure="PR"/>
	 * 		</Inventory>
	 * 	</Source>
	 * </AdjustLocationInventory>
	 * 
	 * @param inXML
	 * @return inXML
	 */
	public Document AdjustInvalidInventory(YFSEnvironment env, Document inXML) {
		System.out.println("@@@@@ Entering NWCGValidateTrackableIDUE::AdjustInvalidInventory @@@@@");
		System.out.println("@@@@@ inXML " + XMLUtil.getXMLString(inXML));
		
		try {
			// get inXML - Node value
            XPathWrapper inXML_AdjustLocationInventory_pathWrapper = new XPathWrapper(inXML);
            Element elemAdjustLocationInventory = (Element)inXML_AdjustLocationInventory_pathWrapper.getNode(NWCGConstants.XPATH_ADJLOCINV);
            String strNode = elemAdjustLocationInventory.getAttribute(NWCGConstants.NODE);
			System.out.println("@@@@@ strNode : " + strNode);
			
			// get inXML - LocationId
            XPathWrapper inXML_Source_pathWrapper = new XPathWrapper(inXML);
            Element elemSource = (Element)inXML_Source_pathWrapper.getNode(NWCGConstants.XPATH_ADJLOCINV_SOURCE);
            String strLocationId = elemSource.getAttribute(NWCGConstants.LOCATION_ID);
			System.out.println("@@@@@ strLocationId : " + strLocationId);
			
			// get inXML - ItemID, ProductClass, and UnitOfMeasure	
            XPathWrapper inXML_InventoryItem_pathWrapper = new XPathWrapper(inXML);
            Element elemInventoryItem = (Element)inXML_InventoryItem_pathWrapper.getNode(NWCGConstants.XPATH_ADJLOCINV_SOURCE_INVENTORY_INVITEM);
			String strItemID = elemInventoryItem.getAttribute(NWCGConstants.ITEM_ID);
			String strProductClass = elemInventoryItem.getAttribute(NWCGConstants.PRODUCT_CLASS);
			String strUOM = elemInventoryItem.getAttribute(NWCGConstants.UNIT_OF_MEASURE);
			
			// get inXML - WorkOrderKey
            XPathWrapper inXML_Audit_pathWrapper = new XPathWrapper(inXML);
            Element elemAudit = (Element)inXML_Audit_pathWrapper.getNode(NWCGConstants.XPATH_ADJLOCINV_AUDIT);
            String strWorkOrderKey = elemAudit.getAttribute(NWCGConstants.WORK_ORDER_KEY);
			System.out.println("@@@@@ strWorkOrderKey : " + strWorkOrderKey);
			
			if(strWorkOrderKey != null && !strWorkOrderKey.equals("")) {
				
				// create input document for getWorkOrderDetails API and call that API
				Document inWorkOrderDetails = XMLUtil.newDocument();
				Element WorkOrderElement = inWorkOrderDetails.createElement(NWCGConstants.WORK_ORDER);
				inWorkOrderDetails.appendChild(WorkOrderElement);
				WorkOrderElement.setAttribute(NWCGConstants.WORK_ORDER_KEY, strWorkOrderKey);
				System.out.println("@@@@@ inWorkOrderDetails " + XMLUtil.getXMLString(inWorkOrderDetails));
				StringBuffer opTemplateBuff = new StringBuffer(NWCGConstants.TEMPLATE_GET_WO);	
				String opTemplate = opTemplateBuff.toString();
				Document getWorkOrderDetailsTemplate = XMLUtil.getDocument(opTemplate);
				Document outWorkOrderDetails = CommonUtilities.invokeAPI(env, getWorkOrderDetailsTemplate, NWCGConstants.API_GET_WORK_ORDER_DTLS, inWorkOrderDetails);
				System.out.println("@@@@@ outWorkOrderDetails " + XMLUtil.getXMLString(outWorkOrderDetails));
				
				// get outWorkOrderDetails - ServiceItemID
                XPathWrapper outWorkOrderDetailspathWrapper = new XPathWrapper(outWorkOrderDetails);
                Element elemWorkOrder = (Element)outWorkOrderDetailspathWrapper.getNode(NWCGConstants.XPATH_WO);
                String strServiceItemID = elemWorkOrder.getAttribute(NWCGConstants.SERVICE_ITEMID);
				System.out.println("@@@@@ strServiceItemID : " + strServiceItemID);
				
				if(strServiceItemID.equals(NWCGConstants.SERVICE_ITEM_ID_REFURB_KITTING)) {
					
					// create input document for getNodeInventory API and call that API
					System.out.println("@@@@@ strNode : " + strNode);
					System.out.println("@@@@@ strLocationId : " + strLocationId);
					System.out.println("@@@@@ strItemID : " + strItemID);
					System.out.println("@@@@@ strProductClass : " + strProductClass);
					System.out.println("@@@@@ strUOM : " + strUOM);
					Document inNodeInventory = XMLUtil.newDocument();
					Element NodeInventoryElement = inNodeInventory.createElement(NWCGConstants.NODE_INV_ELM);
					inNodeInventory.appendChild(NodeInventoryElement);
					NodeInventoryElement.setAttribute(NWCGConstants.LOCATION_ID, strLocationId);
					NodeInventoryElement.setAttribute(NWCGConstants.NODE, strNode);
					Element InventoryElement2 = inNodeInventory.createElement(NWCGConstants.INVENTORY_ATTR);
					NodeInventoryElement.appendChild(InventoryElement2);
					Element InventoryItemElement2 = inNodeInventory.createElement(NWCGConstants.INVENTORY_ITEM_ATTR);
					InventoryElement2.appendChild(InventoryItemElement2);
					InventoryItemElement2.setAttribute(NWCGConstants.ITEM_ID, strItemID);
					InventoryItemElement2.setAttribute(NWCGConstants.PRODUCT_CLASS, strProductClass);
					InventoryItemElement2.setAttribute(NWCGConstants.UNIT_OF_MEASURE, strUOM);
					System.out.println("@@@@@ inNodeInventory " + XMLUtil.getXMLString(inNodeInventory));
					StringBuffer op1TemplateBuff = new StringBuffer(NWCGConstants.TEMPLATE_GET_NODE_INV);	
					String op1Template = op1TemplateBuff.toString();
					Document getNodeInventoryTemplate = XMLUtil.getDocument(op1Template);
					Document outNodeInventory = CommonUtilities.invokeAPI(env, getNodeInventoryTemplate, NWCGConstants.GET_NODE_INVENTORY, inNodeInventory);
					System.out.println("@@@@@ outNodeInventory " + XMLUtil.getXMLString(outNodeInventory));
					
					// get outNodeInventory - InventoryStatus
	                XPathWrapper pathWrapper = new XPathWrapper(outNodeInventory);
	                Element ItemInventoryDetailListElement = (Element)pathWrapper.getNode(NWCGConstants.XPATH_NODEINV_LOCINVLIST_LOCINV_ITEMINVDTLLIST);
	                NodeList ItemInventoryDetailList = ItemInventoryDetailListElement.getElementsByTagName(NWCGConstants.ITEM_INVENTORY_DETAIL);
					for(int i = 0; i < ItemInventoryDetailList.getLength(); i++) {
						Node ItemInventoryDetailNode = ItemInventoryDetailList.item(i);
						Element ItemInventoryDetailElement = (Element)ItemInventoryDetailNode;
						String strInventoryStatus = ItemInventoryDetailElement.getAttribute(NWCGConstants.INVENTORY_STATUS);
						if(strInventoryStatus.equals(NWCGConstants.NRFI_RFB_STATUS)) {
							System.out.println("@@@@@ strInventoryStatus : " + strInventoryStatus);
							
							// set inXML - InventoryStatus
			                XPathWrapper inXMLpathWrapper = new XPathWrapper(inXML);
			                Element elemInventory = (Element)inXMLpathWrapper.getNode(NWCGConstants.XPATH_ADJLOCINV_SOURCE_INV);
			                elemInventory.setAttribute(NWCGConstants.INVENTORY_STATUS, NWCGConstants.RFI_STATUS);
						}
					}
				}
			}
		} catch(Exception ex) {
			System.out.println("!!!!! Caught General Exception, exiting now after doing nothing to handle it...");
		}
		
		System.out.println("@@@@@ inXML " + XMLUtil.getXMLString(inXML));
		System.out.println("@@@@@ Exiting NWCGValidateTrackableIDUE::AdjustInvalidInventory @@@@@");
		return inXML;
	}
}