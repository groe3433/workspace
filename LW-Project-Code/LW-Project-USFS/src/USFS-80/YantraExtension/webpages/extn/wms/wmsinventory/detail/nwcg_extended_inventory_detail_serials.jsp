<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/moverequest.js"></script>

<%
int numSecondarySerials=(int) getNumericValue( "xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/Item/PrimaryInformation/@NumSecondarySerials");
int iCounter=0;

%>
<table class="table" width="100%" editable="false">
<thead>
    <tr> 
        <td class="checkboxheader" sortable="no">
            <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/SerialDetail/@SerialNo")%>">
            <yfc:i18n>Serial_#</yfc:i18n>
        </td>
		<% 
			for (int i=1; i < numSecondarySerials+1 ; i++){
		  	String serLabel= "Secondary_Serial_" + i;
		%>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/SerialDetail/@SecondarySerial1")%>">
            <yfc:i18n><%=serLabel%></yfc:i18n>
        </td>

		<%}%>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ItemInventoryDetail/@InventoryStatus")%>">
        	<yfc:i18n>Inventory_Status</yfc:i18n>
        </td>
        
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ItemInventoryDetail/TagDetail/@ManufacturingDate")%>">
            <yfc:i18n>Manufacturing_Date</yfc:i18n>
        </td>
        
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding= "xml:/NodeInventory/LocationInventoryList/LocationInventory/ItemInventoryDetailList/@ItemInventoryDetail"  id="ItemInventoryDetail" > 
		<yfc:loopXML binding= "xml:/ItemInventoryDetail/SerialList/@SerialDetail"  id="SerialDetail" > 
		<tr> 
	      <td class="checkboxcolumn">
			<input name="chkEntityKey" type="checkbox" yfcMultiSelectCounter='<%=String.valueOf(iCounter)%>' yfcMultiSelectValue1='<%=resolveValue("xml:/NodeInventory/@Node")%>' yfcMultiSelectValue2='<%=resolveValue("xml:/NodeInventory/LocationInventoryList/LocationInventory/@LocationId")%>' yfcMultiSelectValue3='<%=resolveValue("xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@OrganizationCode")%>' yfcMultiSelectValue4='<%=resolveValue("xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@ItemID")%>' yfcMultiSelectValue5='<%=resolveValue("xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@ProductClass")%>' yfcMultiSelectValue6='<%=resolveValue("xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@UnitOfMeasure")%>' yfcMultiSelectValue7='<%=resolveValue("xml:/ItemInventoryDetail/@InventoryStatus")%>' yfcMultiSelectValue8='<%=resolveValue("xml:/ItemInventoryDetail/@PalletId")%>' yfcMultiSelectValue9='<%=resolveValue("xml:/ItemInventoryDetail/@CaseId")%>' yfcMultiSelectValue10='<%=resolveValue("xml:/ItemInventoryDetail/Receipt/@ReceiptNo")%>' yfcMultiSelectValue11='<%=resolveValue("xml:/ItemInventoryDetail/@ReceiptHeaderKey")%>' yfcMultiSelectValue12='<%=resolveValue("xml:/ItemInventoryDetail/@CountryOfOrigin")%>' yfcMultiSelectValue13='<%=resolveValue("xml:/ItemInventoryDetail/@SegmentType")%>' yfcMultiSelectValue14='<%=resolveValue("xml:/ItemInventoryDetail/@Segment")%>' yfcMultiSelectValue15='<%=resolveValue("xml:/ItemInventoryDetail/@ShipByDate")%>' yfcMultiSelectValue16="1" yfcMultiSelectValue17='<%=resolveValue("xml:/ItemInventoryDetail/TagDetail/@TagNumber")%>' yfcMultiSelectValue18='<%=resolveValue("xml:/ItemInventoryDetail/TagDetail/@BatchNo")%>' yfcMultiSelectValue19='<%=resolveValue("xml:/ItemInventoryDetail/TagDetail/@RevisionNo")%>' yfcMultiSelectValue20='<%=resolveValue("xml:/ItemInventoryDetail/TagDetail/@LotNumber")%>' yfcMultiSelectValue21='<%=resolveValue("xml:/ItemInventoryDetail/TagDetail/@LotExpirationDate")%>' yfcMultiSelectValue22='<%=resolveValue("xml:/ItemInventoryDetail/TagDetail/@ManufacturingDate")%>' yfcMultiSelectValue23='<%=resolveValue("xml:/ItemInventoryDetail/TagDetail/@LotAttribute1")%>' yfcMultiSelectValue24='<%=resolveValue("xml:/ItemInventoryDetail/TagDetail/@LotAttribute2")%>' yfcMultiSelectValue25='<%=resolveValue("xml:/ItemInventoryDetail/TagDetail/@LotAttribute3")%>' yfcMultiSelectValue26='<%=resolveValue("xml:/SerialDetail/@SerialNo")%>' multipleSummaryAttributes='N' yHiddenInputName1="ItemInventoryDetailKey_<%=String.valueOf(iCounter)%>"  yHiddenInputName2="ItemKey_<%=String.valueOf(iCounter)%>"/>
					
			<input type="hidden" value='<%=getParameter("itemInventoryDetailKey")%>' name="ItemInventoryDetailKey_<%=String.valueOf(iCounter)%>" />

			<input type="hidden" value='<%=getParameter("itemKey")%>' name="ItemKey_<%=String.valueOf(iCounter)%>"/> 	


        </td>
			 <td class="tablecolumn"><yfc:getXMLValue  binding="xml:/SerialDetail/@SerialNo"/></td>     
			 <% 
			 	for (int i=1; i < numSecondarySerials+1 ; i++){
				 String serBinding="xml:/SerialDetail/@SecondarySerial"+i;
			 %>
			<td class="tablecolumn"><yfc:getXMLValue  binding='<%=serBinding%>'/></td>  
			<%	
				}
			%>
		<%
			iCounter++;
		%>
			<!-- CR 1101 cb 2013-11-19 -->
			<td class="tablecolumn"><yfc:getXMLValue  binding="xml:/ItemInventoryDetail/@InventoryStatus"/></td> 
			<!-- CR 165 ks 2008-12-22 -->
			<td class="tablecolumn"><yfc:getXMLValue  binding="xml:/ItemInventoryDetail/TagDetail/@ManufacturingDate"/></td>  
			</tr>
			
		</yfc:loopXML> 

    </yfc:loopXML> 

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
