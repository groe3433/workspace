<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.util.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/im.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/wmsim.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<div style="width:950px;height:250px;overflow:auto">
	<table  editable="false" class="table">
		<thead>
			<tr>
			    <td class="tablecolumnheader"><yfc:i18n>Count_Request_#</yfc:i18n></td>
				<td class="tablecolumnheader"><yfc:i18n>Count_Iteration</yfc:i18n></td>
				<td class="tablecolumnheader"><yfc:i18n>Location</yfc:i18n></td>
				<td class="tablecolumnheader"><yfc:i18n>Organization</yfc:i18n></td>
				<td class="tablecolumnheader"><yfc:i18n>Pallet_ID</yfc:i18n></td>
				<td class="tablecolumnheader"><yfc:i18n>Parent_Case_ID</yfc:i18n></td>
				<td class="tablecolumnheader"><yfc:i18n>Case_ID</yfc:i18n></td>				
				<td class="tablecolumnheader"><yfc:i18n>Variance_Type</yfc:i18n></td>
				<td class="tablecolumnheader"><yfc:i18n>Count_Entered_By</yfc:i18n></td>
				<td class="tablecolumnheader"><yfc:i18n>Count_Entered_Date</yfc:i18n></td>			
			</tr>
		</thead>
		<tbody>
		<yfc:loopXML binding="xml:/CountResultList/@SummaryResultList" id="SummaryResultList">
		    <yfc:loopXML binding="xml:/SummaryResultList/@CountResult" id="CountResult">
			<% if(( !isVoid(resolveValue("xml:/SummaryResultList/CountResult/@CaseId"))
			        || !isVoid(resolveValue("xml:/SummaryResultList/CountResult/@PalletId")))
					&&(isVoid(resolveValue("xml:/SummaryResultList/CountResult/@ItemID")))) {%>
			    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/CountResult/CountRequest/@CountRequestNo"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/CountResult/CountRequest/@CountIteration"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/CountResult/@LocationId"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/CountResult/@OrganizationCode"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/CountResult/@PalletId"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/CountResult/@ParentCaseId"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/CountResult/@CaseId"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/CountResult/@VarianceType"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/CountResult/@Createuserid"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/CountResult/@Createts"/></td>

			   </tr>
			<%}%>
			</yfc:loopXML>
		</yfc:loopXML>
	</tbody>	
</table>
