package com.nwcg.icbs.yantra.api.incident;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class closses alerts created for ROSS generated issues, when all the
 * orderlines of that issue are either forwarded or backordered
 *
 * @author SMCFS91
 */
public class NWCGCloseAlertForBackOrderForwardOrder implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGCloseAlertForBackOrderForwardOrder.class);

	public void setProperties(Properties arg0) throws Exception {
	}

	public static Document closeAlertforIssue(YFSEnvironment env, Document docOut) throws Exception {
		logger.verbose("@@@@@ Entering NWCGCloseAlertForBackOrderForwardOrder::closeAlertforIssue @@@@@");
		Element eleOutRoot = docOut.getDocumentElement();
		String strOrderHeaderKey = eleOutRoot.getAttribute("OrderHeaderKey");
		if (!eleOutRoot.hasAttribute("MaxOrderStatus")) {
			Document docExceptionList = getExceptionList(env, strOrderHeaderKey);
			// Get the inboxKey for the alert associated to this issue/order
			String inboxKey = getInboxKeyFromExceptionListDoc(docExceptionList);
			resolveAlert(env, inboxKey);
		}
		logger.verbose("@@@@@ docOut :: " + XMLUtil.getXMLString(docOut));
		logger.verbose("@@@@@ Exiting NWCGCloseAlertForBackOrderForwardOrder::closeAlertforIssue @@@@@");
		return docOut;
	}

	private static Document getOrderDetailTemplate() throws Exception {
		logger.verbose("@@@@@ Entering NWCGCloseAlertForBackOrderForwardOrder::getOrderDetailTemplate @@@@@");
		Document docTemplate = XMLUtil.createDocument("Order");
		Element eleRoot = docTemplate.getDocumentElement();
		eleRoot.setAttribute("DraftOrderFlag", "");
		eleRoot.setAttribute("MaxOrderStatus", "");
		eleRoot.setAttribute("OrderHeaderKey", "");
		Element eleExtn = docTemplate.createElement("Extn");
		eleExtn.setAttribute("ExtnSystemOfOrigin", "");
		eleRoot.appendChild(eleExtn);
		logger.verbose("@@@@@ Exiting NWCGCloseAlertForBackOrderForwardOrder::getOrderDetailTemplate @@@@@");
		return docTemplate;
	}

	private static Document getExceptionList(YFSEnvironment env,
			String strOrderHeaderKey) throws Exception {
		logger.verbose("@@@@@ Entering NWCGCloseAlertForBackOrderForwardOrder::getExceptionList @@@@@");
		Document getExceptionListDoc = null;
		StringBuffer sb = new StringBuffer(
				"<Inbox ActiveFlag=\"Y\" EnterpriseKey=\"NWCG\" ");
		sb.append("ExceptionType=\"PlaceResourceRequestExternalReq\">");
		sb.append("<InboxReferencesList><InboxReferences ValueQryType=\"LIKE\" Value=\"");
		sb.append(strOrderHeaderKey);
		sb.append("\"/>");
		sb.append("</InboxReferencesList>");
		sb.append("<OrderBy><Attribute Name=\"InboxKey\"/></OrderBy>");
		sb.append("</Inbox>");
		StringBuffer sbOpTemplate = new StringBuffer(
				"<InboxList><Inbox InboxKey=\"\">");
		sbOpTemplate
				.append("<InboxReferencesList><InboxReferences ReferenceType=\"\" Name=\"\" Value=\"\"/>");
		sbOpTemplate.append("</InboxReferencesList></Inbox></InboxList>");
		String inpStr = sb.toString();
		String inpTemplate = sbOpTemplate.toString();
		Document inptDoc = XMLUtil.getDocument(inpStr);
		Document opTemplate = XMLUtil.getDocument(inpTemplate);
		getExceptionListDoc = CommonUtilities.invokeAPI(env, opTemplate,
				NWCGConstants.API_GET_EXCEPTION_LIST, inptDoc);
		logger.verbose("@@@@@ Exiting NWCGCloseAlertForBackOrderForwardOrder::getExceptionList @@@@@");
		return getExceptionListDoc;

	}

	private static String getInboxKeyFromExceptionListDoc(
			Document getExceptionListOpDoc) {
		logger.verbose("@@@@@ Entering NWCGCloseAlertForBackOrderForwardOrder::getInboxKeyFromExceptionListDoc @@@@@");
		String inboxKey = NWCGConstants.EMPTY_STRING;
		Element apiOpDocElm = getExceptionListOpDoc.getDocumentElement();
		NodeList nl = apiOpDocElm.getElementsByTagName(NWCGConstants.INBOX);
		if (nl != null && nl.getLength() == 1) {
			Node inboxNode = nl.item(0);
			if (inboxNode instanceof Element) {
				Element inboxElm = (Element) inboxNode;
				inboxKey = inboxElm.getAttribute(NWCGConstants.INBOX_KEY);
			}
		}
		logger.verbose("@@@@@ Exiting NWCGCloseAlertForBackOrderForwardOrder::getInboxKeyFromExceptionListDoc @@@@@");
		return inboxKey;
	}

	private static void resolveAlert(YFSEnvironment env, String inboxKey) {
		logger.verbose("@@@@@ Entering NWCGCloseAlertForBackOrderForwardOrder::resolveAlert @@@@@");
		String reason = "Automatically resolved by ICBSR when Issue was completely backordered or forwarded";
		String queueKeyToUse = NWCGConstants.EMPTY_STRING;
		// First call changeException to add an inbox reference of type COMMENT denoting why and how and when the alert was auto resolved. Check if inbox key is not null and blank. 
		if (inboxKey != null && !inboxKey.equals("")) {
			try {
				// Defaults to the Incident Successs Queue if not provided in the input.
				queueKeyToUse = getQueueKeyForInboxRecord(env, inboxKey);
				Document changeExceptionIP = XMLUtil
						.createDocument(NWCGConstants.INBOX);
				Element changeExceptionDocelm = changeExceptionIP
						.getDocumentElement();
				changeExceptionDocelm.setAttribute(NWCGConstants.INBOX_KEY,
						inboxKey);
				changeExceptionDocelm.setAttribute(
						NWCGConstants.ENTERPRISE_KEY,
						NWCGConstants.ENTERPRISE_CODE);
				changeExceptionDocelm.setAttribute(NWCGConstants.QUEUE_KEY,
						queueKeyToUse);
				if (!StringUtil.isEmpty(reason)) {
					Element inboxReferencesList = changeExceptionIP
							.createElement(NWCGConstants.INBOX_REFERENCES_LIST);
					Element inboxReferences = changeExceptionIP
							.createElement(NWCGConstants.INBOX_REFERENCES);
					inboxReferences.setAttribute(NWCGConstants.NAME_ATTR,
							"Comment10");
					inboxReferences.setAttribute(NWCGConstants.VALUE_ATTR,
							reason);
					inboxReferences.setAttribute(NWCGConstants.REFERENCE_TYPE,
							NWCGConstants.REFERENCE_TYPE_COMMENT);
					inboxReferences.setAttribute(NWCGConstants.INBOX_KEY,
							inboxKey);
					inboxReferencesList.appendChild(inboxReferences);
					changeExceptionDocelm.appendChild(inboxReferencesList);
				}
				logger.verbose("@@@@@ NWCGCloseIssueAlertOnReleaseSuccess::resolveAlert, Input XML to changeException: " + XMLUtil.extractStringFromDocument(changeExceptionIP));
				CommonUtilities.invokeAPI(env, NWCGConstants.API_CHANGE_EXCEPTION, changeExceptionIP);
			} catch (ParserConfigurationException pce) {
				logger.error("!!!!! NWCGCloseIssueAlertOnReleaseSuccess::resolveAlert, ParserConfigurationException : " + pce.getMessage(), pce);
			} catch (TransformerException te) {
				logger.error("!!!!! NWCGCloseIssueAlertOnReleaseSuccess::resolveAlert, TransformerException : " + te.getMessage(), te);
			} catch (Exception e) {
				logger.error("!!!!! NWCGCloseIssueAlertOnReleaseSuccess::resolveAlert, Exception : " + e.getMessage(), e);
			}
			// Now call resolveException to close the alert
			try {
				Document docResExcIP = XMLUtil
						.createDocument("ResolutionDetails");
				Element elmInbox = docResExcIP
						.createElement(NWCGConstants.INBOX);
				Element elmDocResExcIP = docResExcIP.getDocumentElement();
				elmDocResExcIP.setAttribute(NWCGConstants.AUTO_RESOLVED_FLAG,
						NWCGConstants.YES);
				elmDocResExcIP.setAttribute(NWCGConstants.RESOLVED_BY,
						env.getUserId());
				elmInbox.setAttribute(NWCGConstants.INBOX_KEY, inboxKey);
				elmDocResExcIP.appendChild(elmInbox);
				logger.verbose("@@@@@ Input XML to resolve exception : " + XMLUtil.extractStringFromDocument(docResExcIP));
				CommonUtilities.invokeAPI(env, NWCGConstants.API_RESOLVE_EXCEPTION, docResExcIP);
			} catch (ParserConfigurationException pce) {
				logger.error("!!!!! NWCGCloseIssueAlertOnReleaseSuccess::resolveAlert, ParserConfigurationException : " + pce.getMessage(), pce);
			} catch (TransformerException te) {
				logger.error("!!!!! NWCGCloseIssueAlertOnReleaseSuccess::resolveAlert, TransformerException : " + te.getMessage(), te);
			} catch (Exception e) {
				logger.error("!!!!! NWCGCloseIssueAlertOnReleaseSuccess::resolveAlert, Exception : " + e.getMessage(), e);
			}
		}
		logger.verbose("@@@@@ Exiting NWCGCloseAlertForBackOrderForwardOrder::resolveAlert @@@@@");
	}

	private static String getQueueKeyForInboxRecord(YFSEnvironment env,
			String inboxKey) throws Exception {
		logger.verbose("@@@@@ Entering NWCGCloseAlertForBackOrderForwardOrder::getQueueKeyForInboxRecord @@@@@");
		String queueKey = NWCGConstants.EMPTY_STRING;
		Document getExceptionDetailsDoc = XMLUtil
				.createDocument(NWCGConstants.INBOX);
		Element inboxElm = getExceptionDetailsDoc.getDocumentElement();
		inboxElm.setAttribute(NWCGConstants.INBOX_KEY, inboxKey);
		Document getExceptionDetailsOpTemplate = XMLUtil
				.createDocument(NWCGConstants.INBOX);
		Element outputTemplateDocElm = getExceptionDetailsOpTemplate
				.getDocumentElement();
		outputTemplateDocElm.setAttribute(NWCGConstants.INBOX_KEY,
				NWCGConstants.EMPTY_STRING);
		outputTemplateDocElm.setAttribute(NWCGConstants.QUEUE_KEY,
				NWCGConstants.EMPTY_STRING);
		Document apiOutputDoc = null;
		try {
			apiOutputDoc = CommonUtilities.invokeAPI(env,
					getExceptionDetailsOpTemplate,
					NWCGConstants.API_GET_EXCEPTION_DETAILS,
					getExceptionDetailsDoc);
		} catch (Exception e) {
			logger.error("!!!!! Exception :: ERROR occured while trying to call getExceptionDetails :: ", e);
			throw e;
		}
		if (apiOutputDoc != null) {
			Element apiOutputDocElm = apiOutputDoc.getDocumentElement();
			if (apiOutputDocElm != null) {
				queueKey = apiOutputDocElm
						.getAttribute(NWCGConstants.QUEUE_KEY);
			}
		}
		logger.verbose("@@@@@ Exiting NWCGCloseAlertForBackOrderForwardOrder::getQueueKeyForInboxRecord @@@@@");
		return queueKey;
	}
}