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
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;


/**
 * This API Creates TWO Billing Transaction Records in NWCG_BILLING_TRANSACTION Table for each Item in an Incident Transfer Order 
 * A Credit Transaction is created for the From Incident Cache and a Debit Transaction is created for the To Incident Cache
 * 
 * @author Oxford
 */
public class NWCGProcessBillingTransConfirmIncidentTO implements YIFCustomApi, NWCGBillingTransRecordMutator {
	
	private static Logger logger = Logger.getLogger(NWCGProcessBillingTransConfirmIncidentTO.class.getName());

	/**
	 * 
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
		Message.append(" ExceptionType='BILLING_TRANS_CONFIRM_INCIDENT_TO'" + " InboxType='" + NWCGConstants.NWCG_BILL_TRANS_INBOXTYPE + "' QueueId='" + NWCGConstants.NWCG_BILL_TRANS_QUEUEID + "' />");
		if (logger.isVerboseEnabled())
			logger.verbose("Throw Alert Method called with message:-" + Message.toString());
		try {
			Document inTemplate = XMLUtil.getDocument(Message.toString());
			CommonUtilities.invokeAPI(env, "createException", inTemplate);
		} catch (Exception ex1) {
			throw new NWCGException("NWCG_BILLING_TRANSACTION_ERROR_WHILE_LOGGING_ALERT");
		}
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
	 * @param inXML
	 * @return
	 * @throws Exception
	 */
	public Document InsertRecord(YFSEnvironment env, Document inXML) throws Exception {
		try {
			Element curOrderList, curOrderLineList = null;
			inXML.getDocumentElement().normalize();
			Element elemOrderRoot = inXML.getDocumentElement();
			if (elemOrderRoot != null) {
				NodeList listOfOrders = inXML.getElementsByTagName("Order");
				for (int i = 0; i < listOfOrders.getLength(); i++) {
					Node OrderNode = listOfOrders.item(i);
					if (OrderNode.getNodeType() == Node.ELEMENT_NODE) {
						curOrderList = (Element) listOfOrders.item(i);
						String OrderNo = curOrderList.getAttribute("OrderNo");
						String EnterpriseCode = curOrderList.getAttribute("EnterpriseCode");
						String OrderDate = curOrderList.getAttribute("OrderDate");
						String OrderHeaderKey = curOrderList.getAttribute("OrderHeaderKey");
						String DocumentType = curOrderList.getAttribute("DocumentType");
						String createUserId = curOrderList.getAttribute("Createuserid");
						String modifyUserId = curOrderList.getAttribute("Modifyuserid");
						Element ExtnIncidentElm = (Element) curOrderList.getElementsByTagName("Extn").item(0);
						String IncidentNo = ExtnIncidentElm.getAttribute("ExtnIncidentNo");
						String IncidentYear = ExtnIncidentElm.getAttribute("ExtnIncidentYear");
						String IncidentName = ExtnIncidentElm.getAttribute("ExtnIncidentName");
						String ExtnFsAcctCode = ExtnIncidentElm.getAttribute("ExtnFsAcctCode");
						String ExtnOverrideCode = ExtnIncidentElm.getAttribute("ExtnOverrideCode");
						String ExtnBlmAcctCode = ExtnIncidentElm.getAttribute("ExtnBlmAcctCode");
						String ExtnOtherAcctCode = ExtnIncidentElm.getAttribute("ExtnOtherAcctCode");
						String CacheId = curOrderList.getAttribute("ShipNode");
						// added just in case cache ID is blank shipnode
						if (CacheId.equals("")) { 
							CacheId = ExtnIncidentElm.getAttribute("ExtnIncidentCacheId");
						}
						NodeList listOfOrderLines = curOrderList.getElementsByTagName("OrderLine");
						for (int j = 0; j < listOfOrderLines.getLength(); j++) {
							Node OrderLineNode = listOfOrderLines.item(j);
							if (OrderLineNode.getNodeType() == Node.ELEMENT_NODE) {
								curOrderLineList = (Element) listOfOrderLines.item(j);
								String OrderLineKey = curOrderLineList.getAttribute("OrderLineKey");
								String ActualQty = curOrderLineList.getAttribute("OrderedQty");
								Element ItemElem = (Element) curOrderLineList.getElementsByTagName("Item").item(0);
								Element ItemPriceInfo = (Element) curOrderLineList.getElementsByTagName("LinePriceInfo").item(0);
								String ItemId = ItemElem.getAttribute("ItemID");
								String ItemDesc = ItemElem.getAttribute("ItemDesc");
								String UOM = ItemElem.getAttribute("UnitOfMeasure");
								String ItemClass = ItemElem.getAttribute("TaxProductCode");
								String ProductLine = ItemElem.getAttribute("ProductLine");
								String UnitCost = ItemPriceInfo.getAttribute("UnitPrice");
								double ucost = Double.parseDouble(UnitCost);
								if (!(ucost > 0)) {
									//jimmy added cache id to alert
									StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransConfirmIncidentTO' " + "DetailDescription='Unit Price is 0 for Order No : " + OrderNo + ",Item Id : " + ItemId + ", Cache ID: "+ CacheId +"'");
									throwAlert(env, stbuf);
								}
								double qty = Double.parseDouble(ActualQty);
								double Tcost = qty * ucost;
								String TcostStr = Double.toString(Tcost);
								String QtyStr = Double.toString(qty);

								// CR-631 <Gaurav> STARTS
								Element OrderLineExtn = (Element) curOrderLineList.getElementsByTagName("Extn").item(0);
								String strTrackableID = OrderLineExtn.getAttribute("ExtnTrackableId");
								String strLastIssueCost = "";
								String lastissueprice = "";
								String strSecondrySerial = "";
								if (strTrackableID != null && !strTrackableID.equals("")) {
									Document docTrackbleItem = XMLUtil.createDocument("NWCGTrackableItem");
									Element eleRoot = docTrackbleItem.getDocumentElement();
									eleRoot.setAttribute("SerialNo", strTrackableID);
									Document trackoutDoc = CommonUtilities.invokeService(env, NWCGConstants.NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE, docTrackbleItem);
									Element trackoutelem = (Element) trackoutDoc.getDocumentElement().getElementsByTagName("NWCGTrackableItem").item(0);
									if (trackoutelem != null) {
										lastissueprice = trackoutelem.getAttribute("LastIssuePrice");
										strSecondrySerial = trackoutelem.getAttribute("SecondarySerial");
										if (lastissueprice != null && !lastissueprice.equals("")) {
											double dlLastIssuePrice = Double.parseDouble(lastissueprice);
											double dlLstIssueCost = dlLastIssuePrice * qty;
											strLastIssueCost = String.valueOf(dlLstIssueCost);
										}
									}

								}
								// CR-631 <Gaurav> ENDS
								
								// Credit From Incident
								Document docBillingTransactionIP = getBillingTransInputDocument(env);
								Element BillingTransElem = docBillingTransactionIP.getDocumentElement();
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_NO, OrderNo);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ENTERPRISE_CODE, EnterpriseCode);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_DATE, OrderDate);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_HEADER_KEY, OrderHeaderKey);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_DOCUMENT_TYPE, DocumentType);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_CACHE_ID, CacheId);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LINE_KEY, OrderLineKey);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_QTY, ActualQty);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_ID, ItemId);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_DESCRIPTION, ItemDesc);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_UOM, UOM);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_CLASSIFICATION, ItemClass);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_ITEM_PRODUCTLINE, ProductLine);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_CREATEUSERID, createUserId);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_MODIFYUSERID, modifyUserId);

								// CR-631 <Gaurav> STARTS
								if (lastissueprice != null && !lastissueprice.equals("")) {
									BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_UNIT_COST, lastissueprice);
									BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_AMOUNT, strLastIssueCost);
								} else {
									BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_UNIT_COST, UnitCost);
									BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_AMOUNT, TcostStr);
								}
								// CR-631 <Gaurav> ENDS
								
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_NO, IncidentNo);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_YEAR, IncidentYear);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_NAME, IncidentName);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE, ExtnFsAcctCode);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE, ExtnOverrideCode);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE, ExtnBlmAcctCode);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE, ExtnOtherAcctCode);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_ACCT_CODE, ExtnFsAcctCode);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_OVERRIDE_CODE, ExtnOverrideCode);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LAST_INCIDENT_BLM_ACCT_CODE, ExtnBlmAcctCode);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LAST_INCIDENT_OTHER_ACCT_CODE, ExtnOtherAcctCode);

								try {
									CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_BILLING_TRANSACTION_SERVICE, docBillingTransactionIP);
								} catch (Exception e) {
									e.printStackTrace();
									StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransConfirmIncidentTO' " + "DetailDescription='Credit Billing Transaction Record Creation Failed on Confirm Incident TO for Order No : " + OrderNo + ",Item Id : " + ItemId + ", Actual QTY: "+ ActualQty +", Cache ID: "+ CacheId +", incident no: "+ IncidentNo +" createUser: "+ createUserId +",TO trans amount: "+ TcostStr +"'");;
									throwAlert(env, stbuf);
								}

								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_CACHE_ID, "");
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_AMOUNT, "");
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_NO, "");
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_YEAR, "");
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_NAME, "");
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE, "");
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE, "");
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE, "");
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_ACCT_CODE, "");
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LAST_INCIDENT_BLM_ACCT_CODE, "");
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LAST_INCIDENT_OTHER_ACCT_CODE, "");

								// Debit To Incident
								String ToIncidentNo = ExtnIncidentElm.getAttribute("ExtnToIncidentNo");
								String ToIncidentYear = ExtnIncidentElm.getAttribute("ExtnToIncidentYear");
								String ToIncidentName = ExtnIncidentElm.getAttribute("ExtnToIncidentName");
								String ExtnToFsAcctCode = ExtnIncidentElm.getAttribute("ExtnToFsAcctCode");
								String ExtnToOverrideCode = ExtnIncidentElm.getAttribute("ExtnToOverrideCode");
								String ExtnToBlmAcctCode = ExtnIncidentElm.getAttribute("ExtnToBlmAcctCode");
								String ExtnToOtherAcctCode = ExtnIncidentElm.getAttribute("ExtnToOtherAcctCode");

								qty = qty * -1;
								Tcost = qty * ucost;
								TcostStr = Double.toString(Tcost);
								QtyStr = Double.toString(qty);

								// CR 438 - update "CONFIRM INCIDENT FROM" for
								// Debitting
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_TYPE, "CONFIRM INCIDENT TO");
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_CACHE_ID, CacheId);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_QTY, QtyStr);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_AMOUNT, TcostStr);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_NO, ToIncidentNo);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_YEAR, ToIncidentYear);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_NAME, ToIncidentName);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE, ExtnToFsAcctCode);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE, ExtnToOverrideCode);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE, ExtnToBlmAcctCode);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE, ExtnToOtherAcctCode);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_ACCT_CODE, ExtnToFsAcctCode);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_OVERRIDE_CODE, ExtnToOverrideCode);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LAST_INCIDENT_BLM_ACCT_CODE, ExtnToBlmAcctCode);
								BillingTransElem.setAttribute(NWCGConstants.BILL_TRANS_LAST_INCIDENT_OTHER_ACCT_CODE, ExtnToOtherAcctCode);

								try {
									//jimmy added more detail to alerts
									CommonUtilities.invokeService(env, NWCGConstants.NWCG_CREATE_BILLING_TRANSACTION_SERVICE, docBillingTransactionIP);
								} catch (Exception e) {
									e.printStackTrace();
									StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransConfirmIncidentTO' " + "DetailDescription='Debit Billing Transaction Record Creation Failed on Confirm Incident TO for Order No : " + OrderNo + ",Item Id : " + ItemId + ", Actual QTY: "+ ActualQty +", Cache ID: "+ CacheId +", incident no: "+ ToIncidentNo +" createUser: "+ createUserId +", TO trans amount: "+ TcostStr +"'");
									throwAlert(env, stbuf);
								}
							}
						}
					}
				}
			}

			try {
				CommonUtilities.invokeService(env, "NWCGProcessConfirmDraftOrderService", inXML);
			} catch (Exception throwprocessconfirmDOexception) {
				throw throwprocessconfirmDOexception;
			}
		} catch (Exception throwablebillingexception) {
			throwablebillingexception.printStackTrace();
			throw throwablebillingexception;
		}
		return inXML;
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
		returnDocElem.setAttribute(NWCGConstants.BILL_TRANS_TYPE, "CONFIRM INCIDENT FROM");
		return returnDoc;
	}
}