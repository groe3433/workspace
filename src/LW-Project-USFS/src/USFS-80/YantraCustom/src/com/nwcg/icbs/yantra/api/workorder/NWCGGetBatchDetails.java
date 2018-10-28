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

public class NWCGGetBatchDetails implements YIFCustomApi{

	public Document getCustomBatchDetails(YFSEnvironment env, Document inDoc)
	throws Exception 
	{
		System.out.println("NWCGGetBatchDetails: getCustomBatchDetails: inDoc " + XMLUtil.getXMLString(inDoc));	
		Document outDoc = generateOutputDoc(env, inDoc);
		return outDoc;
	}

	public Document generateOutputDoc(YFSEnvironment env, Document inDoc)
	throws Exception
	{
		String workOrderNo = "";
		Document outDoc = CommonUtilities.invokeAPI(env,"getBatchDetails", inDoc);		
		Document workOrderDoc = getWorkOrderNo(env, outDoc);

		if(workOrderDoc != null)
		{
			Element workOrderElem = (Element) workOrderDoc.getDocumentElement().getElementsByTagName("WorkOrder").item(0);
			if(workOrderElem != null)
			{
				workOrderNo = workOrderElem.getAttribute("WorkOrderNo");
			}
		}

		Document finalOutDoc = XMLUtil.getDocument();
		Element batchElement = (Element) outDoc.getElementsByTagName("Batch").item(0);
		Element newBatchElement = finalOutDoc.createElement("Batch");
		XMLUtil.copyElement(finalOutDoc, batchElement, newBatchElement);
		finalOutDoc.appendChild(newBatchElement);		
		newBatchElement.setAttribute("WorkOrderNo", workOrderNo);

		System.out.println("NWCGGetBatchDetails: generateOutputDoc: finalOutDoc " + XMLUtil.getXMLString(finalOutDoc));		
		return finalOutDoc;
	}
	
	private Document getWorkOrderNo(YFSEnvironment env, Document inDoc)
	throws Exception
	{
		Element batchElement = (Element) inDoc.getElementsByTagName("Batch").item(0);		
		String moveRequestNo = batchElement.getAttribute("MoveRequestNo");
		String node = batchElement.getAttribute("Node");

		Document inDocForAPI = XMLUtil.newDocument();
		Element element = inDocForAPI.createElement("MoveRequest");
		element.setAttribute("MoveRequestNo", moveRequestNo);
		element.setAttribute("Node", node);
		inDocForAPI.appendChild(element);

		Document invOutDoc = CommonUtilities.invokeAPI(env, NWCGConstants.API_GET_MOVE_REQUEST_DETAILS, inDocForAPI);

		System.out.println("NWCGGetBatchDetails: getWorkOrderNo: invOutDoc " + invOutDoc);
		return invOutDoc;
	}

	public void setProperties(Properties arg0) throws Exception 
	{}
}