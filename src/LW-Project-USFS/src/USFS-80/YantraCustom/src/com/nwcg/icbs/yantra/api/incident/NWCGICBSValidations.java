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

package com.nwcg.icbs.yantra.api.incident;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathWrapper;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * @author sdas
 * @since 03-JUNE-2008
 */
public class NWCGICBSValidations implements YIFCustomApi{

	private static Logger logger = Logger.getLogger();
	private Properties myProperties = null;
	
    /* (non-Javadoc)
     * @see com.yantra.interop.japi.YIFCustomApi#setProperties(java.util.Properties)
     */
    public void setProperties(Properties arg0) throws Exception {
    	this.myProperties = arg0;
    	if (logger.isVerboseEnabled()) 
    		logger.verbose(this.myProperties.toString());
    }

    public Document validate(YFSEnvironment environment, Document document) throws Exception {
        logger.beginTimer("NWCGICBSValidations.validate");
        logger.verbose("Inside NWCGICBSValidations.validate(env, Document) method");
        logger.verbose(XMLUtil.extractStringFromDocument(document));
        
        // Customer ID validation
        String customerID = document.getDocumentElement().getAttribute("CustomerId");
        logger.verbose("Customer ID: "+customerID);
        if(!StringUtil.isEmpty(customerID)){
            
            environment.clearApiTemplate(NWCGConstants.API_GET_CUSTOMER_LIST);
            Document ipAPIDoc = XMLUtil.getDocument("<Customer CustomerID=\""+customerID+"\" />");
            Document ipAPIDocTemplate = XMLUtil.getDocument(NWCGConstants.TEMPLATE_GET_CUSTOMER_LIST);
            environment.setApiTemplate(NWCGConstants.API_GET_CUSTOMER_LIST,ipAPIDocTemplate);
            
            Document opAPIDoc = CommonUtilities.invokeAPI(environment,NWCGConstants.API_GET_CUSTOMER_LIST,ipAPIDoc);
            logger.verbose("getCustomerList output: "+XMLUtil.extractStringFromDocument(opAPIDoc));
            
            if(opAPIDoc.getDocumentElement().getChildNodes().getLength()==0) {
                logger.error("Generating alert for customer ID not found!", opAPIDoc);
                
                Map<String, String> map = new HashMap<String, String>();
                map.put("Invalid Customer ID/Bill To ID", customerID);
                CommonUtilities.raiseAlert(environment,NWCGAAConstants.QUEUEID_INCIDENT_FAILURE,NWCGConstants.ALERT_STRING,document,null,map);
            } else {
                logger.verbose("Modifying return document");
                
                // Access the BillingPersonInfo element from CustomerList
                XPathWrapper pathWrapper = new XPathWrapper(opAPIDoc);
                Element billingPersonInfoElem = (Element)pathWrapper.getNode(NWCGConstants.XPATH_PERSON_BILLING_INFO);
                
                setElementAttributes(document, billingPersonInfoElem);
            }
            
        }
        
        // clear template which is stored in environment earlier
        environment.clearApiTemplates();
        
        // Incident host validation
        String incidentHost = document.getDocumentElement().getAttribute("IncidentHost");
        if(!StringUtil.isEmpty(incidentHost)){
            Document ipAPIDoc1 = XMLUtil.getDocument("<Customer CustomerID=\""+incidentHost+"\" />");
            Document ipAPIDocTemplate1 = XMLUtil.getDocument(NWCGConstants.TEMPLATE_GET_CUSTOMER_LIST);
            environment.setApiTemplate(NWCGConstants.API_GET_CUSTOMER_LIST, ipAPIDocTemplate1);
            
            Document opAPIDoc1 = CommonUtilities.invokeAPI(environment,NWCGConstants.API_GET_CUSTOMER_LIST,ipAPIDoc1);
            if(opAPIDoc1.getDocumentElement().getChildNodes().getLength()==0){
                logger.error("Generating alert for incident host!", opAPIDoc1);
                
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Invalid Incident Host!", incidentHost);
                CommonUtilities.raiseAlert(environment,NWCGAAConstants.QUEUEID_INCIDENT_FAILURE,NWCGConstants.ALERT_STRING,document,null,map1);
            }
        }        
        logger.verbose("Return doc :"+XMLUtil.extractStringFromDocument(document));
        logger.endTimer("NWCGICBSValidations.validate");
        return document;        
    }

    /**
     * @param document
     * @param billingPersonInfoElem
     */
    private void setElementAttributes(Document document, Element billingPersonInfoElem) throws Exception {
        Element billToElem = (Element)document.getDocumentElement().getElementsByTagName("YFSPersonInfoBillTo").item(0);
        logger.verbose("billToElem before set operation:"+XMLUtil.extractStringFromNode(billToElem));
        
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("AddressLine1"))){
            billToElem.setAttribute("AddressLine1",billingPersonInfoElem.getAttribute("AddressLine1"));
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("AddressLine2"))){
            billToElem.setAttribute("AddressLine2",billingPersonInfoElem.getAttribute("AddressLine2"));
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("AddressLine3"))){
            billToElem.setAttribute("AddressLine3",billingPersonInfoElem.getAttribute("AddressLine3"));
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("AddressLine4"))){
            billToElem.setAttribute("AddressLine4",billingPersonInfoElem.getAttribute("AddressLine4"));
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("AddressLine5"))){
            billToElem.setAttribute("AddressLine5",billingPersonInfoElem.getAttribute("AddressLine5"));
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("AddressLine6"))){
            billToElem.setAttribute("AddressLine6",billingPersonInfoElem.getAttribute("AddressLine6"));
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("AlternateEmailID"))){
            billToElem.setAttribute("AlternateEmailID",billingPersonInfoElem.getAttribute("AlternateEmailID"));
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("Beeper"))){
            billToElem.setAttribute("Beeper",billingPersonInfoElem.getAttribute("Beeper"));
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("City"))){
            billToElem.setAttribute("City",billingPersonInfoElem.getAttribute("City"));
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("Company"))){
            billToElem.setAttribute("Company",billingPersonInfoElem.getAttribute("Company"));    
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("Country"))){
            billToElem.setAttribute("Country",billingPersonInfoElem.getAttribute("Country"));    
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("DayFaxNo"))){
            billToElem.setAttribute("DayFaxNo",billingPersonInfoElem.getAttribute("DayFaxNo"));
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("DayPhone"))){
            billToElem.setAttribute("DayPhone",billingPersonInfoElem.getAttribute("DayPhone"));    
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("Department"))){
            billToElem.setAttribute("Department",billingPersonInfoElem.getAttribute("Department"));
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("EMailID"))){
            billToElem.setAttribute("EMailID",billingPersonInfoElem.getAttribute("EMailID"));
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("ErrorTxt"))){
            billToElem.setAttribute("ErrorTxt",billingPersonInfoElem.getAttribute("ErrorTxt"));    
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("EveningFaxNo"))){
            billToElem.setAttribute("EveningFaxNo",billingPersonInfoElem.getAttribute("EveningFaxNo"));
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("EveningPhone"))){
            billToElem.setAttribute("EveningPhone",billingPersonInfoElem.getAttribute("EveningPhone"));    
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("FirstName"))){
            billToElem.setAttribute("FirstName",billingPersonInfoElem.getAttribute("FirstName"));
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("HttpUrl"))){
            billToElem.setAttribute("HttpUrl",billingPersonInfoElem.getAttribute("HttpUrl"));
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("JobTitle"))){
            billToElem.setAttribute("JobTitle",billingPersonInfoElem.getAttribute("JobTitle"));    
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("LastName"))){
            billToElem.setAttribute("LastName",billingPersonInfoElem.getAttribute("LastName"));    
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("MiddleName"))){
            billToElem.setAttribute("MiddleName",billingPersonInfoElem.getAttribute("MiddleName"));    
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("MobilePhone"))){
            billToElem.setAttribute("MobilePhone",billingPersonInfoElem.getAttribute("MobilePhone"));    
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("OtherPhone"))){
            billToElem.setAttribute("OtherPhone",billingPersonInfoElem.getAttribute("OtherPhone"));
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("PersonID"))){
            billToElem.setAttribute("PersonID",billingPersonInfoElem.getAttribute("PersonID"));    
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("PersonInfoKey"))){
            billToElem.setAttribute("PersonInfoKey",billingPersonInfoElem.getAttribute("PersonInfoKey"));    
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("PreferredShipAddress"))){
            billToElem.setAttribute("PreferredShipAddress",billingPersonInfoElem.getAttribute("PreferredShipAddress"));    
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("State"))){
            billToElem.setAttribute("State",billingPersonInfoElem.getAttribute("State"));    
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("Suffix"))){
            billToElem.setAttribute("Suffix",billingPersonInfoElem.getAttribute("Suffix"));    
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("Title"))){
            billToElem.setAttribute("Title",billingPersonInfoElem.getAttribute("Title"));    
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("UseCount"))){
            billToElem.setAttribute("UseCount",billingPersonInfoElem.getAttribute("UseCount"));    
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("VerificationStatus"))){
            billToElem.setAttribute("VerificationStatus",billingPersonInfoElem.getAttribute("VerificationStatus"));    
        }
        if(!StringUtil.isEmpty(billingPersonInfoElem.getAttribute("ZipCode"))){
            billToElem.setAttribute("ZipCode",billingPersonInfoElem.getAttribute("ZipCode"));    
        }        
        logger.verbose("billToElem after set operation :"+XMLUtil.getElementXMLString(billToElem));
    }
    
    public Document validateAddress(YFSEnvironment environment, Document document) throws Exception {        
        return document;
    }
    
    public static void main(String[] args) throws Exception{
        
        Document opAPIDoc = XMLUtil.getDocument("<CustomerList><Customer CanConsumeSupplementalCapacity=\"N\" CustomerID=\"unitIDPrefixunitIDSuffix\" CustomerKey=\"20080528193218868332\" CustomerType=\"02\" OrganizationCode=\"NWCG\" SlotPreferenceType=\"NONE\"><Extn ExtnActiveFlag=\"Y\" ExtnAgency=\"BIA\" ExtnCustomerName=\"customer\" ExtnCustomerType=\"01\" ExtnDepartment=\"DOD\" ExtnGACC=\"AK (Alaska Interagency Coordination Center)\" ExtnUnitType=\"Federal\" /><Consumer BillingAddressKey=\"20080603104135875930\"><BillingPersonInfo AddressLine1=\"\" AddressLine2=\"\" AddressLine3=\"\" AddressLine4=\"\" AddressLine5=\"\" AddressLine6=\"\" AlternateEmailID=\"\" Beeper=\"\" City=\"Lowell\" Company=\"NWCG\" Country=\"US\" Createprogid=\"Console\" Createts=\"20080603T10:41:3404:00\" Createuserid=\"nwcgsys\" DayFaxNo=\"\" DayPhone=\"\" Department=\"\" EMailID=\"\" ErrorTxt=\"\" EveningFaxNo=\"\" EveningPhone=\"\" FirstName=\"s\" HttpUrl=\"\" JobTitle=\"\" LastName=\"d\" Lockid=\"0\" MiddleName=\"\" MobilePhone=\"\" Modifyprogid=\"Console\" Modifyts=\"20080603T10:41:3404:00\" Modifyuserid=\"nwcgsys\" OtherPhone=\"\" PersonID=\"\" PersonInfoKey=\"20080603104135875930\" PreferredShipAddress=\"\" State=\"MA\" Suffix=\"\" Title=\"\" UseCount=\"0\" VerificationStatus=\"\" ZipCode=\"01851\" /></Consumer></Customer></CustomerList>");
        
        XPathWrapper pathWrapper = new XPathWrapper(opAPIDoc);
        Element billingPersonInfoElem = (Element)pathWrapper.getNode(NWCGConstants.XPATH_PERSON_BILLING_INFO);
        
        logger.verbose("elem :"+XMLUtil.getElementXMLString(billingPersonInfoElem));
        //setElementAttributes(document, billingPersonInfoElem);        
    }
}