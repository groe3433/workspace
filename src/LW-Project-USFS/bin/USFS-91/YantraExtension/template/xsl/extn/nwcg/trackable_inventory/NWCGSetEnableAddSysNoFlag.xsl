<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:template match="/*">
   
   <ShipmentList>
   
   <xsl:attribute name="TotalNumberOfRecords">
   <xsl:value-of select="@TotalNumberOfRecords"/>
   </xsl:attribute>
   
   <xsl:attribute name="EnableAddSystemNo">
   
    <xsl:choose><xsl:when test="@TotalNumberOfRecords &gt; 0">
        <xsl:text>N</xsl:text>
      </xsl:when>
      <xsl:otherwise>
      <xsl:text>Y</xsl:text>
      
      </xsl:otherwise>
      </xsl:choose>
   </xsl:attribute>
   
     

         <xsl:copy-of select="child::*" />
         
         </ShipmentList>
         
    
      
   </xsl:template>
   
</xsl:stylesheet>