package com.fanatics.sterling.agent;

/**
 * This class is a custom agent. Picks up all "initial" DB records from the EXTN_FAN_EMAIL table,
 * separates each one of the records and calls a service that sends the jobs in a queue. After a
 * successful attempt, the agent invokes a service that changes the status of the record from Initial to Complete.   
 * 
 * @(#) FanSendEmailAgent.java    
 * Created on   May 26, 2016
 *              
 *
 * Package Declaration: 
 * File Name:       FanSendEmailAgent.java
 * Package Name:    com.fanatics.sterling.agent;
 * Project name:    Fanatics
 * Type Declaration:    
 * Class Name:      FanSendEmailAgent
 * 
 * @author KNtagkas
 * @version 1.0
 * @history none
 *     
 * 
 * (C) Copyright 2016-2017 by owner.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information
 * of the owner. ("Confidential Information").
 * Redistribution of the source code or binary form is not permitted
 * without prior authorisation from the owner.
 *
 */

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fanatics.sterling.constants.EmailComunicationConstants;
import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class FanSendEmailAgent extends YCPBaseAgent {

	private final static YFCLogCategory log = YFCLogCategory.instance(YFCLogCategory.class);

	@Override
	public List<Document> getJobs(YFSEnvironment env, Document criteria, Document lastMessageCreated) {

		log.beginTimer("FanSendEmailAgent -> getJobs - start ");

		List<Document> jobs = null;
		
		try {
			
			Document inputDoc = SCXmlUtil.createFromString(EmailComunicationConstants.XML_EMAIL_LIST_INPUT);

			// Set to fetch net set of data
			if (!YFCObject.isVoid(lastMessageCreated)) {
				String slastOrderHeaderKey = lastMessageCreated.getDocumentElement()
						.getAttribute(EmailComunicationConstants.ATT_ORDER_HEADER_KEY);
				if (!YFCObject.isVoid(slastOrderHeaderKey)) {
					inputDoc.getDocumentElement().setAttribute(EmailComunicationConstants.ATT_ORDER_HEADER_KEY,
							slastOrderHeaderKey);
				}
			}

			Element eleMessageXML  = criteria.getDocumentElement();
			String sMaximumRecords = eleMessageXML.getAttribute(EmailComunicationConstants.NUMBER_OF_RECORDS);

			if (!YFCObject.isVoid(sMaximumRecords)) {
				inputDoc.getDocumentElement().setAttribute(EmailComunicationConstants.ATT_MAXIMUN_RECORDS,  sMaximumRecords );
			}

			Document jobList = CommonUtil.invokeService(env, EmailComunicationConstants.GET_LIST_EMAIL_ENTRY_SERVICE,
					inputDoc);

			if (jobList != null && jobList.hasChildNodes()) {
				jobs = slpitJobs(jobList);
			} else {
				log.warn("FanSendEmailAgent.getJobs -> Job List Either null or empty, No records to be processed!");
			}

		} catch (Exception e) {
			log.error("FanSendEmailAgent -> Exception while getting jobs " + e);
		} finally {
			log.endTimer("FanSendEmailAgent -> getJobs - End ");
		}

		return jobs;
	}

	private List<Document> slpitJobs(Document jobList) {

		log.verbose("FanSendEmailAgent -> Splitting Jobs -> Begin ");

		NodeList jobNodes = SCXmlUtil.getXpathNodes(jobList.getDocumentElement(),
				EmailComunicationConstants.XPATH_FAN_EMAIL);
		ArrayList<Document> jobs = new ArrayList<Document>();

		if (jobNodes != null && jobNodes.getLength() > 0) {
			for (int j = 0; j < jobNodes.getLength(); j++) {

				if (jobNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
					Node jobLine = jobNodes.item(j);

					Document job = SCXmlUtil.createDocument();
					Node jobLineImported = job.importNode(jobLine, true);
					job.appendChild(jobLineImported);
					jobs.add(job);

				}
			}
			log.verbose("jobs imported successfully ");
		}

		return jobs;
	}

	@Override
	@SuppressWarnings("unused")
	public void executeJob(YFSEnvironment env, Document job) throws Exception {

		log.verbose("FanSendEmailAgent -> executeJob : begin");
		log.verbose("FanSendEmailAgent job -> " + XMLUtil.getXMLString(job));

		boolean isSuccess 	  = false;
		String orderHeaderKey = job.getDocumentElement().getAttribute(EmailComunicationConstants.ATT_ORDER_HEADER_KEY);
		String emailKey 	  = job.getDocumentElement().getAttribute(EmailComunicationConstants.ATT_EMAIL_KEY);
		String msgXMLStr	  = job.getDocumentElement().getAttribute("MsgXml");
		Document msgXMLDoc    = XMLUtil.getDocument(msgXMLStr);
		
		log.verbose("FanSendEmailAgent msgXMLDoc -> " + XMLUtil.getXMLString(msgXMLDoc));

		try {

			log.verbose(
					"FanSendEmailAgent -> invoking FanSendEmailTrigger Service for OrderHeaderKey " + orderHeaderKey);
			Document jobList = CommonUtil.invokeService(env, EmailComunicationConstants.FAN_SEND_EMAIL_TRIGGER_SERVICE,
					msgXMLDoc);
			isSuccess = true;

		} catch (Exception e) {
			isSuccess = false;
			log.error("FanSendEmailAgent -> Method executejobs ERROR: " + e);
		}

		if (isSuccess) {
			changeEmailStatus(env, orderHeaderKey, emailKey, EmailComunicationConstants.EMAIL_STATUS_COMPLETE);

		} else {
			log.warn("FanSendEmailAgent -> Changing E-Mail status , No DB entries to change!");
		}

		log.verbose("FanSendEmailAgent -> executeJob : end");
	}

	@SuppressWarnings("unused")
	private void changeEmailStatus(YFSEnvironment env, String orderHeaderKey, String emailKey,
			String emailStatusComplete) throws Exception {

		log.verbose("FanSendEmailAgent -> changeEmailStatus : begin");
		
		try {

			Document inputDoc 	 = SCXmlUtil.createDocument(EmailComunicationConstants.EL_EXTN_FAN_EMAIL);
			Element eleRootInput = inputDoc.getDocumentElement();

			eleRootInput.setAttribute(EmailComunicationConstants.ATT_ORDER_HEADER_KEY, orderHeaderKey);
			eleRootInput.setAttribute(EmailComunicationConstants.ATT_EMAIL_KEY, emailKey);
			eleRootInput.setAttribute(EmailComunicationConstants.ATT_EMAIL_STATUS, emailStatusComplete);

			log.verbose(
					"FanSendEmailAgent -> invoking FanChangeEmailEntry Service for OrderHeaderKey " + orderHeaderKey);
			log.verbose("inputXML -> " + XMLUtil.getXMLString(inputDoc));
			Document jobList = CommonUtil.invokeService(env, EmailComunicationConstants.FAN_CHANGE_EMAIL_ENTRY_SERVICE,
					inputDoc);

		} catch (Exception e) {

			log.error("FanSendEmailAgent -> Method executejobs" + e);
		}
		log.verbose("FanSendEmailAgent -> changeEmailStatus : end");
	}

}
