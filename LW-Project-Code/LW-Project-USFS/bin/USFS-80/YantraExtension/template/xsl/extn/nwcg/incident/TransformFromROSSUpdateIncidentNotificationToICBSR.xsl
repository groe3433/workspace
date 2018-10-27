<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:nwcg="http://nwcg.gov/services/ross/common_types/1.1" xmlns:ron="http://nwcg.gov/services/ross/resource_order_notification/1.1">
	<xsl:output indent="yes"/>
	<xsl:template match="/">
		<xsl:variable name="uppercase">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
		<xsl:variable name="lowercase">abcdefghijklmnopqrstuvwxyz</xsl:variable>
		<xsl:element name="NWCGIncidentOrder">
			<xsl:attribute name="IncidentSource">R</xsl:attribute>
			<xsl:attribute name="CustomerId"><xsl:value-of select="//ron:Incident/IncidentDetails/BillingOrganization/UnitIDPrefix"/><xsl:value-of select="//ron:Incident/IncidentDetails/BillingOrganization/UnitIDSuffix"/></xsl:attribute>
			<xsl:attribute name="DateStarted"><xsl:value-of select="//ron:Incident/IncidentDetails/InitialDateTime"/></xsl:attribute>
			<xsl:attribute name="IncidentHost"><xsl:value-of select="//ron:Incident/IncidentKey/NaturalIncidentKey/HostID/UnitIDPrefix"/><xsl:value-of select="//ron:Incident/IncidentKey/NaturalIncidentKey/HostID/UnitIDSuffix"/></xsl:attribute>
			<xsl:variable name="incName"><xsl:value-of select="//ron:Incident/IncidentDetails/IncidentName"/></xsl:variable>
			<xsl:attribute name="IncidentName">
				<xsl:value-of select="translate($incName, $lowercase, $uppercase)"/>
			</xsl:attribute>
			<xsl:attribute name="IncidentNo"><xsl:value-of select="//ron:Incident/IncidentKey/NaturalIncidentKey/HostID/UnitIDPrefix"/>-<xsl:value-of select="//ron:Incident/IncidentKey/NaturalIncidentKey/HostID/UnitIDSuffix"/>-<xsl:value-of select="//ron:Incident/IncidentKey/NaturalIncidentKey/SequenceNumber"/></xsl:attribute>
			<xsl:attribute name="IncidentType"><xsl:value-of select="//ron:Incident/IncidentDetails/IncidentType"/></xsl:attribute>
			<xsl:attribute name="IncidentId"><xsl:value-of select="//ron:Incident/IncidentKey/IncidentID/EntityID"/></xsl:attribute>
			<xsl:attribute name="LastUpdatedFromROSS"><xsl:value-of select="//NotificationBase/TimeOfNotification"/></xsl:attribute>
			<xsl:attribute name="ROSSDispatchID"><xsl:value-of select="//ron:Incident/IncidentDetails/DispatchOrganization/UnitIDPrefix"/>-<xsl:value-of select="//ron:Incident/IncidentDetails/DispatchOrganization/UnitIDSuffix"/></xsl:attribute>
			<xsl:attribute name="PrimaryCacheId"><xsl:value-of select="//ron:Incident/IncidentDetails/CacheOrganization/UnitIDPrefix"/><xsl:value-of select="//ron:Incident/IncidentDetails/CacheOrganization/UnitIDSuffix"/></xsl:attribute>
			<xsl:for-each select="//ron:Incident/IncidentEntities/IncidentFinancialCodes">
				<xsl:variable name="isPrimary">
					<xsl:value-of select="translate(PrimaryInd, $lowercase, $uppercase)"/>
				</xsl:variable>
				<xsl:if test="count(PrimaryInd)=0 ">
					<xsl:attribute name="ROSSFinancialCode"><xsl:value-of select="FinancialCode/Code"/></xsl:attribute>
					<xsl:attribute name="ROSSFinancialFiscalYear"><xsl:value-of select="FinancialCode/FiscalYear"/></xsl:attribute>
				</xsl:if>
				<xsl:if test="$isPrimary = 'FALSE' ">
					<xsl:attribute name="ROSSFinancialCode"><xsl:value-of select="FinancialCode/Code"/></xsl:attribute>
					<xsl:attribute name="ROSSFinancialFiscalYear"><xsl:value-of select="FinancialCode/FiscalYear"/></xsl:attribute>
				</xsl:if>
				<xsl:if test="$isPrimary = 'TRUE' ">
					<xsl:attribute name="ROSSFinancialCode"><xsl:value-of select="FinancialCode/Code"/></xsl:attribute>
					<xsl:attribute name="ROSSFinancialFiscalYear"><xsl:value-of select="FinancialCode/FiscalYear"/></xsl:attribute>
				</xsl:if>
			</xsl:for-each>
			<xsl:attribute name="ROSSIncidentStatus"><xsl:value-of select="//ron:Incident/IncidentDetails/Status"/></xsl:attribute>
			<xsl:for-each select="//ron:Incident/IncidentEntities/IncidentRequestBlocks">
				<xsl:variable name="SystemType">
					<xsl:value-of select="ForUseByAppSystem/SystemType"/>
				</xsl:variable>
				<xsl:variable name="startBlockNum">
					<xsl:value-of select="StartNumber"/>
				</xsl:variable>
				<xsl:variable name="endBlockNum">
					<xsl:value-of select="EndNumber"/>
				</xsl:variable>
				<xsl:if test="translate($SystemType , $lowercase, $uppercase) = 'ICBS'">
					<xsl:attribute name="RequestNoBlockStart"><xsl:value-of select="$startBlockNum"/></xsl:attribute>
					<xsl:attribute name="RequestNoBlockEnd"><xsl:value-of select="$endBlockNum"/></xsl:attribute>
				</xsl:if>
			</xsl:for-each>
			<xsl:variable name="LatDegreeVal">
				<xsl:value-of select="//ron:Incident/IncidentDetails/IncidentLocation/GeographicCoordinate/GeographicCoordinateLatitude/LatitudeDegreeValue"/>
			</xsl:variable>
			<xsl:variable name="LatMinuteVal">
				<xsl:value-of select="//ron:Incident/IncidentDetails/IncidentLocation/GeographicCoordinate/GeographicCoordinateLatitude/LatitudeMinuteValue"/>
			</xsl:variable>
			<xsl:variable name="LatSecondVal">
				<xsl:value-of select="//ron:Incident/IncidentDetails/IncidentLocation/GeographicCoordinate/GeographicCoordinateLatitude/LatitudeSecondValue"/>
			</xsl:variable>
			<xsl:variable name="LatDirection">
				<xsl:value-of select="//ron:Incident/IncidentDetails/IncidentLocation/GeographicCoordinate/GeographicCoordinateLatitude/LatitudeDirection"/>
			</xsl:variable>
			<xsl:attribute name="GeographicCoordinateLatitude"><xsl:value-of select="concat($LatDegreeVal, ',', $LatMinuteVal, ',', $LatSecondVal, ',', $LatDirection)"/></xsl:attribute>
			<xsl:variable name="LonDegreeVal">
				<xsl:value-of select="//ron:Incident/IncidentDetails/IncidentLocation/GeographicCoordinate/GeographicCoordinateLongitude/LongitudeDegreeValue"/>
			</xsl:variable>
			<xsl:variable name="LonMinuteVal">
				<xsl:value-of select="//ron:Incident/IncidentDetails/IncidentLocation/GeographicCoordinate/GeographicCoordinateLongitude/LongitudeMinuteValue"/>
			</xsl:variable>
			<xsl:variable name="LonSecondVal">
				<xsl:value-of select="//ron:Incident/IncidentDetails/IncidentLocation/GeographicCoordinate/GeographicCoordinateLongitude/LongitudeSecondValue"/>
			</xsl:variable>
			<xsl:variable name="LonDirection">
				<xsl:value-of select="//ron:Incident/IncidentDetails/IncidentLocation/GeographicCoordinate/GeographicCoordinateLongitude/LongitudeDirection"/>
			</xsl:variable>
			<xsl:attribute name="GeographicCoordinateLongitude"><xsl:value-of select="concat($LonDegreeVal, ',', $LonMinuteVal, ',', $LonSecondVal, ',', $LonDirection)"/></xsl:attribute>
			<xsl:attribute name="IsComplexIndicator"><xsl:variable name="isComplex" select="//ron:Incident/IncidentDetails/isComplexIndicator"/><xsl:choose><xsl:when test="translate($isComplex, $lowercase, $uppercase) = 'FALSE' "><xsl:text>N</xsl:text></xsl:when><xsl:otherwise><xsl:if test="translate($isComplex, $lowercase, $uppercase) = 'TRUE' "><xsl:text>Y</xsl:text></xsl:if></xsl:otherwise></xsl:choose></xsl:attribute>
			<xsl:attribute name="Year"><xsl:value-of select="//ron:Incident/IncidentKey/NaturalIncidentKey/YearCreated"/></xsl:attribute>
			<xsl:for-each select="//ron:Incident/IncidentEntities/IncidentAddresses">
				<xsl:variable name="typeVar" select="Address/Type" />
				<xsl:variable name="addressType">
					<xsl:value-of select="translate($typeVar, $lowercase, $uppercase)"/>
				</xsl:variable>
				<xsl:variable name="isPrimary">
					<xsl:value-of select="translate(PrimaryInd, $lowercase, $uppercase)"/>
				</xsl:variable>
				<xsl:if test="$addressType = 'MAILING' and count(PrimaryInd)=0">
					<xsl:element name="YFSPersonInfoDeliverTo">
						<xsl:attribute name="AlternateEmailID">
							<xsl:value-of select="Address/UnitID/UnitIDPrefix"/><xsl:value-of select="Address/UnitID/UnitIDSuffix"/>
						</xsl:attribute>
						<xsl:attribute name="HttpUrl"><xsl:value-of select="Address/Name"/></xsl:attribute>
						<xsl:attribute name="AddressLine1"><xsl:value-of select="Address/Line1"/></xsl:attribute>
						<xsl:attribute name="AddressLine2"><xsl:value-of select="Address/Line2"/></xsl:attribute>
						<xsl:attribute name="City"><xsl:value-of select="Address/City"/></xsl:attribute>
						<xsl:attribute name="Country">
							<xsl:variable name="isCountryUSA">
								<xsl:value-of select="Address/CountryCode"/>
							</xsl:variable>
							<xsl:choose>
								<xsl:when test="$isCountryUSA='USA'">US</xsl:when>
								<xsl:otherwise><xsl:value-of select="$isCountryUSA"/></xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="State"><xsl:value-of select="Address/State"/></xsl:attribute>
						<xsl:attribute name="ZipCode"><xsl:value-of select="Address/ZipCode"/></xsl:attribute>
					</xsl:element>
				</xsl:if>
				<xsl:if test="$addressType = 'MAILING' and $isPrimary = 'FALSE' ">
					<xsl:element name="YFSPersonInfoDeliverTo">
						<xsl:attribute name="AlternateEmailID">
							<xsl:value-of select="Address/UnitID/UnitIDPrefix"/><xsl:value-of select="Address/UnitID/UnitIDSuffix"/>
						</xsl:attribute>
						<xsl:attribute name="HttpUrl"><xsl:value-of select="Address/Name"/></xsl:attribute>
						<xsl:attribute name="AddressLine1"><xsl:value-of select="Address/Line1"/></xsl:attribute>
						<xsl:attribute name="AddressLine2"><xsl:value-of select="Address/Line2"/></xsl:attribute>
						<xsl:attribute name="City"><xsl:value-of select="Address/City"/></xsl:attribute>
						<xsl:attribute name="Country">
							<xsl:variable name="isCountryUSA">
								<xsl:value-of select="Address/CountryCode"/>
							</xsl:variable>
							<xsl:choose>
								<xsl:when test="$isCountryUSA='USA'">US</xsl:when>
								<xsl:otherwise><xsl:value-of select="$isCountryUSA"/></xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="State"><xsl:value-of select="Address/State"/></xsl:attribute>
						<xsl:attribute name="ZipCode"><xsl:value-of select="Address/ZipCode"/></xsl:attribute>
						<xsl:attribute name="PrimaryInd"><xsl:value-of select="PrimaryInd"/></xsl:attribute>
					</xsl:element>
				</xsl:if>
				<xsl:if test="$addressType = 'MAILING' and $isPrimary = 'TRUE' ">
					<xsl:element name="YFSPersonInfoDeliverTo">
						<xsl:attribute name="AlternateEmailID">
							<xsl:value-of select="Address/UnitID/UnitIDPrefix"/><xsl:value-of select="Address/UnitID/UnitIDSuffix"/>
						</xsl:attribute>
						<xsl:attribute name="HttpUrl"><xsl:value-of select="Address/Name"/></xsl:attribute>
						<xsl:attribute name="AddressLine1"><xsl:value-of select="Address/Line1"/></xsl:attribute>
						<xsl:attribute name="AddressLine2"><xsl:value-of select="Address/Line2"/></xsl:attribute>
						<xsl:attribute name="City"><xsl:value-of select="Address/City"/></xsl:attribute>
						<xsl:attribute name="Country">
							<xsl:variable name="isCountryUSA">
								<xsl:value-of select="Address/CountryCode"/>
							</xsl:variable>
							<xsl:choose>
								<xsl:when test="$isCountryUSA='USA'">US</xsl:when>
								<xsl:otherwise><xsl:value-of select="$isCountryUSA"/></xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="State"><xsl:value-of select="Address/State"/></xsl:attribute>
						<xsl:attribute name="ZipCode"><xsl:value-of select="Address/ZipCode"/></xsl:attribute>
						<xsl:attribute name="PrimaryInd"><xsl:value-of select="PrimaryInd"/></xsl:attribute>
					</xsl:element>
				</xsl:if>
				<xsl:if test="$addressType = 'SHIPPING' and count(PrimaryInd)=0">
					<xsl:element name="YFSPersonInfoShipTo">
						<xsl:attribute name="AlternateEmailID">
							<xsl:value-of select="Address/UnitID/UnitIDPrefix"/><xsl:value-of select="Address/UnitID/UnitIDSuffix"/>
						</xsl:attribute>
						<xsl:attribute name="HttpUrl"><xsl:value-of select="Address/Name"/></xsl:attribute>
						<xsl:attribute name="AddressLine1"><xsl:value-of select="Address/Line1"/></xsl:attribute>
						<xsl:attribute name="AddressLine2"><xsl:value-of select="Address/Line2"/></xsl:attribute>
						<xsl:attribute name="City"><xsl:value-of select="Address/City"/></xsl:attribute>
						<xsl:attribute name="Country">
							<xsl:variable name="isCountryUSA">
								<xsl:value-of select="Address/CountryCode"/>
							</xsl:variable>
							<xsl:choose>
								<xsl:when test="$isCountryUSA='USA'">US</xsl:when>
								<xsl:otherwise><xsl:value-of select="$isCountryUSA"/></xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="State"><xsl:value-of select="Address/State"/></xsl:attribute>
						<xsl:attribute name="ZipCode"><xsl:value-of select="Address/ZipCode"/></xsl:attribute>
					</xsl:element>
				</xsl:if>
				<xsl:if test="$addressType = 'SHIPPING' and $isPrimary = 'FALSE' ">
					<xsl:element name="YFSPersonInfoShipTo">
						<xsl:attribute name="AlternateEmailID">
							<xsl:value-of select="Address/UnitID/UnitIDPrefix"/><xsl:value-of select="Address/UnitID/UnitIDSuffix"/>
						</xsl:attribute>
						<xsl:attribute name="HttpUrl"><xsl:value-of select="Address/Name"/></xsl:attribute>
						<xsl:attribute name="AddressLine1"><xsl:value-of select="Address/Line1"/></xsl:attribute>
						<xsl:attribute name="AddressLine2"><xsl:value-of select="Address/Line2"/></xsl:attribute>
						<xsl:attribute name="City"><xsl:value-of select="Address/City"/></xsl:attribute>
						<xsl:attribute name="Country">
							<xsl:variable name="isCountryUSA">
								<xsl:value-of select="Address/CountryCode"/>
							</xsl:variable>
							<xsl:choose>
								<xsl:when test="$isCountryUSA='USA'">US</xsl:when>
								<xsl:otherwise><xsl:value-of select="$isCountryUSA"/></xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="State"><xsl:value-of select="Address/State"/></xsl:attribute>
						<xsl:attribute name="ZipCode"><xsl:value-of select="Address/ZipCode"/></xsl:attribute>
						<xsl:attribute name="PrimaryInd"><xsl:value-of select="PrimaryInd"/></xsl:attribute>
					</xsl:element>
				</xsl:if>
				<xsl:if test="$addressType = 'SHIPPING' and $isPrimary = 'TRUE' ">
					<xsl:element name="YFSPersonInfoShipTo">
						<xsl:attribute name="AlternateEmailID">
							<xsl:value-of select="Address/UnitID/UnitIDPrefix"/><xsl:value-of select="Address/UnitID/UnitIDSuffix"/>
						</xsl:attribute>
						<xsl:attribute name="HttpUrl"><xsl:value-of select="Address/Name"/></xsl:attribute>
						<xsl:attribute name="AddressLine1"><xsl:value-of select="Address/Line1"/></xsl:attribute>
						<xsl:attribute name="AddressLine2"><xsl:value-of select="Address/Line2"/></xsl:attribute>
						<xsl:attribute name="City"><xsl:value-of select="Address/City"/></xsl:attribute>
						<xsl:attribute name="Country">
							<xsl:variable name="isCountryUSA">
								<xsl:value-of select="Address/CountryCode"/>
							</xsl:variable>
							<xsl:choose>
								<xsl:when test="$isCountryUSA='USA'">US</xsl:when>
								<xsl:otherwise><xsl:value-of select="$isCountryUSA"/></xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="State"><xsl:value-of select="Address/State"/></xsl:attribute>
						<xsl:attribute name="ZipCode"><xsl:value-of select="Address/ZipCode"/></xsl:attribute>
						<xsl:attribute name="PrimaryInd"><xsl:value-of select="PrimaryInd"/></xsl:attribute>
					</xsl:element>
				</xsl:if>
				<xsl:if test="$addressType = 'OFFICIAL' and count(PrimaryInd)=0">
					<xsl:element name="YFSPersonInfoBillTo">
						<xsl:attribute name="AlternateEmailID">
							<xsl:value-of select="Address/UnitID/UnitIDPrefix"/><xsl:value-of select="Address/UnitID/UnitIDSuffix"/>
						</xsl:attribute>
						<xsl:attribute name="HttpUrl"><xsl:value-of select="Address/Name"/></xsl:attribute>
						<xsl:attribute name="AddressLine1"><xsl:value-of select="Address/Line1"/></xsl:attribute>
						<xsl:attribute name="AddressLine2"><xsl:value-of select="Address/Line2"/></xsl:attribute>
						<xsl:attribute name="City"><xsl:value-of select="Address/City"/></xsl:attribute>
						<xsl:attribute name="Country">
							<xsl:variable name="isCountryUSA">
								<xsl:value-of select="Address/CountryCode"/>
							</xsl:variable>
							<xsl:choose>
								<xsl:when test="$isCountryUSA='USA'">US</xsl:when>
								<xsl:otherwise><xsl:value-of select="$isCountryUSA"/></xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="State"><xsl:value-of select="Address/State"/></xsl:attribute>
						<xsl:attribute name="ZipCode"><xsl:value-of select="Address/ZipCode"/></xsl:attribute>
					</xsl:element>
				</xsl:if>
				<xsl:if test="$addressType = 'OFFICIAL' and $isPrimary = 'FALSE' ">
					<xsl:element name="YFSPersonInfoBillTo">
						<xsl:attribute name="AlternateEmailID">
							<xsl:value-of select="Address/UnitID/UnitIDPrefix"/><xsl:value-of select="Address/UnitID/UnitIDSuffix"/>
						</xsl:attribute>
						<xsl:attribute name="HttpUrl"><xsl:value-of select="Address/Name"/></xsl:attribute>
						<xsl:attribute name="AddressLine1"><xsl:value-of select="Address/Line1"/></xsl:attribute>
						<xsl:attribute name="AddressLine2"><xsl:value-of select="Address/Line2"/></xsl:attribute>
						<xsl:attribute name="City"><xsl:value-of select="Address/City"/></xsl:attribute>
						<xsl:attribute name="Country">
							<xsl:variable name="isCountryUSA">
								<xsl:value-of select="Address/CountryCode"/>
							</xsl:variable>
							<xsl:choose>
								<xsl:when test="$isCountryUSA='USA'">US</xsl:when>
								<xsl:otherwise><xsl:value-of select="$isCountryUSA"/></xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="State"><xsl:value-of select="Address/State"/></xsl:attribute>
						<xsl:attribute name="ZipCode"><xsl:value-of select="Address/ZipCode"/></xsl:attribute>
						<xsl:attribute name="PrimaryInd"><xsl:value-of select="PrimaryInd"/></xsl:attribute>
					</xsl:element>
				</xsl:if>
				<xsl:if test="$addressType = 'OFFICIAL' and $isPrimary = 'TRUE' ">
					<xsl:element name="YFSPersonInfoBillTo">
						<xsl:attribute name="AlternateEmailID">
							<xsl:value-of select="Address/UnitID/UnitIDPrefix"/><xsl:value-of select="Address/UnitID/UnitIDSuffix"/>
						</xsl:attribute>
						<xsl:attribute name="HttpUrl"><xsl:value-of select="Address/Name"/></xsl:attribute>
						<xsl:attribute name="AddressLine1"><xsl:value-of select="Address/Line1"/></xsl:attribute>
						<xsl:attribute name="AddressLine2"><xsl:value-of select="Address/Line2"/></xsl:attribute>
						<xsl:attribute name="City"><xsl:value-of select="Address/City"/></xsl:attribute>
						<xsl:attribute name="Country">
							<xsl:variable name="isCountryUSA">
								<xsl:value-of select="Address/CountryCode"/>
							</xsl:variable>
							<xsl:choose>
								<xsl:when test="$isCountryUSA='USA'">US</xsl:when>
								<xsl:otherwise><xsl:value-of select="$isCountryUSA"/></xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="State"><xsl:value-of select="Address/State"/></xsl:attribute>
						<xsl:attribute name="ZipCode"><xsl:value-of select="Address/ZipCode"/></xsl:attribute>
						<xsl:attribute name="PrimaryInd"><xsl:value-of select="PrimaryInd"/></xsl:attribute>
					</xsl:element>
				</xsl:if>
			</xsl:for-each>
			<xsl:element name="NWCGIncidentOrderConInfoList">
				<xsl:for-each select="//ron:Incident/IncidentEntities/IncidentContacts">
						<xsl:element name="NWCGIncidentOrderConInfo">
							<xsl:attribute name="IncidentOrderKey"></xsl:attribute>
							<xsl:attribute name="Contact"><xsl:value-of select="Name"/></xsl:attribute>
							<xsl:attribute name="ContactInfo"><xsl:value-of select="Information"/></xsl:attribute>
							<xsl:attribute name="ContactType"><xsl:value-of select="Type"/></xsl:attribute>
						</xsl:element>
				</xsl:for-each>
			</xsl:element>
			<xsl:element name="NWCGRossAccountCodesList">
					<xsl:for-each select="//ron:Incident/IncidentEntities/IncidentFinancialCodes">
						<xsl:element name="NWCGRossAccountCodes">
							<xsl:attribute name="IncidentOrderKey"></xsl:attribute>
							<xsl:attribute name="FinancialCode"><xsl:value-of select="FinancialCode/Code"/></xsl:attribute>
							<xsl:attribute name="FiscalYear"><xsl:value-of select="FinancialCode/FiscalYear"/></xsl:attribute>
							<xsl:attribute name="OwningAgencyName"><xsl:value-of select="FinancialCode/OwningAgencyName"/></xsl:attribute>
							<xsl:attribute name="PrimaryIndicator"><xsl:value-of select="PrimaryInd"/></xsl:attribute>
						</xsl:element>
					</xsl:for-each>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
