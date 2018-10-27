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
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGReportsShippingStatus implements YIFCustomApi {

	private Properties _properties;

	private static Logger log = Logger
			.getLogger(NWCGReportsShippingStatus.class.getName());

	NWCGReportsUtil reportsUtil = new NWCGReportsUtil();

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		_properties = arg0;
	}

	public Document triggerShippingStatusReport(YFSEnvironment env,
			Document inputDoc) throws Exception {
		log.verbose("NWCGReportsIssue::, Entered");
		// System.out.println("NWCGReportsIssue::triggerShippingStatusReport,
		// Input Doc : " + XMLUtil.getXMLString(inputDoc));
		if (log.isVerboseEnabled()) {
			log
					.verbose("NWCGReportsShippingStatus::triggerShippingStatusReport, Input Doc : "
							+ XMLUtil.getXMLString(inputDoc));
		}
		// System.out.println("triggerShippingStatusReport, Input Doc : " +
		// XMLUtil.getXMLString(inputDoc));
		String documentId = "NWCG_SHIPPING_STATUS_REPORT";
		Element rootElem1 = (Element) inputDoc.getElementsByTagName("Shipment")
				.item(0);
		String shipmentKey = rootElem1.getAttribute("ShipmentKey");
		String shipNode = rootElem1.getAttribute(NWCGConstants.SHIP_NODE);
		String enterpriseCode = rootElem1
				.getAttribute(NWCGConstants.ENTERPRISE_CODE_STR);

		Element rootElem2 = (Element) inputDoc.getElementsByTagName(
				"PrinterPreference").item(0);
		String printerId = rootElem2.getAttribute("PrinterId");
		String userlocale = rootElem2.getAttribute("UserLocale");
		Element rootElem3 = (Element) inputDoc.getElementsByTagName(
				"LabelPreference").item(0);
		String noOfCopies = rootElem3.getAttribute("NoOfCopies");
		int noCopies = 1;
		if (Integer.parseInt(noOfCopies) > 1)
			noCopies = Integer.parseInt(noOfCopies);

		Document shipmentDetails = getshipmentDetails(env, shipmentKey,
				userlocale);

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
		if (log.isVerboseEnabled()) {
			log
					.verbose("NWCGReportsShippingStatus::triggerShippingStatusReport, XML to printDocumentSet : "
							+ XMLUtil.getXMLString(shipstatusReportDoc));
		}
		// System.out.println("Print Doc : " +
		// XMLUtil.getXMLString(issueReportDoc));
		for (int i = 0; i < noCopies; i++) {
			CommonUtilities.invokeAPI(env,
					NWCGConstants.API_PRINT_DOCUMENT_SET, shipstatusReportDoc);
		}
		log
				.verbose("NWCGReportsShippingStatus::triggerShippingStatusReport, Returning");
		// System.out.println("Print Doc " +
		// XMLUtil.getXMLString(shipstatusReportDoc));
		return shipstatusReportDoc;
	}

	private Document getOtherInfoAndPrepareXml(YFSEnvironment env,
			Document shipmentDetails) throws Exception {
		log
				.verbose("NWCGReportsShippingStatus::getOtherInfoAndPrepareXml, Entered");

		Element shipmentLineElm = (Element) shipmentDetails
				.getElementsByTagName("ShipmentLine").item(0);
		Element shiptmentrootElm = shipmentDetails.getDocumentElement();
		String orderHdrKey = shipmentLineElm
				.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		Document orderDtls = getOrderDetails(env, orderHdrKey);
		Element OrdDtlsElm = (Element) orderDtls.getElementsByTagName("Order")
				.item(0);
		Element orderDtlsRootElm = orderDtls.getDocumentElement();
		String documentType = OrdDtlsElm
				.getAttribute(NWCGConstants.DOCUMENT_TYPE);

		// Document incidentDetails = getIncidentDetails(env,orderDtls);
		// Element IncidentElm = (Element)
		// incidentDetails.getElementsByTagName("NWCGIncidentOrder").item(0);
		// String Year = IncidentElm.getAttribute("Year");
		// OrdDtlsElm.setAttribute("Year",Year);
		NWCGReportsUtil reportUtil = new NWCGReportsUtil();
		String strDate = reportUtil.dateToString(new java.util.Date(),
				"MM-dd-yyyy");
		shiptmentrootElm.setAttribute("CurrentDate", strDate);

		// Setting Order Header Notes
		NodeList listOfNotes = orderDtls.getElementsByTagName("Note");
		String NoteText = "";
		for (int n = 0; n < listOfNotes.getLength(); n++) {
			Element curNote = (Element) listOfNotes.item(n);
			NoteText = NoteText + " " + curNote.getAttribute("NoteText");
		}

		OrdDtlsElm.setAttribute("NoteText", NoteText);
		// End Setting Order Header Notes

		// Setting Shipping Instructions
		NodeList listOfInstructions = shipmentDetails
				.getElementsByTagName("Instruction");
		String InstructionText = "";
		for (int n = 0; n < listOfInstructions.getLength(); n++) {
			Element curInstruction = (Element) listOfInstructions.item(n);
			InstructionText = InstructionText + " "
					+ curInstruction.getAttribute("InstructionText");
		}

		OrdDtlsElm.setAttribute("InstructionText", InstructionText);
		// End Setting Shipping Instructions

		// Setting Ship To Address
		// Default is Issue "Deliver To". If "Deliver To" is NULL, then "Ship
		// To" will be set.
		// System.out.println("OrderDTLS "+ XMLUtil.getXMLString(orderDtls));
		String deliverToAddr1 = "";
		Element DeliverAddrElm = (Element) orderDtls.getElementsByTagName(
				"AdditionalAddress").item(0);
		Element ShipToAddrElm = (Element) orderDtls.getElementsByTagName(
				"PersonInfoShipTo").item(0);
		Element DeliverToAddrElm = null;
		Element eleExtn = (Element) XMLUtil.getChildNodeByName(
				orderDtlsRootElm, "Extn");
		String strExtnNavInfo = eleExtn.getAttribute("ExtnNavInfo");

		if (DeliverAddrElm != null) {
			DeliverToAddrElm = (Element) DeliverAddrElm.getElementsByTagName(
					"PersonInfo").item(0);

			if (DeliverToAddrElm != null)
				deliverToAddr1 = DeliverToAddrElm.getAttribute("AddressLine1");
		}
		Element toAddrElem = orderDtls.createElement("ShipToAddress");
		orderDtlsRootElm.appendChild(toAddrElem);

		String AddrArray[] = new String[25];
		if (documentType.equals("0001")) // Incident Issue
		{
			if (strExtnNavInfo
					.equalsIgnoreCase(NWCGConstants.SHIPPING_INSTRUCTIONS)) {
				String ShippingInsStr = eleExtn
						.getAttribute(NWCGConstants.EXTN_SHIPPING_INSTRUCTIONS_ATTR);
				AddrArray = CommonUtilities.getAddressLines(ShippingInsStr);
				int AddrArrayLen = AddrArray.length;
				String city = eleExtn
						.getAttribute(NWCGConstants.EXTN_SHIPPING_INSTR_CITY_ATTR);
				String state = eleExtn
						.getAttribute(NWCGConstants.EXTN_SHIPPING_INSTR_STATE_ATTR);
				if (AddrArrayLen > 0)
					toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_1,
							AddrArray[0]);
				if (AddrArrayLen > 1)
					toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_2,
							AddrArray[1]);
				if (AddrArrayLen > 2)
					toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_3,
							AddrArray[2]);
				if (AddrArrayLen > 3)
					toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_4,
							AddrArray[3]);
				if (AddrArrayLen > 4)
					toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_5,
							AddrArray[4]);
				if (AddrArrayLen > 5)
					toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_6,
							AddrArray[5]);
				toAddrElem.setAttribute(NWCGConstants.CITY, city);
				toAddrElem.setAttribute(NWCGConstants.STATE, state);
			} else if (strExtnNavInfo
					.equalsIgnoreCase(NWCGConstants.WILL_PICK_UP)) {
				String addrLine1 = eleExtn
						.getAttribute(NWCGConstants.EXTN_WILL_PICK_UP_NAME);
				String addrLine2 = eleExtn
						.getAttribute(NWCGConstants.EXTN_WILL_PICK_UP_INFO);
				toAddrElem
						.setAttribute(NWCGConstants.ADDRESS_LINE_1, addrLine1);
				toAddrElem
						.setAttribute(NWCGConstants.ADDRESS_LINE_2, addrLine2);

			} else {
				XMLUtil.copyElement(orderDtls, ShipToAddrElm, toAddrElem);
			}
		} else { // Other Doc Types - Other Issue
			if (deliverToAddr1.length() > 1) {
				XMLUtil.copyElement(orderDtls, DeliverToAddrElm, toAddrElem);
			} else {
				XMLUtil.copyElement(orderDtls, ShipToAddrElm, toAddrElem);
			}
		}

		// End Setting Ship To Address

		// Setting Request Numbers
		NodeList listOfOrderLines = orderDtls.getElementsByTagName("OrderLine");
		int totOrderLines = listOfOrderLines.getLength();
		String ReqNoText = "";
		Element FirstOrderLine = (Element) listOfOrderLines.item(0);
		Element FirstOrderExtn = (Element) FirstOrderLine.getElementsByTagName(
				"Extn").item(0);
		String FirstExtnRequestNo = FirstOrderExtn
				.getAttribute("ExtnRequestNo");
		int cnt = 1;
		int PRNo, CRNo, NRNo = 0;
		String LastExtnRequestNo = "";
		ReqNoText = ReqNoText + FirstExtnRequestNo;
		// System.out.println("ReqNoText: "+ ReqNoText);
		if (totOrderLines > 1) {
			Element LastOrderLine = (Element) listOfOrderLines
					.item(totOrderLines - 1);
			Element LastOrderExtn = (Element) LastOrderLine
					.getElementsByTagName("Extn").item(0);
			LastExtnRequestNo = LastOrderExtn.getAttribute("ExtnRequestNo");
			ReqNoText = ReqNoText + "," + LastExtnRequestNo;
		}

		if (totOrderLines > 2) {
			ReqNoText = "";
			ReqNoText = ReqNoText + FirstExtnRequestNo;
			for (int i = 1; i < (totOrderLines - 1); i++) {
				Element PrevOrderLine = (Element) listOfOrderLines.item(i - 1);
				Element PrevOrderExtn = (Element) PrevOrderLine
						.getElementsByTagName("Extn").item(0);
				String PrevExtnRequestNo = PrevOrderExtn
						.getAttribute("ExtnRequestNo");
				if (PrevExtnRequestNo.length() > 2) {
					String[] Ptfields = PrevExtnRequestNo.split("-");
					// System.out.println("Ptfields "+Ptfields[1]);
					String[] Ptfields1 = Ptfields[1].split("\\.");
					PRNo = Integer.parseInt(Ptfields1[0]);
					// System.out.println("PRNo "+PRNo);
				} else {
					PRNo = i;
				}
				Element CurOrderLine = (Element) listOfOrderLines.item(i);
				Element CurOrderExtn = (Element) CurOrderLine
						.getElementsByTagName("Extn").item(0);
				String CurExtnRequestNo = CurOrderExtn
						.getAttribute("ExtnRequestNo");
				if (CurExtnRequestNo.length() > 2) {
					String[] Ctfields = CurExtnRequestNo.split("-");
					String[] Ctfields1 = Ctfields[1].split("\\.");
					CRNo = Integer.parseInt(Ctfields1[0]);
					// System.out.println("CRNo "+CRNo);
				} else {
					CRNo = i + 1;
				}
				Element NextOrderLine = (Element) listOfOrderLines.item(i + 1);
				Element NextOrderExtn = (Element) NextOrderLine
						.getElementsByTagName("Extn").item(0);
				String NextExtnRequestNo = NextOrderExtn
						.getAttribute("ExtnRequestNo");
				if (NextExtnRequestNo.length() > 2) {
					String[] Ntfields = NextExtnRequestNo.split("-");
					String[] Ntfields1 = Ntfields[1].split("\\.");
					NRNo = Integer.parseInt(Ntfields1[0]);
					// System.out.println("NRNo "+NRNo);
				} else {
					NRNo = i + 2;
				}

				if (((CRNo - PRNo) == 1) && ((NRNo - CRNo) == 1)) {
					cnt++;
				} else {
					if (cnt > 1) {
						ReqNoText = ReqNoText + " To " + CurExtnRequestNo;
					} else {
						ReqNoText = ReqNoText + "," + CurExtnRequestNo;
					}
					cnt = 1;
				}
			}
			if (cnt > 1) {
				ReqNoText = ReqNoText + " To " + LastExtnRequestNo;
			} else {
				ReqNoText = ReqNoText + "," + LastExtnRequestNo;
			}
		}
		// System.out.println("ReqNoText - before the ReqNo is set: "+
		// ReqNoText);
		OrdDtlsElm.setAttribute("ReqNoText", ReqNoText);
		// End Setting Request Numbers

		// Setting BackOrdered & Cancelled Items
		String BackOrderItemText = " ";
		String CancelledItemText = " ";
		String AllItemText = " ";
		// CR 70 - include forward order items on shipping status report - kjs
		String ForwardOrderItemText = " ";
		int ForwardOrderCnt = 0;
		int BackOrderCnt = 0;
		int CancelItemCnt = 0;
		for (int j = 0; j < totOrderLines; j++) {
			Element OrderLine = (Element) listOfOrderLines.item(j);
			Element OrderExtn = (Element) OrderLine
					.getElementsByTagName("Extn").item(0);
			String BackOrderFlag = OrderExtn.getAttribute("ExtnBackOrderFlag");
			String ForwardOrderFlag = OrderExtn
					.getAttribute("ExtnForwardOrderFlag");
			String BOQtyStr = OrderExtn.getAttribute("ExtnBackorderedQty");
			String FOQtyStr = OrderExtn.getAttribute("ExtnFwdQty");
			float BOQty = Float.valueOf(BOQtyStr.trim()).floatValue();
			float FOQty = Float.valueOf(FOQtyStr.trim()).floatValue();
			String UTFQtyStr = OrderExtn.getAttribute("ExtnUTFQty");
			float UTFQty = Float.valueOf(UTFQtyStr.trim()).floatValue();
			Element OrderItem = (Element) OrderLine
					.getElementsByTagName("Item").item(0);
			String ExtnItemId = OrderItem.getAttribute("ItemID");
			// cr 69 change backorder/cancelled output on report
			String strReqNo = OrderExtn.getAttribute("ExtnRequestNo");
			String strReqQty = OrderExtn.getAttribute("ExtnOrigReqQty");
			// System.out.println("++++++++++++++++++++++++++++++++");
			// System.out.println("BOQtyStr: "+ BOQtyStr);
			if (BOQty > 0) {
				BackOrderCnt++;
				// System.out.println("BackOrderCnt: "+BackOrderCnt);
				if (BackOrderCnt == 1) {
					BackOrderItemText = strReqNo + " (" + BOQtyStr + ")";
				} else {
					BackOrderItemText = BackOrderItemText + ", " + strReqNo
							+ " (" + BOQtyStr + ")";
				}
			}
			// System.out.println("COUNT FOR BACKORDER: "+BackOrderCnt);
			// System.out.println("BackOrderItemText: "+ BackOrderItemText);
			// System.out.println("BOQtyStr: "+ BOQtyStr);
			// System.out.println(" ");
			if (UTFQty > 0) {
				CancelItemCnt++;
				if (CancelItemCnt == 1) {
					CancelledItemText = strReqNo + " (" + UTFQtyStr + ")";
				} else {
					CancelledItemText = CancelledItemText + ", " + strReqNo
							+ " (" + UTFQtyStr + ")";
				}
			}
			// System.out.println("CancelledItemText: "+ CancelledItemText);
			// System.out.println("strReqNo: "+ strReqNo);
			// System.out.println("UTFQtyStr: "+ UTFQtyStr);
			// check for fwd qty
			if (FOQty > 0) {
				ForwardOrderCnt++;
				if (ForwardOrderCnt == 1) {
					ForwardOrderItemText = strReqNo + " (" + FOQtyStr + ")";
				} else {
					ForwardOrderItemText = ForwardOrderItemText + ", "
							+ strReqNo + " (" + FOQtyStr + ")";
				}
			}
			// System.out.println("ForwardOrderItemText: "+
			// ForwardOrderItemText);
			// System.out.println("strReqNo: "+ strReqNo);
			// System.out.println("FOQtyStr: "+ FOQtyStr);

		}

		// if ((BackOrderCnt == 0) && (CancelItemCnt == 0))
		if ((BackOrderCnt == 0) && (CancelItemCnt == 0)
				&& (ForwardOrderCnt == 0)) {
			AllItemText = "ALL";
		}
		// System.out.println("BackOrderItemText "+BackOrderItemText);
		// System.out.println("CancelledItemText "+CancelledItemText);
		OrdDtlsElm.setAttribute("BackOrderItemText", BackOrderItemText);
		OrdDtlsElm.setAttribute("CancelledItemText", CancelledItemText);
		OrdDtlsElm.setAttribute("ForwardOrderItemText", ForwardOrderItemText);
		OrdDtlsElm.setAttribute("AllItemText", AllItemText);
		// End Setting BackOrdered Items

		Element ordElem = shipmentDetails.createElement("Order");
		shiptmentrootElm.appendChild(ordElem);

		// System.out.println("Before Shipment Details " +
		// XMLUtil.getXMLString(shipmentDetails));
		XMLUtil.copyElement(shipmentDetails, OrdDtlsElm, ordElem);

		if (log.isVerboseEnabled()) {
			log
					.verbose("NWCGReportsShippingStatus::getOtherInfoAndPrepareXml, After adjust lines : "
							+ XMLUtil.getXMLString(shipmentDetails));
		}
		// System.out.println("After Shipment Details " +
		// XMLUtil.getXMLString(shipmentDetails));

		return shipmentDetails;
	}

	private Document getshipmentDetails(YFSEnvironment env, String shipmentKey,
			String usrlocale) {
		String orderHdrKey = "";
		Document shipmentDtls = null;
		try {
			Document ShipmentHdrInputDoc = XMLUtil.getDocument();
			Element ShipmentHdrElm = ShipmentHdrInputDoc
					.createElement("Shipment");
			ShipmentHdrElm.setAttribute("ShipmentKey", shipmentKey);
			ShipmentHdrInputDoc.appendChild(ShipmentHdrElm);
			// System.out.println("Shipment Input XML "+
			// XMLUtil.getXMLString(ShipmentHdrInputDoc));
			shipmentDtls = CommonUtilities.invokeAPI(env, "getShipmentDetails",
					ShipmentHdrInputDoc);
			// System.out.println("Shipment Detail XML "+
			// XMLUtil.getXMLString(shipmentDtls));

			Element shipmentElem = (Element) shipmentDtls.getElementsByTagName(
					"Shipment").item(0);
			Element extnElem = (Element) shipmentElem.getElementsByTagName(
					"Extn").item(0);
			String ActualShipmentDate = shipmentElem
					.getAttribute("ActualShipmentDate");
			String ExpectedDeliveryDate = shipmentElem
					.getAttribute("ExpectedDeliveryDate");
			String ExtnEstimatedDepartDate = extnElem
					.getAttribute("ExtnEstimatedDepartDate");
			String ExtnEstimatedArrivalDate = extnElem
					.getAttribute("ExtnEstimatedArrivalDate");

			System.out.println("before conversion");
			System.out.println("ActualShipmentDate ::" + ActualShipmentDate);
			System.out
					.println("ExpectedDeliveryDate ::" + ExpectedDeliveryDate);
			System.out.println("ExtnEstimatedDepartDate ::"
					+ ExtnEstimatedDepartDate);
			System.out.println("ExtnEstimatedArrivalDate ::"
					+ ExtnEstimatedArrivalDate);

			// call the conversion function for all the four attributes here
			ActualShipmentDate = reportsUtil.convertTimeZone(
					ActualShipmentDate, usrlocale);
			ExpectedDeliveryDate = reportsUtil.convertTimeZone(
					ExpectedDeliveryDate, usrlocale);
			ExtnEstimatedDepartDate = reportsUtil.convertTimeZone(
					ExtnEstimatedDepartDate, usrlocale);
			ExtnEstimatedArrivalDate = reportsUtil.convertTimeZone(
					ExtnEstimatedArrivalDate, usrlocale);

			System.out.println("after conversion");
			System.out.println("ActualShipmentDate ::" + ActualShipmentDate);
			System.out
					.println("ExpectedDeliveryDate ::" + ExpectedDeliveryDate);
			System.out.println("ExtnEstimatedDepartDate ::"
					+ ExtnEstimatedDepartDate);
			System.out.println("ExtnEstimatedArrivalDate ::"
					+ ExtnEstimatedArrivalDate);

			shipmentElem.setAttribute("ActualShipmentDate", ActualShipmentDate);
			shipmentElem.setAttribute("ExpectedDeliveryDate",
					ExpectedDeliveryDate);
			extnElem.setAttribute("ExtnEstimatedDepartDate",
					ExtnEstimatedDepartDate);
			extnElem.setAttribute("ExtnEstimatedArrivalDate",
					ExtnEstimatedArrivalDate);
			if (log.isVerboseEnabled()) {
				log
						.verbose("NWCGReportsShippingStatus::getOrderHeaderKey, Output from getShipmentDetails : "
								+ XMLUtil.getXMLString(shipmentDtls));
			}
			log
					.verbose("NWCGReportsShippingStatus::getOrderHeaderKey, OrderHeaderKey : "
							+ orderHdrKey);
		} catch (Exception e) {
			log
					.error("NWCGReportsShippingStatus::getOrderHeaderKey, Exception Msg : "
							+ e.getMessage());
			log
					.error("NWCGReportsShippingStatus::getOrderHeaderKey, StackTrace : "
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
		log.verbose("NWCGReportsShippingStatus::getOrderDetails, Entered");
		try {
			result = XMLUtil.getDocument();
			Document inputDoc = XMLUtil.getDocument();
			Element orderElm = inputDoc.createElement("Order");
			inputDoc.appendChild(orderElm);
			orderElm.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHdrKey);

			result = CommonUtilities.invokeAPI(env,
					"NWCGReports_getOrderDetails",
					NWCGConstants.API_GET_ORDER_DETAILS, inputDoc);

			if (log.isVerboseEnabled()) {
				log
						.verbose("NWCGReportsShippingStatus::getOrderDetails, getOrderDetails output : "
								+ XMLUtil.getXMLString(result));
			}
		} catch (ParserConfigurationException pce) {
			log
					.error("NWCGReportsShippingStatus::getOrderDetails, ParserConfigurationException Msg : "
							+ pce.getMessage());
			log
					.error("NWCGReportsShippingStatus::getOrderDetails, StackTrace : "
							+ pce.getStackTrace());
		} catch (Exception e) {
			log
					.error("NWCGReportsShippingStatus::getOrderDetails, Exception Msg : "
							+ e.getMessage());
			log
					.error("NWCGReportsShippingStatus::getOrderDetails, StackTrace : "
							+ e.getStackTrace());
		}
		log.verbose("NWCGReportsShippingStatus::getOrderDetails, Returning");
		// System.out.println("OrderDetails XML - Before "+
		// XMLUtil.getXMLString(result));
		Element rootElem = result.getDocumentElement();
		Element OrderTotElem = (Element) rootElem.getElementsByTagName(
				"OverallTotals").item(0);
		String GrandTotal = OrderTotElem.getAttribute("GrandTotal");
		String ConvTotal = StringUtil.NumFormat(GrandTotal);
		OrderTotElem.setAttribute("GrandTotal", ConvTotal);
		// System.out.println("OrderDetails XML - After "+
		// XMLUtil.getXMLString(result));

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
			// System.out.println("Print Doc " +
			// XMLUtil.getXMLString(incidentDtls));
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
}