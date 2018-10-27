<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.util.*" %>
<%@ page import="com.yantra.wms.util.WMSNumberFormat" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/yfsjspcommon/yfsutil.jspf"%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/im.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/wmsim.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<div style="width:950px;overflow:auto">
<table  editable="false" class="table">
	<thead>
		<tr>
			<td class="tablecolumnheader" style="width:30px" sortable="no"><yfc:i18n>Details</yfc:i18n></td>
			<td class="tablecolumnheader" sortable="no"><yfc:i18n>Count_Request_#</yfc:i18n></td>
			<td class="tablecolumnheader" sortable="no"><yfc:i18n>Count_Iteration</yfc:i18n></td>
			<td class="tablecolumnheader" sortable="no"><yfc:i18n>Organization</yfc:i18n></td>
			<td class="tablecolumnheader" sortable="no"><yfc:i18n>Location</yfc:i18n></td>			
			<td class="tablecolumnheader" sortable="no"><yfc:i18n>Pallet_ID</yfc:i18n></td>
			<td class="tablecolumnheader" sortable="no"><yfc:i18n>Parent_Case_ID</yfc:i18n></td>
			<td class="tablecolumnheader" sortable="no"><yfc:i18n>Case_ID</yfc:i18n></td>
			<td class="tablecolumnheader" sortable="no"><yfc:i18n>Item_ID</yfc:i18n></td>
			<td class="tablecolumnheader" sortable="no"><yfc:i18n>Description</yfc:i18n></td>
			<td class="tablecolumnheader" sortable="no"><yfc:i18n>Product_Class</yfc:i18n></td>
			<td class="tablecolumnheader" sortable="no"><yfc:i18n>UOM</yfc:i18n></td>			
			<td class="tablecolumnheader" sortable="no"><yfc:i18n>Net_Variance_Quantity</yfc:i18n></td>
			<td class="tablecolumnheader" sortable="no"><yfc:i18n>Net_Variance_Value</yfc:i18n></td>
			<td class="tablecolumnheader" sortable="no"><yfc:i18n>Currency</yfc:i18n></td>
				
		</tr>
	</thead>
	<tbody>
	<%String className="oddrow";%>
		<yfc:loopXML binding="xml:/CountResultList/@SummaryResultList" id="SummaryResultList">
		 <% if( !isVoid(resolveValue("xml:/SummaryResultList/@ItemID"))){%>
			<tr class='<%=className%>'>
				<td>
				<img onclick="expandCollapseDetails('optionSet_<%=SummaryResultListCounter%>','<%=getI18N("Click_To_See_Details")%>','<%=getI18N("Click_To_Hide_Details")%>','<%=YFSUIBackendConsts.FOLDER_COLLAPSE%>','<%=YFSUIBackendConsts.FOLDER_EXPAND%>')"  style="cursor:hand" <%=getImageOptions(YFSUIBackendConsts.FOLDER,"Click_To_See_Details")%> />
				</td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/SummaryResultList/CountResult/CountRequest/@CountRequestNo"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/SummaryResultList/CountResult/CountRequest/@CountIteration"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/SummaryResultList/@OrganizationCode"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/SummaryResultList/@LocationId"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/SummaryResultList/@PalletId"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/SummaryResultList/CountResult/@ParentCaseId"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/SummaryResultList/@CaseId"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/SummaryResultList/@ItemID"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/SummaryResultList/CountResult/Item/@Description"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/SummaryResultList/@ProductClass"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/SummaryResultList/@UnitOfMeasure"/></td>			
				<td class="numerictablecolumn">
				<%=WMSNumberFormat.getUnsignedValue(resolveValue("xml:/SummaryResultList/@NetVarianceQuantity"))%>
				<%=WMSNumberFormat.getSign(resolveValue("xml:/SummaryResultList/@NetVarianceQuantity"))%>
				</td>
				<td class="numerictablecolumn">
				<%=WMSNumberFormat.getUnsignedValue(resolveValue("xml:/SummaryResultList/@NetVarianceValue"))%>
				<%=WMSNumberFormat.getSign(resolveValue("xml:/SummaryResultList/@NetVarianceValue"))%>
				</td>
				<td class="numerictablecolumn">
				   <yfc:getXMLValue   binding="xml:/SummaryResultList/CountResult/@Currency"/>
				</td>
				
		        <tr id='<%="optionSet_"+SummaryResultListCounter%>' class='<%=className%>' style="display:none" >
					<td colspan="7" >
						<jsp:include page="/wms/wmscountresult/detail/count_result_detail_includeresults.jsp" flush="true">
							<jsp:param name="optionSetBelongingToLine" value='<%=String.valueOf(SummaryResultListCounter)%>'/>
						</jsp:include>
					</td>
				</tr>
			</tr>
			</tr>
			<%}%>
		</yfc:loopXML>
	</tbody>	
</div>
</table>
