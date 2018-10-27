<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/im.js"></script>
<table class="table" border="0" cellspacing="0" width="100%">
<thead>
    <tr> 
        <td class="checkboxheader" sortable="no">
            <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td>
        <td class="tablecolumnheader" nowrap="true" style="width:<%= getUITableSize("xml:/InventoryItem/@ItemID")%>">
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/InventoryItem/@ProductClass")%>">
            <yfc:i18n>PC</yfc:i18n>
        </td>
        <td class="tablecolumnheader"  nowrap="true"  style="width:<%= getUITableSize("xml:/InventoryItem/@UnitOfMeasure")%>">
            <yfc:i18n>UOM</yfc:i18n>
        </td>
        <td class="tablecolumnheader">
            <yfc:i18n>Description</yfc:i18n>
        </td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML name="InventoryList" binding="xml:/InventoryList/@InventoryItem" id="InventoryItem"  keyName="InventoryItemKey" > 
    <tr> 
        <yfc:makeXMLInput name="inventoryItemKey">
            <yfc:makeXMLKey binding="xml:/InventoryItem/@ItemID" value="xml:/InventoryItem/@ItemID" />
            <yfc:makeXMLKey binding="xml:/InventoryItem/@UnitOfMeasure" value="xml:/InventoryItem/@UnitOfMeasure" />
            <yfc:makeXMLKey binding="xml:/InventoryItem/@ProductClass" value="xml:/InventoryItem/@ProductClass" />
            <yfc:makeXMLKey binding="xml:/InventoryItem/@OrganizationCode" value="xml:InventoryList:/InventoryList/@OrganizationCode" />
            <yfc:makeXMLKey binding="xml:/InventoryItem/@InventoryItemKey" value="xml:InventoryList:/InventoryList/@LastInventoryItemKey" />
			<% if(isShipNodeUser()) { %>
				<yfc:makeXMLKey binding="xml:/InventoryItem/@ShipNode" value="xml:CurrentUser:/User/@Node" />
			<%}%>
        </yfc:makeXMLInput>
        <td class="checkboxcolumn">
            <input type="checkbox" value='<%=getParameter("inventoryItemKey")%>' name="EntityKey"/>
			<input type="hidden" name='ItemID_<%=InventoryItemCounter%>' value='<%=resolveValue("xml:/InventoryItem/@ItemID")%>' />
			<input type="hidden" name='UOM_<%=InventoryItemCounter%>' value='<%=resolveValue("xml:/InventoryItem/@UnitOfMeasure")%>' />
			<input type="hidden" name='PC_<%=InventoryItemCounter%>' value='<%=resolveValue("xml:/InventoryItem/@ProductClass")%>' />
			<input type="hidden" name='OrgCode_<%=InventoryItemCounter%>' value='<%=resolveValue("xml:InventoryList:/InventoryList/@OrganizationCode")%>' />
        </td>
        <td class="tablecolumn">
            <a onclick="javascript:showDetailFor('<%=getParameter("inventoryItemKey")%>');return false;" href=""><yfc:getXMLValue name="InventoryItem" binding="xml:/InventoryItem/@ItemID"/></a>
        </td>
        <td class="tablecolumn"><yfc:getXMLValue name="InventoryItem" binding="xml:/InventoryItem/@ProductClass"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="InventoryItem" binding="xml:/InventoryItem/@UnitOfMeasure"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="InventoryItem" binding="xml:/InventoryItem/Item/PrimaryInformation/@Description"/></td>
    </tr>
    </yfc:loopXML> 
</tbody>
</table>