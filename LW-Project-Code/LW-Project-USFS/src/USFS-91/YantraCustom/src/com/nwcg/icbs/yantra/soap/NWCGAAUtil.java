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

package com.nwcg.icbs.yantra.soap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.StringTokenizer;

import javax.mail.internet.MimeUtility;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;

import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.commCore.NWCGCodeTemplate;
import com.nwcg.icbs.yantra.commCore.NWCGProperties;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfs.core.YFSSystem;

public class NWCGAAUtil {

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGAAUtil.class);

	public static String readText(String filename) throws Exception {
		logger.verbose("@@@@@ Entering NWCGAAUtil::readText");
		byte[] data = null;
		File f = new File(filename);
		FileInputStream fs = new FileInputStream(f);
		data = new byte[fs.available()];
		fs.read(data);
		fs.close();
		String msg_str = new String(data);
		logger.verbose("@@@@@ Exiting NWCGAAUtil::readText");
		return msg_str;
	}

	public static Object deserializeObject(String code) throws Exception {
		logger.verbose("@@@@@ Entering NWCGAAUtil::deserializeObject");
		byte data[] = code.getBytes();
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		InputStream is = MimeUtility.decode(bais, "base64");
		ObjectInputStream ois = new ObjectInputStream(is);
		Object o = ois.readObject();
		ois.close();
		logger.verbose("@@@@@ Exiting NWCGAAUtil::deserializeObject");
		return o;
	}

	public static String serializeObject(Object o) throws Exception {
		logger.verbose("@@@@@ Entering NWCGAAUtil::serializeObject");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStream os = MimeUtility.encode(baos, "base64");
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(o);
		oos.close();
		baos.close();
		logger.verbose("@@@@@ Exiting NWCGAAUtil::serializeObject");
		return baos.toString();
	}

	public static NWCGSOAPMsg parseSOAP(Document doc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGAAUtil::parseSOAP");
		String aSoapMsgString = serialize(doc);
		MessageFactory mf = MessageFactory.newInstance();
		MimeHeaders mh = new MimeHeaders();
		ByteArrayInputStream bais = new ByteArrayInputStream(aSoapMsgString.getBytes());
		SOAPMessage msg = mf.createMessage(mh, bais);
		NWCGSOAPMsg nmsg = new NWCGSOAPMsg(msg);
		logger.verbose("@@@@@ Exiting NWCGAAUtil::parseSOAP");
		return nmsg;
	}

	public static Document buildXMLDocument(String xml) {
		logger.verbose("@@@@@ Entering NWCGAAUtil::buildXMLDocument");
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
			doc = db.parse(bais);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		logger.verbose("@@@@@ Exiting NWCGAAUtil::buildXMLDocument");
		return doc;
	}

	public static String serialize(Document doc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGAAUtil::serialize (1)");
		org.apache.xml.serialize.OutputFormat format = new org.apache.xml.serialize.OutputFormat(doc);
		format.setLineWidth(65);
		format.setIndenting(true);
		format.setIndent(2);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLSerializer serializer = new XMLSerializer(baos, format);
		serializer.serialize(doc);
		logger.verbose("@@@@@ Exiting NWCGAAUtil::serialize (1)");
		return baos.toString();
	}

	public static String serialize(SOAPMessage soapMessage) throws Exception {
		logger.verbose("@@@@@ Entering NWCGAAUtil::serialize (2)");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		ByteArrayOutputStream baws = new ByteArrayOutputStream();
		soapMessage.writeTo(baws);
		ByteArrayInputStream bais = new ByteArrayInputStream(baws.toByteArray());
		Document doc = db.parse(bais);
		org.apache.xml.serialize.OutputFormat format = new org.apache.xml.serialize.OutputFormat(doc);
		format.setLineWidth(65);
		format.setIndenting(true);
		format.setIndent(2);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLSerializer serializer = new XMLSerializer(baos, format);
		serializer.serialize(doc);
		logger.verbose("@@@@@ Exiting NWCGAAUtil::serialize (2)");
		return baos.toString();
	}

	public static String buildSOAP(com.nwcg.icbs.yantra.soap.NWCGSOAPMsg aSOAPMessage) throws Exception {
		logger.verbose("@@@@@ In NWCGAAUtil::buildSOAP ");
		return aSOAPMessage.generateCode();
	}

	public static String buildAndSend(com.nwcg.icbs.yantra.soap.NWCGSOAPMsg aSOAPMessage, String target) throws Exception {
		logger.verbose("@@@@@ In NWCGAAUtil::buildAndSend ");
		String msg_str = buildSOAP(aSOAPMessage);
		String reply = sendHttpMsg(msg_str);
		return reply;
	}

	public static String sendHttpMsg(String msg_str) {
		logger.verbose("@@@@@ Entering NWCGAAUtil::sendHttpMsg (1) ");
		String urlAddress = null;
		int port = -1;
		String port_str = NWCGProperties.getProperty("ROSS_PORT");
		port = Integer.parseInt(port_str);
		logger.verbose("Sending HTTP Msg:" + msg_str);
		String reply = sendHttpMsg(msg_str, urlAddress, port);
		logger.verbose("@@@@@ Exiting NWCGAAUtil::sendHttpMsg (1) ");
		return reply;
	}

	public static String sendHttpMsg(String xmldata, String hostname, int port) {
		// TODO: Invoke the IBM Dispatch Client to send the message to ross
		// 1. Create the IBM Dispatch Client (DC)
		// 2. Configure DC for XML SOAP Message Transfer
		// 3. Convert xmldata to SOAPMessage object
		// 4. Send message
		// 5. Convert response SOAPMessage to XML
		logger.verbose("@@@@@ In NWCGAAUtil::sendHttpMsg (2)");
		String respText = NWCGConstants.EMPTY_STRING;
		return respText;
	}

	public static String lookupNodeValue(Document doc, String nodeName) {
		logger.verbose("@@@@@ Entering NWCGAAUtil::lookupNodeValue ");
		NodeList nL = doc.getElementsByTagName(nodeName);
		Node nMC = nL.item(0);
		String messageName = nMC.getFirstChild().getNodeValue();
		logger.verbose("@@@@@ Exiting NWCGAAUtil::lookupNodeValue ");
		return messageName;
	}

	public static String determineServiceGroup(String serviceName) {
		logger.verbose("@@@@@ Entering NWCGAAUtil::determineServiceGroup ");
		if (serviceName.equals("CreateIncidentAndRequestReq")) {
			logger.verbose("@@@@@ Exiting NWCGAAUtil::determineServiceGroup (1)");
			return NWCGAAConstants.INCIDENT_SERVICE_GROUP_NAME;
		}
		logger.verbose("@@@@@ Exiting NWCGAAUtil::determineServiceGroup (2)");
		return NWCGConstants.EMPTY_STRING;
	}

	public void sendMessageAsync(com.nwcg.icbs.yantra.soap.NWCGSOAPMsg aSoapMessage) {
		logger.verbose("@@@@@ In NWCGAAUtil::sendMessageAsync (1) ");
		throw new UnsupportedOperationException();
	}

	public NWCGSOAPMsg sendMessageSync(com.nwcg.icbs.yantra.soap.NWCGSOAPMsg aSoapMessage) {
		logger.verbose("@@@@@ In NWCGAAUtil::sendMessageSync (1) ");
		throw new UnsupportedOperationException();
	}

	public static String lookupMessageKeyFromDistId(YFSEnvironment env, String type, String distID) throws Exception {
		logger.verbose("@@@@@ Entering NWCGAAUtil::lookupMessageKeyFromDistId (1) ");
		String templateFileName = null;
		String listServiceName = null;
		String tagname = null;
		if (type.equals("IB")) {
			templateFileName = NWCGProperties.getProperty("SDF_INBOUND_MESSAGE_SAMPLE_FILENAME");
			listServiceName = NWCGAAConstants.SDF_GET_IB_MESSAGE_LIST_SERVICE_NAME;
			tagname = "NWCGInboundMessage";
		}
		if (type.equals("OB")) {
			templateFileName = NWCGProperties.getProperty("SDF_OUTBOUND_MESSAGE_SAMPLE_FILENAME");
			listServiceName = NWCGAAConstants.SDF_GET_OB_MESSAGE_LIST_SERVICE_NAME;
			tagname = "NWCGOutboundMessage";
		}
		String msg_str = NWCGAAUtil.readText(templateFileName);
		NWCGCodeTemplate ct = new NWCGCodeTemplate(msg_str);
		ct.setSlot("dist_id", distID);
		ct.setSlot("msg", NWCGConstants.EMPTY_STRING);
		ct.setSlot("msgKey", NWCGConstants.EMPTY_STRING);
		ct.setSlot("sysName", NWCGConstants.EMPTY_STRING);
		ct.setSlot("status", NWCGConstants.EMPTY_STRING);
		ct.setSlot("msgType", NWCGConstants.EMPTY_STRING);
		msg_str = ct.generateCode();
		logger.verbose("@@@@@ NWCGResponseStatusTypeHandler.lookupMessageKeyFromDistId" + msg_str);
		logger.verbose("@@@@@ listServiceName:'" + listServiceName + "'");
		Document doc = NWCGAAUtil.buildXMLDocument(msg_str);
		Document doc_rt = CommonUtilities.invokeService(env, listServiceName, doc);
		NodeList nlOBMsg = doc_rt.getElementsByTagName(tagname);
		if (nlOBMsg != null && nlOBMsg.getLength() > 0) {
			for (int index = 0; index < nlOBMsg.getLength(); index++) {
				Node elemOBMsg = nlOBMsg.item(index);
				NamedNodeMap map = elemOBMsg.getAttributes();
				Node node_mt = map.getNamedItem("MessageType");
				String strMsgType = node_mt.getNodeValue();
				if (strMsgType.equals(NWCGAAConstants.MESSAGE_TYPE_LATEST)) {
					Node node_mk = map.getNamedItem("MessageKey");
					String strMK = node_mk.getNodeValue();
					return strMK;
				}
			}
		}
		logger.verbose("@@@@@ NWCGResponseStatusTypeHandler.lookupMessageKeyFromDistId Unable to find latest message with distID '" + distID + "'");
		logger.verbose("@@@@@ Exiting NWCGAAUtil::lookupMessageKeyFromDistId (1) ");
		throw new Exception("Unable to find latest message with distID '" + distID + "'");
	}

	public static String lookupMessageKeyFromDistId(String type, String distID) throws Exception {
		logger.verbose("@@@@@ In NWCGAAUtil::lookupMessageKeyFromDistId ");
		YFSEnvironment env = CommonUtilities.createEnvironment("yantra", "100");
		return lookupMessageKeyFromDistId(env, type, distID);
	}

	/**
	 * This method will get the class from NWCGAnAImpl.properties based on the
	 * type of the message (inbound or outbound). If it is outbound, then we
	 * need to get the .OBResponseProcessor class. As of now, we will not use
	 * .OBRequestProcessor as that requires a call initiation from A&A to
	 * backend which is not in near future, so returning .OBResponseProcessor
	 * everytime. If it is inbound, then get the .IBProcessor.
	 * 
	 * @param msgName
	 * @return
	 */
	public static String getHandler(String msgName) {
		logger.verbose("@@@@@ Entering NWCGAAUtil::getHandler ");
		String handlerClass = NWCGConstants.EMPTY_STRING;
		String boundType = NWCGProperties.getProperty(msgName.concat(NWCGAAConstants.HANDLER_PROP_EXTENSION_TYPE));
		if (boundType.equalsIgnoreCase(NWCGAAConstants.MESSAGE_DIR_TYPE_IB)) {
			handlerClass = NWCGProperties.getProperty(msgName.concat(NWCGAAConstants.HANDLER_PROP_EXTENSION_IBPROCESSOR));
		} else if (boundType.equalsIgnoreCase(NWCGAAConstants.MESSAGE_DIR_TYPE_OB)) {
			handlerClass = NWCGProperties.getProperty(msgName.concat(NWCGAAConstants.HANDLER_PROP_EXTENSION_OBRESPONSE_PROCESSOR));
		}
		logger.verbose("@@@@@ NWCGIBReader::getHandler, Handler Class Name:" + handlerClass);
		logger.verbose("@@@@@ Exiting NWCGAAUtil::getHandler ");
		return handlerClass;
	}

	/**
	 * Get the node name with out URI. If the passed string is
	 * ron:UpdateIncidentKeyNotification, then the node name will be returned as
	 * UpdateIncidentKeyNotification. If the node name is
	 * UpdateIncidentKeyNotification, then the same value is returned.
	 * 
	 * @param nodeName
	 * @return
	 */
	public static String getNodeNameWOURI(String nodeName) {
		logger.verbose("@@@@@ Entering NWCGAAUtil::getNodeNameWOURI ");
		int index = nodeName.indexOf(':');
		if (index != -1) {
			nodeName = nodeName.substring(index + 1, nodeName.length());
		}
		logger.verbose("@@@@@ Exiting NWCGAAUtil::getNodeNameWOURI ");
		return nodeName;
	}

	/**
	 * Determines if inbound message is sync or async based on custom property
	 * in the header
	 * 
	 * @param serviceName
	 * @return boolean if property is true - it is sync message, otherwise if it
	 *         is false or absent - async message
	 */
	public static boolean isSync(String serviceName, String customProp) {
		logger.verbose("@@@@@ Entering NWCGAAUtil::isSync ");
		boolean result = false;
		boolean bTokenFound = false;
		logger.verbose("customProp in isSync :" + customProp);
		logger.verbose("serviceName in isSync :" + serviceName);
		if (!StringUtil.isEmpty(customProp)) {
			StringTokenizer strTokenizer = new StringTokenizer(customProp, ";");
			if (strTokenizer != null && strTokenizer.hasMoreTokens()) {
				String strToken = strTokenizer.nextToken();
				String propName = NWCGConstants.EMPTY_STRING;
				if (strToken.indexOf("=") != -1)
					propName = customProp.substring(0, strToken.indexOf("="));
				logger.verbose("propName :" + propName);
				// Value set in custom property takes precedence
				if (propName.equalsIgnoreCase("processSynchronously")) {
					bTokenFound = true;
					String propValue = customProp.substring(strToken.indexOf("=") + 1);
					if (propValue.equalsIgnoreCase("true"))
						result = true;
				}
			}
		}
		if (!bTokenFound) {
			result = ResourceUtil.getAsBoolean(serviceName + ".isSync", false);
		}
		logger.verbose("@@@@@ Exiting NWCGAAUtil::isSync ");
		return result;
	}

	public static void setSSL() {
		logger.verbose("@@@@@ Entering NWCGAAUtil::setSSL ");
		try {
			System.setProperty("com.ibm.ssl.performURLHostNameVerification", "true");
			
			System.setProperty("java.protocol.handler.pkgs", "com.ibm.net.ssl.internal.www.protocol");
			java.security.Security.addProvider(new com.ibm.jsse.JSSEProvider());
			
			System.setProperty("com.ibm.ssl.enableSignerExchangePrompt", "true");
			
			/* Update to use customer_overrides for 9.1 by Luke */
			//String trustStoreFile = ResourceUtil.get("nwcg.ob.truststore.file");
			//String trustStorePwd = ResourceUtil.get("nwcg.ob.truststore.password");
			//String trustStoreType = ResourceUtil.get("nwcg.ob.truststore.trustStoreType");
			//String keyStoreFile = ResourceUtil.get("nwcg.ob.keystore.file");
			//String keyStorePwd = ResourceUtil.get("nwcg.ob.keystore.password");
			//String keyStoreType = ResourceUtil.get("nwcg.ob.keystore.keyStoreType");
			
			String trustStoreFile = YFSSystem.getProperty("nwcg.ob.truststore.file");
			String trustStorePwd = YFSSystem.getProperty("nwcg.ob.truststore.password");
			String trustStoreType = YFSSystem.getProperty("nwcg.ob.truststore.trustStoreType");
			String keyStoreFile = YFSSystem.getProperty("nwcg.ob.keystore.file");
			String keyStorePwd = YFSSystem.getProperty("nwcg.ob.keystore.password");
			String keyStoreType = YFSSystem.getProperty("nwcg.ob.keystore.keyStoreType");
			
			logger.verbose("@@@@@ truststore file: " + trustStoreFile);
			logger.verbose("@@@@@ truststore pwd : " + trustStorePwd);
			logger.verbose("@@@@@ truststore type : " + trustStoreType);
			logger.verbose("@@@@@ keystore file  : " + keyStoreFile);
			logger.verbose("@@@@@ keystore pwd   : " + keyStorePwd);
			logger.verbose("@@@@@ keystore pwd   : " + keyStoreType);
			System.setProperty("javax.net.ssl.keyStoreType", keyStoreType);
			System.setProperty("javax.net.ssl.keyStore", keyStoreFile);
			System.setProperty("javax.net.ssl.keyStorePassword", keyStorePwd);
			System.setProperty("javax.net.ssl.trustStoreType", trustStoreType);
			System.setProperty("javax.net.ssl.trustStore", trustStoreFile);
			System.setProperty("javax.net.ssl.trustStorePassword", trustStorePwd);

	        System.setProperty("com.ibm.ssl.keyStore", keyStoreFile);
	        System.setProperty("com.ibm.ssl.keyStorePassword", keyStorePwd);
	        System.setProperty("com.ibm.ssl.trustStoreType","PKCS12");
	        System.setProperty("com.ibm.ssl.trustStore", trustStoreFile);
	        System.setProperty("com.ibm.ssl.trustStorePassword", trustStorePwd);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);
			e.printStackTrace();
		}
		logger.verbose("@@@@@ Exiting NWCGAAUtil::setSSL ");
	}
}