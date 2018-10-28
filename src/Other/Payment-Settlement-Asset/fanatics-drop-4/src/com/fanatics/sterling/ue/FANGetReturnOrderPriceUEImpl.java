package com.fanatics.sterling.ue;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.OMPGetReturnOrderPriceUE;
import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.XMLUtil;
import com.fanatics.sterling.util.XPathUtil;

public class FANGetReturnOrderPriceUEImpl implements OMPGetReturnOrderPriceUE,YIFCustomApi {

	private static YFCLogCategory log = YFCLogCategory.instance("com.yantra.yfc.log.YFCLogCategory");

	private Properties props = null;
	
	@Override
	public Document getReturnOrderPrice(YFSEnvironment env, Document returnDoc)
			throws YFSUserExitException {
		
		log.verbose("--------------FANGetReturnOrderPriceUEImpl----returnDoc--1---"
				+XMLUtil.getXMLString(returnDoc));
		
		Element returnEle = returnDoc.getDocumentElement();
		Element returnEle2 = (Element)returnDoc.getElementsByTagName("OrderLine").item(0);
		String salesOrderNo = "";
		String salesOrderHeaderKey = "";
		
		if(returnEle2 != null){
			salesOrderHeaderKey = returnEle2.getAttribute("DerivedFromOrderHeaderKey");
		}

		returnEle2 = (Element)returnDoc.getElementsByTagName("DerivedFrom").item(0);
		
		if(returnEle2 != null){
			salesOrderNo = returnEle2.getAttribute("OrderNo");
		}
		
		
		
		// removing line charges from return order
		NodeList retLineChargesList5 = returnDoc.getElementsByTagName("LineCharges");
		
		if(retLineChargesList5.getLength() > 0){
			
			
			for(int q=0; q<retLineChargesList5.getLength(); q++){
				
				Element retLineCharges5 = (Element)retLineChargesList5.item(q);
				
				NodeList retLineChargeList5 = retLineCharges5.getElementsByTagName("LineCharge");
				
				if(retLineChargeList5.getLength() > 0){
					
					for(int p=0; p<retLineChargeList5.getLength(); p++){
						
						Element retLineCharge = (Element)retLineChargeList5.item(p);
						retLineCharge.getParentNode().removeChild(retLineCharge);
						p--;
					}
					
				} 
				
			}
			
	
		}

		
		
		
		// removing line taxes from return order
		NodeList retLineTaxesList5 = returnDoc.getElementsByTagName("LineTaxes");
		
		if(retLineTaxesList5.getLength() > 0){
			
			
			for(int q=0; q<retLineTaxesList5.getLength(); q++){
				
				Element retLineTaxes5 = (Element)retLineTaxesList5.item(q);
				
				NodeList retLineTaxList5 = retLineTaxes5.getElementsByTagName("LineTax");
				
				if(retLineTaxList5.getLength() > 0){
					
					for(int p=0; p<retLineTaxList5.getLength(); p++){
						
						Element retLineTax = (Element)retLineTaxList5.item(p);
						retLineTax.getParentNode().removeChild(retLineTax);
						p--;
					}
					
				} 
				
			}
			
	
		}
		
		
		log.verbose("--------------FANGetReturnOrderPriceUEImpl----returnDoc--1---2---"
				+XMLUtil.getXMLString(returnDoc));

		
		Document docGetSalesOrderListInput;
		try {
			
			docGetSalesOrderListInput = XMLUtil.createDocument("Order");
			Element eleGetSalesOrderListInput = docGetSalesOrderListInput.getDocumentElement();

			//XMLUtil.setAttribute(eleGetSalesOrderListInput, "EnterpriseCode", "FANATICS_WEB");
			XMLUtil.setAttribute(eleGetSalesOrderListInput, "DocumentType", "0001");
			
			XMLUtil.setAttribute(eleGetSalesOrderListInput, "OrderNo", salesOrderNo);
			XMLUtil.setAttribute(eleGetSalesOrderListInput, "OrderHeaderKey", salesOrderHeaderKey);
			
			log.verbose("--------------FANGetReturnOrderPriceUEImpl----salesDoc--1---"
					+XMLUtil.getXMLString(docGetSalesOrderListInput));
			
			Document docGetSalesOrderListOutput = CommonUtil.invokeAPI(env,"global/template/api/getOrderList_returnOrder.xml","getOrderList", docGetSalesOrderListInput);
			
			log.verbose("--------------FANGetReturnOrderPriceUEImpl----salesDoc--2---"
					+XMLUtil.getXMLString(docGetSalesOrderListOutput));
			
			
			log.verbose("-------------------------------1---------------------------------");
			
			NodeList returnLineList = returnDoc.getElementsByTagName("OrderLine");
			NodeList salesLineList = docGetSalesOrderListOutput.getElementsByTagName("OrderLine");
			
			for(int i=0; i<returnLineList.getLength(); i++){
				
				log.verbose("----------------------------2------------------------------------");
				
				Element retOrderLine = (Element)returnLineList.item(i);
				String primeLineRet = retOrderLine.getAttribute("PrimeLineNo");
				String returnQty = retOrderLine.getAttribute("OrderedQty");
				Float returnQtyFlt = Float.parseFloat(returnQty);
				
				Element retItemEle = (Element)retOrderLine.getElementsByTagName("Item").item(0);
				String retItemId = retItemEle.getAttribute("ItemID");
				
				for(int k=0; k<salesLineList.getLength(); k++){
					
					log.verbose("--------------------------3--------------------------------------");
					
					Element orderLine2 = (Element)salesLineList.item(k);
					String primeLineSales = orderLine2.getAttribute("PrimeLineNo");
					String reshipParentLineKey = orderLine2.getAttribute("ReshipParentLineKey");
					
					Element salesItemDetailsEle = (Element)orderLine2.getElementsByTagName("ItemDetails").item(0);
					String salesItemId = salesItemDetailsEle.getAttribute("ItemID");
					
					boolean isZCR = false;
					
					if(reshipParentLineKey != null){
						if(!reshipParentLineKey.equalsIgnoreCase("")){
							isZCR = true;
						}
					}
					
					log.verbose("--------------FANGetReturnOrderPriceUEImpl------2--0--"
							+XMLUtil.getElementXMLString(orderLine2));
					
					
					
					if(salesItemId.equalsIgnoreCase(retItemId) && (isZCR == false)){
						
						
						log.verbose("-------------------------4---------------------------------------");
						
						String originalOrderedQty = orderLine2.getAttribute("OriginalOrderedQty");
						Float originalOrderedQtyFlt = Float.parseFloat(originalOrderedQty);
						
						
						
						NodeList retLineChargeList = retOrderLine.getElementsByTagName("LineCharge");
						NodeList salesLineChargeList = orderLine2.getElementsByTagName("LineCharge");
						
						if(salesLineChargeList.getLength()>0){
							
							log.verbose("------------------------5----------------------------------------");
							
							for(int a=0; a<salesLineChargeList.getLength(); a++){
								
								log.verbose("--------------------6--------------------------------------------");
								
								Element salesLineCharge = (Element)salesLineChargeList.item(a);
								String salesChargeCategory = salesLineCharge.getAttribute("ChargeCategory");
								String salesChargeName = salesLineCharge.getAttribute("ChargeName");
								
//								boolean chargePresentInRetOrder = false;
//								
//								
//								for(int b=0; b<retLineChargeList.getLength(); b++){
//									
//									log.verbose("------------------------7----------------------------------------");
//									
//									Element retLineCharge = (Element)retLineChargeList.item(b);
//									String retChargeCategory = retLineCharge.getAttribute("ChargeCategory");
//									String retChargeName = retLineCharge.getAttribute("ChargeName");
//									
//									if(salesChargeCategory.equalsIgnoreCase(retChargeCategory) && salesChargeName.equalsIgnoreCase(retChargeName)){
//										log.verbose("----------------------------8------------------------------------");
//										chargePresentInRetOrder = true;
//									}
//									
//								}
//								
//								if(chargePresentInRetOrder == false){

									log.verbose("-------------------------------9---------------------------------");
										Float chargePerLine = Float.parseFloat(salesLineCharge.getAttribute("ChargePerLine"));
										
										Float refundAmount =  chargePerLine.floatValue() * (returnQtyFlt.floatValue()/originalOrderedQtyFlt.floatValue());
										
										String refundAmountStr =  String.format("%.02f", refundAmount.floatValue());
										
										log.verbose("-----FANGetReturnOrderPriceUEImpl----2--1---"+refundAmountStr+"----"+originalOrderedQtyFlt.toString()+"----"+returnQtyFlt.toString());
									
										Element retLineCharge2 = returnDoc.createElement("LineCharge");
										
										retLineCharge2.setAttribute("ChargePerLine", refundAmountStr);
										retLineCharge2.setAttribute("ChargeAmount", refundAmountStr);
										retLineCharge2.setAttribute("ChargeCategory", salesChargeCategory);
										retLineCharge2.setAttribute("ChargeName", salesChargeName);
									
										NodeList retLineChargesList2 = retOrderLine.getElementsByTagName("LineCharges");
										
										if(retLineChargesList2.getLength() == 0){
											
											log.verbose("----------------------------10------------------------------------");	
											Element retLineCharges = returnDoc.createElement("LineCharges");
											retLineCharges.appendChild(retLineCharge2);
											retOrderLine.appendChild(retLineCharges);
										}else{
											
											Element retLineCharges = (Element)retOrderLine.getElementsByTagName("LineCharges").item(0);
											log.verbose("------------------------------11----------------------------------");
											retLineCharges.appendChild(retLineCharge2);
											retOrderLine.appendChild(retLineCharges);
											
										}
							
//								}
							
							
						}
						
						
						
					}
						

						
						
						
						log.verbose("---------------------------------12-------------------------------");	
						
						
						log.verbose("--------------FANGetReturnOrderPriceUEImpl----returnDoc--1---3---"
								+XMLUtil.getXMLString(returnDoc));	

						
						
						NodeList retLineTaxList = retOrderLine.getElementsByTagName("LineTax");
						NodeList salesLineTaxList = orderLine2.getElementsByTagName("LineTax");
						
						if(salesLineTaxList.getLength()>0){
							
							log.verbose("---------------------------------13-------------------------------");
							
							for(int a=0; a<salesLineTaxList.getLength(); a++){
								log.verbose("------------------------------14----------------------------------");
								Element salesLineTax = (Element)salesLineTaxList.item(a);
								String salesChargeCategory = salesLineTax.getAttribute("ChargeCategory");
								String salesChargeName = salesLineTax.getAttribute("ChargeName");
								String salesTaxName = salesLineTax.getAttribute("TaxName");
								
//								boolean taxPresentInRetOrder = false;
//								
//								
//								for(int b=0; b<retLineTaxList.getLength(); b++){
//									log.verbose("-------------------------------15---------------------------------");	
//									Element retLineTax = (Element)retLineTaxList.item(b);
//									String retChargeCategory = retLineTax.getAttribute("ChargeCategory");
//									String retChargeName = retLineTax.getAttribute("ChargeName");
//									
//									if(salesChargeCategory.equalsIgnoreCase(retChargeCategory) && salesChargeName.equalsIgnoreCase(retChargeName)){
//										log.verbose("------------------------------16----------------------------------");	
//										taxPresentInRetOrder = true;
//									}
//									
//								}
//								
//								if(taxPresentInRetOrder == false){  

									log.verbose("---------------------------------17-------------------------------");
										Float taxPerLine = Float.parseFloat(salesLineTax.getAttribute("Tax"));
										
										Float refundAmount =  taxPerLine.floatValue() * (returnQtyFlt.floatValue()/originalOrderedQtyFlt.floatValue());
										
										String refundAmountStr =  String.format("%.02f", refundAmount.floatValue());
										
									log.verbose("-----FANGetReturnOrderPriceUEImpl----2--2---"+refundAmountStr+"----"+originalOrderedQtyFlt.toString()+"----"+returnQtyFlt.toString());
									
										Element retLineTax2 = returnDoc.createElement("LineTax");
										
										retLineTax2.setAttribute("Tax", refundAmountStr);
										retLineTax2.setAttribute("ChargeCategory", salesChargeCategory);
										retLineTax2.setAttribute("ChargeName", salesChargeName);
										retLineTax2.setAttribute("TaxName", salesTaxName);
									
										NodeList retLineTaxesList2 = retOrderLine.getElementsByTagName("LineTaxes");
										
										if(retLineTaxesList2.getLength() == 0){
											log.verbose("---------------------------18-------------------------------------");	
											Element retLineTaxes = returnDoc.createElement("LineTaxes");
											retLineTaxes.appendChild(retLineTax2);
											retOrderLine.appendChild(retLineTaxes);
										}else{
											
											Element retLineTaxes = (Element)retOrderLine.getElementsByTagName("LineTaxes").item(0);
											log.verbose("------------------------------19----------------------------------");
											retLineTaxes.appendChild(retLineTax2);
											retOrderLine.appendChild(retLineTaxes);
											
										}
							
//								}
							
							
						}
						
						
						
					}
						

						
						log.verbose("-----------------------------20-----------------------------------");	
						
						log.verbose("--------------FANGetReturnOrderPriceUEImpl----returnDoc--1---4---"
								+XMLUtil.getXMLString(returnDoc));
						
						
					}
					
					log.verbose("---------------------------------21-------------------------------");
					
				}
				
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.verbose("----------------------------------------22------------------------");
			e.printStackTrace();
		}

		
		
		//log.verbose("docGetSalesOrderListOutput output sourav ========  "+XMLUtil.getElementString(docGetSalesOrderListOutput.getDocumentElement()));
		
		log.verbose("--------------FANGetReturnOrderPriceUEImpl------3---"
				+XMLUtil.getXMLString(returnDoc));
		
		return returnDoc;
	}

	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
