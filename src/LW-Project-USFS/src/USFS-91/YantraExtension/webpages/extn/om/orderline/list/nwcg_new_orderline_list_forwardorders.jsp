<%@taglib  prefix="yfc" uri="/WEB-INF/yfc.tld" %>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.StringUtil" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script>
function createIssueFromForwardOrderLines(){
	// BEGIN - CR 1769 - Aug 12, 2015
	// Rearranged: 2 is OrderNo, 5 is New Issue Qty, and 6 is Fwd Order Qty
	yfcMultiSelectToSingleAPIOnAction('EntityKey', 'yfcMultiSelectCounter', 'yfcMultiSelectValue1', 'OrderLineKey', 'xml:/OrderLineList/OrderLine',null);
	yfcMultiSelectToSingleAPIOnAction('EntityKey', 'yfcMultiSelectCounter', 'yfcMultiSelectValue2', 'OrderNo', 'xml:/OrderLineList/OrderLine',null);
	yfcMultiSelectToSingleAPIOnAction('EntityKey', 'yfcMultiSelectCounter', 'yfcMultiSelectValue3', 'IncidentNo', 'xml:/OrderLineList/OrderLine',null);
	yfcMultiSelectToSingleAPIOnAction('EntityKey', 'yfcMultiSelectCounter', 'yfcMultiSelectValue4', 'UserCache', 'xml:/OrderLineList/OrderLine',null);
	yfcMultiSelectToSingleAPIOnAction('EntityKey', 'yfcMultiSelectCounter', 'yfcMultiSelectValue5', 'NewIssueQty', 'xml:/OrderLineList/OrderLine',null);
	yfcMultiSelectToSingleAPIOnAction('EntityKey', 'yfcMultiSelectCounter', 'yfcMultiSelectValue6', 'ExtnFwdQty', 'xml:/OrderLineList/OrderLine',null);
	// END - CR 1769 - Aug 12, 2015
	return true;
}
function checkBLMCacheAndAccountCode(elem) {
	if (elem.checked) {
		var returnArray = new Object();	
		returnArray["xml:OrderNo"] = elem.yfcMultiSelectValue2;
		fetchDataWithParams(elem, 'getOrderList_ForwardOrder', getIncidentAndYear, returnArray);
	}
}
function getIncidentAndYear(elem, xmlDoc) {
	var ResultList = xmlDoc.getElementsByTagName("Extn");
	if(ResultList.length > 0) {	
		var ResultNode = ResultList(0);
		var IncidentNo = ResultNode.getAttribute("ExtnIncidentNo").replace(/(^\s*)|(\s*$)/gi, "");
		var IncidentYear = ResultNode.getAttribute("ExtnIncidentYear").replace(/(^\s*)|(\s*$)/gi, "");
		var returnArray = new Object();	
		returnArray["xml:IncidentNo"] = IncidentNo;
		returnArray["xml:IncidentYear"] = IncidentYear;
		fetchDataWithParams(elem, 'NWCGIncidentOrder_ForwardOrder', validateAccountCodeAndCache, returnArray);
	}
}
function validateAccountCodeAndCache(elem, xmlDoc) {
	var ResultList = xmlDoc.getElementsByTagName("NWCGIncidentOrder");
	if(ResultList.length > 0) {	
		var ResultNode = ResultList(0);
		var IncidentBlmAcctCode = ResultNode.getAttribute("IncidentBlmAcctCode");
		// BEGIN - CR 1769 - Aug 12, 2015
		var IncidentFsAcctCode = ResultNode.getAttribute("IncidentFsAcctCode");
		var sToFSOverrideAccCode = ResultNode.getAttribute("OverrideCode");	
		var IncidentOtherAcctCode = ResultNode.getAttribute("IncidentOtherAcctCode");
		// END - CR 1769 - Aug 12, 2015
		if(elem.yfcMultiSelectValue4 == 'IDGBK' || elem.yfcMultiSelectValue4 == 'AKAKK' || elem.yfcMultiSelectValue4 == 'MTBFK') {
			if(IncidentBlmAcctCode == null || IncidentBlmAcctCode.replace(/(^\s*)|(\s*$)/gi, "") == "..") {
				alert('This is a BLM Cache (' + elem.yfcMultiSelectValue4 + ') and the Incident Number (' + elem.yfcMultiSelectValue3 + ') for this record does not have a valid BLM Account Code! \nIf you need to fill the selected order from this BLM cache you will need to update the Incident with a valid BLM account code.');
				elem.checked = false;
				return false;
			} 
		} 
		// BEGIN - CR 1769 - Aug 12, 2015
		if(elem.yfcMultiSelectValue4 == 'IDCDK') {
			if(IncidentOtherAcctCode == null || IncidentOtherAcctCode.replace(/(^\s*)|(\s*$)/gi, "") == "") {
				alert('This is a State Cache (' + elem.yfcMultiSelectValue4 + ') and the Incident Number (' + elem.yfcMultiSelectValue3 + ') for this record does not have a valid Other Account Code! \nIf you need to fill the selected order from this State cache you will need to update the Incident with a valid Other account code.');
				elem.checked = false;
				return false;
			} 			
		}
		if(elem.yfcMultiSelectValue4 == 'MTNRK' || elem.yfcMultiSelectValue4 == 'ORNWK' || elem.yfcMultiSelectValue4 == 'ORLGK' || elem.yfcMultiSelectValue4 == 'WAWFK' || elem.yfcMultiSelectValue4 == 'CALSK' || elem.yfcMultiSelectValue4 == 'CANCK' || elem.yfcMultiSelectValue4 == 'CORMK' || elem.yfcMultiSelectValue4 == 'NMSFK' || elem.yfcMultiSelectValue4 == 'AZPFK' || elem.yfcMultiSelectValue4 == 'KYSAK' || elem.yfcMultiSelectValue4 == 'MNNEK') {
			if((IncidentFsAcctCode == null || IncidentFsAcctCode.replace(/(^\s*)|(\s*$)/gi, "") == "") && (sToFSOverrideAccCode == null || sToFSOverrideAccCode.replace(/(^\s*)|(\s*$)/gi, "") == "")) {
				alert('This is a FS Cache (' + elem.yfcMultiSelectValue4 + ') and the Incident Number (' + elem.yfcMultiSelectValue3 + ') for this record does not have a valid FS Account Code! \nIf you need to fill the selected order from this FS cache you will need to update the Incident with a valid FS account code.');
				elem.checked = false;
				return false;
			} 
		}
		// END - CR 1769 - Aug 12, 2015
	} 
}	
function setMultiSelectvalue(elem, counter) {
	if(elem.value == null) {
		return; 
	}
	// BEGIN - CR 1769 - Aug 12, 2015
	if(elem.value <= 0) {
		alert('The IssueQty cannot be less than 1. Please enter the quantity again !!');
		elem.value = "";
		return; 
	}
	// END - CR 1769 - Aug 12, 2015
	var entities = document.all("EntityKey");
	if(typeof entities.length =='undefined') {
		// BEGIN - CR 1769 - Aug 12, 2015
		//entities.yfcMultiSelectValue2 = elem.value;
		entities.yfcMultiSelectValue5 = elem.value;
		// END - CR 1769 - Aug 12, 2015
	} else {
		// BEGIN - CR 1769 - Aug 12, 2015
		//entities[counter -1].yfcMultiSelectValue2 = elem.value;
		entities[counter -1].yfcMultiSelectValue5 = elem.value;
		// END - CR 1769 - Aug 12, 2015
	}
}
<%
	String currentUserOrgKey = resolveValue("xml:CurrentUser:/User/@OrganizationKey");
	if (StringUtil.isEmpty(currentUserOrgKey)) {
		currentUserOrgKey = resolveValue("xml:CurrentUser:/User/@Node");
	}
%>
</script>

<table class="table" cellpadding="0" cellspacing="0" width="100%">
	<thead>
		<tr>
			<td sortable="no" class="checkboxheader">
				<input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
			</td>
			<td class="tablecolumnheader"><yfc:i18n>Ship_Cache</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Incident_Other_Order_No</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Issue_No</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Request_No</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Item_ID</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>PC</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>UOM</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Item_Description</yfc:i18n></td>
			<td class="numerictablecolumnheader"><yfc:i18n>Line_Qty</yfc:i18n></td>
			<td class="numerictablecolumnheader"><yfc:i18n>Forward_Order_Qty</yfc:i18n></td>
			<td class="numerictablecolumnheader"><yfc:i18n>Issue_Qty</yfc:i18n></td>
		</tr>
	</thead>
	<tbody>
		<yfc:loopXML name="OrderLineList" binding="xml:/OrderLineList/@OrderLine" id="OrderLine">
<% 
			//if(!isVoid(resolveValue("xml:OrderLine:/OrderLine/@Status"))) {
%>
				<yfc:makeXMLInput name="orderLineKey">
					<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey" />
					<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderHeaderKey" value="xml:/OrderLine/@OrderHeaderKey" />
					<yfc:makeXMLKey binding="xml:/OrderLineDetail/@UserCache" value="<%=currentUserOrgKey%>" />
				</yfc:makeXMLInput>
				<tr>
					<td class="checkboxcolumn" >
						<!-- BEGIN - CR 1769 - Aug 12, 2015 -->
						<!-- Added Multi Select 6 -->
						<input type="checkbox" value='<%=getParameter("orderLineKey")%>' name="EntityKey" onClick="checkBLMCacheAndAccountCode(this);"
							yfcMultiSelectCounter='<%=OrderLineCounter%>' 
							yfcMultiSelectValue1='<%=resolveValue("xml:/OrderLine/@OrderLineKey")%>'
							yfcMultiSelectValue2='<%=resolveValue("xml:/OrderLine/Order/@OrderNo")%>' 
							yfcMultiSelectValue3='<%=resolveValue("xml:/OrderLine/Order/Extn/@ExtnIncidentNo")%>' 
							yfcMultiSelectValue4='<%=currentUserOrgKey%>'
							yfcMultiSelectValue6='<%=resolveValue("xml:/OrderLine/Extn/@ExtnFwdQty")%>' />
						<!-- END - CR 1769 - Aug 12, 2015 -->
						<input type="hidden" name='OrderLineKey_<%=OrderLineCounter%>' value='<%=resolveValue("xml:/OrderLine/@OrderLineKey")%>' />
						<input type="hidden" name='OrderHeaderKey_<%=OrderLineCounter%>' value='<%=resolveValue("xml:/OrderLine/@OrderHeaderKey")%>' />
					</td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/@ShipNode"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Order/Extn/@ExtnIncidentNo"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Order/@OrderNo"/></td>
					<td class="tablecolumn">
						<yfc:getXMLValue binding="xml:/OrderLine/Extn/@ExtnRequestNo"/>
					</td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ProductClass"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/@OrderingUOM"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemDesc"/></td>
					<td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/Extn/@ExtnOrigReqQty")%>">
						<yfc:getXMLValue binding="xml:OrderLine:/OrderLine/Extn/@ExtnOrigReqQty"/>
					</td>
					<td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/Extn/@ExtnFwdQty")%>">
						<yfc:getXMLValue binding="xml:OrderLine:/OrderLine/Extn/@ExtnFwdQty"/>
					</td>
					<td>
						<input class="unprotectedtext" type="text" size="4" name="ExtnIssueQty_"+<%=OrderLineCounter%> onblur="setMultiSelectvalue(this,'<%=OrderLineCounter%>');"/>
					</td>
<% 
						if (equals("Y", getValue("OrderLine", "xml:/OrderLine/@HoldFlag"))) { 
%>
							<img class="icon" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_order_line_is_held")%>/>
<% 	
						} 
						if(!isVoid(resolveValue("xml:OrderLine:/OrderLine/@Status"))) {
							// Do Nothing...
						} else {						
							if (equals("Y", getValue("OrderLine", "xml:/OrderLine/@isHistory") )) { 
%>
								<img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.HISTORY_ORDER, "This_is_an_archived_order_line")%>/>
<% 
							}																
						}
%>
					</td>
				</tr>
<%
			//}
%>
		</yfc:loopXML>
	</tbody>
</table>