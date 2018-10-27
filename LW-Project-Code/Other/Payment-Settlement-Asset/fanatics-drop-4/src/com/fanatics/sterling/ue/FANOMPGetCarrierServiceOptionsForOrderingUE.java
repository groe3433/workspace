package com.fanatics.sterling.ue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;

import com.fanatics.sterling.util.XMLUtil;
import com.fanatics.sterling.util.XPathUtil;
import com.fanatics.sterling.util.CommonUtil;

import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.OMPGetCarrierServiceOptionsForOrderingUE;

public class FANOMPGetCarrierServiceOptionsForOrderingUE implements OMPGetCarrierServiceOptionsForOrderingUE {
	private static YFCLogCategory logger = YFCLogCategory.instance("com.yantra.yfc.log.YFCLogCategory");

	public Document getCarrierServiceOptionsForOrdering(YFSEnvironment env, Document inXML) throws YFSUserExitException{
		
		logger.verbose("FANOMPGetCarrierServiceOptionsForOrderingUE.getCarrierServiceOptionsForOrdering -> Begin  ");
		logger.verbose("inDoc:  " + XMLUtil.getXMLString(inXML));
		Document opDoc =null;
		try {
		opDoc = CommonUtil.invokeService(env, "FanaticsShippingChargeREST", inXML);
		}catch(Exception e){

			logger.error("getCarrierServiceOptionsForOrdering --> ERROR: " + e.getMessage(), e.getCause());

		}

		logger.verbose("output is :  " + XMLUtil.getXMLString(opDoc));

		return opDoc;
	}
}

