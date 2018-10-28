<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<% setHistoryFlags( (YFCElement)request.getAttribute("Order") ); %>

<table cellSpacing=0 class="anchor" cellpadding="7px">
<tr>
<td height="100%" width="100%">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I02"/>
</jsp:include>
</td>
</tr>
<tr>
<td height="100%" width="100%">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="ShowAuthorized" value="N"/>
<jsp:param name="CurrentInnerPanelID" value="I01"/>
</jsp:include>
</td>
</tr>
</table>
