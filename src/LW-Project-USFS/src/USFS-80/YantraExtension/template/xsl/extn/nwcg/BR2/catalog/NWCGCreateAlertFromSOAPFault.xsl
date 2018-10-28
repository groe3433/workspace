<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cat="http://nwcg.gov/services/ross/catalog/1.1" xmlns:nwcg="http://nwcg.gov/services/ross/common_types/1.1" xmlns:rch="http://nwcg.gov/services/ross/resource_clearinghouse/1.1" xmlns:ro="http://nwcg.gov/services/ross/resource_order/1.1" xmlns:ron="http://nwcg.gov/services/ross/resource_order_notification/1.1" xmlns:rsc="http://nwcg.gov/services/ross/resource/1.1" xmlns:rscn="http://nwcg.gov/services/ross/resource_notification/1.1" xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="1.0">
	<xsl:output indent="yes"/>
	<xsl:template match="/">
		<xsl:element name="Inbox">
			<xsl:attribute name="Description"><xsl:value-of select="/Fault/@Description"/></xsl:attribute>
			<xsl:attribute name="InboxType">ALERT</xsl:attribute>
			<xsl:attribute name="Priority">1</xsl:attribute>
			<xsl:attribute name="QueueId"><xsl:value-of select="/Fault/@QueueId"/></xsl:attribute>
			<xsl:attribute name="DetailDescription"><xsl:value-of select="/Fault/@DetailDescription"/></xsl:attribute>
			<xsl:element name="InboxReferencesList">
				<xsl:element name="InboxReferences">
					<xsl:attribute name="Name">SOAP-MESSAGE</xsl:attribute>
					<xsl:attribute name="ReferenceType">TEXT</xsl:attribute>
					<xsl:attribute name="Value"><xsl:value-of select="/Fault/@DetailDescription"/></xsl:attribute>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
