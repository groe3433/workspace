package com.fanatics.sterling.ue;

import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.FANConstants;
import com.fanatics.sterling.util.XMLUtil;
import com.fanatics.sterling.util.XPathUtil;
import com.fanatics.sterling.constants.CreateOrderConstants;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSBeforeCreateOrderUE;

/**
 * 
 * This class provides the Use Exit implementation for the
 * YFSBeforeCreateOrderUE The extended item attributes are queried from the item
 * database and populated as orderline extensions
 * 
 * @(#) FANYFSBeforeCreateOrderUEImpl.java Created on Apr 11, 2016 11:30:22 AM
 * 
 *      Package Declaration: File Name: FANYFSBeforeCreateOrderUEImpl.java
 *      Package Name: com.fanatics.sterling.ue Project name: Fanatics Type
 *      Declaration: Class Name: FANYFSBeforeCreateOrderUEImpl Type Comment:
 * 
 * 
 * @author kntagkas
 * @version 1.0
 * @history
 * 
 * 
 * 
 * 			(C) Copyright 2016 by owner. All Rights Reserved.
 * 
 *          This software is the confidential and proprietary information of the
 *          owner. ("Confidential Information"). Redistribution of the source
 *          code or binary form is not permitted without prior authorization
 *          from the owner.
 * 
 */

public class FANYFSBeforeCreateOrderUEImpl implements YFSBeforeCreateOrderUE, YIFCustomApi {

	private static YFCLogCategory log = YFCLogCategory.instance("com.yantra.yfc.log.YFCLogCategory");

	private Properties props = null;

	@Override
	public String beforeCreateOrder(YFSEnvironment env, String inStr) throws YFSUserExitException {
		
		Document inDoc  = SCXmlUtil.createFromString(inStr);
		Document outDoc = beforeCreateOrder(env, inDoc);
		inStr 			= SCXmlUtil.getString(outDoc);
		
		return inStr;
	}

	@Override
	public Document beforeCreateOrder(YFSEnvironment env, Document inDoc) throws YFSUserExitException {

		log.verbose("FANYFSBeforeCreateOrderUEImpl -> Begin"+XMLUtil.getXMLString(inDoc));
		
		try {
			
			Element orderEle = inDoc.getDocumentElement();
			String documentType = orderEle.getAttribute("DocumentType");
			String orderPurpose = orderEle.getAttribute("OrderPurpose");
			log.verbose("purpose is "+ orderPurpose);
			boolean orderPurposeRefundFlag = false;
			
			if(orderPurpose != null){
				if(orderPurpose.equalsIgnoreCase("REFUND")){
					orderPurposeRefundFlag = true;
				}
			}
			
			if(orderPurposeRefundFlag == true){
				log.verbose("purpose is Refund");			
				inDoc = replaceWithGiftCertItem(env, inDoc);
				
				return inDoc;
			}
			
			log.verbose("------------------documentType-----------------"+documentType);
						
				if(documentType.equalsIgnoreCase("0001")){
					
					String SellerOrganizationCode = SCXmlUtil.getXpathAttribute(inDoc.getDocumentElement(), CreateOrderConstants.XPATH_SELLER_ORG_CODE);
					
					Document getOrgListInput 	  = SCXmlUtil.createDocument(CreateOrderConstants.ATT_ORGANIZATION);
					getOrgListInput.getDocumentElement().setAttribute(CreateOrderConstants.ATT_ORGANIZATION_CODE, SellerOrganizationCode);
					
					log.verbose("Calling getOrganizationList API for SellerOrganizationCode : " + SellerOrganizationCode);
					
					Document getOrgListOutput     = CommonUtil.invokeAPI(env, CreateOrderConstants.API_GET_ORG_LIST,
							getOrgListInput);
					
					String ExtnCancelOnBackorder  = SCXmlUtil.getXpathAttribute(getOrgListOutput.getDocumentElement(), 
							CreateOrderConstants.XPATH_EXTN_CANCEL);

					inDoc.getDocumentElement().setAttribute("CancelOrderOnBackorder", ExtnCancelOnBackorder);
					
					String carrierServiceCode    = SCXmlUtil.getXpathAttribute(inDoc.getDocumentElement(),
							CreateOrderConstants.XPATH_CARRIER_SERVICE_CODE);
					Document listCarrierServiceInput = SCXmlUtil.createDocument(CreateOrderConstants.EL_CARRIER_SERVICE);
					listCarrierServiceInput.getDocumentElement().setAttribute(CreateOrderConstants.ATT_CARRIER_SERVICE_CODE, carrierServiceCode);

					log.verbose("Calling listCarrierService API for CarrierServiceCode : " + carrierServiceCode);
					
					Document listCarrierServiceOutput = CommonUtil.invokeAPI(env, CreateOrderConstants.API_LIST_CARRIER_SERVICE,
							listCarrierServiceInput);
					
					boolean carrierListNotEmpty = listCarrierServiceOutput.getDocumentElement().hasChildNodes();
					
					Element rootMessageNode = inDoc.getDocumentElement();
					
					if (rootMessageNode != null) {
						
						NodeList orderLines = rootMessageNode.getElementsByTagName(CreateOrderConstants.EL_ORDER_LINE);
						
						if (orderLines != null && orderLines.getLength() > 0) {
							for (int i = 0; i < orderLines.getLength(); i++) {
								if (orderLines.item(i).getNodeType() == Node.ELEMENT_NODE) {
									
									Element orderLine   = (Element) orderLines.item(i);
									Element orderDates  = SCXmlUtil.getXpathElement(orderLine, CreateOrderConstants.XPATH_ORDER_DATES);
									Element orderDate   = SCXmlUtil.getXpathElement(orderLine,
											CreateOrderConstants.XPATH_PROMISHED_SHIP_DATE);
									
									/*
									 * default value for al dates is the current date
									 */
									
									String expectedDate = getSimpleSysDate();
									
									if(null != orderDate){
										
										String expectedDateTmp = orderDate.getAttribute(CreateOrderConstants.ATT_COMMITTED_DATE);
										
										if(!expectedDateTmp.isEmpty() && expectedDateTmp != null){
											expectedDate = expectedDateTmp;
											}
									}else{
										
										log.error("FANYFSBeforeCreateOrderUEImpl --> ERROR: OrderDate Mandatory field missing!");
										log.info("FANYFSBeforeCreateOrderUEImpl --> Defaulting promised ship date to current Date ..");
										
									}
									
									String minDelivery = expectedDate;
									String maxDelivery = expectedDate;

									if (carrierListNotEmpty) {

										String maxTransitDays = SCXmlUtil.getXpathAttribute(
												listCarrierServiceOutput.getDocumentElement(),
												CreateOrderConstants.XPATH_MAX_TRANSIT_DAYS);
										String fixedTransitDays = SCXmlUtil.getXpathAttribute(
												listCarrierServiceOutput.getDocumentElement(),
												CreateOrderConstants.XPATH_FIXED_TRANSIT_DAYS);

										minDelivery = calcDate(expectedDate, fixedTransitDays);
										maxDelivery = calcDate(expectedDate, maxTransitDays);

									} else {
										log.verbose("Carrier Service not found - defaulting to PROMISED_SHIP_DATE");
									}

									Element EleMinDelivery = (Element) orderDate.cloneNode(false);
									Element EleMaxDelivery = (Element) orderDate.cloneNode(false);
									
									orderDates.appendChild(EleMinDelivery);
									orderDates.appendChild(EleMaxDelivery);
									
									changeElements(EleMinDelivery, minDelivery, CreateOrderConstants.ELE_MIN_DELIVERY);
									changeElements(EleMaxDelivery, maxDelivery, CreateOrderConstants.ELE_MAX_DELIVERY);
																
								}
							}
						}
					}
					
				}
			
		if(documentType.equalsIgnoreCase("0003")){
					
			inDoc = addPersonInfo(env,inDoc);
		}
			
		} catch (Exception e) {
			log.error("FANYFSBeforeCreateOrderUEImpl --> ERROR: " + e.getMessage(), e);
			throw new YFSUserExitException("FANYFSBeforeCreateOrderUEImpl");
		}

		log.verbose("--------------FANYFSBeforeCreateOrderUEImpl------100--"
				+XMLUtil.getXMLString(inDoc));
		log.verbose("FANYFSBeforeCreateOrderUEImpl -> End");
		
		return inDoc;
	}

	
	
	private Document replaceWithGiftCertItem(YFSEnvironment yfsEnv, Document inDoc) {
		
		/**
		 * Neeraj: Refund Code Starts:
		 *  Fetch the Gift Certificate Item, instead of the current item and stamp the same in the createOrder xml.
		 */
		
		log.verbose("Inside replaceWithGiftCertItem");
		String strItemID = XMLUtil.getXpathProperty(inDoc, "/Order/OrderLines/OrderLine/Item/@ItemID");
		log.verbose("replaceWithGiftCertItem 1 "+ strItemID);
		
		Document docIPExternalSystem = null;
		Document docOPExternalSystem = null;
		
		String strSellerOrgCode = inDoc.getDocumentElement().getAttribute(FANConstants.ATT_SELLER_ORG_CODE);
		
		String strIPExternalSystem = "<Order SellerOrganizationCode='"+strSellerOrgCode+"' ><Item ItemID='"+strItemID+"'></Item></Order>";

		try 
		{  
			docIPExternalSystem = XMLUtil.getDocument(strIPExternalSystem);
			log.verbose("replaceWithGiftCertItem 2 "+ XMLUtil.getXMLString(docIPExternalSystem));
			
			docOPExternalSystem = CommonUtil.invokeService(yfsEnv, "FANGetActualGiftCertSKU", docIPExternalSystem);
			log.verbose("replaceWithGiftCertItem 3 "+ XMLUtil.getXMLString(docOPExternalSystem));
			
		} catch (Exception e) {  
			e.printStackTrace();  
		}
		
		strItemID = XMLUtil.getXpathProperty(docOPExternalSystem, "/Order/Item/@ItemID");
		log.verbose("replaceWithGiftCertItem 4 "+ strItemID);
		
		Element eleItem = (Element) inDoc.getElementsByTagName(FANConstants.CONSTANT_ITEM).item(0);
		eleItem.setAttribute(FANConstants.ITEM_ID, strItemID);
		log.verbose("replaceWithGiftCertItem 5 "+ XMLUtil.getXMLString(inDoc));
		
		return inDoc;
	}
	
	// method added for the incorporation person info for return orders -- dev by Sourav
	
	private Document addPersonInfo(YFSEnvironment env, Document inDoc)  throws Exception{
		
		Element personInfoShipTo = (Element)inDoc.getElementsByTagName("PersonInfoShipTo").item(0);
		boolean personInfoShipToPresent = true;
		
		if(personInfoShipTo == null){
			personInfoShipToPresent = false;
		}else{
			if(!personInfoShipTo.hasAttributes()){
				personInfoShipToPresent = false;
			}
		}
		
		if(personInfoShipToPresent == false){
			
			
			Element orderEle = inDoc.getDocumentElement();
			String documentType = orderEle.getAttribute("DocumentType");
			
			log.verbose("--------------FANYFSBeforeCreateOrderUEImpl------30---"
					+XMLUtil.getXMLString(inDoc));
			
			Element orderLine = (Element)XPathUtil.getXpathNode(inDoc, "/Order/OrderLines/OrderLine");
			
			if(orderLine != null){
				
				String originalSalesOrderHeaderKey = orderLine.getAttribute("DerivedFromOrderHeaderKey");
				
				log.verbose("------------------documentType-----------------"+documentType);
				
				Document orderDoc = XMLUtil.createDocument("Order");
				Element orderEle2 = orderDoc.getDocumentElement();
				orderEle2.setAttribute("OrderHeaderKey", originalSalesOrderHeaderKey);

				
				log.verbose("--------------FANYFSBeforeCreateOrderUEImpl------31---"
							+XMLUtil.getXMLString(orderDoc));
				
				Document outDoc = CommonUtil.invokeAPI
						(env,"global/template/api/getOrderList_payment.xml","getOrderList", orderDoc);
				
				log.verbose("--------------FANYFSBeforeCreateOrderUEImpl------32---"
						+XMLUtil.getXMLString(outDoc));
				
				
				Element personInfoShipToEle = (Element)XPathUtil.getXpathNode(outDoc, "/OrderList/Order/PersonInfoShipTo");
				log.verbose("--------------FANYFSBeforeCreateOrderUEImpl------personInfoShipToEle---"+XMLUtil.getElementXMLString(personInfoShipToEle));
				Element personInfoShipToEleClone = (Element) inDoc.importNode(personInfoShipToEle.cloneNode(true), true);
				inDoc.getDocumentElement().appendChild(personInfoShipToEleClone);
			}	

		}
		
		
		
		Element personInfoBillTo = (Element)inDoc.getElementsByTagName("PersonInfoBillTo").item(0);
		boolean personInfoBillToPresent = true;
		
		if(personInfoBillTo == null){
			personInfoBillToPresent = false;
		}else{
			if(!personInfoBillTo.hasAttributes()){
				personInfoBillToPresent = false;
			}
		}
		
		if(personInfoBillToPresent == false){
			
			
			Element orderEle = inDoc.getDocumentElement();
			String documentType = orderEle.getAttribute("DocumentType");
			
			log.verbose("--------------FANYFSBeforeCreateOrderUEImpl------33---"
					+XMLUtil.getXMLString(inDoc));
			
			Element orderLine = (Element)XPathUtil.getXpathNode(inDoc, "/Order/OrderLines/OrderLine");
			
			if(orderLine != null){
				
				String originalSalesOrderHeaderKey = orderLine.getAttribute("DerivedFromOrderHeaderKey");
				
				log.verbose("------------------documentType-----------------"+documentType);
				
				Document orderDoc = XMLUtil.createDocument("Order");
				Element orderEle2 = orderDoc.getDocumentElement();
				orderEle2.setAttribute("OrderHeaderKey", originalSalesOrderHeaderKey);

				
				log.verbose("--------------FANYFSBeforeCreateOrderUEImpl------34---"
							+XMLUtil.getXMLString(orderDoc));
				
				Document outDoc = CommonUtil.invokeAPI
						(env,"global/template/api/getOrderList_payment.xml","getOrderList", orderDoc);
				
				log.verbose("--------------FANYFSBeforeCreateOrderUEImpl------35---"
						+XMLUtil.getXMLString(outDoc));
				
				
				Element personInfoBillToEle = (Element)XPathUtil.getXpathNode(outDoc, "/OrderList/Order/PersonInfoBillTo");
				
				Element personInfoBillToEleClone = (Element) inDoc.importNode(personInfoBillToEle
						.cloneNode(true), true);
				inDoc.getDocumentElement().appendChild(personInfoBillToEleClone);	
				
			}
			


		}
		
	return inDoc;
	
	}
	
	
	
	// method added for the incorporation of MRL Fee for return orders -- dev by Sourav

		private Document addMRLFeeIfRequired(YFSEnvironment env, Document inDoc) throws Exception{

			Element orderEle = inDoc.getDocumentElement();
			String documentType = orderEle.getAttribute("DocumentType");
			
			log.verbose("--------------FANYFSBeforeCreateOrderUEImpl------1---"
					+XMLUtil.getXMLString(inDoc));
			
			Element orderLine = (Element)XPathUtil.getXpathNode(inDoc, "/Order/OrderLines/OrderLine");
			String originalSalesOrderHeaderKey = orderLine.getAttribute("DerivedFromOrderHeaderKey");
			
			log.verbose("------------------documentType-----------------"+documentType);
						
				if(documentType.equalsIgnoreCase("0003")){		
					
					boolean MRLFeePresentInXml = false;
					
					NodeList headerChargeList = inDoc.getDocumentElement().getElementsByTagName("HeaderCharge");
					
					for(int i=0; i<headerChargeList.getLength(); i++){
						
						Element headerCharge = (Element)headerChargeList.item(i);
						String chargeCategory = headerCharge.getAttribute("ChargeCategory");
						
						if(chargeCategory.equalsIgnoreCase("MRLFee")){
							MRLFeePresentInXml = true;
						}
					}
					
					
					
				if(MRLFeePresentInXml == false){
					
					
					Element outEle = (Element)inDoc.getDocumentElement().getElementsByTagName("CustomAttributes").item(0);
					
					boolean AddMRLFee = true;
					
					
					if(outEle != null){
						
						String isMRLFeeWaived = outEle.getAttribute("IsMRLFeeWaived");

						if(isMRLFeeWaived != null){
							
							if(isMRLFeeWaived.equalsIgnoreCase("Y")){

								AddMRLFee = false;
								}
							
						}
					}
					
					
					
					
					if(AddMRLFee == true){		
						
						
						Document orderDoc = XMLUtil.createDocument("Order");
						Element orderEle2 = orderDoc.getDocumentElement();
						orderEle2.setAttribute("OrderHeaderKey", originalSalesOrderHeaderKey);

						
						log.verbose("--------------FANYFSBeforeCreateOrderUEImpl------2---"
									+XMLUtil.getXMLString(orderDoc));
						
						Document outDoc = CommonUtil.invokeAPI
								(env,"global/template/api/getOrderList_payment.xml","getOrderList", orderDoc);
						
						log.verbose("--------------FANYFSBeforeCreateOrderUEImpl------3---"
								+XMLUtil.getXMLString(outDoc));
						
						Element outEle2 = (Element)outDoc.getDocumentElement().getElementsByTagName("CustomAttributes").item(0);
						
						if(outEle2 != null){
							
							String isMRLFeeWaived2 = outEle2.getAttribute("IsMRLFeeWaived");
							
							if(isMRLFeeWaived2 != null){
								
								if(isMRLFeeWaived2.equalsIgnoreCase("Y")){
									AddMRLFee = false;
									}
								
							}
						}
						
						
					}
					
					
						
						if(AddMRLFee == true){
							
							Document commonCodeDoc = XMLUtil.createDocument("CommonCode");
							Element ordercommonCodeEle = commonCodeDoc.getDocumentElement();
							ordercommonCodeEle.setAttribute("CodeValue", "MRLFeeRate");

							
							log.verbose("--------------FANYFSBeforeCreateOrderUEImpl------4--"
										+XMLUtil.getXMLString(commonCodeDoc));
							
							Document outDoc3 = CommonUtil.invokeAPI
									(env,"getCommonCodeList", commonCodeDoc);
							
							log.verbose("--------------FANYFSBeforeCreateOrderUEImpl------5---"
									+XMLUtil.getXMLString(outDoc3));
							
							Element outEle3 = (Element)outDoc3.getDocumentElement().getElementsByTagName("CommonCode").item(0);
							
							String codeShortDescription = outEle3.getAttribute("CodeShortDescription");
						      
							String MRLFeeRate = codeShortDescription;
							

							
							Element headerCharges = inDoc.createElement("HeaderCharges");
							Element headerCharge = inDoc.createElement("HeaderCharge");
							
							headerCharge.setAttribute("ChargeAmount", MRLFeeRate);
							headerCharge.setAttribute("ChargeCategory", "MRLFee");
							headerCharge.setAttribute("ChargeName", "MRLFee");
							
							
							
							Element headerChargesOld = (Element) inDoc.getElementsByTagName("HeaderCharges").item(0);
							
							if(headerChargesOld == null){
								log.verbose("------------------------------1--------------------------------------");
								headerCharges.appendChild(headerCharge);
								inDoc.getDocumentElement().appendChild(headerCharges);
							}else{
								log.verbose("------------------------------2--------------------------------------");
								headerChargesOld.appendChild(headerCharge);
							}
							
						}


				}
								
						
			}
				
			log.verbose("--------------FANYFSBeforeCreateOrderUEImpl------10--"
						+XMLUtil.getXMLString(inDoc));
			
			return inDoc;
		}

		
		
	public String calcDate(String expectedDate, String transitDays) throws ParseException {

		int nonBusinessDays = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat(CreateOrderConstants.SIMPLE_DATE_FORMAT);
		Calendar c = Calendar.getInstance();
		c.setTime(sdf.parse(expectedDate));
		
		log.verbose("Initial Date--------> " + sdf.format(c.getTime()));
		log.verbose("transitDays--------> " + transitDays);
		
		//Check if any dates between actual date + transit are weekends
		nonBusinessDays = calcNonBusiness(expectedDate,transitDays);
		log.verbose("nonBusinessDays--------> " + nonBusinessDays);
		c.add(Calendar.DATE, nonBusinessDays+Integer.parseInt(transitDays)); 
		log.verbose("Adjusted Date1--------> " + sdf.format(c.getTime()));
		
		//Check if new date is weekend and adjust to only business days
 		nonBusinessDays = calcWeekends(c);
 		log.verbose("nonBusinessDays--------> " + nonBusinessDays);

 		c.add(Calendar.DATE,  nonBusinessDays); 
		
		String Delivery = sdf.format(c.getTime());
		
		log.verbose("Adjusted Date2--------> " + sdf.format(c.getTime()));
		return Delivery;

	}
	
	public int calcNonBusiness(String expectedDate, String transitDays) throws ParseException  {
 
 		int nonBusinessDays = 0;
 		
 		SimpleDateFormat sdf = new SimpleDateFormat(CreateOrderConstants.SIMPLE_DATE_FORMAT);
		Calendar c = Calendar.getInstance();
		c.setTime(sdf.parse(expectedDate));
 		
 		for(int i=0; i<Integer.parseInt(transitDays); i++){
			c.add(Calendar.DATE, 1); 
			
			int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			
			if(dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY){
				 nonBusinessDays = nonBusinessDays + 1;
			 }
		}
 		
 		return nonBusinessDays;
 	}
	
	public int calcWeekends(Calendar cal)  {
 
 		int nonBusinessDays = 0;
 		
 		if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
 			nonBusinessDays = 2; 
 		}
 		
 		return nonBusinessDays;
 	}

	public void changeElements(Element EleToBeChanged, String deliveryDate, String dateTypeId) {

		EleToBeChanged.setAttribute(CreateOrderConstants.ATT_COMMITTED_DATE, deliveryDate);
		EleToBeChanged.setAttribute(CreateOrderConstants.ATT_DATE_TYPE_ID, dateTypeId);

	}
	
	public String getSimpleSysDate(){
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		return dateFormat.format(date);
		
	}
	
	@Override
	public void setProperties(Properties arg0) throws Exception {
		
		props = arg0;
	}
	
	protected String getProperty(String name) {

		return props.getProperty(name);
	}

}
