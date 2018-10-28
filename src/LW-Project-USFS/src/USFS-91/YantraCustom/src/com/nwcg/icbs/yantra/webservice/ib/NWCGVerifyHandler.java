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
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.yantra.yfc.log.YFCLogCategory;


/**
 * This simple SOAPHandler will output the contents of incoming
 * and outgoing messages.
 */
public class NWCGVerifyHandler implements SOAPHandler<SOAPMessageContext> {
	
	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGVerifyHandler.class);

	final String HANDLER_NAME = "NWCGVerifyHandler";

	private static SimpleDateFormat sdf = new SimpleDateFormat(NWCGAAConstants.STRING_GREGORIAN_CAL_FORMAT);

	private static Calendar calendar = new GregorianCalendar();

	private String serializeddate = null;

	public Set<QName> getHeaders() {
		return null;
	}

	public boolean handleMessage(SOAPMessageContext smc) {
		logger.verbose("@@@@@ Entering NWCGVerifyHandler::handleMessage");
		Boolean direction = (Boolean) smc.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (direction.booleanValue() == true) {
			logger.verbose("@@@@@ NWCGOBHandler.handleMessage>>add soap headers");
			NWCGAAUtil.setSSL();
			try {
				SOAPMessage sm = smc.getMessage();
				SOAPHeader header = sm.getSOAPHeader();
				if (header == null)
					header = sm.getSOAPPart().getEnvelope().addHeader();
				//Getting the date/time stamp 		
				serializeddate = sdf.format(calendar.getTime());
				header = addSecurityElement(header, smc);
				header = addMCElement(header, smc);
				if (sm.saveRequired()) {
					sm.saveChanges();
				}
				smc.setMessage(sm);
			} catch (Exception e) {
				logger.error("!!!!! Exception in setSoapHeader" + e.getMessage());
				logger.error(e.getLocalizedMessage(), e);
			}
		}
		logger.verbose("@@@@@ Exiting NWCGVerifyHandler::handleMessage");
		return true;
	}

	public boolean handleFault(SOAPMessageContext smc) {
		logger.error("!!!!! In NWCGVerifyHandler::handleFault " + HANDLER_NAME);
		return true;
	}

	// nothing to clean up
	public void close(MessageContext messageContext) {
	}

	private SOAPHeader addSecurityElement(SOAPHeader header, SOAPMessageContext smc) throws SOAPException {
		logger.verbose("@@@@@ Entering NWCGVerifyHandler::addSecurityElement");
		SOAPElement ns1SecurityElem = header.addChildElement(NWCGAAConstants.SECURITY_ELEM_NAME, NWCGAAConstants.SECURITY_ELEM_NAMESPACE_PREFIX, NWCGAAConstants.SECURITY_ELEM_NAMESPACE);
		SOAPElement ns1UsernameTokemElem = ns1SecurityElem.addChildElement(NWCGAAConstants.USERNAME_TOKEN_ELEM_NAME, NWCGAAConstants.SECURITY_ELEM_NAMESPACE_PREFIX);
		SOAPElement ns1UsernameElem = ns1UsernameTokemElem.addChildElement(NWCGAAConstants.USERNAME_ELEM_NAME, NWCGAAConstants.SECURITY_ELEM_NAMESPACE_PREFIX);
		// Setting it to blank, later if required we need to set this up
		ns1UsernameElem.addTextNode(NWCGConstants.EMPTY_STRING);
		SOAPElement ns1PasswordElem = ns1UsernameTokemElem.addChildElement(NWCGAAConstants.PASSWORD_ELEM_NAME, NWCGAAConstants.SECURITY_ELEM_NAMESPACE_PREFIX);
		javax.xml.soap.Name typeName = smc.getMessage().getSOAPPart().getEnvelope().createName(NWCGAAConstants.TYPE_NAME);
		ns1PasswordElem.addAttribute(typeName, NWCGAAConstants.TYPE_NAME_ATTR);
		// Setting it to blank, later if required we need to set this up
		ns1PasswordElem.addTextNode(NWCGConstants.EMPTY_STRING);
		ns1UsernameTokemElem.addChildElement(NWCGAAConstants.NONCE_ELEM_NAME, NWCGAAConstants.SECURITY_ELEM_NAMESPACE_PREFIX);
		SOAPElement ns2CreatedElem = ns1UsernameTokemElem.addChildElement(NWCGAAConstants.CREATED_ELEM_NAME, NWCGAAConstants.CREATED_ELEM_NAMESPACE_PREFIX, NWCGAAConstants.CREATED_ELEM_NAMESPACE);
		ns2CreatedElem.addTextNode(NWCGConstants.EMPTY_STRING + serializeddate);
		logger.verbose("@@@@@ Exiting NWCGVerifyHandler::addSecurityElement");
		return header;
	}

	private SOAPHeader addMCElement(SOAPHeader header, SOAPMessageContext smc) throws SOAPException {
		logger.verbose("@@@@@ Entering NWCGVerifyHandler::addMCElement");
		SOAPElement ns1MessageContextElem = header.addChildElement(NWCGAAConstants.MESSAGE_CONTEXT_ELEM_NAME, NWCGAAConstants.MESSAGE_CONTEXT_ELEM_NAMESPACE_PREFIX, NWCGAAConstants.MESSAGE_CONTEXT_ELEM_NAMESPACE);
		//dist ID
		SOAPElement distIDElem = ns1MessageContextElem.addChildElement(NWCGAAConstants.DISTRIBUTION_ID_ELEM_NAME);
		distIDElem.addTextNode(NWCGConstants.EMPTY_STRING);
		//<senderID
		SOAPElement senderIDElem = ns1MessageContextElem.addChildElement(NWCGAAConstants.SENDER_ID_ELEM_NAME);
		senderIDElem.addTextNode(NWCGAAConstants.SENDER_ID);
		//dateTimeSent
		SOAPElement dateTimeSentElem = ns1MessageContextElem.addChildElement(NWCGAAConstants.DATE_TIME_SENT_ELEM_NAME);
		dateTimeSentElem.addTextNode(NWCGConstants.EMPTY_STRING + serializeddate);
		//distributionStatus
		SOAPElement distributionStatusElem = ns1MessageContextElem.addChildElement(NWCGAAConstants.DISTRIBUTION_STATUS_ELEM_NAME);
		distributionStatusElem.addTextNode(NWCGAAConstants.DIST_STATUS_EXERCISE);
		//distributionType
		SOAPElement distributionTypeElem = ns1MessageContextElem.addChildElement(NWCGAAConstants.DISTRIBUTION_TYPE_ELEM_NAME);
		distributionTypeElem.addTextNode(NWCGAAConstants.DIST_TYPE_REQUEST);
		//messageName
		SOAPElement messageNameElem = ns1MessageContextElem.addChildElement(NWCGAAConstants.MESSAGE_NAME_ELEM_NAME);
		// since this handler is specific to verifysecuritysession hard coding these values
		messageNameElem.addTextNode("VerifySecuritySessionReq");
		//namespaceName
		SOAPElement namespaceNameElem = ns1MessageContextElem.addChildElement(NWCGAAConstants.NAMESPACE_ELEM_NAME);		
		namespaceNameElem.addTextNode(NWCGAAConstants.COMMMON_TYPES_NAMESPACE);
		//userOrganization - blank
		SOAPElement uoIDElem = ns1MessageContextElem.addChildElement(NWCGAAConstants.USER_ORG_ELEM_NAME);
		uoIDElem.addTextNode(NWCGConstants.EMPTY_STRING);
		//systemID - blank
		SOAPElement systemIDElem = ns1MessageContextElem.addChildElement(NWCGAAConstants.SYSTEM_ID_ELEM_NAME);
		systemIDElem.addTextNode(NWCGConstants.EMPTY_STRING);
		//systemType
		SOAPElement systemTypeElem = ns1MessageContextElem.addChildElement(NWCGAAConstants.SYSTEM_TYPE_ELEM_NAME);
		systemTypeElem.addTextNode(NWCGAAConstants.SYSTEM_TYPE);
		//customProperties
		SOAPElement customPropertiesElem = ns1MessageContextElem.addChildElement(NWCGAAConstants.CUSTOM_PROPERTY_ELEM_NAME);
		customPropertiesElem.addTextNode(NWCGAAConstants.CUSTOM_PROPERTIES);
		logger.verbose("@@@@@ Exiting NWCGVerifyHandler::addMCElement");
		return header;
	}
}