<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/order.jspf" %>

<script language="javascript">
function checkShipmentStatus()
{
	alert('Checking Shipment status...');	
	return true;	
}
</script>

<% setHistoryFlags((YFCElement) request.getAttribute("Order")); %>
<yfc:callAPI apiID="AP1"/> <!-- getShipmentListForOrder -->

<table class="anchor" cellpadding="7px"  cellSpacing="0" >
<tr>
<td >
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I01"/>
</jsp:include>
</td>
</tr>
</table>