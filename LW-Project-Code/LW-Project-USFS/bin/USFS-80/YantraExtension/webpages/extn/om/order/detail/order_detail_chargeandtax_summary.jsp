<%@include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="java.lang.Double" %>

<%
String isReturnService = getParameter("ReturnService");

String displayInEnterpriseCurrency = request.getParameter("DisplayInEnterpriseCurrency");
String total = "";

if (equals(displayInEnterpriseCurrency,"Y")) {
total = "TotalsInEnterpriseCurrency";
} else {
total = "Totals";
}

String chargeType=request.getParameter("chargeType");
if (isVoid(chargeType)) {
chargeType="Remaining";
}

String ChargeViewID="";
String TaxViewID="";
if(equals(chargeType,"Overall")) {
ChargeViewID="L01";
TaxViewID="L04";
}
else if(equals(chargeType,"Remaining")) {
ChargeViewID="L02";
TaxViewID="L05";
}
else if(equals(chargeType,"Invoiced")) {
ChargeViewID="L03";
TaxViewID="L06";
}

double totalLineExtendedPrice = 0;
double totalLineDiscount = 0;
double totalLineCharges = 0;
double totalLineTax = 0;
double totalLineTotal = 0;
%>
<yfc:loopXML name="Order" binding="xml:/Order/OrderLines/@OrderLine" id="orderline">
<%
request.setAttribute("orderline", (YFCElement)pageContext.getAttribute("orderline"));
YFCElement oOrderLine = (YFCElement)request.getAttribute("orderline");
String sItemGroupCode = oOrderLine.getAttribute("ItemGroupCode");
if(oOrderLine != null)
{
YFCElement oTotals = (YFCElement)oOrderLine.getElementsByTagName("Line" + chargeType + total).item(0);
if(oTotals != null)
{
if(equals("true", isReturnService) && equals(sItemGroupCode, "DS") )
{
totalLineExtendedPrice -= oTotals.getDoubleAttribute("ExtendedPrice");
totalLineExtendedPrice -= oTotals.getDoubleAttribute("OptionPrice");
totalLineTotal -= oTotals.getDoubleAttribute("LineTotal");
totalLineDiscount -= oTotals.getDoubleAttribute("Discount");
totalLineTax -= oTotals.getDoubleAttribute("Tax");
totalLineCharges -= oTotals.getDoubleAttribute("Charges");
}
else
{
totalLineExtendedPrice += oTotals.getDoubleAttribute("ExtendedPrice");
totalLineExtendedPrice += oTotals.getDoubleAttribute("OptionPrice");
totalLineTotal += oTotals.getDoubleAttribute("LineTotal");
totalLineDiscount += oTotals.getDoubleAttribute("Discount");
totalLineTax += oTotals.getDoubleAttribute("Tax");
totalLineCharges += oTotals.getDoubleAttribute("Charges");
}
}
}

%>
</yfc:loopXML>
<%
YFCLocale locale = getLocale();
String sTotalLineExtendedPrice = getLocalizedStringFromDouble(locale,totalLineExtendedPrice);
String sTotalLineDiscount = getLocalizedStringFromDouble(locale,totalLineDiscount);
String sTotalLineTax = getLocalizedStringFromDouble(locale,totalLineTax);
String sTotalLineCharges = getLocalizedStringFromDouble(locale,totalLineCharges);
String sTotalLineTotal = getLocalizedStringFromDouble(locale,totalLineTotal);
%>
<table class="table" cellpadding="0" cellspacing="0" width="100%"  suppressFooter="true">
<thead>
<% if (isVoid(request.getParameter("showOrderedOnly"))) { %>
<tr>
<td sortable="no">
<select name="chargeType" class="combobox" onchange="yfcChangeDetailView(getCurrentViewId());">
<option value="Overall" <%if (equals(chargeType,"Overall")) {%> selected <%}%>><yfc:i18n>Overall</yfc:i18n></option>
<option value="Remaining" <%if (equals(chargeType,"Remaining")) {%> selected <%}%>><yfc:i18n>Open</yfc:i18n></option>
<% if (isVoid(displayInEnterpriseCurrency)) { %>
<option value="Invoiced" <%if (equals(chargeType,"Invoiced")) {%> selected <%}%>><yfc:i18n>Invoiced</yfc:i18n></option>
<%}%>
</select>
</td>
<td colspan="5" sortable="no">
<table>
<tr>
<% if (equals(displayInEnterpriseCurrency,"Y")) { %>
<td>&nbsp;</td>
<td class="detaillabel" ><yfc:i18n>Currency_Conversion_Date</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/PriceInfo/@ReportingConversionDate"/></td>
<%}%>
</tr>
</table>
</td>
</tr>
<%}%>
<% if (equals(displayInEnterpriseCurrency,"Y") && equals(request.getParameter("showOrderedOnly"),"Y")) { %>
<tr>
<td colspan="6">
<table>
<tr>
<td class="detaillabel" ><yfc:i18n>Currency_Conversion_Date</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/PriceInfo/@ReportingConversionDate"/></td>
</tr>
</table>
</td>
</tr>
<%}%>
<tr>
<td class="numerictablecolumnheader" style="width:100px" onmouseover="style.cursor='auto'">&nbsp;</td>
<td class="numerictablecolumnheader" onmouseover="style.cursor='auto'"><yfc:i18n>Price</yfc:i18n></td>
<td class="numerictablecolumnheader" onmouseover="style.cursor='auto'"><yfc:i18n>Discount</yfc:i18n></td>
<td class="numerictablecolumnheader" onmouseover="style.cursor='auto'"><yfc:i18n>Charges</yfc:i18n></td>
<td class="numerictablecolumnheader" onmouseover="style.cursor='auto'"><yfc:i18n>Taxes</yfc:i18n></td>
<td class="numerictablecolumnheader" onmouseover="style.cursor='auto'"><yfc:i18n>Totals</yfc:i18n></td>
</tr>
</thead>
<tbody>
<tr>
<td class="numerictablecolumnheader" style="width:100px" onmouseover="style.cursor='auto'"><yfc:i18n>Header</yfc:i18n></td>
<td class="numerictablecolumn">-</td>
<% if (equals(displayInEnterpriseCurrency,"Y")) { %>
<td class="numerictablecolumn"><yfc:getXMLValue name="Order" binding='<%=buildBinding("xml:/Order/",chargeType,total,"/@HdrDiscount")%>'/></td>
<td class="numerictablecolumn"><yfc:getXMLValue name="Order" binding='<%=buildBinding("xml:/Order/",chargeType,total,"/@HdrCharges")%>'/></td>
<td class="numerictablecolumn"><yfc:getXMLValue name="Order" binding='<%=buildBinding("xml:/Order/",chargeType,total,"/@HdrTax")%>'/></td>
<%} else {%>
<td class="numerictablecolumn"><a <%=getDetailHrefOptions(ChargeViewID, "", "")%>><yfc:getXMLValue name="Order" binding='<%=buildBinding("xml:/Order/",chargeType,"Totals/@HdrDiscount")%>'/></a></td>
<td class="numerictablecolumn"><a <%=getDetailHrefOptions(ChargeViewID, "", "")%>><yfc:getXMLValue name="Order" binding='<%=buildBinding("xml:/Order/",chargeType,"Totals/@HdrCharges")%>'/></a></td>
<td class="numerictablecolumn"><a <%=getDetailHrefOptions(TaxViewID, "", "")%>><yfc:getXMLValue name="Order" binding='<%=buildBinding("xml:/Order/",chargeType,"Totals/@HdrTax")%>'/></a></td>
<%}%>
<td class="totaltext"><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<yfc:getXMLValue name="Order" binding='<%=buildBinding("xml:/Order/",chargeType,total,"/@HdrTotal")%>'/>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/></td>
</tr>
<tr>
<td class="numerictablecolumnheader" style="width:100px" onmouseover="style.cursor='auto'"><yfc:i18n>Line</yfc:i18n></td>
<td class="numerictablecolumn"><%=sTotalLineExtendedPrice%></td>
<td class="numerictablecolumn"><%=sTotalLineDiscount%></td>
<td class="numerictablecolumn"><%=sTotalLineCharges%></td>
<td class="numerictablecolumn"><%=sTotalLineTax%></td>
<td class="totaltext"><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<%=sTotalLineTotal%>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/></td>
</tr>
<tr>
<td class="numerictablecolumnheader" style="width:100px" onmouseover="style.cursor='auto'"><yfc:i18n>Totals</yfc:i18n></td>
<td class="totaltext"><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<%=sTotalLineExtendedPrice%>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/></td>
<td class="totaltext"><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<yfc:getXMLValue name="Order" binding='<%=buildBinding("xml:/Order/",chargeType,total,"/@GrandDiscount")%>'/>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/></td>
<td class="totaltext"><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<yfc:getXMLValue name="Order" binding='<%=buildBinding("xml:/Order/",chargeType,total,"/@GrandCharges")%>'/>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/></td>
<td class="totaltext"><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<yfc:getXMLValue name="Order" binding='<%=buildBinding("xml:/Order/",chargeType,total,"/@GrandTax")%>'/>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/></td>
<td class="totaltext"><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<yfc:getXMLValue name="Order" binding='<%=buildBinding("xml:/Order/",chargeType,total,"/@GrandTotal")%>'/>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/></td>
</tr>
</tbody>
</table>
