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
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGCheckAndCloseAlert implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGCheckAndCloseAlert.class);

	private Properties myProperties = null;

	public void setProperties(Properties arg0) throws Exception {
		this.myProperties = arg0;
	}

	/**
	 * This is called from NWCGCheckAndCloseAlertService. It is triggered on
	 * clicking 'Close' in alert detail Business Logic: Regardless of the alert
	 * type, it will close the alert. If this call is for CreateReqAndPlaceReq
	 * (Inbox/@ExceptionType) or UpdateNFESResourceRequestResp alert type, then
	 * this method will get the latest information for order and incident and
	 * will call NWCGCreateReqAndPlaceReqService or NWCGPostOBMsgService
	 * respectively. If there is a failure, then it will create an attribute
	 * with CustomErrorDesc. We need to display value on the alert console (if
	 * present any). Input XML to this alert will be of this form
	 * <ResolutionDetails AutoResolvedFlag="N" IgnoreOrdering="Y"
	 * InboxKey="200909190123304048668" ResolvedBy="nwcgsys"> <Inbox
	 * ExceptionType="UIEXCEPTION" InboxKey="200909190123304048668">
	 * <InboxReferencesList> <InboxReferences Name="Distribution ID"
	 * ReferenceType="TEXT" Value="1234567"/> </InboxReferencesList> <Queue
	 * QueueDescription="xml:/Inbox/Queue/@QueueDescription"/> </Inbox>
	 * </ResolutionDetails>
	 * 
	 * @param env
	 * @param docIP
	 * @return
	 * @throws Exception
	 */
	public Document checkAndCloseAlert(YFSEnvironment env, Document docIP) throws Exception {
		logger.verbose("@@@@@ Entering NWCGCheckAndCloseAlert::checkAndCloseAlert @@@@@");
		logger.verbose("@@@@@ NWCGCheckAndCloseAlert::checkAndCloseAlert, Input Document : " + XMLUtil.extractStringFromDocument(docIP));
		Document docOP = null;
		NodeList nlChildNodes = docIP.getElementsByTagName(NWCGConstants.INBOX);
		if (nlChildNodes != null && nlChildNodes.getLength() > 0) {
			Element elmInbox = (Element) nlChildNodes.item(0);
			logger.verbose("@@@@@ NWCGCheckAndCloseAlert::checkAndCloseAlert, Node Name : " + elmInbox.getNodeName());
			if (elmInbox.getNodeName().equalsIgnoreCase(NWCGConstants.INBOX)) {
				String strAlertType = elmInbox.getAttribute(NWCGConstants.EXCEPTION_TYPE);
				String distributionID = NWCGConstants.EMPTY_STRING;
				if (strAlertType == null || strAlertType.length() < 1 || strAlertType.equalsIgnoreCase("UpdateNFESResourceRequestResp")) {
					HashMap<String, String> hmapAlertDtls = getAlertDetails(env, elmInbox.getAttribute(NWCGConstants.INBOX_KEY));
					strAlertType = hmapAlertDtls.get(NWCGConstants.EXCEPTION_TYPE);
					distributionID = hmapAlertDtls.get(NWCGConstants.DIST_ID_ATTR);
					// Remove this code before check-in
					Set<String> setStr = hmapAlertDtls.keySet();
					Iterator<String> itrStr = setStr.iterator();
					while (itrStr.hasNext()) {
						String tmp = itrStr.next();
					}
				}
				String queueName = ((Element) docIP.getElementsByTagName(NWCGConstants.QUEUE).item(0)).getAttribute(NWCGConstants.QUEUE_DESC);
				logger.verbose("@@@@@ NWCGCheckAndCloseAlert::checkAndCloseAlert, Queue Name : " + queueName);
				if (strAlertType.equalsIgnoreCase(NWCGConstants.MSG_CREATE_REQUEST_AND_PLACE_RESP) && ((queueName.equalsIgnoreCase(NWCGConstants.Q_NWCG_ISSUE_FAILURE)) || (queueName.equalsIgnoreCase(NWCGConstants.Q_NWCG_FAULT)))) {
					logger.verbose("@@@@@ NWCGCheckAndCloseAlert::checkAndCloseAlert, Handling alert type CreateRequestAndPlaceReq");
					// Make sure the string that we are searching for distribution id should be "Distribution ID"
					HashMap<String, String> hmap = new HashMap<String, String>();
					boolean retnVal = validateAndPlaceICBSRInitiatedReq(env, docIP, hmap, distributionID);
					logger.verbose("@@@@@ NWCGCheckAndCloseAlert::checkAndCloseAlert, Return Value : " + retnVal);
					// If it failed during the processing, do not close the alert. Display the alert message and let the user on the console fix it.
					if (!retnVal) {
						String errorDesc = hmap.get(NWCGConstants.ERROR_DESC);
						try {
							String alertID = elmInbox.getAttribute(NWCGConstants.INBOX_KEY);
							if (alertID == null) {
								alertID = NWCGConstants.EMPTY_STRING;
							}
							Document docCloseAlert = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
							docCloseAlert.getDocumentElement().setAttribute("CustomErrorDesc", alertID + " - " + errorDesc);
							logger.verbose("@@@@@ Exiting NWCGCheckAndCloseAlert::checkAndCloseAlert (1) @@@@@");
							return docCloseAlert;
						} catch (ParserConfigurationException e) {
							logger.error("!!!!! Caught ParserConfigurationException : " + e);
							e.printStackTrace();
						}
					} else {
						logger.verbose("@@@@@ NWCGCheckAndCloseAlert::checkAndCloseAlert, Calling NWCGPostInternalMsgService");
						// Put the message in queue. Let the reader read it from queue and call NWCGCreateReqAndPlaceReqService + additional logic of calling shipments API for reprocessing.
						String opXml = hmap.get("OrderXML");
						logger.verbose("@@@@@ NWCGCheckAndCloseAlert::checkAndCloseAlert, Input Order XML : " + opXml);
						docOP = CommonUtilities.invokeService(env, NWCGConstants.SVC_TRANSFORM_ICBSR_ISSUE_TO_ROSS_XSL, opXml);
						docOP = CommonUtilities.invokeService(env, NWCGConstants.SVC_POST_OB_MSG_SVC, docOP);
					}
				} else if (strAlertType.equalsIgnoreCase("UpdateNFESResourceRequestResp") && ((queueName.equalsIgnoreCase(NWCGConstants.Q_NWCG_ISSUE_FAILURE)) || (queueName.equalsIgnoreCase(NWCGConstants.Q_NWCG_FAULT)))) {
					handleUpdateNFESResMsg(env, docIP, distributionID);
				}
				String inboxKey = elmInbox.getAttribute(NWCGConstants.INBOX_KEY);
				docOP = closeException(env, docIP, inboxKey);
			}
		}
		logger.verbose("@@@@@ Exiting NWCGCheckAndCloseAlert::checkAndCloseAlert @@@@@");
		return docOP;
	}

	/**
	 * This method will create the required input for resolveException and makes
	 * an API call through CommonUtilities
	 * 
	 * @param env
	 * @param docIP
	 * @param inboxKey
	 * @return
	 */
	private Document closeException(YFSEnvironment env, Document docIP, String inboxKey) {
		logger.verbose("@@@@@ Entering NWCGCheckAndCloseAlert::closeException @@@@@");
		Document docResExcOP = null;
		try {
			Document docResExcIP = XMLUtil.createDocument(NWCGConstants.RESOLUTION_DETAILS);
			Element elmInbox = docResExcIP.createElement(NWCGConstants.INBOX);
			Element elmDocResExcIP = docResExcIP.getDocumentElement();
			elmDocResExcIP.setAttribute(NWCGConstants.AUTO_RESOLVED_FLAG, NWCGConstants.YES);
			elmDocResExcIP.setAttribute(NWCGConstants.RESOLVED_BY, env.getUserId());
			elmInbox.setAttribute(NWCGConstants.INBOX_KEY, inboxKey);
			elmDocResExcIP.appendChild(elmInbox);
			logger.verbose("@@@@@ NWCGCheckAndCloseAlert::closeException, Input XML to resolve exception : " + XMLUtil.extractStringFromDocument(docResExcIP));
			docResExcOP = CommonUtilities.invokeAPI(env, NWCGConstants.API_RESOLVE_EXCEPTION, docResExcIP);
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException : " + pce);
			pce.printStackTrace();
		} catch (TransformerException te) {
			logger.error("!!!!! Caught TransformerException : " + te);
			te.printStackTrace();
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);
			e.printStackTrace();
		}
		logger.verbose("@@@@@ Exiting NWCGCheckAndCloseAlert::closeException @@@@@");
		return docResExcOP;
	}

	/**
	 * This method will get the Distribution ID from InboxReferences element. It
	 * will then make a call to getOBEntry method to get the OrderHeaderKey. If
	 * the output of getOBEntry is true, then retreive the OrderHeaderKey and
	 * make a call to getDtlsAndPlaceICBSRInitiatedReq. If the output is false,
	 * then set the error in hashmap attribute and return false. Input parameter
	 * distributionID will contain some value if the close alert is called from
	 * alert list screen
	 * 
	 * @param env
	 * @param docIP
	 * @param hmap
	 * @return
	 */
	private boolean validateAndPlaceICBSRInitiatedReq(YFSEnvironment env, Document docIP, HashMap<String, String> hmap, String distributionID) {
		logger.verbose("@@@@@ Entering NWCGCheckAndCloseAlert::validateAndPlaceICBSRInitiatedReq @@@@@");
		boolean retnVal = true;
		NodeList nlInboxRef = docIP.getElementsByTagName(NWCGConstants.INBOX_REFERENCES);
		String errorDesc = NWCGConstants.EMPTY_STRING;
		if (distributionID != null && distributionID.length() > 2) {
			retnVal = getOBEntry(env, distributionID, hmap);
			if (retnVal) {
				String orderHdrKey = hmap.get(NWCGConstants.ORDER_HEADER_KEY);
				hmap.clear();
				logger.verbose("@@@@@ NWCGCheckAndCloseAlert::validateAndPlaceICBSRInitiatedReq, OrderHeaderKey : " + orderHdrKey);
				retnVal = getDtlsAndPlaceICBSRInitiatedReq(env, orderHdrKey, hmap);
			}
		} else if (nlInboxRef != null && nlInboxRef.getLength() > 0) {
			String distId = ((Element) nlInboxRef.item(0)).getAttribute(NWCGConstants.VALUE_ATTR);
			retnVal = getOBEntry(env, distId, hmap);
			if (retnVal) {
				String orderHdrKey = hmap.get(NWCGConstants.ORDER_HEADER_KEY);
				hmap.clear();
				logger.verbose("@@@@@ NWCGCheckAndCloseAlert::validateAndPlaceICBSRInitiatedReq, OrderHeaderKey : " + orderHdrKey);
				retnVal = getDtlsAndPlaceICBSRInitiatedReq(env, orderHdrKey, hmap);
			}
		} else {
			errorDesc = "Unable to place ICBSR initiated request as Distribution ID is not present";
			hmap.put(NWCGConstants.ERROR_DESC, errorDesc);
			retnVal = false;
		}
		logger.verbose("@@@@@ Exiting NWCGCheckAndCloseAlert::validateAndPlaceICBSRInitiatedReq @@@@@");
		return retnVal;
	}

	/**
	 * This method will make a call to NWCGGetOutboundMessageListService using
	 * Distribution ID. From the output, it will get the OrderHeaderKey and will
	 * set in the hmap. If the service doesn't return any rows, then it will set
	 * the appropriate message in the hashmap attribute
	 * 
	 * @param env
	 * @param distId
	 * @param hmap
	 * @return
	 */
	private boolean getOBEntry(YFSEnvironment env, String distId, HashMap<String, String> hmap) {
		logger.verbose("@@@@@ Entering NWCGCheckAndCloseAlert::getOBEntry @@@@@");
		boolean retnVal = false;
		Document docOP = null;
		try {
			Document docOBEntryIP = XMLUtil.createDocument(NWCGAAConstants.NWCG_OUTBOUND_MSG_ELM);
			Element obEntryElm = docOBEntryIP.getDocumentElement();
			obEntryElm.setAttribute(NWCGConstants.DIST_ID_ATTR, distId);
			if (hmap != null && hmap.size() > 0) {
				Iterator itr = hmap.entrySet().iterator();
				while (itr.hasNext()) {
					Map.Entry entry = (Map.Entry) itr.next();
					obEntryElm.setAttribute((String) entry.getKey(), (String) entry.getValue());
				}
			}
			hmap.clear();
			docOP = CommonUtilities.invokeService(env, NWCGConstants.SVC_GET_OB_MESSAGE_LIST_SERVICE, docOBEntryIP);
			if (docOP != null) {
				NodeList nlOBMsgs = docOP.getElementsByTagName(NWCGAAConstants.NWCG_OUTBOUND_MSG_ELM);
				if (nlOBMsgs != null && nlOBMsgs.getLength() > 0) {
					// Entity Key is present in both the START and LATEST, So picking the first entry
					Element obElm = (Element) nlOBMsgs.item(0);
					hmap.put(NWCGConstants.ORDER_HEADER_KEY, obElm.getAttribute(NWCGAAConstants.ENTITY_KEY));
					hmap.put(NWCGAAConstants.MESSAGE, obElm.getAttribute(NWCGAAConstants.MESSAGE));
					retnVal = true;
				} else {
					retnVal = false;
					String errorDesc = "Distribution ID is not present in ICBSR database";
					hmap.put(NWCGConstants.ERROR_DESC, errorDesc);
				}
			}
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException : " + pce);
			hmap.put(NWCGConstants.ERROR_DESC, pce.getMessage());
			retnVal = false;
			pce.getStackTrace();
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);
			hmap.put(NWCGConstants.ERROR_DESC, e.getMessage());
			retnVal = false;
			e.getStackTrace();
		}
		logger.verbose("@@@@@ Exiting NWCGCheckAndCloseAlert::getOBEntry @@@@@");
		return retnVal;
	}

	/**
	 * This method will make a call to getOrderDetails using OrderHeaderKey. If
	 * the returned output is NULL, then set an appropriate error message and
	 * return false. If the returned output is not null, then get the incident
	 * number and year from Order/Extn element. Get the latest incident details
	 * by calling getIncidentDtls method. If this method returns false, then set
	 * an error message "Incident does not have a primary ROSS account code" and
	 * return false. If it returns true, then update the Order xml with the
	 * incident attributes. Return true.
	 * 
	 * @param env
	 * @param orderHdrKey
	 * @return boolean
	 */
	private boolean getDtlsAndPlaceICBSRInitiatedReq(YFSEnvironment env, String orderHdrKey, HashMap<String, String> hmap) {
		logger.verbose("@@@@@ Entering NWCGCheckAndCloseAlert::getDtlsAndPlaceICBSRInitiatedReq @@@@@");
		String errorDesc = NWCGConstants.EMPTY_STRING;
		boolean retnVal = false;
		try {
			Document docGetOrderDtlsIP = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
			docGetOrderDtlsIP.getDocumentElement().setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHdrKey);
			Document docGetOrderDtlsOP = CommonUtilities.invokeAPI(env, "NWCGCheckAndCloseAlert_getOrderDetails", NWCGConstants.API_GET_ORDER_DETAILS, docGetOrderDtlsIP);
			if (docGetOrderDtlsOP != null) {
				Element elmGetOrderDtlsRootOP = docGetOrderDtlsOP.getDocumentElement();
				NodeList nlRootChildNodes = elmGetOrderDtlsRootOP.getChildNodes();
				if (nlRootChildNodes != null) {
					boolean obtChildNode = false;
					for (int i = 0; i < nlRootChildNodes.getLength() && !obtChildNode; i++) {
						Element elmChild = (Element) nlRootChildNodes.item(i);
						if (elmChild.getNodeName().equalsIgnoreCase(NWCGConstants.EXTN_ELEMENT)) {
							obtChildNode = true;
							String incidentNo = elmChild.getAttribute(NWCGConstants.INCIDENT_NO);
							String incidentYr = elmChild.getAttribute(NWCGConstants.INCIDENT_YEAR);
							// Make incident details calls and update the following fields in the order xml ExtnROSSFinancialCode, ExtnROSSOwningAgency and ExtnROSSFiscalYear
							logger.verbose("@@@@@ NWCGCheckAndCloseAlert::getDtlsAndPlaceICBSRInitiatedReq, Incident No : " + incidentNo);
							logger.verbose("@@@@@ NWCGCheckAndCloseAlert::getDtlsAndPlaceICBSRInitiatedReq, Incident Year : " + incidentYr);
							boolean obtIncDtls = getIncidentDtls(env, incidentNo, incidentYr, hmap);
							if (!obtIncDtls) {
								errorDesc = "Incident does not have primary financial code. Fix the primary financial code and close the alert";
								hmap.put(NWCGConstants.ERROR_DESC, errorDesc);
								retnVal = false;
								logger.verbose("@@@@@ NWCGCheckAndCloseAlert::getDtlsAndPlaceICBSRInitiatedReq, Error Message : " + errorDesc);
							} else {
								logger.verbose("@@@@@ NWCGCheckAndCloseAlert::getDtlsAndPlaceICBSRInitiatedReq, Obtained incident details");
								// update incident details in order xml
								String extnROSSFinancialCode = elmChild.getAttribute(NWCGConstants.EXTN_ROSS_FIN_CODE_ATTR);
								String extnROSSOwningAgency = elmChild.getAttribute(NWCGConstants.EXTN_ROSS_FIN_CODE_ATTR);
								String extnROSSFiscalYear = elmChild.getAttribute(NWCGConstants.EXTN_ROSS_FIN_CODE_ATTR);
								Document docChgOrderOP = null;
								if ((extnROSSFinancialCode.equalsIgnoreCase(hmap.get(NWCGConstants.EXTN_ROSS_FIN_CODE_ATTR))) && (extnROSSOwningAgency.equalsIgnoreCase(hmap.get(NWCGConstants.EXTN_ROSS_OWNING_AGENCY))) && (extnROSSFiscalYear.equalsIgnoreCase(hmap.get(NWCGConstants.EXTN_ROSS_FISCAL_YEAR)))) {
									docChgOrderOP = docGetOrderDtlsOP;
								} else {
									docChgOrderOP = updateOrder(env, orderHdrKey, hmap);
								}
								// put this xml in the Hashmap
								docChgOrderOP.getDocumentElement().setAttribute(NWCGAAConstants.MESSAGE_TYPE, "CreateRequestAndPlaceReq");
								logger.verbose("@@@@@ NWCGCheckAndCloseAlert::getDtlsAndPlaceICBSRInitiatedReq, Order xml : " + XMLUtil.extractStringFromDocument(docChgOrderOP));
								hmap.put("OrderXML", XMLUtil.extractStringFromDocument(docChgOrderOP));
								retnVal = true;
							}
						} 
					} 
				} 
			} 
			else {
				errorDesc = "Order does not exists with the given OrderHeaderKey. Invalid Order";
				hmap.put(NWCGConstants.ERROR_DESC, errorDesc);
				retnVal = false;
			}
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException : " + pce);
			hmap.put(NWCGConstants.ERROR_DESC, pce.getMessage());
			pce.getStackTrace();
			retnVal = false;
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);
			hmap.put(NWCGConstants.ERROR_DESC, e.getMessage());
			e.getStackTrace();
			retnVal = false;
		}
		logger.verbose("@@@@@ Exiting NWCGCheckAndCloseAlert::getDtlsAndPlaceICBSRInitiatedReq @@@@@");
		return retnVal;
	}

	private Document updateOrder(YFSEnvironment env, String orderHdrKey, HashMap<String, String> hmap) {
		logger.verbose("@@@@@ Entering NWCGCheckAndCloseAlert::updateOrder @@@@@");
		Document docChgOrderOP = null;
		try {
			Document docChgOrderIP = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
			Element elmChgOrder = docChgOrderIP.getDocumentElement();
			elmChgOrder.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHdrKey);
			Element elmOrderExtn = docChgOrderIP.createElement(NWCGConstants.EXTN_ELEMENT);
			elmChgOrder.appendChild(elmOrderExtn);
			elmOrderExtn.setAttribute(NWCGConstants.EXTN_ROSS_FIN_CODE_ATTR, hmap.get(NWCGConstants.EXTN_ROSS_FIN_CODE_ATTR));
			elmOrderExtn.setAttribute(NWCGConstants.EXTN_ROSS_OWNING_AGENCY, hmap.get(NWCGConstants.EXTN_ROSS_OWNING_AGENCY));
			elmOrderExtn.setAttribute(NWCGConstants.EXTN_ROSS_FISCAL_YEAR, hmap.get(NWCGConstants.EXTN_ROSS_FISCAL_YEAR));
			docChgOrderOP = CommonUtilities.invokeAPI(env, "NWCGCheckAndCloseAlert_getOrderDetails", NWCGConstants.API_CHANGE_ORDER, docChgOrderIP);
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException : " + pce);
			pce.getStackTrace();
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);
			e.getStackTrace();
		}
		logger.verbose("@@@@@ Exiting NWCGCheckAndCloseAlert::updateOrder @@@@@");
		return docChgOrderOP;
	}

	/**
	 * This method will create the input xml for incident order and return the
	 * NWCGGetIncidentOrderService output
	 * 
	 * @param env
	 * @param incKey
	 * @return
	 */
	private boolean getIncidentDtls(YFSEnvironment env, String incNo, String incYr, HashMap<String, String> hmap) {
		logger.verbose("@@@@@ Entering NWCGCheckAndCloseAlert::getIncidentDtls @@@@@");
		Document docOPIncidentOrder = null;
		boolean retnVal = false;
		try {
			Document docIPIncidentOrder = XMLUtil.createDocument(NWCGConstants.NWCG_INCIDENT_ORDER);
			Element elmIPIncidentOrder = docIPIncidentOrder.getDocumentElement();
			elmIPIncidentOrder.setAttribute(NWCGConstants.INCIDENT_NO_ATTR, incNo);
			elmIPIncidentOrder.setAttribute(NWCGConstants.YEAR_ATTR, incYr);
			logger.verbose("@@@@@ Input to NWCGGetIncidentOrderService : " + XMLUtil.extractStringFromDocument(docIPIncidentOrder));
			docOPIncidentOrder = CommonUtilities.invokeService(env, NWCGConstants.SVC_GET_INCIDENT_ORDER_SVC, docIPIncidentOrder);
			logger.verbose("@@@@@ NWCGCheckAndCloseAlert::getIncidentDtls, Calling for primary ROSS Acct Codes");
			NWCGChkFinCodeAndUpdateIssue getPrimROSSAcctDtls = new NWCGChkFinCodeAndUpdateIssue();
			retnVal = getPrimROSSAcctDtls.checkPrimROSSAcctCodeAndPopIncData(docOPIncidentOrder, hmap);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);
			e.printStackTrace();
		}
		logger.verbose("@@@@@ Exiting NWCGCheckAndCloseAlert::getIncidentDtls @@@@@");
		return retnVal;
	}

	/**
	 * This method will be called if the input from alert list screen doesn't
	 * pass ExceptionType and InboxReferences fields
	 * 
	 * @param inboxKey
	 * @return HashMap<String, String> Alert Details
	 */
	private HashMap<String, String> getAlertDetails(YFSEnvironment env, String inboxKey) {
		logger.verbose("@@@@@ Entering NWCGCheckAndCloseAlert::getAlertDetails @@@@@");
		HashMap<String, String> hmapAlertDtls = new HashMap<String, String>();
		try {
			Document docIPExcDtls = XMLUtil.createDocument(NWCGConstants.INBOX);
			Element elmIPExcDtls = docIPExcDtls.getDocumentElement();
			elmIPExcDtls.setAttribute(NWCGConstants.INBOX_KEY, inboxKey);
			logger.verbose("@@@@@ NWCGCheckAndCloseAlert::getAlertDetails, Input to " + "getExceptionDetails : " + XMLUtil.extractStringFromDocument(docIPExcDtls));
			Document docOPExcDtls = CommonUtilities.invokeAPI(env, "NWCGCheckAndCloseAlert_getExceptionDetails", "getExceptionDetails", docIPExcDtls);
			if (docOPExcDtls != null) {
				Element elmOPExcDtls = docOPExcDtls.getDocumentElement();
				String exceptionType = elmOPExcDtls.getAttribute(NWCGConstants.EXCEPTION_TYPE);
				hmapAlertDtls.put(NWCGConstants.EXCEPTION_TYPE, exceptionType);
				if (exceptionType.equalsIgnoreCase(NWCGConstants.MSG_CREATE_REQUEST_AND_PLACE_RESP) || exceptionType.equalsIgnoreCase("UpdateNFESResourceRequestResp")) {
					NodeList nlInboxReferences = elmOPExcDtls.getElementsByTagName(NWCGConstants.INBOX_REFERENCES);
					if (nlInboxReferences != null && nlInboxReferences.getLength() > 0) {
						boolean obtReqElm = false;
						for (int i = 0; i < nlInboxReferences.getLength() && !obtReqElm; i++) {
							Element elmInboxRef = (Element) nlInboxReferences.item(i);
							String name = elmInboxRef.getAttribute(NWCGConstants.NAME_ATTR);
							if (name.equalsIgnoreCase(NWCGConstants.DIST_ID_ATTR) || name.equalsIgnoreCase(NWCGConstants.ALERT_DIST_ID)) {
								obtReqElm = true;
								hmapAlertDtls.put(NWCGConstants.DIST_ID_ATTR, elmInboxRef.getAttribute(NWCGConstants.VALUE_ATTR));
							}
						} 
					} 
				} 
			} 
			else {
				logger.verbose("@@@@@ NWCGCheckAndCloseAlert::getAlertDetails, Output from getExceptionDetails is NULL");
			}
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);
			e.printStackTrace();
		}
		logger.verbose("@@@@@ Exiting NWCGCheckAndCloseAlert::getAlertDetails @@@@@");
		return hmapAlertDtls;
	}

	public void handleUpdateNFESResMsg(YFSEnvironment env, Document docIP, String distID) {
		logger.verbose("@@@@@ Entering NWCGCheckAndCloseAlert::getAlertDetails @@@@@");
		HashMap<String, String> hmap = new HashMap<String, String>();
		hmap.put("MessageType", "START");
		boolean retnVal = getOBEntry(env, distID, hmap);
		if (!retnVal) {
			logger.verbose("@@@@@ NWCGCheckAndCloseAlert::handleUpdateNFESResMsg, OB did not returned any data for distribution ID : " + distID);
		}
		try {
			String inputXml = hmap.get("Message");
			logger.verbose("@@@@@ Input XML to " + NWCGConstants.SVC_POST_OB_MSG_SVC + ", " + inputXml);
			CommonUtilities.invokeService(env, NWCGConstants.SVC_POST_OB_MSG_SVC, inputXml);
		} catch (Exception ex) {
			logger.error("!!!!! Caught General Exception : " + ex);
		}
		return;
	}
}