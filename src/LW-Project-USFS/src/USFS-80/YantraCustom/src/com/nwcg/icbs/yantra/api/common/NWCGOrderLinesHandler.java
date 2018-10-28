package com.nwcg.icbs.yantra.api.common;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.nwcg.icbs.yantra.util.common.XMLUtil;

public class NWCGOrderLinesHandler implements YIFCustomApi{

	public void setProperties(Properties arg0) throws Exception 
	{}

	public Document handleFlags(YFSEnvironment env, Document inDoc) throws Exception
	{
		//System.out.println("NWCGOrderLinesHandler:handleFlags:inDoc\n" + XMLUtil.getXMLString(inDoc));	

		Element orderLineListRootElement = inDoc.getDocumentElement();
		Element orderElement = (Element) inDoc.getElementsByTagName("Order").item(0);

		if(orderElement != null)
		{
			String documentType = orderElement.getAttribute("DocumentType");

			String createIssueFlag = (!documentType.equals("0006"))? "Y" : "N"; 
			String createCacheTransferFlag = (documentType.equals("0006"))? "Y" : "N";

			orderLineListRootElement.setAttribute("CreateIssueFlag", createIssueFlag);	 
			orderLineListRootElement.setAttribute("CreateCacheTransferFlag", createCacheTransferFlag);	 
		}

		//System.out.println("NWCGOrderLinesHandler:handleFlags:inDoc after setting flags\n" + XMLUtil.getXMLString(inDoc));	

		return inDoc;
	}

}