package com.nwcg.icbs.yantra.api.issue;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGGetIncidentDtlsForIssueCreate implements YIFCustomApi {

	private static Logger log = Logger.getLogger(NWCGGetIncidentDtlsForIssueCreate.class.getName());
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
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
		String incidentNo = "";
		String incidentYr = "";
		log.verbose("NWCGGetIncidentDtlsForIssueCreate::getIncidentDetails, Entered");
		if (log.isVerboseEnabled()){
			log.verbose("NWCGGetIncidentDtlsForIssueCreate::getIncidentDetails, " +
					"Input Document : " + XMLUtil.getXMLString(ipDoc));
		}
		System.out.println("Input XML : " + XMLUtil.getXMLString(ipDoc));
		/*
		NodeList ipDocExtnNL = ipDoc.getElementsByTagName("Extn");
		if (ipDocExtnNL != null && ipDocExtnNL.getLength() > 0){
			// This xml is created on the front end. It will be of the form 
			// <Order> <Extn ExtnIncidentNo="" ExtnIncidentYear=""/> </Order>
			// So, it will have only one Extn element. Assuming that, I am getting the 0th element directly
			Element ipDocExtnElm = (Element) ipDocExtnNL.item(0);
			incidentNo = ipDocExtnElm.getAttribute("ExtnIncidentNo");
			incidentYr = ipDocExtnElm.getAttribute("ExtnIncidentYear");
		}
		else {
			ipDoc.getDocumentElement().setAttribute("ErrorDesc", 
					"Input XML is not in the right format. It doesn't have Extn under Order");
			return ipDoc;
		}
		
		Document incDtlsIPDoc = XMLUtil.createDocument("NWCGIncidentOrder");
		Element incDtlsIPRootElm = incDtlsIPDoc.getDocumentElement();
		incDtlsIPRootElm.setAttribute("IncidentNo", incidentNo);
		incDtlsIPRootElm.setAttribute("Year", incidentYr);
		*/
		
		Element elmIPDoc = ipDoc.getDocumentElement();
		incidentNo = elmIPDoc.getAttribute("IncidentNo");
		incidentYr = elmIPDoc.getAttribute("Year");
		if (log.isVerboseEnabled()){
			log.verbose("NWCGGetIncidentDtlsForIssueCreate::getIncidentDetails, " +
					"Input to NWCGGetIncidentOrderService : " + XMLUtil.getXMLString(ipDoc));
		}
		System.out.println("Input to NWCGGetIncidentOrderService : " + XMLUtil.getXMLString(ipDoc));
		Document incDtlsOPDoc = null;
		try {
			incDtlsOPDoc = CommonUtilities.invokeService(env, NWCGConstants.SVC_GET_INCIDENT_ORDER_SVC, ipDoc);
		}
		catch (Exception e){
			log.error("NWCGGetIncidentDtlsForIssueCreate::getIncidentDetails, " +
					"Exception while making NWCGGetIncidentOrderService : " + e.getMessage());
			e.printStackTrace();
		}
		
		if (incDtlsOPDoc == null){
			return incDtlsOPDoc;
		}
		else {
			Element incDtlsOPRootElm = incDtlsOPDoc.getDocumentElement();
			// Check for active flag
			// If it is inactive, then make a call with LastIncidentNo as the passed incident no. 
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
							}
							else {
								incDtlsOPRootElm.setAttribute("IsPrimaryROSSFinAcctCode", "N");
							}
						}
					}
					else {
						incDtlsOPRootElm.setAttribute("IsPrimaryROSSFinAcctCode", "N");
					}
				}
			}
		}
		return incDtlsOPDoc;
	}

	/**
	 * 
	 * @param env
	 * @param incNo
	 * @param year
	 * @return
	 */
	private Document getLatestIncidentDetails(YFSEnvironment env, String incNo, String year){
		StringBuffer errorDesc = new StringBuffer();
		Document latestIncDtlsIPDoc = null;
		try {
			latestIncDtlsIPDoc = XMLUtil.createDocument("NWCGIncidentOrder");
		} catch (ParserConfigurationException pce) {
			log.error("NWCGGetIncidentDtlsForIssueCreate::getLatestIncidentDetails, " +
					"ParserConfigurationException while creating NWCGIncidentOrder document : " + 
					pce.getMessage());
			pce.printStackTrace();
		}
		Element latestIncDtlsIPRootElm = latestIncDtlsIPDoc.getDocumentElement();
		latestIncDtlsIPRootElm.setAttribute(NWCGConstants.REPLACED_INCIDENT_NO, incNo);
		latestIncDtlsIPRootElm.setAttribute(NWCGConstants.REPLACED_INCIDENT_YEAR, year);
		if (log.isVerboseEnabled()){
			log.verbose("NWCGGetIncidentDtlsForIssueCreate::getLatestIncidentDetails, " +
					"Input to get the latest incident details : " + XMLUtil.getXMLString(latestIncDtlsIPDoc));
		}
		System.out.println("Input to " + NWCGConstants.SVC_GET_INCIDENT_ORDER_LIST_SVC + 
							" : " + XMLUtil.getXMLString(latestIncDtlsIPDoc));
		String latestIncNo = "";
		String latestIncYr = "";
		try {
			// This will return a list of incidents. Something like 
			// <NWCGIncidentOrderList><NWCGIncidentOrder /> </NWCGIncidentOrderList>
			Document latestIncDtlsOPDoc = CommonUtilities.invokeService(
					env,NWCGConstants.SVC_GET_INCIDENT_ORDER_LIST_SVC, latestIncDtlsIPDoc);
			NodeList latestNWCGIncOrderList = latestIncDtlsOPDoc.getElementsByTagName("NWCGIncidentOrder");
			if (latestNWCGIncOrderList != null && latestNWCGIncOrderList.getLength() > 0){
				Element latestNWCGIncOrder = (Element) latestNWCGIncOrderList.item(0);
				latestIncNo = latestNWCGIncOrder.getAttribute(NWCGConstants.INCIDENT_NO_ATTR);
				latestIncYr = latestNWCGIncOrder.getAttribute(NWCGConstants.YEAR_ATTR);
			}
			if (latestIncNo != null && latestIncNo.length() > 2){
				errorDesc.append("This incident is inactive. System received an UpdateIncidentKeyNotification ")
						 .append("message for this incident and incident number has changed from ")
						 .append(incNo).append(" to ").append(latestIncNo).append(" and year ").append(latestIncYr)
						 .append(". Please enter latest incident number and year to proceed");
			}
			else {
				errorDesc.append("This incident is inactive. Please enter an active incident number and year to proceed further."); 
			}
			
		}
		catch (Exception e){
			log.error("NWCGGetIncidentDtlsForIssueCreate::getLatestIncidentDetails, " +
					"Exception while making NWCGGetIncidentOrderService : " + e.getMessage());
			e.printStackTrace();
		}
		
		Document opDoc = null;
		try {
			opDoc = XMLUtil.createDocument("NWCGIncidentOrder");
			opDoc.getDocumentElement().setAttribute("ErrorDesc", errorDesc.toString());
		}
		catch (ParserConfigurationException pce){
			log.error("NWCGGetIncidentDtlsForIssueCreate::getLatestIncidentDetails, " +
					"ParserConfiguration Exception while creating output document : " + pce.getMessage());
			pce.printStackTrace();
		}
		return opDoc;
	}
}
