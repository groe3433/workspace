package com.nwcg.icbs.yantra.api.workorder;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.reports.loftware.NWCGReportsUtil;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGGetMoveRequestDetails implements YIFCustomApi{

	public Document getCustomMoveRequestDetails(YFSEnvironment env, Document inDoc)
	throws Exception 
	{		
		//System.out.println("NWCGGetMoveRequestDetails: getCustomMoveRequestDetails: inDoc " + XMLUtil.getXMLString(inDoc));
		return modifiedMoveRequest(env,inDoc);
	}
	
	private Document modifiedMoveRequest(YFSEnvironment env, Document inDoc)
	throws Exception 
	{
		Document modifiedMoveRequestDoc = XMLUtil.getDocument();
		Element moveRequestElement = (Element) inDoc.getElementsByTagName("MoveRequest").item(0);
		Element newMoveRequestElement = modifiedMoveRequestDoc.createElement("MoveRequest");
		XMLUtil.copyElement(modifiedMoveRequestDoc, moveRequestElement, newMoveRequestElement);
		modifiedMoveRequestDoc.appendChild(newMoveRequestElement);		
		newMoveRequestElement.setAttribute("DocType", getDocumentType(env, inDoc));

		//System.out.println("NWCGGetMoveRequestDetails: modifiedMoveRequest: modifiedMoveRequestDoc " + XMLUtil.getXMLString(modifiedMoveRequestDoc));
		return modifiedMoveRequestDoc;
	}	
	
	private String getDocumentType(YFSEnvironment env, Document inDoc)
	throws Exception 
	{
		String documentType = null;
		Element moveRequestElement = (Element) inDoc.getElementsByTagName("MoveRequest").item(0);		
//		String moveRequestKey = moveRequestElement.getAttribute("MoveRequestKey");
		String workOrderKey = moveRequestElement.getAttribute("WorkOrderKey");
		String shipmentKey = moveRequestElement.getAttribute("ShipmentKey");
		
		if(workOrderKey != null && !workOrderKey.trim().equals(""))
		{
			Document outDoc = CommonUtilities.invokeAPI(env, NWCGConstants.API_GET_WORK_ORDER_DTLS, XMLUtil.getDocument("<WorkOrder WorkOrderKey=\"" + workOrderKey + "\" />"));
			Element workOrderElem = (Element) outDoc.getElementsByTagName("WorkOrder").item(0);
			if(workOrderElem != null)
			{
				documentType = workOrderElem.getAttribute("DocumentType");
			}
		}
		else if(shipmentKey != null && !shipmentKey.trim().equals(""))
		{
			Document outDoc = CommonUtilities.invokeAPI(env, NWCGConstants.API_GET_SHIPMENT_DETAILS , XMLUtil.getDocument("<Shipment ShipmentKey=\"" + shipmentKey + "\" />"));
			Element shipmentElem = (Element) outDoc.getElementsByTagName("Shipment").item(0);
			if(shipmentElem != null)
			{
				documentType = shipmentElem.getAttribute("DocumentType");
			}
		}		

		//System.out.println("NWCGGetMoveRequestDetails: getDocumentType: documentType " + documentType);
		return documentType;
	}	
	
	public void setProperties(Properties arg0) throws Exception 
	{}
}