<%@include file="/yfc/util.jspf" %>
<script language="javascript" src="/yantra/css/scripts/editabletbl.js"></script>
<%
String tagControlledFlag = resolveValue("xml:ItemDetails:/Item/InventoryParameters/@TagControlFlag");
System.out.println("tagControlledFlag: in editable" + tagControlledFlag);

%>
<!-- cr 102 ks -->
<% if (tagControlledFlag == "N") { %>
<!-- end cr 102 ks -->

<table class="tablefooter" width="100%" cellspacing="0" cellpadding="0" >
<tr>
	<td align="left">
		<table class="tablefooter" cellspacing="0" style="border:0px">
		<tr>
			<td width="18px">
				<input type="text" name="numCopyAdd" class="tablefooterinput" value="1" size="1" maxlength="2" minValue="1" maxValue="99" minValueString="1" maxValueString="99" contentEditable="true"/>
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
<!-- cr 102 ks -->
<% } %>
<!-- end cr 102 ks -->
