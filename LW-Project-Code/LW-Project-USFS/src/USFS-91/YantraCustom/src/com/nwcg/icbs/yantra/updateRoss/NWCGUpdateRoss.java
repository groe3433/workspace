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

package com.nwcg.icbs.yantra.updateRoss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import com.yantra.yfc.log.YFCLogCategory;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.issue.NWCGUpdateNFESResReq;
import com.nwcg.icbs.yantra.api.issue.util.NWCGNFESResourceRequest;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

/**
 * @author mlathom ML - Send Update Message to ROSS
 */
public class NWCGUpdateRoss implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGUpdateRoss.class);

	String orderNo;
	String orderHdrKey;
	String shipNode;
	String incNo;
	String incYr;
	String shippingContactName;
	String shippingContactPhone;
	Hashtable<String, String> htReqNo2ItemID = new Hashtable<String, String>();
	Element elmOrderExtn;
	Element elmPersonInfoShipTo;

	public void setProperties(Properties props) throws Exception {
	}

	/**
	 * This method will check for 'Create' or 'Update' requests. If the request
	 * type is create request, then it will make getOrderDetails with
	 * SCHEDULE.ON_SUCCESS.xml template and then it will call
	 * NWCGSendICBSRInitiatedRequestToROSSService. If the request type is update
	 * request, then it will call getOrderLineList for that request number. It
	 * will build the update request and will trigger ROSS
	 * 
	 * @param env
	 * @param inDoc
	 * @return
	 * @throws Exception
	 */
	public Document sendUpdateToRoss(YFSEnvironment env, Document inDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGUpdateRoss::sendUpdateToRoss ");
		logger.verbose("@@@@@ inDoc " + XMLUtil.extractStringFromDocument(inDoc));
		String strMessageType = NWCGConstants.EMPTY_STRING;
		Element inDocRootElm = inDoc.getDocumentElement();
		/*
		 * From here, create two paths to follow to create the XMLS needed to
		 * Update Ross, dependant upon the value of RossInfo/MessageType. Then,
		 * send the completed message to ROSS
		 */
		// Get the message type Passed in
		strMessageType = inDocRootElm.getAttribute(NWCGConstants.MESSAGE_TYPE);
		orderHdrKey = inDocRootElm.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		orderNo = inDocRootElm.getAttribute(NWCGConstants.ORDER_NO);
		if (strMessageType != null) {
			if (strMessageType.equals("CreateRequest")) {
				// call on buildCreateRequestDocument
				Document docServiceInput = buildCreateRequestDocument(env);
				// call NWCGSendICBSRInitiatedRequestToROSSService
				CommonUtilities.invokeService(env, "NWCGSendICBSRInitiatedRequestToROSSService", docServiceInput);
			} else if (strMessageType.equals("UpdateRequest")) {
				// call on buildUpdateRequstDocument then call new update ROSS service
				sendUpdateRequestDocument(env, inDoc);
			} else {
				// Throw an error,as the MessageType is invalid
				logger.error("!!!!! Invalid Update ROSS Message Type!");
				throw new NWCGException("NWCG_UPDATE_ROSS_MSG_INVALID", new Object[] {});
			}
		}
		logger.verbose("@@@@@ Exiting NWCGUpdateRoss::sendUpdateToRoss ");
		return inDoc;
	}

	/**
	 * 
	 * @param env
	 * @return
	 * @throws Exception
	 */
	private Document buildCreateRequestDocument(YFSEnvironment env) throws Exception {
		logger.verbose("@@@@@ Entering NWCGUpdateRoss::buildCreateRequestDocument ");
		Document docGetOrderDetailsInput = XMLUtil.createDocument(NWCGConstants.ORDER);
		Element elemGetOrderDetails = docGetOrderDetailsInput.getDocumentElement();
		elemGetOrderDetails.setAttribute(NWCGConstants.ENTERPRISE_CODE_STR, NWCGConstants.NWCG_ENTERPRISE_CODE);
		elemGetOrderDetails.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHdrKey);
		Document docGetOrderDetailsOutput = CommonUtilities.invokeAPI(env, "NWCGUpdtROSS_CreateReq_getOrderDtls", NWCGConstants.API_GET_ORDER_DETAILS, docGetOrderDetailsInput);
		logger.verbose("@@@@@ Exiting NWCGUpdateRoss::buildCreateRequestDocument ");
		return docGetOrderDetailsOutput;
	}

	private Document sendUpdateRequestDocument(YFSEnvironment env, Document inDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGUpdateRoss::sendUpdateRequestDocument ");
		Element elmOrder = inDoc.getDocumentElement();
		String orderNo = elmOrder.getAttribute(NWCGConstants.ORDER_NO);
		String reqNo = elmOrder.getAttribute("strReqNo");
		String orderHdrKey = elmOrder.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		Element elmExtn = (Element) elmOrder.getElementsByTagName("Extn").item(0);
		String incNo = elmExtn.getAttribute(NWCGConstants.INCIDENT_NO);
		String incYr = elmExtn.getAttribute(NWCGConstants.INCIDENT_YEAR);
		Document docOLList = getOLsForIssueAndReqNo(env, orderNo, reqNo, incNo, incYr);
		if (docOLList != null) {
			Element elmDocOLList = docOLList.getDocumentElement();
			int noOfOLs = elmDocOLList.getElementsByTagName(NWCGConstants.ORDER_LINE).getLength();
			Hashtable<String, Vector<Element>> htBaseReq2OLs = getValidLinesForBaseReq(env, docOLList);
			Hashtable<String, Element> htOLKey2ShipmentLine = new Hashtable<String, Element>();
			Document docShipmentDtls = getShipmentDtls(env, orderHdrKey);
			if ((docShipmentDtls != null) && (docShipmentDtls.getDocumentElement().getElementsByTagName("Shipment").getLength() > 0)) {
				htOLKey2ShipmentLine = getShipmentLines(docShipmentDtls);
			}
			buildAndSendUpdtReq2ROSS(env, htBaseReq2OLs, htOLKey2ShipmentLine);
		}
		logger.verbose("@@@@@ Exiting NWCGUpdateRoss::sendUpdateRequestDocument ");
		return inDoc;
	}

	private Document getOLsForIssueAndReqNo(YFSEnvironment env, String orderNo, String reqNo, String incNo, String incYr) throws Exception {
		logger.verbose("@@@@@ Entering NWCGUpdateRoss::getOLsForIssueAndReqNo ");
		Document docGetOLListOP = null;
		try {
			if (reqNo.indexOf(".") != -1) {
				int indexOfDot = reqNo.indexOf(".");
				reqNo = reqNo.substring(0, indexOfDot);
			}
			Document docGetOLListIP = XMLUtil.createDocument(NWCGConstants.ORDER_LINE);
			Element elmGetOL = docGetOLListIP.getDocumentElement();
			Element elmOLExtn = docGetOLListIP.createElement(NWCGConstants.EXTN_ELEMENT);
			elmGetOL.appendChild(elmOLExtn);
			elmOLExtn.setAttribute("ExtnRequestNoQryType", "FLIKE");
			elmOLExtn.setAttribute(NWCGConstants.EXTN_REQUEST_NO, reqNo);
			Element elmOrder = docGetOLListIP.createElement(NWCGConstants.ORDER_ELM);
			elmGetOL.appendChild(elmOrder);
			elmOrder.setAttribute(NWCGConstants.ORDER_NO, orderNo);
			Element elmOrderExtn = docGetOLListIP.createElement(NWCGConstants.EXTN_ELEMENT);
			elmOrder.appendChild(elmOrderExtn);
			elmOrderExtn.setAttribute(NWCGConstants.INCIDENT_NO, incNo);
			elmOrderExtn.setAttribute(NWCGConstants.INCIDENT_YEAR, incYr);
			docGetOLListOP = CommonUtilities.invokeAPI(env, "NWCGStatusNFESResourceRequestHandler_getOrderLineList", NWCGConstants.API_GET_ORDER_LINE_LIST, docGetOLListIP);
			NodeList nlOLOP = docGetOLListOP.getDocumentElement().getElementsByTagName(NWCGConstants.ORDER_LINE);
			for (int i = 0; i < nlOLOP.getLength(); i++) {
				Element elmOL = (Element) nlOLOP.item(i);
				String reqNoFromOP = ((Element) elmOL.getElementsByTagName("Extn").item(0)).getAttribute(NWCGConstants.EXTN_REQUEST_NO);
				if ((reqNoFromOP.length() > reqNo.length()) && (reqNoFromOP.charAt(reqNo.length()) != '.')) {
					elmOL.getParentNode().removeChild(nlOLOP.item(i));
				}
			}
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! NWCGUpdateRoss::getItemIDForReqNo, " + "Parser Configuration Exception : " + pce.getMessage());
		} catch (Exception e) {
			logger.error("!!!!! NWCGUpdateRoss::getItemIDForReqNo, " + "Exception : " + e.getMessage());
		}
		logger.verbose("@@@@@ Exiting NWCGUpdateRoss::getOLsForIssueAndReqNo ");
		return docGetOLListOP;
	}

	/**
	 * This method will get the hashtable with orderline key to shipment line
	 * 
	 * @param docConfirmShipment
	 * @return
	 */
	private Hashtable<String, Element> getShipmentLines(Document docConfirmShipment) {
		logger.verbose("@@@@@ Entering NWCGUpdateRoss::getShipmentLines ");
		Hashtable<String, Element> htOLKey2ShipmentLine = new Hashtable<String, Element>();
		Element elmConfirmShipment = docConfirmShipment.getDocumentElement();
		Element elmExtnShipment = (Element) elmConfirmShipment.getElementsByTagName("Extn").item(0);
		String edd = elmExtnShipment.getAttribute(NWCGConstants.EXTN_ESTIMATED_DEPART_DATE);
		String ead = elmExtnShipment.getAttribute(NWCGConstants.EXTN_ESTIMATED_ARRIVAL_DATE);
		String actualShipmentDate = elmConfirmShipment.getAttribute(NWCGConstants.ACTUAL_SHIPMENT_DATE);
		if (edd == null || edd.trim().length() < 2) {
			edd = actualShipmentDate;
		}
		if (ead == null || ead.trim().length() < 2) {
			ead = actualShipmentDate;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		try {
			Date dateEAD = sdf.parse(ead);
			Date dateEDD = sdf.parse(edd);
			if (dateEDD.after(dateEAD)) {
				logger.verbose("@@@@@ NWCGUpdateNFESResReq::getShipmentLines, Changing the EAD");
				ead = edd;
			}
		} catch (ParseException e) {
			logger.error("!!!!! NWCGUpdateRoss::getShipmentLines, " + "Failed while converting EAD and EDD dates : " + e.getMessage());
			logger.error("!!!!! Caught ParseException Exception : " + e);
		}
		NodeList nlShipmentLines = elmConfirmShipment.getElementsByTagName("ShipmentLine");
		// WE ARE NOT GETTING QUANTITY IN THE EXTENDED INPUT IN SHIPMENT LINE
		for (int i = 0; i < nlShipmentLines.getLength(); i++) {
			Element elmShipmentLine = (Element) nlShipmentLines.item(i);
			elmShipmentLine.setAttribute(NWCGConstants.EXTN_ESTIMATED_DEPART_DATE, edd);
			elmShipmentLine.setAttribute(NWCGConstants.EXTN_ESTIMATED_ARRIVAL_DATE, ead);
			htOLKey2ShipmentLine.put(elmShipmentLine.getAttribute(NWCGConstants.ORDER_LINE_KEY), elmShipmentLine);
		}
		logger.verbose("@@@@@ Exiting NWCGUpdateRoss::getShipmentLines ");
		return htOLKey2ShipmentLine;
	}

	private Document getShipmentDtls(YFSEnvironment env, String orderHdrKey) {
		logger.verbose("@@@@@ Entering NWCGUpdateRoss::getShipmentDtls ");
		Document docShipmentDtls = null;
		try {
			Document docOrderIP = XMLUtil.createDocument(NWCGConstants.ORDER);
			Element elmOrderIP = docOrderIP.getDocumentElement();
			elmOrderIP.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHdrKey);
			docShipmentDtls = CommonUtilities.invokeAPI(env, "NWCGUpdateRoss_ConfirmShipmentOnSuccess", NWCGConstants.API_GET_SHIPMENT_LIST_FOR_ORDER, docOrderIP);
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! NWCGUpdateRoss::getShipmentDtls, " + "Parser Configuration Exception : " + pce.getMessage());
		} catch (Exception e) {
			logger.error("!!!!! NWCGUpdateRoss::getShipmentDtls, " + "Exception : " + e.getMessage());
		}
		logger.verbose("@@@@@ Exiting NWCGUpdateRoss::getShipmentDtls ");
		return docShipmentDtls;
	}

	/**
	 * This method will get all the valid lines that we need to pass to ROSS
	 * Send the line to ROSS for all the lines except 'Cancelled Due to
	 * Consolidation', 'Cancelled Due to Substitution' and 'UTF'. If it is a
	 * partial UTF line, then add the line to the vector. Determining partial
	 * UTF line: If the current line is in UTF status and if there is a parent
	 * line with the same item id in the current issue, then it is a partial
	 * substitution scenario
	 * 
	 * @param docOrderDtls
	 * @return
	 */
	private Hashtable<String, Vector<Element>> getValidLinesForBaseReq(YFSEnvironment env, Document docOrderLineList) {
		logger.verbose("@@@@@ Entering NWCGUpdateRoss::getValidLinesForBaseReq ");
		NWCGUpdateNFESResReq updtNFESResReq = new NWCGUpdateNFESResReq();
		Hashtable<String, Vector<Element>> htBaseReq2OL = new Hashtable<String, Vector<Element>>();
		Element elmOLList = docOrderLineList.getDocumentElement();
		Node nodeOrder = elmOLList.getElementsByTagName(NWCGConstants.ORDER).item(0);
		String orderCreateTS = ((Element) nodeOrder).getAttribute("Createts");
		NodeList nlOrder = nodeOrder.getChildNodes();
		for (int i = 0; i < nlOrder.getLength(); i++) {
			Node tmpNode = nlOrder.item(i);
			if (tmpNode.getNodeType() == Node.ELEMENT_NODE) {
				Element elmOrderChild = (Element) tmpNode;
				if (elmOrderChild.getNodeName().equalsIgnoreCase("Extn")) {
					elmOrderExtn = elmOrderChild;
					incNo = elmOrderChild.getAttribute(NWCGConstants.INCIDENT_NO);
					incYr = elmOrderChild.getAttribute(NWCGConstants.INCIDENT_YEAR);
					shippingContactName = elmOrderChild.getAttribute(NWCGConstants.SHIP_CONTACT_NAME_ATTR);
					shippingContactPhone = elmOrderChild.getAttribute(NWCGConstants.SHIP_CONTACT_PHONE_ATTR);
				} else if (elmOrderChild.getNodeName().equalsIgnoreCase("PersonInfoShipTo")) {
					elmPersonInfoShipTo = elmOrderChild;
				}
			}
		}
		// Get OrderLine for a base request no
		NodeList nlOrderLines = elmOLList.getElementsByTagName("OrderLine");
		// Hashtable<String, String> htReqNo2ItemID = new Hashtable<String, String>();
		Vector<String> vecItems = new Vector<String>();
		for (int i = 0; i < nlOrderLines.getLength(); i++) {
			Element orderLine = (Element) nlOrderLines.item(i);
			Element elmExtn = (Element) orderLine.getElementsByTagName("Extn").item(0);
			String reqNo = elmExtn.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
			Element elmItem = (Element) orderLine.getElementsByTagName("Item").item(0);
			String itemID = elmItem.getAttribute(NWCGConstants.ITEM_ID);
			htReqNo2ItemID.put(reqNo, itemID);
			if (!vecItems.contains(itemID)) {
				vecItems.add(itemID);
			}
		}
		Hashtable<String, String> htItem2PublishStatus = updtNFESResReq.getItemPublishStatus(env, vecItems);
		for (int i = 0; i < nlOrderLines.getLength(); i++) {
			String baseReqNo = NWCGConstants.EMPTY_STRING;
			Element orderLine = (Element) nlOrderLines.item(i);
			shipNode = orderLine.getAttribute(NWCGConstants.SHIP_NODE);
			String reqNo = NWCGConstants.EMPTY_STRING;
			NodeList nlExtnOL = orderLine.getElementsByTagName("Extn");
			Element elmOLExtn = null;
			if (nlExtnOL != null && nlExtnOL.getLength() > 0) {
				elmOLExtn = (Element) nlExtnOL.item(0);
				reqNo = elmOLExtn.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
				if (reqNo.indexOf(".") != -1) {
					baseReqNo = reqNo.substring(0, reqNo.indexOf("."));
				} else {
					baseReqNo = reqNo;
				}
			}
			// If this is non ROSS published item, then do not add the item in the list
			String itemID = htReqNo2ItemID.get(reqNo);
			String publishStatus = htItem2PublishStatus.get(itemID);
			if (!publishStatus.equalsIgnoreCase("Y")) {
				continue;
			}
			boolean addLine = false;
			// getOrderLineList is not giving MaxLineStatus or MinLineStatus for UTF, so deriving it based on ExtnOrigReqQty and ExtnUTFQty For UTF, conditionVariable2 value is empty or null
			String olStatus = orderLine.getAttribute(NWCGConstants.MAX_LINE_STATUS);
			String condVar2 = orderLine.getAttribute(NWCGConstants.CONDITION_VAR2);
			if (((olStatus == null) || (olStatus.trim().length() < 2)) && (condVar2 == null || condVar2.trim().length() < 1)) {
				olStatus = orderLine.getAttribute("MinLineStatus");
				if ((olStatus == null) || (olStatus.trim().length() < 2)) {
					if (elmOLExtn != null) {
						String reqQty = elmOLExtn.getAttribute(NWCGConstants.ORIGINAL_REQUESTED_QTY);
						String utfQty = elmOLExtn.getAttribute(NWCGConstants.UTF_QTY);
						if ((reqQty != null && reqQty.trim().length() > 0) && (utfQty != null && utfQty.trim().length() > 0)) {
							if (reqQty.indexOf(".") != -1) {
								reqQty = reqQty.substring(0, reqQty.indexOf("."));
							}
							if (utfQty.indexOf(".") != -1) {
								utfQty = utfQty.substring(0, utfQty.indexOf("."));
							}
							if (reqQty.equals(utfQty)) {
								addLine = true;
							}
						}
					}
				}
			}
			if (olStatus.equalsIgnoreCase(NWCGConstants.STATUS_UTF)) {
				addLine = true;
			} else if (olStatus.equalsIgnoreCase(NWCGConstants.STATUS_CANCELLED_DUE_TO_CONS) || olStatus.equalsIgnoreCase(NWCGConstants.STATUS_CANCELLED_DUE_TO_SUBS) || olStatus.equalsIgnoreCase(NWCGConstants.STATUS_CANCELLED)) {
				addLine = false;
			} else {
				// if this is not already dealt as part of UTF above where we are setting addLine to true
				if (!addLine) {
					if (olStatus.indexOf(".") != -1) {
						olStatus = olStatus.substring(0, olStatus.indexOf("."));
					}
					if (olStatus.compareTo("3700") >= 0) {
						addLine = true;
					} else {
						addLine = false;
					}
				}
			}
			// If we need to send this line to ROSS, then add it to hashtable
			if (addLine) {
				orderLine.setAttribute(NWCGConstants.ORDER_NO, orderNo);
				if (orderCreateTS == null) {
					orderCreateTS = NWCGConstants.EMPTY_STRING;
				}
				orderLine.setAttribute("OrderCreatets", orderCreateTS);
				if (htBaseReq2OL.containsKey(baseReqNo)) {
					Vector<Element> vecOLElmsForABaseReq = htBaseReq2OL.get(baseReqNo);
					vecOLElmsForABaseReq.add(orderLine);
				} else {
					Vector<Element> vecOLElm = new Vector<Element>();
					vecOLElm.add(orderLine);
					htBaseReq2OL.put(baseReqNo, vecOLElm);
				}
			}
		}
		logger.verbose("@@@@@ Exiting NWCGUpdateRoss::getValidLinesForBaseReq ");
		return htBaseReq2OL;
	}

	/**
	 * This method will send a message to ROSS for each base request no
	 * 
	 * @param env
	 * @param htBaseReq2OLs
	 * @param htOLKey2ShipmentLine
	 */
	private void buildAndSendUpdtReq2ROSS(YFSEnvironment env, Hashtable<String, Vector<Element>> htBaseReq2OLs, Hashtable<String, Element> htOLKey2ShipmentLine) {
		logger.verbose("@@@@@ Entering NWCGUpdateRoss::buildAndSendUpdtReq2ROSS ");
		Enumeration<Vector<Element>> e = htBaseReq2OLs.elements();
		while (e.hasMoreElements()) {
			NWCGNFESResourceRequest nfesResReq = new NWCGNFESResourceRequest("ro:UpdateNFESResourceRequestReq");
			nfesResReq.setDocAttributes(env.getUserId(), orderHdrKey, "Order", orderNo, null);
			nfesResReq.setMessageOriginator(shipNode);
			nfesResReq.setRequestKey(incNo, incYr, NWCGConstants.EMPTY_STRING);
			boolean updtReqNo = false;
			String reqNo = NWCGConstants.EMPTY_STRING;
			String baseReqNo = NWCGConstants.EMPTY_STRING;
			Vector<Element> olsForABaseReq = e.nextElement();
			for (int i = 0; i < olsForABaseReq.size(); i++) {
				Element elmOL = olsForABaseReq.elementAt(i);
				if (!updtReqNo) {
					updtReqNo = true;
					baseReqNo = ((Element) elmOL.getElementsByTagName("Extn").item(0)).getAttribute(NWCGConstants.EXTN_BASE_REQUEST_NO);
					nfesResReq.updateRequestNo(baseReqNo);
				}
				String olKey = elmOL.getAttribute(NWCGConstants.ORDER_LINE_KEY);
				Element elmShipmentOL = htOLKey2ShipmentLine.get(olKey);
				nfesResReq.populateFillDtlOrConsolidationDtl(elmOL, elmShipmentOL, htReqNo2ItemID);
			}
			nfesResReq.populateAddressDtls(elmOrderExtn, elmPersonInfoShipTo);
			nfesResReq.populateShippingContactDtls(shippingContactName, shippingContactPhone);
			nfesResReq.populateSpecialNeeds();
			try {
				logger.verbose("@@@@@ NWCGUpdateRoss::buildAndSendUpdtReq2ROSS, Input XML to " + NWCGConstants.SVC_POST_OB_MSG_SVC + ", " + XMLUtil.extractStringFromDocument(nfesResReq.getNFESResourceRequestDocument()));
				CommonUtilities.invokeService(env, NWCGConstants.SVC_POST_OB_MSG_SVC, XMLUtil.extractStringFromDocument(nfesResReq.getNFESResourceRequestDocument()));
			} catch (Exception ex) {
				logger.error("!!!!! NWCGUpdateRoss::buildAndSendUpdtReq2ROSS, " + "Exception for Base Req No " + reqNo + " : " + ex.getMessage());
				logger.error("!!!!! Caught General Exception : " + ex);
			}
		}
		logger.verbose("@@@@@ Exiting NWCGUpdateRoss::buildAndSendUpdtReq2ROSS ");
	}
}