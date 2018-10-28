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

package com.nwcg.icbs.yantra.api.common;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.api.billingtransaction.NWCGProcessBillingTransRefurb;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * 
 * @author Oxford Consulting Group
 * @version 1.0
 * @date November 6, 2013
 */
public class NWCGInsertNWCGUSERID implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGInsertNWCGUSERID.class);
	
	Properties apiProperties = null;

	/**
	 * 
	 * @param apiProperties
	 * @throws Exception
	 */
	public void setProperties(Properties apiProperties) throws Exception {
		this.apiProperties = apiProperties;
	}

	/**
	 * 
	 * @param env
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public Document insertNWCGUSERID(YFSEnvironment env, Document document) throws Exception {

		Element root = document.getDocumentElement();
		String userId = env.getUserId();
		if (userId.equalsIgnoreCase("NWCGOrderStatusMonitorAgent")) {
			userId = NWCGAAConstants.ENV_USER_ID;
		}
		root.setAttribute("NWCGUSERID", userId);

		setDispatchUnitSufficPrefix(env, document);

		return document;
	}

	/**
	 * 
	 * @param env
	 * @param doc
	 */
	private void setDispatchUnitSufficPrefix(YFSEnvironment env, Document doc) {

		boolean prefix = false;
		boolean suffix = false;
		String prefixStr = "";
		String suffixStr = "";
		String strMessageName = doc.getDocumentElement().getAttribute("messageName");
		if (strMessageName == null) {
			strMessageName = "";
		}

		try {
			if (strMessageName.equals("CreateCatalogItemReq") || strMessageName.equals("DeleteCatalogItemReq") || strMessageName.equals("UpdateCatalogItemReq")) {
				//Get common code info for Dispatch ID
				Document ipXML = XMLUtil.getDocument("<CommonCode CodeType=\"" + NWCGAAConstants.CODE_TYPE + "\" />");
				Document oputXML = CommonUtilities.invokeAPI(env, NWCGAAConstants.API_COMMONCODELIST, ipXML);
				NodeList nl = oputXML.getDocumentElement().getElementsByTagName("CommonCode");

				for (int count = 0; count < nl.getLength(); count++) {
					Element commonCodeElem = (Element) nl.item(count);
					String codeValue = commonCodeElem.getAttribute("CodeValue");
					String codeShortDesc = commonCodeElem.getAttribute("CodeShortDescription");
					if (codeValue.equals(NWCGAAConstants.DISPATCH_PREFIX)) {
						if (codeShortDesc.equals("CO") || codeShortDesc.equalsIgnoreCase("SOAPFault")) {
							prefix = true;
							prefixStr = commonCodeElem.getAttribute("CodeShortDescription");
						}
					} else if (codeValue.equals(NWCGAAConstants.DISPATCH_SUFFIX) && codeShortDesc.equals("RMK")) {
						suffix = true;
						suffixStr = commonCodeElem.getAttribute("CodeShortDescription");
					}
				}
				nl = doc.getElementsByTagName("UnitIDPrefix");
				int last = nl.getLength();

				for (int counter = 0; counter < last; counter++) {
					Element elem = (Element) nl.item(counter);
					String str = elem.getTextContent();
					if (str != null && str.equals("SETVALUE"))
						;
					{
						if (prefix)
							elem.setTextContent(prefixStr);
					}
				}
				nl = doc.getElementsByTagName("UnitIDSuffix");
				last = nl.getLength();

				for (int counter = 0; counter < last; counter++) {
					Element elem = (Element) nl.item(counter);
					String str = elem.getTextContent();
					if (str != null && str.equals("SETVALUE"))
						;
					{
						if (suffix)
							elem.setTextContent(suffixStr);
					}
				}
			}
		} catch (Exception e) {
			//logger.printStackTrace(e);
		}
	}
}