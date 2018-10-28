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

package com.nwcg.icbs.yantra.webservice.msgStore;

import java.util.HashMap;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public interface NWCGMessageStoreInterface {
	
	public void setMsgStatusSentFault(HashMap msgMap);

	public void setMsgStatusProcessedSync(HashMap msgMap);

	public void setMsgStatusSentSyncFailed(HashMap msgMap);

	public void setMsgStatusSentSyncFault(HashMap msgMap);

	public void setMsgStatusSentSync(HashMap msgMap);

	public void setMsgStatusFailedReadyForPickup(HashMap msgMap);

	public void setMsgStatusFaultReadyForPickup(HashMap msgMap);

	public void setMsgStatusInboundProcessed(HashMap<Object, Object> msgMap);

	public void setMsgStatusInboundProcessed(HashMap<Object, Object> msgMap,
			String responseXML);

	public void setMsgStatusOutboundProcessed(HashMap msgMap);

	public void setMsgStatusProcessingFault(HashMap msgMap);

	public void setMsgStatusProcessingFault(HashMap<Object, Object> msgMap,
			String faultText);

	public void setMsgStatusForcedProcessedFault(HashMap<Object, Object> msgMap);

	public void setMsgStatusForcedProcessed(HashMap<Object, Object> msgMap);

	public void setMsgStatusForcedProcessedOB(HashMap<Object, Object> msgMap);

	public void setMsgStatusForcedProcessedIB(HashMap<Object, Object> msgMap);

	public void setMsgStatusForcedResponseFault(HashMap msgMap);

	public void setMsgStatusForcedProcessedFaultIB(
			HashMap<Object, Object> msgMap);

	public void setMsgStatusForcedProcessedFaultOB(
			HashMap<Object, Object> msgMap);

	public void setMsgStatusForcedFailed(HashMap msgMap);

	public void setMsgStatusSentFailed(HashMap msgMap);

	public void setMsgStatusSent(HashMap msgMap);

	public void setMsgStatusSecurityFailed(HashMap<Object, Object> msgMap);

	public void setMsgStatusSecurityFailed(HashMap<Object, Object> msgMap,
			String soapFaultXml);

	public void updateOutboundMessage(YFSEnvironment env, String distID,
			String message_key) throws Exception;

	public String getLatestMessageKeyIBForDistID(YFSEnvironment env,
			String distID);

	public String getLatestMessageKeyOBForDistID(YFSEnvironment env,
			String distID);

	public HashMap getResponseMessageForDistID(HashMap<Object, Object> msgMap);

	public void setMsgStatusReceived(HashMap<Object, Object> msgMap);

	public void setMsgStatusReceived(HashMap<Object, Object> msgMap,
			String latestMsgKey);

	public void setMsgStatusSentForProcessing(HashMap<Object, Object> msgMap);

	public void setMsgStatusSentForProcessing(HashMap<Object, Object> msgMap,
			String ack);

	public void setMsgStatusAckNegative(HashMap msgMap);

	public void setMsgStatusPosRespPosted(HashMap msgMap);

	public void setMsgStatusNegRespPosted(HashMap msgMap);

	public void setMsgOBStatusSoapFault(HashMap<Object, Object> msgMap);

	public HashMap<String, String> checkMessageNameAndDistID(String distID,
			String messageName);

	public String initOutboundMessageFromMap(HashMap<Object, Object> msgMap)
			throws Exception;

	public String initInboundMessageFromMap(HashMap<Object, Object> msgMap)
			throws Exception;

	// A&A message map
	public final static String MESSAGE_MAP_MSG = "message";

	public final static String MESSAGE_MAP_MSGTYPE = "message_type";

	public final static String MESSAGE_MAP_SYSNAME = "system_name";

	public final static String MESSAGE_MAP_LATESTKEY = "latest_message_key";

	public final static String MESSAGE_MAP_KEY = "message_key";

	public final static String MESSAGE_MAP_ENV = "environment";

	public final static String MESSAGE_MAP_SERVNAME = "serviceName";

	public final static String MESSAGE_MAP_PWD = "password";

	public final static String MESSAGE_MAP_CUSTOMPROP = "customProperty";

	public final static String MESSAGE_MAP_MSGBODY = NWCGAAConstants.MDTO_MSGBODY;// "messageBody";

	public final static String MESSAGE_MAP_MSGNAME = NWCGAAConstants.MDTO_MSGNAME;// "messageName";

	public final static String MESSAGE_MAP_DISTID = NWCGAAConstants.MDTO_DISTID;// "distID";

	public final static String MESSAGE_MAP_USERNAME = NWCGAAConstants.MDTO_USERNAME;// "username";

	public final static String MESSAGE_MAP_NAMESPACE = NWCGAAConstants.MDTO_NAMESPACE;// "namespace";

	public final static String MESSAGE_MAP_ENTNAME = NWCGAAConstants.MDTO_ENTNAME;// "EntityName";

	public final static String MESSAGE_MAP_ENTKEY = NWCGAAConstants.MDTO_ENTKEY;// ="EntityKey";

	public final static String MESSAGE_MAP_ENTVALUE = NWCGAAConstants.MDTO_ENTVALUE;// ="EntityValue";

	// all message statuses that are acceptable for messageStore
	public final static String MESSAGE_STS_PROCESSED = "PROCESSED";

	public final static String MESSAGE_STS_FAILED_READY_FOR_PICKUP = "FAILED_READY_FOR_PICKUP";

	public final static String MESSAGE_STS_FAULT_READY_FOR_PICKUP = "FAULT_READY_FOR_PICKUP";

	public final static String MESSAGE_STS_FORCED_RESPONSE_SENT_FAILED = "FORCED_RESPONSE_SENT_FAILED";

	public final static String MESSAGE_STS_FORCED_RESPONSE_FAULT = "FORCED_RESPONSE_FAULT";

	public final static String MESSAGE_STS_FORCED_PROCESSED = "FORCED_PROCESSED";

	public final static String MESSAGE_STS_FORCED_PROCESSED_FAULT = "FORCED_PROCESSED_FAULT";

	public final static String MESSAGE_STS_SENT = "SENT";

	public final static String MESSAGE_STS_SENT_FAILED = "SENT_FAILED";

	public final static String MESSAGE_STS_SENT_FAULT = "FAULT";

	public final static String MESSAGE_STS_SENT_SYNC = "SENT_SYNC";

	public final static String MESSAGE_STS_PROCESSED_SYNC = "PROCESSED_SYNC";

	public final static String MESSAGE_STS_SENT_SYNC_FAULT = "SENT_FAULT";

	public final static String MESSAGE_STS_SENT_SYNC_FAILED = "SENT_SYNC_FAILED";

	public final static String MESSAGE_STS_SYNC_PROCESSED = "SYNC_PROCESSED";

	public final static String MESSAGE_STS_RESPONSE_RECEIVED = "RESPONSE_RECEIVED";

	public final static String MESSAGE_STS_PROCESSING_FAULT = "PROCESSING_FAULT";

	public final static String MESSAGE_STS_RECEIVED = "RECEIVED";

	public final static String MESSAGE_STS_VOID = "VOID";

	public final static String MESSAGE_STS_ACKNOLEDGEMENT_NEGATIVE = "ACKNOLEDGEMENT_NEGATIVE";

	public final static String MESSAGE_STS_SEND_FOR_PROCESSING = "SEND_FOR_PROCESSING";

	public final static String MESSAGE_STS_AUTHORIZATION_FAILED = "AUTHORIZATION_FAILED";

	public final static String MESSAGE_STS_SENT_FOR_PROCESSING = "SENT_FOR_PROCESSING";

	public final static String MESSAGE_STS_ACKNOWLEGED_NEGATIVE = "ACKNOWLEGED_NEGATIVE";

	public final static String MESSAGE_STS_POS_RESP_POSTED = "POS_RESP_POSTED";

	public final static String MESSAGE_STS_NEG_RESP_POSTED = "NEG_RESP_POSTED";

	public final static String MESSAGE_OB_DEFAULTENTITYKEY = "DEFAULTENTITYKEY";

	public final static String MESSAGE_OB_DEFAULTENTITYNAME = "DEFAULTENTITYNAME";

	public final static String MESSAGE_OB_DEFAULTENTITYVAL = "DEFAULTENTITYVALUE";

	public final static String MESSAGE_STS_SOAP_FAULT = "SOAP_FAULT";
}
