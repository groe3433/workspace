<%@ include file="/yfsjspcommon/yfsutil.jspf"%>

<%
String inputStr = request.getParameter("YFCInput");
YFCDocument changeOrderInDoc = null;
YFCElement changeOrderInElem = null;
YFCElement templateElem = null;

if (!isVoid(inputStr)) {
changeOrderInDoc = YFCDocument.parse(inputStr);
changeOrderInElem = changeOrderInDoc.getDocumentElement();
templateElem = YFCDocument.parse("<Order OrderHeaderKey=\"\"/>").getDocumentElement();
}
%>

<yfc:callAPI apiName="changeOrder" inputElement="<%=changeOrderInElem%>"
templateElement="<%=templateElem%>" outputNamespace=""/>

<%
YFCElement outElem = (YFCElement)request.getAttribute("Order");
response.getWriter().write(outElem.getString());
response.getWriter().flush();
response.getWriter().close();
%>
