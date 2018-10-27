package com.nwcg.icbs.yantra.api.refurb;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGAppendMissingStatus implements YIFCustomApi {

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public Document appendMissingStatus(YFSEnvironment env,Document inXML) throws Exception{
		
		//System.out.println("inXML INPUT************************* "+XMLUtil.getXMLString(inXML));
		Document docOut = CommonUtilities.invokeAPI(env, getCommonCodeTemplate(), "getCommonCodeList", inXML);
		Element eleRoot = docOut.getDocumentElement();
		Element eleCommonCode = docOut.createElement("CommonCode");
		eleCommonCode.setAttribute("CodeShortDescription", NWCGConstants.DESC_MISSING);
		eleCommonCode.setAttribute("CodeValue", NWCGConstants.DISP_MISSING);
		eleRoot.appendChild(eleCommonCode);
		//System.out.println("docOut OUTPUT************************* "+XMLUtil.getXMLString(docOut));
		
		return docOut;
	}
	
	private Document getCommonCodeTemplate() throws Exception
	{
		Document docTemp = XMLUtil.createDocument("CommonCodeList");
		Element eleRoot = docTemp.getDocumentElement();
		Element eleCommonCode = docTemp.createElement("CommonCode");
		eleCommonCode.setAttribute("CodeShortDescription", "");
		eleCommonCode.setAttribute("CodeValue", "");
		eleRoot.appendChild(eleCommonCode);
		
		return docTemp;
		
	}
	
}