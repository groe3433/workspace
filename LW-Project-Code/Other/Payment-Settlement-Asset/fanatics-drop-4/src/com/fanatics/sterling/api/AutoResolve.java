	package com.fanatics.sterling.api;
	
	import java.io.IOException;
	
	import javax.xml.parsers.ParserConfigurationException;
	
	import org.w3c.dom.Document;
	import org.w3c.dom.Element;
	import org.w3c.dom.Node;
	import org.w3c.dom.NodeList;
	import org.xml.sax.SAXException;
	
	import com.fanatics.sterling.constants.FanaticsFraudCheckConstants;
	import com.fanatics.sterling.util.CommonUtil;
	import com.fanatics.sterling.util.FANConstants;
	import com.fanatics.sterling.util.XMLUtil;
	import com.yantra.yfc.log.YFCLogCategory;
	import com.yantra.yfs.japi.YFSEnvironment;
	
	public class AutoResolve {
	
		private static YFCLogCategory logger= YFCLogCategory.instance(YFCLogCategory.class);
		
		public void resolveHolds(YFSEnvironment yfsEnv, Document inXML){
			
			
			logger.verbose("Inside resolveHolds");
			logger.verbose("resolveHolds Input xml is: "+ XMLUtil.getXMLString(inXML));
			
			logger.verbose("resolveAllHolds inXML 1"+ XMLUtil.getXMLString(inXML));
			Document docIPChangeOrder = null;
			Element eleInputChangeOrderRoot = (Element) inXML.getElementsByTagName("Order").item(0);
	
			String strIPChangeOrder = "<Order OrderNo='"+eleInputChangeOrderRoot.getAttribute(FANConstants.ORDER_NO)+"' " +
					"EnterpriseCode='"+eleInputChangeOrderRoot.getAttribute(FANConstants.ATT_EnterpriseCode)+"' " +
					"DocumentType='"+eleInputChangeOrderRoot.getAttribute(FANConstants.ATT_DocumentType)+"'></Order>";
	
			try {
				docIPChangeOrder = XMLUtil.getDocument(strIPChangeOrder);
			 } catch (ParserConfigurationException | SAXException | IOException e1) {
				
				e1.printStackTrace();
			}
	
			logger.verbose("resolveAllHolds docIPChangeOrder is: "+ XMLUtil.getXMLString(docIPChangeOrder));
	
			// call getOrderDetails to get the present holds
	
			String templateGetOrderDetails = "<Order EnterpriseCode=''><OrderHoldTypes><OrderHoldType HoldType='' Status=''></OrderHoldType></OrderHoldTypes></Order>" ;
			Document docGetOrderDetails = null;
			try {
				docGetOrderDetails = XMLUtil.getDocument(templateGetOrderDetails);
			} catch (ParserConfigurationException | SAXException | IOException e1) {
				
				e1.printStackTrace();
			}
	
			Document docOPGetOrderDetails = null;
			try {
				docOPGetOrderDetails = CommonUtil.invokeAPI(yfsEnv, docGetOrderDetails, FANConstants.API_GET_ORDER_DET, docIPChangeOrder);
				logger.verbose("resolveAllHolds docOPGetOrderDetails is: "+ XMLUtil.getXMLString(docOPGetOrderDetails));
			} catch (Exception e) {
	
				e.printStackTrace();
			}
	
			// add OrderHoldTypes Element to the xml
			Element eleOrderHoldTypes = docIPChangeOrder.createElement(FanaticsFraudCheckConstants.ATT_fanatics_OrderHoldTypes);
			Node tempNode = docIPChangeOrder.importNode(eleOrderHoldTypes, false);
			docIPChangeOrder.getDocumentElement().appendChild(tempNode);
			logger.verbose("resolveAllHolds docIPChangeOrder is 11: "+ XMLUtil.getXMLString(docIPChangeOrder));
	
			// get the handle of the OrderHoldTypes element present in the docIPChangeOrder
			eleOrderHoldTypes = (Element) docIPChangeOrder.getDocumentElement().getElementsByTagName(FanaticsFraudCheckConstants.ATT_fanatics_OrderHoldTypes).item(0);
	
			// add the Hold Types (to be resolved) to the changeOrder input xml
			NodeList nlOrderHoldType = docOPGetOrderDetails.getElementsByTagName(FanaticsFraudCheckConstants.ATT_fanatics_OrderHoldType);
			int nlOrderHoldTypeLength = nlOrderHoldType.getLength();
			logger.verbose("nlOrderHoldTypeLength" + nlOrderHoldTypeLength);
			Element eleOrderHoldType = null;
	
			for(int i=0;i<nlOrderHoldTypeLength;i++){
				logger.verbose("inside the loop ");
				eleOrderHoldType = (Element) nlOrderHoldType.item(i);
	
				// add the current hold to the input doc only if the status is created (1100) and the hold is a Buyer's Remorse hold
				logger.verbose("status here is "+ eleOrderHoldType.getAttribute(FANConstants.ATT_STATUS));
				if (eleOrderHoldType.getAttribute(FANConstants.ATT_STATUS).equals(FANConstants.STR_1100) 
						&& eleOrderHoldType.getAttribute(FanaticsFraudCheckConstants.ATT_fanatics_HoldType).equals(FanaticsFraudCheckConstants.CONSTANT_fanatics_BUYERSREMORSE)){
					logger.verbose("inside the if cond ");
					// create new element OrderHoldType
					Element eleIPOrderHoldType = docIPChangeOrder.createElement(FanaticsFraudCheckConstants.ATT_fanatics_OrderHoldType);
					eleIPOrderHoldType.setAttribute(FanaticsFraudCheckConstants.ATT_fanatics_HoldType, eleOrderHoldType.getAttribute(FanaticsFraudCheckConstants.ATT_fanatics_HoldType));
					eleIPOrderHoldType.setAttribute(FANConstants.ATT_STATUS, FANConstants.STR_1300);
	
					tempNode = docIPChangeOrder.importNode(eleIPOrderHoldType, false);
					eleOrderHoldTypes.appendChild(tempNode);
	
					logger.verbose("resolveAllHolds docIPChangeOrder is 12: "+ XMLUtil.getXMLString(docIPChangeOrder));
				}
	
			}
			
			// invoke changeOrder API
			logger.verbose("resolveAllHolds docIPChangeOrder is 13: "+ XMLUtil.getXMLString(docIPChangeOrder));
			try {
				CommonUtil.invokeAPI(yfsEnv, FANConstants.API_CHANGE_ORDER, docIPChangeOrder);
				} catch (Exception e) {
				
				e.printStackTrace();
				}
			
		}
		
	}
