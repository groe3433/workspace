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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * Invoked by the NWCGCheckIncICBSAcctCodesAndUpdateXferIssue service, which is invoked
 * on the page load event of the Inc2Inc transfer order UI if none of the account codes
 * are set on the To Incident. Copies account codes from the NWCGIncidentOrder node
 * to the Order/Extn node
 * 
 * @author drodriguez
 * @since USFS ICBS-ROSS BR2 Increment 5
 */
public class NWCGCheckIncICBSAcctCodesAndUpdateXferIssue implements YIFCustomApi {

	private Properties myProperties = null;
	private static Logger logger = Logger.getLogger(NWCGCheckIncICBSAcctCodesAndUpdateXferIssue.class.getName());
	
	public void setProperties(Properties arg0) throws Exception {
		this.myProperties = arg0;
		if (logger.isDebugEnabled()) logger.debug("Set properties"+this.myProperties);
	}
	
	/**
	 * Invoked by the NWCGCheckIncICBSAcctCodesAndUpdateXferIssue service, which is invoked
	 * on the page load event of the Inc2Inc transfer order UI if none of the account codes
	 * are set on the To Incident. Copies account codes from the NWCGIncidentOrder node
	 * to the Order/Extn node
	 * 
	 * @param env
	 * @param inputDoc
	 * @return retDoc The API's returning org.w3c.dom.Document object
	 * @throws Exception YFSException		
	 */
	public Document onPageLoadOfTransferOrderUI(YFSEnvironment env, Document inputDoc) throws Exception {
		logger.verbose("NWCGCheckIncICBSAcctCodesAndUpdateXferIssue Input: " 
				+ XMLUtil.extractStringFromDocument(inputDoc));
		logger.beginTimer("onPageLoadOfTransferOrderUI");
		
		Element inputDocElm = inputDoc.getDocumentElement();
		String orderHdrKey  = inputDocElm.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		
		// Get the To Incident Number and Year
		Document xferOrderDoc = getXferOrderDetails(env, orderHdrKey);
		String toIncNo = NWCGConstants.EMPTY_STRING;
		String toIncYr = NWCGConstants.EMPTY_STRING;
		if (xferOrderDoc != null && xferOrderDoc.getDocumentElement() != null) {
			Element orderExtnElm = XMLUtil.getFirstElementByName(xferOrderDoc.getDocumentElement(),
					NWCGConstants.EXTN_ELEMENT);
			if (orderExtnElm != null) {
				if (!StringUtil.isEmpty(orderExtnElm.getAttribute(NWCGConstants.EXTN_TO_INCIDENT_NO))) {
					toIncNo = orderExtnElm.getAttribute(NWCGConstants.EXTN_TO_INCIDENT_NO);
				}				
				if (!StringUtil.isEmpty(orderExtnElm.getAttribute(NWCGConstants.EXTN_TO_INCIDENT_YR))) {
					toIncYr = orderExtnElm.getAttribute(NWCGConstants.EXTN_TO_INCIDENT_YR);
				}	
			}
		}
		
		if (StringUtil.isEmpty(toIncNo) || StringUtil.isEmpty(toIncYr)) {
			logger.error("Could not obtain OH.TO_INCIDENTNO/YEAR for OHK: "+orderHdrKey);
			return inputDoc;
		}
		
		Document toIncidentDoc = getIncidentOrderOnly(env, toIncNo, toIncYr);
		Element toIncDocElm = toIncidentDoc.getDocumentElement();
		String toIncFsAcctCode = toIncDocElm.getAttribute(NWCGConstants.INCIDENT_FS_ACCT_CODE);
		String toIncBlmAcctCode = toIncDocElm.getAttribute(NWCGConstants.INCIDENT_BLM_ACCT_CODE);
		String toIncOtherAcctCode = toIncDocElm.getAttribute(NWCGConstants.INCIDENT_OTHER_ACCT_CODE);
		String toIncOverrideCode = toIncDocElm.getAttribute(NWCGConstants.INCIDENT_OVERRIDECODE_ATTR);
		String toIncWBS = toIncDocElm.getAttribute(NWCGConstants.BILL_TRANS_WBS);
		String toIncCostCenter = toIncDocElm.getAttribute(NWCGConstants.BILL_TRANS_COST_CENTER);
		String toIncFunctionalArea = toIncDocElm.getAttribute(NWCGConstants.BILL_TRANS_FUNCTIONAL_AREA);
		
		// ICBS will not auto-resolve the INC_NO_ICBS_CODE hold type on an 
		// Incident to Incident Transfer Issue 
		// unless FS+override, or BLM + FBMS values, or Other+Override is set 
		
		boolean toIncidentHasValidFS = false;
		boolean toIncidentHasValidBLM = false;
		boolean toIncidentHasValidOther = false;
		
		if (StringUtil.isEmpty(toIncFsAcctCode) && StringUtil.isEmpty(toIncOverrideCode)) 
			toIncidentHasValidFS = false;		
		else 
			toIncidentHasValidFS = true;		
		
		if (StringUtil.isEmpty(toIncBlmAcctCode) || toIncBlmAcctCode.length() < 5) 
			toIncidentHasValidBLM = false;
		else 
			toIncidentHasValidBLM = true;		
		
		if (StringUtil.isEmpty(toIncOtherAcctCode) || StringUtil.isEmpty(toIncOverrideCode)) 
			toIncidentHasValidOther = false;
		else 
			toIncidentHasValidOther = true;		
		
		if (!toIncidentHasValidFS && !toIncidentHasValidBLM && !toIncidentHasValidOther) {
			logger.error("Destination Incident is missing ICBS Account Codes!");
			return inputDoc;
		}
			
		// Create the Order/ document for changeOrder API later on
		Document changeOrderIp = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
		Element chgOrDocElm = changeOrderIp.getDocumentElement();
		chgOrDocElm.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHdrKey);
		chgOrDocElm.setAttribute(NWCGConstants.ACTION, NWCGConstants.MODIFY);
		chgOrDocElm.setAttribute(NWCGConstants.OVERRIDE, NWCGConstants.YES);
		chgOrDocElm.setAttribute("ModificationReasonCode", "Miscellaneous");
		chgOrDocElm.setAttribute("ModificationReasonText", "Copied Destination Incident ICBS Account Code data " + 
				"to the Incident to Incident Transfer after valid codes were found on the Destination Incident.");
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
		
		// Set the INC_NO_ICBS_CODES hold type as resolved
		orderHoldTypeElm.setAttribute(NWCGConstants.HOLD_TYPE_ATTR, NWCGConstants.NWCG_HOLD_TYPE_4_INC_NO_ICBS_CODES);
		orderHoldTypeElm.setAttribute(NWCGConstants.STATUS_ATTR, NWCGConstants.HOLD_TYPE_RESOLVED_STATUS);
		StringBuffer sb = new StringBuffer("Automatically resolved since account codes and FBMS values have been entered on the To Incident");
		orderHoldTypeElm.setAttribute(NWCGConstants.HOLD_TYPE_REASON_TEXT_ATTR, sb.toString());
		orderHoldTypeElm.setAttribute("ResolverUserId", env.getUserId());
		
		// Copy the Incident level codes to the issue
		// FS --> FS acct code, ORcode, SA, SAORcode
		// BLM -> BLM acct code, SA
		// Othr-> Oth acct coce, SA
		if (toIncidentHasValidFS) 
		{
			extnElm.setAttribute(NWCGConstants.EXTN_TO_FS_ACCT_CODE, toIncFsAcctCode);
			extnElm.setAttribute(NWCGConstants.EXTN_SHIP_ACCT_CODE, toIncFsAcctCode);
			
			extnElm.setAttribute(NWCGConstants.EXTN_TO_OVERRIDE_CODE, toIncOverrideCode);				
			extnElm.setAttribute(NWCGConstants.EXTN_SA_OVERRIDE_CODE, toIncOverrideCode);
		}
		
		if (toIncidentHasValidOther) 
		{
			extnElm.setAttribute(NWCGConstants.EXTN_TO_OTHER_ACCT_CODE, toIncOtherAcctCode);
			extnElm.setAttribute(NWCGConstants.EXTN_SHIP_ACCT_CODE, toIncOtherAcctCode);
			
			// Add the To Override Code if set
			extnElm.setAttribute(NWCGConstants.EXTN_TO_OVERRIDE_CODE, toIncOverrideCode);				
			extnElm.setAttribute(NWCGConstants.EXTN_SA_OVERRIDE_CODE, toIncOverrideCode);
		}
		
		if (toIncidentHasValidBLM) 
		{
			// Set the ExtnBlm on the Order/Extn and Shipping Acct Code
			extnElm.setAttribute(NWCGConstants.EXTN_TO_INCIDENT_BLM_CODE, toIncBlmAcctCode);
			extnElm.setAttribute(NWCGConstants.EXTN_SA_OVERRIDE_CODE, toIncBlmAcctCode);
			
			// Set FBMS values on the Inc2Inc Order Header / Extn
			extnElm.setAttribute(NWCGConstants.FBMS_COSTCENTER_EXTN_ATTR, toIncCostCenter);
			extnElm.setAttribute(NWCGConstants.FBMS_FUNCTIONALAREA_EXTN_ATTR, toIncFunctionalArea);
			extnElm.setAttribute(NWCGConstants.FBMS_WBS_EXTN_ATTR, toIncWBS);
			
			// Add the To Override Code if set
			if (!StringUtil.isEmpty(toIncOverrideCode)) {
				extnElm.setAttribute(NWCGConstants.EXTN_SA_OVERRIDE_CODE, toIncOverrideCode);					
				extnElm.setAttribute(NWCGConstants.EXTN_TO_OVERRIDE_CODE, toIncOverrideCode);		
			}				
		}
		else {
			// Set FBMS values on the  Order Header / Extn
			extnElm.setAttribute(NWCGConstants.EXTN_TO_INCIDENT_BLM_CODE, NWCGConstants.EMPTY_STRING);
			extnElm.setAttribute(NWCGConstants.FBMS_COSTCENTER_EXTN_ATTR, NWCGConstants.EMPTY_STRING);
			extnElm.setAttribute(NWCGConstants.FBMS_FUNCTIONALAREA_EXTN_ATTR, NWCGConstants.EMPTY_STRING);
			extnElm.setAttribute(NWCGConstants.FBMS_WBS_EXTN_ATTR, NWCGConstants.EMPTY_STRING);
			
			chgOrDocElm.setAttribute("ModificationReasonCode", "Miscellaneous");
			chgOrDocElm.setAttribute("ModificationReasonText", "A valid BLM Account Code was not found on the Destination Incident ["+CommonUtilities.getXMLCurrentTime()+"]");
		}
				
		String chgOrderIpStr = XMLUtil.extractStringFromDocument(changeOrderIp);
		logger.verbose("changeOrder Input: "+chgOrderIpStr);
		
		Document changeOrderOutput = null;
		
		try {
			Document opTemplate = XMLUtil.getDocument("<Order><Extn/></Order>");
			//Element extnElmOpTmplte = opTemplate.createElement(NWCGConstants.EXTN_ELEMENT);
			//opTemplate.getDocumentElement().appendChild(extnElmOpTmplte);
			
			changeOrderOutput = CommonUtilities.invokeAPI(env, opTemplate, NWCGConstants.API_CHANGE_ORDER,
					changeOrderIp);
			logger.verbose("changeOrder output: "+ XMLUtil.extractStringFromDocument(changeOrderOutput));
		}
		catch (Exception e) {
			logger.error("ERROR occured while updating the Issue with Incident Account Information");
			e.printStackTrace();
			throw e;
		}
		logger.verbose("changeOrder Output: "+XMLUtil.extractStringFromDocument(changeOrderOutput));
		logger.endTimer("onPageLoadOfTransferOrderUI");
		return changeOrderOutput;
	}
	
	private Document getXferOrderDetails(YFSEnvironment env, String orderHdrKey) throws Exception {
		String getOrderListInput = "<Order OrderHeaderKey=\"" + orderHdrKey + "\"/>";
		String getOrderTemplate = "<Order OrderHeaderKey=\"\" OrderNo=\"\" DocumentType=\"\" EnterpriseCode=\"\"><Extn/></Order>";

		env.setApiTemplate(NWCGConstants.API_GET_ORDER_DETAILS, YFCDocument.parse(getOrderTemplate).getDocument());
		Document orderDetails = CommonUtilities.invokeAPI(env, NWCGConstants.API_GET_ORDER_DETAILS, YFCDocument.parse(getOrderListInput).getDocument());

		// Clear the template
		env.clearApiTemplate(NWCGConstants.API_GET_ORDER_DETAILS);

		return orderDetails;
	}

	private Document getIncidentOrderOnly (YFSEnvironment env, String incidentNo, String incidentYear) throws Exception {
		String getIncidentOrderInput = 
			"<NWCGIncidentOrder IncidentNo=\""+incidentNo+"\" Year=\""+incidentYear+"\"/>";
		Document apiOutputDoc = null;
		try {
			apiOutputDoc = CommonUtilities.invokeService(env, 
					NWCGConstants.SVC_GET_INCIDENT_ORDER_DTLS_ONLY_SVC, getIncidentOrderInput);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while calling NWCGGetIncidentOrderOnlyService for chkIncidentICBSRCodesAndUpdtIssue API");
			throw e;
		}			
		return apiOutputDoc;
	}
}