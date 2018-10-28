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
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGBreakLPN implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGBreakLPN.class);

	public void setProperties(Properties arg0) throws Exception {
	}

	public Document breakLPN(YFSEnvironment env, Document inputXML)
			throws Exception {
		logger.verbose("@@@@@ Entering NWCGBreakLPN::breakLPN");
		Document inDoc = inputXML;
		try {
			Element eleTasK = inDoc.getDocumentElement();
			String strEnterpriseKey = eleTasK.getAttribute("EnterpriseKey");
			String strNode = eleTasK.getAttribute("Node");

			String strSourceLocationID = eleTasK
					.getAttribute("SourceLocationId");
			String strTargetLocationId = eleTasK
					.getAttribute("TargetLocationId");
			Element Inventory = (Element) XMLUtil.getChildNodeByName(eleTasK,
					"Inventory");
			String strItemID = Inventory.getAttribute("ItemId");
			String strPalletID = Inventory.getAttribute("SourcePalletId");
			if (!strSourceLocationID.equals(strTargetLocationId)
					&& strItemID.equals("") && !strPalletID.equals("")) {
				String sLocationId = "";
				Document docGetLPNDetails = XMLUtil
						.createDocument("GetLPNDetails");
				Element eleRootGetLPNDetails = docGetLPNDetails
						.getDocumentElement();
				eleRootGetLPNDetails.setAttribute("Node", strNode);
				eleRootGetLPNDetails.setAttribute("EnterpriseCode",
						strEnterpriseKey);
				Element lpnElement = docGetLPNDetails.createElement("LPN");
				eleRootGetLPNDetails.appendChild(lpnElement);
				lpnElement.setAttribute("PalletId", strPalletID);
				logger.verbose("@@@@@ getLPNDetails API :: "
						+ XMLUtil.extractStringFromDocument(docGetLPNDetails));
				Document outDocLPN = CommonUtilities.invokeAPI(env,
						"getLPNDetails", docGetLPNDetails);
				if (outDocLPN != null) {
					Element lpnDetailsElem = outDocLPN.getDocumentElement();
					Element lpnElem = (Element) (lpnDetailsElem
							.getElementsByTagName("LPN").item(0));
					if (lpnElem != null) {
						Element lpnLocElem = (Element) XMLUtil
								.getChildNodeByName(lpnElem, "LPNLocation");
						if (lpnLocElem != null) {
							sLocationId = lpnLocElem.getAttribute("LocationId");
						}
						Element itemInvListElem = (Element) XMLUtil
								.getChildNodeByName(lpnElem,
										"ItemInventoryDetailList");
						if (itemInvListElem != null) {
							NodeList itemInvList = itemInvListElem
									.getElementsByTagName("ItemInventoryDetail");
							for (int m = 0; m < itemInvList.getLength(); m++) {
								Element itemInvElem = (Element) itemInvList
										.item(m);
								String strInvStatus = itemInvElem
										.getAttribute("InventoryStatus");

								Document mvDoc = XMLUtil
										.createDocument("MoveLocationInventory");
								Element eleRoot = mvDoc.getDocumentElement();
								eleRoot.setAttribute("EnterpriseCode", "NWCG");
								eleRoot.setAttribute("Node", strNode);

								Element eleSource = mvDoc
										.createElement("Source");
								eleSource.setAttribute("PalletId", strPalletID);
								eleRoot.appendChild(eleSource);

								Element eleInventory = mvDoc
										.createElement("Inventory");
								eleSource.appendChild(eleInventory);
								XMLUtil.copyElement(mvDoc, itemInvElem,
										eleInventory);

								Element eleDest = mvDoc
										.createElement("Destination");
								eleDest.setAttribute("LocationId", sLocationId);
								eleRoot.appendChild(eleDest);

								logger.verbose("@@@@@ moveLocationInventory API :: "
										+ XMLUtil
												.extractStringFromDocument(mvDoc));
								CommonUtilities.invokeAPI(env,
										"moveLocationInventory", mvDoc);

								if (strInvStatus
										.equals(NWCGConstants.NRFI_RFB_STATUS)) {
									Document clDoc = XMLUtil
											.createDocument("ChangeLocationInventoryAttributes");
									Element eleCLRoot = clDoc
											.getDocumentElement();
									eleCLRoot.setAttribute("EnterpriseCode",
											"NWCG");
									eleCLRoot.setAttribute("Node", strNode);

									Element eleCLSource = clDoc
											.createElement("Source");
									eleCLSource.setAttribute("LocationId",
											sLocationId);
									eleCLRoot.appendChild(eleCLSource);

									Element eleFromInventory = clDoc
											.createElement("FromInventory");
									eleCLSource.appendChild(eleFromInventory);
									XMLUtil.copyElement(clDoc, itemInvElem,
											eleFromInventory);
									eleFromInventory
											.removeAttribute("PalletId");
									eleFromInventory.setAttribute(
											"InventoryStatus",
											NWCGConstants.RFI_STATUS);
									Element eleFromSerialList = (Element) eleFromInventory
											.getElementsByTagName("SerialList")
											.item(0);
									if (!eleFromSerialList.hasChildNodes())
										eleFromInventory
												.removeChild(eleFromSerialList);

									Element eleToInventory = clDoc
											.createElement("ToInventory");
									eleCLSource.appendChild(eleToInventory);
									XMLUtil.copyElement(clDoc, itemInvElem,
											eleToInventory);
									eleToInventory.removeAttribute("PalletId");
									eleToInventory
											.setAttribute(
													"InventoryStatus",
													NWCGConstants.NWCG_NRFI_DISPOSITION_CODE);
									Element eleToSerialList = (Element) eleToInventory
											.getElementsByTagName("SerialList")
											.item(0);
									if (!eleToSerialList.hasChildNodes())
										eleToInventory
												.removeChild(eleToSerialList);

									Element eleAudit = clDoc
											.createElement("Audit");
									eleAudit.setAttribute("ReasonCode",
											"OTHERS");
									eleAudit.setAttribute("ReasonText",
											"LPN NRFI Return");
									eleCLRoot.appendChild(eleAudit);

									logger.verbose("@@@@@ changeLocationInventoryAttributes API :: "
											+ XMLUtil
													.extractStringFromDocument(clDoc));
									CommonUtilities
											.invokeAPI(
													env,
													"changeLocationInventoryAttributes",
													clDoc);
								}

							}

						}

					}
				}

			}
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
		}
		logger.verbose("@@@@@ Exiting NWCGBreakLPN::breakLPN");
		return inDoc;
	}

}