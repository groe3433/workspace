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

package com.nwcg.icbs.yantra.api.returns;

import java.util.HashMap;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGgetItemAndComponentDetails implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGgetItemAndComponentDetails.class);
	
	private Properties _properties;

	String IsSerialTracked = "";
	String TagControlFlag = "";
	String ShortDescription = "";
	String TimeSensitive = "";

	public Document getDetails(YFSEnvironment env, Document inXML)
			throws YFSException {
		logger.verbose("@@@@@ Entering NWCGgetItemAndComponentDetails::getDetails");
		
		// Document returnDoc =null;
		Document getItemList_Out = null;
		Document getItemDetails_Out = null;
		int ReturnQuantity = 0;

		String sProductLine = "";

		logger.verbose("@@@@@ input document is:" + XMLUtil.getXMLString(inXML));

		try {

			// First call getitemList using the itemID and calling
			// organizationCode and get the item details like PC
			// PC and UOM
			// Then call getitemdetails to get the component and other required
			// information.

			String ItemID = inXML.getDocumentElement().getAttribute("ItemID");
			String RetQty = inXML.getDocumentElement().getAttribute("RetQty");

			// if condition added to prevent number format exception
			// if(RetQty!=null && RetQty!="")
			// Changed after code review
			if (RetQty != null && !(RetQty.equals("")))
				ReturnQuantity = Integer.parseInt(inXML.getDocumentElement()
						.getAttribute("RetQty"));

			// String CallingOrganizationCode =
			// inXML.getDocumentElement().getAttribute("CallingOrganizationCode");

			// if(ItemID==null||ItemID==""){
			// Changed after code review
			if (ItemID == null || ItemID.equals("")) {
				logger.verbose("@@@@@ Exiting NWCGgetItemAndComponentDetails::getDetails :: inXML null");
				return inXML;
			}// End IF

			getItemList_Out = callGetItemList(env, inXML);

			Element primaryInformationElem = (Element) getItemList_Out
					.getElementsByTagName("PrimaryInformation").item(0);
			sProductLine = primaryInformationElem.getAttribute("ProductLine");

			if (getItemList_Out != null)
				getItemDetails_Out = callGetItemDetails(env, getItemList_Out,
						ReturnQuantity);

		} catch (Exception E) {
			logger.error("!!!!! caught general exception :: " + E);
			throw new NWCGException("NWCG_RETURN_INPUT_VALUES_ERROR");
		}// End Try catch

		getItemDetails_Out.getDocumentElement().setAttribute("sProductLine",
				sProductLine);
		logger.verbose("@@@@@ output document is:" + XMLUtil.getXMLString(getItemDetails_Out));
		logger.verbose("@@@@@ Exiting NWCGgetItemAndComponentDetails::getDetails");
		return getItemDetails_Out;
	}

	// ------------------callGetItemList
	// This method is used to get the UOM and PC for the input itemID
	public Document callGetItemList(YFSEnvironment env, Document inXML)
			throws Exception {
		logger.verbose("@@@@@ Entering NWCGgetItemAndComponentDetails::callGetItemList");
		Document ItemListDoc_out = null;
		Document IteList_Template = null;

		// ---------------------Template
		IteList_Template = XMLUtil.newDocument();

		Element el_ItemList = IteList_Template.createElement("ItemList");
		IteList_Template.appendChild(el_ItemList);

		Element el_Item = IteList_Template.createElement("Item");
		el_ItemList.appendChild(el_Item);
		el_Item.setAttribute("ItemID", "");
		el_Item.setAttribute("ItemKey", "");
		el_Item.setAttribute("OrganizationCode", "");
		el_Item.setAttribute("UnitOfMeasure", "");

		Element el_PrimaryInformation = IteList_Template
				.createElement("PrimaryInformation");
		el_Item.appendChild(el_PrimaryInformation);
		el_PrimaryInformation.setAttribute("DefaultProductClass", "");
		el_PrimaryInformation.setAttribute("NumSecondarySerials", "");
		el_PrimaryInformation.setAttribute("ShortDescription", "");
		el_PrimaryInformation.setAttribute("ProductLine", "");

		Element el_InventoryParameters = IteList_Template
				.createElement("InventoryParameters");

		el_Item.appendChild(el_InventoryParameters);
		el_InventoryParameters.setAttribute("IsSerialTracked", "");
		el_InventoryParameters.setAttribute("TagControlFlag", "");
		el_InventoryParameters.setAttribute("TimeSensitive", "");
		// ----------------------Template End

		env.setApiTemplate("getItemList", IteList_Template);
		ItemListDoc_out = CommonUtilities.invokeAPI(env, "getItemList", inXML);
		env.clearApiTemplate("getItemList");
		logger.verbose("@@@@@ Exiting NWCGgetItemAndComponentDetails::callGetItemList");
		return ItemListDoc_out;
	}

	// This method is used to get the tag attributes and serial
	public Document callGetItemDetails(YFSEnvironment env, Document inXML,
			int RetQty) throws Exception {
		logger.verbose("@@@@@ Entering NWCGgetItemAndComponentDetails::callGetItemDetails");
		// EXPLANATION
		// This method is called after calling getItemList for the kit component
		// to obtain UOM and PC
		// Then use the output xml to call getItemDetails to get the list of
		// componenets
		// For each component call getItemList to get all the attributes except
		// tag attributes
		// For each component call getCompAttributes method to get the tag
		// attributes
		// The return the returnDocument.

		Document returnDocument = XMLUtil.newDocument();

		NodeList Item = inXML.getDocumentElement().getElementsByTagName("Item");
		NodeList PrimaryInformation = inXML.getDocumentElement()
				.getElementsByTagName("PrimaryInformation");
		NodeList InventoryParameters = inXML.getDocumentElement()
				.getElementsByTagName("InventoryParameters");

		// GS - check to see if Item1 is null and length>0.. put next 4 lines in
		// the if condition
		Element Item1 = (Element) Item.item(0);
		String ItemID = Item1.getAttribute("ItemID");
		String OrganizationCode = Item1.getAttribute("OrganizationCode");
		String UnitOfMeasure = Item1.getAttribute("UnitOfMeasure");

		// GS - check to see if PrimaryInformation1 is null and length>0..
		Element PrimaryInformation1 = (Element) PrimaryInformation.item(0);
		String ShortDescription = PrimaryInformation1
				.getAttribute("ShortDescription");
		String DefaultProductClass = PrimaryInformation1
				.getAttribute("DefaultProductClass");
		String NumSecondarySerials = PrimaryInformation1
				.getAttribute("NumSecondarySerials");

		// GS - check to see if InventoryParameters1 is null and length>0..
		Element InventoryParameters1 = (Element) InventoryParameters.item(0);
		String IsSerialTracked = InventoryParameters1
				.getAttribute("IsSerialTracked");
		String TagControlFlag = InventoryParameters1
				.getAttribute("TagControlFlag");
		String TimeSensitive = InventoryParameters1
				.getAttribute("TimeSensitive");

		// ----------Preparing the input xml for the getItemDetails API
		Document getItemDetails_IN = XMLUtil.newDocument();

		Element el_Item = getItemDetails_IN.createElement("Item");
		getItemDetails_IN.appendChild(el_Item);
		el_Item.setAttribute("ItemID", ItemID);
		el_Item.setAttribute("ItemKey", "");
		el_Item.setAttribute("OrganizationCode", OrganizationCode);
		el_Item.setAttribute("UnitOfMeasure", UnitOfMeasure);
		// el_Item.setAttribute("NumSecondarySerials",NumSecondarySerials);

		Document ItemDetails_out_temp = null;
		Document ItemDetails_out = null;

		// -------------------Prepare output template- to get the list of
		// component---
		ItemDetails_out_temp = XMLUtil.newDocument();

		Element el_Item2 = ItemDetails_out_temp.createElement("Item");
		ItemDetails_out_temp.appendChild(el_Item2);

		Element el_Components2 = ItemDetails_out_temp
				.createElement("Components");
		el_Item2.appendChild(el_Components2);

		Element el_Component2 = ItemDetails_out_temp.createElement("Component");
		el_Components2.appendChild(el_Component2);
		el_Component2.setAttribute("ComponentDescription", "");
		el_Component2.setAttribute("ComponentItemID", "");
		el_Component2.setAttribute("ComponentItemKey", "");
		el_Component2.setAttribute("ComponentOrganizationCode", "");
		el_Component2.setAttribute("ComponentUnitOfMeasure", "");
		el_Component2.setAttribute("ItemKey", "");
		el_Component2.setAttribute("KitQuantity", "");

		Element el_Component3 = ItemDetails_out_temp
				.createElement("InventoryTagAttributes");
		el_Item2.appendChild(el_Component3);
		// --------------------End Output template to get the list of component

		env.setApiTemplate("getItemDetails", ItemDetails_out_temp);
		ItemDetails_out = CommonUtilities.invokeAPI(env, "getItemDetails",
				getItemDetails_IN);
		env.clearApiTemplate("getItemDetails");

		// -----------Added after final demo
		String BatchNo = "";
		String LotAttribute1 = "";
		String LotAttribute2 = "";
		String LotAttribute3 = "";
		String LotNumber = "";
		String RevisionNo = "";

		if (ItemDetails_out.getDocumentElement().getElementsByTagName(
				"InventoryTagAttributes") != null
				&& !(ItemDetails_out.getDocumentElement().getElementsByTagName(
						"InventoryTagAttributes").equals(""))) {

			NodeList InvTagList2 = ItemDetails_out.getDocumentElement()
					.getElementsByTagName("InventoryTagAttributes");

			// GS - check to see if InvTagEl2 is null and length>0..
			Element InvTagEl2 = (Element) InvTagList2.item(0);

			// Added during testing on main config
			if (InvTagList2.getLength() != 0) {
				BatchNo = InvTagEl2.getAttribute("BatchNo");
				LotAttribute1 = InvTagEl2.getAttribute("LotAttribute1");
				LotAttribute2 = InvTagEl2.getAttribute("LotAttribute2");
				LotAttribute3 = InvTagEl2.getAttribute("LotAttribute3");
				LotNumber = InvTagEl2.getAttribute("LotNumber");
				RevisionNo = InvTagEl2.getAttribute("RevisionNo");
			}// End if
		}// End if tag node exists

		String TotalNumberOfComp = "0";
		NodeList ComponentList = null;

		if (ItemDetails_out.getDocumentElement().getElementsByTagName(
				"Component") != null) {
			ComponentList = ItemDetails_out.getDocumentElement()
					.getElementsByTagName("Component");
			TotalNumberOfComp = Integer.toString(ComponentList.getLength());
		}// End if components exist

		// START CREATING FINAL OUTPUT DOCUMENT***********************
		Element el_Item1 = returnDocument.createElement("Item");
		returnDocument.appendChild(el_Item1);
		el_Item1.setAttribute("ItemID", ItemID);
		el_Item1.setAttribute("OrganizationCode", OrganizationCode);
		el_Item1.setAttribute("SerializedFlag", IsSerialTracked);
		el_Item1.setAttribute("TagControlFlag", TagControlFlag);
		el_Item1.setAttribute("TimeSensitive", TimeSensitive);
		el_Item1.setAttribute("ProductClass", DefaultProductClass);
		el_Item1.setAttribute("UOM", UnitOfMeasure);
		el_Item1.setAttribute("ShortDescription", ShortDescription);
		el_Item1.setAttribute("TotalNumberOfComps", TotalNumberOfComp);
		el_Item1.setAttribute("NumSecondarySerials", NumSecondarySerials);
		el_Item1.setAttribute("UnitOfMeasure", UnitOfMeasure);

		// Following added after final demo
		el_Item1.setAttribute("BatchNo", BatchNo);
		el_Item1.setAttribute("LotAttribute1", LotAttribute1);
		el_Item1.setAttribute("LotAttribute2", LotAttribute2);
		el_Item1.setAttribute("LotAttribute3", LotAttribute3);
		el_Item1.setAttribute("LotNumber", LotNumber);
		el_Item1.setAttribute("RevisionNo", RevisionNo);
		// End adding after final demo

		Element el_Components = returnDocument.createElement("Components");
		el_Item1.appendChild(el_Components);

		// GS - Also check if the count>0...ComponentList.getLenght()>0
		if (ComponentList != null) {
			for (int count = 0; count < ComponentList.getLength(); count++) {

				Element ComponentEl = (Element) ComponentList.item(count);

				Element el_Component = returnDocument
						.createElement("Component");
				el_Components.appendChild(el_Component);
				el_Component.setAttribute("ItemID",
						ComponentEl.getAttribute("ComponentItemID"));

				HashMap itemAttributes = getIsSerialFlag(env,
						ComponentEl.getAttribute("ComponentItemID"),
						OrganizationCode);
				// Added after final demo
				HashMap CompTagAttributes = getTagAttributes(env,
						ComponentEl.getAttribute("ComponentItemID"),
						OrganizationCode, itemAttributes.get("UnitOfMeasure")
						.toString());

				// End Adding after final demo
				el_Component.setAttribute("SerializedFlag",
						itemAttributes.get("IsSerialTracked").toString());
				el_Component.setAttribute("KitQuantity", ComponentEl
						.getAttribute("KitQuantity").toString());
				float ComponentQtyPerKit = Float.parseFloat(ComponentEl
						.getAttribute("KitQuantity"));
				float MaxPossibleReturnForComponent = ComponentQtyPerKit
						* RetQty;

				el_Component.setAttribute("MaxReturn", new Float(
						MaxPossibleReturnForComponent).toString());

				el_Component.setAttribute("UnitOfMeasure",
						itemAttributes.get("UnitOfMeasure").toString());
				el_Component.setAttribute("DefaultProductClass", itemAttributes
						.get("DefaultProductClass").toString());
				el_Component.setAttribute("ShortDescription", itemAttributes
						.get("ShortDescription").toString());
				el_Component.setAttribute("IsSerialTracked", itemAttributes
						.get("IsSerialTracked").toString());
				el_Component.setAttribute("TagControlFlag",
						itemAttributes.get("TagControlFlag").toString());
				el_Component.setAttribute("TimeSensitive",
						itemAttributes.get("TimeSensitive").toString());
				el_Component.setAttribute("NumSecondarySerials", itemAttributes
						.get("NumSecondarySerials").toString());

				// added the following lines after final demo
				el_Component.setAttribute("BatchNo",
						CompTagAttributes.get("BatchNo").toString());
				el_Component.setAttribute("LotAttribute1", CompTagAttributes
						.get("LotAttribute1").toString());
				el_Component.setAttribute("LotAttribute2", CompTagAttributes
						.get("LotAttribute2").toString());
				el_Component.setAttribute("LotAttribute3", CompTagAttributes
						.get("LotAttribute3").toString());
				el_Component.setAttribute("LotNumber",
						CompTagAttributes.get("LotNumber").toString());
				el_Component.setAttribute("RevisionNo",
						CompTagAttributes.get("RevisionNo").toString());
				// End adding lines after final demo
			}// End For Loop
		}// End if ---ComponentList!=null
		logger.verbose("@@@@@ Exiting NWCGgetItemAndComponentDetails::callGetItemDetails");
		return returnDocument;
	}

	// -----------------------------------use this to get the tag attributes for
	// the components
	public HashMap getTagAttributes(YFSEnvironment env, String ItemID,
			String OrgCode, String UnitOfMeasure) throws Exception {
		logger.verbose("@@@@@ Entering NWCGgetItemAndComponentDetails::getTagAttributes");
		HashMap CompAttributes = new HashMap();
		Document ItemDetailsDoc_out = null;
		Document ItemDetails_out_temp = null;
		// ----------Preparing the input xml for the getItemDetails API
		Document getItemDetails_IN = XMLUtil.newDocument();

		Element el_Item = getItemDetails_IN.createElement("Item");
		getItemDetails_IN.appendChild(el_Item);
		el_Item.setAttribute("ItemID", ItemID);
		el_Item.setAttribute("ItemKey", "");
		el_Item.setAttribute("OrganizationCode", OrgCode);
		el_Item.setAttribute("UnitOfMeasure", UnitOfMeasure);

		// -------------------Prepare output template- to get the list of
		// component---
		ItemDetails_out_temp = XMLUtil.newDocument();

		Element el_Item2 = ItemDetails_out_temp.createElement("Item");
		ItemDetails_out_temp.appendChild(el_Item2);

		Element el_Components2 = ItemDetails_out_temp
				.createElement("Components");
		el_Item2.appendChild(el_Components2);

		Element el_Component2 = ItemDetails_out_temp.createElement("Component");
		el_Components2.appendChild(el_Component2);
		el_Component2.setAttribute("ComponentDescription", "");
		el_Component2.setAttribute("ComponentItemID", "");
		el_Component2.setAttribute("ComponentItemKey", "");
		el_Component2.setAttribute("ComponentOrganizationCode", "");
		el_Component2.setAttribute("ComponentUnitOfMeasure", "");
		el_Component2.setAttribute("ItemKey", "");
		el_Component2.setAttribute("KitQuantity", "");

		Element el_Component3 = ItemDetails_out_temp
				.createElement("InventoryTagAttributes");
		el_Item2.appendChild(el_Component3);
		// --------------------End Output template to get the list of component

		env.setApiTemplate("getItemDetails", ItemDetails_out_temp);
		ItemDetailsDoc_out = CommonUtilities.invokeAPI(env, "getItemDetails",
				getItemDetails_IN);
		env.clearApiTemplate("ItemDetails_out_temp");

		String BatchNo = "";
		String LotAttribute1 = "";
		String LotAttribute2 = "";
		String LotAttribute3 = "";
		String LotNumber = "";
		String RevisionNo = "";

		if (ItemDetailsDoc_out.getDocumentElement().getElementsByTagName(
				"InventoryTagAttributes") != null) {
			NodeList InvTagList = ItemDetailsDoc_out.getDocumentElement()
					.getElementsByTagName("InventoryTagAttributes");
			// GS - Also check if InvTagList!=null... ie InvTagList!=null &&
			// InvTagList.getLength()>0
			if (InvTagList.getLength() > 0) {
				Element InvTagEl = (Element) InvTagList.item(0);
				BatchNo = InvTagEl.getAttribute("BatchNo");
				LotAttribute1 = InvTagEl.getAttribute("LotAttribute1");
				LotAttribute2 = InvTagEl.getAttribute("LotAttribute2");
				LotAttribute3 = InvTagEl.getAttribute("LotAttribute3");
				LotNumber = InvTagEl.getAttribute("LotNumber");
				RevisionNo = InvTagEl.getAttribute("RevisionNo");
			}// End if
		}// End -- checking if tag attribute node exists

		CompAttributes.put("BatchNo", BatchNo);
		CompAttributes.put("LotAttribute1", LotAttribute1);
		CompAttributes.put("LotAttribute2", LotAttribute2);
		CompAttributes.put("LotAttribute3", LotAttribute3);
		CompAttributes.put("LotNumber", LotNumber);
		CompAttributes.put("RevisionNo", RevisionNo);
		logger.verbose("@@@@@ Exiting NWCGgetItemAndComponentDetails::getTagAttributes");
		return CompAttributes;
	}

	public HashMap getIsSerialFlag(YFSEnvironment env, String ItemID,
			String OrgCode) throws Exception {
		logger.verbose("@@@@@ Entering NWCGgetItemAndComponentDetails::getIsSerialFlag");

		Document ItemListDoc_out = null;
		Document IteList_Template = null;
		Document ItemListDoc_in = XMLUtil.newDocument();
		HashMap itemAttributes = new HashMap();

		Element el_Item2 = ItemListDoc_in.createElement("Item");
		ItemListDoc_in.appendChild(el_Item2);
		el_Item2.setAttribute("CallingOrganizationCode", OrgCode);
		el_Item2.setAttribute("ItemID", ItemID);

		// ---------------------Template
		IteList_Template = XMLUtil.newDocument();

		Element el_ItemList = IteList_Template.createElement("ItemList");
		IteList_Template.appendChild(el_ItemList);

		Element el_Item = IteList_Template.createElement("Item");
		el_ItemList.appendChild(el_Item);
		el_Item.setAttribute("ItemID", "");
		el_Item.setAttribute("ItemKey", "");
		el_Item.setAttribute("OrganizationCode", "");
		el_Item.setAttribute("UnitOfMeasure", "");

		Element el_PrimaryInformation = IteList_Template
				.createElement("PrimaryInformation");
		el_Item.appendChild(el_PrimaryInformation);
		el_PrimaryInformation.setAttribute("DefaultProductClass", "");
		el_PrimaryInformation.setAttribute("NumSecondarySerials", "");
		el_PrimaryInformation.setAttribute("ShortDescription", "");

		Element el_InventoryParameters = IteList_Template
				.createElement("InventoryParameters");

		el_Item.appendChild(el_InventoryParameters);
		el_InventoryParameters.setAttribute("IsSerialTracked", "");
		el_InventoryParameters.setAttribute("TagControlFlag", "");
		el_InventoryParameters.setAttribute("TimeSensitive", "");
		// ----------------------Template End

		env.setApiTemplate("getItemList", IteList_Template);
		ItemListDoc_out = CommonUtilities.invokeAPI(env, "getItemList",
				ItemListDoc_in);
		env.clearApiTemplate("getItemList");

		// Get item tag
		NodeList ItemNodes = ItemListDoc_out.getDocumentElement()
				.getElementsByTagName("Item");

		// GS - ItemNodes!=null && ItemNodes.getLength()>0
		Element ItemEl = (Element) ItemNodes.item(0);
		itemAttributes.put("UnitOfMeasure",
				ItemEl.getAttribute("UnitOfMeasure"));

		// PrimaryInformation
		NodeList PrimaryInformation = ItemListDoc_out.getDocumentElement()
				.getElementsByTagName("PrimaryInformation");

		// GS - PrimaryInformation!=null && PrimaryInformation.getLength()>0
		Element PrimaryInformationEL = (Element) PrimaryInformation.item(0);

		itemAttributes.put("DefaultProductClass",
				PrimaryInformationEL.getAttribute("DefaultProductClass"));
		itemAttributes.put("ShortDescription",
				PrimaryInformationEL.getAttribute("ShortDescription"));
		itemAttributes.put("NumSecondarySerials",
				PrimaryInformationEL.getAttribute("NumSecondarySerials"));

		NodeList InventoryParameters = ItemListDoc_out.getDocumentElement()
				.getElementsByTagName("InventoryParameters");

		// GS - InventoryParameters!=null && InventoryParameters.getLength()>0
		Element InventoryParametersEl = (Element) InventoryParameters.item(0);

		itemAttributes.put("IsSerialTracked",
				InventoryParametersEl.getAttribute("IsSerialTracked"));
		itemAttributes.put("TagControlFlag",
				InventoryParametersEl.getAttribute("TagControlFlag"));
		itemAttributes.put("TimeSensitive",
				InventoryParametersEl.getAttribute("TimeSensitive"));
		// String IsSerialTracked =
		// InventoryParametersEl.getAttribute("IsSerialTracked");
		logger.verbose("@@@@@ Exiting NWCGgetItemAndComponentDetails::getIsSerialFlag");
		return itemAttributes;
	}
	
	public void setProperties(Properties prop) throws Exception {
		_properties = prop;
	}
}