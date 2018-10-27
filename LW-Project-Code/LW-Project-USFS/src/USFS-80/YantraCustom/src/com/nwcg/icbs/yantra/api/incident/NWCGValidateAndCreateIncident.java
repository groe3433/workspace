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
	This API is used to check if there is a invalid customer id entered when an incident is created.
**************************/
public class NWCGValidateAndCreateIncident implements YIFCustomApi
{	
	private static Logger logger = Logger.getLogger(NWCGValidateAndCreateIncident.class.getName());

	private Properties props = null;
	
	public void setProperties(Properties props) throws Exception {
		this.props = props;
	}	
	
	/**
	 * main method to be invoked by framework
	 * checks for incident number assignment and any issue created against it
	 * if issue and incident assignment doesnt exists deletes the incident 
	 */
	public Document validateAndCreateIncident(YFSEnvironment env, Document inDoc) throws Exception
	{
		logger.verbose("Create Incident Input XML "+ XMLUtil.extractStringFromDocument(inDoc));
		
		Element elemCustomerID = inDoc.getDocumentElement();
		String CustomerId = elemCustomerID.getAttribute(NWCGConstants.INC_CUST_ID_ATTR);

		Document CustDetailsInput = XMLUtil.createDocument(NWCGConstants.CUST_CUST_ELEMENT);
		CustDetailsInput.getDocumentElement().setAttribute(NWCGConstants.CUST_CUST_ID_ATTR,CustomerId);
		
		//System.out.println("Customer Details Input: "+ XMLUtil.extractStringFromDocument(CustDetailsInput));
		
		Document CustomerList = null;
		
		// customer validation
		if (CustomerId.length() > 0)
		{
			CustomerList = getCustDetails(env,CustDetailsInput);
		} else {
			throw new NWCGException("NWCG_CUSTOMER_VALIDATE_001",new Object[] {CustomerId});
		}
		
		NodeList CusList = CustomerList.getElementsByTagName(NWCGConstants.CUST_CUST_ELEMENT);
		
		if (CusList.getLength() == 0)
		{
			throw new NWCGException("NWCG_CUSTOMER_VALIDATE_001",new Object[] {CustomerId});
		}
		// NEED VALIDATION FOR INCIDENT OTHER ORDER - FIND IF INCIDENT OR OTHER ORDER
		// IF OTHER ORDER
		// CALL OTHER OTHER ORDER FUNCTION - NWCGGetOtherOrderNumber Function
		Element elemOtherOrder = inDoc.getDocumentElement();
		String OtherOrder = elemOtherOrder.getAttribute("IsOtherOrder");
		
		//System.out.println("Other Order Number: " + OtherOrder);
		
		if (OtherOrder.equals(NWCGConstants.YES))
		{
			getOtherOrderNumber(env,inDoc);
			//System.out.println("INSIDE THE OTHER ORDER CONDITION");			
		} 
		
		Document CustIncidentDetails = CommonUtilities.invokeService(env,NWCGConstants.SVC_CREATE_INCIDENT_ORDER_SVC,inDoc);
	
		return CustIncidentDetails;
	}


	public Document getCustDetails(YFSEnvironment env, Document getCustDetailsInput) throws Exception
	{
		Document CustDtls = null;
		try {
		    CustDtls = XMLUtil.getDocument();
			CustDtls = CommonUtilities.invokeAPI(env,NWCGConstants.API_GET_CUSTOMER_LIST,getCustDetailsInput);
		}
		catch(ParserConfigurationException pce){
			logger.error("Parse configuration error thrown");
		}
		catch(Exception e){
			logger.error("there was an exception made");
		}

		logger.verbose("Customer List: "+ XMLUtil.extractStringFromDocument(CustDtls));		
		return CustDtls;
	}
	
	public Document getOtherOrderNumber(YFSEnvironment arg0, Document arg1) throws NWCGException {

		try {		
			logger.verbose("Input XML for getOtherOrder" + XMLUtil.extractStringFromDocument(arg1));
			Element root = arg1.getDocumentElement();
			String strCacheID = root.getAttribute(NWCGConstants.PRIMARY_CACHE_ID);
			
			String strOtherOrderNum = NWCGConstants.EMPTY_STRING; 
			root.setAttribute(NWCGConstants.INCIDENT_NO_ATTR, strOtherOrderNum);
			
			//creating an input document
			Document inputXML = XMLUtil.createDocument("NextReceiptNo");
			inputXML.getDocumentElement().setAttribute("ReceiptNo","");
			inputXML.getDocumentElement().setAttribute("SequenceType","OTHERORDER");
			inputXML.getDocumentElement().setAttribute(NWCGConstants.CACHE_ID_ATTR,strCacheID);
			logger.verbose("Input XML that was just created: "+ XMLUtil.extractStringFromDocument(inputXML));
			
			Document outputXML = CommonUtilities.invokeService(arg0,"NWCGGenerateReceiptNoService",inputXML);
			
			//catch receipt number 
			Element receiptNo = outputXML.getDocumentElement();
			String nextReceiptNo = receiptNo.getAttribute("ReceiptNo");
			
			root.setAttribute(NWCGConstants.INCIDENT_NO_ATTR, nextReceiptNo);			
			logger.verbose("Generate Sequence Number: "+ XMLUtil.extractStringFromDocument(outputXML));
		}
	
		catch (ParserConfigurationException e) {
			e.printStackTrace();
			logger.error("ParserConfigurationException " + e.getMessage());
			throw new NWCGException("ParserConfigurationException "
					+ e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception " + e.getMessage());
			throw new NWCGException("Exception " + e.getMessage());
		}
		return (arg1);
	}			
}