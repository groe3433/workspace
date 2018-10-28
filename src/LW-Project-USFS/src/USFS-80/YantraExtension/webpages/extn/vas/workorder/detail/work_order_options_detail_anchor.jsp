<%@ include file="/yfsjspcommon/yfsutil.jspf" %>

<table cellSpacing=0 class="anchor" cellpadding="7px">
	<tr>
		<td>
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I01"/>
				<jsp:param name="ModifyView" value="true"/>
			</jsp:include>
		</td>
	</tr>
</table>