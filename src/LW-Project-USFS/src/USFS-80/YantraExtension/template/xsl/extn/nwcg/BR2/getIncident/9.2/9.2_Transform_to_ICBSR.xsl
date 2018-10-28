<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:cat="http://nwcg.gov/services/ross/catalog/1.1" xmlns:nwcg="http://nwcg.gov/services/ross/common_types/1.1" xmlns:ro="http://nwcg.gov/services/ross/resource_order/1.1" xmlns:rsc="http://nwcg.gov/services/ross/resource/1.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<xsl:output indent="yes"/>
	<xsl:template match="/">
		<xsl:variable name="uppercase">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
		<xsl:variable name="lowercase">abcdefghijklmnopqrstuvwxyz</xsl:variable>
		<xsl:element name="NWCGIncidentOrder">
			<xsl:attribute name="CustomerId"><xsl:value-of select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentDetails/BillingOrganization/UnitIDPrefix"/>-<xsl:value-of select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentDetails/BillingOrganization/UnitIDSuffix"/></xsl:attribute>
			<xsl:attribute name="DateStarted"><xsl:value-of select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentDetails/InitialDateTime"/></xsl:attribute>
			<xsl:attribute name="IncidentAction"><xsl:value-of select="/MergedDocument/EnvironmentDocument/NWCGIncidentOrder/@IncidentAction"/></xsl:attribute>
			<xsl:attribute name="IncidentHost"><xsl:value-of select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentKey/NaturalIncidentKey/HostID/UnitIDPrefix"/>-<xsl:value-of select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentKey/NaturalIncidentKey/HostID/UnitIDSuffix"/></xsl:attribute>
			<xsl:attribute name="IncidentName"><xsl:value-of select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentDetails/IncidentName"/></xsl:attribute>
			<xsl:attribute name="IncidentNo"><xsl:value-of select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentKey/NaturalIncidentKey/HostID/UnitIDPrefix"/>-<xsl:value-of select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentKey/NaturalIncidentKey/HostID/UnitIDSuffix"/>-<xsl:value-of select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentKey/NaturalIncidentKey/SequenceNumber"/></xsl:attribute>
			<xsl:attribute name="IncidentType"><xsl:value-of select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentDetails/IncidentType"/></xsl:attribute>
			<xsl:attribute name="ROSSDispatchID"><xsl:value-of select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentDetails/DispatchOrganization/UnitIDPrefix"/>-<xsl:value-of select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentDetails/DispatchOrganization/UnitIDSuffix"/></xsl:attribute>
			<xsl:for-each select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentEntities/FinancialCodes/FinancialCode">
				<xsl:variable name="isPrimary">
					<xsl:value-of select="translate(PrimaryInd, $lowercase, $uppercase)"/>
				</xsl:variable>
				<xsl:if test="OwningAgencyName = 'ICBS' and count(PrimaryInd)=0 ">
					<xsl:attribute name="ROSSFinancialCode"><xsl:value-of select="FinancialCode"/></xsl:attribute>
					<xsl:attribute name="ROSSFinancialFiscalYear"><xsl:value-of select="FiscalYear"/></xsl:attribute>
				</xsl:if>
				<xsl:if test="OwningAgencyName = 'ICBS' and $isPrimary = 'TRUE' ">
					<xsl:attribute name="ROSSFinancialCode"><xsl:value-of select="FinancialCode"/></xsl:attribute>
					<xsl:attribute name="ROSSFinancialFiscalYear"><xsl:value-of select="FiscalYear"/></xsl:attribute>
				</xsl:if>
			</xsl:for-each>
			<xsl:attribute name="ROSSIncidentStatus"><xsl:value-of select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentDetails/Status"/></xsl:attribute>
			<xsl:for-each select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentEntities/IncidentRequestBlocks">
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
				<xsl:value-of select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentDetails/IncidentLocation/GeographicCoordinate/GeographicCoordinateLatitude/LatitudeDegreeValue"/>
			</xsl:variable>
			<xsl:variable name="LatMinuteVal">
				<xsl:value-of select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentDetails/IncidentLocation/GeographicCoordinate/GeographicCoordinateLatitude/LatitudeMinuteValue"/>
			</xsl:variable>
			<xsl:variable name="LatSecondVal">
				<xsl:value-of select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentDetails/IncidentLocation/GeographicCoordinate/GeographicCoordinateLatitude/LatitudeSecondValue"/>
			</xsl:variable>
			<xsl:variable name="LatDirection">
				<xsl:value-of select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentDetails/IncidentLocation/GeographicCoordinate/GeographicCoordinateLatitude/LatitudeDirection"/>
			</xsl:variable>
			<xsl:attribute name="GeographicCoordinateLatitude"><xsl:value-of select="concat($LatDegreeVal, ',', $LatMinuteVal, ',', $LatSecondVal, ',', $LatDirection)"/></xsl:attribute>
			<xsl:variable name="LonDegreeVal">
				<xsl:value-of select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentDetails/IncidentLocation/GeographicCoordinate/GeographicCoordinateLongitude/LongitudeDegreeValue"/>
			</xsl:variable>
			<xsl:variable name="LonMinuteVal">
				<xsl:value-of select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentDetails/IncidentLocation/GeographicCoordinate/GeographicCoordinateLongitude/LongitudeMinuteValue"/>
			</xsl:variable>
			<xsl:variable name="LonSecondVal">
				<xsl:value-of select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentDetails/IncidentLocation/GeographicCoordinate/GeographicCoordinateLongitude/LongitudeSecondValue"/>
			</xsl:variable>
			<xsl:variable name="LonDirection">
				<xsl:value-of select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentDetails/IncidentLocation/GeographicCoordinate/GeographicCoordinateLongitude/LongitudeDirection"/>
			</xsl:variable>
			<xsl:attribute name="GeographicCoordinateLongitude"><xsl:value-of select="concat($LonDegreeVal, ',', $LonMinuteVal, ',', $LonSecondVal, ',', $LonDirection)"/></xsl:attribute>
			<xsl:attribute name="IsComplexIndicator"><xsl:variable name="isComplex" select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentDetails/isComplexIndicator"/><xsl:choose><xsl:when test="translate($isComplex, $lowercase, $uppercase) = 'FALSE' "><xsl:text>N</xsl:text></xsl:when><xsl:otherwise><xsl:if test="translate($isComplex, $lowercase, $uppercase) = 'TRUE' "><xsl:text>Y</xsl:text></xsl:if></xsl:otherwise></xsl:choose></xsl:attribute>
			<xsl:attribute name="Year"><xsl:value-of select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentKey/NaturalIncidentKey/YearCreated"/></xsl:attribute>
			<xsl:for-each select="/MergedDocument/InputDocument/ro:GetIncidentResp/ro:Incident/IncidentEntities/IncidentAddresses/Address">
				<xsl:variable name="addressType">
					<xsl:value-of select="translate(Type, $lowercase, $uppercase)"/>
				</xsl:variable>
				<xsl:variable name="isPrimary">
					<xsl:value-of select="translate(PrimaryInd, $lowercase, $uppercase)"/>
				</xsl:variable>
				<xsl:if test="$addressType = 'MAILING' and count(PrimaryInd)=0">
					<xsl:element name="YFSPersonInfoShipTo">
						<xsl:attribute name="AddressLine1"><xsl:value-of select="Line1"/></xsl:attribute>
						<xsl:attribute name="AddressLine2"><xsl:value-of select="Line2"/></xsl:attribute>
						<xsl:attribute name="City"><xsl:value-of select="City"/></xsl:attribute>
						<xsl:attribute name="Country"><xsl:value-of select="CountryCode"/></xsl:attribute>
						<xsl:attribute name="State"><xsl:value-of select="State"/></xsl:attribute>
						<xsl:attribute name="ZipCode"><xsl:value-of select="ZipCode"/></xsl:attribute>
					</xsl:element>
				</xsl:if>
				<xsl:if test="$addressType = 'MAILING' and $isPrimary = 'TRUE' ">
					<xsl:element name="YFSPersonInfoShipTo">
						<xsl:attribute name="AddressLine1"><xsl:value-of select="Line1"/></xsl:attribute>
						<xsl:attribute name="AddressLine2"><xsl:value-of select="Line2"/></xsl:attribute>
						<xsl:attribute name="City"><xsl:value-of select="City"/></xsl:attribute>
						<xsl:attribute name="Country"><xsl:value-of select="CountryCode"/></xsl:attribute>
						<xsl:attribute name="State"><xsl:value-of select="State"/></xsl:attribute>
						<xsl:attribute name="ZipCode"><xsl:value-of select="ZipCode"/></xsl:attribute>
					</xsl:element>
				</xsl:if>
				<xsl:if test="$addressType = 'SHIPPING' and count(PrimaryInd)=0">
					<xsl:element name="YFSPersonInfoBillTo">
						<xsl:attribute name="AddressLine1"><xsl:value-of select="Line1"/></xsl:attribute>
						<xsl:attribute name="AddressLine2"><xsl:value-of select="Line2"/></xsl:attribute>
						<xsl:attribute name="City"><xsl:value-of select="City"/></xsl:attribute>
						<xsl:attribute name="Country"><xsl:value-of select="CountryCode"/></xsl:attribute>
						<xsl:attribute name="State"><xsl:value-of select="State"/></xsl:attribute>
						<xsl:attribute name="ZipCode"><xsl:value-of select="ZipCode"/></xsl:attribute>
					</xsl:element>
				</xsl:if>
				<xsl:if test="$addressType = 'SHIPPING' and $isPrimary = 'TRUE' ">
					<xsl:element name="YFSPersonInfoBillTo">
						<xsl:attribute name="AddressLine1"><xsl:value-of select="Line1"/></xsl:attribute>
						<xsl:attribute name="AddressLine2"><xsl:value-of select="Line2"/></xsl:attribute>
						<xsl:attribute name="City"><xsl:value-of select="City"/></xsl:attribute>
						<xsl:attribute name="Country"><xsl:value-of select="CountryCode"/></xsl:attribute>
						<xsl:attribute name="State"><xsl:value-of select="State"/></xsl:attribute>
						<xsl:attribute name="ZipCode"><xsl:value-of select="ZipCode"/></xsl:attribute>
					</xsl:element>
				</xsl:if>
				<xsl:if test="$addressType = 'OFFICIAL' and count(PrimaryInd)=0">
					<xsl:element name="YFSPersonInfoDeliverTo">
						<xsl:attribute name="AddressLine1"><xsl:value-of select="Line1"/></xsl:attribute>
						<xsl:attribute name="AddressLine2"><xsl:value-of select="Line2"/></xsl:attribute>
						<xsl:attribute name="City"><xsl:value-of select="City"/></xsl:attribute>
						<xsl:attribute name="Country"><xsl:value-of select="CountryCode"/></xsl:attribute>
						<xsl:attribute name="State"><xsl:value-of select="State"/></xsl:attribute>
						<xsl:attribute name="ZipCode"><xsl:value-of select="ZipCode"/></xsl:attribute>
					</xsl:element>
				</xsl:if>
				<xsl:if test="$addressType = 'OFFICIAL' and $isPrimary = 'TRUE' ">
					<xsl:element name="YFSPersonInfoDeliverTo">
						<xsl:attribute name="AddressLine1"><xsl:value-of select="Line1"/></xsl:attribute>
						<xsl:attribute name="AddressLine2"><xsl:value-of select="Line2"/></xsl:attribute>
						<xsl:attribute name="City"><xsl:value-of select="City"/></xsl:attribute>
						<xsl:attribute name="Country"><xsl:value-of select="CountryCode"/></xsl:attribute>
						<xsl:attribute name="State"><xsl:value-of select="State"/></xsl:attribute>
						<xsl:attribute name="ZipCode"><xsl:value-of select="ZipCode"/></xsl:attribute>
					</xsl:element>
				</xsl:if>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
