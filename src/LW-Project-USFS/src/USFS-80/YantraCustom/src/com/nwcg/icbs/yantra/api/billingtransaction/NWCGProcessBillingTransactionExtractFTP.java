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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.reports.loftware.NWCGReportsUtil;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGProcessBillingTransactionExtractFTP implements YIFCustomApi,
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

	public Document insertBillingTransRecord(YFSEnvironment env, Document doc)
			throws Exception {
		// step 1
		return InsertRecordIntoFTP(env, doc);
	}

	public Document InsertRecordIntoFTP(YFSEnvironment env, Document inXML)
			throws Exception {
		// declaration section
		Element elemRootInXML = inXML.getDocumentElement();
		String OwnerAgency = elemRootInXML.getAttribute("OwnerAgency");
		String AcctCodeName = getAccontCodeName(OwnerAgency);
		String PostingDate = getPostingDate();
		String DocumentDate = PostingDate;
		String FiscalPeriod = getFiscalPeriod();
		String extractFileName = getFileName(env, PostingDate, elemRootInXML);
		// It was set previously in yantraimpl.properties.
		// As we need different directory for PreProd and PROD, we moved the property to customer_overrides.properties
		// String OutFileName = NWCGConstants.NWCG_BLM_OUTPUT_DIR + extractFileName;
		String OutFileName = YFSSystem
				.getProperty("nwcg.icbs.billingtransaction.output.directory")
				+ extractFileName;
		String strIncidentBlmAcctCode = "";
		boolean isValidBLMCode = false;
		boolean isWBSNull = true;
		boolean pmsLineExists = false;
		String ItemProductLine = "";
		NodeList nlPMSLines;
		Element elemPMSLine;
		NodeList nlBLMLines;
		Element elemBLMLine;
		PrintStream extractFile;
		String BusinessArea = "L000";
		String InterfaceType = getInterfaceType(elemRootInXML);
		String ReferenceDocumentNumber = "";
		String DocumentHeaderText = "";
		String PostingKey = "";
		String TransAmount = "";
		String GLAcctCode = "6100.264B0";
		String ItemText = "R220Nyymmxx";
		String CompanyID = "1400";
		String strCostCenter = "";
		String strWBS = "";
		String strFunctionalArea = "";
		String OrderNo = "";
		String CommitmentItem = "";
		String FundsCenter = "";
		String Fund = "";
		String DocNoForEarmarkedFunds = "";
		String EarmarkedFundsDocumentNo = "";
		String TransNo = "";
		String DocumentNo = "";
		String FiscalYear = elemRootInXML.getAttribute("FiscalYear");
		String SequenceKey = "";
		String CacheId = elemRootInXML.getAttribute("CacheID");
		//for Alaska change the ItemText
		if (CacheId.equals("AKAKK"))
			ItemText = "L322Nyymmxx";
		String PMSTransAmount = "";
		String TransactionNo = "";
		String strPMSTotalAmount = "";
		double dblPMSTotalAmount = 00.00;
		String strTotalAmount = "";
		double dblTotalAMount = 00.00;
		String PMSAmtLine = "";
		java.text.DecimalFormat df = new java.text.DecimalFormat("###.##");
		double dblBlmTotalAmount = 00.00;
		double dblBlmTempTotalAmount = 00.00;
		String BillingExtractLine = "";
		String InterfacePostingType = "ET";
		String FinalOffesttingRecordLine = "";
		extractFile = Set_OutFile(env, OutFileName);
		HashMap blmActTransAmountHM = new HashMap();

		/*
		 * 
		 * <NWCGBillingTransaction BillingTransFileName="KEITH"
		 * BillingTransactionAction="EXTRACT" CacheID="AZPFK" CurrentSeqNo=""
		 * DocumentNo="0009" FiscalYear="" FromTransDate="05/12/2010"
		 * IgnoreOrdering="Y" OffsetAcctCode="012345678901234567890123456789"
		 * OwnerAgency="BLM" PrimaryEnterpriseKey="NWCG"
		 * ToTransDate="05/14/2010"/>
		 */
		Document docBillingOutDoc = getBillingOutDoc(env, inXML);

		// NWCGBillingTransaction elements if greater then zero then create and print file
		NodeList nlNWCGBillingTransaction = docBillingOutDoc
				.getElementsByTagName("NWCGBillingTransaction");

		int intNWCGBillingTransactionLen = nlNWCGBillingTransaction.getLength();
		if (intNWCGBillingTransactionLen > 0) {

			//FinalOffesttingRecordLine = Integer
			//	.toString(intNWCGBillingTransactionLen)
			//+ " records for selected daterange";

			// for each billing outdoc print into file
			// sum all blm acct code and put the transamount(use hashmap to
			// eliminate duplicate entries)
			// if any pms lines and sum the transamount (do not print them)
			// print them as single pmsline
			// print the final offset

			for (int i = 0; i < intNWCGBillingTransactionLen; i++) {
				PMSTransAmount = "";
				isValidBLMCode = false;
				Element elemNWCGBillingTransaction = (Element) nlNWCGBillingTransaction
						.item(i);
				strIncidentBlmAcctCode = elemNWCGBillingTransaction
						.getAttribute("IncidentBlmAcctCode");
				TransAmount = elemNWCGBillingTransaction
						.getAttribute("TransAmount");
				if (strIncidentBlmAcctCode.length() == 40) {
					isValidBLMCode = true;
					isWBSNull = false;
				} else if (strIncidentBlmAcctCode.length() == 28) {
					isValidBLMCode = true;
					isWBSNull = true;
				}
				if (isValidBLMCode) {
					// input parameters for insertbillingtransaction
					PostingKey = getPostingKey(TransAmount);
					strCostCenter = strIncidentBlmAcctCode.substring(0, 10);
					strFunctionalArea = strIncidentBlmAcctCode
							.substring(11, 27);
					if (isWBSNull) {
						strWBS = "            ";
					} else {
						strWBS = strIncidentBlmAcctCode.substring(28, 40);
					}
					TransNo = elemNWCGBillingTransaction
							.getAttribute("TransLineKey");
					DocumentNo = elemNWCGBillingTransaction
							.getAttribute("DocumentNo");
					SequenceKey = elemNWCGBillingTransaction
							.getAttribute("SequenceKey");
					TransactionNo = elemNWCGBillingTransaction
							.getAttribute("TransactionNo");
					ItemProductLine = elemNWCGBillingTransaction
							.getAttribute("ItemProductLine");
					// if any pms lines and sum the transamount (do not print
					// them) print them as single pmsline
					// for IDGBK only calculate PMS amount
					if (ItemProductLine.equals("PMS Publications")
							&& CacheId.equals("IDGBK")) {
						PMSTransAmount = getPMSAmount(TransAmount);
						// if pms lines are not already processed
						if (!pmsLineExists) {
							pmsLineExists = true;
							nlPMSLines = XPathUtil
									.getNodeList(
											docBillingOutDoc
													.getDocumentElement(),
											"/NWCGBillingTransactionList/NWCGBillingTransaction[@ItemProductLine='PMS Publications']");
							int intPMSLines = nlPMSLines.getLength();
							for (int j = 0; j < intPMSLines; j++) {
								// add the transamount and calculate the
								// transkey and prepare pms line
								// add the transamount to hashmap with key as
								// plmlinestransamount
								elemPMSLine = (Element) nlPMSLines.item(j);
								dblPMSTotalAmount = calcPMSTotalAmount(
										elemPMSLine.getAttribute("TransAmount"),
										dblPMSTotalAmount);
								// blmActTransAmountHM.put("pmsLinesBLM",
								// "10.0");
							}
						}
					}
					//GN else {
					// non pms lines sum by blm code and print
					// check if hashmap has the blmcode already
					// if not check all the lines with same blmcode ,
					// prepare billing transaction line and print on the
					// file
					if (!blmActTransAmountHM
							.containsKey(strIncidentBlmAcctCode)) {
						nlBLMLines = XPathUtil.getNodeList(docBillingOutDoc
								.getDocumentElement(),
								"/NWCGBillingTransactionList/NWCGBillingTransaction[@IncidentBlmAcctCode='"
										+ strIncidentBlmAcctCode + "']");
						int intBLMLines = nlBLMLines.getLength();
						//blm total for this specific blm code
						dblBlmTempTotalAmount = 0.0;
						for (int k = 0; k < intBLMLines; k++) {
							elemBLMLine = (Element) nlBLMLines.item(k);
							dblBlmTempTotalAmount = getBlmTotalAmount(
									elemBLMLine.getAttribute("TransAmount"),
									dblBlmTempTotalAmount);
						}
						blmActTransAmountHM.put(strIncidentBlmAcctCode,
								dblBlmTempTotalAmount);
						//extractFile.println
						String strBLMTransAmount = Double
								.toString(dblBlmTempTotalAmount);
						String strBLMPostingKey = getPostingKey(strBLMTransAmount);
						String strPrintBLMTransAmount = getPrintPMSAmount(strBLMTransAmount);
						BillingExtractLine = BusinessArea + "|" + InterfaceType
								+ "|" + InterfacePostingType + "|"
								+ DocumentDate + "|" + PostingDate + "|"
								+ FiscalPeriod + "|";
						BillingExtractLine += ReferenceDocumentNumber + "|"
								+ DocumentHeaderText + "|" + strBLMPostingKey
								+ "|";
						BillingExtractLine += GLAcctCode + "|";
						BillingExtractLine += strPrintBLMTransAmount + "||";
						BillingExtractLine += ItemText + "||";
						BillingExtractLine += strCostCenter + "|" + OrderNo
								+ "|" + CommitmentItem + "|";
						BillingExtractLine += strWBS + "|" + FundsCenter + "|"
								+ Fund + "|";
						BillingExtractLine += DocNoForEarmarkedFunds + "|"
								+ EarmarkedFundsDocumentNo + "|"
								+ strFunctionalArea + "|||||";
						extractFile.println(BillingExtractLine);
						//total BLM AMount for all blm codes
						dblBlmTotalAmount = dblBlmTotalAmount
								+ dblBlmTempTotalAmount;
					}
					//GN}// end if (ItemProductLine.equals("PMS Publications"))
					// add the entry into billing extract table
					// update entry in the billing transaction table with
					// isextract=Y
					// inserts the billing transaction into
					// nwcg_billing_trans_extract table
					InsertBillingTransactionExtract(env, BusinessArea,
							InterfaceType, InterfacePostingType, DocumentDate,
							PostingDate, FiscalPeriod, ReferenceDocumentNumber,
							DocumentHeaderText, PostingKey, GLAcctCode,
							TransAmount, ItemText, CompanyID, strCostCenter,
							OrderNo, CommitmentItem, strWBS, FundsCenter, Fund,
							DocNoForEarmarkedFunds, EarmarkedFundsDocumentNo,
							strFunctionalArea, TransNo, DocumentNo, FiscalYear,
							TransAmount, SequenceKey, extractFileName, CacheId,
							PMSTransAmount, strIncidentBlmAcctCode,
							TransactionNo);
				}
			}
			if (pmsLineExists) {
				//Changing the sign for PMS Amount, Assuming always a -ve amount
				dblPMSTotalAmount = dblPMSTotalAmount * -1;
				strPMSTotalAmount = Double.toString(dblPMSTotalAmount);
				// printing the actual pms line starts here
				// PMS AMOUNT SECTION
				String strPMSAmountPostingKey = getPostingKey(strPMSTotalAmount);
				strPMSTotalAmount = getPrintPMSAmount(strPMSTotalAmount);
				strCostCenter = "LLFA241000";
				strWBS = "LFFA20000000";
				strFunctionalArea = "LF5810000.HT0000";
				PMSAmtLine = BusinessArea + "|" + InterfaceType + "|"
						+ InterfacePostingType + "|" + DocumentDate + "|"
						+ PostingDate + "|" + FiscalPeriod + "|";
				PMSAmtLine += ReferenceDocumentNumber + "|"
						+ DocumentHeaderText + "|" + strPMSAmountPostingKey
						+ "|" + GLAcctCode + "|";
				PMSAmtLine += strPMSTotalAmount + "||";
				PMSAmtLine += ItemText + "||" + strCostCenter + "|" + OrderNo
						+ "|" + CommitmentItem + "|";
				PMSAmtLine += strWBS + "|" + FundsCenter + "|" + Fund + "|";
				PMSAmtLine += DocNoForEarmarkedFunds + "|"
						+ EarmarkedFundsDocumentNo + "|" + strFunctionalArea
						+ "|||||";
				extractFile.println(PMSAmtLine);
			}
			//FINAL OFFSET LINE
			double dblFinalOffsetAmount = -(dblBlmTotalAmount + dblPMSTotalAmount);
			String strFinalOffsetAmount = Double.toString(dblFinalOffsetAmount);
			String strFinalOffsetPostingKey = getPostingKey(strFinalOffsetAmount);
			String printFinalOffsetAmount = getPrintPMSAmount(strFinalOffsetAmount);
			String strInterfaceType = "";
			strInterfaceType = CacheId.substring(0, 2);
			if (strInterfaceType.equals("AK")) {
				strCostCenter = "LLAK9F2200";
				strWBS = "LFSP77770000";
				//Begin CR840
				//strFunctionalArea 	= "LF20000SP.HU0000";
				strFunctionalArea = NWCGConstants.BillingTransactionExtract_FunctionalArea;
				//End CR840
			} else {
				strCostCenter = "LLFA241000";
				strWBS = "LFSP77770000";
				//Begin CR840 
				//strFunctionalArea 	= "LF20000SP.HU0000";
				strFunctionalArea = NWCGConstants.BillingTransactionExtract_FunctionalArea;
				//End CR840
			}

			FinalOffesttingRecordLine = BusinessArea + "|" + InterfaceType
					+ "|" + InterfacePostingType + "|" + DocumentDate + "|"
					+ PostingDate + "|" + FiscalPeriod + "|";
			FinalOffesttingRecordLine += ReferenceDocumentNumber + "|"
					+ DocumentHeaderText + "|" + strFinalOffsetPostingKey + "|"
					+ GLAcctCode + "|";
			FinalOffesttingRecordLine += printFinalOffsetAmount + "||";
			FinalOffesttingRecordLine += ItemText + "||" + strCostCenter + "|"
					+ OrderNo + "|" + CommitmentItem + "|";
			FinalOffesttingRecordLine += strWBS + "|" + FundsCenter + "|"
					+ Fund + "|";
			FinalOffesttingRecordLine += DocNoForEarmarkedFunds + "|"
					+ EarmarkedFundsDocumentNo + "|" + strFunctionalArea
					+ "|||||";
			extractFile.println(FinalOffesttingRecordLine);
		}
		return inXML;
	}

	private double getBlmTotalAmount(String Transamount,
			double dblBlmTempTotalAmount) {
		dblBlmTempTotalAmount = Double.parseDouble(Transamount)
				+ dblBlmTempTotalAmount;
		return dblBlmTempTotalAmount;
	}

	public String getPrintPMSAmount(String strPMSTotalAmount)
			throws ParseException {
		// input format -0.5599999999999999 or 0.5599999999999999
		// return format 0000000000.05
		java.text.DecimalFormat df = new java.text.DecimalFormat("###.##");
		double dblPMSAmount = Double.parseDouble(strPMSTotalAmount);
		dblPMSAmount = df.parse(df.format(dblPMSAmount)).doubleValue();
		strPMSTotalAmount = Double.toString(dblPMSAmount);
		String strPMSSignCheck = strPMSTotalAmount.substring(0, 1);
		if (strPMSSignCheck.equals("-")) {
			strPMSTotalAmount = strPMSTotalAmount.substring(1);
		}
		int intDecimal = strPMSTotalAmount.indexOf(".");
		int intDecimalIndex = (intDecimal + 1);
		String strDecimal = strPMSTotalAmount.substring(intDecimalIndex);
		if (strDecimal.length() < 2) {
			strPMSTotalAmount = (strPMSTotalAmount + "0");
		}
		return getAmtDocumentCurrencyFormat(strPMSTotalAmount);
	}

	private double calcPMSTotalAmount(String TransAmount,
			double dblPMSTotalAmount) throws ParseException {
		String pmsTransAmount = getPMSAmount(TransAmount);
		dblPMSTotalAmount = dblPMSTotalAmount
				+ Double.parseDouble(pmsTransAmount);
		return dblPMSTotalAmount;
	}

	private String getPMSAmount(String transAmount) throws ParseException {
		double dblTransAmount = Double.parseDouble(transAmount);
		double dblPMSAmount = dblTransAmount - (dblTransAmount / 1.5);
		// int intPMSAmount = Double.(dblPMSAmount*100);
		java.text.DecimalFormat df = new java.text.DecimalFormat("###.##");
		dblPMSAmount = df.parse(df.format(dblPMSAmount)).doubleValue();
		return Double.toString(dblPMSAmount);
	}

	private String getPostingKey(String transAmount) {
		String strTransAmount = transAmount.substring(0, 1);
		String PostingKey = "";
		//GN - Changing Posting Key 50 to 40 and 40 to 50 - 09/28/10
		if (strTransAmount.equals("-")) {
			//PostingKey = "50";
			PostingKey = "40";
		} else {
			//PostingKey = "40";
			PostingKey = "50";
		}
		return PostingKey;
	}

	public String getInterfaceType(Element elemRootInXML) {
		String CacheId = elemRootInXML.getAttribute("CacheID");
		String strInterfaceType = CacheId.substring(0, 2);
		String InterfaceType = "";
		if (strInterfaceType.equals("AK")) {
			InterfaceType = "AF";
		} else {
			InterfaceType = "NF";
		}
		return InterfaceType;
	}

	public String getFileName(YFSEnvironment env, String PostingDate,
			Element elemRootInXML) throws Exception {
		String extractFileName = "";
		Date todaysDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(todaysDate);
		String strHour = "";
		int intHour = (cal.get(Calendar.HOUR_OF_DAY));
		if (intHour >= 10)
			strHour = Integer.toString(intHour);
		else
			strHour = "0" + Integer.toString(intHour);
		String strMin = "";
		int intMin = (cal.get(Calendar.MINUTE));
		if (intMin >= 10)
			strMin = Integer.toString(intMin);
		else
			strMin = "0" + Integer.toString(intMin);
		String strSec = "";
		int intSec = (cal.get(Calendar.SECOND));
		if (intSec >= 10)
			strSec = Integer.toString(intSec);
		else
			strSec = "0" + Integer.toString(intSec);
		String timestamp = PostingDate + strHour + "" + strMin + "" + strSec;
		String FileName = elemRootInXML.getAttribute("BillingTransFileName");

		//Begin CR858 flexible commonCode approach
		String CacheId = elemRootInXML.getAttribute("CacheID");
		Document ipXML = XMLUtil.getDocument("<CommonCode CodeType=\""
				+ NWCGConstants.BillingTransactionExtract_FileName_CodeType
				+ "\" />");
		Document oputXML = CommonUtilities.invokeAPI(env,
				NWCGConstants.API_COMMONCODELIST, ipXML);
		NodeList nl = oputXML.getDocumentElement().getElementsByTagName(
				"CommonCode");
		String codeValue = "ABCDFIREST";
		String codeShortDesc = "ABCDK";
		for (int count = 0; count < nl.getLength(); count++) {
			Element commonCodeElem = (Element) nl.item(count);
			codeShortDesc = commonCodeElem.getAttribute("CodeShortDescription");
			if (codeShortDesc.equals(CacheId)) {
				codeValue = commonCodeElem.getAttribute("CodeValue");
			}
		}
		extractFileName = FileName + timestamp + "." + codeValue + ".txt";
		//End CR858
		/* Begin CR858 conditional logic approach
		 if (CacheId.equals("IDGBK")) {
		 extractFileName = FileName + timestamp + ".FAFIREST.txt";
		 } else if (CacheId.equals("AKAKK")) {
		 extractFileName = FileName + timestamp + ".AKFIREST.txt";
		 } else if (CacheId.equals("MTBFK")) {
		 extractFileName = FileName + timestamp + ".MTFIREST.txt";
		 } else {
		 extractFileName = FileName + timestamp + ".ZZFIREST.txt";
		 }
		 End CR858 */
		return extractFileName;
	}

	public Document getBillingOutDoc(YFSEnvironment env, Document inXML)
			throws Exception {
		Document docBillingInDoc = XMLUtil.getDocument();
		/*
		 * BillingInDoc <NWCGBillingTransaction CacheId="AZPFK"
		 * FromTransDate="20100512" IsExtracted="N" IsReviewed="Y"
		 * ToTransDate="20100514" TransDateQryType="DATERANGE">
		 * </NWCGBillingTransaction>
		 */
		Element elemNWCGBillingTransaction = docBillingInDoc
				.createElement("NWCGBillingTransaction");
		docBillingInDoc.appendChild(elemNWCGBillingTransaction);
		elemNWCGBillingTransaction.setAttribute("CacheId", inXML
				.getDocumentElement().getAttribute("CacheID"));
		String fromTransDateStr = getDateOutFormat(inXML.getDocumentElement()
				.getAttribute("FromTransDate"));
		elemNWCGBillingTransaction.setAttribute("FromTransDate",
				fromTransDateStr);
		elemNWCGBillingTransaction.setAttribute("IsExtracted", "N");
		elemNWCGBillingTransaction.setAttribute("IsReviewed", "Y");
		String toTransDateStr = getDateOutFormat(inXML.getDocumentElement()
				.getAttribute("ToTransDate"));
		elemNWCGBillingTransaction.setAttribute("ToTransDate", toTransDateStr);
		elemNWCGBillingTransaction
				.setAttribute("TransDateQryType", "DATERANGE");
		/* CR 1051 - Stores Extract - time out problem
		 * Removing MaximumRecords restriction
		 * elemNWCGBillingTransaction.setAttribute("MaximumRecords", "20000");*/
		Document docBillingOutDoc = CommonUtilities.invokeService(env,
				NWCGConstants.NWCG_GET_BILLING_TRANSACTION_LIST_SERVICE,
				docBillingInDoc);
		return docBillingOutDoc;
	}

	public String getFiscalPeriod() {
		Date todaysDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(todaysDate);
		int intMonth = (cal.get(Calendar.MONTH) + 1);
		String FiscalPeriod = "";
		switch (intMonth) {
		case 9:
			FiscalPeriod = "12";
			break;
		case 8:
			FiscalPeriod = "11";
			break;
		case 7:
			FiscalPeriod = "10";
			break;
		case 6:
			FiscalPeriod = "09";
			break;
		case 5:
			FiscalPeriod = "08";
			break;
		case 4:
			FiscalPeriod = "07";
			break;
		case 3:
			FiscalPeriod = "06";
			break;
		case 2:
			FiscalPeriod = "05";
			break;
		case 1:
			FiscalPeriod = "04";
			break;
		case 12:
			FiscalPeriod = "03";
			break;
		case 11:
			FiscalPeriod = "02";
			break;
		case 10:
			FiscalPeriod = "01";
		}
		return FiscalPeriod;
	}

	public String getPostingDate() {
		String PostingDate = "";
		Date todaysDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(todaysDate);
		String strMonth = "";
		int intMonth = (cal.get(Calendar.MONTH) + 1);
		if (intMonth >= 10)
			strMonth = Integer.toString(intMonth);
		else
			strMonth = "0" + Integer.toString(intMonth);
		String strDate = "";
		int intDate = (cal.get(Calendar.DAY_OF_MONTH));
		if (intDate >= 10)
			strDate = Integer.toString(intDate);
		else
			strDate = "0" + Integer.toString(intDate);
		PostingDate = Integer.toString(cal.get(Calendar.YEAR)) + strMonth
				+ strDate;
		return PostingDate;
	}

	public String getAccontCodeName(String OwnerAgency) throws Exception {
		String AcctCodeName = "";
		if (OwnerAgency.equals("BLM")) {
			AcctCodeName = "IncidentBlmAcctCode";
		} else if (OwnerAgency.equals("FS")) {
			AcctCodeName = "IncidentFsAcctCode";
		} else {
			AcctCodeName = "IncidentOtherAcctCode";
		}
		return AcctCodeName;
	}

	public String getDateOutFormat(String inDate) throws Exception {
		SimpleDateFormat sdfInFormat = new SimpleDateFormat("mm/dd/yyyy");
		SimpleDateFormat sdfoutformat = new SimpleDateFormat("yyyymmdd");
		Date informatDate = sdfInFormat.parse(inDate);
		String outDateStr = sdfoutformat.format(informatDate);
		return outDateStr;
	}

	public Document InsertRecord(YFSEnvironment env, Document inXML)
			throws Exception {
		// step 2
		if (logger.isVerboseEnabled()) {
			logger
					.verbose("Entering NWCGProcessBillingTransactionExtractFTP, Input document is:"
							+ XMLUtil.getXMLString(inXML));
		}
		// added for CR 589
		/*
		 * 
		 * <NWCGBillingTransaction BillingTransFileName="CORMK-"
		 * BillingTransactionAction="EXTRACT" CacheID="CORMK" CurrentSeqNo=""
		 * DocumentNo="CORMK" FiscalYear="2009" FromTransDate="10/26/2009"
		 * IgnoreOrdering="Y" OffsetAcctCode="1234567890123456789012345678900"
		 * OwnerAgency="BLM" PrimaryEnterpriseKey="NWCG"
		 * ToTransDate="10/30/2009"/>
		 */

		Element elemRoot = null;
		// added for CR 589
		boolean printPMSLine = false;
		elemRoot = inXML.getDocumentElement();
		if (elemRoot != null) {
			String CacheId = elemRoot.getAttribute("CacheID");
			String FromDate = elemRoot.getAttribute("FromTransDate");
			String ToDate = elemRoot.getAttribute("ToTransDate");
			String TempAcctCode = "";
			String SeqNoStr = "";
			String AcctCode = "";
			String TNo = "";
			String IncidentBlmAcctCode = "IncidentBlmAcctCode";
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
			Element el_NWCGBillingTrans = BillingInDoc
					.createElement("NWCGBillingTransaction");
			BillingInDoc.appendChild(el_NWCGBillingTrans);
			el_NWCGBillingTrans.setAttribute("CacheId", CacheId);
			if (FromDate.length() > 0) {
				String[] FromDtFields = FromDate.split("/");
				String FromMonth = FromDtFields[0];
				String FromDay = FromDtFields[1];
				String FromYear = FromDtFields[2];
				el_NWCGBillingTrans.setAttribute("TransDateQryType", "BETWEEN");
				el_NWCGBillingTrans.setAttribute("FromTransDate", FromYear
						+ FromMonth + FromDay);
				el_NWCGBillingTrans.setAttribute("ToTransDate", ToYear
						+ ToMonth + ToDay);
			} else {
				el_NWCGBillingTrans.setAttribute("TransDateQryType", "LE");
				el_NWCGBillingTrans.setAttribute("TransDate", ToYear + ToMonth
						+ ToDay);
			}
			el_NWCGBillingTrans.setAttribute("IsExtracted", "N");
			el_NWCGBillingTrans.setAttribute("IsReviewed", "Y");
			Element el_OrderBy = BillingInDoc.createElement("OrderBy");
			el_NWCGBillingTrans.appendChild(el_OrderBy);
			Element el_Attribute = BillingInDoc.createElement("Attribute");
			el_OrderBy.appendChild(el_Attribute);
			el_Attribute.setAttribute("Name", IncidentBlmAcctCode);
			el_Attribute.setAttribute("Asc", "Y");
			el_Attribute.setAttribute("Name", "TransType");
			el_Attribute.setAttribute("Asc", "Y");
			// step 3 to get all the billing documents that meet the extract
			// criteria
			BillingOutDoc = CommonUtilities.invokeService(env,
					NWCGConstants.NWCG_GET_BILLING_TRANSACTION_LIST_SERVICE,
					BillingInDoc);
			String BillingExtractLine = "";
			String FinalOffesttingRecordLine = "";
			int BillingTransactionCount = 0;
			NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
			String BusinessArea = "L000";
			String InterfaceType = "";
			String DocumentDate = "";
			String ReferenceDocumentNumber = "";
			String DocumentHeaderText = "";
			String PostingKey = "";
			String GLAcctCode = "6100.264B0";
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
			String TransAmount = "";
			String creditAmount = "";
			String TransNo = "";
			String PostingDate = "";
			String FiscalPeriod = "";
			String strIncidentBlmAcctCode = "";
			String amount = "";
			String SequenceKey = "";
			String PMSTransAmount = "";
			double dblPMSTransAmount = 0;
			double dbltotalPMSTransAmount = 0;
			String outputForTestFile = "";
			String strPostingKey = "";
			String strFinalOffsetPostingKey = "";
			String strAbsoluteCheck = "";
			String FinalOffsetAmount = "";
			java.text.DecimalFormat df = new java.text.DecimalFormat("###.##");
			String strTimestamp = reportsUtil.dateToString(
					new java.util.Date(), "yyyy-MM-dd'T'HH:mm:ss");
			// 2009-11-06T12:15:17
			String strYear = strTimestamp.substring(0, 4);
			String strMonth = strTimestamp.substring(5, 7);
			String strDay = strTimestamp.substring(8, 10);
			String strHour = strTimestamp.substring(11, 13);
			String strMinute = strTimestamp.substring(14, 16);
			String strSecond = strTimestamp.substring(17, 19);
			PostingDate = strYear + "" + strMonth + "" + strDay;
			DocumentDate = PostingDate;
			// in 2.7.4 replace below with either properties file or switch
			// statement
			// FiscalPeriod if statement
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
			String timestamp = PostingDate + strHour + "" + strMinute + ""
					+ strDay;
			String OutFileName = NWCGConstants.NWCG_BLM_OUTPUT_DIR + FileName
					+ timestamp + ".FAFIREST.txt";
			String extractFileName = FileName + timestamp + ".FAFIREST.txt";
			PrintStream p;
			p = Set_OutFile(env, OutFileName);
			double dblTotalAmount = 0.00;
			double dblTransAmount = 0.00;
			String strTotalAmount = "";
			double dblLastTransAmount = 0.00;
			double currentTransAmount = 0.00;
			double dblIncidentBlmAcctCode = 0.00;
			String strLastTransAmount = "";
			String strBlmAcctCnt = "";
			int intLastCnt = 0;
			String strAmount = "";
			String strLastAmount = "";
			double dblAmtDocumentCurrency = 0.00;
			String lastBLMacctCode = "";
			String strTransAmount = "";
			String TransactionNo = "";
			String TransHeaderKey = "";
			String strCostCenter = "";
			String strWBS = "";
			String strFunctionalArea = "";
			double dblAmount = 0.00;
			String strInterfaceType = "";
			String PMSAmtLine = "";
			String strPMSAmountPostingKey = "";
			String InterfacePostingType = "ET";
			NodeList BillingTransaction = BillingOutDoc.getDocumentElement()
					.getElementsByTagName("NWCGBillingTransaction");
			BillingTransactionCount = BillingTransaction.getLength();
			ArrayList al = new ArrayList();
			ArrayList alAmount = new ArrayList();
			if (BillingTransactionCount > 0) {
				for (int cnt = 0; cnt < BillingTransactionCount; cnt++) {
					Element BillingTransactionl = (Element) BillingTransaction
							.item(cnt);
					String ItemProductLine = BillingTransactionl
							.getAttribute("ItemProductLine");
					// value from xml
					// IncidentBlmAcctCode="LL61460278.L00020080725.L23456789.111807"
					strIncidentBlmAcctCode = BillingTransactionl
							.getAttribute("IncidentBlmAcctCode");
					// CR 589 sysouts remove later
					// CR 589 strIncidentBlmAcctCode format
					// LLCC....CC.LLFA..........FA.LLWBS....WBS
					// <costcenter10chars>.<functionalarea16chars>.<wbsoptional12chars>
					// blmacccode can be 28 chars or 40 chars
					// if((!strIncidentBlmAcctCode.equals("")) &&
					// (strIncidentBlmAcctCode.length() == 40))
					// CR 589 wbs is optional
					if ((strIncidentBlmAcctCode.length() == 28)
							|| (strIncidentBlmAcctCode.length() == 40)) {
						// String ItemProductLine =
						// BillingTransactionl.getAttribute("ItemProductLine");
						strCostCenter = strIncidentBlmAcctCode.substring(0, 10);
						// strWBS = strIncidentBlmAcctCode.substring(11,23);
						// strFunctionalArea =
						// strIncidentBlmAcctCode.substring(24,40);
						// cr 589 swapped positions of functional area and wbs
						strFunctionalArea = strIncidentBlmAcctCode.substring(
								11, 27);
						if ((strIncidentBlmAcctCode.length() == 40))
							strWBS = strIncidentBlmAcctCode.substring(28, 40);
						else
							strWBS = "";
						TransAmount = BillingTransactionl
								.getAttribute("TransAmount");
						if (ItemProductLine.equals("PMS Publications")) {
							PMSTransAmount = TransAmount.substring(1);
							printPMSLine = true;
						} else {
							PMSTransAmount = "";
						}
						strTransAmount = TransAmount.substring(0, 1);
						if (strTransAmount.equals("-")) {
							PostingKey = "50";
						} else {
							PostingKey = "40";
						}
						// for the update to billing transaction
						SequenceKey = BillingTransactionl
								.getAttribute("SequenceKey");
						TransHeaderKey = BillingTransactionl
								.getAttribute("TransHeaderKey");
						TransNo = BillingTransactionl
								.getAttribute("TransLineKey");
						CacheId = BillingTransactionl.getAttribute("CacheId");
						strInterfaceType = CacheId.substring(0, 2);
						SequenceKey = BillingTransactionl
								.getAttribute("SequenceKey");
						DocumentNo = BillingTransactionl
								.getAttribute("DocumentNo");
						TransactionNo = BillingTransactionl
								.getAttribute("TransactionNo");
						if (strInterfaceType.equals("AK")) {
							InterfaceType = "AF";
						} else {
							InterfaceType = "NF";
						}
						BillingExtractLine = BusinessArea + "|" + InterfaceType
								+ "|" + InterfacePostingType + "|"
								+ DocumentDate + "|" + PostingDate + "|"
								+ FiscalPeriod + "|";
						if (al.contains(strIncidentBlmAcctCode)) {
						} else {
							if (al.size() > 0) {
								// commenting billing extract line as its
								// duplicate of line above
								// BillingExtractLine = BusinessArea + "|" +
								// InterfaceType + "|" + DocumentDate + "|" +
								// PostingDate + "|" + FiscalPeriod + "|";
								if (lastBLMacctCode.length() >= 28) {
									CostCenter = lastBLMacctCode.substring(0,
											10);
									// WBS = lastBLMacctCode.substring(11,23);
									// FunctionalArea =
									// lastBLMacctCode.substring(24,40);
									// cr 589 swapped positions of wbs and
									// functional area
									FunctionalArea = lastBLMacctCode.substring(
											11, 27);
									if (lastBLMacctCode.length() == 40)
										WBS = lastBLMacctCode.substring(28, 40);
									else
										WBS = "";
								} else {
									CostCenter = "";
									FunctionalArea = "";
									WBS = "";
								}
								dblTotalAmount = df.parse(
										df.format(dblTotalAmount))
										.doubleValue();
								amount = Double.toString(dblTotalAmount);
								strTransAmount = amount.substring(0, 1);
								if (strTransAmount.equals("-")) {
									amount = amount.substring(1);
									;
								}
								if (strTransAmount.equals("-")) {
									strPostingKey = "50";
								} else {
									strPostingKey = "40";
								}
								int intDecimal = amount.indexOf(".");
								int intDecimalIndex = (intDecimal + 1);
								String strDecimal = amount
										.substring(intDecimalIndex);
								if (strDecimal.length() < 2) {
									amount = (amount + "0");
								}
								AmtDocumentCurrency = getAmtDocumentCurrencyFormat(amount);
								BillingExtractLine += ReferenceDocumentNumber
										+ "|" + DocumentHeaderText + "|"
										+ strPostingKey + "|";
								BillingExtractLine += GLAcctCode + "|";
								BillingExtractLine += AmtDocumentCurrency
										+ "||";
								BillingExtractLine += ItemText + "|";
								BillingExtractLine += CostCenter + "|"
										+ OrderNo + "|" + CommitmentItem + "|";
								BillingExtractLine += WBS + "|" + FundsCenter
										+ "|" + Fund + "|";
								BillingExtractLine += DocNoForEarmarkedFunds
										+ "|" + EarmarkedFundsDocumentNo + "|"
										+ FunctionalArea + "|||||";
								if (ItemProductLine.equals("PMS Publications")) {
									// pms publications dont print into the
									// report
								} else {
									p.println(BillingExtractLine);
								}
								dblTotalAmount = 0.00;
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
								strLastAmount = alAmount.get(intLastIndex)
										.toString();
								lastBLMacctCode = al.get(intLastIndex)
										.toString();
								;
								if (lastBLMacctCode
										.equals(strIncidentBlmAcctCode)) {
									// CR 589 do not add the transamount of pms
									// publications to the total amount
									// if(!ItemProductLine.equals("PMS Publications")){
									dblTotalAmount = dblTotalAmount
											+ currentTransAmount;
									// }
								}
							} // end of if(intLastIndex >= 0)
						}
					} else {
						// obselete if the control never enters the condition as
						// empty string can never have length of 40
						// if((BillingTransactionCount == (cnt + 1)) &&
						// (strIncidentBlmAcctCode.equals("") &&
						// (strIncidentBlmAcctCode.length() == 40)))
						if ((BillingTransactionCount == (cnt + 1))
								&& (strIncidentBlmAcctCode.equals(""))) {
							BillingExtractLine = BusinessArea + "|"
									+ InterfaceType + "|"
									+ InterfacePostingType + "|" + DocumentDate
									+ "|" + PostingDate + "|" + FiscalPeriod
									+ "|";
							if (lastBLMacctCode.length() >= 28) {
								CostCenter = lastBLMacctCode.substring(0, 10);
								// WBS = lastBLMacctCode.substring(11,23);
								// FunctionalArea =
								// lastBLMacctCode.substring(24,40);
								FunctionalArea = lastBLMacctCode.substring(11,
										27);
								if (lastBLMacctCode.length() == 40)
									WBS = lastBLMacctCode.substring(28, 40);
								else
									WBS = "";
							} else {
								CostCenter = "";
								FunctionalArea = "";
								WBS = "";
							}
							dblTotalAmount = df
									.parse(df.format(dblTotalAmount))
									.doubleValue();
							amount = Double.toString(dblTotalAmount);
							strTransAmount = amount.substring(0, 1);
							if (strTransAmount.equals("-")) {
								amount = amount.substring(1);
								;
							}
							if (strTransAmount.equals("-")) {
								strPostingKey = "50";
							} else {
								strPostingKey = "40";
							}
							int intDecimal = amount.indexOf(".");
							int intDecimalIndex = (intDecimal + 1);
							String strDecimal = amount
									.substring(intDecimalIndex);
							if (strDecimal.length() < 2) {
								amount = (amount + "0");
							}
							AmtDocumentCurrency = getAmtDocumentCurrencyFormat(amount);
							BillingExtractLine += ReferenceDocumentNumber + "|"
									+ DocumentHeaderText + "|" + strPostingKey
									+ "|";
							BillingExtractLine += GLAcctCode + "|";
							BillingExtractLine += AmtDocumentCurrency + "||";
							BillingExtractLine += ItemText + "|";
							BillingExtractLine += CostCenter + "|" + OrderNo
									+ "|" + CommitmentItem + "|";
							BillingExtractLine += WBS + "|" + FundsCenter + "|"
									+ Fund + "|";
							BillingExtractLine += DocNoForEarmarkedFunds + "|"
									+ EarmarkedFundsDocumentNo + "|"
									+ FunctionalArea + "|||||";
							// if (!ItemProductLine.equals("PMS Publications")){
							// pms publications dont print into the report
							// }else {
							p.println(BillingExtractLine);
							// }
							dblTotalAmount = 0.00;
						}
					}
					if ((BillingTransactionCount == (cnt + 1))
							&& (strIncidentBlmAcctCode.equals(""))) {
						BillingExtractLine = BusinessArea + "|" + InterfaceType
								+ "|" + InterfacePostingType + "|"
								+ DocumentDate + "|" + PostingDate + "|"
								+ FiscalPeriod + "|";
						if (lastBLMacctCode.length() >= 28) {
							CostCenter = lastBLMacctCode.substring(0, 10);
							// WBS = lastBLMacctCode.substring(11,23);
							// FunctionalArea =
							// lastBLMacctCode.substring(24,40);
							// cr 589 swapped positions of wbs and functional
							// area
							FunctionalArea = lastBLMacctCode.substring(11, 27);
							if (lastBLMacctCode.length() == 40)
								WBS = lastBLMacctCode.substring(28, 40);
							else
								WBS = "";
						} else {
							CostCenter = "";
							FunctionalArea = "";
							WBS = "";
						}
						dblTotalAmount = df.parse(df.format(dblTotalAmount))
								.doubleValue();
						amount = Double.toString(dblTotalAmount);
						strTransAmount = amount.substring(0, 1);
						if (strTransAmount.equals("-")) {
							amount = amount.substring(1);
							;
						}
						if (strTransAmount.equals("-")) {
							strPostingKey = "50";
						} else {
							strPostingKey = "40";
						}
						int intDecimal = amount.indexOf(".");
						int intDecimalIndex = (intDecimal + 1);
						String strDecimal = amount.substring(intDecimalIndex);
						if (strDecimal.length() < 2) {
							amount = (amount + "0");
						}
						AmtDocumentCurrency = getAmtDocumentCurrencyFormat(amount);
						BillingExtractLine += ReferenceDocumentNumber + "|"
								+ DocumentHeaderText + "|" + strPostingKey
								+ "|";
						BillingExtractLine += GLAcctCode + "|";
						BillingExtractLine += AmtDocumentCurrency + "||";
						BillingExtractLine += ItemText + "|";
						BillingExtractLine += CostCenter + "|" + OrderNo + "|"
								+ CommitmentItem + "|";
						BillingExtractLine += WBS + "|" + FundsCenter + "|"
								+ Fund + "|";
						BillingExtractLine += DocNoForEarmarkedFunds + "|"
								+ EarmarkedFundsDocumentNo + "|"
								+ FunctionalArea + "|||||";
						// if (!ItemProductLine.equals("PMS Publications")){
						// pms publications dont print into the report
						// }else {
						p.println(BillingExtractLine);
						// }
						dblTotalAmount = 0.00;
					}
					if ((BillingTransactionCount == 1)) {
						BillingExtractLine = BusinessArea + "|" + InterfaceType
								+ "|" + InterfacePostingType + "|"
								+ DocumentDate + "|" + PostingDate + "|"
								+ FiscalPeriod + "|";
						if (lastBLMacctCode.length() >= 28) {
							CostCenter = lastBLMacctCode.substring(0, 10);
							// WBS = lastBLMacctCode.substring(11,23);
							// FunctionalArea =
							// lastBLMacctCode.substring(24,40);
							// cr 589 swapped positions of wbs and functional
							// area
							FunctionalArea = lastBLMacctCode.substring(11, 27);
							if (lastBLMacctCode.length() == 40)
								WBS = lastBLMacctCode.substring(28, 40);
							else
								WBS = "";
						} else {
							CostCenter = "";
							FunctionalArea = "";
							WBS = "";
						}
						dblTotalAmount = df.parse(df.format(dblTotalAmount))
								.doubleValue();
						amount = Double.toString(dblTotalAmount);
						strTransAmount = amount.substring(0, 1);
						if (strTransAmount.equals("-")) {
							amount = amount.substring(1);
							;
						}
						if (strTransAmount.equals("-")) {
							strPostingKey = "50";
						} else {
							strPostingKey = "40";
						}
						int intDecimal = amount.indexOf(".");
						int intDecimalIndex = (intDecimal + 1);
						String strDecimal = amount.substring(intDecimalIndex);
						if (strDecimal.length() < 2) {
							amount = (amount + "0");
						}
						AmtDocumentCurrency = getAmtDocumentCurrencyFormat(amount);
						BillingExtractLine += ReferenceDocumentNumber + "|"
								+ DocumentHeaderText + "|" + strPostingKey
								+ "|";
						BillingExtractLine += GLAcctCode + "|";
						BillingExtractLine += AmtDocumentCurrency + "||";
						BillingExtractLine += ItemText + "|";
						BillingExtractLine += CostCenter + "|" + OrderNo + "|"
								+ CommitmentItem + "|";
						BillingExtractLine += WBS + "|" + FundsCenter + "|"
								+ Fund + "|";
						BillingExtractLine += DocNoForEarmarkedFunds + "|"
								+ EarmarkedFundsDocumentNo + "|"
								+ FunctionalArea + "|||||";
						// if (!ItemProductLine.equals("PMS Publications")){
						// pms publications dont print into the report
						// }else {
						p.println(BillingExtractLine);
						// }
						dblTotalAmount = 0.00;
					}
					if ((BillingTransactionCount == (cnt + 1))
							&& (dblTotalAmount != 0)) {
						if (lastBLMacctCode.length() >= 28) {
							CostCenter = lastBLMacctCode.substring(0, 10);
							// WBS = lastBLMacctCode.substring(11,23);
							// FunctionalArea =
							// lastBLMacctCode.substring(24,40);
							// cr 589 swapped positions of wbs and functional
							// area
							FunctionalArea = lastBLMacctCode.substring(11, 27);
							if (lastBLMacctCode.length() == 40)
								WBS = lastBLMacctCode.substring(28, 40);
							else
								WBS = "";
						} else {
							CostCenter = "";
							FunctionalArea = "";
							WBS = "";
						}
						// dblTotalAmount - FORMAT THE ACCUMULATED TOTAL
						dblTotalAmount = df.parse(df.format(dblTotalAmount))
								.doubleValue();
						amount = Double.toString(dblTotalAmount);
						strTransAmount = amount.substring(0, 1);
						if (strTransAmount.equals("-")) {
							amount = amount.substring(1);
							;
						}
						if (strTransAmount.equals("-")) {
							strPostingKey = "50";
						} else {
							strPostingKey = "40";
						}
						int intDecimal = amount.indexOf(".");
						int intDecimalIndex = (intDecimal + 1);
						String strDecimal = amount.substring(intDecimalIndex);
						if (strDecimal.length() < 2) {
							amount = (amount + "0");

						}
						AmtDocumentCurrency = getAmtDocumentCurrencyFormat(amount);
						BillingExtractLine += ReferenceDocumentNumber + "|"
								+ DocumentHeaderText + "|" + strPostingKey
								+ "|";
						BillingExtractLine += GLAcctCode + "|";
						BillingExtractLine += AmtDocumentCurrency + "||";
						BillingExtractLine += ItemText + "|";
						BillingExtractLine += CostCenter + "|" + OrderNo + "|"
								+ CommitmentItem + "|";
						BillingExtractLine += WBS + "|" + FundsCenter + "|"
								+ Fund + "|";
						BillingExtractLine += DocNoForEarmarkedFunds + "|"
								+ EarmarkedFundsDocumentNo + "|"
								+ FunctionalArea + "|||||";
						// if (!ItemProductLine.equals("PMS Publications")){
						// pms publications dont print into the report
						// }else {
						p.println(BillingExtractLine);
						// }
					}
					if (strIncidentBlmAcctCode.length() == 40
							|| strIncidentBlmAcctCode.length() == 28) {
						String strItemProductLine = BillingTransactionl
								.getAttribute("ItemProductLine");
						if (!strItemProductLine
								.equalsIgnoreCase("PMS Publications")) {
							dblAmount = dblAmount + currentTransAmount;
						}
						if (!PMSTransAmount.equals("")) {
							dblPMSTransAmount = Double
									.parseDouble(PMSTransAmount);
							dblPMSTransAmount = df.parse(
									df.format(dblPMSTransAmount)).doubleValue();
						}
						// inserts the billing transaction into
						// nwcg_billing_trans_extract table
						InsertBillingTransactionExtract(env, BusinessArea,
								InterfaceType, InterfacePostingType,
								DocumentDate, PostingDate, FiscalPeriod,
								ReferenceDocumentNumber, DocumentHeaderText,
								PostingKey, GLAcctCode, TransAmount, ItemText,
								CompanyID, strCostCenter, OrderNo,
								CommitmentItem, strWBS, FundsCenter, Fund,
								DocNoForEarmarkedFunds,
								EarmarkedFundsDocumentNo, strFunctionalArea,
								TransNo, DocumentNo, FiscalYear, TransAmount,
								SequenceKey, extractFileName, CacheId,
								PMSTransAmount, strIncidentBlmAcctCode,
								TransactionNo);
					}
				}
				// PMS AMOUNT SECTION
				dbltotalPMSTransAmount = dbltotalPMSTransAmount
						+ dblPMSTransAmount;
				dbltotalPMSTransAmount = (dbltotalPMSTransAmount - (dbltotalPMSTransAmount / (1.4)));
				dbltotalPMSTransAmount = df.parse(
						df.format(dbltotalPMSTransAmount)).doubleValue();
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
				PMSAmtLine = BusinessArea + "|" + InterfaceType + "|"
						+ InterfacePostingType + "|" + DocumentDate + "|"
						+ PostingDate + "|" + FiscalPeriod + "|";
				PMSAmtLine += ReferenceDocumentNumber + "|"
						+ DocumentHeaderText + "|" + strPMSAmountPostingKey
						+ "|" + GLAcctCode + "|";
				PMSAmtLine += strPMSamount + "||";
				PMSAmtLine += ItemText + "|" + CostCenter + "|" + OrderNo + "|"
						+ CommitmentItem + "|";
				PMSAmtLine += WBS + "|" + FundsCenter + "|" + Fund + "|";
				PMSAmtLine += DocNoForEarmarkedFunds + "|"
						+ EarmarkedFundsDocumentNo + "|" + FunctionalArea
						+ "|||||";
				// FINAL OFFSET LINE
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
				String strFinalOffsetAmountDecimal = FinalOffsetAmount
						.substring(intFinalOffsetDecimalIndex);
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
				FinalOffesttingRecordLine = BusinessArea + "|" + InterfaceType
						+ "|" + InterfacePostingType + "|" + DocumentDate + "|"
						+ PostingDate + "|" + FiscalPeriod + "|";
				FinalOffesttingRecordLine += ReferenceDocumentNumber + "|"
						+ DocumentHeaderText + "|" + strFinalOffsetPostingKey
						+ "|" + GLAcctCode + "|";
				FinalOffesttingRecordLine += FinalOffsetAmount + "||";
				FinalOffesttingRecordLine += ItemText + "|" + CostCenter + "|"
						+ OrderNo + "|" + CommitmentItem + "|";
				FinalOffesttingRecordLine += WBS + "|" + FundsCenter + "|"
						+ Fund + "|";
				FinalOffesttingRecordLine += DocNoForEarmarkedFunds + "|"
						+ EarmarkedFundsDocumentNo + "|" + FunctionalArea
						+ "|||||";
			}
			if (printPMSLine) {
				p.println(PMSAmtLine);
			}
			p.println(FinalOffesttingRecordLine);
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
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransactionExtractFTP' "
							+ "DetailDescription='Error in Opening File : "
							+ outfile
							+ ",during NWCGProcessBillingTransactionExtractFTP process'");
			throwAlert(env, stbuf);
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingTransactionExtractFTP::Error in Opening File  "
								+ outfile);
		}
		ps = new PrintStream(out);
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
			String CacheId, String PMSTransAmount, String IncidentBlmAcctCode,
			String TransactionNo) throws Exception {
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
		//BillDocElem.setAttribute("TradingPartnerCompanyId", CompanyID);
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
		BillDocElem.setAttribute("IncidentBlmAcctCode", IncidentBlmAcctCode);
		BillDocElem.setAttribute("TransactionNo", TransactionNo);
		try {
			CommonUtilities
					.invokeService(
							env,
							NWCGConstants.NWCG_CREATE_BILLING_TRANSACTION_EXTRACT_SERVICE,
							BillDoc);
			UpdateBillingTransaction(env, TransNo, FiscalYear, SequenceKey);
		} catch (Exception e) {
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
			//jimmy- added details to throwalert
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransactionExtractFTP' "
							+ "DetailDescription='Error in Inserting NWCG_BILLING_TRANSACTION_EXTRACT Record during NWCGProcessBillingTransactionExtractFTP process.  CacheID: "
							+ CacheId + " , ExtractTransNo: " + TransNo
							+ ", Transaction No: " + TransactionNo
							+ " DocumentNo: " + DocumentNo + " , OrderNo: "
							+ OrderNo + " '");
			throwAlert(env, stbuf);
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingTransactionExtractFTP::Error in Inserting NWCG_BILLING_TRANSACTION_EXTRACT Record for ");
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
			CommonUtilities.invokeService(env,
					NWCGConstants.NWCG_UPDATE_BILLING_TRANSACTION_SERVICE,
					BillDoc);
		} catch (Exception e) {
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingExtract::updateBillingTransactionRecord Caught Exception "
								+ e);
			e.printStackTrace();
			//			Jimmy - Original throwalert
			/*StringBuffer stbuf = new StringBuffer(
			 "<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransactionExtractFTP' "
			 + " Extract Transaction No : " + TransNo + "'");
			 throwAlert(env, stbuf);*/
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransactionExtractFTP' "
							+ " DetailDescription='Error in NWCGProcessBillingTransactionExtractFTP when updateBillingTransactionRecord for TransactionNo: "
							+ TransNo + " , SequenceKey: " + SequenceKey + "'");
			throwAlert(env, stbuf);
		}
		// }
	}

	public static String getAmtDocumentCurrencyFormat(String amount) {
		String AmtDocumentCurrency = "";
		AmtDocumentCurrency = StringUtil.prepadStringWithZeros(amount, 12);
		return AmtDocumentCurrency;
	}
}