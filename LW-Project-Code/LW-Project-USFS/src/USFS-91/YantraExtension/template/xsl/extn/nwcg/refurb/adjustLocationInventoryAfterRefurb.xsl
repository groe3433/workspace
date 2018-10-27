<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<xsl:element name="AdjustLocationInventory">
			<xsl:attribute name="EnterpriseCode">
				<xsl:value-of select="WorkOrder/@EnterpriseCode"/>
			</xsl:attribute>
			<xsl:attribute name="Node">
				<xsl:value-of select="WorkOrder/@NodeKey"/>
			</xsl:attribute>
			<xsl:variable name="item-Id" select="WorkOrder/@ItemID"/>
			<xsl:variable name="pclass" select="WorkOrder/@ProductClass"/>
			<xsl:variable name="unit" select="WorkOrder/@Uom"/>
			<xsl:variable name="locn-Id" select="WorkOrder/WorkOrderActivityDtl/@ActivityLocationId"/>
			<xsl:element name="Source">
					<xsl:attribute name="LocationId">
					<xsl:value-of select="$locn-Id"/>
			</xsl:attribute>
				<xsl:element name="Inventory">
					<xsl:attribute name="InventoryStatus">
					<xsl:text>NRFI-RFB</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="Quantity">
					<xsl:value-of select="0 -(WorkOrder/WorkOrderActivityDtl/@QuantityBeingConfirmed)"/>
					</xsl:attribute>
				<xsl:element name="InventoryItem">
					<xsl:attribute name="ItemID">
					<xsl:value-of select="$item-Id"/>
					</xsl:attribute>
					<xsl:attribute name="ProductClass">
					<xsl:value-of select="$pclass"/>
					</xsl:attribute>
					<xsl:attribute name="UnitOfMeasure">
					<xsl:value-of select="$unit"/>
					</xsl:attribute>
				</xsl:element>
				</xsl:element>
			</xsl:element>
		<xsl:element name="Audit">
			<xsl:attribute name="DocumentType">
			<xsl:text>7001</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="ReasonCode">
			<xsl:text>REFURB-ADJ</xsl:text>
			</xsl:attribute>
		</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
			
		