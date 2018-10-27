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

package com.nwcg.icbs.yantra.api.wms.wmsinventory;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGGetLocationInventory implements YIFCustomApi {
	
	private Properties myProperties = null;
	private static Logger logger = 
		Logger.getLogger(com.nwcg.icbs.yantra.api.wms.wmsinventory.NWCGGetLocationInventory.class.getName());
	
	public void setProperties(Properties arg0) throws Exception {
		this.myProperties = arg0;
		logger.debug("Set properties: "+this.myProperties);
	}
	
	/**
	 * @author mlathom, drodriguez
	 * @since 1-3-0-1
	 * @return Custom API Output above in a org.w3c.dom.Document object
	 */
	public Document getLocationInventory(YFSEnvironment env, Document inputDoc) throws Exception
	{
		logger.debug("NWCGGetLocationInventory::getLocationInventory, " +
				"Input document : " + XMLUtil.extractStringFromDocument(inputDoc));
		logger.beginTimer("getLocationInventory");
		
		logger.debug("NWCGGetLocationInventory::getLocationInventory - Input Document -->" +XMLUtil.getXMLString(inputDoc));	
	
		Document docAPIOutput				= XMLUtil.createDocument(NWCGConstants.SKU_DEDICATIONS_ELM);
	
		Element elemLocationDedication 		= docAPIOutput.getDocumentElement();
		Element inputDocRootElm 			= inputDoc.getDocumentElement();
		
		String node			= NWCGConstants.EMPTY_STRING;
		String itemID		= NWCGConstants.EMPTY_STRING;
		String locationID	= NWCGConstants.EMPTY_STRING;
		String zoneId		= NWCGConstants.EMPTY_STRING;
		String UOM			= NWCGConstants.EMPTY_STRING;
		String maxRecords	= NWCGConstants.EMPTY_STRING;
		String dedItemId	= NWCGConstants.EMPTY_STRING;
		String dedItemUOM   = NWCGConstants.EMPTY_STRING;
		String dedItemQOH   = NWCGConstants.EMPTY_STRING;
		String dedItemYN    = NWCGConstants.EMPTY_STRING;
		String locQryType	= NWCGConstants.EMPTY_STRING; 
		String itemQryType  = NWCGConstants.EMPTY_STRING; 
	
		//Ensure Location and/or Item is passed in
		locationID	= inputDocRootElm.getAttribute(NWCGConstants.LOCATION_ID);
		locQryType	= inputDocRootElm.getAttribute(NWCGConstants.LOC_ID_QRY_TYPE);
		node		= inputDocRootElm.getAttribute(NWCGConstants.NODE);
		maxRecords  = inputDocRootElm.getAttribute(NWCGConstants.MAXIMUM_RECORDS);
		
		Element elmInvItem = null;
		NodeList nlRootChilds = inputDocRootElm.getElementsByTagName("InventoryItem");
		
		if(nlRootChilds != null)
		{	
			for (int rootNode=0; rootNode < nlRootChilds.getLength(); rootNode++)
			{	
				Node tmpNode = nlRootChilds.item(rootNode);
				logger.debug("NWCGNewGetLocationInventory::getLocationInventory -  NODE NAME:" +tmpNode.getNodeName());
				if (tmpNode.getNodeName().equals("InventoryItem"))
				{
					elmInvItem = (Element) tmpNode; 
					break;
				}
			}
			
			if(elmInvItem != null)
			{
				itemID		= elmInvItem.getAttribute(NWCGConstants.ITEM_ID);
				itemQryType	= elmInvItem.getAttribute(NWCGConstants.ITEM_ID_QRY_TYPE);
				UOM			= elmInvItem.getAttribute(NWCGConstants.UNIT_OF_MEASURE);
				
			}
		}
		
		if (StringUtil.isEmpty(node))
		{
			logger.error("Input Parameter ('Node') Missing!");
			throw new NWCGException("NWCG_SKU_DED_NO_NODE", 
					new Object []{});
		}
		
		if (StringUtil.isEmpty(locationID) && StringUtil.isEmpty(itemID))
		{
			logger.error("Input Parameters (Both 'LocationId' and 'Item') Missing!");
			throw new NWCGException("NWCG_SKU_DED_NO_INPUTS", 
					new Object []{});
		}

		logger.debug("NWCGNewGetLocationInventory::getLocationInventory -  LocationID:" + locationID + " ItemID:"+itemID );
		
		docAPIOutput = getInvForQueriedSKUAndLoc(env,node,locationID,locQryType,itemID,itemQryType,UOM);

		logger.debug("NWCGNewGetLocationInventory::getLocationInventory - OUTPUT Document -->" +XMLUtil.getXMLString(docAPIOutput));
		
		NodeList nlFinalRootChilds = docAPIOutput.getElementsByTagName("SKUDedication");
		
		for(int ctr = 0; ctr < nlFinalRootChilds.getLength();ctr++)
		{
			
			if (ctr >= Integer.parseInt(maxRecords))
			{
				Node tempNode = nlFinalRootChilds.item(ctr);
				Node tempParentNode = tempNode.getParentNode();
				tempParentNode.removeChild(tempNode);
				ctr --;
			}
		}
		
		
		logger.endTimer("getLocationInventory");
		return docAPIOutput;
	}

	private Document getInvForQueriedSKUAndLoc (YFSEnvironment env,String node, String locationID,
			String locQryType,String itemID,String itemQryType,String UOM)throws Exception
	{//Procedures for Use Case 7
		Document docFunctionOutput = null;
		try
		{
			Document docLocationListInput		= XMLUtil.createDocument(NWCGConstants.LOCATION_ELM);
			Document docLocationListOutput		= XMLUtil.createDocument(NWCGConstants.LOCATIONS);
			Document docLocationListTemplate	= XMLUtil.createDocument(NWCGConstants.LOCATIONS);
			
			Document docSingleLocationListInput		= XMLUtil.createDocument(NWCGConstants.LOCATION_ELM);
			Document docSingleLocationListOutput	= XMLUtil.createDocument(NWCGConstants.LOCATIONS);
			
			
			Document docNodeInventoryInput		= XMLUtil.createDocument(NWCGConstants.NODE_INV_ELM);
			Document docNodeInventoryOutput		= XMLUtil.createDocument(NWCGConstants.NODE_INV_ELM);
			Document docNodeInventoryTemplate   = XMLUtil.createDocument(NWCGConstants.NODE_INV_ELM);
			
			Document docGetItemDetailsInput		= XMLUtil.createDocument(NWCGConstants.ITEM);
			Document docGetItemDetailsOutput	= XMLUtil.createDocument(NWCGConstants.ITEM);
			Document docGetItemDetailsTemplate 	= XMLUtil.createDocument(NWCGConstants.ITEM);
			
			Document docGetLocationDetailsInput = XMLUtil.createDocument(NWCGConstants.LOCATION_ELM);
			Document docGetLocationDetailsOutput= XMLUtil.createDocument(NWCGConstants.LOCATION_ELM);
			Document docGetLocationDetailsTemplate = XMLUtil.createDocument(NWCGConstants.LOCATION_ELM);
			
			docFunctionOutput			= XMLUtil.createDocument(NWCGConstants.SKU_DEDICATIONS_ELM);
			
			docGetItemDetailsTemplate	= buildGetItemDetailsTemplate(docGetItemDetailsTemplate);
			docGetLocationDetailsTemplate = buildGetLocationDetailsTemplate(docGetLocationDetailsTemplate);
			
		
			String strOutputDedItemYN	= NWCGConstants.EMPTY_STRING;
			String strOutputLocation	= NWCGConstants.EMPTY_STRING;
			String strOutputEntCode	    = NWCGConstants.EMPTY_STRING;
			String strOutputItem		= NWCGConstants.EMPTY_STRING;
			String strOutputPC 			= NWCGConstants.EMPTY_STRING;
			String strOutputUOM			= NWCGConstants.EMPTY_STRING;
			String strOutputDesc		= NWCGConstants.EMPTY_STRING;
			String strOutputStatus		= NWCGConstants.EMPTY_STRING;
			String strOutputQty			= NWCGConstants.EMPTY_STRING;
			
			//Added LocationKey For CR 690		
			String strOutputLocationKey		= NWCGConstants.EMPTY_STRING;
			
			docNodeInventoryTemplate = buildGetNodeInventoryTemplate(docNodeInventoryTemplate);
			docLocationListTemplate  = buildGetLocationListTemplate(docLocationListTemplate);
			
			docNodeInventoryInput = buildGetNodeInventoryInput(node,locationID,locQryType,itemID,itemQryType, UOM);
			docLocationListInput  = buildGetLocationListInput(node,locationID,locQryType,itemID,itemQryType,UOM);
			
			docNodeInventoryOutput = CommonUtilities.invokeAPI(env, docNodeInventoryTemplate,NWCGConstants.GET_NODE_INVENTORY, docNodeInventoryInput);
			docLocationListOutput  = CommonUtilities.invokeAPI(env,docLocationListTemplate, NWCGConstants.GET_LOCATION_LIST, docLocationListInput);
			
			Element elmRoot = docNodeInventoryOutput.getDocumentElement();
			
			//Element elmInvItem = null;
			NodeList nlRootChilds = elmRoot.getElementsByTagName("LocationInventory");
			
		
			for (int rootNode=0; rootNode < nlRootChilds.getLength(); rootNode++)
			{ 
				logger.debug("NWCGNewGetLocationInventory::getLocationInventory - IN THE FOR LOOP"); 
				
				Node tmpNode = nlRootChilds.item(rootNode);
				//String tmpZoneId = ((Element)tmpNode).getAttribute("ZoneId");
				strOutputLocation = ((Element)tmpNode).getAttribute(NWCGConstants.LOCATION_ID);
				strOutputEntCode  = NWCGConstants.ENTERPRISE_CODE;
				strOutputItem	  =	((Element)(tmpNode.getFirstChild())).getAttribute(NWCGConstants.ITEM_ID);
				strOutputPC		  =	((Element)(tmpNode.getFirstChild())).getAttribute(NWCGConstants.PRODUCT_CLASS);
				strOutputUOM	  =	((Element)(tmpNode.getFirstChild())).getAttribute(NWCGConstants.UNIT_OF_MEASURE);
				strOutputDesc	  = ((Element)(((tmpNode.getFirstChild()).getFirstChild()).getFirstChild())).getAttribute(NWCGConstants.SHORT_DESCRIPTION);
				strOutputStatus   = ((Element)((tmpNode.getFirstChild()).getNextSibling())).getAttribute("InventoryStatus");
				strOutputQty	  = ((Element)tmpNode).getAttribute(NWCGConstants.QUANTITY);
				strOutputLocationKey	  = ((Element)tmpNode).getAttribute("LocationKey");
				strOutputDedItemYN= NWCGConstants.YES;
				
				//if(tmpZoneId.equals("OVERFLOW")||tmpZoneId.equals("RECEIVE-ZONE"))
				//	strOutputDedItemYN= NWCGConstants.NO;
				
				// Another SKU DEDICATION lookup for this item and this location is needed to detemine if the NODE Inventory record is 
				// is for a dedicatd location..If the out doc has a SKUDedication Node, then it is dedicated, else it is not
				
				docSingleLocationListInput  = buildGetLocationListInput(node,strOutputLocation,"",strOutputItem,"",UOM);
				docSingleLocationListOutput  = CommonUtilities.invokeAPI(env,docLocationListTemplate, NWCGConstants.GET_LOCATION_LIST, docSingleLocationListInput);
				
				Element elmSingleSKURoot = docSingleLocationListOutput.getDocumentElement();
				NodeList nlSingleSKURootChilds = elmSingleSKURoot.getElementsByTagName("SKUDedication");
				
				NodeList nlSingleLocation = elmSingleSKURoot.getElementsByTagName("Location");
				
				
				if(nlSingleSKURootChilds.getLength() == 0)
				{
					strOutputDedItemYN= NWCGConstants.NO;
					//strOutputLocationKey = ((Element)tempNode).getAttribute("LocationKey");
					//Need to Call getLocationDetails with Node and LocationID to get the Location Key for clickthru...
					//Because the location IS NOT Dedication, the Location INformation Is not returned in the loc list
					
					docGetLocationDetailsInput = buildGetLocationDetailsInput(strOutputLocation,node);
					docGetLocationDetailsOutput = CommonUtilities.invokeAPI(env,docGetLocationDetailsTemplate, NWCGConstants.API_GET_LOCATION_DETAILS, docGetLocationDetailsInput);
					Element elemLocationOut = docGetLocationDetailsOutput.getDocumentElement();
					strOutputLocationKey = elemLocationOut.getAttribute("LocationKey");
					
				}
				else
				{
					strOutputDedItemYN= NWCGConstants.YES;
					Node tempNode = nlSingleLocation.item(0);
					strOutputLocationKey = ((Element)tempNode).getAttribute("LocationKey");
				}
				
				docFunctionOutput = addRowToOutput(docFunctionOutput,node,strOutputLocation,strOutputEntCode,strOutputItem,strOutputPC,strOutputUOM,strOutputDesc,strOutputStatus, strOutputQty,strOutputDedItemYN, strOutputLocationKey);
				
			}
			// Now we need to go thru the LocationList... For Each SKUDedication....
			// If the Item passes the Item Query, and the location passes the location query
			// Check to see if in Output list for that location.
			// If not, get the itemdetails for the item, and add to the Output with a Qty of 0.
			
			
			Element elmSKURoot = docLocationListOutput.getDocumentElement();
			NodeList nlSKURootChilds = elmSKURoot.getElementsByTagName("SKUDedication");
			
			logger.debug("NWCGNewGetLocationInventory::getLocationInventory - There are " + nlSKURootChilds.getLength() +" SKU Dedications"); 
			
			for (int rootNode=0; rootNode < nlSKURootChilds.getLength(); rootNode++)
			{ 
				Node tmpNode = nlSKURootChilds.item(rootNode);
				strOutputItem	  =	((Element)tmpNode).getAttribute("ItemId");
				strOutputLocation =	((Element)tmpNode.getParentNode().getParentNode()).getAttribute("LocationId");
				strOutputLocationKey = 	((Element)tmpNode.getParentNode().getParentNode()).getAttribute("LocationKey");
				strOutputStatus	  = ((Element)tmpNode).getAttribute("Status");
				strOutputUOM	  = ((Element)tmpNode).getAttribute("UnitOfMeasure");
				
				logger.debug("NWCGNewGetLocationInventory::getLocationInventory - strOutputItem " + strOutputItem);
				logger.debug("NWCGNewGetLocationInventory::getLocationInventory - strOutputLocation " + strOutputLocation);
				logger.debug("NWCGNewGetLocationInventory::getLocationInventory - strOutputLocationKey " + strOutputLocationKey);
				//System.out.println("NWCGNewGetLocationInventory::getLocationInventory - strOutputLocationKey " + strOutputLocationKey);
				logger.debug("NWCGNewGetLocationInventory::getLocationInventory - itemQryType " + itemQryType);
				logger.debug("NWCGNewGetLocationInventory::getLocationInventory - locQryType " + locQryType);
				logger.debug("NWCGNewGetLocationInventory::getLocationInventory - itemID " + itemID);
				logger.debug("NWCGNewGetLocationInventory::getLocationInventory - locationID " + locationID);
				
				if(locationID.equals(""))
					locQryType="";
				
				if(itemID.equals(""))
					itemQryType="";
						
				if(itemQryType.equals("LIKE"))
				{//contains
					logger.debug("NWCGNewGetLocationInventory::getLocationInventory - In the item LIKE QUERY...");
					if (locQryType.equals("LIKE"))
					{
						if(!strOutputItem.contains(itemID) || !strOutputLocation.contains(locationID))
							continue;
					}
					else if (locQryType.equals("FLIKE"))
					{
						if(!strOutputItem.contains(itemID) || !strOutputLocation.startsWith(locationID))
						{	
							continue;
						}
					}
					else //The the query modifier must be blank or is
					{
						if(!strOutputItem.contains(itemID))
								continue;
						
						if (!strOutputLocation.equals("") && !locationID.equals("") &&!strOutputLocation.equals(locationID)) 
						{
							continue;
						}
								
					}
				}
				else if(itemQryType.equals("FLIKE"))
				{//contains
					logger.debug("NWCGNewGetLocationInventory::getLocationInventory - In the item FLIKE QUERY...");
					if (locQryType.equals("LIKE"))
					{
						if(!itemID.equals(""))
						{
							if(!strOutputItem.startsWith(itemID) || !strOutputLocation.contains(locationID))
							 continue;
						}
					}
					if (locQryType.equals("FLIKE"))
					{
						if(!itemID.equals(""))
						{
							if(!strOutputItem.startsWith(itemID) || !strOutputLocation.startsWith(locationID))
							continue;
						}
					}
					else //The the query modifier must be blank or is
					{
						if(!strOutputItem.contains(itemID))
								continue;
				
						if (!strOutputLocation.equals("") && !locationID.equals("") &&!strOutputLocation.equals(locationID)) 
						{
							continue;
						}
					}
				}
				else// tThe Item query modifier must be blank or is
				{
					logger.debug("NWCGNewGetLocationInventory::getLocationInventory - In the item BLANK QUERY...");
					
					if (locQryType.equals("LIKE"))
					{
						logger.debug("NWCGNewGetLocationInventory::getLocationInventory - In the LOCATION Like...");
						if(!(itemID.equals("")))
						{
							if(!strOutputItem.equals(itemID))
								continue;
						}
						if (!strOutputLocation.contains(locationID))
						{
							continue;
						}
					}
					else if (locQryType.equals("FLIKE"))
					{
						if(!itemID.equals(""))
						{
							if(!strOutputItem.equals(itemID))
								continue;
						}
						
						if(!strOutputLocation.startsWith(locationID))
							continue;
						
					}
					else //The the query modifier must be blank or is
					{
						logger.debug("NWCGNewGetLocationInventory::getLocationInventory - In the Last else..");
						if(!itemID.equals(""))
						{
							if(!strOutputItem.contains(itemID))
								continue;
						}
						
						if (!strOutputLocation.equals("") && !locationID.equals("") &&!strOutputLocation.equals(locationID)) 
						{
							continue;
						}
					}
				}
				
				if (isAlreadyOutput(docFunctionOutput,strOutputLocation,strOutputItem))
				{	
					continue;
				}
				else
				{	//get the Item Details to finish building out the output line
					logger.debug("NWCGNewGetLocationInventory::getLocationInventory -> YES we do  pass the is AlreadyOutput");
					
					docGetItemDetailsInput		= buildGetItemDetailsInput(strOutputItem,strOutputUOM); 
					
					logger.debug("NWCGNewGetLocationInventory::getLocationInventory - getItemDetails INPUT Document -->" +XMLUtil.getXMLString(docGetItemDetailsInput));	
					logger.debug("NWCGNewGetLocationInventory::getLocationInventory - getItemDetails TEMPLATE Document -->" +XMLUtil.getXMLString(docGetItemDetailsTemplate));
					
					try
					{
						docGetItemDetailsOutput = CommonUtilities.invokeAPI(env,docGetItemDetailsTemplate, NWCGConstants.API_GET_ITEM_DETAILS, docGetItemDetailsInput);
					
					
						logger.debug("NWCGNewGetLocationInventory::getLocationInventory - getItemDetails OUTPUT Document -->" +XMLUtil.getXMLString(docGetItemDetailsOutput));
					
						Element elmItemRoot = docGetItemDetailsOutput.getDocumentElement();
						NodeList nlItemRootChilds = elmItemRoot.getElementsByTagName("PrimaryInformation");
					
						logger.debug("NWCGNewGetLocationInventory::getLocationInventory - getItemDetails NL Length: " +nlItemRootChilds.getLength());
					
						for (int innerRootNode=0; innerRootNode < nlItemRootChilds.getLength(); innerRootNode++)
						{ 
							Node innerTmpNode  = nlItemRootChilds.item(innerRootNode);
							strOutputPC	  = ((Element)innerTmpNode).getAttribute("DefaultProductClass");
							strOutputDesc = ((Element)innerTmpNode).getAttribute("ShortDescription");
							strOutputQty  = "0";
							strOutputStatus = "N/A";
							strOutputDedItemYN= NWCGConstants.YES;	
						}
					}
					catch( Exception e)
					{
						strOutputPC	  = "N/A";
						strOutputDesc = "-- ITEM NOT FOUND --";
						strOutputStatus = "N/A";
						strOutputQty  = "0";
						strOutputDedItemYN= NWCGConstants.YES;
					}
					
					
					docFunctionOutput = addRowToOutput(docFunctionOutput,node,strOutputLocation,strOutputEntCode,strOutputItem,strOutputPC,strOutputUOM,strOutputDesc,strOutputStatus,strOutputQty,strOutputDedItemYN,strOutputLocationKey);
					
				}
			}
				
		}
		catch(ParserConfigurationException pce)
		{
			pce.printStackTrace();
		}
			
		return docFunctionOutput;	
			
	}
	private Boolean isAlreadyOutput(Document docAPIOutput, String loctionID, String itemID)
	{
		Element elmRoot = docAPIOutput.getDocumentElement();
		NodeList nlRootChilds = elmRoot.getElementsByTagName("SKUDedication");
		
		for (int rootNode=0; rootNode < nlRootChilds.getLength(); rootNode++)
		{ 
						
			Node 	tmpNode = nlRootChilds.item(rootNode);
			String	loc	 = ((Element)tmpNode).getAttribute("LocationId");
			String	item = ((Element)tmpNode).getAttribute("ItemID");
			
			if(loc.equals(loctionID) &&item.equals(itemID) )
			{
				return true;
			}
			
		}
		
		return false;
	}
	
	private Document addRowToOutput (Document docFunctionOutput, String strnode,String locationID, String orgCode, String itemID,
			String prodClass, String uom, String desc, String status, String qty, String dedYN,String locKey) throws Exception
	{
		
			Element el_SKUDedication = docFunctionOutput.getDocumentElement();
			Element e_SKUDed = docFunctionOutput.createElement(NWCGConstants.SKU_DEDICATION_ELM);
			el_SKUDedication.appendChild(e_SKUDed);
			e_SKUDed.setAttribute(NWCGConstants.NODE, strnode);
			e_SKUDed.setAttribute(NWCGConstants.LOCATION_ID, locationID);
			e_SKUDed.setAttribute("LocationKey", locKey);
			e_SKUDed.setAttribute(NWCGConstants.ORG_CODE, orgCode);
			e_SKUDed.setAttribute(NWCGConstants.ITEM_ID, itemID);
			e_SKUDed.setAttribute(NWCGConstants.PRODUCT_CLASS, prodClass);
			e_SKUDed.setAttribute(NWCGConstants.UNIT_OF_MEASURE, uom);
			e_SKUDed.setAttribute(NWCGConstants.SHORT_DESCRIPTION, desc);
			e_SKUDed.setAttribute(NWCGConstants.STATUS_ATTR, status);
			e_SKUDed.setAttribute(NWCGConstants.QUANTITY, qty);
			e_SKUDed.setAttribute("ItemDedicated", dedYN);
	
			return docFunctionOutput;
	
		
	}
			
	private Document buildGetItemDetailsInput(String itemID, String uom) throws Exception
	{
		Document docGetItemDetailsInput		= XMLUtil.createDocument(NWCGConstants.ITEM);
		Element e_item = docGetItemDetailsInput.getDocumentElement();
		e_item.setAttribute(NWCGConstants.ITEM_ID, itemID);
		e_item.setAttribute(NWCGConstants.ORG_CODE, NWCGConstants.ENTERPRISE_CODE);
		e_item.setAttribute(NWCGConstants.UNIT_OF_MEASURE, uom);
		
		return docGetItemDetailsInput;
	}
	
	private Document buildGetItemDetailsTemplate(Document docGetItemDetailsTemplate)throws Exception
	{
		Element e_getItemDetailsTemplate = docGetItemDetailsTemplate.getDocumentElement();
		e_getItemDetailsTemplate.setAttribute(NWCGConstants.ORG_CODE, NWCGConstants.EMPTY_STRING);
		e_getItemDetailsTemplate.setAttribute(NWCGConstants.ITEM_ID, NWCGConstants.EMPTY_STRING);
		e_getItemDetailsTemplate.setAttribute(NWCGConstants.UNIT_OF_MEASURE, NWCGConstants.EMPTY_STRING);
		Element e_primaryInformation = docGetItemDetailsTemplate.createElement(NWCGConstants.PRIMARY_INFO);
		e_getItemDetailsTemplate.appendChild(e_primaryInformation);
		e_primaryInformation.setAttribute(NWCGConstants.DEFAULT_PROD_CLASS, NWCGConstants.EMPTY_STRING);
		e_primaryInformation.setAttribute(NWCGConstants.SHORT_DESCRIPTION, NWCGConstants.EMPTY_STRING);
		
		logger.debug("NWCGGetLocationInventory::getLocationInventory - Template for getItemDetails : " 
				+ XMLUtil.extractStringFromDocument(docGetItemDetailsTemplate));
		
		return docGetItemDetailsTemplate;
	}
		
	private Document buildGetLocationListInput (String node,String locationID,String locQryType,String itemID,String itemQryType, String UOM)throws Exception
	{
		Document docLocationListInput		= XMLUtil.createDocument(NWCGConstants.LOCATION_ELM);
		//Location attributes
		Element elemGetLocationList = docLocationListInput.getDocumentElement();
		elemGetLocationList.setAttribute(NWCGConstants.LOCATION_ID, locationID);
		elemGetLocationList.setAttribute(NWCGConstants.LOC_ID_QRY_TYPE, locQryType);		
		elemGetLocationList.setAttribute(NWCGConstants.NODE, node);
		
		Element el_SKUDedications = docLocationListInput.createElement(NWCGConstants.SKU_DEDICATIONS_ELM);
		elemGetLocationList.appendChild(el_SKUDedications);
		Element e_SKUDedication = docLocationListInput.createElement(NWCGConstants.SKU_DEDICATION_ELM);
		el_SKUDedications.appendChild(e_SKUDedication);
		e_SKUDedication.setAttribute(NWCGConstants.ITEM_ID, itemID);
		e_SKUDedication.setAttribute(NWCGConstants.UNIT_OF_MEASURE, UOM);
		
		logger.debug("NWCGGetLocationInventory::getLocationInventory - Input document to getLocationList : " 
				+ XMLUtil.extractStringFromDocument(docLocationListInput));		
		
		return docLocationListInput;
		
	}
	private Document buildGetLocationDetailsInput(String locationID,String node)throws Exception
	{
		
		Document docGetLocDetailsInput = null;
		
		docGetLocDetailsInput = XMLUtil.createDocument(NWCGConstants.LOCATION_ELM);
		Element elemLocation = docGetLocDetailsInput.getDocumentElement();
		elemLocation.setAttribute(NWCGConstants.LOCATION_ID, locationID);
		elemLocation.setAttribute(NWCGConstants.NODE, node);
		
		return docGetLocDetailsInput;
	}
	
	
	private Document buildGetLocationDetailsTemplate(Document docGetLocationDetailsTemplate) throws Exception
	{
		Element elemLocationTemplate = docGetLocationDetailsTemplate.getDocumentElement();
		elemLocationTemplate.setAttribute("LocationKey", NWCGConstants.EMPTY_STRING);
		elemLocationTemplate.setAttribute(NWCGConstants.NODE,NWCGConstants.EMPTY_STRING);
		elemLocationTemplate.setAttribute(NWCGConstants.LOCATION_ID,NWCGConstants.EMPTY_STRING);
		
		return docGetLocationDetailsTemplate;
	}
	
	
	private Document buildGetNodeInventoryInput(String node,String locationID,String locQryType,String itemID,String itemQryType, String UOM)throws Exception
	{
			Document docNodeInventoryInput = null;
		
			docNodeInventoryInput		=  XMLUtil.createDocument(NWCGConstants.NODE_INV_ELM);
			//NodeInventory attributes
			Element elemNodeInventory = docNodeInventoryInput.getDocumentElement();
			elemNodeInventory.setAttribute(NWCGConstants.ENTERPRISE_CODE_STR, NWCGConstants.ENTERPRISE_CODE);
			elemNodeInventory.setAttribute(NWCGConstants.IGNORE_ORDERING, NWCGConstants.YES);
			elemNodeInventory.setAttribute(NWCGConstants.LOCATION_ID, locationID);
			elemNodeInventory.setAttribute(NWCGConstants.LOC_ID_QRY_TYPE, locQryType);
			elemNodeInventory.setAttribute(NWCGConstants.NODE, node);
		
			Element elmInventory = docNodeInventoryInput.createElement(NWCGConstants.INVENTORY_ATTR);
			elemNodeInventory.appendChild(elmInventory);
			Element elmInventoryItem = docNodeInventoryInput.createElement(NWCGConstants.INVENTORY_ITEM_ATTR);
			elmInventory.appendChild(elmInventoryItem);
			elmInventoryItem.setAttribute(NWCGConstants.ITEM_ID, itemID);
			elmInventoryItem.setAttribute(NWCGConstants.ITEM_ID_QRY_TYPE,itemQryType);
			elmInventoryItem.setAttribute(NWCGConstants.UNIT_OF_MEASURE,UOM);
			
			
			logger.debug("NWCGGetLocationInventory::getLocationInventory - Input document to getNodeInventory : " 
					+ XMLUtil.extractStringFromDocument(docNodeInventoryInput));
		
		return docNodeInventoryInput;
		
	}

	private Document buildGetNodeInventoryTemplate(Document docNodeInventoryTemplate)throws Exception
	{
		//Build up getNodeInventory template
		//Root Element & Attributes
		Element elemNodeInvTemplate = docNodeInventoryTemplate.getDocumentElement();
		elemNodeInvTemplate.setAttribute(NWCGConstants.ENTERPRISE_CODE_STR,NWCGConstants.EMPTY_STRING);
		elemNodeInvTemplate.setAttribute(NWCGConstants.NODE,NWCGConstants.EMPTY_STRING);
		//LocationInventoryList element
		Element el_LocationInventoryList = docNodeInventoryTemplate.createElement(NWCGConstants.LOC_INV_LIST);
		elemNodeInvTemplate.appendChild(el_LocationInventoryList);
		//LocationInventory element & Attributes
		Element e_LocationInventory=docNodeInventoryTemplate.createElement("LocationInventory");
	 	el_LocationInventoryList.appendChild(e_LocationInventory);
	 	e_LocationInventory.setAttribute(NWCGConstants.INV_ITEM_KEY,NWCGConstants.EMPTY_STRING);
	 	e_LocationInventory.setAttribute(NWCGConstants.LOCATION_ID,NWCGConstants.EMPTY_STRING);
	 	e_LocationInventory.setAttribute(NWCGConstants.QUANTITY,NWCGConstants.EMPTY_STRING);
	 	e_LocationInventory.setAttribute("ZoneId",NWCGConstants.EMPTY_STRING);
	 	//InventoryItem element and attributes
	 	Element el_InventoryItem=docNodeInventoryTemplate.createElement(NWCGConstants.INVENTORY_ITEM_ATTR);
	 	e_LocationInventory.appendChild(el_InventoryItem);
	 	el_InventoryItem.setAttribute(NWCGConstants.ITEM_ID,NWCGConstants.EMPTY_STRING);
	 	el_InventoryItem.setAttribute(NWCGConstants.PRODUCT_CLASS,NWCGConstants.EMPTY_STRING);
	 	el_InventoryItem.setAttribute(NWCGConstants.UNIT_OF_MEASURE,NWCGConstants.EMPTY_STRING);
	 	//Item element and attributes
	 	Element el_Item=docNodeInventoryTemplate.createElement(NWCGConstants.ITEM);
	 	el_InventoryItem.appendChild(el_Item);
	 	el_Item.setAttribute(NWCGConstants.ITEM_ID,NWCGConstants.EMPTY_STRING);
		//PrimaryInfo element and Attributes
	 	Element e_PrimaryInformation=docNodeInventoryTemplate.createElement(NWCGConstants.PRIMARY_INFORMATION);
	 	el_Item.appendChild(e_PrimaryInformation);
	 	e_PrimaryInformation.setAttribute(NWCGConstants.SHORT_DESCRIPTION,NWCGConstants.EMPTY_STRING);
	 	
		
		Element e_SummaryAttributes = docNodeInventoryTemplate.createElement(NWCGConstants.SUMMARY_ATTRIBUTES);
		e_LocationInventory.appendChild(e_SummaryAttributes);
		e_SummaryAttributes.setAttribute(NWCGConstants.INVENTORY_STATUS,NWCGConstants.EMPTY_STRING);

		logger.debug("NWCGGetLocationInventory::getLocationInventory - Template for getNodeInventory : " 
				+ XMLUtil.extractStringFromDocument(docNodeInventoryTemplate));
	 	
	 	return docNodeInventoryTemplate;
	}
	
	private Document buildGetLocationListTemplate(Document docLocationListTemplate) throws Exception
	{
		//Build up getLocationList template
		Element elemLocationListTemplate = docLocationListTemplate.getDocumentElement();
		//Location Element and Attribures
		Element e_Location = docLocationListTemplate.createElement("Location");
		elemLocationListTemplate.appendChild(e_Location);
		e_Location.setAttribute("LocationId",NWCGConstants.EMPTY_STRING);
		e_Location.setAttribute("LocationKey",NWCGConstants.EMPTY_STRING);
		e_Location.setAttribute("Node",NWCGConstants.EMPTY_STRING);
		e_Location.setAttribute("ZoneId",NWCGConstants.EMPTY_STRING);
		//SKUDedications Element
		Element el_SKUDedicationsList = docLocationListTemplate.createElement("SKUDedications");
		e_Location.appendChild(el_SKUDedicationsList);		
		//SkeDedication Element & Attributes
		Element e_SKUDedication = docLocationListTemplate.createElement("SKUDedication");
		el_SKUDedicationsList.appendChild(e_SKUDedication);	
		e_SKUDedication.setAttribute("EnterpriseCode",NWCGConstants.EMPTY_STRING);
		e_SKUDedication.setAttribute("ItemId",NWCGConstants.EMPTY_STRING);
		e_SKUDedication.setAttribute("ProductClass",NWCGConstants.EMPTY_STRING);
		e_SKUDedication.setAttribute("UnitOfMeasure",NWCGConstants.EMPTY_STRING);
		
	 	logger.debug("NWCGGetLocationInventory::getLocationInventory - Template for getLocationList : " 
				+ XMLUtil.extractStringFromDocument(docLocationListTemplate));
		
		return docLocationListTemplate;
	}
	
}
		
