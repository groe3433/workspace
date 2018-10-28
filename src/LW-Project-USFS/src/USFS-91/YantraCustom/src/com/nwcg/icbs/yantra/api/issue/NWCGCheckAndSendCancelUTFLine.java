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

import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.api.issue.util.NWCGCloseAlert;
import com.nwcg.icbs.yantra.api.issue.util.NWCGNFESResourceRequest;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XPathWrapper;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class will be called on click of 'Save' in 'Issue Details' screen. This
 * class will send a message to ROSS on a ROSS initiated issue 1. For UTF line
 * (not partial substituted line) 2. For cancelled line 3. If the issues
 * contains full UTF lines, then it will close the alert in NWCG_ISSUE_SUCCESS
 * or NWCG_RADIO_ISSUE_SUCCESS.
 * 
 * @author sgunda
 */
public class NWCGCheckAndSendCancelUTFLine implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGCheckAndSendCancelUTFLine.class);

	private String orderNo;
	private String orderHdrKey;
	private String incNo;
	private String incYr;
	private String shippingContactName;
	private String shippingContactPhone;
	private Element elmOrderExtn;
	private Element elmPersonInfoShipTo;
	private String shipNode;
	private String orderCreateTS;

	public void setProperties(Properties arg0) throws Exception {
	}

	/**
	 * // 1. Check if issue's System of origin is ROSS // 2. If it is not ROSS,
	 * return // 3. If all the lines Extn element contain OrigReqIssueQty equals
	 * UTFQty and ConditionVariable2 does not // contain XLD // 4a.1. Send UTF
	 * fill message for each line // 4a.2. Close the PlaceNFESResourceRequest
	 * alert (if all the lines in the issue are in UTF status) // 4b. Return,
	 * don't do anything
	 * 
	 * Consolidation - Say ROSS initiates S-1, S-2, and S-3. 2 & 3 are
	 * consolidated into 1 (named S-1.1 in ICBS). S-1.1 is a subordinate off the
	 * original and a fill message for the consolidated "S-1.1" is sent back to
	 * ROSS as S-1. ROSS doesn't know anything about "S-1.1", they only know
	 * S-1. Also - (Bug as of Feb 28, 2014) - ROSS needs to get a fill message
	 * for the S-2 and S-3 items that were consolidated INTO S-1. (currently
	 * they are not getting this) These fill messages for 2 and 3 are of the
	 * "cancelled" variety.
	 * 
	 * @param env
	 * @param docIP
	 * @return
	 * @throws Exception
	 */
	public Document checkCancelUTFAndSendMsgToROSS(YFSEnvironment env, Document docIP) throws Exception {
		logger.verbose("@@@@@ Entering NWCGCheckAndSendCancelUTFLine::checkCancelUTFAndSendMsgToROSS @@@@@");
		String orderHdrKey = "";
		int noOfUTFOrCancelledLines = 0;
		Element elmRootUTF = docIP.getDocumentElement();
		Element elmExtnOrder = (Element) elmRootUTF.getElementsByTagName("Extn").item(0);
		Element elmOLs = null;
		String sysOfOrigin = elmExtnOrder.getAttribute(NWCGConstants.EXTN_SYSTEM_OF_ORIGIN);
		if (sysOfOrigin != null && sysOfOrigin.equals(NWCGConstants.ROSS_SYSTEM)) {
			orderHdrKey = elmRootUTF.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
			NodeList nlOrderChilds = elmRootUTF.getChildNodes();
			boolean obtOLs = false;
			for (int childs = 0; childs < nlOrderChilds.getLength() && !obtOLs; childs++) {
				Node tmpNode = nlOrderChilds.item(childs);
				if ((tmpNode.getNodeType() == Node.ELEMENT_NODE)) {
					if (tmpNode.getNodeName().equals(NWCGConstants.ORDER_LINES)) {
						elmOLs = (Element) tmpNode;
						NodeList nlOrderLine = elmOLs.getElementsByTagName(NWCGConstants.ORDER_LINE);
						if (nlOrderLine != null && nlOrderLine.getLength() > 0) {
							setOrderAttributes(elmRootUTF);
							noOfUTFOrCancelledLines = nlOrderLine.getLength();
							for (int i = 0; i < noOfUTFOrCancelledLines; i++) {
								Element elmOrderLine = (Element) nlOrderLine.item(i);
								boolean processedLine = false;
								// for UTF-ed (non-cancelled) requests
								if (isFullUTFLine(env, elmOrderLine)) {
									processedLine = true;
									logger.verbose("@@@@@ for UTF-ed (non-cancelled) requests...");
									sendMsgToROSS(env, elmOrderLine);
								}
								// getting the lineStatus for later
								String lineStatus = elmOrderLine.getAttribute(NWCGConstants.MAX_LINE_STATUS);
								logger.verbose("@@@@@ NWCGConstants.MAX_LINE_STATUS :: " + lineStatus);
								if (lineStatus == null || lineStatus.trim().length() < 2) {
									lineStatus = elmOrderLine.getAttribute(NWCGConstants.MIN_LINE_STATUS);
									logger.verbose("@@@@@ NWCGConstants.MIN_LINE_STATUS :: " + lineStatus);
								}
								// did we process a UTF-ed (non-cancelled) request? if no, then proceed...
								if (!processedLine) {
									String condVar2 = elmOrderLine.getAttribute("ConditionVariable2");
									// Do NOT send ROSS a cancellation message for a substituted request, otherwise it cannot be filled
									logger.verbose("@@@@@ condVar2 :: " + condVar2);
									if (!(condVar2.equals(NWCGConstants.CONDVAR2_XLD_REASON_SUB))) {
										if (!(condVar2.equals(NWCGConstants.CONDVAR2_XLD_REASON_RETRV))) {
											// SEND ROSS a cancellation message for a (non-surviving) consolidated request, or SEND ROSS a cancellation request if you cancel the request
											if (condVar2 != null && condVar2.contains(NWCGConstants.CONDVAR2_XLD_REASON_CONS_SUBSTRING)) {
												sendConsolidationMsgToROSS(env, elmOrderLine);
											} else if (lineStatus != null && lineStatus.trim().length() > 2 && lineStatus.equals(NWCGConstants.STATUS_CANCELLED)) {
												sendConsolidationMsgToROSS(env, elmOrderLine);
											}
										}
									}
								}
							}
						} else {
						}
						obtOLs = true;
					}
				}
			}
		} else {
		}
		if (noOfUTFOrCancelledLines > 0) {
			String noOfOLs = elmOLs.getAttribute(NWCGConstants.TOTAL_NUMBER_OF_RECORDS);
			if (noOfOLs.indexOf(".") != -1) {
				noOfOLs = noOfOLs.substring(0, noOfOLs.indexOf("."));
				logger.verbose("@@@@@ noOfOLs :: " + noOfOLs);
			}
			int totalOLs = new Integer(noOfOLs).intValue();
			if (totalOLs == noOfUTFOrCancelledLines) {
				// All the lines in this issue has been UTFed. ICBSR System is closing the alert automatically
				logger.verbose("@@@@@ Automatically resolved by ICBSR. All the lines on this issue are in UTF status...");
				String closeReason = "Automatically resolved by ICBSR. All the lines on this issue are in UTF status";
				NWCGCloseAlert.closeAlert(env, orderHdrKey, "PlaceResourceRequestExternalReq", closeReason);
			}
		}
		logger.verbose("@@@@@ Exiting NWCGCheckAndSendCancelUTFLine::checkCancelUTFAndSendMsgToROSS @@@@@");
		return docIP;
	}

	/**
	 * This method will formulate the XML needed as per fill message and will
	 * send it to ROSS
	 * 
	 * @param env
	 * @param elmOL
	 */
	private void sendMsgToROSS(YFSEnvironment env, Element elmOL) {
		logger.verbose("@@@@@ Entering NWCGCheckAndSendCancelUTFLine::sendMsgToROSS @@@@@");
		String reqNo = ((Element) elmOL.getElementsByTagName(NWCGConstants.EXTN).item(0)).getAttribute(NWCGConstants.EXTN_REQUEST_NO);
		NWCGNFESResourceRequest nfesResReq = new NWCGNFESResourceRequest("ro:UpdateNFESResourceRequestReq");
		nfesResReq.setDocAttributes(NWCGAAConstants.ENV_USER_ID, orderHdrKey, "Order", orderNo, null);
		nfesResReq.setMessageOriginator(shipNode);
		nfesResReq.setRequestKey(incNo, incYr, reqNo);
		elmOL.setAttribute("OrderNo", orderNo);
		if (orderCreateTS == null) {
			orderCreateTS = "";
		}
		elmOL.setAttribute("OrderCreatets", orderCreateTS);
		nfesResReq.populateFillDtlOrConsolidationDtl(elmOL, null, null);
		nfesResReq.populateAddressDtls(elmOrderExtn, elmPersonInfoShipTo);
		nfesResReq.populateShippingContactDtls(shippingContactName, shippingContactPhone);
		nfesResReq.populateSpecialNeeds();
		try {
			logger.verbose("@@@@@ nfesResReq.getNFESResourceRequestDocument() :: " + XMLUtil.extractStringFromDocument(nfesResReq.getNFESResourceRequestDocument()));
			CommonUtilities.invokeService(env, NWCGConstants.SVC_POST_OB_MSG_SVC, XMLUtil.extractStringFromDocument(nfesResReq.getNFESResourceRequestDocument()));
			logger.verbose("@@@@@ After Invoking NWCGPostOBMsgService...");
		} catch (Exception ex) {
			logger.error("!!!!! Caught General Exception :: " + ex);
		}
		logger.verbose("@@@@@ Exiting NWCGCheckAndSendCancelUTFLine::sendMsgToROSS @@@@@");
	}

	/**
	 * This method will send consolidation message to ROSS
	 * 
	 * @param env
	 * @param elmOrderLine
	 */
	private void sendConsolidationMsgToROSS(YFSEnvironment env, Element elmOL) {
		logger.verbose("@@@@@ Entering NWCGCheckAndSendCancelUTFLine::sendConsolidationMsgToROSS @@@@@");
		String reqNo = ((Element) elmOL.getElementsByTagName(NWCGConstants.EXTN).item(0)).getAttribute(NWCGConstants.EXTN_REQUEST_NO);
		NWCGNFESResourceRequest nfesResReq = new NWCGNFESResourceRequest("ro:UpdateNFESResourceRequestReq");
		nfesResReq.setDocAttributes(NWCGAAConstants.ENV_USER_ID, orderHdrKey, "Order", orderNo, null);
		nfesResReq.setMessageOriginator(shipNode);
		nfesResReq.setRequestKey(incNo, incYr, reqNo);
		elmOL.setAttribute("OrderNo", orderNo);
		if (orderCreateTS == null) {
			orderCreateTS = "";
		}
		elmOL.setAttribute("OrderCreatets", orderCreateTS);
		nfesResReq.setCancelledNote("This request is cancelled by ICBSR user:" + env.getUserId());
		nfesResReq.createConsolidationElement(elmOL);
		nfesResReq.populateAddressDtls(elmOrderExtn, elmPersonInfoShipTo);
		nfesResReq.populateShippingContactDtls(shippingContactName, shippingContactPhone);
		nfesResReq.populateSpecialNeeds();
		try {
			logger.verbose("@@@@@ nfesResReq.getNFESResourceRequestDocument() :: " + XMLUtil.extractStringFromDocument(nfesResReq.getNFESResourceRequestDocument()));
			CommonUtilities.invokeService(env, NWCGConstants.SVC_POST_OB_MSG_SVC, XMLUtil.extractStringFromDocument(nfesResReq.getNFESResourceRequestDocument()));
			logger.verbose("@@@@@ After Invoking NWCGPostOBMsgService...");
		} catch (Exception ex) {
			logger.error("!!!!! Caught General Exception :: " + ex);
		}
		logger.verbose("@@@@@ Entering NWCGCheckAndSendCancelUTFLine::sendConsolidationMsgToROSS @@@@@");
	}

	/**
	 * This method sets order related attributes to class variables
	 * 
	 * @param elmOrder
	 */
	private void setOrderAttributes(Element elmOrderDtls) {
		logger.verbose("@@@@@ Entering NWCGCheckAndSendCancelUTFLine::setOrderAttributes @@@@@");
		orderNo = elmOrderDtls.getAttribute(NWCGConstants.ORDER_NO);
		orderHdrKey = elmOrderDtls.getAttribute(NWCGConstants.ORDER_HEADER_KEY);
		shipNode = elmOrderDtls.getAttribute(NWCGConstants.SHIP_NODE);
		orderCreateTS = elmOrderDtls.getAttribute("Createts");
		NodeList nlOrderChildNodes = elmOrderDtls.getChildNodes();
		if (nlOrderChildNodes != null && nlOrderChildNodes.getLength() > 0) {
			for (int i = 0; i < nlOrderChildNodes.getLength(); i++) {
				Node tmpNode = nlOrderChildNodes.item(i);
				if (tmpNode.getNodeType() == Node.ELEMENT_NODE) {
					Element elmIsExtn = (Element) tmpNode;
					if (elmIsExtn.getNodeName().equals("Extn")) {
						incNo = elmIsExtn.getAttribute(NWCGConstants.INCIDENT_NO);
						incYr = elmIsExtn.getAttribute(NWCGConstants.INCIDENT_YEAR);
						shippingContactName = elmIsExtn.getAttribute(NWCGConstants.SHIP_CONTACT_NAME_ATTR);
						shippingContactPhone = elmIsExtn.getAttribute(NWCGConstants.SHIP_CONTACT_PHONE_ATTR);
						elmOrderExtn = elmIsExtn;
					} else if (elmIsExtn.getNodeName().equals("PersonInfoShipTo")) {
						elmPersonInfoShipTo = elmIsExtn;
					}
				}
			}
		}
		logger.verbose("@@@@@ Exiting NWCGCheckAndSendCancelUTFLine::setOrderAttributes @@@@@");
	}

	/**
	 * DETERMING WHETHER THE LINE IS CREATED DUE TO PARTIAL SUBSTITUTION - Check
	 * if the request no has a dot in it (S-1.2). If not, it is a UTF by a
	 * manual user action - If there is a dot, then make getOrderLineList as
	 * below Input Request No = S-1 (Remove the last dot. If it is S-1.2.1, then
	 * the input request will be S-1.2) Input XML <OrderLine
	 * OrderHeaderKey="201004141410074018626"> <Extn ExtnRequestNo="S-1"/> //
	 * Request No = Remove the last dot and number <Item
	 * ItemID="Item ID of the current line"/> </OrderLine> If it returns any
	 * entry, then it means that the current line is created as part of partial
	 * substitution. Do not send this request to ROSS
	 * 
	 * @param env
	 * @param reqNo
	 * @return
	 */
	private boolean isOLPartiallySubstituted(YFSEnvironment env, String reqNo, String itemID) {
		logger.verbose("@@@@@ Entering NWCGCheckAndSendCancelUTFLine::isOLPartiallySubstituted @@@@@");
		boolean isPartiallySubstituted = false;
		if (reqNo.indexOf(".") == -1) {
			isPartiallySubstituted = false;
			return isPartiallySubstituted;
		}
		try {
			Document docGetOLListIP = XMLUtil.createDocument("OrderLine");
			Element elmGetOL = docGetOLListIP.getDocumentElement();
			elmGetOL.setAttribute("OrderHeaderKey", orderHdrKey);
			Element elmOLExtn = docGetOLListIP.createElement("Extn");
			String targetReqNo = reqNo.substring(0, reqNo.lastIndexOf("."));
			elmGetOL.appendChild(elmOLExtn);
			elmOLExtn.setAttribute("ExtnRequestNo", targetReqNo);
			Element elmItem = docGetOLListIP.createElement("Item");
			elmGetOL.appendChild(elmItem);
			elmItem.setAttribute("ItemID", itemID);
			Document docGetOLListOP = CommonUtilities.invokeAPI(env, "NWCGEnhancedOrderMonitor_getOrderLineList", "getOrderLineList", docGetOLListIP);
			String matchingLines = docGetOLListOP.getDocumentElement().getAttribute("TotalLineList");
			if (matchingLines == null || matchingLines.trim().length() < 1) {
				isPartiallySubstituted = false;
			} else {
				int noOfLines = new Integer(matchingLines).intValue();
				if (noOfLines > 0) {
					isPartiallySubstituted = true;
				} else {
					isPartiallySubstituted = false;
				}
			}
		} catch (ParserConfigurationException pce) {
			logger.error("!!!!! ParserConfigurationException :: " + pce);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
		}
		logger.verbose("@@@@@ Exiting NWCGCheckAndSendCancelUTFLine::isOLPartiallySubstituted @@@@@");
		return isPartiallySubstituted;
	}

	/**
	 * This method will check if it is a Full UTF line or not - Requested
	 * Quantity = UTF Quantity - ConditionVariable2 is NULL or not equal to subs
	 * or cons or retrived - Not partially substituted line
	 * 
	 * @param env
	 * @param elmOrderLine
	 * @return
	 */
	private boolean isFullUTFLine(YFSEnvironment env, Element elmOrderLine) {
		logger.verbose("@@@@@ Entering NWCGCheckAndSendCancelUTFLine::isFullUTFLine @@@@@");
		// getting the lineStatus, need to check for a "cancelled" request later
		String lineStatus = elmOrderLine.getAttribute(NWCGConstants.MAX_LINE_STATUS);
		if (lineStatus == null || lineStatus.trim().length() < 2) {
			lineStatus = elmOrderLine.getAttribute(NWCGConstants.MIN_LINE_STATUS);
		}
		boolean isFullUTF = false;
		Element elmExtnOL = (Element) elmOrderLine.getElementsByTagName(NWCGConstants.EXTN).item(0);
		String origReqQty = elmExtnOL.getAttribute("ExtnOrigReqQty");
		String utfQty = elmExtnOL.getAttribute("ExtnUTFQty");
		if (origReqQty == null || origReqQty.trim().length() < 1) {
			origReqQty = "0";
		}
		if (utfQty == null || utfQty.trim().length() < 1) {
			utfQty = "0";
		}
		float fltOrigReqQty = new Float(origReqQty).floatValue();
		float fltUTFQty = new Float(utfQty).floatValue();
		// Backordered or Forwarded - then this will not match
		if (fltOrigReqQty == fltUTFQty) {
			String condVar2 = elmOrderLine.getAttribute("ConditionVariable2");
			if (condVar2 != null) {
				// checking for a regular substitution, not a UTF-ed substitution (a UTF-ed substitution will still have an empty string for the condition variable)
				if (!(condVar2.contains(NWCGConstants.CONDVAR2_XLD_REASON_CONS_SUBSTRING))) {
					condVar2 = condVar2.trim();
					// checking for the "surviving" consolidated request, its condition variable will now be unique
					if (!(condVar2.equals(NWCGConstants.CONDVAR2_XLD_REASON_CONS_NOPROCESSING))) {
						// checking for a UTF-ed (non-cancelled) request
						if (!(lineStatus != null && lineStatus.equals(NWCGConstants.STATUS_CANCELLED))) {
							isFullUTF = true;
							String reqNo = elmExtnOL.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
							Element elmOLItem = (Element) elmOrderLine.getElementsByTagName(NWCGConstants.ITEM).item(0);
							String itemID = elmOLItem.getAttribute("ItemID");
							isFullUTF = !isOLPartiallySubstituted(env, reqNo, itemID);
						}
					}
				}
			}
		}
		logger.verbose("@@@@@ Exiting NWCGCheckAndSendCancelUTFLine::isFullUTFLine @@@@@");
		return isFullUTF;
	}
}