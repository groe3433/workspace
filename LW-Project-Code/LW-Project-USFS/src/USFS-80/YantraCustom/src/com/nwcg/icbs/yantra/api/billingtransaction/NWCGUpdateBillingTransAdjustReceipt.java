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

/**
 * -----------------------------------------------------------------------------
 * QC : 855 Scenario : On the Return Receipt Console, Return Receipt Summary,
 * when a 'Receipt in Progress' status Return is adjusted to Unreceive quantity,
 * the Billing Transaction was not being updated with the quantity after
 * Unreceive was successful. Description : This API Updates/Deletes the Billing
 * Transaction record based on the Unreceive quantity. Update - if the Unreceive
 * quantity is less than the Return quantity. TransQty and TransAmt fields are
 * updated. Delete - if the Unreceive quantity is same as Return quantity.
 * -----------------------------------------------------------------------------
 */
import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.*;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;

import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class NWCGUpdateBillingTransAdjustReceipt {

	public Document updateTransaction(YFSEnvironment env, Document inXML)
			throws YFSException, Exception {
		try {
			Document deleteDoc = null;
			Document updateDoc = null;
			Element childElems = null;
			String qty = null;
			String uQty = null;
			String transKey = null;
			String sKey = null;
			String billUnitCost = null;
			String isExtracted = null;
			String isReviewed = null;
			inXML.getDocumentElement().normalize();
			Element RootElem = inXML.getDocumentElement();
			String receiptHeaderKey = RootElem.getAttribute("ReceiptHeaderKey");
			NodeList nlRootChilds = RootElem.getChildNodes();
			for (int rootNode = 0; rootNode < nlRootChilds.getLength(); rootNode++) {
				Node tmpNode = nlRootChilds.item(rootNode);
				if (tmpNode.getNodeName().equals("ReceiptLines")) {
					childElems = (Element) tmpNode;
				}
			}
			NodeList nlReceiptLine = childElems
					.getElementsByTagName("ReceiptLine");
			if (nlReceiptLine != null && nlReceiptLine.getLength() > 0) {
				for (int i = 0; i < nlReceiptLine.getLength(); i++) {
					Element childElem = (Element) nlReceiptLine.item(i);
					transKey = childElem.getAttribute("ReceiptLineKey");
					qty = childElem.getAttribute("Quantity");
					uQty = childElem.getAttribute("UnreceiveQuantity");
				}
			}
			/*
			 * Service: NWCGGetBillingTransactionListService Params:
			 * TransType="RETURNS" TransactionHeaderKey = ReceiptHeaderKey
			 * TransactionLineKey = ReceiptLineKey
			 */
			Document inDoc = XMLUtil.getDocument();
			Element el = inDoc.createElement("NWCGBillingTransactionList");
			el.setAttribute(NWCGConstants.BILL_TRANS_TYPE, "RETURNS");
			el.setAttribute(NWCGConstants.BILL_TRANS_HEADER_KEY,
					receiptHeaderKey);
			el.setAttribute(NWCGConstants.BILL_TRANS_LINE_KEY, transKey);
			inDoc.appendChild(el);
			Document outDoc = CommonUtilities.invokeService(env,
					NWCGConstants.NWCG_GET_BILLING_TRANSACTION_LIST_SERVICE,
					inDoc);
			Element outXMLElem = outDoc.getDocumentElement();
			NodeList bList = outXMLElem
					.getElementsByTagName("NWCGBillingTransaction");
			if (bList != null && bList.getLength() > 0) {
				for (int i = 0; i < bList.getLength(); i++) {
					Element childBillList = (Element) bList.item(i);
					sKey = childBillList.getAttribute("SequenceKey");
					billUnitCost = childBillList.getAttribute("UnitCost");
					isExtracted = childBillList.getAttribute("IsExtracted");
					isReviewed = childBillList.getAttribute("IsReviewed");
				}
			}
			String sQty = Double.toString(Math.round(Double.parseDouble(qty)));
			String suQty = Double
					.toString(Math.round(Double.parseDouble(uQty)));
			if ((isExtracted.equals("N")) && (isReviewed.equals("N"))) {
				if (sQty.equals(suQty)) {

					/*
					 * Service: NWCGBillingTransactionDeleteService
					 * Description:Deletes the Billing Transaction record for
					 * the mentioned SequenceKey Params: TransType="RETURNS"
					 * SequenceKey = SequenceKey (for the
					 * ReceiptLineKey[TransactionLineKey])
					 */
					deleteDoc = XMLUtil.getDocument();
					Element del = deleteDoc
							.createElement("NWCGBillingTransaction");
					del.setAttribute(NWCGConstants.BILL_TRANS_TYPE, "RETURNS");
					del.setAttribute(NWCGConstants.BILL_TRANS_SEQUENCE_KEY,
							sKey);
					deleteDoc.appendChild(del);
					Document outDeleteDoc = CommonUtilities
							.invokeService(
									env,
									NWCGConstants.NWCG_BILLING_TRANSACTION_DELETE_SERVICE,
									deleteDoc);
				} else {
					double dQty = 0d;
					double duQty = 0d;
					double dbillUnitCost = 0d;
					double dbillQtyUpdated = 0d;
					double dbillAmtUpdated = 0d;
					dQty = Double.parseDouble(qty);
					duQty = Double.parseDouble(uQty);
					dbillUnitCost = Double.parseDouble(billUnitCost);
					dbillQtyUpdated = dQty - duQty;
					dbillAmtUpdated = dbillQtyUpdated * dbillUnitCost;
					String sbillQtyUpdated = Double.toString(dbillQtyUpdated);
					String sbillAmtUpdated = Double.toString(dbillAmtUpdated);
					/*
					 * Service: NWCGUpdateBillingTransactionService Description:
					 * Updates the Billing Transaction record for the mentioned
					 * SequenceKey with the TransQty calculated and the TransAmt
					 * calculated Params: TransType="RETURNS" SequenceKey =
					 * SequenceKey (for the ReceiptLineKey[TransactionLineKey])
					 * TransQty = ReturnQty - ReturnUnreceiveQty TransAmt =
					 * TransQty * UnitCost from the Billing Transaction
					 */
					updateDoc = XMLUtil.getDocument();
					Element update = updateDoc
							.createElement("NWCGBillingTransaction");
					update.setAttribute(NWCGConstants.BILL_TRANS_TYPE,
							"RETURNS");
					update.setAttribute(NWCGConstants.BILL_TRANS_SEQUENCE_KEY,
							sKey);
					update.setAttribute(NWCGConstants.BILL_TRANS_QTY,
							sbillQtyUpdated);
					update.setAttribute(NWCGConstants.BILL_TRANS_AMOUNT,
							sbillAmtUpdated);
					updateDoc.appendChild(update);
					Document outUpdateDoc = CommonUtilities
							.invokeService(
									env,
									NWCGConstants.NWCG_UPDATE_BILLING_TRANSACTION_SERVICE,
									updateDoc);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inXML;
	}
}