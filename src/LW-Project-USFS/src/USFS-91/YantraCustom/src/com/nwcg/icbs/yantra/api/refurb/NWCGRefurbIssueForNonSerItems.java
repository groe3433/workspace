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

package com.nwcg.icbs.yantra.api.refurb;

import java.util.Hashtable;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGRefurbIssueForNonSerItems implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGRefurbIssueForNonSerItems.class);
	
	private Properties _properties;

	public void setProperties(Properties prop) throws Exception {
		_properties = prop;
	}

	/**
	 * - Create the refurb issue only if quantity completed and quantity
	 * requested are same. - Update the cost of refurb issue on refurb work
	 * order - Change the issue status
	 * 
	 * @param env
	 * @param inputDoc
	 * @return
	 * @throws NWCGException
	 */
	public Document checkAndCreateRefurbIssue(YFSEnvironment env,
			Document inputDoc) throws NWCGException, Exception {
		try {
			logger.verbose("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Entered");
			if (logger.isVerboseEnabled()) {
				logger.verbose("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, inputDoc : "
						+ XMLUtil.getXMLString(inputDoc));
			}
			Element rootNode = inputDoc.getDocumentElement();
			String qtyCmpl = XMLUtil
					.getAttribute(rootNode, "QuantityCompleted");
			;
			String qtyReq = XMLUtil.getAttribute(rootNode, "QuantityRequested");
			logger.verbose("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Qty Cmpl : "
					+ qtyCmpl + ", Qty Req : " + qtyReq);
			if (qtyCmpl.equalsIgnoreCase(qtyReq)) {
				logger.verbose("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Creating Input Refurb Issue Doc");

				Document inputRfbIssueDoc = createRefurbIssueDocument(env,
						inputDoc);

				Document rfbIssueOutputDoc = null;// Jay: we shouldnt assing it
													// a blank document
													// XMLUtil.newDocument();

				logger.verbose("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Invoking Create Order : "
						+ NWCGConstants.API_CREATE_ORDER);

				if (inputRfbIssueDoc != null) {
					rfbIssueOutputDoc = CommonUtilities.invokeAPI(env,
							"NWCGRefurbIssue_createOrder",
							NWCGConstants.API_CREATE_ORDER, inputRfbIssueDoc);
				}

				if (rfbIssueOutputDoc != null) {
					// Update the workorder on creation of workorder.
					NWCGRefurbWOCost refurbWOCost = new NWCGRefurbWOCost();
					logger.verbose("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Updating Refurb WO Cost");
					Document updtWOPrice = refurbWOCost.updateWORefurbCost(env,
							rfbIssueOutputDoc);
					logger.verbose("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Creating input status doc");
					Document chgStatusDoc = createChgIssueStatusDoc(rfbIssueOutputDoc);
					// Changing the status to an extended status
					logger.verbose("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Changing the status : "
							+ XMLUtil.getXMLString(chgStatusDoc));
					Document chgStatusOutput = CommonUtilities
							.invokeAPI(env,
									NWCGConstants.API_CHANGE_ORDER_STATUS,
									chgStatusDoc);
					logger.verbose("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Changed the status");
				} else {
					logger.verbose("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Unable to create refurb issue as order document is NULL ");
				}
			} else {
				logger.verbose("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Quantity Completed and Requested are different");
				// throw new NWCGException("NWCG_RFB_CREATE_ISSUE_001", new
				// String[] {qtyCmpl, qtyReq});
			}
		} catch (ParserConfigurationException pce) {
			logger.error("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Parser Conf Exc : "
					+ pce.getMessage());
			logger.error("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Parser StackTrace : "
					+ pce.getStackTrace());
			throw new NWCGException(pce);
		} catch (Exception e) {
			logger.error("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Exception Message : "
					+ e.getMessage());
			logger.error("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, StackTrace : "
					+ e.getStackTrace());
			// Jay: dont mask the exception !!!!!!!!!
			throw e;
		}
		logger.verbose("NWCGRefurbIssueForNonSerItems::checkAndCreateRefurbIssue, Exiting");
		return inputDoc;
	}

	/**
	 * This method builds the input xml for createOrder API.
	 * 
	 * @param env
	 * @param skuOPDoc
	 * @return
	 * @throws NWCGException
	 */
	private Document createRefurbIssueDocument(YFSEnvironment env,
			Document skuOPDoc) throws NWCGException {
		try {
			logger.verbose("NWCGRefurbIssueForNonSerItems::createRefurbIssueDocument, Entered");
			Document orderIssueDoc = XMLUtil.newDocument();
			Element rootNode = skuOPDoc.getDocumentElement();
			String nodeKey = XMLUtil.getAttribute(rootNode, "NodeKey");
			String enterpriseCode = XMLUtil.getAttribute(rootNode,
					"EnterpriseCode");
			String noOfUnits = XMLUtil.getAttribute(rootNode,
					"QuantityRequested");
			double reqQty = new Double(noOfUnits).doubleValue();

			String workOrderNo = XMLUtil.getAttribute(rootNode, "WorkOrderNo");

			Element orderElm = orderIssueDoc.createElement("Order");
			orderIssueDoc.appendChild(orderElm);
			orderElm.setAttribute("DocumentType",
					NWCGConstants.ORDER_DOCUMENT_TYPE);
			orderElm.setAttribute("ApplyDefaultTemplate", NWCGConstants.YES);
			orderElm.setAttribute("CreatedAtNode", NWCGConstants.YES);
			orderElm.setAttribute("CreatedByNode", nodeKey);
			orderElm.setAttribute("EnterpriseCode", enterpriseCode);
			orderElm.setAttribute("IgnoreOrdering", NWCGConstants.YES);
			orderElm.setAttribute("DraftOrderFlag", NWCGConstants.NO);
			orderElm.setAttribute("OrderType",
					NWCGConstants.REFURBISHMENT_ORDER_TYPE);
			orderElm.setAttribute("SellerOrganizationCode", enterpriseCode);
			orderElm.setAttribute("BuyerOrganizationCode", nodeKey);
			orderElm.setAttribute("ShipNode", nodeKey);

			Element orderLinesElm = orderIssueDoc.createElement("OrderLines");
			NodeList orderLinesList = XPathUtil
					.getNodeList(rootNode,
							"/WorkOrder/WorkOrderActivityDtl/WorkOrderComponents/WorkOrderComponent");
			if (orderLinesList == null || orderLinesList.getLength() <= 0) {
				// Returning null as we dont want to create an issue for work
				// order without any components
				return null;
			}

			for (int i = 0; i < orderLinesList.getLength(); i++) {
				Element tempOrderLine = (Element) orderLinesList.item(i);
				String cmpQty = tempOrderLine.getAttribute("ComponentQuantity");
				double qty = new Double(cmpQty).doubleValue();
				double totalQty = reqQty * qty;
				String strTotalQty = new Double(totalQty).toString();

				String uom = tempOrderLine.getAttribute("Uom");
				String itemID = tempOrderLine.getAttribute("ItemID");
				String prodClass = tempOrderLine.getAttribute("ProductClass");

				Element orderLineElm = orderIssueDoc.createElement("OrderLine");
				orderLineElm.setAttribute("ShipNode", nodeKey);

				Element orderLineTranQtyElm = orderIssueDoc
						.createElement("OrderLineTranQuantity");
				orderLineTranQtyElm.setAttribute("OrderedQty", strTotalQty);
				orderLineTranQtyElm.setAttribute("TransactionalUOM", uom);
				orderLineElm.appendChild(orderLineTranQtyElm);

				Element itemElm = orderIssueDoc.createElement("Item");
				itemElm.setAttribute("ItemID", itemID);
				itemElm.setAttribute("ProductClass", prodClass);
				orderLineElm.appendChild(itemElm);

				orderLinesElm.appendChild(orderLineElm);
			}
			orderElm.appendChild(orderLinesElm);

			// Get Incident Number, FS Acct Code, BLM Acct Code, Other Acct Code
			String workOrderKey = XMLUtil
					.getAttribute(rootNode, "WorkOrderKey");
			Hashtable extnDtls = getIncidentDetails(env, workOrderKey);
			Element extnElm = orderIssueDoc.createElement("Extn");
			extnElm.setAttribute("ExtnRefurbWO", workOrderNo);
			if (extnDtls != null && extnDtls.size() > 0) {
				String extnIncidentNo = (String) extnDtls.get("ExtnIncidentNo");
				extnElm.setAttribute("ExtnIncidentNo", extnIncidentNo);
				extnElm.setAttribute("ExtnFsAcctCode",
						(String) extnDtls.get("ExtnFsAcctCode"));
				extnElm.setAttribute("ExtnBlmAcctCode",
						(String) extnDtls.get("ExtnBlmAcctCode"));
				extnElm.setAttribute("ExtnOtherAcctCode",
						(String) extnDtls.get("ExtnOtherAcctCode"));
				orderElm.setAttribute("BillToID",
						(String) extnDtls.get("IncidentCustomerId"));
			}
			orderElm.appendChild(extnElm);

			if (logger.isVerboseEnabled()) {
				logger.verbose("NWCGRefurbIssueForNonSerItems::createRefurbIssueDocument, Order XML : "
						+ XMLUtil.getXMLString(orderIssueDoc));
			}
			logger.verbose("NWCGRefurbIssueForNonSerItems::createRefurbIssueDocument, Returning");
			return orderIssueDoc;
		} catch (ParserConfigurationException pce) {
			logger.error("NWCGRefurbIssueForNonSerItems::createRefurbIssueDocument, Parser Conf Exc : "
					+ pce.getMessage());
			logger.error("NWCGRefurbIssueForNonSerItems::createRefurbIssueDocument, Parser StackTrace : "
					+ pce.getStackTrace());
			throw new NWCGException(pce);
		} catch (Exception e) {
			logger.error("NWCGRefurbIssueForNonSerItems::createRefurbIssueDocument, Exception Message : "
					+ e.getMessage());
			logger.error("NWCGRefurbIssueForNonSerItems::createRefurbIssueDocument, StackTrace : "
					+ e.getStackTrace());
			throw new NWCGException(e);
		}
	}

	/**
	 * This method gets the incident related information (Incident No, FS, BLM
	 * and Other Account Code from getWorkOrderDetails API call.
	 * 
	 * @param env
	 * @param workOrderKey
	 * @return
	 */
	private Hashtable getIncidentDetails(YFSEnvironment env, String workOrderKey) {
		Hashtable result = new Hashtable();
		try {
			Document inputDoc = XMLUtil.getDocument();
			Element workOrderElem = inputDoc.createElement("WorkOrder");
			inputDoc.appendChild(workOrderElem);
			workOrderElem.setAttribute("WorkOrderKey", workOrderKey);
			if (logger.isVerboseEnabled()) {
				logger.verbose("NWCGRefurbIssueForNonSerItems::getIncidentDetails, Input XML : "
						+ XMLUtil.getXMLString(inputDoc));
			}

			Document outputTemplate = XMLUtil.getDocument();
			Element woElem = outputTemplate.createElement("WorkOrder");
			outputTemplate.appendChild(woElem);
			Element extnIncidentElem = outputTemplate.createElement("Extn");
			woElem.appendChild(extnIncidentElem);

			Document outputDoc = CommonUtilities.invokeAPI(env, outputTemplate,
					NWCGConstants.API_GET_WORK_ORDER_DTLS, inputDoc);

			if (outputDoc != null) {
				if (logger.isVerboseEnabled()) {
					logger.verbose("NWCGRefurbIssueForNonSerItems::getIncidentDetails, getWorkOrderDetails : "
							+ XMLUtil.getXMLString(outputDoc));
				}
				Element rootNode = outputDoc.getDocumentElement();
				Element extnElm = (Element) XMLUtil.getChildNodeByName(
						rootNode, "Extn");

				String incidentNo = extnElm.getAttribute("ExtnIncidentNo");
				result.put("ExtnIncidentNo", incidentNo);
				logger.verbose("NWCGRefurbIssueForNonSerItems::getIncidentDetails, Incident No : "
						+ incidentNo);
				String fsAcctCode = extnElm.getAttribute("ExtnFsAcctCode");
				result.put("ExtnFsAcctCode", fsAcctCode);
				String blmAcctCode = extnElm.getAttribute("ExtnBlmAcctCode");
				result.put("ExtnBlmAcctCode", blmAcctCode);
				String otherAcctCode = extnElm
						.getAttribute("ExtnOtherAcctCode");
				result.put("ExtnOtherAcctCode", otherAcctCode);
				String incidentCustomerId = getIncidentCustomerId(env,
						incidentNo);
				result.put("IncidentCustomerId", incidentCustomerId);
			} else {
				logger.verbose("NWCGRefurbIssueForNonSerItems::getIncidentDetails, NULL output from getWorkOrderDetails");
			}
		} catch (ParserConfigurationException pce) {
			logger.error("NWCGRefurbIssueForNonSerItems::getIncidentDetails, ParserConfigurationException Msg : "
					+ pce.getMessage());
			logger.error("NWCGRefurbIssueForNonSerItems::getIncidentDetails, StackTrace : "
					+ pce.getStackTrace());
		} catch (Exception e) {
			logger.error("NWCGRefurbIssueForNonSerItems::getIncidentDetails, Exception Msg : "
					+ e.getMessage());
			logger.error("NWCGRefurbIssueForNonSerItems::getIncidentDetails, StackTrace : "
					+ e.getStackTrace());
		}
		return result;
	}

	/**
	 * This methods build the input xml string for changing the refurb issue
	 * status from CREATED to REFURB_COMPLETED.
	 * 
	 * @param rfbIssueOutputDoc
	 * @return
	 * @throws NWCGException
	 */
	private Document createChgIssueStatusDoc(Document rfbIssueOutputDoc)
			throws NWCGException {
		try {
			logger.verbose("NWCGRefurbIssueForNonSerItems::createChgIssueStatusDoc, Entered");
			Document chgStatusDoc = XMLUtil.newDocument();
			Element orderStatusChgElm = chgStatusDoc
					.createElement("OrderStatusChange");
			chgStatusDoc.appendChild(orderStatusChgElm);
			orderStatusChgElm.setAttribute("BaseDropStatus",
					NWCGConstants.RFB_CMPL_DROP_STATUS);
			orderStatusChgElm.setAttribute("DocumentType",
					NWCGConstants.ORDER_DOCUMENT_TYPE);

			Element rootNode = rfbIssueOutputDoc.getDocumentElement();
			String enterpriseCode = XMLUtil.getAttribute(rootNode,
					"EnterpriseCode");
			orderStatusChgElm.setAttribute("EnterpriseCode", enterpriseCode);

			String orderNo = XMLUtil.getAttribute(rootNode, "OrderNo");
			orderStatusChgElm.setAttribute("OrderNo", orderNo);

			orderStatusChgElm.setAttribute("ChangeForAllAvailableQty",
					NWCGConstants.YES);
			orderStatusChgElm.setAttribute("TransactionId",
					NWCGConstants.TRANSACTION_REFURB_STATUS_CHANGE);
			if (logger.isVerboseEnabled()) {
				logger.verbose("NWCGRefurbIssueForNonSerItems::createChgIssueStatusDoc, Input : "
						+ XMLUtil.getXMLString(chgStatusDoc));
			}
			logger.verbose("NWCGRefurbIssueForNonSerItems::createChgIssueStatusDoc, Exiting");
			return chgStatusDoc;
		} catch (ParserConfigurationException pce) {
			logger.error("NWCGRefurbIssueForNonSerItems::createChgIssueStatusDoc, Parser Conf Exc : "
					+ pce.getMessage());
			logger.error("NWCGRefurbIssueForNonSerItems::createChgIssueStatusDoc, Parser StackTrace : "
					+ pce.getStackTrace());
			throw new NWCGException(pce);
		} catch (Exception e) {
			logger.error("NWCGRefurbIssueForNonSerItems::createChgIssueStatusDoc, Exception Message : "
					+ e.getMessage());
			logger.error("NWCGRefurbIssueForNonSerItems::createChgIssueStatusDoc, StackTrace : "
					+ e.getStackTrace());
			throw new NWCGException(e);
		}
	}

	private String getIncidentCustomerId(YFSEnvironment env, String incidentNo) {
		String customerId = "";
		try {
			Document inputDoc = XMLUtil.getDocument();
			Element incidentOrderElm = inputDoc
					.createElement("NWCGIncidentOrder");
			inputDoc.appendChild(incidentOrderElm);
			incidentOrderElm.setAttribute("IncidentNo", incidentNo);
			if (logger.isVerboseEnabled()) {
				logger.verbose("NWCGRefurbIssueForNonSerItems::getIncidentCustomerId, Input XML : "
						+ XMLUtil.getXMLString(inputDoc));
			}

			Document outputDoc = CommonUtilities.invokeService(env,
					"NWCGGetIncidentOrderService", inputDoc);

			if (outputDoc != null) {
				if (logger.isVerboseEnabled()) {
					logger.verbose("NWCGRefurbIssueForNonSerItems::getIncidentCustomerId, NWCGGetIncidentOrderService : "
							+ XMLUtil.getXMLString(outputDoc));
				}
				Element rootNode = outputDoc.getDocumentElement();
				customerId = XMLUtil.getAttribute(rootNode, "CustomerId");
			} else {
				logger.verbose("NWCGRefurbIssueForNonSerItems::getIncidentCustomerId, NULL output from NWCGGetIncidentOrderService");
			}
		} catch (ParserConfigurationException pce) {
			logger.error("NWCGRefurbIssueForNonSerItems::getIncidentCustomerId, ParserConfigurationException Msg : "
					+ pce.getMessage());
			logger.error("NWCGRefurbIssueForNonSerItems::getIncidentCustomerId, StackTrace : "
					+ pce.getStackTrace());
		} catch (Exception e) {
			logger.error("NWCGRefurbIssueForNonSerItems::getIncidentCustomerId, Exception Msg : "
					+ e.getMessage());
			logger.error("NWCGRefurbIssueForNonSerItems::getIncidentCustomerId, StackTrace : "
					+ e.getStackTrace());
		}
		return customerId;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
