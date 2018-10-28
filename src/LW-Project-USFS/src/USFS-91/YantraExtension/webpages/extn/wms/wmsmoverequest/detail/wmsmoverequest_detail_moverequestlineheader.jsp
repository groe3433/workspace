<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
 <table class="view" width="100%">	
	<tr>
		<td class="detaillabel" ><yfc:i18n>Activity_Group</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequest/FromActivityGroup/@Description" name="MoveRequest"></yfc:getXMLValue></td>
		<td class="detaillabel" ><yfc:i18n>Node</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequest/@Node" name="MoveRequest"></yfc:getXMLValue></td>
		<td></td>
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Move_Request_#</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequest/@MoveRequestNo" name="MoveRequest"></yfc:getXMLValue></td>
		<td class="detaillabel" ><yfc:i18n>Priority</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValueI18NDB binding="xml:/MoveRequest/Priority/@Description" name="MoveRequest"></yfc:getXMLValueI18NDB></td>
		<td></td>
	</tr>	
	<tr>
		<td class="detaillabel"><yfc:i18n>Requested_By</yfc:i18n></td>
		<td class="protectedtext" ><yfc:getXMLValue binding="xml:/MoveRequest/@RequestUserId" name="MoveRequest"></yfc:getXMLValue></td>
		<td class="detaillabel"><yfc:i18n>Status</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValueI18NDB binding="xml:/MoveRequest/Status/@Description" name="MoveRequest"></yfc:getXMLValueI18NDB></td>
		<td></td>
	</tr>	
	<tr>
		<td class="detaillabel"><yfc:i18n>Has_Exceptions</yfc:i18n></td>
		<td class="protectedtext" ><yfc:getXMLValue binding="xml:/MoveRequest/@HasExceptions" name="MoveRequest"></yfc:getXMLValue></td>		
		<td></td>
		<td></td>
		<td></td>
	</tr>	
</table>