/*Author :- Suresh Pillai
 * About :- This API gets called using ajax. This api gets called to get the PC and UOM of the components on tab out from itemID textbox
 * API also get called on tab out from the retQTY textbox.
 * */

//Gomathy please change the code wherever "//GS" comment is present.. a
//Also change the methods to private wherever it is public, unless u plan to use them outside the API

package com.nwcg.icbs.yantra.api.returns;


import java.util.HashMap;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class NWCGgetItemAndComponentDetails implements YIFCustomApi {
    	
	    private Properties _properties;
	    private static YFCLogCategory log = YFCLogCategory.instance(NWCGgetItemAndComponentDetails.class);
	    
	    String IsSerialTracked = "";
	    String TagControlFlag  = "";
	    String ShortDescription = "";
	    String TimeSensitive    = "";
	    private static Logger logger = Logger.getLogger(NWCGgetItemAndComponentDetails.class.getName());
	 
	    public Document getDetails(YFSEnvironment env, Document inXML) throws YFSException
	    {
	    	//Document returnDoc =null;
	    	Document getItemList_Out = null;
	    	Document getItemDetails_Out = null;
	    	int ReturnQuantity=0;
	    	
	    	String sProductLine = "";
	    	
	    	//System.out.println("Entering the NWCGgetItemAndComponentDetails ,input document is:"+XMLUtil.getXMLString(inXML));

	    	if(logger.isVerboseEnabled()){
	    		logger.verbose("Entering the NWCGgetItemAndComponentDetails ,input document is:"+XMLUtil.getXMLString(inXML));
			}//End log

	        try{
		        //System.out.println("\n\n Inside API *******testing :-\n"+ XMLUtil.getXMLString(inXML));
		        
		        
		        //First call getitemList using the itemID and calling organizationCode and get the item details like PC
		        //PC and UOM 
		        //Then call getitemdetails to get the component and other required information.
		        
		        String ItemID 					= inXML.getDocumentElement().getAttribute("ItemID");
		        String RetQty 					= inXML.getDocumentElement().getAttribute("RetQty");
		        
		        //if condition added to prevent number format exception
		        //if(RetQty!=null && RetQty!="")
		        //Changed after code review 
		        if(RetQty!=null && !(RetQty.equals("")))
		        	ReturnQuantity 	= Integer.parseInt(inXML.getDocumentElement().getAttribute("RetQty"));
		        //System.out.println("testing -----4:- " +RetQty);
		          	
		        
		        //System.out.println("suresh testing ItemID:-"+ItemID);
				//String CallingOrganizationCode	= inXML.getDocumentElement().getAttribute("CallingOrganizationCode");
				
		        //if(ItemID==null||ItemID==""){
		        //Changed after code review 
		        if(ItemID==null||ItemID.equals("")){
		        	return inXML;
		        }//End IF
		        
				getItemList_Out = callGetItemList(env,inXML);
				//System.out.println("The Output from callGetItemList:-"+XMLUtil.getXMLString(getItemList_Out));
				
				Element primaryInformationElem = (Element) getItemList_Out.getElementsByTagName("PrimaryInformation").item(0);
				 sProductLine = primaryInformationElem.getAttribute("ProductLine");
				
				
				//System.out.println("sProductLine ::" + sProductLine);
				
				if(getItemList_Out!=null)
					getItemDetails_Out = callGetItemDetails(env,getItemList_Out,ReturnQuantity);
				
				//System.out.println("The output xml from callGetItemDetails :-"+XMLUtil.getXMLString(getItemDetails_Out));
	        }catch(Exception E){
	        	
	        	//System.out.println("Generated exception "+E.toString());
	        	throw new NWCGException("NWCG_RETURN_INPUT_VALUES_ERROR"); 
	        }//End Try catch
	        
	        if(logger.isVerboseEnabled()){
	    		logger.verbose("Exiting the NWCGgetItemAndComponentDetails ,output document is:"+XMLUtil.getXMLString(getItemDetails_Out));
			}//End log
	        
	        getItemDetails_Out.getDocumentElement().setAttribute("sProductLine", sProductLine);
	        
	       // System.out.println("Exiting the NWCGgetItemAndComponentDetails ,output document is:"+XMLUtil.getXMLString(getItemDetails_Out));
	        return getItemDetails_Out;
	    }//End Function getDetails
	
	    
	    //------------------callGetItemList
	    //This method is used to get the UOM and PC for the input itemID
	    public Document callGetItemList(YFSEnvironment env,Document inXML)throws Exception{
	    	Document ItemListDoc_out = null;
	    	Document IteList_Template = null;
	    	
	    	//---------------------Template
	    	IteList_Template = XMLUtil.newDocument();

	    	Element el_ItemList=IteList_Template.createElement("ItemList");
	    	IteList_Template.appendChild(el_ItemList);


	    	Element el_Item=IteList_Template.createElement("Item");
	    	el_ItemList.appendChild(el_Item);
	    	el_Item.setAttribute("ItemID","");
	    	el_Item.setAttribute("ItemKey","");
	    	el_Item.setAttribute("OrganizationCode","");
	    	el_Item.setAttribute("UnitOfMeasure","");


	    	Element el_PrimaryInformation=IteList_Template.createElement("PrimaryInformation");
	    	el_Item.appendChild(el_PrimaryInformation);
	    	el_PrimaryInformation.setAttribute("DefaultProductClass","");
	    	el_PrimaryInformation.setAttribute("NumSecondarySerials","");
	    	el_PrimaryInformation.setAttribute("ShortDescription","");
	    	el_PrimaryInformation.setAttribute("ProductLine","");


	    	Element el_InventoryParameters=IteList_Template.createElement("InventoryParameters");

	    	el_Item.appendChild(el_InventoryParameters);
	    	el_InventoryParameters.setAttribute("IsSerialTracked","");
	    	el_InventoryParameters.setAttribute("TagControlFlag","");
	    	el_InventoryParameters.setAttribute("TimeSensitive","");
	    	//----------------------Template End
	    	
	    	env.setApiTemplate("getItemList",IteList_Template);
	    	//System.out.println("The output template for getitemList API is:-"+XMLUtil.getXMLString(IteList_Template));
	    	//System.out.println("The input xml for getItemList is:-"+XMLUtil.getXMLString(inXML));
	    	ItemListDoc_out = CommonUtilities.invokeAPI(env,"getItemList",inXML);
	    	env.clearApiTemplate("getItemList");
		    //System.out.println("The output xml from getItemList is:-"+XMLUtil.getXMLString(ItemListDoc_out));
		    
		    return ItemListDoc_out;
	    }//End callGetItemList()
	    
	    //This method is used to get the tag attributes and serial
	    public Document callGetItemDetails(YFSEnvironment env,Document inXML, int RetQty)throws Exception{
	    	//EXPLANATION
	    	//This method is called after calling getItemList for the kit component to obtain UOM and PC
	    	//Then use the output xml to call getItemDetails to get the list of componenets
	    	//For each component call getItemList to get all the attributes except tag attributes
	    	//For each component call getCompAttributes method to get the tag attributes
	    	//The return the returnDocument.
	    	
	    	//System.out.println("The input xml for callgetItemDetails is:-"+XMLUtil.getXMLString(inXML));
	    	Document returnDocument = XMLUtil.newDocument();
	    		
	    	NodeList Item = inXML.getDocumentElement().getElementsByTagName("Item");
	    	NodeList PrimaryInformation = inXML.getDocumentElement().getElementsByTagName("PrimaryInformation");
	    	NodeList InventoryParameters = inXML.getDocumentElement().getElementsByTagName("InventoryParameters");
	    	
	    	
	    	//GS - check to see if Item1 is null and length>0.. put next 4 lines in the if condition
	    	Element Item1 = (Element)Item.item(0);
	    	String ItemID = Item1.getAttribute("ItemID");
	    	//System.out.println("ItemID:-"+ItemID);
	    	String OrganizationCode = Item1.getAttribute("OrganizationCode");
	    	//System.out.println("OrgCode:-"+OrganizationCode);
	    	String UnitOfMeasure  = Item1.getAttribute("UnitOfMeasure");
	    		    	
	    	//GS - check to see if PrimaryInformation1 is null and length>0..
	    	Element PrimaryInformation1 = (Element)PrimaryInformation.item(0);
	    	String ShortDescription = PrimaryInformation1.getAttribute("ShortDescription");
	    	//System.out.println("ShortDesc:-"+ShortDescription);
	    	String DefaultProductClass = PrimaryInformation1.getAttribute("DefaultProductClass");
	    	String NumSecondarySerials = PrimaryInformation1.getAttribute("NumSecondarySerials");
	    	//System.out.println("SecondarySerials:-"+NumSecondarySerials);
	    	
	    	//GS - check to see if InventoryParameters1 is null and length>0..
	    	Element InventoryParameters1 = (Element)InventoryParameters.item(0);
	    	String IsSerialTracked = InventoryParameters1.getAttribute("IsSerialTracked");
	    	//System.out.println("IsSerialTracked:-"+IsSerialTracked);
	    	String TagControlFlag  = InventoryParameters1.getAttribute("TagControlFlag");
	    	String TimeSensitive   = InventoryParameters1.getAttribute("TimeSensitive");
	    	
	    	
	    	//----------Preparing the input xml for the getItemDetails API
	    	Document getItemDetails_IN = XMLUtil.newDocument();

	    	Element el_Item=getItemDetails_IN.createElement("Item");
	    	getItemDetails_IN.appendChild(el_Item);
	    	el_Item.setAttribute("ItemID",ItemID);
	    	el_Item.setAttribute("ItemKey","");
	    	el_Item.setAttribute("OrganizationCode",OrganizationCode);
	    	el_Item.setAttribute("UnitOfMeasure",UnitOfMeasure);
	    	//el_Item.setAttribute("NumSecondarySerials",NumSecondarySerials);
	    	
	    	//System.out.println("The input xml for getItemDetails is:-"+XMLUtil.getXMLString(getItemDetails_IN));
	    	Document ItemDetails_out_temp = null;
	    	Document ItemDetails_out = null;
	    	
	    	//-------------------Prepare output template- to get the list of component---
	    	ItemDetails_out_temp = XMLUtil.newDocument();

	    	Element el_Item2=ItemDetails_out_temp.createElement("Item");
	    	ItemDetails_out_temp.appendChild(el_Item2);


	    	Element el_Components2=ItemDetails_out_temp.createElement("Components");
	    	el_Item2.appendChild(el_Components2);


	    	Element el_Component2=ItemDetails_out_temp.createElement("Component");
	    	el_Components2.appendChild(el_Component2);
	    	el_Component2.setAttribute("ComponentDescription","");
	    	el_Component2.setAttribute("ComponentItemID","");
	    	el_Component2.setAttribute("ComponentItemKey","");
	    	el_Component2.setAttribute("ComponentOrganizationCode","");
	    	el_Component2.setAttribute("ComponentUnitOfMeasure","");
	    	el_Component2.setAttribute("ItemKey","");
	    	el_Component2.setAttribute("KitQuantity","");
	    	
	    	Element el_Component3=ItemDetails_out_temp.createElement("InventoryTagAttributes");
	    	el_Item2.appendChild(el_Component3);
	    	//--------------------End Output template to get the list of component
	    	
	    	
	    	//System.out.println("The output template for getItemDetails is:-"+XMLUtil.getXMLString(ItemDetails_out_temp));
	    	env.setApiTemplate("getItemDetails",ItemDetails_out_temp);
	    	ItemDetails_out = CommonUtilities.invokeAPI(env,"getItemDetails",getItemDetails_IN);
	    	env.clearApiTemplate("getItemDetails");
		    //System.out.println("The output xml from getItemDetails is:-"+XMLUtil.getXMLString(ItemDetails_out));
	    	
		    //-----------Added after final demo
		    String  BatchNo = "";
		    String  LotAttribute1 = "";
		    String  LotAttribute2 = "";
		    String  LotAttribute3 = ""; 
		    String  LotNumber = "";
		    String  RevisionNo = "";
		    
		    //System.out.println("testing...1");
		     
		    if(ItemDetails_out.getDocumentElement().getElementsByTagName("InventoryTagAttributes")!=null && 
		    		!(ItemDetails_out.getDocumentElement().getElementsByTagName("InventoryTagAttributes").equals(""))){
			     
			     //System.out.println("testing...1.1");
			     
			     NodeList InvTagList2  = ItemDetails_out.getDocumentElement().getElementsByTagName("InventoryTagAttributes");
			     
			     //System.out.println("testing...1.2");
			     
			     //GS - check to see if InvTagEl2 is null and length>0..
		    	 Element InvTagEl2 =  (Element)InvTagList2.item(0);
		    	 //System.out.println("testing...1.3");
		    	 
		    	 //Added during testing on main config
		    	 if(InvTagList2.getLength()!=0){
			    	 BatchNo =  InvTagEl2.getAttribute("BatchNo");
			    	 //System.out.println("testing...1.4");
			    	 LotAttribute1 =  InvTagEl2.getAttribute("LotAttribute1");
			    	 LotAttribute2 =  InvTagEl2.getAttribute("LotAttribute2");
			    	 LotAttribute3 =  InvTagEl2.getAttribute("LotAttribute3");
			    	 LotNumber =  InvTagEl2.getAttribute("LotNumber");
			    	 RevisionNo =  InvTagEl2.getAttribute("RevisionNo");
			     }//End if 
		     }//End if tag node exists	 
	    	//----------End adding after final demo
		    
		    //System.out.println("testing...2");
		    String TotalNumberOfComp = "0";
		    NodeList ComponentList  = null;
		    
		    if(ItemDetails_out.getDocumentElement().getElementsByTagName("Component")!=null){
		    	ComponentList  = ItemDetails_out.getDocumentElement().getElementsByTagName("Component");
		    	TotalNumberOfComp = Integer.toString(ComponentList.getLength());
		    }//End if components exist	
		    //System.out.println("testing...3");
		    
		    //START CREATING FINAL OUTPUT DOCUMENT***********************
		    Element el_Item1=returnDocument.createElement("Item");
		    returnDocument.appendChild(el_Item1);
	        el_Item1.setAttribute("ItemID",ItemID);
	        el_Item1.setAttribute("OrganizationCode",OrganizationCode);
	        el_Item1.setAttribute("SerializedFlag",IsSerialTracked);
	        el_Item1.setAttribute("TagControlFlag",TagControlFlag);
	        el_Item1.setAttribute("TimeSensitive",TimeSensitive);
	        el_Item1.setAttribute("ProductClass",DefaultProductClass);
	        el_Item1.setAttribute("UOM",UnitOfMeasure);
	        el_Item1.setAttribute("ShortDescription",ShortDescription);
	        el_Item1.setAttribute("TotalNumberOfComps",TotalNumberOfComp);
	        el_Item1.setAttribute("NumSecondarySerials",NumSecondarySerials);
	        el_Item1.setAttribute("UnitOfMeasure",UnitOfMeasure);
	        
	        //Following added after final demo
	        el_Item1.setAttribute("BatchNo",BatchNo);
	        el_Item1.setAttribute("LotAttribute1",LotAttribute1);
	        el_Item1.setAttribute("LotAttribute2",LotAttribute2);
	        el_Item1.setAttribute("LotAttribute3",LotAttribute3);
	        el_Item1.setAttribute("LotNumber",LotNumber);
	        el_Item1.setAttribute("RevisionNo",RevisionNo);
	        //End adding after final demo

	        //System.out.println("reached here1");
	        
	        Element el_Components=returnDocument.createElement("Components");
	        el_Item1.appendChild(el_Components);

			//GS - Also check if the count>0...ComponentList.getLenght()>0
	        if(ComponentList!=null){
	        	for(int count=0;count<ComponentList.getLength();count++){
	        			
	        		//System.out.println("reached here2");
	        			Element ComponentEl = (Element)ComponentList.item(count);
	        			
	        			Element el_Component=returnDocument.createElement("Component");
				        el_Components.appendChild(el_Component);
				        el_Component.setAttribute("ItemID",ComponentEl.getAttribute("ComponentItemID"));
				        
				        ////System.out.println("reached here3");
				        HashMap itemAttributes = getIsSerialFlag(env,ComponentEl.getAttribute("ComponentItemID"),OrganizationCode);
				        //Added after final demo
				        HashMap CompTagAttributes = getTagAttributes(env,ComponentEl.getAttribute("ComponentItemID"),
				        							OrganizationCode,itemAttributes.get("UnitOfMeasure").toString());
				        
				        //End Adding after final demo
				        //System.out.println("reached here4,IsserialFlag:-"+itemAttributes.get("IsSerialTracked"));
				        el_Component.setAttribute("SerializedFlag",itemAttributes.get("IsSerialTracked").toString());
				        //System.out.println("test......... 1");
				        el_Component.setAttribute("KitQuantity",ComponentEl.getAttribute("KitQuantity").toString());
				        //System.out.println("test.......... 2");
				        float ComponentQtyPerKit =  Float.parseFloat(ComponentEl.getAttribute("KitQuantity"));
				        //System.out.println("ComponentQtyPerKit:-"+ComponentQtyPerKit+" return QTY:-"+RetQty);
				        float MaxPossibleReturnForComponent = ComponentQtyPerKit*RetQty;
				        //System.out.println("MaxPossibleReturnForComponent:-"+MaxPossibleReturnForComponent);
				        
				        el_Component.setAttribute("MaxReturn",new Float(MaxPossibleReturnForComponent).toString());
				        
				        el_Component.setAttribute("UnitOfMeasure",itemAttributes.get("UnitOfMeasure").toString());
				        el_Component.setAttribute("DefaultProductClass",itemAttributes.get("DefaultProductClass").toString());
				        el_Component.setAttribute("ShortDescription",itemAttributes.get("ShortDescription").toString());
				        el_Component.setAttribute("IsSerialTracked",itemAttributes.get("IsSerialTracked").toString());
				        el_Component.setAttribute("TagControlFlag",itemAttributes.get("TagControlFlag").toString());
				        el_Component.setAttribute("TimeSensitive",itemAttributes.get("TimeSensitive").toString());
				        el_Component.setAttribute("NumSecondarySerials",itemAttributes.get("NumSecondarySerials").toString());
				        
				        //added the following lines after final demo
				        el_Component.setAttribute("BatchNo",CompTagAttributes.get("BatchNo").toString());
				        el_Component.setAttribute("LotAttribute1",CompTagAttributes.get("LotAttribute1").toString());
				        el_Component.setAttribute("LotAttribute2",CompTagAttributes.get("LotAttribute2").toString());
				        el_Component.setAttribute("LotAttribute3",CompTagAttributes.get("LotAttribute3").toString());
				        el_Component.setAttribute("LotNumber",CompTagAttributes.get("LotNumber").toString());
				        el_Component.setAttribute("RevisionNo",CompTagAttributes.get("RevisionNo").toString());
				        //End adding lines after final demo
	        	}//End For Loop
	        }//End if ---ComponentList!=null
	        	
	    	return returnDocument;
	    }//End callGetItemDetails()
	    
	    
	    //-----------------------------------use this to get the tag attributes for the components
	    public HashMap getTagAttributes(YFSEnvironment env,String ItemID,String OrgCode,String UnitOfMeasure)
	    throws Exception{
	    	HashMap CompAttributes = new HashMap();
	    	Document ItemDetailsDoc_out = null;
	    	Document ItemDetails_out_temp = null;
	    	//----------Preparing the input xml for the getItemDetails API
	    	Document getItemDetails_IN = XMLUtil.newDocument();

	    	Element el_Item=getItemDetails_IN.createElement("Item");
	    	getItemDetails_IN.appendChild(el_Item);
	    	el_Item.setAttribute("ItemID",ItemID);
	    	el_Item.setAttribute("ItemKey","");
	    	el_Item.setAttribute("OrganizationCode",OrgCode);
	    	el_Item.setAttribute("UnitOfMeasure",UnitOfMeasure);
	    	
	    	
	    	//-------------------Prepare output template- to get the list of component---
	    	ItemDetails_out_temp = XMLUtil.newDocument();

	    	Element el_Item2=ItemDetails_out_temp.createElement("Item");
	    	ItemDetails_out_temp.appendChild(el_Item2);


	    	Element el_Components2=ItemDetails_out_temp.createElement("Components");
	    	el_Item2.appendChild(el_Components2);


	    	Element el_Component2=ItemDetails_out_temp.createElement("Component");
	    	el_Components2.appendChild(el_Component2);
	    	el_Component2.setAttribute("ComponentDescription","");
	    	el_Component2.setAttribute("ComponentItemID","");
	    	el_Component2.setAttribute("ComponentItemKey","");
	    	el_Component2.setAttribute("ComponentOrganizationCode","");
	    	el_Component2.setAttribute("ComponentUnitOfMeasure","");
	    	el_Component2.setAttribute("ItemKey","");
	    	el_Component2.setAttribute("KitQuantity","");
	    	
	    	Element el_Component3=ItemDetails_out_temp.createElement("InventoryTagAttributes");
	    	el_Item2.appendChild(el_Component3);
	    	//--------------------End Output template to get the list of component
	    		    	
	    	//System.out.println("The input xml for getCompoenentItemDetails is:-"+XMLUtil.getXMLString(getItemDetails_IN));
	    	env.setApiTemplate("getItemDetails",ItemDetails_out_temp);
	    	ItemDetailsDoc_out = CommonUtilities.invokeAPI(env,"getItemDetails",getItemDetails_IN);
	    	env.clearApiTemplate("ItemDetails_out_temp");
	    	//System.out.println("The output xml from getComponentItemDetails is:-"+XMLUtil.getXMLString(ItemDetailsDoc_out));
	    	
	    	 String  BatchNo = "";
	    	 String  LotAttribute1 = "";
	    	 String  LotAttribute2 = "";
	    	 String  LotAttribute3 = "";
	    	 String  LotNumber = "";
	    	 String  RevisionNo = "";
	    	 
	    	if(ItemDetailsDoc_out.getDocumentElement().getElementsByTagName("InventoryTagAttributes")!=null){	
	    		NodeList InvTagList  = ItemDetailsDoc_out.getDocumentElement().getElementsByTagName("InventoryTagAttributes");
			    	 //System.out.println("inside gettagattributes....1"); 
			    	 //GS - Also check if InvTagList!=null... ie InvTagList!=null && InvTagList.getLength()>0
			    	 if(InvTagList.getLength()>0){
			    		Element InvTagEl =  (Element)InvTagList.item(0);
				    	BatchNo =  InvTagEl.getAttribute("BatchNo");
				    	LotAttribute1 =  InvTagEl.getAttribute("LotAttribute1");
				    	LotAttribute2 =  InvTagEl.getAttribute("LotAttribute2");
				    	LotAttribute3 =  InvTagEl.getAttribute("LotAttribute3");
				    	LotNumber =  InvTagEl.getAttribute("LotNumber");
				    	RevisionNo =  InvTagEl.getAttribute("RevisionNo");
			    	 }//End if
	    	}//End -- checking if tag attribute node exists
	    	 
	    	 //System.out.println("inside gettagattributes....2"); 
	    	 
	    	 CompAttributes.put("BatchNo",BatchNo);
	    	 CompAttributes.put("LotAttribute1",LotAttribute1);
	    	 CompAttributes.put("LotAttribute2",LotAttribute2);
	    	 CompAttributes.put("LotAttribute3",LotAttribute3);
	    	 CompAttributes.put("LotNumber",LotNumber);
	    	 CompAttributes.put("RevisionNo",RevisionNo);
	    	 
	    	return CompAttributes;
	    }//End getTagAttributes()
	    
	    //-------------------------------------------------
	    public HashMap getIsSerialFlag(YFSEnvironment env,String ItemID,String OrgCode)throws Exception{
	    	
	    	Document ItemListDoc_out = null;
	    	Document IteList_Template = null;
	    	Document ItemListDoc_in = XMLUtil.newDocument();
	    	HashMap itemAttributes = new HashMap(); 

	    	Element el_Item2=ItemListDoc_in.createElement("Item");
	    	ItemListDoc_in.appendChild(el_Item2);
	    	el_Item2.setAttribute("CallingOrganizationCode",OrgCode);
	    	el_Item2.setAttribute("ItemID",ItemID);
	    	
	    	//---------------------Template
	    	IteList_Template = XMLUtil.newDocument();

	    	Element el_ItemList=IteList_Template.createElement("ItemList");
	    	IteList_Template.appendChild(el_ItemList);


	    	Element el_Item=IteList_Template.createElement("Item");
	    	el_ItemList.appendChild(el_Item);
	    	el_Item.setAttribute("ItemID","");
	    	el_Item.setAttribute("ItemKey","");
	    	el_Item.setAttribute("OrganizationCode","");
	    	el_Item.setAttribute("UnitOfMeasure","");


	    	Element el_PrimaryInformation=IteList_Template.createElement("PrimaryInformation");
	    	el_Item.appendChild(el_PrimaryInformation);
	    	el_PrimaryInformation.setAttribute("DefaultProductClass","");
	    	el_PrimaryInformation.setAttribute("NumSecondarySerials","");
	    	el_PrimaryInformation.setAttribute("ShortDescription","");


	    	Element el_InventoryParameters=IteList_Template.createElement("InventoryParameters");

	    	el_Item.appendChild(el_InventoryParameters);
	    	el_InventoryParameters.setAttribute("IsSerialTracked","");
	    	el_InventoryParameters.setAttribute("TagControlFlag","");
	    	el_InventoryParameters.setAttribute("TimeSensitive","");
	    	//----------------------Template End
	    	
	    	//System.out.println("The \"IteList_Template\" for getItemList API is:-"+XMLUtil.getXMLString(IteList_Template));
	    	env.setApiTemplate("getItemList",IteList_Template);	
	    	//System.out.println("The input xml for getItemList2 is:-"+XMLUtil.getXMLString(ItemListDoc_in));
	    	ItemListDoc_out = CommonUtilities.invokeAPI(env,"getItemList",ItemListDoc_in);
	    	env.clearApiTemplate("getItemList");
		    //System.out.println("The output xml for getItemList2222 is:-"+XMLUtil.getXMLString(ItemListDoc_out));
		    
		    //Get item tag
		    NodeList ItemNodes = ItemListDoc_out.getDocumentElement().getElementsByTagName("Item");
		    
		    //GS - ItemNodes!=null && ItemNodes.getLength()>0
		    Element ItemEl = (Element)ItemNodes.item(0);
		    //System.out.println("UnitOfMeasure:-"+ItemEl.getAttribute("UnitOfMeasure"));
		    itemAttributes.put("UnitOfMeasure",ItemEl.getAttribute("UnitOfMeasure"));
		    ////System.out.println("UnitOfMeasure:-"+ItemEl.getAttribute("UnitOfMeasure"));
		    
		    //PrimaryInformation
		    NodeList PrimaryInformation = ItemListDoc_out.getDocumentElement().getElementsByTagName("PrimaryInformation");
		    
		    //GS - PrimaryInformation!=null && PrimaryInformation.getLength()>0 
		    Element PrimaryInformationEL = (Element)PrimaryInformation.item(0);
		    //System.out.println("DefaultProductClass:-"+PrimaryInformationEL.getAttribute("DefaultProductClass"));
		    //System.out.println("NumSecondarySerials:-"+PrimaryInformationEL.getAttribute("NumSecondarySerials"));
		    
		    itemAttributes.put("DefaultProductClass",PrimaryInformationEL.getAttribute("DefaultProductClass"));
		    itemAttributes.put("ShortDescription",PrimaryInformationEL.getAttribute("ShortDescription"));
		    itemAttributes.put("NumSecondarySerials",PrimaryInformationEL.getAttribute("NumSecondarySerials"));
		    
		    
		    NodeList InventoryParameters = ItemListDoc_out.getDocumentElement().getElementsByTagName("InventoryParameters");
		    
		    //GS - InventoryParameters!=null && InventoryParameters.getLength()>0
		    Element InventoryParametersEl = (Element)InventoryParameters.item(0);
		    //System.out.println("IsSerialTracked:-"+InventoryParametersEl.getAttribute("IsSerialTracked"));
		    //System.out.println("TagControlFlag:-"+InventoryParametersEl.getAttribute("TagControlFlag"));
		    //System.out.println("TimeSensitive:-"+InventoryParametersEl.getAttribute("TimeSensitive"));
		    
		    itemAttributes.put("IsSerialTracked",InventoryParametersEl.getAttribute("IsSerialTracked"));
		    itemAttributes.put("TagControlFlag",InventoryParametersEl.getAttribute("TagControlFlag"));
		    itemAttributes.put("TimeSensitive",InventoryParametersEl.getAttribute("TimeSensitive"));
		    //String IsSerialTracked = InventoryParametersEl.getAttribute("IsSerialTracked");
		    
		    
		    return itemAttributes;
	    }//End callGetItemList()
	    
	    //public HashMap getTagDetials(){
	    	
	    	
	  //  }
	    public void setProperties(Properties prop) throws Exception
	    {
	        _properties = prop;
	    }
}//End Class
	   
	
    
    


