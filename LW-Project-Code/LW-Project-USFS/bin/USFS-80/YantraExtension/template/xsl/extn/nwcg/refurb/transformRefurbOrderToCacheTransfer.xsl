<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output indent="yes"/>
	<xsl:template match="/">
		<Order>
			<xsl:attribute name="DocumentType"><xsl:text>0006</xsl:text></xsl:attribute>			
			<xsl:attribute name="ReceivingNode"><xsl:value-of select="/NWCGMasterWorkOrderLineList/@DestinationCache"/></xsl:attribute>
			<xsl:attribute name="OrderType"><xsl:text>Refurb Transfer</xsl:text></xsl:attribute>

			<OrderLines>
				<xsl:for-each select="/NWCGMasterWorkOrderLineList/NWCGMasterWorkOrderLine">
				<OrderLine>
					<xsl:attribute name="SerialNo"><xsl:value-of select="@PrimarySerialNo"/></xsl:attribute>
					<xsl:attribute name="ShipNode"><xsl:value-of select="@Node"/></xsl:attribute>
					<Extn>
						<xsl:attribute name="ExtnOrigReqQty"><xsl:value-of select="@TransferQuantity"/></xsl:attribute>
					</Extn>
					<Item>
						<xsl:attribute name="ItemID"><xsl:value-of select="@ItemID"/></xsl:attribute>
						<xsl:attribute name="ItemDesc"><xsl:value-of select="@ItemDesc"/></xsl:attribute>
						<xsl:attribute name="ProductClass"><xsl:value-of select="@ProductClass"/></xsl:attribute>
						<xsl:attribute name="UnitOfMeasure"><xsl:value-of select="@UnitOfMeasure"/></xsl:attribute>
					</Item>
					<OrderLineTranQuantity>
						<xsl:attribute name="OrderedQty"><xsl:value-of select="@TransferQuantity"/></xsl:attribute>
					</OrderLineTranQuantity>
				</OrderLine>
				</xsl:for-each>
			</OrderLines>
		</Order>
	</xsl:template>
</xsl:stylesheet>