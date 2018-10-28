<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output indent="yes"/>
	<xsl:template match="/">
		<xsl:element name="NWCGIncidentOrder">
			<xsl:attribute name="IncidentNo"><xsl:value-of select="/NWCGIncidentOrder/@IncidentNo"/></xsl:attribute>
			<xsl:attribute name="Year"><xsl:value-of select="/NWCGIncidentOrder/@Year"/></xsl:attribute>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
