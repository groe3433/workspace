/**
 * 
 */
package com.nwcg.icbs.yantra.ui.item;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSConnectionHolder;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * @author jvishwakarma
 *
 */
public class NWCGPopulateItemPODetails implements YIFCustomApi {
	/**
	 * Logger Instance.
	 */
	private static Logger logger = Logger
			.getLogger(NWCGPopulateItemPODetails.class.getName());

	
	/* (non-Javadoc)
	 * @see com.yantra.interop.japi.YIFCustomApi#setProperties(java.util.Properties)
	 */
	public void setProperties(Properties props) throws Exception {
		// TODO Auto-generated method stub

	}
	/*
	 * this api will populate the item details when the user enters either the NSN or the ItemID
	 * 
	 */
	public Document populateItemDetails(YFSEnvironment env, Document inDoc) throws Exception{
		
		//System.out.println("Input "+XMLUtil.getXMLString(inDoc));

		YFSConnectionHolder connHolder = (YFSConnectionHolder)env;
		Connection c = null;
		java.sql.Statement stmt = null;
		ResultSet rs = null;

		c = connHolder.getDBConnection();
		stmt = c.createStatement();
				
		Element elem_root = inDoc.getDocumentElement();
		String strEntCode = elem_root.getAttribute("SupplierID");
		String strItemId = elem_root.getAttribute("ItemID");
		String strNode = elem_root.getAttribute("Node");
		/*NodeList nlExtn = elem_root.getElementsByTagName("Extn");
		String strNSN = "" ;
		if(nlExtn != null && nlExtn.getLength() > 0)
		{
			Element elemExtn = (Element)nlExtn.item(0);
			strNSN = elemExtn.getAttribute("ExtnNSN");
			elemExtn.setAttribute("ExtnNSN","");
		}*/
		// NSN is no more an extenssion, we will use GlobalItemID instead
		//elem_root.setAttribute("GlobalItemID",strNSN);
		// return null if the user havnt entred either hte nsn or the item id
		// already taken care of on client side, but still....
		if(StringUtil.isEmpty(strItemId))
		{
			//if(StringUtil.isEmpty(strNSN))
			{
				return inDoc ;
			}
		}
		if(strEntCode == null)
			strEntCode = "" ;
		// we dont know if the item id is pased or the nsn is passed, passing the input xml as it is
		Document doc = CommonUtilities.invokeAPI(env,"NWCGPopulateItemPODetails_getItemList","getItemList",inDoc);
		// now fetch the item id and the nsn
		if(doc != null)
		if(logger.isVerboseEnabled()) logger.verbose("return doc from getItemList "+ XMLUtil.getXMLString(doc));
		//System.out.println("return doc from getItemList\n"+ XMLUtil.getXMLString(doc));

		Document returnXML = null ;
		if(doc != null )
		{
			NodeList nodeLst = doc.getDocumentElement().getElementsByTagName("Item");
			if(logger.isVerboseEnabled()) logger.verbose("Item List Length "+nodeLst.getLength());
			if(nodeLst.getLength() <= 0)
			{
				// item does not exists in system
				elem_root.setAttribute("AvailableQty","");
				elem_root.setAttribute("ShortDescription","");
				if(logger.isVerboseEnabled()) logger.verbose("returning "+XMLUtil.getXMLString(inDoc));
				return inDoc;
			}
			Element item = (Element) nodeLst.item(0);
			strItemId = item.getAttribute("ItemID");
			// get the item id and nsn
			//strItemId = XPathUtil.getString(doc,"/ItemList/Item/@ItemID");
			String strNSN = item.getAttribute("GlobalItemID");
			String strUOM = item.getAttribute("UnitOfMeasure");
			/* Begin CR 572 - ML- */
			String strProdLine = ""; 
			/* End CR 572 - ML- */
			
			NodeList nlPI = item.getElementsByTagName("PrimaryInformation");
			String strPC = "",strDesc="";
			if(nlPI != null && nlPI.getLength() >= 0)
			{
				Element elemPI = (Element) nlPI.item(0);
				strPC = elemPI.getAttribute("DefaultProductClass");
				strDesc = elemPI.getAttribute("ShortDescription");
				/* Begin CR 572 - ML- */
				strProdLine = elemPI.getAttribute("ProductLine");
				/* End CR 572 - ML- */
			}			
			if(logger.isVerboseEnabled()) logger.verbose("strDesc => "+strDesc);
			
			if(strDesc == null){
				strDesc = "" ;
			}
			if(logger.isVerboseEnabled()) logger.verbose("passing the nsn as "+ strNSN + " desc as "+ strDesc);
			
			// Top of -- CR 451 - JK
			NodeList nlExtn = item.getElementsByTagName("Extn");
			String strExtnStandardPack = "";
			if(nlExtn != null && nlExtn.getLength() >= 0)
			{
				Element elemNlExtn = (Element) nlExtn.item(0);
				strExtnStandardPack = elemNlExtn.getAttribute("ExtnStandardPack");
			}
			// Bottom of -- CR 451 - JK

			// get the supplier item details
			returnXML = getReturnXML(env,strItemId,strNSN,strEntCode,strPC,strDesc,strUOM,strNode,strProdLine,strExtnStandardPack);
		}
		if(returnXML != null)
			if(logger.isVerboseEnabled()) logger.verbose("returning_1 "+ XMLUtil.getXMLString(returnXML));

		//System.out.println("returning_1 "+ XMLUtil.getXMLString(returnXML));
		// -- begin -- reserved quantity consideration
		// -- end -- reserved quantity consideration

		return returnXML;
		 
	}
	/*
	 * get the data from supplier item
	 */
	private Document getReturnXML(YFSEnvironment env,String strItemId, String strNSN, String strEntCode,String strPC,String strDesc,String strUOM,String strNode,String strProdLine,String strExtnStandardPack) throws Exception {
		Document doc = XMLUtil.createDocument("NWCGSupplierItem");
		Element elem = doc.getDocumentElement();
		elem.setAttribute("SupplierID",strEntCode);
		elem.setAttribute("ItemID",strItemId);
		if(doc != null )
			if(logger.isVerboseEnabled()) logger.verbose("callling the NWCGGetSupplierItemDetails "+ XMLUtil.getXMLString(doc));
		
		Document supDoc = CommonUtilities.invokeService(env,NWCGConstants.NWCG_GET_SUPPLIER_ITEM_DETAILS,doc);
		
		//if(logger.isVerboseEnabled()) logger.verbose("Document is NULL OR NOT CHECK IT OUT ");
		if(supDoc == null)
		{
			// assing in case no document is returned return the user the data
			supDoc = doc;
		}
		// set the data
		Element sup_elem = supDoc.getDocumentElement();
		sup_elem.setAttribute("ExtnNSN",strNSN);
		sup_elem.setAttribute("ProductClass",strPC);
		sup_elem.setAttribute("ShortDescription",strDesc);
		sup_elem.setAttribute("UnitOfMeasure",strUOM);
		/* Begin CR 572 - ML- */
		sup_elem.setAttribute("ProductLine",strProdLine);
		/* End CR 572 - ML- */
		sup_elem.setAttribute("ExtnStdPack",strExtnStandardPack);

		// -- JSK reserved quantity consideration: added strEndDate
		String strEndDate = "2500-01-01"; // add EndDate for reserved Qty consideration

		Document docSupply = CommonUtilities.getSupplyDetails(strItemId,strUOM,strPC,strNode,strEndDate,env);
		Element elemSupply = docSupply.getDocumentElement();
		sup_elem.setAttribute("AvailableQty",StringUtil.nonNull(elemSupply.getAttribute("AvailableQty")));
		
		
		if(supDoc != null)
			if(logger.isVerboseEnabled()) logger.verbose("returning "+ XMLUtil.getXMLString(supDoc));
		// TODO Auto-generated method stub
		return supDoc;
	}
	
}