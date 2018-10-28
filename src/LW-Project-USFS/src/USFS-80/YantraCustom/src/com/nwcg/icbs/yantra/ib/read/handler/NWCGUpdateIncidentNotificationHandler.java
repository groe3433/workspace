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

package com.nwcg.icbs.yantra.ib.read.handler;

import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.incident.NWCGIncidentValidateData;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * Update Incident Notification Handler
 * 
 * This method will update the incident in ICBSR system 
 * 	- if primary financial code is present in input xml 
 * 	- if Incident exists in ICBSR system and is registered with ROSS
 * 	- if Customer exists in ICBSR system
 * 	- Transforms the ROSS format to ICBSR format
 * 	- Call NWCGChangeIncidentOrder to update the incident
 * Sample input xml:
<?xml version="1.0" encoding="UTF-8"?>
<ron:DeliverNotificationReq encoding="UTF-8" standalone="yes" distributionID="cd038829-60df-4340-b9d3-12168887fead" messageName="UpdateIncidentNotification"
        version="1.0" xmlns:cdf="http://www.cdf.ca.gov/CAD"
        xmlns:ron="http://nwcg.gov/services/ross/resource_order_notification/1.1"
        xmlns:rscn="http://nwcg.gov/services/ross/resource_notification/1.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        <ron:NotifiedSystem>
            <SystemType>ICBS</SystemType>
            <SystemID>null</SystemID>
        </ron:NotifiedSystem>
<ron:UpdateIncidentNotification>
	<ron:NotificationBase>
		<MessageOriginator>
			<SystemOfOrigin>
				<SystemType>ROSS</SystemType>
				<SystemID>aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa</SystemID>
			</SystemOfOrigin>
			<DispatchUnitID>
				<UnitIDPrefix>aa</UnitIDPrefix>
				<UnitIDSuffix>aaaa</UnitIDSuffix>
			</DispatchUnitID>
		</MessageOriginator>
		<TimeOfNotification>2001-12-17T09:30:47.0Z</TimeOfNotification>
		<Action>Cancel</Action>
	</ron:NotificationBase>
	<ron:Incident>
		<IncidentKey>
			<IncidentID>
				<EntityType>IncidentAviationHazard</EntityType>
				<EntityID>aaaaaaaaaaaaaaaaaa</EntityID>
				<ApplicationSystem>
					<SystemType>ROSS</SystemType>
					<SystemID>aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa</SystemID>
				</ApplicationSystem>
			</IncidentID>
			<NaturalIncidentKey>
				<HostID>
					<UnitIDPrefix>CO</UnitIDPrefix>
					<UnitIDSuffix>ARMZ</UnitIDSuffix>
				</HostID>
				<SequenceNumber>9</SequenceNumber>
				<YearCreated>2008</YearCreated>
			</NaturalIncidentKey>
		</IncidentKey>
		<IncidentDetails>
			<IncidentName>IncNameAAAA</IncidentName>
			<IncidentType>IncTypeAAAA</IncidentType>
			<SystemOfRecord>
				<SystemType>ROSS</SystemType>
				<SystemID>SysIDAAAA</SystemID>
			</SystemOfRecord>
			<InitialDateTime>2001-12-17T09:30:47.0Z</InitialDateTime>
			<IncidentLocation>
				<GeographicCoordinate>
					<GeographicCoordinateLatitude>
						<LatitudeDegreeValue>0</LatitudeDegreeValue>
						<LatitudeMinuteValue>0</LatitudeMinuteValue>
						<LatitudeSecondValue>0</LatitudeSecondValue>
						<LatitudeDirection>North</LatitudeDirection>
					</GeographicCoordinateLatitude>
					<GeographicCoordinateLongitude>
						<LongitudeDegreeValue>0</LongitudeDegreeValue>
						<LongitudeMinuteValue>0</LongitudeMinuteValue>
						<LongitudeSecondValue>0</LongitudeSecondValue>
						<LongitudeDirection>East</LongitudeDirection>
					</GeographicCoordinateLongitude>
				</GeographicCoordinate>
				<TRSCoordinate>
					<State>AK</State>
					<MeridianName>BlackHills</MeridianName>
					<Township>aaaaaa</Township>
					<TownshipDirection>North</TownshipDirection>
					<Range>aaaaaa</Range>
					<RangeDirection>East</RangeDirection>
					<Section>1</Section>
					<QuarterName>aaaaa</QuarterName>
				</TRSCoordinate>
				<UTMCoordinate>
					<UTMGridZoneID>aa</UTMGridZoneID>
					<UTMEastingValue>160000</UTMEastingValue>
					<UTMNorthingValue>0</UTMNorthingValue>
					<Hemisphere>North</Hemisphere>
				</UTMCoordinate>
			</IncidentLocation>
			<LocationName>LocNameAAAA</LocationName>
			<OfficeReferenceNumber>OffRefNumAAAA</OfficeReferenceNumber>
			<Description>DescAAAA</Description>
			<OriginatingSystemUserData>OrigSysUserDataAAA</OriginatingSystemUserData>
			<isComplexIndicator>true</isComplexIndicator>
			<DispatchOrganization>
				<UnitIDPrefix>aa</UnitIDPrefix>
				<UnitIDSuffix>aaaa</UnitIDSuffix>
			</DispatchOrganization>
			<BillingOrganization>
				<UnitIDPrefix>unitIDPrefix</UnitIDPrefix>
				<UnitIDSuffix>unitIDSuffix</UnitIDSuffix>
			</BillingOrganization>
			<CacheOrganization>
				<UnitIDPrefix>CO</UnitIDPrefix>
				<UnitIDSuffix>RMK</UnitIDSuffix>
			</CacheOrganization>
			<Status>Open</Status>
		</IncidentDetails>
		<IncidentEntities>
			<IncidentRadioFrequencies>
				<IncidentRadioFrequencyKey>
					<RadioFrequencyNaturalKey>
						<IncidentKey>
							<IncidentID>
								<EntityType>IncidentAviationHazard</EntityType>
								<EntityID>EntityIDAAAA</EntityID>
								<ApplicationSystem>
									<SystemType>ROSS</SystemType>
									<SystemID>SystemIDAAA</SystemID>
								</ApplicationSystem>
							</IncidentID>
							<NaturalIncidentKey>
								<HostID>
									<UnitIDPrefix>CO</UnitIDPrefix>
									<UnitIDSuffix>ARMZ</UnitIDSuffix>
								</HostID>
								<SequenceNumber>1009</SequenceNumber>
								<YearCreated>2008</YearCreated>
							</NaturalIncidentKey>
						</IncidentKey>
						<FrequencyType>FreqTypeAAAA</FrequencyType>
						<FrequencyValue>FreqValAAAA</FrequencyValue>
						<Tone>0</Tone>
					</RadioFrequencyNaturalKey>
					<RadioFrequencyID>
						<EntityType>IncidentAviationHazard</EntityType>
						<EntityID>aaaaaaaaaaaaaaaaaa</EntityID>
						<ApplicationSystem>
							<SystemType>ROSS</SystemType>
							<SystemID>SystemIDAAAA</SystemID>
						</ApplicationSystem>
					</RadioFrequencyID>
				</IncidentRadioFrequencyKey>
				<PrimaryIndicator>true</PrimaryIndicator>
			</IncidentRadioFrequencies>
			<Documentation>
				<Date>2001-12-17T09:30:47.0Z</Date>
				<SystemName>ROSS</SystemName>
				<DispatchUnitID>
					<UnitIDPrefix>aa</UnitIDPrefix>
					<UnitIDSuffix>aaaa</UnitIDSuffix>
				</DispatchUnitID>
				<Author>AuthorAAAA</Author>
				<Text>TextAAAA</Text>
			</Documentation>
			<IncidentAviationHazards>
				<IncidentAviationHazardKey>
					<IncidentAviationHazardID>
						<EntityType>IncidentAviationHazard</EntityType>
						<EntityID>aaaaaaaaaaaaaaaaaa</EntityID>
						<ApplicationSystem>
							<SystemType>ROSS</SystemType>
							<SystemID>SysIDAAA</SystemID>
						</ApplicationSystem>
					</IncidentAviationHazardID>
					<IncidentAviationHazardNaturalKey>
						<IncidentKey>
							<IncidentID>
								<EntityType>IncidentAviationHazard</EntityType>
								<EntityID>EntIDAAA</EntityID>
								<ApplicationSystem>
									<SystemType>ROSS</SystemType>
									<SystemID>SystemIDAAAA</SystemID>
								</ApplicationSystem>
							</IncidentID>
							<NaturalIncidentKey>
								<HostID>
									<UnitIDPrefix>aa</UnitIDPrefix>
									<UnitIDSuffix>aaaa</UnitIDSuffix>
								</HostID>
								<SequenceNumber>1</SequenceNumber>
								<YearCreated>2000</YearCreated>
							</NaturalIncidentKey>
						</IncidentKey>
					</IncidentAviationHazardNaturalKey>
				</IncidentAviationHazardKey>
				<IncidentAviationHazard>
					<HazardType>String</HazardType>
					<City>CityAAAA</City>
					<State>String</State>
					<HazardLocation>
						<GeographicCoordinate>
							<GeographicCoordinateLatitude>
								<LatitudeDegreeValue>0</LatitudeDegreeValue>
								<LatitudeMinuteValue>0</LatitudeMinuteValue>
								<LatitudeSecondValue>0</LatitudeSecondValue>
								<LatitudeDirection>North</LatitudeDirection>
							</GeographicCoordinateLatitude>
							<GeographicCoordinateLongitude>
								<LongitudeDegreeValue>0</LongitudeDegreeValue>
								<LongitudeMinuteValue>0</LongitudeMinuteValue>
								<LongitudeSecondValue>0</LongitudeSecondValue>
								<LongitudeDirection>East</LongitudeDirection>
							</GeographicCoordinateLongitude>
						</GeographicCoordinate>
						<TRSCoordinate>
							<State>AK</State>
							<MeridianName>BlackHills</MeridianName>
							<Township>aaaaaa</Township>
							<TownshipDirection>North</TownshipDirection>
							<Range>aaaaaa</Range>
							<RangeDirection>East</RangeDirection>
							<Section>1</Section>
							<QuarterName>aaaaa</QuarterName>
						</TRSCoordinate>
						<UTMCoordinate>
							<UTMGridZoneID>aa</UTMGridZoneID>
							<UTMEastingValue>160000</UTMEastingValue>
							<UTMNorthingValue>0</UTMNorthingValue>
							<Hemisphere>North</Hemisphere>
						</UTMCoordinate>
					</HazardLocation>
					<Description>DescriptionAAAA</Description>
				</IncidentAviationHazard>
			</IncidentAviationHazards>
			<IncidentAddresses>
				<Address>
					<Name>NameAAAA</Name>
					<Type>SHIPPING</Type>
					<Line1>Line1AAAA</Line1>
					<Line2>Line2AAAA</Line2>
					<City>CityAAA</City>
					<State>String</State>
					<ZipCode>aaaaaaaaaa</ZipCode>
					<CountryCode>CountryCodeAAA</CountryCode>
				</Address>
				<PrimaryInd>true</PrimaryInd>
			</IncidentAddresses>
			<IncidentRequestBlocks>
				<Catalog>Aircraft</Catalog>
				<Name>NameAAAA</Name>
				<StartNumber>0</StartNumber>
				<EndNumber>0</EndNumber>
				<Purpose>PurposeAAAA</Purpose>
				<ForUseByAppSystem>
					<SystemType>ROSS</SystemType>
					<SystemID>SystemIDAAAA</SystemID>
				</ForUseByAppSystem>
			</IncidentRequestBlocks>
			<IncidentFinancialCodes>
				<FinancialCode>
					<Code>a</Code>
					<OwningAgencyName>OwningAgencyNameAAAA</OwningAgencyName>
					<FiscalYear>1901</FiscalYear>
				</FinancialCode>
				<PrimaryInd>true</PrimaryInd>
			</IncidentFinancialCodes>
			<IncidentContacts>
				<Type>Requesting</Type>
				<Name>NameAAAA</Name>
				<Information>InfAAAA</Information>
			</IncidentContacts>
		</IncidentEntities>
	</ron:Incident>
</ron:UpdateIncidentNotification>
</ron:DeliverNotificationReq>
 */
public class NWCGUpdateIncidentNotificationHandler implements NWCGMessageHandlerInterface {
	
	private String custId = NWCGConstants.EMPTY_STRING;
	private String incNo = NWCGConstants.EMPTY_STRING;
	private String year = NWCGConstants.EMPTY_STRING;
	private String distId = NWCGConstants.EMPTY_STRING;
	private String cacheId = NWCGConstants.EMPTY_STRING;

	/**
	 * 
	 */
	public Document process(YFSEnvironment env, Document doc) {
		System.out.println("@@@@@ Entering NWCGUpdateIncidentNotificationHandler::process @@@@@");		
		System.out.println("@@@@@ Input XML : " + XMLUtil.getXMLString(doc));
		
		// Initialize the variables
		distId = doc.getDocumentElement().getAttribute(NWCGConstants.INC_NOTIF_DIST_ID_ATTR);
		parseAndSetIncidentNumberAndYear(doc);
		parseAndSetCustomerId(doc);
		parseAndSetCacheId(doc);
	
		// check if Primary Financial Code exists in the passed input xml
		boolean chkPrimFin = checkPrimaryFinCodeExistence(env, doc);
		NWCGLoggerUtil.Log.finer("NWCGUpdateIncidentNotificationHandler::process, Primary Financial Code exists : " + chkPrimFin);		
		if (!chkPrimFin){
			return doc;
		}
		
		// Check incident existence and its register status
		boolean incChk = checkIfIncidentExistsAndItsRegisterStatus(env, doc);
		NWCGLoggerUtil.Log.finer("NWCGUpdateIncidentNotificationHandler::process, Incident exists and is registered : " + incChk);		
		if (!incChk){
			return doc;
		}
		
		// Check if customer exists and get customer details
		boolean isValidCustId = true;
		Document custDoc = getCustomerDetails(env, doc);
		if (custDoc == null){
			isValidCustId = false;
		}
		
		// Call XSL service to transform ROSS format to ICBSR format xsl
		Document nwcgFormatUpdtIncDoc = null;
		try {
			nwcgFormatUpdtIncDoc = CommonUtilities.invokeService(env, 
										NWCGConstants.SVC_UPDT_INCIDENT_NOTIF_XSL_SVC, doc); 			
		}
		catch (Exception e){
			// raise an alert in NWCG_INCIDENT_FAILURE mentioning XSL failed
			NWCGLoggerUtil.Log.warning("NWCGUpdateIncidentNotificationHandler::process, XSL failed");
			NWCGLoggerUtil.printStackTraceToLog(e);
			String detailDesc = "ROSS format to ICBSR format XSL transformation failed. " +
								"This notification is not processed : " + e.getMessage();
			String desc = "ROSS to ICBSR XSL transformation failed";
			getDataAndRaiseAlert(env, doc, detailDesc, desc, null, false);
			return doc;
		}
		
		// Update Customer details to the above input xml
		Element nwcgFormatUpdtIncRootElm = nwcgFormatUpdtIncDoc.getDocumentElement();
		Element custConsumerElm = null;
		if (isValidCustId){
			Element custDocElm = custDoc.getDocumentElement();
			NodeList custDocChildsNL = custDocElm.getChildNodes();
			Element custExtnElm = null;
			boolean obtExtnElm = false;
			for (int i=0; i < custDocChildsNL.getLength(); i++){
				Node childElm = custDocChildsNL.item(i);
				String childNodeName = childElm.getNodeName(); 
				if (childNodeName.equalsIgnoreCase(NWCGConstants.CUST_EXTN_ELEMENT)){
					custExtnElm = (Element) childElm;
					obtExtnElm = true;
				}
				else if (childNodeName.equalsIgnoreCase(NWCGConstants.CUST_CONSUMER_ELEMENT)){
					custConsumerElm = (Element) childElm;
					nwcgFormatUpdtIncRootElm.setAttribute("PersonInfoBillToKey", custConsumerElm.getAttribute("BillingAddressKey"));
				}
			}

			// Update the customer attributes to incident if Customer has those Extn fields.
			// If it is not present, then do not update the attributes
			NWCGLoggerUtil.Log.finer("NWCGUpdateIncidentNotificationHandler::process, Updating customer details");		
			if (obtExtnElm){
				nwcgFormatUpdtIncRootElm.setAttribute(NWCGConstants.INCIDENT_UNIT_TYPE_ATTR, 
														custExtnElm.getAttribute(NWCGConstants.CUST_UNIT_TYPE_ATTR));
				nwcgFormatUpdtIncRootElm.setAttribute(NWCGConstants.INCIDENT_AGENCY_ATTR, 
														custExtnElm.getAttribute(NWCGConstants.CUST_AGENCY_ATTR));
				nwcgFormatUpdtIncRootElm.setAttribute(NWCGConstants.INCIDENT_DEPARTMENT_ATTR, 
														custExtnElm.getAttribute(NWCGConstants.CUST_DEPARTMENT_ATTR));
				nwcgFormatUpdtIncRootElm.setAttribute(NWCGConstants.INCIDENT_GACC_ATTR, 
														custExtnElm.getAttribute(NWCGConstants.CUST_GACC_ATTR));
				nwcgFormatUpdtIncRootElm.setAttribute(NWCGConstants.INCIDENT_CUSTOMER_NAME_ATTR, 
														custExtnElm.getAttribute(NWCGConstants.CUST_CUSTOMER_NAME_ATTR));
			}
		}
		
		// Check the date format obtained in NotificationBase/TimeOfNotification
		// Verified. Yantra default date format is accepting the input date time stamp (2001-12-17T09:30:47.0Z)
		
		// Check if primary address is present in input xml. If it is not present, use customers address
		// as ship to address for the incident
		NWCGLoggerUtil.Log.finer("NWCGUpdateIncidentNotificationHandler::process, Checking address (unique)");		
		NodeList nwcgFormatChildNodes = nwcgFormatUpdtIncRootElm.getChildNodes();
		boolean isPersonInfoShipToExists = false;
		Document updtIncDoc = null;
		for (int i=0; i < nwcgFormatChildNodes.getLength() && !isPersonInfoShipToExists; i++){
			Node childNode = nwcgFormatChildNodes.item(i);
			if (childNode.getNodeName().equalsIgnoreCase(NWCGConstants.INCIDENT_YFS_PERSON_INFO_SHIPTO_ELEM)){
				isPersonInfoShipToExists = true;
				NWCGIncidentValidateData incValData = new NWCGIncidentValidateData();
				try {
					NWCGLoggerUtil.Log.finer("NWCGUpdateIncidentNotificationHandler::process, Getting single address");		
					updtIncDoc = incValData.getUniqueShippingAddress(env, nwcgFormatUpdtIncDoc);
				}
				catch (Exception e){
					NWCGLoggerUtil.Log.warning("NWCGUpdateIncidentNotificationHandler::process, " +
							"Exception while getting unique address : " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		
		if (!isPersonInfoShipToExists){
			// If there is a Consumer element in getCustomerDetails output
			if (isValidCustId && custConsumerElm != null){
				// Output of getCustomerDetails is templated controlled output. It has only one child
				// under Customer/Consumer
				Element billingPersonInfoElm = (Element) custConsumerElm.getChildNodes().item(0);
				if (billingPersonInfoElm != null){
					Element shipToElm = nwcgFormatUpdtIncDoc.createElement(NWCGConstants.INCIDENT_YFS_PERSON_INFO_SHIPTO_ELEM);
					shipToElm.setAttribute(NWCGConstants.INCIDENT_ADDR_LINE_1_ATTR, 
										billingPersonInfoElm.getAttribute(NWCGConstants.ADDRESS_LINE_1));
					shipToElm.setAttribute(NWCGConstants.INCIDENT_ADDR_LINE_2_ATTR, 
										billingPersonInfoElm.getAttribute(NWCGConstants.ADDRESS_LINE_2));
					shipToElm.setAttribute(NWCGConstants.INCIDENT_CITY_ATTR, 
										billingPersonInfoElm.getAttribute(NWCGConstants.CITY));
					shipToElm.setAttribute(NWCGConstants.INCIDENT_STATE_ATTR, 
										billingPersonInfoElm.getAttribute(NWCGConstants.STATE));
					shipToElm.setAttribute(NWCGConstants.INCIDENT_ZIPCODE_ATTR, 
										billingPersonInfoElm.getAttribute(NWCGConstants.ZIP_CODE));
					shipToElm.setAttribute(NWCGConstants.INCIDENT_COUNTRY_ATTR, 
										billingPersonInfoElm.getAttribute(NWCGConstants.CUST_COUNTRY_ATTR));
					nwcgFormatUpdtIncRootElm.appendChild(shipToElm);
					NWCGLoggerUtil.Log.finer("NWCGUpdateIncidentNotificationHandler::process, Updated customer address to incident address");		
				}
			}
		}
		
		// If YFSPersonInfoShipTo is present, this updtIncDoc will not be NULL. Over there, we are 
		// getting unique address. If it is not there, then it will be NULL.
		if (updtIncDoc == null){
			updtIncDoc = nwcgFormatUpdtIncDoc;
		}
		// Set the incident no with the proper sequence number. XSLT doesn't add 0's to sequence number
		Element updtIncDocElm = updtIncDoc.getDocumentElement();
		updtIncDocElm.setAttribute(NWCGConstants.INCIDENT_NO_ATTR, incNo);
		updtIncDocElm.setAttribute(NWCGConstants.MODIFICATION_CODE, "Auto Update From ROSS");
		String modDesc = "Rec'd UpdateIncidentNotification";
		updtIncDocElm.setAttribute(NWCGConstants.MODIFICATION_DESC, modDesc);
		
		String alertDesc = NWCGConstants.EMPTY_STRING;
		String errors = checkMandatoryParams(updtIncDoc, alertDesc, isValidCustId);
		
		if (errors != null){
			NWCGLoggerUtil.Log.finer("NWCGUpdateIncidentNotificationHandler::process, Mandatory parameters are not passed");
			getDataAndRaiseAlert(env, doc, errors, "Missing mandatory parameters for incident update", alertDesc, false);
			return doc;
		}
				
		try {
			NWCGLoggerUtil.Log.finer("NWCGUpdateIncidentNotificationHandler::process, " +
					"Input XML to NWCGChangeIncidentOrderService : " + XMLUtil.extractStringFromDocument(updtIncDoc));
			
			CommonUtilities.invokeService(env, NWCGConstants.SVC_CHG_INCIDENT_ORDER_SVC, updtIncDoc);
		}
		catch(Exception e){
			// raise an alert in NWCG_INCIDENT_FAILURE - Unable to update 
			NWCGLoggerUtil.Log.warning("NWCGUpdateIncidentNotificationHandler::process, Exception " +
					"while calling NWCGChangeIncidentOnlyService. Message : " + e.getMessage());
			NWCGLoggerUtil.printStackTraceToLog(e);
			String errDesc = "Failed while updating incident details in ICBSR system : " + e.getMessage();
			String desc = "Failed while calling NWCGChangeIncidentOnlyService";
			getDataAndRaiseAlert(env, doc, errDesc, desc, alertDesc, false);
			return doc;
		}
		
		// Raise an alert in NWCG_INCIDENT_SUCCESS.
		String detailDesc = NWCGConstants.EMPTY_STRING;
		if (isValidCustId){
			detailDesc = "Update Incident Notification received for incident " + incNo + " and year " + year +  
							" from ROSS, and incident updated successfully in ICBS-R. ";
		}
		else {
			detailDesc = "Update Incident Notification received for incident " + incNo + " and year " + year +  
							" from ROSS. Received invalid Customer ID. Updated other incident details successfully";
		}
		String desc = " Processed Update Incident Notification for incident " + incNo + " and year " + year;
		getDataAndRaiseAlert(env, doc, detailDesc + alertDesc, desc, alertDesc, true);
		
		System.out.println("@@@@@ Exiting NWCGUpdateIncidentNotificationHandler::process @@@@@");	
		return doc;
    }

	/**
	 * This method checks if primary financial code exists in input xml. If it does not
	 * exists, then raise an alert
	 * @param env
	 * @param doc
	 * @return
	 */
	private boolean checkPrimaryFinCodeExistence(YFSEnvironment env, Document doc) {
		System.out.println("@@@@@ Entering NWCGUpdateIncidentNotificationHandler::checkPrimaryFinCodeExistence @@@@@");	
		
		boolean primFinCodeExists = true;
		Element updtIncNotifRootElm = doc.getDocumentElement();
		// Check primary financial code
		//	UpdateIncidentNotification/Incident/IncidentEntities/IncidentFinancialCodes/FinancialCode/PrimaryInd 
		//	is not NULL and equals TRUE
		boolean obtPrimIndVal = false; // This flag is used to stop recursive calls to IncidentFinancialCodes
		String primaryIndVal = NWCGConstants.EMPTY_STRING;
		NodeList primaryFinCodeNL = updtIncNotifRootElm.getElementsByTagName(NWCGConstants.INC_NOTIF_FINANCIAL_CODES_ELEMENT);
		if (primaryFinCodeNL == null || (primaryFinCodeNL.getLength() < 1)){
			getDataAndRaiseAlert(env, doc, NWCGConstants.NOTIF_ERR_PRIM_FIN_CODE_NOT_PASSED_1, 
					"IncidentFinancialCodes element is not passed in Update Incident Notification", null, false);
			return false;
		}
		
		for (int i=0; i < primaryFinCodeNL.getLength() && !obtPrimIndVal; i++){
			Node primaryFinCodeNode = primaryFinCodeNL.item(i);
			NodeList childNL = primaryFinCodeNode.getChildNodes();
			// childNL cannot be NULL as the schema mandates it to have child elements, 
			// so not checking childNL nullability
			for (int j=0; j < childNL.getLength() && !obtPrimIndVal; j++){
				Node primaryIndNode = childNL.item(j);
				// If primary indicator is not true, then the code will loop through the other
				// financial codes until it gets exhausted or a primary indicator value of true
				if (primaryIndNode.getNodeName().equalsIgnoreCase(NWCGConstants.INC_NOTIF_PRIMARYIND_NODE)){
					primaryIndVal = primaryIndNode.getTextContent();
					if (primaryIndVal.equalsIgnoreCase(NWCGConstants.STR_TRUE)){
						obtPrimIndVal = true;
					}
				}
			} // end of for (int j=0; j < childNL.getLength() && !obtPrimIndVal; j++){
		} // end of for (int i=0; i < primaryFinCodeNL.getLength() && !obtPrimIndVal; i++){

		// If Financial Code doesn't have a primary indicator value of true, then raise an alert
		if (!obtPrimIndVal){
			getDataAndRaiseAlert(env, doc, NWCGConstants.NOTIF_ERR_PRIM_FIN_CODE_NOT_PASSED_2, 
					"Primary Financial Code is not passed in Update Incident Notification", null, false);
			return false;
		}
		
		System.out.println("@@@@@ Exiting NWCGUpdateIncidentNotificationHandler::checkPrimaryFinCodeExistence @@@@@");	
		return primFinCodeExists;
	}

	/**
	 * This method checks if incident exists in ICBSR system. If it exists, check if 
	 * incident is registered in ROSS. Raise alert in all other situations
	 * @param env
	 * @param doc
	 * @return
	 */
	private boolean checkIfIncidentExistsAndItsRegisterStatus(YFSEnvironment env, Document doc) {
		System.out.println("@@@@@ Entering NWCGUpdateIncidentNotificationHandler::checkIfIncidentExistsAndItsRegisterStatus @@@@@");	
		
		boolean incStatus = false;
		try {
			Document nwcgIncDoc = XMLUtil.createDocument(NWCGConstants.INCIDENT_ORDER_ELEM);
			Element nwcgRootElm = nwcgIncDoc.getDocumentElement();
			nwcgRootElm.setAttribute(NWCGConstants.INCIDENT_NO_ATTR, incNo);
			nwcgRootElm.setAttribute(NWCGConstants.YEAR_ATTR, year);
			NWCGLoggerUtil.Log.finest("NWCGUpdateIncidentNotificationHandler::checkIncident, Input XML : " 
					+ XMLUtil.extractStringFromDocument(nwcgIncDoc));
			
			Document nwcgIncOP = null;
			try {
				nwcgIncOP = CommonUtilities.invokeService(env, NWCGConstants.SVC_GET_INCIDENT_ORDER_DTLS_ONLY_SVC, nwcgIncDoc);
			}
			catch (Exception e){
				NWCGLoggerUtil.Log.warning("NWCGUpdateIncidentNotificationHandler::checkIncident, " +
						"Exception while calling service : " + NWCGConstants.SVC_GET_INCIDENT_ORDER_DTLS_ONLY_SVC + 
								", Error Message : " + e.getMessage());
				NWCGLoggerUtil.printStackTraceToLog(e);
				String errDesc = "Incident " + incNo + " is not present in ICBSR. " +
						"Update Incident Notification for this incident is not processed";
				getDataAndRaiseAlert(env, doc, errDesc, "Incident " + incNo + " is not present in ICBSR.", null, false);
				return incStatus;
			}
			
			if (nwcgIncOP != null){
				//Check register status
				// if registered, incStatus = true;
				Element opRootElm = nwcgIncOP.getDocumentElement();
				String incRegStatus = opRootElm.getAttribute(NWCGConstants.INCIDENT_REG_INT_IN_ROSS_ATTR);
				if ((incRegStatus != null) && incRegStatus.equalsIgnoreCase(NWCGConstants.YES)){
					incStatus = true;
				}
				else {
					incStatus = false;
					String errDesc = "Incident " + incNo + " exists in ICBSR, but is not registered with ROSS. " +
							"Update Incident Notification for this incident is not processed";
					// Raise exception with message "Incident exists, but is not registered with ROSS....
					String desc = "Incident " + incNo + " exists in ICBSR, but is not registered with ROSS.";
					getDataAndRaiseAlert(env, doc, errDesc, desc, null, false);
				}
			}
			else {
				// Code should never come here. 
				// Code will come here if service returns NULL instead of exception (if incident doesn't exists) - This is the current Yantra behavior
				String errDesc = "Incident " + incNo + " is not present in ICBSR. Update Incident Notification for this incident is not processed";
				getDataAndRaiseAlert(env, doc, errDesc, "Incident " + incNo + " is not present in ICBSR.", null, false);
			}
		}
		catch (ParserConfigurationException pce){
			NWCGLoggerUtil.Log.warning("NWCGUpdateIncidentNotificationHandler::checkIncident, " +
					"ParserConfigurationException message : " + pce.getMessage());
			NWCGLoggerUtil.printStackTraceToLog(pce);
		}
		catch (Exception e){
			NWCGLoggerUtil.Log.warning("NWCGUpdateIncidentNotificationHandler::checkIncident, " +
					"Exception message : " + e.getMessage());
			NWCGLoggerUtil.printStackTraceToLog(e);			
		}		
		
		System.out.println("@@@@@ Exiting NWCGUpdateIncidentNotificationHandler::checkIfIncidentExistsAndItsRegisterStatus @@@@@");
		return incStatus;
	}
	
	/**
	 * This method parses the input xml and sets the incident number and 
	 * year to class level variables. Incident Number and Year are mandatory in the
	 * input xml. So, null check is not done for those values
	 * @param doc
	 */
	private void parseAndSetIncidentNumberAndYear(Document doc) {
		System.out.println("@@@@@ Entering NWCGUpdateIncidentNotificationHandler::parseAndSetIncidentNumberAndYear @@@@@");
		
		Element updtIncNotifRootElm = doc.getDocumentElement();
		//	Retrieve Source incident. Make a call to get incident details using output template
		NodeList incidentKeyNL = updtIncNotifRootElm.getElementsByTagName(NWCGConstants.INC_NOTIF_INCKEY_ATTR);
		// IncidentKey is a mandatory element and the root element will have only one occurence. 
		// Not checking for NULL. All the elements under incidentkey are
		// also mandatory, so not checking for NULL
		String seqNum = NWCGConstants.EMPTY_STRING;
		String unitIDPrefix = NWCGConstants.EMPTY_STRING;
		String unitIDSuffix = NWCGConstants.EMPTY_STRING;
		StringBuffer sbIncNo = new StringBuffer(NWCGConstants.EMPTY_STRING);
		Node incidentKeyNode = incidentKeyNL.item(0);
		NodeList naturalIncKeyNL = incidentKeyNode.getChildNodes();
		for (int incTrav=0; incTrav < naturalIncKeyNL.getLength(); incTrav++){
			Node naturalIncKeyNode = naturalIncKeyNL.item(incTrav);
			if (naturalIncKeyNode.getNodeName().equalsIgnoreCase(NWCGConstants.INC_NOTIF_NAT_INCKEY_ATTR)){
				NodeList incFieldsNL = naturalIncKeyNode.getChildNodes();
				for (int incFieldsTrav=0; incFieldsTrav < incFieldsNL.getLength(); incFieldsTrav++){
					Node incField = incFieldsNL.item(incFieldsTrav);
					String incFieldNodeName = incField.getNodeName();
					if (incFieldNodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_HOSTID_ATTR)){
						NodeList unitIDNL = incField.getChildNodes();
						for (int unitIDTrav=0; unitIDTrav < unitIDNL.getLength(); unitIDTrav++){
							Node unitIDNode = unitIDNL.item(unitIDTrav);
							String unitIDNodeName = unitIDNode.getNodeName();
							if (unitIDNodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_UNIT_ID_PREFIX_ATTR)){
								unitIDPrefix = unitIDNode.getTextContent();
							}
							else if (unitIDNodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_UNIT_ID_SUFFIX_ATTR)){
								unitIDSuffix = unitIDNode.getTextContent();
							}
						}
					}
					else if (incFieldNodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_SEQ_NO_ATTR)){
						seqNum = incField.getTextContent();
					}
					else if (incFieldNodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_YR_CREATED_ATTR)){
						year = incField.getTextContent();
					}
				} // end of for (int incFieldsTrav=0; incFieldsTrav < incFieldsNL.getLength(); incFieldsTrav++)
			} // if (naturanIncKeyNode == NaturalIncidentKey)
		} // end of for (int incTrav=0; incTrav < naturalIncKeyNL.getLength(); incTrav++){
		
		sbIncNo.append(unitIDPrefix).append("-").append(unitIDSuffix).append("-");
		int seqNumLen = 6;
		for (int i=0; i < seqNumLen - seqNum.length(); i++){
			sbIncNo.append("0");
		}
		incNo = sbIncNo.append(seqNum).toString();
		
		NWCGLoggerUtil.Log.finer("NWCGUpdateIncidentNotificationHandler::checkIncident, Incident Number : " + incNo + ", Year : " + year);
		System.out.println("@@@@@ Exiting NWCGUpdateIncidentNotificationHandler::parseAndSetIncidentNumberAndYear @@@@@");
	}
	
	/**
	 * This method gets the customer details based on class level
	 * variable custId 
	 * @param doc
	 * @return
	 */
	private Document getCustomerDetails(YFSEnvironment env, Document doc) {
		System.out.println("@@@@@ Entering NWCGUpdateIncidentNotificationHandler::getCustomerDetails @@@@@");
		
		Document customerDetailsDoc = null;
		try {
			Document customerInputDoc = XMLUtil.createDocument(NWCGConstants.CUST_CUST_ELEMENT);
			Element customerInputElm = customerInputDoc.getDocumentElement();
			customerInputElm.setAttribute(NWCGConstants.CUST_CUST_ID_ATTR, custId);
			// Hard code organization code to "NWCG" as this is the only enterprise code that we have
			// for USFS project
			customerInputElm.setAttribute(NWCGConstants.CUST_ORG_CODE_ATTR, NWCGConstants.ENTERPRISE_CODE);
			customerDetailsDoc = CommonUtilities.invokeAPI(env, NWCGConstants.TMPL_GET_CUST_DETAILS_TEMPLATE, 
														NWCGConstants.API_GET_CUST_DETAILS, customerInputDoc);
			if (customerDetailsDoc != null){
				NWCGLoggerUtil.Log.finer("NWCGUpdateIncidentNotificationHandler::getCustomerDetails, " +
						"Customer output : " + XMLUtil.extractStringFromDocument(customerDetailsDoc));
			}
		}
		catch (ParserConfigurationException pce){
			NWCGLoggerUtil.Log.warning("NWCGUpdateIncidentNotificationHandler::getCustomerDetails, " +
					"ParserConfigurationException Message : " + pce.getMessage());
			NWCGLoggerUtil.printStackTraceToLog(pce);
		}
		catch (Exception e){
			NWCGLoggerUtil.Log.warning("NWCGUpdateIncidentNotificationHandler::getCustomerDetails, " +
					"Exception Message : " + e.getMessage());
			NWCGLoggerUtil.printStackTraceToLog(e);
			// CHANGE IN REQUIREMENT - 08/18 - Sunjay
			// Do not raise an alert. Mention this in Missing Fields as part of Reference Section
			/*
			String errDesc = "Customer " + custId + " does not exist in ICBSR system. This notification is not processed";
			getDataAndRaiseAlert(env, doc, errDesc, "Customer " + custId + " does not exist in ICBSR system.", null, false);
			*/
			return null;
		}
		
		// If customer is not there, then Yantra will throw an exception. So, code will never go into this
		// if loop. If Yantra changes the way it is handling the response, then we will go through this code.
		if (customerDetailsDoc == null){
			//raise an alert mentioning that customer does not exists
			String errDesc = "Customer " + custId + " does not exist in ICBSR system. This notification is not processed";
			getDataAndRaiseAlert(env, doc, errDesc, "Customer " + custId + " does not exist in ICBSR system.", null, false);
			return null;
		}
		
		System.out.println("@@@@@ Exiting NWCGUpdateIncidentNotificationHandler::getCustomerDetails @@@@@");
		return customerDetailsDoc;
	}
	
	/** 
	 * This method parses through the input xml and sets the customer id to
	 * class level variable. This method gets the parameters from BillingOrganization element
	 * Puts it in the format of UnitIDPrefix + "-" + UnitIDSuffix.
	 * Customer ID is a mandatory element. So, null check is not done
	 * @param doc
	 */
	private void parseAndSetCustomerId(Document doc) {
		System.out.println("@@@@@ Entering NWCGUpdateIncidentNotificationHandler::parseAndSetCustomerId @@@@@");
		
		String unitIDPrefix = NWCGConstants.EMPTY_STRING;
		String unitIDSuffix = NWCGConstants.EMPTY_STRING;
		Element updtNotifRootElm = doc.getDocumentElement();
		NodeList billingOrgNL = updtNotifRootElm.getElementsByTagName(NWCGConstants.INC_NOTIF_BILLINGORG_ELEMENT);
		// There is only one occurence of BillingOrganization and it can have only one occurence 
		// and is a mandatory element
		Node billingOrgNode = billingOrgNL.item(0);
		NodeList unitIDNL = billingOrgNode.getChildNodes();
		for (int i=0; i < unitIDNL.getLength(); i++){
			Node unitIDNode = unitIDNL.item(i);
			String nodeName = unitIDNode.getNodeName();
			if (nodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_UNIT_ID_PREFIX_ATTR)){
				unitIDPrefix = unitIDNode.getTextContent();
			}
			else if (nodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_UNIT_ID_SUFFIX_ATTR)){
				unitIDSuffix = unitIDNode.getTextContent();
			}
		}
		
		NWCGLoggerUtil.Log.finer("NWCGUpdateIncidentNotificationHandler::getCustomerId, Unit ID Prefix : " + unitIDPrefix);
		NWCGLoggerUtil.Log.finer("NWCGUpdateIncidentNotificationHandler::getCustomerId, Unit ID Suffix : " + unitIDSuffix);
		custId = unitIDPrefix.concat(unitIDSuffix);
		NWCGLoggerUtil.Log.finer("NWCGUpdateIncidentNotificationHandler::getCustomerId, Customer ID : " + custId);
		
		System.out.println("@@@@@ Exiting NWCGUpdateIncidentNotificationHandler::parseAndSetCustomerId @@@@@");
	}
	
	/** 
	 * This method parses through the input xml and sets the Cache ID to
	 * class level variable. This method gets the parameters from CacheOrganization element
	 * Puts it in the format of UnitIDPrefix + UnitIDSuffix.
	 * This field will be used to retrieve the user id to which the alert needs to be assigned
	 * @param doc
	 */
	private void parseAndSetCacheId(Document doc) {
		System.out.println("@@@@@ Entering NWCGUpdateIncidentNotificationHandler::parseAndSetCacheId @@@@@");
		
		String unitIDPrefix = NWCGConstants.EMPTY_STRING;
		String unitIDSuffix = NWCGConstants.EMPTY_STRING;
		Element updtNotifRootElm = doc.getDocumentElement();
		NodeList cacheOrgNL = updtNotifRootElm.getElementsByTagName(NWCGConstants.INC_NOTIF_CACHEORG_ELEMENT);
		
		// While testing, we are not getting CacheOrganization element. So, added the logic of not null
		if (cacheOrgNL == null || cacheOrgNL.getLength() < 1){
			cacheId = null;
			return;
		}
		// There is only one occurence of CacheOrganization and it can have only one occurence 
		// and is a mandatory element
		Node cacheOrgNode = cacheOrgNL.item(0);
		NodeList unitIDNL = cacheOrgNode.getChildNodes();
		for (int i=0; i < unitIDNL.getLength(); i++){
			Node unitIDNode = unitIDNL.item(i);
			String nodeName = unitIDNode.getNodeName();
			if (nodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_UNIT_ID_PREFIX_ATTR)){
				unitIDPrefix = unitIDNode.getTextContent();
			}
			else if (nodeName.equalsIgnoreCase(NWCGConstants.INC_NOTIF_UNIT_ID_SUFFIX_ATTR)){
				unitIDSuffix = unitIDNode.getTextContent();
			}
		}
		
		cacheId = unitIDPrefix.concat(unitIDSuffix);
		NWCGLoggerUtil.Log.finer("NWCGUpdateIncidentNotificationHandler::getCacheId, CacheID : " + cacheId);
		
		System.out.println("@@@@@ Exiting NWCGUpdateIncidentNotificationHandler::parseAndSetCacheId @@@@@");
	}
	
	/**
	 * This method will be called on exceptions or on missing data while processing Update
	 * Incident Notification. It will raise an alert in NWCG_INCIDENT_FAILURE queue. It will
	 * get incident number, year and customer id based on class variable values
	 * @param env
	 * @param doc
	 * @param errDesc
	 * @return
	 */
	private boolean getDataAndRaiseAlert(YFSEnvironment env, Document doc, String detailDesc, String desc, String alertDesc, boolean succFail) {
		System.out.println("@@@@@ Entering NWCGUpdateIncidentNotificationHandler::getDataAndRaiseAlert @@@@@");
		
		NWCGLoggerUtil.Log.finer("NWCGUpdateIncidentNotificationHandler::getDataAndRaiseAlert, Entered for handling : " + detailDesc);
		if (incNo.length() < 2){
			parseAndSetIncidentNumberAndYear(doc);
		}
		
		if (custId.length() < 2){
			parseAndSetCustomerId(doc);
		}
		
		// If we already parsed for cache id and if it is not present, then we are setting cacheId to NULL.
		// This will ensure that we will not parse the document again for CacheId. By default, cacheId is set to empty string.
		// This check is done only cacheId and not for incident number and customer id as they are mandatory elements
		if (cacheId != null && cacheId.length() < 2){
			parseAndSetCacheId(doc);
		}
		// If CacheID is still NULL or cacheId's length is still less than 2, then get the cache ID
		// using incident number and year
		if ((cacheId == null) || (cacheId != null && cacheId.length() < 2)){
			cacheId = CommonUtilities.getShipNodeFromIncident(env, incNo, year);
		}
		
		String userId = CommonUtilities.getAdminUserForCache(env, cacheId);
		
		HashMap <String,String>hmap = new HashMap<String,String>();
		hmap.put(NWCGConstants.ALERT_INCIDENT_NO, incNo);
		hmap.put(NWCGConstants.ALERT_YEAR, year);
		hmap.put(NWCGConstants.ALERT_CUST_ID, custId);
		hmap.put(NWCGConstants.ALERT_SOAP_MESSAGE, doc.getDocumentElement().getAttribute(NWCGConstants.INC_NOTIF_MSGNAME_ATTR));
		hmap.put(NWCGConstants.ALERT_DIST_ID, distId);
		hmap.put(NWCGConstants.ALERT_SHIPNODE_KEY, cacheId);
		hmap.put(NWCGConstants.ALERT_DESC, desc);
		if (alertDesc != null){
			hmap.put("Missing Fields", alertDesc);
		}
		
		String alertQ = NWCGConstants.EMPTY_STRING;
		if (succFail){
			alertQ = NWCGConstants.NWCG_INCIDENT_SUCCESS;
		}
		else {
			alertQ = NWCGConstants.NWCG_INCIDENT_FAILURE;
		}
		CommonUtilities.raiseAlertAndAssigntoUser(env, alertQ, detailDesc, userId, doc, hmap);
		
		System.out.println("@@@@@ Exiting NWCGUpdateIncidentNotificationHandler::getDataAndRaiseAlert @@@@@");
		return false;
	}
	
	/**
	 * This method checks for all the mandatory params required for creating an incident.
	 * I am putting this fields by getting the NON NULLS from the NWCG_INCIDENT_ORDER table
	 * and which doesn't have a default value of space -- Validate this
	 * @param incDoc
	 * @return
	 */
	private String checkMandatoryParams (Document incDoc, String alertDesc, boolean isValidCustId) {
		System.out.println("@@@@@ Entering NWCGUpdateIncidentNotificationHandler::checkMandatoryParams @@@@@");
		
		String desc = null;
		StringBuffer sbAlertDesc = new StringBuffer();
		Element elm = incDoc.getDocumentElement();
		// Primary Cache ID can be blank
		if (!isValidCustId){
			sbAlertDesc.append("Incident has INVALID Customer ID"); 
		}
		
		String primCacheId = elm.getAttribute("PrimaryCacheId");
		if (primCacheId == null || primCacheId.length() < 2){
			if (sbAlertDesc.length() > 1){
				sbAlertDesc.append("\n");
			}
			sbAlertDesc.append("Incident does not have Primary Cache ID");
		}
		
		String reqBlockStart = elm.getAttribute("RequestNoBlockStart");
		String reqBlockEnd = elm.getAttribute("RequestNoBlockEnd");
		if ((reqBlockStart == null || reqBlockStart.length() < 1) ||
			(reqBlockEnd == null || reqBlockEnd.length() < 1)){
			if (sbAlertDesc.length() > 1){
				sbAlertDesc.append("\n");
			}
			sbAlertDesc.append("Incident does not have Request Block"); 
		}
		
		alertDesc = sbAlertDesc.toString();
		
		System.out.println("@@@@@ Exiting NWCGUpdateIncidentNotificationHandler::checkMandatoryParams @@@@@");
		return desc;
	}
}