<%@include file="/yfsjspcommon/yfsutil.jspf" %>
<%@include file="/console/jsp/order.jspf" %>

<% setHistoryFlags( (YFCElement)request.getAttribute("OrderLine") ); %>

<table class="anchor" cellpadding="7px" cellSpacing="0">
<tr>
<td>
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I01"/>
</jsp:include>
</td>
</tr>
<tr>
<td>
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I02"/>
<jsp:param name="allowedBinding" value="xml:/OrderLine/AllowedModifications"/>
<jsp:param name="getBinding" value="xml:/OrderLine"/>
<jsp:param name="saveBinding" value="xml:/Order/OrderLines/OrderLine"/>
</jsp:include>
</td>
</tr>
</table>
