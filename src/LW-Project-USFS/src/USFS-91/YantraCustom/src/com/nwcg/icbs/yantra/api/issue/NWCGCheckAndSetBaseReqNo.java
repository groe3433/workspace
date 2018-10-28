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

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGCheckAndSetBaseReqNo implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGCheckAndSetBaseReqNo.class);

	public void setProperties(Properties arg0) throws Exception {
	}
	
	/**
	 * 
	 * @param env
	 * @param docIP
	 * @return
	 * @throws Exception
	 */
	public Document checkAndSetBaseReqNo(YFSEnvironment env, Document docIP) throws Exception {
		logger.verbose("@@@@@ Entering NWCGCheckAndSetBaseReqNo.checkAndSetBaseReqNo @@@@@");
		logger.verbose("@@@@@ Input XML : " + XMLUtil.extractStringFromDocument(docIP));
		NodeList nlOL = docIP.getDocumentElement().getElementsByTagName("OrderLine");
		// If the issue doesn't have any lines, then return the original order xml. Some attributes might have been changed at the Order level
		if (nlOL == null || nlOL.getLength() < 1){
			return docIP;
		}
		for (int i=0; i < nlOL.getLength(); i++){
			Element elmOL = (Element) nlOL.item(i);
			String orderLineKey = elmOL.getAttribute(NWCGConstants.ORDER_LINE_KEY);
			if (orderLineKey == null || orderLineKey.length() < 2){
				// Order Line is not created yet. Set the base request no
				Element elmExtn = (Element) elmOL.getElementsByTagName("Extn").item(0);
				elmExtn.setAttribute(NWCGConstants.EXTN_BASE_REQUEST_NO, elmExtn.getAttribute(NWCGConstants.EXTN_REQUEST_NO));
			}
		}
		logger.verbose("@@@@@ Input XML after setting base request no : " + XMLUtil.getXMLString(docIP));
		logger.verbose("@@@@@ Exiting NWCGCheckAndSetBaseReqNo.checkAndSetBaseReqNo @@@@@");
		return docIP;
	}
}