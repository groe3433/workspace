package com.fanatics.sterling.ue;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;


import com.fanatics.sterling.constants.CustomerMasterConstants;
import com.fanatics.sterling.util.XMLUtil;
import com.fanatics.sterling.util.CommonUtil;

import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.pca.ycd.japi.ue.YCDVerifyAddressWithAVSUE;


public class FANYCDVerifyAddressWithAVSUE implements YCDVerifyAddressWithAVSUE{

	
	private static YFCLogCategory logger = YFCLogCategory.instance("com.yantra.yfc.log.YFCLogCategory");

	public Document verifyAddressWithAVS(YFSEnvironment env, Document inDoc) throws YFSUserExitException{
		
		logger.verbose("FANYCDVerifyAddressWithAVSUE.verifyAddressWithAVS -> Begin  ");
		logger.verbose("inDoc:  " + XMLUtil.getXMLString(inDoc));

		Document outDoc = null;
		Document inDocToCC =null; 
		
		try {
			
		Document inDocFromREST = createInDoc(inDoc);
		

			

		outDoc = CommonUtil.invokeService(env, CustomerMasterConstants.SERVICE_FanaticsAddressValidationREST, inDocFromREST);

		inDocToCC = addInDocName(inDoc, outDoc);

		
		
		}catch (Exception e) {
			logger.error("FANYCDVerifyAddressWithAVSUE --> ERROR: " + e.getMessage(), e.getCause());
		}
		
		
		logger.verbose("doc -> " + XMLUtil.getXMLString(inDocToCC));		
		logger.verbose("FANYCDVerifyAddressWithAVSUE.verifyAddressWithAVS -> End");		

		return inDocToCC;
		
	}
	
	private Document createInDoc(Document inDoc) throws Exception{
		logger.verbose("--------------FANYCDVerifyAddressWithAVSUE------createInDoc entry----");

		String inputString ="<PersonInfo AddressLine1=\"\" AddressLine2=\"\" "
				+ "AddressLine3=\"\" AddressLine4=\"\" AddressLine5=\"\" AddressLine6=\"\" City=\"\" "
				+ "Country=\"\" Latitude=\"\" Longitude=\"\" State=\"\" ZipCode=\"\"/>";
		Document docREST = XMLUtil.getDocument(inputString);

		Element eleRESTPersonInfo = (Element) docREST.getElementsByTagName(CustomerMasterConstants.ATT_PersonInfo).item(0);
		Element eleInDocPersonInfo = (Element) inDoc.getElementsByTagName(CustomerMasterConstants.ATT_PersonInfo).item(0);

		eleRESTPersonInfo.setAttribute("AddressLine1", eleInDocPersonInfo.getAttribute("AddressLine1"));
		eleRESTPersonInfo.setAttribute("AddressLine2", eleInDocPersonInfo.getAttribute("AddressLine2"));
		eleRESTPersonInfo.setAttribute("AddressLine3", eleInDocPersonInfo.getAttribute("AddressLine3"));
		eleRESTPersonInfo.setAttribute("AddressLine4", eleInDocPersonInfo.getAttribute("AddressLine4"));
		eleRESTPersonInfo.setAttribute("AddressLine5", eleInDocPersonInfo.getAttribute("AddressLine5"));
		eleRESTPersonInfo.setAttribute("AddressLine6", eleInDocPersonInfo.getAttribute("AddressLine6"));
		eleRESTPersonInfo.setAttribute("City", eleInDocPersonInfo.getAttribute("City"));
		eleRESTPersonInfo.setAttribute("Country", eleInDocPersonInfo.getAttribute("Country"));
		eleRESTPersonInfo.setAttribute("State", eleInDocPersonInfo.getAttribute("State"));
		eleRESTPersonInfo.setAttribute("ZipCode", eleInDocPersonInfo.getAttribute("ZipCode"));

		logger.verbose("docREST-->" + docREST);
 
		logger.verbose("--------------FANYCDVerifyAddressWithAVSUE------createInDoc exit----");

		return docREST;
	}

	
	private Document addInDocName(Document inDoc, Document outDoc) throws Exception{
		logger.verbose("--------------FANYCDVerifyAddressWithAVSUE------addInDocName entry----");

		Element eleInDocPersonInfo = (Element) inDoc.getElementsByTagName(CustomerMasterConstants.ATT_PersonInfo).item(0);

		Document outDocNew = outDoc; 
		Element eleCustomer = (Element) outDocNew.getElementsByTagName("PersonInfoList").item(0);
		int numnodes = eleCustomer.getElementsByTagName("PersonInfo").getLength();
		logger.verbose("num of PersonInfo nodes- " + numnodes);

		for (int i=0;i < numnodes; i++) {
			logger.verbose("i - " + i);

			Element PersonInfo = (Element) eleCustomer.getElementsByTagName("PersonInfo").item(i);

		PersonInfo.setAttribute("Company", eleInDocPersonInfo.getAttribute("Company"));
		PersonInfo.setAttribute("DayPhone", eleInDocPersonInfo.getAttribute("DayPhone"));
		PersonInfo.setAttribute("EMailID", eleInDocPersonInfo.getAttribute("EMailID"));
		PersonInfo.setAttribute("EnterpriseCode", eleInDocPersonInfo.getAttribute("EnterpriseCode"));
		PersonInfo.setAttribute("EveningPhone", eleInDocPersonInfo.getAttribute("EveningPhone"));
		PersonInfo.setAttribute("PersonInfoKey", eleInDocPersonInfo.getAttribute("PersonInfoKey"));
		PersonInfo.setAttribute("FirstName", eleInDocPersonInfo.getAttribute("FirstName"));
		PersonInfo.setAttribute("LastName", eleInDocPersonInfo.getAttribute("LastName"));
		}


		logger.verbose("docREST-->" + outDocNew);
 
		logger.verbose("--------------FANYCDVerifyAddressWithAVSUE------addInDocName exit----");

		return outDocNew;
	}


}
