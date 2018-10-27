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
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGReturnItemlist implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGReturnItemlist.class);
	
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
	}

	public Document prepareList(YFSEnvironment env, Document inXML)
			throws Exception {
		Document outDoc = XMLUtil.createDocument("ReturnItemList");
		Element eleOutDoc = outDoc.getDocumentElement();
		NodeList ReceiptLineNL = inXML.getElementsByTagName("ReceiptLine");
		int recNLLen = ReceiptLineNL.getLength();
		for (int i = 0; i < recNLLen; i++) {
			String strItemID = "";
			String strQuant = "";
			String strLPN = "";
			String strKey = "";
			Element eleReceiptLine = (Element) ReceiptLineNL.item(i);

			if (eleReceiptLine.hasAttribute("PrimarySerialNo")) {
				strItemID = eleReceiptLine.getAttribute("PrimarySerialNo");
				strQuant = "1";
			} else {
				strItemID = eleReceiptLine.getAttribute("ItemID");
				strQuant = eleReceiptLine.getAttribute("QtyReturned");
			}
			if (eleReceiptLine.hasAttribute("LPNNo")) {
				strLPN = eleReceiptLine.getAttribute("LPNNo");
			}
			strKey = eleReceiptLine.getAttribute("value");
			Element eleItem = outDoc.createElement("Item");
			eleItem.setAttribute("ItemID", strItemID);
			eleItem.setAttribute("Quant", strQuant);
			eleItem.setAttribute("LPNNo", strLPN);
			eleItem.setAttribute("Key", strKey);
			eleOutDoc.appendChild(eleItem);
		}
		return outDoc;

	}

}