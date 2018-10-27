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

package com.nwcg.icbs.yantra.webservice.util.alert;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStore;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStoreInterface;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGTimeKeyManager;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServiceUtils;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServicesConstant;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGAlert {
	
	private static YFCLogCategory logger = NWCGROSSLogger
			.instance(NWCGAlert.class);

	/**
	 * This method uses retrieveEntityInfo of NWCGMessageStore to get incident or item details. 
	 * IncidentNumber and Year are stored in a single variable entityValue separated by -. 
	 * This method gets the incident number and year from entityValue and puts it in the map
	 * Since, we are planning to merge the OB message services into a single class down the road, I
	 * included both INCIDENT and CATALOG in this method.
	 * @param msgStore
	 * @param msg
	 * @param serviceName
	 * @return
	 * @throws Exception
	 */
	private Map getDetailsForAlert(NWCGMessageStore msgStore, String msg, String serviceName) throws Exception {
		logger.verbose("@@@@@ Entering NWCGAlert::getDetailsForAlert");
		Hashtable<Object, Object> map = msgStore.retrieveEntityInfo(msg,
				serviceName);
		String entityValue = (String) map.get(NWCGAAConstants.ENTITY_VALUE);
		String entityName = (String) map.get(NWCGAAConstants.ENTITY_NAME);
		map.clear();
		if (entityValue != null) {
			int index = entityValue.lastIndexOf(NWCGConstants.DASH);
			if (index != -1) {
				String val1 = entityValue.substring(0, index);
				String val2 = entityValue.substring(index + 1, entityValue
						.length());

				if (entityName
						.equalsIgnoreCase(NWCGAAConstants.INCIDENT_SERVICE_GROUP_NAME)) {
					map.put("IncidentNumber", val1);
					map.put(NWCGConstants.YEAR, val2);
				} else if (entityName.equalsIgnoreCase(NWCGAAConstants.CATALOG)) { // Use Constant
					map.put(NWCGConstants.ITEM_ID, val1);
					map.put(NWCGConstants.BILL_TRANS_UOM, val2);
				}
			}
		} else {
			map = null;
		}
		logger.verbose("@@@@@ Exiting NWCGAlert::getDetailsForAlert");
		return map;
	}

	//	Creates an Alert in NWCG_FAULT queue with Alert properties AlertSet3.1.3 for failed inbound DeliveryOperation
	/*
	 * 	@param map - Map object of reference name value pairs.
	 *  @param alertException - exception that is cause FOR this alert
	 *  map must have set("ExceptionType")
	 *  map must have set(NWCGAAConstants.NAME)
	 *  map must have set "DistributionId"
	 */
	public static void createDeliveryFailedAlert(HashMap<Object, Object> map,
			Exception alertException) throws Exception {
		logger.verbose("@@@@@ Entering NWCGAlert::createDeliveryFailedAlert");
		String messageBody = (String) map.get(NWCGAAConstants.NAME);

		Document msgDoc = XMLUtil.getDocument(messageBody);
		logger
				.verbose("NWCGAlert>>Raising Alert in queue "
						+ NWCGAAConstants.QUEUEID_FAULT + " messageBody="
						+ messageBody);
		map.put("ExceptionType", "DeliverOperationResultsReq");
		NWCGWebServiceUtils.raiseAlert(NWCGAAConstants.QUEUEID_FAULT,
				alertException.getMessage(), msgDoc, alertException, map);
		logger.verbose("@@@@@ Exiting NWCGAlert::createDeliveryFailedAlert");
	}

	//	
	/*
	 * Creates an Alert in NWCG_FAULT queue with Alert properties AlertSet3.1.1 
	 *  for failed ack or SoapFault from ROSS
	 * 	@param map - Map object of reference name value pairs.
	 *  @param alertException - exception that is cause FOR this alert
	 *  map must have set("Alert Type")
	 *  map must have set(NWCGAAConstants.NAME)
	 *  map must have set "DistributionId"
	 */
	public static void createReceivedFaultAlert(HashMap<Object, Object> map,
			Exception alertException) throws Exception {
		logger.verbose("@@@@@ Entering NWCGAlert::createReceivedFaultAlert");
		String messageBody = (String) map
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSG);
		Document msgDoc = XMLUtil.getDocument(messageBody);

		String alertType = (String) map
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME);
		String description = "Received Negative Ack or Fault from ROSS";
		map.put("Description", description);
		map.put("ExceptionType", alertType);

		if (StringUtil.isEmpty(alertType)) {
			alertType = "Alert type TBD";
		}

		logger.debug("NWCGAlert>>Raising Alert in queue "
				+ NWCGAAConstants.QUEUEID_FAULT + " messageBody=" + messageBody
				+ " alerttype=" + alertType);

		NWCGWebServiceUtils.raiseAlert(NWCGAAConstants.QUEUEID_FAULT,
				description, msgDoc, alertException, map);
		logger.verbose("@@@@@ Exiting NWCGAlert::createReceivedFaultAlert");
	}

	/*
	 * from inbound asynch traffic 
	 * If security verification fails sends Soap Fault response back to ROSS and set status to AUTHORIZATION_FAILED. 
	 *     Creates an Alert in NWCG_INBOUND_AUTHORIZATION_FAILED queue. 
	 *     Alert properties are AlertSet3.2.1 :
	 *  Alert Type=SoapHeader/MessageContext/MessageName
	 Details= "Authorization Failed"
	 DistributionId=SoapHeader/MessageContext/DistributionId received
	 Reference Map=Fault Actor, FaultCode, FaultString from SoapFault sent to ROSS
	 user/pass:”+username/psw from Header
	 */
	public static void raiseSecurityFailedAlert(HashMap map) {
		logger.verbose("@@@@@ Entering NWCGAlert::raiseSecurityFailedAlert");
		HashMap<Object, Object> alertmap = new HashMap<Object, Object>();
		String messageName = (String) map
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME);
		alertmap.put("ExceptionType", messageName);
		alertmap.put("Description", "Authorization Failed");
		alertmap.put("DistributionId", map
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID));
		String msg = (String) map
				.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY);
		//alertmap.put(NWCGAAConstants.NAME,msg);
		alertmap.put("Return Code",
				NWCGWebServicesConstant.ACK_FAILURE_MESSAGE_CODE);
		alertmap.put("code", NWCGWebServicesConstant.ACK_FAILURE_MESSAGE_CODE);
		alertmap.put("Severity",
				NWCGWebServicesConstant.ACK_FAILURE_MESSAGE_SEVERITY);

		if (messageName.endsWith("otification")) {
			alertmap.put("DistributionId", "");
		}

		try {
			//Document msgDoc=XMLUtil.createDocument(msg);
			Document msgDoc = XMLUtil.getDocument(msg);
			NWCGWebServiceUtils.raiseAlert(
					NWCGAAConstants.QUEUEID_NWCG_INBOUND_AUTHORIZATION_FAILED,
					NWCGAAConstants.ALERT_MESSAGE_17, msgDoc, null, alertmap);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		logger.verbose("@@@@@ Exiting NWCGAlert::raiseSecurityFailedAlert");
	}

	/*
	 * from outbound asynch traffic AlertSet 3.1.1
	 * from getOperations properties are AlertSet3.1.4a. 
	 */
	public static void raiseSoapFaultAlert(HashMap<Object, Object> msgMap) {
		logger.verbose("@@@@@ Entering NWCGAlert::raiseSoapFaultAlert");
		String msg = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSG);
		logger.verbose("@@@@@ msg :: " + msg);
		String messageName = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME);
		logger.verbose("@@@@@ messageName :: " + messageName);
		msgMap.put("ExceptionType", messageName);
		logger.verbose("@@@@@ After msgMap.put... ");
		try {
			Document msgDoc = XMLUtil.getDocument(msg);
			logger.verbose("@@@@@ msgDoc :: " + XMLUtil.getXMLString(msgDoc));
			NWCGWebServiceUtils.raiseAlert(NWCGAAConstants.QUEUEID_FAULT, NWCGAAConstants.ALERT_MESSAGE_16, msgDoc, null, msgMap);
			logger.verbose("@@@@@ After NWCGWebServiceUtils.raiseAlert... ");
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception..." + e);
			logger.error(e.getLocalizedMessage(), e);
		}
		logger.verbose("@@@@@ Exiting NWCGAlert::raiseSoapFaultAlert");
	}

	/**
	 * Used by NWCGGetOperationResultsOB whan Sterling side fail to process responce msg
	 * Creates an Alert in NWCG_FAULT queue. Alert properties are AlertSet3.1.4a.
	 */
	public static void raiseForcedProcessFaultAlert(HashMap msgMap, Document sts) {
	}

	/**
	 * Alert properties are AlertSet3.1.4a - from getOparations
	 */
	public static void raiseForcedProcessFailedAlert(
			HashMap<Object, Object> msgMap, Throwable e) {
		logger.verbose("@@@@@ Entering NWCGAlert::raiseForcedProcessFailedAlert");
		String msg = (String) msgMap.get("message");
		msgMap.put("Description", "Error Retuned from Sterling");

		try {
			Document msgDoc = XMLUtil.createDocument(msg);
			NWCGWebServiceUtils.raiseAlert(NWCGAAConstants.QUEUEID_FAULT,
					NWCGAAConstants.ALERT_MESSAGE_3, msgDoc, e, msgMap);
		} catch (Exception ee) {
			//logger.printStackTrace(ee);
		}
		logger.verbose("@@@@@ Exiting NWCGAlert::raiseForcedProcessFailedAlert");
	}

	/*
	 * Alert properties are AlertSet3.1.4b - from getOparations
	 */
	public static void raiseGetOperationFailedAlert(
			HashMap<Object, Object> msgMap, Throwable e) {
		logger.verbose("@@@@@ Entering NWCGAlert::raiseGetOperationFailedAlert");
		String msg = (String) msgMap.get("message");
		msgMap.put("Description", "Error Retuned from Sterling");

		try {
			Document msgDoc = XMLUtil.createDocument(msg);
			NWCGWebServiceUtils.raiseAlert(NWCGAAConstants.QUEUEID_FAULT,
					NWCGAAConstants.ALERT_MESSAGE_3, msgDoc, e, msgMap);
		} catch (Exception ee) {
			//logger.printStackTrace(ee);
		}
		logger.verbose("@@@@@ Exiting NWCGAlert::raiseGetOperationFailedAlert");
	}

	/*
	 * used when internal error happened in Sterling -AlertSet 3.2.3
	 */
	public static void raiseInternalErrorAlert(HashMap<Object, Object> msgMap) {
		logger.verbose("@@@@@ Entering NWCGAlert::raiseInternalErrorAlert");
		String msg = (String) msgMap.get("message");
		msgMap.put("Description", "Error Retuned from Sterling");

		try {
			Document msgDoc = XMLUtil.createDocument(msg);
			NWCGWebServiceUtils.raiseAlert(NWCGAAConstants.QUEUEID_INCIDENT,
					NWCGAAConstants.ALERT_MESSAGE_3, msgDoc, null, msgMap);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		logger.verbose("@@@@@ Exiting NWCGAlert::raiseInternalErrorAlert");
	}

	/**
	 * used when internal error happened in Sterling -AlertSet 3.1.5
	 */
	public static void raiseRetryLimitAlert(HashMap<Object, Object> msgMap,
			String msgName) {
		logger.verbose("@@@@@ Entering NWCGAlert::raiseRetryLimitAlert");
		String messageBody = (String) msgMap.get(NWCGAAConstants.NAME);
		msgMap.put("ExceptionType", msgName);
		msgMap.put("Description", "Retry Count Limit Reached");

		try {
			Document msgDoc = XMLUtil.createDocument(messageBody);
			NWCGWebServiceUtils.raiseAlert(NWCGAAConstants.QUEUEID_INCIDENT,
					NWCGAAConstants.ALERT_MESSAGE_3, msgDoc, null, msgMap);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		logger.verbose("@@@@@ Exiting NWCGAlert::raiseRetryLimitAlert");
	}

	//creates alert in IB_NOTIFICATION queue when notification is successfully processed
	public static void raiseNotificationAlert(String msgName,
			String messageBody, String distID) {
		logger.verbose("@@@@@ Entering NWCGAlert::raiseNotificationAlert");
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		map
				.put("Return Code",
						NWCGWebServicesConstant.ACK_SUCCESS_MESSAGE_CODE);
		map.put("code", NWCGWebServicesConstant.ACK_SUCCESS_MESSAGE_CODE);
		map.put("Severity",
				NWCGWebServicesConstant.ACK_SUCCESS_MESSAGE_SEVERITY);
		map
				.put("Description",
						NWCGWebServicesConstant.ACK_SUCCESS_MESSAGE_DESC);
		map.put("DistributionId", distID);
		map.put(NWCGAAConstants.NAME, messageBody);
		map.put("ExceptionType", msgName);

		try {
			NWCGWebServiceUtils.raiseAlert(
					NWCGAAConstants.QUEUEID_NWCG_INBOUND_AUTHORIZATION_FAILED,
					NWCGWebServicesConstant.ALERT_NOTIFICATION, XMLUtil
							.getDocument(messageBody), null, map);

		} catch (ParserConfigurationException pce) {
			logger.error("ParserConfigurationException while creating XMLDocument",pce);
		} catch (IOException ioe) {
			logger.error("IOException while creating XMLDocument",ioe);
		} catch (SAXException e) {
			logger.error("SAXException while creating XMLDocument",e);
		}
		logger.verbose("@@@@@ Exiting NWCGAlert::raiseNotificationAlert");
	}
}
