<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<table class="table" width="100%" editable="false">
<thead>
   <tr> 
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ItemList/Item/@ItemID")%>">
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Item/@GlobalItemID")%>">
            <yfc:i18n>Global_Item_ID</yfc:i18n>
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
    <yfc:loopXML name="ItemList" binding="xml:/ItemList/@Item" id="item"> 
	<yfc:makeXMLInput name="orderKey">
			<yfc:makeXMLKey binding="xml:item:/Item/@ItemKey" value="xml:item:/Item/@ItemKey" />
		</yfc:makeXMLInput>
    <tr> 
        <td class="tablecolumn">
		<a href="javascript:showDetailFor('<%=getParameter("orderKey")%>');">
			<yfc:getXMLValue binding="xml:item:/Item/@ItemID"/>
        </a>               
		</td>
		<td class="tablecolumn"><yfc:getXMLValue name="item" binding="xml:item:/Item/@GlobalItemID"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="item" binding="xml:/Item/PrimaryInformation/@DefaultProductClass"/></td>

        <td class="tablecolumn"><yfc:getXMLValue name="item" binding="xml:item:/Item/@UnitOfMeasure"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="item" binding="xml:item:/Item/PrimaryInformation/@ShortDescription"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="item" binding="xml:/Item/PrimaryInformation/@MasterCatalogID"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="item" binding="xml:item:/Item/@OrganizationCode"/></td>
    </tr>
    </yfc:loopXML> 
</tbody>
</table>