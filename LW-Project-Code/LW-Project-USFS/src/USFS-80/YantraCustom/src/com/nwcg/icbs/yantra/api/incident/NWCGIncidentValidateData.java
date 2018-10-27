package com.nwcg.icbs.yantra.api.incident;

import java.util.Properties;
import java.util.HashSet;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import javax.xml.parsers.ParserConfigurationException;

public class NWCGIncidentValidateData implements YIFCustomApi{
	private static Logger log = Logger.getLogger(NWCGIncidentValidateData.class.getName());
	private String yearStr=null;
	private String incidentNumberStr=null;

	/*
	 * Inherited abstract method
	 */
	public void setProperties(Properties arg0) throws Exception {
	}
	
	/*
	 * This method does the following
	 * 	- Updates the incident number from a-b-1 to a-b-000001
	 * 	- Gets the unique shipping address
	 * 	- Gets customer information based on customer id and stamps it the existing doc
	 */
	public Document validateData(YFSEnvironment env, Document ipDoc) throws Exception {
		ipDoc = updateIncidentNumber(ipDoc);
		Document retnDoc = getUniqueShippingAddress(env, ipDoc);
		retnDoc = updateCustomerInfo(env, retnDoc);
		verifyDataAndRaiseAlert(env, retnDoc);
		return retnDoc;
	}
	
	/*
	 * This method is called by NWCGUpdateIncidentNotificationFromROSS;
	 * the method to change the incident number from a-b-1 to a-b-000001 
	 * Performs Business Rules validation of incoming message
	 * Sets the unique shipping address from multiple addresses in the message
	 * Returns processes xml as an input to UpdateIncidentService
	 */
	public Document validateDataForNotification(YFSEnvironment env, Document ipDoc) throws Exception {
		
		Document retnDoc = getUniqueShippingAddress(env, verifyIncomingData(env,updateIncidentNumber(ipDoc)));
		return retnDoc;
	}
	
	/*
	 * Change the sequence number to 6 characters. Prepend it with 0's.
	 */
	public Document updateIncidentNumber(Document ipDoc){
		Element docElm = ipDoc.getDocumentElement();
		String incidentNo = docElm.getAttribute("IncidentNo");
		if (incidentNo != null || incidentNo.length() > 0){
			String seqNum = incidentNo.substring(incidentNo.lastIndexOf('-')+1, incidentNo.length());
			StringBuffer sb = new StringBuffer(incidentNo.substring(0, incidentNo.lastIndexOf('-')+1));

			int length = seqNum.length();
			int seqNumLength = 6;
			for (int i=0; i < seqNumLength-length; i++){
				sb.append("0");
			}
			sb.append(seqNum);
			docElm.setAttribute("IncidentNo", sb.toString());
			this.incidentNumberStr=sb.toString();
		}
		return ipDoc;
	}
	
	/*
	 * The below method gets the YFSPersonInfoShipTo element. Input document
	 * from ROSS can contain more than one YFSPersonInfoShipTo element. PrimaryInd 
	 * attribute is used to figure the right element. Possible values are false or true. If
	 * there is an element with YFSPersonInfoShipTo with PrimaryInd as true, then remove all
	 * the remaining YFSPersonInfoShipTo, else keep the last YFSPersonInfoShipTo element (Other
	 * options are values of 'false' and this attribute not being present at all).
	 */
	public Document getUniqueShippingAddress(YFSEnvironment env, Document ipDoc) throws Exception {
		NWCGLoggerUtil.Log.finer("NWCGIncidentValidateData::getUniqueShippingAddress, Entered : " + XMLUtil.getXMLString(ipDoc));

		Document opDoc = ipDoc;
		try {
			Element docElm = opDoc.getDocumentElement();
			Node shipNode = null;
			Node primaryShipNode = null;
			// Get all the YFSPersonInfoShipTo elements
			NodeList shipToNodeList = docElm.getElementsByTagName(NWCGAAConstants.PERSON_INFO_SHIP_TO);
			
			if (shipToNodeList == null || (shipToNodeList.getLength() == 0) ){
				NWCGLoggerUtil.Log.warning("NWCGIncidentValidateData::getUniqueShippingAddress, " +
								"There are no elements with " + NWCGAAConstants.PERSON_INFO_SHIP_TO);				
				return opDoc;
			}
			
			// If there are more than one PersonInfo Ship To elements, then we need to get the 
			// appropriate PersonInfo Ship To element based on above logic
			NWCGLoggerUtil.Log.finer("NWCGIncidentValidateData::getUniqueShippingAddress, " +
						"Number of " + NWCGAAConstants.PERSON_INFO_SHIP_TO + " elements are " + shipToNodeList.getLength());			
			if (shipToNodeList.getLength() > 1)
			{
				// I had to go through all the elements even though we get the
				// required element as it is not removing all the elements below.
				boolean checkAttributes = true;
				for (int j=0; j < shipToNodeList.getLength() && checkAttributes; j++){
					Node personInfo = shipToNodeList.item(j);
					shipNode = personInfo;
					NamedNodeMap nnm = personInfo.getAttributes();
					
					for (int k=0; k < nnm.getLength() && checkAttributes; k++){
						Node primaryIndNode = nnm.item(k);
						if (primaryIndNode.getNodeName().equalsIgnoreCase(NWCGAAConstants.SHIPPING_ADDR_PRIMARY_IND)){
							String primaryInd = primaryIndNode.getNodeValue();
							if (primaryInd.equalsIgnoreCase("1") || (primaryInd.equalsIgnoreCase("true"))){
								checkAttributes = false;
								primaryShipNode = personInfo;
							}
						}
					}
				}

				NWCGLoggerUtil.Log.finer("NWCGIncidentValidateData::getUniqueShippingAddress, " +
						"Removing all the YFSPersonInfoShipTo elements : " + shipToNodeList.getLength());
				int len = shipToNodeList.getLength();
				for (int r=len; r > 0; r--){
					try {
						docElm.removeChild(shipToNodeList.item(r-1));
					}
					catch (Exception e){
						NWCGLoggerUtil.Log.warning("Exception occured while deleting node : " + e.getMessage());
						e.printStackTrace();
					}
				}

				if (primaryShipNode != null){
					NWCGLoggerUtil.Log.finer("NWCGIncidentValidateData::getUniqueShippingAddress, Adding primary ship node");
					docElm.appendChild(primaryShipNode);
				}
				else if (shipNode != null){
					NWCGLoggerUtil.Log.finer("NWCGIncidentValidateData::getUniqueShippingAddress, Adding last ship node element");
					docElm.appendChild(shipNode);
				}
			}
			NWCGLoggerUtil.Log.finest("NWCGIncidentValidateData::getUniqueShippingAddress, " +
									"Returning the XML : " + XMLUtil.extractStringFromDocument(opDoc));
		}
		catch(Exception e){
			NWCGLoggerUtil.Log.warning("NWCGIncidentValidateData::getUniqueShippingAddress, Exception Message : " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
		return opDoc;
	}
	
	/**
	 * This method gets the customer details and updates the passed document
	 * @param env
	 * @param doc
	 * @return
	 */
	private Document updateCustomerInfo(YFSEnvironment env, Document doc){
		NWCGLoggerUtil.Log.info("NWCGIncidentValidateData::updateCustomerInfo, Entered");
		try {
			Element ipDocElm = doc.getDocumentElement();
			Document custIPDoc = XMLUtil.createDocument(NWCGConstants.CUST_CUST_ELEMENT);
			Element customerInputElm = custIPDoc.getDocumentElement();
			customerInputElm.setAttribute(NWCGConstants.CUST_CUST_ID_ATTR, 
											ipDocElm.getAttribute(NWCGConstants.INC_CUST_ID_ATTR));
			// Hard code organization code to "NWCG" as this is the only enterprise code that we have
			// for USFS project
			customerInputElm.setAttribute(NWCGConstants.CUST_ORG_CODE_ATTR, NWCGConstants.ENTERPRISE_CODE);
			NWCGLoggerUtil.Log.info("NWCGIncidentValidateData::updateCustomerInfo, Input XML : " + XMLUtil.extractStringFromDocument(custIPDoc));
			Document customerOPDoc = CommonUtilities.invokeAPI(env, NWCGConstants.TMPL_CREATE_INC_GET_CUST_DTLS, 
														NWCGConstants.API_GET_CUST_DETAILS, custIPDoc);
			if (customerOPDoc != null){
				Element customerOPElm = customerOPDoc.getDocumentElement();
				NodeList childNodes = customerOPElm.getChildNodes();
				// We are getting the customer details through the template.
				boolean obtExtn = false;
				boolean obtCons = false;
				for (int i=0; i < childNodes.getLength(); i++){
					Node elmOLChild = childNodes.item(i);
					if (elmOLChild.getNodeType() == Node.ELEMENT_NODE){
						if (elmOLChild.getNodeName().equalsIgnoreCase("Extn")){
							Element extnChildElm = (Element) elmOLChild;
							ipDocElm.setAttribute(NWCGConstants.INCIDENT_GACC_ATTR, 
									extnChildElm.getAttribute(NWCGConstants.CUST_GACC_ATTR));
							ipDocElm.setAttribute(NWCGConstants.INCIDENT_CUSTOMER_NAME_ATTR, 
									extnChildElm.getAttribute(NWCGConstants.CUST_CUSTOMER_NAME_ATTR));
							ipDocElm.setAttribute(NWCGConstants.INCIDENT_UNIT_TYPE_ATTR, 
									extnChildElm.getAttribute(NWCGConstants.CUST_UNIT_TYPE_ATTR));
							ipDocElm.setAttribute(NWCGConstants.INCIDENT_AGENCY_ATTR, 
									extnChildElm.getAttribute(NWCGConstants.CUST_AGENCY_ATTR));
							ipDocElm.setAttribute(NWCGConstants.INCIDENT_DEPARTMENT_ATTR, 
									extnChildElm.getAttribute(NWCGConstants.CUST_DEPARTMENT_ATTR));
							obtExtn = true;
						}
						else if (elmOLChild.getNodeName().equalsIgnoreCase("Consumer")){
							Element consumerElm = (Element) elmOLChild;
							ipDocElm.setAttribute("PersonInfoBillToKey", consumerElm.getAttribute("BillingAddressKey"));
							obtCons = true;
						}
					}
					if (obtExtn && obtCons){
						break;
					}
				}				
			}
			else {
				NWCGLoggerUtil.Log.info("NWCGIncidentValidateData::updateCustomerInfo, Customer is not present in ICBSR");
			}
		}
		catch(ParserConfigurationException pce){
			NWCGLoggerUtil.Log.warning("NWCGIncidentValidateData::updateCustomerInfo, " +
										"Parser Configuration Exception : " + pce.getMessage());
			pce.printStackTrace();
		}
		catch (Exception e){
			NWCGLoggerUtil.Log.warning("NWCGIncidentValidateData::updateCustomerInfo, Exception : " + e.getMessage());
			e.printStackTrace();
		}
		
		NWCGLoggerUtil.Log.info("NWCGIncidentValidateData::updateCustomerInfo, Returning");
		return doc;
	}
	
	
	/**
	 * This method will check for the required fields. If it does not have,
	 * then it will populate those fields for display in Alert References
	 * @param env
	 * @param doc
	 */
	public void verifyDataAndRaiseAlert(YFSEnvironment env, Document doc){
		NWCGLoggerUtil.Log.info("NWCGIncidentValidateData::verifyDataAndRaiseAlert, Entered");
		Element rootElm = doc.getDocumentElement();
		StringBuffer sbAlertDesc = new StringBuffer();
		String primCacheId = rootElm.getAttribute(NWCGConstants.PRIMARY_CACHE_ID);
		if (primCacheId == null || primCacheId.length() < 2){
			sbAlertDesc.append("Incident does not have Primary Cache ID");
		}
		
		String reqBlockStart = rootElm.getAttribute(NWCGConstants.INC_REQ_NO_BLOCK_START);
		String reqBlockEnd = rootElm.getAttribute(NWCGConstants.INC_REQ_NO_BLOCK_END);
		if ((reqBlockStart == null || reqBlockStart.length() < 1) ||
			(reqBlockEnd == null || reqBlockEnd.length() < 1)){
			if (sbAlertDesc.length() > 1){
				sbAlertDesc.append("\n");
			}
			sbAlertDesc.append("Incident does not have Request Block"); 
		}
		
		String custId = rootElm.getAttribute(NWCGConstants.INC_CUST_ID_ATTR);
		if (custId == null || (custId.length() < 1)){
			if (sbAlertDesc.length() > 1){
				sbAlertDesc.append("\n");
			}
			sbAlertDesc.append("Incident does not have Customer ID");
		}
		
		String incNo = rootElm.getAttribute(NWCGConstants.INCIDENT_NO_ATTR);
		String incYr = rootElm.getAttribute(NWCGConstants.YEAR_ATTR);
		String detailDesc = "";
		String desc = "";
		
		HashMap hMap = new HashMap();
		String incAction = rootElm.getAttribute(NWCGAAConstants.INCIDENT_ACTION_STR);
		if (incAction.equalsIgnoreCase(NWCGAAConstants.INCIDENT_ACTION_CREATE)){
			detailDesc = "Processed Synchronous Register Incident Interest Response call " +
					"for incident number " + incNo + " and year " + incYr;
			desc = "Processed Synchronous Register Incident Interest Response call";
			hMap.put(NWCGConstants.ALERT_SOAP_MESSAGE, "Synchronous RegisterIncidentInterest");
		}
		else if (incAction.equalsIgnoreCase(NWCGAAConstants.INCIDENT_ACTION_UPDATE)){
			detailDesc = "Processed Asynchronous Register Incident Interest Response call " +
					"for incident number " + incNo + " and year " + incYr;
			desc = "Processed Asynchronous Register Incident Interest Response call";
			hMap.put(NWCGConstants.ALERT_SOAP_MESSAGE, "Asynchronous RegisterIncidentInterest");
		}
		else if (incAction.equalsIgnoreCase(NWCGAAConstants.INCIDENT_ACTION_MERGE)){
			detailDesc = "Created incident " + incNo + " and year " + incYr + 
							"due to Merge Incident Notifiction";
			desc = "Processed MergeIncidentNotification";
			hMap.put(NWCGConstants.ALERT_SOAP_MESSAGE, "MergeIncidentNotification");
		}
		else if (incAction.equalsIgnoreCase(NWCGAAConstants.INCIDENT_ACTION_REASSIGN)){
			// The Resource Reassignment Handler class handles
			// alert creation for IB RR Notifications
			return;
		}
		
		hMap.put(NWCGConstants.ALERT_INCIDENT_NO, incNo);
		hMap.put(NWCGConstants.ALERT_YEAR, incYr);
		hMap.put(NWCGConstants.ALERT_DESC, desc);
		if (sbAlertDesc != null && sbAlertDesc.length() > 2){
			hMap.put("Missing Fields", sbAlertDesc.toString());
		}
		NWCGLoggerUtil.Log.info("NWCGIncidentValidateData::verifyDataAndRaiseAlert, Raising alert");
		CommonUtilities.raiseAlertAndAssigntoUser(env, NWCGConstants.NWCG_INCIDENT_SUCCESS, detailDesc, "", doc, hMap);
		NWCGLoggerUtil.Log.info("NWCGIncidentValidateData::verifyDataAndRaiseAlert, Returning...");
	}	
	
	public Document verifyIncomingData(YFSEnvironment env, Document opDoc) throws Exception {
		if (log.isVerboseEnabled()){
			log.debug("verifyIncomingData>Input document : " + XMLUtil.getXMLString(opDoc));
		}
		
		System.out.println("getUniqueShippingAddress>>Input document : " + XMLUtil.getXMLString(opDoc));
		try {
			Element docElm = opDoc.getDocumentElement();
			
			// Get all the NWCGIncidentOrder element
			NodeList nodeList = docElm.getElementsByTagName("NWCGIncidentOrder");
			if (nodeList.getLength() < 1){
			   
			   raiseAlert(env,opDoc,null);
			   return opDoc;
			   }
			//only one node of this type is expected
			   Node incidentInfo = nodeList.item(0);
			   NamedNodeMap nnm = incidentInfo.getAttributes();
			   HashSet errorSet = new HashSet();
			   
			   //check that incident name is not null 
			   if(!isAttributePresent("IncidentName", nnm))
			    	errorSet.add("IncidentName");
			   
			   //check that incident type is not null 
			   if(!isAttributePresent("IncidentType",  nnm))
			   		errorSet.add("IncidentType");
			   		
			   //check that incident number and year is not null and exist in the database
			   if(!isAttributePresent("IncidentNo",  nnm))
			   		errorSet.add("IncidentNo");
			   if(!isAttributePresent("IncidentYear",nnm))
			   		errorSet.add("Year");
			   
			   		
			   //check that incident DateStarted is not null 
			   if(!isAttributePresent("DateStarted", nnm))		   
			   		errorSet.add("DateStarted");
			   		
			   //check that incident ROSSIncidentStatus is not null
			   if(!isAttributePresent("ROSSIncidentStatus", nnm))
			   		errorSet.add("ROSSIncidentStatus");
			   			   
 				//check that incident PrimaryCacheId is not null
			   if(!isAttributePresent("PrimaryCacheId", nnm))
                  errorSet.add("PrimaryCacheId");
			   	   
			   //check that incident Billing Org ==IncidentHost(by JW) is not null
			   	if(!isAttributePresent("IncidentHost", nnm))
                  errorSet.add("IncidentHost");
			   
			   //check that incident Primary Financial Code exists
			   if(!isAttributePresent("ROSSFinancialCode", nnm))
			          errorSet.add("ROSSFinancialCode");

               if(errorSet.size() > 0)
                   raiseAlert(env,opDoc,errorSet);

               //check that IncidentNo exists in the database
               if(null==getVerifyIncident(env,nnm))
                		raiseAlert(env,opDoc,errorSet);
			   }
			catch(Exception e){
			log.error("Exception Message : " + e.getMessage());
			e.printStackTrace();
			throw e;
		}

		return opDoc;
	}
	
	//checks that IncidentNo and Year already exist in Yantra database
	//assumes incidentNo and year are in the given NamedNodeMap and not empty (checked before)
	private Document getVerifyIncident(YFSEnvironment env,NamedNodeMap nnm){       
		Node	in	=nnm.getNamedItem("IncidentNo");
		String incidentNo= in.getNodeValue();
 		Node	ye	=nnm.getNamedItem("IncidentYear");
		String incidentYear= ye.getNodeValue();
		
		Document retDoc = null;
		try {
			retDoc = getIncidentInfo(env, incidentNo, incidentYear);
			
			if (retDoc == null){
				return retDoc;
			}
			
			Element incidentElm = XMLUtil.getFirstElementByName(retDoc.getDocumentElement(), "NWCGIncidentOrder");
			if (incidentElm == null){
				return null;
			}
		} catch (Exception e) {
			NWCGLoggerUtil.Log.info("NWCGIncidentValidateData::getVerifyIncident, Exception");			
			NWCGLoggerUtil.printStackTraceToLog(e);
			return null;
		}
			
		NWCGLoggerUtil.Log.info("NWCGIncidentValidateData::getVerifyIncident, Returned xml from NWCGGetIncidentOrderListService>>"+XMLUtil.getXMLString(retDoc));
		return retDoc;		
    }
	
	private Document getIncidentInfo (YFSEnvironment env, String incidentNo, String year){
		Document incidentDoc = null;
		try {
			incidentDoc = XMLUtil.newDocument();
		}
		catch(ParserConfigurationException pce){
			NWCGLoggerUtil.Log.warning("NWCGIncidentValidateData::getIncidentInfo, ParserConfigurationException while creating XMLDocument");
			pce.printStackTrace();  
		}
		
		Element incidentElm = incidentDoc.createElement("NWCGIncidentOrder");
		incidentElm.setAttribute("IncidentNo", incidentNo);
		incidentElm.setAttribute("Year", year);
		incidentDoc.appendChild(incidentElm);
		NWCGLoggerUtil.Log.info("NWCGIncidentValidateData::getIncidentInfo, Input xml for getting incident details : " + XMLUtil.getXMLString(incidentDoc));
		
		Document incidentListDoc = null;
		try {
			incidentListDoc = CommonUtilities.invokeService(env, "NWCGGetIncidentOrderListService", incidentDoc);
		}
		catch (Exception e){
			NWCGLoggerUtil.Log.warning("NWCGIncidentValidateData::getIncidentInfo, Exception while making a incident list call");
			NWCGLoggerUtil.printStackTraceToLog(e);
		}
		return incidentListDoc;
	}

    private void raiseAlert(YFSEnvironment env, Document idoc, HashSet errorSet){   
        NWCGLoggerUtil.Log.info("NWCGIncidentValidateData.raiseAlert>>missing data: "+errorSet);   
        System.out.println("NWCGIncidentValidateData.raiseAlert>>missing data: " + errorSet);   
        // Form a map object to pass strings for reference in alert creation
    	HashMap map= new HashMap();
    	if(errorSet != null) {
	    	map.put(NWCGAAConstants.NWCG_INCIDENT_NO,	(errorSet.contains("IncidentNo")?"undefined":this.incidentNumberStr));
	    	map.put(NWCGAAConstants.NWCG_INCIDENT_YEAR, (errorSet.contains("Year")?"undefined"   :this.yearStr));         
	    	map.put("Missing Data: ", errorSet);
    	}
    	map.put("Severity","High");
      	map.put(NWCGAAConstants.NAME, XMLUtil.getXMLString(idoc));
     	map.put("AlertType", NWCGAAConstants.UPDATE_INCIDENT_ERROR);
     	map.put("Missing Data: ", "XML element NWCGIncidentOrder");
     	
     	try{     	
     		CommonUtilities.raiseAlert(env,NWCGAAConstants.QUEUEID_INCIDENT_FAILURE,
    									   NWCGAAConstants.UPDATE_INCIDENT_ERROR_DESCRIPTION,
    									   idoc, null,map);
      	}
     	catch(Exception pce){
         	NWCGLoggerUtil.Log.warning("Exception while creating XMLDocument");
            pce.printStackTrace();  
     	}
    }
	    
	private boolean isAttributePresent(String name, NamedNodeMap nnm){

		Node	attributeNode	=nnm.getNamedItem(name);
		if(attributeNode != null){
		String value= attributeNode.getNodeValue();
		if(!StringUtil.isEmpty(value))
		   return true;
		}
		return false;
	}	   
	
	/**
	 * This method is used to set the incident action value as part of NWCGRegisterIncidentInterestRespService.
	 * If the incident is present, then IncidentAction is set to UPDATE and if it is not present, then it is
	 * set to CREATE 
	 * @param env
	 * @param inDoc
	 * @return
	 * @throws Exception
	 */
	public Document doesIncidentExists(YFSEnvironment env, Document inDoc) throws Exception {
		inDoc = updateIncidentNumber(inDoc);
		Element rootElm = inDoc.getDocumentElement();
		String incidentNo = rootElm.getAttribute("IncidentNo");
		String year = rootElm.getAttribute("Year");
		if (incidentNo != null && year != null){
			NWCGLoggerUtil.Log.info("NWCGIncidentValidateData::doesIncidentExists, Incident Number: " + incidentNo + ", Year: " + year);
			Document incidentListDoc = getIncidentInfo(env, incidentNo, year);
			if (incidentListDoc !=null){
				Element incidentListElm = XMLUtil.getFirstElementByName(incidentListDoc.getDocumentElement(), "NWCGIncidentOrder");
				if (incidentListElm != null){
					rootElm.setAttribute(NWCGAAConstants.INCIDENT_ACTION_STR, NWCGAAConstants.INCIDENT_ACTION_UPDATE);
					rootElm.setAttribute(NWCGAAConstants.INCIDENT_MOD_DESC, "Made RegisterIncidentInterest async call");
					rootElm.setAttribute(NWCGAAConstants.INCIDENT_MOD_CODE, "Sent to ROSS");
				}
				else {
					rootElm.setAttribute(NWCGAAConstants.INCIDENT_ACTION_STR, NWCGAAConstants.INCIDENT_ACTION_CREATE);
					// New incident should be created in 'INACTIVE' status. User is expected to make it active manually
					rootElm.setAttribute(NWCGAAConstants.INCIDENT_IS_ACTIVE_ATTR, "N");
					rootElm.setAttribute(NWCGAAConstants.INCIDENT_MOD_DESC, "Made RegisterIncidentInterest sync call");
					rootElm.setAttribute(NWCGAAConstants.INCIDENT_MOD_CODE, "Sent to ROSS");
				}
			}
			else {
				rootElm.setAttribute(NWCGAAConstants.INCIDENT_ACTION_STR, NWCGAAConstants.INCIDENT_ACTION_CREATE);
			}
		}
		return inDoc;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}
}
