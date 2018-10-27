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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.reports.loftware.NWCGReportsUtil;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGProcessBillingTransactionReExtract implements YIFCustomApi {

	int blankcnt, lengthcnt, totextcnt = 0;

	double Total_File_Amt = 0.00;
	
	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGProcessBillingTransactionReExtract.class);

	public void setProperties(Properties arg0) throws Exception {
	}

	public void throwAlert(YFSEnvironment env, StringBuffer Message)
			throws Exception {
		Message.append(" ExceptionType='BILLING_TRANS_EXTRACT'"
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

	public Document processExtract(YFSEnvironment env, Document inXML)
			throws Exception {
		if (logger.isVerboseEnabled()) {
			logger.verbose("Entering NWCGProcessBillingTransactionReExtractFTP, Input document is:"
					+ XMLUtil.getXMLString(inXML));
		}

		return inXML;
	}

	public PrintStream Set_OutFile(YFSEnvironment env, String outfile)
			throws Exception {
		FileOutputStream out = null;
		PrintStream ps;
		try {
			out = new FileOutputStream(outfile, true);
		} catch (FileNotFoundException e) {
			// Jimmy added api name to alert
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransactionReExtractFTP' "
							+ "DetailDescription='Error in Opening File : "
							+ outfile
							+ ", inside API: NWCGProcessBillingTransactionReExtract'");
			throwAlert(env, stbuf);
			logger.error("!!!!! Caught FileNotFoundException, NWCGProcessBillingTransactionReExtractFTP::Error in Opening File :: " + outfile);
		}
		ps = new PrintStream(out);
		return ps;
	}

	public void InsertBillingTransactionExtract(YFSEnvironment env,
			String BusinessArea, String InterfaceType, String DocumentDate,
			String PostingDate, String FiscalPeriod,
			String ReferenceDocumentNumber, String DocumentHeaderText,
			String PostingKey, String GLAcctCode, String AmtDocumentCurrency,
			String ItemText, String CompanyID, String CostCenter,
			String OrderNo, String CommitmentItem, String WBS,
			String FundsCenter, String Fund, String DocNoForEarmarkedFunds,
			String EarmarkedFundsDocumentNo, String FunctionalArea,
			String TransNo, String DocumentNo, String FiscalYear,
			String TransAmount, String SequenceKey, String extractFileName,
			String CacheId, String PMSTransAmount) throws Exception {
		Document BillDoc = XMLUtil.createDocument("NWCGBillingTransExtract");
		Element BillDocElem = BillDoc.getDocumentElement();
		BillDocElem.setAttribute("BusinessArea", BusinessArea);
		BillDocElem.setAttribute("InterfaceType", InterfaceType);
		BillDocElem.setAttribute("DocumentDate", DocumentDate);
		BillDocElem.setAttribute("PostingDate", PostingDate);
		BillDocElem.setAttribute("ReferenceDocNumber", ReferenceDocumentNumber);
		BillDocElem.setAttribute("DocHeaderText", DocumentHeaderText);
		BillDocElem.setAttribute("PostingKey", PostingKey);
		BillDocElem.setAttribute("GLAccountCode", GLAcctCode);
		BillDocElem.setAttribute("AmtDocumentCurrency", AmtDocumentCurrency);
		BillDocElem.setAttribute("ItemText", ItemText);
		BillDocElem.setAttribute("TradingPartnerCompanyId", CompanyID);
		BillDocElem.setAttribute("CostCenter", CostCenter);
		BillDocElem.setAttribute("OrderNum", OrderNo);
		BillDocElem.setAttribute("CommitmentItem", CommitmentItem);
		BillDocElem.setAttribute("WBS", WBS);
		BillDocElem.setAttribute("FundsCenter", FundsCenter);
		BillDocElem.setAttribute("Fund", Fund);
		BillDocElem.setAttribute("DocNumForEarmarkedFunds",
				DocNoForEarmarkedFunds);
		BillDocElem.setAttribute("EarmarkedFundsDocItem",
				EarmarkedFundsDocumentNo);
		BillDocElem.setAttribute("FunctionalArea", FunctionalArea);
		BillDocElem.setAttribute("ExtractTransNo", TransNo);
		BillDocElem.setAttribute("DocNumForEarmarkedFunds", DocumentNo);
		BillDocElem.setAttribute("FiscalYear", FiscalYear);
		BillDocElem.setAttribute("AmtInDocCurrency", TransAmount);
		BillDocElem.setAttribute("ExtractFileName", extractFileName);
		BillDocElem.setAttribute("PMSTransAmount", PMSTransAmount);
		BillDocElem.setAttribute("CacheId", CacheId);
		try {
			CommonUtilities
					.invokeService(
							env,
							NWCGConstants.NWCG_CREATE_BILLING_TRANSACTION_EXTRACT_SERVICE,
							BillDoc);
			/*
			 * NWCG_CREATE_BILLING_TRANSACTION_EXTRACT_SERVICE xmlDoc:
			 * <NWCGBillingTransExtract AmtDocumentCurrency="000000005.09"
			 * AmtInDocCurrency="-5.09" BusinessArea="L000" CommitmentItem=""
			 * CostCenter="LL11111111" DocHeaderText=""
			 * DocNumForEarmarkedFunds="CORMK" DocumentDate="20091111"
			 * EarmarkedFundsDocItem="" ExtractTransNo="200911101439203794943"
			 * FiscalYear="2009" FunctionalArea="LF6900000.111111" Fund=""
			 * FundsCenter="" GLAccountCode="6100.264B0" InterfaceType="NF"
			 * ItemText="L322N091102" OrderNum="" PostingDate="20091111"
			 * PostingKey="50" ReferenceDocNumber=""
			 * TradingPartnerCompanyId="1400" WBS="L12344567789"/>
			 */
			UpdateBillingTransaction(env, TransNo, FiscalYear, SequenceKey);
		} catch (Exception e) {// Jimmy - Added details to the throw alert
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransactionReExtractFTP' "
							+ "DetailDescription='Error in Inserting NWCG_BILLING_TRANSACTION_EXTRACT Record, during NWCGProcessBillingTransactionReExtract.  For ExtractTransaction No: "
							+ TransNo + " , CacheID: " + CacheId
							+ " , Document number: " + DocumentNo + " '"
							+ "OrderNo='" + OrderNo + "'");
			throwAlert(env, stbuf);
			logger.error("!!!!! Caught General Exception, NWCGProcessBillingTransactionReExtractFTP::Error in Inserting NWCG_BILLING_TRANSACTION_EXTRACT Record for :: " + e);
		}
	}

	public void UpdateBillingTransaction(YFSEnvironment env, String TransNo,
			String FiscalYear, String SequenceKey) throws Exception {
		Document BillDoc = XMLUtil.createDocument("NWCGBillingTransaction");
		Element BillDocElem = BillDoc.getDocumentElement();
		NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
		String LastExtractDate = reportsUtil.dateToString(new java.util.Date(),
				NWCGConstants.YANTRA_DATE_FORMAT);
		// for (int i=0;i<rcnt;i++)
		// {
		// String SequenceKey = SeqKeys[i];
		BillDocElem.setAttribute("IsExtracted", "Y");
		BillDocElem.setAttribute("SequenceKey", SequenceKey);
		BillDocElem.setAttribute("ExtractTransNo", TransNo);
		BillDocElem.setAttribute("TransactionFiscalYear", FiscalYear);
		BillDocElem.setAttribute("LastExtractDate", LastExtractDate);
		try {
			// CommonUtilities.invokeService(env,NWCGConstants.NWCG_UPDATE_BILLING_TRANSACTION_SERVICE,BillDoc);
		} catch (Exception e) {// Jimmy - added detailed description
			logger.error("!!!!! Caught General Exception, NWCGProcessBillingExtract::updateBillingTransactionRecord :: " + e);
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransactionReExtractFTP' "
							// + " Extract Transaction No : " + TransNo + "'");
							+ " DetailDescription=' UpdateBillingTransaction method failed on Transaction No : "
							+ TransNo + "'");
			throwAlert(env, stbuf);
		}
		// }
	}
}