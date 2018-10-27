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

import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.api.adjlocninv.NWCGAdjLocnInvReference;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This is a Consumer class, basically based on the attribute TrackableInventoryAction
 * it Request the factory to for an appropriate instance and then invokes insertOrUpdateTrackableRecord
 * method for processing the message
 */
public class NWCGBillingTransMessageConsumer implements YIFCustomApi {

	private Properties _properties;

	private static Logger log = Logger.getLogger();

	public void setProperties(Properties arg0) throws Exception {
		this._properties = arg0;
	}

	public void throwAlert(YFSEnvironment env, StringBuffer Message) throws Exception {
		Message.append(" ExceptionType='BILLING_TRANS_REVIEW'" + " InboxType='" + NWCGConstants.NWCG_BILL_TRANS_INBOXTYPE + "' QueueId='" + NWCGConstants.NWCG_BILL_TRANS_QUEUEID + "' />");
		try {
			Document inTemplate = XMLUtil.getDocument(Message.toString());
			CommonUtilities.invokeAPI(env, "createException", inTemplate);
		} catch (Exception ex1) {
			throw new NWCGException("NWCG_BILLING_TRANSACTION_ERROR_WHILE_LOGGING_ALERT");
		}
	}
	
	public Document consumeMessage(YFSEnvironment env, Document inDoc) throws Exception {
		Element documentRootElement = inDoc.getDocumentElement();
		NWCGBillingTransRecordMutator implNWCGBillingTransRecordMutator = null;
		if (documentRootElement != null) {
			String strAction = StringUtil.nonNull(documentRootElement.getAttribute("BillingTransactionAction"));
			if (strAction.equals("")) {
				return inDoc;
			}
			implNWCGBillingTransRecordMutator = NWCGBillingTransFactory.getMessageProcesser(strAction);
		}
		
		if(implNWCGBillingTransRecordMutator == null) {
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGBillingTransMessageConsumer' " + "DetailDescription='Implementation returned as null. " + "'");
			throwAlert(env, stbuf);
		} else {
			return implNWCGBillingTransRecordMutator.insertBillingTransRecord(env, inDoc);
		}
		return inDoc;
	}
}