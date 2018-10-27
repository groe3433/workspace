<%@include file="/yfsjspcommon/yfsutil.jspf" %>
<%@include file="/console/jsp/order.jspf" %>
<%@ include file="/extn/yfsjspcommon/nwcg_extended_editable_util_lines.jspf" %>
<%@ page import="java.util.*" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.ResourceUtil" %>

<table class="anchor" cellpadding="7px" cellSpacing="0">
<tr>
<td colspan="3">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I01"/>
<jsp:param name="getRequestDOM" value="Y"/>
<jsp:param name="ModifyView" value="true"/>
</jsp:include>
</td>
</tr>
</table>