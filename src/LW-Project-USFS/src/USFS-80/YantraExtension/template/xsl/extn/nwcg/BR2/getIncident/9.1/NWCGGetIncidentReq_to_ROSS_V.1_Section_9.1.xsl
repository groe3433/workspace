<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
	<xsl:output indent="yes"/>
	<xsl:template match="/">
		
		<xsl:element name="GetIncidentReq">
			<xsl:attribute name = "xmlNameSpace"><xsl:text>xmlns</xsl:text></xsl:attribute>
			<xsl:element name="Service">
				<xsl:attribute name="Name"><xsl:text>ROSS Service Name Goes Here</xsl:text></xsl:attribute>
					<xsl:element name="MessageOriginator">
						<xsl:element name="SystemOfOrigin">
							<xsl:attribute name="xmlNameSpace2"><xsl:text>xmlns</xsl:text></xsl:attribute>
							<xsl:element name="SystemOfOrigin"><xsl:text>ICBS</xsl:text></xsl:element>
						</xsl:element>
					</xsl:element>
					<xsl:element name="IncidentKey">
						<xsl:element name="NaturalIncidentKey">
							<xsl:attribute name = "xmlNameSpace3"><xsl:text>xmlns</xsl:text></xsl:attribute>
							<xsl:element name="HostID">
								<xsl:element name="UnitIDPrefix"><xsl:call-template name="getUnitIDPrefix"/></xsl:element>
								<xsl:element name="UnitIDSuffix"><xsl:call-template name="getUnitIDSuffix"/></xsl:element>
							</xsl:element>
							<xsl:element name="SequenceNumber"><xsl:call-template name="getSequenceNumber"/></xsl:element>
							<xsl:element name="YearCreated"><xsl:value-of select="/NWCGIncidentOrder/@Year"/></xsl:element>
						</xsl:element>
					</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	

	
	<xsl:template name="getUnitIDPrefix">
         <xsl:value-of select="substring-before(/NWCGIncidentOrder/@IncidentNo,'-')" />
	</xsl:template>
	
	 <xsl:template name="getSequenceNumber">
      
      <xsl:variable name="post" select="substring-after(/NWCGIncidentOrder/@IncidentNo,'-')" />
      <xsl:variable name="last" select="substring-after($post,'-')" />
      <xsl:value-of select="$last"/>
      </xsl:template>
	
	
 <xsl:template name="getUnitIDSuffix">
 
      <xsl:variable name="post" select="substring-after(/NWCGIncidentOrder/@IncidentNo,'-')" />
      <xsl:variable name="last" select="substring-before($post, '-')" />
      <xsl:value-of select="$last"/>
   </xsl:template>

</xsl:stylesheet>