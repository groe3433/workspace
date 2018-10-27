
package com.fanatics.sterling.constants;

/**
 * This class contains constants required for the fanatics Etaildirect Implementation.
 * @(#) FANConstants.java    
 * Created on   April 4, 2016
 *              
 *
 * Package Declaration: 
 * File Name:       CreateOrderConstants.java
 * Package Name:    com.fanatics.sterling.constants
 * Project name:    fanatics
 * Type Declaration:    
 * Class Name:      CreateOrderConstants
 * 
 * @author KNtagkas
 * @version 1.0
 * @history April 4, 2016
 *     
 * 
 *  
 *
 * (C) Copyright 2006-2007 by owner.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information
 * of the owner. ("Confidential Information").
 * Redistribution of the source code or binary form is not permitted
 * without prior authorization from the owner.
 *
 */
 
public interface CreateOrderConstants {
		
		//Fanatics DB Sequences
		public static final String SEQ_FANATICS_ORDER_NO        = "SEQ_FANATICS_ORDER_NO";
		public static final int    MAX_DIGITS_FANATICS_ORDER_NO = 999999999;
		
		//Fanatics SQL statements
		public static final String SEQ_EXISTS_SQL = "SELECT COUNT(*) FROM user_sequences WHERE sequence_name = 'SEQ_FANATICS_ORDER_NO'";
		public static final String SEQ_CREATE_SQL = "CREATE SEQUENCE SEQ_FANATICS_ORDER_NO START WITH 1 INCREMENT BY 1 MINVALUE 1 MAXVALUE 999999999";
		
		//Fanatics XML elements
		public static final String ELE_MIN_DELIVERY   = "MIN_DELIVERY";
		public static final String ELE_MAX_DELIVERY   = "MAX_DELIVERY";
		public static final String EL_CARRIER_SERVICE = "CarrierService";
		public static final String EL_ORDER_LINE 	  = "OrderLine";
		
		//Fanatics XPATH Create Order
		public static final String XPATH_ORDER_DATES          = "./OrderDates";
		public static final String XPATH_PROMISHED_SHIP_DATE  = "./OrderDates/OrderDate[@DateTypeId=\"PROMISED_SHIP_DATE\"]";
		public static final String XPATH_REVISED_SHIP_DATE  = "./OrderDates/OrderDate[@DateTypeId=\"REVISED_SHIP_DATE\"]";
		public static final String XPATH_CARRIER_SERVICE_CODE = "//Order/@CarrierServiceCode";
		public static final String XPATH_MAX_TRANSIT_DAYS     = "//CarrierServiceList/CarrierService/@MaximumTransitDays";
		public static final String XPATH_FIXED_TRANSIT_DAYS   = "//CarrierServiceList/CarrierService/@FixedTransitDays";
		public static final String XPATH_EXTN_CANCEL  		  = "//OrganizationList/Organization[1]/Extn/@ExtnCancelOnBackorder";
		public static final String XPATH_SELLER_ORG_CODE 	  = "//Order/@SellerOrganizationCode";
		
		//Fanatics API names
		public static final String API_LIST_CARRIER_SERVICE = "listCarrierService";
		public static final String API_GET_ORG_LIST   		= "getOrganizationList";
		
		//Fanatics XML Attributes
		public static final String ATT_CARRIER_SERVICE_CODE = "CarrierServiceCode";
		public static final String ATT_COMMITTED_DATE 		= "CommittedDate";
		public static final String ATT_DATE_TYPE_ID 		= "DateTypeId";
		public static final String ATT_DOCUMENT_TYPE 		= "DocumentType";
		public static final String ATT_ORGANIZATION         = "Organization";
		public static final String ATT_ORGANIZATION_CODE    = "OrganizationCode";

					
		//Fanatics Miscellaneous Values
		public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
		public static final String NO_VALUE			  = "";
	
}
