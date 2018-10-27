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
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_prepareOrderLine.js"></script>
<script language="javascript">
    document.body.attachEvent("onunload", processSaveRecordsForChildNode);
</script>

<%
	session.setAttribute("ReservedItems",null);
	
	String strDocType = getValue("OrderLine", "xml:/OrderLine/Order/@DocumentType");
	String strDoc = resolveValue("xml:/OrderLine/Order/@DocumentType");

	boolean bOtherIssues = false ;
	if(strDocType != null && (!strDocType.equals("0001")))
	{
		bOtherIssues = true ;
	}
	boolean bAppendOldValue = false;
	if(!isVoid(errors) || equals(sOperation,"Y") || equals(sOperation,"DELETE")) 
		bAppendOldValue = true;
	String modifyView = request.getParameter("ModifyView");
    modifyView = modifyView == null ? "" : modifyView;

    String driverDate = getValue("OrderLine", "xml:/OrderLine/Order/@DriverDate");
	String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("OrderLine", "xml:/OrderLine/Order/@EnterpriseCode"));
	//extraParams = xml:/Item/@CallingOrganizationCode=NWCG


	String ohk = getValue("OrderLine", "xml:/OrderLine/@OrderHeaderKey");
	String origReqQty = getValue("OrderLine", "xml:/OrderLine/Extn/@ExtnOrigReqQty");
	String olRequestNo = getValue("OrderLine", "xml:/OrderLine/Extn/@ExtnRequestNo");
	String olMaxLineStatus = getValue("OrderLine","xml:/OrderLine/@MaxLineStatus");
%>
<table editable="false" width="100%">	
<tr>
	<td class="detaillabel" ><yfc:i18n>Request #</yfc:i18n></td>
	<td>
		<input class="protectedinput" type="text" <%=getTextOptions(olRequestNo)%> />
		<input type="hidden" id="newReqNo" <%=getTextOptions(olRequestNo)%> />
		<input class="unprotectedoverrideinput" type="hidden" id="maxLineStat" <%=getTextOptions("xml:/Order/@MaxLineStatus",olMaxLineStatus)%> />
	</td>
	<td class="detaillabel" ><yfc:i18n>Requested Quantity</yfc:i18n></td>
	<td>
	<input class="protectedinput" type="text" <%=getTextOptions(origReqQty)%> />
	</td>
</tr>
</table>

<table class="table" id="OrderLines" cellspacing="0" width="100%" yfcMaxSortingRecords="1000" >
    <thead>
        <tr>            
            <td class="tablecolumnheader" nowrap="true" style="width:50px"><yfc:i18n>S_#</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true" style="width:60px"><yfc:i18n>Item_ID</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:width:30px"><yfc:i18n>UOM</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:180px"><yfc:i18n>Description</yfc:i18n></td>            
            <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:40px"><yfc:i18n>RFI_Qty</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:30px"><yfc:i18n>Issue_Qty</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/Extn/@ExtnBackorderedQty")%>"><yfc:i18n>Backordered_Qty</yfc:i18n></td>
		<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/Extn/@ExtnUTFQty")%>"><yfc:i18n>UTF_Qty</yfc:i18n></td>

            <% if (equals(driverDate, "02")) { %>
                <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:80px"><yfc:i18n>Delivery_Date</yfc:i18n></td>
            <% } else { %>
                <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:80px"><yfc:i18n>Ship_Date</yfc:i18n></td>
            <% } %>
            <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:100px"><yfc:i18n>Comments</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
			<!-- This will create the blank panel -->
			<jsp:include page="/extn/common/nwcg_new_render_orderline_sub.jsp" flush="true"/>        
	</tbody>
    <tfoot>
    	<table width="100%" class="view">
    <tr>
        <td class="detaillabel">
            <yfc:i18n>System_Number</yfc:i18n> 
        </td>
        <td>
			<input id="ItemSubSysNoEntry" type="text" class="unprotectedinput" onkeypress="handleEnterOnSubPopUp(this, event)" <%=getTextOptions("xml:/Order/@ExtnSystemNo")%> tabindex="1"/>					            
		</td>
    </tr>
</table>
    </tfoot>
</table>
<!-- this will generate all the javascript function dynamically -->
<jsp:include page="/extn/common/nwcg_new_render_orderline_sub_js.jsp" flush="true"/>
