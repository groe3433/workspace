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

package com.nwcg.icbs.yantra.api.incident;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/*
 * this is invoked per issue 
 * input xml 
 * <Order IgnoreOrdering="Y" OrderHeaderKey="20061010203401622980" Override="Y">
 *   <Extn ExtnIncidentNo="1-1-1" ExtnLastIncidentNo1="" ExtnLastIncidentNo2=""/>
 * </Order>
 * Last Incident # 1 = Recent Incident
 * Last Incident # 2 = Previous Incident
 */
public class NWCGValidateAndAssignIncidentToIssue implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGValidateAndAssignIncidentToIssue.class);

	public void setProperties(Properties arg0) throws Exception {

	}

	public Document validateAndAssignIncidentToIssue(YFSEnvironment env,
			Document inDoc) throws Exception {
		if (logger.isVerboseEnabled())
			logger.verbose("validateAndAssignIncidentToIssue()->inDoc:"
					+ XMLUtil.getXMLString(inDoc));

		Element rootElement = inDoc.getDocumentElement();
		String strExtnIncidentNo = rootElement.getAttribute("NewValue");
		String strExtnIncidentYear = rootElement.getAttribute("Year");
		String strOrderHeaderKey = rootElement.getAttribute("OrderHeaderKey");
		String strStatus = rootElement.getAttribute("Status");
		if (strStatus.equals("Shipped") || strStatus.equals("Cancelled")) {
			return null;
		}
		// get the extn tag
		NodeList nlExtn = rootElement.getElementsByTagName("Extn");
		Element elemExtn = null;
		// if there is no extn element (incase the current incident number is
		// blank) create a new one and append it
		// to order element
		if (nlExtn == null || nlExtn.getLength() <= 0) {
			elemExtn = inDoc.createElement("Extn");
			rootElement.appendChild(elemExtn);
		} else {
			elemExtn = (Element) nlExtn.item(0);
		}
		// if()
		{
			// if incident number is not null
			if (!StringUtil.nonNull(strExtnIncidentNo).equals("")) {
				// getting the incident details
				Document getIncidentOrderListIP = XMLUtil
						.createDocument("NWCGIncidentOrder");
				getIncidentOrderListIP.getDocumentElement().setAttribute(
						"IncidentNo", strExtnIncidentNo);
				getIncidentOrderListIP.getDocumentElement().setAttribute(
						"Year", strExtnIncidentYear);

				Document getIncidentListOP = CommonUtilities.invokeService(env,
						NWCGConstants.NWCG_GET_INCIDENT_ORDERLIST_SERVICE,
						getIncidentOrderListIP);
				if (logger.isVerboseEnabled())
					logger.verbose("validateAndAssignIncidentToIssue getIncidentListOP "
							+ XMLUtil.getXMLString(getIncidentListOP));
				Element elemIncidentList = getIncidentListOP
						.getDocumentElement();

				NodeList nlNWCGIncidentOrder = elemIncidentList
						.getElementsByTagName("NWCGIncidentOrder");

				if (nlNWCGIncidentOrder != null
						&& nlNWCGIncidentOrder.getLength() > 0) {
					Element elemIncident = (Element) nlNWCGIncidentOrder
							.item(0);
					// assign the extended attributes to the order extn tag
					elemExtn.setAttribute("ExtnIncidentName",
							elemIncident.getAttribute("IncidentName"));
					elemExtn.setAttribute("ExtnIncidentType",
							elemIncident.getAttribute("IncidentType"));
					elemExtn.setAttribute("ExtnIncidentTeamType",
							elemIncident.getAttribute("IncidentTeamType"));
					elemExtn.setAttribute("ExtnFsAcctCode",
							elemIncident.getAttribute("IncidentFsAcctCode"));
					elemExtn.setAttribute("ExtnBlmAcctCode",
							elemIncident.getAttribute("IncidentBlmAcctCode"));
					elemExtn.setAttribute("ExtnOtherAcctCode",
							elemIncident.getAttribute("IncidentOtherAcctCode"));
					elemExtn.setAttribute("ExtnOverrideCode",
							elemIncident.getAttribute("OverrideCode"));
					elemExtn.setAttribute("ExtnCustomerName",
							elemIncident.getAttribute("CustomerName"));

					rootElement.setAttribute("BillToID",
							elemIncident.getAttribute("CustomerId"));
					// populate override code and customer name
					// rootElement.setAttribute("BuyerOrganizationCode",elemIncident.getAttribute("CustomerId"));
					// assign addresses ONLY WHEN THE STATUS IS NOT INCLUDED IN
					// SHIPMENT
					if (logger.isVerboseEnabled())
						logger.verbose("strStatus ==> " + strStatus);
					if (logger.isVerboseEnabled())
						logger.verbose("strStatus =========>> "
								+ (strStatus != null && strStatus
										.indexOf(NWCGConstants.ORDER_STATUS_INCLUDED_IN_SHIPMENT) == -1));
					/*
					 * if(strStatus != null && strStatus.indexOf(NWCGConstants.
					 * ORDER_STATUS_INCLUDED_IN_SHIPMENT) == -1 ) {
					 * if(logger.isVerboseEnabled())
					 * logger.verbose("appending the addresses");
					 * populateAddressFor
					 * ("YFSPersonInfoShipTo","PersonInfoShipTo"
					 * ,elemIncident,inDoc,true);
					 * populateAddressFor("YFSPersonInfoBillTo"
					 * ,"PersonInfoBillTo",elemIncident,inDoc,true);
					 * populateAddressFor
					 * ("YFSPersonInfoDeliverTo","PersonInfo",elemIncident
					 * ,inDoc,false); }
					 */
					// logic for assigning the last incident number
					String strExtnLastIncidentNo1 = StringUtil.nonNull(elemExtn
							.getAttribute("ExtnLastIncidentNo1"));
					/*
					 * Updated logic: for all cases Last Incident # 1 should
					 * have the value of current Incident Numnber Last Incident
					 * # 2 shuold have the value of Last Incident # 1
					 */
					elemExtn.setAttribute("ExtnLastIncidentNo1", StringUtil
							.nonNull(elemExtn.getAttribute("ExtnIncidentNo")));
					elemExtn.setAttribute("ExtnLastIncidentNo2",
							StringUtil.nonNull(strExtnLastIncidentNo1));

					elemExtn.setAttribute("ExtnIncidentNo",
							elemIncident.getAttribute("IncidentNo"));
					elemExtn.setAttribute("ExtnIncidentYear",
							elemIncident.getAttribute("Year"));

					if (logger.isVerboseEnabled())
						logger.verbose("invoking change order with "
								+ XMLUtil.getXMLString(inDoc));

					if (logger.isVerboseEnabled())
						logger.verbose("changeOrder I/P =>"
								+ XMLUtil.getXMLString(inDoc));
					CommonUtilities.invokeAPI(env, "changeOrder", inDoc);
				}
				// update the trackable inventory records
				// updatetrackableRecords(env,strExtnIncidentNo,strOrderHeaderKey);
			}
		}

		if (logger.isVerboseEnabled())
			logger.verbose("returning " + XMLUtil.getXMLString(inDoc));
		// assigning it to null otherwise it will show the detail page for the
		// last order selected
		return null;
	}

	// this method updates all the trackable inventory records for the given
	// issue number
	/*
	 * Logic: 1. invoke getShipmentList and pass the OrderHeaderKey - this will
	 * return all the shipments for this order 2. fetch the serial number from
	 * shiment serial tag element 3. get all the trackable records for all the
	 * serial number fetched from step 2 4. update all the trackable record with
	 * the new incident number
	 */
	private void updatetrackableRecords(YFSEnvironment env,
			String strExtnIncidentNo, String strOrderHeaderKey)
			throws Exception {
		// get all the shipment list
		Document docGetShipmentList = getShipmentListFromOrder(env,
				strOrderHeaderKey);
		if (logger.isVerboseEnabled())
			logger.verbose("updatetrackableRecords :: docGetShipmentList "
					+ XMLUtil.getXMLString(docGetShipmentList));
		NodeList nlShipmentTagSerial = docGetShipmentList
				.getElementsByTagName("ShipmentTagSerial");
		if (nlShipmentTagSerial != null) {
			if (logger.isVerboseEnabled())
				logger.verbose("updatetrackableRecords :: nlShipmentTagSerial "
						+ nlShipmentTagSerial.getLength());
			int iShipmentTagSerialCounter = nlShipmentTagSerial.getLength();
			for (int index = 0; index < iShipmentTagSerialCounter; index++) {
				Element elemShipmentTag = (Element) nlShipmentTagSerial
						.item(index);
				String strSerialNo = elemShipmentTag.getAttribute("SerialNo");
				if (logger.isVerboseEnabled())
					logger.verbose("updatetrackableRecords :: strSerialNo "
							+ strSerialNo);
				Document docGetTrackableRecords = getTrackableRecordFromSerialNumber(
						env, strSerialNo);
				NodeList nlTrackableRecord = docGetTrackableRecords
						.getElementsByTagName("NWCGTrackableItem");
				if (nlTrackableRecord != null) {
					if (logger.isVerboseEnabled())
						logger.verbose("updatetrackableRecords :: nlTrackableRecord "
								+ nlTrackableRecord.getLength());
					int iTrackableCounter = nlTrackableRecord.getLength();
					for (int indexTrk = 0; indexTrk < iTrackableCounter; indexTrk++) {
						Element elemTrack = (Element) nlTrackableRecord
								.item(indexTrk);
						String strTrackableItemKey = elemTrack
								.getAttribute("TrackableItemKey");
						String strStatusIncidentNo = elemTrack
								.getAttribute("StatusIncidentNo");
						if (logger.isVerboseEnabled())
							logger.verbose("updatetrackableRecords :: trackable item key "
									+ strTrackableItemKey
									+ " and incident no "
									+ strStatusIncidentNo);
						if (strStatusIncidentNo != null
								&& (!strStatusIncidentNo.equals(""))) {
							// this trackable item is out on an incident
							Document docTrackableUpdate = XMLUtil
									.createDocument("NWCGTrackableItem");
							Element elem = docTrackableUpdate
									.getDocumentElement();
							elem.setAttribute("TrackableItemKey",
									strTrackableItemKey);
							elem.setAttribute("StatusIncidentNo",
									strExtnIncidentNo);

							// update the trackable record
							String strService = ResourceUtil
									.get("nwcg.icbs.changetrackableinventory.service",
											"NWCGChangeTrackableInventoryRecordService");
							if (logger.isVerboseEnabled())
								logger.verbose("updatetrackableRecords :: calling changeTrackableInventoryRecord with ip "
										+ docTrackableUpdate);
							CommonUtilities.invokeService(env, strService,
									docTrackableUpdate);

						}// end if

					}// end for all trackable record
				}// end if trackable record is not null

			}// end for all shipment tag serials
		}// end if tag serial node list exists
	}

	private Document getTrackableRecordFromSerialNumber(YFSEnvironment env,
			String strSerialNo) throws Exception {
		Document docTrackableList = XMLUtil.createDocument("NWCGTrackableItem");
		Element elem = docTrackableList.getDocumentElement();
		elem.setAttribute("SerialNo", strSerialNo);

		String strServiceName = ResourceUtil
				.get("nwcg.icbs.gettrackableitemlist.service");
		if (logger.isVerboseEnabled())
			logger.verbose("getTrackableRecordFromSerialNumber :: calling gettrackableitem list with IP "
					+ XMLUtil.getXMLString(docTrackableList));
		return CommonUtilities.invokeService(env, strServiceName,
				docTrackableList);
	}

	private Document getShipmentListFromOrder(YFSEnvironment env,
			String strOrderHeaderKey) throws Exception {
		Document rt_Shipment = XMLUtil.getDocument();

		Element el_Shipment = rt_Shipment.createElement("Shipment");
		rt_Shipment.appendChild(el_Shipment);

		Element el_ShipmentLines = rt_Shipment.createElement("ShipmentLines");
		el_Shipment.appendChild(el_ShipmentLines);

		Element el_ShipmentLine = rt_Shipment.createElement("ShipmentLine");
		el_ShipmentLines.appendChild(el_ShipmentLine);
		el_ShipmentLine.setAttribute("OrderHeaderKey", strOrderHeaderKey);

		if (logger.isVerboseEnabled())
			logger.verbose("getShipmentListFromOrder :: invoking getShipmentList with IP "
					+ rt_Shipment);

		return CommonUtilities.invokeAPI(env,
				"NWCGValidateAndAssignIncidentToIssue_getShipmentList",
				"getShipmentList", rt_Shipment);
	}

	/*
	 * populates the address from Incident Details to the Order Details if this
	 * newly created node as to be attached to the root as the child then pass
	 * true else pass false
	 */
	private void populateAddressFor(String strIncidentAddress,
			String strOrderAddress, Element elemIncident, Document inDoc,
			boolean bAppendToRoot) {
		// get the address details from the incident
		NodeList nlYFSPersonInfo = elemIncident
				.getElementsByTagName(strIncidentAddress);

		if (nlYFSPersonInfo != null && nlYFSPersonInfo.getLength() > 0) {
			Element elemYFSPersonInfo = (Element) nlYFSPersonInfo.item(0);
			// create a new element
			Element elemOrderPersonInfo = inDoc.createElement(strOrderAddress);

			CommonUtilities.populateAddressDetails(elemYFSPersonInfo,
					elemOrderPersonInfo);

			if (bAppendToRoot) {
				inDoc.getDocumentElement().appendChild(elemOrderPersonInfo);
			} else {
				// populate the additional addresses
				Element elemAdditionalAddresses = inDoc
						.createElement("AdditionalAddresses");
				Element elemAdditionalAddress = inDoc
						.createElement("AdditionalAddress");
				elemAdditionalAddress.setAttribute("AddressType",
						NWCGConstants.ADDRESS_TYPE_DELIVER);
				elemAdditionalAddress.appendChild(elemOrderPersonInfo);
				elemAdditionalAddresses.appendChild(elemAdditionalAddress);
				inDoc.getDocumentElement().appendChild(elemAdditionalAddresses);

			}
		}

	}

}
