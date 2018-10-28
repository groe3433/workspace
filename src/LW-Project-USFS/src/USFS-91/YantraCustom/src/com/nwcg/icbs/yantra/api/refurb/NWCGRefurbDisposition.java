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

package com.nwcg.icbs.yantra.api.refurb;

import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;

public class NWCGRefurbDisposition implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGRefurbDisposition.class);

	private Properties _properties;

	/**
	 * <WorkOrderDisposition Disposition="RFI" EnterpriseCode="NWCG"
	 * IgnoreOrdering="Y" IsTracked="Y" ItemID="TestKitSerItem123"
	 * LocationId="KIT01" NodeKey="RMK" ProductClass="Supply" Quantity="2.0"
	 * Uom="KT"> <SerialNumberDetails> <SerialNumberDetail SerialNo="12"
	 * YFC_NODE_NUMBER="2.0"/> <SerialNumberDetail SerialNo="13"
	 * YFC_NODE_NUMBER="1.0"/> </SerialNumberDetails> </WorkOrderDisposition>
	 * 
	 * 
	 * Input XML to changeLocationInventoryAttributes <?xml version="1.0"
	 * encoding="UTF-8"?> <ChangeLocationInventoryAttributes
	 * EnterpriseCode="NWCG" IgnoreOrdering="Y" Node="RMK"> <Source CaseId=""
	 * LocationId="KIT01" PalletId=""> <ToInventory CountryOfOrigin=""
	 * FifoNo="0" InventoryStatus="UNSERVICE" ShipByDate=""> <InventoryItem
	 * ItemID="TestSerItem1" ProductClass="Supply" UnitOfMeasure="EACH"/>
	 * </ToInventory> <FromInventory CountryOfOrigin="" FifoNo="0"
	 * InventoryStatus="RFI" Quantity="1.0" Segment="" SegmentType="">
	 * <InventoryItem ItemID="TestSerItem1" ProductClass="Supply"
	 * UnitOfMeasure="EACH"/> <SerialList> <SerialDetail SerialNo="101"/>
	 * <SerialDetail SerialNo="101"/> <SerialDetail SerialNo="101"/>
	 * <SerialDetail SerialNo="101"/> </SerialList> </FromInventory> </Source>
	 * <Audit ReasonCode="DATA-ENTRY" ReasonText="&#x9;&#x9;" Reference1=""
	 * Reference2="" Reference3="" Reference4="" Reference5=""/>
	 * </ChangeLocationInventoryAttributes>
	 */
	public Document updateDispositionCode(YFSEnvironment env, Document inXML)
			throws NWCGException {
		logger.verbose("NWCGRefurbDisposition::updateDispositionCode, Entered");
		if (env == null) {
			logger.error("NWCGRefurbDisposition::updateDispositionCode, YFSEnvironment is null");
			throw new NWCGException("NWCG_ENV_NULL");
		}
		if (inXML == null) {
			logger.error("NWCGRefurbDisposition::updateDispositionCode, Input Document is null");
			throw new NWCGException("NWCG_GEN_IN_DOC_NULL");
		}

		if (logger.isVerboseEnabled()) {
			logger.verbose("NWCGRefurbDisposition::updateDispositionCode, Input XML : "
					+ XMLUtil.getXMLString(inXML));
		}

		try {
			Element woDisp = inXML.getDocumentElement();
			// Element woDisp = (Element) XMLUtil.getChildNodeByName(rootNode,
			// "WorkOrderDisposition");
			String qtyFromXML = woDisp.getAttribute("Quantity");
			String toDisposition = woDisp.getAttribute("Disposition");
			logger.verbose("NWCGRefurbDisposition::updateDispositionCode, toDisposition : "
					+ toDisposition);
			String enterpriseCode = woDisp.getAttribute("EnterpriseCode");
			String itemId = woDisp.getAttribute("ItemID");
			String locationId = woDisp.getAttribute("LocationId");
			String nodeKey = woDisp.getAttribute("NodeKey");
			String productClass = woDisp.getAttribute("ProductClass");
			String uom = woDisp.getAttribute("Uom");
			String isTracked = woDisp.getAttribute("IsTracked");
			String workOrderKey = woDisp.getAttribute("WorkOrderKey");

			// Form the input XML with the above attributes
			Document chgInvAttrInputXML = XMLUtil.newDocument();

			Element chgLocInvAttr = chgInvAttrInputXML
					.createElement("ChangeLocationInventoryAttributes");
			chgInvAttrInputXML.appendChild(chgLocInvAttr);

			chgLocInvAttr.setAttribute("EnterpriseCode", enterpriseCode);
			chgLocInvAttr.setAttribute("Node", nodeKey);

			Element source = chgInvAttrInputXML.createElement("Source");
			source.setAttribute("LocationId", locationId);

			Element toInv = chgInvAttrInputXML.createElement("ToInventory");
			toInv.setAttribute("InventoryStatus", toDisposition);

			Element invItemTo = chgInvAttrInputXML
					.createElement("InventoryItem");
			// ItemID="TestSerItem1" ProductClass="Supply" UnitOfMeasure="EACH"
			invItemTo.setAttribute("ItemID", itemId);
			invItemTo.setAttribute("ProductClass", productClass);
			invItemTo.setAttribute("UnitOfMeasure", uom);

			Element invItemFrom = chgInvAttrInputXML
					.createElement("InventoryItem");
			// ItemID="TestSerItem1" ProductClass="Supply" UnitOfMeasure="EACH"
			invItemFrom.setAttribute("ItemID", itemId);
			invItemFrom.setAttribute("ProductClass", productClass);
			invItemFrom.setAttribute("UnitOfMeasure", uom);

			toInv.appendChild(invItemTo);
			source.appendChild(toInv);

			Element fromInv = chgInvAttrInputXML.createElement("FromInventory");
			fromInv.setAttribute("InventoryStatus",
					NWCGConstants.NRFI_RFB_STATUS);
			fromInv.appendChild(invItemFrom);
			source.appendChild(fromInv);

			chgLocInvAttr.appendChild(source);

			Element audit = chgInvAttrInputXML.createElement("Audit");
			audit.setAttribute("ReasonCode",
					NWCGConstants.RFB_ADJUSTMENT_REASON_CODE);
			chgLocInvAttr.appendChild(audit);

			NWCGRefurbReturns refurbRtnProcess = new NWCGRefurbReturns();
			String incidentNum = "";
			// If it is a serial tracked order, then we will have more than one
			// entry for
			// serial tracked items. If it is a non-serial order, then we will
			// decrement the
			// items by that quantity.
			if (isTracked.equalsIgnoreCase("Y")) {
				NodeList serialIdList = woDisp
						.getElementsByTagName("SerialNumberDetail");
				// NodeList serialIdList = XPathUtil.getNodeList(rootNode,
				// "/WorkOrderDisposition/SerialNumberDetails/SerialNumberDetail");
				Element serialList = chgInvAttrInputXML
						.createElement("SerialList");
				for (int i = 0; i < serialIdList.getLength(); i++) {
					Element serialDtlFromJSP = (Element) serialIdList.item(i);
					String serialNo = serialDtlFromJSP.getAttribute("SerialNo");
					logger.verbose("Serial Number : " + serialNo);
					Element serialDtl = chgInvAttrInputXML
							.createElement("SerialDetail");
					serialDtl.setAttribute("SerialNo", serialNo);
					serialList.appendChild(serialDtl);
					// Updating NWCG_Incident_Return table
					incidentNum = refurbRtnProcess
							.processRefurbDispositionStatus(env, incidentNum,
									nodeKey, itemId, serialNo, toDisposition,
									isTracked, qtyFromXML, workOrderKey);
				}
				fromInv.appendChild(serialList);
			} else { // Non-Serialized Items
				fromInv.setAttribute("Quantity", qtyFromXML);
				logger.verbose("Quantity : " + qtyFromXML);
				// Updating NWCG_Incident_Return table
				refurbRtnProcess.processRefurbDispositionStatus(env,
						incidentNum, nodeKey, itemId, "", toDisposition,
						isTracked, qtyFromXML, workOrderKey);
			}

			if (logger.isVerboseEnabled()) {
				logger.verbose("NWCGRefurbDisposition::updateDispositionCode, Final XML : "
						+ XMLUtil.getXMLString(chgInvAttrInputXML));
			}

			Document outputDoc = XMLUtil.newDocument();
			outputDoc = CommonUtilities.invokeAPI(env,
					NWCGConstants.API_CHG_LOCN_INV_ATTR, chgInvAttrInputXML);
			if (outputDoc != null) {
				if (logger.isVerboseEnabled()) {
					logger.verbose("NWCGRefurbDisposition::updateDispostionCode, Output Doc : "
							+ XMLUtil.getXMLString(outputDoc));
				}
			} else {
				logger.verbose("NWCGRefurbDisposition::updateDispostionCode, Output Doc is NULL");
			}
		} catch (Exception e) {
			logger.error("NWCGRefurbDisposition::updateDispositionCode, Exception Msg : "
					+ e.getMessage());
			logger.error("NWCGRefurbDisposition::updateDispositionCode, StackTrace : "
					+ e.getStackTrace());
			throw new NWCGException(e);
		}

		logger.verbose("NWCGRefurbDisposition::updateDispositionCode, Exiting");
		return inXML;
	}

	public void setProperties(Properties prop) throws Exception {
		_properties = prop;
	}

}
