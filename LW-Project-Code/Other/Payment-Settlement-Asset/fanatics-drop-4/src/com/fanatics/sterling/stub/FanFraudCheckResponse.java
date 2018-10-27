package com.fanatics.sterling.stub;

import java.io.IOException;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;

/**
 * This class contains the stub response for the fanatics internal and external fraud checks which imitates the REST response for the fanatics implementation.
 * @(#) FanFraudCheckResponse.java    
 * Created on   May 4, 2016
 *              5:17:30 PM
 *
 * Package Declaration: 
 * File Name:       FanFraudCheckResponse.java
 * Package Name:    com.fanatics.sterling.stub;
 * Project name:    fanatics
 * Type Declaration:    
 * Class Name:      FanFraudCheckResponse
 * 
 * @author jtyrrell
 * @version 1.0
 * @history May 4, 2016
 *     
 * 
 *  
 *
 * (C) Copyright 2016-2017 by owner.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information
 * of the owner. ("Confidential Information").
 * Redistribution of the source code or binary form is not permitted
 * without prior authorization from the owner.
 *
 */

public class FanFraudCheckResponse {

private static YFCLogCategory logger = YFCLogCategory.instance("com.yantra.yfc.log.YFCLogCategory");


	
	/*
	 * State
	 * -----------
	 * NY = Reject
	 * FL = Pass
	 * Other = Pending Review
	 */
	public Document internalFraudResponse(YFSEnvironment yfsEnv, Document doc){
	
		Document docFraudResponse = setDocumentAttrs(doc, "INTERNAL");
		
		logger.verbose("fraudResponse input: " + XMLUtil.getXMLString(doc));
	
		String state = XMLUtil.getXpathProperty(doc,"Order/PersonInfoShipTo/@State");
		logger.verbose("state is "+ state);
		
		if(state.equalsIgnoreCase("NY")){
			docFraudResponse.getDocumentElement().setAttribute("FraudResponseCode", "2");
		}
		else if (state.equalsIgnoreCase("FL")){
			docFraudResponse.getDocumentElement().setAttribute("FraudResponseCode", "0");
		}
		else{
			docFraudResponse.getDocumentElement().setAttribute("FraudResponseCode", "1");
			docFraudResponse.getDocumentElement().setAttribute("FraudToken", XMLUtil.getXpathProperty(doc,"Order/@OrderNo"));
			
		}
		
		logger.verbose("fraudResponse output: " + XMLUtil.getXMLString(docFraudResponse));

		//try {
			// CommonUtil.invokeService(yfsEnv, "FanSendFraudResponse", docFraudResponse);
		//} catch (Exception e) {
			//System.out.println(e.getMessage());
			//e.printStackTrace();
		//}
		return docFraudResponse;
	}
	
	/*
	 * State
	 * -----------
	 * FL = Pass
	 * Other = Reject
	 */
	public Document externalFraudResponse(YFSEnvironment yfsEnv, Document doc){
	
		Document docFraudResponse = setDocumentAttrs(doc, "ACCERTIFY");
		
		logger.verbose("fraudResponse input: " + XMLUtil.getXMLString(doc));
	
		String state = XMLUtil.getXpathProperty(doc,"Order/PersonInfoShipTo/@State");
		
		if (state.equalsIgnoreCase("FL")){
			docFraudResponse.getDocumentElement().setAttribute("FraudResponseCode", "0");
		}
		else{
			docFraudResponse.getDocumentElement().setAttribute("FraudResponseCode", "2");
		}
		
		logger.verbose("fraudResponse output: " + XMLUtil.getXMLString(docFraudResponse));
		
		try {
			 CommonUtil.invokeService(yfsEnv, "FanSendFraudResponse", docFraudResponse);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		return docFraudResponse;
	}

	private Document setDocumentAttrs(Document inDoc, String sender){
		
		Document docFraudResponse = SCXmlUtil.createDocument("Order");

		docFraudResponse.getDocumentElement().setAttribute("DocumentType", XMLUtil.getXpathProperty(inDoc,"Order/@DocumentType"));
		docFraudResponse.getDocumentElement().setAttribute("EnterpriseCode", XMLUtil.getXpathProperty(inDoc,"Order/@EnterpriseCode"));
		docFraudResponse.getDocumentElement().setAttribute("OrderNo", XMLUtil.getXpathProperty(inDoc,"Order/@OrderNo"));
		docFraudResponse.getDocumentElement().setAttribute("OrderDate", XMLUtil.getXpathProperty(inDoc,"Order/@OrderDate"));
		docFraudResponse.getDocumentElement().setAttribute("SellerOrganizationCode", XMLUtil.getXpathProperty(inDoc,"Order/@SellerOrganizationCode"));
		docFraudResponse.getDocumentElement().setAttribute("FraudResponseSender", sender);
		
		if (sender.equals("ACCERTIFY"))
		 docFraudResponse.getDocumentElement().setAttribute("FraudToken", XMLUtil.getXpathProperty(inDoc,"Order/@FraudToken"));
		
		Element eleFraudResponse = docFraudResponse.getDocumentElement();

		Element eleCustomAttr = docFraudResponse.createElement("CustomAttributes");
		eleCustomAttr.setAttribute("CustomerOrderNo", XMLUtil.getXpathProperty(inDoc,"Order/CustomAttributes/@CustomerOrderNo"));
		eleFraudResponse.appendChild(eleCustomAttr);

		
		return docFraudResponse;
	}
	
	private int tokenGenerator(){
		Random random = new Random();
		int randomInt = random.nextInt((1000000 - 1000 + 1) - 1000);
		return randomInt;
	}
	
	
	public static void main(String[] args) throws YFSUserExitException, ParserConfigurationException, SAXException, IOException {
		
		String inputString = "<Order OrderNo=\"Y100000006\" DocumentType=\"0001\" EnterpriseCode=\"FANATICS_US\" OrderDate=\"20160412021203\" SellerOrganizationCode=\"951\"> <CustomAttributes CustomerOrderNo=\"12344567\"/> <PersonInfo AddressLine1=\"15 drive\" AddressLine2=\"\" AddressLine3=\"\" AddressLine4=\"\" AddressLine5=\"\" AddressLine6=\"\" Country=\"US\" ZipCode=\"90210\" State=\"FL\"/> </Order>";	
		
		Document doc = XMLUtil.getDocument(inputString);
		
		logger.info("Input: " + SCXmlUtil.getString(doc));
				
		FanFraudCheckResponse response = new FanFraudCheckResponse();
		//doc = response.internalFraudResponse(doc);
		//addr.getAppeasementOffers(null, doc);
		//logger.info("Output: " + SCXmlUtil.getString(doc));

		
	}

}
