package com.fanatics.sterling.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.StringUtil;
import com.fanatics.sterling.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;

public class FanReservationHandler implements YIFCustomApi {
	private static YFCLogCategory logger = YFCLogCategory
			.instance(YFCLogCategory.class);

	Map<String, String> shipDateMap = new HashMap<>();
	Map<String, String> finalMap = new HashMap<>();
	Map<String, String> shipNodeMap = new HashMap<>();
	List<String> bundleItemIDList = new ArrayList<String>();

	Document returnDocument = null;
	Document outDoc = null;
	NodeList nlItem = null;
	double dQtyToBeCancelled = 0;
	double dResQty = 0;
	boolean isCancelReservRequest = false;
	Element eleItmRsvtion = null;

	public Document handleReservation(YFSEnvironment env, Document inXML)
			throws Exception {

		logger.info("Inside handleReservation");
		logger.info("inputXML -> " + XMLUtil.getXMLString(inXML));

		Element root = inXML.getDocumentElement();

		String sItemID = root.getAttribute("ItemID");

		bundleItemIDList = null;
		bundleItemIDList = checkforBundleItem(env, root);

		if (null != bundleItemIDList && !bundleItemIDList.isEmpty()) {
			// It means the request came for Bundle Item..
			// replace the sItemID with Component ItemID
			sItemID = bundleItemIDList.get(0);
			root.setAttribute("ItemID", sItemID);

		}

		root.removeAttribute("ShipDate");

		String sQtyToBeCancelled = root.getAttribute("QtyToBeCancelled");

		if (!StringUtil.isNullOrEmpty(sQtyToBeCancelled)) {
			dQtyToBeCancelled = Double.parseDouble(sQtyToBeCancelled);
		}

		int iQtyToBeCancelled = (int) dQtyToBeCancelled;

		if (!StringUtil.isNullOrEmpty(sQtyToBeCancelled)
				&& iQtyToBeCancelled != 0) {

			isCancelReservRequest = true;

		}

		if (isCancelReservRequest) {

			logger.info("Calling reserveItemInventory API to unReserve");
			// call getReservation API with <getReservation
			// ItemID="RegularItem7" OrganizationCode="FANATICS_US"
			// ReservationID="RESERVE0026" UnitOfMeasure="EACH"/>

			String sOrgCode = root.getAttribute("OrganizationCode");
			String sResevID = root.getAttribute("ReservationID");
			String sUOM = root.getAttribute("UnitOfMeasure");

			Document IpDoc = SCXmlUtil.createDocument("getReservation");
			Element eleRoot = IpDoc.getDocumentElement();

			eleRoot.setAttribute("ItemID", sItemID);
			eleRoot.setAttribute("OrganizationCode", sOrgCode);
			eleRoot.setAttribute("ReservationID", sResevID);
			eleRoot.setAttribute("UnitOfMeasure", sUOM);

			logger.info("getReservation API Input -> "
					+ XMLUtil.getXMLString(IpDoc));

			try {
				outDoc = CommonUtil.invokeAPI(env, "getReservation", IpDoc);

				logger.info("getReservation API Output >>>"
						+ XMLUtil.getXMLString(outDoc));

			} catch (Exception e) {
				logger.error("Exception while invoking API: " + e.getMessage()
						+ ", Cause: " + e.getCause());
				throw e;
			}

			Element outDocEle = outDoc.getDocumentElement();
			NodeList nlItemReservation = outDocEle
					.getElementsByTagName("ItemReservation");
			eleItmRsvtion = (Element) nlItemReservation.item(0);

			if (null != nlItemReservation && null != eleItmRsvtion) {
				for (int k = 0; k <= nlItemReservation.getLength(); k++) {
					Element elemItemReservation = (Element) nlItemReservation
							.item(k);
					if (null != elemItemReservation) {
						String sShipDate = elemItemReservation
								.getAttribute("ShipDate");
						String sReservationQuantity = elemItemReservation
								.getAttribute("ReservationQuantity");
						String sShipNode = elemItemReservation
								.getAttribute("ShipNode");
						shipDateMap.put(sShipDate, sReservationQuantity);
						shipNodeMap.put(sShipDate, sShipNode);
					}
				}
			} else {
				logger.error("No Matching Reservation to cancel exist");
				YFCException ex = new YFCException("YFS10265");
				throw ex;
			}

			// Iterating over the ShipDates.. and forming a finalMap with
			// ShipDate and corresponding QtyToBeCancelled.
			for (String shipDate : shipDateMap.keySet()) {
				logger.info("shipDate = >>>" + shipDateMap.get(shipDate));
				String sResQty = shipDateMap.get(shipDate);
				if (!StringUtil.isNullOrEmpty(sResQty)) {
					dResQty = Double.parseDouble(sResQty);
				}
				int iResQty = (int) dResQty;
				if (iQtyToBeCancelled == iResQty) {
					finalMap.put(shipDate, sResQty);
					break;
				} else if (iQtyToBeCancelled > iResQty) {
					finalMap.put(shipDate, sResQty);
					iQtyToBeCancelled = iQtyToBeCancelled - iResQty;
				} else if (iQtyToBeCancelled < iResQty) {
					finalMap.put(shipDate, String.valueOf(iQtyToBeCancelled));
					break;
				}

			}

			if (finalMap.size() == 1) {
				// It means we will call reserveItemInventory API to Unreserve

				for (String finalShipDate : finalMap.keySet()) {
					root.setAttribute("ShipDate", finalShipDate);
					break;
				}

				// Calling reserveItemInventory API

				logger.info("reserveItemInventory API Input to do UnReservation -> "
						+ XMLUtil.getXMLString(inXML));

				try {
					returnDocument = CommonUtil.invokeAPI(env,
							"reserveItemInventory", inXML);

					logger.info("reserveItemInventory API Output >>>"
							+ XMLUtil.getXMLString(returnDocument));

				} catch (Exception e) {
					logger.error("Exception while invoking API: "
							+ e.getMessage() + ", Cause: " + e.getCause());
					throw e;
				}

			} else {

				Document listApiIpDoc = SCXmlUtil
						.createDocument("ReserveItemInventoryList");
				Element eleListApiIp = listApiIpDoc.getDocumentElement();
				eleListApiIp.setAttribute("ApplyFutureSafetyFactor", "Y");
				eleListApiIp.setAttribute("ApplyOnhandSafetyFactor", "Y");
				eleListApiIp.setAttribute("CheckInventory", "Y");

				for (String listShipDate : finalMap.keySet()) {
					Element eleResItmInv = root;

					eleResItmInv.setAttribute("QtyToBeCancelled",
							finalMap.get(listShipDate));
					eleResItmInv.setAttribute("ShipDate", listShipDate);
					eleResItmInv.setAttribute("ShipNode",
							shipNodeMap.get(listShipDate));
					Node importedNode = listApiIpDoc.importNode(eleResItmInv,
							true);

					eleListApiIp.appendChild(importedNode);

				}

				// Calling reserveItemInventoryList API

				logger.info("reserveItemInventoryList API Input -> "
						+ XMLUtil.getXMLString(listApiIpDoc));

				try {
					returnDocument = CommonUtil.invokeAPI(env,
							"reserveItemInventoryList", listApiIpDoc);

					logger.info("reserveItemInventoryList API Output >>>"
							+ XMLUtil.getXMLString(returnDocument));

				} catch (Exception e) {
					logger.error("Exception while invoking API: "
							+ e.getMessage() + ", Cause: " + e.getCause());
					throw e;
				}

			}

		} else {

			logger.info("reserveItemInventory API Input to create Reservation -> "
					+ XMLUtil.getXMLString(inXML));

			try {
				returnDocument = CommonUtil.invokeAPI(env,
						"reserveItemInventory", inXML);

				logger.info("reserveItemInventory API Output >>>"
						+ XMLUtil.getXMLString(returnDocument));

			} catch (Exception e) {
				logger.error("Exception while invoking API: " + e.getMessage()
						+ ", Cause: " + e.getCause());
				throw e;
			}

		}

		return returnDocument;

	}

	private List<String> checkforBundleItem(YFSEnvironment env, Element root)
			throws Exception {

		Document docGetItemListOp = null;
		String stItmID = root.getAttribute("ItemID");
		String stOrgzCode = root.getAttribute("OrganizationCode");
		String stUOM = root.getAttribute("UnitOfMeasure");
		String sComponentItemID = "";
		List<String> bundleItemIDLst = new ArrayList<String>();
		NodeList nlComponentList = null;

		Document getItemListIp = SCXmlUtil.createDocument("Item");
		Element elegetItemListIp = getItemListIp.getDocumentElement();

		elegetItemListIp.setAttribute("ItemID", stItmID);
		elegetItemListIp.setAttribute("OrganizationCode", stOrgzCode);
		elegetItemListIp.setAttribute("UnitOfMeasure", stUOM);

		logger.info("getItemList API Input -> "
				+ XMLUtil.getXMLString(getItemListIp));

		try {
			docGetItemListOp = CommonUtil.invokeAPI(env,
					"global/template/api/getItemList_Reservation.xml",
					"getItemList", getItemListIp);
			logger.info("getItemList API Output >>>"
					+ XMLUtil.getXMLString(docGetItemListOp));

		} catch (Exception e) {
			logger.error("Exception while invoking API: " + e.getMessage()
					+ ", Cause: " + e.getCause());
			throw e;
		}

		nlComponentList = docGetItemListOp.getElementsByTagName("Component");

		if (null != nlComponentList) {
			for (int k = 0; k <= nlComponentList.getLength(); k++) {
				Element eleComponent = (Element) nlComponentList.item(k);
				if (null != eleComponent) {
					sComponentItemID = eleComponent
							.getAttribute("ComponentItemID");
					bundleItemIDLst.add(sComponentItemID);
				}
			}

			return bundleItemIDLst;

		} else {

			return null;
		}

	}

	@Override
	public void setProperties(Properties props) throws Exception {

	}

}