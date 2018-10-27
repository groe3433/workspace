<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<!--  CR 733 - BEGIN - ML -->
<script language="javascript">
function setIncidentStatus(status){
var elem = document.getElementById('xml:/NWCGIncidentOrder/@IsActive');
alert(elem.value);
elem.value = status;
}
</script>
<%
String incidentStatus = resolveValue("xml:/NWCGIncidentOrder/@IsActive");
if (isVoid(incidentStatus)) { // If values of radio buttons gets changed, this condition need to be revisited.
incidentStatus = "Y";
}

String otherOrder 		= resolveValue("xml:/NWCGIncidentOrder/@IsOtherOrder");
String DocumentType 	= resolveValue("xml:/Order/@DocumentType");
%>
<!--  CR 733 - END - ML -->
<!-- hidden input to indicate other orders -->
<input type="hidden" name="xml:/NWCGIncidentOrder/@IsOtherOrder" value="Y"/>

<table class="view">
<tr>
<td class="searchlabel" >
<yfc:i18n>Other_Order_No</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/NWCGIncidentOrder/@IncidentNoQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/NWCGIncidentOrder/@IncidentNoQryType"/>
</select>
<input type="text" class="unprotectedinput" size=30 maxLength=30 <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentNo")%>/>
</td>
</tr>
<!-- Other order name -->
<tr>
<td class="searchlabel" >
<yfc:i18n>Other_Order_Name</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/NWCGIncidentOrder/@IncidentNameQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/NWCGIncidentOrder/@IncidentNameQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentName")%>/>
</td>
</tr>

<!-- The customer id -->
<tr>
<td class="searchlabel" >
<yfc:i18n>Customer_Id</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/NWCGIncidentOrder/@CustomerIdQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/NWCGIncidentOrder/@CustomerIdQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@CustomerId")%>/>
</td>
</tr>
<!-- The other order type -->
<tr>
<td class="searchlabel" >
<yfc:i18n>Other_Order_Type</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/NWCGIncidentOrder/@OtherOrderTypeQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/NWCGIncidentOrder/@OtherOrderTypeQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@OtherOrderType")%>/>
</td>
</tr>
<!-- the cache id -->
<tr>
<td class="searchlabel" >
<yfc:i18n>Primary_Cache_ID</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/NWCGIncidentOrder/@PrimaryCacheIdQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/NWCGIncidentOrder/@PrimaryCacheIdQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@PrimaryCacheId")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Customer_PO_#</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/NWCGIncidentOrder/@CustomerPONoQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/NWCGIncidentOrder/@CustomerPONoQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@CustomerPONo")%>/>
</td>
</tr>
<!--  CR 733 BEGIN - ML -->
<tr>
<td class="searchcriteriacell">
<input type="radio" <%=getRadioOptions("xml:/NWCGIncidentOrder/@IsActive", incidentStatus,"Y")%>><yfc:i18n>Active</yfc:i18n>
<input type="radio" <%=getRadioOptions("xml:/NWCGIncidentOrder/@IsActive", incidentStatus,"N")%>><yfc:i18n>InActive</yfc:i18n>
<!--
<input type="hidden" name="xml:/NWCGIncidentOrder/@IsActive" value="<%=incidentStatus%>"/>
-->
<% if(!DocumentType.equals("")){  %>
	<% if(DocumentType.equals("0007.ex")){  %>
		<input type="hidden" name="xml:/NWCGIncidentOrder/@IsOtherOrder" value="Y"/>
	<% } else { %>
		<input type="hidden" name="xml:/NWCGIncidentOrder/@IsOtherOrder" value="N"/>
	<% } %>
<% } %>

</td>
</tr>
<!--  CR 733 END - ML -->

<!-- CR # 511 starts here 

<tr>
<td class="searchlabel" >
<yfc:i18n>Incident_Team_Type</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/NWCGIncidentOrder/@IncidentTeamTypeQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/NWCGIncidentOrder/@IncidentTeamTypeQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentTeamType")%>/>
</td>
</tr>

 CR # 511 ends here -->


</table>
