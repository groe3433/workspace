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

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.core.YFSSystem;

import javax.net.ssl.*;
import javax.xml.soap.*;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

public class NWCGPayGovSOAPTest implements YIFCustomApi 
{
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGPayGovSOAPTest.class);
	
	private Properties myProperties = null;
	
	public void setProperties(Properties arg0) throws Exception 
	{
		this.myProperties = arg0;
	}
	
	public Document paygovSOAPCall(YFSEnvironment env,Document inXML) throws Exception 
	{	
		logger.verbose("********************************************");
		logger.verbose("            *NWCG PayGov SOAP Call* 10/10/14");
		logger.verbose("********************************************");
		
		SOAPMessage rp = null;
		Document outDoc = null;

		try {

			System.setProperty("java.protocol.handler.pkgs", "com.ibm.net.ssl.internal.www.protocol");
	        Security.addProvider(new com.ibm.jsse.IBMJSSEProvider()); 
	        
	        /*System.setProperty("javax.xml.soap.MessageFactory",
					"com.ibm.ws.webservices.engine.soap.MessageFactoryImpl");
	        
	        System.setProperty("javax.xml.soap.SOAPConnectionFactory",
					"com.ibm.ws.webservices.engine.soap.SOAPConnectionFactoryImpl");
	        
	        System.setProperty("com.ibm.ssl.performURLHostNameVerification", "true");*/
			
	        String sKSPath = "C:\\Users\\cbarr\\Desktop\\PayGov_DevelopmentTools\\paygovkey.jks";
	        //String sKSPath = "/opt/apps/projects/paygov/paygovkey.jks";
            
	        String sJKPassword = "changeit";
	        
	        System.setProperty("javax.net.ssl.keyStoreType","jks");
			System.setProperty("javax.net.ssl.keyStore",  sKSPath);
	        System.setProperty("javax.net.ssl.keyStorePassword",sJKPassword);
	        System.setProperty("javax.net.ssl.trustStoreType","jks");
	        System.setProperty("javax.net.ssl.trustStore", sKSPath);
	        System.setProperty("javax.net.ssl.trustStorePassword", sJKPassword);
	  
	        //System.setProperty("com.ibm.ssl.enableSignerExchangePrompt", "true");
	        System.setProperty("com.ibm.ssl.keyStoreType","jks");
	        System.setProperty("com.ibm.ssl.keyStore", sKSPath);
	        System.setProperty("com.ibm.ssl.keyStorePassword", sJKPassword);
	        System.setProperty("com.ibm.ssl.trustStoreType","jks");
	        System.setProperty("com.ibm.ssl.trustStore", sKSPath);
	        System.setProperty("com.ibm.ssl.trustStorePassword", sJKPassword);
	        
			URL url = new URL("https://qa.tcs.pay.gov/tcscollections/services/TCSSingleService");
			//URL url = new URL(NWCGConstants.NWCG_PAY_GOV_ENDPOINT);
			
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			
			// Send SOAP Message to SOAP Server
			logger.verbose("before soap call");
			rp = soapConnection.call(createSOAPRequest(inXML),url);
			
			if (rp.getSOAPBody().hasFault()) {
				
				logger.verbose("Response is a SOAPFault: ");
				SOAPFault soapFault = rp.getSOAPBody().getFault();
				
			}
			
			logger.verbose("After soap call");
			
			// Print the  SOAP Response
			printResponse (rp );

        }
		catch(SOAPException soapExp){
			logger.error("!!!!! Caught SoapException :: " + soapExp.getMessage());
			soapExp.printStackTrace();
		}
        catch (Exception e) {
        	logger.error("!!!!! Caught General Exception :: " + e.getMessage());
            e.printStackTrace();
        }
 
		if( rp != null)
		{
			outDoc = toDocument(rp);
		}
		
		return outDoc;
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
	
	 /**
	  * Method used to print the SOAP Response
	  */
	 private static void printResponse(SOAPMessage soapResponse)
	   throws Exception {
	  TransformerFactory transformerFactory = TransformerFactory
	    .newInstance();
	  Transformer transformer = transformerFactory.newTransformer();
	  Source sourceContent = soapResponse.getSOAPPart().getContent();
	  logger.verbose("\nResponse SOAP Message = ");
	  StreamResult result = new StreamResult(System.out);
	  transformer.transform(sourceContent, result);
	 }
	 
	 	    
	    
	    public static SOAPMessage createSOAPRequest(Document inXML) throws Exception 
	    {
			Element rootElement = inXML.getDocumentElement();
			
			String sAgencyTrackingID = rootElement.getAttribute("AgencyTrackingId");
			String sTransactionAmount = rootElement.getAttribute("TransactionAmount");
			String sAccountNumber = rootElement.getAttribute("account_number");
			String sExpDate = rootElement.getAttribute("credit_card_expiration_date");
			String sCustType = rootElement.getAttribute("Action");
			String sIssueCBSCode = "";
			String sShipCBSCode = "";
			
			if(sCustType.equals("FED"))
			{
				sIssueCBSCode = "FED_ISSUES";
				sShipCBSCode = "FED_SHIP";
			}
			
			if(sCustType.equals("NONFED"))
			{
				sIssueCBSCode = "NON_FED_ISSUES";
				sShipCBSCode = "NON_FED_SHIP";
			}

			String sAddressLine1 = rootElement.getAttribute("AddressLine1");
			String sAddressLine2 = rootElement.getAttribute("AddressLine2");
			String sAddressLine3 = rootElement.getAttribute("AddressLine3");
			String sCity = rootElement.getAttribute("City");
			String sState = rootElement.getAttribute("State");
			String sCountry = rootElement.getAttribute("Country");
			String sZipCode = rootElement.getAttribute("ZipCode");
			String sBusinessName = rootElement.getAttribute("CustomerName");
			String sFirstName = rootElement.getAttribute("FirstName");
			String sLastName = rootElement.getAttribute("LastName");
			String sService = rootElement.getAttribute("SCAC");
			String sIssueQty = rootElement.getAttribute("IssueQuantity");
			String sIssueAmount = rootElement.getAttribute("IssueTotalCost");
			String sShipQty = rootElement.getAttribute("ShippingQuantity");
			String sShipCost = rootElement.getAttribute("ShippingTotalCost");
			
	        MessageFactory messageFactory = MessageFactory.newInstance();
	        SOAPMessage soapMessage = messageFactory.createMessage();
	        SOAPPart soapPart = soapMessage.getSOAPPart();

			soapMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION , "true");
			soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING , "UTF-8");

	        String serverURI = "http://fms.treas.gov/tcs/schemas";

	        // SOAP Envelope
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration("sch", serverURI);

			String sAgencyID = "";
			String sTCSAppID="";

			logger.verbose("Properties: ");
			//sAgencyID = YFSSystem.getProperty("nwcg.paygov.agencyid"); 
			//sTCSAppID = YFSSystem.getProperty("nwcg.paygov.tcsappid");

			if( sAgencyID.equals("") ) 
			{
				logger.verbose("Property yfs.nwcg.paygov.agencyid is null, setting default value");
				sAgencyID = "1240";
			}
			
			if( sTCSAppID.equals("") )
			{
				logger.verbose("Property yfs.nwcg.paygov.tcsappid is null, setting default value");
				sTCSAppID = "TCSICBSCERT";
			}

	        // SOAP Body
	        SOAPBody soapBody = envelope.getBody();
	        SOAPElement pcSaleRequestElem = soapBody.addChildElement("PCSaleRequest", "sch");
	        SOAPElement agencyIDElem = pcSaleRequestElem.addChildElement("agency_id", "sch");
	        agencyIDElem.addTextNode(sAgencyID);
	        SOAPElement tcsAppIDElem = pcSaleRequestElem.addChildElement("tcs_app_id", "sch");
	        tcsAppIDElem.addTextNode(sTCSAppID);
	        
	        SOAPElement pcSaleElem = pcSaleRequestElem.addChildElement("PCSale", "sch");

	        
	        SOAPElement agencyTrackingIDElem = pcSaleElem.addChildElement("agency_tracking_id", "sch");
	        agencyTrackingIDElem.addTextNode(sAgencyTrackingID);
	        SOAPElement transactionAmountElem = pcSaleElem.addChildElement("transaction_amount", "sch");
	        transactionAmountElem.addTextNode(sTransactionAmount);
	        SOAPElement accountNumberElem = pcSaleElem.addChildElement("account_number", "sch");
	        accountNumberElem.addTextNode(sAccountNumber);
	        SOAPElement  expirationDateElem = pcSaleElem.addChildElement("credit_card_expiration_date", "sch");
	        expirationDateElem.addTextNode(sExpDate);
	        SOAPElement  businessNameElem = pcSaleElem.addChildElement("business_name", "sch");
	        businessNameElem.addTextNode(sBusinessName);
	        /*SOAPElement  firstNameElem = pcSaleElem.addChildElement("card_security_code", "sch");
	        firstNameElem.addTextNode("123");*/
	   
	       

	        MimeHeaders headers = soapMessage.getMimeHeaders();
	        headers.addHeader("SOAPAction", "urn:ProcessPCSale");
	        
	        soapMessage.saveChanges();

	        /* Print the request message */
	        logger.verbose("Request SOAP Message = ");
	        soapMessage.writeTo(System.out);
	                            
	        return soapMessage;
	    }

	
	
}