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

import com.nwcg.icbs.yantra.reports.loftware.NWCGReportsUtil;
import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGProcessBillingTransReceivePurchaseOrder implements YIFCustomApi, NWCGBillingTransRecordMutator {
	
	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGProcessBillingTransReceivePurchaseOrder.class);
	
	public void setProperties(Properties arg0) throws Exception {
	}

	public void throwAlert(YFSEnvironment env, StringBuffer Message)throws Exception {
		Message.append(" ExceptionType='BILLING_TRANS_RECEIVE_PO'" + " InboxType='" + NWCGConstants.NWCG_BILL_TRANS_INBOXTYPE + "' QueueId='" + NWCGConstants.NWCG_BILL_TRANS_QUEUEID + "' />");
		if (logger.isVerboseEnabled())
			logger.verbose("Throw Alert Method called with message:-" + Message.toString());
		try {
			Document inTemplate = XMLUtil.getDocument(Message.toString());
			CommonUtilities.invokeAPI(env, "createException", inTemplate);
		} catch (Exception ex1) {
			throw new NWCGException("NWCG_BILLING_TRANSACTION_ERROR_WHILE_LOGGING_ALERT");
		}
	}

	public Document InsertRecord(YFSEnvironment env, Document inXML) throws Exception {
		if (logger.isVerboseEnabled())
			logger.verbose("Entering NWCGProcessBillingTransReceivePO, Input document is:" + XMLUtil.getXMLString(inXML));
		// Jimmy - temp cars for alerts description
		String tempReceiptDate = "";
		String tempCacheId = "";
		String tempArrivalDateTime = "";
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
						String EnterpriseCode = curReceiptList.getAttribute("EnterpriseCode");
						String ReceiptDate = curReceiptList.getAttribute("ReceiptDate");
						String ArrivalDateTime = curReceiptList.getAttribute("ArrivalDateTime");
						String DocumentType = curReceiptList.getAttribute("DocumentType");
						String CacheId = curReceiptList.getAttribute("ReceivingNode");
						// Jimmy - populating temp vars for alerts
						tempReceiptDate = ReceiptDate;
						tempCacheId = CacheId;
						tempArrivalDateTime = ArrivalDateTime;
						Element AcctElem = getOrgAcctCode(env, CacheId);
						String AcctCode = AcctElem.getAttribute("ExtnRecvAcctCode");
						String OverrideCode = AcctElem.getAttribute("ExtnRAOverrideCode");
						String OwnerAgency = AcctElem.getAttribute("ExtnOwnerAgency");
						NodeList listOfReceiptLines = curReceiptList.getElementsByTagName("ReceiptLine");
						for (int j = 0; j < listOfReceiptLines.getLength(); j++) {
							Node RLNode = listOfReceiptLines.item(j);
							if (RLNode.getNodeType() == Node.ELEMENT_NODE) {
								curReceiptLineList = (Element) listOfReceiptLines.item(j);
								String DispositionCode = curReceiptLineList.getAttribute("DispositionCode");
								String OrderNo = curReceiptLineList.getAttribute("OrderNo");
								String OrderLineKey = curReceiptLineList.getAttribute("OrderLineKey");
								String OrderHeaderKey = curReceiptLineList.getAttribute("OrderHeaderKey");
								String ActualQty = curReceiptLineList.getAttribute("Quantity");
								String ItemId = curReceiptLineList.getAttribute("ItemID");
								String LocationId = curReceiptLineList.getAttribute("LocationId");
								String UOM = curReceiptLineList.getAttribute("UnitOfMeasure");
								Element ExtnElm = (Element) curReceiptLineList.getElementsByTagName("Extn").item(0);
								String ReceivingPrice = ExtnElm.getAttribute("ExtnReceivingPrice"); 
								Document docBillingTransactionIP = getBillingTransInputDocument(env);
								Element BillingTransElem = docBillingTransactionIP.getDocumentElement();
								NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_NO, OrderNo);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ENTERPRISE_CODE, EnterpriseCode);
								String strDate = reportsUtil.dateToString(new java.util.Date(), "yyyy-MM-dd'T'HH:mm:ss");
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_DATE, strDate);
								// CR 458 -- set billing trans date with
								// ArrivalDateTime
								// BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_DATE,ArrivalDateTime);
								// Reversing CR 458 - GN
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_HEADER_KEY, OrderHeaderKey);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_DOCUMENT_TYPE, DocumentType);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_CACHE_ID, CacheId);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LINE_KEY, OrderLineKey);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_QTY, ActualQty);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_ID, ItemId);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_UOM, UOM);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LOCATION_ID, LocationId);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_DISPOSITION_CODE, DispositionCode);
								Element ItemElem = getItemDetails(env, ItemId, UOM, EnterpriseCode);
								NodeList ItemList = ItemElem.getElementsByTagName("ClassificationCodes");
								Element ItemListElem = (Element) ItemList.item(0);
								String ItemClass = StringUtil.nonNull(ItemListElem.getAttribute("TaxProductCode"));
								NodeList ItemList1 = ItemElem.getElementsByTagName("PrimaryInformation");
								Element ItemListElem1 = (Element) ItemList1.item(0);
								String UnitCost = StringUtil.nonNull(ItemListElem1.getAttribute("UnitCost"));
								String ItemDesc = StringUtil.nonNull(ItemListElem1.getAttribute("Description"));
								String ProductLine = StringUtil.nonNull(ItemListElem1.getAttribute("ProductLine"));
								double rprice = Double.parseDouble(ReceivingPrice);
								if (!(rprice > 0)) {
									rprice = Double.parseDouble(UnitCost);
									ReceivingPrice = UnitCost;
								}
								double qty = Double.parseDouble(ActualQty);
								if (!(rprice > 0)) {
									StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransReceivePO' " + "DetailDescription='Unit Price is 0 for Order No : " + OrderNo + ",Item Id : " + ItemId + ", occurred during NWCGProcessBillingTransReceivePO'");
									throwAlert(env, stbuf);
								}
								double Tcost = qty * rprice;
								String TcostStr = Double.toString(Tcost);
								String QtyStr = Double.toString(qty);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_DESCRIPTION, ItemDesc);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_UNIT_COST, ReceivingPrice);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_AMOUNT, TcostStr);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_CLASSIFICATION, ItemClass);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_PRODUCTLINE, ProductLine);
								Document getOrderDetailsIP = XMLUtil.createDocument("Order");
								Element GetOrderDetailsIPElem = getOrderDetailsIP.getDocumentElement();
								GetOrderDetailsIPElem.setAttribute("OrderHeaderKey", OrderHeaderKey);
								Document getOrderDetailsOP = null;
								try {
									logger.verbose("@@@@@ getOrderDetailsIP :: " + XMLUtil.getXMLString(getOrderDetailsIP));
									getOrderDetailsOP = CommonUtilities.invokeAPI(env, "NWCGProcessBillingTrans_getOrderDetails", "getOrderDetails", getOrderDetailsIP);
									logger.verbose("@@@@@ getOrderDetailsOP :: " + XMLUtil.getXMLString(getOrderDetailsOP));
								} catch (Exception e) {
									StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransReceivePO' " + "DetailDescription='GetOrderDetails Failed during NWCGProcessBillingTransReceivePO, for OrderHeaderKey : " + OrderHeaderKey + "'");
									throwAlert(env, stbuf);
									logger.error("!!!!! Caught General Exception, NWCGProcessBillingTransReceivePO::GetOrderDetails :: " + e);
								}
								Element GetOrderDetailsOPElem = getOrderDetailsOP.getDocumentElement();
								NodeList nlExtn = GetOrderDetailsOPElem.getElementsByTagName("Extn");
								Element elemExtn = (Element) nlExtn.item(0);
								String RecvAcctCode = StringUtil.nonNull(elemExtn.getAttribute("ExtnRecvAcctCode"));
								String RecvOverrideCode = StringUtil.nonNull(elemExtn.getAttribute("ExtnRAOverrideCode"));
								String IncidentNo = StringUtil.nonNull(elemExtn.getAttribute("ExtnIncidentNo"));
								String IncidentYear = StringUtil.nonNull(elemExtn.getAttribute("ExtnIncidentYear"));
								String IncidentName = StringUtil.nonNull(elemExtn.getAttribute("ExtnIncidentName"));
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_NO, IncidentNo);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_YEAR, IncidentYear);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_NAME, IncidentName);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_IS_EXTRACTED, "Y");
								String createUserId = GetOrderDetailsOPElem.getAttribute("Createuserid");
								logger.verbose("@@@@@ createUserId :: " + createUserId);
								String modifyUserId = GetOrderDetailsOPElem.getAttribute("Modifyuserid");
								logger.verbose("@@@@@ modifyUserId :: " + modifyUserId);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_CREATEUSERID, createUserId);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_MODIFYUSERID, modifyUserId);
								if (RecvAcctCode.length() == 0) {
									RecvAcctCode = AcctCode;
									RecvOverrideCode = OverrideCode;
								}
								if (OwnerAgency.equals("BLM")) {
									BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE, RecvAcctCode);
									BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LAST_INCIDENT_BLM_ACCT_CODE, RecvAcctCode);
								} else if (OwnerAgency.equals("FS")) {
									BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE, RecvAcctCode);
									BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_ACCT_CODE, RecvAcctCode);
									BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE, RecvOverrideCode);
									BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_OVERRIDE_CODE, RecvOverrideCode);
								} else {
									BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE, RecvAcctCode);
									BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LAST_INCIDENT_OTHER_ACCT_CODE, RecvAcctCode);
								}
								try {
									CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_BILLING_TRANSACTION_SERVICE, docBillingTransactionIP);
								} catch (Exception e) {
									StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransReceivePO' " + "DetailDescription='Billing Transaction Record Creation Failed on Receive PO for Order No : " + OrderNo + ",Item Id : " + ItemId + ", Cache ID: " + CacheId + ", Deposition Code: " + DispositionCode + ", Location ID : " + LocationId + ", arrival date and time: " + tempArrivalDateTime + "'");
									throwAlert(env, stbuf);
								}
							}
						} 
					} 
				}
			} 
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception, NWCGProcessBillingTransReceivePO::InsertBillingTransactionRecord :: " + e);
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransReceivePO' " + "DetailDescription='InsertBillingTransactionRecord Failed during NWCGProcessBillingTransReceivePO. Where Recieving Cache Id: " + tempCacheId + ", Reciept date: " + tempReceiptDate + "'");
			throwAlert(env, stbuf);
		}
		return inXML;
	}

	private Document getBillingTransInputDocument(YFSEnvironment env) throws Exception {
		Document returnDoc = XMLUtil.createDocument("NWCGBillingTransaction");
		Element returnDocElem = returnDoc.getDocumentElement();
		returnDocElem.setAttribute(NWCGConstants.BILL_TRANS_TYPE, "RECEIVE PO");
		return returnDoc;
	}

	public Document insertBillingTransRecord(YFSEnvironment env, Document doc) throws Exception {
		return InsertRecord(env, doc);
	}

	public Element getItemDetails(YFSEnvironment env, String ItemId, String UOM, String EnterpriseCode) throws Exception {
		Document getItemDetailsIP = XMLUtil.createDocument("Item");
		Element GetItemDetailsIPElem = getItemDetailsIP.getDocumentElement();
		GetItemDetailsIPElem.setAttribute("ItemID", ItemId);
		GetItemDetailsIPElem.setAttribute("UnitOfMeasure", UOM);
		GetItemDetailsIPElem.setAttribute("OrganizationCode", EnterpriseCode);
		Document getItemDetailsOP = null;
		try {
			getItemDetailsOP = CommonUtilities.invokeAPI(env, "getItemDetails", getItemDetailsIP);
		} catch (Exception e) {
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransReceivePO' " + "DetailDescription='GetItemDetails Failed for Item ID : " + ItemId + ", during process billing transaction on receive PO'");
			throwAlert(env, stbuf);
			logger.error("!!!!! Caught General Exception, NWCGProcessBillingTransReceivePO::getItemDetails :: " + e);
		}
		Element GetItemDetailsOPElem = getItemDetailsOP.getDocumentElement();
		return GetItemDetailsOPElem;
	}

	public Element getOrgAcctCode(YFSEnvironment env, String Cache) throws Exception {
		Document inDoc = XMLUtil.newDocument();
		Document outDoc = XMLUtil.newDocument();
		Element el_NWCGOrganization = inDoc.createElement("Organization");
		inDoc.appendChild(el_NWCGOrganization);
		el_NWCGOrganization.setAttribute("OrganizationCode", Cache);
		try {
			outDoc = CommonUtilities.invokeAPI(env, "NWCGProcessBillingTrans_getOrganizationList", "getOrganizationList", inDoc);
		} catch (Exception e) {
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransReceivePO' " + "DetailDescription='GetOrganizationList Failed for Cache ID : " + Cache + ", during process billing transaction on receive PO'");
			throwAlert(env, stbuf);
			logger.error("!!!!! Caught General Exception, NWCGProcessBillingTransReceivePO::getOrganizationList :: " + e);
		}
		Element outDocElem = outDoc.getDocumentElement();
		Element ExtnElm = (Element) outDocElem.getElementsByTagName("Extn").item(0);
		return ExtnElm;
	}
}