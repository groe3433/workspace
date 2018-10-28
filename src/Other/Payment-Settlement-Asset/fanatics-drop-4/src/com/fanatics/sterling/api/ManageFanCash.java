package com.fanatics.sterling.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fanatics.sterling.constants.ManageFanCashConstants;
import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.RESTClient;
import com.fanatics.sterling.util.XMLUtil;
import com.fanatics.sterling.util.XPathUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

public class ManageFanCash {
	private static YFCLogCategory logger= YFCLogCategory.instance(YFCLogCategory.class);
	
	public Document invokeFanCashRESTStub(YFSEnvironment yfsEnv, Document inXML) throws Exception{
		logger.debug("----------Input to invokeFanCashRESTStub---------"+XMLUtil.getXMLString(inXML));
		return inXML;
	}
	public Document invokeFanCashRevAuthRESTStub(YFSEnvironment yfsEnv, Document inXML)throws Exception{
		logger.debug("----------Input to invokeFanCashRESTStub---------"+XMLUtil.getXMLString(inXML));
		return inXML;
	}



	public Document invokeFanCashREST(YFSEnvironment yfsEnv, Document inXML) throws Exception{
		
		logger.debug("----------Input to invokeFanCashREST---------"+XMLUtil.getXMLString(inXML));
		
		RESTClient rClient = new RESTClient();
		
		//Get REST connection details
		String authType = YFSSystem.getProperty("fan.rest.fancash.authtype"); 
		String baseUrl = YFSSystem.getProperty("fan.rest.fancash.base.url"); 
		String username = YFSSystem.getProperty("fan.rest.fancash.user"); 
		String password = YFSSystem.getProperty("fan.rest.fancash.pwd"); 
		String path = YFSSystem.getProperty("fan.rest.fancash.path"); 
		
		rClient.setAuthType(authType);
		rClient.setBaseUrl(baseUrl);
		rClient.setUsername(username);
		rClient.setPassword(password);


		String restOutputStr = "";
		Document restOutputDoc = null;
		
		//Make REST call
		try{
			restOutputStr = rClient.postDataToServer(path, XMLUtil.getXMLString(inXML));
			
			if(restOutputStr == null){
				logger.error("Output Document from callREST is null");
			}
			
			
		}catch(Exception e){
			logger.error("Exception making REST call to payment service: " + e.getMessage() , e.getCause());
		}
		
		//Convert string to document
		try {
			restOutputDoc = XMLUtil.getDocument(restOutputStr);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			logger.error("Exception coverting REST response to XML: " + e.getMessage() , e.getCause());
		}
		
		
	logger.debug("----------Output to FANCallRESTServiceForPayment---------"+XMLUtil.getXMLString(restOutputDoc));
		
	return restOutputDoc;
	}
	
	public Document invokeFanCashRevAuthREST(YFSEnvironment yfsEnv, Document inXML)throws Exception{
		
		logger.debug("----------Input to invokeFanCashREST---------"+XMLUtil.getXMLString(inXML));
		
		RESTClient rClient = new RESTClient();
		
		//Get REST connection details
		String authType = YFSSystem.getProperty("fan.rest.fancashrevauth.authtype"); 
		String baseUrl = YFSSystem.getProperty("fan.rest.fancashrevauth.base.url"); 
		String username = YFSSystem.getProperty("fan.rest.fancashrevauth.user"); 
		String password = YFSSystem.getProperty("fan.rest.fancashrevauth.pwd"); 
		String path = YFSSystem.getProperty("fan.rest.fancashrevauth.path"); 
		
		rClient.setAuthType(authType);
		rClient.setBaseUrl(baseUrl);
		rClient.setUsername(username);
		rClient.setPassword(password);


		String restOutputStr = "";
		Document restOutputDoc = null;
		
		//Make REST call
		try{
			restOutputStr = rClient.postDataToServer(path, XMLUtil.getXMLString(inXML));
			
			if(restOutputStr == null){
				logger.error("Output Document from callREST is null");
			}
			
			
		}catch(Exception e){
			logger.error("Exception making REST call to payment service: " + e.getMessage() , e.getCause());
		}
		
		//Convert string to document
		try {
			restOutputDoc = XMLUtil.getDocument(restOutputStr);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			logger.error("Exception coverting REST response to XML: " + e.getMessage() , e.getCause());
		}
		
		
	logger.debug("----------Output to FANCallRESTServiceForPayment---------"+XMLUtil.getXMLString(restOutputDoc));
		
	return restOutputDoc;	}
	
	public Document manageFanCashRequest(YFSEnvironment env, Document inDoc) throws Exception{
		logger.verbose("----------Begin ManageFanCash.manageFanCashRequest ----------------");
		logger.verbose("----------Input to manageFanCashRequest---------"+XMLUtil.getXMLString(inDoc));
		
		boolean checkReturns = false;
		
			logger.verbose("The Document is not a return");

			Boolean wasAuthorized = getPaymentStatus(inDoc);

			//If fancash was Authorized then do following
			if(wasAuthorized){
			String proRataAmount = calculateProRata(env, inDoc);
				
				if(!proRataAmount.equals("0") || !proRataAmount.equals(null)){
					
					Document revAuthIP = createRevREST(inDoc,proRataAmount);
					Document outDoc = CommonUtil.invokeService(env, ManageFanCashConstants.FANCASH_SERVICE_FANATICSFANCASHREVAUTHREST, revAuthIP);
				}
			}
			
	
			//Check if orderlines SchedFailureReasonCode="NOT_ENOUGH_PRODUCT_CHOICES"
			Boolean wasCanceledFromSchedule = false;
			wasCanceledFromSchedule = getWasCanceledFromSchedule(env, inDoc);
			
			if(wasCanceledFromSchedule){
				//Canceled from schedule so setting reasonCode
				String reason = "NOT_ENOUGH_PRODUCT_CHOICES";
				
				//get the fixed amount for fancash returns per org
				String sellerOrg = XPathUtil.getXpathAttribute(inDoc, ManageFanCashConstants.FANCASH_XPATH_SELLERORGANIZATIONCODE);			
				String enterpriseOrg = XPathUtil.getXpathAttribute(inDoc, ManageFanCashConstants.FANCASH_XPATH_ENTERPRISECODE);			

				Document commonCodeDoc = XMLUtil.createDocument("CommonCode");
				Element ordercommonCodeEle = commonCodeDoc.getDocumentElement();
				ordercommonCodeEle.setAttribute("CodeType", "FanCashOrg");
				ordercommonCodeEle.setAttribute("CodeValue", sellerOrg);
				
				Document commonCodeOP = CommonUtil.invokeAPI(env,"getCommonCodeList", commonCodeDoc);
				String sellerAmount = XPathUtil.getXpathAttribute(inDoc, ManageFanCashConstants.FANCASH_XPATH_COMMONCODESHORTDESC);		
				logger.debug("Seller FanCash amount: " +sellerAmount);

				if (sellerAmount == null || sellerAmount =="") {
					ordercommonCodeEle.setAttribute("CodeValue", enterpriseOrg);
					commonCodeOP = CommonUtil.invokeAPI	(env,"getCommonCodeList", commonCodeDoc);
					sellerAmount = XPathUtil.getXpathAttribute(inDoc, ManageFanCashConstants.FANCASH_XPATH_COMMONCODESHORTDESC);
					logger.debug("Enterprise FanCash amount: " +sellerAmount);
					}
				//create request
				Document requestInDoc = createRESTRequest(env, inDoc, sellerAmount, reason);
				Document outDoc = CommonUtil.invokeService(env, ManageFanCashConstants.FANCASH_SERVICE_FANATICSFANCASHREST, requestInDoc);

			} else {
				
				String csrReason = null;
				csrReason = getCSRReason(inDoc);
				
				if(!csrReason.equals(null) || !csrReason.equals("")){
		
					String regExpFanCash = " ";
					String[] fanCashAmount= csrReason.split(regExpFanCash);
					String amount = fanCashAmount[0];
					logger.verbose("fanCashAmount - " +amount);

					String regExpReasonCode = "'";					
					String[] fanCashReasonCode= csrReason.split(regExpReasonCode);
					String reasonCode = fanCashReasonCode[1];
					logger.verbose("fanCashReasonCode - " +reasonCode);

					Document requestInDoc = createRESTRequest(env, inDoc, amount, reasonCode);
					Document outDoc = CommonUtil.invokeService(env, ManageFanCashConstants.FANCASH_SERVICE_FANATICSFANCASHREST, requestInDoc);

					}
				}
			return inDoc; 
			}
	
	private Document createRevREST(Document inDoc, String proRataAmount) throws Exception{
		logger.verbose("Start createRevREST - " +XMLUtil.getXMLString(inDoc));
		logger.verbose("proRataAmount - " +proRataAmount);
		
		Document fanCashDoc = XMLUtil.createDocument("FanCash");
		Element fanCashDocEle = fanCashDoc.getDocumentElement();
		fanCashDocEle.setAttribute("BillToID", XPathUtil.getXpathAttribute(inDoc, ManageFanCashConstants.FANCASH_XPATH_BILLTOID));
		fanCashDocEle.setAttribute("RevAuth","Y");
		fanCashDocEle.setAttribute("AmountToRevAuth", proRataAmount);

		Element fanCashOrderEle = fanCashDoc.createElement("Order");
		fanCashOrderEle.setAttribute("OrderNo", XPathUtil.getXpathAttribute(inDoc, ManageFanCashConstants.FANCASH_XPATH_ORDERNO));
		fanCashDocEle.appendChild(fanCashOrderEle);
		
		
		logger.verbose("Exit fanCashDoc - " +XMLUtil.getXMLString(fanCashDoc));

		return fanCashDoc;
	}

	private String getCSRReason(Document inDoc) throws Exception{
		
		logger.verbose("Begin getCSRReason inDoc - " +XMLUtil.getXMLString(inDoc));
		
		String csrReason = XPathUtil.getXpathAttribute(inDoc, ManageFanCashConstants.FANCASH_XPATH_CSRREASONCODE);			


		logger.verbose("End getCSRReason - csrReason - " +csrReason);

		
		return csrReason;
	}

	private Document createRESTRequest(YFSEnvironment env, Document inDoc, String sellerAmount, String reason) throws Exception{
		logger.verbose("Start createRESTRequest - " +XMLUtil.getXMLString(inDoc));
		logger.verbose("sellerAmount - " +sellerAmount);
		logger.verbose("reason - " +reason);


		String orderNo = XPathUtil.getXpathAttribute(inDoc, ManageFanCashConstants.FANCASH_XPATH_ORDERNO);			
		String CustID = XPathUtil.getXpathAttribute(inDoc, ManageFanCashConstants.FANCASH_XPATH_BILLTOID);			
		String sellerOrg = XPathUtil.getXpathAttribute(inDoc, ManageFanCashConstants.FANCASH_XPATH_SELLERORGANIZATIONCODE);			
		
		Document getOrderDetailsOP = getOrderDetails(env, orderNo);
		String customerRewardsNo = XPathUtil.getXpathAttribute(getOrderDetailsOP, ManageFanCashConstants.FANCASH_XPATH_CUSTOMERREWARDSNO);			


		Document fanCashDoc = XMLUtil.createDocument("FanCash");
		Element fanCashDocEle = fanCashDoc.getDocumentElement();
		fanCashDocEle.setAttribute("CustomerID", CustID);
		fanCashDocEle.setAttribute("TransactionAmount", sellerAmount);
		fanCashDocEle.setAttribute("ReasonCode", reason);
		fanCashDocEle.setAttribute("OrderNo", orderNo);
		fanCashDocEle.setAttribute("CustomerRewardsNo", customerRewardsNo);

		logger.verbose("End createRESTRequest - fanCashDoc - " +XMLUtil.getXMLString(fanCashDoc));

		return fanCashDoc;
	}
	


	private Boolean getWasCanceledFromSchedule(YFSEnvironment env, Document inDoc) throws Exception {
		logger.verbose("Start getWasCanceledFromSchedule- inDoc- " +XMLUtil.getXMLString(inDoc));

		String orderNo = XPathUtil.getXpathAttribute(inDoc, ManageFanCashConstants.FANCASH_XPATH_ORDERNO);
		
		Document outDoc = getOrderDetails(env,orderNo);
		
		NodeList OrderLineList = outDoc.getElementsByTagName("OrderLine");
		int numOrderLinenodes = OrderLineList.getLength();
		logger.verbose("num of OrderLine nodes- " + numOrderLinenodes);
		
		for (int i=0;i < numOrderLinenodes; i++) {
			logger.verbose("i - " + i);
			
			Element orderLine = (Element) OrderLineList.item(i);
			String schedFailureReasonCode = orderLine.getAttribute("SchedFailureReasonCode");
			
			if(schedFailureReasonCode.equals("NOT_ENOUGH_PRODUCT_CHOICES")){
				logger.verbose("schedFailureReasonCode is " + schedFailureReasonCode);
				logger.verbose("End getWasCanceledFromSchedule");

				return true;
			}
			
		}
		logger.verbose("End getWasCanceledFromSchedule RETURN FALSE");
		return false;
	}
	

	
	private Boolean getPaymentStatus(Document inDoc) throws Exception{
		logger.verbose("Start getPaymentStatus");

		String paymentStatus = XPathUtil.getXpathAttribute(inDoc, ManageFanCashConstants.FANCASH_XPATH_PAYMENTSTATUS);
		logger.verbose("paymentStatus = " +paymentStatus);

		if (paymentStatus.equals("AUTHORIZED")){
			logger.verbose("End getPaymentStatus return TRUE");
			return true;
		}else{
			logger.verbose("End getPaymentStatus return FALSE");
			return false;
		}
	}

	public String calculateProRata(YFSEnvironment env, Document inDoc) throws Exception{
		logger.verbose("Start calculateProRata- inDoc- " +XMLUtil.getXMLString(inDoc));

		String orderNo = XPathUtil.getXpathAttribute(inDoc, ManageFanCashConstants.FANCASH_XPATH_ORDERNO);
				
		Document outDoc = getOrderDetails(env,orderNo);

		NodeList OrderLineList = outDoc.getElementsByTagName("OrderLine");
		int numOrderLinenodes = OrderLineList.getLength();
		logger.verbose("num of OrderLine nodes- " + numOrderLinenodes);
		
		float totalProRata=0;
		
		for (int i=0;i < numOrderLinenodes; i++) {
			logger.verbose("i - " + i);
			
			Element orderLine = (Element)OrderLineList.item(i);
			Element lineCharges = (Element)orderLine.getElementsByTagName("LineCharges").item(0);
			NodeList lineChargeList = orderLine.getElementsByTagName("LineCharge");
			
			String originalOrderedQty = orderLine.getAttribute("OriginalOrderedQty");
			float iOriginalOrderedQty = Float.parseFloat(originalOrderedQty);
			
			
			for (int j=0;j < lineChargeList.getLength(); j++) {
				Element lineCharge = (Element) lineChargeList.item(j);

				String chargeName = lineCharge.getAttribute("ChargeName");
				logger.verbose("chargeName- " + chargeName);
				
				if (chargeName.equals(ManageFanCashConstants.FANCASH_XPATH_CHARGENAME)){
					//get charge amount that is fancash
					String fanChargeAmount = lineCharge.getAttribute("ChargeAmount");
					float iFanChargeAmount = Float.parseFloat(fanChargeAmount);

					logger.verbose("fanChargeAmount- " + fanChargeAmount);
					
					//as line had a fancash amount then check the amount of items that were cancelled
					Element orderStatuses = (Element)orderLine.getElementsByTagName("OrderStatuses").item(0);
					NodeList orderStatusList = (NodeList) orderLine.getElementsByTagName("OrderStatus");

					for (int k=0;k < orderStatusList.getLength(); k++) {
						Element orderStatus = (Element) orderStatusList.item(k);
						String status = lineCharge.getAttribute("Status");
						if (status.equals("9000")){
							//total quantity cancelled for this line
							String statusQty = lineCharge.getAttribute("StatusQty");
							int iStatusQty = Integer.parseInt(statusQty);

							
							float ifancashPerItem = iFanChargeAmount/iOriginalOrderedQty;
							float iProRata = ifancashPerItem * iStatusQty;
							totalProRata  = totalProRata + iProRata;
							logger.verbose("iProRata- " + iProRata);
							logger.verbose("totalProRata- " + totalProRata);
						}
					}
				}
			}
		}

		String proRata = String.valueOf(totalProRata);
		logger.verbose("The total ProRata of all lines is- " + totalProRata);
		//String finalProRata = "+" +proRata;
		logger.verbose("End calculateProRata");

		return proRata;	
		
	}
	
	
	private Document getOrderDetails(YFSEnvironment env, String orderNo) throws Exception{
		logger.verbose("Start getOrderDetails- inDoc- " +orderNo);

		Document getOrderListInDoc = XMLUtil.createDocument("Order");
		Element orderListEle = getOrderListInDoc.getDocumentElement();
		orderListEle.setAttribute("OrderNo", orderNo);
		
		logger.verbose("--------------manageFanCashRequest------getOrderList--ip--"+XMLUtil.getXMLString(getOrderListInDoc));
	
		Document outDoc = CommonUtil.invokeAPI(env,"global/template/api/getOrderList_fancash.xml","getOrderList", getOrderListInDoc);
	
		logger.verbose("--------------manageFanCashRequest------getOrderList--op--"+XMLUtil.getXMLString(outDoc));

		return outDoc;
	}

	
}
