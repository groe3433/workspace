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
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;

import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.commCore.NWCGCodeTemplate;
import com.nwcg.icbs.yantra.commCore.NWCGProperties;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGSOAPMsg {
	
	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGSOAPMsg.class);

	private Object _attribute;

	SOAPMessage theSOAPMsg;
	String xml;
	String body;
	String username;
	String password;
	String serviceName;
	String distId;

	public NWCGCodeTemplate theTemplate;

	public String templateTxt;

	public NWCGSOAPMsg() {
		this.xml = "(NONE)";
	}

	public NWCGSOAPMsg(String xml) {
		this.xml = xml;
	}

	NWCGSOAPMsg(SOAPMessage msg) {
		theSOAPMsg = msg;
	}

	public SOAPMessage getSOAPMessageImpl() {

		return theSOAPMsg;
	}

	public void setSOAPMessageImpl(SOAPMessage theSOAPMsg) {
		this.theSOAPMsg = theSOAPMsg;
	}

	public void getHeader() {
		throw new UnsupportedOperationException();
	}

	public void getEnvelope() {
		throw new UnsupportedOperationException();
	}

	public String getBody() {
		return body;
	}

	public void setEnvelope() {
		throw new UnsupportedOperationException();
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setHeader() {
		throw new UnsupportedOperationException();
	}

	public void getWebServiceMethod() {
		throw new UnsupportedOperationException();
	}

	public void setWebServiceMethod() {
		throw new UnsupportedOperationException();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public SOAPMessage buildSOAPMsg() throws Exception {
		logger.verbose("@@@@@ Entering NWCGSOAPMsg::buildSOAPMsg");
		MessageFactory mf = MessageFactory.newInstance();
		theSOAPMsg = mf.createMessage();
		String path = NWCGProperties.getProperty("SOAP_TEMPLATE");
		byte[] data = null;
		File f = new File(path);
		FileInputStream fs = new FileInputStream(f);
		data = new byte[fs.available()];
		fs.read(data);
		fs.close();
		String msg = new String(data);
		theTemplate = new NWCGCodeTemplate(msg);
		StringWriter writer = new StringWriter();
		try {
			String secStr = writer.toString();
			secStr = secStr.substring(secStr.indexOf('>') + 1);
			writer = new StringWriter();
			String mcStr = writer.toString();
			mcStr = mcStr.substring(mcStr.indexOf('>') + 1);
			theTemplate.setSlot("header", secStr + "\n" + mcStr);
			theTemplate.setSlot("body", "");
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception..." + e);
		}
		logger.verbose("@@@@@ Exiting NWCGSOAPMsg::buildSOAPMsg");
		return theSOAPMsg;
	}

	public void serialize(SOAPMessage soapMessage) throws Exception {
		logger.verbose("@@@@@ Entering NWCGSOAPMsg::serialize");
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
		logger.verbose("@@@@@ Exiting NWCGSOAPMsg::serialize");
	}

	public String generateCode() {
		if (theTemplate == null)
			return this.getXml();
		return theTemplate.generateCode();
	}

	public String getDistId() {
		return distId;
	}

	public void setDistId(String distId) {
		this.distId = distId;
	}
}