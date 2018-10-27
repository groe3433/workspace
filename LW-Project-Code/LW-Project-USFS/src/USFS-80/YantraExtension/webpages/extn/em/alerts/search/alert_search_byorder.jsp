<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfc.core.*" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>


<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript">
function setByQueueUserSearchCriteria(){
    var oChkStatusOpen = document.all("xml:/ByQueueUser/@ChkStatusOpen");
	var oChkStatusWip = document.all("xml:/ByQueueUser/@ChkStatusWip");
	var oChkStatusClosed = document.all("xml:/ByQueueUser/@ChkStatusClosed");
	var oStatusQryType = document.all("xml:/Inbox/@StatusQryType");
    var oStatus = document.getElementById("status");
	
	if ((oChkStatusOpen.checked) && (oChkStatusWip.checked) && (oChkStatusClosed.checked)) {
        oStatusQryType.value="";
        oStatus.value="";
    } else if ((!oChkStatusOpen.checked) && (oChkStatusWip.checked) && (oChkStatusClosed.checked)) {
        oStatusQryType.value="NE";
        oStatus.value="OPEN";
    } else if ((oChkStatusOpen.checked) && (!oChkStatusWip.checked) && (oChkStatusClosed.checked)) {
        oStatusQryType.value="NE";
        oStatus.value="WIP";
    } else if ((oChkStatusOpen.checked) && (oChkStatusWip.checked) && (!oChkStatusClosed.checked)) {
        oStatusQryType.value="NE";
        oStatus.value="CLOSED";
    } else if ((oChkStatusOpen.checked) && (!oChkStatusWip.checked) && (!oChkStatusClosed.checked)) {
        oStatusQryType.value="EQ";
        oStatus.value="OPEN";
    } else if ((!oChkStatusOpen.checked) && (oChkStatusWip.checked) && (!oChkStatusClosed.checked)) {
        oStatusQryType.value="EQ";
        oStatus.value="WIP";
    } else if ((!oChkStatusOpen.checked) && (!oChkStatusWip.checked) && (oChkStatusClosed.checked)) {
        oStatusQryType.value="EQ";
        oStatus.value="CLOSED";
    } else if ((!oChkStatusOpen.checked) && (!oChkStatusWip.checked) && (!oChkStatusClosed.checked)) {
        oStatusQryType.value="EQ";
        oStatus.value="NONE";
    }
}
</script>

<%
    String sStatus = getSearchCriteriaValueWithDefaulting("xml:/Inbox/@Status","CLOSED");
	String sStatusQryType = getSearchCriteriaValueWithDefaulting("xml:/Inbox/@StatusQryType","NE");
	String sChkStatusOpen = "";
    String sChkStatusWip = "";
    String sChkStatusClosed = "";

    if (YFCObject.equals(sStatus,"NONE") && YFCObject.equals(sStatusQryType,"EQ")) {
        sChkStatusOpen = "N";
        sChkStatusWip = "N";
        sChkStatusClosed = "N";
    } else if (YFCObject.equals(sStatus,"CLOSED") && YFCObject.equals(sStatusQryType,"EQ")) {
        sChkStatusOpen = "N";
        sChkStatusWip = "N";
        sChkStatusClosed = "Y";
    } else if (YFCObject.equals(sStatus,"WIP") && YFCObject.equals(sStatusQryType,"EQ")) {
        sChkStatusOpen = "N";
        sChkStatusWip = "Y";
        sChkStatusClosed = "N";
    } else if (YFCObject.equals(sStatus,"OPEN") && YFCObject.equals(sStatusQryType,"EQ")) {
        sChkStatusOpen = "Y";
        sChkStatusWip = "N";
        sChkStatusClosed = "N";
    } else if (YFCObject.equals(sStatus,"CLOSED") && YFCObject.equals(sStatusQryType,"NE")) {
        sChkStatusOpen = "Y";
        sChkStatusWip = "Y";
        sChkStatusClosed = "N";
    } else if (YFCObject.equals(sStatus,"WIP") && YFCObject.equals(sStatusQryType,"NE")) {
        sChkStatusOpen = "Y";
        sChkStatusWip = "N";
        sChkStatusClosed = "Y";
    } else if (YFCObject.equals(sStatus,"OPEN") && YFCObject.equals(sStatusQryType,"NE")) {
        sChkStatusOpen = "N";
        sChkStatusWip = "Y";
        sChkStatusClosed = "Y";
    } else if (YFCObject.equals(sStatusQryType,"") && YFCObject.equals(sStatusQryType,"")) {
        sChkStatusOpen = "Y";
        sChkStatusWip = "Y";
        sChkStatusClosed = "Y";
    }
%>
<table class="view">
    <tr>
        <td>
            <input type="hidden" name="xml:/Inbox/@LastOccurredOnQryType" value="BETWEEN"/>
			<input type="hidden" <%=getTextOptions("xml:/Inbox/Queue/@QueueKey","xml:/Inbox/Queue/@QueueKey")%> />
            <input type="hidden" name="xml:/Inbox/@StatusQryType" value="<%=sStatusQryType%>"/>
            <input type="hidden" id="status" name="xml:/Inbox/@Status" value="<%=sStatus%>"/>
        </td>
    </tr>
	<tr> 
        <td class="searchlabel">
            <yfc:i18n>Alert_ID</yfc:i18n>
        </td>
    </tr>
    <tr> 
        <td true class="searchcriteriacell"> 
            <select name="xml:/Inbox/@InboxKeyQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                        value="QueryType" selected="xml:/Inbox/@InboxKeyQryType"/> 
            </select>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Inbox/@InboxKey")%>/>
        </td>
    </tr>
    <tr> 
        <td class="searchlabel">
            <yfc:i18n>Alert_Type</yfc:i18n>
        </td>
    </tr>
    <tr> 
        <td class="searchcriteriacell"> 
            <select name="xml:/Inbox/@ExceptionTypeQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                        value="QueryType" selected="xml:/Inbox/@ExceptionTypeQryType"/> 
            </select>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Inbox/@ExceptionType")%>/>
        </td>
    </tr>
    <tr> 
        <td class="searchlabel">
            <yfc:i18n>Queue</yfc:i18n>
        </td>
    </tr>
    <tr> 
        <td true class="searchcriteriacell"> 
            <select name="xml:/Inbox/Queue/@QueueId" class="combobox">
                <yfc:loopOptions binding="xml:/QueueList/@Queue" name="QueueName"
                        value="QueueId" selected="xml:/Inbox/Queue/@QueueId"/> 
            </select>

        </td>
    </tr>
    <tr> 
        <td class="searchlabel">
            <yfc:i18n>Assigned_To_User</yfc:i18n>
        </td>
    </tr>
    <tr> 
        <td class="searchcriteriacell"> 
            <select name="xml:/Inbox/User/@LoginidQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                        value="QueryType" selected="xml:/Inbox/User/@LoginidQryType"/> 
            </select>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Inbox/User/@Loginid")%>/>
			<img class="lookupicon" onclick="callLookup(this,'userlookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_User") %> />





        </td>
    </tr>
    <tr> 
        <td class="searchlabel">
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
    </tr>
    <tr> 
        <td true class="searchcriteriacell"> 
            <select name="xml:/Inbox/@ItemIdQryType" class="combobox">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                    value="QueryType" selected="xml:/Inbox/@ItemIdQryType"/> 
            </select>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Inbox/@ItemId")%>/>
        </td>
    </tr>
    <tr> 
        <td class="searchlabel">
            <yfc:i18n>Order_No</yfc:i18n>
        </td>
    </tr>
    <tr> 
        <td class="searchcriteriacell"> 
            <select name="xml:/Inbox/@OrderNoQryType" class="combobox">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                    value="QueryType" selected="xml:/Inbox/@OrderNoQryType"/> 
            </select>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Inbox/@OrderNo")%>/>
        </td>
    </tr>
    <tr> 
        <td class="searchlabel">
            <yfc:i18n>Ship_Node</yfc:i18n>
        </td>
    </tr>
    <tr> 
        <td true class="searchcriteriacell"> 
            <select name="xml:/Inbox/@ShipnodeKeyQryType" class="combobox">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                    value="QueryType" selected="xml:/Inbox/@ShipnodeKeyQryType"/> 
            </select>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Inbox/@ShipnodeKey")%>/>
        </td>
    </tr>
    <tr> 
        <td class="searchlabel">
            <yfc:i18n>Reference_Name</yfc:i18n>
        </td>
    </tr>
    <tr> 
        <td class="searchcriteriacell"> 
            <select name="xml:/Inbox/InboxReferencesList/InboxReferences/@NameQryType" class="combobox">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                    value="QueryType" selected="xml:/Inbox/InboxReferencesList/InboxReferences/@NameQryType"/> 
            </select>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Inbox/InboxReferencesList/InboxReferences/@Name")%>/>
        </td>
    </tr>
    <tr> 
        <td class="searchlabel">
            <yfc:i18n>Reference_Value</yfc:i18n>
        </td>
    </tr>
    <tr> 
        <td class="searchcriteriacell"> 
            <select name="xml:/Inbox/InboxReferencesList/InboxReferences/@ValueQryType" class="combobox">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                    value="QueryType" selected="xml:/Inbox/InboxReferencesList/InboxReferences/@ValueQryType"/> 
            </select>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Inbox/InboxReferencesList/InboxReferences/@Value")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel">
            <yfc:i18n>Last_Raised_On_Date</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true">
            <input class="dateinput" type="text" <%=getTextOptions("xml:/Inbox/@FromLastOccurredOn_YFCDATE")%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %>/>
            <input class="dateinput" type="text" <%=getTextOptions("xml:/Inbox/@FromLastOccurredOn_YFCTIME")%>/>
            <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %>/>
            <yfc:i18n>To</yfc:i18n>
        <td>
    </tr>
    <tr>
        <td nowrap="true">
            <input class="dateinput" type="text" <%=getTextOptions("xml:/Inbox/@ToLastOccurredOn_YFCDATE")%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %>/>
            <input class="dateinput" type="text" <%=getTextOptions("xml:/Inbox/@ToLastOccurredOn_YFCTIME")%>/>
            <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %>/>
        </td>
    </tr>
    <tr> 
        <td class="searchlabel">
            <yfc:i18n>Raised_Count</yfc:i18n>
        </td>
    </tr>
    <tr> 
        <td class="searchcriteriacell"> 
            <select name="xml:/Inbox/@ConsolidationCountQryType" class="combobox">
            <yfc:loopOptions binding="xml:/QueryTypeList/NumericQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Inbox/@ConsolidationCountQryType"/> 
            </select>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Inbox/@ConsolidationCount")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell">
            <input type="checkbox" <%=getCheckBoxOptions("xml:/Inbox/@SubscribedQueues", "xml:/Inbox/@SubscribedQueues", "Y")%> ><yfc:i18n>Only_SubscribedQueues</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell">
            <input type="checkbox" <%=getCheckBoxOptions("xml:/Inbox/@UnAssigned", "xml:/Inbox/@UnAssigned", "Y")%> ><yfc:i18n>Only_UnAssigned</yfc:i18n>
        </td>
    </tr>
	<tr>
	<td><input type="hidden" name="xml:/Inbox/@Status" value="OPEN"/></td>
	</tr>
	<tr>
        <td class="searchlabel">
            <yfc:i18n>Alert_Status</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell">
            <input type="checkbox" onclick="setByQueueUserSearchCriteria()" <%=getCheckBoxOptions("xml:/ByQueueUser/@ChkStatusOpen", sChkStatusOpen, "Y")%> yfcCheckedValue='Y' yfcUnCheckedValue='N' >
                <yfc:i18n>Label_Alert_Status_Open</yfc:i18n>
				&nbsp;&nbsp;
            </input>
            <input type="checkbox" onclick="setByQueueUserSearchCriteria()" <%=getCheckBoxOptions("xml:/ByQueueUser/@ChkStatusWip", sChkStatusWip, "Y")%> yfcCheckedValue='Y' yfcUnCheckedValue='N' >
                <yfc:i18n>Label_Alert_Status_Wip</yfc:i18n>
				&nbsp;&nbsp;
            </input>
            <input type="checkbox" onclick="setByQueueUserSearchCriteria()" <%=getCheckBoxOptions("xml:/ByQueueUser/@ChkStatusClosed", sChkStatusClosed, "Y")%> yfcCheckedValue='Y' yfcUnCheckedValue='N' >
                <yfc:i18n>Label_Alert_Status_Closed</yfc:i18n>
            </input>
        </td>
    </tr>
</table>
