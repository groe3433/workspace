package com.fanatics.sterling.ue;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.FANConstants;
import com.fanatics.sterling.constants.EmailComunicationConstants;
import com.fanatics.sterling.constants.FanaticsFraudCheckConstants;
import com.fanatics.sterling.constants.OrderNotesConstants;
import com.fanatics.sterling.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSProcessOrderHoldTypeUE;

public class FANYFSProcessOrderHoldTypeUEImpl implements YFSProcessOrderHoldTypeUE{

	private static YFCLogCategory logger= YFCLogCategory.instance(YFCLogCategory.class);
	@Override
	public Document processOrderHoldType(YFSEnvironment yfsEnv, Document inXML)
			throws YFSUserExitException {
		
		logger.verbose("Inside processOrderHoldType");
		logger.verbose("Input xml is: "+ XMLUtil.getXMLString(inXML));
		Document docFraudResponse = null;
		
		// fetch the root element of the document
		Element eleRootInXML = inXML.getDocumentElement();
		logger.verbose("processOrderHoldType point 1");
		// fetch the status
		String strHoldStatus = FANConstants.NO_VALUE;
		String strHoldType = FANConstants.NO_VALUE; 
		
		// fetch the HoldType
		NodeList nlOrderHoldType = eleRootInXML.getElementsByTagName(FANConstants.ELEM_fanatics_OrderHoldType);
		logger.verbose("processOrderHoldType point 2");
		int nlOrderHoldTypeLength = nlOrderHoldType.getLength();
		Element eleOrderHoldType = null;
		
		for(int i=0;i<nlOrderHoldTypeLength;i++)
		{
			logger.verbose("processOrderHoldType inside loop "+i);
			eleOrderHoldType = (Element) nlOrderHoldType.item(i);
			strHoldStatus = eleOrderHoldType.getAttribute(FANConstants.ATT_STATUS);
			strHoldType = eleOrderHoldType.getAttribute(FANConstants.ATT_fanatics_HoldType);
			
			logger.verbose("strHoldType "+strHoldType + strHoldType.equals(FanaticsFraudCheckConstants.HOLDTYPE_fanatics_PENDINGEVALUATION));
			logger.verbose("strHoldStatus "+strHoldStatus + strHoldStatus.equals(FANConstants.STR_1100));
			
			
			if (strHoldType.equals(FanaticsFraudCheckConstants.HOLDTYPE_fanatics_PENDINGEVALUATION) && strHoldStatus.equals(FANConstants.STR_1100)) {
				
				// fetch the input xml for the Fraud Engine
				Document docInputFraudEngine = getDocInputFraudEngine(yfsEnv, inXML) ;
			      //Invoke FanaticsFraudCheck service;
				
				try {
					docFraudResponse = CommonUtil.invokeService(yfsEnv, FanaticsFraudCheckConstants.SERVICE_fanatics_FanaticsFraudCheck, docInputFraudEngine);
				} catch (Exception e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}
				break;
			}
		
			
		}
		return docFraudResponse;
	}
	
	private Document getDocInputFraudEngine(YFSEnvironment yfsEnv,
			Document inXML) {

		Document docIPGetOrderDetails = null;
		Document docTemplateGetOrderDetails = null;
		Element eleInputGetOrderDetRoot = inXML.getDocumentElement();

		String strInputGetOrderDet = "<Order OrderNo='"+eleInputGetOrderDetRoot.getAttribute(FANConstants.ORDER_NO)+"' " +
				"EnterpriseCode='"+eleInputGetOrderDetRoot.getAttribute(FANConstants.ATT_EnterpriseCode)+"' " +
				"DocumentType='"+eleInputGetOrderDetRoot.getAttribute(FANConstants.ATT_DocumentType)+"'></Order>";

		String templateGetOrderDetails = "<Order OrderNo='' EnterpriseCode='' DocumentType='' CarrierServiceCode=''  SellerOrganizationCode='' PriorityCode='' PaymentStatus='' OtherCharges='' OriginalTotalAmount='' OriginalTax='' OrderType='' OrderDate='' MinOrderStatus='' MaxOrderStatus='' EntryType='' DraftOrderFlag='' CustomerEMailID=''>"+
											 " <PriceInfo TotalAmount='' ReportingConversionRate='' ReportingConversionDate='' EnterpriseCurrency='' Currency=''/> "+
											 "<OrderLines>"+
											  "<OrderLine DeliveryMethod='' CarrierServiceCode='' ItemGroupCode='' OtherCharges=''  SubLineNo='' StatusQuantity='' PrimeLineNo='' OriginalOrderedQty='' OrderedQty=''  MinLineStatus='' MaxLineStatus=''  >"+
											   "<Item UnitOfMeasure='' ItemID='' UnitCost='' ItemShortDesc='' ItemDesc=''  CostCurrency=''/>"+
											   "<LinePriceInfo UnitPrice='' TaxableFlag='' OrderedPricingQty='' ListPrice='' LineTotal='' DiscountPercentage='' ActualPricingQty=''/>"+
											   "<Schedules>"+
											    "<Schedule Quantity='' ScheduleNo='' ExpectedShipmentDate='' ExpectedDeliveryDate=''/>"+
											   "</Schedules>"+
											   "<LineOverallTotals UnitPrice='' LineTotal='' Tax='' PricingQty='' OptionPrice='' ExtendedPrice='' Discount='' Charges=''/>"+
											   "<LineInvoicedTotals UnitPrice='' LineTotal='' Tax='' PricingQty='' OptionPrice='' ExtendedPrice='' Discount='' Charges='' AdditionalLinePriceTotal=''/>"+
											   "<LineRemainingTotals UnitPrice='' LineTotal='' Tax='' PricingQty='' OptionPrice='' ExtendedPrice='' Discount='' Charges='' AdditionalLinePriceTotal=''/>"+
											   "<LineCharges/>"+
											   "<LineTaxes/>"+
											   "<OrderStatuses>"+
											    "<OrderStatus ShipNode='' Status='' TotalQuantity='' StatusQty='' StatusDate='' PipelineKey=''>"+
											    "</OrderStatus>"+
											   "</OrderStatuses>"+
											   "<CapacityAllocations/>"+
											  "</OrderLine>"+
											 "</OrderLines>"+
											 "<Instructions NumberOfInstructions=''/>"+
											 "<PersonInfoShipTo ZipCode='' Title='' Suffix='' State='' PersonID='' OtherPhone='' MobilePhone='' MiddleName='' LastName='' JobTitle='' FirstName='' EveningPhone='' EveningFaxNo='' EMailID='' Department='' DayPhone='' DayFaxNo='' Country='' Company='' City='' Beeper='' AlternateEmailID='' AddressLine6='' AddressLine5='' AddressLine4='' AddressLine='' AddressLine2='' AddressLine1='' />"+
											 "<PersonInfoBillTo ZipCode='' Title='' Suffix='' State='' PersonID='' OtherPhone='' MobilePhone='' MiddleName='' LastName='' JobTitle='' FirstName='' EveningPhone='' EveningFaxNo='' EMailID='' Department='' DayPhone='' DayFaxNo='' Country='' Company='' City='' Beeper='' AlternateEmailID='' AddressLine6='' AddressLine5='' AddressLine4='' AddressLine='' AddressLine2='' AddressLine1='' />"+
											 "<AdditionalAddresses NumberOfAdditionalAddresses=''/>"+
											 "<References/>"+
											 "<PaymentMethods>"+
											  "<PaymentMethod  PaymentType='' PaymentReference='' PaymentReference2='' PaymentReference1='' GetFundsAvailableUserExitInvoked='' FundsAvailable='' DisplaySvcNo='' DisplayPaymentReference1='' DisplayCreditCardNo='' CreditCardType='' CreditCardNo='' CreditCardName='' CreditCardExpDate='' />"+
											 "</PaymentMethods>"+
											 "<ChargeTransactionDetails >"+
											  "<ChargeTransactionDetail Status='' AuthorizationExpirationDate='' SettledAmount='' TransactionDate='' RequestAmount='' OpenAuthorizedAmount=''  CollectionDate='' ChargeType='' ChargeTransactionKey='' BookAmount='' AuthorizationID='' AuditTransactionID=''>"+
											   "<InvoiceCollectionDetails/>"+
											   "<CreditCardTransactions/>"+
											   "<TransferToOrder/>"+
											   "<TransferFromOrder/>"+
											  "</ChargeTransactionDetail>"+
											 "</ChargeTransactionDetails>"+
											 "<ProductServiceAssocs/>"+
											 "<OverallTotals LineSubTotal='' HdrTotal='' HdrTax='' HdrDiscount='' HdrCharges='' GrandTotal='' GrandTax='' GrandDiscount='' GrandCharges=''/>"+
											 "<InvoicedTotals LineSubTotal='' HdrTotal='' HdrTax='' HdrDiscount='' HdrCharges='' GrandTotal='' GrandTax='' GrandDiscount='' GrandCharges=''/>"+
											 "<RemainingTotals LineSubTotal='' HdrTotal='' HdrTax='' HdrDiscount='' HdrCharges='' GrandTotal='' GrandTax='' GrandDiscount='' GrandCharges=''/>"+
											 "<HeaderCharges/>"+
											 "<HeaderTaxes/>"+
											 "<OrderStatuses>"+
											  "<OrderStatus ShipNode='' Status='' TotalQuantity='' StatusQty='' StatusDate='' PipelineKey=''>"+
											  "</OrderStatus>"+
											 "</OrderStatuses>"+
											 "<ExchangeOrders/>"+
											"</Order>" ;
		
		try {
			docIPGetOrderDetails = XMLUtil.getDocument(strInputGetOrderDet);
			docTemplateGetOrderDetails = XMLUtil.getDocument(templateGetOrderDetails);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Document docOPGetOrderDetails = null;

		try {
			docOPGetOrderDetails = CommonUtil.invokeAPI(yfsEnv, docTemplateGetOrderDetails, FANConstants.API_GET_ORDER_DET, docIPGetOrderDetails);
			logger.verbose("getDocInputFraudEngine docOPGetOrderDetails is: "+ XMLUtil.getXMLString(docOPGetOrderDetails));
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		logger.verbose("getDocInputFraudEngine docIPChangeOrder is: "+ XMLUtil.getXMLString(docOPGetOrderDetails));

		return docOPGetOrderDetails;
	}
	
	
}
