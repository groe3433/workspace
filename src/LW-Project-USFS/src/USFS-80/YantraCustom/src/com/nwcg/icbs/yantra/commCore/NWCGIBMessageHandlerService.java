package com.nwcg.icbs.yantra.commCore;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.handler.NWCGMessageHandlerInterface;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGIBMessageHandlerService implements YIFCustomApi{
	
	public void setProperties(Properties arg0) throws Exception {
		
	}
	
	//is call once a message is retrieved from JMS queue
	public Document process(YFSEnvironment env, Document xml) throws Exception{
		
		NWCGLoggerUtil.Log.info("Input XML: " + XMLUtil.getXMLString(xml));
		NWCGLoggerUtil.Log.info("NWCGIBMessageHandlerService");
		
		String serviceName = xml.getFirstChild().getNodeName();
		//String serviceGroup = NWCGAAUtil.determineServiceGroup(serviceName);
		String serviceGroup = "";
		NWCGLoggerUtil.Log.info("ServiceName:" + serviceName);
		
		NWCGMessageHandlerInterface handler = NWCGMessageHandlerFactory.getHandler(serviceName);
		Document resp_msg = handler.process(env, xml); // Actual processing ??
		
		//get the distID. ??
		//String distID = NWCGAAUtil.lookupNodeValue(xml,"distributionID");
		String distID = "";
		// Get the latest message key(message key) from NWCG_INBOUND_MESSAGE 
		// for the DistributionID
		Document inXML = XMLUtil.getDocument("<NWCGInboundMessage DistributionID="+distID+" />");
		Document opXML = CommonUtilities.invokeService(env,NWCGAAConstants.SDF_GET_IB_MESSAGE_LIST_SERVICE_NAME,inXML);
		NWCGLoggerUtil.Log.info("opXML:"+XMLUtil.getXMLString(opXML));
		
		NWCGMessageStore msgStore = NWCGMessageStore.getMessageStore();
		
		// Update message store
		//updateMessageStatusToProcessing(env, xml, serviceName, resp_msg, distID, opXML, msgStore);
		
		// Post the resp_msg to ESB synchronously.
		
				
		String resp_msg_str = "";
	
		return null;//NWCGAAUtil.buildXMLDocument(resp_msg_str);
	}

	/**
	 * @param env
	 * @param xml
	 * @param serviceName
	 * @param resp_msg
	 * @param distID
	 * @param opXML
	 * @param msgStore
	 */
	private void updateMessageStatusToProcessing(YFSEnvironment env, Document xml, String serviceName, Document resp_msg, String distID, Document opXML, NWCGMessageStore msgStore) {
		if(opXML.getDocumentElement().getChildNodes().getLength()!=0){
			
			NodeList nwcgIBMsgNL = opXML.getElementsByTagName("NWCGInboundMessage");
			
			for(int i = 0; i < nwcgIBMsgNL.getLength(); i++){
			
				Element nwcgIBMsgElem = (Element)nwcgIBMsgNL.item(i);
				String latest_msg_key = nwcgIBMsgElem.getAttribute("MessageKey");
				String messageStatus = nwcgIBMsgElem.getAttribute("MessageStatus");
				
				if(!messageStatus.equals("VOID")){
					if(resp_msg != null){
						String xmlStr = XMLUtil.getXMLString(resp_msg);
						try{
							NWCGLoggerUtil.Log.info("updating message");
							msgStore.updateMessage(distID,NWCGAAConstants.MESSAGE_DIR_TYPE_IB,xmlStr,NWCGAAConstants.MESSAGE_TYPE_LATEST,NWCGAAConstants.MESSAGE_STATUS_PROCESSING,NWCGAAConstants.SYSTEM_NAME,latest_msg_key,true,serviceName);
						}catch(Exception e){
							NWCGLoggerUtil.Log.info("Exception occured while updating message with msg status - PROCESSING");
							CommonUtilities.raiseAlert(env,NWCGAAConstants.QUEUEID_DEFAULT,"",xml,e,null);
						}
					}
				}
            }
		}
	}
	
	
}
