<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<GetLinesToReceive>
			<xsl:attribute name="NumberOfReceivableLines"><xsl:value-of select="count(/GetLinesToReceive/ReceivableLineList/ReceivableLine)"/></xsl:attribute>		
			<xsl:copy-of select="/GetLinesToReceive/@*"/>
			<ReceivableLineList>
				<xsl:for-each select="/GetLinesToReceive/ReceivableLineList/ReceivableLine">
					<xsl:sort select="@PrimeLineNo" data-type="number"/>
					<xsl:copy>
						<xsl:copy-of select="@*"/>
					</xsl:copy>
				</xsl:for-each>
			</ReceivableLineList>
		</GetLinesToReceive>
	</xsl:template>
</xsl:stylesheet>
