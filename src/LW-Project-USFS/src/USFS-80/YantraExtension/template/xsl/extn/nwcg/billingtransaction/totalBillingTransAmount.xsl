<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="NWCGBillingTransactionList">
		<NWCGBillingData>
					<xsl:for-each select="NWCGBillingTransaction/@IncidentFsAcctCode[not(.=preceding::NWCGBillingTransaction/@IncidentFsAcctCode)]">
							<xsl:attribute name="TotalBillingTransAmount">
								<xsl:value-of select="sum(/NWCGBillingTransactionList/NWCGBillingTransaction[@IncidentFsAcctCode=current()]/@TransAmount)"/>
							</xsl:attribute>
					</xsl:for-each>
		</NWCGBillingData>
	</xsl:template>
</xsl:stylesheet>