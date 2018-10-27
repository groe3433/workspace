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

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.reports.loftware.NWCGReportsUtil;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGSetReviewFlag implements YIFCustomApi {
	
	private static Logger logger = Logger
			.getLogger(NWCGProcessBillingTransConfirmShipment.class.getName());

	int blankcnt, lengthcnt, totextcnt = 0;

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

	public Document setReviewFlag(YFSEnvironment env, Document doc)
			throws Exception {
		return updateRecord(env, doc);
	}

	public Document updateRecord(YFSEnvironment env, Document inXML)
			throws Exception {
		if (logger.isVerboseEnabled()) {
			logger.verbose("Entering updateRecord, Input document is:"
					+ XMLUtil.getXMLString(inXML));
		}
		/*
		 * <NWCGBillingTransaction IgnoreOrdering="Y"
		 * SequenceKey="2010012511084914338002"/>
		 */
		Element elemRoot = null;
		elemRoot = inXML.getDocumentElement();
		if (elemRoot != null) {
			String SequenceKey = elemRoot.getAttribute("SequenceKey");
			String isReviewedFlag = elemRoot.getAttribute("IsReviewed");
			updateBillingTransaction(env, SequenceKey, isReviewedFlag);
		}
		return inXML;
	}

	public void updateBillingTransaction(YFSEnvironment env,
			String SequenceKey, String isReviewedFlag) throws Exception {
		Document billDoc = XMLUtil.createDocument("NWCGBillingTransaction");
		Element billDocElem = billDoc.getDocumentElement();
		NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
		billDocElem.setAttribute(NWCGConstants.BILL_TRANS_IS_REVIEWED,
				isReviewedFlag);
		billDocElem.setAttribute("SequenceKey", SequenceKey);
		try {
			CommonUtilities.invokeService(env,
					NWCGConstants.NWCG_UPDATE_BILLING_TRANSACTION_SERVICE,
					billDoc);
		} catch (Exception e) {
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGSetReviewFlag::UpdateBillingTransaction Caught Exception "
								+ e);
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGSetReviewFlag' "
							+ " SequenceKey : " + SequenceKey + "'");
			throwAlert(env, stbuf);
		}
	}
}