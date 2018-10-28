<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

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
<!-- hidden input to indicate other orders -->
<table class="view">
<tr>
<td class="searchlabel" >
<yfc:i18n>Incident_No</yfc:i18n>
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
<tr>
<td class="searchlabel" >
<yfc:i18n>Year</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@Year")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Incident_Host</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/NWCGIncidentOrder/@IncidentHostQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/NWCGIncidentOrder/@IncidentHostQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentHost")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Incident_Name</yfc:i18n>
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
<yfc:callAPI apiID="AP2"/>
<tr>
<td class="searchlabel" >
<yfc:i18n>Incident_Type</yfc:i18n>
</tr>
<tr>
<td nowrap="true">
<select name="xml:/NWCGIncidentOrder/@IncidentType" class="combobox">
<yfc:loopOptions binding="xml:CommonIncidentCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
value="CodeValue" selected="xml:/NWCGIncidentOrder/@IncidentType" isLocalized="Y"/>
</select>
</td>
</tr>
</tr>
<td class="searchlabel" >
<yfc:i18n>IncidentFsAcctCode</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/NWCGIncidentOrder/@IncidentFsAcctCodeQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/NWCGIncidentOrder/@IncidentFsAcctCodeQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentFsAcctCode")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>IncidentBlmAcctCode</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/NWCGIncidentOrder/@IncidentBlmAcctCodeQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/NWCGIncidentOrder/@IncidentBlmAcctCodeQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentBlmAcctCode")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>IncidentOtherAcctCode</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/NWCGIncidentOrder/@IncidentOtherAcctCodeQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/NWCGIncidentOrder/@IncidentOtherAcctCodeQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentOtherAcctCode")%>/>
</td>
</tr>

<!-- CR # 511 starts here -->

<tr>
<td class="searchlabel" >
<yfc:i18n>Incident_Team_Type</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/NWCGIncidentOrder/@IncidentTeamType" class="combobox">
<option value=""> </option>
<option value="Prescribed Burn">Prescribed Burn</option>
  <option value="Type1">Type1</option>
  <option value="Type2">Type2</option>
  <option value="Type3">Type3</option>
</select>
</td>
</tr>

<!-- CR # 511 ends here -->

<tr>
<td class="searchlabel" >
<yfc:i18n>Incident_Status</yfc:i18n>
</td>
</tr>
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
<tr>
<!--
<td class="searchcriteriacell">
<input type="radio" <%=getRadioOptions("xml:/NWCGIncidentOrder/@IsOtherOrder", otherOrder,"Y")%>><yfc:i18n>Other_Order</yfc:i18n>
<input type="radio" <%=getRadioOptions("xml:/NWCGIncidentOrder/@IsOtherOrder", otherOrder,"N")%>><yfc:i18n>Incident_Order</yfc:i18n>
<input type="radio" <%=getRadioOptions("xml:/NWCGIncidentOrder/@IsOtherOrder", otherOrder,"")%>><yfc:i18n>All</yfc:i18n>
-->
</td>
</tr>
</table>
