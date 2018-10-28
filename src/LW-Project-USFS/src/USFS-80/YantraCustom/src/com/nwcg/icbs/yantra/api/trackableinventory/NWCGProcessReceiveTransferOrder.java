package com.nwcg.icbs.yantra.api.trackableinventory;

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
import com.yantra.yfs.japi.YFSEnvironment;
/*
 * author jvishwakarma
 * this class is used for posting the data in the custom trackable_item table
 * its just updates the statuscacheid - to record that transfer to transfer order
 * is received by the node. Invoked only for cache to cache transfer
 */
public class NWCGProcessReceiveTransferOrder implements YIFCustomApi,NWCGITrackableRecordMutator
{
	 private Properties _properties;
	    
	public void setProperties(Properties arg0) throws Exception {
		this._properties = arg0 ;
		// TODO Auto-generated method stub
		
	}
	public Document updateTrackableRecord(YFSEnvironment env, Document inXML) throws Exception
	{
		try
		{
			System.out.println("Receive TO Input XML: "+ XMLUtil.getXMLString(inXML));
			Element elemReceiptRoot = inXML.getDocumentElement();
			if(log.isVerboseEnabled()) log.verbose("NWCGProcessReceiveOrder::createTrackableRecord received the input document "+ XMLUtil.getXMLString(inXML));
			if(elemReceiptRoot != null)
			{
				// get the receipt line
				NodeList nlReceiptLines = elemReceiptRoot.getElementsByTagName("ReceiptLine");
				if(nlReceiptLines != null)
				{
					if(log.isVerboseEnabled()) log.verbose("NWCGProcessReceiveOrder:: total number of receiptlines = "+ nlReceiptLines.getLength());
					
					String strReceivngNode = elemReceiptRoot.getAttribute("ReceivingNode");
					
					// for all the receipt lines
					for(int index = 0 ; index < nlReceiptLines.getLength() ; index++ )
					{
						Element elemReceiptLine = (Element) nlReceiptLines.item(index);
						// check if the serial number is null
						String strSerialNo = elemReceiptLine.getAttribute("SerialNo");
						String strItemID = elemReceiptLine.getAttribute("ItemID");
						if(StringUtil.isEmpty(strSerialNo))
						{
							if(log.isVerboseEnabled()) log.verbose("NWCGProcessReceiveOrder:: Continuing as the serial number is empty");
							// if its null, continue with next record
							continue ;
						}
						
						if(log.isVerboseEnabled()) log.verbose("Passing the element as "+elemReceiptLine);
						Document docCreateTrackableInventoryIP = getTrackableInventoryIPDocument(env,elemReceiptLine);
						
						if(docCreateTrackableInventoryIP != null)
						{
							if(log.isVerboseEnabled()) log.verbose("NWCGProcessReceiveOrder:: got the trackable inv doc as "+ XMLUtil.getXMLString(docCreateTrackableInventoryIP));
							
							Element elemCreateTrackableInventoryIP = docCreateTrackableInventoryIP.getDocumentElement();
							// assign the other attributes
							elemCreateTrackableInventoryIP.setAttribute("StatusCacheID",strReceivngNode);
							elemCreateTrackableInventoryIP.setAttribute("SecondarySerial",CommonUtilities.getSecondarySerial(env,strSerialNo,strItemID));
							try
							{
								// update the record
								CommonUtilities.invokeService(env,NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE,docCreateTrackableInventoryIP);	
							}
							catch(Exception ex)
							{
								if(log.isVerboseEnabled()) log.verbose("Got Exception while createing a record - serial number might already exists in system - trying to update a record "+ex.getMessage());
								//if(log.isVerboseEnabled()) log.verbose(env,ex);
								ex.printStackTrace();
								
							}// end catch
						}// end if docCreateTrackableInventoryIP != null
					}// end while recipt lines
				}// end if receiptlines not null
				
			}
		}
		catch(Exception e)
		{
			if(log.isVerboseEnabled()) log.verbose("NWCGProcessReceiveOrder::createTrackableRecord Caught Exception ");
			e.printStackTrace();
			// comment this later on
			//throw e;
		}
			
		return inXML;
	}
	/*
	 * This method generates the input xml for creating the record
	 */
	private Document getTrackableInventoryIPDocument(YFSEnvironment env,Element elemReceiptLine) throws Exception {
		if(log.isVerboseEnabled()) log.verbose("getTrackableInventoryIPDocument::inXML ==> "+elemReceiptLine);
		Document returnDoc = XMLUtil.createDocument("NWCGTrackableItem");
		Element returnDocElem = returnDoc.getDocumentElement();
		if(log.isVerboseEnabled()) log.verbose("Serial No is == >> "+elemReceiptLine.getAttribute("SerialNo"));
		returnDocElem.setAttribute("SerialNo",StringUtil.nonNull(elemReceiptLine.getAttribute("SerialNo")));
		if(log.isVerboseEnabled()) log.verbose("getTrackableInventoryIPDocument :: returning "+XMLUtil.getXMLString(returnDoc));
		return returnDoc;
	}

	private static Logger log = Logger.getLogger(NWCGProcessReceiveTransferOrder.class.getName());

	public Document insertOrUpdateTrackableRecord(YFSEnvironment env, Document doc) throws Exception {
		// TODO Auto-generated method stub
		return updateTrackableRecord(env,doc);
	}
}
