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
import gov.nwcg.services.ross.resource_order._1.SetIncidentActivationResp;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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

public class NWCGOBIncidentProcessorHandler implements NWCGOBProcessorHandler {
	
	private static YFCLogCategory logger = NWCGROSSLogger
			.instance(NWCGOBIncidentProcessorHandler.class);
	
	private String incidentNo = "";
	private String incidentYr = "";

	/**
	 * 
	 */
	public Document process(HashMap msgMap) throws Exception {
		logger.verbose("@@@@@ Entering NWCGOBIncidentProcessorHandler::process @@@@@");
		YFSEnvironment env = NWCGWebServiceUtils.getEnvironment();
		Object actualObj = null;
		Object objResp = msgMap.get("xsAny");
		boolean bObjRespIsElmType = false;
		
		if (objResp instanceof Element){
			Document docXsAny = XMLUtil.getDocument();
			Node nodeXsAny = docXsAny.importNode((Node) objResp, true);
			docXsAny.appendChild(nodeXsAny);
			actualObj = new NWCGJAXBContextWrapper().getObjectFromDocument(docXsAny, new URL(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
			bObjRespIsElmType = true;
		}

		SetIncidentActivationResp incidentActivationResp = null;
		if (bObjRespIsElmType){
			incidentActivationResp = (SetIncidentActivationResp) actualObj;
		}
		else {
			incidentActivationResp = (SetIncidentActivationResp) objResp;
		}
		if (incidentActivationResp.isSetIncidentKey()){
			setIncidentKey(incidentActivationResp.getIncidentKey().getNaturalIncidentKey());
		}
		
		String msgName = (String) msgMap.get(NWCGAAConstants.MDTO_MSGNAME);
		ResponseStatusType respStatusType = incidentActivationResp.getResponseStatus();
		if (respStatusType.getReturnCode() == -1){
			String distId = (String) msgMap.get(NWCGAAConstants.MDTO_DISTID);
			HashMap hMap = new HashMap();
			hMap.put("ExceptionType", msgName);
			hMap.put("AlertType", msgName);
			hMap.put("Distribution ID", distId);
			hMap.put("ReturnCode", "-1");
			
			// Set the user id who sent the Update request originally
			NWCGMessageStore nwcgMsgStore = NWCGMessageStore.getMessageStore();
			String reqCreatedByUserId = nwcgMsgStore.getUserOfOBMessageForDistID(env, distId);
			if (reqCreatedByUserId != null){
				hMap.put(NWCGConstants.ALERT_REQ_CREATED_BY, reqCreatedByUserId);
			}
			
			if (respStatusType.isSetResponseMessage()){
				ArrayList <ResponseMessageType> listRespMsgType = (ArrayList <ResponseMessageType>) respStatusType.getResponseMessage();
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
			
			HashMap<String, String> hm = new HashMap<String, String>();
			String shipNode = "";
			String entityKey = (String) msgMap.get(NWCGAAConstants.ENTITY_KEY);
			if (incidentNo != null && incidentNo.length() > 2){
				shipNode = CommonUtilities.getShipNodeFromIncident(env, incidentNo, incidentYr);
			}
			else if (entityKey != null && entityKey.length() > 2){
				hm = CommonUtilities.getShipNodeIncidentNoandYearFromKey(env, entityKey);
				incidentNo = hm.get(NWCGConstants.INCIDENT_NO);
				incidentYr = hm.get(NWCGConstants.INCIDENT_YEAR);
				shipNode = hm.get(NWCGConstants.PRIMARY_CACHE_ID);
			}
			else {
				hm = CommonUtilities.getShipNodeIncidentNoandYearFromDistID(env, distId); 
				incidentNo = hm.get(NWCGConstants.INCIDENT_NO);
				incidentYr = hm.get(NWCGConstants.INCIDENT_YEAR);
				shipNode = hm.get(NWCGConstants.PRIMARY_CACHE_ID);
			}
						
			hMap.put("Incident No", incidentNo);
			hMap.put("Incident Year", incidentYr);
			hMap.put(NWCGConstants.ALERT_SHIPNODE_KEY, shipNode);
			String userIdToAssign = "";
			if (shipNode != null){
				userIdToAssign = CommonUtilities.getAdminUserForCache(env, shipNode);
			}
			
			CommonUtilities.raiseAlertAndAssigntoUser(env, NWCGAAConstants.QUEUEID_INCIDENT_FAILURE, "ROSS Incident Synchronization Failied - Active or Inactive", userIdToAssign, null, hMap);
		}
		else {
			unlockIncident(incidentNo, incidentYr, msgName);
		}
		logger.verbose("@@@@@ Exiting NWCGOBIncidentProcessorHandler::process @@@@@");
		return null;
	}


	/**
	 * This method unlocks the incident based on incident key
	 * @param strIncidentKey
	 */
	private void unlockIncident(String incidentNumber, String year, String msgName) {		
		try {
			Document xmlDoc = XMLUtil.createDocument("NWCGIncidentOrder");
			Element elm = xmlDoc.getDocumentElement();
			elm.setAttribute("IncidentNo", incidentNumber);
			elm.setAttribute("Year", year);
			elm.setAttribute("IncidentLocked", "N");
			elm.setAttribute("LockReason", " ");
			elm.setAttribute("ModificationDesc", "Received " + msgName);
			elm.setAttribute("ModificationCode", "Response from ROSS");
			NWCGWebServiceUtils.invokeServiceMethod("NWCGUpdateIncidentOrderService", xmlDoc);
		} catch (ParserConfigurationException pce) {
			//logger.printStackTrace(pce);
		} catch (Exception e2) {
			//logger.printStackTrace(e2);
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
		for (int i=0; i < (6 - strSeqNum.length()); i++) {
			sb.append("0");
		}
		sb.append(strSeqNum);
		incidentNo = sb.toString();
	}
}