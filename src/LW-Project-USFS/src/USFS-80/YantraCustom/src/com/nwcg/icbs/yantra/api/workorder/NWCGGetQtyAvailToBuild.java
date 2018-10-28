/**
 * 
 */
package com.nwcg.icbs.yantra.api.workorder;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * @author jsk
 * calculate the minimum quantity from the kit components' available RFI quantities
 * this will be guide user on how many kit can be built
 */
public class NWCGGetQtyAvailToBuild implements YIFCustomApi {
	/**
	 * Logger Instance.
	 */
	private static Logger logger = Logger.getLogger(NWCGGetQtyAvailToBuild.class.getName());
	
	/* (non-Javadoc)
	 * @see com.yantra.interop.japi.YIFCustomApi#setProperties(java.util.Properties)
	 */
	public void setProperties(Properties props) throws Exception {
		// TODO Auto-generated method stub
	}

	public Document GetQtyAvailToBuild(YFSEnvironment env, Document inDoc) throws Exception{

		if(logger.isVerboseEnabled()) logger.verbose("Entering NWCGGetQtyAvailToBuild()->"+XMLUtil.getXMLString(inDoc));
		//System.out.println("Entering NWCGGetQtyAvailToBuild()->"+XMLUtil.getXMLString(inDoc));

		String qtyAvailToBuild = "0"; // return value
		String strEndDate = "2500-01-01"; // add EndDate for reserved Qty consideration
		String strItemId = "";// fetch the itemid from the input xml
		String strUom = "" ;// fetch the node from input xml 
		String strPC = "" ;// fetch the node from input xml 
		String strNode = "" ;// fetch the node from input xml

		Element root_item = inDoc.getDocumentElement();
		if(root_item != null)
		{
			strItemId = root_item.getAttribute("ItemID");
			strUom = root_item.getAttribute("Uom");
			strPC = root_item.getAttribute("PC");
			strNode = root_item.getAttribute("Node");
		}
		if(logger.isVerboseEnabled()) logger.verbose("Item Id = "+strItemId+" Uom = "+strUom+" PC = "+strPC+" Node = "+strNode);
		//System.out.println("Item Id = "+strItemId+" Uom = "+strUom+" PC = "+strPC+" Node = "+strNode);
		// invoke the getItemDetails to fetch the Components' List
		Document eleComponentList = getItemDetails(strItemId,strUom,env);

		if(eleComponentList != null)
		{
			if(logger.isVerboseEnabled()) logger.verbose("output getItemDetails => "+XMLUtil.getXMLString(eleComponentList));
			//System.out.println("output getItemDetails => \n"+XMLUtil.getXMLString(eleComponentList));
			int minQtyToBuild = 0;
			int qtyToBuild = 0;
			NodeList nodeLst = eleComponentList.getDocumentElement().getElementsByTagName("Component");
			for(int i = 0 ; i < nodeLst.getLength() ; i++)
			{
				Element eleCpnt = (Element) nodeLst.item(i);
				String strCpntItemID = eleCpnt.getAttribute("ComponentItemID");
				String strCpntUom = eleCpnt.getAttribute("ComponentUnitOfMeasure");
				String strCpntKitQty = eleCpnt.getAttribute("KitQuantity");
				//System.out.println("strCpntItemID=["+strCpntItemID+"] strCpntUom=["+ strCpntUom + "] strCpntKitQty=["+ strCpntKitQty + "]");
				if(strCpntItemID != null && (!strCpntItemID.equals("")))
				{
					// JK - reserved quantity consideration: added strEndDate
					// invoke the getNodeInventory to obtain available RFI quantity per component item
					//System.out.println("Entering getNodeInventory...");
					Document docNodeInventory = getNodeInventory(strCpntItemID, strCpntUom, strPC, strNode, env);
					if(docNodeInventory != null)
					{
						if(logger.isVerboseEnabled()) logger.verbose("docNodeInventory => "+ XMLUtil.getXMLString(docNodeInventory));
						//System.out.println("docNodeInventory => \n"+ XMLUtil.getXMLString(docNodeInventory));

						//get the available RFI Qty -- New method
						Element locationInventoryListElem = (Element)XMLUtil.getChildNodeByName(docNodeInventory.getDocumentElement(),"LocationInventoryList");
						String strQtyAvailToBuild = locationInventoryListElem.getAttribute("totalAvailableQty");

						if(strQtyAvailToBuild != null && (!strQtyAvailToBuild.equals("")))
						{
							//String strQtyAvailToBuild = shipNodeElem.getAttribute("AvailableQty");
							int intQtyAvailToBuild = new Double(strQtyAvailToBuild).intValue();
							int intCpntKitQty = new Double(strCpntKitQty).intValue();
							//System.out.println("Item=["+strCpntItemID+"] intQtyAvailToBuild=["+ Integer.toString(intQtyAvailToBuild) + "] intCpntKitQty=["+Integer.toString(intCpntKitQty)+"]");
							qtyToBuild = intQtyAvailToBuild / intCpntKitQty; //decimal is dropped
							//System.out.println("qtyToBuild=["+ Integer.toString(qtyToBuild) + "]");
						} else {
							qtyToBuild = 0; //no inventory found for this component
						}
					} else {
						qtyToBuild = 0; //for safety
					}
					//System.out.println("Item=["+strCpntItemID+"] qtyToBuild=["+ Integer.toString(qtyToBuild) + "]");
				}

				if (i == 0)	{
                    minQtyToBuild = qtyToBuild;
				} else {
                    minQtyToBuild = (minQtyToBuild < qtyToBuild) ? minQtyToBuild : qtyToBuild;
				}
			}
            qtyAvailToBuild = Integer.toString(minQtyToBuild);
			//System.out.println("Return_Val: qtyAvailToBuild=["+ qtyAvailToBuild + "]");
		}
		root_item.setAttribute("QtyAvailToBuild",qtyAvailToBuild);
		return inDoc;
	}

	private Document getItemDetails(String strItemId, String strUom, YFSEnvironment env) throws Exception {
		
		Document inputDoc = XMLUtil.createDocument("Item");
		Element root_ele = inputDoc.getDocumentElement();
		root_ele.setAttribute("ItemID",strItemId);
		root_ele.setAttribute("UnitOfMeasure",strUom);
		root_ele.setAttribute("OrganizationCode",NWCGConstants.ENTERPRISE_CODE);

		//construct the template for the API
		Document itemDetailsTemplate = XMLUtil.createDocument("Item");
		Element item = itemDetailsTemplate.getDocumentElement();
		Element components = itemDetailsTemplate.createElement("Components");
		item.appendChild(components);
		Element eleComponent = itemDetailsTemplate.createElement("Component");
		components.appendChild(eleComponent);
		eleComponent.setAttribute("ComponentItemID", "");
		eleComponent.setAttribute("ComponentUnitOfMeasure", "");
		eleComponent.setAttribute("KitQuantity", "");
		//System.out.println("getItemDetails inputDoc => "+ XMLUtil.getXMLString(inputDoc));

		return CommonUtilities.invokeAPI(env,itemDetailsTemplate,"getItemDetails",inputDoc);
	}

	/*
	------------ Input ---------------
	<?xml version="1.0" encoding="UTF-8"?>
	<NodeInventory IgnoreOrdering="Y" Node="CORMK">
		<Inventory InventoryStatus="RFI">
			<InventoryItem ItemID="000608" ProductClass="Supply" UnitOfMeasure="RO" />
		</Inventory>
	</NodeInventory>
	---------------- TEmplate ----------------
	<?xml version="1.0" encoding="UTF-8"?>
	<NodeInventory>
		<LocationInventoryList TotalNumberOfRecords="">
			<LocationInventory LocationId="" 
				Quantity="">
			</LocationInventory>
		</LocationInventoryList>
	</NodeInventory>
	-------------------------------
	*/
	private Document getNodeInventory(String strCpntItemID, String strCpntUom, String strPC, String strNode, YFSEnvironment env) throws Exception {
		
		Document inputDoc = XMLUtil.createDocument("NodeInventory");
		Element nodeInventoryElem = inputDoc.getDocumentElement();
		nodeInventoryElem.setAttribute("IgnoreOrdering","Y");
		nodeInventoryElem.setAttribute("Node",strNode);
		nodeInventoryElem.setAttribute("EnterpriseCode",NWCGConstants.ENTERPRISE_CODE);

		//construct the Inventory element
		Element inventoryElem = inputDoc.createElement("Inventory");
		inventoryElem.setAttribute("InventoryStatus", "RFI");
		nodeInventoryElem.appendChild(inventoryElem);

		//construct the InventoryItem element
		Element inventoryItemElem = inputDoc.createElement("InventoryItem");
		inventoryItemElem.setAttribute("ItemID",strCpntItemID);
		inventoryItemElem.setAttribute("ProductClass",strPC);
		inventoryItemElem.setAttribute("UnitOfMeasure",strCpntUom);
		inventoryElem.appendChild(inventoryItemElem);

		//construct the template for the API
		Document getNodeInventoryTemplate = XMLUtil.createDocument("NodeInventory");
		Element tmplNodeInventoryElem = getNodeInventoryTemplate.getDocumentElement();
		Element tmplLocationInventoryList = getNodeInventoryTemplate.createElement("LocationInventoryList");
		tmplLocationInventoryList.setAttribute("TotalNumberOfRecords","");
		tmplNodeInventoryElem.appendChild(tmplLocationInventoryList);
		Element tmplLocationInventory = getNodeInventoryTemplate.createElement("LocationInventory");
		tmplLocationInventory.setAttribute("Quantity","");
		tmplLocationInventoryList.appendChild(tmplLocationInventory);

		//System.out.println("getNodeInventory getNodeInventoryTemplate => "+ XMLUtil.getXMLString(getNodeInventoryTemplate));
		//System.out.println("getNodeInventory inputDoc => "+ XMLUtil.getXMLString(inputDoc));

		Document out_doc = CommonUtilities.invokeAPI(env,getNodeInventoryTemplate,"getNodeInventory",inputDoc);
		//System.out.println("After API out_doc => "+ XMLUtil.getXMLString(out_doc));

		Element outLocationInventoryListElem = (Element)XMLUtil.getChildNodeByName(out_doc.getDocumentElement(),"LocationInventoryList");
		//System.out.println("outLocationInventoryListElem => "+ XMLUtil.getElementXMLString(outLocationInventoryListElem));

		int totalQuantity = 0;
		if(outLocationInventoryListElem != null)
		{
			NodeList listOfInventory = outLocationInventoryListElem.getElementsByTagName("LocationInventory");
			for(int i=0; i<listOfInventory.getLength(); i++)
			{
				Element curInventory = (Element)listOfInventory.item(i);
				String strTotalQuantity = curInventory.getAttribute("Quantity");
				int intTotalQuantity = new Double(strTotalQuantity).intValue();
				totalQuantity = totalQuantity + intTotalQuantity;
			}
		}
		//System.out.println("Return_Val: totalQuantity=["+ Integer.toString(totalQuantity) + "]");
		outLocationInventoryListElem.setAttribute("totalAvailableQty", Integer.toString(totalQuantity)); //set return value
		//System.out.println("getNodeInventory out_doc => "+ XMLUtil.getXMLString(out_doc));
		return out_doc;
	}
}