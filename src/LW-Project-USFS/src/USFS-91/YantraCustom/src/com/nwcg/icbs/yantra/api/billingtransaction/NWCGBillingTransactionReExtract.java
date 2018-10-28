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
import java.util.ArrayList;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.reports.loftware.NWCGReportsUtil;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGBillingTransactionReExtract implements YIFCustomApi {

	int blankcnt, lengthcnt, totextcnt = 0;

	double Total_File_Amt = 0.00;
	
	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGBillingTransactionReExtract.class);

	public void setProperties(Properties arg0) throws Exception {
	}

	public void throwAlert(YFSEnvironment env, StringBuffer Message) throws Exception {
		logger.verbose("@@@@@ Entering NWCGBillingTransactionReExtract::throwAlert");
		Message.append(" ExceptionType='BILLING_TRANS_EXTRACT'" + " InboxType='" + NWCGConstants.NWCG_BILL_TRANS_INBOXTYPE + "' QueueId='" + NWCGConstants.NWCG_BILL_TRANS_QUEUEID + "' />");
		logger.verbose("Throw Alert Method called with message:-" + Message.toString());
		try {
			Document inTemplate = XMLUtil.getDocument(Message.toString());
			CommonUtilities.invokeAPI(env, "createException", inTemplate);
		} catch (Exception ex1) {
			logger.error("!!!!! Caught General Exception :: " + ex1);
			throw new NWCGException("NWCG_BILLING_TRANSACTION_ERROR_WHILE_LOGGING_ALERT");
		}
		logger.verbose("@@@@@ Exiting NWCGBillingTransactionReExtract::throwAlert");
	}

	public Document processReExtract(YFSEnvironment env, Document inXML) throws Exception {
		logger.verbose("@@@@@ Entering NWCGBillingTransactionReExtract::processReExtract");
		logger.verbose("@@@@@ inXML:" + XMLUtil.getXMLString(inXML));

		/*
		 * 1) get all entries in nwcg_billing_trans_extract that have the incoming file name
		 * 2) add all the values per BLM acct code
		 * 3) write to file
		 * 
		 * Incoming file:
		 * <NWCGBillingTransExtract    ExtractFileName="CORMK_EXTRACT20100125124725.FAFIREST.txt" IgnoreOrdering="Y"/>
		 */
		Element elemRoot = null;
		elemRoot = inXML.getDocumentElement();
		if (elemRoot != null) {
			logger.verbose("@@@@@ elemRoot != null");
			String ExtractFileName = elemRoot.getAttribute("ExtractFileName");
			Document BillingTransExtractListInDoc = XMLUtil.newDocument();
			Document BillingTransExtractFileListOutDoc = XMLUtil.newDocument();
			Element el_NWCGBillingTransExtractList = BillingTransExtractListInDoc.createElement("NWCGBillingTransExtract");
			BillingTransExtractListInDoc.appendChild(el_NWCGBillingTransExtractList);
			el_NWCGBillingTransExtractList.setAttribute("ExtractFileName", ExtractFileName);
			el_NWCGBillingTransExtractList.setAttribute("MaximumRecords", "20000");
			BillingTransExtractFileListOutDoc = CommonUtilities.invokeService(env, NWCGConstants.NWCG_GET_BILLING_TRANSACTION_EXTRACT_LIST_SERVICE, BillingTransExtractListInDoc);
			NodeList ExtractList = BillingTransExtractFileListOutDoc.getDocumentElement().getElementsByTagName("NWCGBillingTransExtract");
			int ExtractListCount = ExtractList.getLength();
			ArrayList al = new ArrayList();
			ArrayList alAmount = new ArrayList();
			String lastBLMacctCode = "";
			String amount = "";
			String FileName = "ReExtractFile-";
			String DocumentDate = "";
			String PostingDate = "";
			NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
			double dblTotalAmount = 0.00;
			double dblTransAmount = 0;
			String strTotalAmount = "";
			double dblLastTransAmount = 0;
			double currentTransAmount = 0;
			double dblIncidentBlmAcctCode = 0;
			String strLastTransAmount = "";
			String strBlmAcctCnt = "";
			int intLastCnt = 0;
			String strAmount = "";
			String strLastAmount = "";
			double dblAmtDocumentCurrency = 0;
			String AmtDocumentCurrency = "";
			String ItemText = "L322N091101";
			String CompanyID = "1400";
			String OrderNo = "";
			String CommitmentItem = "";
			String FundsCenter = "";
			String Fund = "";
			String DocNoForEarmarkedFunds = "";
			String EarmarkedFundsDocumentNo = "";
			String WBS = "";
			String CostCenter = "";
			String FunctionalArea = "";
			String creditAmount = "";
			String TransNo = "";
			String FiscalPeriod = "";
			String BillingExtractLine = "";
			String SequenceKey = "";
			String PMSTransAmount = "";
			String outputForTestFile = "";
			//String GLAcctCode = "6100.264B0";
			double dblAmount = 0.00;
			String strInterfaceType = "";
			String strPostingKey = "";
			String FinalOffesttingRecordLine = "";
			double dblPMSTransAmount = 0;
			double dbltotalPMSTransAmount = 0;
			String strPMSAmountPostingKey = "";
			String PMSAmtLine = "";
			String strFinalOffsetPostingKey = "";
			String strAbsoluteCheck = "";
			String FinalOffsetAmount = "";
			String BusinessArea = "";
			String InterfaceType = "";
			String InterfacePostingType = "";
			String ReferenceDocumentNumber = "";
			String DocumentHeaderText = "";
			String PostingKey = "";
			String GLAcctCode = "";
			String strCostCenter = "";
			String strFunctionalArea = "";
			String strWBS = "";
			String TransAmount = "";
			String strIncidentBlmAcctCode = "";
			String strTransAmount = "";
			java.text.DecimalFormat df = new java.text.DecimalFormat("###.##");
			String strTimestamp = reportsUtil.dateToString(new java.util.Date(), "yyyy-MM-dd'T'HH:mm:ss");
			//2009-11-06T12:15:17
			logger.verbose("@@@@@ Before Date Setting....");
			String strYear = strTimestamp.substring(0, 4);
			String strMonth = strTimestamp.substring(5, 7);
			String strDay = strTimestamp.substring(8, 10);
			String strHour = strTimestamp.substring(11, 13);
			String strMinute = strTimestamp.substring(14, 16);
			String strSecond = strTimestamp.substring(17, 19);
			PostingDate = strYear + "" + strMonth + "" + strDay;
			DocumentDate = PostingDate;
			//FiscalPeriod if statement
			if (strMonth.equals("10")) {
				FiscalPeriod = "01";
			} else if (strMonth.equals("11")) {
				FiscalPeriod = "02";
			} else if (strMonth.equals("12")) {
				FiscalPeriod = "03";
			} else if (strMonth.equals("01")) {
				FiscalPeriod = "04";
			} else if (strMonth.equals("02")) {
				FiscalPeriod = "05";
			} else if (strMonth.equals("03")) {
				FiscalPeriod = "06";
			} else if (strMonth.equals("04")) {
				FiscalPeriod = "07";
			} else if (strMonth.equals("05")) {
				FiscalPeriod = "08";
			} else if (strMonth.equals("06")) {
				FiscalPeriod = "09";
			} else if (strMonth.equals("07")) {
				FiscalPeriod = "10";
			} else if (strMonth.equals("08")) {
				FiscalPeriod = "11";
			} else if (strMonth.equals("09")) {
				FiscalPeriod = "12";
			}
			PostingDate = strYear + "" + strMonth + "" + strDay;
			DocumentDate = PostingDate;
			String timestamp = PostingDate + strHour + "" + strMinute + "" + strDay;
			String strBillingReExtractFilePath = YFSSystem.getProperty("nwcg.icbs.billingtransaction.output.directory");
			logger.verbose("@@@@@ strBillingReExtractFilePath :: " + strBillingReExtractFilePath);
			String OutFileName = strBillingReExtractFilePath + FileName + timestamp + ".txt";
			logger.verbose("@@@@@ OutFileName :: " + OutFileName);
			PrintStream p;
			p = Set_OutFile(env, OutFileName);
			if (ExtractListCount > 0) {
				logger.verbose("@@@@@ ExtractListCount > 0 :: " + ExtractListCount);
				for (int cnt = 0; cnt < ExtractListCount; cnt++) {
					Element BillingTransactionIteml = (Element) ExtractList.item(cnt);
					BusinessArea = BillingTransactionIteml.getAttribute("BusinessArea");
					InterfaceType = BillingTransactionIteml.getAttribute("InterfaceType");
					InterfacePostingType = BillingTransactionIteml.getAttribute("InterfacePostingType");
					ReferenceDocumentNumber = BillingTransactionIteml.getAttribute("ReferenceDocumentNumber");
					DocumentHeaderText = BillingTransactionIteml.getAttribute("DocumentHeaderText");
					PostingKey = BillingTransactionIteml.getAttribute("PostingKey");
					GLAcctCode = BillingTransactionIteml.getAttribute("GLAccountCode");
					PMSTransAmount = BillingTransactionIteml.getAttribute("PMSTransAmount");
					strCostCenter = BillingTransactionIteml.getAttribute("CostCenter");
					strFunctionalArea = BillingTransactionIteml.getAttribute("FunctionalArea");
					strWBS = BillingTransactionIteml.getAttribute("WBS");
					TransAmount = BillingTransactionIteml.getAttribute("AmtInDocCurrency");
					strIncidentBlmAcctCode = BillingTransactionIteml.getAttribute("IncidentBlmAcctCode");
					if (al.contains(strIncidentBlmAcctCode)) {
					} else {
						if (cnt > 0) {
							CostCenter = lastBLMacctCode.substring(0, 10);
							WBS = lastBLMacctCode.substring(11, 23);
							
							// BEGIN - 9.1 Upgrade Defect 1356
							if(lastBLMacctCode.length() == 40) {
								FunctionalArea = lastBLMacctCode.substring(24, 40);
							} else {
								FunctionalArea = strFunctionalArea;
							}
							logger.verbose("@@@@@ FunctionalArea :: " + FunctionalArea);
							// END - 9.1 Upgrade Defect 1356
							
							//dblTotalAmount - FORMAT THE ACCUMULATED TOTAL
							dblTotalAmount = df.parse(df.format(dblTotalAmount)).doubleValue();
							amount = Double.toString(dblTotalAmount);
							strTransAmount = amount.substring(0, 1);
							if (strTransAmount.equals("-")) {
								amount = amount.substring(1);
							}
							int intDecimal = amount.indexOf(".");
							int intDecimalIndex = (intDecimal + 1);
							String strDecimal = amount.substring(intDecimalIndex);
							if (strDecimal.length() < 2) {
								amount = (amount + "0");
							}
							AmtDocumentCurrency = getAmtDocumentCurrencyFormat(amount);
							if (strTransAmount.equals("-")) {
								PostingKey = "50";
							} else {
								PostingKey = "40";
							}
							BillingExtractLine = BusinessArea + "|" + InterfaceType + "|" + InterfacePostingType + "|" + DocumentDate + "|" + PostingDate + "|" + FiscalPeriod + "|";
							BillingExtractLine += ReferenceDocumentNumber + "|" + DocumentHeaderText + "|" + PostingKey + "|";
							BillingExtractLine += GLAcctCode + "|";
							BillingExtractLine += AmtDocumentCurrency + "||";
							BillingExtractLine += ItemText + "||";
							BillingExtractLine += CostCenter + "|" + OrderNo + "|" + CommitmentItem + "|";
							BillingExtractLine += WBS + "|" + FundsCenter + "|" + Fund + "|";
							BillingExtractLine += DocNoForEarmarkedFunds + "|" + EarmarkedFundsDocumentNo + "|" + FunctionalArea + "|||||";
							//logger.verbose("@@@@@ BillingExtractLine :: " + BillingExtractLine);
							p.println(BillingExtractLine);
							dblTotalAmount = 0;
							al.clear();
							alAmount.clear();
						}
					}
					al.add(strIncidentBlmAcctCode);
					alAmount.add(TransAmount);
					currentTransAmount = Double.parseDouble(TransAmount);
					int intLastIndex = al.size() - 1;
					int intIndexCount = 0;
					intIndexCount = cnt + 1;
					if (al.size() >= 1) {
						if (intLastIndex >= 0) {
							strLastAmount = alAmount.get(intLastIndex).toString();
							lastBLMacctCode = al.get(intLastIndex).toString();
							logger.verbose("@@@@@ lastBLMacctCode :: " + lastBLMacctCode);
							;
							if (lastBLMacctCode.equals(strIncidentBlmAcctCode)) {
								dblTotalAmount = dblTotalAmount + currentTransAmount;
							}
						}
					}
					if (ExtractListCount == intIndexCount) {
						CostCenter = lastBLMacctCode.substring(0, 10);
						WBS = lastBLMacctCode.substring(11, 23);
						
						// BEGIN - 9.1 Upgrade Defect 1356
						if(lastBLMacctCode.length() == 40) {
							FunctionalArea = lastBLMacctCode.substring(24, 40);
						} else {
							FunctionalArea = strFunctionalArea;
						}
						logger.verbose("@@@@@ FunctionalArea :: " + FunctionalArea);
						// END - 9.1 Upgrade Defect 1356
						
						//dblTotalAmount - FORMAT THE ACCUMULATED TOTAL
						dblTotalAmount = df.parse(df.format(dblTotalAmount)).doubleValue();
						amount = Double.toString(dblTotalAmount);
						strTransAmount = amount.substring(0, 1);
						if (strTransAmount.equals("-")) {
							amount = amount.substring(1);
						}
						int intDecimal = amount.indexOf(".");
						int intDecimalIndex = (intDecimal + 1);
						String strDecimal = amount.substring(intDecimalIndex);
						if (strDecimal.length() < 2) {
							amount = (amount + "0");
						}
						AmtDocumentCurrency = getAmtDocumentCurrencyFormat(amount);
						if (strTransAmount.equals("-")) {
							PostingKey = "50";
						} else {
							PostingKey = "40";
						}
						BillingExtractLine = BusinessArea + "|" + InterfaceType + "|" + InterfacePostingType + "|" + DocumentDate + "|" + PostingDate + "|" + FiscalPeriod + "|";
						BillingExtractLine += ReferenceDocumentNumber + "|" + DocumentHeaderText + "|" + PostingKey + "|";
						BillingExtractLine += GLAcctCode + "|";
						BillingExtractLine += AmtDocumentCurrency + "||";
						BillingExtractLine += ItemText + "||";
						BillingExtractLine += CostCenter + "|" + OrderNo + "|" + CommitmentItem + "|";
						BillingExtractLine += WBS + "|" + FundsCenter + "|" + Fund + "|";
						BillingExtractLine += DocNoForEarmarkedFunds + "|" + EarmarkedFundsDocumentNo + "|" + FunctionalArea + "|||||";
						p.println(BillingExtractLine);
					}
					if (strIncidentBlmAcctCode.length() == 40) {
						dblAmount = dblAmount + currentTransAmount;
						if (!PMSTransAmount.equals("")) {
							dblPMSTransAmount = Double.parseDouble(PMSTransAmount);
							dblPMSTransAmount = df.parse(df.format(dblPMSTransAmount)).doubleValue();
						}
					}

				}
				//PMS AMOUNT SECTION PMSTransAmount="63.30"
				dbltotalPMSTransAmount = dbltotalPMSTransAmount + dblPMSTransAmount;
				dbltotalPMSTransAmount = (dbltotalPMSTransAmount - (dbltotalPMSTransAmount / (1.5)));
				dbltotalPMSTransAmount = df.parse(df.format(dbltotalPMSTransAmount)).doubleValue();
				String strPMSamount = Double.toString(dbltotalPMSTransAmount);
				String strPMSSignCheck = strPMSamount.substring(0, 1);
				if (strPMSSignCheck.equals("-")) {
					strPMSamount = strPMSamount.substring(1);
					;
					strPMSAmountPostingKey = "40";
				} else {
					strPMSAmountPostingKey = "50";
				}
				CostCenter = "LLFA241000";
				WBS = "LFFA20000000";
				FunctionalArea = "LF5810000.HT0000";
				strPMSamount = getAmtDocumentCurrencyFormat(strPMSamount);
				PMSAmtLine = BusinessArea + "|" + InterfaceType + "|" + InterfacePostingType + "|" + DocumentDate + "|" + PostingDate + "|" + FiscalPeriod + "|";
				PMSAmtLine += ReferenceDocumentNumber + "|" + DocumentHeaderText + "|" + strPMSAmountPostingKey + "|" + GLAcctCode + "|";
				PMSAmtLine += strPMSamount + "||";
				PMSAmtLine += ItemText + "||" + CostCenter + "|" + OrderNo + "|" + CommitmentItem + "|";
				PMSAmtLine += WBS + "|" + FundsCenter + "|" + Fund + "|";
				PMSAmtLine += DocNoForEarmarkedFunds + "|" + EarmarkedFundsDocumentNo + "|" + FunctionalArea + "|||||";
				//FINAL OFFSET LINE
				dblAmount = dblAmount - dbltotalPMSTransAmount;
				dblAmount = df.parse(df.format(dblAmount)).doubleValue();
				FinalOffsetAmount = Double.toString(dblAmount);
				strAbsoluteCheck = FinalOffsetAmount.substring(0, 1);
				if (strAbsoluteCheck.equals("-")) {
					FinalOffsetAmount = FinalOffsetAmount.substring(1);
					;
					strFinalOffsetPostingKey = "40";
				} else {
					strFinalOffsetPostingKey = "50";
				}
				int intFinalOffsetDecimal = FinalOffsetAmount.indexOf(".");
				int intFinalOffsetDecimalIndex = (intFinalOffsetDecimal + 1);
				String strFinalOffsetAmountDecimal = FinalOffsetAmount.substring(intFinalOffsetDecimalIndex);
				if (strFinalOffsetAmountDecimal.length() < 2) {
					FinalOffsetAmount = (FinalOffsetAmount + "0");
				}
				FinalOffsetAmount = getAmtDocumentCurrencyFormat(FinalOffsetAmount);
				if (strInterfaceType.equals("AK")) {
					CostCenter = "LLAK9F2200";
					WBS = "LFSP77770000";
					FunctionalArea = "LF20000SP.HU0000";
				} else {
					CostCenter = "LLFA241000";
					WBS = "LFSP77770000";
					FunctionalArea = "LF20000SP.HU0000";
				}
				FinalOffesttingRecordLine = BusinessArea + "|" + InterfaceType + "|" + InterfacePostingType + "|" + DocumentDate + "|" + PostingDate + "|" + FiscalPeriod + "|";
				FinalOffesttingRecordLine += ReferenceDocumentNumber + "|" + DocumentHeaderText + "|" + strFinalOffsetPostingKey + "|" + GLAcctCode + "|";
				FinalOffesttingRecordLine += FinalOffsetAmount + "||";
				FinalOffesttingRecordLine += ItemText + "||" + CostCenter + "|" + OrderNo + "|" + CommitmentItem + "|";
				FinalOffesttingRecordLine += WBS + "|" + FundsCenter + "|" + Fund + "|";
				FinalOffesttingRecordLine += DocNoForEarmarkedFunds + "|" + EarmarkedFundsDocumentNo + "|" + FunctionalArea + "|||||";
			}
			p.println(PMSAmtLine);
			p.println(FinalOffesttingRecordLine);
		}
		logger.verbose("@@@@@ Exiting NWCGBillingTransactionReExtract::processReExtract");
		return inXML;
	}

	public PrintStream Set_OutFile(YFSEnvironment env, String outfile) throws Exception {
		logger.verbose("@@@@@ Entering NWCGBillingTransactionReExtract::Set_OutFile");
		FileOutputStream out = null;
		PrintStream ps;
		try {
			out = new FileOutputStream(outfile, true);
		} catch (FileNotFoundException e) {
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransactionReExtractFTP' " + "DetailDescription='Error in Opening File : " + outfile + ". When processing NWCGProcessBillingTransactionReExtractFTP'");
			throwAlert(env, stbuf);
			logger.error("!!!!! Caught FileNotFoundException, NWCGProcessBillingTransactionReExtractFTP::Error in Opening File :: " + outfile);
		}
		ps = new PrintStream(out);
		logger.verbose("@@@@@ Exiting NWCGBillingTransactionReExtract::Set_OutFile");
		return ps;
	}

	public void InsertBillingTransactionExtract(YFSEnvironment env,
			String BusinessArea, String InterfaceType,
			String InterfacePostingType, String DocumentDate,
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
		logger.verbose("@@@@@ Entering NWCGBillingTransactionReExtract::InsertBillingTransactionExtract");
		Document BillDoc = XMLUtil.createDocument("NWCGBillingTransExtract");
		Element BillDocElem = BillDoc.getDocumentElement();
		BillDocElem.setAttribute("BusinessArea", BusinessArea);
		BillDocElem.setAttribute("InterfaceType", InterfaceType);
		BillDocElem.setAttribute("InterfacePostingType", InterfacePostingType);
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
		BillDocElem.setAttribute("DocNumForEarmarkedFunds", DocNoForEarmarkedFunds);
		BillDocElem.setAttribute("EarmarkedFundsDocItem", EarmarkedFundsDocumentNo);
		BillDocElem.setAttribute("FunctionalArea", FunctionalArea);
		BillDocElem.setAttribute("ExtractTransNo", TransNo);
		BillDocElem.setAttribute("DocNumForEarmarkedFunds", DocumentNo);
		BillDocElem.setAttribute("FiscalYear", FiscalYear);
		BillDocElem.setAttribute("AmtInDocCurrency", TransAmount);
		BillDocElem.setAttribute("ExtractFileName", extractFileName);
		BillDocElem.setAttribute("PMSTransAmount", PMSTransAmount);
		BillDocElem.setAttribute("CacheId", CacheId);
		try {
			CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_BILLING_TRANSACTION_EXTRACT_SERVICE, BillDoc);
			UpdateBillingTransaction(env, TransNo, FiscalYear, SequenceKey);
		} catch (Exception e) {
			// Jimmy - added all info to description 
			logger.error("!!!!! Caught General Exception, NWCGProcessBillingTransactionReExtractFTP :: " + e);
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransactionReExtractFTP' " + "DetailDescription='Error in Inserting NWCG_BILLING_TRANSACTION_EXTRACT Record during NWCGProcessBillingTransactionReExtract process.  CacheID: " + CacheId + " , ExtractTransNo: " + TransNo + ", DocumentNo: " + DocumentNo + " , OrderNo: " + OrderNo + " '");
			throwAlert(env, stbuf);
		}
		logger.verbose("@@@@@ Exiting NWCGBillingTransactionReExtract::InsertBillingTransactionExtract");
	}

	public void UpdateBillingTransaction(YFSEnvironment env, String TransNo, String FiscalYear, String SequenceKey) throws Exception {
		logger.verbose("@@@@@ Entering NWCGBillingTransactionReExtract::UpdateBillingTransaction");
		Document BillDoc = XMLUtil.createDocument("NWCGBillingTransaction");
		Element BillDocElem = BillDoc.getDocumentElement();
		NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
		String LastExtractDate = reportsUtil.dateToString(new java.util.Date(), NWCGConstants.YANTRA_DATE_FORMAT);
		//for (int i=0;i<rcnt;i++)
		//{
		//String SequenceKey = SeqKeys[i];
		BillDocElem.setAttribute("IsExtracted", "Y");
		BillDocElem.setAttribute("SequenceKey", SequenceKey);
		BillDocElem.setAttribute("ExtractTransNo", TransNo);
		BillDocElem.setAttribute("TransactionFiscalYear", FiscalYear);
		BillDocElem.setAttribute("LastExtractDate", LastExtractDate);
		try {
			//CommonUtilities.invokeService(env,NWCGConstants.NWCG_UPDATE_BILLING_TRANSACTION_SERVICE,BillDoc); 
		} catch (Exception e) {
			//Jimmy - Original throwalert
			logger.error("!!!!! Caught General Exception, NWCGProcessBillingExtract::updateBillingTransactionRecord :: " + e);
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransactionReExtractFTP' " + " DetailDescription='Error in NWCGProcessBillingTransactionReExtractFTP when updateBillingTransactionRecord for TransactionNo: " + TransNo + " , SequenceKey: " + SequenceKey + "'");
			throwAlert(env, stbuf);
		}
		//}
		logger.verbose("@@@@@ Exiting NWCGBillingTransactionReExtract::UpdateBillingTransaction");
	}

	public static String getAmtDocumentCurrencyFormat(String amount) {
		logger.verbose("@@@@@ In NWCGBillingTransactionReExtract::getAmtDocumentCurrencyFormat");
		String AmtDocumentCurrency = "";
		AmtDocumentCurrency = StringUtil.prepadStringWithZeros(amount, 13);
		return AmtDocumentCurrency;
	}
}