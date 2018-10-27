<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/taskmanagement.js"></script>

<%
    String sIsSummaryTask = resolveValue("xml:/Task/@IsSummaryTask");
	
	String summaryChecked="No";
	String detailChecked="No";
	String allChecked="No";

	if (equals(sIsSummaryTask,"Y")) { 
        summaryChecked = "Y";
    }
	else if (equals(sIsSummaryTask,"N")) { 
	    detailChecked = "N";
	}
	else {
		allChecked = " ";
	}
%>

<table class="view" >
	 <tr>
        <td>
            <input type="hidden" name="xml:/Task/@StartNoEarlierThanQryType" value="BETWEEN"/>
        </td>
    </tr>
	<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
		<jsp:param name="ShowDocumentType" value="false"/>
		<jsp:param name="ShowNode" value="true"/>
		<jsp:param name="RefreshOnNode" value="true"/>
		<jsp:param name="EnterpriseListForNodeField" value="true"/>
		<jsp:param name="RefreshOnEnterpriseCode" value="false"/>
		<jsp:param name="NodeBinding" value="xml:/Task/@Node"/>
		<jsp:param name="EnterpriseCodeBinding" value="xml:/Task/@EnterpriseKey"/>
	</jsp:include>
	<%if(!isVoid(resolveValue("xml:/Task/@Node") ) )	{	%>
		<yfc:callAPI apiID="AP3"/>
	<%}%>

	<td class="searchlabel" >
		<table>
			<tr>
				<td class="searchlabel"><yfc:i18n>Activity_Group</yfc:i18n></td>
				<td class="searchcriteriacell" nowrap="true">
					<select class="combobox" onchange="performActivityGroupSelection('TASK_TYPE_COMBO');" <%=getComboOptions("xml:/Task/TaskType/@ActivityGroupId")%>>
						<yfc:loopOptions binding="xml:/BaseActivityGroupList/@BaseActivityGroup" name="ActivityGroupName" isLocalized="Y" value="ActivityGroupId" selected="xml:/Task/TaskType/@ActivityGroupId"/>
					</select>
				</td>
			</tr>
			<tr>
				<td class="searchlabel"><yfc:i18n>Task_Type</yfc:i18n></td>
				<td class="searchcriteriacell" nowrap="true">
					<select id="TASK_TYPE_COMBO" name="xml:/Task/@TaskType" class="combobox">
						<% if (!isVoid(getValue("Task","xml:/Task/TaskType/@ActivityGroupId"))) { %>
							<yfc:loopOptions binding="xml:/TaskTypeList/@TaskType" name="TaskTypeName" isLocalized="Y" value="TaskType" selected="xml:/Task/@TaskType"/>
						<% } else {%>
							<option value="" selected=true/>
						<% } %>
					</select>
				</td>
			</tr>
			<tr>
				<td class="searchlabel" >
					<yfc:i18n>Task_Status</yfc:i18n>
				</td>
				<td class="searchcriteriacell" nowrap="true">
					<select name="xml:/Task/@TaskStatus" class="combobox">
						<yfc:loopOptions binding="xml:TaskStatusList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
						value="CodeValue" selected="xml:/Task/@TaskStatus" isLocalized="Y" />
					</select>
				</td>
			</tr>
		</table>
	</td>

	<tr>
        <td class="searchlabel" >
            <yfc:i18n>Start_Task_After</yfc:i18n>
        </td>
    </tr>
	<tr>
        <td nowrap="true">
			<%	YFCDate oDate = new YFCDate(); 
				oDate.setEndOfDay();
			%>
            <input class="dateinput" type="text" <%=getTextOptions("xml:/Task/@FromStartNoEarlierThan_YFCDATE")%>/>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Task/@FromStartNoEarlierThan_YFCTIME")%>/>
            <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
            <yfc:i18n>To</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td>
            <input class="dateinput" type="text" <%=getTextOptions("xml:/Task/@ToStartNoEarlierThan_YFCDATE", "xml:/Task/@ToStartNoEarlierThan_YFCDATE", oDate.getString(getLocale(), false))%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Task/@ToStartNoEarlierThan_YFCTIME", "xml:/Task/@ToStartNoEarlierThan_YFCTIME", oDate.getString(getLocale().getTimeFormat()))%>/>
            <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false"	<%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
        </td>
    </tr>

	<tr>
        <td class="searchlabel">
            <yfc:i18n>Show_Only</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell">
			<input type="radio" <%=getRadioOptions("xml:/Task/@IsSummaryTask",summaryChecked,"Y")%>><yfc:i18n>Summary_Tasks</yfc:i18n>
			<input type="radio" <%=getRadioOptions("xml:/Task/@IsSummaryTask",detailChecked,"N")%>><yfc:i18n>Detail_Tasks</yfc:i18n> 
			<input type="radio" <%=getRadioOptions("xml:/Task/@IsSummaryTask",allChecked," ")%>><yfc:i18n>All_Tasks</yfc:i18n> 
        </td>
    </tr>

	<tr>
		<td>
			<fieldset>
		        <legend><yfc:i18n>Reference_Parameters</yfc:i18n></legend>
				<table class="view" width="100%">
	<tr>
        <td class="searchlabel">
            <yfc:i18n>Batch_#</yfc:i18n>
        </td>
        <td nowrap="true" class="searchcriteriacell">
			<select name="xml:/Task/TaskReferences/@BatchNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Task/TaskReferences/@BatchNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Task/TaskReferences/@BatchNo")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel">
            <yfc:i18n>Wave_#</yfc:i18n>
        </td>
        <td nowrap="true" class="searchcriteriacell">
			<select name="xml:/Task/TaskReferences/@WaveNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Task/TaskReferences/@WaveNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Task/TaskReferences/@WaveNo")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Shipment_#</yfc:i18n>
        </td>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Task/TaskReferences/@ShipmentNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Task/TaskReferences/@ShipmentNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Task/TaskReferences/@ShipmentNo")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel">
            <yfc:i18n>Container_#</yfc:i18n>
        </td>
        <td nowrap="true" class="searchcriteriacell">
			<select name="xml:/Task/TaskReferences/@ContainerNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Task/TaskReferences/@ContainerNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Task/TaskReferences/@ContainerNo")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel">
            <yfc:i18n>Order_#</yfc:i18n>
        </td>
        <td nowrap="true" class="searchcriteriacell">
			<select name="xml:/Task/TaskReferences/@OrderNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Task/TaskReferences/@OrderNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Task/TaskReferences/@OrderNo")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel">
            <yfc:i18n>SCAC</yfc:i18n>
        </td>
        <td nowrap="true" class="searchcriteriacell">
			<select name="xml:/Task/TaskReferences/@ScacQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Task/TaskReferences/@ScacQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Task/TaskReferences/@Scac")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel">
            <yfc:i18n>Load_#</yfc:i18n>
        </td>
        <td nowrap="true" class="searchcriteriacell">
			<select name="xml:/Task/TaskReferences/@LoadNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Task/TaskReferences/@LoadNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Task/TaskReferences/@LoadNo")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel">
            <yfc:i18n>Move_Request_#</yfc:i18n>
        </td>
        <td nowrap="true" class="searchcriteriacell">
			<select name="xml:/Task/TaskReferences/@MoveRequestNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Task/TaskReferences/@MoveRequestNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Task/TaskReferences/@MoveRequestNo")%>/>
			<%//If passed populate move request line key.  This is used when linked from move request detail view. %>
            <input type="hidden" <%=getTextOptions("xml:/Task/TaskReferences/@MoveRequestLineKey")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel">
            <yfc:i18n>Count_Request_#</yfc:i18n>
        </td>
        <td nowrap="true" class="searchcriteriacell">
			<select name="xml:/Task/TaskReferences/@CountRequestNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Task/TaskReferences/@CountRequestNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Task/TaskReferences/@CountRequestNo")%>/>
        </td>
    </tr>
    <tr>
		<td class="searchlabel">
            <yfc:i18n>Work_Order_#</yfc:i18n>
        </td>
        <td nowrap="true" class="searchcriteriacell">
			<select name="xml:/Task/TaskReferences/@WorkOrderNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Task/TaskReferences/@WorkOrderNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Task/TaskReferences/@WorkOrderNo")%>/>
			<%//If passed populate move request line key.  This is used when linked from work order detail view. %>
            <input type="hidden"  <%=getTextOptions("xml:/Task/TaskReferences/@WorkOrderKey")%>/>
        </td>
    </tr>
				</table>
			</fieldset>
		</td>
	</tr>
	<input name="xml:/Task/DataAccessFilter/@ApplyDataSecurityFilters"  type="hidden" value="Y"/>
</table>