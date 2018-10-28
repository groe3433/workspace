<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%  
    String modifyView = request.getParameter("ModifyView");
    modifyView = modifyView == null ? "" : modifyView;
%>
<table class="view" width="100%">
	<tr>
        <td class="detaillabel" ><yfc:i18n>Requested</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/WorkOrder/@QuantityRequested"/></td>
        <td class="detaillabel" ><yfc:i18n>Work_Order_Quantity_Confirmed</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/WorkOrder/@QuantityCompleted"/></td>
	</tr>
	<tr>
        <td class="detaillabel" ><yfc:i18n>Work_Order_Quantity_Allocated</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/WorkOrder/@QuantityAllocated"/></td>
        <td class="detaillabel" ><yfc:i18n>Work_Order_Quantity_Cancelled</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/WorkOrder/@QuantityRemoved"/></td>
	</tr>
	<tr>
        <td class="detaillabel" ><yfc:i18n>Released</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/WorkOrder/@QuantityReleased"/></td>
        <td class="detaillabel" ></td>
        <td/>
	</tr>
</table>