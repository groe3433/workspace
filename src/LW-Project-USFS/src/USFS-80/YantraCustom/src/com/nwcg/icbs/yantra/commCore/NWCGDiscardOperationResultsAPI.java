package com.nwcg.icbs.yantra.commCore;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGDiscardOperationResultsAPI {
	
	public Document discardOperationResults(YFSEnvironment env, Document xml) throws Exception{
		
	    NWCGLoggerUtil.Log.info(XMLUtil.getXMLString(xml));
		
	    Element root = XMLUtil.getRootElement(xml);
		if( root != null){
			String action = XMLUtil.getAttribute(root, "Action");
			String distID = XMLUtil.getAttribute(root,"DistributionID");
		  
		    NWCGLoggerUtil.Log.info("action is " + action + " and distID is " + distID);
			
		    if(action != null && action.equalsIgnoreCase("Discard")) {
				NWCGMessageStore msgStore = NWCGMessageStore.getMessageStore();
				
				String latest_message_key = NWCGGetOperationResultsAPI.apiCallToGetLatestMessageKeyForDistID(env,distID);
				
				msgStore.updateMessage(distID,NWCGAAConstants.MESSAGE_DIR_TYPE_OB,XMLUtil.getXMLString(xml), NWCGAAConstants.MESSAGE_TYPE_LATEST, NWCGAAConstants.MESSAGE_STATUS_DISCARD, NWCGAAConstants.EXTERNAL_SYSTEM_NAME, latest_message_key, true);
				
				return formReturnResult("DISCARDED","0: MESSAGE DISCARDED", "Message status changed to discard.");
		    }
		}
		return formReturnResult("FAILED","-1: MESSAGE STATUS CHANGE FAILED", "Message status change failed.");
		
	}
	
	private Document formReturnResult(String icbsStatus, String respStatusStr, String respObjStr) {
		try {
			Document getOperationResults = XMLUtil.createDocument("DiscardActionResult");
			getOperationResults.getDocumentElement().setAttribute("ICBSStatus",icbsStatus);
			getOperationResults.getDocumentElement().setAttribute("ResponseStatus",respStatusStr);
			getOperationResults.getDocumentElement().setAttribute("ResponseMsg",respObjStr);
			return getOperationResults;
		} catch (Exception e) {
			NWCGLoggerUtil.Log.warning("error in discard action operation.");
			return null;
		}
		
	}


	
}