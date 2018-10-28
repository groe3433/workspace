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

package com.nwcg.icbs.yantra.api.trackableinventory;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;


/**
 * This class is used for posting the data in the custom trackable_item table
 * all te time it should create a new record. 
 * 
 * @author Oxford Consulting Group
 * @version 1.0
 * @date July 17, 2013
 */
public class NWCGProcessReceiveOrder implements YIFCustomApi, NWCGITrackableRecordMutator {

	private Properties _properties;

	private static Logger log = Logger.getLogger(NWCGProcessReceiveOrder.class.getName());
	
	/**
	 * 
	 * @param arg0
	 * @throws Exception
	 */
	public void setProperties(Properties arg0) throws Exception {
		this._properties = arg0;
	}

	/**
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 * @throws Exception
	 */
	public Document createTrackableRecord(YFSEnvironment env, Document inXML) throws Exception {
		System.out.println("@@@@@ Entering NWCGProcessReceiveOrder::createTrackableRecord @@@@@");
		try {
			System.out.println("@@@@@ Receive Order Input XML: " + XMLUtil.getXMLString(inXML));
			Element elemReceiptRoot = inXML.getDocumentElement();

			if (elemReceiptRoot != null) {
				// get the receipt line
				NodeList nlReceiptLines = elemReceiptRoot.getElementsByTagName("ReceiptLine");
				if (nlReceiptLines != null) {
					System.out.println("@@@@@ nlReceiptLines.getLength(): " + nlReceiptLines.getLength());

					String strOrderNo = "";
					String strOrderDate = "";
					String strReceiptDate = elemReceiptRoot.getAttribute("ReceiptDate");
					System.out.println("@@@@@ strReceiptDate: " + strReceiptDate);
					String strReceivngNode = elemReceiptRoot.getAttribute("ReceivingNode");
					System.out.println("@@@@@ strReceivngNode: " + strReceivngNode);

					// for all the receipt lines
					for (int index = 0; index < nlReceiptLines.getLength(); index++) {
						Element elemReceiptLine = (Element) nlReceiptLines.item(index);
						// check if the serial number is null
						strOrderNo = elemReceiptLine.getAttribute("OrderNo");
						String strSerialNo = elemReceiptLine.getAttribute("SerialNo");
						String strRevisionNo = elemReceiptLine.getAttribute("RevisionNo");
						if (StringUtil.isEmpty(strSerialNo)) {
							System.out.println("@@@@@ Continuing as the serial number is empty...");
							System.out.println("@@@@@ strSerialNo: " + strSerialNo);
							// if its null, continue with next record
							continue;
						}
						// get the order date from the order line instead of order header
						NodeList nlOrderLine = elemReceiptLine.getElementsByTagName("OrderLine");
						if (nlOrderLine != null && nlOrderLine.getLength() > 0) {
							// only one order line
							Element elemOL = (Element) nlOrderLine.item(0);
							strOrderDate = elemOL.getAttribute("ReqDeliveryDate");
						}
						System.out.println("@@@@@ JDBG:RCV strOrderNo = [" + strOrderNo + "]");
						System.out.println("@@@@@ JDBG:RCV strOrderDate = [" + strOrderDate + "]");
						System.out.println("@@@@@ Passing the element as " + XMLUtil.getElementXMLString(elemReceiptLine));
						
						Document docCreateTrackableInventoryIP = getTrackableInventoryIPDocument(env, elemReceiptLine, strReceivngNode);
						System.out.println("@@@@@ docCreateTrackableInventoryIP: " + XMLUtil.getXMLString(docCreateTrackableInventoryIP));

						if (docCreateTrackableInventoryIP != null) {
							Element elemCreateTrackableInventoryIP = docCreateTrackableInventoryIP.getDocumentElement();
							// assign the other attributes
							elemCreateTrackableInventoryIP.setAttribute("StatusCacheID", strReceivngNode);
							elemCreateTrackableInventoryIP.setAttribute("LastDocumentNo", strOrderNo);
							elemCreateTrackableInventoryIP.setAttribute("LastTransactionDate", strReceiptDate);
							elemCreateTrackableInventoryIP.setAttribute("RevisionNo", strRevisionNo);

							Element AcctElem = getOrgAcctCode(env, strReceivngNode);
							String AcctCode = AcctElem.getAttribute("ExtnRecvAcctCode");
							String OverrideCode = AcctElem.getAttribute("ExtnRAOverrideCode");
							String OwnerAgency = AcctElem.getAttribute("ExtnOwnerAgency");

							if (OwnerAgency.equals("BLM")) {
								elemCreateTrackableInventoryIP.setAttribute("BLMAccountCode", AcctCode);
							} else if (OwnerAgency.equals("FS")) {
								elemCreateTrackableInventoryIP.setAttribute("FSAccountCode", AcctCode);
								elemCreateTrackableInventoryIP.setAttribute("OverrideCode", OverrideCode);
							} else {
								elemCreateTrackableInventoryIP.setAttribute("OtherAccountCode", AcctCode);
							}

							try {
								// create the trackable item record - should
								// always succeed.... as we always receice a
								// unique serialNo
								// but just to make sure ...
								System.out.println("@@@@@ docCreateTrackableInventoryIP: " + XMLUtil.getXMLString(docCreateTrackableInventoryIP));
								CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_TRACKABLE_INVENTORY_SERVICE, docCreateTrackableInventoryIP);
							} catch (Exception ex) {
								System.out.println("!!!!! Got Exception while createing a record - serial number might already exists in system - trying to update a record..." + ex.getMessage());

								// should never come here
								// update the record - should always fail.... as
								// we won't receice the same serial twice
								// but just to make sure ...
								System.out.println("@@@@@ docCreateTrackableInventoryIP: " + XMLUtil.getXMLString(docCreateTrackableInventoryIP));
								CommonUtilities.invokeService(env, NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE, docCreateTrackableInventoryIP);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("!!!!! Got General Exception..." + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("@@@@@ Exiting NWCGProcessReceiveOrder::createTrackableRecord @@@@@");
		return inXML;
	}

	/**
	 * This method generates the input xml for creating the record. 
	 * 
	 * @param env
	 * @param elemReceiptLine
	 * @param ReceivingNode
	 * @return
	 * @throws Exception
	 */
	private Document getTrackableInventoryIPDocument(YFSEnvironment env, Element elemReceiptLine, String ReceivingNode) throws Exception {
		System.out.println("@@@@@ Entering NWCGProcessReceiveOrder::getTrackableInventoryIPDocument @@@@@");
		
		Document returnDoc = XMLUtil.createDocument("NWCGTrackableItem");
		Element returnDocElem = returnDoc.getDocumentElement();

		String strRecvCost = "";
		NodeList nlExtn = elemReceiptLine.getElementsByTagName("Extn");
		{
			if (nlExtn != null) {
				Element elemExtn = (Element) nlExtn.item(0);
				strRecvCost = elemExtn.getAttribute("ExtnReceivingPrice");
			}
		}
		System.out.println("@@@@@ strRecvCost [" + strRecvCost + "]");
		
		String strItemID = StringUtil.nonNull(elemReceiptLine.getAttribute("ItemID"));
		String strUOM = StringUtil.nonNull(elemReceiptLine.getAttribute("UnitOfMeasure"));
		String strSerialNo = StringUtil.nonNull(elemReceiptLine.getAttribute("SerialNo"));
		
		returnDocElem.setAttribute("SerialNo", strSerialNo);
		returnDocElem.setAttribute("ItemID", strItemID);
		returnDocElem.setAttribute("UnitOfMeasure", strUOM);
		returnDocElem.setAttribute("ItemShortDescription", CommonUtilities.getItemDescription(env, strItemID, strUOM));
		returnDocElem.setAttribute("SecondarySerial", CommonUtilities.getSecondarySerial(env, strSerialNo, strItemID));
		returnDocElem.setAttribute("LotAttribute1", StringUtil.nonNull(elemReceiptLine.getAttribute("LotAttribute1")));
		returnDocElem.setAttribute("LotAttribute3", StringUtil.nonNull(elemReceiptLine.getAttribute("LotAttribute3")));
		returnDocElem.setAttribute("ManufacturingDate", StringUtil.nonNull(elemReceiptLine.getAttribute("ManufacturingDate")));
		returnDocElem.setAttribute("SerialStatus", NWCGConstants.SERIAL_STATUS_AVAILABLE);
		returnDocElem.setAttribute("SerialStatusDesc", NWCGConstants.SERIAL_STATUS_AVAILABLE_DESC);
		returnDocElem.setAttribute("Type", NWCGConstants.TRANSACTION_INFO_TYPE_RECEIPT);
		returnDocElem.setAttribute("AcquisitionCost", strRecvCost);
		
		// get the items owner unit id fire getItemList api and get the extended attribute
		String strOwnerUnitID = StringUtil.nonNull(elemReceiptLine.getAttribute("LotAttribute2"));

		if (strOwnerUnitID.length() == 0 || strOwnerUnitID.equals(" ")) {
			strOwnerUnitID = ReceivingNode;
		}
		String strOwnerUnitName = CommonUtilities.getOwnerUnitIDName(env, strOwnerUnitID);

		returnDocElem.setAttribute("OwnerUnitID", strOwnerUnitID);
		returnDocElem.setAttribute("OwnerUnitName", StringUtil.nonNull(strOwnerUnitName));

		System.out.println("@@@@@ returnDoc: " + XMLUtil.getXMLString(returnDoc));
		System.out.println("@@@@@ Exiting NWCGProcessReceiveOrder::getTrackableInventoryIPDocument @@@@@");
		return returnDoc;
	}

	/**
	 * 
	 */
	public Document insertOrUpdateTrackableRecord(YFSEnvironment env, Document doc) throws Exception {
		System.out.println("@@@@@ In NWCGProcessReceiveOrder::insertOrUpdateTrackableRecord @@@@@");
		return createTrackableRecord(env, doc);
	}

	/**
	 * 
	 * @param env
	 * @param Cache
	 * @return
	 * @throws Exception
	 */
	public Element getOrgAcctCode(YFSEnvironment env, String Cache) throws Exception {
		System.out.println("@@@@@ Entering NWCGProcessReceiveOrder::getOrgAcctCode @@@@@");
		
		Document inDoc = XMLUtil.newDocument();
		Document outDoc = XMLUtil.newDocument();

		Element el_NWCGOrganization = inDoc.createElement("Organization");
		inDoc.appendChild(el_NWCGOrganization);
		el_NWCGOrganization.setAttribute("OrganizationCode", Cache);

		try {
			System.out.println("@@@@@ inDoc: " + XMLUtil.getXMLString(inDoc));
			outDoc = CommonUtilities.invokeAPI(env, "NWCGProcessBillingTrans_getOrganizationList", "getOrganizationList", inDoc);
			System.out.println("@@@@@ outDoc: " + XMLUtil.getXMLString(outDoc));
		} catch (Exception e) {
			System.out.println("!!!!! Caught Exception: " + e.getMessage());
			e.printStackTrace();

		}
		Element outDocElem = outDoc.getDocumentElement();
		Element ExtnElm = (Element) outDocElem.getElementsByTagName("Extn").item(0);

		System.out.println("@@@@@ Exiting NWCGProcessReceiveOrder::getOrgAcctCode @@@@@");
		return ExtnElm;
	}
}