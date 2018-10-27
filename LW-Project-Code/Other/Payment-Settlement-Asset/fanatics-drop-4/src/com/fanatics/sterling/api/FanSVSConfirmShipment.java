package com.fanatics.sterling.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fanatics.sterling.constants.FanSVSConfirmShipmentConstants;
import com.fanatics.sterling.constants.FanaticsFraudCheckConstants;
import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.FANConstants;
import com.fanatics.sterling.util.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class FanSVSConfirmShipment {

	private static YFCLogCategory logger = YFCLogCategory.instance(YFCLogCategory.class);
	
	
	public void processSVSShipConfirmMsg (YFSEnvironment env, Document inDoc){
		
		// Check if the message is Reject
		String strAction = XMLUtil.getXpathProperty(inDoc, "Shipment/@Action") ;
		if (strAction.equals("Reject")){
			
			logger.verbose("Its rejected by SVS");
			/**
			 *  put hold on the order
			 */

			String strOrderNo = XMLUtil.getXpathProperty(inDoc, "Shipment/ShipmentLines/ShipmentLine/@OrderNo");
			String strDocumentType = XMLUtil.getXpathProperty(inDoc, "Shipment/@DocumentType");
			String strEnterpriseCode = XMLUtil.getXpathProperty(inDoc, "Shipment/@EnterpriseCode");
			
			Document docIPChangeOrder = null;
			String strIPChangeOrder = "<Order OrderNo='"+strOrderNo+"' EnterpriseCode='"+strEnterpriseCode+"' DocumentType='"+strDocumentType+"'>" +
					"<OrderHoldTypes><OrderHoldType HoldType='SVS_REJECT_HOLD' ReasonText='Rejected By SVS' ResolverUserId='' Status='1100'/>" +
					"</OrderHoldTypes></Order>";
			
			try 
			{  
				docIPChangeOrder = XMLUtil.getDocument(strIPChangeOrder);
			} catch (Exception e) {  
				e.printStackTrace();  
			} 
			
			// invoke changeOrder API
			logger.verbose("review docIPChangeOrder xml is: "+ XMLUtil.getXMLString(docIPChangeOrder));
			try {
				CommonUtil.invokeAPI(env, FANConstants.API_CHANGE_ORDER, docIPChangeOrder);
			} catch (Exception e) {

				e.printStackTrace();
			}
		
			
			// Create Alert
			String strDescription = "SVS Rejected the Refund Order Number "+ strOrderNo;
			String strDetDescription = "SVS Rejected the Refund Order Number "+ strOrderNo;
			
			String strCreateExceptionIP ="<Inbox Description='"+strDescription+"' DetailDescription='"+strDetDescription+"' " +
					                     "EnterpriseKey='"+strEnterpriseCode+"' ExceptionType='SVS_Exception' ExpirationDays='20' FlowName='' " +
							             "OrderNo='"+strOrderNo+"' QueueId='SVSRejectedOrder' />" ;
			Document docCreateException = null;
			try {
				docCreateException = XMLUtil.getDocument(strCreateExceptionIP);
				
				CommonUtil.invokeAPI(env, FANConstants.API_CREATE_EXCEPTION, docCreateException);
				
			} catch (ParserConfigurationException | SAXException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else {
		
		/**
		 * Resolve SVS Reject Hold, if the same hold is on	
		 */
		
			resolveSVSRejectHold(env, inDoc);
		
			
		/**
		 * From the inDoc, fetch make a map with GC Numbers as Key and the GC PIN as pair
		 */
		
		Element eleExtns = (Element) inDoc.getElementsByTagName(FanSVSConfirmShipmentConstants.ELE_EXTNS).item(0);
		NodeList nlExtn = eleExtns.getElementsByTagName(FanSVSConfirmShipmentConstants.ELE_EXTN);
		
		int nlExtnLength = nlExtn.getLength();
		
		Element eleExtn = null;
		String GCNumber = FANConstants.NO_VALUE;
		String GCPin = FANConstants.NO_VALUE;
		Map<String,String> mapGCNumber_GCPin = new HashMap<String,String>();
		
		for(int i=0;i<nlExtnLength;i++){
			
			eleExtn = (Element) nlExtn.item(i) ;
			GCNumber = eleExtn.getAttribute(FanSVSConfirmShipmentConstants.ATT_SVS_GC_NUMBER);
			GCPin = eleExtn.getAttribute(FanSVSConfirmShipmentConstants.ATT_SVS_GC_PIN);
			mapGCNumber_GCPin.put(GCNumber, GCPin);
	
		}
		
		logger.verbose("Map is "+mapGCNumber_GCPin);
		
		/**
		 * Remove the Element containing the GC Numbers and the GC PINs from the inDoc
		 */
		
		Element eleRoot = inDoc.getDocumentElement();
		
		Element eleShipmentLines = (Element) inDoc.getElementsByTagName(FANConstants.ELE_SHIPMENT_LINES).item(0);
		Element eleShipmentLine = (Element) eleShipmentLines.getElementsByTagName(FANConstants.ELE_SHIPMENT_LINE).item(0);
	
		eleShipmentLine.removeChild(eleExtns);
		logger.verbose("Input after removal of node is: "+ XMLUtil.getXMLString(inDoc));
		
		
		/**
		 * call confirmShipment OOB api with the inDoc
		 */
		Document docOPConfirmShipment = null;
		try {
			docOPConfirmShipment = CommonUtil.invokeAPI(env, FanSVSConfirmShipmentConstants.API_CONFIRM_SHIPMENT, inDoc);
			logger.verbose("docOPConfirmShipment is : "+ XMLUtil.getXMLString(docOPConfirmShipment));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
		
		
		/**
		 * Run loop on the map and save all the GC Numbers and PINs in the Hang-Off table
		 */
		
		
		Element rootOPConfirmShipment = docOPConfirmShipment.getDocumentElement();
		
		String strShipmentKey = rootOPConfirmShipment.getAttribute(FANConstants.AT_SHIPMENT_KEY);
		String strShipmentNo = rootOPConfirmShipment.getAttribute(FanSVSConfirmShipmentConstants.ATT_SHIPMENT_NO);
		String strInputCreateGCNoAndPIN = FANConstants.NO_VALUE;
		Document docInputConfirmShipment = null;
		
		// get the details of shipment and fetch the required attributes
		Document docOPGetShipmentDetails = null;
		try {
			Document docIPGetShipmentDetails= XMLUtil.getDocument("<Shipment ShipmentKey='"+strShipmentKey+"'/>");
			Document docTempGetShipmentDetails= 
					XMLUtil.getDocument("<Shipment EnterpriseCode=''><ShipmentLines><ShipmentLine OrderHeaderKey='' OrderNo='' OrderLineKey=''></ShipmentLine></ShipmentLines></Shipment>");
			docOPGetShipmentDetails= 
					CommonUtil.invokeAPI(env, docTempGetShipmentDetails, FanSVSConfirmShipmentConstants.API_GET_SHIPMENT_DETAILS, docIPGetShipmentDetails) ;
			logger.verbose("OP if the docOPGetShipmentDetails : "+ XMLUtil.getXMLString(docOPGetShipmentDetails));
			
		} catch (ParserConfigurationException | SAXException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		String strEnterpriseCode= FANConstants.NO_VALUE;
		String strOrderNo= FANConstants.NO_VALUE;
		String strOrderHeaderKey= FANConstants.NO_VALUE;
		String strOrderLineKey= FANConstants.NO_VALUE;
		
		strEnterpriseCode = docOPGetShipmentDetails.getDocumentElement().getAttribute(FANConstants.ATT_EnterpriseCode);
		Element eleShipmentDetShipmentLine = (Element) docOPGetShipmentDetails.getElementsByTagName("ShipmentLine").item(0);
		strOrderHeaderKey = eleShipmentDetShipmentLine.getAttribute(FANConstants.ATT_ORDER_HEADER_KEY);
		strOrderNo = eleShipmentDetShipmentLine.getAttribute(FANConstants.ORDER_NO);
		strOrderLineKey = eleShipmentDetShipmentLine.getAttribute(FANConstants.ATT_ORDER_LINE_KEY);
		
		Iterator entries = mapGCNumber_GCPin.entrySet().iterator();
		while (entries.hasNext()) {
		  Entry thisEntry = (Entry) entries.next();
		  
		  String strGCNumber = thisEntry.getKey().toString();
		  String strGCPin = thisEntry.getValue().toString();
		  
		  logger.verbose("GCNumber is "+ strGCNumber);
		  logger.verbose("GCPin is "+ strGCPin);
	  
		  strInputCreateGCNoAndPIN = "<EXTNRefundGiftCert ShipmentKey='"+strShipmentKey+"' EnterpriseCode='"+strEnterpriseCode+"' " +
					            "ShipmentNo='"+strShipmentNo+"' OrderNo='"+strOrderNo+"' OrderHeaderKey='"+strOrderHeaderKey+"' " +
					            "GiftCertNo='"+strGCNumber+"' GiftCertPIN='"+strGCPin+"' OrderLineKey='"+strOrderLineKey+"'><YFSShipment ShipmentKey='"+strShipmentKey+"'" +
					            " ShipmentNo='"+strShipmentNo+"' OrderNo='"+strOrderNo+"' OrderHeaderKey='"+strOrderHeaderKey+"' " +
					            "EnterpriseCode='"+strEnterpriseCode+"' OrderLineKey='"+strOrderLineKey+"'/></EXTNRefundGiftCert>";
		  
		  try {  
			docInputConfirmShipment = XMLUtil.getDocument(strInputCreateGCNoAndPIN);
			logger.verbose("Input doc for SRVC_CREATE_GC_NO_PIN is: "+ XMLUtil.getXMLString(docInputConfirmShipment));
			
			CommonUtil.invokeService(env, FanSVSConfirmShipmentConstants.SRVC_CREATE_GC_NO_PIN, docInputConfirmShipment);
			
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		}
		
		
		
	}
	}

	private void resolveSVSRejectHold(YFSEnvironment env, Document inDoc) {
		


		logger.verbose("resolveAllHolds docIPChangeOrder is: "+ XMLUtil.getXMLString(inDoc));

		// call getOrderDetails to get the present holds
		
		String strOrderNo = XMLUtil.getXpathProperty(inDoc, "Shipment/ShipmentLines/ShipmentLine/@OrderNo");
		String strDocumentType = XMLUtil.getXpathProperty(inDoc, "Shipment/@DocumentType");
		String strEnterpriseCode = XMLUtil.getXpathProperty(inDoc, "Shipment/@EnterpriseCode");
		
		String strInputGetOrderDet =  "<Order OrderNo='"+strOrderNo+"' EnterpriseCode='"+strEnterpriseCode+"' DocumentType='"+strDocumentType+"'></Order>";
		
		String templateGetOrderDetails = "<Order EnterpriseCode=''><OrderHoldTypes><OrderHoldType HoldType='' Status=''></OrderHoldType></OrderHoldTypes></Order>" ;
		
		Document docIPGetOrderDetails = null;
		Document docGetOrderDetails = null;
		try 
		{  
			docIPGetOrderDetails = XMLUtil.getDocument(strInputGetOrderDet);
			docGetOrderDetails = XMLUtil.getDocument(templateGetOrderDetails);
		} catch (Exception e) {  
			e.printStackTrace();  
		} 

		Document docOPGetOrderDetails = null;
		try {
			docOPGetOrderDetails = CommonUtil.invokeAPI(env, docGetOrderDetails, FANConstants.API_GET_ORDER_DET, docIPGetOrderDetails);
			logger.verbose("resolveAllHolds docOPGetOrderDetails is: "+ XMLUtil.getXMLString(docOPGetOrderDetails));
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		// add OrderHoldTypes Element to the xml
		Element eleOrderHoldTypes = docIPGetOrderDetails.createElement(FanaticsFraudCheckConstants.ATT_fanatics_OrderHoldTypes);
		Node tempNode = docIPGetOrderDetails.importNode(eleOrderHoldTypes, false);
		docIPGetOrderDetails.getDocumentElement().appendChild(tempNode);
		logger.verbose("resolveAllHolds docIPChangeOrder is 11: "+ XMLUtil.getXMLString(docIPGetOrderDetails));

		// get the handle of the OrderHoldTypes element present in the docIPChangeOrder
		eleOrderHoldTypes = (Element) docIPGetOrderDetails.getDocumentElement().getElementsByTagName(FanaticsFraudCheckConstants.ATT_fanatics_OrderHoldTypes).item(0);

		// add the Hold Types (to be resolved) to the changeOrder input xml
		NodeList nlOrderHoldType = docOPGetOrderDetails.getElementsByTagName(FanaticsFraudCheckConstants.ATT_fanatics_OrderHoldType);
		int nlOrderHoldTypeLength = nlOrderHoldType.getLength();
		logger.verbose("nlOrderHoldTypeLength" + nlOrderHoldTypeLength);
		Element eleOrderHoldType = null;

		Document docOPFraudCommonCodes = null;
		
		for(int i=0;i<nlOrderHoldTypeLength;i++){
			logger.verbose("inside the loop ");
			eleOrderHoldType = (Element) nlOrderHoldType.item(i);
			
			// add the current hold to the input doc only if the status is created (1100) and its an SVS Reject hold
			logger.verbose("status here is "+ eleOrderHoldType.getAttribute(FANConstants.ATT_STATUS));

			
			if (eleOrderHoldType.getAttribute(FANConstants.ATT_STATUS).equals(FANConstants.STR_1100) 
					&& eleOrderHoldType.getAttribute(FANConstants.ATT_fanatics_HoldType).equals("SVS_REJECT_HOLD") ){
				logger.verbose("inside the if cond ");
				// create new element OrderHoldType
				Element eleIPOrderHoldType = docIPGetOrderDetails.createElement(FanaticsFraudCheckConstants.ATT_fanatics_OrderHoldType);
				eleIPOrderHoldType.setAttribute(FanaticsFraudCheckConstants.ATT_fanatics_HoldType, eleOrderHoldType.getAttribute(FanaticsFraudCheckConstants.ATT_fanatics_HoldType));
				eleIPOrderHoldType.setAttribute(FANConstants.ATT_STATUS, FANConstants.STR_1300);

				tempNode = docIPGetOrderDetails.importNode(eleIPOrderHoldType, false);
				eleOrderHoldTypes.appendChild(tempNode);

				logger.verbose("resolveAllHolds docIPChangeOrder is 12: "+ XMLUtil.getXMLString(docIPGetOrderDetails));
			}

		}

		Document docIPChangeOrder = docIPGetOrderDetails;
		// invoke changeOrder API
		        try {
					CommonUtil.invokeAPI(env, FANConstants.API_CHANGE_ORDER, docIPChangeOrder);
					} catch (Exception e) {
	
					e.printStackTrace();
				}
	
		
	}
}
