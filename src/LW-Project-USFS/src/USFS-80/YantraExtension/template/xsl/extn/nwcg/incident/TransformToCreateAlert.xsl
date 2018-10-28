<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output indent="yes" />
	<xsl:template match="/">
			<xsl:element name="Inbox">
				<xsl:attribute name="Description"><xsl:value-of select="/ROSSFailureDoc/@ErrorString" /></xsl:attribute>
				<xsl:attribute name="EnterpriseKey">NWCG</xsl:attribute>
				<xsl:attribute name="InboxType">ALERT</xsl:attribute>
				<xsl:attribute name="Priority">1</xsl:attribute>
				<xsl:attribute name="QueueId">NWCG_INCIDENT</xsl:attribute>
				<xsl:attribute name="DetailDescription"><xsl:value-of select="/ROSSFailureDoc/@ErrorDetailMessage" /></xsl:attribute>
				<xsl:attribute name="AssignedToUserId">nwcgsys</xsl:attribute>
			</xsl:element>
	</xsl:template>
</xsl:stylesheet>
