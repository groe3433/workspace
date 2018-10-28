<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output indent="yes"/>
	<xsl:template match="/Order">
		<xsl:element name="Receipt">
			<xsl:attribute name="DocumentType"><xsl:value-of select="/Order/@DocumentType"/></xsl:attribute>		
			<xsl:attribute name="OrderHeaderKey"><xsl:value-of select="/Order/@OrderHeaderKey"/></xsl:attribute>		
			<xsl:attribute name="OrderNo"><xsl:value-of select="/Order/@OrderNo"/></xsl:attribute>		
			<xsl:attribute name="OrderType"><xsl:value-of select="/Order/@OrderType"/></xsl:attribute>		
			<xsl:attribute name="Node"><xsl:value-of select="/Order/Extn/@ExtnToIncidentCacheId"/></xsl:attribute>
			<xsl:attribute name="IncidentNo"><xsl:value-of select="/Order/Extn/@ExtnToIncidentNo"/></xsl:attribute>
			<xsl:attribute name="IncidentYear"><xsl:value-of select="/Order/Extn/@ExtnToIncidentYear"/></xsl:attribute>
	  
			<xsl:element name="ReceiptLines">
				<xsl:for-each select="/Order/OrderLines/OrderLine">
					<xsl:element name="ReceiptLine">
						<xsl:attribute name="ItemID"><xsl:value-of select="Item/@ItemID"/></xsl:attribute>		
						<xsl:attribute name="OrderLineKey"><xsl:value-of select="@OrderLineKey"/></xsl:attribute>		
						<xsl:attribute name="SerialNo"><xsl:value-of select="Extn/@ExtnTrackableId"/></xsl:attribute>
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>