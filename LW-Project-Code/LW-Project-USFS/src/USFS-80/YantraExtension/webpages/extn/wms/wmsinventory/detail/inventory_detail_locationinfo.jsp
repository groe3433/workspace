<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/inventory.jspf"%>

<%  String identifierAttribute = "Tag_#";
   boolean tagTracked= (equals("Y",getValue("ItemDetails","xml:ItemDetails:/Item/@TagCapturedInInventory"))||(equals("S",getValue("ItemDetails","xml:ItemDetails:/Item/@TagCapturedInInventory")))) ? true : false ;

   YFCElement item= (YFCElement) request.getAttribute("ItemDetails");
   if(item!=null){
	   YFCElement inventoryTagAttributesElem = item.getChildElement("InventoryTagAttributes");
	   if(inventoryTagAttributesElem!=null){
		   identifierAttribute = checkElementForIdentifier(inventoryTagAttributesElem,identifierAttribute);
	   }
   }
	String kitCode = resolveValue("xml:ItemDetails:/Item/PrimaryInformation/@KitCode");
	String isSerialTracked = resolveValue("xml:ItemDetails:/Item/InventoryParameters/@IsSerialTracked");
%>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/wmsim.js"></script>
<script language="javascript" src="/yantra/console/scripts/moverequest.js"></script>

<script language="javascript">
function isSerialTrackedKit(inKitCode, inIsSerialTracked, key)
{
	var t_kitCode = document.all(inKitCode);
	var t_isSerialTracked = document.all(inIsSerialTracked);
	if (t_kitCode.value == "PK" && t_isSerialTracked.value == "Y") {
		// it's physical kit and serial tracked item
		alert("This is serial tracked KIT item. Inventory Adjustment is not allowed");
		return false;
	} else {
		// -- implementation of selectOnlyOne(key) -- //
		var eleArray = document.getElementsByName(key); 
		for ( var i =0; i < eleArray.length; i++ ) {
			if ( (eleArray.item(i).type =="checkbox" && eleArray.item(i).checked )) { 
				var hiddenKeyHandle = document.all[eleArray.item(i).getAttribute("yHiddenInputName"+1)]; 
				eleArray.item(i).value = hiddenKeyHandle.value; 
			} 
		}
		return yfcAllowSingleSelection(key);
	}
}

function isTrackedItemforMoveReq(inIsSerialTracked, key) {
	var t_isSerialTracked = document.all(inIsSerialTracked);
	if (t_isSerialTracked.value == "Y") {
		alert("This is a serially tracked item. Please perform your Move Request from the [Inventory -> Create Move Request] feature. ");
		return false;
	} 
}
</script>

<table width="100%" class="table">
<thead>
    <tr> 
        <td class="checkboxheader" sortable="no">
            <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td>

		<td class="tablecolumnheader"  nowrap="true"  style="width:<%= getUITableSize("xml:/ItemInventoryDetail/@InventoryStatus")%>" >
            <yfc:i18n>Status</yfc:i18n>
        </td>


        <td class="tablecolumnheader"  nowrap="true"><yfc:i18n>Pallet_ID</yfc:i18n></td>

        <td class="tablecolumnheader"  nowrap="true"><yfc:i18n>Case_ID</yfc:i18n></td>

		<td class="tablecolumnheader"  nowrap="true"  style="width:<%= getUITableSize("xml:/ItemInventoryDetail/@InventoryQuantity")%>">
            <yfc:i18n>Quantity</yfc:i18n>
        </td>
    

        <td class="tablecolumnheader"  nowrap="true"  style="width:<%= getUITableSize("xml:/ItemInventoryDetail/@InventoryQuantity")%>">
            <yfc:i18n>Pend_Out_Quantity</yfc:i18n>
        </td>

		<td class="tablecolumnheader"  nowrap="true"  style="width:<%= getUITableSize("xml:/ItemInventoryDetail/@InventoryQuantity")%>">
            <yfc:i18n>Pend_In_Quantity</yfc:i18n>
        </td>

        <td class="tablecolumnheader"  nowrap="true"  style="width:<%= getUITableSize("xml:/ItemInventoryDetail/@SegmentType")%>">
            <yfc:i18n>Segment_Type</yfc:i18n>
        </td>

        <td class="tablecolumnheader"  nowrap="true"  style="width:<%= getUITableSize("xml:/ItemInventoryDetail/@Segment")%>">
            <yfc:i18n>Segment_#</yfc:i18n>
        </td>



		<td class="tablecolumnheader" nowrap="true"  style="width:<%= getUITableSize("xml:/ItemInventoryDetail/@CountryOfOrigin")%>">

            <yfc:i18n>COO</yfc:i18n>
        </td>
        <td class="tablecolumnheader" nowrap="true"  style="width:<%= getUITableSize("xml:/ItemInventoryDetail/@ShipByDate")%>">

            <yfc:i18n>Ship_By_Date</yfc:i18n>
        </td>

        <td class="tablecolumnheader" nowrap="true"  style="width:<%= getUITableSize("xml:/ItemInventoryDetail/@FifoNo")%>">

            <yfc:i18n>FIFO_#</yfc:i18n>
        </td>
        <!-- Remove as part of CR 1102
        <td class="tablecolumnheader" nowrap="true"  style="width:<%= getUITableSize("xml:/ItemInventoryDetail/Receipt/@ReceiptNo")%>">

            <yfc:i18n>Receipt_#</yfc:i18n>
        </td>
        -->
		<%if(tagTracked){%>
		<td class="tablecolumnheader" nowrap="true"  style="width:<%= getUITableSize("xml:/ItemInventoryDetail/TagDetail/@TagNumber")%>">

            <yfc:i18n><%=identifierAttribute%></yfc:i18n>
        </td>
        <%}%>

    </tr>
</thead>
<tbody>
    <% if(!isVoid(resolveValue("xml:/NodeInventory/LocationInventoryList/LocationInventory/@LocationId")))  {  %>
    <yfc:loopXML binding="xml:/NodeInventory/LocationInventoryList/LocationInventory/ItemInventoryDetailList/@ItemInventoryDetail" id="ItemInventoryDetail" > 
    <tr>
	
		<% YFCElement nodeElement= (YFCElement) request.getAttribute("NodeInventory");

		   if(nodeElement!=null){ nodeElement.setAttribute("ChangeInventoryMode","detail");
		   nodeElement.setAttribute("SegmentTypeQryType","VOID");
		   }
		%>
	    <yfc:makeXMLInput name="itemInventoryDetailKey">
            <yfc:makeXMLKey binding="xml:/NodeInventory/@Node" value="xml:/NodeInventory/@Node" />
			<yfc:makeXMLKey binding="xml:/NodeInventory/@ProtectedParam" value="xml:/NodeInventory/@Node" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/@ChangeInventoryMode" value="xml:/NodeInventory/@ChangeInventoryMode" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/@EnterpriseCode" value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@OrganizationCode" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/@ZoneId" value="xml:/NodeInventory/@ZoneId" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/@LocationId" value="xml:/NodeInventory/LocationInventoryList/LocationInventory/@LocationId" />
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@InventoryItemKey" value="xml:/ItemInventoryDetail/@InventoryItemKey"/>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/InventoryItem/@ItemID"  value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@ItemID" />
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/InventoryItem/@UnitOfMeasure"  value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@UnitOfMeasure" />
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/InventoryItem/@ProductClass"  value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@ProductClass" />
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@InventoryTagKey" value="xml:/ItemInventoryDetail/@InventoryTagKey"/>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@ReceiptHeaderKey" value="xml:/ItemInventoryDetail/@ReceiptHeaderKey"/>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@CountryOfOrigin" value="xml:/ItemInventoryDetail/@CountryOfOrigin"/>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@SegmentType" value="xml:/ItemInventoryDetail/@SegmentType"/>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@Segment" value="xml:/ItemInventoryDetail/@Segment"/>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@ShipByDate" value="xml:/ItemInventoryDetail/@ShipByDate"/>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@InventoryStatus" value="xml:/ItemInventoryDetail/@InventoryStatus"/>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@FifoNo" value="xml:/ItemInventoryDetail/@FifoNo"/>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@ReceiptNo" value="xml:/ItemInventoryDetail/@ReceiptNo"/>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/TagDetail/@TagNumber" value="xml:/ItemInventoryDetail/@TagNumber"/>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/TagDetail/@BatchNo" value="xml:/ItemInventoryDetail/TagDetail/@BatchNo"/>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/TagDetail/@RevisionNo" value="xml:/ItemInventoryDetail/TagDetail/@RevisionNo"/>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/TagDetail/@LotNumber" value="xml:/ItemInventoryDetail/TagDetail/@LotNumber"/>
			<% String sCaseId = resolveValue("xml:/ItemInventoryDetail/@CaseId");
			if (!isVoid(sCaseId)) { %>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@CaseId" value="xml:/ItemInventoryDetail/@CaseId"/>
			<%}else{%>
			<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@PalletId" value="xml:/ItemInventoryDetail/@PalletId"/>
			<%}%>
			<%  if(isVoid(resolveValue("xml:/ItemInventoryDetail/@SegmentType"))){%>
				
                <yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@SegmentTypeQryType" value="xml:/NodeInventory/@SegmentTypeQryType"/>
			<%}%>
		</yfc:makeXMLInput>

		<yfc:makeXMLInput name="itemKey">
				<yfc:makeXMLKey binding="xml:/Item/@UserOrganizationCode" value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@OrganizationCode" />
				<yfc:makeXMLKey binding="xml:/Item/@ItemID" value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@ItemID" />
				<yfc:makeXMLKey binding="xml:/Item/@UnitOfMeasure" value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@UnitOfMeasure" />

		 </yfc:makeXMLInput>

		<%
			String PalletID = getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/@PalletId");
			String CaseID = getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/@CaseId");
			if (!isVoid(CaseID))
				PalletID="";
             
            String SerialNo = "";
            if(getNumericValue("xml:/ItemInventoryDetail/@Quantity") == 1)
            {
                SerialNo = getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/SerialList/SerialDetail/@SerialNo");
            }
		%>
	
        <td class="checkboxcolumn">
			<input name="chkEntityKey" type="checkbox" yfcMultiSelectCounter='<%=ItemInventoryDetailCounter%>' yfcMultiSelectValue1='<%=getValue("NodeInventory", "xml:/NodeInventory/@Node")%>' yfcMultiSelectValue2='<%=getValue("NodeInventory", "xml:/NodeInventory/LocationInventoryList/LocationInventory/@LocationId")%>' yfcMultiSelectValue3='<%=getValue("NodeInventory", "xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@OrganizationCode")%>' yfcMultiSelectValue4='<%=getValue("NodeInventory", "xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@ItemID")%>' yfcMultiSelectValue5='<%=getValue("NodeInventory", "xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@ProductClass")%>' yfcMultiSelectValue6='<%=getValue("NodeInventory", "xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@UnitOfMeasure")%>' yfcMultiSelectValue7='<%=getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/@InventoryStatus")%>' yfcMultiSelectValue8='<%=PalletID%>' yfcMultiSelectValue9='<%=CaseID%>' yfcMultiSelectValue10='<%=getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/Receipt/@ReceiptNo")%>' yfcMultiSelectValue11='<%=getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/@ReceiptHeaderKey")%>' yfcMultiSelectValue12='<%=getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/@CountryOfOrigin")%>' yfcMultiSelectValue13='<%=getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/@SegmentType")%>' yfcMultiSelectValue14='<%=getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/@Segment")%>' yfcMultiSelectValue15='<%=getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/@ShipByDate")%>' yfcMultiSelectValue16='<%=getNumericValue("xml:/ItemInventoryDetail/@Quantity")%>' yfcMultiSelectValue17='<%=getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/TagDetail/@TagNumber")%>' yfcMultiSelectValue18='<%=getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/TagDetail/@BatchNo")%>' yfcMultiSelectValue19='<%=getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/TagDetail/@RevisionNo")%>' yfcMultiSelectValue20='<%=getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/TagDetail/@LotNumber")%>' yfcMultiSelectValue21='<%=getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/TagDetail/@LotExpirationDate")%>' yfcMultiSelectValue22='<%=getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/TagDetail/@ManufacturingDate")%>' yfcMultiSelectValue23='<%=getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/TagDetail/@LotAttribute1")%>' yfcMultiSelectValue24='<%=getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/TagDetail/@LotAttribute2")%>' yfcMultiSelectValue25='<%=getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/TagDetail/@LotAttribute3")%>' yfcMultiSelectValue26='<%=SerialNo%>' multipleSummaryAttributes='N' yHiddenInputName1="ItemInventoryDetailKey_<%=ItemInventoryDetailCounter%>"  yHiddenInputName2="ItemKey_<%=ItemInventoryDetailCounter%>"/>
					
			<input type="hidden" value='<%=getParameter("itemInventoryDetailKey")%>' name="ItemInventoryDetailKey_<%=ItemInventoryDetailCounter%>" />

			<input type="hidden" value='<%=getParameter("itemKey")%>' name="ItemKey_<%=ItemInventoryDetailCounter%>"/> 	


        </td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ItemInventoryDetail/@InventoryStatus"/></td>
	    <!-- pallet id link -->

        	<yfc:makeXMLInput name="LPNKey" >
				<yfc:makeXMLKey binding="xml:/GetLPNDetails/LPN/@PalletId" value="xml:/ItemInventoryDetail/@PalletId" />
				<yfc:makeXMLKey binding="xml:/GetLPNDetails/@EnterpriseCode" value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@OrganizationCode" />
				<yfc:makeXMLKey binding="xml:/GetLPNDetails/@Node" value="xml:/NodeInventory/@Node" />
			</yfc:makeXMLInput>
					<td class="tablecolumn">
				<a <%=getDetailHrefOptions("L01",getParameter("LPNKey"),"")%> >
				<yfc:getXMLValue binding="xml:/ItemInventoryDetail/@PalletId"/>
				</a>
			</td>

        <!-- case id link-->   

        	<yfc:makeXMLInput name="LPNKey" >
				<yfc:makeXMLKey binding="xml:/GetLPNDetails/LPN/@CaseId" value="xml:/ItemInventoryDetail/@CaseId" />
				<yfc:makeXMLKey binding="xml:/GetLPNDetails/@EnterpriseCode" value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@OrganizationCode" />
				<yfc:makeXMLKey binding="xml:/GetLPNDetails/@Node" value="xml:/NodeInventory/@Node" />
			</yfc:makeXMLInput>
	        <td class="tablecolumn">
				<a <%=getDetailHrefOptions("L01",getParameter("LPNKey"),"")%> >
				<yfc:getXMLValue binding="xml:/ItemInventoryDetail/@CaseId"/>
				</a>
		    </td>

     
        <td class="numerictablecolumn"><yfc:getXMLValue  binding="xml:/ItemInventoryDetail/@Quantity"/></td>     
        <td class="numerictablecolumn"><yfc:getXMLValue  binding="xml:/ItemInventoryDetail/@PendOutQty"/></td>
        <td class="numerictablecolumn"><yfc:getXMLValue  binding="xml:/ItemInventoryDetail/@PendInQty"/></td>
        <td class="tablecolumn"><yfc:getXMLValue  binding="xml:/ItemInventoryDetail/@SegmentType"/></td>
		<td class="tablecolumn"><yfc:getXMLValue  binding="xml:/ItemInventoryDetail/@Segment"/></td>
        <td class="tablecolumn"><yfc:getXMLValue  binding="xml:/ItemInventoryDetail/@CountryOfOrigin"/></td>
        <td class="tablecolumn"><yfc:getXMLValue  binding="xml:/ItemInventoryDetail/@ShipByDate"/></td>
        <td class="tablecolumn">
		<%=(int)getNumericValue("xml:/ItemInventoryDetail/@FifoNo")%>
		</td>
		<!-- Remove as part of CR 1102
		<td class="tablecolumn"><yfc:getXMLValue  binding="xml:/ItemInventoryDetail/Receipt/@ReceiptNo"/></td>
		-->
		<%if(tagTracked){%>
	    <td class="tablecolumn">
			<a <%=getDetailHrefOptions("L02",getParameter("itemInventoryDetailKey"),"")%> >
				<yfc:getXMLValue binding="xml:/ItemInventoryDetail/TagDetail/@TagNumber"/>
			</a>
		</td>
        <%}%>
    </tr>
    </yfc:loopXML> 
    <% } %>
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

<input type="hidden" <%=getTextOptions("xml:/NodeInventory/@ChangeInventoryMode","xml:/NodeInventory/@ChangeInventoryMode","detail")%>  />
<input type="hidden" <%=getTextOptions("xml:/Item/PrimaryInformation/@KitCode","xml:/Item/PrimaryInformation/@KitCode",kitCode)%>  />
<input type="hidden" <%=getTextOptions("xml:/Item/InventoryParameters/@IsSerialTracked","xml:/Item/InventoryParameters/@IsSerialTracked",isSerialTracked)%>  />
