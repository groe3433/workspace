<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/">
		<Payment AuthCode="244388" AuthReturnCode="SERVICE_UNAVAILABLE" AuthReturnMessage="SERVICE_UNAVAILABLE" AuthorizationId="01" ResponseCode="SERVICE_UNAVAILABLE" TranAmount="0.00" ReasonCode="10" ReasonDesc="Timeout Error" ErrorNo="10" ErrorDesc="Timeout Error" PaymentReference1="" TranReturnCode="" RequestAmount="" ChargeType="" TranReturnMessage="SERVICE_UNAVAILABLE">
			<xsl:attribute name="ChargeType">
				<xsl:value-of select="/Payment/@ChargeType"/>
			</xsl:attribute>
			<xsl:attribute name="SvcNo">
				<xsl:value-of select="/Payment/@SvcNo"/>
			</xsl:attribute>
			<xsl:attribute name="PaymentReference1">
				<xsl:value-of select="/Payment/@PaymentReference1"/>
			</xsl:attribute>
			<xsl:attribute name="AuthorizationAmount">
				<xsl:value-of select="/Payment/@RequestAmount"/>
			</xsl:attribute>
			<xsl:attribute name="TranAmount">
				<xsl:value-of select="/Payment/CreditCardTransactions/CreditCardTransaction/@TranAmount"/>
			</xsl:attribute>
		<!--	<xsl:attribute name="AuthorizationExpirationDate">
				<xsl:value-of select="concat(substring(current-dateTime(),0,5),substring(current-dateTime(),6,2),substring(current-dateTime(),9,2),substring(current-dateTime(),12,2),substring(current-dateTime(),15,2),substring(current-dateTime(),18,2))"/>
			</xsl:attribute>-->				
		</Payment>
	</xsl:template>
</xsl:stylesheet>