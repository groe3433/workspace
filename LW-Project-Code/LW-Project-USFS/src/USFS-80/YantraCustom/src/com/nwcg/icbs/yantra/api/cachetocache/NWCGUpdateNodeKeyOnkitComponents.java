package com.nwcg.icbs.yantra.api.cachetocache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSConnectionHolder;
import com.yantra.yfs.japi.YFSEnvironment;
/*
 * author : gAgrawal
 * CR-701 : This class is inserting Receiving node as ship node key on the kit components through sql update,
 * because for components 'At_Node' flag is false , so can not be updated by standard API.
 */
public class NWCGUpdateNodeKeyOnkitComponents
    implements YIFCustomApi
{

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	private static Logger log = Logger.getLogger(NWCGUpdateNodeKeyOnkitComponents.class.getName());
	
	public Document updateNodeKey(YFSEnvironment env,Document inXML) throws Exception {
		String strProgID = "NWCGUpdateNodeKeyOnkitComponents";
		Date dtCurrentDate = new Date();
		List<String> lsSerialChilds = new ArrayList<String>();
		Element eleRoot = inXML.getDocumentElement();
		String strNodeKey = eleRoot.getAttribute("ReceivingNode");
		NodeList lsReceiptLines = inXML.getElementsByTagName("ReceiptLine");
		if(lsReceiptLines != null)
		{
			for(int i=0;i<lsReceiptLines.getLength();i++)
			{
				Element eleReceiptLine = (Element)lsReceiptLines.item(i);
				String strSerialNo = eleReceiptLine.getAttribute("SerialNo");
				String strItemID = eleReceiptLine.getAttribute("ItemID");
				if(strSerialNo != null && !strSerialNo.equals(""))
				{
					Document opgetAllChildSerialComps = getAllChildSerialComps(env,strSerialNo,strItemID);
					NodeList serialNodeList = opgetAllChildSerialComps.getDocumentElement().getElementsByTagName("Serial");
					if(serialNodeList != null)
					{
						for(int k=0;k<serialNodeList.getLength();k++)
						{
							Element SerialElem = (Element)serialNodeList.item(k);
							String CompSerialKey = SerialElem.getAttribute("GlobalSerialKey");
							lsSerialChilds.add(CompSerialKey);
						}
					}
				}
				
			}
		}
		if(lsSerialChilds != null && !lsSerialChilds.isEmpty())
		{
			try{
				YFSConnectionHolder connHolder = (YFSConnectionHolder)env;
		        Connection c = null;
		        java.sql.Statement stmt = null;
		        Iterator<String> itr = lsSerialChilds.listIterator();
		        while(itr.hasNext())
		        {
		        	String strSerialKey = itr.next();
		        	 ResultSet rs = null;
					 c = connHolder.getDBConnection();
		             stmt = c.createStatement();
		             
		             rs = stmt.executeQuery("update yfs_global_serial_num set shipnode_key = '"+strNodeKey+ "'"+
		            		 				" ,modifyts=sysdate,modifyprogid ='"+strProgID+"'"+" where global_serial_key = '"+strSerialKey+"'"); 
		             rs.close();
		             stmt.close();
		             c.commit();
		            
		        }
			}
			catch(Exception E)
			{
				throw new NWCGException("NWCG_DB_UPDATE_ERROR");
			}
			
		}
		return inXML;
	}
	
	private Document getAllChildSerialComps(YFSEnvironment env,String strSerialNo,String strItemID) throws Exception 
	{
		if(log.isVerboseEnabled()) log.verbose("starting getAllChildSerialComponents ");
				
		String strSerialKey = "";
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
		if(log.isVerboseEnabled()) log.verbose("getAllChildSerialComponents::Invoking getSerialList with input "+XMLUtil.getXMLString(docGetSerialListIP));
		docGetSerialListOP = CommonUtilities.invokeAPI(env,"getSerialList",docGetSerialListIP);
		if(log.isVerboseEnabled()) log.verbose("getAllChildSerialComponents:: getSerialList output "+XMLUtil.getXMLString(docGetSerialListOP));
		env.clearApiTemplate("getSerialList");
		// should return only one record
		strSerialKey = StringUtil.nonNull(XPathUtil.getString(docGetSerialListOP,"SerialList/Serial/@GlobalSerialKey"));
		if(log.isVerboseEnabled()) log.verbose("getAllChildSerialComponents got the golbal serial key ="+strSerialKey);
	
		// have to reset the api actually midifies the input document
		docGetSerialListIP1 = XMLUtil.createDocument("Serial");
		elemGetSerialListIP1 = docGetSerialListIP1.getDocumentElement();
		// get all the serial numbers whoes parent is this serial number
		// will get all the child components
		// clearing off all the child elements
		elemGetSerialListIP1.setAttribute("SerialNo","");
		// getting all the serials whoes parent is this serial 
		elemGetSerialListIP1.setAttribute("ParentSerialKey",strSerialKey);
		env.setApiTemplate("getSerialList","NWCGProcessBlindReturn_getSerialList");
		
		docGetSerialListOP1 = CommonUtilities.invokeAPI(env,"getSerialList",docGetSerialListIP1);
		//System.out.println("Child List - 1st Level "+XMLUtil.getXMLString(docGetSerialListOP1));
		env.clearApiTemplate("getSerialList");
		
		docGetSerialListOP1 = getSubKitComponents(env,docGetSerialListOP1,docGetSerialListOP1);
		//System.out.println("Child List - N Level "+XMLUtil.getXMLString(docGetSerialListOP1));
		return docGetSerialListOP1;
	}
	

	private Document getSubKitComponents(YFSEnvironment env,Document SerialDoc,Document SerialInDoc) throws Exception 
	{
		if(log.isVerboseEnabled()) log.verbose("starting getSubKitComponents ");
		//System.out.println("getSubKitComponents1");
		NodeList serialNodeList = SerialDoc.getDocumentElement().getElementsByTagName("Serial");
		for(int count=0;count<serialNodeList.getLength();count++){
			Element SerialElem = (Element)serialNodeList.item(count);
			String serialKey = SerialElem.getAttribute("GlobalSerialKey");
			Document docGetSerialListOP1 = getComponents(env,serialKey);
			//System.out.println("getSubKitComponents1 1");
			NodeList serialNodeList1 = docGetSerialListOP1.getDocumentElement().getElementsByTagName("Serial");
			if (serialNodeList1.getLength() > 0)
			{
				for(int cnt1=0;cnt1<serialNodeList1.getLength();cnt1++){
					Element SerialElem1 = (Element)serialNodeList1.item(cnt1);
					//System.out.println("SerialElem1 :"+XMLUtil.getElementXMLString(SerialElem1));
					Element ele1 = SerialInDoc.createElement("Serial");
					SerialInDoc.getDocumentElement().appendChild(ele1);
					XMLUtil.copyElement(SerialInDoc,SerialElem1,ele1);
					//System.out.println("getSubKitComponents1 2");
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
		//System.out.println("docGetSerialListOP2 :"+XMLUtil.getXMLString(docGetSerialListOP2));
		
		return docGetSerialListOP2;
	}
	
}