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

<table class="table" ID="AssociationList" cellspacing="0" width="100%" yfcMaxSortingRecords="100" >
<thead>
<tr>
<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/AssociationList/Association/Item/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
<td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/AssociationList/Association/Item/@OrganizationCode")%>"><yfc:i18n>Organization</yfc:i18n></td>
<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/AssociationList/Association/Item/@UnitOfMeasure")%>"><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/AssociationList/Association/@AssociatedQuantity")%>"><yfc:i18n>Quantity</yfc:i18n></td>
<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/AssociationList/Association/@EffectiveFrom")%>"><yfc:i18n>Effective_From</yfc:i18n></td>
<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/AssociationList/Association/@EffectiveTo")%>"><yfc:i18n>Effective_To</yfc:i18n></td>
</tr>
</thead>
<tbody>
<yfc:loopXML name="AssociationList" binding="xml:/AssociationList/@Association" id="Association">
<tr>
<td class="tablecolumn" nowrap="true">
<yfc:getXMLValue binding="xml:/Association/Item/@ItemID"/>
<input type="hidden" <%if(bAppendOldValue) { %>OldValue="<%=resolveValue("xml:OrigAPIAssociation:/Association/Item/@ItemID")%>"  <%}%>
<%=getTextOptions("xml:/AssociationList/Association_" + AssociationCounter + "/Item/@ItemID", "xml:/Association/Item/@ItemID")%>/>
</td>
<td class="tablecolumn" nowrap="true">
<yfc:getXMLValue binding="xml:/Association/Item/@OrganizationCode"/>
<input type="hidden" <%if(bAppendOldValue) { %>OldValue="<%=resolveValue("xml:OrigAPIAssociation:/Association/Item/@OrganizationCode")%>"  <%}%>
<%=getTextOptions("xml:/AssociationList/Association_" + AssociationCounter + "/Item/@OrganizationCode", "xml:/Association/Item/@OrganizationCode")%>/>
</td>
<td class="tablecolumn" nowrap="true">
<yfc:getXMLValue binding="xml:/Association/Item/@UnitOfMeasure"/>
<input type="hidden" <%if(bAppendOldValue) { %>OldValue="<%=resolveValue("xml:OrigAPIAssociation:/Association/Item/@UnitOfMeasure")%>"  <%}%>
<%=getTextOptions("xml:/AssociationList/Association_" + AssociationCounter + "/Item/@UnitOfMeasure", "xml:/Association/Item/@UnitOfMeasure")%>/>
</td>
<td class="tablecolumn" nowrap="true">
<yfc:getXMLValue binding="xml:/Association/@AssociatedQuantity"/>
<input type="hidden" <%if(bAppendOldValue) { %>OldValue="<%=resolveValue("xml:OrigAPIAssociation:/Association/@AssociatedQuantity")%>"  <%}%>
<%=getTextOptions("xml:/AssociationList/Association_" + AssociationCounter + "/@AssociatedQuantity", "xml:/Association/@AssociatedQuantity")%>/>
</td>
<td class="tablecolumn" nowrap="true">
<yfc:getXMLValue binding="xml:/Association/@EffectiveFrom"/>
<input type="hidden" <%if(bAppendOldValue) { %>OldValue="<%=resolveValue("xml:OrigAPIAssociation:/Association/@EffectiveFrom")%>"  <%}%>
<%=getTextOptions("xml:/AssociationList/Association_" + AssociationCounter + "/@EffectiveFrom", "xml:/Association/@EffectiveFrom")%>/>
</td>
<td class="tablecolumn" nowrap="true">
<yfc:getXMLValue binding="xml:/Association/@EffectiveTo"/>
<input type="hidden" <%if(bAppendOldValue) { %>OldValue="<%=resolveValue("xml:OrigAPIAssociation:/Association/@EffectiveTo")%>"  <%}%>
<%=getTextOptions("xml:/AssociationList/Association_" + AssociationCounter + "/@EffectiveTo", "xml:/Association/@EffectiveTo")%>/>
</td>
</tr>
</yfc:loopXML>
</tbody>
</table>
