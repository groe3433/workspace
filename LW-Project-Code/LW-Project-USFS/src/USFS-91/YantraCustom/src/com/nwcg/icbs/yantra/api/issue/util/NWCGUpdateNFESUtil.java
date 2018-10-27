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

package com.nwcg.icbs.yantra.api.issue.util;

import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;

public class NWCGUpdateNFESUtil {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGUpdateNFESUtil.class);

	/**
	 * 
	 * @param userId
	 * @param incNo
	 * @param incYr
	 * @param orderNo
	 * @param orderHdrKey
	 * @param shippingContactName
	 * @param shippingContactPhone
	 * @param htAddrInfo
	 * @return
	 */
	public Document prepareBlankUpdateNFESResourceRequestReqDoc(String userId, String incNo, String incYr, String orderNo, String orderHdrKey, String shippingContactName, String shippingContactPhone, Hashtable<String, String> htAddrInfo) {
		logger.verbose("@@@@@ Entering NWCGUpdateNFESUtil::prepareBlankUpdateNFESResourceRequestReqDoc @@@@@");
		Document docUpdateNFES = null;
		try {
			docUpdateNFES = XMLUtil.getDocument();
			Element elmUpdtNFESResReq = docUpdateNFES.createElementNS(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE, "ro:UpdateNFESResourceRequestReq");
			elmUpdtNFESResReq.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			docUpdateNFES.appendChild(elmUpdtNFESResReq);
			elmUpdtNFESResReq.setAttribute("NWCGUSERID", userId);
			elmUpdtNFESResReq.setAttribute(NWCGAAConstants.MDTO_NAMESPACE, NWCGAAConstants.RESOURCE_ORDER_NS_WITH_SCHEMA);
			elmUpdtNFESResReq.setAttribute(NWCGAAConstants.ENTITY_KEY, orderHdrKey);
			elmUpdtNFESResReq.setAttribute(NWCGAAConstants.ENTITY_NAME, "ISSUE");
			elmUpdtNFESResReq.setAttribute(NWCGAAConstants.ENTITY_VALUE, orderNo);
			Element elmMsgOriginator = docUpdateNFES.createElement("ro:MessageOriginator");
			elmUpdtNFESResReq.appendChild(elmMsgOriginator);
			Element elmSysOfOrig = docUpdateNFES.createElement("SystemOfOrigin");
			elmMsgOriginator.appendChild(elmSysOfOrig);
			Element elmSysType = docUpdateNFES.createElement("SystemType");
			elmSysType.setTextContent("ICBS");
			elmSysOfOrig.appendChild(elmSysType);
			Element elmReqKey = docUpdateNFES.createElement("ro:RequestKey");
			elmUpdtNFESResReq.appendChild(elmReqKey);
			Element elmNaturalResReqKey = docUpdateNFES.createElement("NaturalResourceRequestKey");
			elmReqKey.appendChild(elmNaturalResReqKey);
			Element elmIncKey = docUpdateNFES.createElement("IncidentKey");
			elmNaturalResReqKey.appendChild(elmIncKey);
			Element elmNaturalIncKey = docUpdateNFES.createElement("NaturalIncidentKey");
			elmIncKey.appendChild(elmNaturalIncKey);
			String indexVal = "-";
			int hashIndex = incNo.indexOf(indexVal);
			StringBuffer aftPrefix = new StringBuffer(incNo.substring(hashIndex + indexVal.length(), incNo.length()));
			Element elmHostID = docUpdateNFES.createElement("HostID");
			elmNaturalIncKey.appendChild(elmHostID);
			Element elmUnitIDPref = docUpdateNFES.createElement("UnitIDPrefix");
			elmUnitIDPref.setTextContent(incNo.substring(0, hashIndex));
			elmHostID.appendChild(elmUnitIDPref);
			Element elmUnitIDSuf = docUpdateNFES.createElement("UnitIDSuffix");
			elmUnitIDSuf.setTextContent(aftPrefix.substring(0, aftPrefix.indexOf(indexVal)));
			elmHostID.appendChild(elmUnitIDSuf);
			// Remove the leading zeroes.
			String seqNo = aftPrefix.substring(aftPrefix.indexOf(indexVal) + indexVal.length(), aftPrefix.length());
			int intSeqNo = (new Integer(seqNo)).intValue();
			Element elmSeqNo = docUpdateNFES.createElement("SequenceNumber");
			elmSeqNo.setTextContent((new Integer(intSeqNo)).toString());
			elmNaturalIncKey.appendChild(elmSeqNo);
			Element elmYrCreated = docUpdateNFES.createElement("YearCreated");
			elmYrCreated.setTextContent(incYr);
			elmNaturalIncKey.appendChild(elmYrCreated);
			// Populate Request Code
			Element elmReqCode = docUpdateNFES.createElement("RequestCode");
			elmNaturalResReqKey.appendChild(elmReqCode);
			Element catalogID = docUpdateNFES.createElement("CatalogID");
			elmReqCode.appendChild(catalogID);
			catalogID.setTextContent("S");
			Element elmBaseReqNo = docUpdateNFES.createElement("SequenceNumber");
			elmReqCode.appendChild(elmBaseReqNo);
			// Populate Address
			populateAddrInfo(docUpdateNFES, htAddrInfo);
			// Populate Shipping Contact Name and Phone
			Element elmShippingContactName = docUpdateNFES.createElement("ro:ShippingContactName");
			elmShippingContactName.setTextContent(shippingContactName);
			elmUpdtNFESResReq.appendChild(elmShippingContactName);
			Element elmShippingContactPhone = docUpdateNFES.createElement("ro:ShippingContactPhone");
			elmShippingContactPhone.setTextContent(shippingContactPhone);
			elmUpdtNFESResReq.appendChild(elmShippingContactPhone);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
		}
		logger.verbose("@@@@@ Exiting NWCGUpdateNFESUtil::prepareBlankUpdateNFESResourceRequestReqDoc @@@@@");
		return docUpdateNFES;
	}

	/**
	 * 
	 * @param docUpdateNFES
	 * @param htAddrInfo
	 */
	private void populateAddrInfo(Document docUpdateNFES, Hashtable<String, String> htAddrInfo) {
		logger.verbose("@@@@@ Entering NWCGUpdateNFESUtil::populateAddrInfo @@@@@");
		Element elmDocUpdateNFES = docUpdateNFES.getDocumentElement();
		String addrType = htAddrInfo.get(NWCGConstants.EXTN_NAV_INFO_ATTR);
		if (addrType.equals(NWCGConstants.WILL_PICK_UP)) {
			Element elmWillPickUp = docUpdateNFES.createElement("ro:WillPickUpInfo");
			elmDocUpdateNFES.appendChild(elmWillPickUp);
			Element elmPickUpContactName = docUpdateNFES.createElement("PickUpContactName");
			elmPickUpContactName.setTextContent(htAddrInfo.get(NWCGConstants.EXTN_WILL_PICK_UP_NAME));
			elmWillPickUp.appendChild(elmPickUpContactName);
			Element elmPickUpContactInfo = docUpdateNFES.createElement("PickUpContactInfo");
			elmPickUpContactInfo.setTextContent(htAddrInfo.get(NWCGConstants.EXTN_WILL_PICK_UP_INFO));
			elmWillPickUp.appendChild(elmPickUpContactInfo);
			Element elmPickUpDateTime = docUpdateNFES.createElement("PickUpDateTime");
			elmPickUpDateTime.setTextContent(htAddrInfo.get(NWCGConstants.EXTN_REQ_DELIVERY_DATE_ATTR));
			elmWillPickUp.appendChild(elmPickUpDateTime);
		} else if (addrType.equals(NWCGConstants.SHIPPING_INSTRUCTIONS)) {
			Element elmShippingInstr = docUpdateNFES.createElement("ro:ShippingInstructions");
			elmDocUpdateNFES.appendChild(elmShippingInstr);
			Element elmShippingInstrValue = docUpdateNFES.createElement("ShippingInstructions");
			elmShippingInstrValue.setTextContent(htAddrInfo.get(NWCGConstants.EXTN_SHIPPING_INSTRUCTIONS_ATTR));
			elmShippingInstr.appendChild(elmShippingInstrValue);
			Element elmShippingInstrCity = docUpdateNFES.createElement("City");
			elmShippingInstrCity.setTextContent(htAddrInfo.get(NWCGConstants.EXTN_SHIPPING_INSTR_CITY_ATTR));
			elmShippingInstr.appendChild(elmShippingInstrCity);
			Element elmShippingInstrState = docUpdateNFES.createElement("State");
			elmShippingInstrState.setTextContent(htAddrInfo.get(NWCGConstants.EXTN_SHIPPING_INSTR_STATE_ATTR));
			elmShippingInstr.appendChild(elmShippingInstrState);
		} else if (addrType.equals(NWCGConstants.SHIPPING_ADDRESS)) {
			Element elmShippingAddr = docUpdateNFES.createElement("ro:ShippingAddress");
			elmDocUpdateNFES.appendChild(elmShippingAddr);
			Element elmName = docUpdateNFES.createElement("Name");
			elmName.setTextContent(htAddrInfo.get(NWCGConstants.FIRST_NAME) + " " + htAddrInfo.get(NWCGConstants.LAST_NAME));
			elmShippingAddr.appendChild(elmName);
			Element elmType = docUpdateNFES.createElement("Type");
			elmType.setTextContent("Shipping");
			elmShippingAddr.appendChild(elmType);
			Element elmLine1 = docUpdateNFES.createElement("Line1");
			elmLine1.setTextContent(htAddrInfo.get(NWCGConstants.ADDRESS_LINE_1));
			elmShippingAddr.appendChild(elmLine1);
			Element elmLine2 = docUpdateNFES.createElement("Line2");
			elmLine2.setTextContent(htAddrInfo.get(NWCGConstants.ADDRESS_LINE_2));
			elmShippingAddr.appendChild(elmLine2);
			Element elmCity = docUpdateNFES.createElement("City");
			elmCity.setTextContent(htAddrInfo.get(NWCGConstants.CITY));
			elmShippingAddr.appendChild(elmCity);
			Element elmState = docUpdateNFES.createElement("State");
			elmState.setTextContent(htAddrInfo.get(NWCGConstants.STATE));
			elmShippingAddr.appendChild(elmState);
			Element elmZipCode = docUpdateNFES.createElement("ZipCode");
			elmZipCode.setTextContent(htAddrInfo.get(NWCGConstants.ZIP_CODE));
			elmShippingAddr.appendChild(elmZipCode);
			Element elmCountryCode = docUpdateNFES.createElement("CountryCode");
			elmCountryCode.setTextContent(htAddrInfo.get(NWCGConstants.COUNTRY));
			elmShippingAddr.appendChild(elmCountryCode);
			String unitID = htAddrInfo.get(NWCGConstants.ALTERNATE_EMAIL_ID);
			if (unitID != null && (unitID.indexOf("-") != -1)) {
				Element elmUnitID = docUpdateNFES.createElement("UnitID");
				elmShippingAddr.appendChild(elmUnitID);
				Element elmUnitIDPrefix = docUpdateNFES.createElement("UnitIDPrefix");
				elmUnitIDPrefix.setTextContent(unitID.substring(0, unitID.indexOf("-")));
				elmUnitID.appendChild(elmUnitIDPrefix);
				Element elmUnitIDSuffix = docUpdateNFES.createElement("UnitIDSuffix");
				elmUnitIDSuffix.setTextContent(unitID.substring(unitID.indexOf("-") + 1));
				elmUnitID.appendChild(elmUnitIDSuffix);
			}
		}
		logger.verbose("@@@@@ Exiting NWCGUpdateNFESUtil::populateAddrInfo @@@@@");
	}

	/**
	 * This will populate the Address Info (Either Shipping Instructions, Will
	 * Pick Up or Ship To) based on EXTN_NAV_INST field
	 * 
	 * @param elmOrderDoc
	 * @param elmExtn
	 * @return
	 */
	public void setAddressInfoInHT(Element elmOrderDoc, Element elmExtn, Hashtable<String, String> htAddrInfo) {
		logger.verbose("@@@@@ Entering NWCGUpdateNFESUtil::setAddressInfoInHT @@@@@");
		String addrType = elmExtn.getAttribute(NWCGConstants.EXTN_NAV_INFO_ATTR);
		htAddrInfo.put(NWCGConstants.EXTN_NAV_INFO_ATTR, addrType);
		if (addrType.equalsIgnoreCase(NWCGConstants.WILL_PICK_UP)) {
			htAddrInfo.put(NWCGConstants.EXTN_WILL_PICK_UP_NAME, elmExtn.getAttribute(NWCGConstants.EXTN_WILL_PICK_UP_NAME));
			htAddrInfo.put(NWCGConstants.EXTN_WILL_PICK_UP_INFO, elmExtn.getAttribute(NWCGConstants.EXTN_WILL_PICK_UP_INFO));
			htAddrInfo.put(NWCGConstants.EXTN_REQ_DELIVERY_DATE_ATTR, elmExtn.getAttribute(NWCGConstants.EXTN_REQ_DELIVERY_DATE_ATTR));
		} else if (addrType.equalsIgnoreCase(NWCGConstants.SHIPPING_INSTRUCTIONS)) {
			htAddrInfo.put(NWCGConstants.EXTN_SHIPPING_INSTRUCTIONS_ATTR, elmExtn.getAttribute("ExtnShippingInstructions"));
			htAddrInfo.put(NWCGConstants.EXTN_SHIPPING_INSTR_CITY_ATTR, elmExtn.getAttribute("ExtnShipInstrCity"));
			htAddrInfo.put(NWCGConstants.EXTN_SHIPPING_INSTR_STATE_ATTR, elmExtn.getAttribute("ExtnShipInstrState"));
		} else if (addrType.equalsIgnoreCase(NWCGConstants.SHIPPING_ADDRESS)) {
			NodeList nlPersonInfoShipTo = elmOrderDoc.getElementsByTagName(NWCGConstants.PERSON_INFO_SHIPTO);
			// There will only be one PersonInfoShipTo element in the template controlled Order xml
			Element elmPersonInfoShipTo = (Element) nlPersonInfoShipTo.item(0);
			htAddrInfo.put(NWCGConstants.FIRST_NAME, elmPersonInfoShipTo.getAttribute(NWCGConstants.FIRST_NAME));
			htAddrInfo.put(NWCGConstants.LAST_NAME, elmPersonInfoShipTo.getAttribute(NWCGConstants.LAST_NAME));
			htAddrInfo.put(NWCGConstants.ADDRESS_LINE_1, elmPersonInfoShipTo.getAttribute(NWCGConstants.ADDRESS_LINE_1));
			htAddrInfo.put(NWCGConstants.ADDRESS_LINE_2, elmPersonInfoShipTo.getAttribute(NWCGConstants.ADDRESS_LINE_2));
			htAddrInfo.put(NWCGConstants.CITY, elmPersonInfoShipTo.getAttribute(NWCGConstants.CITY));
			htAddrInfo.put(NWCGConstants.STATE, elmPersonInfoShipTo.getAttribute(NWCGConstants.STATE));
			htAddrInfo.put(NWCGConstants.COUNTRY, elmPersonInfoShipTo.getAttribute(NWCGConstants.COUNTRY));
			htAddrInfo.put(NWCGConstants.ZIP_CODE, elmPersonInfoShipTo.getAttribute(NWCGConstants.ZIP_CODE));
			htAddrInfo.put(NWCGConstants.ALTERNATE_EMAIL_ID, elmPersonInfoShipTo.getAttribute(NWCGConstants.ALTERNATE_EMAIL_ID));
		}
		logger.verbose("@@@@@ Exiting NWCGUpdateNFESUtil::setAddressInfoInHT @@@@@");
	}

	/**
	 * UTFDetail should be present after ConsolidationDetail. As part of the
	 * looping, we never know which (UTF or Fill or Consolidation) comes first.
	 * So, I am adding the UTFDetail before the address element.
	 * 
	 * @param docUpdtNFES
	 * @param ol
	 */
	public void populateUTFDetail(Document docUpdtNFES, String utfQty, String notes, Hashtable<String, String> htAddrInfo) {
		logger.verbose("@@@@@ Entering NWCGUpdateNFESUtil::populateUTFDetail @@@@@");
		Element elmDocUpdtNFES = docUpdtNFES.getDocumentElement();
		Element elmUTFDtl = docUpdtNFES.createElement("ro:UtfDetail");
		String addrType = htAddrInfo.get(NWCGConstants.EXTN_NAV_INFO_ATTR);
		Element elmAddrType = null;
		if (addrType.equals(NWCGConstants.WILL_PICK_UP)) {
			elmAddrType = (Element) elmDocUpdtNFES.getElementsByTagName("ro:WillPickUpInfo").item(0);
		} else if (addrType.equals(NWCGConstants.SHIPPING_INSTRUCTIONS)) {
			elmAddrType = (Element) elmDocUpdtNFES.getElementsByTagName("ro:ShippingInstructions").item(0);
		} else if (addrType.equals(NWCGConstants.SHIPPING_ADDRESS)) {
			elmAddrType = (Element) elmDocUpdtNFES.getElementsByTagName("ro:ShippingAddress").item(0);
		}
		elmDocUpdtNFES.insertBefore(elmUTFDtl, elmAddrType);
		Element elmUTFQty = docUpdtNFES.createElement("Quantity");
		elmUTFDtl.appendChild(elmUTFQty);
		if (utfQty.indexOf(".") != -1) {
			utfQty = utfQty.substring(0, utfQty.indexOf("."));
		}
		elmUTFQty.setTextContent(utfQty);
		if (notes != null && notes.trim().length() > 0) {
			Element elmNote = docUpdtNFES.createElement("UserDocumentation");
			elmUTFDtl.appendChild(elmNote);
			elmNote.setTextContent(notes);
		}
		logger.verbose("@@@@@ Exiting NWCGUpdateNFESUtil::populateUTFDetail @@@@@");
	}

	/**
	 * ConsolidationDetail should be present after FillDetail or before
	 * UTFDetail. UTFDetail and ConsolidationDetail cannot be part of the same
	 * request at any time. So, adding the ConsolidationDetail before the
	 * address element.
	 * 
	 * @param docUpdtNFES
	 * @param ol
	 */
	public void populateConsolidationDetail(Document docUpdtNFES, String notes, Hashtable<String, String> htAddrInfo) {
		logger.verbose("@@@@@ Entering NWCGUpdateNFESUtil::populateConsolidationDetail @@@@@");
		Element elmDocUpdtNFES = docUpdtNFES.getDocumentElement();
		Element elmConsDtl = docUpdtNFES.createElement("ro:ConsolidationDetail");
		String addrType = htAddrInfo.get(NWCGConstants.EXTN_NAV_INFO_ATTR);
		Element elmAddrType = null;
		if (addrType.equals(NWCGConstants.WILL_PICK_UP)) {
			elmAddrType = (Element) elmDocUpdtNFES.getElementsByTagName("ro:WillPickUpInfo").item(0);
		} else if (addrType.equals(NWCGConstants.SHIPPING_INSTRUCTIONS)) {
			elmAddrType = (Element) elmDocUpdtNFES.getElementsByTagName("ro:ShippingInstructions").item(0);
		} else if (addrType.equals(NWCGConstants.SHIPPING_ADDRESS)) {
			elmAddrType = (Element) elmDocUpdtNFES.getElementsByTagName("ro:ShippingAddress").item(0);
		}
		elmDocUpdtNFES.insertBefore(elmConsDtl, elmAddrType);
		Element elmNote = docUpdtNFES.createElement("UserDocumentation");
		elmConsDtl.appendChild(elmNote);
		elmNote.setTextContent(notes);
		logger.verbose("@@@@@ Exiting NWCGUpdateNFESUtil::populateConsolidationDetail @@@@@");
	}

	/**
	 * This method will get the latest note (NoteText) from the notes element
	 * 
	 * @param elmNotes
	 * @return
	 */
	public String getLatestNote(Element elmNotes) {
		logger.verbose("@@@@@ Entering NWCGUpdateNFESUtil::getLatestNote @@@@@");
		String latestNote = "";
		String noOfNotes = elmNotes.getAttribute("NumberOfNotes");
		NodeList nlNoteList = elmNotes.getElementsByTagName("Note");
		if (nlNoteList == null || nlNoteList.getLength() < 1) {
			return latestNote;
		}
		boolean obtLatestNote = false;
		for (int i = 0; i < nlNoteList.getLength() && !obtLatestNote; i++) {
			Element elmNote = (Element) nlNoteList.item(i);
			if (elmNote.getAttribute("SequenceNo").equalsIgnoreCase(noOfNotes)) {
				obtLatestNote = true;
				latestNote = elmNote.getAttribute("NoteText");
				if (latestNote == null) {
					latestNote = "";
				}
			}
		}
		logger.verbose("@@@@@ Exiting NWCGUpdateNFESUtil::getLatestNote @@@@@");
		return latestNote;
	}
}