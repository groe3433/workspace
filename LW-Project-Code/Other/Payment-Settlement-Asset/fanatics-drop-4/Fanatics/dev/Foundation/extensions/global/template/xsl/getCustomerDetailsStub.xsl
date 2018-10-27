<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<xsl:element name="Customer">
			<xsl:attribute name="CustomerID"><xsl:value-of select="Customer/@CustomerID"></xsl:value-of></xsl:attribute>
			<xsl:attribute name="OrganizationCode"><xsl:value-of select="Customer/@OrganizationCode"></xsl:value-of></xsl:attribute>
			<xsl:attribute name="BuyerOrganizationCode"><xsl:value-of select="Customer/@OrganizationCode"></xsl:value-of></xsl:attribute>
			<xsl:element name="ExtnDataElement">
			<xsl:attribute name="CustomerSince">2012</xsl:attribute>
			<xsl:attribute name="AccountBalance">200</xsl:attribute>
			<xsl:attribute name="FanCashBalance">150</xsl:attribute>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
