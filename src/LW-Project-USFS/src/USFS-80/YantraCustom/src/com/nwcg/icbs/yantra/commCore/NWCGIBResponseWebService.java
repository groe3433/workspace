package com.nwcg.icbs.yantra.commCore;

import gov.nwcg.services.ross.common_types._1.ResponseStatusType;
import gov.nwcg.services.ross.resource_order._1.DeliverOperationResultsResp;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.xml.rpc.ServiceException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * The purpose of this class is to send a Deliver Operations request message to ROSS
 * using the deliver operations web service client side stub classes.
 * So we need to generate the ROSS deliver operations web service and also its 
 * corresponding client side stub classes.
 * 
 * @author sdas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NWCGIBResponseWebService implements YIFCustomApi{

	public Document recieveMsg(YFSEnvironment env, Document doc) throws Exception{
		
	    String code = "";
	    String distrID = "";
	    String actualElemDocStr = "";
	    String serviceName = "";
	    
	    Document actualElemDoc = null;
	    
		System.out.println("NWCGResponseWebService");
		
		NodeList nl = doc.getDocumentElement().getChildNodes();
		for(int i=0;i<nl.getLength();i++){
		    
		    Object obj = nl.item(i);
		    System.out.println("obj class name :"+obj.getClass().getName());
		    System.out.println("obj :"+obj);
		    if(obj instanceof org.apache.xerces.dom.DeferredElementImpl){
		        
		        Element distrIdElem = (Element)obj;
		        System.out.println("local name:"+distrIdElem.getNodeName());
		        if(distrIdElem.getNodeName().equals("DistIDElem")){
		            distrID = distrIdElem.getAttribute("DistID");
		    		System.out.println("distID:"+distrID);
		        }else if(distrIdElem.getNodeName().equals("MessageElem")){
		            NodeList childNL = distrIdElem.getChildNodes();
		            for(int j=0;j<childNL.getLength();j++){
		                Object childObj = childNL.item(j);
		                System.out.println("obj class name 1:"+childObj.getClass().getName());
		    		    NWCGLoggerUtil.Log.info("obj 1:"+childObj);
		    		    if(childObj instanceof org.apache.xerces.dom.DeferredElementImpl){
		    		        Element actualElem = (Element)childObj;
		    		        String nodename = actualElem.getNodeName();
		    		        NWCGLoggerUtil.Log.info("node name:"+nodename);
		    		        serviceName = nodename.substring(nodename.indexOf(":")+1);
		    		        NWCGLoggerUtil.Log.info("serviceName :"+serviceName);
		    		        actualElemDoc = XMLUtil.getDocumentForElement(actualElem);
		    		        NWCGLoggerUtil.Log.info("actualElem :"+XMLUtil.getXMLString(actualElemDoc));
		    		    }
		            }
		        }
		    }else{
		        NWCGLoggerUtil.Log.info("else 1");
		    }
		}
		
		actualElemDocStr = XMLUtil.getXMLString(actualElemDoc);
		
		Document opXML = null ;
		// jay: commenting this for compilation
		//NWCGIBMessageHandlerService.getIBMsgListOPXML(env,distrID);
		
		String latest_msg_key = "" ;
//		 jay: commenting this for compilation
		//NWCGIBMessageHandlerService.getLatestMessageKey(opXML);
		
		if(serviceName.indexOf("Notification")>0){
		    
		    NWCGLoggerUtil.Log.info("Processing any notification message");
		
		    /**
		     * 
		     * We need to build a inbound notification response somewhat like this.
		     * <SOAP-ENV:Envelope 
					xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" 
					xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/" 
					xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
					xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
					<SOAP-ENV:Body>
						<DeliverNotificationResp 
							xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
							xmlns:cdf="http://www.cdf.ca.gov/CAD" 
							xmlns:rscn="http://nwcg.gov/services/ross/resource_notification/1.1" 
							xmlns:ron="http://nwcg.gov/services/ross/resource_order_notification/1.1">
							<ResponseStatus>
								<ReturnCode>0</ReturnCode>
							</ResponseStatus>   
						</DeliverNotificationResp>
					</SOAP-ENV:Body>
				</SOAP-ENV:Envelope>
			 *	
             *
		     */
		    
		    /*DeliverNotificationRespService locator = new DeliverNotificationRespService();
		    DeliverNotificationsInterface delPort = locator.getDeliverNotificationRespPort();
		    */
		    ResponseStatusType statType = new ResponseStatusType();
		    // Only return code is added at this point
		    // ResponseMessageType not added at this point.
		    statType.setReturnCode(0);
		    
		    // Invoking the ROSS notifications web service and posting notification response
		    //delPort.deliverNotificationResp(statType);
		    
		}else{
		
		    NWCGLoggerUtil.Log.info("Processing any request response message");
		    
		    /**
			 * we need to build a request response message response somewhat like this:
			 * <soapenv:Envelope 
			 * 	xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
			 *  xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
			 *  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
			 *  xmlns:q0="http://nwcg.gov/services/ross/resource_order/1.1">
					<soapenv:Body>
						<ns2:DeliverOperationResultsReq 
						  xmlns:ns2="http://nwcg.gov/services/ross/resource_order/1.1" 
						  xmlns:ns3="http://nwcg.gov/services/ross/common_types/1.1">
							<ns2:DistributionID>distID</ns2:DistributionID>
							<ro:PlaceResourceRequestResp xmlns:ro="http://nwcg.gov/services/ross/resource_order/1.1">
								<ro:ResponseStatus>
									<ro:ReturnCode>100</ro:ReturnCode>
								</ro:ResponseStatus>
							</ro:PlaceResourceRequestResp>
						</ns2:DeliverOperationResultsReq>
					</soapenv:Body>
				</soapenv:Envelope>
			 * 
			 * 
			 * 
			 */
			
			SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
			
			SOAPEnvelope envelop = getSOAPEnvelop(soapMessage);
			
			SOAPBody soapBody = soapMessage.getSOAPBody();
			
			SOAPElement deliverOpnResultsReqElem = soapBody.addChildElement(envelop.createName("DeliverOperationResultsReq","ns2","http://nwcg.gov/services/ross/resource_order/1.1"));
			deliverOpnResultsReqElem.addNamespaceDeclaration("ns3","http://nwcg.gov/services/ross/common_types/1.1");
			
			SOAPElement distributionIDElem = getDistIDSOAPElement("distID", envelop, deliverOpnResultsReqElem);
			
			// Creating a test response. // TBD
			/*
			 * 1. Ideally the actual business validation response message may or may not be 
			 * namespace aware. if they are not, then we need to write different parsers
			 * for each component message. These individual parsers will parse the normal document xml
			 * and read all relevant information. Then we need to form a name space aware 
			 * SOAP message for the response and stamp the correct values at the correct places.
			 * 
			 * 2. If the business XML obtained from the handler are name space aware
			 * then we need to form a complete SOAPElement out of that message and
			 * pass to the getDeliverOperationsResultsResponse() method.
			 */
			
			/********/
			SOAPElement placeResourceReqRespElem = deliverOpnResultsReqElem.addChildElement(envelop.createName("PlaceResourceRequestResp","ro","http://nwcg.gov/services/ross/resource_order/1.1"));
			SOAPElement respStatElem = placeResourceReqRespElem.addChildElement("ResponseStatus","ro");
			SOAPElement returnCodeElem = respStatElem.addChildElement("ReturnCode");
			returnCodeElem.addTextNode("100");
			/********/
			
	        DeliverOperationResultsResp resp = getDeliverOperationsResultsResponse(deliverOpnResultsReqElem);
			
			/** for verification **/
			code = verifydeliverOperationResultsResp(resp);
			/** for verification **/
			
			// Once we get the DeliverOperationResultsResp we need to update the message store.
			NWCGMessageStore messageStore = NWCGMessageStore.getMessageStore();
			if(code.equals("100")){
				NWCGLoggerUtil.Log.info("Message processed successfully!!!");
			    try{
				    messageStore.updateMessage(env,distrID,NWCGAAConstants.MESSAGE_DIR_TYPE_IB,actualElemDocStr,NWCGAAConstants.MESSAGE_TYPE_LATEST,NWCGAAConstants.MESSAGE_STATUS_PROCESSED,NWCGAAConstants.SYSTEM_NAME,latest_msg_key,true,serviceName);
				}catch(Exception e){
				    NWCGLoggerUtil.Log.warning("Exception thrown while updating message store"+e.getMessage());
				    CommonUtilities.raiseAlert(env,NWCGAAConstants.QUEUEID_INBOUND_EXCEPTION,e.getMessage(),actualElemDoc,e,null);
				}
			}else{
			    NWCGLoggerUtil.Log.info("Message didn't process");
			    // DOUBT - Should message store be updated with failure status or something?
			}
		}
		
		return null;
	}

	/**
     * @param code
     * @param resp
     * @return
     */
    private String verifydeliverOperationResultsResp(DeliverOperationResultsResp resp) {
        
        String code = "";
        
        ResponseStatusType respStatType = resp.getResponseStatus();
        int retCode = respStatType.getReturnCode();
        NWCGLoggerUtil.Log.info("retCode ::"+retCode);
       /* ResponseMessageType[] respMsg = respStatType.getResponseMessage();
        
        for(int i=0;i<respMsg.length;i++){
            
            ResponseMessageType msgType = respStatType.getResponseMessage(i);
            
            code = msgType.getCode();
            NWCGLoggerUtil.Log.info("code >>"+code);
            String desc = msgType.getDescription();
            NWCGLoggerUtil.Log.info("desc >>"+desc);
            Severity sev = msgType.getSeverity();
            NWCGLoggerUtil.Log.info("sev >>"+sev.getValue());
        }*/
        
        return code;
    }

    /**
     * @param deliverOpnResultsReqElem
     * @return
     * @throws ServiceException
     * @throws RemoteException
     */
    private DeliverOperationResultsResp getDeliverOperationsResultsResponse(SOAPElement deliverOpnResultsReqElem) {
       /* ROSSDeliveryServiceLocator locator = new ROSSDeliveryServiceLocator();
        ROSSDeliveryInterface port = locator.getROSSDeliveryPort();
        
        DeliverOperationResultsResp resp = port.ROSSDeliverOperationResult(deliverOpnResultsReqElem);
        return resp;*/
    	return null;
    }

    /**
	 * @param distID
	 * @param envelop
	 * @param deliverOpnResultsReqElem
	 * @throws SOAPException
	 */
	private SOAPElement getDistIDSOAPElement(String distID, SOAPEnvelope envelop, SOAPElement deliverOpnResultsReqElem) throws SOAPException {
		
		SOAPElement distIDElem = deliverOpnResultsReqElem.addChildElement(envelop.createName("DistributionID","ro","http://nwcg.gov/services/ross/resource_order/1.1"));
		distIDElem.addTextNode(distID);
		
		return distIDElem;
	}

	/**
	 * @param soapMessage
	 * @throws SOAPException
	 */
	private SOAPEnvelope getSOAPEnvelop(SOAPMessage soapMessage) throws SOAPException {
		
		SOAPEnvelope soapEnvelop = soapMessage.getSOAPPart().getEnvelope();
		
		soapEnvelop.addNamespaceDeclaration("q0","http://nwcg.gov/services/ross/resource_order/1.1");
		soapEnvelop.addNamespaceDeclaration("soapenv","http://schemas.xmlsoap.org/soap/envelope/");
		soapEnvelop.addNamespaceDeclaration("xsd","http://www.w3.org/2001/XMLSchema");
		soapEnvelop.addNamespaceDeclaration("xsi","http://www.w3.org/2001/XMLSchema-instance");
		
		return soapEnvelop;
	}

	/* (non-Javadoc)
	 * @see com.yantra.interop.japi.YIFCustomApi#setProperties(java.util.Properties)
	 */
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
}