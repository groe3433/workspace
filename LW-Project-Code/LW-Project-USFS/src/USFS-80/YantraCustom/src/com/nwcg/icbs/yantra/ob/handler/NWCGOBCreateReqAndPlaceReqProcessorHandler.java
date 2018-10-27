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

package com.nwcg.icbs.yantra.ob.handler;

import gov.nwcg.services.ross.common_types._1.ResponseMessageType;
import gov.nwcg.services.ross.common_types._1.ResponseStatusType;
import gov.nwcg.services.ross.resource_order._1.CreateRequestAndPlaceResp;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.nwcg.icbs.yantra.api.issue.NWCGUpdateNFESResReq;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.commCore.NWCGMessageStore;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.util.NWCGJAXBContextWrapper;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServiceUtils;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGOBCreateReqAndPlaceReqProcessorHandler implements NWCGOBProcessorHandler {

	String orderHdrKey = "";
	String orderNo = "";
	
	/**
	 * This method will handle all exceptions and log an Alert for them as they occur. 
	 * 
	 * @param env
	 * @param Message
	 * @throws Exception
	 */
	public void throwAlert(YFSEnvironment env, StringBuffer Message) throws Exception {
		System.out.println("@@@@@ Entering NWCGOBCreateReqAndPlaceReqProcessorHandler::throwAlert @@@@@");
		Message.append(" ExceptionType='" + NWCGConstants.NWCG_RETURN_ALERTTYPE + "'" + " InboxType='" + NWCGConstants.NWCG_RETURN_INBOXTYPE + "' QueueId='" + NWCGConstants.NWCG_RETURN_QUEUEID + "' />");
		try {
			System.out.println("@@@@@ Trying to log the Alert...");
			Document inTemplate = XMLUtil.getDocument(Message.toString());
			CommonUtilities.invokeAPI(env, NWCGConstants.NWCG_CREATE_EXCEPTION, inTemplate);
			System.out.println("@@@@@ Logged the Alert.");
		} catch (Exception ex1) {
			System.out.println("@@@@@ Caught exception while trying to log the Alert, choosing to do nothing about it...");
			ex1.printStackTrace();
		}
		System.out.println("@@@@@ Exiting NWCGOBCreateReqAndPlaceReqProcessorHandler::throwAlert @@@@@");
	}	
	
	/**
	 * This method will raise an alert in NWCG_ISSUE_FAILURE or not based
	 * on return code.
	 */
	public Document process(HashMap<Object, Object> msgMap) throws Exception {
		System.out.println("@@@@@ Entering NWCGOBCreateReqAndPlaceReqProcessorHandler::process @@@@@");
		
		YFSEnvironment env = NWCGWebServiceUtils.getEnvironment();
		String distId = (String) msgMap.get(NWCGAAConstants.MDTO_DISTID);
		String msgName = (String) msgMap.get(NWCGAAConstants.MDTO_MSGNAME);
		orderNo = (String) msgMap.get(NWCGAAConstants.MDTO_ENTVALUE);
		orderHdrKey = (String) msgMap.get(NWCGAAConstants.MDTO_ENTKEY);
		Object actualObj = null;
		Object objResp = msgMap.get("xsAny");
		
		boolean bObjRespIsElmType = false;
		if (objResp instanceof Element){
			System.out.println("@@@@@ Object is an Element...");
			Document docXsAny = XMLUtil.getDocument();
			Node nodeXsAny = docXsAny.importNode((Node) objResp, true);
			docXsAny.appendChild(nodeXsAny);
			actualObj = new NWCGJAXBContextWrapper().getObjectFromDocument(docXsAny, new URL(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
			bObjRespIsElmType = true;
		}

		CreateRequestAndPlaceResp createReqAndPlaceResp = null;
		if (bObjRespIsElmType){
			createReqAndPlaceResp = (CreateRequestAndPlaceResp) actualObj;
		}
		else {
			createReqAndPlaceResp = (CreateRequestAndPlaceResp) objResp;
		}
		
		ResponseStatusType respStatType = createReqAndPlaceResp.getResponseStatus();
		HashMap<String, String> hMap = new HashMap<String, String>();
		hMap.put("Distribution ID", distId);
		hMap.put("ExceptionType", msgName);
		hMap.put("AlertType", msgName);
		hMap.put(NWCGAAConstants.NAME, msgName);
		
		NWCGMessageStore nwcgMsgStore = NWCGMessageStore.getMessageStore();
		Document docSentReq = nwcgMsgStore.getOBMessageForDistID(env, distId);
		Element elmSentReq = docSentReq.getDocumentElement();
		String reqCreatedByUserId = elmSentReq.getAttribute("NWCGUSERID");
		if (orderNo == null || orderNo.trim().length() < 2){
			orderNo = elmSentReq.getAttribute(NWCGAAConstants.ENTITY_VALUE);
			orderHdrKey = elmSentReq.getAttribute(NWCGAAConstants.ENTITY_KEY);
		}
		hMap.put("Order No", orderNo);

		if (respStatType.getReturnCode() == -1){
			// generate an alert in NWCG_ISSUE_FAILURE
			hMap.put(NWCGAAConstants.RESP_RETURN_CODE, "-1");
			
			//	Fetching ship node from Order Number
			String cacheId = CommonUtilities.getShipNodeFromOrderNo(env, orderNo);
			
			//	adding ship node to alert references
			hMap.put(NWCGConstants.ALERT_SHIPNODE_KEY, cacheId);
			
			if (reqCreatedByUserId != null){
				hMap.put(NWCGConstants.ALERT_REQ_CREATED_BY, reqCreatedByUserId);
			}
			
			System.out.println("@@@@@ Getting code, severity and description...");
			if (respStatType.isSetResponseMessage()){
				ArrayList <ResponseMessageType> listRespMsgType = (ArrayList <ResponseMessageType>) respStatType.getResponseMessage();
				// We are displaying only one code, severity and description in alert console. So, taking the 0th element only.
				if (listRespMsgType != null && listRespMsgType.size() > 0){
					System.out.println("@@@@@ Error list : " + listRespMsgType.size());
					Iterator respMsgTypeItr = listRespMsgType.iterator();
					if (respMsgTypeItr.hasNext()){
						ResponseMessageType respMsgType = (ResponseMessageType) respMsgTypeItr.next();
						hMap.put(NWCGAAConstants.RESP_CODE, respMsgType.getCode());
						hMap.put(NWCGAAConstants.RESP_SEVERITY, respMsgType.getSeverity());
						hMap.put(NWCGAAConstants.RESP_DESCRIPTION, respMsgType.getDescription());
					}
				}
			}
			
			String userIdToAssign = CommonUtilities.getAdminUserForCache(env, cacheId);
			
			System.out.println("@@@@@ Obtained code, severity and description...");
			CommonUtilities.raiseAlertAndAssigntoUser(env, NWCGAAConstants.QUEUEID_ISSUE_FAILURE, "Failed ICBSR initiated request at ROSS", userIdToAssign, null, hMap);
		}
		else {
			// Do not raise alert if it is a success message - Bug 125 from Increment 4 testing
			if (distId != null){
				System.out.println("@@@@@ Received success message from ROSS for distribution ID : " + distId);
			}
			if (orderHdrKey == null || orderHdrKey.trim().length() < 2){
				// raise alert with distribution id mentioning that there is no order no in input
				System.out.println("@@@@@ No order header key as part of input, so not processing the fill message...");
			}
			else {
				triggerFillMessages(env, reqCreatedByUserId);
			}
		}
		
		System.out.println("@@@@@ Exiting NWCGOBCreateReqAndPlaceReqProcessorHandler::process @@@@@");
		return null;
	}
	
	/**
	 * 
	 * 
	 * @param env
	 * @param reqCreatedByUserId
	 * @throws Exception
	 */
	public void triggerFillMessages(YFSEnvironment env, String reqCreatedByUserId) throws Exception{
		System.out.println("@@@@@ Entering NWCGOBCreateReqAndPlaceReqProcessorHandler::triggerFillMessages @@@@@");
		
		YFSEnvironment updtEnv = CommonUtilities.createEnvironment(reqCreatedByUserId, env.getProgId());
		
		Document docShipmentDtls = getShipmentDtls(updtEnv);
		System.out.println("@@@@@ docShipmentDtls : " + XMLUtil.getXMLString(docShipmentDtls));
		if (docShipmentDtls == null) {
			//raise alert mentioning that there are no shipment details for this issue
			System.out.println("!!!!! Throwing Alert because the docShipmentDtls shipment document is null...");
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGOBCreateReqAndPlaceReqProcessorHandler' " + "No shipment details for this issue! ");
			throwAlert(env, stbuf);
		}
		
		System.out.println("@@@@@ orderHdrKey : " + orderHdrKey);
		Document docOrderDtls = CommonUtilities.getOrderDetails(env, orderHdrKey, "NWCGUpdateNFESResReq_getOrderDetails");
		System.out.println("@@@@@ docOrderDtls : " + XMLUtil.getXMLString(docOrderDtls));
		
		NWCGUpdateNFESResReq updtNFESResReq = new NWCGUpdateNFESResReq();
		updtNFESResReq.sendMessagesToROSS(updtEnv, docShipmentDtls, docOrderDtls);
		
		System.out.println("@@@@@ Exiting NWCGOBCreateReqAndPlaceReqProcessorHandler::triggerFillMessages @@@@@");
	}
	
	/**
	 * 
	 * 
	 * @param env
	 * @return
	 * @throws Exception
	 */
	public Document getShipmentDtls(YFSEnvironment env) throws Exception {
		System.out.println("@@@@@ Entering NWCGOBCreateReqAndPlaceReqProcessorHandler::getShipmentDtls @@@@@");
		
		Document docShipmentDtlsOP = null;
		try {
			Document docShipmentListIP = XMLUtil.getDocument("<Order OrderHeaderKey=\"" + orderHdrKey + "\"/>");
			System.out.println("@@@@@ docShipmentListIP : " + XMLUtil.getXMLString(docShipmentListIP));
			Document docShipmentListTmpl = XMLUtil.getDocument("<ShipmentList><Shipment ShipmentKey=\"\"/></ShipmentList>");
			System.out.println("@@@@@ docShipmentListTmpl : " + XMLUtil.getXMLString(docShipmentListTmpl));
			Document docShipmentListOP = CommonUtilities.invokeAPI(env, docShipmentListTmpl, "getShipmentListForOrder", docShipmentListIP);
			System.out.println("@@@@@ docShipmentListOP : " + XMLUtil.getXMLString(docShipmentListOP));
			
			NodeList nlShipmentList = docShipmentListOP.getDocumentElement().getElementsByTagName(NWCGConstants.SHIPMENT_ELEMENT);
			if (nlShipmentList == null || nlShipmentList.getLength() < 1){
				System.out.println("!!!!! Throwing Alert because the nlShipmentList shipment list is null...");
				StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGOBCreateReqAndPlaceReqProcessorHandler' " + "There are no shipment details for order no : " + orderNo + ". So, not triggering fill messages");
				throwAlert(env, stbuf);
				
				System.out.println("@@@@@ Exiting NWCGOBCreateReqAndPlaceReqProcessorHandler::getShipmentDtls @@@@@");
				return null;
			}
			
			String shipmentKey = ((Element)nlShipmentList.item(0)).getAttribute(NWCGConstants.SHIPMENT_KEY);
			Document docShipmentDtlsIP = XMLUtil.getDocument("<Shipment ShipmentKey=\"" + shipmentKey + "\"/>");
			
			System.out.println("@@@@@ docShipmentDtlsIP : " + XMLUtil.getXMLString(docShipmentDtlsIP));
			docShipmentDtlsOP = CommonUtilities.invokeAPI(env, "NWCGOBCreateReqHndlr_getShipmentDtls", NWCGConstants.API_GET_SHIPMENT_DETAILS, docShipmentDtlsIP);
			System.out.println("@@@@@ docShipmentDtlsOP : " + XMLUtil.getXMLString(docShipmentDtlsOP));
		}
		catch(ParserConfigurationException pce){
			System.out.println("!!!!! ParserConfigurationException...");
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGOBCreateReqAndPlaceReqProcessorHandler' " + "ParserConfigurationException...");
			throwAlert(env, stbuf);
			pce.printStackTrace();
			throw pce;
		} catch (SAXException e) {
			System.out.println("!!!!! SAXException...");
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGOBCreateReqAndPlaceReqProcessorHandler' " + "SAXException...");
			throwAlert(env, stbuf);
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			System.out.println("!!!!! IOException...");
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGOBCreateReqAndPlaceReqProcessorHandler' " + "IOException...");
			throwAlert(env, stbuf);
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			System.out.println("!!!!! Exception...");
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGOBCreateReqAndPlaceReqProcessorHandler' " + "Exception...");
			throwAlert(env, stbuf);
			e.printStackTrace();
			throw e;
		}
		
		System.out.println("@@@@@ Exiting NWCGOBCreateReqAndPlaceReqProcessorHandler::getShipmentDtls @@@@@");
		return docShipmentDtlsOP;
	}
}