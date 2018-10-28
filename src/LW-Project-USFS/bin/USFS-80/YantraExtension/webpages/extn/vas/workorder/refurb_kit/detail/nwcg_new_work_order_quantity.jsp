<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<script>
// changes for CR # 241 : assign the requested quantity to the detail quantity
function updateDtlQuantity(elem)
{
	var val = elem.value;
	var elemDtlList = document.getElementsByTagName("input")
	// find the detail item
	for(index = 0 ;index < elemDtlList.length ; index++)
	{
		var elemDtl = elemDtlList.item(index);

		if(elemDtl.name == "xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity/WorkOrderActivityDtls/WorkOrderActivityDtl/@QuantityRequested")
		{
			// assign the value
			elemDtl.value = val ;
		}
	}
}
</script>
<%  
    String modifyView = request.getParameter("ModifyView");
    modifyView = modifyView == null ? "" : modifyView;
%>
<table class="view" width="100%">
	<tr>
        <td class="detaillabel" ><yfc:i18n>Requested</yfc:i18n></td>
        <td nowrap="true">
        	<input type="text" onblur="onBlurHandler();updateDtlQuantity(this)" class="unprotectedinput" <%=getTextOptions("xml:/WorkOrder/@QuantityRequested")%>/>
        </td>
        <td class="detaillabel" ><yfc:i18n>Work_Order_Quantity_Confirmed</yfc:i18n></td>
<!--        <td class="protectedtext"><yfc:getXMLValue binding="xml:/WorkOrder/@QuantityCompleted"/></td> -->
        <td><input type="text" class="protectedtext" <%=getTextOptions("xml:/WorkOrder/@QuantityCompleted")%> /></td>
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
		
		<!--<Input type="hidden" name="xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity/WorkOrderActivityDtls/WorkOrderActivityDtl/@QuantityRequested" value="<%=resolveValue("xml:/WorkOrder/@QuantityRequested")%>"/>-->

		<Input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity/WorkOrderActivityDtls/WorkOrderActivityDtl/@QuantityRequested")%>/>

		<!-- <td class="detaillabel" ><yfc:i18n>DtlQuantity</yfc:i18n></td>
        <td nowrap="true">
			<Input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity/WorkOrderActivityDtls/WorkOrderActivityDtl/@QuantityRequested")%>/>
        </td>
		-->
	</tr>
</table>
