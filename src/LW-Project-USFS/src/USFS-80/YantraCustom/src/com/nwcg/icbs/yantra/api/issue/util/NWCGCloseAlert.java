package com.nwcg.icbs.yantra.api.issue.util;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public final class NWCGCloseAlert {
	/**
	 * This method will get the open alerts for the order
	 * @param env
	 * @param orderHdrKey
	 * @return
	 */
	public static Document getOpenAlertsForIssue(YFSEnvironment env, String key, String exceptionType){
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
			System.out.println("NWCGCloseAlert::getOpenAlertsForIssue, Input XML : " + ipStr);
			
			Document docAlertsIP = XMLUtil.getDocument(ipStr);
			Document docOPTmpl = XMLUtil.getDocument(opTmpl);
			
			docOpenAlerts = 
				CommonUtilities.invokeAPI(env, docOPTmpl, NWCGConstants.API_GET_EXCEPTION_LIST, docAlertsIP);

		} catch (Exception e) {
			System.out.println("NWCGCloseAlert::getOpenAlertsForIssue, " +
					"Exception thrown while calling getExceptionList API" + e.getMessage());
			e.printStackTrace();			
			return null;
		}		
		return docOpenAlerts;
	}
	
	public static void resolveAlert(YFSEnvironment env, String inboxKey, String queueKey, String reasonForClose){
		if (reasonForClose == null){
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
			System.out.println("NWCGCloseAlert::resolveAlert, Input XML to changeException: " + 
					XMLUtil.extractStringFromDocument(docChgException));
			CommonUtilities.invokeAPI(env, NWCGConstants.API_CHANGE_EXCEPTION, docChgException);
				
		} catch (ParserConfigurationException pce) {
			System.out.println("NWCGCloseAlert::resolveAlert, ParserConfigurationException : " + pce.getMessage());
			pce.printStackTrace();
		} catch (TransformerException te) {
			System.out.println("NWCGCloseAlert::resolveAlert, TransformerException : " + te.getMessage());
			te.printStackTrace();
		} catch (Exception e) {
			System.out.println("NWCGCloseAlert::resolveAlert, Exception : " + e.getMessage());
			e.printStackTrace();
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

			System.out.println("NWCGCloseAlert::resolveAlert, Input XML to resolve exception : " + 
					XMLUtil.extractStringFromDocument(docResExcIP));
			CommonUtilities.invokeAPI(env, NWCGConstants.API_RESOLVE_EXCEPTION, docResExcIP);
		} catch (ParserConfigurationException pce) {
			System.out.println("NWCGCloseAlert::resolveAlert, ParserConfigurationException : " + pce.getMessage());
			pce.printStackTrace();
		} catch (TransformerException te) {
			System.out.println("NWCGCloseAlert::resolveAlert, TransformerException : " + te.getMessage());
			te.printStackTrace();
		} catch (Exception e) {
			System.out.println("NWCGCloseAlert::resolveAlert, Exception : " + e.getMessage());
			e.printStackTrace();
		}		
		System.out.println("NWCGCloseAlert::resolveAlert, Exited");
		return;
	}
	
	public static void closeAlert(YFSEnvironment env, String key, String exceptionType, String reasonForClose){
		Document docAlerts = getOpenAlertsForIssue(env, key, exceptionType);
		if (docAlerts == null){
			System.out.println("NWCGCloseAlert::closeAlert, There are no open alerts for " + key);
			return;
		}
		else {
			NodeList nlAlertList = docAlerts.getElementsByTagName("Inbox");
			if (nlAlertList == null || nlAlertList.getLength() < 1){
				System.out.println("NWCGCloseAlert::closeAlert, Zero open alerts for " + key);
				return;
			}
			else {
				System.out.println("NWCGCloseAlert::closeAlert, No of alerts : " + nlAlertList.getLength());
				// There should be only one alert
				Element elmInbox = (Element) nlAlertList.item(0);
				String inboxKey = elmInbox.getAttribute(NWCGConstants.INBOX_KEY);
				if (inboxKey == null || inboxKey.trim().length() < 2){
					System.out.println("NWCGCloseAlert::closeAlert, Inbox key is null");
					return;
				}
				else {
					String queueKey = elmInbox.getAttribute(NWCGConstants.QUEUE_KEY);
					resolveAlert(env, inboxKey, queueKey, reasonForClose);
				}
			}
		}
		return;
	}

}
