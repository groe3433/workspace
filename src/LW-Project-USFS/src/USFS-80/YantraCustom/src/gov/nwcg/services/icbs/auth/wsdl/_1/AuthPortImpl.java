package gov.nwcg.services.icbs.auth.wsdl._1;

import java.util.List;
import java.util.StringTokenizer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServiceUtils;
import gov.nwcg.services.ross.common_types._1.AuthUserReq;
import gov.nwcg.services.ross.common_types._1.AuthUserResp;
import gov.nwcg.services.ross.common_types._1.AuthUserReq;
import gov.nwcg.services.ross.common_types._1.AuthUserResp;
import gov.nwcg.services.ross.common_types._1.ResponseMessageType;
import gov.nwcg.services.ross.common_types._1.ResponseStatusType;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.TransformerException;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.commCore.NWCGTimeKeyManager;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServiceUtils;
import com.nwcg.icbs.yantra.util.common.XMLUtil;

@javax.jws.HandlerChain(file = "AuthHandler.xml")
@javax.jws.WebService(endpointInterface = "gov.nwcg.services.icbs.auth.wsdl._1.AuthInterface", targetNamespace = "http://nwcg.gov/services/icbs/auth/wsdl/1.1", serviceName = "AuthService", portName = "AuthPort")
public class AuthPortImpl {

	public String msgIDSoapsuccess = "";

	@Resource
	private WebServiceContext wsContext;

	public AuthUserResp authUser(AuthUserReq body) {
		AuthUserResp auresp = null;
		Document opXML = null;
		String firstName = "";
		String lastname = "";
		String activateFlag = "";
		// Get the temporary SOAPMessage from SOAPMessageContext
		MessageContext mc = wsContext.getMessageContext();
		SOAPMessageContext smc = (SOAPMessageContext) mc;
		ServletContext context = (ServletContext) smc
				.get(SOAPMessageContext.SERVLET_CONTEXT);
		Document message = (Document) context.getAttribute("tempDoc");
		String username = body.getUsername();
		opXML = getUserListFromXML(body, username);
		if (opXML != null) {
			// Get User full name
			if (opXML.getChildNodes() != null) {
				Element UserElem = (Element) opXML.getDocumentElement()
						.getElementsByTagName("User").item(0);
				Element contactPersonInfoElem = (Element) UserElem
						.getElementsByTagName("ContactPersonInfo").item(0);
				if (contactPersonInfoElem != null) {
					firstName = contactPersonInfoElem.getAttribute("FirstName");
					lastname = contactPersonInfoElem.getAttribute("LastName");
					if (firstName == null || firstName.equals(""))
						firstName = "NWCG";

					if (lastname == null || lastname.equals(""))
						lastname = "Admin";
				}
				// Get the activate flag
				activateFlag = UserElem.getAttribute("Activateflag");
			}
		}
		// valide the latest_system_key which is infact the message key
		boolean bool = verifyLatestSystemKey(body.getOnetimePassword());
		try {
			if (bool == true) {
				if (NWCGTimeKeyManager.verifyKey(body.getOnetimePassword())) {
					auresp = createSOAPResponseSuccessMessage(body, firstName,
							lastname);
				} else {
					auresp = createSOAPResponseFailureMessage(body, opXML,
							message, firstName, lastname, activateFlag, "");
				}
			} else {
				// SOAP fault for invalid message key. TBD
				auresp = createSOAPResponseFailureMessage(body, opXML, message,
						firstName, lastname, activateFlag, "IM");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return auresp;
	}

	/**
	 * @param body
	 * @param opXML
	 * @param username
	 * @return
	 */
	private Document getUserListFromXML(AuthUserReq body, String username) {
		Document opXML = null;
		try {
			Document ipXML = XMLUtil.getDocument("<User Loginid='" + username
					+ "' />");
			Document templateXML = XMLUtil
					.getDocument(NWCGAAConstants.TEMPLATE_GETUSERLIST);
			opXML = NWCGWebServiceUtils.invokeAPI(
					NWCGAAConstants.API_GETUSERLIST, ipXML, templateXML);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return opXML;
	}

	/**
	 * Verifies the latest system key which is in fact the message key.
	 */
	private boolean verifyLatestSystemKey(String onetimePassword) {
		String latestSystemKey = "";
		try {
			StringTokenizer token = new StringTokenizer(onetimePassword, "-");
			while (token.hasMoreTokens()) {
				latestSystemKey = token.nextToken();
				break;
			}
			if (latestSystemKey
					.equals(NWCGAAConstants.NWCG_DO_NO_AUTH_DB_KEY_2)) {
				return true;
			}
			// Check to first see if we find the message key in the OB msg store
			Document ipXML = XMLUtil
					.getDocument("<NWCGOutboundMessage MessageKey=\""
							+ latestSystemKey + "\" />");
			Document opXML = NWCGWebServiceUtils
					.invokeServiceMethod(
							NWCGAAConstants.SDF_GET_OB_MESSAGE_LIST_SERVICE_NAME,
							ipXML);
			NodeList nlNWCGOutboundMessage = opXML
					.getElementsByTagName("NWCGOutboundMessage");
			if (nlNWCGOutboundMessage != null
					&& nlNWCGOutboundMessage.getLength() >= 1) {
				return true;
			} else {
				// Check to first see if we find the message key in the IB msg
				// store
				ipXML = XMLUtil.getDocument("<NWCGInboundMessage MessageKey=\""
						+ latestSystemKey + "\" />");
				opXML = NWCGWebServiceUtils.invokeServiceMethod(
						NWCGAAConstants.SDF_GET_IB_MESSAGE_LIST_SERVICE_NAME,
						ipXML);
				NodeList nl = opXML.getElementsByTagName("NWCGInboundMessage");
				if (nl != null) {
					if (nl.getLength() >= 1) {
						return true;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private AuthUserResp createSOAPResponseSuccessMessage(AuthUserReq req,
			String firstName, String lastName) {
		AuthUserResp auresp = new AuthUserResp();
		ResponseStatusType respStatusType = new ResponseStatusType();
		respStatusType.setReturnCode(NWCGAAConstants.SOAP_SUCCESS_CODE);
		auresp.setResponseStatus(respStatusType);
		ResponseMessageType respMessageType = new ResponseMessageType();
		respMessageType.setCode(NWCGAAConstants.SOAP_SUCCESS_MESSAGE_CODE);
		respMessageType.setSeverity(NWCGAAConstants.SEVERITY_SUCCESS);
		respMessageType
				.setDescription(NWCGAAConstants.SOAP_SUCCESS_RESPONSE_MESSAGE);
		List<ResponseMessageType> lst = respStatusType.getResponseMessage();
		lst.add(respMessageType);
		auresp.setUsername(req.getUsername());
		auresp.setIsAuthenticatedIndicator(NWCGAAConstants.SOAP_IS_AUTHENTICATION_INDICATOR_TRUE);
		auresp.setUserFullname(firstName + " " + lastName);
		auresp.setAuthenticationMessage(NWCGAAConstants.SOAP_AUTHENTICATION_MESSAGE_SUCCESS);
		return auresp;
	}

	/*
	 * This method evaulates all failure scenarios and generates the appropriate
	 * AuthUserResp message. Default functionality - Time sensitive key
	 */
	private AuthUserResp createSOAPResponseFailureMessage(AuthUserReq reqBody,
			Document doc, Document message, String firstName, String lastname,
			String activateFlag, String flagIM) {
		AuthUserResp auresp = new AuthUserResp();
		ResponseStatusType respStatusType = new ResponseStatusType();
		List<ResponseMessageType> lst = respStatusType.getResponseMessage();
		ResponseMessageType respMessageType = new ResponseMessageType();
		Element UserElem = (Element) doc.getDocumentElement()
				.getElementsByTagName("User").item(0);
		String login = UserElem.getAttribute("Loginid");
		if (doc.getDocumentElement().getElementsByTagName("User").item(0) == null) {
			respStatusType.setReturnCode(NWCGAAConstants.SOAP_FAILURE_CODE);
			auresp.setResponseStatus(respStatusType);
			respMessageType
					.setCode(NWCGAAConstants.SOAP_FAILURE_MESSAGE_CODE_3);
			respMessageType.setSeverity(NWCGAAConstants.SEVERITY_SUCCESS);
			respMessageType
					.setDescription(NWCGAAConstants.SOAP_FAILURE_RESPONSE_MESSAGE_3);
			lst.add(respMessageType);
			auresp.setUsername("");
			auresp.setIsAuthenticatedIndicator(NWCGAAConstants.SOAP_IS_AUTHENTICATION_INDICATOR_FALSE);
			auresp.setUserFullname("");
			auresp.setAuthenticationMessage(NWCGAAConstants.SOAP_AUTHENTICATION_MESSAGE_FAILURE);
		} else if (login != null && activateFlag.trim().equals("N")) {
			respStatusType.setReturnCode(NWCGAAConstants.SOAP_FAILURE_CODE);
			auresp.setResponseStatus(respStatusType);
			respMessageType
					.setCode(NWCGAAConstants.SOAP_FAILURE_MESSAGE_CODE_8);
			respMessageType.setSeverity(NWCGAAConstants.SEVERITY_SUCCESS);
			respMessageType
					.setDescription(NWCGAAConstants.SOAP_FAILURE_RESPONSE_MESSAGE_8);
			lst.add(respMessageType);
			auresp.setUsername(reqBody.getUsername());
			auresp.setIsAuthenticatedIndicator(NWCGAAConstants.SOAP_IS_AUTHENTICATION_INDICATOR_FALSE);
			auresp.setUserFullname("");
			auresp.setAuthenticationMessage(NWCGAAConstants.SOAP_AUTHENTICATION_MESSAGE_FAILURE);
		} else if (UserElem.getElementsByTagName("ContactPersonInfo").item(0) == null) {
			respStatusType.setReturnCode(NWCGAAConstants.SOAP_SUCCESS_CODE);
			auresp.setResponseStatus(respStatusType);
			respMessageType
					.setCode(NWCGAAConstants.SOAP_FAILURE_MESSAGE_CODE_3);
			respMessageType.setSeverity(NWCGAAConstants.SEVERITY_SUCCESS);
			respMessageType
					.setDescription(NWCGAAConstants.SOAP_FAILURE_RESPONSE_MESSAGE_3);
			lst.add(respMessageType);
			auresp.setUsername(reqBody.getUsername());
			auresp.setIsAuthenticatedIndicator(NWCGAAConstants.SOAP_IS_AUTHENTICATION_INDICATOR_TRUE);
			auresp.setUserFullname("");
			auresp.setAuthenticationMessage(NWCGAAConstants.SOAP_AUTHENTICATION_MESSAGE_FAILURE);
		} else if (message.getDocumentElement().getAttribute("Security")
				.equals("N")) {
			respStatusType.setReturnCode(NWCGAAConstants.SOAP_FAILURE_CODE);
			auresp.setResponseStatus(respStatusType);
			respMessageType
					.setCode(NWCGAAConstants.SOAP_FAILURE_MESSAGE_CODE_5);
			respMessageType.setSeverity(NWCGAAConstants.SEVERITY_SUCCESS);
			respMessageType
					.setDescription(NWCGAAConstants.SOAP_FAILURE_RESPONSE_MESSAGE_5);
			lst.add(respMessageType);
			auresp.setUsername("");
			auresp.setIsAuthenticatedIndicator(NWCGAAConstants.SOAP_IS_AUTHENTICATION_INDICATOR_FALSE);
			auresp.setUserFullname("");
			auresp.setAuthenticationMessage(NWCGAAConstants.SOAP_AUTHENTICATION_MESSAGE_FAILURE);
		} else if (message.getDocumentElement().getAttribute("Password")
				.equals("N")) {
			respStatusType.setReturnCode(NWCGAAConstants.SOAP_FAILURE_CODE);
			auresp.setResponseStatus(respStatusType);
			respMessageType
					.setCode(NWCGAAConstants.SOAP_FAILURE_MESSAGE_CODE_6);
			respMessageType.setSeverity(NWCGAAConstants.SEVERITY_SUCCESS);
			respMessageType
					.setDescription(NWCGAAConstants.SOAP_FAILURE_RESPONSE_MESSAGE_6);
			lst.add(respMessageType);
			auresp.setUsername("");
			auresp.setIsAuthenticatedIndicator(NWCGAAConstants.SOAP_IS_AUTHENTICATION_INDICATOR_FALSE);
			auresp.setUserFullname("");
			auresp.setAuthenticationMessage(NWCGAAConstants.SOAP_AUTHENTICATION_MESSAGE_FAILURE);
		} else if (message.getDocumentElement().getAttribute("Created")
				.equals("N")) {
			respStatusType.setReturnCode(NWCGAAConstants.SOAP_FAILURE_CODE);
			auresp.setResponseStatus(respStatusType);
			respMessageType
					.setCode(NWCGAAConstants.SOAP_FAILURE_MESSAGE_CODE_7);
			respMessageType.setSeverity(NWCGAAConstants.SEVERITY_SUCCESS);
			respMessageType
					.setDescription(NWCGAAConstants.SOAP_FAILURE_RESPONSE_MESSAGE_7);
			lst.add(respMessageType);
			auresp.setUsername("");
			auresp.setIsAuthenticatedIndicator(NWCGAAConstants.SOAP_IS_AUTHENTICATION_INDICATOR_FALSE);
			auresp.setUserFullname("");
			auresp.setAuthenticationMessage(NWCGAAConstants.SOAP_AUTHENTICATION_MESSAGE_FAILURE);
		} else if (message.getDocumentElement().getAttribute("Messagecontext")
				.equals("N")) {
			respStatusType.setReturnCode(NWCGAAConstants.SOAP_FAILURE_CODE);
			auresp.setResponseStatus(respStatusType);
			respMessageType
					.setCode(NWCGAAConstants.SOAP_FAILURE_MESSAGE_CODE_4);
			respMessageType.setSeverity(NWCGAAConstants.SEVERITY_SUCCESS);
			respMessageType
					.setDescription(NWCGAAConstants.SOAP_FAILURE_RESPONSE_MESSAGE_4);
			lst.add(respMessageType);
			auresp.setUsername("");
			auresp.setIsAuthenticatedIndicator(NWCGAAConstants.SOAP_IS_AUTHENTICATION_INDICATOR_FALSE);
			auresp.setUserFullname("");
			auresp.setAuthenticationMessage(NWCGAAConstants.SOAP_AUTHENTICATION_MESSAGE_FAILURE);
		} else {
			respStatusType.setReturnCode(NWCGAAConstants.SOAP_FAILURE_CODE);
			auresp.setResponseStatus(respStatusType);
			respMessageType
					.setCode(NWCGAAConstants.SOAP_FAILURE_MESSAGE_CODE_1);
			respMessageType.setSeverity(NWCGAAConstants.SEVERITY_SUCCESS);
			respMessageType
					.setDescription(NWCGAAConstants.SOAP_FAILURE_RESPONSE_MESSAGE_1);
			lst.add(respMessageType);
			auresp.setUsername(reqBody.getUsername());
			auresp.setIsAuthenticatedIndicator(NWCGAAConstants.SOAP_IS_AUTHENTICATION_INDICATOR_FALSE);
			auresp.setUserFullname(firstName + " " + lastname);
			auresp.setAuthenticationMessage(NWCGAAConstants.SOAP_AUTHENTICATION_MESSAGE_FAILURE);
		}
		if (flagIM.equals("IM")) {
			respStatusType.setReturnCode(NWCGAAConstants.SOAP_FAILURE_CODE);
			auresp.setResponseStatus(respStatusType);
			respMessageType
					.setCode(NWCGAAConstants.SOAP_FAILURE_MESSAGE_CODE_9);
			respMessageType.setSeverity(NWCGAAConstants.SEVERITY_SUCCESS);
			respMessageType
					.setDescription(NWCGAAConstants.SOAP_FAILURE_RESPONSE_MESSAGE_9);
			lst.add(respMessageType);
			auresp.setUsername(reqBody.getUsername());
			auresp.setIsAuthenticatedIndicator(NWCGAAConstants.SOAP_IS_AUTHENTICATION_INDICATOR_FALSE);
			auresp.setUserFullname(firstName + " " + lastname);
			auresp.setAuthenticationMessage(NWCGAAConstants.SOAP_AUTHENTICATION_MESSAGE_FAILURE);
		}
		return auresp;
	}
}