package com.nwcg.icbs.yantra.api.ldap;

import java.io.IOException;
import java.util.Properties;

import org.w3c.dom.Document;

import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class SampleAPI implements YIFCustomApi {

	private static YFCLogCategory cat = YFCLogCategory.instance(SampleAPI.class);
	private Properties myProperties = null;
	
	public void setProperties(Properties props) throws Exception {
		// TODO Auto-generated method stub
		this.myProperties = props;
	}
	
	public Document processAPI(YFSEnvironment env, Document inDoc) throws Exception {
		System.out.println("YFCLogUtil.isDebugEnabled = " + YFCLogUtil.isDebugEnabled());
		System.out.println("YFCLogUtil.isVerboseEnabled = " + YFCLogUtil.isVerboseEnabled());
		System.out.println("YFCLogLevel.DEBUG = " + YFCLogLevel.DEBUG + "; YFCLogLevel.VERBOSE=" + YFCLogLevel.VERBOSE);
		cat.beginTimer("processAPI");
		cat.debug("Inside SampleAPI.processAPI - Input XML:" + YFCDocument.getDocumentFor(inDoc).serializeToString());
		cat.info("Inside SampleAPI.processAPI - Input XML:" + YFCDocument.getDocumentFor(inDoc).serializeToString());
		cat.verbose("Inside SampleAPI.processAPI - Input XML:" + YFCDocument.getDocumentFor(inDoc).serializeToString());
		cat.error("Inside SampleAPI.processAPI - Input XML:" + YFCDocument.getDocumentFor(inDoc).serializeToString());
		
		// set logAll to true
		if(YFCLogUtil.isVerboseEnabled()) {
			System.out.println("Setting YFCLogCategory.setLogAll() to true");
			cat.setLogAll(true);
			cat.debug("Inside setLogAll SampleAPI.processAPI - Input XML:" + YFCDocument.getDocumentFor(inDoc).serializeToString());
			cat.info("Inside setLogAll SampleAPI.processAPI - Input XML:" + YFCDocument.getDocumentFor(inDoc).serializeToString());
			cat.verbose("Inside setLogAll SampleAPI.processAPI - Input XML:" + YFCDocument.getDocumentFor(inDoc).serializeToString());
			cat.error("Inside setLogAll SampleAPI.processAPI - Input XML:" + YFCDocument.getDocumentFor(inDoc).serializeToString());
			System.out.println("After Printing all statements");
		}
		
		//
		cat.endTimer("processAPI");
		return inDoc;
	}

}
