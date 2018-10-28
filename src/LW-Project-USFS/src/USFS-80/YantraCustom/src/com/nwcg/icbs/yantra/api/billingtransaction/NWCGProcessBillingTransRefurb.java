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
import com.nwcg.icbs.yantra.reports.loftware.NWCGReportsUtil;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * 
 * @author Oxford Consulting Group
 * @version 1.0
 * @date April 15, 2013
 */
public class NWCGProcessBillingTransRefurb implements YIFCustomApi, NWCGBillingTransRecordMutator {
	
	private static Logger logger = Logger.getLogger(NWCGProcessBillingTransConfirmShipment.class.getName());

	double totrefurbamt = 0.00;

	/**
	 * 
	 * @param arg0
	 * @throws Exception
	 */
	public void setProperties(Properties arg0) throws Exception {
	}

	/**
	 * 
	 * @param env
	 * @param Message
	 * @throws Exception
	 */
	public void throwAlert(YFSEnvironment env, StringBuffer Message) throws Exception {
		Message.append(" ExceptionType='BILLING_TRANS_REFURB'" + " InboxType='" + NWCGConstants.NWCG_BILL_TRANS_INBOXTYPE + "' QueueId='" + NWCGConstants.NWCG_BILL_TRANS_QUEUEID + "' />");
		logger.verbose("!!!!! Throw Alert Method called with message:-" + Message.toString());
		try {
			Document inTemplate = XMLUtil.getDocument(Message.toString());
			CommonUtilities.invokeAPI(env, "createException", inTemplate);
		} catch (Exception ex1) {
			throw new NWCGException("NWCG_BILLING_TRANSACTION_ERROR_WHILE_LOGGING_ALERT");
		}
	}

	/**
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 * @throws Exception
	 */
	public Document InsertRecord(YFSEnvironment env, Document inXML) throws Exception {
		/*
		 * <WorkOrder BillingTransactionAction="REFURB" DocumentType="7001"
		 * EnterpriseCode="NWCG" EnterpriseCodeForComponent="NWCG"
		 * EnterpriseInvOrg="NWCG" IgnoreOrdering="Y" ItemID="000146"
		 * Node="NMSFK" ProductClass="Supply" QuantityRequested="5.0" Uom="EA"
		 * UserID="dclark"> <NWCGMasterWorkOrderLine ActualQuantity="5.0"
		 * DestinationInventoryStatus="RFI" LocationID="TOOL-1"
		 * MasterWorkOrderKey="200906181658083801531"
		 * MasterWorkOrderLineKey="200906181658093801533"
		 * RefurbCost="12.600000000000001" RefurbishedQuantity="5"/>
		 * <WorkOrderComponents> <WorkOrderComponent ComponentQuantity="3.0"
		 * ItemID="000257" ItemUnitPrice="4.2" KitQuantity=""
		 * ProductClass="Supply" ReplaceComponent="NA" Uom="EA"
		 * YFC_NODE_NUMBER="1"> <Extn RefurbCost="12.600000000000001"/>
		 * </WorkOrderComponent> </WorkOrderComponents> </WorkOrder>
		 */

		//jimmy - Temp vars for alerts
		String tempLocationId = "";
		String tempCacheId ="";
		String tempMasterWorkOrderNo = "";
		
		try {
			inXML.getDocumentElement().normalize();
			Element WORootElem = inXML.getDocumentElement();
			String QtyCompleted = "";
			String KitItemId = WORootElem.getAttribute("ItemID");
			String KitUOM = WORootElem.getAttribute("Uom");
			String WorkOrderNo = WORootElem.getAttribute("WorkOrderNo");
			String DocumentType = WORootElem.getAttribute("DocumentType");
			Element MWOLine = (Element) WORootElem.getElementsByTagName("NWCGMasterWorkOrderLine").item(0);
			String MWOKey = MWOLine.getAttribute("MasterWorkOrderKey");
			String MWOLineKey = MWOLine.getAttribute("MasterWorkOrderLineKey");
			String DispositionCode = MWOLine.getAttribute("DestinationInventoryStatus");
			// String RefurbQty = MWOLine.getAttribute("RefurbishedQuantity");
			String isReplacedItem = MWOLine.getAttribute("IsReplacedItem");
			String RefurbQty = WORootElem.getAttribute("QuantityRequested");
			String LocationId = MWOLine.getAttribute("LocationID");
			Document MWODetails = getMWODetails(env, MWOKey);
			Element MWODetailsElem = MWODetails.getDocumentElement();
			String EnterpriseCode = MWODetailsElem.getAttribute("Enterprise");
			String CacheId = MWODetailsElem.getAttribute("Node");
			String IncidentNo = MWODetailsElem.getAttribute("IncidentNo");
			String IncidentYear = MWODetailsElem.getAttribute("IncidentYear");
			String MasterWorkOrderNo = MWODetailsElem.getAttribute("MasterWorkOrderNo");
			String Modifyts = MWODetailsElem.getAttribute("Modifyts");
			String BlmAcctCode = MWODetailsElem.getAttribute("BLMAccountCode");
			String FsAcctCode = MWODetailsElem.getAttribute("FSAccountCode");
			String OtherAcctCode = MWODetailsElem.getAttribute("OtherAccountCode");
			String OverrideCode = MWODetailsElem.getAttribute("OverrideCode");
			String mwoType = MWODetailsElem.getAttribute("MasterWorkOrderType");
			//Jimmy - populating temp vars
			tempLocationId = LocationId;
			tempCacheId =CacheId;
			tempMasterWorkOrderNo = MasterWorkOrderNo;
			Element ItemElem = getItemDetails(env, KitItemId, KitUOM, EnterpriseCode);
			NodeList ItemList = ItemElem.getElementsByTagName("ClassificationCodes");
			Element ItemListElem = (Element) ItemList.item(0);
			String KitItemClass = StringUtil.nonNull(ItemListElem.getAttribute("TaxProductCode"));
			NodeList ItemList1 = ItemElem.getElementsByTagName("PrimaryInformation");
			Element ItemListElem1 = (Element) ItemList1.item(0);
			String KitItemDesc = StringUtil.nonNull(ItemListElem1.getAttribute("Description"));
			String KitProductLine = StringUtil.nonNull(ItemListElem1.getAttribute("ProductLine"));
			double rcost = 0.00;
			double unitprice = 0.00;
			Element AcctElem = getOrgAcctCode(env, CacheId);
			String AcctCode = AcctElem.getAttribute("ExtnAdjustAcctCode");
			String OwnerAgency = AcctElem.getAttribute("ExtnOwnerAgency");
			String TransType = "";
			Document docBillingTransactionIP = getBillingTransInputDocument(env);
			Element BillingTransElem = docBillingTransactionIP.getDocumentElement();
			NWCGReportsUtil reportsUtil = new NWCGReportsUtil();
			String TransDate = reportsUtil.dateToString(new java.util.Date(), NWCGConstants.YANTRA_DATE_FORMAT);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_NO, MasterWorkOrderNo);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ENTERPRISE_CODE, EnterpriseCode);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_DATE, TransDate);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_HEADER_KEY, MWOKey);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_DOCUMENT_TYPE, DocumentType);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_CACHE_ID, CacheId);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LINE_KEY, MWOLineKey);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_NO, IncidentNo);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_YEAR, IncidentYear);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_DISPOSITION_CODE, DispositionCode);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LOCATION_ID, LocationId);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE, BlmAcctCode);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LAST_INCIDENT_BLM_ACCT_CODE, BlmAcctCode);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE, FsAcctCode);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_ACCT_CODE, FsAcctCode);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE, OverrideCode);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_OVERRIDE_CODE, OverrideCode);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE, OtherAcctCode);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LAST_INCIDENT_OTHER_ACCT_CODE, OtherAcctCode);
			NodeList WOCompList = WORootElem.getElementsByTagName("WorkOrderComponent");
			Document getReceiptListDoc = callGetReceiptListAPI(env, MasterWorkOrderNo);
			boolean isAccountSplit = checkIfAccountSplit(env, getReceiptListDoc);
			for (int i = 0; i < WOCompList.getLength(); i++) {
				Element curWOCompElm = (Element) WOCompList.item(i);
				Element ExtnElem = (Element) XMLUtil.getChildNodeByName(curWOCompElm, "Extn");
				if (ExtnElem != null) {
					String RefurbCost = ExtnElem.getAttribute("RefurbCost");
					QtyCompleted = curWOCompElm.getAttribute("ComponentQuantity");
					if (RefurbCost != null && !RefurbCost.equals("") && QtyCompleted != null && !QtyCompleted.equals("")) {
						String CompItemId = curWOCompElm.getAttribute("ItemID");
						
						// BEGIN JB9 - May 1, 2013
						
						// ItemUnitPrice="7.83"
						// RefurbCost="15.66"
						String strItemUnitPrice = curWOCompElm.getAttribute("ItemUnitPrice");
						
						// END JB9 - May 1, 2013
						
						String CompUOM = curWOCompElm.getAttribute("Uom");
						ItemElem = getItemDetails(env, CompItemId, CompUOM, EnterpriseCode);
						ItemList = ItemElem.getElementsByTagName("ClassificationCodes");
						ItemListElem = (Element) ItemList.item(0);
						String ItemClass = StringUtil.nonNull(ItemListElem.getAttribute("TaxProductCode"));
						ItemList1 = ItemElem.getElementsByTagName("PrimaryInformation");
						ItemListElem1 = (Element) ItemList1.item(0);
						String ItemDesc = StringUtil.nonNull(ItemListElem1.getAttribute("Description"));
						String ProductLine = StringUtil.nonNull(ItemListElem1.getAttribute("ProductLine"));
						rcost = Double.parseDouble(RefurbCost);

						double qty = Double.parseDouble(QtyCompleted);
						qty = qty * -1;

						double Tcost = rcost * -1;
						totrefurbamt += Tcost;
						String TcostStr = Double.toString(Tcost);
						String QtyStr = Double.toString(qty);

						BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_QTY, "");
						BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_ID, "");
						BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_UOM, "");
						BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_DESCRIPTION, "");
						BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_CLASSIFICATION, "");
						BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_PRODUCTLINE, "");
						BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_UNIT_COST, "");
						BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_AMOUNT, "");
						BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_QTY, QtyStr);
						BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_ID, CompItemId);
						BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_UOM, CompUOM);
						BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_DESCRIPTION, ItemDesc);
						BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_CLASSIFICATION, ItemClass);
						BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_PRODUCTLINE, ProductLine);
						
						// BEGIN JB9 - May 1, 2013
						BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_UNIT_COST, strItemUnitPrice);
						// END JB9 - May 1, 2013
						
						//CR 1151 - Billing Transaction value for consumed inventory
						if (DispositionCode.equals("UNSERVICE") || DispositionCode.equals(NWCGConstants.DISP_MISSING)) {
							// Unserviceable Refurb Items are not considered as
							// either Credit or Debit Transactions
							BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_AMOUNT, TcostStr);
						} else {
							BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_AMOUNT, TcostStr);
						}
						try {
							CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_BILLING_TRANSACTION_SERVICE, docBillingTransactionIP);
						} catch (Exception e) {
							e.printStackTrace();
							StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransRefurb' " + "DetailDescription='Billing Transaction Record Creation Failed on Confirm Refurb WO for Item ID : " + CompItemId + ", MWO number: "+ MasterWorkOrderNo +", Cache ID: "+ CacheId +", incident no: "+ IncidentNo +", Location ID: "+ LocationId +", Dispostition code: "+ DispositionCode +"'");
							throwAlert(env, stbuf);
						}
					}
				}
			}
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_QTY, "");
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_ID, "");
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_UOM, "");
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_DESCRIPTION, "");
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_CLASSIFICATION, "");
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_PRODUCTLINE, "");
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_UNIT_COST,"");
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_AMOUNT, "");
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_QTY,RefurbQty);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_ID,KitItemId);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_UOM, KitUOM);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_DESCRIPTION, KitItemDesc);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_CLASSIFICATION, KitItemClass);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_PRODUCTLINE, KitProductLine);
			
			// BEGIN JB2 - April 18, 2013
			
			String unitCost="";
			if(isReplacedItem.equalsIgnoreCase("Y")) {
				String strSerialNo = MWOLine.getAttribute("PrimarySerialNo");
				String strItemID = WORootElem.getAttribute("ItemID");
				String strSecondrySerialNo = CommonUtilities.getSecondarySerial(env, strSerialNo, strItemID);
				unitCost = getIssuePrice(env, strSerialNo, strSecondrySerialNo, strItemID);
			} else {
				unitCost = getReceiptPrice(env, MasterWorkOrderNo, KitItemId, mwoType, MWOKey, MWOLineKey);
			}
			if(unitCost == null || unitCost.equals("")) {
				String strItemID = WORootElem.getAttribute("ItemID");
				String strProductClass = WORootElem.getAttribute("ProductClass");
				String strUOM = WORootElem.getAttribute("Uom");
				unitCost = getUnitPrice(env, strItemID, strProductClass, strUOM);
			}
			
			// END JB2 - April 18, 2013
			
			unitprice = Double.parseDouble(unitCost);
			double qtyComp = Double.parseDouble(RefurbQty);
			totrefurbamt = unitprice * qtyComp;
			String TRefurbAmtStr = Double.toString(totrefurbamt);
			BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_UNIT_COST, unitCost);
			if (DispositionCode.equals("UNSERVICE") || DispositionCode.equals(NWCGConstants.DISP_MISSING)) {
				// Unserviceable Refurb Items are not considered as either Credit or Debit Transactions
				BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_AMOUNT, "0.00");
			} 
			else{
				BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_AMOUNT, TRefurbAmtStr);
			}
			try {
				if (isAccountSplit) {
					insertBillingTransactionWithSplitAccountCodeData(env, docBillingTransactionIP, getReceiptListDoc, OwnerAgency);
				} else {
					CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_BILLING_TRANSACTION_SERVICE, docBillingTransactionIP);
				}
			} catch (Exception e) {
				e.printStackTrace();
				StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransConfirmWO' " + "DetailDescription='Billing Transaction Record Creation Failed on Confirm Refurb WO for Item ID : " + KitItemId + ", MWO number: "+ MasterWorkOrderNo +", Cache ID: "+ CacheId +", incident no: "+ IncidentNo +", Location ID: "+ LocationId +", Dispostition code: "+ DispositionCode +"'");
				throwAlert(env, stbuf);
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransRefurb' " + "DetailDescription='InsertBillingTransactionRecord Failed during NWCGProcessBillingTransRefurb, for MWO number: "+ tempMasterWorkOrderNo +", Cache ID: "+ tempCacheId +", Location ID: "+ tempLocationId +"'");
			throwAlert(env, stbuf);
		}
		return inXML;
	}
	
	/**
	 * 
	 * @param env
	 * @param ipBillingTransactionDoc
	 * @param receiptListDoc
	 * @param ownerAgency
	 * @throws Exception
	 */
	private void insertBillingTransactionWithSplitAccountCodeData(YFSEnvironment env, Document ipBillingTransactionDoc, Document receiptListDoc, String ownerAgency) throws Exception {		
		Element extnElement = (Element) receiptListDoc.getElementsByTagName("Extn").item(0);
		Element rootElement = (Element) ipBillingTransactionDoc.getDocumentElement();
		String strTransQty = rootElement.getAttribute("TransQty");
		String strUnitCost = rootElement.getAttribute("UnitCost");
		String dispositionCode = rootElement.getAttribute("DispositionCode");
		rootElement.setAttribute(NWCGConstants.BILL_TRANS_IS_ACCOUNT_SPLIT, "Y");
		// rootElement.setAttribute(NWCGConstants.BILL_TRANS_SPLIT_AMT_NUMBER, 2);
		double transQty = 0;
		double unitCost = 0;
		if (strTransQty != null && strTransQty.trim().length() > 0) {
			transQty = Double.parseDouble(strTransQty);
		}
		if (strUnitCost != null && strUnitCost.trim().length() > 0) {
			unitCost = Double.parseDouble(strUnitCost);
		}

		for (int i = 1; i <= 5; i++) {
			String extnReturnPercentage = extnElement.getAttribute("ExtnReturnPercentage" + i);
			if (extnReturnPercentage != null && Double.parseDouble(extnReturnPercentage) > 0) {
				String accountCode = extnElement.getAttribute("ExtnRefundBlmAcctCode" + i);
				double transAmount = (transQty * unitCost * Double.parseDouble(extnReturnPercentage)) / 100;
				transAmount = (dispositionCode.equals("UNSERVICE")) ? 0.00 : transAmount;
				rootElement.setAttribute("TransAmount", Double.toString(transAmount));
				if (ownerAgency.equals("FS")) {
					rootElement.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE, accountCode);
					rootElement.setAttribute(NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_ACCT_CODE, accountCode);
				} else if (ownerAgency.equals("BLM")) {
					rootElement.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE, accountCode);
					rootElement.setAttribute(NWCGConstants.BILL_TRANS_LAST_INCIDENT_BLM_ACCT_CODE, accountCode);
				} else {
					rootElement.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE, accountCode);
					rootElement.setAttribute(NWCGConstants.BILL_TRANS_LAST_INCIDENT_OTHER_ACCT_CODE, accountCode);
				}
				CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_BILLING_TRANSACTION_SERVICE, ipBillingTransactionDoc);
			}
		}
	}

	/**
	 * 
	 * @param env
	 * @param masterWorkOrderNo
	 * @return
	 * @throws Exception
	 */
	private Document callGetReceiptListAPI(YFSEnvironment env, String masterWorkOrderNo) throws Exception {
		String opTemplate = "<ReceiptList><Receipt ReceiptHeaderKey=\"\"><Extn/></Receipt></ReceiptList>";
		Document opTemplateDoc = XMLUtil.getDocument(opTemplate);
		Document ipDoc = XMLUtil.createDocument("Receipt");
		Element receiptRootElement = ipDoc.getDocumentElement();
		receiptRootElement.setAttribute("ReceiptNo", masterWorkOrderNo);
		Document opDoc = CommonUtilities.invokeAPI(env, opTemplateDoc, "getReceiptList", ipDoc);
		return opDoc;
	}

	/**
	 * 
	 * @param env
	 * @param receiptListDoc
	 * @return
	 * @throws Exception
	 */
	private boolean checkIfAccountSplit(YFSEnvironment env, Document receiptListDoc) throws Exception {
		int numAccounts = 0;
		boolean isAccountSplit = false;
		Element extnElement = (Element) receiptListDoc.getElementsByTagName("Extn").item(0);
		if (extnElement != null) {
			for (int i = 1; i <= 5; i++) {
				String currentReturnPercentage = extnElement.getAttribute("ExtnReturnPercentage" + i);
				numAccounts = (currentReturnPercentage != null && (Double.parseDouble(currentReturnPercentage) > 0)) ? (numAccounts + 1) : numAccounts;
			}
		}

		if (numAccounts > 1) {
			isAccountSplit = true;
		}
		return isAccountSplit;
	}

	/**
	 * 
	 * @param env
	 * @return
	 * @throws Exception
	 */
	private Document getBillingTransInputDocument(YFSEnvironment env) throws Exception {
		Document returnDoc = XMLUtil.createDocument("NWCGBillingTransaction");
		Element returnDocElem = returnDoc.getDocumentElement();
		returnDocElem.setAttribute("TransType", "WO-REFURB");
		return returnDoc;
	}

	/**
	 * 
	 */
	public Document insertBillingTransRecord(YFSEnvironment env, Document doc) throws Exception {
		return InsertRecord(env, doc);
	}

	/**
	 * 
	 * @param env
	 * @param Cache
	 * @return
	 * @throws Exception
	 */
	public Element getOrgAcctCode(YFSEnvironment env, String Cache) throws Exception {
		Document inDoc = XMLUtil.newDocument();
		Document outDoc = XMLUtil.newDocument();
		Element el_NWCGOrganization = inDoc.createElement("Organization");
		inDoc.appendChild(el_NWCGOrganization);
		el_NWCGOrganization.setAttribute("OrganizationCode", Cache);
		try {
			outDoc = CommonUtilities.invokeAPI(env, "NWCGProcessBillingTrans_getOrganizationList", "getOrganizationList", inDoc);
		} catch (Exception e) {
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransRefurb' " + "DetailDescription='GetOrganizationList Failed for Cache ID : " + Cache + ", during NWCGProcessBillingTransRefurb process'");
			throwAlert(env, stbuf);
		}
		Element outDocElem = outDoc.getDocumentElement();
		Element ExtnElm = (Element) outDocElem.getElementsByTagName("Extn").item(0);
		return ExtnElm;
	}

	/**
	 * 
	 * @param env
	 * @param ItemId
	 * @param UOM
	 * @param EnterpriseCode
	 * @return
	 * @throws Exception
	 */
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
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransRefurb' " + "DetailDescription='GetItemDetails Failed for Item ID : " + ItemId + ", during NWCGProcessBillingTransRefurb process'");
			throwAlert(env, stbuf);
		}
		Element GetItemDetailsOPElem = getItemDetailsOP.getDocumentElement();
		return GetItemDetailsOPElem;
	}

	/**
	 * 
	 * @param env
	 * @param MWOKey
	 * @return
	 * @throws Exception
	 */
	public Document getMWODetails(YFSEnvironment env, String MWOKey) throws Exception {
		Document getMWODetailsIP = XMLUtil.createDocument("NWCGMasterWorkOrder");
		Element GetMWODetailsIPElem = getMWODetailsIP.getDocumentElement();
		GetMWODetailsIPElem.setAttribute("MasterWorkOrderKey", MWOKey);
		Document getMWODetailsOP = null;
		try {
			getMWODetailsOP = CommonUtilities.invokeService(env, "NWCGGetMasterWorkOrderDetailsService", getMWODetailsIP);
		} catch (Exception e) {
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransRefurb' " + "DetailDescription='GetMWODetails Failed for Master Work Order Key : " + MWOKey + ", during NWCGProcessBillingTransRefurb process'");
			throwAlert(env, stbuf);
		}
		return getMWODetailsOP;
	}

	/**
	 * This method returns the MWO Line Item Price. If the price is stored on the MWO Line, then it is retrieved and set directly. 
	 * Otherwise it uses the old logic to retrieve the price from the input document. 
	 * 
	 * This Method Modified During JB2 development. 
	 * 
	 * @param env
	 * @param ReceiptNo
	 * @param RItemId
	 * @param mwoType
	 * @param MWOKey
	 * @param MWOLineKey
	 * @return
	 * @throws Exception
	 */
	public String getReceiptPrice(YFSEnvironment env, String ReceiptNo, String RItemId, String mwoType, String MWOKey, String MWOLineKey) throws Exception {
		// get the receipt line details on the MWO line
		Document OutXML_getMWOLine = getMasterWorkOrderLine(env, MWOKey, MWOLineKey);
		String strReceivingPrice = "";
		String strReceiptHeaderKey = "";
		String strReceiptLineKey = "";
		boolean isPriceSet = true;
		if (OutXML_getMWOLine != null) {
			NodeList listOutXML_getMWOLine = OutXML_getMWOLine.getElementsByTagName("NWCGMasterWorkOrderLine");
			Node nodeOutXML_getMWOLine = listOutXML_getMWOLine.item(0);
			Element elemOutXML_getMWOLine = (Element)nodeOutXML_getMWOLine;
		
			// SETTING VALUES!
			// This value comes from the NWCGGetMasterWorkOrderLineListService service call. 
			strReceiptLineKey = elemOutXML_getMWOLine.getAttribute("ReceiptLineKey");
			// This value comes from the NWCGGetMasterWorkOrderLineListService service call. 
			strReceiptHeaderKey = elemOutXML_getMWOLine.getAttribute("ReceiptHeaderKey");
			// This value comes from the NWCGGetMasterWorkOrderLineListService service call. 
			strReceivingPrice = elemOutXML_getMWOLine.getAttribute("ReceivingPrice");
		
			// if it is a new MWO generated with the Receipt Details, POST JB2, it will have this ReceivingPrice on the MWO Line
			// NOTE: the new Receiving Price value is defaulted to 0 for all records, therefore must validate based on Receipt Keys
			if(strReceiptLineKey != null && !strReceiptLineKey.trim().equals("") && strReceiptHeaderKey != null && !strReceiptHeaderKey.trim().equals("")) {
				strReceivingPrice = strReceivingPrice;
				return strReceivingPrice;
			} 
			isPriceSet = false;
		}
		// if the MWO details are null then do it the OLD way...
		if(OutXML_getMWOLine == null || isPriceSet == false) {
			Document inDoc = XMLUtil.newDocument();
			Document outDoc = XMLUtil.newDocument();
			String IssuePrice = "";			 
			Element el_Receipt=inDoc.createElement("Receipt");
			inDoc.appendChild(el_Receipt);
			el_Receipt.setAttribute("ReceiptNo",ReceiptNo);
			outDoc = CommonUtilities.invokeAPI(env,"getReceiptList",inDoc);			
			NodeList RetLines = outDoc.getDocumentElement().getElementsByTagName("ReceiptLine");
			int RetLineCnt = RetLines.getLength();
			if (RetLineCnt == 0) {
				strReceivingPrice = IssuePrice;
			} else {
				for(int cnt = 0; cnt < RetLineCnt; cnt++) {
					Element RetEl = (Element)RetLines.item(cnt);
					String ItemId = RetEl.getAttribute("ItemID");
					if (RItemId.equals(ItemId)) {
						Element ExtnElem = (Element) RetEl.getElementsByTagName("Extn").item(0);
						IssuePrice = ExtnElem.getAttribute("ExtnReceivingPrice");
						double issuePrice = Double.parseDouble(IssuePrice);
						strReceivingPrice = (mwoType != null && mwoType.equals("Refurb Transfer") && issuePrice == 0.00)? "" : IssuePrice; 
						break;
					} 
				}
			}
		}	
		return strReceivingPrice;
	}

	/**
	 * 
	 * @param env
	 * @param strSerialNo
	 * @param strSecondrySerialNo
	 * @param strItemID
	 * @return
	 * @throws Exception
	 */
	public String getIssuePrice(YFSEnvironment env, String strSerialNo, String strSecondrySerialNo, String strItemID) throws Exception {
		Document inDoc = XMLUtil.createDocument("NWCGTrackableItem");
		Element inDocRoot = inDoc.getDocumentElement();
		inDocRoot.setAttribute("ItemID", strItemID);
		inDocRoot.setAttribute("SerialNo", strSerialNo);
		inDocRoot.setAttribute("SecondarySerial", strSecondrySerialNo);		
		Document resultDoc = CommonUtilities.invokeService(env, "NWCGGetTrackableInventoryRecordService", inDoc);
		Element resultRoot = resultDoc.getDocumentElement();
		String issuePrice = resultRoot.getAttribute("LastIssuePrice");
		return issuePrice;
	}

	/**
	 * 
	 * @param env
	 * @param ItemId
	 * @param ProductClass
	 * @param UOM
	 * @return
	 * @throws Exception
	 */
	public String getUnitPrice(YFSEnvironment env, String ItemId, String ProductClass, String UOM) throws Exception {
		Document rt_ComputePriceForItem = XMLUtil.getDocument();
		Element el_ComputePriceForItem = rt_ComputePriceForItem.createElement("ComputePriceForItem");
		rt_ComputePriceForItem.appendChild(el_ComputePriceForItem);
		el_ComputePriceForItem.setAttribute("Currency", "USD");
		el_ComputePriceForItem.setAttribute("ItemID", ItemId);
		el_ComputePriceForItem.setAttribute("OrganizationCode", "NWCG");
		el_ComputePriceForItem.setAttribute("PriceProgramName","NWCG_PRICE_PROGRAM");
		el_ComputePriceForItem.setAttribute("ProductClass", ProductClass);
		el_ComputePriceForItem.setAttribute("Quantity", "1");
		el_ComputePriceForItem.setAttribute("Uom", UOM);
		
		// this code is not used...
		Document rt_ComputePriceForItemOut = XMLUtil.getDocument();
		Element el_ComputePriceForItemOut = rt_ComputePriceForItemOut.createElement("ComputePriceForItem");
		rt_ComputePriceForItemOut.appendChild(el_ComputePriceForItemOut);
		el_ComputePriceForItemOut.setAttribute("UnitPrice", "");
		Document PriceDocument = CommonUtilities.invokeAPI(env, "computePriceForItem", rt_ComputePriceForItem);
		Element root_elem = PriceDocument.getDocumentElement();
		String UnitPrice = "";
		if (root_elem != null) {
			UnitPrice = root_elem.getAttribute("UnitPrice");
		}
		return UnitPrice;
	}
	
	/**
	 * Call NWCGGetMasterWorkOrderLineListService Service with the MasterWorkOrderKey & MasterWorkOrderLineKey. 
	 * Example XML Here: <NWCGMasterWorkOrder MasterWorkOrderKey="20130416145019118756303" MasterWorkOrderLineKey="20130416145019118756307"/>
	 * Retrieve ReceiptHeaderKey & ReceiptLineKey from the output XML document. 
	 * 
	 * This Method Created During JB2 development. 
	 * 
	 * @param env
	 * @param MWOKey
	 * @param ItemID
	 * @param ItemQryType
	 * @return
	 * @throws Exception
	 */
	public Document getMasterWorkOrderLine(YFSEnvironment env, String strMasterWorkOrderKey, String strMasterWorkOrderLineKey) throws Exception {
		// create the NWCGMasterWorkOrder using MasterWorkOrderKey & MasterWorkOrderLineKey to retrieve the Receipt Line details that were previously set on the MWO Line. 
		Document getMWOLineIP = XMLUtil.createDocument("NWCGMasterWorkOrder");
		Element GetMWOLineIPElem = getMWOLineIP.getDocumentElement();
		GetMWOLineIPElem.setAttribute("MasterWorkOrderKey", strMasterWorkOrderKey);
		GetMWOLineIPElem.setAttribute("MasterWorkOrderLineKey", strMasterWorkOrderLineKey);	
		Document getMWOLineOP = null;
		try {
			if (getMWOLineIP != null) {
				// Invoke the NWCGGetMasterWorkOrderLineListService that will retrieve the Receipt Line details that were previously set on the MWO Line. 
				getMWOLineOP = CommonUtilities.invokeService(env, "NWCGGetMasterWorkOrderLineListService", getMWOLineIP);
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGGetMasterWorkOrder' " + "DetailDescription='GetMWOLineList Failed for Master Work Order Key : " + strMasterWorkOrderKey + ", during NWCGProcessBillingTransRefurb process'");
			throwAlert(env, stbuf);
		}
		return getMWOLineOP;
	}
}