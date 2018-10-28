/*
 * Created on May 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.nwcg.icbs.yantra.commCore;

import java.util.Properties;

import com.nwcg.icbs.yantra.agents.NWCGReadNoResultMessageAgent;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.yantra.interop.japi.YIFCustomApi;

/**
 * @author sdas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
// Jay: This class is no longer used replaced with new generic A&A framework
public class NWCGGetIncidentReqToROSS implements YIFCustomApi {
	private static Logger logger = Logger.getLogger(NWCGReadNoResultMessageAgent.class.getName());
    public static String distributionID = "";
    
     /*(non-Javadoc)
     * @see com.yantra.interop.japi.YIFCustomApi#setProperties(java.util.Properties)
     */
    public void setProperties(Properties arg0) throws Exception {
        // TODO Auto-generated method stub
        
    }
     /* 
    public Document process(YFSEnvironment env, Document inDoc) throws Exception{
        
        String system_key 				= "";
		String latest_system_key 		= "";
		String errorDetailMessage 		= "";
		
		Document resultDoc				= null;
		GetIncidentResp getIncidentResp = null;
		
		boolean isFailure 				= false;
		
		String body = XMLUtil.getXMLString(inDoc);
		
		String user_id = env.getUserId();
		String system_name = env.getSystemName();
		
        // extract the service name from the msg
		String serviceName = inDoc.getFirstChild().getNodeName();
		NWCGLoggerUtil.Log.info("serviceName: " + serviceName);
		
		NWCGMessageStore msgStore = NWCGMessageStore.getMessageStore();
		
		// Getting the System key(message key)
		try{
			system_key = msgStore.storeMessage("",NWCGAAConstants.MESSAGE_DIR_TYPE_OB, body, NWCGAAConstants.MESSAGE_TYPE_START, NWCGAAConstants.MESSAGE_STATUS_VOID , NWCGAAConstants.SYSTEM_NAME,serviceName);
		}catch(Exception e){
			NWCGLoggerUtil.Log.warning("Exception occured while storing messages during outbound for generating system key");
			CommonUtilities.raiseAlert(env,NWCGAAConstants.QUEUEID_INCIDENT,NWCGAAConstants.ALERT_MESSAGE_3,inDoc,e,null);
		}
		
		// Getting the Latest system key(latest message key)
		try{
			latest_system_key =  msgStore.storeMessage("",NWCGAAConstants.MESSAGE_DIR_TYPE_OB, body, NWCGAAConstants.MESSAGE_TYPE_LATEST, NWCGAAConstants.MESSAGE_STATUS_MESSAGE_SENT, NWCGAAConstants.SYSTEM_NAME,serviceName);
		}catch(Exception e){
			NWCGLoggerUtil.Log.warning("Exception occured while storing messages during outbound for generating latest system key");
			CommonUtilities.raiseAlert(env,NWCGAAConstants.QUEUEID_INCIDENT,NWCGAAConstants.ALERT_MESSAGE_3,inDoc,e,null);
		}
		
		String pw_key = NWCGTimeKeyManager.createKey(latest_system_key, user_id, system_name);
		NWCGLoggerUtil.Log.info("pw_key::"+pw_key);
		
		// Username should map with loginid
    	String userName = user_id;
		
		// Get the string token to pass on as the value for SystemID.
		// Assuming that nothing is set for SystemID earlier.
		String string_token = NWCGJAXRPCWSHandlerUtils.getStringToken(userName,user_id,pw_key,serviceName);
		
		// The GetIncidentDetails call to ROSS is SYNC
		try{
		    getIncidentResp = postGetIncidentReqMsgToROSS(env,inDoc,string_token);
		}catch(Exception e){
		    NWCGLoggerUtil.Log.warning("Exception thrown is:"+e.toString());
		    NWCGLoggerUtil.Log.warning("Now it should hit handle fault method of the handler");
		    isFailure = true;
		    errorDetailMessage = e.getMessage();
		}
		String errorString = "Exception occured in ROSS";
		
		// Here known limitation is JAXB APIs cannot be used on WAS 6.0 client
		// code to marshall a Java object returned from ROSS to Document XML format.
		// So i am following the painstaking effort of forming a blank GetIncidentResponse
		// XML document and setting respective attribues after getting the values from the 
		// getIncidentResp object.
		
		if(!isFailure){
		    if(getIncidentResp != null){
			    if(getIncidentResp.getResponseStatus().getReturnCode()==-1){
				    
				    ResponseMessageType[] responseMessageTypes = getIncidentResp.getResponseStatus().getResponseMessage();
				    for(int i=0;i<responseMessageTypes.length;i++){
				        ResponseMessageType responseMessageType = responseMessageTypes[i];
				        errorDetailMessage = responseMessageType.getDescription();
				    }
				    
				    resultDoc = getROSSFailureDoc(errorString, errorDetailMessage);
				}else{
			    
				    NWCGLoggerUtil.Log.info("GetIncidentdetails call to ROSS is success");
			    
				    // to retrieve the response xml from handler
				    ServletEndpointContextImpl endpointContextImpl = ServletEndpointContextImpl.get();
				    ServletContext servletContext = endpointContextImpl.getServletContext();
				    String getIncidentRespStr = (String)servletContext.getAttribute("GetIncidentRespXML");
				    NWCGLoggerUtil.Log.info("GetIncidentRespXML :"+getIncidentRespStr);
				    // to retrieve the response xml from handler
				    
					resultDoc = createGetIncidentRespDocumentFromObj(getIncidentResp);
					
					ResponseStatusType responseStatusType = getIncidentResp.getResponseStatus();
			        
					int code = responseStatusType.getReturnCode();
			        NWCGLoggerUtil.Log.info("ret code :"+code);
			        
			        String distID = NWCGGetIncidentReqToROSS.getDistributionID();
			        NWCGLoggerUtil.Log.info("Dist id to be set:"+distID);
			        
			        if(code == 0){ 
			            updateMessage(msgStore,env,distID,NWCGAAConstants.MESSAGE_DIR_TYPE_OB,body, NWCGAAConstants.MESSAGE_TYPE_START, NWCGAAConstants.MESSAGE_STATUS_VOID, NWCGAAConstants.SYSTEM_NAME, system_key, true,inDoc);
						updateMessage(msgStore,env,distID,NWCGAAConstants.MESSAGE_DIR_TYPE_OB,body, NWCGAAConstants.MESSAGE_TYPE_LATEST, NWCGAAConstants.MESSAGE_STATUS_AWAITING_RESPONSE, NWCGAAConstants.SYSTEM_NAME, latest_system_key, true,inDoc);
				    }
			        
			        // Raise alert to notify user that Incident is successfully created in ICBSR
			        // XPath for accessing the incident number
			        XPathWrapper pathWrapper = new XPathWrapper(resultDoc);
			        String prefix = pathWrapper.getAttribute("/GetIncidentResp/Incident/IncidentKey/NaturalIncidentKey/HostID/UnitIDPrefix");
			        String suffix = pathWrapper.getAttribute("/GetIncidentResp/Incident/IncidentKey/NaturalIncidentKey/HostID/UnitIDSuffix");
			        String seq = pathWrapper.getAttribute("/GetIncidentResp/Incident/IncidentKey/NaturalIncidentKey/SequenceNumber");
			        String incidentNo = prefix+"-"+suffix+"-"+seq;
			        NWCGLoggerUtil.Log.info("incidentNo for alert:"+incidentNo);
			        Map map = new HashMap();
			        map.put("IncidentNo",incidentNo);
			        
			        CommonUtilities.raiseAlert(env,NWCGAAConstants.QUEUEID_INCIDENT,"Incident successfully created in ICBSR",null,null,map);
			        
				}
		    }
		}else{
		    NWCGLoggerUtil.Log.info("GetIncidentdetails call to ROSS is failure");
		    resultDoc = getROSSFailureDoc(errorString, errorDetailMessage);
		    
		    // do we need to update message store here?
		}
		
		
		NWCGLoggerUtil.Log.info("resultDoc :"+XMLUtil.getXMLString(resultDoc));
        return resultDoc;
    }
    
    *//**
     * @param errorString
     * @param errorDetailMessage
     * @return
     * @throws ParserConfigurationException
     *//*
    private Document getROSSFailureDoc(String errorString, String errorDetailMessage) throws ParserConfigurationException {
        Document resultDoc;
        resultDoc = XMLUtil.createDocument("ROSSFailureDoc");
        resultDoc.getDocumentElement().setAttribute("ErrorString",errorString);
        resultDoc.getDocumentElement().setAttribute("ErrorDetailMessage",errorDetailMessage);
        return resultDoc;
    }

    *//**
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
	private void updateMessage(NWCGMessageStore msgStore, YFSEnvironment env, String distID, String message_dir_type, String body, String message_type, String message_status, String systemName, String key, boolean b, Document docBody) {
		try{
		    if(b==true){
		        msgStore.updateMessage(distID,message_dir_type, body, message_type, message_status, systemName, key, true);
		    }else{
		        msgStore.updateMessage(distID,message_dir_type, body, message_type, message_status, systemName, key, false);
		    }
		}catch(Exception e){
			NWCGLoggerUtil.Log.warning("exception occured while updating message for "+message_type);
			CommonUtilities.raiseAlert(env,NWCGAAConstants.QUEUEID_INCIDENT,NWCGAAConstants.ALERT_MESSAGE_1,docBody,e,null);
		}
	}
    
    *//**
     * @param getIncidentResp
     * @return
     *//*
    private Document createGetIncidentRespDocumentFromObj(GetIncidentResp getIncidentResp) throws Exception {
        
        Document getIncidentrespDoc = null;
        try{
        	
        	
            getIncidentrespDoc = XMLUtil.createDocument("GetIncidentResp");
           
            Element getIncidentrespDocElem = getIncidentrespDoc.getDocumentElement();
            
            ResponseStatusType responseStatusType = getIncidentResp.getResponseStatus();
            
            
            *//** ResponseStatus start **//*
            Element respStatElem = getIncidentrespDoc.createElement("ResponseStatus");
            
            
            *//** ReturnCode start **//*
            Element returnCodeElem = getIncidentrespDoc.createElement("ReturnCode");
           
            Text textRetCodeElem = getIncidentrespDoc.createTextNode(Integer.toString(responseStatusType.getReturnCode()));
           
            returnCodeElem.appendChild(textRetCodeElem);
           
            respStatElem.appendChild(returnCodeElem);
            
            *//** ReturnCode end **//*
            
            ResponseMessageType[] responseMessageTypes = responseStatusType.getResponseMessage();
            System.out.println("****set up responseMessageTypes[]");
            System.out.println("****responseMessageTypes.length: " + responseMessageTypes.length);
            
            for(int a=0;a<responseMessageTypes.length;a++){
                //ResponseMessage start 
            	System.out.println("**** In responseMessageTypes for loop iteration number: " + a);
                Element responseMessageElem = getIncidentrespDoc.createElement("ResponseMessage");
                
                ResponseMessageType responseMessageType = responseMessageTypes[a];
                
                Element codeElem = getIncidentrespDoc.createElement("Code");
                Text textCodeElem = getIncidentrespDoc.createTextNode(responseMessageType.getCode());
                codeElem.appendChild(textCodeElem);
                responseMessageElem.appendChild(codeElem);
                
                Element descElem = getIncidentrespDoc.createElement("Description");
                Text textdescElem = getIncidentrespDoc.createTextNode(responseMessageType.getDescription());
                descElem.appendChild(textdescElem);
                responseMessageElem.appendChild(descElem);
                
                Element sevElem = getIncidentrespDoc.createElement("Severity");
                Text textsevElem = getIncidentrespDoc.createTextNode(responseMessageType.getSeverity().getValue());
                sevElem.appendChild(textsevElem);
                responseMessageElem.appendChild(sevElem);
                
                respStatElem.appendChild(responseMessageElem);
                // ResponseMessage end 
            }
           
            getIncidentrespDocElem.appendChild(respStatElem);
            *//** ResponseStatus end **//*
            
            IncidentReturnType incidentReturnType = getIncidentResp.getIncident();
            CompositeIncidentKeyType compositeIncidentKeyType = incidentReturnType.getIncidentKey();
            IDType type = compositeIncidentKeyType.getIncidentID();
            
            *//** Incident start **//*
            Element incidentElem = getIncidentrespDoc.createElement("Incident");
            
            // Incident Contact Type element is hard coded to be a child element of Incident.
            // Actual path will be decided based on changes in ROSS schema. Right now i am 
            // doing this so that it will seem as if ROSS sends us a getIncidentResp XML which has 
            // a Incident Contact Type information present in it.
            // Incident has one to many relationship with incident contacts.
            // NOTE: The below block of code should be commented in production.
            *//***** start ********//*
            // 1.
            
           
            Element incidentContactsElem = getIncidentrespDoc.createElement("IncidentContacts");
            
            Element incidentContactTypeElem = getIncidentrespDoc.createElement("IncidentContactType");
            
            Element typeElem = getIncidentrespDoc.createElement("Type");
            Text textTypeElem1 = getIncidentrespDoc.createTextNode("Requesting"); // Can be Shipping or Requesting
            typeElem.appendChild(textTypeElem1);
            incidentContactTypeElem.appendChild(typeElem);
            
            Element nameElem = getIncidentrespDoc.createElement("Name");
            Text textNameElem1 = getIncidentrespDoc.createTextNode("Suryasnat Das");
            nameElem.appendChild(textNameElem1);
            incidentContactTypeElem.appendChild(nameElem);
            
            Element informationElem = getIncidentrespDoc.createElement("Information");
            Text textInformationElem = getIncidentrespDoc.createTextNode("707-342-3103");
            informationElem.appendChild(textInformationElem);
            incidentContactTypeElem.appendChild(informationElem);
            
            incidentContactsElem.appendChild(incidentContactTypeElem);
            
            // 2.
            Element incidentContactTypeElem1 = getIncidentrespDoc.createElement("IncidentContactType");
            
            Element typeElem2 = getIncidentrespDoc.createElement("Type");
            Text textTypeElem2 = getIncidentrespDoc.createTextNode("Shipping"); // Can be Shipping or Requesting
            typeElem2.appendChild(textTypeElem2);
            incidentContactTypeElem1.appendChild(typeElem2);
            
            Element nameElem2 = getIncidentrespDoc.createElement("Name");
            Text textNameElem2 = getIncidentrespDoc.createTextNode("Surya");
            nameElem2.appendChild(textNameElem2);
            incidentContactTypeElem1.appendChild(nameElem2);
            
            Element informationElem1 = getIncidentrespDoc.createElement("Information");
            Text textInformationElem1 = getIncidentrespDoc.createTextNode("9886071719");
            informationElem1.appendChild(textInformationElem1);
            incidentContactTypeElem1.appendChild(informationElem1);
            
            incidentContactsElem.appendChild(incidentContactTypeElem1);
            
            incidentElem.appendChild(incidentContactsElem);
                    
            *//***** end ********//*
            
            // Same as above for incident account codes. incident has one to many 
            // relationship with account codes.
            *//******* start ********//*
           
            Element incidentAccountCodesElem = getIncidentrespDoc.createElement("IncidentAccountCodes");
            
            Element rossAccountCodeElem1 = getIncidentrespDoc.createElement("ROSSAccountCode");
            
            Element accountCodeElem = getIncidentrespDoc.createElement("AccountCode");
            Text textAccountCodeElem = getIncidentrespDoc.createTextNode("account1");
            accountCodeElem.appendChild(textAccountCodeElem);
            rossAccountCodeElem1.appendChild(accountCodeElem);
            
            incidentAccountCodesElem.appendChild(rossAccountCodeElem1);
            
            Element rossAccountCodeElem2 = getIncidentrespDoc.createElement("ROSSAccountCode");
            
            Element accountCodeElem1 = getIncidentrespDoc.createElement("AccountCode");
            Text textAccountCodeElem1 = getIncidentrespDoc.createTextNode("account1");
            accountCodeElem1.appendChild(textAccountCodeElem1);
            rossAccountCodeElem2.appendChild(accountCodeElem1);
            
            incidentAccountCodesElem.appendChild(rossAccountCodeElem2);
            
            incidentElem.appendChild(incidentAccountCodesElem);
           
            *//******* end ********//*
            
            *//** IncidentKey start **//*
            Element incidentKeyElem = getIncidentrespDoc.createElement("IncidentKey");
            
            *//** IncidentID start **//*
            Element incidentIDElem = getIncidentrespDoc.createElement("IncidentID");
            
            *//** EntityType start **//*
            Element enityTypeElem = getIncidentrespDoc.createElement("EntityType");
            Text textentityTypeElem = getIncidentrespDoc.createTextNode(type.getEntityType().getValue());
            enityTypeElem.appendChild(textentityTypeElem);
            incidentIDElem.appendChild(enityTypeElem);
            *//** EntityType end **//*
            
            *//** EntityID start **//*
            Element entityIDElem = getIncidentrespDoc.createElement("EntityID");
            Text textEntityIDElem = getIncidentrespDoc.createTextNode(type.getEntityID());
            entityIDElem.appendChild(textEntityIDElem);
            incidentIDElem.appendChild(entityIDElem);
            *//** EntityID end **//*
            
            *//** ApplicationSystem start **//*
            Element applicationSystemElem = getIncidentrespDoc.createElement("ApplicationSystem");
            
            ApplicationSystemType applicationSystemType = type.getApplicationSystem();
            
            *//** SystemType start **//*
            Element systemTypeElem = getIncidentrespDoc.createElement("SystemType");
            Text textSystemTypeElem = getIncidentrespDoc.createTextNode(applicationSystemType.getSystemType().getValue());
            systemTypeElem.appendChild(textSystemTypeElem);
            applicationSystemElem.appendChild(systemTypeElem);
            *//** SystemType end **//*
           
            *//** SystemID start **//*
            Element systemIDElem = getIncidentrespDoc.createElement("SystemID");
            Text textSystemIDElem = getIncidentrespDoc.createTextNode(applicationSystemType.getSystemID());
            systemIDElem.appendChild(textSystemIDElem);
            applicationSystemElem.appendChild(systemIDElem);
            
            *//** SystemID end **//*
           
            incidentIDElem.appendChild(applicationSystemElem);
            *//** ApplicationSystem end **//*
            
           
            incidentKeyElem.appendChild(incidentIDElem);
            *//** IncidentID end **//*
            
            
            IncidentNumberType incidentNumberType = compositeIncidentKeyType.getNaturalIncidentKey();
            UnitIDType unitIDType = incidentNumberType.getHostID();
           
            
            *//** NaturalIncidentKey start **//*
           
            Element naturalIncidentKeyElem = getIncidentrespDoc.createElement("NaturalIncidentKey");
            
            *//** HostID start **//*
           
            Element hostIDElem = getIncidentrespDoc.createElement("HostID");
            
            *//** HostID UnitIDPrefix start **//*
          
            Element hostIDUnitIDPrefixElem = getIncidentrespDoc.createElement("UnitIDPrefix");
            
            
            // Fetch the distribution id from the UnitIDPrefix
           
            String unitIDPrefix = unitIDType.getUnitIDPrefix();
           
            Text texthostIDUnitIDPrefixElem = getIncidentrespDoc.createTextNode(unitIDPrefix);
            hostIDUnitIDPrefixElem.appendChild(texthostIDUnitIDPrefixElem);
            hostIDElem.appendChild(hostIDUnitIDPrefixElem);
            *//** HostID UnitIDPrefix end **//*
            
            *//** HostID UnitIDSuffix start **//*
            Element hostIDUnitIDSuffixElem = getIncidentrespDoc.createElement("UnitIDSuffix");
            Text texthostIDUnitIDSuffixElem = getIncidentrespDoc.createTextNode(unitIDType.getUnitIDSuffix());
            hostIDUnitIDSuffixElem.appendChild(texthostIDUnitIDSuffixElem);
            hostIDElem.appendChild(hostIDUnitIDSuffixElem);
            *//** HostID UnitIDSuffix end **//*
            naturalIncidentKeyElem.appendChild(hostIDElem);
            *//** HostID end**//*
            
            *//** SequenceNumber start **//*
            Element sequenceNoElem = getIncidentrespDoc.createElement("SequenceNumber");
            Text textSequenceNoElem = getIncidentrespDoc.createTextNode(Long.toString(incidentNumberType.getSequenceNumber()));
            sequenceNoElem.appendChild(textSequenceNoElem);
            naturalIncidentKeyElem.appendChild(sequenceNoElem);
            *//** SequenceNumber end **//*
            
            *//** YearCreated start **//*
            Element yearCreatedElem = getIncidentrespDoc.createElement("YearCreated");
            Text textYearCreatedElem = getIncidentrespDoc.createTextNode(incidentNumberType.getYearCreated());
            yearCreatedElem.appendChild(textYearCreatedElem);
            naturalIncidentKeyElem.appendChild(yearCreatedElem);
            *//** YearCreated end **//*
            
            incidentKeyElem.appendChild(naturalIncidentKeyElem);
            *//** NaturalIncidentKey end **//*
            
            incidentElem.appendChild(incidentKeyElem);
            *//** IncidentKey end **//*
            
            IncidentDetailsReturnType incidentDetailsReturnType = incidentReturnType.getIncidentDetails();
            
            *//** IncidentDetails start **//*
            Element incidentDetailsElem = getIncidentrespDoc.createElement("IncidentDetails");
            
            *//** IncidentName start **//*
            Element incidentNameElem = getIncidentrespDoc.createElement("IncidentName");
            Text textIncidentNameElem = getIncidentrespDoc.createTextNode(incidentDetailsReturnType.getIncidentName());
            incidentNameElem.appendChild(textIncidentNameElem);
            incidentDetailsElem.appendChild(incidentNameElem);
            *//** IncidentName end **//*
            
            *//** IncidentType start **//*
            Element incidentTypeElem = getIncidentrespDoc.createElement("IncidentType");
            Text textIncidentTypeElem = getIncidentrespDoc.createTextNode(incidentDetailsReturnType.getIncidentType());
            incidentTypeElem.appendChild(textIncidentTypeElem);
            incidentDetailsElem.appendChild(incidentTypeElem);
            *//** IncidentName end **//*
            
            *//** SystemOfRecord start **//*
            Element systemOfRecordElem = getIncidentrespDoc.createElement("SystemOfRecord");
            
            ApplicationSystemType applicationSystemType2 = incidentDetailsReturnType.getSystemOfRecord();
            
            *//** SystemType start **//*
            Element systemTypeElem1 = getIncidentrespDoc.createElement("SystemType");
            Text textSystemTypeElem1 = getIncidentrespDoc.createTextNode(applicationSystemType2.getSystemType().getValue());
            systemTypeElem1.appendChild(textSystemTypeElem1);
            systemOfRecordElem.appendChild(systemTypeElem1);
            *//** SystemType end **//*
            
            *//** SystemID start **//*
            Element systemIDElem1 = getIncidentrespDoc.createElement("SystemID");
            Text textSystemIDElem1 = getIncidentrespDoc.createTextNode(applicationSystemType2.getSystemID());
            systemIDElem1.appendChild(textSystemIDElem1);
            systemOfRecordElem.appendChild(systemIDElem1);
            *//** SystemID end **//*
           
            incidentDetailsElem.appendChild(systemOfRecordElem);
            *//** SystemOfRecord end **//*
                       
            *//** InitialDateTime start **//*
            Element initialDateTimeElem = getIncidentrespDoc.createElement("InitialDateTime");
            
            // this method gets the XMLGregorianCalendar from Calendar
            Calendar calendar = incidentDetailsReturnType.getInitialDateTime();
            XMLGregorianCalendar xmlgregorianCalendar = getXMLGregCalFromCalendar(calendar);
            
            Text textInitialDateTimeElem = getIncidentrespDoc.createTextNode(xmlgregorianCalendar.toString());
            initialDateTimeElem.appendChild(textInitialDateTimeElem);
            incidentDetailsElem.appendChild(initialDateTimeElem);
            *//** InitialDateTime end **//*
            
            *//** IncidentLocation start **//*
            Element incidentLocationElem = getIncidentrespDoc.createElement("IncidentLocation");
            
            LocationReturnType locationReturnType = incidentDetailsReturnType.getIncidentLocation();
            UTMCoordinateType coordinateType = locationReturnType.getUTMCoordinate();
            GeographicCoordinateType geographicCoordinateType = locationReturnType.getGeographicCoordinate();
            GeographicCoordinateLatitude geographicCoordinateLatitude = geographicCoordinateType.getGeographicCoordinateLatitude();
            GeographicCoordinateLongitude geographicCoordinateLongitude = geographicCoordinateType.getGeographicCoordinateLongitude();
            
            *//** GeographicCoordinate start **//*
            Element GeographicCoordinateElem = getIncidentrespDoc.createElement("GeographicCoordinate");
           
            *//** GeographicCoordinateLatitude start **//*
            Element GeographicCoordinateLatitudeElem = getIncidentrespDoc.createElement("GeographicCoordinateLatitude");
            
            *//** LatitudeDegreeValue start **//*
            Element LatitudeDegreeValueElem = getIncidentrespDoc.createElement("LatitudeDegreeValue");
            Text textLatitudeDegreeValueElem = getIncidentrespDoc.createTextNode(geographicCoordinateLatitude.getLatitudeDegreeValue().toString());
            LatitudeDegreeValueElem.appendChild(textLatitudeDegreeValueElem);
            GeographicCoordinateLatitudeElem.appendChild(LatitudeDegreeValueElem);
            *//** LatitudeDegreeValue end **//*
            
            *//** LatitudeMinuteValue start **//*
            Element LatitudeMinuteValueElem = getIncidentrespDoc.createElement("LatitudeMinuteValue");
            Text textLatitudeMinuteValueElem = getIncidentrespDoc.createTextNode(geographicCoordinateLatitude.getLatitudeMinuteValue().toString());
            LatitudeMinuteValueElem.appendChild(textLatitudeMinuteValueElem);
            GeographicCoordinateLatitudeElem.appendChild(LatitudeMinuteValueElem);
            *//** LatitudeMinuteValue end **//*
            
            *//** LatitudeSecondValue start **//*
            Element LatitudeSecondValueElem = getIncidentrespDoc.createElement("LatitudeSecondValue");
            Text textLatitudeSecondValueElem = getIncidentrespDoc.createTextNode(geographicCoordinateLatitude.getLatitudeSecondValue().toString());
            LatitudeSecondValueElem.appendChild(textLatitudeSecondValueElem);
            GeographicCoordinateLatitudeElem.appendChild(LatitudeSecondValueElem);
            *//** LatitudeDegreeValue end **//*
            
            *//** LatitudeDirection start **//*
            Element LatitudeDirectionElem = getIncidentrespDoc.createElement("LatitudeDirection");
            Text textLatitudeDirectionElem = getIncidentrespDoc.createTextNode(geographicCoordinateLatitude.getLatitudeDirection().getValue());
            LatitudeDirectionElem.appendChild(textLatitudeDirectionElem);
            GeographicCoordinateLatitudeElem.appendChild(LatitudeDirectionElem);
            *//** LatitudeDegreeValue end **//*
            
            GeographicCoordinateElem.appendChild(GeographicCoordinateLatitudeElem);
            *//** GeographicCoordinateLatitude end **//*
            
            *//** GeographicCoordinateLongitude start **//*
            Element GeographicCoordinateLongitudeElem = getIncidentrespDoc.createElement("GeographicCoordinateLongitude");
            
            *//** LongitudeDegreeValue start **//*
            Element LongitudeDegreeValueElem = getIncidentrespDoc.createElement("LongitudeDegreeValue");
            Text textLongitudeDegreeValueElem = getIncidentrespDoc.createTextNode(geographicCoordinateLongitude.getLongitudeDegreeValue().toString());
            LongitudeDegreeValueElem.appendChild(textLongitudeDegreeValueElem);
            GeographicCoordinateLongitudeElem.appendChild(LongitudeDegreeValueElem);
            *//** LongitudeDegreeValue end **//*
            
            *//** LongitudeMinuteValue start **//*
            Element LongitudeMinuteValueElem = getIncidentrespDoc.createElement("LongitudeMinuteValue");
            Text textLongitudeMinuteValueElem = getIncidentrespDoc.createTextNode(geographicCoordinateLongitude.getLongitudeMinuteValue().toString());
            LongitudeMinuteValueElem.appendChild(textLongitudeMinuteValueElem);
            GeographicCoordinateLongitudeElem.appendChild(LongitudeMinuteValueElem);
            *//** LongitudeMinuteValue end **//*
            
            *//** LongitudeSecondValue start **//*
            Element LongitudeSecondValueElem = getIncidentrespDoc.createElement("LongitudeSecondValue");
            Text textLongitudeSecondValueElemElem = getIncidentrespDoc.createTextNode(geographicCoordinateLongitude.getLongitudeSecondValue().toString());
            LongitudeSecondValueElem.appendChild(textLongitudeSecondValueElemElem);
            GeographicCoordinateLongitudeElem.appendChild(LongitudeSecondValueElem);
            *//** LongitudeSecondValue end **//*
            
            *//** LongitudeDirection start **//*
            Element LongitudeDirectionElem = getIncidentrespDoc.createElement("LongitudeDirection");
            Text textLongitudeDirectionElem = getIncidentrespDoc.createTextNode(geographicCoordinateLongitude.getLongitudeDirection().getValue());
            LongitudeDirectionElem.appendChild(textLongitudeDirectionElem);
            GeographicCoordinateLongitudeElem.appendChild(LongitudeDirectionElem);
            *//** LongitudeDirection end **//*
            
            GeographicCoordinateElem.appendChild(GeographicCoordinateLongitudeElem);
            *//** GeographicCoordinateLongitude end **//*
            //System.out.println("**** Longitude2");
            
            incidentLocationElem.appendChild(GeographicCoordinateElem);
            *//** GeographicCoordinate end **//*
            
            //System.out.println("**** TownshipRangeSectionCoordinateType");
            
            if (locationReturnType.getTRSCoordinate() != null){
            	
            TownshipRangeSectionCoordinateType townshipRangeSectionCoordinateType = locationReturnType.getTRSCoordinate();
            //System.out.println("locationReturnType: " + locationReturnType);
            //System.out.println("townshipRangeSectionCoordinateType: " + townshipRangeSectionCoordinateType);
            
            
            // TRSCoordinate start
            //System.out.println("**** TRSCoordinateElem");
            Element TRSCoordinateElem = getIncidentrespDoc.createElement("TRSCoordinate");
            
            *//** State start **//*
            //System.out.println("**** StateElem");
            Element StateElem = getIncidentrespDoc.createElement("State");
            //System.out.println("**** textStateElem");
            //System.out.println("**** textStateElem: " + townshipRangeSectionCoordinateType.getState().getValue());
            Text textStateElem = null;
            //System.out.println("got here");
            textStateElem = getIncidentrespDoc.createTextNode(townshipRangeSectionCoordinateType.getState().getValue());
            //System.out.println("**** StateElem.appendChild");
            StateElem.appendChild(textStateElem);
            //System.out.println("**** TRSCoordinateElem.appendChild");
            TRSCoordinateElem.appendChild(StateElem);
            *//** State end **//*
            
            *//** MeridianName start **//*
            //System.out.println("**** MeridianNameElem");
            Element MeridianNameElem = getIncidentrespDoc.createElement("MeridianName");
            //System.out.println("**** textMeridianNameElem");
            Text textMeridianNameElem = getIncidentrespDoc.createTextNode(townshipRangeSectionCoordinateType.getMeridianName().getValue());
            //System.out.println("**** MeridianNameElem.appendChild");
            MeridianNameElem.appendChild(textMeridianNameElem);
            //System.out.println("**** TRSCoordinateElem.appendChild");
            TRSCoordinateElem.appendChild(MeridianNameElem);
            *//** MeridianName end **//*
            //System.out.println("**** Meridian name");
            *//** Township start **//*
            Element TownshipElem = getIncidentrespDoc.createElement("Township");
            Text textTownshipElem = getIncidentrespDoc.createTextNode(townshipRangeSectionCoordinateType.getTownship());
            TownshipElem.appendChild(textTownshipElem);
            TRSCoordinateElem.appendChild(TownshipElem);
            *//** Township end **//*
            
            *//** TownshipDirection start **//*
            Element TownshipDirectionElem = getIncidentrespDoc.createElement("TownshipDirection");
            Text textTownshipDirectionElem = getIncidentrespDoc.createTextNode(townshipRangeSectionCoordinateType.getTownshipDirection().getValue());
            TownshipDirectionElem.appendChild(textTownshipDirectionElem);
            TRSCoordinateElem.appendChild(TownshipDirectionElem);
            *//** TownshipDirection end **//*
            
            *//** Range start **//*
            Element RangeElem = getIncidentrespDoc.createElement("Range");
            Text textRangeElem = getIncidentrespDoc.createTextNode(townshipRangeSectionCoordinateType.getRange());
            RangeElem.appendChild(textRangeElem);
            TRSCoordinateElem.appendChild(RangeElem);
            *//** Range end **//*
            
            *//** RangeDirection start **//*
            Element RangeDirectionElem = getIncidentrespDoc.createElement("RangeDirection");
            Text textRangeDirectionElem = getIncidentrespDoc.createTextNode(townshipRangeSectionCoordinateType.getRangeDirection().getValue());
            RangeDirectionElem.appendChild(textRangeDirectionElem);
            TRSCoordinateElem.appendChild(RangeDirectionElem);
            *//** RangeDirection end **//*
            //System.out.println("**** Rangedirection");
            
            *//** Section start **//*
            Element SectionElem = getIncidentrespDoc.createElement("Section");
            Text textSectionElem = getIncidentrespDoc.createTextNode(townshipRangeSectionCoordinateType.getSection().toString());
            SectionElem.appendChild(textSectionElem);
            TRSCoordinateElem.appendChild(SectionElem);
            *//** Range end **//*
            
            incidentLocationElem.appendChild(TRSCoordinateElem);
        }
            *//** TRSCoordinate end **//*
            
            *//** UTMCoordinate start **//*
            Element UTMCoordinateElem = getIncidentrespDoc.createElement("UTMCoordinate");
            
            *//** UTMGridZoneID start **//*
            if (coordinateType.getUTMGridZoneID() != null){
            
            Element UTMGridZoneIDElem = getIncidentrespDoc.createElement("UTMGridZoneID");
            Text TextUTMGridZoneIDElem = getIncidentrespDoc.createTextNode(coordinateType.getUTMGridZoneID());
            UTMGridZoneIDElem.appendChild(TextUTMGridZoneIDElem);
            UTMCoordinateElem.appendChild(UTMGridZoneIDElem);
            *//** UTMGridZoneID end **//*
            }
            
            *//** UTMEastingValue start **//*
            if (coordinateType.getUTMEastingValue() != null){
            Element UTMEastingValueElem = getIncidentrespDoc.createElement("UTMEastingValue");
            Text TextUTMEastingValueElem = getIncidentrespDoc.createTextNode(coordinateType.getUTMEastingValue().toString());
            UTMEastingValueElem.appendChild(TextUTMEastingValueElem);
            UTMCoordinateElem.appendChild(UTMEastingValueElem);
            *//** UTMEastingValue end **//*
            }
            
            *//** UTMNorthingValue start **//*
            if (coordinateType.getUTMNorthingValue() != null){
            Element UTMNorthingValueElem = getIncidentrespDoc.createElement("UTMNorthingValue");
            Text TextUTMNorthingValueElem = getIncidentrespDoc.createTextNode(coordinateType.getUTMNorthingValue().toString());
            UTMNorthingValueElem.appendChild(TextUTMNorthingValueElem);
            UTMCoordinateElem.appendChild(UTMNorthingValueElem);
            *//** UTMNorthingValue end **//*
            }
            
            *//** Hemisphere start **//*
            if (coordinateType.getHemisphere() != null){
            Element HemisphereElem = getIncidentrespDoc.createElement("Hemisphere");
            Text textHemisphereElem = getIncidentrespDoc.createTextNode(coordinateType.getHemisphere().getValue());
            HemisphereElem.appendChild(textHemisphereElem);
            UTMCoordinateElem.appendChild(HemisphereElem);
            *//** Hemisphere end **//*
            }
            
            incidentLocationElem.appendChild(UTMCoordinateElem);
            *//** UTMCoordinate end **//*
            
            incidentDetailsElem.appendChild(incidentLocationElem);
            *//** IncidentLocation end **//*
            //System.out.println("**** Incident Location");
            
            *//** LocationName start **//*
            Element LocationNameElem = getIncidentrespDoc.createElement("LocationName");
            Text textLocationNameElem = getIncidentrespDoc.createTextNode(incidentDetailsReturnType.getLocationName());
            LocationNameElem.appendChild(textLocationNameElem);
            incidentDetailsElem.appendChild(LocationNameElem);
            *//** LocationName end **//*
            
            *//** OfficeReferenceNumber start **//*
            Element OfficeReferenceNumberElem = getIncidentrespDoc.createElement("OfficeReferenceNumber");
            Text textOfficeReferenceNumberElem = getIncidentrespDoc.createTextNode(incidentDetailsReturnType.getOfficeReferenceNumber());
            OfficeReferenceNumberElem.appendChild(textOfficeReferenceNumberElem);
            incidentDetailsElem.appendChild(OfficeReferenceNumberElem);
            *//** OfficeReferenceNumber end **//*
            
            *//** Description start **//*
            Element DescriptionElem = getIncidentrespDoc.createElement("Description");
            Text textDescriptionElem = getIncidentrespDoc.createTextNode(incidentDetailsReturnType.getDescription());
            DescriptionElem.appendChild(textDescriptionElem);
            incidentDetailsElem.appendChild(DescriptionElem);
            *//** Description end **//*
            
            *//** OriginatingSystemUserData start **//*
            Element OriginatingSystemUserDataElem = getIncidentrespDoc.createElement("OriginatingSystemUserData");
            Text textOriginatingSystemUserDataElem = getIncidentrespDoc.createTextNode(incidentDetailsReturnType.getOriginatingSystemUserData());
            OriginatingSystemUserDataElem.appendChild(textDescriptionElem);
            incidentDetailsElem.appendChild(OriginatingSystemUserDataElem);
            *//** OriginatingSystemUserData end **//*
            
            *//** isComplexIndicator start **//*
            Element isComplexIndicatorElem = getIncidentrespDoc.createElement("isComplexIndicator");
            Text textisComplexIndicatorElem = getIncidentrespDoc.createTextNode(Boolean.toString(incidentDetailsReturnType.isIsComplexIndicator()));
            isComplexIndicatorElem.appendChild(textisComplexIndicatorElem);
            incidentDetailsElem.appendChild(isComplexIndicatorElem);
            *//** isComplexIndicator end **//*
            
            *//** DispatchOrganization start **//*
            Element DispatchOrganizationElem = getIncidentrespDoc.createElement("DispatchOrganization");
            
            UnitIDType unitIDType2 = incidentDetailsReturnType.getDispatchOrganization();
            
            *//** UnitIDPrefix start **//*
            Element UnitIDPrefixElem = getIncidentrespDoc.createElement("UnitIDPrefix");
            Text textUnitIDPrefixElem = getIncidentrespDoc.createTextNode(unitIDType2.getUnitIDPrefix());
            UnitIDPrefixElem.appendChild(textUnitIDPrefixElem);
            DispatchOrganizationElem.appendChild(UnitIDPrefixElem);
            *//** UnitIDPrefix end **//*
            //System.out.println("**** UnitID Prefix");
            
            *//** UnitIDPrefix start **//*
            Element UnitIDSuffixElem = getIncidentrespDoc.createElement("UnitIDSuffix");
            Text textUnitIDSuffixElem = getIncidentrespDoc.createTextNode(unitIDType2.getUnitIDSuffix());
            UnitIDSuffixElem.appendChild(textUnitIDSuffixElem);
            DispatchOrganizationElem.appendChild(UnitIDSuffixElem);
            *//** UnitIDPrefix end **//*
            
            incidentDetailsElem.appendChild(DispatchOrganizationElem);
            *//** DispatchOrganization end **//*
            
            *//** BillingOrganization start **//*
            Element BillingOrganizationElem = getIncidentrespDoc.createElement("BillingOrganization");
            
            UnitIDType unitIDType3 = incidentDetailsReturnType.getBillingOrganization();
            
            *//** UnitIDPrefix start **//*
            Element BillUnitIDPrefixElem = getIncidentrespDoc.createElement("UnitIDPrefix");
            Text textBillUnitIDPrefixElem = getIncidentrespDoc.createTextNode(unitIDType3.getUnitIDPrefix());
            BillUnitIDPrefixElem.appendChild(textBillUnitIDPrefixElem);
            BillingOrganizationElem.appendChild(BillUnitIDPrefixElem);
            *//** UnitIDPrefix end **//*
            
            *//** UnitIDPrefix start **//*
            Element BillUnitIDSuffixElem = getIncidentrespDoc.createElement("UnitIDSuffix");
            Text textBillUnitIDSuffixElem = getIncidentrespDoc.createTextNode(unitIDType3.getUnitIDSuffix());
            BillUnitIDSuffixElem.appendChild(textBillUnitIDSuffixElem);
            BillingOrganizationElem.appendChild(BillUnitIDSuffixElem);
            *//** UnitIDPrefix end **//*
            
            incidentDetailsElem.appendChild(BillingOrganizationElem);
            *//** BillingOrganization end **//*
            
            *//** Status start **//*
            Element statusElem = getIncidentrespDoc.createElement("Status");
            Text textStatusElem = getIncidentrespDoc.createTextNode(incidentDetailsReturnType.getStatus().getValue());
            statusElem.appendChild(textStatusElem);
            incidentDetailsElem.appendChild(statusElem);
            *//** Status end **//*
            
            incidentElem.appendChild(incidentDetailsElem);
            *//** IncidentDetails end **//*
            //System.out.println("**** Incident details end 827");
            
            *//** IncidentEntities start **//*
            if (incidentReturnType.getIncidentEntities() != null){
            Element incidentEntitiesElem = getIncidentrespDoc.createElement("IncidentEntities");
            
            IncidentEntitiesReturnType incidentEntitiesReturnType = incidentReturnType.getIncidentEntities();
            
            if(incidentEntitiesReturnType.getIncidentRadioFrequencies() != null){
            IncidentRadioFrequencyReturnType[] incidentRadioFrequencyReturnTypeArr = incidentEntitiesReturnType.getIncidentRadioFrequencies();
            for(int len=0;len<incidentRadioFrequencyReturnTypeArr.length;len++){
                
            	//System.out.println("**** in incidentRadioFrequencyReturnTypeArr for loop iteration: " + len);
            	
                IncidentRadioFrequencyReturnType incidentRadioFrequencyReturnType = incidentRadioFrequencyReturnTypeArr[len];
                CompositeIncidentRadioFrequencyKeyType compositeIncidentRadioFrequencyKeyType = incidentRadioFrequencyReturnType.getIncidentRadioFrequencyKey();
                IncidentRadioFrequencyNaturalKeyType incidentRadioFrequencyNaturalKeyType = compositeIncidentRadioFrequencyKeyType.getRadioFrequencyNaturalKey();
                CompositeIncidentKeyType compositeIncidentKeyType2 = incidentRadioFrequencyNaturalKeyType.getIncidentKey();
                IncidentNumberType incidentNumberType2 = compositeIncidentKeyType2.getNaturalIncidentKey();
                UnitIDType unitIDType4 = incidentNumberType2.getHostID();
                IDType type1 = compositeIncidentKeyType2.getIncidentID();
                
                *//** IncidentRadioFrequencies start **//*
                Element IncidentRadioFrequenciesElem = getIncidentrespDoc.createElement("IncidentRadioFrequencies");
                
                *//** IncidentRadioFrequencyKey start **//*
                Element IncidentRadioFrequencyKeyElem = getIncidentrespDoc.createElement("IncidentRadioFrequencyKey");
                
                *//** RadioFrequencyNaturalKey start **//*
                Element RadioFrequencyNaturalKeyElem = getIncidentrespDoc.createElement("RadioFrequencyNaturalKey");
                
                *//** IncidentKey start **//*
                Element IncidentKeyElem = getIncidentrespDoc.createElement("IncidentKey");
                
                *//** IncidentID start **//*
                Element IncidentIDElem = getIncidentrespDoc.createElement("IncidentID");
                
                *//** EntityType start **//*
                Element EntityTypeElem1 = getIncidentrespDoc.createElement("EntityType");
                Text textEntityTypeElem1 = getIncidentrespDoc.createTextNode();
                EntityTypeElem1.appendChild(textEntityTypeElem1);
                IncidentIDElem.appendChild(EntityTypeElem1);
                *//** EntityType end **//*
                
                *//** EntityID start **//*
                Element EntityIDElem1 = getIncidentrespDoc.createElement("EntityID");
                Text textEntityIDElem1 = getIncidentrespDoc.createTextNode();
                EntityIDElem1.appendChild(textEntityIDElem1);
                IncidentIDElem.appendChild(EntityIDElem1);
                *//** EntityID end **//*
                
                *//** ApplicationSystem start **//*
                Element ApplicationSystemElem = getIncidentrespDoc.createElement("ApplicationSystem");
                
                IncidentIDElem.appendChild(ApplicationSystemElem);
                *//** ApplicationSystem end **//*
                
                IncidentKeyElem.appendChild(IncidentIDElem);
                *//** IncidentID end **//*
                
                *//** NaturalIncidentKey start **//*
                Element NaturalIncidentKeyElem = getIncidentrespDoc.createElement("NaturalIncidentKey");
                
                *//** HostID start **//*
                Element HostIDElem = getIncidentrespDoc.createElement("HostID");
                
                *//** UnitIDPrefix start **//*
                Element hostIDUnitIDPrefix = getIncidentrespDoc.createElement("UnitIDPrefix");
                Text texthostIDUnitIDPrefix = getIncidentrespDoc.createTextNode(unitIDType4.getUnitIDPrefix());
                hostIDUnitIDPrefix.appendChild(texthostIDUnitIDPrefix);
                HostIDElem.appendChild(hostIDUnitIDPrefix);
                *//** UnitIDPrefix end **//*
                
                *//** UnitIDSuffix start **//*
                Element hostIDUnitIDSuffix = getIncidentrespDoc.createElement("UnitIDSuffix");
                Text texthostIDUnitIDSuffix = getIncidentrespDoc.createTextNode(unitIDType4.getUnitIDSuffix());
                hostIDUnitIDSuffix.appendChild(texthostIDUnitIDSuffix);
                HostIDElem.appendChild(hostIDUnitIDSuffix);
                *//** UnitIDSuffix end **//*
                
                NaturalIncidentKeyElem.appendChild(HostIDElem);
                *//** HostID end **//*
                
                *//** SequenceNumber start **//*
                Element SequenceNumberElem = getIncidentrespDoc.createElement("SequenceNumber");
                Text textSequenceNumberElem = getIncidentrespDoc.createTextNode(Long.toString(incidentNumberType2.getSequenceNumber()));
                SequenceNumberElem.appendChild(textSequenceNumberElem);
                NaturalIncidentKeyElem.appendChild(SequenceNumberElem);
                *//** SequenceNumber end **//*
                
                *//** YearCreated start **//*
                Element YearCreatedElem = getIncidentrespDoc.createElement("YearCreated");
                Text textYearCreated = getIncidentrespDoc.createTextNode(incidentNumberType2.getYearCreated());
                YearCreatedElem.appendChild(textYearCreated);
                NaturalIncidentKeyElem.appendChild(YearCreatedElem);
                *//** YearCreated end **//*
                
                IncidentKeyElem.appendChild(NaturalIncidentKeyElem);
                *//** NaturalIncidentKey end **//*
                
                RadioFrequencyNaturalKeyElem.appendChild(IncidentKeyElem);
                *//** IncidentKey end **//*
                
                *//** FrequencyType start **//*
                Element FrequencyTypeElem = getIncidentrespDoc.createElement("FrequencyType");
                Text textFrequencyTypeElem = getIncidentrespDoc.createTextNode(incidentRadioFrequencyNaturalKeyType.getFrequencyType());
                FrequencyTypeElem.appendChild(textFrequencyTypeElem);
                RadioFrequencyNaturalKeyElem.appendChild(FrequencyTypeElem);
                *//** FrequencyType end **//*
                
                *//** FrequencyValue start **//*
                Element FrequencyValueElem = getIncidentrespDoc.createElement("FrequencyValue");
                Text textFrequencyValueElem = getIncidentrespDoc.createTextNode(incidentRadioFrequencyNaturalKeyType.getFrequencyValue());
                FrequencyValueElem.appendChild(textFrequencyValueElem);
                RadioFrequencyNaturalKeyElem.appendChild(FrequencyValueElem);
                *//** FrequencyValue end **//*
                
                IncidentRadioFrequencyKeyElem.appendChild(RadioFrequencyNaturalKeyElem);
                *//** RadioFrequencyNaturalKey end **//*
                
                *//** RadioFrequencyID start **//*
                Element RadioFrequencyIDElem = getIncidentrespDoc.createElement("RadioFrequencyID");
                
                IDType type2 = compositeIncidentRadioFrequencyKeyType.getRadioFrequencyID();
                
                *//** EntityType start **//*
                Element EntityTypeElem = getIncidentrespDoc.createElement("EntityType");
                Text textEntityTypeElem = getIncidentrespDoc.createTextNode(type2.getEntityType().getValue());
                EntityTypeElem.appendChild(textEntityTypeElem);
                RadioFrequencyIDElem.appendChild(EntityTypeElem);
                *//** EntityType end **//*
                
                *//** EntityID start **//*
                Element EntityIDElem = getIncidentrespDoc.createElement("EntityID");
                Text textEntityID = getIncidentrespDoc.createTextNode(type2.getEntityID());
                EntityIDElem.appendChild(textEntityID);
                RadioFrequencyIDElem.appendChild(EntityIDElem);
                *//** EntityID end **//*
                
                *//** ApplicationSystem start **//*
                Element ApplicationSystemElem = getIncidentrespDoc.createElement("ApplicationSystem");
                
                *//** System Type start **//*
                Element SystemTypeElem = getIncidentrespDoc.createElement("SystemType");
                Text textSystemTypeelem = getIncidentrespDoc.createTextNode(type2.getApplicationSystem().getSystemType().getValue());
                SystemTypeElem.appendChild(textSystemTypeelem);
                ApplicationSystemElem.appendChild(SystemTypeElem);
                *//** System Type end **//*
                
                RadioFrequencyIDElem.appendChild(ApplicationSystemElem);
                *//** ApplicationSystem end **//*
                
                IncidentRadioFrequencyKeyElem.appendChild(RadioFrequencyIDElem);
                *//** RadioFrequencyID end **//*
                
                IncidentRadioFrequenciesElem.appendChild(IncidentRadioFrequencyKeyElem);
                *//** IncidentRadioFrequencyKey end **//*
                
                *//** PrimaryIndicator start **//*
                Element PrimaryIndicatorElem = getIncidentrespDoc.createElement("PrimaryIndicator");
                Text textPrimaryIndicatorElem = getIncidentrespDoc.createTextNode(Boolean.toString(incidentRadioFrequencyReturnType.isPrimaryIndicator()));
                PrimaryIndicatorElem.appendChild(textPrimaryIndicatorElem);
                IncidentRadioFrequenciesElem.appendChild(PrimaryIndicatorElem);
                *//** PrimaryIndicator end **//*
                
                incidentEntitiesElem.appendChild(IncidentRadioFrequenciesElem);
                *//** IncidentRadioFrequencies end **//*
                
            }
            }
            //System.out.println("**** out of incidentradiofrequency for loop");
            
            
            if(incidentEntitiesReturnType.getDocumentation() != null){
            DocumentationReturnType[] documentationReturnTypesArr = incidentEntitiesReturnType.getDocumentation();
            for(int l=0;l<documentationReturnTypesArr.length;l++){
            	
            	//System.out.println("**** In documentationReturnTypesArr for loop iteration: " + l);
                DocumentationReturnType documentationReturnType = documentationReturnTypesArr[l];
                
                *//** Documentation start **//*
                Element DocumentationElem = getIncidentrespDoc.createElement("Documentation");
                
                *//** Date start **//*
                Element DateElem = getIncidentrespDoc.createElement("Date");
                
                XMLGregorianCalendar gregorianCalendar = getXMLGregCalFromCalendar(documentationReturnType.getDate());
                
                Text textDateElem = getIncidentrespDoc.createTextNode(gregorianCalendar.toString());
                DateElem.appendChild(textDateElem);
                DocumentationElem.appendChild(DateElem);
                *//** Date end **//*
                
                *//** SystemName start **//*
                Element SystemNameElem = getIncidentrespDoc.createElement("SystemName");
                Text textSystemNameElem = getIncidentrespDoc.createTextNode(documentationReturnType.getSystemName().getValue());
                SystemNameElem.appendChild(textSystemNameElem);
                DocumentationElem.appendChild(SystemNameElem);
                *//** SystemName end **//*
                
                *//** DispatchUnitID start **//*
                Element DispatchUnitIDElem = getIncidentrespDoc.createElement("DispatchUnitID");
                
                UnitIDType unitIDType4 = documentationReturnType.getDispatchUnitID();
                
                *//** UnitIDPrefix start **//*
                Element dispatchUnitIDPrefixElem = getIncidentrespDoc.createElement("UnitIDPrefix");
                Text textdispatchSystemNameElem = getIncidentrespDoc.createTextNode(unitIDType4.getUnitIDPrefix());
                dispatchUnitIDPrefixElem.appendChild(textdispatchSystemNameElem);
                DispatchUnitIDElem.appendChild(dispatchUnitIDPrefixElem);
                *//** UnitIDPrefix end **//*
                
                *//** UnitIDSuffix start **//*
                Element dispatchUnitIDSuffixElem = getIncidentrespDoc.createElement("UnitIDSuffix");
                Text textdispatchUnitIDSuffixElem = getIncidentrespDoc.createTextNode(unitIDType4.getUnitIDSuffix());
                dispatchUnitIDSuffixElem.appendChild(textdispatchUnitIDSuffixElem);
                DispatchUnitIDElem.appendChild(dispatchUnitIDSuffixElem);
                *//** UnitIDSuffix end **//*
                
                DocumentationElem.appendChild(DispatchUnitIDElem);
                *//** DispatchUnitID start **//*
                
                *//** Author start **//*
                Element authorElem = getIncidentrespDoc.createElement("Author");
                Text textAuthorElem = getIncidentrespDoc.createTextNode(documentationReturnType.getAuthor());
                authorElem.appendChild(textAuthorElem);
                DocumentationElem.appendChild(authorElem);
                *//** Author end **//*
                
                *//** Text start **//*
                Element textElem = getIncidentrespDoc.createElement("Text");
                Text textTextElem = getIncidentrespDoc.createTextNode(documentationReturnType.getText());
                textElem.appendChild(textTextElem);
                DocumentationElem.appendChild(textElem);
                *//** Text end **//*
                
                incidentEntitiesElem.appendChild(DocumentationElem);
                *//** Documentation end **//*
                
            }
            }
            //System.out.println("**** Out of documentationReturnTypeblah for loop");
            
            
            if(incidentEntitiesReturnType.getIncidentAviationHazards() != null){
            IncidentAviationHazardReturnType[] incidentAviationHazardReturnTypeArr = incidentEntitiesReturnType.getIncidentAviationHazards();
            for(int a=0;a<incidentAviationHazardReturnTypeArr.length;a++){
            	//System.out.println("**** In incidentAviation for loop iteration: " + a);
                
                IncidentAviationHazardReturnType incidentAviationHazardReturnType = incidentAviationHazardReturnTypeArr[a];
                CompositeIncidentAviationHazardKeyType compositeIncidentAviationHazardKeyType = incidentAviationHazardReturnType.getIncidentAviationHazardKey();
                IDType type2 = compositeIncidentAviationHazardKeyType.getIncidentAviationHazardID();
                
                
                *//**  IncidentAviationHazards start **//*
                Element IncidentAviationHazardsElem = getIncidentrespDoc.createElement("IncidentAviationHazards");
                
                *//** IncidentAviationHazardKey start **//*
                Element IncidentAviationHazardKeyElem = getIncidentrespDoc.createElement("IncidentAviationHazardKey");
                
                *//** IncidentAviationHazardID start **//*
                Element IncidentAviationHazardIDElem = getIncidentrespDoc.createElement("IncidentAviationHazardID");
                
                *//** EntityType start **//*
                Element EntityTypeElem = getIncidentrespDoc.createElement("EntityType");
                Text textEntityTypeElem = getIncidentrespDoc.createTextNode(type2.getEntityType().getValue());
                EntityTypeElem.appendChild(textEntityTypeElem);
                IncidentAviationHazardIDElem.appendChild(EntityTypeElem);
                *//** EntityType end **//*
                
                *//** EntityID start **//*
                Element EntityIDElem = getIncidentrespDoc.createElement("EntityID");
                Text textEntityID = getIncidentrespDoc.createTextNode(type2.getEntityID());
                EntityIDElem.appendChild(textEntityID);
                IncidentAviationHazardIDElem.appendChild(EntityIDElem);
                *//** EntityID end **//*
                
                *//** ApplicationSystem start **//*
                Element ApplicationSystemElem = getIncidentrespDoc.createElement("ApplicationSystem");
                
                *//** SystemType start **//*
                Element SystemTypeElem = getIncidentrespDoc.createElement("SystemType");
                Text textSystemType = getIncidentrespDoc.createTextNode(type2.getApplicationSystem().getSystemType().getValue());
                SystemTypeElem.appendChild(textSystemType);
                ApplicationSystemElem.appendChild(SystemTypeElem);
                *//** SystemType end **//*
                
                IncidentAviationHazardIDElem.appendChild(ApplicationSystemElem);
                *//** ApplicationSystem end **//*
                
                IncidentAviationHazardKeyElem.appendChild(IncidentAviationHazardIDElem);
                *//** IncidentAviationHazardID end **//*
                
                IncidentAviationHazardNaturalKeyType incidentAviationHazardNaturalKeyType = compositeIncidentAviationHazardKeyType.getIncidentAviationHazardNaturalKey();
                CompositeIncidentKeyType compositeIncidentKeyType2 = incidentAviationHazardNaturalKeyType.getIncidentKey();
                IncidentNumberType incidentNumberType2 = compositeIncidentKeyType2.getNaturalIncidentKey();
                UnitIDType unitIDType4 = incidentNumberType2.getHostID();
                
                *//** IncidentAviationHazardNaturalKey start **//*
                Element IncidentAviationHazardNaturalKeyElem = getIncidentrespDoc.createElement("IncidentAviationHazardNaturalKey");
                
                *//** IncidentKey start **//*
                Element IncidentKeyElem = getIncidentrespDoc.createElement("IncidentKey");
                
                *//** NaturalIncidentKey start **//*
                Element NaturalIncidentKeyElem = getIncidentrespDoc.createElement("NaturalIncidentKey");
                
                *//** HostID start **//*
                Element HostIDElem = getIncidentrespDoc.createElement("HostID");
                
                *//** UnitIDPrefix start **//*
                Element HostIDUnitIDPrefixElem = getIncidentrespDoc.createElement("UnitIDPrefix");
                Text textHostIDUnitIDPrefixElem = getIncidentrespDoc.createTextNode(unitIDType4.getUnitIDPrefix());
                HostIDUnitIDPrefixElem.appendChild(textHostIDUnitIDPrefixElem);
                HostIDElem.appendChild(HostIDUnitIDPrefixElem);
                *//** UnitIDPrefix end **//*
                
                *//** UnitIDSuffix start **//*
                Element HostIDUnitIDSuffixElem = getIncidentrespDoc.createElement("UnitIDSuffix");
                Text textHostIDUnitIDSuffixElem = getIncidentrespDoc.createTextNode(unitIDType4.getUnitIDSuffix());
                HostIDUnitIDSuffixElem.appendChild(textHostIDUnitIDSuffixElem);
                HostIDElem.appendChild(HostIDUnitIDSuffixElem);
                *//** UnitIDSuffix end **//*
                
                NaturalIncidentKeyElem.appendChild(HostIDElem);
                *//** HostID end **//*
                
                *//** SequenceNumber start **//*
                Element SequenceNumberElem = getIncidentrespDoc.createElement("SequenceNumber");
                Text textSequenceNumberElem = getIncidentrespDoc.createTextNode(Long.toString(incidentNumberType2.getSequenceNumber()));
                SequenceNumberElem.appendChild(textSequenceNumberElem);
                NaturalIncidentKeyElem.appendChild(SequenceNumberElem);
                *//** SequenceNumber end **//*
                
                *//** YearCreated start **//*
                Element YearCreatedElem = getIncidentrespDoc.createElement("YearCreated");
                Text textYearCreated = getIncidentrespDoc.createTextNode(incidentNumberType2.getYearCreated());
                YearCreatedElem.appendChild(textYearCreated);
                NaturalIncidentKeyElem.appendChild(YearCreatedElem);
                *//** YearCreated end **//*
                
                IncidentKeyElem.appendChild(NaturalIncidentKeyElem);
                *//** NaturalIncidentKey end **//*
                
                IncidentAviationHazardNaturalKeyElem.appendChild(IncidentKeyElem);
                *//** IncidentKey end **//*
                
                
                IncidentAviationHazardKeyElem.appendChild(IncidentAviationHazardNaturalKeyElem);
                *//** IncidentAviationHazardNaturalKey end **//*
                
                IncidentAviationHazardsElem.appendChild(IncidentAviationHazardKeyElem);
                *//** IncidentAviationHazardKey end **//*
                
                IncidentAviationHazardRespType incidentAviationHazardRespType = incidentAviationHazardReturnType.getIncidentAviationHazard();
                
                
                *//** IncidentAviationHazard start **//*
                Element IncidentAviationHazardElem = getIncidentrespDoc.createElement("IncidentAviationHazard");
                
                *//** HazardType start **//*
                Element HazardTypeElem = getIncidentrespDoc.createElement("HazardType");
                Text textHazardTypeElem = getIncidentrespDoc.createTextNode(incidentAviationHazardRespType.getHazardType());
                HazardTypeElem.appendChild(textHazardTypeElem);
                IncidentAviationHazardElem.appendChild(HazardTypeElem);
                *//** HazardType end **//*
                
                *//** HazardLocation start **//*
                Element HazardLocationElem = getIncidentrespDoc.createElement("HazardLocation");
                Text textHazardLocationElem = getIncidentrespDoc.createTextNode(""); // still in doubt..
                HazardLocationElem.appendChild(textHazardLocationElem);
                IncidentAviationHazardElem.appendChild(HazardLocationElem);
                *//** HazardLocation end **//*
                
                *//** Description start **//*
                Element DescriptionElem1 = getIncidentrespDoc.createElement("Description");
                Text textDescriptionElem1 = getIncidentrespDoc.createTextNode(incidentAviationHazardRespType.getDescription());
                DescriptionElem1.appendChild(textDescriptionElem1);
                IncidentAviationHazardElem.appendChild(DescriptionElem1);
                *//** Description end **//*
                
                *//** City start **//*
                Element CityElem1 = getIncidentrespDoc.createElement("City");
                Text textCityElem1 = getIncidentrespDoc.createTextNode(incidentAviationHazardRespType.getCity());
                CityElem1.appendChild(textCityElem1);
                IncidentAviationHazardElem.appendChild(CityElem1);
                *//** City end **//*
                
                *//** State start **//*
                Element StateElem1 = getIncidentrespDoc.createElement("State");
                Text textStateElem1 = getIncidentrespDoc.createTextNode(incidentAviationHazardRespType.getState());
                StateElem1.appendChild(textStateElem1);
                IncidentAviationHazardElem.appendChild(StateElem1);
                *//** state end **//*
                
                IncidentAviationHazardsElem.appendChild(IncidentAviationHazardElem);
                *//** IncidentAviationHazard end **//*
                
                incidentEntitiesElem.appendChild(IncidentAviationHazardsElem);
                *//**  IncidentAviationHazards end **//*
                
            }
            }
            //System.out.println("**** Out of aviation for loop");
            
            
            if(incidentEntitiesReturnType.getIncidentAddresses() != null){
            IncidentAddressType[] incidentAddressTypeArr = incidentEntitiesReturnType.getIncidentAddresses();
            for(int b=0;b<incidentAddressTypeArr.length;b++){
            	//System.out.println("**** In incidentAddressType for loop iteration: " + b);
            	
                IncidentAddressType incidentAddressType = incidentAddressTypeArr[b];
                AddressType addressType = incidentAddressType.getAddress();
                
                *//** IncidentAddresses start **//*
                Element IncidentAddressesElem = getIncidentrespDoc.createElement("IncidentAddresses");
                
                *//** Address start **//*
                Element AddressesElem = getIncidentrespDoc.createElement("Address");
                
                *//** Type start **//*
                Element TypeElem = getIncidentrespDoc.createElement("Type");
                Text textTypeElem = getIncidentrespDoc.createTextNode(addressType.getType().getValue());
                TypeElem.appendChild(textTypeElem);
                AddressesElem.appendChild(TypeElem);
                *//** Type end **//*
                
                *//** Line1 start **//*
                Element Line1Elem = getIncidentrespDoc.createElement("Line1");
                Text textLine1Elem = getIncidentrespDoc.createTextNode(addressType.getLine1());
                Line1Elem.appendChild(textLine1Elem);
                AddressesElem.appendChild(Line1Elem);
                *//** Line1 end **//*
                
                *//** Line2 start **//*
                Element Line2Elem = getIncidentrespDoc.createElement("Line2");
                Text textLine2Elem = getIncidentrespDoc.createTextNode(addressType.getLine2());
                Line2Elem.appendChild(textLine2Elem);
                AddressesElem.appendChild(Line2Elem);
                *//** Line2 end **//*
                
                *//** City start **//*
                Element CityElem = getIncidentrespDoc.createElement("City");
                Text textCityElem = getIncidentrespDoc.createTextNode(addressType.getCity());
                CityElem.appendChild(textCityElem);
                AddressesElem.appendChild(CityElem);
                *//** City end **//*
                
                *//** State start **//*
                Element StateElem1 = getIncidentrespDoc.createElement("State");
                Text textStateElem1 = getIncidentrespDoc.createTextNode(addressType.getState());
                StateElem1.appendChild(textStateElem1);
                AddressesElem.appendChild(StateElem1);
                *//** State end **//*
                
                *//** ZipCode start **//*
                Element ZipCodeElem = getIncidentrespDoc.createElement("ZipCode");
                Text textZipCodeElem = getIncidentrespDoc.createTextNode(addressType.getZipCode());
                ZipCodeElem.appendChild(textZipCodeElem);
                AddressesElem.appendChild(ZipCodeElem);
                *//** ZipCode end **//*
                
                *//** CountryCode start **//*
                Element CountryCodeElem = getIncidentrespDoc.createElement("CountryCode");
                Text textCountryCodeElem = getIncidentrespDoc.createTextNode(addressType.getCountryCode());
                CountryCodeElem.appendChild(textCountryCodeElem);
                AddressesElem.appendChild(CountryCodeElem);
                *//** CountryCode end **//*
                
                IncidentAddressesElem.appendChild(AddressesElem);
                *//** Addresses end **//*
                
                *//** PrimaryIndicator start **//*
                Element PrimaryIndicatorElem = getIncidentrespDoc.createElement("PrimaryIndicator");
                Text textPrimaryIndicatorElem = getIncidentrespDoc.createTextNode(Boolean.toString(incidentAddressType.isPrimaryInd()));
                PrimaryIndicatorElem.appendChild(textPrimaryIndicatorElem);
                IncidentAddressesElem.appendChild(PrimaryIndicatorElem);
                *//** PrimaryIndicator end **//*
                
                incidentEntitiesElem.appendChild(IncidentAddressesElem);
                *//** IncidentAddresses end **//*
            }
            }
            
            //System.out.println("**** out of address for loop");
            
            if(incidentEntitiesReturnType.getIncidentRequestBlocks() != null){
            IncidentRequestBlockType[] incidentRequestBlockTypeArr = incidentEntitiesReturnType.getIncidentRequestBlocks();
            for(int c=0;c<incidentRequestBlockTypeArr.length;c++){
            	//System.out.println("**** In requestblock for loop iteration: " + c);
            	
                IncidentRequestBlockType incidentRequestBlockType = incidentRequestBlockTypeArr[c];
                ApplicationSystemType applicationSystemType3 = incidentRequestBlockType.getForUseByAppSystem();
                
                *//** IncidentRequestBlocks start **//*
                Element IncidentRequestBlocksElem = getIncidentrespDoc.createElement("IncidentRequestBlocks");
                
                *//** Catalog start **//*
                Element CatalogElem = getIncidentrespDoc.createElement("Catalog");
                Text textCatalogElem = getIncidentrespDoc.createTextNode(incidentRequestBlockType.getCatalog().getValue());
                CatalogElem.appendChild(textCatalogElem);
                IncidentRequestBlocksElem.appendChild(CatalogElem);
                *//** Catalog end **//*
                
                *//** Name start **//*
                Element NameElem = getIncidentrespDoc.createElement("Name");
                Text textNameElem = getIncidentrespDoc.createTextNode(incidentRequestBlockType.getName());
                NameElem.appendChild(textNameElem);
                IncidentRequestBlocksElem.appendChild(NameElem);
                *//** Name end **//*
                
                *//** StartNumber start **//*
                Element StartNumberElem = getIncidentrespDoc.createElement("StartNumber");
                Text textStartNumberElem = getIncidentrespDoc.createTextNode(incidentRequestBlockType.getStartNumber().toString());
                StartNumberElem.appendChild(textStartNumberElem);
                IncidentRequestBlocksElem.appendChild(StartNumberElem);
                *//** StartNumber end **//*
                
                *//** EndNumber start **//*
                Element EndNumberElem = getIncidentrespDoc.createElement("EndNumber");
                Text textEndNumberElem = getIncidentrespDoc.createTextNode(incidentRequestBlockType.getEndNumber().toString());
                EndNumberElem.appendChild(textEndNumberElem);
                IncidentRequestBlocksElem.appendChild(EndNumberElem);
                *//** EndNumber end **//*
                
                *//** Purpose start **//*
                Element PurposeElem = getIncidentrespDoc.createElement("Purpose");
                Text textPurposeElem = getIncidentrespDoc.createTextNode(incidentRequestBlockType.getPurpose());
                PurposeElem.appendChild(textPurposeElem);
                IncidentRequestBlocksElem.appendChild(PurposeElem);
                *//** Purpose end **//*
                
                *//** ForUseByAppSystem start **//*
                Element ForUseByAppSystemElem = getIncidentrespDoc.createElement("ForUseByAppSystem");
                
                *//** SystemType start **//*
                Element SystemTypeElem = getIncidentrespDoc.createElement("SystemType");
                Text textSystemTypeElem2 = getIncidentrespDoc.createTextNode(applicationSystemType3.getSystemType().getValue());
                SystemTypeElem.appendChild(textSystemTypeElem2);
                ForUseByAppSystemElem.appendChild(SystemTypeElem);
                *//** SystemType end **//*
                
                *//** SystemID start **//*
                Element SystemIDElem = getIncidentrespDoc.createElement("SystemID");
                Text textSystemIDElem2 = getIncidentrespDoc.createTextNode(applicationSystemType3.getSystemID());
                SystemIDElem.appendChild(textSystemIDElem2);
                ForUseByAppSystemElem.appendChild(SystemIDElem);
                *//** SystemID end **//*
                
                IncidentRequestBlocksElem.appendChild(ForUseByAppSystemElem);
                *//** ForUseByAppSystem end **//*
                
                incidentEntitiesElem.appendChild(IncidentRequestBlocksElem);
                *//** IncidentRequestBlocks end **//*
                
            }
            }
            //System.out.println("**** Out of requestblock for loop");
            
            if(incidentEntitiesReturnType.getIncidentFinancialCodes() != null){
            IncidentFinancialCodeType[] incidentFinancialCodeTypeArr = incidentEntitiesReturnType.getIncidentFinancialCodes();
            for(int d=0;d<incidentFinancialCodeTypeArr.length;d++){
            	//System.out.println("**** In financial for loop iteration" + d);
            	
                IncidentFinancialCodeType incidentFinancialCodeType = incidentFinancialCodeTypeArr[d];
                FinancialCodeType financialCodeType = incidentFinancialCodeType.getFinancialCode();
                
                *//** FinancialCodes start **//*
                Element FinancialCodesElem = getIncidentrespDoc.createElement("IncidentFinancialCodes");
                
                *//** FinancialCode start **//*
                Element FinancialCodeElem = getIncidentrespDoc.createElement("FinancialCode");
                
                *//** FinancialCode child start **//*
                Element FinancialCodeChildElem = getIncidentrespDoc.createElement("FinancialCode");
                Text textFinancialCodeChildElem = getIncidentrespDoc.createTextNode(financialCodeType.getCode());
                FinancialCodeChildElem.appendChild(textFinancialCodeChildElem);
                FinancialCodeElem.appendChild(FinancialCodeChildElem);
                *//** FinancialCode child start **//*
                
                *//** OwningAgencyName start **//*
                Element OwningAgencyNameElem = getIncidentrespDoc.createElement("OwningAgencyName");
                Text textOwningAgencyNameElem = getIncidentrespDoc.createTextNode(financialCodeType.getOwningAgencyName());
                OwningAgencyNameElem.appendChild(textOwningAgencyNameElem);
                FinancialCodeElem.appendChild(OwningAgencyNameElem);
                *//** OwningAgencyName start **//*
                
                *//** FiscalYear child start **//*
                Element FiscalYearElem = getIncidentrespDoc.createElement("FiscalYear");
                Text textFiscalYearElem = getIncidentrespDoc.createTextNode(financialCodeType.getFiscalYear().toString());
                FiscalYearElem.appendChild(textFiscalYearElem);
                FinancialCodeElem.appendChild(FiscalYearElem);
                *//** FiscalYear child start **//*
                
                FinancialCodesElem.appendChild(FinancialCodeElem);
                *//** FinancialCode end **//*
                
                *//** PrimaryInd start **//*
                Element PrimaryIndElem = getIncidentrespDoc.createElement("PrimaryInd");
                Text textPrimaryIndElem = getIncidentrespDoc.createTextNode(Boolean.toString(incidentFinancialCodeType.isPrimaryInd()));
                PrimaryIndElem.appendChild(textPrimaryIndElem);
                FinancialCodesElem.appendChild(PrimaryIndElem);
                *//** PrimaryInd end **//*
                
                incidentEntitiesElem.appendChild(FinancialCodesElem);
                *//** FinancialCodes end **//*
            }
            }
            //System.out.println("**** out of financialcodes for loop");
            
            //System.out.println("**** Appending incidentEntites elem to incidentElem");
            //System.out.println("**** incidentElem is: " + XMLUtil.getElementXMLString(incidentElem));
            //System.out.println("**** incidentEntitiesElem is: " + XMLUtil.getElementXMLString(incidentEntitiesElem));
            incidentElem.appendChild(incidentEntitiesElem);
            *//** IncidentEntities end **//*
            //System.out.println("**** getIncidentrespDocElem is: " + XMLUtil.getXMLString(getIncidentrespDoc));
            getIncidentrespDocElem.appendChild(incidentElem);
            
            }
            
        }catch(ParserConfigurationException e){
        	System.out.println("Caught an error while trying to create a getIncidentFromROSS document");
            e.printStackTrace();
        }
        System.out.println("**** Returned getIncidentrespDoc is: " + XMLUtil.getXMLString(getIncidentrespDoc));
        return getIncidentrespDoc;
    }

    *//**
     * @param incidentDetailsReturnType
     * @return
     * @throws DatatypeConfigurationException
     *//*
    private XMLGregorianCalendar getXMLGregCalFromCalendar(Calendar calendar) throws DatatypeConfigurationException {
        
        GregorianCalendar gregorianCalendar = new GregorianCalendar(calendar.getTimeZone());
        XMLGregorianCalendar xmlgregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        return xmlgregorianCalendar;
    }

    *//**
     * @param unitIDType
     * @return
     *//*
  
  * 	jwarrick: commenting since we can get unitIDPrefix from object
  * 
  *   private String fetchAndSetDistID(UnitIDType unitIDType) {
        String str = unitIDType.getUnitIDPrefix();
        System.out.println("str is: " + str);
        String unitIDPrefix = str.substring(0,str.indexOf("#"));
        String distID = str.substring(str.indexOf("#")+1);
  
        NWCGLoggerUtil.Log.info("dist ID >>"+distID);
        
        NWCGGetIncidentReqToROSS.setDistributionID(distID);
        
        return unitIDPrefix;
    }

    *//**
     * @param env
     * @param string_token
     * @param inDoc
     * @return
     *//*
    private GetIncidentResp postGetIncidentReqMsgToROSS(YFSEnvironment env, Document doc, String string_token) throws Exception {
        
        // form GetIncidentReq message
        GetIncidentReq getIncidentReq = new GetIncidentReq();
        
        *//***** MessageOriginator element *****//*
        MessageOriginatorType messageOriginatorType = new MessageOriginatorType();
        
        Jay: Dispatch Unit ID with in MessageOriginator should be kept blank
         * UnitIDType unitIDType2 = new UnitIDType();
        unitIDType2.setUnitIDPrefix(XPathUtil.getString(doc,NWCGAAConstants.XPATH_GET_INCIDENT_MO_UNIT_ID_PREFIX));
        unitIDType2.setUnitIDSuffix(XPathUtil.getString(doc,NWCGAAConstants.XPATH_GET_INCIDENT_MO_UNIT_ID_SUFFIX));
        messageOriginatorType.setDispatchUnitID(unitIDType2);
        
        ApplicationSystemType applicationSystemType2 = new ApplicationSystemType();
        
        // Assuming that SystemID value is not passed from Document
        //applicationSystemType2.setSystemID(XPathUtil.getString(doc,NWCGAAConstants.XPATH_GET_INCIDENT_MO_SYSTEM_ID));
        // So setting the systemID to the string token.
        applicationSystemType2.setSystemID(string_token);
        
        applicationSystemType2.setSystemType(SystemTypeSimpleType.ICBS);
        messageOriginatorType.setSystemOfOrigin(applicationSystemType2);
        
        getIncidentReq.setMessageOriginator(messageOriginatorType);
        *//***** MessageOriginator element *****//*
        
        *//****** IncidentKey element ******//*
        CompositeIncidentKeyType compositeIncidentKeyType = new CompositeIncidentKeyType();
        
         The Incident ID is not for ICBSR system
         * IDType type = new IDType();
        
        ApplicationSystemType applicationSystemType = new ApplicationSystemType();
        applicationSystemType.setSystemID(XPathUtil.getString(doc,NWCGAAConstants.XPATH_GET_INCIDENT_IK_SYSTEM_ID));
        applicationSystemType.setSystemType(SystemTypeSimpleType.ICBS);
        type.setApplicationSystem(applicationSystemType);
        
        type.setEntityID(XPathUtil.getString(doc,NWCGAAConstants.XPATH_GET_INCIDENT_IK_ENTITY_ID));
        
        type.setEntityType(EntityTypeSimpleType.Incident);
        
        Jay: The Incident ID is not for ICBSR system
         * compositeIncidentKeyType.setIncidentID(type);
        
        IncidentNumberType incidentNumberType = new IncidentNumberType();
        
        UnitIDType unitIDType = new UnitIDType();
        unitIDType.setUnitIDPrefix(XPathUtil.getString(doc,NWCGAAConstants.XPATH_GET_INCIDENT_IK_UNIT_ID_PREFIX));
        unitIDType.setUnitIDSuffix(XPathUtil.getString(doc,NWCGAAConstants.XPATH_GET_INCIDENT_IK_UNIT_ID_SUFFIX));
        incidentNumberType.setHostID(unitIDType);
        
        incidentNumberType.setSequenceNumber(Long.parseLong(XPathUtil.getString(doc,NWCGAAConstants.XPATH_GET_INCIDENT_IK_SEQNO)));
        
        incidentNumberType.setYearCreated(XPathUtil.getString(doc,NWCGAAConstants.XPATH_GET_INCIDENT_IK_YEAR_CREATED));
        
        compositeIncidentKeyType.setNaturalIncidentKey(incidentNumberType);
        
        getIncidentReq.setIncidentKey(compositeIncidentKeyType);
        *//****** IncidentKey element ******//*
        
        // make ross call
        ResourceOrderServiceLocator locator = new ResourceOrderServiceLocator();
        ResourceOrderInterface orderInterface = locator.getResourceOrderPort();
        
        QName portQName = new QName(NWCGAAConstants.INCIDENT_NAMESPACE_OB, NWCGAAConstants.INCIDENT_PORT_OB);
        HandlerInfo hInfo = new HandlerInfo();
        hInfo.setHandlerClass(com.nwcg.icbs.yantra.handler.NWCGOBGetIncidentRequestToROSSHandler.class);
        locator.getHandlerRegistry().getHandlerChain(portQName).add(hInfo);
        
        GetIncidentResp getIncidentResp = orderInterface.getIncident(getIncidentReq);
        
        return getIncidentResp;
        
    }
    
    	
    *//**
     * @return Returns the distributionID.
     *//*
    public static String getDistributionID() {
        return distributionID;
    }
    *//**
     * @param distributionID The distributionID to set.
     *//*
    public static void setDistributionID(String distributionID) {
        NWCGGetIncidentReqToROSS.distributionID = distributionID;
    }
    
    public static void main(String[] args){
        Calendar calendar = Calendar.getInstance();
        if (logger.isVerboseEnabled())logger.verbose(calendar.getTime().toString());
        
        GregorianCalendar calendar2 = new GregorianCalendar(calendar.getTimeZone());
        try {
            XMLGregorianCalendar gregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar2);
            if (logger.isVerboseEnabled())logger.verbose(gregorianCalendar.toString());
        } catch (DatatypeConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
*/}
