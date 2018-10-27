<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<table width="100%" class="view">
<tr>
	<td class="detaillabel" ><yfc:i18n>Move_Request_#</yfc:i18n></td>
    <td class="protectedtext" nowrap="true">
		<yfc:getXMLValue name="MoveRequestLine"  binding="xml:/MoveRequestLine/MoveRequest/@MoveRequestNo" />
    </td>
	<td class="detaillabel" ><yfc:i18n>Target_Location</yfc:i18n></td>
    <td class="protectedtext" nowrap="true">
		<yfc:getXMLValue name="MoveRequestLine"  binding="xml:/MoveRequestLine/@TargetLocationId" />
    </td>
	<td class="detaillabel" ><yfc:i18n>Source_Location</yfc:i18n></td>
    <td class="protectedtext" nowrap="true">
		<yfc:getXMLValue name="MoveRequestLine"  binding="xml:/MoveRequestLine/@SourceLocationId" />
    </td>	
</tr>
<tr>
	<td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
    <td class="protectedtext" nowrap="true">
		<yfc:getXMLValue name="MoveRequestLine"  binding="xml:/MoveRequestLine/@EnterpriseCode" />
    </td>		
	<td class="detaillabel" ><yfc:i18n>Requested_Quantity</yfc:i18n></td>
    <td class="protectedtext" nowrap="true">
         <yfc:getXMLValue name="MoveRequestLine"  binding="xml:/MoveRequestLine/@RequestQuantity"/>
     </td>
	<td class="detaillabel" ><yfc:i18n>Is_Cancelled</yfc:i18n></td>
    <td class="protectedtext" nowrap="true">
		<yfc:getXMLValue name="MoveRequestLine"  binding="xml:/MoveRequestLine/@CancelledFlag"/>
    </td>		
</tr>
<tr>
	<td class="detaillabel" ><yfc:i18n>Released_Quantity</yfc:i18n></td>
    <td class="protectedtext" nowrap="true">
		<yfc:getXMLValue name="MoveRequestLine"  binding="xml:/MoveRequestLine/@ReleasedQuantity" />
    </td>		
	<td class="detaillabel" ><yfc:i18n>Released</yfc:i18n></td>
    <td class="protectedtext" nowrap="true">
	    <yfc:getXMLValue name="MoveRequestLine"  binding="xml:/MoveRequestLine/@ReleasedFlag"/>
    </td>
	<td class="detaillabel" ><yfc:i18n>Has_Exceptions</yfc:i18n></td>
    <td class="protectedtext" nowrap="true">
		<yfc:getXMLValue name="MoveRequestLine"  binding="xml:/MoveRequestLine/@HasExceptions"/>
    </td>
</tr>
</table>