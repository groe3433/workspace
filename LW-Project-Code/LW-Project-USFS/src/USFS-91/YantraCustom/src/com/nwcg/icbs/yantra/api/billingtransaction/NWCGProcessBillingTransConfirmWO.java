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
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGProcessBillingTransConfirmWO implements YIFCustomApi,
		NWCGBillingTransRecordMutator {
	
	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGProcessBillingTransConfirmWO.class);
	
	public void setProperties(Properties arg0) throws Exception {
	}

	public void throwAlert(YFSEnvironment env, StringBuffer Message)
			throws Exception {
		Message.append(" ExceptionType='BILLING_TRANS_WO_KIT_DEKIT'"
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
					.verbose("Entering NWCGProcessBillingTransConfirmWO, Input document is:"
							+ XMLUtil.getXMLString(inXML));
		}
		// jimmys temp vars for alerts
		String tempCacheId = "";
		String tempWorkOrderNo = "";
		String tempServiceItemId = "";
		String tempKitItemId = "";
		String tempcreateUserId = "";
		try {
			inXML.getDocumentElement().normalize();
			Element WORootElem = inXML.getDocumentElement();
			String ServiceItemId = WORootElem.getAttribute("ServiceItemID");
			String QtyCompleted = WORootElem.getAttribute("QuantityCompleted");
			String WorkOrderKey = WORootElem.getAttribute("WorkOrderKey");
			String WorkOrderNo = WORootElem.getAttribute("WorkOrderNo");
			String EnterpriseCode = WORootElem.getAttribute("EnterpriseCode");
			String DocumentType = WORootElem.getAttribute("DocumentType");
			String CacheId = WORootElem.getAttribute("NodeKey");
			String StatusDate = WORootElem.getAttribute("StatusDate");
			String KitItemId = WORootElem.getAttribute("ItemID");
			String KitUOM = WORootElem.getAttribute("Uom");
			String createUserId = WORootElem.getAttribute("Createuserid");
			String modifyUserId = WORootElem.getAttribute("Modifyuserid");
			// jimmy populating temp vars
			tempCacheId = CacheId;
			tempWorkOrderNo = WorkOrderNo;
			tempServiceItemId = ServiceItemId;
			tempKitItemId = KitItemId;
			tempcreateUserId = createUserId;
			Element ItemElem = getItemDetails(env, KitItemId, KitUOM,
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
			String ItemDesc = StringUtil.nonNull(ItemListElem1
					.getAttribute("Description"));
			String ProductLine = StringUtil.nonNull(ItemListElem1
					.getAttribute("ProductLine"));
			String SerialFlag = "";
			SerialFlag = StringUtil.nonNull(ItemListElem1
					.getAttribute("SerializedFlag"));
			double ucost = Double.parseDouble(UnitCost);
			Element AcctElem = getOrgAcctCode(env, CacheId);
			String AcctCode = AcctElem.getAttribute("ExtnAdjustAcctCode");
			String OverrideCode = AcctElem.getAttribute("ExtnFSOverrideCode");
			String OwnerAgency = AcctElem.getAttribute("ExtnOwnerAgency");
			/* This needs to be changed in NILE Implementation (8.x release) */
			double pqty = 0;
			double pqty1 = 0;
			double qtyInserted = 0d;
			if (SerialFlag.equals("Y")) {
				pqty = 1;
			} else {
				pqty = Double.parseDouble(QtyCompleted);
				
				qtyInserted = getBillingTransaction(env, pqty, WorkOrderNo, KitItemId);
		        pqty = qtyInserted;
			}
			String TransType = "";
			if (ServiceItemId.equals("DEKITTING")) {
				pqty1 = pqty * -1.0D;
		        TransType = "WO-DEKITTING";
			} else {
				TransType = "WO-KITTING";
		        pqty1 = pqty;
			}
			double Tcost = pqty1 * ucost;
			String TcostStr = Double.toString(Tcost);
			String QtyStr = Double.toString(pqty1);
			Document docBillingTransactionIP = getBillingTransInputDocument(env);
			Element BillingTransElem = docBillingTransactionIP
					.getDocumentElement();
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_NO,
					WorkOrderNo);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_TYPE,
					TransType);
			BillingTransElem.setAttribute(
					NWCGConstants.BILL_TRANS_ENTERPRISE_CODE, EnterpriseCode);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_DATE,
					StatusDate);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_HEADER_KEY,
					WorkOrderKey);
			BillingTransElem.setAttribute(
					NWCGConstants.BILL_TRANS_DOCUMENT_TYPE, DocumentType);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_CACHE_ID,
					CacheId);
			BillingTransElem
					.setAttribute(NWCGConstants.BILL_TRANS_LINE_KEY, "");
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_QTY, QtyStr);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_ID,
					KitItemId);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_UOM, KitUOM);
			BillingTransElem.setAttribute(
					NWCGConstants.BILL_TRANS_ITEM_DESCRIPTION, ItemDesc);
			BillingTransElem.setAttribute(
					NWCGConstants.BILL_TRANS_ITEM_CLASSIFICATION, ItemClass);
			BillingTransElem.setAttribute(
					NWCGConstants.BILL_TRANS_ITEM_PRODUCTLINE, ProductLine);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_UNIT_COST,
					UnitCost);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_AMOUNT,
					TcostStr);
			BillingTransElem.setAttribute(
					NWCGConstants.BILL_TRANS_IS_EXTRACTED, "Y");
			BillingTransElem.setAttribute(
					NWCGConstants.BILL_TRANS_CREATEUSERID, createUserId);
			BillingTransElem.setAttribute(
					NWCGConstants.BILL_TRANS_MODIFYUSERID, modifyUserId);
			if (OwnerAgency.equals("BLM")) {
				BillingTransElem.setAttribute(
						NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE,
						AcctCode);
				BillingTransElem.setAttribute(
						NWCGConstants.BILL_TRANS_LAST_INCIDENT_BLM_ACCT_CODE,
						AcctCode);
			} else if (OwnerAgency.equals("FS")) {
				BillingTransElem.setAttribute(
						NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE,
						AcctCode);
				BillingTransElem.setAttribute(
						NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_ACCT_CODE,
						AcctCode);
				BillingTransElem.setAttribute(
						NWCGConstants.BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE,
						OverrideCode);
				BillingTransElem
						.setAttribute(
								NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_OVERRIDE_CODE,
								OverrideCode);
			} else {
				BillingTransElem.setAttribute(
						NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE,
						AcctCode);
				BillingTransElem.setAttribute(
						NWCGConstants.BILL_TRANS_LAST_INCIDENT_OTHER_ACCT_CODE,
						AcctCode);
			}
			try {
				CommonUtilities.invokeService(env,
						NWCGConstants.NWCG_CREATE_BILLING_TRANSACTION_SERVICE,
						docBillingTransactionIP);
			} catch (Exception e) {
				e.printStackTrace();
				StringBuffer stbuf = new StringBuffer(
						"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransConfirmWO' "
								+ "DetailDescription='Billing Transaction Record Creation Failed on Confirm WO for Service Item ID : "
								+ ServiceItemId + ",Item Id : " + KitItemId
								+ ", Cache ID: " + CacheId
								+ ", WorkOrder Number: " + WorkOrderNo
								+ ", Transaction amount: " + TcostStr + "'");
				throwAlert(env, stbuf);
			}
			NodeList WOCompList = WORootElem
					.getElementsByTagName("WorkOrderComponent");
			for (int i = 0; i < WOCompList.getLength(); i++) {
				Element curWOCompElm = (Element) WOCompList.item(i);
				String CompItemId = curWOCompElm.getAttribute("ItemID");
				String CompUOM = curWOCompElm.getAttribute("Uom");
				ItemElem = getItemDetails(env, CompItemId, CompUOM,
						EnterpriseCode);
				ItemList = ItemElem.getElementsByTagName("ClassificationCodes");
				ItemListElem = (Element) ItemList.item(0);
				ItemClass = StringUtil.nonNull(ItemListElem
						.getAttribute("TaxProductCode"));
				ItemList1 = ItemElem.getElementsByTagName("PrimaryInformation");
				ItemListElem1 = (Element) ItemList1.item(0);
				UnitCost = StringUtil.nonNull(ItemListElem1
						.getAttribute("UnitCost"));
				ItemDesc = StringUtil.nonNull(ItemListElem1
						.getAttribute("Description"));
				ProductLine = StringUtil.nonNull(ItemListElem1
						.getAttribute("ProductLine"));
				ucost = Double.parseDouble(UnitCost);
				QtyCompleted = curWOCompElm.getAttribute("ComponentQuantity");
				double qty = Double.parseDouble(QtyCompleted) * pqty;
				// QC1056 - getBillingTransaction called for the Billing
				// Transaction changes related to the QC

				if (ServiceItemId.equals("KITTING"))
		        {
		          qty *= -1.0D;
		        }
				Tcost = qty * ucost;
				TcostStr = Double.toString(Tcost);
				QtyStr = Double.toString(qty);
				BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_QTY, "");
				BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_ID,
						"");
				BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_UOM, "");
				BillingTransElem.setAttribute(
						NWCGConstants.BILL_TRANS_ITEM_DESCRIPTION, "");
				BillingTransElem.setAttribute(
						NWCGConstants.BILL_TRANS_ITEM_CLASSIFICATION, "");
				BillingTransElem.setAttribute(
						NWCGConstants.BILL_TRANS_ITEM_PRODUCTLINE, "");
				BillingTransElem.setAttribute(
						NWCGConstants.BILL_TRANS_UNIT_COST, "");
				BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_AMOUNT,
						"");
				BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_QTY,
						QtyStr);
				BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_ID,
						CompItemId);
				BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_UOM,
						CompUOM);
				BillingTransElem.setAttribute(
						NWCGConstants.BILL_TRANS_ITEM_DESCRIPTION, ItemDesc);
				BillingTransElem
						.setAttribute(
								NWCGConstants.BILL_TRANS_ITEM_CLASSIFICATION,
								ItemClass);
				BillingTransElem.setAttribute(
						NWCGConstants.BILL_TRANS_ITEM_PRODUCTLINE, ProductLine);
				BillingTransElem.setAttribute(
						NWCGConstants.BILL_TRANS_UNIT_COST, UnitCost);
				BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_AMOUNT,
						TcostStr);
				try {
					CommonUtilities
							.invokeService(
									env,
									NWCGConstants.NWCG_CREATE_BILLING_TRANSACTION_SERVICE,
									docBillingTransactionIP);
				} catch (Exception e) {
					e.printStackTrace();
					StringBuffer stbuf = new StringBuffer(
							"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransConfirmWO' "
									+ "DetailDescription='Billing Transaction Record Creation Failed on Confirm WO for Service Item ID : "
									+ ServiceItemId + ",Item Id : "
									+ CompItemId + ", Cache ID: " + CacheId
									+ ", WorkOrder Number: " + WorkOrderNo
									+ ", Transaction amount: " + TcostStr + "'");
					throwAlert(env, stbuf);
				}
			}
		} catch (Exception e) {
			// jimmy added details to alerts
			logger.error("!!!!! Caught General Exception, NWCGProcessBillingTransConfirmWO::InsertBillingTransactionRecord :: " + e);
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransConfirmWO' "
							+ "DetailDescription='InsertBillingTransactionRecord Failed on Confirm WO, on Cache ID: "
							+ tempCacheId + ", WorkOrder Number: "
							+ tempWorkOrderNo + ", Service Item ID: "
							+ tempServiceItemId + ", Kit Item ID: "
							+ tempKitItemId + ", CreateUser: "
							+ tempcreateUserId + "'");
			;
			throwAlert(env, stbuf);
		}
		return inXML;
	}

	private Document getBillingTransInputDocument(YFSEnvironment env)
			throws Exception {
		Document returnDoc = XMLUtil.createDocument("NWCGBillingTransaction");
		Element returnDocElem = returnDoc.getDocumentElement();
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
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransConfirmWO' "
							+ "DetailDescription='GetOrganizationList Failed during Confirm WO, for Cache ID : "
							+ Cache + "'");
			throwAlert(env, stbuf);
			logger.error("!!!!! Caught General Exception, NWCGProcessBillingTransConfirmWO::getOrganizationList :: " + e);
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
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransConfirmWO' "
							+ "DetailDescription='GetItemDetails Failed during Confirm WO, for Item ID : "
							+ ItemId + "'");
			throwAlert(env, stbuf);
			logger.error("!!!!! Caught General Exception, NWCGProcessBillingTransConfirmWO::getItemDetails :: " + e);
		}
		Element GetItemDetailsOPElem = getItemDetailsOP.getDocumentElement();
		return GetItemDetailsOPElem;
	}

	/**
	 * 
	 * QC1056 - Kitting Workorder - partial quantity confirmation problem This
	 * method fixes the issue where, when partially confirming the quantity the
	 * billing transaction was being updated wrongly.
	 * 
	 * With the fix, the exact quantity being partially confirmed will be
	 * updated in the billing transaction. The quantity being inserted will be
	 * calculated by calling getBillingTransactionListService and summing up the
	 * quantities that have already been inserted in the billing transaction for
	 * the same itemID.
	 * 
	 * @param env
	 *            YFSEnvironment representing environment variable.
	 * @param pqty
	 *            double QuantityCompleted
	 * @param WorkOrderNo
	 *            String work order number
	 * @param KitItemId
	 *            String Kit Item ID
	 * @return qtyToBeInsertedInBillingTrans double quantity that will be
	 *         inserted in the billing transaction
	 * @throws Exception
	 */
	private double getBillingTransaction(YFSEnvironment env, double pqty,String WorkOrderNo, String ItemId)
			throws Exception {
		double qtyToBeInsertedInBillingTrans = 0d;
		Document inDoc = XMLUtil.getDocument();
		Element el = inDoc.createElement("NWCGBillingTransactionList");
		el.setAttribute(NWCGConstants.BILL_TRANS_NO, WorkOrderNo);
		el.setAttribute(NWCGConstants.BILL_TRANS_ITEM_ID, ItemId);
		inDoc.appendChild(el);
		logger.verbose(" GetBillingTransactionList inDoc : " + XMLUtil.getXMLString(inDoc));
		try {
			Document outXML = CommonUtilities.invokeService(env,NWCGConstants.NWCG_GET_BILLING_TRANSACTION_LIST_SERVICE,inDoc);
			Element outXMLElem = outXML.getDocumentElement();
			NodeList bList = outXMLElem.getElementsByTagName("NWCGBillingTransaction");
			int length = bList.getLength();
			if (length == 0) {
				qtyToBeInsertedInBillingTrans = pqty;
			}
			if (length > 0) {
				double totalQuantityFromBilling = 0d;
				for (int numListIterator = 0; numListIterator < length; numListIterator++) {
					Element curBillingList = (Element) bList.item(numListIterator);
					String quantityFromBilling = curBillingList.getAttribute(NWCGConstants.BILL_TRANS_QTY);
					if (!StringUtil.isEmpty(quantityFromBilling)) {
						totalQuantityFromBilling += Math.abs(Double.valueOf(quantityFromBilling));
					}
				}
				qtyToBeInsertedInBillingTrans = pqty - totalQuantityFromBilling;
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringBuffer alert = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransConfirmWO.getBillingTransaction' "
							+ "DetailDescription='Get Billing Transaction List Not Returned, during Confirm WO,  : "
							+ ",Item Id : "
							+ ItemId
							+ ", Work Order No : " + WorkOrderNo + "'");
			throwAlert(env, alert);
		}
		return qtyToBeInsertedInBillingTrans;
	}
}