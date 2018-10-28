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

import gov.nwcg.services.ross.common_types._1.MessageAcknowledgement;
import gov.nwcg.services.ross.common_types._1.ResponseStatusType;

import java.net.URL;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStoreInterface;
import com.nwcg.icbs.yantra.webservice.ob.NWCGOBSyncAsyncProcessor;
import com.nwcg.icbs.yantra.webservice.util.alert.NWCGAlert;
import com.yantra.yfs.japi.YFSConnectionHolder;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGOutboundAsyncMessage extends NWCGOBSyncAsyncProcessor {
	
	private static YFCLogCategory logger = NWCGROSSLogger
			.instance(NWCGOutboundAsyncMessage.class);

	public NWCGOutboundAsyncMessage() {
		super();
	}

	/*
	 * 1. Message is received by A&A framework from Sterling App via JMS queue
	 * to the class that invokes this method 2. A&A creates new records in the
	 * Message Store with initial status VOID and distribution id set to random
	 * UUID 3. A&A builds Soap Envelop including Soap Headers and send Message
	 * in the Soap Body 4. ROSS system is performing synchronous call to ICSBR
	 * to authenticate the message before it sends acknowledgment (not part of
	 * A&A) 5. If Positive Message Acknowledgement is received, A&A updates
	 * Latest record in the Message Store and set message status to SENT and
	 * Distribution Id to Distribution Id received from ROSS 6. If Soap Fault or
	 * Negative Message Acknowledgement is received, A&A creates record in the
	 * Message Store and set message status to FAULT (see Message Store section
	 * 5.4.2). The reason for the fault is provided by ROSS in the Soap Fault
	 * message status code (see Assumption section 3.1) A&A creates an Alert in
	 * NWCG_FAULT queue, referencing message name as Alert type. (AlertSet
	 * 3.1.1) 7. If A&A receives Exception back indicating Communication failure
	 * or any other exceptions occurred, A&A sets message status to SENT_FAILED
	 * (these messages will be re-sent to ROSS by retry described in
	 * use-scenario 4)
	 */
	public MessageAcknowledgement process(YFSEnvironment env, Document msg) throws Exception {
		logger.verbose("@@@@@ Entering NWCGOutboundAsyncMessage::process");
		logger.verbose("@@@@@ The incoming doc from JMS Q:\n" + XMLUtil.getXMLString(msg));
		setupMessageMap(env, msg);
		setupMessageStore(env);
		logger.verbose("@@@@@ NWCGOutboundAsyncMessage.process, Message Map : " + msgMap.toString());
		MessageAcknowledgement msgAck = null;
		Object respObj = null;
		Object unmarshalledReturnObject = null;
		// now need to invoke web service and post this message.
		try {
			// Before we invoke the ROSS web service we need to commit all transactions in database for user authorization.
			java.sql.Connection conn = ((YFSConnectionHolder) env).getDBConnection();
			conn.commit();

			logger.verbose("@@@@@ Sending THIS message to ROSS :: msg : " + XMLUtil.getXMLString(msg));
			respObj = sendMessageRequest(msg);
			if (respObj != null && respObj instanceof SOAPMessage) {
				SOAPMessage soapMsgResponse = (SOAPMessage) respObj;
				logger.verbose("@@@@@ After soapMsgResponse");
				SOAPBody soapBody = soapMsgResponse.getSOAPBody();
				logger.verbose("@@@@@ After soapBody");
				Document docMsgAck = soapBody.extractContentAsDocument();
				logger.verbose("@@@@@ After extractContentAsDocument");
				unmarshalledReturnObject = getUnmarshalledObject(docMsgAck, new URL(NWCGAAConstants.COMMON_TYPES_NAMESPACE));
				logger.verbose("@@@@@ After getUnmarshalledObject.....");
			}

			logger.verbose("@@@@@ Before if...");
			if (unmarshalledReturnObject != null && unmarshalledReturnObject instanceof MessageAcknowledgement) {
				logger.verbose("@@@@@ Before unmarshalledReturnObject :: " + unmarshalledReturnObject.toString());
				msgAck = (MessageAcknowledgement) unmarshalledReturnObject;
				logger.verbose("@@@@@ After setting msgAck :: " + msgAck.toString());
			}
		} catch (Exception e) {
			logger.error("!!!!! NWCGOBMsgService.processAsync>>exception in OB processing; returning",e);
		}

		if (msgAck != null) {
			ResponseStatusType rst = msgAck.getResponseStatus();
			int returnCode = rst.getReturnCode();
			if (returnCode == -1) {
				NWCGAlert.createReceivedFaultAlert(msgMap, null);
			}
		}

		java.sql.Connection conn = ((YFSConnectionHolder) env).getDBConnection();
		conn.commit();
		
		logger.verbose("@@@@@ Exiting NWCGOutboundAsyncMessage::process");
		return msgAck;
	}

	protected void setupMessageMap(YFSEnvironment env, Document msg) throws Exception {
		logger.verbose("@@@@@ Entering NWCGOutboundAsyncMessage::setupMessageMap");
		super.setupMessageMap(env, msg);
		// if the msg contains an IS_SYNC element then it is in the wrong place
		String serviceName = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_SERVNAME);
		if (determineIsSync(serviceName) == true)
			throw new Exception("NWCGOBMsgService.processAsync>>Asynchronous Message passed from caller to A&A Synch interface"); 
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_CUSTOMPROP, "processSynchronously=false");
		logger.verbose("@@@@@ Exiting NWCGOutboundAsyncMessage::setupMessageMap");
	}
}