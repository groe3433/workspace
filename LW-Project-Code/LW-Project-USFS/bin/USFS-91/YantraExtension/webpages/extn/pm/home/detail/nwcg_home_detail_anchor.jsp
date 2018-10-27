<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/pm.js">
    var contextPath = "<%=request.getContextPath()%>";
</script>
<%
    String moreAlertsFlag="N";
    int totalRecords = 0;
    YFCElement rootElement = (YFCElement)request.getAttribute("InboxList");
	if(rootElement != null){
        totalRecords = rootElement.getIntAttribute("TotalNumberOfRecords");

        if(totalRecords > 0)
            rootElement.setAttribute("AlertDetailFlag", "Y");

        if(totalRecords > 15 )
            moreAlertsFlag="Y";
    }

    rootElement.setAttribute("MoreAlertsFlag", moreAlertsFlag);
%>
<table height="100%" border="0" cellSpacing="0" class="anchor" cellpadding="7px" >
<tr>
    <td width="20%" align="left" class="searchlabel" >&nbsp;
    	<%/* This could be a problem during localization, but how to welcome without a comma! */%>
		<%=getFormatedI18N("Welcome_User_Message", getValue((YFCElement)session.getAttribute("CurrentUser"),"xml:/User/@Username"))%>&nbsp;[<%=getValue((YFCElement)session.getAttribute("CurrentUser"),"xml:/User/@OrganizationKey")%>]

    </td>
	<td width="60%" align="center" class="warninglabel" >&nbsp;

	<% if ( getPasswordExpiresDays() != null && getPasswordChangeLink() != null  ) {%>
		<%= getFormatedI18N("Password_Expires_In_Days",getPasswordExpiresDays()) %>
			<a  href='' onclick='openPasswordChangeWindow("<%=getPasswordChangeLink()%>");return false;' ><%=getI18N("Goto_Password_Expired_Link") %></a>
	<% }else if ( getPasswordExpiresDays() != null  ) { %>
		<%= getFormatedI18N("Password_Expires_In_Days",getPasswordExpiresDays()) %>
	<% }else if ( getPasswordChangeLink() != null  ) { %>
			<a  href='' onclick='openPasswordChangeWindow("<%=getPasswordChangeLink()%>");return false;' ><%=getI18N("Goto_Password_Expired_Link") %></a>	
	<% } %>

	</td>
    <td width="40%" align="right" class="detaillabel"> 
        <%=getTodayDate()%>&nbsp;
    </td>
</tr>
<tr>
    <td width="20%">
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I03"/>
            <jsp:param name="IPHeight" value="360px" />
        </jsp:include>    
    </td>
    <td width="60%">
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
            <jsp:param name="IPHeight" value="360px" />
        </jsp:include>    
    </td>
    <td width="20%">
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
            <jsp:param name="IPHeight" value="360px" />
        </jsp:include>    
    </td>
</tr>
</table>
	