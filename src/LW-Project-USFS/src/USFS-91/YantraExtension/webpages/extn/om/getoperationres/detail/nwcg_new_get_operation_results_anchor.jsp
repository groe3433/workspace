<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<script language="javascript">
	IgnoreChangeNames();
	yfcDoNotPromptForChanges(true);
</script>

<table class="anchor" cellpadding="7px"  cellSpacing="0" >
<tr>
    <td colspan="2">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="010"/>
        </jsp:include>
    </td>
</tr>
</table>