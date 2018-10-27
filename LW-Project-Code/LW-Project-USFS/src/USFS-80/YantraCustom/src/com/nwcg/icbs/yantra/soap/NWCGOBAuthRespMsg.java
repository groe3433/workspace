package com.nwcg.icbs.yantra.soap;

import java.io.StringWriter;
import javax.xml.soap.*; 
public class NWCGOBAuthRespMsg extends com.nwcg.icbs.yantra.soap.NWCGOBRespMsg {

	
	String authMessage;
	String username;
	String isAuthenticated;
	String userFullname;
	String responseStatus;
	
	
	NWCGOBAuthRespMsg(){
		this.setServiceName("AuthUserResp");
		
		
	}

	
	public String getAuthMessage() {
		return authMessage;
	}
	public void setAuthMessage(String authMessage) {
		this.authMessage = authMessage;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getIsAuthenticated() {
		return isAuthenticated;
	}
	public void setIsAuthenticated(String isAuthenticated) {
		this.isAuthenticated = isAuthenticated;
	}
	public String getUserFullname() {
		return userFullname;
	}
	public void setUserFullname(String userFullname) {
		this.userFullname = userFullname;
	}
	public String getResponseStatus() {
		return responseStatus;
	}
	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}
	
	
public SOAPMessage buildSOAPMsg() throws Exception{
		
		System.out.println("NWGCIBAuthReqMsg. buildSOAPMsg()");
		SOAPMessage msg = super.buildSOAPMsg();
		
		
		try{
			
			/*AuthUserResp resp = new AuthUserResp();
			
			resp.setAuthenticationMessage("OK");
			resp.setIsAuthenticatedIndicator(true);
			ResponseStatusType rst = new ResponseStatusType();
			rst.setReturnCode(0);
			resp.setResponseStatus(rst);
			resp.setUserFullname(this.getUserFullname());
			
			resp.setUsername("authme");
			*/
			
			StringWriter writer = new StringWriter();
			
			//Marshaller.marshal(resp, writer);
			
			String reqStr = writer.toString();
			
			reqStr = reqStr.substring(reqStr.indexOf('>')+1);
			
			this.theTemplate.setSlot("body", reqStr);
			
		}catch(Exception e){
			System.out.println(e);
		}
		
		return msg;
		
		
}
	
	
		
		
	
}