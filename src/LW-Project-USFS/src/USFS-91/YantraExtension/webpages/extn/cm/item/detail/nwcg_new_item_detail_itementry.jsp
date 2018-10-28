<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.dom.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<script language="Javascript" >
IgnoreChangeNames();
yfcDoNotPromptForChanges(true);
</script>

<%
String itemKeyVal = resolveValue("xml:/Item/@ItemKey");
%>

<script language="javascript">
<% {
if (!isVoid(itemKeyVal)) {
YFCDocument itemDoc = YFCDocument.createDocument("Item");
itemDoc.getDocumentElement().setAttribute("ItemKey",resolveValue("xml:/Item/@ItemKey"));

%>
function showItemDetail() {
showDetailFor('<%=itemDoc.getDocumentElement().getString(false)%>');
}
window.attachEvent("onload", showItemDetail);
<%				}
}%>
</script>


<table class="view" width="100%">

<tr>
<td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:CurrentOrganization:/Organization/@PrimaryEnterpriseKey")%>/>
</td>
<td class="detaillabel" ><yfc:i18n>Item</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Item/@ItemID")%>/>
</td>
<td class="detaillabel" ><yfc:i18n>Short_Description</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Item/PrimaryInformation/@Description")%>/>
</td>
<% // Now call the APIs that are dependent on the common fields (Doc Type & Enterprise Code) %>
<yfc:callAPI apiID="AP1"/>
<td class="detaillabel" ><yfc:i18n>UOM</yfc:i18n></td>
<td>
<select class="combobox" <%=getComboOptions("xml:/Item/@UnitOfMeasure")%>>
<yfc:loopOptions binding="xml:/UomList/@Uom" name="Uom" value="Uom" selected="xml:/Item/@UnitOfMeasure" isLocalized="Y"/>
</select>
</td>
</tr>
</table>
