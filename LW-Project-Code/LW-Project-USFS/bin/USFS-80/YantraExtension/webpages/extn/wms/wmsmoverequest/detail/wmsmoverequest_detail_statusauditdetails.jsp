<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<yfc:callAPI apiID="AP1"/>
 <table class="view" width="100%">
	<tr>
		<yfc:makeXMLInput name="MoveRequestStatusAuditKey">
			<yfc:makeXMLKey binding="xml:/MoveRequestStatusAudit/MoveRequest/@MoveRequestKey" value="xml:/MoveRequestStatusAudit/MoveRequest/@MoveRequestKey" />
            <yfc:makeXMLKey binding="xml:/MoveRequestStatusAudit/@MoveRequestStatusAuditKey" value="xml:/MoveRequestStatusAudit/@MoveRequestStatusAuditKey" />
        </yfc:makeXMLInput>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Old_Status</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValueI18NDB binding="xml:/MoveRequestStatusAudit/@OldStatus" /></td>
		<td class="detaillabel" ><yfc:i18n>Old_Status_Date</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestStatusAudit/@OldStatusDate" > </yfc:getXMLValue></td>
		<td></td>
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>New_Status</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValueI18NDB binding="xml:/MoveRequestStatusAudit/@NewStatus" /></td>
		<td class="detaillabel" ><yfc:i18n>New_Status_Date</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestStatusAudit/@NewStatusDate" > </yfc:getXMLValue></td>
		<td><input type="hidden" value='<%=getParameter("MoveRequestStatusAuditKey")%>' name="EntityKey"/> </td>
	</tr>
</table>