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
import java.io.*;

import org.w3c.dom.*;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.reports.loftware.NWCGReportsUtil;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class NWCGProcessBillingTransExtract implements YIFCustomApi,
		NWCGBillingTransRecordMutator {

	private static Logger logger = Logger
			.getLogger(NWCGProcessBillingTransConfirmShipment.class.getName());

	int blankcnt, lengthcnt, totextcnt = 0;

	double Total_File_Amt = 0.00;

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

	public Document InsertRecord(YFSEnvironment env, Document inXML)
			throws Exception {
		if (logger.isVerboseEnabled()) {
			logger
					.verbose("Entering NWCGProcessBillingTransExtract, Input document is:"
							+ XMLUtil.getXMLString(inXML));
		}
		Element elemRoot = null;
		//jimmy temp vars for alerts
		String tempCacheId = "";
		String tempFileName = "";
		String tempDocumentNo = "";
		try {
			elemRoot = inXML.getDocumentElement();
			if (elemRoot != null) {
				String CacheId = elemRoot.getAttribute("CacheID");
				String FromDate = elemRoot.getAttribute("FromTransDate");
				String ToDate = elemRoot.getAttribute("ToTransDate");
				String TempAcctCode = "";
				String SeqNoStr = "";
				String AcctCode = "";
				String TNo = "";
				double AcctTotAmt = 0.00, TransAmt = 0.00, PMSTotAmt = 0.00;
				int SeqNo = 1, LineCnt = 0, rcnt = 0;
				String[] SeqKeys = new String[2000];
				String PrimaryEnterpriseKey = elemRoot
						.getAttribute("PrimaryEnterpriseKey");
				String OwnerAgency = elemRoot.getAttribute("OwnerAgency");
				String DocumentNo = elemRoot.getAttribute("DocumentNo");
				String FiscalYear = elemRoot.getAttribute("FiscalYear");
				String FileName = elemRoot.getAttribute("BillingTransFileName");
				String CurrentSeqNo = elemRoot.getAttribute("CurrentSeqNo");
				String OffsetAcctCode = elemRoot.getAttribute("OffsetAcctCode");
				String[] SeqList = CurrentSeqNo.split("-");
				//jimmy populating temp vars
				tempCacheId = CacheId;
				tempFileName = FileName;
				tempDocumentNo = DocumentNo;
				String AcctCodeName = "";
				if (OwnerAgency.equals("BLM")) {
					AcctCodeName = "IncidentBlmAcctCode";
				} else if (OwnerAgency.equals("FS")) {
					AcctCodeName = "IncidentFsAcctCode";
				} else {
					AcctCodeName = "IncidentOtherAcctCode";
				}
				Document BillingInDoc = XMLUtil.newDocument();
				Document BillingOutDoc = XMLUtil.newDocument();
				String[] ToDtFields = ToDate.split("/");
				String ToMonth = ToDtFields[0];
				String ToDay = ToDtFields[1];
				String ToYear = ToDtFields[2];
				if (SeqList[0].equals(ToMonth + ToYear)) {
					SeqNo = Integer.parseInt(SeqList[1]) + 1;
				}
				String OutFileName = NWCGConstants.NWCG_BLM_OUTPUT_DIR
						+ FileName;
				String ExtFilename = FileName + "." + ToMonth + ToYear;
				PrintStream p;
				p = Set_OutFile(env, OutFileName);
				Element el_NWCGBillingTrans = BillingInDoc
						.createElement("NWCGBillingTransaction");
				BillingInDoc.appendChild(el_NWCGBillingTrans);
				el_NWCGBillingTrans.setAttribute("CacheId", CacheId);
				if (FromDate.length() > 0) {
					String[] FromDtFields = FromDate.split("/");
					String FromMonth = FromDtFields[0];
					String FromDay = FromDtFields[1];
					String FromYear = FromDtFields[2];
					el_NWCGBillingTrans.setAttribute("TransDateQryType",
							"BETWEEN");
					el_NWCGBillingTrans.setAttribute("FromTransDate", FromYear
							+ FromMonth + FromDay);
					el_NWCGBillingTrans.setAttribute("ToTransDate", ToYear
							+ ToMonth + ToDay);
				} else {
					el_NWCGBillingTrans.setAttribute("TransDateQryType", "LE");
					el_NWCGBillingTrans.setAttribute("TransDate", ToYear
							+ ToMonth + ToDay);
				}
				el_NWCGBillingTrans.setAttribute("IsExtracted", "N");
				el_NWCGBillingTrans.setAttribute("IsReviewed", "Y");
				Element el_OrderBy = BillingInDoc.createElement("OrderBy");
				el_NWCGBillingTrans.appendChild(el_OrderBy);
				Element el_Attribute = BillingInDoc.createElement("Attribute");
				el_OrderBy.appendChild(el_Attribute);
				el_Attribute.setAttribute("Name", AcctCodeName);
				BillingOutDoc = CommonUtilities
						.invokeService(
								env,
								NWCGConstants.NWCG_GET_BILLING_TRANSACTION_LIST_SERVICE,
								BillingInDoc);
				BillingOutDoc = ValidateAcctCode(env, BillingOutDoc,
						OwnerAgency, AcctCodeName);
				NodeList BList = BillingOutDoc.getDocumentElement()
						.getElementsByTagName("NWCGBillingTransaction");
				for (int j = 0; j < BList.getLength(); j++) {
					Element curBillingList = (Element) BList.item(j);
					String TransactionNo = curBillingList
							.getAttribute("TransactionNo");
					String TransDate = curBillingList.getAttribute("TransDate");
					String ItemProductLine = curBillingList
							.getAttribute("ItemProductLine");
					if (OwnerAgency.equals("BLM")) {
						AcctCode = curBillingList
								.getAttribute("IncidentBlmAcctCode");
					} else if (OwnerAgency.equals("FS")) {
						AcctCode = curBillingList
								.getAttribute("IncidentFsAcctCode");
					} else {
						AcctCode = curBillingList
								.getAttribute("IncidentOtherAcctCode");
					}
					String TransAmount = curBillingList
							.getAttribute("TransAmount");
					TransAmt = Double.parseDouble(TransAmount);
					if (j == 0) {
						TempAcctCode = AcctCode;
					}
					if (!AcctCode.equals(TempAcctCode) && (AcctTotAmt != 0)) {
						if (LineCnt == 25) {
							SeqNo++;
							LineCnt = 0;
						}
						LineCnt++;
						if (SeqNo < 10) {
							SeqNoStr = "0" + Integer.toString(SeqNo);
						} else {
							SeqNoStr = Integer.toString(SeqNo);
						}
						String[] AcctCodeArr = TempAcctCode.split("-");
						Total_File_Amt += AcctTotAmt;
						String AcctTotAmtStr = FormatAmtToString(AcctTotAmt);
						p.print(DocumentNo);
						p.print(FiscalYear.substring(2));
						p.print(ToMonth);
						p.print(SeqNoStr);
						p.print(AcctCodeArr[0]);
						p.print(AcctCodeArr[1]);
						p.print(FiscalYear);
						p.print(AcctCodeArr[2]);
						p.print(AcctCodeArr[3]);
						p.print(AcctCodeArr[4]);
						p.print(AcctCodeArr[5]);
						p.print(AcctCodeArr[6]);
						p.println(AcctTotAmtStr);
						totextcnt++;
						NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
						TNo = reportsUtil.dateToString(new java.util.Date(),
								"yyyyMMddhhmmssSS");
						//Insert into NWCG_BILLING_TRANSACTION_EXTRACT
						InsertBillingTransactionExtract(env, TNo, rcnt,
								CacheId, DocumentNo, FiscalYear, ToMonth,
								SeqNoStr, TempAcctCode, AcctTotAmt,
								ExtFilename, PMSTotAmt);
						//Update NWCG_BILLING_TRANSACTION
						UpdateBillingTransaction(env, TNo, SeqKeys, rcnt,
								DocumentNo, FiscalYear);
						SeqKeys = new String[2000];
						rcnt = 0;
						AcctTotAmt = 0.00;
						PMSTotAmt = 0.00;
					}
					TempAcctCode = AcctCode;
					AcctTotAmt += TransAmt;
					if (ItemProductLine.equals("Publications")) {
						PMSTotAmt += TransAmt;
					}
					String SequenceKey = curBillingList
							.getAttribute("SequenceKey");
					SeqKeys[rcnt] = SequenceKey;
					rcnt++;
				}
				if (LineCnt == 25) {
					SeqNo++;
					LineCnt = 0;
				}
				LineCnt++;
				if (SeqNo < 10) {
					SeqNoStr = "0" + Integer.toString(SeqNo);
				} else

				{
					SeqNoStr = Integer.toString(SeqNo);
				}
				if (rcnt > 0 && AcctTotAmt != 0) {
					String[] AcctCodeArr = TempAcctCode.split("-");
					String AcctTotAmtStr = FormatAmtToString(AcctTotAmt);
					Total_File_Amt += AcctTotAmt;
					Total_File_Amt = Total_File_Amt * -1;
					String TotFileAmtStr = FormatAmtToString(Total_File_Amt);
					NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
					TNo = reportsUtil.dateToString(new java.util.Date(),
							"yyyyMMddhhmmssSS");
					p.print(DocumentNo);
					p.print(FiscalYear.substring(2));
					p.print(ToMonth);
					p.print(SeqNoStr);
					p.print(AcctCodeArr[0]);
					p.print(AcctCodeArr[1]);
					p.print(FiscalYear);
					p.print(AcctCodeArr[2]);
					p.print(AcctCodeArr[3]);
					p.print(AcctCodeArr[4]);
					p.print(AcctCodeArr[5]);
					p.print(AcctCodeArr[6]);
					p.println(AcctTotAmtStr);
					if (LineCnt == 25) {
						SeqNo++;
						LineCnt = 0;
					}
					LineCnt++;
					if (SeqNo < 10) {
						SeqNoStr = "0" + Integer.toString(SeqNo);
					} else {
						SeqNoStr = Integer.toString(SeqNo);
					}
					String[] OffsetAcctArr = OffsetAcctCode.split("-");
					p.print(DocumentNo);
					p.print(FiscalYear.substring(2));
					p.print(ToMonth);
					p.print(SeqNoStr);
					p.print(OffsetAcctArr[0]);
					p.print(OffsetAcctArr[1]);
					p.print(FiscalYear);
					p.print(OffsetAcctArr[2]);
					p.print(OffsetAcctArr[3]);
					p.print(OffsetAcctArr[4]);
					p.print(OffsetAcctArr[5]);
					p.print(OffsetAcctArr[6]);
					p.println(TotFileAmtStr);

					totextcnt++;
					InsertBillingTransactionExtract(env, TNo, rcnt, CacheId,
							DocumentNo, FiscalYear, ToMonth, SeqNoStr,
							TempAcctCode, AcctTotAmt, ExtFilename, PMSTotAmt);
					UpdateBillingTransaction(env, TNo, SeqKeys, rcnt,
							DocumentNo, FiscalYear);
					UpdateOrganization(env, PrimaryEnterpriseKey, CacheId,
							SeqNoStr, ToMonth, ToYear);
				}
			}
		} catch (YFSException e) {
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingExtract::Extract Record Caught Exception "
								+ e);
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransExtract' "
							+ "DetailDescription='Billing Transaction Extract Failed in API: NWCGProcessBillingTransExtract process, Cache ID: "
							+ tempCacheId + ", FileName: " + tempFileName
							+ ", Document No: " + tempDocumentNo + "'");
			throwAlert(env, stbuf);
		}
		Document BillDoc = XMLUtil.createDocument("NWCGBillingTransaction");
		Element BillDocElem = BillDoc.getDocumentElement();
		BillDocElem.setAttribute("TotalExtractCount", Integer
				.toString(totextcnt));
		BillDocElem.setAttribute("BlankCount", Integer.toString(blankcnt));
		BillDocElem.setAttribute("LengthCount", Integer.toString(lengthcnt));
		BillDocElem.setAttribute("AfterExtract", "Y");
		/* if (totextcnt > 0 && blankcnt == 0 && lengthcnt == 0 )
		 {
		 throw new NWCGException("NWCG_BILLING_TRANS_EXTRACT_001",new Object[] {Integer.toString(totextcnt)});
		 }
		 if (totextcnt > 0 || blankcnt > 0 || lengthcnt > 0 )
		 {
		 throw new NWCGException("NWCG_BILLING_TRANS_EXTRACT_002",new Object[] {Integer.toString(totextcnt),
		 Integer.toString(blankcnt),Integer.toString(lengthcnt)});
		 }
		 */
		return BillDoc;
	}

	public Document insertBillingTransRecord(YFSEnvironment env, Document doc)
			throws Exception {
		return InsertRecord(env, doc);
	}

	public PrintStream Set_OutFile(YFSEnvironment env, String outfile)
			throws Exception {
		FileOutputStream out = null;
		PrintStream ps;
		try {
			out = new FileOutputStream(outfile, true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransExtract' "
							+ "DetailDescription='Error in Opening File : "
							+ outfile
							+ ", during NWCGProcessBillingTransExtract process'");
			throwAlert(env, stbuf);
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingTransExtract::Error in Opening File  "
								+ outfile);
		}
		ps = new PrintStream(out);
		return ps;
	}

	public void UpdateBillingTransaction(YFSEnvironment env, String TransNo,
			String[] SeqKeys, int rcnt, String DocumentNo, String FiscalYear)
			throws Exception {
		Document BillDoc = XMLUtil.createDocument("NWCGBillingTransaction");
		Element BillDocElem = BillDoc.getDocumentElement();
		NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
		String LastExtractDate = reportsUtil.dateToString(new java.util.Date(),
				NWCGConstants.YANTRA_DATE_FORMAT);
		for (int i = 0; i < rcnt; i++) {
			String SequenceKey = SeqKeys[i];
			BillDocElem.setAttribute("IsExtracted", "Y");
			BillDocElem.setAttribute("SequenceKey", SequenceKey);
			BillDocElem.setAttribute("ExtractTransNo", TransNo);
			BillDocElem.setAttribute("LastExtractDate", LastExtractDate);
			BillDocElem.setAttribute("DocumentNo", DocumentNo);
			BillDocElem.setAttribute("TransactionFiscalYear", FiscalYear);
			try {
				CommonUtilities.invokeService(env,
						NWCGConstants.NWCG_UPDATE_BILLING_TRANSACTION_SERVICE,
						BillDoc);
			} catch (Exception e) {
				if (logger.isVerboseEnabled())
					logger
							.verbose("NWCGProcessBillingExtract::updateBillingTransactionRecord Caught Exception "
									+ e);
				e.printStackTrace();
				StringBuffer stbuf = new StringBuffer(
						"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransExtract' "
								+ "DetailDescription='UpdateBillingTransaction Failed during NWCGProcessBillingTransExtract, for SequenceKey : "
								+ SequenceKey + " Extract Transaction No : "
								+ TransNo + "'");
				throwAlert(env, stbuf);
			}
		}
	}

	public void InsertBillingTransactionExtract(YFSEnvironment env,
			String TransNo, int rcnt, String CacheId, String DocNo,
			String FYear, String ToMonth, String SeqNoStr, String AcctCode,
			double Amt, String ExtFileName, double PMSTotAmt) throws Exception {
		Document BillDoc = XMLUtil.createDocument("NWCGBillingTransExtract");
		Element BillDocElem = BillDoc.getDocumentElement();
		String[] AcctCodeArr = AcctCode.split("-");
		BillDocElem.setAttribute("CacheId", CacheId);
		BillDocElem.setAttribute("ExtractTransNo", TransNo);
		BillDocElem.setAttribute("DocumentNo", DocNo);
		BillDocElem.setAttribute("FiscalYear1", FYear.substring(2));
		BillDocElem.setAttribute("CalendarMonth", ToMonth);
		BillDocElem.setAttribute("SequenceNo", SeqNoStr);
		BillDocElem.setAttribute("State", AcctCodeArr[0]);
		BillDocElem.setAttribute("Office", AcctCodeArr[1]);
		BillDocElem.setAttribute("FiscalYear2", FYear);
		BillDocElem.setAttribute("FundCode", AcctCodeArr[2]);
		BillDocElem.setAttribute("ActivityCode", AcctCodeArr[3]);
		BillDocElem.setAttribute("ProgramElement", AcctCodeArr[4]);
		BillDocElem.setAttribute("ProjectCode", AcctCodeArr[5]);
		BillDocElem.setAttribute("Object", AcctCodeArr[6]);
		String AmtStr = Double.toString(Amt);
		BillDocElem.setAttribute("TransAmount", AmtStr);
		BillDocElem.setAttribute("ExtractFileName", ExtFileName);
		BillDocElem.setAttribute("AcctCode", AcctCode);
		String RCntStr = Integer.toString(rcnt);
		String PMSAmtStr = Double.toString(PMSTotAmt);
		BillDocElem.setAttribute("TotalRecords", RCntStr);
		BillDocElem.setAttribute("PMSTransAmount", PMSAmtStr);
		//DocumentNo
		//FiscalYear1
		//CalendarMonth
		//SequenceNo
		//State
		//Office
		//FiscalYear2
		//FundCode
		//ActivityCode
		//ProgramElement
		//ProjectCode
		//Object
		//TransAmount
		//ExtractFileName
		//ExtractTransNo
		try {
			CommonUtilities
					.invokeService(
							env,
							NWCGConstants.NWCG_CREATE_BILLING_TRANSACTION_EXTRACT_SERVICE,
							BillDoc);
		} catch (Exception e) {
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransExtract' "
							+ "DetailDescription='Error in Inserting NWCG_BILLING_TRANSACTION_EXTRACT Record for "
							+ "Extract Trans No : " + TransNo
							+ "Extract File Name : " + ExtFileName
							+ ", Cache ID: " + CacheId + "'");
			throwAlert(env, stbuf);
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingTransExtract::Error in Inserting NWCG_BILLING_TRANSACTION_EXTRACT Record for "
								+ "Extract Trans No : "
								+ TransNo
								+ "Extract File Name : " + ExtFileName + "'");
		}
	}

	public void UpdateOrganization(YFSEnvironment env,
			String PrimaryEnterpriseKey, String CacheId, String SeqNoStr,
			String ToMonth, String ToYear) throws Exception {
		Document inDoc = XMLUtil.newDocument();
		Element el_NWCGOrganization = inDoc.createElement("Organization");
		inDoc.appendChild(el_NWCGOrganization);
		el_NWCGOrganization.setAttribute("OrganizationCode", CacheId);
		el_NWCGOrganization.setAttribute("PrimaryEnterpriseKey",
				PrimaryEnterpriseKey);
		Element el_Extn = inDoc.createElement("Extn");
		el_NWCGOrganization.appendChild(el_Extn);
		el_Extn.setAttribute("ExtnCurrentSeqNo", ToMonth + ToYear + "-"
				+ SeqNoStr);
		try {
			CommonUtilities
					.invokeAPI(env, "modifyOrganizationHierarchy", inDoc);
		} catch (Exception e) {
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingExtract::updateOrganizationHierarchy Caught Exception "
								+ e);
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransExtract' "
							+ "DetailDescription='UpdateOrganizationHierarchy Failed during NWCGProcessBillingTransExtract, for OrganizationCode : "
							+ CacheId + "ExtnCurrentSeqNo : " + ToMonth
							+ ToYear + "-" + SeqNoStr + "'");
			throwAlert(env, stbuf);
		}
	}

	public Document ValidateAcctCode(YFSEnvironment env, Document BillDoc,
			String OwnerAgency, String AcctCodeName) throws Exception {
		Document OutBillDoc = XMLUtil.newDocument();
		Element root = OutBillDoc.createElement("NWCGBillingTransactionList");
		OutBillDoc.appendChild(root);
		NodeList BList = BillDoc.getDocumentElement().getElementsByTagName(
				"NWCGBillingTransaction");
		for (int j = 0; j < BList.getLength(); j++) {
			Element curBillingList = (Element) BList.item(j);
			String TransactionNo = curBillingList.getAttribute("TransactionNo");
			String TransDate = curBillingList.getAttribute("TransDate");
			String AcctCode = "";
			if (OwnerAgency.equals("BLM")) {
				AcctCode = curBillingList.getAttribute("IncidentBlmAcctCode");
			} else if (OwnerAgency.equals("FS")) {
				AcctCode = curBillingList.getAttribute("IncidentFsAcctCode");
			} else {
				AcctCode = curBillingList.getAttribute("IncidentOtherAcctCode");
			}
			String[] AcctArr = AcctCode.split("-");
			if (AcctCode.length() == 0) {
				if (logger.isVerboseEnabled())
					logger
							.verbose("NWCGProcessBillingExtract::Account Code is Blank ");
				StringBuffer stbuf = new StringBuffer(
						"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransExtract' "
								+ "DetailDescription= '"
								+ AcctCodeName
								+ " is Blank for Transaction No : "
								+ TransactionNo
								+ ", Transaction Date : "
								+ TransDate
								+ "  This record is not Extracted !!! Ocurred during NWCGProcessBillingTransExtract process'");
				throwAlert(env, stbuf);
				blankcnt++;
				continue;
			}
			if (AcctCode.length() < 28) {
				if (logger.isVerboseEnabled())
					logger
							.verbose("NWCGProcessBillingExtract::Account Code is less than 21 characters ");
				StringBuffer stbuf = new StringBuffer(
						"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransExtract' "
								+ "DetailDescription= '"
								+ AcctCodeName
								+ " : "
								+ AcctCode
								+ " is less than 21 Characters for Transaction No : "
								+ TransactionNo
								+ ", Transaction Date : "
								+ TransDate
								+ "  This record is not Extracted !!!  Ocurred during NWCGProcessBillingTransExtract process'");
				throwAlert(env, stbuf);
				lengthcnt++;
				continue;
			}
			if (AcctArr.length < 7) {
				if (logger.isVerboseEnabled())
					logger
							.verbose("NWCGProcessBillingExtract::Account Code is not formatted correctly ");
				StringBuffer stbuf = new StringBuffer(
						"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransExtract' "
								+ "DetailDescription= '"
								+ AcctCodeName
								+ " : "
								+ AcctCode
								+ " is not formatted correct for Transaction No : "
								+ TransactionNo
								+ ", Transaction Date : "
								+ TransDate
								+ "  This record is not Extracted !!!  Ocurred during NWCGProcessBillingTransExtract process'");
				throwAlert(env, stbuf);
				lengthcnt++;
				continue;
			}
			Element BillDocElem = OutBillDoc
					.createElement("NWCGBillingTransaction");
			XMLUtil.copyElement(OutBillDoc, curBillingList, BillDocElem);
			root.appendChild(BillDocElem);
		}
		return OutBillDoc;
	}

	public String FormatAmtToString(double Amt) throws Exception {
		String AmtStr = "";
		String IsNegative = "N";
		String LastVal1 = "0", LastVal2 = "0";
		String[] AmtFields = new String[3];
		if (Amt < 0) {
			IsNegative = "Y";
			Amt = Amt * -1;
		}
		AmtStr = Double.toString(Amt);
		AmtFields = AmtStr.split("\\.");
		if (AmtFields[1].length() > 0) {
			LastVal1 = AmtFields[1].substring(0, 1);
			LastVal2 = AmtFields[1].substring(1);
		}
		if (IsNegative.equals("Y")) {
			if (LastVal2.equals("0"))
				LastVal2 = NWCGConstants.BILL_TRANS_EXTRACT_NEGATIVE_ZERO;
			if (LastVal2.equals("1"))
				LastVal2 = NWCGConstants.BILL_TRANS_EXTRACT_NEGATIVE_ONE;
			if (LastVal2.equals("2"))
				LastVal2 = NWCGConstants.BILL_TRANS_EXTRACT_NEGATIVE_TWO;
			if (LastVal2.equals("3"))
				LastVal2 = NWCGConstants.BILL_TRANS_EXTRACT_NEGATIVE_THREE;
			if (LastVal2.equals("4"))
				LastVal2 = NWCGConstants.BILL_TRANS_EXTRACT_NEGATIVE_FOUR;
			if (LastVal2.equals("5"))
				LastVal2 = NWCGConstants.BILL_TRANS_EXTRACT_NEGATIVE_FIVE;
			if (LastVal2.equals("6"))
				LastVal2 = NWCGConstants.BILL_TRANS_EXTRACT_NEGATIVE_SIX;
			if (LastVal2.equals("7"))
				LastVal2 = NWCGConstants.BILL_TRANS_EXTRACT_NEGATIVE_SEVEN;
			if (LastVal2.equals("8"))
				LastVal2 = NWCGConstants.BILL_TRANS_EXTRACT_NEGATIVE_EIGHT;
			if (LastVal2.equals("9"))
				LastVal2 = NWCGConstants.BILL_TRANS_EXTRACT_NEGATIVE_NINE;
		} else {
			if (LastVal2.equals("0"))
				LastVal2 = NWCGConstants.BILL_TRANS_EXTRACT_POSITIVE_ZERO;
			if (LastVal2.equals("1"))
				LastVal2 = NWCGConstants.BILL_TRANS_EXTRACT_POSITIVE_ONE;
			if (LastVal2.equals("2"))
				LastVal2 = NWCGConstants.BILL_TRANS_EXTRACT_POSITIVE_TWO;
			if (LastVal2.equals("3"))
				LastVal2 = NWCGConstants.BILL_TRANS_EXTRACT_POSITIVE_THREE;
			if (LastVal2.equals("4"))
				LastVal2 = NWCGConstants.BILL_TRANS_EXTRACT_POSITIVE_FOUR;
			if (LastVal2.equals("5"))
				LastVal2 = NWCGConstants.BILL_TRANS_EXTRACT_POSITIVE_FIVE;
			if (LastVal2.equals("6"))
				LastVal2 = NWCGConstants.BILL_TRANS_EXTRACT_POSITIVE_SIX;
			if (LastVal2.equals("7"))
				LastVal2 = NWCGConstants.BILL_TRANS_EXTRACT_POSITIVE_SEVEN;
			if (LastVal2.equals("8"))
				LastVal2 = NWCGConstants.BILL_TRANS_EXTRACT_POSITIVE_EIGHT;
			if (LastVal2.equals("9"))
				LastVal2 = NWCGConstants.BILL_TRANS_EXTRACT_POSITIVE_NINE;
		}
		AmtStr = AmtFields[0] + LastVal1 + LastVal2;
		AmtStr = StringUtil.prepadStringWithZeros(AmtStr,
				NWCGConstants.BILL_TRANS_AMOUNT_LENGTH);
		return AmtStr;
	}
}