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

import gov.nwcg.services.ross.common_types._1.ResponseStatusType;
import gov.nwcg.services.ross.common_types._1.ServicePingResp;
import gov.nwcg.services.ross.resource_order._1.RegisterIncidentInterestResp;

import java.net.URL;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.soap.NWCGAAUtil;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.business.NWCGBusinessMsgProcessor;
import com.nwcg.icbs.yantra.webservice.ib.NWCGIBWebService;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStore;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStoreInterface;
import com.nwcg.icbs.yantra.webservice.util.NWCGJAXBContextWrapper;
import com.nwcg.icbs.yantra.webservice.util.alert.NWCGAlert;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServicesConstant;

public class NWCGInboundSyncMessage extends NWCGIBWebService {
	/**
	 * Pre-conditions:
	 * WebService interface  receives synchronous call from ROSS
	 * Extracted Soap Header data are in context 
	 * This method updates message status to  RECEIVED
	 *    -Performs authentication and authorization via VerifySecuritySession WebService(done by verifyMessageSecurity)
	 *    -If security verification passed,invoke Sterling API (A&A Interface) . 
	 *     Sends Response XML  to ROSS and set message status to PROCESSED
	 *    -If security verification failed returns Soap Fault to ROSS, does not invoke API and set message status to AUTHENTICATION_FAILED ((done by verifyMessageSecurity))
	 *    -If receives error or exception from Sterling API, 
	 *       sets status to ACKNOLEDGEMENT_NEGATIVE and  
	 *       sends negative Message Acknowledgement response to ROSS . 
	 *       Creates an Alert in NWCG_INBOUND_NEGATIVE queue. Alert property is AlertSet3.2.2
	 */

	public SOAPMessage process() {
		System.out.println("@@@@@ Entering NWCGInboundSyncMessage::process @@@@@");

		SOAPMessage response = null;
		NWCGMessageStoreInterface messageStore = null;

		try {
			//Initialize the message map containing values from the messageContext
			msgMap = setupMessageMap();
			String strMsgName = (String) msgMap
					.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME);
			// ping service is just to check if services are up or not
			if (strMsgName.equals(NWCGWebServicesConstant.SERVICE_NAME_PING)) {
				// instantiate the object
				ServicePingResp resp = new ServicePingResp();
				GregorianCalendar currentCal = new GregorianCalendar();
				XMLGregorianCalendar currentTime = DatatypeFactory
						.newInstance().newXMLGregorianCalendar(currentCal);
				// set current time
				resp.setTimestamp(currentTime);
				Document respDoc = null;
				// get the document
				respDoc = new NWCGJAXBContextWrapper().getDocumentFromObject(
						resp, respDoc, new URL(
								NWCGAAConstants.COMMON_TYPES_NAMESPACE));
				// make soapmessage from document
				return createSOAPMessageResponse(respDoc);

			}
			messageStore = NWCGMessageStore.getMessageStore();
			//Create START / VOID and LATEST / RECEIVED
			//NWCG_INBOUND_MESSAGE records. Default distribution ID from UUID if blank
			messageStore.initInboundMessageFromMap(msgMap);
		} catch (Exception e) {
			NWCGLoggerUtil.Log.severe("Failure to initialize IB message store");
			e.printStackTrace();

			//response = handleInternalProcessingFault();
			//TODO: The behavior for handling of errors or faults for synchronous messsages
			//has yet to be defined as of Apr 2009
			response = null;
			return response;
		}

		try {
			//Determine whether we need to call verifySecuritySession for this
			//message name. Pulls from <messageName.requiresAuth in A&A Impl properties
			boolean requiresAuth = requiresSecurityAuthentication();

			boolean securityVerified = false;
			//Right now, verifyMesssageSecurity() will never return false because it will
			//throw up an exception before returning a false boolean
			if (requiresAuth) {
				securityVerified = verifyMessageSecurity();
			}

			if (securityVerified || !requiresAuth) {
				//Within the same thread, we'll process the message
				//forwardSyncMsg() should return either the positive or negative response
				//it's up the class defined as <messageName>.syncIBServiceClass in the
				//NWCGAnAImpl.properties files to implement the appropriate 
				//response (as a DOM Document)
				Document returnDoc = new NWCGBusinessMsgProcessor()
						.forwardSyncMsg(msgMap);
				String responseXMLString = null;
				if (returnDoc != null) {
					response = createSOAPMessageResponse(returnDoc);
					responseXMLString = XMLUtil.getXMLString(returnDoc);
				} else {
					//TODO: Should we even do anything here? The class handling the
					//response should return the appropriate response DOM Doc
				}

				//sets message status to PROCESSED
				if (returnDoc != null) {
					messageStore.setMsgStatusInboundProcessed(msgMap,
							responseXMLString);
				} else {
					//TODO: We need another mechanism to determine whether to 
					//update the message store with ACK NEG or PROCESSED
					//We could possibly scan the contents of the return DOM object as a string
					//from the implementing class for the message type to see if
					//it contains a certain string. Right now we're strictly going
					//off of whether or not the forwardSyncMsg() impelementation class
					//called returned null or not. Problem with this is that the
					//createNegativeResponse method is hard coded to return a 
					//RegisterIncidentResp body in a SOAP message

					//create negative ack
					response = createNegativeResponse();

					//Store the negative ack in the message store
					messageStore.setMsgStatusAckNegative(msgMap);

					//setup Alert
					createAPIFailedAlert(msgMap);
				}
			}
		} catch (SOAPFaultException sfe) {
			//SOAPFault thrown by verifyMessageSecurity on 
			// - empty password
			// - failure to validate user/pass
			// - unable to post to ROSS authentication webservice
			handleSecurityFailure(sfe);
		} catch (Exception e) {
			NWCGLoggerUtil.Log.severe("Exception from Sterling API="
					+ e.getMessage());
			e.printStackTrace();

			messageStore.setMsgStatusAckNegative(msgMap);

			//create negative resp
			response = createNegativeResponse();

			//setup Alert
			createAPIFailedAlert(msgMap);
		}

		System.out.println("@@@@@ Exiting NWCGInboundSyncMessage::process @@@@@");
		return response;
	}

	protected HashMap setupMessageMap() throws Exception {
		System.out.println("@@@@@ Entering NWCGInboundSyncMessage::setupMessageMap @@@@@");
		
		//if the msg contains an IS_SYNC element then it is in the wrong place
		HashMap msgMap = super.setupMessageMap();
		if (!NWCGAAUtil
				.isSync(
						(String) msgMap
								.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME),
						(String) msgMap
								.get(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_CUSTOMPROP)))
			throw new Exception(
					"Asynchronous Message passed from caller to A&A Synch interface");

		System.out.println("@@@@@ Exiting NWCGInboundSyncMessage::setupMessageMap @@@@@");
		return msgMap;
	}

	/**
	 * @param string_token
	 */
	private SOAPMessage createNegativeResponse() {
		System.out.println("@@@@@ Entering NWCGInboundSyncMessage::createNegativeResponse @@@@@");

		RegisterIncidentInterestResp resp = new RegisterIncidentInterestResp();
		try {

			ResponseStatusType respStatType = new ResponseStatusType();
			respStatType.setReturnCode(NWCGAAConstants.SOAP_FAILURE_CODE);
			resp.setResponseStatus(respStatType);
		} catch (Exception e) {
			NWCGLoggerUtil.Log.info("exception in negative responce");
			e.printStackTrace();
		}
		
		System.out.println("@@@@@ Exiting NWCGInboundSyncMessage::createNegativeResponse @@@@@");
		return getSOAPMessageFromJAXBContextRO(resp);
	}

	private SOAPMessage createSOAPMessageResponse(Document msgDoc) {
		SOAPMessage responseSOAPMessage = makeSOAPMessage(msgDoc);
		return responseSOAPMessage;
	}

	/**
	 * @param string_token
	 */
	private void createAPIFailedAlert(HashMap msgMap) {
		System.out.println("@@@@@ In NWCGInboundSyncMessage::createAPIFailedAlert @@@@@");
		//Creates an Alert in NWCG_FAULT queue with Alert properties AlertSet3.1.3
		NWCGAlert.raiseInternalErrorAlert(msgMap);
	}
}
