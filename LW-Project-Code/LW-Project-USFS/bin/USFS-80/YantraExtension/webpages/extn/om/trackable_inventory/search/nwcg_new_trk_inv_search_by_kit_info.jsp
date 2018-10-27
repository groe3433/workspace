<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="/yantra/console/scripts/im.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>

<table class="view">
<!-- Jay : just a place holder, incase this line is removed the framework will throw the null pointer exception-->
<input type='hidden' <%=getTextOptions("xml:/NWCGTrackableItem/@CallingOrganizationCode")%> />

<tr>
    <td class="searchlabel" ><yfc:i18n>Kit_Cache_Item</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@KitItemIDQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@KitItemIDQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGTrackableItem/@KitItemID") %> />
		
		<% String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", 
		getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>

<img class="lookupicon" name="search"
onclick="callItemLookup('xml:/NWCGTrackableItem/@KitItemID','xml:/Order/OrderLine/Item/@ProductClass','xml:/Order/OrderLine/@OrderingUOM',
'item','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item") %> />

    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Kit_Property_Number</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@KitSerialNoQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@KitSerialNoQryType"/>
        </select>
        <input type="text" size="30" class="unprotectedinput" maxlength="40" <%=getTextOptions("xml:/NWCGTrackableItem/@KitSerialNo") %> />
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Primary_Kit_Cache_Item</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@KitPrimaryItemIDQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@KitPrimaryItemIDQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGTrackableItem/@KitPrimaryItemID") %> 
/>
<img class="lookupicon" name="search"
onclick="callItemLookup('xml:/NWCGTrackableItem/@KitPrimaryItemID','xml:/Order/OrderLine/Item/@ProductClass','xml:/Order/OrderLine/@OrderingUOM',
'item','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item") %> />
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Primary_Kit_Property_Number</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@KitPrimarySerialNoQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@KitPrimarySerialNoQryType"/>
        </select>
        <input type="text" size="30" class="unprotectedinput" maxlength="40" <%=getTextOptions("xml:/NWCGTrackableItem/@KitPrimarySerialNo") %> />
    </td>
</tr>
</table>