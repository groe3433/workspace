<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<script language="javascript" src="/yantra/console/scripts/om.js">
document.body.attachEvent("onunload", processSaveRecordsForOrder);
</script>

<table cellSpacing=0 class="anchor" cellpadding="7px">
<tr>
<td>
<yfc:makeXMLInput name="orderHeaderKey">
<yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey"/>
</yfc:makeXMLInput>
<input type="hidden" value='<%=getParameter("orderHeaderKey")%>' name="OrderEntityKey"/>
<input type="hidden" <%=getTextOptions("xml:/Order/@OrderHeaderKey")%>/>
</td>
</tr>
<tr>
<td height="100%" width="100%" >
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I03"/>
</jsp:include>
</td>
</tr>
<tr>
<td height="100%" width="100%" >
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I01"/>
<jsp:param name="allowedBinding" value="xml:/Order/AllowedModifications"/>
<jsp:param name="getBinding" value="xml:/Order"/>
<jsp:param name="saveBinding" value="xml:/Order"/>
</jsp:include>
</td>
</tr>
<tr>
<td height="100%" width="100%" >
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I04"/>
</jsp:include>
</td>
</tr>
</table>
