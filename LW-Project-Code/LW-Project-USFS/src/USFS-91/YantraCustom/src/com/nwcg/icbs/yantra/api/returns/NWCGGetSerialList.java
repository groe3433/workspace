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

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
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

public class NWCGGetSerialList implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGGetSerialList.class);
	
	private Properties _properties;

	String IsSerialTracked = "";
	String TagControlFlag = "";
	String ShortDescription = "";
	String TimeSensitive = "";

	public Document getSerialList(YFSEnvironment env, Document inXML)
			throws Exception, YFSException {
		logger.verbose("@@@@@ Entering NWCGGetSerialList::getSerialList");
		Document getSerialList_Out = null;

		if (logger.isVerboseEnabled()) {
			logger.verbose("Entering the NWCGGetSerialList API , input document is:"
					+ XMLUtil.getXMLString(inXML));
		}// End log

		try {
			if (logger.isVerboseEnabled())
				logger.verbose("\n\n ****Inside IsSerialUnique API (passOnForVerification)******* :-\n"
						+ XMLUtil.getXMLString(inXML));

			NodeList InvItemList = inXML.getDocumentElement()
					.getElementsByTagName("InventoryItem");

			Element InvAttEl = (Element) InvItemList.item(0);
			String ItemID = InvAttEl.getAttribute("ItemID");
			String ParentItemID = InvAttEl.getAttribute("ParentItemID");
			String SerialNo = inXML.getDocumentElement().getAttribute(
					"SerialNo");
			String SerialNoQryType = inXML.getDocumentElement().getAttribute(
					"SerialNoQryType");
			String IncidentNo = inXML.getDocumentElement().getAttribute(
					"IncidentNo");
			String IncidentYear = inXML.getDocumentElement().getAttribute(
					"IncidentYear");
			String IssueNo = inXML.getDocumentElement().getAttribute("IssueNo");
			String Cache = inXML.getDocumentElement().getAttribute("CacheID");
			String NoOfSecSerials = inXML.getDocumentElement().getAttribute(
					"NoOfSecSerials");
			String CurrentSerialList = inXML.getDocumentElement().getAttribute(
					"CurrentSerialList");
			String CurrentSerialNo = inXML.getDocumentElement().getAttribute(
					"CurrentSerialNo");
			String TagAttribute1 = "";
			String TagAttribute2 = "";
			String TagAttribute3 = "";
			String TagAttribute4 = "";
			String TagAttribute5 = "";
			String TagAttribute6 = "";
			String ManufacturingDate = "";
			// CurrentSerialList =
			// Update_Current_SerialList(env,CurrentSerialList,CurrentSerialNo);

			if (logger.isVerboseEnabled())
				logger.verbose(" ItemID:-" + ItemID + "SerialNo:-" + SerialNo);
			Document inDoc = XMLUtil.newDocument();

			Element el_Serial = inDoc.createElement("Serial");
			inDoc.appendChild(el_Serial);
			el_Serial.setAttribute("AtNode", "N");
			el_Serial.setAttribute("SerialNo", SerialNo);
			el_Serial.setAttribute("SerialNoQryType", SerialNoQryType);
			Element el_InvItem = inDoc.createElement("InventoryItem");
			el_Serial.appendChild(el_InvItem);
			el_InvItem.setAttribute("ItemID", ItemID);
			Document ReturnList = CommonUtilities.invokeAPI(env,
					"getSerialList", inDoc);

			getSerialList_Out = XMLUtil.newDocument();
			Element Result = getSerialList_Out.createElement("SerialList");
			Result.setAttribute("CurrentSerialList", CurrentSerialList);
			getSerialList_Out.appendChild(Result);

			Element elemReturnListRoot = ReturnList.getDocumentElement();
			NodeList listOfReturns = elemReturnListRoot
					.getElementsByTagName("Serial");
			for (int j = 0; j < listOfReturns.getLength(); j++) {
				Element curReturnLineList = (Element) listOfReturns.item(j);
				String TrackableID = curReturnLineList.getAttribute("SerialNo");
				String SecSerialNo = curReturnLineList
						.getAttribute("SecondarySerial1");
				String ParentSerialKey = curReturnLineList
						.getAttribute("ParentSerialKey");
				NodeList TagList = curReturnLineList
						.getElementsByTagName("TagDetail");
				TagAttribute1 = "";
				TagAttribute2 = "";
				TagAttribute3 = "";
				TagAttribute4 = "";
				TagAttribute5 = "";
				TagAttribute6 = "";
				if (TagList.getLength() > 0) {
					Element TagElem = (Element) curReturnLineList
							.getElementsByTagName("TagDetail").item(0);
					if (TagElem.hasAttribute("LotAttribute1")) {
						TagAttribute1 = TagElem.getAttribute("LotAttribute1");
					}
					if (TagElem.hasAttribute("LotAttribute3")) {
						TagAttribute2 = TagElem.getAttribute("LotAttribute3");
					}
					if (TagElem.hasAttribute("LotNumber")) {
						TagAttribute3 = TagElem.getAttribute("LotNumber");
					}
					if (TagElem.hasAttribute("RevisionNo")) {
						TagAttribute4 = TagElem.getAttribute("RevisionNo");
					}
					if (TagElem.hasAttribute("BatchNo")) {
						TagAttribute5 = TagElem.getAttribute("BatchNo");
					}
					if (TagElem.hasAttribute("LotAttribute2")) {
						TagAttribute6 = TagElem.getAttribute("LotAttribute2");
					}
					if (TagElem.hasAttribute("ManufacturingDate")) {
						ManufacturingDate = TagElem
								.getAttribute("ManufacturingDate");
					}

				}

				Document PIncidentList = null;
				Document CIncidentList = null;

				/*
				 * Commented on 10/12/2009 if (ParentSerialKey.length() > 0) {
				 * String PSerialNo = getParentSerialNo(env,ParentSerialKey); if
				 * (ParentItemID.length() > 0) { PIncidentList =
				 * getIncidentReturnList
				 * (env,IncidentNo,IncidentYear,IssueNo,Cache
				 * ,ParentItemID,PSerialNo); } else { PIncidentList =
				 * getIncidentReturnList
				 * (env,IncidentNo,IncidentYear,IssueNo,Cache,"",PSerialNo); } }
				 * 
				 * CIncidentList =
				 * getIncidentReturnList(env,IncidentNo,IncidentYear
				 * ,IssueNo,Cache,ItemID,TrackableID);
				 * 
				 * int pcnt = 0; int ccnt = 0; if (PIncidentList != null) {
				 * Element elemPIncidentListRoot =
				 * PIncidentList.getDocumentElement(); NodeList PlistOfIncidents
				 * =
				 * elemPIncidentListRoot.getElementsByTagName("NWCGIncidentReturn"
				 * ); pcnt = PlistOfIncidents.getLength(); }
				 * 
				 * if (CIncidentList != null) { Element elemCIncidentListRoot =
				 * CIncidentList.getDocumentElement(); NodeList ClistOfIncidents
				 * =
				 * elemCIncidentListRoot.getElementsByTagName("NWCGIncidentReturn"
				 * ); ccnt = ClistOfIncidents.getLength(); }
				 * 
				 * int incidentcnt = 0; incidentcnt = pcnt + ccnt;
				 */

				int validcnt = validateSerialNo(env, CurrentSerialList,
						TrackableID);

				int trackablecnt = 0;
				trackablecnt = getTrackablecnt(env, TrackableID, SecSerialNo,
						IncidentNo, IncidentYear, Cache, ItemID);

				// if (incidentcnt > 0 && validcnt > 0 && trackablecnt == 1)
				if (validcnt > 0 && trackablecnt == 1) {
					// Element curIncidentElem = (Element)
					// listOfIncidents.item(0);
					// IssueNo = curIncidentElem.getAttribute("IssueNo");
					Element el_serial = getSerialList_Out
							.createElement("Serial");
					el_serial.setAttribute("SerialNo", TrackableID);
					el_serial.setAttribute("SecSerialNo", SecSerialNo);
					el_serial.setAttribute("NoOfSecSerials", NoOfSecSerials);
					el_serial.setAttribute("IncidentNo", IncidentNo);
					el_serial.setAttribute("IncidentYear", IncidentYear);
					el_serial.setAttribute("IssueNo", IssueNo);
					el_serial.setAttribute("CacheID", Cache);
					el_serial.setAttribute("ItemID", ItemID);
					el_serial.setAttribute("TagAttribute1", TagAttribute1);
					el_serial.setAttribute("TagAttribute2", TagAttribute2);
					el_serial.setAttribute("TagAttribute3", TagAttribute3);
					el_serial.setAttribute("TagAttribute4", TagAttribute4);
					el_serial.setAttribute("TagAttribute5", TagAttribute5);
					el_serial.setAttribute("TagAttribute6", TagAttribute6);
					el_serial.setAttribute("ManufacturingDate",
							ManufacturingDate);
					Result.appendChild(el_serial);
				}

			}

			// Getting Trackable IDs for Items Received as Components
			// if (ParentItemID.length() > 0)
			// {
			// getSerialList_Out =
			// ProcessParentItem(env,IncidentNo,IncidentYear,IssueNo,Cache,ItemID,ParentItemID,NoOfSecSerials,getSerialList_Out);
			// }
			logger.verbose("@@@@@ Exiting NWCGGetSerialList::getSerialList");
			return getSerialList_Out;

		} catch (Exception E) {
			logger.error("!!!!! Generated exception " + E.toString());
			throw new NWCGException("NWCG_RETURN_INPUT_VALUES_ERROR");
		}
	}

	public Document ProcessParentItem(YFSEnvironment env, String IncidentNo,
			String IncidentYear, String IssueNo, String Cache, String ItemID,
			String ParentItemID, String NoOfSecSerials, Document Serial_OutDoc)
			throws Exception {
		logger.verbose("@@@@@ Entering NWCGGetSerialList::ProcessParentItem");
		Document PReturnList = getIncidentReturnList(env, IncidentNo,
				IncidentYear, IssueNo, Cache, ParentItemID, "");
		Element elemPReturnListRoot = PReturnList.getDocumentElement();
		Element Result = Serial_OutDoc.getDocumentElement();
		NodeList listOfReturns = elemPReturnListRoot
				.getElementsByTagName("NWCGIncidentReturn");
		for (int p = 0; p < listOfReturns.getLength(); p++) {
			Element curReturnLineList = (Element) listOfReturns.item(p);
			String TrackableID = curReturnLineList.getAttribute("TrackableID");
			if (TrackableID.length() > 0) {
				String GlobalSerialKey = getGlobalSerialKey(env, TrackableID,
						ParentItemID);

				Document CompSerialList = getCompSerialList(env,
						GlobalSerialKey);
				Element CompResult = CompSerialList.getDocumentElement();
				NodeList listOfCompSerials = CompResult
						.getElementsByTagName("Serial");
				for (int l = 0; l < listOfCompSerials.getLength(); l++) {
					Element curCompSerialList = (Element) listOfCompSerials
							.item(l);
					String CompSerialNo = curCompSerialList
							.getAttribute("SerialNo");
					String CompSecSerialNo = curCompSerialList
							.getAttribute("SecondarySerial1");
					String AtNode = curCompSerialList.getAttribute("AtNode");
					if (AtNode.equals("N")) {
						Element el_serial = Serial_OutDoc
								.createElement("Serial");
						el_serial.setAttribute("SerialNo", CompSerialNo);
						el_serial.setAttribute("SecSerialNo", CompSecSerialNo);
						el_serial
								.setAttribute("NoOfSecSerials", NoOfSecSerials);
						el_serial.setAttribute("IncidentNo", IncidentNo);
						el_serial.setAttribute("IncidentYear", IncidentYear);
						el_serial.setAttribute("IssueNo", IssueNo);
						// el_serial.setAttribute("CacheID",Cache);
						el_serial.setAttribute("ItemID", ItemID);
						Result.appendChild(el_serial);
					} 
				} 
			}
		}
		logger.verbose("@@@@@ Exiting NWCGGetSerialList::ProcessParentItem");
		return Serial_OutDoc;
	}

	public Document getIncidentReturnList(YFSEnvironment env,
			String IncidentNo, String IncidentYear, String IssueNo,
			String Cache, String ItemID, String SerialNo) throws Exception {
		logger.verbose("@@@@@ Entering NWCGGetSerialList::getIncidentReturnList");
		Document inDoc = XMLUtil.newDocument();
		Document outDoc = XMLUtil.newDocument();
		Element el_NWCGIncidentReturn = inDoc
				.createElement("NWCGIncidentReturn");
		inDoc.appendChild(el_NWCGIncidentReturn);
		// el_NWCGIncidentReturn.setAttribute("CacheID",Cache);
		el_NWCGIncidentReturn.setAttribute("IncidentNo", IncidentNo);
		el_NWCGIncidentReturn.setAttribute("IncidentYear", IncidentYear);
		// el_NWCGIncidentReturn.setAttribute("IssueNo",IssueNo);
		el_NWCGIncidentReturn.setAttribute("ItemID", ItemID);
		el_NWCGIncidentReturn.setAttribute("TrackableID", SerialNo);
		el_NWCGIncidentReturn.setAttribute("QuantityReturned", "0");
		// el_NWCGIncidentReturn.setAttribute("ReceivedAsComponent","N");

		Element el_OrderBy = inDoc.createElement("OrderBy");
		el_NWCGIncidentReturn.appendChild(el_OrderBy);

		Element el_Attribute = inDoc.createElement("Attribute");
		el_OrderBy.appendChild(el_Attribute);
		el_Attribute.setAttribute("Name", "TrackableID");
		if (logger.isVerboseEnabled())
			logger.verbose("Input xml for querying the table is:-"
					+ XMLUtil.getXMLString(inDoc));

		outDoc = CommonUtilities.invokeService(env,
				NWCGConstants.NWCG_QUERY_LIST_ON_INCIDENT_TABLE_SERVICE, inDoc);
		logger.verbose("@@@@@ Exiting NWCGGetSerialList::getIncidentReturnList");
		return outDoc;
	}

	public Document getCompSerialList(YFSEnvironment env, String ParentSerialKey)
			throws Exception {
		logger.verbose("@@@@@ Entering NWCGGetSerialList::getCompSerialList");
		Document inDoc = XMLUtil.newDocument();
		Document outDoc = XMLUtil.newDocument();

		Element el_PSerial = inDoc.createElement("Serial");
		inDoc.appendChild(el_PSerial);
		el_PSerial.setAttribute("ParentSerialKey", ParentSerialKey);
		outDoc = CommonUtilities.invokeAPI(env, "getSerialList", inDoc);
		logger.verbose("@@@@@ Exiting NWCGGetSerialList::getCompSerialList");
		return outDoc;
	}

	public String getGlobalSerialKey(YFSEnvironment env, String SerialNo,
			String strItemID) throws Exception {
		logger.verbose("@@@@@ Entering NWCGGetSerialList::getGlobalSerialKey");
		String GSerialKey = "";

		Document inDoc = XMLUtil.newDocument();
		Document outDoc = XMLUtil.newDocument();

		Element el_PSerial = inDoc.createElement("Serial");
		inDoc.appendChild(el_PSerial);
		el_PSerial.setAttribute("SerialNo", SerialNo);
		Element el_InvItem = inDoc.createElement("InventoryItem");
		el_PSerial.appendChild(el_InvItem);
		el_InvItem.setAttribute("ItemID", strItemID);
		outDoc = CommonUtilities.invokeAPI(env, "getSerialList", inDoc);

		Element PSerialOut = (Element) outDoc.getDocumentElement()
				.getElementsByTagName("Serial").item(0);
		GSerialKey = PSerialOut.getAttribute("GlobalSerialKey");
		logger.verbose("@@@@@ Exiting NWCGGetSerialList::getGlobalSerialKey");
		return GSerialKey;
	}

	public String getParentSerialNo(YFSEnvironment env, String ParentSerialKey)
			throws Exception {
		logger.verbose("@@@@@ Entering NWCGGetSerialList::getParentSerialNo");
		String PSerialNo = "";
		String PSerialKey1 = "";
		String PSerialKey2 = "";

		Document inDoc = XMLUtil.newDocument();
		Document outDoc = XMLUtil.newDocument();

		Element el_PSerial = inDoc.createElement("Serial");
		inDoc.appendChild(el_PSerial);
		el_PSerial.setAttribute("GlobalSerialKey", ParentSerialKey);
		outDoc = CommonUtilities.invokeAPI(env, "getSerialList", inDoc);

		Element PSerialOut = (Element) outDoc.getDocumentElement()
				.getElementsByTagName("Serial").item(0);
		PSerialNo = PSerialOut.getAttribute("SerialNo");

		PSerialKey1 = PSerialOut.getAttribute("ParentSerialKey");

		if (PSerialKey1.length() > 0) {
			el_PSerial.setAttribute("GlobalSerialKey", PSerialKey1);
			outDoc = CommonUtilities.invokeAPI(env, "getSerialList", inDoc);

			PSerialOut = (Element) outDoc.getDocumentElement()
					.getElementsByTagName("Serial").item(0);
			PSerialNo = PSerialOut.getAttribute("SerialNo");
			PSerialKey2 = PSerialOut.getAttribute("ParentSerialKey");
			if (PSerialKey2.length() > 0) {
				el_PSerial.setAttribute("GlobalSerialKey", PSerialKey2);
				outDoc = CommonUtilities.invokeAPI(env, "getSerialList", inDoc);

				PSerialOut = (Element) outDoc.getDocumentElement()
						.getElementsByTagName("Serial").item(0);
				PSerialNo = PSerialOut.getAttribute("SerialNo");
			}
		}
		logger.verbose("@@@@@ Exiting NWCGGetSerialList::getParentSerialNo");
		return PSerialNo;
	}

	public String Update_Current_SerialList(YFSEnvironment env,
			String CurrentList, String CurrentNo) throws Exception {
		logger.verbose("@@@@@ Entering NWCGGetSerialList::Update_Current_SerialList");
		String[] CLfields = CurrentList.split(",");
		String UList = "";
		for (int i = 0; i < CLfields.length; i++) {
			if (!CLfields[i].equals(CurrentNo)) {
				UList = UList + "," + CLfields[i];
			}
		}
		logger.verbose("@@@@@ Exiting NWCGGetSerialList::Update_Current_SerialList");
		return UList;
	}

	public int validateSerialNo(YFSEnvironment env, String CurrentList,
			String SerialNo) throws Exception {
		logger.verbose("@@@@@ Entering NWCGGetSerialList::validateSerialNo");
		String[] CLfields = CurrentList.split(",");

		for (int i = 0; i < CLfields.length; i++) {
			if (CLfields[i].equals(SerialNo)) {
				return 0;
			}
		}
		logger.verbose("@@@@@ Exiting NWCGGetSerialList::validateSerialNo");
		return 1;
	}

	public int getTrackablecnt(YFSEnvironment env, String SerialNo,
			String SecSerialNo, String IncidentNo, String IncidentYear,
			String Cache, String ItemID) throws Exception {
		logger.verbose("@@@@@ Entering NWCGGetSerialList::getTrackablecnt");
		Document trackableDoc = XMLUtil.createDocument("NWCGTrackableItem");
		Element elemTrackable = trackableDoc.getDocumentElement();
		Document outDoc = XMLUtil.newDocument();

		elemTrackable.setAttribute("SerialNo", SerialNo);
		elemTrackable.setAttribute("SecondarySerial", SecSerialNo);
		elemTrackable.setAttribute("SerialStatus",
				NWCGConstants.SERIAL_STATUS_ISSUED);
		// elemTrackable.setAttribute("StatusCacheID",Cache);
		elemTrackable.setAttribute("StatusIncidentNo", IncidentNo);
		elemTrackable.setAttribute("StatusIncidentYear", IncidentYear);
		elemTrackable.setAttribute("ItemID", ItemID);

		outDoc = CommonUtilities.invokeService(env,
				NWCGConstants.NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE,
				trackableDoc);

		Element elemTrackableListRoot = outDoc.getDocumentElement();
		NodeList TrackableList = elemTrackableListRoot
				.getElementsByTagName("NWCGTrackableItem");

		logger.verbose("@@@@@ Exiting NWCGGetSerialList::getTrackablecnt");
		return TrackableList.getLength();
	}

	public void setProperties(Properties prop) throws Exception {
		_properties = prop;
	}
}