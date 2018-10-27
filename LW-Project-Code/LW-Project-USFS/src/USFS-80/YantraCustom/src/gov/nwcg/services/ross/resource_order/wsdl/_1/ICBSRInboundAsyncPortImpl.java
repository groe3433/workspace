package gov.nwcg.services.ross.resource_order.wsdl._1;

import gov.nwcg.services.ross.resource_order._1.CancelResourceRequestReq;
import gov.nwcg.services.ross.resource_order._1.ResourceOrderResponseType;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import javax.annotation.Resource;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerException;
import javax.xml.ws.Provider;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.soap.NWCGAAUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.ib.msg.NWCGGetOperationResultsIB;
import com.nwcg.icbs.yantra.webservice.ib.msg.NWCGInboundASyncMessage;
import com.nwcg.icbs.yantra.webservice.ib.msg.NWCGInboundSyncMessage;
import com.nwcg.icbs.yantra.webservice.ib.notification.NWCGIBNotification;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStoreInterface;
import com.nwcg.icbs.yantra.webservice.ob.msg.NWCGDeliverOperationResultsOB;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServicesConstant;

@ServiceMode(value = javax.xml.ws.Service.Mode.MESSAGE)
@WebServiceProvider(portName = "ICBSRInboundAsyncPort", serviceName = "ICBSRInboundAsyncService", targetNamespace = "http://nwcg.gov/services/ross/resource_order/wsdl/1.1", wsdlLocation = "WEB-INF/wsdl/Inbound_latest.wsdl")
public class ICBSRInboundAsyncPortImpl implements Provider<SOAPMessage> {
	
	@Resource
	WebServiceContext wsContext;

	private HashMap<String, String> myContext = new HashMap<String, String>();

	/**
	 * Entry point for all inbound asynchronous and synchronous web service
	 * calls
	 * 
	 * @param SOAPMessage
	 *            The requesting/calling SOAPMessage received
	 * @return SOAPMessage The response to the incoming request
	 */
	public SOAPMessage invoke(SOAPMessage soapRequest) {
		SOAPMessage soapMessageResponse = null;
		setupLocalContext(soapRequest);
		String serviceName = StringUtil.nonNull(myContext
				.get(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_SERVICENAME));
		String distributionType = StringUtil.nonNull(myContext
				.get(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_DIST_TYPE));
		String messageName = StringUtil.nonNull(myContext
				.get(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_MESSAGENAME));
		if (serviceName.equals(NWCGWebServicesConstant.SERVICE_NAME_PING)) {
			NWCGInboundSyncMessage syncPing = new NWCGInboundSyncMessage();
			syncPing.setContext(myContext);
			return syncPing.process();
		}
		// this will be a request object
		if (distributionType
				.equalsIgnoreCase(NWCGAAConstants.DISTR_TYPE_REQUEST)) {
			boolean isValidCustomProperty = validateCustomProperty();
			if (!isValidCustomProperty) {
				// TODO: Jay: return a soap fault b/c we don't know how to
				// handle, or do we default to
				// async or sync? isSync() right now defaults to async
			}

			if (messageName.equalsIgnoreCase("GetOperationResultsReq")) {
				// GetOperationsResultsReq is always synchronous
				NWCGGetOperationResultsIB getOpIB = new NWCGGetOperationResultsIB();
				getOpIB.setContext(myContext);
				soapMessageResponse = getOpIB.process();
			} else {
				boolean soapMsgIsSync = NWCGAAUtil
						.isSync(serviceName,
								myContext
										.get(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_CUSTOMPROP));

				if (serviceName.indexOf("otification", 0) != -1) {
					// IB Notifications are always async, process() will throw
					// a SOAPFaultException if customProperty is set to procc
					// sync=true
					NWCGIBNotification ibNotification = new NWCGIBNotification();
					ibNotification.setContext(myContext);
					soapMessageResponse = ibNotification.process();
				} else if (soapMsgIsSync) {
					// Call the synchronous implementation
					NWCGInboundSyncMessage ibSyncMsg = new NWCGInboundSyncMessage();
					ibSyncMsg.setContext(myContext);
					soapMessageResponse = ibSyncMsg.process();
				} else {
					// Call the asynchronous implementation
					NWCGInboundASyncMessage ibAsyncMsg = new NWCGInboundASyncMessage();
					ibAsyncMsg.setContext(myContext);
					soapMessageResponse = ibAsyncMsg.process();
				}
			}
		} else if (distributionType
				.equalsIgnoreCase(NWCGAAConstants.DISTR_TYPE_NOTIFICATION)) {
			// IB Notifications are always async, process() will throw
			// a SOAPFaultException if customProperty is set to procc sync=true
			NWCGIBNotification ibNotification = new NWCGIBNotification();
			ibNotification.setContext(myContext);
			soapMessageResponse = ibNotification.process();
		} else if (distributionType
				.equalsIgnoreCase(NWCGAAConstants.DISTR_TYPE_RESPONSE)) {
			if (serviceName
					.equalsIgnoreCase(NWCGAAConstants.DELIVER_OPERATION_RESULTS_REQ_MSG_NAME)) {
				/*
				 * Jay: sleeping the current thread because it has been observed
				 * from our log files that we are getting delivery response from
				 * ROSS before getting message ack and as per ROSS team they
				 * typically send response after 3-4 secs.
				 */
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				NWCGDeliverOperationResultsOB ob = new NWCGDeliverOperationResultsOB();
				ob.setContext(myContext);
				soapMessageResponse = ob.process();
			}
		}
		return soapMessageResponse;
	}

	public ResourceOrderResponseType cancelResourceRequest(
			CancelResourceRequestReq body) {
		MessageContext mc = wsContext.getMessageContext();
		System.out
				.print("ICBSRInboundAsyncPortImpl.placeResourceRequestExternal context="
						+ mc);
		// printInfo(mc.keySet());

		NWCGInboundSyncMessage syncMsg = new NWCGInboundSyncMessage();
		syncMsg.setContext(((SOAPMessageContext) mc));
		// return syncMsg.process();
		// actual implementation to be done
		return null;
	}

	/**
	 * 
	 * @return true if
	 *         soapenv:Envelope/soapenv:Header/ns:MessageContext/customProperties
	 *         isn't blank/empty, contains "ProcessSynchronously=true or false".
	 *         false otherwise
	 */
	private boolean validateCustomProperty() {
		String customProp = myContext
				.get(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_CUSTOMPROP);// ("CustomProp");
		boolean isValid = false;

		if (!StringUtil.isEmpty(customProp)
				&& customProp
						.startsWith(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_PROCESSSYNC)) {
			String propValue = customProp
					.substring(customProp.indexOf("=") + 1);

			if (propValue.equalsIgnoreCase("true")
					|| propValue.equalsIgnoreCase("false"))
				isValid = true;
		}
		return isValid;
	}

	/**
	 * Returns a SOAPMessage into it's string form
	 * 
	 * @param msg
	 * @return String representing the entire SOAPMessage
	 */
	private String getSOAPMessageAsString(SOAPMessage msg) {
		ByteArrayOutputStream baos = null;
		String s = null;
		try {
			baos = new ByteArrayOutputStream();
			msg.writeTo(baos);
			s = baos.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public boolean setupLocalContext(SOAPMessage req) {
		Document doc = null;
		/*
		 * ServletContext object is required because we need to drill through
		 * the SOAP Header information and store the required data(distribution
		 * ID, one time password, security session id, message etc) in the
		 * ServletContext and retrieve it later in the main implementation
		 * class.
		 */
		SOAPBody soapBody = null;
		try {
			SOAPPart sp = req.getSOAPPart();
			SOAPEnvelope se = sp.getEnvelope();
			soapBody = se.getBody();
			doc = soapBody.extractContentAsDocument();
		} catch (SOAPException se1) {
			se1.printStackTrace();
		}
		String serviceName = doc.getDocumentElement().getLocalName();
		myContext.put(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_SERVICENAME,
				serviceName);
		// dont need to do anything if it is ping service request
		if (serviceName.equals(NWCGWebServicesConstant.SERVICE_NAME_PING)) {
			return true;
		}
		// Added the if condition because for notification messages i need to
		// store a different message
		// in servlet context as compared to request response messages.
		if (serviceName
				.equals(NWCGWebServicesConstant.SOAP_HEADER_NOTIFICATION)) {
			NodeList nl = doc.getDocumentElement().getChildNodes();
			for (int c = 0; c < nl.getLength(); c++) {
				Object obj = nl.item(c);
				if (obj instanceof org.apache.xerces.dom.ElementImpl
						|| obj instanceof org.apache.xerces.dom.ElementNSImpl) {
					Element objElem = (Element) obj;
					String objElemLocalName = objElem.getLocalName();
					if (objElemLocalName
							.equals(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_NOTIFIEDSYSTEM)) {
						myContext
								.put(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_NOTIFIEDSYSTEM,
										XMLUtil.getElementXMLString(objElem));
						accessHeaderInformation(req, objElemLocalName);
					}
				}
			}
		} else {
			// had to add this condition because for some unknown reason it ???
			// is coming to the handler after returning MA
			if (!serviceName
					.equals(NWCGWebServicesConstant.SOAP_HEADER_SERVICE_MSGACK)) {
				accessHeaderInformation(req, serviceName);
			}
		}
		Element eRoot = doc.getDocumentElement();
		// Jay: setting these attributes here instead of getting the document
		// from context, creating the document and then setting up attributes
		// later
		// in NWCGBusinessMsgProcessor.java
		eRoot.setAttribute(NWCGAAConstants.MDTO_DISTID, (String) myContext
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID));
		eRoot.setAttribute(NWCGAAConstants.MDTO_MSGNAME,
				myContext.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME));
		eRoot.setAttribute(NWCGAAConstants.MDTO_NAMESPACE, (String) myContext
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_NAMESPACE));
		try {
			myContext.put(NWCGWebServicesConstant.SOAP_ELEMENT_MESSAGEBODY,
					XMLUtil.extractStringFromDocument(doc));
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * This method accesses the SOAP Header containing the Security and
	 * MessageContext elements.
	 * 
	 * @param soapBodyChildStr
	 * @param pwdValue
	 * @param header
	 * @param message
	 * @param ctx
	 * @param serviceName
	 */
	private void accessHeaderInformation(SOAPMessage req, String serviceName) {
		String pwdValue = NWCGConstants.EMPTY_STRING;
		String secSessionID = NWCGConstants.EMPTY_STRING;
		String namespaceName = NWCGConstants.EMPTY_STRING;
		SOAPHeader header = null;
		try {
			header = req.getSOAPHeader();
		} catch (SOAPException e) {
			e.printStackTrace();
		}
		// Below code is under the assumption that inbound
		// notification message will have SOAP-Header element.
		Iterator iter = header.getChildElements();
		while (iter.hasNext()) {
			Object obj = iter.next();
			if (obj instanceof com.ibm.ws.webservices.engine.xmlsoap.Comment) {
				continue;
			}
			if (obj instanceof com.ibm.ws.webservices.engine.xmlsoap.Text) {
			} else {
				SOAPElement soapelem = (SOAPElement) obj;
				String soaplocalname = soapelem.getLocalName();
				if (soaplocalname
						.equals(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_SECURITY)) {
					if (soapelem instanceof com.ibm.ws.webservices.engine.xmlsoap.SOAPHeaderElement) {
						com.ibm.ws.webservices.engine.xmlsoap.SOAPHeaderElement soapHdrElem = (com.ibm.ws.webservices.engine.xmlsoap.SOAPHeaderElement) soapelem;
						try {
							Iterator childElemsIter = soapHdrElem
									.getChildElements();
							while (childElemsIter.hasNext()) {
								Object obj1 = childElemsIter.next();
								if (obj1 instanceof com.ibm.ws.webservices.engine.xmlsoap.Text) {
								} else {
									SOAPElement soapElem = (SOAPElement) obj1;
									Iterator soapElemIter = soapElem
											.getChildElements();
									while (soapElemIter.hasNext()) {
										Object soapElemObj = soapElemIter
												.next();
										if (soapElemObj instanceof com.ibm.ws.webservices.engine.xmlsoap.Text) {
										} else {
											SOAPElement soapElemObjElem = (SOAPElement) soapElemObj;
											// Accessing and storing the
											// password in ServletContext
											if (soapElemObjElem
													.getLocalName()
													.equals(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_PASSWORD)) {
												pwdValue = soapElemObjElem
														.getTextContent();
												// Set the password into message
												// context.
												myContext
														.put(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_PASSWORD,
																pwdValue);
											}
										}
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					try {
						if (soapelem instanceof com.ibm.ws.webservices.engine.xmlsoap.SOAPHeaderElement) {
							com.ibm.ws.webservices.engine.xmlsoap.SOAPHeaderElement soapHdrElemMC = (com.ibm.ws.webservices.engine.xmlsoap.SOAPHeaderElement) soapelem;
							Iterator soapHdrElemMCIter = soapHdrElemMC
									.getChildElements();
							while (soapHdrElemMCIter.hasNext()) {
								Object objMCChildObj = soapHdrElemMCIter.next();
								if (objMCChildObj instanceof com.ibm.ws.webservices.engine.xmlsoap.Text) {
								} else {
									SOAPElement soapMCElem = (SOAPElement) objMCChildObj;
									if (soapMCElem
											.getLocalName()
											.equalsIgnoreCase(
													NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_MESSAGENAME)) {
										String messageName = soapMCElem
												.getTextContent();
										myContext
												.put(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_MESSAGENAME,
														messageName);
									}
									// Accessing and storing the distributionID
									// in ServletContext
									if (soapMCElem
											.getLocalName()
											.equalsIgnoreCase(
													NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_DISTID)) {
										String distID = soapMCElem
												.getTextContent();
										myContext
												.put(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_DISTID,
														distID);
									}
									// Accessing and storing the distributionID
									// in ServletContext
									if (soapMCElem
											.getLocalName()
											.equalsIgnoreCase(
													NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_SYSTEMID)) {
										String sysID = soapMCElem
												.getTextContent();
										myContext
												.put(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_SYSTEMID,
														sysID);
									}
									// Accessing and storing the
									// customProperties in ServletContext
									if (soapMCElem
											.getLocalName()
											.equalsIgnoreCase(
													NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_CUSTOMPROP)) {
										String customProp = soapMCElem
												.getTextContent();
										// Since we are reading all custom
										// properties here not just
										// ProcessSynchronously removing this
										// condition
										// if(customProp.startsWith(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_PROCESSSYNC))
										{
											myContext
													.put(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_CUSTOMPROP,
															customProp);
										}
									}

									// Accessing the securitySessionID element
									// value from the MessageContext Element
									if (soapMCElem
											.getLocalName()
											.equalsIgnoreCase(
													NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_SECURITYSESSIONID)) {
										secSessionID = soapMCElem
												.getTextContent();
										myContext
												.put(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_SECURITYSESSIONID,
														secSessionID);
										// Assuming security session id should
										// be equal to user login id
									}
									// Accessing the namespaceName element value
									// from the MessageContext Element
									if (soapMCElem.getLocalName()
											.equalsIgnoreCase("namespaceName")) {
										namespaceName = soapMCElem
												.getTextContent();
										myContext
												.put(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_NAMESPACE,
														namespaceName);
									}
									if (soapMCElem.getLocalName()
											.equalsIgnoreCase(
													"distributionType")) {
										String distributionType = soapMCElem
												.getTextContent();
										myContext
												.put(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_DIST_TYPE,
														distributionType);
									}

								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}