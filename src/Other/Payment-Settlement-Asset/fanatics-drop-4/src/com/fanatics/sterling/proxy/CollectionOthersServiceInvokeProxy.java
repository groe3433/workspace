package com.fanatics.sterling.proxy;


import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.XMLUtil;
import com.fanatics.sterling.util.XPathUtil;
import com.yantra.pca.bridge.YCDFoundationBridge;
import com.yantra.pca.ycd.ue.utils.YCDPaymentUEXMLManager;
import com.yantra.shared.dbclasses.YFS_Charge_TransactionDBHome;
import com.yantra.shared.dbi.YFS_Charge_Transaction;
import com.yantra.shared.dbi.YFS_Order_Header;
//import com.yantra.shared.dbi.YFS_Payment;
import com.yantra.shared.dbi.YFS_Payment_Type;
import com.yantra.shared.ycd.YCDErrorCodes;
import com.yantra.shared.ycd.YCDServiceNames;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.util.YFCUtils;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.date.YTimestamp;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCDataBuf;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionInputStruct;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionOutputStruct;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSCollectionCreditCardUE;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Properties;

import org.omg.CORBA.StringHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.Document;

import com.fanatics.sterling.constants.VendorFeedConstants;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class CollectionOthersServiceInvokeProxy implements YIFCustomApi {

	private Properties props = null;
	private Document docOutXML = null;
	 
	private static YFCLogCategory logger = YFCLogCategory.instance("com.yantra.yfc.log.YFCLogCategory");
	
	public Document invoke(YFSEnvironment env, Document docInXML)
			throws Exception {
		
		final String serviceName = getPropertyVal("ServiceName");
		
		logger.verbose("----------------ServiceName--------------"+serviceName);
		
		logger.verbose("--------------CollectionOthersServiceInvokeProxy------10---"
				+XMLUtil.getXMLString(docInXML));
		
		Document responseDoc = CommonUtil.invokeService(env, serviceName, docInXML);
		
		logger.verbose("--------------CollectionOthersServiceInvokeProxy------11---"
			+XMLUtil.getXMLString(responseDoc));
		
		
		return responseDoc;
		
		
		
	}
	
	protected String getPropertyVal(String name) {

		return props.getProperty(name);
	}

	public void setProperties(Properties arg0) throws Exception {

		props = arg0;

	}

}
