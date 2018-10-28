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

/**
 * 
 * @author Oxford Consulting Group
 * @version 1.0
 * @date November 6, 2013
 */
public class NWCGChkFinCodeAndUpdateIssue implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGChkFinCodeAndUpdateIssue.class);
	
	private Properties myProperties = null;
	
	/**
	 * 
	 * @param arg0
	 * @throws Exception
	 */
	public void setProperties(Properties arg0) throws Exception {
		this.myProperties = arg0;
	}
	
	/**
	 * Invoked by the NWCGChkIncidentICBSRCodesAndUpdt service, which is invoked
	 * on the page load event of the Issue Details UI if none of the account codes
	 * are set on the incident. Copies account codes from the NWCGIncidentOrder node
	 * to the Order/Extn node
	 * 
	 * @param env
	 * @param inputDoc
	 * @return
	 * @throws Exception YFSException - Ensure that the NEW_INC_BY_ICBSR hold type is configured
	 * 			for the NWCG enterprise and 0001 document type.		
	 */
	public Document chkIncidentICBSRCodesAndUpdtIssue(YFSEnvironment env, Document inputDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGChkFinCodeAndUpdateIssue::chkIncidentICBSRCodesAndUpdtIssue @@@@@");
		Element elmIPDoc = inputDoc.getDocumentElement();
		String incKey = elmIPDoc.getAttribute(NWCGConstants.INCIDENT_KEY);
		String orderHdrKey = elmIPDoc.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		
		if (StringUtil.isEmpty(incKey) || StringUtil.isEmpty(orderHdrKey)) {
			return inputDoc;
		}
		String shipNode = elmIPDoc.getAttribute(NWCGConstants.SHIP_NODE);
		if (StringUtil.isEmpty(shipNode)){
			shipNode = getShipNode(env, orderHdrKey);
		}
		String ownerAgency = CommonUtilities.getOwnerAgencyFromShippingCacheID(env, shipNode);
		
		Document incidentDoc = getIncidentDetails(env, incKey);
		if (incidentDoc == null) {
			return inputDoc;
		}
		
		Element incDocElm = incidentDoc.getDocumentElement();
		String incFsAcctCode = incDocElm.getAttribute(NWCGConstants.INCIDENT_FS_ACCT_CODE);
		String incBlmAcctCode = incDocElm.getAttribute(NWCGConstants.INCIDENT_BLM_ACCT_CODE);
		String incOtherAcctCode = incDocElm.getAttribute(NWCGConstants.INCIDENT_OTHER_ACCT_CODE);
		String incOverrideCode = incDocElm.getAttribute(NWCGConstants.INCIDENT_OVERRIDECODE_ATTR);	
		String wbs = incDocElm.getAttribute(NWCGConstants.BILL_TRANS_WBS);
		String costCenter = incDocElm.getAttribute(NWCGConstants.BILL_TRANS_COST_CENTER);
		String functionalArea = incDocElm.getAttribute(NWCGConstants.BILL_TRANS_FUNCTIONAL_AREA);
		
		// ICBS will not auto-resolve the NEW_INC_BY_ICBSR hold type on an Issue unless FS+override, or BLM + FBMS values, or Other+Override is set on the Issue's Incident. ICBS will not auto-resolve the INC_NO_ICBS_CODE hold type on an Incident to Incident Transfer Issue unless FS+override, or BLM + FBMS values, or Other+Override is set
		boolean hasValidFS = false;
		boolean hasValidBLM = false;
		boolean hasValidOther = false;
		if (StringUtil.isEmpty(incFsAcctCode) && StringUtil.isEmpty(incOverrideCode)) 
			hasValidFS = false;		
		else {
			if (ownerAgency.equalsIgnoreCase(NWCGConstants.FS_OWNER_AGENY)){
				hasValidFS = true;
			}
			else {
				hasValidFS = false;
			}
		}
		if (StringUtil.isEmpty(incBlmAcctCode) || incBlmAcctCode.length() < 5) 
			hasValidBLM = false;		
		else {
			if (ownerAgency.equalsIgnoreCase(NWCGConstants.BLM_OWNER_AGENCT)){
				hasValidBLM = true;		
			}
			else {
				hasValidBLM = false;		
			}
		}
		if (StringUtil.isEmpty(incOtherAcctCode) || StringUtil.isEmpty(incOverrideCode)) 
			hasValidOther = false;		
		else {
			if (ownerAgency.equalsIgnoreCase(NWCGConstants.OTHER_OWNER_AGENY)){
				hasValidOther = true;		
			}
			else {
				hasValidOther = false;		
			}
		}
		if (!hasValidFS && !hasValidBLM && !hasValidOther) 
			return inputDoc;
		// Create the Order/ document for changeOrder API later on
		Document changeOrderIp = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
		Element chgOrDocElm = changeOrderIp.getDocumentElement();
		chgOrDocElm.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHdrKey);
		chgOrDocElm.setAttribute(NWCGConstants.ACTION, NWCGConstants.MODIFY);
		chgOrDocElm.setAttribute(NWCGConstants.OVERRIDE, NWCGConstants.YES);
		chgOrDocElm.setAttribute("ModificationReasonCode", "Miscellaneous");
		chgOrDocElm.setAttribute("ModificationReasonText", "ICBS copied Incident ICBS Account Code data to the Issue after valid codes were found on this Issue's Incident.");
		chgOrDocElm.setAttribute("ModificationReference1", "Time: "+CommonUtilities.getXMLCurrentTime());
		chgOrDocElm.setAttribute("ModificationReference2", "Login ID: "+env.getUserId());
		
		// Create the Order/Extn element
		Element extnElm = changeOrderIp.createElement(NWCGConstants.EXTN_ELEMENT);
		chgOrDocElm.appendChild(extnElm);		
		
		// Create the Order/OrderHoldTypes element
		Element orderHoldTypesElm = changeOrderIp.createElement(NWCGConstants.ORDER_HOLD_TYPES);
		chgOrDocElm.appendChild(orderHoldTypesElm);
		
		// Create the Order/OrderHoldTypes/OrderHoldType element
		Element orderHoldTypeElm = changeOrderIp.createElement(NWCGConstants.ORDER_HOLD_TYPE);
		orderHoldTypesElm.appendChild(orderHoldTypeElm);
		
		// Set the NEW_INC_CREATED hold type as resolved
		orderHoldTypeElm.setAttribute(NWCGConstants.HOLD_TYPE_ATTR, NWCGConstants.NWCG_HOLD_TYPE_2_NEW_INC_CREATED);
		orderHoldTypeElm.setAttribute(NWCGConstants.STATUS_ATTR, NWCGConstants.HOLD_TYPE_RESOLVED_STATUS);
		StringBuffer sb = new StringBuffer("Automatically resolved since account codes and FBMS values have been entered on the Incident");
		orderHoldTypeElm.setAttribute(NWCGConstants.HOLD_TYPE_REASON_TEXT_ATTR, sb.toString());
		orderHoldTypeElm.setAttribute("ResolverUserId", env.getUserId());
		
		// Copy the Incident level codes to the issue
		// FS --> FS acct code, ORcode, SA, SAORcode
		// BLM -> BLM acct code, SA
		// Othr-> Oth acct coce, SA
		if (hasValidFS) {
			extnElm.setAttribute(NWCGConstants.EXTN_FS_ACCT_CODE, incFsAcctCode);
			extnElm.setAttribute(NWCGConstants.EXTN_SHIP_ACCT_CODE, incFsAcctCode);
			if (!StringUtil.isEmpty(incOverrideCode)) {
				extnElm.setAttribute(NWCGConstants.EXTN_OVERRIDE_CODE, incOverrideCode);				
				extnElm.setAttribute(NWCGConstants.EXTN_SA_OVERRIDE_CODE, incOverrideCode);
			}
		}
		if (hasValidOther) {
			extnElm.setAttribute(NWCGConstants.EXTN_OTHER_ACCT_CODE, incOtherAcctCode);
			extnElm.setAttribute(NWCGConstants.EXTN_SHIP_ACCT_CODE, incOtherAcctCode);
			// Add the To Override Code if set
			extnElm.setAttribute(NWCGConstants.EXTN_OVERRIDE_CODE, incOverrideCode);				
			extnElm.setAttribute(NWCGConstants.EXTN_SA_OVERRIDE_CODE, incOverrideCode);
		}
		if (hasValidBLM) {
			// Set the ExtnBlm on the Order/Extn and Shipping Acct Code
			extnElm.setAttribute(NWCGConstants.EXTN_BLM_ACCT_CODE, incBlmAcctCode);
			extnElm.setAttribute(NWCGConstants.EXTN_SHIP_ACCT_CODE, incBlmAcctCode);
			
			// Set FBMS values on the Inc2Inc Order Header / Extn
			extnElm.setAttribute(NWCGConstants.FBMS_COSTCENTER_EXTN_ATTR, costCenter);
			extnElm.setAttribute(NWCGConstants.FBMS_FUNCTIONALAREA_EXTN_ATTR, functionalArea);
			extnElm.setAttribute(NWCGConstants.FBMS_WBS_EXTN_ATTR, wbs);
			
			// Add the To Override Code if set
			if (!StringUtil.isEmpty(incOverrideCode)) {
				extnElm.setAttribute(NWCGConstants.EXTN_SA_OVERRIDE_CODE, incOverrideCode);					
				extnElm.setAttribute(NWCGConstants.EXTN_OVERRIDE_CODE, incOverrideCode);		
			}				
		}
		else {
			// Set FBMS values on the  Order Header / Extn
			extnElm.setAttribute(NWCGConstants.EXTN_BLM_ACCT_CODE, NWCGConstants.EMPTY_STRING);
			extnElm.setAttribute(NWCGConstants.FBMS_COSTCENTER_EXTN_ATTR, NWCGConstants.EMPTY_STRING);
			extnElm.setAttribute(NWCGConstants.FBMS_FUNCTIONALAREA_EXTN_ATTR, NWCGConstants.EMPTY_STRING);
			extnElm.setAttribute(NWCGConstants.FBMS_WBS_EXTN_ATTR, NWCGConstants.EMPTY_STRING);
			chgOrDocElm.setAttribute("ModificationReasonCode", "Miscellaneous");
			chgOrDocElm.setAttribute("ModificationReasonText", "A valid BLM Account Code was not found on the Issue's Incident.");
		}

		Document apiOutputDoc = null;
		Document changeOrderOutputDoc = null;
		try {
			Document opTemplate = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
			Element extnElmOpTmplte = opTemplate.createElement(NWCGConstants.EXTN_ELEMENT);
			Element personInfoShipTo = opTemplate.createElement(NWCGConstants.PERSON_INFO_SHIPTO);
			Element personInfoBillTo = opTemplate.createElement(NWCGConstants.PERSON_INFO_BILLTO);
			opTemplate.getDocumentElement().appendChild(extnElmOpTmplte);
			opTemplate.getDocumentElement().appendChild(personInfoShipTo);
			opTemplate.getDocumentElement().appendChild(personInfoBillTo);
			logger.verbose("@@@@@ changeOrderIp " + XMLUtil.getXMLString(changeOrderIp));
			changeOrderOutputDoc = CommonUtilities.invokeAPI(env, opTemplate, NWCGConstants.API_CHANGE_ORDER, changeOrderIp);
			logger.verbose("@@@@@ changeOrderOutputDoc " + XMLUtil.getXMLString(changeOrderOutputDoc));
		}
		catch (Exception e) {
			//logger.printStackTrace(e);
			throw e;
		}
		
		//Now close the Incident Success alert (could either be in NWCG_INCIDENT_RADIOS_SUCCESS 
		//or NWCG_INCIDENT_SUCCESS alert queue)
		Document getExceptionListDoc = XMLUtil.createDocument(NWCGConstants.INBOX);
		Element getExceptionListDocElm = getExceptionListDoc.getDocumentElement();
		getExceptionListDocElm.setAttribute(NWCGConstants.STATUS_ATTR, "OPEN");
		
		Element inboxReferencesListElm = getExceptionListDoc.createElement(NWCGConstants.INBOX_REFERENCES_LIST);
		Element inboxReferenceElm = getExceptionListDoc.createElement(NWCGConstants.INBOX_REFERENCES);
		
		getExceptionListDocElm.appendChild(inboxReferencesListElm);
		inboxReferencesListElm.appendChild(inboxReferenceElm);
		
		inboxReferenceElm.setAttribute(NWCGConstants.NAME_ATTR, "Incident Detail Link");
		inboxReferenceElm.setAttribute(NWCGConstants.VALUE_ATTR, "javascript:openAlertLink('NWCGIncident.detail','NWCGIncidentOrder','IncidentKey','"+incKey+"');");
		inboxReferenceElm.setAttribute(NWCGConstants.REFERENCE_TYPE, NWCGConstants.REFERENCE_TYPE_URL);		
		
		Document getExceptionListOpTemplate = XMLUtil.createDocument(NWCGConstants.INBOX_LIST);
		Element outputTemplateDocElm = getExceptionListOpTemplate.getDocumentElement();
		Element opTemplateQueueElm = getExceptionListOpTemplate.createElement(NWCGConstants.QUEUE);
		Element inboxElm = getExceptionListOpTemplate.createElement(NWCGConstants.INBOX);
		outputTemplateDocElm.appendChild(inboxElm);
		outputTemplateDocElm.appendChild(opTemplateQueueElm);
		
		inboxElm.setAttribute(NWCGConstants.INBOX_KEY, NWCGConstants.EMPTY_STRING);
		inboxElm.setAttribute(NWCGConstants.QUEUE_KEY, NWCGConstants.EMPTY_STRING);
		inboxElm.setAttribute(NWCGConstants.ALERT_DESC, NWCGConstants.EMPTY_STRING);
		inboxElm.setAttribute(NWCGConstants.DETAIL_DESCRIPTION, NWCGConstants.EMPTY_STRING);
		
		opTemplateQueueElm.setAttribute(NWCGConstants.QUEUE_KEY, NWCGConstants.EMPTY_STRING);
		opTemplateQueueElm.setAttribute(NWCGConstants.QUEUE_ID, NWCGConstants.EMPTY_STRING);
		opTemplateQueueElm.setAttribute("QueueName", NWCGConstants.EMPTY_STRING);
		
		try {
			apiOutputDoc = CommonUtilities.invokeAPI(env, getExceptionListOpTemplate, NWCGConstants.API_GET_EXCEPTION_LIST, getExceptionListDoc);
		}
		catch (Exception e) {
			//logger.printStackTrace(e);
			throw e;
		}
		
		if (apiOutputDoc != null) {
			NodeList inboxRecordNL = apiOutputDoc.getElementsByTagName(NWCGConstants.INBOX);
			for (int i = 0; i < inboxRecordNL.getLength(); i++) {
				Node curNode = inboxRecordNL.item(i);
				Element curInboxElm = ((curNode != null) && (curNode instanceof Element)) ? (Element) curNode : null;
				if (curInboxElm == null) continue;
				String curInboxKey = curInboxElm.getAttribute(NWCGConstants.INBOX_KEY);
				String curDetailDescription = curInboxElm.getAttribute(NWCGConstants.DETAIL_DESCRIPTION);
				if (!StringUtil.isEmpty(curDetailDescription)) {
					if (curDetailDescription.startsWith("New Incident")) {
						if (!StringUtil.isEmpty(curInboxKey)) { 
							closeException(env, curInboxKey, "Automatically resolved by ICBSR, detected valid ICBSR Account Codes on Incident.");
						}
					}
					else if (curDetailDescription.startsWith("Bundle")) {
						if (!StringUtil.isEmpty(curInboxKey)) {
							updateException(env, curInboxKey, "Incident has been updated with valid ICBSR Account Codes. This Issue may proceed with fulfillment.");
						}
					}
				}
			}
		}
		logger.verbose("@@@@@ Exiting NWCGChkFinCodeAndUpdateIssue::chkIncidentICBSRCodesAndUpdtIssue @@@@@");
		return changeOrderOutputDoc;
	}
	
	/**
	 * 
	 * @param env
	 * @param inboxKey
	 * @param reason
	 */
	private void updateException (YFSEnvironment env, String inboxKey, String reason) {
		logger.verbose("@@@@@ Entering NWCGChkFinCodeAndUpdateIssue::updateException @@@@@");
		String queueKeyToUse = NWCGConstants.EMPTY_STRING;
		//Call changeException to add an inbox reference of type COMMENT denoting why and how and when the alert was updated.	
		try {
			//Defaults to the Incident Successs Queue if not provided in the input.
			queueKeyToUse = getQueueKeyForInboxRecord(env, inboxKey);
			if (StringUtil.isEmpty(queueKeyToUse))
				queueKeyToUse = "200907021048304004968";
			Document changeExceptionIP = XMLUtil.createDocument(NWCGConstants.INBOX);
			Element changeExceptionDocelm = changeExceptionIP.getDocumentElement();
			changeExceptionDocelm.setAttribute(NWCGConstants.INBOX_KEY, inboxKey);
			changeExceptionDocelm.setAttribute(NWCGConstants.ENTERPRISE_KEY, NWCGConstants.ENTERPRISE_CODE);
			changeExceptionDocelm.setAttribute(NWCGConstants.QUEUE_KEY, queueKeyToUse);
			if (!StringUtil.isEmpty(reason)) {
				Element inboxReferencesList = changeExceptionIP.createElement(NWCGConstants.INBOX_REFERENCES_LIST);
				Element inboxReferences = changeExceptionIP.createElement(NWCGConstants.INBOX_REFERENCES);
				inboxReferences.setAttribute(NWCGConstants.NAME_ATTR, "Comment20");
				inboxReferences.setAttribute(NWCGConstants.VALUE_ATTR, reason);
				inboxReferences.setAttribute(NWCGConstants.REFERENCE_TYPE, NWCGConstants.REFERENCE_TYPE_COMMENT);
				inboxReferences.setAttribute(NWCGConstants.INBOX_KEY, inboxKey);
				inboxReferencesList.appendChild(inboxReferences);
				changeExceptionDocelm.appendChild(inboxReferencesList);				
			}
			CommonUtilities.invokeAPI(env, NWCGConstants.API_CHANGE_EXCEPTION, changeExceptionIP);
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException : " + pce);
		} catch (TransformerException te) {
			logger.error("!!!!! Caught TransformerException : " + te);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);
		}
		logger.verbose("@@@@@ Exiting NWCGChkFinCodeAndUpdateIssue::updateException @@@@@");
	}
	
	/**
	 * This method will create the required input for resolveException and makes an 
	 * API call through CommonUtilities
	 * @param env
	 * @param inboxKey
	 * @param reason String The reason why the alert is being resolved.
	 * @return Document
	 */
	private Document closeException (YFSEnvironment env, String inboxKey, String reason) {	
		logger.verbose("@@@@@ Entering NWCGChkFinCodeAndUpdateIssue::closeException @@@@@");
		Document docResExcOP = null;		
		String queueKeyToUse = NWCGConstants.EMPTY_STRING;
		
		//First call changeException to add an inbox reference of type COMMENT
		//denoting why and how and when the alert was auto resolved.		
		try {
			//Defaults to the Incident Successs Queue if not provided in the input.
			queueKeyToUse = getQueueKeyForInboxRecord(env, inboxKey);
			if (StringUtil.isEmpty(queueKeyToUse))
				queueKeyToUse = "200907021048304004968";
			
			Document changeExceptionIP = XMLUtil.createDocument(NWCGConstants.INBOX);
			Element changeExceptionDocelm = changeExceptionIP.getDocumentElement();
			
			changeExceptionDocelm.setAttribute(NWCGConstants.INBOX_KEY, inboxKey);
			changeExceptionDocelm.setAttribute(NWCGConstants.ENTERPRISE_KEY, NWCGConstants.ENTERPRISE_CODE);
			changeExceptionDocelm.setAttribute(NWCGConstants.QUEUE_KEY, queueKeyToUse);
			if (!StringUtil.isEmpty(reason)) {
				Element inboxReferencesList = changeExceptionIP.createElement(NWCGConstants.INBOX_REFERENCES_LIST);
				Element inboxReferences = changeExceptionIP.createElement(NWCGConstants.INBOX_REFERENCES);
				
				inboxReferences.setAttribute(NWCGConstants.NAME_ATTR, "Comment10");
				inboxReferences.setAttribute(NWCGConstants.VALUE_ATTR, reason);
				inboxReferences.setAttribute(NWCGConstants.REFERENCE_TYPE, NWCGConstants.REFERENCE_TYPE_COMMENT);
				inboxReferences.setAttribute(NWCGConstants.INBOX_KEY, inboxKey);
				inboxReferencesList.appendChild(inboxReferences);
				changeExceptionDocelm.appendChild(inboxReferencesList);				
			}
			CommonUtilities.invokeAPI(env, NWCGConstants.API_CHANGE_EXCEPTION, changeExceptionIP);
				
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
			elmDocResExcIP.setAttribute(NWCGConstants.IGNORE_HOOK_ERRORS, NWCGConstants.YES);
			
			elmInbox.setAttribute(NWCGConstants.INBOX_KEY, inboxKey);
			elmInbox.setAttribute(NWCGConstants.ENTERPRISE_KEY, NWCGConstants.ENTERPRISE_CODE);			
			elmInbox.setAttribute(NWCGConstants.QUEUE_KEY, queueKeyToUse);
			
			elmDocResExcIP.appendChild(elmInbox);
			
			docResExcOP = CommonUtilities.invokeAPI(env, NWCGConstants.API_RESOLVE_EXCEPTION, docResExcIP);
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException : " + pce);
		} catch (TransformerException te) {
			logger.error("!!!!! Caught TransformerException : " + te);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);
		}		
		logger.verbose("@@@@@ Exiting NWCGChkFinCodeAndUpdateIssue::closeException @@@@@");
		return docResExcOP;
	}
	
	/**
	 * 
	 * @param env
	 * @param inboxKey
	 * @return
	 * @throws Exception
	 */
	private String getQueueKeyForInboxRecord(YFSEnvironment env, String inboxKey) throws Exception {
		logger.verbose("@@@@@ Entering NWCGChkFinCodeAndUpdateIssue::getQueueKeyForInboxRecord @@@@@");
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
			apiOutputDoc = CommonUtilities.invokeAPI(env, getExceptionDetailsOpTemplate, NWCGConstants.API_GET_EXCEPTION_DETAILS, getExceptionDetailsDoc);
		}
		catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);
			throw e;
		}
		
		if (apiOutputDoc != null) {
			Element apiOutputDocElm = apiOutputDoc.getDocumentElement();
			if (apiOutputDocElm != null) {
				queueKey = apiOutputDocElm.getAttribute(NWCGConstants.QUEUE_KEY);
			}
		}		
		logger.verbose("@@@@@ Exiting NWCGChkFinCodeAndUpdateIssue::getQueueKeyForInboxRecord @@@@@");
		return queueKey;
	}
	
	private Document getIncidentDetails (YFSEnvironment env, String incidentKey) throws Exception {
		logger.verbose("@@@@@ Entering NWCGChkFinCodeAndUpdateIssue::getIncidentDetails @@@@@");
		String getIncidentOrderInput = 
			"<NWCGIncidentOrder IncidentKey=\""+incidentKey+"\"/>";
		Document apiOutputDoc = null;
		try {
			apiOutputDoc = CommonUtilities.invokeService(env, NWCGConstants.SVC_GET_INCIDENT_ORDER_DTLS_ONLY_SVC, getIncidentOrderInput);
		}
		catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);
			throw e;
		}			
		logger.verbose("@@@@@ Exiting NWCGChkFinCodeAndUpdateIssue::getIncidentDetails @@@@@");
		return apiOutputDoc;
	}
	
	/**
	 * This will check if primary financial code exists or not. If it is not present,
	 * then it will return a document with IsPrimaryROSSFinAcctCode=N. If it is present
	 * then it will resolve the hold on the issue and return IsPrimaryROSSFinAcctCode=Y
	 * Expected input format
	 * <Order OrderHeaderKey="" IncidentKey="" HoldDueToFinCode=""/>
	 * Output should mention whether the primary financial code is still present or not
	 * @param env
	 * @param inputDoc
	 * @return
	 * @throws Exception
	 */
	public Document chkFinCodeAndUpdtIssue(YFSEnvironment env, Document inputDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGChkFinCodeAndUpdateIssue::chkFinCodeAndUpdtIssue @@@@@");
		Element elmIPDoc = inputDoc.getDocumentElement();
		String incKey = elmIPDoc.getAttribute(NWCGConstants.INCIDENT_KEY);
		
		Document docOPPrimRossAcctCodes = null;
		docOPPrimRossAcctCodes = getPrimaryRossAcctCodeDtls(env, incKey);
		
		Document opDoc = XMLUtil.createDocument("NWCGROSSAcctCodes");
		Element elmOPDoc = opDoc.getDocumentElement();
		if (docOPPrimRossAcctCodes == null){
			elmOPDoc.setAttribute("IsPrimaryROSSFinAcctCode", NWCGConstants.NO);
			return opDoc;
		}
		else {
			NodeList nlPrimRossAcctCodes = docOPPrimRossAcctCodes.getDocumentElement().getElementsByTagName("NWCGRossAccountCodes");
			if (nlPrimRossAcctCodes != null && nlPrimRossAcctCodes.getLength() > 0){
				elmOPDoc.setAttribute("IsPrimaryROSSFinAcctCode", NWCGConstants.YES);
				String orderHdrKey = elmIPDoc.getAttribute(NWCGConstants.ORDER_HEADER_KEY);

				//This field is passed as part of 'On click of Schedule and Release'
				String holdDueToFinCode = elmIPDoc.getAttribute("HoldDueToFinCode");
				if (holdDueToFinCode != null && holdDueToFinCode.equalsIgnoreCase(NWCGConstants.NO)){
					// No need to remove the financial hold as there is no hold on this issue
					logger.verbose("@@@@@ Issue is not on hold, so not doing anything");
				}
				else {
					removeFinCodeHoldOnIssue(env, orderHdrKey);
				}
			}
			else {
				elmOPDoc.setAttribute("IsPrimaryROSSFinAcctCode", NWCGConstants.NO);				
			}
		}
		logger.verbose("@@@@@ Exiting NWCGChkFinCodeAndUpdateIssue::chkFinCodeAndUpdtIssue @@@@@");
		return opDoc;
	}
	
	/**
	 * This method will create the input xml for incident order and return the 
	 * NWCGGetROSSAccountCodesListService output
	 * @param env
	 * @param incKey
	 * @return
	 */
	private Document getPrimaryRossAcctCodeDtls(YFSEnvironment env, String incKey){
		logger.verbose("@@@@@ Entering NWCGChkFinCodeAndUpdateIssue::getPrimaryRossAcctCodeDtls @@@@@");
		Document docOPPrimRossFinCode = null;
		try {
			Document docIPRossAcctCodes = XMLUtil.createDocument("NWCGROSSAccountCodes");
			Element elmIPRossAcctCodes = docIPRossAcctCodes.getDocumentElement();
			elmIPRossAcctCodes.setAttribute("IncidentOrderKey", incKey);
			elmIPRossAcctCodes.setAttribute(NWCGConstants.PRIMARY_INDICATOR, "true");
			docOPPrimRossFinCode = CommonUtilities.invokeService(env, NWCGConstants.SVC_GET_ROSS_ACCT_CODES_LIST_SVC, docIPRossAcctCodes);
		}
		catch (Exception e){
			logger.error("!!!!! Caught General Exception : " + e);
		}
		logger.verbose("@@@@@ Exiting NWCGChkFinCodeAndUpdateIssue::getPrimaryRossAcctCodeDtls @@@@@");
		return docOPPrimRossFinCode;
	}
	
	/**
	 * This method will build the order input to resolve NULL_PRIM_FIN_CODE
	 * hold.
	 * @param env
	 * @param orderHdrKey
	 * @throws Exception
	 */
	private void removeFinCodeHoldOnIssue(YFSEnvironment env, String orderHdrKey) throws Exception{
		logger.verbose("@@@@@ Entering NWCGChkFinCodeAndUpdateIssue::removeFinCodeHoldOnIssue @@@@@");
		try {
			Document docOrder = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
			Element elmDoc = docOrder.getDocumentElement();
			elmDoc.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHdrKey);
			Element elmOrderHoldTypes = docOrder.createElement("OrderHoldTypes");
			elmDoc.appendChild(elmOrderHoldTypes);
			Element elmOrderHoldType = docOrder.createElement("OrderHoldType");
			elmOrderHoldTypes.appendChild(elmOrderHoldType);
			elmOrderHoldType.setAttribute("HoldType", "NULL_PRIM_FIN_CODE");
			elmOrderHoldType.setAttribute("ReasonText", "Incident has primary financial code");
			elmOrderHoldType.setAttribute("ResolveUserId", env.getUserId());
			elmOrderHoldType.setAttribute(NWCGConstants.STATUS_ATTR, "1300");
			CommonUtilities.invokeAPI(env, "changeOrder", docOrder);
		}
		catch(ParserConfigurationException pce){
			logger.error("!!!!! Caught ParserConfigurationException : " + pce);
		}
		catch(Exception e){
			logger.error("!!!!! Caught General Exception : " + e);
			e.getStackTrace();
			throw e;
		}
		logger.verbose("@@@@@ Exiting NWCGChkFinCodeAndUpdateIssue::removeFinCodeHoldOnIssue @@@@@");
	}
	
	/**
	 * This method is called from AJAX call on click of 'Schedule and Release'.
	 * This method makes NWCGIncidentGetOrderDetails service call. It will check primary ross account
	 * code and will update the hold on issue. It will also get the incident related fields
	 * from NWCGIncident and will update the issue. Both the updates mentioned here will happen
	 * in a single changeOrder call.
	 * Input format will be
	 * <Order OrderHeaderKey="" IncidentKey="" HoldDueToFinCode=""/>
	 * @param env
	 * @param inputDoc
	 * @return
	 * @throws Exception
	 */
	public Document getIncDtlsAndUpdtIssue(YFSEnvironment env, Document inputDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGChkFinCodeAndUpdateIssue::getIncDtlsAndUpdtIssue @@@@@");
		Element elmIPDoc = inputDoc.getDocumentElement();
		String orderHdrKey = elmIPDoc.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		String incKey = elmIPDoc.getAttribute(NWCGConstants.INCIDENT_KEY);
		String holdDueToFinCode = elmIPDoc.getAttribute("HoldDueToFinCode");
		Document docIncidentOrder = getIncidentDtls(env, incKey);
		boolean isPrimaryROSSAcctCodePresent = true;
		HashMap<String, String> hmap = new HashMap<String, String>();
		isPrimaryROSSAcctCodePresent = checkPrimROSSAcctCodeAndPopIncData(docIncidentOrder, hmap);
		// Update the Order. Check the comments for that method for the detailed behavior
		Document docOPOrder = updateOrder(env, orderHdrKey, holdDueToFinCode, isPrimaryROSSAcctCodePresent, hmap);
		logger.verbose("@@@@@ Exiting NWCGChkFinCodeAndUpdateIssue::getIncDtlsAndUpdtIssue @@@@@");
		return docOPOrder;
	}

	/**
	 * This method will create the input xml for incident order and return the 
	 * NWCGGetIncidentOrderService output
	 * @param env
	 * @param incKey
	 * @return
	 */
	private Document getIncidentDtls(YFSEnvironment env, String incKey){
		logger.verbose("@@@@@ Entering NWCGChkFinCodeAndUpdateIssue::getIncidentDtls @@@@@");
		Document docOPIncidentOrder = null;
		try {
			Document docIPIncidentOrder = XMLUtil.createDocument(NWCGConstants.NWCG_INCIDENT_ORDER);
			Element elmIPIncidentOrder = docIPIncidentOrder.getDocumentElement();
			elmIPIncidentOrder.setAttribute(NWCGConstants.INCIDENT_KEY, incKey);
			logger.verbose("@@@@@ Input to NWCGGetIncidentOrderService : " + XMLUtil.extractStringFromDocument(docIPIncidentOrder));
			docOPIncidentOrder = CommonUtilities.invokeService(env, NWCGConstants.SVC_GET_INCIDENT_ORDER_SVC, docIPIncidentOrder);
		}
		catch (Exception e){
			logger.error("!!!!! Caught General Exception : " + e);
		}
		logger.verbose("@@@@@ Exiting NWCGChkFinCodeAndUpdateIssue::getIncidentDtls @@@@@");
		return docOPIncidentOrder;
	}
	
	/**
	 * This method will check if the incident has primary ross acct code defined or not.
	 * If it is present, then it will retrieve the attributes required for order and will
	 * populate in the Hashmap.
	 * This method is also called from NWCGCheckAndCloseAlert class
	 * @param docIncidentOrder
	 * @return
	 */
	public boolean checkPrimROSSAcctCodeAndPopIncData(Document docIncidentOrder, HashMap <String, String> hmap){
		logger.verbose("@@@@@ Entering NWCGChkFinCodeAndUpdateIssue::checkPrimROSSAcctCodeAndPopIncData @@@@@");
		boolean bRtnVal = false;
		NodeList nlRossAcctCodes = docIncidentOrder.getDocumentElement().getElementsByTagName("NWCGRossAccountCodes");
		if (nlRossAcctCodes != null && nlRossAcctCodes.getLength() > 0){
			boolean obtPrimInd = false;
			for (int i=0; i < nlRossAcctCodes.getLength() && !obtPrimInd; i++){
				Element elmRossAcctCode = (Element) nlRossAcctCodes.item(i);
				String primIndVal = elmRossAcctCode.getAttribute(NWCGConstants.PRIMARY_INDICATOR);
				if (primIndVal != null && primIndVal.equalsIgnoreCase("true")){
					obtPrimInd = true;
					// Populate incident attributes required for issue in Hashmap ExtnROSSFinancialCode, ExtnROSSOwningAgency, ExtnROSSFiscalYear for XSL
					String financialCode = elmRossAcctCode.getAttribute("FinancialCode");
					String owningAgency = elmRossAcctCode.getAttribute("OwningAgencyName");
					String fiscalYear = elmRossAcctCode.getAttribute("FiscalYear");
					hmap.put(NWCGConstants.EXTN_ROSS_FIN_CODE_ATTR, financialCode!=null?financialCode:"");
					hmap.put(NWCGConstants.EXTN_ROSS_OWNING_AGENCY, owningAgency!=null?owningAgency:"");
					hmap.put(NWCGConstants.EXTN_ROSS_FISCAL_YEAR, fiscalYear!=null?fiscalYear:"");
					bRtnVal = true;
				}
			}
		}
		else {
			bRtnVal = false;				
		}
		logger.verbose("@@@@@ Exiting NWCGChkFinCodeAndUpdateIssue::checkPrimROSSAcctCodeAndPopIncData @@@@@");
		return bRtnVal;
	}
	
	/**
	 * If primaryROSSAcctCode = true
	 * 	  - Check holdDueToFinCode.
	 * 		- If it is Y (it implies that original call from Schedule And Release has hold on Issue)
	 * 			- Make changeOrder call with OrderHoldType(resolved) and extn attribues
	 * 		- If it is N (it implies that original call from Schedule And Release didn't had hold on Issue)
	 * 			- Make changeOrder call with extn attributes
	 * else if primaryROSSAcctCode = false
	 * 	  - Check holdDueToFinCode
	 * 		- If it is Y
	 * 			- Don't do anything
	 * 		- If it is N
	 * 			- Make a changeOrder call to update the hold (create)
	 */
	private Document updateOrder(YFSEnvironment env, String orderHdrKey, String holdDueToFinCode, boolean isPrimROSSAcctCode, HashMap<String, String> hmap){
		logger.verbose("@@@@@ Entering NWCGChkFinCodeAndUpdateIssue::updateOrder @@@@@");
		Document docOPOrder = null;
		boolean bMakeChgOrderCall = false;
		try {
			Document docIPOrder = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
			if (isPrimROSSAcctCode){
				bMakeChgOrderCall = true;
				Element elmOrderExtn = docIPOrder.createElement(NWCGConstants.EXTN_ELEMENT);
				docIPOrder.appendChild(elmOrderExtn);
				elmOrderExtn.setAttribute(NWCGConstants.EXTN_ROSS_FIN_CODE_ATTR, hmap.get(NWCGConstants.EXTN_ROSS_FIN_CODE_ATTR));
				elmOrderExtn.setAttribute(NWCGConstants.EXTN_ROSS_OWNING_AGENCY, hmap.get(NWCGConstants.EXTN_ROSS_OWNING_AGENCY));
				elmOrderExtn.setAttribute(NWCGConstants.EXTN_ROSS_FISCAL_YEAR, hmap.get(NWCGConstants.EXTN_ROSS_FISCAL_YEAR));

				if (holdDueToFinCode.equalsIgnoreCase(NWCGConstants.YES)){
					// resolve the hold and update the extn attributes
					Element elmOrderHoldTypes = docIPOrder.createElement("OrderHoldTypes");
					docIPOrder.appendChild(elmOrderHoldTypes);
					Element elmOrderHoldType = docIPOrder.createElement("OrderHoldType");
					elmOrderHoldTypes.appendChild(elmOrderHoldType);
					elmOrderHoldType.setAttribute("HoldType", "NULL_PRIM_FIN_CODE");
					elmOrderHoldType.setAttribute("ReasonText", "Incident has primary financial code");
					elmOrderHoldType.setAttribute("ResolveUserId", env.getUserId());
					elmOrderHoldType.setAttribute(NWCGConstants.STATUS_ATTR, "1300");
				}
				else if (holdDueToFinCode.equalsIgnoreCase(NWCGConstants.NO)){
					// There is no hold on the original order, so don't need to resolve it.
				}
			}
			else {
				if (holdDueToFinCode.equalsIgnoreCase(NWCGConstants.YES)){
					// Don't do anything as the incident is still on hold
					bMakeChgOrderCall = false;
				}
				else if (holdDueToFinCode.equalsIgnoreCase(NWCGConstants.NO)){
					bMakeChgOrderCall = true;
					// There is no hold on the original order, so create the hold.
					Element elmOrderHoldTypes = docIPOrder.createElement("OrderHoldTypes");
					docIPOrder.appendChild(elmOrderHoldTypes);
					Element elmOrderHoldType = docIPOrder.createElement("OrderHoldType");
					elmOrderHoldTypes.appendChild(elmOrderHoldType);
					elmOrderHoldType.setAttribute("HoldType", "NULL_PRIM_FIN_CODE");
					elmOrderHoldType.setAttribute("ReasonText", "Incident has primary financial code");
				}
			}
			
			if (bMakeChgOrderCall){
				docOPOrder = CommonUtilities.invokeAPI(env, "changeOrder", docIPOrder);
			}
			else {
			}
		}
		catch (Exception e){
			logger.error("!!!!! Caught General Exception : " + e);
		}
		logger.verbose("@@@@@ Exiting NWCGChkFinCodeAndUpdateIssue::updateOrder @@@@@");
		return docOPOrder;
	}
	
	/**
	 * 
	 * @param env
	 * @param orderHdrKey
	 * @return
	 */
	private String getShipNode(YFSEnvironment env, String orderHdrKey){
		logger.verbose("@@@@@ Entering NWCGChkFinCodeAndUpdateIssue::getShipNode @@@@@");
		String shipNode = "";
		StringBuffer sb = new StringBuffer("<Order OrderHeaderKey=\"");
		sb.append(orderHdrKey);
		sb.append("\"/>");
		Document docOrderDtls = null;
		StringBuffer sbOpTemplate = new StringBuffer("<Order ShipNode=\"\"/>");
		try {
			String inpStr = sb.toString();
			String inpTemplate = sbOpTemplate.toString();
			Document inptDoc = XMLUtil.getDocument(inpStr);
			Document opTemplate = XMLUtil.getDocument(inpTemplate);
			docOrderDtls = CommonUtilities.invokeAPI(env, opTemplate, "getOrderList", inptDoc);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);	
		}
		if (docOrderDtls == null){
		}
		else {
			shipNode = docOrderDtls.getDocumentElement().getAttribute(NWCGConstants.SHIP_NODE);
		}
		logger.verbose("@@@@@ Exiting NWCGChkFinCodeAndUpdateIssue::getShipNode @@@@@");
		return shipNode;
	}
}