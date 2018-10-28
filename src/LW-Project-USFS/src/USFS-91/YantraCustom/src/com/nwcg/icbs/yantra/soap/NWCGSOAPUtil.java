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

import com.yantra.yfc.log.YFCLogCategory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGSOAPUtil {

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGSOAPUtil.class);

	public NWCGSOAPMsg parseSOAP(String aSoapMsgString) throws SOAPException {
		logger.verbose("@@@@@ Entering NWCGSOAPUtil::parseSOAP");
		MessageFactory mf = MessageFactory.newInstance();
		MimeHeaders mh = new MimeHeaders();
		StringBuffer sb = new StringBuffer(aSoapMsgString);
		ByteArrayInputStream bais = new ByteArrayInputStream(aSoapMsgString.getBytes());
		try {
			SOAPMessage msg = mf.createMessage(mh, bais);
			NWCGSOAPMsg nmsg = new NWCGSOAPMsg(msg);
			logger.verbose("@@@@@ Exiting NWCGSOAPUtil::parseSOAP (nmsg)");
			return nmsg;
		} catch (Exception e) {

		}
		logger.verbose("@@@@@ Exiting NWCGSOAPUtil::parseSOAP (null)");
		return null;
	}

	public static String serialize(SOAPMessage soapMessage) throws Exception {
		logger.verbose("@@@@@ Entering NWCGSOAPUtil::serialize");
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
		logger.verbose("@@@@@ Exiting NWCGSOAPUtil::serialize");
		return baos.toString();
	}

	public static String buildSOAP(NWCGSOAPMsg aSOAPMessage) throws Exception {
		logger.verbose("@@@@@ In NWCGSOAPUtil::buildSOAP");
		return aSOAPMessage.generateCode();
	}

	public static String buildAndSend(NWCGSOAPMsg aSOAPMessage, String target) throws Exception {
		logger.verbose("@@@@@ Entering NWCGSOAPUtil::buildAndSend");
		String msg_str = buildSOAP(aSOAPMessage);
		String reply = sendHttpMsg(msg_str, target);
		logger.verbose("@@@@@ Exiting NWCGSOAPUtil::buildAndSend");
		return reply;
	}

	public static String sendHttpMsg(String msg_str, String target) {
		logger.verbose("@@@@@ In NWCGSOAPUtil::buildAndSend (1)");
		return null;
	}

	public static String sendHttpMsg(String xmldata, String hostname, int port, String target) {
		logger.verbose("@@@@@ In NWCGSOAPUtil::buildAndSend (2)");
		return null;
	}

	public void sendMessageAsync(NWCGSOAPMsg aSoapMessage) {
		throw new UnsupportedOperationException();
	}

	public NWCGSOAPMsg sendMessageSync(NWCGSOAPMsg aSoapMessage) {
		throw new UnsupportedOperationException();
	}
}