package com.fanatics.sterling.api;

import java.util.ArrayList;

/**
 * This class is a custom API that gets invoked after the PAYMENT_COLLECTION.ON_INVOICE_COLLECTION event is triggered. 
 * It creates the changeOrder Input and adds the order notes. This functionality will be extended to work on other 
 * events as well.
 *    
 * @(#) FanOrderNotes.java    
 * Created on   April 15, 2016
 *              
 *
 * Package Declaration: 
 * File Name:       FanOrderNotes.java
 * Package Name:    com.fanatics.sterling.api;
 * Project name:    Fanatics
 * Type Declaration:    
 * Class Name:      FanOrderNotes
 * 
 * @author KNtagkas
 * @version 1.0
 * @history none
 *     
 * 
 * (C) Copyright 2016-2017 by owner.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information
 * of the owner. ("Confidential Information").
 * Redistribution of the source code or binary form is not permitted
 * without prior authorization from the owner.
 *
 */

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fanatics.sterling.constants.OrderNotesConstants;
import com.fanatics.sterling.util.FANDBUtil;
import com.fanatics.sterling.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

@SuppressWarnings("unused")
public class FanOrderNotes implements YIFCustomApi {

	private static YFCLogCategory logger = YFCLogCategory.instance(FanOrderNotes.class);
	private Properties props;

	@Override
	public void setProperties(Properties arg0) throws Exception {
		props = arg0;

	}

	public Document createOrderNotes(YFSEnvironment env, Document inDoc) {

		logger.info("FanOrderNotes -> Begin");

		Document outputDoc  = SCXmlUtil.createDocument(OrderNotesConstants.EL_ORDER_ROOT);
		Element eleNotes	= outputDoc.createElement(OrderNotesConstants.ATT_NOTES);
		outputDoc.getDocumentElement().appendChild(eleNotes);

		try {

			Element rootElement 	= inDoc.getDocumentElement();
			String rootElementName  = rootElement.getTagName();
			String strUserID 		= env.getUserId();

			logger.verbose("FanOrderNotes -> Fetching current DB time  ");

			ArrayList<Object[]> alTimeStamp = FANDBUtil.getDBResult(env, OrderNotesConstants.SQL_GET_DBTIME, 1);

			Object[] resultRow 				= alTimeStamp.get(0);
			String strCurrentDBDateTime 	= (String) resultRow[0];

			logger.verbose("FanOrderNotes current DB time is " + strCurrentDBDateTime);

			switch (rootElementName) {

			case OrderNotesConstants.EL_ORDER_ROOT: {

				logger.verbose("FanOrderNotes -> Order root element found, running scenario 1");
				
				String orderHKey = rootElement.getAttribute(OrderNotesConstants.ATT_ORDER_HEADER_KEY);
				outputDoc.getDocumentElement().setAttribute(OrderNotesConstants.ATT_ORDER_HEADER_KEY, orderHKey);

				NodeList nodeListHoldtypes = rootElement.getElementsByTagName(OrderNotesConstants.ATT_ORDER_HOLD_TYPE);
				
				/*
				 * COQ-148 Pending Evaluation hold gets applied automatically through CreateOrder and thus changeOrdernever gets called
				 * Added the order notes action in the ON SUCCESS event of Create Order. If createOrder input has a pending evaluation hold
				 * the below logic will add the note for it.
				 */
				
				if (nodeListHoldtypes != null
						&& nodeListHoldtypes.getLength() > 0) {
					for (int i = 0; i < nodeListHoldtypes.getLength(); i++) {
						if (nodeListHoldtypes.item(i).getNodeType() == Node.ELEMENT_NODE) {
							
							Element holdType 	= (Element) nodeListHoldtypes.item(i);
							String holdName		= holdType.getAttribute(OrderNotesConstants.HOLD_TYPE);
							String holdStatus	= holdType.getAttribute(OrderNotesConstants.ATT_STATUS);
							String noteText 	= OrderNotesConstants.NOTE_PENDING_EVAL + " " + strCurrentDBDateTime;
							
							if (holdName.equals(OrderNotesConstants.PENDING_EVAL) && holdStatus.equals(OrderNotesConstants.HOLD_NUM)) {
								
								addNotes(outputDoc ,  noteText);
					
							}else{
								logger.verbose("FanOrderNotes -> Holdtypes found but either status not 1100 or hold is not Pendind evaluation hold .. Note creation bypassed");
							}			
						}
					}
				}
				//COQ-148 - END
				
				if (ElementExists(inDoc, OrderNotesConstants.EL_INVOICE_COLLECTION)) {

					logger.verbose(
							"FanOrderNotes -> Payment Collection On invoice Collection Event detected, adding Order Note");

					// outputDoc.getDocumentElement().setAttribute(OrderNotesConstants.ATT_ORDER_HEADER_KEY,
					// orderHKey);

//					Element orderInvoice = SCXmlUtil.getXpathElement(rootElement,
//							OrderNotesConstants.XPATH_INVOICE_COLLECTION_1 + orderHKey
//									+ OrderNotesConstants.XPATH_INVOICE_COLLECTION_2);
					
					Element orderInvoice = SCXmlUtil.getXpathElement(rootElement,"/Order/InvoiceCollections/InvoiceCollection");
					
					if(orderInvoice != null){
						
						String refundAmount  = orderInvoice.getAttribute(OrderNotesConstants.ATT_AMOUNT_COLLECTED);
						
						if(refundAmount != null){
							
							if(!refundAmount.equalsIgnoreCase("")){
								
								Float refundAmountFlt = Float.parseFloat(refundAmount);
								
								if(refundAmountFlt.floatValue() < 0.0f){
									
									refundAmount = refundAmount.split("-")[1];
									
									String noteText 	 = strUserID + " " + OrderNotesConstants.NOTE_PAYMENT_COLLECTION + " " + refundAmount
											+ " " + strCurrentDBDateTime;

									addNotes(outputDoc, noteText);
								}
							}

						}
					}

				}
				
				if (ElementExists(inDoc, OrderNotesConstants.EL_CANCELED_FROM)) {

					Element eleStatus 		= SCXmlUtil.getXpathElement(rootElement,
							OrderNotesConstants.XPATH_STATUS_RELEASED);
					NodeList nodeListStatus = SCXmlUtil.getXpathNodes(rootElement,
							OrderNotesConstants.XPATH_STATUS_RELEASED_P);

					String status 			= eleStatus.getAttribute(OrderNotesConstants.ATT_STATUS);

					if (status.equalsIgnoreCase(OrderNotesConstants.RELEASED)) {

						logger.verbose(
								"FanOrderNotes -> Order Release, Change ON CANCEL Event detected, adding Cancelled by warehouse Note");

						if (nodeListStatus != null && nodeListStatus.getLength() > 0) {
							for (int i = 0; i < nodeListStatus.getLength(); i++) {
								if (nodeListStatus.item(i).getNodeType() == Node.ELEMENT_NODE) {

									Element orderLine 	 = (Element) nodeListStatus.item(i);
									String itemId	 	 = SCXmlUtil.getXpathAttribute(orderLine,
											OrderNotesConstants.XPATH_ITEM_ID);
									String itemShortDesc = SCXmlUtil.getXpathAttribute(orderLine,
											OrderNotesConstants.XPATH_ITEM_SHORT_DESC);
									String noteText		 = strUserID + " " + OrderNotesConstants.NOTE_CANCELED_WH + " "
											+ itemId + " " + itemShortDesc + " " + strCurrentDBDateTime;
									addNotes(outputDoc, noteText);

								}
							}
						}
					} else {

						logger.verbose(
								"FanOrderNotes -> OrderChange, ON CANCEL Event detected, adding Cancel product Note");

						String action = SCXmlUtil.getXpathAttribute(rootElement,
								OrderNotesConstants.XPATH_ORDER_CANCEL);

						if (action.equalsIgnoreCase(OrderNotesConstants.CANCEL)) {

							logger.verbose("FanOrderNotes -> complete Order cancel action detected");
							
							String additionalInfo=" ";
							NodeList attributeList = inDoc.getElementsByTagName("Attribute");
							
							for(int i=0; i<attributeList.getLength(); i++){
								
								Element attributeEle = (Element)attributeList.item(i);
								String name = attributeEle.getAttribute("Name");
								String newValue = attributeEle.getAttribute("NewValue");
								String modificationType = attributeEle.getAttribute("ModificationType");
								
								if(name.equalsIgnoreCase("FraudStatus") && newValue.equalsIgnoreCase("2") 
										&& modificationType.equalsIgnoreCase("CHANGE_CUSTOM_ATTRIBUTES")){
									
									additionalInfo = " due to fraud reject";
								}
							}

							String refundAmount 	  = SCXmlUtil.getXpathAttribute(rootElement, 
									OrderNotesConstants.XPATH_ORDER_PRICE_INFO);							
							Double refundAmountDouble = Double.parseDouble(refundAmount);
							
							logger.verbose("FanOrderNotes -> refund Amount : " + -refundAmountDouble);

							String noteText = strUserID + " " + OrderNotesConstants.NOTE_CANCELED_ORDER + " " + "$"
									+ -refundAmountDouble + " " + OrderNotesConstants.NOTE_PAYMENT_COLLECTION + " "
									+ strCurrentDBDateTime + additionalInfo;
							addNotes(outputDoc, noteText);

						} else {

							logger.verbose("FanOrderNotes -> Order line or Quantity cancel action detected");

							NodeList orderLineList = SCXmlUtil.getXpathNodes(rootElement,
									OrderNotesConstants.XPATH_STATUS_RELEASED);

							if (orderLineList != null && orderLineList.getLength() > 0) {
								for (int i = 0; i < orderLineList.getLength(); i++) {
									if (orderLineList.item(i).getNodeType() == Node.ELEMENT_NODE) {

										Element orderLine 		= (Element) orderLineList.item(i);
										String changedQuantity  = orderLine
												.getAttribute(OrderNotesConstants.ATT_CHANGE_QUANTITY);
										String itemId 			= SCXmlUtil.getXpathAttribute(orderLine,
												OrderNotesConstants.XPATH_ITEM_ID);
										String itemShortDesc 	= SCXmlUtil.getXpathAttribute(orderLine,
												OrderNotesConstants.XPATH_ITEM_SHORT_DESC);
										Element linePriceInfo 	= SCXmlUtil.getChildElement(orderLine,
												OrderNotesConstants.EL_LINE_PRICE_INFO);
										String changeInLine 	= linePriceInfo
												.getAttribute(OrderNotesConstants.CHANGE_LINE_TOTAL);
										Double changeLineDouble = Double.parseDouble(changeInLine);
										String noteText = strUserID + " " + OrderNotesConstants.NOTE_CANCELED_WH + " "
												+ itemId + " quantity " + changedQuantity + ", " + itemShortDesc + " "
												+ "$" + -changeLineDouble + " "
												+ OrderNotesConstants.NOTE_PAYMENT_COLLECTION + " "
												+ strCurrentDBDateTime;
										addNotes(outputDoc, noteText);

									}
								}
							}

						}

					}

				}

				break;
			}

			case OrderNotesConstants.EL_EXTN_FAN_EMAIL: {

				logger.verbose("FanOrderNotes -> EXTNFanEmail root element found, running scenario 2");
				String orderHKey = rootElement.getAttribute(OrderNotesConstants.ATT_ORDER_HEADER_KEY);
				outputDoc.getDocumentElement().setAttribute(OrderNotesConstants.ATT_ORDER_HEADER_KEY, orderHKey);
				String noteText  = strUserID + " " + OrderNotesConstants.NOTE_SHIP_CONFIRMATION_MAIL + " "
						+ strCurrentDBDateTime;
				addNotes(outputDoc, noteText);

				break;
			}
			
			default : {
				logger.warn("FanOrderNotes -> Message not recognised, FanOrderNotes functionality bypassed!");
			}

			}

			logger.verbose("FanOrderNotes output : " + XMLUtil.getXMLString(outputDoc));

		} catch (Exception e) {
			logger.error("FanOrderNotes --> ERROR: " + e.getMessage(), e);
			throw new YFSException("FanOrderNotes");
		}

		logger.info("FanOrderNotes -> End");

		return outputDoc;
	}

	private void addNotes(Document docIPChangeOrder, String noteText) {

		logger.verbose("Adding note text in changeOrder API input : " + noteText);

		Element eleInputChangeOrderRoot = docIPChangeOrder.getDocumentElement();

		Element eleNotes = SCXmlUtil.getXpathElement(docIPChangeOrder.getDocumentElement(),
				OrderNotesConstants.XPATH_NOTES);
		Element eleNote  = docIPChangeOrder.createElement(OrderNotesConstants.ATT_NOTE);

		eleNote.setAttribute(OrderNotesConstants.ATT_CONTACT_USER, OrderNotesConstants.SYSTEM);
		eleNote.setAttribute(OrderNotesConstants.ATT_NOTE_TEXT, noteText);
		eleNotes.appendChild(eleNote);
		eleInputChangeOrderRoot.appendChild(eleNotes);

		logger.verbose("Order notes added successfully!");

	}

	public boolean ElementExists(Document doc, String content) {

		NodeList nodeList = doc.getElementsByTagName(content);
		int length 		  = nodeList.getLength();
		return length == 0 ? false : true;
	}

}
