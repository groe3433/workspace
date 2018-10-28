<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/order.jspf" %>

<%
prepareRelatedToOrderElement((YFCElement) request.getAttribute("ChainedToOrderLineList"));
%>

<table class="anchor">
<tr>
<td>
<jsp:include page="/yfc/innerpanel.jsp" flush="true">
<jsp:param name="CurrentInnerPanelID" value="I01"/>
</jsp:include>
</td>
</tr>
<% if (isRelatedFrom((YFCElement) request.getAttribute("Order"),"ChainedFromOrderHeaderKey")) { %>
<tr>
<td>
<jsp:include page="/yfc/innerpanel.jsp" flush="true">
<jsp:param name="RelatedFromNamespace" value="ChainedFromOrderDetails"/>
<jsp:param name="RelatedFromBinding" value="xml:/OrderLine/@ChainedFromOrderHeaderKey"/>
<jsp:param name="CurrentInnerPanelID" value="I02"/>
</jsp:include>
</td>
</tr>
<%  }   %>
<yfc:hasXMLNode binding="xml:ChainedToOrderLineList:/OrderLineList/OrderLine">
<tr>
<td >
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="OrderListNamespace" value="ChainedToOrderLineList"/>
<jsp:param name="CurrentInnerPanelID" value="I03"/>
</jsp:include>
</td>
</tr>
</yfc:hasXMLNode>
</table>
