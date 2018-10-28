package com.nwcg.icbs.yantra.soap;

import javax.xml.soap.SOAPMessage;


public class NWCGIBAuthReqMsg extends NWCGIBReqMsg{

	public NWCGIBAuthReqMsg() {
		this.setServiceName("AuthUserReq-IB");
		
	}
	
	
	public SOAPMessage buildSOAPMsg() throws Exception{
		
		
		SOAPMessage msg = super.buildSOAPMsg();
		
		/*AuthUserReq req = new AuthUserReq();
		
		req.setUsername(super.getUsername());
		req.setAction("action");
		DatatypeFactory dtf = DatatypeFactory.newInstance();
		req.setAuthTimestamp(dtf.newXMLGregorianCalendar());
		req.setOnetimePassword(super.getPassword());
		
		
		StringWriter writer = new StringWriter();
		
		//Marshaller.marshal(req, writer);
		
		String reqStr = writer.toString();
		
		reqStr = reqStr.substring(reqStr.indexOf('>')+1);
		
		this.theTemplate.setSlot("body", reqStr);
		
		*/
		
		return msg;

	}
}