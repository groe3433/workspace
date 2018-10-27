<%@ include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="org.w3c.dom.Element" %>
<%@ page import="org.w3c.dom.NodeList" %>

<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<%
System.out.println("Anchor request DOM ==>> "+ getRequestDOM());
if(request.getAttribute("NWCGBillingTransaction") != null)
{ 
	//System.out.println("Anchor request DOM ==>> "+ getRequestDOM());
	System.out.println("request.getAttribute not null");
	setHistoryFlags((YFCElement) request.getAttribute("NWCGBillingTransaction"));
}
%>
<table cellSpacing=0 class="anchor" cellpadding="7px">
<tr>
    <td colspan="4">
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
		    <jsp:param name="getRequestDOM" value="Y"/>
			<jsp:param name="RootNodeName" value="NWCGBillingTransaction"/>
        </jsp:include>
    </td>
</tr>

</table>