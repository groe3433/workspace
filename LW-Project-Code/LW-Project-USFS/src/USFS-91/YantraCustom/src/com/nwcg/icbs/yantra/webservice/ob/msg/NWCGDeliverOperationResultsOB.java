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

import gov.nwcg.services.ross.common_types._1.ResponseMessageType;
import gov.nwcg.services.ross.common_types._1.ResponseStatusType;
import gov.nwcg.services.ross.resource_order._1.DeliverOperationResultsReq;
import gov.nwcg.services.ross.resource_order._1.DeliverOperationResultsResp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.commCore.NWCGProperties;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.business.NWCGBusinessMsgProcessor;
import com.nwcg.icbs.yantra.webservice.ib.NWCGIBWebService;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStore;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStoreInterface;
import com.nwcg.icbs.yantra.webservice.ob.notification.NWCGOBNotification;
import com.nwcg.icbs.yantra.webservice.util.NWCGJAXBContextWrapper;
import com.nwcg.icbs.yantra.webservice.util.alert.NWCGAlert;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServicesConstant;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGDeliverOperationResultsOB extends NWCGIBWebService {

	private static YFCLogCategory logger = NWCGROSSLogger
			.instance(NWCGDeliverOperationResultsOB.class);

	NWCGMessageStoreInterface msgStore = NWCGMessageStore.getMessageStore();

	DeliverOperationResultsReq input = null;

	private static final String className = NWCGDeliverOperationResultsOB.class.getName();
	
	/**
	 * 1.A&A ICBSRInboundAsyncPortImpl receives SOAPMessage with name
	 * DeliverOperationResultsReq 2 DeliverOperationResultsReq has no Soap
	 * Header. Distribution ID, Message Name are extracted from the body. 3 This
	 * method updates message status to RESPONSE_RECEIVED. Authentication and
	 * Authorization is NOT performed. 4 A&A verifies that Distribution ID and
	 * Message Name corresponds to the original Request 5 If combination of
	 * Distribution ID and Message Name does not match in the database, A&A
	 * sends SoapFault back and set status to PROCESSING_FAULT 6 A&A forward
	 * message to Sterling App to process synchronously. 7 If A&A receives NO
	 * errors or exception from API, A&A sends Result message back to ROSS and
	 * set status to PROCESSED 8 If A&A receives error or exception from
	 * Sterling App, it sets status to PROCESSING_FAULT and sends Negative
	 * response to ROSS. A&A creates an Alert in NWCG_FAULT queue with Alert
	 * properties AlertSet3.1.3
	 * 
	 * DeliverOperationResultsReq may come with negative status, but we do not
	 * handle it here, it should be pick up and processed by Sterling API side
	 * (per Jay)
	 * 
	 * @return
	 */
	public SOAPMessage process() {
		logger.verbose("@@@@@ Entering NWCGDeliverOperationResultsOB::process");
		final String methodName = "process";
		String messageBodyXML = getPropertyFromContext(NWCGWebServicesConstant.SOAP_ELEMENT_MESSAGEBODY);
		SOAPMessage response = null;
		NWCGMessageStoreInterface messageStore = NWCGMessageStore.getMessageStore();
		DeliverOperationResultsResp resp = null;
		try {
			// Initialize the message map containing values from the messageContext
			msgMap = setupMessageMap();
			// Create START / VOID and LATEST / RECEIVED NWCG_INBOUND_MESSAGE records. Default distribution ID from UUID
			messageStore.initInboundMessageFromMap(msgMap);
		} catch (Exception e) {
			logger.error("Failure to initialize IB message store");
			logger.error(e.getLocalizedMessage(), e);
			// response = handleInternalProcessingFault(e);
			return response;
		}
		// Unmarshall the input XML to a DeliverOperationResultsReq object
		try {
			Document reqst = XMLUtil.getDocument((String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY));
			logger.verbose("NWCGDeliverOperationResultsOB message received:  " + XMLUtil.extractStringFromDocument(reqst));
			input = (DeliverOperationResultsReq) new NWCGJAXBContextWrapper().getObjectFromDocument(reqst, new URL(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
		} catch (Exception e) {
			logger.error("FAILED TO PARSE INPUT MSG VIA JAXB");
			logger.error(e.getLocalizedMessage(), e);
			response = handleInternalProcessingFault(e);
			return response;
		}
		try {
			// Determine whether we need to call verifySecuritySession for this DeliverOperationResultsReq message
			boolean deliveryReqRequiresAuth = requiresSecurityAuthentication();
			boolean securityVerified = false;
			// Right now, verifyMesssageSecurity() will never return false because it will throw up an exception before returning a false boolean
			logger.verbose("@@@@@ deliveryReqRequiresAuth :: " + deliveryReqRequiresAuth);
			if (deliveryReqRequiresAuth) {
				securityVerified = verifyMessageSecurity();
				logger.verbose("@@@@@ securityVerified :: " + securityVerified);
			}
			if (securityVerified || !deliveryReqRequiresAuth) {
				// Within the same thread of execution delivery the xsAny payload to the proper handler for OB message delivery updates Verify that Distribution ID exists in the root/DistributionID elm
				String distributionId = getDistributionIDFromXMLBody(messageBodyXML);
				// Get the message name from the local name of the xs any element of the DeliverOperationResultsReq message.
				String messageNameFromXsAnyElm = getMessageNameFromXsAnyElm();
				msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME, messageNameFromXsAnyElm);
				// Determine the name of the original outbound request
				String originalRequestName = getOriginalRequestName(messageNameFromXsAnyElm);
				// Determine whether we have a record in the NWCG_OUTBOUND_MESSAGE table for the distribution ID received and original request name
				HashMap<String, String> mapMsgStore = msgStore.checkMessageNameAndDistID(distributionId, originalRequestName);
				String latestMsgKey = mapMsgStore.get(NWCGAAConstants.LATEST_MESSAGE_KEY);
				mapMsgStore.put(NWCGMessageStoreInterface.MESSAGE_MAP_LATESTKEY, latestMsgKey);
				mapMsgStore.put(NWCGAAConstants.MDTO_DISTID, distributionId);
				mapMsgStore.put(NWCGMessageStoreInterface.MESSAGE_MAP_SYSNAME, "ROSS");
				msgMap.put(NWCGAAConstants.ENTITY_NAME, mapMsgStore.get(NWCGAAConstants.ENTITY_NAME));
				msgMap.put(NWCGAAConstants.ENTITY_KEY, mapMsgStore.get(NWCGAAConstants.ENTITY_KEY));
				msgMap.put(NWCGAAConstants.ENTITY_VALUE, mapMsgStore.get(NWCGAAConstants.ENTITY_VALUE));
				Document responseXML = null;
				// The original outbound request has been found
				if (latestMsgKey != null) {
					try {
						// Adding this method to put the miscellaneous fields that will be used in the individual process handlers. forward returned results to sync Sterling Service. no need for return
						new NWCGBusinessMsgProcessor().forwardDeliveryResults(msgMap);
						logger.verbose("@@@@@ Called NWCGBusinessMsgProcessor");
						// no exceptions -send positive resp back to ROSS and set status to PROCESSED
						resp = createDeliverOperationResultsResp();
						logger.verbose("@@@@@ Created DeliverOperationResultsResp");
						// Marshall the DeliverNotificationResp object to a Document object
						try {
							responseXML = getMarshalledObject(resp, new URL(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
						} catch (MalformedURLException e1) {
							//logger.printStackTrace(e1);
						}
						String responseXMLString = XMLUtil.extractStringFromDocument(responseXML);
						msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, responseXMLString);
						msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_LATESTKEY, latestMsgKey);
						msgMap.put(NWCGAAConstants.MDTO_DISTID, distributionId);
						msgStore.setMsgStatusOutboundProcessed(msgMap);
					} catch (Exception e) {
						logger.error("NWCGDeliverOperationResultsOB::process, Exception : " + e.getMessage());
						logger.error(e.getLocalizedMessage(), e);
						// set status to PROCESSING_FAULT and send Negative response to ROSS, create Alert
						resp = returnDeliverOperationResultsNegative(NWCGAAConstants.DELIVER_OPERATION_RESULTS_FAILURE_DESC_3);
						msgStore.setMsgStatusProcessingFault(msgMap, e.toString());
						createDeliveryFailedAlert(distributionId, messageBodyXML, e, null);
					}
				} else {
					// Original request not found in outbound message store Couldn't find the distributionID/originalMessageNameRequest combination in the outbound message store. Create and throw a SOAPFaultException.
					SOAPFault faultMsg = createSOAPFault(NWCGWebServicesConstant.SOAP_FAULT_MESSAGE_DOES_NOT_EXIST);
					logger.warn("!!!!! Sending Fault string=" + faultMsg.getFaultString());
					throw new SOAPFaultException(faultMsg);
				}
				response = makeSOAPMessage(responseXML);
			}
		} catch (SOAPFaultException sfe) {
			// SOAPFault thrown by verifyMessageSecurity on
			// - empty password
			// - failure to validate user/pass
			// - unable to post to ROSS authentication webservice
			logger.error("!!!!! Caught SOAPFaultException :: " + sfe.getMessage());
			logger.error(sfe.getLocalizedMessage(), sfe);
			sfe.printStackTrace();
			handleSecurityFailureSoapFault(sfe);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e.getMessage());
			logger.error(e.getLocalizedMessage(), e);
			e.printStackTrace();
			response = handleInternalProcessingFault(e);
		}
		logger.verbose("@@@@@ Exiting NWCGDeliverOperationResultsOB::process");
		return response;
	}

	private String getMessageNameFromXsAnyElm() {
		logger.verbose("@@@@@ Entering NWCGDeliverOperationResultsOB::getMessageNameFromXsAnyElm");
		String retVal = null;
		String soapMsgBody = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY);
		try {
			Document deliverOpResultsReqDoc = XMLUtil.getDocument(soapMsgBody);
			Element docElm = deliverOpResultsReqDoc.getDocumentElement();
			NodeList nl = docElm.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if (n instanceof Element) {
					Element e = (Element) n;
					logger.verbose("@@@@@ n.getLocalName() :: " + n.getLocalName());
					if (!n.getLocalName().equalsIgnoreCase(
							NWCGConstants.DIST_ID_ATTR)) {
						retVal = n.getLocalName();
						msgMap.put("xsAny", e);
						break;
					}
				}
			}
		} catch (ParserConfigurationException e) {
			logger.error(e.getLocalizedMessage(), e);
		} catch (SAXException e) {
			logger.error(e.getLocalizedMessage(), e);
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		logger.verbose("@@@@@ Exiting NWCGDeliverOperationResultsOB::getMessageNameFromXsAnyElm");
		return retVal;
	}

	/**
	 * Determines whether we need to call the verifySecuritySession ROSS web
	 * service based on whether the <serviceName>.requiresAuth=TRUE or not. This
	 * method also sets the ElementNS reference in the class msgMap for later
	 * use by the NWCGOBCatalogProcessHandler class <br>
	 * <br>
	 * Note: The values of true, false, yes, or no in NWCGAnAImpl.properties are
	 * case-insensitive.<br>
	 * <br>
	 * 
	 * @return boolean true If DeliverOperationResultsReq.requiresAuth=TRUE (or
	 *         YES) according to
	 *         $CLASSPATH/resources/extn/NWCGAnaImpl.properties <br>
	 */
	public boolean requiresSecurityAuthentication() {
		logger.verbose("@@@@@ Entering NWCGDeliverOperationResultsOB::requiresSecurityAuthentication");
		boolean deliveryRequiresAuth = true;
		String serviceName = (String) msgMap
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_SERVNAME);

		String requiresAuth = NWCGProperties.getProperty(serviceName
				+ ".requiresAuth");

		if (!StringUtil.isEmpty(requiresAuth)) {
			if (requiresAuth.equalsIgnoreCase("FALSE")
					|| requiresAuth.equalsIgnoreCase("NO"))
				deliveryRequiresAuth = false;
		} else {
			// If it the property name/value pair isn't found
			logger.warn(serviceName
					+ ".requiresAuth not configured in A&A property file!"
					+ " Defaulting to yes/TRUE");
		}
		logger.verbose("@@@@@ Exiting NWCGDeliverOperationResultsOB::requiresSecurityAuthentication");
		return deliveryRequiresAuth;
	}

	/**
	 * Gets the xsAny sibling element of DistributionID on
	 * DeliverOperationResultsReq messages.
	 * 
	 * @param String
	 *            messageBodyXML A String representing the entire SOAPBody child
	 *            element.
	 * @return <code>String</code> DistributionID text node value of
	 *         DeliverOperationResultsReq message
	 */
	private String getDistributionIDFromXMLBody(String messageBodyXML) {
		logger.verbose("@@@@@ Entering NWCGDeliverOperationResultsOB::getDistributionIDFromXMLBody");
		String distributionId = null;
		try {
			Document deliverOperationResultsReq = XMLUtil.getDocument(messageBodyXML);
			Element docElm = deliverOperationResultsReq.getDocumentElement();
			Element respDistIdElm = XMLUtil.getFirstElementByName(docElm, NWCGAAConstants.DELIVER_OPERATION_RESULTS_DISTID_ELM);
			if (respDistIdElm == null) {
				respDistIdElm = XMLUtil.getFirstElementByName(docElm, NWCGAAConstants.DELIVER_OPERATION_RESULTS_DISTID_ELM);
			}
			if (respDistIdElm != null) {
				distributionId = XMLUtil.getNodeValue(respDistIdElm);
			}
		} catch (ParserConfigurationException e) {
			logger.error(e.getLocalizedMessage(), e);
		} catch (SAXException e) {
			logger.error(e.getLocalizedMessage(), e);
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		logger.verbose("@@@@@ Exiting NWCGDeliverOperationResultsOB::getDistributionIDFromXMLBody");
		return distributionId;
	}

	private String getOriginalRequestName(String messageName) {
		logger.verbose("@@@@@ In NWCGDeliverOperationResultsOB::getOriginalRequestName");
		return NWCGProperties.getProperty(messageName.concat(NWCGAAConstants.HANDLER_PROP_ORIG_REQ_NAME));
	}

	protected HashMap<Object, Object> setupMessageMap(String distID, String messageName, String messageBodyXML, DeliveryStatusBean dsb) {
		logger.verbose("@@@@@ Entering NWCGDeliverOperationResultsOB::setupMessageMap");
		try {
			// we are passing this as an object
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID, distID);
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_SERVNAME, "DeliverOperationResults");
			setupMessageMapBase();
		} catch (Exception e) {
			msgStore.setMsgStatusProcessingFault(msgMap, e.toString());
			createDeliveryFailedAlert(distID, messageBodyXML, e, dsb);
		}
		logger.verbose("@@@@@ Exiting NWCGDeliverOperationResultsOB::setupMessageMap");
		return msgMap;
	}

	private void createDeliveryFailedAlert(String distId, String messageBodyXML, Exception alertException, DeliveryStatusBean dsb) {
		logger.verbose("@@@@@ Entering NWCGDeliverOperationResultsOB::createDeliveryFailedAlert");
		// Creates an Alert in NWCG_FAULT queue with Alert properties AlertSet3.1.3
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		String messageBody = null;
		if (dsb != null) {
			map.put("Return Code", dsb.getReturnCode().trim());
			map.put("Code", dsb.getCode());
			map.put("Severity", dsb.getSeverity());
			map.put("Description", dsb.getDescription());
		}
		map.put(NWCGConstants.DIST_ID_ATTR, distId);
		map.put(NWCGAAConstants.NAME, messageBody);

		try {
			NWCGAlert.createDeliveryFailedAlert(map, alertException);
		} catch (Exception e) {
			logger.error("exception in NWCGDeliverOperationResultsIB.createDeliveryFailedAlert");
			logger.error(e.getLocalizedMessage(), e);
		}
		logger.verbose("@@@@@ Exiting NWCGDeliverOperationResultsOB::createDeliveryFailedAlert");
	}

	/**
	 * Creates the DeliverOperationResultsResp JAXB POJO for sending back
	 * sync'ly to calling system
	 * 
	 * @author
	 * 
	 * @return DeliverOperationResultsResp Object filled method for returning
	 *         different types of response
	 */
	private DeliverOperationResultsResp createDeliverOperationResultsResp() {
		logger.verbose("@@@@@ Entering NWCGDeliverOperationResultsOB::createDeliverOperationResultsResp");
		DeliverOperationResultsResp resp = new DeliverOperationResultsResp();
		ResponseStatusType respStat = new ResponseStatusType();
		respStat.setReturnCode(NWCGAAConstants.DELIVER_OPERATION_RESULTS_RETURN_SUCCESS_CODE);
		ResponseMessageType respType = new ResponseMessageType();
		respType.setCode("0"); 
		respType.setDescription(NWCGAAConstants.DELIVER_OPERATION_RESULTS_SUCCESS_DESC);
		respType.setSeverity(NWCGAAConstants.DELIVER_OPERATION_RESULTS_SEVERITY_2);
		List<ResponseMessageType> list = respStat.getResponseMessage();
		list.add(respType);
		resp.setResponseStatus(respStat);
		logger.verbose("@@@@@ Exiting NWCGDeliverOperationResultsOB::createDeliverOperationResultsResp");
		return resp;
	}

	/**
	 * @author
	 * @param str
	 * @param a
	 * @return method for determining different types of response
	 */
	private DeliverOperationResultsResp returnDeliverOperationResultsNegative(String faultDescription) {
		logger.verbose("@@@@@ Entering NWCGDeliverOperationResultsOB::returnDeliverOperationResultsNegative");
		DeliverOperationResultsResp resp = new DeliverOperationResultsResp();
		logger.verbose("returnDeliverOperationResultsFault>>Forming a response" + faultDescription);
		ResponseStatusType respStat = new ResponseStatusType();
		respStat.setReturnCode(NWCGAAConstants.DELIVER_OPERATION_RESULTS_RETURN_FAILURE_CODE);
		ResponseMessageType respMsg = new ResponseMessageType();
		respMsg.setCode(NWCGAAConstants.DELIVER_OPERATION_RESULTS_MESSAGECODE_1);
		respMsg.setDescription(faultDescription);
		respMsg.setSeverity(NWCGAAConstants.DELIVER_OPERATION_RESULTS_SEVERITY_1);
		List<ResponseMessageType> list = respStat.getResponseMessage();
		list.add(respMsg);
		resp.setResponseStatus(respStat);
		logger.verbose("returnDeliverOperationResultsResp::Response ==> " + resp);
		logger.verbose("@@@@@ Exiting NWCGDeliverOperationResultsOB::returnDeliverOperationResultsNegative");
		return resp;
	}
}