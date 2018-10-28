package com.fanatics.sterling.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.FANConstants;
import com.fanatics.sterling.util.StringUtil;
import com.fanatics.sterling.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class FanAdjustInventoryImpl implements YIFCustomApi {
	private static YFCLogCategory logger = YFCLogCategory
			.instance(YFCLogCategory.class);

	List<String> itemIDList = new ArrayList<String>();
	List<String> bundleItemIDList = new ArrayList<String>();
	Map<String, String> bundleComponentMap = new HashMap<>();
	String sItemId = "";
	String sTotalItemList = "";
	String sComponentItemID = "";
	String sCmpntItemId = "";
	Document docGetItemListOp = null;
	Document returnDocument = null;
	NodeList nlItem = null;
	NodeList ndlsItem = null;
	Element eleComponent = null;

	public Document handleAdjustInventory(YFSEnvironment env, Document inXML)
			throws Exception {

		logger.info("Inside handleAdjustInventory");
		logger.info("inputXML -> " + XMLUtil.getXMLString(inXML));

		Element root = inXML.getDocumentElement();

		NodeList nlItem = root.getElementsByTagName("Item");

		if (null != nlItem) {
			for (int i = 0; i <= nlItem.getLength(); i++) {
				Element eleItem = (Element) nlItem.item(i);
				if (null != eleItem) {
					logger.info("Inside loop : ItemID -> "
							+ eleItem.getAttribute("ItemID"));

					itemIDList.add(eleItem.getAttribute("ItemID"));

				}
			}
		}

		// Forming Input to Complex query to fetch the list of only Bundle items
		// from the Input list.

		Document IpDoc = SCXmlUtil.createDocument("Item");
		Element eleRoot = IpDoc.getDocumentElement();

		if (itemIDList.size() > 0) {
			Element eleComplexQuery = IpDoc
					.createElement(FANConstants.A_COMPLEX_QUERY);
			eleRoot.appendChild(eleComplexQuery);
			eleComplexQuery.setAttribute(FANConstants.A_OPERATOR, "AND");

			Element eleAnd = IpDoc.createElement("And");
			eleComplexQuery.appendChild(eleAnd);

			Element elemExp = IpDoc.createElement("Exp");
			eleAnd.appendChild(elemExp);

			elemExp.setAttribute("Name", "KitCode");
			elemExp.setAttribute("QryType", "EQ");
			elemExp.setAttribute("Value", "BUNDLE");

			Element eleOr = IpDoc.createElement(FANConstants.A_OR);
			eleAnd.appendChild(eleOr);

			Element eleExp = null;

			for (int j = 0; j < itemIDList.size(); j++) {
				sItemId = itemIDList.get(j);
				if (!StringUtil.isNullOrEmpty(sItemId)) {

					eleExp = IpDoc.createElement(FANConstants.A_EXP);
					eleOr.appendChild(eleExp);
					eleExp.setAttribute(FANConstants.A_NAME, "ItemID");
					eleExp.setAttribute(FANConstants.A_VALUE, sItemId);
					eleExp.setAttribute(FANConstants.A_QRYTYPE,
							FANConstants.A_EQUAL);

				}

			}
		}

		logger.info("getItemList API Input -> " + XMLUtil.getXMLString(IpDoc));

		try {
			docGetItemListOp = CommonUtil.invokeAPI(env,
					"global/template/api/getItemList_AdjustInventory.xml",
					"getItemList", IpDoc);
			logger.info("getItemList API Output >>>"
					+ XMLUtil.getXMLString(docGetItemListOp));

		} catch (Exception e) {
			logger.error("Exception while invoking API: " + e.getMessage()
					+ ", Cause: " + e.getCause());
			throw e;
		}

		Element eleGetItemListOp = docGetItemListOp.getDocumentElement();
		sTotalItemList = eleGetItemListOp.getAttribute("TotalItemList");

		if (!StringUtil.isNullOrEmpty(sTotalItemList)) {
			if (sTotalItemList.equalsIgnoreCase("0")) {
				logger.info("Request came has no Bundle Items..");
				// It means request came has no Bundle item.
				// Call API adjustInventory with Incoming Document
				// inXML and we'r not going to touch anything.

				logger.info("adjustInventory API Input -> "
						+ XMLUtil.getXMLString(inXML));
				try {
					returnDocument = CommonUtil.invokeAPI(env,
							"adjustInventory", inXML);
				} catch (Exception e) {
					logger.error("Exception while invoking API: "
							+ e.getMessage() + ", Cause: " + e.getCause());
					throw e;
				}

			} else {
				logger.info("Request came has Bundle Items..");
				// It means request came has few or all Bundle Items.

				ndlsItem = eleGetItemListOp.getElementsByTagName("Item");

				if (null != ndlsItem) {
					for (int k = 0; k <= ndlsItem.getLength(); k++) {
						Element elemItem = (Element) ndlsItem.item(k);
						if (null != elemItem) {
							logger.info("Inside next loop : ItemID -> "
									+ elemItem.getAttribute("ItemID"));

							String sBundleItemID = elemItem
									.getAttribute("ItemID");

							bundleItemIDList.add(sBundleItemID);

							NodeList nlComponent = elemItem
									.getElementsByTagName("Component");

							if (null != nlComponent) {
								eleComponent = (Element) nlComponent.item(0);
								if (null != eleComponent) {
									sComponentItemID = eleComponent
											.getAttribute("ComponentItemID");
									// Saving Bundle Items with their component
									// itemIds in a Hash Map.
									bundleComponentMap.put(sBundleItemID,
											sComponentItemID);
								}
							}

						}
					}
				}

				// Iterating over the BundleItems.. and replacing BundleItemIds
				// with their corresponding Component ItemIds.
				for (String bundleItemIds : bundleComponentMap.keySet()) {

					logger.info("bundleItemIds = >>>"
							+ bundleComponentMap.get(bundleItemIds));

					sCmpntItemId = bundleComponentMap.get(bundleItemIds);

					List<Element> nlBunItem = XMLUtil.getElementListByXpath(
							inXML, "/Items/Item[@ItemID=\"" + bundleItemIds
									+ "\"]");
					if (null != nlBunItem) {
						for (int l = 0; l < nlBunItem.size(); l++) {
							Element eleBunItem = nlBunItem.get(l);
							if (null != eleBunItem) {
								eleBunItem.setAttribute("ItemID", sCmpntItemId);
							}
						}
					}

				}

				// At this point, our Incoming document has been modified and
				// having component Items ids instead of Bundle Item ids.
				// Now, call the API adjustInventory
				// with this updated
				// Incoming Document = inXML

				logger.info("adjustInventory API Input -> "
						+ XMLUtil.getXMLString(inXML));
				try {
					returnDocument = CommonUtil.invokeAPI(env,
							"adjustInventory", inXML);
				} catch (Exception e) {
					logger.error("Exception while invoking API: "
							+ e.getMessage() + ", Cause: " + e.getCause());
				}

			}
		}

		return returnDocument;
	}

	@Override
	public void setProperties(Properties props) throws Exception {

	}

}
