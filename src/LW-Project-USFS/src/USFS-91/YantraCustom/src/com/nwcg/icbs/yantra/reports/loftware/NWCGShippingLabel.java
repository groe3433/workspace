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

package com.nwcg.icbs.yantra.reports.loftware;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import com.yantra.yfc.log.YFCLogCategory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGShippingLabel implements YIFCustomApi {

	private Properties _properties;

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGShippingLabel.class);
	
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		_properties = arg0;
	}

	public Document triggerShippingLabel(YFSEnvironment env, Document inputDoc)
			throws Exception {
		logger.verbose("NWCGReportsIssue::, Entered");

		if (logger.isVerboseEnabled()) {
			logger.verbose("NWCGShippingLabel::triggerShippingLabel, Input Doc : "
					+ XMLUtil.getXMLString(inputDoc));
		}

		String documentId = "NWCG_SHIPPING_LABEL";
		Element rootElem1 = (Element) inputDoc.getElementsByTagName("Shipment")
				.item(0);
		String shipmentKey = rootElem1.getAttribute("ShipmentKey");
		String shipNode = rootElem1.getAttribute(NWCGConstants.SHIP_NODE);
		String enterpriseCode = rootElem1
				.getAttribute(NWCGConstants.ENTERPRISE_CODE_STR);

		Element rootElem2 = (Element) inputDoc.getElementsByTagName(
				"PrinterPreference").item(0);
		String printerId = rootElem2.getAttribute("PrinterId");
		Element rootElem3 = (Element) inputDoc.getElementsByTagName(
				"LabelPreference").item(0);
		String noOfCopies = rootElem3.getAttribute("NoOfCopies");
		int noCopies = 1;
		if (Integer.parseInt(noOfCopies) > 1)
			noCopies = Integer.parseInt(noOfCopies);

		Document shipmentDetails = getshipmentDetails(env, shipmentKey);

		Document sDtls = getOtherInfoAndPrepareXml(env, shipmentDetails);
		Element tempElm = sDtls.getDocumentElement();

		NWCGReportsUtil reportsUtil = new NWCGReportsUtil();

		Document shipstatusReportDoc = reportsUtil.generatePrintHeader(
				documentId, "xml:/Shipment", shipNode, printerId,
				enterpriseCode);
		Element shipstatusRptElmRootNode = shipstatusReportDoc
				.getDocumentElement();
		Element printDocElm = (Element) shipstatusRptElmRootNode
				.getFirstChild();
		Element inputDataElm = shipstatusReportDoc.createElement("InputData");
		Element shipstatusDtlsElm = shipstatusReportDoc
				.createElement("Shipment");
		XMLUtil.copyElement(shipstatusReportDoc, tempElm, shipstatusDtlsElm);
		inputDataElm.appendChild(shipstatusDtlsElm);
		printDocElm.appendChild(inputDataElm);
		if (logger.isVerboseEnabled()) {
			logger
					.verbose("NWCGShippingLabel::triggerShippingStatusReport, XML to printDocumentSet : "
							+ XMLUtil.getXMLString(shipstatusReportDoc));
		}

		for (int i = 0; i < noCopies; i++) {
			CommonUtilities.invokeAPI(env,
					NWCGConstants.API_PRINT_DOCUMENT_SET, shipstatusReportDoc);
		}
		logger
				.verbose("NWCGShippingLabel::triggerShippingStatusReport, Returning");

		return shipstatusReportDoc;
	}

	private Document getOtherInfoAndPrepareXml(YFSEnvironment env,
			Document shipmentDetails) {
		logger.verbose("NWCGShippingLabel::getOtherInfoAndPrepareXml, Entered");

		Element shipmentLineElm = (Element) shipmentDetails
				.getElementsByTagName("ShipmentLine").item(0);
		Element shiptmentrootElm = shipmentDetails.getDocumentElement();
		String orderHdrKey = shipmentLineElm
				.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		Document orderDtls = getOrderDetails(env, orderHdrKey);
		Element orderDtlsRootElm = orderDtls.getDocumentElement();
		// Document incidentDetails = getIncidentDetails(env,orderDtls);

		// Setting Ship To Address
		// Default is Issue "Deliver To". If "Deliver To" is NULL, then "Ship
		// To" will be set.

		// String deliverToAddr1 = "";
		// Element DeliverAddrElm = (Element)
		// orderDtls.getElementsByTagName("AdditionalAddress").item(0);
		Element ShipToAddrElm = (Element) orderDtls.getElementsByTagName(
				"PersonInfoShipTo").item(0);
		// ////////////////////////
		Element eleExtn = (Element) XMLUtil.getChildNodeByName(
				orderDtlsRootElm, "Extn");
		String strExtnNavInfo = eleExtn.getAttribute("ExtnNavInfo");
		// Element DeliverToAddrElm = null;

		/*
		 * if (DeliverAddrElm != null) { DeliverToAddrElm = (Element)
		 * DeliverAddrElm.getElementsByTagName("PersonInfo").item(0);
		 * 
		 * if (DeliverToAddrElm != null) deliverToAddr1 =
		 * DeliverToAddrElm.getAttribute("AddressLine1"); }
		 */
		Element toAddrElem = orderDtls.createElement("ShipToAddress");
		// shiptmentrootElm.appendChild(toAddrElem);
		orderDtlsRootElm.appendChild(toAddrElem);

		if (strExtnNavInfo
				.equalsIgnoreCase(NWCGConstants.SHIPPING_INSTRUCTIONS)) {
			String addrLine1 = eleExtn
					.getAttribute(NWCGConstants.EXTN_SHIPPING_INSTRUCTIONS_ATTR);
			String city = eleExtn
					.getAttribute(NWCGConstants.EXTN_SHIPPING_INSTR_CITY_ATTR);
			String state = eleExtn
					.getAttribute(NWCGConstants.EXTN_SHIPPING_INSTR_STATE_ATTR);
			toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_1, addrLine1);
			toAddrElem.setAttribute(NWCGConstants.CITY, city);
			toAddrElem.setAttribute(NWCGConstants.STATE, state);
		} else if (strExtnNavInfo.equalsIgnoreCase(NWCGConstants.WILL_PICK_UP)) {
			String addrLine1 = eleExtn
					.getAttribute(NWCGConstants.EXTN_WILL_PICK_UP_NAME);
			String addrLine2 = eleExtn
					.getAttribute(NWCGConstants.EXTN_WILL_PICK_UP_INFO);
			toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_1, addrLine1);
			toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_2, addrLine2);

		} else {
			XMLUtil.copyElement(orderDtls, ShipToAddrElm, toAddrElem);
		}
		// End Setting Ship To Address

		// Setting Order Header Notes
		NodeList listOfNotes = orderDtls.getElementsByTagName("Note");
		String NoteText = "";
		for (int n = 0; n < listOfNotes.getLength(); n++) {
			Element curNote = (Element) listOfNotes.item(n);
			NoteText = NoteText + " " + curNote.getAttribute("NoteText");
		}

		Element OrdDtlsElm = (Element) orderDtls.getElementsByTagName("Order")
				.item(0);
		OrdDtlsElm.setAttribute("NoteText", NoteText);
		// End Setting Order Header Notes

		Element ordElem = shipmentDetails.createElement("Order");
		shiptmentrootElm.appendChild(ordElem);

		XMLUtil.copyElement(shipmentDetails, OrdDtlsElm, ordElem);

		if (logger.isVerboseEnabled()) {
			logger
					.verbose("NWCGShippingLabel::getOtherInfoAndPrepareXml, After adjust lines : "
							+ XMLUtil.getXMLString(shipmentDetails));
		}

		return shipmentDetails;
	}

	private Document getshipmentDetails(YFSEnvironment env, String shipmentKey) {
		String orderHdrKey = "";
		Document shipmentDtls = null;
		try {
			Document ShipmentHdrInputDoc = XMLUtil.getDocument();
			Element ShipmentHdrElm = ShipmentHdrInputDoc
					.createElement("Shipment");
			ShipmentHdrElm.setAttribute("ShipmentKey", shipmentKey);
			ShipmentHdrInputDoc.appendChild(ShipmentHdrElm);

			shipmentDtls = CommonUtilities.invokeAPI(env, "getShipmentDetails",
					ShipmentHdrInputDoc);

			if (logger.isVerboseEnabled()) {
				logger
						.verbose("NWCGShippingLabel::getOrderHeaderKey, Output from getShipmentDetails : "
								+ XMLUtil.getXMLString(shipmentDtls));
			}
			logger
					.verbose("NWCGShippingLabel::getOrderHeaderKey, OrderHeaderKey : "
							+ orderHdrKey);
		} catch (Exception e) {
			logger.error("NWCGShippingLabel::getOrderHeaderKey, Exception Msg : "
					+ e.getMessage());
			logger.error("NWCGShippingLabel::getOrderHeaderKey, StackTrace : "
					+ e.getStackTrace());
		}
		return shipmentDtls;
	}

	/**
	 * NWCGReports_getOrderDetails xml removed all additional elements that are
	 * not required
	 * 
	 * @param env
	 * @param orderId
	 * @param enterpriseCode
	 * @param docType
	 * @return
	 */
	private Document getOrderDetails(YFSEnvironment env, String orderHdrKey) {
		Document result = null;
		logger.verbose("NWCGShippingLabel::getOrderDetails, Entered");
		try {
			result = XMLUtil.getDocument();
			Document inputDoc = XMLUtil.getDocument();
			Element orderElm = inputDoc.createElement("Order");
			inputDoc.appendChild(orderElm);
			orderElm.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHdrKey);

			result = CommonUtilities.invokeAPI(env,
					"NWCGReports_getOrderDetails",
					NWCGConstants.API_GET_ORDER_DETAILS, inputDoc);

			if (logger.isVerboseEnabled()) {
				logger
						.verbose("NWCGShippingLabel::getOrderDetails, getOrderDetails output : "
								+ XMLUtil.getXMLString(result));
			}
		} catch (ParserConfigurationException pce) {
			logger
					.error("NWCGShippingLabel::getOrderDetails, ParserConfigurationException Msg : "
							+ pce.getMessage());
			logger.error("NWCGShippingLabel::getOrderDetails, StackTrace : "
					+ pce.getStackTrace());
		} catch (Exception e) {
			logger.error("NWCGShippingLabel::getOrderDetails, Exception Msg : "
					+ e.getMessage());
			logger.error("NWCGShippingLabel::getOrderDetails, StackTrace : "
					+ e.getStackTrace());
		}
		logger.verbose("NWCGShippingLabel::getOrderDetails, Returning");
		return result;
	}

	private Document getIncidentDetails(YFSEnvironment env, Document orderDtls) {
		Document incidentDtls = null;
		String incidentNo = null;
		Element OrderDtlsElm = (Element) orderDtls.getElementsByTagName("Extn")
				.item(0);
		incidentNo = OrderDtlsElm.getAttribute("ExtnIncidentNo");
		try {
			incidentDtls = XMLUtil.getDocument();
			Document getIncidentOrderInput = XMLUtil
					.createDocument("NWCGIncidentOrder");
			getIncidentOrderInput.getDocumentElement().setAttribute(
					"IncidentNo", incidentNo);
			incidentDtls = CommonUtilities.invokeService(env,
					NWCGConstants.NWCG_GET_INCIDENT_ORDER_SERVICE,
					getIncidentOrderInput);

		} catch (ParserConfigurationException pce) {
			logger
					.error("NWCGReportsIssue::getIncidentDetails, ParserConfigurationException Msg : "
							+ pce.getMessage());
			logger.error("NWCGReportsIssue::getIncidentDetails, StackTrace : "
					+ pce.getStackTrace());
		} catch (Exception e) {
			logger.error("NWCGReportsIssue::getIncidentDetails, Exception Msg : "
					+ e.getMessage());
			logger.error("NWCGReportsIssue::getIncidentDetails, StackTrace : "
					+ e.getStackTrace());
		}
		return incidentDtls;
	}
}