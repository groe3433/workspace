<?xml version="1.0" encoding="UTF-8" ?>
<!--
    Document    NWCGItemSKULabel.xsl
    Created on  Nov. 11th, 2006
    Author      Sunjay Gunda
    Description
        This XSL transforms the calls PrintDocumentSet api to print SKU labels.
-->

<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0">
	<!--<xsl:output indent="yes"/> -->
	<xsl:template match="Print | Item">
		<PrintDocuments>
			<xsl:attribute name="FlushToPrinter">
				<xsl:text>Y</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="PrintName">
				<xsl:text>NWCGItemSKULabel</xsl:text>
			</xsl:attribute>
			
			<PrintDocument>
				<xsl:attribute name="BeforeChildrenPrintDocumentId">
					<xsl:text>NWCG_ITEM_SKU_LABEL</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="DataElementPath">
					<xsl:text>xml:/Item</xsl:text>
				</xsl:attribute>
				<xsl:choose>
					<xsl:when test="name()=&quot;Print&quot;">
						<xsl:copy-of select="PrinterPreference"/>
						<xsl:copy-of select="LabelPreference"/>
					</xsl:when>
					<xsl:when test="name()=&quot;Item&quot;">
						<PrinterPreference>
							<xsl:attribute name="UsergroupId"/>
							<xsl:attribute name="UserId"><xsl:text>xml:/Item/@Modifyuserid</xsl:text></xsl:attribute>
							<xsl:attribute name="OrganizationCode"><xsl:text>xml:/Item/@ShipNode</xsl:text></xsl:attribute>
						</PrinterPreference>
					</xsl:when>
				</xsl:choose>
				<KeyAttributes>
					<KeyAttribute>
						<xsl:attribute name="Name"><xsl:text>ItemKey</xsl:text></xsl:attribute>
					</KeyAttribute>	
				</KeyAttributes>
				<InputData>
					<xsl:attribute name="APIName">
						<xsl:text>getItemDetails</xsl:text>
					</xsl:attribute>
					<Item>
						<xsl:choose>
							<xsl:when test="name()=&quot;Print&quot;">
								<xsl:copy-of select="Item/@*"/>
							</xsl:when>
							<xsl:when test="name()=&quot;Item&quot;">
								<xsl:copy-of select="@*"/>	
							</xsl:when>    
					   </xsl:choose> 
					</Item>
					<Template>
						<Item ItemID="" ItemKey="" OrganizationCode="" UnitOfMeasure="" Modifyuserid=""  >
							<PrimaryInformation ShortDescription="" Description="" ExtendedDescription="" UnitWeight="" UnitWeightUOM=""/>
							<Extn />
						</Item>
					</Template>
				</InputData>                        
			</PrintDocument>
		</PrintDocuments>
	</xsl:template>
</xsl:stylesheet>