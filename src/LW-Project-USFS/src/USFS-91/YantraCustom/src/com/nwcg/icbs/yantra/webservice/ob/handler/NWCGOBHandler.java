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

package com.nwcg.icbs.yantra.webservice.ob.handler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.soap.NWCGAAUtil;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServicesConstant;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

/**
 * This simple SOAPHandler will output the contents of incoming and outgoing
 * messages.
 * 
 * @since ICBS-ROSS Interface BR2 Increment 3
 */
public class NWCGOBHandler implements SOAPHandler<SOAPMessageContext> {

	private static YFCLogCategory logger = NWCGROSSLogger
			.instance(NWCGOBHandler.class);
	
	final String HANDLER_NAME = "NWCGOBHandler";

	private static SimpleDateFormat sdf = new SimpleDateFormat(
			NWCGAAConstants.STRING_GREGORIAN_CAL_FORMAT);

	private static Calendar calendar = new GregorianCalendar();

	private String serializeddate = null;

	private static final String className = NWCGOBHandler.class.getName();

	public Set<QName> getHeaders() {
		return null;
	}

	public boolean handleMessage(SOAPMessageContext smc) {
		logger.verbose("@@@@@ Entering NWCGOBHandler::handleMessage");
		Boolean direction = (Boolean) smc.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (direction.booleanValue() == true) {
			logger.verbose("@@@@@ adding soap headers now :: ");
			NWCGAAUtil.setSSL();
			setSoapHeader(smc);
		}
		SOAPMessage sm = smc.getMessage();
		logger.verbose("@@@@@ Exiting NWCGOBHandler::handleMessage");
		return true;
	}

	public boolean handleFault(SOAPMessageContext smc) {
		logger.verbose("@@@@@ Entering NWCGOBHandler::handleFault");
		SOAPMessage sm = smc.getMessage();
		logger.verbose("@@@@@ Exiting NWCGOBHandler::handleFault");
		return true;
	}

	// nothing to clean up
	public void close(MessageContext messageContext) {
		logger.verbose("@@@@@ In NWCGOBHandler::close (this method doesn't do anything)");
	}

	private void setSoapHeader(SOAPMessageContext smc) {
		logger.verbose("@@@@@ Entering NWCGOBHandler::setSoapHeader");
		try {
			SOAPMessage sm = smc.getMessage();
			SOAPHeader header = sm.getSOAPHeader();
			if (header == null) {
				header = sm.getSOAPPart().getEnvelope().addHeader();
			}
			// Getting the date/time stamp
			serializeddate = sdf.format(calendar.getTime());
			header = addSecurityElement(header, smc);
			header = addMCElement(header, smc);
			logger.verbose("@@@@@ header :: " + sm.getSOAPHeader().toString());
			if (sm.saveRequired()) {
				sm.saveChanges();
			}
			smc.setMessage(sm);
		} catch (Exception ex) {
			logger.error("!!!!! Caught General Exception in setSoapHeader *** :: " + ex);
			ex.printStackTrace();
		}
		logger.verbose("@@@@@ Exiting NWCGOBHandler::setSoapHeader");
	}

	private SOAPHeader addSecurityElement(SOAPHeader header, SOAPMessageContext smc) throws SOAPException {
		logger.verbose("@@@@@ Entering NWCGOBHandler::addSecurityElement");
		SOAPElement ns1SecurityElem = header.addChildElement(NWCGAAConstants.SECURITY_ELEM_NAME, NWCGAAConstants.SECURITY_ELEM_NAMESPACE_PREFIX, NWCGAAConstants.SECURITY_ELEM_NAMESPACE);
		SOAPElement ns1UsernameTokemElem = ns1SecurityElem.addChildElement(NWCGAAConstants.USERNAME_TOKEN_ELEM_NAME, NWCGAAConstants.SECURITY_ELEM_NAMESPACE_PREFIX);
		SOAPElement ns1UsernameElem = ns1UsernameTokemElem.addChildElement(NWCGAAConstants.USERNAME_ELEM_NAME, NWCGAAConstants.SECURITY_ELEM_NAMESPACE_PREFIX);
		String userName = (String) smc.get(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_USER);
		logger.verbose("@@@@@ Entering userName :: " + userName);
		if (userName == null || userName.length() == 0 || userName.equalsIgnoreCase("NWCGOrderStatusMonitorAgent")) {
			userName = NWCGAAConstants.ENV_USER_ID;
		}
		ns1UsernameElem.addTextNode(userName);
		SOAPElement ns1PasswordElem = ns1UsernameTokemElem.addChildElement(NWCGAAConstants.PASSWORD_ELEM_NAME, NWCGAAConstants.SECURITY_ELEM_NAMESPACE_PREFIX);
		javax.xml.soap.Name typeName = smc.getMessage().getSOAPPart().getEnvelope().createName(NWCGAAConstants.TYPE_NAME);
		ns1PasswordElem.addAttribute(typeName, NWCGAAConstants.TYPE_NAME_ATTR);
		String password = (String) smc.get(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_PASSWORD);
		ns1PasswordElem.addTextNode(password);
		ns1UsernameTokemElem.addChildElement(NWCGAAConstants.NONCE_ELEM_NAME, NWCGAAConstants.SECURITY_ELEM_NAMESPACE_PREFIX);
		SOAPElement ns2CreatedElem = ns1UsernameTokemElem.addChildElement(NWCGAAConstants.CREATED_ELEM_NAME, NWCGAAConstants.CREATED_ELEM_NAMESPACE_PREFIX, NWCGAAConstants.CREATED_ELEM_NAMESPACE);
		ns2CreatedElem.addTextNode(NWCGConstants.EMPTY_STRING + serializeddate);
		logger.verbose("@@@@@ Exiting NWCGOBHandler::addSecurityElement");
		return header;
	}

	/**
	 * 
	 * @param header
	 * @param smc
	 * @return
	 * @throws SOAPException
	 */
	private SOAPHeader addMCElement(SOAPHeader header, SOAPMessageContext smc) throws SOAPException {
		logger.verbose("@@@@@ Entering NWCGOBHandler::addMCElement");
		SOAPElement ns1MessageContextElem = header.addChildElement(NWCGAAConstants.MESSAGE_CONTEXT_ELEM_NAME, NWCGAAConstants.MESSAGE_CONTEXT_ELEM_NAMESPACE_PREFIX, NWCGAAConstants.MESSAGE_CONTEXT_ELEM_NAMESPACE);
		// dist ID
		SOAPElement distIDElem = ns1MessageContextElem.addChildElement(NWCGAAConstants.DISTRIBUTION_ID_ELEM_NAME);
		distIDElem.addTextNode(NWCGConstants.EMPTY_STRING);
		// <senderID
		SOAPElement senderIDElem = ns1MessageContextElem.addChildElement(NWCGAAConstants.SENDER_ID_ELEM_NAME);
		senderIDElem.addTextNode(NWCGAAConstants.SENDER_ID);
		// dateTimeSent
		SOAPElement dateTimeSentElem = ns1MessageContextElem.addChildElement(NWCGAAConstants.DATE_TIME_SENT_ELEM_NAME);
		dateTimeSentElem.addTextNode(NWCGConstants.EMPTY_STRING + serializeddate);
		// distributionStatus
		SOAPElement distributionStatusElem = ns1MessageContextElem.addChildElement(NWCGAAConstants.DISTRIBUTION_STATUS_ELEM_NAME);
		distributionStatusElem.addTextNode(NWCGAAConstants.DIST_STATUS_EXERCISE);
		// distributionType
		SOAPElement distributionTypeElem = ns1MessageContextElem.addChildElement(NWCGAAConstants.DISTRIBUTION_TYPE_ELEM_NAME);
		distributionTypeElem.addTextNode(NWCGAAConstants.DIST_TYPE_REQUEST);
		// messageName
		SOAPElement messageNameElem = ns1MessageContextElem.addChildElement(NWCGAAConstants.MESSAGE_NAME_ELEM_NAME);
		String messageName = (String) smc.get(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_MESSAGENAME);
		messageNameElem.addTextNode(messageName);
		// namespaceName
		SOAPElement namespaceNameElem = ns1MessageContextElem.addChildElement(NWCGAAConstants.NAMESPACE_ELEM_NAME);
		String namespace = (String) smc.get(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_NAMESPACE);
		namespaceNameElem.addTextNode(namespace);// determineNamespaceFromServiceGroup(messageName));
		// userOrganization - blank
		SOAPElement uoIDElem = ns1MessageContextElem.addChildElement(NWCGAAConstants.USER_ORG_ELEM_NAME);
		uoIDElem.addTextNode(NWCGConstants.EMPTY_STRING);
		// systemID - blank
		SOAPElement systemIDElem = ns1MessageContextElem.addChildElement(NWCGAAConstants.SYSTEM_ID_ELEM_NAME);
		systemIDElem.addTextNode(NWCGConstants.EMPTY_STRING);
		// systemType
		SOAPElement systemTypeElem = ns1MessageContextElem.addChildElement(NWCGAAConstants.SYSTEM_TYPE_ELEM_NAME);
		systemTypeElem.addTextNode(NWCGAAConstants.SYSTEM_TYPE);
		// customProperties
		SOAPElement customPropertiesElem = ns1MessageContextElem.addChildElement(NWCGAAConstants.CUSTOM_PROPERTY_ELEM_NAME);
		String custProp = (String) smc.get(NWCGWebServicesConstant.SOAP_HEADER_ELEMENT_CUSTOMPROP);
		customPropertiesElem.addTextNode(custProp);
		// securitySessionID - not set
		logger.verbose("@@@@@ Exiting NWCGOBHandler::addMCElement");
		return header;
	}
}