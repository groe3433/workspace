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

package com.nwcg.icbs.yantra.ib.read.handler;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;

import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.ib.read.handler.util.NotificationCommonUtilities;

/**
 * transfer incident notification handler
 * 
 * This will do the following business logic
 * - Parses and get the old and new incident 
 * - Checks if the old incident exists and is in registered status. If not raise an alert 
 * - Checks if new incident exists. If it exists, raise an alert 
 * - Creates the new incident 
 * - Updates the old incident as inactive, lock and registerinterestinross to N
 * 
 * There are 2 types of "Old" incidents: 
 * - ones that exist. 
 * - and ones that do NOT exist.
 * 
 * There are 3 types of "New" incidents:
 * - New Incidents that do NOT exist. 
 * - New Incidents that Exist. 
 * - New Incidents that Exist and are Locked and Inactive.
 * - - As of July 9, 2015 - a call with all applications identified that ROSS does NOT allow this. Only this behavior IS allowed in WildCAD far up the stream.
 * - - NO ICBS change is required at this time to allow this behavior. If it is, then need to implement CR 1720. Code will be in QC. 
 * - - (CR 1720 will be marked "Deferred" until ROSS/WildCAD come to a decision, and USFS informs us IF ICBS is required to make a change)
 **/
public class NWCGUpdateIncidentKeyNotificationHandler implements NWCGMessageHandlerInterface {

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGUpdateIncidentKeyNotificationHandler.class);

	private String oldIncNo = "";
	private String oldIncYr = "";
	private String newIncNo = "";
	private String newIncYr = "";
	private String distId = "";
	private String cacheId = "";
	private String newIncId = "";

	public Document process(YFSEnvironment env, Document doc) {
		logger.verbose("@@@@@ Entering NWCGUpdateIncidentKeyNotificationHandler::process @@@@@");
		logger.verbose("@@@@@ doc :: " + XMLUtil.getXMLString(doc));
		// Parse and initialize the variables
		distId = doc.getDocumentElement().getAttribute(NWCGConstants.INC_NOTIF_DIST_ID_ATTR);
		logger.verbose("@@@@@ distId :: " + distId);
		parseAndSetIncidentKeys(doc);
		// Check if source incident exists and if it is registered
		Document oldIncDtls = checkIfOldIncidentExistsAndItsRegisterStatus(env, doc);
		if (oldIncDtls == null) {
			logger.verbose("@@@@@ old Incident does not exist, nothing to replace...");
			logger.verbose("@@@@@ Exiting NWCGUpdateIncidentKeyNotificationHandler::process @@@@@");
			return doc;
		}
		// If new incident exists, then do not process it as we want to create the incident with the new incident number
		Document newIncDtls = checkIfIncidentExists(env, doc);
		if (newIncDtls != null) {
			logger.verbose("@@@@@ new Incident does exist and it is UNLOCKED and ACTIVE, nothing to do here...");
			logger.verbose("@@@@@ Exiting NWCGUpdateIncidentKeyNotificationHandler::process @@@@@");
			return doc;
		}
		logger.verbose("@@@@@ New incident does not exists, need to create it and update the old one...");
		try {
			// Update oldIncDtls with new incident key
			Element oldIncDtlRootElm = oldIncDtls.getDocumentElement();
			oldIncDtlRootElm.setAttribute(NWCGConstants.INCIDENT_NO_ATTR, newIncNo);
			oldIncDtlRootElm.setAttribute(NWCGConstants.INCIDENT_ID_ATTR, newIncId);
			oldIncDtlRootElm.setAttribute(NWCGConstants.YEAR_ATTR, newIncYr);
			// Blanking out the incident key
			oldIncDtlRootElm.setAttribute(NWCGConstants.INCIDENT_KEY, "");
			String replacedIncidentNo1 = oldIncDtlRootElm.getAttribute(NWCGConstants.REPLACED_INCIDENT_NO);
			String replacedIncidentYr1 = oldIncDtlRootElm.getAttribute(NWCGConstants.REPLACED_INCIDENT_YEAR);
			String replacedIncidentNo2 = oldIncDtlRootElm.getAttribute(NWCGConstants.REPLACED_INCIDENT_NO_2);
			String replacedIncidentYr2 = oldIncDtlRootElm.getAttribute(NWCGConstants.REPLACED_INCIDENT_YEAR_2);
			oldIncDtlRootElm.setAttribute(NWCGConstants.REPLACED_INCIDENT_NO, oldIncNo);
			oldIncDtlRootElm.setAttribute(NWCGConstants.REPLACED_INCIDENT_YEAR, oldIncYr);
			if (replacedIncidentNo1 != null && (replacedIncidentNo1.trim().length() > 2)) {
				oldIncDtlRootElm.setAttribute(NWCGConstants.REPLACED_INCIDENT_NO_2, replacedIncidentNo1);
			}
			if (replacedIncidentYr1 != null && (replacedIncidentYr1.trim().length() > 2)) {
				oldIncDtlRootElm.setAttribute(NWCGConstants.REPLACED_INCIDENT_YEAR_2, replacedIncidentYr1);
			}
			if (replacedIncidentNo2 != null && (replacedIncidentNo2.trim().length() > 2)) {
				oldIncDtlRootElm.setAttribute(NWCGConstants.REPLACED_INCIDENT_NO_3, replacedIncidentNo2);
			}
			if (replacedIncidentYr2 != null && (replacedIncidentYr2.trim().length() > 2)) {
				oldIncDtlRootElm.setAttribute(NWCGConstants.REPLACED_INCIDENT_YEAR_3, replacedIncidentYr2);
			}
			NodeList timeOfNotNL = doc.getElementsByTagName(NWCGConstants.INC_NOTIF_TIME_OF_NOTIF_ELEMENT);
			// There is only one occurence of TimeOfNotification and it is a mandatory element. So, directly getting the 0 element
			String timeOfNot = timeOfNotNL.item(0).getTextContent();
			oldIncDtlRootElm.setAttribute(NWCGConstants.LAST_UPDATED_FROM_ROSS, timeOfNot);
			logger.verbose("@@@@@ oldIncDtls : " + XMLUtil.getXMLString(oldIncDtls));
			CommonUtilities.invokeService(env, NWCGConstants.SVC_CREATE_INCIDENT_ORDER_SVC, oldIncDtls);
		} catch (Exception e) {
			// raise an alert in NWCG_INCIDENT_FAILURE - Unable to update 
			logger.error("!!!!! Caught General Exception :: while calling " + NWCGConstants.SVC_CREATE_INCIDENT_ORDER_SVC + ". Message : " + e.getMessage());
			e.printStackTrace();
			String errDesc = "Failed while creating new incident " + newIncNo + " with the old incident " + oldIncNo + " details in ICBSR system : " + e.getMessage();
			getDataAndRaiseAlert(env, doc, errDesc, false);
			logger.error("!!!!! Exiting NWCGUpdateIncidentKeyNotificationHandler::process (3) @@@@@");
			return doc;
		}
		// Update oldInc with incident status of inactive, lock, mod reason (and code) and deregister status (no webservice call)
		updateOldIncidentDtls(env, doc);
		String desc = "Incident Key Notification Received for old incident " + oldIncNo + " and " + oldIncYr + ".  from ROSS." + "New incident " + newIncNo + " with year " + newIncYr + " is created successfully in ICBS-R";
		getDataAndRaiseAlert(env, doc, desc, true);
		logger.verbose("@@@@@ Exiting NWCGUpdateIncidentKeyNotificationHandler::process (4) @@@@@");
		return doc;
	}

	/**
	 * This method calls NotificationCommonUtilities class to parse and set the
	 * incident key for old and new incident
	 * 
	 * @param doc
	 */
	public void parseAndSetIncidentKeys(Document doc) {
		logger.verbose("@@@@@ Entering NWCGUpdateIncidentKeyNotificationHandler::parseAndSetIncidentKeys @@@@@");
		HashMap oldIncHashMap = NotificationCommonUtilities.parseAndRtnIncidentKey(doc.getElementsByTagName("ron:OldIncidentKey"));
		HashMap newIncHashMap = NotificationCommonUtilities.parseAndRtnIncidentKey(doc.getElementsByTagName("ron:NewIncidentKey"));
		oldIncNo = (String) oldIncHashMap.get(NWCGConstants.INCIDENT_NO_ATTR);
		logger.verbose("@@@@@ oldIncNo :: " + oldIncNo);
		oldIncYr = (String) oldIncHashMap.get(NWCGConstants.YEAR_ATTR);
		logger.verbose("@@@@@ oldIncYr :: " + oldIncYr);
		newIncNo = (String) newIncHashMap.get(NWCGConstants.INCIDENT_NO_ATTR);
		logger.verbose("@@@@@ newIncNo :: " + newIncNo);
		newIncId = (String) newIncHashMap.get(NWCGConstants.INCIDENT_ID_ATTR);
		logger.verbose("@@@@@ newIncId :: " + newIncId);
		newIncYr = (String) newIncHashMap.get(NWCGConstants.YEAR_ATTR);
		logger.verbose("@@@@@ newIncYr :: " + newIncYr);
		logger.verbose("@@@@@ Exiting NWCGUpdateIncidentKeyNotificationHandler::parseAndSetIncidentKeys @@@@@");
	}

	/**
	 * This method checks if old incident exists in ICBSR system. If it exists,
	 * check if incident is registered in ROSS. Raise alert in all other
	 * situations
	 * 
	 * @param env
	 * @param doc
	 * @return
	 */
	private Document checkIfOldIncidentExistsAndItsRegisterStatus(YFSEnvironment env, Document doc) {
		logger.verbose("@@@@@ Entering NWCGUpdateIncidentKeyNotificationHandler::checkIfOldIncidentExistsAndItsRegisterStatus @@@@@");
		Document nwcgIncOP = null;
		try {
			Document nwcgIncDoc = XMLUtil.createDocument(NWCGConstants.INCIDENT_ORDER_ELEM);
			Element nwcgRootElm = nwcgIncDoc.getDocumentElement();
			nwcgRootElm.setAttribute(NWCGConstants.INCIDENT_NO_ATTR, oldIncNo);
			nwcgRootElm.setAttribute(NWCGConstants.YEAR_ATTR, oldIncYr);
			try {
				logger.verbose("@@@@@ nwcgIncDoc : " + XMLUtil.getXMLString(nwcgIncDoc));
				nwcgIncOP = CommonUtilities.invokeService(env, NWCGConstants.SVC_GET_INCIDENT_ORDER_SVC, nwcgIncDoc);
				logger.verbose("@@@@@ nwcgIncOP : " + XMLUtil.getXMLString(nwcgIncOP));
			} catch (Exception e) {
				logger.error("!!!!! Caught General Exception :: while calling service : " + NWCGConstants.SVC_GET_INCIDENT_ORDER_SVC + ", Error Message : " + e);
				e.printStackTrace();
				String errDesc = "Incident number " + oldIncNo + " with year " + oldIncYr + " is not present in ICBSR. Update Incident Key " + "Notification for this incident is not processed";
				getDataAndRaiseAlert(env, doc, errDesc, false);
				logger.error("!!!!! Exiting NWCGUpdateIncidentKeyNotificationHandler::checkIfOldIncidentExistsAndItsRegisterStatus (1) @@@@@");
				return null;
			}
			if (nwcgIncOP != null) {
				// Check register status
				Element opRootElm = nwcgIncOP.getDocumentElement();
				cacheId = opRootElm.getAttribute(NWCGConstants.PRIMARY_CACHE_ID);
				logger.verbose("@@@@@ Cache ID : " + cacheId);
				String incRegStatus = opRootElm.getAttribute(NWCGConstants.INCIDENT_REG_INT_IN_ROSS_ATTR);
				logger.verbose("@@@@@ incRegStatus : " + incRegStatus);
				if ((incRegStatus == null) || !incRegStatus.equalsIgnoreCase(NWCGConstants.YES)) {
					String errDesc = "Incident " + oldIncNo + " exists in ICBSR, but is not registered with ROSS. " + "Update Incident Key Notification for this incident is not processed";
					// Raise exception with message "Incident exists, but is not registered with ROSS....
					getDataAndRaiseAlert(env, doc, errDesc, false);
					nwcgIncOP = null;
				}
			} else {
				// Code should never come here. Code will come here if service returns NULL instead of exception (if incident doesn't exists) - This is the current Yantra behavior
				String errDesc = "Incident number " + oldIncNo + " with year " + oldIncYr + " is not present in ICBSR. Update Incident Key " + "Notification for this incident is not processed";
				getDataAndRaiseAlert(env, doc, errDesc, false);
				nwcgIncOP = null;
			}
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException :: " + pce);
			pce.printStackTrace();
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			e.printStackTrace();
		}
		logger.verbose("@@@@@ Exiting NWCGUpdateIncidentKeyNotificationHandler::checkIfOldIncidentExistsAndItsRegisterStatus (2) @@@@@");
		return nwcgIncOP;
	}

	/**
	 * This method checks if new incident exists in ICBSR system. If it exists,
	 * raise an alert that the new incident already exists
	 * 
	 * @param env
	 * @param doc
	 * @return
	 */
	private Document checkIfIncidentExists(YFSEnvironment env, Document doc) {
		logger.verbose("@@@@@ Entering NWCGUpdateIncidentKeyNotificationHandler::checkIfIncidentExists @@@@@");
		Document nwcgIncOP = null;
		try {
			Document nwcgIncDoc = XMLUtil.createDocument(NWCGConstants.INCIDENT_ORDER_ELEM);
			Element nwcgRootElm = nwcgIncDoc.getDocumentElement();
			nwcgRootElm.setAttribute(NWCGConstants.INCIDENT_NO_ATTR, newIncNo);
			nwcgRootElm.setAttribute(NWCGConstants.YEAR_ATTR, newIncYr);
			try {
				logger.verbose("@@@@@ nwcgIncDoc : " + XMLUtil.getXMLString(nwcgIncDoc));
				nwcgIncOP = CommonUtilities.invokeService(env, NWCGConstants.SVC_GET_INCIDENT_ORDER_DTLS_ONLY_SVC, nwcgIncDoc);
				logger.verbose("@@@@@ nwcgIncOP : " + XMLUtil.getXMLString(nwcgIncOP));
			} catch (Exception e) {
				logger.error("!!!!! Caught General Exception :: while calling service : " + NWCGConstants.SVC_GET_INCIDENT_ORDER_DTLS_ONLY_SVC + ", Error Message : " + e);
				e.printStackTrace();
				nwcgIncOP = null;
			}
			if (nwcgIncOP != null) {
				String errDesc = "New incident " + newIncNo + " with Year " + newIncYr + " already exists in ICBSR system. " + "Update Incident Key Notification is not processed";
				getDataAndRaiseAlert(env, doc, errDesc, false);
			}
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException :: " + pce);
			pce.printStackTrace();
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			e.printStackTrace();
		}
		logger.verbose("@@@@@ Exiting NWCGUpdateIncidentKeyNotificationHandler::checkIfIncidentExists @@@@@");
		return nwcgIncOP;
	}

	/**
	 * This method should update the old incident as inactive, locked, register
	 * interest in ross as inactive and
	 * 
	 * @param env
	 * @param doc
	 * @return
	 */
	public Document updateOldIncidentDtls(YFSEnvironment env, Document doc) {
		logger.verbose("@@@@@ Entering NWCGUpdateIncidentKeyNotificationHandler::updateOldIncidentDtls @@@@@");
		Document nwcgIncOP = null;
		try {
			Document nwcgIncDoc = XMLUtil.createDocument(NWCGConstants.INCIDENT_ORDER_ELEM);
			Element nwcgRootElm = nwcgIncDoc.getDocumentElement();
			nwcgRootElm.setAttribute(NWCGConstants.INCIDENT_NO_ATTR, oldIncNo);
			nwcgRootElm.setAttribute(NWCGConstants.YEAR_ATTR, oldIncYr);
			nwcgRootElm.setAttribute(NWCGConstants.IS_ACTIVE, NWCGConstants.NO);
			nwcgRootElm.setAttribute(NWCGConstants.INCIDENT_REG_INT_IN_ROSS_ATTR, NWCGConstants.NO);
			nwcgRootElm.setAttribute(NWCGConstants.INCIDENT_LOCKED, NWCGConstants.YES);
			String modDesc = "Received UpdateIncidentKeyNotification";
			nwcgRootElm.setAttribute(NWCGConstants.MODIFICATION_CODE, NWCGConstants.MOD_CODE_ROSS_VAL);
			nwcgRootElm.setAttribute(NWCGConstants.MODIFICATION_DESC, modDesc);
			nwcgRootElm.setAttribute(NWCGConstants.INCIDENT_LOCK_REASON_ATTR, modDesc);
			try {
				// Service:: NWCGUpdateIncidentOrderService
				logger.verbose("@@@@@ nwcgIncDoc : " + XMLUtil.getXMLString(nwcgIncDoc));
				nwcgIncOP = CommonUtilities.invokeService(env, NWCGConstants.SVC_UPDT_INC_KEY_SVC, nwcgIncDoc);
				logger.verbose("@@@@@ nwcgIncOP : " + XMLUtil.getXMLString(nwcgIncOP));
			} catch (Exception e) {
				logger.error("!!!!! Caught General Exception :: while calling service : " + NWCGConstants.SVC_UPDT_INC_KEY_SVC + ", Error Message : " + e.getMessage());
				logger.error(e.getLocalizedMessage(), e);
				String errDesc = "Failed updating old Incident " + oldIncNo + " as inactive failed. UpdateIncidentKeyNotification is not processed";
				getDataAndRaiseAlert(env, doc, errDesc, false);
			}
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException :: " + pce);
			pce.printStackTrace();
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			e.printStackTrace();
		}
		logger.verbose("@@@@@ Exiting NWCGUpdateIncidentKeyNotificationHandler::updateOldIncidentDtls @@@@@");
		return nwcgIncOP;
	}

	/**
	 * This method will be called on exceptions or on missing data while
	 * processing Update Incident Notification. It will raise an alert in
	 * NWCG_INCIDENT_FAILURE queue. It will get incident number, year and
	 * customer id based on class variable values
	 * 
	 * @param env
	 * @param doc
	 * @param errDesc
	 * @return
	 */
	private void getDataAndRaiseAlert(YFSEnvironment env, Document doc, String desc, boolean succFail) {
		logger.verbose("@@@@@ Entering NWCGUpdateIncidentKeyNotificationHandler::getDataAndRaiseAlert @@@@@");
		// We are originally getting the cacheId from old incident details. If that call itself fails, then cacheId will be null.
		String userId = CommonUtilities.getAdminUserForCache(env, cacheId);
		HashMap hmap = new HashMap();
		hmap.put(NWCGConstants.ALERT_OLD_INCIDENT_NO, oldIncNo);
		hmap.put(NWCGConstants.ALERT_OLD_INCIDENT_YEAR, oldIncYr);
		hmap.put(NWCGConstants.ALERT_NEW_INCIDENT_NO, newIncNo);
		hmap.put(NWCGConstants.ALERT_NEW_INCIDENT_YEAR, newIncYr);
		hmap.put(NWCGConstants.ALERT_SOAP_MESSAGE, doc.getDocumentElement().getAttribute(NWCGConstants.INC_NOTIF_MSGNAME_ATTR));
		hmap.put(NWCGConstants.ALERT_DIST_ID, distId);
		hmap.put(NWCGConstants.ALERT_SHIPNODE_KEY, cacheId);
		String alertQ = "";
		if (succFail){
			alertQ = NWCGConstants.NWCG_INCIDENT_SUCCESS;
		} else {
			alertQ = NWCGConstants.NWCG_INCIDENT_FAILURE;
		}
		CommonUtilities.raiseAlertAndAssigntoUser(env, alertQ, desc, userId, doc, hmap);
		logger.verbose("@@@@@ Exiting NWCGUpdateIncidentKeyNotificationHandler::getDataAndRaiseAlert @@@@@");
	}
}