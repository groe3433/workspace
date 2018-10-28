<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<%
String componentKeyVal = resolveValue("xml:/Item/Components/Component/@ComponentItemKey");
%>

<yfc:callAPI apiID="AP1"/>

<%
String associationKeyVal = resolveValue("xml:/AssociationList/Association/@AssociatedKey");
%>
<%
String hazmatVal = resolveValue("xml:/Item/PrimaryInformation/@IsHazmat");
%>


<table cellSpacing=0 class="anchor" cellpadding="7px">
<tr>
<td>
<yfc:makeXMLInput name="itemKey">
<yfc:makeXMLKey binding="xml:/Item/@ItemKey" value="xml:/Item/@ItemKey"/>
</yfc:makeXMLInput>
<input type="hidden" value='<%=getParameter("itemKey")%>' name="ItemEntityKey"/>
<input type="hidden" <%=getTextOptions("xml:/Item/@ItemEntityKey")%>/>
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
<tr>
<td colspan="4">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I03"/>
<jsp:param name="ModifyView" value="true"/>
</jsp:include>
</td>
</tr>
<tr>
<td colspan="4">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I04"/>
<jsp:param name="ModifyView" value="true"/>
</jsp:include>
</td>
</tr>
<tr>
<td colspan="4">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I05"/>
<jsp:param name="ModifyView" value="true"/>
</jsp:include>
</td>
</tr>
<% if (!isVoid(componentKeyVal)) { //Display only if it is a KIT %>
<tr>
<td colspan="4">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I06"/>
<jsp:param name="ModifyView" value="true"/>
</jsp:include>
</td>
</tr>
<% } %>
<% if (!isVoid(associationKeyVal)) { //Display only if it has substitutes %>
<tr>
<td colspan="4">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I07"/>
<jsp:param name="ModifyView" value="true"/>
</jsp:include>
</td>
</tr>
<% } %>
<% if (!isVoid(hazmatVal) && hazmatVal.equals("Y")) { //Display only if it has substitutes %>
<tr>
<td colspan="4">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I09"/>
<jsp:param name="ModifyView" value="true"/>
</jsp:include>
</td>
</tr>
<% } %>
<tr>
<td colspan="4">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I10"/>
<jsp:param name="ModifyView" value="true"/>
</jsp:include>
</td>
</tr>
</table>
