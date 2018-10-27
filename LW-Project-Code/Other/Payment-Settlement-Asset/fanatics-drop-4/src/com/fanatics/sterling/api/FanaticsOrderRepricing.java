package com.fanatics.sterling.api;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fanatics.sterling.constants.FanaticsOrderRepricingConstants;
import com.fanatics.sterling.util.RESTClient;
import com.fanatics.sterling.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

public class FanaticsOrderRepricing {

	private static YFCLogCategory logger= YFCLogCategory.instance(YFCLogCategory.class);
	
	
	public Document OrderRepricing(YFSEnvironment yfsEnv, Document inXML){
		
		Document outDoc =null;;
		try {
			// Invoke the rest service
			outDoc = this.invokeOrderRepricingREST(yfsEnv, inXML);
								
			/*if(outDoc != null){
				this.processOrderPricingResponse(yfsEnv, outDoc);
			}
			else{
				logger.error("Output Document from FanaticsOrderpricing System is null");
			}*/
		} catch (Exception e) {
				e.printStackTrace();
		}
		
		
		return outDoc;
		
	}
	
	public Document invokeOrderRepricingREST (YFSEnvironment yfsEnv, Document inXML){
		
		RESTClient rClient = new RESTClient();
		
		//Get REST connection details
		String authType = YFSSystem.getProperty("fan.rest.authtype"); 
		String baseUrl = YFSSystem.getProperty("fan.rest.base.url"); 
		String username = YFSSystem.getProperty("fan.rest.user"); 
		String password = YFSSystem.getProperty("fan.rest.pwd"); 
		//String path = YFSSystem.getProperty("fan.rest.orderreprice.path"); 
		
		rClient.setAuthType(authType);
		rClient.setBaseUrl(baseUrl);
		rClient.setUsername(username);
		rClient.setPassword(password);
		
		//Create input for rest call
		Document orderRepricingResponseInput = SCXmlUtil.createDocument("Order");

		orderRepricingResponseInput.getDocumentElement().setAttribute("DocumentType", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingDocumentType));
		orderRepricingResponseInput.getDocumentElement().setAttribute("EnterpriseCode", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingEnterpriseCode));
		orderRepricingResponseInput.getDocumentElement().setAttribute("OrderNo", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingOrderNo));
		orderRepricingResponseInput.getDocumentElement().setAttribute("OrderDate", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingOrderDate));
		orderRepricingResponseInput.getDocumentElement().setAttribute("SellerOrganizationCode", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingSellerOrg));
		orderRepricingResponseInput.getDocumentElement().setAttribute("OrderRepricingResponseSender", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingResponseSender));
		orderRepricingResponseInput.getDocumentElement().setAttribute("OrderRepricingResponseCode", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingResponseCode));
		
		//Element eleOrderLineAttr = orderRepricingResponseInput.createElement("OrderLines/OrderLine");
		orderRepricingResponseInput.getDocumentElement().setAttribute("DeliveryMethod", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingCusOrderNo));
		orderRepricingResponseInput.getDocumentElement().setAttribute("SubLineNo", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingSubLineNo));
		orderRepricingResponseInput.getDocumentElement().setAttribute("PrimeLineNo", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingPrimeLineNo));
		orderRepricingResponseInput.getDocumentElement().setAttribute("OriginalOrderedQty", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingOriginalOrderedQty));
		orderRepricingResponseInput.getDocumentElement().setAttribute("OrderedQty", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingOrderedQty));
		orderRepricingResponseInput.getDocumentElement().setAttribute("OtherCharges", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingOtherCharges));
		
		//Element eleOrderLineItemAttr = orderRepricingResponseInput.createElement("Item");
		orderRepricingResponseInput.getDocumentElement().setAttribute("ItemID", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingITEMID));
		orderRepricingResponseInput.getDocumentElement().setAttribute("UnitOfMeasure", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingUnitOfMeasure));
		orderRepricingResponseInput.getDocumentElement().setAttribute("UnitCost", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingUnitCost));
		orderRepricingResponseInput.getDocumentElement().setAttribute("ItemShortDesc", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingItemShortDesc));
		orderRepricingResponseInput.getDocumentElement().setAttribute("CostCurrency", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingCostCurrency));
				
		
		Element eleOrderRepricingResponse = orderRepricingResponseInput.getDocumentElement();
		Element eleCustomAttr = orderRepricingResponseInput.createElement("CustomAttributes");
		eleCustomAttr.setAttribute("CustomerOrderNo", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingCusOrderNo));
		eleOrderRepricingResponse.appendChild(eleCustomAttr);


		String restOutputStr = "";
		Document restOutputDoc = null;
		//Make REST call
		try{
		//	restOutputStr = rClient.postDataToServer(path, XMLUtil.getXMLString(orderRepricingResponseInput));
			restOutputStr = XMLUtil.getXMLString(this.postDataToStub(yfsEnv, orderRepricingResponseInput));
			
		}catch(Exception e){
			logger.error("Exception making REST call to order Reprice: " + e.getMessage() , e.getCause());
		}
		
		//Convert string to document
		try {
			restOutputDoc = XMLUtil.getDocument(restOutputStr);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			logger.error("Exception coverting REST response to XML: " + e.getMessage() , e.getCause());
		}
		
		
		return restOutputDoc;	
	}
	
	public Document processOrderPricingResponse(YFSEnvironment yfsEnv, Document outDoc) throws ParserConfigurationException{
		
		
		logger.verbose("Inside processOrderPricingResponse");
		logger.verbose("processOrderPricingResponse Input xml is 1: "+ XMLUtil.getXMLString(outDoc));
		Document orderRepricingResponseOutput = SCXmlUtil.createDocument("Order");

		orderRepricingResponseOutput.getDocumentElement().setAttribute("DocumentType", XMLUtil.getXpathProperty(outDoc, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingDocumentType));
		orderRepricingResponseOutput.getDocumentElement().setAttribute("EnterpriseCode", XMLUtil.getXpathProperty(outDoc, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingEnterpriseCode));
		orderRepricingResponseOutput.getDocumentElement().setAttribute("OrderNo", XMLUtil.getXpathProperty(outDoc, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingOrderNo));
		orderRepricingResponseOutput.getDocumentElement().setAttribute("OrderDate", XMLUtil.getXpathProperty(outDoc, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingOrderDate));
		orderRepricingResponseOutput.getDocumentElement().setAttribute("SellerOrganizationCode", XMLUtil.getXpathProperty(outDoc, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingSellerOrg));
		orderRepricingResponseOutput.getDocumentElement().setAttribute("OrderRepricingResponseSender", XMLUtil.getXpathProperty(outDoc, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingResponseSender));
		orderRepricingResponseOutput.getDocumentElement().setAttribute("OrderRepricingResponseCode", XMLUtil.getXpathProperty(outDoc, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingResponseCode));
		
		//Element eleOrderLineAttr = orderRepricingResponseInput.createElement("OrderLines/OrderLine");
		orderRepricingResponseOutput.getDocumentElement().setAttribute("DeliveryMethod", XMLUtil.getXpathProperty(outDoc, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingCusOrderNo));
		orderRepricingResponseOutput.getDocumentElement().setAttribute("SubLineNo", XMLUtil.getXpathProperty(outDoc, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingSubLineNo));
		orderRepricingResponseOutput.getDocumentElement().setAttribute("PrimeLineNo", XMLUtil.getXpathProperty(outDoc, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingPrimeLineNo));
		orderRepricingResponseOutput.getDocumentElement().setAttribute("OriginalOrderedQty", XMLUtil.getXpathProperty(outDoc, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingOriginalOrderedQty));
		orderRepricingResponseOutput.getDocumentElement().setAttribute("OrderedQty", XMLUtil.getXpathProperty(outDoc, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingOrderedQty));
		orderRepricingResponseOutput.getDocumentElement().setAttribute("OtherCharges", XMLUtil.getXpathProperty(outDoc, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingOtherCharges));
		
		//Element eleOrderLineItemAttr = orderRepricingResponseInput.createElement("Item");
		orderRepricingResponseOutput.getDocumentElement().setAttribute("ItemID", XMLUtil.getXpathProperty(outDoc, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingCusOrderNo));
		orderRepricingResponseOutput.getDocumentElement().setAttribute("UnitOfMeasure", XMLUtil.getXpathProperty(outDoc, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingUnitOfMeasure));
		orderRepricingResponseOutput.getDocumentElement().setAttribute("UnitCost", XMLUtil.getXpathProperty(outDoc, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingUnitCost));
		orderRepricingResponseOutput.getDocumentElement().setAttribute("ItemShortDesc", XMLUtil.getXpathProperty(outDoc, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingItemShortDesc));
		orderRepricingResponseOutput.getDocumentElement().setAttribute("CostCurrency", XMLUtil.getXpathProperty(outDoc, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingCostCurrency));
				
		orderRepricingResponseOutput.getDocumentElement().setAttribute("UnitPrice", XMLUtil.getXpathProperty(outDoc, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingLinePriceInfoUnitPrice));
		orderRepricingResponseOutput.getDocumentElement().setAttribute("UnitOfMeasure", XMLUtil.getXpathProperty(outDoc, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingUnitOfMeasure));
		
		
		Element eleOrderRepricingResponse = orderRepricingResponseOutput.getDocumentElement();
		Element eleCustomAttr = orderRepricingResponseOutput.createElement("CustomAttributes");
		eleCustomAttr.setAttribute("CustomerOrderNo", XMLUtil.getXpathProperty(outDoc, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingCusOrderNo));
		eleOrderRepricingResponse.appendChild(eleCustomAttr);

		return orderRepricingResponseOutput;
		

		
		
		}
	
	public Document postDataToStub (YFSEnvironment yfsEnv, Document inXML) throws ParserConfigurationException{
		
		logger.verbose("Inside stubOrderPricingResponse");
		logger.verbose("stubsOrderPricingResponse Input xml is 1: "+ XMLUtil.getXMLString(inXML));
		Document orderRepricingStubResponseOutput = SCXmlUtil.createDocument("Order");
		
		orderRepricingStubResponseOutput.getDocumentElement().setAttribute("DocumentType", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingDocumentType));
		orderRepricingStubResponseOutput.getDocumentElement().setAttribute("EnterpriseCode", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingEnterpriseCode));
		orderRepricingStubResponseOutput.getDocumentElement().setAttribute("OrderNo", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingOrderNo));
		orderRepricingStubResponseOutput.getDocumentElement().setAttribute("OrderDate", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingOrderDate));
		orderRepricingStubResponseOutput.getDocumentElement().setAttribute("SellerOrganizationCode", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingSellerOrg));
		
		Element eleOrderRepricingStubOrderLinesResponse = orderRepricingStubResponseOutput.getDocumentElement();
		Element eleOrderLinesAttr = orderRepricingStubResponseOutput.createElement("OrderLines");
		Element rootMessageNode = inXML.getDocumentElement();
		
		if (rootMessageNode != null) {
			
			NodeList orderLines = rootMessageNode.getElementsByTagName("OrderLine");
				
			for (int i = 0; i < orderLines.getLength(); i++) {
				Element eleOrderLineAttr = orderRepricingStubResponseOutput.createElement("OrderLine");
				Element eleOrderLineItemAttr = orderRepricingStubResponseOutput.createElement("Item");
				eleOrderLineItemAttr.setAttribute("ItemID", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingITEMID));
				eleOrderLineItemAttr.setAttribute("UnitOfMeasure", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingUnitOfMeasure));
				eleOrderLineItemAttr.setAttribute("UnitCost", "5.00");
				eleOrderLineItemAttr.setAttribute("ItemShortDesc", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingItemShortDesc));
				eleOrderLineItemAttr.setAttribute("CostCurrency", XMLUtil.getXpathProperty(inXML, FanaticsOrderRepricingConstants.XPATH_fanatics_OrderRepricingCostCurrency));
				eleOrderLineAttr.appendChild(eleOrderLineItemAttr);
		
				Element eleOrderLinePirceInfoAttr = orderRepricingStubResponseOutput.createElement("LinePriceInfo");
				eleOrderLinePirceInfoAttr.setAttribute("UnitPrice", "1.00");
				eleOrderLineAttr.appendChild(eleOrderLinePirceInfoAttr);
				
				eleOrderLinesAttr.appendChild(eleOrderLineAttr);
				eleOrderRepricingStubOrderLinesResponse.appendChild(eleOrderLinesAttr);
			}
		}
		logger.verbose("stubsOrderPricingResponse Input xml is 2: "+ orderRepricingStubResponseOutput);
	
		return orderRepricingStubResponseOutput;
		
	}

	}



	



