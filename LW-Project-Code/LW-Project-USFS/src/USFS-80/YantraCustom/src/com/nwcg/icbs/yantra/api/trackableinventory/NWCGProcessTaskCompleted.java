package com.nwcg.icbs.yantra.api.trackableinventory;

import java.util.Properties;
import java.util.StringTokenizer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGProcessTaskCompleted implements YIFCustomApi,NWCGITrackableRecordMutator
{
	 private Properties _properties;
	    
	public void setProperties(Properties arg0) throws Exception {
		this._properties = arg0 ;
		// TODO Auto-generated method stub
		
	}
	/*
	 * This method is invoked when ever a user completes the task
	 * 
	 */
	public Document updateTrackableRecord(YFSEnvironment env, Document inXML) throws Exception
	{
		try
		{
			// get the order element
			Element elemTaskRoot = inXML.getDocumentElement();
			String strTargetZoneId = "",strSerialNo="";
			
			if(log.isVerboseEnabled()) 
				log.verbose("NWCGProcessKittingCompleted::createTrackableRecord received the input document "+ XMLUtil.getXMLString(inXML));
			
			if(elemTaskRoot != null)
			{
				NodeList nlInventory  = elemTaskRoot.getElementsByTagName("Inventory");
				Element elemInventory = null;
				// get the attributes
				if(nlInventory != null && nlInventory.getLength() > 0)
				{
					elemInventory = (Element) nlInventory.item(0);
					strSerialNo = StringUtil.nonNull(elemInventory.getAttribute("SerialNo"));
				}
				if(strSerialNo.equals(""))
				{
					if(log.isVerboseEnabled()) 
						log.verbose("NWCGProcessTaskCompleted::returning as no serial number task id ");
					
					return inXML;
				}
				// fetch this if and only if the serial number exists 
				strTargetZoneId = StringUtil.nonNull(elemTaskRoot.getAttribute("TargetZoneId"));
				// if the target zone is not null  update the record
				if(!strTargetZoneId.equals(""))
				{
					updateTrackableRecord(env,strSerialNo,strTargetZoneId);
				}
					
			}// end if orderlines not null
				
			
		}
		catch(Exception e)
		{
			if(log.isVerboseEnabled())
				log.verbose("NWCGProcessConfirmShipment::createTrackableRecord Caught Exception "+e);
			e.printStackTrace();
			// comment this later on
			//throw e;
		}
			
		return inXML;
	}
	/*
	 * this method updates the trackable record if the zone passed is refurb,kitting or dekitting zone
	 */
	private void updateTrackableRecord(YFSEnvironment env,String strSerialNo, String strTargetZoneId) throws Exception 
	{
		boolean bIsRefurbOrKittingDeKittingZone = isRefurbOrKittingDeKittingZone(strTargetZoneId); 
		
		if(log.isVerboseEnabled())
			log.verbose("NWCGProcessTaskCompleted::updateTrackableRecord ZONE "+strTargetZoneId+" Flag bIsRefurbOrKittingDeKittingZone "+bIsRefurbOrKittingDeKittingZone );
			
		if(bIsRefurbOrKittingDeKittingZone)
		{
			Document doc = XMLUtil.createDocument("NWCGTrackableItem");
			Element elem = doc.getDocumentElement();
			elem.setAttribute("SerialNo",strSerialNo);
			elem.setAttribute("SerialStatus",NWCGConstants.NWCG_STATUS_CODE_WORKORDERED);
			elem.setAttribute("SerialStatusDesc",NWCGConstants.NWCG_STATUS_CODE_WORKORDERED_DESC);
			// update the record
			CommonUtilities.invokeService(env,ResourceUtil.get("nwcg.icbs.changetrackableinventory.service"),doc);
		}
	}
/*
 * method identifies if the zone is refurb,kitting or dekitting zone
 */
	private boolean isRefurbOrKittingDeKittingZone(String strTargetZoneId) 
	{
		// defined in yantraimpl.properties file with this key
		String strValidZones = ResourceUtil.get("nwcg.icbs.kittingdekittingrefurbzone");
		
		if(!strValidZones.equals(""))
		{
			StringTokenizer tokenizer = new StringTokenizer(strValidZones,",");
			if(tokenizer != null)
			{
				while(tokenizer.hasMoreTokens())
				{
					String strZones = StringUtil.nonNull(tokenizer.nextToken());
					// if values matches return true
					if(strZones.equals(strTargetZoneId))
						return true;
				}
			}
		}
		//otherwise always false
		return false;
	}

	private static Logger log = Logger.getLogger(NWCGProcessTaskCompleted.class.getName());

	public Document insertOrUpdateTrackableRecord(YFSEnvironment env, Document doc) throws Exception {
		// TODO Auto-generated method stub
		return updateTrackableRecord(env,doc);
	}
}
