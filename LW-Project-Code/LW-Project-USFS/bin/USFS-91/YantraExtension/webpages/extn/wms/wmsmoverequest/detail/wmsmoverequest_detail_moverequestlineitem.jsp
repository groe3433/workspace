<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/moverequest.js"></script>
<div style="width:987px;height:120px;overflow:auto">
<table class="table">
<%
	String sStatus = resolveValue("xml:/MoveRequest/@Status");	
%>


<thead>
    <tr> 
		<td class="checkboxheader" sortable="no">
            <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/MoveRequestLine/@SourceLocationId")%>">
            <yfc:i18n>Source_Location</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/MoveRequestLine/@TargetLocationId")%>">
            <yfc:i18n>Target_Location</yfc:i18n>
        </td>        
		<td class="tablecolumnheader">
            <yfc:i18n>Enterprise</yfc:i18n>
        </td>      
		<td class="tablecolumnheader" >
            <yfc:i18n>Pallet_ID</yfc:i18n>
        </td>
		<td class="tablecolumnheader" >
            <yfc:i18n>Case_ID</yfc:i18n>
        </td>		
        <td class="tablecolumnheader">
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader">
            <yfc:i18n>Item_Description</yfc:i18n>
        </td>
		<td class="tablecolumnheader">
            <yfc:i18n>Product_Class</yfc:i18n>
        </td>
		<td class="tablecolumnheader">
            <yfc:i18n>UOM</yfc:i18n>
        </td>
		<td class="tablecolumnheader">
            <yfc:i18n>Inventory_Status</yfc:i18n>
        </td>
        <td class="tablecolumnheader">
            <yfc:i18n>Requested_Quantity</yfc:i18n>
        </td>
		<td class="tablecolumnheader">
            <yfc:i18n>Released_Quantity</yfc:i18n>
        </td>		
		<td class="tablecolumnheader" >
            <yfc:i18n>Released</yfc:i18n>
        </td>		
		<td class="tablecolumnheader">
            <yfc:i18n>Is_Cancelled</yfc:i18n>
        </td>		
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/MoveRequestLine/@TagNumber")%>">
            <yfc:i18n>Tag_#</yfc:i18n>
        </td>
    </tr>
</thead>
<tbody>
	<yfc:loopXML binding="xml:/MoveRequest/MoveRequestLines/@MoveRequestLine" id="MoveRequestLine"> 
	<%
		if(!isVoid(resolveValue("xml:/MoveRequestLine/@ItemId")))
		{
	%>
    <tr>
		<yfc:makeXMLInput name="MoveRequestLineKey">
			<yfc:makeXMLKey binding="xml:/MoveRequest/@MoveRequestKey" value="xml:/MoveRequest/@MoveRequestKey" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@MoveRequestLineKey" value="xml:/MoveRequestLine/@MoveRequestLineKey" />
            <yfc:makeXMLKey binding="xml:/MoveRequest/@MoveRequestNo" value="xml:/MoveRequest/@MoveRequestNo" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/@Node" value="xml:/MoveRequest/@Node" />
            <yfc:makeXMLKey binding="xml:/MoveRequest/@FromActivityGroup" value="xml:/MoveRequest/@FromActivityGroup" />
            <yfc:makeXMLKey binding="xml:/MoveRequest/@Priority" value="xml:/MoveRequest/@Priority" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/@RequestUserId" value="xml:/MoveRequest/@RequestUserId" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/@HasExceptions" value="xml:/MoveRequest/@HasExceptions" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/@Status" value="xml:/MoveRequest/@Status" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/@StartNoEarlierThan" value="xml:/MoveRequest/@StartNoEarlierThan" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/@FinishNoLaterThan" value="xml:/MoveRequest/@FinishNoLaterThan" />			
        </yfc:makeXMLInput>

		 <yfc:makeXMLInput name="itemInventoryDetailKey">
            <yfc:makeXMLKey binding="xml:/NodeInventory/@Node" value="xml:/MoveRequest/@Node" />
           
            <yfc:makeXMLKey binding="xml:/NodeInventory/@EnterpriseCode" value="xml:/MoveRequestLine/@EnterpriseCode" />
            
            <yfc:makeXMLKey binding="xml:/NodeInventory/@LocationId" value="xml:/MoveRequestLine/@SourceLocationId" />			
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/InventoryItem/@ItemID"  value="xml:/MoveRequestLine/@ItemId" />
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/InventoryItem/@UnitOfMeasure"  value="xml:/MoveRequestLine/@UnitOfMeasure" />
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/InventoryItem/@ProductClass"  value="xml:/MoveRequestLine/@ProductClass" />
			<!-- <yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@InventoryTagKey" value="xml:/ItemInventoryDetail/@InventoryTagKey"/> -->
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@ReceiptHeaderKey" value="xml:/MoveRequest/@ReceiptHeaderKey"/>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@CountryOfOrigin" value="xml:/MoveRequestLine/@CountryOfOrigin"/>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@SegmentType" value="xml:/MoveRequestLine/@SegmentType"/>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@Segment" value="xml:/MoveRequestLine/@Segment"/>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@ShipByDate" value="xml:/MoveRequestLine/@ShipByDate"/>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@InventoryStatus" value="xml:/MoveRequestLine/@InventoryStatus"/>		
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@ReceiptNo" value="xml:/MoveRequestLine/Receipt/@ReceiptNo"/>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/TagDetail/@TagNumber" value="xml:/MoveRequestLine/MoveRequestLineTag/@TagNumber"/>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/TagDetail/@BatchNo" value="xml:/MoveRequestLine/MoveRequestLineTag/@BatchNo"/>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/TagDetail/@RevisionNo" value="xml:/MoveRequestLine/MoveRequestLineTag/@RevisionNo"/>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/TagDetail/@LotNumber" value="xml:/MoveRequestLine/MoveRequestLineTag/@LotNumber"/>
			<% String sCaseId = resolveValue("xml:/MoveRequestLine/@CaseId");
			if (!(isVoid(sCaseId))) { %>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@CaseId" value="xml:/MoveRequestLine/@CaseId"/>
			<%}else{%>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@PalletId" value="xml:/MoveRequestLine/@PalletId"/>
			<%}%>
		</yfc:makeXMLInput>
		<td class="checkboxcolumn">
            <input type="checkbox" value='<%=getParameter("MoveRequestLineKey")%>' name="chkEntityKey" yfcMultiSelectCounter='<%=MoveRequestLineCounter%>'/>
        </td>		
        <td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@SourceLocationId"/>
        </td>
        <td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@TargetLocationId"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@EnterpriseCode"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@PalletId"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@CaseId"/>
        </td>
        <td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@ItemId"/>
        </td>
		
		<!-- Manish K CR 805 First save the input parameters to a string and then use the variables to call the API -->
		<%
		String strItemId = resolveValue("xml:/MoveRequestLine/@ItemId");
		String strOrgCode = resolveValue("xml:/MoveRequestLine/@EnterpriseCode");
		String strUom = resolveValue("xml:/MoveRequestLine/@UnitOfMeasure");
		String strInputXml = "<Item OrganizationCode=\"" + strOrgCode + "\" ItemID=\"" + strItemId + "\" UnitOfMeasure=\"" + strUom + "\" />";
		
		YFCDocument inputGIDDoc = YFCDocument.parse(strInputXml);	
		YFCDocument templateGIDDoc = YFCDocument.parse("<Item ItemID=\"\" OrganizationCode=\"\" UnitOfMeasure=\"\"><PrimaryInformation Description=\"\" ShortDescription=\"\" /></Item>");
		%>
		<yfc:callAPI apiName='getItemDetails' 
		inputElement='<%=inputGIDDoc.getDocumentElement()%>'
		templateElement='<%=templateGIDDoc.getDocumentElement()%>'
		outputNamespace='DescItemDetails'/>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:DescItemDetails:/Item/PrimaryInformation/@ShortDescription"/></td>
		<!-- END Manish K added cor CR 805 -->
		
		<td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@ProductClass"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@UnitOfMeasure"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@InventoryStatus"/>
        </td>
		<td class="tablecolumn">
	
			<input type="text" <%if(!(equals("CREATED",sStatus))){%> disabled="true" <% } %>class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine_"+MoveRequestLineCounter+"/@RequestQuantity","xml:/MoveRequestLine/@RequestQuantity") %> />
	
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@ReleasedQuantity"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@ReleasedFlag"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue binding="xml:/MoveRequestLine/@CancelledFlag"/>
        </td>
		<td class="tablecolumn">
			<a <%=getDetailHrefOptions("L02",getParameter("itemInventoryDetailKey"),"")%> >
				<yfc:getXMLValue binding="xml:/MoveRequestLine/@TagNumber"/>
			</a>            

			<input type="hidden" name="xml:/MoveRequest/MoveRequestLines/MoveRequestLine_<%=MoveRequestLineCounter%>/@MoveRequestLineKey" value='<%=resolveValue("xml:/MoveRequestLine/@MoveRequestLineKey")%>'/>	

			<input type="hidden" name="xml:/JS/JSLine_<%=MoveRequestLineCounter%>/@MoveRequestLineKey" value='<%=resolveValue("xml:/MoveRequestLine/@MoveRequestLineKey")%>'/>


        </td>		
	
    </tr>
	<%
		}	
	%>

    </yfc:loopXML> 	
	<input type="hidden" name="xml:/MoveRequest/MoveRequestLine/@ReasonCode" />
	<input type="hidden" name="xml:/MoveRequest/MoveRequestLine/@ReasonText"/>	

</tbody>
</table>
</div>