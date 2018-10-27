package com.nwcg.icbs.yantra.soap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;

public class NWCGSOAPUtil {
	

	/*static MessageSim rossSim;
	static MessageSim icbsIBSim;
	static MessageSim icbsOBSim;
	*/
	/*public static void setRossSim(MessageSim rossSim){
		NWCGSOAPUtil.rossSim = rossSim;
		
	}
	
	public static void setIcbsOBSim(MessageSim icbsOBSim){
		NWCGSOAPUtil.icbsOBSim = icbsOBSim;
		
	}
	
	public static void setIcbsIBSim(MessageSim icbsIBSim){
		NWCGSOAPUtil.icbsIBSim = icbsIBSim;
		
	}
	
*/	public NWCGSOAPMsg parseSOAP(String aSoapMsgString) throws SOAPException {
		
		MessageFactory mf  = MessageFactory.newInstance();
		MimeHeaders mh = new MimeHeaders();
		StringBuffer sb = new StringBuffer(aSoapMsgString);
		ByteArrayInputStream bais = new ByteArrayInputStream( aSoapMsgString.getBytes());
		try{
			SOAPMessage msg = mf.createMessage(mh, bais);
			NWCGSOAPMsg nmsg = new NWCGSOAPMsg(msg);
			return nmsg;
		}catch(Exception e){
			System.out.println(e);
		}
		return null;
		//throw new UnsupportedOperationException();
	}

	
	
	public static String serialize(SOAPMessage soapMessage) throws Exception {
		
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		
		ByteArrayOutputStream baws = new ByteArrayOutputStream();
		
		soapMessage.writeTo(baws);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(baws.toByteArray());
		
		
		Document doc = db.parse(bais);
		
		
		org.apache.xml.serialize.OutputFormat format = new org.apache.xml.serialize.OutputFormat(doc);
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(2);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        
        XMLSerializer serializer = new XMLSerializer(baos, format);
        serializer.serialize(doc);
        
        return baos.toString();
    }

	
	public static String buildSOAP(NWCGSOAPMsg aSOAPMessage) throws Exception{
		
		//System.out.println("BuildSoap.serviceName:"+aSOAPMessage.getServiceName());
		
		
		//SOAPMessage sMsg = aSOAPMessage.buildSOAPMsg();
		
		//return serialize(sMsg);
		
		return aSOAPMessage.generateCode();
		
		//return aSOAPMessage.getServiceName();
		/*
		//extract the SOAPMssage
		
		SOAPMessage msg = aSOAPMessage.getSOAPMessageImpl();
		
		ByteArrayOutputStream  baos = new ByteArrayOutputStream();
		//writeTo
		baos.writeTo(baos);
				
		return baos.toString();		
		*/

	}

	public static String buildAndSend(NWCGSOAPMsg aSOAPMessage, String target) throws Exception {
		
		
		
		
		String msg_str = buildSOAP(aSOAPMessage);
		
		String reply = sendHttpMsg(msg_str, target);
		
		return reply;
		
	}
	
	
	public static String sendHttpMsg(String msg_str, String target){/*
		
		String urlAddress = null;
		
		int port = -1;
		
		if(target.equals("ROSS")){
			
			NodeState ns = SimServlet.rossState;
			
			urlAddress = ns.targetHost;
			port = SimServlet.rossPort;
		}
		
		if(target.equals("ICBS-IB")){
			
			NodeState ns = SimServlet.icbsState;
			
			urlAddress = ns.targetHost;
			port = SimServlet.icbsIBPort;
		}
		
		if(target.equals("ICBS-OB")){
			
			NodeState ns = SimServlet.icbsState;
			
			urlAddress = ns.targetHost;
			port = SimServlet.icbsOBPort;
		}
		
		String reply = sendHttpMsg(msg_str,urlAddress, port, target);*/
		
		return null;
	}
	
	public static String sendHttpMsg(String xmldata, String hostname, int port, String target){/*
		
		String respText = "";
		try {
		     			
			respText = SoapServer.sendMsg(xmldata, hostname, port);
		     
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		    
		    return respText;
	*/
		return null;
		}
	
	




	
/*
	public static String sendHttpMsg(String msg, String urlAddress){
		 //System.out.println("Reciever: " + urlAddress);
		 //System.out.println(msg);
		 
		SimServlet.ross = new RossSimulator();
		SimServlet.icbs_OB = new NWGCOBMsgService();
		SimServlet.icbs_IB = new NWGCIBWebService();
		
		//TODO: Open HTTP socket to server
		
		
		if(urlAddress.equals("ROSS")){
			return SimServlet.ross.recieveMsg(msg);
		}
		
		if(urlAddress.equals("ICBS-IB")){
			
			return SimServlet.icbs_IB.recieveMsg(msg);
		}
		
		if(urlAddress.equals("ICBS-OB")){
			
			return SimServlet.icbs_OB.recieveMsg(msg);
		}
		
		
		return "reply";
		
		
	}
	
	*/
	public void sendMessageAsync(NWCGSOAPMsg aSoapMessage) {
		throw new UnsupportedOperationException();
	}

	public NWCGSOAPMsg sendMessageSync(NWCGSOAPMsg aSoapMessage) {
		throw new UnsupportedOperationException();
	}
}