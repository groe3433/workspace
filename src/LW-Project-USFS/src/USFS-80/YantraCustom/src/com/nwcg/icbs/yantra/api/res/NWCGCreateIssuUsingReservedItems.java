/*
 * ClearContextAPI.java
 *
 * This software is the confidential and proprietary information of
 * Yantra Corp. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Yantra.
 */ 

/*Author :- Suresh Pillai*/

package com.nwcg.icbs.yantra.api.res;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;


public class NWCGCreateIssuUsingReservedItems implements YIFCustomApi {
    	
	private Properties _properties;
	private static Logger logger = Logger.getLogger(NWCGCreateIssuUsingReservedItems.class.getName());

	/**
	 * The logic is as follows :-
	 * The input xml to this API contains the list of itemKeys and reservationID 
	 * The input xml looks like this :-
	 * <CreateOrder Count="2" Item_0="200605271829216698" Item_1="2006052917572912373" ReservationID="SureshRese1"/>
	 * Then we use the reservation ID to call the custom API NWCGgetReservedItems to get whole list of items reserved 
	 * using the searched reservationID
	 * The input xml for the NWCGgetReservationId is as follows:-
	 * <Item IgnoreOrdering="Y" ItemID="SureshRese1" ItemIDQryType=""     MaximumRecords="30" reservationId="SureshRese1"/>
	 * The output from the NWCGgetReservedItemsAPI is as follows:-
	 *<ItemList TotalNumberOfRecords="">
	 *<Item ItemID="PWR-40" ItemKey="200605271829216698" OrganizationCode="DEFAULT" QTY="10"
	 *UnitOfMeasure="" orderKey="2006060610121118112"/>
	 *<Item ItemID="PWR-41" ItemKey="2006052917572912373" OrganizationCode="DEFAULT" QTY="12"
	 *UnitOfMeasure="" orderKey="2006060610121118112"/>
	 *</ItemList>
	 *Now we put the list of all the items and the corresponding item into in the FinalHashMap
	 *Then using the list of selected items we search for the key in the HashMap
	 *Using the list of items and item detail we create input xml for creating order
	 *from the return xml we get the orderheaderkey and send it out
	 */
	public Document createOrder(YFSEnvironment env, Document inXML) throws YFSException {

		if(logger.isVerboseEnabled()){
			logger.debug("JDBG: Start of NWCGCreateIssuUsingReservedItems:createOrder API. Input XML ->"+XMLUtil.getXMLString(inXML));
		}//End if 
		if (env == null){
			logger.error("YFSEnvironment is null");
			throw new NWCGException("NWCG_RESERVATION_ENV_IS_NULL");
		}//End if
		if (inXML == null){
			logger.error("Input Document is null");
			throw new NWCGException("NWCG_RESERVATION_INPUT_DOC_NULL");
		}//End if
		
		Document rt_Order = null;
		Document CreateOrderInputDoc = null;
		Document CreateOrderOut_xml = null;
		String reservationID = null;
		List key = null;
		String Item = "Item_";
		int totalItemSelected = 0;
		Document OutDoc = null;
		List itemDetails = new ArrayList();          
		HashMap FinalMap = new HashMap();
		String itemid = "";
		String reservedQty = "";
		//String reservationId = "";
		String OrgCode = ""; 
		String UOM = "";
		String ProductClass ="";
		String shipNode = "";
		Element SelectedItem = null;
		int selectedQty = 0;
			
		Element rootNode = inXML.getDocumentElement();
	        
		try{
			Element Item1 = (Element) XPathUtil.getNode(rootNode,"/ItemList/Item");
			reservationID = Item1.getAttribute("reservationID");
			if(logger.isVerboseEnabled()){
				logger.debug("JDBG: reservationID ->"+reservationID);
			}
			//totalItemSelected = Integer.parseInt(Order.getAttribute("Count"));
			////System.out.println("totalItemSelected:- "+totalItemSelected);	        
			
			
			//--------------------------------------------------------------------
			//Prepare input xml for NWCGgetReservedItems Service
			Document rt_Item = XMLUtil.newDocument();
			Element el_Item=rt_Item.createElement("Item");
			rt_Item.appendChild(el_Item);
			el_Item.setAttribute("ItemID",reservationID);
			el_Item.setAttribute("ItemIDQryType","");
			el_Item.setAttribute("MaximumRecords","30");
			//el_Item.setAttribute("ItemID",reservationID);
			el_Item.setAttribute("reservationId",reservationID);
			System.out.println("***6.5***- InXmlToGetReservedItems is ->"+XMLUtil.getXMLString(rt_Item));
			
			//Invoke service to get the list of items associated with the reservation id
			//OutDoc=  CommonUtilities.invokeService(env,"NWCGgetReservedItems",rt_Item);
			OutDoc=  CommonUtilities.invokeService(env,NWCGConstants.NWCG_GET_RESERVED_ITEMS,rt_Item);
			System.out.println("***7***- OutDoc is ->"+XMLUtil.getXMLString(OutDoc));
			
			//get the list of items in the output document
			Element rootElement = OutDoc.getDocumentElement();
			Element itemNode = null;
			NodeList itemList = XPathUtil.getNodeList(rootElement,"./Item");
			//GS - add check if(itemList!=null && itemList.getLength()>0)
			int itemListLength = itemList.getLength();
			System.out.println("***8** list length ->"+itemListLength);	 
			
			for(int i=0;i<itemListLength;i++)
			{
				System.out.println("***8.1**");
				//GS - add check if(itemList!=null && itemList.getLength()>0)
				itemNode = (Element)itemList.item(i);
				System.out.println("***8.2**"+itemNode.getAttribute("ItemID"));
				itemDetails.add(itemNode.getAttribute("ItemID"));
				//System.out.println("***9**");
				itemDetails.add(itemNode.getAttribute("ItemKey"));
				//System.out.println("***10**");
				itemDetails.add(itemNode.getAttribute("OrganizationCode"));
				//System.out.println("***11**");
				if(!(itemNode.getAttribute("QTY").equals("")) && itemNode.getAttribute("QTY")!=null){
					itemDetails.add(itemNode.getAttribute("QTY"));
				}else{
					itemDetails.add("0");
					continue;
				}//End if 
				//System.out.println("***12**");
				itemDetails.add(itemNode.getAttribute("UnitOfMeasure"));
				//System.out.println("***13**");
				itemDetails.add(itemNode.getAttribute("ProductClass"));
				//System.out.println("***14**");
				itemDetails.add(itemNode.getAttribute("shipNode"));
				//System.out.println("***15**");	
				//Creating Key(ItemKey) value(Hashset object-itemDetail having info about each item) pair
				//Building up "FinalMap" object having info of all the items having same reservationID
				FinalMap.put(itemNode.getAttribute("ItemKey"),itemDetails);				//Note
				itemDetails =new ArrayList();
				System.out.println("***16**");
			}//End For Loop- Forming final HashMap and Hashset having complete list of reserved items
			
			System.out.println("FinalMap.size ->" + FinalMap.size());
			
			
			//---------------------------------------------------------
			//Start creating the input xml for createorder and incorporate orderlines for each item selected.
			//---------------------------------------------------------
			
			//--------creteorder xml
			CreateOrderInputDoc = XMLUtil.newDocument();

			Element el_Order=CreateOrderInputDoc.createElement("Order");
			CreateOrderInputDoc.appendChild(el_Order);
			el_Order.setAttribute("AllocationRuleID","");
			el_Order.setAttribute("EnterpriseCode","NWCG");
			el_Order.setAttribute("BuyerOrganizationCode","");
			el_Order.setAttribute("DocumentType",NWCGConstants.NWCG_RESERVATION_NORMAL_ISSUE_DOC);
			el_Order.setAttribute("DraftOrderFlag","Y");
			el_Order.setAttribute("DriverDate","");
			
			el_Order.setAttribute("ExchangeType","");
			el_Order.setAttribute("HasDeliveryLines","N");
			el_Order.setAttribute("HasProductLines","N");
			el_Order.setAttribute("HasServiceLines","N");
			el_Order.setAttribute("HoldFlag","N");
			el_Order.setAttribute("OrderDate","");
			el_Order.setAttribute("OrderHeaderKey","");
			el_Order.setAttribute("OrderNo","");
			el_Order.setAttribute("OrderPurpose","");
			el_Order.setAttribute("OrderType","");
			el_Order.setAttribute("PaymentStatus","");
			el_Order.setAttribute("PendingTransferIn","0.00");
			el_Order.setAttribute("ReturnOrderHeaderKeyForExchange","");
			el_Order.setAttribute("SaleVoided","N");
			el_Order.setAttribute("ScacAndServiceKey","");
			el_Order.setAttribute("SellerOrganizationCode","");
			el_Order.setAttribute("isHistory","N");

			Element el_PriceInfo=CreateOrderInputDoc.createElement("PriceInfo");
			el_Order.appendChild(el_PriceInfo);
			el_PriceInfo.setAttribute("Currency","");

			Element el_OrderLines=CreateOrderInputDoc.createElement("OrderLines");
			el_Order.appendChild(el_OrderLines);

			//------------break
			//----------------------End
			//----------------
			System.out.println("\n\n before calling getnodelist ->\n"+ XMLUtil.getXMLString(inXML));
			//GS - if(inXML!=null)
			NodeList ItemList = inXML.getElementsByTagName("Item");
			//GS - if(ItemList!=null && ItemList.getLength()>0)
			int ItemListLength = ItemList.getLength();
			System.out.println("ItemListLength ->"+ItemListLength);
			
			//----------------- 
			for(int cnt=0; cnt<ItemListLength;cnt++){
				String itemKey = "";
				//getting items to filter out
				//System.out.println("Attribute-"+attributeName+"Has following value:"+Order.getAttribute(attributeName));
				//GS - if(ItemList!=null && ItemList.getLength()>0)
				 SelectedItem = (Element)ItemList.item(cnt);
				 itemKey = SelectedItem.getAttribute("ItemKey");
			   
				//Added after code review 
				if(!(SelectedItem.getAttribute("selectedQTY").equals(""))&& SelectedItem.getAttribute("selectedQTY")!=null){ 
					selectedQty = Integer.parseInt(SelectedItem.getAttribute("selectedQTY"));
				}else{
					System.out.println("selectedQTY from UI is not correct.. so continue");
					continue;
				}//End if -- code review
				
				key = (List)FinalMap.get(itemKey);
				//Added after code review
				if(key == null){ 
					System.out.println("no record details in map");
					continue;
				}else{
					System.out.println("record  details found in map");
				}//End if -- code review

				//------get the item details
				itemid = (String)key.get(0);       
				System.out.println("i==0, itemid"+itemid);
				reservedQty = (String)key.get(3); 
				System.out.println("i=3,reservedQty"+reservedQty);
				OrgCode = (String)key.get(2);
				//System.out.println("i=2,OrgCode"+OrgCode);
				UOM = (String)key.get(4);
				//System.out.println("i=4,UOM"+UOM);
				ProductClass = (String)key.get(5);
				//System.out.println("i=5,ProductClass"+ProductClass);
				shipNode = (String)key.get(6);
				System.out.println("i=6,shipNode"+shipNode);
				//System.out.println("selectedQty :-"+selectedQty);
				//Note :- Ignore i==2 as it is itemKey 
				//=========
				 
				System.out.println("i=4,UOM"+UOM+"itemid"+itemid+"reservedQty"+reservedQty+"OrgCode"+OrgCode+"shipNode:"+shipNode);
					
				Element el_OrderLine=CreateOrderInputDoc.createElement("OrderLine");
				el_OrderLines.appendChild(el_OrderLine);
				el_OrderLine.setAttribute("OrderedQty",new Integer(selectedQty).toString());
				el_OrderLine.setAttribute("PrimeLineNo",Integer.toString(cnt+1));
				el_OrderLine.setAttribute("ReceivingNode","");
				//Commented by GN - 10/30/07
				//el_OrderLine.setAttribute("ReservationID",reservationID);
				//el_OrderLine.setAttribute("ReservationMandatory","Y");
				//el_OrderLine.setAttribute("ReservationID","");
				//el_OrderLine.setAttribute("ReservationMandatory","");
				el_OrderLine.setAttribute("ShipNode",shipNode);
				el_OrderLine.setAttribute("SubLineNo",Integer.toString(cnt+1));

				Element el_Item2=CreateOrderInputDoc.createElement("Item");
				el_OrderLine.appendChild(el_Item2);
				el_Item2.setAttribute("ItemID",itemid);
				el_Item2.setAttribute("ItemShortDesc","");
				el_Item2.setAttribute("ProductClass",ProductClass);
				el_Item2.setAttribute("UnitOfMeasure",UOM);
				//==========
				System.out.println("End of for loop");
			}//End for loop
			
		    //=============rest of xml
			//Element el_PersonInfoShipTo=CreateOrderInputDoc.createElement("PersonInfoShipTo");
			//el_Order.appendChild(el_PersonInfoShipTo);
			//el_PersonInfoShipTo.setAttribute("AddressLine1"," ");

			//Element el_PaymentMethods=CreateOrderInputDoc.createElement("PaymentMethods");
			//el_Order.appendChild(el_PaymentMethods);

			//Element el_PersonInfoBillTo=CreateOrderInputDoc.createElement("PersonInfoBillTo");
			//el_Order.appendChild(el_PersonInfoBillTo);
			//el_PersonInfoBillTo.setAttribute("AddressLine1"," ");
		   //============End xml
		   
		   //-------------------End preparing input xml for create order 
		}catch(Exception E){
			 //System.out.println("Generated exception "+E.toString());
			 throw new NWCGException("NWCG_RESERVATION_ERR_GENERATING_CREATE_ORDER_XML");
		}//End Try-Catch
		
		//Element rootNode = inXML.getDocumentElement();
		System.out.println("CreateOrder input document ->"+XMLUtil.getXMLString(CreateOrderInputDoc));
		
		//-----------------------------------------------------------   
		//-------------------Creating CreateOrder Document to return 
		//-----------------------------------------------------------
		System.out.println("RES CREATE XML" +XMLUtil.getXMLString(CreateOrderInputDoc));
		try{
			CreateOrderOut_xml = XMLUtil.newDocument();
			// -------- jsk commented below ---------
			//CreateOrderOut_xml = CommonUtilities.invokeAPI(env,"createOrder",CreateOrderInputDoc);
			// -------- jsk commented above ---------
			//Element el_Order=rt_Order.createElement("Order");
			//rt_Order.appendChild(el_Order);
			//el_Order.setAttribute("OrderHeaderKey","2006060610121118112");
			//el_Order.setAttribute("ReadFromHistory","B");
			//System.out.println("CreateOrder Output document is :-"+XMLUtil.getXMLString(CreateOrderOut_xml));
		}catch(Exception E){
			throw new NWCGException("NWCG_RESERVATION_ERR_GENERATING_CREATE_ORDER_XML");
		}//End Try
		
		//-------------------------------------------------------------------
		if(logger.isVerboseEnabled()){
			logger.debug("JDBG: End of NWCGCreateIssuUsingReservedItems:createOrder API. Output XML ->"+XMLUtil.getXMLString(CreateOrderOut_xml));
		}
		
		return CreateOrderOut_xml;

	}//Create Order

	public void setProperties(Properties prop) throws Exception
	{
		_properties = prop;
	}

 }//End Class
