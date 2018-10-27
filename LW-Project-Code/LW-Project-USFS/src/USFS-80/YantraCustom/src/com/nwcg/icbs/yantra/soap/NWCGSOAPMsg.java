package com.nwcg.icbs.yantra.soap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;

import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.commCore.NWCGCodeTemplate;
import com.nwcg.icbs.yantra.commCore.NWCGProperties;

public class NWCGSOAPMsg {
	
	private Object _attribute;
	
	SOAPMessage theSOAPMsg;
	String xml;
	
	String body;
	
	String username;
	String password;
	
	String serviceName;
	
	String distId;
	
	public NWCGCodeTemplate theTemplate;
	
	public String templateTxt;
	
	
	public NWCGSOAPMsg(){
		this.xml = "(NONE)";

	}

	public NWCGSOAPMsg(String xml){
		this.xml = xml;
	}
	
	NWCGSOAPMsg(SOAPMessage msg){
		 theSOAPMsg = msg;
	}
	
	public SOAPMessage getSOAPMessageImpl(){
		
		return theSOAPMsg;
	}
	
	public void setSOAPMessageImpl(SOAPMessage theSOAPMsg){
		this.theSOAPMsg = theSOAPMsg;
	}
	
	public void getHeader() {
		throw new UnsupportedOperationException();
	}

	public void getEnvelope() {
		throw new UnsupportedOperationException();
	}

	public String getBody() {
		return body;
	}

	public void setEnvelope() {
		throw new UnsupportedOperationException();
	}

	public void setBody(String body) {
		this.body = body;
		//throw new UnsupportedOperationException();
	}

	public void setHeader() {
		throw new UnsupportedOperationException();
	}



	public void getWebServiceMethod() {
		throw new UnsupportedOperationException();
	}

	public void setWebServiceMethod() {
		throw new UnsupportedOperationException();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public SOAPMessage buildSOAPMsg() throws Exception{
		
		MessageFactory mf = MessageFactory.newInstance();		
		theSOAPMsg = mf.createMessage();
		
		String path = NWCGProperties.getProperty("SOAP_TEMPLATE");// need to find out what the template is.
		

	    byte[] data = null;
	          
        File f = new File(path);
        FileInputStream fs = new FileInputStream(f);
        
        data = new byte[fs.available()];
        fs.read(data);
        fs.close();
        
        String msg = new String(data);
		theTemplate = new NWCGCodeTemplate(msg);
		
		//read the XML template

		//theSOAPMsg.getSOAPHeader().addHeaderElement(Name name) 
		
		//Create the security element
		/*SecurityType sec = new SecurityType();
		
		
		UsernameTokenType stut = new UsernameTokenType();
		
		stut.setUsername(this.getUsername());
		
		PasswordType pw = new PasswordType();*/
		//pw.setContent("foo");
		
		
		//PasswordTypepeType pttt = new PasswordTypeTypeType(0,"X");
	
		//pw.setType(pttt);
/*		stut.setPassword(pw);
		
		stut.setNonce("X");
		stut.setCreated(DatatypeFactory.newInstance().newXMLGregorianCalendar());
	
		sec.setUsernameToken(stut);*/

		// Create a File to marshal to
	
		//Create the messageContext
		/*parse.MessageContext mc = new parse.MessageContext();
		mc.setDistributionID(this.getDistId());
		mc.setDateTimeSent(new Date());
		mc.setMessageName(this.getServiceName());
		mc.setPassword(this.getPassword());
		mc.setSecuritySessionID("100");
		mc.setSenderID("100");
		mc.setSystemID("100");*/
		
		/*mc.setUserOrganization("org");
		
		mc.setDistributionStatus(new DistributionStatusSimpleType(1,"Actual"));
		
		mc.setDistributionType(new DistributionTypeSimpleType(1,"Ack"));
		mc.setNamespaceName("A");
		mc.setSystemType(new SystemTypeSimpleType(1,"ROSS"));
		
*/
		StringWriter writer = new StringWriter();
		
		// Marshal the person object
		
		try{
			//Marshaller.marshal(sec, writer);
			
			String secStr = writer.toString();
			
			//remove the XML element
			
			secStr = secStr.substring(secStr.indexOf('>')+1);
						
			writer = new StringWriter();
			
			//Marshaller.marshal(mc, writer);
			
			String mcStr = writer.toString();
			
			mcStr = mcStr.substring(mcStr.indexOf('>')+1);
			
			theTemplate.setSlot("header", secStr+"\n"+mcStr);
			
			theTemplate.setSlot("body", "");

			
		}catch(Exception e){
			System.out.println(e);
		}
		
		
		return theSOAPMsg;
	}
		
	
	public void serialize(SOAPMessage soapMessage) throws Exception {
		
		
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
        XMLSerializer serializer = new XMLSerializer(System.out, format);
        serializer.serialize(doc);
    }

	public String generateCode(){
		
		//if not using the Standard soap envelop, then theTemplate will be null
		
		if(theTemplate == null) return this.getXml();
		return theTemplate.generateCode();
		
	}



	public String getDistId() {
		return distId;
	}


	public void setDistId(String distId) {
		this.distId = distId;
	}
		
	
}