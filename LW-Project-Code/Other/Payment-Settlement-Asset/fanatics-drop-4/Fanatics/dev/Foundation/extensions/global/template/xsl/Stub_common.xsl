<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:variable name="Amount1" select="/Payment/@RequestAmount" />
	<xsl:variable name="ChargeType1" select="/Payment/@ChargeType" />
	<xsl:template match="/">
	<Payment AuthCode="" AuthReturnCode="" AuthReturnMessage="" AuthorizationId="" ResponseCode="" 
	TranAmount="" ReasonCode="" ReasonDesc="" ErrorNo="" ErrorDesc="" PaymentReference1="" RequestAmount="" ChargeType=""  
	TranReturnCode="" >

	<xsl:if test="$ChargeType1 = 'AUTHORIZATION'"> 
	<xsl:choose>
    <xsl:when test="$Amount1 &lt;= 100 and $Amount1 &gt;-90"> 
			<xsl:attribute name="ResponseCode">AUTHORIZED</xsl:attribute>
			<xsl:attribute name="ReasonCode">05</xsl:attribute>
			<xsl:attribute name="ReasonDesc">SUCCESS</xsl:attribute>
	</xsl:when>
	
	<xsl:when test="$Amount1 &lt;=-90">
	<xsl:attribute name="ResponseCode">HARD_DECLINED</xsl:attribute>
	<xsl:attribute name="ReasonCode">05</xsl:attribute>
	<xsl:attribute name="ReasonDesc">No Card Found</xsl:attribute>
	</xsl:when>
	
	<xsl:when test="$Amount1 &gt;= 100 and $Amount1 &lt;= 200">
	<xsl:attribute name="ResponseCode">HARD_DECLINED</xsl:attribute>
	<xsl:attribute name="ReasonCode">02</xsl:attribute>
	<xsl:attribute name="ReasonDesc">No Card Found</xsl:attribute>
	</xsl:when>
	
    <xsl:otherwise> 
	<xsl:attribute name="ResponseCode">SERVICE_UNAVAILABLE</xsl:attribute>
	<xsl:attribute name="ReasonCode">03</xsl:attribute>
	<xsl:attribute name="ReasonDesc">Timeout Error</xsl:attribute>
	</xsl:otherwise> 
	</xsl:choose>

			<xsl:attribute name="AuthorizationId">
			<xsl:value-of select="/Payment/@AuthorizationId"/>
			</xsl:attribute>
			<xsl:attribute name="RequestAmount">
				<xsl:value-of select="/Payment/@RequestAmount"/>
			</xsl:attribute>
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
				<xsl:value-of select="/Payment/@RequestAmount"/>
			</xsl:attribute>
	
	</xsl:if>
	
	<xsl:if test="$ChargeType1 = 'CHARGE'"> 
	<xsl:choose>
    <xsl:when test="$Amount1 &lt;= 100 and $Amount1 &gt;-90"> 
	<xsl:attribute name="ResponseCode">APPROVED</xsl:attribute>
	<xsl:attribute name="ReasonCode">01</xsl:attribute>
	<xsl:attribute name="ReasonDesc">SUCCESS</xsl:attribute>
	</xsl:when>
	
	<xsl:when test="$Amount1 &lt;=-90">
	<xsl:attribute name="ResponseCode">HARD_DECLINED</xsl:attribute>
	<xsl:attribute name="ReasonCode">05</xsl:attribute>
	<xsl:attribute name="ReasonDesc">No Card Found</xsl:attribute>
	</xsl:when>
	
	<xsl:when test="$Amount1 &gt;= 100 and $Amount1 &lt;= 200">
	<xsl:attribute name="ResponseCode">HARD_DECLINED</xsl:attribute>
	<xsl:attribute name="ReasonCode">02</xsl:attribute>
	<xsl:attribute name="ReasonDesc">No Card Found</xsl:attribute>
	</xsl:when>
	
    <xsl:otherwise> 
	<xsl:attribute name="ResponseCode">SERVICE_UNAVAILABLE</xsl:attribute>
	<xsl:attribute name="ReasonCode">03</xsl:attribute>
	<xsl:attribute name="ReasonDesc">Timeout Error</xsl:attribute>
	</xsl:otherwise> 
	</xsl:choose>
	
			<xsl:attribute name="AuthorizationId">
			<xsl:value-of select="/Payment/@AuthorizationId"/>
			</xsl:attribute>
			<xsl:attribute name="AuthCode">
				<xsl:value-of select="/Payment/@AuthCode"/>
			</xsl:attribute>
			<xsl:attribute name="RequestAmount">
				<xsl:value-of select="/Payment/@RequestAmount"/>
			</xsl:attribute>
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
				<xsl:value-of select="/Payment/@RequestAmount"/>
			</xsl:attribute>
	</xsl:if>
					<!--	<xsl:attribute name="AuthorizationExpirationDate">
				<xsl:value-of select="concat(substring(current-dateTime(),0,5),substring(current-dateTime(),6,2),substring(current-dateTime(),9,2),substring(current-dateTime(),12,2),substring(current-dateTime(),15,2),substring(current-dateTime(),18,2))"/>
			</xsl:attribute>-->	
		
		</Payment>
	</xsl:template>
</xsl:stylesheet>
