<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/order.jspf" %>

<table class="anchor">
<tr>
<td>
<jsp:include page="/yfc/innerpanel.jsp" flush="true">
<jsp:param name="CurrentInnerPanelID" value="I02"/>
</jsp:include>
</td>
</tr>
<%	if (isTrue("xml:/Order/@HasProductLines") )	{	%>
<tr>
<td>
<jsp:include page="/yfc/innerpanel.jsp" flush="true">
<jsp:param name="CurrentInnerPanelID" value="I01"/>
</jsp:include>
</td>
</tr>
<%	}
if (isTrue("xml:/Order/@HasServiceLines") )	{	%>
<tr>
<td>
<jsp:include page="/yfc/innerpanel.jsp" flush="true">
<jsp:param name="CurrentInnerPanelID" value="I03"/>
</jsp:include>
</td>
</tr>
<%  }
if (isTrue("xml:/Order/@HasDeliveryLines") )	{	%>
<tr>
<td>
<jsp:include page="/yfc/innerpanel.jsp" flush="true">
<jsp:param name="CurrentInnerPanelID" value="I04"/>
</jsp:include>
</td>
</tr>
<% } %>
</table>
