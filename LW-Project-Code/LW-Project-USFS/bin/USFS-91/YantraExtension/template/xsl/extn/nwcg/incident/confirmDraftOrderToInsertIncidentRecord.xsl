<?xml version="1.0"?>
<xsl:stylesheet  version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:template match="/Order">
      <Shipment	ActualShipmentDate="">
         <ShipmentLines>
          <xsl:apply-templates select="OrderLines/OrderLine"/>
         </ShipmentLines>
      </Shipment>
   </xsl:template>
   <!--Changing the IncidentNo to ToIncidentNo as shipping incident number and receiving incident number are different -->
   <xsl:template match="OrderLine">
		<ShipmentLine 
			ItemID="{Item/@ItemID}"  
			IncidentNo="{/Order/Extn/@ExtnToIncidentNo}"  
			IncidentYear="{/Order/Extn/@ExtnToIncidentYear}"  
			CacheID="{/Order/@ShipNode}" 
			OrderNo="{/Order/@OrderNo}" 
			ProductClass="{Item/@ProductClass}" 
			ActualQuantity="{@OrderedQty}" 
			UnitOfMeasure="{Item/@UnitOfMeasure}" 
			UnitPrice="{LinePriceInfo/@UnitPrice}">
				<ShipmentTagSerials>
					<ShipmentTagSerial 
						Quantity="1" 
						SerialNo="{Extn/@ExtnTrackableId}"/> 
				</ShipmentTagSerials>
		</ShipmentLine>
   </xsl:template>
</xsl:stylesheet>