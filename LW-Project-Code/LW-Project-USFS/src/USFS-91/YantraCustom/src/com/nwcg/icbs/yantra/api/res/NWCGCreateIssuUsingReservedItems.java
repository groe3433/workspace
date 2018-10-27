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

package com.nwcg.icbs.yantra.api.res;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class NWCGCreateIssuUsingReservedItems implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGCreateIssuUsingReservedItems.class);
	
	private Properties _properties;

	/**
	 * The logic is as follows :- The input xml to this API contains the list of
	 * itemKeys and reservationID The input xml looks like this :- <CreateOrder
	 * Count="2" Item_0="200605271829216698" Item_1="2006052917572912373"
	 * ReservationID="SureshRese1"/> Then we use the reservation ID to call the
	 * custom API NWCGgetReservedItems to get whole list of items reserved using
	 * the searched reservationID The input xml for the NWCGgetReservationId is
	 * as follows:- <Item IgnoreOrdering="Y" ItemID="SureshRese1"
	 * ItemIDQryType="" MaximumRecords="30" reservationId="SureshRese1"/> The
	 * output from the NWCGgetReservedItemsAPI is as follows:- <ItemList
	 * TotalNumberOfRecords=""> <Item ItemID="PWR-40"
	 * ItemKey="200605271829216698" OrganizationCode="DEFAULT" QTY="10"
	 * UnitOfMeasure="" orderKey="2006060610121118112"/> <Item ItemID="PWR-41"
	 * ItemKey="2006052917572912373" OrganizationCode="DEFAULT" QTY="12"
	 * UnitOfMeasure="" orderKey="2006060610121118112"/> </ItemList> Now we put
	 * the list of all the items and the corresponding item into in the
	 * FinalHashMap Then using the list of selected items we search for the key
	 * in the HashMap Using the list of items and item detail we create input
	 * xml for creating order from the return xml we get the orderheaderkey and
	 * send it out
	 */
	public Document createOrder(YFSEnvironment env, Document inXML)
			throws YFSException {

		if (logger.isVerboseEnabled()) {
			logger.debug("JDBG: Start of NWCGCreateIssuUsingReservedItems:createOrder API. Input XML ->"
					+ XMLUtil.getXMLString(inXML));
		}// End if
		if (env == null) {
			logger.error("YFSEnvironment is null");
			throw new NWCGException("NWCG_RESERVATION_ENV_IS_NULL");
		}// End if
		if (inXML == null) {
			logger.error("Input Document is null");
			throw new NWCGException("NWCG_RESERVATION_INPUT_DOC_NULL");
		}// End if

		Document rt_Order = null;
		Document CreateOrderInputDoc = null;
		Document CreateOrderOut_xml = null;
		String reservationID = null;
		List key = null;
		String Item = "Item_";
		int totalItemSelected = 0;
		Document OutDoc = null;
		List itemDetails = new ArrayList();
		HashMap FinalMap = new HashMap();
		String itemid = "";
		String reservedQty = "";
		// String reservationId = "";
		String OrgCode = "";
		String UOM = "";
		String ProductClass = "";
		String shipNode = "";
		Element SelectedItem = null;
		int selectedQty = 0;

		Element rootNode = inXML.getDocumentElement();

		try {
			Element Item1 = (Element) XPathUtil.getNode(rootNode,
					"/ItemList/Item");
			reservationID = Item1.getAttribute("reservationID");
			if (logger.isVerboseEnabled()) {
				logger.debug("JDBG: reservationID ->" + reservationID);
			}
			// totalItemSelected =
			// Integer.parseInt(Order.getAttribute("Count"));

			// --------------------------------------------------------------------
			// Prepare input xml for NWCGgetReservedItems Service
			Document rt_Item = XMLUtil.newDocument();
			Element el_Item = rt_Item.createElement("Item");
			rt_Item.appendChild(el_Item);
			el_Item.setAttribute("ItemID", reservationID);
			el_Item.setAttribute("ItemIDQryType", "");
			el_Item.setAttribute("MaximumRecords", "30");
			// el_Item.setAttribute("ItemID",reservationID);
			el_Item.setAttribute("reservationId", reservationID);

			// Invoke service to get the list of items associated with the
			// reservation id
			// OutDoc=
			// CommonUtilities.invokeService(env,"NWCGgetReservedItems",rt_Item);
			OutDoc = CommonUtilities.invokeService(env,
					NWCGConstants.NWCG_GET_RESERVED_ITEMS, rt_Item);

			// get the list of items in the output document
			Element rootElement = OutDoc.getDocumentElement();
			Element itemNode = null;
			NodeList itemList = XPathUtil.getNodeList(rootElement, "./Item");
			// GS - add check if(itemList!=null && itemList.getLength()>0)
			int itemListLength = itemList.getLength();

			for (int i = 0; i < itemListLength; i++) {
				// GS - add check if(itemList!=null && itemList.getLength()>0)
				itemNode = (Element) itemList.item(i);
				itemDetails.add(itemNode.getAttribute("ItemID"));
				itemDetails.add(itemNode.getAttribute("ItemKey"));
				itemDetails.add(itemNode.getAttribute("OrganizationCode"));
				if (!(itemNode.getAttribute("QTY").equals(""))
						&& itemNode.getAttribute("QTY") != null) {
					itemDetails.add(itemNode.getAttribute("QTY"));
				} else {
					itemDetails.add("0");
					continue;
				}// End if
				itemDetails.add(itemNode.getAttribute("UnitOfMeasure"));
				itemDetails.add(itemNode.getAttribute("ProductClass"));
				itemDetails.add(itemNode.getAttribute("shipNode"));
				// Creating Key(ItemKey) value(Hashset object-itemDetail having
				// info about each item) pair
				// Building up "FinalMap" object having info of all the items
				// having same reservationID
				FinalMap.put(itemNode.getAttribute("ItemKey"), itemDetails); // Note
				itemDetails = new ArrayList();
			}// End For Loop- Forming final HashMap and Hashset having complete
				// list of reserved items

			// ---------------------------------------------------------
			// Start creating the input xml for createorder and incorporate
			// orderlines for each item selected.
			// ---------------------------------------------------------

			// --------creteorder xml
			CreateOrderInputDoc = XMLUtil.newDocument();

			Element el_Order = CreateOrderInputDoc.createElement("Order");
			CreateOrderInputDoc.appendChild(el_Order);
			el_Order.setAttribute("AllocationRuleID", "");
			el_Order.setAttribute("EnterpriseCode", "NWCG");
			el_Order.setAttribute("BuyerOrganizationCode", "");
			el_Order.setAttribute("DocumentType",
					NWCGConstants.NWCG_RESERVATION_NORMAL_ISSUE_DOC);
			el_Order.setAttribute("DraftOrderFlag", "Y");
			el_Order.setAttribute("DriverDate", "");

			el_Order.setAttribute("ExchangeType", "");
			el_Order.setAttribute("HasDeliveryLines", "N");
			el_Order.setAttribute("HasProductLines", "N");
			el_Order.setAttribute("HasServiceLines", "N");
			el_Order.setAttribute("HoldFlag", "N");
			el_Order.setAttribute("OrderDate", "");
			el_Order.setAttribute("OrderHeaderKey", "");
			el_Order.setAttribute("OrderNo", "");
			el_Order.setAttribute("OrderPurpose", "");
			el_Order.setAttribute("OrderType", "");
			el_Order.setAttribute("PaymentStatus", "");
			el_Order.setAttribute("PendingTransferIn", "0.00");
			el_Order.setAttribute("ReturnOrderHeaderKeyForExchange", "");
			el_Order.setAttribute("SaleVoided", "N");
			el_Order.setAttribute("ScacAndServiceKey", "");
			el_Order.setAttribute("SellerOrganizationCode", "");
			el_Order.setAttribute("isHistory", "N");

			Element el_PriceInfo = CreateOrderInputDoc
					.createElement("PriceInfo");
			el_Order.appendChild(el_PriceInfo);
			el_PriceInfo.setAttribute("Currency", "");

			Element el_OrderLines = CreateOrderInputDoc
					.createElement("OrderLines");
			el_Order.appendChild(el_OrderLines);

			// ------------break
			// ----------------------End
			// ----------------
			// GS - if(inXML!=null)
			NodeList ItemList = inXML.getElementsByTagName("Item");
			// GS - if(ItemList!=null && ItemList.getLength()>0)
			int ItemListLength = ItemList.getLength();
			for (int cnt = 0; cnt < ItemListLength; cnt++) {
				String itemKey = "";
				// getting items to filter out
				// GS - if(ItemList!=null && ItemList.getLength()>0)
				SelectedItem = (Element) ItemList.item(cnt);
				itemKey = SelectedItem.getAttribute("ItemKey");

				// Added after code review
				if (!(SelectedItem.getAttribute("selectedQTY").equals(""))
						&& SelectedItem.getAttribute("selectedQTY") != null) {
					selectedQty = Integer.parseInt(SelectedItem
							.getAttribute("selectedQTY"));
				} else {
					continue;
				}// End if -- code review

				key = (List) FinalMap.get(itemKey);
				// Added after code review
				if (key == null) {
					continue;
				} else {
				}// End if -- code review

				// ------get the item details
				itemid = (String) key.get(0);
				reservedQty = (String) key.get(3);
				OrgCode = (String) key.get(2);
				UOM = (String) key.get(4);
				ProductClass = (String) key.get(5);
				shipNode = (String) key.get(6);
				// Note :- Ignore i==2 as it is itemKey

				Element el_OrderLine = CreateOrderInputDoc
						.createElement("OrderLine");
				el_OrderLines.appendChild(el_OrderLine);
				el_OrderLine.setAttribute("OrderedQty",
						new Integer(selectedQty).toString());
				el_OrderLine.setAttribute("PrimeLineNo",
						Integer.toString(cnt + 1));
				el_OrderLine.setAttribute("ReceivingNode", "");
				// Commented by GN - 10/30/07
				// el_OrderLine.setAttribute("ReservationID",reservationID);
				// el_OrderLine.setAttribute("ReservationMandatory","Y");
				// el_OrderLine.setAttribute("ReservationID","");
				// el_OrderLine.setAttribute("ReservationMandatory","");
				el_OrderLine.setAttribute("ShipNode", shipNode);
				el_OrderLine.setAttribute("SubLineNo",
						Integer.toString(cnt + 1));

				Element el_Item2 = CreateOrderInputDoc.createElement("Item");
				el_OrderLine.appendChild(el_Item2);
				el_Item2.setAttribute("ItemID", itemid);
				el_Item2.setAttribute("ItemShortDesc", "");
				el_Item2.setAttribute("ProductClass", ProductClass);
				el_Item2.setAttribute("UnitOfMeasure", UOM);
			}
		} catch (Exception E) {
			throw new NWCGException(
					"NWCG_RESERVATION_ERR_GENERATING_CREATE_ORDER_XML");
		}// End Try-Catch

		// Element rootNode = inXML.getDocumentElement();

		// -----------------------------------------------------------
		// -------------------Creating CreateOrder Document to return
		// -----------------------------------------------------------

		try {
			CreateOrderOut_xml = XMLUtil.newDocument();
			// -------- jsk commented below ---------
			// CreateOrderOut_xml =
			// CommonUtilities.invokeAPI(env,"createOrder",CreateOrderInputDoc);
			// -------- jsk commented above ---------
			// Element el_Order=rt_Order.createElement("Order");
			// rt_Order.appendChild(el_Order);
			// el_Order.setAttribute("OrderHeaderKey","2006060610121118112");
			// el_Order.setAttribute("ReadFromHistory","B");
		} catch (Exception E) {
			throw new NWCGException(
					"NWCG_RESERVATION_ERR_GENERATING_CREATE_ORDER_XML");
		}// End Try

		// -------------------------------------------------------------------
		if (logger.isVerboseEnabled()) {
			logger.debug("JDBG: End of NWCGCreateIssuUsingReservedItems:createOrder API. Output XML ->"
					+ XMLUtil.getXMLString(CreateOrderOut_xml));
		}

		return CreateOrderOut_xml;

	}// Create Order

	public void setProperties(Properties prop) throws Exception {
		_properties = prop;
	}

}// End Class
