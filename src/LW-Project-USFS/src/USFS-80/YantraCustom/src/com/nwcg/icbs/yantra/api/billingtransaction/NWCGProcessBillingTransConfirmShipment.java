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
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGProcessBillingTransConfirmShipment implements YIFCustomApi,
		NWCGBillingTransRecordMutator {
	
	private static Logger logger = Logger
			.getLogger(NWCGProcessBillingTransConfirmShipment.class.getName());

	public void setProperties(Properties arg0) throws Exception {
	}

	public void throwAlert(YFSEnvironment env, StringBuffer Message)
			throws Exception {
		Message.append(" ExceptionType='BILLING_TRANS_CONFIRM_SHIPMENT'"
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
					.verbose("Entering NWCGProcessBillingTransConfirmShipment, Input document is:"
							+ XMLUtil.getXMLString(inXML));
		}
		// Jimmy - Temp vars to be used for alerts
		String tempOrderNo = "";
		String tempDocumentType = "";
		String tempCacheId = "";
		String tempcreateUserId = "";
		try {
			Element curShipList, curShipLineList = null;
			// inXML.getDocumentElement().normalize();
			Element elemshipmentRoot = inXML.getDocumentElement();
			if (elemshipmentRoot != null) {
				NodeList listOfShipments = inXML
						.getElementsByTagName("Shipment");
				curShipList = (Element) listOfShipments.item(0);
				NodeList listOfShipmentLines = curShipList
						.getElementsByTagName("ShipmentLine");
				curShipLineList = (Element) listOfShipmentLines.item(0);
				String OrderHeaderKey = StringUtil.nonNull(curShipLineList
						.getAttribute("OrderHeaderKey"));
				Document getOrderDetailsIP = XMLUtil.createDocument("Order");
				Element GetOrderDetailsIPElem = getOrderDetailsIP
						.getDocumentElement();
				GetOrderDetailsIPElem.setAttribute("OrderHeaderKey",
						OrderHeaderKey);
				Document getOrderDetailsOP = null;
				try {
					getOrderDetailsOP = CommonUtilities.invokeAPI(env,
							"NWCGProcessBillingTrans_getOrderDetails",
							"getOrderDetails", getOrderDetailsIP);
				} catch (Exception e) {
					e.printStackTrace();
					StringBuffer stbuf = new StringBuffer(
							"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransConfirmShipment' "
									+ "DetailDescription='GetOrderDetails Failed during NWCGProcessBillingTransConfirmShipment, for OrderHeaderKey : "
									+ OrderHeaderKey + "'");
					throwAlert(env, stbuf);
					if (logger.isVerboseEnabled())
						logger
								.verbose("NWCGProcessBillingTransConfirmShipment::GetOrderDetails Caught Exception "
										+ e);
				}
				Element GetOrderDetailsOPElem = getOrderDetailsOP
						.getDocumentElement();
				String EnterpriseCode = GetOrderDetailsOPElem
						.getAttribute("EnterpriseCode");
				String ShipDate = curShipList.getAttribute("ShipDate");
				String DocumentType = GetOrderDetailsOPElem
						.getAttribute("DocumentType");
				String OrderNo = GetOrderDetailsOPElem.getAttribute("OrderNo");
				String createUserId = GetOrderDetailsOPElem
						.getAttribute("Createuserid");
				String modifyUserId = GetOrderDetailsOPElem
						.getAttribute("Modifyuserid");
				String CacheId = curShipList.getAttribute("ShipNode");
				Element AcctElem = getOrgAcctCode(env, CacheId);
				String AcctCode = AcctElem.getAttribute("ExtnAdjustAcctCode");
				String OwnerAgency = AcctElem.getAttribute("ExtnOwnerAgency");
				// Getting Order Total
				Element TotElem = (Element) GetOrderDetailsOPElem
						.getElementsByTagName("OverallTotals").item(0);
				String IssueTotal = TotElem.getAttribute("GrandTotal");
				double issuetot = Double.parseDouble(IssueTotal);
				// Jimmy - Populating temp vars for alerts
				tempOrderNo = OrderNo;
				tempDocumentType = DocumentType;
				tempCacheId = CacheId;
				tempcreateUserId = createUserId;
				// Getting Incident Details & Split Account Details
				NodeList nlExtn = GetOrderDetailsOPElem
						.getElementsByTagName("Extn");
				Element elemExtn = (Element) nlExtn.item(0);
				String IncidentNo = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnIncidentNo"));
				String IncidentYear = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnIncidentYear"));
				String IncidentName = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnIncidentName"));
				String ExtnFsAcctCode = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnFsAcctCode"));
				String ExtnOverrideCode = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnOverrideCode"));
				String ExtnBlmAcctCode = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnBlmAcctCode"));
				String ExtnOtherAcctCode = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnOtherAcctCode"));
				// Split Account Codes
				String ExtnAcctCode1 = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnAcctCode1"));
				String ExtnAcctCode2 = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnAcctCode2"));
				String ExtnAcctCode3 = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnAcctCode3"));
				String ExtnAcctCode4 = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnAcctCode4"));
				String ExtnAcctCode5 = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnAcctCode5"));
				String ExtnOverrideCode1 = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnOverrideCode1"));
				String ExtnOverrideCode2 = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnOverrideCode2"));
				String ExtnOverrideCode3 = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnOverrideCode3"));
				String ExtnOverrideCode4 = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnOverrideCode4"));
				String ExtnOverrideCode5 = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnOverrideCode5"));
				String ExtnSplitAmount1 = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnSplitAmount1"));
				String ExtnSplitAmount2 = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnSplitAmount2"));
				String ExtnSplitAmount3 = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnSplitAmount3"));
				String ExtnSplitAmount4 = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnSplitAmount4"));
				String ExtnSplitAmount5 = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnSplitAmount5"));
				double splitamt1 = Double.parseDouble(ExtnSplitAmount1);
				double splitamt2 = Double.parseDouble(ExtnSplitAmount2);
				double splitamt3 = Double.parseDouble(ExtnSplitAmount3);
				double splitamt4 = Double.parseDouble(ExtnSplitAmount4);
				double splitamt5 = Double.parseDouble(ExtnSplitAmount5);
				double percent1 = splitamt1 / issuetot;
				double percent2 = splitamt2 / issuetot;
				double percent3 = splitamt3 / issuetot;
				double percent4 = splitamt4 / issuetot;
				double percent5 = splitamt5 / issuetot;
				String IncidentBlmAcctCode1 = "", IncidentBlmAcctCode2 = "", IncidentBlmAcctCode3 = "", IncidentBlmAcctCode4 = "", IncidentBlmAcctCode5 = "";
				String IncidentFsAcctCode1 = "", IncidentFsAcctCode2 = "", IncidentFsAcctCode3 = "", IncidentFsAcctCode4 = "", IncidentFsAcctCode5 = "";
				String IncidentFsOverrideCode1 = "", IncidentFsOverrideCode2 = "", IncidentFsOverrideCode3 = "", IncidentFsOverrideCode4 = "", IncidentFsOverrideCode5 = "";
				String IncidentOtherAcctCode1 = "", IncidentOtherAcctCode2 = "", IncidentOtherAcctCode3 = "", IncidentOtherAcctCode4 = "", IncidentOtherAcctCode5 = "";
				if (OwnerAgency.equals("BLM")) {
					IncidentBlmAcctCode1 = ExtnAcctCode1;
					IncidentBlmAcctCode2 = ExtnAcctCode2;
					IncidentBlmAcctCode3 = ExtnAcctCode3;
					IncidentBlmAcctCode4 = ExtnAcctCode4;
					IncidentBlmAcctCode5 = ExtnAcctCode5;
				} else if (OwnerAgency.equals("FS")) {
					IncidentFsAcctCode1 = ExtnAcctCode1;
					IncidentFsAcctCode2 = ExtnAcctCode2;
					IncidentFsAcctCode3 = ExtnAcctCode3;
					IncidentFsAcctCode4 = ExtnAcctCode4;
					IncidentFsAcctCode5 = ExtnAcctCode5;
					IncidentFsOverrideCode1 = ExtnOverrideCode1;
					IncidentFsOverrideCode2 = ExtnOverrideCode2;
					IncidentFsOverrideCode3 = ExtnOverrideCode3;
					IncidentFsOverrideCode4 = ExtnOverrideCode4;
					IncidentFsOverrideCode5 = ExtnOverrideCode5;
				} else {
					IncidentOtherAcctCode1 = ExtnAcctCode1;
					IncidentOtherAcctCode2 = ExtnAcctCode2;
					IncidentOtherAcctCode3 = ExtnAcctCode3;
					IncidentOtherAcctCode4 = ExtnAcctCode4;
					IncidentOtherAcctCode5 = ExtnAcctCode5;
				}
				// NodeList listOfOrderLines =
				// GetOrderDetailsOPElem.getElementsByTagName("OrderLine");
				for (int i = 0; i < listOfShipmentLines.getLength(); i++) {
					// Element curOrderLine = (Element)
					// listOfOrderLines.item(i);
					curShipLineList = (Element) listOfShipmentLines.item(i);
					String OrderedQty = curShipLineList
							.getAttribute("ActualQuantity");
					String OrderLineKey = curShipLineList
							.getAttribute("OrderLineKey");
					// Element ItemElem = (Element)
					// curOrderLine.getElementsByTagName("Item").item(0);
					String ItemId = curShipLineList.getAttribute("ItemID");
					String UOM = curShipLineList.getAttribute("UnitOfMeasure");
					// Need to get from Item Details
					String ItemClass = "";
					String ProductLine = "";
					String ItemDesc = "";
					Element ItemElem = getItemDetails(env, ItemId, UOM,
							EnterpriseCode);
					NodeList ItemList = ItemElem
							.getElementsByTagName("ClassificationCodes");
					Element ItemListElem = (Element) ItemList.item(0);
					ItemClass = StringUtil.nonNull(ItemListElem
							.getAttribute("TaxProductCode"));
					NodeList ItemList1 = ItemElem
							.getElementsByTagName("PrimaryInformation");
					Element ItemListElem1 = (Element) ItemList1.item(0);
					ItemDesc = StringUtil.nonNull(ItemListElem1
							.getAttribute("Description"));
					ProductLine = StringUtil.nonNull(ItemListElem1
							.getAttribute("ProductLine"));
					// Element PriceElem = (Element)
					// curOrderLine.getElementsByTagName("LinePriceInfo").item(0);
					String UnitPrice = curShipLineList
							.getAttribute("UnitPrice");
					double uprice = Double.parseDouble(UnitPrice);
					if (!(uprice > 0)) {
						StringBuffer stbuf = new StringBuffer(
								"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransConfirmShipment' "
										+ "DetailDescription='Unit Price is 0 for Order No : "
										+ OrderNo
										+ ",Item Id : "
										+ ItemId
										+ ", ocurred during NWCGProcessBillingTransConfirmShipment.'");
						throwAlert(env, stbuf);
					}
					double qty = Double.parseDouble(OrderedQty);
					Document docBillingTransactionIP = getBillingTransInputDocument(env);
					Element BillingTransElem = docBillingTransactionIP
							.getDocumentElement();
					BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_NO,
							OrderNo);
					BillingTransElem
							.setAttribute(
									NWCGConstants.BILL_TRANS_CREATEUSERID,
									createUserId);
					BillingTransElem
							.setAttribute(
									NWCGConstants.BILL_TRANS_MODIFYUSERID,
									modifyUserId);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_ENTERPRISE_CODE,
							EnterpriseCode);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_DATE, ShipDate);
					BillingTransElem
							.setAttribute(NWCGConstants.BILL_TRANS_HEADER_KEY,
									OrderHeaderKey);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_DOCUMENT_TYPE,
							DocumentType);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_CACHE_ID, CacheId);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_LINE_KEY, OrderLineKey);
					BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_QTY,
							OrderedQty);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_UNIT_COST, UnitPrice);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_ITEM_ID, ItemId);
					BillingTransElem
							.setAttribute(
									NWCGConstants.BILL_TRANS_ITEM_DESCRIPTION,
									ItemDesc);
					BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_UOM,
							UOM);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_ITEM_CLASSIFICATION,
							ItemClass);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_ITEM_PRODUCTLINE,
							ProductLine);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_INCIDENT_NO, IncidentNo);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_INCIDENT_YEAR,
							IncidentYear);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_INCIDENT_NAME,
							IncidentName);
					double Tcost;
					String TcostStr = "";
					String QtyStr = "";
					String SplitAmt = "";
					if (splitamt1 > 0) {
						Tcost = qty * uprice * percent1 * -1;
						TcostStr = Double.toString(Tcost);
						QtyStr = Double.toString(qty);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE,
										IncidentBlmAcctCode1);
						BillingTransElem.setAttribute(
								NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE,
								IncidentFsAcctCode1);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE,
										IncidentFsOverrideCode1);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE,
										IncidentOtherAcctCode1);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_BLM_ACCT_CODE,
										IncidentBlmAcctCode1);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_ACCT_CODE,
										IncidentFsAcctCode1);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_OVERRIDE_CODE,
										IncidentFsOverrideCode1);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_OTHER_ACCT_CODE,
										IncidentOtherAcctCode1);
						BillingTransElem.setAttribute(
								NWCGConstants.BILL_TRANS_IS_ACCOUNT_SPLIT, "Y");
						BillingTransElem.setAttribute(
								NWCGConstants.BILL_TRANS_SPLIT_AMT_NUMBER, "1");
						SplitAmt = "0";
					} else {
						Tcost = qty * uprice * -1;
						TcostStr = Double.toString(Tcost);
						QtyStr = Double.toString(qty);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE,
										ExtnBlmAcctCode);
						BillingTransElem.setAttribute(
								NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE,
								ExtnFsAcctCode);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE,
										ExtnOverrideCode);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE,
										ExtnOtherAcctCode);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_BLM_ACCT_CODE,
										ExtnBlmAcctCode);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_ACCT_CODE,
										ExtnFsAcctCode);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_OVERRIDE_CODE,
										ExtnOverrideCode);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_OTHER_ACCT_CODE,
										ExtnOtherAcctCode);
						SplitAmt = "1";
					}
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_AMOUNT, TcostStr);
					try {
						CommonUtilities
								.invokeService(
										env,
										NWCGConstants.NWCG_CREATE_BILLING_TRANSACTION_SERVICE,
										docBillingTransactionIP);
					} catch (Exception e) {
						e.printStackTrace();
						StringBuffer stbuf = new StringBuffer(
								"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransConfirmShipment' "
										+ "DetailDescription='Debit Billing Transaction Record Creation Failed on Confirm Shipment for Order No : "
										+ OrderNo + ",Item Id : " + ItemId
										+ "Split Amount : " + SplitAmt
										+ ", Cache ID: " + CacheId
										+ ", Order QTY: " + OrderedQty
										+ ", incident no: " + IncidentNo
										+ ", CreateUser: " + createUserId + "'");
						throwAlert(env, stbuf);
					}
					if (splitamt2 > 0) {
						Tcost = qty * uprice * percent2 * -1;
						TcostStr = Double.toString(Tcost);
						QtyStr = Double.toString(qty);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE,
										IncidentBlmAcctCode2);
						BillingTransElem.setAttribute(
								NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE,
								IncidentFsAcctCode2);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE,
										IncidentFsOverrideCode2);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE,
										IncidentOtherAcctCode2);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_BLM_ACCT_CODE,
										IncidentBlmAcctCode2);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_ACCT_CODE,
										IncidentFsAcctCode2);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_OVERRIDE_CODE,
										IncidentFsOverrideCode2);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_OTHER_ACCT_CODE,
										IncidentOtherAcctCode2);
						BillingTransElem.setAttribute(
								NWCGConstants.BILL_TRANS_IS_ACCOUNT_SPLIT, "Y");
						BillingTransElem.setAttribute(
								NWCGConstants.BILL_TRANS_SPLIT_AMT_NUMBER, "2");
						BillingTransElem.setAttribute(
								NWCGConstants.BILL_TRANS_AMOUNT, TcostStr);
						try {
							CommonUtilities
									.invokeService(
											env,
											NWCGConstants.NWCG_CREATE_BILLING_TRANSACTION_SERVICE,
											docBillingTransactionIP);
						} catch (Exception e) {
							e.printStackTrace();
							StringBuffer stbuf = new StringBuffer(
									"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransConfirmShipment' "
											+ "DetailDescription='Debit Billing Transaction Record Creation Failed on Confirm Shipment for Order No : "
											+ OrderNo + ",Item Id : " + ItemId
											+ "Split Amount : 2 , Cache ID: "
											+ CacheId + ", Order QTY: "
											+ OrderedQty + ", incident no: "
											+ IncidentNo + ", CreateUser: "
											+ createUserId + "'");
							throwAlert(env, stbuf);
						}
					}
					if (splitamt3 > 0) {
						Tcost = qty * uprice * percent3 * -1;
						TcostStr = Double.toString(Tcost);
						QtyStr = Double.toString(qty);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE,
										IncidentBlmAcctCode3);
						BillingTransElem.setAttribute(
								NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE,
								IncidentFsAcctCode3);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE,
										IncidentFsOverrideCode3);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE,
										IncidentOtherAcctCode3);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_BLM_ACCT_CODE,
										IncidentBlmAcctCode3);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_ACCT_CODE,
										IncidentFsAcctCode3);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_OVERRIDE_CODE,
										IncidentFsOverrideCode3);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_OTHER_ACCT_CODE,
										IncidentOtherAcctCode3);
						BillingTransElem.setAttribute(
								NWCGConstants.BILL_TRANS_IS_ACCOUNT_SPLIT, "Y");
						BillingTransElem.setAttribute(
								NWCGConstants.BILL_TRANS_SPLIT_AMT_NUMBER, "3");
						BillingTransElem.setAttribute(
								NWCGConstants.BILL_TRANS_AMOUNT, TcostStr);
						try {
							CommonUtilities
									.invokeService(
											env,
											NWCGConstants.NWCG_CREATE_BILLING_TRANSACTION_SERVICE,
											docBillingTransactionIP);
						} catch (Exception e) {
							e.printStackTrace();
							StringBuffer stbuf = new StringBuffer(
									"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransConfirmShipment' "
											+ "DetailDescription='Debit Billing Transaction Record Creation Failed on Confirm Shipment for Order No : "
											+ OrderNo + ",Item Id : " + ItemId
											+ "Split Amount : 3 , Cache ID: "
											+ CacheId + ", Order QTY: "
											+ OrderedQty + ", incident no: "
											+ IncidentNo + ", CreateUser: "
											+ createUserId + "'");
							throwAlert(env, stbuf);
						}
					}
					if (splitamt4 > 0) {
						Tcost = qty * uprice * percent4 * -1;
						TcostStr = Double.toString(Tcost);
						QtyStr = Double.toString(qty);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE,
										IncidentBlmAcctCode4);
						BillingTransElem.setAttribute(
								NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE,
								IncidentFsAcctCode4);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE,
										IncidentFsOverrideCode4);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE,
										IncidentOtherAcctCode4);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_BLM_ACCT_CODE,
										IncidentBlmAcctCode4);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_ACCT_CODE,
										IncidentFsAcctCode4);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_OVERRIDE_CODE,
										IncidentFsOverrideCode4);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_OTHER_ACCT_CODE,
										IncidentOtherAcctCode4);
						BillingTransElem.setAttribute(
								NWCGConstants.BILL_TRANS_IS_ACCOUNT_SPLIT, "Y");
						BillingTransElem.setAttribute(
								NWCGConstants.BILL_TRANS_SPLIT_AMT_NUMBER, "4");
						BillingTransElem.setAttribute(
								NWCGConstants.BILL_TRANS_AMOUNT, TcostStr);
						try {
							CommonUtilities
									.invokeService(
											env,
											NWCGConstants.NWCG_CREATE_BILLING_TRANSACTION_SERVICE,
											docBillingTransactionIP);
						} catch (Exception e) {
							e.printStackTrace();
							StringBuffer stbuf = new StringBuffer(
									"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransConfirmShipment' "
											+ "DetailDescription='Debit Billing Transaction Record Creation Failed on Confirm Shipment for Order No : "
											+ OrderNo + ",Item Id : " + ItemId
											+ "Split Amount : 4 , Cache ID: "
											+ CacheId + ", Order QTY: "
											+ OrderedQty + ", incident no: "
											+ IncidentNo + ", CreateUser: "
											+ createUserId + "'");
							throwAlert(env, stbuf);
						}
					}
					if (splitamt5 > 0) {
						Tcost = qty * uprice * percent5 * -1;
						TcostStr = Double.toString(Tcost);
						QtyStr = Double.toString(qty);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE,
										IncidentBlmAcctCode5);
						BillingTransElem.setAttribute(
								NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE,
								IncidentFsAcctCode5);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE,
										IncidentFsOverrideCode5);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE,
										IncidentOtherAcctCode5);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_BLM_ACCT_CODE,
										IncidentBlmAcctCode5);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_ACCT_CODE,
										IncidentFsAcctCode5);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_OVERRIDE_CODE,
										IncidentFsOverrideCode5);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_OTHER_ACCT_CODE,
										IncidentOtherAcctCode5);
						BillingTransElem.setAttribute(
								NWCGConstants.BILL_TRANS_IS_ACCOUNT_SPLIT, "Y");
						BillingTransElem.setAttribute(
								NWCGConstants.BILL_TRANS_SPLIT_AMT_NUMBER, "5");
						BillingTransElem.setAttribute(
								NWCGConstants.BILL_TRANS_AMOUNT, TcostStr);
						try {
							CommonUtilities
									.invokeService(
											env,
											NWCGConstants.NWCG_CREATE_BILLING_TRANSACTION_SERVICE,
											docBillingTransactionIP);
						} catch (Exception e) {
							e.printStackTrace();
							StringBuffer stbuf = new StringBuffer(
									"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransConfirmShipment' "
											+ "DetailDescription='Debit Billing Transaction Record Creation Failed on Confirm Shipment for Order No : "
											+ OrderNo + ",Item Id : " + ItemId
											+ "Split Amount : 5 , Cache ID: "
											+ CacheId + ", Order QTY: "
											+ OrderedQty + ", incident no: "
											+ IncidentNo + ", CreateUser: "
											+ createUserId + "'");
							throwAlert(env, stbuf);
						}
					}
				}
			}
		} catch (Exception e) {
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingTransConfirmShipment::InsertBillingTransactionRecord Caught Exception "
								+ e);
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransConfirmShipment' "
							+ "DetailDescription='InsertBillingTransactionRecord Failed during NWCGProcessBillingTransConfirmShipment, on Order Number: "
							+ tempOrderNo + ", Document Type: "
							+ tempDocumentType + " Cache ID: " + tempCacheId
							+ ", createUder: " + tempcreateUserId + "'");
			throwAlert(env, stbuf);
		}
		return inXML;
	}

	private Document getBillingTransInputDocument(YFSEnvironment env)
			throws Exception {
		Document returnDoc = XMLUtil.createDocument("NWCGBillingTransaction");
		Element returnDocElem = returnDoc.getDocumentElement();
		returnDocElem.setAttribute(NWCGConstants.BILL_TRANS_TYPE,
				"ISSUE CONFIRM SHIPMENT");
		return returnDoc;
	}

	public Document insertBillingTransRecord(YFSEnvironment env, Document doc)
			throws Exception {
		return InsertRecord(env, doc);
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
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransConfirmShipment' "
							+ "DetailDescription='GetItemDetails Failed during NWCGProcessBillingTransConfirmShipment, for Item ID : "
							+ ItemId + "'");
			throwAlert(env, stbuf);
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingTransConfirmShipment::getItemDetails Caught Exception "
								+ e);
		}
		Element GetItemDetailsOPElem = getItemDetailsOP.getDocumentElement();
		return GetItemDetailsOPElem;
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
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransConfirmShipment' "
							+ "DetailDescription='GetOrganizationList Failed during NWCGProcessBillingTransConfirmShipment, for Cache ID : "
							+ Cache + "'");
			throwAlert(env, stbuf);
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingTransConfirmShipment::getOrganizationList Caught Exception "
								+ e);
		}
		Element outDocElem = outDoc.getDocumentElement();
		Element ExtnElm = (Element) outDocElem.getElementsByTagName("Extn")
				.item(0);
		return ExtnElm;
	}
}