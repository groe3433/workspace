<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@taglib  prefix="yfc" uri="/WEB-INF/yfc.tld" %>


<table class="anchor" cellpadding="7px"  cellSpacing="0" >
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
        </jsp:include>
    </td>
</tr>
</table>
