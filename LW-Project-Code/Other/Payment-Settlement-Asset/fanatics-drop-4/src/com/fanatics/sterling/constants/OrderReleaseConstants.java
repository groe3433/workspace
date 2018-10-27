
package com.fanatics.sterling.constants;

import com.sterlingcommerce.baseutil.SCXmlUtil;

/**
 * This class contains constants required for the fanatics Etaildirect Implementation.
 * @(#) FANConstants.java    
 * Created on   April 4, 2016
 *              
 *
 * Package Declaration: 
 * File Name:       OrderReleaseConstants.java
 * Package Name:    com.fanatics.sterling.constants
 * Project name:    fanatics
 * Type Declaration:    
 * Class Name:      OrderReleaseConstants
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
 
public interface OrderReleaseConstants {
		
		//Fanatics DB Sequences
		public static final String SEQ_FANATICS_ORDER_NO        = "SEQ_FAN_SHIP_ALONE_RELEASE_NO";
		
		
		//Fanatics SQL statements
		public static final String SEQ_EXISTS_SQL = "SELECT COUNT(*) FROM user_sequences WHERE sequence_name = 'SEQ_FAN_SHIP_ALONE_RELEASE_NO'";
		public static final String SEQ_CREATE_SQL = "CREATE SEQUENCE SEQ_FAN_SHIP_ALONE_RELEASE_NO START WITH 1 INCREMENT BY 1 MINVALUE 1 MAXVALUE 999999999";
		
		//Fanatics XML elements
		public static final String EL_ORDER_LINE 	  = "OrderLine";
		public static final String ELE_CUSTOM_ATTR  = "CustomAttributes";
		
		//Fanatics XPATH Release Order
		public static final String XPATH_ITEM_ID           = "./Item/@ItemID";
		public static final String XPATH_ORDER_NO 		   = "./Order/@OrderNo";
		public static final String XPATH_ORDER_CUSTOM_ATTR = "./Order/CustomAttributes";
		public static final String XPATH_CUSTOM_ATTR 	   = "./CustomAttributes";
		public static final String XPATH_SHIP_ALONE_N      = "/OrderRelease/OrderLine[ItemDetails/Extn/@ExtnShipAlone='N']";
		public static final String XPATH_SHIP_ALONE_Y      = "/OrderRelease/OrderLine[ItemDetails/Extn/@ExtnShipAlone='Y']";
		public static final String XPATH_STATUS_QTY        = "./OrderStatuses/OrderStatus/@StatusQty";
		public static final String XPATH_ELE_STATUS        = "./OrderStatuses/OrderStatus[@OrderReleaseKey='";
		public static final String XPATH_ELE_STATUS_END    = "']";
		public static final String XPATH_ORDER_RELEASE_KEY = "/OrderRelease/@OrderReleaseKey";
		public static final String XPATH_ORDERED_QTY       = "./@OrderedQty";
				
		//Fanatics API names
		public static final String SERVICE_RELEASE_MSG_WMS = "FanaticsPublishReleaseMsgToWMS";
		
		//Fanatics XML Attributes		
		public static final String ATT_RELEASE_CTRL_NBR  = "ReleaseControlNbr";
		public static final String ATT_STATUS_QTY        = "StatusQty";
		public static final String ATT_ORDER_RELEASE_KEY = "OrderReleaseKey";
		
		//Fanatics Miscellaneous Values
		public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
		public static final String NO_VALUE			  = "";
		public static final String ONE                = "1";



		


}