<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf"%>
<%@include file="/console/jsp/primarytaskreference.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*"%>
<%@ include file="/console/jsp/modificationutils.jspf"%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/taskmanagement.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/exceptionutils.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<%
	YFCElement taskList = (YFCElement) request.getAttribute("TaskList");
	String strShowHierarchy = "N";
	if (taskList != null) {
		strShowHierarchy = taskList.getAttribute("ShowHierarchy");
	}
%>
		<tr>
			<td><input type="hidden" name="xml:/Task/@StartNoEarlierThanQryType" value="BETWEEN" /></td>
		</tr>
	<table class="table" editable="false" width=100% SuppressRowColoring="true">
		<thead>
			<tr>
				<td sortable="no" class="checkboxheader">
					<input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>' /> 
					<input type="hidden" name="xml:/Task/@Override" value="N" /> 
					<input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);" />
				</td>
				<td class="tablecolumnheader" nowrap="true"><yfc:i18n>Task_ID</yfc:i18n></td>
				<td class="tablecolumnheader" nowrap="true"><yfc:i18n>Task_Type</yfc:i18n></td>
				<td class="tablecolumnheader" nowrap="true"><yfc:i18n>Priority</yfc:i18n></td>
				
				<!-- Begin CR476 ML -->
				<td class="tablecolumnheader" nowrap="true"><yfc:i18n>Date_Created</yfc:i18n></td>
				<!-- End CR476 ML -->
				
				<td class="tablecolumnheader" nowrap="true"><yfc:i18n>Item_ID</yfc:i18n></td>
				
				<!-- BEGIN - CR 825 - Jan 22, 2013 -->
				<td class="tablecolumnheader" nowrap="true"><yfc:i18n>Item_Description</yfc:i18n></td>				
				<!-- END - CR 825 - Jan 22, 2013 -->
				
				<td class="tablecolumnheader" nowrap="true"><yfc:i18n>Quantity</yfc:i18n></td>
<%
				YFCElement elemClassificationPurpose = null;
				if ((!isVoid(resolveValue("xml:/Task/@EnterpriseKey"))) && (equals("COUNT", resolveValue("xml:/Task/TaskType/@ActivityGroupId")))) {
%>
				<yfc:callAPI apiID="AP2" />
<%
					elemClassificationPurpose = (YFCElement) request.getAttribute("ClassificationPurpose");
					if ((!isVoid(elemClassificationPurpose)) && elemClassificationPurpose.hasChildNodes()) {
						String root[] = new String[1];
						root[0] = "ClassificationPurposeCode";
						elemClassificationPurpose.sortChildren(root, true);
						for (Iterator j = elemClassificationPurpose.getChildren(); j.hasNext();) {
							YFCElement childElem = (YFCElement) j.next();
							if (!isVoid(childElem.getAttribute("ClassificationPurposeCode"))) {
%>
				<td class="tablecolumnheader"><yfc:i18n>
					<%=childElem.getAttribute("AttributeName")%>
					</yfc:i18n>
				</td>
<%
							}
						}
					}
				}
%>
				<td class="tablecolumnheader" nowrap="true"><yfc:i18n>Task_Status</yfc:i18n></td>
				<td class="tablecolumnheader" nowrap="true"><yfc:i18n>Source_Location</yfc:i18n></td>
				<td class="tablecolumnheader" nowrap="true"><yfc:i18n>Target_Location</yfc:i18n></td>
				<td class="tablecolumnheader" nowrap="true"><yfc:i18n>Primary_Reference</yfc:i18n></td>
				<td class="tablecolumnheader" nowrap="true"><yfc:i18n>Predecessor_Task</yfc:i18n></td>
				<td class="tablecolumnheader" nowrap="true"><yfc:i18n>Assigned_To_User</yfc:i18n></td>
			</tr>
		</thead>
		<tbody>
			<yfc:loopXML binding="xml:/TaskList/@Task" id="Task">
<%
			boolean childTaskReturned = false;
			long level = 0;
			if (equals(strShowHierarchy, "Y")) {
				String strChildTaskReturned = getValue("Task", "xml:/Task/@ChildTaskReturned");
				if (equals(strChildTaskReturned, "Y")) {
					childTaskReturned = true;
				}
				String strLevel = getValue("Task", "xml:/Task/@Level");
				if (!isVoid(strLevel)) {
					level = Long.parseLong(strLevel);
				}
			}
			String primaryTaskRef = "";
			String docType = getValue("Task", "xml:/Task/TaskReferences/@DocumentType");
			String refName = getValue("Task", "xml:/Task/TaskType/@PrimaryTaskReference");
			String refValue = "";
			if (!isVoid(refName)) {
				refValue = getValue("Task", "xml:/Task/TaskReferences/@" + refName);
				String localizedRefName = "PrimaryTaskReference_" + refName;
				primaryTaskRef = getFormatedI18N(localizedRefName, refValue);
			}
%>
			<tr>
				<yfc:makeXMLInput name="taskKey">
				<yfc:makeXMLKey binding="xml:/Task/@TaskKey" value="xml:/Task/@TaskKey" />
				<yfc:makeXMLKey binding="xml:/Task/@Node" value="xml:/Task/@OrganizationCode" />
				</yfc:makeXMLInput>
				<td class="checkboxcolumn"><input type="checkbox"
					value='<%=getParameter("taskKey")%>' name="EntityKey"
					CountRequestKey='<%=resolveValue("xml:/Task/TaskReferences/@CountRequestKey")%>'
					TaskKey='<%=resolveValue("xml:/Task/@TaskKey")%>'
					IsSummaryTask='<%=getValue("Task", "xml:/Task/@IsSummaryTask")%>'
					Node='<%=getValue("Task", "xml:/Task/@OrganizationCode")%>' />
				</td>
				<td class="tablecolumn" 
<%
					if (childTaskReturned) {
%>
						style="font-weight:bold" 
<%
					}
%>					>
<%
					for (int i = 1; i <= level; i++) {
%>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
<%
					}
%> 					<a href="javascript:showDetailForOnAdvancedList('task','YWMD120','<%=getParameter("taskKey")%>');">
					<yfc:getXMLValue binding="xml:/Task/@TaskId" /> </a> 
<%
 					if (!(equals(strShowHierarchy, "Y")) && (equals("Y", getValue("Task", "xml:/Task/@IsSummaryTask")))) {
%>
						<img class="icon" <%=getImageOptions(YFSUIBackendConsts.SUMMARY_TASK_ICON, "Summary_Task")%> />
<%
					} else if (!(equals(strShowHierarchy, "Y")) && (equals("Y", getValue("Task", "xml:/Task/@IsParent")))) {
%>
						<img class="icon" <%=getImageOptions(YFSUIBackendConsts.KIT_COMPONENTS_COLUMN, "Parent_Task")%> />
<%
					}
%>
				</td>
				<td class="tablecolumn" 
<%
					if (childTaskReturned) {
%>
						style="font-weight:bold" 
<%
					}
%>					
					><yfc:getXMLValueI18NDB binding="xml:/Task/TaskType/@TaskTypeName" />&nbsp;
				</td>
				<td class="tablecolumn" 
<%
					if (childTaskReturned) {
%>		
						style="font-weight:bold" 
<%
					}
%>					><!-- 	<yfc:getXMLValue binding="xml:/Task/@TaskPriority"/>&nbsp; -->
					<%=getComboText("xml:Priority:/CommonCodeList/@CommonCode", "CodeShortDescription", "CodeValue", "xml:/Task/@TaskPriority", true)%>
				</td>
		
				<!-- Begin CR 476 ML -->
				<td class="tablecolumn" 
<%
					if (childTaskReturned) {
%>
						style="font-weight:bold" 
<%
					}
%>
					><yfc:getXMLValue binding="xml:/Task/@Createts" />&nbsp;
				</td>
				<!-- End CR 476 ML -->
				
				<td class="tablecolumn" 
<%
					if (childTaskReturned) {
%>	
						style="font-weight:bold" 
<%
					}
%>		
					><yfc:getXMLValue binding="xml:/Task/Inventory/@ItemId" />&nbsp;
				</td>
				
				<!-- BEGIN - CR 825 - Jan 22, 2013 -->
				<td class="tablecolumn" 
<%
					if (childTaskReturned) {
%>	
						style="font-weight:bold" 
<%
					}
%>		
					><yfc:getXMLValue binding="xml:/Task/Inventory/Item/PrimaryInformation/@ShortDescription" />&nbsp;
				</td>
				<!-- END - CR 825 - Jan 22, 2013 -->
								
<%
				if (getNumericValue("xml:/Task/Inventory/@Quantity") == 0) {
%>
				<td class="numerictablecolumn" 
<%
					if (childTaskReturned) {
%>
						style="font-weight:bold" 
<%
					}
%>					
					>&nbsp;
				</td>
<%
				} else {
%>
				<td class="numerictablecolumn" 
<%
					if (childTaskReturned) {
%>
						style="font-weight:bold" 
<%
					}
%>			
					><yfc:getXMLValue binding="xml:/Task/Inventory/@Quantity" />&nbsp;
				</td>
<%
				}
				if (!isVoid(elemClassificationPurpose)) {
					int iCounter = 1;
					for (Iterator j = elemClassificationPurpose.getChildren(); j.hasNext();) {
						YFCElement childElem = (YFCElement) j.next();
						if (!isVoid(childElem.getAttribute("ClassificationPurposeCode"))) {
							String sAttrBinding = "xml:/Task/@ItemClassification" + String.valueOf(iCounter);
							++iCounter;
%>
				<td class="tablecolumn" 
<%
					if (childTaskReturned) {
%>
						style="font-weight:bold" 
<%
					}
%>					><%=resolveValue(sAttrBinding)%>&nbsp;
				</td>
<%
						}
					}
				}
%>
				<td class="tablecolumn" 
<%
					if (childTaskReturned) {
%>
						style="font-weight:bold" 
<%
					}
%>					><yfc:getXMLValueI18NDB binding="xml:/Task/@TaskStatusDesc" />&nbsp;
				</td>
				<td class="tablecolumn" 
<%
					if (childTaskReturned) {
%>
						style="font-weight:bold" 
<%
					}
%>					><yfc:getXMLValue binding="xml:/Task/@SourceLocationId" />&nbsp;
				</td>
				<td class="tablecolumn" 
<%			
					if (childTaskReturned) {
%>
						style="font-weight:bold" 
<%
					}
%>					><yfc:getXMLValue binding="xml:/Task/@TargetLocationId" />&nbsp;
				</td>
				<td class="tablecolumn" 
<%
					if (childTaskReturned) {
%>
						style="font-weight:bold" 
<%
					}
%>
					>
<%
					if (linkPrimaryReference(refName, docType)) {
%> 						
						<yfc:makeXMLInput name="PrimaryTaskReferenceKey">
						<yfc:makeXMLKey binding="<%=getEntityKeyBinding(refName)%>" value="<%=getEntityKeyValueBinding(refName)%>" />
						</yfc:makeXMLInput> <a href="" onClick="yfcShowDetailPopup('','','900','550',window.dialogArguments,'<%=getEntityIDForPrimaryReference(refName,docType)%>','<%=getParameter("PrimaryTaskReferenceKey")%>');return false;"><%=primaryTaskRef%></a>
<%
					} else {
%> 
						<%=primaryTaskRef%>&nbsp; 
<%
					}
%>
				</td>
				<yfc:makeXMLInput name="predecessorTaskId">
					<yfc:makeXMLKey binding="xml:/Task/@TaskId" value="xml:/Task/@PredecessorTaskId" />
				</yfc:makeXMLInput>
				<td class="tablecolumn" 
<%					
					if (childTaskReturned) {
%>
						style="font-weight:bold" 
<%
					}
%>		
					>
<%
					if (!equals(getValue("Task", "xml:/Task/@PredecessorTaskId"), "")) {
%>
						<a href="javascript:showDetailForOnAdvancedList('task','YWMD120', '<%=getParameter("predecessorTaskId")%>');">
						<yfc:getXMLValue binding="xml:/Task/@PredecessorTaskId" /> </a> 
<%
					} else {
%>
						&nbsp; 
<%
 					}
%>
				</td>
				<yfc:makeXMLInput name="userId">
					<yfc:makeXMLKey binding="xml:/User/@UserId" value="xml:/Task/@AssignedToUserId" />
				</yfc:makeXMLInput>
				<td class="tablecolumn" 
<%					
					if (childTaskReturned) {
%>
						style="font-weight:bold" 
<%
					}
%>		
					>
<%
					if (!equals(getValue("Task", "xml:/Task/@AssignedToUserId"), "")) {
%>
						<a href="javascript:showDetailForOnAdvancedList('task','YWMD300', '<%=getParameter("userId")%>');">
						<yfc:getXMLValue binding="xml:/Task/@AssignedToUserId" /> </a> 
<%
 					} else {
%>
						&nbsp; 
<%
 					}
%>
				</td>
			</tr>
			</yfc:loopXML>
		</tbody>
	</table>