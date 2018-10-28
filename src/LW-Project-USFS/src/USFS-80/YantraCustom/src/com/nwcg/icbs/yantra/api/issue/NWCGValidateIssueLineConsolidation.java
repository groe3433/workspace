package com.nwcg.icbs.yantra.api.issue;

import java.util.Properties;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.issue.util.NWCGValidateIssueLineUtil;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * Back end API invoked by SDF service for implementing and validating
 * order line consolidation requests from the front end user interface
 * 
 * @author drodriguez
 * @since Business Release 2 (BR2) Increment 4
 */
public class NWCGValidateIssueLineConsolidation implements YIFCustomApi {
	
	private Properties sdfApiArgs = null;
	private String orderHeaderKey = null;
	private String newReqNo = null;
	private String baseRequestNoAttr = "";
	private YFSEnvironment myEnvironment = null;
	private static Logger logger = Logger.getLogger();
	
	public void setProperties(Properties sdfApiArgs) throws Exception {
		this.sdfApiArgs = sdfApiArgs;
	}
	
	/**
	 * Validates a consolidation request received from the front end Item Consolidation UI and calls
	 * changeOrder to add the new line and to cancel the lines selected for consolidation.	 
	 * 
	 * @param env		The standard YFSEnvironment object
	 * @param inputDoc	The request DOM passed by the UI from the Order namespace upon hitting the Save button
	 * 
	 * 	Example input:
	 * 
	 * <Order IgnoreOrdering="Y" OrderHeaderKey="201004141345074254482">
	 * 	<OrderLines>
	 * 		<OrderLine ReqShipDate="2010-04-14T00:00:00">
	 * 						<Item ItemID="000018" ProductClass="Supply"/>
	 * 						<Extn ExtnBackOrderFlag="N" ExtnBackorderedQty="0" ExtnRequestNo="S-041401.1"/>
	 * 						<OrderLineTranQuantity OrderedQty="6.0" TransactionalUOM="BD"/>
	 * 			<Notes>
	 * 				<Note NoteText="asasdfasdf"/>
	 * 			</Notes>
	 *		</OrderLine>
	 *	</OrderLines>
	 *	<OrderLinesToConsolidate>
	 *		<OrderLine OrderLineKey="201004141345074254483" YFC_NODE_NUMBER="1"/>
	 *		<OrderLine OrderLineKey="201004141345074254484" YFC_NODE_NUMBER="2"/>
	 *		<OrderLine OrderLineKey="201004141345074254485" YFC_NODE_NUMBER="3"/>
	 *	</OrderLinesToConsolidate>
	 * </Order>
	 *
	 * @return Document	The resulting output of the CommonUtilities.invokeAPI() method. Not used by the caller.
	 * @throws Exception
	 */
	public Document validateConsolidationRequest(YFSEnvironment env, Document inputDoc) throws Exception {
		System.out.println("@@@@@ Entering NWCGValidateIssueLineConsolidation::validateConsolidationRequest @@@@@");
		System.out.println("@@@@@ inputDoc : " + XMLUtil.extractStringFromDocument(inputDoc));
		
		this.myEnvironment = env;
		newReqNo = getNewRequestNumberFromInput(inputDoc);
		System.out.println("@@@@@ newReqNo : " + newReqNo);
		Node orderLinesToConsolidateNode = null;
		
		Element inputDocElm = inputDoc.getDocumentElement();
		orderHeaderKey = inputDocElm.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		System.out.println("@@@@@ orderHeaderKey : " + orderHeaderKey);
		NodeList orderLinesToConsolidateNL = inputDocElm.getElementsByTagName("OrderLinesToConsolidate");

		System.out.println("@@@@@ orderLinesToConsolidateNL.getLength() : " + orderLinesToConsolidateNL.getLength());
		if (orderLinesToConsolidateNL.getLength() == 1) {			
			orderLinesToConsolidateNode = orderLinesToConsolidateNL.item(0);		
		}
		else { 
			System.out.println("!!!!! Error: Unable to complete consolidation request!");
			throw new YFSException("Unable to complete consolidation request!");
		}
		
		//publish unpublished item(s) for Ross initiated issues
		String systemOfOrigin = inputDocElm.getAttribute("ExtnSystemOfOrigin"); 
		System.out.println("@@@@@ systemOfOrigin : " + systemOfOrigin);
		if(systemOfOrigin != null && systemOfOrigin.equalsIgnoreCase("ROSS")){
			NWCGValidateIssueLineUtil.publishUnpublishedItems(env, inputDoc);
		}

		Document secondChangeOrderDoc = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
		Element secondDocElm = secondChangeOrderDoc.getDocumentElement();
		secondDocElm.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHeaderKey);
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
			if (!currElm.hasAttribute(NWCGConstants.CONDITION_VAR1))
				currElm.setAttribute(NWCGConstants.CONDITION_VAR1, NWCGConstants.ICBS_SYSTEM);
			
			Element elmExtn = (Element) currElm.getElementsByTagName(NWCGConstants.EXTN_ELEMENT).item(0);
			elmExtn.setAttribute(NWCGConstants.EXTN_BASE_REQUEST_NO, baseRequestNoAttr);
		}
		
		System.out.println("@@@@@ inputDoc 1 : " + XMLUtil.extractStringFromDocument(inputDoc));
		CommonUtilities.invokeAPI(myEnvironment, NWCGConstants.API_CHANGE_ORDER, inputDoc);
		
		System.out.println("@@@@@ secondChangeOrderDoc 1 : " + XMLUtil.extractStringFromDocument(secondChangeOrderDoc));
		Document docFinalReturn = CommonUtilities.invokeAPI(myEnvironment, NWCGConstants.API_CHANGE_ORDER, secondChangeOrderDoc);
		System.out.println("@@@@@ docFinalReturn : " + XMLUtil.extractStringFromDocument(docFinalReturn));
		System.out.println("@@@@@ Exiting NWCGValidateIssueLineConsolidation::validateConsolidationRequest @@@@@");
		return docFinalReturn;
	}	
	
	private String getNewRequestNumberFromInput(Document inputDoc) throws Exception {	
		System.out.println("@@@@@ Entering NWCGValidateIssueLineConsolidation::getNewRequestNumberFromInput @@@@@");
		String requestNo = "";
		Element orderLineExtn = XMLUtil.getFirstElementByName(inputDoc.getDocumentElement(), "OrderLines/OrderLine/Extn");
		requestNo = orderLineExtn.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
		System.out.println("@@@@@ requestNo : " + requestNo);
		System.out.println("@@@@@ Exiting NWCGValidateIssueLineConsolidation::getNewRequestNumberFromInput @@@@@");
		return requestNo;
	}

	/**
	 * Calls getOrderLineList for each OrderLine element containing only an OrderLineKey attribute
	 * and replaces it with the output OrderLine element of the API
	 * 
	 * @param orderLinesToConsolidateNode The OrderLinesToConsolidate originally
	 * 								node underneath the input doc's Order element
	 * @return orderOrderLinesNode The <OrderLines> node underneath <Order>
	 */
	private Element getChangeOrderOrderLinesIpForConsolidation(Node orderLinesToConsolidateNode) throws Exception {
		System.out.println("@@@@@ Entering NWCGValidateIssueLineConsolidation::getChangeOrderOrderLinesIpForConsolidation @@@@@");
		
		Element orderLinesToConsolidateElm = (Element) orderLinesToConsolidateNode;
		NodeList nl = orderLinesToConsolidateElm.getElementsByTagName(NWCGConstants.ORDER_LINE);
		
		NodeList orderOrderLinesNodeList = orderLinesToConsolidateNode.getOwnerDocument().getDocumentElement().getElementsByTagName(NWCGConstants.ORDER_LINES);
		Node orderOrderLinesNode = null;
		
		System.out.println("@@@@@ orderOrderLinesNodeList.getLength() : " + orderOrderLinesNodeList.getLength());
		if (orderOrderLinesNodeList.getLength() == 1) {			
			orderOrderLinesNode = orderOrderLinesNodeList.item(0);		
		}
		else { 
			System.out.println("!!!!! Error: Unable to complete consolidation request!");
			throw new YFSException("Unable to complete consolidation request!");
		}
		Vector <Node>oLinestoAdd = new Vector<Node>();
		Vector <Node>oLinestoRemove = new Vector<Node>();
		for (int i = 0; i < nl.getLength(); i++) {			
			Node currNode = nl.item(i);
			oLinestoRemove.add(currNode);
			Element currOL = (Element) currNode;
			String currOLK = currOL.getAttribute(NWCGConstants.ORDER_LINE_KEY);
			
			//Get the original order line but with OrderLineTranQuantity/@OrderedQty=0
			//and OrderLine/Extn/@ExtnUTFQty equal to OrderLine/Extn/@ExtnOrigReqQty
			Node origOL = getOriginalOrderLine(currOLK);
			
			Node newOrderLineNode = orderLinesToConsolidateNode.getOwnerDocument().importNode(origOL, true);
			oLinestoAdd.add(newOrderLineNode);
		}	
		while (!oLinestoRemove.isEmpty())
			orderOrderLinesNode.removeChild(oLinestoRemove.remove(0));
		while (!oLinestoAdd.isEmpty())
			orderOrderLinesNode.appendChild(oLinestoAdd.remove(0));
		
		System.out.println("@@@@@ Exiting NWCGValidateIssueLineConsolidation::getChangeOrderOrderLinesIpForConsolidation @@@@@");
		return (Element) orderOrderLinesNode;
	}

	/**
	 * Calls getOrderLineList to get the details of the order line key 
	 * passed in to the method 
	 * 
	 * @return Node A <OrderLines> node with <OrderLine> children nodes.
	 * @throws Exception
	 */
	private Node getOriginalOrderLine(String orderLineKey) throws Exception {
		System.out.println("@@@@@ Entering NWCGValidateIssueLineConsolidation::getOriginalOrderLine @@@@@");
		
		String inputXml = "<OrderLine OrderLineKey=\""+orderLineKey+"\"/>";
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
					Element ExtnElement = (Element)ExtnNode;
					String strExtnRequestNo = ExtnElement.getAttribute("ExtnRequestNo");
					System.out.println("@@@@@ strExtnRequestNo : " + strExtnRequestNo);
					System.out.println("@@@@@ newReqNo : " + newReqNo);
					
					// S-1.1 (survivor), S-2 & S-3 get consolidated into S-1. Only do this if block if your survivor S-1.1 does not contain S-1. 
					if(!newReqNo.contains(strExtnRequestNo)) {
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
		
		System.out.println("@@@@@ Exiting NWCGValidateIssueLineConsolidation::getOriginalOrderLine @@@@@");
		return orderLine; 
	}
	
	private Element setNewOrderLineAttribs(Element orderLine) { 
		System.out.println("@@@@@ Entering NWCGValidateIssueLineConsolidation::setNewOrderLineAttribs @@@@@");
				
		if (orderLine.hasAttribute(NWCGConstants.STATUS_QTY_ATTR)) 
			orderLine.removeAttribute(NWCGConstants.STATUS_QTY_ATTR);
		
		if (orderLine.hasAttribute(NWCGConstants.YFC_NODE_NUMBER))
			orderLine.removeAttribute(NWCGConstants.YFC_NODE_NUMBER);
		
		orderLine.setAttribute(NWCGConstants.CONDITION_VAR1, NWCGConstants.ICBS_SYSTEM);
		
		//Modify the OrderLine/OrderLineTranQuantity/@OrderedQty
		NodeList nl = orderLine.getElementsByTagName(NWCGConstants.ORDER_LINE_TRAN_QTY_ELM);
		for (int i =0; i < nl.getLength(); i++){
			Node n = nl.item(i);
			if (n instanceof Element) {
				Element olTrQty = (Element) n;
				olTrQty.setAttribute(NWCGConstants.ORDERED_QTY, "0");
				break;
			}
		}

		int dotIndex = newReqNo.indexOf('.');
		String requestNo = "";
		//Set the OrderLine/Extn/@ExtnUTFQty to OrderLine/Extn/@ExtnOrigReqQty
		NodeList extnList = orderLine.getElementsByTagName(NWCGConstants.EXTN_ELEMENT);
		for (int i =0; i < extnList.getLength(); i++){
			Node n = extnList.item(i);
			if (n instanceof Element) {
				Element extnElm = (Element) n;
				String extnOrigReqQty = extnElm.getAttribute(NWCGConstants.ORIGINAL_REQUESTED_QTY);
				requestNo = extnElm.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
				extnElm.setAttribute(NWCGConstants.UTF_QTY, extnOrigReqQty);
				if (dotIndex != -1) {
					String baseReqNo = newReqNo.substring(0, dotIndex);
					if (baseReqNo.equals(requestNo)){
						baseRequestNoAttr = extnElm.getAttribute(NWCGConstants.EXTN_BASE_REQUEST_NO);
					}
				}
				break;
			}
		}
		
		//Add OL/Notes/Note & @NoteText to show that the XLD lines are consol'ed
		//except on the surviving request order line
		Document parentDoc = orderLine.getOwnerDocument();
		Element olNotes = parentDoc.createElement(NWCGConstants.NOTES_ELM);
		Element olNote = parentDoc.createElement(NWCGConstants.NOTE_ELM);
		
		if (dotIndex != -1) {
			String baseReqNo = newReqNo.substring(0, dotIndex);
			if (!requestNo.equals(baseReqNo)) {
				orderLine.appendChild(olNotes);
				olNotes.appendChild(olNote);
				olNote.setAttribute(NWCGConstants.NOTE_TEXT_ATTR, "Consolidated into request "+baseReqNo);
			} else {
				//Bug Fix #4 post BR2 go live - update order line comment with cancellation reason
				orderLine.appendChild(olNotes);
				olNotes.appendChild(olNote);
				olNote.setAttribute(NWCGConstants.NOTE_TEXT_ATTR, "Cancelled due to consolidation");				
			}
		}
		System.out.println("@@@@@ Exiting NWCGValidateIssueLineConsolidation::setNewOrderLineAttribs @@@@@");
		return orderLine;
	}	
}