<%@include file="/yfsjspcommon/yfsutil_mb.jspf"%>
<%@include file="/console/jsp/currencyutils_mb.jspf" %>
<%@ page import="com.yantra.yfc.core.*" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/exceptionutils_mb.js"></script>
<script language="javascript">
    function showAssignToUserPopup_mb(viewId) {
        return doEMPopupDialog_mb('<%=getDefaultDetailViewForGroup("YEMD011")%>',440,180);
    }
</script>
<script language="javascript">
    function showAssignToQueuePopup_mb(viewId) {
        return doEMPopupDialog_mb('<%=getDefaultDetailViewForGroup("YEMD012")%>',440,180);
    }
</script>

<table class="table" editable="false" width="100%" cellspacing="0">
    <thead> 
        <tr>
            <td sortable="no" class="checkboxheader">
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll_mb(this);" />
            </td>
            
    <td class="tablecolumnheader"><yfc:i18n>Alert_ID</yfc:i18n></td>
            
    <td class="tablecolumnheader"><yfc:i18n>Alert_Type</yfc:i18n></td>
            
    <td class="tablecolumnheader"><yfc:i18n>Description</yfc:i18n></td>
            
    <td class="tablecolumnheader"><yfc:i18n>Queue</yfc:i18n></td>
            
    <td class="tablecolumnheader"><yfc:i18n>Priority</yfc:i18n></td>
            
    <td class="tablecolumnheader"><yfc:i18n>Assigned_To_User</yfc:i18n></td>

    <td class="tablecolumnheader"><yfc:i18n>Alert_Status</yfc:i18n></td>
            
    <td class="tablecolumnheader"><yfc:i18n>Last_Raised_On</yfc:i18n></td>
    <td class="tablecolumnheader"><yfc:i18n>Raised_Count</yfc:i18n></td>
            
    <td class="tablecolumnheader"><yfc:i18n>Created_For</yfc:i18n></td>
    <td class="tablecolumnheader"><yfc:i18n>Details</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML binding="xml:/InboxList/@Inbox" id="Inbox">
            <tr>
                <yfc:makeXMLInput name="inboxKey">
                    <yfc:makeXMLKey binding="xml:/Inbox/@InboxKey" />
                </yfc:makeXMLInput>

				<%
				String alertStatus = getValue("Inbox","xml:/Inbox/@Status");
				
				if (YFCObject.isVoid(alertStatus)) {
					alertStatus = "OPEN";
				}
				alertStatus = "ALERT_STATUS_" + alertStatus;
				%>

                <td class="checkboxcolumn" > 
                    <input type="checkbox" value='<%=getParameter("inboxKey")%>' name="EntityKey"/>
                </td>
                <td class="tablecolumn"><a href="javascript:showDetailFor_mb('<%=getParameter("inboxKey")%>');">
                    <yfc:getXMLValue binding="xml:/Inbox/@InboxKey"/></a>
                </td>
                <td class="tablecolumn">
                    <yfc:getXMLValue binding="xml:/Inbox/@ExceptionType"/>
                </td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/Inbox/@Description"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/Inbox/Queue/@QueueId"/></td>
                <td class="tablecolumn" sortValue="<%=getNumericValue("xml:/Inbox/@Priority")%>"><yfc:getXMLValue binding="xml:/Inbox/@Priority"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/Inbox/User/@Loginid"/></td>
				<td class="tablecolumn"><yfc:i18n><%=alertStatus%></yfc:i18n></td>
                <td class="tablecolumn" sortValue="<%=getDateValue("xml:/Inbox/@LastOccurredOn")%>"><yfc:getXMLValue binding="xml:/Inbox/@LastOccurredOn"/></td>
                <td class="tablecolumn" sortValue="<%=getNumericValue("xml:/Inbox/@ConsolidationCount")%>"><yfc:getXMLValue binding="xml:/Inbox/@ConsolidationCount"/></td>
				
				<%	
                    boolean bListcontainsScript = false;
                    boolean bDetcontainsScript = false;
                    YFCElement elem = getElement("Inbox");
					String sListDesc = elem.getAttribute("ListDescription");
                    String sDetailDesc = elem.getAttribute("DetailDescription");

                    if ( sListDesc.toUpperCase().indexOf("<SCRIPT") != -1 ){
                        bListcontainsScript = true;
                    }
                    if ( sDetailDesc.toUpperCase().indexOf("<SCRIPT") != -1 ){
                        bDetcontainsScript = true;
                    }
				%>

                <% if ( bListcontainsScript ) { %>
                    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/Inbox/@ListDescription"/></td>
                <% }else {%>
                    <td class="tablecolumn"><%=sListDesc%></td>
                <% } %>

                <% if ( bDetcontainsScript ) { %>
                    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/Inbox/@DetailDescription"/></td>
                <% }else {%>
                    <td class="tablecolumn"><%=sDetailDesc%></td>
                <% } %>


            </tr>
        </yfc:loopXML> 
   </tbody>
</table>
