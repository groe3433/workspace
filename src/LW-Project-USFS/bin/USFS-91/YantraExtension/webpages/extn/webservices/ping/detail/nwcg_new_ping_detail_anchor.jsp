<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<%
if(request.getAttribute("Receipt") != null)
{ 
	setHistoryFlags((YFCElement) request.getAttribute("Receipt"));
}
%>
<table cellSpacing=0 class="anchor" cellpadding="7px">
<tr>
    <td colspan="4">
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
		    <jsp:param name="getRequestDOM" value="Y"/>
        </jsp:include>
    </td>
<tr>
</table>