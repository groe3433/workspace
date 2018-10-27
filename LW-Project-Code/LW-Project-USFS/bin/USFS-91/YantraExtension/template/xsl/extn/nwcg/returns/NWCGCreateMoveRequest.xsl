<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0">
	<xsl:template match="/">
	<xsl:element name="MoveRequest">
		<xsl:attribute name="FromActivityGroup">
                    <xsl:text>RECEIPT</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="Node">
                    <xsl:value-of select="Task/@Node"/>
		</xsl:attribute>
		<xsl:attribute name="EnterpriseCode">
                    <xsl:value-of select="Task/@EnterpriseKey"/>
		</xsl:attribute>
		<xsl:attribute name="IgnoreOrdering">
                    <xsl:text>Y</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="ShipmentKey">
                    <xsl:value-of select="Task/Inventory/@DAttribute"/>
		</xsl:attribute>
		<xsl:attribute name="SourceLocationId">
                    <xsl:value-of select="Task/@TargetLocationId"/>
		</xsl:attribute>
		<xsl:attribute name="TargetLocationId">
                    <xsl:value-of select="Task/Inventory/@DAttribute"/>
		</xsl:attribute>
		<xsl:element name="Shipment">
		      <xsl:attribute name="SellerOrganizationCode">
                        <xsl:value-of select="Task/@EnterpriseKey"/>
		      </xsl:attribute>
		      <xsl:attribute name="ShipNode">
                        <xsl:value-of select="Task/@Node"/>
		      </xsl:attribute>
		      <xsl:attribute name="ShipmentNo">
                        <xsl:value-of select="Task/Inventory/@DAttribute"/>
		      </xsl:attribute>
 		</xsl:element>
		<xsl:element name="MoveRequestLines">
		  <xsl:element name="MoveRequestLine">
		    <xsl:attribute name="InventoryStatus">
                    <xsl:value-of select="Task/Inventory/@InventoryStatus"/>
		    </xsl:attribute>
		    <xsl:attribute name="EnterpriseCode">
                    <xsl:value-of select="Task/@EnterpriseKey"/>
		    </xsl:attribute>
		    <xsl:attribute name="ItemId">
                    <xsl:value-of select="Task/Inventory/@ItemId"/>
		    </xsl:attribute>
		    <xsl:attribute name="ProductClass">
                    <xsl:value-of select="Task/Inventory/@ProductClass"/>
		    </xsl:attribute>
		    <xsl:attribute name="RequestQuantity">
                    <xsl:value-of select="Task/Inventory/@Quantity"/>
		    </xsl:attribute>
		    <xsl:attribute name="SourceLocationId">
                    <xsl:value-of select="Task/@TargetLocationId"/>
		    </xsl:attribute>
		    <xsl:attribute name="TargetLocationId">
                    <xsl:value-of select="Task/Inventory/@DAttribute"/>
		    </xsl:attribute>
		    <xsl:attribute name="UnitOfMeasure">
                    <xsl:value-of select="Task/Inventory/@UnitOfMeasure"/>
		    </xsl:attribute>
		    <xsl:attribute name="ShipByDate">
                    <xsl:value-of select="Task/Inventory/@ShipByDate"/>
		    </xsl:attribute>
                    <xsl:element name="MoveRequestLineTag">
		     <xsl:attribute name="BatchNo">
                         <xsl:value-of select="Task/Inventory/TagAttributes/@BatchNo"/>
		     </xsl:attribute>
		     <xsl:attribute name="LotAttribute1">
                         <xsl:value-of select="Task/Inventory/TagAttributes/@LotAttribute1"/>
		     </xsl:attribute>
		     <xsl:attribute name="LotAttribute2">
                         <xsl:value-of select="Task/Inventory/TagAttributes/@LotAttribute2"/>
		     </xsl:attribute>
		     <xsl:attribute name="LotAttribute3">
                         <xsl:value-of select="Task/Inventory/TagAttributes/@LotAttribute3"/>
		     </xsl:attribute>
		     <xsl:attribute name="LotNumber">
                         <xsl:value-of select="Task/Inventory/TagAttributes/@LotNumber"/>
		     </xsl:attribute>
		     <xsl:attribute name="RevisionNo">
                         <xsl:value-of select="Task/Inventory/TagAttributes/@RevisionNo"/>
		     </xsl:attribute>
		    </xsl:element>
		  </xsl:element>
		</xsl:element>
	</xsl:element>
	</xsl:template>
</xsl:stylesheet>
