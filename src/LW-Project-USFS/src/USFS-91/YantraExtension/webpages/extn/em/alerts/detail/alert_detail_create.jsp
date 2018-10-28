<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<script language="Javascript" >
	IgnoreChangeNames();
	yfcDoNotPromptForChanges(true);
</script>

<%
    // Default the enterprise code
    String enterpriseCode = (String) request.getParameter("xml:/Inbox/@EnterpriseCode");
    if (isVoid(enterpriseCode)) {
        enterpriseCode = getValue("CurrentOrganization", "xml:CurrentOrganization:/Organization/@PrimaryEnterpriseKey");
        request.setAttribute("xml:/Inbox/@EnterpriseCode", enterpriseCode);
    }

%>

<table class="view" width="100%">
    <jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
        <jsp:param name="ScreenType" value="detail"/>
		<jsp:param name="ShowDocumentType" value="true"/>
		<jsp:param name="ShowEnterprise" value="true"/>	 
		<jsp:param name="ShowNode" value="true"/>
        <jsp:param name="RefreshOnDocumentType" value="true"/>
        <jsp:param name="RefreshOnEnterpriseCode" value="true"/>
	    <jsp:param name="RefreshOnNode" value="false"/>
		<jsp:param name="NodeBinding" value="xml:/Inbox/@ShipnodeKey"/>
		<jsp:param name="EnterpriseListForNodeField" value="false"/>
		<jsp:param name="EnterpriseCodeBinding" value="xml:/Inbox/@EnterpriseKey"/>
    </jsp:include>
    <% // Now call the APIs that are dependent on the common fields (Doc Type & Enterprise Code) %>
    <yfc:callAPI apiID="AP1"/>
    <yfc:callAPI apiID="AP2"/>
    <yfc:callAPI apiID="AP3"/>

        <td class="detaillabel" ><yfc:i18n>Alert_Type</yfc:i18n></td>
        <td>
            <select class="combobox" <%=getComboOptions("xml:/Inbox/@ExceptionType")%>>
                <yfc:loopOptions binding="xml:ExceptionTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
                value="CodeValue" isLocalized="Y"/>
            </select>
        </td>
        <td class="detaillabel" ><yfc:i18n>Queue</yfc:i18n></td>
        <td>
            <select class="combobox" <%=getComboOptions("xml:/Inbox/@QueueId")%>>
                <yfc:loopOptions binding="xml:QueueList:/QueueList/@Queue" name="QueueName"
                value="QueueId"/>
            </select>
        </td>
		<td/>
    </tr>
    <tr>
        <td class="detaillabel"><yfc:i18n>Description</yfc:i18n></td>
        <td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Inbox/@Description")%>/>
        </td>
        <td class="detaillabel"><yfc:i18n>Detail_Description</yfc:i18n></td>
		 <td>
			<textarea rows="3" style="width:100%" <%=getTextAreaOptions("xml:/Inbox/@DetailDescription")%>></textarea>
         </td>
		<td/>
    </tr>
</table>
