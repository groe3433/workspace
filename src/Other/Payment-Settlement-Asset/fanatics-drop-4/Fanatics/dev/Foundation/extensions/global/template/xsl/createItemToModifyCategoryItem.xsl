<?xml version="1.0" encoding="UTF-8" ?> 
 <xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 <xsl:template match="/">
 <ModifyCategoryItems>
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
 <CategoryItem>
  <xsl:attribute name="Action">Create</xsl:attribute> 
 <xsl:attribute name="ItemID">
  <xsl:value-of select="//Item/@ItemID" /> 
  </xsl:attribute>
 <xsl:attribute name="UnitOfMeasure">
  <xsl:value-of select="//Item/@UnitOfMeasure" /> 
  </xsl:attribute>
 <xsl:attribute name="OrganizationCode">
  <xsl:value-of select="//Item/@OrganizationCode" /> 
  </xsl:attribute>
  </CategoryItem>
  </CategoryItemList>
  </Category>
  </ModifyCategoryItems>
  </xsl:template>
  </xsl:stylesheet>