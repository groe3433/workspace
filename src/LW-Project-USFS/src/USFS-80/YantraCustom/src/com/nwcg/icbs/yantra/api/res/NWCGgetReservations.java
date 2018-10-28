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
//Gomathi please make appropriate changes whereever comment "//GS" is present
// also make all the methods except the entry method as private

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSConnectionHolder;
import com.yantra.yfs.japi.YFSEnvironment;


/**
 * This API get the reservation ID from the search page. The input xml to this page is :-
 *  <Item ItemID="res1" ItemIDQryType="" MaximumRecords="30" reservationId="res1"/>
 * Then hit the table YFS_reservation to get the list of items assigned the same reservaiton id.
 * Return the list xml which will be used to render the custom list page.
 * 
 * Important files for the module :-
 * @author spillai
 */
public class NWCGgetReservations implements YIFCustomApi {
    	
	    private Properties _properties;
	    private static Logger logger = Logger.getLogger(NWCGgetReservations.class.getName());
        String itemName = null;
        String orgCode = null;
        String reservedQty = null;
        String shipNode = null;
        String shipDate = null;
        String reservationid = null;
        String orderKey = "";
        String ItemKey[] = new String[2];
         
	    /**
	     * This method provided the list of items
	     * This API is used to create Order and send back the orderHeaderKey to the JSP(Redirect page)
	     */
	    public Document getItemList(YFSEnvironment env, Document inXML) throws Exception{
	    	if(logger.isVerboseEnabled()){
	    		logger.verbose("NWCGgetReservedItems API input Doc:"+XMLUtil.getXMLString(inXML));
			}
	    	
	    	logger.debug("Start of NWCGgetItemListAPI");
	        if (null == env)
	        {
	        	logger.error("YFSEnvironment is null");
	            throw new NWCGException("NWCG_RESERVATION_ENV_IS_NULL");
	        }
	        if (null == inXML)
	        {
	        	logger.error("Input Document is null");
	            throw new NWCGException("NWCG_RESERVATION_INPUT_DOC_NULL");
	        }
	        //Document outXML = null;
	        //System.out.println("Writing Input XML to NWCGgetReservationsService");
	        //writedoc(inXML,"");
	        
	        Element rootNode = inXML.getDocumentElement();
	        String InputResId = rootNode.getAttribute("ReservationID");
	        String InputQryType = rootNode.getAttribute("ReservationIDQryType");
	        String ItemId = rootNode.getAttribute("ItemID");
	        String NodeKey = rootNode.getAttribute("Node");
	        
	        //System.out.println("Input Reservation ID : "+ InputResId);
	        //System.out.println("Input Qry Type : " + InputQryType);
	        
	        Document getItemListOutput_Doc = null;
	        
	        //GS - check if(inXML!=null)
	        //Element rootNode = inXML.getDocumentElement();

	        // Retrieve the Short Description if provided
	        try{
	  	        YFSConnectionHolder connHolder = (YFSConnectionHolder)env;
		        Connection c = null;
		        java.sql.Statement stmt = null;
		        ResultSet rs = null;
		        
               //************OUTPUT XML template ===================

		        getItemListOutput_Doc = XMLUtil.newDocument();
		        Element el_ItemList=getItemListOutput_Doc.createElement("ReservationItemList");
		        getItemListOutput_Doc.appendChild(el_ItemList);
		        el_ItemList.setAttribute("TotalNumberOfRecords","");
               
		        
		        //GS - add an if condition to perform the query only if(reservationId!=null && !(reservationId.equals("")))
		        // if not added it will throw error
		        try {
		             c = connHolder.getDBConnection();
		             stmt = c.createStatement();
		             
		             if (NodeKey.equals("")) 
		             {	 
		             if ((InputQryType.equals("EQ")) && (ItemId.equals("")))
		             {
	                 rs = stmt.executeQuery("select a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY'),reservation_id " +
	                 		"from yfs_inventory_reservation a where a.reservation_id = '"+InputResId+"'");
		             }
		             if ((InputQryType.equals("EQ")) && (!ItemId.equals("")))
		             {
		             rs = stmt.executeQuery("select a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY'),a.reservation_id from " +
		     			            		"yfs_inventory_reservation a, yfs_inventory_item b where a.inventory_item_key = b.inventory_item_key " +
		     			            		" and b.item_id = '"+ItemId+"' and a.reservation_id = '"+InputResId+"'");	 
	                 }
		             
		             if ((InputQryType.equals("LIKE")) && (ItemId.equals("")))
		             {
	                 rs = stmt.executeQuery("select a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY'),reservation_id " +
	                 		"from yfs_inventory_reservation a where a.reservation_id like '%"+InputResId+"%'");
		             }
		             if ((InputQryType.equals("LIKE")) && (!ItemId.equals("")))
		             {
		             rs = stmt.executeQuery("select a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY'),a.reservation_id from " +
		     			            		"yfs_inventory_reservation a, yfs_inventory_item b where a.inventory_item_key = b.inventory_item_key " +
		     			            		" and b.item_id = '"+ItemId+"' and a.reservation_id like '%"+InputResId+"%'");	 
	                 }
		             
		             if ((InputQryType.equals("FLIKE")) && (ItemId.equals("")))
		             {
	                 rs = stmt.executeQuery("select a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY'),reservation_id " +
	                 		"from yfs_inventory_reservation a where a.reservation_id like '"+InputResId+"%'");
		             }
		             if ((InputQryType.equals("FLIKE")) && (!ItemId.equals("")))
		             {
		             rs = stmt.executeQuery("select a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY'),a.reservation_id from " +
		     			            		"yfs_inventory_reservation a, yfs_inventory_item b where a.inventory_item_key = b.inventory_item_key " +
		     			            		" and b.item_id = '"+ItemId+"' and a.reservation_id like '"+InputResId+"%'");	 
	                 }
		             
		             if ((InputQryType.equals("")) && (ItemId.equals("")))
		             {
	                 rs = stmt.executeQuery("select a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY'),a.reservation_id " +
	                 		"from yfs_inventory_reservation a ");
		             }
		             
		             if ((InputQryType.equals("")) && (!ItemId.equals("")))
		             {
		             rs = stmt.executeQuery("select a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY'),a.reservation_id from " +
		     			            		"yfs_inventory_reservation a, yfs_inventory_item b where a.inventory_item_key = b.inventory_item_key " +
		     			            		" and b.item_id = '"+ItemId+"'");	 
	                 }
		             } //End Nodekey NULL Checking
		             
		             
		             if (!NodeKey.equals("")) 
		             {	 
		             if ((InputQryType.equals("EQ")) && (ItemId.equals("")))
		             {
	                 rs = stmt.executeQuery("select a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY'),reservation_id " +
	                 		"from yfs_inventory_reservation a where a.reservation_id = '"+InputResId+"' and a.shipnode_key ='"+NodeKey+"'");
		             }
		             if ((InputQryType.equals("EQ")) && (!ItemId.equals("")))
		             {
		             rs = stmt.executeQuery("select a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY'),a.reservation_id from " +
		     			            		"yfs_inventory_reservation a, yfs_inventory_item b where a.inventory_item_key = b.inventory_item_key " +
		     			            		" and b.item_id = '"+ItemId+"' and a.reservation_id = '"+InputResId+"' and a.shipnode_key ='"+NodeKey+"'");	 
	                 }
		             
		             if ((InputQryType.equals("LIKE")) && (ItemId.equals("")))
		             {
	                 rs = stmt.executeQuery("select a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY'),reservation_id " +
	                 		"from yfs_inventory_reservation a where a.reservation_id like '%"+InputResId+"%' and a.shipnode_key ='"+NodeKey+"'");
		             }
		             if ((InputQryType.equals("LIKE")) && (!ItemId.equals("")))
		             {
		             rs = stmt.executeQuery("select a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY'),a.reservation_id from " +
		     			            		"yfs_inventory_reservation a, yfs_inventory_item b where a.inventory_item_key = b.inventory_item_key " +
		     			            		" and b.item_id = '"+ItemId+"' and a.reservation_id like '%"+InputResId+"%' and a.shipnode_key ='"+NodeKey+"'");	 
	                 }
		             
		             if ((InputQryType.equals("FLIKE")) && (ItemId.equals("")))
		             {
	                 rs = stmt.executeQuery("select a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY'),reservation_id " +
	                 		"from yfs_inventory_reservation a where a.reservation_id like '"+InputResId+"%' and a.shipnode_key ='"+NodeKey+"'");
		             }
		             if ((InputQryType.equals("FLIKE")) && (!ItemId.equals("")))
		             {
		             rs = stmt.executeQuery("select a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY'),a.reservation_id from " +
		     			            		"yfs_inventory_reservation a, yfs_inventory_item b where a.inventory_item_key = b.inventory_item_key " +
		     			            		" and b.item_id = '"+ItemId+"' and a.reservation_id like '"+InputResId+"%' and a.shipnode_key ='"+NodeKey+"'");	 
	                 }
		             
		             if ((InputQryType.equals("")) && (ItemId.equals("")))
		             {
	                 rs = stmt.executeQuery("select a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY'),a.reservation_id " +
	                 		"from yfs_inventory_reservation a where a.shipnode_key ='"+NodeKey+"'");
		             }
		             
		             if ((InputQryType.equals("")) && (!ItemId.equals("")))
		             {
		             rs = stmt.executeQuery("select a.inventory_item_key, a.quantity, a.owner_key,a.shipnode_key,to_char(a.ship_date,'MM/DD/YYYY'),a.reservation_id from " +
		     			            		"yfs_inventory_reservation a, yfs_inventory_item b where a.inventory_item_key = b.inventory_item_key " +
		     			            		" and b.item_id = '"+ItemId+"' and a.shipnode_key ='"+NodeKey+"'");	 
	                 }
		             } //End Nodekey IS NOT NULL Checking

		            if(rs != null){
			            while (rs.next()) {
			              			               
			                itemName = rs.getString(1);
			                reservedQty = rs.getString(2);
			                orgCode = rs.getString(3);
			                shipNode = rs.getString(4);
			                shipDate = rs.getString(5);
			                reservationid = rs.getString(6);
			                
			                String itemNameAndKey[] = getItemName(env,itemName,orgCode);

			                Element el_Item=getItemListOutput_Doc.createElement("Item");
					        el_ItemList.appendChild(el_Item);
					        el_Item.setAttribute("ItemID",itemNameAndKey[0]);
					        el_Item.setAttribute("ProductClass",itemNameAndKey[1]);
					        el_Item.setAttribute("ItemKey",itemNameAndKey[3]);
					        el_Item.setAttribute("OrganizationCode",orgCode);
					        el_Item.setAttribute("UnitOfMeasure",itemNameAndKey[2]);
					        el_Item.setAttribute("QTY",reservedQty);
					        el_Item.setAttribute("orderKey",orderKey);
					        el_Item.setAttribute("Description",itemNameAndKey[4]);
					        el_Item.setAttribute("shipNode",shipNode);
					        el_Item.setAttribute("shipDate",shipDate);
					        el_Item.setAttribute("ReservationID",reservationid);
		                
			            }//End While
		            }//rs != null
		        }catch(Exception E){
		        	throw new NWCGException("NWCG_RESERVATION_DB_ERROR");
		        } finally {
		            if ( rs != null )
		                rs.close();
		            if ( stmt != null )
	                stmt.close();
		        }// End Try 
		           
	       }catch(Exception E){
	        	throw new NWCGException("NWCG_RESERVATION_ERR_READING_RESULTS");
	        }//End Try 
	        
	        //logger.debug("End of NWCGgetItemListAPI");
	        
	        if(logger.isVerboseEnabled()){
	    		logger.verbose("Exiting the NWCGgetItemListAPI , return document is:"+XMLUtil.getXMLString(getItemListOutput_Doc));
			}
	        return getItemListOutput_Doc;
	    }//End  getItemList method
	    //-------------------------------------------------------
	    
	    //Method to be used when attributes are passed through properties
	    public void setProperties(Properties prop) throws Exception
	    {
	        _properties = prop;
	    }//End setProperties method
	    //--------------------------------------------------------
	    
	    //This method gets the name of the item based on the itemKey by calling "getInventoryItemList" API
	    public String[] getItemName(YFSEnvironment env,String invIemKey, String OrgCode)throws Exception
	    {
	    	//--Added after code review 
	    	if(logger.isVerboseEnabled()){
	    		logger.verbose("Entering getItemName method");
			}
	    	//----------Code review
	    	
	    	String invItmKey = invIemKey;
	    	String name_Key[] = new String[5];
	    	Document outDoc = null;
	    	
	    	Document getInventoryItemList_Input = XMLUtil.newDocument();
	        Element el_InventoryItem=getInventoryItemList_Input.createElement("InventoryItem");
	        getInventoryItemList_Input.appendChild(el_InventoryItem);
	        el_InventoryItem.setAttribute("InventoryItemKey",invItmKey);
	        el_InventoryItem.setAttribute("OrganizationCode",OrgCode);
	    	
	        ////System.out.println("\n\n Input xml:-"+ XMLUtil.getXMLString(getInventoryItemList_Input));
	       
	        //Form the output xml
	        Document getInventoryItemList_Output = XMLUtil.newDocument();
	        Element el_InventoryList=getInventoryItemList_Output.createElement("InventoryList");
	        getInventoryItemList_Output.appendChild(el_InventoryList);
	        Element el_InventoryItem2=getInventoryItemList_Output.createElement("InventoryItem");
	        el_InventoryList.appendChild(el_InventoryItem2);
	        el_InventoryItem2.setAttribute("ItemID","");
	        el_InventoryItem2.setAttribute("ProductClass","");
	        el_InventoryItem2.setAttribute("UnitOfMeasure","");
	        Element el_Item=getInventoryItemList_Output.createElement("Item");
	        el_InventoryItem2.appendChild(el_Item);
	        el_Item.setAttribute("ItemID","");
	        el_Item.setAttribute("ItemKey","");
	        Element el_PrimaryInformation=getInventoryItemList_Output.createElement("PrimaryInformation");
	        el_Item.appendChild(el_PrimaryInformation);
	        el_PrimaryInformation.setAttribute("Description","");
	        el_PrimaryInformation.setAttribute("ShortDescription","");
	        	        
	        ////System.out.println("\n\n output xml:-"+ XMLUtil.getXMLString(getInventoryItemList_Output));
	        
	        //Set output template and call the API
	        env.setApiTemplate("getInventoryItemList", getInventoryItemList_Output);
	        outDoc = XMLUtil.newDocument();
	        outDoc = CommonUtilities.invokeAPI(env,"getInventoryItemList",getInventoryItemList_Input);
	        //Added after code review
	        env.clearApiTemplate("getInventoryItemList");
	        ////System.out.println("\n\n getInventoryItemList called");
	        //return the ItemName
	        if(outDoc!=null){
		        Element rootNode = outDoc.getDocumentElement();
		        Element Item = (Element) XPathUtil.getNode(rootNode, "/InventoryList/InventoryItem");
		        //GS - add if(Item!=null) before you get the attribute from the element in below lines
		        name_Key[0] = Item.getAttribute("ItemID");
		        name_Key[1] = Item.getAttribute("ProductClass");
		        name_Key[2] = Item.getAttribute("UnitOfMeasure");
		        Element ItemKey = (Element) XPathUtil.getNode(rootNode, "/InventoryList/InventoryItem/Item");
		        //GS - add if(ItemKey!=null)
			    name_Key[3] = ItemKey.getAttribute("ItemKey");
			    Element primeInfoKey = (Element) XPathUtil.getNode(rootNode, "/InventoryList/InventoryItem/Item/PrimaryInformation");
			    //GS - add if(primeInfoKey!=null)
			    name_Key[4] = primeInfoKey.getAttribute("Description");
	        }//End if

	    	if(logger.isVerboseEnabled()){
	    		logger.verbose("Exiting getItemName method");
			}
	    	//----------Code review
	        
	    	return name_Key;
	    }//End getItemName()
	    
	    public static void writedoc(Node node,String indent ) {
			
		      
			 switch(node.getNodeType()) {

			 case Node.DOCUMENT_NODE: {      
			 Document doc = (Document)node;
			 System.out.println(indent + "<?xml version='1.0'?>");  
			 Node child = doc.getFirstChild();   
			 while(child != null) {              
			  writedoc(child,indent);        
			  child = child.getNextSibling(); 
			 }
			 break;
			 } 

			 case Node.ELEMENT_NODE: {        
			 Element elt = (Element) node;
			 System.out.println(indent + "<" + elt.getTagName());   
			 NamedNodeMap attrs = elt.getAttributes();     
			 for(int i = 0; i < attrs.getLength(); i++) {  
			  Node a = attrs.item(i);
			  System.out.println(" " + a.getNodeName() + "='" +  
			  a.getNodeValue() + "'"); 
			  }
			  System.out.println(">");                             
			  String newindent = indent + "    ";           
			  Node child = elt.getFirstChild();             
			  while(child != null) {                        
			   writedoc(child,newindent);               
			   child = child.getNextSibling();           
			  }
			  System.out.println(indent + "</" +                   
			   elt.getTagName() + ">");
			   break;
			  }

			  case Node.TEXT_NODE: {                   
			  Text textNode = (Text)node;
			  String text = textNode.getData().trim();   
			  if ((text != null) && text.length() > 0)   
			    System.out.println(indent + text);     
			    break;
			   }

			  default:   
			  System.err.println("Ignoring node: " + node.getClass().getName());
			  break;
			  }
			}
	}//End Class
    
    


