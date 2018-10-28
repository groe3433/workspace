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

import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import java.text.DecimalFormat;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

/**
 * 
 * @author Oxford Consulting Group
 * @version 1.0
 * @date October 23, 2013
 */
public class NWCGReportsIssueTransferIATAI implements YIFCustomApi {

	private Properties _properties;

	// GENERAL_APP_LOGGER - configured in log4jconfig.custom.xml
	//private static YFCLogCategory cat = YFCLogCategory.instance(NWCGReportsIssueTransferIATAI.class);

	/**
	 * 
	 * @param arg0
	 * @throws Exception
	 */
	public void setProperties(Properties arg0) throws Exception {
		System.out.println("@@@@@ In NWCGReportsIssueTransferIATAI::setProperties @@@@@");
		_properties = arg0;
	}

	/**
	 * 
	 * @param env
	 * @param inputDoc
	 * @return
	 * @throws Exception
	 */
	public Document triggerIssueTransferIATAI(YFSEnvironment env, Document inputDoc) throws Exception {
		System.out.println("@@@@@ Entering NWCGReportsIssueTransferIATAI::triggerIssueTransferIATAI @@@@@");
		System.out.println("@@@@@ Input Doc : " + XMLUtil.getXMLString(inputDoc));
		
		String documentId = "NWCG_ISSUE_TRANSFER_REPORT";
		Element rootElem1 = (Element) inputDoc.getElementsByTagName("ITOrder").item(0);
		String orderHdrKey = rootElem1.getAttribute("TransferOrderKey");
		Element rootElem2 = (Element) inputDoc.getElementsByTagName("PrinterPreference").item(0);
		String printerId = rootElem2.getAttribute("PrinterId");
		String orgCode = rootElem2.getAttribute("OrganizationCode");
		Element rootElem3 = (Element) inputDoc.getElementsByTagName("LabelPreference").item(0);
		String noOfCopies = rootElem3.getAttribute("NoOfCopies");
		int noCopies = 1;
		if (Integer.parseInt(noOfCopies) > 1)
			noCopies = Integer.parseInt(noOfCopies);
		// -- Only if a shipment exists for an orderHeaderKey -- //
		// -- get shipmentkey from orderHeaderKey -- //
		String shipmentKey = "";
		String TotalVolume = "";
		String TotalVolumeUOM = "";
		String TotalWeight = "";
		String TotalWeightUOM = "";
		Document shipmentLine = getShipmentLineFromOrder(env, orderHdrKey);
		Element shipmentRootElm = shipmentLine.getDocumentElement();
		NodeList shipmentLinesNL = shipmentRootElm.getElementsByTagName("ShipmentLine");
		if (shipmentLinesNL != null && shipmentLinesNL.getLength() > 0) {
			Element shipmentLineElm = (Element) shipmentLinesNL.item(0);
			shipmentKey = shipmentLineElm.getAttribute("ShipmentKey");
			// -- pass the obtained shipmentKey to below section
			if (shipmentKey != "") {
				Document shipmentDtls = getOrderHeaderKey(env, shipmentKey);
				Element shipmentrootElm = shipmentDtls.getDocumentElement();
				TotalVolume = shipmentrootElm.getAttribute("TotalVolume");
				TotalVolumeUOM = shipmentrootElm.getAttribute("TotalVolumeUOM");
				TotalWeight = shipmentrootElm.getAttribute("TotalWeight");
				TotalWeightUOM = shipmentrootElm.getAttribute("TotalWeightUOM");
			}
		}
		
		// -- get shipmentkey from orderHeaderKey -- //
		String releaseKey = "";
		Document orderDtls = getOrderDetails(env, orderHdrKey);
		Element orderDtlsElm = orderDtls.getDocumentElement();
		String orderId = orderDtlsElm.getAttribute(NWCGConstants.ORDER_NO);
		String enterpriseCode = orderDtlsElm.getAttribute(NWCGConstants.ENTERPRISE_CODE_STR);
		String documentType = orderDtlsElm.getAttribute(NWCGConstants.DOCUMENT_TYPE);
		Document orderLineListDtls = getOrderLineList(env, orderId, enterpriseCode, documentType, orderHdrKey);
		NodeList orderStatusList = orderDtlsElm.getElementsByTagName("OrderStatus");

		// CR 388 kjs
		Element ExtnOrderElm = (Element) orderDtlsElm.getElementsByTagName("Extn").item(0);
		String ExtnIncidentNo = ExtnOrderElm.getAttribute("ExtnIncidentNo");
		String ExtnRecvAcctCode = ExtnOrderElm.getAttribute("ExtnRecvAcctCode");
		String ExtnRAOverrideCode = ExtnOrderElm.getAttribute("ExtnRAOverrideCode");
		// get receiving Cache code
		Element OrderStatusElm = (Element) orderDtls.getElementsByTagName("OrderStatus").item(0);
		String StrReceivingNode = OrderStatusElm.getAttribute("ReceivingNode");
		Document inDoc = XMLUtil.newDocument();
		Document outOrgDoc = XMLUtil.newDocument();
		Element el_NWCGOrganization = inDoc.createElement("Organization");
		inDoc.appendChild(el_NWCGOrganization);
		el_NWCGOrganization.setAttribute("OrganizationCode", StrReceivingNode);
		outOrgDoc = CommonUtilities.invokeAPI(env, "NWCGProcessBillingTrans_getOrganizationList", "getOrganizationList", inDoc);
		Element outDocElem = outOrgDoc.getDocumentElement();
		// get owner agency code
		Element ExtnElm = (Element) outDocElem.getElementsByTagName("Extn").item(0);
		String ExtnOwnerAgency = ExtnElm.getAttribute("ExtnOwnerAgency");
		// orderDtls this has all the ORDER DETAILS
		Element rootOrderElement = orderDtls.getDocumentElement();
		// get the EXTN node
		NodeList nlOrderExtn = rootOrderElement.getElementsByTagName("Extn");
		// define the element
		Element elemOrderExtn = (Element) nlOrderExtn.item(0);
		if (ExtnOwnerAgency.equals("FS")) {
			elemOrderExtn.setAttribute("ExtnFSAcctCode", ExtnRecvAcctCode);
			elemOrderExtn.setAttribute("ExtnOverrideCode", ExtnRAOverrideCode);
		} else if (ExtnOwnerAgency.equals("BLM")) {
			elemOrderExtn.setAttribute("ExtnBlmAcctCode", ExtnRecvAcctCode);
		} else {
			elemOrderExtn.setAttribute("ExtnOtherAcctCode", ExtnRecvAcctCode);
		}
		// end of CR 388

		if (orderStatusList != null && orderStatusList.getLength() > 0) {
			Element orderStatusElm = (Element) orderStatusList.item(0);
			releaseKey = orderStatusElm.getAttribute(NWCGConstants.RELEASE_KEY);
		}
		String releaseDate = "";
		if (releaseKey != null && releaseKey.length() > 0) {
			releaseDate = getReleaseDate(env, releaseKey);
		}
		// String releaseKey = orderStatusList.item(0)
		Document issueDtls = getOtherInfoAndPrepareXml(env, orderDtls, orderLineListDtls, releaseDate, shipmentLine);
		// remove the order lines here starts
		Element orderLineList = issueDtls.getDocumentElement();
		Element ollDSTElm = issueDtls.createElement("OrderLines");
		NodeList olSoureNL = issueDtls.getElementsByTagName("OrderLine");
		int nlLength = olSoureNL.getLength();
		Node tempNode;
		for (int i = 0; i < nlLength; i++) {
			Element olElm = (Element) olSoureNL.item(i);
			String aa = olElm.getAttribute("OrderedQty");
			if (aa.equals("0.00")) {
				System.out.println("@@@@@ do nothing");
			} else {
				// wnls.appendChild(wnle);
				tempNode = olElm.cloneNode(true);
				ollDSTElm.appendChild(tempNode);
			}
		}
		orderLineList.replaceChild(ollDSTElm, orderLineList.getElementsByTagName("OrderLines").item(0));
		// remove the order lines here ends
		System.out.println("@@@@@ Prepared XML : after remove " + XMLUtil.getXMLString(issueDtls));
		Element tempElm = issueDtls.getDocumentElement();
		NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
		// String documentId = _properties.getProperty("PrintDocumentId");
		// String printerId = _properties.getProperty("PrinterId");
		String shipNode = orderDtlsElm.getAttribute(NWCGConstants.SHIP_NODE);
		Document outDoc;
		Document issueReportDoc = reportsUtil.generatePrintHeader(documentId, "xml:/Order", orgCode, printerId, enterpriseCode);
		Element issueRptElmRootNode = issueReportDoc.getDocumentElement();
		Element printDocElm = (Element) issueRptElmRootNode.getFirstChild();
		Element inputDataElm = issueReportDoc.createElement("InputData");
		Element issueDtlsElm = issueReportDoc.createElement("Order");
		// -- added for Total values/units -- //
		tempElm.setAttribute("TotalVolume", TotalVolume);
		tempElm.setAttribute("TotalVolumeUOM", TotalVolumeUOM);
		tempElm.setAttribute("TotalWeight", TotalWeight);
		tempElm.setAttribute("TotalWeightUOM", TotalWeightUOM);
		// -- added for Total values/units -- //
		XMLUtil.copyElement(issueReportDoc, tempElm, issueDtlsElm);
		inputDataElm.appendChild(issueDtlsElm);
		printDocElm.appendChild(inputDataElm);
		System.out.println("@@@@@ XML to printDocumentSet : " + XMLUtil.getXMLString(issueReportDoc));
		Element shipElm = inputDoc.createElement("Shipment");
		inputDoc.getDocumentElement().appendChild(shipElm);
		shipElm.setAttribute("ShipmentKey", shipmentKey);
		rootElem3.setAttribute("EnterpriseCode", enterpriseCode);
		System.out.println("@@@@@ Input Doc with ShipmentKey : " + XMLUtil.getXMLString(inputDoc));
		for (int i = 0; i < noCopies; i++) {
			CommonUtilities.invokeAPI(env, NWCGConstants.API_PRINT_DOCUMENT_SET, (CommonUtilities.invokeService(env, NWCGConstants.SERVICE_XML_SORTING, issueReportDoc)));
			outDoc = CommonUtilities.invokeService(env, "NWCGPrintShippingTagSerials", inputDoc);
		}

		System.out.println("@@@@@ Exiting NWCGReportsIssueTransferIATAI::triggerIssueTransferIATAI @@@@@");
		return issueReportDoc;
	}

	/**
	 * NWCGReportsIssueTransferIATAI_getShipmentHeaderFromOrder
	 * 
	 * @param env
	 * @param orderHdrKey
	 * @return
	 */
	private Document getShipmentLineFromOrder(YFSEnvironment env, String orderHdrKey) {
		System.out.println("@@@@@ Entering NWCGReportsIssueTransferIATAI::getShipmentLineFromOrder @@@@@");
		Document result = null;
		try {
			result = XMLUtil.getDocument();
			Document inputDoc = XMLUtil.getDocument();
			Element orderElm = inputDoc.createElement("ShipmentLine");
			inputDoc.appendChild(orderElm);
			orderElm.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHdrKey);
			result = CommonUtilities.invokeAPI(env, "NWCGReports_getShipmentLineFromOrder", "getShipmentLineList", inputDoc);
			System.out.println("@@@@@ output : " + XMLUtil.getXMLString(result));
		} catch (ParserConfigurationException pce) {
			System.out.println("!!!!! ParserConfigurationException Msg : " + pce.getMessage());
			System.out.println("!!!!! StackTrace : " + pce.getStackTrace());
		} catch (Exception e) {
			System.out.println("!!!!! Exception Msg : " + e.getMessage());
			System.out.println("!!!!! StackTrace : " + e.getStackTrace());
		}
		System.out.println("@@@@@ Exiting NWCGReportsIssueTransferIATAI::getShipmentLineFromOrder @@@@@");
		return result;
	}

	/**
	 * NWCGReports_getOrderDetails xml removed all additional elements that are
	 * not required
	 * 
	 * @param env
	 * @param orderId
	 * @param enterpriseCode
	 * @param docType
	 * @return
	 */
	private Document getOrderDetails(YFSEnvironment env, String orderHdrKey) {
		System.out.println("@@@@@ Entering NWCGReportsIssueTransferIATAI::getOrderDetails @@@@@");
		Document result = null;
		try {
			result = XMLUtil.getDocument();
			Document inputDoc = XMLUtil.getDocument();
			Element orderElm = inputDoc.createElement("Order");
			inputDoc.appendChild(orderElm);
			orderElm.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHdrKey);
			result = CommonUtilities.invokeAPI(env, "NWCGReports_getOrderDetails", NWCGConstants.API_GET_ORDER_DETAILS, inputDoc);
			System.out.println("@@@@@ getOrderDetails output : " + XMLUtil.getXMLString(result));
		} catch (ParserConfigurationException pce) {
			System.out.println("!!!!! ParserConfigurationException Msg : " + pce.getMessage());
			System.out.println("!!!!! StackTrace : " + pce.getStackTrace());
		} catch (Exception e) {
			System.out.println("!!!!! Exception Msg : " + e.getMessage());
			System.out.println("!!!!! StackTrace : " + e.getStackTrace());
		}
		Element rootElem = result.getDocumentElement();
		Element OrderTotElem = (Element) rootElem.getElementsByTagName("OverallTotals").item(0);
		String GrandTotal = OrderTotElem.getAttribute("GrandTotal");
		String ConvTotal = StringUtil.NumFormat(GrandTotal);
		OrderTotElem.setAttribute("GrandTotal", ConvTotal);
		System.out.println("@@@@@ Exiting NWCGReportsIssueTransferIATAI::getOrderDetails @@@@@");
		return result;
	}

	/**
	 * 
	 * @param env
	 * @param orderId
	 * @param enterpriseCode
	 * @param docType
	 * @param orderHeaderKey
	 * @return
	 */
	private Document getOrderLineList(YFSEnvironment env, String orderId, String enterpriseCode, String docType, String orderHeaderKey) {
		System.out.println("@@@@@ Entering NWCGReportsIssueTransferIATAI::getOrderLineList @@@@@");
		Document result = null;
		try {
			result = XMLUtil.getDocument();
			Document inputDoc = XMLUtil.getDocument();
			Element orderLineElm = inputDoc.createElement("OrderLine");
			inputDoc.appendChild(orderLineElm);
			orderLineElm.setAttribute(NWCGConstants.ORDER_NO, orderId);
			orderLineElm.setAttribute(NWCGConstants.ENTERPRISE_CODE_STR, enterpriseCode);
			orderLineElm.setAttribute(NWCGConstants.DOCUMENT_TYPE, docType);
			orderLineElm.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHeaderKey);
			result = CommonUtilities.invokeAPI(env, "NWCGReports_getOrderLineList", NWCGConstants.API_GET_ORDER_LINE_LIST, inputDoc);
			
			// Setting Order Line Notes
			NodeList listOfLines = result.getElementsByTagName("OrderLine");
			for (int i = 0; i < listOfLines.getLength(); i++) {
				Element curOrderLine = (Element) listOfLines.item(i);
				NodeList listOfNotes = curOrderLine.getElementsByTagName("Note");
				String LineNoteText = "";
				for (int j = 0; j < listOfNotes.getLength(); j++) {
					Element curLineNote = (Element) listOfNotes.item(j);
					LineNoteText = LineNoteText + " " + curLineNote.getAttribute("NoteText");
				}
				curOrderLine.setAttribute("NoteText", LineNoteText);
			}
			// End Setting Order Line Notes
			
			System.out.println("@@@@@ getOrderLineList output: " + XMLUtil.getXMLString(result));
		} catch (ParserConfigurationException pce) {
			System.out.println("!!!!! ParserConfigurationException Msg : " + pce.getMessage());
			System.out.println("!!!!! StackTrace : " + pce.getStackTrace());
		} catch (Exception e) {
			System.out.println("!!!!! Exception Msg : " + e.getMessage());
			System.out.println("!!!!! StackTrace : " + e.getStackTrace());
		}
		System.out.println("@@@@@ Exiting NWCGReportsIssueTransferIATAI::getOrderLineList @@@@@");
		return result;
	}

	/**
	 * 
	 * @param env
	 * @param shipNode
	 * @return
	 */
	private Document getShipNodeDetails(YFSEnvironment env, String shipNode) {
		System.out.println("@@@@@ Entering NWCGReportsIssueTransferIATAI::getShipNodeDetails @@@@@");
		Document result = null;
		try {
			result = XMLUtil.getDocument();
			Document inputDoc = XMLUtil.getDocument();
			Element organizationElm = inputDoc.createElement("Organization");
			inputDoc.appendChild(organizationElm);
			organizationElm.setAttribute(NWCGConstants.ORGANIZATION_KEY, shipNode);
			result = CommonUtilities.invokeAPI(env, "NWCGReports_getOrganizationList", NWCGConstants.API_GET_ORG_LIST, inputDoc);
			System.out.println("@@@@@ getOrganizationList output: " + XMLUtil.getXMLString(result));
		} catch (ParserConfigurationException pce) {
			System.out.println("!!!!! ParserConfigurationException Msg : " + pce.getMessage());
			System.out.println("!!!!! StackTrace : " + pce.getStackTrace());
		} catch (Exception e) {
			System.out.println("!!!!! Exception Msg : " + e.getMessage());
			System.out.println("!!!!! StackTrace : " + e.getStackTrace());
		}
		System.out.println("@@@@@ Exiting NWCGReportsIssueTransferIATAI::getShipNodeDetails @@@@@");
		return result;
	}

	/**
	 * 
	 * @param env
	 * @param releaseKey
	 * @return
	 */
	private String getReleaseDate(YFSEnvironment env, String releaseKey) {
		System.out.println("@@@@@ Entering NWCGReportsIssueTransferIATAI::getReleaseDate @@@@@");
		String releaseDate = "";
		try {
			Document result = XMLUtil.getDocument();
			Document inputDoc = XMLUtil.getDocument();
			Element orderRelElm = inputDoc.createElement("OrderReleaseDetail");
			inputDoc.appendChild(orderRelElm);
			orderRelElm.setAttribute(NWCGConstants.RELEASE_KEY, releaseKey);
			result = CommonUtilities.invokeAPI(env, "NWCGReports_getOrderReleaseDetails", NWCGConstants.API_GET_ORDER_RELEASE_DETAILS, inputDoc);
			System.out.println("@@@@@ getOrderReleaseDetails output: " + XMLUtil.getXMLString(result));
			releaseDate = result.getDocumentElement().getAttribute("Createts");
		} catch (ParserConfigurationException pce) {
			System.out.println("!!!!! ParserConfigurationException Msg : " + pce.getMessage());
			System.out.println("!!!!! StackTrace : " + pce.getStackTrace());
		} catch (Exception e) {
			System.out.println("!!!!! Exception Msg : " + e.getMessage());
			System.out.println("!!!!! StackTrace : " + e.getStackTrace());
		}
		System.out.println("@@@@@ Exiting NWCGReportsIssueTransferIATAI::getReleaseDate @@@@@");
		return releaseDate;
	}

	/**
	 * Here, we are forming the xml. While forming the xml, we need to make
	 * getItemDetails (to get the volume details) and getTaskList API call to
	 * get the location information
	 * 
	 * @param env
	 * @param orderDtls
	 * @return
	 */
	private Document getOtherInfoAndPrepareXml(YFSEnvironment env, Document orderDtls, Document orderLineDtls, String releaseDate, Document shipmentLine) {
		System.out.println("@@@@@ Entering NWCGReportsIssueTransferIATAI::getOtherInfoAndPrepareXml @@@@@");
		try {
			System.out.println("@@@@@ orderDtls -  XML: " + XMLUtil.getXMLString(orderDtls));
			System.out.println("@@@@@ orderLineDtls -  XML: " + XMLUtil.getXMLString(orderLineDtls));
			System.out.println("@@@@@ shipmentLine - XML: " + XMLUtil.getXMLString(shipmentLine));
			Hashtable orderLineReq = new Hashtable();
			Element orderLinesRootElm = orderLineDtls.getDocumentElement();
			NodeList orderLinesNL = orderLinesRootElm.getElementsByTagName("OrderLine");
			//Element shipmentLineRootElm = shipmentLine.getDocumentElement();
			//NodeList shipmentLinesNL = shipmentLineRootElm.getElementsByTagName("ShipmentLine");
			if (orderLinesNL != null && orderLinesNL.getLength() > 0) {
				int lines = orderLinesNL.getLength();
				for (int j = 0; j < lines; j++) {
					Element orderLineElm = (Element) orderLinesNL.item(j);
					//Element shipmentLineElm = (Element) shipmentLinesNL.item(j);
					String orderLineKey = orderLineElm.getAttribute(NWCGConstants.ORDER_LINE_KEY);
					String LineNoteText = orderLineElm.getAttribute("NoteText");
					Element extnElm = (Element) XMLUtil.getChildNodeByName(orderLineElm, "Extn");
					
					String requestNumber = extnElm.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
					String reqQty = orderLineElm.getAttribute(NWCGConstants.ORIGINAL_ORDERED_QTY);
					String issueQty = orderLineElm.getAttribute("ShippedQuantity");
					// String issueQty = shipmentLineElm.getAttribute("Quantity");
					String boQty = extnElm.getAttribute(NWCGConstants.BACKORDERED_QTY);
					String utfQty = extnElm.getAttribute(NWCGConstants.UTF_QTY);
					String fwdQty = extnElm.getAttribute(NWCGConstants.FWD_QTY);
					
					Vector orderLineVec = new Vector();
					orderLineVec.addElement(requestNumber); // S-No - 0
					orderLineVec.addElement(reqQty); // Requested Qty - 1
					orderLineVec.addElement(issueQty); // Issue Qty - 2
					orderLineVec.addElement(boQty); // BackOrdered Qty - 3
					orderLineVec.addElement(utfQty); // UTF Qty - 4
					orderLineVec.addElement(fwdQty); // FWD Qty - 5
					orderLineVec.addElement(LineNoteText); // LineNoteText - 6

					orderLineReq.put(orderLineKey, orderLineVec);
				}
			}
			Element orderDtlsRootElm = orderDtls.getDocumentElement();
			orderDtlsRootElm.setAttribute(NWCGConstants.RELEASE_TS, releaseDate);
			NWCGReportsUtil reportUtil = new NWCGReportsUtil();
			String strDate = reportUtil.dateToString(new java.util.Date(), "MM-dd-yyyy");
			orderDtlsRootElm.setAttribute("CurrentDate", strDate);
			String enterpriseCode = orderDtlsRootElm.getAttribute(NWCGConstants.ENTERPRISE_CODE_STR);
			Vector orderLineHz = new Vector();
			Vector orderLineNonHz = new Vector();
			String shipNode = "";
			double wt = 0.0;
			double vol = 0.0;
			
			// CR 385 KS
			DecimalFormat df = new DecimalFormat("###0.00");
			String unitWt = "";
			String unitVol = "";
			boolean addRegulationDesc = false;
			System.out.println("@@@@@ Getting the orderline information to order");
			NodeList orderLineList = orderDtlsRootElm.getElementsByTagName("OrderLine");
			if (orderLineList != null && orderLineList.getLength() > 0) {
				int len = orderLineList.getLength();
				System.out.println("@@@@@ Number of order lines : " + len);
				for (int i = 0; i < len; i++) {
					System.out.println("@@@@@ Processing line : " + i);
					Element orderLine = (Element) orderLineList.item(i);
					Element OrderTotElem = (Element) orderLine.getElementsByTagName("LineOverallTotals").item(0);
					String LineTotal = OrderTotElem.getAttribute("LineTotal");
					String ConvLineTotal = StringUtil.NumFormat(LineTotal);
					OrderTotElem.setAttribute("LineTotal", ConvLineTotal);
					String UnitPrice = OrderTotElem.getAttribute("UnitPrice");
					String ConvPrice = StringUtil.NumFormat(UnitPrice);
					OrderTotElem.setAttribute("UnitPrice", ConvPrice);
					String qtyStr = orderLine.getAttribute(NWCGConstants.ORDERED_QTY);
					double qty = 0;
					if (qtyStr != null && qtyStr.length() > 0) {
						qty = Double.parseDouble(qtyStr);
					}
					Element itemInfo = (Element) XMLUtil.getChildNodeByName(orderLine, "Item");
					String itemID = itemInfo.getAttribute(NWCGConstants.ITEM_ID);
					String uom = itemInfo.getAttribute(NWCGConstants.UNIT_OF_MEASURE);
					Document itemDtls = getItemDetails(env, enterpriseCode, itemID, uom);
					Element primaryInfoElm = (Element) XMLUtil.getChildNodeByName(itemDtls.getDocumentElement(), "PrimaryInformation");
					String isHazmat = primaryInfoElm.getAttribute(NWCGConstants.IS_HAZMAT);
					orderLine.setAttribute(NWCGConstants.IS_HAZMAT, isHazmat);
					String weight = primaryInfoElm.getAttribute(NWCGConstants.UNIT_WEIGHT);
					if (weight != null && weight.length() > 0) {
						double tmpWt = Double.parseDouble(weight);
						// CR 385 KS
						
						wt = 0.0;
						wt = wt + (tmpWt * qty);
					}
					String volume = primaryInfoElm.getAttribute(NWCGConstants.UNIT_VOLUME);
					if (volume != null && volume.length() > 0) {
						double tmpVol = Double.parseDouble(volume);
						vol = vol + (tmpVol * qty);
					}
					String tmpWtUom = primaryInfoElm.getAttribute(NWCGConstants.UNIT_WEIGHT_UOM);
					String tmpVolUom = primaryInfoElm.getAttribute(NWCGConstants.UNIT_VOLUME_UOM);
					if (tmpWtUom != null && tmpWtUom.length() > 0) {
						unitWt = tmpWtUom;
					}
					if (tmpVolUom != null && tmpVolUom.length() > 0) {
						unitVol = tmpVolUom;
					}
					String orderLineKey = orderLine.getAttribute(NWCGConstants.ORDER_LINE_KEY);
					Vector orderLineVector = (Vector) orderLineReq.get(orderLineKey);
					System.out.println("@@@@@ OrderLineVector : " + orderLineVector.toString());
					String requestNum = (String) orderLineVector.elementAt(0);
					if (requestNum == null || requestNum.length() < 1) {
						requestNum = "";
					}
					orderLine.setAttribute(NWCGConstants.EXTN_REQUEST_NO, requestNum);
					orderLine.setAttribute(NWCGConstants.ORIGINAL_ORDERED_QTY, (String) orderLineVector.elementAt(1));
					orderLine.setAttribute(NWCGConstants.ORDERED_QTY, (String) orderLineVector.elementAt(2));
					orderLine.setAttribute(NWCGConstants.BACKORDERED_QTY, (String) orderLineVector.elementAt(3));
					orderLine.setAttribute(NWCGConstants.UTF_QTY, (String) orderLineVector.elementAt(4));
					orderLine.setAttribute(NWCGConstants.FWD_QTY, (String) orderLineVector.elementAt(5));
					orderLine.setAttribute("NoteText", (String) orderLineVector.elementAt(6));
					String HazmatText = "";
					String properShippingName = "";
					String hazardClass = "";
					String UNNumber = "";
					String packingGroup = "";
					String itemunitWt = "";
					double dblWt = 0.0;
					double dblTotalItemWt = 0.0;
					double kitQty = 0.0;
					double dblComponentWeight = 0.0;
					double dblTotalComponentWeight = 0.0;
					double dblComponentQty = 0.0;
					double dblComItemWt = 0.0;
					double dblTotalHazWt = 0.0;
					if (isHazmat.equalsIgnoreCase("Y")) {
						addRegulationDesc = true;
						System.out.println("@@@@@ itemID " + itemID);
						System.out.println("@@@@@ enterpriseCode " + enterpriseCode);
						System.out.println("@@@@@ uom " + uom);
						NodeList componentList = itemDtls.getElementsByTagName("Component");
						System.out.println("@@@@@ componentList length " + componentList.getLength());
						if (componentList != null && componentList.getLength() > 0) {
							for (int j = 0; j < componentList.getLength(); j++) {
								Element component = (Element) componentList.item(j);
								String ComponentItemID = component.getAttribute("ComponentItemID");
								String ComponentUnitOfMeasure = component.getAttribute("ComponentUnitOfMeasure");
								String ComponentOrganizationCode = component.getAttribute("ComponentOrganizationCode");
								String ComponentKitQuantity = component.getAttribute("KitQuantity");
								dblWt = 0.0;
								System.out.println("@@@@@ Component Item ID  " + ComponentItemID);
								Document componentItemDetails = null;
								try {
									Document itemDetailsInputDoc = XMLUtil.getDocument();
									Element itemDetailsElm = itemDetailsInputDoc.createElement("Item");
									itemDetailsElm.setAttribute(NWCGConstants.ORGANIZATION_CODE, ComponentOrganizationCode);
									itemDetailsElm.setAttribute(NWCGConstants.ITEM_ID, ComponentItemID);
									itemDetailsElm.setAttribute(NWCGConstants.UNIT_OF_MEASURE, ComponentUnitOfMeasure);
									itemDetailsInputDoc.appendChild(itemDetailsElm);
									System.out.println("@@@@@ Input : " + XMLUtil.getXMLString(itemDetailsInputDoc));
									componentItemDetails = CommonUtilities.invokeAPI(env, "NWCGReports_getItemDetails", NWCGConstants.API_GET_ITEM_DETAILS, itemDetailsInputDoc);
									System.out.println("@@@@@ Output : " + XMLUtil.getXMLString(componentItemDetails));
								} catch (Exception e) {
									System.out.println("!!!!! Exception Msg : " + e.getMessage());
									System.out.println("!!!!! StackTrace : " + e.getStackTrace());
								}
								Element componentPrimaryInfoElm = (Element) XMLUtil.getChildNodeByName(componentItemDetails.getDocumentElement(), "PrimaryInformation");
								String isComponentHazmat = componentPrimaryInfoElm.getAttribute(NWCGConstants.IS_HAZMAT);
								String componentWeight = componentPrimaryInfoElm.getAttribute(NWCGConstants.UNIT_WEIGHT);
								NodeList componentList2 = componentItemDetails.getElementsByTagName("Component");
								dblTotalComponentWeight = 0.0;
								dblTotalItemWt = 0.0;
								if (componentList2 == null || componentList2.getLength() == 0) {
									if (isComponentHazmat.equalsIgnoreCase("Y")) {
										System.out.println("@@@@@ HazMat Component Item Weight: " + componentWeight);
										System.out.println("@@@@@ Component Kit  Qty " + ComponentKitQuantity);
										dblComponentWeight = Double.parseDouble(componentWeight);
										dblComponentQty = Double.parseDouble(ComponentKitQuantity);
										dblTotalComponentWeight = (dblComponentWeight * dblComponentQty);
										System.out.println("@@@@@ Component Item Calculated Weight  " + dblTotalComponentWeight);
									}
								}
								if (componentList2 != null && componentList2.getLength() > 0) {
									for (int k = 0; k < componentList2.getLength(); k++) {
										Element subComponent = (Element) componentList2.item(k);
										String subComponentItemID = subComponent.getAttribute("ComponentItemID");
										String subComponentUnitOfMeasure = subComponent.getAttribute("ComponentUnitOfMeasure");
										String subComponentOrganizationCode = subComponent.getAttribute("ComponentOrganizationCode");
										String subComponentKitQuantity = subComponent.getAttribute("KitQuantity");
										if (subComponentKitQuantity != null && subComponentKitQuantity.length() > 0) {
											kitQty = Double.parseDouble(subComponentKitQuantity);
										}
										Document subComponentDetails = null;
										try {
											Document subComponentItemDetailsInputDoc = XMLUtil.getDocument();
											Element subComponentsItemDetailsElm = subComponentItemDetailsInputDoc.createElement("Item");
											subComponentsItemDetailsElm.setAttribute(NWCGConstants.ORGANIZATION_CODE, subComponentOrganizationCode);
											subComponentsItemDetailsElm.setAttribute(NWCGConstants.ITEM_ID, subComponentItemID);
											subComponentsItemDetailsElm.setAttribute(NWCGConstants.UNIT_OF_MEASURE, subComponentUnitOfMeasure);
											subComponentItemDetailsInputDoc.appendChild(subComponentsItemDetailsElm);
											System.out.println("@@@@@ Input : " + XMLUtil.getXMLString(subComponentItemDetailsInputDoc));
											subComponentDetails = CommonUtilities.invokeAPI(env, "NWCGReports_getComponentItemDetails", NWCGConstants.API_GET_ITEM_DETAILS, subComponentItemDetailsInputDoc);
											System.out.println("@@@@@ Output : " + XMLUtil.getXMLString(subComponentDetails));
										} catch (Exception e) {
											System.out.println("!!!!! Exception Msg : " + e.getMessage());
											System.out.println("!!!!! StackTrace : " + e.getStackTrace());
										}
										Element subComponentsDtlsElm = subComponentDetails.getDocumentElement();
										Element primaryInfoSubComponentElm = (Element) XMLUtil.getChildNodeByName(subComponentDetails.getDocumentElement(), "PrimaryInformation");
										String isSubComponentHazmat = primaryInfoSubComponentElm.getAttribute(NWCGConstants.IS_HAZMAT);
										String isSubComponentWeight = primaryInfoSubComponentElm.getAttribute(NWCGConstants.UNIT_WEIGHT);
										String itemIDSubcomponent = subComponentsDtlsElm.getAttribute("ItemID");
										if (isSubComponentHazmat.equalsIgnoreCase("Y")) {
											double dblSubComponentWt = Double.parseDouble(isSubComponentWeight);
											System.out.println("@@@@@ " + subComponentItemID + " subComponent ItemID " + kitQty + " kitQty " + isSubComponentWeight + " isSubComponentWeight");
											dblWt = dblWt + (dblSubComponentWt * kitQty);
										}
									}
									// dblTotalItemWt = dblTotalItemWt + (dblWt * qty);
									dblTotalItemWt = dblTotalItemWt + dblWt;
								}
								System.out.println("@@@@@ dblTotalComponentWeight " + dblTotalComponentWeight);
								System.out.println("@@@@@ ItemID Weight: " + dblTotalItemWt);
								dblTotalHazWt = dblTotalHazWt + dblTotalComponentWeight + dblTotalItemWt;
								System.out.println("@@@@@ TOTAL ITEM WEIGHT - RUNNING TOTAL: " + dblTotalHazWt);
							}
							System.out.println("@@@@@ dblTotalHazWt	" + dblTotalHazWt);
							System.out.println("@@@@@ qty		" + qty);
							dblTotalHazWt = (dblTotalHazWt * qty);
							System.out.println("@@@@@ end of calculation ItemID " + itemID + "'s weight = " + dblTotalHazWt);
						}

						NodeList hazmatline = itemDtls.getDocumentElement().getElementsByTagName("HazmatInformation");
						if (hazmatline.getLength() > 0) {
							Element hazmatElm = (Element) XMLUtil.getChildNodeByName(itemDtls.getDocumentElement(), "HazmatInformation");
							// CR917/JB10 Hazmat text format change -- moved this "if condition" to first
							if (hazmatElm.hasAttribute(NWCGConstants.IATA_UNNUMBER)) {
								UNNumber = hazmatElm.getAttribute(NWCGConstants.IATA_UNNUMBER);
								HazmatText = HazmatText + UNNumber;
							} 
							// CR917/JB10 end.
							if (hazmatElm.hasAttribute(NWCGConstants.PROPER_SHIPPING_NAME)) {
								properShippingName = hazmatElm.getAttribute(NWCGConstants.IATA_PROPER_SHIPPING_NAME);
								HazmatText = HazmatText + " " + properShippingName;
							}
							if (hazmatElm.hasAttribute(NWCGConstants.IATA_HAZARD_CLASS)) {
								hazardClass = hazmatElm.getAttribute(NWCGConstants.IATA_HAZARD_CLASS);
								HazmatText = HazmatText + " " + hazardClass;
							}
							// moved below text to first "if" condition as part of CR917/JB10
							/*
							 * if
							 * (hazmatElm.hasAttribute(NWCGConstants.IATA_UNNUMBER)) {
							 * UNNumber =
							 * hazmatElm.getAttribute(NWCGConstants.IATA_UNNUMBER);
							 * HazmatText = HazmatText + " " + UNNumber; }
							 */
							if (hazmatElm.hasAttribute(NWCGConstants.IATA_PACKING_GROUP)) {
								packingGroup = hazmatElm.getAttribute(NWCGConstants.IATA_PACKING_GROUP);
								HazmatText = HazmatText + " " + packingGroup;
							}
							// if (weight != null) {
							if (uom.equals("KT")) {
								if (dblTotalHazWt == 0.00) {
									// HazmatText = HazmatText;
								} else {
									HazmatText = HazmatText + " " + df.format(dblTotalHazWt) + " " + unitWt;
								}
								System.out.println("@@@@@ " + itemID + " HazmatText: " + HazmatText);
							} else {
								// HazmatText = HazmatText + " " + df.format(dblWt) + " " + unitWt;
								HazmatText = HazmatText + " " + df.format(wt) + " " + unitWt;
								System.out.println("@@@@@ " + itemID + " HazmatText: " + HazmatText);
							}
							// }
						}
						// hazmatline.getLength()
						orderLine.setAttribute(NWCGConstants.PROPER_SHIPPING_NAME, properShippingName);
						orderLine.setAttribute(NWCGConstants.HAZARD_CLASS, hazardClass);
						orderLine.setAttribute(NWCGConstants.UNNUMBER, UNNumber);
						orderLine.setAttribute(NWCGConstants.PACKING_GROUP, packingGroup);
						orderLine.setAttribute(NWCGConstants.UNIT_WEIGHT, weight);
						orderLine.setAttribute("HazmatText", HazmatText);
						orderLineHz.addElement(orderLine);
					} else {
						orderLine.setAttribute(NWCGConstants.PROPER_SHIPPING_NAME, properShippingName);
						orderLine.setAttribute(NWCGConstants.HAZARD_CLASS, hazardClass);
						orderLine.setAttribute(NWCGConstants.UNNUMBER, UNNumber);
						orderLine.setAttribute(NWCGConstants.PACKING_GROUP, packingGroup);
						orderLine.setAttribute(NWCGConstants.UNIT_WEIGHT, itemunitWt);
						orderLine.setAttribute("HazmatText", HazmatText);
						orderLineNonHz.addElement(orderLine);
					}
				}
			} else {
				System.out.println("@@@@@ There are no order lines for this order");
			}

			System.out.println("@@@@@ Updated XML : " + XMLUtil.getXMLString(orderDtls));
			System.out.println("@@@@@ Stored the information in respective vectors hazmat and non-hazmat");
			Element regulationInfoElm = orderDtls.createElement("RegulationInfo");
			orderDtlsRootElm.appendChild(regulationInfoElm);
			if (addRegulationDesc) {
				String desc = "THIS IS TO CERTIFY THE BELOW NAMED MATERIALS ARE PROPERLY CLASSIFIED, DESCRIBED, PACKAGED, " + "MARKED AND LABELED AND ARE IN PROPER CONDITION FOR TRANSPORTATION ACCORDING TO THE APPLICATION " + "REGULATIONS OF THE DEPT OF TRANSPORTATION";
				regulationInfoElm.setAttribute("Description", desc);
				regulationInfoElm.setAttribute("SignatureString", "Signature of Shipper");
				regulationInfoElm.setAttribute("EmergencyPhoneNoString", "Emergency Response Phone Number");
				regulationInfoElm.setAttribute("EmergencyPhoneNo", "(800)424-9300");
			} else {
				regulationInfoElm.setAttribute("Description", "");
				regulationInfoElm.setAttribute("SignatureString", "");
				regulationInfoElm.setAttribute("EmergencyPhoneNoString", "");
				regulationInfoElm.setAttribute("EmergencyPhoneNo", "");
			}
			
			// Setting Order Header Notes
			NodeList listOfNotes = orderDtls.getElementsByTagName("Note");
			String NoteText = "";
			for (int n = 0; n < listOfNotes.getLength(); n++) {
				Element curNote = (Element) listOfNotes.item(n);
				NoteText = NoteText + " " + curNote.getAttribute("NoteText");
			}
			Element OrdDtlsElm = (Element) orderDtls.getElementsByTagName("Order").item(0);
			OrdDtlsElm.setAttribute("NoteText", NoteText);
			// End Setting Order Header Notes

			// Setting Ship To Address Default is Issue "Deliver To". If "Deliver To" is NULL, then "Ship To" will be set.
			String deliverToAddr1 = "";
			Element DeliverAddrElm = (Element) orderDtls.getElementsByTagName("AdditionalAddress").item(0);
			Element ShipToAddrElm = (Element) orderDtls.getElementsByTagName("PersonInfoShipTo").item(0);
			Element DeliverToAddrElm = null;
			if (DeliverAddrElm != null) {
				DeliverToAddrElm = (Element) DeliverAddrElm.getElementsByTagName("PersonInfo").item(0);
				if (DeliverToAddrElm != null)
					deliverToAddr1 = DeliverToAddrElm.getAttribute("AddressLine1");
			}
			Element toAddrElem = orderDtls.createElement("ShipToAddress");
			orderDtlsRootElm.appendChild(toAddrElem);
			if (deliverToAddr1.length() > 1) {
				XMLUtil.copyElement(orderDtls, DeliverToAddrElm, toAddrElem);
			} else {
				XMLUtil.copyElement(orderDtls, ShipToAddrElm, toAddrElem);
			}
			// End Setting Ship To Address

			shipNode = orderDtlsRootElm.getAttribute(NWCGConstants.SHIP_NODE);
			Document shipNodeDoc = XMLUtil.getDocument();
			if (shipNode != null && shipNode.length() > 0) {
				System.out.println("@@@@@ Getting the organization details");
				shipNodeDoc = getShipNodeDetails(env, shipNode);
				System.out.println("@@@@@ Obtained ship node organization details : " + XMLUtil.getXMLString(shipNodeDoc));
				Element shipNodeRootElm = shipNodeDoc.getDocumentElement();
				Element orgElm = (Element) XMLUtil.getChildNodeByName(shipNodeRootElm, "Organization");
				String orgName = orgElm.getAttribute(NWCGConstants.ORGANIZATION_NAME);
				Element corporateOrgElm = (Element) XMLUtil.getChildNodeByName(orgElm, "CorporatePersonInfo");
				String addrLine1 = corporateOrgElm.getAttribute(NWCGConstants.ADDRESS_LINE_1);
				String addrLine2 = corporateOrgElm.getAttribute(NWCGConstants.ADDRESS_LINE_2);
				String city = corporateOrgElm.getAttribute(NWCGConstants.CITY);
				String state = corporateOrgElm.getAttribute(NWCGConstants.STATE);
				String zip = corporateOrgElm.getAttribute(NWCGConstants.ZIP_CODE);
				String dayPhone = corporateOrgElm.getAttribute(NWCGConstants.DAY_PHONE);
				String mobilePhone = corporateOrgElm.getAttribute(NWCGConstants.MOBILE_PHONE);
				String httpUrl = corporateOrgElm.getAttribute(NWCGConstants.HTTP_URL);
				Element issueNodeElm = orderDtls.createElement("ShipNode");
				issueNodeElm.setAttribute(NWCGConstants.ORGANIZATION_NAME, orgName);
				issueNodeElm.setAttribute(NWCGConstants.ADDRESS_LINE_1, addrLine1);
				issueNodeElm.setAttribute(NWCGConstants.ADDRESS_LINE_2, addrLine2);
				issueNodeElm.setAttribute(NWCGConstants.CITY, city);
				issueNodeElm.setAttribute(NWCGConstants.STATE, state);
				issueNodeElm.setAttribute(NWCGConstants.ZIP_CODE, zip);
				issueNodeElm.setAttribute(NWCGConstants.DAY_PHONE, dayPhone);
				issueNodeElm.setAttribute(NWCGConstants.MOBILE_PHONE, mobilePhone);
				issueNodeElm.setAttribute(NWCGConstants.HTTP_URL, httpUrl);
				orderDtlsRootElm.appendChild(issueNodeElm);
				System.out.println("@@@@@ Organization values are set in order details");
			}
			System.out.println("@@@@@ Before deleting lines : " + XMLUtil.getXMLString(orderDtls));
			
			// We are deleting all the order lines and then adding the order hazmat order. If there are hazmat items, then we are adding them first.
			Node orderLinesNode = XMLUtil.getChildNodeByName(orderDtlsRootElm, "OrderLines");
			orderDtlsRootElm.removeChild(orderLinesNode);
			System.out.println("@@@@@ Adding order lines");
			Element orderLinesElm = orderDtls.createElement("OrderLines");
			for (int i = 0; (orderLineHz != null) && (i < orderLineHz.size()); i++) {
				System.out.println("@@@@@ Adding hazmat lines : " + i);
				Element orderLine = (Element) orderLineHz.elementAt(i);
				orderLinesElm.appendChild(orderLine);
			}
			for (int i = 0; (orderLineNonHz != null) && (i < orderLineNonHz.size()); i++) {
				System.out.println("@@@@@ Adding non hazmat lines : " + i);
				Element orderLine = (Element) orderLineNonHz.elementAt(i);
				orderLinesElm.appendChild(orderLine);
			}
			orderDtlsRootElm.appendChild(orderLinesElm);

			System.out.println("@@@@@ Adding weight and volumne related values");
			String totWtWithUom = StringUtil.NumFormat((new Double(wt).toString()));
			totWtWithUom = totWtWithUom + " " + unitWt;
			orderDtlsRootElm.setAttribute(NWCGConstants.TOTAL_WEIGHT, totWtWithUom);
			String totVolWithUom = StringUtil.NumFormat((new Double(vol).toString()));
			totVolWithUom = totVolWithUom + " " + unitVol;
			orderDtlsRootElm.setAttribute(NWCGConstants.TOTAL_VOLUME, totVolWithUom);
			Element orderDtlsExtnElm = XMLUtil.getFirstElementByName(orderDtlsRootElm, "Extn");
			String incidentNo = orderDtlsExtnElm.getAttribute("ExtnToIncidentNo");
			System.out.println("Incident No " + incidentNo);
			String incidentYear = orderDtlsExtnElm.getAttribute("ExtnToIncidentYear");
			System.out.println("Incident Year " + incidentYear);
			String CustomerName = "";
			if (incidentNo.length() > 0) {
				System.out.println("Before Calling Incident Details");
				Document incidentDtls = getIncidentDetails(env, incidentNo, incidentYear);
				System.out.println("After Calling Incident Details");
				CustomerName = incidentDtls.getDocumentElement().getAttribute("CustomerName");
				System.out.println("Cust Name " + CustomerName);
			}
			orderDtlsRootElm.setAttribute("CustomerName", CustomerName);
			System.out.println("@@@@@ After adjust lines : " + XMLUtil.getXMLString(orderDtls));
		} catch (ParserConfigurationException pce) {
			System.out.println("!!!!! ParserConfigurationException Msg : " + pce.getMessage());
			System.out.println("!!!!! StackTrace : " + pce.getStackTrace());
		} catch (Exception e) {
			System.out.println("!!!!! Exception Msg : " + e.getMessage());
			System.out.println("!!!!! StackTrace : " + e.getStackTrace());
		}
		System.out.println("@@@@@ orderDtls " + XMLUtil.getXMLString(orderDtls));
		System.out.println("@@@@@ Exiting NWCGReportsIssueTransferIATAI::getOtherInfoAndPrepareXml @@@@@");
		return orderDtls;
	}

	/**
	 * 
	 * @param env
	 * @param shipmentKey
	 * @return
	 */
	public Document getOrderHeaderKey(YFSEnvironment env, String shipmentKey) {
		System.out.println("@@@@@ Entering NWCGReportsIssueTransferIATAI::getOrderHeaderKey @@@@@");
		String orderHdrKey = "";
		Document shipmentDtls = null;
		try {
			Document ShipmentHdrInputDoc = XMLUtil.getDocument();
			Element ShipmentHdrElm = ShipmentHdrInputDoc.createElement("Shipment");
			ShipmentHdrElm.setAttribute("ShipmentKey", shipmentKey);
			ShipmentHdrInputDoc.appendChild(ShipmentHdrElm);
			shipmentDtls = CommonUtilities.invokeAPI(env, "NWCGReports_getShipmentDetails", "getShipmentDetails", ShipmentHdrInputDoc);
			System.out.println("@@@@@ Output from getShipmentDetails : " + XMLUtil.getXMLString(shipmentDtls));
			Element shipmentDtlsElm = (Element) shipmentDtls.getElementsByTagName("ShipmentLine").item(0);
			orderHdrKey = shipmentDtlsElm.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
			System.out.println("@@@@@ OrderHeaderKey : " + orderHdrKey);
		} catch (Exception e) {
			System.out.println("!!!!! Exception Msg : " + e.getMessage());
			System.out.println("!!!!! StackTrace : " + e.getStackTrace());
		}
		System.out.println("@@@@@ Exiting NWCGReportsIssueTransferIATAI::getOrderHeaderKey @@@@@");
		return shipmentDtls;
	}

	/**
	 * 
	 * @param env
	 * @param enterpriseCode
	 * @param itemID
	 * @param uom
	 * @return
	 */
	private Document getItemDetails(YFSEnvironment env, String enterpriseCode, String itemID, String uom) {
		System.out.println("@@@@@ Entering NWCGReportsIssueTransferIATAI::getItemDetails @@@@@");
		Document itemDtls = null;
		try {
			Document itemDtlsInputDoc = XMLUtil.getDocument();
			Element itemElm = itemDtlsInputDoc.createElement("Item");
			//itemElm.setAttribute(NWCGConstants.ENTERPRISE_CODE_STR, enterpriseCode);
			itemElm.setAttribute(NWCGConstants.ORGANIZATION_CODE, enterpriseCode);
			itemElm.setAttribute(NWCGConstants.ITEM_ID, itemID);
			itemElm.setAttribute(NWCGConstants.UNIT_OF_MEASURE, uom);
			itemDtlsInputDoc.appendChild(itemElm);
			System.out.println("@@@@@ Input : " + XMLUtil.getXMLString(itemDtlsInputDoc));
			itemDtls = CommonUtilities.invokeAPI(env, "NWCGReports_getItemDetails", NWCGConstants.API_GET_ITEM_DETAILS, itemDtlsInputDoc);
			System.out.println("@@@@@ Output : " + XMLUtil.getXMLString(itemDtls));
		} catch (ParserConfigurationException pce) {
			System.out.println("!!!!! ParserConfigurationException Msg : " + pce.getMessage());
			System.out.println("!!!!! StackTrace : " + pce.getStackTrace());
		} catch (Exception e) {
			System.out.println("!!!!! Exception Msg : " + e.getMessage());
			System.out.println("!!!!! StackTrace : " + e.getStackTrace());
		}
		System.out.println("@@@@@ Exiting NWCGReportsIssueTransferIATAI::getItemDetails @@@@@");
		return itemDtls;
	}

	/**
	 * 
	 * @param env
	 * @param incidentNo
	 * @param Year
	 * @return
	 */
	private Document getIncidentDetails(YFSEnvironment env, String incidentNo, String Year) {
		System.out.println("@@@@@ Entering NWCGReportsIssueTransferIATAI::getIncidentDetails @@@@@");
		Document incidentDtls = null;
		try {
			incidentDtls = XMLUtil.getDocument();
			Document getIncidentOrderInput = XMLUtil.createDocument("NWCGIncidentOrder");
			getIncidentOrderInput.getDocumentElement().setAttribute("IncidentNo", incidentNo);
			getIncidentOrderInput.getDocumentElement().setAttribute("Year", Year);
			incidentDtls = CommonUtilities.invokeService(env, NWCGConstants.NWCG_GET_INCIDENT_ORDER_SERVICE, getIncidentOrderInput);
		} catch (ParserConfigurationException pce) {
			System.out.println("!!!!! ParserConfigurationException Msg : " + pce.getMessage());
			System.out.println("!!!!! StackTrace : " + pce.getStackTrace());
		} catch (Exception e) {
			System.out.println("!!!!! Exception Msg : " + e.getMessage());
			System.out.println("!!!!! StackTrace : " + e.getStackTrace());
		}
		System.out.println("@@@@@ Exiting NWCGReportsIssueTransferIATAI::getIncidentDetails @@@@@");
		return incidentDtls;
	}
}