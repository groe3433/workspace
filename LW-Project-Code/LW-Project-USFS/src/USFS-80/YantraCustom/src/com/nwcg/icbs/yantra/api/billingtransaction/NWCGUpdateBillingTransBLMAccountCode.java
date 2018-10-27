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
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.reports.loftware.NWCGReportsUtil;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGUpdateBillingTransBLMAccountCode implements YIFCustomApi {
	public void setProperties(Properties arg0) throws Exception {
	}

	public Document updateBLMAccountCode(YFSEnvironment env, Document doc)
			throws Exception {
		Element eleRoot = doc.getDocumentElement();
		String strIncidentBLMAccountCode = eleRoot
				.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE);
		String strTransactionNo = eleRoot
				.getAttribute(NWCGConstants.BILL_TRANS_NO);
		// Get the list of all the billing transaction with this Transaction no
		Document docTranscationList = getBillingTransactionList(env,
				strTransactionNo);
		NodeList lsTransactionList = docTranscationList
				.getElementsByTagName("NWCGBillingTransaction");
		if (lsTransactionList != null && lsTransactionList.getLength() > 0) {
			for (int i = 0; i < lsTransactionList.getLength(); i++) {
				Element eleNWCGBillingTransaction = (Element) lsTransactionList
						.item(i);
				String strSequenceNo = eleNWCGBillingTransaction
						.getAttribute(NWCGConstants.BILL_TRANS_SEQUENCE_KEY);
				// update new BLM account code on Billing Transactions
				setNewBLMAccountCode(env, strSequenceNo,
						strIncidentBLMAccountCode);
			}
		}
		String strIncidentKey = eleRoot
				.getAttribute(NWCGConstants.INC_NOTIF_INCKEY_ATTR);
		String strCostCenter = eleRoot
				.getAttribute(NWCGConstants.BILL_TRANS_COST_CENTER);
		String strWBS = null;
		if (eleRoot.hasAttribute(NWCGConstants.BILL_TRANS_WBS))
			strWBS = eleRoot.getAttribute(NWCGConstants.BILL_TRANS_WBS);
		String strFunctionalArea = eleRoot
				.getAttribute(NWCGConstants.BILL_TRANS_FUNCTIONAL_AREA);
		// update new BLM account code on Incidents
		setNewBLMAccountCodeOnIncident(env, strIncidentKey, strCostCenter,
				strWBS, strFunctionalArea, strIncidentBLMAccountCode);
		return doc;
	}

	private Document getBillingTransactionList(YFSEnvironment env,
			String strTransactionNo) throws Exception {
		Document doc = XMLUtil.createDocument("NWCGBillingTransaction");
		Element eleRoot = doc.getDocumentElement();
		eleRoot.setAttribute(NWCGConstants.BILL_TRANS_NO, strTransactionNo);
		Document docOut = CommonUtilities.invokeService(env,
				NWCGConstants.NWCG_GET_BILLING_TRANSACTION_LIST_SERVICE, doc);
		return docOut;
	}

	private void setNewBLMAccountCode(YFSEnvironment env, String strSequenceNo,
			String strIncidentBLMAccountCode) throws Exception {
		Document BillDoc = XMLUtil.createDocument("NWCGBillingTransaction");
		Element BillDocElem = BillDoc.getDocumentElement();
		BillDocElem.setAttribute(
				NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE,
				strIncidentBLMAccountCode);
		BillDocElem.setAttribute(NWCGConstants.BILL_TRANS_SEQUENCE_KEY,
				strSequenceNo);
		CommonUtilities.invokeService(env,
				NWCGConstants.NWCG_UPDATE_BILLING_TRANSACTION_SERVICE, BillDoc);
	}

	private void setNewBLMAccountCodeOnIncident(YFSEnvironment env,
			String strIncidentKey, String strCostCenter, String strWBS,
			String strFunctionalArea, String strIncidentBLMAccountCode)
			throws Exception {
		Document docUpdateDoc = XMLUtil.createDocument("NWCGIncidentOrder");
		Element eledocUpdateDoc = docUpdateDoc.getDocumentElement();
		eledocUpdateDoc.setAttribute(NWCGConstants.INC_NOTIF_INCKEY_ATTR,
				strIncidentKey);
		if (strIncidentBLMAccountCode != null
				&& !strIncidentBLMAccountCode.equals(""))
			eledocUpdateDoc.setAttribute(NWCGConstants.INCIDENT_BLM_ACCT_CODE,
					strIncidentBLMAccountCode);
		if (strCostCenter != null && !strCostCenter.equals(""))
			eledocUpdateDoc.setAttribute(NWCGConstants.BILL_TRANS_COST_CENTER,
					strCostCenter);
		if (strWBS != null)
			eledocUpdateDoc.setAttribute(NWCGConstants.BILL_TRANS_WBS, strWBS);
		if (strFunctionalArea != null && !strFunctionalArea.equals(""))
			eledocUpdateDoc
					.setAttribute(NWCGConstants.BILL_TRANS_FUNCTIONAL_AREA,
							strFunctionalArea);
		CommonUtilities.invokeService(env, "NWCGUpdateIncidentOrderService",
				docUpdateDoc);
	}
}