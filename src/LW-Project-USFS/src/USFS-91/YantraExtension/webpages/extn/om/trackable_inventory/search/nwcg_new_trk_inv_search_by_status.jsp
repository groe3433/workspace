<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/im.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<table class="view">
<!-- Jay : just a place holder, incase this line is removed the framework will throw the null pointer exception-->
<input type='hidden' <%=getTextOptions("xml:/NWCGTrackableItem/@CallingOrganizationCode")%> />

<tr>
    <td class="searchlabel" ><yfc:i18n>Status</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@SerialStatusQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@SerialStatusQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGTrackableItem/@SerialStatus") %> />
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Cache_ID</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@StatusCacheIDQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@StatusCacheIDQryType"/>
        </select>
		<!-- CR 133 ks 2009-09-29 -->
        <input type="text" value='<%=getValue("CurrentUser","xml:/User/@Node")%>' class="unprotectedinput" <%=getTextOptions("xml:/NWCGTrackableItem/@StatusCacheID") %> />
		<!-- end CR 133 ks 2009-09-29 -->
				<img class="lookupicon" onclick="callLookup(this,'shipnode')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Cache_ID")%>/>

    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Order_Number</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@StatusIncidentNoQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@StatusIncidentNoQryType"/>
        </select>
        <input type="text" size="20" class="unprotectedinput" maxlength="30" <%=getTextOptions("xml:/NWCGTrackableItem/@StatusIncidentNo") %> />
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Year</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@StatusIncidentYearQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@StatusIncidentYearQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGTrackableItem/@StatusIncidentYear") %> />
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Customer_Unit_ID</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@StatusBuyerOrganizationCodeQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@StatusBuyerOrganizationCodeQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGTrackableItem/@StatusBuyerOrganizationCode") %> />
    </td>
</tr>

<tr>
</table>