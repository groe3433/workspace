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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * 
 * @author Oxford Consulting Group
 * @version 1.0
 * @date November 6, 2013
 */
public class NWCGChkMaxLinesAndConfirmOrder implements YIFCustomApi {

	private Properties myProperties = null;

	private static Logger logger = Logger.getLogger(com.nwcg.icbs.yantra.api.issue.NWCGChkMaxLinesAndConfirmOrder.class.getName());

	private String strShipNode = NWCGConstants.EMPTY_STRING;

	private List<String> lsItemList = new ArrayList<String>();

	/**
	 * 
	 * @param arg0
	 * @throws Exception
	 */
	public void setProperties(Properties arg0) throws Exception {
		this.myProperties = arg0;
	}

	/**
	 * This method will check if maximum no of lines per issue exceeded the
	 * predefined value from Common Code. If it exceeds it will throw an
	 * NWCGException, else it will go ahead and confirm the order
	 * 
	 * @param env
	 * @param docIP
	 * @return
	 * @throws Exception
	 */
	public Document chkMaxLinesAndConfirmOrder(YFSEnvironment env, Document docIP) throws Exception {
		System.out.println("@@@@@ Entering NWCGChkMaxLinesAndConfirmOrder::chkMaxLinesAndConfirmOrder @@@@@");
		System.out.println("@@@@@ docIP : " + XMLUtil.getXMLString(docIP));
		
		int maxLinesPerIssue = CommonUtilities.getMaximumRequestLines(env);
		// Try to get the lines from JSP rather than making a call.
		Element inputDocElm = docIP.getDocumentElement();
		String orderHdrKey = inputDocElm.getAttribute(NWCGConstants.ORDER_HEADER_KEY);

		// Order/@ShipNode is not in the input, only OrderHeaderKey
		strShipNode = inputDocElm.getAttribute(NWCGConstants.SHIP_NODE);

		int noOfLines = getOrderLineCount(env, orderHdrKey);
		System.out.println("@@@@@ noOfLines : " + noOfLines);

		if (noOfLines > maxLinesPerIssue) {
			System.out.println("!!!!! Number of lines per issue (" + noOfLines + ") exceeds maximum configured value of " + maxLinesPerIssue);
			throw new NWCGException("NWCG_MAX_LINES_PER_ISSUE_001", new Object[] { new Integer(noOfLines).toString(), new Integer(maxLinesPerIssue).toString() });
		}

		Document docConfirmDraftOrderIP = null;
		Document docConfirmDraftOrderOP = null;
		try {
			docConfirmDraftOrderIP = XMLUtil.createDocument("ConfirmDraftOrder");
			Element elmConfirmDraftOrderIP = docConfirmDraftOrderIP.getDocumentElement();
			elmConfirmDraftOrderIP.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHdrKey);
			System.out.println("@@@@@ orderHdrKey : " + orderHdrKey);
			
			Document docConfirmDraftOrderTmpl = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
			docConfirmDraftOrderTmpl.getDocumentElement().setAttribute(NWCGConstants.ORDER_HEADER_KEY, NWCGConstants.EMPTY_STRING);
			
			System.out.println("@@@@@ docConfirmDraftOrderIP : " + XMLUtil.getXMLString(docConfirmDraftOrderIP));
			System.out.println("@@@@@ docConfirmDraftOrderTmpl : " + XMLUtil.getXMLString(docConfirmDraftOrderTmpl));
			docConfirmDraftOrderOP = CommonUtilities.invokeAPI(env, docConfirmDraftOrderTmpl, NWCGConstants.API_CONFIRM_DRAFT_ORDER, docConfirmDraftOrderIP);
			System.out.println("@@@@@ docConfirmDraftOrderOP : " + XMLUtil.getXMLString(docConfirmDraftOrderOP));
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}
		
		System.out.println("@@@@@ docConfirmDraftOrderOP : " + XMLUtil.getXMLString(docConfirmDraftOrderOP));
		System.out.println("@@@@@ Exiting NWCGChkMaxLinesAndConfirmOrder::chkMaxLinesAndConfirmOrder @@@@@");
		return docConfirmDraftOrderOP;
	}

	/**
	 * 
	 * @param env
	 * @param orderHdrKey
	 * @return
	 * @throws Exception
	 */
	private int getOrderLineCount(YFSEnvironment env, String orderHdrKey) throws Exception {
		System.out.println("@@@@@ Entering NWCGChkMaxLinesAndConfirmOrder::getOrderLineCount @@@@@");
		
		int noOfLines = -1;
		int apiNumberLinesReturned = 0;
		try {
			Document docOrderLineListIP = null;
			Document docOrderLineListOP = null;
			docOrderLineListIP = XMLUtil.createDocument(NWCGConstants.ORDER_LINE);
			Element elmOrderLineListIP = docOrderLineListIP.getDocumentElement();
			elmOrderLineListIP.setAttribute(NWCGConstants.ORDER_HEADER_KEY, orderHdrKey);
			Document docOrderLineListTmpl = getOrderLineListTemplate();
			docOrderLineListOP = CommonUtilities.invokeAPI(env, docOrderLineListTmpl, NWCGConstants.API_GET_ORDER_LINE_LIST, docOrderLineListIP);
			if (docOrderLineListOP != null) {
				String strNoOfLines = docOrderLineListOP.getDocumentElement().getAttribute(NWCGConstants.TOTAL_NUMBER_OF_RECORDS);
				noOfLines = new Integer(strNoOfLines).intValue();
			}

			NodeList nl = docOrderLineListOP.getElementsByTagName(NWCGConstants.ORDER_LINE);
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				Element orderLine = (n instanceof Element) ? (Element) n : null;
				if (orderLine == null)
					continue;
				if (StringUtil.isEmpty(strShipNode)) {
					strShipNode = orderLine.getAttribute(NWCGConstants.SHIP_NODE);
				}
				apiNumberLinesReturned++;
				// validate communication items-- Gaurav-- starts
				NodeList ls = docOrderLineListOP.getElementsByTagName(NWCGConstants.ITEM);
				String strItemID = NWCGConstants.EMPTY_STRING;
				if (ls.getLength() > 0) {
					for (int j = 0; j < ls.getLength(); j++) {
						Element eleItem = (Element) ls.item(j);
						strItemID = eleItem.getAttribute(NWCGConstants.ITEM_ID);
						String strProductLine = eleItem.getAttribute(NWCGConstants.PRODUCT_LINE);
						if (!StringUtil.isEmpty(strShipNode) && !strShipNode.equals("IDGBK") && (strProductLine.equals(NWCGConstants.NIRSC_COMMUNICATIONS) || strProductLine.equals(NWCGConstants.COMMUNICATIONS_PROD_LINE))) {
							YFSException ne = new YFSException();
							ne.setErrorCode("NWCG_NIRSC_COMMUNICATION_ITEM_ERROR");
							ne.setErrorDescription("Order has Item(s) " + lsItemList.toString() + " which can be processed only at IDGBK Cache");
							throw ne;
						}
					}
				}
				Element olExtnElm = XMLUtil.getFirstElementByName(orderLine, NWCGConstants.EXTN_ELEMENT);
				String availRFIQtyStr = NWCGConstants.EMPTY_STRING;
				String orderedQtyStr = NWCGConstants.EMPTY_STRING;
				String lineRequestNo = NWCGConstants.EMPTY_STRING;
				if (olExtnElm != null) {
					availRFIQtyStr = olExtnElm.getAttribute(NWCGConstants.EXTN_RFI_QTY);
					lineRequestNo = olExtnElm.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
				}
				orderedQtyStr = orderLine.getAttribute(NWCGConstants.ORDERED_QTY);
				double avail = 0;
				double ordered = 0;
				if (!StringUtil.isEmpty(availRFIQtyStr) && !StringUtil.isEmpty(orderedQtyStr)) {
					try {
						avail = Double.parseDouble(availRFIQtyStr);
						ordered = Double.parseDouble(orderedQtyStr);
					} catch (NumberFormatException nfe) {
						YFSException ne = new YFSException();
						ne.setErrorCode("NWCG_CONFIRM_DRAFT_ORDER_QTY_FAILURE_001");
						ne.setErrorDescription("ICBS was unable to determine the Available RFI Qty and Issue Qty!");
						throw ne;
					}
					if (avail < ordered) {
						System.out.println("!!!!! The Issue Qty of " + ordered + " for Item ID " + strItemID + " on Request " + lineRequestNo + " exceeds the Available RFI Quantity of " + avail + ". ICBS cannot " + "confirm the Draft Order until the Issue Quantity is corrected!");
						throw new NWCGException("NWCG_REQ_QTY_NOT_AVAIL", new Object[] { new Double(ordered).toString(), new Double(avail).toString(), lineRequestNo });
					}
				}
			}
		} catch (ParserConfigurationException pce) {
			System.out.println("!!!!! Caught ParserConfigurationException : " + pce);
			pce.printStackTrace();
			throw pce;
		} catch (YFSException ne) {
			System.out.println("!!!!! Caught YFSException : " + ne);
			ne.printStackTrace();
			throw ne;
		} catch (Exception e) {
			System.out.println("!!!!! Caught Exception : " + e);
			e.printStackTrace();
			throw e;
		}
		
		System.out.println("@@@@@ Exiting NWCGChkMaxLinesAndConfirmOrder::getOrderLineCount @@@@@");
		return noOfLines;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	private Document getOrderLineListTemplate() throws Exception {
		System.out.println("@@@@@ Entering NWCGChkMaxLinesAndConfirmOrder::getOrderLineListTemplate @@@@@");

		Document docTemplate = XMLUtil.createDocument("OrderLineList");
		Element eleDocTemplate = docTemplate.getDocumentElement();
		eleDocTemplate.setAttribute(NWCGConstants.TOTAL_NUMBER_OF_RECORDS, NWCGConstants.EMPTY_STRING);

		Element eleOrderLine = docTemplate.createElement(NWCGConstants.ORDER_LINE);
		eleOrderLine.setAttribute(NWCGConstants.ORDER_HEADER_KEY, NWCGConstants.EMPTY_STRING);
		eleOrderLine.setAttribute(NWCGConstants.ORDERED_QTY, NWCGConstants.EMPTY_STRING);
		eleDocTemplate.appendChild(eleOrderLine);

		Element eleLineExtn = docTemplate.createElement(NWCGConstants.EXTN_ELEMENT);
		eleOrderLine.appendChild(eleLineExtn);

		Element eleItem = docTemplate.createElement(NWCGConstants.ITEM);
		eleItem.setAttribute(NWCGConstants.ITEM_ID, NWCGConstants.EMPTY_STRING);
		eleItem.setAttribute(NWCGConstants.PRODUCT_LINE, NWCGConstants.EMPTY_STRING);
		eleOrderLine.appendChild(eleItem);

		System.out.println("@@@@@ Exiting NWCGChkMaxLinesAndConfirmOrder::getOrderLineListTemplate @@@@@");
		return docTemplate;
	}
}