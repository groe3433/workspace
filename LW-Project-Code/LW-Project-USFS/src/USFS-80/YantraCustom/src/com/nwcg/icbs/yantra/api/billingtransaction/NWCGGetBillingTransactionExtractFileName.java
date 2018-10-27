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

import java.util.HashMap;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGGetBillingTransactionExtractFileName implements YIFCustomApi {
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

	public Document getUniqueFileName(YFSEnvironment env, Document inXML)
			throws Exception {
		if (logger.isVerboseEnabled()) {
			logger
					.verbose("Entering NWCGProcessBillingTransactionReExtractFTP, Input document is:"
							+ XMLUtil.getXMLString(inXML));
		}
		Element elemRoot = null;
		Document ExtractFileNameOutDoc = XMLUtil.newDocument();
		Document consExtractFileNameOutDoc = XMLUtil.getDocument();
		Element elemNWCGBillingTransExtractList = consExtractFileNameOutDoc
				.createElement("NWCGBillingTransExtractList");
		consExtractFileNameOutDoc.appendChild(elemNWCGBillingTransExtractList);
		elemRoot = inXML.getDocumentElement();
		if (elemRoot != null) {
			ExtractFileNameOutDoc = CommonUtilities
					.invokeService(
							env,
							NWCGConstants.NWCG_GET_BILLING_TRANSACTION_EXTRACT_LIST_SERVICE,
							inXML);
			//group by transaction no ExtractFileNameOutDoc
			NodeList NWCGBillingTransExtractNL = ExtractFileNameOutDoc
					.getElementsByTagName("NWCGBillingTransExtract");
			int NWCGBillingTransExtractNLLen = NWCGBillingTransExtractNL
					.getLength();
			HashMap transNoHM = new HashMap();
			Element elemNWCGBillingTransExtract;
			String strFileName = "";
			Node temp;

			for (int i = 0; i < NWCGBillingTransExtractNLLen; i++) {
				elemNWCGBillingTransExtract = (Element) NWCGBillingTransExtractNL
						.item(i);
				strFileName = elemNWCGBillingTransExtract
						.getAttribute("ExtractFileName");
				if (transNoHM.containsKey(strFileName)) {
				} else {
					transNoHM.put(strFileName, elemNWCGBillingTransExtract);
					temp = elemNWCGBillingTransExtract.cloneNode(true);
					//Node tempNode = outXML.importNode(XMLUtil.getDocument(tempStr)
					//.getDocumentElement(), true);
					elemNWCGBillingTransExtractList
							.appendChild(consExtractFileNameOutDoc.importNode(
									temp, true));
				}
			}
		}
		return consExtractFileNameOutDoc;
	}

	public Document getFileName(YFSEnvironment env, Document inXML)
			throws Exception {
		if (logger.isVerboseEnabled()) {
			logger
					.verbose("Entering NWCGProcessBillingTransactionReExtractFTP, Input document is:"
							+ XMLUtil.getXMLString(inXML));
		}

		/*
		 * Incoming file:
		 * <NWCGBillingTransExtract ExtractFileName="" ExtractFileNameQryType="" IgnoreOrdering="Y" MaximumRecords="30"/>
		 */

		Element elemRoot = null;

		Document ExtractFileNameOutDoc = XMLUtil.newDocument();
		Document consExtractFileNameOutDoc = XMLUtil.getDocument();

		elemRoot = inXML.getDocumentElement();
		if (elemRoot != null) {
			ExtractFileNameOutDoc = CommonUtilities
					.invokeService(
							env,
							NWCGConstants.NWCG_GET_BILLING_TRANSACTION_EXTRACT_LIST_SERVICE,
							inXML);
			//group by transaction no ExtractFileNameOutDoc
			Element elemNWCGBillingTransExtractList = consExtractFileNameOutDoc
					.createElement("NWCGBillingTransExtractList");
			consExtractFileNameOutDoc
					.appendChild(elemNWCGBillingTransExtractList);
			NodeList NWCGBillingTransExtractNL = ExtractFileNameOutDoc
					.getElementsByTagName("NWCGBillingTransExtract");
			int NWCGBillingTransExtractNLLen = NWCGBillingTransExtractNL
					.getLength();
			HashMap transNoHM = new HashMap();
			Element elemNWCGBillingTransExtract;
			String strTransactionNo = "";
			Node temp;
			String strTotalAmountTransNo = "00.00";
			for (int i = 0; i < NWCGBillingTransExtractNLLen; i++) {
				elemNWCGBillingTransExtract = (Element) NWCGBillingTransExtractNL
						.item(i);
				strTransactionNo = elemNWCGBillingTransExtract
						.getAttribute("TransactionNo");
				if (transNoHM.containsKey(strTransactionNo)) {
				} else {
					transNoHM
							.put(strTransactionNo, elemNWCGBillingTransExtract);
					strTotalAmountTransNo = getTotalTransAmountTransNo(env,
							strTransactionNo);
					//convert total amount to ###.##
					java.text.DecimalFormat df = new java.text.DecimalFormat(
							"###.##");
					double dblTotalAmountTransNo = Double
							.parseDouble(strTotalAmountTransNo);
					dblTotalAmountTransNo = df.parse(
							df.format(dblTotalAmountTransNo)).doubleValue();
					strTotalAmountTransNo = Double
							.toString(dblTotalAmountTransNo);
					elemNWCGBillingTransExtract.setAttribute(
							"AmtInDocCurrency", strTotalAmountTransNo);
					temp = elemNWCGBillingTransExtract.cloneNode(true);
					//Node tempNode = outXML.importNode(XMLUtil.getDocument(tempStr)
					//.getDocumentElement(), true);
					elemNWCGBillingTransExtractList
							.appendChild(consExtractFileNameOutDoc.importNode(
									temp, true));
				}
			}
		}
		return consExtractFileNameOutDoc;
	}

	public String getTotalTransAmountTransNo(YFSEnvironment env,
			String strTransactionNo) throws Exception {
		String strTotalAmountTransNo = "00.00";
		double dblTotalAmountTransNo = 00.00;
		double dblAmount = 00.00;
		String strAmount = "00.00";
		Document getBillingTransNoOutDoc = getBillingTransNoList(env,
				strTransactionNo);
		NodeList nlNWCGBillingTransExtract = getBillingTransNoOutDoc
				.getElementsByTagName("NWCGBillingTransExtract");
		Element elemNWCGBillingTransExtract;
		int nlNWCGBillingTransExtractLen = nlNWCGBillingTransExtract
				.getLength();
		for (int i = 0; i < nlNWCGBillingTransExtractLen; i++) {
			elemNWCGBillingTransExtract = (Element) nlNWCGBillingTransExtract
					.item(i);
			strAmount = elemNWCGBillingTransExtract
					.getAttribute("AmtInDocCurrency");
			dblAmount = Double.parseDouble(strAmount);
			dblTotalAmountTransNo = dblTotalAmountTransNo + dblAmount;
		}
		strTotalAmountTransNo = Double.toString(dblTotalAmountTransNo);
		return strTotalAmountTransNo;
	}

	public static Document getBillingTransNoList(YFSEnvironment env,
			String strTransactionNo) throws Exception {
		Document getBillingTransNoListInDoc = XMLUtil.getDocument();
		Element elemNWCGBillingTransExtract = getBillingTransNoListInDoc
				.createElement("NWCGBillingTransExtract");
		getBillingTransNoListInDoc.appendChild(elemNWCGBillingTransExtract);
		elemNWCGBillingTransExtract.setAttribute("TransactionNo",
				strTransactionNo);
		Document ExtractFileNameOutDoc = CommonUtilities
				.invokeService(
						env,
						NWCGConstants.NWCG_GET_BILLING_TRANSACTION_EXTRACT_LIST_SERVICE,
						getBillingTransNoListInDoc);
		return ExtractFileNameOutDoc;
	}
}