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
import java.util.Hashtable;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.incident.util.NWCGIncidentToIncidentTransferUtil;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.ib.read.handler.util.NotificationCommonUtilities;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGMergeIncidentNotificationHandler implements NWCGMessageHandlerInterface {

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGMergeIncidentNotificationHandler.class);
	private String srcIncNo = NWCGConstants.EMPTY_STRING;
	private String srcIncYr = NWCGConstants.EMPTY_STRING;
	private String destIncNo = NWCGConstants.EMPTY_STRING;
	private String destIncYr = NWCGConstants.EMPTY_STRING;
	private String distId = NWCGConstants.EMPTY_STRING;
	private String cacheId = NWCGConstants.EMPTY_STRING;

	/**
	 * This will take care of Merge process as described below
	 *  - Checks if source incident is present or not
	 *  - If source incident is not present, then do not do anything with MergeIncident Notification
	 *  - If source incident is present, then store that information for creating the order.
	 *  - Make an update message to deregister the incident and deactivate the incident
	 *  - Checks if target incident is present or not
	 *  - If target incident is not present, then create the incident. Raise an alert in
	 * 	  NWCG_INCIDENT_SUCCESS
	 *  - If target incident is present, then store the incident details information for creating the order
	 *  - If RegisteredInterest=N or IsActive=N, then update the incident to
	 *    RegisteredInterest=Y and IsActive=Y THE BELOW STEPS WILL NOT BE DONE AS
	 *    IT IS BEING DONE AS INCIDENT TO INCIDENT TRANSFER IS BEING DONE AS PART
	 *    OF RESOURCE REASSIGNMENT
	 *  - Get shipped trackable items against the source incident and year (NWCG_TRACKABLE_ITEM)
	 *  - From the above item list check if there is a ROSS tracked item by making getItemDetails call
	 *  - If there are ROSS tracked items, create an incident transfer type issue with FROM
	 *    and TO incident details. Confirm the draft order
	 *  - If there are no ROSS tracked items, then ignore Merge Notification Message
	 */
	public Document process(YFSEnvironment env, Document msgXML) throws NWCGException {
		logger.verbose("@@@@@ Entering NWCGMergeIncidentNotificationHandler::process @@@@@");
		logger.verbose("@@@@@ msgXML : " + XMLUtil.getXMLString(msgXML));

		// Set the source and destination incident number and year
		parseAndSetIncNoAndYr(msgXML);

		// Get Source incident details
		Document docSrcIncDtls = NWCGIncidentToIncidentTransferUtil.getIncidentDetails(env, srcIncNo, srcIncYr, "SOURCE");

		if (docSrcIncDtls == null) {
			logger.verbose("@@@@@ Source Incident is not present in ICBSR. So not processing Merge notification for " + srcIncNo + " and " + srcIncYr);
			String detailDesc = "Source Incident " + srcIncNo + " and year " + srcIncYr + " is not present in ICBSR system. Merge Incident Notification is not processed";
			String desc = "Source incident is not present in ICBSR system";
			getDataAndRaiseAlert(env, msgXML, detailDesc, desc, false);
			logger.verbose("@@@@@ Exiting NWCGMergeIncidentNotificationHandler::process (1) @@@@@");
			return msgXML;
		} else {
			String lockReason = "Source Incident " + srcIncNo + "/" + srcIncYr + " is merged to " + "Destination Incident " + destIncNo + "/" + destIncYr;
			Hashtable<String, String> htAttr = new Hashtable<String, String>();
			htAttr.put(NWCGConstants.INCIDENT_LOCKED, NWCGConstants.YES);
			htAttr.put(NWCGConstants.INCIDENT_LOCK_REASON_ATTR, lockReason);
			htAttr.put(NWCGConstants.IS_ACTIVE, NWCGConstants.NO);
			htAttr.put(NWCGConstants.INCIDENT_REG_INT_IN_ROSS_ATTR, NWCGConstants.NO);
			htAttr.put("MergeIncidentNo", destIncNo);
			htAttr.put("MergeIncidentYear", destIncYr);
			NWCGIncidentToIncidentTransferUtil.updateIncident(env, srcIncNo, srcIncYr, htAttr);
		}
		Document docDestIncDtls = NWCGIncidentToIncidentTransferUtil.getIncidentDetails(env, destIncNo, destIncYr, "DESTINATION");
		if (docDestIncDtls == null) {
			logger.verbose("@@@@@ Destination incident " + destIncNo + " is not present in ICBSR system. " + "Creating it...");
			Document docDestIncIP = null;
			try {
				docDestIncIP = XMLUtil.getDocument();
				Element elmDestIncRootElm = docDestIncIP.createElementNS(NWCGAAConstants.RESOURCE_ORDER_NOTIFICATION_NAMESPACE, "ron:Incident");
				docDestIncIP.appendChild(elmDestIncRootElm);
			} catch (Exception e) {
				logger.error("!!!!! Caught General Exception :: Failed while getting a document");
				e.printStackTrace();
			}
			Element elmDoc = docDestIncIP.getDocumentElement();
			Node nodeDestIncFromROSS = (msgXML.getDocumentElement().getElementsByTagName("ron:DestinationIncident")).item(0);
			NodeList nlDestNodeChildsFromROSS = nodeDestIncFromROSS.getChildNodes();
			for (int i = 0; i < nlDestNodeChildsFromROSS.getLength(); i++) {
				Node destIncNodeFromROSS = nlDestNodeChildsFromROSS.item(i);
				Node destIncNode = docDestIncIP.importNode(destIncNodeFromROSS, true);
				elmDoc.appendChild(destIncNode);
			}
			logger.verbose("@@@@@ Destination incident input XML : " + XMLUtil.getXMLString(docDestIncIP));
			Document docIncInput = NWCGIncidentToIncidentTransferUtil.createIncidentInput(env, docDestIncIP, "MERGE");
			Element elmIncInput = docIncInput.getDocumentElement();
			elmIncInput.setAttribute(NWCGConstants.REPLACED_INCIDENT_NO, srcIncNo);
			elmIncInput.setAttribute(NWCGConstants.REPLACED_INCIDENT_YEAR, srcIncYr);
			try {
				CommonUtilities.invokeService(env, NWCGConstants.SVC_CREATE_INCIDENT_ORDER_SVC, docIncInput);
			} catch (Exception e) {
				logger.error("!!!!! Caught General Exception :: Failed while creating target incident : " + e);
				e.printStackTrace();
			}
		} else {
			logger.verbose("@@@@@ Destination incident is present");
			updateIncident(env, docDestIncDtls);
		}
		String detailDesc = "Source Incident " + srcIncNo + "/" + srcIncYr + " is merged to " + destIncNo + "/" + destIncYr + ". Merge Incident Notification processed succesfully. ";
		if (docDestIncDtls == null) {
			detailDesc.concat("Created " + destIncNo + "/" + destIncYr + " due to Merge Incident Notification");
		}
		String desc = "Processed Merge for " + srcIncNo + "/" + srcIncYr;
		getDataAndRaiseAlert(env, msgXML, detailDesc, desc, true);
		logger.verbose("@@@@@ Exiting NWCGMergeIncidentNotificationHandler::process (2) @@@@@");
		return msgXML;
	}

	/**
	 * This method will update the incident after setting the necessary
	 * attributes. This is called for the destination incident.
	 * 
	 * @param env
	 * @param docDestIncDtls
	 */
	private void updateIncident(YFSEnvironment env, Document docDestIncDtls) {
		logger.verbose("@@@@@ Entering NWCGMergeIncidentNotificationHandler::updateIncident @@@@@");
		Element elmRootDest = docDestIncDtls.getDocumentElement();
		Hashtable<String, String> htAttr = new Hashtable<String, String>();
		htAttr.put(NWCGConstants.IS_ACTIVE, NWCGConstants.YES);
		htAttr.put(NWCGConstants.INCIDENT_REG_INT_IN_ROSS_ATTR, NWCGConstants.YES);
		htAttr.put(NWCGConstants.REPLACED_INCIDENT_NO, srcIncNo);
		htAttr.put(NWCGConstants.REPLACED_INCIDENT_YEAR, srcIncYr);
		String replacedIncNo1 = elmRootDest.getAttribute(NWCGConstants.REPLACED_INCIDENT_NO);
		// Last Incident No is present on the destination incident. Copy the Last Incident No to Last Incident No2. Same is true for year
		if (replacedIncNo1 != null && replacedIncNo1.trim().length() > 0) {
			String replacedIncYr1 = elmRootDest.getAttribute(NWCGConstants.REPLACED_INCIDENT_YEAR);
			htAttr.put(NWCGConstants.REPLACED_INCIDENT_NO_2, replacedIncNo1);
			htAttr.put(NWCGConstants.REPLACED_INCIDENT_YEAR_2, replacedIncYr1);
			String replacedIncNo2 = elmRootDest.getAttribute(NWCGConstants.REPLACED_INCIDENT_NO_2);
			// Last Incident No 2 is present on the destination incident. Copy the Last Incident No 2 to Last Incident No 3. Same is true for year
			if (replacedIncNo2 != null && replacedIncNo2.trim().length() > 0) {
				String replacedIncYr2 = elmRootDest.getAttribute(NWCGConstants.REPLACED_INCIDENT_YEAR_2);
				htAttr.put(NWCGConstants.REPLACED_INCIDENT_NO_3, replacedIncNo2);
				htAttr.put(NWCGConstants.REPLACED_INCIDENT_YEAR_3, replacedIncYr2);
			}
		}
		NWCGIncidentToIncidentTransferUtil.updateIncident(env, destIncNo, destIncYr, htAttr);
		logger.verbose("@@@@@ Exiting NWCGMergeIncidentNotificationHandler::updateIncident @@@@@");
	}

	/**
	 * This method will parse and set the destination and source incident no and
	 * year
	 * 
	 * @param inputDoc
	 */
	private void parseAndSetIncNoAndYr(Document inputDoc) {
		logger.verbose("@@@@@ Entering NWCGMergeIncidentNotificationHandler::parseAndSetIncNoAndYr @@@@@");
		HashMap<String, String> srcIncNoAndYr = NotificationCommonUtilities.parseAndRtnIncidentKey(inputDoc.getElementsByTagName("ron:SourceIncidentKey"));
		HashMap<String, String> destIncNoAndYr = NotificationCommonUtilities.parseAndRtnIncidentKey(inputDoc.getElementsByTagName("ron:DestinationIncidentKey"));
		srcIncNo = srcIncNoAndYr.get(NWCGConstants.INCIDENT_NO_ATTR);
		srcIncYr = srcIncNoAndYr.get(NWCGConstants.YEAR_ATTR);
		destIncNo = destIncNoAndYr.get(NWCGConstants.INCIDENT_NO_ATTR);
		destIncYr = destIncNoAndYr.get(NWCGConstants.YEAR_ATTR);
		logger.verbose("@@@@@ Exiting NWCGMergeIncidentNotificationHandler::parseAndSetIncNoAndYr @@@@@");
	}

	/**
	 * This method will be called on exceptions or on missing data while
	 * processing Merge Incident Notification. It will raise an alert in
	 * NWCG_INCIDENT_FAILURE queue. It will get incident number, year and other
	 * variables from class defined data
	 * 
	 * @param env
	 * @param doc
	 * @param errDesc
	 * @return
	 */
	private boolean getDataAndRaiseAlert(YFSEnvironment env, Document doc, String detailDesc, String desc, boolean succFail) {
		logger.verbose("@@@@@ Entering NWCGMergeIncidentNotificationHandler::getDataAndRaiseAlert @@@@@");
		logger.verbose("@@@@@ detailDesc :: " + detailDesc);
		// Cache ID can be empty string if we are raising an alert for Source Incident
		String userId = CommonUtilities.getAdminUserForCache(env, cacheId);
		HashMap<String, String> hmap = new HashMap<String, String>();
		hmap.put(NWCGConstants.ALERT_SRC_INCIDENT_NO, srcIncNo);
		hmap.put(NWCGConstants.ALERT_SRC_INCIDENT_YEAR, srcIncYr);
		hmap.put(NWCGConstants.ALERT_DEST_INCIDENT_NO, destIncNo);
		hmap.put(NWCGConstants.ALERT_DEST_INCIDENT_YEAR, destIncYr);
		hmap.put(NWCGConstants.ALERT_SOAP_MESSAGE, doc.getDocumentElement().getAttribute(NWCGConstants.INC_NOTIF_MSGNAME_ATTR));
		hmap.put(NWCGConstants.ALERT_DIST_ID, distId);
		hmap.put(NWCGConstants.ALERT_SHIPNODE_KEY, cacheId);
		hmap.put(NWCGConstants.ALERT_DESC, desc);
		String alertQ = NWCGConstants.EMPTY_STRING;
		if (succFail) {
			alertQ = NWCGConstants.NWCG_INCIDENT_RADIOS_SUCCESS;
		} else {
			alertQ = NWCGConstants.NWCG_INCIDENT_RADIOS_FAILURE;
		}
		CommonUtilities.raiseAlertAndAssigntoUser(env, alertQ, detailDesc, userId, doc, hmap);
		logger.verbose("@@@@@ Exiting NWCGMergeIncidentNotificationHandler::getDataAndRaiseAlert @@@@@");
		return false;
	}
}