package com.nwcg.icbs.yantra.ob.reader;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGInternalMsgReader implements YIFCustomApi {

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	public Document processMsgFromQueue(YFSEnvironment env, Document docIP) throws Exception {
		NWCGLoggerUtil.Log.info("NWCGInternalMsgReader::process, Entered");
		NWCGLoggerUtil.Log.info("NWCGInternalMsgReader::process, Input document : " + XMLUtil.extractStringFromDocument(docIP));
		
		if (docIP != null){
			Element elmRootDoc = docIP.getDocumentElement();
			String messageType = elmRootDoc.getAttribute("MessageType");
			if (messageType != null && messageType.equalsIgnoreCase("CreateRequestAndPlaceReq")){
				processCreateReqAndPlaceReqMessage(env, docIP);
			}
			else {
				NWCGLoggerUtil.Log.info("NWCGInternalMsgReader::process, Unknown message");
			}
		}
		else {
			
		}
		return docIP;
	}
	
	/**
	 * Call the existing service NWCGSendICBSRInitiatedRequestToROSSService to post
	 * CreateReqAndPlaceReq service. After that, call the retry shipment mechanism.
	 * Retry shipment mechanism is not yet development. It should be developed by Increment 3 testing 
	 * @param env
	 * @param docIP
	 * @return
	 */
	private Document processCreateReqAndPlaceReqMessage(YFSEnvironment env, Document docIP){
		NWCGLoggerUtil.Log.info("NWCGInternalMsgReader::processCreateReqAndPlaceReqMessage, Entered");
		try {
			CommonUtilities.invokeService(env, "NWCGSendICBSRInitiatedRequestToROSSService", docIP);
		}
		catch(Exception e){
			NWCGLoggerUtil.Log.info("NWCGInternalMsgReader::processCreateReqAndPlaceReqMessage, Exception : " + e.getMessage());
			e.printStackTrace();
		}
		
		//TODO Sunjay. Call the shipment API exposed by Danilo for resending shipment requests
		return null;
	}
}
