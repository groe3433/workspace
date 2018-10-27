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

package com.nwcg.icbs.yantra.ib.read.handler.util;

import java.util.HashMap;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.ib.read.handler.NWCGUpdateIncidentNotificationHandler;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;

public final class NotificationCommonUtilities {

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NotificationCommonUtilities.class);

	/**
	 * Pass the correct node list from which we need to get the incident key.
	 * This method parses the input xml and returns the incident number and year
	 * in a map. Incident Number and Year are mandatory in the input xml. So,
	 * null check is not done for those values
	 * 
	 * @param doc
	 * @param index
	 */
	public static HashMap<String, String> parseAndRtnIncidentKey(NodeList incidentKeyNL) {
		int index = 0;
		return parseAndRtnIncidentKey(incidentKeyNL, index);
	}

	public static HashMap<String, String> parseAndRtnIncidentKey(NodeList incidentKeyNL, int index) {
		logger.verbose("@@@@@ Entering NotificationCommonUtilities::parseAndRtnIncidentKey @@@@@");
		// IncidentKey is a mandatory element and the root element will have only one occurence. Not checking for NULL. All the elements under incidentkey are also mandatory, so not checking for NULL
		String seqNum = "";
		String unitIDPrefix = "";
		String unitIDSuffix = "";
		String year = "";
		String entityId = "";
		StringBuffer sbIncNo = new StringBuffer("");
		Node incidentKeyNode = incidentKeyNL.item(index);
		NodeList naturalIncKeyNL = incidentKeyNode.getChildNodes();
		for (int incTrav = 0; incTrav < naturalIncKeyNL.getLength(); incTrav++) {
			Node naturalIncKeyNode = naturalIncKeyNL.item(incTrav);
			if (naturalIncKeyNode.getNodeName().equalsIgnoreCase(NWCGConstants.INC_NOTIF_NAT_INCKEY_ATTR)) {
				NodeList incFieldsNL = naturalIncKeyNode.getChildNodes();
				for (int incFieldsTrav = 0; incFieldsTrav < incFieldsNL.getLength(); incFieldsTrav++) {
					Node incField = incFieldsNL.item(incFieldsTrav);
					String incFieldNodeName = incField.getNodeName();
					if (incFieldNodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_HOSTID_ATTR)) {
						NodeList unitIDNL = incField.getChildNodes();
						for (int unitIDTrav = 0; unitIDTrav < unitIDNL.getLength(); unitIDTrav++) {
							Node unitIDNode = unitIDNL.item(unitIDTrav);
							String unitIDNodeName = unitIDNode.getNodeName();
							if (unitIDNodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_UNIT_ID_PREFIX_ATTR)) {
								unitIDPrefix = unitIDNode.getTextContent();
							} else if (unitIDNodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_UNIT_ID_SUFFIX_ATTR)) {
								unitIDSuffix = unitIDNode.getTextContent();
							}
						}
					} else if (incFieldNodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_SEQ_NO_ATTR)) {
						seqNum = incField.getTextContent();
					} else if (incFieldNodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_YR_CREATED_ATTR)) {
						year = incField.getTextContent();
					}
				} 
			} else if (naturalIncKeyNode.getNodeName().equalsIgnoreCase(
					NWCGConstants.INC_NOTIF_INCID_ATTR)) {
				// here naturalIncKeyNode is IncidentID node
				NodeList incFieldsIncIdNL = naturalIncKeyNode.getChildNodes();
				for (int incFieldsIncTrav = 0; incFieldsIncTrav < incFieldsIncIdNL.getLength(); incFieldsIncTrav++) {
					Node incField = incFieldsIncIdNL.item(incFieldsIncTrav);
					String incFieldNodeName = incField.getNodeName();
					entityId = (incFieldNodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_ENTID_ATTR)) ? incField.getTextContent() : entityId;
				}
			}
		} 
		sbIncNo.append(unitIDPrefix).append("-").append(unitIDSuffix).append("-");
		int seqNumLen = 6;
		for (int i = 0; i < seqNumLen - seqNum.length(); i++) {
			sbIncNo.append("0");
		}
		sbIncNo.append(seqNum);
		HashMap<String, String> hMap = new HashMap<String, String>();
		hMap.put(NWCGConstants.INCIDENT_NO_ATTR, sbIncNo.toString());
		hMap.put(NWCGConstants.INCIDENT_ID_ATTR, entityId);
		hMap.put(NWCGConstants.YEAR_ATTR, year);
		logger.verbose("@@@@@ Incident Number : " + sbIncNo.toString() + ", Year : " + year);
		logger.verbose("@@@@@ Entering NotificationCommonUtilities::parseAndRtnIncidentKey @@@@@");
		return hMap;
	}
}