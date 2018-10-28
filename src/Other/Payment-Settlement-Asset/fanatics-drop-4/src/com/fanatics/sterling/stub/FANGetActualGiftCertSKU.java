package com.fanatics.sterling.stub;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fanatics.sterling.constants.FanSVSConfirmShipmentConstants;
import com.fanatics.sterling.util.FANConstants;
import com.fanatics.sterling.util.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class FANGetActualGiftCertSKU {

	private static YFCLogCategory logger = YFCLogCategory.instance(YFCLogCategory.class);
	
	public Document getActualGiftCertSKU(YFSEnvironment env, Document inDoc){
		
		logger.verbose("Inside getActualGiftCertSKU");
		logger.verbose("getActualGiftCertSKU 1 "+ XMLUtil.getXMLString(inDoc));
		
		// get the Seller Organization
		String strSellerOrgCode = inDoc.getDocumentElement().getAttribute(FANConstants.ATT_SELLER_ORG_CODE);
		
		Element eleItem = (Element) inDoc.getElementsByTagName(FANConstants.CONSTANT_ITEM).item(0);
		
		if(strSellerOrgCode.equals(FANConstants.CONSTANT_FANATICS_WEB)){
			logger.verbose("Its FANATICS_WEB ");
			eleItem.setAttribute(FANConstants.ITEM_ID, FanSVSConfirmShipmentConstants.CONST_GIFT_CERT_SKU_0001);
		}
		
		else if(strSellerOrgCode.equals(FANConstants.CONSTANT_0951)){
			logger.verbose("Its 0951 ");
			eleItem.setAttribute(FANConstants.ITEM_ID, FanSVSConfirmShipmentConstants.CONST_GIFT_CERT_SKU_0001);					
		}
		
		else if(strSellerOrgCode.equals(FANConstants.CONSTANT_VENDORS)){
			logger.verbose("Its VENDORS ");
			eleItem.setAttribute(FANConstants.ITEM_ID, FanSVSConfirmShipmentConstants.CONST_GIFT_CERT_SKU_0001);	
		}
		
		logger.verbose("OP of FANGetActualGiftCertSKU is "+ XMLUtil.getXMLString(inDoc));
		return inDoc;		
		
	}
	
}
