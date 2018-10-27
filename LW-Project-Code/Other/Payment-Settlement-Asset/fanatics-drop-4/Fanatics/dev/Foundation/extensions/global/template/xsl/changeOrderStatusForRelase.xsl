<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/*">
		<OrderStatusChange TransactionId="FAN_WMS_REL_UPDATES.0001.ex">
			<xsl:choose>
				<xsl:when test="//OrderRelease/OrderLines/OrderLine/@Action = 'WAVED'">
					<xsl:attribute name="BaseDropStatus">3200.03</xsl:attribute>
				</xsl:when>
				<xsl:when test="//OrderRelease/OrderLines/OrderLine/@Action = 'PICKED'">
					<xsl:attribute name="BaseDropStatus">3200.04</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="BaseDropStatus">3200.05</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates select="@*|node()" />
		</OrderStatusChange>
	</xsl:template>

	<xsl:template match="*">
		<xsl:if test="name() != 'Extn'">
			<xsl:element name="{local-name()}">
				<xsl:if test="name() = 'OrderLine'">
					<xsl:attribute name="ReleaseNo"><xsl:value-of
						select="//OrderRelease/@ReleaseNo" /></xsl:attribute>
				</xsl:if>
				<xsl:apply-templates select="@* | node()" />
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<xsl:template match="@*">
		<xsl:if test="name() != 'Action' and name() != 'ChangeInQuantity'">
			<xsl:attribute name="{local-name()}">
				<xsl:value-of select="." />
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="name() = 'ChangeInQuantity'">
			<xsl:attribute name="Quantity">
				<xsl:value-of select="number(.) * -1" />
			</xsl:attribute>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
