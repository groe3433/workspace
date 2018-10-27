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

import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGReportsShippingStatus implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGReportsShippingStatus.class);
	
	private Properties _properties;

	NWCGReportsUtil reportsUtil = new NWCGReportsUtil();

	public void setProperties(Properties arg0) throws Exception {
		_properties = arg0;
	}

	public Document triggerShippingStatusReport(YFSEnvironment env, Document inputDoc) throws Exception {
		logger.verbose("@@@@@ Entered NWCGReportsShippingStatus::triggerShippingStatusReport @@@@@");
		logger.verbose("@@@@@ Input Doc : " + XMLUtil.getXMLString(inputDoc));
		String documentId = "NWCG_SHIPPING_STATUS_REPORT";
		Element rootElem1 = (Element) inputDoc.getElementsByTagName("Shipment").item(0);
		String shipmentKey = rootElem1.getAttribute("ShipmentKey");
		String shipNode = rootElem1.getAttribute(NWCGConstants.SHIP_NODE);
		String enterpriseCode = rootElem1.getAttribute(NWCGConstants.ENTERPRISE_CODE_STR);
		Element rootElem2 = (Element) inputDoc.getElementsByTagName("PrinterPreference").item(0);
		String printerId = rootElem2.getAttribute("PrinterId");
		String userlocale = rootElem2.getAttribute("UserLocale");
		Element rootElem3 = (Element) inputDoc.getElementsByTagName("LabelPreference").item(0);
		String noOfCopies = rootElem3.getAttribute("NoOfCopies");
		int noCopies = 1;
		if (Integer.parseInt(noOfCopies) > 1)
			noCopies = Integer.parseInt(noOfCopies);
		Document shipmentDetails = getshipmentDetails(env, shipmentKey, userlocale);
		Document sDtls = getOtherInfoAndPrepareXml(env, shipmentDetails);
		Element tempElm = sDtls.getDocumentElement();
		NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
		Document shipstatusReportDoc = reportsUtil.generatePrintHeader(documentId, "xml:/Shipment", shipNode, printerId, enterpriseCode);
		Element shipstatusRptElmRootNode = shipstatusReportDoc.getDocumentElement();
		Element printDocElm = (Element) shipstatusRptElmRootNode.getFirstChild();
		Element inputDataElm = shipstatusReportDoc.createElement("InputData");
		Element shipstatusDtlsElm = shipstatusReportDoc.createElement("Shipment");
		XMLUtil.copyElement(shipstatusReportDoc, tempElm, shipstatusDtlsElm);
		inputDataElm.appendChild(shipstatusDtlsElm);
		printDocElm.appendChild(inputDataElm);
		logger.verbose("@@@@@ XML to printDocumentSet : " + XMLUtil.getXMLString(shipstatusReportDoc));
		for (int i = 0; i < noCopies; i++) {
			CommonUtilities.invokeAPI(env, NWCGConstants.API_PRINT_DOCUMENT_SET, shipstatusReportDoc);
		}
		logger.verbose("@@@@@ Exiting NWCGReportsShippingStatus::triggerShippingStatusReport @@@@@");
		return shipstatusReportDoc;
	}

	private Document getOtherInfoAndPrepareXml(YFSEnvironment env, Document shipmentDetails) throws Exception {
		logger.verbose("@@@@@ Entered NWCGReportsShippingStatus::getOtherInfoAndPrepareXml @@@@@");
		Element shipmentLineElm = (Element) shipmentDetails.getElementsByTagName("ShipmentLine").item(0);
		Element shiptmentrootElm = shipmentDetails.getDocumentElement();
		String orderHdrKey = shipmentLineElm.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		Document orderDtls = getOrderDetails(env, orderHdrKey);
		Element OrdDtlsElm = (Element) orderDtls.getElementsByTagName("Order").item(0);
		Element orderDtlsRootElm = orderDtls.getDocumentElement();
		String documentType = OrdDtlsElm.getAttribute(NWCGConstants.DOCUMENT_TYPE);
		NWCGReportsUtil reportUtil = new NWCGReportsUtil();
		String strDate = reportUtil.dateToString(new java.util.Date(), "MM-dd-yyyy");
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
		NodeList listOfInstructions = shipmentDetails.getElementsByTagName("Instruction");
		String InstructionText = "";
		for (int n = 0; n < listOfInstructions.getLength(); n++) {
			Element curInstruction = (Element) listOfInstructions.item(n);
			InstructionText = InstructionText + " " + curInstruction.getAttribute("InstructionText");
		}
		OrdDtlsElm.setAttribute("InstructionText", InstructionText);
		// End Setting Shipping Instructions
		// Setting Ship To Address Default is Issue "Deliver To". If "Deliver To" is NULL, then "Ship To" will be set.
		String deliverToAddr1 = "";
		Element DeliverAddrElm = (Element) orderDtls.getElementsByTagName("AdditionalAddress").item(0);
		Element ShipToAddrElm = (Element) orderDtls.getElementsByTagName("PersonInfoShipTo").item(0);
		Element DeliverToAddrElm = null;
		Element eleExtn = (Element) XMLUtil.getChildNodeByName(orderDtlsRootElm, "Extn");
		String strExtnNavInfo = eleExtn.getAttribute("ExtnNavInfo");
		if (DeliverAddrElm != null) {
			DeliverToAddrElm = (Element) DeliverAddrElm.getElementsByTagName("PersonInfo").item(0);
			if (DeliverToAddrElm != null)
				deliverToAddr1 = DeliverToAddrElm.getAttribute("AddressLine1");
		}
		Element toAddrElem = orderDtls.createElement("ShipToAddress");
		orderDtlsRootElm.appendChild(toAddrElem);
		String AddrArray[] = new String[25];
		if (documentType.equals("0001")) {
			// incident issue
			if (strExtnNavInfo.equalsIgnoreCase(NWCGConstants.SHIPPING_INSTRUCTIONS)) {
				String ShippingInsStr = eleExtn.getAttribute(NWCGConstants.EXTN_SHIPPING_INSTRUCTIONS_ATTR);
				AddrArray = CommonUtilities.getAddressLines(ShippingInsStr);
				int AddrArrayLen = AddrArray.length;
				String city = eleExtn.getAttribute(NWCGConstants.EXTN_SHIPPING_INSTR_CITY_ATTR);
				String state = eleExtn.getAttribute(NWCGConstants.EXTN_SHIPPING_INSTR_STATE_ATTR);
				if (AddrArrayLen > 0)
					toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_1, AddrArray[0]);
				if (AddrArrayLen > 1)
					toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_2, AddrArray[1]);
				if (AddrArrayLen > 2)
					toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_3, AddrArray[2]);
				if (AddrArrayLen > 3)
					toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_4, AddrArray[3]);
				if (AddrArrayLen > 4)
					toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_5, AddrArray[4]);
				if (AddrArrayLen > 5)
					toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_6, AddrArray[5]);
				toAddrElem.setAttribute(NWCGConstants.CITY, city);
				toAddrElem.setAttribute(NWCGConstants.STATE, state);
			} else if (strExtnNavInfo.equalsIgnoreCase(NWCGConstants.WILL_PICK_UP)) {
				String addrLine1 = eleExtn.getAttribute(NWCGConstants.EXTN_WILL_PICK_UP_NAME);
				String addrLine2 = eleExtn.getAttribute(NWCGConstants.EXTN_WILL_PICK_UP_INFO);
				toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_1, addrLine1);
				toAddrElem.setAttribute(NWCGConstants.ADDRESS_LINE_2, addrLine2);
			} else {
				XMLUtil.copyElement(orderDtls, ShipToAddrElm, toAddrElem);
			}
		} else { 
			// Other Doc Types - Other Issue
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
		Element FirstOrderExtn = (Element) FirstOrderLine.getElementsByTagName("Extn").item(0);
		String FirstExtnRequestNo = FirstOrderExtn.getAttribute("ExtnRequestNo");
		int cnt = 1;
		int PRNo, CRNo, NRNo = 0;
		String LastExtnRequestNo = "";
		ReqNoText = ReqNoText + FirstExtnRequestNo;
		if (totOrderLines > 1) {
			Element LastOrderLine = (Element) listOfOrderLines.item(totOrderLines - 1);
			Element LastOrderExtn = (Element) LastOrderLine.getElementsByTagName("Extn").item(0);
			LastExtnRequestNo = LastOrderExtn.getAttribute("ExtnRequestNo");
			ReqNoText = ReqNoText + "," + LastExtnRequestNo;
		}
		if (totOrderLines > 2) {
			ReqNoText = "";
			ReqNoText = ReqNoText + FirstExtnRequestNo;
			for (int i = 1; i < (totOrderLines - 1); i++) {
				Element PrevOrderLine = (Element) listOfOrderLines.item(i - 1);
				Element PrevOrderExtn = (Element) PrevOrderLine.getElementsByTagName("Extn").item(0);
				String PrevExtnRequestNo = PrevOrderExtn.getAttribute("ExtnRequestNo");
				if (PrevExtnRequestNo.length() > 2) {
					String[] Ptfields = PrevExtnRequestNo.split("-");
					String[] Ptfields1 = Ptfields[1].split("\\.");
					PRNo = Integer.parseInt(Ptfields1[0]);
				} else {
					PRNo = i;
				}
				Element CurOrderLine = (Element) listOfOrderLines.item(i);
				Element CurOrderExtn = (Element) CurOrderLine.getElementsByTagName("Extn").item(0);
				String CurExtnRequestNo = CurOrderExtn.getAttribute("ExtnRequestNo");
				if (CurExtnRequestNo.length() > 2) {
					String[] Ctfields = CurExtnRequestNo.split("-");
					String[] Ctfields1 = Ctfields[1].split("\\.");
					CRNo = Integer.parseInt(Ctfields1[0]);
				} else {
					CRNo = i + 1;
				}
				Element NextOrderLine = (Element) listOfOrderLines.item(i + 1);
				Element NextOrderExtn = (Element) NextOrderLine.getElementsByTagName("Extn").item(0);
				String NextExtnRequestNo = NextOrderExtn.getAttribute("ExtnRequestNo");
				if (NextExtnRequestNo.length() > 2) {
					String[] Ntfields = NextExtnRequestNo.split("-");
					String[] Ntfields1 = Ntfields[1].split("\\.");
					NRNo = Integer.parseInt(Ntfields1[0]);
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
			Element OrderExtn = (Element) OrderLine.getElementsByTagName("Extn").item(0);
			String BackOrderFlag = OrderExtn.getAttribute("ExtnBackOrderFlag");
			String ForwardOrderFlag = OrderExtn.getAttribute("ExtnForwardOrderFlag");
			String BOQtyStr = OrderExtn.getAttribute("ExtnBackorderedQty");
			String FOQtyStr = OrderExtn.getAttribute("ExtnFwdQty");
			float BOQty = Float.valueOf(BOQtyStr.trim()).floatValue();
			float FOQty = Float.valueOf(FOQtyStr.trim()).floatValue();
			String UTFQtyStr = OrderExtn.getAttribute("ExtnUTFQty");
			float UTFQty = Float.valueOf(UTFQtyStr.trim()).floatValue();
			Element OrderItem = (Element) OrderLine.getElementsByTagName("Item").item(0);
			String ExtnItemId = OrderItem.getAttribute("ItemID");
			// cr 69 change backorder/cancelled output on report
			String strReqNo = OrderExtn.getAttribute("ExtnRequestNo");
			String strReqQty = OrderExtn.getAttribute("ExtnOrigReqQty");
			if (BOQty > 0) {
				BackOrderCnt++;
				if (BackOrderCnt == 1) {
					BackOrderItemText = strReqNo + " (" + BOQtyStr + ")";
				} else {
					BackOrderItemText = BackOrderItemText + ", " + strReqNo + " (" + BOQtyStr + ")";
				}
			}
			if (UTFQty > 0) {
				CancelItemCnt++;
				if (CancelItemCnt == 1) {
					CancelledItemText = strReqNo + " (" + UTFQtyStr + ")";
				} else {
					CancelledItemText = CancelledItemText + ", " + strReqNo + " (" + UTFQtyStr + ")";
				}
			}
			// check for fwd qty
			if (FOQty > 0) {
				ForwardOrderCnt++;
				if (ForwardOrderCnt == 1) {
					ForwardOrderItemText = strReqNo + " (" + FOQtyStr + ")";
				} else {
					ForwardOrderItemText = ForwardOrderItemText + ", " + strReqNo + " (" + FOQtyStr + ")";
				}
			}
		}
		if ((BackOrderCnt == 0) && (CancelItemCnt == 0) && (ForwardOrderCnt == 0)) {
			AllItemText = "ALL";
		}
		OrdDtlsElm.setAttribute("BackOrderItemText", BackOrderItemText);
		OrdDtlsElm.setAttribute("CancelledItemText", CancelledItemText);
		OrdDtlsElm.setAttribute("ForwardOrderItemText", ForwardOrderItemText);
		OrdDtlsElm.setAttribute("AllItemText", AllItemText);
		// End Setting BackOrdered Items
		Element ordElem = shipmentDetails.createElement("Order");
		shiptmentrootElm.appendChild(ordElem);
		XMLUtil.copyElement(shipmentDetails, OrdDtlsElm, ordElem);
		logger.verbose("@@@@@ After adjust lines : " + XMLUtil.getXMLString(shipmentDetails));
		logger.verbose("@@@@@ Exiting NWCGReportsShippingStatus::getOtherInfoAndPrepareXml @@@@@");
		return shipmentDetails;
	}

	private Document getshipmentDetails(YFSEnvironment env, String shipmentKey, String usrlocale) {
		logger.verbose("@@@@@ Entered NWCGReportsShippingStatus::getshipmentDetails @@@@@");
		logger.verbose("@@@@@ usrlocale :: " + usrlocale);
		String orderHdrKey = "";
		Document shipmentDtls = null;
		try {
			Document ShipmentHdrInputDoc = XMLUtil.getDocument();
			Element ShipmentHdrElm = ShipmentHdrInputDoc.createElement("Shipment");
			ShipmentHdrElm.setAttribute("ShipmentKey", shipmentKey);
			ShipmentHdrInputDoc.appendChild(ShipmentHdrElm);
			shipmentDtls = CommonUtilities.invokeAPI(env, "getShipmentDetails", ShipmentHdrInputDoc);
			Element shipmentElem = (Element) shipmentDtls.getElementsByTagName("Shipment").item(0);
			Element extnElem = (Element) shipmentElem.getElementsByTagName("Extn").item(0);
			String ActualShipmentDate = shipmentElem.getAttribute("ActualShipmentDate");
			String ExpectedDeliveryDate = shipmentElem.getAttribute("ExpectedDeliveryDate");
			String ExtnEstimatedDepartDate = extnElem.getAttribute("ExtnEstimatedDepartDate");
			String ExtnEstimatedArrivalDate = extnElem.getAttribute("ExtnEstimatedArrivalDate");
			// call the conversion function for all the four attributes here
			ActualShipmentDate = reportsUtil.convertTimeZone(ActualShipmentDate, usrlocale);
			logger.verbose("@@@@@ ActualShipmentDate :: " + ActualShipmentDate);
			ExpectedDeliveryDate = reportsUtil.convertTimeZone(ExpectedDeliveryDate, usrlocale);
			logger.verbose("@@@@@ ExpectedDeliveryDate :: " + ExpectedDeliveryDate);
			ExtnEstimatedDepartDate = reportsUtil.convertTimeZone(ExtnEstimatedDepartDate, usrlocale);
			logger.verbose("@@@@@ ExtnEstimatedDepartDate :: " + ExtnEstimatedDepartDate);
			ExtnEstimatedArrivalDate = reportsUtil.convertTimeZone(ExtnEstimatedArrivalDate, usrlocale);
			logger.verbose("@@@@@ ExtnEstimatedArrivalDate :: " + ExtnEstimatedArrivalDate);
			shipmentElem.setAttribute("ActualShipmentDate", ActualShipmentDate);
			shipmentElem.setAttribute("ExpectedDeliveryDate",ExpectedDeliveryDate);
			extnElem.setAttribute("ExtnEstimatedDepartDate",ExtnEstimatedDepartDate);
			extnElem.setAttribute("ExtnEstimatedArrivalDate",ExtnEstimatedArrivalDate);
			logger.verbose("@@@@@ Output from getShipmentDetails : " + XMLUtil.getXMLString(shipmentDtls));
			logger.verbose("@@@@@ OrderHeaderKey : " + orderHdrKey);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);
			e.printStackTrace();
		}
		logger.verbose("@@@@@ Exiting NWCGReportsShippingStatus::getshipmentDetails @@@@@");
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
		logger.verbose("@@@@@ Entered NWCGReportsShippingStatus::getOrderDetails @@@@@");
		Document result = null;
		try {
			result = XMLUtil.getDocument();
			Document inputDoc = XMLUtil.getDocument();
			Element orderElm = inputDoc.createElement("Order");
			inputDoc.appendChild(orderElm);
			orderElm.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHdrKey);
			result = CommonUtilities.invokeAPI(env, "NWCGReports_getOrderDetails", NWCGConstants.API_GET_ORDER_DETAILS, inputDoc);
			logger.verbose("@@@@@ getOrderDetails output : " + XMLUtil.getXMLString(result));
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException : " + pce);
			pce.printStackTrace();
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);
			e.printStackTrace();
		}
		Element rootElem = result.getDocumentElement();
		Element OrderTotElem = (Element) rootElem.getElementsByTagName("OverallTotals").item(0);
		String GrandTotal = OrderTotElem.getAttribute("GrandTotal");
		String ConvTotal = StringUtil.NumFormat(GrandTotal);
		OrderTotElem.setAttribute("GrandTotal", ConvTotal);
		logger.verbose("@@@@@ Exiting NWCGReportsShippingStatus::getOrderDetails @@@@@");
		return result;
	}

	private Document getIncidentDetails(YFSEnvironment env, Document orderDtls) {
		logger.verbose("@@@@@ Entered NWCGReportsShippingStatus::getIncidentDetails @@@@@");
		Document incidentDtls = null;
		String incidentNo = null;
		Element OrderDtlsElm = (Element) orderDtls.getElementsByTagName("Extn").item(0);
		incidentNo = OrderDtlsElm.getAttribute("ExtnIncidentNo");
		try {
			incidentDtls = XMLUtil.getDocument();
			Document getIncidentOrderInput = XMLUtil.createDocument("NWCGIncidentOrder");
			getIncidentOrderInput.getDocumentElement().setAttribute("IncidentNo", incidentNo);
			incidentDtls = CommonUtilities.invokeService(env, NWCGConstants.NWCG_GET_INCIDENT_ORDER_SERVICE, getIncidentOrderInput);
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException : " + pce);
			pce.printStackTrace();
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception : " + e);
			e.printStackTrace();
		}
		logger.verbose("@@@@@ Exiting NWCGReportsShippingStatus::getIncidentDetails @@@@@");
		return incidentDtls;
	}
}