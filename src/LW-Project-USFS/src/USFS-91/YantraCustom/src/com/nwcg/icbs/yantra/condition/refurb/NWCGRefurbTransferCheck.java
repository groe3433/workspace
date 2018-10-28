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

package com.nwcg.icbs.yantra.condition.refurb;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.condition.incident.NWCGCheckNegativeResponse;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGRefurbTransferCheck implements YCPDynamicConditionEx {
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGRefurbTransferCheck.class);
	
	public boolean evaluateCondition(YFSEnvironment env, String name,
			Map mapData, Document inDoc) {

		boolean result = false;
		String orderType = null;
		String documentType = null;

		if (mapData != null && mapData.size() > 0) {
			// mapData may not have DocumentType
			Object docType = mapData.get(NWCGConstants.DOCUMENT_TYPE);
			documentType = (docType != null) ? docType.toString()
					: documentType;
		}

		Element rootElement = inDoc.getDocumentElement();
		documentType = (documentType != null) ? documentType : rootElement
				.getAttribute("DocumentType");

		// Begin CR383 01102013
		// PurchaseOrder does not have orderType attribute
		if (!documentType.equals("0006")) {
			return true;
		}
		// End CR383 01102013

		// Cache-To-Cache Transfer Order has orderType attribute
		if (rootElement.getNodeName().equalsIgnoreCase("Shipment")) {
			orderType = rootElement.getAttribute("OrderType");
		} else if (rootElement.getNodeName().equalsIgnoreCase("Receipt")) {
			NodeList receiptLineList = rootElement
					.getElementsByTagName("ReceiptLine");
			if (receiptLineList != null && receiptLineList.getLength() > 0) {
				Element receiptLineFirstElement = (Element) receiptLineList
						.item(0);
				orderType = ((Element) receiptLineFirstElement
						.getElementsByTagName("Order").item(0))
						.getAttribute("OrderType");
			}
		}

		result = ((documentType != null && documentType.equals("0006")) && (orderType != null && orderType
				.equals(NWCGConstants.ORDER_TYPE_REFURB_TRANSFER))) ? true
				: false;
		return result;
	}

	public void setProperties(Map map) {
	}
}