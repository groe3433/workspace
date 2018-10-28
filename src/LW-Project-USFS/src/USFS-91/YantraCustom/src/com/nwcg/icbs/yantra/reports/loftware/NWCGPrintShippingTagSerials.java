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

import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGPrintShippingTagSerials implements YIFCustomApi {
	
	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGPrintShippingTagSerials.class);
	
	int scnt = 0;

	private Properties _properties;

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
		logger.verbose("@@@@@ Entering NWCGPrintShippingTagSerials::printShippingTagSerials @@@@@");
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
		logger.verbose("@@@@@ Exiting NWCGPrintShippingTagSerials::printShippingTagSerials @@@@@");
		return shipTagSerialDoc;
	}

	/**
	 * 
	 * @param env
	 * @param shipmentDetails
	 * @param OrderDetails
	 * @return
	 * @throws Exception
	 */
	private Document getOtherInfoAndPrepareXml(YFSEnvironment env, Document shipmentDetails, Document OrderDetails) throws Exception {
		logger.verbose("@@@@@ Entering NWCGPrintShippingTagSerials::getOtherInfoAndPrepareXml @@@@@");
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
			logger.verbose("@@@@@ strOrderedQty :: " + strOrderedQty);
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
				if (listOfSerials.getLength() > 0) {
					logger.verbose("@@@@@ listOfSerials :: " + listOfSerials.getLength());
					for (int k = 0; k < listOfSerials.getLength(); k++) {
						curSerialList = (Element) listOfSerials.item(k);
						String Serial = curSerialList.getAttribute("SerialNo");
						if (Serial.length() > 0) {
							logger.verbose("@@@@@ Serial :: " + Serial);
							Document serialNumbersList = XMLUtil.getDocument("<SerialList><Serial SerialNo=\"" + Serial + "\" /></SerialList>");
							Document childSerialsDoc = getChildSerialNumbers(env, Serial, OrderNo, OrderLineKey, OrderHeaderKey, serialNumbersList);
							NodeList listOfChildSerials = childSerialsDoc.getElementsByTagName("Serial");
							if (listOfChildSerials.getLength() > 0) {
								logger.verbose("@@@@@ listOfChildSerials :: " + listOfChildSerials.getLength());
								for (int i = 0; i < listOfChildSerials.getLength(); i++) {
									curSerialList = (Element) listOfChildSerials.item(i);
									String serialNo = curSerialList.getAttribute("SerialNo");
									logger.verbose("@@@@@ serialNo :: " + serialNo);
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
		logger.verbose("@@@@@ Exiting NWCGPrintShippingTagSerials::getOtherInfoAndPrepareXml @@@@@");
		return ShipmentTagSerial;
	}

	/**
	 * 
	 * @param env
	 * @param sDetails
	 * @return
	 */
	private Document setTotalQty(YFSEnvironment env, Document sDetails) {
		logger.verbose("@@@@@ Entering NWCGPrintShippingTagSerials::setTotalQty @@@@@");
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
		logger.verbose("@@@@@ Exiting NWCGPrintShippingTagSerials::setTotalQty @@@@@");
		return sDetails;
	}

	/**
	 * 
	 */
	private Document getshipmentDetails(YFSEnvironment env, String shipmentKey) {
		logger.verbose("@@@@@ Entering NWCGPrintShippingTagSerials::getshipmentDetails @@@@@");
		Document shipmentDtls = null;
		try {
			Document ShipmentHdrInputDoc = XMLUtil.getDocument();
			Element ShipmentHdrElm = ShipmentHdrInputDoc.createElement("Shipment");
			ShipmentHdrElm.setAttribute("ShipmentKey", shipmentKey);
			ShipmentHdrInputDoc.appendChild(ShipmentHdrElm);
			shipmentDtls = CommonUtilities.invokeAPI(env, "getShipmentDetails", ShipmentHdrInputDoc);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			e.printStackTrace();
		}
		logger.verbose("@@@@@ Exiting NWCGPrintShippingTagSerials::getshipmentDetails @@@@@");
		return shipmentDtls;
	}

	/**
	 * 
	 * @param env
	 * @param orderHdrKey
	 * @return
	 */
	private Document getOrderDetails(YFSEnvironment env, String orderHdrKey) {
		logger.verbose("@@@@@ Entering NWCGPrintShippingTagSerials::getOrderDetails @@@@@");
		Document result = null;
		try {
			result = XMLUtil.getDocument();
			Document inputDoc = XMLUtil.getDocument();
			Element orderElm = inputDoc.createElement("Order");
			inputDoc.appendChild(orderElm);
			orderElm.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHdrKey);
			result = CommonUtilities.invokeAPI(env, "NWCGReports_getOrderDetails", NWCGConstants.API_GET_ORDER_DETAILS, inputDoc);
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException :: " + pce);
			pce.printStackTrace();
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			e.printStackTrace();
		}
		logger.verbose("@@@@@ Exiting NWCGPrintShippingTagSerials::getOrderDetails @@@@@");
		return result;
	}

	/**
	 * 
	 * @param ShipmentDetails
	 * @param ItemId
	 * @param OrderLineKey
	 * @return
	 */
	private Element getShipmentLine(Document ShipmentDetails, String ItemId, String OrderLineKey) {
		logger.verbose("@@@@@ Entering NWCGPrintShippingTagSerials::getShipmentLine @@@@@");
		Element curShipmentLineList = null;
		NodeList listOfShipmentLines = ShipmentDetails.getElementsByTagName("ShipmentLine");
		for (int j = 0; j < listOfShipmentLines.getLength(); j++) {
			curShipmentLineList = (Element) listOfShipmentLines.item(j);
			String ShipItemId = curShipmentLineList.getAttribute("ItemID");
			String ShipOrderLineKey = curShipmentLineList.getAttribute("OrderLineKey");
			if (ItemId.equals(ShipItemId) && OrderLineKey.equals(ShipOrderLineKey)) {
				logger.verbose("@@@@@ Exiting NWCGPrintShippingTagSerials::getShipmentLine (1) @@@@@");
				return curShipmentLineList;
			}
		}
		logger.verbose("@@@@@ Exiting NWCGPrintShippingTagSerials::getShipmentLine (2) @@@@@");
		return curShipmentLineList;
	}

	/**
	 * This method gets the child trackable components of a kit for the NWCGPrintShippingTagSerials loftware report. 
	 * The client is expecting the report to be in the format:
	 *  - Parent: 0340-GBK-0159-199
	 *  - Child: GBK-0159-199
	 *  This method is recursive. It will try to get multiple levels of kits within kits. 
	 *  
	 *  This code gets called for lots of different issues. 
	 *   - Cache 2 Cache Transfer Issues - require the "current kit configuration" (C2C transfer issues do NOT have "issued kit configuration")
	 *   - Incident Issues - require the "issued kit configuration" (incident issues have "issued kit configuration" and it is set when "Confirm Shipment" is called)
	 *   
	 *  CR 881 - was put in to get the "issued kit configuration" for incident issues, it will try to get the trackable info from NWCGGetIssueTrackableListService service. 
	 * 	 - This was reading the kit components from the "current" kit configuration. 
	 * 	 - However we need to read from the configuration of the kit at the time it was originally shipped. 
	 *   - New Input for extended database API: (NWCGGetIssueTrackableListService)
	 *  	 - <NWCGIssueTrackableList IssueNo="0000664829" ParentSerialNo1="4381KD-FCK-183" OrderHeaderKey="2012071007504585684249" OrderLineKey="2012071008001785686839"/>
	 *   
	 *  CR 1595 - will be put in to get the "current kit configuration" for cache 2 cache transfer issues, it will get the trackable info from getSerialList API. 
	 *   - Will get DocumentType and check it first. if NOT 0001, then we will do it the old way with getSerialList
	 * 
	 * @param env
	 * @param serialNumber
	 * @param serialNumberListMod
	 * @return
	 * @throws Exception
	 */
	private Document getChildSerialNumbers(YFSEnvironment env, String serialNumber, String OrderNo, String OrderLineKey, String OrderHeaderKey, Document serialNumberListMod) throws Exception {
		logger.verbose("@@@@@ Entering NWCGPrintShippingTagSerials::getChildSerialNumbers @@@@@");
		
		// BEGIN - CR 881 - Feb 26, 2014
		String strParentSerialNo1 = serialNumber;
		String strIssueNo = OrderNo;
		String strOrderHeaderKey = OrderHeaderKey;
		String strOrderLineKey = OrderLineKey;
		// END - CR 881 - Feb 26, 2014
		
		// BEGIN - CR 1595 - Sept 02, 2015
		Document outDocGetOrderDetails = CommonUtilities.invokeAPI(env, XMLUtil.getDocument("<Order DocumentType=\"\" />"), "getOrderDetails", XMLUtil.getDocument("<Order OrderHeaderKey=\"" + OrderHeaderKey + "\" />"));
		logger.verbose("@@@@@ outDocGetOrderDetails :: " + XMLUtil.getXMLString(outDocGetOrderDetails));
		String strDocumentType = StringUtil.nonNull(XPathUtil.getString(outDocGetOrderDetails, "Order/@DocumentType"));
		logger.verbose("@@@@@ strDocumentType :: " + strDocumentType);
		// END - CR 1595 - Sept 02, 2015
		
		// BEGIN - CR 1595 - Sept 02, 2015
		NodeList serialChildNodeList = null;
		if(strDocumentType.equals("0006")) {
			String template1 = "<SerialList><Serial SerialNo=\"\" ParentSerialKey=\"\" GlobalSerialKey=\"\"/></SerialList>";
			String template2 = "<SerialList><Serial SerialNo=\"\"/></SerialList>";
			Document templateDoc1 = XMLUtil.getDocument(template1);
			Document templateDoc2 = XMLUtil.getDocument(template2);
			Document intermediateDoc = CommonUtilities.invokeAPI(env, templateDoc1, NWCGConstants.API_GET_SERIAL_LIST, XMLUtil.getDocument("<Serial SerialNo=\"" + serialNumber + "\" />"));
			String globalSerialKey = ((Element) intermediateDoc.getElementsByTagName("Serial").item(0)).getAttribute("GlobalSerialKey");
			Document childSerialsDoc2 = CommonUtilities.invokeAPI(env, templateDoc2, NWCGConstants.API_GET_SERIAL_LIST, XMLUtil.getDocument("<Serial ParentSerialKey=\"" + globalSerialKey + "\" />"));
			logger.verbose("@@@@@ childSerialsDoc2 :: " + XMLUtil.getXMLString(childSerialsDoc2));
			serialChildNodeList = childSerialsDoc2.getDocumentElement().getElementsByTagName("Serial");
			if (serialChildNodeList.getLength() > 0) {
				for (int j = 0; j < serialChildNodeList.getLength(); j++) {
					Element serialChildElement = (Element) serialChildNodeList.item(j);
					Element newSerialChildElement = serialNumberListMod.createElement("Serial");
					String newSerialNumber = serialChildElement.getAttribute("SerialNo");
					logger.verbose("@@@@@ newSerialNumber :: " + newSerialNumber);
					newSerialChildElement.setAttribute("SerialNo", newSerialNumber);
					serialNumberListMod.getDocumentElement().appendChild(newSerialChildElement);
					System.out.println("@@@@@ serialNumberListMod :: " + XMLUtil.getXMLString(serialNumberListMod));
					getChildSerialNumbers(env, newSerialNumber, strIssueNo, strOrderLineKey, strOrderHeaderKey, serialNumberListMod);
				}
			}
		} else {
			// BEGIN - CR 881 - Feb 26, 2014
			Document inDocNWCGIssueTrackableList = XMLUtil.getDocument();
			Element ipDocRootElement = inDocNWCGIssueTrackableList.createElement("NWCGIssueTrackableList");
			ipDocRootElement.setAttribute("IssueNo", strIssueNo);
			ipDocRootElement.setAttribute("ParentSerialNo1", strParentSerialNo1);
			ipDocRootElement.setAttribute("OrderHeaderKey", strOrderHeaderKey);
			ipDocRootElement.setAttribute("OrderLineKey", strOrderLineKey);
			inDocNWCGIssueTrackableList.appendChild(ipDocRootElement);
			logger.verbose("@@@@@ inDocNWCGIssueTrackableList :: " + XMLUtil.getXMLString(inDocNWCGIssueTrackableList));
			Document childSerialsDoc1 = CommonUtilities.invokeService(env, "NWCGGetIssueTrackableListService", inDocNWCGIssueTrackableList);
			logger.verbose("@@@@@ childSerialsDoc1 :: " + XMLUtil.getXMLString(childSerialsDoc1));
			serialChildNodeList = childSerialsDoc1.getDocumentElement().getElementsByTagName("NWCGIssueTrackableList");
			if (serialChildNodeList.getLength() > 0) {
				for (int j = 0; j < serialChildNodeList.getLength(); j++) {
					Element serialChildElement = (Element) serialChildNodeList.item(j);
					Element newSerialChildElement = serialNumberListMod.createElement("Serial");
					String newSerialNumber = serialChildElement.getAttribute("SerialNo");
					logger.verbose("@@@@@ newSerialNumber :: " + newSerialNumber);
					newSerialChildElement.setAttribute("SerialNo", newSerialNumber);
					serialNumberListMod.getDocumentElement().appendChild(newSerialChildElement);
					System.out.println("@@@@@ serialNumberListMod :: " + XMLUtil.getXMLString(serialNumberListMod));
					String newIssueNo = serialChildElement.getAttribute("IssueNo");
					String newOrderLineKey = serialChildElement.getAttribute("OrderLineKey");
					String newOrderHeaderKey = serialChildElement.getAttribute("OrderHeaderKey");
					getChildSerialNumbers(env, newSerialNumber, newIssueNo, newOrderLineKey, newOrderHeaderKey, serialNumberListMod);
				}
			}
			// END - CR 881 - Feb 26, 2014
		}
		// END - CR 1595 - Sept 02, 2015
		
		logger.verbose("@@@@@ Exiting NWCGPrintShippingTagSerials::getChildSerialNumbers @@@@@");
		return serialNumberListMod;
	}

	/**
	 * 
	 * @param env
	 * @param serialNo
	 * @return
	 * @throws Exception
	 */
	private Document getTrackableItem(YFSEnvironment env, String serialNo) throws Exception {
		return CommonUtilities.invokeService(env, NWCGConstants.NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE, XMLUtil.getDocument("<NWCGTrackableItem SerialNo=\"" + serialNo + "\" />"));
	}
}