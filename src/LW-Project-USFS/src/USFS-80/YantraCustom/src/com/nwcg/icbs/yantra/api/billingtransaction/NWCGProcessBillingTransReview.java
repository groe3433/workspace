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

/**
 * 
 * @author Oxford
 * 
 */
public class NWCGProcessBillingTransReview implements YIFCustomApi,
		NWCGBillingTransRecordMutator {

	private static Logger logger = Logger
			.getLogger(NWCGProcessBillingTransChangeOrder.class.getName());

	int reviewcnt = 0;

	public void setProperties(Properties arg0) throws Exception {
	}

	public void throwAlert(YFSEnvironment env, StringBuffer Message)
			throws Exception {
		logger
				.info("@@@@@ Entering NWCGProcessBillingTransReview::throwAlert @@@@@");
		Message.append(" ExceptionType='BILLING_TRANS_REVIEW'" + " InboxType='"
				+ NWCGConstants.NWCG_BILL_TRANS_INBOXTYPE + "' QueueId='"
				+ NWCGConstants.NWCG_BILL_TRANS_QUEUEID + "' />");
		if (logger.isVerboseEnabled()) {
			logger.verbose("Throw Alert Method called with message:-"
					+ Message.toString());
		}
		try {
			Document inTemplate = XMLUtil.getDocument(Message.toString());
			CommonUtilities.invokeAPI(env, "createException", inTemplate);
		} catch (Exception ex1) {
			throw new NWCGException(
					"NWCG_BILLING_TRANSACTION_ERROR_WHILE_LOGGING_ALERT");
		}
		logger
				.info("@@@@@ Exiting NWCGProcessBillingTransReview::throwAlert @@@@@");
	}

	public Document InsertRecord(YFSEnvironment env, Document inXML)
			throws Exception {
		if (logger.isVerboseEnabled()) {
			logger
					.verbose("@@@@@ Entering NWCGProcessBillingTransReview::InsertRecord @@@@@");
		}
		logger
				.info("@@@@@ Entering NWCGProcessBillingTransReview::InsertRecord @@@@@");
		logger.info("@@@@@ inXML : " + XMLUtil.getXMLString(inXML));

		// Jimmy - Temp vars to be included in alerts
		String tempCacheId = "";

		try {
			Element elemRoot = inXML.getDocumentElement();
			if (elemRoot != null) {
				// get the date and time
				String CacheId = elemRoot.getAttribute("CacheID");
				String FromDate = elemRoot.getAttribute("FromTransDate");
				String FromDateTime = elemRoot.getAttribute("FromTransTime");
				String FromHour = "00";
				String FromMinute = "00";
				String FromSecond = "00";
				String ToDate = elemRoot.getAttribute("ToTransDate");
				String ToDateTime = elemRoot.getAttribute("ToTransTime");
				String ToMonth = "00";
				String ToDay = "00";
				String ToYear = "00";
				String ToHour = "00";
				String ToMinute = "00";
				String ToSecond = "00";

				// jimmy - populating temp vars for alerts
				tempCacheId = CacheId;

				// parse to date and to time
				if (ToDate.length() > 0) {
					String[] ToDtFields = ToDate.split("/");
					ToMonth = ToDtFields[0];
					ToDay = ToDtFields[1];
					ToYear = ToDtFields[2];
				}
				if (ToDateTime.length() > 0) {
					String[] ToTimeFields = ToDateTime.split(":");
					ToHour = ToTimeFields[0];
					ToMinute = ToTimeFields[1];
					ToSecond = ToTimeFields[2];
				}
				// create a new inDoc and outDoc
				Document BillingInDoc = XMLUtil.newDocument();
				Document BillingOutDoc = XMLUtil.newDocument();
				// populate the new inDoc
				Element el_NWCGBillingTrans = BillingInDoc
						.createElement("NWCGBillingTransaction");
				BillingInDoc.appendChild(el_NWCGBillingTrans);
				el_NWCGBillingTrans.setAttribute("CacheId", CacheId);
				// parse from date and from time
				if (FromDate.length() > 0) {
					String[] FromDtFields = FromDate.split("/");
					String FromMonth = FromDtFields[0];
					String FromDay = FromDtFields[1];
					String FromYear = FromDtFields[2];
					if (FromDateTime.length() > 0) {
						String[] FromTimeFields = FromDateTime.split(":");
						FromHour = FromTimeFields[0];
						FromMinute = FromTimeFields[1];
						FromSecond = FromTimeFields[2];
					}
					// populated the query type, from date/time, and to
					// date/time
					el_NWCGBillingTrans.setAttribute("TransDateQryType",
							"BETWEEN");
					el_NWCGBillingTrans.setAttribute("FromTransDate", FromYear
							+ FromMonth + FromDay + "T" + FromHour + FromMinute
							+ FromSecond);
					el_NWCGBillingTrans.setAttribute("ToTransDate", ToYear
							+ ToMonth + ToDay + "T" + ToHour + ToMinute
							+ ToSecond);
				} else {
					// populate the query type, and to date/time in case no from
					// date/time
					el_NWCGBillingTrans.setAttribute("TransDateQryType", "LE");
					el_NWCGBillingTrans.setAttribute("TransDate", ToYear
							+ ToMonth + ToDay + "T" + ToHour + ToMinute
							+ ToSecond);
				}
				// populate last inDoc details
				el_NWCGBillingTrans.setAttribute("IsExtracted", "N");
				el_NWCGBillingTrans.setAttribute("IsReviewed", "N");
				// invoke service to get outDoc as a result
				logger.info("@@@@@ BillingInDoc : "
						+ XMLUtil.getXMLString(BillingInDoc));

				// BEGIN CR 816
				// No longer invokes NWCG_GET_BILLING_TRANSACTION_LIST_SERVICE
				// service
				BillingOutDoc = CommonUtilities
						.invokeService(
								env,
								NWCGConstants.NWCG_GET_BILLING_TRANSACTION_DATE_REVIEW_LIST_SERVICE,
								BillingInDoc);
				// END CR 816

				// get list of nodes from outDoc
				NodeList BList = BillingOutDoc.getDocumentElement()
						.getElementsByTagName("NWCGBillingTransaction");
				// walk through list of outDoc nodes
				logger.info("@@@@@ BillingInDoc list count : "
						+ BList.getLength());
				for (int numListIterator = 0; numListIterator < BList
						.getLength(); numListIterator++) {
					logger.info("numListIterator: " + numListIterator);
					Element curBillingList = (Element) BList
							.item(numListIterator);
					String SequenceKey = curBillingList
							.getAttribute("SequenceKey");
					Document docBillingTransactionIP = XMLUtil
							.createDocument("NWCGBillingTransaction");
					Element BillingTransElem = docBillingTransactionIP
							.getDocumentElement();
					BillingTransElem.setAttribute("SequenceKey", SequenceKey);
					BillingTransElem.setAttribute("IsReviewed", "Y");
					try {
						CommonUtilities
								.invokeService(
										env,
										NWCGConstants.NWCG_UPDATE_BILLING_TRANSACTION_SERVICE,
										docBillingTransactionIP);
						reviewcnt++;
					} catch (Exception e) {
						e.printStackTrace();
						StringBuffer stbuf = new StringBuffer(
								"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransReview' "
										+ "DetailDescription='Update Billing Transaction Record Failed on Review for Sequence Key : "
										+ SequenceKey
										+ ", Cache ID: "
										+ CacheId
										+ ", during NWCGProcessBillingTransReview process'");
						throwAlert(env, stbuf);
					}
				}
				logger.info("@@@@@ BillingInDoc list count : "
						+ BList.getLength());
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransReview' "
							+ "DetailDescription='Update Billing Transaction Record Failed on Review, at cache id: "
							+ tempCacheId
							+ ", during NWCGProcessBillingTransReview process'");
			throwAlert(env, stbuf);
		}
		logger
				.info("@@@@@ Exiting NWCGProcessBillingTransReview::InsertRecord @@@@@");
		return inXML;
	}

	public Document insertBillingTransRecord(YFSEnvironment env, Document doc)
			throws Exception {
		logger
				.info("@@@@@ In NWCGProcessBillingTransReview::insertBillingTransRecord @@@@@");
		return InsertRecord(env, doc);
	}
}