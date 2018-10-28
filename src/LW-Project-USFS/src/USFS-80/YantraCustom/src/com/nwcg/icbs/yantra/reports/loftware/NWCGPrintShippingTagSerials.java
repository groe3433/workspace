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

package com.nwcg.icbs.yantra.reports.loftware;

import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGPrintShippingTagSerials implements YIFCustomApi {
	
	int scnt = 0;

	private Properties _properties;

	private static Logger log = Logger.getLogger(NWCGPrintShippingTagSerials.class.getName());

	/**
	 * 
	 * @param arg0
	 * @throws Exception
	 */
	public void setProperties(Properties arg0) throws Exception {
		_properties = arg0;
	}

	/**
	 * 
	 * @param env
	 * @param inputDoc
	 * @return
	 * @throws Exception
	 */
	public Document printShippingTagSerials(YFSEnvironment env, Document inputDoc) throws Exception {
		System.out.println("@@@@@ Entering NWCGPrintShippingTagSerials::printShippingTagSerials @@@@@");
		System.out.println("@@@@@ Input Doc : " + XMLUtil.getXMLString(inputDoc));

		String documentId = "NWCG_SHIPPING_TAG_SERIAL";
		Element rootElem1 = (Element) inputDoc.getElementsByTagName("Shipment").item(0);
		String shipmentKey = rootElem1.getAttribute("ShipmentKey");

		Element rootElem2 = (Element) inputDoc.getElementsByTagName("PrinterPreference").item(0);
		String printerId = rootElem2.getAttribute("PrinterId");
		String shipNode = rootElem2.getAttribute("OrganizationCode");

		Element rootElem3 = (Element) inputDoc.getElementsByTagName("LabelPreference").item(0);
		String enterpriseCode = rootElem3.getAttribute(NWCGConstants.ENTERPRISE_CODE_STR);

		Document shipmentDetails = getshipmentDetails(env, shipmentKey);
		Element shipmentDtlsElm = (Element) shipmentDetails.getElementsByTagName("ShipmentLine").item(0);

		String orderHdrKey = shipmentDtlsElm.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		Document ODtls = getOrderDetails(env, orderHdrKey);

		Document sDetails = getOtherInfoAndPrepareXml(env, shipmentDetails, ODtls);
		Document sDtls = setTotalQty(env, sDetails);
		
		Element tempElm = sDtls.getDocumentElement();

		NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
		String strDate = reportsUtil.dateToString(new java.util.Date(), "MM-dd-yyyy");
		tempElm.setAttribute("CurrentDate", strDate);

		Document shipTagSerialDoc = reportsUtil.generatePrintHeader(documentId, "xml:/Shipment", shipNode, printerId, enterpriseCode);
		Element shipstatusRptElmRootNode = shipTagSerialDoc.getDocumentElement();
		Element printDocElm = (Element) shipstatusRptElmRootNode.getFirstChild();
		Element inputDataElm = shipTagSerialDoc.createElement("InputData");
		Element shipstatusDtlsElm = shipTagSerialDoc.createElement("Shipment");
		XMLUtil.copyElement(shipTagSerialDoc, tempElm, shipstatusDtlsElm);
		inputDataElm.appendChild(shipstatusDtlsElm);
		printDocElm.appendChild(inputDataElm);

		if (scnt > 0)
			CommonUtilities.invokeAPI(env, NWCGConstants.API_PRINT_DOCUMENT_SET, shipTagSerialDoc);
		
		System.out.println("@@@@@ Exiting NWCGPrintShippingTagSerials::printShippingTagSerials @@@@@");
		return shipTagSerialDoc;
	}

	private Document getOtherInfoAndPrepareXml(YFSEnvironment env, Document shipmentDetails, Document OrderDetails) throws Exception {
		System.out.println("@@@@@ Entering NWCGPrintShippingTagSerials::getOtherInfoAndPrepareXml @@@@@");
		System.out.println("@@@@@ OrderDetails " + XMLUtil.getXMLString(OrderDetails));

		Element curOrderLineList, curSerialList = null;
		Element rootElm1 = (Element) shipmentDetails.getElementsByTagName("Shipment").item(0);
		String shipmentKey1 = rootElm1.getAttribute("ShipmentKey");
		Document ShipmentTagSerial = XMLUtil.getDocument();
		Element ShipmentTagHdrElm = ShipmentTagSerial.createElement("Shipment");
		ShipmentTagHdrElm.setAttribute("ShipmentKey", shipmentKey1);
		ShipmentTagSerial.appendChild(ShipmentTagHdrElm);

		Element ShipmentSerialHdrElm = ShipmentTagSerial.createElement("ShipmentSerials");
		ShipmentTagHdrElm.appendChild(ShipmentSerialHdrElm);

		int serialcnt = 0;
		NodeList listOfOrderLines = OrderDetails.getElementsByTagName("OrderLine");

		for (int j = 0; j < listOfOrderLines.getLength(); j++) {
			curOrderLineList = (Element) listOfOrderLines.item(j);
			Element itemElem = (Element) curOrderLineList.getElementsByTagName("Item").item(0);
			Element ExtnElem = (Element) curOrderLineList.getElementsByTagName("Extn").item(0);
			String strOrderedQty = curOrderLineList.getAttribute("OrderedQty");
			if (!strOrderedQty.equals("0.00")) {
				String ItemId = itemElem.getAttribute("ItemID");
				String OrderHeaderKey = curOrderLineList.getAttribute("OrderHeaderKey");
				String OrderLineKey = curOrderLineList.getAttribute("OrderLineKey");
				String ItemDesc = itemElem.getAttribute("ItemDesc");

				Element curShipLineList = getShipmentLine(shipmentDetails, ItemId, OrderLineKey);
				String OrderNo = curShipLineList.getAttribute("OrderNo");
				ShipmentTagHdrElm.setAttribute("OrderNo", OrderNo);
				String ReqNo = ExtnElem.getAttribute("ExtnRequestNo");

				NodeList listOfSerials = curShipLineList.getElementsByTagName("ShipmentTagSerial");
				System.out.println("@@@@@ listOfSerials " + listOfSerials.getLength());

				if (listOfSerials.getLength() > 0) {
					for (int k = 0; k < listOfSerials.getLength(); k++) {
						System.out.println("@@@@@ count k " + k);
						curSerialList = (Element) listOfSerials.item(k);
						String Serial = curSerialList.getAttribute("SerialNo");
						if (Serial.length() > 0) {
							Document serialNumbersList = XMLUtil.getDocument("<SerialList><Serial SerialNo=\"" + Serial + "\" /></SerialList>");
							Document childSerialsDoc = getChildSerialNumbers(env, Serial, OrderNo, OrderLineKey, OrderHeaderKey, serialNumbersList);
							NodeList listOfChildSerials = childSerialsDoc.getElementsByTagName("Serial");
							if (listOfChildSerials.getLength() > 0) {
								for (int i = 0; i < listOfChildSerials.getLength(); i++) {
									curSerialList = (Element) listOfChildSerials.item(i);
									String serialNo = curSerialList.getAttribute("SerialNo");
									if (serialNo != null && !serialNo.trim().equals("")) {
										Document trackableItemDoc = getTrackableItem(env, serialNo);
										Element trackableItem = (Element) trackableItemDoc.getDocumentElement().getElementsByTagName("NWCGTrackableItem").item(0);
										String cItemId = trackableItem.getAttribute("ItemID");
										String cItemDesc = trackableItem.getAttribute("ItemShortDescription");
										String isParent = (serialNo.equals(Serial)) ? "Y" : "N";
										Element shipmentChildSerialElement = ShipmentTagSerial.createElement("ShipmentSerial");
										shipmentChildSerialElement.setAttribute("SerialItem", cItemId);
										shipmentChildSerialElement.setAttribute("SerialItemDesc", cItemDesc);
										shipmentChildSerialElement.setAttribute("SerialItemReqNo", ReqNo);
										shipmentChildSerialElement.setAttribute("SerialNo", serialNo);
										shipmentChildSerialElement.setAttribute("Qty", "1");
										shipmentChildSerialElement.setAttribute("isParent", isParent);
										ShipmentSerialHdrElm.appendChild(shipmentChildSerialElement);
									}
								}
								serialcnt++;
								scnt++;
							}
						}
					}
					serialcnt++;
					scnt++;
				}
			}

		}

		System.out.println("@@@@@ Exiting NWCGPrintShippingTagSerials::getOtherInfoAndPrepareXml @@@@@");
		return ShipmentTagSerial;
	}

	private Document setTotalQty(YFSEnvironment env, Document sDetails) {
		System.out.println("@@@@@ Entering NWCGPrintShippingTagSerials::setTotalQty @@@@@");
		
		String ReqNo, TempReqNo = "";
		int serialcnt = 0;
		NodeList listOfSerialLines = sDetails.getElementsByTagName("ShipmentSerial");
		Element srootElm1 = (Element) sDetails.getElementsByTagName("ShipmentSerials").item(0);
		for (int j = 0; j < listOfSerialLines.getLength(); j++) {
			Element curSerialLineList = (Element) listOfSerialLines.item(j);
			ReqNo = curSerialLineList.getAttribute("SerialItemReqNo");
			String isParent = curSerialLineList.getAttribute("isParent");
			String serial = curSerialLineList.getAttribute("SerialNo");
			if (j == 0) {
				TempReqNo = ReqNo;
			}
			if (!ReqNo.equals(TempReqNo)) {
				Element ShipmentSerialElm = sDetails.createElement("ShipmentSerial");
				ShipmentSerialElm.setAttribute("SerialItem", "");
				ShipmentSerialElm.setAttribute("SerialItemDesc", "Total Issued Qty");
				ShipmentSerialElm.setAttribute("SerialItemReqNo", "");
				ShipmentSerialElm.setAttribute("SerialNo", "");
				ShipmentSerialElm.setAttribute("Qty", Integer.toString(serialcnt));
				srootElm1.insertBefore(ShipmentSerialElm, curSerialLineList);
				serialcnt = -1;
			}
			TempReqNo = ReqNo;
			if (isParent.equals("Y")) {
				serialcnt++;
			}
		}
		Element ShipmentSerialElm = sDetails.createElement("ShipmentSerial");
		ShipmentSerialElm.setAttribute("SerialItem", "");
		ShipmentSerialElm.setAttribute("SerialItemDesc", "Total Issued Qty");
		ShipmentSerialElm.setAttribute("SerialItemReqNo", "");
		ShipmentSerialElm.setAttribute("SerialNo", "");
		ShipmentSerialElm.setAttribute("Qty", Integer.toString(serialcnt));
		srootElm1.appendChild(ShipmentSerialElm);
		
		System.out.println("@@@@@ Exiting NWCGPrintShippingTagSerials::setTotalQty @@@@@");
		return sDetails;
	}

	private Document getshipmentDetails(YFSEnvironment env, String shipmentKey) {
		System.out.println("@@@@@ Entering NWCGPrintShippingTagSerials::getshipmentDetails @@@@@");
		Document shipmentDtls = null;
		try {
			Document ShipmentHdrInputDoc = XMLUtil.getDocument();
			Element ShipmentHdrElm = ShipmentHdrInputDoc.createElement("Shipment");
			ShipmentHdrElm.setAttribute("ShipmentKey", shipmentKey);
			ShipmentHdrInputDoc.appendChild(ShipmentHdrElm);
			shipmentDtls = CommonUtilities.invokeAPI(env, "getShipmentDetails", ShipmentHdrInputDoc);
		} catch (Exception e) {
			System.out.println("!!!!! NWCGPrintShippingTagSerials::getShipmentDetails, Exception Msg : " + e.getMessage());
			System.out.println("!!!!! NWCGPrintShippingTagSerials::getShipmentDetails, StackTrace : " + e.getStackTrace());
		}
		
		System.out.println("@@@@@ Exiting NWCGPrintShippingTagSerials::getshipmentDetails @@@@@");
		return shipmentDtls;
	}

	private Document getOrderDetails(YFSEnvironment env, String orderHdrKey) {
		System.out.println("@@@@@ Entering NWCGPrintShippingTagSerials::getOrderDetails @@@@@");
		Document result = null;
		try {
			result = XMLUtil.getDocument();
			Document inputDoc = XMLUtil.getDocument();
			Element orderElm = inputDoc.createElement("Order");
			inputDoc.appendChild(orderElm);
			orderElm.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHdrKey);
			result = CommonUtilities.invokeAPI(env, "NWCGReports_getOrderDetails", NWCGConstants.API_GET_ORDER_DETAILS, inputDoc);
		} catch (ParserConfigurationException pce) {
			System.out.println("!!!!! NWCGReportsIssue::getOrderDetails, ParserConfigurationException Msg : " + pce.getMessage());
			System.out.println("!!!!! NWCGReportsIssue::getOrderDetails, StackTrace : " + pce.getStackTrace());
		} catch (Exception e) {
			System.out.println("!!!!! NWCGReportsIssue::getOrderDetails, Exception Msg : " + e.getMessage());
			System.out.println("!!!!! NWCGReportsIssue::getOrderDetails, StackTrace : " + e.getStackTrace());
		}
		System.out.println("@@@@@ Exiting NWCGPrintShippingTagSerials::getOrderDetails @@@@@");
		return result;
	}

	private Element getShipmentLine(Document ShipmentDetails, String ItemId, String OrderLineKey) {
		System.out.println("@@@@@ Entering NWCGPrintShippingTagSerials::getShipmentLine @@@@@");
		Element curShipmentLineList = null;
		NodeList listOfShipmentLines = ShipmentDetails.getElementsByTagName("ShipmentLine");
		for (int j = 0; j < listOfShipmentLines.getLength(); j++) {
			curShipmentLineList = (Element) listOfShipmentLines.item(j);
			String ShipItemId = curShipmentLineList.getAttribute("ItemID");
			String ShipOrderLineKey = curShipmentLineList.getAttribute("OrderLineKey");
			if (ItemId.equals(ShipItemId) && OrderLineKey.equals(ShipOrderLineKey)) {
				return curShipmentLineList;
			}
		}
		System.out.println("@@@@@ Exiting NWCGPrintShippingTagSerials::getShipmentLine @@@@@");
		return curShipmentLineList;
	}

	/**
	 * 
	 * @param env
	 * @param serialNumber
	 * @param serialNumberListMod
	 * @return
	 * @throws Exception
	 */
	private Document getChildSerialNumbers(YFSEnvironment env, String serialNumber, String OrderNo, String OrderLineKey, String OrderHeaderKey, Document serialNumberListMod) throws Exception {
		System.out.println("@@@@@ Entering NWCGPrintShippingTagSerials::getChildSerialNumbers @@@@@");
		
		// BEGIN - CR 881 - Feb 26, 2014
		
		/**
		 * This was reading the kit's components from the "current" kit configuration. 
		 * However we needed to read from the configuration of the kit at the time it was originally shipped. 
		 * 
		 * String template1 = "<SerialList><Serial SerialNo=\"\" ParentSerialKey=\"\" GlobalSerialKey=\"\"/></SerialList>";
		 * String template2 = "<SerialList><Serial SerialNo=\"\"/></SerialList>";
		 * Document templateDoc1 = XMLUtil.getDocument(template1);
		 * Document templateDoc2 = XMLUtil.getDocument(template2);
		 * System.out.println("@@@@@ serialNumber : " + serialNumber);
		 * Document intermediateDoc = CommonUtilities.invokeAPI(env, templateDoc1, NWCGConstants.API_GET_SERIAL_LIST, XMLUtil.getDocument("<Serial SerialNo=\"" + serialNumber + "\" />"));
		 * System.out.println("@@@@@ intermediateDoc : " + XMLUtil.getXMLString(intermediateDoc));
		 * String globalSerialKey = ((Element) intermediateDoc.getElementsByTagName("Serial").item(0)).getAttribute("GlobalSerialKey");
		 * System.out.println("@@@@@ globalSerialKey : " + globalSerialKey);
		 * Document childSerialsDoc = CommonUtilities.invokeAPI(env, templateDoc2, NWCGConstants.API_GET_SERIAL_LIST, XMLUtil.getDocument("<Serial ParentSerialKey=\"" + globalSerialKey + "\" />"));
		 */
		
		String strParentSerialNo1 = serialNumber;
		System.out.println("@@@@@ strParentSerialNo1: " + strParentSerialNo1);
		String strIssueNo = OrderNo;
		System.out.println("@@@@@ strIssueNo: " + strIssueNo);
		String strOrderHeaderKey = OrderHeaderKey;
		System.out.println("@@@@@ strOrderHeaderKey: " + strOrderHeaderKey);
		String strOrderLineKey = OrderLineKey;
		System.out.println("@@@@@ strOrderLineKey: " + strOrderLineKey);
		
		/**
		 *  New Input for extended database API: (NWCGGetIssueTrackableListService)
		 *  <NWCGIssueTrackableList IssueNo="0000664829" ParentSerialNo1="4381KD-FCK-183" OrderHeaderKey="2012071007504585684249" OrderLineKey="2012071008001785686839"/>
		 */
		Document inDocNWCGIssueTrackableList = XMLUtil.getDocument();
		Element ipDocRootElement = inDocNWCGIssueTrackableList.createElement("NWCGIssueTrackableList");
		ipDocRootElement.setAttribute("IssueNo", strIssueNo);
		ipDocRootElement.setAttribute("ParentSerialNo1", strParentSerialNo1);
		ipDocRootElement.setAttribute("OrderHeaderKey", strOrderHeaderKey);
		ipDocRootElement.setAttribute("OrderLineKey", strOrderLineKey);
		inDocNWCGIssueTrackableList.appendChild(ipDocRootElement);
		
		System.out.println("@@@@@ inDocNWCGIssueTrackableList : " + XMLUtil.getXMLString(inDocNWCGIssueTrackableList));
		Document childSerialsDoc = CommonUtilities.invokeService(env, "NWCGGetIssueTrackableListService", inDocNWCGIssueTrackableList);
		System.out.println("@@@@@ childSerialsDoc : " + XMLUtil.getXMLString(childSerialsDoc));
		
		//<SerialList><Serial SerialNo=\"\"/></SerialList>
		
		// END - CR 881 - Feb 26, 2014
		
		NodeList serialChildNodeList = childSerialsDoc.getDocumentElement().getElementsByTagName("NWCGIssueTrackableList");
		if (serialChildNodeList.getLength() > 0) {
			for (int j = 0; j < serialChildNodeList.getLength(); j++) {
				System.out.println("@@@@@ j: " + j);
				Element serialChildElement = (Element) serialChildNodeList.item(j);
				Element newSerialChildElement = serialNumberListMod.createElement("Serial");
				//XMLUtil.copyElement(serialNumberListMod, serialChildElement, newSerialChildElement);
				
				String newSerialNumber = serialChildElement.getAttribute("SerialNo");
				System.out.println("@@@@@ newSerialNumber: " + newSerialNumber);
				newSerialChildElement.setAttribute("SerialNo", newSerialNumber);
				
				serialNumberListMod.getDocumentElement().appendChild(newSerialChildElement);
				System.out.println("@@@@@ serialNumberListMod (partial) : " + XMLUtil.getXMLString(serialNumberListMod));
				
				String newIssueNo = serialChildElement.getAttribute("IssueNo");
				System.out.println("@@@@@ newIssueNo: " + newIssueNo);
				String newOrderLineKey = serialChildElement.getAttribute("OrderLineKey");
				System.out.println("@@@@@ newOrderLineKey: " + newOrderLineKey);
				String newOrderHeaderKey = serialChildElement.getAttribute("OrderHeaderKey");
				System.out.println("@@@@@ newOrderHeaderKey: " + newOrderHeaderKey);
				getChildSerialNumbers(env, newSerialNumber, newIssueNo, newOrderLineKey, newOrderHeaderKey, serialNumberListMod);
			}
		}
		
		System.out.println("@@@@@ serialNumberListMod : " + XMLUtil.getXMLString(serialNumberListMod));
		System.out.println("@@@@@ Exiting NWCGPrintShippingTagSerials::getChildSerialNumbers @@@@@");
		return serialNumberListMod;
	}

	private Document getTrackableItem(YFSEnvironment env, String serialNo) throws Exception {
		System.out.println("@@@@@ In NWCGPrintShippingTagSerials::getTrackableItem @@@@@");
		return CommonUtilities.invokeService(env, NWCGConstants.NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE, XMLUtil.getDocument("<NWCGTrackableItem SerialNo=\"" + serialNo + "\" />"));
	}
}