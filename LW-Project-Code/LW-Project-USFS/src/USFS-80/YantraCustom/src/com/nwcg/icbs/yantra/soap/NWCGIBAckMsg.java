package com.nwcg.icbs.yantra.soap;

import javax.xml.soap.SOAPMessage;

public class NWCGIBAckMsg extends com.nwcg.icbs.yantra.soap.NWCGIBMsg {
	
	
	
	public NWCGIBAckMsg(){
		this.setServiceName("MessageAcknowledgement");
	}
	
	
public SOAPMessage buildSOAPMsg() throws Exception{
		
		
		SOAPMessage msg = super.buildSOAPMsg();
		
		
		try{
			
			/*MessageAcknowledgement resp = new MessageAcknowledgement();
			

			resp.setDistributionID(this.getDistId());
			
			ResponseStatusType rs = new ResponseStatusType();
			
			rs.setReturnCode(1);
			
			resp.setResponseStatus(rs);
						
			StringWriter writer = new StringWriter();
			
			//Marshaller.marshal(resp, writer);
			
			String reqStr = writer.toString();
			
			reqStr = reqStr.substring(reqStr.indexOf('>')+1);
			
			this.theTemplate.setSlot("body", reqStr);*/
			
			
		}catch(Exception e){
			System.out.println(e);
		}
		
		return msg;
		
		
}

	
}