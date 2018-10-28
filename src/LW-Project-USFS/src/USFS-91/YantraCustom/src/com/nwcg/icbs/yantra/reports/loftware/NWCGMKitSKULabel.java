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

import com.yantra.yfc.log.YFCLogCategory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGMKitSKULabel implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGMKitSKULabel.class);
	
	private Properties _properties;

	public void setProperties(Properties arg0) throws Exception {
		_properties = arg0;
	}

	/* <Print IgnoreOrdering="Y">
	 <Item ItemKey="20071004135700645891" ItemWeight="0.38"
	 NewExpDate="12/21/2007" NewNote="TEST_NOTE" NewStandardPack="PG"
	 NewStandardPackQty="7" NewTrackableID="" OldStandardPack="100/BD"/>
	 <PrinterPreference OrganizationCode="CORMK" PrinterId="PDFCreator" UserId="cormk1"/>
	 <LabelPreference BuyerOrganizationCode="" EnterpriseCode=""
	 NoOfCopies="1" Node="CORMK" SCAC="" Scac="" SellerOrganizationCode=""/>
	 </Print>
	 */

	public Document triggerMKitSKULabel(YFSEnvironment env, Document inputDoc)
			throws Exception {
		if (logger.isVerboseEnabled()) {
			logger.verbose("NWCGMKitSKULabel::triggerMKitSKULabel, Input XML : "
					+ XMLUtil.getXMLString(inputDoc));
		}

		NWCGReportsUtil reportsUtil = new NWCGReportsUtil();

		Element rootElem1 = (Element) inputDoc.getElementsByTagName("Item")
				.item(0);
		String itemKey = rootElem1.getAttribute("ItemKey");
		String itemWt = rootElem1.getAttribute("ItemWeight");
		itemWt = reportsUtil.TrimCommas(itemWt);
		String expDate = rootElem1.getAttribute("NewExpDate");
		String DLT = rootElem1.getAttribute("DateLastTested");
		String trackID = rootElem1.getAttribute("NewTrackableID");
		String StdPack = "";
		//String StdPack = rootElem1.getAttribute("OldStandardPack");  //Catalogger Standard Pack
		String newStdPackQty = rootElem1.getAttribute("NewStandardPackQty");
		String newStdPack = rootElem1.getAttribute("NewStandardPack"); //Container Standard Pack
		String Note = rootElem1.getAttribute("NewNote");

		double itemWeight = 0.00;
		if (itemWt.length() > 0) {
			if (Double.parseDouble(itemWt) > 0)
				itemWeight = Double.parseDouble(itemWt);
		}
		double stdpackqty = 0;
		if (newStdPackQty.length() > 0) {
			if (Double.parseDouble(newStdPackQty) > 0)
				stdpackqty = Double.parseDouble(newStdPackQty);
		}

		if (stdpackqty > 0) {
			itemWeight = itemWeight * stdpackqty;
			StdPack = newStdPackQty + "/" + newStdPack;
		}

		DecimalFormat df = new DecimalFormat("#.#");

		itemWt = df.format(itemWeight);

		Element rootElem2 = (Element) inputDoc.getElementsByTagName(
				"PrinterPreference").item(0);
		String printerId = rootElem2.getAttribute("PrinterId");
		Element rootElem3 = (Element) inputDoc.getElementsByTagName(
				"LabelPreference").item(0);
		String noOfCopies = rootElem3.getAttribute("NoOfCopies");
		String nodeKey = rootElem3.getAttribute("Node");
		String enterpriseCode = rootElem3.getAttribute("EnterpriseCode");
		String isTrackableStr = rootElem3.getAttribute("IsTrackable");

		trackID = trackID.trim();
		Document itemDtls = getItemDtls(env, itemKey);
		Element itemDtlsRootElm = itemDtls.getDocumentElement();
		itemDtlsRootElm.setAttribute("CacheID", nodeKey);
		itemDtlsRootElm.setAttribute("ItemWeight", itemWt);
		itemDtlsRootElm.setAttribute("ExpiryDate", expDate);
		itemDtlsRootElm.setAttribute("DateLastTested", DLT);
		itemDtlsRootElm.setAttribute("TrackableID", trackID);
		itemDtlsRootElm.setAttribute("StandardPack", StdPack);
		itemDtlsRootElm.setAttribute("Note", Note);

		logger.verbose("NWCGKitSKULabel::generateAndTriggerKitSKULabel, Entered");

		NodeList primaryInfoElmList = itemDtlsRootElm
				.getElementsByTagName("PrimaryInformation");

		Element primaryInfoElm = (Element) primaryInfoElmList.item(0);

		/* 
		 String shortDesc = primaryInfoElm.getAttribute("ShortDescription");
		 String [] Ctfields = shortDesc.split("-");
		 int dcnt = Ctfields.length;
		 primaryInfoElm.setAttribute("ShortDescription", Ctfields[0].toUpperCase());
		 String ExtDesc = "";
		 for (int i=1;i<dcnt;i++)
		 {
		 ExtDesc = ExtDesc + Ctfields[i].toUpperCase();
		 }
		 primaryInfoElm.setAttribute("ExtendedDescription", ExtDesc);
		 */

		NodeList extnList = itemDtlsRootElm.getElementsByTagName("Extn");
		Element extnElm = (Element) extnList.item(0);

		String kitLabelQty = "1";
		if (extnElm.hasAttribute("ExtnKitLabelQty")) {
			kitLabelQty = extnElm.getAttribute("ExtnKitLabelQty");
		}

		int noCopies = 1;
		if (Integer.parseInt(noOfCopies) > 1)
			noCopies = Integer.parseInt(noOfCopies);

		int noOfPages = 1;
		if (kitLabelQty.length() > 0) {
			if (Integer.parseInt(kitLabelQty) > 1)
				noOfPages = Integer.parseInt(kitLabelQty);
		}

		String documentId = "NWCG_MKIT_SKU_LABEL";
		Document finalDoc = reportsUtil.generatePrintHeader(documentId,
				"xml:/Item", nodeKey, printerId, enterpriseCode);
		Element finalDocElm = finalDoc.getDocumentElement();
		Element printDocElm = (Element) finalDocElm.getFirstChild();
		Element inputData = finalDoc.createElement("InputData");
		printDocElm.appendChild(inputData);
		Element tmpElm = finalDoc.createElement("Item");
		XMLUtil.copyElement(finalDoc, itemDtlsRootElm, tmpElm);
		inputData.appendChild(tmpElm);

		//Suffix NT in the variables below refers to Non Trackable (i.e. label printed without Tracking ID)
		String documentIdNT = "NWCG_MKIT_SKU_LABEL_NT";
		Document finalDocNT = reportsUtil.generatePrintHeader(documentIdNT,
				"xml:/Item", nodeKey, printerId, enterpriseCode);
		Element finalDocElmNT = finalDocNT.getDocumentElement();
		Element printDocElmNT = (Element) finalDocElmNT.getFirstChild();
		Element inputDataNT = finalDocNT.createElement("InputData");
		printDocElmNT.appendChild(inputDataNT);
		Element tmpElmNT = finalDocNT.createElement("Item");
		XMLUtil.copyElement(finalDocNT, itemDtlsRootElm, tmpElmNT);
		inputDataNT.appendChild(tmpElmNT);
		boolean isTrackable = (isTrackableStr.equals("Y")) ? true : false;

		tmpElm.setAttribute("NoOfPages", kitLabelQty);
		tmpElmNT.setAttribute("NoOfPages", kitLabelQty);

		for (int i = 1; i <= noCopies; i++) {
			for (int j = 1; j <= noOfPages; j++) {
				String pageNo = new Integer(j).toString();
				tmpElm.setAttribute("PageNo", pageNo);
				tmpElmNT.setAttribute("PageNo", pageNo);
				logger
						.verbose("NWCGMKitSKULabel::generateAndTriggerMKitSKULabel, Page No : "
								+ pageNo);
				// Call the printdocumentset API everytime here
				if (logger.isVerboseEnabled()) {
					logger
							.verbose("NWCGMKitSKULabel::generateAndTriggerMKitSKULabel, XML output to Loftware : "
									+ XMLUtil.getXMLString(finalDoc));
				}

				logger
						.verbose("NWCGKitSKULabel::generateAndTriggerMKitSKULabel, Calling printDocumentSet API");

				if (j > 1 && !isTrackable)
					CommonUtilities.invokeAPI(env,
							NWCGConstants.API_PRINT_DOCUMENT_SET, finalDocNT);
				else
					CommonUtilities.invokeAPI(env,
							NWCGConstants.API_PRINT_DOCUMENT_SET, finalDoc);
			}
		}

		return finalDoc;
	}

	private Document getItemDtls(YFSEnvironment env, String itemKey)
			throws Exception {
		logger.verbose("NWCGMKitSKULabel::getItemDtls, Entered");
		Document itemDtlsInputDoc = XMLUtil.getDocument();
		Element itemElm = itemDtlsInputDoc.createElement("Item");
		itemElm.setAttribute("ItemKey", itemKey);
		itemDtlsInputDoc.appendChild(itemElm);

		if (logger.isVerboseEnabled()) {
			logger
					.verbose("NWCGMKitSKULabel::getItemDtls, Input for getItemDetails : "
							+ XMLUtil.getXMLString(itemDtlsInputDoc));
		}

		/*\
		 * Out XML Format
		 * <?xml version="1.0" encoding="UTF-8" ?> 
		 * <Item ItemGroupCode="" ItemID="" ItemKey="" OrganizationCode="Required" UnitOfMeasure="">
		 * 	<PrimaryInformation Description="" ExtendedDescription="" ShortDescription="" UnitCost="" UnitHeight="" 
		 * 		UnitHeightUOM="" UnitLength="" UnitLengthUOM="" UnitVolume="" UnitVolumeUOM="" UnitWeight="" 
		 * 		UnitWeightUOM="" UnitWidth="" UnitWidthUOM="" /> 
		 * 	<Components>
		 * 		<Component ComponentDescription="" ComponentItemID="" ComponentItemKey="" ComponentOrganizationCode="" 
		 * 			ComponentUnitOfMeasure="" ItemKey="" KitQuantity="" /> 
		 * 	</Components>
		 * </Item>
		 */
		Document itemDtls = XMLUtil.getDocument();
		logger
				.verbose("NWCGMKitSKULabel::getItemDtls, Making a call to get the item details");
		itemDtls = CommonUtilities.invokeAPI(env, "NWCGReports_getItemDetails",
				NWCGConstants.API_GET_ITEM_DETAILS, itemDtlsInputDoc);
		//itemDtls = CommonUtilities.invokeAPI(env,NWCGConstants.API_GET_ITEM_DETAILS, itemDtlsInputDoc);
		if (logger.isVerboseEnabled()) {
			logger
					.verbose("NWCGMKitSKULabel::getItemDtls, Output of organization dtls : "
							+ XMLUtil.getXMLString(itemDtls));
		}

		logger.verbose("NWCGMKitSKULabel::getItemDtls, Returning");
		return itemDtls;
	}
}