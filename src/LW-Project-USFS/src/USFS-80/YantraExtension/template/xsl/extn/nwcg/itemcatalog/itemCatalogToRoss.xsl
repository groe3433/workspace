<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output indent="yes"/>
	<xsl:template match="/">
		<xsl:element name="CreateCatalogItemReq">
			<xsl:element name="MessageOriginator">
				<xsl:element name="SystemOfOrigin">
					<xsl:element name="SystemType"><xsl:text>ICBS</xsl:text></xsl:element>
				</xsl:element>
			</xsl:element>
			<xsl:element name="CatalogItem">
				<xsl:element name="CatalogType">NFES</xsl:element>
				<xsl:element name="CatalogItemName"><xsl:value-of select="/Item/PrimaryInformation/@ShortDescription"/></xsl:element>
				<xsl:element name="CatalogItemCode"><xsl:value-of select="/Item/@ItemID"/></xsl:element>
				<xsl:element name="TrackingRequiredInd"><xsl:value-of select="/Item/Extn/@ExtnRossResourceItem"/></xsl:element>
				<xsl:element name="OrderableWithQuantityInd"><xsl:choose><xsl:when test="/Item/PrimaryInformation/@MinOrderQuantity = 1 and /Item/PrimaryInformation/@MaxOrderQuantity = 1"><xsl:value-of select="0"/></xsl:when><xsl:otherwise><xsl:value-of select="1"/></xsl:otherwise></xsl:choose></xsl:element>
				<xsl:element name="UnitOfIssue"><xsl:value-of select="/Item/@UnitOfMeasure"/></xsl:element>
				<xsl:element name="StandardPack"><xsl:value-of select="/Item/Extn/@ExtnStandardPack"/></xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
