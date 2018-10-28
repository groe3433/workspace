<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<table class="anchor" cellpadding="7px"  cellSpacing="0" >
<tr>
<td>
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I03"/>
</jsp:include>
</td>
</tr>
<tr>
<td>
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I06"/>
<jsp:param name="chargeType" value="Invoiced"/>
</jsp:include>
</td>
</tr>
</table>
