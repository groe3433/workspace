<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ro="http://nwcg.gov/services/ross/resource_order/1.1" xmlns:tnsa="http://nwcg.gov/services/ross/common_types/1.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<xsl:output indent="yes"/>
	<xsl:template match="/">
		<ro:RegisterIncidentInterestReq>
			<xsl:attribute name="EntityName">INCIDENT</xsl:attribute>
			<xsl:attribute name="EntityValue"><xsl:value-of select="/NWCGIncidentOrder/@IncidentNo"/> - <xsl:value-of select="/NWCGIncidentOrder/@Year"/></xsl:attribute>
			<xsl:attribute name="EntityKey"><xsl:value-of select="/NWCGIncidentOrder/@IncidentKey"/></xsl:attribute>
			<xsl:attribute name="messageName">RegisterIncidentInterestReq</xsl:attribute>
			
			<xsl:attribute name="NWCGUSERID">
				<xsl:value-of select="/NWCGIncidentOrder/@NWCGUSERID"/>
			</xsl:attribute>
			<xsl:attribute name="OperationType">
                                <xsl:value-of select="/NWCGIncidentOrder/@OperationType"/>
                        </xsl:attribute>

			<ro:MessageOriginator>
					<SystemOfOrigin>
						<SystemType>ICBS</SystemType>
						<SystemID />
					</SystemOfOrigin>
			</ro:MessageOriginator>
			<ro:IncidentKey>
				<NaturalIncidentKey>
					<HostID>
						<UnitIDPrefix><xsl:call-template name="getUnitIDPrefix"/></UnitIDPrefix>
						<UnitIDSuffix><xsl:call-template name="getUnitIDSuffix"/></UnitIDSuffix>
					</HostID>
					<SequenceNumber><xsl:call-template name="getSequenceNumber"/></SequenceNumber>
					<YearCreated><xsl:value-of select="/NWCGIncidentOrder/@Year"/></YearCreated>
				</NaturalIncidentKey>
			</ro:IncidentKey>
			<ro:RegisterInterestInd>false</ro:RegisterInterestInd>
		</ro:RegisterIncidentInterestReq>
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