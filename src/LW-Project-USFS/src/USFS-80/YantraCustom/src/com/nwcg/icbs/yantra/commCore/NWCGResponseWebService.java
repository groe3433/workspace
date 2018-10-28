package com.nwcg.icbs.yantra.commCore;

import org.w3c.dom.Document;

//import com.nwcg.icbs.yantra.handler.NWCGMessageHandler;
import com.nwcg.icbs.yantra.handler.NWCGOBAuthMessageHandler;
import com.nwcg.icbs.yantra.soap.NWCGAAUtil;
import com.nwcg.icbs.yantra.soap.NWCGSOAPMsg;
import com.yantra.yfs.japi.YFSEnvironment;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;


public class NWCGResponseWebService {

	/**
	 * 1. store message using Sterling DB. output of this will entire message with key
	 * 2. create a JMS message msg, and embeed key in message 
	 * 3. place message on key
	 * 4. respond with ack to ross that contains the message response key
	 */
	
	
	//Todo: Need to review exactly what the SOAP server will call on this method
	public Document recieveMsg(YFSEnvironment env, Document doc) throws Exception{

		
		NWCGAAUtil.logInfo("NWCGResponseWebService", "recieveMsg");
	
		NWCGSOAPMsg msg = new NWCGSOAPMsg();
		msg.setXml(NWCGAAUtil.serialize(doc));
		
		// only reason to use two parameter in a factory is to invoke a particular
		// MessageHandler, so just do it outside.   --yfu
		//NWCGMessageHandler handler = NWCGMessageHandlerFactory.createHandler(NWCGAAConstants.RESPONSE_SERVICE_GROUP_NAME,NWCGAAConstants.RESPONSE_STATUS_TYPE_SERVICE_NAME);
		NWCGOBAuthMessageHandler handler = new NWCGOBAuthMessageHandler();
		NWCGSOAPMsg resp_msg = handler.process(msg);
		
		String resp_msg_str = "";
		
		
		try{
			
			resp_msg_str = NWCGAAUtil.buildSOAP(resp_msg);
			
	
		}catch(Exception e){
			NWCGLoggerUtil.Log.warning(e.toString());
		}
				
		
		//convert this string to a document
		
		return NWCGAAUtil.buildXMLDocument(resp_msg_str);
		
	}
	

	
}