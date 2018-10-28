<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<% setHistoryFlags((YFCElement) request.getAttribute("OrderLine")); %>
<yfc:callAPI apiID='AP1'/> <% /* ChainedOrderLineList */ %>
<yfc:callAPI apiID='AP2'/> <% /* DerivedOrderLineList */ %>

<% prepareRelatedOrderLines((YFCElement) request.getAttribute("OrderLine"),(YFCElement) request.getAttribute("ChainedFromOrderLine"),(YFCElement) request.getAttribute("DerivedFromOrderLine"),(YFCElement) request.getAttribute("ChainedOrderLineList"),(YFCElement) request.getAttribute("DerivedOrderLineList")); %>

<table class="anchor" cellpadding="7px" cellSpacing="0">
<tr>
<td>
<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
<jsp:param name="CurrentInnerPanelID" value="I01"/>
</jsp:include>
</td>
</tr>

<%  ArrayList docTypeList = getLoopingElementList("xml:/OrderLine/RelatedDocuments/FinalRelatedDocuments/@DocumentType");
for (int DocumentTypeCounter = 0; DocumentTypeCounter < docTypeList.size(); DocumentTypeCounter++) {

YFCElement singleDocType = (YFCElement) docTypeList.get(DocumentTypeCounter);
pageContext.setAttribute("DocumentType", singleDocType);

request.setAttribute("DocumentType", pageContext.getAttribute("DocumentType"));

setAdditionalAttr(singleDocType, (YFCElement) request.getAttribute("DocumentParamsList"));
String sDocumentType = singleDocType.getAttribute("DocumentTypeDescription") + " " + getI18N("Lines");
String sIsReturnService = singleDocType.getAttribute("isReturnDocument");
%>
<tr>
<td>
<jsp:include page="/yfc/innerpanel.jsp" flush="true">
<jsp:param name="CurrentInnerPanelID" value="I02"/>
<jsp:param name="DocumentTypeCounter" value='<%=String.valueOf(DocumentTypeCounter)%>'/>
<jsp:param name="Title" value='<%=sDocumentType%>'/>
<jsp:param name="IsReturnService" value='<%=sIsReturnService%>'/>
</jsp:include>
</td>
</tr>
<% } %>
</table>
