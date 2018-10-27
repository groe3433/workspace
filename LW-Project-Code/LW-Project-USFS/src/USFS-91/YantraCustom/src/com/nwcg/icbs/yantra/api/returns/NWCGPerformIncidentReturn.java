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

package com.nwcg.icbs.yantra.api.returns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.api.refurb.NWCGRefurbHelper;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfs.japi.YFSException;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

/**
 * PerformIncidentReturn
 * 
 * @author Oxford Consulting Group
 * @version 1.0
 * @date March 21, 2013
 */
public class NWCGPerformIncidentReturn implements YIFCustomApi {
	
	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGPerformIncidentReturn.class);
	
	int shipmentLineCount = 0;

	int ShipmentTagSerialCount = 0;

	List serialList = new ArrayList();

	Document rt_Return = null;

	private Properties props = null;

	String RRPFlag = "NO";

	// BEGIN CR 675 - added eleOrderLineList
	Element eleOrderLineList = null;

	NodeList RetLines = null;

	int RetLineCount = 0;

	public void setProperties(Properties props) throws Exception {
		this.props = props;
	}

	/**
	 * 
	 * @param env
	 * @param Message
	 * @throws Exception
	 */
	public void throwAlert(YFSEnvironment env, StringBuffer Message) throws Exception {
		Message.append(" ExceptionType='" + NWCGConstants.NWCG_RETURN_ALERTTYPE + "'" + " InboxType='" + NWCGConstants.NWCG_RETURN_INBOXTYPE + "' QueueId='" + NWCGConstants.NWCG_RETURN_QUEUEID + "' />");
		try {
			Document inTemplate = XMLUtil.getDocument(Message.toString());
			CommonUtilities.invokeAPI(env, "createException", inTemplate);
		} catch (Exception ex1) {
			throw new NWCGException("NWCG_RETURN_ERROR_WHILE_LOGGING_ALERT");
		}
	}

	/**
	 * This entry method is used to perform actual return.
	 * ie. create shipment->confirm shipment->start Receipt->Add lines to the receipt -> close receipt
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 * @throws Exception
	 */
	public Document performReturn(YFSEnvironment env, Document inXML) throws Exception {
		inXML = processinXML(env, inXML);
		Element rootElem = inXML.getDocumentElement();
		String IncidentNo = "";
		String IncidentYear = "";
		String IssueNo = "";
		String ReceiptNo = "";
		String UserId = "";
		String CustomerId = "";
		String Cache = "";
		String ReceivingLoc = "";
		String ReturnHeaderNotes = "";
		String Mesg = "";
		String EnterpriseCode = "NWCG"; 
		Map<String, String> splitAcctCodeData = new HashMap<String, String>();
		Element splitAccountCodeDataElement = (Element) rootElem.getElementsByTagName("SplitAccountCodeData").item(0);
		if (splitAccountCodeDataElement != null) {
			boolean isSaveSplitAcct = ("Y".equalsIgnoreCase(splitAccountCodeDataElement.getAttribute("SaveAccounts"))) ? true : false;
			if (isSaveSplitAcct) {
				for (int i = 0; i <= 5; i++) {
					splitAcctCodeData.put("ExtnRefundBlmAcctCode" + i, splitAccountCodeDataElement.getAttribute("RefundAcctCode" + i));
					splitAcctCodeData.put("ExtnRefundChargedAmount" + i, splitAccountCodeDataElement.getAttribute("RefundChargedAmount" + i));
					splitAcctCodeData.put("ExtnReturnPercentage" + i, splitAccountCodeDataElement.getAttribute("ReturnPercentage" + i));
				}
			}
		}
		RetLines = inXML.getDocumentElement().getElementsByTagName("ReceiptLine");
		RetLineCount = RetLines.getLength();
		if (RetLineCount == 0) {
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGPerformIncidentReturn' " + "DetailDescription='No Return Lines'");
			throwAlert(env, stbuf);
			rootElem.setAttribute("ErrorMessage", "Exception : No Return Lines to Process");
			rootElem.setAttribute("NoException", "false");
			return inXML;
		}
		if (rootElem.getAttribute("IncidentNo") != null && !(rootElem.getAttribute("IncidentNo").equals(""))) {
			IncidentNo = rootElem.getAttribute("IncidentNo");
		} else {
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGPerformIncidentReturn' " + "DetailDescription='Error Generated while Performing Return due to missing value for IncidentNo. '");
			throwAlert(env, stbuf);
		}
		if (rootElem.getAttribute("IncidentYear") != null && !(rootElem.getAttribute("IncidentYear").equals(""))) {
			IncidentYear = rootElem.getAttribute("IncidentYear");
		} else {
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGPerformIncidentReturn' " + "DetailDescription='Error Generated while Performing Return due to missing value for IncidentYear. '");
			throwAlert(env, stbuf);
		}
		if (rootElem.getAttribute("CacheID") != null && !(rootElem.getAttribute("CacheID").equals(""))) {
			Cache = rootElem.getAttribute("CacheID");
		} else {
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGPerformIncidentReturn' " + "DetailDescription='Error Generated while Performing Return due to missing value for CacheID.'");
			throwAlert(env, stbuf);
		}
		if (rootElem.getAttribute("ReceivingDock") != null && !(rootElem.getAttribute("ReceivingDock").equals(""))) {
			ReceivingLoc = rootElem.getAttribute("ReceivingDock");
		} else {
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGPerformIncidentReturn' " + "DetailDescription='Error Generated while Performing Return due to missing value for CacheID.'");
			throwAlert(env, stbuf);
		}
		RRPFlag = NWCGRefurbHelper.get_RRP_Flag(env, Cache);
		CustomerId = rootElem.getAttribute("CustomerId");
		ReceiptNo = rootElem.getAttribute("ReceiptNo");
		UserId = rootElem.getAttribute("UserId");
		IssueNo = rootElem.getAttribute("IssueNo");
		ReturnHeaderNotes = rootElem.getAttribute("ReturnHeaderNotes");
		Mesg = validateReceivingLocation(env, ReceivingLoc, Cache);
		if (Mesg.length() > 0) {
			rootElem.setAttribute("ErrorMessage", "Exception : " + Mesg);
			rootElem.setAttribute("NoException", "false");
			return inXML;
		}
		if (RRPFlag.equals("YES")) {
			Mesg = validateStagingLocation(env, "REFURB-RFI", Cache);
		} else {
			Mesg = validateStagingLocation(env, "RFI-1", Cache);
		}
		if (Mesg.length() > 0) {
			rootElem.setAttribute("ErrorMessage", "Exception : " + Mesg);
			rootElem.setAttribute("NoException", "false");
			return inXML;
		}
		Mesg = validateStagingLocation(env, "NRFI-1", Cache);
		if (Mesg.length() > 0) {
			rootElem.setAttribute("ErrorMessage", "Exception : " + Mesg);
			rootElem.setAttribute("NoException", "false");
			return inXML;
		}
		Mesg = validateStagingLocation(env, "UNS-1", Cache);		
		if (Mesg.length() > 0) {
			rootElem.setAttribute("ErrorMessage", "Exception : " + Mesg);
			rootElem.setAttribute("NoException", "false");
			return inXML;
		}
		Mesg = validateStagingLocation(env, "UNSNWT-1", Cache);
		if (Mesg.length() > 0) {
			rootElem.setAttribute("ErrorMessage", "Exception : " + Mesg);
			rootElem.setAttribute("NoException", "false");
			return inXML;
		}
		if (IssueNo.length() > 0) {
			Mesg = validateIssueNo(env, IssueNo);
		} else {
			Mesg = validateIncidentNo(env, IncidentNo, IncidentYear);
		}
		if (Mesg.length() > 0) {
			rootElem.setAttribute("ErrorMessage", "Exception : " + Mesg);
			rootElem.setAttribute("NoException", "false");
			return inXML;
		}
		Mesg = ValidateReturnLines(env, inXML);
		if (Mesg.length() > 0) {
			rootElem.setAttribute("ErrorMessage", "Exception : " + Mesg);
			rootElem.setAttribute("NoException", "false");
			return inXML;
		}
		// Create xml for modifying records in the INCIDENT_RETURN TABLE
		rt_Return = XMLUtil.newDocument();
		Element el_Return = rt_Return.createElement("Return");
		rt_Return.appendChild(el_Return);
		el_Return.setAttribute("CacheID", Cache);
		el_Return.setAttribute("EnterpriseCode", "NWCG");
		el_Return.setAttribute("IncidentNo", IncidentNo);
		el_Return.setAttribute("IncidentYear", IncidentYear);
		el_Return.setAttribute("IssueNo", IssueNo);
		el_Return.setAttribute("LocationId", ReceivingLoc);
		Element el_ReturnLines = rt_Return.createElement("ReturnLines");
		el_Return.appendChild(el_ReturnLines);
		try {
			Document outDoc1 = callCreateShipment(env, inXML, CustomerId, ReceiptNo);
			Document outDoc2 = null;
			Document outDoc3 = null;
			Document outDoc4 = null;
			if (outDoc1 != null) {
				outDoc2 = callConfirmShipment(env, outDoc1);
			}
			if (outDoc2 != null) {
				outDoc3 = callStartReceipt(env, outDoc2, Cache, ReceivingLoc, EnterpriseCode, IncidentNo, IncidentYear, IssueNo, CustomerId, ReceiptNo, ReturnHeaderNotes, splitAcctCodeData);
			}
			if (outDoc2 != null && outDoc3 != null) {
				outDoc4 = callReceiveOrder(env, inXML, outDoc3, outDoc2, Cache, ReceivingLoc, EnterpriseCode);
				// BEGIN CRJB2 - April 2, 2013 
				// set strReceiptHeaderKey in env properties for Create MWO method
				if (outDoc4 != null) {
					Element elemOutDoc4_Receipt = outDoc4.getDocumentElement();
					String strReceiptHeaderKey = elemOutDoc4_Receipt.getAttribute("ReceiptHeaderKey");
					env.setTxnObject("ReceiptHeaderKey", strReceiptHeaderKey);
				}
				// END CRJB2 - April 2, 2013 
			}
		} catch (YFSException ex) {
			rootElem.setAttribute("ErrorMessage", "Exception : " + ex.getErrorDescription());
			rootElem.setAttribute("NoException", "false");
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGPerformIncidentReturn'");
			throwAlert(env, stbuf);
		}
		// Return a Blank document incase of success, no data is populated in the system
		Document doc = XMLUtil.createDocument("Receipt");
		return doc;
	}

	/**
	 * 
	 * @param env
	 * @param returnXML
	 * @param CustomerId
	 * @param ReceiptNo
	 * @return
	 * @throws Exception
	 */
	public Document callCreateShipment(YFSEnvironment env, Document returnXML, String CustomerId, String ReceiptNo) throws Exception {
		Document outDoc = null, outDocVerification = null;
		Document rt_Shipment = XMLUtil.newDocument();
		Element el_Shipment = rt_Shipment.createElement("Shipment");
		rt_Shipment.appendChild(el_Shipment);
		el_Shipment.setAttribute("Action", "Create");
		el_Shipment.setAttribute("AllowNewItemReceipt", "DO_NOT_ALLOW");
		el_Shipment.setAttribute("AllowOverage", "N");
		//el_Shipment.setAttribute("BolNo","");
		el_Shipment.setAttribute("DoNotVerifyPalletContent", "Y");
		//Using 'Blind Receipt' Document
		el_Shipment.setAttribute("DocumentType", NWCGConstants.NWCG_RETURN_BLIND_RECEIPT_DOCUMENT);
		//Enterprise is hardcoded to NWCG
		el_Shipment.setAttribute("EnterpriseCode", "NWCG");
		el_Shipment.setAttribute("EspCheckRequired", "N");
		el_Shipment.setAttribute("IgnoreOrdering", "Y");
		el_Shipment.setAttribute("IsAppointmentReqd", "N");
		el_Shipment.setAttribute("IsSingleOrder", "N");
		el_Shipment.setAttribute("ManuallyEntered", "Y");
		//This flag is set to create shipment w/o refering to any Order
		el_Shipment.setAttribute("OrderAvailableOnSystem", "N");
		el_Shipment.setAttribute("OverrideManualShipmentEntry", "Y");
		//get the value from the input xml 
		el_Shipment.setAttribute("ReceivingNode", returnXML.getDocumentElement().getAttribute("CacheID"));
		//Harcoding 
		//Added on 12/06/05 - GN
		el_Shipment.setAttribute("BillToCustomerId", CustomerId);
		el_Shipment.setAttribute("SellerOrganizationCode", NWCGConstants.NWCG_RETURN_SELLER_ORG);
		//Leave blank, use system generated ShipmentNo
		if (ReceiptNo.length() > 1) {
			//Begin CR844 11302012
			String strPrefixInReceiptNo = ReceiptNo.substring(0, 1);
			if (strPrefixInReceiptNo.equals("-")) {
				return null;
			} else {
				//End CR844 11302012
				String ShipmentNo = "S" + ReceiptNo;
				el_Shipment.setAttribute("ShipmentNo", ShipmentNo);
				//Begin CR844 11302012
			}
			//End CR844 11302012
		}

		//el_Shipment.setAttribute("TrailerNo","");
		Element el_ShipmentLines = rt_Shipment.createElement("ShipmentLines");
		el_Shipment.appendChild(el_ShipmentLines);
		outDoc = CommonUtilities.invokeAPI(env, "changeShipment", rt_Shipment);
		return outDoc;
	}

	/**
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 * @throws Exception
	 */
	public Document callConfirmShipment(YFSEnvironment env, Document inXML) throws Exception {
		Document rt_Shipment = null;
		Document outDoc = null, outDocVerification = null;
		String ShipmentKey = inXML.getDocumentElement().getAttribute("ShipmentKey");
		rt_Shipment = XMLUtil.newDocument();
		Element el_Shipment = rt_Shipment.createElement("Shipment");
		rt_Shipment.appendChild(el_Shipment);
		el_Shipment.setAttribute("ShipComplete", "Y");
		el_Shipment.setAttribute("ShipmentKey", ShipmentKey);
		outDoc = CommonUtilities.invokeAPI(env, "confirmShipment", rt_Shipment);
		return outDoc;
	}

	/**
	 * 
	 * @param env
	 * @param outConfirmShipment_XML
	 * @param Cache
	 * @param ReceivingLoc
	 * @param EnterpriseCode
	 * @param IncidentNo
	 * @param IncidentYear
	 * @param IssueNo
	 * @param CustomerId
	 * @param ReceiptNo
	 * @param HeaderNotes
	 * @param splitAcctCodeData
	 * @return
	 * @throws Exception
	 */
	public Document callStartReceipt(YFSEnvironment env, Document outConfirmShipment_XML, String Cache, String ReceivingLoc, String EnterpriseCode, String IncidentNo, String IncidentYear, String IssueNo, String CustomerId, String ReceiptNo, String HeaderNotes, Map<String, String> splitAcctCodeData) throws Exception {
		Document rt_Receipt = null;
		Document outDoc = null, outDocVerification = null;
		String ShipmentKey = outConfirmShipment_XML.getDocumentElement().getAttribute("ShipmentKey");
		String ShipmentNo = outConfirmShipment_XML.getDocumentElement().getAttribute("ShipmentNo");
		rt_Receipt = XMLUtil.newDocument();
		//---------------Creating input doc
		Element el_Receipt = rt_Receipt.createElement("Receipt");
		rt_Receipt.appendChild(el_Receipt);
		//el_Receipt.setAttribute("ArrivalDateTime","");
		//Document Type is -'Blind Receipt'
		el_Receipt.setAttribute("DocumentType", NWCGConstants.NWCG_RETURN_BLIND_RECEIPT_DOCUMENT);
		//el_Receipt.setAttribute("DriverName","");
		//el_Receipt.setAttribute("NumOfCartons","");
		//el_Receipt.setAttribute("NumOfPallets","");
		//el_Receipt.setAttribute("OpenReceiptFlag","");
		//el_Receipt.setAttribute("ReceiptDate","");
		el_Receipt.setAttribute("ReceiptNo", ReceiptNo);
		//Receiving Dock is obtained from the input xml to the API
		el_Receipt.setAttribute("ReceivingDock", ReceivingLoc);
		el_Receipt.setAttribute("ReceivingNode", Cache);
		//el_Receipt.setAttribute("ShipmentKey","");
		//el_Receipt.setAttribute("TrailerLPNNo","");
		//Added by GN on 12/05/06
		Element el_extn = rt_Receipt.createElement("Extn");
		el_Receipt.appendChild(el_extn);
		el_extn.setAttribute("ExtnIncidentNo", IncidentNo);
		//Added by GN on 01/29/07
		el_extn.setAttribute("ExtnIsReturnReceipt", "Y");
		//Added by GN on 04/17/07
		el_extn.setAttribute("ExtnIncidentYear", IncidentYear);
		el_extn.setAttribute("ExtnReturnComments", HeaderNotes);
		//Added by GN on 06/05/2007
		el_extn.setAttribute("ExtnIssueNo", IssueNo);
		Set<String> xmlNames = splitAcctCodeData.keySet();
		for (String xmlName : xmlNames) {
			el_extn.setAttribute(xmlName, splitAcctCodeData.get(xmlName));
		}
		Element el_Shipment = rt_Receipt.createElement("Shipment");
		el_Receipt.appendChild(el_Shipment);
		//el_Shipment.setAttribute("BuyerOrganizationCode","");
		//el_Shipment.setAttribute("DocumentType","");
		el_Shipment.setAttribute("EnterpriseCode", EnterpriseCode);
		//el_Shipment.setAttribute("ExpectedDeliveryDate","");
		el_Shipment.setAttribute("ReceivingNode", Cache);
		//Added on 12/06/05 - GN
		el_Shipment.setAttribute("BillToCustomerId", CustomerId);
		el_Shipment.setAttribute("SellerOrganizationCode", EnterpriseCode);
		el_Shipment.setAttribute("ShipmentKey", ShipmentKey);
		el_Shipment.setAttribute("ShipmentNo", ShipmentNo);
		outDoc = CommonUtilities.invokeAPI(env, "startReceipt", rt_Receipt);
		return outDoc;
	}

	/**
	 * 
	 * @param env
	 * @param itemInfo
	 * @param Cache
	 * @param ReceivingLoc
	 * @param EnterpriseCode
	 * @param outCreateReceipt
	 * @param outConfirmShipment
	 * @param RFI
	 * @param NRFI
	 * @param UnsRet
	 * @param UnsRetNWT
	 * @param LineNotes
	 * @param RecdAsComp
	 * @return
	 * @throws Exception
	 */
	public Document createReceiveOrderDocument(YFSEnvironment env, HashMap itemInfo, String Cache, String ReceivingLoc, String EnterpriseCode, Document outCreateReceipt, Document outConfirmShipment, int RFI, int NRFI, int UnsRet, int UnsRetNWT, String LineNotes, String RecdAsComp) throws Exception {
		logger.verbose("@@@@@ Entering NWCGPerformIncidentReturn::createReceiveOrderDocument @@@@@");	
		//If the item is serially tracked then we ignore values in RFI,NRFI,UnsRet,UnsRetNWT
		String IsSerial = (String) itemInfo.get("PrimarySerialNo");
		String PrimeSerial = "";
		String SecondarySerialNo = "";
		String DispositionCode = "";
		if (itemInfo.get("PrimarySerialNo").toString() == "" || itemInfo.get("PrimarySerialNo").toString() == null) {
			logger.verbose("!!!!! NO DATA PASSED...");
		}
		//String ExpiryDate = getDate(itemInfo.get("ExpiryDate").toString());
		logger.verbose("@@@@@ RFI:-" + RFI + " NRFI:-" + NRFI + " UnsRet:-" + UnsRet + " UnsRetNWT:-" + UnsRetNWT);
		if (IsSerial != "") {
			PrimeSerial = (String) itemInfo.get("PrimarySerialNo");
			SecondarySerialNo = (String) itemInfo.get("SecondarySerialNo");
			DispositionCode = (String) itemInfo.get("DispositionCode");
		}
		Document rt_Receipt = XMLUtil.newDocument();
		Element el_Receipt = rt_Receipt.createElement("Receipt");
		rt_Receipt.appendChild(el_Receipt);
		el_Receipt.setAttribute("AllowNewItemReceipt", "DO_NOT_ALLOW");
		el_Receipt.setAttribute("AllowOverage", "N");
		el_Receipt.setAttribute("BuyerOrganizationCode", "");
		el_Receipt.setAttribute("CaseContentEntryRequired", "N");
		el_Receipt.setAttribute("DispositionCode", "");
		el_Receipt.setAttribute("DispositionDescription", "");
		el_Receipt.setAttribute("DoNotVerifyCaseContent", "N");
		el_Receipt.setAttribute("DoNotVerifyPalletContent", "Y");
		el_Receipt.setAttribute("DocumentType", NWCGConstants.NWCG_RETURN_BLIND_RECEIPT_DOCUMENT);
		el_Receipt.setAttribute("EnterpriseCode", EnterpriseCode);
		el_Receipt.setAttribute("EnterpriseCodeDesc", "Hub Organization");
		el_Receipt.setAttribute("IgnoreOrdering", "Y");
		el_Receipt.setAttribute("IsBlindReceipt", "Y");
		el_Receipt.setAttribute("IsSingleOrder", "N");
		el_Receipt.setAttribute("LinesEntered", "N");
		el_Receipt.setAttribute("LocationId", ReceivingLoc);
		el_Receipt.setAttribute("ManuallyEntered", "Y");
		el_Receipt.setAttribute("Node", Cache);
		el_Receipt.setAttribute("OpenReceiptFlag", "Y");
		el_Receipt.setAttribute("OrderAvailableOnSystem", "N");
		el_Receipt.setAttribute("OrderHeaderKey", "");
		el_Receipt.setAttribute("OrderNo", "");
		el_Receipt.setAttribute("OrderReleaseKey", "");
		el_Receipt.setAttribute("OverReceiptPercentage", "0.00");
		el_Receipt.setAttribute("OverrideManualShipmentEntry", "Y");
		el_Receipt.setAttribute("PalletIdOrCaseId", "");
		el_Receipt.setAttribute("QCRequired", "N");
		el_Receipt.setAttribute("ReceiptError", "N");
		el_Receipt.setAttribute("ReceiptNo", outCreateReceipt.getDocumentElement().getAttribute("ReceiptNo"));
		el_Receipt.setAttribute("ReceiptTotalNumberOfRecords", "1");
		el_Receipt.setAttribute("ReceivingDock", ReceivingLoc);
		el_Receipt.setAttribute("ReceivingNode", Cache);
		el_Receipt.setAttribute("SelectedUOM", "");
		el_Receipt.setAttribute("SellerOrganizationCode", EnterpriseCode);
		el_Receipt.setAttribute("SellerOrganizationCodeDesc", "Hub Organization");
		el_Receipt.setAttribute("SerialTracking", "Y");
		el_Receipt.setAttribute("ShipNode", "");
		el_Receipt.setAttribute("ShipmentError", "N");
		el_Receipt.setAttribute("ShipmentKey", outConfirmShipment.getDocumentElement().getAttribute("ShipmentKey"));
		el_Receipt.setAttribute("ShipmentNo", outConfirmShipment.getDocumentElement().getAttribute("ShipmentNo"));
		el_Receipt.setAttribute("ShipmentTotalNumberOfRecords", "1");
		Element el_ReceiptLines = rt_Receipt.createElement("ReceiptLines");
		el_Receipt.appendChild(el_ReceiptLines);
		String UnitPrice = "";
		String IssuePrice = "";
		IssuePrice = getIssuePrice(env, itemInfo.get("ItemID").toString(), itemInfo.get("PrimarySerialNo").toString());
		if (IssuePrice.length() > 0) {
			UnitPrice = IssuePrice;
		} else {
			UnitPrice = getUnitPrice(env, itemInfo.get("ItemID").toString(), itemInfo.get("ProductClass").toString(), itemInfo.get("UnitOfMeasure").toString());
		}
		//FYI - TagAttribute1 is "LotAttribute1" represents 'Manufacturers name'
		//	- TagAttribute2 is "LotAttribute3" represents 'Manufacturer Model'
		//	- TagAttribute3 is "Lot Number" for tracking purpose
		//	- TagAttribute4 is "Revision Number" for last tested date
		if (RFI != 0) {
			logger.verbose("@@@@@ RFI...");
			Element el_ReceiptLine = rt_Receipt.createElement("ReceiptLine");
			el_ReceiptLines.appendChild(el_ReceiptLine);
			el_ReceiptLine.setAttribute("DispositionCode", "RFI");
			el_ReceiptLine.setAttribute("EnterpriseCode", EnterpriseCode);
			el_ReceiptLine.setAttribute("FifoNo", "0");
			el_ReceiptLine.setAttribute("ItemID", itemInfo.get("ItemID").toString());
			el_ReceiptLine.setAttribute("LocationId", ReceivingLoc);
			if (itemInfo.get("LotAttribute1") != null)
				el_ReceiptLine.setAttribute("LotAttribute1", itemInfo.get("LotAttribute1").toString());
			if (itemInfo.get("LotAttribute2") != null)
				el_ReceiptLine.setAttribute("LotAttribute2", itemInfo.get("LotAttribute2").toString());
			if (itemInfo.get("LotAttribute3") != null)
				el_ReceiptLine.setAttribute("LotAttribute3", itemInfo.get("LotAttribute3").toString());
			if (itemInfo.get("LotNumber") != null)
				el_ReceiptLine.setAttribute("LotNumber", itemInfo.get("LotNumber").toString());
			//el_ReceiptLine.setAttribute("LotKeyReference",itemInfo.get("LotKeyReference").toString());
			if (itemInfo.get("RevisionNo") != null)
				el_ReceiptLine.setAttribute("RevisionNo", itemInfo.get("RevisionNo").toString());
			if (itemInfo.get("BatchNo") != null)
				el_ReceiptLine.setAttribute("BatchNo", itemInfo.get("BatchNo").toString());
			el_ReceiptLine.setAttribute("NetWeight", "0.00");
			el_ReceiptLine.setAttribute("PrimeLineNo", "0");
			el_ReceiptLine.setAttribute("ProductClass", itemInfo.get("ProductClass").toString());
			el_ReceiptLine.setAttribute("Quantity", Integer.toString(RFI));
			el_ReceiptLine.setAttribute("ReleaseNo", "0");
			el_ReceiptLine.setAttribute("SerialNo", "");
			el_ReceiptLine.setAttribute("ShipmentLineNo", "0");
			el_ReceiptLine.setAttribute("ShipmentSubLineNo", "0");
			el_ReceiptLine.setAttribute("SubLineNo", "0");
			if (itemInfo.get("ExpiryDate").toString() != null && itemInfo.get("ExpiryDate").toString() != "") {
				el_ReceiptLine.setAttribute("ShipByDate", getDate(itemInfo.get("ExpiryDate").toString()));
			}
			el_ReceiptLine.setAttribute("UnitOfMeasure", itemInfo.get("UnitOfMeasure").toString());
			el_ReceiptLine.setAttribute("InspectionComments", LineNotes);
			Element rl_extn = rt_Receipt.createElement("Extn");
			el_ReceiptLine.appendChild(rl_extn);
			rl_extn.setAttribute("ExtnReceivedAsComponent", RecdAsComp);
			rl_extn.setAttribute("ExtnReceivingPrice", UnitPrice);
			logger.verbose("@@@@@ Exiting NWCGPerformIncidentReturn::createReceiveOrderDocument (RFI) @@@@@");
			return rt_Receipt;
		}
		if (NRFI != 0) {
			logger.verbose("@@@@@ NRFI...");
			Element el_ReceiptLine = rt_Receipt.createElement("ReceiptLine");
			el_ReceiptLines.appendChild(el_ReceiptLine);
			el_ReceiptLine.setAttribute("DispositionCode", "NRFI");
			el_ReceiptLine.setAttribute("EnterpriseCode", EnterpriseCode);
			el_ReceiptLine.setAttribute("FifoNo", "0");
			el_ReceiptLine.setAttribute("ItemID", itemInfo.get("ItemID").toString());
			el_ReceiptLine.setAttribute("LocationId", ReceivingLoc);
			if (itemInfo.get("LotAttribute1") != null)
				el_ReceiptLine.setAttribute("LotAttribute1", itemInfo.get("LotAttribute1").toString());
			if (itemInfo.get("LotAttribute2") != null)
				el_ReceiptLine.setAttribute("LotAttribute2", itemInfo.get("LotAttribute2").toString());
			if (itemInfo.get("LotAttribute3") != null)
				el_ReceiptLine.setAttribute("LotAttribute3", itemInfo.get("LotAttribute3").toString());
			if (itemInfo.get("LotNumber") != null)
				el_ReceiptLine.setAttribute("LotNumber", itemInfo.get("LotNumber").toString());
			//el_ReceiptLine.setAttribute("LotKeyReference",itemInfo.get("LotKeyReference").toString());
			if (itemInfo.get("RevisionNo") != null)
				el_ReceiptLine.setAttribute("RevisionNo", itemInfo.get("RevisionNo").toString());
			if (itemInfo.get("BatchNo") != null)
				el_ReceiptLine.setAttribute("BatchNo", itemInfo.get("BatchNo").toString());
			el_ReceiptLine.setAttribute("NetWeight", "0.00");
			el_ReceiptLine.setAttribute("PrimeLineNo", "0");
			el_ReceiptLine.setAttribute("ProductClass", itemInfo.get("ProductClass").toString());
			el_ReceiptLine.setAttribute("Quantity", Integer.toString(NRFI));
			el_ReceiptLine.setAttribute("ReleaseNo", "0");
			el_ReceiptLine.setAttribute("SerialNo", "");
			el_ReceiptLine.setAttribute("ShipmentLineNo", "0");
			el_ReceiptLine.setAttribute("ShipmentSubLineNo", "0");
			el_ReceiptLine.setAttribute("SubLineNo", "0");
			if (itemInfo.get("ExpiryDate").toString() != null && itemInfo.get("ExpiryDate").toString() != "")
				el_ReceiptLine.setAttribute("ShipByDate", getDate(itemInfo.get("ExpiryDate").toString()));
			el_ReceiptLine.setAttribute("UnitOfMeasure", itemInfo.get("UnitOfMeasure").toString());
			el_ReceiptLine.setAttribute("InspectionComments", LineNotes);
			Element rl_extn = rt_Receipt.createElement("Extn");
			el_ReceiptLine.appendChild(rl_extn);
			rl_extn.setAttribute("ExtnReceivedAsComponent", RecdAsComp);
			rl_extn.setAttribute("ExtnReceivingPrice", UnitPrice);
			logger.verbose("@@@@@ Exiting NWCGPerformIncidentReturn::createReceiveOrderDocument (NRFI) @@@@@");
			return rt_Receipt;
		}
		if (UnsRet != 0) {
			logger.verbose("@@@@@ UnsRet...");
			Element el_ReceiptLine = rt_Receipt.createElement("ReceiptLine");
			el_ReceiptLines.appendChild(el_ReceiptLine);
			el_ReceiptLine.setAttribute("DispositionCode", "UNSERVICE");
			el_ReceiptLine.setAttribute("EnterpriseCode", EnterpriseCode);
			el_ReceiptLine.setAttribute("FifoNo", "0");
			el_ReceiptLine.setAttribute("ItemID", itemInfo.get("ItemID").toString());
			el_ReceiptLine.setAttribute("LocationId", ReceivingLoc);
			if (itemInfo.get("LotAttribute1") != null)
				el_ReceiptLine.setAttribute("LotAttribute1", itemInfo.get("LotAttribute1").toString());
			if (itemInfo.get("LotAttribute2") != null)
				el_ReceiptLine.setAttribute("LotAttribute2", itemInfo.get("LotAttribute2").toString());
			if (itemInfo.get("LotAttribute3") != null)
				el_ReceiptLine.setAttribute("LotAttribute3", itemInfo.get("LotAttribute3").toString());
			if (itemInfo.get("LotNumber") != null)
				el_ReceiptLine.setAttribute("LotNumber", itemInfo.get("LotNumber").toString());
			//el_ReceiptLine.setAttribute("LotKeyReference",itemInfo.get("LotKeyReference").toString());
			if (itemInfo.get("RevisionNo") != null)
				el_ReceiptLine.setAttribute("RevisionNo", itemInfo.get("RevisionNo").toString());
			if (itemInfo.get("BatchNo") != null)
				el_ReceiptLine.setAttribute("BatchNo", itemInfo.get("BatchNo").toString());
			el_ReceiptLine.setAttribute("NetWeight", "0.00");
			el_ReceiptLine.setAttribute("PrimeLineNo", "0");
			el_ReceiptLine.setAttribute("ProductClass", itemInfo.get("ProductClass").toString());
			el_ReceiptLine.setAttribute("Quantity", Integer.toString(UnsRet));
			el_ReceiptLine.setAttribute("ReleaseNo", "0");
			el_ReceiptLine.setAttribute("SerialNo", "");
			el_ReceiptLine.setAttribute("ShipmentLineNo", "0");
			el_ReceiptLine.setAttribute("ShipmentSubLineNo", "0");
			el_ReceiptLine.setAttribute("SubLineNo", "0");
			if (itemInfo.get("ExpiryDate").toString() != null && itemInfo.get("ExpiryDate").toString() != "")
				el_ReceiptLine.setAttribute("ShipByDate", getDate(itemInfo.get("ExpiryDate").toString()));
			el_ReceiptLine.setAttribute("UnitOfMeasure", itemInfo.get("UnitOfMeasure").toString());
			el_ReceiptLine.setAttribute("InspectionComments", LineNotes);
			Element rl_extn = rt_Receipt.createElement("Extn");
			el_ReceiptLine.appendChild(rl_extn);
			rl_extn.setAttribute("ExtnReceivedAsComponent", RecdAsComp);
			rl_extn.setAttribute("ExtnReceivingPrice", UnitPrice);
			logger.verbose("@@@@@ Exiting NWCGPerformIncidentReturn::createReceiveOrderDocument (UnsRet) @@@@@");
			return rt_Receipt;
		}
		if (UnsRetNWT != 0) {
			logger.verbose("@@@@@ NWT...");
			Element el_ReceiptLine = rt_Receipt.createElement("ReceiptLine");
			el_ReceiptLines.appendChild(el_ReceiptLine);
			el_ReceiptLine.setAttribute("DispositionCode", "UNSRV-NWT");
			el_ReceiptLine.setAttribute("EnterpriseCode", EnterpriseCode);
			el_ReceiptLine.setAttribute("FifoNo", "0");
			el_ReceiptLine.setAttribute("ItemID", itemInfo.get("ItemID").toString());
			el_ReceiptLine.setAttribute("LocationId", ReceivingLoc);
			if (itemInfo.get("LotAttribute1") != null)
				el_ReceiptLine.setAttribute("LotAttribute1", itemInfo.get("LotAttribute1").toString());
			if (itemInfo.get("LotAttribute2") != null)
				el_ReceiptLine.setAttribute("LotAttribute2", itemInfo.get("LotAttribute2").toString());
			if (itemInfo.get("LotAttribute3") != null)
				el_ReceiptLine.setAttribute("LotAttribute3", itemInfo.get("LotAttribute3").toString());
			if (itemInfo.get("LotNumber") != null)
				el_ReceiptLine.setAttribute("LotNumber", itemInfo.get("LotNumber").toString());
			//el_ReceiptLine.setAttribute("LotKeyReference",itemInfo.get("LotKeyReference").toString());
			if (itemInfo.get("RevisionNo") != null)
				el_ReceiptLine.setAttribute("RevisionNo", itemInfo.get("RevisionNo").toString());
			el_ReceiptLine.setAttribute("NetWeight", "0.00");
			if (itemInfo.get("BatchNo") != null)
				el_ReceiptLine.setAttribute("BatchNo", itemInfo.get("BatchNo").toString());
			el_ReceiptLine.setAttribute("PrimeLineNo", "0");
			el_ReceiptLine.setAttribute("ProductClass", itemInfo.get("ProductClass").toString());
			el_ReceiptLine.setAttribute("Quantity", Integer.toString(UnsRetNWT));
			el_ReceiptLine.setAttribute("ReleaseNo", "0");
			el_ReceiptLine.setAttribute("SerialNo", "");
			el_ReceiptLine.setAttribute("ShipmentLineNo", "0");
			el_ReceiptLine.setAttribute("ShipmentSubLineNo", "0");
			el_ReceiptLine.setAttribute("SubLineNo", "0");
			if (itemInfo.get("ExpiryDate").toString() != null && itemInfo.get("ExpiryDate").toString() != "")
				el_ReceiptLine.setAttribute("ShipByDate", getDate(itemInfo.get("ExpiryDate").toString()));
			el_ReceiptLine.setAttribute("UnitOfMeasure", itemInfo.get("UnitOfMeasure").toString());
			el_ReceiptLine.setAttribute("InspectionComments", LineNotes);
			Element rl_extn = rt_Receipt.createElement("Extn");
			el_ReceiptLine.appendChild(rl_extn);
			rl_extn.setAttribute("ExtnReceivedAsComponent", RecdAsComp);
			rl_extn.setAttribute("ExtnReceivingPrice", UnitPrice);
			logger.verbose("@@@@@ Exiting NWCGPerformIncidentReturn::createReceiveOrderDocument (NWT) @@@@@");
			return rt_Receipt;
		}
		if (IsSerial != "") {
			Element el_ReceiptLine = rt_Receipt.createElement("ReceiptLine");
			el_ReceiptLines.appendChild(el_ReceiptLine);
			if (itemInfo.get("DispositionCode") != null) {
				String DispositionCodeFinal = "";
				if (itemInfo.get("DispositionCode").toString().equals("UnsRet"))
					DispositionCodeFinal = "UNSERVICE";
				if (itemInfo.get("DispositionCode").toString().equals("UnsRetNWT"))
					DispositionCodeFinal = "UNSRV-NWT";
				if (itemInfo.get("DispositionCode").toString().equals("NRFI"))
					DispositionCodeFinal = "NRFI";
				if (itemInfo.get("DispositionCode").toString().equals("RFI"))
					DispositionCodeFinal = "RFI";
				el_ReceiptLine.setAttribute("DispositionCode", DispositionCodeFinal);
			}
			el_ReceiptLine.setAttribute("EnterpriseCode", EnterpriseCode);
			el_ReceiptLine.setAttribute("FifoNo", "0");
			el_ReceiptLine.setAttribute("ItemID", itemInfo.get("ItemID").toString());
			el_ReceiptLine.setAttribute("LocationId", ReceivingLoc);
			if (itemInfo.get("LotAttribute1") != null && !(itemInfo.get("LotAttribute1").equals("03")))
				el_ReceiptLine.setAttribute("LotAttribute1", itemInfo.get("LotAttribute1").toString());
			if (itemInfo.get("LotAttribute2") != null && !(itemInfo.get("LotAttribute2").equals("03")))
				el_ReceiptLine.setAttribute("LotAttribute2", itemInfo.get("LotAttribute2").toString());
			if (itemInfo.get("LotAttribute3") != null && !(itemInfo.get("LotAttribute3").equals("03")))
				el_ReceiptLine.setAttribute("LotAttribute3", itemInfo.get("LotAttribute3").toString());
			if (itemInfo.get("LotNumber") != null && !(itemInfo.get("LotNumber").equals("03")))
				el_ReceiptLine.setAttribute("LotNumber", itemInfo.get("LotNumber").toString());
			//el_ReceiptLine.setAttribute("LotKeyReference",itemInfo.get("LotKeyReference").toString());
			if (itemInfo.get("RevisionNo") != null && !(itemInfo.get("RevisionNo").equals("03")))
				el_ReceiptLine.setAttribute("RevisionNo", itemInfo.get("RevisionNo").toString());
			if (itemInfo.get("BatchNo") != null && !(itemInfo.get("BatchNo").equals("03")))
				el_ReceiptLine.setAttribute("BatchNo", itemInfo.get("BatchNo").toString());
			el_ReceiptLine.setAttribute("NetWeight", "0.00");
			el_ReceiptLine.setAttribute("PrimeLineNo", "0");
			if (itemInfo.get("ProductClass") != null) {
				el_ReceiptLine.setAttribute("ProductClass", itemInfo.get("ProductClass").toString());
			}
			el_ReceiptLine.setAttribute("Quantity", "1");
			el_ReceiptLine.setAttribute("ReleaseNo", "0");
			if (itemInfo.get("PrimarySerialNo") != null)
				el_ReceiptLine.setAttribute("SerialNo", itemInfo.get("PrimarySerialNo").toString());
			//el_ReceiptLine.setAttribute("SerialNo","30000");
			if (itemInfo.get("SecondarySerialNo") != null) {
				Element el_Serial = rt_Receipt.createElement("SerialDetail");
				el_ReceiptLine.appendChild(el_Serial);
				el_Serial.setAttribute("SecondarySerial1", itemInfo.get("SecondarySerialNo").toString());
			}
			el_ReceiptLine.setAttribute("ShipmentLineNo", "0");
			el_ReceiptLine.setAttribute("ShipmentSubLineNo", "0");
			el_ReceiptLine.setAttribute("SubLineNo", "0");
			if (itemInfo.get("ExpiryDate") != null && itemInfo.get("ExpiryDate").toString() != "")
				el_ReceiptLine.setAttribute("ShipByDate", getDate(itemInfo.get("ExpiryDate").toString()));
			if (itemInfo.get("UnitOfMeasure") != null) {
				el_ReceiptLine.setAttribute("UnitOfMeasure", itemInfo.get("UnitOfMeasure").toString());
			}
			el_ReceiptLine.setAttribute("InspectionComments", LineNotes);
			Element rl_extn = rt_Receipt.createElement("Extn");
			el_ReceiptLine.appendChild(rl_extn);
			rl_extn.setAttribute("ExtnReceivedAsComponent", RecdAsComp);
			rl_extn.setAttribute("ExtnReceivingPrice", UnitPrice);
			logger.verbose("@@@@@ Exiting NWCGPerformIncidentReturn::createReceiveOrderDocument (IsSerial) @@@@@");
			return rt_Receipt;
		}
		logger.verbose("@@@@@ Exiting NWCGPerformIncidentReturn::createReceiveOrderDocument @@@@@");
		return rt_Receipt;
	}

	/**
	 * Sample MoveRequest XML:
	 * 
	 * <MoveRequest FromActivityGroup="RECEIPT" Node="" >
	 * <MoveRequestLines>
	 * <MoveRequestLine InventoryStatus="RFI-RTN" ItemId="001095" ProductClass="Supply" RequestQuantity="1.00" SerialNo="" SourceLocationId="RETURN-1" TargetLocationId="NWCG_RFI_RTN_LOCATION" UnitOfMeasure="EA">
	 * <Receipt ReceiptNo="R0000199001" ReceivingNode="CORMK" />
	 * <MoveRequestLineTag BatchNo="" LotAttribute1="" LotAttribute2="" LotAttribute3="" LotNumber="" RevisionNo="" />
	 * </MoveRequestLine>
	 * </MoveRequestLines>
	 * </MoveRequest> 
	 * 
	 * @param env
	 * @param itemInfo
	 * @param UserId
	 * @param Cache
	 * @param ReceivingLoc
	 * @param EnterpriseCode
	 * @param outCreateReceipt
	 * @param outConfirmShipment
	 * @param RFI
	 * @param NRFI
	 * @param UnsRet
	 * @param UnsRetNWT
	 * @param LineNotes
	 * @param RecdAsComp
	 * @return
	 * @throws Exception
	 */
	public Document createMoveRequestDocument(YFSEnvironment env, HashMap itemInfo, String UserId, String Cache, String ReceivingLoc, String EnterpriseCode, Document outCreateReceipt, Document outConfirmShipment, int RFI, int NRFI, int UnsRet, int UnsRetNWT, String LineNotes, String RecdAsComp) throws Exception {
		logger.verbose("@@@@@ Entering NWCGPerformIncidentReturn::createMoveRequestDocument @@@@@");	
		//If the item is serially tracked then we ignore values in RFI,NRFI,UnsRet,UnsRetNWT
		String IsSerial = (String) itemInfo.get("PrimarySerialNo");
		String PrimeSerial = "";
		String SecondarySerialNo = "";
		String DispositionCode = "";
		Document UNS_Doc = null;
		if (itemInfo.get("PrimarySerialNo").toString() == "" || itemInfo.get("PrimarySerialNo").toString() == null) {
			logger.verbose("!!!!! NO DATA PASSED...");
		}
		logger.verbose("@@@@@ RFI:-" + RFI + " NRFI:-" + NRFI + " UnsRet:-" + UnsRet + " UnsRetNWT:-" + UnsRetNWT);
		if (IsSerial != "") {
			PrimeSerial = (String) itemInfo.get("PrimarySerialNo");
			SecondarySerialNo = (String) itemInfo.get("SecondarySerialNo");
			DispositionCode = (String) itemInfo.get("DispositionCode");
		}
		Document doc_MoveRequest = XMLUtil.newDocument();
		Element el_MoveRequest = doc_MoveRequest.createElement("MoveRequest");
		doc_MoveRequest.appendChild(el_MoveRequest);
		el_MoveRequest.setAttribute("FromActivityGroup", "RECEIPT");
		if (NRFI > 0) {
			el_MoveRequest.setAttribute("ForActivityCode", "REFURBISHMENT");
		} else {
			el_MoveRequest.setAttribute("ForActivityCode", "STORAGE");
		}
		el_MoveRequest.setAttribute("Node", Cache);
		el_MoveRequest.setAttribute("EnterpriseCode", EnterpriseCode);
		el_MoveRequest.setAttribute("Priority", "3");
		el_MoveRequest.setAttribute("IgnoreOrdering", "Y");
		el_MoveRequest.setAttribute("ShipmentKey", outConfirmShipment.getDocumentElement().getAttribute("ShipmentKey"));
		Element el_Shipment = doc_MoveRequest.createElement("Shipment");
		el_MoveRequest.appendChild(el_Shipment);
		el_Shipment.setAttribute("SellerOrganizationCode", EnterpriseCode);
		el_Shipment.setAttribute("ShipNode", "");
		el_Shipment.setAttribute("ShipmentNo", outConfirmShipment.getDocumentElement().getAttribute("ShipmentNo"));
		Element el_MoveRequestLines = doc_MoveRequest.createElement("MoveRequestLines");
		el_MoveRequest.appendChild(el_MoveRequestLines);
		// TagAttribute1 is "LotAttribute1" represents 'Manufacturers name'
		// TagAttribute2 is "LotAttribute3" represents 'Manufacturer Model'
		// TagAttribute3 is "Lot Number" for tracking purpose
		// TagAttribute4 is "Revision Number" for last tested date
		Element el_MoveRequestLine = doc_MoveRequest.createElement("MoveRequestLine");
		el_MoveRequestLines.appendChild(el_MoveRequestLine);
		el_MoveRequestLine.setAttribute("ItemId", itemInfo.get("ItemID").toString());
		el_MoveRequestLine.setAttribute("ProductClass", itemInfo.get("ProductClass").toString());
		el_MoveRequestLine.setAttribute("EnterpriseCode", EnterpriseCode);
		el_MoveRequestLine.setAttribute("SourceLocationId", ReceivingLoc);
		el_MoveRequestLine.setAttribute("UnitOfMeasure", itemInfo.get("UnitOfMeasure").toString());
		if (itemInfo.get("ExpiryDate").toString() != null && itemInfo.get("ExpiryDate").toString() != "") {
			el_MoveRequestLine.setAttribute("ShipByDate", getDate(itemInfo.get("ExpiryDate").toString()));
		}
		Element el_Tag = doc_MoveRequest.createElement("MoveRequestLineTag");
		el_MoveRequestLine.appendChild(el_Tag);
		if (itemInfo.get("LotAttribute1") != null)
			el_Tag.setAttribute("LotAttribute1", itemInfo.get("LotAttribute1").toString());
		if (itemInfo.get("LotAttribute2") != null)
			el_Tag.setAttribute("LotAttribute2", itemInfo.get("LotAttribute2").toString());
		if (itemInfo.get("LotAttribute3") != null)
			el_Tag.setAttribute("LotAttribute3", itemInfo.get("LotAttribute3").toString());
		if (itemInfo.get("LotNumber") != null)
			el_Tag.setAttribute("LotNumber", itemInfo.get("LotNumber").toString());
		if (itemInfo.get("RevisionNo") != null)
			el_Tag.setAttribute("RevisionNo", itemInfo.get("RevisionNo").toString());
		if (itemInfo.get("BatchNo") != null)
			el_Tag.setAttribute("BatchNo", itemInfo.get("BatchNo").toString());
		if (RFI != 0) {
			el_MoveRequestLine.setAttribute("InventoryStatus", "RFI");
			el_MoveRequestLine.setAttribute("RequestQuantity", Integer.toString(RFI));
			el_MoveRequestLine.setAttribute("TargetLocationId", "RFI-1");
		}
		if (NRFI != 0) {
			el_MoveRequestLine.setAttribute("InventoryStatus", "NRFI");
			el_MoveRequestLine.setAttribute("RequestQuantity", Integer.toString(NRFI));
			el_MoveRequestLine.setAttribute("TargetLocationId", "NRFI-1");
		}
		if (UnsRet != 0) {
			logger.verbose("@@@@@ Exiting NWCGPerformIncidentReturn::createMoveRequestDocument (1) @@@@@");
			return UNS_Doc;
		}
		if (UnsRetNWT != 0) {
			logger.verbose("@@@@@ Exiting NWCGPerformIncidentReturn::createMoveRequestDocument (2) @@@@@");
			return UNS_Doc;
		}

		if (IsSerial != "") {
			el_MoveRequestLine.setAttribute("SerialNo", itemInfo.get("PrimarySerialNo").toString());
			el_MoveRequestLine.setAttribute("RequestQuantity", "1.00");
			if (itemInfo.get("DispositionCode") != null) {
				if (itemInfo.get("DispositionCode").toString().equals("UnsRet")) {
					return UNS_Doc;
				}
				if (itemInfo.get("DispositionCode").toString().equals("UnsRetNWT")) {
					return UNS_Doc;
				}
				if (itemInfo.get("DispositionCode").toString().equals("NRFI")) {
					el_MoveRequestLine.setAttribute("InventoryStatus", "NRFI");
					el_MoveRequestLine.setAttribute("TargetLocationId", "NRFI-1");
				}
				if (itemInfo.get("DispositionCode").toString().equals("RFI")) {
					el_MoveRequestLine.setAttribute("InventoryStatus", "RFI");
					el_MoveRequestLine.setAttribute("TargetLocationId", "RFI-1");
				}
			}
		}
		logger.verbose("@@@@@ Exiting NWCGPerformIncidentReturn::createMoveRequestDocument @@@@@");
		return doc_MoveRequest;
	}

	/**
	 * Sample MoveLocationInventory XML:
	 * 
	 * <MoveLocationInventory EnterpriseCode="NWCG" Node="CORMK">
	 * <Source CaseId="" LocationId="RFI-1" PalletId="">
	 * <Inventory CountryOfOrigin="" FifoNo="" InventoryItemKey="" InventoryStatus="RFI" InventoryTagKey="" Quantity="1" ReceiptHeaderKey="" Segment="" SegmentType="" ShipByDate="">
	 * <Receipt ReceiptNo="" /> 
	 * <InventoryItem ItemID="000825" ProductClass="Supply" UnitOfMeasure="EA" /> 
	 * <TagDetail BatchNo="" LotAttribute1="" LotAttribute2="" LotAttribute3="" LotKeyReference="" LotNumber="" ManufacturingDate="" RevisionNo="" /> 
	 * <SerialList>
	 * <SerialDetail SerialNo="" /> 
	 * </SerialList>
	 * </Inventory>
	 * </Source>
	 * <Destination CaseId="" LocationId="STOR1-01010101" PalletId="" /> 
	 * <Audit DocumentType="" ReasonCode="" ReasonText="" Reference1="" Reference2="" Reference3="" Reference4="" Reference5="" /> 
	 * </MoveLocationInventory>
	 * 
	 * @param env
	 * @param itemInfo
	 * @param UserId
	 * @param Cache
	 * @param ReceivingLoc
	 * @param EnterpriseCode
	 * @param outCreateReceipt
	 * @param outConfirmShipment
	 * @param RFI
	 * @param NRFI
	 * @param UnsRet
	 * @param UnsRetNWT
	 * @param LineNotes
	 * @param RecdAsComp
	 * @return
	 * @throws Exception
	 */
	public Document createMoveLocationInvDocument(YFSEnvironment env, HashMap itemInfo, String UserId, String Cache, String ReceivingLoc, String EnterpriseCode, Document outCreateReceipt, Document outConfirmShipment, int RFI, int NRFI, int UnsRet, int UnsRetNWT, String LineNotes, String RecdAsComp) throws Exception {
		logger.verbose("@@@@@ Entering NWCGPerformIncidentReturn::createMoveLocationInvDocument @@@@@");
		//If the item is serially tracked then we ignore values in RFI,NRFI,UnsRet,UnsRetNWT
		String IsSerial = (String) itemInfo.get("PrimarySerialNo");
		String PrimeSerial = "";
		String SecondarySerialNo = "";
		String DispositionCode = "";
		if (itemInfo.get("PrimarySerialNo").toString() == "" || itemInfo.get("PrimarySerialNo").toString() == null)
			logger.verbose("!!!!! NO DATA PASSED...");
		logger.verbose("@@@@@ RFI:-" + RFI + " NRFI:-" + NRFI + " UnsRet:-" + UnsRet + " UnsRetNWT:-" + UnsRetNWT);
		if (IsSerial != "") {
			PrimeSerial = (String) itemInfo.get("PrimarySerialNo");
			SecondarySerialNo = (String) itemInfo.get("SecondarySerialNo");
			DispositionCode = (String) itemInfo.get("DispositionCode");
		}
		Document doc_MoveRequest = XMLUtil.newDocument();
		Element el_MoveRequest = doc_MoveRequest.createElement("MoveLocationInventory");
		doc_MoveRequest.appendChild(el_MoveRequest);
		el_MoveRequest.setAttribute("Node", Cache);
		el_MoveRequest.setAttribute("EnterpriseCode", EnterpriseCode);
		Element ElemSource = doc_MoveRequest.createElement("Source");
		el_MoveRequest.appendChild(ElemSource);
		Element ElemDest = doc_MoveRequest.createElement("Destination");
		el_MoveRequest.appendChild(ElemDest);
		ElemSource.setAttribute("LocationId", ReceivingLoc);
		Element ElemInv = doc_MoveRequest.createElement("Inventory");
		ElemSource.appendChild(ElemInv);
		if (itemInfo.get("ExpiryDate").toString() != null && itemInfo.get("ExpiryDate").toString() != "") {
			ElemInv.setAttribute("ShipByDate", getDate(itemInfo.get("ExpiryDate").toString()));
		}
		if (RFI != 0) {
			ElemInv.setAttribute("InventoryStatus", "RFI");
			ElemInv.setAttribute("Quantity", Integer.toString(RFI));
			if (RRPFlag.equals("YES")) {
				ElemDest.setAttribute("LocationId", "REFURB-RFI");
			} else {
				ElemDest.setAttribute("LocationId", "RFI-1");
			}
		}
		if (NRFI != 0) {
			ElemInv.setAttribute("InventoryStatus", "NRFI");
			ElemInv.setAttribute("Quantity", Integer.toString(NRFI));
			ElemDest.setAttribute("LocationId", "NRFI-1");
		}
		if (UnsRet != 0) {
			ElemInv.setAttribute("InventoryStatus", "UNSERVICE");
			ElemInv.setAttribute("Quantity", Integer.toString(UnsRet));
			ElemDest.setAttribute("LocationId", "UNS-1");
		}
		if (UnsRetNWT != 0) {
			ElemInv.setAttribute("InventoryStatus", "UNSRV-NWT");
			ElemInv.setAttribute("Quantity", Integer.toString(UnsRetNWT));
			ElemDest.setAttribute("LocationId", "UNSNWT-1");
		}
		Element ElemInvItem = doc_MoveRequest.createElement("InventoryItem");
		ElemInv.appendChild(ElemInvItem);
		ElemInvItem.setAttribute("ItemID", itemInfo.get("ItemID").toString());
		ElemInvItem.setAttribute("ProductClass", itemInfo.get("ProductClass").toString());
		ElemInvItem.setAttribute("UnitOfMeasure", itemInfo.get("UnitOfMeasure").toString());
		Element ElemReceipt = doc_MoveRequest.createElement("Receipt");
		ElemInv.appendChild(ElemReceipt);
		ElemReceipt.setAttribute("ReceiptNo", outCreateReceipt.getDocumentElement().getAttribute("ReceiptNo"));
		// TagAttribute1 is "LotAttribute1" represents 'Manufacturers name'
		// TagAttribute2 is "LotAttribute3" represents 'Manufacturer Model'
		// TagAttribute3 is "Lot Number" for tracking purpose
		// TagAttribute4 is "Revision Number" for last tested date
		Element ElemTag = doc_MoveRequest.createElement("TagDetail");
		ElemInv.appendChild(ElemTag);
		if (itemInfo.get("LotAttribute1") != null)
			ElemTag.setAttribute("LotAttribute1", itemInfo.get("LotAttribute1").toString());
		if (itemInfo.get("LotAttribute2") != null)
			ElemTag.setAttribute("LotAttribute2", itemInfo.get("LotAttribute2").toString());
		if (itemInfo.get("LotAttribute3") != null)
			ElemTag.setAttribute("LotAttribute3", itemInfo.get("LotAttribute3").toString());
		if (itemInfo.get("LotNumber") != null)
			ElemTag.setAttribute("LotNumber", itemInfo.get("LotNumber").toString());
		if (itemInfo.get("RevisionNo") != null)
			ElemTag.setAttribute("RevisionNo", itemInfo.get("RevisionNo").toString());
		if (itemInfo.get("BatchNo") != null)
			ElemTag.setAttribute("BatchNo", itemInfo.get("BatchNo").toString());
		if (IsSerial != "") {
			Element ElemSerialList = doc_MoveRequest.createElement("SerialList");
			ElemInv.appendChild(ElemSerialList);
			Element ElemSerialDetail = doc_MoveRequest.createElement("SerialDetail");
			ElemSerialList.appendChild(ElemSerialDetail);
			ElemSerialDetail.setAttribute("SerialNo", itemInfo.get("PrimarySerialNo").toString());
			ElemInv.setAttribute("Quantity", "1.00");
			if (itemInfo.get("DispositionCode") != null) {
				if (itemInfo.get("DispositionCode").toString().equals("UnsRet")) {
					ElemInv.setAttribute("InventoryStatus", "UNSERVICE");
					ElemDest.setAttribute("LocationId", "UNS-1");
				}
				if (itemInfo.get("DispositionCode").toString().equals("UnsRetNWT")) {
					ElemInv.setAttribute("InventoryStatus", "UNSRV-NWT");
					ElemDest.setAttribute("LocationId", "UNSNWT-1");
				}
				if (itemInfo.get("DispositionCode").toString().equals("NRFI")) {
					ElemInv.setAttribute("InventoryStatus", "NRFI");
					ElemDest.setAttribute("LocationId", "NRFI-1");
				}
				if (itemInfo.get("DispositionCode").toString().equals("RFI")) {
					ElemInv.setAttribute("InventoryStatus", "RFI");
					if (RRPFlag.equals("YES")) {
						ElemDest.setAttribute("LocationId", "REFURB-RFI");
					} else {
						ElemDest.setAttribute("LocationId", "RFI-1");
					}
				}
			}
		}
		logger.verbose("@@@@@ Exiting NWCGPerformIncidentReturn::createMoveLocationInvDocument @@@@@");
		return doc_MoveRequest;
	}

	/**
	 * 
	 * @param env
	 * @param outStartReceipt_XML
	 * @param Cache
	 * @param EnterpriseCode
	 * @return
	 * @throws Exception
	 */
	public Document callCloseReceipt(YFSEnvironment env, Document outStartReceipt_XML, String Cache, String EnterpriseCode) throws Exception {
		logger.verbose("@@@@@ Entering NWCGPerformIncidentReturn::callCloseReceipt @@@@@");
		Document outDoc = null, outDocVerification = null;
		Document rt_Receipt = XMLUtil.newDocument();
		Element el_Receipt = rt_Receipt.createElement("Receipt");
		rt_Receipt.appendChild(el_Receipt);
		el_Receipt.setAttribute("DocumentType", NWCGConstants.NWCG_RETURN_BLIND_RECEIPT_DOCUMENT);
		el_Receipt.setAttribute("ReceiptHeaderKey", "");
		el_Receipt.setAttribute("ReceiptNo", outStartReceipt_XML.getDocumentElement().getAttribute("ReceiptNo"));
		el_Receipt.setAttribute("ReceivingNode", Cache);
		el_Receipt.setAttribute("ShipmentKey", outStartReceipt_XML.getDocumentElement().getAttribute("ShipmentKey"));
		Element el_Shipment = rt_Receipt.createElement("Shipment");
		el_Receipt.appendChild(el_Shipment);
		el_Shipment.setAttribute("DocumentType", NWCGConstants.NWCG_RETURN_BLIND_RECEIPT_DOCUMENT);
		el_Shipment.setAttribute("EnterpriseCode", EnterpriseCode);
		el_Shipment.setAttribute("OrderHeaderKey", "");
		el_Shipment.setAttribute("OrderNo", "");
		el_Shipment.setAttribute("OrderReleaseKey", "");
		el_Shipment.setAttribute("ReleaseNo", "");
		el_Shipment.setAttribute("SellerOrganizationCode", "");
		el_Shipment.setAttribute("ShipNode", "");
		//ShipmentKey should be enough
		el_Shipment.setAttribute("ShipmentNo", "");
		CommonUtilities.invokeAPI(env, "closeReceipt", rt_Receipt);
		logger.verbose("@@@@@ Exiting NWCGPerformIncidentReturn::callCloseReceipt @@@@@");
		return outDoc;
	}

	/**
	 * 
	 * @param inputdate
	 * @return
	 */
	public String getDate(String inputdate) {
		String inputDate = inputdate;
		StringBuffer input = new StringBuffer();
		int first = inputDate.indexOf("/");
		int second = inputDate.indexOf("/", first);
		int length = inputDate.length();
		//replacing the code with YFCDATE
		//com.yantra.yfc.util.YFCDate dateObj = new com.yantra.yfc.util.YFCDate(inputdate,"mm/dd/yyyy",true);
		//String resutlDate = 	dateObj.getString(NWCGConstants.YANTRA_DATE_FORMAT);
		String month = inputDate.substring(0, first);
		String date = inputDate.substring(3, 5);
		String year = inputDate.substring(6, 10);
		logger.verbose("@@@@@ month:-" + month + " date:-" + date + " year:-" + year);
		input.append(year).append("-").append(month).append("-").append(date);
		logger.verbose("@@@@@ Final date:-" + input.toString());
		return input.toString();
	}

	/**
	 * use this to get the tag attributes for the components
	 * 
	 * @param env
	 * @param ItemID
	 * @param OrgCode
	 * @param UnitOfMeasure
	 * @return
	 * @throws Exception
	 */
	public HashMap getTagAttributes(YFSEnvironment env, String ItemID, String OrgCode, String UnitOfMeasure) throws Exception {
		HashMap CompAttributes = new HashMap();
		Document ItemDetailsDoc_out = null;
		Document ItemDetails_out_temp = null;
		// Preparing the input xml for the getItemDetails API
		Document getItemDetails_IN = XMLUtil.newDocument();
		Element el_Item = getItemDetails_IN.createElement("Item");
		getItemDetails_IN.appendChild(el_Item);
		el_Item.setAttribute("ItemID", ItemID);
		el_Item.setAttribute("ItemKey", "");
		el_Item.setAttribute("OrganizationCode", OrgCode);
		el_Item.setAttribute("UnitOfMeasure", UnitOfMeasure);
		// Prepare output template to get the list of component
		ItemDetails_out_temp = XMLUtil.newDocument();
		Element el_Item2 = ItemDetails_out_temp.createElement("Item");
		ItemDetails_out_temp.appendChild(el_Item2);
		Element el_Components2 = ItemDetails_out_temp.createElement("Components");
		el_Item2.appendChild(el_Components2);
		Element el_Component2 = ItemDetails_out_temp.createElement("Component");
		el_Components2.appendChild(el_Component2);
		el_Component2.setAttribute("ComponentDescription", "");
		el_Component2.setAttribute("ComponentItemID", "");
		el_Component2.setAttribute("ComponentItemKey", "");
		el_Component2.setAttribute("ComponentOrganizationCode", "");
		el_Component2.setAttribute("ComponentUnitOfMeasure", "");
		el_Component2.setAttribute("ItemKey", "");
		el_Component2.setAttribute("KitQuantity", "");
		Element el_Component3 = ItemDetails_out_temp.createElement("InventoryTagAttributes");
		el_Item2.appendChild(el_Component3);
		// End Output template to get the list of component
		logger.verbose("@@@@@ The input xml for getCompoenentItemDetails is:-" + XMLUtil.getXMLString(getItemDetails_IN));
		env.setApiTemplate("getItemDetails", ItemDetails_out_temp);
		ItemDetailsDoc_out = CommonUtilities.invokeAPI(env, "getItemDetails", getItemDetails_IN);
		env.clearApiTemplate("ItemDetails_out_temp");
		logger.verbose("@@@@@ The output xml from getComponentItemDetails is:-" + XMLUtil.getXMLString(ItemDetailsDoc_out));
		NodeList InvTagList = ItemDetailsDoc_out.getDocumentElement().getElementsByTagName("InventoryTagAttributes");
		String BatchNo = "";
		String LotAttribute1 = "";
		String LotAttribute3 = "";
		String LotNumber = "";
		String RevisionNo = "";
		if (InvTagList.getLength() > 0) {
			Element InvTagEl = (Element) InvTagList.item(0);
			BatchNo = InvTagEl.getAttribute("BatchNo");
			LotAttribute1 = InvTagEl.getAttribute("LotAttribute1");
			LotAttribute3 = InvTagEl.getAttribute("LotAttribute3");
			LotNumber = InvTagEl.getAttribute("LotNumber");
			RevisionNo = InvTagEl.getAttribute("RevisionNo");
		}
		CompAttributes.put("BatchNo", BatchNo);
		CompAttributes.put("LotAttribute1", LotAttribute1);
		CompAttributes.put("LotAttribute3", LotAttribute3);
		CompAttributes.put("LotNumber", LotNumber);
		CompAttributes.put("RevisionNo", RevisionNo);
		return CompAttributes;
	}

	/**
	 * 
	 * @param env
	 * @param ItemID
	 * @param OrgCode
	 * @return
	 * @throws Exception
	 */
	public HashMap getIsSerialFlag(YFSEnvironment env, String ItemID, String OrgCode) throws Exception {
		Document ItemListDoc_out = null;
		Document IteList_Template = null;
		Document ItemListDoc_in = XMLUtil.newDocument();
		HashMap itemAttributes = new HashMap();
		Element el_Item2 = ItemListDoc_in.createElement("Item");
		ItemListDoc_in.appendChild(el_Item2);
		el_Item2.setAttribute("CallingOrganizationCode", OrgCode);
		el_Item2.setAttribute("ItemID", ItemID);
		IteList_Template = XMLUtil.newDocument();
		Element el_ItemList = IteList_Template.createElement("ItemList");
		IteList_Template.appendChild(el_ItemList);
		Element el_Item = IteList_Template.createElement("Item");
		el_ItemList.appendChild(el_Item);
		el_Item.setAttribute("ItemID", "");
		el_Item.setAttribute("ItemKey", "");
		el_Item.setAttribute("OrganizationCode", "");
		el_Item.setAttribute("UnitOfMeasure", "");
		Element el_PrimaryInformation = IteList_Template.createElement("PrimaryInformation");
		el_Item.appendChild(el_PrimaryInformation);
		el_PrimaryInformation.setAttribute("DefaultProductClass", "");
		el_PrimaryInformation.setAttribute("NumSecondarySerials", "");
		el_PrimaryInformation.setAttribute("ShortDescription", "");
		Element el_InventoryParameters = IteList_Template.createElement("InventoryParameters");
		el_Item.appendChild(el_InventoryParameters);
		el_InventoryParameters.setAttribute("IsSerialTracked", "");
		el_InventoryParameters.setAttribute("TagControlFlag", "");
		el_InventoryParameters.setAttribute("TimeSensitive", "");
		logger.verbose("@@@@@ The \"IteList_Template\" for getItemList API is:-" + XMLUtil.getXMLString(IteList_Template));
		env.setApiTemplate("getItemList", IteList_Template);
		logger.verbose("@@@@@ The input xml for getItemList2 is:-" + XMLUtil.getXMLString(ItemListDoc_in));
		ItemListDoc_out = CommonUtilities.invokeAPI(env, "getItemList", ItemListDoc_in);
		env.clearApiTemplate("getItemList");
		NodeList ItemNodes = ItemListDoc_out.getDocumentElement().getElementsByTagName("Item");
		Element ItemEl = (Element) ItemNodes.item(0);
		itemAttributes.put("UnitOfMeasure", ItemEl.getAttribute("UnitOfMeasure"));
		NodeList PrimaryInformation = ItemListDoc_out.getDocumentElement().getElementsByTagName("PrimaryInformation");
		Element PrimaryInformationEL = (Element) PrimaryInformation.item(0);
		itemAttributes.put("DefaultProductClass", PrimaryInformationEL.getAttribute("DefaultProductClass"));
		itemAttributes.put("ShortDescription", PrimaryInformationEL.getAttribute("ShortDescription"));
		itemAttributes.put("NumSecondarySerials", PrimaryInformationEL.getAttribute("NumSecondarySerials"));
		NodeList InventoryParameters = ItemListDoc_out.getDocumentElement().getElementsByTagName("InventoryParameters");
		Element InventoryParametersEl = (Element) InventoryParameters.item(0);
		itemAttributes.put("IsSerialTracked", InventoryParametersEl.getAttribute("IsSerialTracked"));
		itemAttributes.put("TagControlFlag", InventoryParametersEl.getAttribute("TagControlFlag"));
		itemAttributes.put("TimeSensitive", InventoryParametersEl.getAttribute("TimeSensitive"));
		return itemAttributes;
	}

	/**
	 * 
	 * @param env
	 * @param inToAPI_XML
	 * @param outCreateReceipt
	 * @param outConfirmShipment
	 * @param Cache
	 * @param ReceivingLoc
	 * @param EnterpriseCode
	 * @return
	 * @throws Exception
	 */
	public Document callReceiveOrder(YFSEnvironment env, Document inToAPI_XML, Document outCreateReceipt, Document outConfirmShipment, String Cache, String ReceivingLoc, String EnterpriseCode) throws Exception {
		logger.verbose("@@@@@ Entering NWCGPerformIncidentReturn::callReceiveOrder @@@@@");
		Document outDoc = null, outDocVerification = null;
		String ReceiptNo = outCreateReceipt.getDocumentElement().getAttribute("ReceiptNo");
		String UserId = inToAPI_XML.getDocumentElement().getAttribute("UserId");
		// BEGIN - Production Issue - CR 928 - May 29, 2013 - make sure not to return a null AIT return outDoc
		outDoc = performAITReturns(env, Cache, ReceiptNo, ReceivingLoc, EnterpriseCode, outCreateReceipt, outConfirmShipment);
		if(outDoc == null) {
			outDoc = outDocVerification;
		}
		outDocVerification = outDoc;
		// END - Production Issue - CR 928 - May 29, 2013
		Document rt_Receipt = XMLUtil.newDocument();
		NodeList ReturnLines = inToAPI_XML.getDocumentElement().getElementsByTagName("ReceiptLine");
		int ReturnLineCount = ReturnLines.getLength();
		for (int rcnt = 0; rcnt < ReturnLineCount; rcnt++) {
			Element ReturnLineEl = (Element) ReturnLines.item(rcnt);
			if (!ReturnLineEl.hasAttribute("LPNNo")) {
				int SerialCount = 0;
				int CompCount = 0;
				NodeList ComponentList = ReturnLineEl.getElementsByTagName("Component");
				CompCount = ComponentList.getLength();
				NodeList SerialList = ReturnLineEl.getElementsByTagName("SerialInfoMainItem");
				SerialCount = SerialList.getLength();
				// Receiving Only as Components for Serially Trackable KIT Items
				if ((CompCount > 0) && (SerialCount > 0)) {
					SerialCount = 0; 
				}
				// BEGIN - Production Issue - CR 928 - May 29, 2013 - Item Received as Components
				if (CompCount > 0) {
					outDoc = ProcessComponents(env, ReturnLineEl, outCreateReceipt, outConfirmShipment, Cache, ReceivingLoc, EnterpriseCode, UserId);
					if(outDoc == null) {
						outDoc = outDocVerification;
					}
					outDocVerification = outDoc;
					continue;
				}
				// Item is Serially Tracked
				if (SerialCount > 0) {
					outDoc = ProcessSerials(env, ReturnLineEl, outCreateReceipt, outConfirmShipment, Cache, ReceivingLoc, EnterpriseCode, UserId);
					if(outDoc == null) {
						outDoc = outDocVerification;
					}
					outDocVerification = outDoc;
					continue;
				}
				// Item with No Components and No Serial Info
				outDoc = ProcessItems(env, ReturnLineEl, outCreateReceipt, outConfirmShipment, Cache, ReceivingLoc, EnterpriseCode, UserId);
				// REASON: Some Users put a BLANK line (or n number of blank lines) in the return. These will cause outDoc to be NULL. 
				// CONCLUSION: NEVER EVER, for any REASON, EVER set this NULL Value. Store the last known VALID value and use it to OVERWRITE the NULL value.
				// NOTE: They only ever set the last value anyway, they are not trying to set all of Receipt Lines in this outDoc. Furthermore, for the new JB2 code, we only need ReceiptHeaderKey which is common to all valid outDoc documents returned for all valid return lines. 
				if(outDoc == null) {
					outDoc = outDocVerification;
				}
				outDocVerification = outDoc;
				// END - Production Issue - CR 928 - May 29, 2013
			}
		}
		NodeList lsReturnLines = rt_Return.getElementsByTagName("ReturnLine");
		if (lsReturnLines.getLength() > 0) {
			CommonUtilities.invokeService(env, NWCGConstants.NWCG_UPDATE_REC_ON_RECEIPT_SERVICE, rt_Return);
		}
		logger.verbose("@@@@@ Exiting NWCGPerformIncidentReturn::callReceiveOrder @@@@@");
		return outDoc;
	}

	/**
	 * 
	 * @param env
	 * @param ReturnLineEl
	 * @param outCreateReceipt
	 * @param outConfirmShipment
	 * @param Cache
	 * @param ReceivingLoc
	 * @param EnterpriseCode
	 * @param UserId
	 * @return
	 * @throws Exception
	 */
	public Document ProcessItems(YFSEnvironment env, Element ReturnLineEl, Document outCreateReceipt, Document outConfirmShipment, String Cache, String ReceivingLoc, String EnterpriseCode, String UserId) throws Exception {		
		int RFI = 0;
		int NRFI = 0;
		int UnsRet = 0;
		int UnsRetNWT = 0;
		int QuantityRFI = 0;
		int QuantityNRFI = 0;
		int QuantityUnsRet = 0;
		int QuantityUnsNwtReturn = 0;
		int QuantityReturned = 0;
		Document rt_Receipt = null;
		Document doc_MovReq = null;
		Document doc_MovReq2 = null;
		Document outDoc = null, outDocVerification = null;
		Document outMovDoc = null;
		if ((ReturnLineEl.getAttribute("QtyReturned") != null) && !(ReturnLineEl.getAttribute("QtyReturned").equals(""))) {
			QuantityReturned = Integer.parseInt(ReturnLineEl.getAttribute("QtyReturned"));
		}
		if ((ReturnLineEl.getAttribute("RFI") != null) && !(ReturnLineEl.getAttribute("RFI").equals(""))) {
			QuantityRFI = Integer.parseInt(ReturnLineEl.getAttribute("RFI"));
		}
		if ((ReturnLineEl.getAttribute("NRFI") != null) && !(ReturnLineEl.getAttribute("NRFI").equals(""))) {
			QuantityNRFI = Integer.parseInt(ReturnLineEl.getAttribute("NRFI"));
		}
		if ((ReturnLineEl.getAttribute("UnsRet") != null) && !(ReturnLineEl.getAttribute("UnsRet").equals(""))) {
			QuantityUnsRet = Integer.parseInt(ReturnLineEl.getAttribute("UnsRet"));
		}
		if ((ReturnLineEl.getAttribute("UnsRetNWT") != null) && !(ReturnLineEl.getAttribute("UnsRetNWT").equals(""))) {
			QuantityUnsNwtReturn = Integer.parseInt(ReturnLineEl.getAttribute("UnsRetNWT"));
		}
		String ReceiptNo = outCreateReceipt.getDocumentElement().getAttribute("ReceiptNo");
		Element TimeSensitiveEl = null;
		TimeSensitiveEl = (Element) ReturnLineEl.getElementsByTagName("TimeExpiryMainItem").item(0);
		Element TagAttribEl = null;
		TagAttribEl = (Element) ReturnLineEl.getElementsByTagName("TagAttributesMainItem").item(0);
		HashMap itemAttr = new HashMap();
		if (TagAttribEl != null) {
			itemAttr = setTagAttributes(env, itemAttr, TagAttribEl);
		} else {
			itemAttr.put("LotAttribute1", "");
			itemAttr.put("LotAttribute2", "");
			itemAttr.put("LotAttribute3", "");
			itemAttr.put("LotNumber", "");
			itemAttr.put("RevisionNo", "");
			itemAttr.put("BatchNo", "");
		}
		itemAttr.put("PrimarySerialNo", "");
		itemAttr.put("DispositionCode", "");
		itemAttr.put("SecondarySerialNo", "");
		String ItemId = ReturnLineEl.getAttribute("ItemID");
		itemAttr.put("ItemID", ReturnLineEl.getAttribute("ItemID"));
		itemAttr.put("QtyReturned", ReturnLineEl.getAttribute("QtyReturned"));
		if (ReturnLineEl.getAttribute("ProductClass") != null && !(ReturnLineEl.getAttribute("ProductClass").equals(""))) {
			itemAttr.put("ProductClass", ReturnLineEl.getAttribute("ProductClass"));
		} else {
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGPerformIncidentReturn' " + "DetailDescription='Error Generated while Performing Return[ReceiptNo :- " + ReceiptNo + "] for the ReceiptLine having itemID= " + ItemId + " as The " + "ProductClass is null or is not entered'");
			throwAlert(env, stbuf);
		}
		if (ReturnLineEl.getAttribute("UOM") != null && !(ReturnLineEl.getAttribute("UOM").equals(""))) {
			itemAttr.put("UnitOfMeasure", ReturnLineEl.getAttribute("UOM"));
		} else {
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGPerformIncidentReturn' " + "DetailDescription='Error Generated while Performing Return[ReceiptNo :- " + ReceiptNo + "] for the ReceiptLine having itemID= " + ItemId + " as The UOM is null or is not entered'");
			throwAlert(env, stbuf);
		}
		if (TimeSensitiveEl != null) {
			itemAttr.put("ExpiryDate", TimeSensitiveEl.getAttribute("ExpiryDate"));
		} else {
			itemAttr.put("ExpiryDate", "");
		}
		for (int cntr1 = 0; cntr1 < 4; cntr1++) {
			if (cntr1 == 0) {
				RFI = QuantityRFI;
				NRFI = UnsRet = UnsRetNWT = 0;
				if (RFI == 0)
					continue;
			}
			if (cntr1 == 1) {
				RFI = 0;
				NRFI = QuantityNRFI;
				UnsRet = UnsRetNWT = 0;
				if (NRFI == 0)
					continue;
			}
			if (cntr1 == 2) {
				RFI = 0;
				NRFI = 0;
				UnsRet = QuantityUnsRet;
				UnsRetNWT = 0;
				if (UnsRet == 0)
					continue;
			}
			if (cntr1 == 3) {
				RFI = 0;
				NRFI = 0;
				UnsRet = 0;
				UnsRetNWT = QuantityUnsNwtReturn;
				if (UnsRetNWT == 0)
					continue;
			}
			String LineNotes = ReturnLineEl.getAttribute("LineNotes");
			String RecdAsComp = ReturnLineEl.getAttribute("RecdAsComp");
			// if not a value quantity then simply skip this, AKA a blank line with blank quantities where the outDoc is never set. 
			if (RFI != 0 || NRFI != 0 || UnsRet != 0 || UnsRetNWT != 0) {
				rt_Receipt = createReceiveOrderDocument(env, itemAttr, Cache, ReceivingLoc, "NWCG", outCreateReceipt, outConfirmShipment, RFI, NRFI, UnsRet, UnsRetNWT, LineNotes, RecdAsComp);
				outDoc = CommonUtilities.invokeAPI(env, "receiveOrder", rt_Receipt);
				if(outDoc == null) {
					outDoc = outDocVerification;
				}
				outDocVerification = outDoc;
				doc_MovReq = createMoveLocationInvDocument(env, itemAttr, UserId, Cache, ReceivingLoc, "NWCG", outCreateReceipt, outConfirmShipment, RFI, NRFI, UnsRet, UnsRetNWT, LineNotes, RecdAsComp);
				doc_MovReq2 = createMoveRequestDocument(env, itemAttr, UserId, Cache, ReceivingLoc, "NWCG", outCreateReceipt, outConfirmShipment, RFI, NRFI, UnsRet, UnsRetNWT, LineNotes, RecdAsComp);
				outMovDoc = ProcessMoveRequest(env, doc_MovReq, doc_MovReq2, UserId, "");
				rt_Return = createIncidentUpdateDocument(env, rt_Return, rt_Receipt, "False");
			}
			if(outDoc == null) {
				outDoc = outDocVerification;
			}
			outDocVerification = outDoc;
		}
		return outDoc;
	}

	/**
	 * 
	 * @param env
	 * @param ReturnLineEl
	 * @param outCreateReceipt
	 * @param outConfirmShipment
	 * @param Cache
	 * @param ReceivingLoc
	 * @param EnterpriseCode
	 * @param UserId
	 * @return
	 * @throws Exception
	 */
	public Document ProcessComponents(YFSEnvironment env, Element ReturnLineEl, Document outCreateReceipt, Document outConfirmShipment, String Cache, String ReceivingLoc, String EnterpriseCode, String UserId) throws Exception {
		Document rt_Receipt = null;
		Document doc_MovReq = null;
		Document doc_MovReq2 = null;
		Document outMovDoc = null;
		Document outDoc = null, outDocVerification = null;
		NodeList ComponentList = ReturnLineEl.getElementsByTagName("Component");
		int CompCount = ComponentList.getLength();
		String RecdAsComp = ReturnLineEl.getAttribute("RecdAsComp");
		for (int compcnt = 0; compcnt < CompCount; compcnt++) {
			Element CompEl = (Element) ComponentList.item(compcnt);
			NodeList SerialList = CompEl.getElementsByTagName("Serial");
			int SerialCount = SerialList.getLength();
			if (SerialCount > 0) {
				outDoc = ProcessCompSerials(env, CompEl, outCreateReceipt, outConfirmShipment, Cache, ReceivingLoc, EnterpriseCode, UserId, RecdAsComp);
				if(outDoc == null) {
					outDoc = outDocVerification;
				}
				outDocVerification = outDoc;
				continue;
			}
			int RFI = 0;
			int NRFI = 0;
			int UnsRet = 0;
			int UnsRetNWT = 0;
			int QuantityReturned = 0;
			int QuantityRFI = 0;
			int QuantityNRFI = 0;
			int QuantityUnsRet = 0;
			int QuantityUnsNwtReturn = 0;
			rt_Receipt = null;
			if ((CompEl.getAttribute("QtyReturned") != null) && !(CompEl.getAttribute("QtyReturned").equals(""))) {
				QuantityReturned = Integer.parseInt(CompEl.getAttribute("QtyReturned"));
			}
			if (QuantityReturned == 0)
				continue;
			if ((CompEl.getAttribute("RFI") != null) && !(CompEl.getAttribute("RFI").equals(""))) {
				QuantityRFI = Integer.parseInt(CompEl.getAttribute("RFI"));
			}
			if ((CompEl.getAttribute("NRFI") != null) && !(CompEl.getAttribute("NRFI").equals(""))) {
				QuantityNRFI = Integer.parseInt(CompEl.getAttribute("NRFI"));
			}
			if ((CompEl.getAttribute("UnsRet") != null) && !(CompEl.getAttribute("UnsRet").equals(""))) {
				QuantityUnsRet = Integer.parseInt(CompEl.getAttribute("UnsRet"));
			}
			if ((CompEl.getAttribute("UnsRetNWT") != null) && !(CompEl.getAttribute("UnsRetNWT").equals(""))) {
				QuantityUnsNwtReturn = Integer.parseInt(CompEl.getAttribute("UnsRetNWT"));
			}
			String ReceiptNo = outCreateReceipt.getDocumentElement().getAttribute("ReceiptNo");
			Element TimeSensitiveEl = null;
			TimeSensitiveEl = (Element) CompEl.getElementsByTagName("SerialInfo").item(0);
			HashMap itemAttr = new HashMap();
			Element CompTagAttribEl = null;
			CompTagAttribEl = (Element) CompEl.getElementsByTagName("TagAttributes").item(0);
			if (CompTagAttribEl != null) {
				itemAttr = setTagAttributes(env, itemAttr, CompTagAttribEl);
			} else {
				itemAttr.put("LotAttribute1", "");
				itemAttr.put("LotAttribute2", "");
				itemAttr.put("LotAttribute3", "");
				itemAttr.put("LotNumber", "");
				itemAttr.put("RevisionNo", "");
				itemAttr.put("BatchNo", "");
			}
			itemAttr.put("PrimarySerialNo", "");
			itemAttr.put("DispositionCode", "");
			itemAttr.put("SecondarySerialNo", "");
			String ItemId = CompEl.getAttribute("ItemID");
			itemAttr.put("ItemID", CompEl.getAttribute("ItemID"));
			itemAttr.put("QtyReturned", CompEl.getAttribute("QtyReturned"));
			if (CompEl.getAttribute("ProductClass") != null && !(CompEl.getAttribute("ProductClass").equals(""))) {
				itemAttr.put("ProductClass", CompEl.getAttribute("ProductClass"));
			} else {
				StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGPerformIncidentReturn' " + "DetailDescription='Error Generated while Performing Return[ReceiptNo :- " + ReceiptNo + "] for the ReceiptLine having itemID= " + ItemId + " as The " + "ProductClass is null or is not entered'");
				throwAlert(env, stbuf);
			}
			if (CompEl.getAttribute("UOM") != null && !(CompEl.getAttribute("UOM").equals(""))) {
				itemAttr.put("UnitOfMeasure", CompEl.getAttribute("UOM"));
			} else {
				StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGPerformIncidentReturn' " + "DetailDescription='Error Generated while Performing Return[ReceiptNo :- " + ReceiptNo + "] for the ReceiptLine having itemID= " + ItemId + " as The UOM is null or is not entered'");
				throwAlert(env, stbuf);
			}
			if (TimeSensitiveEl != null) {
				itemAttr.put("ExpiryDate", TimeSensitiveEl.getAttribute("ExpiryDate"));
			} else {
				itemAttr.put("ExpiryDate", "");
			}
			String LineNotes = CompEl.getAttribute("LineNotes");
			for (int cntr1 = 0; cntr1 < 4; cntr1++) {
				if (cntr1 == 0) {
					RFI = QuantityRFI;
					NRFI = UnsRet = UnsRetNWT = 0;
					if (RFI == 0)
						continue;
				}
				if (cntr1 == 1) {
					RFI = 0;
					NRFI = QuantityNRFI;
					UnsRet = UnsRetNWT = 0;
					if (NRFI == 0)
						continue;
				}
				if (cntr1 == 2) {
					RFI = 0;
					NRFI = 0;
					UnsRet = QuantityUnsRet;
					UnsRetNWT = 0;
					if (UnsRet == 0)
						continue;
				}
				if (cntr1 == 3) {
					RFI = 0;
					NRFI = 0;
					UnsRet = 0;
					UnsRetNWT = QuantityUnsNwtReturn;
					if (UnsRetNWT == 0)
						continue;
				}
				if (RFI != 0 || NRFI != 0 || UnsRet != 0 || UnsRetNWT != 0) {
					rt_Receipt = createReceiveOrderDocument(env, itemAttr, Cache, ReceivingLoc, "NWCG", outCreateReceipt, outConfirmShipment, RFI, NRFI, UnsRet, UnsRetNWT, LineNotes, RecdAsComp);
					outDoc = CommonUtilities.invokeAPI(env, "receiveOrder", rt_Receipt);
					if(outDoc == null) {
						outDoc = outDocVerification;
					}
					outDocVerification = outDoc;
					doc_MovReq = createMoveLocationInvDocument(env, itemAttr, UserId, Cache, ReceivingLoc, "NWCG", outCreateReceipt, outConfirmShipment, RFI, NRFI, UnsRet, UnsRetNWT, LineNotes, RecdAsComp);
					doc_MovReq2 = createMoveRequestDocument(env, itemAttr, UserId, Cache, ReceivingLoc, "NWCG", outCreateReceipt, outConfirmShipment, RFI, NRFI, UnsRet, UnsRetNWT, LineNotes, RecdAsComp);
					outMovDoc = ProcessMoveRequest(env, doc_MovReq, doc_MovReq2, UserId, "");
					rt_Return = createIncidentUpdateDocument(env, rt_Return, rt_Receipt, "True");
				}
			}
			if(outDoc == null) {
				outDoc = outDocVerification;
			}
			outDocVerification = outDoc;
		}
		return outDoc;
	}

	/**
	 * 
	 * @param env
	 * @param ReturnLineEl
	 * @param outCreateReceipt
	 * @param outConfirmShipment
	 * @param Cache
	 * @param ReceivingLoc
	 * @param EnterpriseCode
	 * @param UserId
	 * @return
	 * @throws Exception
	 */
	public Document ProcessSerials(YFSEnvironment env, Element ReturnLineEl, Document outCreateReceipt, Document outConfirmShipment, String Cache, String ReceivingLoc, String EnterpriseCode, String UserId) throws Exception {
		Document rt_Receipt = null;
		Document doc_MovReq = null;
		Document doc_MovReq2 = null;
		Document outDoc = null, outDocVerification = null;
		Document outMovDoc = null;
		int QtyReturned = 0;
		String QtyReturnedStr = ReturnLineEl.getAttribute("QtyReturned");
		QtyReturned = Integer.parseInt(QtyReturnedStr);
		NodeList SerialList = ReturnLineEl.getElementsByTagName("SerialInfoMainItem");
		int SerialMainCount = SerialList.getLength();
		for (int scnt = 0; scnt < SerialMainCount; scnt++) {
			Element SerialMainEl = (Element) SerialList.item(scnt);
			rt_Receipt = null;
			String ReceiptNo = outCreateReceipt.getDocumentElement().getAttribute("ReceiptNo");
			Element TimeSensitiveEl = null;
			TimeSensitiveEl = (Element) ReturnLineEl.getElementsByTagName("TimeExpiryMainItem").item(0);
			HashMap itemAttr = new HashMap();
			String ItemId = ReturnLineEl.getAttribute("ItemID");
			itemAttr.put("ItemID", ReturnLineEl.getAttribute("ItemID"));
			itemAttr.put("QtyReturned", ReturnLineEl.getAttribute("QtyReturned"));
			if ((SerialMainEl.getAttribute("PrimarySerialNo") != null) && !(SerialMainEl.getAttribute("PrimarySerialNo").equals(""))) {
				itemAttr.put("PrimarySerialNo", SerialMainEl.getAttribute("PrimarySerialNo"));
			} else {
				StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGPerformIncidentReturn' " + "DetailDescription='Error Generated while Performing Return[ReceiptNo :- " + ReceiptNo + "] for the ReceiptLine having itemID= " + ItemId + " as The " + "PrimarySerial is null or is not entered'");
				throwAlert(env, stbuf);
			}
			if ((SerialMainEl.getAttribute("DispositionCode") != null) && !(SerialMainEl.getAttribute("DispositionCode").equals(""))) {
				itemAttr.put("DispositionCode", SerialMainEl.getAttribute("DispositionCode"));
			} else {
				StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGPerformIncidentReturn' " + "DetailDescription='Error Generated while Performing Return[ReceiptNo :- " + ReceiptNo + "] for the ReceiptLine having itemID= " + ItemId + " as The " + "DispositionCode is null or is not entered'");
				throwAlert(env, stbuf);
			}
			if (ReturnLineEl.getAttribute("ProductClass") != null && !(ReturnLineEl.getAttribute("ProductClass").equals(""))) {
				itemAttr.put("ProductClass", ReturnLineEl.getAttribute("ProductClass"));
			} else {
				StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGPerformIncidentReturn' " + "DetailDescription='Error Generated while Performing Return[ReceiptNo :- " + ReceiptNo + "] for the ReceiptLine having itemID= " + ItemId + " as The " + "ProductClass is null or is not entered'");
				throwAlert(env, stbuf);
			}
			if (ReturnLineEl.getAttribute("UOM") != null && !(ReturnLineEl.getAttribute("UOM").equals(""))) {
				itemAttr.put("UnitOfMeasure", ReturnLineEl.getAttribute("UOM"));
			} else {
				StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGPerformIncidentReturn' " + "DetailDescription='Error Generated while Performing Return[ReceiptNo :- " + ReceiptNo + "] for the ReceiptLine having itemID= " + ItemId + " as The UOM is null or is not entered'");
				throwAlert(env, stbuf);
			}
			if ((SerialMainEl.getAttribute("SecondarySerialNo") != null) && !(SerialMainEl.getAttribute("SecondarySerialNo").equals(""))) {
				itemAttr.put("SecondarySerialNo", SerialMainEl.getAttribute("SecondarySerialNo"));
			} else {
				itemAttr.put("SecondarySerialNo", "");
			}
			itemAttr = setTagAttributes(env, itemAttr, SerialMainEl);
			if (TimeSensitiveEl != null) {
				itemAttr.put("ExpiryDate", TimeSensitiveEl.getAttribute("ExpiryDate"));
			} else {
				itemAttr.put("ExpiryDate", "");
			}
			String LineNotes = ReturnLineEl.getAttribute("LineNotes");
			String RecdAsComp = ReturnLineEl.getAttribute("RecdAsComp");
			String SerialNo = SerialMainEl.getAttribute("PrimarySerialNo");
			rt_Receipt = createReceiveOrderDocument(env, itemAttr, Cache, ReceivingLoc, "NWCG", outCreateReceipt, outConfirmShipment, 0, 0, 0, 0, LineNotes, RecdAsComp);
			outDoc = CommonUtilities.invokeAPI(env, "receiveOrder", rt_Receipt);
			if(outDoc == null) {
				outDoc = outDocVerification;
			}
			outDocVerification = outDoc;
			doc_MovReq = createMoveLocationInvDocument(env, itemAttr, UserId, Cache, ReceivingLoc, "NWCG", outCreateReceipt, outConfirmShipment, 0, 0, 0, 0, LineNotes, RecdAsComp);
			doc_MovReq2 = createMoveRequestDocument(env, itemAttr, UserId, Cache, ReceivingLoc, "NWCG", outCreateReceipt, outConfirmShipment, 0, 0, 0, 0, LineNotes, RecdAsComp);
			outMovDoc = ProcessMoveRequest(env, doc_MovReq, doc_MovReq2, UserId, SerialNo);
			rt_Return = createIncidentUpdateDocument(env, rt_Return, rt_Receipt, "False");
			if(outDoc == null) {
				outDoc = outDocVerification;
			}
			outDocVerification = outDoc;
		}
		return outDoc;
	}

	/**
	 * 
	 * @param env
	 * @param CompEl
	 * @param outCreateReceipt
	 * @param outConfirmShipment
	 * @param Cache
	 * @param ReceivingLoc
	 * @param EnterpriseCode
	 * @param UserId
	 * @param RecdAsComp
	 * @return
	 * @throws Exception
	 */
	public Document ProcessCompSerials(YFSEnvironment env, Element CompEl, Document outCreateReceipt, Document outConfirmShipment, String Cache, String ReceivingLoc, String EnterpriseCode, String UserId, String RecdAsComp) throws Exception {
		Document rt_Receipt = null;
		Document doc_MovReq = null;
		Document doc_MovReq2 = null;
		Document outDoc = null, outDocVerification = null;
		Document outMovDoc = null;
		int QtyReturned = 0;
		String QtyReturnedStr = CompEl.getAttribute("QtyReturned");
		QtyReturned = Integer.parseInt(QtyReturnedStr);
		NodeList SerialCompList = CompEl.getElementsByTagName("Serial");
		int SerialCompCount = SerialCompList.getLength();
		for (int cscnt = 0; cscnt < SerialCompCount; cscnt++) {
			Element SerialCompEl = (Element) SerialCompList.item(cscnt);
			rt_Receipt = null;
			String ReceiptNo = outCreateReceipt.getDocumentElement().getAttribute("ReceiptNo");
			Element TimeSensitiveEl = null;
			TimeSensitiveEl = (Element) CompEl.getElementsByTagName("SerialInfo").item(0);
			HashMap itemAttr = new HashMap();
			String ItemId = CompEl.getAttribute("ItemID");
			itemAttr.put("ItemID", CompEl.getAttribute("ItemID"));
			itemAttr.put("QtyReturned", CompEl.getAttribute("QtyReturned"));
			if ((SerialCompEl.getAttribute("serialID") != null) && !(SerialCompEl.getAttribute("serialID").equals(""))) {
				itemAttr.put("PrimarySerialNo", SerialCompEl.getAttribute("serialID"));
			} else {
				StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGPerformIncidentReturn' " + "DetailDescription='Error Generated while Performing Return[ReceiptNo :- " + ReceiptNo + "] for the ReceiptLine having itemID= " + ItemId + " as The " + "PrimarySerial is null or is not entered'");
				throwAlert(env, stbuf);
			}
			if ((SerialCompEl.getAttribute("DispositionCode") != null) && !(SerialCompEl.getAttribute("DispositionCode").equals(""))) {
				itemAttr.put("DispositionCode", SerialCompEl.getAttribute("DispositionCode"));
			} else {
				StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGPerformIncidentReturn' " + "DetailDescription='Error Generated while Performing Return[ReceiptNo :- " + ReceiptNo + "] for the ReceiptLine having itemID= " + ItemId + " as The " + "DispositionCode is null or is not entered'");
				throwAlert(env, stbuf);
			}
			if (CompEl.getAttribute("ProductClass") != null && !(CompEl.getAttribute("ProductClass").equals(""))) {
				itemAttr.put("ProductClass", CompEl.getAttribute("ProductClass"));
			} else {
				StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGPerformIncidentReturn' " + "DetailDescription='Error Generated while Performing Return[ReceiptNo :- " + ReceiptNo + "] for the ReceiptLine having itemID= " + ItemId + " as The " + "ProductClass is null or is not entered'");
				throwAlert(env, stbuf);
			}
			if (CompEl.getAttribute("UOM") != null && !(CompEl.getAttribute("UOM").equals(""))) {
				itemAttr.put("UnitOfMeasure", CompEl.getAttribute("UOM"));
			} else {
				StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGPerformIncidentReturn' " + "DetailDescription='Error Generated while Performing Return[ReceiptNo :- " + ReceiptNo + "] for the ReceiptLine having itemID= " + ItemId + " as The UOM is null or is not entered'");
				throwAlert(env, stbuf);
			}
			if ((SerialCompEl.getAttribute("SecondarySerialNo") != null) && !(SerialCompEl.getAttribute("SecondarySerialNo").equals(""))) {
				itemAttr.put("SecondarySerialNo", SerialCompEl.getAttribute("SecondarySerialNo"));
			} else {
				itemAttr.put("SecondarySerialNo", "");
			}
			itemAttr = setTagAttributes(env, itemAttr, SerialCompEl);
			if (TimeSensitiveEl != null) {
				itemAttr.put("ExpiryDate", TimeSensitiveEl.getAttribute("ExpiryDate"));
			} else {
				itemAttr.put("ExpiryDate", "");
			}
			String LineNotes = CompEl.getAttribute("LineNotes");
			String SerialNo = SerialCompEl.getAttribute("serialID");
			rt_Receipt = createReceiveOrderDocument(env, itemAttr, Cache, ReceivingLoc, "NWCG", outCreateReceipt, outConfirmShipment, 0, 0, 0, 0, LineNotes, RecdAsComp);
			outDoc = CommonUtilities.invokeAPI(env, "receiveOrder", rt_Receipt);
			if(outDoc == null) {
				outDoc = outDocVerification;
			}
			outDocVerification = outDoc;
			doc_MovReq = createMoveLocationInvDocument(env, itemAttr, UserId, Cache, ReceivingLoc, "NWCG", outCreateReceipt, outConfirmShipment, 0, 0, 0, 0, LineNotes, RecdAsComp);
			doc_MovReq2 = createMoveRequestDocument(env, itemAttr, UserId, Cache, ReceivingLoc, "NWCG", outCreateReceipt, outConfirmShipment, 0, 0, 0, 0, LineNotes, RecdAsComp);
			outMovDoc = ProcessMoveRequest(env, doc_MovReq, doc_MovReq2, UserId, SerialNo);
			rt_Return = createIncidentUpdateDocument(env, rt_Return, rt_Receipt, "True");
			if(outDoc == null) {
				outDoc = outDocVerification;
			}
			outDocVerification = outDoc;
		}
		return outDoc;
	}

	/**
	 * 
	 * @param env
	 * @param MovReq
	 * @param MovReq2
	 * @param UserId
	 * @param SerialNo
	 * @return
	 * @throws Exception
	 */
	public Document ProcessMoveRequest(YFSEnvironment env, Document MovReq, Document MovReq2, String UserId, String SerialNo) throws Exception {
		Document out_TaskComplete = null;
		Document out_MovDoc = null;
		Document in_MovDoc = null;
		Document in_TaskDoc = null;
		Document out_TaskDoc = null;
		out_MovDoc = CommonUtilities.invokeAPI(env, "moveLocationInventory", MovReq);
		if (MovReq2 != null && out_MovDoc == null) {
			Element MovElem = (Element) MovReq2.getDocumentElement().getElementsByTagName("MoveRequestLine").item(0);
			String TargetLocationId = MovElem.getAttribute("TargetLocationId");
			if ((RRPFlag.equals("NO")) || (TargetLocationId.equals("NRFI-1"))) {
				MovElem.setAttribute("SourceLocationId", TargetLocationId);
				MovElem.removeAttribute("TargetLocationId");
				MovReq2.getDocumentElement().setAttribute("Release", "Y");
				out_MovDoc = CommonUtilities.invokeAPI(env, "createMoveRequest", MovReq2);
			}
		}
		return out_TaskComplete;
	}

	/**
	 * 
	 * @param env
	 * @param iAttr
	 * @param TagEl
	 * @return
	 */
	public HashMap setTagAttributes(YFSEnvironment env, HashMap iAttr, Element TagEl) {
		if ((TagEl.getAttribute("TagAttribute1") != null) && !(TagEl.getAttribute("TagAttribute1").equals(""))) {
			iAttr.put("LotAttribute1", TagEl.getAttribute("TagAttribute1"));
		} else {
			iAttr.put("LotAttribute1", "");
		}
		if ((TagEl.getAttribute("TagAttribute2") != null) && !(TagEl.getAttribute("TagAttribute2").equals(""))) {
			iAttr.put("LotAttribute3", TagEl.getAttribute("TagAttribute2"));
		} else {
			iAttr.put("LotAttribute3", "");
		}
		if ((TagEl.getAttribute("TagAttribute3") != null) && !(TagEl.getAttribute("TagAttribute3").equals(""))) {
			iAttr.put("LotNumber", TagEl.getAttribute("TagAttribute3"));
		} else {
			iAttr.put("LotNumber", "");
		}
		if ((TagEl.getAttribute("TagAttribute4") != null) && !(TagEl.getAttribute("TagAttribute4").equals(""))) {
			iAttr.put("RevisionNo", TagEl.getAttribute("TagAttribute4"));
		} else {
			iAttr.put("RevisionNo", "");
		}
		if ((TagEl.getAttribute("TagAttribute5") != null) && !(TagEl.getAttribute("TagAttribute5").equals(""))) {
			iAttr.put("BatchNo", TagEl.getAttribute("TagAttribute5"));
		} else {
			iAttr.put("BatchNo", "");
		}
		if ((TagEl.getAttribute("TagAttribute6") != null) && !(TagEl.getAttribute("TagAttribute6").equals(""))) {
			iAttr.put("LotAttribute2", TagEl.getAttribute("TagAttribute6"));
		} else {
			iAttr.put("LotAttribute2", "");
		}
		return iAttr;
	}

	/**
	 * 
	 * @param env
	 * @param IncUpdateDoc
	 * @param receiveOrderDoc
	 * @param IsComp
	 * @return
	 * @throws Exception
	 */
	public Document createIncidentUpdateDocument(YFSEnvironment env, Document IncUpdateDoc, Document receiveOrderDoc, String IsComp) throws Exception {
		Document Final = IncUpdateDoc;
		NodeList receiptLineList = receiveOrderDoc.getDocumentElement().getElementsByTagName("ReceiptLine");
		Element receiptLine = (Element) receiptLineList.item(0);
		NodeList el_ReturnLinesList = IncUpdateDoc.getDocumentElement().getElementsByTagName("ReturnLines");
		Element el_ReturnLines = (Element) el_ReturnLinesList.item(0);
		Element el_ReturnLine = rt_Return.createElement("ReturnLine");
		el_ReturnLines.appendChild(el_ReturnLine);
		if (receiptLine.getAttribute("BatchNo") != null && !(receiptLine.getAttribute("BatchNo").equals(""))) {
			el_ReturnLine.setAttribute("BatchNo", receiptLine.getAttribute("BatchNo"));
		} else {
			el_ReturnLine.setAttribute("BatchNo", "");
		}
		el_ReturnLine.setAttribute("IsComponent", IsComp);
		el_ReturnLine.setAttribute("ItemID", receiptLine.getAttribute("ItemID"));
		if (receiptLine.getAttribute("LotAttribute1") != null) {
			el_ReturnLine.setAttribute("LotAttribute1", receiptLine.getAttribute("LotAttribute1"));
		}
		if (receiptLine.getAttribute("LotAttribute2") != null) {
			el_ReturnLine.setAttribute("LotAttribute2", receiptLine.getAttribute("LotAttribute2"));
		} 
		if (receiptLine.getAttribute("LotNumber") != null)
			el_ReturnLine.setAttribute("LotNumber", "");
		if (receiptLine.getAttribute("LotAttribute3") != null)
			el_ReturnLine.setAttribute("LotAttribute3", "");
		if (receiptLine.getAttribute("RevisionNo") != null)
			el_ReturnLine.setAttribute("RevisionNo", "");
		if (receiptLine.getAttribute("LotNumber") != null)
			el_ReturnLine.setAttribute("LotNumber", "");
		if (receiptLine.getAttribute("ProductClass") != null && !(receiptLine.getAttribute("ProductClass").equals(""))) {
			el_ReturnLine.setAttribute("ProductClass", "Required");
		}
		if (receiptLine.getAttribute("DispositionCode").equals("NRFI")) {
			el_ReturnLine.setAttribute("QuantityNRFI", receiptLine.getAttribute("Quantity"));
		} else {
			el_ReturnLine.setAttribute("QuantityNRFI", "0");
		}
		if (receiptLine.getAttribute("DispositionCode").equals("RFI")) {
			el_ReturnLine.setAttribute("QuantityRFI", receiptLine.getAttribute("Quantity"));
		} else {
			el_ReturnLine.setAttribute("QuantityRFI", "0");
		}
		if (receiptLine.getAttribute("Quantity") != null) {
			el_ReturnLine.setAttribute("QuantityReturned", receiptLine.getAttribute("Quantity"));
		} else {
			el_ReturnLine.setAttribute("QuantityReturned", "0");
		}
		el_ReturnLine.setAttribute("QuantityShipped", "0");
		el_ReturnLine.setAttribute("ReceivedAsComponent", "N");
		if (receiptLine.getAttribute("DispositionCode").equals("UNSRV-NWT")) {
			el_ReturnLine.setAttribute("QuantityUnsNwtReturn", receiptLine.getAttribute("Quantity"));
		} else {
			el_ReturnLine.setAttribute("QuantityUnsNwtReturn", "0");
		}
		if (receiptLine.getAttribute("DispositionCode").equals("UNSERVICE")) {
			el_ReturnLine.setAttribute("QuantityUnsRet", receiptLine.getAttribute("Quantity"));
		} else {
			el_ReturnLine.setAttribute("QuantityUnsRet", "0");
		}
		if (receiptLine.getAttribute("SerialNo").length() > 0) {
			el_ReturnLine.setAttribute("TrackableID", receiptLine.getAttribute("SerialNo"));
		} else {
			el_ReturnLine.setAttribute("TrackableID", "");
		}
		if (receiptLine.getAttribute("UnitOfMeasure") != null)
			el_ReturnLine.setAttribute("UnitOfMeasure", receiptLine.getAttribute("UnitOfMeasure"));
		if (receiptLine.getAttribute("ProductClass") != null)
			el_ReturnLine.setAttribute("ProductClass", receiptLine.getAttribute("ProductClass"));
		el_ReturnLine.setAttribute("UnitPrice", "");
		return Final;
	}

	/**
	 * 
	 * @param env
	 * @param LocationId
	 * @param CacheId
	 * @return
	 * @throws Exception
	 */
	public String validateReceivingLocation(YFSEnvironment env, String LocationId, String CacheId) throws Exception {
		logger.verbose("@@@@@ Entering NWCGPerformIncidentReturn::validateReceivingLocation @@@@@");
		Document getLocation_In = XMLUtil.newDocument();
		Document getLocation_Out = null;
		String ErrorMessage = "";
		Element LocationEl = getLocation_In.createElement("Location");
		getLocation_In.appendChild(LocationEl);
		LocationEl.setAttribute("LocationId", LocationId);
		LocationEl.setAttribute("Node", CacheId);
		logger.verbose("@@@@@ The input xml for validateReceivingLocation[check if the Receiving Location is VALID] is:-" + XMLUtil.getXMLString(getLocation_In));
		getLocation_Out = CommonUtilities.invokeAPI(env, "getLocationList", getLocation_In);
		NodeList LocationList = getLocation_Out.getDocumentElement().getElementsByTagName("Location");
		int LocCnt = LocationList.getLength();
		if (LocCnt == 0) {
			ErrorMessage = "Invalid Receiving Dock \"" + LocationId + "\"";
		}
		
		logger.verbose("@@@@@ Exiting NWCGPerformIncidentReturn::validateReceivingLocation @@@@@");
		return ErrorMessage;
	}

	/**
	 * 
	 * @param env
	 * @param LocationId
	 * @param CacheId
	 * @return
	 * @throws Exception
	 */
	public String validateStagingLocation(YFSEnvironment env, String LocationId, String CacheId) throws Exception {
		logger.verbose("@@@@@ Entering NWCGPerformIncidentReturn::validateStagingLocation @@@@@");
		Document getLocation_In = XMLUtil.newDocument();
		Document getLocation_Out = null;
		String ErrorMessage = "";
		Element LocationEl = getLocation_In.createElement("Location");
		getLocation_In.appendChild(LocationEl);
		LocationEl.setAttribute("LocationId", LocationId);
		LocationEl.setAttribute("Node", CacheId);
		logger.verbose("@@@@@ The input xml for validateReceivingLocation[check if the Receiving Location is VALID] is:-" + XMLUtil.getXMLString(getLocation_In));
		getLocation_Out = CommonUtilities.invokeAPI(env, "getLocationList", getLocation_In);
		NodeList LocationList = getLocation_Out.getDocumentElement().getElementsByTagName("Location");
		int LocCnt = LocationList.getLength();
		if (LocCnt == 0) {
			if (RRPFlag.equals("YES")) {
				ErrorMessage = "Undefined Staging Location for RRP Returns Processing...,Please define Staging Locations (REFURB-RFI) for this Node !!!";
			} else {
				ErrorMessage = "Undefined Staging Locations...,Please define all Staging Locations (RFI-1,NRFI-1,UNS-1,UNSNWT-1) for this Node !!!";
			}
		}
		logger.verbose("@@@@@ Exiting NWCGPerformIncidentReturn::validateStagingLocation @@@@@");
		return ErrorMessage;
	}

	/**
	 * 
	 * @param env
	 * @param IssueNo
	 * @return
	 * @throws Exception
	 */
	public String validateIssueNo(YFSEnvironment env, String IssueNo) throws Exception {
		logger.verbose("@@@@@ Entering NWCGPerformIncidentReturn::validateIssueNo @@@@@");
		Document getIssue_In = XMLUtil.newDocument();
		Document getIssue_Out = null;
		Document getDocType = XMLUtil.newDocument();
		String ErrorMessage = "";
		Element IssueEl = getIssue_In.createElement("Order");
		getIssue_In.appendChild(IssueEl);
		IssueEl.setAttribute("OrderNo", IssueNo);
		IssueEl.setAttribute("EnterpriseCode", "NWCG");
		logger.verbose("@@@@@ The input xml for validateIssueNo[check if the Issue No is VALID] is:-" + XMLUtil.getXMLString(getIssue_In));
		getIssue_Out = CommonUtilities.invokeAPI(env, "getOrderList", getIssue_In);
		NodeList IssueList = getIssue_Out.getDocumentElement().getElementsByTagName("Order");
		int IssueCnt = IssueList.getLength();
		if (IssueCnt == 0) {
			ErrorMessage = "Invalid Issue No " + IssueNo;
		} else {
			String strOrderHeaderKey = ((Element) (IssueList.item(0))).getAttribute(NWCGConstants.ORDER_HEADER_KEY);
			Document inIssueDoc = XMLUtil.createDocument("OrderLine");
			Element eleInIssueDoc = inIssueDoc.getDocumentElement();
			eleInIssueDoc.setAttribute(NWCGConstants.ORDER_HEADER_KEY, strOrderHeaderKey);
			Document tempIssueDoc = XMLUtil.createDocument("OrderLineList");
			Element eleTempIssueDoc = tempIssueDoc.getDocumentElement();
			Element eleOrderLine = tempIssueDoc.createElement("OrderLine");
			eleTempIssueDoc.appendChild(eleOrderLine);
			Element eleItem = tempIssueDoc.createElement("Item");
			eleItem.setAttribute(NWCGConstants.ITEM_ID, "");
			eleOrderLine.appendChild(eleItem);
			Element eleLinePriceInfo = tempIssueDoc.createElement("LinePriceInfo");
			eleLinePriceInfo.setAttribute("UnitPrice", "");
			eleOrderLine.appendChild(eleLinePriceInfo);
			Document resDoc = CommonUtilities.invokeAPI(env, tempIssueDoc, "getOrderLineList", inIssueDoc);
			eleOrderLineList = resDoc.getDocumentElement();
		}
		logger.verbose("@@@@@ Exiting NWCGPerformIncidentReturn::validateIssueNo @@@@@");
		return ErrorMessage;
	}

	/**
	 * 
	 * @param env
	 * @param IncidentNo
	 * @param IncidentYear
	 * @return
	 * @throws Exception
	 */
	public String validateIncidentNo(YFSEnvironment env, String IncidentNo, String IncidentYear) throws Exception {
		Document getIncident_In = XMLUtil.newDocument();
		Document getIncident_Out = null;
		String ErrorMessage = "";
		Element IncidentEl = getIncident_In.createElement("NWCGIncidentOrder");
		getIncident_In.appendChild(IncidentEl);
		IncidentEl.setAttribute("IncidentNo", IncidentNo);
		IncidentEl.setAttribute("IncidentYear", IncidentYear);
		getIncident_Out = CommonUtilities.invokeService(env, NWCGConstants.NWCG_GET_INCIDENT_ORDERLIST_SERVICE, getIncident_In);
		NodeList IncidentList = getIncident_Out.getDocumentElement().getElementsByTagName("NWCGIncidentOrder");
		int IncidentCnt = IncidentList.getLength();
		if (IncidentCnt == 0) {
			ErrorMessage = "Invalid Incident No & Incident Year " + IncidentNo + " , " + IncidentYear;
		}
		return ErrorMessage;
	}

	/**
	 * 
	 * @param env
	 * @param inToAPI_XML
	 * @return
	 * @throws Exception
	 */
	public String ValidateReturnLines(YFSEnvironment env, Document inToAPI_XML) throws Exception {
		String ErrorMessage = "";
		NodeList ReturnLines = inToAPI_XML.getDocumentElement().getElementsByTagName("ReceiptLine");
		Element RootRtnElm = inToAPI_XML.getDocumentElement();
		String CacheID = RootRtnElm.getAttribute("CacheID");
		String RDock = RootRtnElm.getAttribute("ReceivingDock");
		int ReturnLineCount = ReturnLines.getLength();
		logger.verbose("@@@@@ The inxml has lines :" + ReturnLineCount);
		for (int rcnt = 0; rcnt < ReturnLineCount; rcnt++) {
			Element ReturnLineEl = (Element) ReturnLines.item(rcnt);
			int SerialCount = 0;
			int CompCount = 0;
			int TagCount = 0;
			String ItemId = ReturnLineEl.getAttribute("ItemID");
			NodeList ComponentList = ReturnLineEl.getElementsByTagName("Component");
			CompCount = ComponentList.getLength();
			NodeList SerialList = ReturnLineEl.getElementsByTagName("SerialInfoMainItem");
			SerialCount = SerialList.getLength();
			if ((CompCount > 0) && (SerialCount > 0)) {
				SerialCount = 0; 
			}
			if (CompCount > 0) {
				for (int compcnt = 0; compcnt < CompCount; compcnt++) {
					Element CompEl = (Element) ComponentList.item(compcnt);
					ItemId = CompEl.getAttribute("ItemID");
					NodeList CompSerialList = CompEl.getElementsByTagName("Serial");
					int CompSerialCount = CompSerialList.getLength();
					if (CompSerialCount > 0) {
						for (int cscnt = 0; cscnt < CompSerialCount; cscnt++) {
							Element SerialListElem = (Element) CompSerialList.item(cscnt);
							ErrorMessage = ValidateTagAttributes(env, SerialListElem, ItemId);
							if (ErrorMessage.length() > 0)
								return ErrorMessage;
							String DCode = SerialListElem.getAttribute("DispositionCode");
							if (DCode.equals("RFI") && RRPFlag.equals("NO"))
								ErrorMessage = validateLocationInventory(env, "RFI-1", CacheID, ItemId, "N");
							if (ErrorMessage.length() > 0)
								return ErrorMessage;
							if (DCode.equals("NRFI"))
								ErrorMessage = validateLocationInventory(env, "NRFI-1", CacheID, ItemId, "N");
							if (ErrorMessage.length() > 0)
								return ErrorMessage;
						}
						NodeList TCSElemList = CompEl.getElementsByTagName("SerialInfo");
						if (TCSElemList.getLength() > 0) {
							Element TCSElem = (Element) CompEl.getElementsByTagName("SerialInfo").item(0);
							if (TCSElem.hasAttribute("ExpiryDate")) {
								String ECSDate = TCSElem.getAttribute("ExpiryDate");
								if (ECSDate.length() == 0) {
									ErrorMessage = "Expiry Date Blank for Item ID : " + ItemId;
									return ErrorMessage;
								}
							}
						}
						ErrorMessage = validateLocationInventory(env, RDock, CacheID, ItemId, "Y");
						if (ErrorMessage.length() > 0)
							return ErrorMessage;
						continue;
					}
					NodeList CompTagList = CompEl.getElementsByTagName("TagAttributes");
					int CompTagCount = CompTagList.getLength();
					if (CompTagCount > 0) {
						for (int ctcnt = 0; ctcnt < CompTagCount; ctcnt++) {
							Element CompTagElem = (Element) CompTagList.item(ctcnt);
							ErrorMessage = ValidateTagAttributes(env, CompTagElem, ItemId);
							if (ErrorMessage.length() > 0)
								return ErrorMessage;
						}
					}
					NodeList TCElemList = CompEl.getElementsByTagName("SerialInfo");
					if (TCElemList.getLength() > 0) {
						Element TCElem = (Element) CompEl.getElementsByTagName("SerialInfo").item(0);
						if (TCElem.hasAttribute("ExpiryDate")) {
							String ECDate = TCElem.getAttribute("ExpiryDate");
							if (ECDate.length() == 0) {
								ErrorMessage = "Expiry Date Blank for Item ID : " + ItemId;
								return ErrorMessage;
							}
						}
					}
					ErrorMessage = validateLocationInventory(env, RDock, CacheID, ItemId, "Y");
					if (ErrorMessage.length() > 0)
						return ErrorMessage;
					if ((CompEl.getAttribute("RFI") != null) && !(CompEl.getAttribute("RFI").equals("")) && (RRPFlag.equals("NO"))) {
						ErrorMessage = validateLocationInventory(env, "RFI-1", CacheID, ItemId, "N");
					}
					if (ErrorMessage.length() > 0)
						return ErrorMessage;
					if ((CompEl.getAttribute("NRFI") != null) && !(CompEl.getAttribute("NRFI").equals(""))) {
						ErrorMessage = validateLocationInventory(env, "NRFI-1", CacheID, ItemId, "N");
					}
					if (ErrorMessage.length() > 0)
						return ErrorMessage;
					ErrorMessage = ValidateQtyReturned(env, CompEl, ItemId);
					if (ErrorMessage.length() > 0)
						return ErrorMessage;
				}
				continue;
			}
			if (SerialCount > 0) {
				NodeList SIElem = ReturnLineEl.getElementsByTagName("SerialInfoMainItem");
				int SICount = SIElem.getLength();
				for (int sicnt = 0; sicnt < SICount; sicnt++) {
					Element RLineElem = (Element) SIElem.item(sicnt);
					ErrorMessage = ValidateTagAttributes(env, RLineElem, ItemId);
					if (ErrorMessage.length() > 0)
						return ErrorMessage;
					String DCode = RLineElem.getAttribute("DispositionCode");
					if (DCode.equals("RFI") && RRPFlag.equals("NO"))
						ErrorMessage = validateLocationInventory(env, "RFI-1", CacheID, ItemId, "N");
					if (ErrorMessage.length() > 0)
						return ErrorMessage;
					if (DCode.equals("NRFI"))
						ErrorMessage = validateLocationInventory(env, "NRFI-1", CacheID, ItemId, "N");
					if (ErrorMessage.length() > 0)
						return ErrorMessage;
				}
				NodeList TSElemList = ReturnLineEl.getElementsByTagName("TimeExpiryMainItem");
				if (TSElemList.getLength() > 0) {
					Element TSElem = (Element) ReturnLineEl.getElementsByTagName("TimeExpiryMainItem").item(0);
					if (TSElem.hasAttribute("ExpiryDate")) {
						String ESDate = TSElem.getAttribute("ExpiryDate");
						if (ESDate.length() == 0) {
							ErrorMessage = "Expiry Date Blank for Item ID : " + ItemId;
							return ErrorMessage;
						}
					} else {
						ErrorMessage = "Expiry Date Blank for Item ID : " + ItemId;
						return ErrorMessage;
					}
				}
				ErrorMessage = validateLocationInventory(env, RDock, CacheID, ItemId, "Y");
				if (ErrorMessage.length() > 0)
					return ErrorMessage;
				continue;
			}
			//Item with No Components and No Serial Info
			NodeList TagList = ReturnLineEl.getElementsByTagName("TagAttributesMainItem");
			TagCount = TagList.getLength();
			if (TagCount > 0) {
				Element RLineElem = (Element) ReturnLineEl.getElementsByTagName("TagAttributesMainItem").item(0);
				ErrorMessage = ValidateTagAttributes(env, RLineElem, ItemId);
				if (ErrorMessage.length() > 0)
					return ErrorMessage;
			}
			NodeList TElemList = ReturnLineEl.getElementsByTagName("TimeExpiryMainItem");
			if (TElemList.getLength() > 0) {
				Element TElem = (Element) ReturnLineEl.getElementsByTagName("TimeExpiryMainItem").item(0);
				if (TElem.hasAttribute("ExpiryDate")) {
					String EDate = TElem.getAttribute("ExpiryDate");
					if (EDate.length() == 0) {
						ErrorMessage = "Expiry Date Blank for Item ID : " + ItemId;
						return ErrorMessage;
					}
				} else {
					ErrorMessage = "Expiry Date Blank for Item ID : " + ItemId;
					return ErrorMessage;
				}
			}
			ErrorMessage = validateLocationInventory(env, RDock, CacheID, ItemId, "Y");
			if (ErrorMessage.length() > 0)
				return ErrorMessage;
			if ((ReturnLineEl.getAttribute("RFI") != null) && !(ReturnLineEl.getAttribute("RFI").equals("")) && (RRPFlag.equals("NO"))) {
				ErrorMessage = validateLocationInventory(env, "RFI-1", CacheID, ItemId, "N");
			}
			if (ErrorMessage.length() > 0)
				return ErrorMessage;
			if ((ReturnLineEl.getAttribute("NRFI") != null) && !(ReturnLineEl.getAttribute("NRFI").equals(""))) {
				ErrorMessage = validateLocationInventory(env, "NRFI-1", CacheID, ItemId, "N");
			}
			if (ErrorMessage.length() > 0)
				return ErrorMessage;
			ErrorMessage = ValidateQtyReturned(env, ReturnLineEl, ItemId);
			if (ErrorMessage.length() > 0)
				return ErrorMessage;
		}
		return ErrorMessage;
	}

	/**
	 * Sample NodeInventory XML:
	 * 
	 * <NodeInventory LocationId="RETURN-1" Node="CORMK" >
	 * <Inventory>
	 * <InventoryItem ItemID="000256" /> 
	 * </Inventory>
	 * </NodeInventory>
	 * 
	 * @param env
	 * @param LocationId
	 * @param CacheId
	 * @param ItemId
	 * @param ReturnLocation
	 * @return
	 * @throws Exception
	 */
	public String validateLocationInventory(YFSEnvironment env, String LocationId, String CacheId, String ItemId, String ReturnLocation) throws Exception {
		logger.verbose("@@@@@ Entering NWCGPerformIncidentReturn::validateLocationInventory @@@@@");
		Document getNodeInv_In = XMLUtil.newDocument();
		Document getNodeInv_Out = null;
		String ErrorMessage = "";
		Element NodeEl = getNodeInv_In.createElement("NodeInventory");
		getNodeInv_In.appendChild(NodeEl);
		NodeEl.setAttribute("LocationId", LocationId);
		NodeEl.setAttribute("Node", CacheId);
		Element InvEl = getNodeInv_In.createElement("Inventory");
		NodeEl.appendChild(InvEl);
		Element InvItemEl = getNodeInv_In.createElement("InventoryItem");
		InvEl.appendChild(InvItemEl);
		InvItemEl.setAttribute("ItemID", ItemId);
		logger.verbose("@@@@@ The input xml for validateReceivingLocation[check if the Receiving Location is VALID] is:-" + XMLUtil.getXMLString(getNodeInv_In));
		getNodeInv_Out = CommonUtilities.invokeAPI(env, "getNodeInventory", getNodeInv_In);
		Element LocationInvListElm = (Element) getNodeInv_Out.getDocumentElement().getElementsByTagName("LocationInventoryList").item(0);
		NodeList LocationInvList = LocationInvListElm.getElementsByTagName("LocationInventory");
		int LocInvCnt = LocationInvList.getLength();
		if (LocInvCnt > 0) {
			double totalPendoutQuant = 0.00;
			double totalQuantity = 0.00;
			double Req_Qty = 0.00;
			double diff = 0.00;
			Element LocInvElm = (Element) LocationInvList.item(0);
			NodeList lsItemInventoryDetail = LocInvElm.getElementsByTagName("ItemInventoryDetail");
			for (int i = 0; i < lsItemInventoryDetail.getLength(); i++) {
				Element eleItemInventoryDetail = (Element) lsItemInventoryDetail.item(i);
				if (eleItemInventoryDetail.getAttribute("PalletId") != null && eleItemInventoryDetail.getAttribute("PalletId").equals("")) {
					String QtyStr = eleItemInventoryDetail.getAttribute("Quantity");
					String PendQtyStr = eleItemInventoryDetail.getAttribute("PendOutQty");
					double Quantity = Double.parseDouble(QtyStr);
					totalQuantity = totalQuantity + Quantity;
					double PendOutQty = 0.00;
					PendOutQty = Double.parseDouble(PendQtyStr);
					totalPendoutQuant = totalPendoutQuant + PendOutQty;
				}
			}
			if (ReturnLocation.equals("N")) {
				Req_Qty = getRequestQty(env, LocationId, CacheId, ItemId);
				diff = totalQuantity - Req_Qty;
			} else {
				diff = totalQuantity + totalPendoutQuant;
			}
			if (diff != 0) {
				//ErrorMessage = "\""+LocationId+"\" has Inventory for ItemId \""+ItemId+"\", Move the Inventory and then Continue"; location has inventory for item ID without a move task, create the move task and continue.
				ErrorMessage = LocationId + " has Inventory for ItemId " + ItemId + " without a move task, Create the move task and continue";
				logger.verbose("@@@@@ Exiting NWCGPerformIncidentReturn::validateLocationInventory (ErrorMessage) :: " + ErrorMessage);
				return ErrorMessage;
			}
		}
		logger.verbose("@@@@@ Exiting NWCGPerformIncidentReturn::validateLocationInventory @@@@@");
		return ErrorMessage;
	}

	/**
	 * Sample MoveRequest XML:
	 * 
	 * <MoveRequest Node="CORMK">
	 * <MoveRequestLines>
	 * <MoveRequestLine ItemId="007147" SourceLocationId="RFI-1" />
	 * </MoveRequestLines>
	 * </MoveRequest>
	 * 
	 * @param env
	 * @param LocationId
	 * @param CacheId
	 * @param ItemId
	 * @return
	 * @throws Exception
	 */
	public double getRequestQty(YFSEnvironment env, String LocationId, String CacheId, String ItemId) throws Exception {
		Document getMoveReq_In = XMLUtil.newDocument();
		Document getMoveReq_Out = null;
		String ReqQty_Str = "";
		Element MoveEl = getMoveReq_In.createElement("MoveRequest");
		getMoveReq_In.appendChild(MoveEl);
		MoveEl.setAttribute("Node", CacheId);
		MoveEl.setAttribute("FromActivityGroup", "RECEIPT");
		MoveEl.setAttribute("StatusQryType", "NE");
		MoveEl.setAttribute("Status", "CLOSED");
		Element ReqLinesEl = getMoveReq_In.createElement("MoveRequestLines");
		MoveEl.appendChild(ReqLinesEl);
		Element ReqLineEl = getMoveReq_In.createElement("MoveRequestLine");
		ReqLinesEl.appendChild(ReqLineEl);
		ReqLineEl.setAttribute("ItemId", ItemId);
		ReqLineEl.setAttribute("SourceLocationId", LocationId);
		getMoveReq_Out = CommonUtilities.invokeAPI(env, "getMoveRequestList", getMoveReq_In);
		NodeList MoveReq = getMoveReq_Out.getDocumentElement().getElementsByTagName("MoveRequest");
		int MoveCnt = MoveReq.getLength();
		double tot_qty = 0.00;
		if (MoveCnt > 0) {
			for (int mcnt = 0; mcnt < MoveCnt; mcnt++) {
				Element MElem = (Element) MoveReq.item(mcnt);
				Element MoveReqListElm = (Element) MElem.getElementsByTagName("MoveRequestLines").item(0);
				Element MoveReqElm = (Element) MoveReqListElm.getElementsByTagName("MoveRequestLine").item(0);
				ReqQty_Str = MoveReqElm.getAttribute("RequestQuantity");
				double rqty = Double.parseDouble(ReqQty_Str);
				tot_qty += rqty;
			}
		}
		return tot_qty;
	}

	/**
	 * 
	 * @param env
	 * @param RLineElem
	 * @param ItemId
	 * @return
	 * @throws Exception
	 */
	public String ValidateTagAttributes(YFSEnvironment env, Element RLineElem, String ItemId) throws Exception {
		String ErrorMessage = "";
		if (RLineElem.hasAttribute("PrimarySerialNo")) {
			String PSerial = RLineElem.getAttribute("PrimarySerialNo");
			if (PSerial.length() == 0) {
				ErrorMessage = "Trackable ID is Blank for Item ID : " + ItemId;
				return ErrorMessage;
			}
		}
		if (RLineElem.hasAttribute("serialID")) {
			String PSerial = RLineElem.getAttribute("serialID");
			if (PSerial.length() == 0) {
				ErrorMessage = "Trackable ID is Blank for Item ID : " + ItemId;
				return ErrorMessage;
			}
		}
		if (RLineElem.hasAttribute("SecondarySerialNo")) {
			String SSerial = RLineElem.getAttribute("SecondarySerialNo");
			if (SSerial.length() == 0) {
				ErrorMessage = "Secondary Serial No is Blank for Item ID : " + ItemId;
				return ErrorMessage;
			}
		}
		if (RLineElem.hasAttribute("TagAttribute1")) {
			String TAttr1 = RLineElem.getAttribute("TagAttribute1");
			if (TAttr1.length() == 0) {
				ErrorMessage = "Missing Attribute for Item ID : " + ItemId;
				return ErrorMessage;
			}
		}
		if (RLineElem.hasAttribute("TagAttribute2")) {
			String TAttr2 = RLineElem.getAttribute("TagAttribute2");
			if (TAttr2.length() == 0) {
				ErrorMessage = "Missing Attribute for Item ID : " + ItemId;
				return ErrorMessage;
			}
		}
		if (RLineElem.hasAttribute("TagAttribute3")) {
			String TAttr3 = RLineElem.getAttribute("TagAttribute3");
			if (TAttr3.length() == 0) {
				ErrorMessage = "Missing Attribute for Item ID : " + ItemId;
				return ErrorMessage;
			}
		}
		if (RLineElem.hasAttribute("TagAttribute4")) {
			String TAttr4 = RLineElem.getAttribute("TagAttribute4");
			if (TAttr4.length() == 0) {
				ErrorMessage = "Missing Attribute for Item ID : " + ItemId;
				return ErrorMessage;
			}
		}
		if (RLineElem.hasAttribute("TagAttribute5")) {
			String TAttr5 = RLineElem.getAttribute("TagAttribute5");
			if (TAttr5.length() == 0) {
				ErrorMessage = "Missing Attribute for Item ID : " + ItemId;
				return ErrorMessage;
			}
		}
		if (RLineElem.hasAttribute("TagAttribute6")) {
			String TAttr6 = RLineElem.getAttribute("TagAttribute6");
			if (TAttr6.length() == 0) {
				ErrorMessage = "Missing Attribute for Item ID : " + ItemId;
				return ErrorMessage;
			}
		}
		return ErrorMessage;
	}

	/**
	 * 
	 * @param env
	 * @param RLineElem
	 * @param ItemId
	 * @return
	 * @throws Exception
	 */
	public String ValidateQtyReturned(YFSEnvironment env, Element RLineElem, String ItemId) throws Exception {
		String ErrorMessage = "";
		int QuantityRFI = 0;
		int QuantityNRFI = 0;
		int QuantityUnsRet = 0;
		int QuantityUnsNwtReturn = 0;
		int QuantityReturned = 0;
		int TotQty = 0;
		if ((RLineElem.getAttribute("QtyReturned") != null) && !(RLineElem.getAttribute("QtyReturned").equals(""))) {
			QuantityReturned = Integer.parseInt(RLineElem.getAttribute("QtyReturned"));
		} else {
			QuantityReturned = 0;
		}
		if ((RLineElem.getAttribute("RFI") != null) && !(RLineElem.getAttribute("RFI").equals(""))) {
			QuantityRFI = Integer.parseInt(RLineElem.getAttribute("RFI"));
		} else {
			QuantityRFI = 0;
		}
		if ((RLineElem.getAttribute("NRFI") != null) && !(RLineElem.getAttribute("NRFI").equals(""))) {
			QuantityNRFI = Integer.parseInt(RLineElem.getAttribute("NRFI"));
		} else {
			QuantityNRFI = 0;
		}
		if ((RLineElem.getAttribute("UnsRet") != null) && !(RLineElem.getAttribute("UnsRet").equals(""))) {
			QuantityUnsRet = Integer.parseInt(RLineElem.getAttribute("UnsRet"));
		} else {
			QuantityUnsRet = 0;
		}
		if ((RLineElem.getAttribute("UnsRetNWT") != null) && !(RLineElem.getAttribute("UnsRetNWT").equals(""))) {
			QuantityUnsNwtReturn = Integer.parseInt(RLineElem.getAttribute("UnsRetNWT"));
		} else {
			QuantityUnsNwtReturn = 0;
		}
		TotQty = QuantityRFI + QuantityNRFI + QuantityUnsRet + QuantityUnsNwtReturn;
		if (QuantityReturned != TotQty) {
			ErrorMessage = "Mismatch in Total Quantity Returned for Item Id : " + ItemId;
		}
		return ErrorMessage;
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
		el_ComputePriceForItem.setAttribute("PriceProgramName", "NWCG_PRICE_PROGRAM");
		el_ComputePriceForItem.setAttribute("ProductClass", ProductClass);
		el_ComputePriceForItem.setAttribute("Quantity", "1");
		el_ComputePriceForItem.setAttribute("Uom", UOM);
		String UnitPrice = "";
		Document PriceDocument = CommonUtilities.invokeAPI(env, "computePriceForItem", rt_ComputePriceForItem);
		Element root_elem = PriceDocument.getDocumentElement();
		if (root_elem != null) {
			UnitPrice = root_elem.getAttribute("UnitPrice");
		}
		return UnitPrice;
	}

	/**
	 * 
	 * @param env
	 * @param ItemID
	 * @param SerialNo
	 * @return
	 * @throws Exception
	 */
	public String getIssuePrice(YFSEnvironment env, String ItemID, String SerialNo) throws Exception {
		Document inDoc = XMLUtil.newDocument();
		Document outDoc = XMLUtil.newDocument();
		Document outDocVerification = null;
		Document trackinDoc = XMLUtil.newDocument();
		Document trackoutDoc = XMLUtil.newDocument();
		String IssuePrice = "";
		String QtyShippedStr = "";
		String QtyReturnedStr = "";
		double QtyShipped = 0.00;
		double QtyReturned = 0.00;
		Element rtElem = rt_Return.getDocumentElement();
		String Cache = rtElem.getAttribute("CacheID");
		String IncidentNo = rtElem.getAttribute("IncidentNo");
		String IncidentYear = rtElem.getAttribute("IncidentYear");
		//BEGIN CR 675 - added issue no
		String IssueNo = rtElem.getAttribute("IssueNo");
		//END CR 675 - added issue no
		String lastissueprice = "";
		if (SerialNo.length() > 0) {
			//BEGIN CR 445
			Element el_NWCGTrackableItem = trackinDoc.createElement("NWCGTrackableItem");
			trackinDoc.appendChild(el_NWCGTrackableItem);
			el_NWCGTrackableItem.setAttribute("StatusIncidentNo", IncidentNo);
			el_NWCGTrackableItem.setAttribute("StatusIncidentYear", IncidentYear);
			el_NWCGTrackableItem.setAttribute("ItemID", ItemID);
			el_NWCGTrackableItem.setAttribute("SerialNo", SerialNo);
			el_NWCGTrackableItem.setAttribute("SecondarySerial", CommonUtilities.getSecondarySerial(env, SerialNo, ItemID));
			el_NWCGTrackableItem.setAttribute("SerialStatus", "I");
			trackoutDoc = CommonUtilities.invokeService(env, NWCGConstants.NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE, trackinDoc);
			String temp = "";
			NodeList listtrackout = trackoutDoc.getElementsByTagName("NWCGTrackableItem");
			for(int i = 0; i < listtrackout.getLength(); i++) {
				Node nodetrackout = listtrackout.item(i);
				Element trackoutelem1 = (Element)nodetrackout;
				temp = trackoutelem1.getAttribute("LastIssuePrice");
			}
			Element trackoutelem = null;
			trackoutelem = (Element) trackoutDoc.getDocumentElement().getElementsByTagName("NWCGTrackableItem").item(0);
			if (trackoutelem != null) {
				lastissueprice = trackoutelem.getAttribute("LastIssuePrice");
			}
		}
		if (lastissueprice.length() > 0) {
			return lastissueprice;
		}
		//END CR 445
		//BEGIN CR 675
		if (!IssueNo.equals("") && SerialNo.length() == 0) {
			NodeList lsOrderLine = eleOrderLineList.getElementsByTagName("OrderLine");
			if (lsOrderLine.getLength() > 0) {
				for (int i = 0; i < lsOrderLine.getLength(); i++) {
					Element eleOrderLineOut = (Element) lsOrderLine.item(i);
					String eleItemIDOut = ((Element) (eleOrderLineOut.getElementsByTagName("Item").item(0))).getAttribute("ItemID");
					if (eleItemIDOut.equals(ItemID)) {
						Element eleLinePriceInfoOut = (Element) (eleOrderLineOut.getElementsByTagName("LinePriceInfo").item(0));
						if (eleLinePriceInfoOut != null) {
							IssuePrice = eleLinePriceInfoOut.getAttribute("UnitPrice");
						}
						break;
					}
				}
			}
		} else {
			//END CR 675
			Element el_NWCGIncidentReturn = inDoc.createElement("NWCGIncidentReturn");
			inDoc.appendChild(el_NWCGIncidentReturn);
			el_NWCGIncidentReturn.setAttribute("CacheID", Cache);
			el_NWCGIncidentReturn.setAttribute("IncidentNo", IncidentNo);
			el_NWCGIncidentReturn.setAttribute("IncidentYear", IncidentYear);
			el_NWCGIncidentReturn.setAttribute("ItemID", ItemID);
			el_NWCGIncidentReturn.setAttribute("TrackableID", SerialNo);
			//This to make sure over receipt entries and received as components are not selected
			el_NWCGIncidentReturn.setAttribute("ReceivedAsComponent", "N");
			el_NWCGIncidentReturn.setAttribute("OverReceipt", "N");
			//Added this to make sure we get the first created record first
			Element el_OrderBy = inDoc.createElement("OrderBy");
			el_NWCGIncidentReturn.appendChild(el_OrderBy);
			Element el_Attribute = inDoc.createElement("Attribute");
			el_OrderBy.appendChild(el_Attribute);
			el_Attribute.setAttribute("Name", "Createts");
			outDoc = CommonUtilities.invokeService(env, NWCGConstants.NWCG_QUERY_LIST_ON_INCIDENT_TABLE_SERVICE, inDoc);
			if(outDoc == null) {
				outDoc = outDocVerification;
			}
			outDocVerification = outDoc;
			NodeList NWCGRetLines = outDoc.getDocumentElement().getElementsByTagName("NWCGIncidentReturn");
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
		}
		return IssuePrice;
	}

	/**
	 * 
	 * @param env
	 * @param Ret_XML
	 * @return
	 * @throws Exception
	 */
	public Document processinXML(YFSEnvironment env, Document Ret_XML) throws Exception {
		NodeList ReturnLines = Ret_XML.getDocumentElement().getElementsByTagName("ReceiptLine");
		Element RootRtnElm = Ret_XML.getDocumentElement();
		int ReturnLineCount = ReturnLines.getLength();
		for (int rcnt = 0; rcnt < ReturnLineCount; rcnt++) {
			Element ReturnLineEl = (Element) ReturnLines.item(rcnt);
			int SerialCount = 0;
			int CompSerialCount = 0;
			int CompCount = 0;
			int TagCount = 0;
			String ItemId = ReturnLineEl.getAttribute("ItemID");
			NodeList ComponentList = ReturnLineEl.getElementsByTagName("Component");
			CompCount = ComponentList.getLength();
			NodeList SerialList = ReturnLineEl.getElementsByTagName("SerialInfoMainItem");
			SerialCount = SerialList.getLength();
			int RFI_cnt = 0;
			int NRFI_cnt = 0;
			int UNS_cnt = 0;
			int UNSNWT_cnt = 0;
			for (int scnt = 0; scnt < SerialCount; scnt++) {
				Element SerialEl = (Element) SerialList.item(scnt);
				String DispositionCode = SerialEl.getAttribute("DispositionCode");
				if (DispositionCode.equals("RFI"))
					RFI_cnt++;
				if (DispositionCode.equals("NRFI"))
					NRFI_cnt++;
				if (DispositionCode.equals("UnsRet"))
					UNS_cnt++;
				if (DispositionCode.equals("UnsRetNWT"))
					UNSNWT_cnt++;
			}
			if (SerialCount > 0) {
				ReturnLineEl.setAttribute("QtyReturned", Integer.toString(SerialCount));
				ReturnLineEl.setAttribute("RFI", Integer.toString(RFI_cnt));
				ReturnLineEl.setAttribute("NRFI", Integer.toString(NRFI_cnt));
				ReturnLineEl.setAttribute("UnsRet", Integer.toString(UNS_cnt));
				ReturnLineEl.setAttribute("UnsRetNWT", Integer.toString(UNSNWT_cnt));
			}
			for (int comp_cnt = 0; comp_cnt < CompCount; comp_cnt++) {
				Element CompEl = (Element) ComponentList.item(comp_cnt);
				NodeList CompSerialList = CompEl.getElementsByTagName("SerialInfo");
				CompSerialCount = CompSerialList.getLength();
				int CRFI_cnt = 0;
				int CNRFI_cnt = 0;
				int CUNS_cnt = 0;
				int CUNSNWT_cnt = 0;
				for (int cscnt = 0; cscnt < CompSerialCount; cscnt++) {
					Element CompSerialEl = (Element) CompSerialList.item(cscnt);
					String CompDispositionCode = CompSerialEl.getAttribute("DispositionCode");
					if (CompDispositionCode.equals("RFI"))
						CRFI_cnt++;
					if (CompDispositionCode.equals("NRFI"))
						CNRFI_cnt++;
					if (CompDispositionCode.equals("UnsRet"))
						CUNS_cnt++;
					if (CompDispositionCode.equals("UnsRetNWT"))
						CUNSNWT_cnt++;
				}
				if (CompSerialCount > 0) {
					CompEl.setAttribute("QtyReturned", Integer.toString(CompSerialCount));
					CompEl.setAttribute("RFI", Integer.toString(CRFI_cnt));
					CompEl.setAttribute("NRFI", Integer.toString(CNRFI_cnt));
					CompEl.setAttribute("UnsRet", Integer.toString(CUNS_cnt));
					CompEl.setAttribute("UnsRetNWT", Integer.toString(CUNSNWT_cnt));
				}
			}
		}
		return Ret_XML;
	}

	/**
	 * In this method LPN is created with all the items having same LPN no at the given location in below format:
	 *  	
	 * <CreateLPN EnterpriseCode="NWCG" Node=cacheID>
	 * <LPN CaseId=LPNNo>
	 * <LPNLocation LocationId=""/>
	 * <InventoryList>
	 * <Inventory Quantity="" InventoryStatus="RFI">
	 * <InventoryItem ItemID="" ProductClass="" UnitOfMeasure="" />
	 * //If TagDtail is present
	 * <TagDetail  LotAttribute1="" LotAttribute2="" LotAttribute3=""  ManufacturingDate="" /> 
	 * //If SerialNo is present
	 * <SerialList>
	 * <SerialDetail SecondarySerial1=""  SerialNo="" /> 
	 * </SerialList>
	 * </Inventory>
	 * </InventoryList>
	 * </LPN>
	 * <Audit ReasonCode="Data-Entry" />
	 * </CreateLPN>
	 * 
	 * @param env
	 * @param cacheID
	 * @param receiptNumber
	 * @param ReceivingLoc
	 * @param EnterpriseCode
	 * @param outCreateReceipt
	 * @param outConfirmShipment
	 * @return
	 * @throws Exception
	 */
	public Document performAITReturns(YFSEnvironment env, String cacheID, String receiptNumber, String ReceivingLoc, String EnterpriseCode, Document outCreateReceipt, Document outConfirmShipment) throws Exception {
		Document outDoc = null, outDocVerification = null;
		HashSet hsLPNNo = new HashSet();
		String strDispositionCode = "";
		for (int i = 0; i < RetLineCount; i++) {
			Element eleReceiptLine = (Element) RetLines.item(i);
			if (eleReceiptLine.hasAttribute("LPNNo")) {
				String strLPN = eleReceiptLine.getAttribute("LPNNo");
				hsLPNNo.add(strLPN);
			}
		}
		if (!hsLPNNo.isEmpty()) {
			String LPNNo = "";
			Iterator itr = hsLPNNo.iterator();
			while (itr.hasNext()) {
				LPNNo = (String) itr.next();
				if (LPNNo != null && !LPNNo.equals("")) {
					Document docCreateLPN = XMLUtil.createDocument("CreateLPN");
					Element rootElement = docCreateLPN.getDocumentElement();
					rootElement.setAttribute("EnterpriseCode", "NWCG");
					rootElement.setAttribute("Node", cacheID);
					Element eleLPN = docCreateLPN.createElement("LPN");
					eleLPN.setAttribute("PalletId", LPNNo);
					rootElement.appendChild(eleLPN);
					Element eleAudit = docCreateLPN.createElement("Audit");
					eleAudit.setAttribute("ReasonCode", "DATA-ENTRY");
					rootElement.appendChild(eleAudit);
					Element eleLPNLocation = docCreateLPN.createElement("LPNLocation");
					eleLPNLocation.setAttribute("LocationId", "RETURN-1");
					eleLPN.appendChild(eleLPNLocation);
					Element eleReceipt = docCreateLPN.createElement("Receipt");
					eleReceipt.setAttribute("ReceiptNo", receiptNumber);
					eleLPN.appendChild(eleReceipt);
					Element eleInventoryList = docCreateLPN.createElement("InventoryList");
					eleLPN.appendChild(eleInventoryList);
					for (int i = 0; i < RetLineCount; i++) {
						Element eleReceiptLine = (Element) RetLines.item(i);
						String strLPN = eleReceiptLine.getAttribute("LPNNo");
						if (strLPN.equals(LPNNo)) {
							String strItemID = eleReceiptLine.getAttribute("ItemID");
							String strQuant = eleReceiptLine.getAttribute("QtyReturned");
							String strProductClass = eleReceiptLine.getAttribute("ProductClass");
							String strUnitOfMeasure = eleReceiptLine.getAttribute("UOM");
							// BEGIN PI909 - June 19, 2013
							/** 
							 *  If it is a trackable item, get the DispositionCode from the SerialInfoMainItem sub-element, 
							 *  Otherwise get it from the main eleReceiptLine. 
							 *  
							 *  Sample of returning a trackable item in an LPN Container XML:
							 *  
							 *  <ReceiptLine ItemID="000340" LPNNo="IDGBKLPN000013" NRFI="0"
							 *    ProductClass="Supply" QtyReturned="1" RFI="1"
							 *    ShortDescription="KIT - CHAIN SAW" UOM="KT" UnsRet="0" UnsRetNWT="0">
							 *    <SerialInfoMainItem DispositionCode="RFI" PrimarySerialNo="0340-GBK-0159-285"/>
							 *  </ReceiptLine>
							 **/
							if (eleReceiptLine.hasChildNodes()) {
								NodeList listEleReceiptLine_SerialInfoMainItem = eleReceiptLine.getElementsByTagName("SerialInfoMainItem");
								Element elemEleReceiptLine_SerialInfoMainItem = (Element) listEleReceiptLine_SerialInfoMainItem.item(0);
								strDispositionCode = elemEleReceiptLine_SerialInfoMainItem.getAttribute("DispositionCode");
							} else {
								strDispositionCode = eleReceiptLine.getAttribute("DispositionCode");
							}
							// END PI909 - June 19, 2013
							Element eleInventory = docCreateLPN.createElement("Inventory");
							eleInventory.setAttribute("Quantity", strQuant);
							eleInventory.setAttribute("InventoryStatus", strDispositionCode);
							eleInventoryList.appendChild(eleInventory);
							Element eleInventoryItem = docCreateLPN.createElement("InventoryItem");
							eleInventoryItem.setAttribute("ItemID", strItemID);
							eleInventoryItem.setAttribute("ProductClass", strProductClass);
							eleInventoryItem.setAttribute("UnitOfMeasure", strUnitOfMeasure);
							eleInventory.appendChild(eleInventoryItem);
							if (eleReceiptLine.hasChildNodes()) {
								Element eleSerialInfo = (Element) eleReceiptLine.getElementsByTagName("SerialInfoMainItem").item(0);
								String strPrimarySerialNo = eleSerialInfo.getAttribute("PrimarySerialNo");
								String strSecondarySerialNo = eleSerialInfo.getAttribute("SecondarySerialNo");
								Element eleTagDetail = docCreateLPN.createElement("TagDetail");
								eleTagDetail.setAttribute("LotNumber", strPrimarySerialNo);
								eleTagDetail.setAttribute("LotAttribute2", cacheID);
								eleInventory.appendChild(eleTagDetail);
								Element eleSerialList = docCreateLPN.createElement("SerialList");
								eleInventory.appendChild(eleSerialList);								
								Element eleSerialDetail = docCreateLPN.createElement("SerialDetail");
								eleSerialDetail.setAttribute("SecondarySerial1", strSecondarySerialNo);
								eleSerialDetail.setAttribute("SerialNo", strPrimarySerialNo);
								eleSerialList.appendChild(eleSerialDetail);
							}
						}
					}				
					CommonUtilities.invokeAPI(env, "createLPN", docCreateLPN);					
					Document rcDoc = createRFIReceiveOrderDocument(env, LPNNo, cacheID, ReceivingLoc, EnterpriseCode, outCreateReceipt, outConfirmShipment);
					outDoc = CommonUtilities.invokeAPI(env, "receiveOrder", rcDoc);
					if(outDoc == null) {
						outDoc = outDocVerification;
					}
					outDocVerification = outDoc;
					String strDestLocation = "";
					if (strDispositionCode.equals("NRFI")) {
						strDestLocation = "NRFI-1";
						Document out_Move = moveLocationInv(env, LPNNo, strDestLocation, cacheID);
					} else {
						if (RRPFlag.equals("YES")) {
							strDestLocation = "REFURB-RFI";
							Document out_Move = moveLocationInv(env, LPNNo, strDestLocation, cacheID);
						}
						if (RRPFlag.equals("NO")) {
							strDestLocation = "RFI-1";
							Document out_Move = moveLocationInv(env, LPNNo, strDestLocation, cacheID);
						}
					}
				}
			}
		}
		return outDoc;
	}

	/**
	 * 
	 * @param env
	 * @param LPNNo
	 * @param Cache
	 * @param ReceivingLoc
	 * @param EnterpriseCode
	 * @param outCreateReceipt
	 * @param outConfirmShipment
	 * @return
	 * @throws Exception
	 */
	public Document createRFIReceiveOrderDocument(YFSEnvironment env, String LPNNo, String Cache, String ReceivingLoc, String EnterpriseCode, Document outCreateReceipt, Document outConfirmShipment) throws Exception {
		Document rt_Receipt = XMLUtil.newDocument();
		Element el_Receipt = rt_Receipt.createElement("Receipt");
		rt_Receipt.appendChild(el_Receipt);
		el_Receipt.setAttribute("DoNotVerifyCaseContent", "N");
		el_Receipt.setAttribute("DocumentType", NWCGConstants.NWCG_RETURN_BLIND_RECEIPT_DOCUMENT);
		el_Receipt.setAttribute("EnterpriseCode", EnterpriseCode);
		el_Receipt.setAttribute("EnterpriseCodeDesc", "Hub Organization");
		el_Receipt.setAttribute("IgnoreOrdering", "Y");
		el_Receipt.setAttribute("IsBlindReceipt", "Y");
		el_Receipt.setAttribute("IsSingleOrder", "N");
		el_Receipt.setAttribute("LinesEntered", "N");
		el_Receipt.setAttribute("LocationId", ReceivingLoc);
		el_Receipt.setAttribute("ManuallyEntered", "Y");
		el_Receipt.setAttribute("Node", Cache);
		el_Receipt.setAttribute("OpenReceiptFlag", "Y");
		el_Receipt.setAttribute("OrderAvailableOnSystem", "N");
		el_Receipt.setAttribute("OrderReleaseKey", "");
		el_Receipt.setAttribute("OverReceiptPercentage", "0.00");
		el_Receipt.setAttribute("OverrideManualShipmentEntry", "Y");
		el_Receipt.setAttribute("PalletId", LPNNo);
		el_Receipt.setAttribute("QCRequired", "N");
		el_Receipt.setAttribute("ReceiptError", "N");
		el_Receipt.setAttribute("ReceiptNo", outCreateReceipt.getDocumentElement().getAttribute("ReceiptNo"));
		el_Receipt.setAttribute("ReceivingDock", ReceivingLoc);
		el_Receipt.setAttribute("ReceivingNode", Cache);
		el_Receipt.setAttribute("SellerOrganizationCode", EnterpriseCode);
		el_Receipt.setAttribute("SellerOrganizationCodeDesc", "Hub Organization");
		el_Receipt.setAttribute("ShipNode", "");
		el_Receipt.setAttribute("ShipmentError", "N");
		el_Receipt.setAttribute("ShipmentKey", outConfirmShipment.getDocumentElement().getAttribute("ShipmentKey"));
		el_Receipt.setAttribute("ShipmentNo", outConfirmShipment.getDocumentElement().getAttribute("ShipmentNo"));
		el_Receipt.setAttribute("ShipmentTotalNumberOfRecords", "1");
		return rt_Receipt;
	}

	/**
	 * 
	 * @param env
	 * @param LPN
	 * @param destLocation
	 * @param cache
	 * @return
	 * @throws Exception
	 */
	public Document moveLocationInv(YFSEnvironment env, String LPN, String destLocation, String cache) throws Exception {
		Document mvDoc = XMLUtil.createDocument("MoveLocationInventory");
		Element eleRoot = mvDoc.getDocumentElement();
		eleRoot.setAttribute("EnterpriseCode", "NWCG");
		eleRoot.setAttribute("Node", cache);
		Element eleSource = mvDoc.createElement("Source");
		eleSource.setAttribute("PalletId", LPN);
		eleSource.setAttribute("LocationId", "RETURN-1");
		eleRoot.appendChild(eleSource);
		Element eleDest = mvDoc.createElement("Destination");
		eleDest.setAttribute("LocationId", destLocation);
		eleRoot.appendChild(eleDest);
		Document outDoc = CommonUtilities.invokeAPI(env, "moveLocationInventory", mvDoc);
		return outDoc;
	}
}