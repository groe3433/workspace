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

public class NWCGPrintInputHandler implements YIFCustomApi{

	public Document customizeInputForPrinting(YFSEnvironment env, Document inDoc)
	throws Exception 
	{		
		System.out.println("NWCGPrintInputHandler: customizeInputForPrinting: inDoc " + XMLUtil.getXMLString(inDoc));
		return customizedInput(env,inDoc);
	}

	private Document customizedInput(YFSEnvironment env, Document inDoc)
	throws Exception 
	{
		Document modifiedDoc = XMLUtil.getDocument();
		Element batchElement = (Element) inDoc.getElementsByTagName("Batch").item(0);
		Element newBatchElement = modifiedDoc.createElement("Batch");
		XMLUtil.copyElement(modifiedDoc, batchElement, newBatchElement);
		modifiedDoc.appendChild(newBatchElement);		
		newBatchElement.setAttribute("DocType", getDocumentType(env, inDoc));

		System.out.println("NWCGPrintInputHandler: customizedInput: modifiedDoc " + XMLUtil.getXMLString(modifiedDoc));
		return modifiedDoc;
	}	

	private String getDocumentType(YFSEnvironment env, Document inDoc)
	throws Exception 
	{
		String documentType = null;
		String shipmentKey = null;
		String workOrderKey = null;

		Element batchElement = (Element) inDoc.getElementsByTagName("Batch").item(0);		
		String node = batchElement.getAttribute("Node");
		String moveRequestNo = getMoveRequestNo(env, inDoc);
		
		Document moveRequestDoc = getMoveRequestDetails(env, moveRequestNo, node);
		Element moveReqElement = (Element) moveRequestDoc.getDocumentElement();		
		shipmentKey = moveReqElement.getAttribute("ShipmentKey");
		workOrderKey = moveReqElement.getAttribute("WorkOrderKey");
		
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
		return documentType;
	}	

	private String getMoveRequestNo(YFSEnvironment env, Document inDoc)
	throws Exception
	{
		String moveRequestNo = null;
		Element batchElement = (Element) inDoc.getElementsByTagName("Batch").item(0);		
		String batchNo = batchElement.getAttribute("BatchNo");

		Document opTemplate = XMLUtil.getDocument("<Batch MoveRequestNo=\"\" />");
		Document ipDoc = XMLUtil.getDocument("<Batch BatchNo=\"" + batchNo + "\" />");

		if(batchNo != null && !batchNo.trim().equals(""))
		{
			Document outDoc = CommonUtilities.invokeAPI(env, opTemplate, "getBatchDetails", ipDoc);
			Element rootElement = (Element) outDoc.getDocumentElement();
			moveRequestNo = rootElement.getAttribute("MoveRequestNo");
		}

		return moveRequestNo;
	}

	
	private Document getMoveRequestDetails(YFSEnvironment env, String moveRequestNo, String node)
	throws Exception
	{
		Document opTemplate = XMLUtil.getDocument("<MoveRequest ShipmentKey=\"\" WorkOrderKey=\"\" />");
		Document ipDoc = XMLUtil.getDocument("<MoveRequest MoveRequestNo=\"" + moveRequestNo + "\" Node=\"" + node + "\"/>");
		return CommonUtilities.invokeAPI(env, opTemplate, "getMoveRequestDetails", ipDoc);
	}

	public void setProperties(Properties arg0) throws Exception 
	{}
}