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
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class NWCGRefurbWOActDtl implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGRefurbWOActDtl.class);

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
	public Document checkAndCallConfirmWOActDtl(YFSEnvironment env,
			Document inXML) throws YFSException {

		logger.verbose("NWCGRefurbWOActDtl::checkAndCallConfirmWOActDtl, Entered");
		if (env == null) {
			logger.error("NWCGRefurbWOActDtl::checkAndCallConfirmWOActDtl, YFSEnvironment is null");
			throw new NWCGException("NWCG_ENV_NULL");
		}
		if (inXML == null) {
			logger.error("NWCGRefurbWOActDtl::checkAndCallConfirmWOActDtl, Input Document is null");
			throw new NWCGException("NWCG_GEN_IN_DOC_NULL");
		}

		if (logger.isVerboseEnabled()) {
			logger.verbose("NWCGRefurbWOActDtl::checkAndCallConfirmWOActDtl, Input XML : "
					+ XMLUtil.getXMLString(inXML));
		}

		try {
			Element woElm = inXML.getDocumentElement();
			String qtyReq = woElm.getAttribute("QuantityRequested");
			int iQtyReq = new Double(qtyReq).intValue();

			String qtyCmpl = woElm.getAttribute("QuantityCompleted");
			int iQtyCmpl = new Double(qtyCmpl).intValue();

			logger.verbose("NWCGRefurbWOActDtl::checkAndCallConfirmWOActDtl, Cmpl : "
					+ qtyCmpl + ", Req : " + qtyReq);
			// Call the confirmWorkOrderActivity api if quantity completed and
			// quantity requested are same
			if (iQtyReq == iQtyCmpl) {
				Document woInputXML = XMLUtil.newDocument();
				Element wo = woInputXML.createElement("WorkOrder");
				woInputXML.appendChild(wo);
				String enterpriseCode = woElm.getAttribute("EnterpriseCode");
				String nodeKey = woElm.getAttribute("NodeKey");
				String workOrderNo = woElm.getAttribute("WorkOrderNo");
				wo.setAttribute("EnterpriseCode", enterpriseCode);
				wo.setAttribute("NodeKey", nodeKey);
				wo.setAttribute("WorkOrderNo", workOrderNo);

				Element woActDtl = woInputXML
						.createElement("WorkOrderActivityDtl");
				wo.appendChild(woActDtl);
				// ActivityCode="REFURBISHMENT"
				// ActivityLocationId="REFURB-1-LOC" QuantityBeingConfirmed="0"
				String actCode = XPathUtil
						.getString(woElm,
								"/WorkOrder/WorkOrderActivities/WorkOrderActivity/@ActivityCode");
				String actLocId = XPathUtil
						.getString(
								woElm,
								"/WorkOrder/WorkOrderActivities/WorkOrderActivity/WorkOrderActivityDtls/WorkOrderActivityDtl/@ActivityLocationId");

				woActDtl.setAttribute("ActivityCode", actCode);
				woActDtl.setAttribute("ActivityLocationId", actLocId);
				// We are forcing the order to complete by setting the
				// QuantityBeingConfirmed=0
				woActDtl.setAttribute("QuantityBeingConfirmed", "0");

				if (logger.isVerboseEnabled()) {
					logger.verbose("checkAndCallConfirmWOActDtl, Input XML : "
							+ XMLUtil.getXMLString(woInputXML));
				}
				Document outputDoc = XMLUtil.newDocument();
				outputDoc = CommonUtilities.invokeAPI(env,
						NWCGConstants.API_CONFIRM_WORK_ORDER_ACTIVITY,
						woInputXML);
				if (outputDoc != null) {
					return outputDoc;
				} else {
					logger.verbose("NWCGRefurbWOActDtl::checkAndCallConfirmWOActDtl, Output document is NULL");
				}
			} else {
				logger.verbose("NWCGRefurbWOActDtl::checkAndCallConfirmWOActDtl, Quantity Requested and Quantity Completed are different");
			}
		} catch (Exception e) {
			logger.error("NWCGRefurbDisposition::checkAndCallConfirmWOActDtl, Exception Msg : "
					+ e.getMessage());
			logger.error("NWCGRefurbDisposition::checkAndCallConfirmWOActDtl, StackTrace : "
					+ e.getStackTrace());
			throw new NWCGException(e);
		}

		logger.verbose("NWCGRefurbWOActDtl::checkAndCallConfirmWOActDtl, Exiting");
		return inXML;
	}

	public void setProperties(Properties prop) throws Exception {
		_properties = prop;
	}

}
