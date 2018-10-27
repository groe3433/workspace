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

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGConsTransactionNo implements YIFCustomApi {

	private static Logger logger = Logger.getLogger();

	private Properties props;

	public Document consTransactionNo(YFSEnvironment env, Document inXML)
			throws Exception {
		logger.verbose("@@@@@ Entering NWCGConsTransactionNo::consTransactionNo @@@@@");
		logger.verbose("@@@@@ inXML : " + XMLUtil.getXMLString(inXML));

		HashMap transRecordsHM = new HashMap();
		Document outXML = XMLUtil.getDocument();

		NodeList nWCGBillingTransactionNL = inXML.getElementsByTagName("NWCGBillingTransaction");
		
		int nWCGBillingTransactionNLLen = nWCGBillingTransactionNL.getLength();
		logger.verbose("@@@@@ nWCGBillingTransactionNLLen : " + nWCGBillingTransactionNLLen);

		Element nWCGBillingTransactionElem;
		String strTransactionNo;
		String strBLMCode;
		String strHashKey;
		String strTransType;
		String strSequenceKey;
		Element temp;
		NodeList tempNL;
		Boolean InvalidTransType = true;
		Document docBillTransListInXML = XMLUtil.getDocument();
		Element elemNWCGBillingTransaction;

		elemNWCGBillingTransaction = docBillTransListInXML.createElement("NWCGBillingTransaction");
		docBillTransListInXML.appendChild(elemNWCGBillingTransaction);

		Document docBillTransListOutXML = XMLUtil.getDocument();
		int numTransCount = 0;

		String arrSize = props.getProperty("MaximumRecords");
		logger.verbose("@@@@@ arrSize : " + arrSize);
		arrSize = ResourceUtil.get(arrSize);
		logger.verbose("@@@@@ arrSize : " + arrSize);

		String[] arrTransactionNo = new String[Integer.parseInt(arrSize)];

		if (nWCGBillingTransactionNLLen == 0) {
			logger.verbose("@@@@@ inXML : " + XMLUtil.getXMLString(inXML));
			return inXML;
		} else {
			// for each nWCGBillingTransactionNL record get the xpath nodelist of the same transaction no add the unit cost
			for (int i = 0; i < nWCGBillingTransactionNLLen; i++) {
				InvalidTransType = true;
				nWCGBillingTransactionElem = (Element) nWCGBillingTransactionNL.item(i);
				strTransactionNo = nWCGBillingTransactionElem.getAttribute("TransactionNo");
				strTransType = nWCGBillingTransactionElem.getAttribute("TransType");
				strBLMCode = nWCGBillingTransactionElem.getAttribute("IncidentBlmAcctCode");
				strHashKey = strTransactionNo + strTransType + strBLMCode;
				if (strTransType.equalsIgnoreCase("CONFIRM INCIDENT TO") || strTransType.equalsIgnoreCase("ISSUE CONFIRM SHIPMENT")
						|| strTransType.equalsIgnoreCase("CONFIRM INCIDENT FROM") || strTransType.equalsIgnoreCase("RETURNS")
						|| strTransType.equalsIgnoreCase("SHIP CACHE TO") || strTransType.equalsIgnoreCase("WO-REFURB")) {
					InvalidTransType = false;
				}
				if (transRecordsHM.containsKey(strHashKey) || InvalidTransType) {
					// do nothing...
				} else {
					// get all the nodes with same transaction no add the values of TransAmount and set the total into temp and put into hasp map with item_0 to item_n as item values
					elemNWCGBillingTransaction.setAttribute("TransactionNo", strTransactionNo);
					elemNWCGBillingTransaction.setAttribute("IncidentBlmAcctCode", strBLMCode);
					elemNWCGBillingTransaction.setAttribute("TransType", strTransType);
					
					// gets all the sequence keys for the transaction
					docBillTransListOutXML = CommonUtilities.invokeService(env, "NWCGCustomGetBillingTransactionListService", docBillTransListInXML);
					
					arrTransactionNo[numTransCount] = strHashKey;
					numTransCount++;
					temp = nWCGBillingTransactionElem;
					// call the service NWCGCustomGetBillingTransactionListService and use outxml to obtain all cache items for given transaction no
					tempNL = docBillTransListOutXML.getElementsByTagName("NWCGBillingTransaction");
					int tempNLLen = tempNL.getLength();
					double douTransAmountTotal = 0.00;
					String strReviewFlag = "Y";
					String strExtractFlag = "Y";
					for (int k = 0; k < tempNLLen; k++) {
						Element nwcgBTTempElem = (Element) tempNL.item(k);
						String strTransAmountTemp = nwcgBTTempElem.getAttribute("TransAmount");
						if (nwcgBTTempElem.getAttribute("IsReviewed").equalsIgnoreCase("N")) {
							strReviewFlag = "N";
						}
						if (nwcgBTTempElem.getAttribute("IsExtracted").equalsIgnoreCase("N")) {
							strExtractFlag = "N";
						}
						douTransAmountTotal = douTransAmountTotal + Double.parseDouble(strTransAmountTemp);
						// set the sequence key SequenceKey
						strSequenceKey = nwcgBTTempElem.getAttribute("SequenceKey");
						temp.setAttribute("SequenceKey_" + k, strSequenceKey);
					}
					douTransAmountTotal = (((douTransAmountTotal * 100.0))) / 100.0;
					temp.setAttribute("douTransAmountTotal", Double.toString(douTransAmountTotal));
					temp.setAttribute("strReviewFlag", strReviewFlag);
					temp.setAttribute("strExtractFlag", strExtractFlag);
					temp.setAttribute("tempNLLen", Integer.toString(tempNLLen));
					transRecordsHM.put(strHashKey, XMLUtil.getElementXMLString(temp));
				}
			}
		}
		int transRecordsHMLen = transRecordsHM.size();
		Element nWCGBillingTransactionListElem = outXML.createElement("NWCGBillingTransactionList");
		outXML.appendChild(nWCGBillingTransactionListElem);
		for (numTransCount = 0; numTransCount < transRecordsHMLen; numTransCount++) {
			strHashKey = arrTransactionNo[numTransCount];
			String tempStr = transRecordsHM.get(strHashKey).toString();
			Node tempNode = outXML.importNode(XMLUtil.getDocument(tempStr).getDocumentElement(), true);
			nWCGBillingTransactionListElem.appendChild(tempNode);
		}
		logger.verbose("@@@@@ outXML : " + XMLUtil.getXMLString(outXML));
		logger.verbose("@@@@@ Exiting NWCGConsTransactionNo::consTransactionNo @@@@@");
		return outXML;
	}

	public void setProperties(Properties arg0) throws Exception {
		props = arg0;
	}
}