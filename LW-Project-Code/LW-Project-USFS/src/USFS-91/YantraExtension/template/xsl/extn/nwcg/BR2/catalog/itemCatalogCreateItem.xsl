<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:cat="http://nwcg.gov/services/ross/catalog/1.1">
	<xsl:template match="/">
		<cat:CreateCatalogItemReq>
			<xsl:attribute name="EntityName">CATALOG</xsl:attribute>
			<xsl:attribute name="EntityValue"><xsl:value-of select="/Item/@ItemID"/> - <xsl:value-of select="/Item/@UnitOfMeasure"/></xsl:attribute>
			<xsl:attribute name="EntityKey"><xsl:value-of select="/Item/@ItemKey"/></xsl:attribute>
			<xsl:attribute name="messageName">CreateCatalogItemReq</xsl:attribute>
			<cat:MessageOriginator>
				<SystemOfOrigin>
					<SystemType>ICBS</SystemType>
				</SystemOfOrigin>
				<DispatchUnitID>
					<UnitIDPrefix>SETVALUE</UnitIDPrefix>
					<UnitIDSuffix>SETVALUE</UnitIDSuffix>
				</DispatchUnitID>
			</cat:MessageOriginator>
			<cat:CatalogItem>
				<CatalogType>NWCG</CatalogType>
				<CatalogItemName>
					<xsl:value-of select="/Item/PrimaryInformation/@ShortDescription"/>
				</CatalogItemName>
				<CatalogItemCode>
					<xsl:value-of select="/Item/@ItemID"/>
				</CatalogItemCode>
				<TrackingRequiredInd>
					<xsl:choose>
						<xsl:when test="/Item/Extn/@ExtnRossResourceItem = 'Y' ">true</xsl:when>
						<xsl:otherwise>false</xsl:otherwise>
					</xsl:choose>
				</TrackingRequiredInd>
				<OrderableWithQuantityInd>
					<xsl:choose>
						<xsl:when test="/Item/PrimaryInformation/@MinOrderQuantity	= 1 and /Item/PrimaryInformation/@MaxOrderQuantity = 1">false</xsl:when>
						<xsl:otherwise>true</xsl:otherwise>
					</xsl:choose>
				</OrderableWithQuantityInd>
				<UnitOfIssue>
					<xsl:value-of select="/Item/@UnitOfMeasure"/>
				</UnitOfIssue>
				<StandardPack>
					<xsl:value-of select="/Item/Extn/@ExtnStandardPack"/>
				</StandardPack>
			</cat:CatalogItem>
		</cat:CreateCatalogItemReq>
	</xsl:template>
</xsl:stylesheet>
