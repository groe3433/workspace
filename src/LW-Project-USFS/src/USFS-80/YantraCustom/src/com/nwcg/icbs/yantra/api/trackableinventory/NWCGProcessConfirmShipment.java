/* Copyright 2010, Sterling Commerce, Inc. All rights reserved. */
/*
                     LIMITATION OF LIABILITY
THIS SOFTWARE SAMPLE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED 
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL STERLING COMMERCE, Inc. BE LIABLE UNDER ANY THEORY OF 
LIABILITY (INCLUDING, BUT NOT LIMITED TO, BREACH OF CONTRACT, BREACH 
OF WARRANTY, TORT, NEGLIGENCE, STRICT LIABILITY, OR ANY OTHER THEORY 
OF LIABILITY) FOR (i) DIRECT DAMAGES OR INDIRECT, SPECIAL, INCIDENTAL, 
OR CONSEQUENTIAL DAMAGES SUCH AS, BUT NOT LIMITED TO, EXEMPLARY OR 
PUNITIVE DAMAGES, OR ANY OTHER SIMILAR DAMAGES, WHETHER OR NOT 
FORESEEABLE AND WHETHER OR NOT STERLING OR ITS REPRESENTATIVES HAVE 
BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES, OR (ii) ANY OTHER 
CLAIM, DEMAND OR DAMAGES WHATSOEVER RESULTING FROM OR ARISING OUT OF
OR IN CONNECTION THE DELIVERY OR USE OF THIS INFORMATION.
*/

package com.nwcg.icbs.yantra.api.trackableinventory;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGProcessConfirmShipment implements YIFCustomApi,NWCGITrackableRecordMutator
{
	private Properties myProperties = null;
	private static Logger logger = Logger.getLogger();
	public void setProperties(Properties arg0) throws Exception {
		this.myProperties = arg0;
		if (logger.isDebugEnabled()) logger.debug("Set properties"+this.myProperties);
	}
	
	/*
	 * this method is invoked when the user clicks on confirm shipment for an incident issue or other issue
	 * fetch all the relevent information from the databse i.e. related to incident/issue 
	 * put that in the custom table an UPDATE 
	 */
	public Document updateTrackableRecord(YFSEnvironment env, Document inXML) throws Exception
	{
		try
		{
			Element elemshipmentRoot = inXML.getDocumentElement();
			// strItemSerialNo Added for CR859 :Laxman
			String strItemSerialNo = NWCGConstants.EMPTY_STRING; 
			
			logger.verbose("NWCGProcessConfirmShipment::updateTrackableRecord - input XML "+ XMLUtil.extractStringFromDocument(inXML));

			if(elemshipmentRoot != null)
			{
				// get all the shipment lines
				String strDocumentType = StringUtil.nonNull(elemshipmentRoot.getAttribute("DocumentType"));
				//String strReceivingNode = StringUtil.nonNull(elemshipmentRoot.getAttribute("ReceivingNode"));
				
				boolean bCacheToCacheTransfer = false ;

				if(strDocumentType.equals(NWCGConstants.TRANSFER_ORDER_DOCUMENT_TYPE))
					bCacheToCacheTransfer = true ;
				
				NodeList nlShipmentLines = elemshipmentRoot.getElementsByTagName("ShipmentLine");
				
				if(nlShipmentLines != null)
				{
					logger.verbose("NWCGProcessConfirmShipment::updateTrackableRecord - total # of shipment lines = "+ nlShipmentLines.getLength());
					
					String strOrderNo = NWCGConstants.EMPTY_STRING;
					//fetch the shipment ship date
					String strOrderDate = elemshipmentRoot.getAttribute("ShipDate");
					// for all the shipment lines
					for(int index = 0 ; index < nlShipmentLines.getLength() ; index++ )
					{
						Element elemShipmentLine = (Element) nlShipmentLines.item(index);
						Document getOrderDetailsIP = XMLUtil.createDocument("Order") ;
						Element elemGetOrderDetailsIP = getOrderDetailsIP.getDocumentElement();
						// fetch the order header key to get the order details
						String strOrderHeaderKey = StringUtil.nonNull(elemShipmentLine.getAttribute("OrderHeaderKey"));
						elemGetOrderDetailsIP.setAttribute("OrderHeaderKey",strOrderHeaderKey);
						// get the item id and uom to be used later on
						String strItemID = StringUtil.nonNull(elemShipmentLine.getAttribute("ItemID"));
						String strUOM = StringUtil.nonNull(elemShipmentLine.getAttribute("UnitOfMeasure"));
						//String strUnitPrice = StringUtil.nonNull(elemShipmentLine.getAttribute("UnitPrice"));
						String strUnitPrice = getCompItemPriceSet(env,strItemID,strUOM);
						// incident related attributes
						String strIncidentNo = NWCGConstants.EMPTY_STRING ;
						String strExtnFsAcctCode = NWCGConstants.EMPTY_STRING;
						String strExtnOverrideCode = NWCGConstants.EMPTY_STRING;
						String strExtnBlmAcctCode = NWCGConstants.EMPTY_STRING ;
						String strExtnOtherAcctCode = NWCGConstants.EMPTY_STRING;
						String strYear = NWCGConstants.EMPTY_STRING ;
						String strCustomerId= NWCGConstants.EMPTY_STRING;
						String strExtnIncidentYear = NWCGConstants.EMPTY_STRING;

						logger.verbose("NWCGProcessConfirmShipment::updateTrackableRecord - invoking getOrderDetails with I/P "+XMLUtil.extractStringFromDocument(getOrderDetailsIP));
						
						//invoke getOrderDetails to extract all these details
						env.setApiTemplate(NWCGConstants.API_GET_ORDER_DETAILS,"NWCGProcessConfirmShipment_getOrderDetails");
						Document getOrderDetailsOP = CommonUtilities.invokeAPI(env,NWCGConstants.API_GET_ORDER_DETAILS,getOrderDetailsIP);
						env.clearApiTemplate(NWCGConstants.API_GET_ORDER_DETAILS);
					
						logger.verbose("NWCGProcessConfirmShipment::getOrderDetails O/P: "+XMLUtil.extractStringFromDocument(getOrderDetailsOP));
						
						Element elemGetOrderDetailsOP = getOrderDetailsOP.getDocumentElement(); 
						
						//strOrderDate = elemGetOrderDetailsOP.getAttribute("ReqShipDate");
						strOrderNo = elemGetOrderDetailsOP.getAttribute("OrderNo");
						NodeList nlExtn = elemGetOrderDetailsOP.getElementsByTagName("Extn");
						// fetch all the incident realted info on an order
						if(nlExtn != null && nlExtn.getLength() > 0)
						{
							Element elemExtn = (Element)nlExtn.item(0);
							strIncidentNo = StringUtil.nonNull(elemExtn.getAttribute("ExtnIncidentNo"));
							strExtnFsAcctCode = StringUtil.nonNull(elemExtn.getAttribute("ExtnFsAcctCode"));
							strExtnOverrideCode = StringUtil.nonNull(elemExtn.getAttribute("ExtnOverrideCode"));
							strExtnBlmAcctCode = StringUtil.nonNull(elemExtn.getAttribute("ExtnBlmAcctCode"));
							strExtnOtherAcctCode = StringUtil.nonNull(elemExtn.getAttribute("ExtnOtherAcctCode"));
							strExtnIncidentYear = StringUtil.nonNull(elemExtn.getAttribute("ExtnIncidentYear"));
							
							// get the incident year and customerId 
							Document docIncidentDetails = null;
							Document getIncidentDetailsIP = null;
							if (strIncidentNo.length() > 0 && strIncidentNo != null)
							{
							   getIncidentDetailsIP = XMLUtil.createDocument("NWCGIncidentOrder");
							   Element elemGetIncidentDetails = getIncidentDetailsIP.getDocumentElement();
							   elemGetIncidentDetails.setAttribute("IncidentNo",strIncidentNo);
							   elemGetIncidentDetails.setAttribute("Year",strExtnIncidentYear);
							   logger.verbose("NWCGProcessConfirmShipment::invoking getIncidentOrderDetails with I/P "+XMLUtil.extractStringFromDocument(getIncidentDetailsIP));
							   docIncidentDetails = CommonUtilities.invokeService(env,NWCGConstants.NWCG_GET_INCIDENT_ORDER_SERVICE,getIncidentDetailsIP);
							}
							
							if (docIncidentDetails != null)
							{
							    Element elemIncidentdetails = docIncidentDetails.getDocumentElement();
								logger.verbose("NWCGProcessConfirmShipment::getIncidentOrderDetails O/P "+XMLUtil.extractStringFromDocument(docIncidentDetails));
							    strCustomerId = elemIncidentdetails.getAttribute("CustomerId");
							}  
							strYear = strExtnIncidentYear ; //elemIncidentdetails.getAttribute("Year");
						}
						// fetch the tag serials within the shipment, this will form a unique entry in custom table
						NodeList nlShipmentSerialList = elemShipmentLine.getElementsByTagName("ShipmentTagSerial");
						if(nlShipmentSerialList != null)
						{
							logger.debug("NWCGProcessConfirmShipment::total number of tags "+nlShipmentSerialList.getLength());
							for(int tagIndex = 0 ; tagIndex < nlShipmentSerialList.getLength() ; tagIndex++ )
							{
								Element elemShipmentTag = (Element) nlShipmentSerialList.item(tagIndex);
								// check if the serial number is null
								String strSerialNo = elemShipmentTag.getAttribute("SerialNo");
								if(StringUtil.isEmpty(strSerialNo))
								{
									logger.debug("NWCGProcessConfirmShipment::Continuing as the serial number is empty");
									continue;  // if its null, continue with next record
								}
								//Start CR 859 changes : Laxman
								else
								{
									strItemSerialNo = strSerialNo;
									
								}
								//End CR 859 changes : Laxman
								
								Document docCreateTrackableInventoryIP = getTrackableInventoryIPDocument(env,elemShipmentTag,bCacheToCacheTransfer);
								// populate StatusIncidentNo,StatusIncidentYear,StatusBuyerOrganizationCode from order->incident
								// LastIncidentNo, LastIncidentYear,LastBuyerOrnanizationCode from Order->Incident
								// LastDocumentNo Order/@OrderNo
								// FSAccountCode,BLMAccountCode and OtherAccountCode from Order->Extn
								// get the base xml for update trackable inventory
								
								// Suryasnat: Trackable ID status update Issue 
								//-- start --
								//-- end --
								
								//Start CR 859 changes : Laxman
								
								if((strDocumentType.equals(NWCGConstants.DOCUMENT_TYPE_OTHERISSUE) ) || 
										(strDocumentType.equals(NWCGConstants.DOCUMENT_TYPE_ISSUE) ))
								{
									NodeList nlOrderLines = elemGetOrderDetailsOP.getElementsByTagName("OrderLine");
									
									for(int indexOrderLine = 0 ; indexOrderLine < nlOrderLines.getLength() ; indexOrderLine++ )
									{
										Element elemOrderLine = (Element) nlOrderLines.item(indexOrderLine);
										NodeList nlItemLine = elemOrderLine.getElementsByTagName("Item");
										Element elemItemLine = (Element) nlItemLine.item(0);
										String strItemLineItemID = elemItemLine.getAttribute("ItemID");
										
										if ((strItemLineItemID.equals(strItemID)) && (strItemSerialNo != null))
										{
											NodeList nlLinePriceInfo = elemOrderLine.getElementsByTagName("LinePriceInfo");
											Element elemLinePriceInfo = (Element) nlLinePriceInfo.item(0);
											strUnitPrice = elemLinePriceInfo.getAttribute("UnitPrice");
									
										}
										
									}
								}
								
								//End CR 859 changes :Laxman
								
								logger.debug("NWCGProcessConfirmShipment:: strIncidentNo=["+strIncidentNo+"]");
								Element elemCreateTrackableInventoryIP = docCreateTrackableInventoryIP.getDocumentElement();
								elemCreateTrackableInventoryIP.setAttribute("StatusIncidentNo",strIncidentNo);
								elemCreateTrackableInventoryIP.setAttribute("LastIncidentNo",strIncidentNo);
								elemCreateTrackableInventoryIP.setAttribute("LastDocumentNo",strOrderNo);
								elemCreateTrackableInventoryIP.setAttribute("FSAccountCode",strExtnFsAcctCode);
								elemCreateTrackableInventoryIP.setAttribute("OverrideCode",strExtnOverrideCode);
								elemCreateTrackableInventoryIP.setAttribute("BLMAccountCode",strExtnBlmAcctCode);
								elemCreateTrackableInventoryIP.setAttribute("OtherAccountCode",strExtnOtherAcctCode);
								elemCreateTrackableInventoryIP.setAttribute("LastTransactionDate",strOrderDate);
								elemCreateTrackableInventoryIP.setAttribute("ItemID",strItemID);
								elemCreateTrackableInventoryIP.setAttribute("UnitOfMeasure",strUOM);
								elemCreateTrackableInventoryIP.setAttribute("LastIncidentYear",strYear);
								elemCreateTrackableInventoryIP.setAttribute("StatusIncidentYear",strYear);
								elemCreateTrackableInventoryIP.setAttribute("StatusBuyerOrganizationCode",strCustomerId);
								elemCreateTrackableInventoryIP.setAttribute("LastBuyerOrnanizationCode",strCustomerId);
								elemCreateTrackableInventoryIP.setAttribute("LastIssuePrice",strUnitPrice);
								elemCreateTrackableInventoryIP.setAttribute("SecondarySerial",CommonUtilities.getSecondarySerial(env,strSerialNo,strItemID));
								
								Document opgetAllChildSerialComps = getAllChildSerialComps(env,StringUtil.nonNull(elemShipmentTag.getAttribute("SerialNo")),strItemID);
								//logger.verbose("opgetAllChildSerialComps ::"+XMLUtil.extractStringFromDocument(opgetAllChildSerialComps));
								updateSerialStatusOfCompItems(env,opgetAllChildSerialComps,bCacheToCacheTransfer,docCreateTrackableInventoryIP);
								//logger.verbose("After Get Child Serial Comps");
							
								/*move this to receive transfer order transation
								 * if(bCacheToCacheTransfer)
									elemCreateTrackableInventoryIP.setAttribute("StatusCacheID",strReceivingNode);
								*/

								logger.verbose("NWCGProcessConfirmShipment:: input to updateTrackableInventory service => "+ XMLUtil.extractStringFromDocument(docCreateTrackableInventoryIP));
								
								//logic for 4390 starts here 
								NodeList orderLineNL = elemGetOrderDetailsOP.getElementsByTagName("OrderLine");
								Element orderLineElem;
								Element extnElem;
								
								
								int orderLineNLLen = orderLineNL.getLength();
								
								for(int i=0;i<orderLineNLLen;i++){
									orderLineElem = (Element) orderLineNL.item(i);
									Element itemElem = (Element) orderLineElem.getElementsByTagName("Item").item(0);
									
									if(strItemID.equalsIgnoreCase(itemElem.getAttribute(NWCGConstants.ITEM_ID))){
										extnElem = (Element) orderLineElem.getElementsByTagName("Extn").item(0);
										docCreateTrackableInventoryIP.getDocumentElement().setAttribute("SystemNo", extnElem.getAttribute("ExtnSystemNo"));
										break;
										
									}
								}
								
								logger.verbose("Trackable Update XML after update system no: "+XMLUtil.extractStringFromDocument(docCreateTrackableInventoryIP));
								
								//logic for 4390 ends here 
								
								try
								{
									CommonUtilities.invokeService(env,NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE,docCreateTrackableInventoryIP);
								}
								catch(Exception e)
								{
									// there is an exception while updating a record, now trying to create a record
									// log a message in a queue stating the error and create a new record
									logger.error("***** NCGProcessConfirmShipment CAUGHT EXCEPTION "+e.getMessage());
									e.printStackTrace();
									Document doc = XMLUtil.createDocument("Inbox");
									Element root_element = doc.getDocumentElement();
									root_element.setAttribute("ApiName","NWCGProcessConfirmShipment");
									
									// database size is restricted to 4000 chars but in case of confirm shipment we are getting an exception with 10K lines in it
									if(e.getMessage()!= null && e.getMessage().length() > 3800)
										root_element.setAttribute("DetailDescription",e.getMessage().substring(0,3800));
									else
										root_element.setAttribute("DetailDescription",StringUtil.nonNull(e.getMessage()));
									
									logger.error("***** NCGProcessConfirmShipment Inbox DOC "+XMLUtil.extractStringFromDocument(doc));
									
									CommonUtilities.raiseTrackableAlert(env,doc);
									
									logger.error("NCGProcessConfirmShipment got an exception : now trying to create a record");
									logger.error("***** NCGProcessConfirmShipment Trackable Inventory In Doc "+XMLUtil.extractStringFromDocument(docCreateTrackableInventoryIP));

								}
							}
						}// end if nlShipmentSerialList != null
					}// end while recipt lines
				}// end if shipmentlines not null
			}
		}
		catch(Exception e)
		{
			if(logger.isVerboseEnabled()) logger.error("NWCGProcessConfirmShipment::updateTrackableRecord Caught Exception ");
			e.printStackTrace();
			throw e;
		}
		logger.verbose("NWCGProcessConfirmShipment::updateTrackableRecord - return XML "+ XMLUtil.extractStringFromDocument(inXML));
		return inXML;
	}
	
	//	Suryasnat: Added method for updating serial status of kit component items
	private void updateSerialStatusOfCompItems(YFSEnvironment env,Document inDoc,boolean CacheTransfer,Document TrackableDoc)throws Exception
	{
		NodeList serialNodeList = inDoc.getDocumentElement().getElementsByTagName("Serial");
		Element elemTrackable = TrackableDoc.getDocumentElement();
		logger.verbose("updateSerialStatusOfCompItems::elemTrackable:"+XMLUtil.extractStringFromDocument(TrackableDoc));
		for(int count=0; count < serialNodeList.getLength(); count++){
			String strLastIssuePrice = NWCGConstants.EMPTY_STRING;
			Element SerialElem = (Element)serialNodeList.item(count);
			//logger.verbose("updateSerialStatusOfCompItems::SerialElem :"+XMLUtil.getElementXMLString(SerialElem));

			String serialNo = SerialElem.getAttribute("SerialNo");
			Document ipNWCGGetTrackableItemListService = XMLUtil.getDocument("<NWCGTrackableItem SerialNo=\""+serialNo+"\" />");

			Document opNWCGGetTrackableItemListService = CommonUtilities.invokeService(env,NWCGConstants.NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE,ipNWCGGetTrackableItemListService);

			Element nwcgTrackableItemElem = (Element)opNWCGGetTrackableItemListService.getDocumentElement().getElementsByTagName("NWCGTrackableItem").item(0); 
			//logger.verbose("nwcgTrackableItemElem:: "+XMLUtil.getElementXMLString(nwcgTrackableItemElem));

			//if(nwcgTrackableItemElem.getAttribute("SerialStatus")=="K"){
				Document updateDoc = XMLUtil.createDocument("NWCGTrackableItem");
				Element updateDocElem = updateDoc.getDocumentElement();
								
				if(CacheTransfer)
				{
					updateDocElem.setAttribute("SerialStatus",NWCGConstants.SERIAL_STATUS_TRANSFERRED);
					updateDocElem.setAttribute("SerialStatusDesc","Transferred in Kit");
					updateDocElem.setAttribute("Type",NWCGConstants.TRANSACTION_INFO_TYPE_CACHE_TRANSFERRED);
				}
				else
				{
					updateDocElem.setAttribute("SerialStatus",NWCGConstants.SERIAL_STATUS_ISSUED);
					updateDocElem.setAttribute("SerialStatusDesc","Issued in Kit");
					updateDocElem.setAttribute("Type",NWCGConstants.TRANSACTION_INFO_TYPE_ISSUE);
					// CR 445 - set "Last Issue Price" except cache transfer
					strLastIssuePrice = getCompItemPriceSet(env,nwcgTrackableItemElem.getAttribute("ItemID"),nwcgTrackableItemElem.getAttribute("UnitOfMeasure"));
					logger.debug("strLastIssuePrice: ["+strLastIssuePrice+"]");
				}
				
				updateDocElem.setAttribute("AcquisitionCost",nwcgTrackableItemElem.getAttribute("AcquisitionCost"));
				updateDocElem.setAttribute("AcquisitionDate",nwcgTrackableItemElem.getAttribute("AcquisitionDate"));
				updateDocElem.setAttribute("ItemID",nwcgTrackableItemElem.getAttribute("ItemID"));
				updateDocElem.setAttribute("ItemShortDescription",nwcgTrackableItemElem.getAttribute("ItemShortDescription"));
				updateDocElem.setAttribute("KitItemID",nwcgTrackableItemElem.getAttribute("KitItemID"));
				updateDocElem.setAttribute("KitSerialNo",nwcgTrackableItemElem.getAttribute("KitSerialNo"));
				updateDocElem.setAttribute("OwnerUnitID",nwcgTrackableItemElem.getAttribute("OwnerUnitID"));
				updateDocElem.setAttribute("RevisionNo",nwcgTrackableItemElem.getAttribute("RevisionNo"));
				updateDocElem.setAttribute("SecondarySerial",nwcgTrackableItemElem.getAttribute("SecondarySerial"));
				updateDocElem.setAttribute("SerialNo",nwcgTrackableItemElem.getAttribute("SerialNo"));
				updateDocElem.setAttribute("StatusCacheID",nwcgTrackableItemElem.getAttribute("StatusCacheID"));
				updateDocElem.setAttribute("TrackableItemKey",nwcgTrackableItemElem.getAttribute("TrackableItemKey"));
				updateDocElem.setAttribute("UnitOfMeasure",nwcgTrackableItemElem.getAttribute("UnitOfMeasure"));		
				
				updateDocElem.setAttribute("StatusIncidentNo",elemTrackable.getAttribute("StatusIncidentNo"));
				updateDocElem.setAttribute("LastIncidentNo",elemTrackable.getAttribute("LastIncidentNo"));
				updateDocElem.setAttribute("LastDocumentNo",elemTrackable.getAttribute("LastDocumentNo"));
				updateDocElem.setAttribute("FSAccountCode",elemTrackable.getAttribute("FSAccountCode"));
				updateDocElem.setAttribute("OverrideCode",elemTrackable.getAttribute("OverrideCode"));
				updateDocElem.setAttribute("BLMAccountCode",elemTrackable.getAttribute("BLMAccountCode"));
				updateDocElem.setAttribute("OtherAccountCode",elemTrackable.getAttribute("OtherAccountCode"));
				updateDocElem.setAttribute("LastTransactionDate",elemTrackable.getAttribute("LastTransactionDate"));
				updateDocElem.setAttribute("LastIncidentYear",elemTrackable.getAttribute("LastIncidentYear"));
				updateDocElem.setAttribute("StatusIncidentYear",elemTrackable.getAttribute("StatusIncidentYear"));
				updateDocElem.setAttribute("StatusBuyerOrganizationCode",elemTrackable.getAttribute("StatusBuyerOrganizationCode"));
				updateDocElem.setAttribute("LastBuyerOrnanizationCode",elemTrackable.getAttribute("LastBuyerOrnanizationCode"));
				updateDocElem.setAttribute("LastIssuePrice",strLastIssuePrice); // CR 445 - set "Last Issue Price"

				logger.verbose("updateDoc >> "+XMLUtil.extractStringFromDocument(updateDoc));
				try{
					CommonUtilities.invokeService(env,NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE,updateDoc);
				}catch(Exception e){
					e.printStackTrace();
					Document doc = XMLUtil.createDocument("Inbox");
					Element root_element = doc.getDocumentElement();
					root_element.setAttribute("ApiName","NWCGProcessConfirmShipment");
					// database size is restricted to 4000 chars but in case of confirm shipment we are getting an exception with 10K lines in it
					if(e.getMessage()!= null && e.getMessage().length() > 3800)
						root_element.setAttribute("DetailDescription",e.getMessage().substring(0,3800));
					else
						root_element.setAttribute("DetailDescription",StringUtil.nonNull(e.getMessage()));

					CommonUtilities.raiseTrackableAlert(env,doc);
				}
			//}
		}
	}

	private String getCompItemPriceSet(YFSEnvironment env,String strCompItemID,String strCompItemUOM) throws Exception 
	{
		Document docPriceSetListIP = null;
		Document docPriceSetListOP = null;
		Element elemPriceSetListIP = null;

		Document docPriceSetDtlIP = null;
		Document docPriceSetDtlOP = null;
		Element elemPriceSetDtlIP = null;

		String rtnLastIssuePrice = NWCGConstants.EMPTY_STRING;

		logger.debug("starting getCompItemPriceSet");
		
		// getting the PriceSetKey from the active price set 
		docPriceSetListIP = XMLUtil.createDocument("PriceSet");
		elemPriceSetListIP = docPriceSetListIP.getDocumentElement();
		elemPriceSetListIP.setAttribute("OrganizationCode","NWCG");
		elemPriceSetListIP.setAttribute("ActiveFlag","Y");
		//logger.verbose("docPriceSetListIP :"+XMLUtil.extractStringFromDocument(docPriceSetListIP));

		docPriceSetListOP = CommonUtilities.invokeAPI(env,"getPriceSetList",docPriceSetListIP);
		//logger.verbose("docPriceSetListOP :"+XMLUtil.extractStringFromDocument(docPriceSetListOP));

		String strPriceSetKey = StringUtil.nonNull(XPathUtil.getString(docPriceSetListOP,"PriceSetList/PriceSet/@PriceSetKey"));
		logger.debug("getCompItemPriceSet -> strPriceSetkey ="+strPriceSetKey);

		// getting the PriceSetDetails for the given pricesetkey
		docPriceSetDtlIP = XMLUtil.createDocument("PriceSet");
		elemPriceSetDtlIP = docPriceSetDtlIP.getDocumentElement();
		elemPriceSetDtlIP.setAttribute("PriceSetKey",strPriceSetKey);
		elemPriceSetDtlIP.setAttribute("OrganizationCode","NWCG");
		elemPriceSetDtlIP.setAttribute("ActiveFlag","Y");

		Element el_ItemPriceSetList = docPriceSetDtlIP.createElement("ItemPriceSetList");
		Element el_ItemPriceSet = docPriceSetDtlIP.createElement("ItemPriceSet");
		el_ItemPriceSet.setAttribute("ItemID",strCompItemID);
		el_ItemPriceSet.setAttribute("ProductClass","Supply");
		el_ItemPriceSet.setAttribute("Uom",strCompItemUOM);

		el_ItemPriceSetList.appendChild(el_ItemPriceSet);
		elemPriceSetDtlIP.appendChild(el_ItemPriceSetList);
		//logger.verbose("docPriceSetDtlIP :"+XMLUtil.extractStringFromDocument(docPriceSetDtlIP));

		docPriceSetDtlOP = CommonUtilities.invokeAPI(env,"getPriceSetDetails",docPriceSetDtlIP);
		//logger.verbose("docPriceSetDtlOP :"+XMLUtil.extractStringFromDocument(docPriceSetDtlOP));

		if (docPriceSetDtlOP != null)
		{
			NodeList nlItemPriceSet = docPriceSetDtlOP.getDocumentElement().getElementsByTagName("ItemPriceSet");
			if(nlItemPriceSet != null && nlItemPriceSet.getLength() > 0)
			{
				Element elemItemPriceSet = (Element)nlItemPriceSet.item(0);
				rtnLastIssuePrice = elemItemPriceSet.getAttribute("ListPrice");
			}
		}
		//logger.verbose("rtnLastIssuePrice :["+rtnLastIssuePrice+"]");
		if (rtnLastIssuePrice.equals(NWCGConstants.EMPTY_STRING))
		{	// in case PriceSetDetails is not found for the item. get the price from yfs_item.unit_cost
			// getting the unit_cost from the item table 
			Document docItemIP = XMLUtil.createDocument("Item");
			Element elemItemIP = docItemIP.getDocumentElement();
			elemItemIP.setAttribute("OrganizationCode","NWCG");
			elemItemIP.setAttribute("ItemID",strCompItemID);
			elemItemIP.setAttribute("UnitOfMeasure",strCompItemUOM);

			//-- output template --//
			Document docItemTemplate = XMLUtil.createDocument("Item");
			Element elemItemTemplate = docItemTemplate.getDocumentElement();
			elemItemTemplate.setAttribute("ItemID",NWCGConstants.EMPTY_STRING);
			Element el_PrimaryInformation = docItemTemplate.createElement("PrimaryInformation");
			el_PrimaryInformation.setAttribute("UnitCost",NWCGConstants.EMPTY_STRING);
			elemItemTemplate.appendChild(el_PrimaryInformation);

			Document docItemOP = CommonUtilities.invokeAPI(env,docItemTemplate,"getItemDetails",docItemIP);
			//logger.verbose("docItemOP :"+XMLUtil.extractStringFromDocument(docItemOP));

			rtnLastIssuePrice = StringUtil.nonNull(XPathUtil.getString(docItemOP,"Item/PrimaryInformation/@UnitCost"));
			logger.debug("getCompItemPriceSet1 -> rtnLastIssuePrice ="+rtnLastIssuePrice);
		}
		//logger.verbose("rtnLastIssuePrice1: ["+rtnLastIssuePrice+"]");
		return rtnLastIssuePrice;
	}	
	
	//Suryasnat: Added the method to get all child serials. Copy of getAllChildSerialComponents() in NWCGProcessBlindReturn.java
	private Document getAllChildSerialComps(YFSEnvironment env,String strSerialNo,String strItemID) throws Exception 
	{
		logger.debug("starting getAllChildSerialComponents ");
		
		String strSerialKey = NWCGConstants.EMPTY_STRING;
		Document docGetSerialListOP = null ;
		Document docGetSerialListOP1 = null ;
		Document docGetSerialListIP = null;
		Document docGetSerialListIP1 = null;
		Element elemGetSerialListIP = null;
		Element elemGetSerialListIP1 = null;
		
		docGetSerialListIP = XMLUtil.createDocument("Serial");
		elemGetSerialListIP = docGetSerialListIP.getDocumentElement();
		elemGetSerialListIP.setAttribute("SerialNo",strSerialNo);
		Element el_InvItem=docGetSerialListIP.createElement("InventoryItem");
		elemGetSerialListIP.appendChild(el_InvItem);
		el_InvItem.setAttribute("ItemID",strItemID);
		env.setApiTemplate("getSerialList","NWCGProcessBlindReturn_getSerialList_GetKey");

		logger.verbose("getAllChildSerialComponents::Invoking getSerialList with input "+XMLUtil.extractStringFromDocument(docGetSerialListIP));

		docGetSerialListOP = CommonUtilities.invokeAPI(env,"getSerialList",docGetSerialListIP);

		logger.verbose("getAllChildSerialComponents:: getSerialList output "+XMLUtil.extractStringFromDocument(docGetSerialListOP));
		env.clearApiTemplate("getSerialList");
		// should return only one record
		strSerialKey = StringUtil.nonNull(XPathUtil.getString(docGetSerialListOP,"SerialList/Serial/@GlobalSerialKey"));

		logger.debug("getAllChildSerialComponents got the golbal serial key ="+strSerialKey);
	
		// have to reset the api actually modifies the input document
		docGetSerialListIP1 = XMLUtil.createDocument("Serial");
		elemGetSerialListIP1 = docGetSerialListIP1.getDocumentElement();
		// get all the serial numbers whos parent is this serial number
		// will get all the child components
		// clearing off all the child elements
		elemGetSerialListIP1.setAttribute("SerialNo",NWCGConstants.EMPTY_STRING);
		// getting all the serials whos parent is this serial 
		elemGetSerialListIP1.setAttribute("ParentSerialKey",strSerialKey);
		env.setApiTemplate("getSerialList","NWCGProcessBlindReturn_getSerialList");
		
		docGetSerialListOP1 = CommonUtilities.invokeAPI(env,"getSerialList",docGetSerialListIP1);
		//logger.verbose("Child List - 1st Level "+XMLUtil.extractStringFromDocument(docGetSerialListOP1));
		env.clearApiTemplate("getSerialList");
		
		docGetSerialListOP1 = getSubKitComponents(env,docGetSerialListOP1,docGetSerialListOP1);
		//logger.verbose("Child List - N Level "+XMLUtil.extractStringFromDocument(docGetSerialListOP1));

		return docGetSerialListOP1;
	}
	
	private Document getSubKitComponents(YFSEnvironment env,Document SerialDoc,Document SerialInDoc) throws Exception 
	{
		logger.debug("starting getSubKitComponents ");
		//logger.verbose("getSubKitComponents1");
		NodeList serialNodeList = SerialDoc.getDocumentElement().getElementsByTagName("Serial");
		for(int count=0; count<serialNodeList.getLength(); count++){
			Element SerialElem = (Element)serialNodeList.item(count);
			String serialKey = SerialElem.getAttribute("GlobalSerialKey");
			Document docGetSerialListOP1 = getComponents(env,serialKey);
			//logger.verbose("getSubKitComponents1 1");
			NodeList serialNodeList1 = docGetSerialListOP1.getDocumentElement().getElementsByTagName("Serial");
			if (serialNodeList1.getLength() > 0)
			{   // this is the nested Serial case
				for(int cnt1=0; cnt1<serialNodeList1.getLength(); cnt1++){
					Element SerialElem1 = (Element)serialNodeList1.item(cnt1);
					//logger.verbose("SerialElem1 :"+XMLUtil.getElementXMLString(SerialElem1));
					Element ele1 = SerialInDoc.createElement("Serial");
					SerialInDoc.getDocumentElement().appendChild(ele1);
					XMLUtil.copyElement(SerialInDoc,SerialElem1,ele1);
					//logger.verbose("getSubKitComponents1 2");
					//logger.verbose("SerialInDoc :"+XMLUtil.extractStringFromDocument(SerialInDoc));
				}
				SerialInDoc = getSubKitComponents(env,docGetSerialListOP1,SerialInDoc);
			}
		}
		//logger.verbose("Bottom of getSubKitComponents -> SerialInDoc :"+XMLUtil.extractStringFromDocument(SerialInDoc));
    	return SerialInDoc;
	}	
	
	private Document getComponents(YFSEnvironment env,String strSerialKey) throws Exception 
	{
		Document docGetSerialListIP2 = null;
		Document docGetSerialListOP2 = null;
		Element elemGetSerialListIP2 = null;
		
		logger.debug("starting getSubKitComponents ");
		
		docGetSerialListIP2 = XMLUtil.createDocument("Serial");
		elemGetSerialListIP2 = docGetSerialListIP2.getDocumentElement();

		// getting all the serials whoes parent is this serial 
		elemGetSerialListIP2.setAttribute("ParentSerialKey",strSerialKey);
		env.setApiTemplate("getSerialList","NWCGProcessBlindReturn_getSerialList");
		
		docGetSerialListOP2 = CommonUtilities.invokeAPI(env,"getSerialList",docGetSerialListIP2);
		//logger.verbose("docGetSerialListOP2: "+XMLUtil.extractStringFromDocument(docGetSerialListOP2));
		
		return docGetSerialListOP2;
	}	
	
	/*
	 * This method generates the input xml for creating the record
	 */
	private Document getTrackableInventoryIPDocument(YFSEnvironment env,Element elemShipmentTag,boolean bCacheToCacheTransfer) throws Exception {
		logger.verbose("getTrackableInventoryIPDocument::inXML ==> "+elemShipmentTag);
		Document returnDoc = XMLUtil.createDocument("NWCGTrackableItem");
		Element returnDocElem = returnDoc.getDocumentElement();
		
		returnDocElem.setAttribute("SerialNo",StringUtil.nonNull(elemShipmentTag.getAttribute("SerialNo")));
		returnDocElem.setAttribute("LotAttribute1",StringUtil.nonNull(elemShipmentTag.getAttribute("LotAttribute1")));
		returnDocElem.setAttribute("LotAttribute3",StringUtil.nonNull(elemShipmentTag.getAttribute("LotAttribute3")));
		if(bCacheToCacheTransfer)
		{
			returnDocElem.setAttribute("SerialStatus",NWCGConstants.SERIAL_STATUS_TRANSFERRED);
			returnDocElem.setAttribute("SerialStatusDesc",NWCGConstants.SERIAL_STATUS_TRANSFERRED_DESC);
			returnDocElem.setAttribute("Type",NWCGConstants.TRANSACTION_INFO_TYPE_CACHE_TRANSFERRED);
		}
		else
		{
			returnDocElem.setAttribute("SerialStatus",NWCGConstants.SERIAL_STATUS_ISSUED);
			returnDocElem.setAttribute("SerialStatusDesc",NWCGConstants.SERIAL_STATUS_ISSUED_DESC);
			returnDocElem.setAttribute("Type",NWCGConstants.TRANSACTION_INFO_TYPE_ISSUE);
		}
		//returnDocElem.setAttribute("StatusCacheID",NWCGConstants.EMPTY_STRING);
		
		return returnDoc;
	}

	public Document insertOrUpdateTrackableRecord(YFSEnvironment env, Document doc) throws Exception {
		return updateTrackableRecord(env,doc);
	}
}