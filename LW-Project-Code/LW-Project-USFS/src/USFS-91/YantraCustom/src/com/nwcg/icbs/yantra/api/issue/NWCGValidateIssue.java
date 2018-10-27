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

package com.nwcg.icbs.yantra.api.issue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGValidateIssue implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGValidateIssue.class);

	public void setProperties(Properties props) throws Exception {
	}

	/**
	 * 
	 * @param env
	 * @param inDoc
	 * @return
	 * @throws Exception
	 */
	public Document validateIssueLine(YFSEnvironment env, Document inDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGValidateIssue.validateIssueLine @@@@@");
		logger.verbose("@@@@@ Input XML : " + XMLUtil.getXMLString(inDoc));
		
		// build the create order
		NodeList inpOlList = inDoc.getDocumentElement().getElementsByTagName("OrderLine");
		logger.verbose("@@@@@ order line list " + inpOlList.getLength());
		int iLength = inpOlList.getLength();
		ArrayList lst = new ArrayList();
		for (int i = 0; i < iLength; i++) {
			logger.verbose("@@@@@ Counter " + i);
			Element ol = (Element) inpOlList.item(i);
			String olKey = ol.getAttribute("OrderLineKey");
			logger.verbose("@@@@@ got order line key " + olKey);
			// if order line key is blank, check for item id and pc
			if (olKey == null || olKey.equals("")) {
				NodeList itemList = ol.getElementsByTagName("Item");
				// no item tag and no order line key remove this element
				if (itemList.getLength() == 0) {
					logger.verbose("@@@@@ removing the node as no order line key and no item tags " + ol);
					lst.add(ol);
					continue;
				} else {
					logger.verbose("@@@@@ got item list " + itemList.getLength());
					for (int j = 0; j < itemList.getLength(); j++) {
						Element item = (Element) itemList.item(j);
						String itemId = item.getAttribute("ItemID");
						String pc = item.getAttribute("ProductClass");
						logger.verbose("@@@@@ itemId " + itemId + " pc " + pc);
						if (itemId == null || itemId.equals("")) {
							if (pc == null || pc.equals("")) {
								// remove this row as this doesnt have the order line key nor item id not product class
								logger.verbose("@@@@@ removing " + ol);
								lst.add(ol);
							}
						}
					}
				}
			}
		}
		Iterator itr = lst.iterator();
		while (itr.hasNext()) {
			Element ele = (Element) itr.next();
			ele.getParentNode().removeChild(ele);
			itr.remove();
		}

		logger.verbose("@@@@@ returning " + XMLUtil.getXMLString(inDoc));
		logger.verbose("@@@@@ Exiting NWCGValidateIssue.validateIssueLine @@@@@");
		return inDoc;
	}
}