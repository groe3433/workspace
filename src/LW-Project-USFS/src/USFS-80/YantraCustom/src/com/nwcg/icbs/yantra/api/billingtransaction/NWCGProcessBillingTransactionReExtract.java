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
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGProcessBillingTransactionReExtract implements YIFCustomApi {
	
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

	public Document processExtract(YFSEnvironment env, Document inXML)
			throws Exception {
		if (logger.isVerboseEnabled()) {
			logger
					.verbose("Entering NWCGProcessBillingTransactionReExtractFTP, Input document is:"
							+ XMLUtil.getXMLString(inXML));
		}

		/*
		 * 1) get all entries in nwcg_billing_trans_extract that have the incoming file name
		 * 2) add all the values per BLM acct code
		 * 3) write to file
		 * 
		 * Incoming file:
		 * <NWCGBillingTransExtract IgnoreOrdering="Y" PostExtractSequenceKey="200912140948283831253" />
		 */

		/*
		 Element elemRoot = null;
		 
		 elemRoot = inXML.getDocumentElement();
		 if(elemRoot != null)
		 {
		 String PostExtractSequenceKey 		= elemRoot.getAttribute("ExtractSequenceKey");
		 
		 Document BillingTransExtractInDoc 	= XMLUtil.newDocument();
		 Document BillingTransExtractOutDoc 	= XMLUtil.newDocument();

		 
		 Element el_NWCGBillingTransExtract=BillingTransExtractInDoc.createElement("NWCGBillingTransExtract");
		 BillingTransExtractInDoc.appendChild(el_NWCGBillingTransExtract);
		 el_NWCGBillingTransExtract.setAttribute("PostExtractSequenceKey",PostExtractSequenceKey);
		 
		 System.out.println("BillingTransExtract IN DOC "+ XMLUtil.getXMLString(BillingTransExtractInDoc));
		 BillingTransExtractOutDoc = CommonUtilities.invokeService(env,NWCGConstants.NWCG_GET_BILLING_TRANSACTION_EXTRACT_DETAIL_SERVICE ,BillingTransExtractInDoc);
		 System.out.println("BillingTransExtract Out Doc "+ XMLUtil.getXMLString(BillingTransExtractOutDoc));
		 
		 
		 
		 //Element NWCGBillingTransExtract = BillingTransExtractOutDoc.getDocumentElement();
		 //String ExtractFileName 		= NWCGBillingTransExtract.getAttribute("ExtractFileName");
		 String ExtractFileName 		= BillingTransExtractOutDoc.getDocumentElement().getAttribute("ExtractFileName");
		 
		 Document BillingTransExtractListInDoc 		= XMLUtil.newDocument();
		 Document BillingTransExtractFileListOutDoc 	= XMLUtil.newDocument();
		 Element el_NWCGBillingTransExtractList		=BillingTransExtractListInDoc.createElement("NWCGBillingTransExtract");
		 BillingTransExtractListInDoc.appendChild(el_NWCGBillingTransExtractList);
		 el_NWCGBillingTransExtractList.setAttribute("ExtractFileName",ExtractFileName);
		 System.out.println("BillingTransExtractList IN DOC "+ XMLUtil.getXMLString(BillingTransExtractListInDoc));
		 BillingTransExtractFileListOutDoc = CommonUtilities.invokeService(env,NWCGConstants.NWCG_GET_BILLING_TRANSACTION_EXTRACT_LIST_SERVICE ,BillingTransExtractListInDoc);
		 System.out.println("BillingTransExtractList Out Doc "+ XMLUtil.getXMLString(BillingTransExtractFileListOutDoc));
		 
		 NodeList ExtractList = BillingTransExtractFileListOutDoc.getDocumentElement().getElementsByTagName("NWCGBillingTransExtract");
		 int ExtractListCount = ExtractList.getLength();
		 System.out.println("ExtractListCount "+ ExtractListCount);
		 
		 ArrayList al = new ArrayList(); 
		 ArrayList alAmount = new ArrayList();
		 String lastBLMacctCode = "";
		 //dbl dblTotalAmount = 0;
		 String amount = "";
		 String FileName 	= "ReExtractFile-";
		 String DocumentDate	= "";
		 String PostingDate 	= "";
		 NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
		 double dblTotalAmount = 0;
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
		 String EarmarkedFundsDocumentNo ="";
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
		 
		 java.text.DecimalFormat df = new
		 java.text.DecimalFormat("###.########");
		 
		 
		 String strTimestamp = reportsUtil.dateToString(new java.util.Date(), "yyyy-MM-dd'T'HH:mm:ss");
		 //2009-11-06T12:15:17
		 String strYear 		= strTimestamp.substring(0,4);
		 String strMonth		= strTimestamp.substring(5,7);
		 String strDay 		= strTimestamp.substring(8,10);
		 String strHour 		= strTimestamp.substring(11,13);
		 String strMinute 	= strTimestamp.substring(14,16);
		 String strSecond 	= strTimestamp.substring(17,19);
		 
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
		 String 	OutFileName = NWCGConstants.NWCG_BLM_OUTPUT_DIR + FileName + timestamp + ".txt";
		 
		 PrintStream p; 
		 p = Set_OutFile(env,OutFileName);
		 
		 if(ExtractListCount>0){
		 for(int cnt=0;cnt<ExtractListCount;cnt++){
		 Element BillingTransactionIteml 	= (Element)ExtractList.item(cnt);
		 System.out.println("8888===================8888=========================8888=======================8888=================8888==========8888");
		 System.out.println("ExtractListCount = " + cnt);
		 
		 String BusinessArea 					= BillingTransactionIteml.getAttribute("BusinessArea");
		 String InterfaceType					= BillingTransactionIteml.getAttribute("InterfaceType");
		 String ReferenceDocumentNumber 			= BillingTransactionIteml.getAttribute("ReferenceDocumentNumber");
		 String DocumentHeaderText 				= BillingTransactionIteml.getAttribute("DocumentHeaderText");
		 String PostingKey						= BillingTransactionIteml.getAttribute("PostingKey");
		 String GLAcctCode						= BillingTransactionIteml.getAttribute("GLAcctCode");
		 
		 String strCostCenter					= BillingTransactionIteml.getAttribute("CostCenter");
		 String strFunctionalArea				= BillingTransactionIteml.getAttribute("FunctionalArea");
		 String strWBS							= BillingTransactionIteml.getAttribute("WBS");
		 String TransAmount 						= BillingTransactionIteml.getAttribute("AmtInDocCurrency");
		 String strIncidentBlmAcctCode			= BillingTransactionIteml.getAttribute("IncidentBlmAcctCode");
		 String strTransAmount					= "";
		 
		 System.out.println("strCostCenter: 			" + strCostCenter);
		 System.out.println("strFunctionalArea: 		" + strFunctionalArea);
		 System.out.println("strWBS: 				" + strWBS);
		 System.out.println("strIncidentBlmAcctCode: " + strIncidentBlmAcctCode);
		 System.out.println("TransAmount: 			" + TransAmount);
		 System.out.println("strTransAmount: 		" + strTransAmount);
		 
		 
		 
		 
		 
		 if( al.contains( strIncidentBlmAcctCode ) )
		 {
		 //System.out.println("ArrayList strIncidentBlmAcctCode as value");
		 }else{
		 //System.out.println("ArrayList does not contain strIncidentBlmAcctCode as value - does dblTotalAmount = " + dblTotalAmount);
		 if(cnt>0)
		 {
		 
		 CostCenter						= lastBLMacctCode.substring(0,10);
		 WBS								= lastBLMacctCode.substring(11,23);
		 FunctionalArea					= lastBLMacctCode.substring(24,40);
		 
		 //dblTotalAmount - FORMAT Tx`HE ACCUMULATED TOTAL
		 dblTotalAmount = df.parse(df.format(dblTotalAmount)).doubleValue();
		 amount = Double.toString(dblTotalAmount);
		 strTransAmount = amount.substring(0,1);
		 System.out.println("strTransAmount 	: "+strTransAmount);
		 System.out.println("amount 			: "+amount);
		 if(strTransAmount.equals("-"))
		 {
		 amount = amount.substring(1);
		 } 
		 System.out.println("(amount)  : "+amount);
		 if(amount.length() == 1 ){
		 AmtDocumentCurrency = "0000000000.0" + amount;
		 } else if (amount.length() == 2 ){
		 AmtDocumentCurrency = "0000000." + amount;
		 } else if (amount.length() == 3 ){
		 AmtDocumentCurrency = "00000000" + amount;
		 } else if (amount.length() == 4 ){
		 AmtDocumentCurrency = "000000000" + amount;
		 } else if (amount.length() == 5 ){
		 AmtDocumentCurrency = "00000000" + amount;
		 } else if (amount.length() == 6 ){
		 AmtDocumentCurrency = "0000000" + amount;
		 } else if (amount.length() == 7 ){
		 AmtDocumentCurrency = "000000" + amount;
		 } else if (amount.length() == 8 ){
		 AmtDocumentCurrency = "00000" + amount;
		 } else if (amount.length() == 9 ){
		 AmtDocumentCurrency = "0000" + amount;
		 } else if (amount.length() == 10 ){
		 AmtDocumentCurrency = "000" + amount;
		 } else if (amount.length() == 11 ){
		 AmtDocumentCurrency = "00" + amount;
		 } else if (amount.length() == 12 ){
		 AmtDocumentCurrency = "0" + amount;
		 } else {
		 AmtDocumentCurrency = amount;
		 }
		 
		 BillingExtractLine = BusinessArea + "|" + InterfaceType + "|" + DocumentDate + "|" + PostingDate + "|" + FiscalPeriod + "|";
		 BillingExtractLine += ReferenceDocumentNumber + "|" + DocumentHeaderText + "|" + PostingKey + "|";
		 BillingExtractLine += GLAcctCode + "|";
		 BillingExtractLine += AmtDocumentCurrency + "|";
		 BillingExtractLine += ItemText + "|";
		 BillingExtractLine += CompanyID + "|" + CostCenter + "|" + OrderNo + "|" + CommitmentItem + "|";
		 BillingExtractLine += WBS + "|" + FundsCenter + "|" + Fund + "|";
		 BillingExtractLine += DocNoForEarmarkedFunds + "|" + EarmarkedFundsDocumentNo + "|" + FunctionalArea + "|||||||";
		 p.println(BillingExtractLine);
		 
		 dblTotalAmount = 0;
		 al.clear();
		 alAmount.clear();
		 }
		 }
		 
		 al.add(strIncidentBlmAcctCode);
		 System.out.println("Contents of al: " + al); 
		 alAmount.add(TransAmount);
		 System.out.println("Contents of al: " + alAmount); 
		 System.out.println("ArrayList contains " + al.size() + " BLM account codes."); 

		 
		 currentTransAmount		 		= Double.parseDouble(TransAmount);
		 
		 
		 int intLastIndex = al.size() - 1;
		 //System.out.println("intLastIndex " + intLastIndex);
		 int intIndexCount = 0;
		 intIndexCount = cnt + 1;
		 //System.out.println("intIndexCount details:	" + intIndexCount);

		 if(al.size() >= 1)
		 {
		 
		 if(intLastIndex >= 0)
		 {
		 strLastAmount 					= alAmount.get(intLastIndex).toString();
		 lastBLMacctCode					= al.get(intLastIndex).toString();;
		 
		 
		 if(lastBLMacctCode.equals(strIncidentBlmAcctCode))
		 {
		 System.out.println("*****************************************************");
		 //System.out.println("IF *1* THEN (dblTotalAmount) "+dblTotalAmount);
		 System.out.println("currentTransAmount			: " + currentTransAmount);
		 System.out.println("dblTotalAmount			: " + dblTotalAmount);
		 
		 dblTotalAmount			=	dblTotalAmount	+ 	currentTransAmount;
		 
		 System.out.println("RUNNING TOTAL (dblTotalAmount)	: " + dblTotalAmount);
		 System.out.println("*****************************************************");
		 
		 } 
		 } // end of if(intLastIndex >= 0)
		 } 
		 
		 
		 if(ExtractListCount == intIndexCount)			        	 
		 {
		 System.out.println("LAST LINE OF XML");
		 
		 CostCenter						= lastBLMacctCode.substring(0,10);
		 WBS								= lastBLMacctCode.substring(11,23);
		 FunctionalArea					= lastBLMacctCode.substring(24,40);
		 
		 //dblTotalAmount - FORMAT THE ACCUMULATED TOTAL
		 dblTotalAmount = df.parse(df.format(dblTotalAmount)).doubleValue();
		 amount = Double.toString(dblTotalAmount);
		 strTransAmount = amount.substring(0,1);
		 System.out.println("strTransAmount 	: " + strTransAmount);
		 System.out.println("amount 			: " + amount);
		 if(strTransAmount.equals("-"))
		 {
		 System.out.println("FIRST CHARACTER IS A '-'");
		 amount = amount.substring(1);
		 } 
		 System.out.println("(amount) : "+amount);
		 if(amount.length() == 1 ){
		 AmtDocumentCurrency = "0000000000.0" + amount;
		 } else if (amount.length() == 2 ){
		 AmtDocumentCurrency = "0000000." + amount;
		 } else if (amount.length() == 3 ){
		 AmtDocumentCurrency = "00000000" + amount;
		 } else if (amount.length() == 4 ){
		 AmtDocumentCurrency = "000000000" + amount;
		 } else if (amount.length() == 5 ){
		 AmtDocumentCurrency = "00000000" + amount;
		 } else if (amount.length() == 6 ){
		 AmtDocumentCurrency = "0000000" + amount;
		 } else if (amount.length() == 7 ){
		 AmtDocumentCurrency = "000000" + amount;
		 } else if (amount.length() == 8 ){
		 AmtDocumentCurrency = "00000" + amount;
		 } else if (amount.length() == 9 ){
		 AmtDocumentCurrency = "0000" + amount;
		 } else if (amount.length() == 10 ){
		 AmtDocumentCurrency = "000" + amount;
		 } else if (amount.length() == 11 ){
		 AmtDocumentCurrency = "00" + amount;
		 } else if (amount.length() == 12 ){
		 AmtDocumentCurrency = "0" + amount;
		 } else {
		 AmtDocumentCurrency = amount;
		 }
		 
		 BillingExtractLine = BusinessArea + "|" + InterfaceType + "|" + DocumentDate + "|" + PostingDate + "|" + FiscalPeriod + "|";
		 BillingExtractLine += ReferenceDocumentNumber + "|" + DocumentHeaderText + "|" + PostingKey + "|";
		 BillingExtractLine += GLAcctCode + "|";
		 BillingExtractLine += AmtDocumentCurrency + "|";
		 BillingExtractLine += ItemText + "|";
		 BillingExtractLine += CompanyID + "|" + CostCenter + "|" + OrderNo + "|" + CommitmentItem + "|";
		 BillingExtractLine += WBS + "|" + FundsCenter + "|" + Fund + "|";
		 BillingExtractLine += DocNoForEarmarkedFunds + "|" + EarmarkedFundsDocumentNo + "|" + FunctionalArea + "|||||||";
		 p.println(BillingExtractLine);
		 }		        	
		 System.out.println("8888===================8888=========================8888=======================8888=================8888==========8888");
		 System.out.println(" ");
		 }
		 }
		 /*
		 //get list of all entries in the billing extract table
		 Document BillingTransExtractFileListInDoc 	= XMLUtil.newDocument();
		 Document BillingTransExtractFileListOutDoc 	= XMLUtil.newDocument();
		 
		 Element el_NWCGBillingTransExtractList=BillingTransExtractFileListInDoc.createElement("NWCGBillingTransExtract");
		 BillingTransExtractInDoc.appendChild(el_NWCGBillingTransExtractList);
		 el_NWCGBillingTransExtractList.setAttribute("ExtractFileNam`e",ExtractFileName);
		 
		 System.out.println("BillingTransExtractList IN DOC "+ XMLUtil.getXMLString(BillingTransExtractFileListInDoc));
		 BillingTransExtractFileListOutDoc = CommonUtilities.invokeService(env,NWCGConstants.NWCG_GET_BILLING_TRANSACTION_EXTRACT_LIST_SERVICE ,BillingTransExtractFileListInDoc);
		 System.out.println("BillingTransExtractList Out Doc "+ XMLUtil.getXMLString(BillingTransExtractFileListOutDoc));
		 
		 NodeList BillingTransExtractList  = BillingTransExtractFileListOutDoc.getDocumentElement().getElementsByTagName("NWCGBillingTransExtract");
		 
		 int BillingTransactionExtractListCount = 0;
		 BillingTransactionExtractListCount = BillingTransExtractList.getLength();
		 
		 System.out.println("---------------------------------------------------");
		 System.out.println("BillingTransactionExtractListCount:		" + BillingTransactionExtractListCount); 
		 System.out.println("---------------------------------------------------");
		 
		 
		 if(BillingTransactionExtractListCount>0){
		 for(int cnt=0;cnt<BillingTransactionExtractListCount;cnt++)
		 {
		 String FileName 	= "ReExtractFile";
		 String DocumentDate	= "";
		 String PostingDate 	= "";
		 NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
		 
		 String strTimestamp = reportsUtil.dateToString(new java.util.Date(), "yyyy-MM-dd'T'HH:mm:ss");
		 //2009-11-06T12:15:17
		 String strYear 		= strTimestamp.substring(0,4);
		 String strMonth		= strTimestamp.substring(5,7);
		 String strDay 		= strTimestamp.substring(8,10);
		 String strHour 		= strTimestamp.substring(11,13);
		 String strMinute 	= strTimestamp.substring(14,16);
		 String strSecond 	= strTimestamp.substring(17,19);
		 
		 PostingDate = strYear + "" + strMonth + "" + strDay;
		 DocumentDate = PostingDate;
		 String timestamp = PostingDate + strHour + "" + strMinute + "" + strDay;
		 String 	OutFileName = NWCGConstants.NWCG_BLM_OUTPUT_DIR + FileName + timestamp + ".txt";
		 
		 PrintStream p; 
		 p = Set_OutFile(env,OutFileName);
		 
		 Element BillingTransactionl 	= (Element)BillingTransExtractList.item(cnt);
		 System.out.println("******************************");
		 System.out.println("COUNT = " + cnt);
		 
		 String strIncidentBlmAcctCode			= BillingTransactionl.getAttribute("IncidentBlmAcctCode");
		 String TransAmount 						= BillingTransactionl.getAttribute("TransAmount");
		 System.out.println("TransAmount: " + TransAmount);
		 String strTransAmount					= TransAmount.substring(0,1);
		 System.out.println("strTransAmount: " + strTransAmount);
		 p.println(strIncidentBlmAcctCode);
		 }
		 }
		 */

		/*			
		 PrintStream p; 
		 p = Set_OutFile(env,OutFileName);
		 
		 if(BillingTransactionCount>0){
		 //--------------------------FOR Loop 
		 for(int cnt=0;cnt<BillingTransactionCount;cnt++)
		 {
		 Element BillingTransactionl 	= (Element)BillingTransaction.item(cnt);
		 System.out.println("******************************");
		 System.out.println("cnt = " + cnt);

		 
		 
		 //value from xml IncidentBlmAcctCode="LL61460278.L00020080725.L23456789.111807"
		 strIncidentBlmAcctCode			= BillingTransactionl.getAttribute("IncidentBlmAcctCode");
		 TransAmount 					= BillingTransactionl.getAttribute("TransAmount");
		 System.out.println("TransAmount: " + TransAmount);
		 strTransAmount					= TransAmount.substring(0,1);
		 System.out.println("strTransAmount: " + strTransAmount);
		 if(strTransAmount.equals("-"))
		 {
		 PostingKey = "50";
		 } else {
		 PostingKey = "40";
		 }
		 System.out.println("PostingKey: " + PostingKey);
		 
		 //for the update to billing transaction
		 SequenceKey						= BillingTransactionl.getAttribute("SequenceKey");
		 TransHeaderKey					= BillingTransactionl.getAttribute("TransHeaderKey");
		 
		 TransNo							= BillingTransactionl.getAttribute("TransLineKey");
		 CacheId 						= BillingTransactionl.getAttribute("CacheId");
		 String strInterfaceType			= CacheId.substring(0,2);
		 SequenceKey 					= BillingTransactionl.getAttribute("SequenceKey");
		 DocumentNo 						= BillingTransactionl.getAttribute("DocumentNo");
		 
		 if(strInterfaceType.equals("AK"))
		 {
		 InterfaceType = "AF";
		 } else {
		 InterfaceType = "NF";
		 }
		 String BillingExtractLine = "";
		 
		 BillingExtractLine = BusinessArea + "|" + InterfaceType + "|" + DocumentDate + "|" + PostingDate + "|" + FiscalPeriod + "|";
		 BillingExtractLine += ReferenceDocumentNumber + "|" + DocumentHeaderText + "|" + PostingKey + "|";
		 BillingExtractLine += GLAcctCode + "|";
		 
		 if( al.contains( strIncidentBlmAcctCode ) )
		 {
		 System.out.println("ArrayList strIncidentBlmAcctCode as value");
		 }else{
		 System.out.println("ArrayList does not contain strIncidentBlmAcctCode as value - does dblTotalAmount = " + dblTotalAmount);
		 if(cnt>0)
		 {
		 CostCenter						= lastBLMacctCode.substring(0,10);
		 WBS								= lastBLMacctCode.substring(11,23);
		 FunctionalArea					= lastBLMacctCode.substring(24,40);
		 
		 //dblTotalAmount - FORMAT THE ACCUMULATED TOTAL
		 amount = Double.toString(dblTotalAmount);
		 
		 if(amount.length() == 1 ){
		 AmtDocumentCurrency = "0000000000.0" + amount;
		 } else if (amount.length() == 2 ){
		 AmtDocumentCurrency = "0000000." + amount;
		 } else if (amount.length() == 3 ){
		 AmtDocumentCurrency = "00000000" + amount;
		 } else if (amount.length() == 4 ){
		 AmtDocumentCurrency = "000000000" + amount;
		 } else if (amount.length() == 5 ){
		 AmtDocumentCurrency = "00000000" + amount;
		 } else if (amount.length() == 6 ){
		 AmtDocumentCurrency = "0000000" + amount;
		 } else if (amount.length() == 7 ){
		 AmtDocumentCurrency = "000000" + amount;
		 } else if (amount.length() == 8 ){
		 AmtDocumentCurrency = "00000" + amount;
		 } else if (amount.length() == 9 ){
		 AmtDocumentCurrency = "0000" + amount;
		 } else if (amount.length() == 10 ){
		 AmtDocumentCurrency = "000" + amount;
		 } else if (amount.length() == 11 ){
		 AmtDocumentCurrency = "00" + amount;
		 } else if (amount.length() == 12 ){
		 AmtDocumentCurrency = "0" + amount;
		 } else {
		 AmtDocumentCurrency = amount;
		 }
		 
		 BillingExtractLine += AmtDocumentCurrency + "|";
		 BillingExtractLine += ItemText + "|";
		 BillingExtractLine += CompanyID + "|" + CostCenter + "|" + OrderNo + "|" + CommitmentItem + "|";
		 BillingExtractLine += WBS + "|" + FundsCenter + "|" + Fund + "|";
		 BillingExtractLine += DocNoForEarmarkedFunds + "|" + EarmarkedFundsDocumentNo + "|" + FunctionalArea + "|||||||";
		 p.println(BillingExtractLine);
		 
		 dblTotalAmount = 0;
		 al.clear();
		 alAmount.clear();
		 }
		 }
		 
		 al.add(strIncidentBlmAcctCode);
		 System.out.println("Contents of al: " + al); 
		 alAmount.add(TransAmount);
		 System.out.println("Contents of al: " + alAmount); 
		 System.out.println("ArrayList contains " + al.size() + " BLM account codes."); 
		 
		 
		 currentTransAmount		 		= Double.parseDouble(TransAmount);
		 
		 
		 int intLastIndex = al.size() - 1;
		 System.out.println("intLastIndex " + intLastIndex);
		 int intIndexCount = 0;
		 intIndexCount = cnt + 1;
		 System.out.println("intIndexCount details:	" + intIndexCount);

		 if(al.size() >= 1)
		 {
		 
		 System.out.println("intLastIndex " + intLastIndex);
		 System.out.println("al.size() is greater than 1: " + al.size());
		 
		 
		 
		 if(intLastIndex >= 0)
		 {
		 System.out.println("intLastIndex =< " + intLastIndex);
		 strLastAmount 					= alAmount.get(intLastIndex).toString();
		 lastBLMacctCode					= al.get(intLastIndex).toString();;
		 
		 
		 if(lastBLMacctCode.equals(strIncidentBlmAcctCode))
		 {
		 System.out.println("===============================================================");
		 System.out.println("IF *1* THEN (dblTotalAmount) "+dblTotalAmount);
		 System.out.println("currentTransAmount			: " + currentTransAmount);
		 System.out.println("dblTotalAmount			: " + dblTotalAmount);
		 
		 dblTotalAmount			=	dblTotalAmount	+ 	currentTransAmount;
		 
		 System.out.println("RUNNING TOTAL (dblTotalAmount)	: " + dblTotalAmount);
		 System.out.println("===============================================================");
		 
		 } 
		 } // end of if(intLastIndex >= 0)
		 } else {
		 System.out.println("al.size() is not greater than 1");
		 }// end if al.size() is greater than 1
		 
		 
		 if(BillingTransactionCount == intIndexCount)			        	 
		 {
		 System.out.println("LAST LINE OF XML");
		 
		 CostCenter						= lastBLMacctCode.substring(0,10);
		 WBS								= lastBLMacctCode.substring(11,23);
		 FunctionalArea					= lastBLMacctCode.substring(24,40);
		 
		 //dblTotalAmount - FORMAT THE ACCUMULATED TOTAL
		 amount = Double.toString(dblTotalAmount);
		 
		 if(amount.length() == 1 ){
		 AmtDocumentCurrency = "0000000000.0" + amount;
		 } else if (amount.length() == 2 ){
		 AmtDocumentCurrency = "0000000." + amount;
		 } else if (amount.length() == 3 ){
		 AmtDocumentCurrency = "00000000" + amount;
		 } else if (amount.length() == 4 ){
		 AmtDocumentCurrency = "000000000" + amount;
		 } else if (amount.length() == 5 ){
		 AmtDocumentCurrency = "00000000" + amount;
		 } else if (amount.length() == 6 ){
		 AmtDocumentCurrency = "0000000" + amount;
		 } else if (amount.length() == 7 ){
		 AmtDocumentCurrency = "000000" + amount;
		 } else if (amount.length() == 8 ){
		 AmtDocumentCurrency = "00000" + amount;
		 } else if (amount.length() == 9 ){
		 AmtDocumentCurrency = "0000" + amount;
		 } else if (amount.length() == 10 ){
		 AmtDocumentCurrency = "000" + amount;
		 } else if (amount.length() == 11 ){
		 AmtDocumentCurrency = "00" + amount;
		 } else if (amount.length() == 12 ){
		 AmtDocumentCurrency = "0" + amount;
		 } else {
		 AmtDocumentCurrency = amount;
		 }
		 
		 BillingExtractLine += AmtDocumentCurrency + "|";
		 BillingExtractLine += ItemText + "|";
		 BillingExtractLine += CompanyID + "|" + CostCenter + "|" + OrderNo + "|" + CommitmentItem + "|";
		 BillingExtractLine += WBS + "|" + FundsCenter + "|" + Fund + "|";
		 BillingExtractLine += DocNoForEarmarkedFunds + "|" + EarmarkedFundsDocumentNo + "|" + FunctionalArea + "|||||||";
		 p.println(BillingExtractLine);
		 }
		 InsertBillingTransactionExtract(env,BusinessArea,InterfaceType,DocumentDate,PostingDate,FiscalPeriod,ReferenceDocumentNumber,DocumentHeaderText,PostingKey,GLAcctCode,TransAmount,ItemText,CompanyID,CostCenter,OrderNo,CommitmentItem,WBS,FundsCenter,Fund,DocNoForEarmarkedFunds,EarmarkedFundsDocumentNo,FunctionalArea,TransNo,DocumentNo,FiscalYear,TransAmount,SequenceKey,extractFileName,CacheId,PMSTransAmount);
		 }//END OF FOR LOOP
		 }//END OF THE IF STATEMENT (BillingTransactionCount>0)
		 //end of file
		 p.println("<!-- BILLING TRANSACTION EXTRACT : EOF -->");
		 */
		//}
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
			//Jimmy added api name to alert
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransactionReExtractFTP' "
							+ "DetailDescription='Error in Opening File : "
							+ outfile
							+ ", inside API: NWCGProcessBillingTransactionReExtract'");
			throwAlert(env, stbuf);
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingTransactionReExtractFTP::Error in Opening File  "
								+ outfile);
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
			/*	NWCG_CREATE_BILLING_TRANSACTION_EXTRACT_SERVICE xmlDoc:
			 *  	<NWCGBillingTransExtract AmtDocumentCurrency="000000005.09"
			 AmtInDocCurrency="-5.09" BusinessArea="L000" CommitmentItem=""
			 CostCenter="LL11111111" DocHeaderText=""
			 DocNumForEarmarkedFunds="CORMK" DocumentDate="20091111"
			 EarmarkedFundsDocItem="" ExtractTransNo="200911101439203794943"
			 FiscalYear="2009" FunctionalArea="LF6900000.111111" Fund=""
			 FundsCenter="" GLAccountCode="6100.264B0" InterfaceType="NF"
			 ItemText="L322N091102" OrderNum="" PostingDate="20091111"
			 PostingKey="50" ReferenceDocNumber="" TradingPartnerCompanyId="1400" WBS="L12344567789"/>
			 */
			UpdateBillingTransaction(env, TransNo, FiscalYear, SequenceKey);
		} catch (Exception e) {//Jimmy - Added details to the throw alert
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransactionReExtractFTP' "
							+ "DetailDescription='Error in Inserting NWCG_BILLING_TRANSACTION_EXTRACT Record, during NWCGProcessBillingTransactionReExtract.  For ExtractTransaction No: "
							+ TransNo + " , CacheID: " + CacheId
							+ " , Document number: " + DocumentNo + " '"
							+ "OrderNo='" + OrderNo + "'");
			throwAlert(env, stbuf);
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingTransactionReExtractFTP::Error in Inserting NWCG_BILLING_TRANSACTION_EXTRACT Record for ");
		}
	}

	public void UpdateBillingTransaction(YFSEnvironment env, String TransNo,
			String FiscalYear, String SequenceKey) throws Exception {
		Document BillDoc = XMLUtil.createDocument("NWCGBillingTransaction");
		Element BillDocElem = BillDoc.getDocumentElement();
		NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
		String LastExtractDate = reportsUtil.dateToString(new java.util.Date(),
				NWCGConstants.YANTRA_DATE_FORMAT);
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
		} catch (Exception e) {//Jimmy - added detailed description 
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingExtract::updateBillingTransactionRecord Caught Exception "
								+ e);
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransactionReExtractFTP' "
							//+ " Extract Transaction No : " + TransNo + "'");
							+ " DetailDescription=' UpdateBillingTransaction method failed on Transaction No : "
							+ TransNo + "'");
			throwAlert(env, stbuf);
		}
		//}
	}
}