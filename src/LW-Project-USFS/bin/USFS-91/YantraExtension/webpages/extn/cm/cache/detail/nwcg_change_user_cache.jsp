<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*"%>
<%@ page import="com.yantra.yfc.dom.*"%>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/extn.js"></script>

<script language="Javascript">
	IgnoreChangeNames();
	yfcDoNotPromptForChanges(true);
	yfcDoNotPromptForChangesForActions(true);
</script>

<%
	YFCElement IsNodeInput = YFCDocument.parse("<Organization	IsNode=\"Y\" />").getDocumentElement();
	YFCElement IsNodeTemplate = YFCDocument.parse("<OrganizationList>  <Organization OrganizationKey=\"\"  /> </OrganizationList>").getDocumentElement();
%>

<yfc:callAPI apiName="getOrganizationList" inputElement="<%=IsNodeInput%>" templateElement="<%=IsNodeTemplate%>" outputNamespace="OrganizationList" />

<table class="view" width="100%" align="center">
	<tr>
		<td class="detaillabel"><yfc:i18n>Current User</yfc:i18n></td>
		<td class="tablecolumn"><INPUT class=protectedinput maxLength=40 name=xml:/User/@Loginid Value="<%=resolveValue("xml:CurrentUser:/User/@Loginid")%>">
		</td>

		<td class="detaillabel"><yfc:i18n>Change User To Cache</yfc:i18n></td>
		<td class="tablecolumn" nowrap="true">
		<select	name="xml:/User/@OrganizationKey" class="combobox">
				<yfc:loopOptions binding="xml:OrganizationList:/OrganizationList/@Organization"	name="OrganizationKey" value="OrganizationKey" selected="xml:/User/@OrganizationKey" />
		</select>
		</td>
	</tr>
	<tr>
		<td class="detaillabel"><yfc:i18n>Current Cache</yfc:i18n></td>
		<%
			String strCurrentNode = resolveValue("xml:CurrentUser:/User/@Node");
			if (strCurrentNode != null && (!strCurrentNode.equals(""))) {
		%>
		<td class="tablecolumn"><INPUT class=protectedinput maxLength=40 name=xml:/User/@Node Value="<%=resolveValue("xml:CurrentUser:/User/@Node")%>"></td>
		<%
			} else {
		%>
		<td class="tablecolumn"><INPUT class=protectedinput maxLength=40 name=xml:/User/@Node Value="NWCG"></td>
		<%
			}
		%>
	</tr>
</table>