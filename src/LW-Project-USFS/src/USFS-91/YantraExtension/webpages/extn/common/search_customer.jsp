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

<td colspan="3" class="detaillabel" ><yfc:i18n>Customer_Id</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" name="xml:/Customer/@CustomerID"  id="xml:/Customer/@CustomerID" onblur="javascript:fetchDataFromServer(this,'getCustomerList',displayCustomerList)"/>
<img class="lookupicon" onclick="callLookup(this,'<%=sLookup%>');" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Customer") %> />
</td>
<td/><input type=submit value="Load" onclick="fetchDataFromServer(this,'getCustomerList',displayCustomerList)"/>
</tr>
</table>