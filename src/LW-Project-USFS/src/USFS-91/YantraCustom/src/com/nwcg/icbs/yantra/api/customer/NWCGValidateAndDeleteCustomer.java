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

package com.nwcg.icbs.yantra.api.customer;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/*
 */
public class NWCGValidateAndDeleteCustomer implements YIFCustomApi {
	
	private static YFCLogCategory log = NWCGApplicationLogger.instance(NWCGValidateAndDeleteCustomer.class);

	private Properties props = null;

	public void setProperties(Properties props) throws Exception {
		this.props = props;
		log.verbose("NWCGValidateAndDeleteCustomer API Properties: "
				+ this.props.toString());
	}

	/*
	 * main method to be invoked by framework checks for incident number
	 * assignment and any issue created against it if issue and incident
	 * assignment doesnt exists deletes the incident
	 */
	public Document validateAndDeleteCustomer(YFSEnvironment env, Document inDoc)
			throws Exception {

		if (log.isVerboseEnabled())
			log.verbose("validateAndDeleteIncident  "
					+ XMLUtil.getXMLString(inDoc));

		Element elemCustomer = inDoc.getDocumentElement();
		String CustomerKey = elemCustomer.getAttribute("CustomerKey");

		Document CustDetailsInput = XMLUtil.createDocument("Customer");
		CustDetailsInput.getDocumentElement().setAttribute("CustomerKey",
				CustomerKey);

		Document CustomerDetails = getCustDetails(env, CustDetailsInput);

		Element CustDtlsElm = (Element) CustomerDetails.getElementsByTagName(
				"Customer").item(0);
		String CustomerID = CustDtlsElm.getAttribute("CustomerID");

		Element CustDtlsExtnElm = (Element) CustomerDetails
				.getElementsByTagName("Extn").item(0);
		String CustomerActiveFlag = CustDtlsExtnElm
				.getAttribute("ExtnActiveFlag");

		// Checking for Open Issues
		Document IssueDetails = getCheckIssueDetails(env, CustomerID);

		// Checking for Active Incidents
		Document IncidentDetails = getIncidentDetails(env, CustomerID);
		NodeList IncidentList = IncidentDetails
				.getElementsByTagName("NWCGIncidentOrder");

		if (IncidentList.getLength() > 0) {
			if (log.isVerboseEnabled())
				log.verbose("validateAndDeleteCustomer throwing NWCG_CUSTOMER_DELETE_002");
			throw new NWCGException("NWCG_CUSTOMER_DELETE_002", new Object[] {
					CustomerID, Integer.toString(IncidentList.getLength()) });
		}

		// Checking Active Customer
		if (CustomerActiveFlag == "Y") {
			if (log.isVerboseEnabled())
				log.verbose("validateAndDeleteCustomer throwing NWCG_CUSTOMER_DELETE_003");
			throw new NWCGException("NWCG_CUSTOMER_DELETE_003",
					new Object[] { CustomerID });
		}

		// Delete Customer
		Document DelCustomer = deleteCustomer(env, CustDetailsInput);

		return XMLUtil.createDocument("CustomerDelete");
	}

	public Document getCustDetails(YFSEnvironment env,
			Document getCustDetailsInput) throws Exception {
		Document CustDtls = null;
		try {
			CustDtls = XMLUtil.getDocument();
			CustDtls = CommonUtilities.invokeAPI(env, "getCustomerDetails",
					getCustDetailsInput);
		} catch (ParserConfigurationException pce) {
			log.error("!!!!! Caught ParserConfigurationException, NWCGValidateAndDeleteCustomer::getCustDetails, ParserConfigurationException Msg : "
					+ pce.getMessage());
			log.error("!!!!! Caught ParserConfigurationException, NWCGValidateAndDeleteCustomer::getCustDetails, StackTrace : "
					+ pce.getStackTrace());
		} catch (Exception e) {
			log.error("!!!!! Caught General Exception, NWCGValidateAndDeleteCustomer::getCustDetails, Exception Msg : "
					+ e.getMessage());
			log.error("!!!!! Caught General Exception, NWCGValidateAndDeleteCustomer::getCustDetails, StackTrace : "
					+ e.getStackTrace());
		}
		return CustDtls;
	}

	public Document getIncidentDetails(YFSEnvironment env, String CustomerId)
			throws Exception {
		Document IncidentDtls = null;
		Document IncidentDetailsInput = XMLUtil
				.createDocument("NWCGIncidentOrder");
		IncidentDetailsInput.getDocumentElement().setAttribute("CustomerId",
				CustomerId);
		IncidentDetailsInput.getDocumentElement().setAttribute("IsActive", "Y");
		try {
			IncidentDtls = XMLUtil.getDocument();
			IncidentDtls = CommonUtilities.invokeService(env,
					"NWCGGetIncidentOrderListService", IncidentDetailsInput);
		} catch (ParserConfigurationException pce) {
			log.error("!!!!! Caught ParserConfigurationException, NWCGValidateAndDeleteCustomer::getIncidentDetails, ParserConfigurationException Msg : "
					+ pce.getMessage());
			log.error("!!!!! Caught ParserConfigurationException, NWCGValidateAndDeleteCustomer::getIncidentDetails, StackTrace : "
					+ pce.getStackTrace());
		} catch (Exception e) {
			log.error("!!!!! Caught General Exception, NWCGValidateAndDeleteCustomer::getIncidentDetails, Exception Msg : "
					+ e.getMessage());
			log.error("!!!!! Caught General Exception, NWCGValidateAndDeleteCustomer::getIncidentDetails, StackTrace : "
					+ e.getStackTrace());
		}
		return IncidentDtls;
	}

	public Document getCheckIssueDetails(YFSEnvironment env, String CustomerId)
			throws Exception {
		Document IssueDtls = null;
		Document IssueDetailsInput = XMLUtil.createDocument("Order");
		IssueDetailsInput.getDocumentElement().setAttribute("BillToID",
				CustomerId);
		try {
			IssueDtls = XMLUtil.getDocument();
			IssueDtls = CommonUtilities.invokeAPI(env, "getOrderList",
					IssueDetailsInput);
		} catch (ParserConfigurationException pce) {
			log.error("!!!!! Caught ParserConfigurationException, NWCGValidateAndDeleteCustomer::getIssueDetails, ParserConfigurationException Msg : "
					+ pce.getMessage());
			log.error("!!!!! Caught ParserConfigurationException, NWCGValidateAndDeleteCustomer::getIssueDetails, StackTrace : "
					+ pce.getStackTrace());
		} catch (Exception e) {
			log.error("!!!!! Caught General Exception, NWCGValidateAndDeleteCustomer::getIssueDetails, Exception Msg : "
					+ e.getMessage());
			log.error("!!!!! Caught General Exception, NWCGValidateAndDeleteCustomer::getIssueDetails, StackTrace : "
					+ e.getStackTrace());
		}
		if (IssueDtls != null) {
			NodeList IssueList = IssueDtls.getElementsByTagName("Order");
			if (IssueList != null) {
				for (int i = 0; i < IssueList.getLength(); i++) {
					Element IssueStatusElm = (Element) IssueList.item(i);
					String IssueStatus = IssueStatusElm.getAttribute("Status");
					String IssueNo = IssueStatusElm.getAttribute("OrderNo");

					if (!IssueStatus.equals("Cancelled")) {
						if (log.isVerboseEnabled())
							log.verbose("validateAndDeleteCustomer throwing NWCG_CUSTOMER_DELETE_001");
						throw new NWCGException("NWCG_CUSTOMER_DELETE_001",
								new Object[] { CustomerId });
					}
				}
			}
		}
		return IssueDtls;
	}

	public Document deleteCustomer(YFSEnvironment env, Document CustDetailsInput)
			throws Exception {
		Document CustDelete = null;
		CustDelete = XMLUtil.getDocument();
		CustDelete = CommonUtilities.invokeAPI(env, "deleteCustomer",
				CustDetailsInput);

		return CustDelete;
	}

}
