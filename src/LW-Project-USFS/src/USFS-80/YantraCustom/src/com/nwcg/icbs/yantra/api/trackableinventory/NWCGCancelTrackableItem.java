package com.nwcg.icbs.yantra.api.trackableinventory;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGCancelTrackableItem implements YIFCustomApi{
	
	public void setProperties(Properties arg0) throws Exception {
	}
	
	public Document cancelTrackableItem(YFSEnvironment env, Document inDoc)
	throws Exception {
		
		System.out.println("NWCGCancelTrackableItem: cancelTrackableItem: inDoc " + XMLUtil.getXMLString(inDoc));
		updateSerialStatus(env, inDoc);
		return inDoc;
	}

	private Document updateSerialStatus(YFSEnvironment env, Document inDoc)
	throws Exception
	{
		Element rootElement = inDoc.getDocumentElement();

		Document inDocForService = XMLUtil.getDocument();
		Element inDocrootElement = inDocForService.createElement("NWCGTrackableItem");
		inDocrootElement.setAttribute("TrackableItemKey", rootElement.getAttribute("TrackableItemKey"));
		inDocrootElement.setAttribute("SerialStatusDesc", getSerialStatusDesciption(rootElement.getAttribute("ReasonForCancellation")));
		inDocrootElement.setAttribute("SerialStatus", rootElement.getAttribute("ReasonForCancellation"));
		inDocForService.appendChild(inDocrootElement);
		
		Document outDoc = CommonUtilities.invokeService(env, "NWCGChangeTrackableInventoryRecordService", inDocForService);
		System.out.println("NWCGCancelTrackableItem: updateSerialStatus: outDoc : " + XMLUtil.getXMLString(outDoc));		
		return outDoc;
	}
	
	private String getSerialStatusDesciption(String serialStatus)
	{
		String serialStatusDesc = null;
		if(serialStatus.equals("L")) serialStatusDesc = "Lost/Stolen";
		else if(serialStatus.equals("X")) serialStatusDesc = "Destroyed";
		else if(serialStatus.equals("R")) serialStatusDesc = "Transferred";
		else if(serialStatus.equals("E")) serialStatusDesc = "Issued";
		else if(serialStatus.equals("O")) serialStatusDesc = "Other";
		return serialStatusDesc;		
	}
}