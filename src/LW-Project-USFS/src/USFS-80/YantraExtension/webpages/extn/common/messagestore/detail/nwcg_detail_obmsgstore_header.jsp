<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfc.core.*" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/exceptionutils.js"></script>

<yfc:makeXMLInput name="messageKey">
	<yfc:makeXMLKey binding="xml:/NWCGOutboundMessage/@MessageKey" value="xml:/NWCGOutboundMessage/@MessageKey"/>
</yfc:makeXMLInput>

<table class="view" width="100%">
	<!-- 1ST ROW -->
	<tr>
	
		<td><yfc:i18n>Distribution_ID</yfc:i18n></td>
        <td class="protectedtext" align="left" colspan="2"><yfc:getXMLValue binding="xml:/NWCGOutboundMessage/@DistributionID"/></td>	
		<td></td>
		<td></td>
		<td></td>
		<td></td>
    </tr>

	<tr>
		<td><yfc:i18n>System_Name</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGOutboundMessage/@SystemName"/></td>
   
    	<td><yfc:i18n>Message_Name</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGOutboundMessage/@MessageName"/></td>

	</tr>

	<!-- 2ND ROW -->
	<tr>
		<td><yfc:i18n>Message_Type</yfc:i18n></td>
			<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGOutboundMessage/@MessageType"/></td>

	    <td><yfc:i18n>Message_Status</yfc:i18n></td>
	        <td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGOutboundMessage/@MessageStatus"/></td>
   
    </tr>

	<!-- 3RD ROW -->
	<tr>
		<td><yfc:i18n>Initial_Message_Sent</yfc:i18n></td>
			<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGOutboundMessage/@Createts"/></td>

	    <td><yfc:i18n>Last_Message_Sent</yfc:i18n></td>
	        <td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGOutboundMessage/@Modifyts"/></td>

   
    </tr>

	<!-- 4TH ROW -->
	<tr>
		<td><yfc:i18n>Entity_Name</yfc:i18n></td>
			<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGOutboundMessage/@EntityName"/></td>

	    <td><yfc:i18n>Entity_Value</yfc:i18n></td>
	        <td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGOutboundMessage/@EntityValue"/></td>

   
    </tr>
    
    <!-- 5TH ROW -->
	<tr>
		<td><yfc:i18n>Entity_Key</yfc:i18n></td>
			<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGOutboundMessage/@EntityKey"/></td>

	    <td></td>
	    <td></td>
   
    </tr>



</table>
