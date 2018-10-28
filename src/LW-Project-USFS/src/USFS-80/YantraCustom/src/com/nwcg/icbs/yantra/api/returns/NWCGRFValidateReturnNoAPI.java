package com.nwcg.icbs.yantra.api.returns;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGRFValidateReturnNoAPI implements YIFCustomApi {

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 * @throws Exception
	 * 
	 * convert <NextReceiptNo CacheID="CORMK" IgnoreOrdering="Y"
    	ReceiptNo="CORMK000503" SequenceType="RETURN"/>
    	
    	to 
    	
    	<NWCGSequence OrganizationID="CORMK" SequenceType="RETURN" SequenceNo="CORMK000483"/>
    	
    	and invoke service "NWCGValidateReturnNoService"

	 */
	
	public Document rfValidateReturnNoAPI(YFSEnvironment env, Document inXML)
	throws Exception {
		
		//System.out.println("rfValidateReturnNoAPI :: inXML ::" + XMLUtil.getXMLString(inXML));
		
		Document validateRNOInXML = XMLUtil.getDocument();
		
		Element elemNWCGSequence = validateRNOInXML.createElement("NWCGSequence");
		
		validateRNOInXML.appendChild(elemNWCGSequence);
		
		elemNWCGSequence.setAttribute("OrganizationID", inXML.getDocumentElement().getAttribute("CacheID"));
		elemNWCGSequence.setAttribute("SequenceNo", inXML.getDocumentElement().getAttribute("ReceiptNo"));
		elemNWCGSequence.setAttribute("SequenceType", inXML.getDocumentElement().getAttribute("SequenceType"));
		
		//System.out.println("rfValidateReturnNoAPI :: validateRNOInXML ::" + XMLUtil.getXMLString(validateRNOInXML));
		
		Document validateRNOOutXML = CommonUtilities.invokeService(env, "NWCGValidateReturnNoService", validateRNOInXML);
		
		//System.out.println("validateRNOOutXML :: " + XMLUtil.getXMLString(validateRNOOutXML));
		
		
		
		
		return validateRNOOutXML;
		
	}

}
