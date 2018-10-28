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

package com.nwcg.icbs.yantra.api.billingtransaction;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * 
 * @author Oxford
 * 
 */
public class NWCGGetFBMSCodes implements YIFCustomApi {

	int blankcnt, lengthcnt, totextcnt = 0;

	private Properties props;
	
	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGGetFBMSCodes.class);

	public void setProperties(Properties arg0) throws Exception {
		this.props = arg0;
	}

	public void throwAlert(YFSEnvironment env, StringBuffer Message)
			throws Exception {
		logger.verbose("@@@@@ Entering NWCGGetFBMSCodes::throwAlert @@@@@");
		Message.append(" ExceptionType='BILLING_TRANS_EXTRACT'"
				+ " InboxType='" + NWCGConstants.NWCG_BILL_TRANS_INBOXTYPE
				+ "' QueueId='" + NWCGConstants.NWCG_BILL_TRANS_QUEUEID
				+ "' />");
		logger.verbose("Throw Alert Method called with message:-"
				+ Message.toString());
		try {
			Document inTemplate = XMLUtil.getDocument(Message.toString());
			CommonUtilities.invokeAPI(env, NWCGConstants.NWCG_CREATE_EXCEPTION,
					inTemplate);
		} catch (Exception ex1) {
			throw new NWCGException(
					"NWCG_BILLING_TRANSACTION_ERROR_WHILE_LOGGING_ALERT");
		}
		logger.verbose("@@@@@ Exiting NWCGGetFBMSCodes::throwAlert @@@@@");
	}

	/**
	 * inXML doc input: <NWCGBillingTransaction CacheId="IDGBK"
	 * CacheIdQryType="" CostCenter="" CostCenterQryType="" DocumentType=""
	 * FTransDate="" FromTransDate="" FunctionalArea="" FunctionalAreaQryType=""
	 * IgnoreOrdering="Y" IncidentBlmAcctCode="" IncidentBlmAcctCodeQryType=""
	 * IncidentFSAcctCodeQryType="" IncidentFsAcctCode="" IncidentNo=""
	 * IncidentNoQryType="" IncidentYear="2012" IsExtracted="Y" IsReviewed="Y"
	 * ItemIDQryType="" ItemId="" MaximumRecords="30"
	 * OrderBy="IncidentBlmAcctCode" TTransDate="" ToTransDate=""
	 * TransDateQryType="DATERANGE" TransType="" TransactionNo=""
	 * TransactionNoQryType="" WBS="" WBSQryType=""/>
	 */
	public Document getFBMSCodes(YFSEnvironment env, Document inXML)
			throws Exception {
		logger.verbose("@@@@@ Entering NWCGGetFBMSCodes::getFBMSCodes @@@@@");
		logger.verbose("@@@@@ inXML : " + XMLUtil.getXMLString(inXML));
		String CostCenter = NWCGConstants.EMPTY_STRING;
		String CostCenterQryType = "LIKE";
		String FunctionalArea = NWCGConstants.EMPTY_STRING;
		String FunctionalAreaQryType = "LIKE";
		String WBS = NWCGConstants.EMPTY_STRING;
		String WBSQryType = "LIKE";
		String IncidentBlmAcctCode = NWCGConstants.EMPTY_STRING;

		String OrderBy = NWCGConstants.EMPTY_STRING;
		Element elemOrder = null;
		Element elemAttr = null;

		Element elemRoot = null;
		elemRoot = inXML.getDocumentElement();
		if (elemRoot != null) {
			CostCenter = elemRoot
					.getAttribute(NWCGConstants.BILL_TRANS_COST_CENTER);
			FunctionalArea = elemRoot
					.getAttribute(NWCGConstants.BILL_TRANS_FUNCTIONAL_AREA);
			WBS = elemRoot.getAttribute(NWCGConstants.BILL_TRANS_WBS);
			IncidentBlmAcctCode = elemRoot
					.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE);
			OrderBy = elemRoot.getAttribute(NWCGConstants.ORDER_BY);
		}

		if (StringUtil.isEmpty(IncidentBlmAcctCode)) {
			if (!StringUtil.isEmpty(CostCenter)) {
				elemRoot.setAttribute(NWCGConstants.INCIDENT_BLM_ACCT_CODE,
						CostCenter);
				elemRoot.setAttribute(
						NWCGConstants.INCIDENT_BLM_ACCT_CODE_QRY_TYPE,
						CostCenterQryType);
			}

			if (!StringUtil.isEmpty(FunctionalArea)) {
				elemRoot.setAttribute(NWCGConstants.INCIDENT_BLM_ACCT_CODE,
						FunctionalArea);
				elemRoot.setAttribute(
						NWCGConstants.INCIDENT_BLM_ACCT_CODE_QRY_TYPE,
						FunctionalAreaQryType);
			}

			if (!StringUtil.isEmpty(WBS)) {
				elemRoot.setAttribute(NWCGConstants.INCIDENT_BLM_ACCT_CODE, WBS);
				elemRoot.setAttribute(
						NWCGConstants.INCIDENT_BLM_ACCT_CODE_QRY_TYPE,
						WBSQryType);
			}
		}

		String StrMaxRecords = props.getProperty("MaximumRecords");
		logger.verbose("@@@@@ StrMaxRecords :: " + StrMaxRecords);
		if (!StringUtil.isEmpty(StrMaxRecords)
				&& !StrMaxRecords.equalsIgnoreCase("useDefault")) {
			StrMaxRecords = ResourceUtil.get(StrMaxRecords);
			logger.verbose("@@@@@ StrMaxRecords not null :: " + StrMaxRecords);
			inXML.getDocumentElement().setAttribute("MaximumRecords",
					StrMaxRecords);
		}
		if (!StringUtil.isEmpty(OrderBy)) {
			elemRoot.setAttribute(NWCGConstants.IGNORE_ORDERING, "N");
			elemOrder = inXML.createElement(NWCGConstants.ORDER_BY);
			elemRoot.appendChild(elemOrder);

			elemAttr = inXML.createElement(NWCGConstants.ATTRIBUTE);
			elemOrder.appendChild(elemAttr);

			elemAttr.setAttribute(NWCGConstants.NAME_ATTR, OrderBy);
		}

		logger.verbose("@@@@@ Exiting NWCGGetFBMSCodes::getFBMSCodes @@@@@");
		return inXML;
	}
}