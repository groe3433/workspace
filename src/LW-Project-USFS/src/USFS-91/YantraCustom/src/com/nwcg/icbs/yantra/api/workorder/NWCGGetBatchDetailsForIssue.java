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

package com.nwcg.icbs.yantra.api.workorder;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.yantra.yfc.log.YFCLogCategory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.reports.loftware.NWCGReportsUtil;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGGetBatchDetailsForIssue implements YIFCustomApi {
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGGetBatchDetailsForIssue.class);

	public Document getCustomBatchDetailsForIssue(YFSEnvironment env,
			Document inDoc) throws Exception {
		Document batchDetailsDoc = CommonUtilities.invokeAPI(env,
				"getBatchDetails", inDoc);
		Document modifiedBatchDetailsDoc = modifyBatchDetails(env,
				batchDetailsDoc);
		return modifiedBatchDetailsDoc;
	}

	private Document modifyBatchDetails(YFSEnvironment env,
			Document batchDetailsDoc) throws Exception {
		Document batchDetailsDocWithNotes = null;

		Element batchElement = (Element) batchDetailsDoc.getElementsByTagName(
				"Batch").item(0);
		String moveRequestNo = batchElement.getAttribute("MoveRequestNo");
		String node = batchElement.getAttribute("Node");

		Document inDocForAPI = XMLUtil.newDocument();
		Element element = inDocForAPI.createElement("MoveRequest");
		element.setAttribute("MoveRequestNo", moveRequestNo);
		element.setAttribute("Node", node);
		inDocForAPI.appendChild(element);

		Document invOutDoc = CommonUtilities.invokeAPI(env,
				NWCGConstants.API_GET_MOVE_REQUEST_DETAILS, inDocForAPI);
		String shipmentKey = ((Element) invOutDoc.getElementsByTagName(
				"MoveRequest").item(0)).getAttribute("ShipmentKey");

		Document outDoc = null;
		String incidentNo = "";
		String issueNo = "";
		String incidentName = "";
		String incidentYear = "";
		String extnCustomerName = "";
		String strExtnNavInfo = "";
		String shipToAddressLine1 = "";
		String shipToAddressLine2 = "";
		String shipToAddressCity = "";
		String shipToAddressState = "";
		String shipToAddressZip = "";
		if (shipmentKey != null && !shipmentKey.trim().equals("")) {
			outDoc = CommonUtilities.invokeAPI(
					env,
					NWCGConstants.API_GET_SHIPMENT_DETAILS,
					XMLUtil.getDocument("<Shipment ShipmentKey=\""
							+ shipmentKey + "\" />"));
			if (outDoc != null) {

				Element shipmentElement = (Element) outDoc
						.getElementsByTagName("Shipment").item(0);
				String enterpriseCode = shipmentElement
						.getAttribute("EnterpriseCode");

				Element extnElement = (Element) outDoc.getElementsByTagName(
						"Extn").item(0);
				if (extnElement != null) {
					incidentNo = extnElement.getAttribute("ExtnIncidentNum");
				}

				Element shipLineElement = (Element) outDoc
						.getElementsByTagName("ShipmentLine").item(0);
				if (shipLineElement != null) {
					issueNo = shipLineElement.getAttribute("OrderNo");
				}

				Document oDetailDoc = CommonUtilities.invokeAPI(
						env,
						"NWCGBatchSheet_getOrderDetails",
						NWCGConstants.API_GET_ORDER_DETAILS,
						XMLUtil.getDocument("<Order OrderNo=\"" + issueNo
								+ "\" EnterpriseCode=\"" + enterpriseCode
								+ "\" />"));
				if (oDetailDoc != null) {
					Map<String, String> notesMap = extractNotes(oDetailDoc);
					batchDetailsDocWithNotes = injectNotes(notesMap,
							batchDetailsDoc);

					Element orderDetailsElement = (Element) oDetailDoc
							.getElementsByTagName("Order").item(0);
					String documentType = orderDetailsElement
							.getAttribute(NWCGConstants.DOCUMENT_TYPE);

					String deliverToAddressLine1 = "";
					Element additionalAddressElement = (Element) oDetailDoc
							.getElementsByTagName("AdditionalAddress").item(0);
					Element shipToAddressElement = (Element) oDetailDoc
							.getElementsByTagName("PersonInfoShipTo").item(0);
					Element deliverToAddressElement = null;

					if (additionalAddressElement != null) {
						deliverToAddressElement = (Element) additionalAddressElement
								.getElementsByTagName("PersonInfo").item(0);

						if (deliverToAddressElement != null)
							deliverToAddressLine1 = deliverToAddressElement
									.getAttribute("AddressLine1");
					}

					Element odExtnElement = (Element) oDetailDoc
							.getElementsByTagName("Extn").item(0);
					if (odExtnElement != null) {
						incidentName = odExtnElement
								.getAttribute("ExtnIncidentName");
						incidentYear = odExtnElement
								.getAttribute("ExtnIncidentYear");
						strExtnNavInfo = odExtnElement
								.getAttribute("ExtnNavInfo");
						extnCustomerName = odExtnElement
								.getAttribute("ExtnCustomerName");
					}

					if (documentType.equals("0001"))// Incident Issue
					{
						if (odExtnElement != null) {
							if (strExtnNavInfo
									.equalsIgnoreCase(NWCGConstants.SHIPPING_INSTRUCTIONS)) {
								shipToAddressLine2 = odExtnElement
										.getAttribute(NWCGConstants.EXTN_SHIPPING_INSTRUCTIONS_ATTR);
								shipToAddressCity = odExtnElement
										.getAttribute(NWCGConstants.EXTN_SHIPPING_INSTR_CITY_ATTR);
								shipToAddressState = odExtnElement
										.getAttribute(NWCGConstants.EXTN_SHIPPING_INSTR_STATE_ATTR);
							} else if (strExtnNavInfo
									.equalsIgnoreCase(NWCGConstants.WILL_PICK_UP)) {
								shipToAddressLine1 = odExtnElement
										.getAttribute(NWCGConstants.EXTN_WILL_PICK_UP_NAME);
								shipToAddressLine2 = odExtnElement
										.getAttribute(NWCGConstants.EXTN_WILL_PICK_UP_INFO);
							} else {
								if (shipToAddressElement != null) {
									shipToAddressLine1 = shipToAddressElement
											.getAttribute("AddressLine1");
									shipToAddressLine2 = shipToAddressElement
											.getAttribute("AddressLine2");
									shipToAddressCity = shipToAddressElement
											.getAttribute("City");
									shipToAddressState = shipToAddressElement
											.getAttribute("State");
									shipToAddressZip = shipToAddressElement
											.getAttribute("ZipCode");
								}
							}
						}
					} else {
						if (deliverToAddressLine1.length() > 1) {
							shipToAddressLine1 = deliverToAddressElement
									.getAttribute("AddressLine1");
							shipToAddressLine2 = deliverToAddressElement
									.getAttribute("AddressLine2");
							shipToAddressCity = deliverToAddressElement
									.getAttribute("City");
							shipToAddressState = deliverToAddressElement
									.getAttribute("State");
							shipToAddressZip = deliverToAddressElement
									.getAttribute("ZipCode");

							// if DeliverTo is set to ShipTo field, blank out
							// ExtnCustomerName
							extnCustomerName = "";
						} else {
							if (shipToAddressElement != null) {
								shipToAddressLine1 = shipToAddressElement
										.getAttribute("AddressLine1");
								shipToAddressLine2 = shipToAddressElement
										.getAttribute("AddressLine2");
								shipToAddressCity = shipToAddressElement
										.getAttribute("City");
								shipToAddressState = shipToAddressElement
										.getAttribute("State");
								shipToAddressZip = shipToAddressElement
										.getAttribute("ZipCode");
							}
						}
					}
				}
			}
		}

		Document modifiedBatchDetailsDoc = XMLUtil.getDocument();
		Element batchElementWithNotes = (Element) batchDetailsDocWithNotes
				.getElementsByTagName("Batch").item(0);
		Element newBatchElement = modifiedBatchDetailsDoc
				.createElement("Batch");
		XMLUtil.copyElement(modifiedBatchDetailsDoc, batchElementWithNotes,
				newBatchElement);
		modifiedBatchDetailsDoc.appendChild(newBatchElement);

		newBatchElement.setAttribute("IssueNo", issueNo);
		newBatchElement.setAttribute("IncidentNo", incidentNo);
		newBatchElement.setAttribute("IncidentName", incidentName);
		newBatchElement.setAttribute("IncidentYear", incidentYear);
		newBatchElement.setAttribute("CustomerName", extnCustomerName);
		newBatchElement.setAttribute("ShipToAddressLine1", shipToAddressLine1);
		newBatchElement.setAttribute("ShipToAddressLine2", shipToAddressLine2);
		newBatchElement.setAttribute("ShipToAddressCity", shipToAddressCity);
		newBatchElement.setAttribute("ShipToAddressState", shipToAddressState);
		newBatchElement.setAttribute("ShipToAddressZip", shipToAddressZip);

		return modifiedBatchDetailsDoc;
	}

	private Map<String, String> extractNotes(Document orderDetailsDoc)
			throws Exception {
		Map<String, String> notesMap = new HashMap<String, String>();

		NodeList orderLineList = orderDetailsDoc
				.getElementsByTagName("OrderLine");
		for (int i = 0; i < orderLineList.getLength(); i++) {
			Element orderLineElement = (Element) orderLineList.item(i);
			Element noteElement = (Element) orderLineElement
					.getElementsByTagName("Note").item(0);
			if (noteElement != null) {
				String noteText = noteElement.getAttribute("NoteText");
				if (!noteText.equals("")) {
					notesMap.put(orderLineElement.getAttribute("OrderLineKey"),
							noteText);
				}
			}
		}
		return notesMap;
	}

	private Document injectNotes(Map<String, String> notesMap,
			Document batchDetailsDoc) throws Exception {
		NodeList taskList = batchDetailsDoc.getElementsByTagName("Task");
		for (int i = 0; i < taskList.getLength(); i++) {
			Element taskElement = (Element) taskList.item(i);
			Element taskReferencesElement = (Element) taskElement
					.getElementsByTagName("TaskReferences").item(0);
			String orderLineKey = taskReferencesElement
					.getAttribute("OrderLineKey");

			String noteText = notesMap.get(orderLineKey);
			if (noteText != null) {
				Element newTaskReferencesElement = batchDetailsDoc
						.createElement("TaskReferences");
				XMLUtil.copyElement(batchDetailsDoc, taskReferencesElement,
						newTaskReferencesElement);
				taskElement.removeChild(taskReferencesElement);
				taskElement.appendChild(newTaskReferencesElement);
				newTaskReferencesElement.setAttribute("NoteText", noteText);
			}
		}
		return batchDetailsDoc;
	}

	public void setProperties(Properties arg0) throws Exception {
	}
}