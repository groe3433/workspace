<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="Order">
		<Order>
		
		<!-- Copy all attributes of Order -->
		<xsl:for-each select="@*">
			<xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
		</xsl:for-each> 
		
			<!-- Copy the following nodes including child nodes -->
			<xsl:copy-of select="Extn"/>
			<xsl:copy-of select="PersonInfoShipTo"/>
			<xsl:copy-of select="PersonInfoBillTo"/>
			<xsl:copy-of select="AdditionalAddresses"/>
			<xsl:copy-of select="OrderDates"/>
			<xsl:copy-of select="Notes"/>
			<xsl:copy-of select="OverallTotals"/>
			<xsl:copy-of select="OrderStatuses"/>
			<xsl:copy-of select="RegulationInfo"/>
			<xsl:copy-of select="ShipToAddress"/>
			<xsl:copy-of select="ShipNode"/>

			<OrderLines>
				<xsl:for-each select="OrderLines/OrderLine">
					<xsl:sort select="@IsHazmat" order="descending" />
					<xsl:sort select="Extn/@ExtnRequestNo" order="ascending" />
						<xsl:copy-of select="."/>
				</xsl:for-each>
			</OrderLines>

		</Order>
	</xsl:template>
</xsl:stylesheet>