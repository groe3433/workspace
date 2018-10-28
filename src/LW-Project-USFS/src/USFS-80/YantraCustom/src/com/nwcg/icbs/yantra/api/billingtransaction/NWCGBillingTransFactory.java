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

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.Logger;

/**
 * This is a factory, will return the message consumer based 
 * on the action passed
 */
public class NWCGBillingTransFactory {
	
	private static Logger log = Logger.getLogger(NWCGBillingTransRecordMutator.class.getName());
	
	public static NWCGBillingTransRecordMutator getMessageProcesser(String strAction) {
		boolean isReturnObjSet = false; 
		NWCGBillingTransRecordMutator returnObj = null;
		if (strAction.equals(NWCGConstants.NWCG_BILLINGTRANS_PROCESS_TYPE_CONFIRM_SHIPMENT)) {
			returnObj = new NWCGProcessBillingTransConfirmShipment();
			isReturnObjSet = true;
		}
		if (strAction.equals(NWCGConstants.NWCG_BILLINGTRANS_PROCESS_TYPE_CHANGE_ORDER)) {
			returnObj = new NWCGProcessBillingTransChangeOrder();
			isReturnObjSet = true;
		}
		if (strAction.equals(NWCGConstants.NWCG_BILLINGTRANS_PROCESS_TYPE_CHANGE_PURCHASEORDER)) {
			returnObj = new NWCGProcessBillingTransChangePurchaseOrder();
			isReturnObjSet = true;
		}
		if (strAction.equals(NWCGConstants.NWCG_BILLINGTRANS_PROCESS_TYPE_RECEIVE_PURCHASEORDER)) {
			returnObj = new NWCGProcessBillingTransReceivePurchaseOrder();
			isReturnObjSet = true;
		}
		if (strAction.equals(NWCGConstants.NWCG_BILLINGTRANS_PROCESS_TYPE_CHANGE_TRANSFERORDER)) {
			returnObj = new NWCGProcessBillingTransChangeTransferOrder();
			isReturnObjSet = true;
		}
		if (strAction.equals(NWCGConstants.NWCG_BILLINGTRANS_PROCESS_TYPE_RECEIVE_TRANSFERORDER)) {
			returnObj = new NWCGProcessBillingTransReceiveTransferOrder();
			isReturnObjSet = true;
		}
		if (strAction.equals(NWCGConstants.NWCG_BILLINGTRANS_PROCESS_TYPE_RETURNS)) {
			returnObj = new NWCGProcessBillingTransReceiveReturns();
			isReturnObjSet = true;
		}
		if (strAction.equals(NWCGConstants.NWCG_BILLINGTRANS_PROCESS_TYPE_ADJ_LOCATION_INVENTORY)) {
			returnObj = new NWCGProcessBillingTransAdjLocationInventory();
			isReturnObjSet = true;
		}
		if (strAction.equals(NWCGConstants.NWCG_BILLINGTRANS_PROCESS_TYPE_CONFIRM_INCIDENT_TO)) {
			returnObj = new NWCGProcessBillingTransConfirmIncidentTO();
			isReturnObjSet = true;
		}
		if (strAction.equals(NWCGConstants.NWCG_BILLINGTRANS_PROCESS_TYPE_CHANGE_INCIDENT_TO)) {
			returnObj = new NWCGProcessBillingTransChangeIncidentTO();
			isReturnObjSet = true;
		}
		if (strAction.equals(NWCGConstants.NWCG_BILLINGTRANS_PROCESS_TYPE_REVIEW)) {
			returnObj = new NWCGProcessBillingTransReview();
			isReturnObjSet = true;
		}
		if (strAction.equals(NWCGConstants.NWCG_BILLINGTRANS_PROCESS_TYPE_CONFIRM_WO)) {
			returnObj = new NWCGProcessBillingTransConfirmWO();
			isReturnObjSet = true;
		}
		if (strAction.equals(NWCGConstants.NWCG_BILLINGTRANS_PROCESS_TYPE_REFURB)) {
			returnObj = new NWCGProcessBillingTransRefurb();
			isReturnObjSet = true;
		}
		if (strAction.equals(NWCGConstants.NWCG_BILLINGTRANS_PROCESS_TYPE_EXTRACT)) {
			returnObj = new NWCGProcessBillingTransactionExtractFTP();
			isReturnObjSet = true;
		} if(isReturnObjSet != true) {
			// returning NULL No matching strings for: strAction
		}
		return returnObj;
	}
}