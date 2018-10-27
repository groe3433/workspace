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

package com.nwcg.icbs.yantra.webservice.ib.msg;

import gov.nwcg.services.ross.resource_order._1.GetOperationResultsResp;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.TransformerException;
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.ib.NWCGIBWebService;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStore;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStoreInterface;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServiceUtils;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServicesConstant;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGGetOperationResultsIB extends NWCGIBWebService {
	
	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGGetOperationResultsIB.class);

	/*
	 * 	ROSS tries to recover Response by sending synchronous request with Distribution ID for the  missing Response message
	 1.	This method is invoked  from getOperationResults  WebService interface for the missing message response
	 2.	There is no recoding of actual receive of the GetOperationResult request in the Message Store.
	 3.	Checks if Response message to be recovered exists in the Message Store with the status FAILED_READY_FOR_PICKUP or  FAULT_READY_FOR_PICKUP
	 4.	If finds Message then returns Response and sets message status to FORCED_PROCESSED
	 6.	If does not find message or the message status is wrong or any other problems encountered, 
	 returns Negative Response with failure reason and description. 
	 There is no records remains in the Message Store to reflect that the failure occurred  
	 */
	public SOAPMessage process() {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsIB::process");
		SOAPMessage resp = null;
		NWCGMessageStoreInterface messageStore = NWCGMessageStore.getMessageStore();
		//GetOperationResults should always be sync, throw SOAPFault if customProperty set to ProcessSynchronously=false
		if (!super.isSync()) {
			SOAPFault sf = createSOAPFault("ProcessSynchronously=false for GetOperationResultsReq");
			logger.error("!!!!! ICBSR received GetOperationResultsReq with customProperty set to ProcessSynchronously=false");
			throw new SOAPFaultException(sf);
		}
		boolean deliveryReqRequiresAuth = requiresSecurityAuthentication();
		boolean securityVerified = false;
		// Right now, verifyMesssageSecurity() will never return false because it will throw up an exception before returning a false boolean
		if (deliveryReqRequiresAuth) {
			securityVerified = verifyMessageSecurity();
		}
		try {
			// extract soapBody content and distID 
			msgMap = setupMessageMap();
			String distId = getDistributionIdFromBody();
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID, distId);
			if (NWCGWebServiceUtils.isEmpty(distId)) {
				resp = createNegativeResponse(NWCGWebServicesConstant.SOAP_FAULT_MESSAGE_GETOPERATION_EMPTY_DISTID, NWCGAAConstants.SOAP_FAILURE_MESSAGE_CODE_2_1);//"ERROR: getOperationResults failed to retreive the message"
				logger.verbose("@@@@@ Exiting NWCGGetOperationResultsIB::process (resp)");
				return resp;
			}
			// get Response message to be recovered if exists in the Message Store with the status FAILED_READY_FOR_PICKUP or  FAULT_READY_FOR_PICKUP			
			msgMap = messageStore.getResponseMessageForDistID(msgMap);
			String msg = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSG);
			if (!NWCGWebServiceUtils.isEmpty(msg))
				resp = createResponse(msg);
			//sets message status to FORCED_PROCESSED
			if (resp != null) {
				messageStore.setMsgStatusForcedProcessedIB(msgMap);
			} else {
				resp = createNegativeResponse(NWCGWebServicesConstant.SOAP_FAULT_MESSAGE_GETOPERATION_FAILEDRETREIVE, NWCGAAConstants.SOAP_FAILURE_MESSAGE_CODE_1);
				messageStore.setMsgStatusForcedProcessedFaultIB(msgMap);
			}
		} catch (Exception e) {
			// Send negative response to ROSS
			resp = createNegativeResponse(NWCGWebServicesConstant.SOAP_FAULT_MESSAGE_GETOPERATION_FAILEDPROCESS, NWCGAAConstants.SOAP_FAILURE_MESSAGE_CODE_2_2);
			messageStore.setMsgStatusForcedProcessedFaultIB(msgMap);
		}
		logger.verbose("@@@@@ Exiting NWCGGetOperationResultsIB::process");
		return resp;
	}

	protected String getDistributionIdFromBody() {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsIB::getDistributionIdFromBody");
		String bodyDistributionId = "";
		String soapBodyString = getPropertyFromContext(NWCGWebServicesConstant.SOAP_ELEMENT_MESSAGEBODY);
		Document soapBodyDoc = getDocumentFromXML(soapBodyString);
		Element distributionIdElement = XMLUtil.getFirstElementByName(soapBodyDoc.getDocumentElement(), "DistributionID");
		bodyDistributionId = XMLUtil.getNodeValue(distributionIdElement);
		logger.verbose("@@@@@ Exiting NWCGGetOperationResultsIB::getDistributionIdFromBody");
		return bodyDistributionId;
	}

	@Override
	protected HashMap setupMessageMap() throws Exception {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsIB::setupMessageMap");
		HashMap msgMap = new HashMap();
		// extract soapBody content and distID --checkout next constant
		String messageBodyXML = getPropertyFromContext(NWCGWebServicesConstant.SOAP_ELEMENT_MESSAGEBODY);
		Document incomingMsg = getDocumentFromXML(messageBodyXML);
		String distID = getPropertyFromContext(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_DISTID);
		// check what is in the header for this message!!!
		logger.verbose("@@@@@ setupMessageMap>>distId=" + distID + " body>>" + messageBodyXML);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY, messageBodyXML);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID, distID);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_SERVNAME, getServiceName(incomingMsg));
		YFSEnvironment env = NWCGWebServiceUtils.getEnvironment();
		String user_id = env.getUserId();
		String system_name = env.getSystemName();
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_ENV, env);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_SYSNAME, system_name);
		String loginID = NWCGAAConstants.USER_NAME;
		String userName = loginID;
		String message_name = getPropertyFromContext(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_MESSAGENAME);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME, message_name);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_USERNAME, userName);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_PWD, NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_PASSWORD);
		logger.verbose("@@@@@ Exiting NWCGGetOperationResultsIB::setupMessageMap");
		return msgMap;
	}

	/**
	 * @param string_token
	 */
	private SOAPMessage createResponse(String msg) {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsIB::createResponse");
		logger.verbose("@@@@@ createResponse>> " + msg);
		GetOperationResultsResp getOperResponseIB = new GetOperationResultsResp();
		Document doc = null;
		try {
			doc = XMLUtil.getDocument();
		} catch (ParserConfigurationException e) {
			logger.error(e.getLocalizedMessage(), e);
			return null;
		}
		Element rootResponseStatus = doc.createElementNS(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE, "ResponseStatus");
		doc.appendChild(rootResponseStatus);
		Element elemReturnCode = doc.createElement(NWCGAAConstants.RESP_RETURN_CODE);
		rootResponseStatus.appendChild(elemReturnCode);
		elemReturnCode.setTextContent(NWCGAAConstants.SOAP_SUCCESS_CODE + "");
		Document xsAny = null;
		try {
			xsAny = XMLUtil.getDocument(msg);
		} catch (ParserConfigurationException e1) {
			//logger.printStackTrace(e1);
		} catch (SAXException e1) {
			//logger.printStackTrace(e1);
		} catch (IOException e1) {
			//logger.printStackTrace(e1);
		}
		List<Object> listContent = getOperResponseIB.getContent();
		listContent.add(doc.getDocumentElement());

		if (xsAny != null) {
			listContent.add(xsAny.getDocumentElement());
		}
		SOAPMessage soapResponse = getSOAPMessageFromJAXBContextRO(getOperResponseIB);
		Document responseXML = getMarshalledObject(getOperResponseIB, "GetOperationResultsResp");
		//Save the GetOperationResultsResp XML in the LATEST IB msg store record, by putting it in as the MESSAGE_MAP_MSG key. Caller is responsible for making call to NWCGMessageStore to update the IB record.
		try {
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, XMLUtil.extractStringFromDocument(responseXML));
		} catch (Exception e) {
			logger.error("!!!!! Failed to saved GetOperationResultsResp XML in LATEST IB msg store.");
			logger.error(e.getLocalizedMessage(), e);
		}
		logger.verbose("@@@@@ Returning SOAPMessage:  " + soapResponse.toString());
		logger.verbose("@@@@@ Exiting NWCGGetOperationResultsIB::createResponse");
		return soapResponse;
	}

	/**
	 * @param String Description
	 * @param String ReturnCode
	 * @throws ParserConfigurationException 
	 * xml example
	 * <ns3:GetOperationResultsResp xmlns:ns2="http://nwcg.gov/services/ross/common_types/1.1" xmlns:ns3="http://nwcg.gov/services/ross/resource_order/1.1">
	 <ns2:responseStatusType>
	 <ReturnCode>-1</ReturnCode>
	 <ResponseMessage>
	 <Code>NWCG-ICBSR-ANA-000001</Code>
	 <Severity>Error</Severity>
	 <Description>ERROR: getOperationResults failed to retreive the message.</Description>
	 </ResponseMessage>
	 </ns2:responseStatusType>
	 </ns3:GetOperationResultsResp>
	 */
	private SOAPMessage createNegativeResponse(String description, String strErrorCode) {
		logger.verbose("@@@@@ Entering NWCGGetOperationResultsIB::createNegativeResponse");
		logger.verbose("@@@@@ createNegativeResponse>> " + description);
		GetOperationResultsResp getOperResponseIB = new GetOperationResultsResp();
		Document doc = null;
		try {
			doc = XMLUtil.getDocument();
		} catch (ParserConfigurationException e) {
			logger.error(e.getLocalizedMessage(), e);
			return null;
		}
		Element rootResponseStatus = doc.createElementNS(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE, "ResponseStatus");
		doc.appendChild(rootResponseStatus);
		Element elemReturnCode = doc.createElement(NWCGAAConstants.RESP_RETURN_CODE);
		rootResponseStatus.appendChild(elemReturnCode);
		elemReturnCode.setTextContent(NWCGAAConstants.SOAP_FAILURE_CODE + "");
		Element elemResponseMessage = doc.createElement("ResponseMessage");
		Element elemCode = doc.createElement(NWCGAAConstants.RESP_CODE);
		elemCode.setTextContent(strErrorCode);
		Element elemSeverity = doc.createElement(NWCGAAConstants.RESP_SEVERITY);
		elemSeverity.setTextContent(NWCGAAConstants.SEVERITY_ERROR);
		Element elemDescription = doc.createElement(NWCGAAConstants.RESP_DESCRIPTION);
		elemDescription.setTextContent(description);
		rootResponseStatus.appendChild(elemResponseMessage);
		elemResponseMessage.appendChild(elemCode);
		elemResponseMessage.appendChild(elemSeverity);
		elemResponseMessage.appendChild(elemDescription);
		List<Object> listContent = getOperResponseIB.getContent();
		listContent.add(doc.getDocumentElement());
		//Save the GetOperationResultsResp XML in the LATEST IB msg store record, by putting it in as the MESSAGE_MAP_MSG key. Caller is responsible for making call to NWCGMessageStore to update the IB record.
		try {
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, XMLUtil.extractStringFromDocument(doc));
		} catch (TransformerException e) {
			logger.error("!!!!! Failed to saved GetOperationResultsResp XML in LATEST IB msg store.");
			logger.error(e.getLocalizedMessage(), e);
		}
		SOAPMessage soapResponse = getSOAPMessageFromJAXBContextRO(getOperResponseIB);
		logger.verbose("@@@@@ Returning SOAPMessage:  " + soapResponse.toString());
		logger.verbose("@@@@@ Exiting NWCGGetOperationResultsIB::createNegativeResponse");
		return soapResponse;
	}
}