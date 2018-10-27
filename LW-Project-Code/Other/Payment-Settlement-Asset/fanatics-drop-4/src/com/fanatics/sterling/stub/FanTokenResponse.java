package com.fanatics.sterling.stub;

import java.io.IOException;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fanatics.sterling.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;


public class FanTokenResponse {

private static YFCLogCategory logger = YFCLogCategory.instance("com.yantra.yfc.log.YFCLogCategory");

	/*
	 * Input -  <Payment CybersourceToken="" />
	 * Output - <Payment FanaticsToken="" />
	 * 
	 */
	
	public Document tokenizeToken(YFSEnvironment yfsEnv, Document doc){
		
		logger.verbose("tokenizeToken input: " + XMLUtil.getXMLString(doc));
		
		String token = XMLUtil.getXpathProperty(doc,"Payment/@CybersourceToken");
		
		Document docTokenResponse = SCXmlUtil.createDocument("Payment");
		docTokenResponse.getDocumentElement().setAttribute("FanaticsToken", tokenGenerator(token));
		
		logger.verbose("tokenizeToken output: " + XMLUtil.getXMLString(docTokenResponse));

		
		return docTokenResponse;
	}
	
	private String tokenGenerator(String token){
		
		final int mid = token.length() / 2; 
		String[] parts = {token.substring(0, mid),token.substring(mid)};
		
		long tokenLong2 = Long.parseLong(parts[1]);
		
		Integer x = (int) (long) tokenLong2;
		if(x<0)
			x=x*-1;
		
		Random random = new Random();
		int randomInt2 = random.nextInt(x);
		
		return String.valueOf(randomInt2) + String.valueOf(randomInt2);
	}
	
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		
		String inputString = "<Payment CybersourceToken=\"4710174816256477004106\" />";
		Document doc = XMLUtil.getDocument(inputString);
		
		System.out.println("Input: " + SCXmlUtil.getString(doc));
				
		FanTokenResponse response = new FanTokenResponse();
		doc = response.tokenizeToken(null, doc);
		//doc = response.internalFraudResponse(doc);
		//addr.getAppeasementOffers(null, doc);
		System.out.println("Output: " + SCXmlUtil.getString(doc));
		
		
		//FanTokenResponse response = new FanTokenResponse();
		//System.out.println(response.tokenGenerator("4710174816256477004106"));

		
	}

}
