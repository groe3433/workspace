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

package com.nwcg.icbs.yantra.webservice.business;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.ws.webservices.engine.xmlsoap.SOAPElement;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.commCore.NWCGProperties;
import com.nwcg.icbs.yantra.ob.handler.NWCGOBResponseProcessor;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.business.inf.NWCGMetaDataTransferObject;
import com.nwcg.icbs.yantra.webservice.business.inf.NWCGMetaDataTransferObjectInterface;
import com.nwcg.icbs.yantra.webservice.ib.msg.NWCGDeliverOperationResultsIB;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStoreInterface;
import com.nwcg.icbs.yantra.webservice.ob.msg.NWCGGetOperationResultsOB;
import com.nwcg.icbs.yantra.webservice.ob.msg.NWCGOutboundAsyncMessage;
import com.nwcg.icbs.yantra.webservice.ob.msg.NWCGOutboundSyncMessage;
import com.nwcg.icbs.yantra.webservice.ob.notification.NWCGOBNotification;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServiceUtils;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServicesConstant;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGBusinessMsgProcessor {
	
	private static YFCLogCategory logger = NWCGROSSLogger
			.instance(NWCGBusinessMsgProcessor.class);

	/**
	 * Sends out messages Sterling to ROSS
	 * 
	 * Changed the signature from void to Object. - IB_DELIVERY: ROSS returns
	 * DeliverOperationResultsResp. Caller needs to convert from object to
	 * DeliverOperationResultsResp class. - OB_SYNC: ROSS returns a document.
	 * Back end will be expecting the document on which it will act on. Convert
	 * the document to object - OB_GET: ROSS sends GetOperationResultsResp.
	 * Convert it into object. This might not be used on the caller end as A&A
	 * code is calling the respective individual class using
	 * NWCGBusinessMsgProcessor().forwardGetOperationResponse(msgMap); -
	 * OB_ASYNC: ROSS returns MessageAcknowledgement. The caller does not act on
	 * any of the information returned from ROSS, so converting to object is
	 * harmless - OB_NOTIFICATION: ROSS doesn't return anything. Caller doesn't
	 * expect anything, so will be return null.
	 * 
	 * @param msgDoc -
	 *            Old MDTO attributes should be in the root element of the input
	 *            document, msg Doc
	 * @param env -
	 *            The YFSEnvironment object to be used for all subsequent MCF
	 *            API/SDF invocations
	 * @return Object
	 */
	public Object postOutboundMessageToROSS(YFSEnvironment env, int operationType, Document msgDoc) {
		logger.verbose("@@@@@ Entering NWCGBusinessMsgProcessor::postOutboundMessageToROSS @@@@@");
		Object retnObj = msgDoc;
		if (msgDoc == null) {
			logger.verbose("!!!!! Exiting NWCGBusinessMsgProcessor::postOutboundMessageToROSS !!!!!");
			return retnObj;
		}
		try {
			logger.verbose("@@@@@ operationType :: " + operationType);
			switch (operationType) {
			case NWCGWebServicesConstant.IBTYPE_DELIVERY:
				retnObj = (new NWCGDeliverOperationResultsIB()).process(env, msgDoc);
				break;
			case NWCGWebServicesConstant.OBTYPE_GET:
				retnObj = (new NWCGGetOperationResultsOB()).process(env, msgDoc);
				break;
			case NWCGWebServicesConstant.OBTYPE_SYNC:
				retnObj = (new NWCGOutboundSyncMessage()).process(env, msgDoc);
				break;
			case NWCGWebServicesConstant.OBTYPE_ASYNC:
				retnObj = (new NWCGOutboundAsyncMessage()).process(env, msgDoc);
				break;
			case NWCGWebServicesConstant.OBTYPE_NOTIFICATION:
				(new NWCGOBNotification()).process(env, msgDoc);
				break;
			default:
				logger.verbose("!!!!! wrong message type...");
				break;
			}
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: during invoking OB type :: " + operationType);
			logger.error(e.getLocalizedMessage(), e);
		}
		logger.verbose("@@@@@ Exiting NWCGBusinessMsgProcessor::postOutboundMessageToROSS @@@@@");
		return retnObj;
	}

	/**
	 * This method is invoked from NWCGInboundASyncMessage to pass incoming
	 * document to Sterling NWCGPostIBMessageService
	 * 
	 * @param msgMap
	 * @throws Exception
	 */
	public void postIncomingMsgToJMS(HashMap msgMap) throws Exception {
		logger.verbose("@@@@@ Entering NWCGBusinessMsgProcessor::postIncomingMsgToJMS @@@@@");
		String messageName = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME);
		logger.verbose("@@@@@ messageName :: " + messageName);
		String serviceToInvoke = NWCGProperties.getProperty(messageName.concat(".asyncIBServiceClass"));
		logger.verbose("@@@@@ serviceToInvoke :: " + serviceToInvoke);
		String msgBody = (String) msgMap.get(NWCGAAConstants.MDTO_MSGBODY);
		Document inDoc = XMLUtil.getDocument(msgBody);
		inDoc.getDocumentElement().setAttribute(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID, (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID));
		try {
			logger.verbose("@@@@@ inDoc : " + XMLUtil.getXMLString(inDoc));
			NWCGWebServiceUtils.invokeServiceMethod(serviceToInvoke, inDoc);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: Invoking Service :: " + serviceToInvoke);
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		}
		logger.verbose("@@@@@ Exiting NWCGBusinessMsgProcessor::postIncomingMsgToJMS @@@@@");
	}

	/**
	 * This method is invoked from NWCGInboundNotification to pass incoming
	 * document to Sterling NWCGPostIBMessageService. 
	 * 
	 * @param msgMap
	 * @throws java.lang.Exception
	 */
	public void postIncomingNotificationToJMS(HashMap msgMap) throws Exception {
		logger.verbose("@@@@@ Entering NWCGBusinessMsgProcessor::postIncomingNotificationToJMS @@@@@");
		String messageName = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME);
		logger.verbose("@@@@@ messageName :: " + messageName);		
		String serviceToInvoke = NWCGProperties.getProperty(messageName.concat(".notificationIBServiceClass"));
		logger.verbose("@@@@@ serviceToInvoke :: " + serviceToInvoke);
		String msgBody = (String) msgMap.get(NWCGAAConstants.MDTO_MSGBODY);
		Document inDoc = XMLUtil.getDocument(msgBody);
		// Adding notification code here because for notifications we will not get fist id from ROSS
		inDoc.getDocumentElement().setAttribute(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID, (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID));
		// Get the actual Notification message from DeliverNotificationReq/*Notification element.
		if (messageName.equalsIgnoreCase(NWCGAAConstants.DELIVER_NOT_REQ_ROOT)) {
			NodeList nl = inDoc.getDocumentElement().getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node curNode = nl.item(i);
				Element curElm = (curNode instanceof Element) ? (Element) curNode : null;
				if (curElm == null)
					continue;
				if (curElm.getNodeName().indexOf("otifiedSystem") != -1)
					continue;
				logger.verbose("@@@@@ curElm.getLocalName() :: " + curElm.getLocalName());
				if (curElm.getLocalName().indexOf("otification") != -1) {
					messageName = curElm.getLocalName();
					break;
				}
			}
		}
		inDoc.getDocumentElement().setAttribute(NWCGAAConstants.MESSAGE_NAME_ELEM_NAME, messageName);
		try {
			logger.verbose("@@@@@ inDoc : " + XMLUtil.getXMLString(inDoc));
			NWCGWebServiceUtils.invokeServiceMethod(serviceToInvoke, inDoc);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: Invoking Service :: " + serviceToInvoke);
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		}
		logger.verbose("@@@@@ Exiting NWCGBusinessMsgProcessor::postIncomingNotificationToJMS @@@@@");
	}

	/**
	 * This method is invoked from NWCGInboundSyncMessage to pass incoming
	 * result document to Sterling in sync mode
	 * 
	 * @param msgMap
	 * @return Document
	 */
	public Document forwardSyncMsg(HashMap msgMap) {
		logger.verbose("@@@@@ Entering NWCGBusinessMsgProcessor::forwardSyncMsg @@@@@");
		Document returnDocument = null;
		String messageName = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME);
		logger.verbose("@@@@@ messageName :: " + messageName);	
		String serviceToInvoke = NWCGProperties.getProperty(messageName.concat(".syncIBServiceClass"));
		logger.verbose("@@@@@ serviceToInvoke :: " + serviceToInvoke);
		String msgBody = (String) msgMap.get(NWCGAAConstants.MDTO_MSGBODY);
		try {
			Document inDoc = XMLUtil.getDocument(msgBody);
			inDoc.getDocumentElement().setAttribute(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID, (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID));
			// Passing and returning MDTO object Documents
			logger.verbose("@@@@@ inDoc : " + XMLUtil.getXMLString(inDoc));
			returnDocument = NWCGWebServiceUtils.invokeServiceMethod(serviceToInvoke, inDoc);
			logger.verbose("@@@@@ returnDocument : " + XMLUtil.getXMLString(returnDocument));
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			logger.error(e.getLocalizedMessage(), e);
		}
		logger.verbose("@@@@@ Exiting NWCGBusinessMsgProcessor::forwardSyncMsg @@@@@");
		return returnDocument;
	}

	/**
	 * Method is invoked from NWCGGetOperationResultsOB to pass returned result
	 * to Sterling. this is synchronous call returning status (??)
	 * NWCGPostGetOperationService - this is the last step in GetOperation agent
	 * function for A&A
	 * 
	 * @param msgMap
	 * @return Document
	 */
	public Document forwardGetOperationResponse(HashMap msgMap) {
		logger.verbose("@@@@@ Entering NWCGBusinessMsgProcessor::forwardGetOperationResponse @@@@@");
		Document ret = null;
		// Creating interface dto
		NWCGMetaDataTransferObjectInterface imdto = NWCGMetaDataTransferObject.getInstance();
		// This sets the map into DTO and retuns map as a document
		Document resultDoc = imdto.getDocFromMap(msgMap);
		// Checking required data in the map
		if (!imdto.isMapValid()) {
			logger.verbose("!!!!! Exiting com.nwcg.icbs.yantra.webservice.business.NWCGBusinessMsgProcessor::forwardGetOperationResponse !!!!!");
			return null;
		}
		try {
			logger.verbose("@@@@@ resultDoc : " + XMLUtil.getXMLString(resultDoc));
			Document resp = NWCGWebServiceUtils.invokeServiceMethod(NWCGWebServicesConstant.NWCG_POST_GET_OPERATIONS_SERVICE, resultDoc);
			logger.verbose("@@@@@ resp : " + XMLUtil.getXMLString(resp));
			if (resp != null) {
				// Convert to map
				HashMap dtoMap = imdto.getMapFromDoc(resp);
				// Get msg body from the map - sts from Sterling
				String msgBody = (String) dtoMap.get(NWCGAAConstants.MDTO_MSGBODY);
				ret = XMLUtil.getDocument(msgBody);
			}
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			logger.error(e.getLocalizedMessage(), e);
		}		
		logger.verbose("@@@@@ Exiting NWCGBusinessMsgProcessor::forwardGetOperationResponse @@@@@");
		return ret;
	}

	/**
	 * This method is invoked from NWCGGetOperationResultsOB to pass retuned
	 * result to Sterling this is synchronous call returning status (??)
	 * NWCGPostGetOperationService - this is the last step in GetOperation agent
	 * function for A&A
	 */
	public void forwardGetOperationResponse(SOAPElement resp) {
		logger.verbose("@@@@@ Entering NWCGBusinessMsgProcessor::forwardGetOperationResponse @@@@@");
		try {
			Document respDoc = resp.getAsDocument();
			String resultDocStr = XMLUtil.extractStringFromDocument(respDoc);
			logger.verbose("@@@@@ respDoc : " + XMLUtil.getXMLString(respDoc));
			NWCGWebServiceUtils.invokeServiceMethod(NWCGWebServicesConstant.NWCG_POST_GET_OPERATIONS_SERVICE, respDoc);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			logger.error(e.getLocalizedMessage(), e);
		}
		logger.verbose("@@@@@ Exiting NWCGBusinessMsgProcessor::forwardGetOperationResponse @@@@@");
	}

	/**
	 * Method is invoked from NWCGDeliverOperationResultsOB to pass incoming
	 * result document to Sterling invokes Sterling Service that calls Sterling
	 * API synchronously returns nothing for success and exception for failure.
	 * 
	 * @param msgMap
	 * @return org.w3c.dom.Document
	 */
	public Document forwardDeliveryResults(HashMap msgMap) throws Exception {
		logger.verbose("@@@@@ Entering NWCGBusinessMsgProcessor::forwardDeliveryResults @@@@@");
		try {
			NWCGOBResponseProcessor responseProcessor = new NWCGOBResponseProcessor();
			logger.verbose("@@@@@ Exiting NWCGBusinessMsgProcessor::forwardDeliveryResults @@@@@");
			return responseProcessor.processResponse(msgMap);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		}
	}

	/**
	 * We need to pass the distributionID along with the actual message request,
	 * so that consumer can pick up the message from JMS Queue and update the
	 * message store with the desired status for the particular distribution ID.
	 * so we need to pass a new input xml to the service instead of the actual
	 * 'messageBodydoc'. This new xml will have both the dist id and the
	 * messageBodydoc combined.
	 * 
	 * @param messageBodydoc
	 * @param distID
	 */
	private Document getInterfaceDoc(Document messageBodydoc, String distID) {
		logger.verbose("@@@@@ Entering NWCGBusinessMsgProcessor::getInterfaceDoc @@@@@");
		Document tempDoc = null;
		try {
			tempDoc = XMLUtil.newDocument();
			Element headerElem = tempDoc.createElement("IBMessageDoc");
			tempDoc.appendChild(headerElem);
			headerElem.setAttribute(NWCGAAConstants.DIST_ID_ATTR, distID);
			Element msgElem = tempDoc.createElement("MessageBody");
			msgElem.appendChild(tempDoc.importNode(messageBodydoc.getDocumentElement(), true));
			headerElem.appendChild(msgElem);
			logger.verbose("@@@@@ tempDoc : " + XMLUtil.getXMLString(tempDoc));
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			logger.error(e.getLocalizedMessage(), e);
		}
		logger.verbose("@@@@@ Exiting NWCGBusinessMsgProcessor::getInterfaceDoc @@@@@");
		return tempDoc;
	}
}