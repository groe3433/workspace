package com.nwcg.icbs.yantra.agents;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.management.ObjectName;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.exception.AdminException;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.ob.msg.NWCGOutboundSyncMessage;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGRossPingAgent extends YCPBaseAgent {

	private static Logger logger = Logger.getLogger();

	private Hashtable<String, String> htAgentCriteriaParam = new Hashtable<String, String>();

	/**
	 * public default constructor
	 */
	public NWCGRossPingAgent() {
		super();
		System.out.println("@@@@@ In NWCGRossPingAgent :: NWCGRossPing agent started!");
	}

	public void executeJob(YFSEnvironment env, Document docIP) throws Exception {
		System.out.println("@@@@@ Entering NWCGRossPingAgent::executeJob @@@@@");
		System.out.println("NWCGRossPingAgent::executeJobs, " + "This should not be called as we are returning NULL from getJobs");
		System.out.println("@@@@@ Exiting NWCGRossPingAgent::executeJob @@@@@");
	}

	public List getJobs(YFSEnvironment env, Document inDoc) throws Exception {
		System.out.println("@@@@@ Entering NWCGRossPingAgent::getJobs @@@@@");
		getCriteriaParameters(inDoc);
		Document docServicePingReq = XMLUtil.getDocument();
		Element el_ServicePingReq = docServicePingReq.createElement("ServicePingReq");
		docServicePingReq.appendChild(el_ServicePingReq);
		processAjaxRequest(env, docServicePingReq);
		System.out.println("@@@@@ Exiting NWCGRossPingAgent::getJobs @@@@@");
		return null;
	}

	public Document process(YFSEnvironment env, Document msg) throws NWCGException {
		System.out.println("@@@@@ Entering NWCGRossPingAgent::process @@@@@");
		System.out.println("@@@@@ getJobs :: process " + XMLUtil.getXMLString(msg));
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
		System.out.println("@@@@@ getJobs :: process " + XMLUtil.getXMLString(msg));
		System.out.println("@@@@@ Exiting NWCGRossPingAgent::process @@@@@");
		return msg;
	}

	public Document processAjaxRequest(YFSEnvironment env, Document doc) {
		System.out.println("@@@@@ Entering NWCGRossPingAgent::processAjaxRequest @@@@@");
		try {
			doc = process(env, doc);
			System.out.println("@@@@@ getJobs :: processAjaxRequest " + XMLUtil.getXMLString(doc));
			Element el_ServicePingReq = doc.getDocumentElement();
			String sResult = (String) (el_ServicePingReq.getAttribute("Result"));
			if (sResult != "" && sResult != null) {
				System.out.println("@@@@@ NWCGRossPingAgent:: processAjaxRequest() ROSS Ping Result: " + sResult);
				if (!sResult.equals("SUCCESS")) {
					System.out.println("@@@@@ ROSS Ping: Sending Email...");
					sendEmail();
				}
			}

		} catch (Exception e) {
			System.out.println("!!!!! Caught General Exception " + e);
			e.printStackTrace();
		}
		System.out.println("@@@@@ Exiting NWCGRossPingAgent::processAjaxRequest @@@@@");
		return doc;
	}

	public void sendEmail() throws MessagingException {
		System.out.println("@@@@@ Entering NWCGRossPingAgent::sendEmail @@@@@");
		boolean debug = false;
		Properties propMail = new Properties();
		String smtpHost = htAgentCriteriaParam.get("SMTP_HOST");

		if (smtpHost == null || smtpHost.trim().length() < 2) {
			System.out.println("SMTP Host information is not provided in the configuration file. " + "Populate SMTP_HOST in the config file from /etc/sendmail.cf");
		}
		propMail.put("mail.smtp.host", smtpHost);
		Session sessionMail = Session.getDefaultInstance(propMail, null);
		sessionMail.setDebug(debug);
		Message msgMail = new MimeMessage(sessionMail);
		String fromEmail = htAgentCriteriaParam.get("FROM_EMAIL");
		if (fromEmail == null || fromEmail.trim().length() < 2) {
			fromEmail = "admin@icbsr.com";
		}
		InternetAddress fromEmailAddr = new InternetAddress(fromEmail);
		msgMail.setFrom(fromEmailAddr);
		String toEmailList = htAgentCriteriaParam.get("TO_EMAIL");
		if (toEmailList == null || toEmailList.trim().length() < 2) {
			System.out.println("ToEmailList is empty");
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
		System.out.println("@@@@@ Exiting NWCGRossPingAgent::sendEmail @@@@@");
	}

	/**
	 * Agent Criteria Details
	 * @param docIP
	 */
	private void getCriteriaParameters(Document docIP) {
		System.out.println("@@@@@ Entering NWCGRossPingAgent::getCriteriaParameters @@@@@");
		System.out.println("@@@@@ docIP : " + XMLUtil.getXMLString(docIP));
		Element elmDocMonitor = docIP.getDocumentElement();
		NamedNodeMap nnm = elmDocMonitor.getAttributes();
		if (nnm == null) {
			System.out.println("!!!!! NWCGRossPingAgent::getCriteriaParameters, " + "Attributes are not set as part of input document");
		}
		for (int i = 0; i < nnm.getLength(); i++) {
			Node tempNode = nnm.item(i);
			String nodeName = tempNode.getNodeName();
			if (tempNode != null && nodeName != null) {
				htAgentCriteriaParam.put(nodeName, tempNode.getNodeValue());
			}
		}
		System.out.println("@@@@@ Exiting NWCGRossPingAgent::getCriteriaParameters @@@@@");
	}
}