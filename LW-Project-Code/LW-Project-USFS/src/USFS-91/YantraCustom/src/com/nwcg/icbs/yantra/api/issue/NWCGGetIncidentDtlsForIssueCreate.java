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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGGetIncidentDtlsForIssueCreate implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGGetIncidentDtlsForIssueCreate.class);
	
	public void setProperties(Properties arg0) throws Exception {
	}

	/**
	 * This method will get the incident details for the passed input. If the retrieved incident
	 * is inactive, then it will try to get the incident for which the passed input incident is
	 * LastIncidentNo. It will set the ErrorDesc if the passed input is not active incident.
	 * If the passed incident is active, then it will check if the passed incident has a priamry
	 * ROSS financial code. If it does not have one, then it will set isPrimROSSFinCode to N, else it
	 * will set to Y. Both the custom fields will be used in AJAX callback function 
	 * updatedIncidentDetails
	 * Expected input XML
	 * <NWCGIncidentOrder IncidentNo="" Year=""/>
	 * @param env
	 * @param ipDoc
	 * @return
	 * @throws Exception
	 */
	public Document getIncidentDtls(YFSEnvironment env, Document ipDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGGetOrderListForIncident::getIncidentDtls @@@@@");
		logger.verbose("@@@@@ Input Document : " + XMLUtil.getXMLString(ipDoc));
		String incidentNo = "";
		String incidentYr = "";
		Element elmIPDoc = ipDoc.getDocumentElement();
		incidentNo = elmIPDoc.getAttribute("IncidentNo");
		incidentYr = elmIPDoc.getAttribute("Year");
		Document incDtlsOPDoc = null;
		try {
			incDtlsOPDoc = CommonUtilities.invokeService(env, NWCGConstants.SVC_GET_INCIDENT_ORDER_SVC, ipDoc);
		}
		catch (Exception e){
			logger.error("!!!!! Caught General Exception :: Exception while making NWCGGetIncidentOrderService : " + e);
		}
		
		if (incDtlsOPDoc == null){
			return incDtlsOPDoc;
		}
		else {
			Element incDtlsOPRootElm = incDtlsOPDoc.getDocumentElement();
			// Check for active flag If it is inactive, then make a call with LastIncidentNo as the passed incident no. 
			String isActive = incDtlsOPRootElm.getAttribute(NWCGConstants.IS_ACTIVE);
			if (!isActive.equalsIgnoreCase(NWCGConstants.YES)){
				// Return the latest incident number and year
				return getLatestIncidentDetails(env, incidentNo, incidentYr);
			}
			else {
				String incSource = incDtlsOPRootElm.getAttribute(NWCGConstants.INCIDENT_SOURCE);
				if (incSource != null && incSource.equalsIgnoreCase("R")){
					// Check if primary ross account code is present. Based on that, set a custom flag to Y or N
					NodeList rossAcctCodeNL = incDtlsOPRootElm.getElementsByTagName(NWCGConstants.NWCG_ROSS_ACCOUNT_CODES_ELM);
					if (rossAcctCodeNL != null && rossAcctCodeNL.getLength() > 0){
						int rossAcctCodesLen = rossAcctCodeNL.getLength();
						boolean obtPrimInd = false;
						for (int i=0; i < rossAcctCodesLen && !obtPrimInd; i++){
							Element rossAcctCode = (Element) rossAcctCodeNL.item(i);
							String primaryInd = rossAcctCode.getAttribute(NWCGConstants.PRIMARY_INDICATOR);
							if (primaryInd.equalsIgnoreCase("true")){
								obtPrimInd = true;
								incDtlsOPRootElm.setAttribute("IsPrimaryROSSFinAcctCode", "Y");
								incDtlsOPRootElm.setAttribute("FinancialCode", rossAcctCode.getAttribute("FinancialCode"));
								incDtlsOPRootElm.setAttribute("OwningAgency", rossAcctCode.getAttribute("OwningAgencyName"));
								incDtlsOPRootElm.setAttribute("FiscalYear", rossAcctCode.getAttribute("FiscalYear"));
							} else {
								incDtlsOPRootElm.setAttribute("IsPrimaryROSSFinAcctCode", "N");
							}
						}
					} else {
						incDtlsOPRootElm.setAttribute("IsPrimaryROSSFinAcctCode", "N");
					}
				}
			}
		}
		logger.verbose("@@@@@ Exiting NWCGGetOrderListForIncident::getIncidentDtls @@@@@");
		return incDtlsOPDoc;
	}

	/**
	 * 
	 * @param env
	 * @param incNo
	 * @param year
	 * @return
	 */
	private Document getLatestIncidentDetails(YFSEnvironment env, String incNo, String year) {
		logger.verbose("@@@@@ Entering NWCGGetOrderListForIncident::getLatestIncidentDetails @@@@@");
		StringBuffer errorDesc = new StringBuffer();
		Document latestIncDtlsIPDoc = null;
		try {
			latestIncDtlsIPDoc = XMLUtil.createDocument("NWCGIncidentOrder");
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException while creating NWCGIncidentOrder document : " + pce);
		}
		Element latestIncDtlsIPRootElm = latestIncDtlsIPDoc.getDocumentElement();
		latestIncDtlsIPRootElm.setAttribute(NWCGConstants.REPLACED_INCIDENT_NO, incNo);
		latestIncDtlsIPRootElm.setAttribute(NWCGConstants.REPLACED_INCIDENT_YEAR, year);
		logger.verbose("@@@@@ Input to get the latest incident details : " + XMLUtil.getXMLString(latestIncDtlsIPDoc));
		String latestIncNo = "";
		String latestIncYr = "";
		try {
			// This will return a list of incidents. Something like <NWCGIncidentOrderList><NWCGIncidentOrder /> </NWCGIncidentOrderList>
			Document latestIncDtlsOPDoc = CommonUtilities.invokeService(env, NWCGConstants.SVC_GET_INCIDENT_ORDER_LIST_SVC, latestIncDtlsIPDoc);
			NodeList latestNWCGIncOrderList = latestIncDtlsOPDoc.getElementsByTagName("NWCGIncidentOrder");
			if (latestNWCGIncOrderList != null && latestNWCGIncOrderList.getLength() > 0){
				Element latestNWCGIncOrder = (Element) latestNWCGIncOrderList.item(0);
				latestIncNo = latestNWCGIncOrder.getAttribute(NWCGConstants.INCIDENT_NO_ATTR);
				latestIncYr = latestNWCGIncOrder.getAttribute(NWCGConstants.YEAR_ATTR);
			}
			if (latestIncNo != null && latestIncNo.length() > 2) {
				errorDesc.append("This incident is inactive. System received an UpdateIncidentKeyNotification ").append("message for this incident and incident number has changed from ").append(incNo).append(" to ").append(latestIncNo).append(" and year ").append(latestIncYr).append(". Please enter latest incident number and year to proceed");
			} else {
				errorDesc.append("This incident is inactive. Please enter an active incident number and year to proceed further."); 
			}
		}
		catch (Exception e){
			logger.error("!!!!! Caught General Exception while making NWCGGetIncidentOrderService : " + e);
		}
		
		Document opDoc = null;
		try {
			opDoc = XMLUtil.createDocument("NWCGIncidentOrder");
			opDoc.getDocumentElement().setAttribute("ErrorDesc", errorDesc.toString());
		}
		catch (ParserConfigurationException pce){
			logger.error("!!!!! Caught ParserConfigurationException while creating output document : " + pce);
		}
		logger.verbose("@@@@@ Exiting NWCGGetOrderListForIncident::getLatestIncidentDetails @@@@@");
		return opDoc;
	}
}