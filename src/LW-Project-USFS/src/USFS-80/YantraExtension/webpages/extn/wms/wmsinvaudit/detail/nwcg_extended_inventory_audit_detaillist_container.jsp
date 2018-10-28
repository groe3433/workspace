<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@page import="com.yantra.yfs.ui.backend.*" %>
<%@include file="/console/jsp/modificationutils.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<%if(equals("Y",request.getParameter("DivRequired"))){%>
<div style="height:250px;overflow:auto">
<%}%>
<table class="table" editable="false" width="100%" cellspacing="0">
<thead>
    <tr> 
        <td class="checkboxheader" sortable="no">
            <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td>	
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:LocationInventoryAudit:/LocationInventoryAudits/LocationInventoryAudit/@Createts")%>">
            <yfc:i18n>Activity_Date</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:LocationInventoryAudit:/LocationInventoryAudit/@EnterpriseCode")%>">
            <yfc:i18n>Enterprise</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:LocationInventoryAudit:/LocationInventoryAudit/@PalletId")%>">
            <yfc:i18n>Pallet_ID</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:LocationInventoryAudit:/LocationInventoryAudit/@CaseId")%>">
            <yfc:i18n>Case_ID</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:LocationInventoryAudit:/LocationInventoryAudit/@LocationId")%>">
            <yfc:i18n>Location</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:LocationInventoryAudit:/LocationInventoryAudit/@PalletId")%>">
            <yfc:i18n>Parent_Pallet_ID</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:LocationInventoryAudit:/LocationInventoryAudit/@CaseId")%>">
            <yfc:i18n>Parent_Case_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="">
            <yfc:i18n>Adjustment_Type</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="">
            <yfc:i18n>Reason_Code</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="">
            <yfc:i18n>User_ID</yfc:i18n>
        </td>
		 <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:LocationInventoryAudit:/LocationInventoryAudit/@AuditOperation")%>">
            <yfc:i18n>Audit_Type</yfc:i18n>
        </td>
		
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:LocationInventoryAudit:/LocationInventoryAudits/@LocationInventoryAudit" id="LocationInventoryAudit"> 
	<%if(isVoid(resolveValue("xml:LocationInventoryAudit:/LocationInventoryAudit/InventoryItem/@ItemID"))){%>  
	<tr> 
        <yfc:makeXMLInput name="locnInventoryAuditKey">
            <yfc:makeXMLKey binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@LocnInventoryAuditKey" value="xml:LocationInventoryAudit:/LocationInventoryAudit/@LocnInventoryAuditKey" />
          </yfc:makeXMLInput>
        <td class="checkboxcolumn">
			<input type="checkbox"  name="cLocEntityKey" value='<%=getParameter("locnInventoryAuditKey")%>' />
        </td>
        <td class="tablecolumn" nowrap="true" sortValue="<%=getDateValue("xml:LocationInventoryAudit:/LocationInventoryAudit/@Createts")%>">
		<a <%=getDetailHrefOptions("L01", getParameter("locnInventoryAuditKey"),"")%> >
			  <yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@Createts"/>
		 </a>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@EnterpriseCode"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@PalletId"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@CaseId"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@LocationId"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@OuterMostPalletId"/>
		</td>
		<td class="tablecolumn">
            <yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@OuterMostCaseId"/>
        </td>
		<td class="tablecolumn">
			<yfc:i18n> 
				<yfc:getXMLValue  binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@AdjustmentType"/> 
			</yfc:i18n>
		</td>
		<td class="tablecolumn">
            <yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@ReasonCode"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@Modifyuserid"/>
        </td>
		<td class="tablecolumn">
    		<%if(equals("+",resolveValue("xml:/LocationInventoryAudit/@AuditOperation"))){%>
		    <yfc:i18n>In</yfc:i18n>
			<%}else{%>
			<yfc:i18n>Out</yfc:i18n>
			<%}%>
        </td>
    </tr>
	<%}%>
    </yfc:loopXML> 
</tbody>
</table>
<%if(equals("Y",request.getParameter("DivRequired"))){%>
</div>
<%}%>
