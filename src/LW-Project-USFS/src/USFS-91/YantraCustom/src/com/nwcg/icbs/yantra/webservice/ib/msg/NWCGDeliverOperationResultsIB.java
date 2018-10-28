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

import gov.nwcg.services.ross.resource_order._1.DeliverOperationResultsReq;
import gov.nwcg.services.ross.resource_order._1.DeliverOperationResultsResp;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.commCore.NWCGTimeKeyManager;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStoreInterface;
import com.nwcg.icbs.yantra.webservice.ob.NWCGOBSyncAsyncProcessor;
import com.nwcg.icbs.yantra.webservice.util.NWCGJAXBContextWrapper;
import com.nwcg.icbs.yantra.webservice.util.alert.NWCGAlert;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGDeliverOperationResultsIB extends NWCGOBSyncAsyncProcessor {
	
	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGDeliverOperationResultsIB.class);

	public DeliverOperationResultsResp process(YFSEnvironment env, DeliverOperationResultsReq inDoc) throws Exception {
		logger.verbose("@@@@@ In NWCGDeliverOperationResultsIB::process");
		DeliverOperationResultsResp resp = null;
		return resp;
	}

	/*
	 *  This method is invoked by Sterling App for the following:
	 *	ICSBR post synchronous DeliverOperationResult to ROSS for each inbound asynchronous message received. 
	 *   this method is invoked by Sterling App to initiate WebService call and passed Response(in ROSS XML)  
	 * 
	 * it posts request and if gets Response successfully status is set to PROCESSED
	 * If connection failed due to communication problems, message status is set to FAILED_READY_FOR_PICKUP
	 * If post Response successfully but receives Soap Fault back it sets status to FAULT_READY_FOR_PICKUP 
	 */
	public DeliverOperationResultsResp process(YFSEnvironment env, Document inDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGDeliverOperationResultsIB::process (2)");
		logger.verbose("@@@@@ NWCGDeliverOperationResults.deliverOperationResults method>>" + XMLUtil.extractStringFromDocument(inDoc));
		msgMap = setupDeliverMessageMap(env, inDoc);
		getKeyFromMessageStore();
		Document reqDoc = createDeliverOperationResultsReq(inDoc);
		DeliverOperationResultsResp resp = null;
		SOAPBody respBody = null;
		Document deliverOperationResultsResp = null;
		try {
			SOAPMessage deliverOprResp = (SOAPMessage) sendMessageRequest(reqDoc);
			respBody = deliverOprResp.getSOAPBody();
			deliverOperationResultsResp = respBody.extractContentAsDocument();
			resp = (DeliverOperationResultsResp) new NWCGJAXBContextWrapper().getObjectFromDocument(deliverOperationResultsResp, new URL(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));//getUnmarshalledObject(respBody.getOwnerDocument());
		} catch (SOAPFaultException sfe) {
			setMsgStatusFailedReadyForPickup(inDoc);
			logger.error("!!!!! SOAPFault returned, exception thrown >>" + sfe.toString());
		} catch (Exception e) {
			setMsgStatusFailedReadyForPickup(inDoc);
		}
		if (deliverOperationResultsResp != null && resp != null) {
			setMsgStatusDeliveredProcessed(deliverOperationResultsResp);
		}
		logger.verbose("@@@@@ Exiting NWCGDeliverOperationResultsIB::process (2)");
		return resp;
	}

	// note inDoc is MDTO!
	protected HashMap setupDeliverMessageMap(YFSEnvironment env, Document inDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGDeliverOperationResultsIB::setupDeliverMessageMap");
		Element inDocElm = inDoc.getDocumentElement();
		String distID = inDocElm.getAttribute(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID);
		if (StringUtil.isEmpty(distID)) {
			throw new Exception("Null Distribution ID passed to A&A IB Delivery process");
		}
		String entKey = inDocElm.getAttribute(NWCGAAConstants.MDTO_ENTKEY);
		String entVal = inDocElm.getAttribute(NWCGAAConstants.MDTO_ENTVALUE);
		String entName = inDocElm.getAttribute(NWCGAAConstants.MDTO_ENTNAME);
		msgMap.put(NWCGAAConstants.MDTO_ENTKEY, entKey);
		msgMap.put(NWCGAAConstants.MDTO_ENTVALUE, entVal);
		msgMap.put(NWCGAAConstants.MDTO_ENTNAME, entName);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID, distID);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_ENV, env);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_SYSNAME, NWCGConstants.EMPTY_STRING);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME, "DeliverOperationResultsReq");
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_NAMESPACE, NWCGAAConstants.RESOURCE_ORDER_NAMESPACE);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_USERNAME, NWCGConstants.EMPTY_STRING);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_PWD, NWCGConstants.EMPTY_STRING);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_CUSTOMPROP, "processSynchronously=true");
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_SERVNAME, "DeliverOperationResultsReq");
		logger.verbose("@@@@@ Exiting NWCGDeliverOperationResultsIB::setupDeliverMessageMap");
		return msgMap;
	}

	protected void getKeyFromMessageStore() {
		logger.verbose("@@@@@ Entering NWCGDeliverOperationResultsIB::getKeyFromMessageStore");
		try {
			String distID = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID);
			YFSEnvironment env = (YFSEnvironment) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_ENV);
			String latest_system_key = msgStore.getMessageKeyIBForDistID(env, distID, true);
			String firstIBMsgKey = msgStore.getMessageKeyIBForDistID(env, distID, false);
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_LATESTKEY, latest_system_key);
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_KEY, firstIBMsgKey);
			// This user id will always be ROSSInterface for Deliver Opration from ICBSR to ROSS because it runs on the agent server
			String user_id = NWCGAAConstants.ENV_USER_ID;
			String system_name = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_SYSNAME);
			String pw_key = NWCGTimeKeyManager.createKey(latest_system_key, user_id, system_name);
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_PWD, pw_key);
		} catch (Exception e) {
			logger.error("!!!!! Exception while generating system key");
			NWCGAlert.raiseInternalErrorAlert(msgMap);
		}
		logger.verbose("@@@@@ Exiting NWCGDeliverOperationResultsIB::getKeyFromMessageStore");
	}

	/**
	 * Forms body XML for the DeliverOpeartionResults OB Request to ROSS
	 * @param inDoc 
	 * @XmlType(name = "", propOrder = {
	 * "distributionID",
	 * "deliverResourceOrderOperationResults",
	 * "deliverResourceOperationResults",
	 * "deliverCatalogOperationResults"
	 * @throws RemoteException
	 */
	private Document createDeliverOperationResultsReq(Document inDoc) {
		logger.verbose("@@@@@ Entering NWCGDeliverOperationResultsIB::createDeliverOperationResultsReq");
		Document docDeliverReq = null;
		;
		try {
			docDeliverReq = XMLUtil.getDocument();
		} catch (ParserConfigurationException e1) {
		}
		DeliverOperationResultsReq req = null;
		try {
			req = new DeliverOperationResultsReq();
			List listContent = req.getContent();
			Element elemRoot = inDoc.getDocumentElement();
			String strDistId = elemRoot.getAttribute(NWCGAAConstants.MDTO_DISTID);
			// this will be used by send request
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_SERVNAME, "DeliverOperationResultsReq");
			msgMap.put(NWCGAAConstants.OPERATION_TYPE, NWCGAAConstants.OPERATION_SYNC);
			elemRoot.setAttribute(NWCGAAConstants.OPERATION_TYPE, NWCGAAConstants.OPERATION_SYNC);
			Element elem = inDoc.createElementNS(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE, "DistributionID");
			elem.appendChild(inDoc.createTextNode(strDistId));
			listContent.add(elem);
			listContent.add(inDoc.getDocumentElement());
			removeMDTOAttributes(inDoc);
			new NWCGJAXBContextWrapper().getDocumentFromObject(req, docDeliverReq, new URL(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
			logger.verbose("@@@@@ elem str is:" + XMLUtil.extractStringFromDocument(docDeliverReq));
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			docDeliverReq = null;
		}
		logger.verbose("@@@@@ Exiting NWCGDeliverOperationResultsIB::createDeliverOperationResultsReq");
		return docDeliverReq;
	}
}