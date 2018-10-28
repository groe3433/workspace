<%@include file="/yfsjspcommon/yfsutil.jspf" %>
<%@include file="/console/jsp/order.jspf" %>
<%@ page import="com.yantra.yfc.core.YFCObject" %>

<%
boolean showServiceWorkOrders = false;
boolean showVASWorkOrders = false;

YFCElement workOrderListElem = (YFCElement)request.getAttribute("WorkOrders");

// If coming from order line.
YFCElement orderLineElem = (YFCElement) request.getAttribute("OrderLine");
if (null != orderLineElem) {
workOrderListElem = orderLineElem.getChildElement("WorkOrders", true);
}

for (Iterator i = workOrderListElem.getChildren(); i.hasNext();) {
YFCElement workOrderElem = (YFCElement) i.next();
if (!(workOrderElem.getTagName().equals("WorkOrder"))) continue;

String servItemGrpCode = workOrderElem.getAttribute("ServiceItemGroupCode");

if (!YFCObject.isVoid(servItemGrpCode)) {
if (YFCObject.equals(servItemGrpCode,"PS") || YFCObject.equals(servItemGrpCode,"DS")) {
showServiceWorkOrders = true;
} else {
showVASWorkOrders = true;
}
}
}
%>

<table class="anchor" cellpadding="7px" cellSpacing="0">
<tr>
<td>
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I01"/>
</jsp:include>
</td>
</tr>

<% if (showServiceWorkOrders) { %>
<tr>
<td>
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I02"/>
</jsp:include>
</td>
</tr>
<% } %>

<% if (showVASWorkOrders) { %>
<tr>
<td>
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I03"/>
</jsp:include>
</td>
</tr>
<% } %>
</table>
