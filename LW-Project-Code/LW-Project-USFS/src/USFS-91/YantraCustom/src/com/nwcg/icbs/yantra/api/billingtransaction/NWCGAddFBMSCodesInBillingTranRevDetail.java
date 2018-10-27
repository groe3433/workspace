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
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGAddFBMSCodesInBillingTranRevDetail implements YIFCustomApi {

	public void setProperties(Properties arg0) throws Exception {
	}

	public Document setFBMSCodesInBillingTranRevDetail(YFSEnvironment env,
			Document inDoc) throws Exception {

		Element eleInDoc = inDoc.getDocumentElement();
		String strIncidentNo = eleInDoc.getAttribute("IncidentNo");
		String strIncidentYear = eleInDoc.getAttribute("IncidentYear");
		Document docIncidentDoc = XMLUtil.createDocument("NWCGIncidentOrder");
		Element eleIncidentRoot = docIncidentDoc.getDocumentElement();
		if (strIncidentNo != null && !strIncidentNo.equals("")) {
			eleIncidentRoot.setAttribute(NWCGConstants.INCIDENT_NO_ATTR,
					strIncidentNo);
		}
		if (strIncidentYear != null && !strIncidentYear.equals("")) {
			eleIncidentRoot.setAttribute(NWCGConstants.YEAR_ATTR,
					strIncidentYear);
		}
		if (eleIncidentRoot.hasAttributes()) {
			Document docOut = CommonUtilities.invokeService(env,
					"NWCGGetIncidentOrderListService", docIncidentDoc);
			Element eleIncidentOrder = (Element) (docOut
					.getElementsByTagName("NWCGIncidentOrder").item(0));
			String strIncidentKey = eleIncidentOrder
					.getAttribute(NWCGConstants.INC_NOTIF_INCKEY_ATTR);
			String strCostCenter = eleIncidentOrder
					.getAttribute(NWCGConstants.BILL_TRANS_COST_CENTER);
			String strWBS = eleIncidentOrder
					.getAttribute(NWCGConstants.BILL_TRANS_WBS);
			String strFunctionalArea = eleIncidentOrder
					.getAttribute(NWCGConstants.BILL_TRANS_FUNCTIONAL_AREA);
			eleInDoc.setAttribute(NWCGConstants.INC_NOTIF_INCKEY_ATTR,
					strIncidentKey);
			eleInDoc.setAttribute(NWCGConstants.BILL_TRANS_COST_CENTER,
					strCostCenter);
			eleInDoc.setAttribute(NWCGConstants.BILL_TRANS_WBS, strWBS);
			eleInDoc.setAttribute(NWCGConstants.BILL_TRANS_FUNCTIONAL_AREA,
					strFunctionalArea);
		}

		String transNo = eleInDoc.getAttribute("TransactionNo");
		String transType = eleInDoc.getAttribute("TransType");
		String totalTransAmount = getTotalTransAmount(env, transNo, transType);
		eleInDoc.setAttribute("TotalBillingTransAmount", totalTransAmount);

		return inDoc;
	}

	private String getTotalTransAmount(YFSEnvironment env, String transNo,
			String transType) throws Exception {
		Document inDoc = XMLUtil.createDocument("NWCGBillingTransaction");
		Element billingTransRootElement = inDoc.getDocumentElement();
		billingTransRootElement.setAttribute("TransactionNo", transNo);
		billingTransRootElement.setAttribute("TransType", transType);
		Document outDoc = CommonUtilities.invokeService(env,
				"NWCGGetTotalBillingTransAmount", inDoc);
		Element billingDataElement = outDoc.getDocumentElement();

		return billingDataElement.getAttribute("TotalBillingTransAmount");
	}
}