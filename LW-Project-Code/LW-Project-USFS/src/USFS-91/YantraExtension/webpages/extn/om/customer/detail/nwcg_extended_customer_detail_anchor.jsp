<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<table cellSpacing=0 class="anchor" cellpadding="7px">
<tr>
<td>
<yfc:makeXMLInput name="customerKey">
<yfc:makeXMLKey binding="xml:/Customer/Consumer/BillingPersonInfo/@PersonInfoKey" value="xml:/Customer/Consumer/BillingPersonInfo/@PersonInfoKey" 
binding="xml:/Customer/@CustomerKey" value="xml:/Customer/@CustomerKey"/>
</yfc:makeXMLInput>
<input type="hidden" value='<%=getParameter("customerKey")%>' name="CustomerEntityKey"/>
<input type="hidden" <%=getTextOptions("xml:/Customer/Consumer/BillingPersonInfo/@PersonInfoKey")%>/>
</td>
</tr>

<tr>
<td colspan="4">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I01"/>
<jsp:param name="ModifyView" value="true"/>
</jsp:include>
</td>
</tr>
<tr>
<td colspan="4">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I02"/>
<jsp:param name="ModifyView" value="true"/>
</jsp:include>
</td>
</tr>

</table>
