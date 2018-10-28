package com.nwcg.icbs.yantra.api.cachetocache;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGCreateCacheTransferFromBackOrderLines implements YIFCustomApi {
	public Document createCacheTransferFromBackOrderLines(YFSEnvironment env,
			Document inDoc) throws Exception {

		Document changeOrderInput = XMLUtil
				.createDocument(NWCGConstants.ORDER_ELM);
		Document createOrderInput = null;
		Document returnDoc = null;

		Element inputDocElm = inDoc.getDocumentElement();
		if (inputDocElm == null) {
			NWCGException e = new NWCGException(
					"NWCG_CACHE_TRANSFER_BACKORDER_000");
			throw e;
		}

		validateLines(env, inDoc);

		NodeList inputDocOrderLineList = inputDocElm
				.getElementsByTagName(NWCGConstants.ORDER_LINE);
		for (int i = 0; i < inputDocOrderLineList.getLength(); i++) {
			Element currOrderLineFromUI = (Element) inputDocOrderLineList
					.item(i);

			String currOLKey = currOrderLineFromUI
					.getAttribute(NWCGConstants.ORDER_LINE_KEY);
			Document getOrderLineDtlsOpDoc = getOrderLineDetails(env, currOLKey);
			Element getOrderLineDtlsDocElm = getOrderLineDtlsOpDoc
					.getDocumentElement();
			String currInputOrderHeaderKey = getOrderLineDtlsDocElm
					.getAttribute(NWCGConstants.ORDER_HEADER_KEY);

			// Create the order header using the details from the first line as
			// the template
			if (i == 0) {
				TempOrderHeaderKey = currInputOrderHeaderKey;

				Element changeOrderDocElm = changeOrderInput
						.getDocumentElement();
				changeOrderDocElm.setAttribute(NWCGConstants.OVERRIDE,
						NWCGConstants.YES);
				changeOrderDocElm.setAttribute(NWCGConstants.ORDER_HEADER_KEY,
						currInputOrderHeaderKey);

				Element orderLines = changeOrderInput
						.createElement(NWCGConstants.ORDER_LINES);
				changeOrderDocElm.appendChild(orderLines);
				createOrderInput = createOrderHeader(env,
						getOrderLineDtlsDocElm);
			}

			// Add the line to the createOrder/createOrderHeader Doc API Input
			// XML
			addCacheTransferLine(createOrderInput, currOrderLineFromUI,
					getOrderLineDtlsDocElm, i + 1);
			changeOrderInput = changeBackOrderStatusInOriginalIssue(env,
					currOrderLineFromUI, getOrderLineDtlsDocElm,
					changeOrderInput);
		}

		returnDoc = CommonUtilities.invokeAPI(env,
				NWCGConstants.API_CHANGE_ORDER, changeOrderInput);

		// Create Cache Transfer Order
		Document createOrderTemplate = XMLUtil
				.createDocument(NWCGConstants.ORDER_ELM);
		createOrderTemplate.getDocumentElement().setAttribute(
				NWCGConstants.ORDER_HEADER_KEY, "");

		returnDoc = CommonUtilities.invokeAPI(env, createOrderTemplate,
				NWCGConstants.API_CREATE_ORDER, createOrderInput);
		return returnDoc;
	}

	private void validateLines(YFSEnvironment env, Document inDoc)
			throws Exception {
		String primaryOrderIdentifier = NWCGConstants.PRIMARY_IDENTIFIER_NULL;
		NodeList inputDocOrderLineList = inDoc.getDocumentElement()
				.getElementsByTagName(NWCGConstants.ORDER_LINE);

		for (int i = 0; i < inputDocOrderLineList.getLength(); i++) {
			Element ol = (Element) inputDocOrderLineList.item(i);
			String tempOrderIdentifier = ol
					.getAttribute(NWCGConstants.INCIDENT_NO_ATTR);
			if (StringUtil.isEmpty(tempOrderIdentifier))
				tempOrderIdentifier = NWCGConstants.PRIMARY_IDENTIFIER_NULL;

			if (i == 0) {
				// for the first line assign the identifier
				primaryOrderIdentifier = tempOrderIdentifier;
			} else {
				// Check if the identifiers are not the same and throw an
				// exception
				if (!primaryOrderIdentifier.equals(tempOrderIdentifier)) {
					throw new NWCGException("NWCG_CACHE_TRANSFER_BACKORDER_002");
				}
			}

			String newIssueQty = ol.getAttribute("NewIssueQty");
			if (StringUtil.isEmpty(newIssueQty)) {
				throw new NWCGException("NWCG_CACHE_TRANSFER_BACKORDER_003");
			} else {
				// Check if we can parse it to a Double, else throw an exception
				try {
					Double.parseDouble(newIssueQty);
				} catch (NumberFormatException ne) {
					throw new NWCGException(
							"NWCG_CACHE_TRANSFER_BACKORDER_004",
							new String[] { newIssueQty });
				}
			}
		}
	}

	private Document createOrderHeader(YFSEnvironment env, Element orderLine)
			throws Exception {
		Document createOrderInput = XMLUtil
				.createDocument(NWCGConstants.ORDER_ELM);
		Element createOrderInputDocElm = createOrderInput.getDocumentElement();

		Element olOrdElem = (Element) XMLUtil.getChildNodeByName(orderLine,
				NWCGConstants.ORDER_ELM);
		createOrderInputDocElm.setAttribute(NWCGConstants.DRAFT_ORDER_FLAG,
				NWCGConstants.YES);// set the draft order flag
		createOrderInputDocElm.setAttribute(NWCGConstants.ENTERPRISE_CODE_STR,
				olOrdElem.getAttribute(NWCGConstants.ENTERPRISE_CODE_STR));
		createOrderInputDocElm.setAttribute(
				NWCGConstants.SELLER_ORGANIZATION_CODE,
				olOrdElem.getAttribute(NWCGConstants.SELLER_ORGANIZATION_CODE));
		createOrderInputDocElm.setAttribute("BillToID",
				olOrdElem.getAttribute("BillToID"));
		createOrderInputDocElm.setAttribute(NWCGConstants.DOCUMENT_TYPE,
				olOrdElem.getAttribute(NWCGConstants.DOCUMENT_TYPE));
		createOrderInputDocElm.setAttribute(NWCGConstants.ORDER_TYPE,
				NWCGConstants.OB_ORDER_TYPE_BACKORDERED);
		createOrderInputDocElm.setAttribute(NWCGConstants.SHIP_NODE,
				orderLine.getAttribute(NWCGConstants.SHIP_NODE));// not found in
																	// getOrderLineDetails
																	// op xml
		createOrderInputDocElm.setAttribute(NWCGConstants.RECEIVING_NODE,
				orderLine.getAttribute(NWCGConstants.RECEIVING_NODE));
		createOrderInputDocElm.setAttribute(NWCGConstants.REQ_SHIP_DATE_ATTR,
				olOrdElem.getAttribute(NWCGConstants.REQ_SHIP_DATE_ATTR));
		createOrderInputDocElm.setAttribute(
				NWCGConstants.REQ_DELIVERY_DATE_ATTR,
				olOrdElem.getAttribute(NWCGConstants.REQ_DELIVERY_DATE_ATTR));
		createOrderInputDocElm.setAttribute(NWCGConstants.REQ_CANCEL_DATE_ATTR,
				olOrdElem.getAttribute(NWCGConstants.REQ_CANCEL_DATE_ATTR));
		createOrderInputDocElm.setAttribute(NWCGConstants.SCAC_AND_SERVICE_KEY,
				olOrdElem.getAttribute(NWCGConstants.SCAC_AND_SERVICE_KEY));

		SimpleDateFormat formatDate = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");
		Calendar currentDate = Calendar.getInstance();

		Element orderDatesElement = createOrderInput
				.createElement("OrderDates");
		Element orderDateElement = createOrderInput.createElement("OrderDate");
		orderDatesElement.appendChild(orderDateElement);
		createOrderInputDocElm.appendChild(orderDatesElement);
		orderDateElement.setAttribute("ExpectedDate",
				formatDate.format(currentDate.getTime()));

		// Set OH/Extn by importing it from the
		// getOrderLineDetails:/OrderLine/Order element
		Node olOrderExtnNode = XMLUtil.getChildNodeByName(olOrdElem,
				NWCGConstants.EXTN_ELEMENT);

		// Import the getOrderLineDetails:/OrdeLine/Order/Extn node into our
		// createOrder input doc
		olOrderExtnNode = createOrderInput.importNode(olOrderExtnNode, true);
		createOrderInputDocElm.appendChild(olOrderExtnNode);

		Element newOrderPersonInfoShipTo = createOrderInput
				.createElement("PersonInfoShipTo");
		Element newOrderPersonInfoBillTo = createOrderInput
				.createElement("PersonInfoBillTo");
		createOrderInputDocElm.appendChild(newOrderPersonInfoShipTo);
		createOrderInputDocElm.appendChild(newOrderPersonInfoBillTo);

		Element olShipToElem = (Element) XMLUtil.getChildNodeByName(orderLine,
				"PersonInfoShipTo");
		if (olShipToElem != null) {
			copyAddress(olShipToElem, newOrderPersonInfoShipTo);
			copyAddress(olShipToElem, newOrderPersonInfoBillTo);
		} else {
			Document inDocOrgList = XMLUtil.newDocument();
			String orgListTemplate = "<OrganizationList><Organization OrganizationCode=\"\"><BillingPersonInfo/></Organization></OrganizationList>";

			Element organizationElement = inDocOrgList
					.createElement("Organization");
			inDocOrgList.appendChild(organizationElement);
			organizationElement.setAttribute("OrganizationCode",
					orderLine.getAttribute(NWCGConstants.RECEIVING_NODE));

			Document outDocOrgList = CommonUtilities.invokeAPI(env,
					orgListTemplate, "getOrganizationList", inDocOrgList);
			Element billingPersonInfoElement = (Element) outDocOrgList
					.getElementsByTagName("BillingPersonInfo").item(0);
			if (billingPersonInfoElement != null) {
				copyAddress(billingPersonInfoElement, newOrderPersonInfoShipTo);
				copyAddress(billingPersonInfoElement, newOrderPersonInfoBillTo);
			}
		}

		newOrderPersonInfoShipTo = removeUnneededAddressFieldsFromPersonInfoElement(newOrderPersonInfoShipTo);
		newOrderPersonInfoBillTo = removeUnneededAddressFieldsFromPersonInfoElement(newOrderPersonInfoBillTo);

		Element orderLines = createOrderInput
				.createElement(NWCGConstants.ORDER_LINES);
		createOrderInputDocElm.appendChild(orderLines);

		return createOrderInput;
	}

	private Document addCacheTransferLine(Document createOrderInputDoc,
			Element inOrderLine, Element origOrderLine, int lineCounter)
			throws Exception {
		Element orderLines = (Element) XMLUtil.getChildNodeByName(
				createOrderInputDoc.getDocumentElement(),
				NWCGConstants.ORDER_LINES);
		Element orderLine = (Element) createOrderInputDoc.importNode(
				origOrderLine, true);
		orderLines.appendChild(orderLine);

		// Remove the OrderHeaderKey from the orderline element
		orderLine.removeAttribute(NWCGConstants.ORDER_HEADER_KEY);

		// Remove the order and extn elements from the orderline (on the
		// getOrderLineList OP Doc)
		orderLine.removeChild(XMLUtil.getChildNodeByName(orderLine,
				NWCGConstants.ORDER_ELM));

		// Get the input issue quantity
		String newIssueQty = inOrderLine.getAttribute("NewIssueQty");

		// if the issue quantity is not entered throw back a NWCGException
		if (StringUtil.isEmpty(newIssueQty)) {
			throw new NWCGException("NWCG_CACHE_TRANSFER_BACKORDER_003");
		}
		// Otherwise process the order line
		// Set the OL/OrderLineTranQuantity/@OrderedQty on the new line to the
		// newIssueQty variable
		Element olTranQtyElm = createOrderInputDoc
				.createElement(NWCGConstants.ORDER_LINE_TRAN_QTY_ELM);

		orderLine.appendChild(olTranQtyElm);
		olTranQtyElm.setAttribute(NWCGConstants.ORDERED_QTY, newIssueQty);

		// As we have already imported the orig orderline we can get the request
		// number from there
		Element orderLineExtn = (Element) XMLUtil.getChildNodeByName(orderLine,
				NWCGConstants.EXTN_ELEMENT);
		Element origOrderLineExtn = (Element) XMLUtil.getChildNodeByName(
				origOrderLine, NWCGConstants.EXTN_ELEMENT);

		orderLineExtn.setAttribute(NWCGConstants.EXTN_BACKORDER_QTY, "0");
		orderLineExtn.setAttribute("ExtnOrigReqQty", newIssueQty);// set the
																	// RequestedQty
		orderLineExtn.setAttribute("ExtnBackOrderFlag", NWCGConstants.NO);// set
																			// the
																			// back
																			// order
																			// flag
																			// to
																			// Y

		// Set the Order/Extn/@ExtnShippingContactName&Phone from the original
		// OrderLine/Order/Extn element
		orderLineExtn.setAttribute(NWCGConstants.SHIP_CONTACT_NAME_ATTR,
				origOrderLineExtn
						.getAttribute(NWCGConstants.SHIP_CONTACT_PHONE_ATTR));
		orderLineExtn.setAttribute(NWCGConstants.SHIP_CONTACT_PHONE_ATTR,
				origOrderLineExtn
						.getAttribute(NWCGConstants.SHIP_CONTACT_PHONE_ATTR));

		String requestNo = orderLineExtn
				.getAttribute(NWCGConstants.EXTN_REQUEST_NO);

		if (!StringUtil.isEmpty(requestNo)) {
			StringBuffer sb = new StringBuffer(requestNo);
			sb.append(NWCGConstants.REQUEST_NO_EXTN_DELIM);
			sb.append(new String("" + lineCounter));
			orderLineExtn.setAttribute(NWCGConstants.EXTN_REQUEST_NO,
					sb.toString());
		}
		return createOrderInputDoc;
	}

	private Document changeBackOrderStatusInOriginalIssue(YFSEnvironment env,
			Element inpOrderLine, Element origOrderLine,
			Document changeOrderInput) throws Exception {
		String origOLorderHeaderKey = origOrderLine
				.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		Element origOrderLineExtn = (Element) XMLUtil.getChildNodeByName(
				origOrderLine, NWCGConstants.EXTN_ELEMENT);
		String origBackOrderQty = origOrderLineExtn
				.getAttribute(NWCGConstants.EXTN_BACKORDER_QTY);
		Double dblOrigBackOrderQty = Double.parseDouble(origBackOrderQty);

		String newIssueQty = inpOrderLine.getAttribute("NewIssueQty");
		Double dblNewIssueQty = Double.parseDouble(newIssueQty);

		if (origOLorderHeaderKey.equals(TempOrderHeaderKey)) {
			Element order = changeOrderInput.getDocumentElement();
			Element orderLines = (Element) order.getElementsByTagName(
					NWCGConstants.ORDER_LINES).item(0);
			Element orderLine = changeOrderInput
					.createElement(NWCGConstants.ORDER_LINE);
			orderLines.appendChild(orderLine);
			orderLine.setAttribute(NWCGConstants.ORDER_LINE_KEY,
					inpOrderLine.getAttribute(NWCGConstants.ORDER_LINE_KEY));
			Element orderLineExtn = changeOrderInput
					.createElement(NWCGConstants.EXTN_ELEMENT);
			orderLine.appendChild(orderLineExtn);

			// Reset the backorder flag to N
			if ((dblOrigBackOrderQty - dblNewIssueQty) <= 0) {
				orderLineExtn.setAttribute(NWCGConstants.EXTN_BACKORDER_FLAG,
						NWCGConstants.NO);
				orderLineExtn.setAttribute(NWCGConstants.EXTN_BACKORDER_QTY,
						"0");
			} else {
				orderLineExtn.setAttribute(NWCGConstants.EXTN_BACKORDER_QTY,
						new Double(dblOrigBackOrderQty - dblNewIssueQty)
								.toString());
			}
			// orderLineExtn.setAttribute(NWCGConstants.EXTN_NAV_INFO_ATTR,
			// NWCGConstants.SHIPPING_ADDRESS);
		} else {
			// API call to changeOrder
			CommonUtilities.invokeAPI(env, NWCGConstants.API_CHANGE_ORDER,
					changeOrderInput);

			changeOrderInput = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
			Element order = changeOrderInput.getDocumentElement();
			order.setAttribute(NWCGConstants.OVERRIDE, NWCGConstants.YES);
			order.setAttribute(NWCGConstants.ORDER_HEADER_KEY,
					origOrderLine.getAttribute(NWCGConstants.ORDER_HEADER_KEY));
			Element orderLines = changeOrderInput
					.createElement(NWCGConstants.ORDER_LINES);
			order.appendChild(orderLines);
			Element orderLine = changeOrderInput
					.createElement(NWCGConstants.ORDER_LINE);
			orderLines.appendChild(orderLine);
			orderLine.setAttribute(NWCGConstants.ORDER_LINE_KEY,
					inpOrderLine.getAttribute(NWCGConstants.ORDER_LINE_KEY));
			Element orderLineExtn = changeOrderInput
					.createElement(NWCGConstants.EXTN_ELEMENT);
			orderLine.appendChild(orderLineExtn);

			// Reset the backorder flag to N
			if ((dblOrigBackOrderQty - dblNewIssueQty) <= 0) {
				orderLineExtn.setAttribute(NWCGConstants.EXTN_BACKORDER_FLAG,
						NWCGConstants.NO);
				orderLineExtn.setAttribute(NWCGConstants.EXTN_BACKORDER_QTY,
						"0");
			} else {
				orderLineExtn.setAttribute(NWCGConstants.EXTN_BACKORDER_QTY,
						new Double(dblOrigBackOrderQty - dblNewIssueQty)
								.toString());
			}
			orderLineExtn.setAttribute(NWCGConstants.EXTN_NAV_INFO_ATTR,
					NWCGConstants.SHIPPING_ADDRESS);
		}

		TempOrderHeaderKey = origOLorderHeaderKey;
		return changeOrderInput;
	}

	private Document getOrderLineDetails(YFSEnvironment env, String orderLineKey)
			throws Exception {

		Document getOrderLineDetailsInput = XMLUtil
				.createDocument("OrderLineDetail");
		getOrderLineDetailsInput.getDocumentElement().setAttribute(
				NWCGConstants.ORDER_LINE_KEY, orderLineKey);
		env.setApiTemplate(NWCGConstants.API_GET_ORDER_LINE_DETAILS,
				"NWCGCreateCacheTransferFromBackOrderLines_getOrderLineDetails");
		Document returnDoc = CommonUtilities.invokeAPI(env,
				NWCGConstants.API_GET_ORDER_LINE_DETAILS,
				getOrderLineDetailsInput);
		env.clearApiTemplate(NWCGConstants.API_GET_ORDER_LINE_DETAILS);

		return returnDoc;
	}

	private void copyAddress(Element from, Element to) {
		NamedNodeMap attrMap = from.getAttributes();
		for (int i = 0; i < attrMap.getLength(); i++) {
			Attr attr = (Attr) attrMap.item(i);
			// Get the binding key from the template
			String name = attr.getName();
			String value = attr.getValue();
			to.setAttribute(name, value);
		}
	}

	private Element removeUnneededAddressFieldsFromPersonInfoElement(
			Element aPersonInfoElm) {
		aPersonInfoElm.removeAttribute("Createprogid");
		aPersonInfoElm.removeAttribute("Createts");
		aPersonInfoElm.removeAttribute("Createuserid");
		aPersonInfoElm.removeAttribute("Modifyprogid");
		aPersonInfoElm.removeAttribute("Modifyts");
		aPersonInfoElm.removeAttribute("Modifyuserid");
		// aPersonInfoElm.removeAttribute("PersonInfoKey");
		aPersonInfoElm.removeAttribute("UseCount");
		aPersonInfoElm.removeAttribute("VerificationStatus");
		// aPersonInfoElm.removeAttribute("PersonID");
		aPersonInfoElm.removeAttribute("isHistory");
		// aPersonInfoElm.removeAttribute("PreferredShipAddress");
		aPersonInfoElm.removeAttribute("Lockid");
		return aPersonInfoElm;
	}

	public void setProperties(Properties props) throws Exception {
	}

	private String TempOrderHeaderKey = "";
}