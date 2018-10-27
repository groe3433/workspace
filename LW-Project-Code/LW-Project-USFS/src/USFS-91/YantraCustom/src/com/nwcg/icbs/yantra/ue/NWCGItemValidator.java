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

package com.nwcg.icbs.yantra.ue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;

public class NWCGItemValidator {
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGItemValidator.class);

	static public void validateItemDescription(YFSEnvironment env,
			String strShortDesc, String strItemID) throws YFSUserExitException {

		Document inputXMLDoc = null;

		try {
			inputXMLDoc = XMLUtil.createDocument("Item");
		} catch (javax.xml.parsers.ParserConfigurationException e) {
			//logger.printStackTrace(e);
		}

		Element eleRoot = inputXMLDoc.getDocumentElement();
		Element elePrimaryInformation = inputXMLDoc
				.createElement("PrimaryInformation");
		elePrimaryInformation.setAttribute("ShortDescription", strShortDesc);
		eleRoot.appendChild(elePrimaryInformation);

		Document docItemList = null;
		try {

			docItemList = CommonUtilities.invokeAPI(env,
					NWCGConstants.API_GET_ITEM_LIST, inputXMLDoc);

		} catch (Exception e) {
			//logger.printStackTrace(e);
		}

		// Element eleItemListRoot = docItemList.getDocumentElement();
		NodeList nlItem = docItemList.getElementsByTagName("Item");

		if (nlItem != null && nlItem.getLength() > 1) {
			if (logger.isVerboseEnabled())
				logger.verbose("nlItem.getLength is: " + nlItem.getLength());
			if (logger.isVerboseEnabled())
				logger.verbose("strShortDesc is: " + strShortDesc);
			if (logger.isVerboseEnabled())
				logger.verbose("strItemID is: " + strItemID);

			throw new YFSUserExitException(
					"Please enter a unique Short Description.");
		} else if (nlItem != null && nlItem.getLength() == 1) {

			Element eleItemReturned = (Element) nlItem.item(0);
			String strItemIDReturned = eleItemReturned.getAttribute("ItemID");
			if (logger.isVerboseEnabled())
				logger.verbose("strItemID : " + strItemID);
			if (logger.isVerboseEnabled())
				logger.verbose("strItemIDReturned: " + strItemIDReturned);
			if (!(strItemIDReturned.equals(strItemID))) {
				throw new YFSUserExitException(
						"Please enter a unique Short Description.  Your description matches ItemID: "
								+ strItemIDReturned);
			}
		}

	}

}