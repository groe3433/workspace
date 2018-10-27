<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<table cellSpacing=0 class="anchor" cellpadding="7px">
<tr>
<td height="100%" width="25%" addressip="true">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I01"/>
<jsp:param name="Path" value="xml:/Order/PersonInfoBillTo"/>
<jsp:param name="DataXML" value="Order"/>
<jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("BillToAddress", "xml:/Order/AllowedModifications")%>'/>
</jsp:include>
</td>
</tr>
</table>
