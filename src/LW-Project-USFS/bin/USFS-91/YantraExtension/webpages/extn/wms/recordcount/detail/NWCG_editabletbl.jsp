<%@include file="/yfc/util.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/css/scripts/editabletbl.js"></script>
<table class="tablefooter" width="100%" cellspacing="0" cellpadding="0" >
<tr>
	<td align="left">
		<table class="tablefooter" cellspacing="0" style="border:0px">
		<tr>
			<td width="18px">
				<input type="text" name="numCopyAdd" onfocus="this.value=1;this.select();" class="tablefooterinput" value="1" size="1" maxlength="4" minValue="1" maxValue="9999" minValueString="1" maxValueString="9999" contentEditable="true"/>
			</td>
			<td>
                <% if(!equals(getParameter("ReloadOnAddLine"),"Y")) {%>
                    <IMG class=icon src='../console/icons/add.gif' alt='<%=getI18N("Add_/_Copy_Row")%>' onclick="addRows(this)" style='width:12px;height:12px'/>
                <% } else { %>
                    <IMG class=icon src='../console/icons/add.gif' alt='<%=getI18N("Add_/_Copy_Row")%>' onclick="setAddRowFlagAndRefresh(this);" style='width:12px;height:12px'/>
                <% } %>
			</td>
		</tr>
		</table>
	</td>
	<td align="right"></td>
</tr>
</table>
