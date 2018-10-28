<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:cat="http://nwcg.gov/services/ross/catalog/1.1">
	<xsl:output indent="yes"/>
	<xsl:template match="/">
		<cat:DeleteCatalogItemReq>
			<xsl:attribute name="EntityName">CATALOG</xsl:attribute>
			<xsl:attribute name="EntityValue"><xsl:value-of select="/Item/@ItemID"/> - <xsl:value-of select="/Item/@UnitOfMeasure"/></xsl:attribute>
			<xsl:attribute name="EntityKey"><xsl:value-of select="/Item/@ItemKey"/></xsl:attribute>
			<xsl:attribute name="messageName">DeleteCatalogItemReq</xsl:attribute>
			<cat:MessageOriginator>
				<SystemOfOrigin>
					<SystemType>ICBS</SystemType>
				</SystemOfOrigin>
				<DispatchUnitID>
					<UnitIDPrefix>SETVALUE</UnitIDPrefix>
					<UnitIDSuffix>SETVALUE</UnitIDSuffix>
				</DispatchUnitID>
			</cat:MessageOriginator>
			<cat:CatalogItemKey>
				<CatalogType>NWCG</CatalogType>
				<!-- <CatalogItemName>
					<xsl:value-of select="/Item/PrimaryInformation/@ShortDescriptionOld"/>
				</CatalogItemName> -->
				<CatalogItemCode>
					<xsl:value-of select="/Item/@ItemID"/>
				</CatalogItemCode>
			</cat:CatalogItemKey>
		</cat:DeleteCatalogItemReq>
	</xsl:template>
</xsl:stylesheet>
