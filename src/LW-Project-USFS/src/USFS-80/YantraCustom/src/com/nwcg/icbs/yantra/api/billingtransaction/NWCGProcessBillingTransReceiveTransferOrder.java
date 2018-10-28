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

import org.w3c.dom.*;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.reports.loftware.NWCGReportsUtil;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGProcessBillingTransReceiveTransferOrder implements
		YIFCustomApi, NWCGBillingTransRecordMutator {
	
	private static Logger logger = Logger
			.getLogger(NWCGProcessBillingTransChangeOrder.class.getName());

	public void setProperties(Properties arg0) throws Exception {
	}

	public void throwAlert(YFSEnvironment env, StringBuffer Message)
			throws Exception {
		Message.append(" ExceptionType='BILLING_TRANS_RECEIVE_TO'"
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
					.verbose("Entering NWCGProcessBillingTransReceiveTO, Input document is:"
							+ XMLUtil.getXMLString(inXML));
		}
		// Jimmy temp vars for alerts
		String tempCacheId = "";
		String tempReceiptDate = "";
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
						String EnterpriseCode = curReceiptList
								.getAttribute("EnterpriseCode");
						String ReceiptDate = curReceiptList
								.getAttribute("ReceiptDate");
						String DocumentType = curReceiptList
								.getAttribute("DocumentType");
						String CacheId = curReceiptList
								.getAttribute("ReceivingNode");
						// Jimmy populating temp vars for alerts
						tempCacheId = CacheId;
						tempReceiptDate = ReceiptDate;
						Element AcctElem = getOrgAcctCode(env, CacheId);
						String AcctCode = AcctElem
								.getAttribute("ExtnRecvAcctCode");
						String ReceiveOwnerAgency = AcctElem
								.getAttribute("ExtnOwnerAgency");
						NodeList listOfReceiptLines = curReceiptList
								.getElementsByTagName("ReceiptLine");
						for (int j = 0; j < listOfReceiptLines.getLength(); j++) {
							curReceiptLineList = (Element) listOfReceiptLines
									.item(j);
							String DispositionCode = curReceiptLineList
									.getAttribute("DispositionCode");
							String OrderNo = curReceiptLineList
									.getAttribute("OrderNo");
							String OrderLineKey = curReceiptLineList
									.getAttribute("OrderLineKey");
							String OrderHeaderKey = curReceiptLineList
									.getAttribute("OrderHeaderKey");
							String ActualQty = curReceiptLineList
									.getAttribute("Quantity");
							String ItemId = curReceiptLineList
									.getAttribute("ItemID");
							String LocationId = curReceiptLineList
									.getAttribute("LocationId");
							String UOM = curReceiptLineList
									.getAttribute("UnitOfMeasure");
							Document docBillingTransactionIP = getBillingTransInputDocument(env);
							Element BillingTransElem = docBillingTransactionIP
									.getDocumentElement();
							NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_NO, OrderNo);
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_ENTERPRISE_CODE,
									EnterpriseCode);
							String strDate = reportsUtil.dateToString(
									new java.util.Date(),
									"yyyy-MM-dd'T'HH:mm:ss");
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_DATE, strDate);
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_HEADER_KEY,
									OrderHeaderKey);
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_DOCUMENT_TYPE,
									DocumentType);
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_CACHE_ID, CacheId);
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_LINE_KEY,
									OrderLineKey);
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_QTY, ActualQty);
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_ITEM_ID, ItemId);
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_UOM, UOM);
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_LOCATION_ID,
									LocationId);
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_DISPOSITION_CODE,
									DispositionCode);
							Element ItemElem = getItemDetails(env, ItemId, UOM,
									EnterpriseCode);
							NodeList ItemList = ItemElem
									.getElementsByTagName("ClassificationCodes");
							Element ItemListElem = (Element) ItemList.item(0);
							String ItemClass = StringUtil.nonNull(ItemListElem
									.getAttribute("TaxProductCode"));
							NodeList ItemList1 = ItemElem
									.getElementsByTagName("PrimaryInformation");
							Element ItemListElem1 = (Element) ItemList1.item(0);
							String UnitCost = StringUtil.nonNull(ItemListElem1
									.getAttribute("UnitCost"));
							String ItemDesc = ItemListElem1
									.getAttribute("Description");
							String ProductLine = ItemListElem1
									.getAttribute("ProductLine");
							double qty = Double.parseDouble(ActualQty);
							double rprice = Double.parseDouble(UnitCost);
							if (!(rprice > 0)) {
								StringBuffer stbuf = new StringBuffer(
										"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransReceiveTO' "
												+ "DetailDescription='Unit Price is 0 for Order No : "
												+ OrderNo
												+ ",Item Id : "
												+ ItemId
												+ ", during NWCGProcessBillingTransReceiveTO process'");
								throwAlert(env, stbuf);
							}
							double Tcost = qty * rprice;
							String TcostStr = Double.toString(Tcost);
							String QtyStr = Double.toString(qty);
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_ITEM_DESCRIPTION,
									ItemDesc);
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_UNIT_COST,
									UnitCost);
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_AMOUNT, TcostStr);
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_ITEM_CLASSIFICATION,
											ItemClass);
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_ITEM_PRODUCTLINE,
									ProductLine);
							Document getOrderDetailsIP = XMLUtil
									.createDocument("Order");
							Element GetOrderDetailsIPElem = getOrderDetailsIP
									.getDocumentElement();
							GetOrderDetailsIPElem.setAttribute(
									"OrderHeaderKey", OrderHeaderKey);
							Document getOrderDetailsOP = null;
							try {
								getOrderDetailsOP = CommonUtilities
										.invokeAPI(
												env,
												"NWCGProcessBillingTrans_getOrderDetails",
												"getOrderDetails",
												getOrderDetailsIP);
							} catch (Exception e) {
								e.printStackTrace();
								StringBuffer stbuf = new StringBuffer(
										"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransReceiveTO' "
												+ "DetailDescription='GetOrderDetails Failed during NWCGProcessBillingTransReceiveTO, for OrderHeaderKey : "
												+ OrderHeaderKey + "'");
								throwAlert(env, stbuf);
								if (logger.isVerboseEnabled())
									logger
											.verbose("NWCGProcessBillingTransReceiveTO::GetOrderDetails Caught Exception "
													+ e);
							}
							Element GetOrderDetailsOPElem = getOrderDetailsOP
									.getDocumentElement();
							NodeList nlExtn = GetOrderDetailsOPElem
									.getElementsByTagName("Extn");
							Element elemExtn = (Element) nlExtn.item(0);
							String RecvAcctCode = StringUtil.nonNull(elemExtn
									.getAttribute("ExtnRecvAcctCode"));
							String RecvOverrideCode = StringUtil
									.nonNull(elemExtn
											.getAttribute("ExtnRAOverrideCode"));
							String IncidentNo = StringUtil.nonNull(elemExtn
									.getAttribute("ExtnIncidentNo"));
							String IncidentYear = StringUtil.nonNull(elemExtn
									.getAttribute("ExtnIncidentYear"));
							String IncidentName = StringUtil.nonNull(elemExtn
									.getAttribute("ExtnIncidentName"));
							String BLMAcctCode = StringUtil.nonNull(elemExtn
									.getAttribute("ExtnBlmAcctCode"));
							String OtherAcctCode = StringUtil.nonNull(elemExtn
									.getAttribute("ExtnOtherAcctCode"));
							String FSAcctCode = StringUtil.nonNull(elemExtn
									.getAttribute("ExtnFsAcctCode"));
							String FSOverrideCode = StringUtil.nonNull(elemExtn
									.getAttribute("ExtnOverrideCode"));
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_INCIDENT_NO,
									IncidentNo);
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_INCIDENT_YEAR,
									IncidentYear);
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_INCIDENT_NAME,
									IncidentName);
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_IS_EXTRACTED, "Y");
							if (RecvAcctCode.length() == 0) {
								RecvAcctCode = AcctCode;
							}
							BillingTransElem = setAccountCodes(
									BillingTransElem, ReceiveOwnerAgency,
									RecvAcctCode, RecvOverrideCode);
							// Credit Receiving Cache
							try {
								CommonUtilities
										.invokeService(
												env,
												NWCGConstants.NWCG_CREATE_BILLING_TRANSACTION_SERVICE,
												docBillingTransactionIP);
							} catch (Exception e) {
								e.printStackTrace();
								StringBuffer stbuf = new StringBuffer(
										"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransReceiveTO' "
												+ "DetailDescription='Credit Billing Transaction Record Creation Failed on Receive TO for Order No : "
												+ OrderNo + ",Item Id : "
												+ ItemId + ", Actual QTY: "
												+ ActualQty + ", Cache ID: "
												+ CacheId + ", Location ID: "
												+ LocationId
												+ ", Deposition Code: "
												+ DispositionCode
												+ ", Reciept date: "
												+ ReceiptDate + "'");
								throwAlert(env, stbuf);
							}
							// String ShipAcctCode =
							// StringUtil.nonNull(elemExtn.getAttribute("ExtnShipAcctCode"));
							Element OrderLineElm = (Element) curReceiptLineList
									.getElementsByTagName("OrderLine").item(0);
							String ShipCacheId = OrderLineElm
									.getAttribute("ShipNode");
							AcctElem = getOrgAcctCode(env, ShipCacheId);
							AcctCode = "";
							String OverrideCode = "";
							String ShipOwnerAgency = "";
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE,
											"");
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE,
											"");
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE,
											"");
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE,
											"");
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_LAST_INCIDENT_BLM_ACCT_CODE,
											"");
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_ACCT_CODE,
											"");
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_OVERRIDE_CODE,
											"");
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_LAST_INCIDENT_OTHER_ACCT_CODE,
											"");
							// AcctCode =
							// AcctElem.getAttribute("ExtnAdjustAcctCode");
							ShipOwnerAgency = AcctElem
									.getAttribute("ExtnOwnerAgency");
							// OverrideCode =
							// AcctElem.getAttribute("ExtnFSOverrideCode");
							if (ShipOwnerAgency.equals("BLM")) {
								AcctCode = BLMAcctCode;
							} else if (ShipOwnerAgency.equals("FS")) {
								AcctCode = FSAcctCode;
							} else {
								AcctCode = OtherAcctCode;
							}
							BillingTransElem = setAccountCodes(
									BillingTransElem, ShipOwnerAgency,
									AcctCode, FSOverrideCode);
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_CACHE_ID,
									ShipCacheId);
							double Scost = qty * rprice * -1;
							String ScostStr = Double.toString(Scost);
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_AMOUNT, ScostStr);
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_TYPE,
									"SHIP CACHE TO");
							BillingTransElem.setAttribute(
									NWCGConstants.BILL_TRANS_IS_EXTRACTED, "N");
							// Debit Shipping Cache
							try {
								CommonUtilities
										.invokeService(
												env,
												NWCGConstants.NWCG_CREATE_BILLING_TRANSACTION_SERVICE,
												docBillingTransactionIP);
							} catch (Exception e) {
								e.printStackTrace();
								StringBuffer stbuf = new StringBuffer(
										"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransReceiveTO' "
												+ "DetailDescription='Debit Billing Transaction Record Creation Failed on Receive TO for Order No : "
												+ OrderNo + ",Item Id : "
												+ ItemId + ", Actual QTY: "
												+ ActualQty + ", Cache ID: "
												+ ShipCacheId
												+ ", Location ID: "
												+ LocationId
												+ ", Deposition Code: "
												+ DispositionCode
												+ ", Reciept date: "
												+ ReceiptDate + "'");
								throwAlert(env, stbuf);
							}
							docBillingTransactionIP = null;
							AcctCode = "";
						}
					} 
				} 
			} 
		} catch (Exception e) {
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingTransReceiveTO::InsertBillingTransactionRecord Caught Exception "
								+ e);
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransReceiveTO' "
							+ "DetailDescription='InsertBillingTransactionRecord Failed during NWCGProcessBillingTransReceiveTO, on Cache ID: "
							+ tempCacheId + ", Reciept Date: "
							+ tempReceiptDate + "'");
			throwAlert(env, stbuf);
		}
		return inXML;
	}

	private Document getBillingTransInputDocument(YFSEnvironment env)
			throws Exception {
		Document returnDoc = XMLUtil.createDocument("NWCGBillingTransaction");
		Element returnDocElem = returnDoc.getDocumentElement();
		returnDocElem.setAttribute(NWCGConstants.BILL_TRANS_TYPE,
				"RECEIVE CACHE TO");
		return returnDoc;
	}

	public Document insertBillingTransRecord(YFSEnvironment env, Document doc)
			throws Exception {
		return InsertRecord(env, doc);
	}

	public Element getOrgAcctCode(YFSEnvironment env, String Cache)
			throws Exception {
		Document inDoc = XMLUtil.newDocument();
		Document outDoc = XMLUtil.newDocument();
		Element el_NWCGOrganization = inDoc.createElement("Organization");
		inDoc.appendChild(el_NWCGOrganization);
		el_NWCGOrganization.setAttribute("OrganizationCode", Cache);
		try {
			outDoc = CommonUtilities.invokeAPI(env,
					"NWCGProcessBillingTrans_getOrganizationList",
					"getOrganizationList", inDoc);
		} catch (Exception e) {
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransReceiveTO' "
							+ "DetailDescription='GetOrganizationList Failed for Cache ID : "
							+ Cache
							+ ", during process billing transaction for receive TO'");
			throwAlert(env, stbuf);
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingTransReceiveTO::getOrganizationList Caught Exception "
								+ e);
		}
		Element outDocElem = outDoc.getDocumentElement();
		Element ExtnElm = (Element) outDocElem.getElementsByTagName("Extn")
				.item(0);
		return ExtnElm;
	}

	public Element getItemDetails(YFSEnvironment env, String ItemId,
			String UOM, String EnterpriseCode) throws Exception {
		Document getItemDetailsIP = XMLUtil.createDocument("Item");
		Element GetItemDetailsIPElem = getItemDetailsIP.getDocumentElement();
		GetItemDetailsIPElem.setAttribute("ItemID", ItemId);
		GetItemDetailsIPElem.setAttribute("UnitOfMeasure", UOM);
		GetItemDetailsIPElem.setAttribute("OrganizationCode", EnterpriseCode);

		Document getItemDetailsOP = null;
		try {
			getItemDetailsOP = CommonUtilities.invokeAPI(env, "getItemDetails",
					getItemDetailsIP);
		} catch (Exception e) {
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransReceiveTO' "
							+ "DetailDescription='GetItemDetails Failed for Item ID : "
							+ ItemId
							+ ", during process billing transaction for receive TO'");
			throwAlert(env, stbuf);
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingTransReceiveTO::getItemDetails Caught Exception "
								+ e);
		}
		Element GetItemDetailsOPElem = getItemDetailsOP.getDocumentElement();
		return GetItemDetailsOPElem;
	}

	public Element setAccountCodes(Element BillElem, String OwnerAgency,
			String AcctCode, String OverrideCode) {
		if (OwnerAgency.equals("BLM")) {
			BillElem.setAttribute(
					NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE, AcctCode);
			BillElem.setAttribute(
					NWCGConstants.BILL_TRANS_LAST_INCIDENT_BLM_ACCT_CODE,
					AcctCode);
		} else if (OwnerAgency.equals("FS")) {
			BillElem.setAttribute(
					NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE, AcctCode);
			BillElem.setAttribute(
					NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_ACCT_CODE,
					AcctCode);
			BillElem.setAttribute(
					NWCGConstants.BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE,
					OverrideCode);
			BillElem.setAttribute(
					NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_OVERRIDE_CODE,
					OverrideCode);
		} else {
			BillElem
					.setAttribute(
							NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE,
							AcctCode);
			BillElem.setAttribute(
					NWCGConstants.BILL_TRANS_LAST_INCIDENT_OTHER_ACCT_CODE,
					AcctCode);
		}
		return BillElem;
	}
}