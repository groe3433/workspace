<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="Javascript"> 
	yfcDoNotPromptForChangesForActions(true); 
</script>

<table cellSpacing=0 class="anchor" cellpadding="7px">
	<tr>
		<td>
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I01"/>
			</jsp:include>
		</td>
	</tr>
</table>
