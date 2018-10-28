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
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStoreInterface;
import com.nwcg.icbs.yantra.webservice.ob.NWCGOBSyncAsyncProcessor;
import com.nwcg.icbs.yantra.webservice.util.NWCGJAXBContextWrapper;
import com.yantra.yfs.japi.YFSConnectionHolder;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGOutboundSyncMessage extends NWCGOBSyncAsyncProcessor {

	public NWCGOutboundSyncMessage() {
		super();
	}

	/*
	 * Invoked by Sterling App user to deliver Message or Notification
	 * synchronously This method Sends synchronous message to ROSS and receives
	 * response back on the same connection Set message status to SENT_SYNC If
	 * receives non-null response, forward response to Sterling App and set
	 * status to PROCESSED_SYNC. If failed due to communication problems,
	 * message status is set to SENT_SYNC_FAILED If receives reply as Soap
	 * Fault, sets status to SENT_SYNC_FAULT and forward response to the user
	 */
	public Document process(YFSEnvironment env, Document msg) throws Exception {
		System.out.println("@@@@@ Entering NWCGOutboundSyncMessage::process @@@@@");

		setupMessageMap(env, msg);
		setupMessageStore(env);

		Document msgDoc = null;
		Object respObj = null;
		Document resp = XMLUtil.getDocument();
		try {
			// added check because when we are calling this class from ping
			// service the env obj is of type *EnvImpl
			// TODO: debug why ping client is sending env which is not
			// compatible with YFSConnectionHolder
			if (env instanceof YFSConnectionHolder) {
				java.sql.Connection conn = ((YFSConnectionHolder) env)
						.getDBConnection();
				conn.commit();
			}
			msgDoc = createMessageRequest();
			// send msg to ROSS
			// Jay: this is a SOAP response object
			respObj = sendMessageRequest(msgDoc);
			if (respObj != null)
				NWCGLoggerUtil.Log
						.info("NWCGOutboundSyncMessage.process>> response type="
								+ respObj.getClass().getName());

			// this should never throw a NullPointer exception because at this
			// time we have the soapmessage object having

			Node nodeRespBody = getResponseElementFromSOAPBody(((SOAPMessage) respObj)
					.getSOAPBody());// Jay: we are already getting
									// getResponse(respObj);
			// NWCGLoggerUtil.Log.info("NWCGOutboundSyncMessage.process>>
			// response firstChild =" +
			// XMLUtil.extractStringFromNode(nodeRespBody));
			Node nodeResp = resp.importNode(nodeRespBody, true);
			resp.appendChild(nodeResp);
			// NWCGLoggerUtil.Log.info("NWCGOutboundSyncMessage.process>>
			// response Document =" + XMLUtil.extractStringFromDocument(resp));

		} catch (SOAPFaultException sfe) {
			Element rootNode = resp.createElement("Response");
			resp.appendChild(rootNode);

			String soapFaultCode = (String) msgMap.get("Fault Code");
			String soapFaultDesc = (String) msgMap.get("Fault Description");
			StringBuffer sb = new StringBuffer(soapFaultCode);
			sb.append(" - ");
			sb.append(soapFaultDesc);
			rootNode.setAttribute("Message", sb.toString());

			return resp;
		} catch (Exception e) {
			setMsgStatusSentSyncFailed(msgDoc);
			NWCGLoggerUtil.Log
					.warning("NWCGOutboundSyncMessage.process>> Seting status SEND_SYNC_FAILED for ");
			return resp;
		}

		if (resp == null) {
			NWCGLoggerUtil.Log
					.info("NWCGOutboundSyncMessage.process>>got null responce");
			setMsgStatusSentSyncFailed(msgDoc);
		} else {
			setMsgStatusSentSync(resp);
		}

		System.out.println("@@@@@ Exiting NWCGOutboundSyncMessage::process @@@@@");
		return resp;
	}

	protected void setupMessageMap(YFSEnvironment env, Document msg)
			throws Exception {
		System.out.println("@@@@@ Entering NWCGOutboundSyncMessage::setupMessageMap @@@@@");
		
		super.setupMessageMap(env, msg);

		// if the msg contains an IS_SYNC element then it is in the wrong place
		String serviceName = (String) msgMap
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_SERVNAME);
		if (determineIsSync(serviceName) == false) {
			NWCGLoggerUtil.Log
					.finer("NWCGOutboundSyncMessage.setupMessageMap>>Asynchronous Message passed from caller to A&A Synch interface");
			// throw new
			// Exception("NWCGOutboundSyncMessage.setupMessageMap>>Asynchronous
			// Message passed from caller to A&A Synch interface");

		}
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_CUSTOMPROP,
				NWCGAAConstants.CUSTOM_PROPERTIES);
		
		System.out.println("@@@@@ Exiting NWCGOutboundSyncMessage::setupMessageMap @@@@@");
	}

	private Document getResponse(Object obj) {
		System.out.println("@@@@@ Entering NWCGOutboundSyncMessage::getResponse @@@@@");
		/*
		 * DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		 * dbf.setNamespaceAware(true); DocumentBuilder db = null;
		 * 
		 * try { db = dbf.newDocumentBuilder(); } catch
		 * (ParserConfigurationException e1) { // TODO Auto-generated catch
		 * block e1.printStackTrace(); }
		 * 
		 * Document doc = db.newDocument();
		 * 
		 * try { Marshaller marshall = jc.createMarshaller();
		 * marshall.marshal(obj,doc);
		 * 
		 * }catch(JAXBException e){
		 * NWCGLoggerUtil.Log.warning("NWCGOutboundSyncMessage.getResponse>>Got
		 * Exception"); System.out.println("getResponse>>Exception during
		 * unmarshalling"); e.printStackTrace(System.out); return null; }
		 */

		Document doc = null;

		boolean bException = false;

		try {
			// first trying with common types
			doc = new NWCGJAXBContextWrapper().getDocumentFromObject(obj, doc,
					new URL(NWCGAAConstants.COMMON_TYPES_NAMESPACE));
		} catch (Exception e) {
			NWCGLoggerUtil.Log
					.info("Exception occured while casting to Message Ack " + e);
			e.printStackTrace();
			bException = true;
		}

		if (bException) {
			bException = false;
			try {
				// next resource order
				doc = new NWCGJAXBContextWrapper().getDocumentFromObject(obj,
						doc, new URL(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
			} catch (Exception e) {
				NWCGLoggerUtil.Log
						.info("Exception occured while casting to Message Ack "
								+ e);
				e.printStackTrace();
				bException = true;
			}
		}

		if (bException) {
			bException = false;
			try {
				// finally notification
				doc = new NWCGJAXBContextWrapper()
						.getDocumentFromObject(
								obj,
								doc,
								new URL(
										NWCGAAConstants.RESOURCE_ORDER_NOTIFICATION_NAMESPACE));
			} catch (Exception e) {
				NWCGLoggerUtil.Log
						.info("Exception occured while casting to Message Ack "
								+ e);
				e.printStackTrace();
				bException = true;
			}
		}
		
		System.out.println("@@@@@ Exiting NWCGOutboundSyncMessage::getResponse @@@@@");
		return doc;
	}
}