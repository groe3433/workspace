<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<yfc:callAPI apiID="AP1"/>
 <table class="view" width="100%">
	<tr>
		<yfc:makeXMLInput name="MoveRequestKey">
            <yfc:makeXMLKey binding="xml:/MoveRequestLine/MoveRequest/@MoveRequestNo" value="xml:/MoveRequestLine/MoveRequest/@MoveRequestNo" />
			<yfc:makeXMLKey binding="xml:/MoveRequestLine/MoveRequest/@Node" value="xml:/MoveRequestLine/MoveRequest/@Node" />			
        </yfc:makeXMLInput>		
		<td class="detaillabel" ><yfc:i18n>Shipment_#</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/MoveRequest/@ShipmentKey" name="MoveRequestLine"></yfc:getXMLValue></td>
		<td class="detaillabel" ><yfc:i18n>Wave_#</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/MoveRequest/@WaveKey" name="MoveRequestLine"></yfc:getXMLValue></td>
		<td></td>
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Work_Order_#</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/MoveRequest/@WorkOrderKey" name="MoveRequestLine"></yfc:getXMLValue></td>
		<td></td>
		<td></td>
		<td></td>
	</tr>
	<tr>
		<td class="detaillabel"><yfc:i18n>Start_No_Earlier_Than</yfc:i18n></td>
		<td class="protectedtext" ><yfc:getXMLValue binding="xml:/MoveRequestLine/MoveRequest/@StartNoEarlierThan" name="MoveRequestLine"></yfc:getXMLValue></td>
		<td class="detaillabel"><yfc:i18n>Finish_No_Later_Than</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequestLine/MoveRequest/@FinishNoLaterThan" name="MoveRequestLine"></yfc:getXMLValue></td>

		<td><input type="hidden" value='<%=getParameter("MoveRequestKey")%>' name="MoveRequestKey"/> </td>
	</tr>	
</table>