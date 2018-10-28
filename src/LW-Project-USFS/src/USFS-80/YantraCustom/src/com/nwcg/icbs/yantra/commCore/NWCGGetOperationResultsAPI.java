/*
 * Created on Feb 11, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.nwcg.icbs.yantra.commCore;

import gov.nwcg.services.ross.common_types._1.ApplicationSystemType;
import gov.nwcg.services.ross.common_types._1.SystemTypeSimpleType;
import gov.nwcg.services.ross.resource_order._1.GetOperationResultsReq;
import gov.nwcg.services.ross.resource_order.wsdl._1.ResourceOrderInterface;
import gov.nwcg.services.ross.resource_order.wsdl._1.ResourceOrderService;

import java.util.Iterator;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.soap.SOAPElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.websphere.webservices.soap.IBMSOAPElement;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;


/**
 * @author sdas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NWCGGetOperationResultsAPI implements YIFCustomApi{

	private Properties props;
	
	public void setProperties(Properties prop) throws Exception {
		this.props = prop;
	}
	
	public Document getOperationResults(YFSEnvironment env, Document inDoc) {
		System.out.println("in getopresults");
	    String userName = "";
	    
	    SOAPElement sElement = null;
	    
	    NWCGLoggerUtil.Log.info("inside getOperationResults method!!");
	    System.out.println("Indoc to get OperationResults::"+XMLUtil.getXMLString(inDoc));
	    
	    String distID = inDoc.getDocumentElement().getAttribute("DistributionID");
	    System.out.println("distID from getOperationResults is: " + distID);
	    
        String latest_message_key = apiCallToGetLatestMessageKeyForDistID(env,distID);
		NWCGLoggerUtil.Log.info("latest system key:"+latest_message_key);
        
		// Get the loginID and Username from Yantra
    	String loginID = NWCGAAConstants.USER_NAME;
    	String system_name = env.getSystemName();
    	NWCGLoggerUtil.Log.info("LoginID::"+loginID);
    	NWCGLoggerUtil.Log.info("System Name::"+system_name);
    	
    	//Username should map with loginid
    	// Change suggested by Jay
    	//System.out.println("###########################################");
    	//System.out.println("user is: " + loginID);
    	userName = loginID;
    	
    	String pw_key = NWCGTimeKeyManager.createKey(latest_message_key,loginID,system_name);
    	NWCGLoggerUtil.Log.info("pw_key :"+pw_key);
    	
		String message_name = inDoc.getDocumentElement().getNodeName();
		NWCGLoggerUtil.Log.info("message_name:"+message_name);
    	
		String string_token = NWCGJAXRPCWSHandlerUtils.getStringToken(userName, loginID, pw_key, message_name);
    	NWCGLoggerUtil.Log.info("string token :"+string_token);
    	
    	// Make a Yantra API call to get the latest message status for the given distribution ID.
    	String latestMsgStatus = apiCallToGetLatestMsgStatusForDistID(env,distID);
    	System.out.println("LatestMessageStatus*****************: " + latestMsgStatus);
	    
    	try {
	    	sElement = createGetOperationsResultsReq(string_token,distID);
	    }catch(Exception e){
	        NWCGLoggerUtil.Log.warning("Excpetion thrown is::"+e.toString());
	        return formReturnResult("-1",e.toString(),latestMsgStatus);
	    }
		
		if(sElement == null) return formReturnResult("0", "No operation result found!",latestMsgStatus);
		else{
			NWCGLoggerUtil.Log.info("Element is: ");
			IBMSOAPElement ibmSoapElem = (IBMSOAPElement)sElement;
			NWCGLoggerUtil.Log.info("soap Elem:"+ibmSoapElem.toXMLString(true));
		}
		
		System.out.println("done with getopresutls blah");
		// This method is just for verification purpose.
	    //processOperationResults(sElement);
		
		Element eleReturned = sElement;
		/*
		Document retDoc = null;
		try {
			retDoc = XMLUtil.getDocument("<ro:GetOperationResultsResp xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:cat=\"http://nwcg.gov/services/ross/catalog/1.1\" xmlns:nwcg=\"http://nwcg.gov/services/ross/common_types/1.1\" xmlns:rch=\"http://nwcg.gov/services/ross/resource_clearinghouse/1.1\" xmlns:ro=\"http://nwcg.gov/services/ross/resource_order/1.1\" xmlns:ron=\"http://nwcg.gov/services/ross/resource_order_notification/1.1\" xmlns:rsc=\"http://nwcg.gov/services/ross/resource/1.1\" xmlns:rscn=\"http://nwcg.gov/services/ross/resource_notification/1.1\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"> <ro:ResponseStatus> <ReturnCode>0</ReturnCode> <ResponseMessage> <Code>OK</Code> <Severity>Information</Severity> <Description>Operation Results Retrieved</Description> </ResponseMessage> </ro:ResponseStatus> <cat:CreateCatalogItemResp> <cat:ResponseStatus> <ReturnCode>0</ReturnCode> </cat:ResponseStatus> <cat:CatalogItemKey> <CatalogType>NWCG</CatalogType> <CatalogItemCode>ITEM02</CatalogItemCode> </cat:CatalogItemKey> </cat:CreateCatalogItemResp></ro:GetOperationResultsResp>");
			
		}
		catch(Exception e){
			System.out.println("There was an error: ");
			e.printStackTrace();
			
		}
		
		distID = "123412341234";
		latestMsgStatus = "PROCESSED";
		*/
		System.out.println("Returning from processReturnedOperationResults.");
		return processReturnedOperationResults(env, eleReturned, distID, latestMsgStatus, latest_message_key);
		//return generateOperationResults(sElement,latestMsgStatus);
	}

	/**
     * @param env
	 * @param distID
	 * This method transforms the returned XML from ROSS to a format that can be displayed
	 * on screen to the user.
     */
	public Document processReturnedOperationResults(YFSEnvironment env, Element inElem, String distID, String latestMsgStatus, String latest_message_key){
					
			//Element inElem = retDoc.getDocumentElement();
			
			NodeList nlRetCode = inElem.getElementsByTagName("ReturnCode");
			NodeList nlRetDesc = inElem.getElementsByTagName("Description");
			NodeList nlRespStatus = inElem.getElementsByTagName("ro:ResponseStatus");
						
					Element eleRetCode = (Element)nlRetCode.item(0);
					Element eleRetDesc = (Element)nlRetDesc.item(0);
					Element eleRespStatusElem = (Element)nlRespStatus.item(0);
					
					String respObjStr = "";
					String respStatusStr = "";
					
					System.out.println("eleRetCode is: " + XMLUtil.getElementXMLString(eleRetCode));
					
					if(eleRetDesc!=null){
						respObjStr = XMLUtil.getNodeValue(eleRetDesc);
						System.out.println("eleRetDesc nodeValue: " + XMLUtil.getNodeValue(eleRetDesc));
					}
					
					if(eleRetCode!=null ){
						respStatusStr = XMLUtil.getNodeValue(eleRetCode);							
					}
					
					Document outDoc = null;
					
					try{
						outDoc = XMLUtil.createDocument("GetOperationResultsReq");
					}
					catch(Exception e){
						System.out.println("Error creating doc:");
						e.printStackTrace();
					}
					Document docDeliverResp = null;
					try{
						//docDeliverResp = XMLUtil.createDocument("DeliverOperationResults");
						docDeliverResp = XMLUtil.getDocument(NWCGAAConstants.DELIVER_OPERATION_RESULTS_ROOT);
					}
					catch(Exception e){
						System.out.println("There was an error creating the document.");
						e.printStackTrace();
					}
					
					eleRespStatusElem.getParentNode().removeChild(eleRespStatusElem);
					
					NodeList nl = inElem.getChildNodes();
					System.out.println("nl.length is: " + nl.getLength());
					Element eleRetDocEleForDelivery = null;
					
					for (int x=0; x < nl.getLength(); x++){
						if (nl.item(x).getNodeType() == 1){
							eleRetDocEleForDelivery = (Element)nl.item(x);
						}
						
					}
					if(docDeliverResp!=null){
					
						Element eleDeliverRoot = docDeliverResp.getDocumentElement();
						
						if(eleRetDocEleForDelivery!=null){
							eleDeliverRoot.setAttribute("DistributionID",distID);
							Node node = docDeliverResp.importNode(eleRetDocEleForDelivery,true);
							eleDeliverRoot.appendChild(node);
						}
					}			
					
			Element eleRoot = outDoc.getDocumentElement();
			eleRoot.setAttribute("DistributionID", distID);
			eleRoot.setAttribute("ReturnedMessage", XMLUtil.getElementXMLString(inElem));
			eleRoot.setAttribute("LatestMessageStatus", latestMsgStatus);
			eleRoot.setAttribute("ReturnCode", respStatusStr);
			eleRoot.setAttribute("DescriptionFromRoss", respObjStr);
			eleRoot.setAttribute("DeliverToRossMessage",XMLUtil.getXMLString(docDeliverResp));
			eleRoot.setAttribute("LatestMessageKey", latest_message_key);
			
			System.out.println("XML being returned: " + XMLUtil.getXMLString(outDoc));
			
			return outDoc;
			
		
		/*
	 * OLD
	 	Document outDoc = null;
		
		try{
			outDoc = XMLUtil.createDocument("GetOperationResultsReq");
		}
		catch(Exception e){
			System.out.println("Error creating doc:");
			e.printStackTrace();
		}
		
		Element inElem = inDoc.getDocumentElement();
		NodeList nlRetCode = inElem.getElementsByTagName("ReturnCode");
		NodeList nlRetDesc = inElem.getElementsByTagName("Description");
					
				Element eleRetCode = (Element)nlRetCode.item(0);
				Element eleRetDesc = (Element)nlRetDesc.item(0);
				
				String strRetCode = eleRetCode.getLocalName();
				String strRetDesc = eleRetDesc.getLocalName();
				
				String respObjStr = "";
				String respStatusStr = "";
				
				System.out.println("eleRetCode is: " + XMLUtil.getElementXMLString(eleRetCode));
				
				if(strRetDesc!=null && strRetDesc.equalsIgnoreCase("Description")){
					respObjStr = XMLUtil.getNodeValue(eleRetDesc);
					
				}
				else{
					if (eleRetDesc!=null)
					respObjStr = XMLUtil.getElementXMLString(eleRetDesc);
				}
				
				if(strRetCode!=null && strRetCode.equalsIgnoreCase("ReturnCode")){
					respStatusStr = XMLUtil.getNodeValue(eleRetCode);
					
				}
				else{
					if (eleRetCode!=null)
					respObjStr = XMLUtil.getElementXMLString(eleRetCode);
				}
				
				
		Element eleRoot = outDoc.getDocumentElement();
		eleRoot.setAttribute("DistributionID", distID);
		eleRoot.setAttribute("ReturnedMessage", XMLUtil.getXMLString(outDoc));
		eleRoot.setAttribute("LatestMessageStatus", latestMsgStatus);
		eleRoot.setAttribute("ReturnCode", respStatusStr);
		eleRoot.setAttribute("Description", respObjStr);
		
		return inDoc;
		*/
	}
	
	
    public static String apiCallToGetLatestMessageKeyForDistID(YFSEnvironment env, String distID) {
    	System.out.println("in get dist key");
    	System.out.println("In apiCalltoGetLatestMessageKeyForDistID, the distID is: " + distID);
        String latestMsgKey = "";
        try{
            Document inDoc = XMLUtil.getDocument("<NWCGOutboundMessage DistributionID=\""+distID+"\" />");
            System.out.println("inDoc to NWCGGetOBMessageListService is: " + XMLUtil.getXMLString(inDoc));
            Document opDoc = CommonUtilities.invokeService(env,NWCGAAConstants.SDF_GET_OB_MESSAGE_LIST_SERVICE_NAME,inDoc);
            
            if(opDoc != null){
    			NodeList nl = opDoc.getElementsByTagName("NWCGOutboundMessage");
    			for(int i=0;i<nl.getLength();i++){
    				Element elem = (Element)nl.item(i);
    				String msgType = elem.getAttribute("MessageType");
    				if(!msgType.equals("START".trim())){
    				    latestMsgKey = elem.getAttribute("MessageKey");
    				}
    			}
    		}
    	}catch(Exception e){
            NWCGLoggerUtil.Log.warning("Exception thrown in method :"+e.toString());
            e.printStackTrace();
        }
    	System.out.println("about to return latestmsgkey");
        if(!StringUtil.isEmpty(latestMsgKey)){
            NWCGLoggerUtil.Log.info("latest message key :"+latestMsgKey);
    		return latestMsgKey;
        }else{ // hardcoding done as of now.
            latestMsgKey = ResourceUtil.get("NWCG_DO_NO_AUTH_DB_KEY", "9011917122761465"); 
            return latestMsgKey;
        }
    }

    /**
     * @param env
	 * @param distID
     */
    public static String apiCallToGetLatestMsgStatusForDistID(YFSEnvironment env, String distID) {
    	System.out.println("in apiCalltoGetLatestMsg");
        String msgStatus = "";
        try{
            Document inDoc = XMLUtil.getDocument("<NWCGOutboundMessage DistributionID=\""+distID+"\" />");
            Document opDoc = CommonUtilities.invokeService(env,NWCGAAConstants.SDF_GET_OB_MESSAGE_LIST_SERVICE_NAME,inDoc);
            //System.out.println("#######opDoc is: " + XMLUtil.getXMLString(opDoc));
            if(opDoc != null){
    			NodeList nl = opDoc.getElementsByTagName("NWCGOutboundMessage");
    			for(int i=0;i<nl.getLength();i++){
    				Element elem = (Element)nl.item(i);
    				String msgType = elem.getAttribute("MessageType");
    				if(!msgType.equals("START".trim())){
    					msgStatus = elem.getAttribute("MessageStatus");
    				}
    			}
    		}
    		
            NWCGLoggerUtil.Log.info("latest message status :"+msgStatus);
    		return msgStatus;
        }catch(Exception e){
            NWCGLoggerUtil.Log.warning("Exception thrown in method :"+e.toString());
            e.printStackTrace();
        }
        System.out.println("done with apicallto getlatestmsg");
        msgStatus = "NULL"; 
        return msgStatus;
    }

    /**
	 * @param string_token
	 */
	private SOAPElement createGetOperationsResultsReq(String string_token,String distID) 
	throws Exception{
		System.out.println("starting createGetOperationResultsReq soap...");
		
		ResourceOrderInterface port = null;
		SOAPElement soapElem = null;
		
		NWCGLoggerUtil.Log.info("dist ID:"+distID);
		
		ResourceOrderService locator = new ResourceOrderService();
		port = locator.getResourceOrderPort();
		
		// Invoke the clientside handler 
	
		callGetOperationsHandler(locator);
		
		
        GetOperationResultsReq req = new GetOperationResultsReq();
        req.setDistributionID(distID);
        ApplicationSystemType type = new ApplicationSystemType();
        type.setSystemID(string_token);
        type.setSystemType(SystemTypeSimpleType.ICBS);
        req.setSystemOfOrigin(type);
	    /*TODO:Commenting now will be changed later once we develop get operations
	     * soapElem = port.getOperationResults(req);*/
	    System.out.println("done with creategetopresreq...");
        return soapElem;
	}

	/**
     * @param locator
     */
    private void callGetOperationsHandler(ResourceOrderService locator) {
    	System.out.println("in callGetOperationsHandler");
        QName portQName = new QName(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE,NWCGAAConstants.RESPORCE_ORDER_PORT);
        HandlerInfo hInfo = new HandlerInfo();
        hInfo.setHandlerClass(com.nwcg.icbs.yantra.handler.NWCGGetOperationResultsHandler.class);
        //locator.getHandlerRegistry().getHandlerChain(portQName).add(hInfo);
        System.out.println("done w/callGetOperationsHandler");
    }

    /**
	 * @param soapElem
	 */
	private void processOperationResults(SOAPElement soapElem) {
		System.out.println("in processOpResults...");
		if( soapElem == null) return;
		
		IBMSOAPElement ibmSoapElem = (IBMSOAPElement)soapElem;
		NWCGLoggerUtil.Log.info("In NWCGGetOperationResultsAPI");
		NWCGLoggerUtil.Log.info("soap Elem:"+ibmSoapElem.toXMLString(true));
		NWCGLoggerUtil.Log.info("soap elem name:"+soapElem.getNodeName());
		
		
		Iterator iter = soapElem.getChildElements();
		while(iter.hasNext()){
			NWCGLoggerUtil.Log.info("iter.getClass() is: " + iter.getClass());
			NWCGLoggerUtil.Log.info("iter.next() is: " + iter.next());
			SOAPElement childElem = (SOAPElement)iter.next();
			NWCGLoggerUtil.Log.info("child node name:"+childElem.getNodeName());
		}
		System.out.println("done with the process");
	}
	
	/**
	 * @param soapElem
	 * @param latestMsgStatus
	 */
	private Document generateOperationResults(SOAPElement soapElem, String latestMsgStatus){
		System.out.println("in generateOperationResults...");
		String respStatusStr = "";
		String respObjStr = "";
		Element eleInput = soapElem;
		System.out.println("The input Element is: " + XMLUtil.getElementXMLString(eleInput));
		Document docOut = null;
		
		try {
			docOut = XMLUtil.getDocumentForElement(eleInput);
			
		}
		catch(Exception e){
			System.out.println("There was an error: ");
			e.printStackTrace();
			
		}
		
		System.out.println("The output doc is: " + XMLUtil.getXMLString(docOut));
		return docOut;
	}
	
	/**
     * @param retCode
     * @return
     */
    private String returnMeaningfulROSSStatusToUser(String retCode) {
    	System.out.println("in returnmeaningfulrossstauts");
        if(retCode.equals(NWCGAAConstants.ROSS_RET_SUCCESS_VALUE)){
        	System.out.println(NWCGAAConstants.ROSS_RET_SUCCESS_VALUE);
            return retCode.concat(NWCGAAConstants.ROSS_RET_SUCCESS_CODE);
        }else{
            return retCode.concat(NWCGAAConstants.ROSS_RET_FAILURE_CODE);
        }
        
    }

    /**
     * @param respObjStr
     * @return
     */
    private String getReturnCodeFromSOAPMessage(String respObjStr) {
    	System.out.println("in getRetCodeFromSOAPMessg");
        String resStr = "";
        try{
	        Document d = XMLUtil.getDocument(respObjStr);
	        Element valElem = d.getDocumentElement();
		    
	        NWCGLoggerUtil.Log.info("class name:"+valElem.getClass().getName());
		    
	        NodeList nl = valElem.getChildNodes();
		    for(int a=0;a<nl.getLength();a++){
		        Object obj = nl.item(a);
		        NWCGLoggerUtil.Log.info("node class name:"+obj.getClass().getName());
		        if(obj instanceof org.apache.xerces.dom.DeferredTextImpl){
		        	System.out.println("trying to get text");
		            org.apache.xerces.dom.DeferredTextImpl defTextImpl = (org.apache.xerces.dom.DeferredTextImpl)obj;
		            resStr = defTextImpl.getTextContent();
		            NWCGLoggerUtil.Log.info("str value :"+resStr);
		            
		        }
		    }
        }catch(Exception e){
            NWCGLoggerUtil.Log.warning("Exception thrown:"+e.toString());
            e.printStackTrace();
            
        }
        System.out.println("done with get retcodefromsoapmsg");
        return resStr;
    }
	
	private Document formReturnResult(String respStatusStr, String respObjStr, String latestMsgStatus) {
		System.out.println("in formreturnresult");
		
		try {
			Document getOperationResults = XMLUtil.createDocument("ROSSOperationResult");
			getOperationResults.getDocumentElement().setAttribute("ResponseStatus",respStatusStr);
			getOperationResults.getDocumentElement().setAttribute("ResponseMsg",respObjStr);
			getOperationResults.getDocumentElement().setAttribute("ICBSStatus",latestMsgStatus);
			System.out.println("returning getopresults doc");
			return getOperationResults;
		} catch (Exception e) {
			NWCGLoggerUtil.Log.severe("error in creating resp document");
			return null;
		}
		
	}
	
	// void main method for testing purpose.
	//public static void main(String[] args) throws Exception{
	    /*String s = "<ReturnCode xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:tnsa=\"http://nwcg.gov/services/ross/common_types/1.1\" xmlns:tns=\"http://nwcg.gov/services/ross/resource_order/1.1\">100</ReturnCode>";
	    Document d = XMLUtil.getDocument(s);
	    Element valElem = d.getDocumentElement();
	    System.out.println("class name:"+valElem.getClass().getName());
	    //System.out.println("val :"+val);
	    NodeList nl = valElem.getChildNodes();
	    for(int a=0;a<nl.getLength();a++){
	        Object n = nl.item(a);
	        System.out.println("node class name:"+n.getClass().getName());
	        if(n instanceof org.apache.xerces.dom.DeferredTextImpl){
	            org.apache.xerces.dom.DeferredTextImpl defTextImpl = (org.apache.xerces.dom.DeferredTextImpl)n;
	            System.out.println("str value :"+defTextImpl.getTextContent());
	        }
	        
	    }*/
	    
	//}

}
