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

package com.nwcg.icbs.yantra.reports.loftware;

import java.util.Hashtable;
import java.util.Properties;
import java.text.DecimalFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGReportsKitPacking implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGReportsKitPacking.class);
	
	private Properties _properties;

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		_properties = arg0;
	}

	/**
	 * This is being called on completion of work order. Output of this is input
	 * over here.
	 * 
	 * @param env
	 * @param woOutputDoc
	 * @return
	 * @throws Exception
	 */
	public Document triggerKitPackingRpt(YFSEnvironment env,
			Document woOutputDoc) throws Exception {
		logger.verbose("NWCGReportsKitPacking::triggerKitPackingRpt, Entered");
		if (logger.isVerboseEnabled()) {
			logger
					.verbose("NWCGReportsKitPacking::triggerKitPackingRpt, Work Order Output Document : "
							+ XMLUtil.getXMLString(woOutputDoc));
		}

		Element woDocElm = woOutputDoc.getDocumentElement();

		if (!isPrintReportSelected(env, woOutputDoc))
			return woOutputDoc;

		Element woActyElm = (Element) woOutputDoc.getDocumentElement()
				.getElementsByTagName("WorkOrderActivityDtl").item(0);
		String ServiceItemID = woDocElm.getAttribute("ServiceItemID");
		if (ServiceItemID.equals("DEKITTING"))
			return woOutputDoc;

		String nodeKey = woDocElm.getAttribute(NWCGConstants.NODE_KEY);
		logger
				.verbose("NWCGReportsKitPacking::triggerKitPackingRpt, Getting the details of Node Key/Cache : "
						+ nodeKey);
		Document cacheDtlsDoc = getCacheDtls(env, nodeKey);
		// Element cacheDtlsRootNode = cacheDtlsDoc.getDocumentElement();

		String enterpriseCode = woDocElm
				.getAttribute(NWCGConstants.ENTERPRISE_CODE_STR);
		String itemId = woDocElm.getAttribute(NWCGConstants.ITEM_ID);
		String uom = woDocElm.getAttribute(NWCGConstants.UOM);
		logger
				.verbose("NWCGReportsKitPacking::triggerKitPackingRpt, Getting the details of Item : "
						+ itemId);
		Document itemDtlsDoc = getItemDtls(env, itemId, enterpriseCode, uom);

		Element itemDtlsRootElm = itemDtlsDoc.getDocumentElement();
		NodeList primaryInfoElmList = itemDtlsRootElm
				.getElementsByTagName("PrimaryInformation");
		String SerialFlag = "";
		if (primaryInfoElmList != null && primaryInfoElmList.getLength() > 0) {
			Element primaryInfoElm = (Element) primaryInfoElmList.item(0);
			SerialFlag = primaryInfoElm.getAttribute("SerializedFlag");
		}

		/* This needs to be changed in NILE Implementation (8.x release) */
		String QtyCompleted = "";
		if (SerialFlag.equals("Y")) {
			QtyCompleted = "1";
		} else {
			QtyCompleted = woDocElm.getAttribute("QuantityCompleted");
		}

		double qtycomp = 1;
		if (QtyCompleted.length() > 0) {
			if (Double.parseDouble(QtyCompleted) > 1)
				qtycomp = Double.parseDouble(QtyCompleted);
		}
		String documentId = "KIT_PACKING_DOCUMENT";
		NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
		String LocationId = woActyElm.getAttribute("ActivityLocationId");
		String EquipmentId = reportsUtil.getEquipmentId(env, nodeKey,
				LocationId);
		String printerId = reportsUtil.getPrinterId(env, documentId, nodeKey,
				EquipmentId);
		Document kitPkOP = XMLUtil.getDocument();
		kitPkOP = generateKitPackingOPDoc(woOutputDoc, cacheDtlsDoc,
				itemDtlsDoc, documentId, printerId);
		if (logger.isVerboseEnabled()) {
			logger
					.verbose("NWCGReportsKitPacking::triggerKitPackingRpt, XML output to Loftware : "
							+ XMLUtil.getXMLString(kitPkOP));
		}

		logger
				.verbose("NWCGReportsKitPacking::triggerKitPackingRpt, Calling printDocumentSet API");
		for (int i = 1; i <= qtycomp; i++) {
			CommonUtilities.invokeAPI(env,
					NWCGConstants.API_PRINT_DOCUMENT_SET, kitPkOP);
		}
		logger.verbose("NWCGReportsKitPacking::triggerKitPackingRpt, Returning");
		return kitPkOP;
	}

	private boolean isPrintReportSelected(YFSEnvironment env, Document inputDoc)
			throws Exception {
		Element rootElm = inputDoc.getDocumentElement();
		String workOrderKey = rootElm.getAttribute("WorkOrderKey");

		DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dFactory.newDocumentBuilder();

		Document woDocument = dBuilder.newDocument();
		Element woElement = woDocument.createElement("WorkOrder");
		woElement.setAttribute("WorkOrderKey", workOrderKey);
		woDocument.appendChild(woElement);

		Document wodDocument = CommonUtilities.invokeAPI(env,
				NWCGConstants.API_GET_WORK_ORDER_DTLS, woDocument);
		Element subElem = (Element) wodDocument.getElementsByTagName("Extn")
				.item(0);
		String isPRSelectedStr = subElem.getAttribute("ExtnPrintReport");
		boolean isPRSelected = (isPRSelectedStr.equals("Y")) ? true : false;

		return isPRSelected;
	}

	/**
	 * 
	 * @param env
	 * @param nodeKey
	 * @return
	 * @throws Exception
	 */
	private Document getCacheDtls(YFSEnvironment env, String nodeKey)
			throws Exception {
		logger.verbose("NWCGReportsKitPacking::getCacheDtls, Entered");
		Document orgInputDoc = XMLUtil.getDocument();
		Element orgElm = orgInputDoc.createElement("Organization");
		orgElm.setAttribute(NWCGConstants.ORGANIZATION_KEY, nodeKey);
		orgInputDoc.appendChild(orgElm);

		if (logger.isVerboseEnabled()) {
			logger
					.verbose("NWCGReportsKitPacking::getCacheDtls, Input for organization dtls : "
							+ XMLUtil.getXMLString(orgInputDoc));
		}

		/*
		 * Output Template : <?xml version="1.0" encoding="UTF-8" ?>
		 * <OrganizationList> <Organization OrganizationCode=""
		 * OrganizationKey="" OrganizationName="" ParentOrganizationCode="">
		 * <CorporatePersonInfo AddressLine1="" AddressLine2="" AddressLine3=""
		 * AddressLine4="" AddressLine5="" AddressLine6="" AlternateEmailID=""
		 * Beeper="" City="" Company="" Country="" DayFaxNo="" DayPhone=""
		 * Department="" EMailID="" ErrorTxt="" EveningFaxNo="" EveningPhone=""
		 * FirstName="" HttpUrl="" JobTitle="" LastName="" Latitude=""
		 * Longitude="" MiddleName="" MobilePhone="" OtherPhone="" PersonID=""
		 * PersonInfoKey="" PreferredShipAddress="" State="" Suffix="" Title=""
		 * UseCount="" VerificationStatus="" ZipCode="" /> </Organization>
		 * </OrganizationList>
		 */
		Document orgDtls = XMLUtil.getDocument();
		logger
				.verbose("NWCGReportsKitPacking::getCacheDtls, Making a call to get the organization details");
		orgDtls = CommonUtilities.invokeAPI(env,
				"NWCGReports_getOrganizationList",
				NWCGConstants.API_GET_ORG_LIST, orgInputDoc);
		if (logger.isVerboseEnabled()) {
			logger
					.verbose("NWCGReportsKitPacking::getCacheDtls, Output of organization dtls : "
							+ XMLUtil.getXMLString(orgDtls));
		}
		logger.verbose("NWCGReportsKitPacking::getCacheDtls, Returning");
		return orgDtls;
	}

	/**
	 * 
	 * @param env
	 * @param itemId
	 * @param orgCode
	 * @param uom
	 * @return
	 * @throws Exception
	 */
	private Document getItemDtls(YFSEnvironment env, String itemId,
			String orgCode, String uom) throws Exception {
		logger.verbose("NWCGReportsKitPacking::getItemDtls, Entered");
		Document itemDtlsInputDoc = XMLUtil.getDocument();
		Element itemElm = itemDtlsInputDoc.createElement("Item");
		itemElm.setAttribute(NWCGConstants.ITEM_ID, itemId);
		itemElm.setAttribute(NWCGConstants.ORGANIZATION_CODE, orgCode);
		itemElm.setAttribute(NWCGConstants.UNIT_OF_MEASURE, uom);
		itemDtlsInputDoc.appendChild(itemElm);

		if (logger.isVerboseEnabled()) {
			logger
					.verbose("NWCGReportsKitPacking::getItemDtls, Input for getItemDetails : "
							+ XMLUtil.getXMLString(itemDtlsInputDoc));
		}

		/*
		 * Out XML Format <?xml version="1.0" encoding="UTF-8" ?> <Item
		 * ItemGroupCode="" ItemID="" ItemKey="" OrganizationCode="Required"
		 * UnitOfMeasure=""> <PrimaryInformation Description=""
		 * ShortDescription="" UnitCost="" UnitHeight="" UnitHeightUOM=""
		 * UnitLength="" UnitLengthUOM="" UnitVolume="" UnitVolumeUOM=""
		 * UnitWeight="" UnitWeightUOM="" UnitWidth="" UnitWidthUOM="" />
		 * <Components> <Component ComponentDescription="" ComponentItemID=""
		 * ComponentItemKey="" ComponentOrganizationCode=""
		 * ComponentUnitOfMeasure="" ItemKey="" KitQuantity="" /> </Components>
		 * </Item>
		 */
		Document itemDtls = XMLUtil.getDocument();
		logger
				.verbose("NWCGReportsKitPacking::getItemDtls, Making a call to get the item details");
		itemDtls = CommonUtilities.invokeAPI(env, "NWCGReports_getItemDetails",
				NWCGConstants.API_GET_ITEM_DETAILS, itemDtlsInputDoc);
		if (logger.isVerboseEnabled()) {
			logger
					.verbose("NWCGReportsKitPacking::getItemDtls, Output of organization dtls : "
							+ XMLUtil.getXMLString(itemDtls));
		}

		logger.verbose("NWCGReportsKitPacking::getItemDtls, Returning");
		return itemDtls;
	}

	/**
	 * 
	 * @param woOutputDoc
	 * @param cacheDtlsDoc
	 * @param itemDtlsDoc
	 * @param documentId
	 * @param printerId
	 * @return
	 * @throws Exception
	 */
	private Document generateKitPackingOPDoc(Document woOutputDoc,
			Document cacheDtlsDoc, Document itemDtlsDoc, String documentId,
			String printerId) throws Exception {
		logger.verbose("NWCGReportsKitPacking::generateKitPackingOPDoc, Entered");
		Element woOPRootNodeElm = woOutputDoc.getDocumentElement();
		String enterpriseCode = woOPRootNodeElm
				.getAttribute(NWCGConstants.ENTERPRISE_CODE_STR);
		String orgCode = woOPRootNodeElm.getAttribute(NWCGConstants.NODE_KEY);
		Element cacheDtlsElmRootNode = cacheDtlsDoc.getDocumentElement();
		Element cacheOrgElm = (Element) cacheDtlsElmRootNode.getFirstChild();
		String orgName = cacheOrgElm
				.getAttribute(NWCGConstants.ORGANIZATION_NAME);
		Element corporateElm = (Element) cacheOrgElm.getFirstChild();
		String addrLine1 = corporateElm
				.getAttribute(NWCGConstants.ADDRESS_LINE_1);
		String addrLine2 = corporateElm
				.getAttribute(NWCGConstants.ADDRESS_LINE_2);
		String city = corporateElm.getAttribute(NWCGConstants.CITY);
		String state = corporateElm.getAttribute(NWCGConstants.STATE);
		String zipCode = corporateElm.getAttribute(NWCGConstants.ZIP_CODE);
		NWCGReportsUtil reportUtil = new NWCGReportsUtil();
		Document kitOPDoc = XMLUtil.getDocument();
		kitOPDoc = reportUtil.generatePrintHeader(documentId,
				"xml:/KitPacking", orgCode, printerId, enterpriseCode);
		Element printElmRootNode = kitOPDoc.getDocumentElement();
		Element printDocElm = (Element) printElmRootNode.getFirstChild();
		Element inputDataElm = kitOPDoc.createElement("InputData");
		Element kitPackingElm = kitOPDoc.createElement("KitPacking");
		String strDate = reportUtil.dateToString(new java.util.Date(),
				"MM-dd-yyyy");
		kitPackingElm.setAttribute(NWCGConstants.DATE, strDate);
		Element cacheElm = kitOPDoc.createElement("Organization");
		cacheElm.setAttribute(NWCGConstants.ORGANIZATION_CODE, orgCode);
		cacheElm.setAttribute(NWCGConstants.ORGANIZATION_NAME, orgName);
		cacheElm.setAttribute(NWCGConstants.ADDRESS_LINE_1, addrLine1);
		cacheElm.setAttribute(NWCGConstants.ADDRESS_LINE_2, addrLine2);
		cacheElm.setAttribute(NWCGConstants.CITY, city);
		cacheElm.setAttribute(NWCGConstants.STATE, state);
		cacheElm.setAttribute(NWCGConstants.ZIP_CODE, zipCode);
		kitPackingElm.appendChild(cacheElm);

		String itemId = woOPRootNodeElm.getAttribute(NWCGConstants.ITEM_ID);
		Element woActDtlElm = (Element) woOPRootNodeElm.getFirstChild();
		String serialNo = woActDtlElm.getAttribute(NWCGConstants.SERIAL_NO);
		if (serialNo == null) {
			serialNo = "";
		}
		Element itemDtlsRootNodeElm = itemDtlsDoc.getDocumentElement();
		String uom = itemDtlsRootNodeElm
				.getAttribute(NWCGConstants.UNIT_OF_MEASURE);
		Element itemPrimaryInfoElm = (Element) itemDtlsRootNodeElm
				.getElementsByTagName("PrimaryInformation").item(0);
		String shortDesc = itemPrimaryInfoElm
				.getAttribute(NWCGConstants.SHORT_DESCRIPTION);
		String InstructionText = "";
		Element itemInstructionLElm = (Element) itemDtlsRootNodeElm
				.getElementsByTagName("ItemInstructionList").item(0);
		if (itemInstructionLElm.hasChildNodes()) {
			Element itemInstructionElm = (Element) itemInstructionLElm
					.getElementsByTagName("ItemInstruction").item(0);
			InstructionText = itemInstructionElm
					.getAttribute("InstructionText");
		}

		String weight = itemPrimaryInfoElm
				.getAttribute(NWCGConstants.UNIT_WEIGHT);
		String weightUom = itemPrimaryInfoElm
				.getAttribute(NWCGConstants.UNIT_WEIGHT_UOM);
		String weightInLbs = "";
		String weightInKg = "";
		if (weightUom.equalsIgnoreCase("lbs")) {
			weightInLbs = weight;
			weightInKg = reportUtil.getWeightInKg(weightInLbs);
		} else {
			weightInKg = weight;
			weightInLbs = reportUtil.getWeightInLbs(weightInKg);
		}
		String vol = itemPrimaryInfoElm.getAttribute(NWCGConstants.UNIT_VOLUME);
		// UnitVolume
		String volUOM = itemPrimaryInfoElm
				.getAttribute(NWCGConstants.UNIT_VOLUME_UOM);
		String volInCbFt = "";
		String volInCbMtrs = "";
		if (volUOM.indexOf("Meter") != -1) {
			volInCbMtrs = vol;
			volInCbFt = reportUtil.getVolInCubicFeet(volInCbMtrs);
		}
		if (volUOM.indexOf("CIN") != -1) {
			volInCbMtrs = vol;
			volInCbFt = reportUtil.getVolInCubicFeetFromInch(volInCbMtrs);
		} else {
			volInCbFt = vol;
			volInCbMtrs = reportUtil.getVolInCubicMeters(volInCbFt);
		}

		double itemWeightlbs = 0.00;
		if (weightInLbs.length() > 0) {

			if (Double.parseDouble(weightInLbs) > 0)
				itemWeightlbs = Double.parseDouble(weightInLbs);
		}
		DecimalFormat df = new DecimalFormat("#.#");
		weightInLbs = df.format(itemWeightlbs);

		double itemWeightkg = 0.00;
		if (weightInKg.length() > 0) {
			if (Double.parseDouble(weightInKg) > 0)
				itemWeightkg = Double.parseDouble(weightInKg);
		}
		weightInKg = df.format(itemWeightkg);

		double volumeFt = 0.00;
		if (volInCbFt.length() > 0) {
			if (Double.parseDouble(volInCbFt) > 0)
				volumeFt = Double.parseDouble(volInCbFt);
		}
		volInCbFt = df.format(volumeFt);

		Element itemDtlsElm = kitOPDoc.createElement("Item");
		itemDtlsElm.setAttribute(NWCGConstants.ITEM_ID, itemId);
		itemDtlsElm.setAttribute(NWCGConstants.SHORT_DESCRIPTION, shortDesc);
		itemDtlsElm.setAttribute(NWCGConstants.UOM, uom);
		itemDtlsElm.setAttribute(NWCGConstants.SERIAL_NO, serialNo);
		itemDtlsElm.setAttribute(NWCGConstants.WEIGHT_IN_LBS, weightInLbs);
		itemDtlsElm.setAttribute(NWCGConstants.WEIGHT_LB_UNITS, "lbs");
		itemDtlsElm.setAttribute(NWCGConstants.WEIGHT_IN_KGS, weightInKg);
		itemDtlsElm.setAttribute(NWCGConstants.WEIGHT_KG_UNITS, "kg");
		// TODO.. Sunjay. Get the correct volume units from the output and
		// change the code
		// to get volume in cubic feet and in cubic meters
		itemDtlsElm.setAttribute(NWCGConstants.VOL_IN_CUB_FT, volInCbFt);
		itemDtlsElm.setAttribute(NWCGConstants.VOL_CUB_FT_UNITS, "Cubic Feet");
		itemDtlsElm.setAttribute(NWCGConstants.VOL_IN_CUB_MTS, volInCbMtrs);
		itemDtlsElm.setAttribute(NWCGConstants.VOL_CUB_MTS_UNITS,
				"Cubic Meters");
		itemDtlsElm.setAttribute("InstructionText", InstructionText);
		kitPackingElm.appendChild(itemDtlsElm);
		// Putting the child items in the hashtable. Output from WorkOrder
		// Components
		// doesn't return the descrition. So putting it in the hashtable and we
		// will
		// use this description while building the work order components tag.
		NodeList childItems = itemDtlsRootNodeElm
				.getElementsByTagName("Component");
		Hashtable childList = new Hashtable();
		for (int ci = 0; ci < childItems.getLength(); ci++) {
			Element childItem = (Element) childItems.item(ci);
			childList.put(childItem
					.getAttribute(NWCGConstants.COMPONENT_ITEM_ID), childItem
					.getAttribute(NWCGConstants.COMPONENT_DESCRIPTION));
		}
		Element woElm = kitOPDoc.createElement("WorkOrder");
		woElm.setAttribute(NWCGConstants.ITEM_ID, itemId);
		woElm.setAttribute(NWCGConstants.SERIAL_NO, serialNo);
		woElm.setAttribute(NWCGConstants.SHORT_DESCRIPTION, shortDesc);
		Element woComponentList = kitOPDoc.createElement("WorkOrderComponents");
		NodeList nodeList = woOPRootNodeElm
				.getElementsByTagName("WorkOrderComponent");
		String childItemId, childDesc, childUOM, childQty, childSerialNo;
		for (int i = 0; i < nodeList.getLength(); i++) {
			childItemId = childDesc = childUOM = childQty = childSerialNo = "";
			Element tmpComp = (Element) nodeList.item(i);
			childItemId = tmpComp.getAttribute(NWCGConstants.ITEM_ID);
			childUOM = tmpComp.getAttribute(NWCGConstants.UOM);
			childQty = tmpComp.getAttribute(NWCGConstants.COMPONENT_QUANTITY);
			childDesc = (String) childList.get(childItemId);
			childSerialNo = tmpComp.getAttribute(NWCGConstants.SERIAL_NO);
			if (childSerialNo == null) {
				childSerialNo = "";
			}

			Element woComponent = kitOPDoc.createElement("WorkOrderComponent");
			woComponent.setAttribute(NWCGConstants.ITEM_ID, childItemId);
			woComponent.setAttribute(NWCGConstants.UOM, childUOM);
			woComponent.setAttribute(NWCGConstants.QUANTITY, childQty);
			woComponent
					.setAttribute(NWCGConstants.SHORT_DESCRIPTION, childDesc);
			woComponent.setAttribute(NWCGConstants.SERIAL_NO, childSerialNo);
			woComponentList.appendChild(woComponent);
		}
		woElm.appendChild(woComponentList);
		// woElm.setAttribute()

		kitPackingElm.appendChild(woElm);

		inputDataElm.appendChild(kitPackingElm);
		printDocElm.appendChild(inputDataElm);
		logger
				.verbose("NWCGReportsKitPacking::generateKitPackingOPDoc, Returning");
		return kitOPDoc;
	}
}