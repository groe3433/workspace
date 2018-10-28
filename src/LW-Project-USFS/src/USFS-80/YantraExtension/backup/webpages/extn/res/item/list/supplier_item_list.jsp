<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<table class="table" width="100%" editable="false">
<thead>
   <tr> 
	   <td sortable="no" class="checkboxheader">
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
		</td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGSupplierItem/@SupplierID")%>">
            <yfc:i18n>Supplier_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGSupplierItem/@ItemID")%>">
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGSupplierItem/@SupplierPartNo")%>">
            <yfc:i18n>Supplier_Part_No</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGSupplierItem/@SupplierStandardPack")%>">
            <yfc:i18n>Supplier_Standar_Pack</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGSupplierItem/@Preferred")%>">
            <yfc:i18n>Preferred</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/NWCGSupplierItem/@UnitCost")%>">
            <yfc:i18n>Unit_Cost</yfc:i18n>
        </td>
   </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/NWCGSupplierItemList/@NWCGSupplierItem" id="NWCGSupplierItem"> 
    <tr> 
        <yfc:makeXMLInput name="orderKey">
			<yfc:makeXMLKey binding="xml:/NWCGSupplierItem/@SupplierItemkey" value="xml:/NWCGSupplierItem/@SupplierItemkey" />
		</yfc:makeXMLInput>                
        <td class="checkboxcolumn">                     
         <input type="checkbox" value='<%=getParameter("orderKey")%>' name="EntityKey"/>
         </td>        
		<td class="tablecolumn">
		<a href="javascript:showDetailFor('<%=getParameter("orderKey")%>');">
			<yfc:getXMLValue binding="xml:NWCGSupplierItem:/NWCGSupplierItem/@SupplierID"/>
        </a>               
		</td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGSupplierItem:/NWCGSupplierItem/@ItemID"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGSupplierItem:/NWCGSupplierItem/@SupplierPartNo"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGSupplierItem:/NWCGSupplierItem/@SupplierStandardPack"/></td>
        <td class="tablecolumn"><yfc:getXMLValue  binding="xml:NWCGSupplierItem:/NWCGSupplierItem/@Preferred"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:NWCGSupplierItem:/NWCGSupplierItem/@UnitCost"/></td>
    </tr>
    </yfc:loopXML> 
</tbody>
</table>