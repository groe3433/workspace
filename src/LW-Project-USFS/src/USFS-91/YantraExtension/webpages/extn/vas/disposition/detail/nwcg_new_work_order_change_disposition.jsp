<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/yfsjspcommon/editable_util_lines.jspf" %>
<%@ include file="/console/jsp/taskutils.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/css/scripts/editabletbl.js"></script>
<script language="Javascript"> yfcDoNotPromptForChanges(true); </script>

<script>



function displayData(obj){
	//var itemID = document.all("xml:/WorkOrderDisposition/@ItemID").value;
	//var dispositionCode = document.all("xml:/WorkOrderDisposition/@Disposition").value;
	//var qty = document.all("xml:/WorkOrderDisposition/@Quantity").value;
	yfcChangeDetailView(getCurrentViewId());
}
</script>

<%
boolean serialTracked=false;
double dSNoCount = 0.0;

serialTracked = equals("Y", resolveValue("xml:/WorkOrderDisposition/@IsTracked"));


if(serialTracked){
	String qty = resolveValue("xml:/WorkOrderDisposition/@Quantity");
	if (qty != null && qty.length() > 0){
		dSNoCount = Double.parseDouble(qty);
	}
}
%>

<Input type="hidden" name="xml:/WorkOrder/@QuantityRequested" value="<%=resolveValue("xml:/WorkOrder/@QuantityRequested")%>"/>

<table class="table" cellspacing="0" width="100%" yfcMaxSortingRecords="1000" >
	<thead>
		<tr>
			<td class="tablecolumnheader"> <yfc:i18n>Item_ID</yfc:i18n> </td>
			<%if (serialTracked) {%>
				<td class="tablecolumnheader"> <yfc:i18n>Trackable_ID</yfc:i18n> </td>
			<%}
			  else {%>
				<td class="tablecolumnheader"> <yfc:i18n>Quantity</yfc:i18n> </td>
			<%}%>
			<td class="tablecolumnheader"> <yfc:i18n>Location_ID</yfc:i18n> </td>
			<td class="tablecolumnheader"> <yfc:i18n>Status</yfc:i18n> </td>
		</tr>
	</thead>

<tbody>
<tr>
	<td class="tablecolumn">
		<Input class="protectedtext" name="xml:/WorkOrderDisposition/@ItemID" value="<%=resolveValue("xml:/WorkOrderDisposition/@ItemID")%>"/
	</td>

	<%if(serialTracked){%>
		<td>
			<b><%=getI18N("Serial_No")%></b>
		</td>
	<%}
	  else {
	%>
	  	<td nowrap="true">
        	<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/WorkOrderDisposition/@Quantity")%>/>
	  	</td>
	<%
	  }
	%>

	<td>
		<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/WorkOrderDisposition/@LocationId") %> />
        <img class="lookupicon" onclick="callLookup(this,'location','xml:/Location/@Node= +  <%= resolveValue("xml:/WorkOrderDisposition/@NodeKey")%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Location") %>
	</td>


	<td class="tablecolumn"  nowrap="true">
		<select class="combobox"  <%=getComboOptions("xml:/WorkOrderDisposition/@Disposition", "")%> >
			<yfc:loopOptions binding="xml:InventoryStatusList:/InventoryStatusList/@InventoryStatus" name="InventoryStatus" value="InventoryStatus" selected="xml:/WorkOrderDisposition/@Disposition" targetBinding="xml:/WorkOrderDisposition/@Disposition"/>
		</select>
	</td>

<%
	if (serialTracked)
	{
		while(dSNoCount>0){
		%>
			<tr  style="display:none"/>
		    <tr>
				<td/>


				<td nowrap="true">
					<input class="unprotectedinput" class='<%=getTextFieldClass("SERIAL_NO", false)%>' <%=getTextOptions("xml:/WorkOrderDisposition/SerialNumberDetails/SerialNumberDetail_" +String.valueOf(dSNoCount) +"/@SerialNo","xml:/WorkOrderDisposition/SerialNumberDetails/SerialNumberDetail_" +String.valueOf(dSNoCount) + "/@SerialNo")%>/>
				</td>
				</td>
			</tr>
			<%
			--dSNoCount;
		}%>
		</tr>
	<%}%>

</tbody>

	<input type="hidden" name="xml:/WorkOrderDisposition/@Quantity" value="<%=resolveValue("xml:/WorkOrderDisposition/@Quantity")%>" />
	<input type="hidden" name="xml:/WorkOrderDisposition/@IsTracked" value="<%=resolveValue("xml:/WorkOrderDisposition/@IsTracked")%>" />
	<input type="hidden" name="xml:/WorkOrderDisposition/@EnterpriseCode" value="<%=resolveValue("xml:/WorkOrderDisposition/@EnterpriseCode")%>" />
	<input type="hidden" name="xml:/WorkOrderDisposition/@NodeKey" value="<%=resolveValue("xml:/WorkOrderDisposition/@NodeKey")%>" />
	<input type="hidden" name="xml:/WorkOrderDisposition/@ProductClass" value="<%=resolveValue("xml:/WorkOrderDisposition/@ProductClass")%>" />
	<input type="hidden" name="xml:/WorkOrderDisposition/@Uom" value="<%=resolveValue("xml:/WorkOrderDisposition/@Uom")%>" />
	<input type="hidden" name="xml:/WorkOrderDisposition/@IncidentNo" value="<%=resolveValue("xml:/WorkOrderDisposition/@IncidentNo")%>" />
	<input type="hidden" name="xml:/WorkOrderDisposition/@WorkOrderKey" value="<%=resolveValue("xml:/WorkOrderDisposition/@WorkOrderKey")%>" />

<tfoot/>
</table>
