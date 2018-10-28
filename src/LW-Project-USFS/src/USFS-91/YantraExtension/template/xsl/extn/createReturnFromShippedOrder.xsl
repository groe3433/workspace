<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<xsl:element name="Order">
			<xsl:attribute name="DocumentType">
				<xsl:text>0003</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="ApplyDefaultTemplate">
				<xsl:text>Y</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="BuyerOrganizationCode">
				<xsl:value-of select="Shipment/@BuyerOrganizationCode"/>
			</xsl:attribute>
			<xsl:attribute name="CreatedAtNode">
				<xsl:text>Y</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="CreatedByNode">
				<xsl:value-of select="Shipment/@ShipNode"/>
			</xsl:attribute>
			<xsl:attribute name="EnterpriseCode">
				<xsl:value-of select="Shipment/@EnterpriseCode"/>
			</xsl:attribute>
			<xsl:attribute name="IgnoreOrdering">
				<xsl:text>Y</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="DraftOrderFlag">
				<xsl:text>Y</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="SellerOrganizationCode">
				<xsl:value-of select="Shipment/@SellerOrganizationCode"/>
			</xsl:attribute>
			<xsl:variable name="rec-node" select="Shipment/@ShipNode"/>
				<xsl:element name="OrderLines">
				<xsl:for-each select="Shipment/ShipmentLines/ShipmentLine">
				<xsl:element name="OrderLine">
				<xsl:attribute name="ShipNode">
					<xsl:value-of select="$rec-node"/>
				</xsl:attribute>
				<xsl:element name="DerivedFrom">
					<xsl:attribute name="OrderReleaseKey">
						<xsl:value-of select="@OrderReleaseKey"/>
					</xsl:attribute>
					<xsl:attribute name="OrderHeaderKey">
						<xsl:value-of select="@OrderHeaderKey"/>
					</xsl:attribute>
					<xsl:attribute name="OrderLineKey">
						<xsl:value-of select="@OrderLineKey"/>
				</xsl:attribute>
				</xsl:element>
				</xsl:element>
				</xsl:for-each>
				</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>

