<?xml version="1.0" encoding="UTF-8" ?> 
 <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 <xsl:template match="/">
 <xsl:variable name="attCallAPI" select="ItemList/Item[@Action = 'Delete']" />
 <ModifyCategoryItems>
 <xsl:if test="count($attCallAPI)">
  <xsl:attribute name="CallAPI">Y</xsl:attribute>
 </xsl:if>
 <xsl:attribute name="CallingOrganizationCode">
  <xsl:value-of select="//Item/@OrganizationCode" /> 
  </xsl:attribute>
 <Category>
 <xsl:attribute name="CategoryPath">
  <xsl:value-of select="concat('/', //Item/@OrganizationCode, 'MasterCatalog/DEFAULT')" /> 
  </xsl:attribute>
 <xsl:attribute name="OrganizationCode">
  <xsl:value-of select="//Item/@OrganizationCode" /> 
  </xsl:attribute>
 <CategoryItemList>
 <xsl:for-each select="/ItemList/Item">
 <xsl:if test="@Action =  'Delete'">
 <CategoryItem>
 <xsl:attribute name="Action">Delete</xsl:attribute> 
 <xsl:attribute name="ItemID">
  <xsl:value-of select="@ItemID" /> 
  </xsl:attribute>
 <xsl:attribute name="UnitOfMeasure">
  <xsl:value-of select="@UnitOfMeasure" /> 
  </xsl:attribute>
 <xsl:attribute name="OrganizationCode">
  <xsl:value-of select="@OrganizationCode" /> 
  </xsl:attribute>
  </CategoryItem>
  </xsl:if>
  </xsl:for-each>
  </CategoryItemList>
  </Category>
  </ModifyCategoryItems>
  </xsl:template>
  </xsl:stylesheet>