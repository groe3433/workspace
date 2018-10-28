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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.TransformerException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.websphere.webservices.soap.IBMSOAPElement;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.commCore.NWCGProperties;
import com.nwcg.icbs.yantra.ob.handler.NWCGOBResponseProcessor;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
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
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGOBSyncAsyncProcessor {

	//Properties set in arguments tab of SDF
	Properties props;

	//HashMap to store all of our name/value pairs to be stored in the message store tables
	protected HashMap<Object, Object> msgMap = new HashMap<Object, Object>();

	//Reference to the message store class
	protected NWCGMessageStore msgStore = NWCGMessageStore.getMessageStore();

	protected NWCGOBSyncAsyncProcessor() {
		super();
		//setupKeyStore();
	}

	protected void setupMessageMap(YFSEnvironment env, Document msg)
			throws Exception {
		System.out.println("@@@@@ Entering NWCGOBSyncAsyncProcessor::setupMessageMap @@@@@");

		Element msgRootElem = msg.getDocumentElement();

		msgMap.put(NWCGAAConstants.MDTO_USERNAME, msgRootElem
				.getAttribute(NWCGAAConstants.MDTO_USERNAME));
		msgMap.put(NWCGAAConstants.MDTO_MSGNAME, msgRootElem
				.getAttribute(NWCGAAConstants.MDTO_MSGNAME));
		msgMap.put(NWCGAAConstants.MDTO_DISTID, msgRootElem
				.getAttribute(NWCGAAConstants.MDTO_DISTID));
		msgMap.put(NWCGAAConstants.MDTO_NAMESPACE, msgRootElem
				.getAttribute(NWCGAAConstants.MDTO_NAMESPACE));
		msgMap.put(NWCGAAConstants.MDTO_ENTNAME, msgRootElem
				.getAttribute(NWCGAAConstants.MDTO_ENTNAME));
		msgMap.put(NWCGAAConstants.MDTO_ENTKEY, msgRootElem
				.getAttribute(NWCGAAConstants.MDTO_ENTKEY));
		msgMap.put(NWCGAAConstants.MDTO_ENTVALUE, msgRootElem
				.getAttribute(NWCGAAConstants.MDTO_ENTVALUE));

		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_SERVNAME, msgMap
				.get(NWCGAAConstants.MDTO_MSGNAME));
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_ENV, env);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY, XMLUtil
				.extractStringFromDocument(msg));

		if (env != null)
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_SYSNAME, env
					.getSystemName());
		
		System.out.println("@@@@@ Exiting NWCGOBSyncAsyncProcessor::setupMessageMap @@@@@");
	}

	//init message store for OB msg and notifications
	protected void setupMessageStore(YFSEnvironment env) throws Exception {
		System.out.println("@@@@@ Entering NWCGOBSyncAsyncProcessor::setupMessageStore @@@@@");

		String userId = (String) msgMap
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_USERNAME);
		if (StringUtil.isEmpty(userId)) {
			userId = NWCGAAConstants.ENV_USER_ID;
		}
		try {
			if (msgStore == null) {
				NWCGLoggerUtil.Log
						.finest("NWCGOBSyncAsyncProcessor.setupMessageStore !!creating msgStore");
				msgStore = NWCGMessageStore.getMessageStore();
			}
			//initOutboundMessageFromMap will set MESSAGE_MAP_KEY and MESSAGE_MAP_LATESTKEY
			//in the msgMap for you
			String latestMessageKey = msgStore
					.initOutboundMessageFromMap(msgMap);

			String pw_key = NWCGTimeKeyManager.createKey(latestMessageKey,
					userId, env.getSystemName());
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_PWD, pw_key);

		} catch (Exception e) {
			NWCGLoggerUtil.Log
					.warning("NWCGOBSyncAsyncProcessor.setupMessageStore>>Exception while generating system key, exiting"
							+ e.getMessage());
			NWCGAlert.raiseInternalErrorAlert(msgMap);
			throw e;
		}
		
		System.out.println("@@@@@ Exiting NWCGOBSyncAsyncProcessor::setupMessageStore @@@@@");
	}

	protected void updateOutboundMessage(String status) throws Exception {
		System.out.println("@@@@@ Entering NWCGOBSyncAsyncProcessor::updateOutboundMessage @@@@@");
		
		String serviceName = (String) msgMap
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_SERVNAME);
		boolean isSyncMessage = determineIsSync(serviceName);

		if (status.equalsIgnoreCase("sent")) {
			if (isSyncMessage)
				msgStore.setMsgStatusProcessedSync(msgMap);
			else
				msgStore.setMsgStatusSent(msgMap);

			updateStartOutboundMessage((String) msgMap
					.get(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID));
		} else if (status.equalsIgnoreCase("sent_failed")) {
			//Jay: Keeping the ICBSR generated dist id
			//msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID, " ");
			if (isSyncMessage)
				msgStore.setMsgStatusSentSyncFailed(msgMap);
			else
				msgStore.setMsgStatusSentFailed(msgMap);

			updateStartOutboundMessage(" ");
		} else if (status.equalsIgnoreCase("fault")) {
			String distId = (String) msgMap
					.get(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID);

			if (isSyncMessage)
				msgStore.setMsgStatusSentSyncFault(msgMap);
			else
				msgStore.setMsgStatusSentFault(msgMap);

			updateStartOutboundMessage(distId);
		} else if (status.equalsIgnoreCase("soap_fault")) {
			// Jay: Keeping the ICBSR generated dist id 
			//msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID, " ");
			msgStore.setMsgOBStatusSoapFault(msgMap);
			updateStartOutboundMessage(" ");
		}
		
		System.out.println("@@@@@ Exiting NWCGOBSyncAsyncProcessor::updateOutboundMessage @@@@@");
	}

	protected void updateStartOutboundMessage(String distributionID)
			throws Exception {
		System.out.println("@@@@@ Entering NWCGOBSyncAsyncProcessor::updateStartOutboundMessage @@@@@");
		
		//Updates the START record of the outbound message store
		msgStore.updateMessage((YFSEnvironment) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_ENV), distributionID, "OB", "", "", "", "", (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_KEY), false, "");
		
		System.out.println("@@@@@ Exiting NWCGOBSyncAsyncProcessor::updateStartOutboundMessage @@@@@");
	}

	/**
	 * @param soapElem
	 */
	private void setMsgString(SOAPElement soapElem) {
		System.out.println("@@@@@ Entering NWCGOBSyncAsyncProcessor::setMsgString @@@@@");
		
		if (soapElem == null)
			return;
		IBMSOAPElement ibmSoapElem = (IBMSOAPElement) soapElem;
		String msg = ibmSoapElem.toXMLString(true);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, msg);
		
		System.out.println("@@@@@ Exiting NWCGOBSyncAsyncProcessor::setMsgString @@@@@");
	}

	public void setMsgStatusProcessedSync(SOAPElement se) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusProcessedSync @@@@@");
		setMsgString(se);
		msgStore.setMsgStatusProcessedSync(msgMap);
	}

	public void setMsgStatusSentSyncFailed(SOAPElement se) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusSentSyncFailed @@@@@");
		setMsgString(se);
		msgStore.setMsgStatusSentSyncFailed(msgMap);
	}

	public void setMsgStatusSentFault(SOAPElement se) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusSentFault @@@@@");
		setMsgString(se);
		msgStore.setMsgStatusSentFault(msgMap);
	}

	public void setMsgStatusSentSyncFailed(Document se) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusSentSyncFailed @@@@@");
		String msg = XMLUtil.getXMLString(se);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, msg);
		msgStore.setMsgStatusSentSyncFailed(msgMap);
	}

	public void setMsgStatusSentSyncFault(SOAPElement se) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusSentSyncFault @@@@@");
		setMsgString(se);
		msgStore.setMsgStatusSentSyncFault(msgMap);
	}

	public void setMsgStatusSentSyncFault(Document se) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusSentSyncFault @@@@@");
		String msg = XMLUtil.getXMLString(se);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, msg);
		msgStore.setMsgStatusSentSyncFault(msgMap);
	}

	public void setMsgStatusSentSync(SOAPElement se) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusSentSync @@@@@");
		setMsgString(se);
		msgStore.setMsgStatusSentSync(msgMap);
	}

	public void setMsgStatusSentSync(Document se) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusSentSync @@@@@");
		String msg = XMLUtil.getXMLString(se);
		msgMap.put("message", msg);
		msgStore.setMsgStatusSentSync(msgMap);
	}

	public void setMsgStatusFailedReadyForPickup(DeliverOperationResultsReq se) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusFailedReadyForPickup @@@@@");
		String msgDoc = getMarshalledObjectAsString(se);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, msgDoc);
		msgStore.setMsgStatusFailedReadyForPickup(msgMap);
	}

	public void setMsgStatusFailedReadyForPickup(Document reqDoc) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusFailedReadyForPickup @@@@@");
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, XMLUtil
				.getXMLString(reqDoc));
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY, XMLUtil
				.getXMLString(reqDoc));
		msgStore.setMsgStatusFailedReadyForPickup(msgMap);
	}

	public void setMsgStatusFaultReadyForPickup(Document se) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusFaultReadyForPickup @@@@@");
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, XMLUtil
				.getXMLString(se));
		msgStore.setMsgStatusFaultReadyForPickup(msgMap);
	}

	public void setMsgStatusFaultReadyForPickup(DeliverOperationResultsReq se) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusFaultReadyForPickup @@@@@");
		String msgDoc = getMarshalledObjectAsString(se);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, msgDoc);
		msgStore.setMsgStatusFaultReadyForPickup(msgMap);
	}

	public void setMsgStatusDeliveredProcessed(DeliverOperationResultsResp se) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusDeliveredProcessed @@@@@");
		String msgDoc = getMarshalledObjectAsString(se);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, msgDoc);
		msgStore.setMsgStatusInboundProcessed(msgMap);
	}

	public void setMsgStatusDeliveredProcessed(Document se) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusDeliveredProcessed @@@@@");
		try {
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, XMLUtil
					.extractStringFromDocument(se));
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY, XMLUtil
					.extractStringFromDocument(se));
		} catch (TransformerException e) {
			e.printStackTrace();
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, XMLUtil
					.getXMLString(se));
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY, XMLUtil
					.getXMLString(se));
		}
		msgStore.setMsgStatusInboundProcessed(msgMap);
	}

	public void setMsgStatusProcessed(SOAPElement se) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusProcessed @@@@@");
		setMsgString(se);
		msgStore.setMsgStatusOutboundProcessed(msgMap);
	}

	public void setMsgStatusForcedProcessed(SOAPElement se) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusForcedProcessed @@@@@");
		setMsgString(se);
		msgStore.setMsgStatusForcedProcessedOB(msgMap);
	}

	public void setMsgStatusForcedFault(SOAPElement se) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusForcedFault @@@@@");
		setMsgString(se);
		msgStore.setMsgStatusForcedResponseFault(msgMap);
	}

	public void setMsgStatusForcedFailed(SOAPElement se) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusForcedFailed @@@@@");
		setMsgString(se);
		msgStore.setMsgStatusForcedFailed(msgMap);
	}

	public void setMsgStatusSentFailed(SOAPElement se) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgStatusSentFailed @@@@@");
		setMsgString(se);
		msgStore.setMsgStatusSentFailed(msgMap);
	}

	public String getMessageKeyForDistId(String distID) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::getMessageKeyForDistId @@@@@");
		YFSEnvironment env = (YFSEnvironment) msgMap
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_ENV);
		return msgStore.getLatestMessageKeyOBForDistID(env, distID);
	}

	public boolean isSoapFault(Object resp) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::isSoapFault @@@@@");
		if ((resp != null) && (resp instanceof javax.xml.soap.SOAPFault))
			return true;
		else
			return false;
	}

	public boolean isSoapFault(SOAPElement resp) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::isSoapFault @@@@@");
		if (resp != null) {
			try {
				javax.xml.soap.SOAPElement msg = resp.getParentElement();
				javax.xml.soap.SOAPBody body = (javax.xml.soap.SOAPBody) msg;
				//NWCGLoggerUtil.Log.info("handleFault>"+body.getTextContent());
				javax.xml.soap.SOAPFault fault = body.getFault();
				NWCGLoggerUtil.Log.warning("fault in method:"
						+ fault.toString());
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean isSoapFault(Document resp) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::isSoapFault @@@@@");
		try {
			Element msg = resp.getDocumentElement();
			javax.xml.soap.SOAPBody body = (javax.xml.soap.SOAPBody) msg;
			//NWCGLoggerUtil.Log.info("handleFault>"+body.getTextContent());
			javax.xml.soap.SOAPFault fault = body.getFault();
			NWCGLoggerUtil.Log.warning("fault in method:" + fault.toString());
			return true;
		} catch (Exception e) {
			//nothing to do
		}
		return false;
	}

	/**
	 * This method  
	 * @param msg
	 * @param serviceName
	 * @return
	 * @throws Exception
	 */
	public Document formReturnResult(String docName, String respStatusStr,
			String respObjStr, String latestMsgStatus) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::formReturnResult @@@@@");
		NWCGLoggerUtil.Log.finest("formReturnResult");
		Document results = null;
		try {
			results = XMLUtil.createDocument(docName);
			setAttribute(results, "ResponseStatus", respStatusStr);
			setAttribute(results, "ResponseMsg", respObjStr);
			setAttribute(results, "ICBSStatus", latestMsgStatus);
			NWCGLoggerUtil.Log.finest("returning getopresults doc");

		} catch (Exception e) {
			NWCGLoggerUtil.Log.severe("error in creating resp document");

		}
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
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::determineIsSync @@@@@");
		String isSync = NWCGProperties.getProperty(serviceName + ".isSync");
		if (StringUtil.isEmpty(isSync)) {
			isSync = "TRUE";
		}
		return isSync.equalsIgnoreCase("TRUE");
	}

	/**
	 * @return Document representing the msgMap.messageBody key's value
	 */
	protected Document createMessageRequest() throws Exception {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::createMessageRequest @@@@@");
		String msg = (String) msgMap
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY);
		Document msgDoc = XMLUtil.getDocument(msg);
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
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::getUnmarshalledObject @@@@@");
		Object actual = null;
		String namespace = (String) msgMap.get(NWCGAAConstants.MDTO_NAMESPACE);
		//String msgBody = XMLUtil.getXMLString(doc);
		NWCGLoggerUtil.Log.finest("getUnmarshalledObject namespace="
				+ namespace);
		try {
			actual = new NWCGJAXBContextWrapper().getObjectFromDocument(doc,
					new URL(namespace));
			NWCGLoggerUtil.Log.finest("getUnmarshalledObject-done");
		} catch (JAXBException e) {
			NWCGLoggerUtil.Log
					.warning("getUnmarshalledObject>>Exception during unmarshalling"
							+ e.getMessage());
			e.printStackTrace();
		} catch (Exception e1) {
			NWCGLoggerUtil.Log.warning("getUnmarshalledObject>>Exception="
					+ e1.getMessage());
			e1.printStackTrace();
		}
		return actual;
	}

	protected Object getUnmarshalledObject(Document doc, URL url) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::getUnmarshalledObject @@@@@");
		Object actual = null;
		String namespace = (String) msgMap.get(NWCGAAConstants.MDTO_NAMESPACE);
		//String msgBody = XMLUtil.getXMLString(doc);
		NWCGLoggerUtil.Log.finest("getUnmarshalledObject namespace="
				+ namespace);
		try {
			/*Unmarshaller unmarshall = jc.createUnmarshaller();			
			 unmarshall.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
			 actual=unmarshall.unmarshal(new StreamSource( new StringReader(msgBody)));
			 */
			actual = new NWCGJAXBContextWrapper().getObjectFromDocument(doc,
					url);
			NWCGLoggerUtil.Log.finest("getUnmarshalledObject-done");
		} catch (JAXBException e) {
			NWCGLoggerUtil.Log
					.warning("getUnmarshalledObject>>Exception during unmarshalling"
							+ e.getMessage());
			e.printStackTrace();
		} catch (Exception e1) {
			NWCGLoggerUtil.Log.warning("getUnmarshalledObject>>Exception="
					+ e1.getMessage());
			e1.printStackTrace();
		}
		return actual;
	}

	protected String getMarshalledObjectAsString(Object obj) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::getMarshalledObjectAsString @@@@@");
		String retStr = null;
		Document doc = null;
		try {
			try {
				doc = new NWCGJAXBContextWrapper().getDocumentFromObject(obj,
						doc, new URL((String) msgMap
								.get(NWCGAAConstants.MDTO_NAMESPACE)));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			retStr = XMLUtil.extractStringFromDocument(doc);
		} catch (JAXBException e) {
			NWCGLoggerUtil.Log.warning("getMarshalledObjectAsString>>exception"
					+ e.getMessage());
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return retStr;
	}

	protected int getOperationType() {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::getOperationType @@@@@");
		Integer resInt = (Integer) NWCGWebServicesConstant.OBInterfaceMap
				.get(msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME));
		NWCGLoggerUtil.Log.finest("getOperationType" + resInt.toString());
		return resInt.intValue();
	}

	protected void setOutboundContext(Map<String, Object> rc) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::setOutboundContext @@@@@");
		rc.put(BindingProvider.USERNAME_PROPERTY, ResourceUtil.get(
				"nwcg.call.username", "icbs_dev"));
		rc.put(BindingProvider.PASSWORD_PROPERTY, ResourceUtil.get(
				"nwcg.call.password", "password1!"));
		rc.put(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_MESSAGENAME, msgMap
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME));
		rc.put(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_NAMESPACE, msgMap
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_NAMESPACE));
		rc.put(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_USER, msgMap
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_USERNAME));
		rc.put(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_PASSWORD, msgMap
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_PWD));
		rc.put(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_CUSTOMPROP, msgMap
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_CUSTOMPROP));
	}

	protected ICBSRInboundAsyncInterface getInboundAsyncPort() {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::getInboundAsyncPort @@@@@");
		ICBSRInboundAsyncService service = new ICBSRInboundAsyncService();
		service.setHandlerResolver(new NWCGOBHandlerResolver());
		ICBSRInboundAsyncInterface port = service.getICBSRInboundAsyncPort();
		BindingProvider bp = (BindingProvider) port;
		Map<String, Object> rc = bp.getRequestContext();
		setOutboundContext(rc);
		return port;
	}

	protected ResourceOrderInterface getResourcePort() {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::getResourcePort @@@@@");
		ResourceOrderService service = new ResourceOrderService();
		service.setHandlerResolver(new NWCGOBHandlerResolver());
		ResourceOrderInterface port = service.getResourceOrderPort();
		BindingProvider bp = (BindingProvider) port;
		Map<String, Object> rc = bp.getRequestContext();
		setOutboundContext(rc);
		return port;
	}

	protected CatalogInterface getCatalogPort() {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::getCatalogPort @@@@@");
		CatalogService service = new CatalogService();
		service.setHandlerResolver(new NWCGOBHandlerResolver());
		CatalogInterface port = service.getCatalogPort();
		BindingProvider bp = (BindingProvider) port;
		Map<String, Object> rc = bp.getRequestContext();
		setOutboundContext(rc);
		return port;
	}

	/**
	 * @param Document To be sent across the wire
	 * @throws Exception
	 * 			
	 */
	public Object sendMessageRequest(Document reqDoc) throws Exception {
		System.out.println("@@@@@ Entering NWCGOBSyncAsyncProcessor::sendMessageRequest @@@@@");
		System.out.println("@@@@@ reqDoc : " + XMLUtil.getXMLString(reqDoc));
		
		SOAPMessage soapMessageResponse = null;
		String soapBodyStr = XMLUtil.extractStringFromDocument(reqDoc);
		try {
			String operationType = reqDoc.getDocumentElement().getAttribute(NWCGAAConstants.OPERATION_TYPE);
			System.out.println("@@@@@ operationType : " + operationType);
			removeMDTOAttributes(reqDoc);
			String strServiceName = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_SERVNAME);
			System.out.println("@@@@@ strServiceName : " + strServiceName);
			Dispatch<SOAPMessage> dispatch = getDispatchFromNamespace(strServiceName);
			MessageFactory mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
			SOAPMessage soapMessageRequest = mf.createMessage();
			SOAPBody soapbodyRequest = soapMessageRequest.getSOAPBody();
			soapbodyRequest.addDocument(reqDoc);
			System.out.println("@@@@@ Test 1...");
			soapMessageResponse = dispatch.invoke(soapMessageRequest);
			System.out.println("@@@@@ Test 2...");
			// invoke Handler only incase of sync or async operations incase of GetOperationResults do not process the response, the caller will do itself
			if (!(strServiceName.equals("GetOperationResultsReq") || strServiceName.equals("DeliverOperationResultsReq"))) {
				System.out.println("@@@@@ In if 1...");
				if (operationType != null && operationType.length() > 2) {
					System.out.println("@@@@@ In if 2...");
					if (operationType.equalsIgnoreCase(NWCGAAConstants.OPERATION_SYNC)) {
						System.out.println("@@@@@ In if 3...");
						processSynchronousResponse(soapMessageResponse);
					} else if (operationType.equalsIgnoreCase(NWCGAAConstants.OPERATION_ASYNC)) {
						System.out.println("@@@@@ In if 4...");
						processAsynchronousResponse(soapMessageResponse);
					} else {
						// do nothing....
					}
				}
				//handle the response, should check the element name of the soapbody's first child to see what we got
				else if (determineIsSync(strServiceName) == true) {
					System.out.println("@@@@@ In if 5...");
					//Sync, we'll get the actual response. e.g.: CreateItemCatalogResp
					processSynchronousResponse(soapMessageResponse);
				} else {
					System.out.println("@@@@@ In if 6...");
					//If we sent off an asynchronous message, we should get back a MessageAcknowledgment in the SOAPBody with a DistributionID node
					processAsynchronousResponse(soapMessageResponse);
				}
			}// end !strServiceName.equals("GetOperationResultsReq")
		} catch (SOAPFaultException sfe) {
			System.out.println("@@@@@ catch 1...");
			SOAPFault soapFault = sfe.getFault();
			String soapFaultMsg = soapFault.toString();
			String strServiceName = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_SERVNAME);
			if (!strServiceName.equals("GetOperationResultsReq") && !strServiceName.equals("DeliverOperationResultsReq")) {
				msgMap.put("Fault Code", soapFault.getFaultCode());
				msgMap.put("Fault Description", soapFault.getFaultString());
				msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, soapFaultMsg);
				updateOutboundMessage("soap_fault");
				NWCGAlert.raiseSoapFaultAlert(msgMap);
			} else if (strServiceName.equals("DeliverOperationResultsReq")) {
				msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, soapBodyStr);
				msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY, soapBodyStr);
			}
			sfe.printStackTrace();
			throw sfe;
		} catch (Exception e) {
			System.out.println("@@@@@ catch 2...");
			String strServiceName = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_SERVNAME);
			if (!strServiceName.equals("GetOperationResultsReq") && !strServiceName.equals("DeliverOperationResultsReq")) {
				msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, e.toString());
				updateOutboundMessage("sent_failed");
			}
			e.printStackTrace();
			throw e;
		}
		
		System.out.println("@@@@@ Exiting NWCGOBSyncAsyncProcessor::sendMessageRequest @@@@@");
		return soapMessageResponse;
	}

	private void processSynchronousResponse(SOAPMessage soapMessageResponse)
			throws Exception {
		System.out.println("@@@@@ Entering NWCGOBSyncAsyncProcessor::processSynchronousResponse @@@@@");
		
		try {
			// if handler is not configured do not do anything
			NWCGOBResponseProcessor responseProcessor = new NWCGOBResponseProcessor();
			// Sending the response in map, without this code the map contains message sent to ROSS not response from ROSS
			SOAPBody body = soapMessageResponse.getSOAPBody();
			// Jay: even though below code works it is not correct approach to get the response, see the replaced method for more details
			//Node response = body.getFirstChild().getNextSibling();
			Node response = getResponseElementFromSOAPBody(body);

			Object responseObj = new NWCGJAXBContextWrapper()
					.getObjectFromNode(response, (String) msgMap
							.get(NWCGAAConstants.MDTO_MSGNAME));
			msgMap.put(NWCGAAConstants.MDTO_MSGBODY, responseObj);

			Document docReturnedByHandler = responseProcessor
					.processResponse(msgMap);
			// Jay: setting up the entity key as received by handler, if handler doesn't return entity key this will be set to blank
			String strEntityKey = StringUtil.nonNull(docReturnedByHandler
					.getDocumentElement().getAttribute(
							NWCGAAConstants.MDTO_ENTKEY));
			msgMap.put(NWCGAAConstants.MDTO_ENTKEY, strEntityKey);

		} catch (Exception e) {
			NWCGLoggerUtil.Log
					.warning("NWCGOBSyncAsyncProcessor::processSynchronousResponse, "
							+ "Exception while invoking the service");
			NWCGLoggerUtil.printStackTraceToLog(e);
			e.printStackTrace();
			throw e;
		}
		
		System.out.println("@@@@@ Exiting NWCGOBSyncAsyncProcessor::processSynchronousResponse @@@@@");
	}

	protected Node getResponseElementFromSOAPBody(SOAPBody body) {
		System.out.println("@@@@@ Entering NWCGOBSyncAsyncProcessor::getResponseElementFromSOAPBody @@@@@");
		
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
		
		System.out.println("@@@@@ Exiting NWCGOBSyncAsyncProcessor::getResponseElementFromSOAPBody @@@@@");
		return response;
	}

	private void processAsynchronousResponse(SOAPMessage soapMessageResponse)
			throws Exception {
		System.out.println("@@@@@ Entering NWCGOBSyncAsyncProcessor::processAsynchronousResponse @@@@@");

		SOAPBody soapResponseBody = soapMessageResponse.getSOAPBody();
		Document soapBodyDocument = soapResponseBody.extractContentAsDocument();
		NodeList nlDistID = soapBodyDocument
				.getElementsByTagName("DistributionID");

		if (nlDistID.getLength() != 0) {
			for (int index = 0; index < nlDistID.getLength(); index++) {
				Element elemDistID = (Element) nlDistID.item(index);
				// get the response and convert it to empty string
				String strDistID = StringUtil.nonNull(elemDistID
						.getTextContent());
				if (!StringUtil.isEmpty(strDistID)) {
					msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID,
							strDistID);
					msgMap
							.put(
									NWCGMessageStoreInterface.MESSAGE_MAP_MSG,
									XMLUtil
											.extractStringFromDocument(soapBodyDocument));
				}
			}
		}

		NodeList nlReturnCode = soapBodyDocument
				.getElementsByTagName("ReturnCode");
		if (nlReturnCode.getLength() != 0) {
			for (int index = 0; index < nlDistID.getLength(); index++) {
				Element elemReturnCode = (Element) nlReturnCode.item(index);
				String strReturnCode = StringUtil.nonNull(elemReturnCode
						.getTextContent());

				if (!strReturnCode.equals("-1")) {
					updateOutboundMessage("sent");
					soapResponseBody.addDocument(soapBodyDocument);
				} else {
					updateOutboundMessage("fault");
					soapResponseBody.addDocument(soapBodyDocument);
				}
			}
		}
		System.out.println("@@@@@ Exiting NWCGOBSyncAsyncProcessor::processAsynchronousResponse @@@@@");
	}

	/** 
	 * This method will remove the DistributionID, entity key/name/value,
	 * and various other MDTO attributes from the passed in Document
	 * @param reqDoc Document requiring removal of MDTO attributes
	 */
	protected void removeMDTOAttributes(Document reqDoc) {
		System.out.println("@@@@@ Entering NWCGOBSyncAsyncProcessor::removeMDTOAttributes @@@@@");
		
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
		
		System.out.println("@@@@@ Exiting NWCGOBSyncAsyncProcessor::removeMDTOAttributes @@@@@");
	}

	private Dispatch<SOAPMessage> getDispatchFromNamespace(String strServiceName) {
		System.out.println("@@@@@ Entering NWCGOBSyncAsyncProcessor::getDispatchFromNamespace @@@@@");
		
		String strNamespace = NWCGProperties.getProperty(strServiceName
				+ ".namespace");
		String strService = NWCGProperties.getProperty(strServiceName
				+ ".service");
		String strPort = NWCGProperties.getProperty(strServiceName + ".port");
		String strServiceGroup = NWCGProperties.getProperty(strServiceName
				+ ".serviceGroup");
		String strServiceGroupAddress = NWCGProperties
				.getProperty(strServiceGroup + ".Address");

		NWCGLoggerUtil.Log
				.finest("NWCGOBSyncAsyncProcessor:;getDispatchFromNamespace properties strNamespace="
						+ strNamespace
						+ " strService "
						+ strService
						+ " strPort "
						+ strPort
						+ " strServiceGroup "
						+ strServiceGroup
						+ " strServiceGroupAddress "
						+ strServiceGroupAddress);

		QName serviceName = new QName(strNamespace, strService);
		QName portName = new QName(strNamespace, strPort);
		Service service = Service.create(serviceName);
		service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING,
				strServiceGroupAddress);

		service.setHandlerResolver(new NWCGOBHandlerResolver());
		NWCGLoggerUtil.Log
				.finest("NWCGOBSyncAsyncProcessor:;getDispatchFromNamespace handler set");
		Dispatch<SOAPMessage> dispatch = service.createDispatch(portName,
				SOAPMessage.class, Service.Mode.MESSAGE);

		NWCGLoggerUtil.Log
				.finest("NWCGOBSyncAsyncProcessor:;getDispatchFromNamespace dispatch created");
		try {
			Map<String, Object> rc = dispatch.getRequestContext();
			setOutboundContext(rc);
			NWCGLoggerUtil.Log
					.finest("NWCGOBSyncAsyncProcessor:;getDispatchFromNamespace setOutboundContext set");
		} catch (Exception e) {
			e.printStackTrace();
			NWCGLoggerUtil.Log.warning("cant associate context"
					+ e.getMessage());
		}
		
		System.out.println("@@@@@ Exiting NWCGOBSyncAsyncProcessor::getDispatchFromNamespace @@@@@");
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
		System.out.println("@@@@@ Entering NWCGOBSyncAsyncProcessor::setAttribute @@@@@");
		
		if (doc != null) {
			if (!StringUtil.isEmpty(value)) {
				doc.getDocumentElement().setAttribute(attribute, value);
			}
		}
		
		System.out.println("@@@@@ Exiting NWCGOBSyncAsyncProcessor::setAttribute @@@@@");
		return doc;
	}

	public void setMsgMap(HashMap<Object, Object> msgMap) {
		System.out.println("@@@@@ In NWCGOBSyncAsyncProcessor::setMsgMap @@@@@");
		this.msgMap = msgMap;
	}
}