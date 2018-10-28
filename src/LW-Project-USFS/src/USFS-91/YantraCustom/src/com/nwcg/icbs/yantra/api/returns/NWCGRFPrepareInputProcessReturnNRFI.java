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
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGRFPrepareInputProcessReturnNRFI implements YIFCustomApi {
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGRFPrepareInputProcessReturnNRFI.class);
	
	Map<String, String> trackItemList = new HashMap<String, String>();

	Map<String, String> parentItemList = new HashMap<String, String>();

	public void setProperties(Properties arg0) throws Exception {
	}

	public Document rfPrepareInputProcessReturnNRFI(YFSEnvironment env,
			Document inXML) throws Exception {

		// prepare input for process return xml

		// convert
		// <TempQ><Receipt/><ReceiptLine/><ReceiptLine/><ReceiptLine/><ReceiptLine/>
		// into
		// <Receipt><ReceiptLines><ReceiptLine/><ReceiptLine/><ReceiptLine/><ReceiptLine/></ReceiptLines></Receipt>

		Document processReturnInDoc = XMLUtil.getDocument();

		Element Receipt = (Element) inXML.getElementsByTagName("Receipt").item(
				0);

		Node temp = processReturnInDoc.importNode(Receipt, true);
		processReturnInDoc.appendChild(temp);
		Element ReceiptLines = processReturnInDoc.createElement("ReceiptLines");
		processReturnInDoc.getDocumentElement().appendChild(ReceiptLines);

		NodeList ReceiptLineNL = inXML.getElementsByTagName("ReceiptLine");
		int recNLLen = ReceiptLineNL.getLength();
		String isSeriallyTracked = "N";
		// hashmap to keep track of trackable items

		// String strItemID = "";
		for (int i = 0; i < recNLLen; i++) {
			Element elemRL = (Element) ReceiptLineNL.item(i);
			isSeriallyTracked = elemRL.getAttribute("isSeriallyTracked");

			if (!isSeriallyTracked.equalsIgnoreCase("Y")) {

				temp = processReturnInDoc.importNode(elemRL, true);
				ReceiptLines.appendChild(temp);

			} else {

				elemRL = prepareTrackIDRLine(elemRL, processReturnInDoc);
				ReceiptLines.appendChild(elemRL);
			}
			// end if(!isSeriallyTracked.equalsIgnoreCase("Y")){

		}

		// Document processReturnOutDoc = CommonUtilities.invokeService(env,
		// "NWCGInvokeAITRFIReturnService", processReturnInDoc);
		Document processReturnOutDoc = CommonUtilities.invokeService(env,
				"NWCGInvokePerformReturnService", processReturnInDoc);

		updateNWCGIncidentReturn(env, processReturnOutDoc);

		return processReturnOutDoc;

	}

	private Element prepareTrackIDRLine(Element elemRL,
			Document processReturnInDoc) throws Exception {

		Element reciptLineElem = processReturnInDoc
				.createElement("ReceiptLine");
		Element serialInfoMainItemElem = processReturnInDoc
				.createElement("SerialInfoMainItem");

		// set receiptline info
		reciptLineElem.setAttribute("ItemID", elemRL.getAttribute("ItemID"));
		reciptLineElem.setAttribute("ProductClass",
				elemRL.getAttribute("ProductClass"));
		reciptLineElem.setAttribute("QtyReturned", "1");
		String strDispositionCode = elemRL.getAttribute("DispositionCode");
		reciptLineElem.setAttribute("DispositionCode", strDispositionCode);
		reciptLineElem.setAttribute(strDispositionCode, "1");
		reciptLineElem.setAttribute("ShortDescription",
				elemRL.getAttribute("ShortDescription"));
		reciptLineElem.setAttribute("UOM", elemRL.getAttribute("UOM"));
		if (elemRL.hasAttribute("LPNNo"))
			reciptLineElem.setAttribute("LPNNo", elemRL.getAttribute("LPNNo"));

		serialInfoMainItemElem.setAttribute("DispositionCode",
				strDispositionCode);

		String strPrimarySerialNo = elemRL.getAttribute("PrimarySerialNo");
		String strSecondarySerialNo = elemRL.getAttribute("SecondarySerialNo");
		String strTagAttribute1 = elemRL.getAttribute("TagAttribute1");
		String strTagAttribute2 = elemRL.getAttribute("TagAttribute2");
		String strTagAttribute3 = elemRL.getAttribute("TagAttribute3");
		String strTagAttribute4 = elemRL.getAttribute("TagAttribute4");
		String strTagAttribute5 = elemRL.getAttribute("TagAttribute5");
		String strTagAttribute6 = elemRL.getAttribute("TagAttribute6");

		if (strPrimarySerialNo != null && !strPrimarySerialNo.equals(""))
			serialInfoMainItemElem.setAttribute("PrimarySerialNo",
					strPrimarySerialNo);
		if (strSecondarySerialNo != null && !strSecondarySerialNo.equals(""))
			serialInfoMainItemElem.setAttribute("SecondarySerialNo",
					strSecondarySerialNo);
		if (strTagAttribute1 != null && !strTagAttribute1.equals(""))
			serialInfoMainItemElem.setAttribute("TagAttribute1",
					strTagAttribute1);
		if (strTagAttribute2 != null && !strTagAttribute2.equals(""))
			serialInfoMainItemElem.setAttribute("TagAttribute2",
					strTagAttribute2);
		if (strTagAttribute3 != null && !strTagAttribute3.equals(""))
			serialInfoMainItemElem.setAttribute("TagAttribute3",
					strTagAttribute3);
		if (strTagAttribute4 != null && !strTagAttribute4.equals(""))
			serialInfoMainItemElem.setAttribute("TagAttribute4",
					strTagAttribute4);
		if (strTagAttribute5 != null && !strTagAttribute5.equals(""))
			serialInfoMainItemElem.setAttribute("TagAttribute5",
					strTagAttribute5);
		if (strTagAttribute6 != null && !strTagAttribute6.equals(""))
			serialInfoMainItemElem.setAttribute("TagAttribute6",
					strTagAttribute6);

		reciptLineElem.appendChild(serialInfoMainItemElem);

		return reciptLineElem;
	}

	private void updateNWCGIncidentReturn(YFSEnvironment env,
			Document processReturnOutDoc) throws Exception {

		String strCacheID = "";
		String strIncidentNo = "";
		String strIncidentYear = "";
		String strIssueNo = "";
		String strReceivingLoc = "";

		Element eleOutRoot = processReturnOutDoc.getDocumentElement();
		strCacheID = eleOutRoot.getAttribute("CacheID");
		if (eleOutRoot.hasAttribute("IssueNo"))
			strIssueNo = eleOutRoot.getAttribute("IssueNo");
		if (eleOutRoot.hasAttribute("IncidentYear"))
			strIncidentYear = eleOutRoot.getAttribute("IncidentYear");
		if (eleOutRoot.hasAttribute("IncidentNo"))
			strIncidentNo = eleOutRoot.getAttribute("IncidentNo");
		strReceivingLoc = eleOutRoot.getAttribute("ReceivingDock");

		Document returnDoc = XMLUtil.createDocument("Return");
		Element el_Return = returnDoc.getDocumentElement();
		el_Return.setAttribute("CacheID", strCacheID);
		el_Return.setAttribute("EnterpriseCode", "NWCG");
		el_Return.setAttribute("IncidentNo", strIncidentNo);
		el_Return.setAttribute("IncidentYear", strIncidentYear);
		el_Return.setAttribute("IssueNo", strIssueNo);
		el_Return.setAttribute("LocationId", strReceivingLoc);

		Element el_ReturnLines = returnDoc.createElement("ReturnLines");
		el_Return.appendChild(el_ReturnLines);

		NodeList lsReceiptLineList = processReturnOutDoc
				.getElementsByTagName("ReceiptLine");
		for (int i = 0; i < lsReceiptLineList.getLength(); i++) {
			Element eleReceiptLine = (Element) lsReceiptLineList.item(i);
			if (eleReceiptLine.hasAttribute("LPNNo")) {
				Element el_ReturnLine = returnDoc.createElement("ReturnLine");
				el_ReturnLines.appendChild(el_ReturnLine);

				el_ReturnLine.setAttribute("ItemID",
						eleReceiptLine.getAttribute("ItemID"));
				el_ReturnLine.setAttribute("ProductClass",
						eleReceiptLine.getAttribute("ProductClass"));
				el_ReturnLine.setAttribute("QuantityNRFI",
						eleReceiptLine.getAttribute("QtyReturned"));
				el_ReturnLine.setAttribute("QuantityReturned",
						eleReceiptLine.getAttribute("QtyReturned"));
				el_ReturnLine.setAttribute("QuantityShipped", "0");
				el_ReturnLine.setAttribute("ReceivedAsComponent", "N");
				el_ReturnLine.setAttribute("IsComponent", "False");
				el_ReturnLine.setAttribute("QuantityUnsRet", "0");
				el_ReturnLine.setAttribute("QuantityUnsNwtReturn", "0");
				el_ReturnLine.setAttribute("QuantityRFI", "0");
				el_ReturnLine.setAttribute("UnitOfMeasure",
						eleReceiptLine.getAttribute("UOM"));

				NodeList lsSerialInfo = eleReceiptLine
						.getElementsByTagName("SerialInfoMainItem");
				if (lsSerialInfo.getLength() > 0) {
					String strTrackableID = ((Element) lsSerialInfo.item(0))
							.getAttribute("PrimarySerialNo");
					String strSecondrySerialNo = ((Element) lsSerialInfo
							.item(0)).getAttribute("SecondarySerialNo");
					el_ReturnLine.setAttribute("TrackableID", strTrackableID);

					// Update trackable inventory table
					Document updateDoc = XMLUtil
							.createDocument("NWCGTrackableItem");
					Element eleUpdate = updateDoc.getDocumentElement();
					eleUpdate.setAttribute("ItemID",
							eleReceiptLine.getAttribute("ItemID"));
					eleUpdate.setAttribute("SerialNo", strTrackableID);
					eleUpdate.setAttribute("SecondarySerial",
							strSecondrySerialNo);
					eleUpdate.setAttribute("SerialStatus",
							NWCGConstants.SERIAL_STATUS_NRFI);
					eleUpdate.setAttribute("SerialStatusDesc",
							NWCGConstants.SERIAL_STATUS_NRFI_DESC);
					CommonUtilities
							.invokeService(
									env,
									NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE,
									updateDoc);
					getSubComponent(env, strTrackableID);
					getParents(env, strTrackableID);

				} else {
					el_ReturnLine.setAttribute("TrackableID", "");
				}
			}

		}
		if (trackItemList != null && !trackItemList.isEmpty()) {
			Set<String> KeySet = trackItemList.keySet();
			java.util.Iterator<String> itr = KeySet.iterator();
			while (itr.hasNext()) {
				String strSerialNo = itr.next();
				String strSecondrySerial = trackItemList.get(strSerialNo);
				Document updateCompDoc = XMLUtil
						.createDocument("NWCGTrackableItem");
				Element eleupdateCompDoc = updateCompDoc.getDocumentElement();
				eleupdateCompDoc.setAttribute("SerialNo", strSerialNo);
				eleupdateCompDoc.setAttribute("SecondarySerial",
						strSecondrySerial);
				eleupdateCompDoc.setAttribute("SerialStatus",
						NWCGConstants.SERIAL_STATUS_NRFI);
				eleupdateCompDoc.setAttribute("SerialStatusDesc",
						NWCGConstants.SERIAL_STATUS_NRFI_DESC);
				CommonUtilities.invokeService(env,
						NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE,
						updateCompDoc);

			}

		}
		if (parentItemList != null && !parentItemList.isEmpty()) {
			Set<String> KeySet = parentItemList.keySet();
			java.util.Iterator<String> itr = KeySet.iterator();
			while (itr.hasNext()) {
				String strSerialNo = itr.next();
				String strSecondrySerial = parentItemList.get(strSerialNo);
				Document updateCompDoc = XMLUtil
						.createDocument("NWCGTrackableItem");
				Element eleupdateCompDoc = updateCompDoc.getDocumentElement();
				eleupdateCompDoc.setAttribute("SerialNo", strSerialNo);
				eleupdateCompDoc.setAttribute("SecondarySerial",
						strSecondrySerial);
				eleupdateCompDoc.setAttribute("SerialStatus", "C");
				eleupdateCompDoc.setAttribute("SerialStatusDesc",
						"Received as Component");
				CommonUtilities.invokeService(env,
						NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE,
						updateCompDoc);

			}

		}
		NodeList lsReturnLines = returnDoc.getElementsByTagName("ReturnLine");
		if (lsReturnLines.getLength() > 0)
			CommonUtilities
					.invokeService(env,
							NWCGConstants.NWCG_UPDATE_REC_ON_RECEIPT_SERVICE,
							returnDoc);
	}

	private String getGlobalserialKey(YFSEnvironment env, String strSerialNo)
			throws Exception {
		String strGSKey = "";
		Document docCompIn = XMLUtil.createDocument("Serial");
		Element eleRootIn = docCompIn.getDocumentElement();
		eleRootIn.setAttribute("SerialNo", strSerialNo);
		Document docCompOut = CommonUtilities.invokeAPI(env, getTemplate(),
				"getSerialList", docCompIn);
		Element eleRootOut = docCompOut.getDocumentElement();
		NodeList lsSerial = eleRootOut.getElementsByTagName("Serial");
		if (lsSerial != null && lsSerial.getLength() > 0) {
			Element eleSerial = (Element) lsSerial.item(0);
			strGSKey = eleSerial.getAttribute("GlobalSerialKey");
		}
		return strGSKey;

	}

	private Document getComponents(YFSEnvironment env, String strGSKey)
			throws Exception {
		Document docCompOut = null;
		Document docCompIn = XMLUtil.createDocument("Serial");
		Element eleRootIn = docCompIn.getDocumentElement();
		eleRootIn.setAttribute("ParentSerialKey", strGSKey);
		docCompOut = CommonUtilities.invokeAPI(env, getTemplate(),
				"getSerialList", docCompIn);
		return docCompOut;

	}

	private void getSubComponent(YFSEnvironment env, String strSerialNo)
			throws Exception {
		String strGSK = getGlobalserialKey(env, strSerialNo);
		if (!strGSK.equals("")) {
			Document docSerialList = getComponents(env, strGSK);
			NodeList lsSerialList = docSerialList
					.getElementsByTagName("Serial");
			if (lsSerialList != null && lsSerialList.getLength() > 0) {
				for (int i = 0; i < lsSerialList.getLength(); i++) {
					Element eleSerial = (Element) lsSerialList.item(i);
					String strSerialNum = eleSerial.getAttribute("SerialNo");
					String strSecondrySerial = eleSerial
							.getAttribute("SecondarySerial1");
					trackItemList.put(strSerialNum, strSecondrySerial);
					getSubComponent(env, strSerialNum);
				}

			}
		}

	}

	private Document getTemplate() throws Exception {

		Document docTemplate = XMLUtil.createDocument("SerialList");
		Element eledocTemplate = docTemplate.getDocumentElement();
		Element eleSerial = docTemplate.createElement("Serial");
		eledocTemplate.appendChild(eleSerial);
		eleSerial.setAttribute("GlobalSerialKey", "");
		eleSerial.setAttribute("SecondarySerial1", "");
		eleSerial.setAttribute("SerialNo", "");
		eleSerial.setAttribute("ParentSerialKey", "");
		return docTemplate;
	}

	private void getParents(YFSEnvironment env, String strSerialNo)
			throws Exception {
		String strPSKey = getParentserialKey(env, strSerialNo);
		if (strPSKey != null && !strPSKey.equals("")) {
			Document docParentDetail = getParentDetail(env, strPSKey);
			NodeList lsSerialList = docParentDetail
					.getElementsByTagName("Serial");
			Element eleSerial = (Element) lsSerialList.item(0);
			String strParentSerialNo = eleSerial.getAttribute("SerialNo");
			String strParentSecondrySerialNo = eleSerial
					.getAttribute("SecondarySerial1");
			parentItemList.put(strParentSerialNo, strParentSecondrySerialNo);
			getParents(env, strParentSerialNo);

		}
	}

	private String getParentserialKey(YFSEnvironment env, String strSerialNo)
			throws Exception {
		String strPSKey = "";
		Document docCompIn = XMLUtil.createDocument("Serial");
		Element eleRootIn = docCompIn.getDocumentElement();
		eleRootIn.setAttribute("SerialNo", strSerialNo);
		Document docCompOut = CommonUtilities.invokeAPI(env, getTemplate(),
				"getSerialList", docCompIn);
		Element eleRootOut = docCompOut.getDocumentElement();
		NodeList lsSerial = eleRootOut.getElementsByTagName("Serial");
		if (lsSerial != null && lsSerial.getLength() > 0) {
			Element eleSerial = (Element) lsSerial.item(0);
			strPSKey = eleSerial.getAttribute("ParentSerialKey");
		}
		return strPSKey;

	}

	private Document getParentDetail(YFSEnvironment env, String strPSKey)
			throws Exception {

		Document docParentDetail = null;
		Document docInDoc = XMLUtil.createDocument("Serial");
		Element rootInDoc = docInDoc.getDocumentElement();
		rootInDoc.setAttribute("GlobalSerialKey", strPSKey);
		docParentDetail = CommonUtilities.invokeAPI(env, getTemplate(),
				"getSerialList", docInDoc);
		return docParentDetail;

	}

}
