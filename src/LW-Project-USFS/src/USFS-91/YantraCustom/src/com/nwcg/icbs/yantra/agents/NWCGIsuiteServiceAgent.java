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

package com.nwcg.icbs.yantra.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URL;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.ajax.NWCGCommandConstants;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGAgentLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.util.api.NWCGWebServiceUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;

/**
 * Converting iSuite Interface to a custom non-task based agent instead of using
 * the real time invocation
 * 
 * @author Barr, Conor
 * @since October 01, 2013
 * @version
 */
public class NWCGIsuiteServiceAgent extends YCPBaseAgent {

	// To save the last key - used in getShipmentList within the complex query
	private static String lastShipmentKey = "";

	private static YFCLogCategory logger = NWCGAgentLogger
			.instance(NWCGIsuiteServiceAgent.class);

	/**
	 * public default constructor
	 */
	public NWCGIsuiteServiceAgent() {
		super();
		logger.verbose("NWCGIsuiteService agent started!");
	}

	/**
	 * Invokes getShipmentList based on EXTN_ISUITE_PROCESSED in YFS_Shipment
	 * table If equal to 'N' the shipment is added to the list for Isuite
	 * processing.
	 */
	public List getJobs(YFSEnvironment env, Document inputDoc) throws Exception {

		startTimer("NWCGIsuiteServiceAgent::getJobs()");

		logger.debug("--------------------------------------------------");
		logger.debug("     NWCGIsuiteServiceResultsAgent::getJobs()     ");
		logger.debug("--------------------------------------------------");

		// initialise lastRecord count
		int lastRecord = 0;

		// Invoke getShipmentList
		Document docGetShipmentList = getShipmentList(env, lastShipmentKey);

		logger.verbose("getJobs :: docGetShipmentList " + XMLUtil.getXMLString(docGetShipmentList));

		List<Document> sentList = new ArrayList<Document>();

		NodeList nlShipment = docGetShipmentList
				.getElementsByTagName(NWCGConstants.SHIPMENT_ELEMENT);

		if (nlShipment != null) {
			// Loop through list and find records for ISuite Processing
			for (int i = 0; i < nlShipment.getLength(); i++) {
				Element elemShipmentTag = (Element) nlShipment.item(i);
				String sShipmentKey = (String) (elemShipmentTag
						.getAttribute(NWCGConstants.SHIPMENT_KEY));

				NodeList nlExtn = elemShipmentTag
						.getElementsByTagName(NWCGConstants.EXTN);

				if (nlExtn != null) {
					Element elemExtnTag = (Element) nlExtn.item(0);

					String sExtnValue = (String) (elemExtnTag
							.getAttribute(NWCGConstants.EXTN_ISUITE_PROCESSED));

					if (sExtnValue != null) {
						// Add to job list
						Document ShipmentDoc = XMLUtil.newDocument();
						Element el_Shipment = ShipmentDoc
								.createElement(NWCGConstants.SHIPMENT_ELEMENT);

						ShipmentDoc.appendChild(el_Shipment);

						if (sShipmentKey != null && (!sShipmentKey.equals(""))) {
							el_Shipment.setAttribute(
									NWCGConstants.SHIPMENT_KEY, sShipmentKey);

							logger.debug("Add following document to list for processing:-"
									+ XMLUtil.getXMLString(ShipmentDoc));

							sentList.add(ShipmentDoc);
						}
					}
				}
			}

			// Get Last shipmentKey for complex query.
			if (nlShipment.getLength() > 0) {
				lastRecord = nlShipment.getLength() - 1;

				Element elemLastShipmentTag = (Element) nlShipment
						.item(lastRecord);

				if (elemLastShipmentTag != null) {
					lastShipmentKey = (String) (elemLastShipmentTag
							.getAttribute(NWCGConstants.SHIPMENT_KEY));
					logger.debug("getJobs():  Last Shipment Key: "
							+ lastShipmentKey);

				}
			}

		}

		if (sentList == null)
			return null;

		logger.debug("getJobs returning " + sentList.size()
				+ " msgs back to the agent framework.");

		endTimer("NWCGIsuiteServiceAgent::getJobs()");

		return (List) sentList;
	}

	/**
	 * Takes the document list (Shipments) from getJobs() Invokes the
	 * iSuiteService method on each shipment Calls changeShipment to mark each
	 * shipment as ISuite Processed e.g. EXTN_ISUITE_PROCESSED = 'Y'
	 */
	public void executeJob(YFSEnvironment env, Document inDoc) throws Exception {

		startTimer("NWCGIsuiteService::executeJob");
		logger.debug("--------------------------------------------------");
		logger.debug("           NWCGIsuiteService::executeJob()        ");
		logger.debug("--------------------------------------------------");

		if (inDoc != null) {
			NodeList nlShipmentList = inDoc
					.getElementsByTagName(NWCGConstants.SHIPMENT_ELEMENT);

			if (nlShipmentList != null) {
				for (int count = 0; count < nlShipmentList.getLength(); count++) {
					Element elemShipmentTag = (Element) nlShipmentList
							.item(count);
					String sShipmentKey = (String) (elemShipmentTag
							.getAttribute(NWCGConstants.SHIPMENT_KEY));

					if (sShipmentKey != null && (!sShipmentKey.equals(""))) {
						logger.debug("Calling the iSuiteService for shipment key : "
								+ sShipmentKey);

						Document ShipmentDoc = XMLUtil.newDocument();
						Element el_Shipment = ShipmentDoc
								.createElement(NWCGConstants.SHIPMENT_ELEMENT);

						ShipmentDoc.appendChild(el_Shipment);
						el_Shipment.setAttribute(NWCGConstants.SHIPMENT_KEY,
								sShipmentKey);

						// Invoke the iSuite Service...
						CommonUtilities.invokeService(env,
								NWCGConstants.NWCG_POST_ISUITE_RECORD_SERVICE,
								ShipmentDoc);

						// Update EXTN_ISUITE_PROCESSED attribute
						Document outDoc = updtIsuiteProcessedFlag(env,
								sShipmentKey);
						logger.verbose("The output xml for ChangeShipment is:-"
							+ XMLUtil.getXMLString(outDoc));

					}
				}
			}
		}

		endTimer("NWCGIsuiteServiceAgent::executeJob");
	}

	/**
	 * ChangeShipment API Call This method updates the IsuiteProcessed flag to
	 * 'Y'
	 * 
	 * @param YFSEnvironment
	 *            env, String sShipmentKey
	 * @return Document
	 */
	private Document updtIsuiteProcessedFlag(YFSEnvironment env,
			String sShipmentKey) throws Exception {

		// Create input changeShipment document
		Document rt_Shipment = XMLUtil.newDocument();
		Element el_Shipment = rt_Shipment
				.createElement(NWCGConstants.SHIPMENT_ELEMENT);
		Element el_Extn = rt_Shipment
				.createElement(NWCGConstants.CUST_EXTN_ELEMENT);
		rt_Shipment.appendChild(el_Shipment);

		el_Shipment.setAttribute(NWCGConstants.SHIPMENT_KEY, sShipmentKey);
		el_Extn.setAttribute(NWCGConstants.EXTN_ISUITE_PROCESSED,
				NWCGConstants.SET_ISUITE_PROCESSED_VALUE);
		el_Shipment.appendChild(el_Extn);

		// Create template
		Document docOutChangeShipmentTemplate = XMLUtil.newDocument();
		Element el_ChangeShipment = docOutChangeShipmentTemplate
				.createElement(NWCGConstants.SHIPMENT_ELEMENT);
		Element eleOutTempExtn = docOutChangeShipmentTemplate
				.createElement(NWCGConstants.CUST_EXTN_ELEMENT);
		docOutChangeShipmentTemplate.appendChild(el_ChangeShipment);

		el_ChangeShipment.setAttribute(NWCGConstants.SHIPMENT_KEY, "");
		eleOutTempExtn.setAttribute(NWCGConstants.EXTN_ISUITE_PROCESSED, "");
		el_ChangeShipment.appendChild(eleOutTempExtn);

		// Update the shipped record - Invoke changeShipment API
		if (logger.isVerboseEnabled())
			logger.verbose("updtIsuiteProcessedFlag, changeShipment Input XML:-"
					+ XMLUtil.getXMLString(rt_Shipment));

		return CommonUtilities.invokeAPI(env, docOutChangeShipmentTemplate,
				NWCGConstants.API_CHANGE_SHIPMENT, rt_Shipment);

	}

	/**
	 * Create shipment document and invoke getShipmentList API with a complex
	 * query
	 * 
	 * @param YFSEnvironment
	 *            env, String sShipmentKey
	 * @return Document
	 * @throws Exception
	 */
	private Document getShipmentList(YFSEnvironment env, String sShipmentKey)
			throws Exception {
		Document rt_Shipment = XMLUtil.getDocument();

		Element el_Shipment = rt_Shipment
				.createElement(NWCGConstants.DOCUMENT_NODE_SHIPMENT);
		Element el_ShipmenExtn = rt_Shipment.createElement(NWCGConstants.EXTN);
		el_Shipment.appendChild(el_ShipmenExtn);
		el_ShipmenExtn.setAttribute(NWCGConstants.EXTN_ISUITE_PROCESSED,
				NWCGConstants.GET_ISUITE_PROCESSED_VALUE);
		el_Shipment.setAttribute(NWCGConstants.MAXIMUM_RECORDS,
				NWCGConstants.GET_SHIPMENT_LIST_MAX_RECORDS_VALUE);// Limit
																	// return
																	// records
																	// to 50 -
																	// prevent
																	// performance
																	// issues
		el_Shipment.setAttribute(NWCGConstants.SHIPMENT_CLOSED_FLAG_ATTR,
				NWCGConstants.SHIPMENT_CLOSED_FLAG_VALUE); // not closed
															// shipments
		el_Shipment.setAttribute(NWCGConstants.DOCUMENT_TYPE,
				NWCGConstants.DOCUMENT_TYPE_ISSUE); // Incident orders
		el_Shipment.setAttribute(NWCGConstants.STATUS_ATTR,
				NWCGConstants.SHIPMENT_SHIPPED_STATUS); // Only pick up shipped
														// shipments

		// Building complex query...
		// Prevent getShipmentList API from picking up the same records
		if (lastShipmentKey != "" && lastShipmentKey != null) {
			Element elmComplexQry = rt_Shipment.createElement("ComplexQuery");
			el_Shipment.appendChild(elmComplexQry);
			elmComplexQry.setAttribute("Operator", "AND");

			Element elmComplexOrQry = rt_Shipment.createElement("And");
			elmComplexQry.appendChild(elmComplexOrQry);

			Element elmComplexOrItemExprQry = rt_Shipment.createElement("Exp");
			elmComplexOrQry.appendChild(elmComplexOrItemExprQry);
			elmComplexOrItemExprQry.setAttribute("Name",
					NWCGConstants.SHIPMENT_KEY);
			elmComplexOrItemExprQry.setAttribute("QryType", "GT");
			elmComplexOrItemExprQry.setAttribute("Value", sShipmentKey);
		}

		rt_Shipment.appendChild(el_Shipment);

		if (logger.isVerboseEnabled()) {
			logger.verbose("getShipmentList, " + "getShipmentList input XML : "
					+ XMLUtil.extractStringFromDocument(rt_Shipment));
		}

		// Set the output template for the getShipmentList API call
		Document docOutGetShipmentListTemplate = XMLUtil
				.createDocument(NWCGConstants.DOCUMENT_NODE_SHIPMENTS);
		Element eleOutTempShipment = docOutGetShipmentListTemplate
				.createElement(NWCGConstants.DOCUMENT_NODE_SHIPMENT);
		Element eleOutTempExtn = docOutGetShipmentListTemplate
				.createElement(NWCGConstants.CUST_EXTN_ELEMENT);
		eleOutTempShipment.appendChild(eleOutTempExtn);
		Element eleOutGetShipmentListTemplate = docOutGetShipmentListTemplate
				.getDocumentElement();
		eleOutGetShipmentListTemplate.appendChild(eleOutTempShipment);
		eleOutTempShipment.setAttribute(NWCGConstants.SHIPMENT_KEY, "");

		return CommonUtilities.invokeAPI(env, docOutGetShipmentListTemplate,
				NWCGConstants.API_GET_SHIPMENT_LIST, rt_Shipment);
	}

}