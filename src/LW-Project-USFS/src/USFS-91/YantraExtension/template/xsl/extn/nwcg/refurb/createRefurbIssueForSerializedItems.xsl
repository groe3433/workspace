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
				<xsl:value-of select="RefurbWorkOrder/@NodeKey"/>
			</xsl:attribute>
			<xsl:attribute name="EnterpriseCode">
				<xsl:value-of select="RefurbWorkOrder/@EnterpriseCode"/>
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
				<xsl:value-of select="RefurbWorkOrder/@EnterpriseCode"/>
			</xsl:attribute>
			<xsl:variable name="rec-node" select="RefurbWorkOrder/@NodeKey"/>
			<xsl:attribute name="BuyerOrganizationCode">
				<xsl:value-of select="RefurbWorkOrder/@NodeKey"/>
			</xsl:attribute>
			<xsl:attribute name="ShipNode">
				<xsl:value-of select="$rec-node"/>
			</xsl:attribute>
			<xsl:attribute name="BillToID">
				<xsl:value-of select="RefurbWorkOrder/@BillToID"/>
			</xsl:attribute>

			<xsl:element name="Extn">
				<xsl:attribute name="ExtnRefurbWO">
					<xsl:value-of select="RefurbWorkOrder/@WorkOrderNo"/>
				</xsl:attribute>
				<xsl:attribute name="ExtnIncidentNo">
					<xsl:value-of select="RefurbWorkOrder/Extn/@ExtnIncidentNumber"/>
				</xsl:attribute>
				<xsl:attribute name="ExtnFsAcctCode">
					<xsl:value-of select="RefurbWorkOrder/Extn/@ExtnFsAcctCode"/>
				</xsl:attribute>
				<xsl:attribute name="ExtnBlmAcctCode">
					<xsl:value-of select="RefurbWorkOrder/Extn/@ExtnBlmAcctCode"/>
				</xsl:attribute>
				<xsl:attribute name="ExtnOtherAcctCode">
					<xsl:value-of select="RefurbWorkOrder/Extn/@ExtnOtherAcctCode"/>
				</xsl:attribute>
			</xsl:element>

			<xsl:element name="OrderLines">
				<xsl:for-each select="RefurbWorkOrder/WorkOrderComponents/WorkOrderComponent">
					<xsl:if test="@RefurbQuantity > 0">
						<xsl:element name="OrderLine">
							<xsl:attribute name="ShipNode">
								<xsl:value-of select="$rec-node"/>
							</xsl:attribute>
							<xsl:element name="OrderLineTranQuantity">
								<xsl:attribute name="OrderedQty">
									<xsl:value-of select="@RefurbQuantity"/>
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
					</xsl:if>
				</xsl:for-each>
			</xsl:element>
				
		</xsl:element>		
	</xsl:template>
</xsl:stylesheet>

