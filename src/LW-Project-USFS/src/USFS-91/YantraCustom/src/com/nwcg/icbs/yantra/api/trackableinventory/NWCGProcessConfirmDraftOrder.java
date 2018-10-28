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

import javax.xml.parsers.ParserConfigurationException;

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
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * Implementation of NWCGTrackableInventoryMessageConsumer Interface.
 * 
 * @author Oxford
 */
public class NWCGProcessConfirmDraftOrder implements YIFCustomApi,
		NWCGITrackableRecordMutator {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGProcessConfirmDraftOrder.class);
	
	private Properties _properties;

	/**
	 * 
	 * @param arg0
	 * @throws Exception
	 */
	public void setProperties(Properties arg0) throws Exception {
		this._properties = arg0;
	}

	/**
	 * 
	 */
	public Document insertOrUpdateTrackableRecord(YFSEnvironment env,
			Document doc) throws Exception {
		// doc is the inXML from NWCGProcessBillingTransConfirmIncidentTO
		return updateTrackableRecord(env, doc);
	}

	/**
	 * this method is invoked when the user clicks on confirm Order for an
	 * incident to incident transfer fetch all the relevent information from the
	 * databse i.e. related to incident/issue put that in the custom table an
	 * UPDATE
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 * @throws Exception
	 */
	public Document updateTrackableRecord(YFSEnvironment env, Document inXML)
			throws Exception {
		try {
			// get the order element
			Element elemOrderRoot = inXML.getDocumentElement();
			if (elemOrderRoot != null) {
				// get all the order lines
				NodeList nlOrderLines = elemOrderRoot
						.getElementsByTagName("OrderLine");

				if (nlOrderLines != null) {
					String strOrderNo = elemOrderRoot.getAttribute("OrderNo");
					String strOrderDate = "";
					String strIncidentNo = "";
					String strExtnFsAcctCode = "";
					String strExtnBlmAcctCode = "";
					String strExtnOtherAcctCode = "";
					String strYear = "";
					String strCustomerId = "";
					String strCacheID = "";
					// fetchig all the details at order level
					strOrderDate = elemOrderRoot.getAttribute("OrderDate");
					strCacheID = elemOrderRoot.getAttribute("ShipNode");
					NodeList nlOrderExtn = elemOrderRoot
							.getElementsByTagName("Extn");
					if (nlOrderExtn != null && nlOrderExtn.getLength() > 0) {
						Element elemOrderExtn = (Element) nlOrderExtn.item(0);
						strIncidentNo = StringUtil.nonNull(elemOrderExtn
								.getAttribute("ExtnToIncidentNo"));
						strExtnFsAcctCode = StringUtil.nonNull(elemOrderExtn
								.getAttribute("ExtnToFsAcctCode"));
						strExtnBlmAcctCode = StringUtil.nonNull(elemOrderExtn
								.getAttribute("ExtnToBlmAcctCode"));
						strExtnOtherAcctCode = StringUtil.nonNull(elemOrderExtn
								.getAttribute("ExtnToOtherAcctCode"));
						strYear = StringUtil.nonNull(elemOrderExtn
								.getAttribute("ExtnToIncidentYear"));
					}
					if (logger.isVerboseEnabled())
						logger.verbose("Extracted values ==> " + strIncidentNo
								+ " " + strExtnFsAcctCode + " "
								+ strExtnBlmAcctCode + " "
								+ strExtnOtherAcctCode);
					Document getIncidentDetailsIP = XMLUtil
							.createDocument("NWCGIncidentOrder");

					Element elemGetIncidentDetails = getIncidentDetailsIP
							.getDocumentElement();
					elemGetIncidentDetails.setAttribute("IncidentNo",
							strIncidentNo);
					elemGetIncidentDetails.setAttribute("Year", strYear);

					if (logger.isVerboseEnabled())
						logger
								.verbose("NWCGProcessConfirmDraftOrder::updateTrackableRecord - invoking getIncidentOrderDetails with I/P "
										+ XMLUtil
												.getXMLString(getIncidentDetailsIP));
					// get the incident year and customerId
					Document docIncidentDetails = CommonUtilities
							.invokeService(
									env,
									NWCGConstants.NWCG_GET_INCIDENT_ORDER_SERVICE,
									getIncidentDetailsIP);
					Element elemIncidentdetails = docIncidentDetails
							.getDocumentElement();
					if (logger.isVerboseEnabled())
						logger
								.verbose("NWCGProcessConfirmDraftOrder::updateTrackableRecord - O/P "
										+ XMLUtil
												.getXMLString(docIncidentDetails));

					// strYear = elemIncidentdetails.getAttribute("Year");
					strCustomerId = elemIncidentdetails
							.getAttribute("CustomerId");
					if (logger.isVerboseEnabled())
						logger
								.verbose("NWCGProcessConfirmDraftOrder:: total number of order lines = "
										+ nlOrderLines.getLength());

					// for all the order lines
					for (int index = 0; index < nlOrderLines.getLength(); index++) {
						Element elemOrderLine = (Element) nlOrderLines
								.item(index);
						// extract the req ship date from the order line
						// strOrderDate =
						// elemOrderLine.getAttribute("ReqShipDate");
						String strItemID = "";
						NodeList nlItem = elemOrderLine
								.getElementsByTagName("Item");
						if (nlItem != null && nlItem.getLength() > 0) {
							Element elemItem = (Element) nlItem.item(0);
							strItemID = elemItem.getAttribute("ItemID");
						}

						/* CR-631 07/19/11 */
						Element ItemPriceInfo = (Element) elemOrderLine
								.getElementsByTagName("LinePriceInfo").item(0);
						String UnitCost = ItemPriceInfo
								.getAttribute("UnitPrice");
						/* CR-631 07/19/11 */

						NodeList nlExtn = elemOrderLine
								.getElementsByTagName("Extn");
						// get the trackable id
						if (nlExtn != null && nlExtn.getLength() > 0) {
							Element elemExtn = (Element) nlExtn.item(0);
							String strSerialNo = elemExtn
									.getAttribute("ExtnTrackableId");

							if (StringUtil.isEmpty(strSerialNo)) {
								if (logger.isVerboseEnabled())
									logger
											.verbose("NWCGProcessConfirmDraftOrder::updateTrackableRecord - continuing as the serial number is empty");
								// if its null, continue with next record
								continue;
							}
							// set up all the attributes
							Document docCreateTrackableInventoryIP = XMLUtil
									.createDocument("NWCGTrackableItem");
							Element elemCreateTrackableInventoryIP = docCreateTrackableInventoryIP
									.getDocumentElement();

							elemCreateTrackableInventoryIP.setAttribute(
									"SerialStatus",
									NWCGConstants.SERIAL_STATUS_ISSUED);
							elemCreateTrackableInventoryIP
									.setAttribute(
											"SerialStatusDesc",
											NWCGConstants.SERIAL_STATUS_TRANSFERRED_DESC);
							elemCreateTrackableInventoryIP
									.setAttribute(
											"Type",
											NWCGConstants.TRANSACTION_INFO_TYPE_INCIDENT_TRANSFER);
							// cache id shuld be blank
							elemCreateTrackableInventoryIP.setAttribute(
									"StatusCacheID", strCacheID);
							elemCreateTrackableInventoryIP.setAttribute(
									"ItemID", strItemID);
							elemCreateTrackableInventoryIP.setAttribute(
									"SerialNo", strSerialNo);
							elemCreateTrackableInventoryIP.setAttribute(
									"SecondarySerial", CommonUtilities
											.getSecondarySerial(env,
													strSerialNo, strItemID));
							elemCreateTrackableInventoryIP.setAttribute(
									"StatusIncidentNo", strIncidentNo);
							elemCreateTrackableInventoryIP.setAttribute(
									"LastIncidentNo", strIncidentNo);
							elemCreateTrackableInventoryIP.setAttribute(
									"LastDocumentNo", strOrderNo);
							elemCreateTrackableInventoryIP.setAttribute(
									"FSAccountCode", strExtnFsAcctCode);
							elemCreateTrackableInventoryIP.setAttribute(
									"BLMAccountCode", strExtnBlmAcctCode);
							elemCreateTrackableInventoryIP.setAttribute(
									"OtherAccountCode", strExtnOtherAcctCode);
							elemCreateTrackableInventoryIP.setAttribute(
									"LastTransactionDate", strOrderDate);
							/* CR-631 07/19/11 */
							elemCreateTrackableInventoryIP.setAttribute(
									"LastIssuePrice", UnitCost);
							/* CR-631 07/19/11 */
							// last incident year should be blank out
							elemCreateTrackableInventoryIP.setAttribute(
									"LastIncidentYear", "");
							elemCreateTrackableInventoryIP.setAttribute(
									"StatusIncidentYear", strYear);
							elemCreateTrackableInventoryIP.setAttribute(
									"StatusBuyerOrganizationCode",
									strCustomerId);
							elemCreateTrackableInventoryIP.setAttribute(
									"LastBuyerOrnanizationCode", strCustomerId);

							Document opgetAllChildSerialComps = getAllChildSerialComps(
									env, strSerialNo, strItemID);

							updateSerialStatusOfCompItems(env,
									opgetAllChildSerialComps,
									docCreateTrackableInventoryIP);

							String ParentSerialKey = getParentSerialKey(env,
									strSerialNo, strItemID);

							Document ParentSerialDoc = XMLUtil
									.createDocument("SerialList");
							String ParentSerialNo = "";
							if (ParentSerialKey.length() > 0) {
								ParentSerialDoc = getParentSerialNo(env,
										ParentSerialDoc, ParentSerialKey);
							}

							NodeList serialNodeList = ParentSerialDoc
									.getDocumentElement().getElementsByTagName(
											"Serial");
							if (serialNodeList.getLength() > 0) {
								for (int cnt1 = 0; cnt1 < serialNodeList
										.getLength(); cnt1++) {
									Element SerialElem = (Element) serialNodeList
											.item(cnt1);
									ParentSerialNo = SerialElem
											.getAttribute("SerialNo");
									Document docParentSerailRecord = getParentRecord(
											env, ParentSerialNo);

									// now update the parent record
									updateParentRecordAsReceivedAsComponents(
											env, docParentSerailRecord,
											docCreateTrackableInventoryIP);
								}
							}

							if (logger.isVerboseEnabled())
								logger
										.verbose("NCGProcessConfirmShipment input to updateTrackableInventory service => "
												+ XMLUtil
														.getXMLString(docCreateTrackableInventoryIP));

							CommonUtilities
									.invokeService(
											env,
											NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE,
											docCreateTrackableInventoryIP);
						}
					}
				}
			}
		} catch (Exception rethrowable) {
			// re-throwing here, so transactions get rolled back
			throw rethrowable;
		}
		return inXML;
	}

	private void updateSerialStatusOfCompItems(YFSEnvironment env, Document inDoc, Document TrackableDoc) throws Exception {
		NodeList serialNodeList = inDoc.getDocumentElement().getElementsByTagName("Serial");
		Element elemTrackable = TrackableDoc.getDocumentElement();
		for (int count = 0; count < serialNodeList.getLength(); count++) {
			Element SerialElem = (Element) serialNodeList.item(count);
			String serialNo = SerialElem.getAttribute("SerialNo");
			Document ipNWCGGetTrackableItemListService = XMLUtil
					.getDocument("<NWCGTrackableItem SerialNo=\"" + serialNo
							+ "\" />");
			Document opNWCGGetTrackableItemListService = CommonUtilities
					.invokeService(env,
							NWCGConstants.NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE,
							ipNWCGGetTrackableItemListService);
			Element nwcgTrackableItemElem = (Element) opNWCGGetTrackableItemListService
					.getDocumentElement().getElementsByTagName(
							"NWCGTrackableItem").item(0);
			// if(nwcgTrackableItemElem.getAttribute("SerialStatus")=="K"){
			Document updateDoc = XMLUtil.createDocument("NWCGTrackableItem");
			Element updateDocElem = updateDoc.getDocumentElement();
			updateDocElem.setAttribute("SerialStatus",
					NWCGConstants.SERIAL_STATUS_ISSUED);
			updateDocElem
					.setAttribute("SerialStatusDesc", "Transferred in Kit");
			updateDocElem.setAttribute("Type",
					NWCGConstants.TRANSACTION_INFO_TYPE_INCIDENT_TRANSFER);
			updateDocElem.setAttribute("AcquisitionCost", nwcgTrackableItemElem
					.getAttribute("AcquisitionCost"));
			updateDocElem.setAttribute("AcquisitionDate", nwcgTrackableItemElem
					.getAttribute("AcquisitionDate"));
			updateDocElem.setAttribute("ItemID", nwcgTrackableItemElem
					.getAttribute("ItemID"));
			updateDocElem.setAttribute("ItemShortDescription",
					nwcgTrackableItemElem.getAttribute("ItemShortDescription"));
			updateDocElem.setAttribute("KitItemID", nwcgTrackableItemElem
					.getAttribute("KitItemID"));
			updateDocElem.setAttribute("KitSerialNo", nwcgTrackableItemElem
					.getAttribute("KitSerialNo"));
			updateDocElem.setAttribute("OwnerUnitID", nwcgTrackableItemElem
					.getAttribute("OwnerUnitID"));
			updateDocElem.setAttribute("RevisionNo", nwcgTrackableItemElem
					.getAttribute("RevisionNo"));
			updateDocElem.setAttribute("SecondarySerial", nwcgTrackableItemElem
					.getAttribute("SecondarySerial"));
			updateDocElem.setAttribute("SerialNo", nwcgTrackableItemElem
					.getAttribute("SerialNo"));
			updateDocElem.setAttribute("TrackableItemKey",
					nwcgTrackableItemElem.getAttribute("TrackableItemKey"));
			updateDocElem.setAttribute("UnitOfMeasure", nwcgTrackableItemElem
					.getAttribute("UnitOfMeasure"));

			updateDocElem.setAttribute("StatusCacheID", elemTrackable
					.getAttribute("StatusCacheID"));
			updateDocElem.setAttribute("StatusIncidentNo", elemTrackable
					.getAttribute("StatusIncidentNo"));
			updateDocElem.setAttribute("LastIncidentNo", elemTrackable
					.getAttribute("LastIncidentNo"));
			updateDocElem.setAttribute("LastDocumentNo", elemTrackable
					.getAttribute("LastDocumentNo"));
			updateDocElem.setAttribute("FSAccountCode", elemTrackable
					.getAttribute("FSAccountCode"));
			updateDocElem.setAttribute("OverrideCode", elemTrackable
					.getAttribute("OverrideCode"));
			updateDocElem.setAttribute("BLMAccountCode", elemTrackable
					.getAttribute("BLMAccountCode"));
			updateDocElem.setAttribute("OtherAccountCode", elemTrackable
					.getAttribute("OtherAccountCode"));
			updateDocElem.setAttribute("LastTransactionDate", elemTrackable
					.getAttribute("LastTransactionDate"));
			updateDocElem.setAttribute("LastIncidentYear", elemTrackable
					.getAttribute("LastIncidentYear"));
			updateDocElem.setAttribute("StatusIncidentYear", elemTrackable
					.getAttribute("StatusIncidentYear"));
			updateDocElem.setAttribute("StatusBuyerOrganizationCode",
					elemTrackable.getAttribute("StatusBuyerOrganizationCode"));
			updateDocElem.setAttribute("LastBuyerOrnanizationCode",
					elemTrackable.getAttribute("LastBuyerOrnanizationCode"));

			try {
				CommonUtilities.invokeService(env,
						NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE,
						updateDoc);
			} catch (Exception e) {
				//logger.printStackTrace(e);
				Document doc = XMLUtil.createDocument("Inbox");
				Element root_element = doc.getDocumentElement();
				root_element.setAttribute("ApiName",
						"NWCGProcessConfirmShipment");
				// database size is restricted to 4000 chars but in case of
				// confirm shipment we are getting an exception with 10K lines
				// in it
				if (e.getMessage() != null && e.getMessage().length() > 3800)
					root_element.setAttribute("DetailDescription", e
							.getMessage().substring(0, 3800));
				else
					root_element.setAttribute("DetailDescription", StringUtil
							.nonNull(e.getMessage()));

				CommonUtilities.raiseTrackableAlert(env, doc);
			}
		}
	}

	private Document getAllChildSerialComps(YFSEnvironment env, String strSerialNo, String strItemID) throws Exception {

		String strSerialKey = "";
		Document docGetSerialListOP = null;
		Document docGetSerialListOP1 = null;
		Document docGetSerialListIP = null;
		Document docGetSerialListIP1 = null;
		Element elemGetSerialListIP = null;
		Element elemGetSerialListIP1 = null;

		/**
		 * This API call needs to return ALL records that have the given Serial Number
		 * <Serial SerialNo="0340-GBK-0159-267"> 
		 * </Serial>
		 * 
		 * Hence We need to call it WITHOUT ITEM ID 
		 * 
		 * this way, IF it returns more than 1 record (and Serial No SHOULD BE unique and hence we should only ever be getting 1 record),
		 * then we can catch the duplicate database record and stop the transaction by throwing the exception. 
		 * If this duplicate record goes uncaught then the recursive method calls for child components will cause the system to crash. 
		 */
		docGetSerialListIP = XMLUtil.createDocument("Serial");
		elemGetSerialListIP = docGetSerialListIP.getDocumentElement();
		elemGetSerialListIP.setAttribute("SerialNo", strSerialNo);

		env.setApiTemplate("getSerialList", "NWCGProcessBlindReturn_getSerialList_GetKey");
		docGetSerialListOP = CommonUtilities.invokeAPI(env, "getSerialList", docGetSerialListIP);
		env.clearApiTemplate("getSerialList");

		// SerialList should return only one record
		NodeList listGetSerialListOP = docGetSerialListOP.getElementsByTagName("SerialList");
		if (listGetSerialListOP.getLength() == 1) {
			NodeList listGetSerialListOP_Serial = docGetSerialListOP.getElementsByTagName("Serial");
			if (listGetSerialListOP_Serial.getLength() > 1) {
				String message = "There is more than 1 Serial Number for this item in the system, please contact support to have this data error fixed...";
				throw new YFSException(message);
			}
		}

		/**
		 * This API call needs to return ONLY 1 record. 
		 * <Serial SerialNo="0340-GBK-0159-267"> 
		 * <InventoryItem ItemID="000340"/> 
		 * </Serial>
		 * 
		 * We need to call the API WITH ITEM ID
		 * 
		 * this way, IF the Trackable ID that was entered is the one for the Parent's Child and NOT for the Parent itself, 
		 * then the Serial Key will be BLANK and hence we will need to throw the exception stating that the Serial Number that
		 * the user entered is incorrect. 
		 * IF this incorrect serial number is allowed to remain uncaught, the system will get ALL trackable items and update them ALL. 
		 */
		docGetSerialListIP = XMLUtil.createDocument("Serial");
		elemGetSerialListIP = docGetSerialListIP.getDocumentElement();
		elemGetSerialListIP.setAttribute("SerialNo", strSerialNo);
		Element el_InvItem = docGetSerialListIP.createElement("InventoryItem");
		elemGetSerialListIP.appendChild(el_InvItem);
		el_InvItem.setAttribute("ItemID", strItemID);

		env.setApiTemplate("getSerialList", "NWCGProcessBlindReturn_getSerialList_GetKey");
		docGetSerialListOP = CommonUtilities.invokeAPI(env, "getSerialList", docGetSerialListIP);
		env.clearApiTemplate("getSerialList");

		// strSerialKey should NEVER be BLANK
		strSerialKey = StringUtil.nonNull(XPathUtil.getString(docGetSerialListOP, "SerialList/Serial/@GlobalSerialKey"));
		if (strSerialKey.equals(null) || strSerialKey.equals("")) {
			String message = "Serial Key is BLANK...please enter the correct serial number for this item...If you need help doing this, then contact support...";
			throw new YFSException(message);
		}

		/**
		 * At this point, now that we have survived BOTH check, we can proceed and process the Parent's Children. 
		 */
		// have to reset the api actually midifies the input document
		docGetSerialListIP1 = XMLUtil.createDocument("Serial");
		elemGetSerialListIP1 = docGetSerialListIP1.getDocumentElement();
		// get all the serial numbers whoes parent is this serial number will
		// get all the child components clearing off all the child elements
		elemGetSerialListIP1.setAttribute("SerialNo", "");
		// getting all the serials whoes parent is this serial
		elemGetSerialListIP1.setAttribute("ParentSerialKey", strSerialKey);
		env.setApiTemplate("getSerialList", "NWCGProcessBlindReturn_getSerialList");
		docGetSerialListOP1 = CommonUtilities.invokeAPI(env, "getSerialList", docGetSerialListIP1);
		env.clearApiTemplate("getSerialList");
		docGetSerialListOP1 = getSubKitComponents(env, docGetSerialListOP1, docGetSerialListOP1);
		return docGetSerialListOP1;
	}

	private Document getSubKitComponents(YFSEnvironment env,
			Document SerialDoc, Document SerialInDoc) throws Exception {
		NodeList serialNodeList = SerialDoc.getDocumentElement()
				.getElementsByTagName("Serial");
		for (int count = 0; count < serialNodeList.getLength(); count++) {
			Element SerialElem = (Element) serialNodeList.item(count);
			String serialKey = SerialElem.getAttribute("GlobalSerialKey");
			Document docGetSerialListOP1 = getComponents(env, serialKey);
			NodeList serialNodeList1 = docGetSerialListOP1.getDocumentElement()
					.getElementsByTagName("Serial");
			if (serialNodeList1.getLength() > 0) {
				for (int cnt1 = 0; cnt1 < serialNodeList1.getLength(); cnt1++) {
					Element SerialElem1 = (Element) serialNodeList1.item(cnt1);
					Element ele1 = SerialInDoc.createElement("Serial");
					SerialInDoc.getDocumentElement().appendChild(ele1);
					XMLUtil.copyElement(SerialInDoc, SerialElem1, ele1);
				}
				SerialInDoc = getSubKitComponents(env, docGetSerialListOP1,
						SerialInDoc);
			}
		}

		return SerialInDoc;
	}

	private Document getComponents(YFSEnvironment env, String strSerialKey)
			throws Exception {
		Document docGetSerialListIP2 = null;
		Document docGetSerialListOP2 = null;
		Element elemGetSerialListIP2 = null;

		if (logger.isVerboseEnabled())
			logger.verbose("starting getSubKitComponents ");

		docGetSerialListIP2 = XMLUtil.createDocument("Serial");
		elemGetSerialListIP2 = docGetSerialListIP2.getDocumentElement();

		// getting all the serials whoes parent is this serial
		elemGetSerialListIP2.setAttribute("ParentSerialKey", strSerialKey);
		env.setApiTemplate("getSerialList",
				"NWCGProcessBlindReturn_getSerialList");

		docGetSerialListOP2 = CommonUtilities.invokeAPI(env, "getSerialList",
				docGetSerialListIP2);

		return docGetSerialListOP2;
	}

	public String getParentSerialKey(YFSEnvironment env, String SerialNo,
			String strItemID) throws Exception {
		try {
			String GSerialKey = "";
			Document inDoc = XMLUtil.newDocument();
			Document outDoc = XMLUtil.newDocument();
			Element el_PSerial = inDoc.createElement("Serial");
			inDoc.appendChild(el_PSerial);
			el_PSerial.setAttribute("SerialNo", SerialNo);
			Element el_InvItem = inDoc.createElement("InventoryItem");
			el_PSerial.appendChild(el_InvItem);
			el_InvItem.setAttribute("ItemID", strItemID);
			env.setApiTemplate("getSerialList",
					"NWCGProcessBlindReturn_getSerialList");
			outDoc = CommonUtilities.invokeAPI(env, "getSerialList", inDoc);
			Element PSerialOut = (Element) outDoc.getDocumentElement()
					.getElementsByTagName("Serial").item(0);
			GSerialKey = PSerialOut.getAttribute("ParentSerialKey");
			env.clearApiTemplate("getSerialList");
			return GSerialKey;
		} catch(NullPointerException npe) {
			String message = "The Serial Number you used is incorrect, it is not a parent but a child! please contact support...";
			throw new YFSException(message);
		} catch (Exception ex) {
			String message = "Caught General Exception!" + ex;
			throw new YFSException(message);
		}
	}

	public Document getParentSerialNo(YFSEnvironment env, Document ParentDoc,
			String ParentSerialKey) throws Exception {

		String PSerialNo = "";

		Document inDoc = XMLUtil.newDocument();
		Document outDoc = XMLUtil.newDocument();

		Element el_PSerial = inDoc.createElement("Serial");
		inDoc.appendChild(el_PSerial);
		el_PSerial.setAttribute("GlobalSerialKey", ParentSerialKey);
		env.setApiTemplate("getSerialList",
				"NWCGProcessBlindReturn_getSerialList");
		outDoc = CommonUtilities.invokeAPI(env, "getSerialList", inDoc);

		Element PSerialOut = (Element) outDoc.getDocumentElement()
				.getElementsByTagName("Serial").item(0);
		PSerialNo = PSerialOut.getAttribute("SerialNo");
		String PSerialKey = PSerialOut.getAttribute("ParentSerialKey");
		env.clearApiTemplate("getSerialList");
		Element ele1 = ParentDoc.createElement("Serial");
		ParentDoc.getDocumentElement().appendChild(ele1);
		XMLUtil.copyElement(ParentDoc, PSerialOut, ele1);

		if (PSerialKey.length() > 0) {
			ParentDoc = getParentSerialNo(env, ParentDoc, PSerialKey);
		}

		return ParentDoc;
	}

	private void updateParentRecordAsReceivedAsComponents(YFSEnvironment env,
			Document docParentSerailRecord, Document Trackableupdatedoc)
			throws Exception {
		if (docParentSerailRecord != null) {
			Element elem = docParentSerailRecord.getDocumentElement();
			Element elemTrackableupddoc = Trackableupdatedoc
					.getDocumentElement();
			NodeList nlNWCGTrackableItem = elem
					.getElementsByTagName("NWCGTrackableItem");
			if (nlNWCGTrackableItem != null) {
				for (int index = 0; index < nlNWCGTrackableItem.getLength(); index++) {
					Element elemNWCGTrackableItem = (Element) nlNWCGTrackableItem
							.item(index);
					String TrackableItemKey = elemNWCGTrackableItem
							.getAttribute("TrackableItemKey");
					Document trackableDoc = null;
					trackableDoc = XMLUtil.createDocument("NWCGTrackableItem");
					Element elemTrackable = trackableDoc.getDocumentElement();
					elemTrackable.setAttribute("TrackableItemKey",
							TrackableItemKey);
					elemTrackable.setAttribute("SerialStatus",
							NWCGConstants.SERIAL_STATUS_ISSUED);
					elemTrackable.setAttribute("SerialStatusDesc",
							"Transferred as Component");
					elemTrackable
							.setAttribute(
									"Type",
									NWCGConstants.TRANSACTION_INFO_TYPE_INCIDENT_TRANSFER);

					elemTrackable.setAttribute("StatusCacheID",
							elemTrackableupddoc.getAttribute("StatusCacheID"));
					elemTrackable.setAttribute("StatusIncidentNo",
							elemTrackableupddoc
									.getAttribute("StatusIncidentNo"));
					elemTrackable.setAttribute("LastIncidentNo",
							elemTrackableupddoc.getAttribute("LastIncidentNo"));
					elemTrackable.setAttribute("LastDocumentNo",
							elemTrackableupddoc.getAttribute("LastDocumentNo"));
					elemTrackable.setAttribute("FSAccountCode",
							elemTrackableupddoc.getAttribute("FSAccountCode"));
					elemTrackable.setAttribute("OverrideCode",
							elemTrackableupddoc.getAttribute("OverrideCode"));
					elemTrackable.setAttribute("BLMAccountCode",
							elemTrackableupddoc.getAttribute("BLMAccountCode"));
					elemTrackable.setAttribute("OtherAccountCode",
							elemTrackableupddoc
									.getAttribute("OtherAccountCode"));
					elemTrackable.setAttribute("LastTransactionDate",
							elemTrackableupddoc
									.getAttribute("LastTransactionDate"));
					elemTrackable.setAttribute("LastIncidentYear",
							elemTrackableupddoc
									.getAttribute("LastIncidentYear"));
					elemTrackable.setAttribute("StatusIncidentYear",
							elemTrackableupddoc
									.getAttribute("StatusIncidentYear"));
					elemTrackable
							.setAttribute(
									"StatusBuyerOrganizationCode",
									elemTrackableupddoc
											.getAttribute("StatusBuyerOrganizationCode"));
					elemTrackable.setAttribute("LastBuyerOrnanizationCode",
							elemTrackableupddoc
									.getAttribute("LastBuyerOrnanizationCode"));

					try {
						CommonUtilities
								.invokeService(
										env,
										NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE,
										trackableDoc);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//logger.printStackTrace(e);
					}
				}// end for
			}
		}
	}

	private Document getParentRecord(YFSEnvironment env, String strSerialNo)
			throws ParserConfigurationException {
		Document trackableDoc = XMLUtil.createDocument("NWCGTrackableItem");
		Element elemTrackable = trackableDoc.getDocumentElement();
		elemTrackable.setAttribute("SerialNo", strSerialNo);

		Document returnDoc = null;
		try {
			returnDoc = CommonUtilities.invokeService(env, ResourceUtil
					.get("nwcg.icbs.gettrackableitemlist.service"),
					trackableDoc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//logger.printStackTrace(e);
		}
		return returnDoc;
	}
}