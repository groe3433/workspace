package com.fanatics.sterling.api;

import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.FANConstants;
import com.fanatics.sterling.util.FANDBUtil;
import com.fanatics.sterling.util.FANDateUtils;
import com.fanatics.sterling.util.RESTClient;
import com.fanatics.sterling.util.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.core.YFSSystem;


public class CustomerUpdate {
	
	private static YFCLogCategory logger = YFCLogCategory.instance("com.yantra.yfc.log.YFCLogCategory");

	private Properties props = null;

	public Document invokeCustomerUpdateREST(YFSEnvironment env, Document inXML) throws Exception {

logger.debug("----------Input to invokeCustomerUpdateREST---------"+XMLUtil.getXMLString(inXML));
		
		RESTClient rClient = new RESTClient();
		
		//Get REST connection details
		String authType = YFSSystem.getProperty("fan.rest.customerupdate.authtype"); 
		String baseUrl = YFSSystem.getProperty("fan.rest.customerupdate.base.url"); 
		String username = YFSSystem.getProperty("fan.rest.customerupdate.user"); 
		String password = YFSSystem.getProperty("fan.rest.customerupdate.pwd"); 
		String path = YFSSystem.getProperty("fan.rest.customerupdate.path"); 
		
		rClient.setAuthType(authType);
		rClient.setBaseUrl(baseUrl);
		rClient.setUsername(username);
		rClient.setPassword(password);


		String restOutputStr = "";
		Document restOutputDoc = null;

		
		//Make REST call
		try{
			restOutputStr = rClient.postDataToServer(path, XMLUtil.getXMLString(inXML));
			
			if(restOutputStr == null){
				logger.error("Output Document from callREST is null");
			}
			
			
		}catch(Exception e){
			logger.error("Exception making REST call to payment service: " + e.getMessage() , e.getCause());
		}
		
		//Convert string to document
		try {
			restOutputDoc = XMLUtil.getDocument(restOutputStr);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			logger.error("Exception coverting REST response to XML: " + e.getMessage() , e.getCause());
		}
		
		
	logger.debug("----------Output to invokeCustomerUpdateREST---------"+XMLUtil.getXMLString(restOutputDoc));
		
	return restOutputDoc;
	}

}
