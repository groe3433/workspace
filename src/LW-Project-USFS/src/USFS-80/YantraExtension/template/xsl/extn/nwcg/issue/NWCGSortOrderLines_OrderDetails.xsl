<?xml version="1.0" encoding="utf-8"?>
<!-- 
	This XSL is used to sort OrderLines within Order XML so that ROSSSpecialNeeds value in OrderHeader JSP is 
	populated with sorted values.
	Most of the values in @ExtnRequestNo starts with "S-".  This is ROSS supplied value.  Sometimes user enters
	the value on the screen.  
	We need to take numeric value out of the string and sort based on that.  Sometimes the values contain 
	just numbers (without "S-" prefix).  Sometime they contain "E-", "s-", "$-".
	
	The sorting order is we take everything after "S-" and sort them as numeric value.  This handles 98% of the cases.
	To handle remaining use cases, we sort just numeric values first and last as alphabets.
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="Order">
		<Order>
			<!-- Copy all attributes of Order -->
			<xsl:for-each select="@*">
				<xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
			</xsl:for-each>
			<!-- Copy the following nodes including child nodes -->
			<xsl:copy-of select="AllowedModifications"/>
			<xsl:copy-of select="Extn"/>
			<xsl:copy-of select="PriceInfo"/>
			<xsl:copy-of select="Instructions"/>
			<xsl:copy-of select="OrderHoldTypes"/>
			<xsl:copy-of select="PersonInfoShipTo"/>
			<xsl:copy-of select="PersonInfoBillTo"/>
			<xsl:copy-of select="PaymentMethods"/>
			<xsl:copy-of select="ChargeTransactionDetails"/>
			<xsl:copy-of select="AdditionalAddresses"/>
			<xsl:copy-of select="OrderDates"/>
			<xsl:copy-of select="Notes"/>
			<xsl:copy-of select="OverallTotals"/>
			<xsl:copy-of select="ReturnOrdersForExchange"/>
			<xsl:copy-of select="OrderStatuses"/>
			<xsl:copy-of select="RegulationInfo"/>
			<xsl:copy-of select="ShipToAddress"/>
			<xsl:copy-of select="ShipNode"/>
			<OrderLines>
			<xsl:for-each select="OrderLines/OrderLine">
				<!--  First sort take everything after "S-" and sort them as numeric values (e.g. 'S-123','S-125', 'S-125.2' etc.) -->
				<xsl:sort select="substring-after(Extn/@ExtnRequestNo, '-')" order="ascending" data-type="number" />
				<!--  Second sort is based on numeric values (e.g. '123','125', '135' etc.) -->
				<xsl:sort select="Extn/@ExtnRequestNo" order="ascending" data-type="number" />
				<!--  Third sort is based on alphabets (e.g. 'ABC','ABD', 'aBC' etc.) -->
				<xsl:sort select="Extn/@ExtnRequestNo" order="ascending" data-type="text" case-order="upper-first"/>
					<xsl:copy-of select="."/>
			</xsl:for-each>
			</OrderLines>
		</Order>
	</xsl:template>
</xsl:stylesheet>