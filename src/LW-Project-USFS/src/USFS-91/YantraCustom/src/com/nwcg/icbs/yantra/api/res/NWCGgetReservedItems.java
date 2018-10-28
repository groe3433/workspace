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

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.yantra.yfc.log.YFCLogCategory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSConnectionHolder;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This API get the reservation ID from the search page. The input xml to this
 * page is :- <Item ItemID="res1" ItemIDQryType="" MaximumRecords="30"
 * reservationId="res1"/> Then hit the table YFS_reservation to get the list of
 * items assigned the same reservaiton id. Return the list xml which will be
 * used to render the custom list page.
 * 
 * <?xml version="1.0" encoding="UTF-8"?> <Item IgnoreOrdering="Y"
 * ItemID="000169" ItemIDQryType="LIKE" MaximumRecords="30" Node="CORMK"
 * ReservationID="JSK" ReservationIDQryType="FLIKE"/>
 * 
 * Important files for the module :-
 * 
 * @author spillai
 */
public class NWCGgetReservedItems implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGgetReservedItems.class);
	
	private Properties _properties;

	List listResId = new ArrayList();
	List listInvItemKey = new ArrayList();
	List listResQty = new ArrayList();
	List listOrg = new ArrayList();
	List listShipNode = new ArrayList();
	List listShipDate = new ArrayList();
	String itemName = null;
	String orgCode = null;
	String reservedQty = null;
	String shipNode = null;
	String shipDate = null;
	String orderKey = "";
	String ResId = null;
	String ItemKey[] = new String[2];

	/**
	 * This method provided the list of items This API is used to create Order
	 * and send back the orderHeaderKey to the JSP(Redirect page)
	 */
	public Document getItemList(YFSEnvironment env, Document inXML)
			throws Exception {

		String itemId = "";
		String reservationId = "";
		String RsvIDQryType = "";
		String ItemIDQryType = "";

		if (logger.isVerboseEnabled()) {
			logger.verbose("Start of NWCGgetReservedItems:getItemList API: Input XML Doc:"
					+ XMLUtil.getXMLString(inXML));
		}
		if (env == null) {
			logger.error("YFSEnvironment is null");
			throw new NWCGException("NWCG_RESERVATION_ENV_IS_NULL");
		}
		if (inXML == null) {
			logger.error("Input Document is null");
			throw new NWCGException("NWCG_RESERVATION_INPUT_DOC_NULL");
		}

		Document getItemListOutput_Doc = null;
		Element rootNode = inXML.getDocumentElement();

		// Retrieve the Short Description if provided
		try {
			Element Item = (Element) XPathUtil.getNode(rootNode, "/Item");
			String nodeKey = Item.getAttribute("Node");
			itemId = Item.getAttribute("ItemID");
			ItemIDQryType = rootNode.getAttribute("ItemIDQryType");
			reservationId = Item.getAttribute("ReservationID");
			RsvIDQryType = rootNode.getAttribute("ReservationIDQryType");

			// get the list of items from YFS_Inventory_Reservation by direct
			// query.
			// -----------------
			YFSConnectionHolder connHolder = (YFSConnectionHolder) env;
			Connection c = null;
			java.sql.Statement stmt = null;
			ResultSet rs = null;

			// GS - add an if condition to perform the query only
			// if(reservationId!=null && !(reservationId.equals("")))
			// if not added it will throw error
			try {
				c = connHolder.getDBConnection();
				stmt = c.createStatement();

				if (!nodeKey.equals("")) {
					if (!(reservationId.equals("")) && !(itemId.equals(""))) { // both
						// are
						// filled
						// in
						if (RsvIDQryType.equals("EQ")
								|| RsvIDQryType.equals("")) {
							if (ItemIDQryType.equals("EQ")
									|| ItemIDQryType.equals("")) {
								rs = stmt
										.executeQuery("select a.reservation_id,a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY') from yfs_inventory_reservation a, yfs_inventory_item b where a.reservation_id = '"
												+ reservationId
												+ "' and a.shipnode_key = '"
												+ nodeKey
												+ "' and a.inventory_item_key = b.inventory_item_key and b.item_id = '"
												+ itemId + "'");
							} else if (ItemIDQryType.equals("LIKE")) {
								rs = stmt
										.executeQuery("select a.reservation_id,a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY') from yfs_inventory_reservation a, yfs_inventory_item b where a.reservation_id = '"
												+ reservationId
												+ "' and a.shipnode_key = '"
												+ nodeKey
												+ "' and a.inventory_item_key = b.inventory_item_key and b.item_id like '%"
												+ itemId + "%'");
							} else if (ItemIDQryType.equals("FLIKE")) {
								rs = stmt
										.executeQuery("select a.reservation_id,a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY') from yfs_inventory_reservation a, yfs_inventory_item b where a.reservation_id = '"
												+ reservationId
												+ "' and a.shipnode_key = '"
												+ nodeKey
												+ "' and a.inventory_item_key = b.inventory_item_key and b.item_id like '"
												+ itemId + "%'");
							}
						} else if (RsvIDQryType.equals("LIKE")) {
							if (ItemIDQryType.equals("EQ")
									|| ItemIDQryType.equals("")) {
								rs = stmt
										.executeQuery("select a.reservation_id,a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY') from yfs_inventory_reservation a, yfs_inventory_item b where a.reservation_id like '%"
												+ reservationId
												+ "%' and a.shipnode_key = '"
												+ nodeKey
												+ "' and a.inventory_item_key = b.inventory_item_key and b.item_id = '"
												+ itemId + "'");
							} else if (ItemIDQryType.equals("LIKE")) {
								rs = stmt
										.executeQuery("select a.reservation_id,a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY') from yfs_inventory_reservation a, yfs_inventory_item b where a.reservation_id like '%"
												+ reservationId
												+ "%' and a.shipnode_key = '"
												+ nodeKey
												+ "' and a.inventory_item_key = b.inventory_item_key and b.item_id like '%"
												+ itemId + "%'");
							} else if (ItemIDQryType.equals("FLIKE")) {
								rs = stmt
										.executeQuery("select a.reservation_id,a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY') from yfs_inventory_reservation a, yfs_inventory_item b where a.reservation_id like '%"
												+ reservationId
												+ "%' and a.shipnode_key = '"
												+ nodeKey
												+ "' and a.inventory_item_key = b.inventory_item_key and b.item_id like '"
												+ itemId + "%'");
							}
						} else if (RsvIDQryType.equals("FLIKE")) {
							if (ItemIDQryType.equals("EQ")
									|| ItemIDQryType.equals("")) {
								rs = stmt
										.executeQuery("select a.reservation_id,a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY') from yfs_inventory_reservation a, yfs_inventory_item b where a.reservation_id like '"
												+ reservationId
												+ "%' and a.shipnode_key = '"
												+ nodeKey
												+ "' and a.inventory_item_key = b.inventory_item_key and b.item_id = '"
												+ itemId + "'");
							} else if (ItemIDQryType.equals("LIKE")) {
								rs = stmt
										.executeQuery("select a.reservation_id,a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY') from yfs_inventory_reservation a, yfs_inventory_item b where a.reservation_id like '"
												+ reservationId
												+ "%' and a.shipnode_key = '"
												+ nodeKey
												+ "' and a.inventory_item_key = b.inventory_item_key and b.item_id like '%"
												+ itemId + "%'");
							} else if (ItemIDQryType.equals("FLIKE")) {
								rs = stmt
										.executeQuery("select a.reservation_id,a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY') from yfs_inventory_reservation a, yfs_inventory_item b where a.reservation_id like '"
												+ reservationId
												+ "%' and a.shipnode_key = '"
												+ nodeKey
												+ "' and a.inventory_item_key = b.inventory_item_key and b.item_id like '"
												+ itemId + "%'");
							}
						}
					} else if (!(reservationId.equals("")) && itemId.equals("")) { // RSVID
						// only
						if (RsvIDQryType.equals("EQ")
								|| RsvIDQryType.equals("")) {
							rs = stmt
									.executeQuery("select a.reservation_id,a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY') from yfs_inventory_reservation a where a.reservation_id = '"
											+ reservationId
											+ "' and a.shipnode_key = '"
											+ nodeKey + "'");
						} else if (RsvIDQryType.equals("LIKE")) {
							rs = stmt
									.executeQuery("select a.reservation_id,a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY') from yfs_inventory_reservation a where a.reservation_id like '%"
											+ reservationId
											+ "%' and a.shipnode_key = '"
											+ nodeKey + "'");
						} else if (RsvIDQryType.equals("FLIKE")) {
							rs = stmt
									.executeQuery("select a.reservation_id,a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY') from yfs_inventory_reservation a where a.reservation_id like '"
											+ reservationId
											+ "%' and a.shipnode_key = '"
											+ nodeKey + "'");
						}
					} else if (reservationId.equals("") && !(itemId.equals(""))) { // ItemID
						// only
						if (ItemIDQryType.equals("EQ")
								|| ItemIDQryType.equals("")) {
							rs = stmt
									.executeQuery("select a.reservation_id,a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY') from yfs_inventory_reservation a, yfs_inventory_item b where a.shipnode_key = '"
											+ nodeKey
											+ "' and a.inventory_item_key = b.inventory_item_key and b.item_id = '"
											+ itemId + "'");
						} else if (ItemIDQryType.equals("LIKE")) {
							rs = stmt
									.executeQuery("select a.reservation_id,a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY') from yfs_inventory_reservation a, yfs_inventory_item b where a.shipnode_key = '"
											+ nodeKey
											+ "' and a.inventory_item_key = b.inventory_item_key and b.item_id like '%"
											+ itemId + "%'");
						} else if (ItemIDQryType.equals("FLIKE")) {
							rs = stmt
									.executeQuery("select a.reservation_id,a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY') from yfs_inventory_reservation a, yfs_inventory_item b where a.shipnode_key = '"
											+ nodeKey
											+ "' and a.inventory_item_key = b.inventory_item_key and b.item_id like '"
											+ itemId + "%'");
						}
					} else { // Both empty
						rs = stmt
								.executeQuery("select a.reservation_id,a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY') from yfs_inventory_reservation a where a.shipnode_key = '"
										+ nodeKey + "'");
					}
				} else {
					if (RsvIDQryType.equals("EQ")) {
						rs = stmt
								.executeQuery("select a.reservation_id,a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY') from yfs_inventory_reservation a where a.reservation_id = '"
										+ reservationId + "'");
					} else if (RsvIDQryType.equals("LIKE")) {
						rs = stmt
								.executeQuery("select a.reservation_id,a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY') from yfs_inventory_reservation a where a.reservation_id like '%"
										+ reservationId + "%'");
					} else if (RsvIDQryType.equals("FLIKE")) {
						rs = stmt
								.executeQuery("select a.reservation_id,a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY') from yfs_inventory_reservation a where a.reservation_id like '"
										+ reservationId + "%'");
					} else {
						rs = stmt
								.executeQuery("select a.reservation_id,a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY') from yfs_inventory_reservation a where 1=1");
					}
				}

				// rs.next();
				if (rs != null) {
					while (rs.next()) {
						listResId.add(rs.getString(1));
						listInvItemKey.add(rs.getString(2));
						listResQty.add(rs.getString(3));
						listOrg.add(rs.getString(4));
						listShipNode.add(rs.getString(5));
						listShipDate.add(rs.getString(6));
					}// End While
				}// rs != null
			} catch (Exception E) {
				throw new NWCGException("NWCG_RESERVATION_DB_ERROR");
			} finally {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}// End Try
			// -----------------

			// Call the getItemList Service
			Iterator ResIdIterator = listResId.iterator();
			Iterator InvItemKey = listInvItemKey.iterator();
			Iterator resQty = listResQty.iterator();
			Iterator OrgCode = listOrg.iterator();
			Iterator shipIterator = listShipNode.iterator();
			Iterator shipDateIterator = listShipDate.iterator();

			// ************OUTPUT XML template ===================
			getItemListOutput_Doc = XMLUtil.newDocument();
			Element el_ItemList = getItemListOutput_Doc
					.createElement("ItemList");
			getItemListOutput_Doc.appendChild(el_ItemList);
			el_ItemList.setAttribute("TotalNumberOfRecords", "");

			while (InvItemKey.hasNext()) {
				itemName = (String) InvItemKey.next();
				OrgCode.hasNext();
				orgCode = (String) OrgCode.next();
				resQty.hasNext();
				reservedQty = (String) resQty.next();
				shipIterator.hasNext();
				shipNode = (String) shipIterator.next();
				shipDateIterator.hasNext();
				shipDate = (String) shipDateIterator.next();
				ResIdIterator.hasNext();
				ResId = (String) ResIdIterator.next();

				String itemNameAndKey[] = getItemName(env, itemName, orgCode);
				// append to the output xml, repeating section--
				if (itemNameAndKey != null) {
					Element el_Item = getItemListOutput_Doc
							.createElement("Item");
					el_ItemList.appendChild(el_Item);
					el_Item.setAttribute("ItemID", itemNameAndKey[0]);
					el_Item.setAttribute("ProductClass", itemNameAndKey[1]);
					// el_Item.setAttribute("UnitOfMeasure2",itemNameAndKey[2]);
					el_Item.setAttribute("ItemKey", itemNameAndKey[3]);
					el_Item.setAttribute("OrganizationCode", orgCode);
					el_Item.setAttribute("UnitOfMeasure", itemNameAndKey[2]);
					el_Item.setAttribute("QTY", reservedQty);
					el_Item.setAttribute("orderKey", orderKey);
					el_Item.setAttribute("Description", itemNameAndKey[4]);
					el_Item.setAttribute("shipNode", shipNode);
					el_Item.setAttribute("shipDate", shipDate);
					el_Item.setAttribute("ReservationID", ResId);
				}// End if
			}// End While

		} catch (Exception E) {
			throw new NWCGException("NWCG_RESERVATION_ERR_READING_RESULTS");
		}// End Try

		if (logger.isVerboseEnabled()) {
			logger.verbose("Exiting the NWCGgetReservedItems:getItemList API, return document:"
					+ XMLUtil.getXMLString(getItemListOutput_Doc));
		}
		return getItemListOutput_Doc;
	}// End getItemList method
	// -------------------------------------------------------

	// Method to be used when attributes are passed through properties
	public void setProperties(Properties prop) throws Exception {
		_properties = prop;
	}// End setProperties method
	// --------------------------------------------------------

	// This method gets the name of the item based on the itemKey by calling
	// "getInventoryItemList" API
	public String[] getItemName(YFSEnvironment env, String invIemKey,
			String OrgCode) throws Exception {
		if (logger.isVerboseEnabled()) {
			logger.verbose("Entering NWCGgetReservedItems:getItemName method");
		}

		String invItmKey = invIemKey;
		String name_Key[] = new String[5];
		Document outDoc = null;

		// Form the input xml
		Document getInventoryItemList_Input = XMLUtil.newDocument();
		Element el_InventoryItem = getInventoryItemList_Input
				.createElement("InventoryItem");
		getInventoryItemList_Input.appendChild(el_InventoryItem);
		el_InventoryItem.setAttribute("InventoryItemKey", invItmKey);
		el_InventoryItem.setAttribute("OrganizationCode", OrgCode);

		if (logger.isVerboseEnabled()) {
			logger.verbose("Input xml:-"
					+ XMLUtil.getXMLString(getInventoryItemList_Input));
		}

		// Form the output xml
		Document getInventoryItemList_Output = XMLUtil.newDocument();
		Element el_InventoryList = getInventoryItemList_Output
				.createElement("InventoryList");
		getInventoryItemList_Output.appendChild(el_InventoryList);
		Element el_InventoryItem2 = getInventoryItemList_Output
				.createElement("InventoryItem");
		el_InventoryList.appendChild(el_InventoryItem2);
		el_InventoryItem2.setAttribute("ItemID", "");
		el_InventoryItem2.setAttribute("ProductClass", "");
		el_InventoryItem2.setAttribute("UnitOfMeasure", "");
		Element el_Item = getInventoryItemList_Output.createElement("Item");
		el_InventoryItem2.appendChild(el_Item);
		el_Item.setAttribute("ItemID", "");
		el_Item.setAttribute("ItemKey", "");
		Element el_PrimaryInformation = getInventoryItemList_Output
				.createElement("PrimaryInformation");
		el_Item.appendChild(el_PrimaryInformation);
		el_PrimaryInformation.setAttribute("Description", "");
		el_PrimaryInformation.setAttribute("ShortDescription", "");

		if (logger.isVerboseEnabled()) {
			logger.verbose("Output xml:-"
					+ XMLUtil.getXMLString(getInventoryItemList_Output));
		}

		// Set output template and call the API
		env.setApiTemplate("getInventoryItemList", getInventoryItemList_Output);
		outDoc = XMLUtil.newDocument();
		outDoc = CommonUtilities.invokeAPI(env, "getInventoryItemList",
				getInventoryItemList_Input);
		env.clearApiTemplate("getInventoryItemList");

		if (outDoc != null) {
			Element rootNode = outDoc.getDocumentElement();
			Element Item = (Element) XPathUtil.getNode(rootNode,
					"/InventoryList/InventoryItem");
			name_Key[0] = Item.getAttribute("ItemID");
			name_Key[1] = Item.getAttribute("ProductClass");
			name_Key[2] = Item.getAttribute("UnitOfMeasure");
			Element ItemKey = (Element) XPathUtil.getNode(rootNode,
					"/InventoryList/InventoryItem/Item");
			name_Key[3] = ItemKey.getAttribute("ItemKey");
			Element primeInfoKey = (Element) XPathUtil.getNode(rootNode,
					"/InventoryList/InventoryItem/Item/PrimaryInformation");
			name_Key[4] = primeInfoKey.getAttribute("Description");
		}// End if

		if (logger.isVerboseEnabled()) {
			logger.verbose("Exiting NWCGgetReservedItems:getItemName method");
		}
		return name_Key;
	}// End getItemName()

	public static void writedoc(Node node, String indent) {

		switch (node.getNodeType()) {

		case Node.DOCUMENT_NODE: {
			Document doc = (Document) node;
			Node child = doc.getFirstChild();
			while (child != null) {
				writedoc(child, indent);
				child = child.getNextSibling();
			}
			break;
		}

		case Node.ELEMENT_NODE: {
			Element elt = (Element) node;
			NamedNodeMap attrs = elt.getAttributes();
			for (int i = 0; i < attrs.getLength(); i++) {
				Node a = attrs.item(i);
			}
			String newindent = indent + "    ";
			Node child = elt.getFirstChild();
			while (child != null) {
				writedoc(child, newindent);
				child = child.getNextSibling();
			}
			break;
		}

		case Node.TEXT_NODE: {
			Text textNode = (Text) node;
			String text = textNode.getData().trim();
			if ((text != null) && text.length() > 0) {
				break;
			}
		}

		default:
			System.err.println("Ignoring node: " + node.getClass().getName());
			break;
		}
	}
}// End Class
