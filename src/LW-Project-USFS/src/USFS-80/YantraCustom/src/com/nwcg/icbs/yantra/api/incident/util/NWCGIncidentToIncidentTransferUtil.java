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

package com.nwcg.icbs.yantra.api.incident.util;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.incident.NWCGIncidentValidateData;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class will be used as utility class for Merge and Reassign incident notifications.
 * 
 * @author sgunda
 * @since USFS ICBS-ROSS Interface BR2 Increment 5
 */
public final class NWCGIncidentToIncidentTransferUtil {

	private static final String className = NWCGIncidentToIncidentTransferUtil.class
			.getName();

	/**
	 * This method will act get the incident details for the passed incident number
	 * and year. If it returns NULL, then it implies that the passed incident is not 
	 * present in ICBSR system. 
	 * Expected values for srcOrDest is SOURCE or DESTINATION. If the value is SOURCE,
	 * then this method will return only incident details. If the value is DESTINATION,
	 * then this method will return incident + person info + bill to incident details.
	 * 
	 * @param incNo: Expected format: CO-RMK-000001
	 * @param incYr
	 * @param srcRDest
	 * @return docIncDtlsOPDoc Document with the output of getIncidentDetails
	 */
	public static Document getIncidentDetails(YFSEnvironment env, String incNo, String incYr, String srcOrDest) {
		final String methodName = "process";
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "ENTRY: "
				+ className + "." + methodName);
		NWCGLoggerUtil.Log.finer("BEGIN TIMER: " + methodName);

		String serviceName = NWCGConstants.EMPTY_STRING;
		
		// Does the incident exist first ...
		Document docIncDtlsOPDoc = null;
		try {
			Document docGetIncDtlsIP = XMLUtil.createDocument("NWCGIncidentOrder");
			Element elmGetIncDtlsDoc = docGetIncDtlsIP.getDocumentElement();
			elmGetIncDtlsDoc.setAttribute(NWCGConstants.INCIDENT_NO_ATTR, incNo);
			elmGetIncDtlsDoc.setAttribute(NWCGConstants.YEAR_ATTR, incYr);
			docIncDtlsOPDoc = CommonUtilities.invokeService(env, 
					NWCGConstants.SVC_GET_INCIDENT_ORDER_DTLS_ONLY_SVC, docGetIncDtlsIP);
		}
		catch(Exception e){
			return null;
		}
		
		if (docIncDtlsOPDoc == null) {
			return null;
		}
		
		if (!srcOrDest.equalsIgnoreCase("SOURCE")) 
		{
			//Get incident details for destination 
			serviceName = NWCGConstants.SVC_GET_INCIDENT_ORDER_SVC;
			docIncDtlsOPDoc = null;
			try {
				Document docGetIncDtlsIP = XMLUtil.createDocument("NWCGIncidentOrder");
				Element elmGetIncDtlsDoc = docGetIncDtlsIP.getDocumentElement();
				elmGetIncDtlsDoc.setAttribute(NWCGConstants.INCIDENT_NO_ATTR, incNo);
				elmGetIncDtlsDoc.setAttribute(NWCGConstants.YEAR_ATTR, incYr);
				docIncDtlsOPDoc = CommonUtilities.invokeService(env, 
											serviceName, docGetIncDtlsIP);
			}
			catch(ParserConfigurationException pce){
				NWCGLoggerUtil.Log.warning("NWCGIncidentToIncidentTransferUtil::getIncidentDetails, " +
						   "Parser Configuration Exception while making " + serviceName + pce.getMessage());
				pce.printStackTrace();
			}
			catch(Exception e){
				NWCGLoggerUtil.Log.warning("NWCGIncidentToIncidentTransferUtil::getIncidentDetails, " +
						   "Exception while making " + serviceName + e.getMessage());
				e.printStackTrace();
			}
		}
		
		NWCGLoggerUtil.Log.finer("  END TIMER: "+methodName);
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "RETURN: "
				+ className + "." + methodName);
		return docIncDtlsOPDoc;
	}

	/**
	 * This method will create the incident with the incident details from the input
	 * Expected input type is nwcg:IncidentReturnType
	 * - Call XSL to convert to ICBSR format TransformFromROSSRegisterIncidentDetailsToICBSR.xsl
	 * - Sets Registered Interest to Y and IsActive to Y
	 * - Update the incident number, get unique shipping address, update customer and
	 *   raise an alert in the incident success alert queue by calling 
	 *   NWCGIncidentValidateData.validateData - It should raise an alert appropriately based
	 *   on Merge or Reassign Notification
	 *   
	 * @param incInfo
	 * @return docCreateIncDocOP Document returned by the NWCGCreateIncidentOrderService
	 */
	public static Document createIncident(YFSEnvironment env, Document docIncInfo, String reasonForMerge) {
		final String methodName = "createIncident";
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "ENTRY: "
				+ className + "." + methodName);
		NWCGLoggerUtil.Log.finer("BEGIN TIMER: " + methodName);

		Document docCreateIncDocOP = null;
		// Call XSL to get NWCGIncidentOrder
		try {
			Document docCreateIncDocAfterXSL = createIncidentInput(env, docIncInfo, reasonForMerge);
			
			docCreateIncDocOP = CommonUtilities.invokeService(env, 
							NWCGConstants.SVC_CREATE_INCIDENT_ORDER_SVC, docCreateIncDocAfterXSL);
			NWCGLoggerUtil.Log.finest("NWCGCreateIncidentOrderService output: "+XMLUtil.extractStringFromDocument(docCreateIncDocOP));
		}
		catch(Exception e) {
			NWCGLoggerUtil.Log.warning("NWCGIncidentToIncidentTransferUtil::createIncident, " +
					   					"Parser Configuration Exception " + e.getMessage());
			e.printStackTrace();
		}
		NWCGLoggerUtil.Log.finer("  END TIMER: "+methodName);
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "RETURN: "
				+ className + "." + methodName);
		return docCreateIncDocOP;
	}
	
	/**
	 * This method will create the incident with the incident details from the input
	 * Expected input type is nwcg:IncidentReturnType
	 * - Call XSL to convert to ICBSR format TransformFromROSSRegisterIncidentDetailsToICBSR.xsl
	 * - Sets Registered Interest to Y and IsActive to Y
	 *   
	 * @param incInfo
	 * @return docCreateIncDocOP Document returned by the NWCGCreateIncidentOrderService
	 */
	public static Document createIncidentInput(YFSEnvironment env, Document docIncInfo, String reasonForMerge) {
		final String methodName = "createIncidentInput";
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "ENTRY: "
				+ className + "." + methodName);
		NWCGLoggerUtil.Log.finer("BEGIN TIMER: " + methodName);

		// Call XSL to get NWCGIncidentOrder
		Document docCreateIncDocAfterXSL = null;
		try {
			docCreateIncDocAfterXSL = CommonUtilities.invokeService(env, 
									NWCGConstants.SVC_UPDT_INCIDENT_NOTIF_XSL_SVC, docIncInfo);
			// Update the document RegisteredInterest = Y and isActive = Y
			Element elmIncDoc = docCreateIncDocAfterXSL.getDocumentElement();
			elmIncDoc.setAttribute("IncidentSource", "R");
			elmIncDoc.setAttribute(NWCGConstants.INCIDENT_REG_INT_IN_ROSS_ATTR, NWCGConstants.YES);
			elmIncDoc.setAttribute(NWCGConstants.IS_ACTIVE, NWCGConstants.YES);
			elmIncDoc.setAttribute("IncidentAction", reasonForMerge);
			String custId = parseAndSetCustomerId(docIncInfo);
			
			NWCGIncidentValidateData incValidateData = new NWCGIncidentValidateData();
			docCreateIncDocAfterXSL = incValidateData.updateIncidentNumber(docCreateIncDocAfterXSL);
			docCreateIncDocAfterXSL = incValidateData.getUniqueShippingAddress(env, docCreateIncDocAfterXSL);
			if (custId != null && custId.length() > 0){
				docCreateIncDocAfterXSL = updtCustAndVerifyAddresses(env, docCreateIncDocAfterXSL, custId);
			}
			NWCGLoggerUtil.Log.finest("NWCGCreateIncidentOrderService output: "+XMLUtil.extractStringFromDocument(docCreateIncDocAfterXSL));
		}
		catch(Exception e) {
			NWCGLoggerUtil.Log.warning("NWCGIncidentToIncidentTransferUtil::createIncident, " +
					   					"Parser Configuration Exception " + e.getMessage());
			e.printStackTrace();
		}
		NWCGLoggerUtil.Log.finer("  END TIMER: "+methodName);
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "RETURN: "
				+ className + "." + methodName);
		return docCreateIncDocAfterXSL;
	}
	
	/**
	 * This method will update the Customer information, adds PersonInfoShipTo
	 * and PersonInfoBillTo based on the existence of the addresses
	 * @param env
	 * @param doc
	 * @return
	 */
	public static Document updtCustAndVerifyAddresses(YFSEnvironment env, Document doc, String custId) {
		final String methodName = "updtCustAndVerifyAddresses";
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "ENTRY: "
				+ className + "." + methodName);
		NWCGLoggerUtil.Log.finer("BEGIN TIMER: " + methodName);

		//String custId = parseAndSetCustomerId(doc);
		Document docCust = getCustomerDetails(env, custId);
		if (docCust == null) {
			return doc;
		}
		
		Element elmCust = docCust.getDocumentElement();
		NodeList nlPersonInfo = elmCust.getElementsByTagName("BillingPersonInfo");
		boolean custPersonInfo = false;
		Element elmCustPersonInfo = null;
		if (nlPersonInfo != null && nlPersonInfo.getLength() > 0) {
			custPersonInfo = true;
			elmCustPersonInfo = (Element) nlPersonInfo.item(0);
		}
		
		Element elmIncRoot = doc.getDocumentElement();
		if (custPersonInfo){
			// Check if Ship To Address is present or not. If it is not present, then
			// create the shipping address
			NodeList nlShipTo = elmIncRoot.getElementsByTagName("YFSPersonInfoShipTo");
			if (nlShipTo != null && nlShipTo.getLength() > 0){
				NWCGLoggerUtil.Log.finest("NWCGIncidentToIncidentTransferUtil::updtCustAndVerifyAddresses, " +
						"PersonInfoShipTo is already present, so not doing anything");
			}
			else {
				//PersonInfoKey
				elmIncRoot.setAttribute("PersonInfoShipToKey", elmCustPersonInfo.getAttribute("PersonInfoKey"));
			}
			
			// Check if Bill To Address is present or not. If it is not present, then create the 
			// billing address
			NodeList nlBillTo = elmIncRoot.getElementsByTagName("YFSPersonInfoBillTo");
			if (nlBillTo != null && nlBillTo.getLength() > 0){
				NWCGLoggerUtil.Log.finest("NWCGIncidentToIncidentTransferUtil::updtCustAndVerifyAddresses, " +
						"PersonInfoBillTo is already present, so not doing anything");
			}
			else {
				//PersonInfoKey
				elmIncRoot.setAttribute("PersonInfoBillToKey", elmCustPersonInfo.getAttribute("PersonInfoKey"));
			}
		}
		
		// Update Customer information on EXTN attributes of the Incident
		Element elmCustExtn = (Element) elmCust.getElementsByTagName("Extn").item(0);
		elmIncRoot.setAttribute(NWCGConstants.INCIDENT_GACC_ATTR, 
							elmCustExtn.getAttribute(NWCGConstants.CUST_GACC_ATTR));
		elmIncRoot.setAttribute(NWCGConstants.INCIDENT_CUSTOMER_NAME_ATTR, 
							elmCustExtn.getAttribute(NWCGConstants.CUST_CUSTOMER_NAME_ATTR));
		elmIncRoot.setAttribute(NWCGConstants.INCIDENT_UNIT_TYPE_ATTR, 
							elmCustExtn.getAttribute(NWCGConstants.CUST_UNIT_TYPE_ATTR));
		elmIncRoot.setAttribute(NWCGConstants.INCIDENT_AGENCY_ATTR, 
							elmCustExtn.getAttribute(NWCGConstants.CUST_AGENCY_ATTR));
		elmIncRoot.setAttribute(NWCGConstants.INCIDENT_DEPARTMENT_ATTR, 
							elmCustExtn.getAttribute(NWCGConstants.CUST_DEPARTMENT_ATTR));

		NWCGLoggerUtil.Log.finer("  END TIMER: "+methodName);
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "RETURN: "
				+ className + "." + methodName);
		return doc;
	}

	/**
	 * This method gets the customer details based on class level
	 * variable custId 
	 * @param doc
	 * @return
	 */
	private static Document getCustomerDetails(YFSEnvironment env, String custId) {
		final String methodName = NWCGConstants.API_GET_CUST_DETAILS;
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "ENTRY: "
				+ className + "." + methodName);
		NWCGLoggerUtil.Log.finer("BEGIN TIMER: " + methodName);

		Document customerDetailsDoc = null;
		try {
			Document customerInputDoc = XMLUtil.createDocument(NWCGConstants.CUST_CUST_ELEMENT);
			Element customerInputElm = customerInputDoc.getDocumentElement();
			customerInputElm.setAttribute(NWCGConstants.CUST_CUST_ID_ATTR, custId);
			customerInputElm.setAttribute(NWCGConstants.ORGANIZATION_CODE, NWCGConstants.ENTERPRISE_CODE);
			// Hard code organization code to "NWCG" as this is the only enterprise code that we have
			// for USFS project
			customerInputElm.setAttribute(NWCGConstants.CUST_ORG_CODE_ATTR, NWCGConstants.ENTERPRISE_CODE);
			customerDetailsDoc = CommonUtilities.invokeAPI(env, NWCGConstants.TMPL_GET_CUST_DETAILS_TEMPLATE, 
														NWCGConstants.API_GET_CUST_DETAILS, customerInputDoc);
			if (customerDetailsDoc != null){
				NWCGLoggerUtil.Log.finest("NWCGIncidentToIncidentTransferUtil::getCustomerDetails, " +
						"Customer output : " + XMLUtil.extractStringFromDocument(customerDetailsDoc));
			}
		}
		catch (ParserConfigurationException pce){
			NWCGLoggerUtil.Log.warning("NWCGIncidentToIncidentTransferUtil::getCustomerDetails, " +
					"ParserConfigurationException Message : " + pce.getMessage());
			NWCGLoggerUtil.printStackTraceToLog(pce);
		}
		catch (Exception e){
			NWCGLoggerUtil.Log.warning("NWCGIncidentToIncidentTransferUtil::getCustomerDetails, " +
					"Exception Message : " + e.getMessage());
			NWCGLoggerUtil.printStackTraceToLog(e);
		}
		NWCGLoggerUtil.Log.finer("  END TIMER: "+methodName);
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "RETURN: "
				+ className + "." + methodName);
		return customerDetailsDoc;
	}

	/**
	 * This method parses through the input xml and sets the customer id to
	 * class level variable. This method gets the parameters from BillingOrganization element
	 * Puts it in the format of UnitIDPrefix + "-" + UnitIDSuffix.
	 * Customer ID is a mandatory element. So, null check is not done
	 * @param doc
	 */
	public static String parseAndSetCustomerId(Document doc) {
		String unitIDPrefix = NWCGConstants.EMPTY_STRING;
		String unitIDSuffix = NWCGConstants.EMPTY_STRING;
		Element rootElm = doc.getDocumentElement();
		NodeList billingOrgNL = rootElm.getElementsByTagName(NWCGConstants.INC_NOTIF_BILLINGORG_ELEMENT);
		// There is only one occurence of BillingOrganization and it can have only one occurence 
		// and is a mandatory element
		Node billingOrgNode = billingOrgNL.item(0);
		NodeList unitIDNL = billingOrgNode.getChildNodes();
		for (int i = 0; i < unitIDNL.getLength(); i++) {
			Node unitIDNode = unitIDNL.item(i);
			String nodeName = unitIDNode.getNodeName();
			if (nodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_UNIT_ID_PREFIX_ATTR)){
				unitIDPrefix = unitIDNode.getTextContent();
			}
			else if (nodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_UNIT_ID_SUFFIX_ATTR)){
				unitIDSuffix = unitIDNode.getTextContent();
			}
		}
		
		NWCGLoggerUtil.Log.finest("NWCGIncidentToIncidentTransferUtil::getCustomerId, Unit ID Prefix : " + unitIDPrefix);
		NWCGLoggerUtil.Log.finest("NWCGIncidentToIncidentTransferUtil::getCustomerId, Unit ID Suffix : " + unitIDSuffix);
		return unitIDPrefix.concat(unitIDSuffix);
	}
	
	/**
	 * This method will update the incident for the given incident no and year
	 * It will add the attributes to NWCGIncidentOrder from the Hashtable.
	 * Hashtable can be like ["RegisterInterest", "Y"], ["IsActive", "Y"], etc
	 * @param incNo
	 * @param incYr
	 * @param lockReason
	 * @return
	 */
	public static Document updateIncident(YFSEnvironment env, String incNo, String incYr, 
								   		  Hashtable<String, String> htIncAttributes) {
		final String methodName = "updateIncident";
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "ENTRY: "
				+ className + "." + methodName);
		NWCGLoggerUtil.Log.finer("BEGIN TIMER: " + methodName);
		// SVC_CHG_INCIDENT_ORDER_SVC
		Document docIncDtlsOPDoc = null;
		try {
			Document docUpdateIncDtlsIP = XMLUtil.createDocument("NWCGIncidentOrder");
			Element elmUpdateIncDtlsDoc = docUpdateIncDtlsIP.getDocumentElement();
			elmUpdateIncDtlsDoc.setAttribute(NWCGConstants.INCIDENT_NO_ATTR, incNo);
			elmUpdateIncDtlsDoc.setAttribute(NWCGConstants.YEAR_ATTR, incYr);
			
			for(Enumeration<String> e = htIncAttributes.keys(); e.hasMoreElements();){
				String incAttr = e.nextElement();
				String incAttrVal = htIncAttributes.get(incAttr);
				elmUpdateIncDtlsDoc.setAttribute(incAttr, incAttrVal);
			}
			docIncDtlsOPDoc = CommonUtilities.invokeService(env, 
										NWCGConstants.SVC_CHG_INCIDENT_ORDER_SVC, docUpdateIncDtlsIP);
		}
		catch(ParserConfigurationException pce) {
			NWCGLoggerUtil.Log.warning("NWCGIncidentToIncidentTransferUtil::updateIncidentDetails, " +
					   "Parser Configuration Exception while making " + 
					   NWCGConstants.SVC_CHG_INCIDENT_ORDER_SVC + pce.getMessage());
			pce.printStackTrace();
		}
		catch(Exception e) {
			NWCGLoggerUtil.Log.warning("NWCGIncidentToIncidentTransferUtil::updateIncidentDetails, " +
					   "Parser Configuration Exception while making " + 
					   NWCGConstants.SVC_CHG_INCIDENT_ORDER_SVC + e.getMessage());
			e.printStackTrace();
		}
		NWCGLoggerUtil.Log.finer("  END TIMER: "+methodName);
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "RETURN: "
				+ className + "." + methodName);
		return docIncDtlsOPDoc;
	}
	
	/**
	 * This method will create the order input XML using source incident and destination
	 * incident details. Source and destination incident details are that of ICBSR and
	 * not from the notification.
	 * This method does an XSL to set the destination incident parameters and then sets
	 * the source incident manually without going through XSL. 
	 * It will set Order attributes, Order/Extn attributes, PersonInfoShipTo and
	 * PersonInfoBillTo
	 * 
	 * @param srcInc
	 * @param destInc
	 * @return docOrder Document createOrder Input XML
	 */
	public static Document createOrderInputWithoutOLs(YFSEnvironment env, 
							Document docSrcInc, Document docDestInc, String shipNode) {
		final String methodName = "createOrderInputWithoutOLs";
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "ENTRY: "
				+ className + "." + methodName);
		NWCGLoggerUtil.Log.finer("BEGIN TIMER: " + methodName);
		// Set current date
		// Update Request Date
		Document docOrder = null;
		try {
			// Set the attributes from destination incident
			docOrder = CommonUtilities.invokeService(env, NWCGConstants.SVC_TRANSFORM_FROM_INC_TO_TRANSFER_ISSUE_XSL, docDestInc);
			
			// Set the attributes from source incident
			setSrcIncAttributes(docOrder, docSrcInc);
			
			Element elmOrderDoc = docOrder.getDocumentElement();
			elmOrderDoc.setAttribute(NWCGConstants.ORDER_DATE, CommonUtilities.convertToYantraDate(new Date()));
			elmOrderDoc.setAttribute(NWCGConstants.REQ_SHIP_DATE_ATTR, CommonUtilities.convertToYantraDate(new Date()));
			elmOrderDoc.setAttribute(NWCGConstants.SHIP_NODE, shipNode);
		} catch (Exception e) {
			NWCGLoggerUtil.Log.warning("NWCGIncidentToIncidentTransferUtil::createOrderInputWithoutOLs, " +
					   "Exception while making " + NWCGConstants.SVC_TRANSFORM_FROM_INC_TO_TRANSFER_ISSUE_XSL + " : " + 
	   				   "Exception : " + e.getMessage());
			e.printStackTrace();
		}
		NWCGLoggerUtil.Log.finer("  END TIMER: "+methodName);
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "RETURN: "
				+ className + "." + methodName);
		return docOrder;
	}
	
	/**
	 * This method will set the source incident attributes to the transfer order xml
	 * 
	 * @param docOrder
	 * @param docSrcInc
	 */
	private static void setSrcIncAttributes(Document docOrder, Document docSrcInc) {
		final String methodName = "setSrcIncAttributes";
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "ENTRY: "
				+ className + "." + methodName);
		NWCGLoggerUtil.Log.finer("BEGIN TIMER: " + methodName);

		Element elmOrderExtn = (Element) docOrder.getDocumentElement()
				.getElementsByTagName(NWCGConstants.EXTN_ELEMENT).item(0);
		Element elmSrcIncDoc = docSrcInc.getDocumentElement();
		docOrder.getDocumentElement().setAttribute(NWCGConstants.OVERRIDE, NWCGConstants.YES);
		
		String blmAcctCode = elmSrcIncDoc.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_BLM_ACCT_CODE);
		if (!StringUtil.isEmpty(blmAcctCode)){
			elmOrderExtn.setAttribute(NWCGConstants.EXTN_BLM_ACCT_CODE, blmAcctCode);
		}
		
		String fsAcctCode = elmSrcIncDoc.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_FS_ACCT_CODE);
		if (!StringUtil.isEmpty(fsAcctCode)){
			elmOrderExtn.setAttribute(NWCGConstants.EXTN_FS_ACCT_CODE, fsAcctCode);
		}
		
		String primaryCacheId = elmSrcIncDoc.getAttribute(NWCGConstants.PRIMARY_CACHE_ID);
		if (!StringUtil.isEmpty(primaryCacheId)){
			elmOrderExtn.setAttribute(NWCGConstants.INCIDENT_CACHE_ID, primaryCacheId);
		}
		
		String incName = elmSrcIncDoc.getAttribute(NWCGConstants.INCIDENT_NAME);
		if (!StringUtil.isEmpty(incName)){
			elmOrderExtn.setAttribute(NWCGConstants.INCIDENT_NAME_ATTR, incName);
		}
		
		String incNo = elmSrcIncDoc.getAttribute(NWCGConstants.INCIDENT_NO_ATTR);
		if (!StringUtil.isEmpty(incNo)){
			elmOrderExtn.setAttribute(NWCGConstants.INCIDENT_NO, incNo);
		}
		
		String incType = elmSrcIncDoc.getAttribute(NWCGConstants.INCIDENT_TYPE);
		if (!StringUtil.isEmpty(incType)){
			elmOrderExtn.setAttribute(NWCGConstants.INCIDENT_TYPE_ATTR, incType);
		}
		
		String incYear = elmSrcIncDoc.getAttribute(NWCGConstants.YEAR);
		if (!StringUtil.isEmpty(incYear)) {
			elmOrderExtn.setAttribute(NWCGConstants.INCIDENT_YEAR, incYear);
		}
		
		String otherAcctCode = elmSrcIncDoc.getAttribute(NWCGConstants.BILL_TRANS_INCIDENT_OTHER_ACCT_CODE);
		if (!StringUtil.isEmpty(otherAcctCode)){
			elmOrderExtn.setAttribute(NWCGConstants.EXTN_OTHER_ACCT_CODE, otherAcctCode);
		}
		
		String overrideCode = elmSrcIncDoc.getAttribute(NWCGConstants.INCIDENT_OVERRIDECODE_ATTR);
		if (!StringUtil.isEmpty(overrideCode)){
			elmOrderExtn.setAttribute(NWCGConstants.EXTN_OVERRIDE_CODE, overrideCode);
		}
		String phoneNo = elmSrcIncDoc.getAttribute(NWCGConstants.PHONE_NO_ATTR);
		if (!StringUtil.isEmpty(phoneNo)){
			elmOrderExtn.setAttribute(NWCGConstants.EXTN_INCIDENT_PHONE_NO, phoneNo);
		}
		
		elmOrderExtn.setAttribute(NWCGConstants.NWCG_EXTN_TO_RECIEVE, NWCGConstants.NO);
		
		NWCGLoggerUtil.Log.finer("  END TIMER: "+methodName);
		NWCGLoggerUtil.Log.logp(Level.FINEST, className, methodName, "RETURN: "
				+ className + "." + methodName);
	}
	
	/**
	 * This method will set the order lines from the input to the passed order document
	 * Expected format for the order line is<br/><br/>
	 * &lt;OrderLine Action="CREATE">
     *      &lt;Extn ExtnTrackableId="101" ExtnRequestNo=""/>
     *      &lt;OrderLineTranQuantity OrderedQty="1.0" TransactionalUOM="EA"/>
     *      &lt;Item ItemID="000148" ProductClass="Supply"/>
     * &lt;/OrderLine>
     * This method will get the OrderLine elements from the vector and it will add it to
     * Order document.
     * 
	 * @param orderDoc
	 * @param vecElmOLs
	 * @return
	 */
	public static Document updateOrderWithOLs(Document docOrder, Vector<Element> vecElmOLs){
		Element elmOrderDoc = docOrder.getDocumentElement();
		Element elmOLs = docOrder.createElement(NWCGConstants.ORDER_LINES);
		elmOrderDoc.appendChild(elmOLs);
		Iterator<Element> itr = vecElmOLs.iterator();
		while (itr.hasNext()) {
			Element elmOL = itr.next();
			Node nodeOL = docOrder.importNode(elmOL, true);
			elmOLs.appendChild(nodeOL);
		}
		return null;
	}	
	
	/**
	 * This method will call createOrderWithOutOLs. From the obtained document, it
	 * will add the order lines from orderLines document.
	 * Expected orderLines document is like below OR OrderLines can be root element too
	 * <br/><br/>&lt;AnyRootElement AnyAttributes="">
	 *     &lt;OrderLines>
	 *     		&lt;OrderLine Action="CREATE">
	 *     			&lt;Extn ExtnTrackableId="101" ExtnRequestNo=""/>
	 *     			&lt;OrderLineTranQuantity OrderedQty="1.0" TransactionalUOM="EA"/>            
	 *     			&lt;Item ItemID="000148" ProductClass="Supply"/>
	 *     		&lt;/OrderLine>
	 *     		&lt;OrderLine Action="CREATE">
	 *     			&lt;Extn ExtnRequestNo=""/>
	 *     			&lt;OrderLineTranQuantity OrderedQty="4.0" TransactionalUOM="EA"/>
	 *     			&lt;Item ItemID="000926" ProductClass="Supply"/>
	 *     		&lt;/OrderLine>
	 *     &lt;/OrderLines>
	 * &lt;/AnyRootElement>
	 * 
	 * @param srcInc
	 * @param destInc
	 * @param orderLines
	 * @return
	 */
	public static Document createOrderWithOLs(YFSEnvironment env, Document docSrcInc, 
							Document docDestInc, Document docOrderLines, String shipNode){
		Document docOrderWithoutOLs = createOrderInputWithoutOLs(env, docSrcInc, docDestInc, shipNode);
		
		String reqTag = NWCGConstants.ORDER_LINES;
		Element elmDocOrderLinesFromIP = docOrderLines.getDocumentElement();
		String ipDocOLNodeName = elmDocOrderLinesFromIP.getNodeName();
		Element elmOLs = null;
		if (reqTag.equalsIgnoreCase(ipDocOLNodeName)) {
			elmOLs = (Element) docOrderLines.getFirstChild();
		} 
		else {
			elmOLs = (Element) elmDocOrderLinesFromIP.getElementsByTagName(NWCGConstants.ORDER_LINES).item(0);
		}

		Node nodeDestOL = docOrderWithoutOLs.importNode(elmOLs, true);
		docOrderWithoutOLs.getDocumentElement().appendChild(nodeDestOL);
		
		return docOrderWithoutOLs;
	}
	
	/**
	 * This method will create the order in Draft status. It will then call
	 * confirmDraftOrder to confirmed the draft order. ON_SUCCESS of confirmDraftOrder 
	 * will complete incident to incident transfer
	 * 
	 * @param orderDoc
	 * @return docOP Document of the output of confirmDraftOrder API
	 */
	public static Document incidentToIncidentTransfer(YFSEnvironment env, Document docOrder){
		Document docOP = null;
		try {
			// Create the order
			Document docOrderTmpl = XMLUtil.createDocument(NWCGConstants.ORDER_ELM);
			Element elmOrderTmplDoc = docOrderTmpl.getDocumentElement();
			elmOrderTmplDoc.setAttribute(NWCGConstants.ORDER_HEADER_KEY, NWCGConstants.EMPTY_STRING);
			elmOrderTmplDoc.setAttribute(NWCGConstants.DOCUMENT_TYPE, NWCGConstants.EMPTY_STRING);
			Document docOrderOP = CommonUtilities.invokeAPI(env, docOrderTmpl, NWCGConstants.API_CREATE_ORDER, docOrder);
			
			// Confirm the draft order
			Document docConfirmDraftOrder = XMLUtil.createDocument("ConfirmDraftOrder");
			Element elmConfirmDraftOrderDoc = docConfirmDraftOrder.getDocumentElement();
			
			Element elmOrderOP = docOrderOP.getDocumentElement();
			
			elmConfirmDraftOrderDoc.setAttribute(NWCGConstants.ORDER_HEADER_KEY, 
										elmOrderOP.getAttribute(NWCGConstants.ORDER_HEADER_KEY));
			elmConfirmDraftOrderDoc.setAttribute(NWCGConstants.DOCUMENT_TYPE, 
										elmOrderOP.getAttribute(NWCGConstants.DOCUMENT_TYPE));
			
			docOP = CommonUtilities.invokeAPI(env, docOrderTmpl, "confirmDraftOrder", docConfirmDraftOrder);
		}
		catch(Exception e){
			NWCGLoggerUtil.Log.warning("NWCGIncidentToIncidentTransferUtil::incidentToIncidentTransfer, " +
					   "Exception while making createOrder API call : " + e.getMessage());
			e.printStackTrace();
		}
		return docOP;
	}
}