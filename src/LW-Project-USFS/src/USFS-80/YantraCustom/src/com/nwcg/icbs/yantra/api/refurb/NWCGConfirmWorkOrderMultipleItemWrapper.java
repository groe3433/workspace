/**
 * 
 */
package com.nwcg.icbs.yantra.api.refurb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;

/**
 * this is a helper class containg all the component + catalog items id as key and value as component item.
 * this will be used by the refurb API to get the WorkOrderComponent  element and any other enclosed tag
 * @author jvishwakarma
 *
 */
class NWCGConfirmWorkOrderMultipleItemWrapper 
{
	private HashMap hm = new HashMap();
	private Document doc = null ; 
	private ArrayList alComponentForCreateOrder = null ; 
	private ArrayList alComponentForConfirmOrder = null ;
	private Document docCreateWO = null;
	private static Logger log = Logger.getLogger(NWCGConfirmWorkOrderMultipleItemWrapper.class.getName());
	//commenting this out as we will be just sending across the list of all components
	// the caller will ceate an confirm xml out of it
	//private Document docConfirmWO = null; 
	protected NWCGConfirmWorkOrderMultipleItemWrapper(Document inXML,boolean bParse) throws NWCGException
	{
		doc = inXML;
		if(bParse)
		{
			parseInputDocument(doc);
		}
	}
	
	protected NWCGConfirmWorkOrderMultipleItemWrapper(Document inXML)
	{
		doc = inXML;
	}
	/*
	 * this method does the initial parsing of the document
	 * looks out for the given component id in the component list
	 * if item id exists, gets the array list and then add the current element to the array
	 * if item doesnt exists creates a new element and inserts into map
	 */
	protected void parseInputDocument(Document inXML) throws NWCGException 
	{
		if(log.isVerboseEnabled()) log.verbose("NWCGConfirmWorkOrderMultipleItemWrapperNWCGConfirmWorkOrderMultipleItemWrapper::parseInputDocument parsing " + XMLUtil.getXMLString(inXML));
		if(inXML == null)
		{
			throw new NWCGException("Can not Parse a NULL Document");
		}
		
		Element rootElement = inXML.getDocumentElement();
		
		NodeList nl = rootElement.getElementsByTagName("WorkOrderComponent");
		
		if(nl != null && nl.getLength() > 0)
		{
			for(int index = 0; index < nl.getLength() ; index++)
			{
				if(log.isVerboseEnabled()) log.verbose("NWCGConfirmWorkOrderMultipleItemWrapperparseInputDocument ==> " + index);
				Element elemWOC = (Element) nl.item(index);
				String strItemID= elemWOC.getAttribute("ItemID");
				if(log.isVerboseEnabled()) log.verbose("NWCGConfirmWorkOrderMultipleItemWrapperparseInputDocument strItemID==> " + strItemID);
				// extract the item id value from map
				ArrayList lst = (ArrayList)hm.get(strItemID);
				if(log.isVerboseEnabled()) log.verbose("NWCGConfirmWorkOrderMultipleItemWrapperparseInputDocument ==> lst " + lst);
				// if map doesnt have any value create a new entry
				if(lst == null)
				{
					lst = new ArrayList();
					if(log.isVerboseEnabled()) log.verbose("NWCGConfirmWorkOrderMultipleItemWrapperNWCGConfirmWorkOrderMultipleItemWrapper:: inserting " + strItemID);
					hm.put(strItemID,lst);
				}
				// add the current work order component
				if(log.isVerboseEnabled()) log.verbose("NWCGConfirmWorkOrderMultipleItemWrapperparseInputDocument adding " + XMLUtil.getElementXMLString(elemWOC));
				lst.add(elemWOC);
			}
			
		}
		if(log.isVerboseEnabled()) log.verbose("NWCGConfirmWorkOrderMultipleItemWrapperNWCGConfirmWorkOrderMultipleItemWrapper::parseInputDocument END ");
	}
	
	/*
	 * this method prepares the component list for the create work order
	 * initializes the work order create document and arraylist to contain all the elements
	 */
	protected void prepareWorkOrderComponentListForCreateOrder() throws ParserConfigurationException
	{
		
		docCreateWO = XMLUtil.getDocument();
		
		docCreateWO.appendChild(docCreateWO.importNode(doc.getDocumentElement(),true));
		
		alComponentForCreateOrder = new ArrayList();
		// browse through all the elements in the map
		Iterator itrHmKey = hm.entrySet().iterator();
		
		while(itrHmKey.hasNext())
		{
			Map.Entry ele = (Map.Entry) itrHmKey.next();
			// the map contains the arraylist and the arraylist contains all the elements 
			ArrayList alElements = (ArrayList) ele.getValue();
			// the arraylist will have minumim of one element, get the first element
			Element elemWOCActual = (Element)alElements.get(0);
			// import using the create work order document - as this will be imported to create wo doc
			Element elemWOC = (Element)docCreateWO.importNode(elemWOCActual,true);
			// set the quantity as the current arraylist size for non serial items
			if(XMLUtil.getChildNodeByName(elemWOC, "WorkOrderComponentTag") != null)
			elemWOC.setAttribute("ComponentQuantity",alElements.size()+""); // set component quantity
			// set component serial number to blank, we should not pass the component serial as we might have more than one quantity
			// if the quantity is more than 1 then we should not pass the quantity
			elemWOC.setAttribute("SerialNo","");
			
			NWCGRefurbHelper.removeChildElementsFromXML(elemWOC,"SerialDetail");
			NWCGRefurbHelper.removeChildElementsFromXML(elemWOC,"WorkOrderComponentTag");
			
			// add the item in the list
			alComponentForCreateOrder.add(elemWOC);
		}
	}
	
	/*
	 * this method will return the create work order xml derived from the document passed in the constructor 
	 * 
	 */
	protected Document getCreateWorkOrderXML() throws Exception
	{
		if(doc == null) throw new NWCGException("Can not extract NULL document");
		if(log.isVerboseEnabled()) log.verbose("NWCGConfirmWorkOrderMultipleItemWrappercopied document " + XMLUtil.getXMLString(docCreateWO));
		
		if(docCreateWO  != null) return docCreateWO ;
		// after this call thie docCreateWO will not be a NULL
		prepareWorkOrderComponentListForCreateOrder();
		
		removeOldChildElmentsAndAddNewOne(true);
		
		removeSerialAndTagInformationFromHeader();
		
		if(log.isVerboseEnabled()) log.verbose("NWCGConfirmWorkOrderMultipleItemWrapperNWCGConfirmWorkOrderMultipleItemWrapper::getCreateWorkOrderXML returning XML :: " + XMLUtil.getXMLString(docCreateWO));
		
		// return the xml
		return docCreateWO ;
	}
	
	private void removeSerialAndTagInformationFromHeader() throws Exception 
	{
		// if we dont do that we wont be able to confirm the work order 
		// remove the work order tag
		Element elem = (Element)XPathUtil.getNode(docCreateWO,"WorkOrder/WorkOrderTag");
		if(elem != null ) elem.getParentNode().removeChild(elem);
		
		elem = (Element)XPathUtil.getNode(docCreateWO,"WorkOrder/SerialDetail");
		if(elem != null ) elem.getParentNode().removeChild(elem);
		
		docCreateWO.getDocumentElement().setAttribute("SerialNo","");
		
	}

	private void removeOldChildElmentsAndAddNewOne(boolean bCreate)
	{
		//		 remove the existing work order component items
		Document docTemp = null ;
		
		if(bCreate)
			docTemp = docCreateWO;
		
		// first remove all the child elements from the existing element
		NWCGRefurbHelper.removeChildElementsFromXML(docTemp.getDocumentElement(),"WorkOrderComponent");
		// get the work order components item for appending all the childs
		NodeList nlWOCs = docTemp.getElementsByTagName("WorkOrderComponents");
		Element elemWOCs = null ;
		// if its null create one
		if(nlWOCs == null || nlWOCs.getLength() <= 0)
		{
			elemWOCs = docTemp.createElement("WorkOrderComponents");
			docTemp.getDocumentElement().appendChild(elemWOCs);
		}
		else
		{
			elemWOCs = (Element) nlWOCs.item(0);
		}
		
		// append all the elements derived from process method
		if(bCreate)
			NWCGRefurbHelper.appendChildElements(elemWOCs,alComponentForCreateOrder);
		else
			NWCGRefurbHelper.appendChildElements(elemWOCs,alComponentForConfirmOrder);
		
	}
	/*
	 * this method will prepare the confirm work order xml 
	 * needs a map containing all the key as item id + uom and value as the work order component key
	 */
	protected ArrayList prepareWorkOrderComponentListForConfirmOrder(Map itemComponentKeyMap)
	{
		if(alComponentForConfirmOrder != null) return alComponentForConfirmOrder ;
		
		alComponentForConfirmOrder = new ArrayList();
		
		if(log.isVerboseEnabled()) log.verbose("NWCGConfirmWorkOrderMultipleItemWrapperhashmap =>> " + hm + " map size "+hm.entrySet().size());
		
		Iterator itrHmKey = hm.entrySet().iterator();
		
		while(itrHmKey.hasNext())
		{
			Map.Entry ele = (Map.Entry) itrHmKey.next();
			// same reason as of the create work order prepare map
			if(log.isVerboseEnabled()) log.verbose("NWCGConfirmWorkOrderMultipleItemWrapperprepareWorkOrderComponentListForConfirmOrder "+ ele);
			ArrayList alElements = (ArrayList) ele.getValue();
			Iterator itrArrayList = alElements.iterator();
			while(itrArrayList.hasNext())
			{
				
				if(log.isVerboseEnabled()) log.verbose("NWCGConfirmWorkOrderMultipleItemWrapperprepareWorkOrderComponentListForConfirmOrder "+ alElements);
				Element elemWOCActual = (Element)itrArrayList.next();
				if(log.isVerboseEnabled()) log.verbose("NWCGConfirmWorkOrderMultipleItemWrapperprepareWorkOrderComponentListForConfirmOrder " + XMLUtil.getElementXMLString(elemWOCActual));
				Element elemWOC = (Element) docCreateWO.importNode(elemWOCActual,true);
				if(log.isVerboseEnabled()) log.verbose("NWCGConfirmWorkOrderMultipleItemWrapperprepareWorkOrderComponentListForConfirmOrder done with importing ");
				String strSerialNo = StringUtil.nonNull(elemWOCActual.getAttribute("SerialNo"));
				String strItemID = StringUtil.nonNull(elemWOCActual.getAttribute("ItemID"));
				String strUOM = StringUtil.nonNull(elemWOCActual.getAttribute("Uom"));
				if(log.isVerboseEnabled()) log.verbose("NWCGConfirmWorkOrderMultipleItemWrapperprepareWorkOrderComponentListForConfirmOrder  " + strSerialNo + " strItemID "  +strItemID + " strUOM " + strUOM);
				// if the item is a serial number the quantity should always be one
				if(!strSerialNo.equals(""))
				{
					elemWOC.setAttribute("Quantity","1"); // set component quantity
				}
				else
				{
					elemWOC.setAttribute("Quantity",elemWOC.getAttribute("ComponentQuantity"));
				}
				// set the work order key - get the key from the hasmap set by the caller
				// having item id + UOM as key and work order component key as value
				if(log.isVerboseEnabled()) log.verbose("NWCGConfirmWorkOrderMultipleItemWrapperitemComponentKeyMap ==> "+itemComponentKeyMap);
				elemWOC.setAttribute("WorkOrderComponentKey",(String)itemComponentKeyMap.get(strItemID+strUOM));
				// set component serial number to blank, we should not pass the component serial as we might have more than one quantity
				// if the quantity is more than 1 then we should not pass the quantity
				alComponentForConfirmOrder.add(elemWOC);
			}
		}
		if(log.isVerboseEnabled()) log.verbose("NWCGConfirmWorkOrderMultipleItemWrapper returning " + alComponentForConfirmOrder);
		if(log.isVerboseEnabled()) log.verbose("NWCGConfirmWorkOrderMultipleItemWrapper returning " + alComponentForConfirmOrder.size());
		
		return alComponentForConfirmOrder;
	}
	
	/*
	 * returns the total number of for the given item id
	 */
	protected int getTotalRecord(String strItemId)
	{
		if(hm == null)
			return 0;
		
		Object obj = hm.get(strItemId);
		
		if(obj == null) return 0;
		
		return ((ArrayList)obj).size();
	}
	// returns all the items in the list for the given item id
	protected ArrayList getItemComoponents(String str)
	{
		return (ArrayList) hm.get(str);
	}
	
	protected Document getDocumentElement()
	{
		return doc;
	}

}
