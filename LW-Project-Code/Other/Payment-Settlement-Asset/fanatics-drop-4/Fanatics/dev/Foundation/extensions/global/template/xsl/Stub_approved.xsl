<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/">
<Payment AuthCode="244388" AuthReturnCode="APPROVED" AuthReturnMessage="APPROVED" AuthorizationId="01" ResponseCode="APPROVED" TranAmount="0.00" ReasonCode="01" ReasonDesc="SUCCESS" ErrorNo="" ErrorDesc="" PaymentReference1="SUCCESS" RequestAmount="" ChargeType=""  TranReturnCode="" TranReturnMessage="APPROVED">
			<xsl:attribute name="SvcNo">
				<xsl:value-of select="/Payment/@SvcNo"/>
			</xsl:attribute>
			<xsl:attribute name="PaymentReference1">
				<xsl:value-of select="/Payment/@PaymentReference1"/>
			</xsl:attribute>
			<xsl:attribute name="AuthorizationAmount">
				<xsl:value-of select="/Payment/@RequestAmount"/>
			</xsl:attribute>
		<!--	<xsl:attribute name="AuthorizationExpirationDate">
				<xsl:value-of select="concat(substring(current-dateTime(),0,5),substring(current-dateTime(),6,2),substring(current-dateTime(),9,2),substring(current-dateTime(),12,2),substring(current-dateTime(),15,2),substring(current-dateTime(),18,2))"/>
			</xsl:attribute>-->				
		</Payment>
	</xsl:template>
</xsl:stylesheet>