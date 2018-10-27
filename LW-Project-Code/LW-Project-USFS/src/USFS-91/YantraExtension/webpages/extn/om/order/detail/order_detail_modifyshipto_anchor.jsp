<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<table class="anchor" cellpadding="7px"  cellSpacing="0" >
<tr>
<td colspan="2" valign="top">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I01"/>
</jsp:include>
</td>
</tr>
<tr>
<td width="50%" height="100%" valign="top">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I02"/>
<jsp:param name="Path" value="xml:/Order/PersonInfoShipTo"/>
<jsp:param name="DataXML" value="Order"/>
<jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("ShipToAddress", "xml:/Order/AllowedModifications")%>'/>
</jsp:include>
</td>
<td width="50%" height="100%" valign="top">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I03"/>
</jsp:include>
</td>
</tr>
<tr>
<td colspan="2" valign="top">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I04"/>
</jsp:include>
</td>
</tr>
</table>
