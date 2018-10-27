package com.nwcg.icbs.yantra.commCore;

/*import gov.nwcg.services.ross.catalog._1._1.CreateCatalogItemReq;
import gov.nwcg.services.ross.catalog._1._1.DeleteCatalogItemReq;
import gov.nwcg.services.ross.catalog._1._1.UpdateCatalogItemReq;
import gov.nwcg.services.ross.catalog.wsdl._1._1.CatalogInterface;
import gov.nwcg.services.ross.catalog.wsdl._1._1.CatalogServiceLocator;
import gov.nwcg.services.ross.common_types._1._1.ApplicationSystemType;
import gov.nwcg.services.ross.common_types._1._1.CatalogItemCreateType;
import gov.nwcg.services.ross.common_types._1._1.CatalogItemUpdateType;
import gov.nwcg.services.ross.common_types._1._1.CatalogTypeSimpleType;
import gov.nwcg.services.ross.common_types._1._1.MessageAcknowledgement;
import gov.nwcg.services.ross.common_types._1._1.MessageOriginatorType;
import gov.nwcg.services.ross.common_types._1._1.SystemTypeSimpleType;
import gov.nwcg.services.ross.common_types._1._1.UnitIDType;
*/

// Jay : This class is not used, replaced with new version of A&A
public class NWCGOBMsgService  {/*implements YIFCustomApi {

	String urlAddress = "ROSS";
	String systemName = "ICBS";
	
	public Document recieveMsg(YFSEnvironment env, Document msg){
		
		Document respMsg = null;
		try{
			respMsg = this.process(env, msg);
			
		}catch(Exception e){
			NWCGLoggerUtil.Log.warning(e.toString());
		}
		return respMsg;
	}

	public Document process(YFSEnvironment env, Document msg) throws Exception{
		
		String system_key = "";
		String latest_system_key = "";
		
		MessageAcknowledgement msgAck = null;
		
		NWCGLoggerUtil.Log.info("NWCGOBMsgService.process");
		
		//extract the service name from the msg
		String serviceName = msg.getFirstChild().getLocalName();
				
		NWCGLoggerUtil.Log.info("NWCGOBMsgService MessageName: " + serviceName);

		//if the msg contains an IS_SYNC element then do sync processing
		boolean isSync = determineIsSync(serviceName);

	    String body = NWCGAAUtil.serialize(msg);
	    body = body.substring(body.indexOf('>')+1);
		NWCGLoggerUtil.Log.info("body::"+body);

		Document docBody = XMLUtil.getDocument(body);
		
		NWCGMessageStore msgStore = NWCGMessageStore.getMessageStore();
		
		// System key
		try{
			system_key = msgStore.storeMessage("",NWCGAAConstants.MESSAGE_DIR_TYPE_OB, body, NWCGAAConstants.MESSAGE_TYPE_START, NWCGAAConstants.MESSAGE_STATUS_VOID , systemName,serviceName);
		}catch(Exception e){
			NWCGLoggerUtil.Log.warning("Exception occured while storing messages during outbound for generating system key");
			Map map = this.getDetailsForAlert(msgStore, body, serviceName);
			CommonUtilities.raiseAlert(env,NWCGAAConstants.QUEUEID_CATALOG,NWCGAAConstants.ALERT_MESSAGE_3,docBody,e,map);
		}
		
		// Latest system key
		try{
			latest_system_key =  msgStore.storeMessage("",NWCGAAConstants.MESSAGE_DIR_TYPE_OB, body, NWCGAAConstants.MESSAGE_TYPE_LATEST, NWCGAAConstants.MESSAGE_STATUS_OB_MESSAGE_SENT, systemName,serviceName);
		}catch(Exception e){
			NWCGLoggerUtil.Log.warning("Exception occured while storing messages during outbound for generating latest system key");
			Map map = this.getDetailsForAlert(msgStore, body, serviceName);
			CommonUtilities.raiseAlert(env,NWCGAAConstants.QUEUEID_CATALOG,NWCGAAConstants.ALERT_MESSAGE_3,docBody,e,map);
		}
		
		String user_id = msg.getDocumentElement().getAttribute("NWCGUSERID");
		NWCGLoggerUtil.Log.info("user_id  :: "+ user_id );
		if(user_id == null || user_id.equals(""))
		{
			NWCGLoggerUtil.Log.warning("Can not post message to ROSS without a NWCGUSERID attribute, check out your configurations");
			throw new NWCGException("Can not post message to ROSS without a NWCGUSERID attribute");
			
		}
        // Get the loginID and Username from Yantra
    	//String loginID = env.getUserId();
    	NWCGLoggerUtil.Log.info("LoginID::"+user_id);
    	
    	String system_name = env.getSystemName();
		
		String pw_key = NWCGTimeKeyManager.createKey(latest_system_key, user_id, system_name);
		NWCGLoggerUtil.Log.info("pw_key::"+pw_key);
		
		if(isSync){// Synchronous message processing. 
			NWCGLoggerUtil.Log.info("sync call!!!!");
			// Needs to be implemented.
			
		}else{// If Asynchronous then get the Message Acknowldegement and store the message in message store.
			NWCGLoggerUtil.Log.info("Async call!!!");
			
			// now need to invoke web service and post this message.
			try{
				msgAck = postMessage(msgStore,env,msg,pw_key,serviceName,latest_system_key,docBody);
			}catch(RemoteException e){
				NWCGLoggerUtil.Log.warning("Catching SOAP Fault in RemoteException...");
			}
			
			if(msgAck != null){
			    // Update Message store after getting Message Acknowledgement.
				String distID = msgAck.getDistributionID();
				NWCGLoggerUtil.Log.info("msgAckStr dist id::"+distID);
				
				updateMessage(msgStore,env,distID,NWCGAAConstants.MESSAGE_DIR_TYPE_OB,body, NWCGAAConstants.MESSAGE_TYPE_START, NWCGAAConstants.MESSAGE_STATUS_VOID, systemName, system_key, true,docBody, serviceName);
				updateMessage(msgStore,env,distID,NWCGAAConstants.MESSAGE_DIR_TYPE_OB,body, NWCGAAConstants.MESSAGE_TYPE_LATEST, NWCGAAConstants.MESSAGE_STATUS_OB_MESSAGE_SENT, systemName, latest_system_key, true,docBody, serviceName);
				
			}
		}
       NWCGLoggerUtil.Log.info("outside");
       return msg;
	}
	
	*//**
	 * Sunjay - Added serviceName as part of this method. This parameter is used to get the entity information 
	 * as part of getDetailsForAlert
	 * @param env
	 * @param msgStore
	 * @param distID
	 * @param message_dir_type_ob
	 * @param body
	 * @param message_type_start
	 * @param message_status_void
	 * @param systemName2
	 * @param system_key
	 * @param b
	 * @param docBody
	 *//*
	private void updateMessage(NWCGMessageStore msgStore, YFSEnvironment env, String distID, 
								String message_dir_type, String body, String message_type, 
								String message_status, String systemName, String key, boolean b, 
								Document docBody, String serviceName) throws Exception{
		try{
		    if(b==true){
		        msgStore.updateMessage(distID,message_dir_type, body, message_type, message_status, systemName, key, true);
		    }else{
		        msgStore.updateMessage(distID,message_dir_type, body, message_type, message_status, systemName, key, false);
		    }
		}catch(Exception e){
			NWCGLoggerUtil.Log.warning("exception occured while updating message for "+message_type);
			Map map = getDetailsForAlert(msgStore, body, serviceName);
			CommonUtilities.raiseAlert(env,NWCGAAConstants.QUEUEID_OB_EXCEPTIONS,NWCGAAConstants.ALERT_MESSAGE_1,docBody,e,map);
		}
	}
	
	*//**
	 * This method uses retrieveEntityInfo of NWCGMessageStore to get incident or item details. 
	 * IncidentNumber and Year are stored in a single variable entityValue separated by -. 
	 * This method gets the incident number and year from entityValue and puts it in the map
	 * Since, we are planning to merge the OB message services into a single class down the road, I
	 * included both INCIDENT and CATALOG in this method.
	 * @param msgStore
	 * @param msg
	 * @param serviceName
	 * @return
	 * @throws Exception
	 *//*
	private Map getDetailsForAlert(NWCGMessageStore msgStore, String msg, String serviceName)
	throws Exception{
		Map map = new HashMap();
		map = msgStore.retrieveEntityInfo(msg, serviceName);
		String entityValue = (String) map.get("EntityValue");
		String entityName = (String) map.get("EntityName");
		map.clear();
		if (entityValue != null){
			int index = entityValue.lastIndexOf("-");
			if (index != -1){
				String val1 = entityValue.substring(0, index);
				String val2 = entityValue.substring(index+1, entityValue.length());
				if (entityName.equalsIgnoreCase(NWCGAAConstants.INCIDENT_SERVICE_GROUP_NAME)){
					map.put("IncidentNumber", val1);
					map.put("Year", val2);
				}
				else if (entityName.equalsIgnoreCase("CATALOG")){ // Use Constant
					map.put("ItemID", val1);
					map.put("UOM", val2);
				}
			}
		}
		else {
			map = null;
		}
		return map;
	}


	public MessageAcknowledgement postMessage(NWCGMessageStore msgStore, YFSEnvironment env, Document doc,
							String one_time_password,String message_name, String latest_system_key, 
							Document docBody) throws RemoteException, Exception {
        NWCGLoggerUtil.Log.info("Got into the service ");
        
        String userName = "";
        
        boolean prefix = false;
        boolean suffix = false;
        String prefixStr = "";
        String suffixStr = "";
              
        CatalogServiceLocator service = new CatalogServiceLocator();
        MessageAcknowledgement resp = null;
        
        String docStr = XMLUtil.getXMLString(doc);
        
        try
        {
        	String user_id = doc.getDocumentElement().getAttribute("NWCGUSERID");
    		NWCGLoggerUtil.Log.info("user_id  :: "+ user_id );
    		
    		if(user_id == null || user_id.equals(""))
    		{
    			NWCGLoggerUtil.Log.warning("Can not post message to ROSS without a NWCGUSERID attribute, check out your configurations");
    			throw new NWCGException("Can not post message to ROSS without a NWCGUSERID attribute");
    			
    		}
            // Get the loginID and Username from Yantra
        	//String loginID = env.getUserId();
        	NWCGLoggerUtil.Log.info("LoginID::"+user_id);
        	
        	//Username should map with loginid
        	userName = user_id;
        	
        	// Get common code info for Dispatch ID
            Document ipXML = XMLUtil.getDocument("<CommonCode CodeType=\""+NWCGAAConstants.CODE_TYPE+"\" />");
            Document oputXML = CommonUtilities.invokeAPI(env,NWCGAAConstants.API_COMMONCODELIST,ipXML);
            NodeList nl = oputXML.getDocumentElement().getElementsByTagName("CommonCode");
            for(int count=0;count<nl.getLength();count++){
            	Element commonCodeElem = (Element)nl.item(count);
            	if(commonCodeElem.getAttribute("CodeValue").equals(NWCGAAConstants.DISPATCH_PREFIX) && commonCodeElem.getAttribute("CodeShortDescription").equals("CO")){
            		prefix = true;
            		prefixStr = commonCodeElem.getAttribute("CodeShortDescription");
            	}
            	if(commonCodeElem.getAttribute("CodeValue").equals(NWCGAAConstants.DISPATCH_SUFFIX) && commonCodeElem.getAttribute("CodeShortDescription").equals("RMK")){
            		suffix = true;
            		suffixStr = commonCodeElem.getAttribute("CodeShortDescription");
            	}
            }
        	            
        	String string_token = NWCGJAXRPCWSHandlerUtils.getStringToken(userName,user_id,one_time_password,message_name);
        	
            NWCGLoggerUtil.Log.info("java.version=" + System.getProperty("java.version"));
            NWCGLoggerUtil.Log.info("Called ");
            CatalogInterface port = service.getCatalogPort();
            
            // Jay: We should be using the JAXB or some other parsers to take care of parsing instead of setting all the attributes
            
            QName portQName = new QName(NWCGAAConstants.CATALOG_NAMESPACE_OB, NWCGAAConstants.CATALOG_PORT_OB);
            
            HandlerInfo hInfo = new HandlerInfo();
            hInfo.setHandlerClass(com.nwcg.icbs.yantra.handler.NWCGOBMsgServiceHandler.class);
            service.getHandlerRegistry().getHandlerChain(portQName).add(hInfo);
            
            //Jeremy: Add SSL parameters here
            //System.setProperty("java.protocol.handler.pkgs", "com.ibm.net.ssl.internal.www.protocol");
            //java.security.Security.addProvider(new com.ibm.jsse.JSSEProvider());
            
            
            //reads from yifclient.properties
            String KeyStoreFile = ResourceUtil.get("nwcg.ldap.keystore.file");
            String KeyStorePasswd = ResourceUtil.get("nwcg.ldap.keystore.password");
            System.out.println("nwcg.ldap.keystore.file: " + KeyStoreFile);
            
            Jay : Commenting this out, should be part of 2.5 release
            System.setProperty("javax.net.ssl.trustStoreType", "JKS");
            System.setProperty("javax.net.ssl.keyStoreType", "JKS");
            System.setProperty("javax.net.ssl.keyStore",KeyStoreFile);
            System.setProperty("javax.net.ssl.keyStorePassword",KeyStorePasswd);
            System.setProperty("javax.net.ssl.trustStore",KeyStoreFile);
            System.setProperty("javax.net.ssl.trustStorePassword",KeyStorePasswd);
            
            
            if( KeyStoreFile != null && KeyStorePasswd != null) {
	            System.setProperty("javax.net.ssl.trustStoreType", "JKS");
	            System.setProperty("javax.net.ssl.keyStoreType", "JKS");
	            System.setProperty("javax.net.ssl.keyStore",KeyStoreFile);
	            System.setProperty("javax.net.ssl.keyStorePassword",KeyStorePasswd);
	            System.setProperty("javax.net.ssl.trustStore",KeyStoreFile);
	            System.setProperty("javax.net.ssl.trustStorePassword",KeyStorePasswd);
            }
            else {
            	NWCGLoggerUtil.Log.warning("KeyStoreFile or KeystorePassword is not set! SSL security not set.");
            }	

            
            
            if(message_name.equals(NWCGAAConstants.CREATE_CATALOG_ITEM_REQ))
            {
	            CreateCatalogItemReq body = new CreateCatalogItemReq();
	            CatalogItemCreateType catalogItem = new CatalogItemCreateType();
	            
	            catalogItem.setCatalogItemCode(XPathUtil.getString(doc,NWCGAAConstants.XPATH_CREATE_CATALOG_ITEM_CODE));
	            catalogItem.setCatalogItemName(XPathUtil.getString(doc,NWCGAAConstants.XPATH_CREATE_CATALOG_ITEM_NAME));
	            catalogItem.setCatalogType(CatalogTypeSimpleType.fromString(XPathUtil.getString(doc,NWCGAAConstants.XPATH_CREATE_CATALOG_TYPE)));
	            catalogItem.setOrderableWithQuantityInd(Boolean.valueOf(XPathUtil.getString(doc,NWCGAAConstants.XPATH_CREATE_ORDERABLE_QTY)).booleanValue());
	            catalogItem.setStandardPack(XPathUtil.getString(doc,NWCGAAConstants.XPATH_CREATE_STANDARD_PACK));
	            catalogItem.setTrackingRequiredInd(Boolean.valueOf(XPathUtil.getString(doc,NWCGAAConstants.XPATH_CREATE_TRACKING_REQ)).booleanValue());
	            catalogItem.setUnitOfIssue(XPathUtil.getString(doc,NWCGAAConstants.XPATH_CREATE_UNIT_OF_ISSUE));
	            
	            body.setCatalogItem(catalogItem);
	            MessageOriginatorType msgorg = new MessageOriginatorType();
	            // Jay : this should be read and set from the common code, for integration purpose sending this as a blank value
	            //logic: 
	            	1. read value from commom code
	            	2. if value exists then append the unit id prefix and suffix
	            	3. if value does not exist, do not append the dispatch unit id 
	            if(prefix == true && suffix == true){
		            UnitIDType unit = new UnitIDType();
		            unit.setUnitIDPrefix(prefixStr);
		            unit.setUnitIDSuffix(suffixStr);
		            msgorg.setDispatchUnitID(unit);
	            }
	            ApplicationSystemType systemoforigin = new ApplicationSystemType();
	            systemoforigin.setSystemType(SystemTypeSimpleType.ICBS);
	            systemoforigin.setSystemID(string_token);
	            msgorg.setSystemOfOrigin(systemoforigin);
	            body.setMessageOriginator(msgorg);
	            
	            NWCGLoggerUtil.Log.info("Invoking the JAX WS service deployed on 1.5");
	            
	            resp = port.createCatalogItem(body);
	            NWCGLoggerUtil.Log.info("response create catalog item ==> "+ resp);
	            if(resp != null)
	            {
		            NWCGLoggerUtil.Log.info("Got the response ==> " + resp.getResponseStatus().getReturnCode());
		            NWCGLoggerUtil.Log.info("Got the response ==> " + resp.getDistributionID());
	            }
            }
            else if(message_name.equals(NWCGAAConstants.DELETE_CATALOG_ITEM_REQ))
            {
            	NWCGLoggerUtil.Log.info("inside DeleteCatalogItemReq");
            	DeleteCatalogItemReq body = new DeleteCatalogItemReq();
	            // setting up the message originator
            	MessageOriginatorType msgorg = new MessageOriginatorType();
            	// Jay : this should be read and set from the common code, for integration purpose sending this as a blank value
	           // logic: 
	            	1. read value from commom code
	            	2. if value exists then append the unit id prefix and suffix
	            	3. if value does not exist, do not append the dispatch unit id 
            	if(prefix == true && suffix == true){
		            UnitIDType unit = new UnitIDType();
		            unit.setUnitIDPrefix(prefixStr);
		            unit.setUnitIDSuffix(suffixStr);
		            msgorg.setDispatchUnitID(unit);
	            }
	            
	            // setting up system type
	            ApplicationSystemType systemoforigin = new ApplicationSystemType();
	            systemoforigin.setSystemType(SystemTypeSimpleType.ICBS);
	            systemoforigin.setSystemID(string_token);
	            msgorg.setSystemOfOrigin(systemoforigin);
	            body.setMessageOriginator(msgorg);
	            
	            SOAPElement catalogItemKey = javax.xml.soap.SOAPFactory.newInstance().createElement(NWCGAAConstants.CATALOG_ITEM_KEY,NWCGAAConstants.CATALOG_ITEM_KEY_PREFIX,NWCGAAConstants.CATALOG_ITEM_KEY_NAMESPACE);
	            SOAPElement elemCatalogType = catalogItemKey.addChildElement(NWCGAAConstants.CATALOG_TYPE);
	            // as per RossCommon.xsd only valid value is NWCG
	            elemCatalogType.addTextNode(NWCGAAConstants.CATALOG_TYPE_VAL);
	            SOAPElement elemCatalogItemName = catalogItemKey.addChildElement(NWCGAAConstants.CATALOG_ITEM_NAME);
	            elemCatalogItemName.addTextNode(XPathUtil.getString(doc,NWCGAAConstants.XPATH_DELETE_CATALOG_ITEM));
				
	            body.setCatalogItemKey(catalogItemKey );
	            
	            NWCGLoggerUtil.Log.info("invoking deleteCatalogItem API ");
	            
	            resp = port.deleteCatalogItem(body);
	            
	            NWCGLoggerUtil.Log.info("Invoking the JAX WS service deployed on 1.5");
	            NWCGLoggerUtil.Log.info("response delete catalog item ==> "+ resp);
	            if(resp != null)
	            {
		            NWCGLoggerUtil.Log.info("Got the response ==> " + resp.getResponseStatus().getReturnCode());
		            NWCGLoggerUtil.Log.info("Got the response ==> " + resp.getDistributionID());
	            }
            }
            else if(message_name.equals(NWCGAAConstants.UPDATE_CATALOG_ITEM_REQ))
            {
            	NWCGLoggerUtil.Log.info("inside UpdateCatalogItemReq");
            	UpdateCatalogItemReq body = new UpdateCatalogItemReq();
            	
            	CatalogItemUpdateType catalogItem = new CatalogItemUpdateType();
				body.setCatalogItem(catalogItem );
            	// jay: we should not populate the catalog item code and catalog item name
            	catalogItem.setCatalogItemCode(XPathUtil.getString(doc,NWCGAAConstants.XPATH_UPDATE_CATALOG_ITEM_CODE));
	            catalogItem.setCatalogItemName(XPathUtil.getString(doc,NWCGAAConstants.XPATH_UPDATE_CATALOG_ITEM_NAME));
	            catalogItem.setOrderableWithQuantityInd(new Boolean(XPathUtil.getString(doc,NWCGAAConstants.XPATH_UPDATE_ORDERABLE_QTY)));
	            catalogItem.setStandardPack(XPathUtil.getString(doc,NWCGAAConstants.XPATH_UPDATE_STANDARD_PACK));
	            catalogItem.setTrackingRequiredInd(new Boolean(XPathUtil.getString(doc,NWCGAAConstants.XPATH_UPDATE_TRACKING_REQ)));
	            catalogItem.setUnitOfIssue(XPathUtil.getString(doc,NWCGAAConstants.XPATH_UPDATE_UNIT_OF_ISSUE));
            	
	            // setting up the message originator
            	MessageOriginatorType msgorg = new MessageOriginatorType();
            	// Jay : this should be read and set from the common code, for integration purpose sending this as a blank value
	           // logic: 
	            	1. read value from commom code
	            	2. if value exists then append the unit id prefix and suffix
	            	3. if value does not exist, do not append the dispatch unit id 
            	if(prefix == true && suffix == true){
		            UnitIDType unit = new UnitIDType();
		            unit.setUnitIDPrefix(prefixStr);
		            unit.setUnitIDSuffix(suffixStr);
		            msgorg.setDispatchUnitID(unit);
	            }
	            
	            // setting up system type
	            ApplicationSystemType systemoforigin = new ApplicationSystemType();
	            systemoforigin.setSystemType(SystemTypeSimpleType.ICBS);
	            systemoforigin.setSystemID(string_token);
	            msgorg.setSystemOfOrigin(systemoforigin);
	            body.setMessageOriginator(msgorg);
	            
	            SOAPElement catalogItemKey = javax.xml.soap.SOAPFactory.newInstance().createElement(NWCGAAConstants.CATALOG_ITEM_KEY,NWCGAAConstants.CATALOG_ITEM_KEY_PREFIX,NWCGAAConstants.CATALOG_ITEM_KEY_NAMESPACE);
	            SOAPElement elemCatalogType = catalogItemKey.addChildElement(NWCGAAConstants.CATALOG_TYPE);
	            // as per RossCommon.xsd only valid value is NWCG
	            elemCatalogType.addTextNode(NWCGAAConstants.CATALOG_TYPE_VAL);
	            SOAPElement elemCatalogItemName = catalogItemKey.addChildElement(NWCGAAConstants.CATALOG_ITEM_NAME);
	            elemCatalogItemName.addTextNode(XPathUtil.getString(doc,NWCGAAConstants.XPATH_UPDATE_CATALOG_ITEM));
				
	            body.setCatalogItemKey(catalogItemKey );
	            
	            NWCGLoggerUtil.Log.info("invoking updateCatalogItem API ");
	            
	            //update Cert Code prior to call to Catalog Jeremy
	            resp = port.updateCatalogItem(body);
	            
	            NWCGLoggerUtil.Log.info("Invoking the JAX WS service deployed on 1.5");
	            NWCGLoggerUtil.Log.info(" update response from ROSS "+resp);
	            if(resp != null)
	            {
		            NWCGLoggerUtil.Log.info("Got the response ==> " + resp.getResponseStatus().getReturnCode());
		            NWCGLoggerUtil.Log.info("Got the response ==> " + resp.getDistributionID());
	            }
            }
            
        }
        //catch (java.net.MalformedURLException me){
        //catch (IOException ie){
        //catch (RuntimeException ie){
        catch (javax.xml.rpc.soap.SOAPFaultException se){
        	NWCGLoggerUtil.Log.warning("SOAP Fault Exception Detail : " + se.getDetail());
        	NWCGLoggerUtil.Log.warning("Fault Actor : " + se.getFaultActor());
        	NWCGLoggerUtil.Log.warning("Fault Code : " + se.getFaultCode());
        	NWCGLoggerUtil.Log.warning("Fault String : " + se.getFaultString());
        	NWCGLoggerUtil.Log.warning("Updating message store as SENT FAILED");
        	if (se.getCause() != null){
            	NWCGLoggerUtil.Log.warning("Cause for exception: " + se.getCause());
        	}
        	NWCGLoggerUtil.Log.warning("Message from exception : " + se.getMessage());
        	updateMessage(msgStore,env, "", NWCGAAConstants.MESSAGE_DIR_TYPE_OB, docStr, NWCGAAConstants.MESSAGE_TYPE_LATEST,
        					NWCGAAConstants.MESSAGE_STATUS_OB_FAULT, NWCGAAConstants.SYSTEM_NAME, latest_system_key, false, docBody, message_name);
            se.printStackTrace();
        }
        catch(java.net.ConnectException ce){
        	NWCGLoggerUtil.Log.warning("Connect Exception : " + ce.toString());
        	NWCGLoggerUtil.Log.warning("Connect Exception Message : " + ce.getMessage());
        	updateMessage(msgStore,env,"",NWCGAAConstants.MESSAGE_DIR_TYPE_OB,docStr,NWCGAAConstants.MESSAGE_TYPE_LATEST,NWCGAAConstants.MESSAGE_STATUS_SENT_FAILED,NWCGAAConstants.SYSTEM_NAME,latest_system_key,false,docBody, message_name);
            ce.printStackTrace();
        }
        catch (RemoteException re){
        	NWCGLoggerUtil.Log.warning("Remote Exception : " + re.toString());
        	NWCGLoggerUtil.Log.warning("Remote Exception Message : " + re.getMessage());
        	NWCGLoggerUtil.Log.warning("Updating the message status to FAULT");
        	updateMessage(msgStore,env,"",NWCGAAConstants.MESSAGE_DIR_TYPE_OB,docStr,NWCGAAConstants.MESSAGE_TYPE_LATEST,NWCGAAConstants.MESSAGE_STATUS_OB_FAULT,NWCGAAConstants.SYSTEM_NAME,latest_system_key,false,docBody, message_name);
            re.printStackTrace();
        }
        catch(Exception e)
        {
        	NWCGLoggerUtil.Log.warning("Caught the exception in the processing : " + e.getMessage());
        	NWCGLoggerUtil.Log.warning(e.toString());
        	NWCGLoggerUtil.Log.warning("Updating message store to FAULT");
        	updateMessage(msgStore,env,"",NWCGAAConstants.MESSAGE_DIR_TYPE_OB,docStr,NWCGAAConstants.MESSAGE_TYPE_LATEST,NWCGAAConstants.MESSAGE_STATUS_OB_FAULT,NWCGAAConstants.SYSTEM_NAME,latest_system_key,false,docBody, message_name);
            e.printStackTrace();
        }
        return resp;
    }

	boolean determineIsSync(String serviceName){
		NWCGLoggerUtil.Log.info("inside determine is sync");
		String isSync = NWCGProperties.getProperty(serviceName+".isSync");
		NWCGLoggerUtil.Log.info("isSync:"+isSync);
		return isSync.equalsIgnoreCase("TRUE");
	}
	
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
*/}