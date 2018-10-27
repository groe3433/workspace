<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="/yantra/console/scripts/im.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/extn/scripts/extn.js"></script>

<script language="javascript">

function SetFDate()
{
	var FromDate = document.getElementById("xml:/NWCGTrackableItem/@FLastTransactionDate").value;
	var FromDtFields = FromDate.split("/");
    var FromMonth = FromDtFields[0];
    var FromDay = FromDtFields[1];
    var FromYear = FromDtFields[2];
	FromDate = FromYear+FromMonth+FromDay;

	document.getElementById("xml:/NWCGTrackableItem/@FromLastTransactionDate").value = FromDate;
	return true;
}

function SetTDate()
{
	var ToDate = document.getElementById("xml:/NWCGTrackableItem/@TLastTransactionDate").value;
	var ToDtFields = ToDate.split("/");
    var ToMonth = ToDtFields[0];
    var ToDay = ToDtFields[1];
    var ToYear = ToDtFields[2];
	ToDate = ToYear+ToMonth+ToDay;

	document.getElementById("xml:/NWCGTrackableItem/@ToLastTransactionDate").value = ToDate;
	return true;
}
</script> 




<table class="view">
<tr>
<td>
<!-- Jay : just a place holder, incase this line is removed the framework will throw the null pointer exception-->
<input type='hidden' <%=getTextOptions("xml:/NWCGTrackableItem/@CallingOrganizationCode")%>/>
<input type="hidden" name="xml:/NWCGTrackableItem/@LastTransactionDateQryType" value="DATERANGE"/>
<input type='hidden' name='xml:/NWCGTrackableItem/@FromLastTransactionDate' value = ""/>
<input type='hidden' name='xml:/NWCGTrackableItem/@ToLastTransactionDate'  value = ""/>
</td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Order_Number</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@LastIncidentNoQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@LastIncidentNoQryType"/>
        </select>
        <input type="text" size="20" class="unprotectedinput" maxlength="30" <%=getTextOptions("xml:/NWCGTrackableItem/@LastIncidentNo") %> />
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Year</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@LastIncidentYearQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@LastIncidentYearQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGTrackableItem/@LastIncidentYear") %> />
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Customer_Unit_ID</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@LastBuyerOrnanizationCodeQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@LastBuyerOrnanizationCodeQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGTrackableItem/@LastBuyerOrnanizationCode") %> />
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Type</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@TypeQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@TypeQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGTrackableItem/@Type") %> />
    </td>
</tr>
<tr>
    <td cnowrap="true" class="searchlabel" ><yfc:i18n>Document_No</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/NWCGTrackableItem/@LastDocumentNoQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/NWCGTrackableItem/@LastDocumentNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGTrackableItem/@LastDocumentNo") %> />
    </td>
</tr>
<tr>
    <td nowrap="true" class="searchlabel" ><yfc:i18n>Date</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
 		<input class="dateinput" type="text" <%=getTextOptions("xml:/NWCGTrackableItem/@FLastTransactionDate")%> />
		<img class="lookupicon" name="search" onclick="invokeCalendar(this);SetFDate();return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar" ) %> />
	    <yfc:i18n>To</yfc:i18n>
		<input class="dateinput" type="text" <%=getTextOptions("xml:/NWCGTrackableItem/@TLastTransactionDate")%> />
		<img class="lookupicon" name="search" onclick="invokeCalendar(this);SetTDate();return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
	</td>
</tr>

<tr>
</table>