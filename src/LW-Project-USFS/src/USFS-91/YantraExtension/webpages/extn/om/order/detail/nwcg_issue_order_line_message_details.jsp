<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/orderentry.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.dom.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/extn.js"></script>
<%
String sLookup = "NWCGCustomerLookUp";
%>
<script language="Javascript" >
IgnoreChangeNames();
yfcDoNotPromptForChanges(true);

function displayCustomerList(elem,xmlDoc){
	return updateAddressWithoutPath(elem,xmlDoc,"BillingPersonInfo");
}
</script>
<table class="view" width="100%">
<tr>

<td colspan="4" class="detaillabel" >
<label><font color="red">One of the Order Lines Qty (Issued + UTF + Backordered + Forwarded) does not equal the Requested Qty.</font></label>
</td>

<td/>
</tr>
</table>