/* Copyright 2010, Sterling Commerce, Inc. All rights reserved. */
/*
 LIMITATION OF LIABILITY
 THIS SOFTWARE SAMPLE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED 
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 IN NO EVENT SHALL STERLING COMMERCE, Inc. BE LIABLE UNDER ANY THEORY OF 
 LIABILITY (INCLUDING, BUT NOT LIMITED TO, BREACH OF CONTRACT, BREACH 
 OF WARRANTY, TORT, NEGLIGENCE, STRICT LIABILITY, OR ANY OTHER THEORY 
 OF LIABILITY) FOR (i) DIRECT DAMAGES OR INDIRECT, SPECIAL, INCIDENTAL, 
 OR CONSEQUENTIAL DAMAGES SUCH AS, BUT NOT LIMITED TO, EXEMPLARY OR 
 PUNITIVE DAMAGES, OR ANY OTHER SIMILAR DAMAGES, WHETHER OR NOT 
 FORESEEABLE AND WHETHER OR NOT STERLING OR ITS REPRESENTATIVES HAVE 
 BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES, OR (ii) ANY OTHER 
 CLAIM, DEMAND OR DAMAGES WHATSOEVER RESULTING FROM OR ARISING OUT OF
 OR IN CONNECTION THE DELIVERY OR USE OF THIS INFORMATION.
 */

package com.nwcg.icbs.yantra.constant.common;

import com.nwcg.icbs.yantra.util.common.ResourceUtil;

/**
 * Contains constants required for the USFS NWCG ICBS BR1 & BR2
 */
public interface NWCGConstants {

	// BEGIN - CR 904 - March 13, 2014

	public static final String XPATH_ADJLOCINV = "/AdjustLocationInventory";
	public static final String XPATH_ADJLOCINV_SOURCE = "/AdjustLocationInventory/Source";
	public static final String XPATH_ADJLOCINV_SOURCE_INVENTORY_INVITEM = "/AdjustLocationInventory/Source/Inventory/InventoryItem";
	public static final String XPATH_ADJLOCINV_AUDIT = "/AdjustLocationInventory/Audit";
	public static final String XPATH_WO = "/WorkOrder";
	public static final String XPATH_NODEINV_LOCINVLIST_LOCINV_ITEMINVDTLLIST = "/NodeInventory/LocationInventoryList/LocationInventory/ItemInventoryDetailList";
	public static final String XPATH_ADJLOCINV_SOURCE_INV = "/AdjustLocationInventory/Source/Inventory";
	public static final String SERVICE_ITEMID = "ServiceItemID";
	public static final String ITEM_INVENTORY_DETAIL = "ItemInventoryDetail";
	public static final String TEMPLATE_GET_WO = "<WorkOrder ServiceItemID=\"\" />";
	public static final String TEMPLATE_GET_NODE_INV = "<NodeInventory><LocationInventoryList><LocationInventory><ItemInventoryDetailList><ItemInventoryDetail InventoryStatus=\"\"></ItemInventoryDetail></ItemInventoryDetailList></LocationInventory></LocationInventoryList></NodeInventory>";

	// END - CR 904 - March 13, 2014

	public static final String TAG_MERGE_ROOT_DOC = "MergedDocument";

	public static final String TAG_MERGE_INPUT_DOC = "InputDocument";

	public static final String TAG_MERGE_ENVIRONMENT_DOC = "EnvironmentDocument";

	public static final String TAG_INTERNAL_ENVELOPE = "EnvironmentObjectEnvelope";

	// Keys used in Env's user object and used as custom API's custom property
	// names
	public static final String KEY_CURR_ORDER_HEADER_KEY = "nwcg.icbs.orderheaderkey";

	public static final String KEY_CURR_ORDER_NO = "nwcg.icbs.orderno";

	public static final String KEY_CURR_ENTERPRISE_CODE = "nwcg.icbs.enterprise.code";

	public static final String KEY_CURR_ORDER_LINE_KEY = "nwcg.icbs.orderlinekey";

	public static final String KEY_CURR_LINE_TYPE = "nwcg.icbs.orderline.type";

	public static final String KEY_CURR_LINE_STATUS = "nwcg.icbs.orderline.status";

	public static final String KEY_ORDER_HOLD_TYPE = "nwcg.icbs.hold.type";

	public static final String KEY_SETDOC_ENV_KEY = "nwcg.setdoc.env.key";

	public static final String KEY_GETDOC_ENV_KEY = "nwcg.getdoc.env.key";

	public static final String KEY_MERGEDOC_ENV_KEY = "nwcg.mergedoc.env.key";

	public static final String KEY_YANTRA_SERVICE_NAME = "nwcg.yantra.service.name";

	public static final String KEY_YANTRA_API_NAME = "nwcg.yantra.api.name";

	public static final String KEY_ORDER_LINE_IDENTIFIER_XPATH = "nwcg.icbs.orderline.identifier.xpath";

	public static final String KEY_INCIDENT_NO_XPATH = "nwcg.icbs.incidentno.xpath";

	public static final String KEY_INCIDENT_YEAR_XPATH = "nwcg.icbs.incidentyear.xpath";

	public static final String KEY_DEV_MODE = "project.yantra.devmode";

	// Templates:
	public static final String TEMPLATE_ORDER_LINE_STATUS_LIST = "<OrderLineStatusList><OrderStatus Status=\"\"/></OrderLineStatusList>";

	public static final String TEMPLATE_ORDER_DETAILS_HOLD = "<Order OrderHeaderKey=\"\"><OrderHoldTypes><OrderHoldType HoldType=\"\" Status=\"\"/></OrderHoldTypes></Order>";

	public static final String TEMPLATE_CREATE_EXCEPTION = "<Inbox InboxKey=\"\"/>";

	public static final String XPATH_ORDER_LINES = "/Order/OrderLines/OrderLine";

	public static final String XPATH_ORDER_DETAILS_ON_HOLD = "/Order/OrderHoldTypes/OrderHoldType[@Status='1100']";

	public static final String XPATH_LINES_NOT_CLOSED_STATUS = "/Order/OrderLines/OrderLine[@MinLineStatus!='1100.1000']";

	// SSO
	public static final String KEY_SSO_USER_IDENTIFIER_FIELD = "nwcg.sso.user.identifier.field";

	public static final String VAL_SSO_TIMEOUT_IDENTIFIER_COOKIE = "JSESSIONID";

	public static final String TEMPLATE_GET_USER_LIST = "<UserList><User Loginid=''/></UserList>";

	public static final String CURRENT_USER = "CurrentUser";

	// Date Format
	public static final String YANTRA_DATE_FORMAT = ResourceUtil
			.get("nwcg.icbs.yantra.date.format");

	// ORDER and SHIPMENT SEQUENCES
	public static final String SEQ_NWCG_INCIDENT_ISSUENO = "SEQ_NWCG_INCIDENT_ISSUENO";

	public int MAX_DIGITS_SEQ_NWCG_INCIDENT_ISSUENO = 10;

	// This is the 2 digit suffix attached to the issue number to the shipment
	// number
	public int MAX_DIGITS_SEQ_NWCG_ISSUE_SHIPMENTNO = 2;

	// RECEIPT SEQUENCES
	public static final String SEQ_NWCG_RECEIPTNO = "SEQ_NWCG_RECEIPTNO";

	public int MAX_DIGITS_SEQ_NWCG_RECEIPTNO = 10;

	// service names
	// ---[Incident order services]--//
	public static final String NWCG_CREATE_INCIDENT_ORDER_SERVICE = ResourceUtil
			.get("nwcg.icbs.createincidentorder.service");

	public static final String NWCG_GET_INCIDENT_ORDER_SERVICE = ResourceUtil
			.get("nwcg.icbs.getincidentorder.service");

	public static final String NWCG_DELETE_INCIDENT_ORDER_SERVICE = ResourceUtil
			.get("nwcg.icbs.deleteincidentorder.service");

	public static final String NWCG_MODIFY_INCIDENT_ORDER_SERVICE = ResourceUtil
			.get("nwcg.icbs.modifyincidentorder.service");

	public static final String NWCG_GET_INCIDENT_ORDERLIST_SERVICE = ResourceUtil
			.get("nwcg.icbs.getincidentorderlist.service");

	public static final String NWCG_GET_INCIDENT_CONTACTS_LIST_SERVICE = ResourceUtil
			.get("nwcg.icbs.getincidentcontactlist.service");

	public static final String NWCG_CREATE_INCIDENT_CONTACTS_SERVICE = ResourceUtil
			.get("nwcg.icbs.createincidentcontacts.service");

	public static final String NWCG_CREATE_ROSS_ACCOUNT_CODES_SERVICE = ResourceUtil
			.get("nwcg.icbs.createrossaccountcodes.service");

	public static final String NWCG_GET_ROSS_ACCOUNT_CODES_LIST_SERVICE = ResourceUtil
			.get("nwcg.icbs.getrossaccountcodeslist.service");

	// Adding service names to remove ross account codes and contact info --
	// Sunjay
	public static final String NWCG_DELETE_ROSS_ACCOUNT_CODES_SERVICE = ResourceUtil
			.get("nwcg.icbs.deleterossaccountcodes.service");

	public static final String NWCG_DELETE_INCIDENT_CONTACTS_SERVICE = ResourceUtil
			.get("nwcg.icbs.deleteincidentcontacts.service");

	public static final String NWCG_MODIFY_ROSS_ACCOUNT_CODES_SERVICE = "NWCGModifyROSSAcctCodesService";

	// create issue
	public static final String NWCG_CREATE_ISSUE_SERVICE = ResourceUtil
			.get("nwcg.icbs.createissue.service");

	//
	public static final String ADDRESS_TYPE_DELIVER = "DELIVER";

	public static final String ADDRESS_TYPE_BILL = "BILL";

	public static final String ADDRESS_TYPE_SHIP = "SHIP";

	// CommonUtilities.java
	public static final String PRICE_PROGRAM = "PRICE_PROGRAM";

	public static final String PROGRAM_NAME = "PROGRAM_NAME";

	public static final String NWCG_PRICE_PROGRAM = "NWCG_PRICE_PROGRAM";

	// NWCGCreateIssuesFromBackOrderLines Constants
	public static final String REQUEST_NO_EXTN_DELIM = ".";

	public static final String ENTERPRISE_CODE = "NWCG";

	public static final String FS_OWNER_AGENY = "FS";

	public static final String BLM_OWNER_AGENCT = "BLM";

	public static final String OTHER_OWNER_AGENY = "OTHER";

	public static final String PRIMARY_IDENTIFIER_NULL = "PRIMARY_IDENTIFIER_NULL";

	// end back order processing constants

	// constants for trackable inventory
	public static final String NWCG_CREATE_TRACKABLE_INVENTORY_SERVICE = ResourceUtil
			.get("nwcg.icbs.createtrackableinventory.service");

	public static final String NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE = ResourceUtil
			.get("nwcg.icbs.updatetrackableinventory.service");

	public static final String NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE = ResourceUtil
			.get("nwcg.icbs.gettrackableitemlist.service");

	public static final String NWCG_GET_SUPPLIER_ITEM_DETAILS = ResourceUtil
			.get("nwcg.icbs.getsupplieritemdetails.service");

	public static final String NWCG_GET_SUPPLIER_ITEM_LIST = ResourceUtil
			.get("nwcg.icbs.getsupplieritemlist.service");

	public static final String SERIAL_STATUS_AVAILABLE = "A";

	public static final String SERIAL_STATUS_AVAILABLE_DESC = "Available";

	// CR 496
	public static final String SERIAL_STATUS_NOT_AVAILABLE = "N";

	public static final String SERIAL_STATUS_NOT_AVAILABLE_DESC = "Not Available";

	// end of CR 496
	public static final String SERIAL_STATUS_AVAILABLE_IN_KIT = "K";

	public static final String SERIAL_STATUS_AVAILABLE_IN_KIT_DESC = "Available in Kit";

	public static final String SERIAL_STATUS_BREAKDOWN_KIT = "B";

	public static final String SERIAL_STATUS_BREAKDOWN_KIT_DESC = "Breakdown Kit";

	public static final String TRANSACTION_INFO_TYPE_RECEIPT = "Receipt";

	public static final String TRANSACTION_INFO_TYPE_RETURN = "Return";

	public static final String SERIAL_STATUS_TRANSFERRED = "T";

	public static final String SERIAL_STATUS_TRANSFERRED_DESC = "Transferred";

	public static final String TRANSACTION_INFO_TYPE_CACHE_TRANSFERRED = "Cache Transfer";

	public static final String TRANSACTION_INFO_TYPE_BUILD_KIT = "Build Kit";

	public static final String SERIAL_STATUS_ISSUED = "I";

	public static final String SERIAL_STATUS_ISSUED_DESC = "Issue";

	public static final String TRANSACTION_INFO_TYPE_ISSUE = "Issue";

	public static final String TRANSACTION_INFO_TYPE_INCIDENT_TRANSFER = "Incident Transfer";

	public Object TRANSFER_ORDER_DOCUMENT_TYPE = "0006";

	public static final String SERIAL_STATUS_WORKORDERED_DESC = "Workordered";

	public static final String SERIAL_STATUS_WORKORDERED = "W";

	public static final String NWCG_RFI_DISPOSITION_CODE = "RFI";

	public static final String NWCG_NRFI_DISPOSITION_CODE = "NRFI";

	public Object PHYSICAL_KIT_CODE = "PK";

	// Added by Gaurav for CR-671
	public static final String SERIAL_STATUS_MISSING = "M";

	public static final String SERIAL_STATUS_NMISSING_DESC = "NOT AVAILABLE";

	public static final String SERIAL_STATUS_NRFI = "N";

	public static final String SERIAL_STATUS_NRFI_DESC = "NRFI";

	public static final String SERIAL_STATUS_UNS = "U";

	public static final String SERIAL_STATUS_UNS_DESC = "UNSERVICE";

	public static final String SERIAL_STATUS_UNSNWT_DESC = "UNS-NWT";

	public Object SERVICE_ITEM_ID_DE_KITTING = "DEKITTING";

	public Object SERVICE_ITEM_ID_KITTING = "KITTING";

	public static final String TRANSACTION_INFO_TYPE_BREAKDOWN_KIT = "Breakdown Kit";

	public Object NWCG_BLIND_RECEIPT_DOCUMENT_TYPE = "0010";

	public static final String CURRENCY_USD = "USD";

	public Object SERVICE_ITEM_ID_REFURB_KITTING = "REFURB-KITTING";

	public Object SERVICE_ITEM_ID_REFURB_DEKITTING = "REFURB-DEKITTING";

	public static final String TRANSACTION_INFO_TYPE_REFURB = "Refurb";

	public Object SERVICE_ITEM_ID_REFURBISHMENT = "REFURBISHMENT";

	public static final String NWCG_STATUS_CODE_RECEIVED_AS_COMPONENT = "C";

	public static final String NWCG_STATUS_CODE_RECEIVED_AS_COMPONENT_DESC = "Received as Component";

	public static final String NWCG_TRANSACTION_TYPE_INV_ADJ = "Inventory Adjusted";

	public static final String NWCG_REASON_CODE_DISPOSAL_EXCESS = "DISPOSAL-EXCESS";

	public static final String NWCG_REASON_CODE_DISPOSAL_EXPIRED = "DISPOSAL-EXPIRED";

	public static final String NWCG_REASON_CODE_DISPOSAL_UNSERVICEABLE = "DISPOSAL-UNSERVICEABLE";

	public static final String NWCG_REASON_CODE_DISPOSAL_DAMAGED = "DISPOSAL-DAMAGED";

	public static final String NWCG_REASON_CODE_DISPOSAL_LOST = "DISPOSAL-LOST";

	public static final String NWCG_TRANSACTION_TYPE_EXCESSED = "Item Disposal - Excessed";

	public static final String NWCG_TRANSACTION_TYPE_EXPIRED = "Item Dispoal - Expired";

	public static final String SERIAL_STATUS_DISPOSED_DESC = "Disposed";

	public static final String SERIAL_STATUS_DISPOSED = "D";

	public static final String NWCG_TRANSACTION_TYPE_UNSERVICEABLE = "Disposal - Unserviceable";

	public static final String NWCG_TRANSACTION_TYPE_DAMAGED = "Disposal - Damaged";

	public static final String NWCG_TRANSACTION_TYPE_LOST = "Disposal - Lost";

	public static final String NWCG_STATUS_CODE_WORKORDERED = "W";

	public static final String NWCG_STATUS_CODE_WORKORDERED_DESC = "Workordered";

	public static final String NWCG_TRACKABLEINVENTORY_PROCESS_TYPE_ADJUST_LOCATION = "ADJUST_LOCATION";

	public static final String NWCG_TRACKABLEINVENTORY_PROCESS_TYPE_BLIND_RETURN = "BLIND_RETURN";

	public static final String NWCG_TRACKABLEINVENTORY_PROCESS_TYPE_CONFIRM_DRAFT_ORDER = "CONFIRM_DRAFT_ORDER";

	public static final String NWCG_TRACKABLEINVENTORY_PROCESS_TYPE_CONFIRM_SHIPMENT = "CONFIRM_SHIPMENT";

	public static final String NWCG_TRACKABLEINVENTORY_PROCESS_TYPE_KITTING = "KITTING";

	public static final String NWCG_TRACKABLEINVENTORY_PROCESS_TYPE_RECEIVE_ORDER = "RECEIVE_ORDER";

	public static final String NWCG_TRACKABLEINVENTORY_PROCESS_TYPE_RECEIVE_TRANSFER_ORDER = "RECEIVE_TRANSFER_ORDER";

	public static final String NWCG_TRACKABLEINVENTORY_PROCESS_TYPE_TASK_COMPLETED = "TASK_COMPLETED";

	public static final String NWCG_TRACKABLEINVENTORY_QUEUE_ID = "NWCG_TRACKABLE_INVENTORY";

	public static final String NWCG_TRACKABLEINVENTORY_INBOX_TYPE = "NWCG_TRACKABLE_INVENTORY";

	// END constants for trackable inventory

	// Begin CR858
	// constants for Billing Transaction
	public static final String BillingTransactionExtract_FileName_CodeType = "NWCG_BTE_FN";

	public static final String API_COMMONCODELIST = "getCommonCodeList";
	// End CR858

	// Begin CR840
	public static final String BillingTransactionExtract_FunctionalArea = "LF2000000.HU0000";
	// CR840

	// BEGIN CR 816
	public static final String NWCG_GET_BILLING_TRANSACTION_DATE_REVIEW_LIST_SERVICE = ResourceUtil
			.get("nwcg.icbs.getbillingtransactiondatereviewlist.service");
	// END CR 816

	public static final String NWCG_CREATE_BILLING_TRANSACTION_SERVICE = ResourceUtil
			.get("nwcg.icbs.createbillingtransaction.service");

	public static final String NWCG_GET_BILLING_TRANSACTION_LIST_SERVICE = ResourceUtil
			.get("nwcg.icbs.getbillingtransactionlist.service");

	public static final String NWCG_GET_BILLING_TRANSACTION_DETAIL_SERVICE = ResourceUtil
			.get("nwcg.icbs.getbillingtransactiondetail.service");

	public static final String NWCG_UPDATE_BILLING_TRANSACTION_SERVICE = ResourceUtil
			.get("nwcg.icbs.updatebillingtransaction.service");
	// QC855
	public static final String NWCG_BILLING_TRANSACTION_DELETE_SERVICE = ResourceUtil
			.get("nwcg.icbs.deleteNWCGBillingTransaction.service");

	public static final String NWCG_BLM_OUTPUT_DIR = ResourceUtil
			.get("nwcg.icbs.billingtransaction.output.directory");

	public static final String NWCG_CREATE_BILLING_TRANSACTION_EXTRACT_SERVICE = ResourceUtil
			.get("nwcg.icbs.createbillingtransactionextract.service");

	public static final String NWCG_GET_BILLING_TRANSACTION_EXTRACT_LIST_SERVICE = ResourceUtil
			.get("nwcg.icbs.getbillingtransactionextractlist.service");

	public static final String NWCG_GET_BILLING_TRANSACTION_EXTRACT_DETAIL_SERVICE = ResourceUtil
			.get("nwcg.icbs.getbillingtransactionextractdetail.service");

	public static final String NWCG_UPDATE_BILLING_TRANSACTION_EXTRACT_SERVICE = ResourceUtil
			.get("nwcg.icbs.updatebillingtransactionextract.service");

	public static final String NWCG_BILLINGTRANS_PROCESS_TYPE_CONFIRM_SHIPMENT = "CONFIRM_SHIPMENT";

	public static final String NWCG_BILLINGTRANS_PROCESS_TYPE_CHANGE_SHIPMENT = "CHANGE_SHIPMENT";

	public static final String NWCG_BILLINGTRANS_PROCESS_TYPE_CHANGE_ORDER = "CHANGE_ORDER";

	public static final String NWCG_BILLINGTRANS_PROCESS_TYPE_CHANGE_PURCHASEORDER = "CHANGE_PURCHASEORDER";

	public static final String NWCG_BILLINGTRANS_PROCESS_TYPE_RECEIVE_PURCHASEORDER = "RECEIVE_PURCHASEORDER";

	public static final String NWCG_BILLINGTRANS_PROCESS_TYPE_CHANGE_TRANSFERORDER = "CHANGE_TRANSFERORDER";

	public static final String NWCG_BILLINGTRANS_PROCESS_TYPE_RECEIVE_TRANSFERORDER = "RECEIVE_TRANSFERORDER";

	public static final String NWCG_BILLINGTRANS_PROCESS_TYPE_ADJ_LOCATION_INVENTORY = "ADJ_LOCATION_INVENTORY";

	public static final String NWCG_BILLINGTRANS_PROCESS_TYPE_RETURNS = "RETURNS";

	public static final String NWCG_BILLINGTRANS_PROCESS_TYPE_CONFIRM_INCIDENT_TO = "CONFIRM_INCIDENT_TO";

	public static final String NWCG_BILLINGTRANS_PROCESS_TYPE_CHANGE_INCIDENT_TO = "CHANGE_INCIDENT_TO";

	public static final String NWCG_BILLINGTRANS_PROCESS_TYPE_CONFIRM_WO = "CONFIRM_WO";

	public static final String NWCG_BILLINGTRANS_PROCESS_TYPE_REFURB = "REFURB";

	public static final String NWCG_BILLINGTRANS_PROCESS_TYPE_EXTRACT = "EXTRACT";

	public static final String NWCG_BILLINGTRANS_PROCESS_TYPE_REVIEW = "REVIEW";

	public static final String BILL_TRANS_SEQUENCE_KEY = "SequenceKey";

	public static final String BILL_TRANS_TYPE = "TransType";

	public static final String BILL_TRANS_DATE = "TransDate";

	public static final String BILL_TRANS_QTY = "TransQty";

	public static final String BILL_TRANS_AMOUNT = "TransAmount";

	public static final String BILL_TRANS_NO = "TransactionNo";

	public static final String BILL_TRANS_CREATEUSERID = "TransCreateUserId";

	public static final String BILL_TRANS_MODIFYUSERID = "TransModifyUserId";

	public static final String BILL_TRANS_HEADER_KEY = "TransHeaderKey";

	public static final String BILL_TRANS_LINE_KEY = "TransLineKey";

	public static final String BILL_TRANS_ENTERPRISE_CODE = "EnterpriseCode";

	public static final String BILL_TRANS_DOCUMENT_TYPE = "DocumentType";

	public static final String BILL_TRANS_DOCUMENT_NO = "DocumentNo";

	public static final String BILL_TRANS_FISCAL_YEAR = "TransactionFiscalYear";

	public static final String BILL_TRANS_CACHE_ID = "CacheId";

	public static final String BILL_TRANS_INCIDENT_NO = "IncidentNo";

	public static final String BILL_TRANS_INCIDENT_YEAR = "IncidentYear";

	public static final String BILL_TRANS_INCIDENT_NAME = "IncidentName";

	public static final String BILL_TRANS_ITEM_ID = "ItemId";

	public static final String BILL_TRANS_ITEM_CLASSIFICATION = "ItemClassification";

	public static final String BILL_TRANS_ITEM_PRODUCTLINE = "ItemProductLine";

	public static final String BILL_TRANS_ITEM_DESCRIPTION = "ItemDescription";

	public static final String BILL_TRANS_UOM = "UOM";

	public static final String BILL_TRANS_UNIT_COST = "UnitCost";

	public static final String BILL_TRANS_INCIDENT_FS_ACCT_CODE = "IncidentFsAcctCode";

	public static final String BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE = "IncidentFsOverrideCode";

	public static final String BILL_TRANS_INCIDENT_BLM_ACCT_CODE = "IncidentBlmAcctCode";

	public static final String BILL_TRANS_COST_CENTER = "CostCenter";

	public static final String BILL_TRANS_WBS = "WBS";

	public static final String BILL_TRANS_FUNCTIONAL_AREA = "FunctionalArea";

	public static final String BILL_TRANS_INCIDENT_OTHER_ACCT_CODE = "IncidentOtherAcctCode";

	public static final String BILL_TRANS_IS_ACCOUNT_SPLIT = "IsAccountSplit";

	public static final String BILL_TRANS_SPLIT_AMT_NUMBER = "SplitAmtNumber";

	public static final String BILL_TRANS_LAST_INCIDENT_FS_ACCT_CODE = "LastFsAcctCode";

	public static final String BILL_TRANS_LAST_INCIDENT_FS_OVERRIDE_CODE = "LastFsOverrideCode";

	public static final String BILL_TRANS_LAST_INCIDENT_BLM_ACCT_CODE = "LastBlmAcctCode";

	public static final String BILL_TRANS_LAST_INCIDENT_OTHER_ACCT_CODE = "LastOtherAcctCode";

	public static final String BILL_TRANS_DISPOSITION_CODE = "DispositionCode";

	public static final String BILL_TRANS_LOCATION_ID = "LocationId";

	public static final String BILL_TRANS_EXTRACT_TRANS_NO = "ExtractTransNo";

	public static final String BILL_TRANS_LAST_EXTRACT_DATE = "LastExtractDate";

	public static final String BILL_TRANS_IS_EXTRACTED = "IsExtracted";

	public static final String BILL_TRANS_IS_REVIEWED = "IsReviewed";

	public static final String BILL_TRANS_REASON_CODE = "ReasonCode";

	public static final String BILL_TRANS_REASON_CODE_TEXT = "ReasonCodeText";

	public int BILL_TRANS_AMOUNT_LENGTH = 12;

	public static final String BILL_TRANS_EXTRACT_POSITIVE_ZERO = "{";

	public static final String BILL_TRANS_EXTRACT_POSITIVE_ONE = "A";

	public static final String BILL_TRANS_EXTRACT_POSITIVE_TWO = "B";

	public static final String BILL_TRANS_EXTRACT_POSITIVE_THREE = "C";

	public static final String BILL_TRANS_EXTRACT_POSITIVE_FOUR = "D";

	public static final String BILL_TRANS_EXTRACT_POSITIVE_FIVE = "E";

	public static final String BILL_TRANS_EXTRACT_POSITIVE_SIX = "F";

	public static final String BILL_TRANS_EXTRACT_POSITIVE_SEVEN = "G";

	public static final String BILL_TRANS_EXTRACT_POSITIVE_EIGHT = "H";

	public static final String BILL_TRANS_EXTRACT_POSITIVE_NINE = "I";

	public static final String BILL_TRANS_EXTRACT_NEGATIVE_ZERO = "}";

	public static final String BILL_TRANS_EXTRACT_NEGATIVE_ONE = "J";

	public static final String BILL_TRANS_EXTRACT_NEGATIVE_TWO = "K";

	public static final String BILL_TRANS_EXTRACT_NEGATIVE_THREE = "L";

	public static final String BILL_TRANS_EXTRACT_NEGATIVE_FOUR = "M";

	public static final String BILL_TRANS_EXTRACT_NEGATIVE_FIVE = "N";

	public static final String BILL_TRANS_EXTRACT_NEGATIVE_SIX = "O";

	public static final String BILL_TRANS_EXTRACT_NEGATIVE_SEVEN = "P";

	public static final String BILL_TRANS_EXTRACT_NEGATIVE_EIGHT = "Q";

	public static final String BILL_TRANS_EXTRACT_NEGATIVE_NINE = "R";

	// END constants for Billing Transaction

	// constant for I-Suite
	public static final String NWCG_BLM_ISUITE_DIR = ResourceUtil
			.get("nwcg.icbs.isuite.output.directory");

	// Constants related to refurb
	public static final String NODE = "Node";

	public static final String INVENTORY_STATUS = "InventoryStatus";

	public static final String ITEM = "Item";

	public static final String ITEM_ID = "ItemID";

	public static final String GLOBAL_ITEM_ID = "GlobalItemID";

	public static final String ITEM_KEY = "ItemKey";

	public static final String PRODUCT_CLASS = "ProductClass";

	public static final String DEFAULT_PRODUCT_CLASS = "DefaultProductClass";

	public static final String PRIMARY_INFORMATION = "PrimaryInformation";

	public static final String PRODUCT_LINE = "ProductLine";

	public static final String UNIT_OF_MEASURE = "UnitOfMeasure";

	public static final String QUANTITY = "Quantity";

	public static final String AVAILABLE_QUANTITY = "AvailableQty";

	public static final String SERIAL_NO = "SerialNo";

	public static final String REASON_CODE = "ReasonCode";

	public static final String DOCUMENT_TYPE = "DocumentType";

	public static final String BAR_CODE_DATA = "BarCodeData";

	public static final String BAR_CODE_TYPE = "BarCodeType";

	public static final String BAR_CODE_TRANSLATION_SOURCE = "BarCodeTranslationSource";

	public static final String TOTAL_NUMBER_OF_RECORDS = "TotalNumberOfRecords";

	public static final String ENTERPRISE_CODE_STR = "EnterpriseCode";

	public static final String ENTERPRISE_KEY = "EnterpriseKey";

	public static final String ORGANIZATION_CODE = "OrganizationCode";

	public static final String SELLER_ORGANIZATION_CODE = "SellerOrganizationCode";

	public static final String INVENTORY_UOM = "InventoryUOM";

	public static final String TRANSACTIONAL_UOM = "TransactionalUOM";

	public static final String SHIP_BY_DATE = "ShipByDate";

	public static final String LOCATION_ID = "LocationId";

	public static final String ORDER_DOCUMENT_TYPE = "0001";

	public static final String INC2INC_TRANSFER_ORDER_DOCUMENT_TYPE = "0008.ex";

	public static final String REFURBISHMENT_ORDER_TYPE = "Refurbishment";

	public static final String API_GET_SERIAL_LIST = "getSerialList";

	public static final String API_ADJUST_LOCN_INV = "adjustLocationInventory";

	public static final String API_CONFIRM_DRAFT_ORDER = "confirmDraftOrder";

	public static final String API_CREATE_ORDER = "createOrder";

	public static final String API_GET_WORK_ORDER_DTLS = "getWorkOrderDetails";

	public static final String API_CHANGE_ORDER_STATUS = "changeOrderStatus";

	public static final String API_CHANGE_ORDER = "changeOrder";

	public static final String API_CONFIRM_WORK_ORDER_ACTIVITY = "confirmWorkOrderActivity";

	public static final String API_CHG_LOCN_INV_ATTR = "changeLocationInventoryAttributes";

	public static final String API_MODIFY_WO = "modifyWorkOrder";

	public static final String API_CHG_WO_STATUS = "changeWorkOrderStatus";

	public static final String API_GET_EXCEPTION_LIST = "getExceptionList";

	public static final String API_GET_EXCEPTION_DETAILS = "getExceptionDetails";

	public static final String API_RESOLVE_EXCEPTION = "resolveException";

	public static final String API_CHANGE_EXCEPTION = "changeException";

	public static final String RFB_CMPL_DROP_STATUS = "1100.0009";

	public static final String RFB_VAS_WO_CMPL_STATUS = "1400";

	public static final String TRANSACTION_REFURB_STATUS_CHANGE = "REFURB_STATUS_CHANGE.0001.ex";

	public static final String NWCG_RFB_MOD_INC_RTN_SERVICE = ResourceUtil
			.get("nwcg.icbs.modifyIncidentRefurbRtn.service");

	public static final String NWCG_RFB_GET_INC_RTN_SERVICE = ResourceUtil
			.get("nwcg.icbs.getIncidentRefurbRtn.service");

	public static final String NWCG_GET_MASTER_WORK_ORDER_DTLS_SERVICE = "NWCGGetMasterWorkOrderDetailsService";

	public static final String RFI_STATUS = "RFI";

	public static final String NRFI_RFB_STATUS = "NRFI-RFB";

	public static final String DISP_UNSERVICE = "UNSERVICE";

	// Added By Gaurav CR-671
	public static final String DISP_MISSING = "MISSING";

	public static final String DESC_MISSING = "Missing";

	public static final String DISP_UNSERVICE_INT = "UNSRV-DISP";

	public static final String DISP_UNSERVICE_NWT = "UNSRV-NWT";

	public static final String DISP_UNSERVICE_NWT_INT = "NWT-DISP";

	public static final String RFB_ADJUSTMENT_REASON_CODE = "REFURB-ADJ";

	public static final String VAS_CONFIRM_WO_TRANSACTION = "CONFIRM_WORK_ORDER";

	// End of constants related to refurb

	// Constants related to Reports
	public static final String NODE_KEY = "NodeKey";

	public static final String ORGANIZATION_KEY = "OrganizationKey";

	public static final String ORGANIZATION_NAME = "OrganizationName";

	public static final String ORGANIZATION = "Organization";

	public static final String UOM = "Uom";

	public static final String FIRST_NAME = "FirstName";

	public static final String MIDDLE_NAME = "MiddleName";

	public static final String LAST_NAME = "LastName";

	public static final String ADDRESS_LINE_1 = "AddressLine1";

	public static final String ADDRESS_LINE_2 = "AddressLine2";

	public static final String ADDRESS_LINE_3 = "AddressLine3";

	public static final String ADDRESS_LINE_4 = "AddressLine4";

	public static final String ADDRESS_LINE_5 = "AddressLine5";

	public static final String ADDRESS_LINE_6 = "AddressLine6";

	public static final String CITY = "City";

	public static final String STATE = "State";

	public static final String ZIP_CODE = "ZipCode";

	public static final String COMPANY = "Company";

	public static final String EMAIL_ID = "EMailID";

	public static final String SHORT_DESCRIPTION = "ShortDescription";

	public static final String ITEM_ORG_CODE = "ItemOrganizationCode";

	public static final String UNIT_WEIGHT = "UnitWeight";

	public static final String UNIT_WEIGHT_UOM = "UnitWeightUOM";

	public static final String UNIT_VOLUME = "UnitVolume";

	public static final String UNIT_VOLUME_UOM = "UnitVolumeUOM";

	public static final String WEIGHT_IN_LBS = "WeightInLbs";

	public static final String WEIGHT_LB_UNITS = "WeightLbUnits";

	public static final String WEIGHT_IN_KGS = "WeightInKg";

	public static final String WEIGHT_KG_UNITS = "WeightKgUnits";

	public static final String VOL_IN_CUB_FT = "VolumeInCubicFeet";

	public static final String VOL_CUB_FT_UNITS = "VolumeCubicFeetUnit";

	public static final String VOL_IN_CUB_MTS = "VolumeInCubicMeter";

	public static final String VOL_CUB_MTS_UNITS = "VolumeCubicMetersUnit";

	public static final String COMPONENT_QUANTITY = "ComponentQuantity";

	public static final String COMPONENT_ITEM_ID = "ComponentItemID";

	public static final String COMPONENT_DESCRIPTION = "ComponentDescription";

	public static final String DATE = "Date";

	public static final String ORDER_NO = "OrderNo";

	public static final String TOTAL_WEIGHT = "TotalWeight";

	public static final String TOTAL_WEIGHT_UOM = "TotalWeightUOM";

	public static final String TOTAL_VOLUME = "TotalVolume";

	public static final String TOTAL_VOLUME_UOM = "TotalVolumeUOM";

	public static final String SHIPMENT_NO = "ShipmentNo";

	public static final String SHIPMENT_ELEMENT = "Shipment";

	public static final String SHIP_NODE = "ShipNode";

	public static final String ORDER_DATE = "OrderDate";

	public static final String IS_HAZMAT = "IsHazmat";

	public static final String ORDER_HEADER_KEY = "OrderHeaderKey";

	public static final String ORDER_LINE_KEY = "OrderLineKey";

	public static final String EXTN_REQUEST_NO = "ExtnRequestNo";

	public static final String ORDER_RELEASE_STATUS_KEY = "OrderReleaseStatusKey";

	public static final String PROPER_SHIPPING_NAME = "ProperShippingName";

	public static final String HAZARD_CLASS = "HazardClass";

	public static final String UNNUMBER = "UNNumber";

	public static final String PACKING_GROUP = "PackingGroup";

	public static final String IATA_PROPER_SHIPPING_NAME = "IATAProperShippingName";

	public static final String IATA_HAZARD_CLASS = "IATAHazardClass";

	public static final String IATA_UNNUMBER = "IATAUNNumber";

	public static final String IATA_PACKING_GROUP = "IATAPackingGroup";

	public static final String ORDERED_QTY = "OrderedQty";

	public static final String ORIGINAL_ORDERED_QTY = "OriginalOrderedQty";

	public static final String BACKORDERED_QTY = "ExtnBackorderedQty";

	public static final String UTF_QTY = "ExtnUTFQty";

	public static final String FWD_QTY = "ExtnFwdQty";

	public static final String ORIGINAL_REQUESTED_QTY = "ExtnOrigReqQty";

	public static final String RELEASE_KEY = "OrderReleaseKey";

	public static final String RELEASE_TS = "ReleaseTS";

	public static final String DAY_PHONE = "DayPhone";

	public static final String MOBILE_PHONE = "MobilePhone";

	public static final String HTTP_URL = "HttpUrl";

	public static final String INCIDENT_NO = "ExtnIncidentNo";

	public static final String INCIDENT_YEAR = "ExtnIncidentYear";

	public static final String INCIDENT_NAME = "IncidentName";

	public static final String INCIDENT_ID = "IncidentId";

	public static final String INCIDENT_NAME_ATTR = "ExtnIncidentName";

	public static final String INCIDENT_TYPE_ATTR = "ExtnIncidentType";

	public static final String INCIDENT_TYPE = "IncidentType";

	public static final String INCIDENT_TEAM_TYPE_ATTR = "ExtnIncidentTeamType";

	public static final String INCIDENT_TEAM_TYPE = "IncidentTeamType";

	public static final String INCIDENT_CACHE_ID = "ExtnIncidentCacheId";

	public static final String INCIDENT_ROSS_BILLING_ORG = "ExtnROSSBillingOrganization";

	public static final String YEAR = "Year";

	public static final String PRINT_DOCUMENT_ID = "PrintDocumentId";

	public static final String PRINTER_ID = "PrinterId";

	public static final String YFC_NODE_NUMBER = "YFC_NODE_NUMBER";

	public static final String API_GET_ORDER_RELEASE_DETAILS = "getOrderReleaseDetails";

	public static final String API_GET_ORDER_DETAILS = "getOrderDetails";

	public static final String API_GET_ORDER_LIST = "getOrderList";

	public static final String API_PRINT_DOCUMENT_SET = "printDocumentSet";

	public static final String SERVICE_XML_SORTING = "NWCGXMLSorting";

	public static final String API_GET_ORG_LIST = "getOrganizationList";

	public static final String API_GET_ITEM_DETAILS = "getItemDetails";

	public static final String API_MODIFY_ITEM = "modifyItem";

	public static final String API_GET_SHIPMENT_LIST_FOR_ORDER = "getShipmentListForOrder";

	public static final String API_GET_ORDER_LINE_LIST = "getOrderLineList";

	public static final String API_GET_ORDER_LINE_DETAILS = "getOrderLineDetails";

	public static final String API_GET_TASK_DETAILS = "getTaskDetails";

	public static final String API_GET_PRINTER = "getPrinter";

	public static final String API_GET_ITEM_LIST = "getItemList";

	public static final String API_GET_LOCATION_LIST = "getLocationList";

	public static final String API_GET_LOCATION_DETAILS = "getLocationDetails";

	// End of constants related to Reports

	// General Constants
	public static final String YES = "Y";

	public static final String NO = "N";

	public static final String END_DATE = "EndDate";

	// Adjust Location Inventory Constants
	public static final String SEQ_YFS_ORDER_NO = "SEQ_YFS_ORDER_NO";

	// -------------------Returns Module Services --------------------
	// db -extn
	public static final String NWCG_INSERT_REC_IN_INCIDENT_TABLE_SERVICE = ResourceUtil
			.get("nwcg.icbs.createrecordinincidenttable.service");

	public static final String NWCG_MODIFY_REC_IN_INCIDENT_TABLE_SERVICE = ResourceUtil
			.get("nwcg.icbs.modifyrecordinincidentreturntable.service");

	public static final String NWCG_QUERY_DETAILS_ON_INCIDENT_TABLE_SERVICE = ResourceUtil
			.get("nwcg.icbs.queryrecordinincidentreturntable.service");

	public static final String NWCG_QUERY_LIST_ON_INCIDENT_TABLE_SERVICE = ResourceUtil
			.get("nwcg.icbs.getthelistofincidentreturnsinthetable.service");

	// QC 1148
	public static final String NWCG_UPDATE_INC_RETURN_ON_ADJUST_RECEIPT_SERVICE = ResourceUtil
			.get("nwcg.icbs.updateIncidentReturnOnAdjustReceipt.service");

	// custom - VIRTUAL RETURN - for reporting
	public static final String NWCG_INSERT_REC_ON_RECEIPT_SERVICE = ResourceUtil
			.get("nwcg.icbs.insertnewrecordthroughapitoincidenttableonshipment.service");

	public static final String NWCG_UPDATE_REC_ON_RECEIPT_SERVICE = ResourceUtil
			.get("nwcg.icbs.receivereturnandupdateincidentreturntable.service");

	// custom - functional - ACTUAL RETURN
	public static final String NWCG_PERFORM_ACTUAL_RETURN_SERVICE = ResourceUtil
			.get("nwcg.icbs.performreturninbackend.service");

	// custom - for ajax calls
	public static final String NWCG_CHECK_IS_SERIAL_UNIQUE_AND_OUT_SERVICE = ResourceUtil
			.get("nwcg.icbs.isserialuniqueacrossnodes.service");

	public static final String NWCG_GET_INCIDENTNAME_FOR_ISSUE_SERVICE = ResourceUtil
			.get("nwcg.icbs.callgetorderdetailstogetissueincidentname.service");

	public static final String NWCG_GET_ITEM_AND_COMP_DETAILS_SERVICE = ResourceUtil
			.get("nwcg.icbs.getitemandcomponentdetials.service");

	public static final String NWCG_GET_ITEM_LIST_OOB_SERVICE = ResourceUtil
			.get("nwcg.icbs.callgetitemlistoobservice");

	// For Alert
	public static final String NWCG_RETURN_QUEUEID = "DEFAULT";

	public static final String NWCG_RETURN_INBOXTYPE = "DEFAULT";

	public static final String NWCG_RETURN_ALERTTYPE = "AlertType";

	public static final String NWCG_RETURN_BLIND_RECEIPT_DOCUMENT = "0010";

	public static final String NWCG_RETURN_SELLER_ORG = "NWCG";

	public static final String NWCG_BILL_TRANS_QUEUEID = "NWCG_BILLING_TRANSACTION";

	public static final String NWCG_BILL_TRANS_INBOXTYPE = "NWCG_BILLING_TRANSACTION";

	public static final String NWCG_BILL_TRANS_ALERTTYPE = "AlertType";

	public static final String NWCG_INCIDENT_RETURN_ERROR = "NWCG_INCIDENT_RETURN_ERROR"; // QC1148

	// ----------------End Returns Module Services --------------------

	// ----Start For Reservation Module-----
	public static final String NWCG_RESERVATION_NORMAL_ISSUE_DOC = "0001";

	public static final String NWCG_RESERVATION_OTHER_ISSUE_DOC = "0007.ex";

	public static final String NWCG_CREATE_NORMAL_ISSUE = ResourceUtil
			.get("nwcg.icbs.createissueusingres.service");

	public static final String NWCG_CREATE_OTHER_ISSUE = ResourceUtil
			.get("nwcg.icbs.createotherissueusingres.service");

	public static final String NWCG_GET_RESERVED_ITEMS = ResourceUtil
			.get("nwcg.icbs.getereserveditems.service");

	// -- End reservation module -----------

	// CREATE_COUNT_TASKS
	public static final String TRIGGER_AGENT_CRITERIA = ResourceUtil
			.get("nwcg.icbs.triggeragent.criteria");

	public static final String ORDER_STATUS_INCLUDED_IN_SHIPMENT = "Included In Shipment";

	public static final String NWCG_ORGANIZATION_ROLE_KEY_SELLER = "SELLER";

	public static final String NWCG_CUSTOMER_TYPE_OTHERS = "02";

	// For Items
	public static final String NWCG_ITEM_DEFAULT_PRODUCT_CLASS = "Supply";

	public static final String NWCG_PRICE_PROGRAM_NAME = "NWCG_PRICE_PROGRAM";

	public static final String NWCG_ITEM_CHANGE_ERROR = "You can not make multiple status changes to an item at the same time.  Please make one change at a time.";

	public static final String NWCG_PROCESS_CREATE_ITEM_CATALOG_SERVICE = "NWCGProcessCreateItemCatalogService";

	public static final String NWCG_PROCESS_DELETE_ITEM_CATALOG_SERVICE = "NWCGProcessDeleteItemCatalogService";

	public static final String NWCG_PROCESS_MODIFY_ITEM_CATALOG_SERVICE = "NWCGProcessModifyItemCatalogService";

	// changes for refurb
	public static final String NWCG_INCIDENT_RETURN = "Incident Return";

	public static final String NWCG_SERVICE_ITEM_ID_REFURBISHMENT = "REFURBISHMENT";

	public static final String NWCG_CREATE_MASTER_WORK_ORDER_SERVICE_NAME = ResourceUtil
			.get("nwcg.icbs.createmasterworkorder.service");

	public static final String NWCG_CREATE_MASTER_WORK_ORDER_LINE_SERVICE_NAME = ResourceUtil
			.get("nwcg.icbs.createmasterworkorderline.service");

	public static final String NWCG_REFURB_MWOL_INITIAL_STATUS = "Awaiting Work Order Creation";

	public static final String NWCG_REFURB_MWOL_INTERMEDTIATE_STATUS = "Work Order Partially Completed";

	public static final String NWCG_REFURB_MWOL_FINAL_STATUS = "Work Order Completed";

	// end
	public static final String API_GET_CUSTOMER_LIST = "getCustomerList";

	public static final String TEMPLATE_GET_CUSTOMER_LIST = "<Customer CanConsumeSupplementalCapacity=\"\" CustomerID=\"\" CustomerKey=\"\" CustomerType=\"\" OrganizationCode=\"\" SlotPreferenceType=\"\"><Extn ExtnActiveFlag=\"\" ExtnAgency=\"\" ExtnCustomerName=\"\" ExtnCustomerType=\"\" ExtnDepartment=\"\" ExtnGACC=\"\" ExtnUnitType=\"\" /><Consumer BillingAddressKey=\"\"><BillingPersonInfo /></Consumer></Customer>";

	public static final String ALERT_STRING = "Alert generated because customer id does not exist on ICBS system";

	public static final String XPATH_PERSON_BILLING_INFO = "/CustomerList/Customer/Consumer/BillingPersonInfo";

	public static final String NWCG_ITEM_STATUS_BACKORDERED = "9100.020";

	public static final String NWCG_ITEM_STATUS_REFURBISH_COMPLETE = "1100.0009";

	public static final String NWCG_ITEM_STATUS_FROM = "1000";

	public static final String NWCG_ITEM_STATUS_TO = "3690";

	public static final String NWCG_UNPUBLISHED_STATUS = "2000";

	public static final String NWCG_PUBLISHED_STATUS = "3000";

	public static final String NWCG_PUBLISH_TO_ROSS_N = "N";

	// SOAP Additions
	public static final String NWCG_CREATE_ITEM_ALERT_QUEUE = "NWCG_OB_RESPONSE";

	public static final String NWCG_CREATE_EXCEPTION = "createException";

	public Object LDAP_SECURITY_PROTOCOL_SSL = "ssl";

	public Object LDAP_VERSION = "3";

	public static final String NWCG_MULTIPLE_STATUS_CHANGE_ERROR = "You can not make multiple status changes to an item at the same time.  Please make one change at a time.";

	public static final String NWCG_CURRENT_PENDING_SUPPLY = "You can not unpublish this item as there are open orders containing this item.";

	public static final String INCIDENT_KEY = "IncidentKey";

	// XML constants
	public static final String NWCG_INCIDENT_ORDER = "NWCGIncidentOrder";

	// Constants for Response status condition
	public static final String RESPONSE_STATUS = "ResponseStatus";

	public static final String REGISTER_INCIDENT_INTEREST_RESP = "RegisterIncidentInterestResp";

	public static final String RETURN_CODE = "ReturnCode";

	// Constants for Notifications
	public static final String NOTIF_ERR_PRIM_FIN_CODE_NOT_PASSED_1 = "IncidentFinancialCodes element is not passed in Update Incident Notification input. This notification is not processed";

	public static final String NOTIF_ERR_PRIM_FIN_CODE_NOT_PASSED_2 = "Primary Financial Code is not passed in Update Incident Notification input. This notification is not processed";

	public static final String NWCG_INCIDENT_FAILURE = "NWCG_INCIDENT_FAILURE";

	public static final String NWCG_INCIDENT_SUCCESS = "NWCG_INCIDENT_SUCCESS";

	public static final String NWCG_INCIDENT_RADIOS_SUCCESS = "NWCG_INCIDENT_RADIOS_SUCCESS";

	public static final String NWCG_INCIDENT_RADIOS_FAILURE = "NWCG_INCIDENT_RADIOS_FAILURE";

	public static final String SVC_UPDT_INCIDENT_NOTIF_XSL_SVC = "NWCGUpdateIncidentNotificationXSLService";

	public static final String SVC_UPDT_INCIDENT_NOTIF_XSL_V2_SVC = "NWCGUpdateIncidentNotificationNoNSXSLService";

	public static final String SVC_CHG_INCIDENT_ORDER_SVC = "NWCGChangeIncidentOrderService";

	public static final String SVC_GET_INCIDENT_ORDER_SVC = "NWCGGetIncidentOrderService";

	public static final String SVC_GET_INCIDENT_ORDER_LIST_SVC = "NWCGGetIncidentOrderListService";

	public static final String SVC_GET_INCIDENT_ORDER_DTLS_ONLY_SVC = "NWCGGetIncidentOrderOnlyService";

	public static final String SVC_TRANSFER_INCIDENT_ORDER_SVC = "NWCGUpdateIncidentOrderService";

	public static final String SVC_CREATE_INCIDENT_ORDER_SVC = "NWCGCreateIncidentOrderService";

	public static final String SVC_UPDT_INC_KEY_SVC = "NWCGUpdateIncidentOrderService";

	public static final String TMPL_GET_CUST_DETAILS_TEMPLATE = "NWCGUpdateIncNotification_getCustomerDetails";

	public static final String API_GET_CUST_DETAILS = "getCustomerDetails";

	public static final String SVC_GET_ROSS_ACCT_CODES_LIST_SVC = "NWCGGetROSSAccountCodesListService";

	public static final String CUST_EXTN_ELEMENT = "Extn";

	public static final String CUST_CONSUMER_ELEMENT = "Consumer";

	public static final String CUST_CUSTOMER_TYPE = "ExtnCustomerType";

	public static final String CUST_UNIT_TYPE_ATTR = "ExtnUnitType";

	public static final String CUST_AGENCY_ATTR = "ExtnAgency";

	public static final String CUST_DEPARTMENT_ATTR = "ExtnDepartment";

	public static final String CUST_GACC_ATTR = "ExtnGACC";

	public static final String CUST_CUSTOMER_NAME_ATTR = "ExtnCustomerName";

	public static final String CUST_CUST_ELEMENT = "Customer";

	public static final String CUST_CUST_ID_ATTR = "CustomerID";

	public static final String CUST_ORG_CODE_ATTR = "OrganizationCode";

	public static final String BILLTO_ID_ATTR = "BillToID";

	public static final String INCIDENT_UNIT_TYPE_ATTR = "UnitType";

	public static final String INCIDENT_AGENCY_ATTR = "Agency";

	public static final String INCIDENT_DEPARTMENT_ATTR = "Department";

	public static final String INCIDENT_GACC_ATTR = "GACC";

	public static final String INCIDENT_CUSTOMER_NAME_ATTR = "CustomerName";

	public static final String INCIDENT_CUSTOMER_TYPE = "CustomerType";

	public static final String INCIDENT_YFS_PERSON_INFO_SHIPTO_ELEM = "YFSPersonInfoShipTo";

	public static final String INCIDENT_ADDR_LINE_1_ATTR = "AddressLine1";

	public static final String INCIDENT_ADDR_LINE_2_ATTR = "AddressLine2";

	public static final String INCIDENT_CITY_ATTR = "City";

	public static final String INCIDENT_STATE_ATTR = "State";

	public static final String INCIDENT_ZIPCODE_ATTR = "ZipCode";

	public static final String INCIDENT_COUNTRY_ATTR = "Country";

	public static final String CUST_COUNTRY_ATTR = "CountryCode";

	public static final String INC_REQ_NO_BLOCK_START = "RequestNoBlockStart";

	public static final String INC_REQ_NO_BLOCK_END = "RequestNoBlockEnd";

	public static final String INCIDENT_BLM_ACCT_CODE = BILL_TRANS_INCIDENT_BLM_ACCT_CODE;

	public static final String INCIDENT_BLM_ACCT_CODE_QRY_TYPE = "IncidentBlmAcctCodeQryType";

	public static final String INCIDENT_FS_ACCT_CODE = BILL_TRANS_INCIDENT_FS_ACCT_CODE;

	public static final String INCIDENT_FS_OVERRIDE_CODE = BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE;

	public static final String INCIDENT_OTHER_ACCT_CODE = BILL_TRANS_INCIDENT_OTHER_ACCT_CODE;

	public static final String INCIDENT_OVERRIDECODE_ATTR = "OverrideCode";

	public static final String INCIDENT_MERGETO_INCIDENT_YEAR = "MergeToIncidentYear";

	public static final String INCIDENT_MERGETO_INCIDENT_NO = "MergeToIncidentNo";

	public static final String INCIDENT_SOURCE = "IncidentSource";

	public static final String INC_NOTIF_FINANCIAL_CODES_ELEMENT = "IncidentFinancialCodes";

	public static final String INC_NOTIF_PRIMARYIND_NODE = "PrimaryInd";

	public static final String INC_NOTIF_INCKEY_ATTR = "IncidentKey";

	public static final String INC_NOTIF_INCID_ATTR = "IncidentID";

	public static final String INC_NOTIF_ENTID_ATTR = "EntityID";

	public static final String INC_NOTIF_NAT_INCKEY_ATTR = "NaturalIncidentKey";

	public static final String INC_NOTIF_HOSTID_ATTR = "HostID";

	public static final String INC_NOTIF_UNIT_ID_PREFIX_ATTR = "UnitIDPrefix";

	public static final String INC_NOTIF_UNIT_ID_SUFFIX_ATTR = "UnitIDSuffix";

	public static final String INC_NOTIF_SEQ_NO_ATTR = "SequenceNumber";

	public static final String INC_NOTIF_YR_CREATED_ATTR = "YearCreated";

	public static final String INC_NOTIF_BILLINGORG_ELEMENT = "BillingOrganization";

	public static final String INC_NOTIF_CACHEORG_ELEMENT = "CacheOrganization";

	public static final String INC_NOTIF_DIST_ID_ATTR = "distributionID";

	public static final String INC_NOTIF_FROM_UNITID_ELEMENT = "FromUnitID";

	public static final String INC_NOTIF_TO_UNITID_ELEMENT = "ToUnitID";

	public static final String INC_NOTIF_MSGNAME_ATTR = "messageName";

	public static final String INC_NOTIF_TIME_OF_NOTIF_ELEMENT = "TimeOfNotification";

	public static final String STR_TRUE = "TRUE";

	public static final String EMPTY_STRING = "";

	public static final String ZERO_STRING = "0";

	public static final String INCIDENT_ORDER_ELEM = "NWCGIncidentOrder";

	public static final String INCIDENT_NO_ATTR = "IncidentNo";

	public static final String INCIDENT_ID_ATTR = "IncidentId";

	public static final String YEAR_ATTR = "Year";

	public static final String ROSS_DISPATCH_ID = "ROSSDispatchID";

	public static final String MODIFICATION_CODE = "ModificationCode";

	public static final String MODIFICATION_DESC = "ModificationDesc";

	public static final String IS_ACTIVE = "IsActive";

	public static final String INCIDENT_LOCKED = "IncidentLocked";

	public static final String PRIMARY_CACHE_ID = "PrimaryCacheId";

	public static final String REPLACED_INCIDENT_NO = "ReplacedIncidentNo";

	public static final String REPLACED_INCIDENT_YEAR = "ReplacedIncidentYear";

	public static final String REPLACED_INCIDENT_NO_2 = "ReplacedIncidentNo2";

	public static final String REPLACED_INCIDENT_YEAR_2 = "ReplacedIncidentYear2";

	public static final String REPLACED_INCIDENT_NO_3 = "ReplacedIncidentNo3";

	public static final String REPLACED_INCIDENT_YEAR_3 = "ReplacedIncidentYear3";

	public static final String LAST_UPDATED_FROM_ROSS = "LastUpdatedFromROSS";

	public static final String PRIMARY_INDICATOR = "PrimaryIndicator";

	public static final String NWCG_ROSS_ACCOUNT_CODES_ELM = "NWCGRossAccountCodes";

	public static final String MOD_CODE_ROSS_VAL = "AUTOUPDATE-ROSS";

	public static final String INCIDENT_REG_INT_IN_ROSS_ATTR = "RegisterInterestInROSS";

	public static final String ALERT_INCIDENT_NO = "Incident No";

	public static final String ALERT_YEAR = "Incident Year";

	public static final String ALERT_CUST_ID = "Customer ID";

	public static final String ALERT_DESC = "Description";

	public static final String ALERT_TYPE = "Alert Type";

	public static final String ALERT_INPUT_DOC = "Input Document";

	public static final String ALERT_DIST_ID = "Distribution ID";

	public static final String ALERT_SOAP_MESSAGE = "SOAP-MESSAGE";

	public static final String ALERT_FROM_DISP_UNIT_ID = "Dispatch Unit ID - From";

	public static final String ALERT_TO_DISP_UNIT_ID = "Dispatch Unit ID - To";

	public static final String ALERT_SHIPNODE_KEY = "CACHEID";

	public static final String ALERT_OLD_INCIDENT_NO = "Old Incident No";

	public static final String ALERT_OLD_INCIDENT_YEAR = "Old Incident Year";

	public static final String ALERT_NEW_INCIDENT_NO = "New Incident No";

	public static final String ALERT_NEW_INCIDENT_YEAR = "New Incident Year";

	public static final String PRIMARY_CACHEID_FOR_INCIDENT = "Primary CacheID";

	public static final String ALERT_REQ_CREATED_BY = "Request Sent By";

	// Inbox Related attribute and node names
	public static final String INBOX = "Inbox";

	public static final String INBOX_KEY = "InboxKey";

	public static final String INBOX_LIST = "InboxList";
	// CR 1051 - Extract time out
	public static final String INBOX_TYPE = "InboxType";

	public static final String SHIPNODE_KEY = "ShipnodeKey";

	public static final String INBOX_ADDNL_DATA = "InboxAddnlData";

	public static final String INBOX_REFERENCES_LIST = "InboxReferencesList";

	public static final String INBOX_REFERENCES = "InboxReferences";

	public static final String QUEUE = "Queue";

	public static final String QUEUE_DESC = "QueueDescription";

	public static final String EXCEPTION_TYPE = "ExceptionType";

	public static final String QUEUE_ID = "QueueId";

	public static final String QUEUE_KEY = "QueueKey";

	public static final String NAME_ATTR = "Name";

	public static final String VALUE_ATTR = "Value";

	public static final String DETAIL_DESCRIPTION = "DetailDescription";

	public static final String REFERENCE_TYPE_URL = "URL";

	public static final String REFERENCE_TYPE_COMMENT = "COMMENT";

	public static final String REFERENCE_TYPE = "ReferenceType";

	public static final String RESOLUTION_DETAILS = "ResolutionDetails";

	public static final String ERROR_DESC = "ErrorDesc";

	// Merge Related Constants
	public static final String ALERT_SRC_INCIDENT_NO = "Source Incident No";

	public static final String ALERT_SRC_INCIDENT_YEAR = "Source Incident Year";

	public static final String ALERT_DEST_INCIDENT_NO = "Destination Incident No";

	public static final String ALERT_DEST_INCIDENT_YEAR = "Destination Incident Year";

	// End of Merge Related Constants

	public static final String SVC_TRANSFORM_FROM_INC_TO_TRANSFER_ISSUE_XSL = "NWCGTransformFromIncDtlsToTxIssueService";

	// Used by NWCGPlaceResourceRequestExternalHandler.java
	public static final String GET_ITEM_LIST_OP_FILENAME = "NWCGPlaceResourceRequestExternalHandler_getItemList.xml";

	public static final String NFES_SUPPLY_PREFIX_LTR = "S";

	public static final String NFES_EQUIP_PREFIX_LTR = "E";

	public static final String DASH = "-";

	public static final String NFES_SUPPLY_FULL_PFX = "S-";

	public static final String NFES_EQUIP_FULL_PFX = "E-";

	public static final String EXTN_ELEMENT = "Extn";

	public static final String SHIP_CONTACT_NAME_ATTR = "ExtnShippingContactName";

	public static final String SHIP_CONTACT_NAME_INPUT_RQST_ATTR = "ShippingContactName";

	public static final String SHIP_CONTACT_PHONE_INPUT_RQST_ATTR = "ShippingContactPhone";

	public static final String SHIP_CONTACT_PHONE_ATTR = "ExtnShippingContactPhone";

	public static final String EXTN_ROSS_FIN_CODE_ATTR = "ExtnROSSFinancialCode";

	public static final String EXTN_ROSS_DISPATCH_UNIT_ID = "ExtnRossDispatchUnitId";

	public static final String ROSS_FINANCIAL_CODE = "ROSSFinancialCode";

	public static final String EXTN_ROSS_FISCAL_YEAR = "ExtnROSSFiscalYear";

	public static final String EXTN_ROSS_OWNING_AGENCY = "ExtnROSSOwningAgency";

	public static final String EXTN_ROSS_RESOURCE_ITEM = "ExtnRossResourceItem";

	public static final String EXTN_PUBLISH_TO_ROSS = "ExtnPublishToRoss";

	public static final String REQ_DELIVERY_DATE_ATTR = "ReqDeliveryDate";

	public static final String REQ_SHIP_DATE_ATTR = "ReqShipDate";

	public static final String REQ_CANCEL_DATE_ATTR = "ReqCancelDate";

	public static final String EXTN_REQUIRED_DELIVERY_DATE = "ExtnRequiredDeliveryDate";

	public static final String EXTN_WILL_PICK_UP_NAME = "ExtnWillPickUpName";

	public static final String EXTN_WILL_PICK_UP_INFO = "ExtnWillPickUpInfo";

	public static final String WILL_PICK_UP_INFO_INPUT_RQST_ATTR = "WillPickUpInfo";

	public static final String WILL_PICK_UP_NAME_INPUT_RQST_ATTR = "WillPickUpName";

	public static final String EXTN_REQ_DELIVERY_DATE_ATTR = "ExtnReqDeliveryDate";

	public static final String EXTN_NAV_INFO_ATTR = "ExtnNavInfo";

	public static final String ACTUAL_DATE = "ActualDate";

	public static final String EXPECTED_DATE = "ExpectedDate";

	public static final String ORDER_LINES = "OrderLines";

	public static final String ORDER_LINE = "OrderLine";

	public static final String ORDER_TYPE = "OrderType";

	public static final String DRAFT_ORDER_FLAG = "DraftOrderFlag";

	public static final String ORDER_TYPE_NORMAL = "Normal";

	public static final String ORDER_TYPE_REPLACEMENT = "Replacement";

	public static final String PERSON_INFO_SHIPTO = "PersonInfoShipTo";

	public static final String PERSON_INFO_BILLTO = "PersonInfoBillTo";

	public static final String PERSON_INFO_PERSONID = "PersonID";

	public static final String EXTN_BLM_ACCT_CODE = "ExtnBlmAcctCode";

	public static final String EXTN_FS_ACCT_CODE = "ExtnFsAcctCode";

	public static final String EXTN_OTHER_ACCT_CODE = "ExtnOtherAcctCode";

	public static final String EXTN_OVERRIDE_CODE = "ExtnOverrideCode";

	public static final String EXTN_SA_OVERRIDE_CODE = "ExtnSAOverrideCode";

	public static final String EXTN_SHIP_ACCT_CODE = "ExtnShipAcctCode";

	public static final String EXTN_ALT_EMAIL_ID = "AlternateEmailID";

	public static final String NWCG_HOLD_TYPE_1_INCIDENT_INACTIVE = "INCIDENT_INACTIVE";

	public static final String NWCG_HOLD_TYPE_2_NEW_INC_CREATED = "NEW_INC_BY_ICBSR";

	public static final String NWCG_HOLD_TYPE_3_SHIPPING_INFO_MISSING = "SHIP_INFO_MISSIN";

	public static final String NWCG_HOLD_TYPE_4_INC_NO_ICBS_CODES = "INC_NO_ICBS_CODES";

	public static final String NWCG_MSG_CODE_914_I_1 = "NWCG_914_I_001";

	public static final String NWCG_MSG_CODE_914_E_1 = "NWCG_914_E_001";

	public static final String NWCG_MSG_CODE_914_E_2 = "NWCG_914_E_002";

	public static final String NWCG_MSG_CODE_914_E_3 = "NWCG_914_E_003";

	public static final String NWCG_MSG_CODE_914_E_4 = "NWCG_914_E_004";

	public static final String NWCG_MSG_CODE_914_E_5 = "NWCG_914_E_005";

	public static final String NWCG_MSG_CODE_914_E_6 = "NWCG_914_E_006";

	public static final String NWCG_MSG_CODE_914_E_7 = "NWCG_914_E_007";

	public static final String NWCG_MSG_CODE_914_E_8 = "NWCG_914_E_008";

	public static final String NWCG_MSG_CODE_914_E_9 = "NWCG_914_E_009";

	public static final String NWCG_MSG_CODE_914_W_1 = "NWCG_914_W_000001";

	public static final String NWCG_MSG_CODE_914_W_2 = "NWCG_914_W_000002";

	public static final String SCAC_AND_SERVICE = "ScacAndService";

	public static final String SCAC_AND_SERVICE_KEY = "ScacAndServiceKey";

	public static final String ORDER_HOLD_TYPES = "OrderHoldTypes";

	public static final String ORDER_HOLD_TYPE = "OrderHoldType";

	public static final String HOLD_TYPE_ATTR = "HoldType";

	public static final String STATUS_ATTR = "Status";

	public static final String HOLD_TYPE_RESOLVED_STATUS = "1300";

	public static final String HOLD_TYPE_REASON_TEXT_ATTR = "ReasonText";

	public static final String FBMS_WBS_EXTN_ATTR = "ExtnWBS";

	public static final String FBMS_COSTCENTER_EXTN_ATTR = "ExtnCostCenter";

	public static final String FBMS_FUNCTIONALAREA_EXTN_ATTR = "ExtnFunctionalArea";

	// Used by Resource Reassignment IB Notification Handler class
	public static final String RESOURCE_REASSIGN_NOTIFICATION = "ResourceRequestReassignNotification";

	public static final String REASSIGNTO_INCIDENT_LOCALNAME = "ReassignToIncident";

	public static final String REASSIGNTO_REQUEST_LOCALNAME = "ReassignToRequest";

	public static final String REASSIGNTO_INCIDENT_NODENAME = "ron:ReassignToIncident";

	public static final String REASSIGNTO_REQUEST_NODENAME = "ron:ReassignToRequest";

	public static final String INCIDENT_NODENAME = "ron:Incident";

	public static final String CATALOG_ID_ELM = "CatalogID";

	public static final String PHONE_NO_ATTR = "PhoneNo";

	public static final String CACHE_ID_ATTR = "CacheID";

	public static final String EXTN_INCIDENT_NO = "ExtnIncidentNo";

	public static final String EXTN_INCIDENT_YEAR = "ExtnIncidentYear";

	public static final String EXTN_TO_INCIDENT_NO = "ExtnToIncidentNo";

	public static final String EXTN_TO_INCIDENT_YR = "ExtnToIncidentYear";

	public static final String EXTN_TO_INCIDENT_NAME = "ExtnToIncidentName";

	public static final String EXTN_TO_INCIDENT_PHONE_NO = "ExtnToPhoneNo";

	public static final String EXTN_INCIDENT_PHONE_NO = "ExtnPhoneNo";

	public static final String EXTN_TO_INCIDENT_TYPE = "ExtnToIncidentType";

	public static final String EXTN_TO_OVERRIDE_CODE = "ExtnToOverrideCode";

	public static final String EXTN_TO_INCIDENT_BLM_CODE = "ExtnToBlmAcctCode";

	public static final String EXTN_TO_FS_ACCT_CODE = "ExtnToFsAcctCode";

	public static final String EXTN_TO_OTHER_ACCT_CODE = "ExtnToOtherAcctCode";

	public static final String EXTN_TO_INCIDENT_CACHE_ID = "ExtnToIncidentCacheId";

	public static final String TO_INCIDENTKEY = "TO_INCIDENTKEY";

	public static final String FROM_INCIDENTKEY = "FROM_INCIDENTKEY";

	public static final String FROM_REQUEST_NO = "From Request Number";

	public static final String TO_REQUEST_NO = "To Request Number ";

	public static final String PRIMARY_CACHE_ID_ALERT_NAME = "Primary Cache Id";

	public static final String REQUEST_NO_ATTR = "RequestNo";

	public static final String SHIPPING_ADDRESS_ELM = "ShippingAddress";

	public static final String NEED_DATETIME_ELM = "NeedDateTime";

	public static final String REPLACEMENT_IND_ELM = "ReplacementInd";

	public static final String SPECIAL_NEEDS_ELM = "SpecialNeeds";

	public static final String SEQUENCE_NUMBER_ELM = "SequenceNumber";

	public static final String STATUS_INCIDENT_NO = "StatusIncidentNo";

	public static final String STATUS_INCIDENT_YR = "StatusIncidentYear";

	public static final String SERIAL_STATUS = "SerialStatus";

	public static final String SERIAL_STATUS_DESC = "SerialStatusDesc";

	public static final String EXTN_TRACKABLE_ID = "ExtnTrackableId";

	public static final String INC2INC_ORDER_STATUS_CONFIRMED_TXT = "Incident Transfer Completed";

	public static final String INC2INC_ORDER_STATUS_CONFIRMED = "1100.0001";

	public static final String SHIPPED_ORDER_STATUS = "Shipped";

	public static final String AA_RR_INC_NOTIF_ERROR_001 = "A&A_RR_INC_NOTIF_001";

	public static final String AA_RR_INC_NOTIF_ERROR_002 = "A&A_RR_INC_NOTIF_002";

	public static final String AA_RR_INC_NOTIF_ERROR_003 = "A&A_RR_INC_NOTIF_003";

	public static final String AA_RR_INC_NOTIF_ERROR_004 = "A&A_RR_INC_NOTIF_004";

	public static final String AA_RR_INC_NOTIF_ERROR_005 = "A&A_RR_INC_NOTIF_005";

	public static final String AA_RR_INC_NOTIF_ERROR_006 = "A&A_RR_INC_NOTIF_006";

	public static final String AA_RR_INC_NOTIF_ERROR_007 = "A&A_RR_INC_NOTIF_007";

	// Used by NWCGRetrieveResourceRequestHandler.java
	public static final String MULTI_API_ELM = "MultiApi";

	public static final String API_ELM = "API";

	public static final String INPUT_ELM = "Input";

	public static final String ORDER_ELM = "Order";

	public static final String MULTI_API_API = "multiApi";

	public static final String ACTION = "Action";

	public static final String REMOVE = "REMOVE";

	public static final String MODIFY = "MODIFY";

	public static final String CANCEL = "CANCEL";

	public static final String OVERRIDE = "Override";

	public static final String MAX_LINE_STATUS = "MaxLineStatus";

	public static final String MAX_ORDER_STATUS = "MaxOrderStatus";

	public static final String ORDER_STATUSES_ELM = "OrderStatuses";

	public static final String CONDITION_VAR1 = "ConditionVariable1";

	public static final String CONDITION_VAR2 = "ConditionVariable2";

	public static final String GET_ORDER_LINE_LIST_OP_TEMPLATE = "NWCGRetrieveResourceRequestHandler_getOrderLineList";

	public static final String GET_ORDER_LINE_LIST_OP_TEMPLATE_RR = "NWCGResourceReassignmentHandler_getOrderLineList";

	public static final String CONDVAR2_XLD_REASON_RETRV = "XLD-Retrieval";

	public static final String CONDVAR2_XLD_REASON_SUB = "XLD-Substituted";

	public static final String CONDVAR2_XLD_REASON_CONS = "XLD-Consolidated";

	public static final String CONDVAR2_XLD_REASON_CONS_NOPROCESSING = "No-Cancellation";

	public static final String CONDVAR2_XLD_REASON_CONS_SUBSTRING = "XLD";

	// Used by the NWCGRetrieveResourceRequestHandler to limit the number of
	// changeOrderAPI calls made within one multiApi
	public static int NWCG_RETRIEVE_MAX_APIS_PER_MULTIAPI = 50;

	// Used as a last resort for a default number of maximum number
	// of lines to display per Issue
	public static final String MAX_DISPLAY_LINES = "50";

	// Used by NWCGCloseIssueAlertOnReleaseSuccess.java
	public static final String EXTN_SYSTEM_OF_ORIGIN = "ExtnSystemOfOrigin";

	public static final String ROSS_SYSTEM = "ROSS";

	public static final String ICBS_SYSTEM = "ICBS";

	public static final String INBOX_REFERENCES_ELM = "InboxReferences";

	public static final String INBOX_REFERENCE_TYPE = "ReferenceType";

	public static final String INBOX_REFERENCE_VALUE = "Value";

	public static final String INBOX_REFERENCE_URL_TYPE = REFERENCE_TYPE_URL;

	public static final String AUTO_RESOLVED_FLAG = "AutoResolvedFlag";

	public static final String RESOLVED_BY = "ResolvedBy";

	public static final String IGNORE_HOOK_ERRORS = "IgnoreHookErrors";

	// Used by NWCGRetrieveResourceRequestHandler for UC 919 / BR2
	public static final String NWCG_ISSUE_RETRIEVED_STATUS = "9000.050";

	public static final String NWCG_MSG_CODE_919_E_1 = "919-E-000001";

	public static final String NWCG_MSG_CODE_919_E_2 = "919-E-000002";

	public static final String NWCG_MSG_CODE_919_E_3 = "919-E-000003";

	public static final String NWCG_MSG_CODE_919_E_4 = "919-E-000004";

	public static final String NWCG_MSG_CODE_919_I_1 = "919-I-000001";

	public static final String NWCG_MSG_CODE_919_W_1 = "919-W-000001";

	public static final String NWCG_MSG_CODE_919_W_2 = "919-W-000002";

	// YFS_ORDER_HEADER.EXTN_NAV_INFO possible values
	public static final String SHIPPING_ADDRESS = "SHIP_ADDRESS";

	public static final String WILL_PICK_UP = "WILL_PICK_UP";

	public static final String SHIPPING_INSTRUCTIONS = "NAV_INST";

	public static final String ROSS_ITEM_PUBLISH_FAILED = "Unable to publish an item to ROSS";

	public static final String INC_CUST_ID_ATTR = "CustomerId";

	public static final String TMPL_CREATE_INC_GET_CUST_DTLS = "NWCGCreateIncident_getCustomerDetails";

	public static final String SVC_UPDT_INC_ORDER_SVC = "NWCGUpdateIncidentOrderService";

	public static final String INCIDENT_LOCK_REASON_ATTR = "LockReason";

	// Constants for ROSS Responses
	public static final String SVC_GET_OB_MESSAGE_LIST_SERVICE = "NWCGGetOBMessageListService";

	public static final String DIST_ID_ATTR = "DistributionID";

	// Alert related constants
	public static final String MSG_CREATE_REQUEST_AND_PLACE_RESP = "CreateRequestAndPlaceResp";

	public static final String Q_NWCG_ISSUE_SUCCESS = "NWCG_ISSUE_SUCCESS";

	public static final String Q_NWCG_ISSUE_FAILURE = "NWCG_ISSUE_FAILURE";

	public static final String Q_RADIOS_SUCCESS = "NWCG_ISSUE_RADIOS_SUCCESS";

	public static final String Q_RADIOS_FAILURE = "NWCG_ISSUE_RADIOS_FAILURE";

	public static final String Q_NWCG_FAULT = "NWCG_FAULT";

	public static final String SVC_TRANSFORM_ICBSR_ISSUE_TO_ROSS_XSL = "NWCGTransformICBSRIssueToROSSXSLService";

	public static final String SVC_POST_OB_MSG_SVC = "NWCGPostOBMsgService";

	// Shipping Instructions related constants
	public static final String EXTN_SHIPPING_INSTRUCTIONS_ATTR = "ExtnShippingInstructions";

	public static final String SHIPPING_INSTRUCTIONS_INPUT_RQST_ATTR = "ShippingInstructions";

	public static final String EXTN_SHIPPING_INSTR_CITY_ATTR = "ExtnShipInstrCity";

	public static final String EXTN_SHIPPING_INSTR_STATE_ATTR = "ExtnShipInstrState";

	public static final String INSTRUCTIONS_ELEMENT = "Instructions";

	public static final String INSTRUCTION_ELEMENT = "Instruction";

	public static final String INSTRUCTION_TEXT_ATTR = "InstructionText";

	public static final String INSTRUCTION_TYPE_ATTR = "InstructionType";

	// PersonInfo Related Constants. Most of them are already defined under
	// Reports
	public static final String COUNTRY = "Country";

	public static final String ALTERNATE_EMAIL_ID = "AlternateEmailID";

	// Shipment related constants
	public static final String ITEM_DESC = "ItemDesc";

	public static final String SHIPMENT_TAG_SERIAL = "ShipmentTagSerial";

	public static final String ACTUAL_QUANTITY = "ActualQuantity";

	public static final String EXTN_ESTIMATED_ARRIVAL_DATE = "ExtnEstimatedArrivalDate";

	public static final String EXTN_ESTIMATED_DEPART_DATE = "ExtnEstimatedDepartDate";

	public static final String ACTUAL_SHIPMENT_DATE = "ActualShipmentDate";

	// Order Line related constants
	public static final String EXTN_UTF_QTY = "ExtnUTFQty";

	public static final String EXTN_BACKORDER_QTY = "ExtnBackorderedQty";

	public static final String EXTN_BACKORDER_FLAG = "ExtnBackOrderFlag";

	public static final String EXTN_FORWARD_ORDER_FLAG = "ExtnForwardOrderFlag";

	public static final String EXTN_FWD_QTY = "ExtnFwdQty";

	public static final String EXTN_RFI_QTY = "ExtnQtyRfi";

	public static final String ITEM_SHORT_DESC = "ItemShortDesc";

	public static final String ORDER_LINE_TRAN_QTY_ELM = "OrderLineTranQuantity";

	public static final String STATUS_QTY_ATTR = "StatusQuantity";

	public static final String EXTN_ORGIN_REQ_QTY = "ExtnOrigReqQty";

	public static final String EXTN_BACK_ORDER_NOTIFIED_ROSS = "ExtnBackOrderNotifiedROSS";

	public static final String EXTN_FORWARD_ORDER_NOTIFIED_ROSS = "ExtnForwardOrderNotifiedROSS";

	public static final String STATUS_SHIPPED = "3700";

	public static final String STATUS_UTF = "9000.010";

	public static final String STATUS_CANCELLED_DUE_TO_CONS = "9000.060";

	public static final String STATUS_BACKORDERED = "9000.020";

	public static final String STATUS_FORWARDED = "9000.030";

	public static final String STATUS_CANCELLED_DUE_TO_SUBS = "9000.040";

	public static final String STATUS_CANCELLED_DUE_TO_RETRIEVAL = "9000.050";

	public static final String STATUS_CANCELLED = "9000";

	public static final String NOTES_ELM = "Notes";

	public static final String NOTE_ELM = "Note";

	public static final String NOTE_TEXT_ATTR = "NoteText";

	public static final String SYSTEM_NO_ELEM = "ExtnSystemNo";

	public static final String SEQUENCE_NO_ATTR = "SequenceNo";

	// Order Header order types (from Common Code Type "ORDER_TYPE" Doc 0001
	public static final String OB_ORDER_TYPE_BACKORDERED = "Backordered";

	public static final String OB_ORDER_TYPE_FWD_ORDER = "Forward Order";

	public static final String OB_ORDER_TYPE_NORMAL = "Normal";

	public static final String OB_ORDER_TYPE_REFURBISHMENT = "Refurbishment";

	public static final String OB_ORDER_TYPE_AIRCRAFT = "Aircraft";

	public static final String OB_ORDER_TYPE_CREW = "Crew";

	public static final String OB_ORDER_TYPE_EQUIPMENT = "Equipment";

	public static final String OB_ORDER_TYPE_INCDT_REFURB = "INCDT_REFURBISHMENT";

	public static final String OB_ORDER_TYPE_INCDT_REPLACEMENT = "INCDT_REPLACEMENT";

	public static final String OB_ORDER_TYPE_INCDT_PREPOS = "Incident Preposition";

	public static final String OB_ORDER_TYPE_OVERHEAD = "Overhead";

	public static final String OB_ORDER_TYPE_REPLACEMENT = "Replacement";

	// Jay: Currently we do noth have UC number for status interface, this
	// constant name and value to be replaced with actual UC number like others
	public static final String NWCG_MSG_CODE_STATUS_I_1 = "STATUS-I-000001";

	public static final String NWCG_MSG_CODE_STATUS_E_1 = "STATUS-E-000001";

	public static final String NWCG_MSG_CODE_STATUS_E_2 = "STATUS-E-000002";

	// BEGIN CR 598 - ML
	public static final String NWCG_NEW_CUSTOMER_IDENTIFIER = "OTHER_CUSTOMER";

	// END CR 598 - ML
	// BEGIN CR 557 - ML
	public static final String NWCG_EXTN_TO_RECIEVE = "ExtnToReceive";

	public static final String SHIPMENT_KEY = "ShipmentKey";

	public static final String RECEIVING_NODE = "ReceivingNode";

	public static final String GET_LINES_TO_RECEIVE = "GetLinesToReceive";

	public static final String EXTN = "Extn";

	public static final String PRIME_LINE_NO = "PrimeLineNo";

	public static final String OPEN_QTY = "OpenQty";

	public static final String LOCATIONS = "Locations";

	public static final String LOC_INV_LIST = "LocationInventoryList";

	public static final String COMPONENT = "Component";

	public static final String INV_ITEM_KEY = "InventoryItemKey";

	public static final String IGNORE_ORDERING = "IgnoreOrdering";

	public static final String ORG_CODE = "OrganizationCode";

	public static final String INCIDENT_ELEM = "Incident";

	public static final String LOCATION_DED_ELM = "LocationDedication";

	public static final String LOCATION_ELM = "Location";

	public static final String SKU_DEDICATIONS_ELM = "SKUDedications";

	public static final String SKU_DEDICATION_ELM = "SKUDedication";

	public static final String NODE_INV_ELM = "NodeInventory";

	public static final String INVENTORY_ATTR = "Inventory";

	public static final String INVENTORY_ITEM_ATTR = "InventoryItem";

	public static final String MAXIMUM_RECORDS = "MaximumRecords";

	public static final String LOC_ID_QRY_TYPE = "LocationIdQryType";

	public static final String ITEM_ID_QRY_TYPE = "ItemIDQryType";

	public static final String GET_NODE_INVENTORY = "getNodeInventory";

	public static final String GET_LOCATION_LIST = "getLocationList";

	public static final String SUMMARY_ATTRIBUTES = "SummaryAttributes";

	public static final String PRIMARY_INFO = "PrimaryInformation";

	public static final String DEFAULT_PROD_CLASS = "DefaultProductClass";

	// END CR 557 - ML
	// added by Gaurav--Starts
	public static final String NIRSC_COMMUNICATIONS = "NIRSC Communications";

	public static final String COMMUNICATIONS_PROD_LINE = "Communications";

	public static final String BLM_ACCOUNT_CODE_SEPERATORS = "..";

	// added by Gaurav--Ends
	// BEGIN CR 46 - ML
	public static final String WORK_ORDER = "WorkOrder";

	public static final String WORK_ORDER_KEY = "WorkOrderKey";

	public static final String API_GET_MOVE_REQUEST_DETAILS = "getMoveRequestDetails";

	public static final String API_GET_SHIPMENT_DETAILS = "getShipmentDetails";

	public static final String NWCG_GET_SORTED_ORDER_LINE_LIST_SERVICE = "NWCGGetSortedOrderLineList";

	// Start - Constants for Pending Resource Reassignment
	public static final String SVC_CREATE_PENDING_RR = "NWCGCreatePendingRRService";

	public static final String SVC_GET_PENDING_RR = "NWCGGetPendingRRService";

	public static final String SVC_GET_PENDING_RR_LIST = "NWCGGetPendingRRListService";

	public static final String SVC_CHANGE_PENDING_RR = "NWCGChangePendingRRService";

	public static final String Q_PENDING_RR = "NWCG_PENDING_RR";

	public static final String STATUS_READY_TO_TRIGGER = "READY_TO_TRIGGER";

	public static final String STATUS_NOT_READY_TO_TRIGGER = "NOT_READY_TO_TRIGGER";

	public static final String STATUS_PROCESSED = "PROCESSED";

	public static final String MESSAGE_ATTR = "Message";

	// End - Constants for Pending Resource Reassignment

	// START Updates for Manual Update To ROSS
	public static final String ORDER = "Order";

	public static final String MESSAGE_TYPE = "MessageType";

	public static final String SVC_GET_ORDR_DETAILTEMPLATE = "NWCGGetOrderDetailTemplateService";

	public static final String NWCG_ENTERPRISE_CODE = "NWCG";

	// END Updates for Manual Update To ROSS

	// START - Updating orderline with correct shipment quantity and utf
	// quantity
	public static final String SHIPMENT_LINE_ELEMENT = "ShipmentLine";

	// END - Updating orderline with correct shipment quantity and utf quantity

	// Start - CR 766 - Cancellation line to ROSS
	public static final String MIN_LINE_STATUS = "MinLineStatus";

	public static final String INCDT_REFURBISHMENT_ORDER_TYPE = "INCDT_REFURBISHMENT";

	// Start - CR 748
	public static final String CORRECTION_MISC_ORDER_TYPE = "Correction/Misc";

	// End - CR 748

	// Start - CR 383
	public static final String DOCUMENT_TYPE_CACHE_TRANSFER_ORDER = "0006";

	public static final String ORDER_TYPE_REFURB_TRANSFER = "Refurb Transfer";

	public static final String MASTER_WORKORDER_TYPE_REFURB_TRANSFER = "Refurb Transfer";

	public static final String ENTERPRISE = "Enterprise";

	public static final String NWCG_REFURB_MWOL_TRANSFERRED_STATUS = "Work Order Transferred";

	public static final String SERIAL_RECORD_TRANS_TYPE_SHIPMENT = "SHIPMENT";

	public static final String SERIAL_RECORD_TRANS_TYPE_RECEIPT = "RECEIPT";

	public static final String DOCUMENT_NODE_SHIPMENT = "Shipment";

	public static final String DOCUMENT_NODE_RECEIPT = "Receipt";

	public static final String DOCUMENT_TYPE_ISSUE = "0001";

	public static final String DOCUMENT_TYPE_CACHETRANSFER = "0006";

	public static final String DOCUMENT_TYPE_BLINDRETURN = "0010";

	public static final String DOCUMENT_TYPE_INCIDENTTRANSFER = "0008.ex";

	// BEGIN MK Production Bug Fix 1/8/13
	public static final String DOCUMENT_TYPE_OTHERISSUE = "0007.ex";

	// END MK Production Bug Fix 1/8/13
	// End - CR 383

	// Begin CR846
	public static final String EXTN_BASE_ORDER_HEADER_KEY = "ExtnBaseOrderHeaderKey";

	public static final String EXTN_BASE_REQUEST_NO = "ExtnBaseRequestNo";

	// End CR846

	// Begin CR868 12052012
	public static final String SERVICE_NWCG_GET_ORDER_LIST = "NWCGGetOrderList";

	public static final String TEMPLATE_GET_ORDER_LIST_FOR_INCIDENT = "NWCGGetOrderList_getOrderList";

	public static final String SERVICE_NWCG_GET_INCIDENT_ORDER_LIST = "NWCGGetIncidentOrderListService";

	// End CR868 12052012

	// Begin CR830 01252013
	public static final String TEMPLATE_NWCG_GET_SORTED_ORDER_LINE_LIST = "NWCGGetSortedOrderLineList";

	// End CR830 01252013

	// Begin CR870
	public static final String NWCG_CHANGE_ORDER_ATTRIBUTE = "Attribute";

	public static final String NWCG_CHANGE_ORDER_ATTRIBUTE_NAME = "Name";

	public static final String NWCG_CHANGE_ORDER_ATTRIBUTE_NEWVALUE = "NewValue";

	public static final String SHIPMENT_LINES_ELEMENT = "ShipmentLines";

	public static final String API_CHANGE_SHIPMENT = "changeShipment";
	// End CR870

	// Begin CR983
	public static final String EXTN_ISUITE_PROCESSED = "ExtnIsuitePro";

	public static final String GET_ISUITE_PROCESSED_VALUE = "N";

	public static final String SET_ISUITE_PROCESSED_VALUE = "Y";

	public static final String NWCG_POST_ISUITE_RECORD_SERVICE = "NWCGPostISuiteRecordService";

	public static final String DOCUMENT_NODE_SHIPMENTS = "Shipments";

	public static final String API_GET_SHIPMENT_LIST = "getShipmentList";

	public static final String SHIPMENT_CLOSED_FLAG_ATTR = "ShipmentClosedFlag";

	public static final String SHIPMENT_CLOSED_FLAG_VALUE = "N";

	public static final String GET_SHIPMENT_LIST_MAX_RECORDS_VALUE = "50";

	public static final String SHIPMENT_SHIPPED_STATUS = "1400";

	// End CR983

	// Begin CR823
	public static final String EXTN_REFURB_REQUIRED = "ExtnRefurbRequired";
	// End CR823

	// CR 1051 - Extract time out
	public static final String NWCG_PROCESS_BILLING_TRANSACTION_EXTRACT_FTP_SERVICE = ResourceUtil
			.get("nwcg.icbs.billingtransactionextractftp.service");

	// To get NWCGGetFBMSCodes.java file to compile
	public static final String ORDER_BY = "ORDER_BY";
	public static final String ATTRIBUTE = "Attribute";

	// Pay.gov
	public static final String NWCG_PAY_GOV_WEBSERVICE_CALL_SERVICE = "NWCGPayGovWebServiceCall";
	public static final String NWCG_PAY_GOV_WEBSERVICE_PREFIX = "tcs:";
	
	public static final String NWCG_PAY_GOV_PCSALE = "PCSale";
	public static final String NWCG_PAY_GOV_PCSALE_REQUEST = "PCSaleRequest";
	public static final String NWCG_PAY_GOV_ACTION = "paygov_action";
	public static final String NWCG_PAY_GOV_AUTH_RESPONSE_CODE = "auth_response_code";
	public static final String NWCG_PAY_GOV_TRANSACTION_STATUS = "transaction_status";
	public static final String NWCG_PAY_GOV_AGENCY_TRACKING_ID = "agency_tracking_id";
	public static final String NWCG_PAY_GOV_TRACKING_ID = "paygov_tracking_id";
	public static final String NWCG_PAY_GOV_TRANSACTION_AMOUNT = "transaction_amount";
	public static final String NWCG_PAY_GOV_RETURN_CODE= "return_code";
	public static final String NWCG_PAY_GOV_RETURN_DETAIL= "return_detail";
	public static final String NWCG_PAY_GOV_TRANSACTION_DATE= "transaction_date";
	public static final String NWCG_PAY_GOV_APPROVAL_CODE= "approval_code";
	public static final String NWCG_PAY_GOV_CSC_RESULT= "csc_result";
	public static final String NWCG_PAY_GOV_AGENCY_ID= "agency_id";
	public static final String NWCG_PAY_GOV_TCS_APP_ID = "tcs_app_id";
	public static final String NWCG_PAY_GOV_BUSINESS_NAME = "business_name";
	public static final String NWCG_PAY_GOV_FIRST_NAME = "first_name";
	public static final String NWCG_PAY_GOV_LAST_NAME = "last_name";
	public static final String NWCG_PAY_GOV_BILLING_ADDRESS = "billing_address";
	public static final String NWCG_PAY_GOV_BILLING_ADDRESS_2 = "billing_address_2";
	public static final String NWCG_PAY_GOV_BILLING_CITY = "billing_city";
	public static final String NWCG_PAY_GOV_BILLING_STATE = "billing_state";
	public static final String NWCG_PAY_GOV_BILLING_ZIP = "billing_zip";
	public static final String NWCG_PAY_GOV_BILLING_COUNTRY = "billing_country";
	public static final String NWCG_PAY_GOV_CUSTOM_FIELDS = "custom_fields";
	public static final String NWCG_PAY_GOV_CUSTOM_FIELD_1 = "custom_field_1";
	public static final String NWCG_PAY_GOV_CUSTOM_FIELD_2 = "custom_field_2";
	public static final String NWCG_PAY_GOV_CUSTOM_FIELD_3 = "custom_field_3";
	public static final String NWCG_PAY_GOV_CUSTOM_FIELD_4 = "custom_field_4";
	public static final String NWCG_PAY_GOV_CUSTOM_FIELD_5 = "custom_field_5";
	public static final String NWCG_PAY_GOV_CUSTOM_FIELD_6 = "custom_field_6";
	public static final String NWCG_PAY_GOV_CUSTOM_FIELD_7 = "custom_field_7";
	public static final String NWCG_PAY_GOV_CUSTOM_FIELD_8 = "custom_field_8";
	public static final String NWCG_PAY_GOV_CUSTOM_FIELD_9 = "custom_field_9";
	public static final String NWCG_PAY_GOV_CUSTOM_FIELD_10 = "custom_field_10";
	public static final String NWCG_PAY_GOV_CUSTOM_FIELD_11 = "custom_field_11";
	public static final String NWCG_PAY_GOV_CUSTOM_FIELD_12 = "custom_field_12";
	
	public static final String NWCG_CUSTOMER_NAME = "CustomerName";
	
	public static final String NWCG_UPDATE_PAYGOV_RESPONSE_EXTERNAL_CHARGES_SERVICE = "NWCGPayGovRecordExternalCharges";
	public static final String NWCG_CHANGE_ORDER_STATUS_TO_SHIPPEDANDCHARGED_SERVICE = "NWCGChangeOrderStatusToShippedAndCharged";
	public static final String NWCG_UPDATE_AUTH_CODE_SERVICE = "NWCGPayGovUpdateAuthorizationCode";

	public static final String NWCG_PAYMENT_CREDIT_CARD_TYPE = "CREDIT_CARD";
	public static final String NWCG_CHARGE_TYPE = "CHARGE";
	public static final String NWCG_SHIPPED_CHARGED_STATUS = "3700.200";
	public static final String NWCG_CHANGE_PAY_STATUS = "CHANGE_PAY_STATUS.0007.ex.ex";
	public static final String NWCG_PAY_GOV_ENDPOINT = "https://qa.tcs.pay.gov/tcscollections/services/TCSSingleService";//move to property file
	public static final String NWCG_PAYGOV_SERVER_URI = "http://fms.treas.gov/tcs/schemas";
	public static final String NWCG_PAY_GOV_AGENCY_ID_VALUE= "1240";
	public static final String NWCG_PAY_GOV_TCS_APP_ID_VALUE = "TCSICBSCERT";

	public static final String EXTN_AUTHORIZATION_CODE = "ExtnAuthorizationCode";
	public static final String NWCG_AUTHORIZATION_CODE = "AuthorizationCode";
	public static final String NWCG_TRANSACTION_STATUS = "TransactionStatus";
	
	public static final String NWCG_SHIPPED_CHARGED = "ShippedAndCharged";
	
	public static final String NWCG_YFS_ENVIRONMENT = "YFSEnvironment";
	
	public static final String NWCG_CODE_SHORT_DESCRIPTION = "CodeShortDescription";
	
	public static final String NWCG_BILLING_PERSON_INFO = "BillingPersonInfo";
	
	public static final String ADDITIONAL_ADDRESSES = "AdditionalAddresses";
	public static final String ADDITIONAL_ADDRESS = "AdditionalAddress";
	public static final String ADDRESS_TYPE = "AddressType";
	public static final String PERSON_INFO = "PersonInfo";
	public static final String COMMON_CODE = "CommonCode";
	public static final String CUSTOMER_NAME = "CustomerName";
	public static final String AGENCY_TRACKING_ID = "AgencyTrackingId";
	public static final String RECORD_EXTERNAL_CHARGES= "RecordExternalCharges";
	public static final String TRANSACTION_ID_ATTR= "TransactionId";
	public static final String PAYMENT_METHOD= "PaymentMethod";
	public static final String CREDIT_CARD_NO_ATTR= "CreditCardNo";
	public static final String PAYMENT_TYPE_ATTR= "PaymentType";
	public static final String PAYMENT_REFERNCE_1_ATTR= "PaymentReference1";
	public static final String PAYMENT_REFERNCE_2_ATTR= "PaymentReference2";
	public static final String PAYMENT_REFERNCE_3_ATTR= "PaymentReference3";
	public static final String TOTAL_ACTUAL_CHARGE_ATTR= "TotalActualCharge";
	public static final String PAYMENT_DETAILS = "PaymentDetails";
	
	public static final String CHARGE_TYPE_ATTR = "ChargeType";
	public static final String PROCESSED_AMOUNT_ATTR = "ProcessedAmount";
	public static final String TRAN_RETURN_CODE_ATTR = "TranReturnCode";
	public static final String TRAN_RETURN_MESSAGE_ATTR = "TranReturnMessage";
	public static final String COLLECTION_DATE_ATTR = "CollectionDate";
	public static final String AUTH_CODE_ATTR = "AuthCode";
	public static final String TRANSACTION_AMOUNT_ATTR = "TransactionAmount";
	public static final String ACCOUNT_NUMBER_ATTR = "account_number";
	public static final String CC_EXP_DATE_ATTR = "credit_card_expiration_date";
	public static final String ADDRESS_LINE_1_ATTR = "AddressLine1";
	public static final String ADDRESS_LINE_2_ATTR = "AddressLine2";
	public static final String ADDRESS_LINE_3_ATTR = "AddressLine3";
	public static final String SCAC_ATTR = "SCAC";
	public static final String ISSUE_QUANTITY_ATTR = "IssueQuantity";
	public static final String ISSUE_TOTAL_COST_ATTR = "IssueTotalCost";
	public static final String SHIPPING_QUANTITY_ATTR = "ShippingQuantity";
	public static final String SHIPPING_TOTAL_COST_ATTR = "ShippingTotalCost";
	
	
	public static final String ORDER_STATUS_CHANGE_ELEMENT = "OrderStatusChange";
	public static final String BASE_STATUS_CHANGE_ATTR = "BaseDropStatus";
	
	public static final String PAYGOV_DEFAULT_RETURN_CODE = "5006";
	public static final String PAYGOV_SUCCESSFUL_RETURN_CODE = "2002";
	public static final String PAYGOV_DEFAULT_RETURN_DESC = "Unable to process the request at this time";
	public static final String PAYGOV_KSPATH = "/opt/apps/projects/paygov/paygovkey.jks";//Move to property file
    public static final String PAYGOV_JKPASSWORD = "changeit";//Move to property file
    
    public static final String ISSUE_CBS_CODE_FED = "FED_ISSUE";
    public static final String ISSUE_CBS_CODE_NONFED = "NONFED_ISSUE";
    public static final String SHIP_CBS_CODE_FED = "FED_SHIP";
    public static final String SHIP_CBS_CODE_NONFED = "NONFED_SHIP";
}