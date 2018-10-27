
package com.fanatics.sterling.util;

/**
 * This class contains constants required for the fanatics Etaildirect Implementation.
 * @(#) FANConstants.java    
 * Created on   Aug 24, 2007
 *              3:17:30 PM
 *
 * Package Declaration: 
 * File Name:       FANConstants.java
 * Package Name:    com.fanatics.sterling.util;
 * Project name:    fanatics
 * Type Declaration:    
 * Class Name:      FANConstants
 * 
 * @author mgarg
 * @version 1.0
 * @history Aug 24, 2007
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

public interface FANConstants {

	public static final String TAG_MERGE_ROOT_DOC = "MergedDocument";
	public static final String TAG_MERGE_INPUT_DOC = "InputDocument";
	public static final String TAG_MERGE_ENVIRONMENT_DOC = "EnvironmentDocument";
	public static final String TAG_INTERNAL_ENVELOPE = "EnvironmentObjectEnvelope";

	// Keys used in Env's user object and used as custom API's custom property
	// names
	public static final String KEY_CURR_ORDER_HEADER_KEY = "fanatics.etaildirect.orderheaderkey";
	public static final String KEY_CURR_ORDER_NO = "fanatics.etaildirect.orderno";
	public static final String KEY_CURR_ENTERPRISE_CODE = "fanatics.etaildirect.enterprise.code";
	public static final String KEY_CURR_ORDER_LINE_KEY = "fanatics.etaildirect.orderlinekey";
	public static final String KEY_CURR_LINE_TYPE = "fanatics.etaildirect.orderline.type";
	public static final String KEY_CURR_LINE_STATUS = "fanatics.etaildirect.orderline.status";
	public static final String KEY_ORDER_HOLD_TYPE = "fanatics.etaildirect.hold.type";
	public static final String KEY_SETDOC_ENV_KEY = "fanatics.setdoc.env.key";
	public static final String KEY_GETDOC_ENV_KEY = "fanatics.getdoc.env.key";
	public static final String KEY_MERGEDOC_ENV_KEY = "fanatics.mergedoc.env.key";
	public static final String KEY_YANTRA_SERVICE_NAME = "fanatics.yantra.service.name";
	public static final String KEY_YANTRA_API_NAME = "fanatics.yantra.api.name";
	public static final String KEY_SUPRESS_EXCEPTION = "fanatics.yantra.suppress.exception";
	
	// Templates:
	public static final String TEMPLATE_ORDER_LINE_STATUS_LIST = "<OrderLineStatusList><OrderStatus Status=\"\"/></OrderLineStatusList>";
	public static final String TEMPLATE_ORDER_DETAILS_HOLD = "<Order OrderHeaderKey=\"\"><OrderHoldTypes><OrderHoldType HoldType=\"\" Status=\"\"/></OrderHoldTypes></Order>";
	public static final String TEMPLATE_CREATE_EXCEPTION = "<Inbox InboxKey=\"\"/>";
	public static final String XPATH_ORDER_LINES = "/Order/OrderLines/OrderLine";
	public static final String XPATH_ORDER_DETAILS_ON_HOLD = "/Order/OrderHoldTypes/OrderHoldType[@Status='1100']";
	public static final String XPATH_LINES_NOT_CLOSED_STATUS = "/Order/OrderLines/OrderLine[@MinLineStatus!='1100.1000']";
	
	// API Tester URL
   public static final String HTTP_API_URL ="https://test.fanatics.com:9443/smcfs/interop/InteropHttpServlet";

	// Date Format
	public static final String YANTRA_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	public static final String CANCELLED_STATUSES_DESCRIPTION = "Cancelled";
	public static final String RELEASED_STATUSES_DESCRIPTION = "Released";
	public static final String WAVED_STATUSES_DESCRIPTION = "Waved";
	public static final String GET_ORDER_RELEASE_LIST_API = "getOrderReleaseDetails";
	

	public static final String CREATE_ACTION = "CREATE";
	public static final String MODIFY_ACTION = "MODIFY";
	public static final String CREATE_USER_API = "createUserHierarchy";
	public static final String MODIFY_USER_API = "modifyUserHierarchy";
	public static final String USER_DOC_ELEMENT = "User";
	public static final String USER_LOGINID_ATTRIBUTE = "Loginid";
	public static final String USER_ACTION_ATTRIBUTE = "Action";
	public static final String USER_SYNCSTATUS_ATTRIBUTE = "SyncStatus";
	public static final String USER_TIMESTAMP_ATTRIBUTE = "Timestamp";
	public static final String GET_USER_LIST_API = "getUserList";
	
	
	public static final String PROMISE="Promise";
    public static final String XPATH_ITEMID="Item/@ItemID";
    public static final String RESERVE_ITEM_INV_API="reserveItemInventory";
    public static final String CANCEL_ITEM_INV_API="cancelReservation";
    public static final String RESERVE_ITEM_INV="ReserveItemInventory";
   public static final String CHECK_INV="CheckInventory";
   public static final String ITEM_ID="ItemID";
   public static final String DEFAULT_ORG="DEFAULT";
   public static final String fanaticsDIRECT_ORG="fanaticsDirect";
   public static final String PROD_CLASS_GOOD="GOOD";
   public static final String fanatics_SHIP_NODE="3PLDC";
   public static final String ORGANIZATION_CODE = "OrganizationCode";
   public static final String PRODUCT_CLASS="ProductClass";
   public static final String ORDERED_QTY="OrderedQty";
   public static final String RESERVATION_ID="ReservationID";
   public static final String UOM="UnitOfMeasure";
   public static final String PRIME_LINE_NO="PrimeLineNo";
   public static final String TRAN_LINE_ID="TransactionalLineId";
   public static final String CANCEL_RESERVATION="CancelReservation";
   public static final String ORDER_LINE_TRAN_QTY="OrderLineTranQuantity";
   public static final String ACTION="Action";
   public static final String ORDER_NO = "OrderNo";
   public static final String SHIP_NODE = "ShipNode";
   public static final String UOM_EACH="EACH";
   public static final String ORDER_LINES="OrderLines";
   public static final String PERSON_INFO_SHIP_TO = "PersonInfoShipTo";
   public static final String CARRIER_SERVICE_CODE="CarrierServiceCode";
   public static final String HEADER_CHARGES="HeaderCharges";
   public static final String HEADER_CHARGE="HeaderCharge";
   public static final String CHARGE_AMOUNT="ChargeAmount";
   public static final String CHRG_NAME_SHIPPING="Shipping";
   
   public static final String ELE_MULTI_API = "MultiApi";
   public static final String ELE_API = "API";
   public static final String ELE_INPUT = "Input";
   public static final String ELE_ASSIGNMENT_TAG = "Assignment";
   public static final String ELE_SHIP_NODE = "ShipNode";
   public static final String ATT_CONSIDER_ALL_NODES = "ConsiderAllNodes";
   public static final String ATT_CONSIDER_INV_NODE_CONTROL = "ConsiderInventoryNodeControl";
   public static final String ATT_DISTRIBUTION_RULE_ID = "DistributionRuleId";
   public static final String API_GET_DISTRIBUTION_SETUP = "getDistributionSetup";	
   
   
   
   public static final String API_ChangeOrderStatus = "changeOrderStatus";
	public static final String ELEM_OrderStatusChange = "OrderStatusChange";
	public static final String ATT_TransactionId = "TransactionId";
	public static final String ATT_BaseDropStatus = "BaseDropStatus";
	public static final String STATUS_PICK_CONFIRMED = "3200.1000";
	public static final String ATT_Quantity = "Quantity";
	public static final String API_changeRelease = "changeRelease";
	public static final String API_scheduleOrderLines = "scheduleOrderLines";
	public static final String N = "N";
	public static final String Y = "Y";
	public static final String ATT_IgnoreReleaseDate = "IgnoreReleaseDate";
	public static final String ATT_ScheduleAndRelease = "ScheduleAndRelease";
	public static final String ELEM_PromiseLines = "PromiseLines";
	public static final String ELEM_PromiseLine = "PromiseLine";
	public static final String ATT_ShipDate = "ShipDate";
	public static final String ATT_SubLineNo = "SubLineNo";
	public static final String ELEM_OrderRelease = "OrderRelease";
	public static final String ATT_Action = "Action";
	public static final String ATT_Override = "Override";
	public static final String ACTION_BACKORDER = "BACKORDER";
	public static final String ATT_ChangeInQuantity = "ChangeInQuantity";
	public static final String MINUS = "-";
	public static final String ATT_ChangeForAllAvailableQty = "ChangeForAllAvailableQty";
	public static final String ATT_DocumentType = "DocumentType";
	public static final String ATT_EnterpriseCode = "EnterpriseCode";
	public static final String API_getOrderList = "getOrderList";
	public static final String STATUS_FOR_RELEASED_TO_STORE = "3200";
	public static final String XPATH_ORDER_RELEASE_LINE = "/OrderRelease/OrderLines/OrderLine";
	public static final String XPATH_ORDER_RELEASE_LIST_LINE = "/OrderReleaseList/OrderRelease/OrderLines/OrderLine";
	public static final String XPATH_ORDER_RELEASE_LIST_LINE_NOTE = "/OrderReleaseList/OrderRelease/OrderLines/OrderLine[@PrimeLineNo='";
	public static final String XPATH_ORDER_RELEASE_LIST_LINE_NOTE_CLOSE_PATH = "']/Notes/Note";
	public static final String API_GET_ORDER_RELEASE_LIST = "getOrderReleaseList";
	public static final String EL_ORDER_RELEASE = "OrderRelease";
	public static final String EL_ORDER_LINES = "OrderLines";
	public static final String EL_ORDER_LINE = "OrderLine";
	public static final String EL_ORDER = "Order";
	public static final String EL_ORDER_RELEASE_DTL = "OrderReleaseDetail";
	public static final String AT_ACTION = "Action";
	public static final String AT_PROD_CLASS = "ProductClass";
	public static final String AT_PRIME_LINE_NO = "PrimeLineNo";
	public static final String AT_SUB_LINE_NO = "SubLineNo";
	public static final String AT_STATUS_QTY = "StatusQuantity";
	public static final String AT_QTY_CHANGED = "ChangeInQuantity";
	public static final String AT_RELEASE_NO = "ReleaseNo";
	public static final String AT_DOC_TYPE = "DocumentType";
	public static final String AT_ENTRP_CODE = "EnterpriseCode";
	public static final String AT_OVERRIDE = "Override";
	public static final String AT_ORDER_NO = "OrderNo";
	public static final String AT_SALES_ORDER_NO = "SalesOrderNo";
	public static final String AT_QTY = "Quantity";
	public static final String AT_NOTE_REASON = "ReasonCode";
	public static final String AT_MODIFICATION_REASON = "ModificationReasonCode";
	public static final String AT_MODIFICATION_REASON_TEXT = "ModificationReasonText";
	public static final String AT_FULFILLMENT_TYPE = "FulfillmentType";

	public static final String ATT_LOCALE_TIMEZONE = "Timezone";
	public static final String ATT_LOCALE_LOCALECODE = "Localecode";
	public static final String EL_LOCALE = "Locale";
	public static final String ATT_ORG_LOCALE_CODE = "LocaleCode";
	public static final String EL_ORGANIZATION = "Organization";
	public static final String ATT_TO_DATE = "ToDate";
	public static final String ATT_FROM_DATE = "FromDate";
	public static final String ATT_CALENDAR_KEY = "CalendarKey";
	public static final String ATT_ORGANIZATION_CODE = "OrganizationCode";
	public static final String EL_CALENDAR = "Calendar";
	public static final String ATT_WORKDAY_TYPE = "Type";
	public static final String ATT_DAY_OF_MONTH = "DayOfMonth";
	public static final String EL_DATE = "Date";
	public static final String EL_DATES = "Dates";
	public static final String ATT_EXTN_TOCONTAINER_NO = "ExtnTOContainerNo";
	public static final String ATT_EXTN_TOCARRIER = "ExtnTOCarrier";
	public static final String ATT_EXTN_TOTRACKING_NO = "ExtnTOTrackingNo";
	public static final String ATT_CONTAINER_NO = "ContainerNo";
	public static final String ATT_TRACKING_NO = "TrackingNo";
	public static final String ATT_FULFILLMENT_TYPE = "FulfillmentType";
	public static final String ATT_EXTN_WEB_ORDER_NUMBER = "ExtnWebOrderNumber";
	public static final String ATT_SALES_ORDER_NO = "SalesOrderNo";
	public static final String ATT_EXTN_PICK_UP_BY_DATE = "ExtnPickUpByDate";
	public static final String ATT_EXTN_PRINT_BY_DATE = "ExtnPrintByDate";
	public static final String VAL_FULFILL_TYPE_BOPIS = "BOPIS";
	public static final String ATT_SHIPPING_CALENDAR_KEY = "ShippingCalendarKey";
	public static final String ATT_NODE = "Node";
	public static final String ATT_SHIP_NODE = "ShipNode";
	public static final String ATT_ORDER_DATE = "OrderDate";
	public static final String YANTRA_DATE_FORMAT2 = "yyyy-MM-dd'T'HH:mm:ss" ;
	public static final String STORENET_DATE_FORMAT = "yyyyMMddHHmmss" ;
	public static final String SHORT_DATE_FORMAT = "yyyy-MM-dd" ;
	public static final String API_GET_LOCALE_LIST = "getLocaleList";
	public static final String API_GET_ORGANIZATION_HIERARCHY = "getOrganizationHierarchy";
	public static final String API_GET_CALENDAR_DAY_DETAILS = "getCalendarDayDetails";
	
	public static final String ATT_LastName = "LastName";
	public static final String ATT_FirstName = "FirstName";
	public static final String ATT_PersonInfoShipTo = "PersonInfoShipTo";
	public static final String ATT_CorporatePersonInfo = "CorporatePersonInfo";
	public static final String API_getOrganizationList = "getOrganizationList";
	public static final String ATT_ReservationID = "ReservationID";
	public static final String ATT_ChainedFromOrderLineKey = "ChainedFromOrderLineKey";
	public static final String ATT_ShipToKey = "ShipToKey";
	public static final String XPATH_ORDER_ORDER_LINES_ORDER_LINE = "/Order/OrderLines/OrderLine";
	public static final String DocumentType_0006 = "0006";
	public static final String XPATH_SHIPMENT_CONTAINERS_CONTAINER_CONTAINER_DETAILS_CONTAINER_DETAIL_SHIPMENT_LINE = "/Shipment/Containers/Container/ContainerDetails/ContainerDetail/ShipmentLine";
	public static final String ATT_InventoryCheckCode = "InventoryCheckCode";
	public static final String ATT_Node = "Node";
	public static final String ATT_OrderLineSourcingCntrl = "OrderLineSourcingCntrl";
	public static final String ATT_OrderLineSourcingControls = "OrderLineSourcingControls";
	public static final String XPATH_SHIPMENT_ORDER_RELEASES_ORDER_RELEASE_ORDER = "/Shipment/OrderReleases/OrderRelease/Order";
	public static final String CARRIER_SERVICE_CODE_GRN = "GRN";
	public static final String STATUS_2160 = "2160";
	public static final String STATUS_1310 = "1310";
	public static final String ATT_STATUS = "Status";
	public static final String ELEM_FROM_ORDER_RELEASE_STATUS = "FromOrderReleaseStatus";
	public static final String ELEM_TO_ORDER_RELEASE_STATUS ="ToOrderReleaseStatus" ;
	public static final String API_CHANGE_ORDER = "changeOrder";
	public static final String fanatics_ACTION = "fanaticsAction";
	public static final String ACTION_fanatics_CANCEL_SALES_ORDER = "fanaticsCancelSalesOrder";
	public static final String NONE = "None";
	public static final String ACTION_PERFORMED = "ActionPerformed";
	
	
	
	public static final String NO_VALUE = "";
	public static final String ATT_QUANTITY = "Quantity";
	public static final String CANCEL = "CANCEL";
	public static final String ATT_SUPPRESS_SOURCING = "SuppressSourcing";
	public static final String ATT_SUPPRESS_PROCUREMENT = "SuppressProcurement";
	public static final String _1ST_BACKORDER = "1ST_BACKORDER";
	public static final String ATT_REASON_TEXT = "ReasonText";
	public static final String NOINV = "NOINV";
	public static final String ATT_INVENTORY_CHECK_CODE = "InventoryCheckCode";
	public static final String ELEM_ORDER_LINE_SOURCING_CNTRL = "OrderLineSourcingCntrl";
	public static final String _999 = "999";
	public static final String PRODUCT_SOURCING = "PRODUCT_SOURCING";
	public static final String ATT_IS_FIRM_PREDEFINED_NODE = "IsFirmPredefinedNode";
	public static final String _3PLDC = "3PLDC";
	public static final String MODIFY = "MODIFY";
	public static final String ELEM_DETAILS = "Details";
	public static final String ATT_ORDER_LINE_KEY = "OrderLineKey";
	public static final String API_SCHEDULE_ORDER = "scheduleOrder";
	public static final String ATT_CHECK_INVENTORY = "CheckInventory";
	public static final String API_RELEASE_ORDER = "releaseOrder";
	public static final String ATT_SOURCING_CLASSIFICATION = "SourcingClassification";
	public static final String GRN = "GRN";
	public static final String ATT_BACK_ORDERED_QUANTITY = "BackOrderedQuantity";
	public static final String ELEM_BACK_ORDERED_FROM = "BackOrderedFrom";
	public static final String ELEM_ORDER_LINE = "OrderLine";
	public static final String ELEM_ORDER_LINES = "OrderLines";
	public static final String ATT_OVERRIDE = "Override";
	public static final String ATT_ACTION = "Action";
	public static final String ATT_ORDER_HEADER_KEY = "OrderHeaderKey";
	public static final String ACTION_BACKORDER_FROM_NODE_TRANSFERORDER = "BACKORDER_FROM_NODE_TRANSFERORDER";
	public static final String ATT_IGNORE_RELEASE_DATE = "IgnoreReleaseDate";
	public static final String SERVICE_fanaticsPOST_TO_INTERNAL_QFOR_PROCESSING_SERVICE = "fanaticsPostToInternalQForProcessingService";
	public static final String SERVICE_fanatics_SEND_BACKORDER_EMAIL = "fanaticsCancelSalesOrderOnTOCancelService";
	public static final String ACTION_fanaticsADD_PROCURE_FROM_NODE_TO_RELEASE = "fanaticsAddProcureFromNodeToRelease";
	public static final String INFINV = "INFINV";
	public static final String ELEM_ORDER_LINE_SOURCING_CONTROLS = "OrderLineSourcingControls";
	public static final String ATT_PROCURE_FROM_NODE = "ProcureFromNode";
	public static final String DELIVERY_METHOD_PICK = "PICK";
	public static final String ATT_DELIVERY_METHOD = "DeliveryMethod";
	public static final String ATT_IS_PROCUREMENT_ALLOWED = "IsProcurementAllowed";
	public static final String REMOVE = "REMOVE";
	public static final String ATT_CHAINED_FROM_ORDER_HEADER_KEY = "ChainedFromOrderHeaderKey";
	public static final String XML_ORDER = "<Order/>";
	public static final String ATT_EXTN_TORECEIVE_DATE = "ExtnTOReceiveDate";
	public static final String XML_ORDER_RELEASE = "<OrderRelease/>";
	public static final String ELEM_INPUT = "Input";
	public static final String API_CHANGE_RELEASE = "changeRelease";
	public static final String ATT_NAME = "Name";
	public static final String ELEM_API = "API";
	public static final String XPATH_SHIPMENT_SHIPMENT_LINES_SHIPMENT_LINE = "/Shipment/ShipmentLines/ShipmentLine";
	public static final String XML_MULTI_API = "<MultiApi />";
	public static final String API_MULTI_API = "multiApi";
	public static final String ATT_SHIPMENT_TYPE = "ShipmentType";
	public static final String AT_SHIPMENT_KEY = "ShipmentKey";	
	
	public static final String ARG_fanatics_CALLING_ORG_CODE = "CallingOrganizationCode";
	public static final String ATT_fanatics_DEFAULT_DISTRIBUTION_RULE_ID = "DefaultDistributionRuleId";
	public static final String ATT_fanatics_OPERATION = "Operation";
	
	//REST
	public static final String AUTHTYPE_BASIC = "Basic";
	public static final int SC_OK = 200;
	public static final int SC_CREATED = 201;
	public static final int SC_NO_CONTENT = 204;
	
	public static final String API_GET_ORDER_DET = "getOrderDetails";
	public static final String ATT_ORDER_NAME = "OrderName";
	public static final String CONSTANT_ZERO = "0";
	public static final String CONSTANT_TWO = "2";
	public static final String CONSTANT_ONE = "1";
	public static final String ATT_Notes = "Notes";
	public static final String ATT_Note = "Note";
	public static final String ELEM_fanatics_OrderHoldType = "OrderHoldType"; 
	public static final String ATT_fanatics_HoldType = "HoldType";
	public static final String STR_1100 = "1100";
	public static final String ATT_NoteText = "NoteText";
	public static final String STR_1300 = "1300";
	public static final String API_getCommonCodeList = "getCommonCodeList";
	public static final String ATT_CommonCode = "CommonCode";
	public static final String ATT_CodeShortDescription = "CodeShortDescription";
	public static final String CONST_DateFormat = "yyyy-MM-dd HH:mm:ss";
	public static final String ATT_OrderDates = "OrderDates";
	public static final String ATT_OrderDate = "OrderDate";
	public static final String ATT_DateTypeId = "DateTypeId";
	public static final String ATT_ExpectedDate = "ExpectedDate";

	public static final String API_COMMON_CODE_LIST = "getCommonCodeList";
	public static final String API_GET_PERSON_INFO_LIST = "getPersonInfoList";
	public static final String ATT_CUSTOMER_EMAIL_ID = "CustomerEMailID";
	public static final String A_ORDER_LIST = "OrderList";
	public static final String A_PERSON_INFO = "PersonInfo";
	public static final String ATT_EMAIL_ID = "EmailID";
	public static final String A_EMAIL_ID_QRY_TYPE = "EMailIDQryType";
	public static final String A_FLIKE = "FLIKE";
	public static final String ATT_PERSON_INFO_KEY = "PersonInfoKey";
	public static final String A_SEARCH_BY_ADDRESS = "SearchByAddress";
	public static final String A_CUSTOMER_EMAIL_ID_QRYTYPE = "CustomerEMailIDQryType";
	public static final String ATT_BILL_TO_KEY = "BillToKey";
	public static final String ATT_TOTAL_ORDER_LIST = "TotalOrderList";
	
	public static final String ATT_CUSTOMER_PHONE_NO = "CustomerPhoneNo";
	public static final String ATT_CUSTOMER_FIRST_NAME = "CustomerFirstName";
	public static final String ATT_CUSTOMER_LAST_NAME = "CustomerLastName";
	public static final String ATT_CUSTOMER_ZIP_CODE = "CustomerZipCode";
	public static final String A_TOTAL_NO_OF_RECORDS = "TotalNumberOfRecords";
	public static final String ATT_ADDRESS_LINE1 = "AddressLine1";
	public static final String ATT_ADDRESS_LINE2 = "AddressLine2";
	public static final String ATT_ZIP_CODE = "ZipCode";
	public static final String A_IS_BILLING_ADDRESS = "IsBillingAddress";
	public static final String ATT_DAY_PHONE = "DayPhone";
	public static final String ATT_CUSTOMER_ORDER_NO = "CustomerOrderNo";
	public static final String A_EMAIL_ID = "EMailID";
	public static final String A_COMPLEX_QUERY = "ComplexQuery";
	public static final String A_OPERATOR = "Operator";
	public static final String AA_OR = "OR";
	public static final String A_OR = "Or";
	public static final String A_EXP = "Exp";
	public static final String A_NAME = "Name";
	public static final String A_VALUE = "Value";
	public static final String A_QRYTYPE = "QryType";
	public static final String A_EQUAL = "EQ";
	public static final String AT_DRAFT_ORDER_FLAG = "DraftOrderFlag";
	
	public static final String ATT_CodeValue = "CodeValue";
	public static final String CONSTANT_FANATICS_WEB = "FANATICS_WEB";
	public static final String CONSTANT_0951 = "0951";
	public static final String CONSTANT_VENDORS = "VENDORS";
	public static final String ATT_SELLER_ORG_CODE = "SellerOrganizationCode";
	public static final String CONSTANT_ITEM ="Item";
	public static final String API_CREATE_EXCEPTION = "createException";
	public static final String ELE_SHIPMENT_LINES = "ShipmentLines";
	public static final String ELE_SHIPMENT_LINE = "ShipmentLine";
	
}
