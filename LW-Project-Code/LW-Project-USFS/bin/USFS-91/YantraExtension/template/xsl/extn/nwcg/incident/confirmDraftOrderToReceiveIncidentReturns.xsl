<?xml version="1.0"?>
<xsl:stylesheet  version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:template match="/Order">
      <Return
       IncidentNo="{Extn/@ExtnIncidentNo}"
       IncidentYear="{Extn/@ExtnIncidentYear}"
       CacheID="{@ShipNode}"
       EnterpriseCode="{@EnterpriseCode}">
         <ReturnLines>
          <xsl:apply-templates select="OrderLines/OrderLine"/>
         </ReturnLines>
      </Return>
   </xsl:template>
   
   <xsl:template match="OrderLine">
        <ReturnLine
             DispositionCode="RFI"
             ItemID="{Item/@ItemID}"
             ProductClass="{Item/@ProductClass}"
             UnitOfMeasure="{Item/@UnitOfMeasure}"
             QuantityNRFI="0"
             QuantityRFI="{@OrderedQty}"
             QuantityReturned="{@OrderedQty}"
             QuantityShipped="0"
             QuantityUnsNwtReturn="0"
             QuantityUnsRet="0"
             ReceivedAsComponent="N"
             TrackableID="{Extn/@ExtnTrackableId}"
             UnitPrice="{Item/@UnitCost}"
             IsComponent="False" />
   </xsl:template>
   
</xsl:stylesheet>

