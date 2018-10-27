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

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
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
public class NWCGReportsIssue implements YIFCustomApi {

	private Properties _properties;

	// GENERAL_APP_LOGGER - configured in log4jconfig.custom.xml
	//private static YFCLogCategory cat = YFCLogCategory.instance(NWCGReportsIssue.class);

	/**
	 * 
	 * @param arg0
	 * @throws Exception
	 */
	public void setProperties(Properties arg0) throws Exception {
		System.out.println("@@@@@ In NWCGReportsIssue::setProperties @@@@@");
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
		System.out.println("@@@@@ Entering NWCGReportsIssue::triggerIssueReport @@@@@");
		System.out.println("@@@@@ Input Doc : " + XMLUtil.getXMLString(inputDoc));

		String orderHdrKey = getOrderHeaderKey(env, inputDoc);
		String releaseKey = "";
		Document orderDtls = getOrderDetails(env, orderHdrKey);
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
		Element tempElm = issueDtls.getDocumentElement();

		NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
		String documentId = _properties.getProperty("PrintDocumentId");
		String printerId = _properties.getProperty("PrinterId");
		String shipNode = orderDtlsElm.getAttribute(NWCGConstants.SHIP_NODE);
		Document issueReportDoc = reportsUtil.generatePrintHeader(documentId, "xml:/Order", shipNode, printerId, enterpriseCode);

		Element issueRptElmRootNode = issueReportDoc.getDocumentElement();
		Element printDocElm = (Element) issueRptElmRootNode.getFirstChild();
		Element inputDataElm = issueReportDoc.createElement("InputData");
		Element issueDtlsElm = issueReportDoc.createElement("Order");
		XMLUtil.copyElement(issueReportDoc, tempElm, issueDtlsElm);
		inputDataElm.appendChild(issueDtlsElm);
		printDocElm.appendChild(inputDataElm);
		System.out.println("@@@@@ XML to printDocumentSet : " + XMLUtil.getXMLString(issueReportDoc));

		CommonUtilities.invokeAPI(env, NWCGConstants.API_PRINT_DOCUMENT_SET, issueReportDoc);
		System.out.println("@@@@@ Exiting NWCGReportsIssue::triggerIssueReport @@@@@");
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
	private Document getOrderDetails(YFSEnvironment env, String orderHdrKey) {
		Document result = null;
		System.out.println("@@@@@ Entering NWCGReportsIssue::getOrderDetails @@@@@");
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
		System.out.println("@@@@@ Exiting NWCGReportsIssue::getOrderDetails @@@@@");
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
		System.out.println("@@@@@ Entering NWCGReportsIssue::getOrderLineList @@@@@");
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
			System.out.println("@@@@@ getOrderLineList output: " + XMLUtil.getXMLString(result));
		} catch (ParserConfigurationException pce) {
			System.out.println("!!!!! ParserConfigurationException Msg : " + pce.getMessage());
			System.out.println("!!!!! StackTrace : " + pce.getStackTrace());
		} catch (Exception e) {
			System.out.println("!!!!! Exception Msg : " + e.getMessage());
			System.out.println("!!!!! StackTrace : " + e.getStackTrace());
		}
		System.out.println("@@@@@ Exiting NWCGReportsIssue::getOrderLineList @@@@@");
		return result;
	}

	/**
	 * 
	 * @param env
	 * @param shipNode
	 * @return
	 */
	private Document getShipNodeDetails(YFSEnvironment env, String shipNode) {
		Document result = null;
		System.out.println("@@@@@ Entering NWCGReportsIssue::getShipNodeDetails @@@@@");
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
		System.out.println("@@@@@ Entering NWCGReportsIssue::getShipNodeDetails @@@@@");
		return result;
	}

	/**
	 * 
	 * @param env
	 * @param releaseKey
	 * @return
	 */
	private String getReleaseDate(YFSEnvironment env, String releaseKey) {
		System.out.println("@@@@@ Entering NWCGReportsIssue::getReleaseDate @@@@@");
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
		System.out.println("@@@@@ Exiting NWCGReportsIssue::getReleaseDate @@@@@");
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
		System.out.println("@@@@@ Entering NWCGReportsIssue::getOtherInfoAndPrepareXml @@@@@");
		try {
			Hashtable orderLineReq = new Hashtable();
			Element orderLinesRootElm = orderLineDtls.getDocumentElement();
			NodeList orderLinesNL = orderLinesRootElm.getElementsByTagName("OrderLine");
			if (orderLinesNL != null && orderLinesNL.getLength() > 0) {
				int lines = orderLinesNL.getLength();
				for (int j = 0; j < lines; j++) {
					Element orderLineElm = (Element) orderLinesNL.item(j);
					String orderLineKey = orderLineElm.getAttribute(NWCGConstants.ORDER_LINE_KEY);
					Element extnElm = (Element) XMLUtil.getChildNodeByName(orderLineElm, "Extn");
			
					String requestNumber = extnElm.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
					String reqQty = orderLineElm.getAttribute(NWCGConstants.ORIGINAL_ORDERED_QTY);
					String issueQty = orderLineElm.getAttribute(NWCGConstants.ORDERED_QTY);
					String boQty = extnElm.getAttribute(NWCGConstants.BACKORDERED_QTY);
					
					Vector orderLineVec = new Vector();
					orderLineVec.addElement(requestNumber); // S-No - 0
					orderLineVec.addElement(reqQty); // Requested Qty - 1
					orderLineVec.addElement(issueQty); // Issue Qty - 2
					orderLineVec.addElement(boQty); // BackOrdered Qty - 3
					
					orderLineReq.put(orderLineKey, orderLineVec);
				}
			}

			Element orderDtlsRootElm = orderDtls.getDocumentElement();
			orderDtlsRootElm.setAttribute(NWCGConstants.RELEASE_TS, releaseDate);
			String enterpriseCode = orderDtlsRootElm.getAttribute(NWCGConstants.ENTERPRISE_CODE_STR);

			Vector orderLineHz = new Vector();
			Vector orderLineNonHz = new Vector();
			String shipNode = "";
			double wt = 0.0;
			double vol = 0.0;
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
					shipNode = orderLine.getAttribute(NWCGConstants.SHIP_NODE);
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

					String properShippingName = "";

					if (isHazmat.equalsIgnoreCase("Y")) {
						addRegulationDesc = true;
						Element hazmatElm = (Element) XMLUtil.getChildNodeByName(itemDtls.getDocumentElement(), "HazmatInformation");
						properShippingName = hazmatElm.getAttribute(NWCGConstants.PROPER_SHIPPING_NAME);
						orderLine.setAttribute(NWCGConstants.PROPER_SHIPPING_NAME, properShippingName);
						orderLineHz.addElement(orderLine);
					} else {
						orderLine.setAttribute(NWCGConstants.PROPER_SHIPPING_NAME, properShippingName);
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
			String totWtWithUom = (new Double(wt).toString()) + " " + unitWt;
			orderDtlsRootElm.setAttribute(NWCGConstants.TOTAL_WEIGHT, totWtWithUom);
			String totVolWithUom = (new Double(vol).toString()) + " " + unitVol;
			orderDtlsRootElm.setAttribute(NWCGConstants.TOTAL_VOLUME, totVolWithUom);
			Element orderDtlsExtnElm = XMLUtil.getFirstElementByName(orderDtlsRootElm, "Extn");
			String incidentNo = orderDtlsExtnElm.getAttribute(NWCGConstants.INCIDENT_NO);
			String incidentYear = "";
			if (incidentNo != null && incidentNo.length() > 0) {
				Document incidentDtls = getIncidentDetails(env, incidentNo);
				incidentYear = incidentDtls.getDocumentElement().getAttribute(NWCGConstants.YEAR);
			}
			orderDtlsRootElm.setAttribute(NWCGConstants.YEAR, incidentYear);
			System.out.println("@@@@@ After adjust lines : " + XMLUtil.getXMLString(orderDtls));
		} catch (ParserConfigurationException pce) {
			System.out.println("!!!!! ParserConfigurationException Msg : " + pce.getMessage());
			System.out.println("!!!!! StackTrace : " + pce.getStackTrace());
		} catch (Exception e) {
			System.out.println("!!!!! Exception Msg : " + e.getMessage());
			System.out.println("!!!!! StackTrace : " + e.getStackTrace());
		}
		System.out.println("@@@@@ Exiting NWCGReportsIssue::getOtherInfoAndPrepareXml @@@@@");
		return orderDtls;
	}

	/**
	 * 
	 * @param env
	 * @param inputDoc
	 * @return
	 */
	private String getOrderHeaderKey(YFSEnvironment env, Document inputDoc) {
		System.out.println("@@@@@ Entering NWCGReportsIssue::getOrderHeaderKey @@@@@");
		String orderHdrKey = "";
		try {
			Document taskDtls = CommonUtilities.invokeAPI(env, "NWCGReports_getTaskDetails", NWCGConstants.API_GET_TASK_DETAILS, inputDoc);
			System.out.println("@@@@@ Output from getTaskDetails : " + XMLUtil.getXMLString(taskDtls));
			Element taskDtlsElm = taskDtls.getDocumentElement();
			Element taskRefElm = XMLUtil.getFirstElementByName(taskDtlsElm, "TaskReferences");
			orderHdrKey = taskRefElm.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
			System.out.println("@@@@@ OrderHeaderKey : " + orderHdrKey);
		} catch (Exception e) {
			System.out.println("!!!!! Exception Msg : " + e.getMessage());
			System.out.println("!!!!! StackTrace : " + e.getStackTrace());
		}
		System.out.println("@@@@@ Exiting NWCGReportsIssue::getOrderHeaderKey @@@@@");
		return orderHdrKey;
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
		System.out.println("@@@@@ Entering NWCGReportsIssue::getItemDetails @@@@@");
		Document itemDtls = null;
		try {
			Document itemDtlsInputDoc = XMLUtil.getDocument();
			Element itemElm = itemDtlsInputDoc.createElement("Item");
			// itemElm.setAttribute(NWCGConstants.ENTERPRISE_CODE_STR, enterpriseCode);
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
		System.out.println("@@@@@ Exiting NWCGReportsIssue::getItemDetails @@@@@");
		return itemDtls;
	}

	/**
	 * 
	 * @param env
	 * @param incidentNo
	 * @return
	 */
	private Document getIncidentDetails(YFSEnvironment env, String incidentNo) {
		System.out.println("@@@@@ Entering NWCGReportsIssue::getIncidentDetails @@@@@");
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
		System.out.println("@@@@@ Exiting NWCGReportsIssue::getIncidentDetails @@@@@");
		return incidentDtls;
	}
}