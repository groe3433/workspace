package com.fanatics.sterling.api;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fanatics.sterling.ue.FANYFSCollectionCreditCardUEImpl;
import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.XMLUtil;
import com.fanatics.sterling.util.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class FANAddInfoAfterOrderCreation implements YIFCustomApi {
	
	private static YFCLogCategory log = YFCLogCategory.instance(YFCLogCategory.class);

	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
	}
	
	public Document appendAdditionalInfo(YFSEnvironment env, Document inDoc) throws Exception{
		
		
		Element orderEle = inDoc.getDocumentElement();
		String documentType = orderEle.getAttribute("DocumentType");
		String orderPurpose = orderEle.getAttribute("OrderPurpose");
		
		boolean orderPurposeRefundFlag = false;
		
		if(orderPurpose != null){
			if(orderPurpose.equalsIgnoreCase("REFUND")){
				orderPurposeRefundFlag = true;
			}
		}
		
		if(orderPurposeRefundFlag == true){
			return inDoc;
		}
		

		log.verbose("--------------AddInfoAfterOrderCreation-----initial--"
				+XMLUtil.getXMLString(inDoc));
		
		if(documentType.equalsIgnoreCase("0003")){
		
		inDoc = addMRLFeeIfRequired(env, inDoc);
		
		log.verbose("--------------AddInfoAfterOrderCreation-----after addMRLFeeIfRequired---"
				+XMLUtil.getXMLString(inDoc));
		
		inDoc = modifyPersonInfoIfGifteeReturn(env, inDoc);
		
		log.verbose("--------------AddInfoAfterOrderCreation----after addPersonInfo----"
				+XMLUtil.getXMLString(inDoc));
		
		// Logic for ReturnReason="Item Missing","Lost in Transit" and "Lost in Return" AND also for "Customer Can Keep" scenarios
		inDoc = checkIfCustomerCanKeepItem(env, inDoc);
		
		log.verbose("--------------AddInfoAfterOrderCreation----after checkIfCustomerCanKeepItem----"
				+XMLUtil.getXMLString(inDoc));
		
		}
		
		return inDoc;
	}
	
	
	
	// method added for the modification of person info for giftee return orders -- dev by Sourav
	
	private Document modifyPersonInfoIfGifteeReturn(YFSEnvironment env, Document inDoc)  throws Exception{
		
		
		// logic to add person info ship to 
		
			Element orderEle = inDoc.getDocumentElement();
			String documentType = orderEle.getAttribute("DocumentType");
			
			if(documentType.equalsIgnoreCase("0003")){
				
				log.verbose("--------------AddInfoAfterOrderCreation---addPersonInfo---8--"
						+XMLUtil.getXMLString(inDoc));
				
				String retOrdHdrKey = orderEle.getAttribute("OrderHeaderKey");
				
				Document orderDoc4 = XMLUtil.createDocument("Order");
				Element orderEle4 = orderDoc4.getDocumentElement();
				orderEle4.setAttribute("OrderHeaderKey", retOrdHdrKey);
				


				
				log.verbose("--------------AddInfoAfterOrderCreation---addPersonInfo---9--"
							+XMLUtil.getXMLString(orderDoc4));
				
				Document outDoc4 = CommonUtil.invokeAPI
						(env,"global/template/api/getOrderList_payment.xml","getOrderList", orderDoc4);
				
				log.verbose("--------------AddInfoAfterOrderCreation---addPersonInfo---10--"
						+XMLUtil.getXMLString(outDoc4));
				
				
				Element tempOrderEle = (Element)XPathUtil.getXpathNode(outDoc4, "/OrderList/Order");
				String entryType = tempOrderEle.getAttribute("EntryType");
				
				if(entryType != null){
					
					if(!entryType.equalsIgnoreCase("Call Center")){
						
						Element orderLine = (Element)XPathUtil.getXpathNode(outDoc4, "/OrderList/Order/OrderLines/OrderLine");
						String originalSalesOrderHeaderKey = orderLine.getAttribute("DerivedFromOrderHeaderKey");
						
						Document orderDoc = XMLUtil.createDocument("Order");
						Element orderEle2 = orderDoc.getDocumentElement();
						orderEle2.setAttribute("OrderHeaderKey", originalSalesOrderHeaderKey);

						
						log.verbose("--------------AddInfoAfterOrderCreation---addPersonInfo---11---"
									+XMLUtil.getXMLString(orderDoc));
						
						Document outDoc = CommonUtil.invokeAPI
								(env,"global/template/api/getOrderList_payment.xml","getOrderList", orderDoc);
						
						log.verbose("--------------AddInfoAfterOrderCreation---addPersonInfo---12---"
								+XMLUtil.getXMLString(outDoc));
						
						
						Element customAttributesRetOrdEle = (Element)XPathUtil.getXpathNode(outDoc4, "/OrderList/Order/CustomAttributes");
						
						
						Element orderDoc2 =  (Element)XPathUtil.getXpathNode(outDoc4, "/OrderList/Order");
						String processPaymentOnReturnOrder = orderDoc2.getAttribute("ProcessPaymentOnReturnOrder");
						
						Element personInfoShipToEle = (Element)XPathUtil.getXpathNode(outDoc4, "/OrderList/Order/PersonInfoShipTo");
						String eMailIDRetOrd = personInfoShipToEle.getAttribute("EMailID");
						
						// calling chageOrder with appended details
						
						Document orderDoc6 = XMLUtil.createDocument("Order");
						Element orderEle6 = orderDoc6.getDocumentElement();
						orderEle6.setAttribute("OrderHeaderKey", retOrdHdrKey);
						orderEle6.setAttribute("Action", "MODIFY");
						orderEle6.setAttribute("Override", "Y");
						
						// copying PersonInfoBillTo email to GifteeMailID
						if(processPaymentOnReturnOrder != null){
								
								if(processPaymentOnReturnOrder.equalsIgnoreCase("Y")){
									
									if(customAttributesRetOrdEle != null){
										
										String gifteeMailID = customAttributesRetOrdEle.getAttribute("GifteeMailID");
										
										if(gifteeMailID != null){
											
											if(!gifteeMailID.trim().equalsIgnoreCase("")){
												
												Element personInfoShipTo2 = (Element)XPathUtil.getXpathNode(outDoc4, "/OrderList/Order/PersonInfoShipTo");
												Element personInfoShipToEleClone2 = (Element) orderDoc6.importNode(personInfoShipTo2.cloneNode(true), true);
												personInfoShipToEleClone2.setAttribute("EMailID", gifteeMailID);
												orderDoc6.getDocumentElement().appendChild(personInfoShipToEleClone2);
												
												log.verbose("--------------AddInfoAfterOrderCreation---addPersonInfo---13--"
												+XMLUtil.getElementXMLString(personInfoShipToEleClone2));
												
												
												Element personInfoBillToEle = orderDoc6.createElement("PersonInfoBillTo");
												copyAttributes(personInfoShipTo2, personInfoBillToEle);
												personInfoBillToEle.setAttribute("EMailID", gifteeMailID);
												orderDoc6.getDocumentElement().appendChild(personInfoBillToEle);

												log.verbose("--------------AddInfoAfterOrderCreation---addPersonInfo---14--"
														+XMLUtil.getElementXMLString(personInfoBillToEle));
											}
										}
										

									}else{

										
										Element customAttributes4 = orderDoc6.createElement("CustomAttributes");
										customAttributes4.setAttribute("GifteeMailID", eMailIDRetOrd);
										orderDoc6.getDocumentElement().appendChild(customAttributes4);
										log.verbose("--------------AddInfoAfterOrderCreation---addPersonInfo---15---");
									
									}
									
								}
							}
									
						log.verbose("--------------AddInfoAfterOrderCreation---addPersonInfo---16---"
								+XMLUtil.getXMLString(orderDoc6));
					
						Document outDoc6 = CommonUtil.invokeAPI
							(env,"changeOrder", orderDoc6);
					
						log.verbose("--------------AddInfoAfterOrderCreation---addPersonInfo---17---"
							+XMLUtil.getXMLString(outDoc6));
					}
					

					
				}

		
			}
			

		
	return inDoc;
	
	}
	
	
	
	// method added for the incorporation of MRL Fee for return orders -- dev by Sourav

		private Document addMRLFeeIfRequired(YFSEnvironment env, Document inDoc) throws Exception{

			
			log.verbose("--------------addMRLFeeIfRequired------entry---"
					+XMLUtil.getXMLString(inDoc));
			
			Element orderEle = inDoc.getDocumentElement();
			String documentType = orderEle.getAttribute("DocumentType");
			String retOrdHdrKey = orderEle.getAttribute("OrderHeaderKey");
			
			Document orderDoc3 = XMLUtil.createDocument("Order");
			Element orderEle3 = orderDoc3.getDocumentElement();
			orderEle3.setAttribute("OrderHeaderKey", retOrdHdrKey);

			
			log.verbose("--------------AddInfoAfterOrderCreation------addMRLFeeIfRequired---before getOrderList--16--"
						+XMLUtil.getXMLString(orderDoc3));
			
			Document outDoc4 = CommonUtil.invokeAPI
					(env,"global/template/api/getOrderList_payment.xml","getOrderList", orderDoc3);
			
			log.verbose("--------------AddInfoAfterOrderCreation------addMRLFeeIfRequired---after getOrderList---17--"
					+XMLUtil.getXMLString(outDoc4));
			
			
			Element orderLine = (Element)XPathUtil.getXpathNode(outDoc4, "/OrderList/Order/OrderLines/OrderLine");
			String originalSalesOrderHeaderKey = orderLine.getAttribute("DerivedFromOrderHeaderKey");
			
			Element overallTotalsEle = (Element)outDoc4.getElementsByTagName("OverallTotals").item(0);
			String grandTotal = overallTotalsEle.getAttribute("GrandTotal");
			Float grandTotalFlt = Float.parseFloat(grandTotal);
			
			
			
			boolean customerCanKeep = false;
				
			NodeList orderLineList2 = outDoc4.getElementsByTagName("OrderLine");
			
			for(int i=0; i<orderLineList2.getLength(); i++){
				
				Element orderLineEle = (Element)orderLineList2.item(i);
				String lineType = orderLineEle.getAttribute("LineType");
				
				if(lineType.equalsIgnoreCase("Credit")){
					customerCanKeep = true;
				}
			}
			
			
			
			log.verbose("------------------documentType-----------------"+documentType);
						
	
					
					boolean MRLFeePresentInXml = false;
					
					NodeList headerChargeList = outDoc4.getDocumentElement().getElementsByTagName("HeaderCharge");
					
					// checking if MRl Fee is present in header charge
					if(headerChargeList.getLength()>0){
						
						for(int i=0; i<headerChargeList.getLength(); i++){
							
							Element headerCharge = (Element)headerChargeList.item(i);
							String chargeCategory = headerCharge.getAttribute("ChargeCategory");
							
							if(chargeCategory.equalsIgnoreCase("MRLFee")){
								MRLFeePresentInXml = true;
								log.verbose("-----------MRl Fee is present in header charge-------------");
							}
						}
					}
					

					
					
				//checking if MRLFee present in Custom Attributes of return order
				if(MRLFeePresentInXml == false){
					
					Element outEle = (Element)outDoc4.getDocumentElement().getElementsByTagName("CustomAttributes").item(0);
					
					boolean AddMRLFee = true;
					
					
					if(outEle != null){
						
						String isMRLFeeWaived = outEle.getAttribute("IsMRLFeeWaived");

						if(isMRLFeeWaived != null){
							
							if(isMRLFeeWaived.equalsIgnoreCase("Y")){

								AddMRLFee = false;
								log.verbose("------MRLFee present in Custom Attributes of return order-------------");
								}
							
						}
					}
					
					
					
					//checking if MRLFee present in Custom Attributes of sales order
					if(AddMRLFee == true){		
						
						
						Document orderDoc = XMLUtil.createDocument("Order");
						Element orderEle2 = orderDoc.getDocumentElement();
						orderEle2.setAttribute("OrderHeaderKey", originalSalesOrderHeaderKey);

						
						log.verbose("--------------AddInfoAfterOrderCreation------addMRLFeeIfRequired--18--"
									+XMLUtil.getXMLString(orderDoc));
						
						Document outDoc = CommonUtil.invokeAPI
								(env,"global/template/api/getOrderList_payment.xml","getOrderList", orderDoc);
						
						log.verbose("--------------AddInfoAfterOrderCreation---addMRLFeeIfRequired---19---"
								+XMLUtil.getXMLString(outDoc));
						
						Element outEle2 = (Element)outDoc.getDocumentElement().getElementsByTagName("CustomAttributes").item(0);
						
						if(outEle2 != null){
							
							String isMRLFeeWaived2 = outEle2.getAttribute("IsMRLFeeWaived");
							
							if(isMRLFeeWaived2 != null){
								
								if(isMRLFeeWaived2.equalsIgnoreCase("Y")){
									AddMRLFee = false;
									log.verbose("------MRLFee present in Custom Attributes of sales order----------");
									}
								
							}
						}
						
						
					}
					
					
						// adding MRL Fee
						if((AddMRLFee == true) && (customerCanKeep == false)){
							
							Document commonCodeDoc = XMLUtil.createDocument("CommonCode");
							Element ordercommonCodeEle = commonCodeDoc.getDocumentElement();
							ordercommonCodeEle.setAttribute("CodeValue", "MRLFeeRate");

							
							log.verbose("-------FANAddInfoAfterOrderCreation----addMRLFeeIfRequired---21---"
										+XMLUtil.getXMLString(commonCodeDoc));
							
							Document outDoc3 = CommonUtil.invokeAPI
									(env,"getCommonCodeList", commonCodeDoc);
							
							log.verbose("-------FANAddInfoAfterOrderCreation----addMRLFeeIfRequired---22---"
									+XMLUtil.getXMLString(outDoc3));
							
							Element outEle3 = (Element)outDoc3.getDocumentElement().getElementsByTagName("CommonCode").item(0);
							
							String codeShortDescription = outEle3.getAttribute("CodeShortDescription");
						      
							String MRLFeeRate = codeShortDescription;
							Float MRLFeeRateFlt = Float.parseFloat(MRLFeeRate);
							
							
							Document orderDoc6 = XMLUtil.createDocument("Order");
							Element orderEle6 = orderDoc6.getDocumentElement();
							orderEle6.setAttribute("OrderHeaderKey", retOrdHdrKey);
							orderEle6.setAttribute("Action", "MODIFY");
							orderEle6.setAttribute("Override", "Y");
							

							Element headerCharges = orderDoc6.createElement("HeaderCharges");
							Element headerCharge = orderDoc6.createElement("HeaderCharge");
							
							headerCharge.setAttribute("ChargeAmount", MRLFeeRate);
							headerCharge.setAttribute("ChargeCategory", "MRLFee");
							headerCharge.setAttribute("ChargeName", "MRLFee");
							

							
//							Element headerChargesOld = (Element) inDoc.getElementsByTagName("HeaderCharges").item(0);
//							
//							log.verbose("------------21--2-"+XMLUtil.getElementXMLString(headerChargesOld));
//							
//							if(headerChargesOld == null){
//								log.verbose("------------------------------22--------------------------------------");
//								headerCharges.appendChild(headerCharge);
//								orderDoc6.getDocumentElement().appendChild(headerCharges);
//							}else{
//								log.verbose("------------------------------23--------------------------------------");
//								headerChargesOld.appendChild(headerCharge);
//							}
							
							
							
							// collecting the part of MRLFee as the MRLFee itself is greater than the total refund amount
							if(grandTotalFlt.floatValue() <= MRLFeeRateFlt.floatValue() ){
								
								MRLFeeRateFlt = grandTotalFlt.floatValue();
								
								headerCharge.setAttribute("ChargeAmount", String.valueOf(MRLFeeRateFlt.floatValue()));
									
									Element notes = orderDoc6.createElement("Notes");
									Element note = orderDoc6.createElement("Note");
									
									note.setAttribute("NoteText", "MRL Fee reduced/waived as refund amount is less than $0.00");
									note.setAttribute("Tranid", "changeOrder");
									note.setAttribute("VisibleToAll", "Y");
									
									notes.appendChild(note);
									orderDoc6.getDocumentElement().appendChild(notes);
									log.verbose("----------grandTotalFlt.floatValue() <= MRLFeeRateFlt.floatValue()------------");
								}
							
							
							headerCharges.appendChild(headerCharge);
							orderDoc6.getDocumentElement().appendChild(headerCharges);
							
							
							log.verbose("---FANAddInfoAfterOrderCreation---addMRLFeeIfRequired---changeOrder ip return order---"
										+XMLUtil.getXMLString(orderDoc6));
							
							Document outDoc6 = CommonUtil.invokeAPI
									(env,"changeOrder", orderDoc6);
							
							log.verbose("-----FANAddInfoAfterOrderCreation--addMRLFeeIfRequired----changeOrder op return order---"
									+XMLUtil.getXMLString(outDoc6));
							
							
							// setting IsMRLFeeWaived="Y" on sales order so that future return orders do not have MRL Fee
							
							Document orderDoc = XMLUtil.createDocument("Order");
							Element orderEle2 = orderDoc.getDocumentElement();
							orderEle2.setAttribute("OrderHeaderKey", originalSalesOrderHeaderKey);
							orderEle2.setAttribute("Action", "MODIFY");
							orderEle2.setAttribute("Override", "Y");
							

							Element customAttributesEle = orderDoc.createElement("CustomAttributes");
							customAttributesEle.setAttribute("IsMRLFeeWaived", "Y");
							
							orderDoc.getDocumentElement().appendChild(customAttributesEle);
							
							
							
							log.verbose("---FANAddInfoAfterOrderCreation---addMRLFeeIfRequired---changeOrder ip sales order---"
										+XMLUtil.getXMLString(orderDoc));
							
							Document outDoc = CommonUtil.invokeAPI
									(env,"changeOrder", orderDoc);
							
							log.verbose("-----FANAddInfoAfterOrderCreation--addMRLFeeIfRequired----changeOrder op sales order---"
									+XMLUtil.getXMLString(outDoc));
							
							
						}


				}
								
	
				
			log.verbose("------FANAddInfoAfterOrderCreation--------addMRLFeeIfRequired---exit--"
						+XMLUtil.getXMLString(inDoc));
			
			return inDoc;
		}

		
	
		// method added to check if customer can keep the item and claim refund for return orders -- dev by Sourav

		private Document checkIfCustomerCanKeepItem(YFSEnvironment env, Document inDoc) throws Exception{

			
			log.verbose("--------------checkIfCustomerCanKeepItem------entry---"
					+XMLUtil.getXMLString(inDoc));
			
			Element orderEle = inDoc.getDocumentElement();
			String documentType = orderEle.getAttribute("DocumentType");
			String retOrdHdrKey = orderEle.getAttribute("OrderHeaderKey");
			String enterpriseCode = orderEle.getAttribute("EnterpriseCode");
			String orderNo = orderEle.getAttribute("OrderNo");
			
			Document orderDoc3 = XMLUtil.createDocument("Order");
			Element orderEle3 = orderDoc3.getDocumentElement();
			orderEle3.setAttribute("OrderHeaderKey", retOrdHdrKey);
			
			String shipNode = "";

			boolean customerCanKeep = false;
			boolean refundEvenIfItemNotReturned = false;
			
			log.verbose("--------------AddInfoAfterOrderCreation------checkIfCustomerCanKeepItem---before getOrderList--23--"
						+XMLUtil.getXMLString(orderDoc3));
			
			Document outDoc4 = CommonUtil.invokeAPI
					(env,"global/template/api/getOrderList_payment.xml","getOrderList", orderDoc3);
			
			log.verbose("--------------AddInfoAfterOrderCreation------checkIfCustomerCanKeepItem---after getOrderList---24--"
					+XMLUtil.getXMLString(outDoc4));
			
			
			NodeList orderLineList2 = outDoc4.getElementsByTagName("OrderLine");
			
			for(int i=0; i<orderLineList2.getLength(); i++){
				
				Element orderLineEle = (Element)orderLineList2.item(i);
				String lineType = orderLineEle.getAttribute("LineType");
				String returnReason = orderLineEle.getAttribute("ReturnReason");
				
				if(lineType.equalsIgnoreCase("Credit")){
					customerCanKeep = true;
				}
				
				if(returnReason.equalsIgnoreCase("Item Missing") || returnReason.equalsIgnoreCase("Lost in Transit") 
						|| returnReason.equalsIgnoreCase("Damaged in Transit") || returnReason.equalsIgnoreCase("Item Defective") 
						|| returnReason.equalsIgnoreCase("Wrong Order") || returnReason.equalsIgnoreCase("Items in Good Condition")){
					refundEvenIfItemNotReturned = true;
				}
				
			}
			
			Document receiptDoc = XMLUtil.createDocument("Receipt");
			Element receiptEle = receiptDoc.getDocumentElement();
			receiptEle.setAttribute("DocumentType", "0003");
			
			
			Element shipmentEle = receiptDoc.createElement("Shipment");
			shipmentEle.setAttribute("DocumentType", "0003");
			shipmentEle.setAttribute("EnterpriseCode", enterpriseCode);
			shipmentEle.setAttribute("ReleaseNo", "1");
			shipmentEle.setAttribute("OrderNo", orderNo);
			

			Element receiptLinesEle = receiptDoc.createElement("ReceiptLines");
			
			
			NodeList orderLineList = outDoc4.getElementsByTagName("OrderLine");
			
			for(int i=0; i<orderLineList.getLength(); i++){
				
			Element orderLineEle = (Element)orderLineList.item(i);
				
			String lineType = orderLineEle.getAttribute("LineType");
			String returnReason = orderLineEle.getAttribute("ReturnReason");
			String orderedQty = orderLineEle.getAttribute("OrderedQty");
			String primeLineNo = orderLineEle.getAttribute("PrimeLineNo");
			Element itemDetailsEle = (Element)orderLineEle.getElementsByTagName("ItemDetails").item(0);
			String itemID = itemDetailsEle.getAttribute("ItemID");
			
				if(lineType.equalsIgnoreCase("Credit") || returnReason.equalsIgnoreCase("Item Missing") || returnReason.equalsIgnoreCase("Lost in Transit") ||
						returnReason.equalsIgnoreCase("Lost in Return")){
					
					Document shipNodeDoc = XMLUtil.createDocument("ShipNode");
					Element shipNodeEle = shipNodeDoc.getDocumentElement();
					shipNodeEle.setAttribute("OwnerKey", "FANATICS_US");
					shipNodeEle.setAttribute("NodeType", "DC");

					
					log.verbose("--------------AddInfoAfterOrderCreation------checkIfCustomerCanKeepItem---before getShipNodeList--25--"
								+XMLUtil.getXMLString(shipNodeDoc));
					
					Document outDoc5 = CommonUtil.invokeAPI
							(env,"global/template/api/getShipNodeList.xml","getShipNodeList", shipNodeDoc);
					
					log.verbose("--------------AddInfoAfterOrderCreation------checkIfCustomerCanKeepItem---after getShipNodeList---26--"
							+XMLUtil.getXMLString(outDoc5));
					
					NodeList shipNodeList = outDoc5.getElementsByTagName("ShipNode");
					
					
					for(int k=0; k<shipNodeList.getLength(); k++){
						
						Element shipNodeEle2 = (Element)shipNodeList.item(k);
						String shipNode2 = shipNodeEle2.getAttribute("ShipNode");
						
						if(shipNode2.contains("DC-")){
							shipNode = shipNode2;
							log.verbose("------------------shipNode----------------"+shipNode);
							break;
						}
					}
				
					Element receiptLineEle = receiptDoc.createElement("ReceiptLine");
					
					receiptLineEle.setAttribute("DispositionCode", "CUSTOMER_CAN_KEEP");
					receiptLineEle.setAttribute("ItemID", itemID);
					receiptLineEle.setAttribute("Quantity", orderedQty);
					receiptLineEle.setAttribute("UnitOfMeasure", "EACH");
					receiptLineEle.setAttribute("SubLineNo", "1");
					receiptLineEle.setAttribute("PrimeLineNo", primeLineNo);
					
					shipmentEle.setAttribute("ReceivingNode", shipNode);
					receiptEle.setAttribute("ReceivingNode", shipNode);
					
					receiptLinesEle.appendChild(receiptLineEle);
				}
			
			}
			
			
			receiptDoc.getDocumentElement().appendChild(receiptLinesEle);
			receiptDoc.getDocumentElement().appendChild(shipmentEle);
			
			if((customerCanKeep == true) || (refundEvenIfItemNotReturned == true)){

				
				// setting shipnodes to the order lines
				
				Document orderDoc = XMLUtil.createDocument("Order");
				Element orderEle2 = orderDoc.getDocumentElement();
				orderEle2.setAttribute("OrderHeaderKey", retOrdHdrKey);
				orderEle2.setAttribute("Action", "MODIFY");
				orderEle2.setAttribute("Override", "Y");
				
				Element orderLinesEle = orderDoc.createElement("OrderLines");
				
				
				for(int i=0; i<orderLineList2.getLength(); i++){
					
					Element orderLineEle = (Element)orderLineList2.item(i);
					
					Element orderLineEle2 = orderDoc.createElement("OrderLine");
					
					orderLineEle2.setAttribute("PrimeLineNo", orderLineEle.getAttribute("PrimeLineNo"));
					orderLineEle2.setAttribute("ShipNode", shipNode);
					orderLineEle2.setAttribute("SubLineNo", "1");
					
					orderLinesEle.appendChild(orderLineEle2);
				}
				
				
				orderDoc.getDocumentElement().appendChild(orderLinesEle);
				
				
				
				log.verbose("--------------AddInfoAfterOrderCreation---checkIfCustomerCanKeepItem---changeOrder ip---27--"
							+XMLUtil.getXMLString(orderDoc));
				
				Document outDoc = CommonUtil.invokeAPI
						(env,"changeOrder", orderDoc);
				
				log.verbose("-----FANAddInfoAfterOrderCreation----checkIfCustomerCanKeepItem--changeOrder op sales order--28--"
						+XMLUtil.getXMLString(outDoc));
				
	
				
				
				log.verbose("--------------AddInfoAfterOrderCreation------checkIfCustomerCanKeepItem---before scheduleOrder--29--"
						+XMLUtil.getXMLString(orderDoc3));
					
				CommonUtil.invokeAPI(env, "scheduleOrder", orderDoc3);
				
				log.verbose("--------------AddInfoAfterOrderCreation------checkIfCustomerCanKeepItem---before scheduleOrder--30--");
				
				
				log.verbose("--------------AddInfoAfterOrderCreation---checkIfCustomerCanKeepItem---FanReturnReceiptSync ip---31--"
						+XMLUtil.getXMLString(receiptDoc));
				
				Document FanReturnReceiptSyncOpDoc = CommonUtil.invokeService(env, "FanReturnReceiptSync", receiptDoc);
				
				log.verbose("--------------FanReturnReceipt---checkIfCustomerCanKeepItem---FanReturnReceiptSync op---32--"
						+XMLUtil.getXMLString(FanReturnReceiptSyncOpDoc));
				
			}

				
			log.verbose("------FANAddInfoAfterOrderCreation--------checkIfCustomerCanKeepItem---exit--"
						+XMLUtil.getXMLString(inDoc));
			
			return inDoc;
		}
		
		/**
		 * @param Source
		 *            Element
		 * @param Destination
		 *            Element
		 * @return void
		 */

		public static void copyAttributes(Element srcElem, Element destElem) {
			NamedNodeMap attrMap = srcElem.getAttributes();
			int attrLength = attrMap.getLength();
			for (int count = 0; count < attrLength; count++) {
				Node attr = attrMap.item(count);
				String attrName = attr.getNodeName();
				String attrValue = attr.getNodeValue();
				destElem.setAttribute(attrName, attrValue);

			}

		}
		

}
