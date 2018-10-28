package com.nwcg.icbs.yantra.soap;

//import gov.nwcg.services.ross.common_types._1.ResponseStatusType;

import java.io.StringWriter;

import javax.xml.soap.SOAPMessage;

public class NWCGResponseStatusTypeReq extends NWCGSOAPMsg{

	NWCGResponseStatusTypeReq(){
		this.setServiceName("ResponseStatusType");
	}
	
	public SOAPMessage buildSOAPMsg() throws Exception{
		
		SOAPMessage msg = super.buildSOAPMsg();
		
		//ResponseStatusType req = new ResponseStatusType();
		
		//req.setReturnCode(1);
		

		StringWriter writer = new StringWriter();
		
		//Marshaller.marshal(req, writer);
		
		String reqStr = writer.toString();
		
		reqStr = reqStr.substring(reqStr.indexOf('>')+1);
		
		this.theTemplate.setSlot("body", reqStr);
		
		
		
		return msg;

	}
}
