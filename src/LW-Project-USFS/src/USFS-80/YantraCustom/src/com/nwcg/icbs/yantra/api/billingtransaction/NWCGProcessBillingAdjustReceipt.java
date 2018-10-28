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
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/*
 * This API Updates all the Billing Transaction Records in NWCG_BILLING_TRANSACTION Table 
 * for Incident/Other Issue Account Code Changes ONLY !!! Other Changes/modifications will not be reflected 
 */
public class NWCGProcessBillingAdjustReceipt implements YIFCustomApi,
		NWCGBillingTransRecordMutator {
	
	private static Logger logger = Logger
			.getLogger(NWCGProcessBillingTransChangeOrder.class.getName());

	public void setProperties(Properties arg0) throws Exception {
	}

	public void throwAlert(YFSEnvironment env, StringBuffer Message)
			throws Exception {
		Message.append(" ExceptionType='BILLING_TRANS_CHANGE_ORDER'"
				+ " InboxType='" + NWCGConstants.NWCG_BILL_TRANS_INBOXTYPE
				+ "' QueueId='" + NWCGConstants.NWCG_BILL_TRANS_QUEUEID
				+ "' />");
		if (logger.isVerboseEnabled())
			logger.verbose("Throw Alert Method called with message:-"
					+ Message.toString());
		try {
			Document inTemplate = XMLUtil.getDocument(Message.toString());
			CommonUtilities.invokeAPI(env, "createException", inTemplate);
		} catch (Exception ex1) {
			throw new NWCGException(
					"NWCG_BILLING_TRANSACTION_ERROR_WHILE_LOGGING_ALERT");
		}
	}

	public Document adjustReceipt(YFSEnvironment env, Document inXML)
			throws Exception {
		if (logger.isVerboseEnabled()) {
			logger
					.verbose("Entering NWCGProcessBillingTransChangeOrder, Input document is:"
							+ XMLUtil.getXMLString(inXML));
		}
		//Jimmy - temp variables for alert descriptions
		String tempItemID = "";
		String tempOrderNo = "";
		try {
			/*
			 * <Receipt IgnoreOrdering="Y" LocationId="RECEIVE-1"
			 OverrideModificationRules="N" ReceiptHeaderKey="2010041411244115361491">
			 <ReceiptLines>
			 <ReceiptLine EnterpriseCode="NWCG" InventoryStatus="RFI"
			 ItemID="000030" OrderNo="0000575309" ProductClass="Supply"
			 Quantity="40.0" ReceiptLineKey="2010041411273215361494"
			 ReceiptLineNo="3" UnitOfMeasure="PG" UnreceiveQuantity="5" YFC_NODE_NUMBER="1"/>
			 </ReceiptLines>
			 <Shipment BuyerOrganizationCode="" DocumentType="0005"
			 EnterpriseCode="NWCG" OrderNo="0000575309" ReceivingNode="CORMK" SellerOrganizationCode="NWCG"/>
			 <Audit ReasonCode="RECEIPT-ERROR" ReasonText=""/>
			 </Receipt>
			 */
			String ItemID = "";
			String OrderNo = "";
			String Quantity = "";
			String UnreceiveQuantity = "";
			String SequenceKey = "";
			String TransQty = "";
			String UnitCost = "";
			String ReceiptLineKey = "";
			String OrderLineKey = "";
			Element elemRoot = inXML.getDocumentElement();
			NodeList rlSoureNL = elemRoot.getElementsByTagName("ReceiptLine");
			int nlLength = rlSoureNL.getLength();
			for (int i = 0; i < nlLength; i++) {
				Element rlElm = (Element) rlSoureNL.item(i);
				ItemID = rlElm.getAttribute("ItemID");
				OrderNo = rlElm.getAttribute("OrderNo");
				Quantity = rlElm.getAttribute("Quantity");
				ReceiptLineKey = rlElm.getAttribute("ReceiptLineKey");
				UnreceiveQuantity = rlElm.getAttribute("UnreceiveQuantity");
			}
			//Jimmy - assigning temp vars
			tempItemID = ItemID;
			tempOrderNo = OrderNo;
			/*
			 * 
			 * getReceiptLineList // input xml
			 * <ReceiptLine OrderNo="0000575309" ReceiptLineKey="2010041411273215361494"/>
			 * this will get the OrderLineKey for transaction
			 */
			Document ReceiptLineListInDoc = XMLUtil.newDocument();
			Document ReceiptLineListOutDoc = XMLUtil.newDocument();
			Element el_ReceiptLineList = ReceiptLineListInDoc
					.createElement("ReceiptLine");
			ReceiptLineListInDoc.appendChild(el_ReceiptLineList);
			el_ReceiptLineList.setAttribute("OrderNo", OrderNo);
			el_ReceiptLineList.setAttribute("ReceiptLineKey", ReceiptLineKey);
			ReceiptLineListOutDoc = CommonUtilities.invokeAPI(env,
					"getReceiptLineList", ReceiptLineListInDoc);
			/*
			 * 
			 * NEED TO INSERT CODE TO GRAB ORDER_LINE_KEY FOR BILLING_TRANSACTION_LIST_SERVICE
			 */
			Element elemReceiptLineRoot = ReceiptLineListOutDoc
					.getDocumentElement();
			NodeList rllSoureNL = elemReceiptLineRoot
					.getElementsByTagName("OrderLine");
			int nrllLength = rllSoureNL.getLength();
			for (int i = 0; i < nlLength; i++) {
				Element rllElm = (Element) rllSoureNL.item(i);
				OrderLineKey = rllElm.getAttribute("OrderLineKey");
			}
			Document BillingInDoc = XMLUtil.newDocument();
			Document BillingOutDoc = XMLUtil.newDocument();
			Element el_NWCGBillingTrans = BillingInDoc
					.createElement("NWCGBillingTransaction");
			BillingInDoc.appendChild(el_NWCGBillingTrans);
			el_NWCGBillingTrans.setAttribute("TransactionNo", OrderNo);
			el_NWCGBillingTrans.setAttribute("ItemID", ItemID);
			el_NWCGBillingTrans.setAttribute("TransLineKey", OrderLineKey);
			BillingOutDoc = CommonUtilities.invokeService(env,
					NWCGConstants.NWCG_GET_BILLING_TRANSACTION_LIST_SERVICE,
					BillingInDoc);
			NodeList BillingTransaction = BillingOutDoc.getDocumentElement()
					.getElementsByTagName("NWCGBillingTransaction");
			int BillingTransactionCount = BillingTransaction.getLength();
			if (BillingTransactionCount > 0) {
				for (int cnt = 0; cnt < BillingTransactionCount; cnt++) {
					Element BillingTransactionl = (Element) BillingTransaction
							.item(cnt);
					SequenceKey = BillingTransactionl
							.getAttribute("SequenceKey");
					TransQty = BillingTransactionl.getAttribute("TransQty");
					UnitCost = BillingTransactionl.getAttribute("UnitCost");
					double dblUnreceiveQuantity = Double
							.parseDouble(UnreceiveQuantity);
					double dblTransQty = Double.parseDouble(TransQty);
					double dblUnitCost = Double.parseDouble(UnitCost);
					double dblAdjustedQty = 0;
					double dblAdjustedAmount = 0;
					dblAdjustedQty = (dblTransQty - dblUnreceiveQuantity);
					String strAdjustedQty = Double.toString(dblAdjustedQty);
					dblAdjustedAmount = (dblAdjustedQty * dblUnitCost);
					String strAdjustedAmount = Double
							.toString(dblAdjustedAmount);
					UpdateBillingTransaction(env, OrderNo, strAdjustedQty,
							strAdjustedAmount, SequenceKey);
				}
			}
		} catch (Exception e) {
			//Jimmy - added itemid and ordernumber to alerts.
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingTransChangeOrder::updateBillingTransactionRecord Caught Exception "
								+ e);
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransChangeOrder' "
							+ "DetailDescription='UpdateBillingTransactionRecord Failed Failed on ItemId: "
							+ tempItemID
							+ ", for OrderNo: "
							+ tempOrderNo
							+ " . While in API: NWCGProcessBillingAdjustReceipt'");
			throwAlert(env, stbuf);
		}

		return inXML;
	}

	public Document insertBillingTransRecord(YFSEnvironment env, Document doc)
			throws Exception {
		return adjustReceipt(env, doc);
	}

	public void UpdateBillingTransaction(YFSEnvironment env, String TransNo,
			String updatedTransQty, String updatedTransAmount,
			String SequenceKey) throws Exception {
		Document BillDoc = XMLUtil.createDocument("NWCGBillingTransaction");
		Element BillDocElem = BillDoc.getDocumentElement();
		//NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
		//String LastExtractDate = reportsUtil.dateToString(new java.util.Date(),NWCGConstants.YANTRA_DATE_FORMAT);
		BillDocElem.setAttribute("SequenceKey", SequenceKey);
		BillDocElem.setAttribute("TransactionNo", TransNo);
		BillDocElem.setAttribute("TransAmount", updatedTransAmount);
		BillDocElem.setAttribute("TransQty", updatedTransQty);
		try {
			CommonUtilities.invokeService(env,
					NWCGConstants.NWCG_UPDATE_BILLING_TRANSACTION_SERVICE,
					BillDoc);
		} catch (Exception e) {
			//Jimmy-Original throw alert commented out
			/*	  if(logger.isVerboseEnabled()) 
			 logger.verbose("NWCGProcessBillingExtract::updateBillingTransactionRecord Caught Exception "+e);
			 e.printStackTrace();
			 StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransactionExtractFTP' "
			 + " Extract Transaction No : " + TransNo + "'");
			 throwAlert(env,stbuf);*/
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingExtract::updateBillingTransactionRecord Caught Exception "
								+ e);
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransactionExtractFTP' "
							+ " DetailDescription='Error while UpdateBillingTransaction within API NWCGProcessBillingAdjustReciept, on Transaction No : "
							+ TransNo + "'");
			throwAlert(env, stbuf);

		}
		//}
	}
}