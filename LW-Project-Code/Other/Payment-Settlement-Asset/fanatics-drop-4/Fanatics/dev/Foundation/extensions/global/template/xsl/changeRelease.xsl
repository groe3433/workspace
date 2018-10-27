<?xml version="1.0" encoding="UTF-8" ?>  
 <xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 <xsl:template match="/">
 <OrderRelease>
 <xsl:attribute name="DocumentType">
  <xsl:value-of select="//OrderRelease/@DocumentType" /> 
  </xsl:attribute>
 <xsl:attribute name="EnterpriseCode">
  <xsl:value-of select="//OrderRelease/@EnterpriseCode" /> 
  </xsl:attribute>
 <xsl:attribute name="OrderNo">
  <xsl:value-of select="//OrderRelease/@OrderNo" /> 
  </xsl:attribute>
 <xsl:attribute name="ModificationReasonCode">
  <xsl:value-of select="//OrderRelease/@ModificationReasonCode" /> 
  </xsl:attribute>
 <xsl:attribute name="ModificationReasonText">
  <xsl:value-of select="//OrderRelease/@ModificationReasonText" /> 
  </xsl:attribute>
 <xsl:attribute name="ReleaseNo">
  <xsl:value-of select="//OrderRelease/@ReleaseNo" /> 
  </xsl:attribute>
 <xsl:attribute name="SellerOrganizationCode">
  <xsl:value-of select="//OrderRelease/@SellerOrganizationCode" /> 
  </xsl:attribute>
 <xsl:attribute name="ShipNode">
  <xsl:value-of select="//OrderRelease/@ShipNode" /> 
  </xsl:attribute>
 <xsl:if test="//OrderRelease/Extn/@RejectDueToInvShortage = 'N'">
  <xsl:attribute name="PutInventoryOnHoldOnBackorder">N</xsl:attribute> 
 </xsl:if>
 <OrderLines>
 
 <xsl:for-each select="/OrderRelease/OrderLines/OrderLine">
 <xsl:if test="@Action = 'BACKORDER'">
 <OrderLine>
<xsl:attribute name="Action">
  <xsl:value-of select="@Action" /> 
  </xsl:attribute>
 <xsl:attribute name="PrimeLineNo">
  <xsl:value-of select="@PrimeLineNo" /> 
  </xsl:attribute>
 <xsl:attribute name="SubLineNo">
  <xsl:value-of select="@SubLineNo" /> 
  </xsl:attribute>
 <xsl:attribute name="ChangeInQuantity">
  <xsl:value-of select="@ChangeInQuantity" /> 
  </xsl:attribute>
 <Item>
 <xsl:attribute name="ItemID">
  <xsl:value-of select="Item/@ItemID" /> 
  </xsl:attribute>
 <xsl:attribute name="ProductClass">
  <xsl:value-of select="Item/@ProductClass" /> 
  </xsl:attribute>
 <xsl:attribute name="UnitOfMeasure">
  <xsl:value-of select="Item/@UnitOfMeasure" /> 
  </xsl:attribute>
  </Item>
  </OrderLine>
  </xsl:if>
  </xsl:for-each>
  
  </OrderLines>
 <Extn>
 <xsl:attribute name="ControlNumber">
  <xsl:value-of select="//OrderRelease/Extn/@ReleaseControlNbr" /> 
  </xsl:attribute>
 <xsl:attribute name="RejectDueToInvShortage">
  <xsl:value-of select="//OrderRelease/Extn/@RejectDueToInvShortage" /> 
  </xsl:attribute>
 <xsl:attribute name="Status">
  <xsl:value-of select="//OrderRelease/Extn/@Status" /> 
  </xsl:attribute>
  </Extn>
  </OrderRelease>
  </xsl:template>
  </xsl:stylesheet>
