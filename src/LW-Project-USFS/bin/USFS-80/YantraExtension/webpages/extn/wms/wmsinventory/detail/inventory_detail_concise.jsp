<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/moverequest.js"></script>
<script language="javascript" src="/yantra/console/scripts/wmsim.js"></script>
<script>
	function getCorrectEntityKeyAndOnly1(entityKeyName , keyNumber) {
		if (yfcAllowSingleSelection(entityKeyName)) {
			return getCorrectEntityKey(entityKeyName,keyNumber);
		}
	}
</script>
<div style="height:300px;overflow:auto">
<table class="table" border="0" cellspacing="0" width="100%">
<thead>
    <tr> 
        <td class="checkboxheader" sortable="no">
            <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td>
        <td class="tablecolumnheader" nowrap="true" >
            <yfc:i18n>Location</yfc:i18n>
        </td>
        <td class="tablecolumnheader" nowrap="true" >
            <yfc:i18n>Enterprise</yfc:i18n>
        </td>
        <td class="tablecolumnheader" nowrap="true" >
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
       
        <td class="tablecolumnheader" nowrap="true" >
            <yfc:i18n>PC</yfc:i18n>
        </td>
        <td class="tablecolumnheader" nowrap="true" >
            <yfc:i18n>UOM</yfc:i18n>
        </td>
         <td class="tablecolumnheader" nowrap="true" >
            <yfc:i18n>Description</yfc:i18n>
        </td>
        <td class="tablecolumnheader" nowrap="true" >
            <yfc:i18n>Status</yfc:i18n>
        </td>
        <td class="numerictablecolumnheader"  nowrap="true"  >
            <yfc:i18n>Quantity</yfc:i18n>
        </td>

    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/NodeInventory/LocationInventoryList/@LocationInventory" id="LocationInventory" > 
    <tr> 

              
        <yfc:makeXMLInput name="invKey">
            <yfc:makeXMLKey binding="xml:/getShipNodeInventory/@ShipNode" value="xml:/NodeInventory/@Node" />
			<yfc:makeXMLKey binding="xml:/getShipNodeInventory/@ItemOrganizationCode" value="xml:/LocationInventory/InventoryItem/@OrganizationCode" />
			<yfc:makeXMLKey binding="xml:/getShipNodeInventory/@OrganizationCode" value="xml:/LocationInventory/InventoryItem/@OrganizationCode" />
            <yfc:makeXMLKey binding="xml:/getShipNodeInventory/@ItemID" value="xml:/LocationInventory/InventoryItem/@ItemID" />
            <yfc:makeXMLKey binding="xml:/getShipNodeInventory/@ProductClass" value="xml:/LocationInventory/InventoryItem/@ProductClass" />
            <yfc:makeXMLKey binding="xml:/getShipNodeInventory/@UnitOfMeasure" value="xml:/LocationInventory/InventoryItem/@UnitOfMeasure" />

     
        </yfc:makeXMLInput>

		<% YFCElement nodeElement= (YFCElement) request.getAttribute("NodeInventory");

		   if(nodeElement!=null) nodeElement.setAttribute("ChangeInventoryMode","list");
		  
		%>
        <yfc:makeXMLInput name="locationInventoryKey">
            <yfc:makeXMLKey binding="xml:/NodeInventory/@Node" value="xml:/NodeInventory/@Node" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/@ChangeInventoryMode" value="xml:/NodeInventory/@ChangeInventoryMode" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/@LocationId" value="xml:/LocationInventory/@LocationId" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/@ZoneId" value="xml:/LocationInventory/@ZoneId" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/@EnterpriseCode" value="xml:/LocationInventory/InventoryItem/@OrganizationCode" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/InventoryItem/@ItemID" value="xml:/LocationInventory/InventoryItem/@ItemID" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/InventoryItem/@ProductClass" value="xml:/LocationInventory/InventoryItem/@ProductClass" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/InventoryItem/@UnitOfMeasure"    value="xml:/LocationInventory/InventoryItem/@UnitOfMeasure" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/Receipt/@ReceiptNo"    
			value="xml:/LocationInventory/SummaryAttributes/Receipt/@ReceiptNo"/> 
            <yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@ReceiptHeaderKey"    
			value="xml:/LocationInventory/SummaryAttributes/@ReceiptHeaderKey"/> 
			<%if(!isVoid(resolveValue("xml:/LocationInventory/SummaryAttributes/@CaseId"))){%>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@CaseId"    
			value="xml:/LocationInventory/SummaryAttributes/@CaseId"/> 
			<%}else{%>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@PalletId"    
			value="xml:/LocationInventory/SummaryAttributes/@PalletId"/> 
			<%}%>     
        </yfc:makeXMLInput>
		<%
			String PalletID = getValue("LocationInventory", "xml:/LocationInventory/SummaryAttributes/@PalletId");
			String CaseID = getValue("LocationInventory", "xml:/LocationInventory/SummaryAttributes/@CaseId");
			if (!isVoid(CaseID))
				PalletID="";
		%>

        <td class="checkboxcolumn">
			<input type="checkbox"  name="ListEntityKey"  yfcMultiSelectCounter='<%=LocationInventoryCounter%>' yfcMultiSelectValue1='<%=getValue("NodeInventory", "xml:/NodeInventory/@Node")%>' yfcMultiSelectValue2='<%=getValue("LocationInventory", "xml:/LocationInventory/@LocationId")%>' yfcMultiSelectValue3='<%=getValue("LocationInventory", "xml:/LocationInventory/InventoryItem/@OrganizationCode")%>' yfcMultiSelectValue4='<%=getValue("LocationInventory", "xml:/LocationInventory/InventoryItem/@ItemID")%>' yfcMultiSelectValue5='<%=getValue("LocationInventory", "xml:/LocationInventory/InventoryItem/@ProductClass")%>' yfcMultiSelectValue6='<%=getValue("LocationInventory", "xml:/LocationInventory/InventoryItem/@UnitOfMeasure")%>' yfcMultiSelectValue7='<%=getValue("LocationInventory", "xml:/LocationInventory/SummaryAttributes/@InventoryStatus")%>' yfcMultiSelectValue8='<%=PalletID%>' yfcMultiSelectValue9='<%=CaseID%>' yfcMultiSelectValue10='<%=getValue("LocationInventory", "xml:/LocationInventory/SummaryAttributes/Receipt/@ReceiptNo")%>' yfcMultiSelectValue11='<%=getValue("LocationInventory", "xml:/LocationInventory/SummaryAttributes/@ReceiptHeaderKey")%>' yfcMultiSelectValue12='<%=getValue("LocationInventory", "xml:/LocationInventory/SummaryAttributes/@CountryOfOrigin")%>' yfcMultiSelectValue13='<%=getValue("LocationInventory", "xml:/LocationInventory/SummaryAttributes/@SegmentType")%>' yfcMultiSelectValue14='<%=getValue("LocationInventory", "xml:/LocationInventory/SummaryAttributes/@Segment")%>' yfcMultiSelectValue15='<%=getValue("LocationInventory", "xml:/LocationInventory/SummaryAttributes/@ShipByDate")%>' yfcMultiSelectValue16='<%=resolveValue("xml:/LocationInventory/@Quantity")%>' yfcMultiSelectValue17='<%=getValue("LocationInventory", "xml:/LocationInventory/SummaryAttributes/TagSummary/@TagNumber")%>' yfcMultiSelectValue18='<%=getValue("LocationInventory", "xml:/LocationInventory/SummaryAttributes/TagSummary/@BatchNo")%>' yfcMultiSelectValue19='<%=getValue("LocationInventory", "xml:/LocationInventory/SummaryAttributes/TagSummary/@RevisionNo")%>' yfcMultiSelectValue20='<%=getValue("LocationInventory", "xml:/LocationInventory/SummaryAttributes/TagSummary/@LotNumber")%>' yfcMultiSelectValue21='<%=getValue("LocationInventory", "xml:/LocationInventory/SummaryAttributes/TagSummary/@LotExpirationDate")%>' yfcMultiSelectValue22='<%=getValue("LocationInventory", "xml:/LocationInventory/SummaryAttributes/TagSummary/@ManufacturingDate")%>' yfcMultiSelectValue23='<%=getValue("LocationInventory", "xml:/LocationInventory/SummaryAttributes/TagSummary/@LotAttribute1")%>' yfcMultiSelectValue24='<%=getValue("LocationInventory", "xml:/LocationInventory/SummaryAttributes/TagSummary/@LotAttribute2")%>' yfcMultiSelectValue25='<%=getValue("LocationInventory", "xml:/LocationInventory/SummaryAttributes/TagSummary/@LotAttribute3")%>' yfcMultiSelectValue26='<%=getValue("LocationInventory", "xml:/LocationInventory/ItemInventoryDetailList/ItemInventoryDetail/SerialList/SerialDetail/@SerialNo")%>' multipleSummaryAttributes='<%=getValue("LocationInventory", "xml:/LocationInventory/@MultipleSummaryAttributes")%>' yHiddenInputName1="LocationInventoryKey_<%=LocationInventoryCounter%>" yHiddenInputName2="InventorySupplyKey_<%=LocationInventoryCounter%>" yHiddenInputName3="LocnInvMoveRequestKey_<%=LocationInventoryCounter%>"  />

            <input type="hidden" value='<%=getParameter("locationInventoryKey")%>' name="LocationInventoryKey_<%=LocationInventoryCounter%>" />

			<input type="hidden" value='<%=getParameter("invKey")%>' name="InventorySupplyKey_<%=LocationInventoryCounter%>"/> 			
        </td>

        <td class="tablecolumn" >

              <a <%=getDetailHrefOptions("L01", getParameter("locationInventoryKey"),"")%> >
			
			<yfc:getXMLValue name="LocationInventory" binding="xml:/LocationInventory/@LocationId"/>
			
			</a>
			
        </td>
        <td class="tablecolumn"><yfc:getXMLValue name="LocationInventory" binding="xml:/LocationInventory/InventoryItem/@OrganizationCode"/></td>
		<td class="tablecolumn"><yfc:getXMLValue name="LocationInventory" binding="xml:/LocationInventory/InventoryItem/@ItemID"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="LocationInventory" binding="xml:/LocationInventory/InventoryItem/@ProductClass"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="LocationInventory" binding="xml:/LocationInventory/InventoryItem/@UnitOfMeasure"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="LocationInventory" binding="xml:/LocationInventory/InventoryItem/Item/PrimaryInformation/@Description"/></td> 
        <td class="tablecolumn">
        <%if(equals("N",resolveValue("xml:/LocationInventory/@MultipleSummaryAttributes"))){%>
		<yfc:getXMLValue name="LocationInventory" binding="xml:/LocationInventory/SummaryAttributes/@InventoryStatus"/>
		<%}else if(!isVoid(resolveValue("xml:/LocationInventory/SummaryAttributes/@InventoryStatus"))){%>
            <yfc:getXMLValue name="LocationInventory" binding="xml:/LocationInventory/SummaryAttributes/@InventoryStatus"/>	
		<%}else{%>
            <yfc:i18n>Multiple</yfc:i18n>		
		<%}%>
		</td>
        <td class="numerictablecolumn">
		   <yfc:getXMLValue name="LocationInventory" binding="xml:/LocationInventory/@Quantity"/>
		</td>
    </tr>
    </yfc:loopXML> 
			<input type="hidden" name="xml:/NodeInventory/CountRequest/@DocumentType" value=""/>
			<input type="hidden" name="xml:/NodeInventory/CountRequest/@Priority" value=""/>
			<input type="hidden" name="xml:/NodeInventory/CountRequest/@RequestingUserId" value=""/>
			<input type="hidden" name="xml:/NodeInventory/CountRequest/@CountRequestNo"/>
			<input type="hidden" name="xml:/NodeInventory/CountRequest/@StartNoEarlierThan"/>
			<input type="hidden" name="xml:/NodeInventory/CountRequest/@FinishNoLaterThan"/>
			<input type="hidden" name="xml:/NodeInventory/CountRequest/@StartNoEarlierThan_YFCDATE" value=""/>
			<input type="hidden" name="xml:/NodeInventory/CountRequest/@StartNoEarlierThan_YFCTIME" value=""/>
			<input type="hidden" name="xml:/NodeInventory/CountRequest/@FinishNoLaterThan_YFCDATE" value=""/>
			<input type="hidden" name="xml:/NodeInventory/CountRequest/@FinishNoLaterThan_YFCTIME" value=""/>
			
			<input type="hidden" name="xml:/NodeInventory/MoveRequest/@Node" Value='<%=resolveValue("xml:/NodeInventory/@Node")%>'/>
	 		<input type="hidden" name="xml:/NodeInventory/MoveRequest/@FromActivityGroup"/>
	 		<input type="hidden" name="xml:/NodeInventory/MoveRequest/@MoveRequestNo"/>
			<input type="hidden" name="xml:/NodeInventory/MoveRequest/@Priority"/>
			<input type="hidden" name="xml:/NodeInventory/MoveRequest/@TargetLocationId"/>
			<input type="hidden" name="xml:/NodeInventory/MoveRequest/@Release"/>
			<input type="hidden" name="xml:/NodeInventory/MoveRequest/@RequestUserId"/>			
			<input type="hidden" name="xml:/NodeInventory/MoveRequest/@StartNoEarlierThan"/>
			<input type="hidden" name="xml:/NodeInventory/MoveRequest/@FinishNoLaterThan"/>
			<input type="hidden" name="xml:/NodeInventory/MoveRequest/@ForActivityCode"/>
			
</tbody>
</table>
</div>