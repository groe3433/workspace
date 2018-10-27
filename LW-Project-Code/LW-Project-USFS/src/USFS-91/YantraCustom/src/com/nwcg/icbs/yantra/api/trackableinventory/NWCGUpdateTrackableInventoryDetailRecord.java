package com.nwcg.icbs.yantra.api.trackableinventory;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGUpdateTrackableInventoryDetailRecord implements YIFCustomApi {

	public Document updateTrackableInventoryDetailRecord(YFSEnvironment env,
			Document inDoc) throws Exception {

		updateTrackableItemDetails(env, inDoc);
		updateTagDetails(env, inDoc);

		return inDoc;
	}

	/**
	 * This function obtains Item details pertaining to the trackable Item key
	 * passed.
	 * 
	 * @param env
	 * @param trackableItemKey
	 * @return
	 * @throws Exception
	 */
	private Document getTrackableItemRecord(YFSEnvironment env,
			String trackableItemKey) throws Exception {
		Document tirDocument = XMLUtil.newDocument();
		Element element = tirDocument.createElement("NWCGTrackableItem");
		element.setAttribute("TrackableItemKey", trackableItemKey);
		tirDocument.appendChild(element);

		Document trackOutDoc = CommonUtilities
				.invokeService(env,
						NWCGConstants.NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE,
						tirDocument);
		return trackOutDoc;
	}

	private Document getInventoryDetails(YFSEnvironment env, String serialNo)
			throws Exception {
		Document invInDocument = XMLUtil.newDocument();
		Element element = invInDocument.createElement("Serial");
		element.setAttribute("SerialNo", serialNo);
		invInDocument.appendChild(element);

		Document invOutDoc = CommonUtilities.invokeAPI(env,
				NWCGConstants.API_GET_SERIAL_LIST, invInDocument);
		return invOutDoc;
	}

	/**
	 * This function updates the Trackable Item Details for the trackable item.
	 * It updates the NWCG_TRACKABLE_ITEM table.
	 * 
	 * @param env
	 * @param tirInDoc
	 * @return
	 * @throws Exception
	 */
	private Document updateTrackableItemDetails(YFSEnvironment env,
			Document tirInDoc) throws Exception {
		Document tirOutDoc = CommonUtilities.invokeService(env,
				"NWCGChangeTrackableInventoryRecordService", tirInDoc);
		return tirOutDoc;
	}

	/**
	 * This function updates the Tag Details for the trackable item. It updates
	 * the YFS_INVENTORY_TAG table.
	 * 
	 * @param env
	 * @param tagInDoc
	 * @return
	 * @throws Exception
	 */
	private Document updateTagDetails(YFSEnvironment env, Document tagInDoc)
			throws Exception {
		Element rootElement = tagInDoc.getDocumentElement();
		String strTrackableItemKey = rootElement
				.getAttribute("TrackableItemKey");
		String strManufacturer = rootElement.getAttribute("LotAttribute1");
		String strModelName = rootElement.getAttribute("LotAttribute3");
		String strDateLastTested = rootElement.getAttribute("RevisionNo");
		String strManufacturingDate = rootElement
				.getAttribute("ManufacturingDate");

		String strItemId = null;
		String strNode = null;
		String strSerialNo = null;
		Document tirDoc = getTrackableItemRecord(env, strTrackableItemKey);
		Element trackOutElem = (Element) tirDoc.getDocumentElement()
				.getElementsByTagName("NWCGTrackableItem").item(0);
		if (trackOutElem != null) {
			strItemId = trackOutElem.getAttribute("ItemID");
			strNode = trackOutElem.getAttribute("StatusCacheID");
			strSerialNo = trackOutElem.getAttribute("SerialNo");
		}

		Document invDetailsDoc = getInventoryDetails(env, strSerialNo);

		String inventoryStatus = null;
		Element serialElem = (Element) invDetailsDoc.getDocumentElement()
				.getElementsByTagName("Serial").item(0);
		if (serialElem != null) {
			inventoryStatus = serialElem.getAttribute("InventoryStatus");
		}

		Document changeLocInvAttrDoc = XMLUtil.newDocument();

		Element rootChangeLocInvAttr = changeLocInvAttrDoc
				.createElement("ChangeLocationInventoryAttributes");
		changeLocInvAttrDoc.appendChild(rootChangeLocInvAttr);

		rootChangeLocInvAttr.setAttribute("EnterpriseCode",
				NWCGConstants.ENTERPRISE_CODE);
		rootChangeLocInvAttr.setAttribute("Node", strNode);
		rootChangeLocInvAttr.setAttribute("IgnoreOrdering", "Y");

		Element source = changeLocInvAttrDoc.createElement("Source");
		Element toInv = changeLocInvAttrDoc.createElement("ToInventory");
		toInv.setAttribute("InventoryStatus", inventoryStatus);

		Element tagDetailTo = changeLocInvAttrDoc.createElement("TagDetail");
		tagDetailTo.setAttribute("LotAttribute1", strManufacturer);
		tagDetailTo.setAttribute("LotAttribute3", strModelName);
		tagDetailTo.setAttribute("LotNumber", strSerialNo);
		tagDetailTo.setAttribute("ManufacturingDate", strManufacturingDate);
		tagDetailTo.setAttribute("RevisionNo", strDateLastTested);

		Element invItemTo = changeLocInvAttrDoc.createElement("InventoryItem");
		invItemTo.setAttribute("ItemID", strItemId);

		toInv.appendChild(tagDetailTo);
		toInv.appendChild(invItemTo);
		source.appendChild(toInv);

		Element fromInv = changeLocInvAttrDoc.createElement("FromInventory");
		fromInv.setAttribute("InventoryStatus", inventoryStatus);

		Element invItemFrom = changeLocInvAttrDoc
				.createElement("InventoryItem");
		invItemFrom.setAttribute("ItemID", strItemId);

		Element tagDetailFrom = changeLocInvAttrDoc.createElement("TagDetail");
		tagDetailFrom.setAttribute("LotNumber", strSerialNo);

		Element serialListFrom = changeLocInvAttrDoc
				.createElement("SerialList");
		Element serialDetailFrom = changeLocInvAttrDoc
				.createElement("SerialDetail");
		serialDetailFrom.setAttribute("SerialNo", strSerialNo);

		serialListFrom.appendChild(serialDetailFrom);
		fromInv.appendChild(invItemFrom);
		fromInv.appendChild(tagDetailFrom);
		fromInv.appendChild(serialListFrom);
		source.appendChild(fromInv);

		rootChangeLocInvAttr.appendChild(source);

		Element audit = changeLocInvAttrDoc.createElement("Audit");
		audit.setAttribute("ReasonCode", "OTHERS");
		rootChangeLocInvAttr.appendChild(audit);

		Document tagOutDoc = CommonUtilities.invokeAPI(env,
				NWCGConstants.API_CHG_LOCN_INV_ATTR, changeLocInvAttrDoc);
		return tagInDoc;
	}

	public void setProperties(Properties arg0) throws Exception {
	}
}