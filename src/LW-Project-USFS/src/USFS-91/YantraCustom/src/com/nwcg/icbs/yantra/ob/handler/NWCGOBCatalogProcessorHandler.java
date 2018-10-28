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

import gov.nwcg.services.ross.catalog._1.CreateCatalogItemResp;
import gov.nwcg.services.ross.catalog._1.DeleteCatalogItemResp;
import gov.nwcg.services.ross.catalog._1.UpdateCatalogItemResp;
import gov.nwcg.services.ross.common_types._1.CatalogItemNaturalKeyType;
import gov.nwcg.services.ross.common_types._1.ResponseMessageType;
import gov.nwcg.services.ross.common_types._1.ResponseStatusType;

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
import com.nwcg.icbs.yantra.ob.handler.generic.NWCGOBGenericHandler;
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

public class NWCGOBCatalogProcessorHandler implements NWCGOBProcessorHandler {
	
	private static YFCLogCategory logger = NWCGROSSLogger
			.instance(NWCGOBCatalogProcessorHandler.class);

	public Document process(HashMap msgMap) throws Exception {
		logger.verbose("@@@@@ Entering NWCGOBCatalogProcessorHandler::process @@@@@");
		Object objResp = msgMap.get("xsAny");
		Object actualObj = null;
		boolean isObjRespElmNSImpl = false;

		if (objResp instanceof Element){
			Document newDocForXsAny = XMLUtil.getDocument();
			Node a = newDocForXsAny.importNode((Node) objResp, true);
			newDocForXsAny.appendChild(a);
			
			actualObj = new NWCGJAXBContextWrapper().getObjectFromDocument(newDocForXsAny, new URL(NWCGAAConstants.CATALOG_NAMESPACE));
			isObjRespElmNSImpl = true;
		}
		
		ResponseStatusType respStatusType = null;
		CatalogItemNaturalKeyType itemNaturalKey = null;
		String msgName = (String) msgMap.get(NWCGAAConstants.MDTO_MSGNAME);
		if (msgName.equalsIgnoreCase(NWCGAAConstants.AL_CONST_1)){
			CreateCatalogItemResp createCatalogResp = null;
			if (isObjRespElmNSImpl) {
				createCatalogResp = (CreateCatalogItemResp) actualObj; 
			}
			else {
				createCatalogResp = (CreateCatalogItemResp) objResp;
			}
			respStatusType = createCatalogResp.getResponseStatus();
			itemNaturalKey = createCatalogResp.getCatalogItemKey();
		}
		else if (msgName.equalsIgnoreCase(NWCGAAConstants.AL_CONST_2)){
			DeleteCatalogItemResp deleteCatalogResp = null;
			if (isObjRespElmNSImpl) {
				deleteCatalogResp = (DeleteCatalogItemResp) actualObj; 
			}
			else {
				deleteCatalogResp = (DeleteCatalogItemResp) objResp;
			}
			respStatusType = deleteCatalogResp.getResponseStatus();
			itemNaturalKey = deleteCatalogResp.getCatalogItemKey();
		}
		else if (msgName.equalsIgnoreCase(NWCGAAConstants.AL_CONST_3)){
			UpdateCatalogItemResp updtCatalogResp = null;
			if (isObjRespElmNSImpl) {
				updtCatalogResp = (UpdateCatalogItemResp) actualObj; 
			}
			else {
				updtCatalogResp = (UpdateCatalogItemResp) objResp;
			}
			respStatusType = updtCatalogResp.getResponseStatus();
			itemNaturalKey = updtCatalogResp.getCatalogItemKey();
		}
		else {
		}
		
		if (respStatusType != null) {
			if (respStatusType.getReturnCode() == -1){
				HashMap hMap = new HashMap();
				String distId = (String) msgMap.get("Distribution ID");
				hMap.put("Distribution ID", distId);
				hMap.put("ReturnCode", "-1");
				hMap.put("ExceptionType", msgName);
				hMap.put("AlertType", msgName);
				
				YFSEnvironment env = NWCGWebServiceUtils.getEnvironment();				
				// Set the user id who sent the Update request originally
				NWCGMessageStore nwcgMsgStore = NWCGMessageStore.getMessageStore();
				String reqCreatedByUserId = nwcgMsgStore.getUserOfOBMessageForDistID(env, distId);
				if (reqCreatedByUserId != null){
					hMap.put(NWCGConstants.ALERT_REQ_CREATED_BY, reqCreatedByUserId);
				}
				
				String itemId = "";
				String entityValue = (String) msgMap.get("EntityValue");
				if (entityValue != null && entityValue.length() > 3){
					String separator = " - ";
					int index = entityValue.indexOf(separator);
					itemId = entityValue.substring(0, index);
					hMap.put("ItemID", itemId);
					hMap.put("UOM", entityValue.substring(index + separator.length(), entityValue.length()));
				}
				
				if (itemNaturalKey != null){
					if ((itemId.length() < 1) && itemNaturalKey.isSetCatalogItemCode()){
						itemId = itemNaturalKey.getCatalogItemCode();
						hMap.put("CatalogItemCode", itemId);
						hMap.put("ItemID", itemId);
					}
					else if ((itemId.length() < 1) && itemNaturalKey.isSetCatalogItemName()){
						itemId = itemNaturalKey.getCatalogItemName();
						hMap.put("CatalogItemName", itemId);
						hMap.put("ItemID", itemId);
					}
					
					hMap.put("CatalogType", itemNaturalKey.getCatalogType().value());
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
				CommonUtilities.raiseAlert(env, NWCGAAConstants.QUEUEID_CATALOG_FAILURE, NWCGAAConstants.ALERT_INVALIDMSG + itemId, null, null,hMap);
			}	
		}
		logger.verbose("@@@@@ Exiting NWCGOBCatalogProcessorHandler::process @@@@@");
		return null;
	}
}