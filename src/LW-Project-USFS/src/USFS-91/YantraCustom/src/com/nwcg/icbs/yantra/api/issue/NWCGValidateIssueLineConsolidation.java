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

package com.nwcg.icbs.yantra.api.issue;

import java.util.Properties;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.api.issue.util.NWCGValidateIssueLineUtil;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * Back end API invoked by SDF service for implementing and validating order
 * line consolidation requests from the front end user interface
 * 
 * @author drodriguez
 * @since Business Release 2 (BR2) Increment 4
 */
public class NWCGValidateIssueLineConsolidation implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGValidateIssueLineConsolidation.class);

	private Properties sdfApiArgs = null;
	private String orderHeaderKey = null;
	private String newReqNo = null;
	private String baseRequestNoAttr = "";
	private YFSEnvironment myEnvironment = null;

	public void setProperties(Properties sdfApiArgs) throws Exception {
		this.sdfApiArgs = sdfApiArgs;
	}

	/**
	 * Validates a consolidation request received from the front end Item
	 * Consolidation UI and calls changeOrder to add the new line and to cancel
	 * the lines selected for consolidation.
	 * 
	 * @param env
	 *            The standard YFSEnvironment object
	 * @param inputDoc
	 *            The request DOM passed by the UI from the Order namespace upon
	 *            hitting the Save button
	 * 
	 *            Example input:
	 * 
	 *            <Order IgnoreOrdering="Y"
	 *            OrderHeaderKey="201004141345074254482"> <OrderLines>
	 *            <OrderLine ReqShipDate="2010-04-14T00:00:00"> <Item
	 *            ItemID="000018" ProductClass="Supply"/> <Extn
	 *            ExtnBackOrderFlag="N" ExtnBackorderedQty="0"
	 *            ExtnRequestNo="S-041401.1"/> <OrderLineTranQuantity
	 *            OrderedQty="6.0" TransactionalUOM="BD"/> <Notes> <Note
	 *            NoteText="asasdfasdf"/> </Notes> </OrderLine> </OrderLines>
	 *            <OrderLinesToConsolidate> <OrderLine
	 *            OrderLineKey="201004141345074254483" YFC_NODE_NUMBER="1"/>
	 *            <OrderLine OrderLineKey="201004141345074254484"
	 *            YFC_NODE_NUMBER="2"/> <OrderLine
	 *            OrderLineKey="201004141345074254485" YFC_NODE_NUMBER="3"/>
	 *            </OrderLinesToConsolidate> </Order>
	 *
	 * @return Document The resulting output of the CommonUtilities.invokeAPI()
	 *         method. Not used by the caller.
	 * @throws Exception
	 */
	public Document validateConsolidationRequest(YFSEnvironment env, Document inputDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGValidateIssueLineConsolidation.validateConsolidationRequest @@@@@");
		logger.verbose("@@@@@ inDoc :: " + XMLUtil.getXMLString(inputDoc));
		this.myEnvironment = env;
		newReqNo = getNewRequestNumberFromInput(inputDoc);
		Node orderLinesToConsolidateNode = null;
		Element inputDocElm = inputDoc.getDocumentElement();
		orderHeaderKey = inputDocElm.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		NodeList orderLinesToConsolidateNL = inputDocElm.getElementsByTagName("OrderLinesToConsolidate");
		if (orderLinesToConsolidateNL.getLength() == 1) {
			orderLinesToConsolidateNode = orderLinesToConsolidateNL.item(0);
		} else {
			throw new YFSException("Unable to complete consolidation request!");
		}
		// publish unpublished item(s) for Ross initiated issues
		String systemOfOrigin = inputDocElm.getAttribute("ExtnSystemOfOrigin");
		logger.verbose("@@@@@ systemOfOrigin :: " + systemOfOrigin);
		if (systemOfOrigin != null && systemOfOrigin.equalsIgnoreCase("ROSS")) {
			NWCGValidateIssueLineUtil.publishUnpublishedItems(env, inputDoc);
		}
		Document secondChangeOrderDoc = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
		Element secondDocElm = secondChangeOrderDoc.getDocumentElement();
		secondDocElm.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHeaderKey);
		
		// BEGIN - CR 1693 - July 30, 2015
		String strNewShipToKey = "";
		String strNewShipToID = "";
		StringBuffer sbTemp_getOrderDetails = new StringBuffer("<Order ShipToKey=\"\" ShipToID=\"\"></Order>");
		String strTemp_getOrderDetails = sbTemp_getOrderDetails.toString();
		StringBuffer sbInput_getOrderDetails = new StringBuffer("<Order OrderHeaderKey=\"" + orderHeaderKey + "\"/>");
		String strInput_getOrderDetails = sbInput_getOrderDetails.toString();
		Document outDoc_getOrderDetails = CommonUtilities.invokeAPI(env, XMLUtil.getDocument(strTemp_getOrderDetails), "getOrderDetails", XMLUtil.getDocument(strInput_getOrderDetails));
		strNewShipToKey = XPathUtil.getString(outDoc_getOrderDetails.getDocumentElement(), "/Order/@ShipToKey");
		logger.verbose("@@@@@ strNewShipToKey :: " + strNewShipToKey);
		strNewShipToID = XPathUtil.getString(outDoc_getOrderDetails.getDocumentElement(), "/Order/@ShipToID");
		logger.verbose("@@@@@ strNewShipToID :: " + strNewShipToID);
		// END - CR 1693 - July 30, 2015
		
		secondDocElm.setAttribute("IgnoreOrdering", NWCGConstants.YES);
		Node orderLinesToConsolidateFromInputDoc = orderLinesToConsolidateNode;
		orderLinesToConsolidateNode = secondChangeOrderDoc.importNode(orderLinesToConsolidateNode, true);
		orderLinesToConsolidateNode = secondChangeOrderDoc.renameNode(orderLinesToConsolidateNode, null, NWCGConstants.ORDER_LINES);
		secondDocElm.appendChild(orderLinesToConsolidateNode);
		getChangeOrderOrderLinesIpForConsolidation(orderLinesToConsolidateNode);
		inputDocElm.removeChild(orderLinesToConsolidateFromInputDoc);
		NodeList newOrderLinesNL = inputDocElm.getElementsByTagName(NWCGConstants.ORDER_LINE);
		for (int i = 0; i < newOrderLinesNL.getLength(); i++) {
			Element currElm = (Element) newOrderLinesNL.item(i);
			
			// BEGIN - CR 1693 - July 30, 2015
			currElm.setAttribute("ShipToKey", strNewShipToKey);
			currElm.setAttribute("ShipToID", strNewShipToID);
			// END - CR 1693 - July 30, 2015
			
			if (!currElm.hasAttribute(NWCGConstants.CONDITION_VAR1))
				currElm.setAttribute(NWCGConstants.CONDITION_VAR1, NWCGConstants.ICBS_SYSTEM);
			Element elmExtn = (Element) currElm.getElementsByTagName(NWCGConstants.EXTN_ELEMENT).item(0);
			elmExtn.setAttribute(NWCGConstants.EXTN_BASE_REQUEST_NO, baseRequestNoAttr);
		}
		CommonUtilities.invokeAPI(myEnvironment, NWCGConstants.API_CHANGE_ORDER, inputDoc);
		Document docFinalReturn = CommonUtilities.invokeAPI(myEnvironment, NWCGConstants.API_CHANGE_ORDER, secondChangeOrderDoc);
		logger.verbose("@@@@@ Exiting NWCGValidateIssueLineConsolidation.validateConsolidationRequest @@@@@");
		return docFinalReturn;
	}

	private String getNewRequestNumberFromInput(Document inputDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGValidateIssueLineConsolidation.getNewRequestNumberFromInput @@@@@");
		String requestNo = "";
		Element orderLineExtn = XMLUtil.getFirstElementByName(inputDoc.getDocumentElement(), "OrderLines/OrderLine/Extn");
		requestNo = orderLineExtn.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
		logger.verbose("@@@@@ Exiting NWCGValidateIssueLineConsolidation.getNewRequestNumberFromInput @@@@@");
		return requestNo;
	}

	/**
	 * Calls getOrderLineList for each OrderLine element containing only an
	 * OrderLineKey attribute and replaces it with the output OrderLine element
	 * of the API
	 * 
	 * @param orderLinesToConsolidateNode
	 *            The OrderLinesToConsolidate originally node underneath the
	 *            input doc's Order element
	 * @return orderOrderLinesNode The <OrderLines> node underneath <Order>
	 */
	private Element getChangeOrderOrderLinesIpForConsolidation(Node orderLinesToConsolidateNode) throws Exception {
		logger.verbose("@@@@@ Entering NWCGValidateIssueLineConsolidation.getChangeOrderOrderLinesIpForConsolidation @@@@@");
		Element orderLinesToConsolidateElm = (Element) orderLinesToConsolidateNode;
		NodeList nl = orderLinesToConsolidateElm.getElementsByTagName(NWCGConstants.ORDER_LINE);
		NodeList orderOrderLinesNodeList = orderLinesToConsolidateNode.getOwnerDocument().getDocumentElement().getElementsByTagName(NWCGConstants.ORDER_LINES);
		Node orderOrderLinesNode = null;
		if (orderOrderLinesNodeList.getLength() == 1) {
			orderOrderLinesNode = orderOrderLinesNodeList.item(0);
		} else {
			throw new YFSException("Unable to complete consolidation request!");
		}
		Vector<Node> oLinestoAdd = new Vector<Node>();
		Vector<Node> oLinestoRemove = new Vector<Node>();
		for (int i = 0; i < nl.getLength(); i++) {
			Node currNode = nl.item(i);
			oLinestoRemove.add(currNode);
			Element currOL = (Element) currNode;
			String currOLK = currOL.getAttribute(NWCGConstants.ORDER_LINE_KEY);
			// Get the original order line but with OrderLineTranQuantity/@OrderedQty=0 and OrderLine/Extn/@ExtnUTFQty equal to OrderLine/Extn/@ExtnOrigReqQty
			Node origOL = getOriginalOrderLine(currOLK);
			Node newOrderLineNode = orderLinesToConsolidateNode.getOwnerDocument().importNode(origOL, true);
			oLinestoAdd.add(newOrderLineNode);
		}
		while (!oLinestoRemove.isEmpty())
			orderOrderLinesNode.removeChild(oLinestoRemove.remove(0));
		while (!oLinestoAdd.isEmpty())
			orderOrderLinesNode.appendChild(oLinestoAdd.remove(0));
		logger.verbose("@@@@@ Exiting NWCGValidateIssueLineConsolidation.getChangeOrderOrderLinesIpForConsolidation @@@@@");
		return (Element) orderOrderLinesNode;
	}

	/**
	 * Calls getOrderLineList to get the details of the order line key passed in
	 * to the method
	 * 
	 * @return Node A <OrderLines> node with <OrderLine> children nodes.
	 * @throws Exception
	 */
	private Node getOriginalOrderLine(String orderLineKey) throws Exception {
		logger.verbose("@@@@@ Entering NWCGValidateIssueLineConsolidation.getOriginalOrderLine @@@@@");
		String inputXml = "<OrderLine OrderLineKey=\"" + orderLineKey + "\"/>";
		StringBuffer opTemplateBuff = new StringBuffer("<OrderLineList>");
		opTemplateBuff.append("<OrderLine OrderHeaderKey=\"\" ConditionVariable1=\"\" ConditionVariable2=\"\" PrimeLineNo=\"\" SubLineNo=\"\" ");
		opTemplateBuff.append("IntentionalBackorder=\"\" ItemGroupCode=\"\" OrderClass=\"\" OriginalOrderedQty=\"\" ShipNode=\"\" isHistory=\"\">");
		opTemplateBuff.append("<Item ItemID=\"\" ProductClass=\"\"/><OrderLineTranQuantity OrderedQty=\"\" TransactionalUOM=\"\"/>");
		opTemplateBuff.append("<Extn/></OrderLine></OrderLineList>");
		String opTemplate = opTemplateBuff.toString();
		Document getOrderLineListIpDoc = XMLUtil.getDocument(inputXml);
		Document opTemplateDoc = XMLUtil.getDocument(opTemplate);
		Document opDoc = CommonUtilities.invokeAPI(myEnvironment, opTemplateDoc, "getOrderLineList", getOrderLineListIpDoc);
		Element orderLine = null;
		if (opDoc != null) {
			Element docElm = opDoc.getDocumentElement();
			NodeList nl = docElm.getElementsByTagName(NWCGConstants.ORDER_LINE);
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if (n instanceof Element) {
					orderLine = (Element) n;
					// OrderLine - Extn - ExtnRequestNo
					NodeList ExtnList = orderLine.getElementsByTagName("Extn");
					Node ExtnNode = ExtnList.item(0);
					Element ExtnElement = (Element) ExtnNode;
					String strExtnRequestNo = ExtnElement.getAttribute("ExtnRequestNo");
					// S-1.1 (survivor), S-2 & S-3 get consolidated into S-1. Only do this if block if your survivor S-1.1 does not contain S-1.
					if (!newReqNo.contains(strExtnRequestNo)) {
						// ConditionVariable2 = XLD-Consolidated
						orderLine.setAttribute(NWCGConstants.CONDITION_VAR2, NWCGConstants.CONDVAR2_XLD_REASON_CONS);
					} else {
						orderLine.setAttribute(NWCGConstants.CONDITION_VAR2, NWCGConstants.CONDVAR2_XLD_REASON_CONS_NOPROCESSING);
					}
					break;
				}
			}
		}
		orderLine = setNewOrderLineAttribs(orderLine);
		logger.verbose("@@@@@ Exiting NWCGValidateIssueLineConsolidation.getOriginalOrderLine @@@@@");
		return orderLine;
	}

	private Element setNewOrderLineAttribs(Element orderLine) {
		logger.verbose("@@@@@ Entering NWCGValidateIssueLineConsolidation.setNewOrderLineAttribs @@@@@");
		if (orderLine.hasAttribute(NWCGConstants.STATUS_QTY_ATTR))
			orderLine.removeAttribute(NWCGConstants.STATUS_QTY_ATTR);
		if (orderLine.hasAttribute(NWCGConstants.YFC_NODE_NUMBER))
			orderLine.removeAttribute(NWCGConstants.YFC_NODE_NUMBER);
		orderLine.setAttribute(NWCGConstants.CONDITION_VAR1, NWCGConstants.ICBS_SYSTEM);
		// Modify the OrderLine/OrderLineTranQuantity/@OrderedQty
		NodeList nl = orderLine.getElementsByTagName(NWCGConstants.ORDER_LINE_TRAN_QTY_ELM);
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n instanceof Element) {
				Element olTrQty = (Element) n;
				olTrQty.setAttribute(NWCGConstants.ORDERED_QTY, "0");
				break;
			}
		}
		int dotIndex = newReqNo.indexOf('.');
		String requestNo = "";
		// Set the OrderLine/Extn/@ExtnUTFQty to OrderLine/Extn/@ExtnOrigReqQty
		NodeList extnList = orderLine.getElementsByTagName(NWCGConstants.EXTN_ELEMENT);
		for (int i = 0; i < extnList.getLength(); i++) {
			Node n = extnList.item(i);
			if (n instanceof Element) {
				Element extnElm = (Element) n;
				String extnOrigReqQty = extnElm.getAttribute(NWCGConstants.ORIGINAL_REQUESTED_QTY);
				requestNo = extnElm.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
				extnElm.setAttribute(NWCGConstants.UTF_QTY, extnOrigReqQty);
				if (dotIndex != -1) {
					String baseReqNo = newReqNo.substring(0, dotIndex);
					if (baseReqNo.equals(requestNo)) {
						baseRequestNoAttr = extnElm.getAttribute(NWCGConstants.EXTN_BASE_REQUEST_NO);
					}
				}
				break;
			}
		}
		// Add OL/Notes/Note & @NoteText to show that the XLD lines are consol'ed except on the surviving request order line
		Document parentDoc = orderLine.getOwnerDocument();
		Element olNotes = parentDoc.createElement(NWCGConstants.NOTES_ELM);
		Element olNote = parentDoc.createElement(NWCGConstants.NOTE_ELM);
		if (dotIndex != -1) {
			String baseReqNo = newReqNo.substring(0, dotIndex);
			if (!requestNo.equals(baseReqNo)) {
				orderLine.appendChild(olNotes);
				olNotes.appendChild(olNote);
				olNote.setAttribute(NWCGConstants.NOTE_TEXT_ATTR, "Consolidated into request " + baseReqNo);
			} else {
				// Bug Fix #4 post BR2 go live - update order line comment with cancellation reason
				orderLine.appendChild(olNotes);
				olNotes.appendChild(olNote);
				olNote.setAttribute(NWCGConstants.NOTE_TEXT_ATTR, "Cancelled due to consolidation");
			}
		}
		logger.verbose("@@@@@ Exiting NWCGValidateIssueLineConsolidation.setNewOrderLineAttribs @@@@@");
		return orderLine;
	}
}