<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>
<script>
yfcDoNotPromptForChanges(true);
</script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>


<table class="view" width="100%">

<input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
<input type="hidden" name="chkCopyCustomerEntityKey" value='<%=getParameter("customerKey")%>' />

<tr>
	<td class="detaillabel" ><yfc:i18n>Customer_ID</yfc:i18n></td>
	<td class="protectedtext"><%=resolveValue("xml:/Customer/@CustomerID")%></td>
	<td class="detaillabel" ><yfc:i18n>Organization_Type</yfc:i18n></td>
	<td class="protectedtext"><%=resolveValue("xml:/Customer/Extn/@ExtnOrganizationType")%></td>
	<td class="detaillabel" ><yfc:i18n>GACC</yfc:i18n></td>
	<td class="protectedtext"><%=resolveValue("xml:/Customer/Extn/@ExtnGACC")%></td>
</tr>

<tr>
	<td class="detaillabel" ><yfc:i18n>Jurisdiction_ID</yfc:i18n></td>
	<td class="protectedtext"><%=resolveValue("xml:/Customer/Extn/@ExtnJurisdictionID")%></td>
	<td class="detaillabel" ><yfc:i18n>Jurisdiction_Name</yfc:i18n></td>
	<td class="protectedtext"><%=resolveValue("xml:/Customer/Extn/@ExtnJurisdictionName")%></td>
	<td class="detaillabel" ><yfc:i18n>Jurisdiction_Type</yfc:i18n></td>
	<td class="protectedtext"><%=resolveValue("xml:/Customer/Extn/@ExtnJurisdictionType")%></td>
</tr>
</table>
