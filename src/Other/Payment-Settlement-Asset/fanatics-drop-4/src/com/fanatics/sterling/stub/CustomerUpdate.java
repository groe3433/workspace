package com.fanatics.sterling.stub;

import java.util.Properties;

import org.w3c.dom.Document;

import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.FANConstants;
import com.fanatics.sterling.util.FANDBUtil;
import com.fanatics.sterling.util.FANDateUtils;
import com.fanatics.sterling.util.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class CustomerUpdate {
	
	private static YFCLogCategory log = YFCLogCategory.instance("com.yantra.yfc.log.YFCLogCategory");

	private Properties props = null;

	public Document invokeCustomerUpdateREST(YFSEnvironment env, Document inXML) throws Exception {

		log.verbose("--------------CustomerUpdate.invokeCustomerUpdateREST----------"
				+XMLUtil.getElementXMLString(inXML.getDocumentElement()));
		return inXML;
	}

}
