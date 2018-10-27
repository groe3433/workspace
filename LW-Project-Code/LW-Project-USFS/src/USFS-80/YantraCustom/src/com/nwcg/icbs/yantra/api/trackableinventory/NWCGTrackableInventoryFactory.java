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

package com.nwcg.icbs.yantra.api.trackableinventory;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.Logger;

/**
 * This is a factory, will return the message consumer based 
 * on the action passed
 * 
 * @author Oxford
 */
public class NWCGTrackableInventoryFactory {
	
	private static Logger log = Logger.getLogger(NWCGITrackableRecordMutator.class.getName());
	
	public static NWCGITrackableRecordMutator getMessageProcesser(String strAction) {
		if (log.isVerboseEnabled())
			log.verbose("NWCGTrackableInventoryFactory::getMessageProcesser gotAction=" + strAction);
		NWCGITrackableRecordMutator returnObj = null;
		if (strAction.equals(NWCGConstants.NWCG_TRACKABLEINVENTORY_PROCESS_TYPE_ADJUST_LOCATION)) {
			if (log.isVerboseEnabled())
				log.verbose("NWCGTrackableInventoryFactory::getMessageProcesser returning NWCGProcessAdjustLocationInventory");
			returnObj = new NWCGProcessAdjustLocationInventory();
		} else if (strAction.equals(NWCGConstants.NWCG_TRACKABLEINVENTORY_PROCESS_TYPE_BLIND_RETURN)) {
			if (log.isVerboseEnabled())
				log.verbose("NWCGTrackableInventoryFactory::getMessageProcesser returning NWCGProcessBlindReturn");
			returnObj = new NWCGProcessBlindReturn();
		} else if (strAction.equals(NWCGConstants.NWCG_TRACKABLEINVENTORY_PROCESS_TYPE_CONFIRM_DRAFT_ORDER)) {
			if (log.isVerboseEnabled())
				log.verbose("NWCGTrackableInventoryFactory::getMessageProcesser returning NWCGProcessConfirmDraftOrder");
			returnObj = new NWCGProcessConfirmDraftOrder();
		} else if (strAction.equals(NWCGConstants.NWCG_TRACKABLEINVENTORY_PROCESS_TYPE_CONFIRM_SHIPMENT)) {
			if (log.isVerboseEnabled())
				log.verbose("NWCGTrackableInventoryFactory::getMessageProcesser returning NWCGProcessConfirmShipment");
			returnObj = new NWCGProcessConfirmShipment();
		} else if (strAction.equals(NWCGConstants.NWCG_TRACKABLEINVENTORY_PROCESS_TYPE_KITTING)) {
			if (log.isVerboseEnabled())
				log.verbose("NWCGTrackableInventoryFactory::getMessageProcesser returning NWCGProcessSerialNumberActivitiesCompleted");
			returnObj = new NWCGProcessSerialNumberActivitiesCompleted();
		} else if (strAction.equals(NWCGConstants.NWCG_TRACKABLEINVENTORY_PROCESS_TYPE_RECEIVE_ORDER)) {
			if (log.isVerboseEnabled())
				log.verbose("NWCGTrackableInventoryFactory::getMessageProcesser returning NWCGProcessReceiveOrder");
			returnObj = new NWCGProcessReceiveOrder();
		} else if (strAction.equals(NWCGConstants.NWCG_TRACKABLEINVENTORY_PROCESS_TYPE_RECEIVE_TRANSFER_ORDER)) {
			if (log.isVerboseEnabled())
				log.verbose("NWCGTrackableInventoryFactory::getMessageProcesser returning NWCGProcessReceiveTransferOrder");
			returnObj = new NWCGProcessBlindReturn();
		} else if (strAction.equals(NWCGConstants.NWCG_TRACKABLEINVENTORY_PROCESS_TYPE_TASK_COMPLETED)) {
			if (log.isVerboseEnabled())
				log.verbose("NWCGTrackableInventoryFactory::getMessageProcesser returning NWCGProcessTaskCompleted");
			returnObj = new NWCGProcessTaskCompleted();
		} else {
			if (log.isVerboseEnabled())
				log.verbose("NWCGTrackableInventoryFactory::getMessageProcesser returning NULL No matching strings for " + strAction);
		}
		return returnObj;
	}
}