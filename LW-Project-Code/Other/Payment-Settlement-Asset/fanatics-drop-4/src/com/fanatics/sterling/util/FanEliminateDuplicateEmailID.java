package com.fanatics.sterling.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.yfs.japi.YFSEnvironment;

public class FanEliminateDuplicateEmailID {

	public Document eliminateDuplicateEmailID(YFSEnvironment yfsEnv, Document inDoc) throws ParserConfigurationException{
		XMLUtil.getXMLString(inDoc);
		List<String> emailid = new ArrayList<String>();
		List<String> orderNo = new ArrayList<String>();
		
		NodeList nl = inDoc.getElementsByTagName("EXTNFanEmail");
		for(int i=0; i<nl.getLength(); i++){
			Element el = (Element) nl.item(i);
			String email = el.getAttribute("EmailID");
			if(!emailid.contains(email)){
				emailid.add(email);
				orderNo.add(el.getAttribute("OrderNo"));
			}
		}
		
		/*Document outDoc = XMLUtil.createDocument("EXTNFanEmailList");
		for(int i=0; i<emailid.size(); i++){
			Element ele = XMLUtil.createChild(outDoc.getDocumentElement(), "EXTNFanEmail");
			ele.setAttribute("EmailID", emailid.get(i));
		}*/
		Document outDoc = XMLUtil.createDocument("EXTNFanEmail");
		if(emailid.size()>0){
			outDoc.getDocumentElement().setAttribute("EmailID", emailid.get(0));
			outDoc.getDocumentElement().setAttribute("OrderNo", orderNo.get(0));
		}
		
		return outDoc;
	}
}
