<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/workorder.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_workorderDetails.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_orderDetails.js"></script>
<%	
	String startDate = "";
	String startTime = "";
	String finishDate = "";
	String finishTime = "";
	String mwoType = resolveValue("xml:/NWCGMasterWorkOrder/@MasterWorkOrderType");
%>
<table class="view" width="100%">
	<tr>
        <td class="detaillabel" ><yfc:i18n>Node</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGMasterWorkOrder/@Node"/></td>
		<td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGMasterWorkOrder/@Enterprise"/></td>
		<td class="detaillabel" ><yfc:i18n></yfc:i18n></td>
		<td class="protectedtext"></td>

	</tr>
	<tr>
        <td class="detaillabel" ><yfc:i18n>Master Work Order/Receipt #</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGMasterWorkOrder/@MasterWorkOrderNo"/></td>
		<td class="detaillabel" ><yfc:i18n>Purpose</yfc:i18n></td>
			<td class="searchcriteriacell">
				<select class=combobox <%=getComboOptions("xml:/NWCGMasterWorkOrder/@Purpose")%>>
					<yfc:loopOptions binding="xml:WorkOrderPurposeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
						value="CodeValue" selected="xml:/NWCGMasterWorkOrder/@Purpose" isLocalized="Y"/>
				</select>
			</td>
			<td class="detaillabel" ><yfc:i18n>Master Work Order Type</yfc:i18n></td>
			<td class="searchcriteriacell">
				<select class=combobox <%=getComboOptions("xml:/NWCGMasterWorkOrder/@MasterWorkOrderType")%>>
					<yfc:loopOptions binding="xml:WorkOrderTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
						value="CodeValue" selected="xml:/NWCGMasterWorkOrder/@MasterWorkOrderType" isLocalized="Y"/>
				</select>
			</td>
		
	</tr>
	
	<tr>
        <td class="detaillabel" ><yfc:i18n>Priority</yfc:i18n></td>
		
		<td class="searchcriteriacell">
			<select class=combobox <%=getComboOptions("xml:/NWCGMasterWorkOrder/@Priority")%>>
					<yfc:loopOptions binding="xml:WorkOrderPriorityList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
						value="CodeValue" selected="xml:/NWCGMasterWorkOrder/@Priority" isLocalized="Y"/>
				</select>
		</td>
	</tr>
	<tr>


		
		<td class="detaillabel" ><yfc:i18n>Service_Item_ID</yfc:i18n></td>
	<td class="protectedtext" >REFURBISHMENT</td>

<td class="detaillabel" ><yfc:i18n>Incident_Number</yfc:i18n></td>
<td nowrap="true" >
<input type="text" size=25 class="unprotectedinput" name="xml:/NWCGMasterWorkOrder/@IncidentNo" <%=getTextOptions("xml:/NWCGMasterWorkOrder/@IncidentNo")%> id="xml:/NWCGMasterWorkOrder/@IncidentNo"/>
<img class="lookupicon" onclick="callIncidentLookup('xml:/NWCGMasterWorkOrder/@IncidentNo','xml:/NWCGMasterWorkOrder/@IncidentYear','NWCGIncidentLookup');IncidentYear.focus()" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Incident") %> />
</td>


<!-- Incident No and Year are now unique Key -->
<td class="detaillabel" ><yfc:i18n>Incident_Year</yfc:i18n></td>
<td nowrap="true" >
<input type="text" size="5" class="unprotectedinput" name="xml:/NWCGMasterWorkOrder/@IncidentYear"  id="IncidentYear" <%=getTextOptions("xml:/NWCGMasterWorkOrder/@IncidentYear")%> onblur="javascript:if(document.getElementById('xml:/NWCGMasterWorkOrder/@IncidentYear').value=='' || document.getElementById('xml:/NWCGMasterWorkOrder/@IncidentNo').value=='' ) {alert('Please enter Incident# and Year');return false;}
fetchDataWithParams(this,'NWCGGetIncidentOrderList',updateIncidentDetailsAcctsOnly,setIncidentParamsForRefurb(this));"/>
</td>

			</tr>
			<tr>
			<td class="detaillabel"><yfc:i18n>Incident_Name</yfc:i18n></td>
			<td>
				<input type="text" readonly=true size=25 class="unprotectedinput" <%=getTextOptions("xml:/NWCGMasterWorkOrder/@IncidentName")%>/>
				</td>
			<td class="detaillabel"><yfc:i18n>Fs_Acct_Code</yfc:i18n></td>
			<td>
				<input type="text" class="unprotectedinput" readonly=true size=25 <%=getTextOptions("xml:/NWCGMasterWorkOrder/@FSAccountCode")%>/>
				</td>
				<td class="detaillabel"><yfc:i18n>Override Code</yfc:i18n></td>
			<td>
				<input type="text" class="unprotectedinput" readonly=true size=25 <%=getTextOptions("xml:/NWCGMasterWorkOrder/@OverrideCode")%>/>
				</td>
			
			</tr>
			<tr>
				<td class="detaillabel"><yfc:i18n>Blm_Acct_Code</yfc:i18n></td>
				<td>
					<input type="text" class="unprotectedinput" readonly=true size=50 <%=getTextOptions("xml:/NWCGMasterWorkOrder/@BLMAccountCode")%>/>
				</td>
			<td class="detaillabel"><yfc:i18n>Other_Acct_Code</yfc:i18n></td>
			<td>
				<input type="text" class="unprotectedinput" readonly=true size=25 <%=getTextOptions("xml:/NWCGMasterWorkOrder/@OtherAccountCode")%>/>
			</td>
<%			
if(mwoType.equals("Refurb Transfer")){
%>
			<td class="detaillabel">Original Master Work Order #</td>
			<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGMasterWorkOrder/@SourceMWONo"/></td>
<%}else{%>			
			<td class="detaillabel"></td>
			<td></td>
<%}%>			
	</tr>
</table>