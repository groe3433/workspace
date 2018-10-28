<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>


<table class="table" border="0" cellspacing="0" width="100%">
<thead>
   <tr> 
        <td class="tablecolumnheader" nowrap="true">
            <yfc:i18n>Location_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" nowrap="true">
            <yfc:i18n>Enterprise</yfc:i18n>
        </td>
		<td class="tablecolumnheader" nowrap="true">
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" nowrap="true">
            <yfc:i18n>PC</yfc:i18n>
        </td>
        <td class="tablecolumnheader" nowrap="true">
            <yfc:i18n>UOM</yfc:i18n>
        </td>
        <td class="tablecolumnheader" nowrap="true">
            <yfc:i18n>Description</yfc:i18n>
        </td>
        <td class="tablecolumnheader" nowrap="true">
            <yfc:i18n>Status</yfc:i18n>
        </td>
        <td class="tablecolumnheader" nowrap="true">
            <yfc:i18n>Quantity_On_Hand</yfc:i18n>
        </td>
        </td>
        <td class="tablecolumnheader" nowrap="true">
            <yfc:i18n>Item_Dedicated</yfc:i18n>
        </td>
   </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/SKUDedications/@SKUDedication" id="SKUDedication"> 
    	<yfc:makeXMLInput name="locationKey">
			<yfc:makeXMLKey binding="xml:/SKUDedication/@LocationKey" value="xml:/SKUDedication/@LocationKey" />
		</yfc:makeXMLInput>
	
        <yfc:makeXMLInput name="locationInventoryKey">
            <yfc:makeXMLKey binding="xml:/NodeInventory/@Node" value="xml:/SKUDedication/@Node" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/@LocationId" value="xml:/SKUDedication/@LocationId" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/@EnterpriseCode" value="xml:/SKUDedication/@OrganizationCode" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/InventoryItem/@ItemID" value="xml:/SKUDedication/@ItemID" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/InventoryItem/@ProductClass" value="xml:/SKUDedication/@ProductClass" />
            <yfc:makeXMLKey binding="xml:/NodeInventory/Inventory/InventoryItem/@UnitOfMeasure"  value="xml:/SKUDedication/@UnitOfMeasure" />
        </yfc:makeXMLInput>
        <tr>
        <td class="tablecolumn">
               <!--<a href="javascript:showDetailForViewGroupId('location', 'YWMD030','<%=getParameter("locationKey")%>');"> -->
               <a href="javascript:showDetailForViewGroupId('wmsinventory', 'NWCYWMD042','<%=getParameter("locationInventoryKey")%>');">
                        <yfc:getXMLValue binding="xml:/SKUDedication/@LocationId"/>
               </a>
        </td>
	   	<td class="tablecolumn"><yfc:getXMLValue binding="xml:/SKUDedication/@OrganizationCode"/></td>
    	<td class="tablecolumn"><yfc:getXMLValue binding="xml:/SKUDedication/@ItemID"/></td>
    	<td class="tablecolumn"><yfc:getXMLValue binding="xml:/SKUDedication/@ProductClass"/></td>
   		<td class="tablecolumn"><yfc:getXMLValue binding="xml:/SKUDedication/@UnitOfMeasure"/></td>
    	<td class="tablecolumn"><yfc:getXMLValue binding="xml:/SKUDedication/@ShortDescription"/></td>
    	<td class="tablecolumn"><yfc:getXMLValue binding="xml:/SKUDedication/@Status"/></td>
      	<td class="tablecolumn"><yfc:getXMLValue binding="xml:/SKUDedication/@Quantity"/></td>
    	<td class="tablecolumn"><yfc:getXMLValue binding="xml:/SKUDedication/@ItemDedicated"/></td>
   </tr>
   </yfc:loopXML>
</tbody>
</table>