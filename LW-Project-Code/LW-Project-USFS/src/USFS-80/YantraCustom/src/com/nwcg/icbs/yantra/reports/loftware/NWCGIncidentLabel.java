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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGIncidentLabel implements YIFCustomApi {

	private Properties _properties;

	private static Logger log = Logger.getLogger(NWCGIncidentLabel.class
			.getName());

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		_properties = arg0;
	}

	public Document triggerIncidentLabel(YFSEnvironment env, Document inputDoc)
			throws Exception {
		System.out
				.println("NWCGIncidentLabel::triggerIncidentLabel, Input Doc : "
						+ XMLUtil.getXMLString(inputDoc));
		if (log.isVerboseEnabled()) {
			log.verbose("NWCGIncidentLabel::triggerIncidentLabel, Input Doc : "
					+ XMLUtil.getXMLString(inputDoc));
		}

		String documentId = "NWCG_INCIDENT_LABEL";
		Element rootElem1 = (Element) inputDoc.getElementsByTagName("Incident")
				.item(0);
		String incidentNo = rootElem1.getAttribute("IncidentNo");
		String incidentYear = rootElem1.getAttribute("Year");
		String GenerateReturn = rootElem1.getAttribute("GenerateReturnNo");
		Document incidentDtls = getIncidentDetails(env, incidentNo,
				incidentYear);
		// System.out.println("Incident Details Doc : " +
		// XMLUtil.getXMLString(incidentDtls));

		// Setting Ship To Address
		// It's Always Incident "YFSPersonInfoShip To" Based on 04/19/07 Review

		// Element DeliverToAddrElm = (Element)
		// incidentDtls.getElementsByTagName("YFSPersonInfoDeliverTo").item(0);
		// Element ShipToAddrElm = (Element)
		// incidentDtls.getElementsByTagName("YFSPersonInfoShipTo").item(0);

		// Element toAddrElem = incidentDtls.createElement("ShipToAddress");
		// Element incidentrootElm = (Element)
		// incidentDtls.getDocumentElement();
		// incidentrootElm.appendChild(toAddrElem);

		// String deliverToAddr1 =
		// DeliverToAddrElm.getAttribute("AddressLine1");
		// if (deliverToAddr1.length() > 1)
		// {
		// XMLUtil.copyElement(incidentDtls,DeliverToAddrElm,toAddrElem);
		// }
		// else
		// {
		// XMLUtil.copyElement(incidentDtls,ShipToAddrElm,toAddrElem);
		// }
		// End Setting Ship To Address

		Element rootElem2 = (Element) inputDoc.getElementsByTagName(
				"PrinterPreference").item(0);
		String printerId = rootElem2.getAttribute("PrinterId");
		Element rootElem3 = (Element) inputDoc.getElementsByTagName(
				"LabelPreference").item(0);
		String shipNode = rootElem3.getAttribute("Node");
		String enterpriseCode = rootElem3
				.getAttribute(NWCGConstants.ENTERPRISE_CODE_STR);
		String noOfCopies = rootElem3.getAttribute("NoOfCopies");
		System.out.println("ShipNode " + shipNode);
		int noCopies = 1;
		if (Integer.parseInt(noOfCopies) > 1)
			noCopies = Integer.parseInt(noOfCopies);

		Element tempElm = incidentDtls.getDocumentElement();

		NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
		System.out.println("Incident Label 4");
		Document incidentLabelDoc = reportsUtil.generatePrintHeader(documentId,
				"xml:/Incident", shipNode, printerId, enterpriseCode);
		Element incidentLabelElmRootNode = incidentLabelDoc
				.getDocumentElement();
		Element printDocElm = (Element) incidentLabelElmRootNode
				.getFirstChild();
		Element inputDataElm = incidentLabelDoc.createElement("InputData");
		Element incidentDtlsElm = incidentLabelDoc.createElement("Incident");
		incidentDtlsElm.setAttribute("UserCacheID", shipNode);
		XMLUtil.copyElement(incidentLabelDoc, tempElm, incidentDtlsElm);
		inputDataElm.appendChild(incidentDtlsElm);
		printDocElm.appendChild(inputDataElm);
		if (log.isVerboseEnabled()) {
			log
					.verbose("NWCGIncidentLabel::triggerShippingStatusReport, XML to printDocumentSet : "
							+ XMLUtil.getXMLString(incidentLabelDoc));
		}

		if (GenerateReturn.equals("Y")) {
			String ReturnNo = GenerateSeqNo(env, shipNode);
			System.out.println("ReturnNo " + ReturnNo);
			incidentDtlsElm.setAttribute("ReturnNo", ReturnNo);
		} else {
			incidentDtlsElm.setAttribute("ReturnNo", "");
		}
		for (int i = 0; i < noCopies; i++) {
			CommonUtilities.invokeAPI(env,
					NWCGConstants.API_PRINT_DOCUMENT_SET, incidentLabelDoc);
		}
		log
				.verbose("NWCGIncidentLabel::triggerShippingStatusReport, Returning");
		System.out.println("Print Doc "
				+ XMLUtil.getXMLString(incidentLabelDoc));
		return incidentLabelDoc;
	}

	private Document getIncidentDetails(YFSEnvironment env, String incidentNo,
			String incidentYear) {
		Document incidentDtls = null;
		try {
			incidentDtls = XMLUtil.getDocument();
			Document getIncidentOrderInput = XMLUtil
					.createDocument("NWCGIncidentOrder");
			getIncidentOrderInput.getDocumentElement().setAttribute(
					"IncidentNo", incidentNo);
			getIncidentOrderInput.getDocumentElement().setAttribute("Year",
					incidentYear);
			incidentDtls = CommonUtilities.invokeService(env,
					NWCGConstants.NWCG_GET_INCIDENT_ORDER_SERVICE,
					getIncidentOrderInput);
			System.out.println("Incident Label Doc "
					+ XMLUtil.getXMLString(incidentDtls));
		} catch (ParserConfigurationException pce) {
			log
					.error("NWCGReportsIssue::getIncidentDetails, ParserConfigurationException Msg : "
							+ pce.getMessage());
			log.error("NWCGReportsIssue::getIncidentDetails, StackTrace : "
					+ pce.getStackTrace());
		} catch (Exception e) {
			log.error("NWCGReportsIssue::getIncidentDetails, Exception Msg : "
					+ e.getMessage());
			log.error("NWCGReportsIssue::getIncidentDetails, StackTrace : "
					+ e.getStackTrace());
		}
		return incidentDtls;
	}

	public String GenerateSeqNo(YFSEnvironment env, String CacheID) {
		String SeqNo = "";

		Document SeqDtls = null;
		Document getSeqInput = null;
		try {
			getSeqInput = XMLUtil.createDocument("NextReceiptNo");
			getSeqInput.getDocumentElement().setAttribute("ReceiptNo", "");
			getSeqInput.getDocumentElement().setAttribute("SequenceType",
					"RETURN");
			getSeqInput.getDocumentElement().setAttribute("CacheID", CacheID);
			SeqDtls = CommonUtilities.invokeService(env,
					"NWCGGenerateReceiptNoService", getSeqInput);
			System.out.println("Seq Detail " + XMLUtil.getXMLString(SeqDtls));
		} catch (ParserConfigurationException pce) {
			log
					.error("NWCGReportsIssue::getIncidentDetails, GenerateSeqNo Msg : "
							+ pce.getMessage());
			log.error("NWCGReportsIssue::getIncidentDetails, GenerateSeqNo : "
					+ pce.getStackTrace());
		} catch (Exception e) {
			log.error("NWCGReportsIssue::getIncidentDetails, Exception Msg : "
					+ e.getMessage());
			log.error("NWCGReportsIssue::getIncidentDetails, StackTrace : "
					+ e.getStackTrace());
		}

		if (SeqDtls != null) {
			Element rootElem = SeqDtls.getDocumentElement();
			SeqNo = rootElem.getAttribute("ReceiptNo");
			System.out.println("Seq No " + SeqNo);
		}
		return SeqNo;
	}
}