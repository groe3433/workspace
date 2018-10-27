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

package com.nwcg.icbs.yantra.ib.read.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.api.incident.util.NWCGIncidentToIncidentTransferUtil;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.ib.read.handler.util.NotificationCommonUtilities;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * Class invoked by ICBS A&A for handling Resource Reassignment Notifications
 * inbound from the ROSS system via the enterprise service bus (ESB).
 * One NWCGResourceRequestReassignNotificationHandler object instantiated for
 * each incoming Resource Reassignment notification.
 * 
 * @author drodriguez
 * @since USFS ICBS-ROSS Interface Increment 5
 * @version 1.1
 * 
 * Modifications for 9.1:
 * 	- Line 676-679 - added this validation because namespaces/prefix were causing a NullPointerException
 *  - Line 269-270 - added these lines to set the namespace on the "ron:Incident" element due to a SAX Parse Exception in the XSL transform. 
 * 
 * @revisions lightwell
 * @version 1.2
 */
public class NWCGResourceRequestReassignNotificationHandler implements NWCGMessageHandlerInterface {

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGResourceRequestReassignNotificationHandler.class);

	private static final String className = NWCGResourceRequestReassignNotificationHandler.class.getName();

	private String fromIncNo = NWCGConstants.EMPTY_STRING;
	private String fromIncYr = NWCGConstants.EMPTY_STRING;
	private String toIncNo = NWCGConstants.EMPTY_STRING;
	private String toIncYr = NWCGConstants.EMPTY_STRING;
	private String defaultCacheForDispatch = NWCGConstants.EMPTY_STRING;
	private String shippingCacheId = NWCGConstants.EMPTY_STRING;
	private String toIncidentKey = NWCGConstants.EMPTY_STRING;
	private String fromIncidentKey = NWCGConstants.EMPTY_STRING;
	private String inc2incKey = NWCGConstants.EMPTY_STRING;

	// Distribution ID of the input SOAP Message from the A&A Framework
	private String distributionId = NWCGConstants.EMPTY_STRING;

	// boolean used to determine whether to look for the destination incident on OL.EXTN_TO_INCIDENT_NO/YR or OL.EXTN_INCIDENT_NO/YR
	private boolean useToIncident = false;

	// Standard YFSEnvironment object utilized by all API/SDF calls
	private YFSEnvironment myEnvironment;

	// The org.w3c.dom.Document object passed into the Notification Handler
	private Document inputDoc = null;

	// This hashtable will store all the from and to request elements
	private Hashtable<Element, Element> htFromToReqs = new Hashtable<Element, Element>();

	// This hashtable will store the pending from req no and to request element. It is used to store the request elements in PENDING RR if atleast one of the request line is not in shipped/confirmed status (inc2inc transfer line)
	private Hashtable<String, Element> htFromReqToReqElm = new Hashtable<String, Element>();

	// This variable is used to set the comment in the alert. Currently, an alert is raised only when an incident to incident transfer issue is created. Now, we are creating an alert even though the issue is not created. When the issue is not created, we should not put that comment
	private boolean createdIssue = true;

	private boolean raisedPendingRRAlert = false;

	// If pending RR is present, then this will be set to true
	private boolean isPendingRRPresent = false;

	/**
	 * This method will run the following business logic for handling Resource
	 * Reassignment (RR) notifications received from ROSS:
	 * 
	 * Nomenclature & abbreviations used in comments throughout: IRNS = Invalid
	 * Request Number Scenario I2I = Incident To Incident Tx = Transaction DNE =
	 * Does not exist OL = Order Line RR = Resource Reassign
	 * 
	 * 1. Determine if the Source Incident ("From Incident") given exists - if
	 * not, error condition IRNS. [guaranteed to almost never happen] 2. a. If
	 * Source Incident ("From Incident") DNE: - IRNS b. If Source Incident
	 * ("From Incident") does exist: - Determine if the From Request Number
	 * exists on a fully shipped OL for an Issue on the Source Incident
	 * ("From Incident").
	 * 
	 * getOrderLineList with the following inputs:
	 * OrderLine/Order/Extn/@ExtnIncidentNo&Year = Source Incident Number&Year
	 * OrderLine/Order/Extn/@ExtnRequestNo = From Request Number
	 * OrderLine/Order/@DocumentType = 0001 OrderLine/Order/@EnterpriseCode =
	 * NWCG OrderLine/@Status = "Shipped" or OrderLine/@MaxLineStatus = 3700
	 * 
	 * - Yes it does: Shipped Request Number found on Source Incident
	 * ("From Incident"): 1. Create an Incident-to-Incident transfer order type
	 * 2. Add the ToRequest Line to the new I2I order doc type
	 * 
	 * - No, it doesn't: if the From Request Number OL does not exist on the
	 * Source Incident ("From Incident"), the handler will then look for an I2I
	 * transfer OLs using
	 * 
	 * getOrderLineList with the following inputs:
	 * OrderLine/Order/Extn/@ExtnToIncident = Source Incident Number&Year
	 * OrderLine/Order/Extn/@ExtnRequestNo = From Request Number
	 * OrderLine/Order/@DocumentType = 0008.ex OrderLine/Order/@EnterpriseCode =
	 * NWCG OrderLine/@Status = "Shipped" or OrderLine/@MaxLineStatus = 3700
	 * 
	 * - I2I OL found: 1. Create an Incident-to-Incident transfer order type 2.
	 * Use the ReassignToRequest/SequenceNumber as the request number on the I2I
	 * document. - I2I OL !found or !shipped: IRNS
	 * 
	 * 3. Validate that the OL found for the given request number is in
	 * 'Shipped' status. If no: error scenario
	 * 
	 * 4. Validate if the Destination Incident ("To Incident") exists in ICBS a.
	 * ICBS will create the Destination Incident and automatically activate the
	 * incident and mark it as "Registered Interest" since ROSS automatically
	 * does this on their end for us when- ever they send a RR notification to
	 * ICBS. 5. Create an Incident-to-Incident transfer order of doc type
	 * 0008.ex 6. Add the line(s) for the given ReassignToRequest Request to the
	 * Incident-to-Incident transfer. 7. Create the Incident-to-Incident
	 * transfer order.
	 * 
	 * @param YFSEnvironment
	 *            Standard com.yantra.yfs.japi.YFSEnvironment object required
	 *            for all Sterling APIs
	 * @param Document
	 *            Input document, should be the SOAPBody XML contents of the
	 *            incoming DeliverNotificationReq SOAPMessage
	 * @return Document
	 * @throws  
	 */
	public Document process(YFSEnvironment env, Document inputDoc) throws NWCGException {
		logger.verbose("@@@@@ Entering NWCGResourceRequestReassignNotificationHandler::process");
		
		final String methodName = "process";
		this.inputDoc = inputDoc;
		boolean toIncidentInICBSHasICBSCodes = false;

		// Get the ParentNode/@distributionID attribute from the input XML
		distributionId = inputDoc.getDocumentElement().getAttribute(NWCGConstants.INC_NOTIF_DIST_ID_ATTR);
		logger.verbose("@@@@@ distributionId : " + distributionId);

		// Set the local YFSEnvironment class variable
		this.myEnvironment = env;

		// Remove distributionID attribute from the documentElement of the input XML
		inputDoc.getDocumentElement().removeAttribute(NWCGConstants.INC_NOTIF_DIST_ID_ATTR);

		// Extract out the from/to Inc No/Year/Request numbers from the IB notification
		try {
			parseAndSetSrcAndDestIncInfo(inputDoc);
		} catch (Exception e) {
			logger.error("!!!!! " + NWCGConstants.AA_RR_INC_NOTIF_ERROR_001);
			logger.error("!!!!! Caught General Exception :: " + e);
			e.printStackTrace();
			throw new NWCGException(e);
		}

		// Get the optional Default Cache for the given Dispatch as the concatenation of UnitIDPrefix+Suffix from the ReassignToIncident/IncidentDetails/CacheOrganization element&children e.g. IDGBK
		defaultCacheForDispatch = getUnitIdTypeFromToIncident(NWCGConstants.INC_NOTIF_CACHEORG_ELEMENT);

		// Debug/finer statements showing everything we found on the IB notification
		logger.verbose("@@@@@ Total no of request lines as part of Resource Reassignment : " + htFromToReqs.size());
		logger.verbose("@@@@@ Cache ID: " + defaultCacheForDispatch);

		// Determine if the Source Incident ("From Incident") given exists - if not, error condition. [guaranteed to almost never happen]

		// Wrapping with TIMER log entries for future perf testing.
		Document docSrcIncDtls = NWCGIncidentToIncidentTransferUtil.getIncidentDetails(env, fromIncNo, fromIncYr, "SOURCE");
		logger.verbose("@@@@@ docSrcIncDtls : " + XMLUtil.getXMLString(docSrcIncDtls));

		if (docSrcIncDtls == null || docSrcIncDtls.getDocumentElement() == null || !docSrcIncDtls.getDocumentElement().getNodeName().equals(NWCGConstants.NWCG_INCIDENT_ORDER)) {
			logger.verbose("@@@@@ NWCGResourceRequestReassignNotificationHandler::process, " + "Source Incident is not present in ICBSR. " + "Not processing Resource Reassignment notification for " + fromIncNo + " and " + fromIncYr);
			String detailDesc = "Source Incident " + fromIncNo + " and year " + fromIncYr + " is not present in ICBSR system. Resource Reassignment " + "Notification is not processed!";
			String desc = "Source Incident is not present in ICBSR system!";
			createIncidentAlertAndAssignToCacheAdmin(inputDoc, detailDesc, desc, false);
			return inputDoc;
		}

		// Pull out the Source Incident Key from the output of NWCGGetIncidentOrderOnlyService.
		Element srcDocElm = docSrcIncDtls.getDocumentElement();
		fromIncidentKey = srcDocElm.getAttribute(NWCGConstants.INCIDENT_KEY);

		// Determine if the Destination Incident ("To Incident") given exists - if not, ICBS will create it.
		Document docDestIncIP = null;
		boolean needToCreateToIncident = false;

		// Wrapping with TIMER log entries for future perf testing.
		Document docDestIncDtls = NWCGIncidentToIncidentTransferUtil.getIncidentDetails(env, toIncNo, toIncYr, "DESTINATION");
		logger.verbose("@@@@@ docDestIncDtls : " + XMLUtil.getXMLString(docDestIncDtls));

		Element newOrUpdatedDestIncDocElm = null;
		if (docDestIncDtls != null) {
			newOrUpdatedDestIncDocElm = docDestIncDtls.getDocumentElement();
			if (newOrUpdatedDestIncDocElm.getNodeName().equals(NWCGConstants.NWCG_INCIDENT_ORDER) || newOrUpdatedDestIncDocElm.hasChildNodes()) {
				needToCreateToIncident = false;
				try {
					toIncidentKey = newOrUpdatedDestIncDocElm.getAttribute(NWCGConstants.INCIDENT_KEY);
					logger.verbose("@@@@@ Already existing in ICBSR Destination Incident Details: " + XMLUtil.extractStringFromDocument(docDestIncDtls));
				} catch (TransformerException te) {
					logger.error("!!!!! Caught TransformerException :: " + te);
					te.printStackTrace();
					throw new NWCGException(te);
				}
			} else {
				// This means we got something like: <?xml version="1.0" encoding="UTF-8"?> org.w3c.dom.Document w/no children.
				needToCreateToIncident = true;
				toIncidentInICBSHasICBSCodes = false;
			}
		} else {
			needToCreateToIncident = true;
			toIncidentInICBSHasICBSCodes = false;
		}
		try {
			docDestIncIP = XMLUtil.getDocument();
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			logger.error(e.getLocalizedMessage(), e);
			String detailDesc = "Unable to create DOM Document from XMLUtil.getDocument()! An exception was thrown while trying to create the Document.";
			String desc = "Unable to create DOM Document from XMLUtil.getDocument()!";
			createIncidentAlertAndAssignToCacheAdmin(inputDoc, detailDesc, desc, false);
			logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::process (Unable to create DOM Document from XMLUtil.getDocument()! An exception was thrown while trying to create the Document.)");
			return inputDoc;
		}
		// Get a NodeList of the ron:ReassignToIncident node, should only be 1
		NodeList nlDestInc = inputDoc.getElementsByTagNameNS(NWCGAAConstants.DELIVER_NOT_RON_NS_URI, NWCGConstants.REASSIGNTO_INCIDENT_LOCALNAME);
		if (nlDestInc == null || nlDestInc.getLength() != 1)
			nlDestInc = inputDoc.getElementsByTagName(NWCGConstants.REASSIGNTO_INCIDENT_NODENAME);
		Node reassignToIncidentFromInputNode = nlDestInc.item(0);
		Node importedNode = docDestIncIP.importNode(reassignToIncidentFromInputNode, true);
		Element el = (Element)importedNode;
		el.setAttribute("xmlns:ron", NWCGAAConstants.RESOURCE_ORDER_NOTIFICATION_NAMESPACE);
		docDestIncIP.appendChild(importedNode);
		// Change the root Element's node name to ron:Incident from ron:ReassignToIncident This is necessary in order for the XSL invoked by createIncident() to match properly on the root Element's full Node Name
		docDestIncIP.renameNode(importedNode, NWCGAAConstants.RESOURCE_ORDER_NOTIFICATION_NAMESPACE, NWCGConstants.INCIDENT_NODENAME);
		// If we didn't find the Destination incident, we'll create it with the <ron:Incident> Document docDestIncIP
		if (needToCreateToIncident) {
			docDestIncDtls = createDestInc(docDestIncIP, docDestIncDtls);
			toIncidentKey = docDestIncDtls.getDocumentElement().getAttribute(NWCGConstants.INCIDENT_KEY);
			newOrUpdatedDestIncDocElm = docDestIncDtls.getDocumentElement();
			newOrUpdatedDestIncDocElm.setAttribute(NWCGConstants.INCIDENT_BLM_ACCT_CODE, NWCGConstants.EMPTY_STRING);
			newOrUpdatedDestIncDocElm.setAttribute(NWCGConstants.INCIDENT_FS_ACCT_CODE, NWCGConstants.EMPTY_STRING);
			newOrUpdatedDestIncDocElm.setAttribute(NWCGConstants.INCIDENT_FS_OVERRIDE_CODE, NWCGConstants.EMPTY_STRING);
			newOrUpdatedDestIncDocElm.setAttribute(NWCGConstants.INCIDENT_OVERRIDECODE_ATTR, NWCGConstants.EMPTY_STRING);
			newOrUpdatedDestIncDocElm.setAttribute(NWCGConstants.INCIDENT_OTHER_ACCT_CODE, NWCGConstants.EMPTY_STRING);
			newOrUpdatedDestIncDocElm.setAttribute(NWCGConstants.BILL_TRANS_COST_CENTER, NWCGConstants.EMPTY_STRING);
			newOrUpdatedDestIncDocElm.setAttribute(NWCGConstants.BILL_TRANS_FUNCTIONAL_AREA, NWCGConstants.EMPTY_STRING);
			newOrUpdatedDestIncDocElm.setAttribute(NWCGConstants.BILL_TRANS_WBS, NWCGConstants.EMPTY_STRING);
			toIncidentKey = newOrUpdatedDestIncDocElm.getAttribute(NWCGConstants.INCIDENT_KEY);
		} else {
			// else Incident already exists so docDestIncDtls reference is not null Need to update the Destination Incident with the latest info contained in the ReassignToIncident/IncidentDetails/ if Dest Inc wasnt just created Call XSL service to transform ROSS format to ICBSR format xsl
			try {
				docDestIncIP = CommonUtilities.invokeService(env, NWCGConstants.SVC_UPDT_INCIDENT_NOTIF_XSL_SVC, docDestIncIP);
				// Remove any attributes with empty valuesm such as CustomerName="" because this causes ICBS to erase the current value which we don't want.
				docDestIncIP = removeEmptyAttributes(docDestIncIP);
				Element destIncDocElm = docDestIncIP.getDocumentElement();
				String incNo = destIncDocElm.getAttribute(NWCGConstants.INCIDENT_NO_ATTR);
				int lastIndex = incNo.lastIndexOf(NWCGConstants.DASH);
				String seqNum = incNo.substring(lastIndex + 1);
				seqNum = StringUtil.prepadStringWithZeros(seqNum, 6);
				incNo = incNo.substring(0, lastIndex + 1).concat(seqNum);
				destIncDocElm.setAttribute(NWCGConstants.INCIDENT_NO_ATTR, incNo);
				destIncDocElm.setAttribute(NWCGConstants.INCIDENT_KEY, toIncidentKey);
				destIncDocElm.setAttribute(NWCGConstants.MODIFICATION_CODE, "Auto Update From ROSS");
				String modDesc = "RR: " + CommonUtilities.getXMLCurrentTime();
				destIncDocElm.setAttribute(NWCGConstants.MODIFICATION_DESC, modDesc);
				// Set LastUpdatedFromROSS
				String lastUpdtFromROSS = destIncDocElm.getAttribute(NWCGConstants.LAST_UPDATED_FROM_ROSS);
				if (lastUpdtFromROSS == null || lastUpdtFromROSS.trim().length() < 2) {
					destIncDocElm.setAttribute(NWCGConstants.LAST_UPDATED_FROM_ROSS, CommonUtilities.getXMLCurrentTime());
				}
				logger.verbose("@@@@@ docDestIncIP to NWCGChangeIncidentOrderService : " + XMLUtil.extractStringFromDocument(docDestIncIP));
				docDestIncIP = CommonUtilities.invokeService(env, NWCGConstants.SVC_CHG_INCIDENT_ORDER_SVC, docDestIncIP);
				newOrUpdatedDestIncDocElm = docDestIncIP.getDocumentElement();
				toIncidentKey = newOrUpdatedDestIncDocElm.getAttribute(NWCGConstants.INCIDENT_KEY);
			} catch (Exception e) {
				// Log a severe error to A&A log file, but a failure to update the Destination Incident if it already exists shouldn't stop the entire RR
				logger.error("!!!!! FAILED to UPDATE Destination Incident...");
				logger.error("!!!!! Caught General Exception :: " + e);
				e.printStackTrace();
			}
		}

		// Wrapping with TIMER log entries for future perf testing.
		Document newOrUpdatedDestInc = NWCGIncidentToIncidentTransferUtil.getIncidentDetails(env, toIncNo, toIncYr, "DESTINATION");
		logger.verbose("@@@@@ newOrUpdatedDestInc : " + XMLUtil.getXMLString(newOrUpdatedDestInc));

		newOrUpdatedDestIncDocElm = newOrUpdatedDestInc.getDocumentElement();

		// Get a DOM Document with OrderLines/OrderLine* to be put on the inc2inc xfer order * Could possibly be multiple since we do an FLIKE getOrderLineList on the Request No
		Document docOLsForXferOrder = null;
		try {
			docOLsForXferOrder = getTransferOrderLines();
			logger.verbose("@@@@@ docOLsForXferOrder : " + XMLUtil.getXMLString(docOLsForXferOrder));
			// If pending RR is present, then the code in getTransferOrderLines is already generating an alert for Pending RR, so need to do anything here.
			if (isPendingRRPresent) {
				logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::process (isPendingRRPresent) :: " + isPendingRRPresent);
				return inputDoc;
			}
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			e.printStackTrace();
			String detailDesc = "Exception thrown by getTransferOrderLines(inputDoc)";
			String desc = "Resource Reassignment notification processing failure!";
			createIncidentAlertAndAssignToCacheAdmin(inputDoc, detailDesc, desc, false);
			return inputDoc;
		}

		// Check to see if we got 0 lines back for lines eligible for inc2inc transfer
		NodeList nlFromorderLinesForXferOrder = docOLsForXferOrder.getElementsByTagName(NWCGConstants.ORDER_LINE);
		if (nlFromorderLinesForXferOrder == null || nlFromorderLinesForXferOrder.getLength() < 1) {
			logger.verbose("@@@@@ NWCGResourceRequestReassignNotificationHandler::process, " + "No issue lines found to reassign to Destination IncidentSource. " + "Not processing Resource Reassignment notification for " + fromIncNo + " and " + fromIncYr);
			String detailDesc = "No Issue Lines found for Source Incident " + fromIncNo + " and year " + fromIncYr + " . Resource Reassignment Notification is not processed!";
			String desc = "No Issue Lines found for Source Incident for Reassignment";
			createIncidentAlertAndAssignToCacheAdmin(inputDoc, detailDesc, desc, false);
			return inputDoc;
		}
		logger.verbose("@@@@@ NWCGResourceRequestReassignNotificationHandler::process, " + "No of order lines to be transferred are " + nlFromorderLinesForXferOrder.getLength());

		// Default to Shipping Address (ExtnNavInfo="SHIP_ADDRESS")
		String extnNavInfo = NWCGConstants.SHIPPING_ADDRESS;

		// Get the first Order element we encounter (top-to-bottom search)
		NodeList orderNodes = docOLsForXferOrder.getDocumentElement().getElementsByTagName(NWCGConstants.ORDER_ELM);
		Element firstOrderElm = null;
		if (orderNodes != null && orderNodes.getLength() > 0) {
			firstOrderElm = (Element) orderNodes.item(0);
			// Setting the document type at the order line level. This will be used while getting the trackable information for the order line key from shipment details
			for (int on = 0; on < orderNodes.getLength(); on++) {
				Node nodeTmpOrderNode = orderNodes.item(on);
				String docType = ((Element) nodeTmpOrderNode).getAttribute(NWCGConstants.DOCUMENT_TYPE);
				((Element) nodeTmpOrderNode.getParentNode()).setAttribute(NWCGConstants.DOCUMENT_TYPE, docType);
			}
		} else {
			// Couldn't find an Order element in the OrderLineList output doc should never happen, but handling here as a error message
			logger.verbose("@@@@@ FATAL error 1! :: Couldn't find an Order element in the OrderLineList output doc should never happen, but handling here as a error message...");
			throw new NWCGException(NWCGConstants.AA_RR_INC_NOTIF_ERROR_001);
		}

		String willPickUpName = NWCGConstants.EMPTY_STRING;
		String willPickUpInfo = NWCGConstants.EMPTY_STRING;
		String reqDeliveryDate = NWCGConstants.EMPTY_STRING;
		String extnShipInstrCity = NWCGConstants.EMPTY_STRING;
		String extnShipInstrState = NWCGConstants.EMPTY_STRING;
		String extnShippingInstructions = NWCGConstants.EMPTY_STRING;
		// not getting blm or other from op of createIncident (destination)

		String fromIncidentFSAccountCode = srcDocElm.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE).trim();
		String fromIncidentBlmAccountCode = srcDocElm.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE).trim();
		String fromIncidentOtherAcctCode = srcDocElm.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE).trim();
		String fromIncidentOverrideCode = srcDocElm.getAttribute(NWCGConstants.INCIDENT_OVERRIDECODE_ATTR).trim();
		String fromIncidentPhoneNo = srcDocElm.getAttribute(NWCGConstants.PHONE_NO_ATTR).trim();
		String fromIncidentCacheId = srcDocElm.getAttribute(NWCGConstants.CACHE_ID_ATTR).trim();
		String fromIncidentType = srcDocElm.getAttribute(NWCGConstants.INCIDENT_TYPE).trim();
		String fromIncidentName = srcDocElm.getAttribute(NWCGConstants.INCIDENT_NAME).trim();
		String toIncidentFSAccountCode = newOrUpdatedDestIncDocElm.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE).trim();
		String toIncidentBlmAccountCode = newOrUpdatedDestIncDocElm.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE).trim();
		String toIncidentCostCenter = newOrUpdatedDestIncDocElm.getAttribute(NWCGConstants.BILL_TRANS_COST_CENTER).trim();
		String toIncidentFA = newOrUpdatedDestIncDocElm.getAttribute(NWCGConstants.BILL_TRANS_FUNCTIONAL_AREA).trim();
		String toIncidentWBS = newOrUpdatedDestIncDocElm.getAttribute(NWCGConstants.BILL_TRANS_WBS).trim();
		String toIncidentOtherAcctCode = newOrUpdatedDestIncDocElm.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE).trim();
		String toIncidentOverrideCode = newOrUpdatedDestIncDocElm.getAttribute(NWCGConstants.INCIDENT_OVERRIDECODE_ATTR).trim();
		String toIncidentPhoneNo = newOrUpdatedDestIncDocElm.getAttribute(NWCGConstants.PHONE_NO_ATTR).trim();
		String toIncidentCacheId = newOrUpdatedDestIncDocElm.getAttribute(NWCGConstants.CACHE_ID_ATTR).trim();
		String toIncidentType = newOrUpdatedDestIncDocElm.getAttribute(NWCGConstants.INCIDENT_TYPE).trim();
		String toIncidentName = newOrUpdatedDestIncDocElm.getAttribute(NWCGConstants.INCIDENT_NAME).trim();
		if (firstOrderElm == null) {
			logger.verbose("@@@@@ FATAL error 2! ");
			logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::process (FATAL error 2!)");
			return inputDoc;
		}

		Element orderExtnElm = XMLUtil.getFirstElementByName(firstOrderElm, NWCGConstants.EXTN_ELEMENT);
		if (orderExtnElm != null) {
			if (orderExtnElm.hasAttribute(NWCGConstants.EXTN_NAV_INFO_ATTR)) {
				orderExtnElm.removeAttribute(NWCGConstants.EXTN_NAV_INFO_ATTR);
			}
		}
		if (firstOrderElm.hasAttribute(NWCGConstants.REQ_DELIVERY_DATE_ATTR)) {
			reqDeliveryDate = firstOrderElm.getAttribute(NWCGConstants.REQ_DELIVERY_DATE_ATTR);
		}
		if (firstOrderElm.hasAttribute(NWCGConstants.SHIP_NODE)) {
			shippingCacheId = firstOrderElm.getAttribute(NWCGConstants.SHIP_NODE);
		}
		// Don't have to keep one-upping the index because a NodeList is "Live" so removing from the NodeList, reduces its getLength(). Removing order nodes
		for (int i = 0; orderNodes.getLength() > 0;) {
			Node toRemove = orderNodes.item(i);
			toRemove.getParentNode().removeChild(toRemove);
		}
		// Remove OrderLine/Extn/@ExtnNavInfo
		NodeList extnNodes = docOLsForXferOrder.getDocumentElement().getElementsByTagName(NWCGConstants.EXTN_ELEMENT);
		// Pair up the order line with it's serial number
		for (int i = 0; i < extnNodes.getLength(); i++) {
			Node curNode = extnNodes.item(i);
			Element curExtnElm = (curNode instanceof Element) ? (Element) curNode : null;
			Element curOrderLine = (curNode.getParentNode() instanceof Element) ? (Element) curNode.getParentNode() : null;
			if (curExtnElm == null || curOrderLine == null)
				continue;
			String curOLK = "";
			if (curExtnElm.hasAttribute(NWCGConstants.EXTN_NAV_INFO_ATTR)) {
				logger.verbose("@@@@@ curOrderLine.getNodeName() :: " + curOrderLine.getNodeName());
				logger.verbose("@@@@@ curOrderLine.getLocalName() :: " + curOrderLine.getLocalName());
				if (curOrderLine.getNodeName().equals(NWCGConstants.ORDER_LINE) || curOrderLine.getLocalName().equals(NWCGConstants.ORDER_LINE)) {
					curExtnElm.removeAttribute(NWCGConstants.EXTN_NAV_INFO_ATTR);
					curOLK = curOrderLine.getAttribute(NWCGConstants.ORDER_LINE_KEY);
					curOrderLine.removeAttribute(NWCGConstants.ORDER_LINE_KEY);
					if (StringUtil.isEmpty(curOLK)) {
						logger.verbose("@@@@@ No OrderLine/@OrderLineKey attribute " + "found in output of getOrderLineList API!");
						logger.verbose("@@@@@ FATAL error 2!");
						throw new NWCGException(NWCGConstants.AA_RR_INC_NOTIF_ERROR_004);
					}
					curOrderLine.removeAttribute(NWCGConstants.DOCUMENT_TYPE);
				}
			}
		}

		// Get a Document as the input for inc2inc tx order creation
		String cacheToUseAsShipNodeOnInc2Inc = (!StringUtil.isEmpty(shippingCacheId)) ? shippingCacheId : defaultCacheForDispatch;
		Document docTransferOrderXML = NWCGIncidentToIncidentTransferUtil.createOrderWithOLs(env, docSrcIncDtls, docDestIncDtls, docOLsForXferOrder, cacheToUseAsShipNodeOnInc2Inc);
		logger.verbose("@@@@@ docTransferOrderXML : " + XMLUtil.getXMLString(docTransferOrderXML));
		// Now set all the Order/Extn attributes from String variables set above
		Element docXferOrderElm = docTransferOrderXML.getDocumentElement();
		Element docXferOrderExtnElm = XMLUtil.getFirstElementByName(docXferOrderElm, NWCGConstants.EXTN_ELEMENT);
		docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_NAV_INFO_ATTR, extnNavInfo);
		// inc2inc hasn't been created yet at this point, we only have the input xml for it
		if (!StringUtil.isEmpty(willPickUpName))
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_WILL_PICK_UP_NAME, willPickUpName);
		if (!StringUtil.isEmpty(willPickUpInfo))
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_WILL_PICK_UP_INFO, willPickUpInfo);
		if (!StringUtil.isEmpty(extnShippingInstructions))
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_SHIPPING_INSTRUCTIONS_ATTR, extnShippingInstructions);
		if (!StringUtil.isEmpty(extnShipInstrCity))
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_SHIPPING_INSTR_CITY_ATTR, extnShipInstrCity);
		if (!StringUtil.isEmpty(extnShipInstrState))
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_SHIPPING_INSTR_STATE_ATTR, extnShipInstrState);
		if (!StringUtil.isEmpty(toIncidentType))
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_TO_INCIDENT_TYPE, toIncidentType);
		if (!StringUtil.isEmpty(fromIncidentType))
			docXferOrderExtnElm.setAttribute(NWCGConstants.INCIDENT_TYPE_ATTR, fromIncidentType);
		if (!StringUtil.isEmpty(toIncidentName))
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_TO_INCIDENT_NAME, toIncidentName);
		if (!StringUtil.isEmpty(fromIncidentName))
			docXferOrderExtnElm.setAttribute(NWCGConstants.INCIDENT_NAME_ATTR, fromIncidentName);
		if (!StringUtil.isEmpty(toIncidentPhoneNo))
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_TO_INCIDENT_PHONE_NO, toIncidentPhoneNo);
		if (!StringUtil.isEmpty(fromIncidentPhoneNo))
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_INCIDENT_PHONE_NO, fromIncidentPhoneNo);
		if (!StringUtil.isEmpty(toIncidentPhoneNo))
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_TO_INCIDENT_CACHE_ID, toIncidentCacheId);
		if (!StringUtil.isEmpty(fromIncidentCacheId))
			docXferOrderExtnElm.setAttribute(NWCGConstants.INCIDENT_CACHE_ID, fromIncidentCacheId);
		if (!StringUtil.isEmpty(toIncidentFSAccountCode))
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_TO_FS_ACCT_CODE, toIncidentFSAccountCode);
		if (!StringUtil.isEmpty(fromIncidentFSAccountCode))
			docXferOrderExtnElm.setAttribute(NWCGConstants.INCIDENT_FS_ACCT_CODE, fromIncidentFSAccountCode);
		if (!StringUtil.isEmpty(toIncidentBlmAccountCode))
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_TO_INCIDENT_BLM_CODE, toIncidentBlmAccountCode);
		if (!StringUtil.isEmpty(fromIncidentBlmAccountCode))
			docXferOrderExtnElm.setAttribute(NWCGConstants.INCIDENT_BLM_ACCT_CODE, fromIncidentBlmAccountCode);
		if (!StringUtil.isEmpty(toIncidentOtherAcctCode))
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_TO_OTHER_ACCT_CODE, toIncidentOtherAcctCode);
		if (!StringUtil.isEmpty(fromIncidentOtherAcctCode))
			docXferOrderExtnElm.setAttribute(NWCGConstants.INCIDENT_OTHER_ACCT_CODE, fromIncidentOtherAcctCode);
		if (!StringUtil.isEmpty(toIncidentOverrideCode))
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_TO_OVERRIDE_CODE, toIncidentOverrideCode);
		if (!StringUtil.isEmpty(fromIncidentOverrideCode))
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_OVERRIDE_CODE, fromIncidentOverrideCode);
		if (!StringUtil.isEmpty(reqDeliveryDate))
			docXferOrderElm.setAttribute(NWCGConstants.REQ_DELIVERY_DATE_ATTR, reqDeliveryDate);
		String customerId = getUnitIdTypeFromToIncident(NWCGConstants.INC_NOTIF_BILLINGORG_ELEMENT);
		if (!StringUtil.isEmpty(customerId)) {
			docXferOrderElm.setAttribute(NWCGConstants.BILLTO_ID_ATTR, customerId);
		}
		String toShipToKey = newOrUpdatedDestIncDocElm.getAttribute("PersonInfoShipToKey");
		if (!StringUtil.isEmpty(toShipToKey)) {
			docXferOrderElm.setAttribute("ShipToKey", toShipToKey);
		}
		String toBillToKey = newOrUpdatedDestIncDocElm.getAttribute("PersonInfoBillToKey");
		if (!StringUtil.isEmpty(toBillToKey))
			docXferOrderElm.setAttribute("BillToKey", toBillToKey);
		if (!StringUtil.isEmpty(toIncidentFSAccountCode) && !StringUtil.isEmpty(toIncidentOverrideCode)) {
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_TO_FS_ACCT_CODE, toIncidentFSAccountCode);
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_TO_OVERRIDE_CODE, toIncidentOverrideCode);
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_SHIP_ACCT_CODE, toIncidentFSAccountCode);
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_SA_OVERRIDE_CODE, toIncidentOverrideCode);
			toIncidentInICBSHasICBSCodes = true;
		}
		// modified by Gaurav as WBS is no more mandatory
		if (!StringUtil.isEmpty(toIncidentBlmAccountCode) && !StringUtil.isEmpty(toIncidentCostCenter) && !StringUtil.isEmpty(toIncidentFA)) {
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_TO_INCIDENT_BLM_CODE, toIncidentBlmAccountCode);
			docXferOrderExtnElm.setAttribute(NWCGConstants.FBMS_COSTCENTER_EXTN_ATTR, toIncidentCostCenter);
			docXferOrderExtnElm.setAttribute(NWCGConstants.FBMS_FUNCTIONALAREA_EXTN_ATTR, toIncidentFA);
			docXferOrderExtnElm.setAttribute(NWCGConstants.FBMS_WBS_EXTN_ATTR, toIncidentWBS);
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_SHIP_ACCT_CODE, toIncidentBlmAccountCode);
			if (!StringUtil.isEmpty(toIncidentOverrideCode)) {
				docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_SA_OVERRIDE_CODE, toIncidentOverrideCode);
			}
			toIncidentInICBSHasICBSCodes = true;
		}
		if (!StringUtil.isEmpty(toIncidentOtherAcctCode) && !StringUtil.isEmpty(toIncidentOverrideCode)) {
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_TO_OTHER_ACCT_CODE, toIncidentOtherAcctCode);
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_TO_OVERRIDE_CODE, toIncidentOverrideCode);
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_SHIP_ACCT_CODE, toIncidentOtherAcctCode);
			docXferOrderExtnElm.setAttribute(NWCGConstants.EXTN_SA_OVERRIDE_CODE, toIncidentOverrideCode);
			toIncidentInICBSHasICBSCodes = true;
		}
		
		// Only going to create the inc2inc 0008.ex Create the order
		Document docOrderTmpl = null;
		try {
			docOrderTmpl = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
			Element elmOrderTmplDoc = docOrderTmpl.getDocumentElement();
			elmOrderTmplDoc.setAttribute(NWCGConstants.ORDER_HEADER_KEY, NWCGConstants.EMPTY_STRING);
			elmOrderTmplDoc.setAttribute(NWCGConstants.DOCUMENT_TYPE, NWCGConstants.EMPTY_STRING);
			// Remove Order/PersonInfoShipTo and Order/PersonInfoBillTo since we dont populate them, instead we use the keys on the Order element
			NodeList nl = docTransferOrderXML.getElementsByTagName("PersonInfoBillTo");
			if (nl != null && nl.getLength() == 1) {
				Node toRemove = nl.item(0);
				Node parentNode = toRemove.getParentNode();
				parentNode.removeChild(toRemove);
			}
			nl = docTransferOrderXML.getElementsByTagName("PersonInfoShipTo");
			if (nl != null && nl.getLength() == 1) {
				Node toRemove = nl.item(0);
				Node parentNode = toRemove.getParentNode();
				parentNode.removeChild(toRemove);
			}
			Document docOrderOP = CommonUtilities.invokeAPI(env, docOrderTmpl, NWCGConstants.API_CREATE_ORDER, docTransferOrderXML);
			inc2incKey = (docOrderOP != null) ? docOrderOP.getDocumentElement().getAttribute(NWCGConstants.ORDER_HEADER_KEY) : NWCGConstants.EMPTY_STRING;
		} catch (Exception e) {
			logger.error("!!!!! Exception thrown by createOrder(docTransferOrderXML)");
			logger.error("!!!!! Caught General Exception :: " + e);
			String detailDesc = "Exception thrown by createOrder(docTransferOrderXML)";
			String desc = "Resource Reassignment notification processing failure!";
			createIncidentAlertAndAssignToCacheAdmin(inputDoc, detailDesc, desc, false);
			logger.error("!!!!! Exiting NWCGResourceRequestReassignNotificationHandler::process (Resource Reassignment notification processing failure!)");
			return inputDoc;
		}
		// Set the alert description appropriately if to incident has account codes. If to incident doesn't have account codes, then put hold on the issue
		if (toIncidentInICBSHasICBSCodes) {
			StringBuffer sb = new StringBuffer("Resource Reassignment Notification received from ROSS for Source Incident ");
			sb.append(NWCGConstants.EMPTY_STRING + fromIncNo + " and " + fromIncYr + " from ROSS. ");
			sb.append("Destination Incident " + toIncNo + " with year " + toIncYr);
			sb.append(" is processed successfully in ICBSR");
			String desc = "Resource Reassignment successful";
			createIncidentAlertAndAssignToCacheAdmin(docTransferOrderXML, sb.toString(), desc, true);
		} else {
			logger.error("!!!!! The Incident to Incident Transfer is not confirmed due to the To Incident not having ICBS Account Codes and/or FBMS elements (if BLM cache)");
			StringBuffer sb = new StringBuffer("Resource Reassignment Notification received from ROSS for Source Incident ");
			sb.append(NWCGConstants.EMPTY_STRING + fromIncNo + " and " + fromIncYr + " from ROSS. ");
			sb.append("Destination Incident " + toIncNo + " with year " + toIncYr);
			sb.append(" is received in ICBSR, but the Destination Incident does not" + " have ICBS Account Codes and/or FBMS values (if BLM cache)." + " The Incident To Incident transfer order is created but not confirmed!"  + " Please confirm the transfer order after updating the Destination Incident.");
			String desc = "Resource Reassignment received, Destination Incident needs ICBS Acct Codes!";
			// Apply hold type to the Inc2Inc transfer order
			placeInc2IncOrderOnHoldWithHoldType(NWCGConstants.NWCG_HOLD_TYPE_4_INC_NO_ICBS_CODES);
			// Create the alert in the NWCG_INCIDENT_FAILURE queue instructing the user to apply the ICBS codes + FBMS values to the To Incident
			createIncidentAlertAndAssignToCacheAdmin(docTransferOrderXML, sb.toString(), desc, false);
		}
		logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::process");
		return inputDoc;
	}

	/**
	 * This method creates the destination incident
	 * 
	 * @param docDestIncIP
	 * @param docDestIncDtls
	 * @return
	 */
	private Document createDestInc(Document docDestIncIP, Document docDestIncDtls) throws NWCGException {
		logger.verbose("@@@@@ Entering NWCGResourceRequestReassignNotificationHandler::createDestInc");
		String methodName = "createDestInc";

		// Call createIncident for the destination incident
		try {
			// Wrapping with TIMER log entries for future perf testing.
			docDestIncDtls = NWCGIncidentToIncidentTransferUtil.createIncident(myEnvironment, docDestIncIP, NWCGAAConstants.INCIDENT_ACTION_REASSIGN);
			logger.verbose("@@@@@ docDestIncDtls : " + XMLUtil.getXMLString(docDestIncDtls));
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: FAILED to Create Destination Incident :: " + e);
			e.printStackTrace();
			String detailDesc = "Exception thrown by NWCGIncidentToIncidentTransferUtil.createIncident(): " + e.getMessage();
			String desc = "Resource Reassignment notification processing failure!";
			createIncidentAlertAndAssignToCacheAdmin(inputDoc, detailDesc, desc, false);
			logger.error("!!!!! Exiting NWCGResourceRequestReassignNotificationHandler::createDestInc");
			return inputDoc;
		}
		logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::createDestInc");
		return docDestIncDtls;
	}

	/**
	 * 
	 * @param inDoc
	 * @return
	 */
	private Document removeEmptyAttributes(Document inDoc) {
		logger.verbose("@@@@@ Entering NWCGResourceRequestReassignNotificationHandler::removeEmptyAttributes");
		if (inDoc != null) {
			NodeList nl = inDoc.getElementsByTagName("*");
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
		logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::removeEmptyAttributes");
		return inDoc;
	}

	/**
	 * This method will extract the Source and Destination Incident Number, and
	 * Year String variables.
	 * 
	 * @param inputDoc
	 *            Document Input XML passed into the IB RR handler class
	 */
	private void parseAndSetSrcAndDestIncInfo(Document inputDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGResourceRequestReassignNotificationHandler::parseAndSetSrcAndDestIncInfo");
		
		final String methodName = "parseAndSetSrcAndDestIncInfo";
		logger.verbose("@@@@@ inputDoc : " + XMLUtil.getXMLString(inputDoc));
		try {
			Element elmDeliverNotifReqRoot = inputDoc.getDocumentElement();
			NodeList nlReassignNotif = elmDeliverNotifReqRoot.getElementsByTagNameNS(NWCGAAConstants.DELIVER_NOT_RON_NS_URI, "ResourceRequestReassignNotification");
			if (nlReassignNotif == null || nlReassignNotif.getLength() < 1) {
				logger.verbose("@@@@@ Try Again 1...");
				nlReassignNotif = elmDeliverNotifReqRoot.getElementsByTagName("ResourceRequestReassignNotification");
				if (nlReassignNotif == null || nlReassignNotif.getLength() < 1) {
					logger.verbose("@@@@@ Try Again 2...");
					nlReassignNotif = elmDeliverNotifReqRoot.getElementsByTagName("ron:ResourceRequestReassignNotification");
				}
			}
			Element elmRRRNotification = (Element) nlReassignNotif.item(0);
			NodeList nlReassignNotifChilds = elmRRRNotification.getChildNodes();
			Element elmReassignedRequests = null;
			Element elmReassignedToIncident = null;
			for (int i = 0; i < nlReassignNotifChilds.getLength(); i++) {
				logger.verbose("@@@@@ i :: " + i);
				Node tmpNode = nlReassignNotifChilds.item(i);
				if (tmpNode.getNodeType() == Node.ELEMENT_NODE) {
					Element elmReassignNotifChild = (Element) tmpNode;
					logger.verbose("@@@@@ elmReassignNotifChild.getNodeName() :: " + elmReassignNotifChild.getNodeName());
					// Check whether contains works or not
					if (elmReassignNotifChild.getNodeName().contains("ReassignedRequests")) {
						elmReassignedRequests = elmReassignNotifChild;
					//} else if (elmReassignNotifChild.getNodeName().contains(NWCGConstants.REASSIGNTO_INCIDENT_NODENAME)) {
					} else if (elmReassignNotifChild.getNodeName().contains("ReassignToIncident")) {		
						elmReassignedToIncident = elmReassignNotifChild;
					}
				}
			}
			if (elmReassignedRequests == null) {
				throw new NullPointerException("Invalid RR notification structure - ReassignedRequests element not found in input document");
			}
			if (elmReassignedToIncident == null) {
				throw new NullPointerException("Invalid RR notification structure - ReassignedToIncident element not found in input document");
			}
			NodeList nlFromIncIncKey = elmReassignedRequests.getElementsByTagName(NWCGConstants.INCIDENT_KEY);
			if (nlFromIncIncKey.getLength() < 1) {
				throw new Exception("Invalid RR notification structure! - No ReassignedRequest element found");
			}
			logger.verbose("@@@@@ Before parseAndRtnIncidentKey 1...");
			HashMap<String, String> srcIncNoAndYr = NotificationCommonUtilities.parseAndRtnIncidentKey(nlFromIncIncKey, 0);
			logger.verbose("@@@@@ After parseAndRtnIncidentKey 1...");
			NodeList nlToIncIncKey = elmReassignedToIncident.getElementsByTagName(NWCGConstants.INCIDENT_KEY);
			if (nlToIncIncKey.getLength() < 1) {
				throw new Exception("Invalid RR notification structure! - IncidentKey element not found in ReassignToIncident element");
			}
			logger.verbose("@@@@@ Before parseAndRtnIncidentKey 2...");
			HashMap<String, String> destIncNoAndYr = NotificationCommonUtilities.parseAndRtnIncidentKey(nlToIncIncKey, 0);
			logger.verbose("@@@@@ After parseAndRtnIncidentKey 2...");
			fromIncNo = srcIncNoAndYr.get(NWCGConstants.INCIDENT_NO_ATTR);
			fromIncYr = srcIncNoAndYr.get(NWCGConstants.YEAR_ATTR);
			toIncNo = destIncNoAndYr.get(NWCGConstants.INCIDENT_NO_ATTR);
			toIncYr = destIncNoAndYr.get(NWCGConstants.YEAR_ATTR);
			logger.verbose("@@@@@ " + className + "::" + methodName + ", From Incident : " + fromIncNo + "/" + fromIncYr + ", To Incident : " + toIncNo + "/" + toIncYr);
			parseAndSetFromAndToReqElms(elmReassignedRequests);
		} catch(NullPointerException npe) {
			logger.error("!!!!! Caught NullPointerException :: " + npe);
			npe.printStackTrace();
		} catch(Exception e) {
			logger.error("!!!!! Caught General Execption :: " + e);
			e.printStackTrace();
		}
		logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::parseAndSetSrcAndDestIncInfo");
	}

	/**
	 * This method will get the from and to request elements and sets in a
	 * hashtable String variables.
	 * 
	 * @param elmReassignedRequests
	 *            Element containing from and to request details
	 */
	private void parseAndSetFromAndToReqElms(Element elmReassignedRequests) throws Exception {
		logger.verbose("@@@@@ Entering NWCGResourceRequestReassignNotificationHandler::parseAndSetFromAndToReqElms");
		final String methodName = "parseAndSetFromAndToReqElms";
		NodeList nlReassignedReqsChilds = elmReassignedRequests.getChildNodes();
		logger.verbose("@@@@@ " + className + "::" + methodName + ", No of ReassignedRequest elements : " + nlReassignedReqsChilds.getLength());
		for (int i = 0; i < nlReassignedReqsChilds.getLength(); i++) {
			Node tmpNode = nlReassignedReqsChilds.item(i);
			if ((tmpNode.getNodeType() == Node.ELEMENT_NODE) && tmpNode.getNodeName().contains("ReassignedRequest")) {
				Element elmReassignedReqChild = (Element) tmpNode;
				NodeList nlReassignedReqChilds = elmReassignedReqChild.getChildNodes();
				Element elmFromReq = null;
				Element elmToReq = null;
				for (int j = 0; j < nlReassignedReqChilds.getLength(); j++) {
					Node fromOrToNode = nlReassignedReqChilds.item(j);
					if (fromOrToNode != null && fromOrToNode.getNodeType() == Node.ELEMENT_NODE) {
						if (fromOrToNode.getNodeName().contains("ReassignFromRequest")) {
							elmFromReq = (Element) fromOrToNode;
						} else if (fromOrToNode.getNodeName().contains("ReassignToRequest")) {
							elmToReq = (Element) fromOrToNode;
						}
					}
				} 
				if (elmFromReq != null && elmToReq != null) {
					htFromToReqs.put(elmFromReq, elmToReq);
				}
			} 
		} 
		logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::parseAndSetFromAndToReqElms");
	}

	/**
	 * Retrieves OrderLines rooted document for all order lines FLIKE the From
	 * Request number.
	 * 
	 * @return Document
	 * @throws Exception
	 */
	private Document getTransferOrderLines() throws NWCGException {
		logger.verbose("@@@@@ Entering NWCGResourceRequestReassignNotificationHandler::getTransferOrderLines");
		final String methodName = "getTransferOrderLines";
		Document docOLsForXferOrder;
		try {
			docOLsForXferOrder = XMLUtil.createDocument(NWCGConstants.ORDER_LINES);
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException :: " + pce.getMessage());
			throw new NWCGException(pce);
		}
		// Make getOrderLinesOnSourceOrToIncident for each request no and pass the corresponding to request element
		Iterator<Element> itrFromElms = htFromToReqs.keySet().iterator();
		logger.verbose("@@@@@ NWCGRRRNotificationHndlr::getTxOrderLines, Total number of request elements : " + htFromToReqs.size());
		Vector<String> vecSerialNos = new Vector<String>();
		while (itrFromElms.hasNext()) {
			Element elmFrom = itrFromElms.next();
			Element elmTo = htFromToReqs.get(elmFrom);
			String fromReqNo = getFromReqNo(elmFrom);
			logger.verbose("@@@@@ NWCGRRRNotificationHndlr::getTxOrderLines, Processing from request no : " + fromReqNo);
			getOrderLineForFromReq(fromReqNo, docOLsForXferOrder, elmTo, vecSerialNos);
			logger.verbose("@@@@@ NWCGRRRNotificationHndlr::getTxOrderLines, Pending RR Present : " + isPendingRRPresent);
		}
		// If there is a pending RR, then create entries for all the elements from htFromReqToReqElm
		if (isPendingRRPresent) {
			if (htFromReqToReqElm.size() > 0) {
				Iterator<String> itrKeyForReadyToTriggerReqs = htFromReqToReqElm.keySet().iterator();
				while (itrKeyForReadyToTriggerReqs.hasNext()) {
					String fromReqNoOfReadyToTrigger = itrKeyForReadyToTriggerReqs.next();
					Element elmToOfReadyToTrigger = htFromReqToReqElm.get(fromReqNoOfReadyToTrigger);
					createPendingRREntry(fromReqNoOfReadyToTrigger, elmToOfReadyToTrigger, null, NWCGConstants.STATUS_READY_TO_TRIGGER);
				}
			}
		} else {
			// If it is not a pending RR, then check whether the trackable items are against the correct incident number or not. If it is not present, raise an alert
			if (vecSerialNos.size() > 0) {
				Element elmOLsForXferOrder = docOLsForXferOrder.getDocumentElement();
				logger.verbose("@@@@@ NWCGRRRNotificationHndlr::getTxOrderLines, " + "No of serials to check : " + vecSerialNos.size());
				logger.verbose("@@@@@ NWCGRRRNotificationHndlr::getTxOrderLines, " + "No of order lines in document : " + elmOLsForXferOrder.getElementsByTagName(NWCGConstants.ORDER_LINE).getLength());
				Document docTrackedLinesFromDB = getTrackedItems(vecSerialNos);
				if (docTrackedLinesFromDB == null || elmOLsForXferOrder.getElementsByTagName("NWCGTrackableItem").getLength() < 0) {
					logger.verbose("@@@@@ NWCGRRRNotificationHndlr::getTransferOrderLines, " + "There are no ROSS tracked items against the source incident/year");
					String detailDesc = "There are no trackable items against the source incident " + fromIncNo + "/" + fromIncYr + ". Incident to incident transfer issue is not created in ICBSR.";
					String desc = "No trackable items against the source incident";
					createdIssue = false;
					createIncidentAlertAndAssignToCacheAdmin(inputDoc, detailDesc, desc, false);
					logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::getTransferOrderLines (1)");
					return null;
				} else {
					// We are traversing through the list of serial numbers here to see if DB didn't return any serials that are passed as input
					Vector<String> vecSerialNosFromDB = new Vector<String>();
					Element elmTrackedItemsFromDB = docTrackedLinesFromDB.getDocumentElement();
					NodeList nlTrackedItemsFromDB = elmTrackedItemsFromDB.getElementsByTagName("NWCGTrackableItem");
					if (nlTrackedItemsFromDB != null && nlTrackedItemsFromDB.getLength() > 0) {
						for (int trackedItem = 0; trackedItem < nlTrackedItemsFromDB.getLength(); trackedItem++) {
							Element elmTrackedItem = (Element) nlTrackedItemsFromDB.item(trackedItem);
							String tmpTrackableNo = elmTrackedItem.getAttribute(NWCGConstants.SERIAL_NO);
							vecSerialNosFromDB.add(elmTrackedItem.getAttribute(NWCGConstants.SERIAL_NO));
							logger.verbose("@@@@@ NWCGRRRNotificationHndlr::getTxOrderLines, " + "Serial Nos from Trackable item table : " + tmpTrackableNo);
						}
						if (vecSerialNosFromDB != null && vecSerialNosFromDB.size() > 0) {
							for (int j = 0; j < vecSerialNos.size(); j++) {
								String serialNoFromInput = vecSerialNos.get(j);
								if (vecSerialNosFromDB.indexOf(serialNoFromInput) == -1) {
									logger.verbose("@@@@@ NWCGRRRNotificationHndlr::getTransferOrderLines, " + "Serial No " + serialNoFromInput + " is not present against the source incident/year");
									String detailDesc = "Trackable item " + serialNoFromInput + " is not present against the " + "source incident " + fromIncNo + "/" + fromIncYr + ". Incident to " + "incident transfer issue is not created in ICBSR.";
									String desc = "rackable item not found against the source incident";
									createdIssue = false;
									createIncidentAlertAndAssignToCacheAdmin(inputDoc, detailDesc, desc, false);
									logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::getTransferOrderLines (2)");
									return null;
								}
							} 
						} else {
							logger.verbose("@@@@@ NWCGRRRNotificationHndlr::getTransferOrderLines, " + "This should not happen as we are querying against the DB with serial nos.");
						} 
					} else {
						logger.verbose("@@@@@ NWCGRRRNotificationHndlr::getTransferOrderLines, " + "This should not happen as we are doing this check above.");
					} 
				} 
			} else {
			} 
		}
		logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::getTransferOrderLines");
		return docOLsForXferOrder;
	}

	/**
	 * 
	 * @param fromReqNo
	 * @param docOLsForXferOrder
	 * @param elmToReq
	 * @param vecSerialNos
	 * @throws NWCGException
	 */
	private void getOrderLineForFromReq(String fromReqNo, Document docOLsForXferOrder, Element elmToReq, Vector<String> vecSerialNos) throws NWCGException {
		logger.verbose("@@@@@ Entering NWCGResourceRequestReassignNotificationHandler::getOrderLineForFromReq");
		// Get the order lines for the given S#/FromInc/FromIncYr for either a 0001 or 0008.ex document type
		useToIncident = false;
		NodeList srcOLineList = getOrderLinesOnSourceOrToIncident(fromReqNo);
		if (srcOLineList == null || srcOLineList.getLength() < 1) {
			// No order lines found for either 0001 or 0008.ex doc type for the Source Incident No/Year/From Request Number Look for order lines utilizing the ReassignToIncident as the OrderLine/Extn/@ExtnToIncidentNo and OrderLine/Extn/@ExtnToIncidentYear
			useToIncident = true;
			srcOLineList = getOrderLinesOnSourceOrToIncident(fromReqNo);
			if (srcOLineList == null || srcOLineList.getLength() < 1) {
				// PENDING RR CHANGE 1. Check if it is present in Pending RR table
				handlePendingRR(fromReqNo, elmToReq);
				if (!isPendingRRPresent) {
					logger.verbose("@@@@@ Invalid From Request Number for given From Incident");
					logger.verbose("@@@@@ " +  NWCGConstants.AA_RR_INC_NOTIF_ERROR_004);
					String errDescDtl = "Incident number " + fromIncNo + " with year " + fromIncYr + " is not present in ICBS. Resource Reassignment " + "Notification for this Source Incident is not processed!";
					String errDesc = "Invalid Request Number for given Incident/Year.";
					createIncidentAlertAndAssignToCacheAdmin(inputDoc, errDescDtl, errDesc, false);
					throw new NWCGException(NWCGConstants.AA_RR_INC_NOTIF_ERROR_004);
				} else {
					logger.verbose("@@@@@ New entry has been added in Pending RR table...");
					logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::getTransferOrderLines (1)");
					return;
				}
			}
		}
		// Iterate over the order lines found to see if the request number is Shipped on either a 0001 or 0008.ex doc type
		Element curOLElem = null;
		Element orderLinesElm = docOLsForXferOrder.getDocumentElement();
		// Loop over all the OrderLine elements
		for (int i = 0; i < srcOLineList.getLength(); i++) {
			Node curNode = srcOLineList.item(i);
			curOLElem = (curNode instanceof Element) ? (Element) curNode : null;
			if (curOLElem == null)
				continue;
			String reqNo = "";
			Element orderLineOrderElm = null;
			Element elmOLExtn = null;
			NodeList nlOrderLine = curOLElem.getChildNodes();
			for (int j = 0; j < nlOrderLine.getLength(); j++) {
				Node tmpNode = nlOrderLine.item(j);
				if (tmpNode.getNodeType() == Node.ELEMENT_NODE) {
					Element elmOrderLineChild = (Element) tmpNode;
					if (elmOrderLineChild.getNodeName().equalsIgnoreCase("Extn")) {
						elmOLExtn = elmOrderLineChild;
						reqNo = elmOrderLineChild.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
					} else if (elmOrderLineChild.getNodeName().equalsIgnoreCase(NWCGConstants.ORDER_ELM)) {
						orderLineOrderElm = elmOrderLineChild;
					}
				}
			}
			String trimmedFromReqNo = getTrimmedRequestNumberForFLIKEQuery(fromReqNo);
			if ((reqNo.length() > trimmedFromReqNo.length()) && ((reqNo.charAt(trimmedFromReqNo.length())) != '.')) {
				continue;
			}
			String lineDocType = orderLineOrderElm.getAttribute(NWCGConstants.DOCUMENT_TYPE);
			String statusText = curOLElem.getAttribute(NWCGConstants.STATUS_ATTR);
			String maxLineStatus = curOLElem.getAttribute(NWCGConstants.MAX_LINE_STATUS);
			if (!StringUtil.isEmpty(lineDocType) && lineDocType.equals(NWCGConstants.ORDER_DOCUMENT_TYPE)) {
				if ((!StringUtil.isEmpty(statusText) && statusText.equalsIgnoreCase(NWCGConstants.SHIPPED_ORDER_STATUS)) || (!StringUtil.isEmpty(maxLineStatus) && maxLineStatus.equals(NWCGConstants.STATUS_SHIPPED))) {
					curOLElem = removeOrderStatusesNodesFromElement(curOLElem);
					String curOLK = curOLElem.getAttribute(NWCGConstants.ORDER_LINE_KEY);
					String serialNoShipped = this.getSerialNoFromShipmentLine(curOLK);
					elmOLExtn.setAttribute(NWCGConstants.EXTN_TRACKABLE_ID, serialNoShipped);
					vecSerialNos.add(serialNoShipped);
					setOrderLineAttributes(curOLElem, elmToReq);
					orderLinesElm.appendChild(docOLsForXferOrder.importNode(curNode, true));
					htFromReqToReqElm.put(fromReqNo, elmToReq);
				}
			} else if (!StringUtil.isEmpty(lineDocType) && lineDocType.equals(NWCGConstants.INC2INC_TRANSFER_ORDER_DOCUMENT_TYPE)) {
				if ((!StringUtil.isEmpty(statusText) && statusText.equalsIgnoreCase(NWCGConstants.INC2INC_ORDER_STATUS_CONFIRMED_TXT)) || (!StringUtil.isEmpty(maxLineStatus) && maxLineStatus.equalsIgnoreCase(NWCGConstants.INC2INC_ORDER_STATUS_CONFIRMED))) {
					curOLElem = removeOrderStatusesNodesFromElement(curOLElem);
					setOrderLineAttributes(curOLElem, elmToReq);
					orderLinesElm.appendChild(docOLsForXferOrder.importNode(curNode, true));
					htFromReqToReqElm.put(fromReqNo, elmToReq);
					vecSerialNos.add(elmOLExtn.getAttribute(NWCGConstants.EXTN_TRACKABLE_ID));
				} else {
					// Transfer order isn't confirmed. Happens if ROSS sends a RR for a request on a inc2inc transfer order that has not yet been confirmed.
					logger.warn("Transfer order isn't confirmed!!");
					// PENDING RR CHANGE 2. Insert new entry in NWCG_PENDING_RR table and raise an alert 1. ROSS places 9 inc 2 inc transfers from Inc A/S-1.a to Inc B/S-1.a 2. ROSS places 1 inc 2 inc transfer from Inc B/S-1.a to 1.9 to Inc C/S-1.a to 1.9 3. If Inc A/S-1.1 and S-1.2 are complete, but not S-1.3 then we need to put all the 3 rows in pending RR table This is being handled in getTransferOrderLines method
					if (shippingCacheId == null || shippingCacheId.trim().length() < 1) {
						shippingCacheId = curOLElem.getAttribute(NWCGConstants.SHIP_NODE);
					}
					createPendingRREntry(fromReqNo, elmToReq, curOLElem, NWCGConstants.STATUS_NOT_READY_TO_TRIGGER);
					isPendingRRPresent = true;
					continue;
				}
				continue;
			} else {
				// No status found on order line element
				logger.verbose("@@@@@ " + NWCGConstants.AA_RR_INC_NOTIF_ERROR_007);
				logger.verbose("@@@@@ " + "Could not determine the OrderLine status");
				String errDescDtl = "Could not determine the OrderLine status. " + "OrderLine/@MaxLineStatus nor OrderLine/@Status attributes " + " contain values!";
				String errDesc = "Could not determine the OrderLine status";
				createIncidentAlertAndAssignToCacheAdmin(inputDoc, errDescDtl, errDesc, false);
				continue;
			}
		}
		logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::getOrderLineForFromReq");
	}

	/**
	 * This method will return the from request no
	 * 
	 * @param elmReassignFromReq
	 * @return
	 * @throws NWCGException
	 */
	private String getFromReqNo(Element elmReassignFromReq) throws NWCGException {
		logger.verbose("@@@@@ Entering NWCGResourceRequestReassignNotificationHandler::getFromReqNo");
		String fromReqNo = "";
		NodeList nlReqCode = elmReassignFromReq.getElementsByTagName("RequestCode");
		if (nlReqCode == null || nlReqCode.getLength() < 1) {
			throw new NWCGException("RequestCode is not present under ReassignFromRequest");
		}
		String catalogID = "";
		String seqNo = "";
		Element elmReqCode = (Element) nlReqCode.item(0);
		NodeList nlReqCodeChilds = elmReqCode.getChildNodes();
		for (int i = 0; i < nlReqCodeChilds.getLength(); i++) {
			Node tmpNode = nlReqCodeChilds.item(i);
			if (tmpNode.getNodeType() == Node.ELEMENT_NODE) {
				Element elmReqCodeChild = (Element) tmpNode;
				// Check whether contains works or not
				if (elmReqCodeChild.getNodeName().contains("CatalogID")) {
					catalogID = elmReqCodeChild.getTextContent();
				} else if (elmReqCodeChild.getNodeName().contains("SequenceNumber")) {
					seqNo = elmReqCodeChild.getTextContent();
				}
			}
		}
		if (catalogID.length() < 1 || seqNo.length() < 1) {
			throw new NWCGException("Invalid CatalogID or invalid SequenceNumber in ReassignFromRequest");
		}
		fromReqNo = catalogID.concat("-").concat(seqNo);
		logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::getFromReqNo");
		return fromReqNo;
	}

	/**
	 * Removes all occurences of OrderStatuses node and children per OrderLine
	 * element passed to the method
	 * 
	 * @param curOLElem
	 * @return curOLElem
	 */
	private Element removeOrderStatusesNodesFromElement(Element curOLElem) {
		logger.verbose("@@@@@ Entering NWCGResourceRequestReassignNotificationHandler::removeOrderStatusesNodesFromElement");
		final String methodName = "removeOrderStatusesNodesFromElement";
		Vector<Node> orderStatusesElmsToRemove = new Vector<Node>();
		NodeList nl = curOLElem.getElementsByTagName("*");
		if (nl != null && nl.getLength() > 0) {
			for (int j = 0; j < nl.getLength(); j++) {
				Node crNode = nl.item(j);
				String localName = crNode.getLocalName();
				String nodeName = crNode.getNodeName();
				logger.verbose("@@@@@ crNode.getLocalName() :: " + localName);
				logger.verbose("@@@@@ crNode.getNodeName() :: " + nodeName);
				if (!StringUtil.isEmpty(nodeName) && nodeName.equals(NWCGConstants.ORDER_STATUSES_ELM)) {
					orderStatusesElmsToRemove.add(crNode);
					continue;
				} else if (!StringUtil.isEmpty(localName) && localName.equals(NWCGConstants.ORDER_STATUSES_ELM)) {
					orderStatusesElmsToRemove.add(crNode);
					continue;
				}
			}
		}
		while (orderStatusesElmsToRemove.size() > 0) {
			Node x = orderStatusesElmsToRemove.remove(0);
			x.getParentNode().removeChild(x);
		}
		logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::removeOrderStatusesNodesFromElement");
		return curOLElem;
	}

	/**
	 * Returns the "trimmed" input Request Number e.g. S-1000487.1.2.1 "trims"
	 * to S-1000487.1
	 * 
	 * @return String
	 */
	private String getTrimmedRequestNumberForFLIKEQuery(String trimmedReqNo) {
		logger.verbose("@@@@@ Entering NWCGResourceRequestReassignNotificationHandler::getTrimmedRequestNumberForFLIKEQuery");
		final String methodName = "getTrimmedRequestNumberForFLIKEQuery";
		// Trim the From Request Number down to it's first extended number e.g. : S-1000487.1.2.1 is trimmed to S-1000487.1 index: 0123456789012345 In our example, 9
		int indexOfFirstDot = trimmedReqNo.indexOf('.', 0);
		// In our example, 15 length, character array is indexed 0 to 14.
		int stopIndex = trimmedReqNo.length();
		if (indexOfFirstDot != -1) {
			int indexOfLastDot = trimmedReqNo.lastIndexOf('.');
			// If the indices of the first and last dot are the same then we have a request number such as S-1000487.1
			if (indexOfFirstDot != indexOfLastDot) {
				stopIndex = trimmedReqNo.indexOf('.', indexOfFirstDot + 1);
				if (stopIndex == -1)
					stopIndex = trimmedReqNo.length();
			}
			trimmedReqNo = trimmedReqNo.substring(0, stopIndex);
		}
		logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::getTrimmedRequestNumberForFLIKEQuery");
		return trimmedReqNo;
	}

	/**
	 * 
	 * @param reqNo
	 * @param opTemplateName
	 * @return
	 * @throws NWCGException
	 */
	private Document getOLsForABaseReqInAnInc(String reqNo, String opTemplateName) throws NWCGException {
		logger.verbose("@@@@@ Entering NWCGResourceRequestReassignNotificationHandler::getOLsForABaseReqInAnInc");
		final String methodName = "getOLsForABaseReqInAnInc";
		Document docGetOLListOP = null;
		try {
			Document docGetOLListIP = XMLUtil.createDocument(NWCGConstants.ORDER_LINE);
			Element elmGetOL = docGetOLListIP.getDocumentElement();
			Element elmOLExtn = docGetOLListIP.createElement(NWCGConstants.EXTN_ELEMENT);
			elmGetOL.appendChild(elmOLExtn);
			elmOLExtn.setAttribute("ExtnRequestNoQryType", "FLIKE");
			elmOLExtn.setAttribute(NWCGConstants.EXTN_REQUEST_NO, reqNo);
			Element elmOrder = docGetOLListIP.createElement(NWCGConstants.ORDER_ELM);
			elmGetOL.appendChild(elmOrder);
			Element elmOrderExtn = docGetOLListIP.createElement(NWCGConstants.EXTN_ELEMENT);
			elmOrder.appendChild(elmOrderExtn);
			if (!useToIncident) {
				elmOrder.setAttribute(NWCGConstants.DOCUMENT_TYPE, NWCGConstants.ORDER_DOCUMENT_TYPE);
				elmOrderExtn.setAttribute(NWCGConstants.INCIDENT_NO, fromIncNo);
				elmOrderExtn.setAttribute(NWCGConstants.INCIDENT_YEAR, fromIncYr);
			} else {
				elmOrder.setAttribute(NWCGConstants.DOCUMENT_TYPE, NWCGConstants.INC2INC_TRANSFER_ORDER_DOCUMENT_TYPE);
				elmOrderExtn.setAttribute(NWCGConstants.EXTN_TO_INCIDENT_NO, fromIncNo);
				elmOrderExtn.setAttribute(NWCGConstants.EXTN_TO_INCIDENT_YR, fromIncYr);
			}
			docGetOLListOP = CommonUtilities.invokeAPI(myEnvironment, opTemplateName, NWCGConstants.API_GET_ORDER_LINE_LIST, docGetOLListIP);
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException :: " + pce);
			pce.printStackTrace();
			throw new NWCGException(pce);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Execption :: " + e);
			e.printStackTrace();
			throw new NWCGException(e);
		}
		logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::getOLsForABaseReqInAnInc");
		return docGetOLListOP;
	}

	/**
	 * Returns a NodeList of all order lines for the given Source Incident
	 * Number/Year and Request Number
	 * 
	 * @return NodeList null if no 001/shipped/ross resource item/trackable
	 *         order lines are found
	 */
	private NodeList getOrderLinesOnSourceOrToIncident(String fromRequestNo) throws NWCGException {
		logger.verbose("@@@@@ Entering NWCGResourceRequestReassignNotificationHandler::getOrderLinesOnSourceOrToIncident");
		final String methodName = "getOrderLinesOnSourceIncident";
		// Trim the From Request Number down to it's first extended number
		String trimmedFromReqNo = getTrimmedRequestNumberForFLIKEQuery(fromRequestNo);
		// Call getOLsForABaseReqInAnInc method to get all order lines for the given fromIncNo, fromIncYr, and FLIKE(trimmedFromReqNo) Note: This will span across document types e.g.: 0001 and 0008.ex order lines may be included in the line list returned.
		Document apiOutputDoc = null;
		try {
			apiOutputDoc = getOLsForABaseReqInAnInc(trimmedFromReqNo, NWCGConstants.GET_ORDER_LINE_LIST_OP_TEMPLATE_RR);
		} catch (Exception e) {
			throw new NWCGException(e);
		}
		// Check if the API returned null
		if (apiOutputDoc == null) {
			logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::getOrderLinesOnSourceOrToIncident (1)");
			return null;
		}

		Element docElm = apiOutputDoc.getDocumentElement();
		NodeList orderLineNodeList = docElm.getElementsByTagName(NWCGConstants.ORDER_LINE);

		// If no lines are returned for the given Incident No/Year by getOrderLineList, MCF will not return TotalLineList attribute
		if (orderLineNodeList == null || orderLineNodeList.getLength() < 1) {
			logger.verbose("@@@@@ No orderLines found! (Inc/Year/ReqNo) (" + fromIncNo + "/" + fromIncYr + "/" + fromRequestNo + ")");
			logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::getOrderLinesOnSourceOrToIncident (2)");
			return null;
		}
		logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::getOrderLinesOnSourceOrToIncident");
		return orderLineNodeList;
	}

	/**
	 * Populates the given order line <code>Element curOLElem</code>
	 * 
	 * Also populates the OrderLine/Order/@ExtnNavInfo with whatever the
	 * shipping method is contained in the ReassignToRequest element of the
	 * input RR notification. This attribute is then copied up to the
	 * Order/Extn/@ExtnNavInfo attribute after the method call to get the Order
	 * input XML for the inc2inc transfer order.
	 * 
	 * @param inputDoc
	 *            Document Input XML DOM passed into process() of the RR handler
	 * @param curOLElem
	 *            Element An OrderLine element with a child Extn Element
	 */
	private void setOrderLineAttributes(Element elmCurOL, Element elmToReq) {
		logger.verbose("@@@@@ Entering NWCGResourceRequestReassignNotificationHandler::setOrderLineAttributes");
		final String methodName = "setOrderLineAttributes";
		elmCurOL.removeAttribute(NWCGConstants.ORDER_HEADER_KEY);
		elmCurOL.removeAttribute(NWCGConstants.MAX_LINE_STATUS);
		elmCurOL.removeAttribute(NWCGConstants.STATUS_ATTR);
		Element elmOrderLineExtn = XMLUtil.getFirstElementByName(elmCurOL, NWCGConstants.EXTN_ELEMENT);
		Element elmReplaceIndic = XMLUtil.getFirstElementByName(elmToReq, NWCGConstants.REPLACEMENT_IND_ELM);
		Element elmSpecialNeeds = XMLUtil.getFirstElementByName(elmToReq, NWCGConstants.SPECIAL_NEEDS_ELM);
		Element elmContactName = XMLUtil.getFirstElementByName(elmToReq, NWCGConstants.SHIP_CONTACT_NAME_INPUT_RQST_ATTR);
		Element elmContactPhone = XMLUtil.getFirstElementByName(elmToReq, NWCGConstants.SHIP_CONTACT_PHONE_INPUT_RQST_ATTR);
		// Default the shipping method to Shipping Address
		elmOrderLineExtn.setAttribute(NWCGConstants.EXTN_NAV_INFO_ATTR, NWCGConstants.SHIPPING_ADDRESS);
		Element elmToReqNo = XMLUtil.getFirstElementByName(elmToReq, "SequenceNumber");
		String toReqSeqNo = elmToReqNo.getTextContent();
		String toReqNo = "S-".concat(toReqSeqNo);
		// Set the Order/Extn/@ExtnRequestNo as the S-<sequenceNumber> from the ToRequest element of the notification
		elmOrderLineExtn.setAttribute(NWCGConstants.EXTN_REQUEST_NO, toReqNo);
		// Set Order/Instructions/Instruction element if the SpecialNeeds element on the ToRequest in the IB Notification is not empty
		if (elmSpecialNeeds != null && !StringUtil.isEmpty(elmSpecialNeeds.getTextContent())) {
			Element ins = elmCurOL.getOwnerDocument().createElement(NWCGConstants.INSTRUCTIONS_ELEMENT);
			Element in = elmCurOL.getOwnerDocument().createElement(NWCGConstants.INSTRUCTION_ELEMENT);
			elmCurOL.appendChild(ins);
			ins.appendChild(in);
			in.setAttribute(NWCGConstants.INSTRUCTION_TEXT_ATTR, elmSpecialNeeds.getTextContent());
			in.setAttribute(NWCGConstants.INSTRUCTION_TYPE_ATTR, "ROSS Notes");
		}
		// Set Order/Extn/@ExtnShippingContactName
		if (elmContactName != null && !StringUtil.isEmpty(elmContactName.getTextContent())) {
			elmOrderLineExtn.setAttribute(NWCGConstants.SHIP_CONTACT_NAME_ATTR, elmContactName.getTextContent());
		}
		// Set Order/Extn/@ExtnShippingContactPhone
		if (elmContactPhone != null && !StringUtil.isEmpty(elmContactPhone.getTextContent())) {
			elmOrderLineExtn.setAttribute(NWCGConstants.SHIP_CONTACT_PHONE_ATTR, elmContactPhone.getTextContent());
		}
		// Set the Order/@OrderType to Replacement if the replacement indicator is set to 'Y' on the IB notification
		if (elmReplaceIndic != null && !StringUtil.isEmpty(elmReplaceIndic.getTextContent()) && elmReplaceIndic.getTextContent().equalsIgnoreCase(NWCGConstants.YES)) {
			elmCurOL.getOwnerDocument().getDocumentElement().setAttribute(NWCGConstants.ORDER_TYPE, NWCGConstants.ORDER_TYPE_REPLACEMENT);
		}
		logger.verbose("@@@@@ OL/Order/Extn/@ExtnTrackableId: " + elmOrderLineExtn.getAttribute(NWCGConstants.EXTN_TRACKABLE_ID));
		logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::setOrderLineAttributes");
	}

	/**
	 * Calls the SDF service configured for the resource entry in
	 * <code>NWCGAnAImpl.properties</code> to get a list of NWCGTrackableItem
	 * Elements for the Source Incident ("From Incident") and Source Incident
	 * Year
	 * 
	 * @return Document The output of "nwcg.icbs.gettrackableitemlist.service"
	 *         in <code>resources/extn/NWCGAnAImpl.properties</code> file.
	 */
	private Document getTrackedItems(Vector<String> serialNos) throws NWCGException {
		logger.verbose("@@@@@ Entering NWCGResourceRequestReassignNotificationHandler::getTrackedItems");
		final String methodName = "getTrackedItems";
		Document docTrackedItemsOP = null;
		try {
			Document docTrackedItemsIP = XMLUtil.createDocument("NWCGTrackableItem");
			Element elmRootTrackedItemsIP = docTrackedItemsIP.getDocumentElement();
			elmRootTrackedItemsIP.setAttribute(NWCGConstants.STATUS_INCIDENT_NO, fromIncNo);
			elmRootTrackedItemsIP.setAttribute(NWCGConstants.STATUS_INCIDENT_YR, fromIncYr);
			elmRootTrackedItemsIP.setAttribute(NWCGConstants.SERIAL_STATUS, NWCGConstants.SERIAL_STATUS_ISSUED);
			Element elmComplexQry = docTrackedItemsIP.createElement("ComplexQuery");
			elmRootTrackedItemsIP.appendChild(elmComplexQry);
			elmComplexQry.setAttribute("Operator", "AND");
			Element elmComplexOrQry = docTrackedItemsIP.createElement("Or");
			elmComplexQry.appendChild(elmComplexOrQry);
			for (int i = 0; i < serialNos.size(); i++) {
				String serialNo = serialNos.get(i);
				Element elmComplexOrSerialNoQry = docTrackedItemsIP.createElement("Or");
				elmComplexOrQry.appendChild(elmComplexOrSerialNoQry);
				Element elmComplexOrSerialNoExprQry = docTrackedItemsIP.createElement("Exp");
				elmComplexOrSerialNoQry.appendChild(elmComplexOrSerialNoExprQry);
				elmComplexOrSerialNoExprQry.setAttribute("Name", NWCGConstants.SERIAL_NO);
				elmComplexOrSerialNoExprQry.setAttribute("Value", serialNo);
			}
			logger.verbose("@@@@@ NWCGResourceRequestReassignNotificationHandler::getTrackedItems, " + "Tracked Items input XML : " + XMLUtil.extractStringFromDocument(docTrackedItemsIP));
			docTrackedItemsOP = CommonUtilities.invokeService(this.myEnvironment, NWCGConstants.NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE, docTrackedItemsIP);
			if (docTrackedItemsOP != null) {
				logger.verbose("@@@@@ NWCGRRRNotificationHndlr::getTrackedItems, Output from Trackable item table : " + XMLUtil.extractStringFromDocument(docTrackedItemsOP));
			}
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Parser Configuration Exception while making " + NWCGConstants.NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE + " : " + pce.getMessage());
			logger.error("!!!!! Caught ParserConfigurationException :: " + pce);
			pce.printStackTrace();
			throw new NWCGException(pce);
		} catch (Exception e) {
			logger.error("!!!!! Parser Configuration Exception while making " + NWCGConstants.NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE + " : " + "Exception : " + e.getMessage());
			logger.error("!!!!! Caught General Execption :: " + e);
			e.printStackTrace();
			throw new NWCGException(e);
		}
		logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::getTrackedItems");
		return docTrackedItemsOP;
	}

	/**
	 * This method will be called on exceptions or on missing data while
	 * processing Resource Reassignment Notifications. It will raise an alert in
	 * NWCG_INCIDENT_FAILURE queue. It will get incident number, year and
	 * customer id based on class variable values.
	 * 
	 * @param env
	 * @param inputDoc
	 * @param errDesc
	 * @return true If the alert creation was successful
	 */
	private void createIncidentAlertAndAssignToCacheAdmin(Document doc, String detailDesc, String desc, boolean succFail) {
		logger.verbose("@@@@@ Entering NWCGResourceRequestReassignNotificationHandler::createIncidentAlertAndAssignToCacheAdmin");
		final String methodName = "createIncidentAlertAndAssignToCacheAdmin";
		HashMap<String, String> inboxReferencesMap = new HashMap<String, String>();
		inboxReferencesMap.put(NWCGConstants.ALERT_SOAP_MESSAGE, inputDoc.getDocumentElement().getAttribute(NWCGConstants.INC_NOTIF_MSGNAME_ATTR));
		// If we find a user Id to assign the alert to, then do so, otherwise the raiseAlertAndAssignToUser method will leave the alert unassigned
		String toIncidentDispatchUnitId = getUnitIdTypeFromToIncident("DispatchOrganization");
		inboxReferencesMap.put(NWCGConstants.ALERT_SRC_INCIDENT_NO, fromIncNo);
		inboxReferencesMap.put(NWCGConstants.ALERT_SRC_INCIDENT_YEAR, fromIncYr);
		inboxReferencesMap.put(NWCGConstants.ALERT_DEST_INCIDENT_NO, toIncNo);
		inboxReferencesMap.put(NWCGConstants.ALERT_DEST_INCIDENT_YEAR, toIncYr);
		inboxReferencesMap.put(NWCGConstants.ALERT_TO_DISP_UNIT_ID, toIncidentDispatchUnitId);
		inboxReferencesMap.put(NWCGConstants.ALERT_DIST_ID, distributionId);
		inboxReferencesMap.put(NWCGAAConstants.NAME, NWCGConstants.RESOURCE_REASSIGN_NOTIFICATION);
		inboxReferencesMap.put(NWCGConstants.ALERT_DESC, desc);
		// Add the Source IncidentKey
		if (!StringUtil.isEmpty(fromIncidentKey)) {
			inboxReferencesMap.put(NWCGConstants.FROM_INCIDENTKEY, fromIncidentKey);
		}
		// Add the Destination IncidentKey
		if (!StringUtil.isEmpty(toIncidentKey)) {
			inboxReferencesMap.put(NWCGConstants.TO_INCIDENTKEY, toIncidentKey);
		}
		// Add the Inc2Inc Order Header Key
		if (!StringUtil.isEmpty(inc2incKey)) {
			inboxReferencesMap.put("Incident To Incident Transfer Order Link", inc2incKey);
		}
		// Grab from the BillingOrganization UnitIDType Prefix+Suffix / Customer ID from the input ReassignToIncident element
		String customerId = getUnitIdTypeFromToIncident(NWCGConstants.INC_NOTIF_BILLINGORG_ELEMENT);
		// Unlike the Place Request Message where we get a mandatory "PlaceToUnitID" indicating the Shipping Cache / Ship Node, RR notifcations do not contain the same Element, so we have to default to first try and use the Ship Node the original request shipped out from, else default to the CacheOrganization In the ToIncident element of the notification.
		String cacheToUseAsShipNodeOnInc2Inc = (!StringUtil.isEmpty(shippingCacheId)) ? shippingCacheId : defaultCacheForDispatch;
		if (!StringUtil.isEmpty(cacheToUseAsShipNodeOnInc2Inc)) {
			inboxReferencesMap.put(NWCGConstants.CACHE_ID_ATTR, cacheToUseAsShipNodeOnInc2Inc);
			inboxReferencesMap.put(NWCGConstants.ALERT_SHIPNODE_KEY, cacheToUseAsShipNodeOnInc2Inc);
		} else {
			inboxReferencesMap.put(NWCGConstants.PRIMARY_CACHE_ID_ALERT_NAME, "None specified in reassignment notification");
		}
		inboxReferencesMap.put(NWCGConstants.ALERT_CUST_ID, customerId);
		String alertQ = NWCGConstants.EMPTY_STRING;
		StringBuffer sb = new StringBuffer(NWCGConstants.EMPTY_STRING);
		if (succFail) {
			if (raisedPendingRRAlert) {
				alertQ = NWCGConstants.Q_PENDING_RR;
				sb.append(detailDesc);
			} else {
				alertQ = NWCGConstants.Q_RADIOS_SUCCESS;
				sb.append("A Resource Reassignment has been received by ICBSR from ROSS and ");
				sb.append("has been processed successfully! This alert may be closed!");
			}
		} else {
			alertQ = NWCGConstants.Q_RADIOS_FAILURE;
			sb.append("A Resource Reassignment has been received by ICBSR from ROSS. ");
			sb.append("An ICBSR user for this cache needs to add ICBSR Account Codes ");
			sb.append("to the Destination Incident and then confirm the Draft ");
			sb.append("Incident to Incident Transfer Order via the link to the right.");
			sb.append("All new Incidents created by ICBSR require the entry of ICBSR Account Codes: ");
			sb.append("either a Forest Service (FS) Account Code and an Override Code, ");
			sb.append("or an Other Account Code and an Override Code, or all three ");
			sb.append("FBMS values (Cost Center, Functional Area, Work Breakdown Structure) ");
			sb.append("to compose a Bureau of Land Management (BLM) Account Code.");
		}
		if (createdIssue) {
			inboxReferencesMap.put("Comment1", sb.toString());
		}
		String userIdToAssign = CommonUtilities.getAdminUserForCache(myEnvironment, cacheToUseAsShipNodeOnInc2Inc);
		CommonUtilities.raiseAlertAndAssigntoUser(myEnvironment, alertQ, detailDesc, userIdToAssign, doc, inboxReferencesMap);
		logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::createIncidentAlertAndAssignToCacheAdmin");
	}

	/**
	 * 
	 * @param localNodeName
	 * @return
	 */
	private String getUnitIdTypeFromToIncident(String localNodeName) {
		logger.verbose("@@@@@ Entering NWCGResourceRequestReassignNotificationHandler::getUnitIdTypeFromToIncident");
		NodeList nl = inputDoc.getDocumentElement().getElementsByTagName(localNodeName);
		String retVal = localNodeName;
		if (nl == null || nl.getLength() < 1)
			return retVal;
		StringBuffer sb = new StringBuffer();
		if (nl.getLength() == 1) {
			Node n = nl.item(0);
			NodeList childNodes = n.getChildNodes();
			if (childNodes != null && childNodes.getLength() >= 2) {
				for (int i = 0; i < childNodes.getLength(); i++) {
					Node cN = childNodes.item(i);
					if (cN.getNodeName().indexOf("refix") != -1) {
						sb.append(cN.getTextContent());
					} else if (cN.getNodeName().indexOf("uffix") != -1) {
						sb.append(cN.getTextContent());
					}
				}
			}
		}
		retVal = sb.toString();
		logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::getUnitIdTypeFromToIncident");
		return retVal;
	}

	/**
	 * Calls changeOrder for the given order to apply the given hold type to the
	 * Inc2Inc order created.
	 * 
	 * @param holdType
	 *            String
	 */
	private void placeInc2IncOrderOnHoldWithHoldType(String holdType) throws NWCGException {
		logger.verbose("@@@@@ Entering NWCGResourceRequestReassignNotificationHandler::placeInc2IncOrderOnHoldWithHoldType");
		final String methodName = "placeOrderOnHoldWithHoldType";
		if (StringUtil.isEmpty(inc2incKey)) {
			logger.verbose("@@@@@ Unable to apply hold type to Incident to Incident transfer order!");
			throw new NWCGException(NWCGConstants.AA_RR_INC_NOTIF_ERROR_005);
		}
		try {
			Document changeOrderIp = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
			Element chgOrderDocElm = changeOrderIp.getDocumentElement();
			Element chgOrderExtnElm = changeOrderIp.createElement(NWCGConstants.EXTN_ELEMENT);
			chgOrderDocElm.appendChild(chgOrderExtnElm);
			chgOrderDocElm.setAttribute(NWCGConstants.ORDER_HEADER_KEY, inc2incKey);
			chgOrderDocElm.setAttribute(NWCGConstants.OVERRIDE, NWCGConstants.YES);
			Element orderHoldTypes = changeOrderIp.createElement(NWCGConstants.ORDER_HOLD_TYPES);
			Element orderHoldType = changeOrderIp.createElement(NWCGConstants.ORDER_HOLD_TYPE);
			chgOrderDocElm.appendChild(orderHoldTypes);
			orderHoldTypes.appendChild(orderHoldType);
			orderHoldType.setAttribute(NWCGConstants.HOLD_TYPE_ATTR, holdType);
			orderHoldType.setAttribute(NWCGConstants.STATUS_ATTR, "1100");
			String chOr = XMLUtil.extractStringFromDocument(changeOrderIp);
			Document doc = CommonUtilities.invokeAPI(myEnvironment, NWCGConstants.API_CHANGE_ORDER, changeOrderIp);
			chOr = XMLUtil.extractStringFromDocument(doc);
		} catch (Exception e) {
			logger.error("!!!!! Exception while calling changeOrder API to add hold type: " + holdType + " Exception : " + e.getMessage());
			logger.error("!!!!! Caught General Exception :: " + e);
			e.printStackTrace();
			throw new NWCGException(e);
		}
		logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::placeInc2IncOrderOnHoldWithHoldType");
	}

	/**
	 * This method will create an entry in NWCG_PENDING_RR table with status of
	 * NOT_READY_TO_TRIGGER and will raise an alert Line Status can be
	 * NOT_READY_TO_TRIGGER, READY_TO_TRIGGER
	 * 
	 * @param fromReqNo
	 * @param elmToReq
	 */
	private void createPendingRREntry(String fromReqNo, Element elmToReq, Element elmInc2IncLine, String lineStatus) {
		logger.verbose("@@@@@ Entering NWCGResourceRequestReassignNotificationHandler::createPendingRREntry");
		Element elmToReqNo = XMLUtil.getFirstElementByName(elmToReq, "SequenceNumber");
		String toReqSeqNo = elmToReqNo.getTextContent();
		String toReqNo = "S-".concat(toReqSeqNo);
		logger.verbose("@@@@@ NWCGRRRNotificationHndlr::createPendingRREntry, From Req No : " + fromReqNo + ", To Req No : " + toReqNo);
		try {
			Document docPendRR = XMLUtil.createDocument("NWCGPendingRr");
			Element elmPendRR = docPendRR.getDocumentElement();
			elmPendRR.setAttribute("DistributionID", distributionId);
			elmPendRR.setAttribute("Message", XMLUtil.extractStringFromDocument(inputDoc));
			elmPendRR.setAttribute("FromIncidentNo", fromIncNo);
			elmPendRR.setAttribute("FromIncidentYear", fromIncYr);
			elmPendRR.setAttribute("FromRequestNo", fromReqNo);
			elmPendRR.setAttribute("ToIncidentNo", toIncNo);
			elmPendRR.setAttribute("ToIncidentYear", toIncYr);
			elmPendRR.setAttribute("ToRequestNo", toReqNo);
			elmPendRR.setAttribute("Status", lineStatus);
			CommonUtilities.invokeService(myEnvironment, NWCGConstants.SVC_CREATE_PENDING_RR, docPendRR);
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException :: " + pce);
			pce.printStackTrace();
		} catch (SAXException se) {
			logger.error("!!!!! Caught SAXException :: " + se);
			se.printStackTrace();
		} catch (IOException ioe) {
			logger.error("!!!!! Caught IOException :: " + ioe);
			ioe.printStackTrace();
		} catch (TransformerException te) {
			logger.error("!!!!! Caught TransformerException :: " + te);
			te.printStackTrace();
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			e.printStackTrace();
		}
		if (!raisedPendingRRAlert) {
			raisedPendingRRAlert = true;
			String alertDesc = "";
			if (elmInc2IncLine != null) {
				String orderNo = ((Element) elmInc2IncLine.getElementsByTagName("Order").item(0)).getAttribute(NWCGConstants.ORDER_NO);
				alertDesc = "Pending RR : Waiting for original incident " + "to incident transfer issue completion. One of them is " + orderNo;
			} else {
				alertDesc = "Pending RR : This resource reassignment is waiting on another " + "incident to incident transfer that is still in PENDING RR List";
			}
			createIncidentAlertAndAssignToCacheAdmin(inputDoc, alertDesc, alertDesc, true);
		}
		logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::createPendingRREntry");
	}

	/**
	 * This method will check if there is a pending RR line waiting for another
	 * inc2inc transfer or for another request line in this table. If so, it
	 * will create an entry with status of NOT_READY_TO_TRIGGER
	 * 
	 * @param fromReqNo
	 * @param elmToReq
	 * @return
	 */
	private void handlePendingRR(String fromReqNo, Element elmToReq) {
		logger.verbose("@@@@@ Entering NWCGResourceRequestReassignNotificationHandler::handlePendingRR");
		try {
			Document docPendRR = XMLUtil.createDocument("NWCGPendingRr");
			Element elmPendRR = docPendRR.getDocumentElement();
			elmPendRR.setAttribute("ToIncidentNo", fromIncNo);
			elmPendRR.setAttribute("ToIncidentYear", fromIncYr);
			elmPendRR.setAttribute("ToRequestNo", fromReqNo);
			Element elmComplexQry = docPendRR.createElement("ComplexQuery");
			elmPendRR.appendChild(elmComplexQry);
			elmComplexQry.setAttribute("Operator", "AND");
			Element elmComplexOrQry = docPendRR.createElement("Or");
			elmComplexQry.appendChild(elmComplexOrQry);
			Element elmComplexOrStatus1Qry = docPendRR.createElement("Or");
			elmComplexOrQry.appendChild(elmComplexOrStatus1Qry);
			Element elmComplexOrStatus1ExprQry = docPendRR.createElement("Exp");
			elmComplexOrStatus1Qry.appendChild(elmComplexOrStatus1ExprQry);
			elmComplexOrStatus1ExprQry.setAttribute("Name", "Status");
			elmComplexOrStatus1ExprQry.setAttribute("Value", NWCGConstants.STATUS_READY_TO_TRIGGER);
			Element elmComplexOrStatus2Qry = docPendRR.createElement("Or");
			elmComplexOrQry.appendChild(elmComplexOrStatus2Qry);
			Element elmComplexOrStatus2ExprQry = docPendRR.createElement("Exp");
			elmComplexOrStatus2Qry.appendChild(elmComplexOrStatus2ExprQry);
			elmComplexOrStatus2ExprQry.setAttribute("Name", "Status");
			elmComplexOrStatus2ExprQry.setAttribute("Value", NWCGConstants.STATUS_NOT_READY_TO_TRIGGER);
			Document docOPPendingRR = CommonUtilities.invokeService(myEnvironment, NWCGConstants.SVC_GET_PENDING_RR_LIST, docPendRR);
			if ((docOPPendingRR != null) && ((docOPPendingRR.getDocumentElement().getElementsByTagName("NWCGPendingRr")).getLength() > 0)) {
				isPendingRRPresent = true;
				this.createPendingRREntry(fromReqNo, elmToReq, null, NWCGConstants.STATUS_NOT_READY_TO_TRIGGER);
			}
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException :: " + pce);
			pce.printStackTrace();
		} catch (SAXException se) {
			logger.error("!!!!! Caught SAXException :: " + se);
			se.printStackTrace();
		} catch (IOException ioe) {
			logger.error("!!!!! Caught IOException :: " + ioe);
			ioe.printStackTrace();
		} catch (TransformerException te) {
			logger.error("!!!!! Caught TransformerException :: " + te);
			te.printStackTrace();
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			e.printStackTrace();
		}
		logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::handlePendingRR");
	}

	/**
	 * This method will return serial number from shipment line details.
	 * 
	 * @param curOLK
	 * @return
	 */
	private String getSerialNoFromShipmentLine(String curOLK) throws NWCGException {
		logger.verbose("@@@@@ Entering NWCGResourceRequestReassignNotificationHandler::getSerialNoFromShipmentLine");
		String serialNoShipped = "";
		Document docGetShipmentLineListOP = null;
		try {
			docGetShipmentLineListOP = CommonUtilities.invokeAPI(myEnvironment, "NWCGStatusNFESResourceRequestHandler_getShipmentLineList", "getShipmentLineList", "<ShipmentLine OrderLineKey='" + curOLK + "'/>");
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			e.printStackTrace();
			throw new NWCGException(NWCGConstants.AA_RR_INC_NOTIF_ERROR_004);
		}
		NodeList nlShipmentLine = docGetShipmentLineListOP.getElementsByTagName("ShipmentLine");
		Element elemShipmentLine = null;
		if (nlShipmentLine != null && nlShipmentLine.getLength() > 0) {
			elemShipmentLine = (Element) nlShipmentLine.item(0);
			NodeList nlShipmentTags = elemShipmentLine.getElementsByTagName(NWCGConstants.SHIPMENT_TAG_SERIAL);
			if (nlShipmentTags != null && nlShipmentTags.getLength() > 0) {
				serialNoShipped = ((Element) nlShipmentTags.item(0)).getAttribute(NWCGConstants.SERIAL_NO);
			}
		}
		logger.verbose("@@@@@ Exiting NWCGResourceRequestReassignNotificationHandler::getSerialNoFromShipmentLine");
		return serialNoShipped;
	}
}