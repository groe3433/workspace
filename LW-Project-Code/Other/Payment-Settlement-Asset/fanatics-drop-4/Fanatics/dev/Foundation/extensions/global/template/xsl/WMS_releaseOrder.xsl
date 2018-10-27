<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
	<OrderRelease>
	<xsl:attribute name="CarrierServiceCode">
        <xsl:value-of select="//OrderRelease/@CarrierServiceCode"/>
  	</xsl:attribute>
  	<xsl:attribute name="Currency">
        <xsl:value-of select="//OrderRelease/@Currency"/>
  	</xsl:attribute>
  	<xsl:attribute name="EnterpriseCode">
        <xsl:value-of select="//OrderRelease/@EnterpriseCode"/>
  	</xsl:attribute>
  	<xsl:attribute name="MaxOrderReleaseStatus">
        <xsl:value-of select="//OrderRelease/@MaxOrderReleaseStatus"/>
  	</xsl:attribute>
  		<xsl:attribute name="MinOrderReleaseStatus">
        <xsl:value-of select="//OrderRelease/@MinOrderReleaseStatus"/>
  	</xsl:attribute>
  	<xsl:attribute name="NotifyAfterShipmentFlag">
        <xsl:value-of select="//OrderRelease/@NotifyAfterShipmentFlag"/>
  	</xsl:attribute>
  	<xsl:attribute name="OrderDate">
        <xsl:value-of select="//OrderRelease/@OrderDate"/>
  	</xsl:attribute>
  	<xsl:attribute name="OrderType">
        <xsl:value-of select="//OrderRelease/@OrderType"/>
  	</xsl:attribute>
  	<xsl:attribute name="OtherCharges">
        <xsl:value-of select="//OrderRelease/@OtherCharges"/>
  	</xsl:attribute>
  	<xsl:attribute name="PacklistType">
        <xsl:value-of select="//OrderRelease/@PacklistType"/>
  	</xsl:attribute>
  	<xsl:attribute name="PriorityCode">
        <xsl:value-of select="//OrderRelease/@PriorityCode"/>
  	</xsl:attribute>
  	<xsl:attribute name="ReleaseNo">
        <xsl:value-of select="//OrderRelease/@ReleaseNo"/>
  	</xsl:attribute>
  	<xsl:attribute name="ReleaseSeqNo">
        <xsl:value-of select="//OrderRelease/@ReleaseSeqNo"/>
  	</xsl:attribute>
  	<xsl:attribute name="ReqCancelDate">
        <xsl:value-of select="//OrderRelease/@ReqCancelDate"/>
  	</xsl:attribute>
  	<xsl:attribute name="ReqDeliveryDate">
        <xsl:value-of select="//OrderRelease/@ReqDeliveryDate"/>
  	</xsl:attribute>
  	 <xsl:attribute name="ReqShipDate">
        <xsl:value-of select="//OrderRelease/@ReqShipDate"/>
  	</xsl:attribute>
  	 <xsl:attribute name="SalesOrderNo">
        <xsl:value-of select="//OrderRelease/@SalesOrderNo"/>
  	</xsl:attribute>
  	 <xsl:attribute name="ShipAdviceNo">
        <xsl:value-of select="//OrderRelease/@ShipAdviceNo"/>
  	</xsl:attribute>
  	<xsl:attribute name="ShipCompleteFlag">
        <xsl:value-of select="//OrderRelease/@ShipCompleteFlag"/>
  	</xsl:attribute>
  	<xsl:attribute name="ShipNode">
        <xsl:value-of select="//OrderRelease/@ShipNode"/>
  	</xsl:attribute>
  	<xsl:attribute name="ShipToKey">
        <xsl:value-of select="//OrderRelease/@ShipToKey"/>
  	</xsl:attribute>
  	<xsl:attribute name="Status">
        <xsl:value-of select="//OrderRelease/@Status"/>
  	</xsl:attribute>
  	<xsl:attribute name="SupplierCode">
        <xsl:value-of select="//OrderRelease/@SupplierCode"/>
  	</xsl:attribute>
  	 <xsl:attribute name="SupplierName">
        <xsl:value-of select="//OrderRelease/@SupplierName"/>
  	</xsl:attribute>
	<Order>
		<xsl:attribute name="DocumentType">
         <xsl:value-of select="//OrderRelease/Order/@DocumentType"/>
  		</xsl:attribute>
  		<xsl:attribute name="OrderNo">
         <xsl:value-of select="//OrderRelease/Order/@OrderNo"/>
  		</xsl:attribute>
  		<xsl:attribute name="EnterpriseCode">
         <xsl:value-of select="//OrderRelease/Order/@EnterpriseCode"/>
  		</xsl:attribute>
  		<Instructions>
  		<xsl:for-each select="OrderRelease/Order/Instructions/Instruction">
  			<Instruction>
  				<xsl:attribute name="InstructionText">
         			<xsl:value-of select="./@InstructionText"/>
  				</xsl:attribute>
  				<xsl:attribute name="InstructionType">
         			<xsl:value-of select="./@InstructionType"/>
  				</xsl:attribute>
  				<xsl:attribute name="SequenceNo">
         			<xsl:value-of select="./@SequenceNo"/>
  				</xsl:attribute>
  			</Instruction>
  		</xsl:for-each>
  		</Instructions>
  		<CustomAttributes>
  			<xsl:attribute name="CustomerOrderNo">
         		<xsl:value-of select="//OrderRelease/Order/CustomAttributes/@CustomerOrderNo"/>
  			</xsl:attribute>
  		</CustomAttributes>
	</Order>
	<PersonInfoShipTo>
		<xsl:attribute name="AddressLine1">
         		<xsl:value-of select="//OrderRelease/PersonInfoShipTo/@AddressLine1"/>
  			</xsl:attribute>
  			<xsl:attribute name="AddressLine2">
         		<xsl:value-of select="//OrderRelease/PersonInfoShipTo/@AddressLine2"/>
  			</xsl:attribute>
  			<xsl:attribute name="AddressLine3">
         		<xsl:value-of select="//OrderRelease/PersonInfoShipTo/@AddressLine4"/>
  			</xsl:attribute>
  			<xsl:attribute name="City">
         		<xsl:value-of select="//OrderRelease/PersonInfoShipTo/@City"/>
  			</xsl:attribute>
  			<xsl:attribute name="Company">
         		<xsl:value-of select="//OrderRelease/PersonInfoShipTo/@Company"/>
  			</xsl:attribute>
  			<xsl:attribute name="Country">
         		<xsl:value-of select="//OrderRelease/PersonInfoShipTo/@Country"/>
  			</xsl:attribute>
  			<xsl:attribute name="DayPhone">
         		<xsl:value-of select="//OrderRelease/PersonInfoShipTo/@DayPhone"/>
  			</xsl:attribute>
  			<xsl:attribute name="EMailID">
         		<xsl:value-of select="//OrderRelease/PersonInfoShipTo/@EMailID"/>
  			</xsl:attribute>
  			<xsl:attribute name="FirstName">
         		<xsl:value-of select="//OrderRelease/PersonInfoShipTo/@FirstName"/>
  			</xsl:attribute>
  			<xsl:attribute name="LastName">
         		<xsl:value-of select="//OrderRelease/PersonInfoShipTo/@LastName"/>
  			</xsl:attribute>
  			<xsl:attribute name="State">
         		<xsl:value-of select="//OrderRelease/PersonInfoShipTo/@State"/>
  			</xsl:attribute>
  			<xsl:attribute name="Suffix">
         		<xsl:value-of select="//OrderRelease/PersonInfoShipTo/@Suffix"/>
  			</xsl:attribute>
  			<xsl:attribute name="ZipCode">
         		<xsl:value-of select="//OrderRelease/PersonInfoShipTo/@ZipCode"/>
  			</xsl:attribute>
	</PersonInfoShipTo>
	<xsl:for-each select="OrderRelease/OrderLine">			
	<OrderLine>
  			<xsl:attribute name="PrimeLineNo">
         		<xsl:value-of select="./@PrimeLineNo"/>
  			</xsl:attribute>
  			<xsl:attribute name="SubLineNo">
         		<xsl:value-of select="./@SubLineNo"/>
  			</xsl:attribute>
  			<Item>
  				<xsl:attribute name="CountryOfOrigin">
         			<xsl:value-of select="./Item/@CountryOfOrigin"/>
  				</xsl:attribute>
  				<xsl:attribute name="ItemDesc">
         			<xsl:value-of select="./Item/@ItemDesc"/>
  				</xsl:attribute>
  				<xsl:attribute name="ItemID">
         			<xsl:value-of select="./Item/@ItemID"/>
  				</xsl:attribute>
  				<xsl:attribute name="ItemShortDesc">
         			<xsl:value-of select="./Item/@ItemShortDesc"/>
  				</xsl:attribute>
  				<xsl:attribute name="ItemWeight">
         			<xsl:value-of select="./Item/@ItemWeight"/>
  				</xsl:attribute>
  				<xsl:attribute name="ItemWeightUOM">
         			<xsl:value-of select="./Item/@ItemWeightUOM"/>
  				</xsl:attribute>
  				<xsl:attribute name="ProductClass">
         			<xsl:value-of select="./Item/@ProductClass"/>
  				</xsl:attribute>
  				<xsl:attribute name="UnitCost">
         			<xsl:value-of select="./Item/@UnitCost"/>
  				</xsl:attribute>
  				<xsl:attribute name="UnitOfMeasure">
         			<xsl:value-of select="./Item/@UnitOfMeasure"/>
  				</xsl:attribute>
  				<Extn>
  					<xsl:attribute name="ExtnShipAlone">
         				<xsl:value-of select="./ItemDetails/Extn/@ExtnShipAlone"/>
  					</xsl:attribute>
  				</Extn>
  			</Item>
  			<OrderDates>  			
  			  <xsl:for-each select="./OrderDates/OrderDate[@DateTypeId='MAX_DELIVERY' or @DateTypeId='MIN_DELIVERY']">	
  				<OrderDate>
  					<xsl:attribute name="CommittedDate">
         				<xsl:value-of select="./@CommittedDate"/>
  					</xsl:attribute>
  					<xsl:attribute name="DateTypeId">
         				<xsl:value-of select="./@DateTypeId"/>
  					</xsl:attribute>
  				</OrderDate>
  			  </xsl:for-each>
  			</OrderDates>
  			<OrderStatuses>
  			  <xsl:for-each select="./OrderStatuses/OrderStatus">	
  			    <OrderStatus>
  					<xsl:attribute name="Status">
         				<xsl:value-of select="./@Status"/>
  					</xsl:attribute>
  					<xsl:attribute name="StatusDate">
         				<xsl:value-of select="./@StatusDate"/>
  					</xsl:attribute>
  					<xsl:attribute name="StatusDescription">
         				<xsl:value-of select="./@StatusDescription"/>
  					</xsl:attribute>
  					<xsl:attribute name="StatusQty">
         				<xsl:value-of select="./@StatusQty"/>
  					</xsl:attribute>
  					<xsl:attribute name="TotalQuantity">
         				<xsl:value-of select="./@TotalQuantity"/>
  					</xsl:attribute>
  			    </OrderStatus>
  			  </xsl:for-each>  			
  			</OrderStatuses>
  		 <LinePackListPriceInfo>
  		 	<xsl:attribute name="Charges">
         		<xsl:value-of select="./LinePackListPriceInfo/@Charges"/>
  			</xsl:attribute>
  			<xsl:attribute name="UnitPrice">
         		<xsl:value-of select="./LinePackListPriceInfo/@UnitPrice"/>
  			</xsl:attribute>
  		 </LinePackListPriceInfo> 			 
	</OrderLine>
	</xsl:for-each>
		<CustomAttributes>
  			<xsl:attribute name="ReleaseControlNbr">
         		<xsl:value-of select="//OrderRelease/CustomAttributes/@ReleaseControlNbr"/>
  			</xsl:attribute>
		</CustomAttributes>
	</OrderRelease>	
	</xsl:template>
</xsl:stylesheet>