<%@include file="/yfsjspcommon/yfsutil.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreasonpopup.js"></script>

<table width="100%" class="view">
<tr>
<td>
<yfc:i18n>Reason_Code</yfc:i18n>
</td>
<td>
<select name="xml:/ModificationReason/@ReasonCode" class="combobox">
<yfc:loopOptions binding="xml:ReasonCodeList:/CommonCodeList/@CommonCode"
name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
</select>
</td>
</tr>
<tr>
<td>
<yfc:i18n>Reason_Text</yfc:i18n>
</td>
<td>
<textarea class="unprotectedtextareainput" rows="3" cols="50" <%=getTextAreaOptions("xml:/ModificationReason/@ReasonText")%>></textarea>
</td>
</tr>
<tr>
<td></td>
<td align="right">
<input type="button" class="button" value='<%=getI18N("__OK__")%>' onclick="setOKClickedAttribute();return false;"/>
<input type="button" class="button" value='<%=getI18N("Cancel")%>' onclick="window.close();"/>
<td>
<tr>
</table>
