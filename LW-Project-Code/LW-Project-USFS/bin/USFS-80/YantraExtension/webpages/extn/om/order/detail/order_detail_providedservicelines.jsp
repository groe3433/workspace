<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>

<script language="javascript">
document.body.attachEvent("onunload", processSaveRecordsForOrderLines);
</script>

<table class="table" ID="OrderLines" cellspacing="0" width="100%" >
<thead>
<tr>
<td class="checkboxheader" sortable="no">
<input type="checkbox" value="checkbox" name="checkbox" onclick="doCheckAll(this);"/>
</td>
<td class="tablecolumnheader" nowrap="true" style="width:30px">&nbsp;</td>
<td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/OrderLine/@PrimeLineNo")%>"><yfc:i18n>Line</yfc:i18n></td>
<td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/OrderLine/Item/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/Item/@ItemShortDesc")%>"><yfc:i18n>Description</yfc:i18n></td>
<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/OrderLine/@ShipNode")%>"><yfc:i18n>Ship_Node</yfc:i18n></td>
<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/OrderLine/@PromisedApptStartDate")%>"><yfc:i18n>Appointment</yfc:i18n></td>
<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/OrderLine/@OrderedQty")%>"><yfc:i18n>Line_Qty</yfc:i18n></td>
<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/LineOverallTotals/@LineTotal")%>"><yfc:i18n>Amount</yfc:i18n></td>
<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/@Status")%>"><yfc:i18n>Status</yfc:i18n></td>
</tr>
</thead>
<tbody>
<yfc:loopXML name="Order" binding="xml:/Order/OrderLines/@OrderLine" id="OrderLine">
<%
if(!isVoid(resolveValue("xml:OrderLine:/OrderLine/@Status"))) {
if (equals(getValue("OrderLine","xml:/OrderLine/@ItemGroupCode"),"PS"))
//display line in this inner panel only if it is ItemGroupCode = Provided service
{
%>
<tr>
<yfc:makeXMLInput name="orderLineKey">
<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey"/>
<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey"/>
</yfc:makeXMLInput>
<td class="checkboxcolumn" >
<input type="checkbox" value='<%=getParameter("orderLineKey")%>' name="chkEntityKeyPS"/>

<%/*This hidden input is required by yfc to match up each line attribute that is editable in this row against the appropriate order line # on the server side once you save.  */%>
<input type="hidden" name='OrderLineKey_<%=OrderLineCounter%>' value='<%=resolveValue("xml:/OrderLine/@OrderLineKey")%>' />
<input type="hidden" name='OrderHeaderKey_<%=OrderLineCounter%>' value='<%=resolveValue("xml:/Order/@OrderHeaderKey")%>' />
<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_" + OrderLineCounter + "/@OrderLineKey", "xml:/OrderLine/@OrderLineKey")%> />
<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_" + OrderLineCounter + "/@PrimeLineNo", "xml:/OrderLine/@PrimeLineNo")%> />
<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_" + OrderLineCounter + "/@SubLineNo", "xml:/OrderLine/@SubLineNo")%> />

</td>
<td class="tablecolumn" nowrap="true">
<yfc:hasXMLNode binding="xml:/OrderLine/Instructions/Instruction">
<a <%=getDetailHrefOptions("L01", getParameter("orderLineKey"), "")%>>
<img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.INSTRUCTIONS_COLUMN, "Instructions")%>></a>
</yfc:hasXMLNode>
<% String currentWorkOrderKey = getValue("OrderLine", "xml:/OrderLine/@CurrentWorkOrderKey");
if(!isVoid(currentWorkOrderKey) && !equals("Y", resolveValue("xml:/Order/@isHistory"))) {
%>
<yfc:makeXMLInput name="workOrderKey">
<yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderKey" value="xml:/OrderLine/@CurrentWorkOrderKey"/>
</yfc:makeXMLInput>
<a <%=getDetailHrefOptions("L07", getParameter("workOrderKey"), "")%>>
<img class="columnicon" <%=getImageOptions("/yantra/console/icons/workorders.gif", "View_Work_Order")%>>
</a>
<% } %>
</td>
<td class="tablecolumn" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/@PrimeLineNo")%>">
<% if(showOrderLineNo("Order","Order")) {%>
<a <%=getDetailHrefOptions("L02", getParameter("orderLineKey"), "")%>>
<yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"/></a>
<%} else {%>
<yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"/>
<%}%>
</td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemShortDesc"/></td>
<td class="tablecolumn" nowrap="true">
<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine_" + OrderLineCounter + "/@ShipNode", "xml:/OrderLine/@ShipNode", "xml:/OrderLine/AllowedModifications")%>/>
<img class="lookupicon" onclick="callLookup(this,'shipnode')" <%=yfsGetImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Ship_Node", "xml:/OrderLine/@ShipNode", "xml:/OrderLine/AllowedModifications")%>/>
</td>
<td class="tablecolumn">
<%-- cr 34417 --%>
<%
String sTimeWindow = displayTimeWindow(getValue("OrderLine", "xml:/OrderLine/@PromisedApptStartDate"),getValue("OrderLine", "xml:/OrderLine/@PromisedApptEndDate"), resolveValue("xml:/OrderLine/@Timezone"));

if(!isVoid(sTimeWindow) )
{
%>
<%=sTimeWindow%>
<%=showTimeZoneIcon(resolveValue("xml:OrderLine:/OrderLine/@Timezone"), getLocale().getTimezone())%>
<%	} %>
</td>

<td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/@OrderedQty")%>">
<%	if(isTrue("xml:/OrderLine/@IsStandaloneService") )	{	%>
<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine_" + OrderLineCounter + "/@OrderedQty", "xml:/OrderLine/@OrderedQty", "xml:/OrderLine/AllowedModifications")%> style='width:40px' />
<%	}	else	{	%>
<yfc:getXMLValue binding="xml:/OrderLine/@OrderedQty"/>
<%	}	%>
</td>

<td class="numerictablecolumn" nowrap="true" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/LineOverallTotals/@LineTotal")%>">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<yfc:getXMLValue binding="xml:/OrderLine/LineOverallTotals/@LineTotal"/>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
<td class="tablecolumn">
<%=displayOrderStatus(getValue("OrderLine","xml:/OrderLine/@MultipleStatusesExist"),getValue("OrderLine","xml:/OrderLine/@MaxLineStatusDesc"))%>
</td>
</tr>
<%		}
}   %>
</yfc:loopXML>
</tbody>
</table>
