<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf" %>
<%@ page import="com.yantra.yfc.core.*" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/exceptionutils.js"></script>

<script language="javascript">
    function showAssignToUserPopup(viewId) {
        return doEMPopupDialog('<%=getDefaultDetailViewForGroup("YEMD011")%>',440,180);
    }
</script>
<script language="javascript">
    function showAssignToQueuePopup(viewId) {
        return doEMPopupDialog('<%=getDefaultDetailViewForGroup("YEMD012")%>',440,180);
    }
        
</script>

<%
YFCElement errorElement = getElement("Order");
if (errorElement != null){
	String errorDesc = errorElement.getAttribute("CustomErrorDesc");
	if (errorDesc != null && (errorDesc.length() > 0)){
%>
		<table class="simpletable" width="100%">
				<tr>
						<td class="errortext">Alert processing failed: <%=errorDesc%> </td>
				</tr>
		</table>
<%
	}
}

%>
<%String queueName = "";%>

	<yfc:loopXML binding="xml:/InboxList/@Inbox" id="Inbox">
		<%queueName = resolveValue("xml:/Inbox/Queue/@QueueDescription");%>
	</yfc:loopXML>


<%
String displayIssue = "NO";

if (queueName.equals("NWCG_ISSUE_FAILURE") ||
	queueName.equals("NWCG_ISSUE_RADIOS_SUCCESS") ||
	queueName.equals("NWCG_ISSUE_RADIOS_FAILURE") ||
	queueName.equals("NWCG_ISSUE_SUCCESS")){
	displayIssue = "YES";
}
%>



<table class="table" editable="false" width="100%" cellspacing="0">
    <thead> 
        <tr>
            <td sortable="no" class="checkboxheader">
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);" />
            </td>
            
    <td class="tablecolumnheader"><yfc:i18n>Alert_ID</yfc:i18n></td>            
    <td class="tablecolumnheader"><yfc:i18n>Alert_Type</yfc:i18n></td>            
    <td class="tablecolumnheader"><yfc:i18n>Description</yfc:i18n></td>            
    <td class="tablecolumnheader"><yfc:i18n>Queue</yfc:i18n></td>            
    <td class="tablecolumnheader"><yfc:i18n>Priority</yfc:i18n></td>            
    <td class="tablecolumnheader"><yfc:i18n>Assigned_To_User</yfc:i18n></td>
    <td class="tablecolumnheader"><yfc:i18n>Alert_Status</yfc:i18n></td>            
    <td class="tablecolumnheader"><yfc:i18n>Last_Raised_On</yfc:i18n></td>
    <%
    if (displayIssue.equals("YES")){
    %>
    <td class="tablecolumnheader"><yfc:i18n>Issue_No</yfc:i18n></td>
    <%}%>
    <td class="tablecolumnheader"><yfc:i18n>Incident_No</yfc:i18n></td>            
    <td class="tablecolumnheader"><yfc:i18n>Incident_Name</yfc:i18n></td>
    <td class="tablecolumnheader"><yfc:i18n>Requested_Delivery_Date</yfc:i18n></td>
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

				String exceptionType = getValue("Inbox","xml:/Inbox/@ExceptionType");
				%>

                <td class="checkboxcolumn" > 
                    <input type="checkbox" value='<%=getParameter("inboxKey")%>' name="EntityKey"/>
                </td>
                <td class="tablecolumn"><a href="javascript:showDetailFor('<%=getParameter("inboxKey")%>');">
                    <yfc:getXMLValue binding="xml:/Inbox/@InboxKey"/></a>
                </td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/Inbox/@ExceptionType"/></td>
				<input type="hidden" name="xml:/Inbox/@ExceptionType" value="<%=exceptionType%>" id="xml:/Inbox/@ExceptionType"/>

                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/Inbox/@Description"/></td>
                <td class="tablecolumn"><yfc:getXMLValueI18NDB binding="xml:/Inbox/Queue/@QueueDescription"/></td>
				<input type="hidden" name="xml:/Inbox/Queue/@QueueDescription" value="<%=resolveValue("xml:/Inbox/Queue/@QueueDescription")%>" id="xml:/Inbox/Queue/@QueueDescription"/>
                <td class="tablecolumn" sortValue="<%=getNumericValue("xml:/Inbox/@Priority")%>"><yfc:getXMLValue binding="xml:/Inbox/@Priority"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/Inbox/User/@Loginid"/></td>
				<td class="tablecolumn"><yfc:i18n><%=alertStatus%></yfc:i18n></td>
                <td class="tablecolumn" sortValue="<%=getDateValue("xml:/Inbox/@LastOccurredOn")%>"><yfc:getXMLValue binding="xml:/Inbox/@LastOccurredOn"/></td>
				<% String incidentNo = "";
				   String irName = "";
				   String ohk = "";
				   String issueDetailLink ="";
				   String issueNo = "";
				%>
				<yfc:loopXML binding="xml:Inbox:/Inbox/InboxReferencesList/@InboxReferences" id="InboxReferences">
					<%
						irName = resolveValue("xml:InboxReferences:/InboxReferences/@Name");
						
						if (!(exceptionType.equals("UIEXCEPTION"))) {						
							if (irName.equals("Incident Number")) {
								incidentNo = resolveValue("xml:InboxReferences:/InboxReferences/@Value");
							}
							else if (irName.startsWith("OrderHeaderKey")) {
								ohk = resolveValue("xml:InboxReferences:/InboxReferences/@Value");
							}
							else if (irName.startsWith("Issue Detail Link")) {
								issueDetailLink = resolveValue("xml:InboxReferences:/InboxReferences/@Value");
								String ohkInLink = "OrderHeaderKey&#39;,&#39;";
								String endOfLink = "&#39;&#41;&#59;";
								int beg = issueDetailLink.lastIndexOf(ohkInLink);
								int end = issueDetailLink.lastIndexOf(endOfLink);								
								
								int begIndx = beg+ohkInLink.length();
								int endIndx = end;
								ohk = issueDetailLink.substring(begIndx, endIndx);
							}
							else if ((irName.equals("Order No")) || irName.startsWith("Issue Number")){
								issueNo = resolveValue("xml:InboxReferences:/InboxReferences/@Value");
							}
						}						
					%>
					
				</yfc:loopXML>
				
			    <%
			    if (displayIssue.equals("YES")){
			    %>
			    <td class="tablecolumn"><%=issueNo%></td>
			    <%}%>

				<td class="tablecolumn"><%=incidentNo%></td>
				<%
					YFCDocument inputDoc = YFCDocument.parse("<Order OrderHeaderKey=\""+ohk+"\"/>");
					YFCDocument templateDoc = 
					YFCDocument.parse("<Order OrderNo=\"\" ReqDeliveryDate=\"\"><Extn ExtnIncidentName=\"\"/></Order>");
				
					if (!(exceptionType.equals("UIEXCEPTION")) && !(ohk.equals("")) ) {
				%>
					<yfc:callAPI apiName='getOrderDetails' inputElement='<%=inputDoc.getDocumentElement()%>'
					templateElement='<%=templateDoc.getDocumentElement()%>'
					outputNamespace='RelatedOrderDetails'/>

					<td class="tablecolumn"><%=resolveValue("xml:RelatedOrderDetails:/Order/Extn/@ExtnIncidentName")%></td>
					<td class="tablecolumn"><%=getDateOrTimePart("YFCDATE", resolveValue("xml:RelatedOrderDetails:/Order/@ReqDeliveryDate"))%> <%=getDateOrTimePart("YFCTIME", resolveValue("xml:RelatedOrderDetails:/Order/@ReqDeliveryDate"))%></td>					
				<%} else {%>
				<td class="tablecolumn"/>
				<td class="tablecolumn"/>
				<%}%>
                
            </tr>
        </yfc:loopXML> 
   </tbody>
</table>
