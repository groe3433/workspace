package com.nwcg.icbs.yantra.api.cachetocache;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/*
 * author : jvishwakarma 
 */
public class NWCGUpdateAccountCodes implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGUpdateAccountCodes.class);
	private Properties props;
	
	public NWCGUpdateAccountCodes() {
		props = null;
	}

	public void setProperties(Properties props) throws Exception {
		this.props = props;
		logger.verbose("NWCGUpdateAccountCodes API Properties: "+ this.props.toString());
	}

	/*
	 * Fetches the account codes based on the node and the owner agency For ex.
	 * If the owner agency is FS the both the account codes are pulled up for FS
	 * account code of an incident
	 */

	public Document modifyAccountCodes(YFSEnvironment env, Document inDoc)
			throws Exception {
		if (logger.isVerboseEnabled()) {
			logger.verbose("Entering NWCGUpdateAccountCodesService");
			logger.verbose("modfiyAccountCodes() -> inDoc ="
					+ XMLUtil.getXMLString(inDoc));
		}

		String orgCode = inDoc.getDocumentElement().getAttribute(
				"OrganizationCode");
		String extnIncidentNo = inDoc.getDocumentElement().getAttribute(
				"ExtnIncidentNo");
		String extnIncidentYear = inDoc.getDocumentElement().getAttribute(
				"ExtnIncidentYear");

		if (logger.isVerboseEnabled())
			if (logger.isVerboseEnabled())
				logger.verbose("Incident No = " + extnIncidentNo
						+ " orgCode = " + orgCode);

		String IncidentFSAcctCode = null;
		String IncidentBlmAcctCode = null;
		String IncidentOtherAcctCode = null;
		String IncidentOverrideCode = null;
		Document getOrgListInXMLDoc = XMLUtil.createDocument("Organization");
		Element inDocRoot = getOrgListInXMLDoc.getDocumentElement();
		inDocRoot.setAttribute("OrganizationCode", orgCode);

		// Document getOrgListTemplate = XMLUtil.createDocument("Organization");
		// getOrgListTemplate.getDocumentElement().setAttribute("OrganizationKey",
		// "");
		// Element extn = getOrgListTemplate.createElement("Extn");
		// extn.setAttribute("ExtnAdjustAcctCode", "");
		// extn.setAttribute("ExtnRecvAcctCode", "");
		// extn.setAttribute("ExtnOwnerAgency", "");
		// extn.setAttribute("ExtnShipAcctCode", "");

		// getOrgListTemplate.getDocumentElement().appendChild(extn);
		Document getOrgOutDoc = null;
		// invoke the organization details
		if (orgCode != null && (!orgCode.equals(""))) {
			env.setApiTemplate("getOrganizationHierarchy",
					"NWCGUpdateAccountCodes_getOrganizationHierarchy");
			getOrgOutDoc = CommonUtilities.invokeAPI(env,
					"getOrganizationHierarchy", getOrgListInXMLDoc);
			env.clearApiTemplate("getOrganizationHierarchy");
		}

		Element orgExtn = null;
		String ownerAgency = "";
		if (getOrgOutDoc != null) {
			orgExtn = (Element) XMLUtil.getChildNodeByName(
					getOrgOutDoc.getDocumentElement(), "Extn");
			// get the cache owner agency
			ownerAgency = orgExtn.getAttribute("ExtnOwnerAgency");
		}

		// 10/31/07 - removed the extnIncidentYear so that other order case can
		// be processed
		// if((!StringUtil.isEmpty(extnIncidentNo)) &&
		// (!StringUtil.isEmpty(extnIncidentYear)))

		if (!StringUtil.isEmpty(extnIncidentNo)) {
			if (logger.isVerboseEnabled())
				logger.verbose("Incident Number is not EMPTY ");

			Document getIncidentDetailsInput = XMLUtil
					.createDocument("NWCGIncidentOrder");
			getIncidentDetailsInput.getDocumentElement().setAttribute(
					"IncidentNo", extnIncidentNo);

			// for other orders if incident year is blank or null set it to " "
			if (extnIncidentYear.equalsIgnoreCase("")) {
				extnIncidentYear = " ";
			}

			getIncidentDetailsInput.getDocumentElement().setAttribute("Year",
					extnIncidentYear);

			// get the incident details
			Document incident = CommonUtilities.invokeService(env,
					NWCGConstants.NWCG_GET_INCIDENT_ORDER_SERVICE,
					getIncidentDetailsInput);

			if (incident != null) {
				// Commented the following for CR-609 - GN
				/*
				 * // if owner agency is FS pull the fs account code from
				 * incident if(NWCGConstants.FS_OWNER_AGENY.equals(ownerAgency))
				 * { accountCode =
				 * incident.getDocumentElement().getAttribute("IncidentFsAcctCode"
				 * ); // only if the agency is FS, then capture override code
				 * overrideCode =
				 * incident.getDocumentElement().getAttribute("OverrideCode"); }
				 * // check for BLS else
				 * if(NWCGConstants.BLM_OWNER_AGENCT.equals(ownerAgency)) {
				 * accountCode =
				 * incident.getDocumentElement().getAttribute("IncidentBlmAcctCode"
				 * ); } // else assume its other account code else { accountCode
				 * = incident.getDocumentElement().getAttribute(
				 * "IncidentOtherAcctCode"); }
				 */

				IncidentFSAcctCode = incident.getDocumentElement()
						.getAttribute("IncidentFsAcctCode");
				IncidentOverrideCode = incident.getDocumentElement()
						.getAttribute("OverrideCode");
				IncidentBlmAcctCode = incident.getDocumentElement()
						.getAttribute("IncidentBlmAcctCode");
				IncidentOtherAcctCode = incident.getDocumentElement()
						.getAttribute("IncidentOtherAcctCode");

				// append the incident details - this will redue one over head
				// of api call
				if (getOrgOutDoc != null) {
					if (logger.isVerboseEnabled())
						logger.verbose("Incident Details  "
								+ XMLUtil.getXMLString(incident));
					Node incidentDtl = getOrgOutDoc.importNode(
							incident.getDocumentElement(), true);
					getOrgOutDoc.getDocumentElement().appendChild(incidentDtl);
				}
			}
			if (orgExtn != null) {
				// set the Incident account codes
				orgExtn.setAttribute("IncidentFSAcctCode", IncidentFSAcctCode);
				orgExtn.setAttribute("IncidentOverrideCode",
						IncidentOverrideCode);
				orgExtn.setAttribute("IncidentBlmAcctCode", IncidentBlmAcctCode);
				orgExtn.setAttribute("IncidentOtherAcctCode",
						IncidentOtherAcctCode);

				if (logger.isVerboseEnabled()) {
					logger.verbose("Printing IncidentAccountCodes");
					logger.verbose("Account codes ->" + IncidentFSAcctCode);
					logger.verbose("Account codes ->" + IncidentBlmAcctCode);
					logger.verbose("Account codes ->" + IncidentOtherAcctCode);
					logger.verbose("Override codes ->" + IncidentOverrideCode);
				}
			}
		}
		if (getOrgOutDoc != null)
			if (logger.isVerboseEnabled())
				logger.verbose("Exiting:" + XMLUtil.getXMLString(getOrgOutDoc));
		return getOrgOutDoc;
	}

}
