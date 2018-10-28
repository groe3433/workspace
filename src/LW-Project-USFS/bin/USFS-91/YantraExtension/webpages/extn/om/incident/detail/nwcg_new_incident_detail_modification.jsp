<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/extn.js"></script>
<script language="javascript">
	IgnoreChangeNames();
	yfcDoNotPromptForChanges(true);
</script>

<table class="view" width="100%">
<tr>
<td class="detaillabel"><yfc:i18n>Modification_Code</yfc:i18n></td>
<td>
<select class="combobox" <%=getComboOptions("xml:/NWCGIncidentOrder/@ModificationCode")%>>
<yfc:loopOptions binding="xml:ModificationCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
</select>
</td>
<td class="detaillabel"><yfc:i18n>Modification_Desc</yfc:i18n></td>
<td valign="top">
 <textarea class="unprotectedtextareainput" style="HEIGHT:100px;WIDTH:170px;" name="xml:/NWCGIncidentOrder/@ModificationDesc">
</textarea>
</td>
</tr>
</table>