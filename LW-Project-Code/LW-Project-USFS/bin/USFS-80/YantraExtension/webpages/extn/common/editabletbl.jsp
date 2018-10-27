<%@include file="/yfc/util.jspf" %>
<script language="javascript" src="/yantra/css/scripts/editabletbl.js"></script>
<table class="tablefooter" width="100%" cellspacing="0" cellpadding="0" >
<tr>
	<td align="left">
		<table class="tablefooter" cellspacing="0" style="border:0px">
		<tr>
			<td width="18px">
				<input type="hidden" name="numCopyAdd" class="tablefooterinput" value="<%=getParameter("MaxValue")%> " size="1" maxlength="2" minValue="1" maxValue="99" minValueString="1" maxValueString="99" contentEditable="true"/>
			</td>
			<td>
				<input type=hidden name="ADD_IMAGE"  value="<%=getParameter("MaxValue")%>"/>
			</td>
		</tr>
		</table>
	</td>
	<td align="right"></td>
</tr>
</table>