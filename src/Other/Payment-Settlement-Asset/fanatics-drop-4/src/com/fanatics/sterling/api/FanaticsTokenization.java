package com.fanatics.sterling.api;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fanatics.sterling.util.PasswordUtils;
import com.fanatics.sterling.util.RESTClient;
import com.fanatics.sterling.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

public class FanaticsTokenization {

	private static YFCLogCategory logger= YFCLogCategory.instance(YFCLogCategory.class);
	
	public Document invokeTokenization(YFSEnvironment yfsEnv, Document inXML){
		
		RESTClient rClient = new RESTClient();
		
		//Get REST connection details
		String authType = YFSSystem.getProperty("fan.rest.authtype"); 
		String baseUrl = YFSSystem.getProperty("fan.rest.base.url"); 
		String username = YFSSystem.getProperty("fan.rest.user"); 
		String password = YFSSystem.getProperty("fan.rest.pwd"); 
		String path = YFSSystem.getProperty("fan.rest.tokenization.path"); 
		
		rClient.setAuthType(authType);
		rClient.setBaseUrl(baseUrl);
		rClient.setUsername(username);
		rClient.setPassword(password);

		String restOutputStr = "";
		Document restOutputDoc = null;
		//Make REST call
		try{
			restOutputStr = rClient.postDataToServer(path, XMLUtil.getXMLString(inXML));
		}catch(Exception e){
			logger.error("Exception making REST call to fraud: " + e.getMessage() , e.getCause());
		}
		
		//Convert string to document
		try {
			restOutputDoc = XMLUtil.getDocument(restOutputStr);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			logger.error("Exception coverting REST response to XML: " + e.getMessage() , e.getCause());
		}
		
		
		return restOutputDoc;	
	}
	
	public Document encode64(YFSEnvironment yfsEnv, Document inXML){
		
		logger.info("encode64 input: " + XMLUtil.getXMLString(inXML));
		
		String hmac = XMLUtil.getXpathProperty(inXML,"Payment/@Hmac");
		logger.info("hmac: " + hmac);
		
		String hashStr = null;
		
		try {
			hashStr = PasswordUtils.getHash(hmac);
		} catch (Exception e) {
			logger.error("GetHash Exception: " + e.getMessage());
		}
		logger.info("hashStr: " + hashStr);

		Document docTokenResponse = SCXmlUtil.createDocument("Payment");
		docTokenResponse.getDocumentElement().setAttribute("Base64", hashStr);
		
		logger.info("encode64 output: " + XMLUtil.getXMLString(docTokenResponse));

		
		return docTokenResponse;
		
	}

}
