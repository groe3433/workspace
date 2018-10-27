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

package com.nwcg.icbs.yantra.commCore;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGDiscardOperationResultsAPI {

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGDiscardOperationResultsAPI.class);

	public Document discardOperationResults(YFSEnvironment env, Document xml) throws Exception {
		logger.verbose("@@@@@ Entering NWCGDiscardOperationResultsAPI::discardOperationResults ");
		logger.verbose("@@@@@ " + XMLUtil.getXMLString(xml));
		Element root = XMLUtil.getRootElement(xml);
		if (root != null) {
			String action = XMLUtil.getAttribute(root, "Action");
			String distID = XMLUtil.getAttribute(root, "DistributionID");
			logger.verbose("@@@@@ action is " + action + " and distID is " + distID);
			if (action != null && action.equalsIgnoreCase("Discard")) {
				NWCGMessageStore msgStore = NWCGMessageStore.getMessageStore();
				String latest_message_key = NWCGGetOperationResultsAPI.apiCallToGetLatestMessageKeyForDistID(env, distID);
				msgStore.updateMessage(distID,
						NWCGAAConstants.MESSAGE_DIR_TYPE_OB,
						XMLUtil.getXMLString(xml),
						NWCGAAConstants.MESSAGE_TYPE_LATEST,
						NWCGAAConstants.MESSAGE_STATUS_DISCARD,
						NWCGAAConstants.EXTERNAL_SYSTEM_NAME,
						latest_message_key, true);
				logger.verbose("@@@@@ Exiting NWCGDiscardOperationResultsAPI::discardOperationResults (DISCARDED)");
				return formReturnResult("DISCARDED", "0: MESSAGE DISCARDED", "Message status changed to discard.");
			}
		}
		logger.verbose("@@@@@ Exiting NWCGDiscardOperationResultsAPI::discardOperationResults (FAILED)");
		return formReturnResult("FAILED", "-1: MESSAGE STATUS CHANGE FAILED", "Message status change failed.");
	}

	private Document formReturnResult(String icbsStatus, String respStatusStr, String respObjStr) {
		logger.verbose("@@@@@ Entering NWCGDiscardOperationResultsAPI::formReturnResult");
		try {
			Document getOperationResults = XMLUtil.createDocument("DiscardActionResult");
			getOperationResults.getDocumentElement().setAttribute("ICBSStatus", icbsStatus);
			getOperationResults.getDocumentElement().setAttribute("ResponseStatus", respStatusStr);
			getOperationResults.getDocumentElement().setAttribute("ResponseMsg", respObjStr);
			logger.verbose("@@@@@ Exiting NWCGDiscardOperationResultsAPI::formReturnResult (Success)");
			return getOperationResults;
		} catch (Exception e) {
			logger.error("!!!!! Exiting NWCGDiscardOperationResultsAPI::formReturnResult (Failure)");
			return null;
		}
	}
}