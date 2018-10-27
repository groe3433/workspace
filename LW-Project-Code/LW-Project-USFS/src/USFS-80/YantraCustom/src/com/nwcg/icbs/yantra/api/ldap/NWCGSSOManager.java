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
import com.yantra.ycp.japi.util.YCPSSOManager;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.core.YFSSystem;

/**
 * When "UserId" is not present in URL call from NAP this class is never called.  Its default yantra behavior.
 * @Task - 
 * 1. need to add logging and remove System.out; 
 * 2. make all properties read from NWCG properties
 * @author Jay
 *
 */
public class NWCGSSOManager implements YCPSSOManager {

	 /**
	 * For logging
	 */
	// private static Logger logger = Logger.getLogger(NWCGSSOManager.class.getName());
	
	private static YFCLogCategory cat = YFCLogCategory.instance(NWCGSSOManager.class);
	 
	/* (non-Javadoc)
	 * @see com.yantra.ycp.japi.util.YCPSSOManager#getUserData(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public String getUserData(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// logger.verbose("NWCGLdapAuthenticator.getUserData");
		System.out.println("NWCGLdapAuthenticator.getUserData v1.3");
		cat.debug("NWCGLdapAuthenticator.getUserData v1.3");  
		
		String userIdFromNAP = "";
		String userId = "";
		String password = "";
		String token = "";
		String appInst = "";
		Enumeration reqParams = req.getParameterNames();
		// logger.verbose("Printing Parameters:");
		System.out.println("Printing Parameters:");
		while (reqParams.hasMoreElements()) {
			String headers = (String) reqParams.nextElement();
			if (headers != null && headers.equalsIgnoreCase("UserId")) {
				userId = req.getParameter(headers);
				// logger.verbose(headers + "=" + userId);
			} else if (headers != null && headers.equalsIgnoreCase("Password")) {
				password = req.getParameter(headers);
				// logger.verbose(headers + "=" + password);
			} else if (headers != null && headers.equalsIgnoreCase("token")) {
				token = req.getParameter(headers);
				// logger.verbose(headers + "=" + token);
			} else if (headers != null && headers.equalsIgnoreCase("?token")) {
				// because of NAP limitation, then are sending '?token' instead of 'token'.
				token = req.getParameter(headers);
				// logger.verbose(headers + "=" + token);
			} else if (headers != null && headers.equalsIgnoreCase("appInst")) {
				appInst = req.getParameter(headers);
				// logger.verbose(headers + "=" + appInst);
			} else {
				// logger.verbose(headers + "=" + req.getParameter(headers));
			}
		}

		if ((token.trim().length() > 0) && (appInst.trim().length() > 0)) {
			// logger.verbose("UserId=" + userId + "; Password=" + password + "; token=" + token + "; appInst=" + appInst);
			System.out.println("UserId=" + userId + "; Password=" + password + "; token=" + token + "; appInst=" + appInst);
			userIdFromNAP = validateTokenWithNAP(token, appInst);
		} else if ((userId.trim().length() > 0) && (password.trim().length() > 0)) {
			/* This code can be used to test SOAP based user authentication instead of LDAP
			System.out.println("Calling LDAP class with UserId=" + userId + " and Password = " + password);
			userIdFromNAP = validateUserNamePwdWithNAP(userId, password);
			*/
			try {
				Hashtable lMap = new Hashtable();
				NWCGLdapAuthenticator ldapAuth = new NWCGLdapAuthenticator();
				lMap = (Hashtable) ldapAuth.authenticate(userId, password);
				return userId;
			}catch (Exception excp) {
				String errMsg = excp.getMessage();
            	System.out.println("LDAP login Error Mesg="+ errMsg);
            	req.setAttribute("ErrorMsg", errMsg);
            	req.setAttribute("ErrorMsgDetail", errMsg);req.setAttribute("UserId", userId);
            	throw new YFCException("Login failed - LDAP");
			}
			
		}
		
		// logger.verbose("Before returning user id = " + userIdFromNAP);
		System.out.println("Before returning user id = " + userIdFromNAP);
		/*
		if(userIdFromNAP == null || userIdFromNAP.length() < 1)	{
			throw new Exception("Login failed");
		} */
		return userIdFromNAP;
	}

	public String validateTokenWithNAP(String token, String appInst) throws Exception {
		String userIdFromNAP = "";
		
		// Create MessageFactory and SoapHeader and SoapBody
		MessageFactory mf = MessageFactory.newInstance();
		SOAPMessage soapMessageRequest = mf.createMessage();
		soapMessageRequest.setProperty(SOAPMessage.WRITE_XML_DECLARATION , "true");
		soapMessageRequest.setProperty(SOAPMessage.CHARACTER_SET_ENCODING , "UTF-8");
		
		// add Namespace Declaration
		SOAPPart part = soapMessageRequest.getSOAPPart();
		SOAPEnvelope envelope = part.getEnvelope();
        // String ssoPrefix = "ssoAuth";
        // String ssoURI = "http://www.nwcg.org/webservices/security/ssoAuthentication";
		// String securityPrefix = "wsse";
        // String securityURI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
		String ssoPrefix = NWCGAAConstants.NAP_SSO_PREFIX;
		String ssoURI = NWCGAAConstants.NAP_SSO_URI;
		String securityPrefix = NWCGAAConstants.NAP_SECURITY_PREFIX;
        String securityURI = NWCGAAConstants.NAP_SECURITY_URI;
        String napWSUserName = YFSSystem.getProperty("nwcg.nap.webservices.username");
        String napWSPassword = YFSSystem.getProperty("nwcg.nap.webservices.password");
        String serviceTicket = YFSSystem.getProperty("nwcg.nap.webservices.serviceticket");
        String ssoURL = YFSSystem.getProperty("nwcg.nap.webservices.ssoURL");
        System.out.println("Properties: ssoPrefix=" + ssoPrefix + ";ssoURI=" + ssoURI + ";securityPrefix=" + securityPrefix + ";securityURI=" + securityURI);
        System.out.println("Properties: napWSUserName=" + napWSUserName + "; napWSPassword=" + napWSPassword + "; ssoURL=" + ssoURL);
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
		// usernameElem.addTextNode("wfamweb");
		// passwordElem.addTextNode("SH@r3dAdm1n12");

		// create Body
		// String serviceTicket = "https://fam.nwcg.gov:50443";
		SOAPBody soapbodyRequest = soapMessageRequest.getSOAPBody();		
		SOAPElement ssoAuthReqElem = soapbodyRequest.addChildElement("ssoAuthenticationRequest", ssoPrefix);
		SOAPElement ssoSrvcTcktElem = ssoAuthReqElem.addChildElement("ServiceTicket", ssoPrefix);
		SOAPElement ssoSrvcURLElem = ssoAuthReqElem.addChildElement("ServiceURL", ssoPrefix);
		SOAPElement ssoApplInstElem = ssoAuthReqElem.addChildElement("ApplicationInstance", ssoPrefix);
		ssoSrvcTcktElem.addTextNode(decodeToken(token));
		ssoSrvcURLElem.addTextNode(serviceTicket);
		ssoApplInstElem.addTextNode(appInst);
		
        // print the SOAP XML to system.out
		// logger.verbose("SOAPMessage:");
		System.out.println("NWCGLdapAuthenticator.validateTokenWithNAP - SOAPMessage:");
		soapMessageRequest.writeTo(System.out);
		
		 /* TrustStore Parameters 
        String KeyStoreFile = "/app/IBM/certificate/nap-ft.jks";
        String KeyStorePasswd = "napft7";
        System.setProperty("javax.net.ssl.keyStoreType","jks");
        System.setProperty("javax.net.ssl.keyStore",KeyStoreFile);
        System.setProperty("javax.net.ssl.keyStorePassword",KeyStorePasswd);
        System.setProperty("javax.net.ssl.trustStoreType","jks");
        System.setProperty("javax.net.ssl.trustStore",KeyStoreFile);
        System.setProperty("javax.net.ssl.trustStorePassword",KeyStorePasswd);
        // logger.verbose("\n Set System property for SSL:" + System.getProperty("javax.net.ssl.keyStore"));
		*/
		
		//Call Soap Request
		// String ssoURL = "https://nap-ft.nwcg.gov/rossTR/ssoAuthenticationService";
		URL endPt = new URL(ssoURL);
		SOAPConnectionFactory myFct = SOAPConnectionFactory.newInstance();
		SOAPConnection myCon = myFct.createConnection();
		// logger.verbose("\nBefore Calling SOAP Request to NAP:" + endPt.toString());
		System.out.println("\nBefore Calling SOAP Request to NAP:" + endPt.toString());
		SOAPMessage reply = null;
		try {
			reply = myCon.call(soapMessageRequest, endPt);
		} catch (SOAPException soapExp) {
			System.out.println("Exception in NAP Token SOAP call:" + soapExp.getMessage());
			soapExp.printStackTrace();
			throw new Exception("Login failed - NAP is not availble");
		}		
		// logger.verbose("\nAfter Calling SOAP Request to NAP:");
		System.out.println("\nAfter Calling SOAP Request to NAP:");
		reply.writeTo(System.out);
		myCon.close();
		userIdFromNAP = processNAPResponse(reply);
		return userIdFromNAP;
	}
	
	public String processNAPResponse(SOAPMessage reply) throws Exception {
		String userIdFromNAP = "";
		SOAPBody soapbodyResponse = reply.getSOAPBody();
		
		// see if we receive any fault code
		if (soapbodyResponse.hasFault()) {
			SOAPFault soapFault = soapbodyResponse.getFault();
			String faultStr = soapFault.getFaultString();
			// logger.verbose("SOAP Fault String: " + faultStr);
			System.out.println("SOAP Fault String: " + faultStr);
			throw new Exception("Login failed - " + faultStr);
		} else {
			// good response
			NodeList list = soapbodyResponse.getChildNodes();
			System.out.println("Node List size:" + list.getLength());
			if(list != null) {
				for(int index = 0; index < list.getLength() ; index ++) {
					Node ssoAuthResp = list.item(index);
					NodeList ssoAuthRespNodeList = ssoAuthResp.getChildNodes();
					System.out.println("ssoAuthResp NodeName=" + ssoAuthResp.getNodeName() + "Node List size=" + ssoAuthRespNodeList.getLength());
					if(ssoAuthRespNodeList != null) {
						for(int index2 = 0; index2 < ssoAuthRespNodeList.getLength(); index2++) {
							Node ssoAuthRespChild = ssoAuthRespNodeList.item(index2);
							if (ssoAuthRespChild instanceof SOAPElement)	{
								SOAPElement childEle = (SOAPElement)ssoAuthRespChild;
								String ssoAuthRespChildName = childEle.getNodeName();
								// System.out.println("Node Name:" + element.getNodeName() + " Node Value:" + element.getValue());
								if (ssoAuthRespChildName.substring(ssoAuthRespChildName.indexOf(":")+1).equalsIgnoreCase(NWCGAAConstants.NAP_SOAP_USERNAME_TAG)) {
									userIdFromNAP = childEle.getValue();
									System.out.println("AuthResp Node Name:" + childEle.getNodeName() + "userIdFromNAP:" + userIdFromNAP);
									break;
								} else {
									System.out.println("AuthResp Node Name:" + childEle.getNodeName() + "userIdFromNAP:" + childEle.getValue());
								}
							}
						}
						break;
					}
				}
			}
			return userIdFromNAP;
		}
	}
	
	/**
	 * This method decode the token supplied by NAP in URL.
	 * @param 	napToken
	 * @return	decodedToken
	 */
	public String decodeToken(String napToken) throws Exception {
		
		byte[] codedToken = napToken.getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(codedToken);
        InputStream b64is = MimeUtility.decode(bais, NWCGAAConstants.NAP_TOKEN_ENCODING);
        byte[] tmp = new byte[codedToken.length];
        int n = b64is.read(tmp);
        byte[] res = new byte[n];
        System.arraycopy(tmp, 0, res, 0, n);
        System.out.println("decodedBytes " + new String(res));
        // logger.verbose("decodedBytes " + new String(res));
        return (new String(res).trim());
	}
	
	public String validateUserNamePwdWithNAP(String userName, String password) throws Exception { 
		
		String securityPrefix = NWCGAAConstants.NAP_SECURITY_PREFIX;
        String napWSUserName = YFSSystem.getProperty("nwcg.nap.webservices.username");
        String napWSPassword = YFSSystem.getProperty("nwcg.nap.webservices.password");
        String authPrefix = "aut";
        String authURL = "https://nap-ft.nwcg.gov/rossTR/authenticationService";
        String appInst = "ICBS-TEST";
        String userIdFromNAP = "";
        
		MessageFactory mf = MessageFactory.newInstance();
		SOAPMessage soapMessageRequest = mf.createMessage();
		soapMessageRequest.setProperty(SOAPMessage.WRITE_XML_DECLARATION , "true");
		soapMessageRequest.setProperty(SOAPMessage.CHARACTER_SET_ENCODING , "UTF-8");
		
		// add Namespace Declaration
		SOAPPart part = soapMessageRequest.getSOAPPart();
		SOAPEnvelope envelope = part.getEnvelope();
		envelope.addNamespaceDeclaration(authPrefix, "http://www.nwcg.org/webservices/security/authentication");
		// String prefix = "wsse";
		
        String uri = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
        envelope.addNamespaceDeclaration(securityPrefix, uri);
		
		// create Header first
		SOAPHeader header = soapMessageRequest.getSOAPHeader();
		// String prefix = "wsse";
        // String uri = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
		SOAPElement securityElem = header.addChildElement("Security", securityPrefix);
		SOAPElement userNametokenElem = securityElem.addChildElement("UsernameToken", securityPrefix);
		SOAPElement usernameElem = userNametokenElem.addChildElement("Username", securityPrefix);
		SOAPElement passwordElem = userNametokenElem.addChildElement("Password", securityPrefix);
		// usernameElem.addTextNode("wfamweb");
		// passwordElem.addTextNode("SH@r3dAdm1n12");
		usernameElem.addTextNode(napWSUserName);
		passwordElem.addTextNode(napWSPassword);

		// create Body
		// SOAPFactory soapFactory = SOAPFactory.newInstance();
		// Name bodyName = soapFactory.createName("authenticationRequest", "aut", "http://www.nwcg.org/webservices/security/authentication");
		SOAPBody soapbodyRequest = soapMessageRequest.getSOAPBody();
		// SOAPBodyElement bodyElement = soapbodyRequest.addBodyElement(bodyName);
		SOAPElement autReqElem = soapbodyRequest.addChildElement("authenticationRequest", authPrefix);
		SOAPElement autUserNameElem = autReqElem.addChildElement("UserName", authPrefix);
		SOAPElement autPasswordElem = autReqElem.addChildElement("Password", authPrefix);
		SOAPElement autApplInstElem = autReqElem.addChildElement("ApplicationInstance", authPrefix);
		autUserNameElem.addTextNode(userName);
		autPasswordElem.addTextNode(password);
		autApplInstElem.addTextNode(appInst);
		
        // soapbodyRequest.addDocument(getRequestDocument());
		// logger.verbose("SOAPMessage:");
		soapMessageRequest.writeTo(System.out);
		
		//Call Soap Request
		URL endPt = new URL(authURL);
		SOAPConnectionFactory myFct = SOAPConnectionFactory.newInstance();
		SOAPConnection myCon = myFct.createConnection();
		// logger.verbose("\nBefore Calling SOAP Request to NAP:" + endPt.toString());
		SOAPMessage reply = null;
		try {
			reply = myCon.call(soapMessageRequest, endPt);
		} catch (SOAPException soapExp) {
			System.out.println("Exception in NAP Token SOAP call:" + soapExp.getMessage());
			soapExp.printStackTrace();
			throw new Exception("Login failed - NAP is not availble");
		}
		// SOAPMessage reply = myCon.call(soapMessageRequest, endPt);
		// logger.verbose("\nAfter Calling SOAP Request to NAP:" );
		reply.writeTo(System.out);
		myCon.close();
		userIdFromNAP = processNAPResponse(reply);
		return userIdFromNAP;
	}
	
	/*
	public Document getRequestDocument() throws Exception {
		Document reqXML2 = XMLUtil.createDocument("authenticationRequest");
		// logger.verbose("reqXML2:" + XMLUtil.extractStringFromDocument(reqXML2));
		String reqXMLString = "<authenticationRequest><UserName>agroenewold</UserName><Password>P@assword2013</Password><ApplicationInstance>ICBS-TEST</ApplicationInstance></authenticationRequest>";
		Document reqXML = XMLUtil.getDocument(reqXMLString);
		return reqXML;
	} */
	
    public static void main(String[] args) throws Exception {
    	// new NWCGSSOManager().validateUserNamePwdWithNAP("vrao", "Naptesting#13");
    	
    	String soapText = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soapenv:Header/><soapenv:Body><ssoAuth:ssoAuthenticationResponse xmlns:ssoAuth=\"http://www.nwcg.org/webservices/security/ssoAuthentication\"><ssoAuth:UserName>agroenewold</ssoAuth:UserName><ssoAuth:FirstName>Andrew</ssoAuth:FirstName><ssoAuth:MiddleName>Alan</ssoAuth:MiddleName><ssoAuth:LastName>Groenewold</ssoAuth:LastName><ssoAuth:LDAPIdentifier>384401</ssoAuth:LDAPIdentifier><ssoAuth:Status>Active</ssoAuth:Status><ssoAuth:ROBAccepted>true</ssoAuth:ROBAccepted><ssoAuth:ROBExpirationDays>0</ssoAuth:ROBExpirationDays><ssoAuth:IsPrivileged>false</ssoAuth:IsPrivileged></ssoAuth:ssoAuthenticationResponse></soapenv:Body></soapenv:Envelope>";
    	// Create SoapMessage  
        MessageFactory msgFactory     = MessageFactory.newInstance();  
        SOAPMessage message           = msgFactory.createMessage();  
        SOAPPart soapPart             = message.getSOAPPart();  

        // Load the SOAP text into a stream source  
        byte[] buffer                 = soapText.getBytes();  
        ByteArrayInputStream stream   = new ByteArrayInputStream(buffer);  
        StreamSource source           = new StreamSource(stream);
        
        // Set contents of message   
        soapPart.setContent(source);
        message.writeTo(System.out); 
        System.out.println("\n");
        String userId = new NWCGSSOManager().processNAPResponse(message);
        System.out.println("userId in SOAPResponse - " + userId);
        
    	// byte[] decoded = javax.xml.bind.DatatypeConverter.parseBase64Binary(\"U1QtMTgtQ0ZxWjR1UklsZzRsY2ZNVFpmMWctY2Fz\");
		// System.out.println("Decoded value:" + decoded);
		// String decodedToken = new String(decoded);
		// new NWCGSSOManager().validateTokenWithNAP("ST-87-BlbbUyvpbfyZN1LjEdYd-cas", "ICBS-TEST");
    	// new NWCGSSOManager().decodeToken("U1QtMTAtWFFldEx4UmEzVU9rYWFCR29RMWwtY2Fz");
    	// byte[] decoded = javax.xml.bind.DatatypeConverter.parseBase64Binary("U1QtMTAtWFFldEx4UmEzVU9rYWFCR29RMWwtY2Fz");
        // System.out.println("decodedBytes " + new String(decoded));
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream b64os = MimeUtility.encode(baos, "base64");
        b64os.write("realhowto".getBytes());
        b64os.close();
        System.out.println("encodedBytes " + new String(baos.toByteArray()));
        
        // new NWCGSSOManager().decodeToken("U1QtMTAtWFFldEx4UmEzVU9rYWFCR29RMWwtY2Fz");
    }

    public static String extractStringFromSOAPMessage (SOAPMessage soapMsg) throws TransformerException, SOAPException {
	   
       StringWriter writer = new StringWriter();
       StreamResult result = new StreamResult(writer);
       TransformerFactory tf = TransformerFactory.newInstance();
       Transformer transformer = tf.newTransformer();
       transformer.transform(soapMsg.getSOAPPart().getContent(), result);
       return writer.toString(); 
	}
}
