<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<xsl:element name="Serial">
			<xsl:attribute name="AtNode">
				<xsl:text>Y</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="InventoryStatus">
				<xsl:text>Y</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="ShipNode">
				<xsl:value-of select="BarCode/ContextualInfo/@OrganizationCode"/>
			</xsl:attribute>
			<xsl:attribute name="SerialNo">
				<xsl:value-of select="BarCode/@BarCodeData"/>
			</xsl:attribute>
				<xsl:element name="InventoryItem">
					<xsl:attribute name="ItemID">
						<xsl:value-of select="BarCode/ItemContextualInfo/@ItemID"/>
					</xsl:attribute>
					<xsl:attribute name="InventoryOrganizationCode">
						<xsl:value-of select="BarCode/ContextualInfo/@EnterpriseCode"/>
					</xsl:attribute>
					<xsl:attribute name="UnitOfMeasure">
						<xsl:value-of select="BarCode/ItemContextualInfo/@InventoryUOM"/>
					</xsl:attribute>
				</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>