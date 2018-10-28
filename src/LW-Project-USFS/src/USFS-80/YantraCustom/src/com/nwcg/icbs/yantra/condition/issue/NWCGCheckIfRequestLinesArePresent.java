package com.nwcg.icbs.yantra.condition.issue;

import java.util.Map;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class will be called as part of a condition before sending CreateRequest message to ROSS
 * @author sgunda
 *
 */
public class NWCGCheckIfRequestLinesArePresent implements YCPDynamicConditionEx {

	private static Logger logger = Logger.getLogger();
	private Map myProperties = null;
	
	public boolean evaluateCondition(YFSEnvironment arg0, String arg1,
			Map arg2, Document doc) {
		logger.verbose("NWCGCheckIfRequestLinesArePresent::evaluateCondition, Entered");
		try {
			logger.verbose("NWCGCheckIfRequestLinesArePresent::evaluateCondition, Input XML : " + XMLUtil.extractStringFromDocument(doc));
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
		NodeList nlReq = doc.getDocumentElement().getElementsByTagName("ro:Request");
		if (nlReq != null && nlReq.getLength() > 0){
			return true;
		}
		else {
			return false;
		}
	}

	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub
		myProperties = arg0;
	}

}
