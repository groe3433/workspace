package com.nwcg.icbs.yantra.api.trackableinventory;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.reports.loftware.NWCGReportsUtil;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGProcessAdjustLocationInventory implements YIFCustomApi,NWCGITrackableRecordMutator
{
	 private Properties _properties;
	    
	public void setProperties(Properties arg0) throws Exception {
		this._properties = arg0 ;
		// TODO Auto-generated method stub
		
	}
	/*
	 * this method is invoked when the user does an adjustLocationInventory. 
	 * inserts a record in trackable_item table.
	 * 
	 */
	public Document adjustLocationInventory(YFSEnvironment env, Document inXML) throws Exception
	{
		//System.out.println("Adjust Inventory: "+ XMLUtil.getXMLString(inXML));
		try
		{
			
					
			// get the order element
			Element elemWorkOrderRoot = inXML.getDocumentElement();
			
			// extracting all the header values, to be passed or used later on
			if(log.isVerboseEnabled()) 
				log.verbose("NWCGProcessAdjustLocationInventory::adjustLocationInventory - input XML "+ XMLUtil.getXMLString(inXML));
			
			if(elemWorkOrderRoot != null)
			{
				String strCacheID = elemWorkOrderRoot.getAttribute("Node");
				NodeList nlTagDetail  = elemWorkOrderRoot.getElementsByTagName("TagDetail");
				Element elemTagDetail = null;
				String strBatchNo="",strLotAttribute1="",strOwnerUnitID="",strLotAttribute3="",strRevisionNo="",strManufacturingDate = "";
				// get the tag details
				if(nlTagDetail != null && nlTagDetail.getLength() > 0)
				{
					elemTagDetail = (Element) nlTagDetail.item(0);
					strRevisionNo = elemTagDetail.getAttribute("RevisionNo");
					strLotAttribute1 = elemTagDetail.getAttribute("LotAttribute1");
					strOwnerUnitID = elemTagDetail.getAttribute("LotAttribute2");
					strLotAttribute3 = elemTagDetail.getAttribute("LotAttribute3");
					strBatchNo = elemTagDetail.getAttribute("BatchNo");
					//cr 368 kjs
					strManufacturingDate = elemTagDetail.getAttribute("ManufacturingDate");
				}
				
				if (strOwnerUnitID.length() == 0 || strOwnerUnitID.equals(" "))
				{
					strOwnerUnitID = strCacheID;
				}
							
				//String strOverrideCode = elemWorkOrderRoot.getAttribute("OverrideCode");
				//System.out.println("OVERRIDE CODE: " + strOverrideCode);		
				
				
				NodeList nlInventoryItem = elemWorkOrderRoot.getElementsByTagName("InventoryItem");
				String strItemID="",strUOM="";
				// get the item detail
				if(nlInventoryItem != null)
				{
					Element elemInventoryItem = (Element) nlInventoryItem.item(0);
					strItemID = elemInventoryItem.getAttribute("ItemID");
					strUOM = elemInventoryItem.getAttribute("UnitOfMeasure");
				}
				
				NodeList nlAudit  = elemWorkOrderRoot.getElementsByTagName("Audit");
				String OverrideCode = "",strReference1 = "",strReference5="",strReasonCode="",strReference2="",strReference3="",strOwnerAgency="";
				String FSAcctCode = "",BLMAcctCode = "", OtherAcctCode = "";
				// pull the document details
				if(nlInventoryItem != null)
				{
					Element elemnlAudit = (Element) nlAudit.item(0);
					
					strReference1 = elemnlAudit.getAttribute("Reference1");
					strReference2 = elemnlAudit.getAttribute("Reference2");
					strReference3 = elemnlAudit.getAttribute("Reference3");
					strReference5 = elemnlAudit.getAttribute("Reference5");
					strOwnerAgency = elemnlAudit.getAttribute("OwnerAgency");
					strReasonCode = StringUtil.nonNull(elemnlAudit.getAttribute("ReasonCode"));
				}
				
				//getting the FS account code and substring
				if (strOwnerAgency.equals("FS"))
				{		
				  String [] acctfields = strReference3.split("~");
				  if (acctfields.length > 1)
				  {
				   FSAcctCode = acctfields[0];
				   OverrideCode = acctfields[1];
				  }
				}  
				else if (strOwnerAgency.equals("BLM"))
				{		
				  BLMAcctCode = strReference3;
				}  
				else
				{
				  OtherAcctCode = strReference3;
				}
								
				// get all the work order activity detail - should be only one at a time
				NodeList nlSerialDetail = elemWorkOrderRoot.getElementsByTagName("SerialDetail");
				
				if(nlSerialDetail != null)
				{
					for(int index = 0 ; index < nlSerialDetail.getLength() ; index++ )
					{
						Element elemSerialDetail = (Element) nlSerialDetail.item(index);
						
						// get the serial number
						String strSerialNo = elemSerialDetail.getAttribute("SerialNo");
						
						//CR496 - update the status of inventory and codes
						String strQty = elemSerialDetail.getAttribute("Quantity");
						double dblQty = Double.parseDouble(strQty);
						
						String secondSerialNo = elemSerialDetail.getAttribute("SecondarySerial1");
											
						//CR 496 END of modification
							
						if(log.isVerboseEnabled())
							log.verbose("SerialNo = "+strSerialNo);
						
						if(StringUtil.isEmpty(strSerialNo))
						{
							if(log.isVerboseEnabled()) 
								log.verbose("NWCGProcessAdjustLocationInventory::adjustLocationInventory - continuing as the serial number is empty");
							// if its null, continue with next record
							continue ;
						}
												
						// update the serial record and al the components
						Document docTrackableInventoryRecord = getTrackableInventoryRecord(env,elemSerialDetail);
						Element elemCreateTrackableInventoryIP = docTrackableInventoryRecord.getDocumentElement();
						// set other attributes
						elemCreateTrackableInventoryIP.setAttribute("SerialNo",strSerialNo);
						elemCreateTrackableInventoryIP.setAttribute("ItemID",strItemID);
						elemCreateTrackableInventoryIP.setAttribute("SecondarySerial",CommonUtilities.getSecondarySerial(env,strSerialNo,strItemID));		
						elemCreateTrackableInventoryIP.setAttribute("UnitOfMeasure",strUOM);
						elemCreateTrackableInventoryIP.setAttribute("ItemShortDescription",CommonUtilities.getItemDescription(env,strItemID,strUOM));
						elemCreateTrackableInventoryIP.setAttribute("LotAttribute1",strLotAttribute1);
						elemCreateTrackableInventoryIP.setAttribute("OwnerUnitID",strOwnerUnitID);
						elemCreateTrackableInventoryIP.setAttribute("OwnerUnitName",CommonUtilities.getOwnerUnitIDName(env,strOwnerUnitID));
						elemCreateTrackableInventoryIP.setAttribute("LotAttribute3",strLotAttribute3);
						elemCreateTrackableInventoryIP.setAttribute("RevisionNo",strRevisionNo);
						elemCreateTrackableInventoryIP.setAttribute("BatchNo",strBatchNo);
						//CR 368 KJS
						elemCreateTrackableInventoryIP.setAttribute("ManufacturingDate",strManufacturingDate);
						elemCreateTrackableInventoryIP.setAttribute("StatusIncidentNo",strReference2);
						elemCreateTrackableInventoryIP.setAttribute("StatusIncidentYear",strReference5);
						elemCreateTrackableInventoryIP.setAttribute("LastDocumentNo",strReference1);
						elemCreateTrackableInventoryIP.setAttribute("FSAccountCode",FSAcctCode);
						//Last Transaction Information - QC 282
						elemCreateTrackableInventoryIP.setAttribute("LastIncidentNo",strReference2);
						elemCreateTrackableInventoryIP.setAttribute("LastIncidentYear",strReference5);
						//extented this column for 496
						elemCreateTrackableInventoryIP.setAttribute("OverrideCode",OverrideCode);
						elemCreateTrackableInventoryIP.setAttribute("BLMAccountCode",BLMAcctCode);
						elemCreateTrackableInventoryIP.setAttribute("OtherAccountCode",OtherAcctCode);
						//getting timestamp for the last transaction date - not to use MODIFYTS in the db
						NWCGReportsUtil reportsUtil = new NWCGReportsUtil();  
						String strDate = reportsUtil.dateToString(new java.util.Date(), "yyyy-MM-dd'T'HH:mm:ss");
	
						elemCreateTrackableInventoryIP.setAttribute("LastTransactionDate",strDate);
						
						//this is where the status is getting set
						//get the qty from here
						//if qty > 0
						//then if statement to check if -/+ qty then set the status and status desc
						if (dblQty <= 0){
							elemCreateTrackableInventoryIP.setAttribute("SerialStatus",NWCGConstants.SERIAL_STATUS_NOT_AVAILABLE);
							elemCreateTrackableInventoryIP.setAttribute("SerialStatusDesc",NWCGConstants.SERIAL_STATUS_NOT_AVAILABLE_DESC);
						} else {
							elemCreateTrackableInventoryIP.setAttribute("SerialStatus",NWCGConstants.SERIAL_STATUS_AVAILABLE);
							elemCreateTrackableInventoryIP.setAttribute("SerialStatusDesc",NWCGConstants.SERIAL_STATUS_AVAILABLE_DESC);
						}
						//END OF CR496
												
						if(strReasonCode.equals(NWCGConstants.NWCG_REASON_CODE_DISPOSAL_EXCESS))
						{
							elemCreateTrackableInventoryIP.setAttribute("SerialStatus",NWCGConstants.SERIAL_STATUS_NOT_AVAILABLE);
							elemCreateTrackableInventoryIP.setAttribute("SerialStatusDesc",NWCGConstants.NWCG_TRANSACTION_TYPE_EXCESSED);
							//elemCreateTrackableInventoryIP.setAttribute("Type",NWCGConstants.NWCG_TRANSACTION_TYPE_EXCESSED);
						}
						else if(strReasonCode.equals(NWCGConstants.NWCG_REASON_CODE_DISPOSAL_EXPIRED))
						{
							elemCreateTrackableInventoryIP.setAttribute("SerialStatus",NWCGConstants.SERIAL_STATUS_DISPOSED);
							elemCreateTrackableInventoryIP.setAttribute("SerialStatusDesc",NWCGConstants.SERIAL_STATUS_DISPOSED_DESC);
							//elemCreateTrackableInventoryIP.setAttribute("Type",NWCGConstants.NWCG_TRANSACTION_TYPE_EXPIRED);
						}
						else if(strReasonCode.equals(NWCGConstants.NWCG_REASON_CODE_DISPOSAL_UNSERVICEABLE))
						{
							elemCreateTrackableInventoryIP.setAttribute("SerialStatus",NWCGConstants.SERIAL_STATUS_DISPOSED);
							elemCreateTrackableInventoryIP.setAttribute("SerialStatusDesc",NWCGConstants.SERIAL_STATUS_DISPOSED_DESC);
							//elemCreateTrackableInventoryIP.setAttribute("Type",NWCGConstants.NWCG_TRANSACTION_TYPE_UNSERVICEABLE);
						}
						else if(strReasonCode.equals(NWCGConstants.NWCG_REASON_CODE_DISPOSAL_DAMAGED))
						{
							elemCreateTrackableInventoryIP.setAttribute("SerialStatus",NWCGConstants.SERIAL_STATUS_NRFI);
							elemCreateTrackableInventoryIP.setAttribute("SerialStatusDesc",NWCGConstants.SERIAL_STATUS_NRFI_DESC);
							//elemCreateTrackableInventoryIP.setAttribute("Type",NWCGConstants.NWCG_TRANSACTION_TYPE_DAMAGED);
						}
						else if(strReasonCode.equals(NWCGConstants.NWCG_REASON_CODE_DISPOSAL_LOST))
						{
							elemCreateTrackableInventoryIP.setAttribute("SerialStatus",NWCGConstants.SERIAL_STATUS_DISPOSED);
							elemCreateTrackableInventoryIP.setAttribute("SerialStatusDesc",NWCGConstants.SERIAL_STATUS_DISPOSED_DESC);
							//elemCreateTrackableInventoryIP.setAttribute("Type",NWCGConstants.NWCG_TRANSACTION_TYPE_LOST);
						}
						/* Setting Type as Inventory Adjustment - GN 11/03/08 */
						elemCreateTrackableInventoryIP.setAttribute("Type","Inventory Adjustment");
						elemCreateTrackableInventoryIP.setAttribute("StatusCacheID",strCacheID);
						try
						{
							CommonUtilities.stampAcquisitionCost(env,elemCreateTrackableInventoryIP);
						}
						catch(Exception e)
						{
							if(log.isVerboseEnabled())
									log.verbose("GOT Exception on stampAcquisitionCost "+e.getMessage());
							//e.printStackTrace();
						}
						if(log.isVerboseEnabled()) 
							//system out line to see what is update
							//System.out.println("Customer Details Input: "+ XMLUtil.getXMLString(docTrackableInventoryRecord));
							log.verbose("NWCGProcessAdjustLocationInventory::adjustLocationInventory input XML to updateTrackableInventory service => "+ XMLUtil.getXMLString(docTrackableInventoryRecord));
						try
						{
							CommonUtilities.invokeService(env,NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE,docTrackableInventoryRecord);
						}
						catch(Exception ex)
						{
							if(log.isVerboseEnabled())
								log.verbose("updateRecord failed no record found, so create one I/P XML " + XMLUtil.getXMLString(docTrackableInventoryRecord));
							CommonUtilities.invokeService(env,NWCGConstants.NWCG_CREATE_TRACKABLE_INVENTORY_SERVICE,docTrackableInventoryRecord);
						}
					}// end for order lines
				}// end if orderlines not null
				
			}
		}
		catch(Exception e)
		{
			if(log.isVerboseEnabled())
				log.verbose("NWCGProcessAdjustLocationInventory::adjustLocationInventory Caught Exception "+e);
			e.printStackTrace();
			// comment this later on
			//sthrow e;
		}
		//System.out.println("XML: "+ XMLUtil.getXMLString(inXML));
		
		
		return inXML;
	}
	/*
	 * method used to update the trackable item record, incase the record doesnt exists
	 * if the serial number is generated during kitting process this will create a new one
	 */
	private Document getTrackableInventoryRecord(YFSEnvironment env, Element elemSerialDetail) throws Exception 
	{
				
		if(log.isVerboseEnabled()) log.verbose("NWCGProcessAdjustLocationInventory::updateRecord entered");
		Document docCreateTrackableInventoryIP = XMLUtil.createDocument("NWCGTrackableItem");
		Element elemCreateTrackableInventoryIP = docCreateTrackableInventoryIP.getDocumentElement();
		
		//elemCreateTrackableInventoryIP.setAttribute("SecondarySerial",elemSerialDetail.getAttribute("SecondarySerial1"));
		//elemCreateTrackableInventoryIP.setAttribute("SecondarySerial",CommonUtilities.getSecondarySerial(env,elemSerialDetail.getAttribute("SerialNo")));		
		// make these items blank as they might have previous values
		elemCreateTrackableInventoryIP.setAttribute("KitItemID","");
		elemCreateTrackableInventoryIP.setAttribute("KitSerialNo","");
		elemCreateTrackableInventoryIP.setAttribute("KitPrimaryItemID","");
		elemCreateTrackableInventoryIP.setAttribute("KitPrimarySerialNo","");
	
		elemCreateTrackableInventoryIP.setAttribute("LastIncidentNo","");
		elemCreateTrackableInventoryIP.setAttribute("LastBuyerOrganizationCode","");
		
			
		/* Commented by GN - 04/03/08
		if secondary serial number is blank, then pass the primary serial number for the update
		String strSecondarySerialNo = elemCreateTrackableInventoryIP.getAttribute("SecondarySerial");
			if(strSecondarySerialNo == "")
			{
				elemCreateTrackableInventoryIP.setAttribute("SecondarySerial",CommonUtilities.getSecondarySerial(env,elemSerialDetail.getAttribute("SerialNo")));
			}		
		*/
		
		//System.out.println("Input XML for inventory: "+ XMLUtil.getXMLString(docCreateTrackableInventoryIP));
		return docCreateTrackableInventoryIP;
	}

	

	private static Logger log = Logger.getLogger(NWCGProcessAdjustLocationInventory.class.getName());

	public Document insertOrUpdateTrackableRecord(YFSEnvironment env, Document doc) throws Exception 
	{
		return adjustLocationInventory(env,doc);
	}
}
