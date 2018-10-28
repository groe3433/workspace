package com.fanatics.sterling.constants;
/**
 * This class contains constants required for the fanatics change address Implementation and address verification
 * @(#) FANConstants.java    
 * Created on   July 05, 2016
 *              
 *
 * Package Declaration: 
 * File Name:       CustomerMasterConstants.java
 * Package Name:    com.fanatics.sterling.constants
 * Project name:    fanatics
 * Type Declaration:    
 * Class Name:      CreateOrderConstants
 * 
 * @author JMcCloskey-Lee
 * @version 1.0
 * @history July 05, 2016
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
public interface CustomerMasterConstants {
	
	
	public static final String SERVICE_FanaticsCustomerUpdateREST = "FanaticsCustomerUpdateREST";
	public static final String SERVICE_FanaticsAddressValidationREST  = "FanaticsAddressValidationREST";

	
	public static final String ELE_PersonInfoShipTo = "PersonInfoShipTo";
	public static final String ELE_PersonInfoBillTo = "PersonInfoBillTo";
	public static final String ELE_Order = "Order";
	public static final String ELE_Customer = "Customer";
	public static final String ELE_CustomerContactList = "CustomerContactList";
	public static final String ELE_CustomerContact = "CustomerContact";
	public static final String ELE_CustomerAdditionalAddress = "CustomerAdditionalAddress";
	public static final String ELE_PersonInfo = "PersonInfo";


	//Constants for comparing address details
	public static final String COMPARE_Array_Attribute = "AddressID,AddressLine1,AddressLine2,City,Company,Country,DayPhone,EMailID,EveningPhone,FirstName,LastName,State,ZipCode";
	public static final String COMPARE_Attribute_1 = "AddressID";
	public static final String COMPARE_Attribute_2 = "AddressLine1";
	public static final String COMPARE_Attribute_3 = "AddressLine2";
	public static final String COMPARE_Attribute_4 = "City";
	public static final String COMPARE_Attribute_5 = "Company";
	public static final String COMPARE_Attribute_6 = "Country";
	public static final String COMPARE_Attribute_7 = "DayPhone";
	public static final String COMPARE_Attribute_8 = "EMailID";
	public static final String COMPARE_Attribute_9 = "EveningPhone";
	public static final String COMPARE_Attribute_10 = "FirstName";
	public static final String COMPARE_Attribute_11 = "LastName";
	public static final String COMPARE_Attribute_12 = "State";
	public static final String COMPARE_Attribute_13 = "ZipCode";
	
	
	//PersonInfo
	public static final String ATT_PersonInfo = "PersonInfo";

	
}
