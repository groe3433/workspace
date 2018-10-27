<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<OrderLineList>
			<xsl:for-each select="OrderLineList/OrderLine">
				<xsl:sort select="Extn/@ExtnRequestNo" order="ascending" />
					<xsl:copy-of select="."/>
			</xsl:for-each>
		</OrderLineList>
	</xsl:template>
</xsl:stylesheet>