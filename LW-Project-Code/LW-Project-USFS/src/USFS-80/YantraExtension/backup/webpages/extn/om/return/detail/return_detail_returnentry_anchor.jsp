<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<table cellSpacing=0 class="anchor" cellpadding="7px">
<tr>
<td height="100%" width="100%" >
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I01"/>
<jsp:param name="ModifyView" value="true"/>
</jsp:include>
</td>
</tr>
</table>
