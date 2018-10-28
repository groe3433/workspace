<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/yfsjspcommon/editable_util_lines.jspf" %>
<%@ include file="/console/jsp/taskutils.jspf" %>
<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/css/scripts/editabletbl.js"></script>
<script language="Javascript">
	yfcDoNotPromptForChanges(true);
</script>

<%
	String strQty = resolveValue("xml:/WOAdjustInv/@Quantity");
	int qty = 0;
	if (strQty != null && !strQty.equalsIgnoreCase("")){
		qty = new Double(strQty).intValue();
	}
%>


<table class="table" cellspacing="0" width="100%" yfcMaxSortingRecords="1000" >
	<thead>
		<tr>
			<td class="tablecolumnheader"> <yfc:i18n>Item_ID</yfc:i18n> </td>
			<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/WorkOrderComponent/Item/PrimaryInformation/@Description")%>"><yfc:i18n>Description</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Product_Class</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Serial_Number</yfc:i18n></td>
		</tr>
	</thead>

	<tr>
		<td class="tablecolumn">
			<Input class="protectedtext" name="xml:/AdjustLocationInventory/Source/Inventory/InventoryItem/@ItemID" value="<%=resolveValue("xml:/WOAdjustInv/@ItemID")%>"/
		</td>

		<td class="tablecolumn">
			<Input class="protectedtext" value="<%=resolveValue("xml:/WOAdjustInv/@Description")%>"/
		</td>

		<td class="tablecolumn">
			<Input class="protectedtext" name="xml:/AdjustLocationInventory/Source/Inventory/InventoryItem/@ProductClass" value="<%=resolveValue("xml:/WOAdjustInv/@ProductClass")%>"/
		</td>
		<td class="tablecolumn">
			<Input class="protectedtext" name="xml:/AdjustLocationInventory/Source/Inventory/InventoryItem/@UnitOfMeasure" value="<%=resolveValue("xml:/WOAdjustInv/@Uom")%>"/
		</td>
		<td>
			<b><%=getI18N("Serial_No")%></b>
		</td>
	</tr>

	<%
		for (int i=0; i < qty; i++){
	%>
			<tr  style="display:none"/>
		    <tr>
				<td/> <td/> <td/> <td/>
				<td nowrap="true">
					<input class="unprotectedinput" class='<%=getTextFieldClass("SERIAL_NO", false)%>' <%=getTextOptions("xml:/AdjustLocationInventory/Source/Inventory/SerialList/SerialDetail_" + String.valueOf(i) +"/@SerialNo","xml:/AdjustLocationInventory/Source/Inventory/SerialList/SerialDetail_" +String.valueOf(i) + "/@SerialNo")%>/>
					<input type="hidden" 	<%=getTextOptions("xml:/AdjustLocationInventory/Source/Inventory/SerialList/SerialDetail_" + String.valueOf(i)+"/@Quantity","-1")%>/>
				</td>
			</tr>
	<%
		}

	%>

</tbody>

	<input type="hidden" name="xml:/AdjustLocationInventory/@EnterpriseCode" value="<%=resolveValue("xml:/WOAdjustInv/@EnterpriseCode")%>" />
	<input type="hidden" name="xml:/AdjustLocationInventory/@Node" value="<%=resolveValue("xml:/WOAdjustInv/@NodeKey")%>" />
	<input type="hidden" name="xml:/AdjustLocationInventory/Audit/@ReasonCode" value="<%=resolveValue("REFURB-ADJ")%>" />
	<input type="hidden" name="xml:/AdjustLocationInventory/Source/@LocationId" value="<%=resolveValue("xml:/WOAdjustInv/@ActivityLocationId")%>" />
	<input type="hidden" name="xml:/AdjustLocationInventory/Source/@LocationId" value="<%=resolveValue("xml:/WOAdjustInv/@ActivityLocationId")%>" />
	<input type="hidden" name="xml:/AdjustLocationInventory/Source/Inventory/InventoryItem/@ItemID" value="<%=resolveValue("xml:/WOAdjustInv/@ItemID")%>" />
	<input type="hidden" name="xml:/AdjustLocationInventory/Source/Inventory/InventoryItem/@UnitOfMeasure" value="<%=resolveValue("xml:/WOAdjustInv/@Uom")%>"/>
	<input type="hidden" name="xml:/AdjustLocationInventory/Source/Inventory/@InventoryStatus" value="NRFI-RFB" />

	<tfoot/>
</table>
