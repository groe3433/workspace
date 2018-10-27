package com.nwcg.icbs.yantra.soap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

import javax.mail.internet.MimeUtility;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;

import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.commCore.NWCGCodeTemplate;
import com.nwcg.icbs.yantra.commCore.NWCGProperties;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGAAUtil 
{	
	public static void logWarn(String name, String text) {
		NWCGLoggerUtil.Log.info(name + " , " + text);		
	}
	
	public static void logInfo(String name, String text){
		NWCGLoggerUtil.Log.info(name + " , " + text);		
	}

	public static void logError(String name, String text){
		NWCGLoggerUtil.Log.info(name + " , " + text);		
	}
	
	public static void logDebug(String name, String text){
		NWCGLoggerUtil.Log.info(name + " , " + text);		
	}
	
	public static String readText(String filename) throws Exception {		
		byte[] data = null;
		
		File f = new File(filename);
        FileInputStream fs = new FileInputStream(f);
        
        data = new byte[fs.available()];
        fs.read(data);
        fs.close();
        
        String msg_str = new String(data);
        
        return msg_str;
	}
	
	public static Object deserializeObject(String code) throws Exception {

		byte data[] = code.getBytes();
		ByteArrayInputStream bais = new ByteArrayInputStream(data);

		InputStream is = MimeUtility.decode(bais, "base64");

		ObjectInputStream ois = new ObjectInputStream(is);
		Object o = ois.readObject();
		ois.close();

		return o;
	}
	 
	 public static String serializeObject(Object o) throws Exception {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		OutputStream os = MimeUtility.encode(baos, "base64");

		ObjectOutputStream oos = new ObjectOutputStream(os);

		oos.writeObject(o);
		oos.close();
		baos.close();
		return baos.toString();
	}
	
	public static NWCGSOAPMsg parseSOAP(Document doc) throws Exception {

		String aSoapMsgString = serialize(doc);

		MessageFactory mf = MessageFactory.newInstance();
		MimeHeaders mh = new MimeHeaders();

		ByteArrayInputStream bais = new ByteArrayInputStream(aSoapMsgString
				.getBytes());

		SOAPMessage msg = mf.createMessage(mh, bais);
		NWCGSOAPMsg nmsg = new NWCGSOAPMsg(msg);
		return nmsg;
	}
	
    public static Document buildXMLDocument(String xml) {	
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
			doc = db.parse(bais);
		} catch (Exception e) {
			NWCGLoggerUtil.Log.info(e.getLocalizedMessage());
		}
		return doc;
	}
  
  public static String serialize(Document doc) throws Exception {		
		org.apache.xml.serialize.OutputFormat format = new org.apache.xml.serialize.OutputFormat(doc);
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(2);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        XMLSerializer serializer = new XMLSerializer(baos, format);
        serializer.serialize(doc);
        
        return baos.toString();
    }
	
	public static String serialize(SOAPMessage soapMessage) throws Exception {		
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();		
		DocumentBuilder db = dbf.newDocumentBuilder();		
		ByteArrayOutputStream baws = new ByteArrayOutputStream();		
		soapMessage.writeTo(baws);		
		ByteArrayInputStream bais = new ByteArrayInputStream(baws.toByteArray());	
		
		Document doc = db.parse(bais);
		
		org.apache.xml.serialize.OutputFormat format = new org.apache.xml.serialize.OutputFormat(doc);
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(2);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();        
        
        XMLSerializer serializer = new XMLSerializer(baos, format);
        serializer.serialize(doc);
        
        return baos.toString();
    }
	
	public static String buildSOAP(com.nwcg.icbs.yantra.soap.NWCGSOAPMsg aSOAPMessage) throws Exception {
		return aSOAPMessage.generateCode();
	}

	public static String buildAndSend(
			com.nwcg.icbs.yantra.soap.NWCGSOAPMsg aSOAPMessage, String target)
			throws Exception {

		String msg_str = buildSOAP(aSOAPMessage);

		String reply = sendHttpMsg(msg_str);

		return reply;
	}
	
	public static String sendHttpMsg(String msg_str) {
		
		String urlAddress = null;		
		int port = -1;	
	
		String port_str = NWCGProperties.getProperty("ROSS_PORT");		
		port = Integer.parseInt(port_str);
		
		NWCGLoggerUtil.Log.info("Sending HTTP Msg:" + msg_str);
		String reply = sendHttpMsg(msg_str,urlAddress, port);
		
		return reply;
	}
	
	public static String sendHttpMsg(String xmldata, String hostname, int port) {	
		
		//TODO: Invoke the IBM Dispatch Client to send the message to ross
		
		//1. Create the IBM Dispatch Client (DC)
		
		//2. Configure DC for XML SOAP Message Transfer
		
		//3. Convert xmldata to SOAPMessage object
		
		//4. Send message 
		
		//5. Convert response SOAPMessage to XML
		
		String respText = NWCGConstants.EMPTY_STRING;
		/*try {
		     			
			respText = SoapServer.sendMsg(xmldata, hostname, port);
		     
		    } catch (Exception e) {
		    	
		    	//Todo: Time Out
		    	
		    	//
		      e.printStackTrace();
		    }*/		    
	    return respText;
	}

	public static String lookupNodeValue(Document doc, String nodeName) {
		NodeList nL = doc.getElementsByTagName(nodeName);
		
		Node nMC = nL.item(0);
		
		String messageName = nMC.getFirstChild().getNodeValue();
		
		return messageName;
	}
	
	public static String determineServiceGroup(String serviceName) {
		
		if(serviceName.equals("CreateIncidentAndRequestReq")) {
			return NWCGAAConstants.INCIDENT_SERVICE_GROUP_NAME;
		}
		return NWCGConstants.EMPTY_STRING;
	}	
	
	public void sendMessageAsync(com.nwcg.icbs.yantra.soap.NWCGSOAPMsg aSoapMessage) {
		throw new UnsupportedOperationException();
	}

	public NWCGSOAPMsg sendMessageSync(com.nwcg.icbs.yantra.soap.NWCGSOAPMsg aSoapMessage) {
		throw new UnsupportedOperationException();
	}	
	
	public static String lookupMessageKeyFromDistId(YFSEnvironment env,
			String type, String distID) throws Exception {	
		
		String templateFileName = null;
		String listServiceName = null;
		String tagname = null;
		
		if(type.equals("IB")) {
			
			templateFileName = NWCGProperties.getProperty("SDF_INBOUND_MESSAGE_SAMPLE_FILENAME");

			listServiceName = NWCGAAConstants.SDF_GET_IB_MESSAGE_LIST_SERVICE_NAME;
			
			tagname = "NWCGInboundMessage";

		}
		
		if(type.equals("OB")) {
			
			templateFileName = NWCGProperties.getProperty("SDF_OUTBOUND_MESSAGE_SAMPLE_FILENAME");

			listServiceName = NWCGAAConstants.SDF_GET_OB_MESSAGE_LIST_SERVICE_NAME;
			
			tagname = "NWCGOutboundMessage";
		}
		
		
		
        String msg_str = NWCGAAUtil.readText(templateFileName);
       
        NWCGCodeTemplate ct = new NWCGCodeTemplate(msg_str);
        
        ct.setSlot("dist_id", distID);
        ct.setSlot("msg", NWCGConstants.EMPTY_STRING);     	        
        ct.setSlot("msgKey",NWCGConstants.EMPTY_STRING) ;
        ct.setSlot("sysName", NWCGConstants.EMPTY_STRING) ;
        ct.setSlot("status",NWCGConstants.EMPTY_STRING) ;
        ct.setSlot("msgType",NWCGConstants.EMPTY_STRING) ;
        		        
        msg_str = ct.generateCode();

        NWCGAAUtil.logInfo("NWCGResponseStatusTypeHandler.lookupMessageKeyFromDistId", msg_str);
        
        NWCGLoggerUtil.Log.info("listServiceName:'" + listServiceName + "'");
        
	
        Document doc = NWCGAAUtil.buildXMLDocument(msg_str);
        Document doc_rt = CommonUtilities.invokeService(env,listServiceName,doc);  
        
        NodeList nlOBMsg = doc_rt.getElementsByTagName(tagname);

        if(nlOBMsg != null && nlOBMsg.getLength() > 0) {
        	for(int index = 0; index < nlOBMsg.getLength(); index++)
        	{        		
                Node elemOBMsg = nlOBMsg.item(index);
                NamedNodeMap map =  elemOBMsg.getAttributes();

                Node node_mt = map.getNamedItem("MessageType");                
                String strMsgType = node_mt.getNodeValue();

                if(strMsgType.equals(NWCGAAConstants.MESSAGE_TYPE_LATEST))
                {
                	Node node_mk = map.getNamedItem("MessageKey");
                	String strMK = node_mk.getNodeValue();
                	return strMK;
                }
        	}
        }        
        NWCGAAUtil.logInfo(
				"NWCGResponseStatusTypeHandler.lookupMessageKeyFromDistId",
				"Unable to find latest message with distID '" + distID + "'");
        
        throw new Exception("Unable to find latest message with distID '"+distID+"'");        
	}
	
	public static String lookupMessageKeyFromDistId(String type, String distID) throws Exception {

		 YFSEnvironment env = CommonUtilities.createEnvironment("yantra", "100");		 
		 return lookupMessageKeyFromDistId(env, type, distID);  
	}
	
	/**
	 * This method will get the class from NWCGAnAImpl.properties based on the
	 * type of the message (inbound or outbound). If it is outbound, then we need to
	 * get the .OBResponseProcessor class. As of now, we will not use .OBRequestProcessor
	 * as that requires a call initiation from A&A to backend which is not in near future, 
	 * so returning .OBResponseProcessor everytime. If it is inbound, then get the .IBProcessor.
	 * @param msgName
	 * @return
	 */
	public static String getHandler(String msgName) {
		String handlerClass = NWCGConstants.EMPTY_STRING;
		String boundType = NWCGProperties.getProperty(msgName
				.concat(NWCGAAConstants.HANDLER_PROP_EXTENSION_TYPE));
		if (boundType.equalsIgnoreCase(NWCGAAConstants.MESSAGE_DIR_TYPE_IB)){
			handlerClass = NWCGProperties
					.getProperty(msgName
							.concat(NWCGAAConstants.HANDLER_PROP_EXTENSION_IBPROCESSOR));
		}
		else if (boundType.equalsIgnoreCase(NWCGAAConstants.MESSAGE_DIR_TYPE_OB)){
			handlerClass = NWCGProperties
					.getProperty(msgName
							.concat(NWCGAAConstants.HANDLER_PROP_EXTENSION_OBRESPONSE_PROCESSOR));
		}
		NWCGAAUtil.logInfo("NWCGAAUtil.getHandler",
				"NWCGIBReader::getHandler, Handler Class Name:" + handlerClass);
		return handlerClass;
	}
	
	/**
	 * Get the node name with out URI. If the passed string is ron:UpdateIncidentKeyNotification,
	 * then the node name will be returned as UpdateIncidentKeyNotification. If the node name is 
	 * UpdateIncidentKeyNotification, then the same value is returned.
	 * @param nodeName
	 * @return
	 */
	public static String getNodeNameWOURI(String nodeName){
		//TODO... Sunjay Get the node name based on java api and not using substring
		int index = nodeName.indexOf(':');
		if (index != -1){
			nodeName = nodeName.substring(index+1, nodeName.length());
		}
		return nodeName;
	}

	/**
	 * Determines if inbound message is sync or async based on custom property
	 * in the header
	 * 
	 * @param serviceName
	 * @return boolean if property is true - it is sync message, otherwise if it
	 *         is false or absent - async message
	 */	
	public static boolean isSync(String serviceName,String customProp) {
		boolean result = false;
		
		boolean bTokenFound = false;
		NWCGLoggerUtil.Log.info("customProp in isSync :"+ customProp);
		NWCGLoggerUtil.Log.info("serviceName in isSync :"+ serviceName);
		if (!StringUtil.isEmpty(customProp)) {
			StringTokenizer strTokenizer = new StringTokenizer(customProp,";");
			if(strTokenizer != null && strTokenizer.hasMoreTokens())
			{
				String strToken = strTokenizer.nextToken();
				String propName = NWCGConstants.EMPTY_STRING;
				if(strToken.indexOf("=") != -1)
					propName = customProp.substring(0,strToken.indexOf("="));
				
				NWCGLoggerUtil.Log.info("propName :" + propName);
				// Value set in custom property takes precedence
				if (propName.equalsIgnoreCase("processSynchronously"))
				{
					bTokenFound = true;
					String propValue = customProp.substring(strToken.indexOf("=")+1);
					if(propValue.equalsIgnoreCase("true"))
						result = true;
				}				
			}
		}
		if(!bTokenFound)
		{
			result = ResourceUtil.getAsBoolean(serviceName+".isSync", false);
		}
		return result;
	}
	
	public static void setSSL()
	{
		try
        {
			System.setProperty("java.protocol.handler.pkgs", "com.ibm.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.ibm.jsse.JSSEProvider());            
            
            NWCGLoggerUtil.Log.info("*** From Resource File following lines will be commented later ****");
            String trustStoreFile = ResourceUtil.get("nwcg.ob.truststore.file");
            String trustStorePwd = ResourceUtil.get("nwcg.ob.truststore.password");
            String trustStoreType = ResourceUtil.get("nwcg.ob.truststore.trustStoreType");
            String keyStoreFile = ResourceUtil.get("nwcg.ob.keystore.file");
            String keyStorePwd = ResourceUtil.get("nwcg.ob.keystore.password");
            String keyStoreType = ResourceUtil.get("nwcg.ob.keystore.keyStoreType");
            
			NWCGLoggerUtil.Log.info("truststore file: " + trustStoreFile);
			NWCGLoggerUtil.Log.info("truststore pwd : " + trustStorePwd);
			NWCGLoggerUtil.Log.info("truststore type : " + trustStoreType);
			NWCGLoggerUtil.Log.info("keystore file  : " + keyStoreFile);
			NWCGLoggerUtil.Log.info("keystore pwd   : "+ keyStorePwd);
			NWCGLoggerUtil.Log.info("keystore pwd   : "+ keyStoreType);
			
			System.setProperty("javax.net.ssl.trustStore", trustStoreFile);
			System.setProperty("javax.net.ssl.trustStorePassword", trustStorePwd);
			System.setProperty("javax.net.ssl.trustStoreType", trustStoreType);
			System.setProperty("javax.net.ssl.keyStore", keyStoreFile) ;
			System.setProperty("javax.net.ssl.keyStorePassword", keyStorePwd) ;
			System.setProperty("javax.net.ssl.keyStoreType", keyStoreType);
			NWCGLoggerUtil.Log.info("*** From System ****");
			NWCGLoggerUtil.Log.info("truststore file: " + System.getProperty("javax.net.ssl.trustStore"));
			NWCGLoggerUtil.Log.info("truststore pwd : "+System.getProperty("javax.net.ssl.trustStorePassword"));
			NWCGLoggerUtil.Log.info("truststore type : "+System.getProperty("javax.net.ssl.trustStoreType"));
			NWCGLoggerUtil.Log.info("keystore file  : "+System.getProperty("javax.net.ssl.keyStore"));
			NWCGLoggerUtil.Log.info("keystore pwd   : "+System.getProperty("javax.net.ssl.keyStorePassword"));			
			NWCGLoggerUtil.Log.info("keystore type  : "+System.getProperty("javax.net.ssl.keyStoreType"));			
        }
		catch(Exception e) {
			NWCGLoggerUtil.printStackTraceToLog(e);
		}
	}
}