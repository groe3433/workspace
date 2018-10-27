<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<xsl:element name="AdjustLocationInventory">
			<xsl:attribute name="EnterpriseCode">
				<xsl:value-of select="AdjustLocationInventory/@EnterpriseCode"/>
			</xsl:attribute>
			<xsl:attribute name="Node">
				<xsl:value-of select="AdjustLocationInventory/@Node"/>
			</xsl:attribute>

			<xsl:element name="Source">
				<xsl:attribute name="LocationId">
					<xsl:value-of select="AdjustLocationInventory/Source/@LocationId"/>
				</xsl:attribute>
				<xsl:element name="Inventory">
					<xsl:attribute name="InventoryStatus">
						<xsl:value-of select="AdjustLocationInventory/Source/Inventory/@InventoryStatus" />
					</xsl:attribute>

					<xsl:element name="InventoryItem">
						<xsl:attribute name="ItemID">
							<xsl:value-of select="AdjustLocationInventory/Source/Inventory/InventoryItem/@ItemID"/>
						</xsl:attribute>
						<xsl:attribute name="ProductClass">
							<xsl:value-of select="AdjustLocationInventory/Source/Inventory/InventoryItem/@ProductClass"/>
						</xsl:attribute>
						<xsl:attribute name="UnitOfMeasure">
							<xsl:value-of select="AdjustLocationInventory/Source/Inventory/InventoryItem/@UnitOfMeasure"/>
						</xsl:attribute>
					</xsl:element>

					<xsl:element name="SerialList">
						<xsl:for-each select="AdjustLocationInventory/Source/Inventory/SerialList/SerialDetail">
							<xsl:if test="normalize-space(@SerialNo) != &quot;&quot;">
								<xsl:element name="SerialDetail">
									<xsl:attribute name="Quantity">
										<xsl:value-of select="@Quantity"/>
									</xsl:attribute>
									<xsl:attribute name="SerialNo">
										<xsl:value-of select="@SerialNo"/>
									</xsl:attribute>
								</xsl:element>
							</xsl:if>
						</xsl:for-each>
					</xsl:element>
				</xsl:element>
			</xsl:element>
			<xsl:element name="Audit">
				<xsl:attribute name="ReasonCode">
					<xsl:value-of select="AdjustLocationInventory/Audit/@ReasonCode" />
				</xsl:attribute>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
			
		