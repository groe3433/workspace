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

package com.nwcg.icbs.yantra.webservice.ob;

import gov.nwcg.services.ross.catalog.wsdl._1.CatalogInterface;
import gov.nwcg.services.ross.catalog.wsdl._1.CatalogService;
import gov.nwcg.services.ross.resource_order._1.DeliverOperationResultsReq;
import gov.nwcg.services.ross.resource_order._1.DeliverOperationResultsResp;
import gov.nwcg.services.ross.resource_order.wsdl._1.ICBSRInboundAsyncInterface;
import gov.nwcg.services.ross.resource_order.wsdl._1.ICBSRInboundAsyncService;
import gov.nwcg.services.ross.resource_order.wsdl._1.ResourceOrderInterface;
import gov.nwcg.services.ross.resource_order.wsdl._1.ResourceOrderService;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.ibm.websphere.webservices.soap.IBMSOAPElement;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.commCore.NWCGProperties;
import com.nwcg.icbs.yantra.ob.handler.NWCGOBResponseProcessor;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStore;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStoreInterface;
import com.nwcg.icbs.yantra.webservice.ob.handler.NWCGOBHandlerResolver;
import com.nwcg.icbs.yantra.webservice.util.NWCGJAXBContextWrapper;
import com.nwcg.icbs.yantra.webservice.util.alert.NWCGAlert;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGTimeKeyManager;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServicesConstant;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGOBSyncAsyncProcessor {
	
	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGOBSyncAsyncProcessor.class);

	Properties props;

	protected HashMap<Object, Object> msgMap = new HashMap<Object, Object>();

	protected NWCGMessageStore msgStore = NWCGMessageStore.getMessageStore();

	protected NWCGOBSyncAsyncProcessor() {
		super();
	}

	protected void setupMessageMap(YFSEnvironment env, Document msg) throws Exception {
		logger.verbose("@@@@@ Entering NWCGOBSyncAsyncProcessor::setupMessageMap");
		Element msgRootElem = msg.getDocumentElement();
		msgMap.put(NWCGAAConstants.MDTO_USERNAME, msgRootElem.getAttribute(NWCGAAConstants.MDTO_USERNAME));
		logger.verbose("@@@@@ msgRootElem.getAttribute(NWCGAAConstants.MDTO_USERNAME) :: " + msgRootElem.getAttribute(NWCGAAConstants.MDTO_USERNAME));
		msgMap.put(NWCGAAConstants.MDTO_MSGNAME, msgRootElem.getAttribute(NWCGAAConstants.MDTO_MSGNAME));
		logger.verbose("@@@@@ msgRootElem.getAttribute(NWCGAAConstants.MDTO_MSGNAME) :: " + msgRootElem.getAttribute(NWCGAAConstants.MDTO_MSGNAME));
		msgMap.put(NWCGAAConstants.MDTO_DISTID, msgRootElem.getAttribute(NWCGAAConstants.MDTO_DISTID));
		logger.verbose("@@@@@ msgRootElem.getAttribute(NWCGAAConstants.MDTO_DISTID) :: " + msgRootElem.getAttribute(NWCGAAConstants.MDTO_DISTID));
		msgMap.put(NWCGAAConstants.MDTO_NAMESPACE, msgRootElem.getAttribute(NWCGAAConstants.MDTO_NAMESPACE));
		logger.verbose("@@@@@ msgRootElem.getAttribute(NWCGAAConstants.MDTO_NAMESPACE) :: " + msgRootElem.getAttribute(NWCGAAConstants.MDTO_NAMESPACE));
		msgMap.put(NWCGAAConstants.MDTO_ENTNAME, msgRootElem.getAttribute(NWCGAAConstants.MDTO_ENTNAME));
		logger.verbose("@@@@@ msgRootElem.getAttribute(NWCGAAConstants.MDTO_ENTNAME) :: " + msgRootElem.getAttribute(NWCGAAConstants.MDTO_ENTNAME));
		msgMap.put(NWCGAAConstants.MDTO_ENTKEY, msgRootElem.getAttribute(NWCGAAConstants.MDTO_ENTKEY));
		logger.verbose("@@@@@ msgRootElem.getAttribute(NWCGAAConstants.MDTO_ENTKEY) :: " + msgRootElem.getAttribute(NWCGAAConstants.MDTO_ENTKEY));
		msgMap.put(NWCGAAConstants.MDTO_ENTVALUE, msgRootElem.getAttribute(NWCGAAConstants.MDTO_ENTVALUE));
		logger.verbose("@@@@@ msgRootElem.getAttribute(NWCGAAConstants.MDTO_ENTVALUE) :: " + msgRootElem.getAttribute(NWCGAAConstants.MDTO_ENTVALUE));
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_SERVNAME, msgMap.get(NWCGAAConstants.MDTO_MSGNAME));
		logger.verbose("@@@@@ msgRootElem.getAttribute(NWCGAAConstants.MDTO_MSGNAME) :: " + msgRootElem.getAttribute(NWCGAAConstants.MDTO_MSGNAME));
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_ENV, env);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY, XMLUtil.extractStringFromDocument(msg));
		if (env != null) {
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_SYSNAME, env.getSystemName());
			logger.verbose("@@@@@ env.getSystemName() :: " + env.getSystemName());
		}
		logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::setupMessageMap");
	}

	//init message store for OB msg and notifications
	protected void setupMessageStore(YFSEnvironment env) throws Exception {
		logger.verbose("@@@@@ Entering NWCGOBSyncAsyncProcessor::setupMessageStore");
		String userId = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_USERNAME);
		if (StringUtil.isEmpty(userId)) {
			userId = NWCGAAConstants.ENV_USER_ID;
		}
		try {
			if (msgStore == null) {
				logger.verbose("NWCGOBSyncAsyncProcessor.setupMessageStore !!creating msgStore");
				msgStore = NWCGMessageStore.getMessageStore();
			}
			String latestMessageKey = msgStore.initOutboundMessageFromMap(msgMap);
			String pw_key = NWCGTimeKeyManager.createKey(latestMessageKey, userId, env.getSystemName());
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_PWD, pw_key);
		} catch (Exception e) {
			logger.error("NWCGOBSyncAsyncProcessor.setupMessageStore>>Exception while generating system key, exiting" + e.getMessage());
			NWCGAlert.raiseInternalErrorAlert(msgMap);
			throw e;
		}
		logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::setupMessageStore");
	}

	protected void updateOutboundMessage(String status) throws Exception {
		logger.verbose("@@@@@ Entering NWCGOBSyncAsyncProcessor::updateOutboundMessage");
		
		String serviceName = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_SERVNAME);
		logger.verbose("@@@@@ serviceName :: " + serviceName);
		boolean isSyncMessage = determineIsSync(serviceName);
		logger.verbose("@@@@@ isSyncMessage :: " + isSyncMessage);
		
		logger.verbose("@@@@@ status :: " + status);
		if (status.equalsIgnoreCase("sent")) {
			if (isSyncMessage)
				msgStore.setMsgStatusProcessedSync(msgMap);
			else
				msgStore.setMsgStatusSent(msgMap);
			updateStartOutboundMessage((String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID));
		} else if (status.equalsIgnoreCase("sent_failed")) {
			if (isSyncMessage)
				msgStore.setMsgStatusSentSyncFailed(msgMap);
			else
				msgStore.setMsgStatusSentFailed(msgMap);
			updateStartOutboundMessage(" ");
		} else if (status.equalsIgnoreCase("fault")) {
			String distId = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID);
			logger.verbose("@@@@@ distId :: " + distId);
			if (isSyncMessage)
				msgStore.setMsgStatusSentSyncFault(msgMap);
			else
				msgStore.setMsgStatusSentFault(msgMap);
			updateStartOutboundMessage(distId);
		} else if (status.equalsIgnoreCase("soap_fault")) {
			msgStore.setMsgOBStatusSoapFault(msgMap);
			updateStartOutboundMessage(" ");
		}
		logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::updateOutboundMessage");
	}

	protected void updateStartOutboundMessage(String distributionID) throws Exception {
		msgStore.updateMessage((YFSEnvironment) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_ENV), distributionID, "OB", "", "", "", "", (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_KEY), false, "");
	}

	/**
	 * @param soapElem
	 */
	private void setMsgString(SOAPElement soapElem) {
		logger.verbose("@@@@@ Entering NWCGOBSyncAsyncProcessor::setMsgString");
		if (soapElem == null) {
			logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::setMsgString (null)");
			return;
		}
		IBMSOAPElement ibmSoapElem = (IBMSOAPElement) soapElem;
		String msg = ibmSoapElem.toXMLString(true);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, msg);
		logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::setMsgString");
	}

	public void setMsgStatusProcessedSync(SOAPElement se) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusProcessedSync");
		setMsgString(se);
		msgStore.setMsgStatusProcessedSync(msgMap);
	}

	public void setMsgStatusSentSyncFailed(SOAPElement se) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusSentSyncFailed");
		setMsgString(se);
		msgStore.setMsgStatusSentSyncFailed(msgMap);
	}

	public void setMsgStatusSentFault(SOAPElement se) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusSentFault");
		setMsgString(se);
		msgStore.setMsgStatusSentFault(msgMap);
	}

	public void setMsgStatusSentSyncFailed(Document se) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusSentSyncFailed");
		String msg = XMLUtil.getXMLString(se);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, msg);
		msgStore.setMsgStatusSentSyncFailed(msgMap);
	}

	public void setMsgStatusSentSyncFault(SOAPElement se) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusSentSyncFault (SOAPElement)");
		setMsgString(se);
		msgStore.setMsgStatusSentSyncFault(msgMap);
	}

	public void setMsgStatusSentSyncFault(Document se) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusSentSyncFault (Document)");
		String msg = XMLUtil.getXMLString(se);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, msg);
		msgStore.setMsgStatusSentSyncFault(msgMap);
	}

	public void setMsgStatusSentSync(SOAPElement se) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusSentSync (SOAPElement)");
		setMsgString(se);
		msgStore.setMsgStatusSentSync(msgMap);
	}

	public void setMsgStatusSentSync(Document se) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusSentSync (Document)");
		String msg = XMLUtil.getXMLString(se);
		msgMap.put("message", msg);
		msgStore.setMsgStatusSentSync(msgMap);
	}

	public void setMsgStatusFailedReadyForPickup(DeliverOperationResultsReq se) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusFailedReadyForPickup (DeliverOperationResultsReq)");
		String msgDoc = getMarshalledObjectAsString(se);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, msgDoc);
		msgStore.setMsgStatusFailedReadyForPickup(msgMap);
	}

	public void setMsgStatusFailedReadyForPickup(Document reqDoc) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusFailedReadyForPickup (Document)");
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, XMLUtil.getXMLString(reqDoc));
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY, XMLUtil.getXMLString(reqDoc));
		msgStore.setMsgStatusFailedReadyForPickup(msgMap);
	}

	public void setMsgStatusFaultReadyForPickup(Document se) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusFaultReadyForPickup (Document)");
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, XMLUtil.getXMLString(se));
		msgStore.setMsgStatusFaultReadyForPickup(msgMap);
	}

	public void setMsgStatusFaultReadyForPickup(DeliverOperationResultsReq se) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusFaultReadyForPickup (DeliverOperationResultsReq)");
		String msgDoc = getMarshalledObjectAsString(se);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, msgDoc);
		msgStore.setMsgStatusFaultReadyForPickup(msgMap);
	}

	public void setMsgStatusDeliveredProcessed(DeliverOperationResultsResp se) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusDeliveredProcessed (DeliverOperationResultsReq)");
		String msgDoc = getMarshalledObjectAsString(se);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, msgDoc);
		msgStore.setMsgStatusInboundProcessed(msgMap);
	}

	public void setMsgStatusDeliveredProcessed(Document se) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusDeliveredProcessed (Document)");
		try {
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, XMLUtil.extractStringFromDocument(se));
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY, XMLUtil.extractStringFromDocument(se));
		} catch (TransformerException e) {
			logger.error(e.getLocalizedMessage(), e);
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, XMLUtil.getXMLString(se));
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY, XMLUtil.getXMLString(se));
		}
		msgStore.setMsgStatusInboundProcessed(msgMap);
	}

	public void setMsgStatusProcessed(SOAPElement se) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusProcessed");
		setMsgString(se);
		msgStore.setMsgStatusOutboundProcessed(msgMap);
	}

	public void setMsgStatusForcedProcessed(SOAPElement se) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusForcedProcessed");
		setMsgString(se);
		msgStore.setMsgStatusForcedProcessedOB(msgMap);
	}

	public void setMsgStatusForcedFault(SOAPElement se) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusForcedFault");
		setMsgString(se);
		msgStore.setMsgStatusForcedResponseFault(msgMap);
	}

	public void setMsgStatusForcedFailed(SOAPElement se) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusForcedFailed");
		setMsgString(se);
		msgStore.setMsgStatusForcedFailed(msgMap);
	}

	public void setMsgStatusSentFailed(SOAPElement se) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusSentFailed");
		setMsgString(se);
		msgStore.setMsgStatusSentFailed(msgMap);
	}

	public String getMessageKeyForDistId(String distID) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::getMessageKeyForDistId");
		YFSEnvironment env = (YFSEnvironment) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_ENV);
		return msgStore.getLatestMessageKeyOBForDistID(env, distID);
	}

	public boolean isSoapFault(Object resp) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::isSoapFault (Object)");
		if ((resp != null) && (resp instanceof javax.xml.soap.SOAPFault))
			return true;
		else
			return false;
	}

	public boolean isSoapFault(SOAPElement resp) {
		logger.verbose("@@@@@ Entering NWCGOBSyncAsyncProcessor::isSoapFault (SOAPElement) ");
		if (resp != null) {
			try {
				javax.xml.soap.SOAPElement msg = resp.getParentElement();
				logger.warn("!!!!! fault in method (msg) :: " + msg.toString());
				javax.xml.soap.SOAPBody body = (javax.xml.soap.SOAPBody) msg;
				logger.warn("!!!!! fault in method (body) :: " + body.toString());
				javax.xml.soap.SOAPFault fault = body.getFault();
				logger.warn("!!!!! fault in method (fault) :: " + fault.toString());
				return true;
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
		logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::isSoapFault (SOAPElement) ");
		return false;
	}

	public boolean isSoapFault(Document resp) {
		logger.verbose("@@@@@ Entering NWCGOBSyncAsyncProcessor::isSoapFault (Document) ");
		try {
			Element msg = resp.getDocumentElement();
			javax.xml.soap.SOAPBody body = (javax.xml.soap.SOAPBody) msg;
			logger.warn("!!!!! fault in method (body) :: " + body.toString());
			javax.xml.soap.SOAPFault fault = body.getFault();
			logger.warn("!!!!! fault in method (fault) :: " + fault.toString());
			return true;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::isSoapFault (Document) ");
		return false;
	}

	/**
	 * This method  
	 * @param msg
	 * @param serviceName
	 * @return
	 * @throws Exception
	 */
	public Document formReturnResult(String docName, String respStatusStr, String respObjStr, String latestMsgStatus) {
		logger.verbose("@@@@@ Entering NWCGOBSyncAsyncProcessor::formReturnResult");
		Document results = null;
		try {
			results = XMLUtil.createDocument(docName);
			setAttribute(results, "ResponseStatus", respStatusStr);
			setAttribute(results, "ResponseMsg", respObjStr);
			setAttribute(results, "ICBSStatus", latestMsgStatus);
			logger.verbose("@@@@@ results : " + XMLUtil.getXMLString(results));
		} catch (Exception e) {
			logger.error("!!!!! error in creating resp document");
		}
		logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::formReturnResult");
		return results;
	}

	/**
	 * This method pull the name/value pair from NWCGAnAImpl.properties for
	 * <messageName>.isSync. Will return true or false boolean depending on what
	 * is found in the properties file. If the name/value pair is not found, defaults to TRUE
	 * @param String	serviceName
	 * @return boolean 	NWCGAnAImpl.properties <messageName>.isSync value as boolean, 
	 * 					defaults to TRUE if not found
	 * @throws Exception
	 */
	protected boolean determineIsSync(String serviceName) {
		logger.verbose("@@@@@ Entering NWCGOBSyncAsyncProcessor::determineIsSync");
		String isSync = NWCGProperties.getProperty(serviceName + ".isSync");
		if (StringUtil.isEmpty(isSync)) {
			isSync = "TRUE";
		}
		logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::determineIsSync");
		return isSync.equalsIgnoreCase("TRUE");
	}

	/**
	 * @return Document representing the msgMap.messageBody key's value
	 */
	protected Document createMessageRequest() throws Exception {
		logger.verbose("@@@@@ Entering NWCGOBSyncAsyncProcessor::createMessageRequest");
		String msg = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY);
		Document msgDoc = XMLUtil.getDocument(msg);
		logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::createMessageRequest");
		return msgDoc;
	}

	/**
	 * @author 
	 * @param package_name
	 * @param ownerDoc
	 * This method unmarshalls the Document version of the ElementNSImpl
	 * to return a object.
	 */
	protected Object getUnmarshalledObject(Document doc) {
		logger.verbose("@@@@@ Entering NWCGOBSyncAsyncProcessor::getUnmarshalledObject 1");
		Object actual = null;
		String namespace = (String) msgMap.get(NWCGAAConstants.MDTO_NAMESPACE);
		logger.verbose("@@@@@ getUnmarshalledObject namespace=" + namespace);
		try {
			logger.verbose("@@@@@ doc :: " + XMLUtil.getXMLString(doc));
			actual = new NWCGJAXBContextWrapper().getObjectFromDocument(doc, new URL(namespace));
			logger.verbose("@@@@@ getUnmarshalledObject-done 1");
		} catch (JAXBException e) {
			logger.error("!!!!! Caught JAXBException :: " + e.getMessage().toString());
		} catch (Exception e1) {
			logger.error("!!!!! Caught General Exception :: " + e1.getMessage().toString());
		}
		logger.verbose("@@@@@ actual : " + XMLUtil.getXMLString((Document)actual));
		logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::getUnmarshalledObject 1");
		return actual;
	}

	protected Object getUnmarshalledObject(Document doc, URL url) {
		logger.verbose("@@@@@ Entering NWCGOBSyncAsyncProcessor::getUnmarshalledObject 2");
		Object actual = null;
		String namespace = (String) msgMap.get(NWCGAAConstants.MDTO_NAMESPACE);
		logger.verbose("@@@@@ getUnmarshalledObject namespace=" + namespace);
		try {
			logger.verbose("@@@@@ doc :: " + XMLUtil.getXMLString(doc));
			logger.verbose("@@@@@ url :: " + url.toString());
			actual = new NWCGJAXBContextWrapper().getObjectFromDocument(doc, url);
			logger.verbose("@@@@@ getUnmarshalledObject-done 2");
		} catch (JAXBException e) {
			logger.error("!!!!! Caught JAXBException :: " + e.getMessage().toString());
		} catch (Exception e1) {
			logger.error("!!!!! Caught General Exception :: " + e1.getMessage().toString());
		}
		logger.verbose("@@@@@ We Are Here...");
		if(actual == null) {
			logger.verbose("@@@@@ Actual is null...");
		}
		logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::getUnmarshalledObject 2");
		return actual;
	}

	protected String getMarshalledObjectAsString(Object obj) {
		logger.verbose("@@@@@ Entering NWCGOBSyncAsyncProcessor::getMarshalledObjectAsString");
		String retStr = null;
		Document doc = null;
		try {
			try {
				doc = new NWCGJAXBContextWrapper().getDocumentFromObject(obj, doc, new URL((String) msgMap.get(NWCGAAConstants.MDTO_NAMESPACE)));
			} catch (MalformedURLException murle) {
				logger.error("!!!!! Caught MalformedURLException :: " + murle.getMessage().toString());
			} catch (ParserConfigurationException pce) {
				logger.error("!!!!! Caught ParserConfigurationException :: " + pce.getMessage().toString());
			}
			retStr = XMLUtil.extractStringFromDocument(doc);
		} catch (JAXBException jbe) {
			logger.error("!!!!! Caught JAXBException :: " + jbe.getMessage().toString());
		} catch (TransformerException te) {
			logger.error("!!!!! Caught TransformerException :: " + te.getMessage().toString());
		}
		logger.verbose("@@@@@ doc : " + XMLUtil.getXMLString(doc));
		logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::getMarshalledObjectAsString");
		return retStr;
	}

	protected int getOperationType() {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::getOperationType");
		Integer resInt = (Integer) NWCGWebServicesConstant.OBInterfaceMap.get(msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME));
		logger.verbose("@@@@@ getOperationType" + resInt.toString());
		return resInt.intValue();
	}

	protected void setOutboundContext(Map<String, Object> rc) {
		logger.verbose("@@@@@ Entering NWCGOBSyncAsyncProcessor::setOutboundContext");
		rc.put(BindingProvider.USERNAME_PROPERTY, ResourceUtil.get("nwcg.call.username", "icbs_dev"));
		rc.put(BindingProvider.PASSWORD_PROPERTY, ResourceUtil.get("nwcg.call.password", "password1!"));
		rc.put(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_MESSAGENAME, msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME));
		rc.put(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_NAMESPACE, msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_NAMESPACE));
		rc.put(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_USER, msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_USERNAME));
		rc.put(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_PASSWORD, msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_PWD));
		rc.put(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_CUSTOMPROP, msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_CUSTOMPROP));
		logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::setOutboundContext");
	}

	protected ICBSRInboundAsyncInterface getInboundAsyncPort() {
		logger.verbose("@@@@@ Entering NWCGOBSyncAsyncProcessor::getInboundAsyncPort");
		ICBSRInboundAsyncService service = new ICBSRInboundAsyncService();
		service.setHandlerResolver(new NWCGOBHandlerResolver());
		ICBSRInboundAsyncInterface port = service.getICBSRInboundAsyncPort();
		BindingProvider bp = (BindingProvider) port;
		Map<String, Object> rc = bp.getRequestContext();
		setOutboundContext(rc);
		logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::getInboundAsyncPort");
		return port;
	}

	protected ResourceOrderInterface getResourcePort() {
		logger.verbose("@@@@@ Entering NWCGOBSyncAsyncProcessor::getResourcePort");
		ResourceOrderService service = new ResourceOrderService();
		service.setHandlerResolver(new NWCGOBHandlerResolver());
		ResourceOrderInterface port = service.getResourceOrderPort();
		BindingProvider bp = (BindingProvider) port;
		Map<String, Object> rc = bp.getRequestContext();
		setOutboundContext(rc);
		logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::getResourcePort");
		return port;
	}

	protected CatalogInterface getCatalogPort() {
		logger.verbose("@@@@@ Entering NWCGOBSyncAsyncProcessor::getCatalogPort");
		CatalogService service = new CatalogService();
		service.setHandlerResolver(new NWCGOBHandlerResolver());
		CatalogInterface port = service.getCatalogPort();
		BindingProvider bp = (BindingProvider) port;
		Map<String, Object> rc = bp.getRequestContext();
		setOutboundContext(rc);
		logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::getCatalogPort");
		return port;
	}

	/**
	 * @param Document To be sent across the wire
	 * @throws Exception
	 * 			
	 */
	public Object sendMessageRequest(Document reqDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGOBSyncAsyncProcessor::sendMessageRequest");
		logger.verbose("@@@@@ reqDoc : " + XMLUtil.getXMLString(reqDoc));
		SOAPMessage soapMessageResponse = null;
		String soapBodyStr = XMLUtil.extractStringFromDocument(reqDoc);
		try {
			String operationType = reqDoc.getDocumentElement().getAttribute(NWCGAAConstants.OPERATION_TYPE);
			logger.verbose("@@@@@ operationType : " + operationType);
			removeMDTOAttributes(reqDoc);
			logger.verbose("@@@@@ After removeMDTOAttributes... ");
			String strServiceName = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_SERVNAME);
			logger.verbose("@@@@@ strServiceName : " + strServiceName);
			Dispatch<SOAPMessage> dispatch = getDispatchFromNamespace(strServiceName);
			logger.verbose("@@@@@ After getDispatchFromNamespace... ");
			MessageFactory mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
			logger.verbose("@@@@@ After getting instance of message factory... ");
			SOAPMessage soapMessageRequest = mf.createMessage();
			logger.verbose("@@@@@ After createMessage... ");
			SOAPBody soapbodyRequest = soapMessageRequest.getSOAPBody();
			logger.verbose("@@@@@ After getSOAPBody... ");
			Document reqDoc1 = XMLUtil.getDocument(XMLUtil.extractStringFromDocument(reqDoc));
			logger.verbose("@@@@@ reqDoc1 : " + XMLUtil.getXMLString(reqDoc1));
			soapbodyRequest.addDocument(reqDoc1);  
			logger.verbose("@@@@@ After addDocument... ");
			soapMessageResponse = dispatch.invoke(soapMessageRequest);
			logger.verbose("@@@@@ After invoke... ");
			if (!(strServiceName.equals("GetOperationResultsReq") || strServiceName.equals("DeliverOperationResultsReq"))) {
				logger.verbose("@@@@@ in IF 1... ");
				if (operationType != null && operationType.length() > 2) {
					logger.verbose("@@@@@ in IF 2... ");
					if (operationType.equalsIgnoreCase(NWCGAAConstants.OPERATION_SYNC)) {
						logger.verbose("@@@@@ in IF 3... ");
						processSynchronousResponse(soapMessageResponse);
						logger.verbose("@@@@@ After processSynchronousResponse...");
					} else if (operationType.equalsIgnoreCase(NWCGAAConstants.OPERATION_ASYNC)) {
						logger.verbose("@@@@@ in ELSE IF 3... ");
						processAsynchronousResponse(soapMessageResponse);
						logger.verbose("@@@@@ After processAsynchronousResponse...");
					} else {
						logger.verbose("@@@@@ in ELSE 3... ");
						// do nothing....
					}
				}
				else if (determineIsSync(strServiceName) == true) {
					logger.verbose("@@@@@ in ELSE IF 2... ");
					processSynchronousResponse(soapMessageResponse);
					logger.verbose("@@@@@ After processSynchronousResponse...");
				} else {
					logger.verbose("@@@@@ in ELSE 2... ");
					processAsynchronousResponse(soapMessageResponse);
					logger.verbose("@@@@@ After processAsynchronousResponse...");
				}
			}
		} catch (SOAPFaultException sfe) {
			logger.verbose("@@@@@ In SOAPFaultException ...");
			SOAPFault soapFault = sfe.getFault();
			String soapFaultMsg = soapFault.toString();
			logger.verbose("@@@@@ soapFaultMsg : " + soapFaultMsg);
			String strServiceName = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_SERVNAME);
			logger.verbose("@@@@@ strServiceName : " + strServiceName);
			if (!strServiceName.equals("GetOperationResultsReq") && !strServiceName.equals("DeliverOperationResultsReq")) {
				logger.verbose("@@@@@ In IF ...");
				msgMap.put("Fault Code", soapFault.getFaultCode());
				msgMap.put("Fault Description", soapFault.getFaultString());
				msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, soapFaultMsg);
				updateOutboundMessage("soap_fault");
				NWCGAlert.raiseSoapFaultAlert(msgMap);
			} else if (strServiceName.equals("DeliverOperationResultsReq")) {
				logger.verbose("@@@@@ In ELSE IF ...");
				msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, soapBodyStr);
				msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY, soapBodyStr);
			}
			logger.verbose("!!!!! NWCGOBSyncAsyncProcessor SOAPFaultException : " + sfe);
			sfe.printStackTrace();
			throw sfe;
		} catch (Exception e) {
			logger.error("@@@@@ In Exception ...");
			String strServiceName = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_SERVNAME);
			logger.verbose("@@@@@ strServiceName : " + strServiceName);
			if (!strServiceName.equals("GetOperationResultsReq") && !strServiceName.equals("DeliverOperationResultsReq")) {
				logger.verbose("@@@@@ In IF ...");
				msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, e.toString());
				updateOutboundMessage("sent_failed");
			}
			logger.error("!!!!! NWCGOBSyncAsyncProcessor Caught General Exception : " + e);
			e.printStackTrace();
			throw e;
		}
		logger.verbose("@@@@@ soapMessageResponse : " + soapMessageResponse.toString());
		logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::sendMessageRequest");
		return soapMessageResponse;
	}

	private void processSynchronousResponse(SOAPMessage soapMessageResponse) throws Exception {
		logger.verbose("@@@@@ Entering NWCGOBSyncAsyncProcessor::processSynchronousResponse");
		try {
			// if handler is not configured do not do anything
			NWCGOBResponseProcessor responseProcessor = new NWCGOBResponseProcessor();
			// Sending the response in map, without this code the map contains message sent to ROSS not response from ROSS
			SOAPBody body = soapMessageResponse.getSOAPBody();
			// even though below code works it is not correct approach to get the response, see the replaced method for more details Node response = body.getFirstChild().getNextSibling();
			Node response = getResponseElementFromSOAPBody(body);
			Object responseObj = new NWCGJAXBContextWrapper().getObjectFromNode(response, (String) msgMap.get(NWCGAAConstants.MDTO_MSGNAME));
			msgMap.put(NWCGAAConstants.MDTO_MSGBODY, responseObj);
			Document docReturnedByHandler = responseProcessor.processResponse(msgMap);
			// setting up the entity key as received by handler, if handler doesn't return entity key this will be set to blank
			String strEntityKey = StringUtil.nonNull(docReturnedByHandler.getDocumentElement().getAttribute(NWCGAAConstants.MDTO_ENTKEY));
			msgMap.put(NWCGAAConstants.MDTO_ENTKEY, strEntityKey);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		}	
		logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::processSynchronousResponse");
	}

	protected Node getResponseElementFromSOAPBody(SOAPBody body) {
		logger.verbose("@@@@@ Entering NWCGOBSyncAsyncProcessor::getResponseElementFromSOAPBody");
		Node response = null;
		NodeList list = body.getChildNodes();
		if (list != null) {
			for (int index = 0; index < list.getLength(); index++) {
				Node nodeItem = list.item(index);
				if (nodeItem instanceof com.ibm.ws.webservices.engine.xmlsoap.SOAPBodyElement) {
					response = nodeItem;
				}
			}
		}
		logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::getResponseElementFromSOAPBody");
		return response;
	}

	private void processAsynchronousResponse(SOAPMessage soapMessageResponse) throws Exception {
		logger.verbose("@@@@@ Entering NWCGOBSyncAsyncProcessor::processAsynchronousResponse");
		SOAPBody soapResponseBody = soapMessageResponse.getSOAPBody();
		Document soapBodyDocument = soapResponseBody.extractContentAsDocument();
		NodeList nlDistID = soapBodyDocument.getElementsByTagName("DistributionID");
		if (nlDistID.getLength() != 0) {
			for (int index = 0; index < nlDistID.getLength(); index++) {
				Element elemDistID = (Element) nlDistID.item(index);
				// get the response and convert it to empty string
				String strDistID = StringUtil.nonNull(elemDistID.getTextContent());
				if (!StringUtil.isEmpty(strDistID)) {
					msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID, strDistID);
					msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, XMLUtil.extractStringFromDocument(soapBodyDocument));
				}
			}
		}
		NodeList nlReturnCode = soapBodyDocument.getElementsByTagName("ReturnCode");
		if (nlReturnCode.getLength() != 0) {
			for (int index = 0; index < nlDistID.getLength(); index++) {
				Element elemReturnCode = (Element) nlReturnCode.item(index);
				String strReturnCode = StringUtil.nonNull(elemReturnCode.getTextContent());
				if (!strReturnCode.equals("-1")) {
					updateOutboundMessage("sent");
					soapResponseBody.addDocument(soapBodyDocument);
				} else {
					updateOutboundMessage("fault");
					soapResponseBody.addDocument(soapBodyDocument);
				}
			}
		}
		logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::processAsynchronousResponse");
	}

	/** 
	 * This method will remove the DistributionID, entity key/name/value,
	 * and various other MDTO attributes from the passed in Document
	 * @param reqDoc Document requiring removal of MDTO attributes
	 */
	protected void removeMDTOAttributes(Document reqDoc) {
		logger.verbose("@@@@@ Entering NWCGOBSyncAsyncProcessor::removeMDTOAttributes");
		Element elem = reqDoc.getDocumentElement();
		elem.removeAttribute(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID);
		elem.removeAttribute(NWCGMessageStoreInterface.MESSAGE_MAP_ENTKEY);
		elem.removeAttribute(NWCGMessageStoreInterface.MESSAGE_MAP_ENTNAME);
		elem.removeAttribute("NWCGUSERID");
		elem.removeAttribute(NWCGMessageStoreInterface.MESSAGE_MAP_ENTVALUE);
		elem.removeAttribute(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME);
		elem.removeAttribute(NWCGMessageStoreInterface.MESSAGE_MAP_NAMESPACE);
		elem.removeAttribute(NWCGMessageStoreInterface.MESSAGE_MAP_SERVNAME);
		elem.removeAttribute(NWCGMessageStoreInterface.MESSAGE_MAP_USERNAME);
		elem.removeAttribute(NWCGAAConstants.OPERATION_TYPE);
		logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::removeMDTOAttributes");
	}

	private Dispatch<SOAPMessage> getDispatchFromNamespace(String strServiceName) {
		logger.verbose("@@@@@ Entering NWCGOBSyncAsyncProcessor::getDispatchFromNamespace");
		String strNamespace = NWCGProperties.getProperty(strServiceName + ".namespace");
		String strService = NWCGProperties.getProperty(strServiceName + ".service");
		String strPort = NWCGProperties.getProperty(strServiceName + ".port");
		String strServiceGroup = NWCGProperties.getProperty(strServiceName + ".serviceGroup");
		
		// BEGIN - 9.1 Upgrade Modification - December 10, 2014 - READ ROSS URLs from customer_overrides.properties
		//String strServiceGroupAddress = NWCGProperties.getProperty(strServiceGroup + ".Address");
		String strServiceGroupAddress = YFSSystem.getProperty(strServiceGroup + ".Address");
		logger.verbose("@@@@@ strServiceGroupAddress : " + strServiceGroupAddress);
		// END - 9.1 Upgrade Modification
		
		QName serviceName = new QName(strNamespace, strService);
		QName portName = new QName(strNamespace, strPort);
		Service service = Service.create(serviceName);
		service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, strServiceGroupAddress);
		service.setHandlerResolver(new NWCGOBHandlerResolver());
		Dispatch<SOAPMessage> dispatch = service.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);
		try {
			Map<String, Object> rc = dispatch.getRequestContext();
			setOutboundContext(rc);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			logger.error("!!!!! cant associate context" + e.getMessage());
		}	
		logger.verbose("@@@@@ Exiting NWCGOBSyncAsyncProcessor::getDispatchFromNamespace");
		return dispatch;
	}

	/**
	 * Helper method for settings attribute/value's on a Document's root element
	 * @param Document 
	 * @param string of the attribute name
	 * @param string of the value to set on the attribute
	 * @return the document object passed in
	 */
	private Document setAttribute(Document doc, String attribute, String value) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::setAttribute");
		if (doc != null) {
			if (!StringUtil.isEmpty(value)) {
				doc.getDocumentElement().setAttribute(attribute, value);
			}
		}
		return doc;
	}

	public void setMsgMap(HashMap<Object, Object> msgMap) {
		logger.verbose("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgMap");
		this.msgMap = msgMap;
	}
}