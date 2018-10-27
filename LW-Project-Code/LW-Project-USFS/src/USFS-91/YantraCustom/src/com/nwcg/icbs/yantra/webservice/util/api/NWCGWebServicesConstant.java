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

package com.nwcg.icbs.yantra.webservice.util.api;

import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

/**
 * @author jvishwakarma All the constants required ONLY for 6.1 should be
 *         defined here
 * 
 */
public class NWCGWebServicesConstant {

	public static final String XPATH_GET_INCIDENT_IK_INCIDENT_KEY = "/GetIncidentReq/IncidentKey/NaturalIncidentKey/IncidentKey/text()";

	public static final String XPATH_GET_INCIDENT_IK = "GetIncidentReq";

	public static final String XPATH_REGISTER_INCIDENT_INTEREST_KEY = "/RegisterIncidentInterestReq/IncidentKey/NaturalIncidentKey/IncidentKey/text()";

	public static final String XPATH_ACTIVATE_INCIDENT_INTEREST_KEY = "/SetIncidentActivationReq/IncidentKey/NaturalIncidentKey/IncidentKey/text()";

	public static final String XPATH_CREATE_CATALOG_ITEM_KEY = "/CreateCatalogItemReq/CatalogItem/CatalogItemKey/text()";

	public static final String XPATH_DELETE_CATALOG_ITEM_KEY = "/DeleteCatalogItemReq/CatalogItem/CatalogItemKey/text()";

	public static final String XPATH_UPDATE_CATALOG_ITEM_KEY = "/UpdateCatalogItemReq/CatalogItem/CatalogItemKey/text()";

	public static final String XPATH_DELETE_CATALOG_ITEM_CODE = "/DeleteCatalogItemReq/CatalogItemKey/CatalogItemCode/text()";

	public static final String XPATH_DELETE_CATALOG_ITEM_UNIT_OF_ISSUE = "/DeleteCatalogItemReq/CatalogItem/UnitOfIssue/text()";

	public static final String SOAP_FAULT_MESSAGE_PROCESSING_NOT_SUPPORTED = "ERROR: Though async processing is requested but it is not supported by the service";

	public static final String SOAP_FAULT_MESSAGE_DOES_NOT_EXIST = "ERROR: DistID and Message Name combination requested does not exists in the database";

	public static final String SOAP_FAULT_XS_ANY_ELM_NOT_FOUND = "Did not find DeliverOperationResultsReq/DistributionID/xsAnyElement";

	// SOAP Header elements in Context
	public static final String SOAP_HEADER_ELEMENT_SERVICENAME = "ServiceNameElement";

	public static final String SOAP_HEADER_NOTIFICATION = "DeliverNotificationReq";

	public static final String SOAP_HEADER_DELIVERY = "DeliverOperationResultsReq";

	public static final String SOAP_HEADER_ELEMENT_NOTIFIEDSYSTEM = "NotifiedSystem";

	public static final String SOAP_HEADER_SERVICE_MSGACK = "MessageAcknowledgement";

	public static final String SERVICE_NAME_PING = "ServicePingReq";

	public static final String SOAP_ELEMENT_MESSAGEBODY = "MessageBody";

	public static final String SOAP_HEADER_ELEMENT_SECURITY = "Security";

	public static final String SOAP_HEADER_ELEMENT_PASSWORD = "Password";

	public static final String SOAP_HEADER_ELEMENT_MESSAGENAME = "messageName";

	public static final String SOAP_HEADER_ELEMENT_DISTID = "distributionID";

	public static final String SOAP_HEADER_ELEMENT_SYSTEMID = "SystemID";

	public static final String SOAP_HEADER_ELEMENT_CUSTOMPROP = "customProperties";

	public static final String SOAP_HEADER_ELEMENT_SECURITYSESSIONID = "securitySessionID";

	public static final String SOAP_HEADER_ELEMENT_NOTIFICATIONOBJ = "notificationObj";

	public static final String SOAP_HEADER_ELEMENT_PROCESSSYNC = "processSynchronously";

	public static final String SOAP_HEADER_ELEMENT_USER = "username";

	public static final String SOAP_HEADER_ELEMENT_NAMESPACE = "namespace";

	public static final String SOAP_HEADER_ELEMENT_DIST_TYPE = "distributionType";

	// User defined SOAP Message constant
	public static final String SOAP_MESSAGE_IB_RESP = "IB_RESPONSE";

	// services invoked for ansync processing
	public static final String NWCG_POST_IB_NOTIFICATION_SERVICE = "NWCGPostIBNotificationService"; // TB
																									// added

	public static final String NWCG_POST_IB_MESSAGE_SERVICE = "NWCGPostIBMessageService"; // TB
																							// added

	public static final String NWCG_CALL_IB_API_SERVICE = "NWCGSyncAPICallService"; // TB
																					// added

	public static final String NWCG_POST_GET_OPERATIONS_SERVICE = "NWCGPostGetOperationService";

	// handles all inbound notifications
	// public static final String QUEUEID_INBOUND_NOTIFICATION =
	// "NWCG_IB_NOTIFICATION";
	public static final String QUEUEID_INBOUND_NOTIFICATION = "NWCG_IT_ADMIN";

	public static final String ALERT_NOTIFICATION = "ROSS Notification received for Item - ";

	// ack message constants
	public static final String ACK_SUCCESS_MESSAGE_CODE = "0";

	public static final String ACK_SUCCESS_MESSAGE_DESC = "Message sent to ICBSR for processing";

	public static final String ACK_SUCCESS_MESSAGE_SEVERITY = "Information";

	public static final String ACK_FAILURE_MESSAGE_CODE = "-1";

	public static final String ACK_FAILURE_MESSAGE_DESC = "User Verification Fail";

	public static final String ACK_FAILURE_MESSAGE_SEVERITY = "Error";

	// operations for ResourceOrder interface
	public static final int OPTYPE_REG_INCIDENT_INTREST = 1;

	public static final int OPTYPE_SET_INCIDENT_ACTIVE = 2;

	public static final int OPTYPE_PLACE_RES_REQUEST = 3;

	public static final int OPTYPE_RETURN_NATIONAL_RESOURCE_ITEM = 4;

	public static final int OPTYPE_SEND_UPDATE_REQUEST = 5;

	public static final int OPTYPE_SEND_UTF_REQUEST = 6;

	public static final int OPTYPE_SEND_FILL_REQUEST = 7;

	public static final int OPTYPE_getCatalogItem = 8;

	public static final int OPTYPE_updateCatalogItem = 9;

	public static final int OPTYPE_deleteCatalogItem = 10;

	public static final int OPTYPE_createCatalogItem = 11;

	public static final int OPTYPE_GET_OPERATION_RESULT = 12;

	// A&A retuned soap fault strings
	public static final String SOAP_FAULT_MESSAGE_GETOPERATION_FAILEDRETREIVE = "ERROR: getOperationResults failed to retrieve the message.";

	public static final String SOAP_FAULT_MESSAGE_GETOPERATION_FAILEDPROCESS = "ERROR: getOperationResults failed in processing";

	public static final String SOAP_FAULT_MESSAGE_GETOPERATION_EMPTY_DISTID = "ERROR: can not process get operation results without distribution id";

	public static final String XMLENCODING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	// Constants to define the type of operation
	public static final int IBTYPE_DELIVERY = 1;

	public static final int OBTYPE_GET = 2;

	public static final int OBTYPE_SYNC = 3;

	public static final int OBTYPE_ASYNC = 4;

	public static final int OBTYPE_NOTIFICATION = 5;

	public static final String DELIVER_OPERATION_RESULTS_REQ = "DeliverOperationResultsReq";

	// A&A web interfaces operations
	public static java.util.HashMap OBInterfaceMap = new java.util.HashMap<String, Integer>() {

		private static final long serialVersionUID = 362498820763181265L;
		{
			put("RegisterIncidentInterestReq", new Integer(
					NWCGWebServicesConstant.OPTYPE_REG_INCIDENT_INTREST));
			put("SetIncidentActivationReq", new Integer(
					NWCGWebServicesConstant.OPTYPE_SET_INCIDENT_ACTIVE));
			put("placeResourceRequestReq", new Integer(
					NWCGWebServicesConstant.OPTYPE_PLACE_RES_REQUEST));

			put(
					"returnNationalResourceItemReq",
					new Integer(
							NWCGWebServicesConstant.OPTYPE_RETURN_NATIONAL_RESOURCE_ITEM));
			put("sendUpdateRequestReq", new Integer(
					NWCGWebServicesConstant.OPTYPE_SEND_UPDATE_REQUEST));
			put("sendUTFRequestReq", new Integer(
					NWCGWebServicesConstant.OPTYPE_SEND_UTF_REQUEST));
			put("sendFillRequestReq", new Integer(
					NWCGWebServicesConstant.OPTYPE_SEND_FILL_REQUEST));
			put("getOperationResultsReq", new Integer(
					NWCGWebServicesConstant.OPTYPE_GET_OPERATION_RESULT));
			put("getCatalogItemReq", new Integer(
					NWCGWebServicesConstant.OPTYPE_getCatalogItem));
			put("updateCatalogItemReq", new Integer(
					NWCGWebServicesConstant.OPTYPE_updateCatalogItem));
			put("DeleteCatalogItemReq", new Integer(
					NWCGWebServicesConstant.OPTYPE_deleteCatalogItem));
			put("CreateCatalogItemReq", new Integer(
					NWCGWebServicesConstant.OPTYPE_createCatalogItem));
		}

	};

	public static final String NOTF_SUCCESS_MESSAGE_CODE = "0";

	public static final String NOTF_SUCCESS_MESSAGE_DESC = "Notification received";

	public static final String NOTF_SUCCESS_MESSAGE_SEVERITY = "Information";

	public static final String NOTF_FAILURE_MESSAGE_CODE = "-1";

	public static final String NOTF_FAILURE_MESSAGE_DESC = "Unable to receive notification";

	public static final String NOTF_FAILURE_MESSAGE_SEVERITY = "Information";
}