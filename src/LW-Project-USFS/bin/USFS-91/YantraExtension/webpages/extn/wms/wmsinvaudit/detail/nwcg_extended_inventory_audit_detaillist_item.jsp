<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@page import="com.yantra.yfs.ui.backend.*" %>
<%@include file="/console/jsp/modificationutils.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<%if(equals("Y",request.getParameter("DivRequired"))){%>
<div style="height:250px;overflow:auto">
<%}%>
<table class="table" editable="false" width="100%" cellspacing="0">
<%
	boolean bylpn = false;
	String serachcriteria = (String) request.getAttribute("SearchCriteriaIs");
	if(serachcriteria!=null)
	{
	if (equals("ByLPN",serachcriteria)) {
		bylpn = true;
	}
	}
%>
<thead>
    <tr> 
        <td class="checkboxheader" sortable="no">
            <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td>	
        <td class="tablecolumnheader">
            <yfc:i18n>Activity_Date</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:LocationInventoryAudit:/LocationInventoryAudit/@LocationId")%>">
            <yfc:i18n>Location</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:LocationInventoryAudit:/LocationInventoryAudit/@EnterpriseCode")%>">
            <yfc:i18n>Enterprise</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="">
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:LocationInventoryAudit:/LocationInventoryAudit/InventoryItem/@ProductClass")%>">
            <yfc:i18n>PC</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:LocationInventoryAudit:/LocationInventoryAudit/InventoryItem/@UnitOfMeasure")%>">
            <yfc:i18n>UOM</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="">
            <yfc:i18n>Description</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:LocationInventoryAudit:/LocationInventoryAudit/@InventoryStatus")%>">
            <yfc:i18n>Status</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="">
            <yfc:i18n>Adjustment_Type</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="">
            <yfc:i18n>Task_Type</yfc:i18n>
        </td>
         <td class="tablecolumnheader" style="">
            <yfc:i18n>Order_Number</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="">
            <yfc:i18n>Release_Number</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="">
            <yfc:i18n>Shipment_Number</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="">
            <yfc:i18n>Receipt_Number</yfc:i18n>
        </td>
         <td class="tablecolumnheader" style="">
            <yfc:i18n>Trackable_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="">
            <yfc:i18n>Reason_Code</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="">
            <yfc:i18n>User_ID</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:LocationInventoryAudit:/LocationInventoryAudit/@Quantity")%>">
            <yfc:i18n>Quantity</yfc:i18n>
        </td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:LocationInventoryAudit:/LocationInventoryAudits/@LocationInventoryAudit" id="LocationInventoryAudit"> 
<%   if (bylpn) {
		if((!(isVoid(resolveValue("xml:LocationInventoryAudit:/LocationInventoryAudit/InventoryItem/@ItemID")))) && (!(isVoid(resolveValue("xml:LocationInventoryAudit:/LocationInventoryAudit/@PalletId"))) || !(isVoid(resolveValue("xml:LocationInventoryAudit:/LocationInventoryAudit/@CaseId"))))){  %>
			<tr> 
				<yfc:makeXMLInput name="locnInventoryAuditKey">
					<yfc:makeXMLKey binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@LocnInventoryAuditKey" value="xml:LocationInventoryAudit:/LocationInventoryAudit/@LocnInventoryAuditKey" />
				  </yfc:makeXMLInput>
				<td class="checkboxcolumn">
					<input type="checkbox"  name="LocEntityKey" value='<%=getParameter("locnInventoryAuditKey")%>' />
				</td>
				<td class="tablecolumn" sortValue="<%=getDateValue("xml:LocationInventoryAudit:/LocationInventoryAudit/@Createts")%>">
				<a <%=getDetailHrefOptions("L01", getParameter("locnInventoryAuditKey"),"")%> >
				  <yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@Createts"/>
				 </a>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@LocationId"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@EnterpriseCode"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/InventoryItem/@ItemID"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/InventoryItem/@ProductClass"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/InventoryItem/@UnitOfMeasure"/>
				</td>
                <td class="tablecolumn">
					<yfc:getXMLValue name="LocationInventoryAudit" 
					binding="xml:LocationInventoryAudit:/LocationInventoryAudit/InventoryItem/Item/PrimaryInformation/@Description" />
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@InventoryStatus"/>
				</td>
				<td class="tablecolumn">
					<yfc:i18n> 
						<yfc:getXMLValue  binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@AdjustmentType"/> 
					</yfc:i18n>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@TaskType"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@OrderNo"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@ReleaseNo"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@ShipmentNo"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/Receipt/@ReceiptNo"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@SerialNo"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@ReasonCode"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@Modifyuserid"/>
				</td>
				<td class="numerictablecolumn" sortValue="
					<%if (equals("+",resolveValue("xml:LocationInventoryAudit:/LocationInventoryAudit/@AuditOperation"))){%>
					+<%}else{%>
					-<%}%>
					<%=getNumericValue("xml:LocationInventoryAudit:/LocationInventoryAudit/@Quantity")%>">
					<yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@Quantity"/>
					<%if (equals("+",resolveValue("xml:LocationInventoryAudit:/LocationInventoryAudit/@AuditOperation"))){%><yfc:i18n>+</yfc:i18n>
					<%}else{%><yfc:i18n>-</yfc:i18n>
					<%}%>
				</td>
			</tr>
		<% }
		} else {
		if(!(isVoid(resolveValue("xml:LocationInventoryAudit:/LocationInventoryAudit/InventoryItem/@ItemID")))){%>  

    <tr> 
        <yfc:makeXMLInput name="locnInventoryAuditKey">
            <yfc:makeXMLKey binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@LocnInventoryAuditKey" value="xml:LocationInventoryAudit:/LocationInventoryAudit/@LocnInventoryAuditKey" />
          </yfc:makeXMLInput>
        <td class="checkboxcolumn">
			<input type="checkbox"  name="LocEntityKey" value='<%=getParameter("locnInventoryAuditKey")%>' />
		</td>
        <td class="tablecolumn" sortValue="<%=getDateValue("xml:LocationInventoryAudit:/LocationInventoryAudit/@Createts")%>">
		<a <%=getDetailHrefOptions("L01", getParameter("locnInventoryAuditKey"),"")%> >
		  <yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@Createts"/>
		 </a>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@LocationId"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@EnterpriseCode"/>
        </td>
        <td class="tablecolumn">
            <yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/InventoryItem/@ItemID"/>
        </td>
        <td class="tablecolumn">
            <yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/InventoryItem/@ProductClass"/>
        </td>
        <td class="tablecolumn">
            <yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/InventoryItem/@UnitOfMeasure"/>
        </td>
        <td class="tablecolumn">
	        <yfc:getXMLValue name="LocationInventoryAudit" 
			binding="xml:LocationInventoryAudit:/LocationInventoryAudit/InventoryItem/Item/PrimaryInformation/@Description" />
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@InventoryStatus"/>
        </td>
		<td class="tablecolumn">
			<yfc:i18n> 
			     <yfc:getXMLValue  binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@AdjustmentType"/> 
			</yfc:i18n>
		</td>
		<td class="tablecolumn">
			<yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@TaskType"/>
		</td>
		<td class="tablecolumn">
			<yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@OrderNo"/>
		</td>
		<td class="tablecolumn">
			<yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@ReleaseNo"/>
		</td>
		<td class="tablecolumn">
			<yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@ShipmentNo"/>
		</td>
		<td class="tablecolumn">
			<yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/Receipt/@ReceiptNo"/>
		</td>
		<td class="tablecolumn">
			<yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@SerialNo"/>
		</td>
		<td class="tablecolumn">
            <yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@ReasonCode"/>
        </td>
		<td class="tablecolumn">
			<yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@Modifyuserid"/>
		</td>
		<td class="numerictablecolumn" sortValue="
			<%if (equals("+",resolveValue("xml:LocationInventoryAudit:/LocationInventoryAudit/@AuditOperation"))){%>
		    +<%}else{%>
			-<%}%>
			<%=getNumericValue("xml:LocationInventoryAudit:/LocationInventoryAudit/@Quantity")%>">
		    <yfc:getXMLValue name="LocationInventoryAudit" binding="xml:LocationInventoryAudit:/LocationInventoryAudit/@Quantity"/>
			<%if (equals("+",resolveValue("xml:LocationInventoryAudit:/LocationInventoryAudit/@AuditOperation"))){%>
		    <yfc:i18n>+</yfc:i18n>
			<%}else{%><yfc:i18n>-</yfc:i18n>
			<%}%>
        </td>
    </tr>
		<%} 
		}%>
    </yfc:loopXML> 
</tbody>
</table>
<%if(equals("Y",request.getParameter("DivRequired"))){%>
</div>
<%}%>