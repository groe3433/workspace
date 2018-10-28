package com.fanatics.sterling.api;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class FanCreateReturn {

	private static YFCLogCategory log = YFCLogCategory.instance("com.yantra.yfc.log.YFCLogCategory");

	private Properties props = null;

	public Document createReturnOrder(YFSEnvironment env, Document inXML) throws Exception{

		log.verbose("--------------FanCreateReturn------1----"
				+XMLUtil.getElementXMLString(inXML.getDocumentElement()));
		
		Element orderEle = inXML.getDocumentElement();
		orderEle.setAttribute("DraftOrderFlag", "Y");
		boolean giftFlagBool = false;
		
		NodeList headerChargeList = orderEle.getElementsByTagName("HeaderCharge");
		
		if(headerChargeList.getLength() == 0){
			
			NodeList headerChargesList = orderEle.getElementsByTagName("HeaderCharges");
			
			if(headerChargesList.getLength() != 0){
				
				Element headerCharges = (Element)headerChargesList.item(0);
				orderEle.removeChild(headerCharges);
			}
		}
		
		
		NodeList headerTaxList = orderEle.getElementsByTagName("HeaderTax");
		
		if(headerTaxList.getLength() == 0){
			
			NodeList headerTaxesList = orderEle.getElementsByTagName("HeaderTaxes");
			
			if(headerTaxesList.getLength() != 0){
				
				Element headerTaxes = (Element)headerTaxesList.item(0);
				orderEle.removeChild(headerTaxes);
			}
		}
		
		
		Element customAttributes = (Element)inXML.getElementsByTagName("CustomAttributes").item(0);
		
		if(customAttributes != null){
			
			String gifteeMail = customAttributes.getAttribute("GifteeMailID");
			NodeList orderLineList2 = inXML.getElementsByTagName("OrderLine");
			
			if(gifteeMail != null){
				if(!gifteeMail.equalsIgnoreCase("")){
					
					giftFlagBool = true;
					for(int i=0; i<orderLineList2.getLength(); i++){
						
						Element orderLine = (Element)orderLineList2.item(i);
						orderLine.setAttribute("GiftFlag", "Y");
						inXML.getDocumentElement().setAttribute("ProcessPaymentOnReturnOrder", "Y");
						inXML.getDocumentElement().setAttribute("ReturnByGiftRecipient", "Y");
						
					}
					log.verbose("-------FanCreateReturn-----checkIfPaymentProcessingRequiredOnReturnOrder---gifteeMail----1----"+gifteeMail);
				}
			}
		}
		

		
		
		
		log.verbose("--------------FanCreateReturn------createDraftOrder--ip-"
					+XMLUtil.getXMLString(inXML));
		
		Document outDoc = CommonUtil.invokeAPI
				(env,"createOrder", inXML);
		
		log.verbose("--------------FanCreateReturn------createDraftOrder--op-"
				+XMLUtil.getXMLString(outDoc));
		
		
		
		
		//<Order DetermineOrdersForReturn="Y" DocumentType="0003"  ExecuteReturnPolicy="Y" IsInquiry="N" OrderHeaderKey="20160609135618115876" />
		
		Document orderDoc4 = XMLUtil.createDocument("Order");
		Element orderEle2 = orderDoc4.getDocumentElement();
		orderEle2.setAttribute("OrderHeaderKey", outDoc.getDocumentElement().getAttribute("OrderHeaderKey"));
		orderEle2.setAttribute("DetermineOrdersForReturn", "Y");
		orderEle2.setAttribute("DocumentType", "0003");
		orderEle2.setAttribute("ExecuteReturnPolicy", "Y");
		orderEle2.setAttribute("IsInquiry", "N");
		
		
		log.verbose("--------------FanCreateReturn------processReturnOrder--ip-"
				+XMLUtil.getXMLString(orderDoc4));
	
		Document outDoc2 = CommonUtil.invokeAPI
			(env,"processReturnOrder", orderDoc4);
	
		log.verbose("--------------FanCreateReturn------processReturnOrder--op-"
			+XMLUtil.getXMLString(outDoc2));
		
		
		// setting ProcessPaymentOnReturnOrder="Y" if giftee return
		
		if(giftFlagBool == true){
			
			Document orderDoc5 = XMLUtil.createDocument("Order");
			Element orderEle5 = orderDoc5.getDocumentElement();
			orderEle5.setAttribute("OrderHeaderKey", outDoc.getDocumentElement().getAttribute("OrderHeaderKey"));
			orderEle5.setAttribute("Action", "MODIFY");
			orderEle5.setAttribute("ProcessPaymentOnReturnOrder", "Y");
			//orderEle5.setAttribute("ReturnByGiftRecipient", "Y");
			orderEle5.setAttribute("Override", "Y");
			
			log.verbose("--------FanCreateReturn------changeOrder--ip--"
					+XMLUtil.getXMLString(orderDoc5));
		
			Document outDoc5 = CommonUtil.invokeAPI
				(env,"changeOrder", orderDoc5);
		
			log.verbose("------FanCreateReturn----changeOrder--op--"
				+XMLUtil.getXMLString(outDoc5));
			
		}
		
		
		//<ConfirmDraftOrder DocumentType="0003" OrderHeaderKey="2016060414563796045"  SelectMethod="WAIT"  />
		
		Document orderDoc3 = XMLUtil.createDocument("ConfirmDraftOrder");
		Element orderEle3 = orderDoc3.getDocumentElement();
		orderEle3.setAttribute("OrderHeaderKey", outDoc.getDocumentElement().getAttribute("OrderHeaderKey"));
		orderEle3.setAttribute("SelectMethod", "WAIT");
		orderEle3.setAttribute("DocumentType", "0003");

		log.verbose("--------------FanCreateReturn------confirmDraftOrder--ip-"
				+XMLUtil.getXMLString(orderDoc3));
	
		Document outDoc3 = CommonUtil.invokeAPI
			(env,"confirmDraftOrder", orderDoc3);
	
		log.verbose("--------------FanCreateReturn------confirmDraftOrder--op-"
			+XMLUtil.getXMLString(outDoc3));
		
	
		

		// checking if Giftee Return
		
		log.verbose("--------------FanCreateReturn------getOrderList--ip--"
					+XMLUtil.getXMLString(outDoc));
		
		Document outDoc6 = CommonUtil.invokeAPI
				(env,"global/template/api/getOrderList_payment.xml","getOrderList", outDoc);
		
		log.verbose("--------------FanCreateReturn------getOrderList--op--"
				+XMLUtil.getXMLString(outDoc6));
		
		
		checkIfPaymentProcessingRequiredOnReturnOrder(env, outDoc6, inXML);
		

		
		return outDoc3;
	}

	
	
	// method added to check if refund is to be given to sales order or return order
	
	
	private Document checkIfPaymentProcessingRequiredOnReturnOrder(
			YFSEnvironment env, Document inDoc, Document inXML) throws Exception {
		


		log.verbose("--------FanCreateReturn-------checkIfPaymentProcessingRequiredOnReturnOrder----start--"+XMLUtil.getXMLString(inDoc));
		
		Element orderEle = (Element)inDoc.getElementsByTagName("Order").item(0);
				
						boolean giftFlagBool = false;
						
						String orderHeaderKey = orderEle.getAttribute("OrderHeaderKey");
						
						Document orderDoc = XMLUtil.createDocument("Order");
						Element orderEle2 = orderDoc.getDocumentElement();
						orderEle2.setAttribute("OrderHeaderKey", orderHeaderKey);
						orderEle2.setAttribute("Action", "MODIFY");
						orderEle2.setAttribute("Override", "Y");
						
						// checking for gift flag in the input document
						NodeList orderLineList2 = inDoc.getElementsByTagName("OrderLine");
						
						for(int i=0; i<orderLineList2.getLength(); i++){
							
							Element orderLine = (Element)orderLineList2.item(i);

							
							String giftFlag = orderLine.getAttribute("GiftFlag");
							
							if(giftFlag != null){
								
								if(giftFlag.equalsIgnoreCase("Y")){
									giftFlagBool = true;
									log.verbose("-------FanCreateReturn-----checkIfPaymentProcessingRequiredOnReturnOrder---giftFlagBool----1----"+giftFlagBool);
								}
								
							}
							
						}
						
						log.verbose("-------FanCreateReturn----checkIfPaymentProcessingRequiredOnReturnOrder----giftFlagBool----2----"+giftFlagBool);
						
						if(giftFlagBool == true){
							
							Element overallTotalsEle = (Element)inDoc.getElementsByTagName("OverallTotals").item(0);
							String grandTotal = overallTotalsEle.getAttribute("GrandTotal");
							Float grandTotalFlt = Float.parseFloat(grandTotal);
							
//							orderDoc.getDocumentElement().setAttribute("ProcessPaymentOnReturnOrder", "Y");
//							orderDoc.getDocumentElement().setAttribute("ReturnByGiftRecipient", "Y");
							
//						    <PaymentMethod SvcNo="1234567812345678" DisplaySvcNo="5678" MaxChargeLimit="100" PaymentReference1="GC-000032323" 
//						    		   PaymentType="GIFT_CARD" UnlimitedCharges="N">
//					            <PaymentDetails AuthAvs="NN" AuthCode="999888" AuthorizationExpirationDate="2016-12-23T10:09:43-08:00" AuthorizationID="12345dummy" 
//					            ChargeType="AUTHORIZATION" ProcessedAmount="100" RequestAmount="100"/>
//					        </PaymentMethod>
					        
							Element paymentMethodsEle = orderDoc.createElement("PaymentMethods");
							Element paymentMethodEle = orderDoc.createElement("PaymentMethod");
							Element paymentDetailsEle = orderDoc.createElement("PaymentDetails");
							Element paymentDetailsEle2 = orderDoc.createElement("PaymentDetails");
							
							paymentMethodEle.setAttribute("SvcNo", "Dummy_SVC");
							paymentMethodEle.setAttribute("DisplaySvcNo", "1234");
							paymentMethodEle.setAttribute("MaxChargeLimit", "99999");
							paymentMethodEle.setAttribute("PaymentReference1", "GC-0000001");
							paymentMethodEle.setAttribute("PaymentType", "GIFT_CARD");
							paymentMethodEle.setAttribute("UnlimitedCharges", "N");
							
							paymentDetailsEle.setAttribute("AuthorizationExpirationDate", "2025-12-23T10:09:43-08:00");
							paymentDetailsEle.setAttribute("AuthorizationID", "Dummy_AuthID_GC");
							paymentDetailsEle.setAttribute("ChargeType", "AUTHORIZATION");
							paymentDetailsEle.setAttribute("AuthCode", "Dummy_AuthID_GC");
							//paymentDetailsEle.setAttribute("", "");
							paymentDetailsEle.setAttribute("RequestAmount", grandTotal);
							paymentDetailsEle.setAttribute("ProcessedAmount", grandTotal);
							
							paymentDetailsEle2.setAttribute("AuthorizationExpirationDate", "2025-12-23T10:09:43-08:00");
							paymentDetailsEle2.setAttribute("AuthorizationID", "Dummy_AuthID_GC");
							paymentDetailsEle2.setAttribute("ChargeType", "CHARGE");
							paymentDetailsEle2.setAttribute("AuthCode", "Dummy_AuthID_GC");
							//paymentDetailsEle2.setAttribute("", "");
							paymentDetailsEle2.setAttribute("RequestAmount", grandTotal);
							//paymentDetailsEle2.setAttribute("ProcessedAmount", grandTotal);
							
							paymentMethodEle.appendChild(paymentDetailsEle);
							paymentMethodEle.appendChild(paymentDetailsEle2);
							paymentMethodsEle.appendChild(paymentMethodEle);
							orderDoc.getDocumentElement().appendChild(paymentMethodsEle);
							//orderDoc.getDocumentElement().appendChild(orderLinesEle);
							
							log.verbose("--------FanCreateReturn----checkIfPaymentProcessingRequiredOnReturnOrder---changeOrder--ip--"
									+XMLUtil.getXMLString(orderDoc));
						
							Document outDoc = CommonUtil.invokeAPI
								(env,"changeOrder", orderDoc);
						
							log.verbose("--------FanCreateReturn----checkIfPaymentProcessingRequiredOnReturnOrder---changeOrder--op--"
								+XMLUtil.getXMLString(outDoc));
						}
						
		
		
		
			
		log.verbose("--------FanCreateReturn-------checkIfPaymentProcessingRequiredOnReturnOrder----end--"
		+XMLUtil.getXMLString(inDoc));
		
		return inDoc;
	
		
	}
	
	
	
}
