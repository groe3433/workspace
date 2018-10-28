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

package com.nwcg.icbs.yantra.commCore;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGMessageStore {
	
	private static final String className = NWCGMessageStore.class.getName();

	public String storeMessage(String dist_id, String type, String msg, String msg_type , String status, 
			String systemName, String serviceName) throws Exception {
		final String methodName = "storeMessage";
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "ENTRY: "+className+"."+methodName);
		NWCGLoggerUtil.Log.finer("BEGIN TIMER: "+methodName);

		String key = null;
		
		String msg_str= null;
		String sdfServiceName = NWCGConstants.EMPTY_STRING;
		Document OBDoc = null;
		Document IBDoc = null;
		
		if(type.equals(NWCGAAConstants.MESSAGE_DIR_TYPE_OB)){
			 msg_str = "<NWCGOutboundMessage Createprogid=\"\" Createts=\"\" Createuserid=\"\" " +
			    "DistributionID=\"\" LatestInbound=\"\" Lockid=\"\" Message=\"\" "+
		        "MessageKey=\"\" MessageStatus=\"\" MessageType=\"\" Modifyprogid=\"\" " +
		        "EntityKey=\"\" EntityName=\"\" EntityValue=\"\" " +
		        "Modifyts=\"\" Modifyuserid=\"\" SystemName=\"\"/>";//NWCGAAUtil.readText(NWCGProperties.getProperty("SDF_OUTBOUND_MESSAGE_SAMPLE_FILENAME"));
			 NWCGLoggerUtil.Log.finest("msg_str OB:"+msg_str);
			 OBDoc = XMLUtil.getDocument(msg_str);
			 sdfServiceName = NWCGAAConstants.SDF_CREATE_OB_MESSAGE_NAME;
		}
		
		if(type.equals(NWCGAAConstants.MESSAGE_DIR_TYPE_IB)){
			 msg_str = "<NWCGInboundMessage Createprogid=\"\" Createts=\"\" Createuserid=\"\" " +
			    "DistributionID=\"\" LatestInbound=\"\" Lockid=\"\" Message=\"\" "+ 
			    "MessageKey=\"\" MessageStatus=\"\" MessageType=\"\" Modifyprogid=\"\" " +
			    "Modifyts=\"\" Modifyuserid=\"\" SystemName=\"\"/>";//NWCGAAUtil.readText(NWCGProperties.getProperty("SDF_INBOUND_MESSAGE_SAMPLE_FILENAME"));
			 NWCGLoggerUtil.Log.finest("msg_str IB:"+msg_str);
			 IBDoc = XMLUtil.getDocument(msg_str);
			 sdfServiceName = NWCGAAConstants.SDF_CREATE_IB_MESSAGE_NAME;
		}	
		
        YFSEnvironment env = CommonUtilities.createEnvironment(NWCGAAConstants.ENV_USER_ID, NWCGAAConstants.ENV_PROG_ID);
		NWCGLoggerUtil.Log.finest("Created YFSEnvironment");

        Document doc_rt = null;
        
        String sdf_xml_name = "(NONE)";
        
    	if(type.equals(NWCGAAConstants.MESSAGE_DIR_TYPE_OB)){
    		Element elem = OBDoc.getDocumentElement();
    		 elem.setAttribute(NWCGAAConstants.DIST_ID_ATTR,dist_id);
    		 elem.setAttribute(NWCGAAConstants.MESSAGE,msg.trim());
    		 elem.setAttribute(NWCGAAConstants.MESSAGE_TYPE,msg_type);
    		 elem.setAttribute(NWCGAAConstants.MESSAGE_STATUS,status);
    		 elem.setAttribute(NWCGAAConstants.SYSTEM_NAME_ATTR,systemName);
    		 elem.setAttribute(NWCGAAConstants.MESSAGE_NAME,serviceName);
    		 			
			 NWCGLoggerUtil.Log.finest("Retrieving entity data");
    		 Map map = retrieveEntityInfo(msg, serviceName);
    		 elem.setAttribute(NWCGAAConstants.ENTITY_KEY, (String)map.get(NWCGAAConstants.ENTITY_KEY));
    		 elem.setAttribute(NWCGAAConstants.ENTITY_NAME, (String) map.get(NWCGAAConstants.ENTITY_NAME));
    		 elem.setAttribute(NWCGAAConstants.ENTITY_VALUE, (String) map.get(NWCGAAConstants.ENTITY_VALUE));
			 NWCGLoggerUtil.Log.finest("Retrieving entity data");
    		 
    		 doc_rt = CommonUtilities.invokeService(env,sdfServiceName,OBDoc);
			 sdf_xml_name = NWCGAAConstants.NWCG_OUTBOUND_MSG_ELM;
    	}
		
		if(type.equals(NWCGAAConstants.MESSAGE_DIR_TYPE_IB)){
			Element elem = IBDoc.getDocumentElement();
			elem.setAttribute(NWCGAAConstants.DIST_ID_ATTR,dist_id);
			elem.setAttribute(NWCGAAConstants.MESSAGE,msg.trim());
			elem.setAttribute(NWCGAAConstants.MESSAGE_TYPE,msg_type);
			elem.setAttribute(NWCGAAConstants.MESSAGE_STATUS,status);
			elem.setAttribute(NWCGAAConstants.SYSTEM_NAME_ATTR,systemName);
			elem.setAttribute(NWCGAAConstants.MESSAGE_NAME,serviceName);
	   		 doc_rt = CommonUtilities.invokeService(env,sdfServiceName,IBDoc);
			 sdf_xml_name = NWCGAAConstants.NWCG_INBOUND_MSG_ELM;
		}
        NWCGLoggerUtil.Log.finest("doc_rt::"+XMLUtil.extractStringFromDocument(doc_rt));
	
        Element elem = (Element) doc_rt.getElementsByTagName(sdf_xml_name).item(0);
        key = elem.getAttribute(NWCGAAConstants.MESSAGE_KEY);
        
        NWCGLoggerUtil.Log.finest("MessageKey:"+key);
		NWCGLoggerUtil.Log.finer("  END TIMER: "+methodName);
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "RETURN: "+className+"."+methodName);
        return key;			
	}
	
	/**
	 * This method gets the entity information based on the type of the service and sets it in a hashtable 
	 * @param msg
	 * @param serviceName
	 * @return
	 * @throws Exception
	 */
	public Map retrieveEntityInfo (String msg, String serviceName) throws Exception{
		Document doc = XMLUtil.getDocument(msg);
		Map map = new HashMap();
		if (serviceName.equalsIgnoreCase(NWCGAAConstants.XPATH_ACTIVATE_INCIDENT_INTEREST) ||
			serviceName.equalsIgnoreCase(NWCGAAConstants.XPATH_DEACTIVATE_INCIDENT_INTEREST)){
			String incidentKey = XPathUtil.getString(doc, NWCGAAConstants.XPATH_ACTIVATE_INCIDENT_INTEREST_KEY);
			if (incidentKey == null){
				incidentKey = NWCGConstants.EMPTY_STRING;
			}
			map.put(NWCGAAConstants.ENTITY_KEY, incidentKey);
			
			String incidentId = XPathUtil.getString(doc, NWCGAAConstants.XPATH_ACTIVE_INCIDENT_INTEREST_UNITID_PREFIX);
			if (incidentId == null){
				incidentId = NWCGConstants.EMPTY_STRING;
			}
			else {
				StringBuffer sb = new StringBuffer(incidentId);
				sb.append(NWCGConstants.DASH).append(XPathUtil.getString(doc, NWCGAAConstants.XPATH_ACTIVE_INCIDENT_INTEREST_UNITID_SUFFIX));
				sb.append(NWCGConstants.DASH).append(XPathUtil.getString(doc, NWCGAAConstants.XPATH_ACTIVATE_INCIDENT_INTEREST_SEQUENCE));
				sb.append(NWCGConstants.DASH).append(XPathUtil.getString(doc, NWCGAAConstants.XPATH_ACTIVATE_INCIDENT_INTEREST_YEAR));
				incidentId = sb.toString();
			}
			
			map.put(NWCGAAConstants.ENTITY_VALUE, incidentId);
			map.put(NWCGAAConstants.ENTITY_NAME, NWCGAAConstants.INCIDENT_SERVICE_GROUP_NAME);
		}
		else if (serviceName.equalsIgnoreCase(NWCGAAConstants.XPATH_REGISTER_INCIDENT_INTEREST) ||
				 serviceName.equalsIgnoreCase(NWCGAAConstants.XPATH_DEREGISTER_INCIDENT_INTEREST)){
			String incidentKey = XPathUtil.getString(doc, NWCGAAConstants.XPATH_REGISTER_INCIDENT_INTEREST_KEY);
			if (incidentKey == null){
				incidentKey = NWCGConstants.EMPTY_STRING;
			}
			map.put(NWCGAAConstants.ENTITY_KEY, incidentKey);
			
			String incidentId = XPathUtil.getString(doc, NWCGAAConstants.XPATH_REGISTER_INCIDENT_INTEREST_UNITID_PREFIX);
			if (incidentId == null){
				incidentId = NWCGConstants.EMPTY_STRING;
			}
			else {
				StringBuffer sb = new StringBuffer(incidentId);
				sb.append(NWCGConstants.DASH).append(XPathUtil.getString(doc, NWCGAAConstants.XPATH_REGISTER_INCIDENT_INTEREST_UNITID_SUFFIX));
				sb.append(NWCGConstants.DASH).append(XPathUtil.getString(doc, NWCGAAConstants.XPATH_REGISTER_INCIDENT_INTEREST_SEQUENCE));
				sb.append(NWCGConstants.DASH).append(XPathUtil.getString(doc, NWCGAAConstants.XPATH_REGISTER_INCIDENT_INTEREST_YEAR));
				incidentId = sb.toString();
			}
			
			map.put(NWCGAAConstants.ENTITY_VALUE, incidentId);
			map.put(NWCGAAConstants.ENTITY_NAME, NWCGAAConstants.INCIDENT_SERVICE_GROUP_NAME);
		}
		else if (serviceName.equalsIgnoreCase(NWCGAAConstants.XPATH_GET_INCIDENT_IK)){
			String incidentKey = XPathUtil.getString(doc, NWCGAAConstants.XPATH_GET_INCIDENT_IK_INCIDENT_KEY);
			if (incidentKey == null){
				incidentKey = NWCGConstants.EMPTY_STRING;
			}
			map.put(NWCGAAConstants.ENTITY_KEY, incidentKey);
			
			String incidentId = XPathUtil.getString(doc, NWCGAAConstants.XPATH_GET_INCIDENT_IK_UNIT_ID_PREFIX);
			if (incidentId == null){
				incidentId = NWCGConstants.EMPTY_STRING;
			}
			else {
				StringBuffer sb = new StringBuffer(incidentId);
				sb.append(NWCGConstants.DASH).append(XPathUtil.getString(doc, NWCGAAConstants.XPATH_GET_INCIDENT_IK_UNIT_ID_SUFFIX));
				sb.append(NWCGConstants.DASH).append(XPathUtil.getString(doc, NWCGAAConstants.XPATH_GET_INCIDENT_IK_SEQNO));
				sb.append(NWCGConstants.DASH).append(XPathUtil.getString(doc, NWCGAAConstants.XPATH_GET_INCIDENT_IK_YEAR_CREATED));
				incidentId = sb.toString();
			}
			
			map.put(NWCGAAConstants.ENTITY_VALUE, incidentId);
			map.put(NWCGAAConstants.ENTITY_NAME, NWCGAAConstants.INCIDENT_SERVICE_GROUP_NAME);
		}
		else if (serviceName.equalsIgnoreCase(NWCGAAConstants.CREATE_CATALOG_ITEM_REQ)){
			String catalogKey = XPathUtil.getString(doc, NWCGAAConstants.XPATH_CREATE_CATALOG_ITEM_KEY);
			if (catalogKey == null){
				catalogKey = NWCGConstants.EMPTY_STRING;
			}
			map.put(NWCGAAConstants.ENTITY_KEY, catalogKey);
			
			String catalogValue = XPathUtil.getString(doc, NWCGAAConstants.XPATH_CREATE_CATALOG_ITEM_CODE);
			if (catalogValue == null){
				catalogValue = NWCGConstants.EMPTY_STRING;
			}
			else {
				String uom = XPathUtil.getString(doc, NWCGAAConstants.XPATH_CREATE_UNIT_OF_ISSUE);
				if (uom != null){
					catalogValue += NWCGConstants.DASH + uom;
				}
			}
			map.put(NWCGAAConstants.ENTITY_VALUE, catalogValue);
			map.put(NWCGAAConstants.ENTITY_NAME, NWCGAAConstants.CATALOG);
		}
		else if (serviceName.equalsIgnoreCase(NWCGAAConstants.UPDATE_CATALOG_ITEM_REQ)){
			String catalogKey = XPathUtil.getString(doc, NWCGAAConstants.XPATH_UPDATE_CATALOG_ITEM_KEY);
			if (catalogKey == null){
				catalogKey = NWCGConstants.EMPTY_STRING;
			}
			map.put(NWCGAAConstants.ENTITY_KEY, catalogKey);
			
			String catalogValue = XPathUtil.getString(doc, NWCGAAConstants.XPATH_UPDATE_CATALOG_ITEM_CODE);
			if (catalogValue == null){
				catalogValue = NWCGConstants.EMPTY_STRING;
			}
			else {
				String uom = XPathUtil.getString(doc, NWCGAAConstants.XPATH_UPDATE_UNIT_OF_ISSUE);
				if (uom != null){
					catalogValue += NWCGConstants.DASH + uom;
				}
			}
			map.put(NWCGAAConstants.ENTITY_VALUE, catalogValue);
			map.put(NWCGAAConstants.ENTITY_NAME, NWCGAAConstants.CATALOG);
		}
		else if (serviceName.equalsIgnoreCase(NWCGAAConstants.DELETE_CATALOG_ITEM_REQ)){
			String catalogKey = XPathUtil.getString(doc, NWCGAAConstants.XPATH_DELETE_CATALOG_ITEM_KEY);
			if (catalogKey == null){
				catalogKey = NWCGConstants.EMPTY_STRING;
			}
			map.put(NWCGAAConstants.ENTITY_KEY, catalogKey);
			
			String catalogValue = XPathUtil.getString(doc, NWCGAAConstants.XPATH_DELETE_CATALOG_ITEM_CODE);
			if (catalogValue == null){
				catalogValue = NWCGConstants.EMPTY_STRING;
			}
			else {
				String uom = XPathUtil.getString(doc, NWCGAAConstants.XPATH_DELETE_CATALOG_ITEM_UNIT_OF_ISSUE);
				if (uom != null){
					catalogValue += NWCGConstants.DASH + uom;
				}
			}
			map.put(NWCGAAConstants.ENTITY_VALUE, catalogValue);
			map.put(NWCGAAConstants.ENTITY_NAME, NWCGAAConstants.CATALOG);
		}
		else {
		// Unknown service name
			NWCGLoggerUtil.Log.finest("Unknown service name : " + serviceName);
		}
		
		return map;
	}

	// overriding this method to pass an empty string
	public void updateMessage(String distID, String type, String msg, String msg_type , String status, String systemName, String key, boolean isLatestInbound) throws Exception{
		updateMessage(distID, type, msg, msg_type , status, systemName, key, isLatestInbound,NWCGConstants.EMPTY_STRING);
	}
	
	public void updateMessage(YFSEnvironment env, String distID, String type, String msg, String msg_type , 
			String status, String systemName, String key, boolean isLatestInbound,String serviceName) throws Exception{
		
	    NWCGLoggerUtil.Log.finest("NWCGMessageStore.updateMessage");
		String latest_inbound = NWCGConstants.EMPTY_STRING;
		
		Document doc_rt = null;
		Document IBDoc = null;
		Document OBDoc = null;
		
		if(isLatestInbound){
			latest_inbound = NWCGConstants.YES;
		}
		
		// Support sugested to remove Lockid from the template xml
		if(type.equals(NWCGAAConstants.MESSAGE_DIR_TYPE_OB)){
			OBDoc = XMLUtil.createDocument(NWCGAAConstants.NWCG_OUTBOUND_MSG_ELM);
			NWCGLoggerUtil.Log.finest("OBDoc before changes is: " + XMLUtil.extractStringFromDocument(OBDoc));
		}
		
		// Support sugested to remove Lockid from the template xml
		if(type.equals(NWCGAAConstants.MESSAGE_DIR_TYPE_IB)){
			IBDoc = XMLUtil.createDocument(NWCGAAConstants.NWCG_INBOUND_MSG_ELM);
			NWCGLoggerUtil.Log.finest("IBDoc before changes is: " + XMLUtil.extractStringFromDocument(IBDoc));
		}
	    
		 if(type.equals(NWCGAAConstants.MESSAGE_DIR_TYPE_OB)){
		    	if(!isEmpty(distID)){
		    		OBDoc.getDocumentElement().setAttribute(NWCGAAConstants.DIST_ID_ATTR,distID);
		    	}
		    	if(!isEmpty(msg)){
		    		OBDoc.getDocumentElement().setAttribute(NWCGAAConstants.MESSAGE,msg.trim());
		    		
		    	}
		    	if(!isEmpty(msg_type)){
		    		OBDoc.getDocumentElement().setAttribute(NWCGAAConstants.MESSAGE_TYPE,msg_type);
		    	}
		    	if(!isEmpty(status)){
		    		OBDoc.getDocumentElement().setAttribute(NWCGAAConstants.MESSAGE_STATUS,status);
		    	}
		    	if(!isEmpty(latest_inbound)){
		    		OBDoc.getDocumentElement().setAttribute(NWCGAAConstants.LATEST_INBOUND,latest_inbound);
		    	}
		    	if(!isEmpty(key)){
		    		OBDoc.getDocumentElement().setAttribute(NWCGAAConstants.MESSAGE_KEY,key);
		    	}
		    	if(!isEmpty(serviceName)){
		    		OBDoc.getDocumentElement().setAttribute(NWCGAAConstants.MESSAGE_NAME,serviceName);
		    	}
		    	if(!isEmpty(systemName)){
		    		OBDoc.getDocumentElement().setAttribute(NWCGAAConstants.SYSTEM_NAME_ATTR,systemName);
		    	}
		    	NWCGLoggerUtil.Log.finest("Input to NWCGChangeOBMessage"+XMLUtil.extractStringFromDocument(OBDoc));
		    	doc_rt = CommonUtilities.invokeService(env,NWCGAAConstants.SDF_CHANGE_OB_MESSAGE_NAME,OBDoc);
		    }
		
		    if(type.equals(NWCGAAConstants.MESSAGE_DIR_TYPE_IB)){			
		    	if(!isEmpty(distID)){
		    		IBDoc.getDocumentElement().setAttribute(NWCGAAConstants.DIST_ID_ATTR,distID);
		    	}
		    	if(!isEmpty(msg.trim())){
		    		IBDoc.getDocumentElement().setAttribute(NWCGAAConstants.MESSAGE,msg.trim());
		    	}
		    	if(!isEmpty(msg_type)){
		    		IBDoc.getDocumentElement().setAttribute(NWCGAAConstants.MESSAGE_TYPE,msg_type);
		    	}
		    	if(!isEmpty(status)){
		    		IBDoc.getDocumentElement().setAttribute(NWCGAAConstants.MESSAGE_STATUS,status);
		    	}
		    	if(!isEmpty(latest_inbound)){
		    		IBDoc.getDocumentElement().setAttribute(NWCGAAConstants.LATEST_INBOUND,latest_inbound);
		    	}
		    	if(!isEmpty(key)){
		    		IBDoc.getDocumentElement().setAttribute(NWCGAAConstants.MESSAGE_KEY,key);
		    	}
		    	if(!isEmpty(serviceName)){
		    		IBDoc.getDocumentElement().setAttribute(NWCGAAConstants.MESSAGE_NAME,serviceName);
		    	}
		    	if(!isEmpty(systemName)){
		    		IBDoc.getDocumentElement().setAttribute(NWCGAAConstants.SYSTEM_NAME_ATTR,systemName);
		    	}
		    	NWCGLoggerUtil.Log.finest("Input to NWCGChangeIBMessage"+XMLUtil.extractStringFromDocument(IBDoc));
		    	doc_rt = CommonUtilities.invokeService(env,NWCGAAConstants.SDF_CHANGE_IB_MESSAGE_NAME,IBDoc);
		    }
		    NWCGLoggerUtil.Log.finest("changed doc::"+XMLUtil.extractStringFromDocument(doc_rt));	    	
		}	
	
	public void updateMessage(String distID, String type, String msg, String msg_type , String status, String systemName, String key, boolean isLatestInbound,String serviceName) throws Exception{
		
		YFSEnvironment  env = CommonUtilities.createEnvironment(NWCGAAConstants.YANTRA_USERNAME, NWCGAAConstants.YANTRA_USER_ID);
		 
		this.updateMessage(env, distID, type, msg, msg_type, status, systemName, key, isLatestInbound,serviceName);
		
	}
	
	public static boolean isEmpty(String str) {
        return (str == null || str.trim().length() == 0);
    }
	
	public static NWCGMessageStore getMessageStore() {		
		//should get the current instance with the DB connections
		return new NWCGMessageStore();
	}
	public void updateMessage(YFSEnvironment env, String string, String string2, String soap_xml, String message_type_latest, String message_status_awaiting_acknowledgment, String external_system_name, String latest_system_key, boolean b) throws Exception {
		updateMessage(env, string, string2, soap_xml, message_type_latest, message_status_awaiting_acknowledgment, external_system_name, latest_system_key, b,NWCGConstants.EMPTY_STRING);		
	}
	
	public String getUserOfOBMessageForDistID(YFSEnvironment env, String distID) throws Exception{    	
    	NWCGLoggerUtil.Log.finest("NWCGMessageStore::getUserOfOBMessageForDistID, DistID : " + distID);
        String userId = NWCGConstants.EMPTY_STRING;
        try{
            Document inDoc = XMLUtil.getDocument("<NWCGOutboundMessage DistributionID=\""+distID+"\" MessageType=\"START\"/>");
            Document opDoc = CommonUtilities.invokeService(env,
                                                           NWCGAAConstants.SDF_GET_OB_MESSAGE_LIST_SERVICE_NAME,
                                                           inDoc);
            
            if(opDoc != null){
    			NodeList nl = opDoc.getElementsByTagName(NWCGAAConstants.NWCG_OUTBOUND_MSG_ELM);
    			if (nl != null && nl.getLength() > 0){
    				String inputXML = ((Element) nl.item(0)).getAttribute(NWCGAAConstants.MESSAGE);
    				Document docIP = XMLUtil.getDocument(inputXML);
    				userId = docIP.getDocumentElement().getAttribute("NWCGUSERID");
    				if (userId == null || userId.trim().length() < 1){
        				userId = ((Element) nl.item(0)).getAttribute(NWCGAAConstants.CREATE_USER_ID);
    				}
    			}
    		}
    	}catch(Exception e){
            NWCGLoggerUtil.Log.warning("NWCGMessageStore::getUserOfOBMessageForDistID, Exception : "+e.toString());
            e.printStackTrace();
        }
    	NWCGLoggerUtil.Log.finest("NWCGMessageStore::getUserOfOBMessageForDistID, Returning UserID");
    	return userId;
    }
	
	public Document getOBMessageForDistID(YFSEnvironment env, String distID) throws Exception{    	
    	NWCGLoggerUtil.Log.finest("NWCGMessageStore::getUserOfOBMessageForDistID, DistID : " + distID);
        Document docIP = null;
        try{
            Document inDoc = XMLUtil.getDocument("<NWCGOutboundMessage DistributionID=\""+distID+"\" MessageType=\"START\"/>");
            Document opDoc = CommonUtilities.invokeService(env,
                                                           NWCGAAConstants.SDF_GET_OB_MESSAGE_LIST_SERVICE_NAME,
                                                           inDoc);
            
            if(opDoc != null){
    			NodeList nl = opDoc.getElementsByTagName(NWCGAAConstants.NWCG_OUTBOUND_MSG_ELM);
    			if (nl != null && nl.getLength() > 0){
    				String inputXML = ((Element) nl.item(0)).getAttribute(NWCGAAConstants.MESSAGE);
    				docIP = XMLUtil.getDocument(inputXML);
    			}
    		}
    	}catch(Exception e){
            NWCGLoggerUtil.Log.warning("NWCGMessageStore::getUserOfOBMessageForDistID, Exception : "+e.toString());
            e.printStackTrace();
        }
    	NWCGLoggerUtil.Log.finest("NWCGMessageStore::getUserOfOBMessageForDistID, Returning UserID");
    	return docIP;
    }
}