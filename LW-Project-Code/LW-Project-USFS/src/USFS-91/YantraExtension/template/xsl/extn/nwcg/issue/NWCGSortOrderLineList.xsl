<?xml version="1.0" encoding="utf-8"?>
<!-- 
	This XSL is used to sort OrderLines within Incident/Issue JSP.
	Most of the values in @ExtnRequestNo starts with "S-".  This is ROSS supplied value.  Sometimes user enters
	the value on the screen.  
	We need to take numeric value out of the string and sort based on that.  Sometimes the values contain 
	just numbers (without "S-" prefix).  Sometime they contain "E-", "s-", "$-".
	
	The sorting order is we take everything after "S-" and sort them as numeric value.  This handles 98% of the cases.
	To handle remaining use cases, we sort just numeric values first and last as alphabets.
	This XSL has a xsl:when condition.  It only keeps the orderlines from "FromPrimeLineNo" to "ToPrimeLineNo".  
	Removes the remaining orderlines from XML.
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="OrderLineList">
		<OrderLineList>
			<!-- Copy all attributes of OrderLineList -->
			<xsl:for-each select="@*">
				<xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
			</xsl:for-each>
			<xsl:variable name="endPosition" select="@ToPrimeLineNo" />
			<xsl:variable name="startPosition" select="@FromPrimeLineNo" />
			<xsl:for-each select="OrderLine">
				<!--  First sort take everything after "S-" and sort them as numeric values (e.g. 'S-123','S-125', 'S-125.2' etc.) -->
				<xsl:sort select="substring-after(Extn/@ExtnRequestNo, '-')" order="ascending" data-type="number" />
				<!--  Second sort is based on numeric values (e.g. '123','125', '135' etc.) -->
				<xsl:sort select="Extn/@ExtnRequestNo" order="ascending" data-type="number" />
				<!--  Third sort is based on alphabets (e.g. 'ABC','ABD', 'aBC' etc.) -->
				<xsl:sort select="Extn/@ExtnRequestNo" order="ascending" data-type="text" case-order="upper-first"/>
				<xsl:choose>
					<xsl:when test="(position() &gt;= $startPosition) and (position() &lt;= $endPosition)">
						<xsl:copy-of select="."/>
					</xsl:when>
					<xsl:otherwise></xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</OrderLineList>
	</xsl:template>
</xsl:stylesheet>