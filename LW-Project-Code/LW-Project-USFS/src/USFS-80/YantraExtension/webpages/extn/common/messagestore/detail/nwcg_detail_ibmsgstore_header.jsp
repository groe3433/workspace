<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfc.core.*" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/exceptionutils.js"></script>

<yfc:makeXMLInput name="messageKey">
	<yfc:makeXMLKey binding="xml:/NWCGInboundMessage/@MessageKey" value="xml:/NWCGInboundMessage/@MessageKey"/>
</yfc:makeXMLInput>

<table class="view" width="100%" >
	<!-- 1ST ROW -->
	<tr>
	    <td><yfc:i18n>Distribution_ID</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGInboundMessage/@DistributionID"/></td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
	</tr>
	
	<tr>	
		<td><yfc:i18n>System_Name</yfc:i18n></td>
			<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGInboundMessage/@SystemName"/></td>
   
    <td><yfc:i18n>Message_Name</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGInboundMessage/@MessageName"/></td>

    </tr>

	<!-- 2ND ROW -->
	<tr>
		<td><yfc:i18n>Message_Type</yfc:i18n></td>
			<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGInboundMessage/@MessageType"/></td>

	    <td><yfc:i18n>Message_Status</yfc:i18n></td>
	        <td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGInboundMessage/@MessageStatus"/></td>
   
    </tr>

	<!-- 3RD ROW -->
	<tr>
		<td><yfc:i18n>Initial_Message_Received</yfc:i18n></td>
			<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGInboundMessage/@Createts"/></td>

	    <td><yfc:i18n>Last_Message_Received</yfc:i18n></td>
	        <td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGInboundMessage/@Modifyts"/></td>

   
    </tr>

	<!-- 4TH ROW -->
	<tr>
		<td><yfc:i18n>Entity_Name</yfc:i18n></td>
			<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGInboundMessage/@EntityName"/></td>

	    <td><yfc:i18n>Entity_Value</yfc:i18n></td>
	        <td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGInboundMessage/@EntityValue"/></td>

   
    </tr>

	<!-- 5TH ROW -->
	<tr>
		<td><yfc:i18n>Entity_Key</yfc:i18n></td>
			<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGInboundMessage/@EntityKey"/></td>

	    <td></td>
	    <td/></td>
   
    </tr>

</table>
