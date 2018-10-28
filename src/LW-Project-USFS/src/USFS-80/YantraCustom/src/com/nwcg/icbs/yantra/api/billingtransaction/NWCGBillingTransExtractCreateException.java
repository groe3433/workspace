package com.nwcg.icbs.yantra.api.billingtransaction;

/**
 * -----------------------------------------------------------------------------
 * QC : 1051
 * Description : This class is created to save the Store Billing Extract 
 * criteria/document in the table YFS_INBOX, so the time triggered agent could 
 * pick up the record in the decided interval and process the record to create 
 * the file. 
 * -----------------------------------------------------------------------------
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGBillingTransExtractCreateException {
	public void postExtractXML(YFSEnvironment env, Document doc)
	throws Exception {
try {

	System.out.println("<<>> In postExtractXML " + XMLUtil.getXMLString(doc));
	String sDoc = XMLUtil.getXMLString(doc);
	String sInboxType = "EXTRACT";
	Element elemRootDoc = doc.getDocumentElement();
	String sCacheId = elemRootDoc.getAttribute("CacheID");
	
	Document inDoc = XMLUtil.getDocument();
	Element elDoc = inDoc.createElement("Inbox");
	elDoc.setAttribute(NWCGConstants.INBOX_TYPE, sInboxType);
	elDoc.setAttribute(NWCGConstants.INBOX_ADDNL_DATA,sDoc);
	elDoc.setAttribute(NWCGConstants.SHIPNODE_KEY,sCacheId);
	inDoc.appendChild(elDoc);
	
	System.out.println(" <<>> Printing postExtractXML.inDoc : " + XMLUtil.getXMLString(inDoc));
	Document dPostExtractCriteria = CommonUtilities.invokeAPI(env,"NWCGBillingTransExtract_createException", NWCGConstants.NWCG_CREATE_EXCEPTION, inDoc);
	System.out.println(" <<>> Printing postExtractXML.dPostExtractCriteria : " + XMLUtil.getXMLString(dPostExtractCriteria));
	
} catch (Exception e) {
	e.printStackTrace();
}
}
}
