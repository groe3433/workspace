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
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/*
 * This API Updates all the Billing Transaction Records in NWCG_BILLING_TRANSACTION Table 
 * for Incident/Other Issue Account Code Changes ONLY !!! Other Changes/modifications will not be reflected 
 */
public class NWCGProcessBillingTransChangeOrder implements YIFCustomApi,
		NWCGBillingTransRecordMutator {
	
	private static Logger logger = Logger
			.getLogger(NWCGProcessBillingTransChangeOrder.class.getName());

	public void setProperties(Properties arg0) throws Exception {
	}

	public void throwAlert(YFSEnvironment env, StringBuffer Message)
			throws Exception {
		Message.append(" ExceptionType='BILLING_TRANS_CHANGE_ORDER'"
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
					.verbose("Entering NWCGProcessBillingTransChangeOrder, Input document is:"
							+ XMLUtil.getXMLString(inXML));
		}
		// Jimmy setup temp vars for alerts
		String tempExtnBlmAcctCode = "";
		String tempExtnFsAcctCode = "";
		String tempOrderHeaderKey = "";
		String tempcreateUserId = "";
		try {
			Element curBillingList = null;
			inXML.getDocumentElement().normalize();
			Element elemOrderRoot = inXML.getDocumentElement();
			if (elemOrderRoot != null) {
				String OrderHeaderKey = elemOrderRoot
						.getAttribute("OrderHeaderKey");
				String createUserId = elemOrderRoot
						.getAttribute("Createuserid");
				String modifyUserId = elemOrderRoot
						.getAttribute("Modifyuserid");
				NodeList nlExtn = elemOrderRoot.getElementsByTagName("Extn");
				Element elemExtn = (Element) nlExtn.item(0);
				String ExtnFsAcctCode = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnFsAcctCode"));
				String ExtnOverrideCode = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnOverrideCode"));
				String ExtnBlmAcctCode = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnBlmAcctCode"));
				String ExtnOtherAcctCode = StringUtil.nonNull(elemExtn
						.getAttribute("ExtnOtherAcctCode"));
				// jimmy populating temp vars
				tempExtnBlmAcctCode = ExtnBlmAcctCode;
				tempExtnFsAcctCode = ExtnFsAcctCode;
				tempOrderHeaderKey = OrderHeaderKey;
				tempcreateUserId = createUserId;
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
				Document BillTransList = queryBillingTransaction(env,
						OrderHeaderKey);
				NodeList BList = BillTransList.getDocumentElement()
						.getElementsByTagName("NWCGBillingTransaction");
				if (BList.getLength() == 0) {
					// Order is not Shipped and not yet recorded in
					// NWCG_BILLING_TRANSACTION. Changes are not updated!!!
					if (logger.isVerboseEnabled()) {
						logger
								.verbose("Order is not Shipped Yet. No Updates to NWCG_BILLING_TRANSACTION. Input Document is:"
										+ XMLUtil.getXMLString(inXML));
					}
					return inXML;
				}
				/*
				 * String AcctChanged = checkAcctCodeChange(env,elemOrderRoot);
				 * 
				 * if (!AcctChanged.equals("Y")) { // There is no change to
				 * Account Code at the Order Level. Other changes are not
				 * updated. if(logger.isVerboseEnabled()){
				 * logger.verbose("Account Code information is not changed. No
				 * Updates to NWCG_BILLING_TRANSACTION. Input Document
				 * is:"+XMLUtil.getXMLString(inXML)); } return inXML; }
				 */
				String OwnerAgency = "";
				for (int j = 0; j < BList.getLength(); j++) {
					curBillingList = (Element) BList.item(j);
					String SequenceKey = curBillingList
							.getAttribute(NWCGConstants.BILL_TRANS_SEQUENCE_KEY);
					String CacheId = curBillingList
							.getAttribute(NWCGConstants.BILL_TRANS_CACHE_ID);
					String SplitNumberStr = curBillingList
							.getAttribute(NWCGConstants.BILL_TRANS_SPLIT_AMT_NUMBER);
					String IncidentBlmAcctCode = curBillingList
							.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE);
					String IncidentFsAcctCode = curBillingList
							.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE);
					String IncidentOverrideAcctCode = curBillingList
							.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE);
					String IncidentOtherAcctCode = curBillingList
							.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE);
					double SplitNbr = Double.parseDouble(SplitNumberStr);
					if (OwnerAgency.length() == 0) {
						Element AcctElem = getOrgAcctCode(env, CacheId);
						OwnerAgency = AcctElem.getAttribute("ExtnOwnerAgency");
					}
					Document docBillingTransactionIP = getBillingTransInputDocument(env);
					Element BillingTransElem = docBillingTransactionIP
							.getDocumentElement();
					BillingTransElem.setAttribute(
							NWCGConstants.BILL_TRANS_SEQUENCE_KEY, SequenceKey);
					BillingTransElem
							.setAttribute(
									NWCGConstants.BILL_TRANS_LAST_INCIDENT_BLM_ACCT_CODE,
									IncidentBlmAcctCode);
					BillingTransElem
							.setAttribute(
									NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_ACCT_CODE,
									IncidentFsAcctCode);
					BillingTransElem
							.setAttribute(
									NWCGConstants.BILL_TRANS_LAST_INCIDENT_FS_OVERRIDE_CODE,
									IncidentOverrideAcctCode);
					BillingTransElem
							.setAttribute(
									NWCGConstants.BILL_TRANS_LAST_INCIDENT_OTHER_ACCT_CODE,
									IncidentOtherAcctCode);
					BillingTransElem
							.setAttribute(
									NWCGConstants.BILL_TRANS_CREATEUSERID,
									createUserId);
					BillingTransElem
							.setAttribute(
									NWCGConstants.BILL_TRANS_MODIFYUSERID,
									modifyUserId);
					if (SplitNbr == 0) {
						if (OwnerAgency.equals("BLM")) {
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE,
											ExtnBlmAcctCode);
						} else if (OwnerAgency.equals("FS")) {
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE,
											ExtnFsAcctCode);
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE,
											ExtnOverrideCode);
						} else {
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE,
											ExtnOtherAcctCode);
						}
					}
					if (SplitNbr == 1) {
						if (OwnerAgency.equals("BLM")) {
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE,
											ExtnAcctCode1);
						} else if (OwnerAgency.equals("FS")) {
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE,
											ExtnAcctCode1);
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE,
											ExtnOverrideCode1);
						} else {
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE,
											ExtnAcctCode1);
						}
					}
					if (SplitNbr == 2) {
						if (OwnerAgency.equals("BLM")) {
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE,
											ExtnAcctCode2);
						} else if (OwnerAgency.equals("FS")) {
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE,
											ExtnAcctCode2);
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE,
											ExtnOverrideCode2);
						} else {
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE,
											ExtnAcctCode2);
						}
					}
					if (SplitNbr == 3) {
						if (OwnerAgency.equals("BLM")) {
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE,
											ExtnAcctCode3);
						} else if (OwnerAgency.equals("FS")) {
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE,
											ExtnAcctCode3);
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE,
											ExtnOverrideCode3);
						} else {
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE,
											ExtnAcctCode3);
						}
					}
					if (SplitNbr == 4) {
						if (OwnerAgency.equals("BLM")) {
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE,
											ExtnAcctCode4);
						} else if (OwnerAgency.equals("FS")) {
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE,
											ExtnAcctCode4);
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE,
											ExtnOverrideCode4);
						} else {
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE,
											ExtnAcctCode4);
						}
					}
					if (SplitNbr == 5) {
						if (OwnerAgency.equals("BLM")) {
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE,
											ExtnAcctCode5);
						} else if (OwnerAgency.equals("FS")) {
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE,
											ExtnAcctCode5);
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_FS_OVERRIDE_CODE,
											ExtnOverrideCode5);
						} else {
							BillingTransElem
									.setAttribute(
											NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE,
											ExtnAcctCode5);
						}
					}
					try {
						CommonUtilities
								.invokeService(
										env,
										NWCGConstants.NWCG_UPDATE_BILLING_TRANSACTION_SERVICE,
										docBillingTransactionIP);
					}
					catch (Exception e) {
						// jimmy added all detail to alert
						if (logger.isVerboseEnabled())
							logger
									.verbose("NWCGProcessBillingTransChangeOrder::updateBillingTransactionRecord Caught Exception "
											+ e);
						e.printStackTrace();
						StringBuffer stbuf = new StringBuffer(
								"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransChangeOrder' "
										+ "DetailDescription='UpdateBillingTransaction Failed during NWCGProcessBillingTransChangeOrder for SequenceKey : "
										+ SequenceKey
										+ ", Cache ID: "
										+ CacheId
										+ " and OrderHeader Key: "
										+ tempOrderHeaderKey
										+ ", where FS Act Code: "
										+ tempExtnFsAcctCode
										+ ", BLM Acct Code: "
										+ tempExtnBlmAcctCode
										+ ".  Where LAST INCIDENT BLM ACCT CODE : "
										+ IncidentBlmAcctCode
										+ " and LAST INCIDENT FS OVERRIDE CODE: "
										+ IncidentOverrideAcctCode
										+ " and LAST INCIDENT FS OVERRIDE CODE: "
										+ IncidentOverrideAcctCode
										+ " and LAST INCIDENT OTHER ACCT CODE: "
										+ IncidentOtherAcctCode + "'");
						throwAlert(env, stbuf);
					}
					if (logger.isVerboseEnabled()) {
						logger
								.verbose("Exiting NWCGProcessBillingTransChangeOrder,Output Document is:"
										+ XMLUtil
												.getXMLString(docBillingTransactionIP));
					}
				}
			}
		} catch (Exception e) {
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingTransChangeOrder::updateBillingTransactionRecord Caught Exception "
								+ e);
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransChangeOrder' "
							+ "DetailDescription='UpdateBillingTransactionRecord Failed, during NWCGProcessBillingTransChangeOrder for OrderHeader Key: "
							+ tempOrderHeaderKey + ", where FS Act Code: "
							+ tempExtnFsAcctCode + ", BLM Acct Code: "
							+ tempExtnBlmAcctCode + "'");
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

	public Document queryBillingTransaction(YFSEnvironment env,
			String TransactionHeaderKey) throws Exception {
		Document inDoc = XMLUtil.newDocument();
		Document outDoc = XMLUtil.newDocument();
		Element el_NWCGBillingTransaction = inDoc
				.createElement("NWCGBillingTransaction");
		inDoc.appendChild(el_NWCGBillingTransaction);
		el_NWCGBillingTransaction.setAttribute(
				NWCGConstants.BILL_TRANS_HEADER_KEY, TransactionHeaderKey);
		try {
			outDoc = CommonUtilities.invokeService(env,
					NWCGConstants.NWCG_GET_BILLING_TRANSACTION_LIST_SERVICE,
					inDoc);
		} catch (Exception e) {
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransChangeOrder' "
							+ "DetailDescription='GetBillingTransactionList Failed during NWCGProcessBillingTransChangeOrder process, for TransactionHeaderKey : "
							+ TransactionHeaderKey + "'");
			throwAlert(env, stbuf);
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingTransChangeOrder::getBillingTransactionList Caught Exception "
								+ e);
		}
		return outDoc;
	}

	public String checkAcctCodeChange(YFSEnvironment env, Element OrderList)
			throws Exception {
		String AcctCodeChanged = "";
		NodeList listOfAuditLines = OrderList
				.getElementsByTagName("OrderAuditLevels");
		for (int i = 0; i < listOfAuditLines.getLength(); i++) {
			Element curAuditLine = (Element) listOfAuditLines.item(i);
			NodeList listOfAuditDetails = curAuditLine
					.getElementsByTagName("OrderAuditDetails");
			for (int j = 0; j < listOfAuditDetails.getLength(); j++) {
				Element curAuditDetail = (Element) listOfAuditDetails.item(j);
				NodeList listOfAttributes = curAuditDetail
						.getElementsByTagName("Attributes");
				for (int k = 0; k < listOfAttributes.getLength(); k++) {
					Element curAttribute = (Element) listOfAttributes.item(k);
					Element AttribElem = (Element) curAttribute
							.getElementsByTagName("Attribute").item(0);
					String Name = AttribElem.getAttribute("Name");
					int a = Name.indexOf("AcctCode");
					int b = Name.indexOf("OverrideCode");
					if (a > 1 || b > 1) {
						AcctCodeChanged = "Y";
						return AcctCodeChanged;
					}
				}
			}
		}
		return AcctCodeChanged;
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
					"<Inbox ActiveFlag='' ApiName='NWCGProcessBillingTransChangeOrder' "
							+ "DetailDescription='GetOrganizationList Failed during NWCGProcessBillingTransChangeOrder process, for Cache ID : "
							+ Cache + "'");
			throwAlert(env, stbuf);
			if (logger.isVerboseEnabled())
				logger
						.verbose("NWCGProcessBillingTransChangeOrder::getOrganizationList Caught Exception "
								+ e);
		}
		Element outDocElem = outDoc.getDocumentElement();
		Element ExtnElm = (Element) outDocElem.getElementsByTagName("Extn")
				.item(0);
		return ExtnElm;
	}
}