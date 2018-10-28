package com.nwcg.icbs.yantra.api.common;

import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;

import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

import java.util.Properties;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class NWCGSetEnableSchedulingFlag implements YIFCustomApi{

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	public Document setFlag(YFSEnvironment env, Document inDoc) throws Exception{
         //       System.out.println("NWCGSetEnableSchedulingFlag::setFlag:: Begin");
         //       System.out.println("NWCGSetEnableSchedulingFlag::setFlag:: INPUT inDoc=" +XMLUtil.extractStringFromDocument(inDoc));
		 
		 Element eleRoot = inDoc.getDocumentElement();
		 String strMaxOrderStatus = eleRoot.getAttribute("MaxOrderStatus");
		 if(strMaxOrderStatus.equals("1100"))
			 eleRoot.setAttribute("isSchedulingEnabled", "Y");
		 else
			 eleRoot.setAttribute("isSchedulingEnabled", "N");
		 
		 String draftOrderFlag = eleRoot.getAttribute("DraftOrderFlag");
		 Element extnElement = (Element) inDoc.getElementsByTagName("Extn").item(0);	
		 String extnSystemOfOrigin = extnElement.getAttribute("ExtnSystemOfOrigin");
		 String removeLineFlag = (draftOrderFlag.equals("Y") && !extnSystemOfOrigin.equals("ROSS"))? "Y" : "N";
		 eleRoot.setAttribute("RemoveLineFlag", removeLineFlag);	 

         //System.out.println("NWCGSetEnableSchedulingFlag::setFlag:: OUTPUT inDoc=" +XMLUtil.extractStringFromDocument(inDoc));
	 //System.out.println("NWCGSetEnableSchedulingFlag::setFlag:: End");
      	 return inDoc;
	 }	
}