<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
  <ItemShipNode>
       
  <xsl:attribute name="ShipnodeKey">
         <xsl:value-of select="//Organization/@OrganizationCode"/>
  </xsl:attribute>
  
  <xsl:attribute name="DistributionRuleId">
         <xsl:value-of select="//Organization/@DefaultDistributionRuleId"/>
  </xsl:attribute>
  
  <xsl:attribute name="OwnerKey">
         <xsl:value-of select="//Organization/@CallingOrganizationCode"/>
  </xsl:attribute>
  
  <xsl:attribute name="ItemId">ALL</xsl:attribute> 
   </ItemShipNode>
</xsl:template>
</xsl:stylesheet>