/**
 * 
 */
package com.nwcg.icbs.yantra.api.issue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGValidateIssue implements YIFCustomApi {
	/**
	 * Logger Instance.
	 */
	private static Logger logger = Logger
			.getLogger(NWCGValidateIssue.class.getName());

	
	/* (non-Javadoc)
	 * @see com.yantra.interop.japi.YIFCustomApi#setProperties(java.util.Properties)
	 */
	public void setProperties(Properties props) throws Exception {
		// TODO Auto-generated method stub

	}
	public Document validateIssueLine(YFSEnvironment env, Document inDoc) throws Exception{
		 if(logger.isVerboseEnabled())
		 	logger.verbose("Entering ValidateIssue()->"+XMLUtil.getXMLString(inDoc));
		 logger.verbose("Input "+XMLUtil.getXMLString(inDoc));
		 // build the create order
		 NodeList inpOlList = inDoc.getDocumentElement().getElementsByTagName("OrderLine");
		 logger.verbose("order line list "+inpOlList.getLength());
		 int iLength = inpOlList.getLength();
		 
		 ArrayList lst = new ArrayList();
		 
		 for(int i = 0 ; i < iLength;i++)
		 {
			 logger.verbose("Counter "+ i);
			 Element ol = (Element)inpOlList.item(i);
			 String olKey = ol.getAttribute("OrderLineKey");
			 logger.verbose("got order line key "+olKey);
			 // if order line key is blank, check for item id and pc
			 if(olKey == null || olKey.equals(""))
			 {
				 NodeList itemList = ol.getElementsByTagName("Item");
				 // no item tag and no order line key
				 // remove this element
				 if(itemList.getLength() == 0)
				 {
					 logger.verbose("removing the node as no order line key and no item tags "+ol);
					 lst.add(ol);
					 //ol.getParentNode().removeChild(ol);
					 continue;
				 }
				 else
				 {
					 logger.verbose("got item list "+itemList.getLength());
					 for(int j = 0 ; j < itemList.getLength() ; j++)
					 {
						 Element item = (Element)itemList.item(j);
						 String itemId = item.getAttribute("ItemID");
						 String pc = item.getAttribute("ProductClass");
						 logger.verbose("itemId "+itemId + " pc "+pc);
						 if(itemId == null || itemId.equals(""))
						 {
							 if(pc == null || pc.equals(""))
							 {
								 //remove this row as this doesnt have the 
								 // order line key nor item id not product class
								 logger.verbose("removing "+ol);
								 lst.add(ol);
								 //ol.getParentNode().removeChild(ol);
								 
							 }
						 }// end if itemid is null
					 }// end for item list
				 }
			 }// end if 
		 }
		 Iterator itr = lst.iterator();
		 while (itr.hasNext())
		 {
			 Element ele = (Element) itr.next();
			 ele.getParentNode().removeChild(ele);
			 itr.remove();
		 }
		 
		 logger.verbose("returning "+ XMLUtil.getXMLString(inDoc));
		 return inDoc;
		 
	}
	
	
		}
