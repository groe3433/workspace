package com.fanatics.sterling.stub;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.fanatics.sterling.constants.FANUserAuthConstants;
import com.fanatics.sterling.util.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;

public class FANBastilleResponse {

	private static YFCLogCategory logger= YFCLogCategory.instance(YFCLogCategory.class);
	
	public Document performAuthentication(Document docBasteelInputXml) {
		
		logger.verbose("Inside performAuthentication");
		logger.verbose("Input XML " + docBasteelInputXml);
		
		Document responseXml = null;
		try {
			Document docInputXml = docBasteelInputXml; //XMLUtil.getDocument(docBasteelInputXml);
			
			logger.verbose("Basteel input xml "+ XMLUtil.getXMLString(docInputXml));
			
			Element rootElement = docInputXml.getDocumentElement();
			
			String strLoginID = rootElement.getAttribute(FANUserAuthConstants.ATT_LoginID); 
			logger.verbose("Login ID is "+ strLoginID);
			
			if(strLoginID.equals("admin") || strLoginID.equals("web") || strLoginID.equals("Jeff Bolton") || strLoginID.equals("Jim") 
					|| strLoginID.equals("Raja") || strLoginID.equals("fanatics") || strLoginID.equals("weborderuser") || 
					strLoginID.equals("FAN_US_ADMIN") || strLoginID.equals("jack")){
				responseXml = XMLUtil.getDocument("<Login  LoginID='"+strLoginID+"' LoginStatus='Y' ></Login>");
			}
			else if(strLoginID.equals("invalid_user")) {
				responseXml = XMLUtil.getDocument("<Login LoginID='"+strLoginID+"' LoginStatus ='N' FailureReasonCode='012' FailureReasonMessage='Access Denied'></Login>");
				logger.verbose("User is invalid_user");
			}
			else{
				responseXml = XMLUtil.getDocument("<Login LoginID='"+strLoginID+"' LoginStatus ='N' FailureReasonCode='012' FailureReasonMessage='Access Denied'></Login>");
				logger.verbose("User is an invalid user");
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return responseXml;
		
	}
	
	
}
