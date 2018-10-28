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

package com.nwcg.icbs.yantra.handler;

import gov.nwcg.services.ross.common_types._1.AddressType;
import gov.nwcg.services.ross.common_types._1.CacheIssueCreateType;
import gov.nwcg.services.ross.common_types._1.CatalogID;
import gov.nwcg.services.ross.common_types._1.CatalogItemNaturalKeyType;
import gov.nwcg.services.ross.common_types._1.CatalogItemType;
import gov.nwcg.services.ross.common_types._1.CompositeIncidentKeyType;
import gov.nwcg.services.ross.common_types._1.FinancialCodeType;
import gov.nwcg.services.ross.common_types._1.IDType;
import gov.nwcg.services.ross.common_types._1.IncidentDetailsReturnType;
import gov.nwcg.services.ross.common_types._1.IncidentNumberType;
import gov.nwcg.services.ross.common_types._1.IncidentReturnType;
import gov.nwcg.services.ross.common_types._1.PlaceCacheIssueCreateType;
import gov.nwcg.services.ross.common_types._1.RequestCodeType;
import gov.nwcg.services.ross.common_types._1.ResourceRequestCreateType;
import gov.nwcg.services.ross.common_types._1.ResponseMessageType;
import gov.nwcg.services.ross.common_types._1.ResponseStatusType;
import gov.nwcg.services.ross.common_types._1.ShippingInstructionsCreateType;
import gov.nwcg.services.ross.common_types._1.UnitIDType;
import gov.nwcg.services.ross.common_types._1.WillPickUpInformationType;
import gov.nwcg.services.ross.resource_order._1.DeliverOperationResultsResp;
import gov.nwcg.services.ross.resource_order._1.PlaceResourceRequestExternalReq;
import gov.nwcg.services.ross.resource_order._1.PlaceResourceRequestExternalResp;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathWrapper;
import com.nwcg.icbs.yantra.webservice.ib.msg.NWCGDeliverOperationResultsIB;
import com.nwcg.icbs.yantra.webservice.util.NWCGJAXBContextWrapper;
import com.yantra.yfs.japi.YFSConnectionHolder;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;

/**
 * Class invoked by ICBS A&A for handling PlaceResourceRequestExternalReq async
 * messages inbound from the ROSS system via the enterprise service bus (ESB).
 * One NWCGPlaceResourceRequestExternalHandler object instantiated for each
 * incoming PlaceResourceRequestExternalReq message.
 * 
 * @author drodriguez
 * @since USFS ICBS-ROSS Interface Increment 3
 * 
 * Modifications for 9.1:
 * 	- Line 894-895 - revised the if statement so it did not try to use getLocalName() as this was throwing a NullPointerException
 *  - Line 247-248 - revised the if statement so it did not try to use getLocalName() as this was throwing a NullPointerException
 * 	- Line 948 - commented this as it was giving an error as to the namespace being created/modified improperly
 *  - Line 953-954 - added these lines to set the namespace on the "ron:Incident" element due to a SAX Parse Exception in the XSL transform. 
 *  
 * @revisions lightwell
 * @version 1.2
 */
public class NWCGPlaceResourceRequestExternalHandler implements com.nwcg.icbs.yantra.ib.read.handler.NWCGMessageHandlerInterface {

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGPlaceResourceRequestExternalHandler.class);

	private static final String className = NWCGPlaceResourceRequestExternalHandler.class.getName();
	private YFSEnvironment myEnvironment;
	private HashMap<String, String> localContext = new HashMap<String, String>();
	private HashMap<String, Vector> ordersToCreate = new HashMap<String, Vector>();
	private Vector<Document> ordersCreated = new Vector<Document>();
	private HashMap<String, String> orderNoToMapKey = new HashMap<String, String>();
	private boolean incidentAlreadyExists = false;
	private boolean hasNewIncidentInboxKeySet = false;
	private boolean incidentJustNowCreated = false;
	private boolean incidentHasValidBillingOrg = true;
	private boolean incidentHasAllCodes = false;
	private Document newlyCreatedIncidentDoc = null;
	private String newIncidentInboxKey = null;
	private String distributionId = NWCGConstants.EMPTY_STRING;
	private int MAX_ENTITY_VALUE_LEN = 25;
	private boolean containsRadioItems = false;
	private String ownerAgency = "";

	// The unmarshalled JAXB object of the input message
	PlaceResourceRequestExternalReq input = null;

	/**
	 * 1. Verify if the shipping cache exists in ICBSR 
	 * 2. Verify if the incident exists in ICBSR 
	 * 3. Create the input for NWCGCreateIncidentIssueOrderService 
	 * 4. Verify which kind of, if any, shipping information is given (address, will pick up, instructions) 
	 * 5. Verify if the primary financial code exists for the incident issue 
	 * 6. Create response XML 
	 * 7. Generate an alert in NWCG_ISSUE_SUCCESS assigned to the appropriate cache administrator 
	 * 8. Post response message to ROSS via the A&A framework
	 * 
	 * @param YFSEnvironment - given by Sterling integration JVM
	 * @param Document - The body of the SOAPMessage received by A&A and retrieved from a JMS queue
	 */
	public Document process(YFSEnvironment env, Document inDoc) throws NWCGException {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::process @@@@@");
		
		final String methodName = "process";
		this.myEnvironment = env;
		Element docElm = inDoc.getDocumentElement();
		distributionId = docElm.getAttribute(NWCGConstants.INC_NOTIF_DIST_ID_ATTR);
		logger.verbose("@@@@@ distributionId :: " + distributionId);
		
		// Unmarshall the Input XML to a PlaceResourceRequestExternalReq object
		try {
			this.input = (PlaceResourceRequestExternalReq) new NWCGJAXBContextWrapper().getObjectFromDocument(inDoc, new URL(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
		} catch (Exception e) {
			logger.error("!!!!! " + NWCGConstants.NWCG_MSG_CODE_914_E_3);
			logger.error("!!!!! Caught General Exception : " + e);
			handleGeneralFailureWhileProcessingRequestBundle(e);
			throw new NWCGException(e);
		}
		// Determine if the number of request lines on this message exceeds the maximum (Common Code: NWCG_PLACEREQST.MAX_RQSTS_PER_MSG
		int numRequestLines = getNumberRequestLines();
		int maximumAllowed = CommonUtilities.getMaximumRequestLines(env);
		if (numRequestLines > maximumAllowed) {
			handleIssueTooManyLines(maximumAllowed, numRequestLines);
			return inDoc;
		} else if (numRequestLines == 0) {
			handleIssueNoRequestLines();
			return inDoc;
		}
		// Determine whether we need to split any of the request lines out into their own separate issues due to differences amongst combination of Shipping Contact Name/Phone/ReplacementIndicator across all the request lines in the given bundle.
		generateOrdersToCreateMap();
		// Pull out the Shipping Cache ID from the input
		String shippingCacheId = getShippingCacheIdFromInput();
		ownerAgency = getOwnerAgencyFromShippingCacheID(shippingCacheId);
		// And verify it exists in ICBSR
		boolean isValidShippingCacheId = CommonUtilities.isValidShippingCacheId(myEnvironment, shippingCacheId);
		// If the Shipping Cache ID doesn't exist, handle the scenario
		if (!isValidShippingCacheId) {
			handleInvalidShippingCache();
			return inDoc;
		}
		// First build a map to hold all of the input incident information from the input message
		HashMap<String, String> incidentInputMap = getIncidentInfoMapFromInput();
		// Pull out the incident number and incident year from the map.
		String incidentNo = incidentInputMap.get(NWCGConstants.INCIDENT_NO_ATTR);
		String incidentYear = incidentInputMap.get(NWCGConstants.BILL_TRANS_INCIDENT_YEAR);
		String incidentId = incidentInputMap.get(NWCGConstants.INCIDENT_ID);
		// Determine whether this PlaceRequestExternalReq message contains any radio kit items, if so, set the class boolean containsRadioItems = true;
		containsRadioItems = doesRequestBundleHaveRadioItems();
		// Verify that the incident exists
		Document existingIncident = doesIncidentExist(incidentNo, incidentYear);
		if (!incidentAlreadyExists && existingIncident == null) {
			// If not, create the incident, but first we validate whether there is primary financial code given in any of the Incident/IncidentDetails/IncidentFinancialCodes/FinancialCode elements on the input received.
			boolean isValidPrimaryFinancialCode = CommonUtilities.checkPrimaryFinCodeExistence(myEnvironment, inDoc);
			if (!isValidPrimaryFinancialCode) {
				handleInvalidPrimaryFinancialCode();
			}
			// Populate the localContext map with Extn attributes of the YFS_CUSTOMER table from Incident/IncidentDetails/BillingOrganization/Pfx+Sfx
			try {
				getCustomerExtnFieldsFromCustomerID();
			} catch (Exception e) {
				logger.error("!!!!! Error occured while calling getCustomerList API!");
				logger.error("!!!!! Caught General Exception : " + e);
				handleGeneralFailureWhileProcessingRequestBundle(e);
				throw new NWCGException(e);
			}
			if (!incidentHasValidBillingOrg) {
				handleInvalidBillingOrg();
				return inDoc;
			}
			// Create the NWCGIncidentOrder/ (NWCG_INCIDENT_ORDER ...)
			createNewIncident(inDoc);
			// Populate the localContext map with all of the information from the newly created incident by caling NWCGGetIncidentOrderOnly SDF service
			populateLocalContextWithNewIncidentInfo(incidentNo, incidentYear);
			// And create an alert in the Incident Success Alert Queue
			StringBuffer alertDesc = null;
			if (!incidentHasValidBillingOrg) {
				alertDesc = new StringBuffer("New Incident Received from ROSS with errors: Incident ");
			} else {
				alertDesc = new StringBuffer("New Incident Received from ROSS: Incident ");
			}
			alertDesc.append(newlyCreatedIncidentDoc.getDocumentElement().getAttribute(NWCGConstants.INCIDENT_NO_ATTR));
			if (containsRadioItems)
				createIssueAlertAndAssign(NWCGConstants.NWCG_INCIDENT_RADIOS_SUCCESS, alertDesc.toString());
			else
				createIssueAlertAndAssign(NWCGConstants.NWCG_INCIDENT_SUCCESS, alertDesc.toString());
		} else {
			incidentAlreadyExists = true;
		}
		String incidentKey = localContext.get(NWCGConstants.INCIDENT_KEY);
		// Need to update the NWCG_INCIDENT_ORDER record with the latest info contained in the PlaceResourceRequestExternalReq/Incident/IncidentDetails/ if the Incident already exists in ICBS. Otherwise the createIncident() method call above already created the Incident with the latest Incident data provided by ROSS.
		if (incidentAlreadyExists) {
			try {
				logger.verbose("@@@@@ Existing NWCGIncidentOrder from getNWCGIncidentOrder SDF: " + XMLUtil.extractStringFromDocument(existingIncident));
				Node firstChild = inDoc.getFirstChild();
				NodeList children = firstChild.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					Node currentNode = children.item(i);
					if (!(currentNode instanceof Element))
						continue;
					logger.verbose("@@@@@ currentNode.getNodeName() :: " + currentNode.getNodeName());
					//logger.verbose("@@@@@ currentNode.getLocalName() :: " + currentNode.getLocalName());
					//if (currentNode.getNodeName().equals(NWCGConstants.INCIDENT_ELEM) || currentNode.getLocalName().equals(NWCGConstants.INCIDENT_ELEM)) {
					if (currentNode.getNodeName().equals(NWCGConstants.INCIDENT_ELEM) || currentNode.getNodeName().contains(NWCGConstants.INCIDENT_ELEM)) {
						Document updateIncidentDoc = getCreateIncidentInputDoc(currentNode);
						Element updateIncidentDocElm = updateIncidentDoc.getDocumentElement();
						if (!StringUtil.isEmpty(localContext.get(NWCGConstants.INCIDENT_AGENCY_ATTR))) {
							updateIncidentDocElm.setAttribute(NWCGConstants.INCIDENT_AGENCY_ATTR, localContext.get(NWCGConstants.INCIDENT_AGENCY_ATTR));
						}
						if (!StringUtil.isEmpty(localContext.get(NWCGConstants.INCIDENT_GACC_ATTR))) {
							updateIncidentDocElm.setAttribute(NWCGConstants.INCIDENT_GACC_ATTR, localContext.get(NWCGConstants.INCIDENT_GACC_ATTR));
						}
						if (!StringUtil.isEmpty(localContext.get(NWCGConstants.INCIDENT_CUSTOMER_TYPE))) {
							updateIncidentDocElm.setAttribute(NWCGConstants.INCIDENT_CUSTOMER_TYPE, localContext.get(NWCGConstants.INCIDENT_CUSTOMER_TYPE));
						}
						if (!StringUtil.isEmpty(localContext.get(NWCGConstants.INCIDENT_CUSTOMER_NAME_ATTR))) {
							updateIncidentDocElm.setAttribute(NWCGConstants.INCIDENT_CUSTOMER_NAME_ATTR, localContext.get(NWCGConstants.INCIDENT_CUSTOMER_NAME_ATTR));
						}
						if (!StringUtil.isEmpty(localContext.get(NWCGConstants.INCIDENT_DEPARTMENT_ATTR))) {
							updateIncidentDocElm.setAttribute(NWCGConstants.INCIDENT_DEPARTMENT_ATTR, localContext.get(NWCGConstants.INCIDENT_DEPARTMENT_ATTR));
						}
						if (!StringUtil.isEmpty(localContext.get(NWCGConstants.INCIDENT_TYPE))) {
							updateIncidentDocElm.setAttribute(NWCGConstants.INCIDENT_TYPE, localContext.get(NWCGConstants.INCIDENT_TYPE));
						}
						if (!StringUtil.isEmpty(localContext.get(NWCGConstants.INCIDENT_ID))) {
							updateIncidentDocElm.setAttribute(NWCGConstants.INCIDENT_ID, localContext.get(NWCGConstants.INCIDENT_ID));
						}
						if (!StringUtil.isEmpty(localContext.get(NWCGConstants.INCIDENT_UNIT_TYPE_ATTR))) {
							updateIncidentDocElm.setAttribute(NWCGConstants.INCIDENT_UNIT_TYPE_ATTR, localContext.get(NWCGConstants.INCIDENT_UNIT_TYPE_ATTR));
						}
						//  Call the NWCGUpdateIncidentNotificationXSLService to transfer the XML format from the Incident/IncidentDetails format to NWCGIncidentOrder
						logger.verbose("@@@@@ updateIncidentDoc :: " + XMLUtil.getXMLString(updateIncidentDoc));
						Document updateAfterXslDoc = CommonUtilities.invokeService(env, NWCGConstants.SVC_UPDT_INCIDENT_NOTIF_XSL_SVC, updateIncidentDoc);
						logger.verbose("@@@@@ updateAfterXslDoc :: " + XMLUtil.getXMLString(updateAfterXslDoc));
						String dbgNWCGIncDoc = XMLUtil.extractStringFromDocument(updateAfterXslDoc);
						Element updateAfterXslDocElm = updateAfterXslDoc.getDocumentElement();
						// If we're updating an existing document there's no need to pad the seq nbr of the Incident because we already know the NWCG_INCIDENT_ORDER.INCIDENT_KEY
						if (updateAfterXslDocElm.hasAttribute(NWCGConstants.INCIDENT_NO_ATTR) || !StringUtil.isEmpty(updateAfterXslDocElm.getAttribute(NWCGConstants.INCIDENT_NO_ATTR))) {
							updateAfterXslDocElm.removeAttribute(NWCGConstants.INCIDENT_NO_ATTR);
						}
						if (!StringUtil.isEmpty(localContext.get(NWCGConstants.INCIDENT_AGENCY_ATTR))) {
							updateAfterXslDocElm.setAttribute(NWCGConstants.INCIDENT_AGENCY_ATTR, localContext.get(NWCGConstants.INCIDENT_AGENCY_ATTR));
						}
						if (!StringUtil.isEmpty(localContext.get(NWCGConstants.INCIDENT_GACC_ATTR))) {
							updateAfterXslDocElm.setAttribute(NWCGConstants.INCIDENT_GACC_ATTR, localContext.get(NWCGConstants.INCIDENT_GACC_ATTR));
						}
						if (!StringUtil.isEmpty(localContext.get(NWCGConstants.INCIDENT_CUSTOMER_TYPE))) {
							updateAfterXslDocElm.setAttribute(NWCGConstants.INCIDENT_CUSTOMER_TYPE, localContext.get(NWCGConstants.INCIDENT_CUSTOMER_TYPE));
						}
						if (!StringUtil.isEmpty(localContext.get(NWCGConstants.INCIDENT_CUSTOMER_NAME_ATTR))) {
							updateAfterXslDocElm.setAttribute(NWCGConstants.INCIDENT_CUSTOMER_NAME_ATTR, localContext.get(NWCGConstants.INCIDENT_CUSTOMER_NAME_ATTR));
						}
						if (!StringUtil.isEmpty(localContext.get(NWCGConstants.INCIDENT_DEPARTMENT_ATTR))) {
							updateAfterXslDocElm.setAttribute(NWCGConstants.INCIDENT_DEPARTMENT_ATTR, localContext.get(NWCGConstants.INCIDENT_DEPARTMENT_ATTR));
						}
						if (!StringUtil.isEmpty(localContext.get(NWCGConstants.INCIDENT_TYPE))) {
							updateAfterXslDocElm.setAttribute(NWCGConstants.INCIDENT_TYPE, localContext.get(NWCGConstants.INCIDENT_TYPE));
						}
						if (!StringUtil.isEmpty(localContext.get(NWCGConstants.INCIDENT_ID))) {
							updateAfterXslDocElm.setAttribute(NWCGConstants.INCIDENT_ID, localContext.get(NWCGConstants.INCIDENT_ID));
						}
						if (!StringUtil.isEmpty(localContext.get(NWCGConstants.INCIDENT_TEAM_TYPE))) {
							updateAfterXslDocElm.setAttribute(NWCGConstants.INCIDENT_TEAM_TYPE, localContext.get(NWCGConstants.INCIDENT_TEAM_TYPE));
						}
						if (!StringUtil.isEmpty(localContext.get(NWCGConstants.INCIDENT_UNIT_TYPE_ATTR))) {
							updateAfterXslDocElm.setAttribute(NWCGConstants.INCIDENT_UNIT_TYPE_ATTR, localContext.get(NWCGConstants.INCIDENT_UNIT_TYPE_ATTR));
						}
						updateAfterXslDocElm.setAttribute(NWCGAAConstants.MDTO_DISTID, distributionId);
						updateAfterXslDocElm.setAttribute(NWCGConstants.INCIDENT_KEY, incidentKey);
						updateAfterXslDocElm.setAttribute(NWCGConstants.MODIFICATION_CODE, "Auto Update From ROSS");
						String modDesc = "PlaceRq: " + CommonUtilities.getXMLCurrentTime();
						logger.verbose("@@@@@ modDesc : " + modDesc);
						updateAfterXslDocElm.setAttribute(NWCGConstants.MODIFICATION_DESC, modDesc);
						if (!StringUtil.isEmpty(localContext.get("PersonInfoShipToKey"))) {
							updateAfterXslDocElm.setAttribute("PersonInfoShipToKey", localContext.get("PersonInfoShipToKey"));
							logger.verbose("@@@@@ PersonInfoShipToKey :: " + localContext.get("PersonInfoShipToKey"));
						}
						if (!StringUtil.isEmpty(localContext.get("PersonInfoBillToKey"))) {
							updateAfterXslDocElm.setAttribute("PersonInfoBillToKey", localContext.get("PersonInfoBillToKey"));
						}
						if (!StringUtil.isEmpty(localContext.get("BillingPersonInfoKey"))) {
							updateAfterXslDocElm.setAttribute("PersonInfoDeliverToKey", localContext.get("PersonInfoDeliverToKey"));
						}
						// Set LastUpdatedFromROSS
						String lastUpdtFromROSS = updateAfterXslDocElm.getAttribute(NWCGConstants.LAST_UPDATED_FROM_ROSS);
						if (lastUpdtFromROSS == null || lastUpdtFromROSS.trim().length() < 2) {
							updateAfterXslDocElm.setAttribute(NWCGConstants.LAST_UPDATED_FROM_ROSS, CommonUtilities.getXMLCurrentTime());
							logger.verbose("@@@@@ lastUpdtFromROSS : " + CommonUtilities.getXMLCurrentTime());
						}
						try {
							// Remove any attributes with empty values such as CustomerName="" because this causes ICBS to erase the current value which we don't want.
							updateAfterXslDoc = removeEmptyAttributes(updateAfterXslDoc);
							logger.verbose("@@@@@ NWCGPlaceResourceRequestExternalHandler::process, Input XML to NWCGChangeIncidentOrderService : " + XMLUtil.extractStringFromDocument(updateAfterXslDoc));
							CommonUtilities.invokeService(env, NWCGConstants.SVC_CHG_INCIDENT_ORDER_SVC, updateAfterXslDoc);
							// Populate the localContext map with all of the updated Incident data from the newly updated Incident by calling NWCGGetIncidentOrderOnly SDF service
							populateLocalContextWithNewIncidentInfo(incidentNo, incidentYear);
							break;
						} catch (Exception e) {
							logger.error("!!!!! FAILED to UPDATE Incident! ");
							throw e;
						}
					}
				}
			} catch (Exception e) {
				// Not creating an alert
				logger.error("!!!!! Outer Exception: NWCGPlaceResourceRequestExternalHandler::process, Incident update failed.");
				logger.error("!!!!! Caught General Exception :: " + e);
				logger.error("!!!!! Outer Exception: FAILED to UPDATE Incident!");
			}
		}
		// Create the order header in draft status and add the request lines
		try {
			createDraftIssuesAndAddLines(env);
		} catch (Exception e) {
			logger.error("!!!!! " + NWCGConstants.NWCG_MSG_CODE_914_E_8);
			logger.error("!!!!! Caught General Exception :: " + e);
			handleGeneralFailureWhileProcessingRequestBundle(e);
			throw new NWCGException(e);
		}
		// Validate whether there is either a Request/ShippingAddress, Request/ShippingInstructions, or Request/WillPickUpInfo on the first request line in the bundle
		boolean isValidShippingInfo = isValidShippingInfoGiven();
		if (!isValidShippingInfo) {
			handleInvalidShippingInfo();
		}
		// If the incident already existed, determine whether it was active or inactive in ICBSR.
		if (incidentAlreadyExists) {
			Element existingIncidentDocElm = existingIncident.getDocumentElement();
			String fsAcctCode = NWCGConstants.EMPTY_STRING;
			String toAcctCode = NWCGConstants.EMPTY_STRING;
			String blmAcctCode = NWCGConstants.EMPTY_STRING;
			// Determine whether it's active or not in ICBSR
			boolean isIncidentActiveICBSR = existingIncidentDocElm.getAttribute(NWCGConstants.IS_ACTIVE).equalsIgnoreCase(NWCGConstants.YES) ? true : false;
			if (isIncidentActiveICBSR) {
				fsAcctCode = existingIncidentDocElm.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE);
				toAcctCode = existingIncidentDocElm.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE);
				blmAcctCode = existingIncidentDocElm.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE);
				if (ownerAgency == null || ownerAgency.length() < 1) {
					ownerAgency = getOwnerAgencyFromShippingCacheID(shippingCacheId);
				}
				if (!StringUtil.isEmpty(ownerAgency)) {
					if (ownerAgency.equals(NWCGConstants.BLM_OWNER_AGENCT) && (StringUtil.isEmpty(blmAcctCode) || blmAcctCode.length() < 4)) {
						placeNewIssueOnHoldWithHoldType(NWCGConstants.NWCG_HOLD_TYPE_2_NEW_INC_CREATED);
					} else if (ownerAgency.equals(NWCGConstants.FS_OWNER_AGENY) && StringUtil.isEmpty(fsAcctCode)) {
						placeNewIssueOnHoldWithHoldType(NWCGConstants.NWCG_HOLD_TYPE_2_NEW_INC_CREATED);
					} else if (ownerAgency.equals(NWCGConstants.OTHER_OWNER_AGENY) && StringUtil.isEmpty(toAcctCode)) {
						placeNewIssueOnHoldWithHoldType(NWCGConstants.NWCG_HOLD_TYPE_2_NEW_INC_CREATED);
					} else {
						incidentHasAllCodes = true;
					}
				}
				// Create the alert in NWCG_ISSUE_SUCCESS for the newly created issue(s)
				handleIssueCreatedSuccessfully();
				// Send issue# in response to ROSS with information message "Issue successfully created in ICBSR."
				String respMsg = "Issue successfully created in ICBSR";
				if (ordersCreated.size() > 1) {
					respMsg = "Issues successfully created in ICBSR";
				}
				postResponseToROSS(NWCGAAConstants.ROSS_RET_SUCCESS_VALUE, NWCGAAConstants.DELIVER_OPERATION_RESULTS_SEVERITY_3, respMsg, NWCGConstants.NWCG_MSG_CODE_914_W_1, true);
			} else {
				//  Generate an alert in NWCG_ISSUE_SUCCESS Queue with message "Bundle received from ROSS: Issue created successfully", providing incident information, issue# along with first and the last request# of the bundle in reference field, and assign alert to the Cache administrator.
				handleIssueCreatedSuccessfully();
				// Since the incident is inactive in ICBSR, we apply the hold type INCIDENT_INACTIVE to all orders created
				placeNewIssueOnHoldWithHoldType(NWCGConstants.NWCG_HOLD_TYPE_1_INCIDENT_INACTIVE);
				// Send issue# in response to ROSS with information message 0 / WARNING "Issue successfully created in ICBSR and placed on hold."
				String respMsg = "Issue successfully created in ICBSR and placed on hold";
				if (ordersCreated.size() > 1) {
					respMsg = "Issues successfully created in ICBSR and placed on hold";
				}
				postResponseToROSS(NWCGAAConstants.ROSS_RET_SUCCESS_VALUE, NWCGAAConstants.DELIVER_OPERATION_RESULTS_SEVERITY_3, respMsg, NWCGConstants.NWCG_MSG_CODE_914_W_2, true);
			}
		} else {
			// Generate an alert in NWCG_ISSUE_SUCCESS Queue with message "Bundle received from ROSS: Issue created successfully", providing incident information, issue# along with first and the last request# of the bundle in reference field, and assign alert to the Cache administrator.
			handleIssueCreatedSuccessfully();
			// ICBSR had to create the incident so we put the new issue on hold
			placeNewIssueOnHoldWithHoldType(NWCGConstants.NWCG_HOLD_TYPE_2_NEW_INC_CREATED);
			// Update the alert for the new incident to include the order number(s) created
			updateNewIncidentAlertWithIssueRefs();
			// Send issue# in response to ROSS with information message 0 /WARNING / "Issue successfully created in ICBSR and placed on hold."
			String respMsg = "Issue successfully created in ICBSR and placed on hold.";
			if (ordersCreated.size() > 1) {
				respMsg = "Issues successfully created in ICBSR and placed on hold.";
			}
			postResponseToROSS(NWCGAAConstants.ROSS_RET_SUCCESS_VALUE, NWCGAAConstants.DELIVER_OPERATION_RESULTS_SEVERITY_3, respMsg, NWCGConstants.NWCG_MSG_CODE_914_W_1, true);
		}
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::process @@@@@");
		return inDoc;
	}

	/**
	 * 
	 * @param updateAfterXslDoc
	 * @return
	 */
	private Document removeEmptyAttributes(Document updateAfterXslDoc) {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::removeEmptyAttributes @@@@@");
		if (updateAfterXslDoc != null) {
			NodeList nl = updateAfterXslDoc.getElementsByTagName("*");
			if (nl != null && nl.getLength() > 0) {
				for (int i = 0; i < nl.getLength(); i++) {
					Node n = nl.item(i);
					Element curElm = (n instanceof Element) ? (Element) n : null;
					if (curElm == null)
						continue;
					NamedNodeMap nnm = curElm.getAttributes();
					for (int j = 0; j < nnm.getLength(); j++) {
						Node d = nnm.item(j);
						if (StringUtil.isEmpty(d.getNodeValue())) {
							nnm.removeNamedItem(d.getNodeName());
						}
					}
				}
			}
		}
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::removeEmptyAttributes @@@@@");
		return updateAfterXslDoc;
	}

	/**
	 * 
	 * @return
	 */
	private boolean doesRequestBundleHaveRadioItems() {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::doesRequestBundleHaveRadioItems @@@@@");
		final String methodName = "doesRequestBundleHaveRadioItems";
		boolean retVal = false;
		ArrayList<ResourceRequestCreateType> al = (ArrayList<ResourceRequestCreateType>) input.getRequest();
		CatalogItemType curRequestCatalogItem = null;
		CatalogItemNaturalKeyType curRequestItemKey = null;
		String curItemDesc = null;
		for (int i = 0; i < al.size(); i++) {
			ResourceRequestCreateType curRequest = (ResourceRequestCreateType) al.get(i);
			if (curRequest == null)
				continue;
			if (curRequest.isSetRequestedItemKey()) {
				curRequestItemKey = curRequest.getRequestedItemKey();
				curItemDesc = curRequestItemKey.getCatalogItemName();
			} else if (curRequest.isSetRequestedCatalogItem()) {
				curRequestCatalogItem = curRequest.getRequestedCatalogItem();
				curItemDesc = curRequestCatalogItem.getCatalogItemDescription();
			}
			retVal = isRadioItem(curItemDesc);
		}
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::doesRequestBundleHaveRadioItems @@@@@");
		return retVal;
	}

	/**
	 * 
	 * @param shortDesc
	 * @return
	 */
	private boolean isRadioItem(String shortDesc) {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::isRadioItem @@@@@");
		final String methodName = "isRadioItem";
		boolean radioItem = false;
		Document getItemListInputDoc;
		try {
			getItemListInputDoc = XMLUtil.createDocument(NWCGConstants.ITEM);
		} catch (ParserConfigurationException e) {
			logger.error("!!!!! Failed to create an XML Document: <Item/>");
			logger.error("!!!!! Caught ParserConfigurationException Exception: " + e);
			return radioItem;
		}

		Element primaryInfoElm = getItemListInputDoc.createElement(NWCGConstants.PRIMARY_INFORMATION);
		primaryInfoElm.setAttribute(NWCGConstants.SHORT_DESCRIPTION, shortDesc);
		getItemListInputDoc.getDocumentElement().appendChild(primaryInfoElm);
		Document getItemListOutput;
		try {
			getItemListOutput = CommonUtilities.invokeAPI(myEnvironment, NWCGConstants.GET_ITEM_LIST_OP_FILENAME, NWCGConstants.API_GET_ITEM_LIST, getItemListInputDoc);
		} catch (Exception e) {
			logger.error("!!!!! ICBS was unable to determine whether Item Description: " + shortDesc + "is a ROSS Resource Item and Published to ROSS!");
			logger.error("!!!!! Caught General Exception: " + e);
			return radioItem;
		}
		NodeList nl = getItemListOutput.getDocumentElement().getElementsByTagName(NWCGConstants.ITEM);
		if (nl != null && nl.getLength() == 1) {
			Node itemNode = nl.item(0);
			Element itemElem = (itemNode instanceof Element) ? (Element) itemNode : null;
			if (itemElem != null) {
				// Try to get the Item/PrimaryInformation element first as the first child of the Item/ element
				Element itemPrimaryInfoElm = (Element) itemElem.getFirstChild();
				String productLine = itemPrimaryInfoElm.getAttribute(NWCGConstants.PRODUCT_LINE);
				Locale localeENUS = new Locale("en", "US");
				if (!StringUtil.isEmpty(productLine) && (productLine.toLowerCase(localeENUS).contains("communication") || productLine.toUpperCase(localeENUS).contains("COMMUNICATION"))) {
					radioItem = true;
				}
			}
		}
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::isRadioItem @@@@@");
		return radioItem;
	}

	/**
	 * Creates Order/OrderLines/OrderLine elements per Incident/Request in the input message.
	 * 
	 * @param retVal
	 * @param ordersToCreateKey
	 * @return Document createOrder
	 * @throws Exception
	 */
	private Document addRequestLinesToOrder(YFSEnvironment env, Document createOrderInputDoc, String ordersToCreateKey) throws Exception {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::addRequestLinesToOrder @@@@@");
		Element docElm = createOrderInputDoc.getDocumentElement();
		Element orderLines = createOrderInputDoc.createElement(NWCGConstants.ORDER_LINES);
		Vector v = ordersToCreate.get(ordersToCreateKey);
		boolean hasShippingMethodBeenSet = false;
		boolean hasRossFinCodeChildrenSet = false;
		Element orderExtnElm = XMLUtil.getFirstElementByName(docElm, NWCGConstants.EXTN_ELEMENT);
		if (orderExtnElm == null) {
			orderExtnElm = XMLUtil.getFirstElementByName(docElm, NWCGConstants.EXTN_ELEMENT);
		}
		orderExtnElm.setAttribute(NWCGConstants.EXTN_SYSTEM_OF_ORIGIN, NWCGConstants.ROSS_SYSTEM);
		boolean seqNbrExtracted = false;
		for (int i = 0; i < v.size(); i++) {
			ResourceRequestCreateType curRequest = (ResourceRequestCreateType) v.get(i);
			CatalogItemType curRequestCatalogItem = null;
			CatalogItemNaturalKeyType curRequestItemKey = null;
			String curItemDesc = null;
			if (curRequest.isSetRequestedItemKey()) {
				curRequestItemKey = curRequest.getRequestedItemKey();
				curItemDesc = curRequestItemKey.getCatalogItemName();
			} else if (curRequest.isSetRequestedCatalogItem()) {
				curRequestCatalogItem = curRequest.getRequestedCatalogItem();
				curItemDesc = curRequestCatalogItem.getCatalogItemDescription();
			}
			// Pull the necessary string values we want from the root/Request element, and children
			String qtyRequested = curRequest.getQuantityRequested().toString();
			String reqDelivDate = curRequest.getNeedDateTime().toXMLFormat();
			// If we get the time zone in GMT, then the timestamp is coming as 2011-03-11T18:00:00Z instead of 2011-03-11T18:00:00+00:00
			if (reqDelivDate.endsWith("Z")) {
				reqDelivDate = reqDelivDate.replaceFirst("Z", "+00:00");
			}
			// Hardcoding timezone to CST
			reqDelivDate = CommonUtilities.convertTimeZone(reqDelivDate, "en_US_CST");
			String replaceIndic = curRequest.isReplacementInd().booleanValue() ? NWCGConstants.YES : NWCGConstants.NO;
			String specialNeeds = curRequest.getSpecialNeeds();
			String seqNumber = curRequest.getSequenceNumber();
			String contactName = curRequest.getShippingContactName();
			String contactPhone = curRequest.getShippingContactPhone();
			if (!seqNbrExtracted) {
				seqNbrExtracted = true;
			}
			if (replaceIndic.equals(NWCGConstants.YES) || replaceIndic.equalsIgnoreCase(NWCGConstants.STR_TRUE)) {
				docElm.setAttribute(NWCGConstants.ORDER_TYPE, NWCGConstants.ORDER_TYPE_REPLACEMENT);
			} else {
				docElm.setAttribute(NWCGConstants.ORDER_TYPE, NWCGConstants.ORDER_TYPE_NORMAL);
			}
			if (!hasRossFinCodeChildrenSet) {
				FinancialCodeType fc = curRequest.getFinancialCode();
				orderExtnElm.setAttribute(NWCGConstants.EXTN_ROSS_FIN_CODE_ATTR, fc.getCode());
				orderExtnElm.setAttribute(NWCGConstants.EXTN_ROSS_OWNING_AGENCY, fc.getOwningAgencyName());
				Integer fiscalYear = new Integer(fc.getFiscalYear());
				orderExtnElm.setAttribute(NWCGConstants.EXTN_ROSS_FISCAL_YEAR, fiscalYear.toString());
				hasRossFinCodeChildrenSet = true;
			}
			// Set Order/@ReqDeliveryDate as the WillPickUpDateTime if it's specified other wise from Request/NeedDateTime
			if (curRequest.isSetWillPickUpInfo() && curRequest.getWillPickUpInfo().isSetPickUpDateTime()) {
				reqDelivDate = curRequest.getWillPickUpInfo().getPickUpDateTime().toXMLFormat();
				if (reqDelivDate.endsWith("Z")) {
					reqDelivDate = reqDelivDate.replaceFirst("Z", "+00:00");
				}
				// Hardcoding timezone to CST
				reqDelivDate = CommonUtilities.convertTimeZone(reqDelivDate, "en_US_CST");
				docElm.setAttribute(NWCGConstants.REQ_DELIVERY_DATE_ATTR, reqDelivDate);
				localContext.put(NWCGConstants.EXTN_REQ_DELIVERY_DATE_ATTR, reqDelivDate);
			} else {
				docElm.setAttribute(NWCGConstants.REQ_DELIVERY_DATE_ATTR, reqDelivDate);
				orderExtnElm.setAttribute(NWCGConstants.EXTN_REQ_DELIVERY_DATE_ATTR, reqDelivDate);
				localContext.put(NWCGConstants.EXTN_REQ_DELIVERY_DATE_ATTR, reqDelivDate);
			}
			// Check to see if the Request/SequenceNumber starts with S-, other wise prepend it to the number TODO: We are manually pre-pending "S-" here for the request number sequence number given on the request line. Needs to be updated to handle other ("E") supply types.
			if (!seqNumber.startsWith(NWCGConstants.NFES_SUPPLY_FULL_PFX)) {
				StringBuffer sb = new StringBuffer(NWCGConstants.NFES_SUPPLY_FULL_PFX);
				sb.append(seqNumber);
				seqNumber = sb.toString();
			}
			Element orderLine = createOrderInputDoc.createElement(NWCGConstants.ORDER_LINE);
			Element orderLineItem = createOrderInputDoc.createElement(NWCGConstants.ITEM);
			Element orderLineInstructions = createOrderInputDoc.createElement(NWCGConstants.INSTRUCTIONS_ELEMENT);
			Element orderLineInstructionTxt = createOrderInputDoc.createElement(NWCGConstants.INSTRUCTION_ELEMENT);
			Element orderLineExtn = createOrderInputDoc.createElement(NWCGConstants.EXTN_ELEMENT);
			Element orderLineTranQty = createOrderInputDoc.createElement(NWCGConstants.ORDER_LINE_TRAN_QTY_ELM);
			orderLines.appendChild(orderLine);
			orderLine.appendChild(orderLineTranQty);
			orderLine.appendChild(orderLineExtn);
			orderLine.appendChild(orderLineItem);
			if (!StringUtil.isEmpty(specialNeeds)) {
				orderLine.appendChild(orderLineInstructions);
				orderLineInstructions.appendChild(orderLineInstructionTxt);
				orderLineInstructionTxt.setAttribute(NWCGConstants.INSTRUCTION_TEXT_ATTR, specialNeeds);
				orderLineInstructionTxt.setAttribute(NWCGConstants.INSTRUCTION_TYPE_ATTR, "ROSS Notes");
			}
			HashMap<String, String> item = getICBSRItemWithROSSItemDesc(curItemDesc);
			item.put(NWCGConstants.SHORT_DESCRIPTION, curItemDesc);
			String rfiQty = getRFIQty(env, item);
			orderExtnElm.setAttribute(NWCGConstants.EXTN_SYSTEM_OF_ORIGIN, NWCGConstants.ROSS_SYSTEM);
			orderExtnElm.setAttribute(NWCGConstants.SHIP_CONTACT_NAME_ATTR, StringUtil.nonNull(contactName));
			orderExtnElm.setAttribute(NWCGConstants.SHIP_CONTACT_PHONE_ATTR, StringUtil.nonNull(contactPhone));
			orderLine.setAttribute(NWCGConstants.CONDITION_VAR1, NWCGConstants.ROSS_SYSTEM);
			orderLineExtn.setAttribute(NWCGConstants.EXTN_REQUEST_NO, seqNumber);
			orderLineExtn.setAttribute(NWCGConstants.EXTN_BASE_REQUEST_NO, seqNumber);
			orderLineExtn.setAttribute(NWCGConstants.EXTN_SYSTEM_OF_ORIGIN, NWCGConstants.ROSS_SYSTEM);
			orderLineExtn.setAttribute(NWCGConstants.EXTN_BACKORDER_FLAG, NWCGConstants.NO);
			orderLineExtn.setAttribute(NWCGConstants.EXTN_BACKORDER_QTY, NWCGConstants.ZERO_STRING);
			orderLineExtn.setAttribute(NWCGConstants.EXTN_FWD_QTY, NWCGConstants.ZERO_STRING);
			orderLineExtn.setAttribute(NWCGConstants.ORIGINAL_REQUESTED_QTY, qtyRequested);
			orderLineExtn.setAttribute(NWCGConstants.EXTN_RFI_QTY, rfiQty);
			orderLineExtn.setAttribute(NWCGConstants.EXTN_UTF_QTY, NWCGConstants.ZERO_STRING);
			orderLineItem.setAttribute(NWCGConstants.ITEM_ID, item.get(NWCGConstants.ITEM_ID));
			orderLineItem.setAttribute(NWCGConstants.UNIT_OF_MEASURE, item.get(NWCGConstants.UNIT_OF_MEASURE));
			orderLineItem.setAttribute(NWCGConstants.PRODUCT_CLASS, item.get(NWCGConstants.DEFAULT_PRODUCT_CLASS));
			orderLineItem.setAttribute(NWCGConstants.ITEM_SHORT_DESC, curItemDesc);
			orderLineTranQty.setAttribute(NWCGConstants.ORDERED_QTY, qtyRequested);
			orderLineTranQty.setAttribute(NWCGConstants.TRANSACTIONAL_UOM, item.get(NWCGConstants.UNIT_OF_MEASURE));
			
			logger.verbose("@@@@@ hasShippingMethodBeenSet :: " + hasShippingMethodBeenSet);
			if (!hasShippingMethodBeenSet) {
				logger.verbose("@@@@@ curRequest.isSetShippingAddress() :: " + curRequest.isSetShippingAddress());
				if (curRequest.isSetShippingAddress()) {
					orderExtnElm.setAttribute(NWCGConstants.EXTN_NAV_INFO_ATTR, NWCGConstants.SHIPPING_ADDRESS);
					hasShippingMethodBeenSet = true;
					setPersonInfoShipToElm(createOrderInputDoc, curRequest);
				} else if (curRequest.isSetWillPickUpInfo()) {
					orderExtnElm.setAttribute(NWCGConstants.EXTN_NAV_INFO_ATTR, NWCGConstants.WILL_PICK_UP);
					// Grab the ScacAndServiceKey out of NWCGAnAEnvironment.properties
					String willPickUpScacAndServiceKey = ResourceUtil.get("PlaceResourceRequestExternalReq.ScacAndServiceKey.WillPickUp");
					// If it's not found, default to the hard coded value
					if (StringUtil.isEmpty(willPickUpScacAndServiceKey)) {
						willPickUpScacAndServiceKey = "200903121110024297629";
					}
					docElm.setAttribute(NWCGConstants.SCAC_AND_SERVICE_KEY, willPickUpScacAndServiceKey);
					hasShippingMethodBeenSet = true;
					setWillPickUpExtnAttribs(orderExtnElm, curRequest);
				} else if (curRequest.isSetShippingInstructions()) {
					orderExtnElm.setAttribute(NWCGConstants.EXTN_NAV_INFO_ATTR, NWCGConstants.SHIPPING_INSTRUCTIONS);
					hasShippingMethodBeenSet = true;
					setExtnShippingInstructions(orderExtnElm, curRequest);
				}
			}
		}
		docElm.appendChild(orderLines);
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::addRequestLinesToOrder @@@@@");
		return createOrderInputDoc;
	}

	/**
	 * Calls createOrder to create the order header and order lines to add the
	 * request lines from the input bundle. The number of calls made to the
	 * createOrder API is dependant on the number of unique combinations of
	 * Shipping Contact Name / Phone / and Replacement Indicator across all of
	 * the request lines in the incoming bundle.
	 * 
	 * createOrder sample input generated:
	 * 
	 * <?xml version="1.0" encoding="UTF-8"?> <Order BillToID="AZPFK"
	 * DocumentType="0001" DraftOrderFlag="Y" EnterpriseCode="NWCG"
	 * IncidentKey="200910311700124023498" IsActive="Y" OrderType="Normal"
	 * ReqDeliveryDate="2001-12-01T09:30:00.0Z" SellerOrganizationCode="NWCG"
	 * ShipNode="CORMK"> <Extn ExtnAgency="FS"
	 * ExtnCustomerName="SW AREA PRESCOTT INCIDENT SUPPORT CACHE"
	 * ExtnCustomerType="01" ExtnDepartment="USDA" ExtnGACC=
	 * "SW (Southwest Area Coordination Center FC (National Interagency Coordination Center)"
	 * ExtnIncidentCacheId="CORMK"
	 * ExtnIncidentName="Incr 2 - Integration Test Incident A"
	 * ExtnIncidentNo="AZ-PFK-000003" ExtnIncidentTeamType="FireWildfire"
	 * ExtnIncidentType="FireWildfire" ExtnIncidentYear="2009"
	 * ExtnNavInfo="SHIP_ADDRESS" ExtnOverrideCode=""
	 * ExtnROSSFinancialCode="BL-MAC-249-CTAS-AS-ASAS-264B"
	 * ExtnROSSFiscalYear="2008" ExtnROSSOwningAgency="BLM"
	 * ExtnReqDeliveryDate="2001-12-01T09:30:00.0Z" ExtnSAOverrideCode=""
	 * ExtnShipAcctCode="" ExtnSystemOfOrigin="ROSS" ExtnUnitType="Federal"/>
	 * <OrderDates> <OrderDate ActualDate="2009-11-08T19:19:54.262-0500"
	 * DateTypeId="NWCG_DATE" ExpectedDate="2009-11-08T19:19:54.262-0500"/>
	 * </OrderDates> <PersonInfoShipTo AddressLine1="1234 Main St"
	 * AddressLine2="Suite 200" AlternateEmailID="XYZABC" City="Los Angeles"
	 * Country="US" FirstName="John" LastName="Doe" State="CA" ZipCode="90210"/>
	 * <OrderLines> <OrderLine ConditionVariable1="ROSS"> <OrderLineTranQuantity
	 * OrderedQty="3" TransactionalUOM="EA"/> <Extn ExtnBackOrderFlag="N"
	 * ExtnBackorderedQty="0" ExtnFwdQty="0" ExtnOrigReqQty="3" ExtnQtyRfi="0"
	 * ExtnRequestNo="S-121" ExtnShippingContactName="Shipping Contact Name - 2"
	 * ExtnShippingContactPhone="404-555-1212" ExtnSystemOfOrigin="ROSS"
	 * ExtnUTFQty="0"/> <Item ItemID="005321"
	 * ItemShortDesc="ANTENNA - KING, VHF SCREWMOUNT OR BNC"
	 * ProductClass="Supply" UnitOfMeasure="EA"/> <Instructions> <Instruction
	 * InstructionText="Special Needs for this request line"
	 * InstructionType="ROSS Notes"/> </Instructions> </OrderLine> </OrderLines>
	 * </Order>
	 * 
	 * @param input
	 * @return Document for the NWCGCreateIncidentOrder SDF service
	 * @throws Exception
	 */
	private void createDraftIssuesAndAddLines(YFSEnvironment env) throws Exception {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::createDraftIssuesAndAddLines @@@@@");
		Set<String> ordersToCreateKeys = ordersToCreate.keySet();
		Iterator<String> keySetIterator = ordersToCreateKeys.iterator();
		while (keySetIterator.hasNext()) {
			String curKey = keySetIterator.next();
			Document createOrderHdrIp = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
			Element docElm = createOrderHdrIp.getDocumentElement();
			Element orderExtnElm = createOrderHdrIp.createElement(NWCGConstants.EXTN_ELEMENT);
			docElm.appendChild(orderExtnElm);
			getCustomerExtnFieldsFromCustomerID();
			// <Order ...>
			setOrderElementAttributes(docElm);
			// <Order><Extn ...></Order>
			setOrderExtnElementAttributes(orderExtnElm);
			// <Order><OrderDates><OrderDate DateTypeId="NWCG_DATE" ActualDate=<cur> ExpecteDate=<cur>
			setOrderHeaderDates(docElm);
			try {
				createOrderHdrIp = addRequestLinesToOrder(env, createOrderHdrIp, curKey);
				String crOr = XMLUtil.extractStringFromDocument(createOrderHdrIp);
				StringBuffer sb = new StringBuffer("<Order EnterpriseCode=\"\" OrderHeaderKey=\"\" DocumentType=\"\" OrderNo=\"\" Createts=\"\">");
				sb.append("<OrderLines>");
				sb.append("<OrderLine OrderLineKey=\"\">");
				sb.append("<Extn ExtnRequestNo=\"\"/>");
				sb.append("</OrderLine>");
				sb.append("</OrderLines>");
				sb.append("</Order>");
				Document opTemplate = XMLUtil.getDocument(sb.toString());
				logger.verbose("@@@@@ opTemplate :: " + XMLUtil.getXMLString(opTemplate));
				logger.verbose("@@@@@ createOrderHdrIp :: " + XMLUtil.getXMLString(createOrderHdrIp));
				Document crOrOutput = CommonUtilities.invokeAPI(myEnvironment, opTemplate, NWCGConstants.API_CREATE_ORDER, createOrderHdrIp);
				logger.verbose("@@@@@ crOrOutput :: " + XMLUtil.getXMLString(crOrOutput));
				String curOrderNo = crOrOutput.getDocumentElement().getAttribute(NWCGConstants.ORDER_NO);
				orderNoToMapKey.put(curOrderNo, curKey);
				String crOrOutputStr = XMLUtil.extractStringFromDocument(crOrOutput);
				ordersCreated.add(crOrOutput);
			} catch (Exception e) {
				logger.error("!!!!! " + NWCGConstants.NWCG_MSG_CODE_914_E_6);
				logger.error("!!!!! Caught General Exception: " + e);
				myEnvironment.setRollbackOnly(true);
				throw e;
			}
		}
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::createDraftIssuesAndAddLines @@@@@");
	}

	/**
	 * 
	 * @param queueId
	 * @param alertDescription
	 */
	private void createIssueAlertAndAssign(String queueId, String alertDescription) {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::createIssueAlertAndAssign @@@@@");
		HashMap<String, String> inboxReferencesMap = new HashMap<String, String>();
		// Get the first and last sequence numbers in the request bundle. Ordering is determined by the top-down appearance of request lines in the input message XML.
		// HashMap<String, String> firstAndLastSeqNos = getFirstAndLastRequestNumbers();
		// inboxReferencesMap.put("FirstRequestNo", firstAndLastSeqNos.get("first"));
		// inboxReferencesMap.put("LastRequestNo", firstAndLastSeqNos.get("last"));
		inboxReferencesMap.put(NWCGAAConstants.NAME, "PlaceResourceRequestExternalReq");
		// If we find a user Id to assign the alert to, then do so, otherwise the raiseAlertAndAssignToUser method will leave the alert unassigned
		String incidentNoFromInput = getIncidentNumberFromInput();
		String incidentYearFromInput = getIncidentYearFromInput();
		String shippingCacheFromInput = getShippingCacheIdFromInput();
		String dispatchUnitIdFromInput = getDispatchOrganizationFromInput();
		inboxReferencesMap.put("Incident Number", incidentNoFromInput);
		inboxReferencesMap.put("Incident Year", incidentYearFromInput);
		inboxReferencesMap.put(NWCGConstants.ALERT_SHIPNODE_KEY, shippingCacheFromInput);
		inboxReferencesMap.put("Dispatch Unit ID", dispatchUnitIdFromInput);
		inboxReferencesMap.put("Distribution ID", distributionId);
		// Add the IncidentKey
		String incidentKey = localContext.get(NWCGConstants.INCIDENT_KEY);
		if (incidentKey != null && incidentKey.trim().length() != 0) {
			inboxReferencesMap.put(NWCGConstants.INCIDENT_KEY, incidentKey);
		}
		// Grab from the BillTo Org / Customer ID from the input incident element
		String customerId = getCustomerIdFromInput();
		String primaryCacheIdForIncident = getPrimaryCacheForIncident(incidentNoFromInput, incidentYearFromInput);
		if (!incidentHasValidBillingOrg) {
			inboxReferencesMap.put(NWCGConstants.ALERT_CUST_ID, customerId + " (invalid)");
		} else {
			inboxReferencesMap.put(NWCGConstants.ALERT_CUST_ID, customerId);
		}
		if (!StringUtil.isEmpty(primaryCacheIdForIncident)) {
			inboxReferencesMap.put(NWCGConstants.PRIMARY_CACHEID_FOR_INCIDENT, primaryCacheIdForIncident);
		} else {
			inboxReferencesMap.put(NWCGConstants.PRIMARY_CACHEID_FOR_INCIDENT, "None specified in Place Request message");
		}
		if (!incidentHasAllCodes || incidentJustNowCreated) {
			StringBuffer sb = new StringBuffer("A new Incident has been created by ICBSR as a result of processing ");
			sb.append("a PlaceResourceRequestExternalReq message from ROSS. ");
			sb.append("All new Incidents require the entry of ICBSR Account Codes: ");
			sb.append("either a Forest Service (FS) Account Code and an Override Code, ");
			sb.append("or an Other Account Code and an Override Code, or all three ");
			sb.append("FBMS values (Cost Center, Functional Area, Work Breakdown Structure) ");
			sb.append("to compose a Bureau of Land Management (BLM) Account Code.");
			inboxReferencesMap.put("Comment1", sb.toString());
		}
		String userIdToAssign = CommonUtilities.getAdminUserForCache(myEnvironment, shippingCacheFromInput);
		for (int i = 0; i < ordersCreated.size(); i++) {
			Document curDoc = ordersCreated.get(i);
			Element curDocElm = curDoc.getDocumentElement();
			String curOHK = curDocElm.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
			String curOrderNo = curDocElm.getAttribute(NWCGConstants.ORDER_NO);
			StringBuffer sb = new StringBuffer("Issue Detail Link");
			sb.append(" (");
			sb.append(new Integer(i + 1).toString());
			sb.append(" of ");
			sb.append(ordersCreated.size());
			sb.append(")");
			String issueDtlCnt = sb.toString();
			inboxReferencesMap.put(issueDtlCnt, curOHK);
			sb = new StringBuffer("Issue Number");
			sb.append(" (");
			sb.append(new Integer(i + 1).toString());
			sb.append(" of ");
			sb.append(ordersCreated.size());
			sb.append(")");
			String issueNoCnt = sb.toString();
			inboxReferencesMap.put(issueNoCnt, curOrderNo);
			String mapKey = orderNoToMapKey.get(curOrderNo);
			boolean radioQ = mapKey.endsWith("true");
			if (radioQ) {
				if (queueId.contains("SUCCESS")) {
					queueId = NWCGConstants.Q_RADIOS_SUCCESS;
				} else {
					queueId = NWCGConstants.Q_RADIOS_FAILURE;
				}
			} else {
				if (queueId.contains("SUCCESS")) {
					queueId = NWCGConstants.Q_NWCG_ISSUE_SUCCESS;
				} else {
					queueId = NWCGConstants.Q_NWCG_ISSUE_FAILURE;
				}
			}
			CommonUtilities.raiseAlertAndAssigntoUser(myEnvironment, queueId, alertDescription, userIdToAssign, null, inboxReferencesMap);
			// This will make sure that we are not adding this to a new alert
			inboxReferencesMap.remove(issueDtlCnt);
			inboxReferencesMap.remove(issueNoCnt);
		}
		// This is for new incident
		if (ordersCreated.size() < 1) {
			if (containsRadioItems) {
				if (queueId.equalsIgnoreCase(NWCGConstants.Q_NWCG_ISSUE_SUCCESS))
					queueId = NWCGConstants.Q_RADIOS_SUCCESS;
				else if (queueId.equalsIgnoreCase(NWCGConstants.Q_NWCG_ISSUE_FAILURE))
					queueId = NWCGConstants.Q_RADIOS_FAILURE;
			}
			CommonUtilities.raiseAlertAndAssigntoUser(myEnvironment, queueId, alertDescription, userIdToAssign, null, inboxReferencesMap);
		}
		if (!hasNewIncidentInboxKeySet && incidentJustNowCreated) {
			newIncidentInboxKey = inboxReferencesMap.get("NewIncidentInboxKey");
			hasNewIncidentInboxKeySet = true;
		} else if (hasNewIncidentInboxKeySet && incidentJustNowCreated) {
			incidentJustNowCreated = false;
		}
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::createIssueAlertAndAssign @@@@@");
	}
	
	/**
	 * Creates a new incident in ICBSR using the given Incident node and sub
	 * nodes in the input message
	 * 
	 * @param inDoc
	 * @throws NWCGException
	 */
	private void createNewIncident(Document inDoc) throws NWCGException {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::createNewIncident @@@@@");
		Node firstChild = inDoc.getFirstChild();
		NodeList children = firstChild.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node currentNode = children.item(i);
			if (!(currentNode instanceof Element)) {
				continue;
			}
			logger.verbose("@@@@@ currentNode.getNodeName() :: " + currentNode.getNodeName());
			//if (currentNode.getNodeName().equals(NWCGConstants.INCIDENT_ELEM) || currentNode.getLocalName().equals(NWCGConstants.INCIDENT_ELEM)) {
			if (currentNode.getNodeName().equals(NWCGConstants.INCIDENT_ELEM) || currentNode.getNodeName().contains(NWCGConstants.INCIDENT_ELEM)) {
				try {
					// This createIncidentDoc returned by the method call is a different DOM Document than from the input inDoc document
					Document createIncidentDoc = getCreateIncidentInputDoc(currentNode);
					Element newIncidentDocElm = createIncidentDoc.getDocumentElement();
					newIncidentDocElm.setAttribute(NWCGConstants.INCIDENT_AGENCY_ATTR, localContext.get(NWCGConstants.INCIDENT_AGENCY_ATTR));
					newIncidentDocElm.setAttribute(NWCGConstants.INCIDENT_CUSTOMER_NAME_ATTR, localContext.get(NWCGConstants.INCIDENT_CUSTOMER_NAME_ATTR));
					newIncidentDocElm.setAttribute(NWCGConstants.INCIDENT_CUSTOMER_TYPE, localContext.get(NWCGConstants.INCIDENT_CUSTOMER_TYPE));
					newIncidentDocElm.setAttribute(NWCGConstants.INCIDENT_DEPARTMENT_ATTR, localContext.get(NWCGConstants.INCIDENT_DEPARTMENT_ATTR));
					newIncidentDocElm.setAttribute(NWCGConstants.INCIDENT_GACC_ATTR, localContext.get(NWCGConstants.INCIDENT_GACC_ATTR));
					newIncidentDocElm.setAttribute(NWCGConstants.INCIDENT_TYPE, localContext.get(NWCGConstants.INCIDENT_TYPE));
					newIncidentDocElm.setAttribute(NWCGConstants.INCIDENT_ID, localContext.get(NWCGConstants.INCIDENT_ID));
					newIncidentDocElm.setAttribute(NWCGConstants.INCIDENT_UNIT_TYPE_ATTR, localContext.get(NWCGConstants.INCIDENT_UNIT_TYPE_ATTR));
					newIncidentDocElm.setAttribute("PersonInfoBillToKey", localContext.get("BillingPersonInfoKey"));
					newIncidentDocElm.setAttribute("PersonInfoShipToKey", localContext.get("PersonInfoShipToKey"));
					logger.verbose("@@@@@ PersonInfoShipToKey :: " + localContext.get("PersonInfoShipToKey"));
					newIncidentDocElm.setAttribute("PersonInfoDeliverToKey", localContext.get("PersonInfoDeliverToKey"));
					// Creates a new incident by first calling the SDF service NWCGUpdateIncidentNotificationXSLService
					newlyCreatedIncidentDoc = CommonUtilities.createIncident(myEnvironment, createIncidentDoc);
					String newIncOutputXML = XMLUtil.extractStringFromDocument(newlyCreatedIncidentDoc);
					logger.verbose("@@@@@ CommonUtilities.createIncident(env, Document) output: " + newIncOutputXML);
					break;
				} catch (ParserConfigurationException pce) {
					logger.error("!!!!! FAILED TO CREATE INPUT DOC for createIncident");
					logger.error("!!!!! " + NWCGConstants.NWCG_MSG_CODE_914_E_5);
					logger.error("!!!!! Caught ParserConfigurationException :: " + pce);
					throw new NWCGException(pce);
				} catch (Exception e) {
					logger.error("!!!!! FAILED TO CREATE NEW INCIDENT");
					logger.error("!!!!! " + NWCGConstants.NWCG_MSG_CODE_914_E_4);
					logger.error("!!!!! Caught General Exception :: " + e);
					throw new NWCGException(e);
				}
			}
		}
		incidentJustNowCreated = true;
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::createNewIncident @@@@@");
	}

	/**
	 * Imports the passed in Incident node <code>org.w3c.dom.Node</code> into a
	 * new document with root node name of ron:Incident where xmlsns:ron=
	 * "http://nwcg.gov/services/ross/resource_order_notification/1.1" Had to
	 * create the document with this namespace for the
	 * UpdateIncidentNotificationXSLService
	 * 
	 * @param incidentNode
	 * @return Document <ron:NWCGIncidentOrder
	 *         xmlns:ron="xmls:ns="http://nwcg.gov
	 *         /services/ross/resource_order_notification/1.1" ...
	 * @throws Exception
	 */
	private Document getCreateIncidentInputDoc(Node incidentNode) throws Exception {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::getCreateIncidentInputDoc @@@@@");
		Document createIncidentInputDoc = null;
		//incidentNode.setPrefix(null);
		try {
			createIncidentInputDoc = XMLUtil.getDocument();
			Node n = createIncidentInputDoc.importNode(incidentNode, true);
			n = createIncidentInputDoc.renameNode(n, NWCGAAConstants.RESOURCE_ORDER_NOTIFICATION_NAMESPACE, "ron:Incident");
			Element el = (Element)n;
			el.setAttribute("xmlns:ron", NWCGAAConstants.RESOURCE_ORDER_NOTIFICATION_NAMESPACE);
			createIncidentInputDoc.appendChild(n);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			throw e;
		}
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::getCreateIncidentInputDoc @@@@@");
		return createIncidentInputDoc;
	}

	/**
	 * The Incident document returned doesn't contain financial info, or
	 * incident addresses. Use NWCGGetIncidentOrderService instead for those
	 * elements to be included in the Incident
	 * 
	 * Determines whether or not the incident referenced in the input exists
	 * already in ICBSR
	 * 
	 * @param String
	 *            incidentNo
	 * @param String
	 *            incidentYear
	 * @return Document Output of NWCGGetIncidentOrderOnlyService or null if
	 *         incident doesn't exist
	 */
	private Document doesIncidentExist(String incidentNo, String incidentYear) {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::doesIncidentExist @@@@@");
		String getIncidentOrderInput = "<NWCGIncidentOrder IncidentNo=\"" + incidentNo + "\"" + " Year=\"" + incidentYear + "\"/>";
		Document apiOutputDoc = null;
		try {
			apiOutputDoc = CommonUtilities.invokeService(myEnvironment, NWCGConstants.SVC_GET_INCIDENT_ORDER_DTLS_ONLY_SVC, getIncidentOrderInput);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception: " + e);
			incidentAlreadyExists = false;
			return null;
		}
		// We get a null pointer back when the incident is not found.
		if (apiOutputDoc == null) {
			incidentAlreadyExists = false;
			return null;
		}
		incidentAlreadyExists = true;
		Element docElm = apiOutputDoc.getDocumentElement();
		localContext.put(NWCGConstants.CUST_CUST_ID_ATTR, docElm.getAttribute(NWCGConstants.CUST_CUST_ID_ATTR));
		localContext.put(NWCGConstants.INCIDENT_KEY, docElm.getAttribute(NWCGConstants.INCIDENT_KEY));
		localContext.put(NWCGConstants.INCIDENT_NAME, docElm.getAttribute(NWCGConstants.INCIDENT_NAME));
		localContext.put(NWCGConstants.INCIDENT_ID, docElm.getAttribute(NWCGConstants.INCIDENT_ID));
		localContext.put(NWCGConstants.INCIDENT_TYPE, docElm.getAttribute(NWCGConstants.INCIDENT_TYPE));
		localContext.put(NWCGConstants.INCIDENT_TEAM_TYPE, docElm.getAttribute(NWCGConstants.INCIDENT_TEAM_TYPE));
		localContext.put(NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE, docElm.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE));
		localContext.put(NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE, docElm.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE));
		localContext.put(NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE, docElm.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE));
		localContext.put(NWCGConstants.IS_ACTIVE, docElm.getAttribute(NWCGConstants.IS_ACTIVE));
		localContext.put(NWCGConstants.INCIDENT_OVERRIDECODE_ATTR, docElm.getAttribute(NWCGConstants.INCIDENT_OVERRIDECODE_ATTR));
		localContext.put(NWCGConstants.ROSS_FINANCIAL_CODE, docElm.getAttribute(NWCGConstants.ROSS_FINANCIAL_CODE));
		localContext.put(NWCGConstants.PRIMARY_CACHE_ID, docElm.getAttribute(NWCGConstants.PRIMARY_CACHE_ID));
		localContext.put(NWCGConstants.BILL_TRANS_COST_CENTER, docElm.getAttribute(NWCGConstants.BILL_TRANS_COST_CENTER));
		localContext.put(NWCGConstants.BILL_TRANS_WBS, docElm.getAttribute(NWCGConstants.BILL_TRANS_WBS));
		localContext.put(NWCGConstants.BILL_TRANS_FUNCTIONAL_AREA, docElm.getAttribute(NWCGConstants.BILL_TRANS_FUNCTIONAL_AREA));
		localContext.put("PersonInfoShipToKey", docElm.getAttribute("PersonInfoShipToKey"));
		logger.verbose("@@@@@ PersonInfoShipToKey :: " + localContext.get("PersonInfoShipToKey"));
		localContext.put("PersonInfoBillToKey", docElm.getAttribute("PersonInfoBillToKey"));
		localContext.put("PersonInfoDeliverToKey", docElm.getAttribute("PersonInfoDeliverToKey"));
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::doesIncidentExist @@@@@");
		return apiOutputDoc;
	}

	/**
	 * Populates the localContext HashMap with fields from the output of the
	 * getCustomerList API using the CustomerID associated to the incident
	 * 
	 * @throws Exception
	 */
	private void getCustomerExtnFieldsFromCustomerID() throws Exception {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::getCustomerExtnFieldsFromCustomerID @@@@@");
		String customerId = getCustomerIdFromInput();
		Document apiOutputTemplatedoc = XMLUtil.getDocument(NWCGConstants.TEMPLATE_GET_CUSTOMER_LIST);
		String apiName = NWCGConstants.API_GET_CUSTOMER_LIST;
		Document apiOutputDoc = null;
		Document getCustomerListInputDoc = XMLUtil.createDocument(NWCGConstants.CUST_CUST_ELEMENT);
		getCustomerListInputDoc.getDocumentElement().setAttribute(NWCGConstants.CUST_CUST_ID_ATTR, customerId);
		try {
			apiOutputDoc = CommonUtilities.invokeAPI(myEnvironment, apiOutputTemplatedoc, apiName, getCustomerListInputDoc);
		} catch (Exception e) {
			logger.error("!!!!! " + NWCGAAConstants.ALERT_MESSAGE_10);
			logger.error("!!!!! Caught General Exception: " + e);
			incidentHasValidBillingOrg = false;
			return;
		}
		if (apiOutputDoc != null && apiOutputDoc.getDocumentElement().getChildNodes().getLength() == 0) {
			incidentHasValidBillingOrg = false;
		} else {
			// Access the CustomerList/Customer/Extn element
			XPathWrapper pathWrapper = new XPathWrapper(apiOutputDoc);
			Element customerExtnElm = (Element) pathWrapper.getNode("/CustomerList/Customer/Extn");
			Element custConsumer = (Element) pathWrapper.getNode("/CustomerList/Customer/Consumer");
			if (customerExtnElm == null)
				return;
			localContext.put(NWCGConstants.CUST_CUST_ID_ATTR, customerId);
			// Populate the localContext HashMap w/the extended attributes we need for the createOrder API input XML
			localContext.put(NWCGConstants.INCIDENT_AGENCY_ATTR, customerExtnElm.getAttribute(NWCGConstants.CUST_AGENCY_ATTR));
			localContext.put(NWCGConstants.INCIDENT_CUSTOMER_NAME_ATTR, customerExtnElm.getAttribute(NWCGConstants.CUST_CUSTOMER_NAME_ATTR));
			localContext.put(NWCGConstants.INCIDENT_CUSTOMER_TYPE, customerExtnElm.getAttribute(NWCGConstants.CUST_CUSTOMER_TYPE));
			localContext.put(NWCGConstants.INCIDENT_DEPARTMENT_ATTR, customerExtnElm.getAttribute(NWCGConstants.CUST_DEPARTMENT_ATTR));
			localContext.put(NWCGConstants.INCIDENT_GACC_ATTR, customerExtnElm.getAttribute(NWCGConstants.CUST_GACC_ATTR));
			localContext.put(NWCGConstants.INCIDENT_UNIT_TYPE_ATTR, customerExtnElm.getAttribute(NWCGConstants.CUST_UNIT_TYPE_ATTR));
			localContext.put("ActiveFlag", customerExtnElm.getAttribute("ExtnActiveFlag"));
			if (custConsumer == null)
				return;
			localContext.put("BillingPersonInfoKey", custConsumer.getAttribute("BillingAddressKey"));
		}
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::getCustomerExtnFieldsFromCustomerID @@@@@");
	}

	/**
	 * Concatenates input
	 * root/MessageOriginator/DispatchUnitID/UnitIDPrefix+UnitIDSuffix as the
	 * Dispatch Unit ID and returns the String.
	 * 
	 * @return String DispatchUnitID/Pfx+Sfx, Sfx 0-padded to length 6
	 */
	private String getDispatchOrganizationFromInput() {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::getDispatchOrganizationFromInput @@@@@");
		// Get dispatchID out of IncidentDetails/DispatchOrganization
		IncidentReturnType incidentElement = input.getIncident();
		IncidentDetailsReturnType incidentDetailsElm = incidentElement.getIncidentDetails();
		UnitIDType moUnitId = incidentDetailsElm.getDispatchOrganization();
		String dispatchPrefix = moUnitId.getUnitIDPrefix();
		String dispatchSuffix = moUnitId.getUnitIDSuffix();
		StringBuffer dispatchOrgBuffer = new StringBuffer(dispatchPrefix);
		dispatchOrgBuffer.append(dispatchSuffix);
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::getDispatchOrganizationFromInput @@@@@");
		return dispatchOrgBuffer.toString();
	}

	/**
	 * Builds and returns a HashMap with the first and last sequence numbers for
	 * the given PlaceResourceRequestExternalReq message received. If there is
	 * only 1 line on the message, the last sequence number will be an empty
	 * string.
	 * 
	 * @return HashMap <String,String> with first and last sequence numbers in
	 *         request bundle
	 */
	private HashMap<String, String> getFirstAndLastRequestNumbers() {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::getFirstAndLastRequestNumbers @@@@@");
		HashMap<String, String> firstAndLast = new HashMap<String, String>(2);
		ArrayList<ResourceRequestCreateType> al = (ArrayList<ResourceRequestCreateType>) input.getRequest();
		String firstSeqNumber = NWCGConstants.EMPTY_STRING, lastSeqNumber = NWCGConstants.EMPTY_STRING;
		int numberOfRequestLines = al.size();
		if (numberOfRequestLines == 1) {
			ResourceRequestCreateType firstRequest = al.get(0);
			firstSeqNumber = firstRequest.getSequenceNumber();
			lastSeqNumber = firstSeqNumber;
		} else if (numberOfRequestLines > 1) {
			ResourceRequestCreateType firstRequest = al.get(0);
			firstSeqNumber = firstRequest.getSequenceNumber();
			ResourceRequestCreateType lastRequest = al.get(al.size() - 1);
			lastSeqNumber = lastRequest.getSequenceNumber();
		}
		firstAndLast.put("first", firstSeqNumber);
		firstAndLast.put("last", lastSeqNumber);
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::getFirstAndLastRequestNumbers @@@@@");
		return firstAndLast;
	}

	/**
	 * 
	 * @param rossItemDesc
	 * @return
	 * @throws Exception
	 */
	private HashMap<String, String> getICBSRItemWithROSSItemDesc(String rossItemDesc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::getICBSRItemWithROSSItemDesc @@@@@");
		HashMap<String, String> item = new HashMap<String, String>();
		Document getItemListInputDoc = XMLUtil.createDocument(NWCGConstants.ITEM);
		Element primaryInfoElm = getItemListInputDoc.createElement(NWCGConstants.PRIMARY_INFORMATION);
		primaryInfoElm.setAttribute(NWCGConstants.SHORT_DESCRIPTION, rossItemDesc);
		getItemListInputDoc.getDocumentElement().appendChild(primaryInfoElm);
		Document getItemListOutput = CommonUtilities.invokeAPI(myEnvironment, NWCGConstants.GET_ITEM_LIST_OP_FILENAME, NWCGConstants.API_GET_ITEM_LIST, getItemListInputDoc);
		NodeList nl = getItemListOutput.getDocumentElement().getElementsByTagName(NWCGConstants.ITEM);
		if (nl.getLength() == 0) {
			// Item Not Found: create negative response to ROSS and create an alert
			StringBuffer sb = new StringBuffer("Invalid CatalogItemDescription. Item ");
			sb.append(rossItemDesc);
			sb.append(" not found in ICBSR");
			String responseMessage = sb.toString();
			createIssueAlertAndAssign(NWCGAAConstants.QUEUEID_ISSUE_FAILURE, responseMessage);
			postResponseToROSS(NWCGAAConstants.ROSS_RET_FAILURE_VALUE, NWCGAAConstants.DELIVER_OPERATION_RESULTS_SEVERITY_1, responseMessage, NWCGConstants.NWCG_MSG_CODE_914_E_2, false);
			java.sql.Connection conn = ((YFSConnectionHolder) myEnvironment).getDBConnection();
			myEnvironment.setRollbackOnly(false);
			conn.commit();
			// Need to commit what we've done Before we toss an exception out of the IB JVM thread
			throw new NWCGException("NWCG_ISSUE_WHILE_PREPARING_CREATE_ORDER_INPUT: " + rossItemDesc);
		} else if (nl.getLength() > 1) {
			// Multiple Items found for a given CatalogItemDescription
			StringBuffer sb = new StringBuffer("Multiple ICBSR items exist for CatalogItemDescription: ");
			sb.append(rossItemDesc);
			String responseMessage = sb.toString();
			postResponseToROSS(NWCGAAConstants.ROSS_RET_FAILURE_VALUE, NWCGAAConstants.DELIVER_OPERATION_RESULTS_SEVERITY_1, responseMessage, NWCGConstants.NWCG_MSG_CODE_914_E_4, false);
			createIssueAlertAndAssign(NWCGAAConstants.QUEUEID_ISSUE_FAILURE, responseMessage);
			java.sql.Connection conn = ((YFSConnectionHolder) myEnvironment).getDBConnection();
			myEnvironment.setRollbackOnly(false);
			conn.commit();
			throw new NWCGException("NWCG_ISSUE_WHILE_PREPARING_CREATE_ORDER_INPUT: " + rossItemDesc);
		} else if (nl.getLength() == 1) {
			Node itemNode = nl.item(0);
			Element itemElem = (itemNode instanceof Element) ? (Element) itemNode : null;
			if (itemElem != null) {
				String strItemId = itemElem.getAttribute(NWCGConstants.ITEM_ID);
				String strUOM = itemElem.getAttribute(NWCGConstants.UNIT_OF_MEASURE);
				item.put(NWCGConstants.ITEM_ID, strItemId);
				item.put(NWCGConstants.ITEM_KEY, itemElem.getAttribute(NWCGConstants.ITEM_KEY));
				item.put(NWCGConstants.ORGANIZATION_CODE, itemElem.getAttribute(NWCGConstants.ORGANIZATION_CODE));
				item.put(NWCGConstants.UNIT_OF_MEASURE, strUOM);
				Element itemPrimaryInfoElem = (Element) itemElem.getFirstChild();
				String strPC = itemPrimaryInfoElem.getAttribute(NWCGConstants.DEFAULT_PRODUCT_CLASS);
				item.put(NWCGConstants.DEFAULT_PRODUCT_CLASS, strPC);
			}
		}
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::getICBSRItemWithROSSItemDesc @@@@@");
		return item;
	}

	/**
	 * This method will get the RFI quantity for the item using
	 * CommonUtilities.getAvailableRFIQty
	 * 
	 * @param env
	 * @param hmItem
	 * @return
	 */
	private String getRFIQty(YFSEnvironment env, HashMap<String, String> hmItem) {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::getRFIQty @@@@@");
		String rfiQty = "0";
		String itemID = hmItem.get(NWCGConstants.ITEM_ID);
		String uom = hmItem.get(NWCGConstants.UNIT_OF_MEASURE);
		String prodClass = hmItem.get(NWCGConstants.DEFAULT_PRODUCT_CLASS);
		String shipNode = getShippingCacheIdFromInput();
		String itemDesc = hmItem.get(NWCGConstants.SHORT_DESCRIPTION);
		try {
			Document docRFIQty = CommonUtilities.getAvailableRFIQty(env, itemID, uom, prodClass, shipNode, itemDesc);
			if (docRFIQty != null) {
				rfiQty = docRFIQty.getDocumentElement().getAttribute(NWCGConstants.AVAILABLE_QUANTITY);
			} else {
				logger.verbose("@@@@@ RFI call returned null document");
			}
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);
		}
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::getRFIQty @@@@@");
		return rfiQty;
	}

	/**
	 * Gets the incident number from the input message by concatenating
	 * PlaceResourceRequestExternalReq
	 * /Incident/IncidentNumber/HostID/UnitIDPrefix, UnitIDSuffix,
	 * PlaceResourceRequestExternalReq/Incident/SequenceNumber with hyphens
	 * 
	 * @return String in VVV-YYY-ZZZ form of incident number, e.g.:
	 *         MN-MNS-080008
	 */
	private HashMap<String, String> getIncidentInfoMapFromInput() {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::getIncidentInfoMapFromInput @@@@@");
		String incidentNo = getIncidentNumberFromInput();
		String incidentYear = getIncidentYearFromInput();
		String incidentId = getIncidentIdFromInput();
		String dispatchUnitId = getDispatchOrganizationFromInput();
		String incidentType = getIncidentTypeFromInput();
		HashMap<String, String> retMap = new HashMap<String, String>();
		localContext.put(NWCGConstants.INCIDENT_NO_ATTR, incidentNo);
		localContext.put(NWCGConstants.BILL_TRANS_INCIDENT_YEAR, incidentYear);
		localContext.put("DispatchUnitId", dispatchUnitId);
		localContext.put(NWCGConstants.INCIDENT_TYPE, incidentType);
		localContext.put(NWCGConstants.INCIDENT_ID, incidentId);
		retMap.put(NWCGConstants.INCIDENT_NO_ATTR, incidentNo);
		retMap.put(NWCGConstants.BILL_TRANS_INCIDENT_YEAR, incidentYear);
		retMap.put("DispatchUnitId", dispatchUnitId);
		retMap.put(NWCGConstants.INCIDENT_ID, incidentId);
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::getIncidentInfoMapFromInput @@@@@");
		return retMap;
	}

	/**
	 * Retrieves IncidentDetails/IncidentType
	 * 
	 * @return String IncidentType
	 */
	private String getIncidentTypeFromInput() {
		logger.verbose("@@@@@ In NWCGPlaceResourceRequestExternalHandler::getIncidentTypeFromInput @@@@@");
		IncidentReturnType incidentElement = input.getIncident();
		IncidentDetailsReturnType incidentDetailsElm = incidentElement.getIncidentDetails();
		return incidentDetailsElm.getIncidentType();
	}

	/**
	 * Returns the incident number of the input root/Incident element by
	 * concatenating root/Incident/IncidentKey/NaturalIncidentKey/HostID/
	 * UnitIDPrefix-UnitIDSuffix-SequenceNumber Note: Sequence number is padded
	 * with 0's to reach a length of 6.
	 * 
	 * @return
	 */
	private String getIncidentNumberFromInput() {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::getIncidentNumberFromInput @@@@@");
		// Get element data out of root/Incident/IncidentKey/NaturalIncidentKey
		IncidentReturnType incidentObj = input.getIncident();
		CompositeIncidentKeyType incidentKeyType = incidentObj.getIncidentKey();
		IncidentNumberType naturalIncidentKey = incidentKeyType.getNaturalIncidentKey();
		UnitIDType unitIDObj = naturalIncidentKey.getHostID();
		String unitIdPrefix = unitIDObj.getUnitIDPrefix();
		String unitIdSuffix = unitIDObj.getUnitIDSuffix();
		// Sequence Number 5 will get padded to = 000005 for a length of 6
		int sequenceNo = naturalIncidentKey.getSequenceNumber();
		String paddedSequenceNo = Integer.toString(sequenceNo);
		paddedSequenceNo = StringUtil.prepadStringWithZeros(paddedSequenceNo, 6);
		StringBuffer sb = new StringBuffer(unitIdPrefix);
		sb.append(NWCGConstants.DASH);
		sb.append(unitIdSuffix);
		sb.append(NWCGConstants.DASH);
		sb.append(paddedSequenceNo);
		String incidentNo = sb.toString();
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::getIncidentNumberFromInput @@@@@");
		return incidentNo;
	}

	/**
	 * Returns root/Incident/IncidentKey/NaturalIncidentKey/YearCreated as the
	 * Incident Year.
	 * 
	 * @return String Incident Year
	 */
	private String getIncidentYearFromInput() {
		logger.verbose("@@@@@ In NWCGPlaceResourceRequestExternalHandler::getIncidentYearFromInput @@@@@");
		IncidentReturnType incidentObj = input.getIncident();
		CompositeIncidentKeyType compositeIncident = incidentObj.getIncidentKey();
		IncidentNumberType incidentNumberObj = compositeIncident.getNaturalIncidentKey();
		String incidentYear = incidentNumberObj.getYearCreated();
		return incidentYear;
	}

	private String getIncidentIdFromInput() {
		logger.verbose("@@@@@ In NWCGPlaceResourceRequestExternalHandler::getIncidentIdFromInput @@@@@");
		IncidentReturnType incidentObj = input.getIncident();
		CompositeIncidentKeyType compositeIncident = incidentObj.getIncidentKey();
		IDType incidentIdObj = compositeIncident.getIncidentID();
		String incidentId = incidentIdObj.getEntityID();
		return incidentId;
	}

	/**
	 * Returns the Owner Agency for the given shipping cache ID by calling
	 * getOrganizationList API
	 * 
	 * @param shippingCacheId
	 * @return null if nothing is found at /OrganizationList/Organization/Extn/@ExtnOwnerAgency
	 *         in the output of getOrganizationList
	 */
	private String getOwnerAgencyFromShippingCacheID(String shippingCacheId) {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::getOwnerAgencyFromShippingCacheID @@@@@");
		String getOrganizationListInput = "<Organization OrganizationCode=\"" + shippingCacheId + "\"/>";
		Document apiOutputDoc = null;
		try {
			apiOutputDoc = CommonUtilities.invokeAPI(myEnvironment, "CommonUtilities_getOrganizationList.xml", NWCGConstants.API_GET_ORG_LIST, getOrganizationListInput);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception: " + e);
			logger.error("!!!!! Exiting NWCGPlaceResourceRequestExternalHandler::getOwnerAgencyFromShippingCacheID (1) !!!!!");
			return NWCGConstants.EMPTY_STRING;
		}
		if (apiOutputDoc == null) {
			logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::getOwnerAgencyFromShippingCacheID (2) @@@@@");
			return NWCGConstants.EMPTY_STRING;
		}
		XPathWrapper pathWrapper = new XPathWrapper(apiOutputDoc);
		String extnOwnerAgency = NWCGConstants.EMPTY_STRING;
		try {
			extnOwnerAgency = pathWrapper.getAttribute("/OrganizationList/Organization/Extn/@ExtnOwnerAgency");
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception: " + e);
		}
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::getOwnerAgencyFromShippingCacheID (3) @@@@@");
		return extnOwnerAgency;
	}

	/**
	 * Pulls the Primary Cache ID value for an Incident from the Place Resource
	 * Request message.
	 * 
	 * @return root/Incident/IncidentDetails/CacheOrganization/UnitIDPrefix+
	 *         UnitIDSuffix
	 */
	private String getPrimaryCacheForIncident(String incidentNo, String incidentYear) {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::getPrimaryCacheForIncident @@@@@");
		IncidentReturnType incidentElement = input.getIncident();
		IncidentDetailsReturnType incidentDetailsElm = incidentElement.getIncidentDetails();
		String retVal = NWCGConstants.EMPTY_STRING;
		if (incidentDetailsElm.isSetCacheOrganization()) {
			UnitIDType placeToUnidId = incidentDetailsElm.getCacheOrganization();
			StringBuffer defaultPrimeCacheIdsb = new StringBuffer();
			defaultPrimeCacheIdsb.append(placeToUnidId.getUnitIDPrefix());
			defaultPrimeCacheIdsb.append(placeToUnidId.getUnitIDSuffix());
			retVal = defaultPrimeCacheIdsb.toString();
		} else {
			retVal = getShippingCacheIdFromInput();
		}
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::getPrimaryCacheForIncident @@@@@");
		return retVal;
	}

	/**
	 * Pulls the shipping cache ID value from xpath in input msg.
	 * 
	 * @return root/PlaceToUnitID/UnitIDPrefix+UnitIDSuffix
	 */
	private String getShippingCacheIdFromInput() {
		logger.verbose("@@@@@ In NWCGPlaceResourceRequestExternalHandler::getShippingCacheIdFromInput @@@@@");
		// Get shipping cache ID by concatenating root/PlaceToUnitID/UnitIDPrefix and UnitIDSuffix
		UnitIDType placeToUnidId = input.getPlaceToUnitID();
		StringBuffer cacheId = new StringBuffer();
		cacheId.append(placeToUnidId.getUnitIDPrefix());
		cacheId.append(placeToUnidId.getUnitIDSuffix());
		return cacheId.toString();
	}

	/**
	 * Determines whether or not there exists at least one instance of either
	 * PlaceResourceRequestExternalReq/Request/ShippingAddress
	 * PlaceResourceRequestExternalReq/Request/WillPickUpInfo or
	 * PlaceResourceRequestExternalReq/Request/ShippingInstructions for the
	 * first request line in the input message
	 * 
	 * @return true if at least one shipping method/info is specified on the
	 *         first request line
	 */
	private boolean isValidShippingInfoGiven() {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::isValidShippingInfoGiven @@@@@");
		boolean isValid = false;
		ArrayList<ResourceRequestCreateType> al = (ArrayList<ResourceRequestCreateType>) input.getRequest();
		if (al.size() > 0) {
			ResourceRequestCreateType curRequest = al.get(0);
			// Check to see if at least 1 of the following elements exists: Request/ShippingAddress or Request/ShippingInstructions or Request/WillPickUpInfo
			if (curRequest.isSetShippingAddress() || curRequest.isSetWillPickUpInfo() || curRequest.isSetShippingInstructions()) {
				isValid = true;
			}
		}
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::isValidShippingInfoGiven @@@@@");
		return isValid;
	}

	/**
	 * Calls changeOrder for all of the newly created issues in order to apply
	 * the given hold type
	 * 
	 * @param holdType
	 */
	private void placeNewIssueOnHoldWithHoldType(String holdType) {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::placeNewIssueOnHoldWithHoldType @@@@@");
		final String methodName = "placeNewIssueOnHoldWithHoldType";
		try {
			for (int i = 0; i < ordersCreated.size(); i++) {
				Document curDoc = ordersCreated.get(i);
				Element curDocElm = curDoc.getDocumentElement();
				Document changeOrderIp = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
				Element chgOrderDocElm = changeOrderIp.getDocumentElement();
				Element chgOrderExtnElm = changeOrderIp.createElement(NWCGConstants.EXTN_ELEMENT);
				chgOrderDocElm.appendChild(chgOrderExtnElm);
				String curOHK = curDocElm.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
				chgOrderDocElm.setAttribute(NWCGConstants.ORDER_HEADER_KEY, curOHK);
				chgOrderDocElm.setAttribute(NWCGConstants.OVERRIDE, NWCGConstants.YES);
				Element orderHoldTypes = changeOrderIp.createElement(NWCGConstants.ORDER_HOLD_TYPES);
				Element orderHoldType = changeOrderIp.createElement(NWCGConstants.ORDER_HOLD_TYPE);
				chgOrderDocElm.appendChild(orderHoldTypes);
				orderHoldTypes.appendChild(orderHoldType);
				orderHoldType.setAttribute(NWCGConstants.HOLD_TYPE_ATTR, holdType);
				orderHoldType.setAttribute(NWCGConstants.STATUS_ATTR, "1100");
				logger.verbose("@@@@@ Input to changeOrder: ");
				logger.verbose("@@@@@ " + XMLUtil.extractStringFromDocument(changeOrderIp));
				CommonUtilities.invokeAPI(myEnvironment, NWCGConstants.API_CHANGE_ORDER, changeOrderIp);
			}
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
		}
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::placeNewIssueOnHoldWithHoldType @@@@@");
	}

	/**
	 * 
	 * @param incidentNo
	 * @param incidentYear
	 */
	private void populateLocalContextWithNewIncidentInfo(String incidentNo, String incidentYear) {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::populateLocalContextWithNewIncidentInfo @@@@@");
		String getIncidentOrderInput = "<NWCGIncidentOrder IncidentNo=\"" + incidentNo + "\"" + " Year=\"" + incidentYear + "\"/>";
		Document apiOutputDoc = null;
		try {
			apiOutputDoc = CommonUtilities.invokeService(myEnvironment, NWCGConstants.SVC_GET_INCIDENT_ORDER_DTLS_ONLY_SVC, getIncidentOrderInput);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
		}
		// We get a null pointer back when the incident is not found.
		if (apiOutputDoc != null) {
			Element docElm = apiOutputDoc.getDocumentElement();
			localContext.put(NWCGConstants.CUST_CUST_ID_ATTR, docElm.getAttribute(NWCGConstants.CUST_CUST_ID_ATTR));
			localContext.put(NWCGConstants.INCIDENT_KEY, docElm.getAttribute(NWCGConstants.INCIDENT_KEY));
			localContext.put(NWCGConstants.INCIDENT_NAME, docElm.getAttribute(NWCGConstants.INCIDENT_NAME));
			localContext.put(NWCGConstants.INCIDENT_TYPE, docElm.getAttribute(NWCGConstants.INCIDENT_TYPE));
			localContext.put(NWCGConstants.INCIDENT_ID, docElm.getAttribute(NWCGConstants.INCIDENT_ID));
			localContext.put(NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE, docElm.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE));
			localContext.put(NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE, docElm.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE));
			localContext.put(NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE, docElm.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE));
			localContext.put(NWCGConstants.IS_ACTIVE, docElm.getAttribute(NWCGConstants.IS_ACTIVE));
			localContext.put(NWCGConstants.INCIDENT_OVERRIDECODE_ATTR, docElm .getAttribute(NWCGConstants.INCIDENT_OVERRIDECODE_ATTR));
			localContext.put(NWCGConstants.ROSS_FINANCIAL_CODE, docElm.getAttribute(NWCGConstants.ROSS_FINANCIAL_CODE));
			localContext.put(NWCGConstants.PRIMARY_CACHE_ID, docElm.getAttribute(NWCGConstants.PRIMARY_CACHE_ID));
		}
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::populateLocalContextWithNewIncidentInfo @@@@@");
	}

	/**
	 * 
	 */
	private void updateNewIncidentAlertWithIssueRefs() {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::updateNewIncidentAlertWithIssueRefs @@@@@");
		final String methodName = "updateNewIncidentAlertWithIssueRefs";
		if (StringUtil.isEmpty(newIncidentInboxKey) || ordersCreated == null || ordersCreated.size() < 1) {
			logger.verbose("@@@@@ newIncidentInboxKey or ordersCreated is empty!!");
			logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::updateNewIncidentAlertWithIssueRefs (1) @@@@@");
			return;
		}
		StringBuffer sb = new StringBuffer("<Inbox InboxKey=\"" + newIncidentInboxKey + "\">");
		sb.append("<InboxReferencesList>");
		int numOrdersCreated = ordersCreated.size();
		for (int i = 0; i < ordersCreated.size(); i++) {
			Document curDoc = ordersCreated.get(i);
			Element curDocElm = curDoc.getDocumentElement();
			String curOHK = curDocElm.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
			String curOrderNo = curDocElm.getAttribute(NWCGConstants.ORDER_NO);
			sb.append("<InboxReferences Name=\"" + NWCGConstants.ORDER_HEADER_KEY);
			sb.append(" (");
			sb.append(new Integer(i + 1).toString());
			sb.append(" of ");
			sb.append(numOrdersCreated);
			sb.append(")\"");
			sb.append(" Value=\"" + curOHK + "\" ReferenceType=\"TEXT\"/>");
			sb.append("<InboxReferences Name=\"Issue Number");
			sb.append(" (");
			sb.append(new Integer(i + 1).toString());
			sb.append(" of ");
			sb.append(numOrdersCreated);
			sb.append(")\"");
			sb.append(" Value=\"" + curOrderNo + "\" ReferenceType=\"TEXT\"/>");
		}
		sb.append("</InboxReferencesList></Inbox>");
		String updateExceptionReferences = sb.toString();
		Document ip = null;
		try {
			ip = XMLUtil.getDocument(updateExceptionReferences);
			CommonUtilities.invokeAPI(myEnvironment, "changeException", ip);
		} catch (ParserConfigurationException e) {
			logger.error("!!!!! Caught ParserConfigurationException Exception: " + e);
		} catch (SAXException e) {
			logger.error("!!!!! Caught SAXException Exception: " + e);
		} catch (IOException e) {
			logger.error("!!!!! Caught IOException Exception: " + e);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception: " + e);
		}
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::updateNewIncidentAlertWithIssueRefs (2) @@@@@");
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void generateOrdersToCreateMap() {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::generateOrdersToCreateMap @@@@@");
		ArrayList<ResourceRequestCreateType> al = (ArrayList<ResourceRequestCreateType>) input.getRequest();
		ResourceRequestCreateType curRqst = null;
		for (int i = 0; i < al.size(); i++) {
			Object o = al.get(i);
			String shipContactName = null, shipContactPhone = null, replIndic = null;
			curRqst = (o instanceof ResourceRequestCreateType) ? (ResourceRequestCreateType) o : null;
			if (curRqst == null)
				continue;
			// Get the Request/ShippingContactName
			shipContactName = curRqst.getShippingContactName().toUpperCase();
			// Get the Request/ShippingContactPhone
			shipContactPhone = curRqst.getShippingContactPhone().toUpperCase();
			// Get the Request/ReplacementInd boolean and store a string for this request as either Y or N to be used in the ordersToCreate hashmap, as the 3rd part of the key.
			Boolean isReplacementRqst = curRqst.isReplacementInd();
			if (isReplacementRqst != null) {
				replIndic = isReplacementRqst.booleanValue() ? NWCGConstants.YES : NWCGConstants.NO;
			} else {
				// Assuming no
				replIndic = NWCGConstants.NO;
			}
			// The concatenation of the 3 fields will comprise the key for the orderToCreate HashMap<String, Vector> Jay: temorary fix to avoid null pointer exception
			if (shipContactName == null)
				shipContactName = NWCGConstants.EMPTY_STRING;
			StringBuffer sb = new StringBuffer(shipContactName);
			if (shipContactPhone == null)
				shipContactPhone = NWCGConstants.EMPTY_STRING;
			sb.append(shipContactPhone);
			if (replIndic == null)
				replIndic = NWCGConstants.EMPTY_STRING;
			sb.append(replIndic);
			String shortDesc = "";
			if (curRqst.isSetRequestedItemKey()) {
				shortDesc = curRqst.getRequestedItemKey().getCatalogItemName();
			} else if (curRqst.isSetRequestedCatalogItem()) {
				shortDesc = curRqst.getRequestedCatalogItem().getCatalogItemDescription();
			}
			boolean radioItem = isRadioItem(shortDesc);
			sb.append(radioItem);
			String mapKey = sb.toString();
			if (!ordersToCreate.containsKey(mapKey)) {
				Vector<ResourceRequestCreateType> v = new Vector<ResourceRequestCreateType>();
				v.add(curRqst);
				ordersToCreate.put(mapKey, v);
			} else {
				Vector<ResourceRequestCreateType> v = ordersToCreate.get(mapKey);
				v.add(curRqst);
			}
		}
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::generateOrdersToCreateMap @@@@@");
	}

	/**
	 * Calculates the number of request lines in this particular message
	 * 
	 * @return int Number of request lines in this bundle / issue / order
	 */
	private int getNumberRequestLines() {
		logger.verbose("@@@@@ In NWCGPlaceResourceRequestExternalHandler::getNumberRequestLines @@@@@");
		return !input.isSetRequest() ? 0 : input.getRequest().size();
	}

	/**
	 * This method populates Order/Extn/@ExtnShippingInstructions with
	 * Request/ShippingInstructions/ShippingInstructions
	 * Order/Extn/@ExtnShipInstrCity and State come from city/state in the input
	 * ResourceRequestCreateType object
	 * 
	 * @param orderExtnElm
	 * @param curRequest
	 */
	private void setExtnShippingInstructions(Element orderExtnElm, ResourceRequestCreateType curRequest) {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::setExtnShippingInstructions @@@@@");
		// Here's where we'll populate Order/Extn/@ExtnShippingInstructions with Request/ShippingInstructions/ShippingInstructions Order/Extn/@ExtnShipInstrCity and State come from city/state in the input Request/ShippingInstructions element
		ShippingInstructionsCreateType shipInstrElm = curRequest.getShippingInstructions();
		String shippingInstructionText = shipInstrElm.getShippingInstructions();
		String shipInstrCity = shipInstrElm.getCity();
		String shipInstrState = shipInstrElm.getState();
		orderExtnElm.setAttribute(NWCGConstants.EXTN_SHIPPING_INSTRUCTIONS_ATTR, shippingInstructionText);
		orderExtnElm.setAttribute(NWCGConstants.EXTN_SHIPPING_INSTR_CITY_ATTR, shipInstrCity);
		orderExtnElm.setAttribute(NWCGConstants.EXTN_SHIPPING_INSTR_STATE_ATTR, shipInstrState);
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::setExtnShippingInstructions @@@@@");
	}

	/**
	 * 
	 * @param docElm
	 */
	private void setOrderElementAttributes(Element docElm) {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::setOrderElementAttributes @@@@@");
		docElm.setAttribute(NWCGConstants.DOCUMENT_TYPE, NWCGConstants.ORDER_DOCUMENT_TYPE);
		docElm.setAttribute(NWCGConstants.BILLTO_ID_ATTR, getROSSBillingOrgFromInput());
		docElm.setAttribute(NWCGConstants.ENTERPRISE_CODE_STR, NWCGConstants.ENTERPRISE_CODE);
		docElm.setAttribute(NWCGConstants.SELLER_ORGANIZATION_CODE, NWCGConstants.ENTERPRISE_CODE);
		docElm.setAttribute(NWCGConstants.INCIDENT_KEY, localContext.get(NWCGConstants.INCIDENT_KEY));
		docElm.setAttribute(NWCGConstants.DRAFT_ORDER_FLAG, NWCGConstants.YES);
		docElm.setAttribute(NWCGConstants.SHIP_NODE, getShippingCacheIdFromInput());
		docElm.setAttribute("ModificationReasonCode", "Auto Update from ROSS");
		docElm.setAttribute("ModificationReasonText", "ROSS initiated Issue, customer: " + getROSSBillingOrgFromInput());
		docElm.setAttribute("ModificationReference1", "Time: " + CommonUtilities.getXMLCurrentTime());
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::setOrderElementAttributes @@@@@");
	}

	/**
	 * 
	 * @param orderExtnElm
	 */
	private void setOrderExtnElementAttributes(Element orderExtnElm) {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::setOrderExtnElementAttributes @@@@@");
		// Set customer fields on order header
		String shippingCacheId = getShippingCacheIdFromInput();
		orderExtnElm.setAttribute(NWCGConstants.CUST_AGENCY_ATTR, localContext.get(NWCGConstants.INCIDENT_AGENCY_ATTR));
		orderExtnElm.setAttribute(NWCGConstants.CUST_CUSTOMER_NAME_ATTR, localContext.get(NWCGConstants.INCIDENT_CUSTOMER_NAME_ATTR));
		orderExtnElm.setAttribute(NWCGConstants.CUST_CUSTOMER_TYPE, localContext.get(NWCGConstants.INCIDENT_CUSTOMER_TYPE));
		orderExtnElm.setAttribute(NWCGConstants.CUST_DEPARTMENT_ATTR, localContext.get(NWCGConstants.INCIDENT_DEPARTMENT_ATTR));
		orderExtnElm.setAttribute(NWCGConstants.CUST_GACC_ATTR, localContext.get(NWCGConstants.INCIDENT_GACC_ATTR));
		orderExtnElm.setAttribute(NWCGConstants.CUST_UNIT_TYPE_ATTR, localContext.get(NWCGConstants.INCIDENT_UNIT_TYPE_ATTR));
		orderExtnElm.setAttribute(NWCGConstants.INCIDENT_NO, localContext.get(NWCGConstants.INCIDENT_NO_ATTR));
		orderExtnElm.setAttribute(NWCGConstants.INCIDENT_YEAR, localContext.get(NWCGConstants.BILL_TRANS_INCIDENT_YEAR));
		orderExtnElm.setAttribute(NWCGConstants.INCIDENT_NAME_ATTR, localContext.get(NWCGConstants.INCIDENT_NAME));
		orderExtnElm.setAttribute(NWCGConstants.INCIDENT_TYPE_ATTR, localContext.get(NWCGConstants.INCIDENT_TYPE));
		orderExtnElm.setAttribute(NWCGConstants.INCIDENT_TEAM_TYPE_ATTR, localContext.get(NWCGConstants.INCIDENT_TEAM_TYPE));
		orderExtnElm.setAttribute(NWCGConstants.INCIDENT_CACHE_ID, shippingCacheId);
		orderExtnElm.setAttribute(NWCGConstants.EXTN_SYSTEM_OF_ORIGIN, NWCGConstants.ROSS_SYSTEM);
		orderExtnElm.setAttribute(NWCGConstants.INCIDENT_ROSS_BILLING_ORG, getROSSBillingOrgFromInput());
		orderExtnElm.setAttribute(NWCGConstants.EXTN_ROSS_FIN_CODE_ATTR, localContext.get(NWCGConstants.ROSS_FINANCIAL_CODE));
		orderExtnElm.setAttribute(NWCGConstants.EXTN_ROSS_DISPATCH_UNIT_ID, localContext.get("DispatchUnitId"));
		orderExtnElm.setAttribute(NWCGConstants.FBMS_FUNCTIONALAREA_EXTN_ATTR, localContext.get(NWCGConstants.BILL_TRANS_FUNCTIONAL_AREA));
		orderExtnElm.setAttribute(NWCGConstants.FBMS_WBS_EXTN_ATTR, localContext.get(NWCGConstants.BILL_TRANS_WBS));
		orderExtnElm.setAttribute(NWCGConstants.FBMS_COSTCENTER_EXTN_ATTR, localContext.get(NWCGConstants.BILL_TRANS_COST_CENTER));
		String fsAccountCode = localContext.get(NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE);
		String blmAccountCode = localContext.get(NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE);
		String otherAccountCode = localContext.get(NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE);
		// shipacctcode, use shipping cache id as the lookup into organization table to get the owner agency
		if (StringUtil.isEmpty(ownerAgency)) {
			ownerAgency = getOwnerAgencyFromShippingCacheID(shippingCacheId);
		}
		if (!StringUtil.isEmpty(fsAccountCode) && ownerAgency.equalsIgnoreCase(NWCGConstants.FS_OWNER_AGENY)) {
			orderExtnElm.setAttribute(NWCGConstants.EXTN_FS_ACCT_CODE, fsAccountCode);
			orderExtnElm.setAttribute(NWCGConstants.EXTN_SHIP_ACCT_CODE, fsAccountCode);
		} else {
			// if we did get back null from the localContext, we want to make sure we set the fs account code as an empty string for use below as the override code.
			fsAccountCode = NWCGConstants.EMPTY_STRING;
		}
		if (!StringUtil.isEmpty(blmAccountCode) && (blmAccountCode.length() > 3) && ownerAgency.equalsIgnoreCase(NWCGConstants.BLM_OWNER_AGENCT)) {
			orderExtnElm.setAttribute(NWCGConstants.EXTN_BLM_ACCT_CODE, blmAccountCode);
			orderExtnElm.setAttribute(NWCGConstants.EXTN_SHIP_ACCT_CODE, blmAccountCode);
			orderExtnElm.setAttribute(NWCGConstants.FBMS_FUNCTIONALAREA_EXTN_ATTR, localContext.get(NWCGConstants.BILL_TRANS_FUNCTIONAL_AREA));
			orderExtnElm.setAttribute(NWCGConstants.FBMS_WBS_EXTN_ATTR, localContext.get(NWCGConstants.BILL_TRANS_WBS));
			orderExtnElm.setAttribute(NWCGConstants.FBMS_COSTCENTER_EXTN_ATTR, localContext.get(NWCGConstants.BILL_TRANS_COST_CENTER));
		}
		if (!StringUtil.isEmpty(otherAccountCode) && ownerAgency.equalsIgnoreCase(NWCGConstants.OTHER_OWNER_AGENY)) {
			orderExtnElm.setAttribute(NWCGConstants.EXTN_OTHER_ACCT_CODE, otherAccountCode);
			orderExtnElm.setAttribute(NWCGConstants.EXTN_SHIP_ACCT_CODE, otherAccountCode);
		}
		// if the override code is present on the incident, use that override code. otherwise override code will always be the same as fs acct code, irrespective of owner agency if the fs assct code is blank, then the override code will be blank
		String overrideCode = localContext.get(NWCGConstants.INCIDENT_OVERRIDECODE_ATTR);
		if (!StringUtil.isEmpty(overrideCode)) {
			orderExtnElm.setAttribute(NWCGConstants.EXTN_OVERRIDE_CODE, overrideCode);
			orderExtnElm.setAttribute(NWCGConstants.EXTN_SA_OVERRIDE_CODE, overrideCode);
		} else {
			orderExtnElm.setAttribute(NWCGConstants.EXTN_OVERRIDE_CODE, fsAccountCode);
			orderExtnElm.setAttribute(NWCGConstants.EXTN_SA_OVERRIDE_CODE, fsAccountCode);
		}
		// shipacctcode, use shipping cache id as the lookup into organization table to get the owner agency
		shippingCacheId = getShippingCacheIdFromInput();
		String ownerAgency = getOwnerAgencyFromShippingCacheID(shippingCacheId);
		if (!StringUtil.isEmpty(ownerAgency)) {
			if (ownerAgency.equals(NWCGConstants.BLM_OWNER_AGENCT)) {
				orderExtnElm.setAttribute(NWCGConstants.EXTN_SHIP_ACCT_CODE, blmAccountCode);
			} else if (ownerAgency.equals(NWCGConstants.FS_OWNER_AGENY)) {
				orderExtnElm.setAttribute(NWCGConstants.EXTN_SHIP_ACCT_CODE, fsAccountCode);
			} else {
				orderExtnElm.setAttribute(NWCGConstants.EXTN_SHIP_ACCT_CODE, otherAccountCode);
			}
		}
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::setOrderExtnElementAttributes @@@@@");
	}

	/**
	 * 
	 * @param docElm
	 */
	private void setOrderHeaderDates(Element docElm) {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::setOrderHeaderDates @@@@@");
		Document ownerDoc = docElm.getOwnerDocument();
		Element orderDates = ownerDoc.createElement("OrderDates");
		Element orderDate = ownerDoc.createElement("OrderDate");
		String currentXMLdateTime = CommonUtilities.getXMLCurrentTime();
		orderDate.setAttribute("DateTypeId", "NWCG_DATE");
		orderDate.setAttribute(NWCGConstants.ACTUAL_DATE, currentXMLdateTime);
		orderDate.setAttribute(NWCGConstants.EXPECTED_DATE, currentXMLdateTime);
		docElm.appendChild(orderDates);
		orderDates.appendChild(orderDate);
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::setOrderHeaderDates @@@@@");
	}

	/**
	 * 
	 * @param code
	 * @param severity
	 * @param responseMessage
	 * @param messageCode
	 * @param registerInterestInd
	 */
	private void postResponseToROSS(String code, String severity, String responseMessage, String messageCode, boolean registerInterestInd) {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::postResponseToROSS @@@@@");
		final String methodName = "postResponseToROSS";
		PlaceResourceRequestExternalResp resp = new PlaceResourceRequestExternalResp();
		resp.setRegisterInterestInd(registerInterestInd);
		ResponseStatusType value = new ResponseStatusType();
		int returnCode = Integer.parseInt(code);
		value.setReturnCode(returnCode);
		ResponseMessageType rmt = new ResponseMessageType();
		rmt.setCode(messageCode);
		rmt.setDescription(responseMessage);
		rmt.setSeverity(severity);
		value.getResponseMessage().add(rmt);
		resp.setResponseStatus(value);
		// Now the for loop over all of the orders created for the 0-to-many relationship of CacheIssueInfo elements
		for (int i = 0; i < ordersCreated.size(); i++) {
			Document curDoc = ordersCreated.get(i);
			Element curDocElm = curDoc.getDocumentElement();
			String curCreatets = curDocElm.getAttribute("Createts");
			Vector<String> requestNoOnThisIssue = getRequestNumbersForOrder(curDoc);
			// Request/CacheIssueInfo/
			PlaceCacheIssueCreateType pcict = new PlaceCacheIssueCreateType();
			// Request/CacheIssueInfo/CacheIssue
			CacheIssueCreateType cict = new CacheIssueCreateType();
			// set CacheIssue/CacheIssueNumber
			cict.setCacheIssueNumber(curDocElm.getAttribute(NWCGConstants.ORDER_NO));
			XMLGregorianCalendar xmlGregorialCal = null;
			try {
				DatatypeFactory dtf = DatatypeFactory.newInstance();
				xmlGregorialCal = dtf.newXMLGregorianCalendar(curCreatets);
			} catch (Exception e) {
				logger.error("!!!!! Failed to post response to ROSS:" + e.getMessage());
				logger.error("!!!!! " + NWCGConstants.NWCG_MSG_CODE_914_E_2);
				logger.error("!!!!! Caught General Exception: " + e);
				return;
			}
			// set CacheIssue/CacheIssueCreateDateTime
			cict.setCacheIssueCreateDateTime(xmlGregorialCal);
			// Place the CacheIssueCreateType under the CacheIssueInfo element
			pcict.setCacheIssue(cict);
			// Now create k # of RequestCodeType objects /RequestCode elements per # of issues created on this request bundle
			for (int k = 0; k < requestNoOnThisIssue.size(); k++) {
				// Request/CacheIssueInfo/RequestCode
				RequestCodeType rct = new RequestCodeType();
				// CacheIssueInfo/RequestCode/CatalogID
				rct.setCatalogID(CatalogID.fromValue(NWCGConstants.NFES_SUPPLY_PREFIX_LTR));
				StringBuffer catalogLetterBf = new StringBuffer(NWCGConstants.NFES_SUPPLY_PREFIX_LTR);
				catalogLetterBf.append(NWCGConstants.DASH);
				String curRequestNo = requestNoOnThisIssue.get(k);
				int seqPrefixIndex = curRequestNo.indexOf(catalogLetterBf.toString(), 0);
				String requestNumberOnly = curRequestNo.substring(seqPrefixIndex + 2);
				// Set CacheIssueInfo/RequestCode/SequenceNumber
				rct.setSequenceNumber(requestNumberOnly);
				// Add the newly created RequestCode to the CacheIssue content list
				pcict.getRequestCode().add(rct);
			}
			resp.getCacheIssueInfo().add(pcict);
		}
		try {
			// Call A&A framework for synchronous delivery of delivery operations
			Document responseDoc = null;
			responseDoc = new NWCGJAXBContextWrapper().getDocumentFromObject(resp, responseDoc, new URL(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
			Element docElm = responseDoc.getDocumentElement();
			docElm.setAttribute(NWCGAAConstants.MDTO_DISTID, distributionId);
			docElm.setAttribute(NWCGAAConstants.MDTO_ENTKEY, NWCGConstants.EMPTY_STRING);
			docElm.setAttribute(NWCGAAConstants.MDTO_ENTNAME, "ISSUE");
			int numOrdersCreated = ordersCreated.size();
			if (numOrdersCreated == 1) {
				Element firstOrderDocElm = ordersCreated.get(0).getDocumentElement();
				docElm.setAttribute(NWCGAAConstants.MDTO_ENTVALUE, firstOrderDocElm.getAttribute(NWCGConstants.ORDER_NO));
				docElm.setAttribute(NWCGAAConstants.MDTO_ENTKEY, firstOrderDocElm.getAttribute(NWCGConstants.ORDER_HEADER_KEY));
			} else {
				if (numOrdersCreated > 1) {
					Element firstOrderDocElm = ordersCreated.get(0).getDocumentElement();
					Element lastOrderDocElm = ordersCreated.get(numOrdersCreated - 1).getDocumentElement();
					StringBuffer sb = new StringBuffer(firstOrderDocElm.getAttribute(NWCGConstants.ORDER_NO));
					sb.append(NWCGConstants.DASH);
					sb.append(lastOrderDocElm.getAttribute(NWCGConstants.ORDER_NO));
					String entVal = sb.toString().trim();
					// If the concatenation of the first order #, dash, & last order # exceeds MAX_ENTITY_VALUE_LEN, the entity value will be the last MAX_ENTITY_VALUE_LEN characters in the string.
					if (entVal.length() > MAX_ENTITY_VALUE_LEN) {
						entVal = entVal.substring(0, MAX_ENTITY_VALUE_LEN - 1);
					}
					docElm.setAttribute(NWCGAAConstants.MDTO_ENTVALUE, entVal);
				}
			}
			DeliverOperationResultsResp aaResponse = new NWCGDeliverOperationResultsIB().process(myEnvironment, responseDoc);
		} catch (Exception e) {
			logger.error("!!!!! Failed to post response to ROSS:" + e);
			logger.error("!!!!! " + NWCGConstants.NWCG_MSG_CODE_914_E_2);
			logger.error("!!!!! Caught General Exception: " + e);
			return;
		}
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::postResponseToROSS @@@@@");
	}

	/**
	 * 
	 * @param curDoc
	 * @return
	 */
	private Vector<String> getRequestNumbersForOrder(Document curDoc) {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::getRequestNumbersForOrder @@@@@");
		Vector<String> requestNos = new Vector<String>();
		Element docElm = curDoc.getDocumentElement();
		List orderExtns = XMLUtil.getElementsByTagName(docElm, NWCGConstants.EXTN_ELEMENT);
		Iterator it = orderExtns.iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof Element) {
				Element curElm = (Element) o;
				if (curElm.hasAttribute(NWCGConstants.EXTN_REQUEST_NO))
					requestNos.add(curElm.getAttribute(NWCGConstants.EXTN_REQUEST_NO));
			} else {
				continue;
			}
		}
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::getRequestNumbersForOrder @@@@@");
		return requestNos;
	}

	/**
	 * Sets Order/PersonInfoShipTo attributes for the given
	 * ResourceRequestCreateType object.
	 * 
	 * @param retVal
	 * @param curRequest
	 * @return Document
	 */
	private Document setPersonInfoShipToElm(Document retVal, ResourceRequestCreateType curRequest) {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::setPersonInfoShipToElm @@@@@");
		AddressType shippingAddress = curRequest.getShippingAddress();
		logger.verbose("@@@@@ retVal :: " + XMLUtil.getXMLString(retVal));
		Element docElm = retVal.getDocumentElement();
		Element personInfoShipTo = retVal.createElement(NWCGConstants.PERSON_INFO_SHIPTO);
		if (shippingAddress.isSetName()) {
			String shippingAddressName = shippingAddress.getName();
			int spaceIndex = shippingAddressName.indexOf(' ');
			if (spaceIndex == -1) {
				personInfoShipTo.setAttribute(NWCGConstants.FIRST_NAME, shippingAddress.getName());
			} else {
				String firstName = shippingAddressName.substring(0, spaceIndex);
				String lastName = shippingAddressName.substring(spaceIndex + 1, shippingAddressName.length());
				personInfoShipTo.setAttribute(NWCGConstants.FIRST_NAME, firstName);
				personInfoShipTo.setAttribute(NWCGConstants.LAST_NAME, lastName);
			}
		}
		if (shippingAddress.isSetLine1()) {
			personInfoShipTo.setAttribute(NWCGConstants.ADDRESS_LINE_1, shippingAddress.getLine1());
		}
		if (shippingAddress.isSetLine2()) {
			personInfoShipTo.setAttribute(NWCGConstants.ADDRESS_LINE_2, shippingAddress.getLine2());
		}
		if (shippingAddress.isSetCity()) {
			personInfoShipTo.setAttribute(NWCGConstants.CITY, shippingAddress.getCity());
		}
		if (shippingAddress.isSetState()) {
			personInfoShipTo.setAttribute(NWCGConstants.STATE, shippingAddress.getState());
		}
		if (shippingAddress.isSetZipCode()) {
			personInfoShipTo.setAttribute(NWCGConstants.ZIP_CODE, shippingAddress.getZipCode());
		}
		if (shippingAddress.isSetCountryCode()) {
			personInfoShipTo.setAttribute(NWCGConstants.INCIDENT_COUNTRY_ATTR, shippingAddress.getCountryCode());
		}
		// Place the concatenation of ShippingAddress/UnitID/Prefix+Suffix in AlternateEmailID for the YFS_PERSON_INFO record entry for this shipping address.
		if (shippingAddress.isSetUnitID()) {
			UnitIDType unitId = shippingAddress.getUnitID();
			if (unitId.isSetUnitIDPrefix() && unitId.isSetUnitIDSuffix()) {
				StringBuffer altEmailId = new StringBuffer(unitId.getUnitIDPrefix());
				altEmailId.append(unitId.getUnitIDSuffix());
				personInfoShipTo.setAttribute(NWCGConstants.EXTN_ALT_EMAIL_ID, altEmailId.toString());
				personInfoShipTo.setAttribute(NWCGConstants.PERSON_INFO_PERSONID, altEmailId.toString());
			}
		}
		docElm.appendChild(personInfoShipTo);
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::setPersonInfoShipToElm @@@@@");
		return retVal;
	}
	
	/**
	 * Sets the Will Pick Up related Order/Extn attributes such as Name, Info,
	 * and Will Pick Up Date/Time on the passed in Element, extracting the data
	 * from the ResourceRequestCreateType (RossCommon.xsd)object passed in.
	 * 
	 * @param orderExtnElm
	 * @param curRequest
	 */
	private void setWillPickUpExtnAttribs(Element orderExtnElm, ResourceRequestCreateType curRequest) {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::setWillPickUpExtnAttribs @@@@@");
		// Here's where we will populate Order/Extn/@ExtnWillPickUpName and Order/Extn/@ExtnWillPickUpInfo
		WillPickUpInformationType wpuit = curRequest.getWillPickUpInfo();
		String willPickUpInfo = wpuit.getPickUpContactInfo();
		String willPickUpName = wpuit.getPickUpContactName();
		XMLGregorianCalendar needDateTimeCal = wpuit.getPickUpDateTime();
		String needDateTime = needDateTimeCal.toXMLFormat();
		if (needDateTime.endsWith("Z")) {
			needDateTime = needDateTime.replaceFirst("Z", "+00:00");
		}
		try {
			// Hardcoding timezone to CST
			needDateTime = CommonUtilities.convertTimeZone(needDateTime, "en_US_CST");
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
		}
		// and Order/@ReqDeliveryDate as the WillPickUpDateTime
		Document parentDoc = orderExtnElm.getOwnerDocument();
		Element rootDocElm = parentDoc.getDocumentElement();
		rootDocElm.setAttribute(NWCGConstants.REQ_DELIVERY_DATE_ATTR, needDateTime);
		orderExtnElm.setAttribute(NWCGConstants.EXTN_REQ_DELIVERY_DATE_ATTR, needDateTime);
		orderExtnElm.setAttribute(NWCGConstants.EXTN_WILL_PICK_UP_NAME, willPickUpName);
		orderExtnElm.setAttribute(NWCGConstants.EXTN_WILL_PICK_UP_INFO, willPickUpInfo);
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::setWillPickUpExtnAttribs @@@@@");
	}

	/**
	 * Return the concatenation of
	 * IncidentDetails/BillingOrganization/UnitID/Pfx+Sfx
	 * 
	 * @return String
	 */
	private String getROSSBillingOrgFromInput() {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::getROSSBillingOrgFromInput @@@@@");
		IncidentReturnType incident = input.getIncident();
		IncidentDetailsReturnType incidentDetails = incident.getIncidentDetails();
		UnitIDType billOrgType = incidentDetails.getBillingOrganization();
		String prefix = NWCGConstants.EMPTY_STRING, suffix = NWCGConstants.EMPTY_STRING;
		if (billOrgType.isSetUnitIDPrefix())
			prefix = billOrgType.getUnitIDPrefix();
		if (billOrgType.isSetUnitIDSuffix())
			suffix = billOrgType.getUnitIDSuffix();
		StringBuffer sb = new StringBuffer(prefix);
		sb.append(suffix);
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::getROSSBillingOrgFromInput @@@@@");
		return sb.toString();
	}

	/**
	 * Return the concatenation of
	 * IncidentDetails/BillingOrganization/UnitID/Pfx+Sfx
	 * 
	 * @return String representing the Incident Host / Billing Organization / Customer ID
	 */
	private String getCustomerIdFromInput() {
		logger.verbose("@@@@@ In NWCGPlaceResourceRequestExternalHandler::getCustomerIdFromInput @@@@@");
		return getROSSBillingOrgFromInput();
	}

	/**
	 * 
	 * @param maximumAllowed
	 * @param numRequestLines
	 */
	private void handleIssueTooManyLines(int maximumAllowed, int numRequestLines) {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::handleIssueTooManyLines @@@@@");
		String alertDesc = "Too many requests received per message PlaceResourceRequestExternalReq";
		if (containsRadioItems)
			createIssueAlertAndAssign(NWCGAAConstants.QUEUEID_ISSUE_RADIOS_FAILURE, alertDesc);
		else
			createIssueAlertAndAssign(NWCGAAConstants.QUEUEID_ISSUE_FAILURE, alertDesc);
		StringBuffer sb = new StringBuffer("Cannot create the issue. ");
		sb.append("The maximum number of requests (issue lines) which may be created ");
		sb.append("and placed in a single issue is ");
		sb.append(maximumAllowed);
		sb.append(".");
		String responseMessage = sb.toString();
		// Create negative response and post it back to ROSS
		postResponseToROSS(NWCGAAConstants.ROSS_RET_FAILURE_VALUE, NWCGAAConstants.DELIVER_OPERATION_RESULTS_SEVERITY_1, responseMessage, NWCGConstants.NWCG_MSG_CODE_914_E_6, false);
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::handleIssueTooManyLines @@@@@");
	}

	/**
	 * 
	 */
	private void handleInvalidPrimaryFinancialCode() {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::handleInvalidPrimaryFinancialCode @@@@@");
		String alertDescription = "Bundle received from ROSS: Issue created successfully with errors. Primary financial code missing";
		if (containsRadioItems)
			createIssueAlertAndAssign(NWCGAAConstants.QUEUEID_ISSUE_RADIOS_FAILURE, alertDescription);
		else
			createIssueAlertAndAssign(NWCGAAConstants.QUEUEID_ISSUE_FAILURE, alertDescription);
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::handleInvalidPrimaryFinancialCode @@@@@");
	}

	/**
	 * 1. Generate an alert in the NWCG_ISSUE_FAILURE alert queue assigned to
	 * the cache administrator with message "Can’t process ROSS request for
	 * issue creation: Shipping cache <given cache id> does not exist in
	 * system." Provide incident information, first and last request number in
	 * the bundle as reference fields in the generated alert. 2. Create negative
	 * response message (PlaceResourceRequestExternalResp) with message
	 * "Shipping cache <given shipping cache id> doesn’t exist in ICBSR" and a
	 * -1 response code. 3. Call SDF service for posting response message back
	 * to ROSS.
	 */
	private void handleInvalidShippingCache() {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::handleInvalidShippingCache @@@@@");
		String invalidShippingCacheId = getShippingCacheIdFromInput();
		StringBuffer alertDescBuff = new StringBuffer("Can’t process ROSS request for issue creation: Shipping cache ");
		alertDescBuff.append(invalidShippingCacheId);
		alertDescBuff.append(" does not exist in system.");
		String alertDescription = alertDescBuff.toString();
		if (containsRadioItems)
			createIssueAlertAndAssign(NWCGAAConstants.QUEUEID_ISSUE_RADIOS_FAILURE, alertDescription);
		else
			createIssueAlertAndAssign(NWCGAAConstants.QUEUEID_ISSUE_FAILURE, alertDescription);
		StringBuffer sb = new StringBuffer("Shipping cache ");
		sb.append(invalidShippingCacheId);
		sb.append(" doesn't exist in ICBSR.");
		String responseMessage = sb.toString();
		// Create negative response and post it back to ROSS
		postResponseToROSS(NWCGAAConstants.ROSS_RET_FAILURE_VALUE, NWCGAAConstants.DELIVER_OPERATION_RESULTS_SEVERITY_1, responseMessage, NWCGConstants.NWCG_MSG_CODE_914_E_5, false);
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::handleInvalidShippingCache @@@@@");
	}

	/**
	 * 
	 */
	private void handleIssueNoRequestLines() {
		logger.verbose("@@@@@ In NWCGPlaceResourceRequestExternalHandler::handleIssueNoRequestLines @@@@@");
		String responseMessage = "Zero request lines in NFES request.";
		// Create negative response and post it back to ROSS
		postResponseToROSS(NWCGAAConstants.ROSS_RET_FAILURE_VALUE, NWCGAAConstants.DELIVER_OPERATION_RESULTS_SEVERITY_1, responseMessage, NWCGConstants.NWCG_MSG_CODE_914_E_7, false);
	}

	/**
	 * 1. Generate an alert in the NWCG_ISSUE_FAILURE alert queue assigned to
	 * the cache administrator with message
	 * "Bundle received from ROSS: Issue crated successfully with errors."
	 * Provide incident information, first and last request number in the bundle
	 * as reference fields in the generated alert. 2. Place issue/order on hold
	 * with hold type of SHIP_INFO_MISSIN
	 * ("Shipping information missing in issue")
	 */
	private void handleInvalidShippingInfo() {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::handleInvalidShippingInfo @@@@@");
		String alertDescription = "Bundle received from ROSS: Issue created successfully with errors.";
		if (containsRadioItems)
			createIssueAlertAndAssign(NWCGAAConstants.QUEUEID_ISSUE_RADIOS_FAILURE, alertDescription);
		else
			createIssueAlertAndAssign(NWCGAAConstants.QUEUEID_ISSUE_FAILURE, alertDescription);
		// Now call changeOrder to put the issue on hold
		placeNewIssueOnHoldWithHoldType(NWCGConstants.NWCG_HOLD_TYPE_3_SHIPPING_INFO_MISSING);
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::handleInvalidShippingInfo @@@@@");
	}

	/**
	 * Generate an alert in NWCG_ISSUE_SUCCESS Queue with message
	 * "Bundle received from ROSS: Issue created successfully", providing
	 * incident information, issue# along with first and the last request# of
	 * the bundle in reference field, and assign alert to the Cache
	 * administrator.
	 */
	private void handleIssueCreatedSuccessfully() {
		logger.verbose("@@@@@ Entering NWCGPlaceResourceRequestExternalHandler::handleIssueCreatedSuccessfully @@@@@");
		String alertDescription = "Bundle received from ROSS: Issue created successfully.";
		if (containsRadioItems)
			createIssueAlertAndAssign(NWCGAAConstants.QUEUEID_ISSUE_RADIOS_SUCCESS, alertDescription);
		else
			createIssueAlertAndAssign(NWCGAAConstants.QUEUEID_ISSUE_SUCCESS, alertDescription);
		logger.verbose("@@@@@ Exiting NWCGPlaceResourceRequestExternalHandler::handleIssueCreatedSuccessfully @@@@@");
	}

	/**
	 * 
	 */
	private void handleInvalidBillingOrg() {
		logger.verbose("@@@@@ In NWCGPlaceResourceRequestExternalHandler::handleInvalidBillingOrg @@@@@");
		postResponseToROSS(NWCGAAConstants.ROSS_RET_FAILURE_VALUE, NWCGAAConstants.DELIVER_OPERATION_RESULTS_SEVERITY_1, "BillingOrganization on Incident does not exist in ICBSR", NWCGConstants.NWCG_MSG_CODE_914_E_8, false);
	}

	/**
	 * 
	 * @param e
	 */
	private void handleGeneralFailureWhileProcessingRequestBundle(Exception e) {
		logger.verbose("@@@@@ In NWCGPlaceResourceRequestExternalHandler::handleGeneralFailureWhileProcessingRequestBundle @@@@@");
		postResponseToROSS(NWCGAAConstants.ROSS_RET_FAILURE_VALUE, NWCGAAConstants.DELIVER_OPERATION_RESULTS_SEVERITY_1, "The following exception was thrown by ICBS while attempting to " + "process the request: " + e.getMessage(), NWCGConstants.NWCG_MSG_CODE_914_E_9, false);
	}
}