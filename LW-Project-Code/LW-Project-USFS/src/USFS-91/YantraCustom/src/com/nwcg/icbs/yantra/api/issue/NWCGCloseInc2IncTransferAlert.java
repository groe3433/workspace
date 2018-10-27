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

package com.nwcg.icbs.yantra.api.issue;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGCloseInc2IncTransferAlert implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGRemoveTrackableItems.class);

	public void setProperties(Properties arg0) throws Exception {
	}

	/**
	 * This method will be called on ON_SUCCESS event of confirmOrder for incident to incident transfer issue. 
	 * This is called after posting billing transaction
	 * 1. This will get the open alert for Resource Reassignment using OrderHeaderKey
	 * 2. Closes this alert
	 * 
	 * ToDo: Check if we can make the class generic, so that future services can use the generic closure
	 * of alert
	 * @param env
	 * @param docIP
	 * @return
	 * @throws NWCGException
	 */
	public Document closeInc2IncTransferAlert(YFSEnvironment env, Document docIP) throws Exception{
		logger.verbose("@@@@@ Entering NWCGCloseInc2IncTransferAlert::closeInc2IncTransferAlert @@@@@");
		logger.verbose("@@@@@ Input XML to close RR alert : " + XMLUtil.extractStringFromDocument(docIP));
		
		String orderHdrKey = docIP.getDocumentElement().getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		logger.verbose("NWCGCloseInc2IncTransferAlert::closeInc2IncTransferAlert, Closing Alert for " + orderHdrKey);
		Document docAlerts = getOpenAlertsForIssue(env, orderHdrKey);
		if (docAlerts == null){
			logger.verbose("@@@@@ There are no open alerts for " + orderHdrKey);
			logger.verbose("@@@@@ Exiting NWCGCloseInc2IncTransferAlert::closeInc2IncTransferAlert (1) @@@@@");
			return docIP;
		} else {
			NodeList nlAlertList = docAlerts.getElementsByTagName("Inbox");
			if (nlAlertList == null || nlAlertList.getLength() < 1){
				logger.verbose("@@@@@ Zero open alerts for " + orderHdrKey);
				logger.verbose("@@@@@ Exiting NWCGCloseInc2IncTransferAlert::closeInc2IncTransferAlert (2) @@@@@");
				return docIP;
			}
			else {
				logger.verbose("@@@@@ No of alerts : " + nlAlertList.getLength());
				// There should be only one alert
				Element elmInbox = (Element) nlAlertList.item(0);
				String inboxKey = elmInbox.getAttribute(NWCGConstants.INBOX_KEY);
				if (inboxKey == null || inboxKey.trim().length() < 2){
					logger.verbose("@@@@@ Inbox key is null");
					logger.verbose("@@@@@ Exiting NWCGCloseInc2IncTransferAlert::closeInc2IncTransferAlert (3) @@@@@");
					return docIP;
				} else {
					String queueKey = elmInbox.getAttribute(NWCGConstants.QUEUE_KEY);
					resolveAlert(env, inboxKey, queueKey);
				}
			}
		}
		logger.verbose("@@@@@ Exiting NWCGCloseInc2IncTransferAlert::closeInc2IncTransferAlert @@@@@");
		return null;
	}
	
	/**
	 * This method will get the open alerts for the order
	 * @param env
	 * @param orderHdrKey
	 * @return
	 */
	private Document getOpenAlertsForIssue(YFSEnvironment env, String orderHdrKey){
		logger.verbose("@@@@@ Entering NWCGCloseInc2IncTransferAlert::getOpenAlertsForIssue @@@@@");
		Document docOpenAlerts = null;
		StringBuffer sb = new StringBuffer("<Inbox ActiveFlag=\"Y\" EnterpriseKey=\"NWCG\" Status=\"OPEN\" ");
		sb.append("ExceptionType=\"ResourceRequestReassignNotification\">");
		sb.append("<InboxReferencesList><InboxReferences ValueQryType=\"LIKE\" Value=\"");
		sb.append(orderHdrKey);
		sb.append("\"/>");
		sb.append("</InboxReferencesList>");
		sb.append("</Inbox>");
		
		StringBuffer sbOpTemplate = new StringBuffer("<InboxList><Inbox InboxKey=\"\" QueueKey=\"\"/>");
		sbOpTemplate.append("</InboxList>");
		
		try {
			String ipStr = sb.toString();
			String opTmpl = sbOpTemplate.toString();
			
			Document docAlertsIP = XMLUtil.getDocument(ipStr);
			Document docOPTmpl = XMLUtil.getDocument(opTmpl);
			
			docOpenAlerts = 
				CommonUtilities.invokeAPI(env, docOPTmpl, NWCGConstants.API_GET_EXCEPTION_LIST, docAlertsIP);

		} catch (Exception e) {
			logger.error("Caught General Exception thrown while calling getExceptionList API : " + e);		
			return null;
		}		
		logger.verbose("@@@@@ Exiting NWCGCloseInc2IncTransferAlert::getOpenAlertsForIssue @@@@@");
		return docOpenAlerts;
	}
	
	private void resolveAlert(YFSEnvironment env, String inboxKey, String queueKey){
		logger.verbose("@@@@@ Entering NWCGCloseInc2IncTransferAlert::resolveAlert @@@@@");
		String reason = "Automatically resolved by ICBSR when Inc2Inc transfer issue was successfully Confirmed";
		try {
			Document docChgException = XMLUtil.createDocument(NWCGConstants.INBOX);
			Element elmChgExc = docChgException.getDocumentElement();
			
			elmChgExc.setAttribute(NWCGConstants.INBOX_KEY, inboxKey);
			elmChgExc.setAttribute(NWCGConstants.ENTERPRISE_KEY, NWCGConstants.ENTERPRISE_CODE);
			elmChgExc.setAttribute(NWCGConstants.QUEUE_KEY, queueKey);
			if (!StringUtil.isEmpty(reason)) {
				Element inboxReferencesList = docChgException.createElement(NWCGConstants.INBOX_REFERENCES_LIST);
				Element inboxReferences = docChgException.createElement(NWCGConstants.INBOX_REFERENCES);
				
				inboxReferences.setAttribute(NWCGConstants.NAME_ATTR, "Comment10");
				inboxReferences.setAttribute(NWCGConstants.VALUE_ATTR, reason);
				inboxReferences.setAttribute(NWCGConstants.REFERENCE_TYPE, NWCGConstants.REFERENCE_TYPE_COMMENT);
				inboxReferences.setAttribute(NWCGConstants.INBOX_KEY, inboxKey);
				inboxReferencesList.appendChild(inboxReferences);
				elmChgExc.appendChild(inboxReferencesList);				
			}
			logger.verbose("@@@@@ Input XML to changeException: " + XMLUtil.extractStringFromDocument(docChgException));
			CommonUtilities.invokeAPI(env, NWCGConstants.API_CHANGE_EXCEPTION, docChgException);
				
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException : " + pce);
		} catch (TransformerException te) {
			logger.error("!!!!! Caught TransformerException : " + te);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);
		}
		
		//Now call resolveException to close the alert
		try {
			Document docResExcIP = XMLUtil.createDocument("ResolutionDetails");
			Element elmInbox = docResExcIP.createElement(NWCGConstants.INBOX);
			
			Element elmDocResExcIP = docResExcIP.getDocumentElement();
			elmDocResExcIP.setAttribute(NWCGConstants.AUTO_RESOLVED_FLAG, NWCGConstants.YES);
			elmDocResExcIP.setAttribute(NWCGConstants.RESOLVED_BY, env.getUserId());
			elmInbox.setAttribute(NWCGConstants.INBOX_KEY, inboxKey);
			elmDocResExcIP.appendChild(elmInbox);

			logger.verbose("@@@@@ Input XML to resolve exception : " + XMLUtil.extractStringFromDocument(docResExcIP));
			CommonUtilities.invokeAPI(env, NWCGConstants.API_RESOLVE_EXCEPTION, docResExcIP);
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException : " + pce);
		} catch (TransformerException te) {
			logger.error("!!!!! Caught TransformerException : " + te);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);
		}		
		logger.verbose("@@@@@ Exiting NWCGCloseInc2IncTransferAlert::resolveAlert @@@@@");
		return;
	}
}