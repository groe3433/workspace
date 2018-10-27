<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/chargeutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<%@ include file="/console/jsp/paymentutils.jspf" %>
<%@page  import="com.yantra.yfc.ui.backend.util.*" %>

<%
computePaymentAmounts((YFCElement) request.getAttribute("Order"));
%>

<table class="anchor" cellpadding="7px">
<tr>
<td valign="top">
<table class="view" width="100%">
<tr>
<td class="detaillabel" ><yfc:i18n>Payment_Rule</yfc:i18n></td>
<td>
<select <%=yfsGetComboOptions("xml:/Order/@PaymentRuleId", "xml:/Order/AllowedModifications")%>>
<yfc:loopOptions binding="xml:/PaymentRuleList/@PaymentRuleId" name="PaymentRuleId" value="PaymentRuleId" selected="xml:/Order/@PaymentRuleId"/>
</select>
</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Total_Collected</yfc:i18n></td>
<td class="protectednumber">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>
<a <%=getDetailHrefOptions("L01", "", "")%>><%=getValue("Order", "xml:/Order/ChargeTransactionDetails/@TotalCredits")%></a>
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
<td class="detaillabel" ><yfc:i18n>Total_Refunded</yfc:i18n></td>
<td class="protectednumber">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>
<%=getValue("Order", "xml:/Order/ChargeTransactionDetails/@TotalRefunds")%>
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Total_Adjustments</yfc:i18n></td>
<td class="protectednumber">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/> <%=getValue("Order", "xml:/Order/@TotalAdjustmentAmount")%> <yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
<td class="detaillabel" ><yfc:i18n>Total_Cancelled</yfc:i18n></td>
<td class="protectednumber">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>
<%=getValue("Order", "xml:/Order/ChargeTransactionDetails/@TotalCancelled")%>
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
</tr>
<tr>
<% if(!equals(getParameter("ShowAuthorized"),"N")) {%>
<td class="detaillabel" ><yfc:i18n>Open_Authorized</yfc:i18n></td>
<td class="protectednumber">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/> <%=getValue("Order", "xml:/Order/ChargeTransactionDetails/@TotalOpenAuthorizations")%> <yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
<%} else {%>
<td colspan="2">&nbsp;</td>
<%}%>
<td colspan="2">&nbsp;</td>
</tr>

<% if(equals(getValue("Order", "xml:/Order/@OrderPurpose"), "EXCHANGE")){ %>
<tr>
<%
String prefixSymbol = getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol");
String postfixSymbol = getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol");

String fundsAvailFromReturn = prefixSymbol + " " + getValue("Order", "xml:/Order/ChargeTransactionDetails/@FundsAvailableFromReturn") + " " + postfixSymbol;

double dPendingTransferIn = getNumericValue("xml:/Order/@PendingTransferIn");

String pendingTransfer =  prefixSymbol + " " + getValue("Order", "xml:/Order/@PendingTransferIn") + " " + postfixSymbol;
String fundsAvailString = getFormatedI18N("Awaiting_Credit_For_Product_Receipt", new String[]{ pendingTransfer });
%>

<td class="detaillabel" ><yfc:i18n>Funds_Available_From_Return</yfc:i18n></td>
<td class="protectednumber">
<%=fundsAvailFromReturn%>
</td>

<%	if(!YFCCommon.equals(0, dPendingTransferIn) )	{	%>
<td class="protectedtext" colspan="2">
<%=fundsAvailString%>
</td>
<% } %>
</tr>
<% } %>
</table>
</td>
</tr>
<tr>
<td valign="top">
<table class="view" width="100%" cellSpacing="10">
<%  ArrayList paymentMethodsList = getLoopingElementList("xml:/Order/PaymentMethods/@PaymentMethod");
for (int PaymentMethodCounter = 0; PaymentMethodCounter < paymentMethodsList.size(); PaymentMethodCounter++) {

YFCElement singlePaymentMethod = (YFCElement) paymentMethodsList.get(PaymentMethodCounter);
pageContext.setAttribute("PaymentMethod", singlePaymentMethod);

/*The line directly below has been added to work around a bug in innerpanel.jsp.
Need to assess the impact of the proposed change to innerpanel.jsp before making
the fix and removing the line below.*/

request.removeAttribute(YFCUIBackendConsts.YFC_CURRENT_INNER_PANEL_ID);

String paymentType = getValue("PaymentMethod", "xml:/PaymentMethod/@PaymentType");
String paymentTypeGroup = "";
if(!isVoid(paymentType)){
YFCElement paymentTypeListElem = (YFCElement)request.getAttribute("PaymentTypeList");
for (Iterator i = paymentTypeListElem.getChildren(); i.hasNext();) {
YFCElement childElem = (YFCElement) i.next();
if(equals(paymentType, childElem.getAttribute("PaymentType"))){
paymentTypeGroup = childElem.getAttribute("PaymentTypeGroup");
break;
}
}
}
request.setAttribute("PaymentMethod", pageContext.getAttribute("PaymentMethod"));

preparePaymentMethodElement((YFCElement) request.getAttribute("PaymentMethod"));
%>
<!-- The IF condition below was added becos the Output XML of getOrderDetails API contained the <PaymentMethod> element
even if the Order did'nt have any Payment Methods Setup.This not an API bug.The blank <PaymentMethod> element is added by YFC
and it adds it becos one of the Additional API's that was getting called in the PaymentType view was accessing some attribute
of the PaymentMethod element and hence YFC adds a blank <PaymentMethod> element to the API output.
-->
<% if(!isVoid(resolveValue("xml:/PaymentMethod/@PaymentKey"))){%>
<tr>
<td addressip="true">
<jsp:include page="/yfc/innerpanel.jsp" flush="true">
<jsp:param name="CurrentInnerPanelID" value="I03"/>
<jsp:param name="PaymentMethodCounter" value='<%=String.valueOf(PaymentMethodCounter)%>'/>
<jsp:param name="Title" value='<%=getLocalizedPaymentType(paymentTypeGroup)%>'/>
<jsp:param name="Path" value="xml:/Order/PersonInfoBillTo"/>
<jsp:param name="AllowedModValue" value='N'/>
<jsp:param name="DataXML" value="Order"/>
</jsp:include>
</td>
</tr>
<%}
}%>
</table>
</td>
</tr>
</table>


