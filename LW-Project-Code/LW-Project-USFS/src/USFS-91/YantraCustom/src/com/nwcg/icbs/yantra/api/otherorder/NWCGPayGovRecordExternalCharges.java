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

package com.nwcg.icbs.yantra.api.otherorder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.core.YFSSystem;

public class NWCGPayGovRecordExternalCharges implements YIFCustomApi 
{
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGPayGovRecordExternalCharges.class);
	
	private Properties myProperties = null;
	
	public void setProperties(Properties arg0) throws Exception 
	{
		this.myProperties = arg0;
	}
	
	public Document buildInputForRecordExternalCharges(YFSEnvironment env, Document soapResponse) throws Exception {
		
		/*
			<RecordExternalCharges TransactionId="25F2575V" EnterpriseCode="NWCG" OrderNo="0000695417" OrderHeaderKey="20140313140352126115207">
			<PaymentMethod CreditCardNo="****************" FirstName="John" LastName="Doe" PaymentType="CREDIT_CARD" PaymentReference1="70482P" >
			<PaymentDetails ChargeType="CHARGE" ProcessedAmount="7.00" TranReturnCode="2002" TranReturnMessage="Successful submission of PCSale" CollectionDate="2014-03-29T17:48:35" AuthCode="00" Reference1="367634125" Reference2="P"/>
			</PaymentMethod>
			</RecordExternalCharges> 
		*/
		
		logger.verbose("********************************************");
		logger.verbose("            recordExternalCharges           ");
		logger.verbose("********************************************");
	
		logger.verbose("pay.gov response:: "+ XMLUtil.getXMLString(soapResponse));
		Element rootElement = soapResponse.getDocumentElement();
		String prefix =  NWCGConstants.NWCG_PAY_GOV_WEBSERVICE_PREFIX;
		
		String sPayGovTrackingID = getString(prefix+ NWCGConstants.NWCG_PAY_GOV_TRACKING_ID, rootElement);
		String sAgencyTrackingID = getString(prefix+ NWCGConstants.NWCG_PAY_GOV_AGENCY_TRACKING_ID, rootElement);
		String sOrderNo = getString(prefix+ NWCGConstants.ORDER_NO, rootElement);
		String sTransactionAmount = getString(prefix+ NWCGConstants.NWCG_PAY_GOV_TRANSACTION_AMOUNT, rootElement);
		String sReturnCode = getString(prefix+ NWCGConstants.NWCG_PAY_GOV_RETURN_CODE, rootElement);
		String sReturnDetail = getString(prefix+ NWCGConstants.NWCG_PAY_GOV_RETURN_DETAIL, rootElement);
		//String sTransactionStatus = getString(prefix+"transaction_status", rootElement);Not Needed
		String sTransactionDate = getString(prefix+ NWCGConstants.NWCG_PAY_GOV_TRANSACTION_DATE, rootElement);
		String sApprovalCode = getString(prefix+ NWCGConstants.NWCG_PAY_GOV_APPROVAL_CODE, rootElement);
		String sAuthResponseCode = getString(prefix+ NWCGConstants.NWCG_PAY_GOV_AUTH_RESPONSE_CODE, rootElement);
		//String sAuthResponseText = getString(prefix+"auth_response_text", rootElement);Not Used
		//String sAVSResponseCode = getString(prefix+"avs_response_code", rootElement);Not Used
		String sCSCResult = getString(prefix+ NWCGConstants.NWCG_PAY_GOV_CSC_RESULT, rootElement);
		//String sAuthorizedAmount = getString(prefix+"authorized_amount", rootElement);Not used
		//String sRemainingBalance = getString(prefix+"remaining_balance", rootElement);Not used
		String sAction = getString(prefix+ NWCGConstants.NWCG_PAY_GOV_ACTION, rootElement);
		
		String sFirstName = getString(prefix+ "iFirstName", rootElement);
		String sLastName = getString(prefix+ "iLastName", rootElement);
		
		Document recordExternalChargesInputDoc = XMLUtil.newDocument();
		Element el_recordExternalCharges = recordExternalChargesInputDoc.createElement(NWCGConstants.RECORD_EXTERNAL_CHARGES);

		recordExternalChargesInputDoc.appendChild(el_recordExternalCharges);
		el_recordExternalCharges.setAttribute( NWCGConstants.ENTERPRISE_CODE_STR , NWCGConstants.NWCG_ENTERPRISE_CODE);
		
		//remove ICB prefix to sAgencyTrackingID, append leading zeros again...
		
		String sOrderHeaderKey = getOrderHeaderKeyForOtherOrder(env, sOrderNo);
		if( !sOrderHeaderKey.equals("") && sOrderHeaderKey != null)
		{
			el_recordExternalCharges.setAttribute( NWCGConstants.ORDER_HEADER_KEY ,sOrderHeaderKey);
		}
		
		el_recordExternalCharges.setAttribute( NWCGConstants.DOCUMENT_TYPE, NWCGConstants.DOCUMENT_TYPE_OTHERISSUE);
		el_recordExternalCharges.setAttribute( NWCGConstants.ORDER_NO , sOrderNo);
		el_recordExternalCharges.setAttribute( NWCGConstants.TRANSACTION_ID_ATTR ,sPayGovTrackingID);
		
		Element el_paymentMethod = recordExternalChargesInputDoc.createElement( NWCGConstants.PAYMENT_METHOD );
		el_recordExternalCharges.appendChild(el_paymentMethod);

		el_paymentMethod.setAttribute( NWCGConstants.CREDIT_CARD_NO_ATTR, "****************");
		el_paymentMethod.setAttribute( NWCGConstants.PAYMENT_TYPE_ATTR, NWCGConstants.NWCG_PAYMENT_CREDIT_CARD_TYPE);
		
		if( !sFirstName.equals("") && sFirstName != null)
		{
			logger.verbose("Include first name in External Charges - " + sFirstName);
			el_paymentMethod.setAttribute( "FirstName", sFirstName);
		}
		
		if( !sLastName.equals("") && sLastName != null)
		{
			logger.verbose("Include second name in External Charges - " + sLastName);
			el_paymentMethod.setAttribute( "LastName", sLastName);
		}
		
		el_paymentMethod.setAttribute( NWCGConstants.PAYMENT_REFERNCE_1_ATTR, sApprovalCode);
		el_paymentMethod.setAttribute( NWCGConstants.PAYMENT_REFERNCE_2_ATTR, sCSCResult);
		el_paymentMethod.setAttribute( NWCGConstants.PAYMENT_REFERNCE_3_ATTR, sAction);
		
		Element el_paymentDetails = recordExternalChargesInputDoc.createElement(NWCGConstants.PAYMENT_DETAILS);
		el_paymentMethod.appendChild(el_paymentDetails);
		
		el_paymentDetails.setAttribute( NWCGConstants.CHARGE_TYPE_ATTR, NWCGConstants.NWCG_CHARGE_TYPE);
		el_paymentDetails.setAttribute( NWCGConstants.PROCESSED_AMOUNT_ATTR, sTransactionAmount);
		el_paymentDetails.setAttribute( NWCGConstants.TRAN_RETURN_CODE_ATTR, sReturnCode);
		el_paymentDetails.setAttribute( NWCGConstants.TRAN_RETURN_MESSAGE_ATTR, sReturnDetail);
		el_paymentDetails.setAttribute( NWCGConstants.COLLECTION_DATE_ATTR, sTransactionDate);
		el_paymentDetails.setAttribute( NWCGConstants.AUTH_CODE_ATTR, sAuthResponseCode);
		//el_paymentDetails.setAttribute("Reference2", sCSCResult);
		//el_paymentDetails.setAttribute("Reference1", sAction);
		
		logger.verbose("recordExternalCharges API Input XML:: "+ XMLUtil.getXMLString(recordExternalChargesInputDoc));
		
		return recordExternalChargesInputDoc;
		
	}

	
	protected String getString(String tagName, Element element) 
	{
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();

            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }

        return null;
    }
	
	private String getOrderHeaderKeyForOtherOrder(YFSEnvironment myEnvironment, String sOrderNo) {
		
		StringBuffer sb = new StringBuffer("<Order OrderNo=\"");
		String sOrderHeaderKey = "";
		sb.append(sOrderNo);
		sb.append("\" EnterpriseCode='NWCG' DocumentType='0007.ex' />");
		
		Document getOrderDetailsOpDoc = null;
		
		StringBuffer sbOpTemplate = new StringBuffer("<Order OrderHeaderKey=\"\"/>");
		try {
			String inpStr = sb.toString();
			String inpTemplate = sbOpTemplate.toString();
			
			Document inptDoc = XMLUtil.getDocument(inpStr);
			Document opTemplate = XMLUtil.getDocument(inpTemplate);
			
			logger.verbose("Input:: "+ XMLUtil.getXMLString(inptDoc));
			logger.verbose("Template:: "+ XMLUtil.getXMLString(opTemplate));
			
			getOrderDetailsOpDoc = CommonUtilities.invokeAPI(myEnvironment, opTemplate, NWCGConstants.API_GET_ORDER_DETAILS, inptDoc);

			logger.verbose("Output:: "+ XMLUtil.getXMLString(getOrderDetailsOpDoc));
			
		} catch (Exception e) {
			//logger.printStackTrace(e);	
			throw new YFSException("Exception thrown while calling getCustomerDetails API" + e);			
		}
	
		if (getOrderDetailsOpDoc != null) 
		{
			Element opDocElm = getOrderDetailsOpDoc.getDocumentElement();
			sOrderHeaderKey = opDocElm.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
						
		}	
		
		return sOrderHeaderKey;
	}

	
}