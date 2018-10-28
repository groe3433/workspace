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

package com.nwcg.icbs.yantra.pingService;

import java.util.Calendar;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.nwcg.icbs.yantra.ajax.NWCGAjaxAPI;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.ob.msg.NWCGOutboundSyncMessage;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServiceUtils;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

/**
 * 
 * @author Oxford Consulting Group
 * @version 1.7
 * @date October 18, 2013
 */
public class NWCGTestPingService implements YIFCustomApi, NWCGAjaxAPI {

	private static YFCLogCategory cat = YFCLogCategory.instance("com.nwcg.icbs.yantra");	
	
	/**
	 * 
	 * @param arg0
	 * @throws Exception
	 */
	public void setProperties(Properties arg0) throws Exception {
	}	
	
	/**
	 * 
	 * @param env
	 * @param msg
	 * @return
	 * @throws NWCGException
	 */
	public Document process(YFSEnvironment env, Document msg) throws NWCGException {
		
		Document docReq = null;
		try {
			docReq = XMLUtil.getDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		if (msg != null)
			msg.getDocumentElement().setAttribute("Result", "FAILED");

		// set up the attributes required by A&A framework
		Element elem = docReq.createElementNS("http://nwcg.gov/services/ross/common_types/1.1", "common:ServicePingReq");
		elem.setAttribute("NWCGUSERID", NWCGAAConstants.ENV_USER_ID);
		elem.setAttribute(NWCGAAConstants.OPERATION_TYPE, NWCGAAConstants.OPERATION_SYNC);
		elem.setAttribute(NWCGAAConstants.MDTO_NAMESPACE, NWCGAAConstants.COMMON_TYPES_NAMESPACE);
		elem.setAttribute(NWCGAAConstants.MDTO_MSGNAME, "ServicePingReq");
		docReq.appendChild(elem);
		// instantiate the synchronous A&A class
		NWCGOutboundSyncMessage ana = new NWCGOutboundSyncMessage();
		Document respDoc = null;
		try {
			// call process method to send message to external system
			respDoc = ana.process(env, docReq);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (respDoc != null) {
			try {
				// setup the attributes to be read by client
				String strTimeStamp = respDoc.getDocumentElement().getElementsByTagName("Timestamp").item(0).getTextContent();
				msg.getDocumentElement().setAttribute("Result", "SUCCESS");
				msg.getDocumentElement().setAttribute("TimeStamp", "" + strTimeStamp);
			} catch (Exception e) {
				msg.getDocumentElement().setAttribute("Result", "FAILURE");
			}
		}
		
		return msg;
	}

	/**
	 * 
	 * @param env
	 * @param inDoc
	 * @return
	 */
	public Document processAjaxRequest(YFSEnvironment env, Document inDoc) {
		
		Document doc = null;
		try {
			//Create Input Document
			doc = XMLUtil.getDocument();
			Element el_PingReq = doc.createElement("ServicePingReq");
			doc.appendChild(el_PingReq);
			doc = process(env, doc);
			
			Element el_ServicePingReq = doc.getDocumentElement();
			String sResult = (String) (el_ServicePingReq.getAttribute("Result"));
			if (sResult != "" && sResult != null) {
				
				if (!sResult.equals("SUCCESS")) {
					//sendEmail();
				}
			}
			return doc;
		} catch (Exception e) {
			System.out.println("Exception " + e);
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 
	 * @throws MessagingException
	 */
	public static void sendEmail() throws MessagingException {
		
		boolean debug = false;
		Properties propMail = new Properties();
		String smtpHost = "mail.nwcg.gov";
		if (smtpHost == null || smtpHost.trim().length() < 2) {
			System.out.println("!!!!! SMTP Host information is not provided in the configuration file. " + "Populate SMTP_HOST in the config file from /etc/sendmail.cf");
		}
		propMail.put("mail.smtp.host", smtpHost);
		Session sessionMail = Session.getDefaultInstance(propMail, null);
		sessionMail.setDebug(debug);
		Message msgMail = new MimeMessage(sessionMail);
		String fromEmail = "admin@icbsr.gov";
		if (fromEmail == null || fromEmail.trim().length() < 2) {
			fromEmail = "admin@icbsr.com";
		}
		InternetAddress fromEmailAddr = new InternetAddress(fromEmail);
		msgMail.setFrom(fromEmailAddr);
		String toEmailList = "conor.barr@oxford-consulting.com";
		if (toEmailList == null || toEmailList.trim().length() < 2) {
			System.out.println("!!!!! To email addresses are not mentioned in the agent criteria/config file. " + "List the email addresses to whom this alerts needs to go for string TO_EMAIL " + "separated by a comma delimiter in config file");
		}
		String toEmails[] = toEmailList.split(",");
		InternetAddress[] toEmailAddresses = new InternetAddress[toEmails.length];
		for (int i = 0; i < toEmails.length; i++) {
			toEmailAddresses[i] = new InternetAddress(toEmails[i]);
		}
		msgMail.setRecipients(Message.RecipientType.TO, toEmailAddresses);
		msgMail.setSubject("ROSS Ping Failed");
		String content = "FAILURE: " + CommonUtilities.formatDate("yyyy-MM-dd HH:mm:ss Z", Calendar.getInstance().getTime());
		msgMail.setContent(content, "text/plain");
		Transport.send(msgMail);
	}
}