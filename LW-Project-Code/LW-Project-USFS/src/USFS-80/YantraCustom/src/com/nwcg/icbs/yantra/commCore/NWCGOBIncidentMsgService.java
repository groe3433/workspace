package com.nwcg.icbs.yantra.commCore;

/*import gov.nwcg.services.ross.common_types._1._1.ApplicationSystemType;
import gov.nwcg.services.ross.common_types._1._1.CompositeIncidentKeyType;
import gov.nwcg.services.ross.common_types._1._1.EntityTypeSimpleType;
import gov.nwcg.services.ross.common_types._1._1.IDType;
import gov.nwcg.services.ross.common_types._1._1.IncidentNumberType;
import gov.nwcg.services.ross.common_types._1._1.MessageAcknowledgement;
import gov.nwcg.services.ross.common_types._1._1.MessageOriginatorType;
import gov.nwcg.services.ross.common_types._1._1.SystemTypeSimpleType;
import gov.nwcg.services.ross.common_types._1._1.UnitIDType;
import gov.nwcg.services.ross.resource_order._1._1.RegisterIncidentInterestReq;
import gov.nwcg.services.ross.resource_order._1._1.SetIncidentActivationReq;
import gov.nwcg.services.ross.resource_order.wsdl._1._1.ResourceOrderInterface;
import gov.nwcg.services.ross.resource_order.wsdl._1._1.ResourceOrderServiceLocator;
*/

// Jay: this class is no longer used and is replaced with new A& framework
public class NWCGOBIncidentMsgService  {/*implements YIFCustomApi {

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
		
		NWCGLoggerUtil.Log.info("NWCGOBIncidentMsgService.process");
		
		//extract the service name from the msg
		String serviceName = msg.getFirstChild().getNodeName();
				
		NWCGLoggerUtil.Log.info("NWCGOBIncidentMsgService MessageName: " + serviceName);

		//if the msg contains an IS_SYNC element then do sync processing
		//boolean isSync = determineIsSync(serviceName);
		boolean isSync = ResourceUtil.getAsBoolean(serviceName+".sync", false);
		
		//boolean isSync = true;
		
		// Jay: this code is to remove <?xml version="1.0" encoding="UTF-8"?> tag from input xml
	    String body = NWCGAAUtil.serialize(msg);
	    NWCGLoggerUtil.Log.info("Main body without substring =>"+ body);
	    body = body.substring(body.indexOf('>')+1);
		//NWCGLoggerUtil.Log.info("body::"+body);

		Document docBody = XMLUtil.getDocument(body);
		
		NWCGMessageStore msgStore = NWCGMessageStore.getMessageStore();
		
		// System key
		try{
			system_key = msgStore.storeMessage("",NWCGAAConstants.MESSAGE_DIR_TYPE_OB, body, NWCGAAConstants.MESSAGE_TYPE_START, NWCGAAConstants.MESSAGE_STATUS_VOID , systemName,serviceName);
		}catch(Exception e){
			NWCGLoggerUtil.Log.warning("Exception occured while storing messages during outbound for generating system key");
			Map map = getDetailsForAlert(msgStore, body, serviceName);
			CommonUtilities.raiseAlert(env,NWCGAAConstants.QUEUEID_OB_EXCEPTIONS,NWCGAAConstants.ALERT_MESSAGE_3,docBody,e,map);
		}
		
		// Latest system key
		try{
			latest_system_key =  msgStore.storeMessage("",NWCGAAConstants.MESSAGE_DIR_TYPE_OB, body, NWCGAAConstants.MESSAGE_TYPE_LATEST, NWCGAAConstants.MESSAGE_STATUS_MESSAGE_SENT, systemName,serviceName);
		}catch(Exception e){
			NWCGLoggerUtil.Log.warning("Exception occured while storing messages during outbound for generating latest system key");
			Map map = getDetailsForAlert(msgStore, body, serviceName);
			CommonUtilities.raiseAlert(env,NWCGAAConstants.QUEUEID_OB_EXCEPTIONS,NWCGAAConstants.ALERT_MESSAGE_3,docBody,e,map);
		}
		
		String user_id = docBody.getDocumentElement().getAttribute("NWCGUSERID");
		NWCGLoggerUtil.Log.info("user_id  :: "+ user_id );
		if(user_id == null || user_id.equals(""))
		{
			NWCGLoggerUtil.Log.warning("Can not post message to ROSS without a NWCGUSERID attribute, check out your configurations");
			
			throw new NWCGException("Can not post message to ROSS without a NWCGUSERID attribute");
			
		}
		 Jay : This is hardcoded only for one type of interface... changing to generic code
		if (serviceName.equals(NWCGAAConstants.XPATH_REGISTER_INCIDENT_INTEREST)) {
			XPathUtil.getString(docBody,NWCGAAConstants.XPATH_REGISTER_INCIDENT_NWCGUSERID);
		}
		if (user_id == null) 
			user_id = env.getUserId();
			
		
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
				updateMessage(msgStore,env,distID,NWCGAAConstants.MESSAGE_DIR_TYPE_OB,body, NWCGAAConstants.MESSAGE_TYPE_LATEST, NWCGAAConstants.MESSAGE_STATUS_AWAITING_RESPONSE, systemName, latest_system_key, true,docBody, serviceName);
				
			}
		}
       NWCGLoggerUtil.Log.info("outside");
       return msg;
	}
	
	*//**
	 * Sunjay - Added serviceName attribute. This will help in using retrieveEntityInfo method of
	 * NWCGMessageStore class.
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
			String message_dir_type, String body, String message_type, String message_status, 
			String systemName, String key, boolean b, Document docBody, String serviceName) throws Exception{
		try{
		    if(b==true){
		        msgStore.updateMessage(distID,message_dir_type, body, message_type, message_status, systemName, key, true);
		    }else{
		        msgStore.updateMessage(distID,message_dir_type, body, message_type, message_status, systemName, key, false);
		    }
		}catch(Exception e){
			NWCGLoggerUtil.Log.warning("exception occured while updating message for "+message_type);
			Map map = getDetailsForAlert(msgStore, body, serviceName);
			// Modified the below code to put the appropriate alert message type - START or LATEST
			String alertMessageType = "";
			if (message_type.equalsIgnoreCase(NWCGAAConstants.MESSAGE_TYPE_START)){
				alertMessageType = NWCGAAConstants.ALERT_MESSAGE_1;
			}
			else {
				alertMessageType = NWCGAAConstants.ALERT_MESSAGE_2;
			}
			
			CommonUtilities.raiseAlert(env,NWCGAAConstants.QUEUEID_OB_EXCEPTIONS,alertMessageType,docBody,e,map);
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
			String one_time_password,String message_name, String latest_system_key, Document docBody) 
	throws RemoteException, Exception {
        NWCGLoggerUtil.Log.info("Got into NWCGOBIncident service ");
        
        String userName = "";
        String seqNumberStr="";     
        ResourceOrderServiceLocator service = new ResourceOrderServiceLocator();
        MessageAcknowledgement resp = null;
        
        String docStr = XMLUtil.getXMLString(doc);
        NWCGLoggerUtil.Log.info("Doc is " + docStr);
        
        try
        {
        	String prefixStr = "";
        	String suffixStr = "";

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
             Jay : Not required, this code is only for catalog interface
             * Document ipXML = XMLUtil.getDocument("<CommonCode CodeType=\""+NWCGAAConstants.CODE_TYPE+"\" />");
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
            NWCGLoggerUtil.Log.info("Called in NWCGOBIncidentMsgService before get port");
            ResourceOrderInterface port = service.getResourceOrderPort();

            QName portQName = new QName(NWCGAAConstants.RESOURCE_NAMESPACE_OB, NWCGAAConstants.RESOURCE_PORT_OB);
            HandlerInfo hInfo = new HandlerInfo();
            hInfo.setHandlerClass(com.nwcg.icbs.yantra.handler.NWCGOBMsgServiceHandler.class);
            service.getHandlerRegistry().getHandlerChain(portQName).add(hInfo);
                     
            IncidentNumberType number = new IncidentNumberType();  
                   
            if(message_name.equals(NWCGAAConstants.XPATH_REGISTER_INCIDENT_INTEREST))
            {
            	prefixStr = XPathUtil.getString(doc,NWCGAAConstants.XPATH_REGISTER_INCIDENT_INTEREST_UNITID_PREFIX);
                suffixStr = XPathUtil.getString(doc,NWCGAAConstants.XPATH_REGISTER_INCIDENT_INTEREST_UNITID_SUFFIX);
                NWCGLoggerUtil.Log.info("prefix & Suffirx " + prefixStr + " & " +suffixStr);
	            NWCGLoggerUtil.Log.info("Get a RegisterIncidentInterestReq");
	            RegisterIncidentInterestReq body = new RegisterIncidentInterestReq();
	            
	            CompositeIncidentKeyType incidentKey = new CompositeIncidentKeyType();
	            
	            
	            ApplicationSystemType applicationSystem = new ApplicationSystemType();
	            applicationSystem.setSystemType(SystemTypeSimpleType.ROSS);
	            IDType id = new IDType();
	            id.setEntityID(XPathUtil.getString(doc,NWCGAAConstants.XPATH_REGISTER_INCIDENT_INTEREST_ID));
	            id.setEntityType(EntityTypeSimpleType.Incident);
	            id.setApplicationSystem(applicationSystem);
	            NWCGLoggerUtil.Log.info("Year ==> "+XPathUtil.getString(doc,NWCGAAConstants.XPATH_REGISTER_INCIDENT_INTEREST_YEAR));
	            

	            number.setYearCreated(XPathUtil.getString(doc,NWCGAAConstants.XPATH_REGISTER_INCIDENT_INTEREST_YEAR));
	            seqNumberStr=XPathUtil.getString(doc,NWCGAAConstants.XPATH_REGISTER_INCIDENT_INTEREST_SEQUENCE);
	            NWCGLoggerUtil.Log.info("SeqNumber ==> "+seqNumberStr);
	            number.setSequenceNumber(Long.parseLong(seqNumberStr));
	            
	        
	            MessageOriginatorType msgorg = new MessageOriginatorType();
	       
	            if(prefix == true && suffix == true)
	             * 
               {
		            UnitIDType unit = new UnitIDType();
		            unit.setUnitIDPrefix(prefixStr);
		            unit.setUnitIDSuffix(suffixStr);
		            number.setHostID(unit);
		            msgorg.setDispatchUnitID(unit);
	            }
	            
	            ApplicationSystemType systemoforigin = new ApplicationSystemType();
	            systemoforigin.setSystemType(SystemTypeSimpleType.ICBS);
	            systemoforigin.setSystemID(string_token);
	            msgorg.setSystemOfOrigin(systemoforigin);
	            // Jay: the incident id should be kept blank if providing the natural incident key
	            //incidentKey.setIncidentID(id);
	            incidentKey.setNaturalIncidentKey(number);
	            
	            body.setIncidentKey(incidentKey);
	            body.setMessageOriginator(msgorg);
	            String strFlag = StringUtil.nonNull(XPathUtil.getString(doc,NWCGAAConstants.XPATH_REGISTER_INCIDENT_INTEREST_INDICATOR));
	            NWCGLoggerUtil.Log.info("strFlag ==>"+ strFlag+"<==");
	            NWCGLoggerUtil.Log.info("boolean Flag ==>"+ (strFlag.equals("true")? true:false)+ "<==");
	            NWCGLoggerUtil.Log.info("Posting Register (String) ? " + StringUtil.nonNull(XPathUtil.getString(doc,NWCGAAConstants.XPATH_REGISTER_INCIDENT_INTEREST_INDICATOR))+ "<==");
	            NWCGLoggerUtil.Log.info("Posting Register (Boolean)? " + Boolean.valueOf(strFlag).booleanValue()+ "<==");
	            body.setRegisterInterestInd(Boolean.valueOf(strFlag).booleanValue());
	            
	            NWCGLoggerUtil.Log.info("register incident interest ==> ");
	            resp = port.registerIncidentInterest(body);
	            NWCGLoggerUtil.Log.info("register incident interest ==> " + resp);
	            
	            if(resp != null)
	            {
		            NWCGLoggerUtil.Log.info("Got the response ==> " + resp.getResponseStatus().getReturnCode());
		            //NWCGLoggerUtil.Log.info("Got the response ==> " + resp.getDistributionID());
	            }
        }

            
            else if(message_name.equals(NWCGAAConstants.XPATH_ACTIVATE_INCIDENT_INTEREST))
            {
            	prefixStr = XPathUtil.getString(doc,NWCGAAConstants.XPATH_ACTIVE_INCIDENT_INTEREST_UNITID_PREFIX);
                suffixStr = XPathUtil.getString(doc,NWCGAAConstants.XPATH_ACTIVE_INCIDENT_INTEREST_UNITID_SUFFIX);
                NWCGLoggerUtil.Log.info("prefix & Suffirx " + prefixStr + " & " +suffixStr);
	            NWCGLoggerUtil.Log.info("Get an ActivateIncidentInterestReq");
	            SetIncidentActivationReq body = new SetIncidentActivationReq();
	            
	            CompositeIncidentKeyType incidentKey = new CompositeIncidentKeyType();
	            
	            
	            ApplicationSystemType applicationSystem = new ApplicationSystemType();
	            applicationSystem.setSystemType(SystemTypeSimpleType.ROSS);
	            IDType id = new IDType();
	            id.setEntityID(XPathUtil.getString(doc,NWCGAAConstants.XPATH_ACTIVATE_INCIDENT_INTEREST_ID));
	            id.setEntityType(EntityTypeSimpleType.Incident);
	            id.setApplicationSystem(applicationSystem);
	            
	            
	            number.setYearCreated(XPathUtil.getString(doc,NWCGAAConstants.XPATH_ACTIVATE_INCIDENT_INTEREST_YEAR));
	            seqNumberStr=XPathUtil.getString(doc,NWCGAAConstants.XPATH_ACTIVATE_INCIDENT_INTEREST_SEQUENCE);
	            number.setSequenceNumber(Long.parseLong(seqNumberStr));
	            
	        
	            MessageOriginatorType msgorg = new MessageOriginatorType();
	       
	            if(prefix == true && suffix == true)
	            {
		            UnitIDType unit = new UnitIDType();
		            unit.setUnitIDPrefix(prefixStr);
		            unit.setUnitIDSuffix(suffixStr);
		            number.setHostID(unit);
		            msgorg.setDispatchUnitID(unit);
	            }
	            ApplicationSystemType systemoforigin = new ApplicationSystemType();
	            systemoforigin.setSystemType(SystemTypeSimpleType.ICBS);
	            systemoforigin.setSystemID(string_token);
	            msgorg.setSystemOfOrigin(systemoforigin);
//	          Jay: the incident id should be kept blank if providing the natural incident key
	            //incidentKey.setIncidentID(id);
	            incidentKey.setNaturalIncidentKey(number);
	            
	            body.setIncidentKey(incidentKey);
	            body.setMessageOriginator(msgorg);
	            String strFlag = (StringUtil.nonNull(XPathUtil.getString(doc,NWCGAAConstants.XPATH_SET_INCIDENT_ACTIVE_INDICATOR)).trim());
	            NWCGLoggerUtil.Log.info("strFlag ==>" + strFlag+ "<==");
	            NWCGLoggerUtil.Log.info("Boolean Flag ==>" + (strFlag.equals("true")? true:false)+ "<==");
	            NWCGLoggerUtil.Log.info("Posting Active (String)? " + StringUtil.nonNull(XPathUtil.getString(doc,NWCGAAConstants.XPATH_SET_INCIDENT_ACTIVE_INDICATOR))+ "<==");
	            NWCGLoggerUtil.Log.info("Posting Active (Boolean)? " + Boolean.valueOf(strFlag).booleanValue()+ "<==");
	            body.setActiveInExtSystemInd(Boolean.valueOf(strFlag).booleanValue());
	            
	            NWCGLoggerUtil.Log.info("activate incident interest ==> ");
	            resp = port.setIncidentActivation(body);
	            NWCGLoggerUtil.Log.info("activate incident interest ==> "+ resp);
	            
	            if(resp != null)
	            {
		            NWCGLoggerUtil.Log.info("Got the response ==> " + resp.getResponseStatus().getReturnCode());
		            //NWCGLoggerUtil.Log.info("Got the response ==> " + resp.getDistributionID());
	            }
            }//endof else

            //incident is locked by now, just updating Lock Reason
            if(resp != null)
            {
            	String distId= resp.getDistributionID();
            	String reasonForLock = message_name +" DistributionID: "+distId;
            	String strIncidentNo= (prefixStr+"-"+suffixStr+"-"+seqNumberStr);
            	NWCGLoggerUtil.Log.info("Storing reason for lock  "+reasonForLock+" incidentNumber="+strIncidentNo+" Year="+number.getYearCreated());
            	if((number.getYearCreated() != null)&&(number.getYearCreated().length() >0))
            	{
            		updateLockReasonInService(env,strIncidentNo, number.getYearCreated(), reasonForLock);
            	}
            	else
            		NWCGLoggerUtil.Log.info("LockReason was not stored, since required YearCreated=null");
            }
        }
        catch(Exception e)
        {
        	NWCGLoggerUtil.Log.warning("Caught the exception in the re-processing ");
			NWCGLoggerUtil.printStackTraceToLog(e);
        	NWCGLoggerUtil.Log.warning("Updating message store");
        	updateMessage(msgStore,env,"",NWCGAAConstants.MESSAGE_DIR_TYPE_OB,docStr,NWCGAAConstants.MESSAGE_TYPE_LATEST,NWCGAAConstants.MESSAGE_STATUS_AUTHORIZATION_FAILED,NWCGAAConstants.SYSTEM_NAME,latest_system_key,false,docBody, message_name);
        	
            e.printStackTrace();
        }
        return resp;
    }
	
	
	**updates LockReason column in NWCG_INCIDENT_ORDER table with msg name and distID
	
	private void updateLockReasonInService(YFSEnvironment env,String strIncidentNo,String year,String reasonForLock) 
	{	
		
    		Document updateIncidentOrderXMLDoc = null;
    		try {
                
                NWCGLoggerUtil.Log.info((new StringBuffer("<NWCGIncidentOrder IncidentNo=\""))
				.append(""+ strIncidentNo).append("\" Year=\"").append(""+year).append("\" IncidentLocked=\"Y\" LockReason=\"").append(reasonForLock +"\"  />").toString());

    			updateIncidentOrderXMLDoc = XMLUtil.getDocument((new StringBuffer("<NWCGIncidentOrder IncidentNo=\""))
						.append(""+ strIncidentNo).append("\" Year=\"").append(""+year).append("\" IncidentLocked=\"Y\" LockReason=\"").append(reasonForLock +"\"  />").toString());
    			
    			
    		
    		} catch(ParserConfigurationException e){
	        	NWCGLoggerUtil.Log.info("ParserConfigurationException:"+e.getMessage());
	        	e.printStackTrace();
	        } catch (SAXException e) {
	        	NWCGLoggerUtil.Log.info("SAXException:"+e.getMessage());
	        	e.printStackTrace();
			} catch (IOException e) {
				NWCGLoggerUtil.Log.info("IOException:"+e.getMessage());
				e.printStackTrace();
			}
    		
    		try {
				CommonUtilities.invokeService(env,"NWCGUpdateIncidentOrderService",updateIncidentOrderXMLDoc);
			} catch (Exception e2) {
				NWCGLoggerUtil.Log.info("exception in updateLockReasonByService while invoking NWCGUpdateIncidentOrderService");
				e2.printStackTrace();
				NWCGLoggerUtil.printStackTraceToLog(e2);
			}
		
		
	}
	
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
*/}