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

<table class="table" cellspacing="0" width="100%" yfcMaxSortingRecords="1000" >
	<thead>
		<tr>
			<td class="tablecolumnheader"> <yfc:i18n>Item_ID</yfc:i18n> </td>
			<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/WorkOrderComponent/Item/PrimaryInformation/@Description")%>"><yfc:i18n>Description</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Product_Class</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Qty_Per_Kit</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Total_Quantity</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Refurb_Quantity</yfc:i18n></td>
		</tr>
	</thead>

	<tbody>
		<yfc:loopXML binding="xml:/WorkOrder/WorkOrderComponents/@WorkOrderComponent" id="WorkOrderComponent">
			<tr>
				<td class="tablecolumn">
					<input type="text" class="protectedtext" <%=getTextOptions("xml:/RefurbWorkOrder/WorkOrderComponents/WorkOrderComponent_" + WorkOrderComponentCounter + "/@ItemID", "xml:/WorkOrderComponent/@ItemID")%>/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/WorkOrderComponent/Item/PrimaryInformation/@Description"/>
				</td>
				<td class="tablecolumn">
					<input type="text" class="protectedtext" <%=getTextOptions("xml:/RefurbWorkOrder/WorkOrderComponents/WorkOrderComponent_" + WorkOrderComponentCounter + "/@ProductClass", "xml:/WorkOrderComponent/@ProductClass")%>/>
				<td class="tablecolumn">
					<input type="text" class="protectedtext" <%=getTextOptions("xml:/RefurbWorkOrder/WorkOrderComponents/WorkOrderComponent_" + WorkOrderComponentCounter + "/@Uom", "xml:/WorkOrderComponent/@Uom")%>/>
				</td>
				<td class="tablecolumn">
					<input type="text" class="protectedtext" <%=getTextOptions("xml:/RefurbWorkOrder/WorkOrderComponents/WorkOrderComponent_" + WorkOrderComponentCounter + "/@ComponentQuantity", "xml:/WorkOrderComponent/@ComponentQuantity")%>/>
				</td>
				<%
				  String qtyPerKit = resolveValue("xml:/WorkOrderComponent/@ComponentQuantity");
				  String reqQty = resolveValue("xml:/WorkOrder/@QuantityRequested");
				  float fQtyPerKit = 0;
				  if (qtyPerKit != null && qtyPerKit.length() > 0){
					fQtyPerKit = (new Float(qtyPerKit)).floatValue();
				  }

				  float fReqQty = 0;
				  if (reqQty != null && reqQty.length() > 0){
					fReqQty = (new Float(reqQty)).floatValue();
				  }
				  float total = fQtyPerKit * fReqQty;
				%>
				<td class="tablecolumn"><%=total%></td>
				<td class="tablecolumn"><input type="text" class="unprotectedinput" <%=getTextOptions("xml:/RefurbWorkOrder/WorkOrderComponents/WorkOrderComponent_" + WorkOrderComponentCounter + "/@RefurbQuantity", "xml:/WorkOrder/@QuantityRequested")%>/></td>
			</tr>
		</yfc:loopXML>

	</tbody>
	<yfc:callAPI apiID="AP2"/>

	<input type="hidden" name="xml:/RefurbWorkOrder/@NodeKey" value="<%=resolveValue("xml:/WorkOrder/@NodeKey")%>" />
	<input type="hidden" name="xml:/RefurbWorkOrder/@EnterpriseCode" value="<%=resolveValue("xml:/WorkOrder/@EnterpriseCode")%>" />
	<input type="hidden" name="xml:/RefurbWorkOrder/@QuantityRequested" value="<%=resolveValue("xml:/WorkOrder/@QuantityRequested")%>" />
	<input type="hidden" name="xml:/RefurbWorkOrder/Extn/@ExtnIncidentNumber" value="<%=resolveValue("xml:/WorkOrder/Extn/@ExtnIncidentNo")%>" />
	<input type="hidden" name="xml:/RefurbWorkOrder/Extn/@ExtnFsAcctCode" value="<%=resolveValue("xml:/WorkOrder/Extn/@ExtnFsAcctCode")%>" />
	<input type="hidden" name="xml:/RefurbWorkOrder/Extn/@ExtnBlmAcctCode" value="<%=resolveValue("xml:/WorkOrder/Extn/@ExtnBlmAcctCode")%>" />
	<input type="hidden" name="xml:/RefurbWorkOrder/Extn/@ExtnOtherAcctCode" value="<%=resolveValue("xml:/WorkOrder/Extn/@ExtnOtherAcctCode")%>" />
	<input type="hidden" name="xml:/RefurbWorkOrder/@WorkOrderNo" value="<%=resolveValue("xml:/WorkOrder/@WorkOrderNo")%>" />
	<input type="hidden" name="xml:/RefurbWorkOrder/@BillToID" value="<%=resolveValue("xml:/NWCGIncidentOrder/@CustomerId")%>" />


	<tfoot/>
</table>
