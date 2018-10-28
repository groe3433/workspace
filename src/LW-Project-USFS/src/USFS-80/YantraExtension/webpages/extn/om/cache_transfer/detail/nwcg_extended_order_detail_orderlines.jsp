<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.ResourceUtil" %>
<%@ include file="/extn/yfsjspcommon/nwcg_extended_editable_util_lines.jspf" %>

<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/css/scripts/editabletbl.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_orderDetails.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_C2C_prepareOrderLine.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_cache_transfer.js"></script>
<script language="javascript">
document.body.attachEvent("onunload", processSaveRecordsForChildNode);
</script>

<%
	boolean bAppendOldValue = false;
	if(!isVoid(errors) || equals(sOperation,"Y") || equals(sOperation,"DELETE"))
	bAppendOldValue = true;

	String modifyView = request.getParameter("ModifyView");
    modifyView = modifyView == null ? "" : modifyView;

    String driverDate = getValue("Order", "xml:/Order/@DriverDate");
	String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("Order", "xml:/Order/@EnterpriseCode"));

	String sDraftOrderFlag = getValue("Order", "xml:/Order/@DraftOrderFlag");
 	System.out.println("sDraftOrderFlag " + sDraftOrderFlag);
%>

<table class="sortableTable" ID="OrderLines" cellspacing="0" width="100%" yfcMaxSortingRecords="1000" >
<thead>
<tr>
	<td class="checkboxheader" sortable="no">
		<input type="hidden" id="userOperation" name="userOperation" value="" />
		<input type="hidden" id="numRowsToAdd" name="numRowsToAdd" value="" />
		<% if(sDraftOrderFlag.equalsIgnoreCase("Y")) {
			}  
		   else { %>
           <input type="checkbox" value="checkbox" name="checkbox" onclick="doCheckAll(this);"/>
			<% } %>
	</td>

<!-- Begin CR817 added font-size:11px for all columns below -->
	<td class="tablecolumnheader" nowrap="true" style="width:5px; font-size:11px">Line</td>
<%--<td class="tablecolumnheader" nowrap="true" style="width:5px; font-size:11px">Issue_Line_#</td>--%>
<%--<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/OrderLine/Extn/@ExtnRequestNo")%>; font-size:11px"><yfc:i18n>Request_Number</yfc:i18n></td>--%>

	<td class="tablecolumnheader" nowrap="true" style="width:55px; font-size:11px"><yfc:i18n>Item_ID</yfc:i18n></td>
	<!--ROLLBACK of CR 338 PC BEGIN   -->
	<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/Item/@ProductClass")%>; font-size:11px"  sortable="no"><yfc:i18n>PC</yfc:i18n></td>
	<!--ROLLBACK of CR 338 PC END   -->
	<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/OrderLineTranQuantity/@TransactionalUOM")%>; font-size:11px"  sortable="no"><yfc:i18n>UOM</yfc:i18n></td>
	<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/Item/@ItemDesc")%>; font-size:11px"  sortable="no"><yfc:i18n>Description</yfc:i18n></td>
	<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:40px; font-size:11px"><yfc:i18n>Requested_Qty</yfc:i18n></td>
	<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:40px; font-size:11px"><yfc:i18n>RFI_Qty</yfc:i18n></td>
	<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:40px; font-size:11px"><yfc:i18n>Issue_Qty</yfc:i18n></td>

	<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:30px; font-size:11px"><yfc:i18n>Actual Pricing Qty</yfc:i18n></td>
	<td class="tablecolumnheader" nowrap="false" sortable="no" style="width:<%=getUITableSize("xml:/OrderLine/Extn/@ExtnUTFQty")%>; font-size:11px"><yfc:i18n>UTF_Qty</yfc:i18n></td>
	<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/OrderLine/Extn/@ExtnBackorderedQty")%>; font-size:11px"><yfc:i18n>Backordered_Qty</yfc:i18n></td>

            <% if (equals(driverDate, "02")) { %>
                <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:90px; font-size:11px"><yfc:i18n>Delivery_Date</yfc:i18n></td>
            <% } else { %>
                <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:90px; font-size:11px"><yfc:i18n>Ship_Date</yfc:i18n></td>
            <% } %>
	<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:100px; font-size:11px"><yfc:i18n>Comments</yfc:i18n></td>

	<!--<%=getUITableSize("xml:/OrderLine/Notes/Note/@NoteText")%> -->
	<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/LineOverallTotals/@LineTotal")%>; font-size:11px"  sortable="no"><yfc:i18n>Amount</yfc:i18n></td>
	<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/@Status")%>; font-size:11px"  sortable="no"><yfc:i18n>Status</yfc:i18n></td>
<!-- End CR817 -->

</tr>
</thead>
 <tbody>
	<!-- This will create the blank panel -->
			<jsp:include page="/extn/common/nwcg_new_render_C2C_orderline.jsp" flush="true"/>        
    </tbody>

 <tfoot>        
		
    </tfoot>
</table>
<!-- this will generate all the javascript function dynamically -->
<jsp:include page="/extn/common/nwcg_new_render_C2C_orderline_js.jsp" flush="true"/>