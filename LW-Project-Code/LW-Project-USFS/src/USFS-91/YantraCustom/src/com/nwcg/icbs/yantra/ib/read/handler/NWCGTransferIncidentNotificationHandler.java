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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;

import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * transfer incident notification handler
 * 
 * This method will do the following
 * 	- Gets the incident number and year from the input xml
 * 	- Checks if this incident number is present in ICBSR system. If it is not present, then it will raise an alert
 * 	-	Checks if this incident is registered. If it is not registered, then it will raise an alert
 * 	- Gets the dispatch unit id from the input xml and calls NWCGTransferIncidentNotificationService. It will call
 * 	  the extended database api modifyNWCGIncidentOrder by passing only dispatch unit info along with incident key details
 */
public class NWCGTransferIncidentNotificationHandler implements NWCGMessageHandlerInterface {
	
	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGTransferIncidentNotificationHandler.class);
	private String incNo = "";
	private String incId = "";
	private String year = "";
	private String custId = "";
	private String cacheId = "";
	private String cacheIdFromIncDtls = "";
	private String distId = "";
	private String fromUnitId = "";
	private String toUnitId = "";

	public Document process(YFSEnvironment env, Document doc) {
		logger.verbose("@@@@@ Entering NWCGTransferIncidentNotificationHandler::process @@@@@");
		logger.verbose("@@@@@ doc :: " + XMLUtil.getXMLString(doc));
		// Initialize the variables
		Element notifRootElm = doc.getDocumentElement();
		distId = notifRootElm.getAttribute(NWCGConstants.INC_NOTIF_DIST_ID_ATTR);
		logger.verbose("@@@@@ distId :: " + distId);
		parseAndSetIncidentNumberAndYear(doc);
		parseAndSetCustomerId(doc);
		parseAndSetCacheId(doc);
		// Parse and set dispatch unit ids
		parseAndSetDispatchIds(doc);
		// Check if incident exists and its registered status
		boolean incChk = checkIfIncidentExistsAndItsRegisterStatus(env, doc);
		if (!incChk) {
			logger.verbose("@@@@@ Exiting NWCGTransferIncidentNotificationHandler::process (1) @@@@@");
			return doc;
		}
		if (toUnitId.length() < 2) {
			String errDesc = "To Unit ID is empty in Transfer Incident Notification. Not updating the dispatch unit id.";
			String desc = "Transfer Incident Notification failed. Not updating the dispatch unit id.";
			getDataAndRaiseAlert(env, doc, errDesc, desc,  false);
			logger.verbose("@@@@@ Exiting NWCGTransferIncidentNotificationHandler::process (2) @@@@@");
			return doc;
		}
		updateDispatchId(env, doc);
		logger.verbose("@@@@@ Exiting NWCGTransferIncidentNotificationHandler::process (3) @@@@@");
		return doc;
    }	
    
	/**
	 * This method parses the input xml and sets the incident number and 
	 * year to class level variables. Incident Number and Year are mandatory in the
	 * input xml. So, null check is not done for those values
	 * @param doc
	 */
	private void parseAndSetIncidentNumberAndYear(Document doc) {
		logger.verbose("@@@@@ Entering NWCGTransferIncidentNotificationHandler::parseAndSetIncidentNumberAndYear @@@@@");
		Element transferIncNotifRootElm = doc.getDocumentElement();
		// Retrieve Source incident. Make a call to get incident details using output template
		NodeList incidentKeyNL = transferIncNotifRootElm.getElementsByTagName(NWCGConstants.INC_NOTIF_INCKEY_ATTR);
		// IncidentKey is a mandatory element and the root element will have only one occurence. Not checking for NULL. All the elements under incidentkey are also mandatory, so not checking for NULL
		String seqNum = "";
		String unitIDPrefix = "";
		String unitIDSuffix = "";
		StringBuffer sbIncNo = new StringBuffer("");
		Node incidentKeyNode = incidentKeyNL.item(0);
		NodeList naturalIncKeyNL = incidentKeyNode.getChildNodes();
		for (int incTrav=0; incTrav < naturalIncKeyNL.getLength(); incTrav++){
			Node naturalIncKeyNode = naturalIncKeyNL.item(incTrav);
			if (naturalIncKeyNode.getNodeName().equalsIgnoreCase(NWCGConstants.INC_NOTIF_NAT_INCKEY_ATTR)){
				NodeList incFieldsNL = naturalIncKeyNode.getChildNodes();
				for (int incFieldsTrav=0; incFieldsTrav < incFieldsNL.getLength(); incFieldsTrav++){
					Node incField = incFieldsNL.item(incFieldsTrav);
					String incFieldNodeName = incField.getNodeName();
					if (incFieldNodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_HOSTID_ATTR)){
						NodeList unitIDNL = incField.getChildNodes();
						for (int unitIDTrav=0; unitIDTrav < unitIDNL.getLength(); unitIDTrav++){
							Node unitIDNode = unitIDNL.item(unitIDTrav);
							String unitIDNodeName = unitIDNode.getNodeName();
							if (unitIDNodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_UNIT_ID_PREFIX_ATTR)){
								unitIDPrefix = unitIDNode.getTextContent();
							} else if (unitIDNodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_UNIT_ID_SUFFIX_ATTR)){
								unitIDSuffix = unitIDNode.getTextContent();
							}
						}
					} else if (incFieldNodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_SEQ_NO_ATTR)){
						seqNum = incField.getTextContent();
					} else if (incFieldNodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_YR_CREATED_ATTR)){
						year = incField.getTextContent();
					}
				} 
			} else if(naturalIncKeyNode.getNodeName().equalsIgnoreCase(NWCGConstants.INC_NOTIF_INCID_ATTR)) {
				//here naturalIncKeyNode is IncidentID node
				NodeList incFieldsIncIdNL = naturalIncKeyNode.getChildNodes();
				for(int incFieldsIncTrav = 0; incFieldsIncTrav < incFieldsIncIdNL.getLength(); incFieldsIncTrav++) {
					Node incField = incFieldsIncIdNL.item(incFieldsIncTrav);
					String incFieldNodeName = incField.getNodeName();
					incId = (incFieldNodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_ENTID_ATTR))? incField.getTextContent() : incId;
				}
			}
		} 
		sbIncNo.append(unitIDPrefix).append("-").append(unitIDSuffix).append("-");
		int seqNumLen = 6;
		for (int i=0; i < seqNumLen - seqNum.length(); i++){
			sbIncNo.append("0");
		}
		incNo = sbIncNo.append(seqNum).toString();
		logger.verbose("@@@@@ Incident Number : " + incNo + ", Year : " + year);
		logger.verbose("@@@@@ Exiting NWCGTransferIncidentNotificationHandler::parseAndSetIncidentNumberAndYear @@@@@");
	}
	
	/** 
	 * This method parses through the input xml and sets the customer id to
	 * class level variable. This method gets the parameters from BillingOrganization element
	 * Puts it in the format of UnitIDPrefix + "-" + UnitIDSuffix.
	 * Customer ID is a mandatory element. So, null check is not done
	 * @param doc
	 */
	private void parseAndSetCustomerId(Document doc) {
		logger.verbose("@@@@@ Entering NWCGTransferIncidentNotificationHandler::parseAndSetCustomerId @@@@@");
		String unitIDPrefix = "";
		String unitIDSuffix = "";
		Element transferNotifRootElm = doc.getDocumentElement();
		NodeList billingOrgNL = transferNotifRootElm.getElementsByTagName(NWCGConstants.INC_NOTIF_BILLINGORG_ELEMENT);
		// There is only one occurence of BillingOrganization and it can have only one occurence and is a mandatory element
		Node billingOrgNode = billingOrgNL.item(0);
		NodeList unitIDNL = billingOrgNode.getChildNodes();
		for (int i=0; i < unitIDNL.getLength(); i++){
			Node unitIDNode = unitIDNL.item(i);
			String nodeName = unitIDNode.getNodeName();
			if (nodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_UNIT_ID_PREFIX_ATTR)){
				unitIDPrefix = unitIDNode.getTextContent();
			} else if (nodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_UNIT_ID_SUFFIX_ATTR)){
				unitIDSuffix = unitIDNode.getTextContent();
			}
		}
		custId = unitIDPrefix.concat(unitIDSuffix);
		logger.verbose("@@@@@ Customer ID : " + custId);
		logger.verbose("@@@@@ Exiting NWCGTransferIncidentNotificationHandler::parseAndSetCustomerId @@@@@");
	}
	
	/** 
	 * This method parses through the input xml and sets the Cache ID to
	 * class level variable. This method gets the parameters from CacheOrganization element
	 * Puts it in the format of UnitIDPrefix + UnitIDSuffix.
	 * This field will be used to retrieve the user id to which the alert needs to be assigned
	 * @param doc
	 */
	private void parseAndSetCacheId(Document doc) {
		logger.verbose("@@@@@ Entering NWCGTransferIncidentNotificationHandler::parseAndSetCacheId @@@@@");
		String unitIDPrefix = "";
		String unitIDSuffix = "";
		Element transferNotifRootElm = doc.getDocumentElement();
		NodeList cacheOrgNL = transferNotifRootElm.getElementsByTagName(NWCGConstants.INC_NOTIF_CACHEORG_ELEMENT);
		// There is only one occurence of CacheOrganization and it can have only one occurence and is a mandatory element
		if (cacheOrgNL != null && cacheOrgNL.getLength() > 0) {		
			Node cacheOrgNode = cacheOrgNL.item(0);
			NodeList unitIDNL = cacheOrgNode.getChildNodes();
			for (int i=0; i < unitIDNL.getLength(); i++){
				Node unitIDNode = unitIDNL.item(i);
				String nodeName = unitIDNode.getNodeName();
				if (nodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_UNIT_ID_PREFIX_ATTR)){
					unitIDPrefix = unitIDNode.getTextContent();
				} else if (nodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_UNIT_ID_SUFFIX_ATTR)){
					unitIDSuffix = unitIDNode.getTextContent();
				}
			}
		}
		cacheId = unitIDPrefix.concat(unitIDSuffix);
		logger.verbose("@@@@@ CacheID : " + cacheId);
		logger.verbose("@@@@@ Exiting NWCGTransferIncidentNotificationHandler::parseAndSetCacheId @@@@@");
	}
	
	/**
	 * This method checks if incident exists in ICBSR system. If it exists, check if 
	 * incident is registered in ROSS. Raise alert in all other situations
	 * @param env
	 * @param doc
	 * @return
	 */
	private boolean checkIfIncidentExistsAndItsRegisterStatus(YFSEnvironment env, Document doc) {
		logger.verbose("@@@@@ Entering NWCGTransferIncidentNotificationHandler::checkIfIncidentExistsAndItsRegisterStatus @@@@@");
		boolean incStatus = false;
		try {
			Document nwcgIncDoc = XMLUtil.createDocument(NWCGConstants.INCIDENT_ORDER_ELEM);
			Element nwcgRootElm = nwcgIncDoc.getDocumentElement();
			nwcgRootElm.setAttribute(NWCGConstants.INCIDENT_NO_ATTR, incNo);
			nwcgRootElm.setAttribute(NWCGConstants.YEAR_ATTR, year);
			logger.verbose("@@@@@ Input XML : " + XMLUtil.getXMLString(nwcgIncDoc));
			Document nwcgIncOP = null;
			try {
				nwcgIncOP = CommonUtilities.invokeService(env, NWCGConstants.SVC_GET_INCIDENT_ORDER_DTLS_ONLY_SVC, nwcgIncDoc);
			} catch (Exception e) {
				logger.error("!!!!! Caught General Exception :: while calling service : " + NWCGConstants.SVC_GET_INCIDENT_ORDER_DTLS_ONLY_SVC + ", Error Message : " + e);
				e.printStackTrace();
				String errDesc = "Transfer Incident Notification Synchronization Failed: Incident " + incNo + " with Year " + year + "  does not exist in ICBS-R";
				String desc = "Transfer Incident Notification Synchronization Failed for Incident " + incNo;
				getDataAndRaiseAlert(env, doc, errDesc, desc, false);
				return incStatus;
			}
			if (nwcgIncOP != null) {
				// Check register status if registered, incStatus = true;
				Element opRootElm = nwcgIncOP.getDocumentElement();
				String incRegStatus = opRootElm.getAttribute(NWCGConstants.INCIDENT_REG_INT_IN_ROSS_ATTR);
				cacheIdFromIncDtls = opRootElm.getAttribute(NWCGConstants.PRIMARY_CACHE_ID);
				if ((incRegStatus != null) && incRegStatus.equalsIgnoreCase(NWCGConstants.YES)){
					incStatus = true;
				} else {
					incStatus = false;
					String errDesc = "Incident " + incNo + " exists in ICBSR, but is not registered with ROSS. " + "Transfer Incident Notification for this incident is not processed";
					// Raise exception with message "Incident exists, but is not registered with ROSS....
					String desc = "Transfer Incident Notification Synchronization Failed: Incident " + incNo ;
					getDataAndRaiseAlert(env, doc, errDesc, desc, false);
				}
			} else {
				// Code should never come here. Code will come here if service returns NULL instead of exception (if incident doesn't exists) - This is the current Yantra behavior
				String errDesc = "Transfer Incident Notification Synchronization Failed: Incident " + incNo + " with Year " + year + "  does not exist in ICBS-R";
				String desc = "Transfer Incident Notification Synchronization Failed: Incident " + incNo ;
				getDataAndRaiseAlert(env, doc, errDesc, desc, false);
			}
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException :: " + pce);
			pce.printStackTrace();
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			e.printStackTrace();
		}	
		logger.verbose("@@@@@ Exiting NWCGTransferIncidentNotificationHandler::checkIfIncidentExistsAndItsRegisterStatus @@@@@");
		return incStatus;
	}
	
	/** 
	 * This method parses through the input xml and sets the dispatch ids to
	 * class level variable. This method gets the parameters from DispatchFromAndTo element
	 * Puts it in the format of UnitIDPrefix + "-" + UnitIDSuffix.
	 * Customer ID is a mandatory element. So, null check is not done
	 * @param doc
	 */
	private void parseAndSetDispatchIds(Document doc) {
		logger.verbose("@@@@@ Entering NWCGTransferIncidentNotificationHandler::parseAndSetDispatchIds @@@@@");
		String unitIDPrefix = "";
		String unitIDSuffix = "";
		Element transferNotifRootElm = doc.getDocumentElement();
		// There is only one element with name FromUnitId and it can have only one occurence and is a mandatory element
		NodeList fromUnitIdNL = transferNotifRootElm.getElementsByTagName(NWCGConstants.INC_NOTIF_FROM_UNITID_ELEMENT);
		Node fromUnitIdNode = fromUnitIdNL.item(0);
		NodeList fromUnitIDNL = fromUnitIdNode.getChildNodes();
		for (int i=0; i < fromUnitIDNL.getLength(); i++) {
			Node fromUnitIDNode = fromUnitIDNL.item(i);
			String nodeName = fromUnitIDNode.getNodeName();
			if (nodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_UNIT_ID_PREFIX_ATTR)) {
				unitIDPrefix = fromUnitIDNode.getTextContent();
			} else if (nodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_UNIT_ID_SUFFIX_ATTR)) {
				unitIDSuffix = fromUnitIDNode.getTextContent();
			}
		}
		fromUnitId = unitIDPrefix.concat("-").concat(unitIDSuffix);
		unitIDPrefix = unitIDSuffix = "";
		// There is only one element with name ToUnitId and it can have only one occurence and is a mandatory element
		NodeList toUnitIdNL = transferNotifRootElm.getElementsByTagName(NWCGConstants.INC_NOTIF_TO_UNITID_ELEMENT);
		Node toUnitIdNode = toUnitIdNL.item(0);
		NodeList toUnitIDNL = toUnitIdNode.getChildNodes();
		for (int i=0; i < toUnitIDNL.getLength(); i++) {
			Node toUnitIDNode = toUnitIDNL.item(i);
			String nodeName = toUnitIDNode.getNodeName();
			if (nodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_UNIT_ID_PREFIX_ATTR)) {
				unitIDPrefix = toUnitIDNode.getTextContent();
			} else if (nodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_UNIT_ID_SUFFIX_ATTR)) {
				unitIDSuffix = toUnitIDNode.getTextContent();
			}
		}
		toUnitId = unitIDPrefix.concat("-").concat(unitIDSuffix);
		logger.verbose("@@@@@ From Unit ID : " + fromUnitId + ", To Unit ID : " + toUnitId);
		logger.verbose("@@@@@ Exiting NWCGTransferIncidentNotificationHandler::parseAndSetDispatchIds @@@@@");
	}
	
	/**
	 * This method will update the dispatch unit id by calling UpdateIncidentNotification handler.
	 * It will pass only the incident key and dispatch unit information
	 * @param env
	 */
	private Document updateDispatchId(YFSEnvironment env, Document doc) {
		logger.verbose("@@@@@ Entering NWCGTransferIncidentNotificationHandler::updateDispatchId @@@@@");
		Document opDoc = null;
		try {
			Document nwcgIncDoc = XMLUtil.createDocument(NWCGConstants.INCIDENT_ORDER_ELEM);
			Element nwcgRootElm = nwcgIncDoc.getDocumentElement();
			nwcgRootElm.setAttribute(NWCGConstants.INCIDENT_NO_ATTR, incNo);
			nwcgRootElm.setAttribute(NWCGConstants.INCIDENT_ID_ATTR, incId);
			nwcgRootElm.setAttribute(NWCGConstants.YEAR_ATTR, year);
			nwcgRootElm.setAttribute(NWCGConstants.ROSS_DISPATCH_ID, toUnitId);
			nwcgRootElm.setAttribute(NWCGConstants.MODIFICATION_CODE, NWCGConstants.MOD_CODE_ROSS_VAL);
			String modDesc = "Received TransferIncidentNotification";
			nwcgRootElm.setAttribute(NWCGConstants.MODIFICATION_DESC, modDesc);
			logger.verbose("@@@@@ Input XML : " + XMLUtil.getXMLString(nwcgIncDoc));
			try {
				opDoc = CommonUtilities.invokeService(env, NWCGConstants.SVC_TRANSFER_INCIDENT_ORDER_SVC, nwcgIncDoc);
			}
			catch (Exception e){
				logger.error("!!!!! Caught General Exception :: while calling update service : " + NWCGConstants.SVC_TRANSFER_INCIDENT_ORDER_SVC + ", Error Message : " + e);
				e.printStackTrace();
				String errDesc = "Failed while updating dispatch unit id for Incident " + incNo + ". " + "Transfer Incident Notification is not processed";
				String desc = "Failed processing Transfer Incident Notification for Incident " + incNo + ".";
				getDataAndRaiseAlert(env, doc, errDesc, desc, false);
				return opDoc;
			}
			String detailDesc = "Transfer Incident Notification Received for incident " + incNo + " from ROSS, and incident updated successfully in ICBS-R";
			String desc = "Transfer Incident Notification Received for incident " + incNo + " from ROSS";
			getDataAndRaiseAlert(env, doc, detailDesc, desc, true);
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException :: " + pce);
			pce.printStackTrace();
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			e.printStackTrace();
		}	
		logger.verbose("@@@@@ Exiting NWCGTransferIncidentNotificationHandler::updateDispatchId @@@@@");
		return opDoc;
	}
	
	/**
	 * This method will be called on exceptions or on missing data while processing Transfer
	 * Incident Notification. It will raise an alert in NWCG_INCIDENT_FAILURE queue. It will
	 * get incident number, year and customer id based on class variable values
	 * @param env
	 * @param doc
	 * @param errDesc
	 * @return
	 */
	private boolean getDataAndRaiseAlert(YFSEnvironment env, Document doc, String detailDesc, String desc, boolean succFail) {
		logger.verbose("@@@@@ Entering NWCGTransferIncidentNotificationHandler::getDataAndRaiseAlert @@@@@");
		logger.verbose("@@@@@ detailDesc :: " + detailDesc);
		if (incNo.length() < 2) {
			parseAndSetIncidentNumberAndYear(doc);
		}
		if (custId.length() < 2) {
			parseAndSetCustomerId(doc);
		}
		// Cache ID may not be present in the input XML, so retrieving it from incident details of DB
		if (cacheId.length() < 2) {
			parseAndSetCacheId(doc);
			if ((cacheId == null) || (cacheId != null && cacheId.length() < 2)) {
				cacheId = cacheIdFromIncDtls;
			}
		} else {
			cacheId = cacheIdFromIncDtls;
		}
		if (toUnitId.length() < 2){
			parseAndSetDispatchIds(doc);
		}
		String userId = CommonUtilities.getAdminUserForCache(env, cacheId);
		HashMap hmap = new HashMap();
		hmap.put(NWCGConstants.ALERT_DIST_ID, distId);
		hmap.put(NWCGConstants.ALERT_INCIDENT_NO, incNo);
		hmap.put(NWCGConstants.ALERT_YEAR, year);
		hmap.put(NWCGConstants.ALERT_CUST_ID, custId);
		hmap.put(NWCGConstants.ALERT_TO_DISP_UNIT_ID, toUnitId);
		hmap.put(NWCGConstants.ALERT_FROM_DISP_UNIT_ID, fromUnitId);
		hmap.put(NWCGConstants.ALERT_SOAP_MESSAGE, doc.getDocumentElement().getAttribute(NWCGConstants.INC_NOTIF_MSGNAME_ATTR));
		hmap.put(NWCGConstants.ALERT_SHIPNODE_KEY, cacheId);
		hmap.put(NWCGConstants.ALERT_DESC, desc);
		String alertQ = "";
		if (succFail){
			alertQ = NWCGConstants.NWCG_INCIDENT_SUCCESS;
		}
		else {
			alertQ = NWCGConstants.NWCG_INCIDENT_FAILURE;
		}
		CommonUtilities.raiseAlertAndAssigntoUser(env, alertQ, detailDesc, userId, doc, hmap);
		logger.verbose("@@@@@ Exiting NWCGTransferIncidentNotificationHandler::getDataAndRaiseAlert @@@@@");
		return false;
	}
}