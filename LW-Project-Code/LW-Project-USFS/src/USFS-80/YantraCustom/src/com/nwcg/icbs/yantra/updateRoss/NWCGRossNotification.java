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

import com.nwcg.icbs.yantra.api.issue.util.NWCGNFESResourceRequest;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGRossNotification implements YIFCustomApi {
	
	String incidentNo;

	String incidentYear;

	String orderNo;

	String orderHeaderKey;

	String shipNode;

	Element orderExtnElement;

	Element personInfoShipToElement;

	String shippingContactName;

	String shippingContactPhone;

	Hashtable<String, String> requestNoItemIdMap;

	/**
	 * Called to initialize data:
	 *  - Issue Details - When you select a "Shipping Method" from that little dropdown. 
	 *  - Issue Details - When you have entered items and click "Save"
	 *  - Shipment Details - When you "Confirm Shipment". 
	 *  
	 * @param env
	 * @param inDoc
	 * @return
	 * @throws Exception
	 */
	public NWCGRossNotification() {
		System.out.println("@@@@@ In NWCGRossNotification::NWCGRossNotification @@@@@");
		incidentNo = NWCGConstants.EMPTY_STRING;
		incidentYear = NWCGConstants.EMPTY_STRING;
		orderNo = NWCGConstants.EMPTY_STRING;
		orderHeaderKey = NWCGConstants.EMPTY_STRING;
		shipNode = NWCGConstants.EMPTY_STRING;
		requestNoItemIdMap = new Hashtable<String, String>();
	}

	public void setProperties(Properties arg0) throws Exception {
	}

	/**
	 * Called to Notify ROSS:
	 *  - Issue Details - When you select a "Shipping Method" from that little dropdown. 
	 *  - Issue Details - When you have entered items and click "Save"
	 *  - Shipment Details - When you "Confirm Shipment". 
	 *  
	 * @param env
	 * @param inDoc
	 * @return
	 * @throws Exception
	 */
	public Document notifyRoss(YFSEnvironment env, Document inDoc) throws Exception {
		System.out.println("@@@@@ Entering NWCGRossNotification::notifyRoss @@@@@");
		System.out.println("@@@@@ inDoc : " + XMLUtil.getXMLString(inDoc));
		
		Element orderElement = inDoc.getDocumentElement();
		String documentType = orderElement.getAttribute(NWCGConstants.DOCUMENT_TYPE);
		String systemOfOrigin = null;

		NodeList orderChildNodeList = orderElement.getChildNodes();
		for (int i = 0; i < orderChildNodeList.getLength(); i++) {
			Element currentElement = (Element) orderChildNodeList.item(i);
			if (currentElement.getNodeName().equalsIgnoreCase("Extn")) {
				orderExtnElement = currentElement;
				incidentNo = currentElement.getAttribute(NWCGConstants.INCIDENT_NO);
				incidentYear = currentElement.getAttribute(NWCGConstants.INCIDENT_YEAR);
				shippingContactName = currentElement.getAttribute(NWCGConstants.SHIP_CONTACT_NAME_ATTR);
				shippingContactPhone = currentElement.getAttribute(NWCGConstants.SHIP_CONTACT_PHONE_ATTR);
				systemOfOrigin = currentElement.getAttribute(NWCGConstants.EXTN_SYSTEM_OF_ORIGIN);
			} else if (currentElement.getNodeName().equalsIgnoreCase("PersonInfoShipTo")) {
				personInfoShipToElement = currentElement;
			}
		}
		if ((systemOfOrigin != null && systemOfOrigin.equalsIgnoreCase("ROSS")) && (documentType != null && documentType.equals("0001"))) {
			Hashtable<String, Vector<Element>> requestNoOrderLinesMap = getSelectedOrderLines(env, inDoc);
			buildAndSendUpdateRequest(env, requestNoOrderLinesMap);
			updateYFSOrderLine(env, requestNoOrderLinesMap);
		}

		System.out.println("@@@@@ Exiting NWCGRossNotification::notifyRoss @@@@@");
		return inDoc;
	}

	/**
	 * a. Get only those Order Lines which are full backorder or full forward order.
	 * b. set EXTN_BACK_ORDER_NOTIFIED_ROSS and EXTN_FORWARD_ORDER_NOTIFIED_ROSS flags.
	 * 
	 * @param env
	 * @param orderDetailsDoc
	 * @return
	 */
	private Hashtable<String, Vector<Element>> getSelectedOrderLines(YFSEnvironment env, Document orderDetailsDoc) {
		System.out.println("@@@@@ Entering NWCGRossNotification::getSelectedOrderLines @@@@@");
		
		Hashtable<String, Vector<Element>> requestNoOrderLineMap = new Hashtable<String, Vector<Element>>();
		Element orderElement = orderDetailsDoc.getDocumentElement();
		orderNo = orderElement.getAttribute(NWCGConstants.ORDER_NO);
		System.out.println("@@@@@ orderNo: " + orderNo);
		orderHeaderKey = orderElement.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		System.out.println("@@@@@ orderHeaderKey: " + orderHeaderKey);
		shipNode = orderElement.getAttribute(NWCGConstants.SHIP_NODE);
		System.out.println("@@@@@ shipNode: " + shipNode);

		NodeList orderLinesNodeList = orderElement.getElementsByTagName("OrderLine");
		Vector<String> itemsVector = new Vector<String>();
		for (int i = 0; i < orderLinesNodeList.getLength(); i++) {
			Element orderLineElement = (Element) orderLinesNodeList.item(i);
			Element extnElement = (Element) orderLineElement.getElementsByTagName("Extn").item(0);
			String requestNo = extnElement.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
			System.out.println("@@@@@ requestNo: " + requestNo);
			Element itemElement = (Element) orderLineElement.getElementsByTagName("Item").item(0);
			String itemID = itemElement.getAttribute(NWCGConstants.ITEM_ID);
			System.out.println("@@@@@ itemID: " + itemID);
			requestNoItemIdMap.put(requestNo, itemID);
			if (!itemsVector.contains(itemID)) {
				itemsVector.add(itemID);
			}
		}

		Hashtable<String, String> htItem2PublishStatus = getItemPublishStatus(env, itemsVector);

		for (int i = 0; i < orderLinesNodeList.getLength(); i++) {
			String baseReqNo = NWCGConstants.EMPTY_STRING;
			Element orderLine = (Element) orderLinesNodeList.item(i);
			String reqNo = NWCGConstants.EMPTY_STRING;
			int requestedQty = 0;
			int backOrderQty = 0;
			int forwardOrderQty = 0;
			String backOrderNotifiedRoss = "N";
			String forwardOrderNotifiedRoss = "N";
			Element orderLineExtnElement = null;
			NodeList extnElementNodeList = orderLine.getElementsByTagName("Extn");
			if (extnElementNodeList != null && extnElementNodeList.getLength() > 0) {
				orderLineExtnElement = (Element) extnElementNodeList.item(0);
				requestedQty = (int) Double.parseDouble(orderLineExtnElement.getAttribute(NWCGConstants.EXTN_ORGIN_REQ_QTY));
				System.out.println("@@@@@ requestedQty: " + requestedQty);
				backOrderQty = (int) Double.parseDouble(orderLineExtnElement.getAttribute(NWCGConstants.EXTN_BACKORDER_QTY));
				System.out.println("@@@@@ backOrderQty: " + backOrderQty);
				forwardOrderQty = (int) Double.parseDouble(orderLineExtnElement.getAttribute(NWCGConstants.EXTN_FWD_QTY));
				System.out.println("@@@@@ forwardOrderQty: " + forwardOrderQty);
				backOrderNotifiedRoss = orderLineExtnElement.getAttribute(NWCGConstants.EXTN_BACK_ORDER_NOTIFIED_ROSS);
				System.out.println("@@@@@ backOrderNotifiedRoss: " + backOrderNotifiedRoss);
				forwardOrderNotifiedRoss = orderLineExtnElement.getAttribute(NWCGConstants.EXTN_FORWARD_ORDER_NOTIFIED_ROSS);
				System.out.println("@@@@@ forwardOrderNotifiedRoss: " + forwardOrderNotifiedRoss);
				reqNo = orderLineExtnElement.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
				System.out.println("@@@@@ reqNo: " + reqNo);
				if (reqNo.indexOf(".") != -1) {
					baseReqNo = reqNo.substring(0, reqNo.indexOf("."));
				} else {
					baseReqNo = reqNo;
				}
				System.out.println("@@@@@ baseReqNo: " + baseReqNo);
			}

			// Only ROSS published items are added
			String itemID = requestNoItemIdMap.get(reqNo);
			System.out.println("@@@@@ itemID: " + itemID);
			String publishStatus = htItem2PublishStatus.get(itemID);
			System.out.println("@@@@@ publishStatus: " + publishStatus);
			if (!publishStatus.equalsIgnoreCase("Y")) {
				continue;
			}

			boolean addLine = false;
			String extnBOFlag = null;
			String extnFOFlag = null;

			if ((requestedQty > 0) && (requestedQty == backOrderQty) && backOrderNotifiedRoss.equalsIgnoreCase("N")) {
				addLine = true;
				extnBOFlag = "Y";
				extnFOFlag = "N";
			} else if ((requestedQty > 0) && (requestedQty == forwardOrderQty) && forwardOrderNotifiedRoss.equalsIgnoreCase("N")) {
				addLine = true;
				extnBOFlag = "N";
				extnFOFlag = "Y";
			}
			System.out.println("@@@@@ extnBOFlag: " + extnBOFlag);
			System.out.println("@@@@@ extnFOFlag: " + extnFOFlag);

			if (addLine) {
				orderLine.setAttribute(NWCGConstants.ORDER_NO, orderNo);
				String orderCreateTS = orderElement.getAttribute("Createts");
				System.out.println("@@@@@ orderCreateTS: " + orderCreateTS);
				orderCreateTS = (orderCreateTS != null) ? orderCreateTS : NWCGConstants.EMPTY_STRING;
				orderLine.setAttribute("OrderCreatets", orderCreateTS);
				if (orderLineExtnElement != null) {
					orderLineExtnElement.setAttribute((NWCGConstants.EXTN_BACK_ORDER_NOTIFIED_ROSS), extnBOFlag);
					orderLineExtnElement.setAttribute((NWCGConstants.EXTN_FORWARD_ORDER_NOTIFIED_ROSS), extnFOFlag);
				}
				if (requestNoOrderLineMap.containsKey(baseReqNo)) {
					Vector<Element> vecOLElmsForABaseReq = requestNoOrderLineMap.get(baseReqNo);
					vecOLElmsForABaseReq.add(orderLine);
				} else {
					Vector<Element> vecOLElm = new Vector<Element>();
					vecOLElm.add(orderLine);
					requestNoOrderLineMap.put(baseReqNo, vecOLElm);
				}
			}
		}
		
		System.out.println("@@@@@ Exiting NWCGRossNotification::getSelectedOrderLines @@@@@");
		return requestNoOrderLineMap;
	}

	/**
	 * 
	 * @param env
	 * @param itemsVector
	 * @return
	 */
	private Hashtable<String, String> getItemPublishStatus(YFSEnvironment env, Vector<String> itemsVector) {
		System.out.println("@@@@@ Entering NWCGRossNotification::getItemPublishStatus @@@@@");
		
		Hashtable<String, String> itemPublishStatusMap = new Hashtable<String, String>();
		try {
			Document docGetItemListIP = XMLUtil.createDocument("Item");
			Element elmItemList = docGetItemListIP.getDocumentElement();
			elmItemList.setAttribute(NWCGConstants.ORGANIZATION_CODE, "NWCG");

			Element elmComplexQry = docGetItemListIP.createElement("ComplexQuery");
			elmItemList.appendChild(elmComplexQry);
			elmComplexQry.setAttribute("Operator", "AND");

			Element elmComplexOrQry = docGetItemListIP.createElement("Or");
			elmComplexQry.appendChild(elmComplexOrQry);

			for (int i = 0; i < itemsVector.size(); i++) {
				Element elmComplexOrItemExprQry = docGetItemListIP.createElement("Exp");
				elmComplexOrQry.appendChild(elmComplexOrItemExprQry);
				elmComplexOrItemExprQry.setAttribute("Name", "ItemID");
				elmComplexOrItemExprQry.setAttribute("Value", itemsVector.get(i));
			}

			Document docGetItemListOP = CommonUtilities.invokeAPI(env, "NWCGCreateRequestMsgToROSS_getItemList", NWCGConstants.API_GET_ITEM_LIST, docGetItemListIP);
			NodeList nlItems = docGetItemListOP.getElementsByTagName("Item");
			if (nlItems != null && nlItems.getLength() > 0) {
				for (int j = 0; j < nlItems.getLength(); j++) {
					Element elmItem = (Element) nlItems.item(j);
					String publishToROSS = ((Element) elmItem.getElementsByTagName("Extn").item(0)).getAttribute(NWCGConstants.EXTN_PUBLISH_TO_ROSS);
					System.out.println("@@@@@ publishToROSS: " + publishToROSS);
					itemPublishStatusMap.put(elmItem.getAttribute(NWCGConstants.ITEM_ID), publishToROSS);
				}
			}
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("@@@@@ Exiting NWCGRossNotification::getItemPublishStatus @@@@@");
		return itemPublishStatusMap;
	}

	/**
	 * Only gets called if the System of Origin is "ROSS" AND Document Type is "0001" to update ROSS. 
	 * 
	 * @param env
	 * @param requestNoOrderLinesMap
	 */
	private void buildAndSendUpdateRequest(YFSEnvironment env, Hashtable<String, Vector<Element>> requestNoOrderLinesMap) {
		System.out.println("@@@@@ Entering NWCGRossNotification::buildAndSendUpdateRequest @@@@@");
		
		Enumeration<Vector<Element>> orderLinesEnum = requestNoOrderLinesMap.elements();
		while (orderLinesEnum.hasMoreElements()) {
			NWCGNFESResourceRequest nfesResReq = new NWCGNFESResourceRequest(NWCGAAConstants.UPDATE_NFES_RESOURCE_REQUEST_MESSAGE_ROOT);
			nfesResReq.setDocAttributes(env.getUserId(), orderHeaderKey, "Order", orderNo, null);
			nfesResReq.setMessageOriginator(shipNode);

			Document latestIncidentInfoDoc = NWCGNFESResourceRequest.getLatestIncidentInfo(env, incidentNo, incidentYear);
			if (latestIncidentInfoDoc != null) {
				Element incidentOrderElement = (Element) latestIncidentInfoDoc.getDocumentElement().getElementsByTagName("NWCGIncidentOrder").item(0);
				incidentNo = incidentOrderElement.getAttribute("IncidentNo");
				incidentYear = incidentOrderElement.getAttribute("Year");
			}

			nfesResReq.setRequestKey(incidentNo, incidentYear, NWCGConstants.EMPTY_STRING);
			boolean updtReqNo = false;
			String baseReqNo = NWCGConstants.EMPTY_STRING;
			Vector<Element> selectedOrderLines = orderLinesEnum.nextElement();

			for (int i = 0; i < selectedOrderLines.size(); i++) {
				Element selectedOrderLineElement = selectedOrderLines.elementAt(i);
				if (!updtReqNo) {
					updtReqNo = true;
					baseReqNo = ((Element) selectedOrderLineElement.getElementsByTagName("Extn").item(0)).getAttribute(NWCGConstants.EXTN_BASE_REQUEST_NO);
					nfesResReq.updateRequestNo(baseReqNo);
				}
				// Get the item ID for base request no. If it is not present, then make a call to get the item ID
				String baseItemID = requestNoItemIdMap.get(baseReqNo);
				System.out.println("@@@@@ baseItemID (didn't have to get it using Hashtable): " + baseItemID);
				if (baseItemID == null) {
					requestNoItemIdMap.put(baseReqNo, getItemIDForReqNo(env, baseReqNo));
				}
				nfesResReq.populateFillDtlOrConsolidationDtl(selectedOrderLineElement, null, requestNoItemIdMap);
			}

			nfesResReq.populateAddressDtls(orderExtnElement, personInfoShipToElement);
			nfesResReq.populateShippingContactDtls(shippingContactName, shippingContactPhone);
			nfesResReq.populateSpecialNeeds();

			try {
				String serviceInput = XMLUtil.extractStringFromDocument(nfesResReq.getNFESResourceRequestDocument());
				System.out.println("@@@@@ Input XML to " + NWCGConstants.SVC_POST_OB_MSG_SVC + ", " + serviceInput);
				CommonUtilities.invokeService(env, NWCGConstants.SVC_POST_OB_MSG_SVC, serviceInput);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		System.out.println("@@@@@ Exiting NWCGRossNotification::buildAndSendUpdateRequest @@@@@");
	}

	/**
	 * Used to get the Item ID if it wasn't present, during the build and send update to ROSS method.  
	 * 
	 * @param env
	 * @param reqNo
	 * @return
	 */
	private String getItemIDForReqNo(YFSEnvironment env, String reqNo) {
		System.out.println("@@@@@ Entering NWCGRossNotification::getItemIDForReqNo @@@@@");
		
		String itemId = "";
		try {
			Document ipDoc = XMLUtil.getDocument("<OrderLine><Extn ExtnRequestNo=\"" + reqNo + "\" /><Order><Extn ExtnIncidentNo=\"" + incidentNo + "\" ExtnIncidentYear=\"" + incidentYear + "\" /></Order></OrderLine>");
			Document opDoc = CommonUtilities.invokeAPI(env, "NWCGUpdateNFESResReq_getOrderLineList", NWCGConstants.API_GET_ORDER_LINE_LIST, ipDoc);
			itemId = ((Element) opDoc.getDocumentElement().getElementsByTagName("Item").item(0)).getAttribute(NWCGConstants.ITEM_ID);
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("@@@@@ baseItemID (had to get it using Hashtable): " + itemId);
		System.out.println("@@@@@ Exiting NWCGRossNotification::getItemIDForReqNo @@@@@");
		return itemId;
	}

	/**
	 * Only gets called if the System of Origin is "ROSS" AND Document Type is "0001", 
	 * after the build and send update to ROSS method is called. 
	 * 
	 * The special ROSS version of "changeOrder" is called to update the order, using the following custom template:
	 * 
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * <Order EnterpriseCode="" OrderHeaderKey="" DocumentType="" OrderNo="" ShipNode="" Status="" Createuserid="" Modifyuserid="" Createts="" >
	 *     <Extn/> 
	 *     <OrderLines>
	 *         <OrderLine OrderLineKey="" OrderNo="" Createuserid="" Modifyuserid="">
	 *             <Item ItemID="" ProductClass="" UnitOfMeasure="" />
	 *             <Extn/>
	 * 			<Notes/>
	 * 			<Instructions/>
	 *         </OrderLine>
	 *     </OrderLines>
	 * 	<PersonInfoShipTo/>
	 * </Order>
	 * 
	 * @param env
	 * @param requestNoOrderLinesMap
	 */
	private void updateYFSOrderLine(YFSEnvironment env, Hashtable<String, Vector<Element>> requestNoOrderLinesMap) {
		System.out.println("@@@@@ Entering NWCGRossNotification::updateYFSOrderLine @@@@@");
		
		try {
			Document changeOrderInputDoc = XMLUtil.createDocument("Order");
			Element orderElement = changeOrderInputDoc.getDocumentElement();
			orderElement.setAttribute("OrderHeaderKey", orderHeaderKey);
			Element orderLinesElement = changeOrderInputDoc.createElement("OrderLines");
			orderElement.appendChild(orderLinesElement);
			Enumeration<Vector<Element>> orderLinesEnum = requestNoOrderLinesMap.elements();
			while (orderLinesEnum.hasMoreElements()) {
				Vector<Element> orderLines = orderLinesEnum.nextElement();
				for (int i = 0; i < orderLines.size(); i++) {
					Element currentElement = orderLines.elementAt(i);
					Element orderLineElement = changeOrderInputDoc.createElement("OrderLine");
					orderLinesElement.appendChild(orderLineElement);
					XMLUtil.copyElement(changeOrderInputDoc, currentElement, orderLineElement);
					orderLineElement.setAttribute("Action", "MODIFY");
				}
			}
			System.out.println("@@@@@ changeOrderInputDoc : " + XMLUtil.getXMLString(changeOrderInputDoc));
			CommonUtilities.invokeAPI(env, "NWCGRossNotification_changeOrder", NWCGConstants.API_CHANGE_ORDER, changeOrderInputDoc);
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("@@@@@ Exiting NWCGRossNotification::updateYFSOrderLine @@@@@");
	}
}