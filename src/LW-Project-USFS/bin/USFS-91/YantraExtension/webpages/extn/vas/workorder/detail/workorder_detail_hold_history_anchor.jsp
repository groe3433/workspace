<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<table cellSpacing=0 class="anchor" cellpadding="7px">
	<tr>
		<td colspan="4">
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I01"/>
				<jsp:param name="ForWorkOrder" value="Y"/>
			</jsp:include>
		</td>
	</tr>
	<tr>
		<td colspan="4">
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I02"/>
				<jsp:param name="ForWorkOrder" value="Y"/>
			</jsp:include>
		</td>
	</tr>
</table>
