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

package com.nwcg.icbs.yantra.ue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSBeforeCreateOrderUE;

/**
 * This UE is for converting Requested Delivery Date from GMT to CST (due to PI 1698). 
 * 2 fields will be updated: 
 *  - ReqDeliveryDate
 *  - ExtnReqDeliveryDate. 
 * This is for ROSS Place Requests ONLY. Hence the code is only activiated when:
 *  - DocumentType = 0001
 *  - DraftOrderFlag = Y
 *  - ExtnSystemOfOrigin = ROSS
 * 
 * Turn on VERBOSE logging for createOrder API to see these loggers. 
 * 
 * Sample inXML from ROSS Place Request::
	<Order BillToID="WYBHF" DocumentType="0001" DraftOrderFlag="Y"
	    EnterpriseCode="NWCG" IncidentKey="20140725095557174969272"
	    ModificationReasonCode="Auto Update from ROSS"
	    ModificationReasonText="ROSS initiated Issue, customer: WYBHF"
	    ModificationReference1="Time: 2015-07-23T09:26:39.530-0500"
	    OrderType="Normal" ReqDeliveryDate="2014-08-08T18:00:00-0000"
	    SellerOrganizationCode="NWCG" ShipNode="CORMK">
	    <Extn ExtnAgency="FS" ExtnCostCenter=""
	        ExtnCustomerName="USFS BIGHORN NATIONAL FOREST"
	        ExtnCustomerType="01" ExtnDepartment="USDA"
	        ExtnFsAcctCode="P2EKT8" ExtnFunctionalArea=""
	        ExtnGACC="RM (Rocky Mountain Area Coordination Center)"
	        ExtnIncidentCacheId="CORMK" ExtnIncidentName="ROANE CREEK"
	        ExtnIncidentNo="WY-BHF-000255" ExtnIncidentTeamType=""
	        ExtnIncidentType="FireWildfire" ExtnIncidentYear="2014"
	        ExtnNavInfo="SHIP_ADDRESS" ExtnOverrideCode="0202"
	        ExtnROSSBillingOrganization="WYBHF"
	        ExtnROSSFinancialCode="P2EKT8 (0202)" ExtnROSSFiscalYear="2014"
	        ExtnROSSOwningAgency="U.S. Forest Service"
	        ExtnReqDeliveryDate="2014-08-08T18:00:00-0500"
	        ExtnRossDispatchUnitId="WYCDC" ExtnSAOverrideCode="0202"
	        ExtnShipAcctCode="P2EKT8" ExtnShippingContactName="Wade Wyman"
	        ExtnShippingContactPhone="307-347-5203"
	        ExtnSystemOfOrigin="ROSS" ExtnUnitType="Federal" ExtnWBS=""/>
	    <OrderDates>
	        <OrderDate ActualDate="2015-07-23T09:26:39.541-0500"
	            DateTypeId="NWCG_DATE" ExpectedDate="2015-07-23T09:26:39.541-0500"/>
	    </OrderDates>
	    <PersonInfoShipTo AddressLine1="101 S 23rd Street"
	        AlternateEmailID="WYWBD" City="Worland" Country="US"
	        FirstName="Wind" LastName="River/Bighorn Basin District"
	        PersonID="WYWBD" State="WY" ZipCode="82401"/>
	    <OrderLines>
	        <OrderLine ConditionVariable1="ROSS">
	            <OrderLineTranQuantity OrderedQty="3" TransactionalUOM="KT"/>
	            <Extn ExtnBackOrderFlag="N" ExtnBackorderedQty="0"
	                ExtnBaseRequestNo="S-104" ExtnFwdQty="0"
	                ExtnOrigReqQty="3" ExtnQtyRfi="113.0"
	                ExtnRequestNo="S-104" ExtnSystemOfOrigin="ROSS" ExtnUTFQty="0"/>
	            <Item ItemID="000340" ItemShortDesc="KIT - CHAIN SAW"
	                ProductClass="Supply" UnitOfMeasure="KT"/>
	            <PersonInfoShipTo AddressLine1="101 S 23rd Street"
	                AlternateEmailID="WYWBD" City="Worland" Country="US"
	                FirstName="Wind" LastName="River/Bighorn Basin District"
	                PersonID="WYWBD" State="WY" ZipCode="82401"/>
	        </OrderLine>
	    </OrderLines>
	</Order>
 * 
 * @author lightwell
 * @date July 23, 2015
 */
public class NWCGBeforeCreateOrderUE implements YFSBeforeCreateOrderUE {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGBeforeCreateOrderUE.class);

	@Override
	public Document beforeCreateOrder(YFSEnvironment env, Document inXML) throws YFSUserExitException {
		logger.verbose("@@@@@ Entering com.nwcg.icbs.yantra.ue.NWCGBeforeCreateOrderUE::beforeCreateOrder @@@@@");
		try {
			String DocumentType = XPathUtil.getString(inXML.getDocumentElement(), "/Order/@DocumentType");
			String DraftOrderFlag = XPathUtil.getString(inXML.getDocumentElement(), "/Order/@DraftOrderFlag");
			String ExtnSystemOfOrigin = XPathUtil.getString(inXML.getDocumentElement(), "/Order/Extn/@ExtnSystemOfOrigin");
			logger.verbose("@@@@@ If Condition :: " + DocumentType + " " + DraftOrderFlag + " " + ExtnSystemOfOrigin);
			if(DocumentType.equals("0001") && DraftOrderFlag.equals("Y") && ExtnSystemOfOrigin.equals("ROSS")) {
				String ReqDeliveryDate = XPathUtil.getString(inXML.getDocumentElement(), "/Order/@ReqDeliveryDate");
				logger.verbose("@@@@@ OldReqDeliveryDate :: " + ReqDeliveryDate);
				String newDateStr = "";
				if (ReqDeliveryDate != null && ReqDeliveryDate.length() > 0) {
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");	
					sdf2.setTimeZone(TimeZone.getTimeZone("GMT"));
					sdf1.setTimeZone(TimeZone.getTimeZone("CST"));
					Date tdate = sdf2.parse(ReqDeliveryDate);
					newDateStr = sdf1.format(tdate);
				}
				Element elemOrder = (Element)XPathUtil.getNode(inXML.getDocumentElement(), "/Order");
				elemOrder.setAttribute("ReqDeliveryDate", newDateStr);
				logger.verbose("@@@@@ ReqDeliveryDate :: " + newDateStr);
				Element elemExtn = (Element)XPathUtil.getNode(inXML.getDocumentElement(), "/Order/Extn");
				elemExtn.setAttribute("ExtnReqDeliveryDate", newDateStr);
				logger.verbose("@@@@@ ExtnReqDeliveryDate :: " + newDateStr);
			}
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			e.printStackTrace();
		}
		logger.verbose("@@@@@ Exiting com.nwcg.icbs.yantra.ue.NWCGBeforeCreateOrderUE::beforeCreateOrder @@@@@");
		return inXML;
	}

	@Override
	public String beforeCreateOrder(YFSEnvironment env, String inString) throws YFSUserExitException {
		// TODO Auto-generated method stub
		return null;
	}
}