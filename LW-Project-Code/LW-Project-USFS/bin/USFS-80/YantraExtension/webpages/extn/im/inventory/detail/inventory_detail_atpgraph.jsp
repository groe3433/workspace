<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<table width="100%" class="view">
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Availability_View</yfc:i18n>&nbsp;
        <select name="xml:/InventoryInformation/Item/@PeriodicalLength" class="combobox" OldValue="<%=resolveValue("xml:/InventoryInformation/Item/@PeriodicalLength")%>" onchange="if(validateControlValues())yfcChangeDetailView(getCurrentViewId())">
            <option value="0" <%if ("0".equals(resolveValue("xml:/InventoryInformation/Item/@PeriodicalLength"))) {%> SELECTED <%}%>><yfc:i18n>All</yfc:i18n></option>
            <option value="1" <%if ("1".equals(resolveValue("xml:/InventoryInformation/Item/@PeriodicalLength"))) {%> SELECTED <%}%>><yfc:i18n>Weekly</yfc:i18n></option>
            <option value="2" <%if ("2".equals(resolveValue("xml:/InventoryInformation/Item/@PeriodicalLength"))) {%> SELECTED <%}%>><yfc:i18n>Monthly</yfc:i18n></option>
        </select>
    </td>
<tr>
<% if(equals(resolveValue("xml:/InventoryInformation/Item/@TrackedEverywhere"),"Y")){%>
<tr>
    <td>
	<%if ( "Y".equals(request.getParameter(YFCUIBackendConsts.YFC_IN_POPUP)) ) {%>
        	<div style="width:850px;height:245px;overflow:auto">
	<%} else { %>
		<div style="width:975px;height:245px;overflow:auto">
	<% } %>
            <img src="<%=request.getContextPath()%>/inventory/ATPgraph/graph?requestID=<%=getParameter("requestID")%>" />
        </div>
    </td>
</tr>
<%}%>
<tr>
    <td>
        <jsp:include page="/im/inventory/detail/inventory_detail_atptable.jsp" flush="true"/>
    </td>
</tr>
</table>