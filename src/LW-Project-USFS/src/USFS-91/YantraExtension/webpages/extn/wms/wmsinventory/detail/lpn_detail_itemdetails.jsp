<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/moverequest.js"></script>
<%@ page import="com.yantra.yfs.ui.backend.*" %>


<table class="table" ID="Item_Details" cellspacing="0" width="100%">
    <thead>
        <tr>
	        <td class="checkboxheader" sortable="no">
		        <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
	        </td>
	        <td class="tablecolumnheader" nowrap="true"><yfc:i18n>Case_ID</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/ItemInventoryDetail/InventoryItem/@ItemID")%>">
		    <yfc:i18n>Item_ID</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/ItemInventoryDetail/InventoryItem/@ProductClass")%>">
			<yfc:i18n>PC</yfc:i18n></td>

            <td class="tablecolumnheader" >
		    <yfc:i18n>UOM</yfc:i18n></td>

            <td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/ItemInventoryDetail/InventoryItem/Item/PrimaryInformation/@Description")%>">
		    <yfc:i18n>Description</yfc:i18n></td>

	        <td class="tablecolumnheader" nowrap="true" ><yfc:i18n>Status</yfc:i18n></td>


			<td class="numerictablecolumnheader" nowrap="true" style="width:40px">
			<yfc:i18n>Quantity</yfc:i18n></td>


	        <td class="numerictablecolumnheader" nowrap="true" style="width:45px">
			<yfc:i18n>Pend_Out_Quantity</yfc:i18n></td>
			
	        <td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/ItemInventoryDetail/@SegmentType")%>"><yfc:i18n>Segment_Type</yfc:i18n></td>
	        <td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/ItemInventoryDetail/@Segment")%>"><yfc:i18n>Segment_#</yfc:i18n></td>
	        <td class="tablecolumnheader" nowrap="true" ><yfc:i18n>COO</yfc:i18n></td>
	        <td class="tablecolumnheader" nowrap="true" style=
			"width:<%=getUITableSize("xml:/ItemInventoryDetail/@ShipByDate")%>"> <yfc:i18n>Ship_By_Date</yfc:i18n></td>
	        <td class="tablecolumnheader" nowrap="true" ><yfc:i18n>FIFO_#</yfc:i18n></td>
	        <td class="tablecolumnheader" nowrap="true" ><yfc:i18n>Receipt_#</yfc:i18n></td>

	        <td class="tablecolumnheader" nowrap="true" ><yfc:i18n>Tag_#</yfc:i18n></td>

        </tr>
    </thead>
    <tbody>
        <yfc:loopXML  binding="xml:/GetLPNDetails/LPN/ItemInventoryDetailList/@ItemInventoryDetail" id="ItemInventoryDetail">
	
		<% YFCElement nodeElement= (YFCElement) request.getAttribute("GetLPNDetails");

		   if(nodeElement!=null){ nodeElement.setAttribute("ChangeInventoryMode","detail");
            nodeElement.setAttribute("SegmentTypeQryType","VOID");
		   }
		%>
	       <yfc:makeXMLInput name="locationInventoryKey">
				<%if(isVoid(resolveValue("xml:/GetLPNDetails/@EnterpriseCode"))){%>
					<yfc:makeXMLKey binding="xml:/NodeInventory/@EnterpriseCode" value="xml:/GetLPNDetails/@InventoryOrganizationCode"/> 
				<%}else{%>
					<yfc:makeXMLKey binding="xml:/NodeInventory/@EnterpriseCode" value="xml:/GetLPNDetails/@EnterpriseCode"/> 
				<%}%>
				<yfc:makeXMLKey binding="xml:/NodeInventory/@Node" value="xml:/GetLPNDetails/@Node"/> 
				<yfc:makeXMLKey binding="xml:/NodeInventory/@ChangeInventoryMode" value="xml:/GetLPNDetails/@ChangeInventoryMode" />
				<yfc:makeXMLKey binding="xml:/NodeInventory/@ProtectedParam" value="xml:/GetLPNDetails/@Node"/>
				<yfc:makeXMLKey binding="xml:/NodeInventory/@LocationId"	value="xml:/GetLPNDetails/LPN/LPNLocation/@LocationId" />
				<yfc:makeXMLKey binding="xml:/NodeInventory/@ZoneId" value="xml:/GetLPNDetails/LPN/LPNLocation/@ZoneId" />
				<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@CaseId" value="xml:/ItemInventoryDetail/@CaseId"/>
				<%if(isVoid(resolveValue("xml:/ItemInventoryDetail/@CaseId"))){ %>
					<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@PalletId" value="xml:/ItemInventoryDetail/@PalletId"/>
				<%}%>
				<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@InventoryItemKey" value="xml:/ItemInventoryDetail/@InventoryItemKey"/>
				<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/InventoryItem/@ItemID"  value="xml:/ItemInventoryDetail/InventoryItem/@ItemID" />
				<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/InventoryItem/@UnitOfMeasure"   value="xml:/ItemInventoryDetail/InventoryItem/@UnitOfMeasure"/>
				<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/InventoryItem/@ProductClass"  value="xml:/ItemInventoryDetail/InventoryItem/@ProductClass" />
				<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@InventoryTagKey" value="xml:/ItemInventoryDetail/@InventoryTagKey"/>
				<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@ReceiptHeaderKey" value="xml:/ItemInventoryDetail/@ReceiptHeaderKey"/>
				<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@SegmentType" value="xml:/ItemInventoryDetail/@SegmentType"/>
				<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@Segment" value="xml:/ItemInventoryDetail/@Segment"/>
				<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@ShipByDate" value="xml:/ItemInventoryDetail/@ShipByDate"/>
				<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@InventoryStatus" value="xml:/ItemInventoryDetail/@InventoryStatus"/>
				<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@CountryOfOrigin" value="xml:/ItemInventoryDetail/@CountryOfOrigin"/>
				<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@FifoNo" value="xml:/ItemInventoryDetail/@FifoNo"/>
				<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@ReceiptNo" value="xml:/ItemInventoryDetail/@ReceiptNo"/>
				<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/TagDetail/@TagNumber" value="xml:/ItemInventoryDetail/@TagNumber"/>
                <% if(isVoid(resolveValue("xml:/ItemInventoryDetail/@SegmentType"))){%>
                <yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@SegmentTypeQryType" value="xml:/GetLPNDetails/@SegmentTypeQryType"/>
                <%}%>
			</yfc:makeXMLInput>

			<yfc:makeXMLInput name="itemKey">
				<%if(isVoid(resolveValue("xml:/GetLPNDetails/@EnterpriseCode"))){%>
					<yfc:makeXMLKey binding="xml:/Item/@UserOrganizationCode" value="xml:/GetLPNDetails/@InventoryOrganizationCode"/>
				<%}else{%>
					<yfc:makeXMLKey binding="xml:/Item/@UserOrganizationCode" value="xml:/GetLPNDetails/@EnterpriseCode"/>
				<%}%>
				<yfc:makeXMLKey binding="xml:/Item/@ItemID" value="xml:/ItemInventoryDetail/InventoryItem/@ItemID" />
				<yfc:makeXMLKey binding="xml:/Item/@ItemKey" value="xml:/ItemInventoryDetail/InventoryItem/Item/@ItemKey" />
				<yfc:makeXMLKey binding="xml:/Item/@ProductClass" value="xml:/ItemInventoryDetail/InventoryItem/@ProductClass" />
				<yfc:makeXMLKey binding="xml:/Item/@UnitOfMeasure" value="xml:/ItemInventoryDetail/InventoryItem/@UnitOfMeasure" />

			 </yfc:makeXMLInput>

			<%
				String CaseID = getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/@CaseId");				
				String PalletID = "";								
				if (isVoid(CaseID)){
					PalletID = getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/@PalletId");
				}
				String ItemID = getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/InventoryItem/@ItemID");
				String	ProductClass = getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/InventoryItem/@ProductClass");
				String	UnitOfMeasure = getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/InventoryItem/@UnitOfMeasure");
				String	InventoryStatus = getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/@InventoryStatus");
				String	ReceiptNo = getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/Receipt/@ReceiptNo");
				String	ReceiptHeaderKey = getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/@ReceiptHeaderKey");
				String	CountryOfOrigin = getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/@CountryOfOrigin");
				String	SegmentType = getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/@SegmentType");
				String	Segment = getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/@Segment");
				String	ShipByDate = resolveValue("xml:/ItemInventoryDetail/@ShipByDate");
				double	Quantity = getNumericValue("xml:/ItemInventoryDetail/@Quantity");
				String	TagNumber = getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/TagDetail/@TagNumber");
				String	BatchNo = getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/TagDetail/@BatchNo");
				String	RevisionNo = getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/TagDetail/@RevisionNo");
				String	LotNumber = getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/TagDetail/@LotNumber");					
				String	ManufacturingDate = resolveValue("xml:/ItemInventoryDetail/TagDetail/@ManufacturingDate");
				String	LotAttribute1 = getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/TagDetail/@LotAttribute1");
				String	LotAttribute2 = getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/TagDetail/@LotAttribute2");
				String	LotAttribute3 = getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/TagDetail/@LotAttribute3");				

				String	SerialNo = getValue("ItemInventoryDetail", "xml:/ItemInventoryDetail/SerialList/SerialDetail/@SerialNo");
				
			%>
			
			<tr>
			    <td class="checkboxcolumn">
						<input name="LpnEntityKey" type="checkbox" yfcMultiSelectCounter='<%=ItemInventoryDetailCounter%>' yfcMultiSelectValue1='<%=getValue("GetLPNDetails", "xml:/GetLPNDetails/@Node")%>' yfcMultiSelectValue2='<%=getValue("GetLPNDetails", "xml:/GetLPNDetails/LPN/LPNLocation/@LocationId")%>' yfcMultiSelectValue3='<%=getValue("GetLPNDetails", "xml:/GetLPNDetails/@EnterpriseCode")%>' yfcMultiSelectValue8='<%=PalletID%>' yfcMultiSelectValue9='<%=CaseID%>' yfcMultiSelectValue4='<%=ItemID%>' yfcMultiSelectValue5='<%=ProductClass%>' yfcMultiSelectValue6='<%=UnitOfMeasure%>' yfcMultiSelectValue7='<%=InventoryStatus%>'  yfcMultiSelectValue10='<%=ReceiptNo%>' yfcMultiSelectValue11='<%=ReceiptHeaderKey%>' yfcMultiSelectValue12='<%=CountryOfOrigin%>' yfcMultiSelectValue13='<%=SegmentType%>' yfcMultiSelectValue14='<%=Segment%>' yfcMultiSelectValue15='<%=ShipByDate%>' yfcMultiSelectValue16='<%=Quantity%>' yfcMultiSelectValue17='<%=TagNumber%>' yfcMultiSelectValue18='<%=BatchNo%>' yfcMultiSelectValue19='<%=RevisionNo%>' yfcMultiSelectValue20='<%=LotNumber%>' yfcMultiSelectValue22='<%=ManufacturingDate%>' yfcMultiSelectValue23='<%=LotAttribute1%>' yfcMultiSelectValue24='<%=LotAttribute2%>' yfcMultiSelectValue25='<%=LotAttribute3%>' yfcMultiSelectValue26='<%=SerialNo%>' multipleSummaryAttributes='N' yHiddenInputName1="LocationInventoryKey_<%=ItemInventoryDetailCounter%>"  yHiddenInputName2="ItemKey_<%=ItemInventoryDetailCounter%>"/>
					
						<input type="hidden" value='<%=getParameter("locationInventoryKey")%>' name="LocationInventoryKey_<%=ItemInventoryDetailCounter%>" />

						<input type="hidden" value='<%=getParameter("itemKey")%>' name="ItemKey_<%=ItemInventoryDetailCounter%>"/> 	

				</td>

				<td class="tablecolumn" > 
					<%if((!(equals(resolveValue("xml:/ItemInventoryDetail/@CaseId"),resolveValue("xml:/GetLPNDetails/LPN/OuterMostLPN/@CaseId")))) &&(!equals(resolveValue("xml:/GetLPNDetails/LPN/@CaseId"),resolveValue("xml:/ItemInventoryDetail/@CaseId")))){ %>
						<yfc:makeXMLInput name="LPNKey" >
							<yfc:makeXMLKey binding="xml:/GetLPNDetails/LPN/@CaseId" value="xml:/ItemInventoryDetail/@CaseId" />
							<yfc:makeXMLKey binding="xml:/GetLPNDetails/@EnterpriseCode" value="xml:/GetLPNDetails/@EnterpriseCode" />
							<yfc:makeXMLKey binding="xml:/GetLPNDetails/@Node" value="xml:/GetLPNDetails/@Node" />

						</yfc:makeXMLInput>
						<a <%=getDetailHrefOptions("L01",getParameter("LPNKey"),"")%> >
							<yfc:getXMLValue binding="xml:/ItemInventoryDetail/@CaseId"/>
						</a>
						<%}else if(!(equals(resolveValue("xml:/GetLPNDetails/LPN/@CaseId"), resolveValue("xml:/ItemInventoryDetail/@CaseId") ) ) ){%>

								<yfc:getXMLValue binding="xml:/ItemInventoryDetail/@CaseId"/>
						<%}%>
					
				</td>

					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/ItemInventoryDetail/InventoryItem/@ItemID"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/ItemInventoryDetail/InventoryItem/@ProductClass"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/ItemInventoryDetail/InventoryItem/@UnitOfMeasure"/></td>
                    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ItemInventoryDetail/InventoryItem/Item/PrimaryInformation/@Description"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/ItemInventoryDetail/@InventoryStatus"/></td>
	                <td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/ItemInventoryDetail/@Quantity"/></td>
					<td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/ItemInventoryDetail/@PendOutQty"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/ItemInventoryDetail/@SegmentType"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/ItemInventoryDetail/@Segment"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/ItemInventoryDetail/@CountryOfOrigin"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/ItemInventoryDetail/@ShipByDate"/></td>
					<td class="tablecolumn">
					<%=(int)getNumericValue("xml:/ItemInventoryDetail/@FifoNo")%></td>
					<td class="tablecolumn"><yfc:getXMLValue  binding="xml:/ItemInventoryDetail/Receipt/@ReceiptNo"/></td>
					<td class="tablecolumn"><a <%=getDetailHrefOptions("L02",getParameter("locationInventoryKey"),"")%> >
						<yfc:getXMLValue binding="xml:/ItemInventoryDetail/TagDetail/@TagNumber"/>
					</a></td>
            </tr>
        </yfc:loopXML>
			<input type="hidden" name="xml:/NodeInventory/MoveRequest/@Node" Value='<%=resolveValue("xml:/GetLPNDetails/@Node")%>'/>
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
<input type="hidden" <%=getTextOptions("xml:/NodeInventory/@ChangeInventoryMode","xml:/NodeInventory/@ChangeInventoryMode","detail") %> />