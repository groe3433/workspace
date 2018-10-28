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

package com.nwcg.icbs.yantra.webservice.ib.notification;

import gov.nwcg.services.ross.resource_order_notification._1.DeliverNotificationResp;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.soap.SOAPMessage;
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.business.NWCGBusinessMsgProcessor;
import com.nwcg.icbs.yantra.webservice.ib.NWCGIBWebService;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStore;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStoreInterface;
import com.nwcg.icbs.yantra.webservice.util.alert.NWCGAlert;

public class NWCGIBNotification extends NWCGIBWebService {

	/**
	 * 	1.	A&A port of WebService interface  receives  Notification call from ROSS
	 * 	2.	A&A extracts Soap Header data and creates new record for this Notification with status  RECEIVED.
	 *      Notifications stored with UUID for Distribution id since ROSS does not provide Distribution ID in this case.
	 *  3.	A&A performs authentication and authorization via VerifySecuritySession WebService.
	 *  	   If security verification fails A&A sends Soap Fault response back to ROSS and set status to SOAP_FAULT. A&A creates an Alert in NWCG_INBOUND_AUTHORIZATION_FAILED queue. Alert properties are AlertSet3.2.1
	 *	4.	If security verification passed A&A forward Notification to JMS queue for Sterling App to process .
	 *  	If there are no errors from Sterling A&A returns DeliverNotificationResp  and set message status to PROCESSED
	 *  5.	If A&A receives error or exception from Sterling App, it set status to PROCESSING_FAULT and
	 *  	send Negative response to ROSS. A&A creates an Alert in NWCG_FAULT queue with Alert properties AlertSet3.1.3
	 *  @return SOAPMessage Synchronous DeliverNotificationResp response object
	 *  				    returned to ROSS. Only indicates that ICBS received the
	 *  					notification, no functional response. 
	 */
	public SOAPMessage process() {
		System.out.println("@@@@@ Entering NWCGIBNotification::process @@@@@");

		SOAPMessage response = null;
		NWCGMessageStoreInterface messageStore = NWCGMessageStore
				.getMessageStore();

		// Ignoring the processSynchronously=true/false value for the customProperty Element
		// in the SOAPHeader of the IB Notification
		/*(if (super.isSync()) {
		 
		 SOAPFault sf = createSOAPFault("processSynchronously=true for IB Notification");
		 NWCGLoggerUtil.Log.severe("ICBSR received IB Notification with customProperty set to processSynchronously=true");
		 throw new SOAPFaultException(sf);
		 }*/

		try {
			//Initialize the message map containing values from the wsContext
			msgMap = setupMessageMap();

			//Create START / VOID and LATEST / RECEIVED
			//NWCG_INBOUND_MESSAGE records. Default distribution ID from UUID
			messageStore.initInboundMessageFromMap(msgMap);
		} catch (Exception e) {
			NWCGLoggerUtil.Log.severe("Failure to initialize IB message store");
			e.printStackTrace();

			response = handleInternalProcessingFault();
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
				//Post message to Sterling JMS queue for consumption by integration service
				new NWCGBusinessMsgProcessor()
						.postIncomingNotificationToJMS(msgMap);

				//Create the positive DeliverNotificationResp object
				DeliverNotificationResp resp = getDeliverNotificationResp(true);

				//Marshall the DeliverNotificationResp object to a Document
				Document responseXML = getMarshalledObject(resp, new URL(
						NWCGAAConstants.RESOURCE_ORDER_NOTIFICATION_NAMESPACE));
				String responseXMLString = XMLUtil
						.extractStringFromDocument(responseXML);

				//Only want to set LATEST to PROCESSED after posting to JMS queue
				messageStore.setMsgStatusInboundProcessed(msgMap,
						responseXMLString);

				response = makeSOAPMessage(responseXML);
			}
		} catch (SOAPFaultException sfe) {
			//SOAPFault thrown by verifyMessageSecurity on 
			// - empty password
			// - failure to validate user/pass
			// - unable to post to ROSS authentication webservice
			handleSecurityFailureSoapFault(sfe);
		} catch (Exception e) {
			NWCGLoggerUtil.Log.severe("Exception from Sterling API="
					+ e.getMessage());
			e.printStackTrace();

			response = handleInternalProcessingFault();
		}
		
		System.out.println("@@@@@ Exiting NWCGIBNotification::process @@@@@");
		return response;
	}

	//creates alert in IB_NOTIFICATION queue when notification is successfully processed
	@Override
	protected void raiseNotificationAlert(String messageBody, String distID) {
		System.out.println("@@@@@ Entering NWCGIBNotification::raiseNotificationAlert @@@@@");

		String msgName = (String) msgMap
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME);
		NWCGLoggerUtil.Log
				.info("raiseNotificationAlert>>success; processed notification="
						+ msgName);
		//properties for populating Alert screen in UI
		NWCGAlert.raiseNotificationAlert(msgName, messageBody, distID);
		
		System.out.println("@@@@@ Exiting NWCGIBNotification::raiseNotificationAlert @@@@@");
	}

	protected SOAPMessage handleInternalProcessingFault() {
		System.out.println("@@@@@ Entering NWCGIBNotification::handleInternalProcessingFault @@@@@");
		
		DeliverNotificationResp delNotResp = null;
		SOAPMessage responseSOAPMsg = null;

		//Setup Sterling Alert
		raiseNotificationAlert((String) msgMap
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY),
				(String) msgMap
						.get(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID));

		//Return the negative acknowledgement message DeliverNotificationResp object
		delNotResp = getDeliverNotificationResp(false);

		//Marshall the DeliverNotificationResp object to a Document object
		Document responseXML = null;
		try {
			responseXML = getMarshalledObject(delNotResp, new URL(
					NWCGAAConstants.RESOURCE_ORDER_NOTIFICATION_NAMESPACE));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		String responseXMLString = XMLUtil.getXMLString(responseXML);

		//Update LATEST IB message store record to PROCESSING_FAULT and w/the responseXML
		messageStore.setMsgStatusProcessingFault(msgMap, responseXMLString);

		responseSOAPMsg = makeSOAPMessage(responseXML);

		System.out.println("@@@@@ Exiting NWCGIBNotification::handleInternalProcessingFault @@@@@");
		return responseSOAPMsg;
	}
}