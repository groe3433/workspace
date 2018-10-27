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

package com.nwcg.icbs.yantra.soap;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;

public class NWCGMessageFactory {

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGMessageFactory.class);

	public static NWCGSOAPMsg newAckMessage(String type) {
		logger.verbose("@@@@@ In NWCGMessageFactory::newAckMessage");
		return newMessage(type + "_ACK");
	}

	public static NWCGSOAPMsg newMessage(String type) {
		logger.verbose("@@@@@ Entering NWCGMessageFactory::newMessage");
		try {
			if (type.equals(NWCGAAConstants.GET_INCIDENT_REQ_MSG_NAME)) {
				logger.verbose("@@@@@ Exiting NWCGMessageFactory::newMessage (NWCGGetIncidentReqMsg)");
				return new NWCGGetIncidentReqMsg();
			}
			if (type.equals("AuthUserReq-IB")) {
				logger.verbose("@@@@@ Exiting NWCGMessageFactory::newMessage (NWCGIBAuthReqMsg)");
				return new NWCGIBAuthReqMsg();
			}
			if (type.equals("AuthUserResp-OB")) {
				logger.verbose("@@@@@ Exiting NWCGMessageFactory::newMessage (NWCGOBAuthRespMsg)");
				return new NWCGOBAuthRespMsg();
			}
			if (type.equals("ResponseStatusType")) {
				logger.verbose("@@@@@ Exiting NWCGMessageFactory::newMessage (NWCGResponseStatusTypeReq)");
				return new NWCGResponseStatusTypeReq();
			}
			if (type.equals("ACK")) {
				logger.verbose("@@@@@ Exiting NWCGMessageFactory::newMessage (NWCGIBAckMsg)");
				return new NWCGIBAckMsg();
			}
			logger.verbose("@@@@@ Exiting NWCGMessageFactory::newMessage (NWCGSOAPMsg)");
			return new NWCGSOAPMsg();
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception..." + e);
		}
		NWCGSOAPMsg msg = new NWCGSOAPMsg();
		msg.setServiceName(type);
		logger.verbose("@@@@@ Exiting NWCGMessageFactory::newMessage (end)");
		return msg;
	}

	public static String getServiceName(Document docMsg) {
		logger.verbose("@@@@@ Entering NWCGMessageFactory::getServiceName");
		String serviceName = "";
		try {
			NodeList nL = docMsg.getElementsByTagName("messageName");
			Node nMC = nL.item(0);
			String messageName = nMC.getFirstChild().getNodeValue();
			logger.verbose("@@@@@ Exiting NWCGMessageFactory::getServiceName (1)");
			return messageName;
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception..." + e);
		}
		logger.verbose("@@@@@ Exiting NWCGMessageFactory::getServiceName (end)");
		return serviceName;
	}
}