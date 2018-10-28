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

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class NWCGRefurbWOStatusChange implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGRefurbWOStatusChange.class);

	private Properties _properties;

	/**
	 * Here the input is the output of modifyWorkOrder. If the QuantityRequested
	 * - QuantityConfirmed = 0, then call the confirmWorkOrderActivity api with
	 * a quantity of 0. Here is the sample input XML <?xml version="1.0"
	 * encoding="UTF-8" ?> <WorkOrder EnterpriseCode="NWCG" NodeKey="RMK"
	 * WorkOrderNo="651"> <WorkOrderActivityDtl ActivityCode="REFURBISHMENT"
	 * ActivityLocationId="REFURB-1-LOC" QuantityBeingConfirmed="0" >
	 * </WorkOrderActivityDtl> </WorkOrder>
	 */
	public Document checkAndCallChangeWOStatus(YFSEnvironment env,
			Document inXML) throws YFSException {
		logger.verbose("NWCGRefurbWOStatusChange::checkAndCallChangeWOStatus, Entered");
		if (env == null) {
			logger.error("NWCGRefurbWOStatusChange::checkAndCallChangeWOStatus, YFSEnvironment is null");
			throw new NWCGException("NWCG_ENV_NULL");
		}
		if (inXML == null) {
			logger.error("NWCGRefurbWOStatusChange::checkAndCallChangeWOStatus, Input Document is null");
			throw new NWCGException("NWCG_GEN_IN_DOC_NULL");
		}

		try {
			Element woElm = inXML.getDocumentElement();
			// Element woElm = (Element) XMLUtil.getChildNodeByName(rootNode,
			// "WorkOrder");
			String qtyReq = woElm.getAttribute("QuantityRequested");

			int iQtyReq = new Double(qtyReq).intValue();

			String qtyCmpl = woElm.getAttribute("QuantityCompleted");
			int iQtyCmpl = new Double(qtyCmpl).intValue();

			if (iQtyReq == iQtyCmpl) {
				// Call the confirmWorkOrderActivity api
				Document woInputXML = XMLUtil.newDocument();
				Element woStatus = woInputXML.createElement("WorkOrder");
				woInputXML.appendChild(woStatus);
				String enterpriseCode = woElm.getAttribute("EnterpriseCode");
				String nodeKey = woElm.getAttribute("NodeKey");
				String workOrderNo = woElm.getAttribute("WorkOrderNo");

				// ActivityCode="REFURBISHMENT"
				// ActivityLocationId="REFURB-1-LOC" QuantityBeingConfirmed="0"
				// <?xml version="1.0" encoding="UTF-8" ?>
				// <WorkOrder BaseDropStatus="" EnterpriseCode="" NodeKey=""
				// TaskQKey="" TransactionId="" WorkOrderKey="" WorkOrderNo="">
				//
				// </WorkOrder>

				// String actCode = XPathUtil.getString(rootNode,
				// "/WorkOrder/WorkOrderActivities/WorkOrderActivity/@ActivityCode");
				// String actLocId = XPathUtil.getString(rootNode,
				// "/WorkOrder/WorkOrderActivities/WorkOrderActivity/WorkOrderActivityDtls/WorkOrderActivityDtl/@ActivityLocationId");

				woStatus.setAttribute("BaseDropStatus",
						NWCGConstants.RFB_VAS_WO_CMPL_STATUS);
				woStatus.setAttribute("EnterpriseCode", enterpriseCode);
				woStatus.setAttribute("NodeKey", nodeKey);
				woStatus.setAttribute("TransactionId",
						NWCGConstants.VAS_CONFIRM_WO_TRANSACTION);
				woStatus.setAttribute("WorkOrderNo", workOrderNo);

				Document outputDoc = XMLUtil.newDocument();
				outputDoc = CommonUtilities.invokeAPI(env,
						NWCGConstants.API_CHG_WO_STATUS, woInputXML);
				if (outputDoc != null) {
					return outputDoc;
				} else {
					logger.verbose("NWCGRefurbWOStatus::checkAndCallChangeWOStatus, Output document is NULL");
				}
			} else {
				logger.verbose("NWCGRefurbWOStatus::checkAndCallChangeWOStatus, Quantity Requested and Quantity Completed are different");
			}
		} catch (Exception e) {
			logger.error("NWCGRefurbDisposition::checkAndCallChangeWOStatus, Exception Msg : "
					+ e.getMessage());
			logger.error("NWCGRefurbDisposition::checkAndCallChangeWOStatus, StackTrace : "
					+ e.getStackTrace());
			throw new NWCGException(e);
		}

		logger.verbose("NWCGRefurbWOStatusChange::checkAndCallChangeWOStatus, Returning");
		return inXML;
	}

	public void setProperties(Properties prop) throws Exception {
		_properties = prop;
	}

}
