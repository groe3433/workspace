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

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.handler.NWCGMessageHandler;
import com.nwcg.icbs.yantra.handler.NWCGOBAuthMessageHandler;
import com.nwcg.icbs.yantra.handler.NWCGResponseStatusTypeHandler;
import com.nwcg.icbs.yantra.soap.NWCGAAUtil;
import com.nwcg.icbs.yantra.soap.NWCGSOAPMsg;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;

/**
 * 1. store message using Sterling DB. output of this will entire message
 * with key 2. create a JMS message msg, and embeed key in message 3. place
 * message on key 4. respond with ack to ross that contains the message
 * response key
 */
public class NWCGIBAuthWebService {

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGIBAuthWebService.class);

	public Document recieveMsg(YFSEnvironment env, Document xml) throws Exception {
		logger.verbose("@@@@@ Entering NWCGIBAuthWebService::recieveMsg");
		// Note: This msg should be an instance of NWGCOBAuthReqMsg
		NWCGSOAPMsg msg = new NWCGSOAPMsg();
		msg.setXml(NWCGAAUtil.serialize(xml));
		NWCGMessageHandler handler = NWCGMessageHandlerFactory.createHandler(NWCGAAConstants.AUTH_SERVICE_GROUP_NAME, NWCGAAConstants.AUTH_USER_REQ_IB_SERVICE_NAME);
		NWCGSOAPMsg resp_msg = handler.process(msg);
		String resp_msg_str = "";
		try {
			resp_msg_str = NWCGAAUtil.buildSOAP(resp_msg);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e.toString());
		}
		logger.verbose("@@@@@ Exiting NWCGIBAuthWebService::recieveMsg");
		return NWCGAAUtil.buildXMLDocument(resp_msg_str);
	}

	boolean reqRequiresAuth(String serviceName) {
		logger.verbose("@@@@@ In NWCGIBAuthWebService::reqRequiresAuth");
		// Depending on the service name, we determine if auth is required this will be in a prop file Check for notification, auth, get results
		return false;
	}

	NWCGMessageHandler getHandler(String serviceName) {
		logger.verbose("@@@@@ Entering NWCGIBAuthWebService::getHandler");
		logger.verbose("@@@@@ getHandler.ServiceName: " + serviceName);
		if (serviceName.equals("AuthUserReq-IB")) {
			logger.verbose("@@@@@ Exiting NWCGIBAuthWebService::getHandler (1)");
			return new NWCGOBAuthMessageHandler();
		}
		if (serviceName.equals("ResponseStatusType")) {
			logger.verbose("@@@@@ Exiting NWCGIBAuthWebService::getHandler (2)");
			return new NWCGResponseStatusTypeHandler();
		}
		return new NWCGMessageHandler();
	}
}