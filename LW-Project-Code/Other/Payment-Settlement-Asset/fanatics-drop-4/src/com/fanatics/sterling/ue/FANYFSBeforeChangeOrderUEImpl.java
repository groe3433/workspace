package com.fanatics.sterling.ue;

/**
 * 
 * This class provides the Use Exit implementation for the BeforeChangeOrderUE 
 * 
 * @(#) FANYFSBeforeChangeOrderUEImpl.java Created on June 1, 2016 11:30:22 AM
 * 
 *      Package Declaration: File Name: FANYFSBeforeChangeOrderUEImpl.java
 *      Package Name: com.fanatics.sterling.ue Project name: Fanatics Type
 *      Declaration: Class Name: FANYFSBeforeChangeOrderUEImpl Type Comment:
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

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fanatics.sterling.constants.CustomerMasterConstants;
import com.fanatics.sterling.constants.OrderNotesConstants;
import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.FANDBUtil;
import com.fanatics.sterling.util.XMLUtil;
import com.fanatics.sterling.util.XPathUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSBeforeChangeOrderUE;

public class FANYFSBeforeChangeOrderUEImpl implements YFSBeforeChangeOrderUE{
	
	private static YFCLogCategory logger = YFCLogCategory.instance("com.yantra.yfc.log.YFCLogCategory");
	
	@Override
	public Document beforeChangeOrder(YFSEnvironment env, Document inDoc) throws YFSUserExitException {
		
		logger.verbose("FANYFSBeforeChangeOrderUEImpl  -> Begin  ");
		
		Document outDoc = null;
		
		try {
			String sDocType = XPathUtil.getAttribute(inDoc.getDocumentElement(), "DocumentType");
			logger.verbose("FANYFSBeforeChangeOrderUEImpl  -> DocumentType "+sDocType);
			String sOrderLineKey = XPathUtil.getXpathAttribute(inDoc, "//Order/OrderLines/OrderLine/@DerivedFromOrderLineKey");
			logger.verbose("FANYFSBeforeChangeOrderUEImpl  -> OrderLineKey "+sOrderLineKey);

			if(!XMLUtil.isVoid(sDocType)){
				if(!XMLUtil.isVoid(sOrderLineKey) && sDocType.compareTo("0003")==0){
					Document getCompleteOrderLineListinDoc = XMLUtil.createDocument("OrderLine");
					XMLUtil.setAttribute(getCompleteOrderLineListinDoc.getDocumentElement(), "OrderLineKey", sOrderLineKey);
					Document getCompleteOrderLineList = XMLUtil.getDocument("<OrderLineList><OrderLine OrderHeaderKey=\"\" OrderLineKey=\"\"><Order BillToKey=\"\" ShipToKey=\"\"/></OrderLine></OrderLineList>");
					Document getCompleteOrderLineListoutDoc = CommonUtil.invokeAPI(env,getCompleteOrderLineList,"getCompleteOrderLineList", getCompleteOrderLineListinDoc);
					logger.verbose("FANYFSBeforeChangeOrderUEImpl  -> getCompleteOrderLineListoutDoc "+XMLUtil.getXMLString(getCompleteOrderLineListoutDoc));
					String sBillToKey = XPathUtil.getXpathAttribute(getCompleteOrderLineListoutDoc, "//OrderLineList/OrderLine/Order/@BillToKey");
					String sShipToKey = XPathUtil.getXpathAttribute(getCompleteOrderLineListoutDoc, "//OrderLineList/OrderLine/Order/@ShipToKey");
					XMLUtil.setAttribute(inDoc.getDocumentElement(), "BillToKey", sBillToKey);
					XMLUtil.setAttribute(inDoc.getDocumentElement(), "ShipToKey", sShipToKey);
				}
			}
			
			logger.verbose("FANYFSBeforeChangeOrderUEImpl  -> Fetching current DB time  ");
			ArrayList<Object[]> alTimeStamp = FANDBUtil.getDBResult(env, OrderNotesConstants.SQL_GET_DBTIME, 1);
			
			Object[] resultRow 			= alTimeStamp.get(0);
			String strCurrentDBDateTime = (String) resultRow[0]; 
			
			//Format the date to correct format
			//strCurrentDBDateTime = FANDateUtils.formatDate("yyyy-MM-dd kk:mm:SS", "MMMMM d, yyyy h:mm a", strCurrentDBDateTime);
			
			logger.verbose("FANYFSBeforeChangeOrderUEImpl current DB time is "+ strCurrentDBDateTime);
			
			// get the current user from the environment
			String strUserID 		= env.getUserId();
			Element eleOrder 		= inDoc.getDocumentElement();
			
			logger.verbose("FANYFSBeforeChangeOrderUEImpl -> Environement user found : "+ strUserID);
			
		//Begin shipping Charges for LOS change
			//Check if UpdatedShippingCharge is in the inDoc
		
			Element eleInOrder = inDoc.getDocumentElement();
			String CarrierServiceCode = SCXmlUtil.getXpathAttribute(eleInOrder, "//Order/OrderLines/OrderLine/@CarrierServiceCode");
			if(!(CarrierServiceCode.isEmpty()) && CarrierServiceCode!= null) {
				logger.verbose("if (!CarrierServiceCode.equals(null) || !CarrierServiceCode.equals");
				logger.verbose("CarrierServiceCode: " +CarrierServiceCode);

			String updatedShippingCharge = getUpdatedShippingCharge(env,CarrierServiceCode,inDoc);
			
				
			if(!(updatedShippingCharge.isEmpty()) && updatedShippingCharge!= null) {
					logger.verbose("inside IF");
					
					String newLOSPrice = getNewLOSPrice(env, inDoc,updatedShippingCharge);
					
					Element headerChargesEle = inDoc.createElement("HeaderCharges");
					Element headerChargeEle = inDoc.createElement("HeaderCharge");
					headerChargeEle.setAttribute("ChargeAmount", newLOSPrice);
					headerChargeEle.setAttribute("ChargeCategory", "Shipping");
					headerChargeEle.setAttribute("ChargeName", "Shipping");

					headerChargesEle.appendChild(headerChargeEle);
					eleInOrder.appendChild(headerChargesEle);
					eleInOrder.setAttribute("CarrierServiceCode", CarrierServiceCode);
					logger.verbose("Shipping Charge LOS change update doc: "+XMLUtil.getXMLString(inDoc));
				
			}
			}
			
			//End shipping Charges for LOS change
			
			String fraudServiceUser = SCXmlUtil.getXpathAttribute(eleOrder, OrderNotesConstants.XPATH_FRAUD_STATUS_USER);
			
			/*
			 * COQ-149 if fraud check resolves a hold, the fraud service name has to be the user instead of the environment one.
			 */
			
			if(!(fraudServiceUser.isEmpty()) && fraudServiceUser!= null) {
				logger.verbose("fraud user found, replacing environment user with fraud service user : " + fraudServiceUser);
				strUserID = fraudServiceUser;
			}else{
				logger.verbose("FANYFSBeforeChangeOrderUEImpl -> Fraud user not found, defaulting to environment user");
			}
			
			eleOrder.setAttribute(OrderNotesConstants.CURRENT_DB_DATE_TIME, strCurrentDBDateTime);
			eleOrder.setAttribute(OrderNotesConstants.USER_ID, strUserID);
			
			Document getServerListInput  = SCXmlUtil.createDocument(OrderNotesConstants.EL_SERVERS);
			getServerListInput.getDocumentElement().setAttribute(OrderNotesConstants.ATT_TYPE, OrderNotesConstants.INTEG_SERVER);
			
			logger.verbose("FANYFSBeforeChangeOrderUEImpl -> Invoking " + OrderNotesConstants.API_GET_SERVER_LIST 
					+ "API");
			
			Document getServerListOutDoc = CommonUtil.invokeAPI(env, OrderNotesConstants.API_GET_SERVER_LIST, getServerListInput);
			
			Element rootMessageNode = getServerListOutDoc.getDocumentElement();
			
			/*
			 * In case an Agent resolves/applies a hold and calls changeOrder, FANYFSBeforeChangeOrderUEImpl stamps the User SYSTEM
			 * instead of the agent name (example: FanaticsAdjustInvFullSyncServer) COQ-147. It should not replace Fraud user and it will
			 * stamp SYSTEM only when the fraud check user does not exist.
			 */
			
			if (rootMessageNode != null) {				
				NodeList serverLines = rootMessageNode
						.getElementsByTagName(OrderNotesConstants.EL_SERVER);	
				if (serverLines != null
						&& serverLines.getLength() > 0) {
					for (int i = 0; i < serverLines.getLength(); i++) {
						
						Element eleNode = (Element)serverLines.item(i);
						String name 	= eleNode.getAttribute(OrderNotesConstants.ATT_NAME);
						String status 	= eleNode.getAttribute(OrderNotesConstants.ATT_STATUS);
						
						if(name.equalsIgnoreCase(strUserID) && status.equalsIgnoreCase(OrderNotesConstants.ACTIVE)){
							
							logger.verbose("Active AgentServer found in the Server List, changing user name to SYSTEM");
							eleOrder.setAttribute(OrderNotesConstants.USER_ID, OrderNotesConstants.SYSTEM);
						}
								
					}
				}
			}
			
			/*
			 * COQ-152 if similar person details passed, the xsl template creates invalid notes, in this case the 
			 * below logic determines whether there was a change or not and if yes then it sets a flag to let the
			 * template create the notes. 
			 */
			
			eleOrder.setAttribute(OrderNotesConstants.CREATE_NOTE, OrderNotesConstants.YES);
			
			if(elementExists(inDoc, OrderNotesConstants.EL_PERSON_INFO_SHIP_TO) && elementExists(inDoc, OrderNotesConstants.EL_PERSON_INFO_BILL_TO) && !checkAddressChange(inDoc, env)){
				eleOrder.setAttribute(OrderNotesConstants.CREATE_NOTE, OrderNotesConstants.NO);
			}
				
			logger.verbose("FANYFSBeforeChangeOrderUEImpl -> Invoking " + OrderNotesConstants.SERVICE_FAN_CHANGE_NOTES 
					+ "service");

			logger.verbose("inputXML -> " + XMLUtil.getXMLString(inDoc));
			
			try{
				
				outDoc = CommonUtil.invokeService(env, OrderNotesConstants.SERVICE_FAN_CHANGE_NOTES, inDoc); 
				
			}catch(Exception e){
				
				outDoc = inDoc;
				logger.error("FANYFSBeforeChangeOrderUEImpl 1 --> ERROR: " + e.getMessage(), e.getCause());
			}
			
			try{
				
				outDoc = waiveMRLFeeIfRequired(env, outDoc);
				
			}catch(Exception e){

				logger.error("FANYFSBeforeChangeOrderUEImpl 2 --> ERROR: " + e.getMessage(), e.getCause());
			}

			try{
				
				outDoc = checkIfPaymentProcessingRequiredOnReturnOrder(env,outDoc);
				
			}catch(Exception e){
				
				logger.error("FANYFSBeforeChangeOrderUEImpl 3 --> ERROR: " + e.getMessage(), e.getCause());
			}
			
			try{
			//Customer Update - Address Validation Change Begin
				
			//Check if PersonInfoShipTo is in the inDoc
		    NodeList shipToNodeList = inDoc.getElementsByTagName("PersonInfoShipTo");
		    boolean isShipToList = shipToNodeList.getLength() != 0 ? true : false;
			logger.verbose("isShipToList -> " + isShipToList);
			
			
			//Check if PersonInfoBillTo is in the inDoc
		    NodeList billToNodeList = inDoc.getElementsByTagName("PersonInfoBillTo");
		    boolean isBillToList = billToNodeList.getLength() != 0 ? true : false;
			logger.verbose("isBillToList -> " + isBillToList);
			
			
			if (isBillToList || isShipToList){
				logger.verbose("inside IF");
			checkShippingChange(env, inDoc,isShipToList,isBillToList);
			}
			
			}catch(Exception e){
				
				logger.error("FANYFSBeforeChangeOrderUEImpl 4 --> ERROR: " + e.getMessage(), e.getCause());
			}
			
		} catch (Exception e) {
			logger.error("FANYFSBeforeChangeOrderUEImpl 5 --> ERROR: " + e.getMessage(), e.getCause());
		}
		
		return outDoc == null ? inDoc : outDoc;
	}

	

private String getUpdatedShippingCharge (YFSEnvironment env,String CarrierServiceCode,Document inDoc) throws Exception {
		logger.verbose("--------------FANYFSBeforeChangeOrderUEImpl------getUpdatedShippingCharge entry----");
		logger.verbose("---------getCarrierServiceOptionsForOrdering--inDoc--" +XMLUtil.getXMLString(inDoc));

		Element inDocele = inDoc.getDocumentElement();
		String OHK = XPathUtil.getXpathAttribute(inDocele, "//Order/@OrderHeaderKey");
		logger.verbose("---------OrderHeaderKey: " +OHK);
		
		Document orderDoc = XMLUtil.createDocument("Order");
		Element orderEle2 = orderDoc.getDocumentElement();


		orderEle2.setAttribute("DisplayLocalizedFieldInLocale", "en_US_EST");
		orderEle2.setAttribute("IgnoreOrdering", "Y");
		orderEle2.setAttribute("OrderHeaderKey", OHK);
		logger.verbose("---------getCarrierServiceOptionsForOrdering--orderDoc--" +XMLUtil.getXMLString(orderDoc));
		
		Document outDoc = CommonUtil.invokeAPI(env,"getCarrierServiceOptionsForOrdering", orderDoc);
		Element orderEle = outDoc.getDocumentElement();

		String newLOSPrice = XPathUtil.getXpathAttribute(orderEle, "//Order/OrderLines/OrderLine[@PrimeLineNo='1']/CarrierServiceList/CarrierService[@CarrierServiceCode='"+CarrierServiceCode+"']/@Price");
		logger.verbose("--------------getUpdatedShippingCharge-----newLOSPrice is: " +newLOSPrice);

		return newLOSPrice;
		
		
	}

	private String getNewLOSPrice (YFSEnvironment env,Document inDoc,String newLOSPrice) throws Exception {
		
		logger.verbose("--------------FANYFSBeforeChangeOrderUEImpl------getNewLOSPrice entry----");
		float returnedLOSPrice =0.00f;
		Element eleInOrder = inDoc.getDocumentElement();

		float fnewLOSPrice = Float.parseFloat(newLOSPrice);

		//Call getOrderList API to get original address
		Document orderDoc = XMLUtil.createDocument("Order");
		Element orderEle2 = orderDoc.getDocumentElement();
		orderEle2.setAttribute("OrderHeaderKey", XPathUtil.getXpathAttribute(eleInOrder, "//Order/@OrderHeaderKey"));
		logger.verbose("--------------FANYFSBeforeChangeOrderUEImpl------getOrderList--checkShippingChange--Input--"
				+XMLUtil.getXMLString(orderDoc));
		
		Document opGOLDoc = CommonUtil.invokeAPI(env,"global/template/api/getOrderList_shippingCharge.xml","getOrderList", orderDoc);

		logger.verbose("--------------FANYFSBeforeChangeOrderUEImpl------getOrderList--checkShippingChange--opGOLDoc--"
				+XMLUtil.getXMLString(opGOLDoc));
		Element eleOutOrder = opGOLDoc.getDocumentElement();

		String oldLOSPrice = XPathUtil.getXpathAttribute(eleOutOrder, "//OrderList/Order/HeaderCharges/HeaderCharge[@ChargeName='Shipping']/@ChargeAmount");
		float foldLOSPrice = Float.parseFloat(oldLOSPrice);
		returnedLOSPrice = foldLOSPrice;
		logger.verbose("New LOS Price: " +newLOSPrice);
		logger.verbose("Old LOS Price: " +oldLOSPrice);

		String maxOrderStatus = XPathUtil.getXpathAttribute(eleOutOrder, "//OrderList/Order/@MaxOrderStatus");
		float fmaxOrderStatus = Float.parseFloat(maxOrderStatus);
		boolean isShipped = false;
		logger.verbose("fmaxOrderStatus = " +fmaxOrderStatus);

		if (fmaxOrderStatus >= 3700){
			isShipped = true;
			logger.verbose("isShipped = true ");
		}
		
		if(XPathUtil.getXpathAttribute(eleInOrder, "//Order/CustomAttributes/@OverrideShippingCharge").equals("N")) {
		if(foldLOSPrice > fnewLOSPrice) {
			if(isShipped){
				logger.verbose("Old price is higher than new price and line is already shipped - do nothing");
			}else{
				returnedLOSPrice = fnewLOSPrice;
				logger.verbose("Old price is higher than new price and no Shipments -returnedLOSPrice: "+returnedLOSPrice);
			}
		}else{
		
				if(isShipped){
					float tempPrice = fnewLOSPrice - foldLOSPrice;
					returnedLOSPrice = tempPrice;
					logger.verbose("Old price is Lower than new price and there are Shipments -returnedLOSPrice: "+returnedLOSPrice);
				}else{
					returnedLOSPrice =fnewLOSPrice;
					logger.verbose("Old price is Lower than new price and no Shipments -returnedLOSPrice: "+returnedLOSPrice);
				}
					
			}
		}

		String returnPrice = Float.toString(returnedLOSPrice);
		logger.verbose("returning LOS price: " +returnPrice);
		return returnPrice;


	}

	private  void checkShippingChange(YFSEnvironment env,Document inDoc, boolean isShipToList,boolean isBillToList ) throws Exception {
		logger.verbose("--------------FANYFSBeforeChangeOrderUEImpl------checkShippingChange entry----");
		
		Element eleOrder = inDoc.getDocumentElement();
		String orderHeaderKey = eleOrder.getAttribute("OrderHeaderKey");
		
		boolean isShipToList1 = isShipToList;
		boolean isBillToList1 = isBillToList;
		
		int shipTochanges=0;
		int billToChanges=0;
		
		/*get attributes from constants to compare for differences.
		 *It will get a large String and split it into array on ","   
		 */
		String[] splitAttributeArray = CustomerMasterConstants.COMPARE_Array_Attribute.split(",");
		
		//Call getOrderList API to get original address
		Document orderDoc = XMLUtil.createDocument("Order");
		Element orderEle2 = orderDoc.getDocumentElement();
		orderEle2.setAttribute("OrderHeaderKey", orderHeaderKey);
		
		logger.verbose("--------------FANYFSBeforeChangeOrderUEImpl------getOrderList--checkShippingChange--Input--"
				+XMLUtil.getXMLString(orderDoc));
		
		Document opGOLDoc = CommonUtil.invokeAPI(env,"global/template/api/getOrderList_updateAddress.xml","getOrderList", orderDoc);
		Element getOLEle = opGOLDoc.getDocumentElement();

		
		logger.verbose("--------------FANYFSBeforeChangeOrderUEImpl------getOrderList--checkShippingChange--opGOLDoc--"
				+XMLUtil.getXMLString(opGOLDoc));
		
		//Logic to compare PersonInfoShipTo from inDoc to getOrderList Output
		if (isShipToList1){
			Element newShipToElem = (Element) eleOrder.getElementsByTagName(CustomerMasterConstants.ELE_PersonInfoShipTo).item(0);
			Element PrevShipToElem = (Element) getOLEle.getElementsByTagName(CustomerMasterConstants.ELE_PersonInfoShipTo).item(0);
 
			for (int i=0; i < splitAttributeArray.length; i++){
				logger.verbose("isShipToList - While loop - number: "+i +" Checking Attribute: "+ splitAttributeArray[i]);
				
				String newValue = newShipToElem.getAttribute(splitAttributeArray[i]);
				String prevValue = PrevShipToElem.getAttribute(splitAttributeArray[i]);				
				logger.verbose("Compare newValue: "+newValue+ " with prevValue: "+prevValue);
				
				if (!newValue.equals(prevValue)){
					logger.verbose("Address Mismatch!!");
					shipTochanges++;
					logger.verbose("--------------FANYFSBeforeChangeOrderUEImpl------checkShippingChange --shipTochanges" +shipTochanges);

									
				}
				
				 
			}
			
		}

		//Logic to compare PersonInfoBillTo from inDoc to getOrderList Output
		if (isBillToList1){
			int i =0;
			Element newBillToElem = (Element) eleOrder.getElementsByTagName(CustomerMasterConstants.ELE_PersonInfoBillTo).item(0);
			Element PrevBillToElem = (Element) getOLEle.getElementsByTagName(CustomerMasterConstants.ELE_PersonInfoBillTo).item(0);

			while (splitAttributeArray[i] != null){
				logger.verbose("isBillToList - While loop - num: "+i +" Checking Attribute: "+ splitAttributeArray[i]);
				
				String newValue = newBillToElem.getAttribute(splitAttributeArray[i]);
				String prevValue = PrevBillToElem.getAttribute(splitAttributeArray[i]);				
				logger.verbose("Compare newValue: "+newValue+ " with prevValue: "+prevValue);
				
				if (!newValue.equals(prevValue)){
					logger.verbose("Address Mismatch!!");
					billToChanges++;
					logger.verbose("--------------FANYFSBeforeChangeOrderUEImpl------checkShippingChange --billToChanges" +billToChanges);

									
				}
				
				 i++;
			}
			
		}
		
		if (shipTochanges != 0) {
			logger.verbose("Total shipTochanges are: " +shipTochanges);
			String eleName = CustomerMasterConstants.ELE_PersonInfoShipTo;
			customerUpdate(env, inDoc,opGOLDoc, eleName);

		}
		
		if (billToChanges != 0) {
			logger.verbose("Total billToChanges are: " +billToChanges);
			String eleName = CustomerMasterConstants.ELE_PersonInfoBillTo;
			customerUpdate(env, inDoc,opGOLDoc, eleName);

		}
		
		
		
		logger.verbose("--------------FANYFSBeforeChangeOrderUEImpl------checkShippingChange exit----");
	}
	
	//Create document to be sent when address info is changed.
	private void customerUpdate(YFSEnvironment env, Document inDoc, Document opGOLDoc, String eleName) throws Exception {
		// TODO Auto-generated method stub
		logger.verbose("--------------FANYFSBeforeChangeOrderUEImpl------createCustUpdateDoc entry----");
		
		Document opRESTDoc = XMLUtil.getDocument("<Customer CustomerRewardsNo=\"\" OrganizationCode=\"\" CustomerID=\"\">"
				+ "	<CustomerContactList> <CustomerContact Company=\"\" EmailID=\"\" DayPhone=\"\" FirstName=\"\" LastName=\"\" MiddleName=\"\" MobilePhone=\"\">"
				+ "	<CustomerAdditionalAddressList>	<CustomerAdditionalAddress>	<PersonInfo AddressLine1=\"\" AddressLine2=\"\" "
				+ "AddressLine3=\"\" City=\"\" DayPhone=\"\" EveningPhone=\"\" MobilePhone=\"\" EmailID=\"\" "
				+ "PersonID=\"\" Company=\"\" FirstName=\"\" LastName=\"\" State=\"\" "
				+ "AddressID=\"\" ZipCode=\"\"> </PersonInfo> </CustomerAdditionalAddress> </CustomerAdditionalAddressList>"
				+ "</CustomerContact> </CustomerContactList> </Customer>	");
		logger.verbose("opRESTDoc -> " + XMLUtil.getXMLString(opRESTDoc));
		
		Document dipRESTDoc = XMLUtil.createDocument("Customer");
		dipRESTDoc.getDocumentElement().setAttribute("CustomerRewardsNo", "");//need this
		
		Element eleOrder = inDoc.getDocumentElement();
		Element eleinDocOrder = (Element) eleOrder.getElementsByTagName(eleName).item(0);

		
		Element eleGOLDoc = opGOLDoc.getDocumentElement();
		Element eleGOLOrder = (Element) eleGOLDoc.getElementsByTagName(CustomerMasterConstants.ELE_Order).item(0);
		
		
		Element eleCustomer = (Element) opRESTDoc.getElementsByTagName(CustomerMasterConstants.ELE_Customer).item(0);
		Element eleCustomerList = (Element) eleCustomer.getElementsByTagName(CustomerMasterConstants.ELE_CustomerContactList).item(0);
		Element eleCustomerContact = (Element) eleCustomerList.getElementsByTagName(CustomerMasterConstants.ELE_CustomerContact).item(0);
		Element eleCustomerAddAddress = (Element) eleCustomerContact.getElementsByTagName(CustomerMasterConstants.ELE_CustomerAdditionalAddress).item(0);
		Element elePersonInfo = (Element) eleCustomerAddAddress.getElementsByTagName(CustomerMasterConstants.ELE_PersonInfo).item(0);



		opRESTDoc.getDocumentElement().setAttribute("CustomerRewardsNo", eleGOLOrder.getAttribute("CustomerRewardsNo"));
		opRESTDoc.getDocumentElement().setAttribute("OrganizationCode", eleGOLOrder.getAttribute("EnterpriseCode"));
		opRESTDoc.getDocumentElement().setAttribute("CustomerID", eleGOLOrder.getAttribute("BillToID"));
		eleCustomerContact.setAttribute("Company", eleinDocOrder.getAttribute("Company"));
		eleCustomerContact.setAttribute("EmailID", eleinDocOrder.getAttribute("EMailID"));
		eleCustomerContact.setAttribute("DayPhone", eleinDocOrder.getAttribute("DayPhone"));
		eleCustomerContact.setAttribute("FirstName", eleinDocOrder.getAttribute("FirstName"));
		eleCustomerContact.setAttribute("LastName", eleinDocOrder.getAttribute("LastName"));
		//eleCustomerContact.setAttribute("MiddleName", eleinDocOrder.getAttribute("BillToID"));
		//eleCustomerContact.setAttribute("MobilePhone", eleinDocOrder.getAttribute("BillToID"));
		
		
		elePersonInfo.setAttribute("AddressLine1", eleinDocOrder.getAttribute("AddressLine1"));
		elePersonInfo.setAttribute("AddressLine2", eleinDocOrder.getAttribute("AddressLine2"));
		//elePersonInfo.setAttribute("AddressLine3", eleGOLOrder.getAttribute("BillToID"));
		elePersonInfo.setAttribute("City", eleinDocOrder.getAttribute("City"));
		elePersonInfo.setAttribute("DayPhone", eleinDocOrder.getAttribute("DayPhone"));
		elePersonInfo.setAttribute("EveningPhone", eleinDocOrder.getAttribute("EveningPhone"));
		//elePersonInfo.setAttribute("MobilePhone", eleGOLOrder.getAttribute("BillToID"));
		elePersonInfo.setAttribute("EmailID", eleinDocOrder.getAttribute("EMailID"));
		elePersonInfo.setAttribute("PersonID", eleinDocOrder.getAttribute("PersonInfoKey"));
		elePersonInfo.setAttribute("Company", eleinDocOrder.getAttribute("Company"));
		elePersonInfo.setAttribute("FirstName", eleinDocOrder.getAttribute("FirstName"));
		elePersonInfo.setAttribute("LastName", eleinDocOrder.getAttribute("LastName"));
		elePersonInfo.setAttribute("State", eleinDocOrder.getAttribute("State"));
		elePersonInfo.setAttribute("AddressID", eleinDocOrder.getAttribute("AddressID"));
		elePersonInfo.setAttribute("ZipCode", eleinDocOrder.getAttribute("ZipCode"));


		logger.verbose("Input to REST call: "
				+XMLUtil.getXMLString(opRESTDoc));
		
		CommonUtil.invokeService(env, CustomerMasterConstants.SERVICE_FanaticsCustomerUpdateREST, opRESTDoc);
		logger.verbose("--------------FANYFSBeforeChangeOrderUEImpl------createCustUpdateDoc exit----");
	}


	
	// method added to check if refund is to be given to sales order or return order
	
	
	private Document checkIfPaymentProcessingRequiredOnReturnOrder(
			YFSEnvironment env, Document inDoc) throws Exception {
		


		logger.verbose("--------FANYFSBeforeChangeOrderUEImpl-------checkIfPaymentProcessingRequiredOnReturnOrder----start--"+XMLUtil.getXMLString(inDoc));
		
		Element orderEle = inDoc.getDocumentElement();
		
		String documentType = orderEle.getAttribute("DocumentType");

		
		
		if(documentType != null){
			
			if(documentType.equalsIgnoreCase("0003")){
				
				String processPaymentOnReturnOrder = orderEle.getAttribute("ProcessPaymentOnReturnOrder");
				
				boolean giftFlagBool = false;
				
				if(processPaymentOnReturnOrder != null){
					
					if(processPaymentOnReturnOrder.equalsIgnoreCase("Y")){
						
						String orderHeaderKey = orderEle.getAttribute("OrderHeaderKey");
						
						Document orderDoc = XMLUtil.createDocument("Order");
						Element orderEle2 = orderDoc.getDocumentElement();
						orderEle2.setAttribute("OrderHeaderKey", orderHeaderKey);
						
						logger.verbose("-------FANYFSBeforeChangeOrderUEImpl---checkIfPaymentProcessingRequiredOnReturnOrder---getOrderList--ip--"
								+XMLUtil.getXMLString(orderDoc));
					
						Document outDoc = CommonUtil.invokeAPI
							(env,"global/template/api/getOrderList_payment.xml","getOrderList", orderDoc);
					
						logger.verbose("-------FANYFSBeforeChangeOrderUEImpl---checkIfPaymentProcessingRequiredOnReturnOrder---getOrderList--op--"
							+XMLUtil.getXMLString(outDoc));
						
						NodeList orderLineList = outDoc.getElementsByTagName("OrderLine");
						
						// checking for gift flag in the output of getOrderList
						for(int i=0; i<orderLineList.getLength(); i++){
							
							Element orderLine = (Element)orderLineList.item(i);
							
							String giftFlag = orderLine.getAttribute("GiftFlag");
							
							if(giftFlag != null){
								
								if(giftFlag.equalsIgnoreCase("Y")){
									giftFlagBool = true;
									logger.verbose("---------giftFlagBool----0----"+giftFlagBool);
								}
								
							}
							
						}
						
						
						// checking for gift flag in the input document
						NodeList orderLineList2 = orderEle.getElementsByTagName("OrderLine");
						
						for(int i=0; i<orderLineList2.getLength(); i++){
							
							Element orderLine = (Element)orderLineList2.item(i);
							
							String giftFlag = orderLine.getAttribute("GiftFlag");
							
							if(giftFlag != null){
								
								if(giftFlag.equalsIgnoreCase("Y")){
									giftFlagBool = true;
									logger.verbose("---------giftFlagBool----1----"+giftFlagBool);
								}
								
							}
							
						}
						
						logger.verbose("---------giftFlagBool----2----"+giftFlagBool);
						
						if(giftFlagBool == false){
							inDoc.getDocumentElement().setAttribute("ProcessPaymentOnReturnOrder", "N");
							inDoc.getDocumentElement().setAttribute("ReturnByGiftRecipient", "N");
						}
						
					}
					
				}
				
				
				
				
			}
			
		}
		
		
		
			
		logger.verbose("--------checkIfPaymentProcessingRequiredOnReturnOrder-------checkIfPaymentProcessingRequiredOnReturnOrder----end--"
		+XMLUtil.getXMLString(inDoc));
		
		return inDoc;
	
		
	}
	
	// setting MRL Fee zero if it is waived
	
	private Document waiveMRLFeeIfRequired(YFSEnvironment env,
			Document inputDoc) throws Exception {
		


		logger.verbose("--------------FANYFSBeforeChangeOrderUEImpl------waiveMRLFeeIfRequired entry----"
				+XMLUtil.getXMLString(inputDoc));

		Element eleOrder = inputDoc.getDocumentElement();
		String documentType = eleOrder.getAttribute("DocumentType");		

		
		if(documentType.equalsIgnoreCase("0003")){
			
			String orderHeaderKey = eleOrder.getAttribute("OrderHeaderKey");

			
			Element customAttributes = (Element)inputDoc.getElementsByTagName("CustomAttributes").item(0);
			
			if(customAttributes != null){
				
				String isMRLFeeWaived = customAttributes.getAttribute("IsMRLFeeWaived");
				
				if(isMRLFeeWaived != null){
					
					if(isMRLFeeWaived.equalsIgnoreCase("Y")){
						
						eleOrder.setAttribute("Override", "Y");
						eleOrder.setAttribute("Action", "MODIFY");
						
						String MRLFeeWaiverReason = customAttributes.getAttribute("MRLFeeWaiverReason");
						
						Document orderDoc = XMLUtil.createDocument("Order");
						Element orderEle2 = orderDoc.getDocumentElement();
						orderEle2.setAttribute("OrderHeaderKey", orderHeaderKey);
						
						logger.verbose("--------------FANYFSBeforeChangeOrderUEImpl------getOrderList--ip--"
								+XMLUtil.getXMLString(orderDoc));
					
					Document outDoc = CommonUtil.invokeAPI
							(env,"global/template/api/getOrderList_payment.xml","getOrderList", orderDoc);
					
					logger.verbose("--------------FANYFSBeforeChangeOrderUEImpl------getOrderList--op--"
							+XMLUtil.getXMLString(outDoc));
					
					NodeList headerChargeList = outDoc.getDocumentElement().getElementsByTagName("HeaderCharge");
					
					if(headerChargeList.getLength() > 0){
						
						for(int i=0; i<headerChargeList.getLength(); i++){
							
							Element headerCharge = (Element)headerChargeList.item(i);
							String chargeCategory = headerCharge.getAttribute("ChargeCategory");
							
							if(chargeCategory.equalsIgnoreCase("MRLFee")){
								
								Element headerChargesEle = inputDoc.createElement("HeaderCharges");
								Element headerChargeEle = inputDoc.createElement("HeaderCharge");
								
								headerChargeEle.setAttribute("ChargeCategory", "MRLFee");
								headerChargeEle.setAttribute("ChargeName", "MRLFee");
								headerChargeEle.setAttribute("ChargeAmount", "0.0");
								headerChargeEle.setAttribute("RemainingChargeAmount", "0.0");
								
								headerChargesEle.appendChild(headerChargeEle);
								inputDoc.getDocumentElement().appendChild(headerChargesEle);
								
								Element notes = inputDoc.createElement("Notes");
								Element note = inputDoc.createElement("Note");
								
								note.setAttribute("NoteText", "CSR waived MRL Fee, Reason : " + MRLFeeWaiverReason);
								note.setAttribute("Tranid", "changeOrder");
								note.setAttribute("VisibleToAll", "Y");
								
								notes.appendChild(note);
								inputDoc.getDocumentElement().appendChild(notes);
								
								}
							}
						}
					}
				}
			}
			
	
		}
		
		
		
		logger.verbose("--------------FANYFSBeforeChangeOrderUEImpl------waiveMRLFeeIfRequired exit----"
				+XMLUtil.getXMLString(inputDoc));
		
		return inputDoc;
		
	
		
	}

	public boolean checkAddressChange(Document inDoc, YFSEnvironment env){
		
		logger.verbose("FANYFSBeforeChangeOrderUEImpl.checkAddressChange --> Start ");
		
		try{
			
			Element rootElement   		= inDoc.getDocumentElement();
			Document getOrderListInput  = SCXmlUtil.createDocument(OrderNotesConstants.EL_ORDER_ROOT);
			String orderHeaderKey		= rootElement.getAttribute(OrderNotesConstants.ATT_ORDER_HEADER_KEY);
			
			Element order = getOrderListInput.getDocumentElement();
			order.setAttribute(OrderNotesConstants.ATT_ORDER_HEADER_KEY, orderHeaderKey);
			
			Document getOrderListOutput = CommonUtil.invokeAPI
					(env,OrderNotesConstants.GET_ORDER_LIST, getOrderListInput);
		
			Element rootElementCo 	   = getOrderListOutput.getDocumentElement();
			Element personInfoBillToGO = SCXmlUtil.getXpathElement(rootElement, OrderNotesConstants.XPATH_INFO_BILL_TO);
			Element personInfoShipToGO = SCXmlUtil.getXpathElement(rootElement, OrderNotesConstants.XPATH_INFO_SHIP_TO);
			
			Element personInfoBillToCO = SCXmlUtil.getXpathElement(rootElementCo, OrderNotesConstants.XPATH_ORDER_LIST_INFO_BILL_TO);
			Element personInfoShipToCO = SCXmlUtil.getXpathElement(rootElementCo, OrderNotesConstants.XPATH_ORDER_LIST_INFO_SHIP_TO);
		
			ArrayList<String> details  = new ArrayList<String>();
			details.add(OrderNotesConstants.ATT_ADDRESS_1);
			details.add(OrderNotesConstants.ATT_ADDRESS_2);
			details.add(OrderNotesConstants.ATT_CITY);
			details.add(OrderNotesConstants.ATT_STATE);
			details.add(OrderNotesConstants.ATT_ZIP_CODE);
			
			logger.verbose("FANYFSBeforeChangeOrderUEImpl.checkAddressChange --> comparing person information for possible changes");
			
			for( String detail : details){
				
				String billToTempGo = personInfoBillToGO.getAttribute(detail);
				String billToTempCo = personInfoBillToCO.getAttribute(detail);
				
				String shipToTempGo = personInfoShipToGO.getAttribute(detail);
				String shipToTempCo = personInfoShipToCO.getAttribute(detail);
				
				boolean compareBillTo = billToTempGo.equalsIgnoreCase(billToTempCo);
				boolean compareShipTo = shipToTempGo.equalsIgnoreCase(shipToTempCo);
				
				if(!compareBillTo && compareShipTo){SCXmlUtil.removeNode(personInfoShipToGO);}
				if(!compareShipTo && compareBillTo){SCXmlUtil.removeNode(personInfoBillToGO);}
				
				if(!(compareBillTo || compareShipTo)){
					
					logger.verbose("FANYFSBeforeChangeOrderUEImpl.checkAddressChange --> change in the person info details detected ");
					
					return true;
				}
			}
			
		}catch (Exception e) {
			logger.error("FANYFSBeforeChangeOrderUEImpl --> ERROR: checkAddressChange method " + e.getMessage(), e.getCause());
		}
		
		return false;		
	}
	
	public boolean elementExists(Document doc, String content) {

		NodeList nodeList = doc.getElementsByTagName(content);
		int length 		  = nodeList.getLength();
		return length == 0 ? false : true;
	}
	
}
