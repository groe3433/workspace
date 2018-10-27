package com.nwcg.icbs.yantra.commCore;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.handler.NWCGMessageHandler;
import com.nwcg.icbs.yantra.soap.NWCGAAUtil;
import com.nwcg.icbs.yantra.soap.NWCGIBAuthReqMsg;
import com.nwcg.icbs.yantra.soap.NWCGMessageFactory;
import com.nwcg.icbs.yantra.soap.NWCGSOAPMsg;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;

import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGIBNotificationWebService {

	
	//Todo: Need to review exactly what the SOAP server will call on this method
	public Document recieveMsg(YFSEnvironment env, Document xml) throws Exception{
	//public String recieveMsg(String xml){
		
		NWCGLoggerUtil.Log.info(" NWCGIBNotificationWebService.recieveMsg");
		
		String serviceName = getNotificationMessageName(xml);
		
		//String serviceGroupName = NWCGAAUtil.determineServiceGroup(serviceName);
		
		//get the distID and systID
		
		String distID = NWCGAAUtil.lookupNodeValue(xml,"distributionID");
		String systID = NWCGAAUtil.lookupNodeValue(xml,"systemID");
		
		NWCGLoggerUtil.Log.info("ServiceName:" + serviceName);
		
		
		NWCGMessageStore msgStore = NWCGMessageStore.getMessageStore();
		
		//Create the start message
		
		String system_key = msgStore.storeMessage(systID,"IB", NWCGAAUtil.serialize(xml), NWCGAAConstants.MESSAGE_TYPE_START, NWCGAAConstants.MESSAGE_STATUS_VOID , NWCGAAConstants.EXTERNAL_SYSTEM_NAME,"");
		String latest_system_key =  msgStore.storeMessage(systID,"IB", NWCGAAUtil.serialize(xml), NWCGAAConstants.MESSAGE_TYPE_LATEST, NWCGAAConstants.MESSAGE_STATUS_MESSAGE_RECEIVED, NWCGAAConstants.EXTERNAL_SYSTEM_NAME,"");

		boolean isSync = determineIsSync(serviceName);
		
		NWCGSOAPMsg msg = NWCGMessageFactory.newMessage(serviceName);
		
		msg.setXml(NWCGAAUtil.serialize(xml));
		
		
		//Note: This msg should be an instance of NWGCOBAuthReqMsg
		
		//NWGCSOAPMsg msg = NWGCSOAPUtil.parseSOAP(xml);
		

		//check to see if the request requires auth.  
		//A notification and ASYNC message response do not require auth
		if(reqRequiresAuth(serviceName)){

			msgStore.updateMessage(systID,"IB", NWCGAAUtil.serialize(xml), NWCGAAConstants.MESSAGE_TYPE_LATEST, NWCGAAConstants.MESSAGE_STATUS_AWAITING_AUTHORIZATION, NWCGAAConstants.EXTERNAL_SYSTEM_NAME, latest_system_key, true);
			
			if(!sendAuthRequest(xml)){
				msgStore.updateMessage(systID,"IB", NWCGAAUtil.serialize(xml), NWCGAAConstants.MESSAGE_TYPE_LATEST, NWCGAAConstants.MESSAGE_STATUS_AUTHORIZATION_FAILED, NWCGAAConstants.EXTERNAL_SYSTEM_NAME, latest_system_key, true);
			}
			
			
			//TODO: create AUTH request
			//For inbound, create request, send, and then wait
			
		}
		
		
		msgStore.updateMessage(systID,"IB", NWCGAAUtil.serialize(xml), NWCGAAConstants.MESSAGE_TYPE_LATEST, NWCGAAConstants.MESSAGE_STATUS_PROCESSING , NWCGAAConstants.EXTERNAL_SYSTEM_NAME, latest_system_key, true);

		if(isSync){
			//TODO: Create a Handler Factory
			
			
			NWCGMessageHandler handler = NWCGMessageHandlerFactory.createHandler("", serviceName);
					
			NWCGSOAPMsg resp_msg = handler.process(msg);
			
	
			String resp_msg_str = "";

			try{
				resp_msg_str = NWCGAAUtil.buildSOAP(resp_msg);
				
				
				msgStore.updateMessage(systID,"IB", NWCGAAUtil.serialize(xml), NWCGAAConstants.MESSAGE_TYPE_LATEST, NWCGAAConstants.MESSAGE_STATUS_PROCESSED, NWCGAAConstants.EXTERNAL_SYSTEM_NAME, latest_system_key, true);

				
			}catch(Exception e){
				NWCGLoggerUtil.Log.warning(e.toString());
			}
							
			//convert this string to a document
	
			NWCGLoggerUtil.Log.info("resp_msg_str:" + resp_msg_str);
			
			return NWCGAAUtil.buildXMLDocument(resp_msg_str);
		}
		
		//TODO: place message in queue 

		CommonUtilities.invokeService(env,NWCGAAConstants.SDF_POST_SOAP_SERVICE_NAME,xml);

		//TODO: send acknowledgement
		
		NWCGSOAPMsg ack = NWCGMessageFactory.newMessage("ACK");
		 
		ack.setUsername("");
		
		ack.setDistId(systID);
		
		ack.buildSOAPMsg();

		return NWCGAAUtil.buildXMLDocument(NWCGAAUtil.buildSOAP(ack));
		
		
	}


	boolean determineIsSync(String serviceName){
		
		String isSync = NWCGProperties.getProperty(serviceName+".isSync");
		
		return isSync.equalsIgnoreCase("TRUE");
		//return false;
	}
	boolean sendAuthRequest(Document xml){
		
		NWCGIBAuthReqMsg authReqMsg = (NWCGIBAuthReqMsg)NWCGMessageFactory.newMessage("AuthUserReq-OB");
		//authReqMsg.setPassword(password)
		
		String username = NWCGAAUtil.lookupNodeValue(xml, "username");
		String password = NWCGAAUtil.lookupNodeValue(xml, "password");
		
		authReqMsg.setUsername(username);
		authReqMsg.setPassword(password);
		
		String respMsg = "NONE";
		
		
		try{
		
			authReqMsg.buildSOAPMsg();
			
			String auth_resp = NWCGAAUtil.buildAndSend(authReqMsg, "ICBS-IB");
		
			
			String returnCode = NWCGAAUtil.lookupNodeValue(NWCGAAUtil.buildXMLDocument(auth_resp) , "ReturnCode");
			
			if(!returnCode.equals("0")){
				//auth failed
				
				
			}
			return true;

		}catch(Exception e){
			NWCGLoggerUtil.Log.warning(e.toString());
		}
		return false;
			
	}
	
	
	boolean reqRequiresAuth(String serviceName){
		
		
		String requiresAuth = NWCGProperties.getProperty(serviceName+".requiresAuth");
		
		return requiresAuth.equalsIgnoreCase("TRUE");
		
		//Depending on the service name, we determine if auth is required
		//If it is a notification then it does not require authentication
		
		
		//this will be in a prop file
		
		//Check for notification, auth, get results
		
		//return false;
	}
	
	public String getNotificationMessageName(Document doc){
		
		//iterate through all the message names and determine if the XML contains the approiate element
		
		//Document doc = NWCGAAUtil.buildXMLDocument(xml);
		
		Iterator i = NWCGProperties.notifyMsg.iterator();
		
		while(i.hasNext()){
			
			//Document doc = NWCGAAUtil.buildXMLDocument(msg_str);
			
			String msgName = (String)i.next();
			NodeList nL = doc.getElementsByTagName(msgName);
			
			if(nL.item(0) != null){
				return msgName;
			}
			
		}
		
		return null;
	}
	
}