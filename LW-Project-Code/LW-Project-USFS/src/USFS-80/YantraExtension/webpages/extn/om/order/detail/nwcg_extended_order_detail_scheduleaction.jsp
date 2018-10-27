<%@ include file="/yfsjspcommon/yfsutil.jspf" %>

<script language="javascript">
var myObject = new Object();
myObject = dialogArguments;
var parentWindow = myObject.currentWindow;

function setClickedAttribute() {

var retVal = new Object();
retVal["xml:/ScheduleOrder/@AllocationRuleID"] = document.all["xml:/ScheduleOrder/@AllocationRuleID"].value;
if (document.all["xml:/ScheduleOrder/@IgnoreReleaseDate"].checked) {
retVal["xml:/ScheduleOrder/@IgnoreReleaseDate"] = "Y";
}
else {
retVal["xml:/ScheduleOrder/@IgnoreReleaseDate"] = "N";
}
if (document.all["xml:/ScheduleOrder/@ScheduleAndRelease"].checked) {
retVal["xml:/ScheduleOrder/@ScheduleAndRelease"] = "Y";
}
else {
retVal["xml:/ScheduleOrder/@ScheduleAndRelease"] = "N";
}

window.dialogArguments["OMReturnValue"] = retVal;
window.dialogArguments["OKClicked"] = "YES";
window.close();
}
</script>

<table width="100%" class="view">
<tr>
<td class="detaillabel">
<yfc:i18n>Scheduling_Rule</yfc:i18n>
</td>
<td>
<select name="xml:/ScheduleOrder/@AllocationRuleID" class="combobox">
<%
// the scheduling rule shuld not be defaulted to blank
com.yantra.yfc.dom.YFCElement elem = (com.yantra.yfc.dom.YFCElement) request.getAttribute("AllocationRuleList");
com.yantra.yfc.dom.YFCNodeList nl = elem.getElementsByTagName("AllocationRule");
for(int index = 0 ; index < nl.getLength() ; index++)
{
	com.yantra.yfc.dom.YFCElement elemAllocationRule = (com.yantra.yfc.dom.YFCElement) nl.item(index);
%>
<option value='<%=elemAllocationRule.getAttribute("AllocationRuleId")%>'> <%=elemAllocationRule.getAttribute("Description")%></option>
<%}%>
<option></option>
</select>
</td>
</tr>
</tr>
<tr>
<td class="detaillabel">
<yfc:i18n>Release_Immediately</yfc:i18n>
</td>
<td>
<input class="checkbox" type="checkbox" <%=getCheckBoxOptions("xml:/ScheduleOrder/@ScheduleAndRelease", "Y", "Y")%>/>
</td>
</tr>
<tr>
<td class="detaillabel">
<yfc:i18n>Override_Release_Date</yfc:i18n>
</td>
<td>
<input class="checkbox" type="checkbox" <%=getCheckBoxOptions("xml:/ScheduleOrder/@IgnoreReleaseDate", "Y", "Y")%>/>
</td>
<tr>
<td></td>
<td align="right">
<input type="button" class="button" value='<%=getI18N("__OK__")%>' onclick="setClickedAttribute();return true;"/>
<input type="button" class="button" value='<%=getI18N("Cancel")%>' onclick="window.close();"/>
<td>
<tr>
</table>
