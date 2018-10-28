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

package com.nwcg.icbs.yantra.ib.read.handler;

import gov.nwcg.services.ross.common_types._1.CatalogID;
import gov.nwcg.services.ross.common_types._1.CompositeIncidentKeyType;
import gov.nwcg.services.ross.common_types._1.CompositeResourceRequestKeyType;
import gov.nwcg.services.ross.common_types._1.IncidentNumberType;
import gov.nwcg.services.ross.common_types._1.RequestCodeType;
import gov.nwcg.services.ross.common_types._1.ResourceRequestNaturalKeyType;
import gov.nwcg.services.ross.common_types._1.ResponseMessageType;
import gov.nwcg.services.ross.common_types._1.ResponseStatusType;
import gov.nwcg.services.ross.common_types._1.UnitIDType;
import gov.nwcg.services.ross.resource_order._1.DeliverOperationResultsResp;
import gov.nwcg.services.ross.resource_order._1.RetrieveResourceRequestReq;
import gov.nwcg.services.ross.resource_order._1.RetrieveResourceRequestResp;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.ib.msg.NWCGDeliverOperationResultsIB;
import com.nwcg.icbs.yantra.webservice.util.NWCGJAXBContextWrapper;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * Webservice message handler for RetrieveResourceRequestReq 
 * messages from ROSS
 */
public class NWCGRetrieveResourceRequestHandler implements com.nwcg.icbs.yantra.ib.read.handler.NWCGMessageHandlerInterface {
	
	private static final String className = NWCGRetrieveResourceRequestHandler.class.getName();

	// Standard YFSEnvironment object utilized by all API/SDF calls
	private YFSEnvironment myEnvironment;

	// Distribution ID of the input SOAP Message from the A&A Framework
	private String distributionId = NWCGConstants.EMPTY_STRING;

	// The unmarshalled JAXB object of the input RetrieveResourceRequestReq message
	private RetrieveResourceRequestReq input = null;

	private Vector <Element> validLines = new Vector<Element>();	
	private Vector <Element> alreadyScheduledReleased = new Vector<Element>();
	private Vector <Element> alreadyCancelled = new Vector<Element>();
	private HashMap<String, String> ohksForAlertsToPossiblyClose = new HashMap<String,String>();	
	private boolean doValidOrderLinesExistForRequestNo = false;	
	
	/**
	 *   1. Verify that the incident year / no exist. 
	 *   2. Verify if there all lines are 'retrievable' 
	 *      for all lines associated to the request number passed 
	 *      in the message. This could potentially include backorder 
	 *      and forward orders. Excludes cancelled subordinate
	 *      requests
	 *   3. Find all the order lines and their statuses for
	 *	    the given request sequence number 
	 *   4. Verify each line is in either 1100, or 10* status. 
	 *   5. Call changeOrder Sterling API to retrieve line(s).
	 *   (done by setting the OL/OrderLineTranQuantity/@Qty to 0) 
	 *   6. Post response message to ROSS.
	 * 
	 * @param YFSEnvironment
	 *            Given by Sterling integration JVM
	 * @param Document
	 *            The body of the SOAPMessage received by A&A and retrieved from
	 *            a JMS queue
	 */
	public Document process(YFSEnvironment env, Document inputDoc) throws NWCGException {	
		System.out.println("@@@@@ Entering NWCGRetrieveResourceRequestHandler::process @@@@@");
		
		final String methodName = "process";
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "ENTRY: "+className+"."+methodName);
		NWCGLoggerUtil.Log.finer("BEGIN TIMER: "+methodName);
		
		// Get the root/@distributionID attribute from the input XML
		distributionId = inputDoc.getDocumentElement().getAttribute(NWCGConstants.INC_NOTIF_DIST_ID_ATTR);
		
		// Set the local YFSEnvironment instance object
		this.myEnvironment = env;
		
		// Remove distributionID attribute from the documentElement of the input XML
		inputDoc.getDocumentElement().removeAttribute(NWCGConstants.INC_NOTIF_DIST_ID_ATTR);
		
		// Unmarshall the input XML to a RetrieveResourceRequestReq object
		try {
			this.input = (RetrieveResourceRequestReq) new NWCGJAXBContextWrapper()
					.getObjectFromDocument(inputDoc, new URL(
							NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
		} catch (Exception e) {
			NWCGLoggerUtil.Log.severe("FAILED TO PARSE INPUT MSG VIA JAXB");
			NWCGLoggerUtil.printStackTraceToLog(e);
			throw new NWCGException(e);
		}

		// Get Req #, Inc #, and Inc Year
		String requestNo	= getRequestNumberFromInput();
		String incidentNo   = getIncidentNumberFromInput();
		String incidentYear = getIncidentYearFromInput();		
		
		// doesIncidentExist() calls NWCGGetOrderOnlyService to
		// determine incident existence
		boolean incidentExists = doesIncidentExist(incidentNo, incidentYear);
		if (incidentExists) {
			
			// If the incident already exists, then we need to separate all of
			// the order lines associated to the input request number across all
			// issues, backorders, and forward orders.
			separateOrderLinesIntoStatusCategories(requestNo, incidentNo, incidentYear);
		}
		else 
		{
			/**
			 * Use Case Scenario 2.a The request number, incident number and
			 * year does not exist in ICBSR
			 * ----------------------------------------------------------------
			 * 1. ICBSR sends a negative message to ROSS with description “ICBSR
			 * cannot process the message with distribution id < distribution id >:
			 * Request <request number> on incident <Incident number & Year>
			 * does not exist in ICBSR”. 
			 * 2. Ends with success (will not continue with main success scenario).
			 * 
			 * Classified into 2 parts
			 * - Incident doesn't exist
			 * - No issues with the given request number
			 */
			
			NWCGLoggerUtil.Log.warning("Incident " + incidentNo + " / " + incidentYear + " does not exist in ICBS!");
						
			String responseMessage = getResponseMsgNoIncidentNoLines(requestNo, incidentNo, incidentYear); 
			postResponseToROSS(NWCGAAConstants.ROSS_RET_FAILURE_VALUE, 
					NWCGAAConstants.DELIVER_OPERATION_RESULTS_SEVERITY_1, 
					responseMessage, false, NWCGConstants.NWCG_MSG_CODE_919_E_1);
			return inputDoc;
		}
		String code = null, severity = null, responseMessage = null, messageCode = null;
		boolean retrievalInd = false;
		
		if (!doValidOrderLinesExistForRequestNo) {
			code = NWCGAAConstants.ROSS_RET_FAILURE_VALUE;			
			severity = NWCGAAConstants.DELIVER_OPERATION_RESULTS_SEVERITY_1;
			responseMessage = getResponseMsgNoIncidentNoLines(requestNo, incidentNo, incidentYear);
			retrievalInd = false;
			messageCode = NWCGConstants.NWCG_MSG_CODE_919_E_2 ;
		} 
		
		// Only if ALL available lines for eligible for cancellation, we'll treat
		// this as a success scenario. Eligibility means it's in Draft or Created
		// status and it is not already cancelle
		if (!validLines.isEmpty() && alreadyScheduledReleased.isEmpty()) {
			
			// Call changeOrder via multiApi to cancel all OrderLine's in the 
			// validLines structure
			cancelAllEligibleLines();
			
			// See if there are any Active alerts for Exception
			// Type PlaceResourceRequestExternalReq
			// to see if any of the orders just modified are now
			// completely cancelled, if so, we'll close these 
			// alerts
			if (ohksForAlertsToPossiblyClose.size() > 0) {
				closeOpenAlertsForXLDOrders(requestNo, incidentNo, incidentYear);
			}
			
			// And post 0 / INFORMATION positive response to ROSS.
			code = NWCGAAConstants.ROSS_RET_SUCCESS_VALUE;
			severity = NWCGAAConstants.DELIVER_OPERATION_RESULTS_SEVERITY_2;
			responseMessage = getResponseMsgSuccessfulRetrieval(requestNo, incidentNo, incidentYear);
			retrievalInd = true;
			messageCode = NWCGConstants.NWCG_MSG_CODE_919_I_1 ;
		} 
		else if (validLines.isEmpty()) {
			code = NWCGAAConstants.ROSS_RET_FAILURE_VALUE;			
			severity = NWCGAAConstants.DELIVER_OPERATION_RESULTS_SEVERITY_1;
			responseMessage = getResponseNoRetrievableLinesFound(requestNo, incidentNo, incidentYear);
			retrievalInd = false;
			messageCode = NWCGConstants.NWCG_MSG_CODE_919_E_3 ;			
		}
		else if (!alreadyScheduledReleased.isEmpty()) {
			code = NWCGAAConstants.ROSS_RET_FAILURE_VALUE;
			severity = NWCGAAConstants.DELIVER_OPERATION_RESULTS_SEVERITY_1;
			responseMessage = getOrderLineAlreadyScheduled(requestNo, incidentNo, incidentYear);
			retrievalInd = false;
			messageCode = NWCGConstants.NWCG_MSG_CODE_919_E_4;
		}

		postResponseToROSS(code, severity, responseMessage, retrievalInd, messageCode);
		NWCGLoggerUtil.Log.finer("END TIMER: "+methodName);
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "RETURN: "+className+"."+methodName);
		
		System.out.println("@@@@@ Exiting NWCGRetrieveResourceRequestHandler::process @@@@@");
		return inputDoc;
	}

	private void closeOpenAlertsForXLDOrders(String requestNo, String incidentNo, String incidentYear) throws NWCGException {
		System.out.println("@@@@@ Entering NWCGRetrieveResourceRequestHandler::closeOpenAlertsForXLDOrders @@@@@");
		
		final String methodName = "closeOpenAlertsForXLDOrders";
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "ENTRY: "+className+"."+methodName);
		NWCGLoggerUtil.Log.finer("BEGIN TIMER: "+methodName);
		
		Set <String> ohkStrings = ohksForAlertsToPossiblyClose.keySet();
		Iterator <String> ohkStrIterator = ohkStrings.iterator();
		
		try {
			while (ohkStrIterator.hasNext()) {
				String ohk = ohkStrIterator.next();
				if (StringUtil.isEmpty(ohk)) continue;
								
				Document exceptionListDoc = getExceptionListForIssue(ohk);
				if (exceptionListDoc == null) continue;
				
				NodeList inboxRecordsNL = exceptionListDoc.getElementsByTagName(NWCGConstants.INBOX);
				
				// No alerts found for this OHK, on to the next one
				if (inboxRecordsNL == null || inboxRecordsNL.getLength() == 0) continue;
				
				// Get the largest 2 digit index XX for reference type COMMENT and value CommentXX
				// for all InboxReferences on every Inbox record found.
				int maxIndex = 0;
				for (int i = 0; i < inboxRecordsNL.getLength(); i++) {
					Node o = inboxRecordsNL.item(i);
					Element inboxRecordElm = (o instanceof Element) ? (Element) o : null;
					if (inboxRecordElm == null) continue;
					
					NodeList ibxReferences = inboxRecordElm.getElementsByTagName(NWCGConstants.INBOX_REFERENCES);
					if (ibxReferences != null && ibxReferences.getLength() > 0) {
						for (int j = 0; j < ibxReferences.getLength(); j++) {
							Node p = ibxReferences.item(j);
							Element ibxReferencesElm = (p instanceof Element) ? (Element) p : null;
							if (ibxReferencesElm == null) continue;
							
							// If a YFS_INBOX_REFERENCES record is found with a reference type of
							// COMMENT, then get the integer index (last two characters) of the
							// InboxReferences/@Name element. There may be more so we'll
							// need to ensure we get the greatest value comment and then pass
							// an index value ro resolveAlert that is 5 above.
							String referenceType = ibxReferencesElm.getAttribute(NWCGConstants.REFERENCE_TYPE);
							if (!StringUtil.isEmpty(referenceType) && 
									referenceType.equalsIgnoreCase(NWCGConstants.REFERENCE_TYPE_COMMENT)) 
							{
								String referenceName = ibxReferencesElm.getAttribute(NWCGConstants.NAME_ATTR);
								if (!StringUtil.isEmpty(referenceName)) {
									String cIdxStr = referenceName.substring(referenceName.length()-2);
									try {
										int x = Integer.parseInt(cIdxStr);
										if (x > maxIndex) maxIndex = x;
									}
									catch (NumberFormatException nfe) {
										NWCGLoggerUtil.Log.warning("The last 2 characters of the Name attribute for " +
												"this COMMENT InboxReferences record isn't numeric!");
										NWCGLoggerUtil.Log.warning("Name attribute's value: " + cIdxStr);
										continue;
									}
								}
							}
						}// for loop on InboxReferences Elements						
					}// if the inboxReferences NodeList isn't null and > 0
					
					String inboxKey = inboxRecordElm.getAttribute(NWCGConstants.INBOX_KEY);
					if (!StringUtil.isEmpty(inboxKey)) {
						
						// See if the order for this ohk is still not cancelled
						String maxOrderStatus = getOrderStatusForOrderHeaderKey(ohk);
						String reason = NWCGConstants.EMPTY_STRING;
						boolean orderCompleteXld = false;
						
						if (!StringUtil.isEmpty(maxOrderStatus) && maxOrderStatus.startsWith("9000")) {
							reason = "Automatically resolved by ICBSR when a successful ROSS retrieval request " +
							"for Request No " +requestNo+" resulted in all lines on the Issue being cancelled.";
							orderCompleteXld = true;
						}
						else {
							reason = "Request number "+requestNo+" on the Issue has been successfully retrieved by ROSS.";
						}						
						resolveAlert(inboxKey, ohk, maxIndex, reason, orderCompleteXld);
					}
				}
			}
		}
		catch (Exception e) {
			NWCGLoggerUtil.Log.warning("Unable to close any open alerts for any Issues with Request Numbers just retrieved!");
			NWCGLoggerUtil.printStackTraceToLog(e);
		}
		NWCGLoggerUtil.Log.finer("END TIMER: "+methodName);
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "RETURN: "+className+"."+methodName);
		
		System.out.println("@@@@@ Exiting NWCGRetrieveResourceRequestHandler::closeOpenAlertsForXLDOrders @@@@@");
	}

	private void resolveAlert(String inboxKey, String ohk, int index, String reason, boolean orderCompleteXld) {
		System.out.println("@@@@@ Entering NWCGRetrieveResourceRequestHandler::resolveAlert @@@@@");
		
		final String methodName = "resolveAlert";
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "ENTRY: "+className+"."+methodName);
		NWCGLoggerUtil.Log.finer("BEGIN TIMER: "+methodName);
		
		//String reason = NWCGConstants.EMPTY_STRING;
		String queueKeyToUse = NWCGConstants.EMPTY_STRING;
		
		// Create the CommentXX number for the Name attribute of the
		// new inbox reference being created as a multiple of 5 
		// and the passed in index
		index += 5;
		String strIdx = String.valueOf(index);
		strIdx = StringUtil.prepadStringWithZeros(strIdx, 2);
		
		// First call changeException to add an inbox reference of type COMMENT
		// denoting why and how and when the alert was auto resolved.		
		try {
			// Defaults to the Issue Successs Queue if not provided in the input.
			queueKeyToUse = getQueueKeyForInboxRecord(inboxKey);
			if (StringUtil.isEmpty(queueKeyToUse))
				queueKeyToUse = "200909291155054020446";
			
			Document changeExceptionIP = XMLUtil.createDocument(NWCGConstants.INBOX);
			Element changeExceptionDocelm = changeExceptionIP.getDocumentElement();
			
			changeExceptionDocelm.setAttribute(NWCGConstants.INBOX_KEY, inboxKey);
			changeExceptionDocelm.setAttribute(NWCGConstants.ENTERPRISE_KEY, NWCGConstants.ENTERPRISE_CODE);
			changeExceptionDocelm.setAttribute(NWCGConstants.QUEUE_KEY, queueKeyToUse);
			if (!StringUtil.isEmpty(reason)) {
				Element inboxReferencesList = changeExceptionIP.createElement(NWCGConstants.INBOX_REFERENCES_LIST);
				Element inboxReferences = changeExceptionIP.createElement(NWCGConstants.INBOX_REFERENCES);
				
				inboxReferences.setAttribute(NWCGConstants.NAME_ATTR, new String("Comment"+strIdx));
				inboxReferences.setAttribute(NWCGConstants.VALUE_ATTR, reason);
				inboxReferences.setAttribute(NWCGConstants.REFERENCE_TYPE, NWCGConstants.REFERENCE_TYPE_COMMENT);
				inboxReferences.setAttribute(NWCGConstants.INBOX_KEY, inboxKey);
				inboxReferencesList.appendChild(inboxReferences);
				changeExceptionDocelm.appendChild(inboxReferencesList);				
			}
			NWCGLoggerUtil.Log.finest("NWCGCloseIssueAlertOnReleaseSuccess::resolveAlert, Input XML to changeException: " + 
					XMLUtil.extractStringFromDocument(changeExceptionIP));
			CommonUtilities.invokeAPI(myEnvironment, NWCGConstants.API_CHANGE_EXCEPTION, changeExceptionIP);
				
		} catch (ParserConfigurationException pce) {
			NWCGLoggerUtil.Log.warning("NWCGCloseIssueAlertOnReleaseSuccess::resolveAlert, ParserConfigurationException : " + pce.getMessage());
			NWCGLoggerUtil.printStackTraceToLog(pce);
		} catch (TransformerException te) {
			NWCGLoggerUtil.Log.warning("NWCGCloseIssueAlertOnReleaseSuccess::resolveAlert, TransformerException : " + te.getMessage());
			NWCGLoggerUtil.printStackTraceToLog(te);
		} catch (Exception e) {
			NWCGLoggerUtil.Log.warning("NWCGCloseIssueAlertOnReleaseSuccess::resolveAlert, Exception : " + e.getMessage());
			NWCGLoggerUtil.printStackTraceToLog(e);
		}
		
		// Don't call resolveException if the order
		// isn't cancelled completely
		if (!orderCompleteXld)
			return;
		
		// Now call resolveException to close the alert
		try {
			Document docResExcIP = XMLUtil.createDocument("ResolutionDetails");
			Element elmInbox = docResExcIP.createElement(NWCGConstants.INBOX);
			
			Element elmDocResExcIP = docResExcIP.getDocumentElement();
			elmDocResExcIP.setAttribute(NWCGConstants.AUTO_RESOLVED_FLAG, NWCGConstants.YES);
			elmDocResExcIP.setAttribute(NWCGConstants.RESOLVED_BY, myEnvironment.getUserId());
			elmInbox.setAttribute(NWCGConstants.INBOX_KEY, inboxKey);
			elmDocResExcIP.appendChild(elmInbox);

			NWCGLoggerUtil.Log.finest("NWCGCloseIssueAlertOnReleaseSuccess::resolveAlert, Input XML to resolve exception : " 
					+ XMLUtil.extractStringFromDocument(docResExcIP));
			CommonUtilities.invokeAPI(myEnvironment, NWCGConstants.API_RESOLVE_EXCEPTION, docResExcIP);
		} catch (ParserConfigurationException pce) {
			NWCGLoggerUtil.Log.warning("NWCGCloseIssueAlertOnReleaseSuccess::resolveAlert, ParserConfigurationException : " + pce.getMessage());
			NWCGLoggerUtil.printStackTraceToLog(pce);
		} catch (TransformerException te) {
			NWCGLoggerUtil.Log.warning("NWCGCloseIssueAlertOnReleaseSuccess::resolveAlert, TransformerException : " + te.getMessage());
			NWCGLoggerUtil.printStackTraceToLog(te);
		} catch (Exception e) {
			NWCGLoggerUtil.Log.warning("NWCGCloseIssueAlertOnReleaseSuccess::resolveAlert, Exception : " + e.getMessage());
			NWCGLoggerUtil.printStackTraceToLog(e);
		}				
		NWCGLoggerUtil.Log.finer("END TIMER: "+methodName);
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "RETURN: "+className+"."+methodName);
		
		System.out.println("@@@@@ Exiting NWCGRetrieveResourceRequestHandler::resolveAlert @@@@@");
	}
	
	private String getOrderStatusForOrderHeaderKey(String ohk) {
		System.out.println("@@@@@ Entering NWCGRetrieveResourceRequestHandler::getOrderStatusForOrderHeaderKey @@@@@");
		
		StringBuffer sb = new StringBuffer("<Order OrderHeaderKey=\"");
		sb.append(ohk);
		sb.append("\"/>");
		String maxOrderStatus = NWCGConstants.EMPTY_STRING;
		Document getOrderDetailsOpDoc = null;
		
		StringBuffer sbOpTemplate =
			new StringBuffer("<OrderList><Order Status=\"\" MaxOrderStatus=\"\" MaxOrderStatusDesc=\"\"/></OrderList>");
		try {
			String inpStr = sb.toString();
			String inpTemplate = sbOpTemplate.toString();
			
			Document inptDoc = XMLUtil.getDocument(inpStr);
			Document opTemplate = XMLUtil.getDocument(inpTemplate);
			
			getOrderDetailsOpDoc = 
				CommonUtilities.invokeAPI(myEnvironment, opTemplate, NWCGConstants.API_GET_ORDER_LIST, inptDoc);
		} catch (Exception e) {
			NWCGLoggerUtil.Log.severe("Exception thrown while calling getOrderList API");
			NWCGLoggerUtil.printStackTraceToLog(e);
			return null;
		}
		
		if (getOrderDetailsOpDoc != null) {
			Element opDocElm = getOrderDetailsOpDoc.getDocumentElement();
			NodeList nl = opDocElm.getElementsByTagName(NWCGConstants.ORDER_ELM);
			if (nl != null && nl.getLength() == 1) {
				Node orderNode = nl.item(0);
				if (orderNode instanceof Element) {
					Element orderElm = (Element) orderNode;
					if (!orderElm.hasAttribute("Status") && !orderElm.hasAttribute("MaxOrderStatus") &&
							!orderElm.hasAttribute("MaxOrderStatusDesc")) {
						maxOrderStatus = "9000";
					}
					else {
						maxOrderStatus = orderElm.getAttribute("MaxOrderStatus");
					}
				}
			}						
		}		
		
		System.out.println("@@@@@ Exiting NWCGRetrieveResourceRequestHandler::getOrderStatusForOrderHeaderKey @@@@@");
		return maxOrderStatus;
	}
	
	private String getQueueKeyForInboxRecord(String inboxKey) throws Exception {
		System.out.println("@@@@@ Entering NWCGRetrieveResourceRequestHandler::getQueueKeyForInboxRecord @@@@@");
		
		String queueKey = NWCGConstants.EMPTY_STRING;
		Document getExceptionDetailsDoc = XMLUtil.createDocument(NWCGConstants.INBOX);
		Element inboxElm = getExceptionDetailsDoc.getDocumentElement();
		inboxElm.setAttribute(NWCGConstants.INBOX_KEY, inboxKey);	
		
		Document getExceptionDetailsOpTemplate = XMLUtil.createDocument(NWCGConstants.INBOX);
		Element outputTemplateDocElm = getExceptionDetailsOpTemplate.getDocumentElement();
		
		outputTemplateDocElm.setAttribute(NWCGConstants.INBOX_KEY, NWCGConstants.EMPTY_STRING);
		outputTemplateDocElm.setAttribute(NWCGConstants.QUEUE_KEY, NWCGConstants.EMPTY_STRING);

		Document apiOutputDoc = null;
		try {
			apiOutputDoc = CommonUtilities.invokeAPI(myEnvironment, getExceptionDetailsOpTemplate, 
					NWCGConstants.API_GET_EXCEPTION_DETAILS, getExceptionDetailsDoc);
		}
		catch (Exception e) {
			NWCGLoggerUtil.Log.severe("ERROR occured while trying to call getExceptionDetails");
			NWCGLoggerUtil.printStackTraceToLog(e);
			throw e;
		}
		
		if (apiOutputDoc != null) {
			Element apiOutputDocElm = apiOutputDoc.getDocumentElement();
			if (apiOutputDocElm != null) {
				queueKey = apiOutputDocElm.getAttribute(NWCGConstants.QUEUE_KEY);
			}
		}		
		
		System.out.println("@@@@@ Exiting NWCGRetrieveResourceRequestHandler::getQueueKeyForInboxRecord @@@@@");
		return queueKey;
	}
	
	/**
	 * Retrieves all YFS_INBOX records in a list Document
	 * for all Active NWCG alerts for the PlaceResourceRequestExternalReq
	 * ExceptionType
	 *  
	 * @param orderHeaderKey
	 * @return getExceptionListDoc
	 */
	private Document getExceptionListForIssue(String orderHeaderKey) throws NWCGException {
		System.out.println("@@@@@ Entering NWCGRetrieveResourceRequestHandler::getExceptionListForIssue @@@@@");
		
		final String methodName = "getExceptionListForIssue";
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "ENTRY: "+className+"."+methodName);
		NWCGLoggerUtil.Log.finer("BEGIN TIMER: "+methodName);
		
		Document getExceptionListDoc = null;
		StringBuffer sb = new StringBuffer("<Inbox ActiveFlag=\"Y\" EnterpriseKey=\"NWCG\" ");
		sb.append("ExceptionType=\"PlaceResourceRequestExternalReq\">");
		sb.append("<InboxReferencesList><InboxReferences ValueQryType=\"LIKE\" Value=\"");
		sb.append(orderHeaderKey);
		sb.append("\"/>");
		sb.append("</InboxReferencesList>");
		sb.append("<OrderBy><Attribute Name=\"InboxKey\"/></OrderBy>");
		sb.append("</Inbox>");
		
		StringBuffer sbOpTemplate = new StringBuffer("<InboxList><Inbox InboxKey=\"\">");
		sbOpTemplate.append("<InboxReferencesList><InboxReferences ReferenceType=\"\" Name=\"\" Value=\"\"/>");
		sbOpTemplate.append("</InboxReferencesList></Inbox></InboxList>");
		
		try {
			String inpStr = sb.toString();
			String inpTemplate = sbOpTemplate.toString();
			
			Document inptDoc = XMLUtil.getDocument(inpStr);
			Document opTemplate = XMLUtil.getDocument(inpTemplate);
			
			getExceptionListDoc = 
				CommonUtilities.invokeAPI(myEnvironment, opTemplate, NWCGConstants.API_GET_EXCEPTION_LIST, inptDoc);

		} catch (Exception e) {
			NWCGLoggerUtil.Log.severe("Exception thrown while calling getExceptionList!");
			NWCGLoggerUtil.printStackTraceToLog(e);			
			throw new NWCGException(e);			
		}	
		NWCGLoggerUtil.Log.finer("END TIMER: "+methodName);
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "RETURN: "+className+"."+methodName);
		
		System.out.println("@@@@@ Exiting NWCGRetrieveResourceRequestHandler::getExceptionListForIssue @@@@@");
		return getExceptionListDoc;
	}

	/**
	 * Calls changeOrder with an OL/OLTranQty = 0 to change the line status to
	 * 9000.050 (Cancelled due to ROSS retrieval) for every order line in the
	 * validLines Vector.
	 * 
	 */
	private void cancelAllEligibleLines() throws NWCGException {
		System.out.println("@@@@@ Entering NWCGRetrieveResourceRequestHandler::cancelAllEligibleLines @@@@@");
		
		// Updated so that this method does not add more API calls than
		// NWCGConstants.NWCG_RETRIEVE_MAX_APIS_PER_MULTIAPI per 
		// multiAPI invocation in order to keep memory usage down for 
		// Incidents with thousands of lines.
		final String methodName = "cancelAllEligibleLines";
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "ENTRY: "+className+"."+methodName);
		NWCGLoggerUtil.Log.finer("BEGIN TIMER: "+methodName);

		// <MultiApi/>
		Document multiApiInput = null;
		
		try 
		{
			multiApiInput = XMLUtil.createDocument(NWCGConstants.MULTI_API_ELM);
			String orderHeaderKey = null, orderLineKey = null, maxLineStatus = null, lineStatus = null;			
			Element multiApiInputDocElm = multiApiInput.getDocumentElement();
			NWCGLoggerUtil.Log.finer("cancelAllEligibleLines, Valid Lines size : " + validLines.size());
			int numMultiApiCalls = 0;
			int modResult = validLines.size() % NWCGConstants.NWCG_RETRIEVE_MAX_APIS_PER_MULTIAPI;
			if (modResult > 0) {
				int x = (validLines.size()/NWCGConstants.NWCG_RETRIEVE_MAX_APIS_PER_MULTIAPI);
				numMultiApiCalls = x + 1;
			} else {
				numMultiApiCalls = (validLines.size()/NWCGConstants.NWCG_RETRIEVE_MAX_APIS_PER_MULTIAPI);
			}
			
			// Default to one multiApi call if for some reason we get 0 
			// out of the operations above.
			if (numMultiApiCalls == 0)
				numMultiApiCalls = 1;
			
			NWCGLoggerUtil.Log.finer("Invoking multiApi API " + numMultiApiCalls + " times.");
			int z = 0;
			for (z = 0; z < numMultiApiCalls; z++) {					
				
				// xml:/MultiApi/API
				Element apiElm = null;
				
				// xml:/MultiApi/API/Input
				Element inpElm = null;
				
				// xml:/MultiApi/API/Input/Order
				Element apiInputOrderElm = null;				
				
				int noApisInsertedInCurrentMultiApiDoc = 0;
				String dbg = XMLUtil.extractStringFromDocument(multiApiInput);

				for (int validLinesIndex = 0;
					validLinesIndex < validLines.size() && 
					noApisInsertedInCurrentMultiApiDoc < NWCGConstants.NWCG_RETRIEVE_MAX_APIS_PER_MULTIAPI;
					validLinesIndex++) 
				{
					NWCGLoggerUtil.Log.finest((numMultiApiCalls+1)+" multiApi IP XML: "+dbg);
						
					Object o = validLines.get(validLinesIndex);
					Element currOrderLine = (o instanceof Element) ? (Element) o : null;
					if (currOrderLine == null) {
						continue;
					}
					
					// Grab the OHK, OLK, and Line Status (numeric)
					orderHeaderKey = currOrderLine.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
					orderLineKey = currOrderLine.getAttribute(NWCGConstants.ORDER_LINE_KEY);
					maxLineStatus = currOrderLine.getAttribute(NWCGConstants.MAX_LINE_STATUS);
					lineStatus = currOrderLine.getAttribute(NWCGConstants.STATUS_ATTR);
					String curOrderLineCondVar2 = currOrderLine.getAttribute(NWCGConstants.CONDITION_VAR2);

					if (maxLineStatus != null && maxLineStatus.trim().length() < 2){
						NWCGLoggerUtil.Log.finest("cancelAllEligibleLines, Current order line \n" + XMLUtil.extractStringFromNode((Node)currOrderLine));
					}
					// if this line's status is already 9000*, we can't do anything to
					// it via changeOrder so we skip. Verified with J. Billiard
					// First condition 
					//	1. will take care of CANCEL line since we are getting cancelled line status as 9000
					//	2. will take care of subs, cons and retrieval due to condition variable 2
					// Second condition
					//	1. will take care of all the lines that are not in created or draft status
					//	2. For backorder or forward order lines, we are not getting any status from the API. It is blank, but we should
					//	   retrieve them too. So, adding a check where maxLineStatus is not empty and status is in draft or created status.
					//	   If the maxLineStatus is empty, then it means it can be backorder or forwarded here.
					if ((maxLineStatus.startsWith("9000") && 
						 !maxLineStatus.equals(NWCGConstants.STATUS_BACKORDERED) &&
						 !maxLineStatus.equals(NWCGConstants.STATUS_FORWARDED))
							|| curOrderLineCondVar2.startsWith("XLD"))
						continue;
					else if ((maxLineStatus.trim().length() > 2) && 
							 !maxLineStatus.startsWith("1100") && !maxLineStatus.startsWith("1000")) {
						continue;
					}
					
					// Create the standard multiApi elements
					apiElm = multiApiInput.createElement(NWCGConstants.API_ELM);
					inpElm =  multiApiInput.createElement(NWCGConstants.INPUT_ELM);
					apiInputOrderElm = multiApiInput.createElement(NWCGConstants.ORDER_ELM);
					apiElm.setAttribute("Name", NWCGConstants.API_CHANGE_ORDER);
					
					multiApiInputDocElm.appendChild(apiElm);
					apiElm.appendChild(inpElm);
					inpElm.appendChild(apiInputOrderElm);
					
					apiInputOrderElm.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHeaderKey);
					apiInputOrderElm.setAttribute(NWCGConstants.OVERRIDE, NWCGConstants.YES);
					
					Element orderLines = multiApiInput.createElement(NWCGConstants.ORDER_LINES);
					Element orderLine = multiApiInput.createElement(NWCGConstants.ORDER_LINE);
									
					apiInputOrderElm.appendChild(orderLines);					
					orderLine.setAttribute(NWCGConstants.ORDER_LINE_KEY, orderLineKey);
					
					NodeList olStats = orderLine.getElementsByTagName(NWCGConstants.ORDER_STATUSES_ELM);
					if (olStats != null && olStats.getLength() == 1) {
						// Remove it
						orderLine.removeChild(olStats.item(0));
					}				

					// if draft order
					if (lineStatus.contains("raft") || maxLineStatus.startsWith("1000")) {
						orderLine.setAttribute(NWCGConstants.ACTION, NWCGConstants.MODIFY);
						
						Node copiedOrderLine = multiApiInput.importNode(currOrderLine, true);
						orderLines.appendChild(copiedOrderLine);		
						if (copiedOrderLine instanceof Element) {
							Element newOrderLine = (Element) copiedOrderLine;
							newOrderLine = setNewOrderLineAttribs(newOrderLine);						
							newOrderLine.setAttribute(NWCGConstants.ACTION, NWCGConstants.MODIFY);
							newOrderLine.setAttribute(NWCGConstants.CONDITION_VAR2, NWCGConstants.CONDVAR2_XLD_REASON_RETRV);
						}
					}	
					else {
						orderLines.appendChild(orderLine);
						orderLine.setAttribute(NWCGConstants.CONDITION_VAR2, NWCGConstants.CONDVAR2_XLD_REASON_RETRV);
						orderLine.setAttribute(NWCGConstants.ACTION, NWCGConstants.CANCEL);	
						Element elmExtnOrderLine = multiApiInput.createElement(NWCGConstants.EXTN);
						orderLine.appendChild(elmExtnOrderLine);
						elmExtnOrderLine.setAttribute(NWCGConstants.EXTN_BACKORDER_FLAG, NWCGConstants.NO);
						elmExtnOrderLine.setAttribute(NWCGConstants.EXTN_FORWARD_ORDER_FLAG, NWCGConstants.NO);
						noApisInsertedInCurrentMultiApiDoc++;						
					}//if draft order					
				}//for - validLines.size()

				// This is where the multiApi API is invoked, could be several
				// times if there are many lines or the constant
				// NWCG_RETRIEVE_MAX_APIS_PER_MULTIAPI in the NWCGConstants 
				// class is set to a low number.								
				String finalInput = XMLUtil.extractStringFromDocument(multiApiInput);
				NWCGLoggerUtil.Log.finest("------------------------------------------------");
				NWCGLoggerUtil.Log.finest("FINAL multiApi input XML (# "+(z+1)+ "): " + finalInput);
				NWCGLoggerUtil.Log.finest("------------------------------------------------");
				
				// Call to multiApi API
				Document multiApiOutputDoc = CommonUtilities.invokeAPI(myEnvironment, NWCGConstants.MULTI_API_API, multiApiInput);
				
				//Put the order header keys that were just cancelled into a unique
				//set via a HashMap data structure
				if (multiApiOutputDoc != null) {
					NWCGLoggerUtil.Log.finest("multiApiOutputDoc: " + XMLUtil.extractStringFromDocument(multiApiOutputDoc));
					NodeList nl = multiApiOutputDoc.getElementsByTagName(NWCGConstants.ORDER_ELM);
					if (nl != null && nl.getLength()> 0) {
						for (int i = 0; i < nl.getLength(); i++) {
							Node o = nl.item(i);
							Element curOrderElm = (o instanceof Element) ? (Element) o : null;
							if (curOrderElm == null) continue;
							
							String curOHK = curOrderElm.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
							if (!StringUtil.isEmpty(curOHK))
								ohksForAlertsToPossiblyClose.put(curOHK.trim(), curOHK.trim());
						}						
					}
				}				
			}//for - numMultiApiCalls
		}//try
		catch (ParserConfigurationException pce) {
			NWCGLoggerUtil.Log.severe("FAILED TO RETRIEVE ORDER/ISSUE");
			NWCGLoggerUtil.printStackTraceToLog(pce);			
			throw new NWCGException(pce);
		}
		catch (Exception e) {
			NWCGLoggerUtil.Log.severe("FAILED TO RETRIEVE ORDER/ISSUE");
			NWCGLoggerUtil.printStackTraceToLog(e);			
			throw new NWCGException(e);
		}
		NWCGLoggerUtil.Log.finer("END TIMER: "+methodName);
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "RETURN: "+className+"."+methodName);
		
		System.out.println("@@@@@ Exiting NWCGRetrieveResourceRequestHandler::cancelAllEligibleLines @@@@@");
	}
	
	/**
	 * Sets the order line attributes for changeOrder for draft lines
	 * @param orderLine
	 * @return Element OrderLine
	 */
	private Element setNewOrderLineAttribs(Element orderLine) { 
		System.out.println("@@@@@ Entering NWCGRetrieveResourceRequestHandler::setNewOrderLineAttribs @@@@@");
		
		if (orderLine.hasAttribute(NWCGConstants.MAX_LINE_STATUS))
			orderLine.removeAttribute(NWCGConstants.MAX_LINE_STATUS);
		if (orderLine.hasAttribute(NWCGConstants.STATUS_ATTR))
			orderLine.removeAttribute(NWCGConstants.STATUS_ATTR);
		
		// Modify the OrderLine/OrderLineTranQty/@OrderedQty to 0
		Element olTranQty = XMLUtil.getFirstElementByName(orderLine, NWCGConstants.ORDER_LINE_TRAN_QTY_ELM);
		if (olTranQty != null) {
			olTranQty.setAttribute(NWCGConstants.ORDERED_QTY, "0");
		}
		
		// Set OL/Extn/@ExtnBackOrderFlag="N" and @ExtnForwardOrderFlag="N" on Draft 
		// Orders lines since it's not possible to "Cancel" a draft line, we can only
		// remove it but ICBS needs to keep the line, set OrderedQty to 0 and reset
		// the BO and Fwd Flags to N
		Element olExtnElm = XMLUtil.getFirstElementByName(orderLine, NWCGConstants.EXTN_ELEMENT);
		if (olExtnElm != null) {
			olExtnElm.setAttribute(NWCGConstants.EXTN_BACKORDER_FLAG, NWCGConstants.NO);
			olExtnElm.setAttribute(NWCGConstants.EXTN_FORWARD_ORDER_FLAG, NWCGConstants.NO);
		}
		
		// Remove OrderLine/Order/OrderStasuses, if it exists
		Element olOrderStatuses = XMLUtil.getFirstElementByName(orderLine, NWCGConstants.ORDER_STATUSES_ELM);
		if (olOrderStatuses != null) {
			Node olOrderStatusesNode = olOrderStatuses;
			Node parentNode = olOrderStatuses.getParentNode();
			olOrderStatuses = null;
			parentNode.removeChild(olOrderStatusesNode);		
		}
	
		// Create the OL/OrderLineTranQuantity element instead.
		Document multiApi = orderLine.getOwnerDocument();
		Element olTranQtyElm = multiApi
				.createElement(NWCGConstants.ORDER_LINE_TRAN_QTY_ELM);
		orderLine.appendChild(olTranQtyElm);
		olTranQtyElm.setAttribute(NWCGConstants.ORDERED_QTY, "0");

		String itemUom = getItemUOMFromOrderLine(orderLine);
		olTranQtyElm.setAttribute(NWCGConstants.TRANSACTIONAL_UOM, itemUom);
		
		// Remove the OrderLine/Order node and children since the order line
		// Element is passed to the changeOrder API which doesn't accept an Order node
		// as a child of the OrderLine node
		Element orderLineOrder = XMLUtil.getFirstElementByName(orderLine, NWCGConstants.ORDER_ELM);
		if (orderLineOrder != null) {
			Node toBeRemoved = orderLineOrder;
			orderLine.removeChild(toBeRemoved);
		}
		
		System.out.println("@@@@@ Exiting NWCGRetrieveResourceRequestHandler::setNewOrderLineAttribs @@@@@");
		return orderLine;
	}
	
	private String getItemUOMFromOrderLine(Element orderLine) {
		System.out.println("@@@@@ Entering NWCGRetrieveResourceRequestHandler::getItemUOMFromOrderLine @@@@@");
		
		String retVal = "EA";
		Element item = XMLUtil.getFirstElementByName(orderLine, NWCGConstants.ITEM);
		if (item != null) {
			if (item.hasAttribute(NWCGConstants.UNIT_OF_MEASURE)) {
				retVal = item.getAttribute(NWCGConstants.UNIT_OF_MEASURE);
			}
		}		
		
		System.out.println("@@@@@ Exiting NWCGRetrieveResourceRequestHandler::getItemUOMFromOrderLine @@@@@");
		return retVal;
	}

	/**
	 * Determines whether an incident exists in NWCG_INCIDENT_ORDER for the
	 * given incident number and incident year
	 * 
	 * @param incidentNo
	 * @param incidentYear
	 * @return boolean true - if a record is found for the Incident No/Year
	 *         false - otherwise, including on any exceptions
	 */
	private boolean doesIncidentExist(String incidentNo, String incidentYear) {
		System.out.println("@@@@@ Entering NWCGRetrieveResourceRequestHandler::doesIncidentExist @@@@@");
		
		final String methodName = "doesIncidentExist";
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "ENTRY: "+className+"."+methodName);
		NWCGLoggerUtil.Log.finer("BEGIN TIMER: "+methodName);
		
		boolean incidentExists = true;
		
		StringBuffer sb = new StringBuffer("<NWCGIncidentOrder IncidentNo=\"");
		sb.append(incidentNo);
		sb.append("\"" + " Year=\"" + incidentYear + "\"/>");		
		String getIncidentOrderInput = sb.toString();
		
		Document apiOutputDoc = null;
		try {
			apiOutputDoc = CommonUtilities.invokeService(myEnvironment,
					NWCGAAConstants.NWCG_GET_INCIDENT_ORDER_ONLY_SERVICE, getIncidentOrderInput);
		} catch (Exception e) {
			NWCGLoggerUtil.printStackTraceToLog(e);
			incidentExists = false;
			return incidentExists;
		}

		// We get a null pointer back when the incident is not found.
		if (apiOutputDoc == null) {
			incidentExists = false;
		}
		NWCGLoggerUtil.Log.finer("END TIMER: "+methodName);
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "RETURN: "+className+"."+methodName);
		
		System.out.println("@@@@@ Exiting NWCGRetrieveResourceRequestHandler::doesIncidentExist @@@@@");
		return incidentExists;
	}

	/**
	 * Calls CommonUtilities.getOLsForABaseReqInAnInc() to get the output of the
	 * getOrderLineList API for the given Incident Number, Incident Year, and
	 * Request Number.
	 * 
	 * Note: The OrderLine/Extn/@ExtnRequestNo is passed to the
	 * getOrderLineDetails API with a query type of FLIKE (ExtnRequestNoQryType) 
	 * so that all order lines associated to the input request number is 
	 * returned as well.
	 * 
	 * Sample Input XML: 
	 * <OrderLine ReadFromHistory="N">
	 *  <Extn ExtnRequestNo="S-1"/> 
	 *  <Order>
	 *   <Extn ExtnIncidentNo="CO-RMK-000500" ExtnIncidentYear="2010"/> 
	 *  </Order>
	 * </OrderLine>
	 * 
	 * API Output template:
	 * (resources.jar/template/api/extn/NWCGRetrieveResourceRequestHandler_getOrderLineList.xml)
	 *  
	 * <OrderLineList TotalLineList="">
	 *  <OrderLine OrderHeaderKey="" OrderLineKey="" ConditionVariable1=""
	 *   ConditionVariable2="" MaxLineStatus="">
	 *    <Extn ExtnRequestNo="" ExtnBackOrderFlag="" ExtnBackorderedQty="" 
	 *     ExtnFwdQty="" ExtnOrigReqQty="" ExtnQtyRfi="" ExtnShippingContactName=""
	 *     ExtnShippingContactPhone="" ExtnSystemOfOrigin="" ExtnUTFQty=""/>
	 *  <OrderLineTranQty OrderedQty="" TransactionalUOM=""/>
	 *   <Order OrderHeaderKey="" OrderNo="" MaxOrderStatus="" DocumentType="">
	 *    <Extn ExtnIncidentNo="" ExtnIncidentYear=""/>
	 *   <OrderStatuses>
	 *    <OrderStatus OrderLineScheduleKey="" OrderReleaseKey="" OrderReleaseStatusKey=""
	 *     ReceivingNode="" ShipNode="" Status="" StatusDate="" StatusDescription=""
	 *     StatusQty="" StatusReason="" TotalQuantity="">
	 *      <OrderStatusTranQuantity StatusQty="" TotalQuantity="" TransactionalUOM=""/>
	 *       <Details ExpectedDeliveryDate="" ExpectedShipmentDate="" ShipByDate=""
	 *        TagNumber=""/>
	 *     </OrderStatus> 
	 *    </OrderStatuses> 
	 *   </Order> 
	 *   <Item ItemID="" ProductClass="" UnitOfMeasure=""/> 
	 *  </OrderLine> 
	 * </OrderLineList>
	 * 
	 * @param requestNo
	 * @param incidentNo
	 * @param incidentYear
	 * @return
	 */
	private void separateOrderLinesIntoStatusCategories(String requestNo, String incidentNo, String incidentYear) {
		System.out.println("@@@@@ Entering NWCGRetrieveResourceRequestHandler::separateOrderLinesIntoStatusCategories @@@@@");
	
		// Call CommonUtilities method to get all order lines for the given
		// incidentNo, incidentYear, and FLIKE(requestNo)
		Document apiOutputDoc = null;
		try {
			apiOutputDoc = CommonUtilities.getOLsForABaseReqInAnInc(
					myEnvironment, incidentNo, incidentYear, requestNo,
					NWCGConstants.GET_ORDER_LINE_LIST_OP_TEMPLATE);
		} catch (Exception e) {
			NWCGLoggerUtil.Log.severe("ICBS was unable to find any Issue Lines for " + 
					requestNo + " on Incident "+incidentNo+" / "+incidentYear);
			NWCGLoggerUtil.printStackTraceToLog(e);
			return;
		}

		// Check if the API returned null
		if (apiOutputDoc == null) {
			NWCGLoggerUtil.Log.severe("ICBS was unable to find any Issue Lines for " + 
					requestNo + " on Incident "+incidentNo+" / "+incidentYear);
			return;
		}
		
		try {
			NWCGLoggerUtil.Log.finest("CommonUtilities.getOLsForABaseReqInAnInc() OUTPUT XML: ");
			NWCGLoggerUtil.Log.finest(XMLUtil.extractStringFromDocument(apiOutputDoc));
		} catch (TransformerException e) {
			NWCGLoggerUtil.printStackTraceToLog(e);
		}

		Element docElm = apiOutputDoc.getDocumentElement();
		NodeList orderLineNodeList = docElm.getElementsByTagName(NWCGConstants.ORDER_LINE);
		
		// If no lines are returned for the given Incident No/Year by
		// getOrderLineList, MCF will not return TotalLineList attribute
		if (orderLineNodeList == null || orderLineNodeList.getLength() < 1){
			NWCGLoggerUtil.Log.warning("No orderLines found(Inc/Year/ReqNo) ("+incidentNo+"/"+incidentYear+"/"+requestNo+")");
			return;
		}
		
		// This integer represent the total # of OrderLine elements found in the
		// output XML of the getOrderLineList API call made by the CommonUtilities
		// method getOLsForABaseReqInAnInc()
		int numberOfOrderLineNodes = orderLineNodeList.getLength();		
		int baseReqLen = requestNo.length();
		
		// Iterate over all of the "OrderLine" Nodes 
		for (int i = 0; i < numberOfOrderLineNodes; i++) 
		{
			Object o = orderLineNodeList.item(i);
			Element currentOrderLineElement = (o instanceof Element) ? (Element) o : null;
			if (currentOrderLineElement == null) continue;
			
			if (currentOrderLineElement.getNodeName().equals(NWCGConstants.ORDER_LINE) ||
			    currentOrderLineElement.getLocalName().equals(NWCGConstants.ORDER_LINE)) 
			{							
				 // Get OrderLine/Extn element, which is the firstChild of
				 // currentOrderLineElement
				Node firstChildOfOrderLine = currentOrderLineElement.getFirstChild();
				Element firstChildElement = (firstChildOfOrderLine instanceof Element) ? (Element) firstChildOfOrderLine : null;
				if (firstChildElement == null) continue;
				
				if (firstChildElement.getNodeName().equals(NWCGConstants.EXTN_ELEMENT) ||
					firstChildElement.getLocalName().equals(NWCGConstants.EXTN_ELEMENT)) 
				{
					Element orderLineExtnElm = firstChildElement;
					String currOLExtnRequestNo = orderLineExtnElm.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
				
					if (StringUtil.isEmpty(currOLExtnRequestNo)) {
						NWCGLoggerUtil.Log.severe("OrderLine/Extn/@ExtnRequestNo attribute not found on getOrderLineList OrderLine");
						return;
					}
				
					// if a derived line is found, should always be true bc of CommonUtilities method
					if (currOLExtnRequestNo.startsWith(requestNo)) {						
						// If the current request number begins with the input
						// request number get the lineStatus in number format as a
						// String, "1100"
						// Check if the current line is a derived base request no (If request no is S-1, then check if this
						// line request no is either S-1 or S-1.x instead of S-10, etc
						if ((currOLExtnRequestNo.length() > baseReqLen) && ((currOLExtnRequestNo.charAt(baseReqLen)) != '.')){
							continue;
						}
						
						 String statusText = currentOrderLineElement.getAttribute(NWCGConstants.STATUS_ATTR);
						 String condVar2 = currentOrderLineElement.getAttribute(NWCGConstants.CONDITION_VAR2);
						
						// If the max line status is Cancelled, find out if
						// it's possible because of an ICBS entered sub,
						// cons, or ROSS initiated retrieval request, or fwd qty
						
						if (condVar2.equalsIgnoreCase(NWCGConstants.CONDVAR2_XLD_REASON_RETRV) ||
							condVar2.equalsIgnoreCase(NWCGConstants.CONDVAR2_XLD_REASON_SUB) ||
							condVar2.equalsIgnoreCase(NWCGConstants.CONDVAR2_XLD_REASON_CONS))
						{
							alreadyCancelled.add(currentOrderLineElement);		
						} 
						else if (StringUtil.isEmpty(condVar2) && StringUtil.isEmpty(statusText)) {
							validLines.add(currentOrderLineElement);
							doValidOrderLinesExistForRequestNo = true;
						}
						else if (!StringUtil.isEmpty(statusText) && statusText.contains("reated")
								|| statusText.contains("raft")) 
						{
							validLines.add(currentOrderLineElement);
							doValidOrderLinesExistForRequestNo = true;
						} else if (!statusText.contains("reated")
								&& statusText.contains("raft")) 
						{
							alreadyScheduledReleased.add(currentOrderLineElement);
						}
						else {
							alreadyScheduledReleased.add(currentOrderLineElement);
						}
					}// if derived line found
				}// if Order/Extn element found
			}// if Order element found
		}// for loop over OrderLine elements	
		System.out.println("@@@@@ Exiting NWCGRetrieveResourceRequestHandler::separateOrderLinesIntoStatusCategories @@@@@");
	}

	/**
	 * Creates a DeliverOperationResultsReq object and sets the xs:any portion
	 * as a RetrieveResourceRequestResp type. Sends the message to ROSS via the
	 * NWCGDeliverOperationResultsIB().process(YFSEnvironment, Document) method.
	 * 
	 * @param code
	 * @param severity
	 * @param responseMessage
	 * @param retrieveAcceptedInd
	 * @param messageCode
	 */
	private void postResponseToROSS(String code, String severity, String responseMessage, boolean retrieveAcceptedInd, String messageCode) {
		System.out.println("@@@@@ Entering NWCGRetrieveResourceRequestHandler::postResponseToROSS @@@@@");

		RetrieveResourceRequestResp resp = new RetrieveResourceRequestResp();
		ResponseStatusType rst = new ResponseStatusType();

		ResponseMessageType rmt = new ResponseMessageType();
		rmt.setCode(messageCode);
		rmt.setDescription(responseMessage);
		rmt.setSeverity(severity);

		rst.setReturnCode(Integer.parseInt(code));
		rst.getResponseMessage().add(rmt);

		resp.setResponseStatus(rst);
		resp.setRetrieveAcceptedInd(retrieveAcceptedInd);
		
		try {
			// Call A&A framework for synchronous delivery of delivery
			// operations
			Document response = null;
			response = new NWCGJAXBContextWrapper().getDocumentFromObject(resp, response, new URL(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
			Element docElm = response.getDocumentElement();
			docElm.setAttribute(NWCGAAConstants.MDTO_DISTID, distributionId);
			DeliverOperationResultsResp aaResponse = new NWCGDeliverOperationResultsIB()
					.process(myEnvironment, response);
			if (aaResponse != null)
				NWCGLoggerUtil.Log.finest(aaResponse.toString());
		} catch (Exception e) {
			NWCGLoggerUtil.Log.warning("Failed to post response to ROSS:" + e);
			NWCGLoggerUtil.printStackTraceToLog(e);
		}
		
		System.out.println("@@@@@ Exiting NWCGRetrieveResourceRequestHandler::postResponseToROSS @@@@@");
	}
	
	/**
	 * Returns the request number of the input
	 * root/RequestKey/NaturalRequestKey/RequestCode /CatalogID concatenating
	 * with a hyphen with /SequenceNumber Note: Sequence number is padded with
	 * 0's to reach a length of 6.
	 * 
	 * @return requestNumber
	 */
	private String getRequestNumberFromInput () {
		System.out.println("@@@@@ Entering NWCGRetrieveResourceRequestHandler::getRequestNumberFromInput @@@@@");
		
		// Get data out of /RequestKey/NaturalRequestKey/RequestCode
		// /CatalogID
		CompositeResourceRequestKeyType crrkt = input.getRequestKey();
		ResourceRequestNaturalKeyType rrnkt = crrkt
				.getNaturalResourceRequestKey();

		RequestCodeType requestCodeType = rrnkt.getRequestCode();
		CatalogID catalogIdType = requestCodeType.getCatalogID();
		String reqSeqNo = requestCodeType.getSequenceNumber();
		String catalogId = catalogIdType.value();

		StringBuffer sb = new StringBuffer(catalogId);
		sb.append(NWCGConstants.DASH);
		sb.append(reqSeqNo);
		String requestNumber = sb.toString();
		
		System.out.println("@@@@@ Exiting NWCGRetrieveResourceRequestHandler::getRequestNumberFromInput @@@@@");
		return requestNumber;
	}
	
	private String getIncidentNumberFromInput () {
		System.out.println("@@@@@ Entering NWCGRetrieveResourceRequestHandler::getIncidentNumberFromInput @@@@@");
		
		// Get data out of
		// /RequestKey/NaturalRequestKey/IncidentKey/NaturalIncidentKey/
		// /HostID/UnidIDPrefix concatenated with a hyphen, UnidIDSuffix,
		// hyphen, Sequence
		// Number padded up to 6 places with zero's
		CompositeResourceRequestKeyType crrkt = input.getRequestKey();
		ResourceRequestNaturalKeyType rrnkt = crrkt
				.getNaturalResourceRequestKey();
		CompositeIncidentKeyType incidentKeyType = rrnkt.getIncidentKey();
		IncidentNumberType naturalIncidentKey = incidentKeyType
			.getNaturalIncidentKey();
		UnitIDType hostId = naturalIncidentKey.getHostID();
		String unitIdPrefix = hostId.getUnitIDPrefix();
		String unitIdSuffix = hostId.getUnitIDSuffix();
		
		int sequenceNo = naturalIncidentKey.getSequenceNumber();
		String paddedSequenceNo = Integer.toString(sequenceNo);
		paddedSequenceNo = StringUtil
				.prepadStringWithZeros(paddedSequenceNo, 6);

		StringBuffer sb = new StringBuffer(unitIdPrefix);
		sb.append(NWCGConstants.DASH);
		sb.append(unitIdSuffix);
		sb.append(NWCGConstants.DASH);
		sb.append(paddedSequenceNo);

		String incidentNo = sb.toString();
		
		System.out.println("@@@@@ Exiting NWCGRetrieveResourceRequestHandler::getIncidentNumberFromInput @@@@@");
		return incidentNo;
	}
	
	private String getIncidentYearFromInput () {
		System.out.println("@@@@@ Entering NWCGRetrieveResourceRequestHandler::getIncidentYearFromInput @@@@@");
		
		//Get data out of /RequestKey/NaturalRequestKey/IncidentKey/NaturalIncidentKey/
		// /YearCreated
		CompositeResourceRequestKeyType crrkt = input.getRequestKey();
		ResourceRequestNaturalKeyType rrnkt = crrkt.getNaturalResourceRequestKey();
		CompositeIncidentKeyType incidentKeyType = rrnkt.getIncidentKey();
		IncidentNumberType naturalIncidentKey = incidentKeyType.getNaturalIncidentKey();
		String incidentYear = naturalIncidentKey.getYearCreated();
		
		System.out.println("@@@@@ Exiting NWCGRetrieveResourceRequestHandler::getIncidentYearFromInput @@@@@");
		return incidentYear;
	}
	
	private String getResponseMsgNoIncidentNoLines (String requestNo, String incidentNo, String incidentYear) {
		System.out.println("@@@@@ Entering NWCGRetrieveResourceRequestHandler::getResponseMsgNoIncidentNoLines @@@@@");
		
		StringBuffer responseMsg = new StringBuffer("ICBSR cannot process the message with distribution id ");
		responseMsg.append(distributionId);
		responseMsg.append(": Request ");
		responseMsg.append(requestNo);
		responseMsg.append(" on incident ");
		responseMsg.append(incidentNo);
		responseMsg.append(" ");
		responseMsg.append(incidentYear);
		responseMsg.append(" does not exist in ICBSR.");
		
		System.out.println("@@@@@ Exiting NWCGRetrieveResourceRequestHandler::getResponseMsgNoIncidentNoLines @@@@@");
		return responseMsg.toString();
	}
	
	private String getResponseMsgSuccessfulRetrieval (String requestNo, String incidentNo, String incidentYear) {
		System.out.println("@@@@@ Entering NWCGRetrieveResourceRequestHandler::getResponseMsgSuccessfulRetrieval @@@@@");
		
		StringBuffer sb = 
			new StringBuffer("ICBSR has successfully retrieved all lines for Request ");
		sb.append(requestNo);
		sb.append(" on Incident ");
		sb.append(incidentNo);
		sb.append(" Year ");
		sb.append(incidentYear);
		sb.append(".");
		
		System.out.println("@@@@@ Exiting NWCGRetrieveResourceRequestHandler::getResponseMsgSuccessfulRetrieval @@@@@");
		return sb.toString();	
	}
	
	private String getResponseNoRetrievableLinesFound (String requestNo, String incidentNo, String incidentYear) {
		System.out.println("@@@@@ Entering NWCGRetrieveResourceRequestHandler::getResponseNoRetrievableLinesFound @@@@@");
		
		StringBuffer sb = 
			new StringBuffer("The request number could not be retrieved in ICBS for ");
		sb.append(requestNo);
		sb.append(" on Incident ");
		sb.append(incidentNo);
		sb.append(" Year ");
		sb.append(incidentYear);
		sb.append(" because ICBSR was unable to find any requests eligible for retrieval ");
		sb.append("for the given request number, Incident & Year.");
		
		System.out.println("@@@@@ Exiting NWCGRetrieveResourceRequestHandler::getResponseNoRetrievableLinesFound @@@@@");
		return sb.toString();
	}
	
	private String getOrderLineAlreadyScheduled (String requestNo, String incidentNo, String incidentYear) {
		System.out.println("@@@@@ Entering NWCGRetrieveResourceRequestHandler::getOrderLineAlreadyScheduled @@@@@");
		
		StringBuffer sb = 
			new StringBuffer("Unable to retrieve request");
		sb.append(requestNo);
		sb.append(" on Incident ");
		sb.append(incidentNo);
		sb.append(" Year ");
		sb.append(incidentYear);
		sb.append(", because the request number or a subordinate of the request is already ");
		sb.append("being processed for fulfillment at the cache.");
		
		System.out.println("@@@@@ Exiting NWCGRetrieveResourceRequestHandler::getOrderLineAlreadyScheduled @@@@@");
		return sb.toString();
	}
}