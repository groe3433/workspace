package com.nwcg.icbs.yantra.soap;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;


public class NWCGMessageFactory {


	public static NWCGSOAPMsg newAckMessage(String type){
		
		return newMessage(type+"_ACK");
		
	}
	public static NWCGSOAPMsg newMessage(String type){
		
		
		try{

	
			if(type.equals(NWCGAAConstants.GET_INCIDENT_REQ_MSG_NAME)){
				
				
				
				return new NWCGGetIncidentReqMsg();
			}
			
			if(type.equals("AuthUserReq-IB")){
				return new NWCGIBAuthReqMsg();
			}
			
			
			if(type.equals("AuthUserResp-OB")){
				return new NWCGOBAuthRespMsg();
			}
			
			if(type.equals("ResponseStatusType")){
				
				return new NWCGResponseStatusTypeReq();
			}
			
			if(type.equals("ACK")){
				
				return new NWCGIBAckMsg();
			}
			
			return new NWCGSOAPMsg();
			
			}catch(Exception e){
				System.out.println(e);
			}
			
		NWCGSOAPMsg msg = new NWCGSOAPMsg();
		
		msg.setServiceName(type);
		return msg;
		
		
		
	}
	
	public static String getServiceName(Document docMsg){
						
		String serviceName = "";
		try{
			NodeList nL = docMsg.getElementsByTagName("messageName");
			Node nMC = nL.item(0);
			String messageName = nMC.getFirstChild().getNodeValue();
			return messageName;
		}catch(Exception e){
			System.out.println(e);
		}
		return serviceName;
	}
	
}
