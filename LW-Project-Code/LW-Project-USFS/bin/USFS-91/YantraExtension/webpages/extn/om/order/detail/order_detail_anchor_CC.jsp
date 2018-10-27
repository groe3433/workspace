<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<%
	String strOrderNo = resolveValue("xml:/Order/@OrderNo");
%>

<table cellSpacing=0 class="anchor" cellpadding="7px">
	<tr>
	    <td colspan="4">
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
            <jsp:param name="ModifyView" value="true"/>
		    <jsp:param name="getRequestDOM" value="Y"/>
        </jsp:include>
    	</td>
	</tr>
	<tr>
	    <td colspan="4">
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
            <jsp:param name="ModifyView" value="true"/>
		    <jsp:param name="getRequestDOM" value="Y"/>
        </jsp:include>
    	</td>
	</tr>
	<tr>
	    <td colspan="4">
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I04"/>
            <jsp:param name="ModifyView" value="true"/>
		    <jsp:param name="getRequestDOM" value="Y"/>
        </jsp:include>
    	</td>
	</tr>
</table>