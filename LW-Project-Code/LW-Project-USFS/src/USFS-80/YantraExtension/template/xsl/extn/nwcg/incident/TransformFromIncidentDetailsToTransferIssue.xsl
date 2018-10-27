<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" >
	<xsl:output indent="yes"/>
	<xsl:template match="/">
		<xsl:variable name="uppercase">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
		<xsl:variable name="lowercase">abcdefghijklmnopqrstuvwxyz</xsl:variable>
		<xsl:element name="Order">
			<xsl:attribute name="ApplyDefaultTemplate">Y</xsl:attribute>
			<xsl:attribute name="DocumentType">0008.ex</xsl:attribute>
			<xsl:attribute name="DraftOrderFlag">Y</xsl:attribute>
			<xsl:attribute name="EnterpriseCode">NWCG</xsl:attribute>
			<xsl:attribute name="OrderDate"></xsl:attribute>
			<xsl:attribute name="OrderType">ICBSR_INIT_TRANSFER</xsl:attribute>
			<xsl:attribute name="SellerOrganizationCode">NWCG</xsl:attribute>
			<xsl:attribute name="ShipNode"></xsl:attribute>
			<xsl:attribute name="ReqShipDate"></xsl:attribute>

			<xsl:element name="Extn">
				<xsl:attribute name="ExtnToBlmAcctCode"><xsl:value-of select="//NWCGIncidentOrder/@IncidentBlmAcctCode"/></xsl:attribute>
				<xsl:attribute name="ExtnToFsAcctCode"><xsl:value-of select="//NWCGIncidentOrder/@IncidentFsAcctCode"/></xsl:attribute>
				<xsl:attribute name="ExtnToIncidentCacheId"><xsl:value-of select="//NWCGIncidentOrder/@PrimaryCacheId"/></xsl:attribute>
				<xsl:attribute name="ExtnToIncidentName"><xsl:value-of select="//NWCGIncidentOrder/@IncidentName"/></xsl:attribute>
				<xsl:attribute name="ExtnToIncidentNo"><xsl:value-of select="//NWCGIncidentOrder/@IncidentNo"/></xsl:attribute>
				<xsl:attribute name="ExtnToIncidentType"><xsl:value-of select="//NWCGIncidentOrder/@IncidentType"/></xsl:attribute>
				<xsl:attribute name="ExtnToIncidentYear"><xsl:value-of select="//NWCGIncidentOrder/@Year"/></xsl:attribute>
				<xsl:attribute name="ExtnToOtherAcctCode"><xsl:value-of select="//NWCGIncidentOrder/@IncidentOtherAcctCode"/></xsl:attribute>
				<xsl:attribute name="ExtnToOverrideCode"><xsl:value-of select="//NWCGIncidentOrder/@OverrideCode"/></xsl:attribute>
				<xsl:attribute name="ExtnToPhoneNo"><xsl:value-of select="//NWCGIncidentOrder/@PhoneNo"/></xsl:attribute>
			</xsl:element>

			<xsl:element name="PersonInfoBillTo">
				<xsl:attribute name="AddressLine1"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@AddressLine1"/></xsl:attribute>
				<xsl:attribute name="AddressLine2"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@AddressLine2"/></xsl:attribute>
				<xsl:attribute name="AddressLine3"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@AddressLine3"/></xsl:attribute>
				<xsl:attribute name="AddressLine4"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@AddressLine4"/></xsl:attribute>
				<xsl:attribute name="AddressLine5"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@AddressLine5"/></xsl:attribute>
				<xsl:attribute name="AddressLine6"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@AddressLine6"/></xsl:attribute>
				<xsl:attribute name="AddressType"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@AddressType"/></xsl:attribute>
				<xsl:attribute name="AlternateEmailID"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@AlternateEmailID"/></xsl:attribute>
				<xsl:attribute name="Beeper"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@Beeper"/></xsl:attribute>
				<xsl:attribute name="City"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@City"/></xsl:attribute>
				<xsl:attribute name="Company"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@Company"/></xsl:attribute>
				<xsl:attribute name="Country"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@Country"/></xsl:attribute>
				<xsl:attribute name="DayFaxNo"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@DayFaxNo"/></xsl:attribute>
				<xsl:attribute name="DayPhone"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@DayPhone"/></xsl:attribute>
				<xsl:attribute name="Department"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@Department"/></xsl:attribute>
				<xsl:attribute name="EMailID"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@EMailID"/></xsl:attribute>
				<xsl:attribute name="EveningFaxNo"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@EveningFaxNo"/></xsl:attribute>
				<xsl:attribute name="EveningPhone"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@EveningPhone"/></xsl:attribute>
				<xsl:attribute name="FirstName"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@FirstName"/></xsl:attribute>
				<xsl:attribute name="HttpUrl"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@HttpUrl"/></xsl:attribute>
				<xsl:attribute name="JobTitle"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@JobTitle"/></xsl:attribute>
				<xsl:attribute name="LastName"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@LastName"/></xsl:attribute>
				<xsl:attribute name="MiddleName"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@MiddleName"/></xsl:attribute>
				<xsl:attribute name="MobilePhone"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@MobilePhone"/></xsl:attribute>
				<xsl:attribute name="OtherPhone"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@OtherPhone"/></xsl:attribute>
				<xsl:attribute name="PersonID"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@PersonID"/></xsl:attribute>
				<xsl:attribute name="PersonInfoKey"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@PersonInfoKey"/></xsl:attribute>
				<xsl:attribute name="PreferredShipAddress"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@PreferredShipAddress"/></xsl:attribute>
				<xsl:attribute name="State"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@State"/></xsl:attribute>
				<xsl:attribute name="Suffix"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@Suffix"/></xsl:attribute>
				<xsl:attribute name="Title"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@Title"/></xsl:attribute>
				<xsl:attribute name="UseCount"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@UseCount"/></xsl:attribute>
				<xsl:attribute name="VerificationStatus"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@VerificationStatus"/></xsl:attribute>
				<xsl:attribute name="ZipCode"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoBillTo/@ZipCode"/></xsl:attribute>
			</xsl:element>

			<xsl:element name="PersonInfoShipTo">
				<xsl:attribute name="AddressLine1"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@AddressLine1"/></xsl:attribute>
				<xsl:attribute name="AddressLine2"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@AddressLine2"/></xsl:attribute>
				<xsl:attribute name="AddressLine3"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@AddressLine3"/></xsl:attribute>
				<xsl:attribute name="AddressLine4"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@AddressLine4"/></xsl:attribute>
				<xsl:attribute name="AddressLine5"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@AddressLine5"/></xsl:attribute>
				<xsl:attribute name="AddressLine6"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@AddressLine6"/></xsl:attribute>
				<xsl:attribute name="AddressType"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@AddressType"/></xsl:attribute>
				<xsl:attribute name="AlternateEmailID"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@AlternateEmailID"/></xsl:attribute>
				<xsl:attribute name="Beeper"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@Beeper"/></xsl:attribute>
				<xsl:attribute name="City"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@City"/></xsl:attribute>
				<xsl:attribute name="Company"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@Company"/></xsl:attribute>
				<xsl:attribute name="Country"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@Country"/></xsl:attribute>
				<xsl:attribute name="DayFaxNo"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@DayFaxNo"/></xsl:attribute>
				<xsl:attribute name="DayPhone"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@DayPhone"/></xsl:attribute>
				<xsl:attribute name="Department"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@Department"/></xsl:attribute>
				<xsl:attribute name="EMailID"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@EMailID"/></xsl:attribute>
				<xsl:attribute name="EveningFaxNo"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@EveningFaxNo"/></xsl:attribute>
				<xsl:attribute name="EveningPhone"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@EveningPhone"/></xsl:attribute>
				<xsl:attribute name="FirstName"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@FirstName"/></xsl:attribute>
				<xsl:attribute name="HttpUrl"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@HttpUrl"/></xsl:attribute>
				<xsl:attribute name="JobTitle"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@JobTitle"/></xsl:attribute>
				<xsl:attribute name="LastName"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@LastName"/></xsl:attribute>
				<xsl:attribute name="MiddleName"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@MiddleName"/></xsl:attribute>
				<xsl:attribute name="MobilePhone"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@MobilePhone"/></xsl:attribute>
				<xsl:attribute name="OtherPhone"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@OtherPhone"/></xsl:attribute>
				<xsl:attribute name="PersonID"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@PersonID"/></xsl:attribute>
				<xsl:attribute name="PersonInfoKey"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@PersonInfoKey"/></xsl:attribute>
				<xsl:attribute name="PreferredShipAddress"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@PreferredShipAddress"/></xsl:attribute>
				<xsl:attribute name="State"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@State"/></xsl:attribute>
				<xsl:attribute name="Suffix"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@Suffix"/></xsl:attribute>
				<xsl:attribute name="Title"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@Title"/></xsl:attribute>
				<xsl:attribute name="UseCount"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@UseCount"/></xsl:attribute>
				<xsl:attribute name="VerificationStatus"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@VerificationStatus"/></xsl:attribute>
				<xsl:attribute name="ZipCode"><xsl:value-of select="//NWCGIncidentOrder/YFSPersonInfoShipTo/@ZipCode"/></xsl:attribute>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
