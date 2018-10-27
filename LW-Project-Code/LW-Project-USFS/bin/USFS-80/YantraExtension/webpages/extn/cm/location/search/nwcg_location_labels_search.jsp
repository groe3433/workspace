<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="/yantra/console/scripts/im.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>

<table class="view">

<tr>
    <td class="searchlabel" ><yfc:i18n>Cache_ID</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/Location/@NodeQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/Location/@NodeQryType"/>
        </select>
		<input value='<%=getValue("CurrentUser","xml:/User/@Node")%>' type="text" class="unprotectedinput" <%=getTextOptions("xml:/Location/@Node")%> />
		<img class="lookupicon" onclick="callLookup(this,'shipnode')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Node")%>/>
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Location ID</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/Location/@LocationIdQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/Location/@LocationIdQryType"/>
        </select>
		<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Location/@LocationId")%>/>
	 </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Zone ID</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/Location/@ZoneIdQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/Location/@ZoneIdType"/>
        </select>
		<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Location/@ZoneId")%>/>
    </td>
</tr>

</table>