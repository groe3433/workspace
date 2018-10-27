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
public class NWCGReportsIssueIATA implements YIFCustomApi {

	private Properties _properties;

	// GENERAL_APP_LOGGER - configured in log4jconfig.custom.xml
	//private static YFCLogCategory cat = YFCLogCategory.instance(NWCGReportsIssueIATA.class);

	NWCGReportsUtil reportsUtil = new NWCGReportsUtil();

	/**
	 * 
	 * @param arg0
	 * @throws Exception
	 */
	public void setProperties(Properties arg0) throws Exception {
		System.out.println("@@@@@ In NWCGReportsIssueIATA::setProperties @@@@@");
		_properties = arg0;
	}

	/**
	 * 
	 * @param env
	 * @param inputDoc
	 * @return
	 * @throws Exception
	 */
	public Document triggerIssueReport(YFSEnvironment env, Document inputDoc) throws Exception {
		System.out.println("@@@@@ Entering NWCGReportsIssueIATA::triggerIssueReport @@@@@");
		System.out.println("@@@@@ Input Doc : " + XMLUtil.getXMLString(inputDoc));

		String documentId = "NWCG_ISSUE_REPORT";
		Element rootElem1 = (Element) inputDoc.getElementsByTagName("Shipment").item(0);
		String shipmentKey = rootElem1.getAttribute("ShipmentKey");
		Element rootElem2 = (Element) inputDoc.getElementsByTagName("PrinterPreference").item(0);
		String printerId = rootElem2.getAttribute("PrinterId");
		String userlocale = rootElem2.getAttribute("UserLocale");
		Element rootElem3 = (Element) inputDoc.getElementsByTagName("LabelPreference").item(0);
		String noOfCopies = rootElem3.getAttribute("NoOfCopies");
		int noCopies = 1;
		if (Integer.parseInt(noOfCopies) > 1)
			noCopies = Integer.parseInt(noOfCopies);
		
		Document shipmentDtls = getOrderHeaderKey(env, shipmentKey);
		Element shipmentDtlsElm = (Element) shipmentDtls.getElementsByTagName("ShipmentLine").item(0);
		String orderHdrKey = shipmentDtlsElm.getAttribute(NWCGConstants.ORDER_HEADER_KEY);

		Element shipmentrootElm = shipmentDtls.getDocumentElement();
		String TotalVolume = shipmentrootElm.getAttribute("TotalVolume");
		String TotalVolumeUOM = shipmentrootElm.getAttribute("TotalVolumeUOM");
		String TotalWeight = shipmentrootElm.getAttribute("TotalWeight");
		String TotalWeightUOM = shipmentrootElm.getAttribute("TotalWeightUOM");
		String releaseKey = "";

		Document orderDtls = getOrderDetails(env, orderHdrKey, userlocale);
		Element orderDtlsElm = orderDtls.getDocumentElement();
		String orderId = orderDtlsElm.getAttribute(NWCGConstants.ORDER_NO);
		String enterpriseCode = orderDtlsElm.getAttribute(NWCGConstants.ENTERPRISE_CODE_STR);
		String documentType = orderDtlsElm.getAttribute(NWCGConstants.DOCUMENT_TYPE);
		Document orderLineListDtls = getOrderLineList(env, orderId, enterpriseCode, documentType, orderHdrKey);
		NodeList orderStatusList = orderDtlsElm.getElementsByTagName("OrderStatus");
		if (orderStatusList != null && orderStatusList.getLength() > 0) {
			Element orderStatusElm = (Element) orderStatusList.item(0);
			releaseKey = orderStatusElm.getAttribute(NWCGConstants.RELEASE_KEY);
		}
		String releaseDate = "";
		if (releaseKey != null && releaseKey.length() > 0) {
			releaseDate = getReleaseDate(env, releaseKey);
		}
		// String releaseKey = orderStatusList.item(0)
		Document issueDtls = getOtherInfoAndPrepareXml(env, orderDtls, orderLineListDtls, releaseDate);
		Document sortedIssueDtls = CommonUtilities.invokeService(env, "NWCGSortedIssueReport", issueDtls);
		Element tempElm = sortedIssueDtls.getDocumentElement();
		// String documentId = _properties.getProperty("PrintDocumentId");
		// String printerId = _properties.getProperty("PrinterId");
		String shipNode = orderDtlsElm.getAttribute(NWCGConstants.SHIP_NODE);
		Document issueReportDoc = reportsUtil.generatePrintHeader(documentId, "xml:/Order", shipNode, printerId, enterpriseCode);
		Document outDoc;
		Element issueRptElmRootNode = issueReportDoc.getDocumentElement();
		Element printDocElm = (Element) issueRptElmRootNode.getFirstChild();
		Element inputDataElm = issueReportDoc.createElement("InputData");
		Element issueDtlsElm = issueReportDoc.createElement("Order");
		tempElm.setAttribute("TotalVolume", TotalVolume);
		tempElm.setAttribute("TotalVolumeUOM", TotalVolumeUOM);
		tempElm.setAttribute("TotalWeight", TotalWeight);
		tempElm.setAttribute("TotalWeightUOM", TotalWeightUOM);
		XMLUtil.copyElement(issueReportDoc, tempElm, issueDtlsElm);
		inputDataElm.appendChild(issueDtlsElm);
		printDocElm.appendChild(inputDataElm);
		System.out.println("@@@@@ XML to printDocumentSet : " + XMLUtil.getXMLString(issueReportDoc));
		for (int i = 0; i < noCopies; i++) {
			CommonUtilities.invokeAPI(env, NWCGConstants.API_PRINT_DOCUMENT_SET, (CommonUtilities.invokeService(env, NWCGConstants.SERVICE_XML_SORTING, issueReportDoc)));
			outDoc = CommonUtilities.invokeService(env, "NWCGPrintShippingTagSerials", inputDoc);
			// Top of CR 611 - if doctype is other issue -- check and print "Split Acct Code"
			if (documentType.equals("0007.ex")) {
				// only if docType is Other Issue
				Element inputDocelem = inputDoc.getDocumentElement();
				Element OrdDtlsExtnElm = (Element) orderDtls.getElementsByTagName("Extn").item(0);
				if (OrdDtlsExtnElm != null) { 
					// append the child from order extn to the inputDoc
					rootElem1.setAttribute("OrderNo", orderId); 
					// pass orderNo
					inputDocelem.appendChild(inputDoc.importNode(OrdDtlsExtnElm, true));
					outDoc = CommonUtilities.invokeService(env, "NWCGPrintSplitAcctCode", inputDoc);
				}
			}
			// Bottom of CR 611
		}
		System.out.println("@@@@@ Exiting NWCGReportsIssueIATA::triggerIssueReport @@@@@");
		return issueReportDoc;
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
	private Document getOrderDetails(YFSEnvironment env, String orderHdrKey, String usrlocale) {
		System.out.println("@@@@@ Entering NWCGReportsIssueIATA::getOrderDetails @@@@@");
		Document result = null;
		try {
			result = XMLUtil.getDocument();
			Document inputDoc = XMLUtil.getDocument();
			Element orderElm = inputDoc.createElement("Order");
			inputDoc.appendChild(orderElm);
			orderElm.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHdrKey);
			result = CommonUtilities.invokeAPI(env, "NWCGReports_getOrderDetails", NWCGConstants.API_GET_ORDER_DETAILS, inputDoc);
			String OrderDate = result.getDocumentElement().getAttribute("OrderDate");
			System.out.println("@@@@@ OrderDate " + OrderDate);
			String OrderDate_New = (OrderDate != null) ? reportsUtil.convertTimeZone(OrderDate, usrlocale) : OrderDate;
			System.out.println("@@@@@ OrderDate_New " + OrderDate_New);
			result.getDocumentElement().setAttribute("OrderDate", OrderDate_New);
			String ReqShipDate = result.getDocumentElement().getAttribute("ReqShipDate");
			System.out.println("@@@@@ ReqShipDate " + ReqShipDate);
			String ReqShipDate_New = (ReqShipDate != null) ? reportsUtil.convertTimeZone(ReqShipDate, usrlocale) : ReqShipDate;
			System.out.println("@@@@@ ReqShipDate_New " + ReqShipDate_New);
			result.getDocumentElement().setAttribute("ReqShipDate", ReqShipDate_New);
			NodeList orderDateList = result.getDocumentElement().getElementsByTagName("OrderDate");
			if (orderDateList != null) {
				Element elemDate = (Element) orderDateList.item(0);
				if (elemDate != null) {
					String ActualDate = elemDate.getAttribute("ActualDate");
					System.out.println("ActualDate " + ActualDate);
					String ActualDate_New = (ActualDate != null) ? reportsUtil.convertTimeZone(ActualDate, usrlocale) : ActualDate;
					System.out.println("ActualDate_New " + ActualDate_New);
					elemDate.setAttribute("ActualDate", ActualDate_New);
				}
			}
			NodeList extnList = result.getDocumentElement().getElementsByTagName("Extn");
			if (extnList != null) {
				Element elemExtn = (Element) extnList.item(0);
				if (elemExtn != null) {
					String ExtnReqDeliveryDate = elemExtn.getAttribute("ExtnReqDeliveryDate");
					System.out.println("@@@@@ ExtnReqDeliveryDate " + ExtnReqDeliveryDate);
					String ExtnReqDeliveryDate_New = (ExtnReqDeliveryDate != null) ? reportsUtil.convertTimeZone(ExtnReqDeliveryDate, usrlocale) : ExtnReqDeliveryDate;
					System.out.println("@@@@@ ExtnReqDeliveryDate_New " + ExtnReqDeliveryDate_New);
					elemExtn.setAttribute("ExtnReqDeliveryDate", ExtnReqDeliveryDate_New);
				}
			}
			System.out.println("@@@@@ getOrderDetails output : " + XMLUtil.getXMLString(result));
		} catch (ParserConfigurationException pce) {
			System.out.println("!!!!! ParserConfigurationException Msg : " + pce.getMessage());
			System.out.println("!!!!! StackTrace : " + pce.getStackTrace());
		} catch (Exception e) {
			System.out.println("!!!!! Exception Msg : " + e.getMessage());
			System.out.println("!!!!! StackTrace : " + e.getStackTrace());
		}
		System.out.println("@@@@@ Exiting NWCGReportsIssueIATA::getOrderDetails @@@@@");
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
		System.out.println("@@@@@ Entering NWCGReportsIssueIATA::getOrderLineList @@@@@");
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
		System.out.println("@@@@@ Exiting NWCGReportsIssueIATA::getOrderLineList @@@@@");
		return result;
	}

	/**
	 * 
	 * @param env
	 * @param shipNode
	 * @return
	 */
	private Document getShipNodeDetails(YFSEnvironment env, String shipNode) {
		System.out.println("@@@@@ Entering NWCGReportsIssueIATA::getShipNodeDetails @@@@@");
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
		System.out.println("@@@@@ Exiting NWCGReportsIssueIATA::getShipNodeDetails @@@@@");
		return result;
	}

	/**
	 * 
	 * @param env
	 * @param releaseKey
	 * @return
	 */
	private String getReleaseDate(YFSEnvironment env, String releaseKey) {
		System.out.println("@@@@@ Entering NWCGReportsIssueIATA::getReleaseDate @@@@@");
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
		System.out.println("@@@@@ Exiting NWCGReportsIssueIATA::getReleaseDate @@@@@");
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
	private Document getOtherInfoAndPrepareXml(YFSEnvironment env, Document orderDtls, Document orderLineDtls, String releaseDate) {
		System.out.println("@@@@@ Entering NWCGReportsIssueIATA::getOtherInfoAndPrepareXml @@@@@");
		try {
			Hashtable orderLineReq = new Hashtable();
			boolean bDeliverToAddress = false; // CR 584
			Element orderLinesRootElm = orderLineDtls.getDocumentElement();
			NodeList orderLinesNL = orderLinesRootElm.getElementsByTagName("OrderLine");
			// Element shipmentLineRootElm = shipmentLine.getDocumentElement();
			// NodeList shipmentLinesNL = shipmentLineRootElm.getElementsByTagName("ShipmentLine");
			if (orderLinesNL != null && orderLinesNL.getLength() > 0) {
				int lines = orderLinesNL.getLength();
				for (int j = 0; j < lines; j++) {
					Element orderLineElm = (Element) orderLinesNL.item(j);
					// Element shipmentLineElm = (Element)shipmentLinesNL.item(j);
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
			String strDate = reportsUtil.dateToString(new java.util.Date(), "MM-dd-yyyy");
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
			double gtot = 0;
			if (orderLineList != null && orderLineList.getLength() > 0) {
				int len = orderLineList.getLength();
				System.out.println("@@@@@ Number of order lines : " + len);
				for (int i = 0; i < len; i++) {
					System.out.println("@@@@@ Processing line : " + i);
					Element orderLine = (Element) orderLineList.item(i);
					String qtyStr = orderLine.getAttribute("ShippedQuantity");
					double qty = 0;
					if (qtyStr != null && qtyStr.length() > 0) {
						qty = Double.parseDouble(qtyStr);
					}

					Element OrderTotElem = (Element) orderLine.getElementsByTagName("LineOverallTotals").item(0);
					String UnitPrice = OrderTotElem.getAttribute("UnitPrice");
					double linetot = 0;
					double uprice = 0;
					uprice = Double.parseDouble(UnitPrice);
					linetot = qty * uprice;
					gtot = gtot + linetot;
					String LineTotal = Double.toString(linetot);
					String ConvLineTotal = StringUtil.NumFormat(LineTotal);
					OrderTotElem.setAttribute("LineTotal", ConvLineTotal);
					String ConvPrice = StringUtil.NumFormat(UnitPrice);
					OrderTotElem.setAttribute("UnitPrice", ConvPrice);

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
						NodeList componentList = itemDtls.getElementsByTagName("Component");
						if (componentList != null && componentList.getLength() > 0) {
							for (int j = 0; j < componentList.getLength(); j++) {
								Element component = (Element) componentList.item(j);
								String ComponentItemID = component.getAttribute("ComponentItemID");
								String ComponentUnitOfMeasure = component.getAttribute("ComponentUnitOfMeasure");
								String ComponentOrganizationCode = component.getAttribute("ComponentOrganizationCode");
								String ComponentKitQuantity = component.getAttribute("KitQuantity");
								dblWt = 0.0;
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
										dblComponentWeight = Double.parseDouble(componentWeight);
										dblComponentQty = Double.parseDouble(ComponentKitQuantity);
										dblTotalComponentWeight = (dblComponentWeight * dblComponentQty);
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
											dblWt = dblWt + (dblSubComponentWt * kitQty);
										}
									}
									// dblTotalItemWt = dblTotalItemWt + (dblWt * qty);
									dblTotalItemWt = dblTotalItemWt + dblWt;
								}
								dblTotalHazWt = dblTotalHazWt + dblTotalComponentWeight + dblTotalItemWt;
							}
							dblTotalHazWt = (dblTotalHazWt * qty);
						}

						NodeList hazmatline = itemDtls.getDocumentElement().getElementsByTagName("HazmatInformation");
						if (hazmatline.getLength() > 0) {
							Element hazmatElm = (Element) XMLUtil.getChildNodeByName(itemDtls.getDocumentElement(), "HazmatInformation");
							// CR917/JB10 Hazmat text format change -- moved this "if condition" to first
							if (hazmatElm.hasAttribute(NWCGConstants.IATA_UNNUMBER)) {
								UNNumber = hazmatElm.getAttribute(NWCGConstants.IATA_UNNUMBER);
								HazmatText = HazmatText + UNNumber;
							} else if (hazmatElm.hasAttribute(NWCGConstants.UNNUMBER)) {
								UNNumber = hazmatElm.getAttribute(NWCGConstants.UNNUMBER);
								HazmatText = HazmatText + UNNumber;
							}
							if (hazmatElm.hasAttribute(NWCGConstants.PROPER_SHIPPING_NAME)) {
								properShippingName = hazmatElm.getAttribute(NWCGConstants.PROPER_SHIPPING_NAME);
								HazmatText = HazmatText + " " + properShippingName;
							}
							if (hazmatElm.hasAttribute(NWCGConstants.IATA_HAZARD_CLASS)) {
								hazardClass = hazmatElm.getAttribute(NWCGConstants.IATA_HAZARD_CLASS);
								HazmatText = HazmatText + " " + hazardClass;
							} else if (hazmatElm.hasAttribute(NWCGConstants.HAZARD_CLASS)) {
								hazardClass = hazmatElm.getAttribute(NWCGConstants.HAZARD_CLASS);
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
							} else if (hazmatElm.hasAttribute(NWCGConstants.PACKING_GROUP)) {
								packingGroup = hazmatElm.getAttribute(NWCGConstants.PACKING_GROUP);
								HazmatText = HazmatText + " " + packingGroup;
							}
							// CR917/JB10 end.
							
							// if (weight != null) {
							if (uom.equals("KT")) {
								if (dblTotalHazWt == 0.00) {
									// HazmatText = HazmatText;
								} else {
									HazmatText = HazmatText + " " + df.format(dblTotalHazWt) + " " + unitWt;
								}
							} else {
								// HazmatText = HazmatText + " " + df.format(dblWt) + " " + unitWt;
								HazmatText = HazmatText + " " + df.format(wt) + " " + unitWt;
							}
							// }
						}
						// hazmatline.getLength()
						System.out.println("@@@@@ HazmatText: " + HazmatText);
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

			// Setting Order Header Notes CR 563 - resolve Note issue
			NodeList listOfNotes = orderDtls.getElementsByTagName("Note");
			String NoteText = "";
			String returnSuffix = "\r\n"; 
			// carriage return & line feed strings to be removed
			for (int n = 0; n < listOfNotes.getLength(); n++) {
				Element curNote = (Element) listOfNotes.item(n);
				String noteString = curNote.getAttribute("NoteText");
				int suffixIndex = noteString.lastIndexOf(returnSuffix);
				while (suffixIndex != -1) {
					noteString = noteString.substring(0, suffixIndex) + " " + noteString.substring(suffixIndex + 2, noteString.length());
					suffixIndex = noteString.lastIndexOf(returnSuffix); 
					// check again for CR and LF
				}

				if (n == 0) { // first one
					NoteText = noteString;
				} else {
					NoteText = NoteText + " " + noteString;
				}
				System.out.println("@@@@@ NoteText_" + n + "=[" + NoteText + "]");
			}

			Element OrdDtlsElm = (Element) orderDtls.getElementsByTagName("Order").item(0);
			OrdDtlsElm.setAttribute("NoteText", NoteText);
			String GTotal = Double.toString(gtot);
			String ConvGTotal = StringUtil.NumFormat(GTotal);
			Element OrderTotElem = (Element) orderDtls.getElementsByTagName("OverallTotals").item(0);
			OrderTotElem.setAttribute("GrandTotal", ConvGTotal);
			// End Setting Order Header Notes
			String documentType = OrdDtlsElm.getAttribute(NWCGConstants.DOCUMENT_TYPE);
			// Setting Ship To Address Default is Issue "Deliver To". If "Deliver To" is NULL, then "Ship To" will be set.
			String deliverToAddr1 = "";
			Element DeliverAddrElm = (Element) orderDtls.getElementsByTagName("AdditionalAddress").item(0);
			Element ShipToAddrElm = (Element) orderDtls.getElementsByTagName("PersonInfoShipTo").item(0);
			Element DeliverToAddrElm = null;
			Element eleExtn = (Element) XMLUtil.getChildNodeByName(orderDtlsRootElm, "Extn");
			String strExtnNavInfo = eleExtn.getAttribute("ExtnNavInfo");
			if (DeliverAddrElm != null) {
				DeliverToAddrElm = (Element) DeliverAddrElm.getElementsByTagName("PersonInfo").item(0);

				if (DeliverToAddrElm != null)
					deliverToAddr1 = DeliverToAddrElm.getAttribute("AddressLine1");
			}
			Element toAddrElem = orderDtls.createElement("ShipToAddress");
			orderDtlsRootElm.appendChild(toAddrElem);
			String AddrArray[] = new String[25];
			// Incident Issue
			if (documentType.equals("0001")) {
				if (strExtnNavInfo.equalsIgnoreCase(NWCGConstants.SHIPPING_INSTRUCTIONS)) {
					String ShippingInsStr = eleExtn.getAttribute(NWCGConstants.EXTN_SHIPPING_INSTRUCTIONS_ATTR);
					AddrArray = CommonUtilities.getAddressLines(ShippingInsStr);
					int AddrArrayLen = AddrArray.length;
					String city = eleExtn.getAttribute(NWCGConstants.EXTN_SHIPPING_INSTR_CITY_ATTR);
					String state = eleExtn.getAttribute(NWCGConstants.EXTN_SHIPPING_INSTR_STATE_ATTR);
					if (AddrArrayLen > 0)
						toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_1, AddrArray[0]);
					if (AddrArrayLen > 1)
						toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_2, AddrArray[1]);
					if (AddrArrayLen > 2)
						toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_3, AddrArray[2]);
					if (AddrArrayLen > 3)
						toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_4, AddrArray[3]);
					if (AddrArrayLen > 4)
						toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_5, AddrArray[4]);
					if (AddrArrayLen > 5)
						toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_6, AddrArray[5]);
					toAddrElem.setAttribute(NWCGConstants.CITY, city);
					toAddrElem.setAttribute(NWCGConstants.STATE, state);
				} else if (strExtnNavInfo.equalsIgnoreCase(NWCGConstants.WILL_PICK_UP)) {
					String addrLine1 = eleExtn.getAttribute(NWCGConstants.EXTN_WILL_PICK_UP_NAME);
					String addrLine2 = eleExtn.getAttribute(NWCGConstants.EXTN_WILL_PICK_UP_INFO);
					toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_1, addrLine1);
					toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_2, addrLine2);

				} else {
					XMLUtil.copyElement(orderDtls, ShipToAddrElm, toAddrElem);
				}
			} else { 
				// Other Doc Types - Other Issue
				if (deliverToAddr1.length() > 1) {
					XMLUtil.copyElement(orderDtls, DeliverToAddrElm, toAddrElem);

					// Top of CR 584 - when "Deliver To" is set to "Ship To" field, blank out the ExtnCustomerName field
					Element OrderExtnElm = (Element) orderDtls.getElementsByTagName("Extn").item(0);
					if (OrderExtnElm != null) {
						OrderExtnElm.setAttribute("ExtnCustomerName", "");
						bDeliverToAddress = true;
					}
					// Bottom of CR 584 - blank out the ExtnCustomerName field

				} else {
					XMLUtil.copyElement(orderDtls, ShipToAddrElm, toAddrElem);
				}
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
				String addrLine3 = corporateOrgElm.getAttribute(NWCGConstants.ADDRESS_LINE_3);
				String addrLine4 = corporateOrgElm.getAttribute(NWCGConstants.ADDRESS_LINE_4);
				String addrLine5 = corporateOrgElm.getAttribute(NWCGConstants.ADDRESS_LINE_5);
				String city = corporateOrgElm.getAttribute(NWCGConstants.CITY);
				String state = corporateOrgElm.getAttribute(NWCGConstants.STATE);
				String zip = corporateOrgElm.getAttribute(NWCGConstants.ZIP_CODE);
				String dayPhone = corporateOrgElm.getAttribute(NWCGConstants.DAY_PHONE);
				String mobilePhone = corporateOrgElm.getAttribute(NWCGConstants.MOBILE_PHONE);
				String httpUrl = corporateOrgElm.getAttribute(NWCGConstants.HTTP_URL);
				if (bDeliverToAddress) { 
					// CR 584
					httpUrl = ""; // blank out this field when DeliverTo address is used in Ship To fields
					Element personInfoShipToElem = (Element) orderDtls.getElementsByTagName("PersonInfoShipTo").item(0);
					personInfoShipToElem.setAttribute(NWCGConstants.HTTP_URL, ""); // blank out
				}

				Element issueNodeElm = orderDtls.createElement("ShipNode");
				issueNodeElm.setAttribute(NWCGConstants.ORGANIZATION_NAME, orgName);
				issueNodeElm.setAttribute(NWCGConstants.ADDRESS_LINE_1, addrLine1);
				issueNodeElm.setAttribute(NWCGConstants.ADDRESS_LINE_2, addrLine2);
				issueNodeElm.setAttribute(NWCGConstants.ADDRESS_LINE_3, addrLine3);
				issueNodeElm.setAttribute(NWCGConstants.ADDRESS_LINE_4, addrLine4);
				issueNodeElm.setAttribute(NWCGConstants.ADDRESS_LINE_5, addrLine5);
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
			
			// We are deleting all the order lines and then adding the order lines again in the hazmat order. If there are hazmat items, then we are adding them first.
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
			// Element orderDtlsExtnElm = XMLUtil.getFirstElementByName(orderDtlsRootElm, "Extn");
			// String incidentNo = orderDtlsExtnElm.getAttribute(NWCGConstants.INCIDENT_NO);
			// String incidentYear = orderDtlsExtnElm.getAttribute("ExtnIncidentYear");
			// if (incidentNo != null && incidentNo.length() > 0){
			// Document incidentDtls = getIncidentDetails(env, incidentNo);
			// incidentYear =
			// incidentDtls.getDocumentElement().getAttribute(NWCGConstants.YEAR);
			// }
			// orderDtlsRootElm.setAttribute(NWCGConstants.YEAR, incidentYear);
			System.out.println("@@@@@ After adjust lines : " + XMLUtil.getXMLString(orderDtls));
		} catch (ParserConfigurationException pce) {
			System.out.println("!!!!! ParserConfigurationException Msg : " + pce.getMessage());
			System.out.println("!!!!! StackTrace : " + pce.getStackTrace());
		} catch (Exception e) {
			System.out.println("!!!!! Exception Msg : " + e.getMessage());
			System.out.println("!!!!! StackTrace : " + e.getStackTrace());
		}
		System.out.println("@@@@@ Exiting NWCGReportsIssueIATA::getOtherInfoAndPrepareXml @@@@@");
		return orderDtls;
	}

	/**
	 * 
	 * @param env
	 * @param shipmentKey
	 * @return
	 */
	public Document getOrderHeaderKey(YFSEnvironment env, String shipmentKey) {
		System.out.println("@@@@@ Entering NWCGReportsIssueIATA::getOrderHeaderKey @@@@@");
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
		System.out.println("@@@@@ Exiting NWCGReportsIssueIATA::getOrderHeaderKey @@@@@");
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
		System.out.println("@@@@@ Entering NWCGReportsIssueIATA::getItemDetails @@@@@");
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
		System.out.println("@@@@@ Exiting NWCGReportsIssueIATA::getItemDetails @@@@@");
		return itemDtls;
	}

	/**
	 * 
	 * @param env
	 * @param incidentNo
	 * @return
	 */
	private Document getIncidentDetails(YFSEnvironment env, String incidentNo) {
		System.out.println("@@@@@ Entering NWCGReportsIssueIATA::getIncidentDetails @@@@@");
		Document incidentDtls = null;
		try {
			incidentDtls = XMLUtil.getDocument();
			Document getIncidentOrderInput = XMLUtil.createDocument("NWCGIncidentOrder");
			getIncidentOrderInput.getDocumentElement().setAttribute("IncidentNo", incidentNo);
			incidentDtls = CommonUtilities.invokeService(env, NWCGConstants.NWCG_GET_INCIDENT_ORDER_SERVICE, getIncidentOrderInput);
		} catch (ParserConfigurationException pce) {
			System.out.println("!!!!! ParserConfigurationException Msg : " + pce.getMessage());
			System.out.println("!!!!! StackTrace : " + pce.getStackTrace());
		} catch (Exception e) {
			System.out.println("!!!!! Exception Msg : " + e.getMessage());
			System.out.println("!!!!! StackTrace : " + e.getStackTrace());
		}
		System.out.println("@@@@@ Exiting NWCGReportsIssueIATA::getIncidentDetails @@@@@");
		return incidentDtls;
	}
}