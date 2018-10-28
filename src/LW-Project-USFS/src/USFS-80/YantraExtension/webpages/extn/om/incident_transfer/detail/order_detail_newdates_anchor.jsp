<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<table cellSpacing=0 class="anchor" cellpadding="7px">
<tr>
    <td>
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td>
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
			<jsp:param name="ReqNameSpace" value="Order"/>
			<jsp:param name="Level" value="01"/>
        </jsp:include>
    </td>
</tr>
</table>