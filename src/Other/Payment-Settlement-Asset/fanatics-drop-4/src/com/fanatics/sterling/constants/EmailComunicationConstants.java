package com.fanatics.sterling.constants;

public interface EmailComunicationConstants {
		
		//Email Comunications XML elements
		public static final String EL_ORDER_ROOT 	  	 = "Order";
		public static final String EL_SHIPMENT_ROOT   	 = "Shipment";
		public static final String EL_RET_RECEIPT_ROOT   = "Receipt";
		public static final String EL_STATUS_BREAK_UP    = "StatusBreakupForCanceledQty";
		public static final String EL_INVOICE_COLLECTION = "InvoiceCollections";
		public static final String EL_ORDER_AUDIT		 = "OrderAudit";
		public static final String EL_ORDER_NO			 = "OrderNo";
		public static final String EL_ORDER_HEADER_KEY	 = "OrderHeaderKey";
		public static final String EL_EMAIL_TYPE	     = "EmailType";
		public static final String EL_EMAIL_ID	     	 = "EmailID";
		public static final String EL_EXTN_FAN_EMAIL	 = "EXTNFanEmail";
		public static final String EL_ORDER_DATES		 = "OrderDates";
		public static final String EL_ORDER_DATE		 = "OrderDate";
		public static final String EL_BILL_TO_ID		 = "BillToID";
		public static final String EL_ENTITY_KEY 		 = "EntityKey";
		public static final String EL_ENTITY_NAME 		 = "EntityName";

		//Email Comunications XML Attributes
		public static final String ATT_ENTERPRISE_CODE	 = "EnterpriseCode";
		public static final String ATT_MSG_XML			 = "MsgXml";
		public static final String ATT_EMAIL_STATUS		 = "EmailStatus";
		public static final String ATT_ORDER_HEADER_KEY  = "OrderHeaderKey";
		public static final String ATT_EMAIL_KEY 		 = "EmailKey";
		public static final String ATT_MAXIMUN_RECORDS	 = "MaximumRecords";
		public static final String ATT_ORGANIZATION_CODE = "OrganizationCode";
		public static final String ATT_DATE_TYPE_ID 	 = "DateTypeId";
		public static final String ATT_SHIPMENT_KEY 	 = "ShipmentKey";
		
		public static final String EL_EMAIL = "Email";
		public static final String ATT_EVENT_ID = "EventId";
		public static final String ATT_EVENT_DESC = "EventDescription";
		public static final String ATT_EMAIL_ID = "EmailID";
		public static final String EVNT_ORDER_CANCEL = "OrderCancel";
		public static final String DESC_ORDER_CANCELLATION = "OrderCancellation";
		
		public static final String EVNT_RETURN_ORDER = "ReturnRefund";
		public static final String DESC_RETURN_ORDER_REFUND = "ReturnOrderRefund";

		public static final String EVNT_REVISE_SHIP_DATE = "ReviseShipDate";
		public static final String DESC_REVISED_SHIP_BY_DATE = "RevisedShipByDate";
		
		public static final String EVNT_RETURN_RECEIPT = "ReturnReceipt";
		public static final String DESC_RETURN_RECEIPT = "ReturnReceipt";
		
		public static final String EVNT_SHIP_CONF = "ShipConf";
		public static final String DESC_SHIP_CONFIRMATION = "ShipConfirmation";
		

		//Email XML input
		public static final String XML_EMAIL_LIST_INPUT  = "<EXTNFanEmail EmailStatus=\"Initial\" MaximumRecords=\"5000\"/>";
		public static final String XML_ORDER_AUDIT_LIST_TEMPLATE = "<OrderAuditList>" +
									   "<OrderAudit ReasonCode=\"\" ReasonText=\"\" OrderAuditKey=\"\" OrderHeaderKey=\"\" OrderLineKey=\"\" OrderReleaseKey=\"\">" +
									   "<Order  DocumentType=\"\" EnterpriseCode=\"\" OrderNo=\"\"/>" +
									   "<OrderAuditLevels>" +
									   "<OrderAuditLevel OrderReleaseKey=\"\" OrderLineKey=\"\">" +
									   "<OrderLine PrimeLineNo=\"\" SubLineNo=\"\"/>" +
									   "<OrderRelease ReleaseNo=\"\"/>" +
									   "</OrderAuditLevel>" +
									   "</OrderAuditLevels>" +
									   "</OrderAudit>" +
									   "</OrderAuditList>";

		//Email Comunications XPATHs
		public static final String XPATH_RECEIPT_ORDER_HEADER 		= "//Receipt/ReceiptLines/ReceiptLine/@OrderHeaderKey";
		public static final String XPATH_RECEIPT_ORDER_NO	  		= "//Receipt/Shipment/@OrderNo";
		public static final String XPATH_SHIPMENT_ORDER_HEADER		= "//Shipment/ShipmentLines/ShipmentLine/@OrderHeaderKey";
		public static final String XPATH_SHIPMENT_ORDER_NO			= "//Shipment/ShipmentLines/ShipmentLine/@OrderNo";
		public static final String XPATH_FAN_EMAIL 					= "/EXTNFanEmailList/EXTNFanEmail";
		public static final String XPATH_FAN_FULL_BILL_EMAIL    	= "OrderList/Order/PersonInfoBillTo/@EMailID";
		public static final String XPATH_FAN_FULL_SHIP_EMAIL   		= "OrderList/Order/PersonInfoShipTo/@EMailID";
		public static final String XPATH_FAN_FULL_CUSTOMERORDERNO   = "OrderList/Order/CustomAttributes/@CustomerOrderNo";
		public static final String XPATH_FAN_BILL_EMAIL 			= "PersonInfoBillTo/@EMailID";
		public static final String XPATH_FAN_SHIP_EMAIL 			= "PersonInfoShipTo/@EMailID";
		
		
		
		//Email Comunications Services
		public static final String CREATE_EMAIL_ENTRY_SERVICE     = "FanCreateEmailEntry";
		public static final String GET_LIST_EMAIL_ENTRY_SERVICE   = "FanGetListEmailEntry";
		public static final String FAN_SEND_EMAIL_TRIGGER_SERVICE = "FanSendEmailTrigger";
		public static final String FAN_CHANGE_EMAIL_ENTRY_SERVICE = "FanChangeEmailEntry";
		
		//MISC
		public static final String EMAIL_STATUS_INIT 	 	 = "Initial";
		public static final String EMAIL_STATUS_COMPLETE 	 = "Complete";
		public static final String CHANGE_ORDER_CANCEL   	 = "Change Order Cancel";
		public static final String SCHED_ORDER_CANCEL 	 	 = "Schedule Order Cancel";
		public static final String PAYMENT_COL_ON_INV 		 = "Payment Collection Returns";
		public static final String RECEIVE_ON_RETURN_RECEIPT = "Return Receipt Success";
		public static final String CONFIRM_SHIPMENT			 = "Confirm Shipment Success";
		public static final String NUMBER_OF_RECORDS 		 = "NumRecordsToBuffer";
		public static final String REVISED_SHIP_BY_DATE		 = "Revised Ship By Date";
		public static final String REVISED_SHIP_DATE		 = "REVISED_SHIP_DATE";
		public static final String CUSTOMER_ORDER_NO		 = "CustomerOrderNo";
		public static final String CUSTOMER_ID		 		 = "CustomerID";
		public static final String TRANSACTION_AMOUNT 		 = "TransactionAmount";
		public static final String SHIPMENT_KEY 			 = "SHIPMENT_KEY";
		public static final String EMPTY_STRING 			 = "";
		
}
