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

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGUpdateBillingTransExtractRecordsAPI implements YIFCustomApi {

	public Document updateBillingTransExtractRecords(YFSEnvironment env,
			Document doc) throws Exception {
		Element elemNWCGBillingTransExtract = doc.getDocumentElement();
		String strTransactionNo = elemNWCGBillingTransExtract
				.getAttribute("TransactionNo");
		String strCostCenter = elemNWCGBillingTransExtract
				.getAttribute("CostCenter");
		String strFA = elemNWCGBillingTransExtract
				.getAttribute("FunctionalArea");
		String strWBS = elemNWCGBillingTransExtract.getAttribute("WBS");
		String strPostExtractSequenceKey = "";
		Document getBillingTransNoListDoc = NWCGGetBillingTransactionExtractFileName
				.getBillingTransNoList(env, strTransactionNo);
		Document updateBillingTransRecordInDoc = XMLUtil.getDocument();
		Element eleNWCGBillingTransExtract = updateBillingTransRecordInDoc
				.createElement("NWCGBillingTransExtract");
		updateBillingTransRecordInDoc.appendChild(eleNWCGBillingTransExtract);
		eleNWCGBillingTransExtract.setAttribute("TransactionNo",
				strTransactionNo);
		if (!strCostCenter.equalsIgnoreCase("")) {
			eleNWCGBillingTransExtract
					.setAttribute("CostCenter", strCostCenter);
		}
		if (!strFA.equalsIgnoreCase("")) {
			eleNWCGBillingTransExtract.setAttribute("FunctionalArea", strFA);
		}
		if (!strWBS.equalsIgnoreCase("")) {
			eleNWCGBillingTransExtract.setAttribute("WBS", strWBS);
		}
		Element tempNWCGBillingTransExtractElem;

		NodeList nlNWCGBillingTransExtract = getBillingTransNoListDoc
				.getElementsByTagName("NWCGBillingTransExtract");
		int nlNWCGBTELen = nlNWCGBillingTransExtract.getLength();
		for (int i = 0; i < nlNWCGBTELen; i++) {
			// prepare input xml for service
			// NWCGUpdateBillingTransExtractRecordService
			tempNWCGBillingTransExtractElem = (Element) nlNWCGBillingTransExtract
					.item(i);
			strPostExtractSequenceKey = tempNWCGBillingTransExtractElem
					.getAttribute("PostExtractSequenceKey");
			eleNWCGBillingTransExtract.setAttribute("PostExtractSequenceKey",
					strPostExtractSequenceKey);
			CommonUtilities.invokeService(env,
					"NWCGUpdateBillingTransExtractRecordService",
					updateBillingTransRecordInDoc);
		}
		return doc;
	}

	public void setProperties(Properties arg0) throws Exception {
	}
}