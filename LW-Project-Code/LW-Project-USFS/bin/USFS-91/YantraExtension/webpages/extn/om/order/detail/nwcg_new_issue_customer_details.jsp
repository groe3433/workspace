<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.nwcg.icbs.yantra.constant.common.NWCGConstants" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/extn.js"></script>


<table class="view" width="100%">

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
