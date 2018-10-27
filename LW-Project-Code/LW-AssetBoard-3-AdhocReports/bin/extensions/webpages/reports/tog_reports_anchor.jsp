<%@ include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf"%>
<%@ include file="/console/jsp/order.jspf"%>

<table cellSpacing=0 class="anchor" cellpadding="7px">
	<tr>
		<td height="100%" width="25%" addressip="true">
			<jsp:include page="/yfc/innerpanel.jsp" flush="true">
				<jsp:param name="CurrentInnerPanelID" value="I01" />
				<jsp:param name="DataXML" value="TriggerAgent" />
			</jsp:include>
		</td>
	</tr>
</table>