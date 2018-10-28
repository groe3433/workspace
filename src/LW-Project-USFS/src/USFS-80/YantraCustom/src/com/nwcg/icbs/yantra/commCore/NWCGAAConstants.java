package com.nwcg.icbs.yantra.commCore;

public class NWCGAAConstants {
	
	//SOAPFault codes and strings
	public static final String SF_CLIENT_AUTH = "soapenv:Client.Authentication";
	
	//message names
	
	public static final String GET_INCIDENT_REQ_MSG_NAME = "GetIncidentReq";
	public static final String AUTH_RESP_MSG_NAME = "" ;
	public static final String UPDATE_NFES_RESOURCE_REQUEST_MESSAGE_ROOT = "ro:UpdateNFESResourceRequestReq";
	
	//Service Grouped Names
	public static final String AUTH_SERVICE_GROUP_NAME = "AUTH";
	public static final String RESPONSE_SERVICE_GROUP_NAME = "RESPONSE";
	public static final String INCIDENT_SERVICE_GROUP_NAME = "INCIDENT";
	public static final String SERVICE_NAME = "NWCGGetOBMessageListService";
	public static final String GET_OB_MESSAGE_LIST_SERVICE = "NWCGGetOBMessageListService";
	public static final String GET_IB_MESSAGE_LIST_SERVICE = "NWCGGetIBMessageListService";
	public static final String DELETE_IB_MESSAGE_SERVICE = "NWCGDeleteIBMessageService";
	public static final String DELETE_OB_MESSAGE_SERVICE = "NWCGDeleteOBMessageService";
	public static final String NWCG_CHANGE_INCIDENT_ORDER_SERVICE = "NWCGChangeIncidentOrderService";
	public static final String NWCG_GET_INCIDENT_ORDER_ONLY_SERVICE = "NWCGGetIncidentOrderOnlyService";
	public static final String GET_OPER_RESU_OB_MSG_LIST_SERVICE = "NWCGGetOperResOBMsgListService";
	public static final String GET_IB_MESSAGE_LIST_SERVICE_WTEMPLATE = "NWCGGetIBMessageListServiceWTemplate";
	
	//XML Message Names
	public static final String AUTH_USER_RESP_OB_MSG_NAME = "AuthUserResp-OB";
	
	//Service Names
	public static final String AUTH_USER_REQ_IB_SERVICE_NAME = "AuthUserReq-IB";
	public static final String RESPONSE_STATUS_TYPE_SERVICE_NAME = "ResponseStatusType";
	
	public static final String SYSTEM_NAME = "ICBS";
	public static final String EXTERNAL_SYSTEM_NAME = "ROSS";
	
	//Old Message Statuses
	public static final String MESSAGE_STATUS_CREATED = "CREATED";	
	
	// VOID is the status of the initial message which is stored in message store
	public static final String MESSAGE_STATUS_VOID = "VOID"; 

	// SENT is the status of the latest OB message which is posted to the ROSS web service
	// This is part of redesign of A&A
	public static final String MESSAGE_STATUS_OB_MESSAGE_SENT = "SENT"; 
	
	// used in deliver operations
	public static final String MESSAGE_STATUS_MESSAGE_RECEIVED = "MESSAGE_RECEIVED"; 

	// Above MESSAGE RECEIVED is being changed to RESPONSE_RECEIVED
	public static final String MESSAGE_STATUS_RESPONSE_RECEIVED = "RESPONSE_RECEIVED"; 
	
	// used in deliver operations
	public static final String MESSAGE_STATUS_PROCESSED = "PROCESSED";	
	
	public static final String MESSAGE_STATUS_AUTHORIZED = "AUTHORIZED"; 
	
	// AWAITING RESPONSE is the status of the latest message which just got posted
	// from the client successfully(i.e client got the message acknowledgement
	public static final String MESSAGE_STATUS_AWAITING_RESPONSE = "AWAITING_RESPONSE"; 
	public static final String MESSAGE_STATUS_AWAITING_AUTHORIZATION = "AWAITING_AUTHORIZATION";
	public static final String MESSAGE_STATUS_MESSAGE_SENT="MESSAGE_SENT";
	public static final String 	MESSAGE_STATUS_AUTHORIZATION_FAILED="AUTHORIZATION_FAILED";
	
	// SENT_FAILED status is used for remote connection failure
	public static final String MESSAGE_STATUS_SENT_FAILED = "SENT_FAILED";
	
	public static final String MESSAGE_STATUS_FAILED_READY_FOR_PICKUP = "FAILED_READY_FOR_PICKUP";
	
	//FAULT is the latest message status which is generated on account of Auth&Auth failure - New A&A Design 
	public static final String MESSAGE_STATUS_OB_FAULT = "FAULT";
	public static final String MESSAGE_STATUS_SOAP_FAULT = "SOAP_FAULT";
	
	// used in deliver operations
	public static final String MESSAGE_STATUS_PROCESSING= "MESSAGE_PROCESSING";
	
	public static final String MESSAGE_STATUS_DISCARD = "MESSAGE_DISCARDED";
	
	// General constants
	public static final String STR_TRUE = "TRUE";
	public static final String STR_FALSE = "FALSE";
	
	// This string is used to denote whether it is sync or async
	public static final String OPERATION_TYPE = "OperationType";
	public static final String OPERATION_SYNC = "Sync";
	public static final String OPERATION_ASYNC = "Async";
		
	// Message Type
	public static final String MESSAGE_TYPE_START = "START";
	public static final String MESSAGE_TYPE_LATEST = "LATEST";
	
	public static final String MESSAGE_DIR_TYPE_OB = "OB";
	public static final String MESSAGE_DIR_TYPE_IB = "IB";
	
	public static final String HANDLER_PROP_ORIG_REQ_NAME=".originalRequestName";
	public static final String HANDLER_PROP_EXTENSION_TYPE =".type";
	public static final String HANDLER_PROP_EXTENSION_IBPROCESSOR = ".IBProcessor";
	public static final String HANDLER_PROP_EXTENSION_OBRESPONSE_PROCESSOR = ".OBResponseProcessor";
	public static final String HANDLER_PROP_EXTENSION_OBREQUEST_PROCESSOR = ".OBRequestProcessor";
	public static final String HANDLER_PROP_EXTENSION_IS_SYNC =".isSync";
	public static final String HANDLER_PROP_EXTENSION_IS_NOTIFICATION =".isNotification";
	
	// SDF Service Names
	public static final String SDF_CREATE_OB_MESSAGE_NAME = "NWCGCreateOBMessage";
	public static final String SDF_CREATE_IB_MESSAGE_NAME = "NWCGCreateIBMessage";
	
	public static final String SDF_CHANGE_OB_MESSAGE_NAME = "NWCGChangeOBMessage";
	public static final String SDF_CHANGE_IB_MESSAGE_NAME = "NWCGChangeIBMessage";
	
	public static final String SDF_GET_OB_MESSAGE_LIST_SERVICE_NAME = "NWCGGetOBMessageListService";
	public static final String SDF_GET_IB_MESSAGE_LIST_SERVICE_NAME = "NWCGGetIBMessageListService";	
	public static final String SDF_POST_SOAP_SERVICE_NAME = "JMS_IB2";
	 
	
	//YFS Environemnt vars
	
	public static final String YANTRA_USERNAME = "IcbsInterface";
	public static final String YANTRA_USER_ID = "IcbsInterface";
	
	public static final String PROPERTIES_FILENAME = "C:\\AST\\workspace\\ICBSRAuth\\WebContent\\WEB-INF\\yantraimpl.properties";

	//SOAP Fault Template Strings
	public static final String SOAP_FAULT_FAULTCODE = "SOAP-ENV:Server";
	public static final String SOAP_FAULT_FAULTACTOR = "http://brokerjms:5555/soap/ross";
	public static final String SOAP_FAULT_DETAILS_NAMESPACE = "http://www.webMethods.com/2001/10/soap/encoding";
	public static final String SOAP_FAULT_SCHEMA = "http://schemas.xmlsoap.org/soap/envelope/";
	
	//SOAP Codes
	public static final int SOAP_SUCCESS_CODE = 0;
	public static final int SOAP_FAILURE_CODE = -1;
	
	//SOAP Success Response Codes
	public static final String SOAP_SUCCESS_MESSAGE_CODE = "OK";
	
	//SOAP Failure Response Codes
	public static final String SOAP_FAILURE_MESSAGE_CODE_1 = "NWCG-ICBSR-ANA-000001";
	public static final String SOAP_FAILURE_MESSAGE_CODE_2_1 = "NWCG-ICBSR-ANA-000002.1";
	public static final String SOAP_FAILURE_MESSAGE_CODE_2_2 = "NWCG-ICBSR-ANA-000002.2";
	public static final String SOAP_FAILURE_MESSAGE_CODE_3 = "NWCG-ICBSR-ANA-000003";
	public static final String SOAP_FAILURE_MESSAGE_CODE_4 = "NWCG-ICBSR-ANA-000004";
	public static final String SOAP_FAILURE_MESSAGE_CODE_5 = "NWCG-ICBSR-ANA-000005";
	public static final String SOAP_FAILURE_MESSAGE_CODE_6 = "NWCG-ICBSR-ANA-000006";
	public static final String SOAP_FAILURE_MESSAGE_CODE_7 = "NWCG-ICBSR-ANA-000007";
	public static final String SOAP_FAILURE_MESSAGE_CODE_8 = "NWCG-ICBSR-ANA-000008";
	public static final String SOAP_FAILURE_MESSAGE_CODE_9 = "NWCG-ICBSR-ANA-000009";
	
	//SOAP Success Response Messages
	public static final String SOAP_SUCCESS_RESPONSE_MESSAGE = "Success";
	
	//SOAP Failure Response Messages
	public static final String SOAP_FAILURE_RESPONSE_MESSAGE_1 = "Invalid Time Sensitive Key";
	public static final String SOAP_FAILURE_RESPONSE_MESSAGE_2 = "User ID and Dispatch unit ID does not match";
	public static final String SOAP_FAILURE_RESPONSE_MESSAGE_3 = "Invalid User ID";
	public static final String SOAP_FAILURE_RESPONSE_MESSAGE_4 = "MessageContext does not exist";
	public static final String SOAP_FAILURE_RESPONSE_MESSAGE_5 = "Security Token does not exist";
	public static final String SOAP_FAILURE_RESPONSE_MESSAGE_6 = "Security Token does not have password element";
	public static final String SOAP_FAILURE_RESPONSE_MESSAGE_7 = "Security Token does not have valid time stamp";
	public static final String SOAP_FAILURE_RESPONSE_MESSAGE_8 = "Black Listed User";
	public static final String SOAP_FAILURE_RESPONSE_MESSAGE_9 = "Invalid message key";

	// SOAP Failure message after getting response from ROSS
	public static final String SOAP_FAILURE_INVALID_DIST_ID = "Response Message received from ROSS is not equal to the message which maps to the Distribution ID";
	public static final String SOAP_FAILURE_NO_DIST_ID = "Response Message received from ROSS without Distribution ID";
	
	//SOAP Authentication Message
	public static final String SOAP_AUTHENTICATION_MESSAGE_SUCCESS = "Success";
	public static final String SOAP_AUTHENTICATION_MESSAGE_FAILURE = "Failure";
	
	//SOAP IsAuthenticationIndicator values
	public static final boolean SOAP_IS_AUTHENTICATION_INDICATOR_TRUE = true;
	public static final boolean SOAP_IS_AUTHENTICATION_INDICATOR_FALSE = false;
	
	public static final String SEVERITY_SUCCESS = "Information";
	public static final String SEVERITY_ERROR = "Error";
	
	public static final String API_GETUSERLIST	  = "getUserList";
	public static final String TEMPLATE_GETUSERLIST = "<UserList><User Activateflag=\"\" LoginStatus=\"\" Loginid=\"\" OrganizationKey=\"\" Password=\"\" Username=\"\" Usertype=\"\"><ContactPersonInfo FirstName=\"\" LastName=\"\" MiddleName=\"\" /></User></UserList>";
	
	public static final String BLACKLISTED_USER = "blacklisteduser1";
	
	//Distribution status
    public static final String DIST_STATUS_ACTUAL = "Actual";
    public static final String DIST_STATUS_EXERCISE = "Exercise";
    public static final String DIST_STATUS_SYSTEM = "System";
    public static final String DIST_STATUS_TEST = "Test";

    //Distribution type
    public static final String DIST_TYPE_REQUEST = "Request";
    public static final String DIST_TYPE_RESPONSE = "Response";
    public static final String DIST_TYPE_ACK = "Ack";
    public static final String DIST_TYPE_REPORT = "Report";
    public static final String DIST_TYPE_UPDATE = "Update";
    public static final String DIST_TYPE_CACNEL = "Cancel";
    public static final String DIST_TYPE_DISPATCH = "Dispatch";
    public static final String DIST_TYPE_ERROR = "Error";

    //Message Context strings of Acknowledgement from ROSS side
    public static final String CUSTOM_PROPERTIES = "processSynchronously=true";
    public static final String SYSTEM_ID = "";
    public static final String SYSTEM_TYPE = "ICBS";
    public static final String CATALOG_NAMESPACE = "http://nwcg.gov/services/ross/catalog/1.1";
    public static final String OPERATIONS_NAMESPACE = "http://nwcg.gov/services/ross/resource_order/1.1";
    public static final String COMMMON_TYPES_NAMESPACE = "http://nwcg.gov/services/ross/common_types/wsdl/1.1";
    public static final String SENDER_ID = "ICBS";
    
    // Alerts related constants
    
    // handles all inbound related exceptions
    public static final String QUEUEID_INBOUND_EXCEPTION = "NWCG_IB_EXCEPTIONS";
    
    // not used as of now
    public static final String QUEUEID_INCIDENT 		= "NWCG_INCIDENT";
    
    // default queue
    public static final String QUEUEID_DEFAULT 		= "DEFAULT";
    
    // NWCG_FAULT_Q queue handles all alerts raised as a result of SOAP faults
    public static final String QUEUEID_FAULT 			= "NWCG_FAULT";
    
    //If security verification fails for Inbound Async msg
    public static final String  QUEUEID_NWCG_INBOUND_AUTHORIZATION_FAILED="NWCG_INBOUND_AUTHORIZATION_FAILED";
    
    // This queue contains alerts which are generated for no message response
    public static final String QUEUEID_NO_RESPONSE	=	"NWCG_NO_RESPONSE_MESSAGES";
    
    public static final String QUEUEID_INCIDENT_FAILURE = "NWCG_INCIDENT_FAILURE";
    public static final String QUEUEID_CATALOG_FAILURE  = "NWCG_CATALOG_FAILURE";
    public static final String QUEUEID_ROSS_PUBLISH  = "NWCG_ROSS_PUBLISH";
	
    //Queues for issue responses
    public static final String QUEUEID_ISSUE_FAILURE = "NWCG_ISSUE_FAILURE";
    public static final String QUEUEID_ISSUE_SUCCESS = "NWCG_ISSUE_SUCCESS";
    
    public static final String QUEUEID_ISSUE_RADIOS_SUCCESS = "NWCG_ISSUE_RADIOS_SUCCESS";
    public static final String QUEUEID_ISSUE_RADIOS_FAILURE = "NWCG_ISSUE_RADIOS_FAILURE";
    
    public static final String ENTERPRISE_KEY 		= "NWCG";
    public static final String INBOX_TYPE 			= "ALERT";
    public static final String PRIORITY 				= "1";
    public static final String NAME 					= "SOAP-MESSAGE";
    public static final String REFERENCE_TYPE 		= "TEXT";
    public static final String ASSIGNED_TO_USER_ID 	= "nwcgsys"; // hard coded as of now
    public static final String ALERT_USERID			= "icbsross"; // Reference field only
    public static final String ALERT_PROGID			= "icbsross"; // Reference field only
    public static final String API_CREATE_EXCEPTION	= "createException";
    
    // Message Store constants
    public static final String MSG_STORE_1 = "Message Store update failed";
    public static final String MESSAGE = "Message";
    public static final String MESSAGE_NAME = "MessageName";
    public static final String MESSAGE_KEY = "MessageKey";
    public static final String MESSAGE_STATUS = "MessageStatus";
    public static final String MESSAGE_TYPE = "MessageType";
    public static final String SYSTEM_NAME_ATTR = "SystemName";
    public static final String LATEST_INBOUND = "LatestInbound";
    public static final String LATEST_MESSAGE_KEY = "LatestMessageKey";
    public static final String NWCG_OUTBOUND_MSG_ELM = "NWCGOutboundMessage";
    public static final String NWCG_INBOUND_MSG_ELM = "NWCGInboundMessage";
    
    // DeliverOperationResults constants
    /**** start *****/
    
    public static final String ALERT_INVALIDMSG = "ROSS Catalog Item synchronization failed for Item - ";
    public static final String ALERT_INVALIDMSG_INC = "ROSS Incident Item synchronization failed for Incident - ";
    
    public static final String ALERT_HANDLER_ERROR = "Alert raised due to a problem in Yantra handler invocation";
    
    public static final String DELIVER_OPERATION_RESULTS_SUCCESS = "Success";
    public static final String DELIVER_OPERATION_RESULTS_FAILURE = "Failure";
    
    public static final int DELIVER_OPERATION_RESULTS_RETURN_SUCCESS_CODE = 0;
    public static final int DELIVER_OPERATION_RESULTS_RETURN_FAILURE_CODE = -1;
    
    public static final String DELIVER_OPERATION_RESULTS_SUCCESS_DESC   = "Deliver Operation Results Success";
    public static final String DELIVER_OPERATION_RESULTS_FAILURE_DESC_1 = "Original request could not be found for distribution id";
    public static final String DELIVER_OPERATION_RESULTS_FAILURE_DESC_2 = "No Distribution ID found in passed input";
    public static final String DELIVER_OPERATION_RESULTS_FAILURE_DESC_3 = "Deliver Operation failed in processing";
    public static final String DELIVER_OPERATION_RESULTS_FAILURE_DESC_4 = "There is no message body in passed input";
    
    public static final String DELIVER_OPERATION_RESULTS_SEVERITY_1 = "Error";
    public static final String DELIVER_OPERATION_RESULTS_SEVERITY_2 = "Information";
    public static final String DELIVER_OPERATION_RESULTS_SEVERITY_3 = "Warning";
    
    public static final String DELIVER_OPERATION_RESULTS_MESSAGECODE_1 = "NWCG-ICBSR-DO-000001";
    public static final String DELIVER_OPERATION_RESULTS_REQ_MSG_NAME = "DeliverOperationResultsReq";
    public static final String DELIVER_OPERATION_RESULTS_DISTID_ELM = "DistributionID";
    
    public static final String AL_CONST_1 = "CreateCatalogItemResp";
    public static final String AL_CONST_2 = "DeleteCatalogItemResp";
    public static final String AL_CONST_3 = "UpdateCatalogItemResp";
    public static final String AL_CONST_4 = "RegisterIncidentInterestResp";
    public static final String AL_CONST_5 = "SetIncidentActivationResp";
    public static final String REG_INC_INT_REQ = "RegisterIncidentInterestReq";
    public static final String INCIDENT_TYPE = "IncidentType";
    public static final String XPATH_RETURNCODE            = "/ResponseStatus/ReturnCode";
    public static final String XPATH_CODE        	   		 = "/ResponseStatus/ResponseMessage/Code";
    public static final String XPATH_SEVERITY   	   		 = "/ResponseStatus/ResponseMessage/Severity";
    public static final String XPATH_DESCRIPTION     		 = "/ResponseStatus/ResponseMessage/Description";
    public static final String XPATH_CATALOGTYPE     		 = "/CatalogItemKey/CatalogType";
    public static final String XPATH_CATALOGITEMNAME 		 = "/CatalogItemKey/CatalogItemName";
    public static final String XPATH_CREATECATALOGITEMRESP = "/DeliverOperationResultsReq/CreateCatalogItemResp";
    
    public static final String NWCG_POST_DELIVER_OPERATIONS_SERVICE = "NWCGPostDeliverOperationsResultsService";
    public static final String NWCG_POST_SOAP_MESSAGE_SERVICE		  = "NWCGPostIBNotificationService"; //TB added
    
    /**** end *****/
    
    // Dispatch ID related constants.
    public static final String CODE_TYPE 			= "NWCG_DISPATCH";
    public static final String API_COMMONCODELIST = "getCommonCodeList";
    public static final String DISPATCH_PREFIX 	= "DISPATCH_PREFIX";
    public static final String DISPATCH_SUFFIX 	= "DISPATCH_SUFFIX";
    
    // SOAP Fault related constants
    public static final String FAULT_CODE_STRING 			 = "faultcode";
    public static final String FAULT_CODE_STRING_STARTELEM = "<faultcode>";
    public static final String FAULT_CODE_STRING_ENDELEM 	 = "</faultcode>";
    
    public static final String FAULT_STRING 			= "faultstring";
    public static final String FAULT_STRING_STARTELEM = "<faultstring>";
    public static final String FAULT_STRING_ENDELEM 	= "</faultstring>";
    
    // Outbound message header constants
    /**** start ******/
    public static final String STRING_GREGORIAN_CAL_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    
    public static final String SECURITY_ELEM_NAME = "Security";
    public static final String SECURITY_ELEM_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    public static final String SECURITY_ELEM_NAMESPACE_PREFIX = "ns1";
    
    public static final String USERNAME_TOKEN_ELEM_NAME = "UsernameToken";
    public static final String USERNAME_ELEM_NAME = "Username";
	public static final String PASSWORD_ELEM_NAME = "Password";
	public static final String TYPE_NAME = "Type";
	public static final String TYPE_NAME_ATTR = "wsse:PasswordText";
	public static final String NONCE_ELEM_NAME = "Nonce";
	public static final String USER_ORG_ELEM_NAME= "userOrganization";
	public static final String CREATED_ELEM_NAME = "Created";
	public static final String CREATED_ELEM_NAMESPACE_PREFIX = "ns2";
	public static final String CREATED_ELEM_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
	
	public static final String MESSAGE_CONTEXT_ELEM_NAME = "MessageContext";
	public static final String MESSAGE_CONTEXT_ELEM_NAMESPACE_PREFIX = "ns1";
	public static final String MESSAGE_CONTEXT_ELEM_NAMESPACE = "http://nwcg.gov/services/ross/common_types/1.1";
	public static final String DISTRIBUTION_ID_ELEM_NAME = "distributionID";
	public static final String SENDER_ID_ELEM_NAME = "senderID";
	public static final String DATE_TIME_SENT_ELEM_NAME = "dateTimeSent";
	public static final String DISTRIBUTION_STATUS_ELEM_NAME = "distributionStatus";
	public static final String DISTRIBUTION_TYPE_ELEM_NAME = "distributionType";
	public static final String MESSAGE_NAME_ELEM_NAME = "messageName";
	public static final String NAMESPACE_ELEM_NAME = "namespaceName";
	public static final String SYSTEM_ID_ELEM_NAME = "systemID";
	public static final String SYSTEM_TYPE_ELEM_NAME = "systemType";
	public static final String CUSTOM_PROPERTY_ELEM_NAME = "customProperties";
	
    /**** end *****/
   
	public static final String CREATE_CATALOG_ITEM_REQ = "CreateCatalogItemReq";
	public static final String XPATH_CREATE_CATALOG_ITEM_CODE = "/CreateCatalogItemReq/CatalogItem/CatalogItemCode/text()";
	public static final String XPATH_CREATE_CATALOG_ITEM_NAME = "/CreateCatalogItemReq/CatalogItem/CatalogItemName/text()";
	public static final String XPATH_CREATE_CATALOG_TYPE = "/CreateCatalogItemReq/CatalogItem/CatalogType/text()";
	public static final String XPATH_CREATE_CATALOG_ITEM_KEY = "/CreateCatalogItemReq/CatalogItem/CatalogItemKey/text()";
	public static final String XPATH_CREATE_ORDERABLE_QTY = "/CreateCatalogItemReq/CatalogItem/OrderableWithQuantityInd/text()";
	public static final String XPATH_CREATE_STANDARD_PACK = "/CreateCatalogItemReq/CatalogItem/StandardPack/text()";
	public static final String XPATH_CREATE_TRACKING_REQ = "/CreateCatalogItemReq/CatalogItem/TrackingRequiredInd/text()";
	public static final String XPATH_CREATE_UNIT_OF_ISSUE = "/CreateCatalogItemReq/CatalogItem/UnitOfIssue/text()";
	public static final String CATALOG_NAMESPACE_OB = "http://nwcg.gov/services/ross/catalog/wsdl/1.1";
	public static final String CATALOG_PORT_OB = "CatalogPort";
	public static final String RESOURCE_NAMESPACE_OB = "http://nwcg.gov/services/ross/resource_order/wsdl/1.1";
	public static final String RESOURCE_PORT_OB = "ResourceOrderPort";
	public static final String NOTIFICATION_NAMESPACE_IB = "http://nwcg.gov/services/ross/resource_order/wsdl/1.1";
	public static final String NOTIFICATION_PORT_IB = "DeliverNotificationRespPort";
	public static final String REQUESTRESPONSE_NAMESPACE_IB = "http://nwcg.gov/services/ross/resource_order/wsdl/1.1";
	public static final String REQUESTRESPONSE_PORT_IB = "ROSSDeliveryPort";
	
	public static final String DELETE_CATALOG_ITEM_REQ = "DeleteCatalogItemReq";
	public static final String CATALOG_ITEM_KEY = "CatalogItemKey";
	public static final String CATALOG_ITEM_KEY_PREFIX = "cat";
	public static final String CATALOG_ITEM_KEY_NAMESPACE = "http://nwcg.gov/services/ross/catalog/1.1";
	public static final String CATALOG_TYPE = "CatalogType";
	public static final String CATALOG_TYPE_VAL = "NWCG";
	public static final String CATALOG_ITEM_NAME = "CatalogItemName";
	public static final String XPATH_DELETE_CATALOG_ITEM = "/DeleteCatalogItemReq/CatalogItemKey/CatalogItemName/text()";
	public static final String XPATH_DELETE_CATALOG_ITEM_CODE = "/DeleteCatalogItemReq/CatalogItemKey/CatalogItemCode/text()";
	public static final String XPATH_DELETE_CATALOG_ITEM_KEY = "/DeleteCatalogItemReq/CatalogItem/CatalogItemKey/text()";
	public static final String XPATH_DELETE_CATALOG_ITEM_UNIT_OF_ISSUE = "/DeleteCatalogItemReq/CatalogItem/UnitOfIssue/text()";
	
	public static final String UPDATE_CATALOG_ITEM_REQ = "UpdateCatalogItemReq";
	public static final String XPATH_UPDATE_CATALOG_ITEM_CODE = "/UpdateCatalogItemReq/CatalogItem/CatalogItemCode/text()";
	public static final String XPATH_UPDATE_CATALOG_ITEM_NAME = "/UpdateCatalogItemReq/CatalogItem/CatalogItemName/text()";
	public static final String XPATH_UPDATE_CATALOG_ITEM_KEY = "/UpdateCatalogItemReq/CatalogItem/CatalogItemKey/text()";
	public static final String XPATH_UPDATE_ORDERABLE_QTY = "/UpdateCatalogItemReq/CatalogItem/OrderableWithQuantityInd/text()";
	public static final String XPATH_UPDATE_STANDARD_PACK = "/UpdateCatalogItemReq/CatalogItem/StandardPack/text()";
	public static final String XPATH_UPDATE_TRACKING_REQ = "/UpdateCatalogItemReq/CatalogItem/TrackingRequiredInd/text()";
	public static final String XPATH_UPDATE_UNIT_OF_ISSUE = "/UpdateCatalogItemReq/CatalogItem/UnitOfIssue/text()";
	public static final String XPATH_UPDATE_CATALOG_ITEM = "/UpdateCatalogItemReq/CatalogItemKey/CatalogItemName/text()";
	
	public static final String XPATH_REGISTER_INCIDENT_INTEREST = "RegisterIncidentInterestReq";
	public static final String XPATH_REGISTER_INCIDENT_INTEREST_ID = "/RegisterIncidentInterestReq/IncidentKey/IncidentID/EntityID/text()";
	public static final String XPATH_REGISTER_INCIDENT_INTEREST_YEAR = "/RegisterIncidentInterestReq/IncidentKey/NaturalIncidentKey/YearCreated/text()";
	public static final String XPATH_REGISTER_INCIDENT_INTEREST_SEQUENCE = "/RegisterIncidentInterestReq/IncidentKey/NaturalIncidentKey/SequenceNumber/text()";
	public static final String XPATH_REGISTER_INCIDENT_INTEREST_UNITID_PREFIX = "/RegisterIncidentInterestReq/IncidentKey/NaturalIncidentKey/HostID/UnitIDPrefix/text()";
	public static final String XPATH_REGISTER_INCIDENT_INTEREST_UNITID_SUFFIX = "/RegisterIncidentInterestReq/IncidentKey/NaturalIncidentKey/HostID/UnitIDSuffix/text()";
	public static final String XPATH_REGISTER_INCIDENT_INTEREST_KEY = "/RegisterIncidentInterestReq/IncidentKey/NaturalIncidentKey/IncidentKey/text()";
	public static final String XPATH_ACTIVE_INCIDENT_INTEREST_UNITID_PREFIX = "/SetIncidentActivationReq/IncidentKey/NaturalIncidentKey/HostID/UnitIDPrefix/text()";
	public static final String XPATH_ACTIVE_INCIDENT_INTEREST_UNITID_SUFFIX = "/SetIncidentActivationReq/IncidentKey/NaturalIncidentKey/HostID/UnitIDSuffix/text()";
	public static final String XPATH_REGISTER_INCIDENT_INTEREST_INDICATOR = "/RegisterIncidentInterestReq/RegisterInterestInd/text()";
	public static final String XPATH_SET_INCIDENT_ACTIVE_INDICATOR = "/SetIncidentActivationReq/IsActive/text()";
	
	public static final String XPATH_REGISTER_INCIDENT_NWCGUSERID = "/RegisterIncidentInterestReq/NWCGUSERID/text()";
	
	public static final String XPATH_DEREGISTER_INCIDENT_INTEREST = "RegisterIncidentInterestReq";
	public static final String XPATH_DEREGISTER_INCIDENT_INTEREST_ID = "/RegisterIncidentInterestReq/IncidentKey/IncidentID/EntityID/text()";
	public static final String XPATH_DEREGISTER_INCIDENT_INTEREST_YEAR = "/RegisterIncidentInterestReq/IncidentKey/NaturalIncidentKey/YearCreated/text()";
	public static final String XPATH_DEREGISTER_INCIDENT_INTEREST_SEQUENCE = "/RegisterIncidentInterestReq/IncidentKey/NaturalIncidentKey/SequenceNumber/text()";
	
	public static final String XPATH_ACTIVATE_INCIDENT_INTEREST = "SetIncidentActivationReq";
	public static final String XPATH_ACTIVATE_INCIDENT_INTEREST_ID = "/SetIncidentActivationReq/IncidentKey/IncidentID/EntityID/text()";
	public static final String XPATH_ACTIVATE_INCIDENT_INTEREST_YEAR = "/SetIncidentActivationReq/IncidentKey/NaturalIncidentKey/YearCreated/text()";
	public static final String XPATH_ACTIVATE_INCIDENT_INTEREST_SEQUENCE = "/SetIncidentActivationReq/IncidentKey/NaturalIncidentKey/SequenceNumber/text()";
	public static final String XPATH_ACTIVATE_INCIDENT_INTEREST_KEY = "/SetIncidentActivationReq/IncidentKey/NaturalIncidentKey/IncidentKey/text()";
	
	
	public static final String XPATH_DEACTIVATE_INCIDENT_INTEREST = "SetIncidentActivationReq";
	public static final String XPATH_DEACTIVATE_INCIDENT_INTEREST_ID = "/SetIncidentActivationReq/IncidentKey/IncidentID/EntityID/text()";
	public static final String XPATH_DEACTIVATE_INCIDENT_INTEREST_YEAR = "/SetIncidentActivationReq/IncidentKey/NaturalIncidentKey/YearCreated/text()";
	public static final String XPATH_DEACTIVATE_INCIDENT_INTEREST_SEQUENCE = "/SetIncidentActivationReq/IncidentKey/NaturalIncidentKey/SequenceNumber/text()";
			
	public static final String USER_NAME = "nwcgsys"; // Test user likely to be changed.
	
	public static final String CATALOG = "CATALOG";
    public static final String CATALOG_PACKAGE_NAME = "gov.nwcg.services.ross.catalog._1";
    public static final String INCIDENT_PACKAGE_NAME = "gov.nwcg.services.ross.resource_order._1";
    public static final String RESOURCEORDER_PACKAGE_NAME = "gov.nwcg.services.ross.resource_order._1"; 
    public static final String RESOURCEORDER_NOTIFICATION_PACKAGE_NAME = "gov.nwcg.services.ross.resource_order_notification._1"; 
    public static final String COMMON_PACKAGE_NAME = "gov.nwcg.services.ross.common_types._1"; 
    
    public static final String RESOURCE_ORDER_NAMESPACE = "http://nwcg.gov/services/ross/resource_order/1.1";
    public static final String RESOURCE_ORDER_NOTIFICATION_NAMESPACE = "http://nwcg.gov/services/ross/resource_order_notification/1.1";
    public static final String COMMON_TYPES_NAMESPACE = "http://nwcg.gov/services/ross/common_types/1.1";
	public static final String RESPORCE_ORDER_PORT	  = "ResourceOrderPort";
	public static final String RESOURCE_ORDER_NS_WITH_SCHEMA = "http://nwcg.gov/services/ross/resource_order/1.1 ResourceOrder.xsd";

	public static final String ALERT_MESSAGE_1 = "exception occured while updating message for MESSAGE_TYPE_START";
	public static final String ALERT_MESSAGE_2 = "exception occured while updating message for MESSAGE_TYPE_LATEST";
	public static final String ALERT_MESSAGE_3 = "Exception occured while storing messages during outbound";
	public static final String ALERT_MESSAGE_4 = "Exception occured while storing inbound message";
	public static final String ALERT_MESSAGE_5 = "Exception occured while updating inbound message";
	public static final String ALERT_MESSAGE_6 = "Exception occured while updating message with msg status - PROCESSING";
	public static final String ALERT_MESSAGE_7 = "The ROSS system did not respond.  Some messages were unable to be delivered.";
	public static final String ALERT_MESSAGE_16 = "Soap Fault response received for outbound messages";
	public static final String ALERT_MESSAGE_17 = "Security verification failed for inbound async message";
	
	//from notifications
	public static final String ALERT_MESSAGE_8 = "The Host Id String Part is missing.";
	public static final String ALERT_MESSAGE_9 =	"The Notification Refer to HostId that is not found or not unique.";
	public static final String ALERT_MESSAGE_10 =	"The Notification encounter exception calling getCustomerList";
	public static final String ALERT_MESSAGE_11 = "error invoking NWCGGetIncidentOrderListService";
	public static final String ALERT_MESSAGE_12 =	"Notification Refers to Incident Number that is not found or not unique.";
	public static final String ALERT_MESSAGE_13 =	"Notification encounter exception calling NWCGGetIncidentOrderListService";
	public static final String ALERT_MESSAGE_14 = "Incident Number or Year string part is missing.";
	public static final String ALERT_MESSAGE_15 = "error invoking NWCGChangeIncidentOrderService.";
	
	public static final String DELIVERY_NAMESPACE = "http://nwcg.gov/services/ross/resource_order/1.1";
	public static final String DELIVERY_PORT		= "DeliveryPort";
	
	// inbound response notification message constants
	public static final String DELIVER_NOT_REQ_ROOT     = "DeliverNotificationReq";
	public static final String DELIVER_NOT_RESP_ROOT 	= "DeliverNotificationResp";
	public static final String DELIVER_NOT_XSI_NS	   	= "xmlns:xsi";
	public static final String DELIVER_NOT_XSI_NS_URI	= "http://www.w3.org/2001/XMLSchema-instance";
	public static final String DELIVER_NOT_CDF_NS		= "xmlns:cdf";
	public static final String DELIVER_NOT_CDF_NS_URI	= "http://www.cdf.ca.gov/CAD";
	public static final String DELIVER_NOT_RSCN_NS	= "xmlns:rscn";
	public static final String DELIVER_NOT_RSCN_NS_URI= "http://nwcg.gov/services/ross/resource_notification/1.1";
	public static final String DELIVER_NOT_RON_NS		= "xmlns:ron";
	public static final String DELIVER_NOT_RON_NS_URI	= "http://nwcg.gov/services/ross/resource_order_notification/1.1";
	public static final String DELIVER_OPERATION_RESULTS_ROOT = "<DeliverOperationResults xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:cat=\"http://nwcg.gov/services/ross/catalog/1.1\" xmlns:nwcg=\"http://nwcg.gov/services/ross/common_types/1.1\" xmlns:rch=\"http://nwcg.gov/services/ross/resource_clearinghouse/1.1\" xmlns:ro=\"http://nwcg.gov/services/ross/resource_order/1.1\" xmlns:ron=\"http://nwcg.gov/services/ross/resource_order_notification/1.1\" xmlns:rsc=\"http://nwcg.gov/services/ross/resource/1.1\" xmlns:rscn=\"http://nwcg.gov/services/ross/resource_notification/1.1\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/>";
	
	public static final String DELIVER_OPN_RESULT_ROOT = "DeliverOperationResultsReq";
	public static final String DELIVER_OPN_RESULT_PREFIX = "ns2";
	public static final String DELIVER_OPN_RESULT_URI	= "http://nwcg.gov/services/ross/resource_order/1.1";
	public static final String DELIVER_OPN_RESULT_NS1 = "ns3";
	public static final String DELIVER_OPN_RESULT_NS1_URI = "http://nwcg.gov/services/ross/common_types/1.1";
	public static final String DIST_ID_ROOT = "DistributionID";
	
	public static final String NWCG_DO_NO_AUTH_DB_KEY = "20080324155441821554";
	public static final String NWCG_DO_NO_AUTH_DB_KEY_2 = "9011917122761465";
	
	public static final String ROSS_RET_SUCCESS_CODE = ": ROSS RETURNED SUCCESS";
	public static final String ROSS_RET_FAILURE_CODE = ": ROSS RETURNED FALURE";
	public static final String ROSS_RET_SUCCESS_VALUE = "0";
	public static final String ROSS_RET_FAILURE_VALUE = "-1";
	
	// Get Incident Details XPATH constants
	public static final String XPATH_GET_INCIDENT_MO_SYSTEM_ID = "/GetIncidentReq/MessageOriginator/SystemOfOrigin/SystemID/text()";
	public static final String XPATH_GET_INCIDENT_MO_UNIT_ID_PREFIX = "/GetIncidentReq/MessageOriginator/DispatchUnitID/UnitIDPrefix/text()";
	public static final String XPATH_GET_INCIDENT_MO_UNIT_ID_SUFFIX = "/GetIncidentReq/MessageOriginator/DispatchUnitID/UnitIDSuffix/text()";
	public static final String XPATH_GET_INCIDENT_IK_ENTITY_ID = "/GetIncidentReq/IncidentKey/IncidentID/EntityID/text()";
	public static final String XPATH_GET_INCIDENT_IK_SYSTEM_ID = "/GetIncidentReq/IncidentKey/ApplicationSystem/SystemID/text()";
	public static final String XPATH_GET_INCIDENT_IK_UNIT_ID_PREFIX = "/GetIncidentReq/IncidentKey/NaturalIncidentKey/HostID/UnitIDPrefix/text()";
	public static final String XPATH_GET_INCIDENT_IK_UNIT_ID_SUFFIX = "/GetIncidentReq/IncidentKey/NaturalIncidentKey/HostID/UnitIDSuffix/text()";
	public static final String XPATH_GET_INCIDENT_IK_SEQNO = "/GetIncidentReq/IncidentKey/NaturalIncidentKey/SequenceNumber/text()";
	public static final String XPATH_GET_INCIDENT_IK_YEAR_CREATED = "/GetIncidentReq/IncidentKey/NaturalIncidentKey/YearCreated/text()";
	public static final String XPATH_GET_INCIDENT_IK = "GetIncidentReq";
	public static final String XPATH_GET_INCIDENT_IK_INCIDENT_KEY = "/GetIncidentReq/IncidentKey/NaturalIncidentKey/IncidentKey/text()";

	public static final String XPATH_TRANSFER_INCIDENT_NOTIFICATION="TransferIncidentNotification";	
	public static final String XPATH_TRANSFER_INCIDENT_NOTIFICATION_INCIDENTID_PREFIX="//Incident/IncidentKey/NaturalIncidentKey/HostID/UnitIDPrefix/text()";
	public static final String XPATH_TRANSFER_INCIDENT_NOTIFICATION_INCIDENTID_SUFFIX="//Incident/IncidentKey/NaturalIncidentKey/HostID/UnitIDSuffix/text()";
	public static final String XPATH_TRANSFER_INCIDENT_NOTIFICATION_INCIDENTID_SEQUENCE="//Incident/IncidentKey/NaturalIncidentKey/SequenceNumber/text()";
	public static final String XPATH_TRANSFER_INCIDENT_NOTIFICATION_INCIDENTID_YEAR="//Incident/IncidentKey/NaturalIncidentKey/YearCreated/text()";	
	
	public static final String XPATH_TRANSFER_INCIDENT_NOTIFICATION_TOUNITID_PREFIX="//DispatchFromAndTo/ToUnitID/UnitIDPrefix/text()";
	public static final String XPATH_TRANSFER_INCIDENT_NOTIFICATION_TOUNITID_SUFFIX="//DispatchFromAndTo/ToUnitID/UnitIDSuffix/text()";
	public static final String XPATH_TRANSFER_INCIDENT_NOTIFICATION_FROMUNITID_PREFIX="//DispatchFromAndTo/FromUnitID/UnitIDPrefix/text()";
	public static final String XPATH_TRANSFER_INCIDENT_NOTIFICATION_FROMUNITID_SUFFIX="//DispatchFromAndTo/FromUnitID/UnitIDSuffix/text()";
	
	public static final String XPATH_TRANSFER_INCIDENT_NOTIFICATION_CUSTOMERID_PREFIX="//Incident/IncidentDetails/BillingOrganization/UnitIDPrefix/text()";
	public static final String XPATH_TRANSFER_INCIDENT_NOTIFICATION_CUSTOMERID_SUFFIX="//Incident/IncidentDetails/BillingOrganization/UnitIDSuffix/text()";
	public static final String XPATH_TRANSFER_INCIDENT_NOTIFICATION_CACHEID_PREFIX="//Incident/IncidentDetails/CacheOrganization/UnitIDPrefix/text()";
	public static final String XPATH_TRANSFER_INCIDENT_NOTIFICATION_CACHEID_SUFFIX="//Incident/IncidentDetails/CacheOrganization/UnitIDSuffix/text()";
	
	public static final String XPATH_UPDATE_INCIDENT_KEY_NOTIFICATION="UpdateIncidentKeyNotification";	
	public static final String XPATH_UPDATE_INCIDENT_KEY_NOTIFICATION_OLDINCIDENTID_PREFIX="//OldIncidentKey/NaturalIncidentKey/HostID/UnitIDPrefix/text()";
	public static final String XPATH_UPDATE_INCIDENT_KEY_NOTIFICATION_OLDINCIDENTID_SUFFIX="//OldIncidentKey/NaturalIncidentKey/HostID/UnitIDSuffix/text()";
	public static final String XPATH_UPDATE_INCIDENT_KEY_NOTIFICATION_OLDINCIDENTID_SEQUENCE="//OldIncidentKey/NaturalIncidentKey/SequenceNumber/text()";
	public static final String XPATH_UPDATE_INCIDENT_KEY_NOTIFICATION_OLDINCIDENTID_YEAR="//OldIncidentKey/NaturalIncidentKey/YearCreated/text()";	
	
	public static final String XPATH_UPDATE_INCIDENT_KEY_NOTIFICATION_NEWINCIDENTID_PREFIX="//NewIncidentKey/NaturalIncidentKey/HostID/UnitIDPrefix/text()";
	public static final String XPATH_UPDATE_INCIDENT_KEY_NOTIFICATION_NEWINCIDENTID_SUFFIX="//NewIncidentKey/NaturalIncidentKey/HostID/UnitIDSuffix/text()";
	public static final String XPATH_UPDATE_INCIDENT_KEY_NOTIFICATION_NEWINCIDENTID_SEQUENCE="//NewIncidentKey/NaturalIncidentKey/SequenceNumber/text()";
	public static final String XPATH_UPDATE_INCIDENT_KEY_NOTIFICATION_NEWINCIDENTID_YEAR="//NewIncidentKey/NaturalIncidentKey/YearCreated/text()";		
	public static final String INCIDENT_NAMESPACE_OB = "http://nwcg.gov/services/ross/resource_order/wsdl/1.1";
	public static final String INCIDENT_PORT_OB = "ResourceOrderPort";

	// NWCG Incident Creation related constants: This is used in NWCGIncidentValidateData class
	public static final String PERSON_INFO_SHIP_TO = "YFSPersonInfoShipTo";
	public static final String SHIPPING_ADDR_PRIMARY_IND = "PrimaryInd";
	public static final String INCIDENT_ORDER=	"NWCGIncidentOrder";
	
	// NWCG Incident ROSS and Contacts related constants: This are used in NWCGIncidentGetMiscData class
	public static final String INCIDENT_ORDER_LIST_TAG = "NWCGIncidentOrderList";
	public static final String INCIDENT_ORDER_TAG = "NWCGIncidentOrder";
	public static final String INCIDENT_ORDER_CON_INFO_TAG = "NWCGIncidentOrderConInfo";
	
	public static final String SOAP_FAULT_MESSAGE_NULL_PASSWORD = "Password is null";
	public static final String SOAP_FAULT_MESSAGE_NULL_USERFULLNAME = "User Full Name is null";
	public static final String SOAP_FAULT_MESSAGE_VERIFICATION_FAILED = "User Verification Failed";
	public static final String SOAP_FAULT_MESSAGE_INVALID_METHOD_CALL = "ERROR: Trying to invoke handler synchronously using async web service method";
	public static final String SOAP_FAULT_MESSAGE_INVALID_METHOD_CALL1 = "ERROR: Trying to invoke handler Asynchronously using Sync web service method";
	public static final String SOAP_FAULT_MESSAGE_GETOPERATION_FAILEDRETREIVE ="ERROR: getOperationResults failed to retrieve the message";
	public static final String SOAP_FAULT_MESSAGE_GETOPERATION_FAILEDPROCESS ="ERROR: getOperationResults failed in processing";
	
	public static final String SUCCESS_MESSAGE_CODE		= "100";
	public static final String SUCCESS_MESSAGE_DESC		= "User Verification Successful";
	public static final String SUCCESS_MESSAGE_SEVERITY	= "Information";
	
	//notification handler constants
	public static final String NWCG_MOD_CODE_ROSS_TRANSFER = "ROSS TRANSFER";
	public static final String NWCG_ROSS_TRANSFER = "ROSS Initiated Incident Transfer";
	public static final String TRANSFER_INCIDENT_ERROR = "ROSS Incident Transfer Error";
	public static final String TRANSFER_INCIDENT_SUCCESS = "ROSS Incident Transfered Successfully";
	public static final String NWCG_INCIDENT_NO = "Incident Number";
	public static final String NWCG_INCIDENT_YEAR = "Incident Year";
	public static final String NWCG_INCIDENT_NEWYEAR = "New Incident Year";
	public static final String NWCG_INCIDENT_OLDYEAR = "Old Incident Year";
	public static final String NWCG_INCIDENT_CUSTOMERID = "Customer ID";
    public static final String NWCG_INCIDENT_CACHEID = "Cache ID";
    public static final String NWCG_INCIDENT_OLDUNITID = "Old Unit ID";
    public static final String NWCG_INCIDENT_NEWUNITID = "New Unit ID";
    public static final String UPDATE_INCIDENT_ERROR = "ROSS Incident Update Error";
	public static final String UPDATE_INCIDENT_SUCCESS = "ROSS Incident Updated Successfully";
 	public static final String UPDATE_INCIDENT_ERROR_DESCRIPTION = "UpdateIncidentNotification Failed, One or More Required Data Fields are Missing";
 	
 	// Metadata xml related constants
	public static final String MDTO_DOCUMENT_ROOT = "MetadataTransferObject";
	public static final String MDTO_DISTID = "distributionID";
	public static final String MDTO_USERNAME = "username";
	public static final String MDTO_NAMESPACE = "namespace";
	public static final String MDTO_MSGNAME = "messageName";
	public static final String MDTO_ENTNAME = "EntityName";
	public static final String MDTO_ENTKEY = "EntityKey";
	public static final String MDTO_ENTVALUE = "EntityValue";
	public static final String MDTO_MSGBODY = "messageBody";
	
	// Environment related constants
	public static final String ENV_USER_ID = "ROSSInterface";
	public static final String ENV_PROG_ID = "ROSSInterface";
	
	// Constants used to set response data
	public static final String RESP_CODE = "Code";
	public static final String RESP_SEVERITY = "Severity";
	public static final String RESP_DESCRIPTION = "Description";
	public static final String RESP_RETURN_CODE = "ReturnCode";
	public static final String RESP_CATALOG_TYPE = "CatalogType";
	public static final String RESP_CATALOG_ITEM_NAME = "CatalogItemName";
	public static final String RESP_CATALOG_ITEM_CODE = "CatalogItemCode";
	
	// Constants used by Negative Response Handler
	public static final String RESPONSE_MESSAGE ="ResponseMessage";
	public static final String CODE = "Code";
	public static final String SEVERITY = "Severity";
	public static final String DESCRIPTION = "Description";
	public static final String NATURAL_INCIDENT_KEY = "NaturalIncidentKey";
	public static final String SEQUENCE_NUMBER = "SequenceNumber";
	public static final String YEAR_CREATED = "YearCreated";
	public static final String HOST_ID = "HostID";
	public static final String UNIT_ID_PREFIX = "UnitIDPrefix";
	public static final String UNIT_ID_SUFFIX = "UnitIDSuffix";
	
	// Constants used by handlers
	public static final String REGISTER_INCIDENT_INTEREST_SERVICE = "NWCGPostRegisterIncidentInterestRespService";
	
	// Additional constants
	public static final String INCIDENT_ACTION_CREATE = "CREATE";
	public static final String INCIDENT_ACTION_UPDATE = "UPDATE";
	public static final String INCIDENT_ACTION_MERGE = "MERGE";
	public static final String INCIDENT_ACTION_REASSIGN = "REASSIGN";
	public static final String INCIDENT_ACTION_STR = "IncidentAction";
	public static final String INCIDENT_IS_ACTIVE_ATTR = "IsActive";
	public static final String DIST_ID_ATTR = "DistributionID";
	public static final String ENTITY_KEY = "EntityKey";
	public static final String ENTITY_NAME = "EntityName";
	public static final String ENTITY_VALUE = "EntityValue";
	public static final String INCIDENT_MOD_DESC = "ModificationDesc";
	public static final String INCIDENT_MOD_CODE = "ModificationCode";
	public static final String INCIDENT_KEY = "IncidentKey";
	
	//Possible values for SOAPHeader/MessageContext/distributionType
	public static final String DISTR_TYPE_REQUEST = "Request";
	public static final String DISTR_TYPE_RESPONSE = "Response";
	public static final String DISTR_TYPE_NOTIFICATION = "Notification";	
	
	public static final String OB_GET_OPERATIONS_WAIT_TIME = "GetOperationResultsResponseLag";
	public static final String IB_DELIVER_OPERATION_RESULTS_WAIT_TIME = "DeliverOperationResultsRetryLagTime";	
	
	public static final String CREATE_USER_ID = "Createuserid";
	
	// NAP webServices properties
	public static final String NAP_SSO_PREFIX = "ssoAuth";
	public static final String NAP_SSO_URI = "http://www.nwcg.org/webservices/security/ssoAuthentication";
	public static final String NAP_SECURITY_PREFIX = "wsse";
	public static final String NAP_SECURITY_URI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
	public static final String NAP_TOKEN_ENCODING = "base64";
	public static final String NAP_SOAP_USERNAME_TAG ="UserName";
}