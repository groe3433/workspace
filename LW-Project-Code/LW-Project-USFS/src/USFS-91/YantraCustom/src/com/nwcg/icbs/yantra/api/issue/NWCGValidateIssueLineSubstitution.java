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

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.api.issue.util.NWCGValidateIssueLineUtil;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGValidateIssueLineSubstitution implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGValidateIssueLineSubstitution.class);

	private Properties sdfApiArgs = null;
	private String orderHeaderKey = null;
	private String orderLineKey = null;
	private String maxLineStatus = null;
	private YFSEnvironment myEnvironment = null;
	private Node originalOrderLine = null;

	public void setProperties(Properties arg0) throws Exception {
		sdfApiArgs = arg0;
	}

	/**
	 * 1 a Cancel the original order line being substituted if it's in Created
	 * status b Modify the original order line being substituted if it's in less
	 * than Created status set the OrderLineTranQty of 0 so that the pipeline
	 * conditions will set the line status to "Canceled due to substitution" 2
	 * Create the input for the createOrder API using the input doc
	 * 
	 * @param env
	 * @param doc
	 * @return Document output of the CommonUtilities.invokeAPI() method
	 * @throws Exception
	 */
	public Document validateSubstitutionRequest(YFSEnvironment env, Document inputDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGValidateIssueLineSubstitution.validateSubstitutionRequest @@@@@");
		logger.verbose("@@@@@ inputDoc : " + XMLUtil.getXMLString(inputDoc));
		this.myEnvironment = env;
		Element docElm = inputDoc.getDocumentElement();
		maxLineStatus = docElm.getAttribute(NWCGConstants.MAX_LINE_STATUS);
		orderHeaderKey = docElm.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		orderLineKey = getOrderLineKeyFromInput(docElm);
		originalOrderLine = getOriginalOrderLine();
		// publish unpublished item(s) for Ross initiated issues
		String systemOfOrigin = docElm.getAttribute("ExtnSystemOfOrigin");
		logger.verbose("@@@@@ systemOfOrigin :: " + systemOfOrigin);
		if (systemOfOrigin != null && systemOfOrigin.equalsIgnoreCase("ROSS")) {
			NWCGValidateIssueLineUtil.publishUnpublishedItems(env, inputDoc);
		}
		Document addNewOrderLinesMultiApiDoc = addNewOrderLines(inputDoc);
		Document multiApiDoc = changeSubstitutedOrderLineStatusToXld(inputDoc, addNewOrderLinesMultiApiDoc);
		logger.verbose("@@@@@ Input document to multiApi : " + XMLUtil.extractStringFromDocument(addNewOrderLinesMultiApiDoc));
		logger.verbose("@@@@@ Exiting NWCGValidateIssueLineSubstitution.validateSubstitutionRequest @@@@@");
		return CommonUtilities.invokeAPI(myEnvironment, NWCGConstants.MULTI_API_API, multiApiDoc);
	}

	private Document addNewOrderLines(Document inputDoc) throws Exception {
		logger.verbose("@@@@@ Entering NWCGValidateIssueLineSubstitution.addNewOrderLines @@@@@");
		Document multiApiDoc = null;
		try {
			multiApiDoc = XMLUtil.createDocument(NWCGConstants.MULTI_API_ELM);
			Element maDocElm = multiApiDoc.getDocumentElement();
			Element apiElm = multiApiDoc.createElement(NWCGConstants.API_ELM);
			Element inpElm = multiApiDoc.createElement(NWCGConstants.INPUT_ELM);
			Element apiInputElm = multiApiDoc.createElement(NWCGConstants.ORDER_ELM);
			apiElm.setAttribute("Name", NWCGConstants.API_CHANGE_ORDER);
			apiInputElm.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHeaderKey);
			logger.verbose("@@@@@ orderHeaderKey :: " + orderHeaderKey);
			
			// BEGIN - CR 1693 - July 30, 2015
			String strNewShipToKey = "";
			String strNewShipToID = "";
			StringBuffer sbTemp_getOrderDetails = new StringBuffer("<Order ShipToKey=\"\" ShipToID=\"\"></Order>");
			String strTemp_getOrderDetails = sbTemp_getOrderDetails.toString();
			StringBuffer sbInput_getOrderDetails = new StringBuffer("<Order OrderHeaderKey=\"" + orderHeaderKey + "\"/>");
			String strInput_getOrderDetails = sbInput_getOrderDetails.toString();
			Document outDoc_getOrderDetails = CommonUtilities.invokeAPI(myEnvironment, XMLUtil.getDocument(strTemp_getOrderDetails), "getOrderDetails", XMLUtil.getDocument(strInput_getOrderDetails));
			strNewShipToKey = XPathUtil.getString(outDoc_getOrderDetails.getDocumentElement(), "/Order/@ShipToKey");
			logger.verbose("@@@@@ strNewShipToKey :: " + strNewShipToKey);
			strNewShipToID = XPathUtil.getString(outDoc_getOrderDetails.getDocumentElement(), "/Order/@ShipToID");
			logger.verbose("@@@@@ strNewShipToID :: " + strNewShipToID);
			// END - CR 1693 - July 30, 2015
			
			maDocElm.appendChild(apiElm);
			apiElm.appendChild(inpElm);
			inpElm.appendChild(apiInputElm);
			logger.verbose("@@@@@ inputDoc :: " + XMLUtil.getXMLString(inputDoc));
			Element inDocElm = inputDoc.getDocumentElement();
			NodeList nl = inDocElm.getElementsByTagName(NWCGConstants.ORDER_LINES);
			Node n = nl.item(0);
			if (n != null && (n instanceof Element)) {
				n = multiApiDoc.importNode(n, true);
				apiInputElm.appendChild(n);
			}
			String systemNumber = XPathUtil.getString(inDocElm, "/Order/@ExtnSystemNo");
			// Remove the OrderLine/@OrderLineKey attribute on the first order line
			nl = apiInputElm.getElementsByTagName(NWCGConstants.ORDER_LINE);
			for (int i = 0; i < nl.getLength(); i++) {
				Node olNode = nl.item(i);
				if (olNode != null && (olNode instanceof Element)) {
					Element olElm = (Element) olNode;
					
					// BEGIN - CR 1693 - July 30, 2015
					olElm.setAttribute("ShipToKey", strNewShipToKey);
					olElm.setAttribute("ShipToID", strNewShipToID);
					// END - CR 1693 - July 30, 2015
					
					if (olElm.hasAttribute(NWCGConstants.ORDER_LINE_KEY)) {
						olElm.removeAttribute(NWCGConstants.ORDER_LINE_KEY);
					}
					if (olElm.hasAttribute(NWCGConstants.YFC_NODE_NUMBER)) {
						olElm.removeAttribute(NWCGConstants.YFC_NODE_NUMBER);
					}
					// Set OrderLine/Extn/@ExtnSystemNo; Set the Base request no to that of the base request no of the original line
					NodeList nl2 = olElm.getElementsByTagName(NWCGConstants.EXTN_ELEMENT);
					if (nl2 != null && nl2.getLength() == 1) {
						if (nl2.item(0) instanceof Element) {
							Element orderLineExtnElm = (Element) nl2.item(0);
							if (StringUtil.isEmpty(systemNumber))
								systemNumber = NWCGConstants.EMPTY_STRING;
							orderLineExtnElm.setAttribute(NWCGConstants.SYSTEM_NO_ELEM, systemNumber);
							String extnBaseReqNo = ((Element) ((Element) originalOrderLine).getElementsByTagName(NWCGConstants.EXTN).item(0)).getAttribute(NWCGConstants.EXTN_BASE_REQUEST_NO);
							orderLineExtnElm.setAttribute(NWCGConstants.EXTN_BASE_REQUEST_NO, extnBaseReqNo);
						}
					}
					// Set OrderLine/Notes/Note/@NoteText = SYS NO: systemNumber + UI popup comment
					String newNoteText = NWCGConstants.EMPTY_STRING;
					StringBuffer sb = new StringBuffer("SYS NO: ");
					sb.append(systemNumber);
					newNoteText = sb.toString();
					NodeList nl3 = olElm.getElementsByTagName(NWCGConstants.NOTE_ELM);
					if (nl3 != null && nl3.getLength() > 0) {
						if (nl3.item(0) instanceof Element) {
							Element noteElm = (Element) nl3.item(0);
							String subPopupEnteredComment = noteElm.getAttribute(NWCGConstants.NOTE_TEXT_ATTR);
							if (!StringUtil.isEmpty(subPopupEnteredComment)) {
								noteElm.setAttribute(NWCGConstants.NOTE_TEXT_ATTR, newNoteText.concat(" " + subPopupEnteredComment));
							}
						}
					} else {
						Element curNewNotesElm = multiApiDoc.createElement(NWCGConstants.NOTES_ELM);
						Element curNewNoteElm = multiApiDoc.createElement(NWCGConstants.NOTE_ELM);
						curNewNoteElm.setAttribute(NWCGConstants.NOTE_TEXT_ATTR, newNoteText);
						curNewNotesElm.appendChild(curNewNoteElm);
						olElm.appendChild(curNewNotesElm);
					}
					olElm.setAttribute(NWCGConstants.CONDITION_VAR1, NWCGConstants.ICBS_SYSTEM);
				}
			}
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! Caught ParserConfigurationException :: FAILED TO SUBSTITUTE ORDER/ISSUE LINE...");
			throw new NWCGException(pce);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: FAILED TO SUBSTITUTE ORDER/ISSUE LINE...");
			throw new NWCGException(e);
		}
		logger.verbose("@@@@@ Exiting NWCGValidateIssueLineSubstitution.addNewOrderLines @@@@@");
		return multiApiDoc;
	}

	/**
	 * 
	 * @param inputDoc
	 * @throws NWCGException
	 */
	private Document changeSubstitutedOrderLineStatusToXld(Document inputDoc, Document multiApiInput) throws NWCGException {
		logger.verbose("@@@@@ Entering NWCGValidateIssueLineSubstitution.changeSubstitutedOrderLineStatusToXld @@@@@");
		try {
			Element maDocElm = multiApiInput.getDocumentElement();
			Element apiElm = multiApiInput.createElement(NWCGConstants.API_ELM);
			Element inpElm = multiApiInput.createElement(NWCGConstants.INPUT_ELM);
			Element apiInputElm = multiApiInput.createElement(NWCGConstants.ORDER_ELM);
			apiElm.setAttribute("Name", NWCGConstants.API_CHANGE_ORDER);
			maDocElm.appendChild(apiElm);
			apiElm.appendChild(inpElm);
			inpElm.appendChild(apiInputElm);
			Element oLines = multiApiInput.createElement(NWCGConstants.ORDER_LINES);
			apiInputElm.appendChild(oLines);
			apiInputElm.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHeaderKey);
			Element origOrderLineElm = null;
			if (originalOrderLine instanceof Element) {
				origOrderLineElm = (Element) originalOrderLine;
			}
			if (maxLineStatus.startsWith("11")) {
				Element oLine = multiApiInput.createElement(NWCGConstants.ORDER_LINE);
				oLines.appendChild(oLine);
				oLine.setAttribute(NWCGConstants.ORDER_LINE_KEY, orderLineKey);
				// If the line is in Created status we can do an action of cancel to cancel the substituted order line.
				oLine.setAttribute(NWCGConstants.ACTION, NWCGConstants.CANCEL);
				oLine.setAttribute(NWCGConstants.CONDITION_VAR2, NWCGConstants.CONDVAR2_XLD_REASON_SUB);
				// Bug fix #4 post BR2 go-live, add cancellation reason as NoteText on orderline.
				Element notesElm = multiApiInput.createElement(NWCGConstants.NOTES_ELM);
				Element noteElm = multiApiInput.createElement(NWCGConstants.NOTE_ELM);
				oLine.appendChild(notesElm);
				notesElm.appendChild(noteElm);
				noteElm.setAttribute(NWCGConstants.NOTE_TEXT_ATTR, "Cancelled due to substitution");
				// end of bug fix #4
				// Set the OrderLine/Extn/@ExtnUTFQty to OrderLine/Extn/@ExtnOrigReqQty
				Element oLineExtn = multiApiInput.createElement(NWCGConstants.EXTN_ELEMENT);
				oLine.appendChild(oLineExtn);
				NodeList extnList = origOrderLineElm.getElementsByTagName(NWCGConstants.EXTN_ELEMENT);
				for (int i = 0; i < extnList.getLength(); i++) {
					Node n = extnList.item(i);
					if (n instanceof Element) {
						Element extnElm = (Element) n;
						String extnOrigReqQty = extnElm.getAttribute(NWCGConstants.ORIGINAL_REQUESTED_QTY);
						oLineExtn.setAttribute(NWCGConstants.UTF_QTY, extnOrigReqQty);
						break;
					}
				}
			} else {
				originalOrderLine = multiApiInput.importNode(originalOrderLine, true);
				originalOrderLine = oLines.appendChild(originalOrderLine);
				if (originalOrderLine instanceof Element) {
					origOrderLineElm = (Element) originalOrderLine;
					origOrderLineElm.setAttribute(NWCGConstants.ACTION, NWCGConstants.MODIFY);
					origOrderLineElm.setAttribute(NWCGConstants.CONDITION_VAR2, NWCGConstants.CONDVAR2_XLD_REASON_SUB);
					origOrderLineElm.setAttribute(NWCGConstants.ORDER_LINE_KEY, orderLineKey);
					// Bug fix #4 post BR2 go-live, add cancellation reason as NoteText on orderline.
					Element notesElm = multiApiInput.createElement(NWCGConstants.NOTES_ELM);
					Element noteElm = multiApiInput.createElement(NWCGConstants.NOTE_ELM);
					origOrderLineElm.appendChild(notesElm);
					notesElm.appendChild(noteElm);
					noteElm.setAttribute(NWCGConstants.NOTE_TEXT_ATTR, "Cancelled due to substitution");
					// end of bug fix #4
				}
			}
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: FAILED TO SUBSTITUTE ORDER/ISSUE LINE...");
			throw new NWCGException(e);
		}
		logger.verbose("@@@@@ Exiting NWCGValidateIssueLineSubstitution.changeSubstitutedOrderLineStatusToXld @@@@@");
		return multiApiInput;
	}

	/**
	 * Calls getOrderLineList to get the details of the order line being removed
	 * (in draft order scenario) by the substitiution and then being re-added in
	 * "Cancelled due to substitution" status
	 * 
	 * @return
	 * @throws Exception
	 */
	private Node getOriginalOrderLine() throws Exception {
		logger.verbose("@@@@@ Entering NWCGValidateIssueLineSubstitution.getOriginalOrderLine @@@@@");
		String inputXml = "<OrderLine OrderLineKey=\"" + orderLineKey + "\"/>";
		StringBuffer opTemplateBuff = new StringBuffer("<OrderLineList>");
		opTemplateBuff.append("<OrderLine OrderHeaderKey=\"\" ConditionVariable1=\"\" ConditionVariable2=\"\" PrimeLineNo=\"\" SubLineNo=\"\" ");
		opTemplateBuff.append("IntentionalBackorder=\"\" ItemGroupCode=\"\" OrderClass=\"\" OriginalOrderedQty=\"\" ShipNode=\"\" isHistory=\"\">");
		opTemplateBuff.append("<Item ItemID=\"\" ProductClass=\"\"/><OrderLineTranQuantity OrderedQty=\"\" TransactionalUOM=\"\"/>");
		opTemplateBuff.append("<Extn/></OrderLine></OrderLineList>");
		String opTemplate = opTemplateBuff.toString();
		Document getOrderLineListIpDoc = XMLUtil.getDocument(inputXml);
		Document opTemplateDoc = XMLUtil.getDocument(opTemplate);
		Document opDoc = CommonUtilities.invokeAPI(myEnvironment, opTemplateDoc, NWCGConstants.API_GET_ORDER_LINE_LIST, getOrderLineListIpDoc);
		Element orderLine = null;
		if (opDoc != null) {
			Element docElm = opDoc.getDocumentElement();
			NodeList nl = docElm.getElementsByTagName(NWCGConstants.ORDER_LINE);
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if (n instanceof Element) {
					orderLine = (Element) n;
					orderLine.setAttribute(NWCGConstants.CONDITION_VAR2, NWCGConstants.CONDVAR2_XLD_REASON_SUB);
					break;
				}
			}
		}
		orderLine = setNewOrderLineAttribs(orderLine);
		logger.verbose("@@@@@ Exiting NWCGValidateIssueLineSubstitution.getOriginalOrderLine @@@@@");
		return orderLine;
	}

	private Element setNewOrderLineAttribs(Element orderLine) {
		logger.verbose("@@@@@ Entering NWCGValidateIssueLineSubstitution.setNewOrderLineAttribs @@@@@");
		if (orderLine.hasAttribute(NWCGConstants.STATUS_QTY_ATTR))
			orderLine.removeAttribute(NWCGConstants.STATUS_QTY_ATTR);
		// Modify the OrderLine/OrderLineTranQty/@OrderedQty
		NodeList nl = orderLine.getElementsByTagName(NWCGConstants.ORDER_LINE_TRAN_QTY_ELM);
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n instanceof Element) {
				Element olTrQty = (Element) n;
				olTrQty.setAttribute(NWCGConstants.ORDERED_QTY,
						NWCGConstants.ZERO_STRING);
				break;
			}
		}
		// Set the OrderLine/Extn/@ExtnUTFQty to OrderLine/Extn/@ExtnOrigReqQty
		NodeList extnList = orderLine.getElementsByTagName(NWCGConstants.EXTN_ELEMENT);
		for (int i = 0; i < extnList.getLength(); i++) {
			Node n = extnList.item(i);
			if (n instanceof Element) {
				Element extnElm = (Element) n;
				String extnOrigReqQty = extnElm.getAttribute(NWCGConstants.ORIGINAL_REQUESTED_QTY);
				extnElm.setAttribute(NWCGConstants.UTF_QTY, extnOrigReqQty);
				break;
			}
		}
		logger.verbose("@@@@@ Exiting NWCGValidateIssueLineSubstitution.setNewOrderLineAttribs @@@@@");
		return orderLine;
	}

	private String getOrderLineKeyFromInput(Element docElm) {
		logger.verbose("@@@@@ Entering NWCGValidateIssueLineSubstitution.getOrderLineKeyFromInput @@@@@");
		String retVal = NWCGConstants.EMPTY_STRING;
		NodeList nl = docElm.getElementsByTagName(NWCGConstants.ORDER_LINE);
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n instanceof Element) {
				Element ol = (Element) n;
				if (ol != null && ol.hasAttribute(NWCGConstants.ORDER_LINE_KEY)) {
					retVal = ol.getAttribute(NWCGConstants.ORDER_LINE_KEY);
					break;
				}
			}
		}
		logger.verbose("@@@@@ Exiting NWCGValidateIssueLineSubstitution.getOrderLineKeyFromInput @@@@@");
		return retVal;
	}
}