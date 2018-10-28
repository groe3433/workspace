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

package com.nwcg.icbs.yantra.ob.handler;

import gov.nwcg.services.ross.common_types._1.IncidentNumberType;
import gov.nwcg.services.ross.common_types._1.ResponseMessageType;
import gov.nwcg.services.ross.common_types._1.ResponseStatusType;
import gov.nwcg.services.ross.resource_order._1.RegisterIncidentInterestResp;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.commCore.NWCGMessageStore;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.util.NWCGJAXBContextWrapper;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServiceUtils;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

/**
 * This class is used as handler for both Sync and Async Register call.
 * In case of async, response object is stored in xsAny and in case of
 * sync, response object is stored in MDTO_MSGBODY
 * @author sgunda
 */
public class NWCGOBRegisterIncidentProcessorHandler implements NWCGOBProcessorHandler {
	
	private static YFCLogCategory logger = NWCGROSSLogger
			.instance(NWCGOBRegisterIncidentProcessorHandler.class);
	
	private String incidentNo = "";
	private String incidentYr = "";
	private String strShipNode = "";
	private boolean isRegIncident = true;

	public Document process(HashMap msgMap) throws Exception {	
		logger.verbose("@@@@@ Entering NWCGOBRegisterIncidentProcessorHandler::process @@@@@");
		Document opDoc = null;
		YFSEnvironment env = NWCGWebServiceUtils.getEnvironment();
		String distId = (String) msgMap.get(NWCGAAConstants.MDTO_DISTID);
		String msgName = (String) msgMap.get(NWCGAAConstants.MDTO_MSGNAME);

		Object actualObj = null;
		Object objResp = msgMap.get("xsAny");
		boolean bObjRespIsElmType = false;
		
		if (objResp instanceof Element){
			Document docXsAny = XMLUtil.getDocument();
			Node nodeXsAny = docXsAny.importNode((Node) objResp, true);
			docXsAny.appendChild(nodeXsAny);
			actualObj = new NWCGJAXBContextWrapper().getObjectFromDocument(
									docXsAny, new URL(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
			bObjRespIsElmType = true;
		}

		RegisterIncidentInterestResp riiResp = null;
		
		// In case of async, response object is stored in xsAny and in case of
		// sync, response object is stored in MDTO_MSGBODY
		if (objResp == null){
			riiResp = (RegisterIncidentInterestResp) msgMap.get(NWCGAAConstants.MDTO_MSGBODY);
		}
		else if (bObjRespIsElmType){
			riiResp = (RegisterIncidentInterestResp) actualObj;
		}
		else {
			riiResp = (RegisterIncidentInterestResp) objResp;
		}
		
		
		//RegisterIncidentInterestResp riiResp = (RegisterIncidentInterestResp) msgMap.get(NWCGAAConstants.MDTO_MSGBODY);
		ResponseStatusType respStatType = riiResp.getResponseStatus();
		if (respStatType.getReturnCode() == -1){
			
			//<Gaurav> Get inc No if present
			HashMap<String, String> hm = new HashMap<String, String>();
			String entityKey = (String) msgMap.get(NWCGAAConstants.MDTO_ENTKEY);
			if (entityKey != null && entityKey.length() > 1)
			{
				hm = CommonUtilities.getShipNodeIncidentNoandYearFromKey(env, entityKey);
			}
			else {
				hm = CommonUtilities.getShipNodeIncidentNoandYearFromDistID(env, distId);
			}
			
			if (hm != null && hm.size() > 1){
				incidentNo = hm.get(NWCGConstants.INCIDENT_NO);
				incidentYr = hm.get(NWCGConstants.INCIDENT_YEAR);
				strShipNode = hm.get(NWCGConstants.PRIMARY_CACHE_ID);
			}
			// generate an alert
			HashMap hMap = new HashMap();
			hMap.put("Distribution ID", distId);
			hMap.put("ReturnCode", "-1");
			
			hMap.put("ExceptionType", msgName);
			hMap.put("AlertType", msgName);
			//<Gaurav> populate incident no and year in alert
			
			if((incidentNo != null && !incidentNo.equals("")) && (incidentYr != null && !incidentYr.equals("")))
			{
				hMap.put("IncidentNo", incidentNo);
				hMap.put("Year", incidentYr);
			}
			
			// Set the user id who sent the Update request originally
			NWCGMessageStore nwcgMsgStore = NWCGMessageStore.getMessageStore();
			String reqCreatedByUserId = nwcgMsgStore.getUserOfOBMessageForDistID(env, distId);
			if (reqCreatedByUserId != null){
				hMap.put(NWCGConstants.ALERT_REQ_CREATED_BY, reqCreatedByUserId);
			}
			
			if (respStatType.isSetResponseMessage()){
				ArrayList <ResponseMessageType> listRespMsgType = (ArrayList <ResponseMessageType>) respStatType.getResponseMessage();
				// We are displaying only one code, severity and description in alert console.
				// So, taking the 0th element only.
				if (listRespMsgType != null && listRespMsgType.size() > 0){
					Iterator respMsgTypeItr = listRespMsgType.iterator();
					if (respMsgTypeItr.hasNext()){
						ResponseMessageType respMsgType = (ResponseMessageType) respMsgTypeItr.next();
						hMap.put("Code", respMsgType.getCode());
						hMap.put("Severity", respMsgType.getSeverity());
						hMap.put("Description", respMsgType.getDescription());
					}
				}
			}
			String userIdToAssign = "";
			if(strShipNode != null && !strShipNode.equals("")){
				hMap.put(NWCGConstants.ALERT_SHIPNODE_KEY, strShipNode);
				userIdToAssign = CommonUtilities.getAdminUserForCache(env, strShipNode);
			}

			CommonUtilities.raiseAlertAndAssigntoUser(env, NWCGAAConstants.QUEUEID_INCIDENT_FAILURE, "ROSS Incident Synchronization Failed", userIdToAssign, null, hMap);

			// This opDoc is used on the JSP page for synchronous register incident interest call
			Document tempDoc = null;
			opDoc = new NWCGJAXBContextWrapper().getDocumentFromObject(riiResp, tempDoc, msgName);
		}
		else {
			if (riiResp.isSetIncident()){
				setIncidentKey(riiResp.getIncident().getIncidentKey().getNaturalIncidentKey());
				
				// DO THE BELOW LOGIC, IF IT IS FOR REGISTER. DO NOT DO ANYTHING IF IT IS DEREGISTER
				// If IncidentDetails element is present, then it is a register incident call or else it is a deregister call
				isRegIncident = riiResp.getIncident().isSetIncidentDetails();
				
				if (isRegIncident){
					Document inDoc = null ;
					inDoc = new NWCGJAXBContextWrapper().getDocumentFromObject(riiResp, inDoc, new URL (NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
					opDoc = CommonUtilities.invokeService(env, NWCGAAConstants.REGISTER_INCIDENT_INTEREST_SERVICE, inDoc);
					Element opDocElm = opDoc.getDocumentElement();
					opDocElm.setAttribute(NWCGAAConstants.ENTITY_KEY, opDocElm.getAttribute(NWCGAAConstants.INCIDENT_KEY));
				}
				unlockIncidentByNo(incidentNo, incidentYr, msgName);
			}
			else {
				// unlock the incident using distribution id. This scenario happens only for
				// deregister. For register, LM is supposed to send the incident details as part 
				// of positive response.
				String entityKey = (String) msgMap.get("EntityKey");
				if (entityKey != null && entityKey.length() > 1){
					unlockIncidentByKey(entityKey, msgName);
				}
				else {
					getIncidentKeyAndUnlockIncident(env, distId, msgName);
				}
			}
		}
		logger.verbose("@@@@@ Exiting NWCGOBRegisterIncidentProcessorHandler::process @@@@@");
		return opDoc;
	}

	private void getIncidentKeyAndUnlockIncident(YFSEnvironment env, String distId, String msgName) throws Exception {
		try {
			Document msgStoreIPDoc = XMLUtil.createDocument("NWCGOutboundMessage");
			Element msgStoreIPDocElm = msgStoreIPDoc.getDocumentElement();
			msgStoreIPDocElm.setAttribute(NWCGAAConstants.DIST_ID_ATTR, distId);
			Document msgStoreOPListDoc = CommonUtilities.invokeService(env, NWCGAAConstants.GET_OB_MESSAGE_LIST_SERVICE, msgStoreIPDoc); 
			if (msgStoreOPListDoc != null){
				NodeList obMsgNL = msgStoreOPListDoc.getElementsByTagName("NWCGOutboundMessage");
				if (obMsgNL != null && obMsgNL.getLength() > 0){
					Element obMsgStoreElm = (Element) obMsgNL.item(0);
					String entityKey = obMsgStoreElm.getAttribute(NWCGAAConstants.ENTITY_KEY);
					if (entityKey != null && entityKey.length() > 2){
						unlockIncidentByKey(entityKey, msgName);
					}
					else {
					}
				}
				else {
				}
			}
			else {
			}
		}
		catch(ParserConfigurationException pce){
			//logger.printStackTrace(pce);
		}
	}
	
	/**
	 * This method sets the incident key
	 * @param incidentType
	 */
	private void setIncidentKey(IncidentNumberType incidentType) {
		incidentYr = incidentType.getYearCreated();
		int seqNum = incidentType.getSequenceNumber();
		String prefix = incidentType.getHostID().getUnitIDPrefix();
		String suffix = incidentType.getHostID().getUnitIDSuffix();
		StringBuffer sb = new StringBuffer();
		sb = sb.append(prefix).append("-").append(suffix).append("-");
		String strSeqNum = (new Integer(seqNum)).toString();
		for (int i=0; i < (6 - strSeqNum.length()); i++){
			sb.append("0");
		}
		sb.append(strSeqNum);
		incidentNo = sb.toString();
	}

	/**
	 * This method unlocks the incident based on incident number and incident year
	 * @param incidentNumber
	 * @param year
	 */
	private void unlockIncidentByNo(String incidentNumber, String year, String msgName) {		
		try {
			Document xmlDoc = XMLUtil.createDocument("NWCGIncidentOrder");
			Element elm = xmlDoc.getDocumentElement();
			elm.setAttribute("IncidentNo", incidentNumber);
			elm.setAttribute("Year", year);
			elm.setAttribute("IncidentLocked", "N");
			elm.setAttribute("ModificationDesc", "Received " + msgName);
			elm.setAttribute("ModificationCode", "Response from ROSS");
			if (!isRegIncident){
				elm.setAttribute("RegisterInterestInROSS", "N");
			}
			NWCGWebServiceUtils.invokeServiceMethod("NWCGUpdateIncidentOrderService", xmlDoc);
		} catch (ParserConfigurationException pce) {
			//logger.printStackTrace(pce);
		} catch (Exception e2) {
			//logger.printStackTrace(e2);
		}
	}

	/**
	 * This method unlocks the incident based on incident key
	 * @param strIncidentKey
	 */
	private void unlockIncidentByKey(String incidentKey, String msgName) {		
		try {
			Document xmlDoc = XMLUtil.createDocument("NWCGIncidentOrder");
			Element elm = xmlDoc.getDocumentElement();
			elm.setAttribute("IncidentKey", incidentKey);
			elm.setAttribute("IncidentLocked", "N");
			if (!isRegIncident){
				elm.setAttribute("RegisterInterestInROSS", "N");
			}
			elm.setAttribute("ModificationDesc", "Received " + msgName);
			elm.setAttribute("ModificationCode", "Response from ROSS");
		} catch (ParserConfigurationException pce) {
			//logger.printStackTrace(pce);
		} catch (Exception e2) {
			//logger.printStackTrace(e2);
		}
	}
}