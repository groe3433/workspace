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

package com.nwcg.icbs.yantra.api.item;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGValidateSupplierItem implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGValidateSupplierItem.class);

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
	}

	public Document validateSupplierItem(YFSEnvironment env, Document inXML)
			throws Exception {
		if (logger.isVerboseEnabled())
			logger.verbose("validateSupplierItem:: "
					+ XMLUtil.getXMLString(inXML));
		Element elemRoot = inXML.getDocumentElement();
		String strSupplierId = StringUtil.nonNull(elemRoot
				.getAttribute("SupplierID"));
		String strItemId = StringUtil.nonNull(elemRoot.getAttribute("ItemID"));
		String strProductClass = StringUtil.nonNull(elemRoot
				.getAttribute("ProductClass"));
		String strUnitOfMeasure = StringUtil.nonNull(elemRoot
				.getAttribute("UnitOfMeasure"));
		String strSupplierUnitOfMeasure = StringUtil.nonNull(elemRoot
				.getAttribute("SupplierUOM"));

		if (strSupplierId.equals("") || strItemId.equals("")
				|| strProductClass.equals("") || strUnitOfMeasure.equals("")) {
			throw new NWCGException("NWCG_SUPPLIER_ITEM_CREATE_003");
		}

		if (logger.isVerboseEnabled())
			logger.verbose("validateSupplierItem:: validateSupplier strSupplierId "
					+ strSupplierId);
		validateSupplier(env, strSupplierId);
		if (logger.isVerboseEnabled())
			logger.verbose("validateSupplierItem:: validateItem s  "
					+ strItemId + " strUnitOfMeasure " + strUnitOfMeasure
					+ " strProductClass " + strProductClass);
		validateItem(env, strItemId, strUnitOfMeasure, strProductClass,
				strSupplierUnitOfMeasure);

		return inXML;
	}

	private void validateItem(YFSEnvironment env, String strItemId,
			String strUOM, String strPC, String strSupplierUOM)
			throws Exception {
		Document doc = XMLUtil.createDocument("Item");
		Element elem = doc.getDocumentElement();
		elem.setAttribute("ItemID", strItemId);
		elem.setAttribute("UnitOfMeasure", strUOM);
		elem.setAttribute("SupplierUOM", strSupplierUOM);
		Element elemPrimaryInformation = doc
				.createElement("PrimaryInformation");
		elemPrimaryInformation.setAttribute("DefaultProductClass", strPC);
		elem.appendChild(elemPrimaryInformation);

		Document outDoc = CommonUtilities.invokeAPI(env,
				"NWCGValidateSupplierItem_getItemList", "getItemList", doc);
		if (logger.isVerboseEnabled())
			logger.verbose("outDoc ==> " + outDoc == null ? "NULL DOCUMENT"
					: XMLUtil.getXMLString(outDoc));
		NodeList nl = outDoc.getElementsByTagName("Item");
		if (nl != null && nl.getLength() <= 0) {
			throw new NWCGException("NWCG_SUPPLIER_ITEM_CREATE_002");
		}
		if (logger.isVerboseEnabled())
			logger.verbose("validateItem Item is VALID");
	}

	private void validateSupplier(YFSEnvironment env, String strSupplierId)
			throws Exception {
		Document doc = XMLUtil.createDocument("Organization");
		doc.getDocumentElement()
				.setAttribute("OrganizationCode", strSupplierId);

		Document outDoc = CommonUtilities.invokeAPI(env,
				"NWCGValidateSupplierItem_getOrganizationList",
				"getOrganizationList", doc);

		if (logger.isVerboseEnabled())
			logger.verbose("outDoc ==> " + outDoc == null ? "NULL DOCUMENT"
					: XMLUtil.getXMLString(outDoc));

		NodeList nl = outDoc.getElementsByTagName("OrgRole");

		boolean bSeller = false;
		if (nl != null) {
			for (int index = 0; index < nl.getLength(); index++) {
				Element elem = (Element) nl.item(index);
				String strRoleKey = StringUtil.nonNull(elem
						.getAttribute("RoleKey"));
				if (strRoleKey
						.equals(NWCGConstants.NWCG_ORGANIZATION_ROLE_KEY_SELLER)) {
					bSeller = true;
					break;
				}// end if
			}// end for
		}
		if (nl == null || nl.getLength() <= 0 || (!bSeller)) {
			throw new NWCGException("NWCG_SUPPLIER_ITEM_CREATE_001");
		}
		if (logger.isVerboseEnabled())
			logger.verbose("validateSupplier Supplier is VALID");
	}

}
