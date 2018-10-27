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
	function setInventoryUpdateActivity(InvUpdateActivityCode){
		if(InvUpdateActivityCode != ""){
			document.all("xml:/WorkOrder/@InvUpdateActivityCode").value = InvUpdateActivityCode;
		}
	}
</script>
<%
    String modifyView = resolveValue("xml:/WorkOrder/@WorkOrderKey");
	int tfootCounter = 1;
    modifyView = modifyView == null ? "" : modifyView;

	if(modifyView == ""){
		YFCElement activityDetails = (YFCElement)request.getAttribute("ActivityDetails");
		if(isVoid(activityDetails)){
%>
			<yfc:callAPI apiID='AP3'/>
			<yfc:loopXML binding="xml:ServiceItemList:/ItemList/@Item" id="Item">
				<yfc:callAPI apiID='AP2'/>
			</yfc:loopXML>
<%
			activityDetails = (YFCElement)request.getAttribute("WorkOrderServiceActivityList");
	
		}
		request.setAttribute("WorkOrderServiceActivityList",activityDetails);
		YFCElement activityDetail = (YFCElement)request.getAttribute("WorkOrderServiceActivityList");
		if(activityDetail != null){
			String [] serviceActivityAttributes = {"ActivitySeqNo"};
			activityDetail.sortNumericChildren(serviceActivityAttributes);
		}

	}else{%>
		<yfc:callAPI apiID='AP1'/>
	<%}

	//check if components are there or not. If not then put the div tag to show the activities in entire width
	YFCElement root = (YFCElement)request.getAttribute("WorkOrder");
	YFCElement itemComponentElem = null;
	YFCElement individualComponent = null;
	if(root != null){
		itemComponentElem= root.getChildElement("WorkOrderComponents");
	}
	if(itemComponentElem != null){
		individualComponent = itemComponentElem.getChildElement("WorkOrderComponent");
	}
	 if(individualComponent != null){
		 if(individualComponent.toString() != ""){%>


<div style="height:250px;width:485px;overflow:auto">
		<%}%>
	<%}%>
<table class="table" ID="WorkOrderActivities" cellspacing="0" width="100%">
  	<%String className="oddrow";%>
    <thead>
        <tr>
            <td sortable="no" class="checkboxheader">
				<input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
			</td>
			<td class="tablecolumnheader"><yfc:i18n>Location_Details</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Inv_Upd_Activity</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Seq_#</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Work_Order_Activity_Code</yfc:i18n></td>
        <% if(modifyView != "") {%>
			<td class="tablecolumnheader"><yfc:i18n>Work_Order_Is_Complete</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Work_Order_Quantity_Completed</yfc:i18n></td>
		<%}%>
			<!--<td class="tablecolumnheader"><yfc:i18n>Instructions</yfc:i18n></td>-->
        </tr>
    </thead>
    <tbody>
        <% if(modifyView != "") {
			if(root != null){
				String [] activityAttributes = {"ActivitySeqNo"};
				YFCElement workOrderActivities = root.getChildElement("WorkOrderActivities");
				workOrderActivities.sortNumericChildren(activityAttributes);
			}
			%>
			<Input type="hidden" name="xml:/WorkOrder/@InvUpdateActivityCode" value="<%=resolveValue("xml:/WorkOrder/@InvUpdateActivityCode")%>"/>
			<yfc:loopXML binding="xml:/WorkOrder/WorkOrderActivities/@WorkOrderActivity" id="WorkOrderActivity">
				<%
				String binding="xml:/WorkOrder/DeletedActivity/@Activity_"+resolveValue("xml:/WorkOrderActivity/@WorkOrderActivityKey");
				if(!(equals("Y",resolveValue(binding)))){%>
				<yfc:makeXMLInput name="WorkOrderActivityKey" >
					<yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderKey" value="xml:/WorkOrder/@WorkOrderKey" />
					<yfc:makeXMLKey binding="xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity/@WorkOrderActivityKey" value="xml:/WorkOrderActivity/@WorkOrderActivityKey" />
				</yfc:makeXMLInput>
				<%tfootCounter++;%>
				<%
					if (equals("oddrow",className))
							className="evenrow";
						else
							className="oddrow";
				%>
				<tr class='<%=className%>'>
					<td class="checkboxcolumn" >
						<input type="checkbox" value='<%=getParameter("WorkOrderActivityKey")%>' name="chkModifyActivityKey" yfcMultiSelectCounter='<%=WorkOrderActivityCounter%>' yfcMultiSelectValue1='<%=resolveValue("xml:/WorkOrderActivity/@WorkOrderActivityKey")%>'/>
					</td>
					<td>
						<img onclick="expandCollapseDetails('optionSet_<%=WorkOrderActivityCounter%>','<%=getI18N("Click_To_See_Location_Info")%>','<%=getI18N("Click_To_Hide_Location_Info")%>','<%=YFSUIBackendConsts.FOLDER_COLLAPSE%>','<%=YFSUIBackendConsts.FOLDER_EXPAND%>')"  style="cursor:hand" <%=getImageOptions(YFSUIBackendConsts.FOLDER,"Click_To_See_Location_Info")%> />
					</td>
					<td>
						<%if(equals(resolveValue("xml:/WorkOrder/@InvUpdateActivityCode"), resolveValue("xml:/WorkOrderActivity/@ActivityCode"))){
							if(equals("Y",resolveValue("xml:/WorkOrder/@WorkOrderNotCompleted"))){%>
								<input type="radio" name="InvUpdateActivityCode" value="false" onclick="setInventoryUpdateActivity('<%=resolveValue("xml:/WorkOrderActivity/@ActivityCode")%>')" checked="true" />
							<%}else{%>
								<input type="radio" name="InvUpdateActivityCode" value="false" onclick="setInventoryUpdateActivity('<%=resolveValue("xml:/WorkOrderActivity/@ActivityCode")%>')" checked="true" DISABLED/>
							<%}
						}else{
							if(equals("Y",resolveValue("xml:/WorkOrder/@WorkOrderNotCompleted"))){%>
								<input type="radio" name="InvUpdateActivityCode" value="false" onclick="setInventoryUpdateActivity('<%=resolveValue("xml:/WorkOrderActivity/@ActivityCode")%>')"/>
							<%}else{%>
								<input type="radio" name="InvUpdateActivityCode" value="false" onclick="setInventoryUpdateActivity('<%=resolveValue("xml:/WorkOrderActivity/@ActivityCode")%>')" DISABLED/>		
							<%}
						}%>
					</td>
					<td class="tablecolumn">
						<yfc:getXMLValue binding="xml:/WorkOrderActivity/@ActivitySeqNo"/>             
					</td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrderActivity/@ActivityCode"/></td>
					<%
						if(modifyView != "") {
					%>
							<td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrderActivity/@IsComplete"/></td>
							<td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrderActivity/@QuantityComplete"/></td>
					<%	}
					request.setAttribute("CurrentInnerPanelID","I09");%>
					<tr id='optionSet_<%=WorkOrderActivityCounter%>' class='<%=className%>' style="display:none">
						<td colspan="7" >
							<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
								<jsp:param name="CurrentInnerPanelID" value="I09"/>
								<jsp:param name="optionSetBelongingToLine" value='<%=String.valueOf(WorkOrderActivityCounter)%>'/>
							</jsp:include>
						</td>
					</tr>
				</tr>
				<Input type="hidden" name="xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity_<%=WorkOrderActivityCounter%>/@ActivitySeqNo" value="<%=resolveValue("xml:/WorkOrderActivity/@ActivitySeqNo")%>"/>
				<Input type="hidden" name="xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity_<%=WorkOrderActivityCounter%>/@ActivityCode" value="<%=resolveValue("xml:/WorkOrderActivity/@ActivityCode")%>"/>
				<%}else{%>
				<input type="hidden" 	<%=getTextOptions(binding,resolveValue(binding))%> />	
				<%}%>
			</yfc:loopXML>
		<%}else{
			%>
			<Input type="hidden" name="xml:/WorkOrder/@InvUpdateActivityCode" value="<%=resolveValue("xml:/WorkOrder/@InvUpdateActivityCode")%>"/>
			<yfc:loopXML binding="xml:WorkOrderServiceActivityList:/ServiceActivityList/@ServiceActivity" id="ServiceActivity">
				<%
				String binding="xml:/WorkOrder/DeletedActivity/@Activity_"+resolveValue("xml:/ServiceActivity/@ServiceActivityKey");
				if(!(equals("Y",resolveValue(binding)))){%>
					<yfc:makeXMLInput name="ServiceActivityKey" >
						<yfc:makeXMLKey binding="xml:WorkOrderServiceActivityList:/ServiceActivityList/ServiceActivity/@ServiceActivityKey" value="xml:/ServiceActivity/@ServiceActivityKey" />
					</yfc:makeXMLInput>
					<%tfootCounter++;%>
					<%
						if (equals("oddrow",className))
								className="evenrow";
							else
								className="oddrow";
					%>
					<tr class='<%=className%>'>
						<td class="checkboxcolumn" >
							<input type="checkbox" value='<%=getParameter("ServiceActivityKey")%>' name="chkCreateActivityKey" yfcMultiSelectCounter='<%=ServiceActivityCounter%>' yfcMultiSelectValue1='<%=resolveValue("xml:/ServiceActivity/@ServiceActivityKey")%>'/>
						</td>
						<td>
							<img onclick="expandCollapseDetails('optionSet_<%=ServiceActivityCounter%>','<%=getI18N("Click_To_See_Location_Info")%>','<%=getI18N("Click_To_Hide_Location_Info")%>','<%=YFSUIBackendConsts.FOLDER_COLLAPSE%>','<%=YFSUIBackendConsts.FOLDER_EXPAND%>')"  style="cursor:hand" <%=getImageOptions(YFSUIBackendConsts.FOLDER,"Click_To_See_Location_Info")%> />
						</td>
						<td>
							<%if(equals(resolveValue("xml:/WorkOrder/@InvUpdateActivityCode"), resolveValue("xml:/ServiceActivity/@ActivityCode"))){%>
								<input type="radio" name="InvUpdateActivityCode" value="false" onclick="setInventoryUpdateActivity('<%=resolveValue("xml:/ServiceActivity/@ActivityCode")%>')" checked="true"/>
							<%}else{%>
								<input type="radio" name="InvUpdateActivityCode" value="false" onclick="setInventoryUpdateActivity('<%=resolveValue("xml:/ServiceActivity/@ActivityCode")%>')" checked="true"/>
								<%if(equals(resolveValue("xml:/WorkOrder/@InvUpdateActivityCode"),"")){%>
									<script>
										setInventoryUpdateActivity('<%=resolveValue("xml:/ServiceActivity/@ActivityCode")%>');
									</script>
								<%}%>
							<%}%>
						</td>

						<td class="tablecolumn"><yfc:getXMLValue binding="xml:/ServiceActivity/@ActivitySeqNo"/></td>
						<td class="tablecolumn"><yfc:getXMLValue binding="xml:/ServiceActivity/@ActivityCode"/></td>
						<tr id='<%="optionSet_"+ServiceActivityCounter%>' class='<%=className%>' style="display:none">
							<td colspan="7" >
								<jsp:include page="/vas/workorder/detail/work_order_activityLocation.jsp" flush="true">
									<jsp:param name="optionSetBelongingToLine" value='<%=String.valueOf(ServiceActivityCounter)%>'/>
								</jsp:include>
							</td>
						</tr>
					</tr>
					<Input type="hidden" name="xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity_<%=ServiceActivityCounter%>/@ActivitySeqNo" value="<%=resolveValue("xml:/ServiceActivity/@ActivitySeqNo")%>"/>
					<Input type="hidden" name="xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity_<%=ServiceActivityCounter%>/@ActivityCode" value="<%=resolveValue("xml:/ServiceActivity/@ActivityCode")%>"/>
					<yfc:loopXML binding="xml:/ServiceActivity/ServiceActivityInstructions/@ServiceActivityInstruction" id="ServiceActivityInstruction">
						<Input type="hidden" name="xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity_<%=ServiceActivityCounter%>/WorkOrderActivityInstructions/Instruction_<%=ServiceActivityInstructionCounter%>/@InstructionText" value="<%=resolveValue("xml:/ServiceActivityInstruction/@InstructionText")%>"/>
						<Input type="hidden" name="xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity_<%=ServiceActivityCounter%>/WorkOrderActivityInstructions/Instruction_<%=ServiceActivityInstructionCounter%>/@InstructionType" value="<%=resolveValue("xml:/ServiceActivityInstruction/@InstructionType")%>"/>
						<Input type="hidden" name="xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity_<%=ServiceActivityCounter%>/WorkOrderActivityInstructions/Instruction_<%=ServiceActivityInstructionCounter%>/@InstructionURL" value="<%=resolveValue("xml:/ServiceActivityInstruction/@InstructionURL")%>"/>
						<Input type="hidden" name="xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity_<%=ServiceActivityCounter%>/WorkOrderActivityInstructions/Instruction_<%=ServiceActivityInstructionCounter%>/@InstructionUsage" value="<%=resolveValue("xml:/ServiceActivityInstruction/@InstructionUsage")%>"/>
						<Input type="hidden" name="xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity_<%=ServiceActivityCounter%>/WorkOrderActivityInstructions/Instruction_<%=ServiceActivityInstructionCounter%>/@SequenceNo" value="<%=resolveValue("xml:/ServiceActivityInstruction/@SequenceNo")%>"/>
					</yfc:loopXML>

				<%}else{%>
				<input type="hidden" 	<%=getTextOptions(binding,resolveValue(binding))%> />	
				<%}%>

			</yfc:loopXML>
		<%}%>

	</tbody>
	<%if(equals("Y",resolveValue("xml:/WorkOrder/@WorkOrderNotCompleted")) || isVoid(resolveValue("xml:/WorkOrder/@WorkOrderKey"))){%>
    <tfoot>
		<tr style='display:none' TemplateRow="true">
			<td class="checkboxcolumn">&nbsp;</td>
			<td>
				<!--<img onclick="expandCollapseDetails('optionSet_<%=tfootCounter%>','<%=getI18N("Click_To_See_Loction_Info")%>','<%=getI18N("Click_To_Hide_Location_Info")%>','<%=YFSUIBackendConsts.FOLDER_COLLAPSE%>','<%=YFSUIBackendConsts.FOLDER_EXPAND%>')"  style="cursor:hand" <%=getImageOptions(YFSUIBackendConsts.FOLDER,"Click_To_See_Location_Info")%> />-->
			</td>
			<td>
				<!--<input type="radio" name="InvUpdateActivityCode" value="false" onclick="setInventoryUpdateActivity('<%=resolveValue("xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity_/@ActivityCode")%>')"/>-->
			</td>
			<td nowrap="true" class="tablecolumn">
				<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity_/@ActivitySeqNo")%>/>
			</td>
			<td class="searchcriteriacell" nowrap="true" >
				<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity_/@ActivityCode") %> />
				<img class="lookupicon" onclick="callLookup(this,'activity')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Activity") %> />

			</td>
		<% if(modifyView != "") {%>
			<td nowrap="true"/>
			<td nowrap="true"/>
		<%}%>
		</tr>
		<tr>
		<% if(modifyView != "") {%>
        	<td nowrap="true" colspan="7">
        		<jsp:include page="/common/editabletbl.jsp" flush="true">
        		</jsp:include>
        	</td>
		<%}else{%>
        	<td nowrap="true" colspan="5">
        		<jsp:include page="/common/editabletbl.jsp" flush="true">
       		</jsp:include>
        	</td>
		<%}%>
		</tr>
	</tfoot>
	<%}%>
</table>
<%
	 if(individualComponent != null){
		 if(individualComponent.toString() != ""){%>
</div>
		<%}%>
	<%}%>