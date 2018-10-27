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

package com.nwcg.icbs.yantra.api.trackableinventory;

import java.util.Properties;
import java.util.StringTokenizer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGProcessTaskCompleted implements YIFCustomApi,
		NWCGITrackableRecordMutator {
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGRemoveTrackableItems.class);
	
	private Properties _properties;

	public void setProperties(Properties arg0) throws Exception {
		this._properties = arg0;
		// TODO Auto-generated method stub

	}

	/*
	 * This method is invoked when ever a user completes the task
	 */
	public Document updateTrackableRecord(YFSEnvironment env, Document inXML)
			throws Exception {
		try {
			// get the order element
			Element elemTaskRoot = inXML.getDocumentElement();
			String strTargetZoneId = "", strSerialNo = "";

			if (logger.isVerboseEnabled())
				logger.verbose("NWCGProcessKittingCompleted::createTrackableRecord received the input document "
						+ XMLUtil.getXMLString(inXML));

			if (elemTaskRoot != null) {
				NodeList nlInventory = elemTaskRoot
						.getElementsByTagName("Inventory");
				Element elemInventory = null;
				// get the attributes
				if (nlInventory != null && nlInventory.getLength() > 0) {
					elemInventory = (Element) nlInventory.item(0);
					strSerialNo = StringUtil.nonNull(elemInventory
							.getAttribute("SerialNo"));
				}
				if (strSerialNo.equals("")) {
					if (logger.isVerboseEnabled())
						logger.verbose("NWCGProcessTaskCompleted::returning as no serial number task id ");

					return inXML;
				}
				// fetch this if and only if the serial number exists
				strTargetZoneId = StringUtil.nonNull(elemTaskRoot
						.getAttribute("TargetZoneId"));
				// if the target zone is not null update the record
				if (!strTargetZoneId.equals("")) {
					updateTrackableRecord(env, strSerialNo, strTargetZoneId);
				}

			}// end if orderlines not null

		} catch (Exception e) {
			logger.error("!!!!! NWCGProcessConfirmShipment::createTrackableRecord Caught Exception "
								+ e, e);
		}

		return inXML;
	}

	/*
	 * this method updates the trackable record if the zone passed is
	 * refurb,kitting or dekitting zone
	 */
	private void updateTrackableRecord(YFSEnvironment env, String strSerialNo,
			String strTargetZoneId) throws Exception {
		boolean bIsRefurbOrKittingDeKittingZone = isRefurbOrKittingDeKittingZone(strTargetZoneId);

		if (logger.isVerboseEnabled())
			logger.verbose("NWCGProcessTaskCompleted::updateTrackableRecord ZONE "
					+ strTargetZoneId
					+ " Flag bIsRefurbOrKittingDeKittingZone "
					+ bIsRefurbOrKittingDeKittingZone);

		if (bIsRefurbOrKittingDeKittingZone) {
			Document doc = XMLUtil.createDocument("NWCGTrackableItem");
			Element elem = doc.getDocumentElement();
			elem.setAttribute("SerialNo", strSerialNo);
			elem.setAttribute("SerialStatus",
					NWCGConstants.NWCG_STATUS_CODE_WORKORDERED);
			elem.setAttribute("SerialStatusDesc",
					NWCGConstants.NWCG_STATUS_CODE_WORKORDERED_DESC);
			// update the record
			CommonUtilities.invokeService(env, ResourceUtil
					.get("nwcg.icbs.changetrackableinventory.service"), doc);
		}
	}

	/*
	 * method identifies if the zone is refurb,kitting or dekitting zone
	 */
	private boolean isRefurbOrKittingDeKittingZone(String strTargetZoneId) {
		// defined in yantraimpl.properties file with this key
		String strValidZones = ResourceUtil
				.get("nwcg.icbs.kittingdekittingrefurbzone");

		if (!strValidZones.equals("")) {
			StringTokenizer tokenizer = new StringTokenizer(strValidZones, ",");
			if (tokenizer != null) {
				while (tokenizer.hasMoreTokens()) {
					String strZones = StringUtil.nonNull(tokenizer.nextToken());
					// if values matches return true
					if (strZones.equals(strTargetZoneId))
						return true;
				}
			}
		}
		// otherwise always false
		return false;
	}

	public Document insertOrUpdateTrackableRecord(YFSEnvironment env,
			Document doc) throws Exception {
		// TODO Auto-generated method stub
		return updateTrackableRecord(env, doc);
	}
}
