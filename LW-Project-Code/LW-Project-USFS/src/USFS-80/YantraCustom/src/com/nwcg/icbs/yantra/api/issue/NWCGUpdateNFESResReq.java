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

package com.nwcg.icbs.yantra.api.issue;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.nwcg.icbs.yantra.api.issue.util.NWCGNFESResourceRequest;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * 
 * @author Oxford Consulting Group
 * @version 1.0
 * @date November 6, 2013
 */
public class NWCGUpdateNFESResReq implements YIFCustomApi {

	String incNo;

	String incYr;

	String orderNo;

	String orderHdrKey;

	Element elmOrderExtn;

	Element elmPersonInfoShipTo;

	Hashtable<String, String> htAddrInfo;

	String shippingContactName;

	String shippingContactPhone;

	boolean populatedBaseReqNo;

	String shipNode;

	Hashtable<String, String> htReqNo2ItemID = new Hashtable<String, String>();

	private Properties myProperties = null;

	private static Logger logger = Logger.getLogger();

	/**
	 * 
	 * @param arg0
	 * @throws Exception
	 */
	public void setProperties(Properties arg0) throws Exception {
		this.myProperties = arg0;
	}

	/**
	 * 
	 *
	 */
	public NWCGUpdateNFESResReq() {
		incNo = NWCGConstants.EMPTY_STRING;
		incYr = NWCGConstants.EMPTY_STRING;
		orderNo = NWCGConstants.EMPTY_STRING;
		orderHdrKey = NWCGConstants.EMPTY_STRING;
		htAddrInfo = new Hashtable<String, String>();
		shippingContactName = NWCGConstants.EMPTY_STRING;
		shippingContactPhone = NWCGConstants.EMPTY_STRING;
		populatedBaseReqNo = false;
		elmOrderExtn = null;
		elmPersonInfoShipTo = null;
		shipNode = NWCGConstants.EMPTY_STRING;
	}

	/**
	 * This method will - Get order details using OrderHeaderKey from Shipment
	 * output xml - Make a hashtable of orderline key to shipment line - Make a
	 * hashtable of base request no to orderlines - Send
	 * UpdateIncidentNotification message for each base request along with
	 * Shipment line
	 * 
	 * @param env
	 * @param docIP
	 * @return
	 * @throws Exception
	 */
	public Document processUpdateNFESResourceReq(YFSEnvironment env, Document docIP) throws Exception {
		System.out.println("@@@@@ Entering NWCGUpdateNFESResReq::processUpdateNFESResourceReq @@@@@");
		System.out.println("@@@@@ docIP : " + XMLUtil.getXMLString(docIP));

		Element elmOnSuccDoc = docIP.getDocumentElement();
		String strOrderHdrKey = elmOnSuccDoc
				.getAttribute(NWCGConstants.ORDER_HEADER_KEY);

		// OrderHeaderKey element is present in the extended event template, but it is not present in ON_SUCCESS event of confirmShipment. So, making a check of null or no data and if so, get the order header key from shipment line
		if (strOrderHdrKey == null || strOrderHdrKey.length() < 2) {
			strOrderHdrKey = ((Element) elmOnSuccDoc.getElementsByTagName("ShipmentLine").item(0)).getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		}

		Document docOrderDtls = getOrderDetails(env, strOrderHdrKey);
		Element elmOrder = docOrderDtls.getDocumentElement();
		String strOrderType = elmOrder.getAttribute(NWCGConstants.ORDER_TYPE);
		if (strOrderType.equalsIgnoreCase(NWCGConstants.OB_ORDER_TYPE_REFURBISHMENT) || strOrderType.equalsIgnoreCase(NWCGConstants.OB_ORDER_TYPE_INCDT_REFURB) || strOrderType.equalsIgnoreCase(NWCGConstants.CORRECTION_MISC_ORDER_TYPE)) {
			return docIP;
		}
		NodeList nlOrderChildNodes = elmOrder.getChildNodes();

		String sysOfOrigin = "";
		// Begin CR846
		String strBaseOrderHeaderKey = "";
		// End CR846
		boolean obtExtnElm = false;
		for (int i = 0; i < nlOrderChildNodes.getLength() && !obtExtnElm; i++) {
			Node tmpNode = nlOrderChildNodes.item(i);
			if (tmpNode.getNodeType() == Node.ELEMENT_NODE) {
				Element elmOrderChild = (Element) tmpNode;
				if (elmOrderChild.getNodeName().equalsIgnoreCase("Extn")) {
					sysOfOrigin = elmOrderChild.getAttribute(NWCGConstants.EXTN_SYSTEM_OF_ORIGIN);
					// Begin CR846
					strBaseOrderHeaderKey = elmOrderChild.getAttribute(NWCGConstants.EXTN_BASE_ORDER_HEADER_KEY);
					// End CR846
				}
			}
		}

		if (sysOfOrigin == null || sysOfOrigin.equalsIgnoreCase("ICBSR")) {
			// Begin CR846
			if (strOrderType.equals(NWCGConstants.OB_ORDER_TYPE_BACKORDERED) || strOrderType.equals(NWCGConstants.OB_ORDER_TYPE_FWD_ORDER)) {
				Document docBaseOrderDetails = getOrderDetails(env, strBaseOrderHeaderKey);
				Element rootElemBaseOrder = docBaseOrderDetails.getDocumentElement();
				String strBaseOrderType = rootElemBaseOrder.getAttribute(NWCGConstants.ORDER_TYPE);
				if (strBaseOrderType.equalsIgnoreCase(NWCGConstants.OB_ORDER_TYPE_NORMAL) || strBaseOrderType.equalsIgnoreCase(NWCGConstants.OB_ORDER_TYPE_INCDT_REPLACEMENT)) {
					// BEGIN Production Issue # 1002 fix - Manish K The sendMessagesToROSS outside this if condition (line 195) was being used but that resulted in sending for all ICBSR issues Sending the message right away and then returning so we do not loose the logical track
					sendMessagesToROSS(env, docIP, docOrderDtls);
					return docIP;
					// END Production Issue # 1002 fix - Manish K
				} else {
					// End CR846
					return docIP;
					// Begin CR846
				}
			}
			// BEGIN Production Issue # 1002 fix - Manish K There was no Else ! - if the order was neither backorder nor forward order, it would still send the message !
			else {
				return docIP;
			}
			// END Production Issue # 1002 fix - Manish K
			// End CR846
		}

		// This is a ROSS initiated issue
		sendMessagesToROSS(env, docIP, docOrderDtls);

		System.out.println("@@@@@ Exiting NWCGUpdateNFESResReq::processUpdateNFESResourceReq @@@@@");
		return docIP;
	}

	/**
	 * This method will be called from different places, so moved the common
	 * code from processUpdateNFESResourceReq to sendMessagesToROSS
	 * 
	 * @param env
	 * @param docShipmentDtls
	 * @param docOrderDtls
	 * @return
	 */
	public Document sendMessagesToROSS(YFSEnvironment env, Document docShipmentDtls, Document docOrderDtls) {
		System.out.println("@@@@@ Entering NWCGUpdateNFESResReq::sendMessagesToROSS @@@@@");	
		System.out.println("@@@@@ docShipmentDtls : " + XMLUtil.getXMLString(docShipmentDtls));
		System.out.println("@@@@@ docOrderDtls : " + XMLUtil.getXMLString(docOrderDtls));
		Hashtable<String, Element> htOLKey2ShipmentLine = getShipmentLines(docShipmentDtls);
		Hashtable<String, Vector<Element>> htBaseReq2OLs = getValidLinesForBaseReq(env, docOrderDtls);
		buildAndSendUpdtReq2ROSS(env, htBaseReq2OLs, htOLKey2ShipmentLine);
		System.out.println("@@@@@ Exiting NWCGUpdateNFESResReq::sendMessagesToROSS @@@@@");	
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
	private Hashtable<String, Vector<Element>> getValidLinesForBaseReq(YFSEnvironment env, Document docOrderDtls) {
		System.out.println("@@@@@ Entering NWCGUpdateNFESResReq::getValidLinesForBaseReq @@@@@");	
		System.out.println("@@@@@ docOrderDtls : " + XMLUtil.getXMLString(docOrderDtls));
		
		Hashtable<String, Vector<Element>> htBaseReq2OL = new Hashtable<String, Vector<Element>>();
		Element elmOrderDoc = docOrderDtls.getDocumentElement();
		orderNo = elmOrderDoc.getAttribute(NWCGConstants.ORDER_NO);
		orderHdrKey = elmOrderDoc.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		shipNode = elmOrderDoc.getAttribute(NWCGConstants.SHIP_NODE);
		NodeList nlOrder = elmOrderDoc.getChildNodes();
		// Get the address details, Incident No and Incident Year
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
		NodeList nlOrderLines = elmOrderDoc.getElementsByTagName("OrderLine");
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
		Hashtable<String, String> htItem2PublishStatus = getItemPublishStatus(env, vecItems);
		for (int i = 0; i < nlOrderLines.getLength(); i++) {
			String baseReqNo = NWCGConstants.EMPTY_STRING;
			Element orderLine = (Element) nlOrderLines.item(i);
			String reqNo = NWCGConstants.EMPTY_STRING;
			String extnBoNotifiedRoss = NWCGConstants.EMPTY_STRING;
			String extnFoNotifiedRoss = NWCGConstants.EMPTY_STRING;
			NodeList nlExtnOL = orderLine.getElementsByTagName("Extn");
			if (nlExtnOL != null && nlExtnOL.getLength() > 0) {
				Element elmExtnOL = (Element) nlExtnOL.item(0);
				reqNo = elmExtnOL.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
				extnBoNotifiedRoss = elmExtnOL.getAttribute(NWCGConstants.EXTN_BACK_ORDER_NOTIFIED_ROSS);
				extnFoNotifiedRoss = elmExtnOL.getAttribute(NWCGConstants.EXTN_FORWARD_ORDER_NOTIFIED_ROSS);
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
			String olStatus = orderLine.getAttribute(NWCGConstants.MAX_LINE_STATUS);
			if (olStatus.equalsIgnoreCase(NWCGConstants.STATUS_UTF)) {
				// Add this line if it is a partial substitution line
				if (reqNo.indexOf(".") == -1) {
					// UTF due to user action
					addLine = false; 
				} else {
					String parentReqNo = reqNo.substring(0, reqNo.lastIndexOf("."));
					// Check if the parent request number is part of this issue, if so it is a partial substitution line
					String parentItemID = htReqNo2ItemID.get(parentReqNo);
					if (parentItemID != null && parentItemID.trim().length() > 0) {
						String olItemID = htReqNo2ItemID.get(reqNo);
						if (olItemID.equals(parentItemID)) {
							// Partial Substitution Line
							addLine = true;
						} else {
							// This line is derived as part of substitution, but user made it UTF manually. Agent will send this line
							addLine = false;
						}
					} else {
						// This implies that the line is in UTF status because of user action
						addLine = false;
					}
				}
			} else if (olStatus.equalsIgnoreCase(NWCGConstants.STATUS_CANCELLED_DUE_TO_CONS) || olStatus.equalsIgnoreCase(NWCGConstants.STATUS_CANCELLED_DUE_TO_SUBS) || olStatus.equalsIgnoreCase(NWCGConstants.STATUS_CANCELLED)) {
				addLine = false;
			} else {
				if (olStatus.indexOf(".") != -1) {
					olStatus = olStatus.substring(0, olStatus.indexOf("."));
				}
				if (olStatus.compareTo("3700") >= 0) {
					addLine = true;
				} else {
					addLine = false;
				}
			}
			// if orderLine was already notified earlier do not add to the list
			if (extnBoNotifiedRoss.equalsIgnoreCase("Y") || extnFoNotifiedRoss.equalsIgnoreCase("Y")) {
				continue;
			}
			// If we need to send this line to ROSS, then add it to hashtable
			if (addLine) {
				orderLine.setAttribute(NWCGConstants.ORDER_NO, orderNo);
				String orderCreateTS = elmOrderDoc.getAttribute("Createts");
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
		System.out.println("@@@@@ Exiting NWCGUpdateNFESResReq::getValidLinesForBaseReq @@@@@");
		return htBaseReq2OL;
	}

	/**
	 * This method will get the items publish status and sets in a hashtable
	 * against each item
	 * 
	 * @param env
	 * @param vecItems
	 * @return
	 */
	public Hashtable<String, String> getItemPublishStatus(YFSEnvironment env, Vector<String> vecItems) {
		System.out.println("@@@@@ Entering NWCGUpdateNFESResReq::getItemPublishStatus @@@@@");	
		
		Hashtable<String, String> htItem2PublishStatus = new Hashtable<String, String>();
		try {
			Document docGetItemListIP = XMLUtil.createDocument("Item");
			Element elmItemList = docGetItemListIP.getDocumentElement();
			elmItemList.setAttribute(NWCGConstants.ORGANIZATION_CODE, "NWCG");

			Element elmComplexQry = docGetItemListIP.createElement("ComplexQuery");
			elmItemList.appendChild(elmComplexQry);
			elmComplexQry.setAttribute("Operator", "AND");

			Element elmComplexOrQry = docGetItemListIP.createElement("Or");
			elmComplexQry.appendChild(elmComplexOrQry);
			for (int i = 0; i < vecItems.size(); i++) {
				Element elmComplexOrItemExprQry = docGetItemListIP.createElement("Exp");
				elmComplexOrQry.appendChild(elmComplexOrItemExprQry);
				elmComplexOrItemExprQry.setAttribute("Name", "ItemID");
				elmComplexOrItemExprQry.setAttribute("Value", vecItems.get(i));
			}

			System.out.println("@@@@@ docGetItemListIP : " + XMLUtil.getXMLString(docGetItemListIP));
			Document docGetItemListOP = CommonUtilities.invokeAPI(env, "NWCGCreateRequestMsgToROSS_getItemList", NWCGConstants.API_GET_ITEM_LIST, docGetItemListIP);
			System.out.println("@@@@@ docGetItemListOP : " + XMLUtil.getXMLString(docGetItemListOP));
			NodeList nlItems = docGetItemListOP.getElementsByTagName("Item");
			if (nlItems != null && nlItems.getLength() > 0) {
				for (int j = 0; j < nlItems.getLength(); j++) {
					Element elmItem = (Element) nlItems.item(j);
					String publishToROSS = ((Element) elmItem.getElementsByTagName("Extn").item(0)).getAttribute(NWCGConstants.EXTN_PUBLISH_TO_ROSS);
					htItem2PublishStatus.put(elmItem.getAttribute(NWCGConstants.ITEM_ID), publishToROSS);
				}
			}
		} catch (ParserConfigurationException pce) {
			System.out.println("!!!!! Parser Configuration Exception while making " + NWCGConstants.NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE + " : " + pce.getMessage());
			pce.printStackTrace();
		} catch (Exception e) {
			System.out.println("!!!!! Exception while making " + NWCGConstants.NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE + " : " + "Exception : " + e.getMessage());
			e.printStackTrace();
		}

		System.out.println("@@@@@ Exiting NWCGUpdateNFESResReq::getItemPublishStatus @@@@@");	
		return htItem2PublishStatus;
	}

	/**
	 * This method will get the hashtable with orderline key to shipment line
	 * 
	 * @param docConfirmShipment
	 * @return
	 */
	private Hashtable<String, Element> getShipmentLines(Document docConfirmShipment) {
		System.out.println("@@@@@ Entering NWCGUpdateNFESResReq::getShipmentLines @@@@@");	
		
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
				ead = edd;
			}
		} catch (ParseException e) {
			System.out.println("!!!!! Failed while converting EAD and EDD dates : " + e.getMessage());
			e.printStackTrace();
		}
		NodeList nlShipmentLines = elmConfirmShipment.getElementsByTagName("ShipmentLine");
		// WE ARE NOT GETTING QUANTITY IN THE EXTENDED INPUT IN SHIPMENT LINE
		for (int i = 0; i < nlShipmentLines.getLength(); i++) {
			Element elmShipmentLine = (Element) nlShipmentLines.item(i);
			elmShipmentLine.setAttribute(NWCGConstants.EXTN_ESTIMATED_DEPART_DATE, edd);
			elmShipmentLine.setAttribute(NWCGConstants.EXTN_ESTIMATED_ARRIVAL_DATE, ead);
			htOLKey2ShipmentLine.put(elmShipmentLine.getAttribute(NWCGConstants.ORDER_LINE_KEY), elmShipmentLine);
		}
		
		System.out.println("@@@@@ Exiting NWCGUpdateNFESResReq::getShipmentLines @@@@@");	
		return htOLKey2ShipmentLine;
	}

	/**
	 * This method will get the order details based on order header key
	 * 
	 * @param env
	 * @param strOrderHdrKey
	 * @return
	 * @throws Exception
	 */
	private Document getOrderDetails(YFSEnvironment env, String strOrderHdrKey) throws Exception {
		System.out.println("@@@@@ Entering NWCGUpdateNFESResReq::getOrderDetails @@@@@");
		
		Document docOrderDtlsOP = null;
		try {
			Document docOrderDtlsIP = XMLUtil.getDocument("<Order OrderHeaderKey=\"" + strOrderHdrKey + "\"/>");
			System.out.println("@@@@@ docOrderDtlsIP : " + XMLUtil.getXMLString(docOrderDtlsIP));
			docOrderDtlsOP = CommonUtilities.invokeAPI(env, "NWCGUpdateNFESResReq_getOrderDetails", NWCGConstants.API_GET_ORDER_DETAILS, docOrderDtlsIP);
			System.out.println("@@@@@ docOrderDtlsOP : " + XMLUtil.getXMLString(docOrderDtlsOP));
		} catch (ParserConfigurationException pce) {
			System.out.println("!!!!! ParserConfigurationException : " + pce.getMessage());
			pce.printStackTrace();
			throw pce;
		} catch (SAXException e) {
			System.out.println("!!!!! SAXException : " + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			System.out.println("!!!!! IOException : " + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			System.out.println("!!!!! Exception : " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
		
		System.out.println("@@@@@ Exiting NWCGUpdateNFESResReq::getOrderDetails @@@@@");
		return docOrderDtlsOP;
	}

	/**
	 * This method will send a message to ROSS for each base request no
	 * 
	 * @param env
	 * @param htBaseReq2OLs
	 * @param htOLKey2ShipmentLine
	 */
	private void buildAndSendUpdtReq2ROSS(YFSEnvironment env, Hashtable<String, Vector<Element>> htBaseReq2OLs, Hashtable<String, Element> htOLKey2ShipmentLine) {
		System.out.println("@@@@@ Entering NWCGUpdateNFESResReq::buildAndSendUpdtReq2ROSS @@@@@");
		
		Enumeration<Vector<Element>> e = htBaseReq2OLs.elements();
		while (e.hasMoreElements()) {
			NWCGNFESResourceRequest nfesResReq = new NWCGNFESResourceRequest("ro:UpdateNFESResourceRequestReq");
			nfesResReq.setDocAttributes(env.getUserId(), orderHdrKey, "Order", orderNo, null);
			nfesResReq.setMessageOriginator(shipNode);
			Document latestIncidentInfoDoc = NWCGNFESResourceRequest.getLatestIncidentInfo(env, incNo, incYr);
			if (latestIncidentInfoDoc != null) {
				Element incidentOrderElement = (Element) latestIncidentInfoDoc.getDocumentElement().getElementsByTagName("NWCGIncidentOrder").item(0);
				incNo = incidentOrderElement.getAttribute("IncidentNo");
				incYr = incidentOrderElement.getAttribute("Year");
			}
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
				// Get the item ID for base request no. If it is not present, then make a call to get the item ID
				String baseItemID = htReqNo2ItemID.get(baseReqNo);
				if (baseItemID == null) {
					htReqNo2ItemID.put(baseReqNo, getItemIDForReqNo(env, baseReqNo));
				}
				String olKey = elmOL.getAttribute(NWCGConstants.ORDER_LINE_KEY);
				Element elmShipmentOL = htOLKey2ShipmentLine.get(olKey);
				nfesResReq.populateFillDtlOrConsolidationDtl(elmOL, elmShipmentOL, htReqNo2ItemID);
			}
			nfesResReq.populateAddressDtls(elmOrderExtn, elmPersonInfoShipTo);
			nfesResReq.populateShippingContactDtls(shippingContactName, shippingContactPhone);
			nfesResReq.populateSpecialNeeds();
			try {
				System.out.println("@@@@@ Input XML to " + NWCGConstants.SVC_POST_OB_MSG_SVC + ", " + XMLUtil.extractStringFromDocument(nfesResReq.getNFESResourceRequestDocument()));
				CommonUtilities.invokeService(env, NWCGConstants.SVC_POST_OB_MSG_SVC, XMLUtil.extractStringFromDocument(nfesResReq.getNFESResourceRequestDocument()));
			} catch (Exception ex) {
				System.out.println("!!!!! Exception for Base Req No " + reqNo + " : " + ex.getMessage());
				ex.printStackTrace();
			}
		}
		
		System.out.println("@@@@@ Exiting NWCGUpdateNFESResReq::buildAndSendUpdtReq2ROSS @@@@@");
	}

	/**
	 * Make getOrderLineList with the below input template <OrderLine> <Extn
	 * ExtnRequestNo="S-100511"/> <Order> <Extn ExtnIncidentNo=""
	 * ExtnIncidentYear="" /> </Order> </OrderLine>
	 * 
	 * @param reqNo
	 * @return Document getOrderLineList output XML based on the given template
	 *         specified on parameter opTemplateName
	 */
	private String getItemIDForReqNo(YFSEnvironment env, String reqNo) {
		System.out.println("@@@@@ Entering NWCGUpdateNFESResReq::getItemIDForReqNo @@@@@");
		
		String itemID = "";
		try {
			Document docGetOLListIP = XMLUtil.createDocument(NWCGConstants.ORDER_LINE);
			Element elmGetOL = docGetOLListIP.getDocumentElement();
			Element elmOLExtn = docGetOLListIP.createElement(NWCGConstants.EXTN_ELEMENT);
			elmGetOL.appendChild(elmOLExtn);
			elmOLExtn.setAttribute(NWCGConstants.EXTN_REQUEST_NO, reqNo);
			Element elmOrder = docGetOLListIP.createElement(NWCGConstants.ORDER_ELM);
			elmGetOL.appendChild(elmOrder);
			Element elmOrderExtn = docGetOLListIP.createElement(NWCGConstants.EXTN_ELEMENT);
			elmOrder.appendChild(elmOrderExtn);
			elmOrderExtn.setAttribute(NWCGConstants.INCIDENT_NO, incNo);
			elmOrderExtn.setAttribute(NWCGConstants.INCIDENT_YEAR, incYr);
			System.out.println("@@@@@ docGetOLListIP : " + XMLUtil.getXMLString(docGetOLListIP));
			Document docGetOLListOP = CommonUtilities.invokeAPI(env, "NWCGUpdateNFESResReq_getOrderLineList", NWCGConstants.API_GET_ORDER_LINE_LIST, docGetOLListIP);
			System.out.println("@@@@@ docGetOLListOP : " + XMLUtil.getXMLString(docGetOLListOP));
			itemID = ((Element) docGetOLListOP.getDocumentElement().getElementsByTagName("Item").item(0)).getAttribute(NWCGConstants.ITEM_ID);
		} catch (ParserConfigurationException pce) {
			System.out.println("!!!!! Parser Configuration Exception : " + pce.getMessage());

		} catch (Exception e) {
			System.out.println("!!!!! Exception : " + e.getMessage());
		}
		
		System.out.println("@@@@@ Exiting NWCGUpdateNFESResReq::getItemIDForReqNo @@@@@");
		return itemID;
	}
}