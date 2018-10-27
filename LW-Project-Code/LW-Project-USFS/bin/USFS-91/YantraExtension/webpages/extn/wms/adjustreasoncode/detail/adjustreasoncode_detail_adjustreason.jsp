<%@include file="/yfsjspcommon/yfsutil.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/adjustreasonpopup.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/wmsim.js"></script>
<%
   YFCElement countRequest = (YFCElement)request.getAttribute("CountRequest");
%> 
<% String requestType = resolveValue("xml:/CountRequest/@RequestType"); %>

<table width="100%" class="view">
<tr>
    <td class="detaillabel">
        <yfc:i18n>WMS_Reason_Code</yfc:i18n>
    </td>
    <td>
	  <% if(requestType != null && !requestType.equals("") && requestType.equals("PHYSICAL-COUNT")) {%>
        <input type="text" class="protectedinput" readonly="true" <%=getTextOptions("xml:/ModificationReason/@ReasonCode","PC")%> />
	  <%} else { %>
        <input type="text" class="unprotectedinput" <%= getTextOptions("xml:/ModificationReason/@ReasonCode")%> />
		<img class="lookupicon" name="search" onclick="callListLookup(this,'adjustreasoncode',
		'&xml:/AdjustmentReason/@Node=<%=getValue("CurrentUser","xml:CurrentUser:/User/@ShipNode")%>&xml:/AdjustmentReason/@EnterpriseCode=<%=getValue("CountRequest","xml:CountRequest:/CountRequest/@EnterpriseCode")%>')" 
		<%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Reason_Code") %> />
	  <%} %>
    </td>
</tr> 
<tr>    
    <td class="detaillabel">
        <yfc:i18n>Reason_Text</yfc:i18n>
    </td>
    <td >
        <textarea class="unprotectedtextareainput" rows="3" cols="50" <%=getTextAreaOptions("xml:/ModificationReason/@ReasonText")%>></textarea>
    </td>
</tr>
<tr></tr>
<tr>
    <td align="right">
		<input type="button" class="button" value='<%=getI18N("__OK__")%>' onclick="setOKClickedAttribute();return false;"/>
	</td>
	<td>
        <input type="button" class="button" value='<%=getI18N("Cancel")%>' onclick="window.close();"/>
   </td>
</tr>
</table>