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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGItemSKULabel implements YIFCustomApi {

	private Properties _properties;

	private static Logger log = Logger.getLogger(NWCGItemSKULabel.class
			.getName());

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

	public Document triggerItemSKULabel(YFSEnvironment env, Document inputDoc)
			throws Exception {
		if (log.isVerboseEnabled()) {
			log.verbose("NWCGItemSKULabel::triggerItemSKULabel, Input XML : "
					+ XMLUtil.getXMLString(inputDoc));
		}
		System.out.println("NWCGItemSKULabel Input XML : "
				+ XMLUtil.getXMLString(inputDoc));

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
		StdPack = rootElem1.getAttribute("OldStandardPack"); //Catalog Standard Pack
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
		int noCopies = 1;
		if (Integer.parseInt(noOfCopies) > 1)
			noCopies = Integer.parseInt(noOfCopies);

		Document itemDtls = getItemDtls(env, itemKey);
		Element itemDtlsRootElm = itemDtls.getDocumentElement();
		itemDtlsRootElm.setAttribute("CacheID", nodeKey);
		itemDtlsRootElm.setAttribute("ItemWeight", itemWt);
		itemDtlsRootElm.setAttribute("ExpiryDate", expDate);
		itemDtlsRootElm.setAttribute("DateLastTested", DLT);
		itemDtlsRootElm.setAttribute("TrackableID", trackID);
		itemDtlsRootElm.setAttribute("StandardPack", StdPack);
		itemDtlsRootElm.setAttribute("Note", Note);

		log.verbose("NWCGKitSKULabel::generateAndTriggerKitSKULabel, Entered");

		NodeList primaryInfoElmList = itemDtlsRootElm
				.getElementsByTagName("PrimaryInformation");
		//System.out.println("1");
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

		String documentId = "";
		if (trackID.length() > 0) {
			documentId = "NWCG_TRACKABLEITEM_SKU_LABEL";
			trackID = trackID.trim();
		} else {
			documentId = "NWCG_ITEM_SKU_LABEL";
		}
		Document finalDoc = reportsUtil.generatePrintHeader(documentId,
				"xml:/Item", nodeKey, printerId, enterpriseCode);
		Element finalDocElm = finalDoc.getDocumentElement();
		Element printDocElm = (Element) finalDocElm.getFirstChild();
		Element inputData = finalDoc.createElement("InputData");
		printDocElm.appendChild(inputData);
		Element tmpElm = finalDoc.createElement("Item");
		XMLUtil.copyElement(finalDoc, itemDtlsRootElm, tmpElm);
		inputData.appendChild(tmpElm);

		for (int i = 0; i < noCopies; i++) {
			String pageNo = new Integer(i + 1).toString();
			log
					.verbose("NWCGItemSKULabel::generateAndTriggerItemSKULabel, Page No : "
							+ pageNo);
			// Call the printdocumentset API everytime here
			if (log.isVerboseEnabled()) {
				log
						.verbose("NWCGItemSKULabel::generateAndTriggerItemSKULabel, XML output to Loftware : "
								+ XMLUtil.getXMLString(finalDoc));
			}

			log
					.verbose("NWCGKitSKULabel::generateAndTriggerItemSKULabel, Calling printDocumentSet API");
			CommonUtilities.invokeAPI(env,
					NWCGConstants.API_PRINT_DOCUMENT_SET, finalDoc);
		}
		System.out.println("Item SKU Label XML Output Doc "
				+ XMLUtil.getXMLString(finalDoc));
		return finalDoc;
	}

	private Document getItemDtls(YFSEnvironment env, String itemKey)
			throws Exception {
		log.verbose("NWCGItemSKULabel::getItemDtls, Entered");
		Document itemDtlsInputDoc = XMLUtil.getDocument();
		Element itemElm = itemDtlsInputDoc.createElement("Item");
		itemElm.setAttribute("ItemKey", itemKey);
		itemDtlsInputDoc.appendChild(itemElm);

		if (log.isVerboseEnabled()) {
			log
					.verbose("NWCGItemSKULabel::getItemDtls, Input for getItemDetails : "
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
		log
				.verbose("NWCGItemSKULabel::getItemDtls, Making a call to get the item details");
		itemDtls = CommonUtilities.invokeAPI(env, "NWCGReports_getItemDetails",
				NWCGConstants.API_GET_ITEM_DETAILS, itemDtlsInputDoc);
		//itemDtls = CommonUtilities.invokeAPI(env,NWCGConstants.API_GET_ITEM_DETAILS, itemDtlsInputDoc);
		if (log.isVerboseEnabled()) {
			log
					.verbose("NWCGItemSKULabel::getItemDtls, Output of organization dtls : "
							+ XMLUtil.getXMLString(itemDtls));
		}

		log.verbose("NWCGItemSKULabel::getItemDtls, Returning");
		return itemDtls;
	}
}