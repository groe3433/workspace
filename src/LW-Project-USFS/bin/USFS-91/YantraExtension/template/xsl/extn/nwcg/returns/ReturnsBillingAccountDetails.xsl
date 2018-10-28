<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output omit-xml-declaration="yes" indent="yes"/>
	<xsl:template match="node()|@*">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*"/>
		</xsl:copy>
	</xsl:template>
	<xsl:template match="NWCGBillingTransactionList">
		<NWCGBillingAccountList>
			<xsl:variable name="countY" select="count(/NWCGBillingTransactionList/NWCGBillingTransaction[@IsAccountSplit='Y'])"/>
			<xsl:variable name="countN" select="count(/NWCGBillingTransactionList/NWCGBillingTransaction[@IsAccountSplit='N'])"/>
			<xsl:variable name="countAll" select="count(/NWCGBillingTransactionList/NWCGBillingTransaction)"/>
			<xsl:attribute name="IsAccountSplit">
				<xsl:choose>
					<xsl:when test="$countY &gt; 0 and $countN=0">
						<xsl:value-of select="NWCGBillingTransaction[@IsAccountSplit='Y'][1]/@IsAccountSplit"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="NWCGBillingTransaction[@IsAccountSplit='N'][1]/@IsAccountSplit"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:choose>
				<xsl:when test="NWCGBillingTransaction/@IncidentFsAcctCode != &quot;&quot;">
					<xsl:for-each select="NWCGBillingTransaction/@IncidentFsAcctCode[not(.=preceding::NWCGBillingTransaction/@IncidentFsAcctCode)]">
						<NWCGBillingAccount>
							<xsl:attribute name="AccountCode">
								<xsl:value-of select="/NWCGBillingTransactionList/NWCGBillingTransaction[@IncidentFsAcctCode=current()]/@IncidentFsAcctCode"/>
							</xsl:attribute>
							<xsl:attribute name="TotalAmountCharged">
								<xsl:value-of select="sum(/NWCGBillingTransactionList/NWCGBillingTransaction[@IncidentFsAcctCode=current()]/@TransAmount)"/>
							</xsl:attribute>
							<xsl:attribute name="TotalAmountRefunded">
								<xsl:value-of select="sum(/NWCGBillingTransactionList/NWCGBillingTransaction[@IncidentFsAcctCode=current()]/@TransAmount)"/>
							</xsl:attribute>
							<xsl:attribute name="AccountType">
								<xsl:value-of select="FS"/>
							</xsl:attribute>
						</NWCGBillingAccount>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:for-each select="NWCGBillingTransaction/@IncidentBlmAcctCode[not(.=preceding::NWCGBillingTransaction/@IncidentBlmAcctCode)]">
						<NWCGBillingAccount>
							<xsl:attribute name="AccountCode">
								<xsl:value-of select="/NWCGBillingTransactionList/NWCGBillingTransaction[@IncidentBlmAcctCode=current()]/@IncidentBlmAcctCode"/>
							</xsl:attribute>
							<xsl:attribute name="TotalAmountCharged">
								<xsl:value-of select="sum(/NWCGBillingTransactionList/NWCGBillingTransaction[@IncidentBlmAcctCode=current()]/@TransAmount)"/>
							</xsl:attribute>
							<xsl:attribute name="TotalAmountRefunded">
								<xsl:value-of select="sum(/NWCGBillingTransactionList/NWCGBillingTransaction[@IncidentBlmAcctCode=current()]/@TransAmount)"/>
							</xsl:attribute>
							<xsl:attribute name="AccountType">
								<xsl:value-of select="BLM"/>
							</xsl:attribute>
						</NWCGBillingAccount>
					</xsl:for-each>
				</xsl:otherwise>
			</xsl:choose>			
		</NWCGBillingAccountList>
	</xsl:template>
</xsl:stylesheet>