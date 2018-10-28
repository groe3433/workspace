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

package com.nwcg.icbs.yantra.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.w3c.dom.Document;

import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;

/**
 * This class will make a webservice call to ROSS. Input to ROSS is read from a
 * file. This file needs to be present in the directory where this standalone
 * application is run. The only dependency from Sterling perspective is XMLUtil
 * code. For that reference, we need to have XMLUtil available as part of some
 * jar. Also, we need to have 3 jars from IBM runtime directory in classpath
 * 
 * @author sgunda
 * 
 */
public class TestSOAPMsgDispatcher {

	private static YFCLogCategory logger = NWCGROSSLogger
			.instance(TestSOAPMsgDispatcher.class);

	// Certificate Path
	public Dispatch<SOAPMessage> getDispatchFromNamespace(String strServiceName) {
		String strNamespace = "http://nwcg.gov/services/ross/catalog/1.1";
		String strService = "CatalogService";
		String strPort = "CatalogPort";
		String strServiceGroup = "CATALOG";
		String strServiceGroupAddress = "https://esb-extint.lw-lmco.com:15558/soap/ross";

		logger.verbose("TestSOAPMsgDispatcher::getDispatchFromNamespace "
				+ "properties strNamespace=" + strNamespace + " strService "
				+ strService + " strPort " + strPort + " strServiceGroup "
				+ strServiceGroup + " strServiceGroupAddress "
				+ strServiceGroupAddress);

		QName serviceName = new QName(strNamespace, strService);
		QName portName = new QName(strNamespace, strPort);
		Service service = Service.create(serviceName);
		service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING,
				strServiceGroupAddress);

		Dispatch<SOAPMessage> dispatch = service.createDispatch(portName,
				SOAPMessage.class, Service.Mode.MESSAGE);

		logger.verbose("TestSOAPMsgDispatcher::getDispatchFromNamespace dispatch created");
		try {
			Map<String, Object> rc = dispatch.getRequestContext();
			setOutboundContext(rc);
			logger.verbose("TestSOAPMsgDispatcher::getDispatchFromNamespace setOutboundContext set");
		} catch (Exception e) {
			logger.error("!!!!! TestSOAPMsgDispatcher::cant associate context"
							+ e.getMessage(), e);
		}
		return dispatch;
	}

	protected void setOutboundContext(Map<String, Object> rc) {
		rc.put(BindingProvider.USERNAME_PROPERTY, "icbs_dev");
		rc.put(BindingProvider.PASSWORD_PROPERTY, "password1!");
	}

	public Document getDocumentFromFile(String fileName) {
		Document tmpDoc = null;
		StringBuffer content = new StringBuffer();
		File f = new File(fileName);
		try {
			BufferedReader input = new BufferedReader(new FileReader(f));
			String line = null;
			while ((line = input.readLine()) != null) {
				content.append(line);
				content.append(System.getProperty("line.separator"));
			}
			tmpDoc = XMLUtil.getDocument(content.toString());
		} catch (Exception e) {
			logger.error("!!!!! Exception e : " + e.getMessage(), e);
		}
		return tmpDoc;
	}

	public String getStringFromFile(String fileName) {
		StringBuffer content = new StringBuffer();
		File f = new File(fileName);
		try {
			BufferedReader input = new BufferedReader(new FileReader(f));
			String line = null;
			while ((line = input.readLine()) != null) {
				content.append(line);
				content.append(System.getProperty("line.separator"));
			}
		} catch (Exception e) {
			logger.error("!!!!! Exception e : " + e.getMessage(), e);
		}
		return content.toString();
	}

	public static void main(String[] args) {
		try {
			TestSOAPMsgDispatcher msgDispatcher = new TestSOAPMsgDispatcher();
			MessageFactory mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
			// Create SOAPMessage from XML file
			SOAPMessage soapMessageRequest = mf.createMessage();
			SOAPPart soapPart = soapMessageRequest.getSOAPPart();
			// Load the SOAP text into a stream source
			byte[] buffer = msgDispatcher.getStringFromFile("soapMsgReq.xml").getBytes();
			ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
			StreamSource source = new StreamSource(stream);
			soapPart.setContent(source);
			soapMessageRequest.writeTo(System.out);
			// Set Certificate Path
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

			// Set request attributes like userid, password
			String strServiceName = "CreateCatalogItemReq";
			Dispatch<SOAPMessage> dispatch = msgDispatcher
					.getDispatchFromNamespace(strServiceName);

			// dispatch.invoke
			SOAPMessage soapMessageResponse = dispatch
					.invoke(soapMessageRequest);
			logger.verbose("sendMessageRequest got response "
					+ soapMessageResponse);
			logger.verbose("Response Start ===========================  ");
			soapMessageResponse.writeTo(System.out);
			logger.verbose("Response End ===========================  ");
		} catch (SOAPException se) {
			logger.error("!!!!! SOAPException : " + se.getMessage());
			se.getStackTrace();
		} catch (IOException ioe) {
			logger.error("!!!!! IOException : " + ioe.getMessage());
			ioe.getStackTrace();
		}
	}
}
