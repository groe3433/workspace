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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.ydm.japi.ue.YDMBeforeConsolidateToShipment;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;

/**
 * @author jvishwakarma this class stamps the shipping account code from the
 *         order to the shipment Since we are invoking the
 *         consolidateToShipmentAPI which intern calls createShipment and we
 *         cant place the extenssion as a part of i/p to consolodateToShipment
 *         API we are implimenting this UE to get the work done
 * 
 *         Mapping the “Estimated Delivery Date” from issue to “Estimated
 *         Arrival Date” field of Shipment Mapping the “Requested Ship Date”
 *         from issue to “Estimated Depart Date” field of Shipment
 * 
 */
public class NWCGYDMBeforeConsolidateToShipment implements
		YDMBeforeConsolidateToShipment {
	
	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGYDMBeforeConsolidateToShipment.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yantra.ydm.japi.ue.YDMBeforeConsolidateToShipment#
	 * beforeConsolidateToShipment(com.yantra.yfs.japi.YFSEnvironment,
	 * org.w3c.dom.Document) Input xml inDoc <Shipment Action="Create"
	 * BillToAddressKey="20060329120529251809" BillToCustomerId=""
	 * BuyerMarkForNodeId="" BuyerOrganizationCode="" BuyerReceivingNodeId=""
	 * CarrierAccountNo="" CarrierServiceCode="Extra Hours" CarrierType="PARCEL"
	 * Currency="USD" CustomerPoNo="" DeliveryCode="" DeliveryMethod="SHP"
	 * DeliveryTS="2007-04-20T00:00:00-04:00" DepartmentCode=""
	 * DocumentType="0001" EnterpriseCode="NWCG" EspCheckRequired="N"
	 * ExpectedDeliveryDate="2007-04-20T00:00:00-04:00"
	 * ExpectedShipmentDate="2007-04-20T00:00:00-04:00" FindShipmentAndAdd="Y"
	 * FreightTerms="" GiftFlag="N" IsAppointmentReqd="N" ItemClassification=""
	 * MarkForKey="" MergeNode="" OrderType="" OverrideModificationRules="N"
	 * PackListType="" PriorityCode="" ReceivingNode="" RoutingSource="02"
	 * SCAC="FEDX" SellerOrganizationCode="NWCG"
	 * ShipDate="2007-04-20T00:00:00-04:00" ShipNode="RMK" ShipToCustomerId=""
	 * ShipmentConsolidationGroupId="" ToAddressKey="200704030944121186511"
	 * WorkOrderApptKey="" WorkOrderKey=""> <ShipmentLines> <ShipmentLine
	 * BuyerMarkForNodeId="" CustomerPoNo=" " DepartmentCode=" " GiftFlag="N"
	 * MarkForKey=" " OrderHeaderKey="20061010205230623283"
	 * OrderLineKey="200704030943381185995" OrderNo="0000001011"
	 * OrderReleaseKey="200704030945071187495" OrderType=" " Quantity="1.00"
	 * ShipToCustomerId=" " ShipmentConsolidationGroupId=""/> </ShipmentLines>
	 * </Shipment>
	 */

	public Document beforeConsolidateToShipment(YFSEnvironment env,
			Document inDoc) throws YFSUserExitException {
		try {
			if (logger.isVerboseEnabled())
				logger.verbose("NWCGYDMBeforeConsolidateToShipment :: inXML "
						+ XMLUtil.getXMLString(inDoc));
			Element elemShip = inDoc.getDocumentElement();
			// extract the orderheaderkey from shiment line
			NodeList nlShipLine = elemShip.getElementsByTagName("ShipmentLine");
			if (nlShipLine != null && nlShipLine.getLength() > 0) {
				// extract the order header key from the first shipmnet line
				// order to shipment mapping is ONE TO ONE
				Element elemShipLine = (Element) nlShipLine.item(0);
				String strOrderKey = elemShipLine
						.getAttribute("OrderHeaderKey");
				if (logger.isVerboseEnabled())
					logger.verbose("NWCGYDMBeforeConsolidateToShipment :: strOrderKey=> "
							+ strOrderKey);
				Element elemExtn = getShippingAccountCodeFromOrder(env,
						strOrderKey);

				if (elemExtn != null) {
					// append the child from order extn to the shipment extn
					// since the variable names are same we dont have to write
					// any code for attribute name mapping
					elemShip.appendChild(inDoc.importNode(elemExtn, true));
				}

			}

			if (logger.isVerboseEnabled())
				logger.verbose("NWCGYDMBeforeConsolidateToShipment :: returning inXML "
						+ XMLUtil.getXMLString(inDoc));
		} catch (Exception ex) {
			logger.error("!!!!! NWCGYDMBeforeConsolidateToShipment:: Caught Exception ::: "
						+ ex.getMessage(),ex);
			new YFSUserExitException(ex.getMessage());
		}
		return inDoc;
	}

	/*
	 * this method returns the extn element of order having the shipping account
	 * code
	 */
	private Element getShippingAccountCodeFromOrder(YFSEnvironment env,
			String strOrderKey) throws Exception {
		Document inDoc = XMLUtil.createDocument("Order");
		Element rootElem = inDoc.getDocumentElement();
		rootElem.setAttribute("OrderHeaderKey", strOrderKey);

		if (logger.isVerboseEnabled())
			logger.verbose("NWCGYDMBeforeConsolidateToShipment :: getOrderDetails API I/P "
					+ XMLUtil.getXMLString(inDoc));
		Document outDoc = CommonUtilities.invokeAPI(env,
				"NWCGYDMBeforeConsolidateToShipment_getOrderDetails",
				"getOrderDetails", inDoc);
		if (logger.isVerboseEnabled())
			logger.verbose("NWCGYDMBeforeConsolidateToShipment :: getOrderDetails API O/P "
					+ XMLUtil.getXMLString(outDoc));

		Element rootOutDoc = outDoc.getDocumentElement();
		// creating a document based on the output document
		Element elemShipmentExtn = outDoc.createElement("Extn");
		if (rootOutDoc != null) {
			String strReqShipDate = rootOutDoc.getAttribute("ReqShipDate");
			// Estimated Departure Date (EXTN) = Requested Ship Date

			// -- CR 542 -- assign ExpectedDate to Estimated Departure Date
			// below:
			// elemShipmentExtn.setAttribute("ExtnEstimatedDepartDate",strReqShipDate);
			NodeList nlExtn = rootOutDoc.getElementsByTagName("Extn");
			if (nlExtn != null && nlExtn.getLength() > 0) {
				Element elemExtn = (Element) nlExtn.item(0);
				// Account Codes
				elemShipmentExtn.setAttribute("ExtnShipAcctCode",
						elemExtn.getAttribute("ExtnShipAcctCode"));
				elemShipmentExtn.setAttribute("ExtnIncidentNum",
						elemExtn.getAttribute("ExtnIncidentNo"));
				elemShipmentExtn.setAttribute("ExtnYear",
						elemExtn.getAttribute("ExtnIncidentYear"));
			}
			NodeList nlOrderDate = rootOutDoc.getElementsByTagName("OrderDate");
			if (nlOrderDate != null && nlOrderDate.getLength() > 0) {
				Element elemOrderDate = (Element) nlOrderDate.item(0);
				// Estimated Arrival Date (EXTN) = Estimated Delivery Date
				elemShipmentExtn.setAttribute("ExtnEstimatedArrivalDate",
						elemOrderDate.getAttribute("ExpectedDate"));
				elemShipmentExtn.setAttribute("ExtnEstimatedDepartDate",
						elemOrderDate.getAttribute("ExpectedDate"));
			}
		}
		return elemShipmentExtn;
	}
}
