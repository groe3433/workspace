/* Copyright 2006, Sterling Commerce, Inc. All rights reserved. */
/*
 LIMITATION OF LIABILITY
 THIS SOFTWARE SAMPLE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED 
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 IN NO EVENT SHALL YANTRA CORPORATION BE LIABLE UNDER ANY THEORY OF 
 LIABILITY (INCLUDING, BUT NOT LIMITED TO, BREACH OF CONTRACT, BREACH 
 OF WARRANTY, TORT, NEGLIGENCE, STRICT LIABILITY, OR ANY OTHER THEORY 
 OF LIABILITY) FOR (i) DIRECT DAMAGES OR INDIRECT, SPECIAL, INCIDENTAL, 
 OR CONSEQUENTIAL DAMAGES SUCH AS, BUT NOT LIMITED TO, EXEMPLARY OR 
 PUNITIVE DAMAGES, OR ANY OTHER SIMILAR DAMAGES, WHETHER OR NOT 
 FORESEEABLE AND WHETHER OR NOT YANTRA OR ITS REPRESENTATIVES HAVE 
 BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES, OR (ii) ANY OTHER 
 CLAIM, DEMAND OR DAMAGES WHATSOEVER RESULTING FROM OR ARISING OUT OF
 OR IN CONNECTION THE DELIVERY OR USE OF THIS INFORMATION.
 */

package com.nwcg.icbs.yantra.api.ldap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.yantra.ycp.japi.util.YCPSSOManager;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.core.YFSSystem;

/**
 * When "UserId" is not present in URL call from NAP this class is never called.
 * Its default yantra behavior.
 * 
 * @Task - 1. need to add logging 2. make all properties read from NWCG properties
 * @author Jay
 *
 */
public class NWCGSSOManager implements YCPSSOManager {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGSSOManager.class);

	/**
	 * NWCGLdapAuthenticator.getUserData
	 */
	public String getUserData(HttpServletRequest req, HttpServletResponse res) throws Exception {
		logger.verbose("@@@@@ Entering NWCGLdapAuthenticator.getUserData");
		String userIdFromNAP = "";
		String userId = "";
		String password = "";
		String token = "";
		String appInst = "";
		Enumeration reqParams = req.getParameterNames();
		logger.verbose("@@@@@ Printing Parameters --> ");
		while (reqParams.hasMoreElements()) {
			String headers = (String) reqParams.nextElement();
			if (headers != null && headers.equalsIgnoreCase("UserId")) {
				userId = req.getParameter(headers);
			} else if (headers != null && headers.equalsIgnoreCase("Password")) {
				password = req.getParameter(headers);
			} else if (headers != null && headers.equalsIgnoreCase("token")) {
				token = req.getParameter(headers);
			} else if (headers != null && headers.equalsIgnoreCase("?token")) {
				// because of NAP limitation, then are sending '?token' instead of 'token'.
				token = req.getParameter(headers);
			} else if (headers != null && headers.equalsIgnoreCase("appInst")) {
				appInst = req.getParameter(headers);
			} else {
				// Do Nothing...
			}
		}
		if ((token.trim().length() > 0) && (appInst.trim().length() > 0)) {
			userIdFromNAP = validateTokenWithNAP(token, appInst);
		} else if ((userId.trim().length() > 0) && (password.trim().length() > 0)) {
			logger.verbose("@@@@@ In Else ...");
			try {
				Hashtable lMap = new Hashtable();
				NWCGLdapAuthenticator ldapAuth = new NWCGLdapAuthenticator();
				lMap = (Hashtable) ldapAuth.authenticate(userId, password);
				logger.verbose("@@@@@ Exiting NWCGLdapAuthenticator.getUserData (try)");
				return userId;
			} catch (Exception excp) {
				String errMsg = excp.getMessage();
				req.setAttribute("ErrorMsg", errMsg);
				req.setAttribute("ErrorMsgDetail", errMsg);
				req.setAttribute("UserId", userId);
				logger.error("!!!!! Before Throwing Exception ...");
				throw new YFCException("Login failed - LDAP");
			}
		}
		logger.verbose("@@@@@ Exiting NWCGLdapAuthenticator.getUserData");
		return userIdFromNAP;
	}

	/**
	 * 
	 * @param token
	 * @param appInst
	 * @return
	 * @throws Exception
	 */
	public String validateTokenWithNAP(String token, String appInst) throws Exception {
		logger.verbose("@@@@@ Entering NWCGLdapAuthenticator.validateTokenWithNAP");
		String userIdFromNAP = "";
		// Create MessageFactory and SoapHeader and SoapBody
		MessageFactory mf = MessageFactory.newInstance();
		SOAPMessage soapMessageRequest = mf.createMessage();
		soapMessageRequest.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
		soapMessageRequest.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "UTF-8");
		// add Namespace Declaration
		SOAPPart part = soapMessageRequest.getSOAPPart();
		SOAPEnvelope envelope = part.getEnvelope();
		String ssoPrefix = NWCGAAConstants.NAP_SSO_PREFIX;
		String ssoURI = NWCGAAConstants.NAP_SSO_URI;
		String securityPrefix = NWCGAAConstants.NAP_SECURITY_PREFIX;
		String securityURI = NWCGAAConstants.NAP_SECURITY_URI;
		String napWSUserName = YFSSystem.getProperty("nwcg.nap.webservices.username");
		String napWSPassword = YFSSystem.getProperty("nwcg.nap.webservices.password");
		String serviceTicket = YFSSystem.getProperty("nwcg.nap.webservices.serviceticket");
		String ssoURL = YFSSystem.getProperty("nwcg.nap.webservices.ssoURL");
		envelope.addNamespaceDeclaration(ssoPrefix, ssoURI);
		envelope.addNamespaceDeclaration(securityPrefix, securityURI);
		// create Header first
		SOAPHeader header = soapMessageRequest.getSOAPHeader();
		SOAPElement securityElem = header.addChildElement("Security", securityPrefix);
		SOAPElement userNametokenElem = securityElem.addChildElement("UsernameToken", securityPrefix);
		SOAPElement usernameElem = userNametokenElem.addChildElement("Username", securityPrefix);
		SOAPElement passwordElem = userNametokenElem.addChildElement("Password", securityPrefix);
		usernameElem.addTextNode(napWSUserName);
		passwordElem.addTextNode(napWSPassword);
		// create Body
		SOAPBody soapbodyRequest = soapMessageRequest.getSOAPBody();
		SOAPElement ssoAuthReqElem = soapbodyRequest.addChildElement("ssoAuthenticationRequest", ssoPrefix);
		SOAPElement ssoSrvcTcktElem = ssoAuthReqElem.addChildElement("ServiceTicket", ssoPrefix);
		SOAPElement ssoSrvcURLElem = ssoAuthReqElem.addChildElement("ServiceURL", ssoPrefix);
		SOAPElement ssoApplInstElem = ssoAuthReqElem.addChildElement("ApplicationInstance", ssoPrefix);
		ssoSrvcTcktElem.addTextNode(decodeToken(token));
		ssoSrvcURLElem.addTextNode(serviceTicket);
		ssoApplInstElem.addTextNode(appInst);
		// Call Soap Request
		URL endPt = new URL(ssoURL);
		SOAPConnectionFactory myFct = SOAPConnectionFactory.newInstance();
		SOAPConnection myCon = myFct.createConnection();
		logger.verbose("@@@@@ Before Calling SOAP Request to NAP :: " + endPt.toString());
		SOAPMessage reply = null;
		try {
			reply = myCon.call(soapMessageRequest, endPt);
		} catch (SOAPException soapExp) {
			logger.error("!!!!! Caught SOAPException :: " + soapExp);
			throw new Exception("Login failed - NAP is not availble");
		}
		myCon.close();
		userIdFromNAP = processNAPResponse(reply);
		logger.verbose("@@@@@ Exiting NWCGLdapAuthenticator.validateTokenWithNAP");
		return userIdFromNAP;
	}

	/**
	 * 
	 * @param reply
	 * @return
	 * @throws Exception
	 */
	public String processNAPResponse(SOAPMessage reply) throws Exception {
		logger.verbose("@@@@@ Entering NWCGLdapAuthenticator.processNAPResponse");
		String userIdFromNAP = "";
		SOAPBody soapbodyResponse = reply.getSOAPBody();
		// see if we receive any fault code
		if (soapbodyResponse.hasFault()) {
			SOAPFault soapFault = soapbodyResponse.getFault();
			String faultStr = soapFault.getFaultString();
			logger.verbose("@@@@@ SOAP Fault String: " + faultStr);
			throw new Exception("Login failed - " + faultStr);
		} else {
			// good response
			NodeList list = soapbodyResponse.getChildNodes();
			if (list != null) {
				for (int index = 0; index < list.getLength(); index++) {
					Node ssoAuthResp = list.item(index);
					NodeList ssoAuthRespNodeList = ssoAuthResp.getChildNodes();
					if (ssoAuthRespNodeList != null) {
						for (int index2 = 0; index2 < ssoAuthRespNodeList.getLength(); index2++) {
							Node ssoAuthRespChild = ssoAuthRespNodeList.item(index2);
							if (ssoAuthRespChild instanceof SOAPElement) {
								SOAPElement childEle = (SOAPElement) ssoAuthRespChild;
								String ssoAuthRespChildName = childEle.getNodeName();
								if (ssoAuthRespChildName.substring(ssoAuthRespChildName.indexOf(":") + 1).equalsIgnoreCase(NWCGAAConstants.NAP_SOAP_USERNAME_TAG)) {
									userIdFromNAP = childEle.getValue();
									break;
								} else {
								}
							}
						}
						break;
					}
				}
			}
			logger.verbose("@@@@@ Exiting NWCGLdapAuthenticator.processNAPResponse");
			return userIdFromNAP;
		}
	}

	/**
	 * This method decode the token supplied by NAP in URL.
	 * 
	 * @param napToken
	 * @return decodedToken
	 */
	public String decodeToken(String napToken) throws Exception {
		logger.verbose("@@@@@ Entering NWCGLdapAuthenticator.decodeToken");
		byte[] codedToken = napToken.getBytes();
		ByteArrayInputStream bais = new ByteArrayInputStream(codedToken);
		InputStream b64is = MimeUtility.decode(bais, NWCGAAConstants.NAP_TOKEN_ENCODING);
		byte[] tmp = new byte[codedToken.length];
		int n = b64is.read(tmp);
		byte[] res = new byte[n];
		System.arraycopy(tmp, 0, res, 0, n);
		logger.verbose("@@@@@ decodedBytes " + new String(res));
		logger.verbose("@@@@@ Exiting NWCGLdapAuthenticator.decodeToken");
		return (new String(res).trim());
	}

	/**
	 * 
	 * @param userName
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public String validateUserNamePwdWithNAP(String userName, String password) throws Exception {
		logger.verbose("@@@@@ Entering NWCGLdapAuthenticator.validateUserNamePwdWithNAP");
		String securityPrefix = NWCGAAConstants.NAP_SECURITY_PREFIX;
		String napWSUserName = YFSSystem.getProperty("nwcg.nap.webservices.username");
		String napWSPassword = YFSSystem.getProperty("nwcg.nap.webservices.password");
		String authPrefix = "aut";
		String authURL = "https://nap-ft.nwcg.gov/rossTR/authenticationService";
		String appInst = "ICBS-TEST";
		String userIdFromNAP = "";
		MessageFactory mf = MessageFactory.newInstance();
		SOAPMessage soapMessageRequest = mf.createMessage();
		soapMessageRequest.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
		soapMessageRequest.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "UTF-8");
		// add Namespace Declaration
		SOAPPart part = soapMessageRequest.getSOAPPart();
		SOAPEnvelope envelope = part.getEnvelope();
		envelope.addNamespaceDeclaration(authPrefix, "http://www.nwcg.org/webservices/security/authentication");
		String uri = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
		envelope.addNamespaceDeclaration(securityPrefix, uri);
		// create Header first
		SOAPHeader header = soapMessageRequest.getSOAPHeader();
		SOAPElement securityElem = header.addChildElement("Security", securityPrefix);
		SOAPElement userNametokenElem = securityElem.addChildElement("UsernameToken", securityPrefix);
		SOAPElement usernameElem = userNametokenElem.addChildElement("Username", securityPrefix);
		SOAPElement passwordElem = userNametokenElem.addChildElement("Password", securityPrefix);
		usernameElem.addTextNode(napWSUserName);
		passwordElem.addTextNode(napWSPassword);
		// create Body
		SOAPBody soapbodyRequest = soapMessageRequest.getSOAPBody();
		SOAPElement autReqElem = soapbodyRequest.addChildElement("authenticationRequest", authPrefix);
		SOAPElement autUserNameElem = autReqElem.addChildElement("UserName", authPrefix);
		SOAPElement autPasswordElem = autReqElem.addChildElement("Password", authPrefix);
		SOAPElement autApplInstElem = autReqElem.addChildElement("ApplicationInstance", authPrefix);
		autUserNameElem.addTextNode(userName);
		autPasswordElem.addTextNode(password);
		autApplInstElem.addTextNode(appInst);
		// Call Soap Request
		URL endPt = new URL(authURL);
		SOAPConnectionFactory myFct = SOAPConnectionFactory.newInstance();
		SOAPConnection myCon = myFct.createConnection();
		logger.verbose("@@@@@ Before Calling SOAP Request to NAP:" + endPt.toString());
		SOAPMessage reply = null;
		try {
			reply = myCon.call(soapMessageRequest, endPt);
		} catch (SOAPException soapExp) {
			logger.error("!!!!! Caught SOAPException :: " + soapExp);
			throw new Exception("Login failed - NAP is not availble");
		}
		// SOAPMessage reply = myCon.call(soapMessageRequest, endPt);
		logger.verbose("@@@@@ After Calling SOAP Request to NAP:" );
		myCon.close();
		userIdFromNAP = processNAPResponse(reply);
		logger.verbose("@@@@@ Exiting NWCGLdapAuthenticator.validateUserNamePwdWithNAP");
		return userIdFromNAP;
	}

	/**
	 * For Testing Only :: Put in your own settings here. 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		logger.verbose("@@@@@ Entering NWCGLdapAuthenticator.main");
		String soapText = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soapenv:Header/><soapenv:Body><ssoAuth:ssoAuthenticationResponse xmlns:ssoAuth=\"http://www.nwcg.org/webservices/security/ssoAuthentication\"><ssoAuth:UserName>agroenewold</ssoAuth:UserName><ssoAuth:FirstName>Andrew</ssoAuth:FirstName><ssoAuth:MiddleName>Alan</ssoAuth:MiddleName><ssoAuth:LastName>Groenewold</ssoAuth:LastName><ssoAuth:LDAPIdentifier>384401</ssoAuth:LDAPIdentifier><ssoAuth:Status>Active</ssoAuth:Status><ssoAuth:ROBAccepted>true</ssoAuth:ROBAccepted><ssoAuth:ROBExpirationDays>0</ssoAuth:ROBExpirationDays><ssoAuth:IsPrivileged>false</ssoAuth:IsPrivileged></ssoAuth:ssoAuthenticationResponse></soapenv:Body></soapenv:Envelope>";
		// Create SoapMessage
		MessageFactory msgFactory = MessageFactory.newInstance();
		SOAPMessage message = msgFactory.createMessage();
		SOAPPart soapPart = message.getSOAPPart();
		// Load the SOAP text into a stream source
		byte[] buffer = soapText.getBytes();
		ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
		StreamSource source = new StreamSource(stream);
		// Set contents of message
		soapPart.setContent(source);
		String userId = new NWCGSSOManager().processNAPResponse(message);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStream b64os = MimeUtility.encode(baos, "base64");
		b64os.write("realhowto".getBytes());
		b64os.close();
		logger.verbose("@@@@@ Exiting NWCGLdapAuthenticator.main");
	}

	/**
	 * 
	 * @param soapMsg
	 * @return
	 * @throws TransformerException
	 * @throws SOAPException
	 */
	public static String extractStringFromSOAPMessage(SOAPMessage soapMsg) throws TransformerException, SOAPException {
		logger.verbose("@@@@@ Entering NWCGLdapAuthenticator.extractStringFromSOAPMessage");
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(soapMsg.getSOAPPart().getContent(), result);
		logger.verbose("@@@@@ Exiting NWCGLdapAuthenticator.extractStringFromSOAPMessage");
		return writer.toString();
	}
}