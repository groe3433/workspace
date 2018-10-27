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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGSetReviewFlagWrapper {

	public Document setReviewFlagWrapper(YFSEnvironment env, Document doc)
			throws Exception {
		Element elemNWCGBillingTransaction = doc.getDocumentElement();
		String strTempNLLen = elemNWCGBillingTransaction
				.getAttribute("strTempNLLen");
		int intNlLen = Integer.parseInt(strTempNLLen);
		String strSequenceKey = "";
		Document setReviewFlagInDoc = XMLUtil.getDocument();
		Element setReviewFlagElem = setReviewFlagInDoc
				.createElement("NWCGBillingTransaction");
		setReviewFlagInDoc.appendChild(setReviewFlagElem);
		for (int i = 0; i < intNlLen; i++) {
			strSequenceKey = elemNWCGBillingTransaction
					.getAttribute("SequenceKey_" + i);
			setReviewFlagElem.setAttribute("SequenceKey", strSequenceKey);
			setReviewFlagElem.setAttribute(
					NWCGConstants.BILL_TRANS_IS_REVIEWED, "Y");
			CommonUtilities.invokeService(env, "NWCGSetReviewFlagService",
					setReviewFlagInDoc);
		}
		return doc;
	}

	public Document unsetReviewFlagWrapper(YFSEnvironment env, Document doc)
			throws Exception {
		Element elemNWCGBillingTransaction = doc.getDocumentElement();
		String strTempNLLen = elemNWCGBillingTransaction
				.getAttribute("strTempNLLen");
		int intNlLen = Integer.parseInt(strTempNLLen);
		String strSequenceKey = "";
		Document setReviewFlagInDoc = XMLUtil.getDocument();
		Element setReviewFlagElem = setReviewFlagInDoc
				.createElement("NWCGBillingTransaction");
		setReviewFlagInDoc.appendChild(setReviewFlagElem);
		for (int i = 0; i < intNlLen; i++) {
			strSequenceKey = elemNWCGBillingTransaction
					.getAttribute("SequenceKey_" + i);
			setReviewFlagElem.setAttribute("SequenceKey", strSequenceKey);
			// Create Document to check if transaction is extracted
			Document billTranDoc = XMLUtil.getDocument();
			Element billTranInElem = billTranDoc
					.createElement("NWCGBillingTransaction");
			billTranInElem.setAttribute("SequenceKey", strSequenceKey);
			billTranDoc.appendChild(billTranInElem);
			billTranDoc = CommonUtilities.invokeService(env,
					NWCGConstants.NWCG_GET_BILLING_TRANSACTION_DETAIL_SERVICE,
					billTranDoc);
			Element billTranOutElem = (Element) billTranDoc
					.getElementsByTagName("NWCGBillingTransaction").item(0);
			String isExtracted = billTranOutElem
					.getAttribute(NWCGConstants.BILL_TRANS_IS_EXTRACTED);
			if (isExtracted.equals("N")) {
				setReviewFlagElem.setAttribute(
						NWCGConstants.BILL_TRANS_IS_REVIEWED, "N");
				CommonUtilities.invokeService(env, "NWCGSetReviewFlagService",
						setReviewFlagInDoc);
			}
		}
		return doc;
	}
}