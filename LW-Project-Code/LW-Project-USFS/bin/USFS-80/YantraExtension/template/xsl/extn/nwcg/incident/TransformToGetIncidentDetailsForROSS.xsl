<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ro="http://nwcg.gov/services/ross/resource_order/1.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<xsl:output indent="yes"/>
	<xsl:template match="/">
		<ro:GetIncidentReq>
			<ro:MessageOriginator>
				<SystemOfOrigin>
					<SystemType>ICBS</SystemType>
					<SystemID />
				</SystemOfOrigin>
				<DispatchUnitID>
					<UnitIDPrefix />
					<UnitIDSuffix />
				</DispatchUnitID>
			</ro:MessageOriginator>
			<ro:IncidentKey>
				<IncidentID>
					<EntityType />
					<EntityID />
					<ApplicationSystem>
						<SystemType />
						<SystemID />
					</ApplicationSystem>
				</IncidentID>
				<NaturalIncidentKey>
					<HostID>
						<UnitIDPrefix><xsl:call-template name="getUnitIDPrefix"/></UnitIDPrefix>
						<UnitIDSuffix><xsl:call-template name="getUnitIDSuffix"/></UnitIDSuffix>
					</HostID>
					<SequenceNumber><xsl:call-template name="getSequenceNumber"/></SequenceNumber>
					<YearCreated><xsl:value-of select="/NWCGIncidentOrder/@Year"/></YearCreated>
				</NaturalIncidentKey>
			</ro:IncidentKey>
		</ro:GetIncidentReq>
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