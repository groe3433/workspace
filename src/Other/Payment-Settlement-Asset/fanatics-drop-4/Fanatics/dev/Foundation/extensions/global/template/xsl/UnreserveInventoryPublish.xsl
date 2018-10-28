<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<Order>
		    <xsl:attribute name="DocumentType">
			<xsl:value-of select="//Order/@DocumentType" />
			</xsl:attribute>
			<xsl:attribute name="EnterpriseCode">
			<xsl:value-of select="//Order/@EnterpriseCode" />
			</xsl:attribute>
			<xsl:attribute name="EntryType"> 
            <xsl:value-of select="//Order/@EntryType" /> 
            </xsl:attribute> 
			<xsl:attribute name="OrderNo">
			<xsl:value-of select="//Order/@OrderNo" />
			</xsl:attribute>
            <xsl:attribute name="SellerOrganizationCode"> 
            <xsl:value-of select="//Order/@SellerOrganizationCode" /> 
            </xsl:attribute> 
            <xsl:attribute name="OrderDate"> 
            <xsl:value-of select="//Order/@OrderDate" /> 
            </xsl:attribute> 
			<OrderLines>
				<xsl:for-each select="Order/OrderLines/OrderLine">
				<OrderLine>
					<xsl:attribute name="OrderedQty"><xsl:value-of
					select="@OrderedQty" /></xsl:attribute>
					<xsl:attribute name="PrimeLineNo"><xsl:value-of
					select="@PrimeLineNo" /></xsl:attribute>
					<xsl:attribute name="SubLineNo"><xsl:value-of
					select="@SubLineNo" /></xsl:attribute>
					
					
					<Item>
						<xsl:attribute name="ItemID"><xsl:value-of
						select="Item/@ItemID" /></xsl:attribute>
						<xsl:attribute name="UnitOfMeasure"><xsl:value-of
						select="Item/@UnitOfMeasure" /></xsl:attribute>
					</Item>
					</OrderLine>
				</xsl:for-each>
			</OrderLines>
			<CustomAttributes>
				<xsl:attribute name="CustomerOrderNo"><xsl:value-of
				select="//Order/CustomAttributes/@CustomerOrderNo" /></xsl:attribute>
			</CustomAttributes>
		</Order>
	</xsl:template>
</xsl:stylesheet>
