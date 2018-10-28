package com.nwcg.icbs.yantra.api.incident;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGCheckForIssuesIncidentDelete implements YIFCustomApi {

	/**
	 * Logger Instance.
	 */
	private static Logger logger = Logger
			.getLogger(NWCGCheckForIssuesIncidentDelete.class.getName());

	private Properties props = null;

	public void setProperties(Properties props) throws Exception {
		this.props = props;
	}

	/*
	 * This is a wrapper for incident deletion check if there are any issues
	 * associated with the incident throws an NWCGException
	 */
	public Document checkForIssuesIncidentDelete(YFSEnvironment env,
			Document inDoc) throws Exception {
		
		logger.verbose("Entering checkForIssuesIncidentDelete()");
		if (logger.isVerboseEnabled()) {
			logger.verbose("checkForIssuesIncidentDelete()->inDoc:"
					+ XMLUtil.getXMLString(inDoc));
		}

		Document returnDoc = inDoc;
		// get the incident no.
		String incidentNo = inDoc.getDocumentElement().getAttribute(
				"IncidentNo");

		// 1.Get the Issue List with the given IncidentNo
		Document getOrderListTemplate = XMLUtil.createDocument("OrderList");
		Element orderList = getOrderListTemplate.getDocumentElement();
		orderList.setAttribute(
				"TotalOrderList", "");
		Element order = getOrderListTemplate.createElement("Order");
		order.setAttribute("OrderHeaderKey", "");
		order.setAttribute("OrderNo", "");
		orderList.appendChild(order);
		// set the template
		env.setApiTemplate("getOrderList", getOrderListTemplate);

		Document getOrderLineListInput = XMLUtil.createDocument("Order");
		Element orderExtn = getOrderLineListInput.createElement("Extn");
		orderExtn.setAttribute("ExtnIncidentNo", incidentNo);
		getOrderLineListInput.getDocumentElement().appendChild(orderExtn);
		Document orderListDoc = CommonUtilities.invokeAPI(env, "getOrderList",
				getOrderLineListInput);
		// clear template
		env.clearApiTemplate("getOrderList");

		String totalOrderList = orderListDoc.getDocumentElement().getAttribute(
				"TotalOrderList");

		NodeList orders = orderListDoc.getDocumentElement()
				.getElementsByTagName("Order");

		// Check if order count is +ve throw exception
		if (!StringUtil.isEmpty(totalOrderList)
				&& Integer.parseInt(totalOrderList) > 0) {
			// Throw exception
			NWCGException e = new NWCGException("NWCG_INCIDENT_DELETE_001",
					new String[] { incidentNo, totalOrderList,
							getOrderNoString(orders) });
			throw e;
		}
		// Call delete Incident service
		// Document deleteIncidentDoc =
		// XMLUtil.createDocument("NWCGIncidentOrder");
		// deleteIncidentDoc.getDocumentElement().setAttribute("IncidentNo",incidentNo);
		// returnDoc =
		// CommonUtilities.invokeService(env,"NWCGDeleteIncidentOrderService",inDoc);
		if (logger.isVerboseEnabled()) {
			logger.verbose("checkForIssuesIncidentDelete()->returnDoc:"
					+ XMLUtil.getXMLString(returnDoc));
		}
		logger.verbose("Exiting checkForIssuesIncidentDelete()");
		return returnDoc;
	}
	/*
	 * Get the order no list as a string
	 */
	private String getOrderNoString(NodeList orders) {
		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < orders.getLength(); i++) {
			Element o = (Element) orders.item(i);
			buf.append("," + o.getAttribute("OrderNo"));
		}

		return buf.toString();
	}
}
