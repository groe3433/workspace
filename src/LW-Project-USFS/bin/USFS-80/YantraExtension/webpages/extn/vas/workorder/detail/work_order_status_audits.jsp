<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<table class="table" width="100%">
<thead>
    <tr> 
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/WorkOrder/WorkOrderStatusAudits/WorkOrderStatusAudit/@CreateUserName")%>">
            <yfc:i18n>Modified_By</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/WorkOrder/WorkOrderStatusAudits/WorkOrderStatusAudit/@OldStatus")%>">
            <yfc:i18n>Old_Status</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/WorkOrder/WorkOrderStatusAudits/WorkOrderStatusAudit/@OldStatusDate")%>">
            <yfc:i18n>Old_Status_Date</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/WorkOrder/WorkOrderStatusAudits/WorkOrderStatusAudit/@NewStatus")%>">
            <yfc:i18n>New_Status</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/WorkOrder/WorkOrderStatusAudits/WorkOrderStatusAudit/@NewStatusDate")%>">
            <yfc:i18n>New_Status_Date</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/WorkOrder/WorkOrderStatusAudits/WorkOrderStatusAudit/@ReasonCode")%>">
            <yfc:i18n>Reason_Code</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/WorkOrder/WorkOrderStatusAudits/WorkOrderStatusAudit/@ReasonText")%>">
            <yfc:i18n>Reason_Text</yfc:i18n>
        </td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML name="WorkOrder" binding="xml:/WorkOrder/WorkOrderStatusAudits/@WorkOrderStatusAudit" id="WorkOrderStatusAudit" > 
		<tr>
			<td class="tablecolumn">
                <%
                    String sUser = resolveValue("xml:/WorkOrderStatusAudit/@CreateUserName");
                    if(isVoid(sUser) )
                        sUser = resolveValue("xml:/WorkOrderStatusAudit/@Createuserid");
                %>
                <%=sUser%>
            </td>       
			<td class="tablecolumn">
				<yfc:getXMLValueI18NDB name="WorkOrderStatusAudit" binding="xml:/WorkOrderStatusAudit/OldStatus/@Description"/>
			</td>
			<td class="tablecolumn">
				<yfc:getXMLValue name="WorkOrderStatusAudit" binding="xml:/WorkOrderStatusAudit/@OldStatusDate"/>
			</td>
			<td class="tablecolumn">
				<yfc:getXMLValueI18NDB name="WorkOrderStatusAudit" binding="xml:/WorkOrderStatusAudit/NewStatus/@Description"/>
			</td>
			<td class="tablecolumn">
				<yfc:getXMLValue name="WorkOrderStatusAudit" binding="xml:/WorkOrderStatusAudit/@NewStatusDate"/>
			</td>
			<td class="tablecolumn">
				<yfc:getXMLValue name="WorkOrderStatusAudit" binding="xml:/WorkOrderStatusAudit/@ReasonCode"/>
			</td>
			<td class="tablecolumn">
				<yfc:getXMLValue name="WorkOrderStatusAudit" binding="xml:/WorkOrderStatusAudit/@ReasonText"/>
			</td>
		</tr>
    </yfc:loopXML> 
</tbody>
</table>
