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

import gov.nwcg.services.ross.common_types._1.ResponseMessageType;
import gov.nwcg.services.ross.common_types._1.ResponseStatusType;
import gov.nwcg.services.ross.resource_order._1.UpdateNFESResourceRequestResp;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.commCore.NWCGMessageStore;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.util.NWCGJAXBContextWrapper;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServiceUtils;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGOBUpdateNFESResourceRequestHandler implements NWCGOBProcessorHandler {
	
	/**
	 * This method will raise an alert in NWCG_ISSUE_SUCCESS or NWCG_ISSUE_FAILURE based
	 * on return code.
	 */
	public Document process(HashMap<Object, Object> msgMap) throws Exception {
		System.out.println("@@@@@ Entering NWCGOBUpdateNFESResourceRequestHandler::process @@@@@");
		
		YFSEnvironment env = NWCGWebServiceUtils.getEnvironment();
		String distId = (String) msgMap.get(NWCGAAConstants.MDTO_DISTID);
		String msgName = (String) msgMap.get(NWCGAAConstants.MDTO_MSGNAME);
		String orderNo = (String) msgMap.get(NWCGAAConstants.MDTO_ENTVALUE);
		Object actualObj = null;
		Object objResp = msgMap.get("xsAny");
		
		UpdateNFESResourceRequestResp updtNFESResReqResp = null;
		if (objResp instanceof Element){
			System.out.println("@@@@@ Object is an Element...");
			Document docXsAny = XMLUtil.getDocument();
			Node nodeXsAny = docXsAny.importNode((Node) objResp, true);
			docXsAny.appendChild(nodeXsAny);
			actualObj = new NWCGJAXBContextWrapper().getObjectFromDocument(docXsAny, new URL(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
			updtNFESResReqResp = (UpdateNFESResourceRequestResp) actualObj;
			System.out.println("@@@@@ Element Object : " + XMLUtil.extractStringFromDocument(docXsAny));
		}
		else {
			updtNFESResReqResp = (UpdateNFESResourceRequestResp) objResp;
		}
		
		ResponseStatusType respStatType = updtNFESResReqResp.getResponseStatus();
		HashMap<String, String> hMap = new HashMap<String, String>();
		hMap.put("DistributionID", distId);
		hMap.put("ExceptionType", msgName);
		hMap.put("AlertType", msgName);
		hMap.put(NWCGAAConstants.NAME, msgName);
		NWCGMessageStore nwcgMsgStore = NWCGMessageStore.getMessageStore();
		Document docSentReq = nwcgMsgStore.getOBMessageForDistID(env, distId);
		Element elmSentReq = docSentReq.getDocumentElement();
		String reqCreatedByUserId = elmSentReq.getAttribute("NWCGUSERID");
		if (orderNo == null || orderNo.trim().length() < 2){
			orderNo = elmSentReq.getAttribute(NWCGAAConstants.ENTITY_VALUE);
		}
		hMap.put("Order No", orderNo);
		
		if (respStatType.getReturnCode() == -1){
			//	Fetching ship node from Order Number
			String cacheId = CommonUtilities.getShipNodeFromOrderNo(env, orderNo);
			//	adding ship node to alert references
			hMap.put(NWCGConstants.ALERT_SHIPNODE_KEY, cacheId);
			// generate an alert in NWCG_ISSUE_FAILURE
			hMap.put("ReturnCode", "-1");
			// Set the user id who sent the Update request originally
			if (reqCreatedByUserId != null){
				hMap.put(NWCGConstants.ALERT_REQ_CREATED_BY, reqCreatedByUserId);
			}

			System.out.println("@@@@@ Getting code, severity and description...");
			if (respStatType.isSetResponseMessage()){
				ArrayList <ResponseMessageType> listRespMsgType = (ArrayList <ResponseMessageType>) respStatType.getResponseMessage();
				// We are displaying only one code, severity and description in alert console. So, taking the 0th element only.
				if (listRespMsgType != null && listRespMsgType.size() > 0){
					System.out.println("@@@@@ Error list : " + listRespMsgType.size());
					Iterator respMsgTypeItr = listRespMsgType.iterator();
					if (respMsgTypeItr.hasNext()){
						ResponseMessageType respMsgType = (ResponseMessageType) respMsgTypeItr.next();
						hMap.put("Code", respMsgType.getCode());
						hMap.put("Severity", respMsgType.getSeverity());
						hMap.put("Description", respMsgType.getDescription());
					}
				}
			}

			String userIdToAssign = CommonUtilities.getAdminUserForCache(env, cacheId);
			
			System.out.println("@@@@@ NWCGOBUpdateNFESResourceRequestHandler::process, Obtained code, severity and description");
			CommonUtilities.raiseAlertAndAssigntoUser(env, NWCGAAConstants.QUEUEID_ISSUE_FAILURE, "Update NFES Resource Request failed", userIdToAssign, null, hMap);
		}
		
		System.out.println("@@@@@ Exiting NWCGOBUpdateNFESResourceRequestHandler::process @@@@@");
		return null;
	}
}