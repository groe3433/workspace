<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<xsl:element name="Order">
			<xsl:attribute name="DocumentType">
				<xsl:text>0001</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="ApplyDefaultTemplate">
				<xsl:text>Y</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="CreatedAtNode">
				<xsl:text>Y</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="CreatedByNode">
				<xsl:value-of select="WorkOrder/@NodeKey"/>
			</xsl:attribute>
			<xsl:attribute name="EnterpriseCode">
				<xsl:value-of select="WorkOrder/@EnterpriseCode"/>
			</xsl:attribute>
			<xsl:attribute name="IgnoreOrdering">
				<xsl:text>Y</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="DraftOrderFlag">
				<xsl:text>N</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="OrderType">
				<xsl:text>INCDT_REFURBISHMENT</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="SellerOrganizationCode">
				<xsl:value-of select="WorkOrder/@EnterpriseCode"/>
			</xsl:attribute>
			<xsl:variable name="rec-node" select="WorkOrder/@NodeKey"/>
			<xsl:attribute name="BuyerOrganizationCode">
				<xsl:value-of select="WorkOrder/@NodeKey"/>
			</xsl:attribute>
			<xsl:attribute name="ShipNode">
				<xsl:value-of select="$rec-node"/>
			</xsl:attribute>

			<xsl:variable name="noOfUnits" select="WorkOrder/@QuantityRequested"/>
			
			<xsl:element name="Extn">
				<xsl:attribute name="ExtnRefurbWO">
					<xsl:value-of select="WorkOrder/@WorkOrderNo"/>
				</xsl:attribute>
				<xsl:attribute name="ExtnIncidentNum">
					<xsl:value-of select="WorkOrder/Extn/@ExtnIncidentNumber"/>
				</xsl:attribute>
			</xsl:element>

			<xsl:element name="OrderLines">
				<xsl:for-each select="WorkOrder/WorkOrderComponents/WorkOrderComponent">
					<xsl:element name="OrderLine">
						<xsl:attribute name="ShipNode">
							<xsl:value-of select="$rec-node"/>
						</xsl:attribute>
						<xsl:element name="OrderLineTranQuantity">
							<xsl:attribute name="OrderedQty">
								<xsl:variable name="compQty" select="@ComponentQuantity"/>
								<xsl:value-of select="$noOfUnits * $compQty"/>
							</xsl:attribute>
							<xsl:attribute name="TransactionalUOM">
								<xsl:value-of select="@Uom"/>
							</xsl:attribute>
						</xsl:element>
						<xsl:element name="Item">
							<xsl:attribute name="ItemID">
								<xsl:value-of select="@ItemID"/>
							</xsl:attribute>
							<xsl:attribute name="ProductClass">
								<xsl:value-of select="@ProductClass"/>
							</xsl:attribute>
						</xsl:element>
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
				
		</xsl:element>		
	</xsl:template>
</xsl:stylesheet>

