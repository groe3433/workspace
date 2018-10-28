<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="/yantra/console/scripts/im.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<table class="view">


<tr>
    <td class="searchlabel" ><yfc:i18n>Organization</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
		<input type="text" class="protectedinput" contenteditable="false" <%=getTextOptions("xml:/Item/@CallingOrganizationCode")%>/>
    </td>
</tr>

    <% // Now call the APIs that are dependent on the calling organization code %>
	<yfc:callAPI apiID="AP2"/>
    <yfc:callAPI apiID="AP3"/>

<tr>
    <td class="searchlabel" ><yfc:i18n>Item_ID</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGSupplierItem/@ItemIDQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGSupplierItem/@ItemIDQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGSupplierItem/@ItemID") %> />
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select name="xml:/NWCGSupplierItem/@UnitOfMeasure" class="combobox" >
            <yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure" value="UnitOfMeasure" selected="xml:/NWCGSupplierItem/@UnitOfMeasure"/>
        </select>
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Supplier_ID</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGSupplierItem/@SupplierIDQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGSupplierItem/@SupplierIDQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGSupplierItem/@SupplierID") %> />
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Supplier_Part_Number</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGSupplierItem/@SupplierPartNoQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGSupplierItem/@SupplierPartNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGSupplierItem/@SupplierPartNo") %> />
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Supplier_Standar_Pack</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGSupplierItem/@SupplierStandardPackQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGSupplierItem/@SupplierStandardPackQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGSupplierItem/@SupplierStandardPack") %> />            
    </td>
</tr>
<!--<tr>
	<td class="searchcriteriacell">
		<input type="checkbox" <%=getCheckBoxOptions("xml:/NWCGSupplierItem/@Preferred", "xml:/NWCGSupplierItem/@Preferred", "Y")%> yfcCheckedValue='Y' yfcUnCheckedValue='N' ><yfc:i18n>Preffered Supplier</yfc:i18n></input>
	</td>
</tr>    
-->
</table>