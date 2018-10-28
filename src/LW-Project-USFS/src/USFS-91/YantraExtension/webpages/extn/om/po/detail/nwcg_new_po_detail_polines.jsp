<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.ResourceUtil" %>
<%@ include file="/yfsjspcommon/editable_util_lines.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/css/scripts/editabletbl.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_orderDetails.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_reportRecordReceipt.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_preparePOLine.js"></script>
<script language="javascript">
    document.body.attachEvent("onunload", processSaveRecordsForChildNode);
</script>

	<%
	String strDocType = resolveValue("xml:/Order/@DocumentType");
	boolean bOtherIssues = false ;
	if(strDocType != null && (!strDocType.equals("0001")))
	//if(strDocType != null && strDocType.equals(NWCGConstants.OTHER_ISSUES_DOCUMENT_TYPE))
	{
		bOtherIssues = true ;
	}

	boolean bAppendOldValue = false;
	if(!isVoid(errors) || equals(sOperation,"Y") || equals(sOperation,"DELETE")) 
		bAppendOldValue = true;
	String modifyView = request.getParameter("ModifyView");
    modifyView = modifyView == null ? "" : modifyView;

    String driverDate = getValue("Order", "xml:/Order/@DriverDate");
	String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("Order", "xml:/Order/@EnterpriseCode"));
	
	/////// Added by gaurav
	YFCElement getOrderLineListIP = null;	
	String ohk = resolveValue("xml:/Order/@OrderHeaderKey");
	getOrderLineListIP = YFCDocument.parse("<OrderLine OrderHeaderKey=\"" + ohk + "\" />").getDocumentElement();
	YFCElement getOrderLineListTemplate = YFCDocument.parse("<OrderLineList TotalLineList=\"\"> <OrderLine OrderLineKey=\"\"><Extn ExtnSystemNo=\"\"/></OrderLine></OrderLineList>").getDocumentElement();
	int totalLinesOnOrder = 0;
	%>
	<yfc:callAPI apiName="getOrderLineList" inputElement="<%=getOrderLineListIP%>" templateElement="<%=getOrderLineListTemplate%>" outputNamespace="orderLineList"/>
	<yfc:loopXML binding="xml:orderLineList:/OrderLineList/@OrderLine" id="OrderLine" > 
	<%	
	totalLinesOnOrder++;
	%>
	</yfc:loopXML> 
	<input type="hidden" name="totalLinesOnOrder" value="<%=totalLinesOnOrder%>"/>

<table class="table" ID="OrderLines" cellspacing="0" width="100%" yfcMaxSortingRecords="1000" >
    <thead>
        <tr>
            <td class="checkboxheader" sortable="no" style="width:5px">
                <input type="hidden" id="userOperation" name="userOperation" value="" />
                <input type="hidden" id="numRowsToAdd" name="numRowsToAdd" value="" />
                <input type="checkbox" value="checkbox" name="checkbox" onclick="doCheckAll(this);"/>
            </td>
            <td class="tablecolumnheader" nowrap="true" style="width:20px"><yfc:i18n>Receive_Line</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="true" style="width:20px"><yfc:i18n>Line</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true" style="width:60px"><yfc:i18n>Item_ID</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true" style="width:80px"><yfc:i18n>NSN</yfc:i18n></td>
			<!-- CR 440 KS -->
            <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:40px"><yfc:i18n>Supplier_U/I</yfc:i18n></td>
            <td class="tablecolumnheader" sortable="no" style="width:100px"><yfc:i18n>Supplier_Std_Pack</yfc:i18n></td>
			<!-- end CR 440 KS -->
			<!-- CR 626 GA -->
            <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:40px"><yfc:i18n>GSA_Order_Qty</yfc:i18n></td>
			<!-- end CR 626 GA -->
			<td class="tablecolumnheader" style="width:140px"><yfc:i18n>Description</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="true" style="width:40px"><yfc:i18n>GSA_Serial_No</yfc:i18n></td>
			<!--ROLLBACK of CR 338 PC BEGIN   -->
            <td class="tablecolumnheader" style="width:width:30px"><yfc:i18n>PC</yfc:i18n></td> 
			<!--ROLLBACK of CR 338 PC END   -->
            <td class="tablecolumnheader" style="width:width:30px"><yfc:i18n>Cache_UOM</yfc:i18n></td>
            
			<td class="tablecolumnheader" style="width:30px"><yfc:i18n>Avail_RFI_Qty</yfc:i18n></td>
            <!-- <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:40px"><yfc:i18n>Requested_Qty</yfc:i18n></td> -->
            
            <!-- Top of CR 507 JK -->
			<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:40px"><yfc:i18n>Cache_Order_Quantity</yfc:i18n></td>
            <!-- <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:30px"><yfc:i18n>Line_Qty</yfc:i18n></td> -->
            <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:30px"><yfc:i18n>Received_Quantity</yfc:i18n></td>
            <!-- Bottom of CR 507 JK -->
			<td class="tablecolumnheader" style="width:100px"><yfc:i18n>Cache_Std_Pack</yfc:i18n></td>
			<% if (equals(driverDate, "02")) { %>
                <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:80px"><yfc:i18n>Delivery_Date</yfc:i18n></td>
            <% } else { %>
                <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:80px"><yfc:i18n>Ship_Date</yfc:i18n></td>
            <% } %>
            <td class="tablecolumnheader" style="width:50px"><yfc:i18n>Amount</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/@Status")%>"><yfc:i18n>Status</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/@strNote")%>"><yfc:i18n>Comments</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
	<!-- This will create the blank panel -->
			<jsp:include page="/extn/common/nwcg_new_render_orderline.jsp" flush="true"/>        
    </tbody>
    <tfoot>        
		
    </tfoot>
</table>
<!-- this will generate all the javascript function dynamically -->
<jsp:include page="/extn/common/nwcg_new_render_PO_orderline_js.jsp" flush="true"/>
