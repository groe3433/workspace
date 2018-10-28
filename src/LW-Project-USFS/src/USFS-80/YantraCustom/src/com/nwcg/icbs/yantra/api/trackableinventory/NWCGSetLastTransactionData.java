package com.nwcg.icbs.yantra.api.trackableinventory;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
/*
 * author jvishwakarma
 * 
 */
public class NWCGSetLastTransactionData implements YIFCustomApi,NWCGITrackableRecordMutator
{
	 private Properties _properties;
	    
	public void setProperties(Properties arg0) throws Exception {
		this._properties = arg0 ;
		// TODO Auto-generated method stub
		
	}
	/*
	 * this method sets current status attributes like order number, year and customer id to the the last transaction
	 * bascially pushing the current status to the last the transaction  
	 */
	public Document updateTrackableRecord(YFSEnvironment env, Document inXML) throws Exception
	{
		try
		{
			if(log.isVerboseEnabled()) log.verbose("NWCGSetLastTransactionData::updateTrackableRecord in XML" + XMLUtil.getXMLString(inXML));
			Element elemTrackableItem = inXML.getDocumentElement();
			
			if(elemTrackableItem != null)
			{
				//get the current status for the serial record
				Document docTrackableInv = XMLUtil.createDocument("NWCGTrackableItem"); 
				Element elemTrackableInv = docTrackableInv.getDocumentElement();
				elemTrackableInv.setAttribute("SerialNo",elemTrackableItem.getAttribute("SerialNo"));

				Document docTrackableInvOP = CommonUtilities.invokeService(env,ResourceUtil.get("nwcg.icbs.gettrackableitemlist.service"),docTrackableInv);
				Element elemCurrentTrackableItem = docTrackableInvOP.getDocumentElement();
				
				NodeList nlTrkItem = elemCurrentTrackableItem.getElementsByTagName("NWCGTrackableItem");
				
				if(nlTrkItem != null && nlTrkItem.getLength() > 0)
				{
					// should return one and only one record or no records
					elemCurrentTrackableItem = (Element) nlTrkItem.item(0);
					if(elemCurrentTrackableItem != null)
					{
						String strStatusIncidentNo = StringUtil.nonNull(elemCurrentTrackableItem.getAttribute("StatusIncidentNo"));
						String strStatusIncidentYear = StringUtil.nonNull(elemCurrentTrackableItem.getAttribute("StatusIncidentYear"));
						String strStatusBuyerOrganizationCode = StringUtil.nonNull(elemCurrentTrackableItem.getAttribute("StatusBuyerOrganizationCode"));
						
						elemTrackableItem.setAttribute("LastIncidentNo",strStatusIncidentNo);
						elemTrackableItem.setAttribute("LastIncidentYear",strStatusIncidentYear);
						elemTrackableItem.setAttribute("LastBuyerOrganizationCode",strStatusBuyerOrganizationCode);
					}
				}
			}
		}
		catch(Exception e)
		{
			if(log.isVerboseEnabled())
				log.verbose("NWCGSetLastTransactionData::updateTrackableRecord Caught Exception ");
			e.printStackTrace();
			// comment this later on
			//throw e;
		}
		if(log.isVerboseEnabled()) log.verbose("NWCGSetLastTransactionData::updateTrackableRecord return XML" + XMLUtil.getXMLString(inXML));
		return inXML;
	}
	

	private static Logger log = Logger.getLogger(NWCGSetLastTransactionData.class.getName());

	public Document insertOrUpdateTrackableRecord(YFSEnvironment env, Document doc) throws Exception {
		// TODO Auto-generated method stub
		return updateTrackableRecord(env,doc);
	}
}
