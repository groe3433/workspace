<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output indent="yes"/>
	<xsl:template match="/">
		<MasterWorkOrder>
			<NWCGMasterWorkOrder>
				<xsl:attribute name="MasterWorkOrderNo"><xsl:value-of select="/Receipt/@ReceiptNo"/></xsl:attribute>
				<xsl:attribute name="ReceiptHeaderKey"><xsl:value-of select="/Receipt/@ReceiptHeaderKey"/></xsl:attribute>
				<xsl:attribute name="Enterprise"><xsl:value-of select="/Receipt/@EnterpriseCode"/></xsl:attribute>
				<xsl:attribute name="Node"><xsl:value-of select="/Receipt/@ReceivingNode"/></xsl:attribute>
				<xsl:attribute name="MasterWorkOrderType"><xsl:text>Refurb Transfer</xsl:text></xsl:attribute>
				<xsl:attribute name="ServiceItemID"><xsl:text>REFURBISHMENT</xsl:text></xsl:attribute>			
				<xsl:attribute name="IncidentNo"><xsl:value-of select="/Receipt/ReceiptLines/ReceiptLine/OrderLine/Order/Extn/@ExtnIncidentNo"/></xsl:attribute>
				<xsl:attribute name="IncidentYear"><xsl:value-of select="/Receipt/ReceiptLines/ReceiptLine/OrderLine/Order/Extn/@ExtnIncidentYear"/></xsl:attribute>
				<xsl:attribute name="IncidentName"><xsl:value-of select="/Receipt/ReceiptLines/ReceiptLine/OrderLine/Order/Extn/@ExtnIncidentName"/></xsl:attribute>
				<xsl:attribute name="IncidentType"><xsl:value-of select="/Receipt/ReceiptLines/ReceiptLine/OrderLine/Order/Extn/@ExtnIncidentType"/></xsl:attribute>
				<xsl:attribute name="SourceMWONo"><xsl:value-of select="/Receipt/ReceiptLines/ReceiptLine/OrderLine/Order/Extn/@ExtnRefurbWO"/></xsl:attribute>
				<xsl:attribute name="SourceNode"><xsl:value-of select="/Receipt/Shipment/@ShipNode"/></xsl:attribute>
			</NWCGMasterWorkOrder>
			<NWCGMasterWorkOrderLines>
				<xsl:for-each select="/Receipt/ReceiptLines/ReceiptLine">
				<NWCGMasterWorkOrderLine>
						<xsl:attribute name="ItemID"><xsl:value-of select="@ItemID"/></xsl:attribute>
						<xsl:attribute name="ItemDesc"><xsl:value-of select="OrderLine/Item/@ItemShortDesc"/></xsl:attribute>
						<xsl:attribute name="ProductClass"><xsl:value-of select="OrderLine/Item/@ProductClass"/></xsl:attribute>
						<xsl:attribute name="UnitOfMeasure"><xsl:value-of select="OrderLine/Item/@UnitOfMeasure"/></xsl:attribute>
						<xsl:attribute name="PrimarySerialNo"><xsl:value-of select="@SerialNo"/></xsl:attribute>
					    <xsl:attribute name="SecondarySerialNo1"><xsl:value-of select="SerialDetail/@SecondarySerial1"/></xsl:attribute>
					    <xsl:attribute name="ActualQuantity"><xsl:value-of select="@Quantity"/></xsl:attribute>
						<xsl:attribute name="Node"><xsl:value-of select="/Receipt/@ReceivingNode"/></xsl:attribute>
					    <xsl:attribute name="Status"><xsl:text>Awaiting Work Order Creation</xsl:text></xsl:attribute>
						
						<xsl:attribute name="ManufacturerName"><xsl:value-of select="@LotAttribute1"/></xsl:attribute>
						<xsl:attribute name="OwnerUnitID"><xsl:value-of select="@LotAttribute2"/></xsl:attribute>
						<xsl:attribute name="ManufacturerModel"><xsl:value-of select="@LotAttribute3"/></xsl:attribute>
						<xsl:attribute name="LotNo"><xsl:value-of select="@LotNumber"/></xsl:attribute>
						<xsl:attribute name="RevisionNo"><xsl:value-of select="@RevisionNo"/></xsl:attribute>
				</NWCGMasterWorkOrderLine>						
				</xsl:for-each>
			</NWCGMasterWorkOrderLines>
		</MasterWorkOrder>
	</xsl:template>
</xsl:stylesheet>