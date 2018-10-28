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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.core.YFSSystem;

public class NWCGPayGovSOAP implements YIFCustomApi 
{
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGPayGovSOAP.class);
	
	private Properties myProperties = null;
	
	public void setProperties(Properties arg0) throws Exception 
	{
		this.myProperties = arg0;
	}
	
	public Document callPayGovRequest(YFSEnvironment env,Document inXML) throws Exception 
	{	
		logger.verbose("********************************************");
		logger.verbose("          Call Pay.Gov Webservice           ");
		logger.verbose("********************************************");
		
		logger.verbose("INPUT XML:: "+ XMLUtil.getXMLString(inXML));
		SOAPMessage reply = null;
		String sReturnCode = NWCGConstants.PAYGOV_DEFAULT_RETURN_CODE;
		String sReturnDesc = NWCGConstants.PAYGOV_DEFAULT_RETURN_DESC;
		//String sKSPath = NWCGConstants.PAYGOV_KSPATH;
        //String sJKPassword = NWCGConstants.PAYGOV_JKPASSWORD;
		String sKSType = YFSSystem.getProperty("nwcg.paygov.keystore.type");
        String sKSPath = YFSSystem.getProperty("nwcg.paygov.keystore.file");
        String sKSPassword = YFSSystem.getProperty("nwcg.paygov.keystore.password");
        
        if( sKSType.equals("") || sKSType == null)
        {
        	logger.verbose("No KeyStore Type found in properties!");
        }
        
        if( sKSPath.equals("") || sKSPath == null)
        {
        	logger.verbose("No KeyStore Path found in properties!");
        }
        
        if( sKSPassword.equals("") || sKSPassword == null)
        {
        	logger.verbose("No KeyStore Password found in properties!");
        }  
        
        String sURL = "";
		
		if(inXML != null)
		{
			SOAPMessage soapMessageRequest = createSOAPRequest(inXML);
			SOAPConnectionFactory myFct = null;
			SOAPConnection myCon = null;
			try 
			{
				//System.setProperty("java.protocol.handler.pkgs", "com.ibm.net.ssl.internal.www.protocol");
		        //Security.addProvider(new com.ibm.jsse.IBMJSSEProvider()); 
				
		        System.setProperty("javax.net.ssl.keyStoreType", sKSType);
				System.setProperty("javax.net.ssl.keyStore",  sKSPath);
		        System.setProperty("javax.net.ssl.keyStorePassword",sKSPassword);
		        System.setProperty("javax.net.ssl.trustStoreType", sKSType);
		        System.setProperty("javax.net.ssl.trustStore", sKSPath);
		        System.setProperty("javax.net.ssl.trustStorePassword", sKSPassword);
		  
		        System.setProperty("com.ibm.ssl.keyStore", sKSPath);
		        System.setProperty("com.ibm.ssl.keyStorePassword", sKSPassword);
		        System.setProperty("com.ibm.ssl.trustStoreType", sKSType);
		        System.setProperty("com.ibm.ssl.trustStore", sKSPath);
		        System.setProperty("com.ibm.ssl.trustStorePassword", sKSPassword);
				
		        System.setProperty("com.ibm.ssl.protocol", "TLSv1.2");
		        
		        sURL = YFSSystem.getProperty("nwcg.paygov.endpoint");
		        
		        if( sURL.equals("") || sURL == null)
		        {
		        	//sURL = NWCGConstants.NWCG_PAY_GOV_ENDPOINT;
		        	logger.verbose("No EndPoint URL found in properties!");
		        }
		        
				URL endPt = new URL(sURL);
				myFct = SOAPConnectionFactory.newInstance();
				myCon = myFct.createConnection();
				reply = myCon.call(soapMessageRequest, endPt);
			} 
			catch (SOAPException soapExp) 
			{				
				YFSException ne = new YFSException();
				ne.setErrorCode(sReturnCode);
				ne.setErrorDescription(sReturnDesc);
				throw ne;
			}
			catch(Exception e)
			{
				YFSException ne = new YFSException();
				ne.setErrorCode(sReturnCode);
				ne.setErrorDescription(sReturnDesc);
				throw ne;
			}
			finally
			{
				myCon.close();
			}
			
			logger.verbose("\nAfter Calling SOAP Request to pay.gov:");
			reply.writeTo(System.out);
			
			String sReturn = processResponse(reply);
			
			if(!sReturn.equals(",") && sReturn !=  null)
			{
			    String[] items = sReturn.split(",");
			    sReturnCode = items[0];
			    sReturnDesc = items[1];
			}
		    
			if( sReturnCode != null)
			{
				if(!sReturnCode.equals( NWCGConstants.PAYGOV_SUCCESSFUL_RETURN_CODE ))
				{
					YFSException ne = new YFSException();
					ne.setErrorCode(sReturnCode);
					ne.setErrorDescription(sReturnDesc);
					throw ne;
				}
			}
			else
			{
				YFSException ne = new YFSException();
				ne.setErrorCode(NWCGConstants.PAYGOV_DEFAULT_RETURN_CODE);//Default
				ne.setErrorDescription(NWCGConstants.PAYGOV_DEFAULT_RETURN_DESC);
				throw ne;
			}
		}
			
		return toDocument(reply);
	}
	
	public String processResponse(SOAPMessage reply) throws Exception {
		
		logger.verbose("In NWCGPayGovSOAP.processResponse: ");
		
		String sReturnCode = "";
		String sDescription = "";
		String prefix = NWCGConstants.NWCG_PAY_GOV_WEBSERVICE_PREFIX;
		
		SOAPBody soapbodyResponse = reply.getSOAPBody();
		logger.verbose("In NWCGPayGovSOAP.processResponse.soapbodyResponse: "+soapbodyResponse);
		
		if (soapbodyResponse.hasFault()) 
		{
			SOAPFault soapFault = soapbodyResponse.getFault();
			String faultStr = soapFault.getFaultString();
			throw new YFSException("Login failed - " + faultStr);
		} 
		else 
		{
			Document docResponse = toDocument(reply);
			
			if(docResponse != null)
			{
				Element rootReponseElement = docResponse.getDocumentElement();
				
				if(rootReponseElement != null)
				{
					sReturnCode = getString(prefix+ NWCGConstants.NWCG_PAY_GOV_RETURN_CODE, rootReponseElement);
					sDescription = getString(prefix+ NWCGConstants.NWCG_PAY_GOV_RETURN_DETAIL, rootReponseElement);
				}
			}
			
			
		}
		
		return sReturnCode+","+sDescription;
	}


    public SOAPMessage createSOAPRequest(Document inXML) throws Exception 
    {
		Element rootElement = inXML.getDocumentElement();
		
		/* inXML:
		<PCSale Action="FED" AddressLine1="WEST REGIONAL OFFICE"
		    AddressLine2="300 E MALLARD DR SUITE 200" AddressLine3=""
		    AgencyTrackingId="0000722992" City="BOISE" Country="US"
		    CustomerName="NBC AVIATION MANAGEMENT" FirstName="JUDY"
		    IgnoreOrdering="Y" IssueQuantity="1" IssueTotalCost="112.32"
		    LastName="RAGAIN" OrderHeaderKey="20140626091402162778262"
		    SCAC="ALL CACHES" ScacAndService="ALL CACHES - BEST MEANS"
		    ShippingQuantity="0" ShippingTotalCost="999" State="ID"
		    TransactionAmount="1111.32" ZipCode="83706"
		    account_number="4556640711036841" credit_card_expiration_date="09-2016"/>
		*/
		
		String sAgencyTrackingID = rootElement.getAttribute( NWCGConstants.AGENCY_TRACKING_ID );
		String sAgencyTrackingID_CBS = "";
		String sTransactionAmount = rootElement.getAttribute( NWCGConstants.TRANSACTION_AMOUNT_ATTR );
		String sAccountNumber = rootElement.getAttribute( NWCGConstants.ACCOUNT_NUMBER_ATTR);
		String sExpDate = rootElement.getAttribute( NWCGConstants.CC_EXP_DATE_ATTR);
		String sRequestPrefix = "sch";
		
		//Reverse date to avoid schema errors
		String[] parts = sExpDate.split("-");
		String part1 = parts[0]; 
		String part2 = parts[1]; 
		String sExpDateReversed = part2 + "-" +  part1;
		
		String sCustType = rootElement.getAttribute( NWCGConstants.ACTION);
		String sIssueCBSCode = "";
		String sShipCBSCode = "";
		
		if(sCustType.equals("FED"))
		{
			sIssueCBSCode = "FED_ISSUE"; //NWCGConstants.ISSUE_CBS_CODE_FED
			sShipCBSCode = "FED_SHIP"; //NWCGConstants.SHIP_CBS_CODE_FED;
		}
		
		if(sCustType.equals("NONFED"))
		{
			sIssueCBSCode = "NONFED_ISSUE"; //NWCGConstants.ISSUE_CBS_CODE_NONFED;
			sShipCBSCode = "NONFED_SHIP"; //NWCGConstants.SHIP_CBS_CODE_NONFED;
		}

		String sAddressLine1 = rootElement.getAttribute( NWCGConstants.ADDRESS_LINE_1_ATTR );
		//sAddressLine1 = sAddressLine1.replace("&amp;" , "&").replace("&lt;" , "<").replace("&gt;" , ">").replace("&quot;", "\"").replace("&apos;", "''");

		String sAddressLine2 = rootElement.getAttribute( NWCGConstants.ADDRESS_LINE_2_ATTR );
		//sAddressLine2 = sAddressLine2.replace("&amp;" , "&").replace("&lt;" , "<").replace("&gt;" , ">").replace("&quot;", "\"").replace("&apos;", "''");

		String sAddressLine3 = rootElement.getAttribute( NWCGConstants.ADDRESS_LINE_3_ATTR );
		//sAddressLine3 = sAddressLine3.replace("&amp;" , "&").replace("&lt;" , "<").replace("&gt;" , ">").replace("&quot;", "\"").replace("&apos;", "''");

		String sCity = rootElement.getAttribute( NWCGConstants.CITY );
		String sState = rootElement.getAttribute( NWCGConstants.STATE );
		String sCountry = rootElement.getAttribute( NWCGConstants.COUNTRY );//UK - 826, USA - 840
		String sZipCode = rootElement.getAttribute( NWCGConstants.ZIP_CODE );
		String sBusinessName = rootElement.getAttribute( NWCGConstants.CUSTOMER_NAME );
		//sBusinessName = sBusinessName.replace("&amp;" , "&").replace("&lt;" , "<").replace("&gt;" , ">").replace("&quot;", "\"").replace("&apos;", "''");
		//logger.verbose("***Business name: " + sBusinessName);
		
		String sFirstName = rootElement.getAttribute( NWCGConstants.FIRST_NAME );
		String sLastName = rootElement.getAttribute( NWCGConstants.LAST_NAME );
		String sService = rootElement.getAttribute( NWCGConstants.SCAC_ATTR );
		//String sIssueQty = rootElement.getAttribute( NWCGConstants.ISSUE_QUANTITY_ATTR );
		String sIssueAmount = rootElement.getAttribute( NWCGConstants.ISSUE_TOTAL_COST_ATTR);
		//String sShipQty = rootElement.getAttribute( NWCGConstants.SHIPPING_QUANTITY_ATTR);
		String sShipCost = rootElement.getAttribute( NWCGConstants.SHIPPING_TOTAL_COST_ATTR);
		
		//Additional Fields...
		String iFirstName = rootElement.getAttribute( "iFirstName" );
		String iLastName = rootElement.getAttribute( "iLastName" );
		
		 if( iFirstName == null)
	     {
			 iFirstName = "";
	     }
		 
		 if( iLastName == null)
	     {
			 iLastName = "";
	     }
		
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        
		soapMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION , "true");
		soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING , "UTF-8");

        String serverURI = NWCGConstants.NWCG_PAYGOV_SERVER_URI;

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(sRequestPrefix, serverURI);

		String sAgencyID = "";
		String sTCSAppID="";
		logger.verbose("Properties: ");
		
		sAgencyID = YFSSystem.getProperty("nwcg.paygov.agencyid");
		sTCSAppID = YFSSystem.getProperty("nwcg.paygov.tcsappid");

		if( sAgencyID.equals("") || sAgencyID == null) 
		{
			logger.verbose("Property yfs.nwcg.paygov.agencyid is null, setting default value");
			sAgencyID = NWCGConstants.NWCG_PAY_GOV_AGENCY_ID_VALUE;//1240
		}
		
		if( sTCSAppID.equals("") || sTCSAppID == null)
		{
			logger.verbose("Property yfs.nwcg.paygov.tcsappid is null, setting default value");
			sTCSAppID = NWCGConstants.NWCG_PAY_GOV_TCS_APP_ID_VALUE;//TCSICBSCERT
		}

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement pcSaleRequestElem = soapBody.addChildElement( NWCGConstants.NWCG_PAY_GOV_PCSALE_REQUEST , sRequestPrefix);
        
        SOAPElement agencyIDElem = pcSaleRequestElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_AGENCY_ID, sRequestPrefix);
        agencyIDElem.addTextNode(sAgencyID);
        
        SOAPElement tcsAppIDElem = pcSaleRequestElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_TCS_APP_ID, sRequestPrefix);
        tcsAppIDElem.addTextNode(sTCSAppID);
        
        SOAPElement pcSaleElem = pcSaleRequestElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_PCSALE, sRequestPrefix);

        SOAPElement agencyTrackingIDElem = pcSaleElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_AGENCY_TRACKING_ID, sRequestPrefix);
        
        //remove leading zeros and append 105 - Requirement for CBS, requested by Therese
        if(sAgencyTrackingID != null)
        {
			sAgencyTrackingID_CBS = sAgencyTrackingID;
			Pattern p = Pattern.compile("^\\d+$");
			Matcher m = p.matcher(sAgencyTrackingID_CBS);
			
			if(m.matches())
			{
			  String resultWithZerosRemoved =  sAgencyTrackingID_CBS.replaceAll("^0+", "");
			  sAgencyTrackingID_CBS  = "105" + resultWithZerosRemoved;
			  logger.verbose(" Agency Tracking id (Altered for CBS): " + sAgencyTrackingID_CBS);
			} 
			else 
			{
			  logger.verbose("Issue Number does not contain only numbers: " + sAgencyTrackingID_CBS);
			}
        }
        agencyTrackingIDElem.addTextNode(sAgencyTrackingID_CBS);
        
        SOAPElement transactionAmountElem = pcSaleElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_TRANSACTION_AMOUNT, sRequestPrefix);
        transactionAmountElem.addTextNode(sTransactionAmount);
        SOAPElement accountNumberElem = pcSaleElem.addChildElement( NWCGConstants.ACCOUNT_NUMBER_ATTR, sRequestPrefix);
        accountNumberElem.addTextNode(sAccountNumber);
        SOAPElement  expirationDateElem = pcSaleElem.addChildElement( NWCGConstants.CC_EXP_DATE_ATTR, sRequestPrefix);
        expirationDateElem.addTextNode(sExpDateReversed);
        
        /*Not needed anymore due to custom field change. Business name will go into custom field 3.
         * if(!sBusinessName.equals("") && sBusinessName != null)
        {
	        SOAPElement  businessNameElem = pcSaleElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_BUSINESS_NAME, sRequestPrefix);
	        businessNameElem.addTextNode(sBusinessName); 
        }
        else
        {
        	SOAPElement  firstNameElem = pcSaleElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_FIRST_NAME, sRequestPrefix);
	        firstNameElem.addTextNode(sFirstName);
	        SOAPElement  lastNameElem = pcSaleElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_LAST_NAME, sRequestPrefix);
	        lastNameElem.addTextNode(sLastName);   
        }*/
        
    	SOAPElement  firstNameElem = pcSaleElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_FIRST_NAME, sRequestPrefix);
        firstNameElem.addTextNode(sFirstName);
        SOAPElement  lastNameElem = pcSaleElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_LAST_NAME, sRequestPrefix);
        lastNameElem.addTextNode(sLastName);   
        
 
        SOAPElement  billingAddressElem = pcSaleElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_BILLING_ADDRESS, sRequestPrefix);
        billingAddressElem.addTextNode(sAddressLine1);        
        SOAPElement billingAddress2Elem = pcSaleElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_BILLING_ADDRESS_2, sRequestPrefix);
        billingAddress2Elem.addTextNode(sAddressLine2);       
        SOAPElement  billingCityElem= pcSaleElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_BILLING_CITY, sRequestPrefix);
        billingCityElem.addTextNode(sCity);     
        SOAPElement  billingStateElem= pcSaleElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_BILLING_STATE , sRequestPrefix);
        billingStateElem.addTextNode(sState);        
        SOAPElement  billingZipElem = pcSaleElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_BILLING_ZIP, sRequestPrefix);
        billingZipElem.addTextNode(sZipCode);
        SOAPElement  billingCountryElem = pcSaleElem.addChildElement(NWCGConstants.NWCG_PAY_GOV_BILLING_COUNTRY, sRequestPrefix);
        billingCountryElem.addTextNode(sCountry);

        
        SOAPElement customFieldsElem = pcSaleElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELDS, sRequestPrefix);
        
        /* 
        New Custom field layout:
        
        Custom Field 1	ALT_TRACKING_ID 
        Custom Field 2	CUST_TYPE
        Custom Field 3	CUST_NAME_ORG
        Custom Field 4	CUST_NAME_FIRST (Replaces MISC_ITEM)
        Custom Field 5	CUST_NAME_LAST (Replaces ORDER_SEQ_1)
        Custom Field 6	REMARK_1
        Custom Field 7	CBS_CODE_1
        Custom Field 8	COST_1
        Custom Field 9	MISC_ITEM (Replaces ORDER_SEQ_2)
        Custom Field 10	REMARK_2
        Custom Field 11	CBS_CODE_2
        Custom Field 12	COST_2
		*/
        
        //1. ALT_TRACKING_ID - same as order no
        SOAPElement customField1Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_1, sRequestPrefix);
        customField1Elem.addTextNode(sAgencyTrackingID);
        //2. CUST_TYPE - Action e.g. FED or NONFED
        SOAPElement customField2Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_2, sRequestPrefix);
        customField2Elem.addTextNode(sCustType); 
        //3. CUST_NAME_ORG - Business Name
        SOAPElement customField3Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_3, sRequestPrefix);
        customField3Elem.addTextNode(sBusinessName);
        //4. CUST_NAME_FIRST (Replaces MISC_ITEM)
        SOAPElement customField4Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_4, sRequestPrefix);
        customField4Elem.addTextNode(iFirstName);
        //5. CUST_NAME_LAST (Replaces ORDER_SEQ_1)
        SOAPElement customField5Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_5, sRequestPrefix);
        customField5Elem.addTextNode(iLastName);
        //6. REMARK - ISSUE: [Order No] 
        SOAPElement customField6Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_6, sRequestPrefix);
        customField6Elem.addTextNode("ISSUE:" + sAgencyTrackingID);   
        //7. CBS_CODE_1 - FED_ISSUES or NON_FED_ISSUES
        SOAPElement customField7Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_7, sRequestPrefix);
        customField7Elem.addTextNode(sIssueCBSCode);  
        //8. Issue COST - Total issue cost 
        SOAPElement customField8Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_8, sRequestPrefix);
        customField8Elem.addTextNode(sIssueAmount); 
        
        //If Shipping cost > 0 include the following elements...
        double dShipCost = 0;
        
        if(!sShipCost.equals("") && sShipCost != null)
        {
        	dShipCost = Double.parseDouble(sShipCost);
        }

        if(dShipCost > 0)
        {
        	logger.verbose("Shipping cost above 0, send details to pay.gov...");
	        //MISC_ITEM (Replaces ORDER_SEQ_2)
	        SOAPElement customField9Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_9, sRequestPrefix);
	        customField9Elem.addTextNode("");
	        //REMARK_2 - Service
	        SOAPElement customField10Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_10, sRequestPrefix);
	        customField10Elem.addTextNode(sService); 
	        //CBS_CODE_2 - FED_SHIP or NON_FED_SHIP
	        SOAPElement customField11Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_11, sRequestPrefix);
	        customField11Elem.addTextNode(sShipCBSCode); 
	        //Shipping Cost
	        SOAPElement customField12Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_12, sRequestPrefix);
	        customField12Elem.addTextNode(sShipCost);  
        }
        
        /*
        Old Custom field structure....
        
        //ALT_TRACKING_ID - same as order no
        SOAPElement customField1Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_1, sRequestPrefix);
        customField1Elem.addTextNode(sAgencyTrackingID);
        //CUST_TYPE - Action e.g. FED or NONFED
        SOAPElement customField2Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_2, sRequestPrefix);
        customField2Elem.addTextNode(sCustType); 
        //ORDER_SEQ_NR - Hardcoded to 1
        SOAPElement customField3Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_3, sRequestPrefix);
        customField3Elem.addTextNode("1");
        //REMARK - ISSUE: [Order No] 
        SOAPElement customField4Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_4, sRequestPrefix);
        customField4Elem.addTextNode("ISSUE:" + sAgencyTrackingID);   
        //CBS_CODE - FED_ISSUES or NON_FED_ISSUES
        SOAPElement customField5Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_5, sRequestPrefix);
        customField5Elem.addTextNode(sIssueCBSCode);  
        //Issue QUANTITY - defaulted to 1
        SOAPElement customField6Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_6, sRequestPrefix);
        customField6Elem.addTextNode("1");         
        //Issue COST - Total issue cost 
        SOAPElement customField7Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_7, sRequestPrefix);
        customField7Elem.addTextNode(sIssueAmount); 
        
        //If Shipping cost > 0 include the following elements...
        double dShipCost = 0;
        
        if(!sShipCost.equals("") && sShipCost != null)
        {
        	dShipCost = Double.parseDouble(sShipCost);
        }

        if(dShipCost > 0)
        {
        	logger.verbose("Shipping cost above 0, send details to pay.gov...");
	        //ORDER_SEQ_NR - hard code to 2
	        SOAPElement customField8Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_8, sRequestPrefix);
	        customField8Elem.addTextNode("2");
	        //REMARK - Service
	        SOAPElement customField9Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_9, sRequestPrefix);
	        customField9Elem.addTextNode(sService); 
	        //CBS_CODE - FED_SHIP or NON_FED_SHIP
	        SOAPElement customField10Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_10, sRequestPrefix);
	        customField10Elem.addTextNode(sShipCBSCode); 
	        //Shipping QUANTITY - default to 1
	        SOAPElement customField11Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_11, sRequestPrefix);
	        customField11Elem.addTextNode("1");  
	        //Shipping Cost
	        SOAPElement customField12Elem  = customFieldsElem.addChildElement( NWCGConstants.NWCG_PAY_GOV_CUSTOM_FIELD_12, sRequestPrefix);
	        customField12Elem.addTextNode(sShipCost);  
        }
        
        */
        
        //MimeHeaders headers = soapMessage.getMimeHeaders();
        //headers.addHeader("SOAPAction", serverURI  + "PCSaleRequest");

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", "urn:ProcessPCSale");
        
        soapMessage.saveChanges();

        /* Print the request message */
        logger.verbose("Request SOAP Message = ");
        soapMessage.writeTo(System.out);
                            
        return soapMessage;
    }
    
	public Document toDocument(SOAPMessage soapMsg) throws TransformerConfigurationException, TransformerException, SOAPException 
    {  
		Source src = soapMsg.getSOAPPart().getContent();  
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();  
		DOMResult result = new DOMResult(); 
		transformer.transform(src, result);
		return (Document)result.getNode();  
	}  
	
	/*
	public String getCountryCode(String sCountry)
	{
		String sCodeType = "CountryCodes";
		String codeValue = "";
		String codeShortDesc = "";
		String codeLongDesc = "";
		
		try 
		{
			Document ipXML = XMLUtil.getDocument("<CommonCode CodeType=\"" + sCodeType + "\" />");
			Document oputXML = CommonUtilities.invokeAPI(env, NWCGAAConstants.API_COMMONCODELIST, ipXML);
			NodeList nl = oputXML.getDocumentElement().getElementsByTagName("CommonCode");
			
			for (int count = 0; count < nl.getLength(); count++) 
			{
				
				Element commonCodeElem = (Element) nl.item(count);
				codeValue = commonCodeElem.getAttribute("CodeValue");
				codeShortDesc = commonCodeElem.getAttribute("CodeShortDescription");
				codeLongDesc = commonCodeElem.getAttribute("CodeLongDescription");
				
				if ( codeShortDesc.equals(sCountry) ||  codeLongDesc.equals(sCountry) ) 
				{
					logger.verbose("Match, the code value for "+ sCountry +" is " + codeValue);
					return codeValue;
				} 
			}
		}
		catch(Exception e)
		{
			throw new YFSException(e);
		}
		
		codeValue = "";
		return codeValue;
	}*/
	
	
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
}