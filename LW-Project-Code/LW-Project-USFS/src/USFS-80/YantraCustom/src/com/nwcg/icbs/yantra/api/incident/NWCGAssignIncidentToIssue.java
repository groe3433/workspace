package com.nwcg.icbs.yantra.api.incident;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGAssignIncidentToIssue implements YIFCustomApi{

	/**
	 * Logger Instance.
	 */
	private static Logger logger = Logger.getLogger(NWCGAssignIncidentToIssue.class
			.getName());

	private Properties props = null;
	
	public void setProperties(Properties props) throws Exception {
		this.props = props;
	}

	public Document assignIncident(YFSEnvironment env, Document inDoc) throws Exception{
		
		logger.verbose("Entering assignIncident()");
		if(logger.isVerboseEnabled()){
			logger.verbose("assignIncident()->inDoc:"+XMLUtil.getXMLString(inDoc));
		}
		
		Document returnDoc = null;
		//1.Get the issue details to get the provios incident nos.
		String orderHeaderKey = inDoc.getDocumentElement().getAttribute("OrderHeaderKey");
		String incidentNo = 
			((Element)XMLUtil.getChildNodeByName(
					inDoc.getDocumentElement(),"Extn")).getAttribute("ExtnIncidentNo");
		
		logger.verbose("OrderHeaderKey="+orderHeaderKey+",IncidentNo="+incidentNo);
		Document getOrderDetailsInput = XMLUtil.createDocument("Order");
		getOrderDetailsInput.getDocumentElement().setAttribute("OrderHeaderKey",orderHeaderKey);
		
		Document orderDetailsDoc = CommonUtilities.invokeAPI(
				env,"NWCGAssignIncidentToIssue_getOrderDetails","getOrderDetails",getOrderDetailsInput);
		
		//	2.Create the changeOrder XML and set the new and old incident nos.
		Element orderExtnElem = (Element)XMLUtil.getChildNodeByName(orderDetailsDoc.getDocumentElement(),"Extn");
		orderExtnElem.setAttribute("ExtnLastIncidentNo2",orderExtnElem.getAttribute("ExtnLastIncidentNo1"));
		orderExtnElem.setAttribute("ExtnLastIncidentNo1",orderExtnElem.getAttribute("ExtnIncidentNo"));
		orderExtnElem.setAttribute("ExtnIncidentNo",incidentNo);

		if(logger.isVerboseEnabled()){
			logger.verbose("assignIncident()->Change Order Input:"+XMLUtil.getXMLString(orderDetailsDoc));
		}
		//3.Do a changeOrder and return
		returnDoc = CommonUtilities.invokeAPI(env,"changeOrder",orderDetailsDoc);
		if(logger.isVerboseEnabled()){
			logger.verbose("assignIncident()->returnDoc:"+XMLUtil.getXMLString(returnDoc));
		}
		logger.verbose("Exiting assignIncident()");
		return returnDoc;
	}
}
