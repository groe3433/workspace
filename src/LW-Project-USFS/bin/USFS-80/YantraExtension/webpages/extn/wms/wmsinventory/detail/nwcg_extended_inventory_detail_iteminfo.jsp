<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.shared.ycp.*" %>
<script language="javascript" src="/yantra/console/scripts/wmsim.js"></script>
<%  YFCElement nodeElement= (YFCElement) request.getAttribute("NodeInventory");
	if(nodeElement!=null){ 
	   nodeElement.setDateTimeAttribute("FromCreatets", new YFCDate(YCPConstants.YCP_LOW_DATE));
	   nodeElement.setDateTimeAttribute("ToCreatets", new YFCDate(YCPConstants.YCP_HIGH_DATE) );
	   nodeElement.setAttribute("CreatetsQryType","BETWEEN" );
	   double onhandQty=getNumericValue("xml:/NodeInventory/LocationInventoryList/LocationInventory/@Quantity");
	   double pendinQty=getNumericValue("xml:/NodeInventory/LocationInventoryList/LocationInventory/@PendInQty");
	   double pendoutQty=getNumericValue("xml:/NodeInventory/LocationInventoryList/LocationInventory/@PendOutQty"); 
	   double netQty=onhandQty-pendoutQty + pendinQty;
	   nodeElement.getChildElement("LocationInventoryList").getChildElement("LocationInventory").setAttribute("NetQuantity", netQty);
	}


%> 

<yfc:makeXMLInput name="locationKey">
	<yfc:makeXMLKey  binding="xml:/Location/@Node" value="xml:/NodeInventory/@Node"/>
	<yfc:makeXMLKey  binding="xml:/Location/@LocationId"    value="xml:/NodeInventory/LocationInventoryList/LocationInventory/@LocationId"  />
</yfc:makeXMLInput>

<yfc:makeXMLInput name="itemKey">
	 <yfc:makeXMLKey binding="xml:/Item/@UserOrganizationCode" value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@OrganizationCode" />
	 <yfc:makeXMLKey binding="xml:/Item/@ItemID"  value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@ItemID" />
	 <yfc:makeXMLKey binding="xml:/Item/@ProductClass" value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@ProductClass" />
	 <yfc:makeXMLKey binding="xml:/Item/@UnitOfMeasure" value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@UnitOfMeasure" />
</yfc:makeXMLInput>


<yfc:makeXMLInput name="invAudKey">
	 <yfc:makeXMLKey binding="xml:/LocationInventoryAudit/@EnterpriseCode" value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@OrganizationCode" />
	 <yfc:makeXMLKey binding="xml:/LocationInventoryAudit/InventoryItem/@ItemID"  value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@ItemID" />
	<yfc:makeXMLKey binding="xml:/LocationInventoryAudit/InventoryItem/@InventoryOrganizationCode"  value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@OrganizationCode" />
	 <yfc:makeXMLKey binding="xml:/LocationInventoryAudit/InventoryItem/@ProductClass" value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@ProductClass" />
	 <yfc:makeXMLKey binding="xml:/LocationInventoryAudit/InventoryItem/@UnitOfMeasure" value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@UnitOfMeasure" />
	 <yfc:makeXMLKey binding="xml:/LocationInventoryAudit/@LocationId" value="xml:/NodeInventory/LocationInventoryList/LocationInventory/@LocationId" />
	 <yfc:makeXMLKey binding="xml:/LocationInventoryAudit/@Node" value="xml:/NodeInventory/@Node" />
	 <yfc:makeXMLKey binding="xml:/LocationInventoryAudit/@FromCreatets" value="xml:/NodeInventory/@FromCreatets" />
	 <yfc:makeXMLKey binding="xml:/LocationInventoryAudit/@ToCreatets" value="xml:/NodeInventory/@ToCreatets" />
	 <yfc:makeXMLKey binding="xml:/LocationInventoryAudit/@CreatetsQryType" value="xml:/NodeInventory/@CreatetsQryType" />

</yfc:makeXMLInput>


<yfc:makeXMLInput name="locInvKey">
     <yfc:makeXMLKey binding="xml:/NodeInventory/@Node" value="xml:/NodeInventory/@Node" />
     <yfc:makeXMLKey binding="xml:/NodeInventory/@EnterpriseCode" value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@OrganizationCode" />
     <yfc:makeXMLKey binding="xml:/NodeInventory/@LocationId" value="xml:/NodeInventory/LocationInventoryList/LocationInventory/@LocationId" />
	 <yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@InventoryItemKey" value="xml:/NodeInventory/LocationInventoryList/LocationInventory/ItemInventoryDetailList/ItemInventoryDetail/@InventoryItemKey"/>
     <%if(!isVoid(resolveValue("xml:/NodeInventory/LocationInventoryList/LocationInventory/SummaryAttributes/@CaseId"))){%>
		<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@CaseId"    
		value="xml:/NodeInventory/LocationInventoryList/LocationInventory/SummaryAttributes/@CaseId"/> 
	 <%}else{%>
		<yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/@PalletId" 
         value="xml:/NodeInventory/LocationInventoryList/LocationInventory/SummaryAttributes/@PalletId"/> 
	<%}%>    
</yfc:makeXMLInput>


<input type="hidden" name="WmsInvAudKey" value='<%=getParameter("invAudKey")%>'/>
<input type="hidden" name="WmsInvSerialKey" value='<%=getParameter("locInvKey")%>'/>


<table width="100%" border="0" cellpadding="0" cellSpacing="7px">
	
	<tr>
			<td width="67%" height="100%">
			<table width="100%" class="view" >	
				<tr>
					<td class="detaillabel">
						<yfc:i18n>Node</yfc:i18n>
					</td>
					<td class="protectedtext">
						<yfc:getXMLValue  binding="xml:/NodeInventory/@Node"/>
					</td>
					
					<td class="detaillabel">
						<yfc:i18n>Location</yfc:i18n>
					</td>
					<td class="protectedtext"> <a <%=getDetailHrefOptions("L01",getParameter("locationKey"),"")%> > 
						<yfc:getXMLValue  binding="xml:/NodeInventory/LocationInventoryList/LocationInventory/@LocationId"/>
						</a>
					</td>
				</tr>
				<tr>

					<td class="detaillabel">
						<yfc:i18n>Enterprise</yfc:i18n>
					</td>
					<td class="protectedtext">
						<yfc:getXMLValue  binding="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@OrganizationCode"/>
					</td>

					<td class="detaillabel" >
						<yfc:i18n>Item_ID</yfc:i18n>
					</td>
					<td class="protectedtext" ><a <%=getDetailHrefOptions("L02",getParameter("itemKey"),"")%> >
						<yfc:getXMLValue  binding="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@ItemID"/>
						</a>
					</td>
				</tr>
				<tr>
					<td class="detaillabel" >
						<yfc:i18n>Product_Class</yfc:i18n>
					</td>
					<td class="protectedtext" >
						<yfc:getXMLValue  binding="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@ProductClass"/>
					</td>

		
					<td class="detaillabel" >
						<yfc:i18n>Unit_Of_Measure</yfc:i18n>
					</td>
					<td class="protectedtext" >
						<yfc:getXMLValue  binding="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@UnitOfMeasure"/>
					</td>
				</tr>
				<tr>
					<td class="detaillabel" >
						<yfc:i18n>Item_Description</yfc:i18n>
					</td>
					<td class="protectedtext" colspan="3" >
						<yfc:getXMLValue  binding="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/Item/PrimaryInformation/@Description"/>
					</td>
				</tr>
				</td>
				</table>
			</td>
			<td width="33%" style="border:1px ridge black" valign="top" height="100%">	
					<table  width="100%" class="view" >	
					<tr>

					<td class="detaillabel"  >
					<yfc:i18n>On_Hand_Quantity</yfc:i18n>
					 </td>
					<td class="protectednumber"  >
					<yfc:getXMLValue  binding="xml:/NodeInventory/LocationInventoryList/LocationInventory/@Quantity"/>
					</td>
					</tr>
					<tr>

					<td class="detaillabel"  >
						<yfc:i18n>Pend_In_Quantity</yfc:i18n>
					</td>
					<td class="protectednumber"  >
						<yfc:getXMLValue  binding="xml:/NodeInventory/LocationInventoryList/LocationInventory/@PendInQty"/>
					</td>
					</tr>
					<tr>

					<td class="detaillabel"  >
						<yfc:i18n>Pend_Out_Quantity</yfc:i18n>
					</td>
					<td class="protectednumber"  >
						<yfc:getXMLValue  binding="xml:/NodeInventory/LocationInventoryList/LocationInventory/@PendOutQty"/>
					</td>

					</tr>
				
					<tr>

					<td class="detaillabel"  >
						<yfc:i18n>Net_Quantity</yfc:i18n>
					</td>
					<td class="protectednumber" >

						<yfc:getXMLValue  binding="xml:/NodeInventory/LocationInventoryList/LocationInventory/@NetQuantity"/>

					</td>

				</tr>

			<yfc:makeXMLInput name="nodeKey">
            <yfc:makeXMLKey binding="xml:/InventoryItem/@ItemID" value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@ItemID" />
            <yfc:makeXMLKey binding="xml:/InventoryItem/@UnitOfMeasure" value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@UnitOfMeasure" />
            <yfc:makeXMLKey binding="xml:/InventoryItem/@ProductClass" value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@ProductClass" />
            <yfc:makeXMLKey binding="xml:/InventoryItem/@OrganizationCode" value="xml:/NodeInventory/LocationInventoryList/LocationInventory/InventoryItem/@OrganizationCode" />
            <yfc:makeXMLKey binding="xml:/InventoryItem/@EndDate" value="xml:/NodeInventory/Inventory/@ShipByDate" />
            <yfc:makeXMLKey binding="xml:/InventoryItem/@ShipNode" value="xml:/NodeInventory/@Node" />
			</yfc:makeXMLInput>

			<input type="hidden" name="newEntityKey" value="<%=getParameter("nodeKey")%>"/>

				</table>
			</td>	
</table>
