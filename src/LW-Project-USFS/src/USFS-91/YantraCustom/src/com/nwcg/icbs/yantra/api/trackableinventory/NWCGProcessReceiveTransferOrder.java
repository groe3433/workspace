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

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/*
 * author jvishwakarma
 * this class is used for posting the data in the custom trackable_item table
 * its just updates the statuscacheid - to record that transfer to transfer order
 * is received by the node. Invoked only for cache to cache transfer
 */
public class NWCGProcessReceiveTransferOrder implements YIFCustomApi,
		NWCGITrackableRecordMutator {
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGProcessReceiveTransferOrder.class);
	
	private Properties _properties;

	public void setProperties(Properties arg0) throws Exception {
		this._properties = arg0;
		// TODO Auto-generated method stub

	}

	public Document updateTrackableRecord(YFSEnvironment env, Document inXML)
			throws Exception {
		try {
			Element elemReceiptRoot = inXML.getDocumentElement();
			if (logger.isVerboseEnabled())
				logger.verbose("NWCGProcessReceiveOrder::createTrackableRecord received the input document "
						+ XMLUtil.getXMLString(inXML));
			if (elemReceiptRoot != null) {
				// get the receipt line
				NodeList nlReceiptLines = elemReceiptRoot
						.getElementsByTagName("ReceiptLine");
				if (nlReceiptLines != null) {
					if (logger.isVerboseEnabled())
						logger.verbose("NWCGProcessReceiveOrder:: total number of receiptlines = "
								+ nlReceiptLines.getLength());

					String strReceivngNode = elemReceiptRoot
							.getAttribute("ReceivingNode");

					// for all the receipt lines
					for (int index = 0; index < nlReceiptLines.getLength(); index++) {
						Element elemReceiptLine = (Element) nlReceiptLines
								.item(index);
						// check if the serial number is null
						String strSerialNo = elemReceiptLine
								.getAttribute("SerialNo");
						String strItemID = elemReceiptLine
								.getAttribute("ItemID");
						if (StringUtil.isEmpty(strSerialNo)) {
							if (logger.isVerboseEnabled())
								logger.verbose("NWCGProcessReceiveOrder:: Continuing as the serial number is empty");
							// if its null, continue with next record
							continue;
						}

						if (logger.isVerboseEnabled())
							logger.verbose("Passing the element as "
									+ elemReceiptLine);
						Document docCreateTrackableInventoryIP = getTrackableInventoryIPDocument(
								env, elemReceiptLine);

						if (docCreateTrackableInventoryIP != null) {
							if (logger.isVerboseEnabled())
								logger.verbose("NWCGProcessReceiveOrder:: got the trackable inv doc as "
										+ XMLUtil
												.getXMLString(docCreateTrackableInventoryIP));

							Element elemCreateTrackableInventoryIP = docCreateTrackableInventoryIP
									.getDocumentElement();
							// assign the other attributes
							elemCreateTrackableInventoryIP.setAttribute(
									"StatusCacheID", strReceivngNode);
							elemCreateTrackableInventoryIP.setAttribute(
									"SecondarySerial", CommonUtilities
											.getSecondarySerial(env,
													strSerialNo, strItemID));
							try {
								// update the record
								CommonUtilities
										.invokeService(
												env,
												NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE,
												docCreateTrackableInventoryIP);
							} catch (Exception ex) {
								logger.error("!!!!! Got Exception while creating a record - serial number might already exists in system - trying to update a record "
											+ ex.getMessage(),ex);

							}// end catch
						}// end if docCreateTrackableInventoryIP != null
					}// end while recipt lines
				}// end if receiptlines not null

			}
		} catch (Exception e) {
			logger.error("!!!!! NWCGProcessReceiveOrder::createTrackableRecord Caught Exception ",e);
		}

		return inXML;
	}

	/*
	 * This method generates the input xml for creating the record
	 */
	private Document getTrackableInventoryIPDocument(YFSEnvironment env,
			Element elemReceiptLine) throws Exception {
		if (logger.isVerboseEnabled())
			logger.verbose("getTrackableInventoryIPDocument::inXML ==> "
					+ elemReceiptLine);
		Document returnDoc = XMLUtil.createDocument("NWCGTrackableItem");
		Element returnDocElem = returnDoc.getDocumentElement();
		if (logger.isVerboseEnabled())
			logger.verbose("Serial No is == >> "
					+ elemReceiptLine.getAttribute("SerialNo"));
		returnDocElem.setAttribute("SerialNo",
				StringUtil.nonNull(elemReceiptLine.getAttribute("SerialNo")));
		if (logger.isVerboseEnabled())
			logger.verbose("getTrackableInventoryIPDocument :: returning "
					+ XMLUtil.getXMLString(returnDoc));
		return returnDoc;
	}

	public Document insertOrUpdateTrackableRecord(YFSEnvironment env,
			Document doc) throws Exception {
		// TODO Auto-generated method stub
		return updateTrackableRecord(env, doc);
	}
}
