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

package com.nwcg.icbs.yantra.api.incident;

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
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;


/*
 * This class checks for the incident number 
 * Algorithm for incident deletion:
 * 1. Use will login to the console. 
 * 2. Navigates to Incident Details 
 * 3. Hits on Delete button 
 * 4. Framework invokes a custom api 
 * 5. API (Program) will check if the current incident (to be deleted) is assigned to any of the incidents 
 * as Last Incident # 1 (or 2)? 
 * 6. If true – program will move to next step – else program will throw an error message to user (console) 
 * 7. Program will check if there are any issues assigned to this incident number? 
 * 8. If true – program will throw an error message to user (console) – else will delete the Incident. 
 * 9. Stop
 */
public class NWCGValidateAndDeleteIncident implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGValidateAndDeleteIncident.class);

	private Properties props = null;

	public void setProperties(Properties props) throws Exception {
		this.props = props;
	}

	/*
	 * main method to be invoked by framework checks for incident number
	 * assignment and any issue created against it if issue and incident
	 * assignment doesnt exists deletes the incident
	 */
	public Document validateAndDeleteIncident(YFSEnvironment env, Document inDoc)
			throws Exception {
		if (logger.isVerboseEnabled())
			logger.verbose("validateAndDeleteIncident  "
					+ XMLUtil.getXMLString(inDoc));

		Element elemIncident = inDoc.getDocumentElement();
		String strIncidentNo = elemIncident.getAttribute("IncidentNo");
		if (logger.isVerboseEnabled())
			logger.verbose("validateAndDeleteIncident Incident Number = "
					+ strIncidentNo);
		// look out for all incidents where this incident number is assigned as
		// the ReplacedIncidentNo or ReplacedIncidentNo2
		// since yantra doesnt support the complex queries for custom tables, we
		// have to invoke the database twice
		// first we will search for ReplacedIncidentNo and then for
		// ReplacedIncidentNo2
		boolean bIncident1Exists = incidentNumberAssigned(env,
				"ReplacedIncidentNo", strIncidentNo);
		if (logger.isVerboseEnabled())
			logger.verbose("validateAndDeleteIncident bIncident1Exists = "
					+ bIncident1Exists);
		if (bIncident1Exists) {
			if (logger.isVerboseEnabled())
				logger.verbose("validateAndDeleteIncident throwing NWCG_INCIDENT_DELETE_003 ");
			throw new NWCGException("NWCG_INCIDENT_DELETE_003",
					new Object[] { strIncidentNo });
		}

		boolean bIncident2Exists = incidentNumberAssigned(env,
				"ReplacedIncidentNo2", strIncidentNo);
		if (logger.isVerboseEnabled())
			logger.verbose("validateAndDeleteIncident bIncident2Exists = "
					+ bIncident2Exists);
		if (bIncident2Exists) {
			if (logger.isVerboseEnabled())
				logger.verbose("validateAndDeleteIncident throwing NWCG_INCIDENT_DELETE_003 ");
			throw new NWCGException("NWCG_INCIDENT_DELETE_003",
					new Object[] { strIncidentNo });
		}

		/*
		 * boolean bIncident3Exists =
		 * incidentNumberAssigned(env,"ReplacedIncidentNo3",strIncidentNo);
		 * if(logger.isVerboseEnabled())
		 * logger.verbose("validateAndDeleteIncident bIncident3Exists = " +
		 * bIncident3Exists); if(bIncident3Exists) {
		 * if(logger.isVerboseEnabled()) logger.verbose(
		 * "validateAndDeleteIncident throwing NWCG_INCIDENT_DELETE_003 ");
		 * throw new NWCGException("NWCG_INCIDENT_DELETE_003",new Object[]
		 * {strIncidentNo}); }
		 */

		// now check for any issues

		boolean bIssueexists = issueExistsForIncident(env, strIncidentNo);
		if (bIssueexists) {
			if (logger.isVerboseEnabled())
				logger.verbose("validateAndDeleteIncident throwing NWCG_INCIDENT_DELETE_002 ");
			throw new NWCGException("NWCG_INCIDENT_DELETE_002",
					new Object[] { strIncidentNo });
		}
		if (logger.isVerboseEnabled())
			logger.verbose("validateAndDeleteIncident its all clear : now deleting the incident peacefully ");
		// else delete the incident
		CommonUtilities.invokeService(env,
				NWCGConstants.NWCG_DELETE_INCIDENT_ORDER_SERVICE, inDoc);
		if (logger.isVerboseEnabled())
			logger.verbose("Incident " + strIncidentNo
					+ "DELETED SUCESSFULLY throwing message for deletion");
		return XMLUtil.createDocument("Junk");
	}

	private boolean issueExistsForIncident(YFSEnvironment env,
			String strIncidentNo) throws Exception {
		if (logger.isVerboseEnabled())
			logger.verbose("issueExistsForIncident check if the issue exists for incident "
					+ strIncidentNo);
		Document docGetOrderListIP = getOrderListOP(strIncidentNo);
		if (logger.isVerboseEnabled())
			logger.verbose("issueExistsForIncident getOrderList input "
					+ XMLUtil.getXMLString(docGetOrderListIP));
		Document docGetOrderListOP = CommonUtilities.invokeAPI(env,
				"NWCGValidateAndDeleteIncident_getOrderList", "getOrderList",
				docGetOrderListIP);
		if (logger.isVerboseEnabled())
			logger.verbose("issueExistsForIncident getOrderList output "
					+ XMLUtil.getXMLString(docGetOrderListOP));
		if (docGetOrderListOP != null) {
			Element elemGetIncidentListOP = docGetOrderListOP
					.getDocumentElement();

			if (elemGetIncidentListOP != null) {
				NodeList nl = elemGetIncidentListOP
						.getElementsByTagName("Order");
				if (logger.isVerboseEnabled())
					logger.verbose("issueExistsForIncident nodelist " + nl);
				if (nl != null && nl.getLength() > 0)
					return true;
			}
		}
		return false;
	}

	private Document getOrderListOP(String strIncidentNo)
			throws ParserConfigurationException {
		Document doc = XMLUtil.createDocument("Order");
		Element elem = doc.getDocumentElement();
		elem.setAttribute("MaximumRecords", "1");
		Element elemExtn = doc.createElement("Extn");
		elemExtn.setAttribute("ExtnIncidentNo", strIncidentNo);
		elem.appendChild(elemExtn);

		return doc;
	}

	private boolean incidentNumberAssigned(YFSEnvironment env,
			String strAttribute, String strIncidentNo) throws Exception {
		if (logger.isVerboseEnabled())
			logger.verbose("incidentNumberAssigned checking if incident number assigned to any other incident ");
		Document docGetIncidentList = getIncidentListInput(strAttribute,
				strIncidentNo);
		if (logger.isVerboseEnabled())
			logger.verbose("incidentNumberAssigned docGetIncidentList "
					+ XMLUtil.getXMLString(docGetIncidentList));
		Document docGetIncidentListOP = CommonUtilities.invokeService(env,
				NWCGConstants.NWCG_GET_INCIDENT_ORDERLIST_SERVICE,
				docGetIncidentList);
		if (logger.isVerboseEnabled())
			logger.verbose("incidentNumberAssigned docGetIncidentListOP "
					+ XMLUtil.getXMLString(docGetIncidentListOP));
		if (docGetIncidentListOP != null) {
			Element elemGetIncidentListOP = docGetIncidentListOP
					.getDocumentElement();

			if (elemGetIncidentListOP != null) {
				NodeList nl = elemGetIncidentListOP
						.getElementsByTagName("NWCGIncidentOrder");
				if (logger.isVerboseEnabled())
					logger.verbose("incidentNumberAssigned nodelist " + nl);
				if (nl != null && nl.getLength() > 0)
					return true;
			}
		}
		return false;
	}

	private Document getIncidentListInput(String strAttribute,
			String strIncidentNo) throws ParserConfigurationException {
		Document returnDoc = XMLUtil.createDocument("NWCGIncidentOrder");
		Element elem = returnDoc.getDocumentElement();
		elem.setAttribute(strAttribute, strIncidentNo);

		return returnDoc;
	}

}
