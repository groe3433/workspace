package com.nwcg.icbs.yantra.api.trackableinventory;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;


public class NWCGProcessSerialNumberActivitiesCompleted implements YIFCustomApi,NWCGITrackableRecordMutator
{
	 private Properties _properties;
	    
	public void setProperties(Properties arg0) throws Exception {
		this._properties = arg0 ;
		// TODO Auto-generated method stub
		
	}
	/*
	 * this method is invoked when the user does kitting or dekitting of the components happens
	 * input xml format
	 * <?xml version="1.0" encoding="UTF-8"?>
		<WorkOrder >
		    <WorkOrderActivityDtl>
		        <WorkOrderComponents>
		            <WorkOrderComponent>
		                <SerialDetail/>
		            </WorkOrderComponent>
		            <WorkOrderComponent>
		                <SerialDetail />
		            </WorkOrderComponent>
		            <WorkOrderComponent >
		                <SerialDetail/>
		            </WorkOrderComponent>
		        </WorkOrderComponents>
		    </WorkOrderActivityDtl>
		</WorkOrder>
		* program extracts the serial number and then updates it based on the service item id
		* then fetches all the kit component serial numbers if any
		* then fetches updates the record as well as the component records
		* if the component is a kit, then it stamps the primary serial number on the components component
	 */
	public Document updateTrackableRecord(YFSEnvironment env, Document inXML) throws Exception
	{
		try
		{
			// get the order element
			Element elemWorkOrderRoot = inXML.getDocumentElement();
			// extracting all the header values, to be passed or used later on
			System.out.println("SNActivity Complete XML "+ XMLUtil.getXMLString(inXML));
			String strServiceItemID = elemWorkOrderRoot.getAttribute("ServiceItemID");
			String strWorkOrderNo = elemWorkOrderRoot.getAttribute("WorkOrderNo");
			String strDate = elemWorkOrderRoot.getAttribute("StatusDate");
			String strItemID = elemWorkOrderRoot.getAttribute("ItemID");
			String strUom = elemWorkOrderRoot.getAttribute("Uom");
			String strNodeKey = elemWorkOrderRoot.getAttribute("NodeKey");
			
			String strWOCost = "";
			NodeList nlExtn = elemWorkOrderRoot.getElementsByTagName("Extn");
			if(nlExtn != null && nlExtn.getLength() > 0)
			{
				Element elemExtn = (Element)nlExtn.item(0);
				strWOCost = elemExtn.getAttribute("ExtnStdRefurbCost");
			}
			if(log.isVerboseEnabled()) log.verbose("NWCGProcessKittingCompleted::createTrackableRecord received the input document "+ XMLUtil.getXMLString(inXML));
			
			if(elemWorkOrderRoot != null)
			{
				// get all the work order activity detail - should be only one at a time
				NodeList nlWorkOrderActivityDtl = elemWorkOrderRoot.getElementsByTagName("WorkOrderActivityDtl");
				
				if(nlWorkOrderActivityDtl != null)
				{
					for(int index = 0 ; index < nlWorkOrderActivityDtl.getLength() ; index++ )
					{
						Element elemWorkOrderActivityDtl = (Element) nlWorkOrderActivityDtl.item(index);
						
						// get the serial number
						String strSerialNo = elemWorkOrderActivityDtl.getAttribute("SerialNo");
						
						if(log.isVerboseEnabled()) log.verbose("MAIN SerialNo = "+strSerialNo);
						
						if(StringUtil.isEmpty(strSerialNo))
						{
							if(log.isVerboseEnabled()) log.verbose("NWCGProcessConfirmShipment:: Continuing as the serial number is empty");
							// if its null, continue with next record
							continue ;
						}
						// update the serial record and al the components
						updateSerialRecords(env,elemWorkOrderActivityDtl,
								strWorkOrderNo,strServiceItemID,strDate,strItemID,strUom,strWOCost,strNodeKey);
						
					}// end for order lines
				}// end if orderlines not null
				
			}
		}
		catch(Exception e)
		{
			if(log.isVerboseEnabled()) log.verbose("NWCGProcessConfirmShipment::createTrackableRecord Caught Exception "+e);
			e.printStackTrace();
			// comment this later on
			//throw e;
		}
			
		return inXML;
	}
	/*
	 * updates the serial number record in te database
	 */
	private void updateSerialRecords(YFSEnvironment env, Element elemWorkOrderActivityDtl,String strWorkOrderNo,
			String strServiceItemID,String strDate,String strKitItemID, String strUom,
			String strWOCost,String strNodeKey) throws Exception 
	{
		// get the serial number
		String strKitSerialNo = elemWorkOrderActivityDtl.getAttribute("SerialNo");
		// Jay : Extracting the serial number by xpath as we know the first serialdetail tag will be the the parents
		// serial detail...
		String strSecondarySerial = XPathUtil.getString(elemWorkOrderActivityDtl,"WorkOrderActivityDtl/SerialDetail/@SecondarySerial1");
		// this will update the kit record
		NodeList nlWorkOrderTag = elemWorkOrderActivityDtl.getElementsByTagName("WorkOrderTag");
		Element elemWorkOrderTag = null ;
		if(nlWorkOrderTag != null && nlWorkOrderTag.getLength() > 0)
		{
			// will have one and only one WorkOrderTag
			elemWorkOrderTag = (Element)nlWorkOrderTag.item(0);
		}
		
		updateRecord(env,strKitSerialNo,strWorkOrderNo,strDate,strKitSerialNo,
				strKitItemID,strServiceItemID,strUom,elemWorkOrderTag,strWOCost,strSecondarySerial,strNodeKey);
		
		NodeList nlWorkOrderComponent = elemWorkOrderActivityDtl.getElementsByTagName("WorkOrderComponent");
		if(nlWorkOrderComponent != null)
		{
			if(log.isVerboseEnabled()) log.verbose("NWCGProcessSerialNumberActivitiesCompleted::updateSerialRecords components "+nlWorkOrderComponent.getLength());
			
			for(int index = 0 ; index < nlWorkOrderComponent.getLength() ; index++)
			{
				Element elemWorkOrderComponent = (Element) nlWorkOrderComponent.item(index);
				// update all the components
				updateComponentRecords(env,elemWorkOrderComponent,strWorkOrderNo,strDate,strKitSerialNo,
						strKitItemID,strServiceItemID,strNodeKey);
			}
		}
	}
	/*
	 * if the kit contains any component which is serially tracked.this method will update those records
	 */
	private void updateComponentRecords(YFSEnvironment env, Element elemWorkOrderComponent, String strWorkOrderNo, 
			String strDate, String strKitSerialNo,String strKitItemID, String strServiceItemID,String strNodeKey) throws Exception 
	{
		NodeList nlWorkOrderComponentTag = elemWorkOrderComponent.getElementsByTagName("WorkOrderComponent");
		Element eleWorkOrderComponentTag = null ;
		String strSecondarySerial = "" ;
		
		Document docCreateTrackableInventoryIP = XMLUtil.createDocument("NWCGTrackableItem");
		Element elemCreateTrackableInventoryIP = docCreateTrackableInventoryIP.getDocumentElement();
		String strCompSerialNo = StringUtil.nonNull(elemWorkOrderComponent.getAttribute("SerialNo")) ;
		
		String strUom = StringUtil.nonNull(elemWorkOrderComponent.getAttribute("Uom")) ;
		String strItemID = StringUtil.nonNull(elemWorkOrderComponent.getAttribute("ItemID")) ;
		if(StringUtil.isEmpty(strCompSerialNo))
		{
			// if this component doesnt have any serial number skip this 
			return ;
		}
		
		/* if(nlWorkOrderComponentTag != null && nlWorkOrderComponentTag.getLength() > 0)
		{
			eleWorkOrderComponentTag = (Element)nlWorkOrderComponentTag.item(0);
			
			NodeList nlSerialDetail = eleWorkOrderComponentTag.getElementsByTagName("SerialDetail");
			if(nlSerialDetail != null && nlSerialDetail.getLength() > 0)
			{
				// will have only one serial detail tag
				Element elemSerialDetail = (Element) nlSerialDetail.item(0);
				strSecondarySerial = StringUtil.nonNull(elemSerialDetail.getAttribute("SecondarySerial1"));
			}
		}
		*/
		
		elemCreateTrackableInventoryIP.setAttribute("StatusCacheID",strNodeKey);
		elemCreateTrackableInventoryIP.setAttribute("SerialNo",strCompSerialNo);
		elemCreateTrackableInventoryIP.setAttribute("SecondarySerial",CommonUtilities.getSecondarySerial(env,strCompSerialNo,strItemID));
		if(eleWorkOrderComponentTag != null)
		{
			elemCreateTrackableInventoryIP.setAttribute("LotAttribute1",eleWorkOrderComponentTag.getAttribute("LotAttribute1"));
			elemCreateTrackableInventoryIP.setAttribute("LotAttribute3",eleWorkOrderComponentTag.getAttribute("LotAttribute3"));
			elemCreateTrackableInventoryIP.setAttribute("RevisionNo",eleWorkOrderComponentTag.getAttribute("RevisionNo"));
			elemCreateTrackableInventoryIP.setAttribute("BatchNo",eleWorkOrderComponentTag.getAttribute("BatchNo"));
		}
		
		// make these items blank as they might have previous values
		elemCreateTrackableInventoryIP.setAttribute("KitItemID",strKitItemID);
		elemCreateTrackableInventoryIP.setAttribute("KitSerialNo",strKitSerialNo);
		elemCreateTrackableInventoryIP.setAttribute("KitPrimaryItemID","");
		elemCreateTrackableInventoryIP.setAttribute("KitPrimarySerialNo","");
	
		elemCreateTrackableInventoryIP.setAttribute("StatusIncidentNo","");
		elemCreateTrackableInventoryIP.setAttribute("StatusBuyerOrganizationCode","");
		elemCreateTrackableInventoryIP.setAttribute("StatusIncidentYear","");

		elemCreateTrackableInventoryIP.setAttribute("LastIncidentNo","");
		elemCreateTrackableInventoryIP.setAttribute("LastBuyerOrganizationCode","");
		elemCreateTrackableInventoryIP.setAttribute("LastIncidentYear","");

		if(!StringUtil.isEmpty(strServiceItemID))
		{
			if ( (strServiceItemID.equals(NWCGConstants.SERVICE_ITEM_ID_KITTING)) ||
				 (strServiceItemID.equals(NWCGConstants.SERVICE_ITEM_ID_REFURB_KITTING)) )
			{
				elemCreateTrackableInventoryIP.setAttribute("SerialStatus",NWCGConstants.SERIAL_STATUS_AVAILABLE_IN_KIT);
				elemCreateTrackableInventoryIP.setAttribute("SerialStatusDesc",NWCGConstants.SERIAL_STATUS_AVAILABLE_IN_KIT_DESC);
				elemCreateTrackableInventoryIP.setAttribute("Type",NWCGConstants.TRANSACTION_INFO_TYPE_BUILD_KIT);
			}
			else if( (strServiceItemID.equals(NWCGConstants.SERVICE_ITEM_ID_DE_KITTING)) ||
					 (strServiceItemID.equals(NWCGConstants.SERVICE_ITEM_ID_REFURB_DEKITTING)) )
			{
				elemCreateTrackableInventoryIP.setAttribute("SerialStatus",NWCGConstants.SERIAL_STATUS_AVAILABLE);
				elemCreateTrackableInventoryIP.setAttribute("SerialStatusDesc",NWCGConstants.SERIAL_STATUS_AVAILABLE_DESC);
				elemCreateTrackableInventoryIP.setAttribute("Type",NWCGConstants.TRANSACTION_INFO_TYPE_BREAKDOWN_KIT);
			}
		}
		// setting up all the other attributes based on the requirement
		elemCreateTrackableInventoryIP.setAttribute("LastDocumentNo",strWorkOrderNo);
		elemCreateTrackableInventoryIP.setAttribute("LastTransactionDate",strDate);
		elemCreateTrackableInventoryIP.setAttribute("FSAccountCode","");
		elemCreateTrackableInventoryIP.setAttribute("BLMAccountCode","");
		elemCreateTrackableInventoryIP.setAttribute("OtherAccountCode","");
		elemCreateTrackableInventoryIP.setAttribute("OverrideCode","");
		
		System.out.println("Trackable Update Record  "+ XMLUtil.getXMLString(docCreateTrackableInventoryIP));
		
		if(log.isVerboseEnabled()) log.verbose("NWCGProcessSerialNumberActivitiesCompleted input to updateTrackableInventory service => "+ XMLUtil.getXMLString(docCreateTrackableInventoryIP));
		try
		{
			CommonUtilities.invokeService(env,NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE,docCreateTrackableInventoryIP);
		}
		catch(Exception ex)
		{
//			incase the record is an create we need to stamp the item id and the UOM
			elemCreateTrackableInventoryIP.setAttribute("UnitOfMeasure",strUom);
			elemCreateTrackableInventoryIP.setAttribute("ItemID",strItemID);
			elemCreateTrackableInventoryIP.setAttribute("ItemShortDescription",CommonUtilities.getItemDescription(env,strItemID,strUom));
			if(log.isVerboseEnabled()) log.verbose("NWCGProcessSerialNumberActivitiesCompleted::updateComponentRecords failed no record found I/P XML " + XMLUtil.getXMLString(docCreateTrackableInventoryIP));
			if(log.isVerboseEnabled()) log.verbose("Creating the record ");
			//CommonUtilities.invokeService(env,NWCGConstants.NWCG_CREATE_TRACKABLE_INVENTORY_SERVICE,docCreateTrackableInventoryIP);
		}
		
		// now check if this component is a KIT or not, if this component itself a kit
		// then stamp the Primary kit id for the components of this KIT, the kit id must have already been marked 
		// for that component while making a kit
		
		stampPrimaryKitInformationOnComponentsComponent(env,strCompSerialNo,strKitSerialNo,strKitItemID);
	}
	/*
	 * this mehtod stamps the primary kit information on all the components whoes kit it the the component of the
	 * item which is under VAS being kitted or dekitted 
	 */
	private void stampPrimaryKitInformationOnComponentsComponent(YFSEnvironment env, String strCompSerialNo, 
			String strKitSerialNo, String strKitItemID) throws Exception 
	{
		// get all the trackable records whoes kit serial number is this serial number
		// update those serial number records with primary kit serial number
		Document docTrackableInventoryIP = XMLUtil.createDocument("NWCGTrackableItem");
		Element elemTrackableInventoryIP = docTrackableInventoryIP.getDocumentElement();
		elemTrackableInventoryIP.setAttribute("KitSerialNo",strCompSerialNo);
		Document docOP = CommonUtilities.invokeService(env,ResourceUtil.get("nwcg.icbs.gettrackableitemlist.service"),docTrackableInventoryIP);
		if(docOP != null)
		{
			if(log.isVerboseEnabled()) log.verbose("stampPrimaryKitInformationOnComponentsComponent :: getTrackableItemList docOP "+XMLUtil.getXMLString(docOP));
			Element rootElement = docOP.getDocumentElement();
			NodeList nlNWCGTrackableItem = rootElement.getElementsByTagName("NWCGTrackableItem");
			if(nlNWCGTrackableItem != null)
			{
				for(int index = 0 ; index < nlNWCGTrackableItem.getLength() ; index++)
				{
					Element elemNWCGTrackableItem = (Element)nlNWCGTrackableItem.item(index);
					String strSerialNo = elemNWCGTrackableItem.getAttribute("SerialNo");
					Document docUpdateTrackableInventoryIP = XMLUtil.createDocument("NWCGTrackableItem");
					Element elemUpdateTrackableInventoryIP = docUpdateTrackableInventoryIP.getDocumentElement();
					elemUpdateTrackableInventoryIP.setAttribute("SerialNo",strSerialNo);
					elemUpdateTrackableInventoryIP.setAttribute("KitPrimaryItemID",strKitItemID);
					elemUpdateTrackableInventoryIP.setAttribute("KitPrimarySerialNo",strKitSerialNo);
					System.out.println("Trackable Update Record  "+ XMLUtil.getXMLString(docUpdateTrackableInventoryIP));
					try
					{
						CommonUtilities.invokeService(env,NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE,docUpdateTrackableInventoryIP);
					}
					catch(Exception ex)
					{
						// shouldnt fail any of the time
						if(log.isVerboseEnabled()) log.verbose("stampPrimaryKitInformationOnComponentsComponent failed no record found I/P XML " + XMLUtil.getXMLString(docUpdateTrackableInventoryIP));

					}
				}
			}// end if nlNWCGTrackableItem != null
		}// end if docOP != null
		
	}
	/*
	 * method used to update the trackable item record, incase the record doesnt exists
	 * if the serial number is generated during kitting process this will create a new one
	 */
	private void updateRecord(YFSEnvironment env, String strSerialNo,String strWorkOrderNo,String strDate,
			String strKitSerialNo,String strKitItemID,String strServiceItemID, String strUom, 
			Element elemTagAttributes,String strWOCost,String strSecondarySerial,String strNodeKey) throws Exception 
	{
		if(log.isVerboseEnabled()) log.verbose("NWCGProcessSerialNumberActivitiesCompleted::updateRecord entered");
		Document docCreateTrackableInventoryIP = XMLUtil.createDocument("NWCGTrackableItem");
		Element elemCreateTrackableInventoryIP = docCreateTrackableInventoryIP.getDocumentElement();
		
		elemCreateTrackableInventoryIP.setAttribute("StatusCacheID",strNodeKey);
		elemCreateTrackableInventoryIP.setAttribute("SerialNo",strSerialNo);
		elemCreateTrackableInventoryIP.setAttribute("ItemID",strKitItemID);
		elemCreateTrackableInventoryIP.setAttribute("UnitOfMeasure",strUom);
		elemCreateTrackableInventoryIP.setAttribute("ItemShortDescription",CommonUtilities.getItemDescription(env,strKitItemID,strUom));
		if(elemTagAttributes != null)
		{
			elemCreateTrackableInventoryIP.setAttribute("LotAttribute1",elemTagAttributes.getAttribute("LotAttribute1"));
			elemCreateTrackableInventoryIP.setAttribute("SecondarySerial",strSecondarySerial);
			elemCreateTrackableInventoryIP.setAttribute("LotAttribute3",elemTagAttributes.getAttribute("LotAttribute3"));
			elemCreateTrackableInventoryIP.setAttribute("RevisionNo",elemTagAttributes.getAttribute("RevisionNo"));
			elemCreateTrackableInventoryIP.setAttribute("BatchNo",elemTagAttributes.getAttribute("BatchNo"));
		}
		// make these items blank as they might have previous values
		elemCreateTrackableInventoryIP.setAttribute("KitItemID",strKitItemID);
		elemCreateTrackableInventoryIP.setAttribute("KitSerialNo",strKitSerialNo);
		elemCreateTrackableInventoryIP.setAttribute("KitPrimaryItemID","");
		elemCreateTrackableInventoryIP.setAttribute("KitPrimarySerialNo","");
		
		elemCreateTrackableInventoryIP.setAttribute("StatusIncidentNo","");
		elemCreateTrackableInventoryIP.setAttribute("StatusBuyerOrganizationCode","");
		elemCreateTrackableInventoryIP.setAttribute("StatusIncidentYear","");
		
		elemCreateTrackableInventoryIP.setAttribute("LastIncidentNo","");
		elemCreateTrackableInventoryIP.setAttribute("LastBuyerOrganizationCode","");
		elemCreateTrackableInventoryIP.setAttribute("LastIncidentYear","");
		
		if(!StringUtil.isEmpty(strServiceItemID))
		{
			if(strServiceItemID.equals(NWCGConstants.SERVICE_ITEM_ID_KITTING))
			{
				elemCreateTrackableInventoryIP.setAttribute("Type",NWCGConstants.TRANSACTION_INFO_TYPE_BUILD_KIT);
				elemCreateTrackableInventoryIP.setAttribute("SerialStatus",NWCGConstants.SERIAL_STATUS_AVAILABLE);
				elemCreateTrackableInventoryIP.setAttribute("SerialStatusDesc",NWCGConstants.SERIAL_STATUS_AVAILABLE_DESC);
			}
			else if(strServiceItemID.equals(NWCGConstants.SERVICE_ITEM_ID_DE_KITTING))
			{
				elemCreateTrackableInventoryIP.setAttribute("Type",NWCGConstants.TRANSACTION_INFO_TYPE_BREAKDOWN_KIT);
				elemCreateTrackableInventoryIP.setAttribute("SerialStatus",NWCGConstants.SERIAL_STATUS_BREAKDOWN_KIT);
				elemCreateTrackableInventoryIP.setAttribute("SerialStatusDesc",NWCGConstants.SERIAL_STATUS_BREAKDOWN_KIT_DESC);
			}
			else if(strServiceItemID.equals(NWCGConstants.SERVICE_ITEM_ID_REFURB_DEKITTING)) 
			{
				elemCreateTrackableInventoryIP.setAttribute("WOServiceCost",strWOCost);
				elemCreateTrackableInventoryIP.setAttribute("Type",NWCGConstants.TRANSACTION_INFO_TYPE_REFURB);
				elemCreateTrackableInventoryIP.setAttribute("SerialStatus",NWCGConstants.SERIAL_STATUS_BREAKDOWN_KIT);
				elemCreateTrackableInventoryIP.setAttribute("SerialStatusDesc",NWCGConstants.SERIAL_STATUS_BREAKDOWN_KIT_DESC);
			}
			else if(strServiceItemID.equals(NWCGConstants.SERVICE_ITEM_ID_REFURBISHMENT))
			{
				elemCreateTrackableInventoryIP.setAttribute("WOServiceCost",strWOCost);
				elemCreateTrackableInventoryIP.setAttribute("Type",NWCGConstants.TRANSACTION_INFO_TYPE_REFURB);
				elemCreateTrackableInventoryIP.setAttribute("SerialStatus",NWCGConstants.SERIAL_STATUS_AVAILABLE);
				elemCreateTrackableInventoryIP.setAttribute("SerialStatusDesc",NWCGConstants.SERIAL_STATUS_AVAILABLE_DESC);
			}
		}
		
		elemCreateTrackableInventoryIP.setAttribute("LastDocumentNo",strWorkOrderNo);
		elemCreateTrackableInventoryIP.setAttribute("LastTransactionDate",strDate);
		elemCreateTrackableInventoryIP.setAttribute("FSAccountCode","");
		elemCreateTrackableInventoryIP.setAttribute("BLMAccountCode","");
		elemCreateTrackableInventoryIP.setAttribute("OtherAccountCode","");
		elemCreateTrackableInventoryIP.setAttribute("OverrideCode","");
		
		/* No need to set the Owner Unit ID and Name for KITS, as per discussion with Jeri,Chad & Henry on 01/22/09 - GN
		
		String strOwnerUnitID = "";
		if(elemTagAttributes != null )
		{
			strOwnerUnitID = StringUtil.nonNull(elemTagAttributes.getAttribute("LotAttribute2"));
		}
		
		if (strOwnerUnitID.length() == 0 || strOwnerUnitID.equals(" "))
		{
			strOwnerUnitID = strNodeKey;
		}
		if(log.isVerboseEnabled()) log.verbose("elemCreateTrackableInventoryIP " + elemCreateTrackableInventoryIP);
		
		elemCreateTrackableInventoryIP.setAttribute("OwnerUnitID",strOwnerUnitID);
		elemCreateTrackableInventoryIP.setAttribute("OwnerUnitName",CommonUtilities.getOwnerUnitIDName(env,strOwnerUnitID));
		*/
		
		System.out.println("Trackable Update Record  "+ XMLUtil.getXMLString(docCreateTrackableInventoryIP));
		
		if(log.isVerboseEnabled()) log.verbose
			("NWCGProcessSerialNumberActivitiesCompleted::updateRecord input to updateTrackableInventory service => "+ XMLUtil.getXMLString(docCreateTrackableInventoryIP));
		try
		{
			CommonUtilities.invokeService(env,NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE,docCreateTrackableInventoryIP);
		}
		catch(Exception ex)
		{
			if(log.isVerboseEnabled()) log.verbose("updateRecord failed no record found I/P XML " + XMLUtil.getXMLString(docCreateTrackableInventoryIP));
			if(log.isVerboseEnabled()) log.verbose("Creating the record " + elemTagAttributes);
		
			try
			{
				CommonUtilities.stampAcquisitionCost(env,elemCreateTrackableInventoryIP);
			}
			catch(Exception e)
			{
				if(log.isVerboseEnabled()) log.verbose("GOT Exception on stampAcquisitionCost "+e.getMessage());
				e.printStackTrace();
			}
			if(log.isVerboseEnabled()) log.verbose("docCreateTrackableInventoryIP " + docCreateTrackableInventoryIP);
			CommonUtilities.invokeService(env,NWCGConstants.NWCG_CREATE_TRACKABLE_INVENTORY_SERVICE,docCreateTrackableInventoryIP);
		}
	}

	

	private static Logger log = Logger.getLogger(NWCGProcessSerialNumberActivitiesCompleted.class.getName());

	public Document insertOrUpdateTrackableRecord(YFSEnvironment env, Document doc) throws Exception {
		// TODO Auto-generated method stub
		return updateTrackableRecord(env,doc);
	}
}
