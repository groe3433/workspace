/**
 * 
 */
package com.nwcg.icbs.yantra.api.incident;

import java.util.Properties;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
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

/**
 * @author jkim
 * 
 */
public class NWCGCancelBOFromBackOrderLines implements YIFCustomApi {
	String TempOrderHeaderKey = "";

	/**
	 * Logger Instance.
	 */
	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGCancelBOFromBackOrderLines.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yantra.interop.japi.YIFCustomApi#setProperties(java.util.Properties)
	 */
	public void setProperties(Properties props) throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * @param env
	 * @param inDoc
	 *            The orderlines with the backorder qty
	 * @return Document The createOrder output after the issue has been created
	 * @throws Exception
	 */
	public Document cancelBOFromBackOrderLines(YFSEnvironment env,
			Document inDoc) throws Exception {
		if (logger.isVerboseEnabled())
			logger.verbose("Entering cancelBOFromBackOrderLines()->\n"
					+ XMLUtil.getXMLString(inDoc));
		// Build a create order
		Document changeOrderInput = XMLUtil.createDocument("Order");
		Document createIssueInput = null;
		Document output_doc = null;
		Document returnDoc = null;

		// validate the lines before processing. if the input is not valid, then
		// throw exception
		validateLines(env, inDoc);

		NodeList inpOlList = inDoc.getDocumentElement().getElementsByTagName(
				"OrderLine");
		// process one OrderLine at a time
		for (int i = 0; i < inpOlList.getLength(); i++) {
			String BFlag = "";
			Element ol = (Element) inpOlList.item(i);
			String olKey = ol.getAttribute("OrderLineKey");
			if (logger.isVerboseEnabled())
				logger.verbose("orderLineKey=[" + olKey + "]");
			Document orderLineDoc = getOrderLineDetails(env, olKey);
			if (logger.isVerboseEnabled())
				logger.verbose("OutPut getOrderLineDetails()->\n"
						+ XMLUtil.getXMLString(orderLineDoc));

			Element orderLine = orderLineDoc.getDocumentElement();
			String OrderHeaderKey = orderLine.getAttribute("OrderHeaderKey");
			// create the order header using the details from the first line as
			// the template
			if (i == 0) {
				TempOrderHeaderKey = OrderHeaderKey;
				Element order = changeOrderInput.getDocumentElement();
				order.setAttribute("Override", "Y");
				order.setAttribute("OrderHeaderKey", OrderHeaderKey);
				Element orderlines = changeOrderInput
						.createElement("OrderLines");
				order.appendChild(orderlines);
				createIssueInput = createIssueHeader(env, orderLine); // new
																		// Issue
																		// header
																		// is
																		// created
			}
			// add the line to the create order input
			output_doc = addLineToIssue(createIssueInput, ol, orderLine);
			changeOrderInput = changeBackOrderStatusInOriginalIssue(env, ol,
					orderLine, changeOrderInput);
			if (logger.isVerboseEnabled())
				logger.verbose("ChangeOrder API Input XML ->\n"
						+ XMLUtil.getXMLString(changeOrderInput));
			returnDoc = CommonUtilities.invokeAPI(env, "changeOrder",
					changeOrderInput);
		} // --- for loop ---//

		if (logger.isVerboseEnabled())
			logger.verbose("cancelBOFromBackOrderLines()->Create Order API Input XML ->\n"
					+ XMLUtil.getXMLString(createIssueInput));

		// -- 1. create the issue --//
		Document createOrderTemplate = XMLUtil.createDocument("Order");
		createOrderTemplate.getDocumentElement().setAttribute("OrderHeaderKey",
				"");
		returnDoc = CommonUtilities.invokeAPI(env, createOrderTemplate,
				"createOrder", createIssueInput);

		if (logger.isVerboseEnabled())
			logger.verbose("After CreateOrderAPI OutPut XML ->\n"
					+ XMLUtil.getXMLString(returnDoc));

		// extract newly created orderHeaderKey
		String createdOrderHeaderKey = returnDoc.getDocumentElement()
				.getAttribute("OrderHeaderKey");
		if (logger.isVerboseEnabled())
			logger.verbose("createdOrderHeaderKey=[" + createdOrderHeaderKey
					+ "]");

		// -- 2. confirm the issue --//
		// this is all done behind the scene
		// <?xml version="1.0" encoding="UTF-8"?>
		// <ConfirmDraftOrder IgnoreOrdering="Y"
		// OrderHeaderKey="200811071118071279097"/>
		// --> set up all the attributes
		Document confirmDraftOrderInput = XMLUtil
				.createDocument("ConfirmDraftOrder");
		Element elemconfirmDraftOrderInput = confirmDraftOrderInput
				.getDocumentElement();
		elemconfirmDraftOrderInput.setAttribute("IgnoreOrdering", "Y");
		elemconfirmDraftOrderInput.setAttribute("OrderHeaderKey",
				createdOrderHeaderKey);
		if (logger.isVerboseEnabled())
			logger.verbose("cancelBOFromBackOrderLines()->confirmDraftOrder API Input XML ->\n"
					+ XMLUtil.getXMLString(confirmDraftOrderInput));
		returnDoc = CommonUtilities.invokeAPI(env, "confirmDraftOrder",
				confirmDraftOrderInput);

		// -- 3. cancel the issue --//
		// by calling changeOrder API with action="CANCEL"
		// this is all done behind the scene
		// <?xml version="1.0" encoding="UTF-8"?>
		// <Order Action="CANCEL" IgnoreOrdering="Y"
		// ModificationReasonCode="CUSTOMERCHANGE" ModificationReasonText=""
		// OrderHeaderKey="200811071118071279097" Override="Y"/>
		Document chgordinput = XMLUtil.createDocument("Order");
		Element elemchgordinput = chgordinput.getDocumentElement();
		elemchgordinput.setAttribute("Action", "CANCEL");
		elemchgordinput.setAttribute("IgnoreOrdering", "Y");
		elemchgordinput
				.setAttribute("ModificationReasonCode", "CUSTOMERCHANGE");
		elemchgordinput.setAttribute("Override", "Y");
		elemchgordinput.setAttribute("OrderHeaderKey", createdOrderHeaderKey);
		if (logger.isVerboseEnabled())
			logger.verbose("ChangeOrder API Input XML for Cancel ->\n"
					+ XMLUtil.getXMLString(chgordinput));
		returnDoc = CommonUtilities.invokeAPI(env, "changeOrder", chgordinput);

		if (logger.isVerboseEnabled())
			logger.verbose("Exiting cancelBOFromBackOrderLines() Output XML ->\n"
					+ XMLUtil.getXMLString(returnDoc));
		return returnDoc;
	}

	/*
	 * This method validates the input from the JSP if the selected lines belong
	 * to different order/incidents then we throw an exception if the issue
	 * quantity for the new lines is not entered or not a number we throw and
	 * exception
	 */
	private void validateLines(YFSEnvironment env, Document inDoc)
			throws Exception {
		if (logger.isVerboseEnabled())
			logger.verbose("in validateLines() ->\n"
					+ XMLUtil.getXMLString(inDoc));
		String primaryOrderIdentifier = NWCGConstants.PRIMARY_IDENTIFIER_NULL;
		String primaryIncidentYear = NWCGConstants.PRIMARY_IDENTIFIER_NULL;
		NodeList inpOlList = inDoc.getDocumentElement().getElementsByTagName(
				"OrderLine");

		for (int i = 0; i < inpOlList.getLength(); i++) {
			Element ol = (Element) inpOlList.item(i);
			// ---[Check the primary order identifier - IncidentNo+Year or
			// IncidentNo]-----//
			String tempOrderIdentifier = ol.getAttribute("IncidentNo");
			String tempIncidentYear = ol.getAttribute("IncidentYear");
			if (logger.isVerboseEnabled())
				logger.verbose("incidentNo=[" + tempOrderIdentifier
						+ "] tempIncidentYear=[" + tempIncidentYear + "]");
			if (StringUtil.isEmpty(tempOrderIdentifier))
				tempOrderIdentifier = NWCGConstants.PRIMARY_IDENTIFIER_NULL;

			// check if the IncidentNo/Year or Other Order No are the same for
			// all passed order lines
			if (!StringUtil.isEmpty(tempIncidentYear)) { // Incident Issue cases
				if (i == 0) {
					// for the first line assign the identifier
					primaryOrderIdentifier = tempOrderIdentifier; // save the
																	// first
																	// incidentNo
					primaryIncidentYear = tempIncidentYear; // save the first
															// incidentYear
				} else {
					// Check if the identifiers are not the same and if they
					// differ, throw an exception
					if (!primaryOrderIdentifier.equals(tempOrderIdentifier)
							|| !primaryIncidentYear.equals(tempIncidentYear))
						throw new NWCGException("NWCG_ISSUE_BACKORDER_002");
				}
			} else { // Other Issue cases
				if (i == 0) {
					// for the first line assign the identifier
					primaryOrderIdentifier = tempOrderIdentifier;
				} else {
					// Check if the identifiers are not the same and throw and
					// exception
					if (!primaryOrderIdentifier.equals(tempOrderIdentifier)) {
						throw new NWCGException("NWCG_ISSUE_BACKORDER_002");
					}
				}
			}
			if (logger.isVerboseEnabled())
				logger.verbose("primaryOrderIdentifier=["
						+ primaryOrderIdentifier + "] primaryIncidentYear=["
						+ primaryIncidentYear + "]");

			// ---[end check primary identifier]------//

			// ---[check for issue quantity]-----//
			String newIssueQty = ol.getAttribute("NewIssueQty");
			if (StringUtil.isEmpty(newIssueQty)) {
				throw new NWCGException("NWCG_ISSUE_BACKORDER_003");
			} else {
				// check if we can parse it to a number else throw and exception
				try {
					Double.parseDouble(newIssueQty);
				} catch (NumberFormatException ne) {
					throw new NWCGException("NWCG_ISSUE_BACKORDER_004",
							new String[] { newIssueQty });
				}
			} 
		} 
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
	private Document createIssueHeader(YFSEnvironment env, Element orderLine)
			throws Exception {
		Document createIssueInput = XMLUtil.createDocument("Order");
		Element order = createIssueInput.getDocumentElement();
		// ----[set the order headers]----//
		Element olOrdElem = (Element) XMLUtil.getChildNodeByName(orderLine,
				"Order");
		order.setAttribute("DraftOrderFlag", "Y");// set the draft order flag
		order.setAttribute("EnterpriseCode",
				olOrdElem.getAttribute("EnterpriseCode"));
		order.setAttribute("SellerOrganizationCode",
				olOrdElem.getAttribute("SellerOrganizationCode"));
		order.setAttribute("BillToID", olOrdElem.getAttribute("BillToID"));
		order.setAttribute("DocumentType",
				olOrdElem.getAttribute("DocumentType"));
		order.setAttribute("OrderType", "Backordered");
		order.setAttribute("ShipNode", olOrdElem.getAttribute("ShipNode"));
		// ---[end set headers]--//

		Element olOrdExtnElem = (Element) XMLUtil.getChildNodeByName(olOrdElem,
				"Extn");
		String incidentNo = olOrdExtnElem.getAttribute("ExtnIncidentNo");
		String year = olOrdExtnElem.getAttribute("ExtnIncidentYear");
		logger.verbose("Incident No:" + incidentNo);
		logger.verbose("Incident year:" + year);
		if (!StringUtil.isEmpty(incidentNo)) {
			// -----------[add the incident details]---------------//
			addIncidentDetails(env, createIssueInput, incidentNo, year);
		}
		// Add the orderLines element
		Element orderLines = createIssueInput.createElement("OrderLines");
		order.appendChild(orderLines);
		if (logger.isVerboseEnabled())
			logger.verbose("Exiting createIssueHeader()->\n"
					+ XMLUtil.getXMLString(createIssueInput));
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
	private void addIncidentDetails(YFSEnvironment env,
			Document createIssueInput, String incidentNo, String year)
			throws Exception {
		Element order = createIssueInput.getDocumentElement();
		Element orderExtn = createIssueInput.createElement("Extn");
		order.appendChild(orderExtn);

		Document getIncidentOrderInput = XMLUtil
				.createDocument("NWCGIncidentOrder");
		getIncidentOrderInput.getDocumentElement().setAttribute("IncidentNo",
				incidentNo);
		if (year.length() == 0)
			year = " ";
		getIncidentOrderInput.getDocumentElement().setAttribute("Year", year);
		Document incidentDetails = CommonUtilities.invokeService(env,
				NWCGConstants.NWCG_GET_INCIDENT_ORDER_SERVICE,
				getIncidentOrderInput);
		// if we do not get any incident details throw an exception
		if (null == incidentDetails) {
			NWCGException e = new NWCGException("NWCG_ISSUE_BACKORDER_001",
					new String[] { incidentNo });
			throw e;
		}

		Element incident = incidentDetails.getDocumentElement();

		// copy the following attributes from the NWCGIncidentOrder node into
		// Order node
		order.setAttribute("ShipNode", incident.getAttribute("PrimaryCacheId"));

		// copy for more customer detail info at Order/Extn level here ///
		orderExtn.setAttribute("ExtnUnitType",
				incident.getAttribute("UnitType"));
		orderExtn.setAttribute("ExtnDepartment",
				incident.getAttribute("Department"));
		orderExtn.setAttribute("ExtnAgency", incident.getAttribute("Agency"));
		orderExtn.setAttribute("ExtnGACC", incident.getAttribute("GACC"));

		// copy the following attributes from the
		// ExtnIncidentNo,ExtnIncidentYear,ExtnIncidentName,ExtnFsAcctCode,ExtnBlmAcctCode,ExtnOtherAcctCode,
		// ExtnOverrideCode,ExtnIncidentTeamType,ExtnIncidentType
		orderExtn.setAttribute("ExtnIncidentNo",
				incident.getAttribute("IncidentNo"));
		orderExtn.setAttribute("ExtnIncidentYear",
				incident.getAttribute("Year"));
		orderExtn.setAttribute("ExtnIncidentName",
				incident.getAttribute("IncidentName"));
		orderExtn.setAttribute("ExtnFsAcctCode",
				incident.getAttribute("IncidentFsAcctCode"));
		orderExtn.setAttribute("ExtnBlmAcctCode",
				incident.getAttribute("IncidentBlmAcctCode"));
		orderExtn.setAttribute("ExtnOtherAcctCode",
				incident.getAttribute("IncidentOtherAcctCode"));
		orderExtn.setAttribute("ExtnOverrideCode",
				incident.getAttribute("OverrideCode"));

		// added the below three attributes
		orderExtn.setAttribute("ExtnCustomerName",
				incident.getAttribute("CustomerName"));
		orderExtn.setAttribute("ExtnShipAcctCode",
				incident.getAttribute("IncidentFsAcctCode"));
		orderExtn.setAttribute("ExtnSAOverrideCode",
				incident.getAttribute("OverrideCode"));

		orderExtn.setAttribute("ExtnIncidentTeamType",
				incident.getAttribute("IncidentTeamType"));
		orderExtn.setAttribute("ExtnIncidentType",
				incident.getAttribute("IncidentType"));
		orderExtn.setAttribute("ExtnPoNo",
				incident.getAttribute("CustomerPONo"));

		// --------[copy incident addresses]--------------//
		// shipto

		Element incidentShipToAdd = (Element) XMLUtil.getChildNodeByName(
				incident, "YFSPersonInfoShipTo");
		if (null != incidentShipToAdd) {
			Element personInfoShipTo = createIssueInput
					.createElement("PersonInfoShipTo");
			order.appendChild(personInfoShipTo);
			copyAddress(incidentShipToAdd, personInfoShipTo);
		}
		// billto
		Element incidentBillToAdd = (Element) XMLUtil.getChildNodeByName(
				incident, "YFSPersonInfoBillTo");
		if (null != incidentBillToAdd) {
			Element personInfoBillTo = createIssueInput
					.createElement("PersonInfoBillTo");
			order.appendChild(personInfoBillTo);
			copyAddress(incidentBillToAdd, personInfoBillTo);
		}
		// deliverto
		Element incidentDeliverTo = (Element) XMLUtil.getChildNodeByName(
				incident, "YFSPersonInfoDeliverTo");
		if (null != incidentDeliverTo) {
			Element additionalAddresses = createIssueInput
					.createElement("AdditionalAddresses");
			order.appendChild(additionalAddresses);
			Element additionalAddress = createIssueInput
					.createElement("AdditionalAddress");
			additionalAddresses.appendChild(additionalAddress);
			additionalAddress.setAttribute("AddressType",
					NWCGConstants.ADDRESS_TYPE_DELIVER);
			Element personInfo = createIssueInput.createElement("PersonInfo");
			additionalAddress.appendChild(personInfo);
			copyAddress(incidentDeliverTo, personInfo);
		}

	}

	// copy address attributes from from element to to element.
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
			// get the binding key from the template
			to.setAttribute(attr.getName(), attr.getValue());
		}
	}

	/**
	 * This method adds and orderline to the create issue input It gets the
	 * issue qty from the input orderline and adjusts it with the order qty
	 * while creating the new issue It also sets the backorder flag on the line
	 * if the issue qty cannot does not fully satisfy the backorder qty The
	 * S-Number or the request no. is also updated with increments/extensions
	 * eg. S_1 would become S_1.1 after first extension and S_1.2 after second
	 * extension and so on.
	 * 
	 * @param orderDoc
	 * @param inOrderLine
	 * @param origOrderLine
	 * @return Document
	 * @throws Exception
	 */
	private Document addLineToIssue(Document orderDoc, Element inOrderLine,
			Element origOrderLine) throws Exception {
		if (logger.isVerboseEnabled())
			logger.verbose("in addLineToIssue() orderDoc XML ->\n"
					+ XMLUtil.getXMLString(orderDoc));
		Element orderLines = (Element) XMLUtil.getChildNodeByName(
				orderDoc.getDocumentElement(), "OrderLines");
		Element orderLine = (Element) orderDoc.importNode(origOrderLine, true);
		orderLines.appendChild(orderLine);
		// remove the OrderHeaderKey from the orderline element
		orderLine.removeAttribute("OrderHeaderKey");
		// remove the order and extn elements from the orderline
		orderLine.removeChild(XMLUtil.getChildNodeByName(orderLine, "Order"));
		// get the input issue quantity
		String newIssueQty = inOrderLine.getAttribute("NewIssueQty");

		// if the issue quantity is not entered throw back and exception
		if (StringUtil.isEmpty(newIssueQty)) {
			throw new NWCGException("NWCG_ISSUE_BACKORDER_003");
		}
		// else we process the line
		// set the order qty on the new line to the input qty
		orderLine.setAttribute("OrderedQty", newIssueQty);

		// -----[The back order and quantity]--------//
		// As we have already imported the orig orderline we can get the request
		// number from there
		Element olExtn = (Element) XMLUtil
				.getChildNodeByName(orderLine, "Extn");
		Element origOlExtn = (Element) XMLUtil.getChildNodeByName(
				origOrderLine, "Extn");
		String origBackOrderQty = origOlExtn.getAttribute("ExtnBackorderedQty");
		// if neither strings are empty
		if (!StringUtil.isEmpty(newIssueQty)
				&& !StringUtil.isEmpty(origBackOrderQty)) {
			double newQty = Double.parseDouble(newIssueQty);
			double backQty = Double.parseDouble(origBackOrderQty);
			double delta = backQty - newQty;
			if (delta > 0 || delta == 0) { // set the difference as the back
											// order qty in the new order
				olExtn.setAttribute("ExtnBackorderedQty",
						Double.toString(delta));
				olExtn.setAttribute("ExtnOrigReqQty", origBackOrderQty);// set
																		// the
																		// RequestedQty
				olExtn.setAttribute("ExtnBackOrderFlag", "Y");// set the back
																// order flag to
																// Y
				if (delta == 0) { // if entire backorderedqty is consumed, i.e.
									// become issue qty
					olExtn.setAttribute("ExtnBackOrderFlag", "N");// set the
																	// back
																	// order
																	// flag to N
				}
			}
		}

		// -----[ The s Number assignment]----//
		String requestNo = olExtn.getAttribute("ExtnRequestNo");
		if (!StringUtil.isEmpty(requestNo)) {
			String parts[] = requestNo.split("\\"
					+ NWCGConstants.REQUEST_NO_EXTN_DELIM);
			int revision = 1;// default revision
			if (parts.length == 2) {// if already has an earlier revision
				int currentRev = Integer.parseInt(parts[1]);
				revision = currentRev + 1;
			}
			requestNo = parts[0] + NWCGConstants.REQUEST_NO_EXTN_DELIM
					+ revision;
			olExtn.setAttribute("ExtnRequestNo", requestNo);
		}
		if (logger.isVerboseEnabled())
			logger.verbose("Exiting addLineToIssue() orderDoc XML ->\n"
					+ XMLUtil.getXMLString(orderDoc));
		return orderDoc;
	}

	/**
	 * This method removes the backorder status from the original line as all
	 * the backorder qty is already transfered to the new issue
	 * 
	 * @param env
	 * @param inpOrderLine
	 * @param origOrderLine
	 * @return
	 * @throws Exception
	 */
	private Document changeBackOrderStatusInOriginalIssue(YFSEnvironment env,
			Element inpOrderLine, Element origOrderLine,
			Document changeOrderInput) throws Exception {

		if (logger.isVerboseEnabled())
			logger.verbose("in changeBackOrderStatusInOriginalIssue() ->\n"
					+ XMLUtil.getXMLString(changeOrderInput));
		String OrderHeaderKey = origOrderLine.getAttribute("OrderHeaderKey");
		Document returnDoc = null;
		if (OrderHeaderKey.equals(TempOrderHeaderKey)) {
			Element order = changeOrderInput.getDocumentElement();
			Element orderLines = (Element) order.getElementsByTagName(
					"OrderLines").item(0);
			Element orderLine = changeOrderInput.createElement("OrderLine");
			orderLines.appendChild(orderLine);
			orderLine.setAttribute("OrderLineKey",
					inpOrderLine.getAttribute("OrderLineKey"));
			Element orderLineExtn = changeOrderInput.createElement("Extn");
			orderLine.appendChild(orderLineExtn);
			// reset the backorder flag
			orderLineExtn.setAttribute("ExtnBackOrderFlag", "N");
		} else {
			returnDoc = CommonUtilities.invokeAPI(env, "changeOrder",
					changeOrderInput);
			changeOrderInput = XMLUtil.createDocument("Order");
			Element order = changeOrderInput.getDocumentElement();
			order.setAttribute("Override", "Y");
			order.setAttribute("OrderHeaderKey",
					origOrderLine.getAttribute("OrderHeaderKey"));
			Element orderLines = changeOrderInput.createElement("OrderLines");
			order.appendChild(orderLines);
			Element orderLine = changeOrderInput.createElement("OrderLine");
			orderLines.appendChild(orderLine);
			orderLine.setAttribute("OrderLineKey",
					inpOrderLine.getAttribute("OrderLineKey"));
			Element orderLineExtn = changeOrderInput.createElement("Extn");
			orderLine.appendChild(orderLineExtn);
			// reset the backorder flag
			orderLineExtn.setAttribute("ExtnBackOrderFlag", "N");
		}
		if (logger.isVerboseEnabled())
			logger.verbose("Exiting changeBackOrderQtyInOriginalIssue()-> ChangeOrderInput XML ->\n"
					+ XMLUtil.getXMLString(changeOrderInput));
		// Document returnDoc =
		// CommonUtilities.invokeAPI(env,"changeOrder",changeOrderInput);
		TempOrderHeaderKey = OrderHeaderKey;
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
	private Document getOrderLineDetails(YFSEnvironment env, String orderLineKey)
			throws Exception {
		Document getOrderLineDetailsInput = XMLUtil
				.createDocument("OrderLineDetail");
		getOrderLineDetailsInput.getDocumentElement().setAttribute(
				"OrderLineKey", orderLineKey);
		env.setApiTemplate("getOrderLineDetails",
				"NWCGCancelBOFromBackOrderLines_getOrderLineDetails");
		Document returnDoc = CommonUtilities.invokeAPI(env,
				"getOrderLineDetails", getOrderLineDetailsInput);
		env.clearApiTemplate("getOrderLineDetails");
		if (logger.isVerboseEnabled())
			logger.verbose("Exiting getOrderLineDetails()->\n"
					+ XMLUtil.getXMLString(returnDoc));
		return returnDoc;
	}
}
