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
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.core.YFSSystem;

public class NWCGPayGovChargeCreditCard implements YIFCustomApi 
{
	private Properties myProperties = null;
	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGPayGovChargeCreditCard.class);
	
	public void setProperties(Properties arg0) throws Exception 
	{
		this.myProperties = arg0;
	}
	
	public Document chargeCard(YFSEnvironment env,Document inXML) throws Exception 
	{	
		logger.verbose("********************************************");
		logger.verbose("               NWCG Pay.Gov                  ");
		logger.verbose("********************************************");
		logger.verbose("INPUT XML:: "+ XMLUtil.getXMLString(inXML));
		
		/*Example of inXML...
		 
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
		
		if(inXML != null)
		{
			Element rootElement = inXML.getDocumentElement();
			String sOrderHeaderKey = rootElement.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
			String sOrderNo = rootElement.getAttribute(NWCGConstants.AGENCY_TRACKING_ID);//Grab order_no as we alter this for CBS later in the code.
	
			String sStatusOfOrder = getOrderStatus(env, sOrderHeaderKey);
			
			if( sStatusOfOrder.equals( NWCGConstants.NWCG_SHIPPED_CHARGED ) )
			{
				throw new YFSException("Order already processed, refresh page!");
			}
			
			//SOAP Call...
			logger.verbose("INPUT XML:: "+ XMLUtil.getXMLString(inXML));
			Document paygovResponse = CommonUtilities.invokeService(env, NWCGConstants.NWCG_PAY_GOV_WEBSERVICE_CALL_SERVICE, inXML);
			logger.verbose("OUTPUT XML:: "+ XMLUtil.getXMLString(paygovResponse));
			//create another YFSEnvironment variable so not to affect the paygov process
			/*
			Document document = XMLUtil.createDocument(NWCGConstants.NWCG_YFS_ENVIRONMENT);
			Element elem = document.getDocumentElement();
			elem.setAttribute("userId", "ad.vrao");
			elem.setAttribute("progId", "ad.vrao");
			YIFApi api = YIFClientFactory.getInstance().getLocalApi();
			YFSEnvironment envForAPICalls = api.createEnvironment(document);
			
			if(api != null)
			{
				logger.verbose("YIFApi not equal to null");
			}
			*/
			
			//Check addresses...
			compareCreditCardAddressWithBillToAddress(env, inXML);

			String sAction = rootElement.getAttribute(NWCGConstants.ACTION);
			logger.verbose("***Action: " + sAction);
			
			String sAdditionalFirstName = rootElement.getAttribute("iFirstName");
			String sAdditionalLastName = rootElement.getAttribute("iLastName");
			logger.verbose("***Additional First Name: " + sAdditionalFirstName);
			logger.verbose("***Additional Last Name: " + sAdditionalLastName);
			
			String prefix = NWCGConstants.NWCG_PAY_GOV_WEBSERVICE_PREFIX;
			
			Node pcSaleList = paygovResponse.getElementsByTagName( prefix + NWCGConstants.NWCG_PAY_GOV_PCSALE ).item(0);
			
	        Element actionNode = paygovResponse.createElement( prefix + NWCGConstants.NWCG_PAY_GOV_ACTION );
	        Text actionText = paygovResponse.createTextNode(sAction);
	        
	        Element orderNoNode = paygovResponse.createElement( prefix + NWCGConstants.ORDER_NO );
	        Text orderNoText = paygovResponse.createTextNode(sOrderNo);
	        
	        Element additionalFirstNameNode = paygovResponse.createElement( prefix + "iFirstName" );
	        Text additionalFirstNameText = paygovResponse.createTextNode(sAdditionalFirstName);
	        
	        Element additionalLastNameNode = paygovResponse.createElement( prefix + "iLastName" );
	        Text additionalLastNameText = paygovResponse.createTextNode(sAdditionalLastName);	        
	        
	        additionalFirstNameNode.appendChild(additionalFirstNameText);
	        additionalLastNameNode.appendChild(additionalLastNameText);
	        
	        actionNode.appendChild(actionText);
	        orderNoNode.appendChild(orderNoText);
	        
	        pcSaleList.appendChild(actionNode);
	        pcSaleList.appendChild(orderNoNode);
	        pcSaleList.appendChild(additionalFirstNameNode);        
	        pcSaleList.appendChild(additionalLastNameNode);
	        
	        try
	        {
	        	//recordExternalCharges.
				Document recordExternalChargesResult = CommonUtilities.invokeService( env, NWCGConstants.NWCG_UPDATE_PAYGOV_RESPONSE_EXTERNAL_CHARGES_SERVICE, paygovResponse);
				logger.verbose("Result of recordExternalCharges API: "+ XMLUtil.getXMLString(recordExternalChargesResult));
				
				//ChangeOrderStatus API...
				Document changeOrderStatusResult = CommonUtilities.invokeService(env, NWCGConstants.NWCG_CHANGE_ORDER_STATUS_TO_SHIPPEDANDCHARGED_SERVICE, inXML);
				logger.verbose("Result of changeOrderStatus API: "+ XMLUtil.getXMLString(changeOrderStatusResult));
				
				//Retreive Authorization Code
				Element paygpvRootElement = paygovResponse.getDocumentElement();
				String sAuthResponseCode = getString(prefix + NWCGConstants.NWCG_PAY_GOV_AUTH_RESPONSE_CODE, paygpvRootElement);
				Element inXMLRootElement = inXML.getDocumentElement();
				inXMLRootElement.setAttribute( NWCGConstants.NWCG_AUTHORIZATION_CODE , sAuthResponseCode);
				
				//ChangeOrder API...
				Document changeOrderResult = CommonUtilities.invokeService(env, NWCGConstants.NWCG_UPDATE_AUTH_CODE_SERVICE, inXML);
				logger.verbose("Result of changeOrder API: "+ XMLUtil.getXMLString(changeOrderResult));
				
				//append an attribute to the XML here to check when details screen appears
				String sTransactionStatus = getString(prefix+ NWCGConstants.NWCG_PAY_GOV_TRANSACTION_STATUS, paygpvRootElement);
				inXMLRootElement.setAttribute( NWCGConstants.NWCG_TRANSACTION_STATUS, sTransactionStatus);
				
	        }
	        catch(Exception e){
	        	logger.error("!!!!! Exception: "+ e);
	        }
			logger.verbose("Sent back: "+ XMLUtil.getXMLString(inXML));
		}	
		return inXML;
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
	
	private void compareCreditCardAddressWithBillToAddress(YFSEnvironment env,Document inXML)
	{
		logger.verbose("****compareCreditCardAddressWithBillToAddress()****");

		String sBillAddress1 = "";
		String sBillAddress2 = "";
		String sBillAddress3 = "";
		String sBillCountry = "";
		String sBillZipCode = "";
		String sBillState = "";
		String sBillCity = "";
		String sBillFirstName = "";
		String sBillLastName = "";
		String sBillCustName = "";
		
		Element rootElement = inXML.getDocumentElement();
		
		String sCreditAddress1 = rootElement.getAttribute(NWCGConstants.ADDRESS_LINE_1);
		String sCreditAddress2 = rootElement.getAttribute(NWCGConstants.ADDRESS_LINE_2);
		String sCreditAddress3 = rootElement.getAttribute(NWCGConstants.ADDRESS_LINE_3);
		String sCreditCountry = "US";//Default
		String sCountryCode = rootElement.getAttribute( NWCGConstants.COUNTRY );
		
		logger.verbose("Country Code: " + sCountryCode);
		
		try 
		{
			Document ipCommonCodeXML = XMLUtil.getDocument("<CommonCode CodeType='COUNTRY_CODE' CodeValue=\"" + sCountryCode + "\" />");
			Document oputCommonCodeXML = CommonUtilities.invokeAPI(env, NWCGAAConstants.API_COMMONCODELIST, ipCommonCodeXML);
			NodeList nlCommonCode = oputCommonCodeXML.getDocumentElement().getElementsByTagName( NWCGConstants.COMMON_CODE );
			
			if(nlCommonCode != null)
			{
				Element commonCodeElem = (Element) nlCommonCode.item(0);
				sCreditCountry = commonCodeElem.getAttribute( NWCGConstants.NWCG_CODE_SHORT_DESCRIPTION );
				
			}

		}
		catch(Exception e)
		{
			logger.error("!!!!! Exception" + e);
		}
		
		if( sCreditCountry.equals("USA") )
		{
			sCreditCountry = "US";
		}
		
		logger.verbose("Country: " + sCreditCountry);
		
		String sCreditBillZipCode = rootElement.getAttribute( NWCGConstants.ZIP_CODE );
		String sCreditState = rootElement.getAttribute( NWCGConstants.STATE );
		
		String sCreditCity = rootElement.getAttribute( NWCGConstants.CITY );
		String sCreditFirstName = rootElement.getAttribute( NWCGConstants.FIRST_NAME );
		String sCreditLastName = rootElement.getAttribute( NWCGConstants.LAST_NAME );
		String sCreditCustName = rootElement.getAttribute(NWCGConstants.CUSTOMER_NAME);
		
		String sBillToID = rootElement.getAttribute( NWCGConstants.BILLTO_ID_ATTR );
		
		StringBuffer sb = new StringBuffer("<Customer OrganizationCode='NWCG' CustomerID=\"");
		sb.append(sBillToID);
		sb.append("\"/>");
		
		Document getCustomerDetailsOutputDoc = null;
		
		StringBuffer customerTemplate = new StringBuffer("<Customer CustomerID=''><Extn ExtnCustomerName=''/><Consumer BillingAddressKey=''><BillingPersonInfo ZipCode='' State='' LastName='' FirstName='' Country='' City='' AddressLine3='' AddressLine2='' AddressLine1='' /></Consumer></Customer>");
		
		try 
		{
			String inStr = sb.toString();
			String inTemplate = customerTemplate.toString();
			
			Document inptDoc = XMLUtil.getDocument(inStr);
			Document opTemplate = XMLUtil.getDocument(inTemplate);
			
			logger.verbose("***getCustomerDetails***");
			logger.verbose("Input:: "+ XMLUtil.getXMLString(inptDoc));
			logger.verbose("Template:: "+ XMLUtil.getXMLString(opTemplate));
			
			getCustomerDetailsOutputDoc = CommonUtilities.invokeAPI(env, opTemplate, NWCGConstants.API_GET_CUST_DETAILS, inptDoc);

			logger.verbose("Output:: "+ XMLUtil.getXMLString(getCustomerDetailsOutputDoc));
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();	
			throw new YFSException("Exception thrown while calling getCustomerDetails API" + e);
		}
		
		if (getCustomerDetailsOutputDoc != null) 
		{
			Element opDocElm = getCustomerDetailsOutputDoc.getDocumentElement();
			
			NodeList nlBillingPersonInfo = opDocElm.getElementsByTagName( NWCGConstants.NWCG_BILLING_PERSON_INFO );
			
			if (nlBillingPersonInfo != null && nlBillingPersonInfo.getLength() == 1) 
			{
				Node billPersonInfoNode = nlBillingPersonInfo.item(0);
				
				if (billPersonInfoNode instanceof Element) 
				{
					
					Element billPersonInfoElm = (Element) billPersonInfoNode;
					sBillFirstName = billPersonInfoElm.getAttribute( NWCGConstants.FIRST_NAME );
					sBillLastName = billPersonInfoElm.getAttribute( NWCGConstants.LAST_NAME );
					sBillAddress1 = billPersonInfoElm.getAttribute( NWCGConstants.ADDRESS_LINE_1 );
					sBillAddress2 = billPersonInfoElm.getAttribute( NWCGConstants.ADDRESS_LINE_2 );
					sBillAddress3 = billPersonInfoElm.getAttribute( NWCGConstants.ADDRESS_LINE_3 );
					sBillCity = billPersonInfoElm.getAttribute( NWCGConstants.CITY );
					sBillState = billPersonInfoElm.getAttribute( NWCGConstants.STATE );
					sBillZipCode = billPersonInfoElm.getAttribute( NWCGConstants.ZIP_CODE );
					sBillCountry = billPersonInfoElm.getAttribute( NWCGConstants.COUNTRY );
					
				}
			}
			
			NodeList nlExtn = opDocElm.getElementsByTagName( NWCGConstants.CUST_EXTN_ELEMENT );
			
			if (nlExtn != null && nlExtn.getLength() == 1) 
			{
				Node extnNode = nlExtn.item(0);
					
				if (extnNode instanceof Element) 
				{
					
				Element extnElm = (Element) extnNode;
				sBillCustName = extnElm.getAttribute( NWCGConstants.CUST_CUSTOMER_NAME_ATTR );
						
				}
			}
			
		}
			
		//Compare addresses...
		if(		!sBillFirstName.equals(sCreditFirstName)|| !sBillLastName.equals(sCreditLastName) 	||
				!sBillAddress1.equals(sCreditAddress1) 	|| !sBillAddress2.equals(sCreditAddress2) 	||
				!sBillAddress3.equals(sCreditAddress3) 	|| !sBillCity.equals(sCreditCity)		   	||
				!sBillState.equals(sCreditState)		|| !sBillZipCode.equals(sCreditBillZipCode)	||
				!sBillCountry.equals(sCreditCountry)	|| !sBillCustName.equals(sCreditCustName)	)
		{

			logger.verbose("Add new address details...");
			
			String sOrderNo = rootElement.getAttribute( NWCGConstants.AGENCY_TRACKING_ID );
			String sOrderHeaderKey = rootElement.getAttribute( NWCGConstants.ORDER_HEADER_KEY );
					
				/*
				 * Call ChangeOrder with new address...
				 * 
				<Order OrderNo="" OrderHeaderKey="" Action="MODIFY">
				 
				 <OrderLines>
				 <OrderLine>
				 
				 <AdditionalAddresses>
				 <AdditionalAddress AddressType="CREDIT_CARD">
				 <PersonInfo ZipCode="" State="" LastName="" FirstName="" Country="" Company="" City="" AddressLine3="" AddressLine2="" AddressLine1=""/>
				 </AdditionalAddress>
				 </AdditionalAddresses>
				 
				  </OrderLine>
				  </OrderLines>
				</Order>*/
			try 
			{
					
				Document changeOrderInputDoc = XMLUtil.newDocument();//unreported exception
				Element el_Order = changeOrderInputDoc.createElement(NWCGConstants.ORDER_ELM);
		
				changeOrderInputDoc.appendChild(el_Order);
				el_Order.setAttribute( NWCGConstants.ACTION , NWCGConstants.MODIFY );
				el_Order.setAttribute( NWCGConstants.ORDER_HEADER_KEY , sOrderHeaderKey);
				el_Order.setAttribute( NWCGConstants.ORDER_NO , sOrderNo);
					
				Element el_additionalAddresses = changeOrderInputDoc.createElement( NWCGConstants.ADDITIONAL_ADDRESSES );
				el_Order.appendChild(el_additionalAddresses);
		
				Element el_additionalAddress = changeOrderInputDoc.createElement( NWCGConstants.ADDITIONAL_ADDRESS);
				el_additionalAddresses.appendChild(el_additionalAddress);
					
				el_additionalAddress.setAttribute( NWCGConstants.ADDRESS_TYPE , NWCGConstants.NWCG_PAYMENT_CREDIT_CARD_TYPE);
		
				Element el_PersonInfo= changeOrderInputDoc.createElement(NWCGConstants.PERSON_INFO);
				el_additionalAddress.appendChild(el_PersonInfo);
		
				el_PersonInfo.setAttribute( NWCGConstants.FIRST_NAME, sCreditFirstName);
				el_PersonInfo.setAttribute( NWCGConstants.LAST_NAME, sCreditLastName);
				el_PersonInfo.setAttribute( NWCGConstants.ADDRESS_LINE_1, sCreditAddress1);	
				el_PersonInfo.setAttribute( NWCGConstants.ADDRESS_LINE_2, sCreditAddress2);
				el_PersonInfo.setAttribute( NWCGConstants.ADDRESS_LINE_3, sCreditAddress3);
				el_PersonInfo.setAttribute( NWCGConstants.CITY, sCreditCity);
				el_PersonInfo.setAttribute( NWCGConstants.STATE, sCreditState);
				el_PersonInfo.setAttribute( NWCGConstants.ZIP_CODE, sCreditBillZipCode);
				el_PersonInfo.setAttribute( NWCGConstants.COUNTRY, sCreditCountry);
				el_PersonInfo.setAttribute( NWCGConstants.COMPANY, sCreditCustName);
					
				logger.verbose("changeOrder API Input XML:: "+ XMLUtil.getXMLString(changeOrderInputDoc));
					 
				//Call changeOrder API
				Document changeOrderOutputDocument = CommonUtilities.invokeAPI(env, NWCGConstants.API_CHANGE_ORDER, changeOrderInputDoc);
		            
				logger.verbose("changeOrder API Output XML:: "+ XMLUtil.getXMLString(changeOrderOutputDocument));
				 
			}
			catch (Exception e)  
			{
				e.printStackTrace();	
				throw new YFSException("Exception thrown while updating additional Addresses with changeOrder API" + e);
			}	
		}
	
	}
	
	private String getOrderStatus(YFSEnvironment env, String OrderHeaderKey) throws Exception{
		Document outDoc = null;
		String sStatus = "";
		
		try {
			Document docOrderDtlsIP = XMLUtil.getDocument("<Order OrderHeaderKey=\"" + OrderHeaderKey + "\"/>");

			//Create template
			Document temp = XMLUtil.newDocument();
			Element el_order=temp.createElement(NWCGConstants.ORDER);
			el_order.setAttribute( NWCGConstants.STATUS_ATTR ,"");
			temp.appendChild(el_order);
			
			outDoc = CommonUtilities.invokeAPI(env, temp ,NWCGConstants.API_GET_ORDER_DETAILS, docOrderDtlsIP);
			
			Element rootElement = outDoc.getDocumentElement();
			sStatus = rootElement.getAttribute( NWCGConstants.STATUS_ATTR );			
		}
		catch(ParserConfigurationException pce){
			logger.error("!!!!! NWCGPayGovChargeCreditCard::getOrderStatus, " +
					"ParserConfigurationException : " + pce.getMessage());
			pce.printStackTrace();
			throw pce;
		} catch (SAXException e) {
			logger.error("!!!!! NWCGPayGovChargeCreditCard::getOrderStatus, " +
					"SAXException : " + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			logger.error("!!!!! NWCGPayGovChargeCreditCard::getOrderStatus, " +
					"IOException : " + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			logger.error("!!!!! NWCGPayGovChargeCreditCard::getOrderStatus, " +
					"Exception : " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
		return sStatus;
	}
	
}