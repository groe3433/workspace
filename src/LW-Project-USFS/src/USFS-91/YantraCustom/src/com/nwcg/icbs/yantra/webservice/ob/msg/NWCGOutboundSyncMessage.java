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

import java.net.URL;

import javax.xml.soap.SOAPMessage;
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStoreInterface;
import com.nwcg.icbs.yantra.webservice.ob.NWCGOBSyncAsyncProcessor;
import com.nwcg.icbs.yantra.webservice.util.NWCGJAXBContextWrapper;
import com.yantra.yfs.japi.YFSConnectionHolder;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGOutboundSyncMessage extends NWCGOBSyncAsyncProcessor {

	private static YFCLogCategory logger = NWCGROSSLogger
			.instance(NWCGOutboundSyncMessage.class);

	public NWCGOutboundSyncMessage() {
		super();
	}
	
	/**
	 * Invoked by Sterling App user to deliver Message or Notification
	 * synchronously This method Sends synchronous message to ROSS and receives
	 * response back on the same connection Set message status to SENT_SYNC If
	 * receives non-null response, forward response to Sterling App and set
	 * status to PROCESSED_SYNC. If failed due to communication problems,
	 * message status is set to SENT_SYNC_FAILED If receives reply as Soap
	 * Fault, sets status to SENT_SYNC_FAULT and forward response to the user
	 * 
	 * @param env
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public Document process(YFSEnvironment env, Document msg) throws Exception {
		logger.verbose("@@@@@ Entering NWCGOutboundSyncMessage:process");
		setupMessageMap(env, msg);
		setupMessageStore(env);
		Document msgDoc = null;
		Object respObj = null;
		Document resp = XMLUtil.getDocument();
		try {
			// added check because when we are calling this class from ping service the env obj is of type *EnvImpl
			// TODO: debug why ping client is sending env which is not compatible with YFSConnectionHolder
			if (env instanceof YFSConnectionHolder) {
				logger.verbose("@@@@@ Unsure why we are checking this...");
				java.sql.Connection conn = ((YFSConnectionHolder) env).getDBConnection();
				conn.commit();
			}
			logger.verbose("@@@@@ Before Creating Message for ROSS...");
			msgDoc = createMessageRequest();
			// send msg to ROSS
			logger.verbose("@@@@@ Sending THIS message to ROSS :: msgDoc : " + XMLUtil.getXMLString(msgDoc));
			respObj = sendMessageRequest(msgDoc);
			logger.verbose("@@@@@ Getting Response back from sending THAT message to ROSS...");
			if (respObj != null)
				logger.error("!!!!! NWCGOutboundSyncMessage.process>> response type=" + respObj.getClass().getName());
			// this should never throw a NullPointer exception because at this time we have the soapmessage object having
			Node nodeRespBody = getResponseElementFromSOAPBody(((SOAPMessage) respObj).getSOAPBody());
			Node nodeResp = resp.importNode(nodeRespBody, true);
			resp.appendChild(nodeResp);
		} catch (SOAPFaultException sfe) {
			Element rootNode = resp.createElement("Response");
			resp.appendChild(rootNode);
			String soapFaultCode = (String) msgMap.get("Fault Code");
			String soapFaultDesc = (String) msgMap.get("Fault Description");
			StringBuffer sb = new StringBuffer(soapFaultCode);
			sb.append(" - ");
			sb.append(soapFaultDesc);
			rootNode.setAttribute("Message", sb.toString());
			logger.error("!!!!! Caught SOAPFaultException :: " + sfe);
			logger.error("!!!!! SOAPFaultException :: resp : " + XMLUtil.getXMLString(resp));
			return resp;
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			setMsgStatusSentSyncFailed(msgDoc);
			logger.error("!!!!! Exception :: resp : " + XMLUtil.getXMLString(resp));
			return resp;
		}
		if (resp == null) {
			logger.error("!!!!! NWCGOutboundSyncMessage.process>>got null response");
			setMsgStatusSentSyncFailed(msgDoc);
		} else {
			logger.verbose("@@@@@ resp : " + XMLUtil.getXMLString(resp));
			setMsgStatusSentSync(resp);
		}
		logger.verbose("@@@@@ FINAL resp : " + XMLUtil.getXMLString(resp));
		logger.verbose("@@@@@ Exiting NWCGOutboundSyncMessage:process");
		return resp;
	}

	/**
	 * 
	 */
	protected void setupMessageMap(YFSEnvironment env, Document msg) throws Exception {
		logger.verbose("@@@@@ Entering NWCGOutboundSyncMessage:setupMessageMap");
		super.setupMessageMap(env, msg);
		// if the msg contains an IS_SYNC element then it is in the wrong place
		String serviceName = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_SERVNAME);
		logger.verbose("@@@@@ serviceName :: " + serviceName);
		if (determineIsSync(serviceName) == false) {
			logger.verbose("!!!!! NWCGOutboundSyncMessage.setupMessageMap>>Asynchronous Message passed from caller to A&A Synch interface");
		}
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_CUSTOMPROP, NWCGAAConstants.CUSTOM_PROPERTIES);
		logger.verbose("@@@@@ Exiting NWCGOutboundSyncMessage:setupMessageMap");
	}

	/**
	 * 
	 * @param obj
	 * @return
	 */
	private Document getResponse(Object obj) {
		logger.verbose("@@@@@ Entering NWCGOutboundSyncMessage::getResponse");
		Document doc = null;
		boolean bException = false;
		try {
			// first trying with common types
			logger.verbose("@@@@@ NWCGAAConstants.COMMON_TYPES_NAMESPACE :: " + NWCGAAConstants.COMMON_TYPES_NAMESPACE);
			doc = new NWCGJAXBContextWrapper().getDocumentFromObject(obj, doc, new URL(NWCGAAConstants.COMMON_TYPES_NAMESPACE));
		} catch (Exception e) {
			logger.error("!!!!! Exception occured while casting to Message Ack " + e);
			logger.error(e.getLocalizedMessage(), e);
			bException = true;
		}
		if (bException) {
			bException = false;
			try {
				// next resource order
				logger.verbose("@@@@@ NWCGAAConstants.RESOURCE_ORDER_NAMESPACE :: " + NWCGAAConstants.RESOURCE_ORDER_NAMESPACE);
				doc = new NWCGJAXBContextWrapper().getDocumentFromObject(obj, doc, new URL(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
			} catch (Exception e) {
				logger.error("!!!!! Exception occured while casting to Message Ack " + e);
				logger.error(e.getLocalizedMessage(), e);
				bException = true;
			}
		}
		if (bException) {
			bException = false;
			try {
				// finally notification
				logger.verbose("@@@@@ NWCGAAConstants.RESOURCE_ORDER_NOTIFICATION_NAMESPACE :: " + NWCGAAConstants.RESOURCE_ORDER_NOTIFICATION_NAMESPACE);
				doc = new NWCGJAXBContextWrapper().getDocumentFromObject(obj, doc, new URL(NWCGAAConstants.RESOURCE_ORDER_NOTIFICATION_NAMESPACE));
			} catch (Exception e) {
				logger.error("!!!!! Exception occured while casting to Message Ack " + e);
				logger.error(e.getLocalizedMessage(), e);
				bException = true;
			}
		}
		logger.verbose("@@@@@ Exiting NWCGOutboundSyncMessage::getResponse");
		return doc;
	}
}