/**
 * 
 */
package com.nwcg.icbs.yantra.ue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.ycp.japi.ue.YCPBeforeCancelTaskUE;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;

/**
 * @author M. Lathom
 * CR 504 - Do not allow Non RRP Return Move tasks to be canceled
 */
public class NWCGBeforeCancelTaskUE implements YCPBeforeCancelTaskUE 
{

    private static Logger logger = Logger.getLogger(NWCGBeforeCancelTaskUE.class.getName());
	

	public Document beforeCancelTask(YFSEnvironment env, Document inDoc) throws YFSUserExitException
	{
		if(logger.isVerboseEnabled()) logger.verbose("NWCGBeforeCancelTaskUE Input XML "+ XMLUtil.getXMLString(inDoc));
		//System.out.println("NWCGBeforeCancelTaskUE Input ==>" + XMLUtil.getXMLString(inDoc));
		
		String strDocumentType = "";
		Element elemTask = inDoc.getDocumentElement();
		String 	strTaskKey = elemTask.getAttribute("TaskKey");
		String strShipmentKey = "";
		
		try
		{
				if(logger.isVerboseEnabled()){logger.verbose("NWCGBeforeCancelTaskUE :: strTaskKey=> "+strTaskKey);}
				
				Document taskDocument = getTaskDocument(env,strTaskKey);	
				elemTask = taskDocument.getDocumentElement();
				String strSourceLocation = elemTask.getAttribute("SourceLocationId");
				String strDoctype = "";
					
				if(strSourceLocation.equals("RFI-1")|| strSourceLocation.equals("NRFI-1"))
				{
					// IF Source is not RFI-1, then allow cancel... no need to process any further
					Element taskReferences = (Element)XMLUtil.getChildNodeByName(elemTask,"TaskReferences");
					strShipmentKey = taskReferences.getAttribute("ShipmentKey");
					if(logger.isVerboseEnabled()){logger.verbose("NWCGBeforeCancelTaskUE :: strShipmentKey=> "+strShipmentKey);}
					
					if (strShipmentKey != null)
					{// if ShipmentKey is null, then this is an adhoc...No need to process further
						strDoctype = checkDocumentType(env, strShipmentKey);
						
						if(strDoctype.equals("0010"))
						{//If you got here, and the Doc type = 0010, then this is a Non RRP return.
						 // and the Task cannot be canceled	
							throw new YFSUserExitException("Incident Return Cancel Task Exception - This Task cannot be canceled as its Source Zone is RFI-ZONE !!!");
						}
					}
				}
		}
		catch(Exception e)
		{
			if(logger.isVerboseEnabled()) logger.verbose("Generated exception "+e.toString());
	    	System.out.println("Exception "+e.toString());
	    	throw new YFSUserExitException(e.toString());
	   }

		return inDoc;
	}
	
	private Document getTaskDocument(YFSEnvironment env, String strTaskKey) 
	{
			try
			{
				Document inDoc = XMLUtil.createDocument("Task");
				Element rootElem = inDoc.getDocumentElement();
				rootElem.setAttribute("TaskKey",strTaskKey);
			
				if(logger.isVerboseEnabled()){ logger.verbose("NWCGBeforeCancelTaskUE :: getTaskDeatils API I/P "+ XMLUtil.getXMLString(inDoc));}
				Document outDoc = CommonUtilities.invokeAPI(env,"NWCGBeforeCancelTaskUE_getTaskDetails","getTaskDetails",inDoc);
				if(logger.isVerboseEnabled()){logger.verbose("NWCGBeforeCancelTaskUE :: getTaskDeatils API O/P "+ XMLUtil.getXMLString(outDoc));}
			
				return outDoc;
			}
			catch(Exception e)
			{
				if(logger.isVerboseEnabled()) logger.verbose("Generated exception "+e.toString());
				throw new RuntimeException(e);
			}
	}
	private Document getShipmentDocument(YFSEnvironment env, String strShipmentKey) 
	{
		try
		{
			Document inDoc = XMLUtil.createDocument("Shipment");
			Element rootElem = inDoc.getDocumentElement();
			rootElem.setAttribute("ShipmentKey",strShipmentKey);
		
			if(logger.isVerboseEnabled()){ logger.verbose("NWCGBeforeCancelTaskUE :: getShipmentDetails API I/P "+ XMLUtil.getXMLString(inDoc));}
			Document outDoc = CommonUtilities.invokeAPI(env,"NWCGBeforeCancelTaskUE_getShipmentDetails","getShipmentDetails",inDoc);
			if(logger.isVerboseEnabled()){ logger.verbose("NWCGBeforeCancelTaskUE :: getShipmentDetails API O/P "+ XMLUtil.getXMLString(outDoc));}

			return outDoc;
		}
		catch(Exception e)
		{
			if(logger.isVerboseEnabled()) logger.verbose("Generated exception "+e.toString());
			throw new RuntimeException(e);
		}
		
	}
	private String checkDocumentType(YFSEnvironment env, String strShipmentKey) throws YFSException
	{		
			Document shipmentDocument = getShipmentDocument(env,strShipmentKey);	
			Element elemShipment = shipmentDocument.getDocumentElement();
			String strDocumentType = "";
			strDocumentType = elemShipment.getAttribute("DocumentType");
			
			return strDocumentType;
	}
	
}