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

import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.api.issue.util.NWCGNFESResourceRequest;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * 
 * @author Oxford Consulting Group
 * @version 1.0
 * @date November 6, 2013
 */
public class NWCGChkIssueTypeAndUpdtItems implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGChkIssueTypeAndUpdtItems.class);
	
	/**
	 * 
	 * @param arg0
	 * @throws Exception
	 */
	public void setProperties(Properties arg0) throws Exception {
	}
	
	/**
	 * This method does the following
	 *   - Remove items that are not published to ROSS
	 *   - Set publish status. This parameter will be used later
	 *     in the service for sending the create request message to ROSS. If atleast one line
	 *     is present, then SendRequest field will be set to TRUE, else it will be set to FALSE
	 * @param env
	 * @param docIP
	 * @return
	 * @throws Exception
	 */
	public Document setPublishStatusAndRemoveUnpubItems(YFSEnvironment env, Document docIP) throws Exception {
		logger.verbose("@@@@@ Entering NWCGChkIssueTypeAndUpdtItems::setPublishStatusAndRemoveUnpubItems @@@@@");
		Element elmRootIP = docIP.getDocumentElement();
		Element elmOLs = null;
		NodeList nlRootChilds = elmRootIP.getChildNodes();
		for (int rootNode=0; rootNode < nlRootChilds.getLength(); rootNode++){
			Node tmpNode = nlRootChilds.item(rootNode);
			if (tmpNode.getNodeName().equals("OrderLines")){
				elmOLs = (Element) tmpNode; 
			}
		}
		NodeList nlOrderLine = elmOLs.getElementsByTagName("OrderLine");
		String sendReq = "FALSE";
		Hashtable<String, String> olKey2ItemID = new Hashtable<String, String>();
		Vector<String> vecItemList = new Vector<String>();
		if (nlOrderLine != null && nlOrderLine.getLength() > 0) {
			for (int i=0; i < nlOrderLine.getLength(); i++){
				Element elmOL = (Element) nlOrderLine.item(i);
				String olKey = elmOL.getAttribute("OrderLineKey");
				String itemID = ((Element) elmOL.getElementsByTagName("Item").item(0)).getAttribute("ItemID");
				olKey2ItemID.put(olKey, itemID);
				if (!vecItemList.contains(itemID)) {
					vecItemList.add(itemID);
				}
			}
			Hashtable <String, String> htItem2PublishStatus = getItemDtlsAndSetPublishVales(env, vecItemList);
			boolean sendLinesToROSS = false;
			int noOfOLs = nlOrderLine.getLength();
			for (int i=noOfOLs; i > 0; i--) {
				Element elmOL = (Element) nlOrderLine.item(i-1);
				String olKey = elmOL.getAttribute("OrderLineKey");
				String itemID = olKey2ItemID.get(olKey);
				String publishStatus = htItem2PublishStatus.get(itemID);
				if (publishStatus.equalsIgnoreCase(NWCGConstants.YES)) {
					sendLinesToROSS = true;
				}
				else {
					// Delete the orderline
					elmOLs.removeChild(nlOrderLine.item(i-1));
				}
			}
			// If there is atleast one line that has publish to status as "Y", then send the message to ROSS
			if (sendLinesToROSS){
				sendReq = "TRUE";
			}
		}
		else {
			sendReq = "FALSE";
		}
		elmRootIP.setAttribute("SendRequest", sendReq);
		logger.verbose("@@@@@ Exiting NWCGChkIssueTypeAndUpdtItems::setPublishStatusAndRemoveUnpubItems @@@@@");
		return docIP;
	}
	
	/**
	 * This method will make getItemList for all the items and returns a hashtable mentioning
	 * whether they are published or not
	 * @param env
	 * @param vecItemList
	 * @return
	 */
	private Hashtable <String, String> getItemDtlsAndSetPublishVales(YFSEnvironment env, Vector<String> vecItemList) {
		logger.verbose("@@@@@ Entering NWCGChkIssueTypeAndUpdtItems::getItemDtlsAndSetPublishVales @@@@@");
    	Hashtable <String, String> htItem2PublishStatus = new Hashtable<String, String>();
		try {
			Document docGetItemListIP = XMLUtil.createDocument("Item");
			Element elmItemList = docGetItemListIP.getDocumentElement();
			elmItemList.setAttribute(NWCGConstants.ORGANIZATION_CODE, "NWCG");
			
			Element elmComplexQry = docGetItemListIP.createElement("ComplexQuery");
			elmItemList.appendChild(elmComplexQry);
			elmComplexQry.setAttribute("Operator", "AND");
			
			Element elmComplexOrQry = docGetItemListIP.createElement("Or");
			elmComplexQry.appendChild(elmComplexOrQry);

			for (int i=0; i < vecItemList.size(); i++){
				String itemID = vecItemList.get(i);
				Element elmComplexOrItemExprQry = docGetItemListIP.createElement("Exp");
				elmComplexOrQry.appendChild(elmComplexOrItemExprQry);
				elmComplexOrItemExprQry.setAttribute("Name", "ItemID");
				elmComplexOrItemExprQry.setAttribute("Value", itemID);
			}
			
			Document docGetItemListOP = CommonUtilities.invokeAPI(env, "NWCGCreateRequestMsgToROSS_getItemList", NWCGConstants.API_GET_ITEM_LIST, docGetItemListIP);
			NodeList nlItems = docGetItemListOP.getElementsByTagName("Item");
			if (nlItems != null && nlItems.getLength() > 0){
				for (int j=0; j < nlItems.getLength(); j++){
					Element elmItem = (Element) nlItems.item(j);
					String publishToROSS = ((Element) elmItem.getElementsByTagName("Extn").item(0)).getAttribute(NWCGConstants.EXTN_PUBLISH_TO_ROSS);
					htItem2PublishStatus.put(elmItem.getAttribute(NWCGConstants.ITEM_ID), publishToROSS);
				}
			}
		}
		catch(ParserConfigurationException pce){
			logger.error("!!!!! Caught ParserConfigurationException : " + pce);
		}
		catch (Exception e){
			logger.error("!!!!! Caught General Exception : " + e);
		}
		logger.verbose("@@@@@ Exiting NWCGChkIssueTypeAndUpdtItems::getItemDtlsAndSetPublishVales @@@@@");
		return htItem2PublishStatus;
	}

	/**
	 * This method will get the order details on template of SCHEDULE_ORDER.ON_SUCCESS.xml
	 * Check for system of origin for this issue - ICBSR or ROSS
	 * Check for refursbishment or incdt_refurbishment issue type
	 * 	If system of origina is ROSS or issue type is refurbishment or incdt_refurbishment, then
	 * 	return the document with an added attribute of SendMessage=FALSE
	 * Call setPublishStatusAndRemoveUnpubItems
	 * @param env
	 * @param docIP
	 * @return
	 * @throws Exception
	 */
	public Document chkIssueAndItemTypeSetPublishStatus(YFSEnvironment env, Document docIP) throws Exception {
		logger.verbose("@@@@@ Entering NWCGChkIssueTypeAndUpdtItems::chkIssueAndItemTypeSetPublishStatus @@@@@");
		Element elmOnSuccDoc = docIP.getDocumentElement();
		String strOrderHdrKey = elmOnSuccDoc.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		// OrderHeaderKey element is present in the extended event template, but it is not present in ON_SUCCESS event of confirmShipment. So, making a check of null or no data and if so, get the order header key from shipment line
		if (strOrderHdrKey == null || strOrderHdrKey.length() < 2){
			strOrderHdrKey = ((Element)elmOnSuccDoc.getElementsByTagName("ShipmentLine").item(0)).getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		}
		Document docOrderDtls = CommonUtilities.getOrderDetails(env, strOrderHdrKey, "NWCGCreateRequestMsgToROSS_getOrderDetails");
		Element elmOrder = docOrderDtls.getDocumentElement();
		NodeList nlOrderChildNodes = elmOrder.getChildNodes();
		String sysOfOrigin = "";
		boolean obtExtnElm = false;
		for (int i=0; i < nlOrderChildNodes.getLength() && !obtExtnElm; i++){
			Node tmpNode = nlOrderChildNodes.item(i);
			if (tmpNode.getNodeType() == Node.ELEMENT_NODE){
				Element elmOrderChild = (Element) tmpNode;
				if (elmOrderChild.getNodeName().equalsIgnoreCase("Extn")){
					sysOfOrigin = elmOrderChild.getAttribute(NWCGConstants.EXTN_SYSTEM_OF_ORIGIN);
				}
			}
		}
		if ((sysOfOrigin != null) && sysOfOrigin.equalsIgnoreCase("ROSS")){
			elmOrder.setAttribute("SendRequest", "FALSE");
			return docOrderDtls;
		}
		String orderType = elmOrder.getAttribute(NWCGConstants.ORDER_TYPE);
		if (orderType.equalsIgnoreCase(NWCGConstants.REFURBISHMENT_ORDER_TYPE) || orderType.equalsIgnoreCase(NWCGConstants.INCDT_REFURBISHMENT_ORDER_TYPE) || orderType.equalsIgnoreCase(NWCGConstants.CORRECTION_MISC_ORDER_TYPE)) {
			elmOrder.setAttribute("SendRequest", "FALSE");
			return docOrderDtls;
		}
		
		//get latest incidentNo and update order
		NodeList nodeList = elmOrder.getElementsByTagName("Extn");
		if(nodeList != null) {
			Element extnElement = (Element)nodeList.item(0);
			if(extnElement != null) {
				String incidentNo = extnElement.getAttribute(NWCGConstants.EXTN_INCIDENT_NO);
				String incidentYear = extnElement.getAttribute(NWCGConstants.EXTN_INCIDENT_YEAR);
				Document latestIncidentInfoDoc = NWCGNFESResourceRequest.getLatestIncidentInfo(env, incidentNo, incidentYear);
				if(latestIncidentInfoDoc != null) {
					Element incidentOrderElement = (Element)latestIncidentInfoDoc.getDocumentElement().getElementsByTagName("NWCGIncidentOrder").item(0);
					incidentNo = incidentOrderElement.getAttribute("IncidentNo");
					incidentYear = incidentOrderElement.getAttribute("Year");
				}
				extnElement.setAttribute(NWCGConstants.EXTN_INCIDENT_NO, incidentNo);
				extnElement.setAttribute(NWCGConstants.EXTN_INCIDENT_YEAR, incidentYear);
			}
		}
		setPublishStatusAndRemoveUnpubItems(env, docOrderDtls);
		logger.verbose("@@@@@ Exiting NWCGChkIssueTypeAndUpdtItems::chkIssueAndItemTypeSetPublishStatus @@@@@");
		return docOrderDtls;
	}
}