package com.nwcg.icbs.yantra.api.refurb.tag;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSConnectionHolder;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGPopulateWorkOrderSerialKit implements YIFCustomApi 
{

	public Document populateWorkOrderSerialKit(YFSEnvironment env, Document inDoc) throws Exception 
	{
		getAttributesFromWorkOrder(inDoc, env);
		return inDoc;
	}

	public void getAttributesFromWorkOrder(Document doc, YFSEnvironment env) 
	{
		System.out.println("Input doc is...");
		System.out.println(XMLUtil.getXMLString(doc));
		Element root = doc.getDocumentElement();
		Document outXML;
		Element elemOutXML;
		String ParentSerialNo = "";
		String ParentItemID = "";
		String ServiceItemID = "";
		ServiceItemID = root.getAttribute("ServiceItemID");
		String strServiceItemGroupCode = root.getAttribute("ServiceItemGroupCode");
		
		try 
		{
			/*
			 * Get the attributes for the WorkOrder (WO), write them to
			 * NWCG_WORK_ORDER_SERIAL_KIT then process each work order component
			 * and write each components attributes to the same table as a
			 * seperate row, but keep the work order key the same for all
			 * components.
			 */
			if(strServiceItemGroupCode.equalsIgnoreCase("KIT"))
			{
			
				outXML = XMLUtil.createDocument("NWCGWorkOrderSerialKit");
				elemOutXML = outXML.getDocumentElement();
	
				// start same attributes for all items in kit
	
				String strProductClass = root.getAttribute("ProductClass");
				elemOutXML.setAttribute("ProductClass", strProductClass);
	
				String strEnterpriseCode = root.getAttribute("EnterpriseCode");
				elemOutXML.setAttribute("EnterpriseCode", strEnterpriseCode);
	
				String strDocumentType = root.getAttribute("DocumentType");
				elemOutXML.setAttribute("DocumentType", strDocumentType);
	
				String strNodeKey = root.getAttribute("NodeKey");
				elemOutXML.setAttribute("NodeKey", strNodeKey);
	
				elemOutXML.setAttribute("SellerOrganizationCode", "NWCG");
	
				elemOutXML.setAttribute("WorkOrderKey", root.getAttribute("WorkOrderKey"));
	
				// end same for all items in work order
	
				ParentItemID = root.getAttribute("ItemID");
				elemOutXML.setAttribute("ItemID", root.getAttribute("ItemID"));
				elemOutXML.setAttribute("UnitOfMeasure", root.getAttribute("Uom"));
				elemOutXML.setAttribute("ShipByDate", root.getAttribute("ShipByDate"));
				elemOutXML.setAttribute("Quantity", "1.00");
	
				// WorkOrderActivityDtl
	
				NodeList nlWorkOrderActivityDtl = root.getElementsByTagName("WorkOrderActivityDtl");
	
				if (nlWorkOrderActivityDtl != null && nlWorkOrderActivityDtl.getLength() >= 1) 
				{
					Element eleWorkOrderActivityDtl = (Element) nlWorkOrderActivityDtl.item(0);
	
					elemOutXML.setAttribute("SerialNo", eleWorkOrderActivityDtl.getAttribute("SerialNo"));
					ParentSerialNo = eleWorkOrderActivityDtl.getAttribute("SerialNo");
					elemOutXML.setAttribute("ParentSerialNo", ParentSerialNo);
	
					NodeList nlWorkOrderTag = eleWorkOrderActivityDtl.getElementsByTagName("WorkOrderTag");
	
					if (nlWorkOrderTag != null && nlWorkOrderTag.getLength() >= 1) 
					{
	
						Element ele = (Element) nlWorkOrderTag.item(0);
	
						elemOutXML.setAttribute("BatchNo", ele.getAttribute("BatchNo"));
						elemOutXML.setAttribute("RevisionNo", ele.getAttribute("RevisionNo"));
						elemOutXML.setAttribute("LotAttribute1", ele.getAttribute("LotAttribute1"));
						elemOutXML.setAttribute("LotAttribute2", ele.getAttribute("LotAttribute2"));
						elemOutXML.setAttribute("LotAttribute3", ele.getAttribute("LotAttribute3"));
						elemOutXML.setAttribute("LotKeyReference", ele.getAttribute("LotKeyReference"));
						elemOutXML.setAttribute("LotNumber", ele.getAttribute("LotNumber"));
						elemOutXML.setAttribute("LotManufactureDate", ele.getAttribute("ManufacturingDate"));
						elemOutXML.setAttribute("WorkOrderTagsKey", ele.getAttribute("WorkOrderTagKey"));
	
					}
				
					Element eleWorkOrderSerialDetail =(Element) XPathUtil.getNode (root,"/WorkOrder/WorkOrderActivityDtl/SerialDetail");
						
					if (eleWorkOrderSerialDetail != null) 
					{
	
						elemOutXML.setAttribute("SecondarySerialNo1",eleWorkOrderSerialDetail.getAttribute("SecondarySerial1"));
						elemOutXML.setAttribute("SecondarySerialNo2",eleWorkOrderSerialDetail.getAttribute("SecondarySerial2"));
						elemOutXML.setAttribute("SecondarySerialNo3",eleWorkOrderSerialDetail.getAttribute("SecondarySerial3"));
						elemOutXML.setAttribute("SecondarySerialNo4",eleWorkOrderSerialDetail.getAttribute("SecondarySerial4"));
						elemOutXML.setAttribute("SecondarySerialNo5",eleWorkOrderSerialDetail.getAttribute("SecondarySerial5"));
						elemOutXML.setAttribute("SecondarySerialNo6",eleWorkOrderSerialDetail.getAttribute("SecondarySerial6"));
	
					}
				}
				/*
				 * ***********Call api to write to db here*********
				 */
	
				//System.out.println("Writing the following to the DB...");
				// //System.out.println(XMLUtil.getXMLString(outXML));
				CommonUtilities.invokeService(env,"NWCGCreateWorkOrderSerialKitService", outXML);
				outXML = clearXMLAttributes(outXML);
	
				NodeList nlWorkOrderComponent = root.getElementsByTagName("WorkOrderComponent");
	
				if (nlWorkOrderComponent != null && nlWorkOrderComponent.getLength() >= 1) 
				{
					for (int y = 0; y < nlWorkOrderComponent.getLength(); y++) 
					{
						/*
						 * Iterate through each WOComponent and write it to the DB
						 * with every iteration.
						 */
	
						Element ele = (Element) nlWorkOrderComponent.item(y);
	
						elemOutXML.setAttribute("ItemID", ele.getAttribute("ItemID"));
						elemOutXML.setAttribute("SerialNo", ele.getAttribute("SerialNo"));
						elemOutXML.setAttribute("ParentSerialNo", ParentSerialNo);
						
						//Make sure that if the component has a serial number, the quantity is set to one
						//Since we are writing to the DB seperately for every item, we can qty to 1
						//if (ele.getAttribute("SerialNo") == null ||ele.getAttribute("SerialNo").equals("") ){
							elemOutXML.setAttribute("Quantity", ele.getAttribute("ComponentQuantity"));
						//}
						//else{
							//elemOutXML.setAttribute("Quantity", "1.00");						
						//}
						
						elemOutXML.setAttribute("ShipByDate", ele.getAttribute("ShipByDate"));
						elemOutXML.setAttribute("UnitOfMeasure", ele.getAttribute("Uom"));
						elemOutXML.setAttribute("TagNumber", ele.getAttribute("TagNumber"));
						elemOutXML.setAttribute("WorkOrderComponentKey", ele.getAttribute("WorkOrderComponentKey"));
	
						NodeList nlWorkOrderComponentTag = ele.getElementsByTagName("WorkOrderComponentTag");
	
						if (nlWorkOrderComponentTag != null && nlWorkOrderComponentTag.getLength() >= 1) 
						{
							Element eleComponentTag = (Element) nlWorkOrderComponentTag.item(0);
	
							elemOutXML.setAttribute("BatchNo", eleComponentTag.getAttribute("BatchNo"));
							elemOutXML.setAttribute("LotAttribute1",eleComponentTag.getAttribute("LotAttribute1"));
							elemOutXML.setAttribute("LotAttribute2",eleComponentTag.getAttribute("LotAttribute2"));
							elemOutXML.setAttribute("LotAttribute3",eleComponentTag.getAttribute("LotAttribute3"));
							elemOutXML.setAttribute("LotKeyReference",eleComponentTag.getAttribute("LotKeyReference"));
							elemOutXML.setAttribute("LotNumber", eleComponentTag.getAttribute("LotNumber"));
							elemOutXML.setAttribute("LotManufactureDate",eleComponentTag.getAttribute("ManufacturingDate"));
							elemOutXML.setAttribute("WorkOrderTagsKey",eleComponentTag.getAttribute("WorkOrderComponentTagKey"));
	
						}
	
						NodeList nlWorkOrderComponentSerialDetail = ele.getElementsByTagName("SerialDetail");
	
						if (nlWorkOrderComponentSerialDetail != null  && nlWorkOrderComponentSerialDetail.getLength() >= 1) 
						{
							Element eleSerialDetail = (Element) nlWorkOrderComponentSerialDetail.item(0);
	
							elemOutXML.setAttribute("SecondarySerialNo1",eleSerialDetail.getAttribute("SecondarySerial1"));
							elemOutXML.setAttribute("SecondarySerialNo2",eleSerialDetail.getAttribute("SecondarySerial2"));
							elemOutXML.setAttribute("SecondarySerialNo3",eleSerialDetail.getAttribute("SecondarySerial3"));
							elemOutXML.setAttribute("SecondarySerialNo4",eleSerialDetail.getAttribute("SecondarySerial4"));
							elemOutXML.setAttribute("SecondarySerialNo5",eleSerialDetail.getAttribute("SecondarySerial5"));
							elemOutXML.setAttribute("SecondarySerialNo6",eleSerialDetail.getAttribute("SecondarySerial6"));
	
						}
	
						// call api to write to db here
	
						 //System.out.println("Writing the following WorkOrder Component to the DB...");
						 //System.out.println(XMLUtil.getXMLString(outXML));
						CommonUtilities.invokeService(env,"NWCGCreateWorkOrderSerialKitService", outXML);
						outXML = clearXMLAttributes(outXML);
	
					}// end for work order component
					
					if (ServiceItemID.equals("KITTING"))
					{
					  set_global_serials(env,nlWorkOrderComponent,ParentSerialNo,ParentItemID);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}// getAttributesFromWorkOrder
	
	public void set_global_serials(YFSEnvironment env,NodeList WOComponent,String ParentSerialNo,String ParentItemID) throws Exception
	{
		String ParentSerialKey = getGlobalSerialKey(env,ParentSerialNo,ParentItemID);
		Document inserialDoc = XMLUtil.newDocument();
		Document outserialDoc = XMLUtil.newDocument();
		YFSConnectionHolder connHolder = (YFSConnectionHolder)env;
	    
		
		Element elem_serial = inserialDoc.createElement("Serial");
		inserialDoc.appendChild(elem_serial);
		elem_serial.setAttribute("ParentSerialKey",ParentSerialKey);
		outserialDoc = CommonUtilities.invokeAPI(env,"getSerialList",inserialDoc);
		
		NodeList serialList = outserialDoc.getElementsByTagName("Serial");

		if (serialList != null && serialList.getLength() >= 1) 
		{
			for (int y = 0; y < serialList.getLength(); y++) 
			{
				Element ele1 = (Element) serialList.item(y);
				String Serial1 = "";
				Serial1 = ele1.getAttribute("SerialNo");
				String GlobalSerialKey = "";
				GlobalSerialKey = ele1.getAttribute("GlobalSerialKey");
				
				int scnt = 0;
				for (int z = 0; z < WOComponent.getLength(); z++) 
				{
					Element ele2 = (Element) WOComponent.item(z);
					String Serial2 = ele2.getAttribute("SerialNo");
					if (Serial1.equals(Serial2))
					{
						scnt++;
					}
				}
				
				/*update yfs_global_serial_num */
				if (scnt == 0)
				{
					 Connection c = null;
			         java.sql.Statement stmt = null;
			         ResultSet rs = null;
					 c = connHolder.getDBConnection();
		             stmt = c.createStatement();
		             
		             rs = stmt.executeQuery("update yfs_global_serial_num set parent_serial_key = ' ' "+
		            		 				" where global_serial_key = '"+GlobalSerialKey+"'"+
					                         " and serial_no = '"+Serial1+"'"+
					                         " and parent_serial_key = '"+ParentSerialKey+"'"); 
		             rs.close();
		             stmt.close();
				}
			}
		}
	
	}
	
    public String getGlobalSerialKey(YFSEnvironment env,String SerialNo,String strItemID)throws Exception{
    	
    	String GSerialKey = "";
    	
    	Document inDoc = XMLUtil.newDocument();
    	Document outDoc = XMLUtil.newDocument();

			Element el_PSerial=inDoc.createElement("Serial");
			inDoc.appendChild(el_PSerial);
			el_PSerial.setAttribute("SerialNo",SerialNo);
			Element el_InvItem=inDoc.createElement("InventoryItem");
			el_PSerial.appendChild(el_InvItem);
			el_InvItem.setAttribute("ItemID",strItemID);
			outDoc = CommonUtilities.invokeAPI(env,"getSerialList",inDoc);
			
			Element PSerialOut = (Element) outDoc.getDocumentElement().getElementsByTagName("Serial").item(0);
			GSerialKey = PSerialOut.getAttribute("GlobalSerialKey");

        return GSerialKey;
    }

	public Document clearXMLAttributes(Document inDoc) {
		/*
		 * This method resets all the attributes listed to "" since we are
		 * reusing the same Document and the component items have different
		 * attributes with some exceptions.
		 */

		Element root = inDoc.getDocumentElement();
		root.setAttribute("BatchNo", "");
		root.setAttribute("ItemID", "");
		root.setAttribute("LotAttribute1", "");
		root.setAttribute("LotAttribute2", "");
		root.setAttribute("LotAttribute3", "");
		root.setAttribute("LotKeyReference", "");
		root.setAttribute("LotManufactureDate", "");
		root.setAttribute("LotNumber", "");
		root.setAttribute("Quantity", "");
		root.setAttribute("SecondarySerialNo1", "");
		root.setAttribute("SecondarySerialNo2", "");
		root.setAttribute("SecondarySerialNo3", "");
		root.setAttribute("SecondarySerialNo4", "");
		root.setAttribute("SecondarySerialNo5", "");
		root.setAttribute("SecondarySerialNo6", "");
		root.setAttribute("SerialNo", "");
		root.setAttribute("ParentSerialNo", "");
		root.setAttribute("ShipByDate", "");
		root.setAttribute("TagNumber", "");
		root.setAttribute("UnitOfMeasure", "");
		root.setAttribute("WorkOrderComponentKey", "");

		return inDoc;
	}

	public static void main(String argv[]) {

	}// main

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}
}