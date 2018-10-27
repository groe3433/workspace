<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<script language="javascript" src="/yantra/console/scripts/exceptionutils.js"></script>
<%
    boolean isAlertAvailable = false;
    int totalRecords = 0;
    YFCElement rootElement = (YFCElement)request.getAttribute("InboxList");
    if(rootElement != null){
        totalRecords = rootElement.getIntAttribute("TotalNumberOfRecords");

        if(totalRecords > 0)
            isAlertAvailable = true;
    }
%>

<% if (isAlertAvailable) {%>
<table class="table">
<thead>
    <tr> 
        <td class="checkboxheader" sortable="no">
            <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
            <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Inbox/User/@Loginid","xml:CurrentUser:/User/@Loginid")%> />
            <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Inbox/@AssignedToUserKey","xml:CurrentUser:/User/@UserKey")%> />
            <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Inbox/@ActiveFlag","Y")%> />
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Inbox/@InboxKey")%>">
            <yfc:i18n>Alert_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Inbox/@ExceptionType")%>">
            <yfc:i18n>Type</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Inbox/@QueueId")%>">
            <yfc:i18n>Queue</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Inbox/@Priority")%>">
            <yfc:i18n>Priority</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Inbox/@ConsolidationCount")%>">
            <yfc:i18n>Count</yfc:i18n>
        </td>
        <td class="tablecolumnheader" nowrap="true" style="width:<%= getUITableSize("xml:/Inbox/@LastOccurredOn")%>">
            <yfc:i18n>Last_Raised_On</yfc:i18n>
        </td>
        <td class="tablecolumnheader" nowrap="true" style="width:<%= getUITableSize("xml:/Inbox/@LastOccurredOn")%>">
            <yfc:i18n>Created_For</yfc:i18n>
        </td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/InboxList/@Inbox" id="Inbox" keyName="InboxKey">
        <yfc:makeXMLInput name="inboxKey" >
            <yfc:makeXMLKey binding="xml:/Inbox/@InboxKey" value="xml:/Inbox/@InboxKey" />
        </yfc:makeXMLInput>
    <tr>
        <td class="checkboxcolumn"> 
            <input type="checkbox" value='<%=getParameter("inboxKey")%>' name="chkEntityKey"/>
        </td>
        <td class="tablecolumn">
            <a href = "javascript:showDetailForViewGroupId('exception','NWAYEMD010','<%=getParameter("inboxKey")%>');"> 
                <yfc:getXMLValue binding="xml:/Inbox/@InboxKey"/>
	    	</a>
        </td>
        <td class="tablecolumn">
            <yfc:getXMLValue name="Inbox" binding="xml:/Inbox/@ExceptionType"/>
        </td>
        <td class="tablecolumn">
            <yfc:getXMLValue name="Inbox" binding="xml:/Inbox/Queue/@QueueId"/>
        </td>
        <td class="tablecolumn">
            <yfc:getXMLValue name="Inbox" binding="xml:/Inbox/@Priority"/>
        </td>
        <td class="tablecolumn" sortValue="<%=getNumericValue("xml:/Inbox/@ConsolidationCount")%>">
            <yfc:getXMLValue name="Inbox" binding="xml:/Inbox/@ConsolidationCount"/>
        </td>
        <td class="tablecolumn" sortValue="<%=getDateValue("xml:Inbox:/Inbox/@LastOccurredOn")%>">
            <yfc:getXMLValue name="Inbox" binding="xml:/Inbox/@LastOccurredOn"/>
        </td>
        <%  YFCElement elem = getElement("Inbox");
            String sListDesc = elem.getAttribute("ListDescription");
        %>

        <td class="tablecolumn"><%=sListDesc%></td>
    </tr>
    </yfc:loopXML> 
</tbody>
</table>
<%} else {%>
<table class="table">
<tr align="center" valign="middle">
    <td>
        <yfc:i18n>There_are_no_open_alerts_assigned_to_you</yfc:i18n>
        <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Inbox/User/@Loginid","xml:CurrentUser:/User/@Loginid")%> />
        <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Inbox/@AssignedToUserKey","xml:CurrentUser:/User/@UserKey")%> />
        <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Inbox/@ActiveFlag","Y")%> />
    </td>
</tr>
</table>
<%}%>

