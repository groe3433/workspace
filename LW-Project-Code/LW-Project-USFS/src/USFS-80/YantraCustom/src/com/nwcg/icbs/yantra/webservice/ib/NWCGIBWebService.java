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

package com.nwcg.icbs.yantra.webservice.ib;

import gov.nwcg.services.ross.common_types._1.MessageAcknowledgement;
import gov.nwcg.services.ross.common_types._1.ResponseMessageType;
import gov.nwcg.services.ross.common_types._1.ResponseStatusType;
import gov.nwcg.services.ross.resource_order._1.GetOperationResultsResp;
import gov.nwcg.services.ross.resource_order._1.RegisterIncidentInterestResp;
import gov.nwcg.services.ross.resource_order_notification._1.DeliverNotificationResp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.commCore.NWCGProperties;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.soap.NWCGAAUtil;
import com.nwcg.icbs.yantra.soap.NWCGSOAPFault;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStore;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStoreInterface;
import com.nwcg.icbs.yantra.webservice.util.NWCGJAXBContextWrapper;
import com.nwcg.icbs.yantra.webservice.util.alert.NWCGAlert;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServiceUtils;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServicesConstant;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class NWCGIBWebService {

	public final static int NOTIFICATION_TYPE = 1;

	public final static int SYNCMESSAGE_TYPE = 2;

	public final static int ASYNCMESSAGE_TYPE = 3;

	protected SOAPMessageContext wsContext = null; // WebServiceContext

	protected HashMap<String, String> myContext;

	protected HashMap<Object, Object> msgMap = new HashMap<Object, Object>();

	protected NWCGMessageStoreInterface messageStore = NWCGMessageStore
			.getMessageStore();

	public static NWCGIBWebService getNWCGIBWebService() {
		return new NWCGIBWebService();
	}

	protected void handleSecurityFailureSoapFault(SOAPFaultException sfe) {
		NWCGAlert.raiseSecurityFailedAlert(msgMap);
		messageStore.setMsgStatusSecurityFailed(msgMap, sfe.getFault()
				.toString());
		throw sfe;
	}

	protected void handleSecurityFailure(SOAPFaultException sfe) {
		NWCGAlert.raiseSecurityFailedAlert(msgMap);
		SOAPFault sfeRetVal = createSOAPFault(sfe,
				NWCGAAConstants.SOAP_AUTHENTICATION_MESSAGE_FAILURE);
		throw new SOAPFaultException(sfeRetVal);
	}

	/**
	 * Determines whether the inbound message being processed is to be processed
	 * synchronously or not. Based on the existence of a property in
	 * NWCGAnAImpl.properties in the format of messageName.isSync=true/false
	 * 
	 * @return true only if the property is set, false otherwise.
	 */
	public boolean determineIsSyncFromProperty(String serviceName) {
		NWCGLoggerUtil.Log.info("inside determine is sync for" + serviceName);
		String isSync = NWCGProperties.getProperty(serviceName + ".isSync");
		NWCGLoggerUtil.Log.info("isSync:" + isSync);
		boolean res = (Boolean.getBoolean(isSync));
		return res;// isSync.equalsIgnoreCase("TRUE");
	}

	/**
	 * Determines whether we need to call the verifySecuritySession ROSS web
	 * service based on the existence of a property in NWCGAnAImpl.properties in
	 * the format of messageName.requiresAuth=TRUE/yes or FALSE/no Note: The
	 * values of true, false, yes, or no are case-insensitive
	 * 
	 * @return false if property set to false/no, defaults to true otherwise
	 */
	public boolean requiresSecurityAuthentication() {
		System.out.println("@@@@@ Entering NWCGIBWebService::requiresSecurityAuthentication @@@@@");
		
		boolean result = true;
		String messageName = (String) msgMap
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME);

		NWCGLoggerUtil.Log
				.info("Determing whether verifySecuritySession needs to be called "
						+ "for messageName: " + messageName);

		String requiresAuth = NWCGProperties.getProperty(messageName
				+ ".requiresAuth");

		if (!StringUtil.isEmpty(requiresAuth)) {
			if (requiresAuth.equalsIgnoreCase("false")
					|| requiresAuth.equalsIgnoreCase("no"))
				result = false;
		} else {
			// If it the property name/value pair isn't found
			NWCGLoggerUtil.Log.warning(messageName
					+ ".requiresAuth not configured in A&A property file!"
					+ " Defaulting to yes/TRUE");
		}
		
		System.out.println("@@@@@ Exiting NWCGIBWebService::requiresSecurityAuthentication @@@@@");
		return result;
	}

	// inject context into this class
	public void setContext(SOAPMessageContext aContext) { // WebServiceContext
		wsContext = aContext;
	}

	public void setContext(HashMap<String, String> myContext) {
		this.myContext = myContext;
	}

	// find msg type and send it to approperiate processing
	public MessageAcknowledgement processByType() {
		System.out.println("@@@@@ Entering NWCGIBWebService::processByType @@@@@");

		MessageAcknowledgement msgAck = null;

		String messageName = getPropertyFromContext(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_MESSAGENAME);
		String customProp = getPropertyFromContext(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_CUSTOMPROP);
		NWCGLoggerUtil.Log.info("processMessageByType starts messageName="
				+ messageName);
		if (StringUtil.isEmpty(messageName))
			return null; // raiseAlert or negateAck?

		// get type as sync,async or notification base on messageName and header
		// custom property
		int messageType = getMessageType(messageName, NWCGAAUtil.isSync(
				messageName, customProp));

		switch (messageType) {
		case NOTIFICATION_TYPE: {
			// msgAck=receiveNotificationMsg();
			break;
		}
		case SYNCMESSAGE_TYPE: {
			msgAck = null;// returning reply from services
			break;
		}

		case ASYNCMESSAGE_TYPE: {

			break;
		}

		}// switch

		System.out.println("@@@@@ Exiting NWCGIBWebService::processByType @@@@@");
		return msgAck;
	}

	private void setOutboundContext(Map<String, Object> rc) {
		System.out.println("@@@@@ Entering NWCGIBWebService::setOutboundContext @@@@@");
		
		rc.put(BindingProvider.USERNAME_PROPERTY, ResourceUtil.get(
				"nwcg.call.username", "icbs_dev"));
		rc.put(BindingProvider.PASSWORD_PROPERTY, ResourceUtil.get(
				"nwcg.call.password", "password1!"));
		rc.put(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_MESSAGENAME,
				"VerifySecuritySessionReq");
		rc.put(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_NAMESPACE,
				"http://nwcg.gov/services/ross/common_types/wsdl/1.1");
		
		System.out.println("@@@@@ Exiting NWCGIBWebService::setOutboundContext @@@@@");
	}

	// this method works as following:
	// -check message header for password if empty throws SOAPFaultException
	// get securitySessionId from header and call ROSS A&A service, if fail
	// creates SOAPFault
	public boolean verifyMessageSecurity() {
		System.out.println("@@@@@ Entering NWCGIBWebService::verifyMessageSecurity @@@@@");
		
		String securitySessionID = getPropertyFromContext(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_SECURITYSESSIONID);
		NWCGLoggerUtil.Log.info("Password in inboundNotification:"
				+ securitySessionID);

		if (StringUtil.isEmpty(securitySessionID)) {
			SOAPFault fault = createSOAPFault(NWCGAAConstants.SOAP_FAULT_MESSAGE_NULL_PASSWORD);
			throw new SOAPFaultException(fault);
		}
		boolean bIsVerified = false;
		try {
			// setting up the service name and port name
			QName serviceName = new QName(
					"http://nwcg.gov/services/ross/common_types/wsdl/1.1",
					"VerifySecuritySessionService");
			QName portName = new QName(
					"http://nwcg.gov/services/ross/common_types/wsdl/1.1",
					"VerifySecuritySessionPort");
			Service service = Service.create(serviceName);
			// set up handler resolver, this will intern inoke the the JAX-WS
			// handler
			service.setHandlerResolver(new NWCGHandlerResolver());
			// setting the port, a web services call be made on this address
			service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING,
					ResourceUtil.get("verifySecuritySession.Address"));
			// setting up some mandatory params
			Dispatch<SOAPMessage> dispatch = service.createDispatch(portName,
					SOAPMessage.class, Service.Mode.MESSAGE);

			setOutboundContext(dispatch.getRequestContext());

			MessageFactory mf = MessageFactory
					.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
			SOAPMessage request = mf.createMessage();
			SOAPPart part = request.getSOAPPart();
			SOAPEnvelope env = part.getEnvelope();
			// getting the body, will set everything in the body now
			SOAPBody body = env.getBody();

			// getting the document
			Document doc = XMLUtil.getDocument();// docBuilder.parse("C:\\Documents
													// and
													// Settings\\jvishwakarma\\Desktop\\updateCatalog.xml");
			// creating the elements now
			Element elemVerifySecuritySessionReq = doc.createElementNS(
					"http://nwcg.gov/services/ross/common_types/1.1",
					"nwcg:VerifySecuritySessionReq");
			Element elemSecuritySessionID = doc
					.createElement("SecuritySessionID");
			// setting the password
			elemSecuritySessionID.setTextContent(securitySessionID);
			elemVerifySecuritySessionReq.appendChild(elemSecuritySessionID);
			doc.appendChild(elemVerifySecuritySessionReq);

			NWCGLoggerUtil.Log.info("Document before making call to ROSS "
					+ XMLUtil.extractStringFromDocument(doc));
			body.addDocument(doc);
			SOAPMessage resp = dispatch.invoke(request);
			SOAPBody respBody = resp.getSOAPBody();
			NWCGLoggerUtil.Log.info("Response from ROSS "
					+ XMLUtil.extractStringFromDocument(respBody
							.getOwnerDocument()));

			Document docResp = respBody.getOwnerDocument();
			NodeList nlIsVerified = docResp.getElementsByTagName("IsVerified");
			for (int index = 0; index < nlIsVerified.getLength(); index++) {
				Element elemIsVerified = (Element) nlIsVerified.item(index);
				// get the response and convert it to empty string
				String strIsVerified = StringUtil.nonNull(elemIsVerified
						.getTextContent());
				try {
					bIsVerified = Boolean.parseBoolean(strIsVerified);

				} catch (Exception ex) {
					// this might through some exception while parsing the
					// string
					NWCGLoggerUtil.Log
							.warning("do nothing try parsing as Y and TRUE");
				}
				if (strIsVerified.equalsIgnoreCase("Y")
						|| strIsVerified.equalsIgnoreCase("TRUE")) {
					bIsVerified = true;
				}

			}
		} catch (SOAPFaultException sfe) {
			sfe.printStackTrace();
			NWCGLoggerUtil.Log
					.warning("SOAPFaultException while calling VerifyMessageSecurity ROSS WS");
			NWCGLoggerUtil.Log.warning("SOAPFaultException message: "
					+ sfe.getMessage());
			handleSecurityFailure(sfe);
			throw sfe;
		} catch (Exception e) {
			e.printStackTrace();
			NWCGLoggerUtil.Log.warning("Caught :: " + e.getMessage());
		}
		if (!bIsVerified) {
			SOAPFault soapFault = createSOAPFault(null,
					NWCGAAConstants.MESSAGE_STATUS_AUTHORIZATION_FAILED);
			throw new SOAPFaultException(soapFault);
		}

		System.out.println("@@@@@ Exiting NWCGIBWebService::verifyMessageSecurity @@@@@");
		return bIsVerified;
	}

	public String getServiceName(Document docMsg) {
		System.out.println("@@@@@ Entering NWCGIBWebService::getServiceName @@@@@");
		
		String serviceName = "";

		String nodeNameDocMsg = docMsg.getDocumentElement().getNodeName();
		NWCGLoggerUtil.Log.info("nodeNameDocMsg :" + nodeNameDocMsg);

		if (nodeNameDocMsg.indexOf(":") > 0) {
			serviceName = nodeNameDocMsg
					.substring(nodeNameDocMsg.indexOf(":") + 1);
		} else {
			serviceName = nodeNameDocMsg;
		}

		NWCGLoggerUtil.Log.info("serviceName :" + serviceName);
		
		System.out.println("@@@@@ Exiting NWCGIBWebService::getServiceName @@@@@");
		return serviceName;
	}

	// determine if message type is one of the three posible values:
	// notification,sync or async
	private int getMessageType(String messageName, boolean isSync) {
		System.out.println("@@@@@ Entering NWCGIBWebService::getMessageType @@@@@");
		
		int result = 0;
		if (messageName.equalsIgnoreCase("DeliverNotificationReq")) {
			result = NOTIFICATION_TYPE;
		} else {
			if (isSync) {
				result = SYNCMESSAGE_TYPE;
			} else {
				result = ASYNCMESSAGE_TYPE;
			}
		}
		NWCGLoggerUtil.Log.info("getMessageType returns type=>" + result);
		
		System.out.println("@@@@@ Exiting NWCGIBWebService::getMessageType @@@@@");
		return result;
	}

	/*
	 * setup data from context that where extracted by SoapHeader handler
	 * 
	 */
	protected HashMap<Object, Object> setupMessageMap() throws Exception {
		System.out.println("@@@@@ Entering NWCGIBWebService::setupMessageMap @@@@@");
		
		// extract soapBody content and distID --checkout next constant
		String messageBodyXML = getPropertyFromContext(NWCGWebServicesConstant.SOAP_ELEMENT_MESSAGEBODY);
		String distID = getPropertyFromContext(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_DISTID);
		NWCGLoggerUtil.Log.finest("setupMessageMap>>distId=" + distID
				+ " body>>" + messageBodyXML);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY,
				messageBodyXML);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID, distID);

		// getting service name from context instead, value of service name and
		// message name is same
		// if service name is null assigning the value from message name
		String serviceName = StringUtil
				.nonNull(getPropertyFromContext(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_SERVICENAME));// getServiceName(incomingMsg);

		// Jay: Message name will be the actual notification comming from ROSS,
		// for example UpdateincidentKeyNotification,
		// this is based on discussion with LM on distributionType
		String message_name = getPropertyFromContext(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_MESSAGENAME);

		if (serviceName.equals(NWCGConstants.EMPTY_STRING)) {
			serviceName = message_name;
		}
		// assign service name ot messagename, required for ping service
		if (message_name == null
				|| message_name.equals(NWCGConstants.EMPTY_STRING))
			message_name = serviceName;
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_SERVNAME, serviceName);
		msgMap
				.put(
						NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_CUSTOMPROP,
						getPropertyFromContext(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_CUSTOMPROP));
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME, message_name);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_NAMESPACE, StringUtil
				.nonNull(NWCGProperties.getProperty(message_name
						.concat(".namespace"))));

		setupMessageMapBase();
		
		System.out.println("@@@@@ Exiting NWCGIBWebService::setupMessageMap @@@@@");
		return msgMap;
	}

	protected void setupMessageMapBase() throws Exception {
		System.out.println("@@@@@ Entering NWCGIBWebService::setupMessageMapBase @@@@@");
		
		// /move to parent:
		YFSEnvironment env = NWCGWebServiceUtils.getEnvironment(); 

		String system_name = env.getSystemName();
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_ENV, env);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_SYSNAME, system_name);
		String userName = NWCGAAConstants.USER_NAME;
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_USERNAME, userName);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_PWD,
				NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_PASSWORD);
		
		System.out.println("@@@@@ Exiting NWCGIBWebService::setupMessageMapBase @@@@@");
	}

	/**
	 * /* determine if inbound message is sync or async based on custom property
	 * in the header if property is true - it is sync message, otherwise if it
	 * is false or absent - async message
	 */
	protected boolean isSync() {
		System.out.println("@@@@@ In NWCGIBWebService::isSync @@@@@");
		return NWCGAAUtil
				.isSync(
						getPropertyFromContext(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_MESSAGENAME),
						getPropertyFromContext(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_CUSTOMPROP));
	}

	// creates alert in IB_NOTIFICATION queue when notification is successfully
	// processed
	protected void raiseNotificationAlert(String messageBody, String distID) {
		System.out.println("@@@@@ Entering NWCGIBWebService::raiseNotificationAlert @@@@@");

		String msgName = getPropertyFromContext(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_MESSAGENAME);
		NWCGLoggerUtil.Log
				.info("raiseNotificationAlert>>success; processed notification="
						+ msgName);
		// properties for populating Alert screen in UI
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		map
				.put("Return Code",
						NWCGWebServicesConstant.ACK_SUCCESS_MESSAGE_CODE);
		map.put("code", NWCGWebServicesConstant.ACK_SUCCESS_MESSAGE_CODE);
		map.put("Severity",
				NWCGWebServicesConstant.ACK_SUCCESS_MESSAGE_SEVERITY);
		map
				.put("Description",
						NWCGWebServicesConstant.ACK_SUCCESS_MESSAGE_DESC);
		map.put("DistributionId", distID);
		map.put(NWCGAAConstants.NAME, messageBody);
		map.put("ExceptionType", msgName);

		try {
			NWCGWebServiceUtils.raiseAlert(NWCGAAConstants.QUEUEID_FAULT,
					NWCGWebServicesConstant.ALERT_NOTIFICATION, XMLUtil
							.getDocument(messageBody), null, map);
		} catch (ParserConfigurationException pce) {

			NWCGLoggerUtil.Log
					.warning("ParserConfigurationException while creating XMLDocument");
			pce.printStackTrace();
		} catch (IOException ioe) {

			NWCGLoggerUtil.Log
					.warning("IOException while creating XMLDocument");
			ioe.printStackTrace();
		} catch (SAXException e) {

			NWCGLoggerUtil.Log
					.warning("SAXException while creating XMLDocument");
			e.printStackTrace();
		}
		
		System.out.println("@@@@@ Exiting NWCGIBWebService::raiseNotificationAlert @@@@@");
	}

	/**
	 * Method for returning MessageAcknowledgement back to ROSS
	 * 
	 * @param distID
	 * @return
	 */
	protected MessageAcknowledgement getMessageAcknowledgement(String distID,
			boolean isPositive) {
		System.out.println("@@@@@ In NWCGIBWebService::getMessageAcknowledgement @@@@@");
		return getMessageAcknowledgement(distID, isPositive, "");
	}

	protected MessageAcknowledgement getMessageAcknowledgement(String distID,
			boolean isPositive, String msgDesc) {
		System.out.println("@@@@@ Entering NWCGIBWebService::getMessageAcknowledgement @@@@@");

		NWCGLoggerUtil.Log.finer("getMessageAcknowledgement>distId/type="
				+ distID + '/' + isPositive);

		MessageAcknowledgement msgAck = new MessageAcknowledgement();

		msgAck.setDistributionID(distID);
		ResponseStatusType statType = new ResponseStatusType();

		List<ResponseMessageType> list = statType.getResponseMessage();
		ResponseMessageType msgType = new ResponseMessageType();
		if (isPositive) {
			msgType.setCode(NWCGWebServicesConstant.ACK_SUCCESS_MESSAGE_CODE);
			msgType
					.setDescription(NWCGWebServicesConstant.ACK_SUCCESS_MESSAGE_DESC);
			msgType
					.setSeverity(NWCGWebServicesConstant.ACK_SUCCESS_MESSAGE_SEVERITY);
			statType.setReturnCode(NWCGAAConstants.SOAP_SUCCESS_CODE);
		} else {// ?
			msgType.setCode(NWCGWebServicesConstant.ACK_FAILURE_MESSAGE_CODE);
			String msgDescription = (!StringUtil.isEmpty(msgDesc)) ? msgDesc
					: NWCGWebServicesConstant.ACK_SUCCESS_MESSAGE_SEVERITY;
			msgType.setDescription(msgDescription);
			msgType
					.setSeverity(NWCGWebServicesConstant.ACK_FAILURE_MESSAGE_SEVERITY);
			statType.setReturnCode(NWCGAAConstants.SOAP_FAILURE_CODE);
		}
		list.add(msgType);

		msgAck.setResponseStatus(statType);

		System.out.println("@@@@@ Exiting NWCGIBWebService::getMessageAcknowledgement @@@@@");
		return msgAck;
	}

	protected DeliverNotificationResp getDeliverNotificationResp(
			boolean isPositive) {
		System.out.println("@@@@@ Entering NWCGIBWebService::getDeliverNotificationResp @@@@@");

		NWCGLoggerUtil.Log
				.finer("getDeliverOperationResp>distId/type (is Positive?)= "
						+ isPositive);

		DeliverNotificationResp resp = new DeliverNotificationResp();
		ResponseStatusType statType = new ResponseStatusType();
		List<ResponseMessageType> list = statType.getResponseMessage();
		ResponseMessageType msgType = new ResponseMessageType();
		if (isPositive) {
			msgType.setCode(NWCGWebServicesConstant.NOTF_SUCCESS_MESSAGE_CODE);
			msgType
					.setDescription(NWCGWebServicesConstant.NOTF_SUCCESS_MESSAGE_DESC);
			msgType
					.setSeverity(NWCGWebServicesConstant.NOTF_SUCCESS_MESSAGE_SEVERITY);
			statType.setReturnCode(NWCGAAConstants.SOAP_SUCCESS_CODE);
		} else {
			msgType.setCode(NWCGWebServicesConstant.NOTF_FAILURE_MESSAGE_CODE);
			msgType
					.setDescription(NWCGWebServicesConstant.NOTF_FAILURE_MESSAGE_DESC);
			msgType
					.setSeverity(NWCGWebServicesConstant.NOTF_FAILURE_MESSAGE_SEVERITY);
			statType.setReturnCode(NWCGAAConstants.SOAP_FAILURE_CODE);
		}
		list.add(msgType);

		resp.setResponseStatus(statType);

		System.out.println("@@@@@ Exiting NWCGIBWebService::getDeliverNotificationResp @@@@@");
		return resp;
	}

	// helper method to find given property in wsContext
	protected String getPropertyFromContext(String property) {
		System.out.println("@@@@@ Entering NWCGIBWebService::getPropertyFromContext @@@@@");
		
		if (myContext == null)
			return null;
		if (StringUtil.isEmpty(property))
			return null;
		String value = myContext.get(property);
		
		System.out.println("@@@@@ Exiting NWCGIBWebService::getPropertyFromContext @@@@@");
		return value;
	}

	/**
	 * This method is equivalent to getPropertyFromContext. Here, we are not
	 * type casting to String. We are returning the stored object. It is
	 * responsibility of the caller to type cast to the appropriate type.
	 * 
	 * @param property
	 * @return
	 */
	protected Object getPropertyObjFromContext(String property) {
		System.out.println("@@@@@ Entering NWCGIBWebService::getPropertyObjFromContext @@@@@");
		
		if (wsContext == null)
			return null;
		if (StringUtil.isEmpty(property))
			return null;
		
		System.out.println("@@@@@ Exiting NWCGIBWebService::getPropertyObjFromContext @@@@@");
		return wsContext.get(property);
	}

	// convert xml string to Document object
	protected Document getDocumentFromXML(String xmlString) {
		System.out.println("@@@@@ Entering NWCGIBWebService::getDocumentFromXML @@@@@");
		
		Document doc = null;
		try {
			doc = XMLUtil.getDocument(xmlString);
			NWCGLoggerUtil.Log.info("getDocumentFromXML :"
					+ XMLUtil.getXMLString(doc));
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (SAXException e2) {
			e2.printStackTrace();
		} catch (IOException e3) {
			e3.printStackTrace();
		}
		
		System.out.println("@@@@@ Exiting NWCGIBWebService::getDocumentFromXML @@@@@");
		return doc;
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
	protected Document getTempDoc(Document messageBodydoc, String distID) {
		System.out.println("@@@@@ Entering NWCGIBWebService::getTempDoc @@@@@");
		
		Document tempDoc = null;

		try {
			tempDoc = XMLUtil
					.createDocument(NWCGAAConstants.MDTO_DOCUMENT_ROOT);
			Element elemMDTO = tempDoc.getDocumentElement();

			elemMDTO.setAttribute(NWCGAAConstants.MDTO_DISTID, distID);
			elemMDTO.appendChild(tempDoc.importNode(elemMDTO, true));

		} catch (Exception e1) {
			NWCGLoggerUtil.Log.warning("Exception occured");
			e1.printStackTrace();
		}
		NWCGLoggerUtil.Log.info("tempDoc ::" + XMLUtil.getXMLString(tempDoc));
		
		System.out.println("@@@@@ Exiting NWCGIBWebService::getTempDoc @@@@@");
		return tempDoc;
	}

	/**
	 * This method will return the unmarshalled object for a given context. This
	 * method instantiates a JAXBContext and unmarshalls the Document version of
	 * the ElementNSImpl
	 * 
	 * @param doc
	 * @param context
	 * @return Object
	 */
	protected Object getUnMarshallObject(Document doc, String strMessageName) {
		System.out.println("@@@@@ Entering NWCGIBWebService::getUnMarshallObject @@@@@");
		
		Object obj = null;
		NWCGLoggerUtil.Log
				.info("NWCGIBWebService::getUnMarshallObject(Document)");
		try {
			/*
			 * Unmarshaller unMarshaller = jc.createUnmarshaller();
			 * unMarshaller.setEventHandler(new
			 * javax.xml.bind.helpers.DefaultValidationEventHandler()); return
			 * unMarshaller.unmarshal(doc.getDocumentElement());
			 */
			return new NWCGJAXBContextWrapper().getObjectFromDocument(doc,
					strMessageName);
		} catch (JAXBException je) {
			NWCGLoggerUtil.Log
					.warning("NWCGIBWebService::getUnMarshallObjectForAContext, JAXBException : "
							+ je.getMessage());
			je.printStackTrace();
		} catch (Exception e) {
			NWCGLoggerUtil.Log
					.warning("NWCGIBWebService::getUnMarshallObjectForAContext, Exception : "
							+ e.getMessage());
		}
		
		System.out.println("@@@@@ Exiting NWCGIBWebService::getUnMarshallObject @@@@@");
		return obj;
	}

	protected Object getUnMarshallObject(Document doc, URL url) {
		System.out.println("@@@@@ Entering NWCGIBWebService::getUnMarshallObject @@@@@");
		
		Object obj = null;
		NWCGLoggerUtil.Log
				.info("NWCGIBWebService::getUnMarshallObject(Document)");
		try {
			/*
			 * Unmarshaller unMarshaller = jc.createUnmarshaller();
			 * unMarshaller.setEventHandler(new
			 * javax.xml.bind.helpers.DefaultValidationEventHandler()); return
			 * unMarshaller.unmarshal(doc.getDocumentElement());
			 */
			return new NWCGJAXBContextWrapper().getObjectFromDocument(doc, url);
		} catch (JAXBException je) {
			NWCGLoggerUtil.Log
					.warning("NWCGIBWebService::getUnMarshallObjectForAContext, JAXBException : "
							+ je.getMessage());
			je.printStackTrace();
		} catch (Exception e) {
			NWCGLoggerUtil.Log
					.warning("NWCGIBWebService::getUnMarshallObjectForAContext, Exception : "
							+ e.getMessage());
		}
		
		System.out.println("@@@@@ Exiting NWCGIBWebService::getUnMarshallObject @@@@@");
		return obj;
	}

	protected Document getMarshalledObject(Object obj, String strMessageName) {
		System.out.println("@@@@@ Entering NWCGIBWebService::getMarshalledObject @@@@@");
		
		Document doc = null;

		try {
			return new NWCGJAXBContextWrapper().getDocumentFromObject(obj, doc,
					strMessageName);
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (JAXBException e) {
			NWCGLoggerUtil.Log
					.warning("getMarshalledObject>>Exception during unmarshalling "
							+ e.getMessage());
			e.printStackTrace();
			return null;
		}
		
		System.out.println("@@@@@ Exiting NWCGIBWebService::getMarshalledObject @@@@@");
		return doc;
	}

	protected Document getMarshalledObject(Object obj, URL url) {
		System.out.println("@@@@@ Entering NWCGIBWebService::getMarshalledObject @@@@@");

		Document doc = null;
		try {
			return new NWCGJAXBContextWrapper().getDocumentFromObject(obj, doc,
					url);
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (JAXBException e) {
			NWCGLoggerUtil.Log
					.warning("getMarshalledObject>>Exception during unmarshalling "
							+ e.getMessage());
			e.printStackTrace();
			return null;
		}
		
		System.out.println("@@@@@ Exiting NWCGIBWebService::getMarshalledObject @@@@@");
		return doc;
	}

	protected String checkEncodingSet(String msg) {
		System.out.println("@@@@@ Entering NWCGIBWebService::checkEncodingSet @@@@@");
		
		if (msg.contains("encoding"))
			return msg;
		StringBuffer strBuf = new StringBuffer(
				NWCGWebServicesConstant.XMLENCODING);
		strBuf.append(msg);
		
		System.out.println("@@@@@ Exiting NWCGIBWebService::checkEncodingSet @@@@@");
		return strBuf.toString();
	}

	protected SOAPFault createSOAPFault(SOAPFaultException sfe, String faultMsg) {
		return NWCGSOAPFault.createParentSOAPFault(sfe, faultMsg);
	}

	protected SOAPFault createSOAPFault(String faultMsg) {
		return NWCGSOAPFault.createParentSOAPFault(null, faultMsg);
	}

	protected SOAPMessage makeSOAPMessage(Document msg) {
		System.out.println("@@@@@ Entering NWCGIBWebService::makeSOAPMessage @@@@@");
		
		try {
			MessageFactory factory = MessageFactory
					.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
			SOAPMessage message = factory.createMessage();
			SOAPPart part = message.getSOAPPart();
			SOAPEnvelope env = part.getEnvelope();
			SOAPBody body = env.getBody();
			body.addDocument(msg);

			System.out.println("@@@@@ Exiting NWCGIBWebService::makeSOAPMessage @@@@@");
			return message;
		} catch (Exception e) {
			System.out.println("@@@@@ Exiting NWCGIBWebService::makeSOAPMessage @@@@@");
			return null;
		}
	}

	protected SOAPMessage getSOAPMessageFromJAXBContextRO(
			GetOperationResultsResp resp) {
		System.out.println("@@@@@ Entering NWCGIBWebService::getSOAPMessageFromJAXBContextRO @@@@@");
		
		// Marshall the GetOperationResultsResp object to a Document object
		Document responseXML = getMarshalledObject(resp,
				"GetOperationResultsResp");

		// And return a SOAPMessage with the GetOperationResultsResp in the
		// SOAPBody
		
		System.out.println("@@@@@ Exiting NWCGIBWebService::getSOAPMessageFromJAXBContextRO @@@@@");
		return makeSOAPMessage(responseXML);
	}

	protected SOAPMessage getSOAPMessageFromJAXBContextRO(
			RegisterIncidentInterestResp resp) {
		System.out.println("@@@@@ Entering NWCGIBWebService::getSOAPMessageFromJAXBContextRO @@@@@");
		
		// Marshall the RegisterIncidentInterestResp object to a Document object
		Document responseXML = getMarshalledObject(resp,
				"RegisterIncidentInterestResp");

		// And return a SOAPMessage with the RegisterIncidentInterestResp in the
		// SOAPBody
		
		System.out.println("@@@@@ Exiting NWCGIBWebService::getSOAPMessageFromJAXBContextRO @@@@@");
		return makeSOAPMessage(responseXML);
	}

	protected SOAPMessage handleInternalProcessingFault(Exception e) {
		System.out.println("@@@@@ Entering NWCGIBWebService::handleInternalProcessingFault @@@@@");
		
		MessageAcknowledgement messageAckResp = null;
		SOAPMessage responseSOAPMsg = null;
		String distributionID = (String) msgMap
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID);

		// Setup Sterling Alert
		raiseNotificationAlert((String) msgMap
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY),
				(String) msgMap
						.get(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID));

		// Return the negative acknowledgement message DeliverNotificationResp
		// object
		if (e instanceof YFSException) {
			YFSException yfe = (YFSException) e;
			String yfsExceptionDesc = "Error received from Sterling: ";
			yfsExceptionDesc += yfe.getErrorCode();
			yfsExceptionDesc += ": " + yfe.getErrorDescription();
			messageAckResp = getMessageAcknowledgement(distributionID, false,
					yfsExceptionDesc);
		} else {
			messageAckResp = getMessageAcknowledgement(distributionID, false);
		}

		// Marshall the DeliverNotificationResp object to a Document object
		Document responseXML = null;
		try {
			responseXML = getMarshalledObject(messageAckResp, new URL(
					NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String responseXMLString = "Failed to set DeliveOperationResultsResp in OB Msg store";
		try {
			responseXMLString = XMLUtil.extractStringFromDocument(responseXML);
		} catch (TransformerException e1) {
			e1.printStackTrace();
		}

		// Update LATEST IB message store record to PROCESSING_FAULT and w/the
		// responseXML
		messageStore.setMsgStatusProcessingFault(msgMap, responseXMLString);

		responseSOAPMsg = makeSOAPMessage(responseXML);

		System.out.println("@@@@@ Exiting NWCGIBWebService::handleInternalProcessingFault @@@@@");
		return responseSOAPMsg;
	}
}