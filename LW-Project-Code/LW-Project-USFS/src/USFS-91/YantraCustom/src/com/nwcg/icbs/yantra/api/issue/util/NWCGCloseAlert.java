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

package com.nwcg.icbs.yantra.api.issue.util;

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
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public final class NWCGCloseAlert {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGCloseAlert.class);

	/**
	 * This method will get the open alerts for the order
	 * 
	 * @param env
	 * @param orderHdrKey
	 * @return
	 */
	public static Document getOpenAlertsForIssue(YFSEnvironment env, String key, String exceptionType) {
		logger.verbose("@@@@@ Entering NWCGCloseAlert::getOpenAlertsForIssue @@@@@");
		Document docOpenAlerts = null;
		StringBuffer sb = new StringBuffer("<Inbox ActiveFlag=\"Y\" EnterpriseKey=\"NWCG\" Status=\"OPEN\" ");
		sb.append("ExceptionType=\"").append(exceptionType).append("\">");
		sb.append("<InboxReferencesList><InboxReferences ValueQryType=\"LIKE\" Value=\"");
		sb.append(key);
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
			docOpenAlerts = CommonUtilities.invokeAPI(env, docOPTmpl, NWCGConstants.API_GET_EXCEPTION_LIST, docAlertsIP);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			return null;
		}
		logger.verbose("@@@@@ Exiting NWCGCloseAlert::getOpenAlertsForIssue @@@@@");
		return docOpenAlerts;
	}

	/**
	 * 
	 * @param env
	 * @param inboxKey
	 * @param queueKey
	 * @param reasonForClose
	 */
	public static void resolveAlert(YFSEnvironment env, String inboxKey, String queueKey, String reasonForClose) {
		logger.verbose("@@@@@ Entering NWCGCloseAlert::resolveAlert @@@@@");
		if (reasonForClose == null) {
			reasonForClose = "Automatically resolved by ICBSR";
		}
		try {
			Document docChgException = XMLUtil.createDocument(NWCGConstants.INBOX);
			Element elmChgExc = docChgException.getDocumentElement();
			elmChgExc.setAttribute(NWCGConstants.INBOX_KEY, inboxKey);
			elmChgExc.setAttribute(NWCGConstants.ENTERPRISE_KEY, NWCGConstants.ENTERPRISE_CODE);
			elmChgExc.setAttribute(NWCGConstants.QUEUE_KEY, queueKey);
			if (!StringUtil.isEmpty(reasonForClose)) {
				Element inboxReferencesList = docChgException.createElement(NWCGConstants.INBOX_REFERENCES_LIST);
				Element inboxReferences = docChgException.createElement(NWCGConstants.INBOX_REFERENCES);
				inboxReferences.setAttribute(NWCGConstants.NAME_ATTR, "Comment10");
				inboxReferences.setAttribute(NWCGConstants.VALUE_ATTR, reasonForClose);
				inboxReferences.setAttribute(NWCGConstants.REFERENCE_TYPE, NWCGConstants.REFERENCE_TYPE_COMMENT);
				inboxReferences.setAttribute(NWCGConstants.INBOX_KEY, inboxKey);
				inboxReferencesList.appendChild(inboxReferences);
				elmChgExc.appendChild(inboxReferencesList);
			}
			CommonUtilities.invokeAPI(env, NWCGConstants.API_CHANGE_EXCEPTION, docChgException);
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException :: " + pce);
		} catch (TransformerException te) {
			logger.error("!!!!! Caught TransformerException :: " + te);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
		}
		// Now call resolveException to close the alert
		try {
			Document docResExcIP = XMLUtil.createDocument("ResolutionDetails");
			Element elmInbox = docResExcIP.createElement(NWCGConstants.INBOX);
			Element elmDocResExcIP = docResExcIP.getDocumentElement();
			elmDocResExcIP.setAttribute(NWCGConstants.AUTO_RESOLVED_FLAG, NWCGConstants.YES);
			elmDocResExcIP.setAttribute(NWCGConstants.RESOLVED_BY, env.getUserId());
			elmInbox.setAttribute(NWCGConstants.INBOX_KEY, inboxKey);
			elmDocResExcIP.appendChild(elmInbox);
			CommonUtilities.invokeAPI(env, NWCGConstants.API_RESOLVE_EXCEPTION, docResExcIP);
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException :: " + pce);
		} catch (TransformerException te) {
			logger.error("!!!!! Caught TransformerException :: " + te);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
		}
		logger.verbose("@@@@@ Exiting NWCGCloseAlert::resolveAlert @@@@@");
		return;
	}

	/**
	 * 
	 * @param env
	 * @param key
	 * @param exceptionType
	 * @param reasonForClose
	 */
	public static void closeAlert(YFSEnvironment env, String key, String exceptionType, String reasonForClose) {
		logger.verbose("@@@@@ Entering NWCGCloseAlert::closeAlert @@@@@");
		Document docAlerts = getOpenAlertsForIssue(env, key, exceptionType);
		if (docAlerts == null) {
			return;
		} else {
			NodeList nlAlertList = docAlerts.getElementsByTagName("Inbox");
			if (nlAlertList == null || nlAlertList.getLength() < 1) {
				return;
			} else {
				// There should be only one alert
				Element elmInbox = (Element) nlAlertList.item(0);
				String inboxKey = elmInbox.getAttribute(NWCGConstants.INBOX_KEY);
				if (inboxKey == null || inboxKey.trim().length() < 2) {
					return;
				} else {
					String queueKey = elmInbox.getAttribute(NWCGConstants.QUEUE_KEY);
					resolveAlert(env, inboxKey, queueKey, reasonForClose);
				}
			}
		}
		logger.verbose("@@@@@ Exiting NWCGCloseAlert::closeAlert @@@@@");
		return;
	}
}