package com.nwcg.icbs.yantra.api.workorder;

import java.util.ArrayList;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGGetShipmentDetails implements YIFCustomApi 
{
	private static Logger log = Logger.getLogger(NWCGGetShipmentDetails.class.getName());

	public void setProperties(Properties arg0) throws Exception {
	}

	public Document getCustomShipmentDetails(YFSEnvironment env, Document inputDoc)
	throws Exception 
	{
		log.verbose("NWCGGetShipmentDetails::getCustomShipmentDetails, Entered");
		System.out.println("NWCGGetShipmentDetails::getCustomShipmentDetails, Input Doc : " + XMLUtil.getXMLString(inputDoc));
		if (log.isVerboseEnabled()){
			log.verbose("NWCGGetShipmentDetails::getCustomShipmentDetails, Input Doc : " + XMLUtil.getXMLString(inputDoc));
		}

		Document outDoc =  CommonUtilities.invokeAPI(env, "getShipmentDetails", inputDoc);

		log.verbose("NWCGGetShipmentDetails::getCustomShipmentDetails, Returning");
		System.out.println("NWCGGetShipmentDetails::getCustomShipmentDetails, Output Doc : " + XMLUtil.getXMLString(outDoc));

		return generateOutputDoc(env, outDoc);
	}

	private Document generateOutputDoc(YFSEnvironment env, Document inputDoc)
	throws Exception
	{
		Element rootElement =  inputDoc.getDocumentElement();
		String cacheId = rootElement.getAttribute("ShipNode");
		String orderNo = ((Element)inputDoc.getElementsByTagName("ShipmentLine").item(0)).getAttribute("OrderNo");
		Element extnElement = (Element)inputDoc.getElementsByTagName("Extn").item(0);
		String incidentNo = extnElement.getAttribute("ExtnIncidentNum");
		String incidentYear = extnElement.getAttribute("ExtnYear");

		Document ipDoc = XMLUtil.getDocument();
		Element ipDocRootElement = ipDoc.createElement("NWCGIssueTrackableList");
		ipDocRootElement.setAttribute("IssueNo", orderNo);
		ipDocRootElement.setAttribute("IncidentNo", incidentNo);
		ipDocRootElement.setAttribute("IncidentYear", incidentYear);
		ipDocRootElement.setAttribute("CacheId", cacheId);
		ipDoc.appendChild(ipDocRootElement);

		Document opDoc = CommonUtilities.invokeService(env, "NWCGGetIssueTrackableListService", ipDoc);

		System.out.println("NWCGGetShipmentDetails::generateOutputDoc: opDoc \n" + XMLUtil.getXMLString(opDoc));
		
		NodeList nList = opDoc.getElementsByTagName("NWCGIssueTrackableList");
		
		Document outDoc = XMLUtil.newDocument();
		Element shipDetailsRootElement = outDoc.createElement("ShipmentDetails");
		outDoc.appendChild(shipDetailsRootElement);		

		for(int j = 0; j < nList.getLength(); j++)
		{
			Element listElement = (Element)nList.item(j);
			
			Element shipLineElement = outDoc.createElement("ShipmentLine");
			shipLineElement.setAttribute("SerialNo", listElement.getAttribute("SerialNo"));
			shipLineElement.setAttribute("ItemID", listElement.getAttribute("ItemId"));
			shipLineElement.setAttribute("OrderNo", listElement.getAttribute("IssueNo"));

			shipDetailsRootElement.appendChild(shipLineElement);
		}
		return outDoc;	
	}
}
