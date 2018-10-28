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

/*
 * This API Creates ONE Billing Transaction Record in NWCG_BILLING_TRANSACTION Table for each Item Inventory Adjustment 
 * The Increase in Qty will create a Credit Transaction and the Decrease in Qty will create a Debit Transaction for the CACHE
 */
public class NWCGProcessBillingTransAdjLocationInventory implements
		YIFCustomApi, NWCGBillingTransRecordMutator {
	
	private static Logger logger = Logger
			.getLogger(NWCGProcessBillingTransAdjLocationInventory.class
					.getName());

	public void setProperties(Properties arg0) throws Exception {
	}

	public void throwAlert(YFSEnvironment env, StringBuffer Message)
			throws Exception {
		Message.append(" ExceptionType='BILLING_TRANS_ADJ_LOC_INV'"
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
					.verbose("Entering NWCGProcessBillingTransAdjLocationInventory, Input document is:"
							+ XMLUtil.getXMLString(inXML));
		}
		// jimmy setting up temp vars to populate alerts
		String tempCacheId = "";
		String tempLocationId = "";
		String tempInvStatus = "";
		String tempQuantity = "";
		String tempTransactionNo = "";
		String tempIncidentNo = "";
		String tempIncidentYear = "";
		String tempItemId = "";
		try {
			inXML.getDocumentElement().normalize();
			Element elemInvRoot = inXML.getDocumentElement();
			if (elemInvRoot != null) {
				NodeList listOfAdjLocnInventory = inXML
						.getElementsByTagName("AdjustLocationInventory");
				for (int i = 0; i < listOfAdjLocnInventory.getLength(); i++) {
					Element curInvRecord = (Element) listOfAdjLocnInventory
							.item(i);
					String EnterpriseCode = curInvRecord
							.getAttribute("EnterpriseCode");
					String CacheId = curInvRecord.getAttribute("Node");
					String createUserId = curInvRecord
							.getAttribute("Createuserid");
					String modifyUserId = curInvRecord
							.getAttribute("Modifyuserid");
					Element curInvSource = (Element) curInvRecord
							.getElementsByTagName("Source").item(0);
					String LocationId = curInvSource.getAttribute("LocationId");
					Element curInventory = (Element) curInvSource
							.getElementsByTagName("Inventory").item(0);
					String InvStatus = curInventory
							.getAttribute("InventoryStatus");
					// Getting Item Details
					Document getItemDetailsIP = XMLUtil.createDocument("Item");
					Element GetItemDetailsIPElem = getItemDetailsIP
							.getDocumentElement();
					Element curInvItem = (Element) curInventory
							.getElementsByTagName("InventoryItem").item(0);
					String ItemId = curInvItem.getAttribute("ItemID");
					String UOM = curInvItem.getAttribute("UnitOfMeasure");

					GetItemDetailsIPElem.setAttribute("ItemID", ItemId);
					GetItemDetailsIPElem.setAttribute("UnitOfMeasure", UOM);
					GetItemDetailsIPElem.setAttribute("OrganizationCode",
							EnterpriseCode);
					Document getItemDetailsOP = null;
					// jimmy populating temp vars
					tempCacheId = CacheId;
					tempLocationId = LocationId;
					tempInvStatus = InvStatus;
					tempQuantity = ItemId;
					try {
						getItemDetailsOP = CommonUtilities.invokeAPI(env,
								"getItemDetails", getItemDetailsIP);
					} catch (Exception e) {
						// Jimmy included what api it failed in
						e.printStackTrace();
						StringBuffer stbuf = new StringBuffer(
								"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransAdjLocnInv' "
										+ "DetailDescription='GetItemDetails Failed for Item ID : "
										+ ItemId
										+ ", during NWCGProcessBillingTransAdjLocnInv process'");
						throwAlert(env, stbuf);
						if (logger.isVerboseEnabled())
							logger
									.verbose("NWCGProcessBillingTransAdjLocationInventory::getItemDetails Caught Exception "
											+ e);
					}
					Element GetItemDetailsOPElem = getItemDetailsOP
							.getDocumentElement();
					NodeList ItemList = GetItemDetailsOPElem
							.getElementsByTagName("ClassificationCodes");
					Element ItemListElem = (Element) ItemList.item(0);
					String ItemClass = StringUtil.nonNull(ItemListElem
							.getAttribute("TaxProductCode"));
					NodeList ItemList1 = GetItemDetailsOPElem
							.getElementsByTagName("PrimaryInformation");
					Element ItemListElem1 = (Element) ItemList1.item(0);
					String UnitCost = StringUtil.nonNull(ItemListElem1
							.getAttribute("UnitCost"));
					String ItemDesc = StringUtil.nonNull(ItemListElem1
							.getAttribute("Description"));
					String ProductLine = StringUtil.nonNull(ItemListElem1
							.getAttribute("ProductLine"));
					if (UnitCost.length() == 0) {
						StringBuffer stbuf = new StringBuffer(
								"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransAdjLocnInv' "
										+ "DetailDescription='Unit Cost is 0 for Item Id "
										+ ItemId
										+ ", during NWCGProcessBillingTransAdjLocnInv process'");
						throwAlert(env, stbuf);
					}
					double ucost = Double.parseDouble(UnitCost);
					String Quantity = curInventory.getAttribute("Quantity");
					if (Quantity.equals("") || Quantity.length() == 0) {
						// If Item is Trackable, getting count of all Trackable
						// IDs
						Quantity = SetSerialQty(env, curInventory);
					}
					double qty = Double.parseDouble(Quantity);
					double Tcost = qty * ucost;
					String TcostStr = Double.toString(Tcost);
					Document docBillingTransactionIP = getBillingTransInputDocument(env);
					Element BillingTransElem = docBillingTransactionIP
							.getDocumentElement();
					NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
					// String TNo = reportsUtil.dateToString(new
					// java.util.Date(),"yyyyMMddhhmmssSS");
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_ENTERPRISE_CODE,
							EnterpriseCode);
					String strDate = reportsUtil.dateToString(
							new java.util.Date(), "yyyy-MM-dd'T'HH:mm:ss");
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_DATE, strDate);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_HEADER_KEY, "");
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_DOCUMENT_TYPE, "N/A");
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_CACHE_ID, CacheId);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_LINE_KEY, "");
					BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_QTY,
							Quantity);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_DISPOSITION_CODE,
							InvStatus);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_ITEM_ID, ItemId);
					BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_UOM,
							UOM);
					BillingTransElem
							.setAttribute(
									NWCGConstants.BILL_TRANS_ITEM_DESCRIPTION,
									ItemDesc);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_ITEM_CLASSIFICATION,
							ItemClass);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_ITEM_PRODUCTLINE,
							ProductLine);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_UNIT_COST, UnitCost);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_AMOUNT, TcostStr);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_LOCATION_ID, LocationId);
					BillingTransElem
							.setAttribute(
									NWCGConstants.BILL_TRANS_CREATEUSERID,
									createUserId);
					BillingTransElem
							.setAttribute(
									NWCGConstants.BILL_TRANS_MODIFYUSERID,
									modifyUserId);

					Element curInvAudit = (Element) curInvRecord
							.getElementsByTagName("Audit").item(0);
					String OverrideCode = "";
					String AcctCode = "";
					// CR 489
					String ReasonCode = curInvAudit.getAttribute("ReasonCode");
					String ReasonText = curInvAudit.getAttribute("ReasonText");
					String OwnerAgency = curInvAudit
							.getAttribute("OwnerAgency");
					String TransactionNo = curInvAudit
							.getAttribute("Reference1");
					String IncidentNo = curInvAudit.getAttribute("Reference2");
					String AcctCodeStr = curInvAudit.getAttribute("Reference3");
					String[] Acctfields = AcctCodeStr.split("~");
					if (Acctfields.length > 1) {
						AcctCode = Acctfields[0];
						OverrideCode = Acctfields[1];
					}
					String IncidentName = curInvAudit
							.getAttribute("Reference4");
					String IncidentYear = curInvAudit
							.getAttribute("Reference5");
					// jimmy populate rest of temp vars for alerts
					tempTransactionNo = TransactionNo;
					tempIncidentNo = IncidentNo;
					tempIncidentYear = IncidentYear;
					if ((AcctCode.length() == 0) || (OwnerAgency.length() == 0)) {
						Element AcctElem = getOrgAcctCode(env, CacheId);
						AcctCode = AcctElem.getAttribute("ExtnAdjustAcctCode");
						OverrideCode = AcctElem
								.getAttribute("ExtnFSOverrideCode");
						OwnerAgency = AcctElem.getAttribute("ExtnOwnerAgency");
					}
					BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_NO,
							TransactionNo);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_INCIDENT_NO, IncidentNo);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_INCIDENT_YEAR,
							IncidentYear);
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_INCIDENT_NAME,
							IncidentName);
					if (OwnerAgency.equals("BLM")) {
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE,
										AcctCode);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_BLM_ACCT_CODE,
										AcctCode);
					} else if (OwnerAgency.equals("FS")) {
						BillingTransElem.setAttribute(
								NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE,
								AcctCode);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_ACCT_CODE,
										AcctCode);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE,
										OverrideCode);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_OVERRIDE_CODE,
										OverrideCode);
					} else {
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE,
										AcctCode);
						BillingTransElem
								.setAttribute(
										NWCGConstants.BILL_TRANS_LAST_INCIDENT_OTHER_ACCT_CODE,
										AcctCode);
					}
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_IS_EXTRACTED, "Y");
					// CR 489
					BillingTransElem.setAttribute("ReasonCode", ReasonCode);
					BillingTransElem.setAttribute("ReasonCodeText", ReasonText);
					try {
						CommonUtilities
								.invokeService(
										env,
										NWCGConstants.NWCG_CREATE_BILLING_TRANSACTION_SERVICE,
										docBillingTransactionIP);
					} catch (Exception e) {
						e.printStackTrace();
						StringBuffer stbuf = new StringBuffer(
								"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransAdjLocnInv' "
										+ "DetailDescription='Billing Transaction Record Creation Failed on Adjust Location Inventory for Item ID : "
										+ ItemId + "Location Id : "
										+ LocationId + "Qty : " + Quantity
										+ ", deposition code: " + InvStatus
										+ "'");
						throwAlert(env, stbuf);
					}
					if (logger.isVerboseEnabled()) {
						logger
								.verbose("Exiting the NWCGProcessBillingTransAdjLocationInventory, Output Document is:"
										+ XMLUtil
												.getXMLString(docBillingTransactionIP));
					}
				}
			} else {
				StringBuffer stbuf = new StringBuffer(
						"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransAdjLocnInv' "
								+ "DetailDescription='No Inventory Record- Input Document to NWCGProcessBillingTransAdjLocnInv was empty'");
				throwAlert(env, stbuf);
			}
		} catch (Exception e) {
			// jimmy added all detail to catch alert
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingTransAdjLocationInventory::createBillingTransactionRecord Caught Exception "
								+ e);
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransAdjLocnInv' "
							+ "DetailDescription='Billing Transaction Record Creation Failed on Adjust Location Inventory, for Cache ID: "
							+ tempCacheId + " , Location Id: " + tempLocationId
							+ ", Inventory status: " + tempInvStatus
							+ ", Quantity: " + tempQuantity
							+ " , Transaction Number: " + tempTransactionNo
							+ ", Incident Number: " + tempIncidentNo
							+ ", Year:" + tempIncidentYear + "'");
			throwAlert(env, stbuf);
		}
		return inXML;
	}

	private String SetSerialQty(YFSEnvironment env, Element InvElm)
			throws Exception {
		String QtyStr, TotQtyStr = "";
		double Qty, TotQty;
		TotQty = 0;
		NodeList SerialList = InvElm.getElementsByTagName("SerialDetail");
		for (int i = 0; i < SerialList.getLength(); i++) {
			Element curSerialElm = (Element) SerialList.item(i);
			QtyStr = curSerialElm.getAttribute("Quantity");
			Qty = Double.parseDouble(QtyStr);
			TotQty += Qty;
		}
		TotQtyStr = Double.toString(TotQty);
		return TotQtyStr;
	}

	private Document getBillingTransInputDocument(YFSEnvironment env)
			throws Exception {
		Document returnDoc = XMLUtil.createDocument("NWCGBillingTransaction");
		Element returnDocElem = returnDoc.getDocumentElement();
		returnDocElem.setAttribute(NWCGConstants.BILL_TRANS_TYPE,
				"ADJ LOCATION INVENTORY");
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
		} catch (Exception e) {//jimmy added the api it failed in
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransAdjLocnInv' "
							+ "DetailDescription='GetOrganizationList Failed, during NWCGProcessBillingTransAdjLocnInv process for Cache ID : "
							+ Cache + "'");
			throwAlert(env, stbuf);
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingTransAdjLocationInventory::getOrganizationList Caught Exception "
								+ e);
		}
		Element outDocElem = outDoc.getDocumentElement();
		Element ExtnElm = (Element) outDocElem.getElementsByTagName("Extn")
				.item(0);
		return ExtnElm;
	}
}