package com.nwcg.icbs.yantra.api.issue;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGCheckAndSetBaseReqNo implements YIFCustomApi {
	private static Logger logger = Logger.getLogger(NWCGCheckAndSetBaseReqNo.class.getName());

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}
	
	public Document checkAndSetBaseReqNo(YFSEnvironment env, Document docIP) throws Exception {
		logger.info("NWCGCheckAndSetBaseReqNo::checkAndSetBaseReqNo, Entered");
		logger.verbose("NWCGCheckAndSetBaseReqNo::checkAndSetBaseReqNo, Input XML : " + 
						XMLUtil.extractStringFromDocument(docIP));
		NodeList nlOL = docIP.getDocumentElement().getElementsByTagName("OrderLine");
		
		// If the issue doesn't have any lines, then return the original order xml. Some
		// attributes might have been changed at the Order level
		if (nlOL == null || nlOL.getLength() < 1){
			return docIP;
		}
		
		for (int i=0; i < nlOL.getLength(); i++){
			Element elmOL = (Element) nlOL.item(i);
			String orderLineKey = elmOL.getAttribute(NWCGConstants.ORDER_LINE_KEY);
			
			if (orderLineKey == null || orderLineKey.length() < 2){
				// Order Line is not created yet. Set the base request no
				Element elmExtn = (Element) elmOL.getElementsByTagName("Extn").item(0);
				elmExtn.setAttribute(NWCGConstants.EXTN_BASE_REQUEST_NO, 
									 elmExtn.getAttribute(NWCGConstants.EXTN_REQUEST_NO));
			}
		}
		logger.verbose("NWCGCheckAndSetBaseReqNo::checkAndSetBaseReqNo, Input XML after setting " +
				"base request no : " + XMLUtil.extractStringFromDocument(docIP));
		logger.info("NWCGCheckAndSetBaseReqNo::checkAndSetBaseReqNo, Returning");
		return docIP;
	}

}
