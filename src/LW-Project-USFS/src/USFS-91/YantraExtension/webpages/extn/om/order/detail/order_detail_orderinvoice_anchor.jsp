<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/order.jspf" %>

<% setHistoryFlags((YFCElement) request.getAttribute("Order")); %>
<yfc:callAPI apiID="AP1"/> <!-- getOrderInvoiceList [Populates DerivedOrderList] -->

<table class="anchor">
<tr>
<td>
<jsp:include page="/yfc/innerpanel.jsp" flush="true">
<jsp:param name="CurrentInnerPanelID" value="I02"/>
</jsp:include>
</td>
</tr>
<yfc:hasXMLNode binding="xml:OrderInvoiceList:/OrderInvoiceList/OrderInvoice">
<tr>
<td>
<jsp:include page="/yfc/innerpanel.jsp" flush="true">
<jsp:param name="CurrentInnerPanelID" value="I01"/>
</jsp:include>
</td>
</tr>
</yfc:hasXMLNode>
<yfc:hasXMLNode binding="xml:DerivedOrderInvoiceList:/OrderInvoiceList/OrderInvoice">
<tr>
<td >
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I03"/>
</jsp:include>
</td>
</tr>
</yfc:hasXMLNode>
</table>
