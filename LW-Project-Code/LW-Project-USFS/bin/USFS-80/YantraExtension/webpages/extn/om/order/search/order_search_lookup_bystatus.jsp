<%@ include file="/om/order/search/order_search_bystatus.jsp" %>
<%if(isShipNodeUser()) {%>
<table class="view">
<tr>
<td class="searchlabel" >
<yfc:i18n>Ship_Node</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell">
<input type="text" class="protectedinput" disabled <%=getTextOptions("xml:/Order/OrderLines/OrderLine/@ShipNode","xml:CurrentUser:/User/@Node")%>/>
<input type="hidden" name="xml:/Order/OrderLine/@ShipNode" value='<%=resolveValue("xml:CurrentUser:/User/@Node")%>'/>
</td>
</tr>
</table>
<%}%>
