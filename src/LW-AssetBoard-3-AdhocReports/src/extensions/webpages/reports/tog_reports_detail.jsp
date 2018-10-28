<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*"%>
<%@ include file="/console/jsp/modificationutils.jspf"%>

<% 
	// Used in Resource: TOGReport: /extn/reports/tog_reports.jsp
%>

<script language="javascript">
	// 6 reports listed on this page, increase to 7 if you uncomment Back Order Detail Report option. 
	var count = 6;
</script>

<script language="javascript">
function validate() {
	var atLeastOne = 0;
	for (var j = 1; j <= count; j++) {
		if (atLeastOne > 1) {
			break;
		}
		if (document.getElementById("checkbox_" + j).checked) {
			atLeastOne++;
		}
	}		
	if (atLeastOne == 0) {
		alert("Please select at least 1 report. Thank you. ");
		return false;
	} else if (atLeastOne > 1) {
		alert("Please select only 1 report. Thank you.");			
		return false;			
	} else {
		alert("Your request has been received, watch for an email regarding further details. Thank you.");
		for (var j = 1; j <= count; j++) {
			if (document.getElementById("checkbox_" + j).checked) {
				break;
			}
		}
		yfcMultiSelectToSingleAPIOnAction(('EntityKey_' + j), 'yfcMultiSelectCounter', 'yfcMultiSelectValue1', 'TriggerAgentType', 'xml:/TriggerAgent/TriggerAgentType');
		yfcMultiSelectToSingleAPIOnAction(('EntityKey_' + j), 'yfcMultiSelectCounter', 'yfcMultiSelectValue2', 'TriggerAgentScreen', 'xml:/TriggerAgent/TriggerAgentScreen');		
		return true;
	}
	return true;
}
</script>

<table class="table" editable="false" width="100%" cellspacing="0">
	<thead>
		<tr>
			<td sortable="no" class="checkboxheader"></td>
			<td class="tablecolumnheader"><yfc:i18n>Report_Title</yfc:i18n></td>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td class="checkboxcolumn">
				<input id="checkbox_1" type="checkbox" name="EntityKey_1" yfcMultiSelectCounter='<%=1%>' yfcMultiSelectValue1='<%="Open_Orders"%>' yfcMultiSelectValue2='<%="Reports"%>' />
			</td>
			<td class="tablecolumn">
				<yfc:i18n>Open_Orders</yfc:i18n>
			</td>
		</tr>
		<tr>
			<td class="checkboxcolumn">
				<input id="checkbox_2" type="checkbox" name="EntityKey_2" yfcMultiSelectCounter='<%=2%>' yfcMultiSelectValue1='<%="Open_Order_Detail"%>' yfcMultiSelectValue2='<%="Reports"%>' />
			</td>
			<td class="tablecolumn">
				<yfc:i18n>Open_Order_Detail</yfc:i18n>
			</td>
		</tr>
		<tr>
			<td class="checkboxcolumn">
				<input id="checkbox_3" type="checkbox" name="EntityKey_3" yfcMultiSelectCounter='<%=3%>' yfcMultiSelectValue1='<%="Open_Order_Detail_ATP"%>' yfcMultiSelectValue2='<%="Reports"%>' />
			</td>
			<td class="tablecolumn">
				<yfc:i18n>Open_Order_Detail_ATP</yfc:i18n>
			</td>
		</tr>		
		<tr>
			<td class="checkboxcolumn">
				<input id="checkbox_4" type="checkbox" name="EntityKey_4" yfcMultiSelectCounter='<%=4%>' yfcMultiSelectValue1='<%="Back_Order_Summary"%>' yfcMultiSelectValue2='<%="Reports"%>' />
			</td>
			<td class="tablecolumn">
				<yfc:i18n>Back_Order_Summary</yfc:i18n>
			</td>
		</tr>
		<tr>
			<td class="checkboxcolumn">
				<input id="checkbox_5" type="checkbox" name="EntityKey_5" yfcMultiSelectCounter='<%=5%>' yfcMultiSelectValue1='<%="Order_Reconciliation"%>' yfcMultiSelectValue2='<%="Reports"%>' />
			</td>
			<td class="tablecolumn">
				<yfc:i18n>Order_Reconciliation</yfc:i18n>
			</td>
		</tr>
		<tr>
			<td class="checkboxcolumn">
				<input id="checkbox_6" type="checkbox" name="EntityKey_6" yfcMultiSelectCounter='<%=6%>' yfcMultiSelectValue1='<%="Item_Substitution"%>' yfcMultiSelectValue2='<%="Reports"%>' />
			</td>
			<td class="tablecolumn">
				<yfc:i18n>Item_SubStitution</yfc:i18n>
			</td>
		</tr>
		<!-- Back Order Detail Report is not required at this time -->
		<!--tr>
			<td class="checkboxcolumn">
				<input id="checkbox_7" type="checkbox" name="EntityKey_7" yfcMultiSelectCounter='<%=7%>' yfcMultiSelectValue1='<%="Back_Order_Detail"%>' yfcMultiSelectValue2='<%="Reports"%>' />
			</td>
			<td class="tablecolumn">
				<yfc:i18n>Back_Order_Detail</yfc:i18n>
			</td>
		</tr-->		
	</tbody>
</table>