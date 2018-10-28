package com.nwcg.icbs.yantra.api.trackableinventory;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

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
/*
 * author jvishwakarma
 * this class is used for posting the data in the custom trackable_item table
 * all te time it should create a new record
 * this is invoked when the user does a receipt or accept the returns (which intern calls the receiveOrder)
 * This class handles the receive of all lines which are serially controlled, incase that item is a kit it goes 
 * and update the components for that kit and incase we have a kit as the component this updates the kits components
 * as well. If want to know the fields which are updated, please follow the detail design document of trackable
 * inventory
 * This API assumes if any kit is received, the status of all the components or kits within that kit would be same as
 * the status of the KIT, you can't receive a kit which is under NRFI status but having any component within it in 
 * RFI status. this is because we can't distinguish if an item is received as a kit or as a component
 * and we are not capturing the components serial number within the KIT  
 * Logic :
 * 1. Get all the receipt lines
 * 2. get the line serial
 * 3. if serial is null return else continue with the program
 * 4. get the trackable inventory document
 * 5. get all the serial whoes parent is this serial number
 * 6. update the child serial records (components)
 * 7. if the component is a KIT, invoke all the components/items whoes parent kit is the record from step 5.
 * 8. update these records again - components of the kit, and this kit is within the kit - for which the user has 
 * 	  enterd the serial number 
 * 9. invoke the create record api for NWCG_TRACKABLE_ITEM
 * 
 * NOTE : THIS API ASSUMES THAT THE SERIAL RECORDS EXISTS IN THE SYSTEM, IF NOT THIS WILL CREATE A NEW ONE
 */


/* This is a Common API for both Blind Returns and Cache Transfer Receipts 
 * Made this change on 05/20/08 - SGN
 */

public class NWCGProcessBlindReturn implements YIFCustomApi,NWCGITrackableRecordMutator
{
	 private Properties _properties;
	    
	public void setProperties(Properties arg0) throws Exception {
		this._properties = arg0 ;
		// TODO Auto-generated method stub
		
	}
	public Document createTrackableRecord(YFSEnvironment env, Document inXML) throws Exception
	{
		try
		{
			//System.out.println("Trackable Inv Input XML "+ XMLUtil.getXMLString(inXML));
			Element elemReceiptRoot = inXML.getDocumentElement();
			
			if(log.isVerboseEnabled())
				log.verbose("******* BEGIN NWCGProcessReceiveOrder::createTrackableRecord received the input document "+ XMLUtil.getXMLString(inXML));
			
			if(elemReceiptRoot != null)
			{
				String strReceiptNo = elemReceiptRoot.getAttribute("ReceiptNo");
				String strTrackableAction = elemReceiptRoot.getAttribute("TrackableInventoryAction");
				String strTransType = "";
				if (strTrackableAction.equals("RECEIVE_TRANSFER_ORDER"))
				{
					strTransType = NWCGConstants.TRANSACTION_INFO_TYPE_CACHE_TRANSFERRED;
				}
				else
				{
					strTransType = NWCGConstants.TRANSACTION_INFO_TYPE_RETURN;
				}
				// get the receipt line
				NodeList nlReceiptLines = elemReceiptRoot.getElementsByTagName("ReceiptLine");
				if(nlReceiptLines != null)
				{
					if(log.isVerboseEnabled()) log.verbose("NWCGProcessReceiveOrder:: total number of receiptlines = "+ nlReceiptLines.getLength());
					String strReceivngNode = elemReceiptRoot.getAttribute("ReceivingNode");
					String strReceiptDate = elemReceiptRoot.getAttribute("ReceiptDate");
					// for all the receipt lines
					for(int index = 0 ; index < nlReceiptLines.getLength() ; index++ )
					{
						Element elemReceiptLine = (Element) nlReceiptLines.item(index);
						String strDispositionCode = elemReceiptLine.getAttribute("DispositionCode");
						// if disposition code is null get it from the receipt line
						if(StringUtil.isEmpty(strDispositionCode))
						{
							strDispositionCode = elemReceiptRoot.getAttribute("DispositionCode");
						}
						//System.out.println("Disposition Code "+ strDispositionCode);
						// check if the serial number is null
						String strSerialNo = elemReceiptLine.getAttribute("SerialNo");
						//System.out.println("strSerialNo "+ strSerialNo);
						if(StringUtil.isEmpty(strSerialNo))
						{
							if(log.isVerboseEnabled()) log.verbose("NWCGProcessReceiveOrder:: Continuing as the serial number is empty");
							// if its null, continue with next record
							continue ;
						}
						// get the order date from the order line instead of order header
						// these steps are just for getting the order date - requested ship date
						if(log.isVerboseEnabled()) log.verbose("Passing the element as "+elemReceiptLine);
						// get the trackable inventory record input xml, this is called only for the
						// parent or the KIT item, the components within the KIT will be updated 
						// individually later on
						Document docCreateTrackableInventoryIP = getTrackableInventoryIPDocument(env,elemReceiptLine,strDispositionCode,strReceivngNode);
						// insert Shipment/@SellerOrganizationCode, @ReceivingNode,Shipment/@OrderNo, Order/@OrderDate
						// on top of the xml returned from from the  getTrackableInventoryIPDocument call
						if(docCreateTrackableInventoryIP != null)
						{
							if(log.isVerboseEnabled()) log.verbose("NWCGProcessReceiveOrder:: got the trackable inv doc as "+ XMLUtil.getXMLString(docCreateTrackableInventoryIP));
							
							Element elemCreateTrackableInventoryIP = docCreateTrackableInventoryIP.getDocumentElement();
							String strKitCode = elemCreateTrackableInventoryIP.getAttribute("KitCode");
							boolean isKit = isKitItem(strKitCode);
							// assign the other attributes
							if(log.isVerboseEnabled()) log.verbose("NWCGProcessReceiveOrder:: isKit="+isKit);
							// setting the attributes for blind receipt and RFI, even if its kit or not 
							elemCreateTrackableInventoryIP.setAttribute("StatusCacheID",strReceivngNode);
							elemCreateTrackableInventoryIP.setAttribute("Type",strTransType);
							elemCreateTrackableInventoryIP.setAttribute("LastDocumentNo",strReceiptNo);
							elemCreateTrackableInventoryIP.setAttribute("LastTransactionDate",strReceiptDate);
								
							// if its a return and disposition code is RFI and its a kit
							// update all the other kit components
							
							if(isKit)
							{
								if(log.isVerboseEnabled()) log.verbose("Its a KIT component:: updating all the child records calling updateKitComponents");
								// update the kit id of all the components
						        updateKitComponents(elemCreateTrackableInventoryIP,env,strReceivngNode,strReceiptNo,strReceiptDate,strDispositionCode,strTransType);
							}
							else // if its not a KIT... then blank out all the kit related information
								// as this might be received as a component before and the value may still exist
							{
								elemCreateTrackableInventoryIP.setAttribute("KitItemID","");
								elemCreateTrackableInventoryIP.setAttribute("KitSerialNo","");
								elemCreateTrackableInventoryIP.setAttribute("KitPrimaryItemID","");	
							}
							System.out.println("Trackable Inv Update XML "+ XMLUtil.getXMLString(docCreateTrackableInventoryIP));
							try
							{
								CommonUtilities.invokeService(env,NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE,docCreateTrackableInventoryIP);
							}
							catch(Exception e1)
							{
								if(log.isVerboseEnabled()) log.verbose("NWCGProcessReceiveOrder:: while updating the BLIND or RETURN record exception "+e1.getMessage());
								if(log.isVerboseEnabled()) log.verbose("CREATING THE RECORD AS UPDATE FAILED Input XML "+XMLUtil.getXMLString(docCreateTrackableInventoryIP));
								e1.printStackTrace();
								CommonUtilities.invokeService(env,NWCGConstants.NWCG_CREATE_TRACKABLE_INVENTORY_SERVICE,docCreateTrackableInventoryIP);
								// got an exception try creating a new one
								//throw e1;
							}
							// get the parent of this serial number... if any record is returned
							// it means the kit is received as component - since this serial number was in a kit
							//System.out.println("Before Calling Parent Update strSerialNo "+ strSerialNo);
							// Added by GN - 11/08/07
							String strItemID = StringUtil.nonNull(elemReceiptLine.getAttribute("ItemID"));
							String ParentSerialKey = getParentSerialKey(env,strSerialNo,strItemID);
							//System.out.println("ParentSerialKey "+ ParentSerialKey);
							Document ParentSerialDoc = XMLUtil.createDocument("SerialList");
							String ParentSerialNo = "";
							if (ParentSerialKey.length() > 0)
							{
							  ParentSerialDoc = getParentSerialNo(env,ParentSerialDoc,ParentSerialKey);
							}
							//System.out.println("Parent SerialNo "+ ParentSerialNo);
							//System.out.println("Parent Serial XML "+ XMLUtil.getXMLString(ParentSerialDoc));
							NodeList serialNodeList = ParentSerialDoc.getDocumentElement().getElementsByTagName("Serial");
							if (serialNodeList.getLength() > 0)
							{
							  for(int cnt1=0;cnt1<serialNodeList.getLength();cnt1++){
							   Element SerialElem = (Element)serialNodeList.item(cnt1);
							   ParentSerialNo = SerialElem.getAttribute("SerialNo");
							   Document docParentSerailRecord = getParentRecord(env,ParentSerialNo);
							    //System.out.println("Parent XML "+ XMLUtil.getXMLString(docParentSerailRecord));
							    // now update the parent record 
							   updateParentRecordAsReceivedAsComponents(env,docParentSerailRecord,docCreateTrackableInventoryIP);
							  } 
					     	}
						}// end if docCreateTrackableInventoryIP != null
					}// end while recipt lines
				}// end if receiptlines not null
				
			}
		}
		catch(Exception e)
		{
			if(log.isVerboseEnabled()) log.verbose("NWCGProcessReceiveOrder::createTrackableRecord Caught Exception ");
			e.printStackTrace();
			// comment this later on
			//throw e;
		}
		if(log.isVerboseEnabled()) log.verbose("****** END NWCGProcessReceiveOrder::createTrackableRecord ******");
		return inXML;
	}
	/*
	 * this method will update the parent record status as received as component
	 */
	private void updateParentRecordAsReceivedAsComponents(YFSEnvironment env, Document docParentSerailRecord,Document Trackableupdatedoc) throws Exception
	{
		
		if(docParentSerailRecord != null)
		{
			Element elem = docParentSerailRecord.getDocumentElement();
			Element elemTrackableupddoc = Trackableupdatedoc.getDocumentElement();
			NodeList nlNWCGTrackableItem = elem.getElementsByTagName("NWCGTrackableItem");
			if(nlNWCGTrackableItem != null)
			{
				for(int index = 0 ; index < nlNWCGTrackableItem.getLength() ; index++)
				{
					Element elemNWCGTrackableItem = (Element) nlNWCGTrackableItem.item(index);
					String TrackableItemKey = elemNWCGTrackableItem.getAttribute("TrackableItemKey");
					Document trackableDoc = null;
					trackableDoc = XMLUtil.createDocument("NWCGTrackableItem");
					Element elemTrackable = trackableDoc.getDocumentElement();
					elemTrackable.setAttribute("TrackableItemKey",TrackableItemKey);
					elemTrackable.setAttribute("SerialStatus",NWCGConstants.NWCG_STATUS_CODE_RECEIVED_AS_COMPONENT);
					elemTrackable.setAttribute("SerialStatusDesc",NWCGConstants.NWCG_STATUS_CODE_RECEIVED_AS_COMPONENT_DESC);
					
					elemTrackable.setAttribute("StatusCacheID",elemTrackableupddoc.getAttribute("StatusCacheID"));
					elemTrackable.setAttribute("LastDocumentNo",elemTrackableupddoc.getAttribute("LastDocumentNo"));
					elemTrackable.setAttribute("Type",elemTrackableupddoc.getAttribute("Type"));
					elemTrackable.setAttribute("LastTransactionDate",elemTrackableupddoc.getAttribute("LastTransactionDate"));
					
					
					//System.out.println("Trackable Inv Update XML : ParentSerialRecord "+ XMLUtil.getXMLString(trackableDoc));
					try 
					{
							CommonUtilities.invokeService(env,NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE,trackableDoc);
					} 
					catch (Exception e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}// end for 
			}
		}
	}
	/*
	 * get all the records whoes child is this serial number
	 */
	private Document getParentRecord(YFSEnvironment env, String strSerialNo) throws ParserConfigurationException 
	{
		Document trackableDoc = XMLUtil.createDocument("NWCGTrackableItem");
		Element elemTrackable = trackableDoc.getDocumentElement();
		elemTrackable.setAttribute("SerialNo",strSerialNo);
		
		Document returnDoc = null;
		try 
		{
				returnDoc = CommonUtilities.invokeService(env,ResourceUtil.get("nwcg.icbs.gettrackableitemlist.service"),trackableDoc);
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnDoc;
	}
	/*
	 * this method gets all the components serial number for the given kit
	 * updates the NWCG_TRACKABLE_ITEM table for those components
	 * the only difference between the component item and the kit is
	 * 1. the status shuld be updated to Available in Kit 
	 * 2. populating all the kit information for that kit component
	 */
	private void updateKitComponents(Element elem,YFSEnvironment env,String strCacheId,String strReceiptNo,
			String strReceiptDate,String strDispositionCode,String strTransType) throws Exception 
	{
		// get all the child serial numbers
		String strSerialNo = elem.getAttribute("SerialNo");
		String strItemID = elem.getAttribute("ItemID");
		// get all the child components for this serial number
		Document docGetSerialListOP = getAllChildSerialComponents(env,strSerialNo,false,strItemID);
		if(log.isVerboseEnabled()) log.verbose("updateKitComponents:: Components  for serial number "+ strSerialNo + "  XML "+ XMLUtil.getXMLString(docGetSerialListOP));
		Element elemGetSerialListOP = null ;
		if(docGetSerialListOP != null)
		{
			elemGetSerialListOP = docGetSerialListOP.getDocumentElement();
		}
		// System.out.println("docGetSerialListOP XML "+ XMLUtil.getXMLString(docGetSerialListOP));
		// get all the serials - basically all the child components
		NodeList nlSerial = elemGetSerialListOP.getElementsByTagName("Serial");

		if(nlSerial != null)
		{
			// now we will update all the component records which can again be a KIT
			// use this document element to update all the components
			if(log.isVerboseEnabled()) log.verbose("updateKitComponents:: total serials "+ nlSerial.getLength() );
			for(int index = 0 ; index < nlSerial.getLength() ; index++)
			{
				Document trackableDoc = XMLUtil.createDocument("NWCGTrackableItem");
				Element elemTrackable = trackableDoc.getDocumentElement();
				Element elemSerial = (Element) nlSerial.item(index);
				String strCompSerialNo = elemSerial.getAttribute("InventoryItemKey");
				
				String strCompItemID = setItemAttributesFromInventoryItemKey(env,strCompSerialNo,elemTrackable);
				//elemTrackable.setAttribute("ItemID",strCompItemID);
				elemTrackable.setAttribute("SerialNo",elemSerial.getAttribute("SerialNo"));
				String esno = elemSerial.getAttribute("SerialNo");
				//System.out.println("ESNO : "+ esno);
				elemTrackable.setAttribute("SecondarySerial",CommonUtilities.getSecondarySerial(env,esno,strCompItemID));
				// updating the component of the parent - can be kit or a component 
				if(log.isVerboseEnabled()) log.verbose("updateKitComponents:: Updating the kit or component ItemID="+strCompItemID + " SerialNo="+elemSerial.getAttribute("SerialNo"));
				updateKitOrComponent(env,elemTrackable,strCacheId,strReceiptNo,strReceiptDate,strSerialNo,strItemID,"","",strDispositionCode,strTransType);
				
                /* Commenting out - No Longer needed. 05/19/08 - GN 
				// now again update the components of this kit if any
				// extract the key and again fetch all the childs for this kit
				String strSerialKey = elemSerial.getAttribute("GlobalSerialKey");
				if(log.isVerboseEnabled()) log.verbose("All the childs for the serial number key = "+ strSerialKey);
				Document docGetComponentSerialListOP = getAllChildSerialComponents(env,strSerialKey,true,"");
				System.out.println("docGetComponentSerialListOP XML "+ XMLUtil.getXMLString(docGetComponentSerialListOP));
				if(log.isVerboseEnabled()) log.verbose("updateKitComponents:: "+XMLUtil.getXMLString(docGetComponentSerialListOP));
				Element elemGetComponentSerialListOP = null ;
				
				if(docGetComponentSerialListOP != null)
				{
					elemGetComponentSerialListOP = docGetComponentSerialListOP.getDocumentElement();
					NodeList nlComponentSerial = elemGetComponentSerialListOP.getElementsByTagName("Serial");
					if(nlComponentSerial != null )
					{
						if(log.isVerboseEnabled()) log.verbose("updateKitComponents:: total number of component of component "+nlComponentSerial.getLength());
						for(int serialIndex = 0 ; serialIndex < nlComponentSerial.getLength() ; serialIndex++)
						{
							Document componentTrackableDoc = XMLUtil.createDocument("NWCGTrackableItem");
							Element elemComponentTrackable = componentTrackableDoc.getDocumentElement();
							// at this point we have just got the serial number and the inventory record key
							// we dont know the item id, get the item id from the getInventoryItem API call
							Element elemComponentSerial = (Element) nlComponentSerial.item(serialIndex);
							String strItemSelfSerialNo = elemComponentSerial.getAttribute("SerialNo");
							String strInventoryItemSelfKey = elemComponentSerial.getAttribute("InventoryItemKey");
							// get the kit item id
							
							String strSelfItemID = setItemAttributesFromInventoryItemKey(env,strInventoryItemSelfKey,elemComponentTrackable);
							//elemComponentTrackable.setAttribute("ItemID",strSelfItemID);
							elemComponentTrackable.setAttribute("SerialNo",strItemSelfSerialNo);
							elemComponentTrackable.setAttribute("SecondarySerial",CommonUtilities.getSecondarySerial(env,strItemSelfSerialNo,strSelfItemID));
							
							if(log.isVerboseEnabled()) log.verbose("updateKitComponents:: item id for the inventory item key "+strInventoryItemSelfKey+" is "+strSelfItemID);
							// update all the records
							// pass the appropriate primary kit serial number and kit serial number
							if(log.isVerboseEnabled()) log.verbose("updateKitComponents:: Updating the kit of kit now ItemID="+strSelfItemID + " SerialNo="+strItemSelfSerialNo);
							updateKitOrComponent(env,elemComponentTrackable,strCacheId,
									strReceiptNo,strReceiptDate,strCompSerialNo,strCompItemID,strSerialNo,
									strItemID,strDispositionCode);
						}// end for all serials
					}// end if component list is not null
				}// end if document is not null
				End Comment 05/19/08 - GN  */
				
			}// end for all serial list
		}// end if node list for serial is not null
		
	}
	private String setItemAttributesFromInventoryItemKey(YFSEnvironment env, String strInventoryItemKey, Element elem) throws Exception  
	{
		if(log.isVerboseEnabled()) log.verbose("getItemIDFromInventoryItemKey:: strInventoryItemKey "+strInventoryItemKey);
		Document docGetInventoryItemIP = XMLUtil.createDocument("InventoryItem");
		Element elemGetInventoryItemIP = docGetInventoryItemIP.getDocumentElement();
		elemGetInventoryItemIP.setAttribute("OrganizationCode",NWCGConstants.ENTERPRISE_CODE);
		elemGetInventoryItemIP.setAttribute("InventoryItemKey",strInventoryItemKey);
		if(log.isVerboseEnabled()) log.verbose("getItemIDFromInventoryItemKey:: getInventoryItemList IP "+XMLUtil.getXMLString(docGetInventoryItemIP));
		
		env.setApiTemplate("getInventoryItemList","NWCGProcessBlindReturn_getInventoryItemList");
		
		Document docGetInventoryItemOP = CommonUtilities.invokeAPI(env,"getInventoryItemList",docGetInventoryItemIP);
		System.out.println("docGetInventoryItemOP XML "+ XMLUtil.getXMLString(docGetInventoryItemOP));
		env.clearApiTemplate("getInventoryItemList");
		if(log.isVerboseEnabled()) log.verbose("getItemIDFromInventoryItemKey:: getInventoryItemList OP "+XMLUtil.getXMLString(docGetInventoryItemOP));
		Element elemGetInventoryItemOP = docGetInventoryItemOP.getDocumentElement();
		NodeList nlInvItem = elemGetInventoryItemOP.getElementsByTagName("InventoryItem");
		String strItemID = "";
		String strUOM = "";
		if(log.isVerboseEnabled()) log.verbose("getItemIDFromInventoryItemKey:: nlInvItem length "+nlInvItem.getLength());
		if(nlInvItem != null )
		{
			// should have only one element as we are passing the key
			Element elemInvItem = (Element)nlInvItem.item(0);
			strItemID = elemInvItem.getAttribute("ItemID") ;
			strUOM = elemInvItem.getAttribute("UnitOfMeasure") ;
			elem.setAttribute("ItemID",strItemID);
			elem.setAttribute("UnitOfMeasure",strUOM);
			elem.setAttribute("ItemShortDescription",XPathUtil.getString(elemGetInventoryItemOP,"/InventoryList/InventoryItem/Item/PrimaryInformation/@ShortDescription"));
		}
		
		if(log.isVerboseEnabled()) log.verbose("getItemIDFromInventoryItemKey returning the item id as "+strItemID);
		return strItemID;
	}
	/*
	 * this method returns all the child components for the given serial number
	 * if the value passed is a serial number this method gets the serial key for this serial number
	 * and then fetches all the records whoes parnet serial number is this record
	 */
	private Document getAllChildSerialComponents(YFSEnvironment env,String strSerialNo,boolean isSerialKey,String strItemID) throws Exception 
	{
		if(log.isVerboseEnabled()) log.verbose("starting getAllChildSerialComponents ");
		
		
		String strSerialKey = "";
		Document docGetSerialListOP = null ;
		// We got the global serial key
		if(isSerialKey)
		{
			strSerialKey = strSerialNo ;
		}
		// we got the serial number. extract the global serial key from this 
		else
		{
			Document docGetSerialListIP = XMLUtil.createDocument("Serial");
			Element elemGetSerialListIP = docGetSerialListIP.getDocumentElement();
			elemGetSerialListIP.setAttribute("SerialNo",strSerialNo);
			Element el_InvItem=docGetSerialListIP.createElement("InventoryItem");
			elemGetSerialListIP.appendChild(el_InvItem);
			el_InvItem.setAttribute("ItemID",strItemID);
			env.setApiTemplate("getSerialList","NWCGProcessBlindReturn_getSerialList_GetKey");
			if(log.isVerboseEnabled()) log.verbose("getAllChildSerialComponents::Invoking getSerialList with input "+XMLUtil.getXMLString(docGetSerialListIP));
			docGetSerialListOP = CommonUtilities.invokeAPI(env,"getSerialList",docGetSerialListIP);
			if(log.isVerboseEnabled()) log.verbose("getAllChildSerialComponents:: getSerialList output "+XMLUtil.getXMLString(docGetSerialListOP));
			env.clearApiTemplate("getSerialList");
			// should return only one record
			strSerialKey = StringUtil.nonNull(XPathUtil.getString(docGetSerialListOP,"SerialList/Serial/@GlobalSerialKey"));
			if(log.isVerboseEnabled()) log.verbose("getAllChildSerialComponents got the golbal serial key ="+strSerialKey);
		}
		// have to reset the api actually midifies the input document
		Document docGetSerialListIP = XMLUtil.createDocument("Serial");
		Element elemGetSerialListIP = docGetSerialListIP.getDocumentElement();
		// get all the serial numbers whoes parent is this serial number
		// will get all the child components
		// clearing off all the child elements
		elemGetSerialListIP.setAttribute("SerialNo","");
		// getting all the serials whoes parent is this serial 
		elemGetSerialListIP.setAttribute("ParentSerialKey",strSerialKey);
		env.setApiTemplate("getSerialList","NWCGProcessBlindReturn_getSerialList");
		if(log.isVerboseEnabled()) log.verbose("getAllChildSerialComponents::Invoking getSerialList with input "+XMLUtil.getXMLString(docGetSerialListIP));
		docGetSerialListOP = CommonUtilities.invokeAPI(env,"getSerialList",docGetSerialListIP);
		if(log.isVerboseEnabled()) log.verbose("getAllChildSerialComponents:: getSerialList output "+XMLUtil.getXMLString(docGetSerialListOP));
		env.clearApiTemplate("getSerialList");
		
		docGetSerialListOP = getSubKitComponents(env,docGetSerialListOP,docGetSerialListOP);
		
		return docGetSerialListOP;
	}
	
	private Document getSubKitComponents(YFSEnvironment env,Document SerialDoc,Document SerialInDoc) throws Exception 
	{
		if(log.isVerboseEnabled()) log.verbose("starting getSubKitComponents ");
		System.out.println("getSubKitComponents1");
		NodeList serialNodeList = SerialDoc.getDocumentElement().getElementsByTagName("Serial");
		for(int count=0;count<serialNodeList.getLength();count++){
			Element SerialElem = (Element)serialNodeList.item(count);
			String serialKey = SerialElem.getAttribute("GlobalSerialKey");
			Document docGetSerialListOP1 = getComponents(env,serialKey);
			System.out.println("getSubKitComponents1 1");
			NodeList serialNodeList1 = docGetSerialListOP1.getDocumentElement().getElementsByTagName("Serial");
			if (serialNodeList1.getLength() > 0)
			{
				for(int cnt1=0;cnt1<serialNodeList1.getLength();cnt1++){
					Element SerialElem1 = (Element)serialNodeList1.item(cnt1);
					//System.out.println("SerialElem1 :"+XMLUtil.getElementXMLString(SerialElem1));
					Element ele1 = SerialInDoc.createElement("Serial");
					SerialInDoc.getDocumentElement().appendChild(ele1);
					XMLUtil.copyElement(SerialInDoc,SerialElem1,ele1);
					System.out.println("getSubKitComponents1 2");
					//System.out.println("SerialInDoc :"+XMLUtil.getXMLString(SerialInDoc));
				}
				  SerialInDoc = getSubKitComponents(env,docGetSerialListOP1,SerialInDoc);
			}
		}
		
    	return SerialInDoc;
	}	
	
	private Document getComponents(YFSEnvironment env,String strSerialKey) throws Exception 
	{
		Document docGetSerialListIP2 = null;
		Document docGetSerialListOP2 = null;
		Element elemGetSerialListIP2 = null;
		
		if(log.isVerboseEnabled()) log.verbose("starting getSubKitComponents ");
		
		docGetSerialListIP2 = XMLUtil.createDocument("Serial");
		elemGetSerialListIP2 = docGetSerialListIP2.getDocumentElement();

		// getting all the serials whoes parent is this serial 
		elemGetSerialListIP2.setAttribute("ParentSerialKey",strSerialKey);
		env.setApiTemplate("getSerialList","NWCGProcessBlindReturn_getSerialList");
		
		docGetSerialListOP2 = CommonUtilities.invokeAPI(env,"getSerialList",docGetSerialListIP2);
		System.out.println("docGetSerialListOP2 :"+XMLUtil.getXMLString(docGetSerialListOP2));
		
		return docGetSerialListOP2;
	}	

	/*
	 * this methods updates the component or a kit. it the component is a kit and updates the kit and if its
	 * a component, updates the component, system will invoke this if and only if the root or the parent itself a kit
	 */
	private void updateKitOrComponent(YFSEnvironment env,Element elemTrackable, 
			String strCacheId, String strReceiptNo, String strReceiptDate,String strKitSerialNo,
			String strKitItemID,String strPrimaryKitSerialNo,String strPrimaryKitItemID,
			String strDispositionCode,String strTransType) throws Exception 
	{
		
		if(log.isVerboseEnabled()) log.verbose("updateKitOrComponent strDispositionCode="+strDispositionCode);
		
		if(strDispositionCode.equals(NWCGConstants.NWCG_RFI_DISPOSITION_CODE))
		{
			elemTrackable.setAttribute("SerialStatus",NWCGConstants.SERIAL_STATUS_AVAILABLE_IN_KIT);
			elemTrackable.setAttribute("SerialStatusDesc",NWCGConstants.SERIAL_STATUS_AVAILABLE_IN_KIT_DESC);
		}
		else if (strDispositionCode.equals(NWCGConstants.NWCG_NRFI_DISPOSITION_CODE))
		{
			elemTrackable.setAttribute("SerialStatus",NWCGConstants.SERIAL_STATUS_NRFI);
			elemTrackable.setAttribute("SerialStatusDesc",NWCGConstants.SERIAL_STATUS_NRFI_DESC);
		}
		elemTrackable.setAttribute("StatusCacheID",strCacheId);
		//elemTrackable.setAttribute("StatusIncidentNo","");
		//elemTrackable.setAttribute("StatusIncidentYear","");
		//elemTrackable.setAttribute("StatusBuyerOrganizationCode","");
		//elemTrackable.setAttribute("LastIncidentNo","");
		//elemTrackable.setAttribute("LastIncidentYear","");
		//elemTrackable.setAttribute("LastBuyerOrnanizationCode","");
		elemTrackable.setAttribute("Type",strTransType);
		elemTrackable.setAttribute("LastDocumentNo",strReceiptNo);
		elemTrackable.setAttribute("LastTransactionDate",strReceiptDate);
		
		//elemTrackable.setAttribute("FSAccountCode","");
		//elemTrackable.setAttribute("BLMAccountCode","");
		//elemTrackable.setAttribute("OtherAccountCode","");
		// the kit item id and kit serial number should be the kit which is receipt
		elemTrackable.setAttribute("KitItemID",strKitItemID);
		elemTrackable.setAttribute("KitSerialNo",strKitSerialNo);
		elemTrackable.setAttribute("KitPrimaryItemID",strPrimaryKitItemID);
		elemTrackable.setAttribute("KitPrimarySerialNo",strPrimaryKitSerialNo);
		if(log.isVerboseEnabled()) log.verbose("updateKitOrComponent:: GENERATED THE XMLS AS " + XMLUtil.getXMLString(elemTrackable.getOwnerDocument()));
		try
		{
			// create the record
			CommonUtilities.invokeService(env,NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE,elemTrackable.getOwnerDocument());
		}
		catch(Exception ex)
		{
			if(log.isVerboseEnabled()) 
				log.verbose("Got Exception while Updating the component record - "+ex.getMessage());
			//if(log.isVerboseEnabled()) log.verbose(env,ex);
			ex.printStackTrace();
			// should never come here
			if(log.isVerboseEnabled()) log.verbose("updateKitOrComponent:: CREATING THE RECORD AS UPDATE FAILED " + XMLUtil.getXMLString(elemTrackable.getOwnerDocument()));
			try
			{
				CommonUtilities.invokeService(env,NWCGConstants.NWCG_CREATE_TRACKABLE_INVENTORY_SERVICE,elemTrackable.getOwnerDocument());
			}
			catch(Exception e)
			{
				if(log.isVerboseEnabled()) 
					log.verbose("Got Exception while Updating the component record - "+e.getMessage());
				//if(log.isVerboseEnabled()) log.verbose(env,ex);
				ex.printStackTrace();
				//throw e ;
			}
			//throw ex;
		}// end catch
		
	}
	private boolean isKitItem(String strKitCode)
	{
		boolean isKit = false;
		// if this is empty or equals PK its a kit
		if((!StringUtil.isEmpty(strKitCode)) ) //|| strKitCode.equals(NWCGConstants.PHYSICAL_KIT_CODE))
		{
			isKit = true ;
		}
		return isKit;
	}
	/*
	 * This method generates the input xml for creating the record
	 */
	private Document getTrackableInventoryIPDocument(YFSEnvironment env,Element elemReceiptLine,String strDispositionCode,String ReceivingNode) throws Exception 
	{
		if(log.isVerboseEnabled()) log.verbose("getTrackableInventoryIPDocument::inXML ==> "+elemReceiptLine);
		
		Document returnDoc = XMLUtil.createDocument("NWCGTrackableItem");
		Element returnDocElem = returnDoc.getDocumentElement();
		// UN COMMENT THESE LINES ONCE case 38012 is Fixed
// COMMENT STARTS		
//		NodeList nlSerialDetail = elemReceiptLine.getElementsByTagName("SerialDetail");
//		if(nlSerialDetail != null && nlSerialDetail.getLength() > 0)
//		{
//			// will have only one serial detail tag
//			Element elemSerialDetail = (Element) nlSerialDetail.item(0);
//			String strSecondarySerial = StringUtil.nonNull(elemSerialDetail.getAttribute("SecondarySerial1"));
//			returnDocElem.setAttribute("SecondarySerial",strSecondarySerial);
//		}
// END COMMENT
		
		String strSerialNo = StringUtil.nonNull(elemReceiptLine.getAttribute("SerialNo"));
			
		String strItemID = StringUtil.nonNull(elemReceiptLine.getAttribute("ItemID"));
		String strUOM = StringUtil.nonNull(elemReceiptLine.getAttribute("UnitOfMeasure"));
		
		if(log.isVerboseEnabled()) log.verbose("Serial No is == >> "+elemReceiptLine.getAttribute("SerialNo"));
		if(log.isVerboseEnabled()) log.verbose("UnitOfMeasure is == >> "+elemReceiptLine.getAttribute("UnitOfMeasure"));
		
		returnDocElem.setAttribute("SerialNo",strSerialNo);
		returnDocElem.setAttribute("SecondarySerial",CommonUtilities.getSecondarySerial(env,strSerialNo,strItemID));
		returnDocElem.setAttribute("ItemID",strItemID);
		returnDocElem.setAttribute("UnitOfMeasure",strUOM);
		
		/* No need to set the Owner Unit ID and Name , as per discussion with Jeri,Chad & Henry on 01/22/09 - GN
		
		String strOwnerUnitID = StringUtil.nonNull(StringUtil.nonNull(elemReceiptLine.getAttribute("LotAttribute2"))) ;
		System.out.println("Before strOwnerUnitID "+ strOwnerUnitID);
		System.out.println("Before strOwnerUnitID Length "+ strOwnerUnitID.length());
		
		//Added by GN - 08/20/08
		if (strOwnerUnitID.length() == 0 || strOwnerUnitID.equals(" "))
		{
			strOwnerUnitID = ReceivingNode;
		}
		
		System.out.println("After strOwnerUnitID "+ strOwnerUnitID);
		returnDocElem.setAttribute("OwnerUnitID",strOwnerUnitID);
		String strOwnerUnitName = CommonUtilities.getOwnerUnitIDName(env,strOwnerUnitID);
		returnDocElem.setAttribute("OwnerUnitName",strOwnerUnitName);
		*/
		
		returnDocElem.setAttribute("LotAttribute1",StringUtil.nonNull(elemReceiptLine.getAttribute("LotAttribute1")));
		returnDocElem.setAttribute("LotAttribute3",StringUtil.nonNull(elemReceiptLine.getAttribute("LotAttribute3")));
		
		Document itemList = getItemListOP(env, strItemID,strUOM);
		// get the items owner unit id
		// fire getItemList api and get the extended attribute
		String strKitCode = XPathUtil.getString(itemList,"ItemList/Item/PrimaryInformation/@KitCode");
		String strItemDesc = XPathUtil.getString(itemList,"ItemList/Item/PrimaryInformation/@ShortDescription");
		
		returnDocElem.setAttribute("ItemShortDescription",strItemDesc);
		// assign this status anyways to the record, this is vary only for the kit components
		
		//Added by GN - 01/03/08
		
		if(strDispositionCode.equals(NWCGConstants.NWCG_RFI_DISPOSITION_CODE))
		{
			returnDocElem.setAttribute("SerialStatus",NWCGConstants.SERIAL_STATUS_AVAILABLE);
			returnDocElem.setAttribute("SerialStatusDesc",NWCGConstants.SERIAL_STATUS_AVAILABLE_DESC);
		}
		else 
		{
			returnDocElem.setAttribute("SerialStatus",NWCGConstants.SERIAL_STATUS_NRFI);
			returnDocElem.setAttribute("SerialStatusDesc",NWCGConstants.SERIAL_STATUS_NRFI_DESC);
		}
		
		
		//this is where the FS account code is getting nulled out
		//returnDocElem.setAttribute("FSAccountCode","");
		//returnDocElem.setAttribute("BLMAccountCode","");
		//returnDocElem.setAttribute("OtherAccountCode","");
		//returnDocElem.setAttribute("StatusIncidentNo","");
		//returnDocElem.setAttribute("StatusYear","");
		//returnDocElem.setAttribute("StatusBuyerOrganizationCode","");
		//returnDocElem.setAttribute("LastIncidentNo","");
		//returnDocElem.setAttribute("LastIncidentYear","");
		//returnDocElem.setAttribute("LastCustomerUnitID","");
		
		boolean isKit = isKitItem(strKitCode);
		
		if(isKit)
		{
			returnDocElem.setAttribute("KitSerialNo",StringUtil.nonNull(elemReceiptLine.getAttribute("SerialNo")));
			returnDocElem.setAttribute("KitItemID",strItemID);
		}
		// to be used later in the caller function
		returnDocElem.setAttribute("KitCode",strKitCode);
		
		if(log.isVerboseEnabled()) log.verbose("getTrackableInventoryIPDocument :: returning "+XMLUtil.getXMLString(returnDoc));
		return returnDoc;
	}

	private Document getItemListOP(YFSEnvironment env,String strItemID, String strUOM) throws Exception {
		Document getItemListOP = null ;
		Document docGetItemListIP = XMLUtil.createDocument("Item");
		Element elemGetItemListIP = docGetItemListIP.getDocumentElement();
		elemGetItemListIP.setAttribute("ItemID",strItemID);
		elemGetItemListIP.setAttribute("UnitOfMeasure",strUOM);
		env.setApiTemplate("getItemList","NWCGProcessBlindReturn_getItemList");
		getItemListOP = CommonUtilities.invokeAPI(env,"getItemList",docGetItemListIP);
		env.clearApiTemplate("getItemList");
		return getItemListOP;
	}

	private static Logger log = Logger.getLogger(NWCGProcessBlindReturn.class.getName());

    public String getParentSerialKey(YFSEnvironment env,String SerialNo,String strItemID)throws Exception{
    	
    	String GSerialKey = "";
    	
    	Document inDoc = XMLUtil.newDocument();
    	Document outDoc = XMLUtil.newDocument();

			Element el_PSerial=inDoc.createElement("Serial");
			inDoc.appendChild(el_PSerial);
			el_PSerial.setAttribute("SerialNo",SerialNo);
			Element el_InvItem=inDoc.createElement("InventoryItem");
			el_PSerial.appendChild(el_InvItem);
			el_InvItem.setAttribute("ItemID",strItemID);
			env.setApiTemplate("getSerialList","NWCGProcessBlindReturn_getSerialList");
			outDoc = CommonUtilities.invokeAPI(env,"getSerialList",inDoc);
			//System.out.println("Parent Serial Key Output "+ XMLUtil.getXMLString(outDoc));
			
			Element PSerialOut = (Element) outDoc.getDocumentElement().getElementsByTagName("Serial").item(0);
			GSerialKey = PSerialOut.getAttribute("ParentSerialKey");
			//System.out.println("GSerialKey "+GSerialKey);
			env.clearApiTemplate("getSerialList");

        return GSerialKey;
    }
	    	    
    public Document getParentSerialNo(YFSEnvironment env,Document ParentDoc,String ParentSerialKey)throws Exception{
    	
    	String PSerialNo = "";
    	
    	Document inDoc = XMLUtil.newDocument();
    	Document outDoc = XMLUtil.newDocument();

			Element el_PSerial=inDoc.createElement("Serial");
			inDoc.appendChild(el_PSerial);
			el_PSerial.setAttribute("GlobalSerialKey",ParentSerialKey);
			env.setApiTemplate("getSerialList","NWCGProcessBlindReturn_getSerialList");
			outDoc = CommonUtilities.invokeAPI(env,"getSerialList",inDoc);
			
			Element PSerialOut = (Element) outDoc.getDocumentElement().getElementsByTagName("Serial").item(0);
    	    PSerialNo = PSerialOut.getAttribute("SerialNo");
    	    String PSerialKey = PSerialOut.getAttribute("ParentSerialKey");
    	    env.clearApiTemplate("getSerialList");
    	    Element ele1 = ParentDoc.createElement("Serial");
    	    ParentDoc.getDocumentElement().appendChild(ele1);
			XMLUtil.copyElement(ParentDoc,PSerialOut,ele1);
			
			if (PSerialKey.length() > 0)
			{
				ParentDoc = getParentSerialNo(env,ParentDoc,PSerialKey);
			}
			
        return ParentDoc;
    }
    
	public Document insertOrUpdateTrackableRecord(YFSEnvironment env, Document doc) throws Exception 
	{
	
		return createTrackableRecord(env,doc);
	}
}