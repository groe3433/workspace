<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.awt.*" %>
<%@ page import="java.lang.*" %>
<%@ page import="java.lang.Object.*" %>
<%@ page import="com.yantra.yfc.dom.*" %>
<%@ page import="com.yantra.yfc.util.*" %>


<%
YFCElement rootElement = (YFCElement)request.getAttribute("Order");
if(rootElement != null){
YFCNodeList chargeTransactionDetailsNodeList = rootElement.getElementsByTagName("ChargeTransactionDetails");
if(chargeTransactionDetailsNodeList.getLength() > 0){
YFCElement chargeTransactionDetailsElement= (YFCElement)chargeTransactionDetailsNodeList.item(0);
if(chargeTransactionDetailsElement != null){
double dTotalOpenAuthorizations =0;
double dTotalCredits =0;
double dBalance =0;
dTotalOpenAuthorizations = chargeTransactionDetailsElement.getDoubleAttribute("TotalOpenAuthorizations");
dTotalCredits = chargeTransactionDetailsElement.getDoubleAttribute("TotalCredits");
dBalance= dTotalOpenAuthorizations - dTotalCredits;
chargeTransactionDetailsElement.setDoubleAttribute("Balance", dBalance);
}
}
}

%>
