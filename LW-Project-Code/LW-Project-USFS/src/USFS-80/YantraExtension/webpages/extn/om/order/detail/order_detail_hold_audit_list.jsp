<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/order.jspf" %>

<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>

<table class="view" width="33%">
<tr>
<td class="detaillabel" nowrap="true" ><yfc:i18n>Order_Created_In_Yantra</yfc:i18n></td>
<td class="protectedtext">  <yfc:getXMLValue name="Order" binding="xml:/Order/@Createts"/></td>
</tr>
</table>
<table class="table" width="100%">
<thead>
<tr>
<td class="tablecolumnheader">
<yfc:i18n>Audit_#</yfc:i18n>
</td>
<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderAudit/@Createts")%>">
<yfc:i18n>Date</yfc:i18n>
</td>
<td class="tablecolumnheader">
<yfc:i18n>Modified_By</yfc:i18n>
</td>
<td class="tablecolumnheader" sortable="no">
<yfc:i18n>Reason</yfc:i18n>
</td>
<td class="tablecolumnheader" sortable="no">
<yfc:i18n>Modification_Levels</yfc:i18n>
</td>
<td class="tablecolumnheader" sortable="no">
<yfc:i18n>Modification_Types</yfc:i18n>
</td>
</tr>
</thead>
<tbody>
<yfc:loopXML name="OrderAuditList" binding="xml:/OrderAuditList/@OrderAudit" id="OrderAudit" >
<% String divToDisplay = "yfsOrderAudit_" + OrderAuditCounter; %>
<tr>
<td class="tablecolumn" sortValue="<%=OrderAuditCounter%>">
<%=OrderAuditCounter %>
<img id="<%=divToDisplay+"_img"%>" onclick="expandCollapseDetails('<%=divToDisplay%>','<%=getI18N("Click_To_Expand_Audit_Info")%>','<%=getI18N("Click_To_Collapse_Audit_Info")%>','<%=YFSUIBackendConsts.FOLDER_COLLAPSE%>','<%=YFSUIBackendConsts.FOLDER_EXPAND%>')" style="cursor:hand" <%=getImageOptions(YFSUIBackendConsts.FOLDER,"Click_To_Expand_Audit_Info")%> />
</td>
<td class="tablecolumn" sortValue="<%=getDateValue("xml:/OrderAudit/@Createts")%>">
<yfc:getXMLValue name="OrderAudit" binding="xml:/OrderAudit/@Createts"/>
</td>
<td class="tablecolumn">
<%
String sUser = resolveValue("xml:/OrderAudit/@CreateUserName");
if(isVoid(sUser) )
sUser = resolveValue("xml:/OrderAudit/@Createuserid");
%>
<%=sUser%>
</td>
<td class="tablecolumn">
<yfc:hasXMLNode binding="xml:/OrderAudit/@ReasonCode">
<%=getComboText("xml:ReasonCodeList:/CommonCodeList/@CommonCode", "CodeShortDescription", "CodeValue", "xml:/OrderAudit/@ReasonCode",true)%>
<br>
</yfc:hasXMLNode>
<yfc:getXMLValue name="OrderAudit" binding="xml:/OrderAudit/@ReasonText"/>
</td>
<td class="tablecolumn">
<% /* Loop through all modification levels of all the levels, but only display unique set */ %>
<% /* Slightly non-standard formatting in the loop to get the comma to look right */ %>
<% HashSet modLevelSet = new HashSet(); %>
<% String comma = ""; %>
<yfc:loopXML binding="xml:/OrderAudit/OrderAuditLevels/@OrderAuditLevel" id="OrderAuditLevel"><%
String modLevel = resolveValue("xml:/OrderAuditLevel/@ModificationLevelScreenName");
if( ! isVoid( modLevel ) && modLevelSet.add( modLevel ) ) {
%><%=comma%> <%=modLevel%><%
if( "".equals(comma) ) {
comma = getI18N(",");
}
}
%></yfc:loopXML>
</td>
<td class="tablecolumn">
<% /* Loop through all modification types of all the levels, but only display unique set */
/* Display a maximum of MAX items, followed by an "..." if there are more. */
HashSet modTypeSet = new HashSet();
int MAX = 3;
int count = 1;
%>
<yfc:loopXML binding="xml:/OrderAudit/OrderAuditLevels/@OrderAuditLevel" id="OrderAuditLevel">
<yfc:loopXML binding="xml:/OrderAuditLevel/ModificationTypes/@ModificationType" id="ModificationType" >
<%
String modType = resolveValue("xml:/ModificationType/@ScreenName");
if( modTypeSet.add( modType ) ) { /* Only display the type if it's not already there */
if( count <= MAX ) {
if( count > 1 ) {
%><br/><%             /* Only put in a line break once something has been written */
}
%><%=modType%><%          /* Output the type */
} else if( count == MAX+1 ) { /* Only display "..." if there really are more records */
%><yfc:i18n>...</yfc:i18n><%
}
count++;
}
%>
</yfc:loopXML>
</yfc:loopXML>
</td>
</tr>
<tr id="<%=divToDisplay%>" style="display:none" BypassRowColoring="true">
<td class="tablecolumn" colspan="6">
<%
YFCElement auditElem = (YFCElement)pageContext.getAttribute("OrderAudit");
request.setAttribute("OrderAudit", auditElem);
%>
<jsp:include page="/om/order/detail/order_detail_audit_list_expand.jsp" flush="true"/>
&nbsp;
</td>
</tr>
</yfc:loopXML>
</tbody>
</table>
