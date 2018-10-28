<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript">
	function refreshPageAndDeleteActivity(key)
	{
		var eleArray = document.forms["containerform"].elements;
		for ( var i = 0; i < eleArray.length; i++ ) {
			if ( eleArray[i].name == key ) {

				if (eleArray[i].checked) {

					var counterValue = eleArray[i].getAttribute('yfcMultiSelectCounter');
					var multiInputValue = eleArray[i].getAttribute('yfcMultiSelectValue1');
					var name="xml:/WorkOrder/DeletedActivity/@Activity_"+multiInputValue;
					var hiddenKeyInput = document.createElement("<INPUT type='hidden' name='" + name + "'>");
					hiddenKeyInput.value = "Y";
					eleArray.appendChild(hiddenKeyInput);					
				}
			}
		}
		yfcChangeDetailView(getCurrentViewId());
	}
</script>
<%  
    String modifyView = resolveValue("xml:/WorkOrder/@WorkOrderKey");
    modifyView = modifyView == null ? "" : modifyView;

	String ItemId=resolveValue("xml:/WorkOrder/@ItemId");
%>
<table class="table" >
<tbody>
    <tr>
		<td width="10%" >
			&nbsp;
		</td>
		<td width="80%" style="border:1px solid black">
			<table class="table" editable="true" width="100%" cellspacing="0">
				<thead>
					<tr>
							<td class="tablecolumnheader"><yfc:i18n></yfc:i18n></td>
							<td class="tablecolumnheader"><yfc:i18n>Work_Order_Preferred_Location</yfc:i18n></td>
							<td class="tablecolumnheader"><yfc:i18n>Requested_Quantity</yfc:i18n></td>
						<% if(modifyView != "") {%> 
							<td class="tablecolumnheader"><yfc:i18n>Work_Order_Quantity_Completed</yfc:i18n></td>
							<td class="tablecolumnheader"><yfc:i18n>Pallet_ID</yfc:i18n></td>
							<td class="tablecolumnheader"><yfc:i18n>Case_ID</yfc:i18n></td>
							<td class="tablecolumnheader"><yfc:i18n>Serial_#</yfc:i18n></td>
						<%}%>
				</tr>
				</thead>
				<tbody>
				<yfc:loopXML binding="xml:/WorkOrder/WorkOrderActivities/@WorkOrderActivity" id="WorkOrderActivity">
					<%String sLineNo=getParameter("optionSetBelongingToLine");
					Integer myInteger=new Integer(Integer.parseInt(sLineNo));
					String ActivityCode=resolveValue("xml:/WorkOrderActivity/@ActivityCode");
					String ActivitySeqNo=resolveValue("xml:/WorkOrderActivity/@ActivitySeqNo");
					if (equals(myInteger,WorkOrderActivityCounter)){%>
						<yfc:loopXML binding="xml:/WorkOrderActivity/WorkOrderActivityDtls/@WorkOrderActivityDtl" id="WorkOrderActivityDtl">
							<% String LocationActivityCode=resolveValue("xml:/WorkOrderActivityDtl/@ActivityCode");
							if(equals(ActivityCode,LocationActivityCode)){%>
							<tr>
								<yfc:makeXMLInput name="WorkOrderActivityLocationKey" >
									<yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderKey" value="xml:/WorkOrder/@WorkOrderKey" />
									<yfc:makeXMLKey binding="xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity/@ActivityCode" value="xml:/WorkOrderActivity/@ActivityCode" />
									<yfc:makeXMLKey binding="xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity/@ActivitySeqNo" value="xml:/WorkOrderActivity/@ActivitySeqNo" />
									<yfc:makeXMLKey binding="xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity/WorkOrderActivityDtls/WorkOrderActivityDtl/@WorkOrderActivityDtlKey" value="xml:/WorkOrderActivityDtl/@WorkOrderActivityDtlKey" />
								</yfc:makeXMLInput>
								<%if(!isVoid(resolveValue("xml:/WorkOrderActivityDtl/@ActivityLocationId"))){%>
									<td class="checkboxcolumn" >
										<input type="checkbox" value='<%=getParameter("WorkOrderActivityLocationKey")%>' name="chkModifyPreferredLocationKey" yfcMultiSelectCounter='<%=WorkOrderActivityDtlCounter%>' yfcMultiSelectValue1='<%=resolveValue("xml:/WorkOrderActivityDtl/@WorkOrderActivityLocationKey")%>'/>
									</td>					
								<%}else{%>
									<td class="tablecolumn"/>
								<%}%>
								<td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrderActivityDtl/@ActivityLocationId"/></td>
								<td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrderActivityDtl/@QuantityRequested"/></td>
								<% if(modifyView != "") {%> 
									<td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrderActivityDtl/@QuantityCompleted"/></td>
									<td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrderActivityDtl/@PalletId"/></td>
									<td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrderActivityDtl/@CaseId"/></td>
									<td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrderActivityDtl/@SerialNo"/></td>
								<%}%>
							</tr>
							<%}%>
						</yfc:loopXML>
					<%}%> 
					</yfc:loopXML>  
				</tbody>
	<%if(equals("Y",resolveValue("xml:/WorkOrder/@WorkOrderNotCompleted")) || isVoid(resolveValue("xml:/WorkOrder/@WorkOrderKey"))){%>
		<tfoot> 
			<%String sLineNo=getParameter("optionSetBelongingToLine");%>
			<tr style='display:none' TemplateRow="true">
						<td class="checkboxcolumn">&nbsp;</td>
						<td class="searchcriteriacell" nowrap="true" >
							<input class="unprotectedinput" type="text"
<%=getTextOptions("xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity_"+sLineNo+"/WorkOrderActivityDtls/WorkOrderActivityDtl_/@ActivityLocationId")%>/>
							<img class="lookupicon" onclick="callLookup(this,'location','xml:/Location/@Node=<%=resolveValue("xml:/WorkOrder/@NodeKey")%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Location") %> />
						<td nowrap="true" class="tablecolumn">
							<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity_"+sLineNo+"/WorkOrderActivityDtls/WorkOrderActivityDtl_/@QuantityRequested")%>/> 
						</td>
					<% if(modifyView != "") {%> 
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					<%}%>
			   </tr>
			   <tr>
					<% if(modifyView != "") {%> 
						<td nowrap="true" colspan="7">
							<jsp:include page="/common/editabletbl.jsp" flush="true">
							</jsp:include>
						</td>
					<%} else {%>
						<td nowrap="true" colspan="3">
							<jsp:include page="/common/editabletbl.jsp" flush="true">
							</jsp:include>
						</td>
					<%}%>
					</tr>
				</tfoot>
			<%}%>
			</table>
		</td>
	</tr>
</tbody>
</table>