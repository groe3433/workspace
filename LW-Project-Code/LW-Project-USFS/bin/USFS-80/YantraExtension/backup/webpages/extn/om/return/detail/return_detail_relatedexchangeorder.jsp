<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/order.jspf" %>
<%@include file="/console/jsp/paymentutils.jspf" %>
<%@ page import="com.yantra.util.*" %>

<%
computePaymentAmounts((YFCElement) request.getAttribute("ExchangeOrder"));
%>
<table class="view" width="100%">
<tr>
<yfc:makeXMLInput name="orderKey">
<yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/ExchangeOrder/@OrderHeaderKey" ></yfc:makeXMLKey>
</yfc:makeXMLInput>
<td class="detaillabel" ><yfc:i18n>0001_Order_#</yfc:i18n></td>
<td class="protectedtext">
<a <%=getDetailHrefOptions("L01",getParameter("orderKey"),"")%>>
<yfc:getXMLValue binding="xml:/ExchangeOrder/@OrderNo"></yfc:getXMLValue>
</a>&nbsp;
</td>
<td class="detaillabel" ><yfc:i18n>Exchange_Type</yfc:i18n></td>
<td class="protectedtext">	<%=getComboText("xml:ExchangeTypeList:/CommonCodeList/@CommonCode","CodeShortDescription","CodeValue","xml:/ExchangeOrder/@ExchangeType",true)%>
</td>
<td class="detaillabel" ><yfc:i18n>Status</yfc:i18n></td>
<td class="protectedtext">
<% if (isVoid(getValue("ExchangeOrder", "xml:/ExchangeOrder/@Status"))) {%>
[<yfc:i18n>Draft</yfc:i18n>]
<% } else { %>
<a <%=getDetailHrefOptions("L02", getParameter("orderKey"), "ShowReleaseNo=Y")%>><%=displayOrderStatus(getValue("ExchangeOrder","xml:/ExchangeOrder/@MultipleStatusesExist"),getValue("ExchangeOrder","xml:/ExchangeOrder/@MaxOrderStatusDesc"))%></a>
<% } %>
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/ExchangeOrder/@EnterpriseCode"/></td>
<td class="detaillabel" ><yfc:i18n>Buyer</yfc:i18n></td>
<td class="protectedtext">
<yfc:getXMLValue binding="xml:/ExchangeOrder/@BuyerOrganizationCode"/>
</td>
<td class="detaillabel" ><yfc:i18n>Seller</yfc:i18n></td>
<td class="protectedtext">
<yfc:getXMLValue binding="xml:/ExchangeOrder/@SellerOrganizationCode"/>
</td>
</tr>
<tr>
<%
String prefixSymbol = getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol");
String postfixSymbol = getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol");

String fundsAvailFromReturn = prefixSymbol + " " + getValue("ExchangeOrder", "xml:/ExchangeOrder/ChargeTransactionDetails/@FundsAvailableFromReturn") + " " + postfixSymbol;

double dPendingTransferIn = getNumericValue("xml:/ExchangeOrder/@PendingTransferIn");

String pendingTransfer =  prefixSymbol + " " + getValue("ExchangeOrder", "xml:/ExchangeOrder/@PendingTransferIn") + " " + postfixSymbol;

String fundsAvailString = getFormatedI18N("Awaiting_Credit_For_Product_Receipt", new String[]{ pendingTransfer });

%>

<td class="detaillabel" ><yfc:i18n>Funds_Available_From_Return</yfc:i18n></td>
<td class="protectedtext" colspan="4">
<%=fundsAvailFromReturn%>&nbsp;<%	if(!YFCCommon.equals(0, dPendingTransferIn) )	{	%><%=fundsAvailString%><% } %>
</td>
</tr>
</table>

<table class="table" >
<tbody>
<tr>
<td width="2%">&nbsp;</td>
<td width="96%" style="border:1px solid black">
<table class="table" width="100%" cellspacing="0">
<thead>
<tr>
<td class="tablecolumnheader"><yfc:i18n>Line_#</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Date</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Item_ID</yfc:i18n><br/><yfc:i18n>Description</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Product_Class</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>UOM</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Quantity</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Amount</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Status</yfc:i18n></td>
</tr>
</thead>
<tbody>
<yfc:loopXML binding="xml:/ExchangeOrder/OrderLines/@OrderLine" id="OrderLine">
<tr>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"/></td>
<td class="tablecolumn" sortValue='<%=getDateValue("xml:/OrderLine/@Createts")%>'><yfc:getXMLValue binding="xml:/OrderLine/@Createts"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"/><br/>
<%=getLocalizedOrderLineDescription("OrderLine")%>
</td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ProductClass"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/OrderLineTranQuantity/@TransactionalUOM"/></td>
<td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/@OrderedQty"/></td>
<td class="numerictablecolumn" nowrap="true" >
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<yfc:getXMLValue binding="xml:/OrderLine/LineOverallTotals/@LineTotal"/>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
<td class="tablecolumn"><%=displayOrderStatus(getValue("OrderLine","xml:/OrderLine/@MultipleStatusesExist"),getValue("OrderLine","xml:/OrderLine/@MaxLineStatusDesc"))%></td>
</tr>
</yfc:loopXML>
</tbody>
</table>
</td>
<td width="2%">&nbsp;</td>
</tr>
</tbody>
</table>
