<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.ResourceUtil" %>
<%@ include file="/extn/yfsjspcommon/nwcg_extended_editable_util_lines.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/css/scripts/editabletbl.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_orderDetails.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_C2C_prepareOrderLine.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_cache_transfer.js"></script>
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
%>

<table class="sortableTable" ID="OrderLines" cellspacing="0" width="100%" yfcMaxSortingRecords="1000" >
<thead>
<tr>
	<td class="tablecolumnheader" nowrap="true" style="width:90px"><yfc:i18n>Item_ID</yfc:i18n></td>
	<!--ROLLBACK of CR 338 PC BEGIN   -->
	<td class="tablecolumnheader" style="width:80px"  sortable="no"><yfc:i18n>PC</yfc:i18n></td>
	<!--ROLLBACK of CR 338 PC END   -->
	<td class="tablecolumnheader" style="width:80px"  sortable="no"><yfc:i18n>UOM</yfc:i18n></td>
	<td class="tablecolumnheader" style="width:550px"  sortable="no"><yfc:i18n>Description</yfc:i18n></td>
	<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:40px"><yfc:i18n>Requested_Qty</yfc:i18n></td>

            <% if (equals(driverDate, "02")) { %>
                <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:120px"><yfc:i18n>Delivery_Date</yfc:i18n></td>
            <% } else { %>
                <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:120px"><yfc:i18n>Ship_Date</yfc:i18n></td>
            <% } %>
	<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:550px"><yfc:i18n>Comments</yfc:i18n></td>
</tr>
</thead>
 <tbody>
	<!-- This will create the blank panel -->
			<jsp:include page="/extn/common/nwcg_new_render_C2C_orderline_CacheToCacheRequest.jsp" flush="true"/>        
    </tbody>

 <tfoot>        
 </tfoot>

</table>
<!-- this will generate all the javascript function dynamically -->
<jsp:include page="/extn/common/nwcg_new_render_C2C_orderline_js_CacheToCacheRequest.jsp" flush="true"/>