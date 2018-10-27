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

package com.nwcg.icbs.yantra.agents;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.billingtransaction.NWCGProcessBillingTransConfirmShipment;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.ib.read.handler.NWCGGetOperationResultsHandler;
import com.nwcg.icbs.yantra.reports.loftware.NWCGReportsUtil;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGAgentLogger;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;

/**
 * -----------------------------------------------------------------------------
 * QC : 1051 Scenario : Billing Transaction Extract timed out when the number of
 * records fetched is more than 20000 and also because this is done real time
 * Description : To resolve the time out issue, an agent is being created to get
 * the input provided by the user and provide the billing transaction extract in
 * the decided frequency.
 * -----------------------------------------------------------------------------
 */
public class NWCGBillingTransactionExtractServiceAgent extends YCPBaseAgent {

	private static YFCLogCategory logger = NWCGAgentLogger
			.instance(NWCGBillingTransactionExtractServiceAgent.class);

	/**
	 * public default constructor
	 */
	public NWCGBillingTransactionExtractServiceAgent() {
		super();
	}

	public void throwAlert(YFSEnvironment env, StringBuffer Message)
			throws Exception {

		Message.append(" ExceptionType='BILLING_TRANS_EXTRACT'"
				+ " InboxType='" + NWCGConstants.NWCG_BILL_TRANS_INBOXTYPE
				+ "' QueueId='" + NWCGConstants.NWCG_BILL_TRANS_QUEUEID
				+ "' />");
		try {
			Document inTemplate = XMLUtil.getDocument(Message.toString());
			CommonUtilities.invokeAPI(env, "createException", inTemplate);
		} catch (Exception ex) {
			logger.error("!!!!! Caught General Exception :: " + ex);
			throw new NWCGException("NWCG_BILLING_TRANSACTION_ERROR_WHILE_LOGGING_ALERT");
		}
	}

	/*
	 * This method getJobs fetches the records from YFS_INBOX table Only those
	 * records are fetched with INBOX_TYPE="EXTRACT" and STATUS="OPEN" All the
	 * records are passed to the executeJobs method
	 * 
	 * @param env, inputDoc
	 * 
	 * @throws Exception
	 * 
	 * @returns List
	 */
	public List getJobs(YFSEnvironment env, Document inputDoc) throws Exception {

		String sInboxType = "EXTRACT";
		String sStatus = "OPEN";

		/*
		 * API: getExceptionList Params: InboxType="EXTRACT" Status="OPEN"
		 */
		Document dInputXml = XMLUtil.getDocument("<Inbox InboxType=\""
				+ sInboxType + "\" Status=\"" + sStatus + "\" />");
		Document dGetExtractRecords = CommonUtilities.invokeAPI(env,
				"NWCGBillingTransExtract_getExceptionList",
				NWCGConstants.API_GET_EXCEPTION_LIST, dInputXml);

		List<Document> extractList = new ArrayList<Document>();
		NodeList nlExtract = dGetExtractRecords.getElementsByTagName("Inbox");

		if (nlExtract != null) {
			for (int i = 0; i < nlExtract.getLength(); i++) {
				Element elemExtractTag = (Element) nlExtract.item(i);
				String sExtractKey = (String) (elemExtractTag
						.getAttribute("InboxKey"));
				String sXML = (String) (elemExtractTag
						.getAttribute("InboxAddnlData"));

				Document dBillExtractDoc = XMLUtil.newDocument();
				Element elBillExtract = dBillExtractDoc.createElement("Inbox");
				elBillExtract.setAttribute("InboxKey", sExtractKey);
				elBillExtract.setAttribute("InboxAddnlData", sXML);
				dBillExtractDoc.appendChild(elBillExtract);

				extractList.add(dBillExtractDoc);
			}
		}
		return (List) extractList;
	}

	/*
	 * This method executeJob calls the
	 * NWCGProcessBillingTransactionExtractFTPService to process the Extract
	 * file for all the records fetched by getJobs method It also calls
	 * changeException api to update the Status of the record to "WIP"
	 * 
	 * @param env, inXML
	 * 
	 * @throws Exception
	 */
	public void executeJob(YFSEnvironment env, Document inXML) throws Exception {

		if (inXML != null) {
			NodeList nlExtractList = inXML
					.getElementsByTagName(NWCGConstants.INBOX);

			if (nlExtractList != null) {
				for (int count = 0; count < nlExtractList.getLength(); count++) {
					Element elemExtractTag = (Element) nlExtractList
							.item(count);
					String sInboxKey = (String) (elemExtractTag
							.getAttribute(NWCGConstants.INBOX_KEY));
					String sInboxAddnlData = (String) (elemExtractTag
							.getAttribute(NWCGConstants.INBOX_ADDNL_DATA));

					Document dExtractXML = XMLUtil.getDocument(sInboxAddnlData);

					/*
					 * Service: NWCGProcessBillingTransactionExtractFTPService
					 * Params: Document passed from getJobs
					 */
					CommonUtilities
							.invokeService(
									env,
									NWCGConstants.NWCG_PROCESS_BILLING_TRANSACTION_EXTRACT_FTP_SERVICE,
									dExtractXML);

					/*
					 * API: changeException Params: InboxKey, Status="OPEN"
					 */
					String sStatus = "WIP";
					Document dInputXml = XMLUtil
							.getDocument("<Inbox InboxKey=\"" + sInboxKey
									+ "\" Status=\"" + sStatus + "\" />");
					CommonUtilities.invokeAPI(env,
							NWCGConstants.API_CHANGE_EXCEPTION, dInputXml);
				}
			}
		}
	}
}
