package com.fanatics.sterling.api;

/**
 * This class is invoked in 
 * â€¢	ON_CANCEL event of change Sales Order. 
 * â€¢	ON_CANCEL event of Schedule Order.
 * â€¢	ON_SUCCESS of Confirm Shipment transaction. 
 * â€¢	ON_SUCCESS of Receive transaction in Return Receipt
 * â€¢	ON_INVOICE_COLLECTION of Payment Collection in Reverse Logistics
 * 
 * It enriches the above Event XML and puts an entry in the EXTN_FAN_EMAIL Table.
 * And the messages in the Email table is picked by the Agent and generates different email templates
 * 
 * @(#) FanSendEmailTable.java    
 * Created on   May 19, 2016
 *              
 *
 * Package Declaration: 
 * File Name:       FanSendEmailTable.java
 * Package Name:    com.fanatics.sterling.api;
 * Project name:    Fanatics
 * Type Declaration:    
 * Class Name:      FanSendEmailTable
 * 
 * @author KNtagkas
 * @version 1.0
 * @history 
 * 	1.1 Jack Tyrrell - added logic to extract email id and add it to output xml
 *     
 * 
 * (C) Copyright 2016-2017 by owner.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information
 * of the owner. ("Confidential Information").
 * Redistribution of the source code or binary form is not permitted
 * without prior authorisation from the owner.
 *
 */
import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fanatics.sterling.constants.CreateOrderConstants;
import com.fanatics.sterling.constants.EmailComunicationConstants;
import com.fanatics.sterling.constants.FanaticsFraudCheckConstants;
import com.fanatics.sterling.constants.ManageFanCashConstants;
import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.FANConstants;
import com.fanatics.sterling.util.XMLUtil;
import com.fanatics.sterling.util.XPathUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

@SuppressWarnings("unused")
public class FanSendEmailTable implements YIFCustomApi {

	private static YFCLogCategory logger = YFCLogCategory.instance(YFCLogCategory.class);
	private Properties props;

	@Override
	public void setProperties(Properties arg0) throws Exception {
		props = arg0;
	}

	public void execute(YFSEnvironment env, Document inDoc) {

		logger.verbose("IN METHOD");
		logger.verbose("FanSendEmailTable -> Begin");

		try {
			
			inDoc = callAudits(env, inDoc);

			Document outputDoc 		= SCXmlUtil.createDocument(EmailComunicationConstants.EL_EXTN_FAN_EMAIL);
			Document outputEmailDoc = SCXmlUtil.createDocument(EmailComunicationConstants.EL_EMAIL);
			Element outRootElement 	= outputDoc.getDocumentElement();
			Element outRootEmailElement 	= outputEmailDoc.getDocumentElement();
			Element rootElement		= inDoc.getDocumentElement();
			String enterpriseCode 	= rootElement.getAttribute(EmailComunicationConstants.ATT_ENTERPRISE_CODE);
			
			outRootElement.setAttribute(EmailComunicationConstants.ATT_ORGANIZATION_CODE, enterpriseCode);
			outRootElement.setAttribute(EmailComunicationConstants.ATT_EMAIL_STATUS,
					EmailComunicationConstants.EMAIL_STATUS_INIT);

			String rootElementName = inDoc.getDocumentElement().getTagName();

			switch (rootElementName) {

			case EmailComunicationConstants.EL_ORDER_ROOT: {

				logger.verbose("FanSendEmailTable -> Order root element found, running scenario 1");
				String orderNo	 = rootElement.getAttribute(EmailComunicationConstants.EL_ORDER_NO);
				String orderHKey = rootElement.getAttribute(EmailComunicationConstants.EL_ORDER_HEADER_KEY);
				String emailID = getEmailAddress(env, orderHKey, EmailComunicationConstants.XPATH_FAN_BILL_EMAIL, false);
				setAttributes(outRootElement, orderHKey, orderNo, EmailComunicationConstants.SCHED_ORDER_CANCEL, emailID, EmailComunicationConstants.EMPTY_STRING, EmailComunicationConstants.EMPTY_STRING);
				
				outRootEmailElement.setAttribute(EmailComunicationConstants.ATT_EVENT_ID, EmailComunicationConstants.EVNT_ORDER_CANCEL);
				outRootEmailElement.setAttribute(EmailComunicationConstants.ATT_EVENT_DESC, EmailComunicationConstants.DESC_ORDER_CANCELLATION);
				outRootEmailElement.setAttribute(EmailComunicationConstants.ATT_EMAIL_ID, emailID);
				
				boolean hasFanCash = true;

				if (ElementExists(inDoc, EmailComunicationConstants.EL_ORDER_AUDIT)) {
					logger.verbose("FanSendEmailTable -> Change Order On Cancel Eventg detected, updating Email type");
					outputDoc.getDocumentElement().setAttribute(EmailComunicationConstants.EL_EMAIL_TYPE,
							EmailComunicationConstants.CHANGE_ORDER_CANCEL);
				}
				if (ElementExists(inDoc, EmailComunicationConstants.EL_INVOICE_COLLECTION)) {
					logger.verbose(
							"FanSendEmailTable -> Payment Collection On invoice Collection Event detected, updating Email type");
					outputDoc.getDocumentElement().setAttribute(EmailComunicationConstants.EL_EMAIL_TYPE,
							EmailComunicationConstants.PAYMENT_COL_ON_INV);
					
					outRootEmailElement.setAttribute(EmailComunicationConstants.ATT_EVENT_ID, EmailComunicationConstants.EVNT_RETURN_ORDER);
					outRootEmailElement.setAttribute(EmailComunicationConstants.ATT_EVENT_DESC, EmailComunicationConstants.DESC_RETURN_ORDER_REFUND);
					
					hasFanCash = false;
				}
				if (ElementExists(inDoc, EmailComunicationConstants.EL_ORDER_DATES)) {
					
					if(checkRevisedShipDate(inDoc)){
						logger.verbose(
								"FanSendEmailTable -> Revised ship by date, updating Email type and email id");
						outputDoc.getDocumentElement().setAttribute(EmailComunicationConstants.EL_EMAIL_TYPE,
								EmailComunicationConstants.REVISED_SHIP_BY_DATE);
						String reviseEmailID = getEmailAddress(env, orderHKey, EmailComunicationConstants.XPATH_FAN_SHIP_EMAIL, true);
						outputDoc.getDocumentElement().setAttribute(EmailComunicationConstants.EL_EMAIL_ID, reviseEmailID);
						
						outRootEmailElement.setAttribute(EmailComunicationConstants.ATT_EVENT_ID, EmailComunicationConstants.EVNT_REVISE_SHIP_DATE);
						outRootEmailElement.setAttribute(EmailComunicationConstants.ATT_EVENT_DESC, EmailComunicationConstants.DESC_REVISED_SHIP_BY_DATE);
						outRootEmailElement.setAttribute(EmailComunicationConstants.ATT_EMAIL_ID, reviseEmailID);
						
						hasFanCash = false;
					}
				}
				
				Element eleInDoc = inDoc.getDocumentElement();
				
				//Add customattributes node
				Element eleCustomAttr = inDoc.createElement(FanaticsFraudCheckConstants.ATT_fanatics_CustomAttributes);
				eleCustomAttr.setAttribute(EmailComunicationConstants.CUSTOMER_ORDER_NO, getCusomterOrderNo(env, orderHKey));
				eleInDoc.appendChild(eleCustomAttr);
				
				//Add fancash node
				if(hasFanCash == true){
					Element eleFanCashAttr = inDoc.createElement(FanaticsFraudCheckConstants.ATT_fanatics_FanCash);
					eleFanCashAttr.setAttribute(EmailComunicationConstants.CUSTOMER_ID, rootElement.getAttribute(EmailComunicationConstants.EL_BILL_TO_ID));
					eleFanCashAttr.setAttribute(EmailComunicationConstants.TRANSACTION_AMOUNT, getFanCashTransactionAmount(env, inDoc));
					eleInDoc.appendChild(eleFanCashAttr);
				}
				
				// method called for COQ-179
				addReasonCodeForCancellationOnBackorder(env, inDoc);
				
				
				
				
				XMLUtil.addDocument(outputEmailDoc, inDoc, false);
				outRootElement.setAttribute(EmailComunicationConstants.ATT_MSG_XML, SCXmlUtil.getString(outputEmailDoc));
				
				callCreateEntryService(env, outputDoc);
				break;
			}

			case EmailComunicationConstants.EL_RET_RECEIPT_ROOT: {

				logger.verbose("FanSendEmailTable -> Receipt element found, running scenario 2");
				String orderHeaderKeyRec = SCXmlUtil.getXpathAttribute(rootElement,
						EmailComunicationConstants.XPATH_RECEIPT_ORDER_HEADER);
				String orderNoRec 		 = SCXmlUtil.getXpathAttribute(rootElement,
						EmailComunicationConstants.XPATH_RECEIPT_ORDER_NO);
				
				String emailID = getEmailAddress(env, orderHeaderKeyRec, EmailComunicationConstants.XPATH_FAN_BILL_EMAIL, false);
				
				setAttributes(outRootElement, orderHeaderKeyRec, orderNoRec,
						EmailComunicationConstants.RECEIVE_ON_RETURN_RECEIPT, emailID, EmailComunicationConstants.EMPTY_STRING, EmailComunicationConstants.EMPTY_STRING);
				
				outRootEmailElement.setAttribute(EmailComunicationConstants.ATT_EVENT_ID, EmailComunicationConstants.EVNT_RETURN_RECEIPT);
				outRootEmailElement.setAttribute(EmailComunicationConstants.ATT_EVENT_DESC, EmailComunicationConstants.DESC_RETURN_RECEIPT);
				outRootEmailElement.setAttribute(EmailComunicationConstants.ATT_EMAIL_ID, emailID);
				
				XMLUtil.addDocument(outputEmailDoc, inDoc, false);
				outRootElement.setAttribute(EmailComunicationConstants.ATT_MSG_XML, SCXmlUtil.getString(outputEmailDoc));
				
				callCreateEntryService(env, outputDoc);
				break;
			}

			case EmailComunicationConstants.EL_SHIPMENT_ROOT: {

				logger.verbose("FanSendEmailTable -> Shipment element found, running scenario 3");
				String orderHeaderKeyShip = SCXmlUtil.getXpathAttribute(rootElement,
						EmailComunicationConstants.XPATH_SHIPMENT_ORDER_HEADER);
				String orderNoShip 		  = SCXmlUtil.getXpathAttribute(rootElement,
						EmailComunicationConstants.XPATH_SHIPMENT_ORDER_NO);
				
				String emailID 		= getEmailAddress(env, orderHeaderKeyShip, EmailComunicationConstants.XPATH_FAN_SHIP_EMAIL, true);
				String shipmentKey 	=  rootElement.getAttribute(EmailComunicationConstants.ATT_SHIPMENT_KEY);
				
				setAttributes(outRootElement, orderHeaderKeyShip, orderNoShip,
						EmailComunicationConstants.CONFIRM_SHIPMENT, emailID, shipmentKey, EmailComunicationConstants.SHIPMENT_KEY);
				
				Element eleInDoc = inDoc.getDocumentElement();
				
				Element eleCustomAttr = inDoc.createElement(FanaticsFraudCheckConstants.ATT_fanatics_CustomAttributes);
				eleCustomAttr.setAttribute(EmailComunicationConstants.CUSTOMER_ORDER_NO, getCusomterOrderNo(env, orderHeaderKeyShip));
				eleInDoc.appendChild(eleCustomAttr);
				
				outRootEmailElement.setAttribute(EmailComunicationConstants.ATT_EVENT_ID, EmailComunicationConstants.EVNT_SHIP_CONF);
				outRootEmailElement.setAttribute(EmailComunicationConstants.ATT_EVENT_DESC, EmailComunicationConstants.DESC_SHIP_CONFIRMATION);
				outRootEmailElement.setAttribute(EmailComunicationConstants.ATT_EMAIL_ID, emailID);
				
				XMLUtil.addDocument(outputEmailDoc, inDoc, false);
				outRootElement.setAttribute(EmailComunicationConstants.ATT_MSG_XML, SCXmlUtil.getString(outputEmailDoc));
				
				callCreateEntryService(env, outputDoc);
				break;
			}

			}

		} catch (Exception e) {
			
			logger.error("FanSendEmailTable --> ERROR: " + e.getMessage(), e);
			throw new YFSException("FanSendEmailTable");
		}

	}
	
	private Document callAudits(YFSEnvironment env, Document inDoc) throws Exception {

		Document orderAuditListTemp = XMLUtil.getDocument(EmailComunicationConstants.XML_ORDER_AUDIT_LIST_TEMPLATE);

		Document outDoc = XMLUtil.createDocument("OrderAudit");
		String orderHeaderKey = inDoc.getDocumentElement().getAttribute("OrderHeaderKey");
		Element eOutDoc = (Element) outDoc.getDocumentElement();
		eOutDoc.setAttribute("OrderHeaderKey", orderHeaderKey);
		outDoc = CommonUtil.invokeAPI(env,orderAuditListTemp,"getOrderAuditList", outDoc);
		Element einDoc = (Element) inDoc.getDocumentElement();
		eOutDoc = (Element) outDoc.getDocumentElement();
		XMLUtil.addDocument(inDoc, outDoc,false);
		return inDoc;
	}

	// method added for COQ-179
	private void addReasonCodeForCancellationOnBackorder(YFSEnvironment env,
			Document inDoc) {

		try{
			
			logger.verbose("--------FanSendEmailTable-----addReasonCodeForCancellationOnBackorder---entry--"+XMLUtil.getXMLString(inDoc));
			
			String orderHeaderKey = inDoc.getDocumentElement().getAttribute("OrderHeaderKey");
			
			Document orderReleaseDoc = XMLUtil.createDocument("OrderRelease");
			Element orderReleaseEle = orderReleaseDoc.getDocumentElement();
			orderReleaseEle.setAttribute("OrderHeaderKey", orderHeaderKey);

			logger.verbose("--------------FanSendEmailTable------addReasonCodeForCancellationOnBackorder--ip--1--"
						+XMLUtil.getXMLString(orderReleaseDoc));
			
			Document outDoc = CommonUtil.invokeAPI
					(env,"global/template/api/getOrderReleaseList_op.xml","getOrderReleaseList", orderReleaseDoc);
			
			logger.verbose("--------------FanSendEmailTable----addReasonCodeForCancellationOnBackorder--op--2--"
					+XMLUtil.getXMLString(outDoc));
			
			NodeList orderReleaseList = outDoc.getElementsByTagName("OrderRelease");
			
			for(int i=0; i<orderReleaseList.getLength(); i++){
				
				Element OrderReleaseEle = (Element)orderReleaseList.item(i);
				
				String releaseNo = OrderReleaseEle.getAttribute("ReleaseNo");
				
				Document orderReleaseDetailDoc = XMLUtil.createDocument("OrderReleaseDetail");
				Element orderReleaseDetailEle = orderReleaseDetailDoc.getDocumentElement();
				orderReleaseDetailEle.setAttribute("ReleaseNo", releaseNo);
				orderReleaseDetailEle.setAttribute("OrderHeaderKey", orderHeaderKey);

				logger.verbose("--------------FanSendEmailTable------addReasonCodeForCancellationOnBackorder--ip--3--"
							+XMLUtil.getXMLString(orderReleaseDetailDoc));
				
				Document outDoc2 = CommonUtil.invokeAPI
						(env,"global/template/api/getOrderReleaseDetails_op.xml","getOrderReleaseDetails", orderReleaseDetailDoc);
				
				logger.verbose("--------------FanSendEmailTable----addReasonCodeForCancellationOnBackorder--op--4--"
						+XMLUtil.getXMLString(outDoc2));
				
				boolean cancelledOnBackorderflag = false;
				String reasonCode2 = "";
				String noteText2 = "";
				
				NodeList notesList = outDoc2.getElementsByTagName("Note");
				
				for(int k=0; k<notesList.getLength(); k++){
					
					Element noteEle = (Element)notesList.item(k);
					
					String reasonCode = noteEle.getAttribute("ReasonCode");
					String noteText = noteEle.getAttribute("NoteText");
					
					if(reasonCode != null){
						
						if(reasonCode.contains("INV_SHORTAGE")){
							
							logger.verbose("----------contains INV_SHORTAGE-----------5--------");
							
							NodeList modificationTypeList = inDoc.getElementsByTagName("ModificationType");
							
							for(int j=0; j<modificationTypeList.getLength(); j++){
								
								Element modificationTypeEle = (Element)modificationTypeList.item(j);
								logger.verbose("--------modificationTypeEle-----7---"+XMLUtil.getElementXMLString(modificationTypeEle)); 
								
								if(modificationTypeEle.getAttribute("Name").equalsIgnoreCase("CANCEL")){
									
									modificationTypeEle.setAttribute("ModificationReasonCode", reasonCode);
									modificationTypeEle.setAttribute("ModificationReasonText", noteText);
									reasonCode2 = reasonCode;
									noteText2 = noteText;
									cancelledOnBackorderflag = true;
								}
								
							}
							
						}
					}
					
					
				}
				
				
				if(cancelledOnBackorderflag == true){
				
					Document orderDoc = XMLUtil.createDocument("Order");
					Element orderEle = orderDoc.getDocumentElement();
					orderEle.setAttribute("OrderHeaderKey", orderHeaderKey);
					orderEle.setAttribute("Action", "MODIFY");
					orderEle.setAttribute("Override", "Y");
					
					Element notesEle = orderDoc.createElement("Notes");
					Element noteEle = orderDoc.createElement("Note");
					noteEle.setAttribute("ReasonCode", reasonCode2);
					noteEle.setAttribute("NoteText", "Order cancelled due to "+noteText2);
					
					notesEle.appendChild(noteEle);
					orderEle.appendChild(notesEle);
					

					logger.verbose("--------------FanSendEmailTable------addReasonCodeForCancellationOnBackorder--6--"
								+XMLUtil.getXMLString(orderDoc));
					
					Document outDoc3 = CommonUtil.invokeAPI
							(env,"changeOrder", orderDoc);
					
					logger.verbose("--------------FanSendEmailTable----addReasonCodeForCancellationOnBackorder--7--"
							+XMLUtil.getXMLString(outDoc3));
				}

				
				
			}
			
			logger.verbose("--------FanSendEmailTable-----addReasonCodeForCancellationOnBackorder---exit--"+XMLUtil.getXMLString(inDoc));
			
			
		}catch(Exception e){
			
			logger.verbose("FanSendEmailTable --> ERROR inside addReasonCodeForCancellationOnBackorder : " + e.getMessage());
		}

	
		
	}


	private void setAttributes(Element outRootElement, String orderHeaderKey, String orderNo, String emailType, String emailID, String entityKey, String entityName) {
		
		outRootElement.setAttribute(EmailComunicationConstants.EL_ORDER_NO, orderNo);
		outRootElement.setAttribute(EmailComunicationConstants.EL_ORDER_HEADER_KEY, orderHeaderKey);
		outRootElement.setAttribute(EmailComunicationConstants.EL_EMAIL_TYPE, emailType);
		outRootElement.setAttribute(EmailComunicationConstants.EL_EMAIL_ID, emailID);
		outRootElement.setAttribute(EmailComunicationConstants.EL_ENTITY_KEY, entityKey);
		outRootElement.setAttribute(EmailComunicationConstants.EL_ENTITY_NAME, entityName);
	}

	public boolean ElementExists(Document doc, String content) {

		NodeList nodeList = doc.getElementsByTagName(content);
		int length = nodeList.getLength();
		return length == 0 ? false : true;
	}

	private void callCreateEntryService(YFSEnvironment env, Document outputDoc) {

		logger.info(
				"FanSendEmailTable -> Invoking " + EmailComunicationConstants.CREATE_EMAIL_ENTRY_SERVICE + "service");

		logger.verbose("inputXML -> " + XMLUtil.getXMLString(outputDoc));
		try {
			
			Document outDoc = CommonUtil.invokeService(env, EmailComunicationConstants.CREATE_EMAIL_ENTRY_SERVICE,
					outputDoc);
			logger.verbose(EmailComunicationConstants.CREATE_EMAIL_ENTRY_SERVICE
					+ " sercvice Invoked, DB entry created !. Response is : " + SCXmlUtil.getString(outDoc));
			
		} catch (Exception e) {
			logger.error("Exception while invoking " + EmailComunicationConstants.CREATE_EMAIL_ENTRY_SERVICE + " : " + e.getMessage() + ", Cause: " + e.getCause());
		}	

	}
	
	private boolean checkRevisedShipDate(Document inDoc){
		
		boolean isRevisedShipDate = false;
		
		Element rootMessageNode = inDoc.getDocumentElement();

		NodeList orderDates = rootMessageNode.getElementsByTagName(EmailComunicationConstants.EL_ORDER_DATE);
		
		if (orderDates != null && orderDates.getLength() > 0) {
			for (int i = 0; i < orderDates.getLength(); i++) {
				if (orderDates.item(i).getNodeType() == Node.ELEMENT_NODE) {
					
					/*
					 * COQ-155 minor xml change, replaced element with String attribute and changed the control statement
					 */
					
					Element orderDate   = (Element) orderDates.item(i);
					String revisedOrderDate = orderDate.getAttribute(EmailComunicationConstants.ATT_DATE_TYPE_ID);
	
					if(null != revisedOrderDate && revisedOrderDate.equalsIgnoreCase(EmailComunicationConstants.REVISED_SHIP_DATE)){
						
						isRevisedShipDate = true;
						break;
					}
				}
			}
		}
		
		return isRevisedShipDate;
	}
	
	private String getEmailAddress(YFSEnvironment env, String orderHeaderKey, String emailAttribute, boolean checkBoth){
		
		String inDocStr = "<Order OrderHeaderKey=\"" + orderHeaderKey + "\" />";
		String templateStr = "<OrderList><Order><PersonInfoShipTo EMailID=\"\" /><PersonInfoBillTo EMailID=\"\" /></Order></OrderList>";
		
		String emailID = "";
		
		Document outDoc = getOrderListAPI(env,orderHeaderKey, inDocStr, templateStr);
	
		
		if(emailAttribute.equals(EmailComunicationConstants.XPATH_FAN_BILL_EMAIL) && checkBoth==false){
			emailID = XMLUtil.getXpathProperty(outDoc, EmailComunicationConstants.XPATH_FAN_FULL_BILL_EMAIL);
		}
		else if(emailAttribute.equals(EmailComunicationConstants.XPATH_FAN_BILL_EMAIL) && checkBoth==true){
			emailID = XMLUtil.getXpathProperty(outDoc, EmailComunicationConstants.XPATH_FAN_FULL_BILL_EMAIL);
			
			if(emailID.equals(""))
				emailID = XMLUtil.getXpathProperty(outDoc,EmailComunicationConstants.XPATH_FAN_FULL_SHIP_EMAIL);
		}
		else if(emailAttribute.equals(EmailComunicationConstants.XPATH_FAN_SHIP_EMAIL) && checkBoth==false){
			emailID = XMLUtil.getXpathProperty(outDoc, EmailComunicationConstants.XPATH_FAN_FULL_SHIP_EMAIL);
		}
		else if(emailAttribute.equals(EmailComunicationConstants.XPATH_FAN_SHIP_EMAIL) && checkBoth==true){
			emailID = XMLUtil.getXpathProperty(outDoc, EmailComunicationConstants.XPATH_FAN_FULL_SHIP_EMAIL);
			
			if(emailID.equals(""))
				emailID = XMLUtil.getXpathProperty(outDoc, EmailComunicationConstants.XPATH_FAN_FULL_BILL_EMAIL);
		}
		
		
		return emailID;
	}
	
	private Document getOrderListAPI(YFSEnvironment env, String orderHeaderKey, String input, String template){
		
		Document templateDoc = null;
		Document inDoc = null;
		Document outDoc = null;
		
		try {
			templateDoc = XMLUtil.getDocument(template);
			inDoc = XMLUtil.getDocument(input);
			outDoc = CommonUtil.invokeAPI(env, template, FANConstants.API_getOrderList, inDoc);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outDoc;
		
	}
	
	private String getCusomterOrderNo(YFSEnvironment env, String orderHeaderKey){
		
		String inDocStr = "<Order OrderHeaderKey=\"" + orderHeaderKey + "\" />";
		String templateStr = "<OrderList><Order><CustomAttributes CustomerOrderNo=\"\" /></Order></OrderList>";
		
		Document outDoc = getOrderListAPI(env,orderHeaderKey, inDocStr, templateStr);
		
		String customerOrderNo = "";
		customerOrderNo = XMLUtil.getXpathProperty(outDoc, EmailComunicationConstants.XPATH_FAN_FULL_CUSTOMERORDERNO);
		
		return customerOrderNo;
	}
	
	private String getFanCashTransactionAmount(YFSEnvironment env, Document inDoc) throws Exception{
		logger.verbose("Inside getFanCashTransactionAmount");

		double total = 0.00; 
		Element rootMessageNode = inDoc.getDocumentElement();
		

		String sellerOrg = XPathUtil.getXpathAttribute(inDoc, ManageFanCashConstants.FANCASH_XPATH_SELLERORGANIZATIONCODE);			
		String enterpriseOrg = XPathUtil.getXpathAttribute(inDoc, ManageFanCashConstants.FANCASH_XPATH_ENTERPRISECODE);			

		Document commonCodeDoc = XMLUtil.createDocument("CommonCode");
		Element ordercommonCodeEle = commonCodeDoc.getDocumentElement();
		ordercommonCodeEle.setAttribute("CodeType", "FanCashOrg");
		ordercommonCodeEle.setAttribute("CodeValue", sellerOrg);

		Document commonCodeOP = CommonUtil.invokeAPI(env,"getCommonCodeList", commonCodeDoc);
		String sellerAmount = XPathUtil.getXpathAttribute(commonCodeOP, ManageFanCashConstants.FANCASH_XPATH_COMMONCODESHORTDESC);		
		logger.debug("Seller FanCash amount: " +sellerAmount);

		if (sellerAmount == null || sellerAmount =="") {
			ordercommonCodeEle.setAttribute("CodeValue", enterpriseOrg);
			commonCodeOP = CommonUtil.invokeAPI	(env,"getCommonCodeList", commonCodeDoc);
			sellerAmount = ""+Double.valueOf(XPathUtil.getXpathAttribute(commonCodeOP, ManageFanCashConstants.FANCASH_XPATH_COMMONCODESHORTDESC));
			logger.debug("Enterprise FanCash amount: " +sellerAmount);
		}
		
		logger.verbose("Total Fancash -> " + sellerAmount);
		
		return sellerAmount;
	}
	
	

}
