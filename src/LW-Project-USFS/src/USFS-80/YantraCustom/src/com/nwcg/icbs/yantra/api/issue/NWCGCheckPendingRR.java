package com.nwcg.icbs.yantra.api.issue;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.nwcg.icbs.yantra.api.issue.util.NWCGCloseAlert;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.ib.read.handler.NWCGResourceRequestReassignNotificationHandler;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGCheckPendingRR implements YIFCustomApi {

	private static Logger logger = Logger.getLogger(NWCGCheckPendingRR.class.getName());
	private String incNo = "";
	private String incYr = "";
	private Vector <String> vecPendRRDistIds = new Vector <String>();
	private Hashtable <String, Vector<String>> htDistID2PendRRKeys = new Hashtable <String, Vector<String>>();
	
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	public Document checkAndTriggerPendingRR(YFSEnvironment env, Document docIP) throws Exception {
		logger.verbose("NWCGCheckPendingRR::checkAndTriggerPendingRR, Entered");
		NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::checkAndTriggerPendingRR, Entered");
		NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::checkAndTriggerPendingRR, Input XML : " + 
				XMLUtil.extractStringFromDocument(docIP));
		String inc2incTxIssue = docIP.getDocumentElement().getAttribute(NWCGConstants.ORDER_NO);
		Vector<String> vecReqNos = setIncKeyAndGetReqs(docIP);
		if (vecReqNos != null && vecReqNos.size() > 0){
			NWCGLoggerUtil.Log.info("NWCGCheckPendingRR::checkAndTriggerPendingRR, " +
					"No of inc 2 inc transfer lines : " + vecReqNos.size());
			for (int i=0; i < vecReqNos.size(); i++){
				String reqNo = vecReqNos.get(i);
				checkAndUpdatePendingRR(env, reqNo);
			}

			// If the vector containing distribution ids size is greater than 1, then
			// check whether all the lines from that distribution id are ready to be
			// triggered. If so, trigger resource reassignment and close this particular
			// alert.
			// For our code, if pendRRXml is null, it means that atleast one of the pending RR
			// is not READY_TO_TRIGGER status
			if (vecPendRRDistIds != null && vecPendRRDistIds.size() > 0){
				for (int distIdCount=0; distIdCount < vecPendRRDistIds.size(); distIdCount++){
					String distId = vecPendRRDistIds.get(distIdCount);
					String pendRRXml = isDistReadyToTrigger(env, distId);
					if (pendRRXml != null){
						String closeReason = "Automatically resolved by ICBSR. Pending RR is triggered as " +
											 "incident to incident transfer issue " + inc2incTxIssue + " is confirmed";
						// For Pending RR, RR alert is being created without shipnode as the incident does not contain
						// CacheOrganization, so the code in RRhandler is creating with shipnode of "CacheOrganization"
						// Since "CacheOrganization" is not a valid ship node, a normal user will not be able to
						// see those alert messages. For example, slegg user will not be able to close the alert,
						// so changing the user to ROSSInterface only for closing the alert.
						NWCGLoggerUtil.Log.info("NWCGCheckPendingRR::checkAndTriggerPendingRR, " +
								"Closing alert with user " + NWCGAAConstants.ENV_USER_ID);
				        YFSEnvironment tmpEnv = CommonUtilities.createEnvironment(
				        		NWCGAAConstants.ENV_USER_ID, NWCGAAConstants.ENV_PROG_ID);
						NWCGCloseAlert.closeAlert(tmpEnv, distId, "ResourceRequestReassignNotification", closeReason);
						
						// Set the distribution ID in input XML
						Document docTmp = XMLUtil.getDocument(pendRRXml);
						docTmp.getDocumentElement().setAttribute(NWCGConstants.INC_NOTIF_DIST_ID_ATTR, distId);
						triggerPendingRR(env, docTmp);
						
						// Update all the lines for this distribution id to PROCESSED
						updatePendRRStatus(env, distId, "PROCESSED");
					}
				}
			}
			else {
				NWCGLoggerUtil.Log.info("NWCGCheckPendingRR::checkAndTriggerPendingRR," +
						"There are no order lines in Pending RR table, so not doing anything");
			}
		}
		else {
			NWCGLoggerUtil.Log.info("NWCGCheckPendingRR::checkAndTriggerPendingRR, " +
									"There are no order lines for this incident transfer issue " + inc2incTxIssue);
		}
		NWCGLoggerUtil.Log.info("NWCGCheckPendingRR::checkAndTriggerPendingRR, returning...");
		return null;
	}
	
	/**
	 * This method sets the incident number and year and retrieves 
	 * request numbers from the input xml to this class
	 * @param docIP
	 * @return
	 */
	private Vector<String> setIncKeyAndGetReqs(Document docIP){
		Vector<String> vecReqNos = new Vector<String>();
		Element elmInc2IncXfer = docIP.getDocumentElement();
		NodeList nlXfer = elmInc2IncXfer.getChildNodes();
		for (int i=0; i < nlXfer.getLength(); i++){
			Node tmpNode = nlXfer.item(i);
			if(tmpNode.getNodeType() == Node.ELEMENT_NODE){
				String nodeName = tmpNode.getNodeName();
				if (nodeName.equalsIgnoreCase(NWCGConstants.EXTN)){
					Element elmOrderExtn = (Element) tmpNode;
					incNo = elmOrderExtn.getAttribute(NWCGConstants.EXTN_TO_INCIDENT_NO);
					incYr = elmOrderExtn.getAttribute(NWCGConstants.EXTN_TO_INCIDENT_YR);
				}
				else if (nodeName.equalsIgnoreCase(NWCGConstants.ORDER_LINES)){
					Element elmOLs = (Element) tmpNode;
					NodeList nlOLExtn = elmOLs.getElementsByTagName(NWCGConstants.EXTN);
					for (int extnElms=0; extnElms < nlOLExtn.getLength(); extnElms++){
						Element elmOLExtn = (Element) nlOLExtn.item(extnElms);
						String reqNo = elmOLExtn.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
						vecReqNos.add(reqNo);
					}
				}
			}
		}
		
		return vecReqNos;
	}
	
	/**
	 * This method will check if this request line is present in Pending RR table. If it is
	 * present, then it will update the status to READY_FOR_TRIGGER. It will keep a list
	 * of distribution ids in class variable vector. Below is the use of vector 
	 * Scenario 1: ROSS sends 9 req lines in a single inc2inc transfer from Inc A to Inc B. Now,
	 * ROSS can send inc2inc transfer for all the 9 lines from B to C in a single inc2inc transfer
	 * request or it can send 9 different requests. If either case, we need to trigger resource
	 * reassignment for all the distribution ids (if the other pending RR lines in this table are ready
	 * to be triggered), so we are storing the distribution ids in this vector 
	 * @param env
	 * @param reqNo
	 */
	private void checkAndUpdatePendingRR(YFSEnvironment env, String reqNo){
		try {
			Document docPendRR = XMLUtil.createDocument("NWCGPendingRr");
			Element elmPendRR = docPendRR.getDocumentElement();
			elmPendRR.setAttribute("FromIncidentNo", incNo);
			elmPendRR.setAttribute("FromIncidentYear", incYr);
			elmPendRR.setAttribute("FromRequestNo", reqNo);
			elmPendRR.setAttribute(NWCGConstants.STATUS_ATTR, NWCGConstants.STATUS_NOT_READY_TO_TRIGGER);
			
			NWCGLoggerUtil.Log.info("NWCGCheckPendingRR::checkAndUpdatePendingRR, Input XML : " + 
									XMLUtil.extractStringFromDocument(docPendRR));
			Document docOPPendRRList = CommonUtilities.invokeService(env, NWCGConstants.SVC_GET_PENDING_RR_LIST, docPendRR);
			if (docOPPendRRList != null){
				Element elmPendingRRList = docOPPendRRList.getDocumentElement();
				NodeList nlPendingRR = elmPendingRRList.getElementsByTagName("NWCGPendingRr");
				
				if (nlPendingRR != null && nlPendingRR.getLength()> 0){
					Element elmPendingRR = (Element) nlPendingRR.item(0);
					String distId = elmPendingRR.getAttribute(NWCGConstants.DIST_ID_ATTR);
					String pendRRKey = elmPendingRR.getAttribute("PendRRKey");
					if (vecPendRRDistIds != null && vecPendRRDistIds.size() > 0){
						if (!vecPendRRDistIds.contains(distId)){
							vecPendRRDistIds.add(distId);
						}
					}
					else {
						vecPendRRDistIds.add(distId);
					}
					
					Document docUpdtPendRR = XMLUtil.createDocument("NWCGPendingRr");
					Element elmUpdtPendRR = docUpdtPendRR.getDocumentElement();
					elmUpdtPendRR.setAttribute("PendRRKey", pendRRKey);
					elmUpdtPendRR.setAttribute(NWCGConstants.STATUS_ATTR, NWCGConstants.STATUS_READY_TO_TRIGGER);
					NWCGLoggerUtil.Log.info("NWCGCheckPendingRR::checkAndUpdatePendingRR, Input XML for change order : " + 
											XMLUtil.extractStringFromDocument(docUpdtPendRR));
					
					Document docOPUpdtPendRR = CommonUtilities.invokeService(env, NWCGConstants.SVC_CHANGE_PENDING_RR, docUpdtPendRR);
					if (docOPUpdtPendRR == null){
						NWCGLoggerUtil.Log.info("NWCGCheckPendingRR::checkAndUpdatePendingRR, Unable to " +
								"update Request " + incNo + "/" + incYr + "/" + reqNo + " to " + NWCGConstants.STATUS_READY_TO_TRIGGER);
					}
				}
				else {
					NWCGLoggerUtil.Log.info("NWCGCheckPendingRR::checkAndUpdatePendingRR, nlPendingRR is NULL");
					NWCGLoggerUtil.Log.info("NWCGCheckPendingRR::checkAndUpdatePendingRR, Request " + incNo + "/" + incYr 
										+ "/" + reqNo + " is not present in pending RR");
				}
			}
			else {
				NWCGLoggerUtil.Log.info("NWCGCheckPendingRR::checkAndUpdatePendingRR, Request " + incNo + "/" + incYr 
						+ "/" + reqNo + " is not present in pending RR list");
			}
		} catch (ParserConfigurationException e) {
			NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::checkAndUpdatePendingRR, ParserConfigurationException : " + e.getMessage());
			e.printStackTrace();
		} catch (SAXException e) {
			NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::checkAndUpdatePendingRR, SAXException : " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::checkAndUpdatePendingRR, IOException : " + e.getMessage());
			e.printStackTrace();
		} catch (TransformerException e) {
			NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::checkAndUpdatePendingRR, TransformerException : " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::checkAndUpdatePendingRR, Request " + incNo + "/" + incYr 
					+ "/" + reqNo + " is not present in pending RR list");
			NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::checkAndUpdatePendingRR, Exception : " + e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	/**
	 * This method will get all the entries for that distribution id from pending RR table
	 * If all of them are in ready to be triggered status, then it will return the 
	 * resource reassignment input xml or else it will return null value
	 * @param env
	 * @param distId
	 * @return
	 */
	private String isDistReadyToTrigger(YFSEnvironment env, String distId) {
		String rrXml = null;
		try {
			Document docIPPendRR = XMLUtil.createDocument("NWCGPendingRr");
			Element elmIPPendRR = docIPPendRR.getDocumentElement();
			elmIPPendRR.setAttribute(NWCGConstants.DIST_ID_ATTR, distId);
			System.out.println("NWCGCheckPendingRR::isDistReadyToTrigger, Input XML : " + 
								XMLUtil.extractStringFromDocument(docIPPendRR));
			
			Document docOPPendRRList = CommonUtilities.invokeService(env, NWCGConstants.SVC_GET_PENDING_RR_LIST, docIPPendRR);
			if (docOPPendRRList != null){
				Element elmPendingRRList = docOPPendRRList.getDocumentElement();
				NodeList nlPendingRR = elmPendingRRList.getElementsByTagName("NWCGPendingRr");
				if (nlPendingRR != null && nlPendingRR.getLength() > 0){
					Vector <String> pendRRKeys = new Vector <String>();
					for (int i=0; i < nlPendingRR.getLength(); i++){
						Element elmOPPendRR = (Element)nlPendingRR.item(i);
						String pendRRStatus = elmOPPendRR.getAttribute(NWCGConstants.STATUS_ATTR);
						pendRRKeys.add(elmOPPendRR.getAttribute("PendRRKey"));
						if (pendRRStatus.equalsIgnoreCase(NWCGConstants.STATUS_READY_TO_TRIGGER)){
							rrXml = elmOPPendRR.getAttribute(NWCGConstants.MESSAGE_ATTR);
						}
						else {
							rrXml = null;
							NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::isDistReadyToTrigger, Request " +
									elmOPPendRR.getAttribute(NWCGConstants.FROM_REQUEST_NO) + " is in status " + 
									pendRRStatus + ", so not triggering pending RR");
							break;
						}
					} // end of for loop of all entries for this dist id
					htDistID2PendRRKeys.put(distId, pendRRKeys);
				}
				else {
					NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::isDistReadyToTrigger, ****THIS SHOULD NOT HAPPEN******"); 
					NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::isDistReadyToTrigger, " +
							"Unable to find any entry for distribution ID : " + distId);
				}
			}
		} catch (ParserConfigurationException e) {
			NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::isDistReadyToTrigger, ParserConfigurationException : " + e.getMessage());
			e.printStackTrace();
		} catch (SAXException e) {
			NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::isDistReadyToTrigger, SAXException : " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::isDistReadyToTrigger, IOException : " + e.getMessage());
			e.printStackTrace();
		} catch (TransformerException e) {
			NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::isDistReadyToTrigger, TransformerException : " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::isDistReadyToTrigger, Exception : " + e.getMessage());
			e.printStackTrace();
		}
		if (rrXml == null){
			NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::isDistReadyToTrigger, Distribution ID : " + 
										distId + " is not ready to trigger");
		}
		else {
			NWCGLoggerUtil.Log.info("NWCGCheckPendingRR::isDistReadyToTrigger, Distribution ID : " + 
									distId + " is ready to trigger for pending RR");
		}
		return rrXml;
	}
	
	/**
	 * This method will make pending RR call as if we received it from ROSS for the first time
	 * @param env
	 * @param pendRRIPXml
	 * @return
	 */
	private Document triggerPendingRR(YFSEnvironment env, Document docPendRR){
		NWCGLoggerUtil.Log.info("NWCGCheckPendingRR::triggerPendingRR, Entered");
		NWCGResourceRequestReassignNotificationHandler rrNotifHdlr = new NWCGResourceRequestReassignNotificationHandler();
		try {
			rrNotifHdlr.process(env, docPendRR);
		} catch (NWCGException e) {
			NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::triggerPendingRR, NWCGException Message : " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Update Distribution ID to the passed status. As of now, we are passing only PROCESSED as the status
	 * @param env
	 * @param distId
	 * @param status
	 */
	private void updatePendRRStatus(YFSEnvironment env, String distId, String status){
		try {
			Document docPendRR = XMLUtil.createDocument("NWCGPendingRr");
			Element elmPendRR = docPendRR.getDocumentElement();
			elmPendRR.setAttribute(NWCGConstants.STATUS_ATTR, status);
			Vector<String> vecPendRRKeys = htDistID2PendRRKeys.get(distId);
			for (int i=0; i < vecPendRRKeys.size(); i++){
				String pendRRKey = vecPendRRKeys.get(i);
				elmPendRR.setAttribute("PendRRKey", pendRRKey);
				NWCGLoggerUtil.Log.info("NWCGCheckPendingRR::updatePendRRStatus, Input XML to update status : " + 
									XMLUtil.extractStringFromDocument(docPendRR));
				Document docOPPendRR = CommonUtilities.invokeService(env, NWCGConstants.SVC_CHANGE_PENDING_RR, docPendRR);
				if (docOPPendRR != null){
					NWCGLoggerUtil.Log.info("NWCGCheckPendingRR::updatePendRRStatus, Updated Pending RR Key " + pendRRKey + " to " + status);
				}
				else {
					NWCGLoggerUtil.Log.info("NWCGCheckPendingRR::updatePendRRStatus, " +
							"Unable to update Pending RR Key " + pendRRKey + " to " + status);
				}
							
			}
		} catch (ParserConfigurationException e) {
			NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::updatePendRRStatus, ParserConfigurationException : " + e.getMessage());
			e.printStackTrace();
		} catch (SAXException e) {
			NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::updatePendRRStatus, SAXException : " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::updatePendRRStatus, IOException : " + e.getMessage());
			e.printStackTrace();
		} catch (TransformerException e) {
			NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::updatePendRRStatus, TransformerException : " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			NWCGLoggerUtil.Log.warning("NWCGCheckPendingRR::updatePendRRStatus, Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
}
