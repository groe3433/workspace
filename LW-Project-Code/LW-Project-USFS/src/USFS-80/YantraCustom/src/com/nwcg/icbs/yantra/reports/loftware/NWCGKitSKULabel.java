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

import java.util.Properties;
import java.text.DecimalFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGKitSKULabel implements YIFCustomApi {

	private Properties _properties;

	private static Logger log = Logger.getLogger(NWCGKitSKULabel.class
			.getName());

	public void setProperties(Properties arg0) throws Exception {
		_properties = arg0;
	}

	/**
	 * Sample input XML : <WorkOrder CaseId="" DocumentType="7001"
	 * EnterpriseCode="NWCG" InvUpdateActivityCode="VAS" ItemID="RTEST"
	 * NextAlertTs="1900-01-01T00:00:00-05:00" NodeKey="CORMK"
	 * OpenWorkOrderFlag="N" OrderLineKey="" PalletId=""
	 * PipeLineKey="20060824120337481538" PreCallStatus=""
	 * PreCallStatusDescription="" Priority="3" ProductClass="Supply" Purpose=""
	 * QuantityAllocated="1.00" QuantityCompleted="1.00" QuantityReleased="1.00"
	 * QuantityRemoved="0.00" QuantityRequested="1.00" ReasonCode=""
	 * ReasonCodeDescription="" ReasonText="" Segment="" SegmentType=""
	 * SerialNo="" ServiceItemDescription="KITTING ITEM"
	 * ServiceItemGroupCode="KIT" ServiceItemID="KITTING" ServiceUom=""
	 * ShipmentKey="" ShipmentNo=""
	 * StartNoEarlierThan="2006-11-15T13:42:33-05:00" Status="1400"
	 * StatusDate="2006-11-15T12:53:19-05:00" StatusDescription="Work Order
	 * Completed" Uom="KT" WorkOrderKey="20061115124255574635"
	 * WorkOrderNo="211"> <WorkOrderActivityDtl ActivityCode="VAS"
	 * ActivityLocationId="V1-000001" CaseId="" EndDate="11/15/2006"
	 * EndTime="11:53:00" EndTimeStamp="2006-11-15T11:53:00" PalletId=""
	 * SerialNo="305" StartDate="11/15/2006" StartTime="11:53:00"
	 * StartTimeStamp="2006-11-15T11:53:00" WorkOrderKey="20061115124255574635">
	 * <WorkOrderComponents> <WorkOrderComponent ComponentQuantity="1.00"
	 * ItemID="Comp-1" ProductClass="Supply" Segment="" SegmentType=""
	 * SerialNo="105" ShipByDate="2500-01-01" Uom="EA"
	 * WorkOrderComponentKey="20061115124256574636"
	 * WorkOrderKey="20061115124255574635"/> <WorkOrderComponent
	 * ComponentQuantity="1.00" ItemID="Comp-2" ProductClass="Supply" Segment=""
	 * SegmentType="" SerialNo="205" ShipByDate="2500-01-01" Uom="EA"
	 * WorkOrderComponentKey="20061115124256574637"
	 * WorkOrderKey="20061115124255574635"/> </WorkOrderComponents>
	 * </WorkOrderActivityDtl> </WorkOrder>
	 * 
	 * @param env
	 * @param inputDoc
	 * @return
	 * @throws Exception
	 */
	public Document triggerKitSKULabel(YFSEnvironment env, Document inputDoc)
			throws Exception {
		if (log.isVerboseEnabled()) {
			log.verbose("NWCGKitSKULabel::triggerKitSKULabel, Input XML : "
					+ XMLUtil.getXMLString(inputDoc));
		}
		System.out.println("NWCGKitSKULabel Input XML : "
				+ XMLUtil.getXMLString(inputDoc));
		Element rootElm = inputDoc.getDocumentElement();

		if (!isPrintReportSelected(env, inputDoc))
			return inputDoc;

		String ServiceItemID = rootElm.getAttribute("ServiceItemID");
		if (ServiceItemID.equals("DEKITTING"))
			return inputDoc;
		String itemID = rootElm.getAttribute(NWCGConstants.ITEM_ID);
		// System.out.println("NWCGKitSKULabel::triggerKitSKULabel, Item ID : "
		// + itemID);
		log.verbose("NWCGKitSKULabel::triggerKitSKULabel, Item ID : " + itemID);

		String uom = rootElm.getAttribute(NWCGConstants.UOM);
		log.verbose("NWCGKitSKULabel::triggerKitSKULabel, UOM : " + uom);
		String orgCode = "NWCG";
		if (uom.equalsIgnoreCase("KT")) {
			Document itemDtls = getItemDtls(env, itemID, orgCode, uom);
			generateAndTriggerKitSKULabel(env, inputDoc, itemDtls);
		} else {
			log
					.verbose("NWCGKitSKULabel::triggerKitSKULabel, UOM is not a kit, so not generating label");
		}

		return inputDoc;
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

		System.out.println("isPrintReportStr: " + isPRSelectedStr);

		return isPRSelected;
	}

	private Document generateAndTriggerKitSKULabel(YFSEnvironment env,
			Document inputDoc, Document itemDtls) throws Exception {
		log.verbose("NWCGKitSKULabel::generateAndTriggerKitSKULabel, Entered");
		NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
		Element woRootElm = inputDoc.getDocumentElement();
		Element woActyElm = (Element) inputDoc.getDocumentElement()
				.getElementsByTagName("WorkOrderActivityDtl").item(0);
		String enterpriseCode = woRootElm
				.getAttribute(NWCGConstants.ENTERPRISE_CODE_STR);
		String orgCode = woRootElm.getAttribute(NWCGConstants.NODE_KEY);
		String documentId = "NWCG_KIT_SKU_LABEL";
		String LocationId = woActyElm.getAttribute("ActivityLocationId");
		String EquipmentId = reportsUtil.getEquipmentId(env, orgCode,
				LocationId);
		// System.out.println("Equipment Id "+ EquipmentId);
		String printerId = reportsUtil.getPrinterId(env, documentId, orgCode,
				EquipmentId);
		// System.out.println("Printer Id "+ printerId);
		Document finalDoc = reportsUtil.generatePrintHeader(documentId,
				"xml:/WorkOrder", orgCode, printerId, enterpriseCode);
		Element finalDocElm = finalDoc.getDocumentElement();
		Element printDocElm = (Element) finalDocElm.getFirstChild();
		Element inputData = finalDoc.createElement("InputData");
		printDocElm.appendChild(inputData);
		Element tmpWOElm = finalDoc.createElement("WorkOrder");
		XMLUtil.copyElement(finalDoc, woRootElm, tmpWOElm);
		inputData.appendChild(tmpWOElm);
		Element itemDtlsRootElm = itemDtls.getDocumentElement();
		NodeList primaryInfoElmList = itemDtlsRootElm
				.getElementsByTagName("PrimaryInformation");
		String SerialFlag = "";
		if (primaryInfoElmList != null && primaryInfoElmList.getLength() > 0) {
			Element primaryInfoElm = (Element) primaryInfoElmList.item(0);
			String itemWt = primaryInfoElm.getAttribute("UnitWeight");
			SerialFlag = primaryInfoElm.getAttribute("SerializedFlag");
			double itemWeight = 0.00;
			if (itemWt.length() > 0) {
				if (Double.parseDouble(itemWt) > 0)
					itemWeight = Double.parseDouble(itemWt);
			}

			DecimalFormat df = new DecimalFormat("#.#");

			itemWt = df.format(itemWeight);
			// System.out.println("ItemWeight "+ itemWt);

			Element tmpPrimaryElm = finalDoc
					.createElement("PrimaryInformation");
			XMLUtil.copyElement(finalDoc, primaryInfoElm, tmpPrimaryElm);
			tmpWOElm.appendChild(tmpPrimaryElm);

			/*
			 * String shortDesc =
			 * tmpPrimaryElm.getAttribute("ShortDescription"); String []
			 * Ctfields = shortDesc.split("-"); int dcnt = Ctfields.length;
			 * tmpPrimaryElm.setAttribute("UnitWeight",itemWt);
			 * 
			 * tmpPrimaryElm.setAttribute("ShortDescription",
			 * Ctfields[0].toUpperCase()); String ExtDesc = ""; for (int i=1;i<dcnt;i++) {
			 * ExtDesc = ExtDesc + Ctfields[i].toUpperCase(); }
			 * tmpPrimaryElm.setAttribute("ExtendedDescription", ExtDesc);
			 */

			NodeList extnList = itemDtlsRootElm.getElementsByTagName("Extn");
			Element extnElm = (Element) extnList.item(0);
			/* This needs to be changed in NILE Implementation (8.x release) */
			String QtyCompleted = "";
			if (SerialFlag.equals("Y")) {
				QtyCompleted = "1";
			} else {
				QtyCompleted = woRootElm.getAttribute("QuantityCompleted");
			}
			// System.out.println("Qty Completed "+QtyCompleted);
			double qtycomp = 1;
			if (QtyCompleted.length() > 0) {
				if (Double.parseDouble(QtyCompleted) > 1)
					qtycomp = Double.parseDouble(QtyCompleted);
			}
			// System.out.println("qtycomp "+qtycomp);
			String kitLabelQty = "1";
			if (extnElm.hasAttribute("ExtnKitLabelQty")) {
				kitLabelQty = extnElm.getAttribute("ExtnKitLabelQty");
			}
			// System.out.println("Kit Label Qty "+ kitLabelQty);
			tmpPrimaryElm.setAttribute("ExtnKitLabelQty", kitLabelQty);

			// added by gaurav for CR 436
			tmpPrimaryElm.setAttribute("ExtnReportDesc", extnElm
					.getAttribute("ExtnReportDesc"));

			NodeList invTagAttrList = itemDtlsRootElm
					.getElementsByTagName("InventoryTagAttributes");
			if (invTagAttrList != null && invTagAttrList.getLength() > 0) {
				Element invTagAttrElm = (Element) invTagAttrList.item(0);
				String dateLastTested = invTagAttrElm
						.getAttribute("RevisionNo");
				tmpPrimaryElm.setAttribute("DateLastTested", dateLastTested);
			}
			int noOfPages = 1;
			if (kitLabelQty.length() > 0) {
				if (Integer.parseInt(kitLabelQty) > 1)
					noOfPages = Integer.parseInt(kitLabelQty);
			}
			// int noOfPages = new Integer(kitLabelQty).intValue();
			// System.out.println("No Of Pages "+ noOfPages);
			log
					.verbose("NWCGKitSKULabel::generateAndTriggerKitSKULabel, Kit Label Qty : "
							+ noOfPages);
			for (int i = 1; i <= qtycomp; i++) {
				for (int j = 1; j <= noOfPages; j++) {
					String pageNo = new Integer(j).toString();
					log
							.verbose("NWCGKitSKULabel::generateAndTriggerKitSKULabel, Page No : "
									+ pageNo);
					tmpPrimaryElm.setAttribute("PageNo", pageNo);
					// Call the printdocumentset API everytime here
					if (log.isVerboseEnabled()) {
						log
								.verbose("NWCGKitSKULabel::generateAndTriggerKitSKULabel, XML output to Loftware : "
										+ XMLUtil.getXMLString(finalDoc));
					}

					log
							.verbose("NWCGKitSKULabel::generateAndTriggerKitSKULabel, Calling printDocumentSet API");
					CommonUtilities.invokeAPI(env,
							NWCGConstants.API_PRINT_DOCUMENT_SET, finalDoc);
				}
			}
		}
		// System.out.println("KIT SKU Label XML Output Doc "+
		// XMLUtil.getXMLString(finalDoc));
		return finalDoc;
	}

	private Document getItemDtls(YFSEnvironment env, String itemId,
			String orgCode, String uom) throws Exception {
		log.verbose("NWCGKitSKULabel::getItemDtls, Entered");
		Document itemDtlsInputDoc = XMLUtil.getDocument();
		Element itemElm = itemDtlsInputDoc.createElement("Item");
		itemElm.setAttribute(NWCGConstants.ITEM_ID, itemId);
		itemElm.setAttribute(NWCGConstants.ORGANIZATION_CODE, orgCode);
		itemElm.setAttribute(NWCGConstants.UNIT_OF_MEASURE, uom);
		itemDtlsInputDoc.appendChild(itemElm);

		if (log.isVerboseEnabled()) {
			log
					.verbose("NWCGKitSKULabel::getItemDtls, Input for getItemDetails : "
							+ XMLUtil.getXMLString(itemDtlsInputDoc));
		}

		/*
		 * \ Out XML Format <?xml version="1.0" encoding="UTF-8" ?> <Item
		 * ItemGroupCode="" ItemID="" ItemKey="" OrganizationCode="Required"
		 * UnitOfMeasure=""> <PrimaryInformation Description=""
		 * ExtendedDescription="" ShortDescription="" UnitCost="" UnitHeight=""
		 * UnitHeightUOM="" UnitLength="" UnitLengthUOM="" UnitVolume=""
		 * UnitVolumeUOM="" UnitWeight="" UnitWeightUOM="" UnitWidth=""
		 * UnitWidthUOM="" /> <Components> <Component ComponentDescription=""
		 * ComponentItemID="" ComponentItemKey="" ComponentOrganizationCode=""
		 * ComponentUnitOfMeasure="" ItemKey="" KitQuantity="" /> </Components>
		 * </Item>
		 */
		Document itemDtls = XMLUtil.getDocument();
		log
				.verbose("NWCGKitSKULabel::getItemDtls, Making a call to get the item details");
		itemDtls = CommonUtilities.invokeAPI(env, "NWCGReports_getItemDetails",
				NWCGConstants.API_GET_ITEM_DETAILS, itemDtlsInputDoc);
		// itemDtls =
		// CommonUtilities.invokeAPI(env,NWCGConstants.API_GET_ITEM_DETAILS,
		// itemDtlsInputDoc);
		if (log.isVerboseEnabled()) {
			log
					.verbose("NWCGKitSKULabel::getItemDtls, Output of organization dtls : "
							+ XMLUtil.getXMLString(itemDtls));
		}

		log.verbose("NWCGKitSKULabel::getItemDtls, Returning");
		return itemDtls;
	}
}