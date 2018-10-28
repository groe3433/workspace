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

package com.nwcg.icbs.yantra.webservice.ob.msg;

import gov.nwcg.services.ross.common_types._1.ResponseMessageType;
import gov.nwcg.services.ross.common_types._1.ResponseStatusType;
import gov.nwcg.services.ross.resource_order._1.GetOperationResultsResp;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBElement;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.commCore.NWCGTimeKeyManager;
import com.nwcg.icbs.yantra.soap.NWCGSOAPFault;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.business.NWCGBusinessMsgProcessor;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStoreInterface;
import com.nwcg.icbs.yantra.webservice.ob.NWCGOBSyncAsyncProcessor;
import com.nwcg.icbs.yantra.webservice.util.NWCGJAXBContextWrapper;
import com.nwcg.icbs.yantra.webservice.util.alert.NWCGAlert;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServicesConstant;
import com.yantra.yfs.japi.YFSConnectionHolder;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

/**
 * 
 * 
 */
public class NWCGGetOperationResultsOB extends NWCGOBSyncAsyncProcessor
		implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGROSSLogger
			.instance(NWCGGetOperationResultsOB.class);
	
	/*
	 * A&A perform periodic scan of its database for the messages that have been
	 * sent, but not responded by ROSS. The message must have status SENT or
	 * FORCED_RESPONSE_ SENT_FAILED and persisted for more then allowed period
	 * of time This method creates Soap Envelop and sets required Soap Header
	 * elements. add message and sends synchronously to ROSS for each of the
	 * above messages.
	 * 
	 * If receives successful synchronous Response in getOperationResponse, sets
	 * message status to FORCED_PROCESSED. forward message to Sterling App for
	 * processing synchronously
	 * 
	 * If receives error or exception from Sterling App, it set status to
	 * FORCED_PROCESSED_FAULT. Creates an Alert in NWCG_FAULT queue. Alert
	 * properties are AlertSet3.1.4a.
	 * 
	 * If connection failed due to communication problems, message status is set
	 * to FORCED_RESPONSE_ SENT_FAILED. Does not forward message to Sterling
	 * App.
	 * 
	 * If receives Soap Fault back,sets status to FORCED_RESPONSE_FAULT and stop
	 * processing. Creates an Alert in NWCG_FAULT queue. Alert property are
	 * AlertSet3.1.4b
	 * 
	 */
	private Properties myProperties = null;

	public Document getOperationResults(YFSEnvironment env, Document inDoc) {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsOB::getOperationResults");
		try {
			return process(env, inDoc);
		} catch (Exception e) {

		}
		logger.verbose("@@@@@ Exiting NWCGGetOperationResultsOB::getOperationResults");
		return null;
	}

	public Document process(YFSEnvironment env, Document msg) throws Exception {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsOB::process");
		
		setupMessageMap(env, msg);
		String latestMsgkey = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_LATESTKEY);
		String OBdistributionID = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID);
		logger.verbose("@@@@@ Incoming distribution ID on GetOperationResultsOB class: " + OBdistributionID);

		Document msgDoc = null;
		Object respObj = null;
		Document resp = XMLUtil.getDocument();
		Object unmarshalledReturnObject = null;
		GetOperationResultsResp getOpResp = null;
		try {
			// Before we invoke the ROSS web service we need to comit all transactions in database for user authorization.
			java.sql.Connection conn = ((YFSConnectionHolder) env).getDBConnection();
			conn.commit();
			msgDoc = createMessageRequest();

			// Send across
			respObj = sendMessageRequest(msg);
			Document returnResponse = null;
			String returnResponseStr = null;
			if (respObj != null && respObj instanceof SOAPMessage) {
				SOAPMessage soapMsgResponse = (SOAPMessage) respObj;
				SOAPBody soapBody = soapMsgResponse.getSOAPBody();
				returnResponse = soapBody.extractContentAsDocument();
				returnResponseStr = XMLUtil.extractStringFromDocument(returnResponse);
				unmarshalledReturnObject = getUnmarshalledObject(returnResponse, new URL(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
			}
			if (unmarshalledReturnObject != null && unmarshalledReturnObject instanceof GetOperationResultsResp) {
				getOpResp = (GetOperationResultsResp) unmarshalledReturnObject;
			}
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, returnResponseStr);
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_LATESTKEY, latestMsgkey);
			msgStore.setMsgStatusForcedProcessedOB(msgMap);
		} catch (SOAPFaultException sfe) {
			handleSecurityFailureSoapFault(sfe);
		} catch (Exception e) {
			msgStore.setMsgStatusForcedFailed(msgMap);
			logger.error("NWCGGetOperationResultsOB.processAsync>>exception in OB processing; returning",e);
		}
		if (getOpResp == null) {
			logger.verbose("++++++++++++++++++++++++++++++++++++++++++++++++++++");
			logger.verbose("NWCGGetOperationResultsOB.process>>got null responce");
			logger.verbose("++++++++++++++++++++++++++++++++++++++++++++++++++++");
			setMsgStatusSentSyncFailed(msgDoc);
		}

		List<Object> listContent = getOpResp.getContent();
		Iterator it = listContent.iterator();
		int returnCode = 0;
		Object xsAnyObj = null;
		String responseMessage = null;
		List<ResponseMessageType> rmts = null;
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof JAXBElement) {
				JAXBElement jxb = (JAXBElement) o;
				Object p = jxb.getValue();
				if (p instanceof ResponseStatusType) {
					ResponseStatusType rst = (ResponseStatusType) p;
					returnCode = rst.getReturnCode();
					rmts = rst.getResponseMessage();
				}
			} else {
				xsAnyObj = o;
			}
		}
		if (returnCode == -1) {
			handleNegativeReturnCode(env, rmts);
			msgStore.setMsgStatusForcedFailed(msgMap);
			return null;
		} else {
			if (StringUtil.isEmpty(latestMsgkey)) {// NOT FOUND
				// Couldn't find the distributionID/originalMessageNameRequest combination in the outbound message store. Create a throw a SOAPFault.
				SOAPFault fault = NWCGSOAPFault.createParentSOAPFault(NWCGWebServicesConstant.SOAP_FAULT_MESSAGE_DOES_NOT_EXIST);
				throw new SOAPFaultException(fault);
			}
			Document xsAnyDoc = null;
			try {
				xsAnyDoc = new NWCGJAXBContextWrapper().getDocumentFromUnknownObject(xsAnyObj, xsAnyDoc);
				responseMessage = XMLUtil.extractStringFromDocument(xsAnyDoc);
			} catch (Exception e) {
				return null;
			}
			String xsAnyName = xsAnyDoc.getDocumentElement().getLocalName();
			logger.verbose("@@@@@ xsAnyDoc.getDocumentElement().getLocalName() :: " + xsAnyName);
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, responseMessage);
			msgMap.put("xsAny", xsAnyDoc.getDocumentElement());
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME, xsAnyName);
			msgStore.setMsgStatusForcedProcessedOB(msgMap);
			try {
				// Forward response to Sterling
				Document sts = new NWCGBusinessMsgProcessor().forwardDeliveryResults(msgMap);
				// If receives error or exception from Sterling App, it set status to FORCED_PROCESSED_FAULT. sts need to be parsed to find its meaning
			} catch (Exception e) {
				setMsgStatusForcedProcessedFault();
				NWCGAlert.raiseForcedProcessFaultAlert(msgMap, null);
			}
		}
		java.sql.Connection conn = ((YFSConnectionHolder) env).getDBConnection();
		conn.commit();

		logger.verbose("@@@@@ Exiting NWCGGetOperationResultsOB::process");
		return resp;
	}

	private void handleNegativeReturnCode(YFSEnvironment env, List<ResponseMessageType> responseMessages) {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsOB::handleNegativeReturnCode");
		String alertDescription = "Failed to get operation results. Received -1 return code";
		HashMap<String, String> inboxReferencesMap = new HashMap<String, String>();
		inboxReferencesMap.put(NWCGAAConstants.NAME, "GetOperationResultsReq");
		int respMsgSize = responseMessages.size();
		for (int i = 0; i < respMsgSize; i++) {
			ResponseMessageType rmt = responseMessages.get(i);
			inboxReferencesMap.put("Code (" + (i + 1) + " of " + respMsgSize + ")", rmt.getCode());
			inboxReferencesMap.put("Description (" + (i + 1) + " of " + respMsgSize + ")", rmt.getDescription());
			inboxReferencesMap.put("Severity (" + (i + 1) + " of " + respMsgSize + ")", rmt.getSeverity());
		}
		CommonUtilities.raiseAlertAndAssigntoUser(env, "NWCG_FAULT", alertDescription, "", null, inboxReferencesMap);
		logger.verbose("@@@@@ Exiting NWCGGetOperationResultsOB::handleNegativeReturnCode");
	}

	private String getDistributionKeyFromInputReq(YFSEnvironment env,
			Document inDoc) {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsOB::getDistributionKeyFromInputReq");
		String retVal = null;
		Element docElm = inDoc.getDocumentElement();
		NodeList nodeList = docElm
				.getElementsByTagNameNS("*", "DistributionID");

		for (int count = 0; count < nodeList.getLength(); count++) {
			Node curNode = nodeList.item(count);
			logger.verbose("@@@@@ XMLUtil::getFirstElementByName::curNode.getLocalName() :: " + curNode.getLocalName());
			if (curNode.getLocalName().equalsIgnoreCase("DistributionID")) {
				retVal = curNode.getTextContent();
			}
		}
		logger.verbose("@@@@@ Exiting NWCGGetOperationResultsOB::getDistributionKeyFromInputReq");
		return retVal;
	}

	@Override
	protected void setupMessageMap(YFSEnvironment env, Document msg) throws Exception {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsOB::setupMessageMap");
		super.setupMessageMap(env, msg);
		String OBdistributionID = getDistributionKeyFromInputReq(env, msg);
		String latestMsgkey = msgStore.getLatestMessageKeyOBForDistID(env, OBdistributionID);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID, OBdistributionID);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_LATESTKEY, latestMsgkey);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME, "GetOperationResultsReq");
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_SERVNAME, "GetOperationResultsReq");
		// incase this is nitiated by the user from UI the user id will be what we receive, else incase of agent or apitester use dummy ross interface user
		String strUid = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_USERNAME);
		if (strUid != null && strUid.equals("")) {
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_USERNAME, NWCGAAConstants.ENV_USER_ID);
		}
		msgMap.put(NWCGAAConstants.MDTO_NAMESPACE, NWCGAAConstants.RESOURCE_ORDER_NAMESPACE);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_CUSTOMPROP, NWCGAAConstants.CUSTOM_PROPERTIES);
		String pw_key = NWCGTimeKeyManager.createKey(latestMsgkey, NWCGAAConstants.ENV_USER_ID, env.getSystemName());
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_PWD, pw_key);
		logger.verbose("@@@@@ Exiting NWCGGetOperationResultsOB::setupMessageMap");
	}

	protected void handleSecurityFailureSoapFault(SOAPFaultException sfe) {
		logger.verbose("@@@@@ In NWCGGetOperationResultsOB::handleSecurityFailureSoapFault");
		NWCGAlert.raiseSecurityFailedAlert(msgMap);
		msgStore.setMsgStatusForcedFailed(msgMap);
		throw sfe;
	}

	public void setProperties(Properties arg0) throws Exception {
		logger.verbose("@@@@@ In NWCGGetOperationResultsOB::setProperties");
		this.myProperties = arg0;
	}

	public void setMsgStatusForcedProcessedFault() {
		logger.verbose("@@@@@ In NWCGGetOperationResultsOB::setMsgStatusForcedProcessedFault");
		msgStore.setMsgStatusForcedProcessedFault(msgMap);
	}
}