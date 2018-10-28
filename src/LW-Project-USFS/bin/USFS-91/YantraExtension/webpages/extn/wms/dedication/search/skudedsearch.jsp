<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.nwcg.icbs.yantra.constant.common.NWCGConstants" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/wmsim.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<%
String currentUsersNode = "";
String xmlCurrentUsersNode = "";
String defaultEnterpriseCode = NWCGConstants.ENTERPRISE_CODE;
String defaultProductClass = NWCGConstants.NWCG_ITEM_DEFAULT_PRODUCT_CLASS;

if (isShipNodeUser()) {
	currentUsersNode = getValue("CurrentUser","xml:CurrentUser:/User/@ShipNode");
	xmlCurrentUsersNode = new String("xml:/NodeInventory/@Node=" + currentUsersNode);
}
%>

<table class="view">
<yfc:callAPI apiID="AP3"/>
<tr>
	<td >
		<yfc:i18n>Node</yfc:i18n>
	</td>
</tr>
<tr>
	<td  class="searchcriteriacell">
		<% if (isShipNodeUser()) {%>
			<input type="text" class="protectedinput" name="xml:/NodeInventory/@Node" value='<%=currentUsersNode%>'/>
		<% } else {%>
			<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/NodeInventory/@Node","xml:/NodeInventory/@Node")%>   />
			<img class="lookupicon" name="search" onclick="callLookup(this,'shipnode')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Node")%>/>
		<% } %>
	</td>
</tr>
<tr>


<tr>
    <td class="searchlabel" ><yfc:i18n>Item_ID</yfc:i18n></td>
</tr>
<tr >
    <td class="searchcriteriacell" nowrap="true">
        <select class="combobox"  <%=getComboOptions("xml:/NodeInventory/Inventory/InventoryItem/@ItemIDQryType") %> >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NodeInventory/Inventory/InventoryItem/@ItemIDQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NodeInventory/Inventory/InventoryItem/@ItemID") %> />
        <% String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", defaultEnterpriseCode); %>
		<img class="lookupicon" onclick="callItemLookup('xml:/NodeInventory/Inventory/InventoryItem/@ItemID','<%=defaultProductClass %>', 'xml:yfcSearchCriteria:/NodeInventory/@UnitOfMeasure','item','<%=extraParams%>')"  <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item") %> />
    </td>
</tr>

<tr>
    <tr>
    <td class="searchlabel" ><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select name="xml:/NodeInventory/Inventory/InventoryItem/@UnitOfMeasure" class="combobox" >
            <yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure" value="UnitOfMeasure" selected="xml:/NodeInventory/Inventory/InventoryItem/@UnitOfMeasure"/>
        </select>
    </td>
</tr>



<tr>
    <td class="searchlabel" ><yfc:i18n>Location</yfc:i18n></td>
</tr>
<tr >
    <td class="searchcriteriacell" nowrap="true">
        <select <%=getComboOptions("xml:/NodeInventory/@LocationIdQryType")%> class="combobox">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NodeInventory/@LocationIdQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NodeInventory/@LocationId") %> />
		<% if (isShipNodeUser()) {%>
			<img class="lookupicon" onclick="callLookup(this,'location','<%=xmlCurrentUsersNode%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Location") %> />
		<% } else { %>
			<img class="lookupicon" onclick="if (document.getElementById('xml:/NodeInventory/@Node').value == '') { alert('Please enter a value for Node'); }else{ callLookup(this,'location','xml:/NodeInventory/@Node=' +  document.all['xml:/NodeInventory/@Node'].value ) }" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Location") %> />		                        
		<% } %>
    </td>
</tr>
<!-- FBMS Elements -->
<!-- FBMS Elements -->
</table>