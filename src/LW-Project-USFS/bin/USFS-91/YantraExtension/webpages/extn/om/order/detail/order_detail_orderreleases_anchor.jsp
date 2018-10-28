<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<%
//Here, take the output of findReceipt API for open receipts to
//set the visible binding for start receipt &  receive options.
double isOpen = getNumericValue("xml:/Receipts/@TotalNumberOfRecords");
YFCElement elem = (YFCElement) request.getAttribute("Receipts");

if(elem != null){
elem.setAttribute("StartReceiptFlag", false);
elem.setAttribute("ReceiveFlag", false);
if ( isOpen > 0) {
elem.setAttribute("ReceiveFlag", true);

}
else {
elem.setAttribute("StartReceiptFlag", true);
}
}
%>

<table class="anchor" cellpadding="7px" cellSpacing="0">
<tr>
<td colspan="3" >
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I02"/>
</jsp:include>
</td>
</tr>
<tr>
<td colspan="3" >
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I01"/>
</jsp:include>
</td>
</tr>
</table>
