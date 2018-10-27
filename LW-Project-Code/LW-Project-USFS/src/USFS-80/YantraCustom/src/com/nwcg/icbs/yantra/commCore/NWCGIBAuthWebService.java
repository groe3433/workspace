package com.nwcg.icbs.yantra.commCore;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.handler.NWCGMessageHandler;
import com.nwcg.icbs.yantra.handler.NWCGOBAuthMessageHandler;
import com.nwcg.icbs.yantra.handler.NWCGResponseStatusTypeHandler;
import com.nwcg.icbs.yantra.soap.NWCGAAUtil;
import com.nwcg.icbs.yantra.soap.NWCGSOAPMsg;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGIBAuthWebService {

	/**
	 * 1. store message using Sterling DB. output of this will entire message with key
	 * 2. create a JMS message msg, and embeed key in message 
	 * 3. place message on key
	 * 4. respond with ack to ross that contains the message response key
	 */
	
	
	
	public Document recieveMsg(YFSEnvironment env, Document xml) throws Exception{


		NWCGAAUtil.logInfo("NWCGIBAuthWebService", "recieveMsg");
		
		//Note: This msg should be an instance of NWGCOBAuthReqMsg
		
		NWCGSOAPMsg msg = new NWCGSOAPMsg();
		msg.setXml(NWCGAAUtil.serialize(xml));
			
					
		NWCGMessageHandler handler = NWCGMessageHandlerFactory.createHandler(NWCGAAConstants.AUTH_SERVICE_GROUP_NAME, NWCGAAConstants.AUTH_USER_REQ_IB_SERVICE_NAME);

		NWCGSOAPMsg resp_msg = handler.process(msg);
		String resp_msg_str = "";
		
		try{
			
			resp_msg_str = NWCGAAUtil.buildSOAP(resp_msg);
			
		}catch(Exception e){
			NWCGLoggerUtil.Log.warning(e.toString());
		}
						
		return NWCGAAUtil.buildXMLDocument(resp_msg_str);
		

	}
	
	boolean reqRequiresAuth(String serviceName){
		
		//Depending on the service name, we determine if auth is required
		
		
		//this will be in a prop file
		
		//Check for notification, auth, get results
		
		return false;
	}
	
	NWCGMessageHandler getHandler(String serviceName){
	
		NWCGLoggerUtil.Log.info("getHandler.ServiceName: " + serviceName);
		if(serviceName.equals("AuthUserReq-IB")){
			
			return new NWCGOBAuthMessageHandler();
		}
		
		
		if(serviceName.equals("ResponseStatusType")){
			
			return new NWCGResponseStatusTypeHandler();
		}
		
		return new NWCGMessageHandler();
			
	}
	
}