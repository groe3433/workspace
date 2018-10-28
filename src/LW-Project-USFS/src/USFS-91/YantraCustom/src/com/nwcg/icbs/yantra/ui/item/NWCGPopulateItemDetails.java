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

package com.nwcg.icbs.yantra.ui.item;

import java.util.Properties;

import com.yantra.yfc.log.YFCLogCategory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.updateRoss.NWCGRossNotification;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * @author jvishwakarma populates the item details when user tabs out form item
 *         ML - Updated to include NSN and Cache Description CR 581 & 582
 */
public class NWCGPopulateItemDetails implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGPopulateItemDetails.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yantra.interop.japi.YIFCustomApi#setProperties(java.util.Properties)
	 */
	public void setProperties(Properties props) throws Exception {
	}

	public Document populateItemDetailsWithRFIQuantities(YFSEnvironment env,
			Document inDoc) throws Exception {

		String strProductLine = NWCGConstants.EMPTY_STRING;

		if (logger.isVerboseEnabled())
			logger.verbose("Entering NWCGPopulateItemDetails()->"
					+ XMLUtil.extractStringFromDocument(inDoc));

		String strItemId = NWCGConstants.EMPTY_STRING;// fetch the itemid from
														// the input xml
		String strNode = NWCGConstants.EMPTY_STRING;// fetch the node from input
													// xml
		Element root_item = inDoc.getDocumentElement();
		if (root_item != null) {
			strItemId = root_item.getAttribute(NWCGConstants.ITEM_ID);
			strNode = root_item.getAttribute(NWCGConstants.NODE);
		}
		if (logger.isVerboseEnabled())
			logger.verbose("Item Id = " + strItemId + " Node = " + strNode);
		// invoke the ItemList to fetch the UOM and Product Class
		Document eleItemList = getItemList(strItemId, env);
		if (eleItemList != null)
			if (logger.isVerboseEnabled())
				logger.verbose("Output getItemList => "
						+ XMLUtil.extractStringFromDocument(eleItemList));

		NodeList nodeLst = eleItemList.getDocumentElement()
				.getElementsByTagName(NWCGConstants.ITEM);
		String strUOM = NWCGConstants.EMPTY_STRING;// fetch from the element
		String strPC = NWCGConstants.EMPTY_STRING; // fetch from the element
		String strEndDate = "2500-01-01"; // add EndDate for reserved Qty
											// consideration
		String extnROSSResourceItem = ""; // fetch from Extn element
		String extnPublishToROSS = ""; // fetch from Extn element
		String strNSN = NWCGConstants.EMPTY_STRING;
		String strShortDesc = NWCGConstants.EMPTY_STRING;
		String isSerialTracked = ""; // fetch from InventoryParameters element
		if (nodeLst.getLength() <= 0) {
			// item does not exists in system
			root_item.setAttribute(NWCGConstants.AVAILABLE_QUANTITY,
					NWCGConstants.EMPTY_STRING);
			root_item.setAttribute(NWCGConstants.SHORT_DESCRIPTION,
					NWCGConstants.EMPTY_STRING);
			return inDoc;
		}
		for (int i = 0; i < nodeLst.getLength(); i++) {
			Element item = (Element) nodeLst.item(i);
			strUOM = item.getAttribute(NWCGConstants.UNIT_OF_MEASURE);
			strNSN = item.getAttribute(NWCGConstants.GLOBAL_ITEM_ID);
			NodeList nlPI = item
					.getElementsByTagName(NWCGConstants.PRIMARY_INFORMATION);
			if (nlPI != null && nlPI.getLength() >= 0) {
				Element elemPI = (Element) nlPI.item(0);
				strPC = elemPI
						.getAttribute(NWCGConstants.DEFAULT_PRODUCT_CLASS);
				strShortDesc = elemPI
						.getAttribute(NWCGConstants.SHORT_DESCRIPTION);
				// added for CR 573
				strProductLine = elemPI
						.getAttribute(NWCGConstants.PRODUCT_LINE);
			}

			NodeList nlItemExtn = item.getElementsByTagName("Extn");
			if (nlItemExtn != null && nlItemExtn.getLength() >= 0) {
				Element elemItemExtn = (Element) nlItemExtn.item(0);
				extnROSSResourceItem = elemItemExtn
						.getAttribute(NWCGConstants.EXTN_ROSS_RESOURCE_ITEM);
				extnPublishToROSS = elemItemExtn
						.getAttribute(NWCGConstants.EXTN_PUBLISH_TO_ROSS);
			}

			// TODO: Kunle add to strings to NWCGConstants
			NodeList nlItemIP = item
					.getElementsByTagName("InventoryParameters");
			if (nlItemIP != null && nlItemIP.getLength() >= 0) {
				Element elemItemIP = (Element) nlItemIP.item(0);
				isSerialTracked = elemItemIP.getAttribute("IsSerialTracked");
			}
		}
		// get the PC

		if (logger.isVerboseEnabled())
			logger.verbose("UOM = " + strUOM + " PC = " + strPC + " EndDate = "
					+ strEndDate);

		Document availRFIQtyDoc = CommonUtilities.getAvailableRFIQty(env,
				strItemId, strUOM, strPC, strNode, strShortDesc);
		// outputShipmentDetailsDoc =
		// CommonUtilities.invokeAPI(env,"getShipmentDetails","getShipmentDetails",inputDoc);
		// cr 443 ks

		if (availRFIQtyDoc != null) {
			Element eleSupplyDetailsElm = availRFIQtyDoc.getDocumentElement();
			eleSupplyDetailsElm
					.setAttribute(NWCGConstants.EXTN_ROSS_RESOURCE_ITEM,
							extnROSSResourceItem);
			eleSupplyDetailsElm.setAttribute(
					NWCGConstants.EXTN_PUBLISH_TO_ROSS, extnPublishToROSS);
			eleSupplyDetailsElm
					.setAttribute("IsSerialTracked", isSerialTracked);
			if (logger.isVerboseEnabled())
				logger.verbose("availRFIQtyDoc: "
						+ XMLUtil.extractStringFromDocument(availRFIQtyDoc));
		}
		// added for CR 575
		availRFIQtyDoc.getDocumentElement().setAttribute(
				NWCGConstants.PRODUCT_LINE, strProductLine);
		if (logger.isVerboseEnabled())
			logger.verbose("availRFIQtyDoc: "
					+ XMLUtil.extractStringFromDocument(availRFIQtyDoc));
		return availRFIQtyDoc;
	}

	public Document populateItemDetails(YFSEnvironment env, Document inDoc)
			throws Exception {

		String strProductLine = NWCGConstants.EMPTY_STRING;

		if (logger.isVerboseEnabled())
			logger.verbose("Entering NWCGPopulateItemDetails()->"
					+ XMLUtil.extractStringFromDocument(inDoc));

		String strItemId = NWCGConstants.EMPTY_STRING;// fetch the itemid from
														// the input xml
		String strNode = NWCGConstants.EMPTY_STRING;// fetch the node from input
													// xml
		Element root_item = inDoc.getDocumentElement();
		if (root_item != null) {
			strItemId = root_item.getAttribute(NWCGConstants.ITEM_ID);
			strNode = root_item.getAttribute(NWCGConstants.NODE);
		}
		if (logger.isVerboseEnabled())
			logger.verbose("Item Id = " + strItemId + " Node = " + strNode);
		// invoke the ItemList to fetch the UOM and Product Class
		Document eleItemList = getItemList(strItemId, env);
		if (eleItemList != null)
			if (logger.isVerboseEnabled())
				logger.verbose("Output getItemList => "
						+ XMLUtil.extractStringFromDocument(eleItemList));

		NodeList nodeLst = eleItemList.getDocumentElement()
				.getElementsByTagName(NWCGConstants.ITEM);
		String strUOM = NWCGConstants.EMPTY_STRING;// fetch from the element
		String strPC = NWCGConstants.EMPTY_STRING; // fetch from the element
		String strEndDate = "2500-01-01"; // add EndDate for reserved Qty
											// consideration
		String extnROSSResourceItem = ""; // fetch from Extn element
		String extnPublishToROSS = ""; // fetch from Extn element
		String strNSN = NWCGConstants.EMPTY_STRING;
		String strShortDesc = NWCGConstants.EMPTY_STRING;
		if (nodeLst.getLength() <= 0) {
			// item does not exists in system
			root_item.setAttribute(NWCGConstants.AVAILABLE_QUANTITY,
					NWCGConstants.EMPTY_STRING);
			root_item.setAttribute(NWCGConstants.SHORT_DESCRIPTION,
					NWCGConstants.EMPTY_STRING);
			return inDoc;
		}
		for (int i = 0; i < nodeLst.getLength(); i++) {
			Element item = (Element) nodeLst.item(i);
			strUOM = item.getAttribute(NWCGConstants.UNIT_OF_MEASURE);
			strNSN = item.getAttribute(NWCGConstants.GLOBAL_ITEM_ID);
			NodeList nlPI = item
					.getElementsByTagName(NWCGConstants.PRIMARY_INFORMATION);
			if (nlPI != null && nlPI.getLength() >= 0) {
				Element elemPI = (Element) nlPI.item(0);
				strPC = elemPI
						.getAttribute(NWCGConstants.DEFAULT_PRODUCT_CLASS);
				strShortDesc = elemPI
						.getAttribute(NWCGConstants.SHORT_DESCRIPTION);
				// added for CR 573
				strProductLine = elemPI
						.getAttribute(NWCGConstants.PRODUCT_LINE);
			}

			NodeList nlItemExtn = item.getElementsByTagName("Extn");
			if (nlItemExtn != null && nlItemExtn.getLength() >= 0) {
				Element elemItemExtn = (Element) nlItemExtn.item(0);
				extnROSSResourceItem = elemItemExtn
						.getAttribute(NWCGConstants.EXTN_ROSS_RESOURCE_ITEM);
				extnPublishToROSS = elemItemExtn
						.getAttribute(NWCGConstants.EXTN_PUBLISH_TO_ROSS);
			}

		}
		// get the PC

		if (logger.isVerboseEnabled())
			logger.verbose("UOM = " + strUOM + " PC = " + strPC + " EndDate = "
					+ strEndDate);

		// -- JSK reserved quantity consideration: added strEndDate
		Document eleSupplyDetails = CommonUtilities.getAvailableRFIQty(env,
				strItemId, strUOM, strPC, strNode, strShortDesc);
		// outputShipmentDetailsDoc =
		// CommonUtilities.invokeAPI(env,"getShipmentDetails","getShipmentDetails",inputDoc);
		// cr 443 ks
		// Document inputDoc = XMLUtil.newDocument();
		// Element
		// element_NWCGShipmentDetails=inputDoc.createElement(NWCGConstants.SHIPMENT_ELEMENT);
		if (eleSupplyDetails != null) {
			Element eleSupplyDetailsElm = eleSupplyDetails.getDocumentElement();
			eleSupplyDetailsElm
					.setAttribute(NWCGConstants.EXTN_ROSS_RESOURCE_ITEM,
							extnROSSResourceItem);
			eleSupplyDetailsElm.setAttribute(
					NWCGConstants.EXTN_PUBLISH_TO_ROSS, extnPublishToROSS);
			eleSupplyDetailsElm.setAttribute(NWCGConstants.SHIP_NODE, strNode);
			eleSupplyDetailsElm.setAttribute("ShipDate",
					CommonUtilities.getXMLCurrentTime());
			if (logger.isVerboseEnabled())
				logger.verbose("returning_1 "
						+ XMLUtil.extractStringFromDocument(eleSupplyDetails));
		}
		// added for CR 575
		eleSupplyDetails.getDocumentElement().setAttribute(
				NWCGConstants.PRODUCT_LINE, strProductLine);
		if (logger.isVerboseEnabled())
			logger.verbose("eleSupplyDetails: "
					+ XMLUtil.extractStringFromDocument(eleSupplyDetails));
		return eleSupplyDetails;
	}

	private Document getItemList(String strItemId, YFSEnvironment env)
			throws Exception {

		Document doc = XMLUtil.createDocument(NWCGConstants.ITEM);

		Element root_ele = doc.getDocumentElement();
		root_ele.setAttribute(NWCGConstants.ITEM_ID, strItemId);
		root_ele.setAttribute(NWCGConstants.ORGANIZATION_CODE,
				NWCGConstants.ENTERPRISE_CODE);
		return CommonUtilities.invokeAPI(env,
				"NWCGPopulateItemDetails_getItemDetails",
				NWCGConstants.API_GET_ITEM_LIST, doc);
	}
}