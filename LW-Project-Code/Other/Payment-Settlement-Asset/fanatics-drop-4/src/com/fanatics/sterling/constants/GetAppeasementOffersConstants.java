
package com.fanatics.sterling.constants;

public interface GetAppeasementOffersConstants {

	// XPATH's
	String XPATH_H_CHARGE_CATEGORY	= "//AppeasementOffers/Order/HeaderCharges/HeaderCharge/@ChargeCategory";
	String XPATH_H_CHARGE_NAME 		= "//AppeasementOffers/Order/HeaderCharges/HeaderCharge/@ChargeName";
	String XPATH_ORDER_HEADER_KEY 	= "/AppeasementOffers/Order/@OrderHeaderKey";
	String XPATH_REASON_CODE 		= "/AppeasementOffers/Order/AppeasementReason/@ReasonCode";

	// XML ELEMENTS
	String EL_COMMON_CODE		 = "CommonCode";
	String EL_APPEASEMENT_OFFER  = "AppeasementOffer";
	String EL_ORDER 			 = "Order";
	String EL_APPEASEMENT_OFFERS = "AppeasementOffers";

	// XML ATTTRIBUTES
	String ATT_CODE_VALUE 		 = "CodeValue";
	String ATT_CODE_TYPE 		 = "CodeType";
	String ATT_CHARGE_CATEGORY   = "ChargeCategory";
	String ATT_CHARGE_NAME 		 = "ChargeName";
	String ATT_ORDER_HEADER_KEY  = "OrderHeaderKey";
	String ATT_REASON_CODE 		 = "ReasonCode";
	String ATT_OFFER_TYPE 		 = "OfferType";
	String ATT_PREFERRED 		 = "Preferred";
	String VARIABLE_AMOUNT_ORDER = "VARIABLE_AMOUNT_ORDER";

	// API's
	String API_COMMON_CODE_LIST  = "getCommonCodeList";

	// MISC
	String APPROVED_APPEASEMENTS = "ApprovedAppeasements";
	String N = "N";

}