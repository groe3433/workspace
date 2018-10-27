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

package com.nwcg.icbs.yantra.handler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;
import com.nwcg.icbs.yantra.soap.NWCGAAUtil;
import javax.xml.rpc.Call;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.commCore.NWCGProperties;

import com.nwcg.icbs.yantra.util.common.ResourceUtil;

import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;

/**
 * @author sdas Abstract class for all outbound web service handlers.
 * 
 */
public abstract class NWCGJAXRPCWSHandler {

	public abstract Vector getStringTokens(SOAPMessageContext soap);

	public void formHeader(MessageContext mc, SOAPMessageContext soap,
			Vector stringVector) throws SOAPException {
		System.out.println("@@@@@ Entering NWCGJAXRPCWSHandler::formHeader @@@@@");

		String userName;
		String password;
		String loginID;
		String messageName;

		try {
			System.setProperty("java.protocol.handler.pkgs",
					"com.ibm.net.ssl.internal.www.protocol");
			java.security.Security.addProvider(new com.ibm.jsse.JSSEProvider());

			// reads from yifclient.properties
			String KeyStoreFile = ResourceUtil.get("nwcg.ob.keystore.file");
			String KeyStorePasswd = ResourceUtil
					.get("nwcg.ob.keystore.password");
			NWCGLoggerUtil.Log.info("nwcg.ob.keystore.file: " + KeyStoreFile);

			if (KeyStoreFile != null && KeyStorePasswd != null) {
				System.setProperty("javax.net.ssl.trustStoreType", ResourceUtil
						.get("nwcg.ob.truststore.trustStoreType"));
				System.setProperty("javax.net.ssl.keyStoreType", ResourceUtil
						.get("nwcg.ob.keystore.keyStoreType"));
				System.setProperty("javax.net.ssl.keyStore", KeyStoreFile);
				System.setProperty("javax.net.ssl.keyStorePassword",
						KeyStorePasswd);
				System.setProperty("javax.net.ssl.trustStore", KeyStoreFile);
				System.setProperty("javax.net.ssl.trustStorePassword",
						KeyStorePasswd);
			} else {
				NWCGLoggerUtil.Log
						.warning("KeyStoreFile or KeystorePassword is not set! SSL security not set.");
			}
			// mc.setProperty("ssl.configName",ResourceUtil.get("nwcg.ssl.configName","sundanceNode03/ROSS"));
			NWCGLoggerUtil.Log.info("nwcg.call.username"
					+ ResourceUtil.get("nwcg.call.username", "icbs_dev"));
			mc.setProperty(Call.USERNAME_PROPERTY, ResourceUtil.get(
					"nwcg.call.username", "icbs_dev"));
			mc.setProperty(Call.PASSWORD_PROPERTY, ResourceUtil.get(
					"nwcg.call.password", "password1!"));
			NWCGLoggerUtil.Log.info("nwcg.call.username"
					+ ResourceUtil.get("nwcg.call.password", "password1!"));
		} catch (Exception e) {
			NWCGLoggerUtil.printStackTraceToLog(e);
		}

		password = (String) stringVector.get(0);
		NWCGLoggerUtil.Log.info("password:" + password);
		loginID = (String) stringVector.get(1);
		NWCGLoggerUtil.Log.info("loginID" + loginID);
		userName = (String) stringVector.get(2);
		NWCGLoggerUtil.Log.info("userName:" + userName);
		messageName = (String) stringVector.get(3);
		NWCGLoggerUtil.Log.info("messageName:" + messageName);

		// Getting the date/time stamp
		Calendar calendar = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat(
				NWCGAAConstants.STRING_GREGORIAN_CAL_FORMAT);
		String serializeddate = sdf.format(calendar.getTime());

		SOAPHeader header = soap.getMessage().getSOAPHeader();
		if (header == null)
			header = soap.getMessage().getSOAPPart().getEnvelope().addHeader();

		/** *** Header Element formation **** */
		SOAPElement ns1SecurityElem = header.addChildElement(
				NWCGAAConstants.SECURITY_ELEM_NAME,
				NWCGAAConstants.SECURITY_ELEM_NAMESPACE_PREFIX,
				NWCGAAConstants.SECURITY_ELEM_NAMESPACE);
		SOAPElement ns1UsernameTokemElem = ns1SecurityElem.addChildElement(
				NWCGAAConstants.USERNAME_TOKEN_ELEM_NAME,
				NWCGAAConstants.SECURITY_ELEM_NAMESPACE_PREFIX);
		SOAPElement ns1UsernameElem = ns1UsernameTokemElem.addChildElement(
				NWCGAAConstants.USERNAME_ELEM_NAME,
				NWCGAAConstants.SECURITY_ELEM_NAMESPACE_PREFIX);
		ns1UsernameElem.addTextNode(userName);
		SOAPElement ns1PasswordElem = ns1UsernameTokemElem.addChildElement(
				NWCGAAConstants.PASSWORD_ELEM_NAME,
				NWCGAAConstants.SECURITY_ELEM_NAMESPACE_PREFIX);
		javax.xml.soap.Name typeName = soap.getMessage().getSOAPPart()
				.getEnvelope().createName(NWCGAAConstants.TYPE_NAME);
		ns1PasswordElem.addAttribute(typeName, NWCGAAConstants.TYPE_NAME_ATTR);
		ns1PasswordElem.addTextNode(password);
		ns1UsernameTokemElem.addChildElement(NWCGAAConstants.NONCE_ELEM_NAME,
				NWCGAAConstants.SECURITY_ELEM_NAMESPACE_PREFIX);
		SOAPElement ns2CreatedElem = ns1UsernameTokemElem.addChildElement(
				NWCGAAConstants.CREATED_ELEM_NAME,
				NWCGAAConstants.CREATED_ELEM_NAMESPACE_PREFIX,
				NWCGAAConstants.CREATED_ELEM_NAMESPACE);
		ns2CreatedElem.addTextNode("" + serializeddate);
		/** ********* */

		/** *** Message Context Element Formation ***** */
		SOAPElement ns1MessageContextElem = header.addChildElement(
				NWCGAAConstants.MESSAGE_CONTEXT_ELEM_NAME,
				NWCGAAConstants.MESSAGE_CONTEXT_ELEM_NAMESPACE_PREFIX,
				NWCGAAConstants.MESSAGE_CONTEXT_ELEM_NAMESPACE);
		ns1MessageContextElem
				.addChildElement(NWCGAAConstants.DISTRIBUTION_ID_ELEM_NAME);
		SOAPElement senderIDElem = ns1MessageContextElem
				.addChildElement(NWCGAAConstants.SENDER_ID_ELEM_NAME);
		senderIDElem.addTextNode(NWCGAAConstants.SENDER_ID);
		SOAPElement dateTimeSentElem = ns1MessageContextElem
				.addChildElement(NWCGAAConstants.DATE_TIME_SENT_ELEM_NAME);
		dateTimeSentElem.addTextNode("" + serializeddate);
		SOAPElement distributionStatusElem = ns1MessageContextElem
				.addChildElement(NWCGAAConstants.DISTRIBUTION_STATUS_ELEM_NAME);
		distributionStatusElem
				.addTextNode(NWCGAAConstants.DIST_STATUS_EXERCISE);
		SOAPElement distributionTypeElem = ns1MessageContextElem
				.addChildElement(NWCGAAConstants.DISTRIBUTION_TYPE_ELEM_NAME);
		distributionTypeElem.addTextNode(NWCGAAConstants.DIST_TYPE_REQUEST);
		SOAPElement messageNameElem = ns1MessageContextElem
				.addChildElement(NWCGAAConstants.MESSAGE_NAME_ELEM_NAME);
		messageNameElem.addTextNode(messageName);
		SOAPElement namespaceNameElem = ns1MessageContextElem
				.addChildElement(NWCGAAConstants.NAMESPACE_ELEM_NAME);
		namespaceNameElem
				.addTextNode(determineNamespaceFromServiceGroup(messageName));
		SOAPElement systemIDElem = ns1MessageContextElem
				.addChildElement(NWCGAAConstants.SYSTEM_ID_ELEM_NAME);
		// jay : system id should always be blank
		systemIDElem.addTextNode("");
		SOAPElement systemTypeElem = ns1MessageContextElem
				.addChildElement(NWCGAAConstants.SYSTEM_TYPE_ELEM_NAME);
		systemTypeElem.addTextNode(NWCGAAConstants.SYSTEM_TYPE);
		SOAPElement customPropertiesElem = ns1MessageContextElem
				.addChildElement(NWCGAAConstants.CUSTOM_PROPERTY_ELEM_NAME);
		customPropertiesElem
				.addTextNode(determineCustomProperties(messageName));
		/** ******** */

		SOAPMessage msg = soap.getMessage();
		try {
			NWCGLoggerUtil.Log.info("msg>>" + NWCGAAUtil.serialize(msg));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			msg.writeTo(System.out);
		} catch (IOException e) {
			NWCGLoggerUtil.printStackTraceToLog(e);
		}
		
		System.out.println("@@@@@ Exiting NWCGJAXRPCWSHandler::formHeader @@@@@");
	}

	public static String determineCustomProperties(String messageName) {
		System.out.println("@@@@@ Entering NWCGJAXRPCWSHandler::determineCustomProperties @@@@@");
		
		String customProp = "";
		String isSync = NWCGProperties.getProperty(messageName + ".isSync");
		if (isSync.equalsIgnoreCase("TRUE")) {
			customProp = NWCGAAConstants.CUSTOM_PROPERTIES;
		}
		
		System.out.println("@@@@@ Exiting NWCGJAXRPCWSHandler::determineCustomProperties @@@@@");
		return customProp;
	}

	public String determineNamespaceFromServiceGroup(String messageName) {
		System.out.println("@@@@@ Entering NWCGJAXRPCWSHandler::determineNamespaceFromServiceGroup @@@@@");
		
		String nameSpace = "";
		String serviceGroup = NWCGProperties.getProperty(messageName
				+ ".serviceGroup");
		if (serviceGroup.equals("CATALOG")) {
			nameSpace = NWCGAAConstants.CATALOG_NAMESPACE;
		} else if (serviceGroup.equals("INCIDENT")) {
			nameSpace = NWCGAAConstants.RESOURCE_ORDER_NAMESPACE;
		} else if (serviceGroup.equals("OPERATIONS")) {
			nameSpace = NWCGAAConstants.OPERATIONS_NAMESPACE;
		}
		
		System.out.println("@@@@@ Exiting NWCGJAXRPCWSHandler::determineNamespaceFromServiceGroup @@@@@");
		return nameSpace;
	}
}