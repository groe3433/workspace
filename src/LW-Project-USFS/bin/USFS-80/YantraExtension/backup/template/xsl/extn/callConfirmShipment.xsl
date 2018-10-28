<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0">
	<xsl:template match="/Shipment">
	<xsl:element name="Shipment">
		<xsl:attribute name="ShipComplete">
                    <xsl:text>Y</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="ManifestBeingClosed">
                    <xsl:text>Y</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="LoadBeingConfirmed">
                    <xsl:text>Y</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="ShipmentKey">
                    <xsl:value-of select="@ShipmentKey"/>
		</xsl:attribute>
	</xsl:element>
	</xsl:template>
</xsl:stylesheet>
