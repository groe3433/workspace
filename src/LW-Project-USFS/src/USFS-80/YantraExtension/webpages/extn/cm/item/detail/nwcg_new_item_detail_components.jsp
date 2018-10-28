<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/yfsjspcommon/editable_util_lines.jspf" %>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/css/scripts/editabletbl.js"></script>

<%
boolean bAppendOldValue = false;
//	if(!isVoid(errors) || equals(sOperation,"Y") || equals(sOperation,"DELETE"))
//		bAppendOldValue = true;
//	String modifyView = request.getParameter("ModifyView");
//  modifyView = modifyView == null ? "" : modifyView;

%>

<table class="table" ID="Components" cellspacing="0" width="100%" yfcMaxSortingRecords="100" >
<thead>
<tr>
<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/Item/Components/Component/@ComponentItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
<td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/Item/Components/Component/@ComponentOrganizationCode")%>"><yfc:i18n>Organization</yfc:i18n></td>
<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/Item/Components/Component/@ComponentUnitOfMeasure")%>"><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/Item/Components/Component/@KitQuantity")%>"><yfc:i18n>Kit_Quantity</yfc:i18n></td>
</tr>
</thead>
<tbody>
<yfc:loopXML name="Item" binding="xml:/Item/Components/@Component" id="Component">
<tr>
<td class="tablecolumn" nowrap="true">
<yfc:getXMLValue binding="xml:/Component/@ComponentItemID"/>
<input type="hidden" <%if(bAppendOldValue) { %>OldValue="<%=resolveValue("xml:OrigAPIComponent:/Component/@ComponentItemID")%>"  <%}%>
<%=getTextOptions("xml:/Item/Components/Component_" + ComponentCounter + "/@ComponentItemID", "xml:/Component/@ComponentItemID")%>/>
</td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/Component/@ComponentOrganizationCode"/></td>
<td class="tablecolumn" nowrap="true">
<yfc:getXMLValue binding="xml:/Component/@ComponentUnitOfMeasure"/>
<input type="hidden" <%if(bAppendOldValue) { %>OldValue="<%=resolveValue("xml:OrigAPIComponent:/Component/@ComponentUnitOfMeasure")%>"  <%}%>
<%=getTextOptions("xml:/Item/Components/Component_" + ComponentCounter + "/@ComponentUnitOfMeasure", "xml:/Component/@ComponentUnitOfMeasure")%>/>
</td>
<td class="tablecolumn" nowrap="true">
<yfc:getXMLValue binding="xml:/Component/@KitQuantity"/>
<input type="hidden" <%if(bAppendOldValue) { %>OldValue="<%=resolveValue("xml:OrigAPIComponent:/Component/@KitQuantity")%>"  <%}%>
<%=getTextOptions("xml:/Item/Components/Component_" + ComponentCounter + "/@KitQuantity", "xml:/Component/@KitQuantity")%>/>
</td>
</tr>
</yfc:loopXML>
</tbody>
</table>
