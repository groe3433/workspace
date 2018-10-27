<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<Order>
			<xsl:attribute name="OrderHeaderKey">
				<xsl:value-of select="//Order/@OrderHeaderKey" />
			</xsl:attribute>
			
			<OrderHoldTypes>
				<OrderHoldType>
					<xsl:attribute name="HoldType">MISSING_PAYMENT_INFO</xsl:attribute>				
					<xsl:attribute name="ReasonText">Missing payment information</xsl:attribute>
					<xsl:attribute name="Status">1100</xsl:attribute>
				</OrderHoldType>
			</OrderHoldTypes>
		</Order>
	</xsl:template>
</xsl:stylesheet>