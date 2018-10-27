package com.nwcg.icbs.yantra.api.issue;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGCloseInc2IncTransferAlert implements YIFCustomApi {
	private static Logger logger = Logger.getLogger(NWCGCloseInc2IncTransferAlert.class.getName());

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

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
		logger.verbose("NWCGCloseInc2IncTransferAlert::closeInc2IncTransferAlert, Entered");
		logger.verbose("NWCGCloseInc2IncTransferAlert::closeInc2IncTransferAlert, Input XML to close RR alert : " +
					XMLUtil.extractStringFromDocument(docIP));
		
		String orderHdrKey = docIP.getDocumentElement().getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		logger.verbose("NWCGCloseInc2IncTransferAlert::closeInc2IncTransferAlert, Closing Alert for " + orderHdrKey);
		Document docAlerts = getOpenAlertsForIssue(env, orderHdrKey);
		if (docAlerts == null){
			logger.verbose("NWCGCloseInc2IncTransferAlert::closeInc2IncTransferAlert, " +
					"There are no open alerts for " + orderHdrKey);
			return docIP;
		}
		else {
			NodeList nlAlertList = docAlerts.getElementsByTagName("Inbox");
			if (nlAlertList == null || nlAlertList.getLength() < 1){
				logger.verbose("NWCGCloseInc2IncTransferAlert::closeInc2IncTransferAlert, " +
						"Zero open alerts for " + orderHdrKey);
				return docIP;
			}
			else {
				logger.verbose("NWCGCloseInc2IncTransferAlert::closeInc2IncTransferAlert, " +
						"No of alerts : " + nlAlertList.getLength());
				// There should be only one alert
				Element elmInbox = (Element) nlAlertList.item(0);
				String inboxKey = elmInbox.getAttribute(NWCGConstants.INBOX_KEY);
				if (inboxKey == null || inboxKey.trim().length() < 2){
					logger.verbose("NWCGCloseInc2IncTransferAlert::closeInc2IncTransferAlert, Inbox key is null");
					return docIP;
				}
				else {
					String queueKey = elmInbox.getAttribute(NWCGConstants.QUEUE_KEY);
					resolveAlert(env, inboxKey, queueKey);
				}
			}
		}
		return null;
	}
	
	/**
	 * This method will get the open alerts for the order
	 * @param env
	 * @param orderHdrKey
	 * @return
	 */
	private Document getOpenAlertsForIssue(YFSEnvironment env, String orderHdrKey){
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
			System.out.println("NWCGCloseInc2IncTransferAlert::getOpenAlertsForIssue, Input XML : " + ipStr);
			
			Document docAlertsIP = XMLUtil.getDocument(ipStr);
			Document docOPTmpl = XMLUtil.getDocument(opTmpl);
			
			docOpenAlerts = 
				CommonUtilities.invokeAPI(env, docOPTmpl, NWCGConstants.API_GET_EXCEPTION_LIST, docAlertsIP);

		} catch (Exception e) {
			logger.error("NWCGCloseInc2IncTransferAlert::getOpenAlertsForIssue, " +
					"Exception thrown while calling getExceptionList API" + e.getMessage());
			e.printStackTrace();			
			return null;
		}		
		return docOpenAlerts;
	}
	
	private void resolveAlert(YFSEnvironment env, String inboxKey, String queueKey){
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
			logger.verbose("NWCGCloseInc2IncTransferAlert::resolveAlert, Input XML to changeException: " + 
					XMLUtil.extractStringFromDocument(docChgException));
			CommonUtilities.invokeAPI(env, NWCGConstants.API_CHANGE_EXCEPTION, docChgException);
				
		} catch (ParserConfigurationException pce) {
			logger.error("NWCGCloseInc2IncTransferAlert::resolveAlert, ParserConfigurationException : " + pce.getMessage(),pce);
			pce.printStackTrace();
		} catch (TransformerException te) {
			logger.error("NWCGCloseInc2IncTransferAlert::resolveAlert, TransformerException : " + te.getMessage(),te);
			te.printStackTrace();
		} catch (Exception e) {
			logger.error("NWCGCloseInc2IncTransferAlert::resolveAlert, Exception : " + e.getMessage(),e);
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

			logger.verbose("NWCGCloseInc2IncTransferAlert::resolveAlert, Input XML to resolve exception : " + 
					XMLUtil.extractStringFromDocument(docResExcIP));
			CommonUtilities.invokeAPI(env, NWCGConstants.API_RESOLVE_EXCEPTION, docResExcIP);
		} catch (ParserConfigurationException pce) {
			logger.error("NWCGCloseInc2IncTransferAlert::resolveAlert, ParserConfigurationException : " + pce.getMessage(),pce);
			pce.printStackTrace();
		} catch (TransformerException te) {
			logger.error("NWCGCloseInc2IncTransferAlert::resolveAlert, TransformerException : " + te.getMessage(),te);
			te.printStackTrace();
		} catch (Exception e) {
			logger.error("NWCGCloseInc2IncTransferAlert::resolveAlert, Exception : " + e.getMessage(),e);
			e.printStackTrace();
		}		
		logger.debug("NWCGCloseInc2IncTransferAlert::resolveAlert, Exited");
		return;
	}
}
