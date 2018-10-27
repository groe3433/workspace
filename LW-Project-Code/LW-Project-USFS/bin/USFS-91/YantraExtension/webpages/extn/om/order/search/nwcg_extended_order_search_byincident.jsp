<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<table class="view">
<tr>
<td>
</td>
</tr>

<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
</jsp:include>

<tr>
<td class="searchlabel" >
<yfc:i18n>Issue_#</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true">
<select name="xml:/Order/@OrderNoQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/@OrderNoQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/@OrderNo")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Incident_Number</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true">
<select name="xml:/Order/Extn/@ExtnIncidentNoQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/Extn/@ExtnIncidentNoQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentNo")%>/>
<img class="lookupicon" onclick="callLookup(this,'NWCGIncidentLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Incident") %> />

</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Incident_Name</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true">
<select name="xml:/Order/Extn/@ExtnIncidentNameQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/Extn/@ExtnIncidentNameQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentName")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Incident_FsAcct_Code</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true">
<select name="xml:/Order/Extn/@ExtnFsAcctCodeQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/Extn/@ExtnFsAcctCodeQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/Extn/@ExtnFsAcctCode")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Incident_BlmAcct_Code</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true">
<select name="xml:/Order/Extn/@ExtnBlmAcctCodeQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/Extn/@ExtnBlmAcctCodeQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/Extn/@ExtnBlmAcctCode")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Incident_OtherAcct_Code</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true">
<select name="xml:/Order/Extn/@ExtnOtherAcctCodeQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/Extn/@ExtnOtherAcctCodeQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/Extn/@ExtnOtherAcctCode")%>/>
</td>
</tr>

</table>