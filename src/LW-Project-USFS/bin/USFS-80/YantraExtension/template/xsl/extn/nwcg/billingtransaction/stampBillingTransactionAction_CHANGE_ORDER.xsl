<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:template match="/*">
   
      <xsl:element name="{name()}">
      
         <xsl:attribute name="BillingTransactionAction">CHANGE_ORDER</xsl:attribute>

         <xsl:for-each select="@*">
            <xsl:attribute name="{name()}">
               <xsl:value-of select="." />
            </xsl:attribute>
         </xsl:for-each>

         <xsl:copy-of select="child::*" />
         
      </xsl:element>
      
   </xsl:template>
   
</xsl:stylesheet>