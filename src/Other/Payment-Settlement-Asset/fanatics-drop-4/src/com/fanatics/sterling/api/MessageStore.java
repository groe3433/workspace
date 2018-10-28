package com.fanatics.sterling.api;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fanatics.sterling.constants.MsgStoreConstants;
import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;



public class MessageStore implements YIFCustomApi{
	
	private static YFCLogCategory logger= YFCLogCategory.instance(YFCLogCategory.class);
	private Properties props;
	
	
	public Document storeMessage(YFSEnvironment env, Document inXML) {
		logger.verbose("Inside storeMessage");
		logger.verbose("inputXML -> " + XMLUtil.getXMLString(inXML));
		
		boolean isPropertiesValidated = false;
		
		//Validate the arguments passed
		isPropertiesValidated = validateProperties(props);
		
		//Get attributes from xml passed and construct input for service
		if(isPropertiesValidated){

			logger.verbose("isPropertiesValidated: " + isPropertiesValidated);
			
			Document serviceXML = constructServiceInput(inXML, props);
			logger.verbose("serviceXML -> " + XMLUtil.getXMLString(serviceXML));

			//Call service to insert data into custom table
			try {
				CommonUtil.invokeService(env, MsgStoreConstants.FAN_MSG_STORE_SERVICE, serviceXML);
				logger.verbose("Service FanaticsMessageStore Invoked");
			} catch (Exception e) {
				logger.error("Exception within Message Store: " + e.getMessage() + ", Cause: " + e.getCause());
			}	
		}
		
		
		logger.verbose("inputXML -> " + XMLUtil.getXMLString(inXML));
		//Return input as output
		return inXML;
	}
	
	private boolean validateProperties(Properties inProps){
		
		if(inProps !=null && inProps.size() > 0 && (inProps.getProperty(MsgStoreConstants.IS_BACKUP_ENABLED) != null 
				&& inProps.getProperty(MsgStoreConstants.IS_BACKUP_ENABLED).equals("Y"))){
			return true;
		}
		
		return false;
	}
	
	private Document constructServiceInput(Document inXML, Properties props){
		
		Document outXML = null;
		
		try {
			outXML = XMLUtil.createDocument(MsgStoreConstants.EXTN_FAN_MSG_STORE);
			
			Element eleMessageStore = outXML.getDocumentElement();
			
			String serviceName = props.getProperty(MsgStoreConstants.SERVICE_NAME);
			if(serviceName != null && !serviceName.equals(""))
				eleMessageStore.setAttribute(MsgStoreConstants.SERVICE_NAME, serviceName);
			
			
			//Loop the 5 field/value pairs
			for(int i=1; i<6; i++){
				
				String field = props.getProperty(MsgStoreConstants.FIELD+i);
				String value = props.getProperty(MsgStoreConstants.VALUE+i);
				
				logger.verbose("Field: " + field);
				logger.verbose("Value: " + value);

				//if field not found do not look for value
				if((field != null && !field.equals("")) && (value != null && !value.equals(""))){
					eleMessageStore.setAttribute(MsgStoreConstants.FIELD+i, field);
										
					String xmlValue = XMLUtil.getXpathProperty(inXML,value);
					eleMessageStore.setAttribute(MsgStoreConstants.VALUE+i, xmlValue);
				}
			}
			
			//Check whether to add xml to custom table
			String persistXml = props.getProperty(MsgStoreConstants.PERSIST_XML_MSG);
			logger.verbose("persistXml: " + persistXml);
			if(persistXml != null && !persistXml.equals("") && persistXml.equals("Y"))
				eleMessageStore.setAttribute("XML", XMLUtil.getXMLString(inXML));
			
		} catch (ParserConfigurationException e) {
			logger.error("Exception within Message Store: " + e.getMessage() + ", Cause: " + e.getCause());
		}
		
		
		return outXML;
	}
	

	@Override
	public void setProperties(Properties arg0) throws Exception {
		props = arg0;
	}
	
	
	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		MessageStore ms = new MessageStore();
		Document doc;
		String inputXML = "<Items><Item ItemID=\"Id101\" ProductClass=\"NEW\" UnitOfMeasure=\"EA\" /></Items>";
		
		//arguments passed are null
		//doc = ms.storeMessage(null, XMLUtil.getDocument(inputXML));
		//System.out.println(XMLUtil.getXMLString(doc));
		
		/*props.setProperty("IsBackupEnabled", "");
		ms.setProperties(props);
		doc = ms.storeMessage(null, XMLUtil.getDocument(inputXML));
		System.out.println(XMLUtil.getXMLString(doc));*/
		
		/*props.setProperty("IsBackupEnabled", "N");
		ms.setProperties(props);
		doc = ms.storeMessage(null, XMLUtil.getDocument(inputXML));
		System.out.println(XMLUtil.getXMLString(doc));*/
		
		/*//Blank if only property
		props.setProperty("IsBackupEnabled", "Y");
		ms.setProperties(props);
		doc = ms.storeMessage(null, XMLUtil.getDocument(inputXML));
		System.out.println(XMLUtil.getXMLString(doc));*/
		
		/*props.setProperty("IsBackupEnabled", "Y");
		props.setProperty("ServiceName", "FanaticsAdjustInventory");
		ms.setProperties(props);
		doc = ms.storeMessage(null, XMLUtil.getDocument(inputXML));
		System.out.println(XMLUtil.getXMLString(doc));*/
		
		/*props.setProperty("IsBackupEnabled", "Y");
		props.setProperty("ServiceName", "FanaticsAdjustInventory");
		props.setProperty("Field1", "ItemID");
		props.setProperty("Value1", "/Items/Item/@ItemID");
		ms.setProperties(props);
		doc = ms.storeMessage(null, XMLUtil.getDocument(inputXML));
		System.out.println(XMLUtil.getXMLString(doc));*/
		
		/*props.setProperty("IsBackupEnabled", "Y");
		props.setProperty("ServiceName", "FanaticsAdjustInventory");
		props.setProperty("Field1", "ItemID");
		props.setProperty("Value1", "");
		ms.setProperties(props);
		doc = ms.storeMessage(null, XMLUtil.getDocument(inputXML));
		System.out.println(XMLUtil.getXMLString(doc));*/
		
		/*props.setProperty("IsBackupEnabled", "Y");
		props.setProperty("ServiceName", "FanaticsAdjustInventory");
		props.setProperty("Field1", "ItemID");
		ms.setProperties(props);
		doc = ms.storeMessage(null, XMLUtil.getDocument(inputXML));
		System.out.println(XMLUtil.getXMLString(doc));*/
		
		/*props.setProperty("IsBackupEnabled", "Y");
		props.setProperty("PersistXMLMessage", "N");
		props.setProperty("ServiceName", "FanaticsAdjustInventory");
		props.setProperty("Field1", "ItemID");
		props.setProperty("Value1", "/Items/Item/@ItemID");
		ms.setProperties(props);
		doc = ms.storeMessage(null, XMLUtil.getDocument(inputXML));
		System.out.println(XMLUtil.getXMLString(doc));*/
		
		/*props.setProperty("IsBackupEnabled", "Y");
		props.setProperty("PersistXMLMessage", "Y");
		props.setProperty("ServiceName", "FanaticsAdjustInventory");
		props.setProperty("Field1", "ItemID");
		props.setProperty("Value1", "/Items/Item/@ItemID");
		ms.setProperties(props);
		doc = ms.storeMessage(null, XMLUtil.getDocument(inputXML));
		System.out.println(XMLUtil.getXMLString(doc));*/
		
		/*props.setProperty("Field1", "ItemID");
		props.setProperty("Value1", "/Items/Item/@ItemID");
		
		MessageStore ms = new MessageStore();
		//String s = ms.getProperty(props, "Field2");
		
		//System.out.println(s);
		
		String inputXML = "<Items><Item ItemID=\"Id101\" ProductClass=\"NEW\" UnitOfMeasure=\"EA\" /></Items>";
		
		ms.constructServiceInput(XMLUtil.getDocument(inputXML), props);*/

	}

}
