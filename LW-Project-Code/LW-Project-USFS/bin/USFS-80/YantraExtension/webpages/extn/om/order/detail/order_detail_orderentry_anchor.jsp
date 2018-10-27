<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%
String strDocumentType = resolveValue("xml:/Order/@DocumentType");
	boolean bOtherOrder = false ;
	if(strDocumentType != null && (!strDocumentType.equals("0001")))
	{
		bOtherOrder = true ;
	}
%>
<table class="anchor" cellpadding="7px"  cellSpacing="0" >
<tr>
<td colspan="2">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I01"/>
</jsp:include>
</td>
</tr>

<%
if(isVoid(resolveValue("xml:/ReturnOrder/@ReturnOrderHeaderKeyForExchange"))){
%>
<tr>
<td height="100%" width="50%" addressip="true">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I02"/>
<jsp:param name="Path" value="xml:/Order/PersonInfoShipTo"/>
<jsp:param name="DataXML" value="Order"/>
<jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("ShipToAddress", "xml:/Order/AllowedModifications")%>'/>
</jsp:include>
</td>
<td height="100%" width="50%" addressip="true">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I03"/>
<jsp:param name="Path" value="xml:/Order/PersonInfoBillTo"/>
<jsp:param name="DataXML" value="Order"/>
<jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("BillToAddress", "xml:/Order/AllowedModifications")%>'/>
</jsp:include>
</td>
</td>
</tr>
<%if(!bOtherOrder){%>
<tr>
<td height="100%" width="50%" addressip="true">
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I04"/>
<jsp:param name="Path" value="xml:/Order/AdditionalAddresses/AdditionalAddress/PersonInfo"/>
<jsp:param name="DataXML" value="Order"/>
<jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("BillToAddress", "xml:/Order/AllowedModifications")%>'/>
</jsp:include>
</td>
</tr>
<%}%>
<%	} %>

</table>