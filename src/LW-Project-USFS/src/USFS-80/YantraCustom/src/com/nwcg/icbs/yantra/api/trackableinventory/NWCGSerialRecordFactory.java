package com.nwcg.icbs.yantra.api.trackableinventory;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;

public class NWCGSerialRecordFactory 
{
	public static NWCGSerialRecord getRecordProcessor(String docNode, String docType)
	{
		if(docNode.equalsIgnoreCase(NWCGConstants.DOCUMENT_NODE_SHIPMENT) && docType.equals(NWCGConstants.DOCUMENT_TYPE_ISSUE)){
			return new NWCGSerialRecordShipmentIncidentIssue();
		}else if(docNode.equalsIgnoreCase(NWCGConstants.DOCUMENT_NODE_SHIPMENT) && docType.equals(NWCGConstants.DOCUMENT_TYPE_CACHETRANSFER)){
			return new NWCGSerialRecordShipmentCacheTransfer();
		}else if(docNode.equalsIgnoreCase(NWCGConstants.DOCUMENT_NODE_SHIPMENT) && docType.equals(NWCGConstants.DOCUMENT_TYPE_INCIDENTTRANSFER)){
			return new NWCGSerialRecordShipmentIncidentTransfer();
		}else if(docNode.equalsIgnoreCase(NWCGConstants.DOCUMENT_NODE_RECEIPT) && docType.equals(NWCGConstants.DOCUMENT_TYPE_BLINDRETURN)){
			return new NWCGSerialRecordReceiptReturn();
		}else if(docNode.equalsIgnoreCase(NWCGConstants.DOCUMENT_NODE_RECEIPT) && docType.equals(NWCGConstants.DOCUMENT_TYPE_CACHETRANSFER)){
			return new NWCGSerialRecordReceiptCacheTransfer();
		}else if(docNode.equalsIgnoreCase(NWCGConstants.DOCUMENT_NODE_RECEIPT) && docType.equals(NWCGConstants.DOCUMENT_TYPE_INCIDENTTRANSFER)){
			return new NWCGSerialRecordReceiptIncidentTransfer();
			//	BEGIN MK Production Bug Fix 1/8/13
		}else if(docNode.equalsIgnoreCase(NWCGConstants.DOCUMENT_NODE_SHIPMENT) && docType.equals(NWCGConstants.DOCUMENT_TYPE_OTHERISSUE)){
			return new NWCGSerialRecordShipmentIncidentIssue();
			//	END MK Production Bug Fix 1/8/13
		}else{
			throw new NWCGException("NWCGException: Exception occurred while creating serial record: Unexpected document node and/or type: [" + docNode + ", " + docType + "]");
		}
	}
}
