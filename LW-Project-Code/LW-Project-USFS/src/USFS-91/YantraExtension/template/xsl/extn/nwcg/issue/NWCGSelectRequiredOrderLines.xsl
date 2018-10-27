<?xml version="1.0" encoding="utf-8"?>
<!-- This XSL is used to trim all orderlines except lines between 'FromPrimeLineNo' to 'ToPrimeLineNo'
		This XSL is used in Pagination service. -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/OrderLineList">
		<OrderLineList>
			<!-- Copy all attributes of OrderLineList -->
			<xsl:for-each select="@*">
				<xsl:attribute name="{name()}"><xsl:value-of select="." /></xsl:attribute>
			</xsl:for-each>
			<xsl:variable name="endPosition" select="@ToPrimeLineNo" />
			<xsl:variable name="startPosition" select="@FromPrimeLineNo" />
			<xsl:for-each select="OrderLine">
				<xsl:choose>
					<xsl:when test="(position() &gt;= $startPosition) and (position() &lt;= $endPosition)">
						<xsl:copy-of select="."/>
					</xsl:when>
					<xsl:otherwise></xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</OrderLineList>
	</xsl:template>
</xsl:stylesheet>