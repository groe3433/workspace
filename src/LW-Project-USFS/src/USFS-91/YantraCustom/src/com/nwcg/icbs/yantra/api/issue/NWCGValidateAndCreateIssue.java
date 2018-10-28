/* Copyright 2010, Sterling Commerce, Inc. All rights reserved. */
/*
 LIMITATION OF LIABILITY
 THIS SOFTWARE SAMPLE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED 
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 IN NO EVENT SHALL STERLING COMMERCE, Inc. BE LIABLE UNDER ANY THEORY OF 
 LIABILITY (INCLUDING, BUT NOT LIMITED TO, BREACH OF CONTRACT, BREACH 
 OF WARRANTY, TORT, NEGLIGENCE, STRICT LIABILITY, OR ANY OTHER THEORY 
 OF LIABILITY) FOR (i) DIRECT DAMAGES OR INDIRECT, SPECIAL, INCIDENTAL, 
 OR CONSEQUENTIAL DAMAGES SUCH AS, BUT NOT LIMITED TO, EXEMPLARY OR 
 PUNITIVE DAMAGES, OR ANY OTHER SIMILAR DAMAGES, WHETHER OR NOT 
 FORESEEABLE AND WHETHER OR NOT STERLING OR ITS REPRESENTATIVES HAVE 
 BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES, OR (ii) ANY OTHER 
 CLAIM, DEMAND OR DAMAGES WHATSOEVER RESULTING FROM OR ARISING OUT OF
 OR IN CONNECTION THE DELIVERY OR USE OF THIS INFORMATION.
 */

package com.nwcg.icbs.yantra.api.issue;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * CR 610 
 * 1. This API is used to check if the entered incident/year is a invalid. 
 * 2. This API is used to check if there is a invalid customer id entered when an issue is created.
 * 
 * @author lightwell
 */
public class NWCGValidateAndCreateIssue implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGValidateAndCreateIssue.class);

	private Properties props = null;

	public void setProperties(Properties props) throws Exception {
		this.props = props;
	}
	
	/**
	 * main method to be invoked by framework checks for incident number
	 * assignment and any issue created against it if issue and incident
	 * assignment doesnt exists throw an exception
	 * 
	 * @param env
	 * @param inDoc
	 * @return
	 * @throws Exception
	 */
	public Document validateAndCreateIssue(YFSEnvironment env, Document inDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGValidateAndCreateIssue::validateAndCreateIssue @@@@@");
		Element orderNode = inDoc.getDocumentElement();

		// 1.validate the entered incidentNo/Year
		Element orderExtnElm = (Element) XMLUtil.getChildNodeByName(orderNode, "Extn");
		String extnIncidentNo = orderExtnElm.getAttribute("ExtnIncidentNo");
		String extnIncidentYear = orderExtnElm.getAttribute("ExtnIncidentYear");
		Document getIncidentDetailsInput = XMLUtil.createDocument("NWCGIncidentOrder");
		getIncidentDetailsInput.getDocumentElement().setAttribute("IncidentNo", extnIncidentNo);
		if (extnIncidentYear != null && (!extnIncidentYear.equals(""))) {
			// Set IncidentYear when year is passed - incident issue case
			getIncidentDetailsInput.getDocumentElement().setAttribute("Year", extnIncidentYear);
		} else {
			// Set IncidentYear with blank when year is NOT passed - Other issue case
			getIncidentDetailsInput.getDocumentElement().setAttribute("Year", " ");
		}
		// get the incident details
		Document incident = CommonUtilities.invokeService(env, NWCGConstants.NWCG_GET_INCIDENT_ORDER_SERVICE, getIncidentDetailsInput);
		if (incident == null) {
			throw new NWCGException("NWCG_INCIDENT_VALIDATE_001", new Object[] { extnIncidentNo, extnIncidentYear });
		}

		// 2.validate the entered customerID
		String CustomerId = orderNode.getAttribute("BillToID");
		Document CustDetailsInput = XMLUtil.createDocument("Customer");
		CustDetailsInput.getDocumentElement().setAttribute("CustomerID", CustomerId);
		Document CustomerList = null;
		if (CustomerId.length() > 0) {
			CustomerList = getCustDetails(env, CustDetailsInput);
		} else {
			throw new NWCGException("NWCG_CUSTOMER_VALIDATE_001", new Object[] { CustomerId });
		}
		NodeList CusList = CustomerList.getElementsByTagName("Customer");
		if (CusList.getLength() == 0) {
			throw new NWCGException("NWCG_CUSTOMER_VALIDATE_001", new Object[] { CustomerId });
		}

		logger.verbose("@@@@@ inDoc : " + XMLUtil.getXMLString(inDoc));
		Document CustIncidentDetails = CommonUtilities.invokeAPI(env, "createOrder", inDoc);
		logger.verbose("@@@@@ CustIncidentDetails : " + XMLUtil.getXMLString(CustIncidentDetails));

		// Begin CR846
		Element rootElemOrder = CustIncidentDetails.getDocumentElement();
		String strOrderHeaderKey = rootElemOrder.getAttribute("OrderHeaderKey");

		// instantiate and populate the changeOrder document
		Document changeOrderDocument = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
		Element rootElemChangeOrderDocument = changeOrderDocument.getDocumentElement();
		rootElemChangeOrderDocument.setAttribute(NWCGConstants.ORDER_HEADER_KEY, strOrderHeaderKey);
		rootElemChangeOrderDocument.setAttribute(NWCGConstants.ACTION, NWCGConstants.MODIFY);
		rootElemChangeOrderDocument.setAttribute(NWCGConstants.OVERRIDE, NWCGConstants.YES);
		rootElemChangeOrderDocument.setAttribute("ModificationReasonCode", "Miscellaneous");
		rootElemChangeOrderDocument.setAttribute("ModificationReasonText", "CR846 ICBS fill messages for Backorder and Forwardorder quantity changes made to IncidentIssueType Normal and IncidentReplenishment should be notified to Ross");
		rootElemChangeOrderDocument.setAttribute("ModificationReference1", "Time: " + CommonUtilities.getXMLCurrentTime());
		rootElemChangeOrderDocument.setAttribute("ModificationReference2", "Login ID: " + env.getUserId());

		// Append the OrderHeader Extn element and populate attributes
		Element extnElemChangeOrderDocument = changeOrderDocument.createElement(NWCGConstants.EXTN_ELEMENT);
		rootElemChangeOrderDocument.appendChild(extnElemChangeOrderDocument);
		extnElemChangeOrderDocument.setAttribute(NWCGConstants.EXTN_BASE_ORDER_HEADER_KEY, strOrderHeaderKey);

		try {
			Document outputTemplate = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
			Element extnElemOutputTemplate = outputTemplate.createElement(NWCGConstants.EXTN_ELEMENT);
			outputTemplate.getDocumentElement().appendChild(extnElemOutputTemplate);
			logger.verbose("@@@@@ changeOrderDocument : " + XMLUtil.getXMLString(changeOrderDocument));
			Document changeOrderOutputDocument = CommonUtilities.invokeAPI(env, outputTemplate, NWCGConstants.API_CHANGE_ORDER, changeOrderDocument);
			logger.verbose("@@@@@ changeOrderOutputDocument : " + XMLUtil.getXMLString(changeOrderOutputDocument));
		} catch (Exception ex) {
			logger.error("!!!!! Caught General Exception :: ERROR occured while updating the IncidentIssue column EXTN_BASE_ORDER_HEADER_KEY", ex);
			ex.printStackTrace();
		}

		logger.verbose("@@@@@ Exiting NWCGValidateAndCreateIssue::validateAndCreateIssue @@@@@");
		return CustIncidentDetails;
	}

	public Document getCustDetails(YFSEnvironment env, Document getCustDetailsInput) throws Exception {
		logger.verbose("@@@@@ Exiting NWCGValidateAndCreateIssue::getCustDetails @@@@@");
		Document CustDtls = null;
		try {
			CustDtls = XMLUtil.getDocument();
			CustDtls = CommonUtilities.invokeAPI(env, "getCustomerList", getCustDetailsInput);
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException : " + pce);
			pce.printStackTrace();
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);
			e.printStackTrace();
		}
		logger.verbose("@@@@@ Exiting NWCGValidateAndCreateIssue::getCustDetails @@@@@");
		return CustDtls;
	}
}
