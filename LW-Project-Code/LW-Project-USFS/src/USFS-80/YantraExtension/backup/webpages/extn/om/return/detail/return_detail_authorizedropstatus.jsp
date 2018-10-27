<%@include file="/yfsjspcommon/yfsutil.jspf" %>

<%
String sBindingNode=request.getParameter("BindingNode");
%>


<table class="view">
<tr>
<td class="detaillabel">
<yfc:i18n>Authorize_Drop_Status</yfc:i18n>
</td>
<td>
<select name='<%=buildBinding(sBindingNode,"/@ReasonCode", "")%>'  class="combobox" >
<yfc:loopOptions binding="xml:ReasonCode:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
</select>
</td>
</tr>
<tr>
<td class="detaillabel">
<yfc:i18n>Reason_Text</yfc:i18n>
</td>
<td rowspan="3">
<textarea class="unprotectedtextareainput" rows="3" cols="35" <%=getTextAreaOptions(buildBinding(sBindingNode, "/@ReasonText", ""), "")%>></textarea>
</td>
</tr>
</table>
