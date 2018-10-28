package com.nwcg.icbs.yantra.api.workorder;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.reports.loftware.NWCGReportsUtil;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGModifyXMLBeforePrinting implements YIFCustomApi{

	public Document modifyXMLBeforePrinting(YFSEnvironment env, Document inDoc)
	throws Exception 
	{		
		//System.out.println("NWCGModifyXMLBeforePrinting: modifyXMLBeforePrinting: inDoc " + XMLUtil.getXMLString(inDoc));
		
		Document outDoc = modifiedPrintDocument(env, inDoc);
		return outDoc;
	}

	private Document modifiedPrintDocument(YFSEnvironment env, Document inDoc)
	throws Exception
	{	
		Document outDoc = CommonUtilities.invokeAPI(env, NWCGConstants.API_PRINT_DOCUMENT_SET, inDoc);		
		
		//System.out.println("NWCGModifyXMLBeforePrinting: modifiedPrintDocument: outDoc " + XMLUtil.getXMLString(outDoc));	
		return outDoc;
	}

	public void setProperties(Properties arg0) throws Exception 
	{}
}