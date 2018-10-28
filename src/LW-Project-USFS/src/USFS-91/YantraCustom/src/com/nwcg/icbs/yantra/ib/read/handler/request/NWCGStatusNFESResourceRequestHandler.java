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

package com.nwcg.icbs.yantra.ib.read.handler.request;

import gov.nwcg.services.ross.common_types._1.CompositeIncidentKeyType;
import gov.nwcg.services.ross.resource_order._1.DeliverOperationResultsResp;
import gov.nwcg.services.ross.resource_order._1.StatusNFESResourceRequestReq;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.issue.util.NWCGNFESResourceRequest;
import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.ib.read.handler.NWCGMessageHandlerInterface;
import com.nwcg.icbs.yantra.ib.read.handler.util.NotificationCommonUtilities;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.webservice.ib.msg.NWCGDeliverOperationResultsIB;
import com.nwcg.icbs.yantra.webservice.util.NWCGJAXBContextWrapper;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGStatusNFESResourceRequestHandler implements NWCGMessageHandlerInterface {

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGStatusNFESResourceRequestHandler.class);

	public Document process(YFSEnvironment env, Document msgXML) throws NWCGException {
		logger.verbose("@@@@@ Entering NWCGStatusNFESResourceRequestHandler::process @@@@@");
		NWCGNFESResourceRequest statusNFESResourceRequestResp = new NWCGNFESResourceRequest("ro:StatusNFESResourceRequestResp");
		String strDistId = "", strRequestNo = "";
		try {
			logger.verbose("@@@@@ msgXML :: " + XMLUtil.extractStringFromDocument(msgXML));
			strDistId = msgXML.getDocumentElement().getAttribute(NWCGAAConstants.MDTO_DISTID);
			StatusNFESResourceRequestReq statusObj = null;
			NWCGJAXBContextWrapper wrapper = new NWCGJAXBContextWrapper();
			try {
				statusObj = (StatusNFESResourceRequestReq) wrapper.getObjectFromDocument(msgXML, new URL(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE));
			} catch (MalformedURLException mue) {
				logger.error("!!!!! Caught MalformedURLException :: " + mue);
				mue.printStackTrace();
			} catch (JAXBException jbe) {
				logger.error("!!!!! Caught JAXBException :: " + jbe);
				jbe.printStackTrace();
			}
			CompositeIncidentKeyType incKey = statusObj.getRequestKey().getNaturalResourceRequestKey().getIncidentKey();
			String strIncidentNo = incKey.getNaturalIncidentKey().getHostID() .getUnitIDPrefix() + "-" + incKey.getNaturalIncidentKey().getHostID().getUnitIDSuffix() + "-" + StringUtil.prepadStringWithZeros("" + incKey.getNaturalIncidentKey().getSequenceNumber(), 6);
			String strIncidentYear = incKey.getNaturalIncidentKey().getYearCreated();
			strRequestNo = NWCGConstants.NFES_SUPPLY_FULL_PFX + statusObj.getRequestKey().getNaturalResourceRequestKey().getRequestCode().getSequenceNumber();
			int reqNoLength = strRequestNo.length();
			logger.verbose("@@@@@ strIncidentNo " + strIncidentNo + " strIncidentYear " + strIncidentYear + " strRequestNo " + strRequestNo);
			// String strGetOrderLineList = "<OrderLine> <Extn
			// ExtnRequestNo='"+strRequestNo+"'/><Order><Extn
			// ExtnIncidentNo='"+strIncidentNo+"'
			// ExtnIncidentYear='"+strIncidentYear+"' /> </Order></OrderLine>";
			// call getOrderLineList with template
			// fetch first line
			// verify if any line is surviving line due to cons or subs, if
			// true, continue with next line
			// do not send line to NWCGNFESResourceRequest if line count > 1 and
			// current line status = cancelled due to subs/cons
			// call getShipmentLineList API for order line key
			// call NWCGNFESResourceRequest with order line and shipment line
			// elements
			// continue with next order line
			// call NWCGNFESResourceRequest to set up AnA attrbutes
			// call DeliverOperationResults to deliver the result to ROSS
			Document docGetOrderLineListOP = null;
			try {
				docGetOrderLineListOP = CommonUtilities.getOLsForABaseReqInAnInc(env, strIncidentNo, strIncidentYear, strRequestNo, "NWCGStatusNFESResourceRequestHandler_getOrderLineList");
				logger.verbose("@@@@@ OP docGetOrderLineListOP " + XMLUtil.extractStringFromDocument(docGetOrderLineListOP));
			} catch (Exception e) {
				logger.error("!!!!! Caught General Exception :: " + e);
				e.printStackTrace();
			}
			if (docGetOrderLineListOP != null) {
				NodeList nlOrderLine = docGetOrderLineListOP.getDocumentElement().getElementsByTagName("OrderLine");
				int iOrderLineCount = nlOrderLine.getLength();
				if (nlOrderLine == null || iOrderLineCount <= 0) {
					StringBuffer strBuf = new StringBuffer("Unable to locate Request ");
					strBuf.append(strRequestNo).append(" on Incident ").append(strIncidentNo).append(" Year ").append(strIncidentYear);
					statusNFESResourceRequestResp.setFailureResponseStatus(NWCGConstants.NWCG_MSG_CODE_STATUS_E_1, strBuf.toString());
				} else {
					Hashtable<String, String> htReqNo2ItemID = new Hashtable<String, String>();
					Hashtable<String, String> htOLKey2ReqNo = new Hashtable<String, String>();
					// Create the base request to item id hashtable
					for (int i = 0; i < iOrderLineCount; i++) {
						Element elmOL = (Element) nlOrderLine.item(i);
						NodeList nlOLChilds = elmOL.getChildNodes();
						String reqNo = "";
						String itemID = "";
						for (int olChilds = 0; olChilds < nlOLChilds.getLength(); olChilds++) {
							Node nodeTmp = nlOLChilds.item(olChilds);
							if (nodeTmp.getNodeType() == Node.ELEMENT_NODE) {
								Element elmTmp = (Element) nodeTmp;
								if (elmTmp.getNodeName().equalsIgnoreCase("Extn")) {
									reqNo = elmTmp.getAttribute(NWCGConstants.EXTN_REQUEST_NO);
								} else if (elmTmp.getNodeName().equalsIgnoreCase("Item")) {
									itemID = elmTmp.getAttribute(NWCGConstants.ITEM_ID);
								}
							}
						}
						if (reqNo.length() > 0 && itemID.length() > 0) {
							htReqNo2ItemID.put(reqNo, itemID);
						}
						htOLKey2ReqNo.put(elmOL.getAttribute("OrderLineKey"), reqNo);
					}
					Element elemOrder = null, elemExtn = null, elemPersonInfoShipTo = null;
					boolean setData = false;
					for (int index = 0; index < iOrderLineCount; index++) {
						Element elemOrderLine = (Element) nlOrderLine.item(index);
						String strOrderLineKey = elemOrderLine.getAttribute("OrderLineKey");
						String processingReqNo = htOLKey2ReqNo.get(strOrderLineKey);
						if (processingReqNo.length() > reqNoLength) {
							if (processingReqNo.charAt(reqNoLength) != '.') {
								logger.verbose("@@@@@ " + processingReqNo + " is not a subs/cons/forward/backorder of " + strRequestNo);
								continue;
							}
						}
						// code to check the issue line with surviving request #
						if (iOrderLineCount > 1) {
							// check by max line status or min line status if any one exists then continue with next order line
							String strMaxLineStatus = elemOrderLine.getAttribute("MaxLineStatus");
							if (strMaxLineStatus != null && (strMaxLineStatus.equals(NWCGConstants.STATUS_CANCELLED_DUE_TO_CONS) || strMaxLineStatus.equals(NWCGConstants.STATUS_CANCELLED_DUE_TO_SUBS))) {
								// since this is surviving line (request# is reused) we dont need to send this line to NWCGNFESResourceRequest
								logger.verbose("@@@@@ " + strOrderLineKey + " is surviving line in cancelled due to cons or subs status");
								continue;
							}
							String strMinLineStatus = elemOrderLine.getAttribute("MinLineStatus");
							if (strMinLineStatus != null && (strMinLineStatus.equals(NWCGConstants.STATUS_CANCELLED_DUE_TO_CONS) || strMinLineStatus.equals(NWCGConstants.STATUS_CANCELLED_DUE_TO_SUBS))) {
								// since this is surviving line (request# is reused) we dont need to send this line to NWCGNFESResourceRequest
								logger.verbose("@@@@@ " + strOrderLineKey + " is surviving line in cancelled due to cons or subs status");
								continue;
							}
							// Sunjay 10/19 - If the line is in cancelled due to
							// substitution, then we are not getting
							// MaxLineStatus or MinLineStatus from the
							// template. If the request is made for a line that
							// is cancelled due to some other reason (other than
							// sub or cons), then
							// the code will not come to this statement as the
							// number of order lines with the same request no
							// will not be greater than 1
							// I tested for backordered, forwarded and UTF
							// lines. For all of them, the template returned
							// MaxLineStatus and MinLineStatus
							// The below check would be true only Cancelled Due
							// to Substitution
							if ((strMaxLineStatus == null) && (strMinLineStatus == null)) {
								logger.verbose("@@@@@ Template didn't return any status for the order line : " + strOrderLineKey);
								continue;
							}
							if ((strMaxLineStatus != null && strMaxLineStatus.trim().length() < 1) && (strMinLineStatus != null && strMinLineStatus.trim().length() < 1)) {
								logger.verbose("@@@@@ Status for the order line " + strOrderLineKey + " is empty");
								continue;
							}
						}
						// end code to check the issue line with surviving request#
						Element elemShipmentLine = null;
						// set these attribute only once
						if (!setData) {
							String strShipNode = elemOrderLine.getAttribute("ShipNode");
							// we have to place it under order line loop because we have to pass ship node in messager originator
							statusNFESResourceRequestResp.setRequestKey(strIncidentNo, strIncidentYear, strRequestNo);
							statusNFESResourceRequestResp.setMessageOriginator(strShipNode);
							setData = true;
						}
						try {
							Document docGetShipmentLineListOP = CommonUtilities.invokeAPI(env, "NWCGStatusNFESResourceRequestHandler_getShipmentLineList", "getShipmentLineList", "<ShipmentLine OrderLineKey='" + strOrderLineKey + "'/>");
							logger.verbose("@@@@@ docGetShipmentLineListOP " + XMLUtil.extractStringFromDocument(docGetShipmentLineListOP));
							NodeList nlShipmentLine = docGetShipmentLineListOP.getElementsByTagName("ShipmentLine");
							if (nlShipmentLine != null && nlShipmentLine.getLength() > 0) {
								elemShipmentLine = (Element) nlShipmentLine.item(0);
								Element elemShipmentExtn = (Element) elemShipmentLine.getElementsByTagName("Extn").item(0);
								elemShipmentLine.setAttribute("ExtnEstimatedArrivalDate", elemShipmentExtn.getAttribute("ExtnEstimatedArrivalDate"));
								elemShipmentLine.setAttribute("ExtnEstimatedDepartDate", elemShipmentExtn.getAttribute("ExtnEstimatedDepartDate"));
							}
						} catch (Exception e) {
							logger.error("!!!!! Caught General Exception :: " + e);
							e.printStackTrace();
						}
						// set the OrderNo attribute under OrderLine as required by NWCGNFESResourceRequest class
						NodeList nlOrder = elemOrderLine.getElementsByTagName("Order");
						if (nlOrder != null && nlOrder.getLength() > 0) {
							elemOrder = (Element) nlOrder.item(0);
							elemOrderLine.setAttribute("OrderNo", StringUtil.nonNull(elemOrder.getAttribute("OrderNo")));
							elemOrderLine.setAttribute("OrderCreatets", StringUtil.nonNull(elemOrder.getAttribute("Createts")));
						}
						// set fill detail or consolidation details element
						statusNFESResourceRequestResp.populateFillDtlOrConsolidationDtl(elemOrderLine, elemShipmentLine, htReqNo2ItemID);
						// this is the orderline record having latest order
						// (sorted based on order header key which is derived
						// from order create date)
						// this code will get executed only once, it is the last
						// Order i.e. the latest Order in system
						// Changing the way, we are getting the last element. We
						// might not come to this loop if it is the last request
						// because
						// the last request number might not be related to the
						// original request. If the request is for S-1, then
						// getOrderLineList
						// will get for S-10, S-11, etc. There is a code check
						// above which will return if the request not is not S-1
						// or S-1.x
						// So, the earlier check of if (i = lastindex -1)
						// doesn't work. We will be making this calls for all
						// the requests.
						// this code assumes that Order element will always have
						// Extn element, which is always true as we specify it
						// in the template
						elemExtn = (Element) ((Element) nlOrder.item(0)).getElementsByTagName("Extn").item(0);
						NodeList nlPersonInfoShipTo = ((Element) nlOrder.item(0)).getElementsByTagName("PersonInfoShipTo");
						if (nlPersonInfoShipTo != null && nlPersonInfoShipTo.getLength() > 0)
							elemPersonInfoShipTo = (Element) nlPersonInfoShipTo.item(0);
					}
					// set the address details, after exiting "for loop" the
					// elemExtn and elemPersonInfoShipTo will have latest order
					// info as it is already sorted by OrderHeaderKey
					statusNFESResourceRequestResp.populateAddressDtls(elemExtn, elemPersonInfoShipTo);
					// setup ShippingContactName and Phone
					statusNFESResourceRequestResp.populateShippingContactDtls(StringUtil.nonNull(elemExtn.getAttribute("ExtnShippingContactName")), StringUtil.nonNull(elemExtn.getAttribute("ExtnShippingContactPhone")));
					// this method should be called last as specialneeds is the
					// last element
					statusNFESResourceRequestResp.populateSpecialNeeds();
				}
			}
			try {
				statusNFESResourceRequestResp.setDocAttributes(null, null, "REQUEST", strRequestNo + "-" + strIncidentNo + "-" + strIncidentYear, strDistId);
				DeliverOperationResultsResp aaResponse = new NWCGDeliverOperationResultsIB().process(env, statusNFESResourceRequestResp.getNFESResourceRequestDocument());
			} catch (Exception e) {
				// dont need todo anything, exception will be handled as part of A&A
				logger.error("!!!!! Caught General Exception :: " + e);
				e.printStackTrace();
			}

		} catch (Exception ex) {
			statusNFESResourceRequestResp.setFailureResponseStatus(NWCGConstants.NWCG_MSG_CODE_STATUS_E_2, ex.getMessage());
			statusNFESResourceRequestResp.setDocAttributes(null, null, "REQUEST", strRequestNo, strDistId);
			try {
				DeliverOperationResultsResp aaResponse = new NWCGDeliverOperationResultsIB().process(env, statusNFESResourceRequestResp.getNFESResourceRequestDocument());
			} catch (Exception e) {
				logger.error("!!!!! Caught General Exception :: " + e);
				e.printStackTrace();
			}
			logger.error("!!!!! Caught General Exception :: " + ex);
			ex.printStackTrace();
		}
		try {
			logger.verbose("@@@@@ " + XMLUtil.extractStringFromDocument(statusNFESResourceRequestResp.getNFESResourceRequestDocument()));
		} catch (TransformerException te) {
			logger.error("!!!!! Caught TransformerException :: " + te);
			te.printStackTrace();
		}
		logger.verbose("@@@@@ Exiting NWCGStatusNFESResourceRequestHandler::process @@@@@");
		return statusNFESResourceRequestResp.getNFESResourceRequestDocument();
	}
}