<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<xsl:element name="OrderStatusChange">
			<xsl:attribute name="BaseDropStatus">
				<xsl:text>1100.0009</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="DocumentType">
				<xsl:text>0001</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="EnterpriseCode">
				<xsl:value-of select="Order/@EnterpriseCode"/>
			</xsl:attribute>
			<xsl:attribute name="OrderNo">
				<xsl:value-of select="Order/@OrderNo"/>
			</xsl:attribute>
			<xsl:attribute name="ChangeForAllAvailableQty">
				<xsl:text>Y</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="TransactionId">
				<xsl:text>REFURB_STATUS_CHANGE.0001.ex</xsl:text>
			</xsl:attribute>
		</xsl:element>		
	</xsl:template>
</xsl:stylesheet>

