<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<script language="javascript" src="/yantra/console/scripts/wmsim.js"></script>
<table class="anchor" cellpadding="7px"  cellSpacing="0">
	 <tr height="70%">
		<td>
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I01"/>


			</jsp:include>
		</td>
	</tr>
	<tr height="30%">
		<td>
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I04"/>
			</jsp:include>
		</td>
	</tr>
	 <tr>
		<td >
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I02"/>
			</jsp:include>
		</td>
	 </tr>
	 <tr>
		<td >
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I03"/>
			</jsp:include>
		</td>
	 </tr>
</table>