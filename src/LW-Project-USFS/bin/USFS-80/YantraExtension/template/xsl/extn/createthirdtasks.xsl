<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0">
    <xsl:output indent="yes"/>
    <xsl:template match="/">
	<xsl:element name="Message">
		<xsl:attribute name="FlowName">
			<xsl:text>CREATE_VAR_TASK_79_THIRD.3001.ex</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="TransactionKey">
			<xsl:text>200910141117104034179</xsl:text>
		</xsl:attribute>
		<xsl:element name="AgentDetails">
			<xsl:element name="MessageXml">
				<xsl:attribute name="Action">
					<xsl:text>Get</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="NumRecordsToBuffer">
					<xsl:text>5000</xsl:text>
				</xsl:attribute>
			</xsl:element>
		</xsl:element>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>
