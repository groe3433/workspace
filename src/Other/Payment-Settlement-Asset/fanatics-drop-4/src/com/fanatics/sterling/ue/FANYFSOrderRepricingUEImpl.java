package com.fanatics.sterling.ue;

import org.w3c.dom.Document;

import com.fanatics.sterling.constants.FanaticsOrderRepricingConstants;
import com.fanatics.sterling.util.CommonUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSOrderRepricingUE;

/**
 * 
 * This class provides the Use Exit implementation for the
 * YFSOrderRepricingUE. On certain modifications that are done on the order 
 * there will be a need to re-evaluate the pricing of an order
 * 
 */

public class FANYFSOrderRepricingUEImpl implements YFSOrderRepricingUE
{
	private static YFCLogCategory logger= YFCLogCategory.instance(YFCLogCategory.class);
	
	public Document orderReprice(YFSEnvironment yfsEnv, Document inXML)
			throws YFSUserExitException {
		Document outDoc =null;;
		try {
			// Invoke the rest service
			outDoc = CommonUtil.invokeService(yfsEnv, FanaticsOrderRepricingConstants.SERVICE_fanatics_FanaticsOrderRepricing, inXML);
						
			/*if(outDoc != null){
				CommonUtil.invokeService(yfsEnv, FanaticsFraudCheckConstants.SERVICE_fanatics_FanSendFraudResponse, inXML);
			}
			else{
				logger.error("Output Document from FanaticsFraudCheckREST is null");
			}*/
		} catch (Exception e) {
				e.printStackTrace();
		}
		
		
		return outDoc;
	}

}
