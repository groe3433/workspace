<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/chargeutils.jspf" %>
<%@ include file="/console/jsp/paymentutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<%
String PaymentMethodCounter = getParameter("PaymentMethodCounter");
String paymentType = getValue("PaymentMethod", "xml:/PaymentMethod/@PaymentType");
String paymentTypeGroup = "";
boolean disableUnlimitedCharges = false;
if(!isVoid(paymentType)){
YFCElement paymentTypeListElem = (YFCElement)request.getAttribute("PaymentTypeList");
for (Iterator i = paymentTypeListElem.getChildren(); i.hasNext();) {
YFCElement childElem = (YFCElement) i.next();
if(equals(paymentType, childElem.getAttribute("PaymentType"))){
paymentTypeGroup = childElem.getAttribute("PaymentTypeGroup");
disableUnlimitedCharges = childElem.getBooleanAttribute("ChargeUpToAvailable");
break;
}
}
}
String incomplete = getValue("PaymentMethod", "xml:/PaymentMethod/@IncompletePaymentType");
String creditCardEncrypted = isAttributeEncrypted((YFCElement) request.getAttribute("CreditCardRule"));
String storedValueCardEncrypted = isAttributeEncrypted((YFCElement) request.getAttribute("StoredValueCardRule"));
String customerAccountEncrypted = isAttributeEncrypted((YFCElement) request.getAttribute("CustomerAccountRule"));
String otherPaymentTypeEncrypted = isAttributeEncrypted((YFCElement) request.getAttribute("OtherPaymentTypeRule"));

String path = "xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter;
String viewType = "view";
if(equals(resolveValue("xml:/PaymentMethod/@SuspendAnyMoreCharges"),"Y") ||
equals(resolveValue("xml:/PaymentMethod/@SuspendAnyMoreCharges"),"B"))
viewType = "disabledview";
%>

<table class="<%=viewType%>" width="100%">
<tr>
<td valign="top">
<table class="view" width="100%">
<tr>
<td>
<yfc:makeXMLInput name="paymentKey">
<yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey"/>
<yfc:makeXMLKey binding="xml:/Order/PaymentMethods/PaymentMethod/@PaymentKey" value="xml:/PaymentMethod/@PaymentKey"/>
</yfc:makeXMLInput>
<td class="checkboxcolumn" >
<input type="hidden" value='<%=getParameter("paymentKey")%>' name="<%=getDefaultEntityKeyName()%>"/>
<input type="hidden" value='N' yName="AllowedModValue"/>
<input type="hidden" <%=getTextOptions("xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter + "/@PaymentKey","xml:/PaymentMethod/@PaymentKey")%>/>
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Payment_Type</yfc:i18n></td>
<td class="protectedtext">				<%=getComboText("xml:/PaymentTypeList/@PaymentType","PaymentTypeDescription","PaymentType","xml:/PaymentMethod/@PaymentType",true)%>
</td>
</tr>
<% if (equals(paymentTypeGroup,"CREDIT_CARD")) {  %>
<tbody>
<jsp:include page="/om/order/detail/order_detail_paymenttype_creditcard.jsp" flush="true">
<jsp:param name="Incomplete" value='<%=incomplete%>'/>
<jsp:param name="CreditCardEncrypted" value='<%=creditCardEncrypted%>'/>
<jsp:param name="Path" value='<%=path%>'/>
</jsp:include>
</tbody>
<% } else if (equals(paymentTypeGroup,"CUSTOMER_ACCOUNT")) { %>
<tbody>
<jsp:include page="/om/order/detail/order_detail_paymenttype_account.jsp" flush="true">
<jsp:param name="Incomplete" value='<%=incomplete%>'/>
<jsp:param name="CustomerAccountEncrypted" value='<%=customerAccountEncrypted%>'/>
<jsp:param name="Path" value='<%=path%>'/>
</jsp:include>
</tbody>
<% } else if (equals(paymentTypeGroup,"CHECK")) { %>
<tbody>
<jsp:include page="/om/order/detail/order_detail_paymenttype_check.jsp" flush="true">
<jsp:param name="Incomplete" value='<%=incomplete%>'/>
<jsp:param name="OtherPaymentTypeEncrypted" value='<%=otherPaymentTypeEncrypted%>'/>
<jsp:param name="Path" value='<%=path%>'/>
</jsp:include>
</tbody>
<% } else if (equals(paymentTypeGroup,"STORED_VALUE_CARD")) { %>
<tbody>
<jsp:include page="/om/order/detail/order_detail_paymenttype_svc.jsp" flush="true">
<jsp:param name="Incomplete" value='<%=incomplete%>'/>
<jsp:param name="StoredValueCardEncrypted" value='<%=storedValueCardEncrypted%>'/>
<jsp:param name="Path" value='<%=path%>'/>
</jsp:include>
</tbody>
<% } else  { %>
<tbody>
<jsp:include page="/om/order/detail/order_detail_paymenttype_check.jsp" flush="true">
<jsp:param name="Incomplete" value='<%=incomplete%>'/>
<jsp:param name="OtherPaymentTypeEncrypted" value='<%=otherPaymentTypeEncrypted%>'/>
<jsp:param name="Path" value='<%=path%>'/>
</jsp:include>
</tbody>
<% } %>
</table>
</td>
<td valign="top">
<table class="view" width="100%">
<tr>
<td class="detaillabel" ><yfc:i18n>Charge_Sequence</yfc:i18n></td>
<td>
<input type="text" <%=yfsGetTextOptions("xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter + "/@ChargeSequence","xml:/PaymentMethod/@ChargeSequence","xml:/Order/AllowedModifications")%>/>
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Unlimited_Charges</yfc:i18n></td>
<td>
<% if (disableUnlimitedCharges) { %>
<input class="checkbox" type="checkbox" disabled="true"  <%=getCheckBoxOptions("xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter + "/@UnlimitedCharges","xml:/PaymentMethod/@UnlimitedCharges","Y")%>/>
<%} else { %>
<input class="checkbox" type="checkbox" <%=yfsGetCheckBoxOptions("xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter + "/@UnlimitedCharges","xml:/PaymentMethod/@UnlimitedCharges","Y","xml:/Order/AllowedModifications")%>/>
<% } %>
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Max_Charge_Limit</yfc:i18n></td>
<td>
<input type="text" <%=yfsGetTextOptions("xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter + "/@MaxChargeLimit","xml:/PaymentMethod/@MaxChargeLimit","xml:/Order/AllowedModifications")%>/>
</td>
</tr>
</table>
</td>
<td valign="top">
<table class="view" width="100%">
<%
double dAmount = getNumericValue("xml:/PaymentMethod/@TotalCharged") - getNumericValue("xml:/PaymentMethod/@TotalRefundedAmount");
%>
<tr>
<td class="detaillabel" ><yfc:i18n>Collected_Amount</yfc:i18n></td>
<td class="protectednumber">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>
<%=getFormattedDouble(dAmount)%>
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Refunded_Amount</yfc:i18n></td>
<td class="protectednumber">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>
<yfc:getXMLValue name="PaymentMethod" binding="xml:/PaymentMethod/@TotalRefundedAmount"/>
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
</tr>
<% if(!equals(getParameter("ShowAuthorized"),"N")) {%>
<tr>
<td class="detaillabel" ><yfc:i18n>Authorized_Amount</yfc:i18n></td>
<td class="protectednumber">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>
<yfc:getXMLValue name="PaymentMethod" binding="xml:/PaymentMethod/@TotalAuthorized"/>
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
</tr>
<%}%>
<tr>
<td class="detaillabel" ><yfc:i18n>Awaiting_Collections</yfc:i18n></td>
<td class="protectednumber">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>
<yfc:getXMLValue name="PaymentMethod" binding="xml:/PaymentMethod/@CombinedCharges"/>
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
</tr>
<% if(!equals(getParameter("ShowAuthorized"),"N")) {%>
<tr>
<td class="detaillabel" ><yfc:i18n>Awaiting_Authorizations</yfc:i18n></td>
<td class="protectednumber">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>
<yfc:getXMLValue name="PaymentMethod" binding="xml:/PaymentMethod/@CombinedAuthorizations"/>
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
</tr>
<%}%>
</table>

<%	//	address.jsp included to help 'Show Bill To Address' icon.  Modify_Address jsp requires that the address
// is present in parent window.. hence hidden address

String personInfoElement = "xml:/Order/PersonInfoBillTo";
String sDataXML = "Order";
if(!isVoid(resolveValue("xml:/PaymentMethod/PersonInfoBillTo/@PersonInfoKey") ) )	{
personInfoElement = "xml:/PaymentMethod/PersonInfoBillTo";
sDataXML = "PaymentMethod";
}
%>
<jsp:include page="/yfsjspcommon/address.jsp" flush="true">
<jsp:param name="Path" value="<%=personInfoElement%>"/>
<jsp:param name="AllowedModValue" value='N'/>
<jsp:param name="DataXML" value="<%=sDataXML%>"/>
<jsp:param name="style" value='display:none'/>
</jsp:include>
</td>

</tr>
<tr>
<td class="detaillabel" colspan="3" >
<FIELDSET>
<LEGEND class="detaillabel"><yfc:i18n>Payment_Type_Status</yfc:i18n></LEGEND>
<table class="view" width="100%">
<tr>
<td>
<input type="radio" class="radiobutton" <%if (equals(resolveValue("xml:/PaymentMethod/@SuspendAnyMoreCharges"),"Y")) {%> CHECKED <%}%>
<%=yfsGetRadioOptions("xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter + "/@SuspendAnyMoreCharges","","Y","xml:/Order/AllowedModifications")%>>
<yfc:i18n>Suspended_For_Charge</yfc:i18n>
</td>
<td>
<input type="radio" class="radiobutton" <%if (equals(resolveValue("xml:/PaymentMethod/@SuspendAnyMoreCharges"),"B")) {%> CHECKED <%}%>
<%=yfsGetRadioOptions("xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter + 	"/@SuspendAnyMoreCharges","","B","xml:/Order/AllowedModifications")%>>
<yfc:i18n>Suspended_For_Charge_And_Refund</yfc:i18n>
</td>
<td>
<input type="radio" class="radiobutton" <%if (equals(resolveValue("xml:/PaymentMethod/@SuspendAnyMoreCharges"),"N")) {%> CHECKED <%}%>
<%=yfsGetRadioOptions("xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter + "/@SuspendAnyMoreCharges","","N","xml:/Order/AllowedModifications")%>>
<yfc:i18n>Active</yfc:i18n>
</td>
</tr>
</table>
</FIELDSET>
</td>
</tr>
</table>
