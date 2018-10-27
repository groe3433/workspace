package com.nwcg.icbs.yantra.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.util.common.XMLUtil;

/**
 * This class will make a webservice call to ROSS. Input to ROSS is read from a file. This
 * file needs to be present in the directory where this standalone application is run. The 
 * only dependency from Sterling perspective is XMLUtil code. For that reference, we need 
 * to have XMLUtil available as part of some jar. Also, we need to have 3 jars from 
 * IBM runtime directory in classpath
 * @author sgunda
 *
 */
public class TestSOAPMsgDispatcher {
	
	// Certificate Path
	public Dispatch<SOAPMessage> getDispatchFromNamespace(String strServiceName){
		String strNamespace = "http://nwcg.gov/services/ross/catalog/1.1";
		String strService = "CatalogService";
		String strPort = "CatalogPort";
		String strServiceGroup = "CATALOG";
		String strServiceGroupAddress = "https://esb-extint.lw-lmco.com:15558/soap/ross";
		
		System.out.println("TestSOAPMsgDispatcher::getDispatchFromNamespace " +
				"properties strNamespace="+strNamespace + " strService " + strService + " strPort " + strPort + 
				" strServiceGroup " + strServiceGroup + " strServiceGroupAddress " + strServiceGroupAddress);
		
		QName serviceName = new QName(strNamespace, strService);
        QName portName = new QName(strNamespace,strPort);
        Service service = Service.create(serviceName);
        service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, strServiceGroupAddress);
     
        Dispatch<SOAPMessage> dispatch = service.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);
        
        System.out.println("TestSOAPMsgDispatcher::getDispatchFromNamespace dispatch created");
	    try
	    {
        	Map<String, Object> rc = dispatch.getRequestContext();
	        setOutboundContext(rc);
	        System.out.println("TestSOAPMsgDispatcher::getDispatchFromNamespace setOutboundContext set");
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    	System.out.println("TestSOAPMsgDispatcher::cant associate context" + e.getMessage());
	    }        
        return dispatch;
	}
	
    protected void setOutboundContext( Map<String, Object> rc){
        rc.put (BindingProvider.USERNAME_PROPERTY, "icbs_dev");
        rc.put (BindingProvider.PASSWORD_PROPERTY, "password1!");        
    }
    
	public Document getDocumentFromFile(String fileName){
		Document tmpDoc = null;
		StringBuffer content = new StringBuffer();
		File f = new File(fileName);
		try {
			BufferedReader input = new BufferedReader(new FileReader(f));
			String line = null;
			while ((line = input.readLine())!= null){
				content.append(line);
				content.append(System.getProperty("line.separator"));
			}
			tmpDoc = XMLUtil.getDocument(content.toString());
		}
		catch(Exception e){
			System.out.println("Exception e : " + e.getMessage());
			e.printStackTrace();
		}
		return tmpDoc;
	}

	public String getStringFromFile(String fileName){
		StringBuffer content = new StringBuffer();
		File f = new File(fileName);
		try {
			BufferedReader input = new BufferedReader(new FileReader(f));
			String line = null;
			while ((line = input.readLine())!= null){
				content.append(line);
				content.append(System.getProperty("line.separator"));
			}
		}
		catch(Exception e){
			System.out.println("Exception e : " + e.getMessage());
			e.printStackTrace();
		}
		return content.toString();
	}
	
	public static void main(String[] args){
		try {
			TestSOAPMsgDispatcher msgDispatcher = new TestSOAPMsgDispatcher();
			MessageFactory mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
	        // Create SOAPMessage from XML file
	        SOAPMessage soapMessageRequest = mf.createMessage();
	        //SOAPBody soapbodyRequest = soapMessageRequest.getSOAPBody();
	        //soapbodyRequest.addDocument(msgDispatcher.getDocumentFromFile("catalog.xml"));
	        /*
	        soapMessageRequest.getSOAPHeader().detachNode();
	        soapMessageRequest.getSOAPHeader().setTextContent(
	        		XMLUtil.getXMLString(msgDispatcher.getDocumentFromFile("header.xml")));
	        */
	        SOAPPart soapPart = soapMessageRequest.getSOAPPart();
	        // Load the SOAP text into a stream source  
	        byte[] buffer                 = msgDispatcher.getStringFromFile("soapMsgReq.xml").getBytes();  
	        ByteArrayInputStream stream   = new ByteArrayInputStream(buffer);  
	        StreamSource source           = new StreamSource(stream);       
	        soapPart.setContent(source);
	        soapMessageRequest.writeTo(System.out);
	        
	        // Set Certificate Path
			System.setProperty("javax.net.ssl.trustStore", "/opt/WebSphere/Dev/AppServer/Key/keypass.jks");
			System.setProperty("javax.net.ssl.trustStorePassword","icbsross");
			System.setProperty("javax.net.ssl.trustStoreType", "JKS");
			System.setProperty("javax.net.ssl.keyStore", "/opt/WebSphere/Dev/AppServer/Key/keypass.jks");
			System.setProperty("javax.net.ssl.keyStorePassword", "icbsross") ;
			System.setProperty("javax.net.ssl.keyStoreType", "JKS");	        

	        	// Set request attributes like userid, password
	        	String strServiceName = "CreateCatalogItemReq";
			Dispatch<SOAPMessage> dispatch = msgDispatcher.getDispatchFromNamespace(strServiceName);
			
	        	// dispatch.invoke	        
            		SOAPMessage soapMessageResponse = dispatch.invoke(soapMessageRequest);	        
			System.out.println("sendMessageRequest got response "+ soapMessageResponse);
			System.out.println("Response Start ===========================  ");
			soapMessageResponse.writeTo(System.out);
			System.out.println("Response End ===========================  ");
		}
		catch(SOAPException se){
			System.out.println("SOAPException : " + se.getMessage());
			se.getStackTrace();
		}
		catch(IOException ioe){
			System.out.println("IOException : " + ioe.getMessage());
			ioe.getStackTrace();
		}
	}
}
