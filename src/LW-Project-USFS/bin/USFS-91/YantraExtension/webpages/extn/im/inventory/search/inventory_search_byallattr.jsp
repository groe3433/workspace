<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<table width="100%" class="view">

    <jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
        <jsp:param name="EnterpriseCodeBinding" value="xml:/InventoryItem/@OrganizationCode"/>
        <jsp:param name="EnterpriseCodeLabel" value="Organization"/>
        <jsp:param name="RefreshOnEnterpriseCode" value="true"/>
        <jsp:param name="ShowDocumentType" value="false"/>
        <jsp:param name="OrganizationListForInventory" value="true"/>
    </jsp:include>
    <% // Now call the APIs that are dependent on the common fields (Organization Code) %>
    <yfc:callAPI apiID="AP2"/>
    <yfc:callAPI apiID="AP3"/>

<tr>
    <td class="searchlabel" ><yfc:i18n>Item_ID</yfc:i18n></td>
</tr>
<tr >
    <td class="searchcriteriacell" nowrap="true">
        <select name="xml:/InventoryItem/@ItemIDQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/InventoryItem/@ItemIDQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/InventoryItem/@ItemID") %> />
		<% String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>
        <img class="lookupicon" onclick="callItemLookup('xml:/InventoryItem/@ItemID','xml:/InventoryItem/@ProductClass','xml:/InventoryItem/@UnitOfMeasure','item','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item") %> />
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Product_Class</yfc:i18n></td>
</tr>
<tr>
    <td class="searchcriteriacell" nowrap="true">
        <select name="xml:/InventoryItem/@ProductClass" class="combobox" >
            <yfc:loopOptions binding="xml:ProductClass:/CommonCodeList/@CommonCode" 
                name="CodeValue" value="CodeValue" selected="xml:/InventoryItem/@ProductClass"/>
        </select>
    </td>
</tr>
<tr>
    <td  class="searchlabel" ><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
</tr>
<tr>
    <td class="searchcriteriacell" nowrap="true">
        <select name="xml:/InventoryItem/@UnitOfMeasure" class="combobox" >
            <yfc:loopOptions binding="xml:UnitOfMeasure:/ItemUOMMasterList/@ItemUOMMaster" 
                name="UnitOfMeasure" value="UnitOfMeasure" selected="xml:/InventoryItem/@UnitOfMeasure"/>
        </select>
    </td>
</tr>
</table>
