<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@include file="/yfc/util.jspf" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.ResourceUtil" %>
<tr>
	<td colspan=2 align=left>
		<input type=button id="addButton" value='Add' name="Add" onClick="addMultipleRows();"/>
		<input class=unprotectedoverrideinput id="rowCount" type="text" style="width:40px" name="rowCount" value="1" onkeypress="handleEnterOnSubPopUp(this, event)" onblur="addMultipleRows()"/>
	</td>
	<td colspan=1 align=left>
		<input type=button id="clearButton" value='Clear' name="Clear" onClick="clearTableData();focusOnAddButton();"/>
	</td>
</tr>