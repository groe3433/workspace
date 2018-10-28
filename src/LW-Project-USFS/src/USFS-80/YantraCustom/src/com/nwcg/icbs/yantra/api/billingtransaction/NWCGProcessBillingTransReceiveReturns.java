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

package com.nwcg.icbs.yantra.api.billingtransaction;

import java.util.Properties;

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

public class NWCGProcessBillingTransReceiveReturns implements YIFCustomApi,
		NWCGBillingTransRecordMutator {
	
	private static Logger logger = Logger
			.getLogger(NWCGProcessBillingTransReceiveReturns.class.getName());

	public void setProperties(Properties arg0) throws Exception {
	}

	public void throwAlert(YFSEnvironment env, StringBuffer Message)
			throws Exception {
		Message.append(" ExceptionType='BILLING_TRANS_RETURNS'"
				+ " InboxType='" + NWCGConstants.NWCG_BILL_TRANS_INBOXTYPE
				+ "' QueueId='" + NWCGConstants.NWCG_BILL_TRANS_QUEUEID
				+ "' />");
		if (logger.isVerboseEnabled())
			logger.verbose("Throw Alert Method called with message:-"
					+ Message.toString());
		try {
			Document inTemplate = XMLUtil.getDocument(Message.toString());
			CommonUtilities.invokeAPI(env, "createException", inTemplate);
		} catch (Exception ex1) {
			throw new NWCGException(
					"NWCG_BILLING_TRANSACTION_ERROR_WHILE_LOGGING_ALERT");
		}
	}

	public Document InsertRecord(YFSEnvironment env, Document inXML)
			throws Exception {
		if (logger.isVerboseEnabled()) {
			logger
					.verbose("Entering NWCGProcessBillingTransReceiveReturns, Input document is:"
							+ XMLUtil.getXMLString(inXML));
		}
		String ReceiptNo = "";
		String ReceiptKey = "";
		String CacheId = "";
		try {
			Element curReceiptList, curReceiptLineList = null;
			inXML.getDocumentElement().normalize();
			Element elemshipmentRoot = inXML.getDocumentElement();
			if (elemshipmentRoot != null) {
				NodeList listOfReceipts = inXML.getElementsByTagName("Receipt");
				for (int i = 0; i < listOfReceipts.getLength(); i++) {
					Node RNode = listOfReceipts.item(i);
					if (RNode.getNodeType() == Node.ELEMENT_NODE) {
						curReceiptList = (Element) listOfReceipts.item(i);
						ReceiptNo = curReceiptList.getAttribute("ReceiptNo");
						String EnterpriseCode = curReceiptList
								.getAttribute("EnterpriseCode");
						String ReceiptDate = curReceiptList
								.getAttribute("ShipDate");
						ReceiptKey = curReceiptList
								.getAttribute("ReceiptHeaderKey");
						String DocumentType = curReceiptList
								.getAttribute("DocumentType");
						CacheId = curReceiptList.getAttribute("ReceivingNode");
						boolean isAccountSplit = checkIfAccountSplit(env,
								ReceiptKey);
						String ownerAgency = getOwnerAgency(env, CacheId);
						NodeList listOfReceiptLines = curReceiptList
								.getElementsByTagName("ReceiptLine");
						for (int j = 0; j < listOfReceiptLines.getLength(); j++) {
							Node RLNode = listOfReceiptLines.item(j);
							if (RLNode.getNodeType() == Node.ELEMENT_NODE) {
								curReceiptLineList = (Element) listOfReceiptLines
										.item(j);
								String DispositionCode = curReceiptLineList
										.getAttribute("DispositionCode");
								String strLPN = curReceiptLineList
										.getAttribute("PalletId");
								/*
								 * if (DispositionCode.equals("NRFI") && (strLPN ==
								 * null || strLPN.equals(""))) { // The NRFI Qty
								 * is not recorded as a Billing Transaction
								 * Record continue; }
								 */
								String strItemID = curReceiptLineList
										.getAttribute("ItemID");
								if (!strLPN.trim().equals("")
										&& strItemID.trim().equals(""))
									processLPN(env, strLPN, ReceiptNo,
											EnterpriseCode, ReceiptKey,
											DocumentType, CacheId, ReceiptDate,
											DispositionCode, isAccountSplit,
											ownerAgency);
								else
									insertBillingTrans(env, curReceiptLineList,
											ReceiptNo, EnterpriseCode,
											ReceiptKey, DocumentType, CacheId,
											ReceiptDate, DispositionCode, "",
											isAccountSplit, ownerAgency);
							} 
						}
					}
				}
			} 
		} catch (Exception e) {
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingTransReturns::InsertBillingTransactionRecord Caught Exception "
								+ e);
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransReturns' "
							+ "DetailDescription='InsertBillingTransactionRecord Failed for ReceiptNo = "
							+ ReceiptNo
							+ ", ReceiptHeaderKey = "
							+ ReceiptKey
							+ ", ReceivingNode = "
							+ CacheId
							+ ", During the billing transaction receive returns process'");
			throwAlert(env, stbuf);
		}
		return inXML;
	}

	private String getOwnerAgency(YFSEnvironment env, String cacheId)
			throws Exception {
		String opOrgListTemplate = "<OrganizationList><Organization OrganizationCode=\"\"><Extn ExtnOwnerAgency=\"\"/></Organization></OrganizationList>";
		Document opOrgListTemplateDoc = XMLUtil.getDocument(opOrgListTemplate);
		Document ipOrgListDoc = XMLUtil.createDocument("Organization");
		Element ipRootElement = ipOrgListDoc.getDocumentElement();
		ipRootElement.setAttribute("OrganizationCode", cacheId);
		Document opOrgListDoc = CommonUtilities.invokeAPI(env,
				opOrgListTemplateDoc, "getOrganizationList", ipOrgListDoc);
		Element extnOrgListElement = (Element) opOrgListDoc
				.getElementsByTagName("Extn").item(0);
		String ownerAgency = extnOrgListElement.getAttribute("ExtnOwnerAgency");
		return ownerAgency;
	}

	private boolean checkIfAccountSplit(YFSEnvironment env, String receiptKey)
			throws Exception {
		int numAccounts = 0;
		boolean isAccountSplit = false;
		String opTemplate = "<Receipt ReceiptHeaderKey=\"\"><Extn/></Receipt>";
		Document opTemplateDoc = XMLUtil.getDocument(opTemplate);
		Document ipDoc = XMLUtil.createDocument("Receipt");
		Element receiptRootElement = ipDoc.getDocumentElement();
		receiptRootElement.setAttribute("ReceiptHeaderKey", receiptKey);
		Document opDoc = CommonUtilities.invokeAPI(env, opTemplateDoc,
				"getReceiptDetails", ipDoc);
		Element extnElement = (Element) opDoc.getElementsByTagName("Extn")
				.item(0);
		for (int i = 1; i <= 5; i++) {
			String currentReturnPercentage = extnElement
					.getAttribute("ExtnReturnPercentage" + i);
			numAccounts = (currentReturnPercentage != null && (Double
					.parseDouble(currentReturnPercentage) > 0)) ? (numAccounts + 1)
					: numAccounts;
		}
		if (numAccounts > 1)
			isAccountSplit = true;
		return isAccountSplit;
	}

	public Document getBillingTransInputDocument(YFSEnvironment env)
			throws Exception {
		Document returnDoc = XMLUtil.createDocument("NWCGBillingTransaction");
		Element returnDocElem = returnDoc.getDocumentElement();
		returnDocElem.setAttribute(NWCGConstants.BILL_TRANS_TYPE, "RETURNS");
		return returnDoc;
	}

	public Document insertBillingTransRecord(YFSEnvironment env, Document doc)
			throws Exception {
		return InsertRecord(env, doc);
	}

	public String getUnitCost(YFSEnvironment env, String Cache, String ItemID,
			String IncidentNo, String IncidentYear, String IssueNo)
			throws Exception {
		Document inDoc = XMLUtil.newDocument();
		Document outDoc = XMLUtil.newDocument();
		Element el_NWCGIncidentReturn = inDoc
				.createElement("NWCGIncidentReturn");
		inDoc.appendChild(el_NWCGIncidentReturn);
		el_NWCGIncidentReturn.setAttribute("CacheID", Cache);
		el_NWCGIncidentReturn.setAttribute("IncidentNo", IncidentNo);
		el_NWCGIncidentReturn.setAttribute("IncidentYear", IncidentYear);
		el_NWCGIncidentReturn.setAttribute("IssueNo", IssueNo);
		el_NWCGIncidentReturn.setAttribute("ItemID", ItemID);
		outDoc = CommonUtilities.invokeService(env,
				NWCGConstants.NWCG_QUERY_LIST_ON_INCIDENT_TABLE_SERVICE, inDoc);
		Element outDocElem = outDoc.getDocumentElement();
		String UnitCost = outDocElem.getAttribute("UnitPrice");
		double rprice = 0.00;
		if (UnitCost.length() > 0) {
			rprice = Double.parseDouble(UnitCost);
		}
		if (!(rprice > 0)) {
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransReturns' "
							+ "DetailDescription='Unit Price is 0 for Incident No : "
							+ IncidentNo
							+ ",Incident Year : "
							+ IncidentYear
							+ ",Issue No :"
							+ IssueNo
							+ ",Item Id : "
							+ ItemID
							+ ", During the billing transaction receive returns process'");
			throwAlert(env, stbuf);
		}
		return UnitCost;
	}

	public void insertBillingTrans(YFSEnvironment env,
			Element curReceiptLineList, String ReceiptNo,
			String EnterpriseCode, String ReceiptKey, String DocumentType,
			String CacheId, String ReceiptDate, String DispositionCode,
			String serialNumber, boolean isAccountSplit, String ownerAgency)
			throws Exception {
		String ReceiptLineKey = curReceiptLineList
				.getAttribute("ReceiptLineKey");
		String ActualQty = curReceiptLineList.getAttribute("Quantity");
		String ItemId = curReceiptLineList.getAttribute("ItemID");
		String LocationId = curReceiptLineList.getAttribute("LocationId");
		String UOM = curReceiptLineList.getAttribute("UnitOfMeasure");
		String createUserId = curReceiptLineList.getAttribute("Createuserid");
		String modifyUserId = curReceiptLineList.getAttribute("Modifyuserid");
		Document docLPNReturn = null;
		String calPrice = curReceiptLineList.getAttribute("CalculatePrice");
		Document docBillingTransactionIP = getBillingTransInputDocument(env);
		Element BillingTransElem = docBillingTransactionIP.getDocumentElement();
		BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_NO, ReceiptNo);
		BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ENTERPRISE_CODE,
				EnterpriseCode);
		BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_DATE,
				ReceiptDate);
		BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_HEADER_KEY,
				ReceiptKey);
		BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_DOCUMENT_TYPE,
				DocumentType);
		BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_CACHE_ID,
				CacheId);
		BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LINE_KEY,
				ReceiptLineKey);
		BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_QTY, ActualQty);
		BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_ID, ItemId);
		BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_UOM, UOM);
		BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LOCATION_ID,
				LocationId);
		BillingTransElem.setAttribute(
				NWCGConstants.BILL_TRANS_DISPOSITION_CODE, DispositionCode);
		BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_CREATEUSERID,
				createUserId);
		BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_MODIFYUSERID,
				modifyUserId);
		// Item Details
		Document getItemDetailsIP = XMLUtil.createDocument("Item");
		Element GetItemDetailsIPElem = getItemDetailsIP.getDocumentElement();
		GetItemDetailsIPElem.setAttribute("ItemID", ItemId);
		GetItemDetailsIPElem.setAttribute("UnitOfMeasure", UOM);
		GetItemDetailsIPElem.setAttribute("OrganizationCode", EnterpriseCode);
		Document getItemDetailsOP = CommonUtilities.invokeAPI(env,
				"getItemDetails", getItemDetailsIP);
		Element GetItemDetailsOPElem = getItemDetailsOP.getDocumentElement();
		NodeList ItemList = GetItemDetailsOPElem
				.getElementsByTagName("ClassificationCodes");
		Element ItemListElem = (Element) ItemList.item(0);
		String ItemClass = StringUtil.nonNull(ItemListElem
				.getAttribute("TaxProductCode"));
		NodeList ItemList1 = GetItemDetailsOPElem
				.getElementsByTagName("PrimaryInformation");
		Element ItemListElem1 = (Element) ItemList1.item(0);
		String ItemDesc = ItemListElem1.getAttribute("Description");
		String ProductLine = ItemListElem1.getAttribute("ProductLine");
		String ProductClass = ItemListElem1.getAttribute("DefaultProductClass");
		// Incident Details
		Document getReceiptDetailsIP = XMLUtil.createDocument("Receipt");
		Element GetReceiptDetailsIPElem = getReceiptDetailsIP
				.getDocumentElement();
		GetReceiptDetailsIPElem.setAttribute("ReceiptHeaderKey", ReceiptKey);
		Document getReceiptDetailsOP = CommonUtilities.invokeAPI(env,
				"getReceiptDetails", getReceiptDetailsIP);
		Element IncidentElem = (Element) getReceiptDetailsOP
				.getElementsByTagName("Extn").item(0);
		Document getIncidentDetailsIP = XMLUtil
				.createDocument("NWCGIncidentOrder");
		Element GetIncidentDetailsIPElem = getIncidentDetailsIP
				.getDocumentElement();
		String strIncidentNo = StringUtil.nonNull(IncidentElem
				.getAttribute("ExtnIncidentNo"));
		String strIncidentYear = StringUtil.nonNull(IncidentElem
				.getAttribute("ExtnIncidentYear"));
		String strIssueNo = StringUtil.nonNull(IncidentElem
				.getAttribute("ExtnIssueNo"));
		String strUnitCost = "";
		if (calPrice != null && calPrice.equalsIgnoreCase("Y")) {
			strUnitCost = calPrice(env, ItemId, serialNumber, CacheId,
					strIncidentNo, strIncidentYear, ProductClass, UOM);
		} else {
			Element PriceElem = (Element) curReceiptLineList
					.getElementsByTagName("Extn").item(0);
			strUnitCost = StringUtil.nonNull(PriceElem
					.getAttribute("ExtnReceivingPrice"));
		}
		// String UnitCost =
		// getUnitCost(env,CacheId,ItemId,strIncidentNo,strIncidentYear,strIssueNo);
		BillingTransElem.setAttribute(
				NWCGConstants.BILL_TRANS_ITEM_DESCRIPTION, ItemDesc);
		BillingTransElem.setAttribute(
				NWCGConstants.BILL_TRANS_ITEM_CLASSIFICATION, ItemClass);
		BillingTransElem.setAttribute(
				NWCGConstants.BILL_TRANS_ITEM_PRODUCTLINE, ProductLine);
		BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_UNIT_COST,
				strUnitCost);
		double qty = Double.parseDouble(ActualQty);
		double rprice = 0;
		if (strUnitCost.length() > 0) {
			rprice = Double.parseDouble(strUnitCost);
		}
		double Tcost = qty * rprice;
		String TcostStr = Double.toString(Tcost);
		String QtyStr = Double.toString(qty);
		if (DispositionCode.equals("UNSERVICE")) {
			// Unserviceable Returned Items are not considered as either Credit
			// or Debit Transactions
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_AMOUNT,
					"0.00");
		} else {
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_AMOUNT,
					TcostStr);
		}
		String IncidentNo = "";
		String IncidentYear = "";
		String IncidentName = "";
		String ExtnFsAcctCode = "";
		String ExtnBlmAcctCode = "";
		String ExtnOtherAcctCode = "";
		String ExtnOverrideCode = "";
		if (strIssueNo.equals("")) {
			GetIncidentDetailsIPElem.setAttribute("IncidentNo", strIncidentNo);
			if (strIncidentYear.equals("") || strIncidentYear.length() == 0) {
				GetIncidentDetailsIPElem.setAttribute("Year", " ");
			} else {
				GetIncidentDetailsIPElem.setAttribute("Year", strIncidentYear);
			}
			Document getIncidentDetailsOP = CommonUtilities.invokeService(env,
					NWCGConstants.NWCG_GET_INCIDENT_ORDER_SERVICE,
					getIncidentDetailsIP);
			Element GetIncidentDetailsOPElem = getIncidentDetailsOP
					.getDocumentElement();
			IncidentNo = StringUtil.nonNull(GetIncidentDetailsOPElem
					.getAttribute("IncidentNo"));
			IncidentYear = StringUtil.nonNull(GetIncidentDetailsOPElem
					.getAttribute("IncidentYear"));
			IncidentName = StringUtil.nonNull(GetIncidentDetailsOPElem
					.getAttribute("IncidentName"));
			ExtnBlmAcctCode = StringUtil.nonNull(GetIncidentDetailsOPElem
					.getAttribute("IncidentBlmAcctCode"));
			ExtnFsAcctCode = StringUtil.nonNull(GetIncidentDetailsOPElem
					.getAttribute("IncidentFsAcctCode"));
			ExtnOverrideCode = StringUtil.nonNull(GetIncidentDetailsOPElem
					.getAttribute("OverrideCode"));
			ExtnOtherAcctCode = StringUtil.nonNull(GetIncidentDetailsOPElem
					.getAttribute("IncidentOtherAcctCode"));
		} else {
			Document getOrderDetailsIP = XMLUtil.createDocument("Order");
			Element GetOrderDetailsIPElem = getOrderDetailsIP
					.getDocumentElement();
			GetOrderDetailsIPElem.setAttribute("OrderNo", strIssueNo);
			GetOrderDetailsIPElem
					.setAttribute("EnterpriseCode", EnterpriseCode);
			// GetOrderDetailsIPElem.setAttribute("DocumentType","0001");
			Document getOrderDetailsOP = CommonUtilities.invokeAPI(env,
					"getOrderDetails", getOrderDetailsIP);
			Element GetOrderDetailsOPElem = getOrderDetailsOP
					.getDocumentElement();
			NodeList nlExtn = GetOrderDetailsOPElem
					.getElementsByTagName("Extn");
			Element elemExtn = (Element) nlExtn.item(0);
			// start of change for CR 1138 - Modified by JayP 10-20-2013
			String documentType = GetOrderDetailsOPElem
					.getAttribute("DocumentType");
			if (documentType.equalsIgnoreCase("0008.ex")) {
				// For Incident Transfer Orders we need to get values from
				// ExtnTo fields because ExtnIncidentNo is the "from-Incident"
				// in transfer.
				IncidentNo = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnToIncidentNo"));
				IncidentYear = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnToIncidentYear"));
				IncidentName = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnToIncidentName"));
				ExtnFsAcctCode = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnToFsAcctCode"));
				ExtnOverrideCode = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnToOverrideCode"));
				ExtnBlmAcctCode = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnToBlmAcctCode"));
				ExtnOtherAcctCode = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnToOtherAcctCode"));
			} else {
				// for all other orders we use default values.
				IncidentNo = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnIncidentNo"));
				IncidentYear = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnIncidentYear"));
				IncidentName = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnIncidentName"));
				ExtnFsAcctCode = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnFsAcctCode"));
				ExtnOverrideCode = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnOverrideCode"));
				ExtnBlmAcctCode = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnBlmAcctCode"));
				ExtnOtherAcctCode = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnOtherAcctCode"));
			}
			// end of change for CR 1138 - Modified by JayP 10-20-2013
		}
		BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_NO,
				IncidentNo);
		BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_YEAR,
				strIncidentYear);
		BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_NAME,
				IncidentName);
		BillingTransElem.setAttribute(
				NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE, ExtnFsAcctCode);
		BillingTransElem.setAttribute(
				NWCGConstants.BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE,
				ExtnOverrideCode);
		BillingTransElem.setAttribute(
				NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE,
				ExtnBlmAcctCode);
		BillingTransElem.setAttribute(
				NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE,
				ExtnOtherAcctCode);
		BillingTransElem.setAttribute(
				NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_ACCT_CODE,
				ExtnFsAcctCode);
		BillingTransElem.setAttribute(
				NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_OVERRIDE_CODE,
				ExtnOverrideCode);
		BillingTransElem.setAttribute(
				NWCGConstants.BILL_TRANS_LAST_INCIDENT_BLM_ACCT_CODE,
				ExtnBlmAcctCode);
		BillingTransElem.setAttribute(
				NWCGConstants.BILL_TRANS_LAST_INCIDENT_OTHER_ACCT_CODE,
				ExtnOtherAcctCode);
		// Just to make sure the values are set correctly for Billing XML
		String strLPN = curReceiptLineList.getAttribute("LPNNo");
		if (strLPN != null && !strLPN.equals("")) {
			String strSerialNo = curReceiptLineList.getAttribute("SerialNo");
			docLPNReturn = XMLUtil.createDocument("NWCGLpnReturn");
			Element eleInLPNReturn = docLPNReturn.getDocumentElement();
			eleInLPNReturn.setAttribute("LPNNo", strLPN);
			eleInLPNReturn.setAttribute("ReturnNo", ReceiptNo);
			eleInLPNReturn.setAttribute("ReturnHeaderKey", ReceiptKey);
			eleInLPNReturn.setAttribute("ReceiptDate", ReceiptDate);
			eleInLPNReturn.setAttribute("CacheID", CacheId);
			eleInLPNReturn.setAttribute("IncidentNo", IncidentNo);
			eleInLPNReturn.setAttribute("IncidentYear", strIncidentYear);
			eleInLPNReturn.setAttribute("ItemID", ItemId);
			eleInLPNReturn.setAttribute("UOM", UOM);
			eleInLPNReturn.setAttribute("UnitPrice", strUnitCost);
			eleInLPNReturn.setAttribute("ItemDescription", ItemDesc);
			eleInLPNReturn.setAttribute("ItemProductLine", ProductLine);
			eleInLPNReturn.setAttribute("ItemClassification", ItemClass);
			eleInLPNReturn.setAttribute("DispositionCode", DispositionCode);
			eleInLPNReturn.setAttribute("SerialNo", strSerialNo);
			eleInLPNReturn.setAttribute("Quantity", ActualQty);
			CommonUtilities.invokeService(env, "NWCGCreateLPNReturn",
					docLPNReturn);
		}
		try {
			if (!DispositionCode.equals("NRFI")) {
				if (isAccountSplit) {
					insertBillingTransactionWithSplitAccountCodeData(env,
							docBillingTransactionIP, getReceiptDetailsOP,
							ownerAgency);
				} else {
					CommonUtilities
							.invokeService(
									env,
									NWCGConstants.NWCG_CREATE_BILLING_TRANSACTION_SERVICE,
									docBillingTransactionIP);
				}
			}
		} catch (Exception e) {
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransReturns' "
							+ "DetailDescription='Insert Billing Transaction Record Failed for  Incident No : "
							+ IncidentNo
							+ ",Incident Year : "
							+ IncidentYear
							+ "Issue No :"
							+ strIssueNo
							+ "Item Id : "
							+ ItemId
							+ "Receipt No : "
							+ ReceiptNo
							+ ", During the billing transaction receive returns process'");
			throwAlert(env, stbuf);
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingTransReturns::InsertBillingTransactionRecord Caught Exception "
								+ e);
			e.printStackTrace();
		}
	}

	public void insertBillingTransactionWithSplitAccountCodeData(
			YFSEnvironment env, Document ipBillingTransactionDoc,
			Document receiptDetailsDoc, String ownerAgency) throws Exception {
		Element extnElement = (Element) receiptDetailsDoc.getElementsByTagName(
				"Extn").item(0);
		Element rootElement = (Element) ipBillingTransactionDoc
				.getDocumentElement();
		String strTransQty = rootElement.getAttribute("TransQty");
		String strUnitCost = rootElement.getAttribute("UnitCost");
		String dispositionCode = rootElement.getAttribute("DispositionCode");
		rootElement.setAttribute("IsAccountSplit", "Y");
		double transQty = 0;
		double unitCost = 0;
		if (strTransQty != null && strTransQty.trim().length() > 0)
			transQty = Double.parseDouble(strTransQty);
		if (strUnitCost != null && strUnitCost.trim().length() > 0)
			unitCost = Double.parseDouble(strUnitCost);
		for (int i = 1; i <= 5; i++) {
			String extnReturnPercentage = extnElement
					.getAttribute("ExtnReturnPercentage" + i);
			if (extnReturnPercentage != null
					&& Double.parseDouble(extnReturnPercentage) > 0) {
				String accountCode = extnElement
						.getAttribute("ExtnRefundBlmAcctCode" + i);
				double transAmount = (transQty * unitCost * Double
						.parseDouble(extnReturnPercentage)) / 100;
				transAmount = (dispositionCode.equals("UNSERVICE")) ? 0.00
						: transAmount;
				rootElement.setAttribute("TransAmount", Double
						.toString(transAmount));
				if (ownerAgency.equals("FS")) {
					rootElement.setAttribute(
							NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE,
							accountCode);
					rootElement
							.setAttribute(
									NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_ACCT_CODE,
									accountCode);
				} else if (ownerAgency.equals("BLM")) {
					rootElement.setAttribute(
							NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE,
							accountCode);
					rootElement
							.setAttribute(
									NWCGConstants.BILL_TRANS_LAST_INCIDENT_BLM_ACCT_CODE,
									accountCode);
				} else {
					rootElement.setAttribute(
							NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE,
							accountCode);
					rootElement
							.setAttribute(
									NWCGConstants.BILL_TRANS_LAST_INCIDENT_OTHER_ACCT_CODE,
									accountCode);
				}
				CommonUtilities.invokeService(env,
						NWCGConstants.NWCG_CREATE_BILLING_TRANSACTION_SERVICE,
						ipBillingTransactionDoc);
			}
		}
	}

	public void processLPN(YFSEnvironment env, String LPN, String ReceiptNo,
			String EnterpriseCode, String ReceiptKey, String DocumentType,
			String CacheId, String ReceiptDate, String DispositionCode,
			boolean isAccountSplit, String ownerAgency) throws Exception {
		Document inDoc = XMLUtil.createDocument("GetLPNDetails");
		Element eleRoot = inDoc.getDocumentElement();
		eleRoot.setAttribute("Node", CacheId);
		Element eleLPN = inDoc.createElement("LPN");
		eleRoot.appendChild(eleLPN);
		eleLPN.setAttribute("PalletId", LPN);
		Document outDoc = CommonUtilities
				.invokeAPI(env, "getLPNDetails", inDoc);
		Element eleOutRoot = outDoc.getDocumentElement();
		String strLocation = ((Element) eleOutRoot.getElementsByTagName(
				"LPNLocation").item(0)).getAttribute("LocationId");
		NodeList lsInveDetailList = eleOutRoot
				.getElementsByTagName("ItemInventoryDetail");
		for (int i = 0; i < lsInveDetailList.getLength(); i++) {
			String strSerialNo = "";
			Element eleInvDetail = (Element) lsInveDetailList.item(i);
			String strQuant = eleInvDetail.getAttribute("Quantity");
			DispositionCode = eleInvDetail.getAttribute("InventoryStatus");
			Element eleInventoryItem = (Element) eleInvDetail
					.getElementsByTagName("InventoryItem").item(0);
			Element eleItem = (Element) eleInventoryItem.getElementsByTagName(
					"Item").item(0);
			String strItemID = eleItem.getAttribute("ItemID");
			String strUOM = eleItem.getAttribute("UnitOfMeasure");

			Document recDoc = XMLUtil.createDocument("ReceiptLine");
			Element eleReceiptline = recDoc.getDocumentElement();
			eleReceiptline.setAttribute("LocationId", strLocation);
			eleReceiptline.setAttribute("Quantity", strQuant);
			eleReceiptline.setAttribute("ItemID", strItemID);
			eleReceiptline.setAttribute("UnitOfMeasure", strUOM);
			eleReceiptline.setAttribute("CalculatePrice", "Y");
			eleReceiptline.setAttribute("LPNNo", LPN);
			NodeList eleSerialDetailList = eleInvDetail
					.getElementsByTagName("SerialDetail");
			if (eleSerialDetailList.getLength() > 0) {
				Element SerialDetail = (Element) eleSerialDetailList.item(0);
				if (SerialDetail != null) {
					strSerialNo = SerialDetail.getAttribute("SerialNo");

				}
			}
			eleReceiptline.setAttribute("SerialNo", strSerialNo);
			insertBillingTrans(env, eleReceiptline, ReceiptNo, EnterpriseCode,
					ReceiptKey, DocumentType, CacheId, ReceiptDate,
					DispositionCode, strSerialNo, isAccountSplit, ownerAgency);
		}
	}

	public String calPrice(YFSEnvironment env, String ItemID, String SerialNo,
			String CacheID, String IncidentNo, String IncidentYear,
			String ProductClass, String UOM) throws Exception {
		String strPrice = getIssuePrice(env, ItemID, SerialNo, CacheID,
				IncidentNo, IncidentYear);
		if (strPrice.endsWith(""))
			strPrice = getUnitPrice(env, ItemID, ProductClass, UOM);
		return strPrice;
	}

	public String getIssuePrice(YFSEnvironment env, String ItemID,
			String SerialNo, String CacheID, String IncidentNo,
			String IncidentYear) throws Exception {
		Document inDoc = XMLUtil.newDocument();
		Document outDoc = XMLUtil.newDocument();
		Document trackinDoc = XMLUtil.newDocument();
		Document trackoutDoc = XMLUtil.newDocument();
		String IssuePrice = "";
		String QtyShippedStr = "";
		String QtyReturnedStr = "";
		double QtyShipped = 0.00;
		double QtyReturned = 0.00;
		String lastissueprice = "";
		if (SerialNo.length() > 0) {
			/* Added for CR-445 GN */
			Element el_NWCGTrackableItem = trackinDoc
					.createElement("NWCGTrackableItem");
			trackinDoc.appendChild(el_NWCGTrackableItem);
			el_NWCGTrackableItem.setAttribute("StatusIncidentNo", IncidentNo);
			el_NWCGTrackableItem.setAttribute("StatusIncidentYear",
					IncidentYear);
			el_NWCGTrackableItem.setAttribute("ItemID", ItemID);
			el_NWCGTrackableItem.setAttribute("SerialNo", SerialNo);
			el_NWCGTrackableItem.setAttribute("SecondarySerial",
					CommonUtilities.getSecondarySerial(env, SerialNo, ItemID));
			el_NWCGTrackableItem.setAttribute("SerialStatus", "I");
			trackoutDoc = CommonUtilities.invokeService(env,
					NWCGConstants.NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE,
					trackinDoc);
			Element trackoutelem = null;
			trackoutelem = (Element) trackoutDoc.getDocumentElement()
					.getElementsByTagName("NWCGTrackableItem").item(0);
			if (trackoutelem != null) {
				lastissueprice = trackoutelem.getAttribute("LastIssuePrice");
			}
		}
		if (lastissueprice.length() > 0) {
			return lastissueprice;
		}
		/* End CR-445 Change - GN */
		Element el_NWCGIncidentReturn = inDoc
				.createElement("NWCGIncidentReturn");
		inDoc.appendChild(el_NWCGIncidentReturn);
		el_NWCGIncidentReturn.setAttribute("CacheID", CacheID);
		el_NWCGIncidentReturn.setAttribute("IncidentNo", IncidentNo);
		el_NWCGIncidentReturn.setAttribute("IncidentYear", IncidentYear);
		el_NWCGIncidentReturn.setAttribute("ItemID", ItemID);
		el_NWCGIncidentReturn.setAttribute("TrackableID", SerialNo);
		// This to make sure over receipt entries and received as components are
		// not selected
		el_NWCGIncidentReturn.setAttribute("ReceivedAsComponent", "N");
		el_NWCGIncidentReturn.setAttribute("OverReceipt", "N");
		// Added this to make sure we get the first created record first
		Element el_OrderBy = inDoc.createElement("OrderBy");
		el_NWCGIncidentReturn.appendChild(el_OrderBy);
		Element el_Attribute = inDoc.createElement("Attribute");
		el_OrderBy.appendChild(el_Attribute);
		el_Attribute.setAttribute("Name", "Createts");
		outDoc = CommonUtilities.invokeService(env,
				NWCGConstants.NWCG_QUERY_LIST_ON_INCIDENT_TABLE_SERVICE, inDoc);
		NodeList NWCGRetLines = outDoc.getDocumentElement()
				.getElementsByTagName("NWCGIncidentReturn");
		int RetLineCnt = NWCGRetLines.getLength();
		if (RetLineCnt == 0) {
			return IssuePrice;
		} else {
			for (int cnt = 0; cnt < RetLineCnt; cnt++) {
				Element RetEl = (Element) NWCGRetLines.item(cnt);
				QtyShippedStr = RetEl.getAttribute("QuantityShipped");
				QtyReturnedStr = RetEl.getAttribute("QuantityReturned");
				IssuePrice = RetEl.getAttribute("UnitPrice");
				QtyShipped = Double.parseDouble(QtyShippedStr);
				QtyReturned = Double.parseDouble(QtyReturnedStr);
				if ((QtyShipped - QtyReturned) > 0) {
					return IssuePrice;
				}
			}
		}
		return IssuePrice;
	}

	public String getUnitPrice(YFSEnvironment env, String ItemId,
			String ProductClass, String UOM) throws Exception {
		Document rt_ComputePriceForItem = XMLUtil.getDocument();
		Element el_ComputePriceForItem = rt_ComputePriceForItem
				.createElement("ComputePriceForItem");
		rt_ComputePriceForItem.appendChild(el_ComputePriceForItem);
		el_ComputePriceForItem.setAttribute("Currency", "USD");
		el_ComputePriceForItem.setAttribute("ItemID", ItemId);
		el_ComputePriceForItem.setAttribute("OrganizationCode", "NWCG");
		el_ComputePriceForItem.setAttribute("PriceProgramName",
				"NWCG_PRICE_PROGRAM");
		el_ComputePriceForItem.setAttribute("ProductClass", ProductClass);
		el_ComputePriceForItem.setAttribute("Quantity", "1");
		el_ComputePriceForItem.setAttribute("Uom", UOM);
		String UnitPrice = "";
		Document rt_ComputePriceForItemOut = XMLUtil.getDocument();
		Element el_ComputePriceForItemOut = rt_ComputePriceForItemOut
				.createElement("ComputePriceForItem");
		rt_ComputePriceForItemOut.appendChild(el_ComputePriceForItemOut);
		el_ComputePriceForItemOut.setAttribute("UnitPrice", "");
		Document PriceDocument = CommonUtilities.invokeAPI(env,
				"computePriceForItem", rt_ComputePriceForItem);
		Element root_elem = PriceDocument.getDocumentElement();
		if (root_elem != null) {
			UnitPrice = root_elem.getAttribute("UnitPrice");
		}
		return UnitPrice;
	}
}