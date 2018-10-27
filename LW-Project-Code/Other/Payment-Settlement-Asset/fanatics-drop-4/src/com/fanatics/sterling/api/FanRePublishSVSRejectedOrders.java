package com.fanatics.sterling.api;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;


import com.fanatics.sterling.constants.FanSVSConfirmShipmentConstants;
import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class FanRePublishSVSRejectedOrders {

	private static YFCLogCategory logger = YFCLogCategory.instance(YFCLogCategory.class);
	
public void rePublishSVSRejectedOrders(YFSEnvironment yfsEnv, Document inXML){
	
	logger.verbose("Inside rePublishSVSRejectedOrders");
	logger.verbose("rePublishSVSRejectedOrders Input xml is 1: "+ XMLUtil.getXMLString(inXML));
	
	String strOrderReleaseKey = XMLUtil.getXpathProperty(inXML, "MonitorConsolidation/Order/OrderStatuses/OrderStatus/@OrderReleaseKey");
	
	logger.verbose("OrderReleaseKey is "+ strOrderReleaseKey);
	
	Document docIPGetOrderRelDetails = null;
	Document docOPGetOrderRelDetails = null;
	
	try {
		docIPGetOrderRelDetails = XMLUtil.getDocument("<OrderReleaseDetail OrderReleaseKey='"+strOrderReleaseKey+"' />");
		
		docOPGetOrderRelDetails = CommonUtil.invokeAPI(yfsEnv, FanSVSConfirmShipmentConstants.GET_ORDER_RELEASE_DETAILS_API, docIPGetOrderRelDetails);
		logger.verbose("docOPGetOrderRelDetails xml is : "+ XMLUtil.getXMLString(docOPGetOrderRelDetails));
		
		// Push the release details to the queue. The queue is read by SVS
		CommonUtil.invokeService(yfsEnv, FanSVSConfirmShipmentConstants.SRVC_REPUBLISH_TO_SVS, docOPGetOrderRelDetails);
		
	} catch (ParserConfigurationException | SAXException | IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}
	
}
