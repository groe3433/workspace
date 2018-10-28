<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<table class="view" width="100%">
<tr>
<td colspan="6">
<table>
<tr>
<td class="detaillabel" >
<yfc:i18n>Kit_Code</yfc:i18n>
</td>
<td class="protectedtext">
<%=getComboText("xml:KitCodeList:/CommonCodeList/@CommonCode","CodeShortDescription","CodeValue","xml:/OrderLine/@KitCode", true)%>
</td>
</tr>
</table>
</tr>
</table>

<table class="table" width="100%">
<thead>
<tr>
<td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLine/KitLines/KitLine/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
<td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLine/KitLines/KitLine/@ProductClass")%>"><yfc:i18n>PC</yfc:i18n></td>
<td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLine/KitLines/KitLine/KitLineTranQuantity/@TransactionalUOM")%>"><yfc:i18n>UOM</yfc:i18n></td>
<td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLine/KitLines/KitLine/@ItemDesc")%>"><yfc:i18n>Description</yfc:i18n></td>
<td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLine/KitLines/KitLine/@UnitCost")%>"><yfc:i18n>Unit_Cost</yfc:i18n></td>
<td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLine/KitLines/KitLine/KitLineTranQuantity/@KitQty")%>"><yfc:i18n>Qty_Per_Kit</yfc:i18n></td>
<td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLine/KitLines/KitLine/KitLineTranQuantity/@ComponentQty")%>"><yfc:i18n>Component_Qty</yfc:i18n></td>
</tr>
</thead>
<tbody>
<yfc:loopXML binding="xml:/OrderLine/KitLines/@KitLine" id="KitLine">
<yfc:makeXMLInput name="invItemKey">
<yfc:makeXMLKey binding="xml:/InventoryItem/@ItemID" value="xml:KitLine:/KitLine/@ItemID" ></yfc:makeXMLKey>
<yfc:makeXMLKey binding="xml:/InventoryItem/@ProductClass" value="xml:KitLine:/KitLine/@ProductClass" ></yfc:makeXMLKey>
<yfc:makeXMLKey binding="xml:/InventoryItem/@UnitOfMeasure" value="xml:KitLine:/KitLine/KitLineTranQuantity/@TransactionalUOM" ></yfc:makeXMLKey>
<yfc:makeXMLKey binding="xml:/InventoryItem/@OrganizationCode" value="xml:/OrderLine/Order/@SellerOrganizationCode" ></yfc:makeXMLKey>
<% if(isShipNodeUser()) { %>
<yfc:makeXMLKey binding="xml:/InventoryItem/@ShipNode" value="xml:CurrentUser:/User/@Node"/>
<%} else {%>
<yfc:makeXMLKey binding="xml:/InventoryItem/@ShipNode" value="xml:/OrderLine/@ShipNode" ></yfc:makeXMLKey>
<%}%>
</yfc:makeXMLInput>
<tr>
<td nowrap="true" class="tablecolumn">
<%
String sNode = resolveValue("xml:/OrderLine/@ShipNode");
if(isVoid(sNode)) {%>
<a <%=getDetailHrefOptions("L01",getParameter("invItemKey"),"")%>><yfc:getXMLValue binding="xml:/KitLine/@ItemID"></yfc:getXMLValue></a>&nbsp;
<%} else {%>
<a <%=getDetailHrefOptions("L02",getParameter("invItemKey"),"")%>><yfc:getXMLValue binding="xml:/KitLine/@ItemID"></yfc:getXMLValue></a>&nbsp;
<% } %>
</td>
<td nowrap="true" class="tablecolumn">
<yfc:getXMLValue binding="xml:/KitLine/@ProductClass"></yfc:getXMLValue>
</td>
<td nowrap="true" class="tablecolumn">
<yfc:getXMLValue binding="xml:/KitLine/KitLineTranQuantity/@TransactionalUOM"></yfc:getXMLValue>
</td>
<td nowrap="true"class="tablecolumn">
<yfc:getXMLValue binding="xml:/KitLine/@ItemDesc"></yfc:getXMLValue>
</td>
<td  nowrap="true" class="numerictablecolumn" sortValue="<%=getNumericValue("xml:KitLine:/KitLine/@UnitCost")%>">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<yfc:getXMLValue binding="xml:/KitLine/@UnitCost"></yfc:getXMLValue>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
<td  nowrap="true" class="numerictablecolumn" sortValue="<%=getNumericValue("xml:KitLine:/KitLine/KitLineTranQuantity/@KitQty")%>">
<yfc:getXMLValue binding="xml:/KitLine/KitLineTranQuantity/@KitQty"></yfc:getXMLValue>
</td>
<td  nowrap="true" class="numerictablecolumn" sortValue="<%=getNumericValue("xml:KitLine:/KitLine/KitLineTranQuantity/@ComponentQty")%>">
<yfc:getXMLValue binding="xml:/KitLine/KitLineTranQuantity/@ComponentQty"></yfc:getXMLValue>
</td>
</tr>
</yfc:loopXML>
</tbody>
</table>
