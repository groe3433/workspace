package com.fanatics.sterling.api;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fanatics.sterling.constants.EmailComunicationConstants;
import com.fanatics.sterling.constants.FanaticsFraudCheckConstants;
import com.fanatics.sterling.constants.OrderNotesConstants;
import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.FANConstants;
import com.fanatics.sterling.util.FANDBUtil;
import com.fanatics.sterling.util.FANDateUtils;
import com.fanatics.sterling.util.RESTClient;
import com.fanatics.sterling.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

public class FraudCheck {

	private static YFCLogCategory logger= YFCLogCategory.instance(YFCLogCategory.class);
	
	
	public Document initiateFraudCheck(YFSEnvironment yfsEnv, Document inXML){
		
		Document outDoc =null;;
		try {
			// Invoke the rest service
			outDoc = CommonUtil.invokeService(yfsEnv, FanaticsFraudCheckConstants.SERVICE_fanatics_FanaticsFraudCheckREST, inXML);
						
			if(outDoc != null){
				CommonUtil.invokeService(yfsEnv, FanaticsFraudCheckConstants.SERVICE_fanatics_FanSendFraudResponse, outDoc);
			}
			else{
				logger.error("Output Document from FanaticsFraudCheckREST is null");
			}
		} catch (Exception e) {
				e.printStackTrace();
		}
		
		
		return outDoc;
	}
	
	public Document invokeFraudRESTCheck(YFSEnvironment yfsEnv, Document inXML){
		
		RESTClient rClient = new RESTClient();
		
		//Get REST connection details
		String authType = YFSSystem.getProperty("fan.rest.authtype"); 
		String baseUrl = YFSSystem.getProperty("fan.rest.base.url"); 
		String username = YFSSystem.getProperty("fan.rest.user"); 
		String password = YFSSystem.getProperty("fan.rest.pwd"); 
		String path = YFSSystem.getProperty("fan.rest.fraud.path"); 
		
		rClient.setAuthType(authType);
		rClient.setBaseUrl(baseUrl);
		rClient.setUsername(username);
		rClient.setPassword(password);
		
		//Create input for rest call
		Document fraudResponseInput = SCXmlUtil.createDocument("Order");

		fraudResponseInput.getDocumentElement().setAttribute("DocumentType", XMLUtil.getXpathProperty(inXML, FanaticsFraudCheckConstants.XPATH_fanatics_FraudDocumentType));
		fraudResponseInput.getDocumentElement().setAttribute("EnterpriseCode", XMLUtil.getXpathProperty(inXML, FanaticsFraudCheckConstants.XPATH_fanatics_FraudEnterpriseCode));
		fraudResponseInput.getDocumentElement().setAttribute("OrderNo", XMLUtil.getXpathProperty(inXML, FanaticsFraudCheckConstants.XPATH_fanatics_FraudOrderNo));
		fraudResponseInput.getDocumentElement().setAttribute("OrderDate", XMLUtil.getXpathProperty(inXML, FanaticsFraudCheckConstants.XPATH_fanatics_FraudOrderDate));
		fraudResponseInput.getDocumentElement().setAttribute("SellerOrganizationCode", XMLUtil.getXpathProperty(inXML, FanaticsFraudCheckConstants.XPATH_fanatics_FraudSellerOrg));
		fraudResponseInput.getDocumentElement().setAttribute("FraudResponseSender", XMLUtil.getXpathProperty(inXML, FanaticsFraudCheckConstants.XPATH_fanatics_FraudResponseSender));
		fraudResponseInput.getDocumentElement().setAttribute("FraudResponseCode", XMLUtil.getXpathProperty(inXML, FanaticsFraudCheckConstants.XPATH_fanatics_FraudResponseCode));
		
		Element eleFraudResponse = fraudResponseInput.getDocumentElement();
		Element eleCustomAttr = fraudResponseInput.createElement("CustomAttributes");
		eleCustomAttr.setAttribute("CustomerOrderNo", XMLUtil.getXpathProperty(inXML, FanaticsFraudCheckConstants.XPATH_fanatics_FraudCusOrderNo));
		eleCustomAttr.setAttribute("FraudToken", XMLUtil.getXpathProperty(inXML, FanaticsFraudCheckConstants.XPATH_fanatics_FraudTokenOMS));
		eleFraudResponse.appendChild(eleCustomAttr);


		String restOutputStr = "";
		Document restOutputDoc = null;
		//Make REST call
		try{
			restOutputStr = rClient.postDataToServer(path, XMLUtil.getXMLString(fraudResponseInput));
		}catch(Exception e){
			logger.error("Exception making REST call to fraud: " + e.getMessage() , e.getCause());
		}
		
		//Convert string to document
		try {
			restOutputDoc = XMLUtil.getDocument(restOutputStr);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			logger.error("Exception coverting REST response to XML: " + e.getMessage() , e.getCause());
		}
		
		
		return restOutputDoc;	
	}
	
	public Document processFraudResponse(YFSEnvironment yfsEnv, Document inXML) throws ParserConfigurationException{

		logger.verbose("Inside processFraudResponse");
		logger.verbose("processFraudResponse Input xml is 1: "+ XMLUtil.getXMLString(inXML));

		// Make REST call to the Fraud Engine
//		RESTClient restObj = new RESTClient();
//		
//		String fanFraudEngineBaseUrl = YFSSystem.getProperty(FanaticsFraudCheckConstants.FRAUD_PROPS_fanatics_BASE_URL); 
//		String fanFraudEngineUser =  YFSSystem.getProperty(FanaticsFraudCheckConstants.FRAUD_PROPS_fanatics_USERNAME);
//		String fanFraudEnginePwd =  YFSSystem.getProperty(FanaticsFraudCheckConstants.FRAUD_PROPS_fanatics_PASSWORD);
//		
//		restObj.setBaseUrl(fanFraudEngineBaseUrl); //("https://192.168.56.101:9443");
//		restObj.setUsername(fanFraudEngineUser); //("admin");
//		restObj.setPassword(fanFraudEnginePwd); //("password");

		Document docResponseFanFraudEngine = null;

		/**
		 * REST call starts
		 */
//		try {
//			strFraudResponse = restObj.putDataToServer(FanaticsFraudCheckConstants.FRAUD_PROPS_fanatics_PATH, inXML.toString());
//
//
//			if (!strFraudResponse.equals(""))
//				docResponseFanFraudEngine = convertStringToDocument(strFraudResponse);
//			else {
//
//				logger.info("Fraud Engine is down");
//				Exception e = new Exception();
//				throw e;
//			}
//
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
		/**
		 * REST call ends
		 */		
			docResponseFanFraudEngine = inXML;
		
		// fetch the root element from the fraud Response
		Element eleFraudResponseRoot = docResponseFanFraudEngine.getDocumentElement();

		// check the sender information
		String strSender = eleFraudResponseRoot.getAttribute(FanaticsFraudCheckConstants.ATT_fanatics_Sender);
		logger.verbose("Sender "+ strSender);

		logger.verbose("FanaticsFraudCheckConstants.CONSTANT_fanatics_Accertify is "+FanaticsFraudCheckConstants.CONSTANT_fanatics_Accertify);
		logger.verbose("condition is "+ strSender.equals(FanaticsFraudCheckConstants.CONSTANT_fanatics_Accertify));
		
		// if its a secondary response
		if (strSender.equals(FanaticsFraudCheckConstants.CONSTANT_fanatics_Accertify)){

			// call getOrderDetails to fetch the token
			String strInputGetOrderDet = "<Order OrderNo='"+eleFraudResponseRoot.getAttribute(FANConstants.ORDER_NO)+"' " +
					"EnterpriseCode='"+eleFraudResponseRoot.getAttribute(FANConstants.ATT_EnterpriseCode)+"' " +
					"DocumentType='"+eleFraudResponseRoot.getAttribute(FANConstants.ATT_DocumentType)+"'/>";
			logger.verbose("inputXML loop : "+ strInputGetOrderDet);
			Document docInputGetOrderDet = null;
			try 
			{  
				docInputGetOrderDet = XMLUtil.getDocument(strInputGetOrderDet);
			} catch (Exception e) {  
				e.printStackTrace();  
			} 

			String templateInputGetOrderDet = "<Order OrderName='' Status=''><CustomAttributes FraudToken='' /></Order>" ; // OrderName is temporarily being used to store the token from the Fraud Engine
			
			Document docTemplateGetOrderDet = null;
			try 
			{  
				docTemplateGetOrderDet = XMLUtil.getDocument(templateInputGetOrderDet);
			} catch (Exception e) {  
				e.printStackTrace();  
			} 
			
			logger.verbose("docTemplateGetOrderDet "+ XMLUtil.getXMLString(docTemplateGetOrderDet));
			// invoke getOrderDetails
			Document docOutputGetOrderDet = null;
			try {
				docOutputGetOrderDet = CommonUtil.invokeAPI(yfsEnv, docTemplateGetOrderDet, FANConstants.API_GET_ORDER_DET, docInputGetOrderDet);
				logger.verbose("docOutputGetOrderDet "+ XMLUtil.getXMLString(docOutputGetOrderDet));

			} catch (Exception e) {

				e.printStackTrace();
			}

			// process this response only of the token received in the response matches the one on the order already and the order is not in the cancelled state
			Element eleOutputGetOrderDetRoot = docOutputGetOrderDet.getDocumentElement();
			String strFraudTokenOPGetOrdDet = XMLUtil.getXpathProperty(docOutputGetOrderDet, FanaticsFraudCheckConstants.XPATH_fanatics_FraudTokenOMS);
			logger.verbose("strFraudTokenOPGetOrdDet is "+ strFraudTokenOPGetOrdDet);

			// Fraud Token from the Fraud Engine
			String strFraudTokenResponseDoc = XMLUtil.getXpathProperty(docResponseFanFraudEngine,FanaticsFraudCheckConstants.XPATH_fanatics_FraudTokenFraudEngine);
			logger.verbose("strFraudTokenResponseDoc is "+strFraudTokenResponseDoc);
			
			logger.verbose(" cond 1 "+ !FANConstants.CANCELLED_STATUSES_DESCRIPTION.equals(eleOutputGetOrderDetRoot.getAttribute(FANConstants.ATT_STATUS)));
			logger.verbose("cond 2 "+ strFraudTokenOPGetOrdDet.equals(strFraudTokenResponseDoc));

			if (!FANConstants.CANCELLED_STATUSES_DESCRIPTION.equals(eleOutputGetOrderDetRoot.getAttribute(FANConstants.ATT_STATUS)) 
					&& strFraudTokenOPGetOrdDet.equals(strFraudTokenResponseDoc)) {
				logger.verbose("1 is true");
				processResponse(yfsEnv, docResponseFanFraudEngine);

			}

		}else{
			logger.verbose("2 is true");
			processResponse(yfsEnv, docResponseFanFraudEngine);

		}

		return inXML;


	}



	private void processResponse(YFSEnvironment yfsEnv, Document inXML) {

		logger.verbose("processResponse inXML 1"+ XMLUtil.getXMLString(inXML));
		Document docIPChangeOrder = null;
		Element eleInputChangeOrderRoot = inXML.getDocumentElement();
		// check the Fraud Response Code
		String strResponseCode = inXML.getDocumentElement().getAttribute(FanaticsFraudCheckConstants.ATT_fanatics_FraudResponseCode);
		logger.verbose("strResponseCode "+strResponseCode);
		
		String strResponseSender = inXML.getDocumentElement().getAttribute(FanaticsFraudCheckConstants.ATT_fanatics_Sender);
		logger.verbose("strResponseSender "+strResponseSender);
		
		String strCurrentDBDateTime = getDateTime(yfsEnv);
		logger.verbose("strCurrentDBDateTime "+strCurrentDBDateTime);
		
		if (strResponseCode.equals(FANConstants.CONSTANT_ZERO)) {

			logger.verbose("response is accept");
			// Resolve all holds 
			docIPChangeOrder = resolveAllHolds(yfsEnv, resolveAllHoldsInput(inXML));

			logger.verbose("docIPChangeOrder inXML 1"+ XMLUtil.getXMLString(docIPChangeOrder));

			// stampOrder with System Date-Time and the user who resolve this hold
			docIPChangeOrder = stampDateTimeUser(yfsEnv, docIPChangeOrder, strResponseCode, strCurrentDBDateTime, strResponseSender);

			/**
			 * Commented as a resolution to COQ 90. Konst code is stamping the Order Notes already.
			 */
			// Add Notes
			//docIPChangeOrder = addNotes( docIPChangeOrder, addNotesText(strResponseCode, strResponseSender, strCurrentDBDateTime));
			logger.verbose("docIPChangeOrder inXML 2"+ XMLUtil.getXMLString(docIPChangeOrder));
			
			//Stamp approved
			docIPChangeOrder = stampApprovalDate(docIPChangeOrder, strCurrentDBDateTime);

			// ensuring approval notes to be stamped only at approval
			boolean fraudCheckFailed = false;
			
			NodeList holdTypeList = docIPChangeOrder.getElementsByTagName("OrderHoldType");
			
			for(int i=0; i<holdTypeList.getLength(); i++){
				
				Element holdTypeEle = (Element)holdTypeList.item(i);
				holdTypeEle.setAttribute("FraudResponse", "APPROVED");
				
				if("PENDINGREVIEW".equalsIgnoreCase(holdTypeEle.getAttribute("HoldType")) 
						&& "1100".equalsIgnoreCase(holdTypeEle.getAttribute("Status"))){
					
					fraudCheckFailed = true;
				}
				
			}
			
			Element customAttributesEle = (Element)docIPChangeOrder.getElementsByTagName("CustomAttributes").item(0);
			
			if(customAttributesEle != null){
				
				String fraudStatus = customAttributesEle.getAttribute("FraudStatus");
				
				if(fraudStatus != null){
					
					if("2".equalsIgnoreCase(fraudStatus)){
						
						fraudCheckFailed = true;
					}
				}
			}

			
			for(int i=0; i<holdTypeList.getLength(); i++){
				
				Element holdTypeEle = (Element)holdTypeList.item(i);
				
				if(fraudCheckFailed == false){
					holdTypeEle.setAttribute("FraudResponseRef", "APPROVED");
				}
			}

			// Invoke FanaticsBuyersRemorse service
			try {
				CommonUtil.invokeService(yfsEnv, FanaticsFraudCheckConstants.ATT_fanatics_FanaticsBuyersRemorse, docIPChangeOrder);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		else if (strResponseCode.equals(FANConstants.CONSTANT_TWO)) {

			logger.verbose("response is reject");

			// resolve all holds
			docIPChangeOrder = resolveAllHolds(yfsEnv, resolveAllHoldsInput(inXML));

			/**
			 * Commented as a resolution to COQ 90. Konst code is stamping the Order Notes already.
			 */
			// Add Notes
			//docIPChangeOrder = addNotes(docIPChangeOrder, addNotesText(strResponseCode, strResponseSender, strCurrentDBDateTime));

			// stampOrder with System Date-Time and the user who resolve this hold
			docIPChangeOrder = stampDateTimeUser(yfsEnv, docIPChangeOrder, strResponseCode, strCurrentDBDateTime, strResponseSender);			
			
			NodeList holdTypeList = docIPChangeOrder.getElementsByTagName("OrderHoldType");
			
			for(int i=0; i<holdTypeList.getLength(); i++){
				
				Element holdTypeEle = (Element)holdTypeList.item(i);
				holdTypeEle.setAttribute("ReferenceOrderStatus", "CANCEL");
			}
			// invoke changeOrder API
			logger.verbose("point 2");
			try {
				CommonUtil.invokeAPI(yfsEnv, FANConstants.API_CHANGE_ORDER, docIPChangeOrder);
			} catch (Exception e) {

				e.printStackTrace();
			}

			/**
			 * Call FanaticsCancelOrder service to cancel the lines on the order
			 */
			logger.verbose("point 3");
            try {
				
				String strCancelOrder = "<Order OrderNo='"+eleInputChangeOrderRoot.getAttribute(FANConstants.ORDER_NO)+"' " +
						"EnterpriseCode='"+eleInputChangeOrderRoot.getAttribute(FANConstants.ATT_EnterpriseCode)+"' " +
						"DocumentType='"+eleInputChangeOrderRoot.getAttribute(FANConstants.ATT_DocumentType)+"'/>";
				
				logger.verbose("docInputCancelOrder 2 is "+ XMLUtil.getXMLString(XMLUtil.getDocument(strCancelOrder)));
				CommonUtil.invokeService(yfsEnv, FanaticsFraudCheckConstants.SERVICE_fanatics_FanaticsCancelOrder, XMLUtil.getDocument(strCancelOrder));
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}

			/**
			 * Post cancellation email trigger to fanatics
			 */
			logger.verbose("point 4");
			try {
				CommonUtil.invokeService(yfsEnv, FanaticsFraudCheckConstants.SERVICE_fanatics_FanaticsPostEmailTrigger, docIPChangeOrder);
			} catch (Exception e) {
				logger.info(e.getMessage());
				e.printStackTrace();
			}

		}

		else if (strResponseCode.equals(FANConstants.CONSTANT_ONE)){

			logger.verbose("response is review");
			// fetch the Fraud Response Token
			String strResponseToken = XMLUtil.getXpathProperty(inXML,FanaticsFraudCheckConstants.XPATH_fanatics_FraudTokenFraudEngine);//inXML.getDocumentElement().getAttribute("FraudToken");
            
			String strIPChangeOrder = "<Order OrderNo='"+eleInputChangeOrderRoot.getAttribute(FANConstants.ORDER_NO)+"' " +
					"EnterpriseCode='"+eleInputChangeOrderRoot.getAttribute(FANConstants.ATT_EnterpriseCode)+"' " +
					"DocumentType='"+eleInputChangeOrderRoot.getAttribute(FANConstants.ATT_DocumentType)+"'>" +
					"<CustomAttributes FraudStatus='1' FraudStatusUserID='"+strResponseSender+"' FraudToken='"+strResponseToken+"'/>" +
					"<OrderHoldTypes><OrderHoldType HoldType='PENDINGREVIEW' ReasonText='' ResolverUserId='' Status='1100'/>" +
					"<OrderHoldType HoldType='PENDINGEVALUATION' ReasonText='' ResolverUserId='' Status='1300'/>" +
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
				CommonUtil.invokeAPI(yfsEnv, FANConstants.API_CHANGE_ORDER, docIPChangeOrder);
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

	}

	private String getDateTime(YFSEnvironment yfsEnv){
		// get the current time stamp from the DB
		ArrayList<Object[]> alTimeStamp = new ArrayList<Object[]>();
		String strCurrentDBDateTime = "";
		try {

			alTimeStamp = FANDBUtil.getDBResult(yfsEnv, FanaticsFraudCheckConstants.SQL_fanatics_GetDBTime, 1);
		} catch (Exception e1) {
					e1.printStackTrace();
		}

		Object[] resultRow = alTimeStamp.get(0);
		strCurrentDBDateTime = (String) resultRow[0]; 
		logger.verbose("strCurrentDBDateTime is "+ strCurrentDBDateTime);
		
		return strCurrentDBDateTime;
		
	}

	private Document stampDateTimeUser(YFSEnvironment yfsEnv,
			Document docIPChangeOrder, String strResponseCode, String dateTime, String strResponseSender) {

		// create the new element in the input xml
		Element eleCustomAttributes = docIPChangeOrder.createElement(FanaticsFraudCheckConstants.ATT_fanatics_CustomAttributes);

		// get the current user from the environment
		String strUserID = yfsEnv.getUserId();
        String strSystemName = yfsEnv.getSystemName();
        logger.verbose("system name is "+ strSystemName);
		
		// set the attributes in the xml
		eleCustomAttributes.setAttribute(FanaticsFraudCheckConstants.ATT_fanatics_FraudStatusTS, dateTime);
		eleCustomAttributes.setAttribute(FanaticsFraudCheckConstants.ATT_fanatics_FraudStatusUserID, strResponseSender);
		eleCustomAttributes.setAttribute(FanaticsFraudCheckConstants.ATT_fanatics_FraudStatus, strResponseCode);

		docIPChangeOrder.getDocumentElement().appendChild(eleCustomAttributes);

		logger.verbose("docIPChangeOrder inXML here is "+ XMLUtil.getXMLString(docIPChangeOrder));

		return docIPChangeOrder;
	}
	
	
	private Document stampApprovalDate(Document docIPChangeOrder, String dateTime){
		
		// create the new element in the input xml
		Element eleOrderDates = docIPChangeOrder.createElement(FanaticsFraudCheckConstants.ATT_fanatics_OrderDates);

		Element eleOrderDate = docIPChangeOrder.createElement(FanaticsFraudCheckConstants.ATT_fanatics_OrderDate);

		dateTime = dateTime.replace(" ", "T");
		
		// set the attributes in the xml
		eleOrderDate.setAttribute(FANConstants.ATT_DateTypeId, FanaticsFraudCheckConstants.CONSTANT_fanatics_FRAUD_APPROVAL_DATE);
		eleOrderDate.setAttribute(FanaticsFraudCheckConstants.ATT_fanatics_ActualDate, dateTime);
	
		eleOrderDates.appendChild(eleOrderDate);
		docIPChangeOrder.getDocumentElement().appendChild(eleOrderDates);

		logger.verbose("docIPChangeOrder inXML here is "+ XMLUtil.getXMLString(docIPChangeOrder));
		

		return docIPChangeOrder;
	}



	private Document addNotes(Document docIPChangeOrder, String noteText) {

		logger.verbose("response code is "+ noteText);
		Element eleInputChangeOrderRoot = docIPChangeOrder.getDocumentElement();

		Element eleNotes = docIPChangeOrder.createElement(FANConstants.ATT_Notes);
		Element eleNote = docIPChangeOrder.createElement(FANConstants.ATT_Note);
		eleNote.setAttribute(FANConstants.ATT_NoteText, noteText);

		eleNotes.appendChild(eleNote);

		eleInputChangeOrderRoot.appendChild(eleNotes);

		logger.verbose("Input xml is: "+ XMLUtil.getXMLString(docIPChangeOrder));

		return docIPChangeOrder;
	}
	private String addNotesText(String strResponseCode, String strResponseSender, String strCurrentDBDateTime){
		
		logger.verbose("response code is "+ strResponseCode + " from sender " + strResponseSender + "at " + strCurrentDBDateTime);
		
		try {
			strCurrentDBDateTime = FANDateUtils.formatDate("yyyy-MM-dd kk:mm:SS", "EEE, MMMMM d, yyyy h:mm a", strCurrentDBDateTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String noteText = "";
		
		if (strResponseCode.equals(FANConstants.CONSTANT_ZERO))
			noteText =  strResponseSender + " approved the order " + strCurrentDBDateTime;//"Fraud Engine passed the order";
		else if (strResponseCode.equals(FANConstants.CONSTANT_ONE))
			noteText =  strResponseSender + " passed the request to Accertify " + strCurrentDBDateTime; //"Fraud Engine passed the request to Accertify";
		else if (strResponseCode.equals(FANConstants.CONSTANT_TWO))
			noteText = strResponseSender + " rejected the order " + strCurrentDBDateTime;//"Fraud Engine rejected the order";
		
		return noteText;
		
	}

	
	private Document resolveAllHoldsInput(Document inXML){
		logger.verbose("resolveAllHoldsInput inXML 1"+ XMLUtil.getXMLString(inXML));
		
		Document docIPChangeOrder = null;
		Element eleInputChangeOrderRoot = inXML.getDocumentElement();

		String strIPChangeOrder = "<Order OrderNo='"+eleInputChangeOrderRoot.getAttribute(FANConstants.ORDER_NO)+"' " +
				"EnterpriseCode='"+eleInputChangeOrderRoot.getAttribute(FANConstants.ATT_EnterpriseCode)+"' " +
				"DocumentType='"+eleInputChangeOrderRoot.getAttribute(FANConstants.ATT_DocumentType)+"'></Order>";

		try 
		{  
			docIPChangeOrder = XMLUtil.getDocument(strIPChangeOrder);
		} catch (Exception e) {  
			e.printStackTrace();  
		} 	
		
		return docIPChangeOrder;
	}


	public Document resolveAllHolds(YFSEnvironment yfsEnv, Document inXML) {

		logger.verbose("resolveAllHolds docIPChangeOrder is: "+ XMLUtil.getXMLString(inXML));

		// call getOrderDetails to get the present holds
		String templateGetOrderDetails = "<Order EnterpriseCode=''><OrderHoldTypes><OrderHoldType HoldType='' Status=''></OrderHoldType></OrderHoldTypes></Order>" ;
		
		Document docGetOrderDetails = null;
		try 
		{  
			docGetOrderDetails = XMLUtil.getDocument(templateGetOrderDetails);
		} catch (Exception e) {  
			e.printStackTrace();  
		} 

		Document docOPGetOrderDetails = null;
		try {
			docOPGetOrderDetails = CommonUtil.invokeAPI(yfsEnv, docGetOrderDetails, FANConstants.API_GET_ORDER_DET, inXML);
			logger.verbose("resolveAllHolds docOPGetOrderDetails is: "+ XMLUtil.getXMLString(docOPGetOrderDetails));
		} catch (Exception e) {

			e.printStackTrace();
		}

		/**
		 * COQ 134 : Resolution Start
		 */
		// fetch the list of Fraud Related holds
		ArrayList alFraudRelatedHolds = getFraudHoldsList(yfsEnv); 
		/**
		 * COQ 134 : Resolution End
		 */
		
		// add OrderHoldTypes Element to the xml
		Element eleOrderHoldTypes = inXML.createElement(FanaticsFraudCheckConstants.ATT_fanatics_OrderHoldTypes);
		Node tempNode = inXML.importNode(eleOrderHoldTypes, false);
		inXML.getDocumentElement().appendChild(tempNode);
		logger.verbose("resolveAllHolds docIPChangeOrder is 11: "+ XMLUtil.getXMLString(inXML));

		// get the handle of the OrderHoldTypes element present in the docIPChangeOrder
		eleOrderHoldTypes = (Element) inXML.getDocumentElement().getElementsByTagName(FanaticsFraudCheckConstants.ATT_fanatics_OrderHoldTypes).item(0);

		// add the Hold Types (to be resolved) to the changeOrder input xml
		NodeList nlOrderHoldType = docOPGetOrderDetails.getElementsByTagName(FanaticsFraudCheckConstants.ATT_fanatics_OrderHoldType);
		int nlOrderHoldTypeLength = nlOrderHoldType.getLength();
		logger.verbose("nlOrderHoldTypeLength" + nlOrderHoldTypeLength);
		Element eleOrderHoldType = null;

		Document docOPFraudCommonCodes = null;
		
		for(int i=0;i<nlOrderHoldTypeLength;i++){
			logger.verbose("inside the loop ");
			eleOrderHoldType = (Element) nlOrderHoldType.item(i);
			
			// add the current hold to the input doc only if the status is created (1100) and its a Fraud related hold
			logger.verbose("status here is "+ eleOrderHoldType.getAttribute(FANConstants.ATT_STATUS));
			
			logger.verbose("Is it a Fraud hold: "+ alFraudRelatedHolds.contains(eleOrderHoldType.getAttribute(FANConstants.ATT_fanatics_HoldType)) );
			
			if (eleOrderHoldType.getAttribute(FANConstants.ATT_STATUS).equals(FANConstants.STR_1100) 
					&& alFraudRelatedHolds.contains(eleOrderHoldType.getAttribute(FANConstants.ATT_fanatics_HoldType)) ){
				logger.verbose("inside the if cond ");
				// create new element OrderHoldType
				Element eleIPOrderHoldType = inXML.createElement(FanaticsFraudCheckConstants.ATT_fanatics_OrderHoldType);
				eleIPOrderHoldType.setAttribute(FanaticsFraudCheckConstants.ATT_fanatics_HoldType, eleOrderHoldType.getAttribute(FanaticsFraudCheckConstants.ATT_fanatics_HoldType));
				eleIPOrderHoldType.setAttribute(FANConstants.ATT_STATUS, FANConstants.STR_1300);

				tempNode = inXML.importNode(eleIPOrderHoldType, false);
				eleOrderHoldTypes.appendChild(tempNode);

				logger.verbose("resolveAllHolds docIPChangeOrder is 12: "+ XMLUtil.getXMLString(inXML));
			}

		}

		// invoke changeOrder API
		/*try {
				CommonUtil.invokeAPI(yfsEnv, FANConstants.API_CHANGE_ORDER, docIPChangeOrder);
				} catch (Exception e) {

				e.printStackTrace();
				}*/

		return inXML;
	}
	
	/**
	 * COQ 134 : Resolution Start
	 */
	
	private ArrayList getFraudHoldsList(YFSEnvironment yfsEnv) {
		
		logger.verbose("Inside getFraudHoldsList");
		
		Document docOPFraudCommonCodes = null;
		ArrayList alFraudCodeValues = new ArrayList();
		
					// fetch the Fraud related holds using the common codes
					String strFraudHoldsList = "<CommonCode CodeType='"+ FanaticsFraudCheckConstants.CONSTANT_fanatics_FRAUD_HOLDS_COMMON_CODE +"'/>";
					String templateFraudHoldsList = "<CommonCodeList><CommonCode CodeValue='' /></CommonCodeList>";
					
					try {
						Document docGetFraudCommonCodeIP = XMLUtil.getDocument(strFraudHoldsList);
						Document docGetFraudCommonCodeTEMP = XMLUtil.getDocument(templateFraudHoldsList);
						
						docOPFraudCommonCodes =
						CommonUtil.invokeAPI(yfsEnv, docGetFraudCommonCodeTEMP, FANConstants.API_getCommonCodeList, docGetFraudCommonCodeIP);
						
					} catch (ParserConfigurationException | SAXException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// run loop on the Fraud Hold List document and put all the code values in an ArrayList
					NodeList nlCommonCode = docOPFraudCommonCodes.getElementsByTagName(FANConstants.ATT_CommonCode);
					Element eleCommonCode = null;
					
					int nlCommonCodeLength = nlCommonCode.getLength();
					for(int i=0;i<nlCommonCodeLength;i++){
						eleCommonCode = (Element) nlCommonCode.item(i);
						String strCodeValue = eleCommonCode.getAttribute(FANConstants.ATT_CodeValue);
						alFraudCodeValues.add(strCodeValue);
					}
					
		logger.verbose("Fraud Hold arrayList is "+ alFraudCodeValues);
		return alFraudCodeValues;
	}

	/**
	 * COQ 134 : Resolution End
	 */
	
	public void processHoldChange(YFSEnvironment yfsEnv, Document inXML) {
		
		logger.verbose("processHoldChange input: "+ XMLUtil.getXMLString(inXML));
		
		String holdType = XMLUtil.getXpathProperty(inXML, "/OrderHoldType/@HoldType");
		String holdStatus = XMLUtil.getXpathProperty(inXML, "/OrderHoldType/@Status");

		logger.verbose("pholdType: "+ holdType + ", holdStatus: " + holdStatus);


		if(holdType.equals("Pending Review")){
			
			String strCurrentDBDateTime = getDateTime(yfsEnv);
			logger.verbose("strCurrentDBDateTime "+strCurrentDBDateTime);
			
			//Create base input
			Document baseInput = processHoldBaseDoc(inXML);
			
			String strResponseSender = inXML.getDocumentElement().getAttribute(FanaticsFraudCheckConstants.ATT_fanatics_Sender);
			
			//Resolve
			if(holdStatus.equals("1300")){
				
				//Resolve all holds
				baseInput = resolveAllHolds(yfsEnv, baseInput);

				//Add notes to the order
				baseInput = addNotes( baseInput, "User resolved the Pending Review hold");
				
				//Stamp the order
				baseInput = stampDateTimeUser(yfsEnv, baseInput, "Resolved", strCurrentDBDateTime, strResponseSender);		
				
				changeOrder(yfsEnv, baseInput);
				
				
			}
			//Reject
			else if(holdStatus.equals("1200")){
				
				//Add notes to the order
				baseInput = addNotes( baseInput, "User rejected the Pending Review hold");

				//Stamp the order
				baseInput = stampDateTimeUser(yfsEnv, baseInput, "Rejected", strCurrentDBDateTime, strResponseSender);	
				
				changeOrder(yfsEnv, baseInput);
				
				//Cancel the order
				/**
				 * Call FanaticsCancelOrder service to cancel the lines on the order
				 */
				try {
					CommonUtil.invokeService(yfsEnv, FanaticsFraudCheckConstants.SERVICE_fanatics_FanaticsCancelOrder, baseInput);
				} catch (Exception e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}
				
			}
			
		}
		
		if (holdType.equals(FanaticsFraudCheckConstants.HOLDTYPE_fanatics_PENDINGEREVIEW) && holdStatus.equals(FANConstants.STR_1300)) {
			
			logger.verbose("Pending Review Hold resolved");
			//Create base input

			Document baseInput = processHoldBaseDoc(inXML);
			
		     //Invoke FanaticsBuyersRemorse service;
			try {
				CommonUtil.invokeService(yfsEnv, FanaticsFraudCheckConstants.ATT_fanatics_FanaticsBuyersRemorse, baseInput);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		
	}
	
	private Document processHoldBaseDoc(Document inXML) {
		
		logger.verbose("Inside processHoldBaseDoc");
		Document baseInput = null;
		
		String orderHeaderKey = XMLUtil.getXpathProperty(inXML, "/OrderHoldType/Order/@OrderHeaderKey");

		try 
		{  
			baseInput = XMLUtil.getDocument("<Order/>");
		} catch (Exception e) {  
			e.printStackTrace();  
		} 
		
		Element baseInputEle = baseInput.getDocumentElement();
		baseInputEle.setAttribute("OrderHeaderKey", orderHeaderKey);
		
		return baseInput;
	}

	
	private void changeOrder(YFSEnvironment yfsEnv, Document inXML){
		
		 // invoke changeOrder API
		try {
			CommonUtil.invokeAPI(yfsEnv, FANConstants.API_CHANGE_ORDER, inXML);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
	}


}
