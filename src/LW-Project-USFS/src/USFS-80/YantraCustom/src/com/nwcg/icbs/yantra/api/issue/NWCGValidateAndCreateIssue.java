package com.nwcg.icbs.yantra.api.issue;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
/*************************
	CR 610 
	1. This API is used to check if the entered incident/year is a invalid.
	2. This API is used to check if there is a invalid customer id entered when an issue is created.
**************************/
public class NWCGValidateAndCreateIssue implements YIFCustomApi
{
	private static Logger log = Logger.getLogger(NWCGValidateAndCreateIssue.class.getName());

	private Properties props = null;
	
	public void setProperties(Properties props) throws Exception {
		this.props = props;
	}
	
	/*
	 * main method to be invoked by framework
	 * checks for incident number assignment and any issue created against it
	 * if issue and incident assignment doesnt exists throw an exception 
	 */
	public Document validateAndCreateIssue(YFSEnvironment env, Document inDoc) throws Exception
	{
                //System.out.println("NWCGValidateAndCreateIssue::validateAndCreateIssue::Begin");
                //System.out.println("Input XML =>"+ XMLUtil.getXMLString(inDoc));
                Element orderNode = inDoc.getDocumentElement();
		//System.out.println("Element orderNode =>"+ XMLUtil.getElementXMLString(orderNode));


		// 1.validate the entered incidentNo/Year
		Element orderExtnElm = (Element)XMLUtil.getChildNodeByName(orderNode, "Extn");
                //System.out.println("Element orderExtnElm =>"+ XMLUtil.getElementXMLString(orderExtnElm));
		String extnIncidentNo = orderExtnElm.getAttribute("ExtnIncidentNo");
		String extnIncidentYear = orderExtnElm.getAttribute("ExtnIncidentYear");
		//System.out.println("extnIncidentNo/year [" +extnIncidentNo+"/"+extnIncidentYear+"]");
		Document getIncidentDetailsInput = XMLUtil.createDocument("NWCGIncidentOrder");
		getIncidentDetailsInput.getDocumentElement().setAttribute("IncidentNo", extnIncidentNo);
		if(extnIncidentYear != null && (!extnIncidentYear.equals(""))) {
			// Set IncidentYear when year is passed - incident issue case
			getIncidentDetailsInput.getDocumentElement().setAttribute("Year", extnIncidentYear);
		} else {
			// Set IncidentYear with blank when year is NOT passed - Other issue case
			getIncidentDetailsInput.getDocumentElement().setAttribute("Year", " ");
		}
		// get the incident details
		Document incident = CommonUtilities.invokeService(env, NWCGConstants.NWCG_GET_INCIDENT_ORDER_SERVICE, getIncidentDetailsInput);
		if(incident == null)
		{
			throw new NWCGException("NWCG_INCIDENT_VALIDATE_001",new Object[] {extnIncidentNo,extnIncidentYear});
		}


		// 2.validate the entered customerID
		String CustomerId = orderNode.getAttribute("BillToID");		
		//System.out.println("Customer & Bill to ID: " + CustomerId);
		Document CustDetailsInput = XMLUtil.createDocument("Customer");
		CustDetailsInput.getDocumentElement().setAttribute("CustomerID",CustomerId);
		//System.out.println("Customer Details Input: "+ XMLUtil.getXMLString(CustDetailsInput));
		Document CustomerList = null;
		if (CustomerId.length() > 0) {
			CustomerList = getCustDetails(env,CustDetailsInput);
		} else {
			throw new NWCGException("NWCG_CUSTOMER_VALIDATE_001",new Object[] {CustomerId});
		}
		NodeList CusList = CustomerList.getElementsByTagName("Customer");
		if (CusList.getLength() == 0) {
			throw new NWCGException("NWCG_CUSTOMER_VALIDATE_001",new Object[] {CustomerId});
		}


                //System.out.println("BEFORE invoke CreateOrder: Input XML =>"+ XMLUtil.getXMLString(inDoc));
		Document CustIncidentDetails = CommonUtilities.invokeAPI(env,"createOrder",inDoc);
                //System.out.println("AFTER invoke CreateOrder: Input XML =>"+ XMLUtil.getXMLString(inDoc));

//Begin CR846
		//System.out.println("NWCGValidateAndCreateIssue::validateAndCreateIssue::CustIncidentDetails" +XMLUtil.getXMLString(CustIncidentDetails));
                Element rootElemOrder = CustIncidentDetails.getDocumentElement();
                String strOrderHeaderKey = rootElemOrder.getAttribute("OrderHeaderKey");
                //System.out.println("NWCGValidateAndCreateIssue::validateAndCreateIssue::strOrderHeaderKey"+ strOrderHeaderKey);

                // instantiate and populate the changeOrder document
		Document changeOrderDocument = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
		Element rootElemChangeOrderDocument = changeOrderDocument.getDocumentElement();
		rootElemChangeOrderDocument.setAttribute(NWCGConstants.ORDER_HEADER_KEY, strOrderHeaderKey);
		rootElemChangeOrderDocument.setAttribute(NWCGConstants.ACTION, NWCGConstants.MODIFY);
		rootElemChangeOrderDocument.setAttribute(NWCGConstants.OVERRIDE, NWCGConstants.YES);
		rootElemChangeOrderDocument.setAttribute("ModificationReasonCode", "Miscellaneous");
		rootElemChangeOrderDocument.setAttribute("ModificationReasonText", "CR846 ICBS fill messages for Backorder and Forwardorder quantity changes made to IncidentIssueType Normal and IncidentReplenishment should be notified to Ross");
		rootElemChangeOrderDocument.setAttribute("ModificationReference1", "Time: "+CommonUtilities.getXMLCurrentTime());
		rootElemChangeOrderDocument.setAttribute("ModificationReference2", "Login ID: "+env.getUserId());
                //System.out.println("NWCGValidateAndCreateIssue::validateAndCreateIssue::changeOrderDocument BEFORE appending Extn"+ XMLUtil.getXMLString(changeOrderDocument));
		
		// Append the OrderHeader Extn element and populate attributes
		Element extnElemChangeOrderDocument = changeOrderDocument.createElement(NWCGConstants.EXTN_ELEMENT);
		rootElemChangeOrderDocument.appendChild(extnElemChangeOrderDocument);
		extnElemChangeOrderDocument.setAttribute(NWCGConstants.EXTN_BASE_ORDER_HEADER_KEY, strOrderHeaderKey);
                //System.out.println("NWCGValidateAndCreateIssue::validateAndCreateIssue::changeOrderDocument AFTER appending Extn"+ XMLUtil.getXMLString(changeOrderDocument));

                Document apiOutputDoc = null;
		Document changeOrderOutputDocument = null;
		try {
			Document outputTemplate = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
			Element extnElemOutputTemplate = outputTemplate.createElement(NWCGConstants.EXTN_ELEMENT);
			outputTemplate.getDocumentElement().appendChild(extnElemOutputTemplate);
			changeOrderOutputDocument = CommonUtilities.invokeAPI(env, outputTemplate, NWCGConstants.API_CHANGE_ORDER, changeOrderDocument);
                        //System.out.println("NWCGValidateAndCreateIssue::validateAndCreateIssue::changeOrderOutputDocument"+ XMLUtil.getXMLString(changeOrderOutputDocument));

		}
		catch (Exception exc0) {
			log.error("ERROR occured while updating the IncidentIssue column EXTN_BASE_ORDER_HEADER_KEY");
			exc0.printStackTrace();
			throw exc0;
		}		
//End CR846                                

                //System.out.println("NWCGValidateAndCreateIssue::validateAndCreateIssue::End");
		return CustIncidentDetails;
	}

	public Document getCustDetails(YFSEnvironment env, Document getCustDetailsInput) throws Exception
	{
		Document CustDtls = null;
		try {
		    CustDtls = XMLUtil.getDocument();
			CustDtls = CommonUtilities.invokeAPI(env,"getCustomerList",getCustDetailsInput);
		}
		catch(ParserConfigurationException pce){
//			log.error("NWCGValidateAndDeleteCustomer::getCustDetails, ParserConfigurationException Msg : " + pce.getMessage());
//			log.error("NWCGValidateAndDeleteCustomer::getCustDetails, StackTrace : " + pce.getStackTrace());
			//System.out.println("Parse configuration error thrown");
		}
		catch(Exception e){
//			log.error("NWCGValidateAndDeleteCustomer::getCustDetails, Exception Msg : " + e.getMessage());
//			log.error("NWCGValidateAndDeleteCustomer::getCustDetails, StackTrace : " + e.getStackTrace());
			//System.out.println("there was an exception");
		}
		//System.out.println("Customer List: "+ XMLUtil.getXMLString(CustDtls));
		return CustDtls;
	}
}
