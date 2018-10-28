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
import javax.xml.transform.TransformerException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGCreateIssueFromForwardOrderLines implements YIFCustomApi {
	
	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGCreateIssueFromForwardOrderLines.class);

	String TempOrderHeaderKey = NWCGConstants.EMPTY_STRING;

	private Properties myProperties = null;

	public void setProperties(Properties props) throws Exception {
		this.myProperties = props;
	}	
	
	/**
	 * @param env
	 * @param inDoc
	 *            The orderlines with the Forwardorder qty for which the issue
	 *            needs to be created
	 * @return Document The createOrder output after the issue has been created
	 * @throws Exception
	 */
	public Document createIssuesFromForwardOrderLines(YFSEnvironment env, Document inDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGCreateIssueFromForwardOrderLines::createIssuesFromForwardOrderLines @@@@@");
		logger.verbose("@@@@@ inDoc :: " + XMLUtil.getXMLString(inDoc));

		// Build the createOrder input XML Document changeOrderInput = null;
		Document changeOrderInput = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
		Document createIssueInput = null;
		Document co_output = null;
		Document returnDoc = null;

		// Validate the lines before processing. In case we find that the input is not valid, throw an exception.
		validateLines(env, inDoc);
		logger.verbose("@@@@@ After validateLines... ");
		
		NodeList inpOlList = inDoc.getDocumentElement().getElementsByTagName(NWCGConstants.ORDER_LINE);
		for (int i = 0; i < inpOlList.getLength(); i++) {
			Element ol = (Element) inpOlList.item(i);
			String olKey = ol.getAttribute(NWCGConstants.ORDER_LINE_KEY);
			String userCache = ol.getAttribute("UserCache");
			Document orderLineDoc = getOrderLineDetails(env, olKey);
			Element orderLine = orderLineDoc.getDocumentElement();
			String OrderHeaderKey = orderLine.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
			// Create the order header using the details from the first line as the template
			if (i == 0) {
				TempOrderHeaderKey = OrderHeaderKey;
				Element order = changeOrderInput.getDocumentElement();
				order.setAttribute(NWCGConstants.OVERRIDE, NWCGConstants.YES);
				order.setAttribute(NWCGConstants.ORDER_HEADER_KEY, OrderHeaderKey);
				Element orderlines = changeOrderInput.createElement(NWCGConstants.ORDER_LINES);
				order.appendChild(orderlines);
				createIssueInput = createIssueHeader(env, orderLine, userCache);
			}

			// Add the line to the createOrder input XML
			addLineToIssue(env, createIssueInput, ol, orderLine, i + 1);
			co_output = changeForwardOrderStatusInOriginalIssue(env, ol, orderLine, changeOrderInput);
			changeOrderInput = co_output;
		}
		returnDoc = CommonUtilities.invokeAPI(env, NWCGConstants.API_CHANGE_ORDER, changeOrderInput);

		// Create the issue
		Document createOrderTemplate = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
		createOrderTemplate.getDocumentElement().setAttribute(NWCGConstants.ORDER_HEADER_KEY, NWCGConstants.EMPTY_STRING);
		returnDoc = CommonUtilities.invokeAPI(env, createOrderTemplate, NWCGConstants.API_CREATE_ORDER, createIssueInput);

		logger.verbose("@@@@@ returnDoc :: " + XMLUtil.getXMLString(returnDoc));
		logger.verbose("@@@@@ Exiting NWCGCreateIssueFromForwardOrderLines::createIssuesFromForwardOrderLines @@@@@");
		return returnDoc;
	}

	/**
	 * This method validates the input from the JSP if the selected lines belong
	 * to different order/incidents then we throw and exception if the issue
	 * quantity for the new lines is not entered or not a number we throw and
	 * exception
	 * 
	 * @param YFSEnvironment
	 * @param inDoc
	 */
	private void validateLines(YFSEnvironment env, Document inDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGCreateIssueFromForwardOrderLines::validateLines @@@@@");
		logger.verbose("@@@@@ inDoc :: " + XMLUtil.getXMLString(inDoc));

		String primaryOrderIdentifier = NWCGConstants.PRIMARY_IDENTIFIER_NULL;
		NodeList inpOlList = inDoc.getDocumentElement().getElementsByTagName(NWCGConstants.ORDER_LINE);
		for (int i = 0; i < inpOlList.getLength(); i++) {
			Element ol = (Element) inpOlList.item(i);
			String tempOrderIdentifier = ol.getAttribute(NWCGConstants.INCIDENT_NO_ATTR);
			if (StringUtil.isEmpty(tempOrderIdentifier))
				tempOrderIdentifier = NWCGConstants.PRIMARY_IDENTIFIER_NULL;
			if (i == 0) {
				primaryOrderIdentifier = tempOrderIdentifier;
			} else {
				if (!primaryOrderIdentifier.equals(tempOrderIdentifier)) {
					logger.verbose("@@@@@ Check if the identifiers are not the same and throw and exception...");
					throw new NWCGException("NWCG_ISSUE_FORWARDORDER_002");
				}
			}
			// BEGIN - CR 1769 - Aug 12, 2015
			// only added the parts for checking FWD Order Qty
			String newIssueQty = ol.getAttribute("NewIssueQty");
			String ExtnFwdQty = ol.getAttribute("ExtnFwdQty");
			logger.verbose("@@@@@ newIssueQty :: " + newIssueQty + " :: Length :: " + newIssueQty.length());
			if (StringUtil.isEmpty(newIssueQty)) {
				logger.verbose("@@@@@ Check if the identifiers are not the same and throw and exception...newIssueQty :: " + newIssueQty);
				throw new NWCGException("NWCG_ISSUE_FORWARDORDER_003");
			} else if(StringUtil.isEmpty(ExtnFwdQty)) {
				logger.verbose("@@@@@ Check if the identifiers are not the same and throw and exception...ExtnFwdQty :: " + ExtnFwdQty);
				throw new NWCGException("NWCG_ISSUE_FORWARDORDER_003");
			} else {
				try {
					logger.verbose("@@@@@ Try to parse :: " + newIssueQty);
					Double d1 = Double.parseDouble(newIssueQty);
					Double d2 = Double.parseDouble(ExtnFwdQty);
					if(d1 > d2) {
						throw new ArithmeticException();
					}
				} catch (NumberFormatException ne) {
					logger.error("!!!!! Caught NumberFormatException :: " + ne);
					throw new NWCGException("NWCG_ISSUE_FORWARDORDER_004", new String[] { newIssueQty });
				} catch(ArithmeticException ae) {
					logger.error("!!!!! Caught ArithmeticException :: " + ae);
					throw new NWCGException("Issue Qty can't be Greater than Fwd Qty (issue qty then fwd qty here) :: ", new String[] { newIssueQty, ExtnFwdQty });
				}
			}
			// END - CR 1769 - Aug 12, 2015
		}
		logger.verbose("@@@@@ Exiting NWCGCreateIssueFromForwardOrderLines::validateLines @@@@@");
	}

	/**
	 * Create the issue header using the original order details also incorporate
	 * the incident details into the header from the original order
	 * 
	 * @param env
	 * @param orderLine
	 * @return
	 * @throws Exception
	 */
	private Document createIssueHeader(YFSEnvironment env, Element orderLine, String userCache) throws Exception {
		logger.verbose("@@@@@ Entering NWCGCreateIssueFromForwardOrderLines::createIssueHeader @@@@@");

		Document createIssueInput = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
		Element createIssueInputDocElm = createIssueInput.getDocumentElement();
		Element olOrdElem = (Element) XMLUtil.getChildNodeByName(orderLine, NWCGConstants.ORDER_ELM);
		createIssueInputDocElm.setAttribute(NWCGConstants.DRAFT_ORDER_FLAG, NWCGConstants.YES);
		createIssueInputDocElm.setAttribute(NWCGConstants.ENTERPRISE_CODE_STR, olOrdElem.getAttribute(NWCGConstants.ENTERPRISE_CODE_STR));
		createIssueInputDocElm.setAttribute(NWCGConstants.SELLER_ORGANIZATION_CODE, olOrdElem.getAttribute(NWCGConstants.SELLER_ORGANIZATION_CODE));
		createIssueInputDocElm.setAttribute(NWCGConstants.BILLTO_ID_ATTR, olOrdElem.getAttribute(NWCGConstants.BILLTO_ID_ATTR));
		createIssueInputDocElm.setAttribute(NWCGConstants.DOCUMENT_TYPE, olOrdElem.getAttribute(NWCGConstants.DOCUMENT_TYPE));
		createIssueInputDocElm.setAttribute(NWCGConstants.ORDER_TYPE, NWCGConstants.OB_ORDER_TYPE_FWD_ORDER);
		createIssueInputDocElm.setAttribute(NWCGConstants.SHIP_NODE, userCache);
		createIssueInputDocElm.setAttribute(NWCGConstants.REQ_SHIP_DATE_ATTR, olOrdElem.getAttribute(NWCGConstants.REQ_SHIP_DATE_ATTR));
		createIssueInputDocElm.setAttribute(NWCGConstants.REQ_DELIVERY_DATE_ATTR, olOrdElem.getAttribute(NWCGConstants.REQ_DELIVERY_DATE_ATTR));
		createIssueInputDocElm.setAttribute(NWCGConstants.REQ_CANCEL_DATE_ATTR, olOrdElem.getAttribute(NWCGConstants.REQ_CANCEL_DATE_ATTR));
		createIssueInputDocElm.setAttribute(NWCGConstants.SCAC_AND_SERVICE_KEY, olOrdElem.getAttribute(NWCGConstants.SCAC_AND_SERVICE_KEY));
		Node olOrderExtnNode = XMLUtil.getChildNodeByName(olOrdElem, NWCGConstants.EXTN_ELEMENT);

		// Import the getOrderLineDetails:/OrdeLine/Order/Extn node into our createOrder input doc
		olOrderExtnNode = createIssueInput.importNode(olOrderExtnNode, true);
		createIssueInputDocElm.appendChild(olOrderExtnNode);
		Element olOrdExtnElem = (Element) XMLUtil.getChildNodeByName(olOrdElem, NWCGConstants.EXTN_ELEMENT);
		String incidentNo = olOrdExtnElem.getAttribute(NWCGConstants.INCIDENT_NO);
		String incidentYear = olOrdExtnElem.getAttribute(NWCGConstants.INCIDENT_YEAR);
		
		// Top of - CR 524 populate extn attributes from OrderHeader records
		String reqDeliveryDate = olOrdExtnElem.getAttribute("ExtnReqDeliveryDate");
		String shipAcctCode = olOrdExtnElem.getAttribute("ExtnShipAcctCode");
		String sAOverrideCode = olOrdExtnElem.getAttribute("ExtnSAOverrideCode");
		String unitType = olOrdExtnElem.getAttribute("ExtnUnitType");
		// Bottom of - CR 524 populate extn attributes from OrderHeader records

		logger.verbose("@@@@@ Incident No:" + incidentNo);
		logger.verbose("@@@@@ Incident Year:" + incidentYear);
		if (!StringUtil.isEmpty(incidentNo)) {
			addIncidentDetails(env, createIssueInput, incidentNo, incidentYear, reqDeliveryDate, shipAcctCode, sAOverrideCode, unitType);
		}

		// Add the orderLines element
		Element orderLines = createIssueInput.createElement(NWCGConstants.ORDER_LINES);
		createIssueInputDocElm.appendChild(orderLines);

		logger.verbose("@@@@@ Exiting NWCGCreateIssueFromForwardOrderLines::createIssueHeader @@@@@");
		return createIssueInput;
	}

	/**
	 * This method adds the incident details to the order header if Incident no
	 * is not null
	 * 
	 * @param env
	 * @param createIssueInput
	 * @param incidentNo
	 * @throws Exception
	 */
	private void addIncidentDetails(YFSEnvironment env, Document createIssueInput, String incidentNo, String incidentYear, String reqDeliveryDate, String shipAcctCode, String sAOverrideCode, String unitType) throws Exception {
		logger.verbose("@@@@@ Entering NWCGCreateIssueFromForwardOrderLines::addIncidentDetails @@@@@");
		
		Element order = createIssueInput.getDocumentElement();
		Document getIncidentOrderInput = XMLUtil.createDocument(NWCGConstants.NWCG_INCIDENT_ORDER);
		getIncidentOrderInput.getDocumentElement().setAttribute(NWCGConstants.INCIDENT_NO_ATTR, incidentNo);
		if (incidentYear.length() == 0)
			incidentYear = " ";
		getIncidentOrderInput.getDocumentElement().setAttribute(NWCGConstants.YEAR, incidentYear);
		Document incidentDetails = CommonUtilities.invokeService(env, NWCGConstants.SVC_GET_INCIDENT_ORDER_DTLS_ONLY_SVC, getIncidentOrderInput);
		// If we do not get any incident details throw an exception
		if (null == incidentDetails) {
			NWCGException e = new NWCGException("NWCG_ISSUE_FORWARDORDER_001", new String[] { incidentNo });
			throw e;
		}
		Element incidentDocElm = incidentDetails.getDocumentElement();
		Node olOrderExtnNode = XMLUtil.getChildNodeByName(order, NWCGConstants.EXTN_ELEMENT);
		Element orderExtn = (olOrderExtnNode instanceof Element) ? (Element) olOrderExtnNode : null;

		// copy for more customer detail info at Order/Extn level here
		if (orderExtn != null) {
			orderExtn.setAttribute("ExtnUnitType", unitType);
			orderExtn.setAttribute("ExtnDepartment", incidentDocElm.getAttribute("Department"));
			orderExtn.setAttribute("ExtnAgency", incidentDocElm.getAttribute("Agency"));
			orderExtn.setAttribute("ExtnGACC", incidentDocElm.getAttribute("GACC"));

			// copy the following attributes from the ExtnIncidentNo,ExtnIncidentYear,ExtnIncidentName,ExtnFsAcctCode,ExtnBlmAcctCode,ExtnOtherAcctCode,ExtnOverrideCode,ExtnIncidentTeamType,ExtnIncidentType
			orderExtn.setAttribute("ExtnIncidentNo", incidentDocElm.getAttribute("IncidentNo"));
			orderExtn.setAttribute("ExtnIncidentYear", incidentDocElm.getAttribute("Year"));
			orderExtn.setAttribute("ExtnIncidentName", incidentDocElm.getAttribute("IncidentName"));
			String strIncidentFsAcctCode = incidentDocElm.getAttribute("IncidentFsAcctCode");
			if (strIncidentFsAcctCode != null && !strIncidentFsAcctCode.equals("")) {
				orderExtn.setAttribute("ExtnFsAcctCode", strIncidentFsAcctCode);
				orderExtn.setAttribute("ExtnShipAcctCode", strIncidentFsAcctCode);
			}
			String strIncidentBlmAcctCode = incidentDocElm.getAttribute("IncidentBlmAcctCode");
			if (strIncidentBlmAcctCode != null && !strIncidentBlmAcctCode.equals("")) {
				orderExtn.setAttribute("ExtnBlmAcctCode", strIncidentBlmAcctCode);
				orderExtn.setAttribute("ExtnShipAcctCode", strIncidentBlmAcctCode);
				// CR 1769 - Begin
				orderExtn.setAttribute("ExtnCostCenter", incidentDocElm.getAttribute("CostCenter"));
				orderExtn.setAttribute("ExtnFunctionalArea", incidentDocElm.getAttribute("FunctionalArea"));
				orderExtn.setAttribute("ExtnWBS", incidentDocElm.getAttribute("WBS"));
				// CR 1769 - End
			}
			orderExtn.setAttribute("ExtnOtherAcctCode", incidentDocElm.getAttribute("IncidentOtherAcctCode"));
			orderExtn.setAttribute("ExtnOverrideCode", incidentDocElm.getAttribute("OverrideCode"));
			orderExtn.setAttribute("ExtnCustomerName", incidentDocElm.getAttribute("CustomerName"));
			orderExtn.setAttribute("ExtnSAOverrideCode", incidentDocElm.getAttribute("OverrideCode"));
			orderExtn.setAttribute("ExtnIncidentTeamType", incidentDocElm.getAttribute("IncidentTeamType"));
			orderExtn.setAttribute("ExtnIncidentType", incidentDocElm.getAttribute("IncidentType"));
			orderExtn.setAttribute("ExtnPoNo", incidentDocElm.getAttribute("CustomerPONo"));
		}
		
		// Top of CR 524 - populate Requested Delivery Date from the original order
		if (orderExtn != null) {
			if (!StringUtil.isEmpty(reqDeliveryDate)) {
				orderExtn.setAttribute("ExtnReqDeliveryDate", reqDeliveryDate);
			}
			if (!StringUtil.isEmpty(shipAcctCode)) {
				// user manually entered ShipAcctCode
				orderExtn.setAttribute("ExtnShipAcctCode", shipAcctCode); 
			}
			if (!StringUtil.isEmpty(sAOverrideCode)) {
				// user manually entered ShipAcctOverrideCode
				orderExtn.setAttribute("ExtnSAOverrideCode", sAOverrideCode); 
			}
		}
		// Bottom of CR 524 - populate Requested Delivery Date from the original order

		Element incidentShipToAdd = (Element) XMLUtil.getChildNodeByName(incidentDocElm, "YFSPersonInfoShipTo");
		if (null != incidentShipToAdd) {
			Element personInfoShipTo = createIssueInput.createElement("PersonInfoShipTo");
			copyAddress(incidentShipToAdd, personInfoShipTo);
			personInfoShipTo = removeUnneededAddressFieldsFromPersonInfoElement(personInfoShipTo);
			order.appendChild(personInfoShipTo);
		}
		Element incidentBillToAdd = (Element) XMLUtil.getChildNodeByName(incidentDocElm, "YFSPersonInfoBillTo");
		if (null != incidentBillToAdd) {
			Element personInfoBillTo = createIssueInput.createElement("PersonInfoBillTo");
			copyAddress(incidentBillToAdd, personInfoBillTo);
			personInfoBillTo = removeUnneededAddressFieldsFromPersonInfoElement(personInfoBillTo);
			order.appendChild(personInfoBillTo);
		}
		Element incidentDeliverTo = (Element) XMLUtil.getChildNodeByName(incidentDocElm, "YFSPersonInfoDeliverTo");
		if (null != incidentDeliverTo) {
			Element additionalAddresses = createIssueInput.createElement("AdditionalAddresses");
			order.appendChild(additionalAddresses);
			Element additionalAddress = createIssueInput.createElement("AdditionalAddress");
			additionalAddresses.appendChild(additionalAddress);
			additionalAddress.setAttribute("AddressType", NWCGConstants.ADDRESS_TYPE_DELIVER);
			Element personInfo = createIssueInput.createElement("PersonInfo");
			additionalAddress.appendChild(personInfo);
			copyAddress(incidentDeliverTo, personInfo);
		}
		logger.verbose("@@@@@ Exiting NWCGCreateIssueFromForwardOrderLines::addIncidentDetails @@@@@");
	}

	/**
	 * @param from
	 *            Address element
	 * @param to
	 *            Address element
	 */
	private void copyAddress(Element from, Element to) {
		NamedNodeMap attrMap = from.getAttributes();
		for (int i = 0; i < attrMap.getLength(); i++) {
			Attr attr = (Attr) attrMap.item(i);
			to.setAttribute(attr.getName(), attr.getValue());
		}
	}

	/**
	 * This method adds an OrderLine to the createOrder input It gets the Issue
	 * Qty from the input orderline and adjusts it with the order qty while
	 * creating the new issue It also sets the Forwardorder flag on the line if
	 * the issue qty cannot does not fully satisfy the Forwardorder qty The
	 * S-Number or the request no. is also updated with increments/extensions
	 * eg. S_1 would become S_1.1 after first extension and S_1.1.1 after second
	 * extension and so on.
	 * 
	 * @param createOrderInputDoc
	 * @param inOrderLine
	 * @param origOrderLine
	 * @return Document
	 * @throws Exception
	 */
	private Document addLineToIssue(YFSEnvironment env, Document createOrderInputDoc, Element inOrderLine, Element origOrderLine, int lineCounter) throws Exception {
		logger.verbose("@@@@@ Entering NWCGCreateIssueFromForwardOrderLines::addLineToIssue @@@@@");

		Element orderLines = (Element) XMLUtil.getChildNodeByName(createOrderInputDoc.getDocumentElement(), NWCGConstants.ORDER_LINES);
		Element orderLine = (Element) createOrderInputDoc.importNode(origOrderLine, true);
		orderLines.appendChild(orderLine);
		
		// remove the OrderHeaderKey from the orderline element
		orderLine.removeAttribute(NWCGConstants.ORDER_HEADER_KEY);
		
		// remove the order and extn elements from the orderline
		orderLine.removeChild(XMLUtil.getChildNodeByName(orderLine, NWCGConstants.ORDER_ELM));
		
		// get the input issue quantity
		String newIssueQty = inOrderLine.getAttribute("NewIssueQty");

		// if the issue quantity is not entered throw Forward and exception
		if (StringUtil.isEmpty(newIssueQty)) {
			throw new NWCGException("NWCG_ISSUE_FORWARDORDER_003");
		} 

		// Else we'll process the order line Set the OL/OrderLineTranQuantity/@OrderedQty on the new line to the newIssueQty variable
		Element olTranQtyElm = createOrderInputDoc.createElement(NWCGConstants.ORDER_LINE_TRAN_QTY_ELM);
		orderLine.appendChild(olTranQtyElm);
		olTranQtyElm.setAttribute(NWCGConstants.ORDERED_QTY, newIssueQty);

		// Top of CR 382: calc RFI Qty 
		Element olItem = (Element) XMLUtil.getChildNodeByName(orderLine, NWCGConstants.ITEM);
		String strItemID = olItem.getAttribute(NWCGConstants.ITEM_ID);
		
		// current user cache
		String strNode = inOrderLine.getAttribute("UserCache"); 

		Document ItemDtls = null;
		Document ItemDetailsInput = XMLUtil.createDocument(NWCGConstants.ITEM);
		ItemDetailsInput.getDocumentElement().setAttribute(NWCGConstants.ITEM_ID, strItemID);
		ItemDetailsInput.getDocumentElement().setAttribute(NWCGConstants.NODE, strNode);
		ItemDtls = XMLUtil.getDocument();
		ItemDtls = CommonUtilities.invokeService(env, "NWCGPopulateItemDetailsService", ItemDetailsInput);
		Element itemDtlsElement = (Element) XMLUtil.getChildNodeByName(ItemDtls, NWCGConstants.ITEM);
		String strRFIQty = itemDtlsElement.getAttribute("AvailableQty");
		// Bottom of CR 382: calc RFI Qty

		// As we have already imported the orig orderline we can get the request number from there
		Element olExtn = (Element) XMLUtil.getChildNodeByName(orderLine, NWCGConstants.EXTN_ELEMENT);
		// set the RequestedQty
		olExtn.setAttribute("ExtnOrigReqQty", newIssueQty);
		// set the Forward order flag to N
		olExtn.setAttribute(NWCGConstants.EXTN_FORWARD_ORDER_FLAG, NWCGConstants.NO);
		olExtn.setAttribute(NWCGConstants.EXTN_FWD_QTY, "0");
		olExtn.setAttribute(NWCGConstants.EXTN_RFI_QTY, strRFIQty);
		// if neither strings are empty
		if (!StringUtil.isEmpty(newIssueQty)) {
			double newQty = Double.parseDouble(newIssueQty);
			// Top of CR 382: calc RFI Qty 
			// depends on the "Available RFI Qty", set the new OrderedQty and FwdQty accordingly
			olExtn.setAttribute(NWCGConstants.EXTN_RFI_QTY, strRFIQty);
			double rfiQty = Double.parseDouble(strRFIQty);
			// check if ordered qty is greater than available qty
			double delta = newQty - rfiQty;
			// set ordered qty to avail qty and set the rest to another forwarded qty
			if (delta > 0) { 
				// new ordered qty
				orderLine.setAttribute("OrderedQty", strRFIQty); 
			}
			// Bottom of CR 382: calc RFI Qty
		}

		// Updated to not do a plus 1 of existing dot number, but instead to add a new ".<lineCounter>" on new lines This change was also made in the createIssueFromBackorderLines as well.
		String requestNo = olExtn.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
		if (!StringUtil.isEmpty(requestNo)) {
			StringBuffer sb = new StringBuffer(requestNo);
			sb.append(NWCGConstants.REQUEST_NO_EXTN_DELIM);
			sb.append(new String(NWCGConstants.EMPTY_STRING + lineCounter));
			olExtn.setAttribute(NWCGConstants.EXTN_REQUEST_NO, sb.toString());
		}

		logger.verbose("@@@@@ Exiting NWCGCreateIssueFromForwardOrderLines::addLineToIssue @@@@@");
		return createOrderInputDoc;
	}

	/**
	 * This method removes the Forwardorder status from the original line as all
	 * the Forwardorder qty is already transfered to the new issue
	 * 
	 * @param env
	 * @param inpOrderLine
	 * @param origOrderLine
	 * @return
	 * @throws Exception
	 */
	private Document changeForwardOrderStatusInOriginalIssue(YFSEnvironment env, Element inpOrderLine, Element origOrderLine, Document changeOrderInput) throws Exception {
		logger.verbose("@@@@@ Entering NWCGCreateIssueFromForwardOrderLines::changeForwardOrderStatusInOriginalIssue @@@@@");

		String OrderHeaderKey = origOrderLine.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		String newIssueQty = inpOrderLine.getAttribute("NewIssueQty");
		Double dblNewIssueQty = Double.parseDouble(newIssueQty);
		Element origOlExtn = (Element) XMLUtil.getChildNodeByName(origOrderLine, NWCGConstants.EXTN_ELEMENT);
		String origFwdOrderQty = origOlExtn.getAttribute("ExtnFwdQty");
		Double dblOrigFwdQty = Double.parseDouble(origFwdOrderQty);
		if (OrderHeaderKey.equals(TempOrderHeaderKey)) {
			Element order = changeOrderInput.getDocumentElement();
			Element orderLines = (Element) order.getElementsByTagName(NWCGConstants.ORDER_LINES).item(0);
			Element orderLine = changeOrderInput.createElement(NWCGConstants.ORDER_LINE);
			orderLines.appendChild(orderLine);
			orderLine.setAttribute(NWCGConstants.ORDER_LINE_KEY, inpOrderLine.getAttribute(NWCGConstants.ORDER_LINE_KEY));
			Element orderLineExtn = changeOrderInput.createElement(NWCGConstants.EXTN_ELEMENT);
			orderLine.appendChild(orderLineExtn);
			// reset the Forwardorder flag
			if ((dblOrigFwdQty - dblNewIssueQty) <= 0) {
				orderLineExtn.setAttribute(NWCGConstants.EXTN_FORWARD_ORDER_FLAG, NWCGConstants.NO);
				// Commenting out the following line for CR-800 - SGN orderLineExtn.setAttribute(NWCGConstants.EXTN_FWD_QTY, "0");
			} else {
				orderLineExtn.setAttribute(NWCGConstants.EXTN_FWD_QTY, new Double(dblOrigFwdQty - dblNewIssueQty).toString());
			}
		} else {
			CommonUtilities.invokeAPI(env, NWCGConstants.API_CHANGE_ORDER, changeOrderInput);
			changeOrderInput = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
			Element order = changeOrderInput.getDocumentElement();
			order.setAttribute(NWCGConstants.OVERRIDE, NWCGConstants.YES);
			order.setAttribute(NWCGConstants.ORDER_HEADER_KEY, origOrderLine.getAttribute(NWCGConstants.ORDER_HEADER_KEY));
			Element orderLines = changeOrderInput.createElement(NWCGConstants.ORDER_LINES);
			order.appendChild(orderLines);
			Element orderLine = changeOrderInput.createElement(NWCGConstants.ORDER_LINE);
			orderLines.appendChild(orderLine);
			orderLine.setAttribute(NWCGConstants.ORDER_LINE_KEY, inpOrderLine.getAttribute(NWCGConstants.ORDER_LINE_KEY));
			Element orderLineExtn = changeOrderInput.createElement(NWCGConstants.EXTN_ELEMENT);
			orderLine.appendChild(orderLineExtn);
			// reset the Forwardorder flag
			if ((dblOrigFwdQty - dblNewIssueQty) <= 0) {
				orderLineExtn.setAttribute(NWCGConstants.EXTN_FORWARD_ORDER_FLAG, NWCGConstants.NO);
				// Commenting out the following line for CR-800 - SGN orderLineExtn.setAttribute(NWCGConstants.EXTN_FWD_QTY, "0");
			} else {
				orderLineExtn.setAttribute(NWCGConstants.EXTN_FWD_QTY, new Double(dblOrigFwdQty - dblNewIssueQty).toString());
			}
		}
		TempOrderHeaderKey = OrderHeaderKey;
		
		logger.verbose("@@@@@ Exiting NWCGCreateIssueFromForwardOrderLines::changeForwardOrderStatusInOriginalIssue @@@@@");		
		return changeOrderInput;
	}

	/**
	 * Get the orderline details for the given orderline key
	 * 
	 * @param env
	 * @param orderLineKey
	 * @return
	 * @throws Exception
	 */
	private Document getOrderLineDetails(YFSEnvironment env, String orderLineKey) throws Exception {
		Document getOrderLineDetailsInput = XMLUtil.createDocument("OrderLineDetail");
		getOrderLineDetailsInput.getDocumentElement().setAttribute(NWCGConstants.ORDER_LINE_KEY, orderLineKey);
		env.setApiTemplate(NWCGConstants.API_GET_ORDER_LINE_DETAILS, "NWCGCreateIssueFromForwardOrderLines_getOrderLineDetails");
		Document returnDoc = CommonUtilities.invokeAPI(env, NWCGConstants.API_GET_ORDER_LINE_DETAILS, getOrderLineDetailsInput);
		env.clearApiTemplate(NWCGConstants.API_GET_ORDER_LINE_DETAILS);
		return returnDoc;
	}

	/**
	 * Removes attributes from a PersonInfo{Ship,Bill,Deliver}To element that
	 * should not be present in the input to createOrder
	 * 
	 * @param aPersonInfoElm
	 * @return Element Containing the Element passed in with misc attributes
	 *         removed
	 */
	private Element removeUnneededAddressFieldsFromPersonInfoElement(Element aPersonInfoElm) {
		aPersonInfoElm.removeAttribute("Createprogid");
		aPersonInfoElm.removeAttribute("Createts");
		aPersonInfoElm.removeAttribute("Createuserid");
		aPersonInfoElm.removeAttribute("Modifyprogid");
		aPersonInfoElm.removeAttribute("Modifyts");
		aPersonInfoElm.removeAttribute("Modifyuserid");
		aPersonInfoElm.removeAttribute("PersonInfoKey");
		aPersonInfoElm.removeAttribute("UseCount");
		aPersonInfoElm.removeAttribute("VerificationStatus");
		// aPersonInfoElm.removeAttribute("PersonID");
		aPersonInfoElm.removeAttribute("isHistory");
		aPersonInfoElm.removeAttribute("PreferredShipAddress");
		aPersonInfoElm.removeAttribute("Lockid");
		return aPersonInfoElm;
	}
}