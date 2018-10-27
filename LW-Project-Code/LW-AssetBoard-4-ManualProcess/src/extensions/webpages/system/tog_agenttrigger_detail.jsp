<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*"%>
<%@ include file="/console/jsp/modificationutils.jspf"%>

<% 
	// Used in Resource: TOGAgentTrigger: /extn/system/tog_agent_trigger.jsp
%>

<script language="javascript">
	// 5 reports listed on this page
	var count = 3;
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
		alert("Please select at least 1 Schedule or Release Process to run. Thank you. ");
		return false;
	} else if (atLeastOne > 1) {
		alert("Please select only 1 Schedule or Release Process to run. Thank you.");			
		return false;			
	} else {
		for (var j = 1; j <= count; j++) {
			if (document.getElementById("checkbox_" + j).checked) {
				break;
			}
		}
		if(j == 1) {
			alert("Schedule Orders process has been started. Thank you.");
		} else if(j == 2) {
			alert("Schedule Back Orders process has been started. Thank you.");			
		} else if(j == 3) {
			alert("Release Orders process has been started. Thank you.");			
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
			<td class="tablecolumnheader"><yfc:i18n>Agent_Title</yfc:i18n></td>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td class="checkboxcolumn">
				<input id="checkbox_1" type="checkbox" name="EntityKey_1" yfcMultiSelectCounter='<%=1%>' yfcMultiSelectValue1='<%="Schedule_Agent"%>' yfcMultiSelectValue2='<%="Manual_Processing"%>' />
			</td>
			<td class="tablecolumn">
				<yfc:i18n>Schedule_Agent</yfc:i18n>
			</td>
		</tr>
		<tr>
			<td class="checkboxcolumn">
				<input id="checkbox_2" type="checkbox" name="EntityKey_2" yfcMultiSelectCounter='<%=2%>' yfcMultiSelectValue1='<%="Schedule_Backorder_Agent"%>' yfcMultiSelectValue2='<%="Manual_Processing"%>' />
			</td>
			<td class="tablecolumn">
				<yfc:i18n>Schedule_Backorder_Agent</yfc:i18n>
			</td>
		</tr>
		<tr>
			<td class="checkboxcolumn">
				<input id="checkbox_3" type="checkbox" name="EntityKey_3" yfcMultiSelectCounter='<%=3%>' yfcMultiSelectValue1='<%="Release_Agent"%>' yfcMultiSelectValue2='<%="Manual_Processing"%>' />
			</td>
			<td class="tablecolumn">
				<yfc:i18n>Release_Agent</yfc:i18n>
			</td>
		</tr>		
	</tbody>
</table>