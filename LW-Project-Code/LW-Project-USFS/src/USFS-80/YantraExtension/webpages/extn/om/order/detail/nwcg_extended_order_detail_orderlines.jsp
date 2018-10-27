<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.ResourceUtil" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.StringUtil" %>
<%@ include file="/extn/yfsjspcommon/nwcg_extended_editable_util_lines.jspf" %>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/css/scripts/editabletbl.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_orderDetails.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_prepareOrderLine.js"></script>
<script language="javascript">
    document.body.attachEvent("onunload", processSaveRecordsForChildNode);
    yfcDoNotPromptForChangesForActions(true);
</script>

<%
	String modifyView = request.getParameter("ModifyView");
	modifyView = modifyView == null ? "" : modifyView;
	
	session.setAttribute("ReservedItems",null);
	String strDocType = resolveValue("xml:/Order/@DocumentType");
	boolean bOtherIssues = false ;
	if(strDocType != null && (!strDocType.equals("0001")))
	{
		bOtherIssues = true ;
	}

	boolean bAppendOldValue = false;
	if(!isVoid(errors) || equals(sOperation,"Y") || equals(sOperation,"DELETE")) 
		bAppendOldValue = true;	

    String driverDate = getValue("Order", "xml:/Order/@DriverDate");
	String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("Order", "xml:/Order/@EnterpriseCode"));

	String sDraftOrderFlag = getValue("Order", "xml:/Order/@DraftOrderFlag");
	
	// Conditionally including System Number column
	boolean displaySysNoCol = false;		
	YFCElement getOrderLineListIP = null;	
	String ohk = resolveValue("xml:/Order/@OrderHeaderKey");
	getOrderLineListIP = YFCDocument.parse("<OrderLine OrderHeaderKey=\"" + ohk + "\" />").getDocumentElement();
	YFCElement getOrderLineListTemplate = YFCDocument.parse("<OrderLineList TotalLineList=\"\"> <OrderLine OrderLineKey=\"\"><Extn ExtnSystemNo=\"\"/></OrderLine></OrderLineList>").getDocumentElement();
	int numLinesWithSys = 0;
	String temp = "";
	int totalLinesOnOrder = 0;

	//Prepare the input for getCommonCodeList to get NWCG_LINE_VIEW - Make "Get Avail RFI" for each line
	//input element for API call
	String commonCodeInputXml = "<CommonCode CodeValue=\"Get Avail RFI Qty for every line\"/>";
	String commonCodeOpTp = "<CommonCodeList><CommonCode CodeShortDescription=\"\"/></CommonCodeList>";
	YFCElement commonCodeInput = YFCDocument.parse(commonCodeInputXml).getDocumentElement();
	//templement element for API call
	YFCElement commonCodeTemplate = YFCDocument.parse(commonCodeOpTp).getDocumentElement();
%>
<yfc:callAPI apiName="getCommonCodeList" inputElement="<%=commonCodeInput%>" templateElement="<%=commonCodeTemplate%>" outputNamespace="useLiveInv"/>
<%
	String codeShortDescStr = resolveValue("xml:useLiveInv:/CommonCodeList/CommonCode/@CodeShortDescription");
	String useLiveRfiStr = "false";
	if (codeShortDescStr != null && !codeShortDescStr.equals("")) {
		if (codeShortDescStr.equalsIgnoreCase("true") ||
				codeShortDescStr.equalsIgnoreCase("yes") ||
				codeShortDescStr.equalsIgnoreCase("y")) {
			String statusText = getValue("Order","xml:/Order/@Status");
			if (!StringUtil.isEmpty(statusText)) {
				if (statusText.contains("raft") || statusText.contains("reated")) {
					useLiveRfiStr = "true";
				}
			}
		}
	}
%>
<input type="hidden" name="UseLiveRFI" value="<%=useLiveRfiStr%>" />

<yfc:callAPI apiName="getOrderLineList" inputElement="<%=getOrderLineListIP%>" templateElement="<%=getOrderLineListTemplate%>" outputNamespace="orderLineList"/>
<yfc:loopXML binding="xml:orderLineList:/OrderLineList/@OrderLine" id="OrderLine" > 
<%	
	totalLinesOnOrder++;
	temp = resolveValue("xml:/OrderLine/Extn/@ExtnSystemNo");
	if (temp != null && temp.trim().length() > 0) {
		numLinesWithSys++;
	}		
%>
</yfc:loopXML> 
<input type="hidden" name="totalLinesOnOrder" value="<%=totalLinesOnOrder%>"/>
<% if (numLinesWithSys != 0) { displaySysNoCol = true; } 
   if (numLinesWithSys == 0 && totalLinesOnOrder > 0) { displaySysNoCol = false; }
%>
<table class="sortableTable" ID="OrderLines" cellspacing="0" width="100%" yfcMaxSortingRecords="1000">
	<thead>
		<tr>
			<td class="checkboxheader" sortable="no" style="width:5px"><input
				type="hidden" id="userOperation" name="userOperation" value="" /> <input
				type="hidden" id="numRowsToAdd" name="numRowsToAdd" value="" /> <input
				type="checkbox" value="checkbox" name="checkbox"
				onclick="doCheckFirstLevel(this);" /></td>
			<td class="numerictablecolumnheader" nowrap="true" style="width:5px">Line</td>
			<td class="tablecolumnheader" nowrap="false" style="width:10px"><yfc:i18n>S_#</yfc:i18n></td>
			<td class="numerictablecolumnheader" nowrap="true" style="width:40px"><yfc:i18n>Item_ID</yfc:i18n></td>
			<!--ROLLBACK of CR 338 PC BEGIN   --> 		
			<td class="tablecolumnheader" style="width:40px" sortable="no"><yfc:i18n>PC</yfc:i18n></td> 
			<!--ROLLBACK of CR 338 PC END   -->
			<td class="tablecolumnheader" style="width:10px" sortable="no"><yfc:i18n>UOM</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="false" style="width:110px" sortable="no"><yfc:i18n>Description</yfc:i18n></td>	
			<%if (displaySysNoCol) { %>
			<td class="tablecolumnheader" style="width:10px" sortable="no"><yfc:i18n>System_Number</yfc:i18n></td>
			<% } %>
			<td class="tablecolumnheader" nowrap="true" sortable="no" align="center"
				style="width:40px"><yfc:i18n>Requested_Qty</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="false" sortable="no"
				style="width:40px"><yfc:i18n>Avail_RFI_Qty</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="false" sortable="no"
				style="width:<%=getUITableSize("xml:/OrderLine/Extn/@ExtnFwdQty")%>"><yfc:i18n>Issue_Qty</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="true" sortable="no"
				style="width:30px"><yfc:i18n>Actual Pricing Qty</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="false" sortable="no"
				style="width:<%=getUITableSize("xml:/OrderLine/Extn/@ExtnUTFQty")%>"><yfc:i18n>UTF_Qty</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="true" sortable="no"
				style="width:<%=getUITableSize("xml:/OrderLine/Extn/@ExtnBackorderedQty")%>"><yfc:i18n>Backordered_Qty</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="true" sortable="no"
				style="width:<%=getUITableSize("xml:/OrderLine/Extn/@ExtnFwdQty")%>"><yfc:i18n>Forwarded_Qty</yfc:i18n></td>

			<% if (equals(driverDate, "02")) { %>
			<td class="tablecolumnheader" nowrap="true" sortable="no"
				style="width:80px"><yfc:i18n>Delivery_Date</yfc:i18n></td>
			<% } else { %>
			<td class="tablecolumnheader" nowrap="true" sortable="no"
				style="width:80px"><yfc:i18n>Ship_Date</yfc:i18n></td>
			<% } %>
			<td class="tablecolumnheader" nowrap="true" sortable="no"
				style="width:100px"><yfc:i18n>Comments</yfc:i18n></td>
			<td class="numerictablecolumnheader" style="width:50px"><yfc:i18n>Amount</yfc:i18n></td>
			<td class="tablecolumnheader"
				style="width:<%=getUITableSize("xml:/OrderLine/@Status")%>" sortable="no"><yfc:i18n>Status</yfc:i18n></td>
		</tr>
	</thead>
	<tbody>
		<!-- This will create the blank panel -->
		<jsp:include page="/extn/common/nwcg_new_render_orderline.jsp" flush="true" />
	</tbody>
	<tfoot>

	</tfoot>
</table>
<!-- this will generate all the javascript function dynamically -->
<jsp:include page="/extn/common/nwcg_new_render_orderline_js.jsp" flush="true" />
