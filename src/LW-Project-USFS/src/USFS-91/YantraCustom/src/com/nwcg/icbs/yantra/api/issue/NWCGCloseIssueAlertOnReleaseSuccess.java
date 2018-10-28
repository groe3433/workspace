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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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

public class NWCGCloseIssueAlertOnReleaseSuccess implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGCloseIssueAlertOnReleaseSuccess.class);

	private Properties sdfApiArgs = null;
	private String orderHeaderKey = null;
	private YFSEnvironment myEnvironment = null;

	public void setProperties(Properties arg0) throws Exception {
		this.sdfApiArgs = arg0;
	}

	/**
	 * 1 Find the alert for the order/issue passed to this API by the ON_SUCCESS
	 * event of the Schedule Order transaction. 2 If more than one order was
	 * created for the alert, verify if all orders for the alert have been
	 * scheduled and released. a If no, do nothing and exit. b If yes, then
	 * create the input for the resolveException API in order to close the alert
	 * 
	 * @param env
	 * @param doc
	 * @return
	 * @throws Exception
	 */
	public Document closeIssueAlert(YFSEnvironment env, Document inputDoc)
			throws Exception {
		logger.verbose("@@@@@ Entering NWCGCloseIssueAlertOnReleaseSuccess::closeIssueAlert @@@@@");
		logger.verbose("@@@@@ input document is:\n" + XMLUtil.extractStringFromDocument(inputDoc));
		this.myEnvironment = env;
		Element docElm = inputDoc.getDocumentElement();
		orderHeaderKey = docElm.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		logger.verbose("@@@@@ closeIssueAlert for orderHeaderKey=" + orderHeaderKey);
		String extnSysOfOrigin = getSystemOfOriginFromInputDoc(inputDoc);
		if (StringUtil.isEmpty(extnSysOfOrigin)) {
			// Couldn't find the attribute, so do nothing
			logger.verbose("@@@@@ Unable to find Order/Extn element in input document to closeIssueAlert API");
			return inputDoc;
		}
		if (!extnSysOfOrigin.equalsIgnoreCase(NWCGConstants.ROSS_SYSTEM)) {
			// Not a ROSS initiated issue, so do nothing
			logger.verbose("@@@@@ closeIssueAlert(): Not a an ROSS-initiated issue, orderHeaderKey:" + orderHeaderKey);
			return inputDoc;
		}
		// Call getExceptionList to get the list of InboxReferences for this OrderHeaderKey
		Document getExceptionListOpDoc = getExceptionListForIssue();
		if (getExceptionListOpDoc == null)
			return inputDoc;
		// Get the inboxKey for the alert associated to this issue/order
		String inboxKey = getInboxKeyFromExceptionListDoc(getExceptionListOpDoc);
		// Get a data structure to map OrderHeaderKeys to their respective MaxOrderStatus
		HashMap<String, String> ohkStatusMap = getOrderStatusMap(getExceptionListOpDoc);
		boolean alertCanBeClosed = true;
		if (ohkStatusMap != null) {
			if (ohkStatusMap.size() == 1) {
				resolveAlert(inboxKey);
			} else {
				Iterator<String> it = ohkStatusMap.keySet().iterator();
				while (it.hasNext()) {
					String curOhk = it.next();
					if (curOhk == orderHeaderKey)
						continue;
					String curOhkStat = ohkStatusMap.get(curOhk);
					if (curOhkStat.startsWith("1")) {
						alertCanBeClosed = false;
						break;
					}
				}
				if (alertCanBeClosed)
					resolveAlert(inboxKey);
			}
		}
		if (!alertCanBeClosed) {
			logger.verbose("@@@@@ Alert can not be closed.");
		}
		logger.verbose("@@@@@ Exiting NWCGCloseIssueAlertOnReleaseSuccess::closeIssueAlert @@@@@");
		return inputDoc;
	}

	/**
	 * Returns the Order/Extn/@ExtnSystemOfOrigin attribute from the input DOM
	 * object passed to this extended API from the ON_SUCCESS event of the
	 * Schedule Order transaction
	 * 
	 * @param inputDoc
	 * @return String System Of Origin
	 */
	private String getSystemOfOriginFromInputDoc(Document inputDoc) {
		logger.verbose("@@@@@ Entering NWCGCloseIssueAlertOnReleaseSuccess::getSystemOfOriginFromInputDoc @@@@@");
		String extnSystemOfOrigin = NWCGConstants.EMPTY_STRING;
		Element docElm = inputDoc.getDocumentElement();
		Node orderFirstChildNode = docElm.getFirstChild();
		if (orderFirstChildNode != null
				&& (orderFirstChildNode.getNodeType() == Element.ELEMENT_NODE)
				|| orderFirstChildNode instanceof Element) {
			Element orderFirstChildElm = (Element) orderFirstChildNode;
			extnSystemOfOrigin = orderFirstChildElm
					.getAttribute(NWCGConstants.EXTN_SYSTEM_OF_ORIGIN);
		}
		logger.verbose("@@@@@ Exiting NWCGCloseIssueAlertOnReleaseSuccess::getSystemOfOriginFromInputDoc @@@@@");
		return extnSystemOfOrigin;
	}

	/**
	 * Returns the InboxList/Inbox/@InboxKey attribute from the input document,
	 * assumed to be the output of the getExceptionList API
	 * 
	 * @param getExceptionListOpDoc
	 * @return String inboxKey
	 */
	private String getInboxKeyFromExceptionListDoc(Document getExceptionListOpDoc) {
		logger.verbose("@@@@@ Entering NWCGCloseIssueAlertOnReleaseSuccess::getInboxKeyFromExceptionListDoc @@@@@");
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
		logger.verbose("@@@@@ Entering NWCGCloseIssueAlertOnReleaseSuccess::getInboxKeyFromExceptionListDoc @@@@@");
		return inboxKey;
	}

	/**
	 * Calls the resolveException API to close an alert for the given inboxKey.
	 * Sets the ResolvedBy attribute as the User ID found in the YFSEnvironment
	 * object
	 * 
	 * @param inboxKey
	 */
	private void resolveAlert(String inboxKey) {
		logger.verbose("@@@@@ Entering NWCGCloseIssueAlertOnReleaseSuccess::resolveAlert @@@@@");
		String reason = "Automatically resolved by ICBSR when Issue was successfully Scheduled & Released.";
		String queueKeyToUse = NWCGConstants.EMPTY_STRING;
		// First call changeException to add an inbox reference of type COMMENT denoting why and how and when the alert was auto resolved.
		if (inboxKey != null && !inboxKey.equals("")) {
			try {
				// Defaults to the Incident Successs Queue if not provided in the input.
				queueKeyToUse = getQueueKeyForInboxRecord(inboxKey);
				if (StringUtil.isEmpty(queueKeyToUse))
					queueKeyToUse = "200907021048304004968";
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
				logger.verbose("@@@@@ NWCGCloseIssueAlertOnReleaseSuccess::resolveAlert, Input XML to changeException: "
						+ XMLUtil.extractStringFromDocument(changeExceptionIP));
				CommonUtilities.invokeAPI(myEnvironment,
						NWCGConstants.API_CHANGE_EXCEPTION, changeExceptionIP);
			} catch (ParserConfigurationException pce) {
				logger.error("!!!!! Caught ParserConfigurationException : " + pce);
			} catch (TransformerException te) {
				logger.error("!!!!! Caught TransformerException : " + te);
			} catch (Exception e) {
				logger.error("!!!!! Caught General Exception : " + e);
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
						myEnvironment.getUserId());
				elmInbox.setAttribute(NWCGConstants.INBOX_KEY, inboxKey);
				elmDocResExcIP.appendChild(elmInbox);
				logger.verbose("@@@@@ NWCGCloseIssueAlertOnReleaseSuccess::resolveAlert, Input XML to resolve exception : "
						+ XMLUtil.extractStringFromDocument(docResExcIP));
				CommonUtilities.invokeAPI(myEnvironment,
						NWCGConstants.API_RESOLVE_EXCEPTION, docResExcIP);
			} catch (ParserConfigurationException pce) {
				logger.error("!!!!! Caught ParserConfigurationException : " + pce);
			} catch (TransformerException te) {
				logger.error("!!!!! Caught TransformerException : " + te);
			} catch (Exception e) {
				logger.error("!!!!! Caught General Exception : " + e);
			}
			logger.verbose("@@@@@ Exiting NWCGCloseIssueAlertOnReleaseSuccess::resolveAlert @@@@@");
		}
	}

	private String getQueueKeyForInboxRecord(String inboxKey) throws Exception {
		logger.verbose("@@@@@ Entering NWCGCloseIssueAlertOnReleaseSuccess::getQueueKeyForInboxRecord @@@@@");
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
			apiOutputDoc = CommonUtilities.invokeAPI(myEnvironment,
					getExceptionDetailsOpTemplate,
					NWCGConstants.API_GET_EXCEPTION_DETAILS,
					getExceptionDetailsDoc);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exceptio :: ERROR occured while trying to call getExceptionDetails : " + e);
			throw e;
		}
		if (apiOutputDoc != null) {
			Element apiOutputDocElm = apiOutputDoc.getDocumentElement();
			if (apiOutputDocElm != null) {
				queueKey = apiOutputDocElm
						.getAttribute(NWCGConstants.QUEUE_KEY);
			}
		}
		logger.verbose("@@@@@ Exiting NWCGCloseIssueAlertOnReleaseSuccess::getQueueKeyForInboxRecord @@@@@");
		return queueKey;
	}

	/**
	 * Returns a HashMap data structure mapping OrderHeaderKeys to their
	 * respective Order/@MaxOrderStatus by extracting the OrderHeaderKey from
	 * the InboxReference/@Value attribute for the Javascript hyperlink in the
	 * UI to jump from the Alert Details page directly to the Issue Details page
	 * 
	 * @param getExceptionListOpDoc
	 * @return HashMap<String,String>
	 */
	private HashMap<String, String> getOrderStatusMap(Document getExceptionListOpDoc) {
		logger.verbose("@@@@@ Entering NWCGCloseIssueAlertOnReleaseSuccess::getOrderStatusMap @@@@@");
		// Key: OHK -> Value: Status (e.g. 1100)
		HashMap<String, String> ohkStatusMap = new HashMap<String, String>();
		Element apiOpDocElm = getExceptionListOpDoc.getDocumentElement();
		NodeList apiOpInboxNL = apiOpDocElm
				.getElementsByTagName(NWCGConstants.INBOX_REFERENCES_ELM);
		if (apiOpInboxNL != null && apiOpInboxNL.getLength() != 0) {
			// Iterate over all the Inbox/InboxReferences elements
			for (int i = 0; i < apiOpInboxNL.getLength(); i++) {
				Node curInboxRefNode = apiOpInboxNL.item(i);
				if (curInboxRefNode != null
						&& curInboxRefNode instanceof Element) {
					Element curInboxRefElm = (Element) curInboxRefNode;
					String urlRef = curInboxRefElm
							.getAttribute(NWCGConstants.INBOX_REFERENCE_TYPE);
					String jsLink = curInboxRefElm
							.getAttribute(NWCGConstants.INBOX_REFERENCE_VALUE);
					if (!StringUtil.isEmpty(urlRef)
							&& !StringUtil.isEmpty(jsLink)
							&& urlRef
									.equals(NWCGConstants.INBOX_REFERENCE_URL_TYPE)) {
						String ohk = getOrderHeaderKeyFromJSLink(jsLink);
						String orderStatus = getOrderStatusForOrderHeaderKey(ohk);
						ohkStatusMap.put(ohk, orderStatus);
					}
				}
			}
		} else {
			logger.verbose("@@@@@ No active inbox record found for order header key:"
					+ orderHeaderKey);
			return null;
		}
		logger.verbose("@@@@@ Exiting NWCGCloseIssueAlertOnReleaseSuccess::getOrderStatusMap @@@@@");
		return ohkStatusMap;
	}

	private String getOrderStatusForOrderHeaderKey(String ohk) {
		logger.verbose("@@@@@ Entering NWCGCloseIssueAlertOnReleaseSuccess::getOrderStatusForOrderHeaderKey @@@@@");
		StringBuffer sb = new StringBuffer("<Order OrderHeaderKey=\"");
		sb.append(ohk);
		sb.append("\"/>");
		String maxOrderStatus = NWCGConstants.EMPTY_STRING;
		Document getOrderDetailsOpDoc = null;

		StringBuffer sbOpTemplate = new StringBuffer(
				"<OrderList><Order MaxOrderStatus=\"\"/></OrderList>");
		try {
			String inpStr = sb.toString();
			String inpTemplate = sbOpTemplate.toString();

			Document inptDoc = XMLUtil.getDocument(inpStr);
			Document opTemplate = XMLUtil.getDocument(inpTemplate);

			getOrderDetailsOpDoc = CommonUtilities.invokeAPI(myEnvironment,
					opTemplate, "getOrderList", inptDoc);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: Exception thrown while calling getOrderList API : " + e);
			return null;
		}

		if (getOrderDetailsOpDoc != null) {
			Element opDocElm = getOrderDetailsOpDoc.getDocumentElement();
			NodeList nl = opDocElm
					.getElementsByTagName(NWCGConstants.ORDER_ELM);
			if (nl != null && nl.getLength() == 1) {
				Node orderNode = nl.item(0);
				if (orderNode instanceof Element) {
					Element orderElm = (Element) orderNode;
					maxOrderStatus = orderElm
							.getAttribute(NWCGConstants.MAX_ORDER_STATUS);
				}
			}
		}
		logger.verbose("@@@@@ Exiting NWCGCloseIssueAlertOnReleaseSuccess::getOrderStatusForOrderHeaderKey @@@@@");
		return maxOrderStatus;
	}

	private String getOrderHeaderKeyFromJSLink(String attribute) {
		logger.verbose("@@@@@ Entering NWCGCloseIssueAlertOnReleaseSuccess::getOrderHeaderKeyFromJSLink @@@@@");
		int beg = attribute.indexOf("'OrderHeaderKey','");
		if (beg != -1)
			beg += 18;
		else
			return null;
		// Minus 3 for the '); characters
		int end = attribute.length() - 3;
		String retVal = attribute.substring(beg, end);
		logger.verbose("@@@@@ Exiting NWCGCloseIssueAlertOnReleaseSuccess::getOrderHeaderKeyFromJSLink @@@@@");
		return retVal;
	}

	private Document getExceptionListForIssue() {
		logger.verbose("@@@@@ Entering NWCGCloseIssueAlertOnReleaseSuccess::getExceptionListForIssue @@@@@");
		Document getExceptionListDoc = null;
		StringBuffer sb = new StringBuffer(
				"<Inbox ActiveFlag=\"Y\" EnterpriseKey=\"NWCG\" ");
		sb.append("ExceptionType=\"PlaceResourceRequestExternalReq\">");
		sb.append("<InboxReferencesList><InboxReferences ValueQryType=\"LIKE\" Value=\"");
		sb.append(orderHeaderKey);
		sb.append("\"/>");
		sb.append("</InboxReferencesList>");
		sb.append("<OrderBy><Attribute Name=\"InboxKey\"/></OrderBy>");
		sb.append("</Inbox>");

		StringBuffer sbOpTemplate = new StringBuffer(
				"<InboxList><Inbox InboxKey=\"\">");
		sbOpTemplate
				.append("<InboxReferencesList><InboxReferences ReferenceType=\"\" Name=\"\" Value=\"\"/>");
		sbOpTemplate.append("</InboxReferencesList></Inbox></InboxList>");

		try {
			String inpStr = sb.toString();
			String inpTemplate = sbOpTemplate.toString();

			Document inptDoc = XMLUtil.getDocument(inpStr);
			Document opTemplate = XMLUtil.getDocument(inpTemplate);

			getExceptionListDoc = CommonUtilities.invokeAPI(myEnvironment,
					opTemplate, NWCGConstants.API_GET_EXCEPTION_LIST, inptDoc);

		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: thrown while calling getExceptionList API" + e);
			return null;
		}
		logger.verbose("@@@@@ Exiting NWCGCloseIssueAlertOnReleaseSuccess::getExceptionListForIssue @@@@@");
		return getExceptionListDoc;
	}
}