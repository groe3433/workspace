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

package com.nwcg.icbs.yantra.commCore;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.handler.NWCGMessageHandler;
import com.nwcg.icbs.yantra.soap.NWCGAAUtil;
import com.nwcg.icbs.yantra.soap.NWCGIBAuthReqMsg;
import com.nwcg.icbs.yantra.soap.NWCGMessageFactory;
import com.nwcg.icbs.yantra.soap.NWCGSOAPMsg;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;

public class NWCGIBNotificationWebService {

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGIBNotificationWebService.class);

	public Document recieveMsg(YFSEnvironment env, Document xml) throws Exception {
		logger.verbose("@@@@@ Entering NWCGIBNotificationWebService::recieveMsg");
		String serviceName = getNotificationMessageName(xml);
		String distID = NWCGAAUtil.lookupNodeValue(xml, "distributionID");
		String systID = NWCGAAUtil.lookupNodeValue(xml, "systemID");
		logger.verbose("@@@@@ ServiceName:" + serviceName);
		NWCGMessageStore msgStore = NWCGMessageStore.getMessageStore();
		// Create the start message
		String system_key = msgStore.storeMessage(systID, "IB", NWCGAAUtil.serialize(xml), NWCGAAConstants.MESSAGE_TYPE_START, NWCGAAConstants.MESSAGE_STATUS_VOID, NWCGAAConstants.EXTERNAL_SYSTEM_NAME, "");
		String latest_system_key = msgStore.storeMessage(systID, "IB", NWCGAAUtil.serialize(xml), NWCGAAConstants.MESSAGE_TYPE_LATEST, NWCGAAConstants.MESSAGE_STATUS_MESSAGE_RECEIVED, NWCGAAConstants.EXTERNAL_SYSTEM_NAME, "");
		boolean isSync = determineIsSync(serviceName);
		NWCGSOAPMsg msg = NWCGMessageFactory.newMessage(serviceName);
		msg.setXml(NWCGAAUtil.serialize(xml));
		// Note: This msg should be an instance of NWGCOBAuthReqMsg
		// Check to see if the request requires auth. A notification and ASYNC message response do not require auth
		if (reqRequiresAuth(serviceName)) {
			msgStore.updateMessage(systID, "IB", NWCGAAUtil.serialize(xml), NWCGAAConstants.MESSAGE_TYPE_LATEST, NWCGAAConstants.MESSAGE_STATUS_AWAITING_AUTHORIZATION, NWCGAAConstants.EXTERNAL_SYSTEM_NAME, latest_system_key, true);
			if (!sendAuthRequest(xml)) {
				msgStore.updateMessage(systID, "IB", NWCGAAUtil.serialize(xml), NWCGAAConstants.MESSAGE_TYPE_LATEST, NWCGAAConstants.MESSAGE_STATUS_AUTHORIZATION_FAILED, NWCGAAConstants.EXTERNAL_SYSTEM_NAME, latest_system_key, true);
			}
			// create AUTH request For inbound, create request, send, and then wait
		}
		msgStore.updateMessage(systID, "IB", NWCGAAUtil.serialize(xml), NWCGAAConstants.MESSAGE_TYPE_LATEST, NWCGAAConstants.MESSAGE_STATUS_PROCESSING, NWCGAAConstants.EXTERNAL_SYSTEM_NAME, latest_system_key, true);
		if (isSync) {
			// Create a Handler Factory
			NWCGMessageHandler handler = NWCGMessageHandlerFactory.createHandler("", serviceName);
			NWCGSOAPMsg resp_msg = handler.process(msg);
			String resp_msg_str = "";
			try {
				resp_msg_str = NWCGAAUtil.buildSOAP(resp_msg);
				msgStore.updateMessage(systID, "IB", NWCGAAUtil.serialize(xml), NWCGAAConstants.MESSAGE_TYPE_LATEST, NWCGAAConstants.MESSAGE_STATUS_PROCESSED, NWCGAAConstants.EXTERNAL_SYSTEM_NAME, latest_system_key, true);
			} catch (Exception e) {
				logger.error("!!!!! Caught General Exception :: " + e.toString());
			}
			// convert this string to a document
			logger.verbose("@@@@@ resp_msg_str:" + resp_msg_str);
			logger.verbose("@@@@@ Exiting NWCGIBNotificationWebService::recieveMsg (1)");
			return NWCGAAUtil.buildXMLDocument(resp_msg_str);
		}
		// place message in queue
		CommonUtilities.invokeService(env, NWCGAAConstants.SDF_POST_SOAP_SERVICE_NAME, xml);
		// send acknowledgement
		NWCGSOAPMsg ack = NWCGMessageFactory.newMessage("ACK");
		ack.setUsername("");
		ack.setDistId(systID);
		ack.buildSOAPMsg();
		logger.verbose("@@@@@ Exiting NWCGIBNotificationWebService::recieveMsg");
		return NWCGAAUtil.buildXMLDocument(NWCGAAUtil.buildSOAP(ack));
	}

	boolean determineIsSync(String serviceName) {
		logger.verbose("@@@@@ In NWCGIBNotificationWebService::determineIsSync");
		String isSync = NWCGProperties.getProperty(serviceName + ".isSync");
		return isSync.equalsIgnoreCase("TRUE");
	}

	boolean sendAuthRequest(Document xml) {
		logger.verbose("@@@@@ Entering NWCGIBNotificationWebService::sendAuthRequest");
		NWCGIBAuthReqMsg authReqMsg = (NWCGIBAuthReqMsg) NWCGMessageFactory.newMessage("AuthUserReq-OB");
		String username = NWCGAAUtil.lookupNodeValue(xml, "username");
		String password = NWCGAAUtil.lookupNodeValue(xml, "password");
		authReqMsg.setUsername(username);
		authReqMsg.setPassword(password);
		String respMsg = "NONE";
		try {
			authReqMsg.buildSOAPMsg();
			String auth_resp = NWCGAAUtil.buildAndSend(authReqMsg, "ICBS-IB");
			String returnCode = NWCGAAUtil.lookupNodeValue(NWCGAAUtil.buildXMLDocument(auth_resp), "ReturnCode");
			if (!returnCode.equals("0")) {
				// auth failed
			}
			return true;
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e.toString());
		}
		logger.verbose("@@@@@ Exiting NWCGIBNotificationWebService::sendAuthRequest");
		return false;
	}

	boolean reqRequiresAuth(String serviceName) {
		logger.verbose("@@@@@ In NWCGIBNotificationWebService::reqRequiresAuth");
		String requiresAuth = NWCGProperties.getProperty(serviceName + ".requiresAuth");
		return requiresAuth.equalsIgnoreCase("TRUE");
	}

	public String getNotificationMessageName(Document doc) {
		logger.verbose("@@@@@ Entering NWCGIBNotificationWebService::getNotificationMessageName");
		Iterator i = NWCGProperties.notifyMsg.iterator();
		while (i.hasNext()) {
			String msgName = (String) i.next();
			NodeList nL = doc.getElementsByTagName(msgName);
			if (nL.item(0) != null) {
				logger.verbose("@@@@@ Entering NWCGIBNotificationWebService::getNotificationMessageName (msgName)");
				return msgName;
			}
		}
		logger.verbose("@@@@@ Entering NWCGIBNotificationWebService::getNotificationMessageName (null)");
		return null;
	}
}