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

import gov.nwcg.services.ross.common_types._1.MessageAcknowledgement;

import java.net.URL;
import java.util.HashMap;

import javax.xml.soap.SOAPMessage;
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.business.NWCGBusinessMsgProcessor;
import com.nwcg.icbs.yantra.webservice.ib.NWCGIBWebService;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStore;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStoreInterface;
import com.yantra.yfs.japi.YFSException;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

/**
 * ICBS A&A class for handling Inbound (to ICBS) Asynchronous messages
 * @author drodriguez
 */
public class NWCGInboundASyncMessage extends NWCGIBWebService {
	
	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGInboundASyncMessage.class);

	/**
	 * Pre-conditions:
	 * WebService interface  receives asynchronous call from ROSS
	 * Extracted Soap Header data are in context 
	 * This method updates message status to  RECEIVED
	 *    -Performs authentication and authorization via VerifySecuritySession WebService
	 *    -If security verification passed, places message to JMS queue for Sterling App to process .
	 *    -If receives error or exception from Sterling App, it set status to PROCESSING_FAULT and send Negative response to ROSS. 
	 *     A&A creates an Alert in NWCG_FAULT queue with Alert properties AlertSet3.1.3 
	 *    -If no errors from Sterling then sends Message Acknowledgement to ROSS and set message status to SEND_FOR_PROCESSING
	 *    -If security verification fails sends Soap Fault response back to ROSS and set status to AUTHORIZATION_FAILED. 
	 *     Creates an Alert in NWCG_INBOUND_AUTHORIZATION_FAILED queue. Alert properties are AlertSet3.2.1
	 *    
	 *    FUTURE enh:
	 *   (TBD)If receives message that is an error or exception from ROSS, it sets status to 
	 *   ACKNOLEDGEMENT_NEGATIVE and sends negative Message Acknowledgement response to ROSS 
	 *   without processing by Sterling App (???) 
	 *   A&A creates an Alert in NWCG_INBOUND_NEGATIVE queue. Alert property is AlertSet3.2.1a
	 *   
	 */
	public SOAPMessage process() {
		logger.verbose("@@@@@ Entering NWCGInboundASyncMessage::process");
		SOAPMessage response = null;
		NWCGMessageStoreInterface messageStore = NWCGMessageStore.getMessageStore();
		try {
			// Initialize the message map containing values from the messageContext
			msgMap = setupMessageMap();
			//Create START / VOID and LATEST / RECEIVED NWCG_INBOUND_MESSAGE records. Default distribution ID from UUID
			messageStore.initInboundMessageFromMap(msgMap);
		} catch (Exception e) {
			logger.error("!!!!! Failure to initialize IB message store");
			logger.error(e.getLocalizedMessage(), e);
			response = handleInternalProcessingFault(e);
			logger.error("!!!!! Exiting NWCGInboundASyncMessage::process (initialize)");
			return response;
		}
		try {
			// Determine whether we need to call verifySecuritySession for this message name. Pulls from <messageName.requiresAuth in A&A Impl properties
			boolean requiresAuth = requiresSecurityAuthentication();
			boolean securityVerified = false;
			// Right now, verifyMesssageSecurity() will never return false because it will throw up an exception before returning a false boolean
			if (requiresAuth) {
				securityVerified = verifyMessageSecurity();
			}
			if (securityVerified || !requiresAuth) {
				// Post message to Sterling JMS queue for consumption by integration service
				new NWCGBusinessMsgProcessor().postIncomingMsgToJMS(msgMap);
				// Create the positive MessageAcknowledgement object
				MessageAcknowledgement ack = getMessageAcknowledgement((String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID), true);
				// Marshall the MessageAcknowledgement object to a Document
				Document responseXML = null;
				responseXML = getMarshalledObject(ack, new URL(NWCGAAConstants.COMMON_TYPES_NAMESPACE));
				String responseXMLString = XMLUtil.extractStringFromDocument(responseXML);
				//Only want to set LATEST to PROCESSED after posting to JMS queue
				messageStore.setMsgStatusSentForProcessing(msgMap, responseXMLString);
				response = makeSOAPMessage(responseXML);
			}
		} catch (YFSException yfe) {
			logger.error("!!!!! YFSException received by A&A from Sterling",yfe);
			response = handleInternalProcessingFault(yfe);
		} catch (SOAPFaultException sfe) {
			// SOAPFault thrown by verifyMessageSecurity on 
			// - empty password
			// - failure to validate user/pass
			// - unable to post to ROSS authentication webservice
			handleSecurityFailureSoapFault(sfe);
		} catch (Exception e) {
			logger.error("!!!!! Exception from Sterling API=" + e.getMessage(),e);
			response = handleInternalProcessingFault(e);
		}
		logger.verbose("@@@@@ Exiting NWCGInboundASyncMessage::process");
		return response;
	}

	@Override
	protected HashMap<Object, Object> setupMessageMap() throws Exception {
		logger.verbose("@@@@@ In NWCGInboundASyncMessage::setupMessageMap");
		HashMap<Object, Object> msgMap = super.setupMessageMap();
		if (super.isSync() == true)
			throw new Exception("setupMessageMap>>Synchronous Message passed from caller to A&A ASynch service");
		return msgMap;
	}
}