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

package com.nwcg.icbs.yantra.webservice.msgStore;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.UUID;
import java.util.logging.Level;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.nwcg.icbs.yantra.webservice.util.alert.NWCGAlert;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServiceUtils;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServicesConstant;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGMessageStore implements NWCGMessageStoreInterface {
	
	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGMessageStore.class);

	private static NWCGMessageStore messageStore;

	private static final String className = NWCGMessageStore.class.getName();

	// Prevent direct instantiation of a NWCGMessageStore object
	private NWCGMessageStore() {
	}

	/**
	 * Thread-safe implementation for retrieving a NWCGMessageStore reference
	 * 
	 * @return NWCGMessageStore static reference
	 */
	public static synchronized NWCGMessageStore getMessageStore() {
		logger.verbose("@@@@@ In NWCGMessageStore::getMessageStore");
		if (messageStore == null) {
			messageStore = new NWCGMessageStore();
		}
		return messageStore;
	}

	/**
	 * Method to prevent the unlikely scenario where a caller attempts to clone
	 * their messageStore reference. Avoid threads problems
	 */
	public Object clone() throws CloneNotSupportedException {
		logger.verbose("@@@@@ In NWCGMessageStore::clone");
		throw new CloneNotSupportedException();
	}

	/**
	 * @return String representing a random distribution ID
	 */
	public String getUUID() {
		logger.verbose("@@@@@ In NWCGMessageStore::getUUID");
		return UUID.randomUUID().toString();
	}

	/**
	 * This method gets the entity information based on the type of the service
	 * and sets it in a hashtable
	 * 
	 * @param msg
	 * @param serviceName
	 * @return
	 * @throws Exception
	 */
	public Hashtable<Object, Object> retrieveEntityInfo(String msg, String serviceName) throws Exception {
		logger.verbose("@@@@@ Entering NWCGMessageStore::retrieveEntityInfo");
		
		final String methodName = "retrieveEntityInfo";
		Hashtable<Object, Object> ht = new Hashtable<Object, Object>();
		Document doc = XMLUtil.getDocument(msg);
		if (serviceName.equalsIgnoreCase(NWCGAAConstants.XPATH_ACTIVATE_INCIDENT_INTEREST) || serviceName.equalsIgnoreCase(NWCGAAConstants.XPATH_DEACTIVATE_INCIDENT_INTEREST)) {
			String incidentKey = XPathUtil.getString(doc, NWCGWebServicesConstant.XPATH_ACTIVATE_INCIDENT_INTEREST_KEY);
			if (incidentKey == null) {
				incidentKey = NWCGConstants.EMPTY_STRING;
			}
			ht.put(NWCGAAConstants.ENTITY_KEY, incidentKey);
			String incidentId = XPathUtil.getString(doc, NWCGAAConstants.XPATH_ACTIVE_INCIDENT_INTEREST_UNITID_PREFIX);
			if (incidentId == null) {
				incidentId = NWCGConstants.EMPTY_STRING;
			} else {
				StringBuffer sb = new StringBuffer(incidentId);
				sb.append(NWCGConstants.DASH).append(XPathUtil.getString(doc, NWCGAAConstants.XPATH_ACTIVE_INCIDENT_INTEREST_UNITID_SUFFIX));
				sb.append(NWCGConstants.DASH).append(XPathUtil.getString(doc, NWCGAAConstants.XPATH_ACTIVATE_INCIDENT_INTEREST_SEQUENCE));
				sb.append(NWCGConstants.DASH).append(XPathUtil.getString(doc, NWCGAAConstants.XPATH_ACTIVATE_INCIDENT_INTEREST_YEAR));
				incidentId = sb.toString();
			}

			ht.put(NWCGAAConstants.ENTITY_VALUE, incidentId);
			ht.put(NWCGAAConstants.ENTITY_NAME, NWCGAAConstants.INCIDENT_SERVICE_GROUP_NAME);
		} else if (serviceName.equalsIgnoreCase(NWCGAAConstants.XPATH_REGISTER_INCIDENT_INTEREST) || serviceName.equalsIgnoreCase(NWCGAAConstants.XPATH_DEREGISTER_INCIDENT_INTEREST)) {
			String incidentKey = XPathUtil.getString(doc, NWCGWebServicesConstant.XPATH_REGISTER_INCIDENT_INTEREST_KEY);
			if (incidentKey == null) {
				incidentKey = NWCGConstants.EMPTY_STRING;
			}
			ht.put(NWCGAAConstants.ENTITY_KEY, incidentKey);
			String incidentId = XPathUtil.getString(doc, NWCGAAConstants.XPATH_REGISTER_INCIDENT_INTEREST_UNITID_PREFIX);
			if (incidentId == null) {
				incidentId = NWCGConstants.EMPTY_STRING;
			} else {
				StringBuffer sb = new StringBuffer(incidentId);
				sb.append(NWCGConstants.DASH).append(XPathUtil.getString(doc, NWCGAAConstants.XPATH_REGISTER_INCIDENT_INTEREST_UNITID_SUFFIX));
				sb.append(NWCGConstants.DASH).append(XPathUtil.getString(doc, NWCGAAConstants.XPATH_REGISTER_INCIDENT_INTEREST_SEQUENCE));
				sb.append(NWCGConstants.DASH).append(XPathUtil.getString(doc, NWCGAAConstants.XPATH_REGISTER_INCIDENT_INTEREST_YEAR));
				incidentId = sb.toString();
			}
			ht.put(NWCGAAConstants.ENTITY_VALUE, incidentId);
			ht.put(NWCGAAConstants.ENTITY_NAME, NWCGAAConstants.INCIDENT_SERVICE_GROUP_NAME);
		} else if (serviceName.equalsIgnoreCase(NWCGWebServicesConstant.XPATH_GET_INCIDENT_IK)) {
			String incidentKey = XPathUtil.getString(doc, NWCGWebServicesConstant.XPATH_GET_INCIDENT_IK_INCIDENT_KEY);
			if (incidentKey == null) {
				incidentKey = NWCGConstants.EMPTY_STRING;
			}
			ht.put(NWCGAAConstants.ENTITY_KEY, incidentKey);
			String incidentId = XPathUtil.getString(doc, NWCGAAConstants.XPATH_GET_INCIDENT_IK_UNIT_ID_PREFIX);
			if (incidentId == null) {
				incidentId = NWCGConstants.EMPTY_STRING;
			} else {
				StringBuffer sb = new StringBuffer(incidentId);
				sb.append(NWCGConstants.DASH).append(XPathUtil.getString(doc, NWCGAAConstants.XPATH_GET_INCIDENT_IK_UNIT_ID_SUFFIX));
				sb.append(NWCGConstants.DASH).append(XPathUtil.getString(doc, NWCGAAConstants.XPATH_GET_INCIDENT_IK_SEQNO));
				sb.append(NWCGConstants.DASH).append(XPathUtil.getString(doc, NWCGAAConstants.XPATH_GET_INCIDENT_IK_YEAR_CREATED));
				incidentId = sb.toString();
			}
			ht.put(NWCGAAConstants.ENTITY_VALUE, incidentId);
			ht.put(NWCGAAConstants.ENTITY_NAME, NWCGAAConstants.INCIDENT_SERVICE_GROUP_NAME);
		} else if (serviceName.equalsIgnoreCase(NWCGAAConstants.CREATE_CATALOG_ITEM_REQ)) {
			String catalogKey = XPathUtil.getString(doc, NWCGWebServicesConstant.XPATH_CREATE_CATALOG_ITEM_KEY);
			if (catalogKey == null) {
				catalogKey = NWCGConstants.EMPTY_STRING;
			}
			ht.put(NWCGAAConstants.ENTITY_KEY, catalogKey);
			String catalogValue = XPathUtil.getString(doc, NWCGAAConstants.XPATH_CREATE_CATALOG_ITEM_CODE);
			if (catalogValue == null) {
				catalogValue = NWCGConstants.EMPTY_STRING;
			} else {
				String uom = XPathUtil.getString(doc, NWCGAAConstants.XPATH_CREATE_UNIT_OF_ISSUE);
				if (uom != null) {
					catalogValue += NWCGConstants.DASH + uom;
				}
			}
			ht.put(NWCGAAConstants.ENTITY_VALUE, catalogValue);
			ht.put(NWCGAAConstants.ENTITY_NAME, NWCGAAConstants.CATALOG);
		} else if (serviceName.equalsIgnoreCase(NWCGAAConstants.UPDATE_CATALOG_ITEM_REQ)) {
			String catalogKey = XPathUtil.getString(doc, NWCGWebServicesConstant.XPATH_UPDATE_CATALOG_ITEM_KEY);
			if (catalogKey == null) {
				catalogKey = NWCGConstants.EMPTY_STRING;
			}
			ht.put(NWCGAAConstants.ENTITY_KEY, catalogKey);
			String catalogValue = XPathUtil.getString(doc, NWCGAAConstants.XPATH_UPDATE_CATALOG_ITEM_CODE);
			if (catalogValue == null) {
				catalogValue = NWCGConstants.EMPTY_STRING;
			} else {
				String uom = XPathUtil.getString(doc, NWCGAAConstants.XPATH_UPDATE_UNIT_OF_ISSUE);
				if (uom != null) {
					catalogValue += NWCGConstants.DASH + uom;
				}
			}
			ht.put(NWCGAAConstants.ENTITY_VALUE, catalogValue);
			ht.put(NWCGAAConstants.ENTITY_NAME, NWCGAAConstants.CATALOG);
		} else if (serviceName.equalsIgnoreCase(NWCGAAConstants.DELETE_CATALOG_ITEM_REQ)) {
			String catalogKey = XPathUtil.getString(doc, NWCGWebServicesConstant.XPATH_DELETE_CATALOG_ITEM_KEY);
			if (catalogKey == null) {
				catalogKey = NWCGConstants.EMPTY_STRING;
			}
			ht.put(NWCGAAConstants.ENTITY_KEY, catalogKey);
			String catalogValue = XPathUtil.getString(doc, NWCGWebServicesConstant.XPATH_DELETE_CATALOG_ITEM_CODE);
			if (catalogValue == null) {
				catalogValue = NWCGConstants.EMPTY_STRING;
			} else {
				String uom = XPathUtil.getString(doc, NWCGWebServicesConstant.XPATH_DELETE_CATALOG_ITEM_UNIT_OF_ISSUE);
				if (uom != null) {
					catalogValue += NWCGConstants.DASH + uom;
				}
			}
			ht.put(NWCGAAConstants.ENTITY_VALUE, catalogValue);
			ht.put(NWCGAAConstants.ENTITY_NAME, NWCGAAConstants.CATALOG);
		} else {
			// Unknown service name
		}
		
		logger.verbose("@@@@@ Exiting NWCGMessageStore::retrieveEntityInfo");
		return ht;
	}

	public void updateMessage(YFSEnvironment env, String distID, String type, String msg, String msg_type, String status, String systemName, String key, boolean isLatestInbound, String serviceName) throws Exception {
		logger.verbose("@@@@@ Entering NWCGMessageStore::updateMessage");

		final String methodName = "updateMessage";
		String latest_inbound = isLatestInbound ? NWCGConstants.YES : NWCGConstants.EMPTY_STRING;

		Document doc_rt = null;
		Document IBDoc = null;
		Document OBDoc = null;

		// Support sugested to remove Lockid from the template xml
		if (type.equals(NWCGAAConstants.MESSAGE_DIR_TYPE_OB)) {
			OBDoc = XMLUtil.createDocument(NWCGAAConstants.NWCG_OUTBOUND_MSG_ELM);
			logger.verbose("@@@@@ OBDoc before changes is: " + XMLUtil.extractStringFromDocument(OBDoc));
		}

		// Support sugested to remove Lockid from the template xml
		if (type.equals(NWCGAAConstants.MESSAGE_DIR_TYPE_IB)) {
			IBDoc = XMLUtil.createDocument(NWCGAAConstants.NWCG_INBOUND_MSG_ELM);
			logger.verbose("@@@@@ IBDoc before changes is: " + XMLUtil.extractStringFromDocument(IBDoc));
		}

		if (type.equals(NWCGAAConstants.MESSAGE_DIR_TYPE_OB)) {
			if (!StringUtil.isEmpty(distID) || distID.equalsIgnoreCase(" ")) {
				OBDoc.getDocumentElement().setAttribute(NWCGAAConstants.DIST_ID_ATTR, distID);
			}
			if (!StringUtil.isEmpty(msg)) {
				OBDoc.getDocumentElement().setAttribute(NWCGAAConstants.MESSAGE, msg.trim());
			}
			if (!StringUtil.isEmpty(msg_type)) {
				OBDoc.getDocumentElement().setAttribute(NWCGAAConstants.MESSAGE_TYPE, msg_type);
			}
			if (!StringUtil.isEmpty(status)) {
				OBDoc.getDocumentElement().setAttribute(NWCGAAConstants.MESSAGE_STATUS, status);
			}
			if (!StringUtil.isEmpty(latest_inbound)) {
				OBDoc.getDocumentElement().setAttribute(NWCGAAConstants.LATEST_INBOUND, latest_inbound);
			}
			if (!StringUtil.isEmpty(key)) {
				OBDoc.getDocumentElement().setAttribute(NWCGAAConstants.MESSAGE_KEY, key);
			}
			if (!StringUtil.isEmpty(serviceName)) {
				OBDoc.getDocumentElement().setAttribute(NWCGAAConstants.MESSAGE_NAME, serviceName);
			}
			if (!StringUtil.isEmpty(systemName)) {
				OBDoc.getDocumentElement().setAttribute(NWCGAAConstants.SYSTEM_NAME_ATTR, systemName);
			}
			logger.verbose("@@@@@ Input to NWCGChangeOBMessage" + XMLUtil.extractStringFromDocument(OBDoc));
			doc_rt = CommonUtilities.invokeService(env, NWCGAAConstants.SDF_CHANGE_OB_MESSAGE_NAME, OBDoc);
		}
		if (type.equals(NWCGAAConstants.MESSAGE_DIR_TYPE_IB)) {
			if (!StringUtil.isEmpty(distID)) {
				IBDoc.getDocumentElement().setAttribute(NWCGAAConstants.DIST_ID_ATTR, distID);
			}
			if (!StringUtil.isEmpty(msg.trim())) {
				IBDoc.getDocumentElement().setAttribute(NWCGAAConstants.MESSAGE, msg.trim());
			}
			if (!StringUtil.isEmpty(msg_type)) {
				IBDoc.getDocumentElement().setAttribute(NWCGAAConstants.MESSAGE_TYPE, msg_type);
			}
			if (!StringUtil.isEmpty(status)) {
				IBDoc.getDocumentElement().setAttribute(NWCGAAConstants.MESSAGE_STATUS, status);
			}
			if (!StringUtil.isEmpty(latest_inbound)) {
				IBDoc.getDocumentElement().setAttribute(NWCGAAConstants.LATEST_INBOUND, latest_inbound);
			}
			if (!StringUtil.isEmpty(key)) {
				IBDoc.getDocumentElement().setAttribute(NWCGAAConstants.MESSAGE_KEY, key);
			}
			if (!StringUtil.isEmpty(serviceName)) {
				IBDoc.getDocumentElement().setAttribute(NWCGAAConstants.MESSAGE_NAME, serviceName);
			}
			if (!StringUtil.isEmpty(systemName)) {
				IBDoc.getDocumentElement().setAttribute(NWCGAAConstants.SYSTEM_NAME_ATTR, systemName);
			}
			logger.verbose("@@@@@ Input to NWCGChangeIBMessage" + XMLUtil.extractStringFromDocument(IBDoc));
			doc_rt = CommonUtilities.invokeService(env, NWCGAAConstants.SDF_CHANGE_IB_MESSAGE_NAME, IBDoc);
		}
		logger.verbose("@@@@@ changed doc::" + XMLUtil.extractStringFromDocument(doc_rt));
	
		logger.verbose("@@@@@ Exiting com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStore:updateMessage");
	}

	/**
	 * This method
	 * 
	 * @param msg
	 * @param serviceName
	 * @return
	 * @throws Exception
	 */
	public String storeInboundMessage(String dist_id, String msg, String msg_type, String status, String systemName, String serviceName, HashMap<Object, Object> msgMap) throws Exception {
		logger.verbose("@@@@@ Entering NWCGMessageStore::storeInboundMessage");
		
		String msg_str = "<NWCGInboundMessage Createprogid=\"\" Createts=\"\"  " + "DistributionID=\"\" LatestInbound=\"\" Lockid=\"\" Message=\"\" " + "MessageKey=\"\" MessageStatus=\"\" MessageType=\"\" Modifyprogid=\"\" " + "SystemName=\"\"/>";
		Document IBDoc = XMLUtil.getDocument(msg_str);

		// Pull the YFSEnvironment object out of the msgMap
		YFSEnvironment env = (YFSEnvironment) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_ENV);

		// Only if we can't pull the YFSEnvironment from the message map will we create a new one
		if (env == null) {
			env = CommonUtilities.createEnvironment(NWCGAAConstants.ENV_USER_ID, NWCGAAConstants.ENV_PROG_ID);
		}

		Element elem = IBDoc.getDocumentElement();
		elem.setAttribute(NWCGAAConstants.DIST_ID_ATTR, dist_id);
		elem.setAttribute(NWCGAAConstants.MESSAGE, msg.trim());
		elem.setAttribute(NWCGAAConstants.MESSAGE_TYPE, msg_type);
		elem.setAttribute(NWCGAAConstants.MESSAGE_STATUS, status);
		elem.setAttribute(NWCGAAConstants.SYSTEM_NAME_ATTR, systemName);
		elem.setAttribute(NWCGAAConstants.MESSAGE_NAME, serviceName);
		Document doc_rt = CommonUtilities.invokeService(env, NWCGAAConstants.SDF_CREATE_IB_MESSAGE_NAME, IBDoc);
		logger.verbose("@@@@@ doc_rt::" + XMLUtil.extractStringFromDocument(doc_rt));

		String sdf_xml_name = NWCGAAConstants.NWCG_INBOUND_MSG_ELM;
		Element el = (Element) doc_rt.getElementsByTagName(sdf_xml_name).item(0);
		String key = el.getAttribute(NWCGAAConstants.MESSAGE_KEY);
		logger.verbose("@@@@@ MessageKey:" + key);
		
		logger.verbose("@@@@@ Exiting NWCGMessageStore::storeInboundMessage");
		return key;
	}

	/**
	 * This method gets
	 * 
	 * @param msg
	 * @param serviceName
	 * @return
	 * @throws Exception
	 */
	public String storeOutboundMessage(HashMap<Object, Object> map, String msg_type, String status) throws Exception {
		logger.verbose("@@@@@ Entering NWCGMessageStore::storeOutboundMessage 1");
		
		String key = null;
		String msg_str = "<NWCGOutboundMessage Createprogid=\"\" Createts=\"\"  " + "DistributionID=\"\" LatestInbound=\"\" Lockid=\"\" Message=\"\" " + "MessageKey=\"\" MessageStatus=\"\" MessageType=\"\" Modifyprogid=\"\" " + "EntityKey=\"\" EntityName=\"\" EntityValue=\"\" SystemName=\"\"/>";
		Document OBDoc = XMLUtil.getDocument(msg_str);

		// Pull the YFSEnvironment object out of the msgMap
		YFSEnvironment env = (YFSEnvironment) map.get(NWCGMessageStoreInterface.MESSAGE_MAP_ENV);

		// Only if we can't pull the YFSEnvironment from the message map will we create a new one
		if (env == null) {
			env = CommonUtilities.createEnvironment(NWCGAAConstants.ENV_USER_ID, NWCGAAConstants.ENV_PROG_ID);
		}

		Element elem = OBDoc.getDocumentElement();
		elem.setAttribute(NWCGAAConstants.DIST_ID_ATTR, (String) map.get(MESSAGE_MAP_DISTID));
		elem.setAttribute(NWCGAAConstants.MESSAGE, (String) map.get(MESSAGE_MAP_MSGBODY));
		elem.setAttribute(NWCGAAConstants.MESSAGE_TYPE, msg_type);
		elem.setAttribute(NWCGAAConstants.MESSAGE_STATUS, status);
		elem.setAttribute(NWCGAAConstants.SYSTEM_NAME_ATTR, (String) map.get(MESSAGE_MAP_SYSNAME));
		elem.setAttribute(NWCGAAConstants.MESSAGE_NAME, (String) map.get(MESSAGE_MAP_MSGNAME));
		if (StringUtil.isEmpty((String) map.get(MESSAGE_MAP_ENTKEY)))
			map.put(MESSAGE_MAP_ENTKEY, NWCGConstants.EMPTY_STRING);
		elem.setAttribute(NWCGAAConstants.ENTITY_KEY, (String) map.get(MESSAGE_MAP_ENTKEY));
		if (StringUtil.isEmpty((String) map.get(MESSAGE_MAP_ENTNAME)))
			map.put(MESSAGE_MAP_ENTNAME, MESSAGE_OB_DEFAULTENTITYNAME);
		elem.setAttribute(NWCGAAConstants.ENTITY_NAME, (String) map.get(MESSAGE_MAP_ENTNAME));
		if (StringUtil.isEmpty((String) map.get(MESSAGE_MAP_ENTVALUE)))
			map.put(MESSAGE_MAP_ENTVALUE, MESSAGE_OB_DEFAULTENTITYVAL);
		elem.setAttribute(NWCGAAConstants.ENTITY_VALUE, (String) map.get(MESSAGE_MAP_ENTVALUE));
		Document doc_rt = CommonUtilities.invokeService(env, NWCGAAConstants.SDF_CREATE_OB_MESSAGE_NAME, OBDoc);
		logger.verbose("doc_rt::" + XMLUtil.extractStringFromDocument(doc_rt));
		String sdf_xml_name = NWCGAAConstants.NWCG_OUTBOUND_MSG_ELM;
		Element el = (Element) doc_rt.getElementsByTagName(sdf_xml_name).item(0);
		key = el.getAttribute(NWCGAAConstants.MESSAGE_KEY);
		logger.verbose("@@@@@ MessageKey:" + key);
		
		logger.verbose("@@@@@ Exiting NWCGMessageStore::storeOutboundMessage 1");
		return key;
	}

	public void updateOutboundMessage(YFSEnvironment env, String distID, String message_key) throws Exception {
		logger.verbose("@@@@@ Entering NWCGMessageStore::updateOutboundMessage 2");
		
		final String methodName = "updateOutboundMessage";
		Document OBDoc = XMLUtil.createDocument(NWCGAAConstants.NWCG_OUTBOUND_MSG_ELM);
		OBDoc = setAttribute(OBDoc, NWCGAAConstants.DIST_ID_ATTR, distID);
		OBDoc = setAttribute(OBDoc, NWCGAAConstants.MESSAGE_KEY, message_key);
		logger.verbose("@@@@@ Input to NWCGChangeOBMessage" + XMLUtil.extractStringFromDocument(OBDoc));
		Document doc_rt = CommonUtilities.invokeService(env, NWCGAAConstants.SDF_CHANGE_OB_MESSAGE_NAME, OBDoc);
		logger.verbose("@@@@@ changed doc::" + XMLUtil.extractStringFromDocument(doc_rt));
		
		logger.verbose("@@@@@ Exiting NWCGMessageStore::updateOutboundMessage 2");
	}

	/**
	 * This method
	 * 
	 * @param msg
	 * @param messageName
	 * @return
	 * @throws Exception
	 */
	public void updateOutboundMessage(YFSEnvironment env, String distID, String msg, String msg_type, String status, String systemName, String key, String messageName) throws Exception {
		logger.verbose("@@@@@ Entering NWCGMessageStore::updateOutboundMessage 3");

		final String methodName = "updateOutboundMessage";
		Document OBDoc = XMLUtil.createDocument(NWCGAAConstants.NWCG_OUTBOUND_MSG_ELM);
		OBDoc = setAttribute(OBDoc, NWCGAAConstants.LATEST_INBOUND, "N");
		OBDoc.getDocumentElement().setAttribute(NWCGAAConstants.DIST_ID_ATTR, distID);
		OBDoc = setAttribute(OBDoc, NWCGAAConstants.MESSAGE, msg);
		OBDoc = setAttribute(OBDoc, NWCGAAConstants.MESSAGE_TYPE, msg_type);
		OBDoc = setAttribute(OBDoc, NWCGAAConstants.MESSAGE_STATUS, status);
		OBDoc = setAttribute(OBDoc, NWCGAAConstants.MESSAGE_KEY, key);
		OBDoc = setAttribute(OBDoc, NWCGAAConstants.MESSAGE_NAME, messageName);
		OBDoc = setAttribute(OBDoc, NWCGAAConstants.SYSTEM_NAME_ATTR, systemName);
		logger.verbose("@@@@@ updateOutboundMessage>>doc to NWCGChangeOBMessage" + XMLUtil.extractStringFromDocument(OBDoc));
		Document doc_rt = CommonUtilities.invokeService(env, NWCGAAConstants.SDF_CHANGE_OB_MESSAGE_NAME, OBDoc);
		logger.verbose("@@@@@ updateOutboundMessage return >>" + XMLUtil.extractStringFromDocument(doc_rt));
		
		logger.verbose("@@@@@ Exiting NWCGMessageStore::updateOutboundMessage 3");
	}

	public void updateOutboundMessage(YFSEnvironment env, String distID, String msg, String msg_type, String status, String systemName, String key, String messageName, String strEntityKey) throws Exception {
		logger.verbose("@@@@@ Entering NWCGMessageStore::updateOutboundMessage 4");
		
		final String methodName = "updateOutboundMessage";
		Document OBDoc = XMLUtil.createDocument(NWCGAAConstants.NWCG_OUTBOUND_MSG_ELM);
		OBDoc = setAttribute(OBDoc, NWCGAAConstants.LATEST_INBOUND, "N");
		OBDoc.getDocumentElement().setAttribute(NWCGAAConstants.DIST_ID_ATTR, distID);
		OBDoc = setAttribute(OBDoc, NWCGAAConstants.MESSAGE, msg);
		OBDoc = setAttribute(OBDoc, NWCGAAConstants.MESSAGE_TYPE, msg_type);
		OBDoc = setAttribute(OBDoc, NWCGAAConstants.MESSAGE_STATUS, status);
		OBDoc = setAttribute(OBDoc, NWCGAAConstants.MESSAGE_KEY, key);
		OBDoc = setAttribute(OBDoc, NWCGAAConstants.MESSAGE_NAME, messageName);
		OBDoc = setAttribute(OBDoc, NWCGAAConstants.SYSTEM_NAME_ATTR, systemName);
		OBDoc = setAttribute(OBDoc, NWCGAAConstants.ENTITY_KEY, strEntityKey);
		logger.verbose("@@@@@ updateOutboundMessage>>doc to NWCGChangeOBMessage" + XMLUtil.extractStringFromDocument(OBDoc));
		Document doc_rt = CommonUtilities.invokeService(env, NWCGAAConstants.SDF_CHANGE_OB_MESSAGE_NAME, OBDoc);
		logger.verbose("@@@@@ updateOutboundMessage return >>" + XMLUtil.extractStringFromDocument(doc_rt));

		logger.verbose("@@@@@ Exiting NWCGMessageStore::updateOutboundMessage 4");
	}

	protected void updateInboundMessage(YFSEnvironment env, String distID, String msg, String msg_type, String status, String systemName, String key, String msgName) throws Exception {
		logger.verbose("@@@@@ Entering NWCGMessageStore::updateInboundMessage 1");
		updateInboundMessage(env, distID, msg, msg_type, status, systemName, key, msgName, NWCGConstants.EMPTY_STRING, NWCGConstants.EMPTY_STRING, NWCGConstants.EMPTY_STRING);
		logger.verbose("@@@@@ Exiting NWCGMessageStore::updateInboundMessage 1");
	}

	/**
	 * This method
	 * 
	 * @param msg
	 * @param serviceName
	 * @return
	 * @throws Exception
	 */
	protected void updateInboundMessage(YFSEnvironment env, String distID, String msg, String msg_type, String status, String systemName, String key, String msgName, String entKey, String entVal, String entName) throws Exception {
		logger.verbose("@@@@@ Entering NWCGMessageStore::updateInboundMessage 2");

		final String methodName = "updateInboundMessage";
		Document IBDoc = XMLUtil.createDocument(NWCGAAConstants.NWCG_INBOUND_MSG_ELM);
		IBDoc = setAttribute(IBDoc, NWCGAAConstants.LATEST_INBOUND, NWCGConstants.YES);
		IBDoc = setAttribute(IBDoc, NWCGAAConstants.DIST_ID_ATTR, distID);
		IBDoc = setAttribute(IBDoc, NWCGAAConstants.MESSAGE, msg.trim());
		IBDoc = setAttribute(IBDoc, NWCGAAConstants.MESSAGE_TYPE, msg_type);
		IBDoc = setAttribute(IBDoc, NWCGAAConstants.MESSAGE_STATUS, status);
		IBDoc = setAttribute(IBDoc, NWCGAAConstants.MESSAGE_KEY, key);
		IBDoc = setAttribute(IBDoc, NWCGAAConstants.MESSAGE_NAME, msgName);
		IBDoc = setAttribute(IBDoc, NWCGAAConstants.SYSTEM_NAME_ATTR, systemName);
		IBDoc = setAttribute(IBDoc, NWCGAAConstants.MDTO_ENTKEY, entKey);
		IBDoc = setAttribute(IBDoc, NWCGAAConstants.MDTO_ENTVALUE, entVal);
		IBDoc = setAttribute(IBDoc, NWCGAAConstants.MDTO_ENTNAME, entName);
		logger.verbose("@@@@@ Input to NWCGChangeIBMessage" + XMLUtil.extractStringFromDocument(IBDoc));
		Document doc_rt = CommonUtilities.invokeService(env, NWCGAAConstants.SDF_CHANGE_IB_MESSAGE_NAME, IBDoc);
		logger.verbose("@@@@@ Output from NWCGChangeIBMessage: " + XMLUtil.extractStringFromDocument(doc_rt));
		
		logger.verbose("@@@@@ Exiting NWCGMessageStore::updateInboundMessage 2");
	}

	// to be moved out
	private Document setAttribute(Document doc, String attribute, String value) {
		logger.verbose("@@@@@ In NWCGMessageStore::setAttribute");
		if (doc != null) {
			if (!StringUtil.isEmpty(value))
				doc.getDocumentElement().setAttribute(attribute, value);
		}
		return doc;
	}

	public HashMap<String, String> checkMessageNameAndDistID(String distID, String messageName) {
		logger.verbose("@@@@@ Entering NWCGMessageStore::checkMessageNameAndDistID");
		
		final String methodName = "checkMessageNameAndDistID";
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			YFSEnvironment env = CommonUtilities.createEnvironment(NWCGAAConstants.ENV_USER_ID, NWCGAAConstants.ENV_PROG_ID);
			Document OBDoc = XMLUtil.createDocument(NWCGAAConstants.NWCG_OUTBOUND_MSG_ELM);
			OBDoc = setAttribute(OBDoc, NWCGAAConstants.MESSAGE_NAME, messageName.trim());
			OBDoc = setAttribute(OBDoc, NWCGAAConstants.DIST_ID_ATTR, distID);
			Document opDoc = CommonUtilities.invokeService(env, NWCGAAConstants.SDF_GET_OB_MESSAGE_LIST_SERVICE_NAME, OBDoc);
			logger.verbose("checkMessageNameAndDistID result>>" + XMLUtil.extractStringFromDocument(opDoc));
			if (opDoc != null) {
				NodeList nl = opDoc.getElementsByTagName(NWCGAAConstants.NWCG_OUTBOUND_MSG_ELM);
				for (int i = 0; i < nl.getLength(); i++) {
					Element elem = (Element) nl.item(i);
					String msgType = elem.getAttribute(NWCGAAConstants.MESSAGE_TYPE);
					if (!msgType.equals(NWCGAAConstants.MESSAGE_TYPE_START.trim())) {
						map.put(NWCGAAConstants.LATEST_MESSAGE_KEY, elem.getAttribute(NWCGAAConstants.MESSAGE_KEY));
						map.put(NWCGAAConstants.ENTITY_KEY, elem.getAttribute(NWCGAAConstants.ENTITY_KEY));
						map.put(NWCGAAConstants.ENTITY_NAME, elem.getAttribute(NWCGAAConstants.ENTITY_NAME));
						map.put(NWCGAAConstants.ENTITY_VALUE, elem.getAttribute(NWCGAAConstants.ENTITY_VALUE));
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			logger.error("!!!!! Exception thrown in method :" + e.toString());
		}
		logger.verbose("checkMessageNameAndDistID found key=" + map.get(NWCGAAConstants.LATEST_MESSAGE_KEY));
		
		logger.verbose("@@@@@ Exiting NWCGMessageStore::checkMessageNameAndDistID");
		return map;
	}

	public HashMap getResponseMessageForDistID(HashMap<Object, Object> msgMap) {
		logger.verbose("@@@@@ Entering NWCGMessageStore::getResponseMessageForDistID");
		
		final String methodName = "getResponseMessageForDistID";
		YFSEnvironment env = (YFSEnvironment) msgMap.get(MESSAGE_MAP_ENV);
		String distID = (String) msgMap.get(MESSAGE_MAP_DISTID);
		logger.verbose("@@@@@ NWCGMessageStore.getResponseMessageForDistID>> the distID is: " + distID + " invoking " + NWCGAAConstants.SDF_GET_IB_MESSAGE_LIST_SERVICE_NAME);
		try {
			Document inDoc = XMLUtil.createDocument(NWCGAAConstants.NWCG_INBOUND_MSG_ELM);
			inDoc = setAttribute(inDoc, NWCGAAConstants.MESSAGE_STATUS, MESSAGE_STS_FAULT_READY_FOR_PICKUP);
			inDoc = setAttribute(inDoc, NWCGAAConstants.DIST_ID_ATTR, distID);
			inDoc = setAttribute(inDoc, NWCGAAConstants.MESSAGE_TYPE, NWCGAAConstants.MESSAGE_TYPE_LATEST);
			Document opDoc = CommonUtilities.invokeService(env, NWCGAAConstants.SDF_GET_IB_MESSAGE_LIST_SERVICE_NAME, inDoc);
			if (opDoc != null) {
				NodeList nl = opDoc.getElementsByTagName(NWCGAAConstants.NWCG_INBOUND_MSG_ELM);
				Element elem = null;
				logger.verbose("@@@@@ NWCGMessageStore.getResponseMessageForDistID>>Got response length= " + nl.getLength() + " first elem " + nl.item(0));
				if ((nl != null) && nl.getLength() > 0)
					elem = (Element) nl.item(0);
				else {
					inDoc = setAttribute(inDoc, NWCGAAConstants.MESSAGE_STATUS, MESSAGE_STS_FAILED_READY_FOR_PICKUP);
					opDoc = CommonUtilities.invokeService(env, NWCGAAConstants.SDF_GET_IB_MESSAGE_LIST_SERVICE_NAME, inDoc);
					if (opDoc != null) {
						nl = opDoc.getElementsByTagName(NWCGAAConstants.NWCG_INBOUND_MSG_ELM);
						logger.verbose("@@@@@ NWCGMessageStore.getResponseMessageForDistID>>Got 2nd response length=" + nl.getLength());
						if ((nl != null) && nl.getLength() > 0)
							elem = (Element) nl.item(0);
					}
				}
				if (elem != null) {
					String key = elem.getAttribute(NWCGAAConstants.MESSAGE_KEY);
					String msg = elem.getAttribute(NWCGAAConstants.MESSAGE);
					msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSG, msg);
					msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_LATESTKEY, key);
					logger.verbose("@@@@@ NWCGMessageStore.getResponseMessageForDistID>>retuning key= " + key + " message= " + msg);
				}

			}
		} catch (Exception e) {
			logger.error("!!!!! NWCGMessageStore.getResponseMessageForDistID>>Exception thrown in method :" + e.toString());
			logger.error(e.getLocalizedMessage(), e);
		}
		
		logger.verbose("@@@@@ Exiting NWCGMessageStore::getResponseMessageForDistID");
		return msgMap;
	}

	public String getLatestMessageKeyIBForDistID(YFSEnvironment env, String distID) {
		logger.verbose("@@@@@ In NWCGMessageStore::getLatestMessageKeyIBForDistID");
		return getMessageKeyIBForDistID(env, distID, true);
	}

	public String getMessageKeyIBForDistID(YFSEnvironment env, String distID, boolean latest) {
		logger.verbose("@@@@@ Entering NWCGMessageStore::getMessageKeyIBForDistID");
		
		logger.verbose("@@@@@ getLatestMessageKeyIBForDistID distID is: " + distID);
		String retVal = NWCGConstants.EMPTY_STRING;
		try {
			Document inDoc = XMLUtil.getDocument("<NWCGInboundMessage DistributionID=\"" + distID + "\" />");
			Document opDoc = CommonUtilities.invokeService(env, NWCGAAConstants.SDF_GET_IB_MESSAGE_LIST_SERVICE_NAME, inDoc);
			if (opDoc != null) {
				NodeList nl = opDoc.getElementsByTagName(NWCGAAConstants.NWCG_INBOUND_MSG_ELM);
				for (int i = 0; i < nl.getLength(); i++) {
					Element elem = (Element) nl.item(i);
					String msgType = elem.getAttribute(NWCGAAConstants.MESSAGE_TYPE);
					if (msgType.equalsIgnoreCase(NWCGAAConstants.MESSAGE_TYPE_LATEST) && latest) {
						retVal = elem.getAttribute(NWCGAAConstants.MESSAGE_KEY);
						break;
					}
					if (msgType.equalsIgnoreCase(NWCGAAConstants.MESSAGE_TYPE_START) && !latest) {
						retVal = elem.getAttribute(NWCGAAConstants.MESSAGE_KEY);
					}
				}
			}
		} catch (Exception e) {
			logger.error("!!!!! Exception thrown in method :" + e.toString());
			logger.error(e.getLocalizedMessage(), e);
		}
		logger.verbose("@@@@@ Returning IB msg key=" + retVal);
		
		logger.verbose("@@@@@ Exiting NWCGMessageStore::getMessageKeyIBForDistID");
		return retVal;
	}

	public String getLatestMessageKeyOBForDistID(YFSEnvironment env, String distID) {
		logger.verbose("@@@@@ Entering NWCGMessageStore::getLatestMessageKeyOBForDistID ");
		String latestMsgKey = NWCGConstants.EMPTY_STRING;
		try {
			Document inDoc = XMLUtil.getDocument("<NWCGOutboundMessage DistributionID=\"" + distID + "\" />");
			Document opDoc = CommonUtilities.invokeService(env, NWCGAAConstants.SDF_GET_OB_MESSAGE_LIST_SERVICE_NAME, inDoc);
			if (opDoc != null) {
				NodeList nl = opDoc.getElementsByTagName(NWCGAAConstants.NWCG_OUTBOUND_MSG_ELM);
				for (int i = 0; i < nl.getLength(); i++) {
					Element elem = (Element) nl.item(i);
					String msgType = elem.getAttribute(NWCGAAConstants.MESSAGE_TYPE);
					if (!msgType.equals(NWCGAAConstants.MESSAGE_TYPE_START.trim())) {
						latestMsgKey = elem.getAttribute(NWCGAAConstants.MESSAGE_KEY);
					}
				}
			}
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e.toString());
			logger.error(e.getLocalizedMessage(), e);
		}
		logger.verbose("about to return latestmsgkey");
		if (!StringUtil.isEmpty(latestMsgKey)) {
			logger.verbose("@@@@@ latestMsgKey :: " + latestMsgKey);
			logger.verbose("@@@@@ Exiting NWCGMessageStore::getLatestMessageKeyOBForDistID ");
			return latestMsgKey;
		} else { 
			latestMsgKey = ResourceUtil.get("NWCG_DO_NO_AUTH_DB_KEY", "9011917122761465");
			logger.verbose("@@@@@ latestMsgKey :: " + latestMsgKey);
			logger.verbose("@@@@@ Exiting NWCGMessageStore::getLatestMessageKeyOBForDistID ");
			return latestMsgKey;
		}
	}

	public void setMsgStatusProcessedSync(HashMap msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusProcessedSync");
		setOutboundMsgStatus(msgMap, MESSAGE_STS_SYNC_PROCESSED);
	}

	public void setMsgStatusSentSyncFailed(HashMap msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusSentSyncFailed");
		setOutboundMsgStatus(msgMap, MESSAGE_STS_SENT_FAULT);
	}

	public void setMsgStatusSentSyncFault(HashMap msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusSentSyncFault");
		setOutboundMsgStatus(msgMap, MESSAGE_STS_SENT_FAULT);
	}

	public void setMsgOBStatusSoapFault(HashMap<Object, Object> msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgOBStatusSoapFault");
		setOutboundMsgStatus(msgMap, MESSAGE_STS_SENT_FAULT);
	}

	public void setMsgStatusSentSync(HashMap msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusSentSync");
		setOutboundMsgStatus(msgMap, MESSAGE_STS_PROCESSED_SYNC);
	}

	public void setMsgStatusForcedResponseFault(HashMap msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusForcedResponseFault");
		setOutboundMsgStatus(msgMap, MESSAGE_STS_FORCED_RESPONSE_FAULT);
	}

	public void setMsgStatusForcedProcessedFault(HashMap msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusForcedProcessedFault");
		setOutboundMsgStatus(msgMap, MESSAGE_STS_FORCED_PROCESSED_FAULT);
	}

	public void setMsgStatusForcedProcessedFaultOB(HashMap msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusForcedProcessedFaultOB");
		setOutboundMsgStatus(msgMap, MESSAGE_STS_FORCED_PROCESSED_FAULT);
	}

	public void setMsgStatusForcedProcessedFaultIB(HashMap msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusForcedProcessedFaultIB");
		setInboundMsgStatus(msgMap, MESSAGE_STS_FORCED_PROCESSED_FAULT);
		setInboundMsgStatusOnly(msgMap, MESSAGE_STS_FORCED_PROCESSED_FAULT);
	}

	public void setMsgStatusForcedFailed(HashMap msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusForcedFailed");
		setOutboundMsgStatus(msgMap, MESSAGE_STS_FORCED_RESPONSE_SENT_FAILED);
	}

	public void setMsgStatusSentFailed(HashMap msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusSentFailed");
		setOutboundMsgStatus(msgMap, MESSAGE_STS_SENT_FAILED);
	}

	public void setMsgStatusSent(HashMap msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusSent");
		setOutboundMsgStatus(msgMap, MESSAGE_STS_SENT);
	}

	public void setMsgStatusSentFault(HashMap msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusSentFault");
		setOutboundMsgStatus(msgMap, MESSAGE_STS_SENT_FAULT);
	}

	public void setMsgStatusReceived(HashMap<Object, Object> msgMap, String latestMsgKey) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusReceived (1)");
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_LATESTKEY, latestMsgKey);
		setMsgStatusReceived(msgMap);
	}

	public void setMsgStatusReceived(HashMap<Object, Object> msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusReceived (2)");
		setOutboundMsgStatus(msgMap, MESSAGE_STS_RECEIVED);
	}

	public void setMsgStatusOutboundProcessed(HashMap msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusOutboundProcessed");
		setOutboundMsgStatus(msgMap, MESSAGE_STS_PROCESSED);
	}

	public void setMsgStatusProcessingFault(HashMap msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusProcessingFault (1)");
		setInboundMsgStatus(msgMap, MESSAGE_STS_PROCESSING_FAULT);
	}

	public void setMsgStatusProcessingFault(HashMap<Object, Object> msgMap, String faultText) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusProcessingFault (2)");
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY, faultText);
		setInboundMsgStatus(msgMap, MESSAGE_STS_PROCESSING_FAULT);
	}

	public void setMsgStatusSentForProcessing(HashMap<Object, Object> msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusSentForProcessing (1)");
		setInboundMsgStatus(msgMap, MESSAGE_STS_SENT_FOR_PROCESSING);
	}

	public void setMsgStatusSentForProcessing(HashMap<Object, Object> msgMap, String ack) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusSentForProcessing (2)");
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY, ack);
		setInboundMsgStatus(msgMap, MESSAGE_STS_SENT_FOR_PROCESSING);
	}

	public void setMsgStatusSecurityFailed(HashMap<Object, Object> msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusSecurityFailed (1)");
		setInboundMsgStatus(msgMap, MESSAGE_STS_SOAP_FAULT);
	}

	public void setMsgStatusSecurityFailed(HashMap<Object, Object> msgMap, String soapFaultXml) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusSecurityFailed (1)");
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY, soapFaultXml);
		setInboundMsgStatus(msgMap, MESSAGE_STS_SOAP_FAULT);
	}

	public void setMsgStatusSoapFault(HashMap<Object, Object> msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusSoapFault");
		setInboundMsgStatus(msgMap, MESSAGE_STS_SOAP_FAULT);
	}

	public void setMsgStatusAckNegative(HashMap msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusAckNegative");
		setInboundMsgStatus(msgMap, MESSAGE_STS_ACKNOWLEGED_NEGATIVE);
	}

	public void setMsgStatusPosRespPosted(HashMap msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusPosRespPosted");
		setInboundMsgStatus(msgMap, MESSAGE_STS_POS_RESP_POSTED);
	}

	public void setMsgStatusNegRespPosted(HashMap msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusNegRespPosted");
		setInboundMsgStatus(msgMap, MESSAGE_STS_NEG_RESP_POSTED);
	}

	public void setMsgStatusFailedReadyForPickup(HashMap msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusFailedReadyForPickup (1)");
		setInboundMsgStatus(msgMap, MESSAGE_STS_FAILED_READY_FOR_PICKUP);
	}

	public void setMsgStatusFaultReadyForPickup(HashMap msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusFaultReadyForPickup (2)");
		setInboundMsgStatus(msgMap, MESSAGE_STS_FAULT_READY_FOR_PICKUP);
	}

	public void setMsgStatusInboundProcessed(HashMap msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusInboundProcessed (1)");
		setInboundMsgStatus(msgMap, MESSAGE_STS_PROCESSED);
	}

	public void setMsgStatusInboundProcessed(HashMap<Object, Object> msgMap, String responseText) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusInboundProcessed (2)");
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY, responseText);
		setInboundMsgStatus(msgMap, MESSAGE_STS_PROCESSED);
	}

	public void setMsgStatusForcedProcessed(HashMap<Object, Object> msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusForcedProcessed");
		setInboundMsgStatusOnly(msgMap, MESSAGE_STS_FORCED_PROCESSED);
	}

	public void setMsgStatusForcedProcessedOB(HashMap<Object, Object> msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusForcedProcessedOB");
		setOutboundMsgStatus(msgMap, MESSAGE_STS_FORCED_PROCESSED);
	}

	public void setMsgStatusForcedProcessedIB(HashMap<Object, Object> msgMap) {
		logger.verbose("@@@@@ In NWCGMessageStore::setMsgStatusForcedProcessedIB");
		setInboundMsgStatus(msgMap, MESSAGE_STS_FORCED_PROCESSED);
		setInboundMsgStatusOnly(msgMap, MESSAGE_STS_FORCED_PROCESSED);
	}

	private void setInboundMsgStatusOnly(HashMap msgMap, String sts) {
		logger.verbose("@@@@@ Entering NWCGMessageStore::setInboundMsgStatusOnly");
		
		YFSEnvironment env = (YFSEnvironment) msgMap.get(MESSAGE_MAP_ENV);
		if (env == null) {
			try {
				env = CommonUtilities.createEnvironment(NWCGAAConstants.ENV_USER_ID, NWCGAAConstants.ENV_PROG_ID);
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
		String msg = (String) msgMap.get(MESSAGE_MAP_MSG);
		String key = (String) msgMap.get(MESSAGE_MAP_LATESTKEY);
		logger.verbose("setInboundMsgStatusOnly key=" + key + " status=" + sts);
		try {
			updateInboundMessage(env, NWCGConstants.EMPTY_STRING, msg, NWCGConstants.EMPTY_STRING, sts, NWCGConstants.EMPTY_STRING, key, NWCGConstants.EMPTY_STRING);
		} catch (Exception e) {
			logger.error("MessageStore.setInboundMsgStatusOnly exception>>" + e.toString());
		}
		logger.verbose("setInboundMsgStatusOnly done for key=" + key + " status=" + sts);
		
		logger.verbose("@@@@@ Exiting NWCGMessageStore::setInboundMsgStatusOnly");
	}

	private void setInboundMsgStatus(HashMap msgMap, String sts) {
		logger.verbose("@@@@@ Entering NWCGMessageStore::setInboundMsgStatus");
		
		YFSEnvironment env = (YFSEnvironment) msgMap.get(MESSAGE_MAP_ENV);
		if (env == null) {
			try {
				env = CommonUtilities.createEnvironment(NWCGAAConstants.ENV_USER_ID, NWCGAAConstants.ENV_PROG_ID);
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
		String distID = (String) msgMap.get(MESSAGE_MAP_DISTID);
		if (NWCGWebServiceUtils.isEmpty(distID))
			distID = getUUID();
		String msg = (String) msgMap.get(MESSAGE_MAP_MSGBODY);
		String msg_type = NWCGAAConstants.MESSAGE_TYPE_LATEST;
		String systemName = (String) msgMap.get(MESSAGE_MAP_SYSNAME);
		if (systemName == null)
			systemName = NWCGConstants.ROSS_SYSTEM;
		String key = (String) msgMap.get(MESSAGE_MAP_LATESTKEY);
		String messageName = NWCGConstants.EMPTY_STRING;
		String entKey = (String) msgMap.get(NWCGAAConstants.MDTO_ENTKEY);
		String entVal = (String) msgMap.get(NWCGAAConstants.MDTO_ENTVALUE);
		String entName = (String) msgMap.get(NWCGAAConstants.MDTO_ENTNAME);
		logger.verbose("setInboundMsgStatus key=" + key + " status=" + sts);
		try {
			updateInboundMessage(env, distID, msg, msg_type, sts, systemName, key, messageName, entKey, entVal, entName);
		} catch (Exception e) {
			logger.error("MessageStore.setInboundMsgStatus exception>>" + e.toString());
		}
		logger.verbose("setInboundMsgStatus done for key=" + key+ " status=" + sts);	
		
		logger.verbose("@@@@@ Exiting NWCGMessageStore::setInboundMsgStatus");
	}

	private void setOutboundMsgStatus(HashMap msgMap, String sts) {
		logger.verbose("@@@@@ Entering NWCGMessageStore::setOutboundMsgStatus");
		YFSEnvironment env = (YFSEnvironment) msgMap.get(MESSAGE_MAP_ENV);
		if (env == null) {
			try {
				env = CommonUtilities.createEnvironment(NWCGAAConstants.ENV_USER_ID, NWCGAAConstants.ENV_PROG_ID);
			} catch (Exception e) {
				logger.error("!!!!! Caught General Exception thrown >> " + e.toString());
				logger.error(e.getLocalizedMessage(), e);
			}
		}
		String distID = (String) msgMap.get(MESSAGE_MAP_DISTID);
		logger.verbose("@@@@@ distID = " + distID);
		String msg = (String) msgMap.get(MESSAGE_MAP_MSG);
		logger.verbose("@@@@@ msg = " + msg);
		String msg_type = NWCGAAConstants.MESSAGE_TYPE_LATEST;
		logger.verbose("@@@@@ msg_type = " + msg_type);
		String systemName = (String) msgMap.get(MESSAGE_MAP_SYSNAME);
		logger.verbose("@@@@@ systemName = " + systemName);
		String key = (String) msgMap.get(MESSAGE_MAP_LATESTKEY);
		logger.verbose("@@@@@ key = " + key);
		String messageName = (String) msgMap.get(MESSAGE_MAP_MSGNAME);
		logger.verbose("@@@@@ messageName = " + messageName);
		String strEntityKey = (String) msgMap.get(MESSAGE_MAP_ENTKEY);
		logger.verbose("@@@@@ strEntityKey = " + strEntityKey);
		try {
			logger.verbose("@@@@@ before updateOutboundMessage msg = " + msg);
			updateOutboundMessage(env, distID, msg, msg_type, sts, systemName, key, messageName, strEntityKey);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception thrown >> " + e.toString());
			logger.error(e.getLocalizedMessage(), e);
		}
		logger.verbose("@@@@@ setOutboundMsgStatus returns distId=" + distID + " key=" + key);	
		
		logger.verbose("@@@@@ Exiting NWCGMessageStore::setOutboundMsgStatus");
	}

	/**
	 * @param soapElem
	 */
	public String initInboundMessageFromMap(HashMap<Object, Object> msgMap) throws Exception {
		logger.verbose("@@@@@ Entering NWCGMessageStore::initInboundMessageFromMap");
		
		String body = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY);
		String systemName = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_SYSNAME);
		String serviceName = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_MSGNAME);
		String distID = (String) msgMap.get(NWCGMessageStoreInterface.MESSAGE_MAP_DISTID);
		if (StringUtil.isEmpty(distID)) {
			distID = getUUID();
			msgMap.put(MESSAGE_MAP_DISTID, distID);
		}
		logger.verbose("initInboundMessageFromMap>>distID=" + distID);
		String latest_message_key = "1112"; 
		try {
			String message_key = storeInboundMessage(distID, body, NWCGAAConstants.MESSAGE_TYPE_START, MESSAGE_STS_VOID, systemName, serviceName, msgMap);
			logger.verbose("initInboundMessageFromMap>>key=" + message_key);
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_KEY, message_key);
			latest_message_key = storeInboundMessage(distID, body, NWCGAAConstants.MESSAGE_TYPE_LATEST, MESSAGE_STS_RECEIVED, systemName, serviceName, msgMap);
			logger.verbose("initInboundMessageFromMap>>latest=" + latest_message_key);
			msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_LATESTKEY, latest_message_key);
		} catch (Exception e) {
			logger.error("!!!!! Exception thrown >>");
		}
		
		logger.verbose("@@@@@ Exiting NWCGMessageStore::initInboundMessageFromMap");
		return latest_message_key;
	}

	/**
	 * @param soapElem
	 */
	public String initOutboundMessageFromMap(HashMap<Object, Object> msgMap) throws Exception {
		logger.verbose("@@@@@ Entering NWCGMessageStore::initOutboundMessageFromMap");

		String distId = getUUID();
		logger.verbose("initOutboundMessageFromMap>generated DistId=" + distId);
		msgMap.put(MESSAGE_MAP_DISTID, distId);
		String message_key = storeOutboundMessage(msgMap, NWCGAAConstants.MESSAGE_TYPE_START, NWCGAAConstants.MESSAGE_STATUS_VOID);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_KEY, message_key);
		String latest_message_key = storeOutboundMessage(msgMap, NWCGAAConstants.MESSAGE_TYPE_LATEST, NWCGAAConstants.MESSAGE_STATUS_VOID);
		msgMap.put(NWCGMessageStoreInterface.MESSAGE_MAP_LATESTKEY, latest_message_key);
		logger.verbose("initOutboundMessageFromMap>latestkey=" + latest_message_key);
		
		logger.verbose("@@@@@ Exiting NWCGMessageStore::initOutboundMessageFromMap");
		return latest_message_key;
	}
}