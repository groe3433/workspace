<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<script language="javascript" src="/yantra/console/scripts/pm.js"></script>
<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script> 
<%
    String QueueName=getValue("getAlertStatisticsForUser","xml:/getAlertStatisticsForUser/Queue/@QueueName");
%>

<% if (!isVoid(QueueName) ) {%>
<table class="table" editable="false" width="100%" cellspacing="0">
<thead>
    <tr> 
        <td class="tablecolumnheader">
            <yfc:i18n>Queue</yfc:i18n>
        </td>
        <td class="numerictablecolumnheader">
            <yfc:i18n>Your_Open</yfc:i18n>
        </td>
        <td class="numerictablecolumnheader">
            <yfc:i18n>Total_Open</yfc:i18n>
        </td>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/getAlertStatisticsForUser/@Queue" id="Queue">
        <tr>
            <td class="tablecolumn">            	            		
            	<%
				    String entityName="exception";
					String sAddnParams="xml:/Inbox/@ActiveFlag=Y&xml:/Inbox/@Status=OPEN&xml:/Inbox/@StatusQryType=EQ&xml:/Inbox/Queue/@QueueKey=" + getValue("Queue","xml:/Queue/@QueueKey");
				%>
            	<a href="javascript:yfcShowListWithParams('','<%=entityName%>','<%=sAddnParams%>');">
				<yfc:getXMLValue name="Queue" binding="xml:/Queue/@QueueId"/>
				</a>
            </td>
            <td class="numerictablecolumn">            	            		
				<yfc:getXMLValue name="Queue" binding="xml:/Queue/@NumberOfAlertsAssigned"/>
            </td>
            <td class="numerictablecolumn">            	            		
				<yfc:getXMLValue name="Queue" binding="xml:/Queue/@NumberOfAlerts"/>
            </td>
        </tr>    
    </yfc:loopXML>
</tbody>
</table>
<%} else {%>
<table class="table">
    <tr align="center" valign="middle">
        <td>
            <yfc:i18n>You_are_not_subscribed_to_any_queues</yfc:i18n>
        </td>
    </tr>
</table>
<%}%>

