package com.fanatics.sterling.constants;

import java.util.ArrayList;

public interface OrderNotesConstants {

	//Fanatics  Order Notes SQL statements
	public static final String  SQL_GET_DBTIME = "select to_char( cast(sysdate as timestamp with local time zone), 'Month dd, yyyy hh:mi:ss PM TZD') as res from dual";
	
	//Fanatics  Order Notes XPATHs
	public static final String XPATH_INVOICE_COLLECTION_1 = "/Order/InvoiceCollections/InvoiceCollection/OrderInvoice[@OrderHeaderKey=\"";
	public static final String XPATH_INVOICE_COLLECTION_2 = "\"]/parent::InvoiceCollection";
	//public static final String XPATH_STATUS_RELEASED	  = "//Order/OrderLines/OrderLine/StatusBreakupForCanceledQty/CanceledFrom[@Status='1300']";
	public static final String XPATH_STATUS_RELEASED	  = "/Order/OrderLines/OrderLine";
	public static final String XPATH_STATUS_RELEASED_P	  = "//Order/OrderLines/OrderLine[StatusBreakupForCanceledQty/CanceledFrom[@Status='3200']]";
	public static final String XPATH_FRAUD_STATUS_USER 	  = "/Order/CustomAttributes/@FraudStatusUserID";
	public static final String XPATH_ORDER_CANCEL		  = "//Order/OrderLines/OrderLine/@Action";
	public static final String XPATH_LINE_PRICE_INFO	  = "/Order/OrderLines/OrderLine/LinePriceInfo";
	public static final String XPATH_ORDER_PRICE_INFO	  = "//Order/PriceInfo/@ChangeInTotalAmount";
	public static final String XPATH_ITEM_ID 			  = "Item/@ItemID";
	public static final String XPATH_ITEM_SHORT_DESC 	  = "Item/@ItemShortDesc";
	public static final String XPATH_NOTES 				  = "/Order/Notes";
	public static final String XPATH_INFO_BILL_TO 		  = "//Order/PersonInfoBillTo";
	public static final String XPATH_INFO_SHIP_TO 		  = "//Order/PersonInfoShipTo";
	public static final String XPATH_ORDER_LIST_INFO_BILL_TO = "//OrderList/Order/PersonInfoBillTo";
	public static final String XPATH_ORDER_LIST_INFO_SHIP_TO = "//OrderList/Order/PersonInfoShipTo";
	
	//Fanatics  Order Notes XML Attributes
	public static final String CURRENT_DB_DATE_TIME  = "CurrentDBDateTime";
	public static final String USER_ID 				 = "UserId";
	public static final String ATT_ORDER_HEADER_KEY  = "OrderHeaderKey";
	public static final String ATT_NOTES 			 = "Notes";
	public static final String ATT_NOTE 	 		 = "Note";
	public static final String ATT_NOTE_TEXT 		 = "NoteText";
	public static final String ATT_AMOUNT_COLLECTED  = "AmountCollected";
	public static final String ATT_CONFIRM_SHIPMENT  = "Confirm Shipment Success";
	public static final String ATT_STATUS 			 = "Status";
	public static final String ATT_CHANGE_QUANTITY   = "ChangeInOrderedQty";
	public static final String ATT_CONTACT_USER		 = "ContactUser";
	public static final String ATT_TYPE 			 = "Type";
	public static final String ATT_NAME   			 = "Name";
	public static final String ATT_ORDER_HOLD_TYPE 	 = "OrderHoldType";
	public static final String ATT_ADDRESS_1	 	 = "AddressLine1";
	public static final String ATT_ADDRESS_2	 	 = "AddressLine2";
	public static final String ATT_CITY			 	 = "City";
	public static final String ATT_STATE		 	 = "State";
	public static final String ATT_ZIP_CODE		 	 = "ZipCode";
	public static final String CHANGE_LINE_TOTAL 	 = "ChangeInLineTotal";
	
	//Fanatics Order Notes  XML elements
	public static final String EL_ORDER_ROOT 	  	  = "Order";
	public static final String EL_INVOICE_COLLECTION  = "InvoiceCollections";
	public static final String EL_EMAIL_TYPE	      = "EmailType";
	public static final String EL_EXTN_FAN_EMAIL	  = "EXTNFanEmail";
	public static final String EL_CANCELED_FROM		  = "CanceledFrom";
	public static final String EL_LINE_PRICE_INFO 	  = "LinePriceInfo";
	public static final String EL_SERVERS			  = "Servers";
	public static final String EL_SERVER			  = "Server";
	public static final String EL_PERSON_INFO_SHIP_TO = "PersonInfoShipTo";
	public static final String EL_PERSON_INFO_BILL_TO = "PersonInfoBillTo";
	
	//MISC
	public static final String RELEASED 	= "3200";
	public static final String CANCEL 		= "CANCEL";
	public static final String SYSTEM		= "SYSTEM";
	public static final String INTEG_SERVER = "IntegrationAgentServer";
	public static final String ACTIVE 		= "Active";
	public static final String HOLD_NUM		= "1100";
	public static final Object PENDING_EVAL = "PENDINGEVALUATION";
	public static final String CREATE_NOTE	= "CreateNote";
	public static final String YES 			= "Y";
	public static final String NO 			= "N";


	//Fanatics Order Notes Services and API's
	public static final String SERVICE_FAN_CHANGE_NOTES = "FanaticsCreateOrderChangeNotes";
	public static final String API_GET_SERVER_LIST		= "getServerList";
	public static final String GET_ORDER_LIST 			= "getOrderList";
	
	//Fanatics Order Notes
	public static final String NOTE_PAYMENT_COLLECTION 		= "refunded";
	public static final String NOTE_SHIP_CONFIRMATION_MAIL 	= "resent shipping confirmation email";
	public static final String NOTE_CANCELED_WH 			= "cancelled PID";
	public static final String NOTE_CANCELED_ORDER 			= "cancelled order";
	public static final String NOTE_PENDING_EVAL 			= "Order is in Pending Evaluation hold";

	public static final String HOLD_TYPE = "HoldType";
}
