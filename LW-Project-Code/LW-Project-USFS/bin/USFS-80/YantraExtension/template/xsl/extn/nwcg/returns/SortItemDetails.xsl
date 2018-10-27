<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<Item>
			<xsl:copy-of select="/Item/@*"/>
			<Components>
				<xsl:for-each select="/Item/Components/Component">
					<xsl:sort select="@ItemID" data-type="text"/>
					<xsl:copy>
						<xsl:copy-of select="@*"/>
					</xsl:copy>
				</xsl:for-each>
			</Components>
		</Item>
	</xsl:template>
</xsl:stylesheet>
