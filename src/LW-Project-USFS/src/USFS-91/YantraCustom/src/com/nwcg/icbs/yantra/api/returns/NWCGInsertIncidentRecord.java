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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGInsertIncidentRecord implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGInsertIncidentRecord.class);
	
	int shipmentLineCount = 0;
	int ShipmentTagSerialCount = 0;
	List serialList = new ArrayList();

	private Properties props = null;

	public void setProperties(Properties props) throws Exception {
		this.props = props;
	}

	public Document insertRecord(YFSEnvironment env, Document inXML) throws Exception {
		logger.verbose("@@@@@ Entering com.nwcg.icbs.yantra.api.returns.NWCGInsertIncidentRecord::insertRecord @@@@@");
		logger.verbose("@@@@@ inXML :: " + XMLUtil.getXMLString(inXML));
		if (null == env) {
			logger.error("!!!!! YFSEnvironment is null");
			throw new NWCGException("NWCG_RETURN_ENV_IS_NULL");
		} if (null == inXML) {
			logger.error("!!!!! Input Document is null");
			throw new NWCGException("NWCG_RETURN_INPUT_DOC_NULL");
		}
		String issueDate = inXML.getDocumentElement().getAttribute("ActualShipmentDate");
		NodeList ShipmentLines = inXML.getDocumentElement().getElementsByTagName("ShipmentLine");
		shipmentLineCount = ShipmentLines.getLength();
		if (shipmentLineCount > 0) {
			for (int cnt = 0; cnt < shipmentLineCount; cnt++) {
				Element shipmentLineEl = (Element) ShipmentLines.item(cnt);
				Element ShipmentTagSerialsEL = null;
				if (XPathUtil.getNode(shipmentLineEl, "./ShipmentTagSerials/ShipmentTagSerial") != null)
					ShipmentTagSerialsEL = (Element) XPathUtil.getNode(shipmentLineEl, "./ShipmentTagSerials/ShipmentTagSerial");
				String Serial = "";
				if (ShipmentTagSerialsEL != null)
					Serial = ShipmentTagSerialsEL.getAttribute("SerialNo");
				if (Serial != null && !(Serial.equals(""))) {
					NodeList ShipmentTagSerial = XPathUtil.getNodeList(shipmentLineEl, "./ShipmentTagSerials/ShipmentTagSerial");
					ShipmentTagSerialCount = ShipmentTagSerial.getLength();
					serialList = new ArrayList();
					for (int cnt2 = 0; cnt2 < ShipmentTagSerialCount; cnt2++) {
						Element ShipmentTagSerialEl = (Element) ShipmentTagSerial.item(cnt2);
						if (ShipmentTagSerialEl.getAttribute("SerialNo") != null) {
							serialList.add(ShipmentTagSerialEl.getAttribute("SerialNo").toString());
						} else {
						}
					}
				}
				String IncidentNo = shipmentLineEl.getAttribute("IncidentNo");
				String IncidentYear = shipmentLineEl.getAttribute("IncidentYear");
				String Cache = shipmentLineEl.getAttribute("CacheID");
				String ItemID = shipmentLineEl.getAttribute("ItemID");
				String Price = shipmentLineEl.getAttribute("UnitPrice");
				String issueQTY = shipmentLineEl.getAttribute("ActualQuantity");
				String orderNo = shipmentLineEl.getAttribute("OrderNo");
				if (serialList.isEmpty() || Serial.equals("")) {
					Document outDoc = queryTable(env, IncidentNo, IncidentYear, Cache, ItemID, Price);
					Element NWCGIncidentReturn = (Element) XPathUtil.getNode(outDoc.getDocumentElement(), "./NWCGIncidentReturn");
					if (NWCGIncidentReturn == null) {
						makeEntry(env, Cache, IncidentNo, IncidentYear, ItemID, Price, issueDate, issueQTY, "", orderNo);
					} else {
						Element NWCGIncidentReturnEl = (Element) outDoc.getDocumentElement().getFirstChild();
						String QuantityShipped = NWCGIncidentReturnEl.getAttribute("QuantityShipped");
						String IncidentReturnKey = NWCGIncidentReturnEl.getAttribute("IncidentReturnKey");
						double finalShippedQty = Double.parseDouble(QuantityShipped) + Double.parseDouble(issueQTY);
						updateEntry(env, IncidentReturnKey, IncidentNo, IncidentYear, Cache, ItemID, Double.toString(finalShippedQty), Price, orderNo);
					}
				} else {
					Iterator i = serialList.iterator();
					while (i.hasNext()) {
						String SerialNo = (String) i.next();
						makeEntry(env, Cache, IncidentNo, IncidentYear, ItemID, Price, issueDate, "1", SerialNo, orderNo);
					}
				}
			}
		} else {
			throw new NWCGException("NWCG_RETURN_INPUT_XML_NO_RECORDS_TO_PROCESS");
		}
		logger.verbose("@@@@@ Exiting com.nwcg.icbs.yantra.api.returns.NWCGInsertIncidentRecord::insertRecord @@@@@");
		return inXML;
	}

	// This method is called only if the qty is to be updated. This is the case where the item is shipped for same price ie. itemID-Cache-IncidentNo is same
	public void updateEntry(YFSEnvironment env, String IncidentReturnKey, String Cache, String IncidentNo, String IncidentYear, String ItemID, String issueQTY, String Price, String IssueNo) throws Exception {
		logger.verbose("@@@@@ Entering com.nwcg.icbs.yantra.api.returns.NWCGInsertIncidentRecord::updateEntry @@@@@");
		Document inDoc = XMLUtil.newDocument();
		Element el_NWCGIncidentReturn = inDoc.createElement("NWCGIncidentReturn");
		inDoc.appendChild(el_NWCGIncidentReturn);
		el_NWCGIncidentReturn.setAttribute("IncidentReturnKey", IncidentReturnKey);
		el_NWCGIncidentReturn.setAttribute("QuantityShipped", issueQTY);
		el_NWCGIncidentReturn.setAttribute("IssueNo", IssueNo);
		CommonUtilities.invokeService(env, NWCGConstants.NWCG_MODIFY_REC_IN_INCIDENT_TABLE_SERVICE, inDoc);
		logger.verbose("@@@@@ Exiting com.nwcg.icbs.yantra.api.returns.NWCGInsertIncidentRecord::updateEntry @@@@@");
	}

	// This method is called if the item is shiped for different price of if the itemID-cache-IncidentNo does not exist in the table
	public void makeEntry(YFSEnvironment env, String Cache, String IncidentNo, String IncidentYear, String ItemID, String Price, String issueDate, String issueQTY, String SerialNo, String IssueNo) throws Exception {
		logger.verbose("@@@@@ Entering com.nwcg.icbs.yantra.api.returns.NWCGInsertIncidentRecord::makeEntry @@@@@");
		Document inDoc = XMLUtil.newDocument();
		Element el_NWCGIncidentReturn = inDoc.createElement("NWCGIncidentReturn");
		inDoc.appendChild(el_NWCGIncidentReturn);
		el_NWCGIncidentReturn.setAttribute("CacheID", Cache);
		el_NWCGIncidentReturn.setAttribute("DateIssued", issueDate);
		el_NWCGIncidentReturn.setAttribute("IncidentNo", IncidentNo);
		el_NWCGIncidentReturn.setAttribute("IncidentYear", IncidentYear);
		el_NWCGIncidentReturn.setAttribute("ItemID", ItemID);
		el_NWCGIncidentReturn.setAttribute("QuantityShipped", issueQTY);
		el_NWCGIncidentReturn.setAttribute("TrackableID", SerialNo);
		el_NWCGIncidentReturn.setAttribute("UnitPrice", Price);
		el_NWCGIncidentReturn.setAttribute("IssueNo", IssueNo);
		CommonUtilities.invokeService(env, NWCGConstants.NWCG_INSERT_REC_IN_INCIDENT_TABLE_SERVICE, inDoc);
		logger.verbose("@@@@@ Exiting com.nwcg.icbs.yantra.api.returns.NWCGInsertIncidentRecord::makeEntry @@@@@");
	}
	
	public Document queryTable(YFSEnvironment env, String IncidentNo, String IncidentYear, String Cache, String ItemID, String Price) throws Exception {
		logger.verbose("@@@@@ Entering com.nwcg.icbs.yantra.api.returns.NWCGInsertIncidentRecord::queryTable @@@@@");
		Document inDoc = XMLUtil.newDocument();
		Document outDoc = XMLUtil.newDocument();
		Element el_NWCGIncidentReturn = inDoc.createElement("NWCGIncidentReturn");
		inDoc.appendChild(el_NWCGIncidentReturn);
		el_NWCGIncidentReturn.setAttribute("CacheID", Cache);
		el_NWCGIncidentReturn.setAttribute("IncidentNo", IncidentNo);
		el_NWCGIncidentReturn.setAttribute("IncidentYear", IncidentYear);
		el_NWCGIncidentReturn.setAttribute("ItemID", ItemID);
		el_NWCGIncidentReturn.setAttribute("UnitPrice", Price);
		el_NWCGIncidentReturn.setAttribute("ReceivedAsComponent", "N");
		el_NWCGIncidentReturn.setAttribute("OverReceipt", "N");
		outDoc = CommonUtilities.invokeService(env, NWCGConstants.NWCG_QUERY_LIST_ON_INCIDENT_TABLE_SERVICE, inDoc);
		logger.verbose("@@@@@ Exiting com.nwcg.icbs.yantra.api.returns.NWCGInsertIncidentRecord::queryTable @@@@@");
		return outDoc;
	}
}