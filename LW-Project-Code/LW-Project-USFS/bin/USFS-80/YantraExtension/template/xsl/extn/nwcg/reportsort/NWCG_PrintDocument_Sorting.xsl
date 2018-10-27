<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output indent="yes" method="xml" />
		<xsl:template match="/">
			<PrintDocuments>
				<xsl:apply-templates select="PrintDocuments"/>
			</PrintDocuments>
		</xsl:template>
	
		<xsl:template match="PrintDocuments">
			<xsl:for-each select="@*">
				<xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
			</xsl:for-each>
			<PrintDocument>
				<xsl:apply-templates select="PrintDocument"/>
			</PrintDocument>
		</xsl:template>

		<xsl:template match="PrintDocument">
			<xsl:for-each select="@*">
				<xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
			</xsl:for-each>
			<xsl:copy-of select="PrinterPreference"/>
			<xsl:copy-of select="LabelPreference"/>
			<InputData>
				<xsl:apply-templates select="InputData"/>
			</InputData>
		</xsl:template>
		
		<xsl:template match="InputData">
			<xsl:for-each select="@*">
				<xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
			</xsl:for-each>
			<Order>
				<xsl:apply-templates select="Order"/>
			</Order>
		</xsl:template>
		
		<xsl:template match="Order">
			<xsl:for-each select="@*">
				<xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
			</xsl:for-each>
			<xsl:copy-of select="Extn"/>
			<xsl:copy-of select="PersonInfoShipTo"/>
			<xsl:copy-of select="PersonInfoBillTo"/>
			<xsl:copy-of select="AdditionalAddresses"/>
			<xsl:copy-of select="OrderDates"/>
			<xsl:copy-of select="Notes"/>
			<xsl:copy-of select="OverallTotals"/>
			<xsl:copy-of select="OrderStatuses"/>
			<xsl:copy-of select="RegulationInfo"/>
			<xsl:copy-of select="ShipToAddress"/>
			<xsl:copy-of select="ShipNode"/>
			<OrderLines>
				<xsl:apply-templates select="OrderLines"/>
			</OrderLines>
		</xsl:template>
		
		<xsl:template match="OrderLines">
			<xsl:for-each select="@*">
				<xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
			</xsl:for-each>
        	       	<xsl:apply-templates select="OrderLine">
       		         	<xsl:sort select="@IsHazmat" order="descending"/>
         	           	<xsl:sort select="substring-after(@ExtnRequestNo,'-')" order="ascending" data-type="number"/>                        
         	        </xsl:apply-templates>
		</xsl:template>

		<xsl:template match="OrderLine">
			<xsl:copy-of select="."/>
		</xsl:template>
	
		<xsl:template match="text()[not(string-length(normalize-space()))]"/>
		<xsl:template match="text()[string-length(normalize-space()) > 0]">
	  		<xsl:value-of select="translate(.,'&#xA;&#xD;', '  ')"/>
	</xsl:template>
</xsl:stylesheet>