<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<OrderRelease>
			<xsl:attribute name="OrderReleaseKey">
				<xsl:value-of select="Order/OrderStatuses/OrderStatus/@OrderReleaseKey"/>
			</xsl:attribute>
			<xsl:attribute name="OrderHeaderKey">
				<xsl:value-of select="Order/@OrderHeaderKey"/>
			</xsl:attribute>
		</OrderRelease>
	</xsl:template>
</xsl:stylesheet>
