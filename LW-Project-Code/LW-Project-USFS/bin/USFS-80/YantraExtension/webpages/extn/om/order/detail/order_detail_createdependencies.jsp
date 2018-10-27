<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript">
function window.onload() {
if (!yfcBodyOnLoad() && (!document.all('YFCDetailError'))) {
return;
}
yfcDisplayOnlySelectedLines("DependentLines");
}
</script>

<table class="view" width="100%">
<tr>
<td>
<table class="table" ID="DependentLines" cellspacing="0" width="100%" >
<thead>
<tr>
<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:30px"><yfc:i18n>Parent</yfc:i18n></td>
<td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/OrderLine/@PrimeLineNo")%>"><yfc:i18n>Line</yfc:i18n></td>
<td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/OrderLine/Item/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/Item/@ProductClass")%>"><yfc:i18n>PC</yfc:i18n></td>
<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/OrderLineTranQuantity/@TransactionalUOM")%>"><yfc:i18n>UOM</yfc:i18n></td>
<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/Item/@ItemDesc")%>"><yfc:i18n>Description</yfc:i18n></td>
<td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/OrderLine/@ReceivingNode")%>"><yfc:i18n>Recv_Node</yfc:i18n></td>
<td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/OrderLine/@ShipNode")%>"><yfc:i18n>Ship_Node</yfc:i18n></td>
<td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/OrderLine/@ReqShipDate")%>"><yfc:i18n>Ship_Date</yfc:i18n></td>
<td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/OrderLine/OrderLineTranQuantity/@OrderedQty")%>"><yfc:i18n>Line_Qty</yfc:i18n></td>
<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/LineOverallTotals/@LineTotal")%>"><yfc:i18n>Amount</yfc:i18n></td>
<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/@Status")%>"><yfc:i18n>Status</yfc:i18n></td>
</tr>
</thead>
<tbody>
<yfc:loopXML name="Order" binding="xml:/Order/OrderLines/@OrderLine" id="OrderLine">
<yfc:makeXMLInput name="orderLineKey">
<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey"/>
<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey"/>
</yfc:makeXMLInput>
<tr yfcSelectionKey="<%=getParameter("orderLineKey")%>" style="display:none" id="DependencyRow">
<td nowrap="true">
<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_" + OrderLineCounter + "/@OrderLineKey", "xml:/OrderLine/@OrderLineKey")%> />
<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_" + OrderLineCounter + "/@ParentOfDependentGroup", "")%> />
<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_" + OrderLineCounter + "/Dependency/@DependentOnPrimeLineNo", "")%> />
<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_" + OrderLineCounter + "/Dependency/@DependentOnSubLineNo", "")%> />
<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_" + OrderLineCounter + "/@PrimeLineNo", "xml:/OrderLine/@PrimeLineNo")%> />
<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_" + OrderLineCounter + "/@SubLineNo", "xml:/OrderLine/@SubLineNo")%> />
<input type="radio" name="parentGroup" class="radiobutton" value='<%=getValue("Order","xml:/Order/OrderLines/OrderLine_" + OrderLineCounter + "/@OrderLineKey")%>' onclick="setParent('<%=OrderLineCounter%>')"/>
</td>
<td class="tablecolumn" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/@PrimeLineNo")%>">
<% if(showOrderLineNo("Order","Order")) {%>
<a <%=getDetailHrefOptions("L01", getParameter("orderLineKey"), "")%>>
<yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"/></a>
<%} else {%>
<yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"/>
<%}%>
</td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ProductClass"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/OrderLineTranQuantity/@TransactionalUOM"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemDesc"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/@ReceivingNode"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/@ShipNode"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/@ReqShipDate"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/OrderLineTranQuantity/@OrderedQty"/></td>
<td class="numerictablecolumn" nowrap="true" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/LineOverallTotals/@LineTotal")%>">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;
<yfc:getXMLValue binding="xml:/OrderLine/LineOverallTotals/@LineTotal"/>&nbsp;
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
<td class="tablecolumn">
<a <%=getDetailHrefOptions("L02", getParameter("orderLineKey"),"ShowReleaseNo=Y")%>>
<%=displayOrderStatus(getValue("OrderLine","xml:/OrderLine/@MultipleStatusesExist"),getValue("OrderLine","xml:/OrderLine/@MaxLineStatusDesc"))%></a>
</td>
</tr>
</yfc:loopXML>
</tbody>
</table>
</td>
</tr>
<tr>
<td>
<fieldset>
<legend><yfc:i18n>Dependency_Properties</yfc:i18n></legend>
<table class="view" width="100%">
<tr>
<td>
<input type="radio" class="radiobutton" id="DependencyShippingRule1" <%=yfsGetRadioOptions( "xml:/Order/OrderLines/OrderLine/@DependencyShippingRule", "","01","xml:/Order/AllowedModifications")%> onclick="setMergeNodeState(this.value)"/>
<yfc:i18n>Ship_Together</yfc:i18n>
</td>
<td>
<input type="radio" class="radiobutton" id="DependencyShippingRule2" <%=yfsGetRadioOptions( "xml:/Order/OrderLines/OrderLine/@DependencyShippingRule", "","02","xml:/Order/AllowedModifications")%> onclick="setMergeNodeState(this.value)"/>
<yfc:i18n>Deliver_Together</yfc:i18n>
</td>
<td class="detaillabel">
<yfc:i18n>Merge_Node</yfc:i18n>
</td>
<td>
<input type="text" id="MergeNode" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/@MergeNode","xml:/Order/AllowedModifications")%>/>
<img class="lookupicon" onclick="callLookup(this,'shipnode')" disabled <%=yfsGetImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_For_Merge_Node", "xml:/Order/OrderLines/OrderLine/@MergeNode", "xml:/Order/AllowedModifications")%> name="NodeLookUp"/>
</td>
</tr>
</table>
</fieldset>
</td>
</tr>
</table>
