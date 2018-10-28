<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/extn.js"></script>


<table class="table" width="100%" editable="false">
<thead>
   <tr> 
        <td sortable="no" class="checkboxheader">
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ItemList/Item/@ItemID")%>">
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ItemList/Item/PrimaryInformation/@DefaultProductClass")%>">
            <yfc:i18n>Default_PC</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ItemList/Item/@UnitOfMeasure")%>">
            <yfc:i18n>UOM</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ItemList/Item/PrimaryInformation/@ShortDescription")%>">
            <yfc:i18n>Short_Description</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ItemList/Item/PrimaryInformation/@MasterCatalogID")%>">
            <yfc:i18n>Master_Catalog_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ItemList/Item/@OrganizationCode")%>">
            <yfc:i18n>Catalog_Organization</yfc:i18n>
        </td>
   </tr>
</thead>
<tbody>
    <yfc:loopXML name="ItemList" binding="xml:/ItemList/@Item" id="Item"> 
    <tr> 
		<yfc:makeXMLInput name="itemKey">
			<yfc:makeXMLKey binding="xml:/Item/@ItemKey" value="xml:/Item/@ItemKey" />
			<yfc:makeXMLKey binding="xml:/Item/@ItemID" value="xml:/Item/@ItemID" />
			<yfc:makeXMLKey binding="xml:/Item/@UnitOfMeasure" value="xml:/Item/@UnitOfMeasure" />
            <yfc:makeXMLKey binding="xml:/Item/@OrganizationCode" value="xml:/Item/@OrganizationCode" />
        </yfc:makeXMLInput>
		<yfc:makeXMLInput name="itemPrintKey">
			<yfc:makeXMLKey binding="xml:/Print/Item/@ItemKey" value="xml:/Item/@ItemKey" />
			<yfc:makeXMLKey binding="xml:/Print/Item/@ItemID" value="xml:/Item/@ItemID" />
			<yfc:makeXMLKey binding="xml:/Print/Item/@UnitOfMeasure" value="xml:/Item/@UnitOfMeasure" />
            <yfc:makeXMLKey binding="xml:/Print/Item/@OrganizationCode" value="xml:/Item/@OrganizationCode" />
			<yfc:makeXMLKey binding="xml:/Print/Item/@StandardPack" value="xml:/Item/Extn/@ExtnStandardPack" />
			<yfc:makeXMLKey binding="xml:/Print/Item/@UnitWeight" value="xml:/Item/PrimaryInformation/@UnitWeight" />
			<yfc:makeXMLKey binding="xml:/Print/Item/@IsSerialTracked" value="xml:/Item/InventoryParameters/@IsSerialTracked" />
		</yfc:makeXMLInput>
		
		<td class="checkboxcolumn"> 
            <input type="checkbox" value='<%=getParameter("itemKey")%>' name="EntityKey" 
			PrintEntityKey='<%=getParameter("itemPrintKey")%>'/>
		</td>
        <td class="tablecolumn"><yfc:getXMLValue name="Item" binding="xml:/Item/@ItemID"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="Item" binding="xml:/Item/PrimaryInformation/@DefaultProductClass"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="Item" binding="xml:/Item/@UnitOfMeasure"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="Item" binding="xml:/Item/PrimaryInformation/@ShortDescription"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="Item" binding="xml:/Item/PrimaryInformation/@MasterCatalogID"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="Item" binding="xml:/Item/@OrganizationCode"/></td>
    </tr>
    </yfc:loopXML> 
</tbody>
</table>