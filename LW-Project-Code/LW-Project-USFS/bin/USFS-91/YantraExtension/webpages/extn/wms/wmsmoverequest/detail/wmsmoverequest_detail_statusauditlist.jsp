<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<yfc:callAPI apiID="AP1"/>
<table class="table">
<thead>
    <tr> 		  
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/MoveRequestStatusAudit/@OldStatus")%>">
            <yfc:i18n>Old_Status</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/MoveRequestStatusAudit/@OldStatusDate")%>">
			<yfc:i18n>Old_Status_Date</yfc:i18n>
		</td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/MoveRequestStatusAudit/@NewStatus")%>">
            <yfc:i18n>New_Status</yfc:i18n>
        </td>        
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/MoveRequestStatusAudit/@NewStatusDate")%>">
            <yfc:i18n>New_Status_Date</yfc:i18n>
        </td>  
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/MoveRequestStatusAudits/@MoveRequestStatusAudit" id="MoveRequestStatusAudit"> 
    <tr>		
        <td width="20%" class="tablecolumn">
            <yfc:getXMLValueI18NDB name="MoveRequestStatusAudit" binding="xml:/MoveRequestStatusAudit/OldStatus/@Description"/>
        </td>
		<td width="20%" class="tablecolumn">
            <yfc:getXMLValue name="MoveRequestStatusAudit" binding="xml:/MoveRequestStatusAudit/@OldStatusDate"/>
        </td>
        <td width="20%" class="tablecolumn">
            <yfc:getXMLValueI18NDB name="MoveRequestStatusAudit" binding="xml:/MoveRequestStatusAudit/NewStatus/@Description"/>
        </td>
        <td width="20%" class="tablecolumn">
            <yfc:getXMLValue name="MoveRequestStatusAudit" binding="xml:/MoveRequestStatusAudit/@NewStatusDate"/>
        </td>		
    </tr>
    </yfc:loopXML> 
</tbody>
</table>