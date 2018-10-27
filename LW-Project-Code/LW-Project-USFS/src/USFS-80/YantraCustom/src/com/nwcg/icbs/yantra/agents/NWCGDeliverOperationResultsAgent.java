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

package com.nwcg.icbs.yantra.agents;

import gov.nwcg.services.ross.resource_order._1.DeliverOperationResultsResp;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;

import javax.xml.bind.JAXBException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.ib.msg.NWCGDeliverOperationResultsIB;
import com.nwcg.icbs.yantra.webservice.msgStore.NWCGMessageStoreInterface;
import com.nwcg.icbs.yantra.webservice.util.NWCGJAXBContextWrapper;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGDeliverOperationResultsAgent extends YCPBaseAgent {
	private static Logger logger = Logger.getLogger(NWCGDeliverOperationResultsAgent.class.getName());
	private static YIFApi api;
    
    static {
        try {
            api = YIFClientFactory.getInstance().getLocalApi();
        } catch (YIFClientCreationException e) {
        	logger.error("Error while instantiating YIFApi .. ", e);
        }
    }    
    
    private Document getIBMessageDetails (YFSEnvironment env, String distID) throws Exception { 
		
		StringBuffer sb = new StringBuffer("<NWCGInboundMessage DistributionID=\"");
		sb.append(distID);
		sb.append("\" MessageType=\"LATEST\" />");		
		String getIBMsgDtlsStr = sb.toString();
		
		Document apiOutputDoc = null;
		try {
			apiOutputDoc = CommonUtilities.invokeService(env,
					"NWCGGetIBMessageListService", getIBMsgDtlsStr);
		} catch (Exception e) {
			e.printStackTrace();
			return null;    		
		}

		return apiOutputDoc;
    }
    
	public void executeJob(YFSEnvironment env, Document obMsgStoreRecord) throws Exception {
		startTimer("NWCGDeliverOperationResultsAgent::executeJob");
		logger.debug("NWCGDeliverOperationResultsAgent::executeJob, inside execute jobs");
		logger.debug("--------------------------------------------------");
		logger.debug("  NWCGDeliverOperationResultsAgent::executeJob()  ");
		logger.debug("--------------------------------------------------");
		
		if(obMsgStoreRecord != null) {
			Element docElm = obMsgStoreRecord.getDocumentElement();
			String distID = docElm.getAttribute(NWCGConstants.DIST_ID_ATTR);
			String retriesAttemptedStr = docElm.getAttribute("Lockid");
			logger.verbose("Input to executeJob(): " + XMLUtil.extractStringFromDocument(obMsgStoreRecord));			
			
			String inboundMessageKey = docElm.getAttribute(NWCGAAConstants.MESSAGE_KEY);
			logger.verbose("NWCG Inbound Message Key: "+inboundMessageKey);
			
			String messageName = docElm.getAttribute("MessageName");
			logger.verbose("MessageName: "+messageName);
			
			String temp0 = docElm.getAttribute("maxNoRetryAttempts");
			Document getIBMsgDtlsInpDoc = getIBMessageDetails(env, distID);
			NodeList nwcgInboundMessageList = null;
			if (getIBMsgDtlsInpDoc != null) {
				logger.verbose("getIBMessageDetails output: "+ XMLUtil.extractStringFromDocument(getIBMsgDtlsInpDoc));
				Element getIBMsgDtlsInpDocElm = getIBMsgDtlsInpDoc.getDocumentElement();
				nwcgInboundMessageList = getIBMsgDtlsInpDocElm.getElementsByTagName(NWCGAAConstants.NWCG_INBOUND_MSG_ELM);			
			}
			
			// If no records are returned or not just 1, error scenario
			if (nwcgInboundMessageList == null || nwcgInboundMessageList.getLength() != 1){
				logger.error("Distribution ID: "+distID+" not found in SENT_FAILED status (type: LATEST) in NWCG_INBOUND_MESSAGE table at " +CommonUtilities.getXMLCurrentTime());
				return;
			}
			
			Object o = nwcgInboundMessageList.item(0);
			Element inbMsgStoreElm = (o instanceof Element) ? (Element) o : null;
			if (inbMsgStoreElm == null) return;
			
			//Determiner whether to proceed based on wehether this particular
			//IB msg store record has become greater than the maximum allowable
			//retry attempts via the Agent Criteria
			int retriesAttemptedInt = 1;
			
			//Get the maximum number of retry attempts configured in the agent
			//criteria that has been passed in the root element of each
			//org.w3c.dom.Document object passed to executeJobs()
			int maxNoRetryAttempts = 3;
			try {
				maxNoRetryAttempts = Integer.parseInt(temp0);
			}
			catch (NumberFormatException nfe) {
				logger.error("Unable to parseInt attribute of \"maxNoRetryAttempts\" from root element.", nfe);
			}
			
			try {
				retriesAttemptedInt = Integer.parseInt(retriesAttemptedStr);
			}
			catch (NumberFormatException nfe) {
				logger.debug("executeJobs(): Defaulting to first retry attempt.");				
			}
			if (retriesAttemptedInt > maxNoRetryAttempts)  {
				logger.info("ICBS has attempted to deliver the operation results for Distribution ID: "+distID+ " greater than the maximum" +
						" number of retry attempts allowed in the agent criteria: "  + maxNoRetryAttempts);
				updateInboundMessageModifyTs(env, distID, inboundMessageKey, "FAILED_AFTER_"+maxNoRetryAttempts+"_ATTEMPTS");
				endTimer("NWCGDeliverOperationResultsAgent::executeJob");
				return;
			}

			// Determine whether we're running in test mode with no actual
			// API calls made. SOAPMessage is printed instead to the 
			// log appender configured for this class or package in tge
			// AGENTDynamicclasspath.cfg classpath entry for
			// resources.jar!/resources/log4jconfig.xml
			//String liveModeFromAgentCriteria = LIVE_MODE;
			String message = inbMsgStoreElm.getAttribute(NWCGAAConstants.MESSAGE);
			String productionMode = ResourceUtil.get("DeliverOperationResultsAgent.production_mode");
					
			String liveModeFromIbMsg = docElm.getAttribute("liveMode");
			String temp2 = docElm.getAttribute("liveModeSetInAgentCriteria");			
			
			docElm.removeAttribute("liveMode");
			docElm.removeAttribute("liveModeSetInAgentCriteria");
			docElm.removeAttribute("maxNoRetryAttempts");
			docElm.removeAttribute("retryLagTimeInSeconds");			
			
			boolean liveModeSetInAgentCriteria = false;
			try {
				liveModeSetInAgentCriteria = Boolean.parseBoolean(temp2);
			}
			catch (Exception e) {}
			
			// If the "LiveMode" agent criteria is present for the Deliver Operation
			// Results Agent transaction, then the value of this parameter supercedes
			// whatever value may be specified in NWCGAnAEnvironment.properties
			if (liveModeSetInAgentCriteria)				
			{
				// if an agent criteria entry for "LiveMode" is found and it is 
				// equal to true or y, case insensitive, then this indicates
				// the agent is in running in Live Mode and is resending messages
				// to the ROSS system.
				if (liveModeFromIbMsg.equalsIgnoreCase(NWCGConstants.STR_TRUE))
				{
					logger.debug("DeliverOperationResultsAgent: Live Mode is ON! [via Agent Criteria]");				
					logger.debug("Resending: "+ message);
					postResponseToROSS(env, message, distID, messageName);
				}
				// if LiveMode is set not true, then we display output to logger.info() 
				else
				{
					logger.info("NWCGDeliverOperationResultsAgent::executeJob would have sent:");
					logger.info("(if DeliverOperationResultsAgent.production_mode=true in NWCGAnAEnvironment.properties)");
					logger.info("Distribution ID: " + distID);
					logger.info("DeliverOperationResultsAgent.production_mode="+productionMode);
					logger.info("Input XML: " + message);	
					updateInboundMessageModifyTs(env, distID, inboundMessageKey, null);
				}
			}
			// However, if there is not an Agent Criteria Parameter Name of "LiveMode"
			// set for the transaction, we'll look to see if it's set in the 
			// AGENTDynamicclasspath.cfg classpath entry for:
			// resources.jar!/resources/extn/NWCGAnAEnvironment.properties
			// DeliverOperationResultsAgent.production_mode=
			else {
				
				if (!StringUtil.isEmpty(productionMode) && 
					(productionMode.equalsIgnoreCase(NWCGConstants.STR_TRUE) ||
					 productionMode.equalsIgnoreCase(NWCGConstants.YES)))
				{
					logger.info("DeliverOperationResultsAgent: Live Mode is ON! [via properties file]");				
					logger.debug("Resending: "+ message);
					postResponseToROSS(env, message, distID, messageName);
				}
				else
				{
					logger.info("NWCGDeliverOperationResultsAgent::executeJob would have sent:");
					logger.info("(if DeliverOperationResultsAgent.production_mode=true in NWCGAnAEnvironment.properties)");
					logger.info("Distribution ID: " + distID);
					logger.info("DeliverOperationResultsAgent.production_mode="+productionMode);
					logger.info("Input XML: " + message);
					updateInboundMessageModifyTs(env, distID, inboundMessageKey, null);
				}
			}
		}
		endTimer("NWCGDeliverOperationResultsAgent::executeJob");
	}
	
	/***
	 * Updates a NWCG_INBOUND_MESSAGE record's MODIFYTS value in order to 
	 * avoid having records picked up by the agent over and over again, 
	 * needlessly when NOT operating in production mode.
	 *   
	 * @param env
	 * @param distID
	 * @param messageName
	 * @throws Exception
	 */
	private void updateInboundMessageModifyTs (YFSEnvironment env, String distID, String inboundMessageKey, String status) throws Exception {
		
		Document IBDoc = XMLUtil.createDocument(NWCGAAConstants.NWCG_INBOUND_MSG_ELM);	
		Element docElm = IBDoc.getDocumentElement();
		
		docElm.setAttribute(NWCGAAConstants.MESSAGE_KEY, inboundMessageKey);
		docElm.setAttribute(NWCGAAConstants.DIST_ID_ATTR, distID);			
		docElm.setAttribute(NWCGAAConstants.MESSAGE_TYPE, NWCGAAConstants.MESSAGE_TYPE_LATEST);
		if (!StringUtil.isEmpty(status)) {
			docElm.setAttribute(NWCGAAConstants.MESSAGE_STATUS, status);
		}
		
		// Not sure if this needed or even acceptable by the API, but it won't reject
		// the entire change API call because of the presence of the attribute so
		// it's in for now to ensure that the Modifyts on the current NWCG_INBOUND_MESSAGE
		// record being processed is updated to the current time.
		docElm.setAttribute("Modifyts", CommonUtilities.getXMLCurrentTime());

		logger.verbose("Input to NWCGChangeIBMessage"+XMLUtil.extractStringFromDocument(IBDoc));
		Document doc_rt = CommonUtilities.invokeService(env,NWCGAAConstants.SDF_CHANGE_IB_MESSAGE_NAME,IBDoc);
		logger.verbose("Output from NWCGChangeIBMessage: "+ XMLUtil.extractStringFromDocument(doc_rt));
	}
	
	/**
	 * Creates a DeliverOperationResultsResp object and sets the xs:any portion
	 * as the CLOB message contents. Sends the message to ROSS via the
	 * NWCGDeliverOperationResultsIB().process(YFSEnvironment, Document) method.
	 * 
	 * @author drodriguez
	 * @param YFSEnvironment
	 * @param messageToSend
	 * @param distID
	 * @param messageName
	 */
	private void postResponseToROSS(YFSEnvironment env, String messageToSend, String distID, String messageName) throws Exception {
		logger.debug("NWCGDeliverOperationResultsAgent::postResponseToROSS: begin");
		
		try {
			//Create an XML Doc of the messageToSend
			Document resp = XMLUtil.getDocument(messageToSend);			
			logger.verbose("messageToSend in XML: "+XMLUtil.extractStringFromDocument(resp));
			logger.debug("messageName: "+messageName);
			
			// Call A&A framework for synchronous delivery of delivery
			// operations
			Object o = new NWCGJAXBContextWrapper().getObjectFromNode(resp, messageName);
			Document responseDoc = null;
			if (o != null) {
				String messageClass = o.getClass().getName();
				logger.debug("messageClass: "+messageClass);
				
				//Defaults to the ResourceOrder namespace
				URL urlToUse = null;
				if (messageClass.contains("catalog")) {
					urlToUse = new URL(NWCGAAConstants.CATALOG_NAMESPACE);
				}
				else if (messageClass.contains("common_types")) {
					urlToUse = new URL(NWCGAAConstants.COMMON_TYPES_NAMESPACE);
				}
				else if (messageClass.contains("notification")) {
					urlToUse = new URL(NWCGAAConstants.RESOURCE_ORDER_NOTIFICATION_NAMESPACE);
				}
				else if (messageClass.contains("resource_order")) {
					urlToUse = new URL(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE);					
				}
				else {
					throw new Exception("ICBS was unable to determine the proper namespace for "+messageClass+" "+distID);
				}
				
				responseDoc = new NWCGJAXBContextWrapper().
									getDocumentFromObject(o, 
											responseDoc, 
											urlToUse);
				
				Element responseDocElm = responseDoc.getDocumentElement();

				// Set up the attributes required by A&A framework
				responseDocElm.setAttribute(NWCGAAConstants.MDTO_DISTID, distID);
				responseDocElm.setAttribute("NWCGUSERID", NWCGAAConstants.ENV_USER_ID);
				responseDocElm.setAttribute(NWCGAAConstants.OPERATION_TYPE, NWCGAAConstants.OPERATION_SYNC);
				responseDocElm.setAttribute(NWCGAAConstants.MDTO_MSGNAME, messageName);
				
				responseDocElm.setAttribute(NWCGAAConstants.MDTO_USERNAME, 	NWCGAAConstants.ENV_USER_ID);
				responseDocElm.setAttribute(NWCGAAConstants.MDTO_MSGNAME, 	messageName);
				responseDocElm.setAttribute(NWCGAAConstants.MDTO_DISTID, 	distID);
				responseDocElm.setAttribute(NWCGAAConstants.MDTO_NAMESPACE, 	urlToUse.toString());				
				responseDocElm.setAttribute(NWCGMessageStoreInterface.MESSAGE_MAP_SERVNAME, messageName);				
				responseDocElm.setAttribute(NWCGMessageStoreInterface.MESSAGE_MAP_MSGBODY, XMLUtil.csc(XMLUtil.extractStringFromDocument(responseDoc)));
				
				if(env != null && !StringUtil.isEmpty(env.getSystemName()))
					responseDocElm.setAttribute(NWCGMessageStoreInterface.MESSAGE_MAP_SYSNAME,env.getSystemName()); 
								
				DeliverOperationResultsResp aaResponse = new NWCGDeliverOperationResultsIB().process(env, responseDoc);
				if (aaResponse != null)
					logger.verbose("Output of DeliverOperationResultsResp.process(): "+ aaResponse.toString());
			}
			else {
				throw new Exception("ICBS could not marshall the contents of NWCG_INBOUND_MESSAGE.MESSAGE for "+messageName+" "+distID);
			}			
		}
		catch (JAXBException je) {
			logger.error("JAXB Related Error in Deliver OperationResults Agent!", je);
			je.printStackTrace();
			throw je;
		}
		catch (Exception e) {
			logger.error("Failed to post response to ROSS:", e);
			e.printStackTrace();
			throw e;
		}
		logger.debug("NWCGDeliverOperationResultsAgent::postResponseToROSS: end");
	}	
	
	/**
	 * This method will gets NWCG_INBOUND_MESSAGE records in SENT_FAILED status
	 * whose MODIFYTS is greater than DeliverOperationResultsRetryLagTime ms ago
	 * from resources/extn/NWCGAnAEnvironment.properties
	 * @author drodriguez
	 * @param YFSEnvironment
	 * @param inputDoc 
	 * @param lastMsg
	 */
	public List getJobs(YFSEnvironment env, Document inputDoc, Document lastMsg) throws Exception {
		startTimer("NWCGDeliverOperationResultsAgent::getJobs()");
		logger.debug("--------------------------------------------------");
		logger.debug("   NWCGDeliverOperationResultsAgent::getJobs()    ");		
		logger.debug("--------------------------------------------------");		
		logger.verbose("Input XML Document from agent framework: " + 
				XMLUtil.extractStringFromDocument(inputDoc));
		
		if (lastMsg != null) {
			logger.info("Finished trigger at: " + CommonUtilities.getXMLCurrentTime());
			endTimer("NWCGDeliverOperationResultsAgent::getJobs()");
			return null;
		}	
		
		String liveMode = "false";
		boolean liveModeSetInAgentCriteria = false;
		int retryLagTimeInSeconds = 60;
		int maxNoRetryAttempts = 3;
		boolean retryLagTimeSetInAgentCriteria = false;
		
		try {
	    	// The document type to search for in the query, default to 0001
			String agentCriteriaLiveModeParm = inputDoc.getDocumentElement().getAttribute("LiveMode");
			if (!StringUtil.isEmpty(agentCriteriaLiveModeParm) &&
				(agentCriteriaLiveModeParm.equalsIgnoreCase(NWCGConstants.STR_TRUE) ||
				agentCriteriaLiveModeParm.equalsIgnoreCase(NWCGConstants.YES))) {
				liveMode = NWCGConstants.STR_TRUE;
				liveModeSetInAgentCriteria = true;
			}
			
			String agentCriteriaRetryLagTimeSeconds = inputDoc.getDocumentElement().getAttribute("RetryLagTimeInSeconds");
			if (!StringUtil.isEmpty(agentCriteriaRetryLagTimeSeconds)) {
				try {
					retryLagTimeInSeconds = Integer.parseInt(agentCriteriaRetryLagTimeSeconds);
					retryLagTimeSetInAgentCriteria = true;
				}
				catch (NumberFormatException nfe) {
					//Not a valid integer set as the parm, defaulting to
					//what we found in NWCGAnAEnvironment.properties
					retryLagTimeSetInAgentCriteria = false;
				}
			}
			
			String agentCriteriaMaxRetryAttempts = inputDoc.getDocumentElement().getAttribute("MaxRetryAttempts");
			if (!StringUtil.isEmpty(agentCriteriaMaxRetryAttempts)) {
				try {
					maxNoRetryAttempts = Integer.parseInt(agentCriteriaMaxRetryAttempts);
					logger.debug("MaxRetryAttempts set in Agent Criteria: "+maxNoRetryAttempts);
				}
				catch (NumberFormatException nfe) {
					//Not a valid integer set as the parm, defaulting to
					//3 max attempts
					logger.error("An integer wasn't found as the value for MaxRetryAttempts agent criteria value", nfe);
					logger.debug("Defaulting to 3 Maximum Retry Attempts");
					maxNoRetryAttempts = 3;
				}
			}
		} 
		catch (Exception e) {
			logger.error("Error trying to get Agent Criteria Parameter & Value: LiveMode", e);
			e.printStackTrace();
		}
				
		List<Document> sentFailedList = getIBRecordsOverLagTime(env, NWCGAAConstants.MESSAGE_STATUS_SENT_FAILED, retryLagTimeSetInAgentCriteria, retryLagTimeInSeconds, maxNoRetryAttempts);
		List<Document> readyForPickUpList = getIBRecordsOverLagTime(env, NWCGAAConstants.MESSAGE_STATUS_FAILED_READY_FOR_PICKUP, retryLagTimeSetInAgentCriteria, retryLagTimeInSeconds, maxNoRetryAttempts);
		if (sentFailedList == null && readyForPickUpList == null) return null;
		
		int numInSentFailedStatus = sentFailedList.size();
		int numInReadyForPickUpStatus = readyForPickUpList.size();
		
		if (numInSentFailedStatus != 0) {
			logger.debug("Deliver Operation Results Agent getJobs() found "+numInSentFailedStatus+" record(s) in status" +
					NWCGAAConstants.MESSAGE_STATUS_SENT_FAILED);
		}	
		
		if (numInReadyForPickUpStatus != 0) {
			logger.debug("Deliver Operation Results Agent getJobs() found "+numInSentFailedStatus+" record(s) in status" +
					NWCGAAConstants.MESSAGE_STATUS_FAILED_READY_FOR_PICKUP);
			sentFailedList.addAll(readyForPickUpList);
		}
		logger.info("getJobs returning "+ numInSentFailedStatus +" msgs in status: " + NWCGAAConstants.MESSAGE_STATUS_SENT_FAILED);
		logger.info("getJobs returning "+ numInReadyForPickUpStatus +" msgs in status: " + NWCGAAConstants.MESSAGE_STATUS_FAILED_READY_FOR_PICKUP);		
		endTimer("NWCGDeliverOperationResultsAgent::getJobs()");
		
		//Add the agent criteria name/value pairs as attributes of the root node of each document in the returned list
		return updateAllSentFailedListWithAgentCriteria(sentFailedList, liveModeSetInAgentCriteria, retryLagTimeInSeconds, maxNoRetryAttempts, liveMode);
	}
	
	private List updateAllSentFailedListWithAgentCriteria(
			List<Document> sentFailedList, boolean liveModeSetInAgentCriteria,
			int retryLagTimeInSeconds, int maxNoRetryAttempts, String liveMode) 
	{		
		String lvMoStr = "false";
		for (int i = 0; i < sentFailedList.size(); i++) {
			Document cur = sentFailedList.get(i);
			if (liveModeSetInAgentCriteria) {
				lvMoStr = "true";
			}
			cur.getDocumentElement().setAttribute("liveModeSetInAgentCriteria",
					lvMoStr);
			cur.getDocumentElement().setAttribute("retryLagTimeInSeconds",
					Integer.toString(retryLagTimeInSeconds));
			cur.getDocumentElement().setAttribute("maxNoRetryAttempts",
					Integer.toString(maxNoRetryAttempts));
			cur.getDocumentElement().setAttribute("liveMode", liveMode);
		}
		return sentFailedList;
	}

	/**
	 *  Create the input for and calls the NWCGGetIBMessageListServiceWTemplate service:
	 *	 <NWCGInboundMessage MessageStatus="SENT_FAILED" ModifytsQryType="LT" 
	 *	 Modifyts="less than NWCGAnAEnvironment.properties/DeliverOperationResultsRetryLagTime"/>
	 * @param env
	 * @param String msgStatus representing the status we're looking for in the OB msg store table.
	 * @return List Documents representing each <NWCGInboundMessage> record returned
	 * @throws Exception
	 */
	private List<Document> getIBRecordsOverLagTime (YFSEnvironment env, String msgStatus, boolean retryLagTimeSetInAgentCriteria, int retryLagTimeInSeconds, int maxNoRetryAttempts) throws Exception {

		List<Document> messagesToResend = new ArrayList<Document>();
		String defaultRetryLagTime = "60000";
		int seconds = 60;
		
		// if the retry lag time in seconds has been set in the agent criteria,
		// use that time, otherwise default to what is found in NWCGAnAEnvironment.properties,
		// or if worst case, defaults to 60000ms=60s=1m
		if (retryLagTimeSetInAgentCriteria) {
			seconds = retryLagTimeInSeconds;
		}
		else {
			String reprocessingTime = ResourceUtil.get(NWCGAAConstants.IB_DELIVER_OPERATION_RESULTS_WAIT_TIME, 
					defaultRetryLagTime);
			Integer repTimeInt = Integer.parseInt(reprocessingTime);	
			int milliSeconds = repTimeInt.intValue();
			seconds = milliSeconds/1000;	
		}			
		
		SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		Calendar xTimeAgo = Calendar.getInstance();
		xTimeAgo.add(Calendar.SECOND, -seconds);
		String xmlFormattedXTimeAgo = formatter.format(xTimeAgo.getTime());
		
		Document inGetIBMsgDoc = XMLUtil.createDocument(NWCGAAConstants.NWCG_INBOUND_MSG_ELM);
		Element ibMsgSentFailedDocElm = inGetIBMsgDoc.getDocumentElement();
		ibMsgSentFailedDocElm.setAttribute(NWCGAAConstants.MESSAGE_STATUS, msgStatus);
		ibMsgSentFailedDocElm.setAttribute("ModifytsQryType", "LT");
		ibMsgSentFailedDocElm.setAttribute("Modifyts", xmlFormattedXTimeAgo);
		
		logger.debug("--------------------------------------------------------");
		logger.debug("Current time: " + CommonUtilities.getXMLCurrentTime());
		logger.debug("" + seconds +" seconds ago: " + xmlFormattedXTimeAgo);		
		logger.debug("--------------------------------------------------------");		

		logger.verbose("Generated NCWGGetInboundMessageListServiceWTemplate Input XML : " 
				+ XMLUtil.extractStringFromDocument(inGetIBMsgDoc));		
		
		Document opGetIBMsgDoc = api.executeFlow(env, 
				NWCGAAConstants.GET_IB_MESSAGE_LIST_SERVICE_WTEMPLATE, inGetIBMsgDoc);

		if (opGetIBMsgDoc != null){
			logger.verbose("Output from " + NWCGAAConstants.GET_IB_MESSAGE_LIST_SERVICE_WTEMPLATE + ": " 
					+ XMLUtil.extractStringFromDocument(opGetIBMsgDoc));
		}
		else {
			logger.debug("Output from " + NWCGAAConstants.GET_IB_MESSAGE_LIST_SERVICE_WTEMPLATE + " is null!");
			return null;
		}
		
		List obMsgs = XMLUtil.getElementsByTagName(opGetIBMsgDoc.getDocumentElement(), NWCGAAConstants.NWCG_INBOUND_MSG_ELM);
		if (!obMsgs.isEmpty()) {
			ListIterator li = obMsgs.listIterator();
			while (li.hasNext()) {
				try {			
					Object o = li.next();
					if (o instanceof Element) {
						Document toAddToList = XMLUtil.getDocument();
						Element curIbMsgElm = (Element) o;
						int lockid = 0;
						String distID = curIbMsgElm.getAttribute(NWCGConstants.DIST_ID_ATTR);
						String inboundMessageKey = curIbMsgElm.getAttribute(NWCGAAConstants.MESSAGE_KEY);
						if (StringUtil.isEmpty(distID)) distID = new String();
						
						if (curIbMsgElm != null && curIbMsgElm.hasAttribute("Lockid")) {
							try {
								lockid = Integer.parseInt(curIbMsgElm.getAttribute("Lockid"));
							}
							catch (NumberFormatException nfe) {
								logger.info("Lockid not found on NWCGIBMessage element!!");
							}
						}
						
						if (lockid > maxNoRetryAttempts) {
							logger.info("ICBS has attempted to deliver the operation results for Distribution ID: "+distID + 
									" greater than the maximum" +
									" number of retry attempts allowed in the agent criteria: "  + 
									maxNoRetryAttempts);
							updateInboundMessageModifyTs(env, distID, inboundMessageKey, "FAILED_AFTER_"+maxNoRetryAttempts+"_ATTEMPTS");
							endTimer("NWCGDeliverOperationResultsAgent::executeJob");
							continue;
						}
						else 
						{			
							Node n = toAddToList.importNode((Element) o,  true);
							toAddToList.appendChild(n);
							messagesToResend.add(toAddToList);
							logger.verbose("Adding to messagesToResend List: " 
									+ XMLUtil.extractStringFromDocument(toAddToList));
						}
					}
				}
				catch (Exception e) {
					throw e;
				}
			}
		}
		else {
			logger.debug("No inbound message store records found for status "+msgStatus);
		}
		return messagesToResend;
	}
	
	/**
	 *	Returns the number of jobs pending for this agent. This information will be displayed
     *  in the System Management UI. This method will also be called by the health monitor process
     *  to alert if the pending job count exceeds the threshold configured.
     *  The input to this method is the criteria element or the Input XML that gets passed 
     *  to the getJobs method.
     *
	 * @param env
	 * @param criteriaElem
	 * @return long Number of Inbound Message Store records left to resend in SENT_FAILED or
	 * 			FAILED_READY_FOR_PICKUP status
	 */
	public long getPendingJobCount(YFSEnvironment env, YFCElement criteriaElem){

		long retVal = -1;
		
		try {
			List<Document> sentFailedPending = 
				getIBRecordsOverLagTime(env, NWCGAAConstants.MESSAGE_STATUS_SENT_FAILED, true, 60, 10);
			List<Document> failedReadyPickUp = 
				getIBRecordsOverLagTime(env, NWCGAAConstants.MESSAGE_STATUS_FAILED_READY_FOR_PICKUP, true, 60, 10);
			if (sentFailedPending != null && failedReadyPickUp!= null) {
				retVal = new Integer(sentFailedPending.size()).longValue();
				retVal += new Integer(failedReadyPickUp.size()).longValue();
			}
		}
		catch (Exception e)  {
			logger.error("Failed to getPendingJobCount for NWCGDeliverOperationResultsAgent", e);
			e.printStackTrace();			
		}			
		return retVal;
    }
}