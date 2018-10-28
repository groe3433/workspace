<%@include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="java.lang.Double" %>

<%
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
double totalOtherCharges = 0;
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
if(equals(sItemGroupCode, "DS") )
{
totalLineExtendedPrice -= oTotals.getDoubleAttribute("ExtendedPrice");
totalLineExtendedPrice -= oTotals.getDoubleAttribute("OptionPrice");
totalLineTotal -= oTotals.getDoubleAttribute("LineTotal");
totalLineDiscount += oTotals.getDoubleAttribute("Discount");
totalLineTax -= oTotals.getDoubleAttribute("Tax");
totalLineCharges -= oTotals.getDoubleAttribute("Charges");
totalOtherCharges += oTotals.getDoubleAttribute("Discount");
totalOtherCharges -= oTotals.getDoubleAttribute("Charges");

}
else
{
totalLineExtendedPrice += oTotals.getDoubleAttribute("ExtendedPrice");
totalLineExtendedPrice += oTotals.getDoubleAttribute("OptionPrice");
totalLineTotal += oTotals.getDoubleAttribute("LineTotal");
totalLineDiscount -= oTotals.getDoubleAttribute("Discount");
totalLineTax += oTotals.getDoubleAttribute("Tax");
totalLineCharges += oTotals.getDoubleAttribute("Charges");
totalOtherCharges -= oTotals.getDoubleAttribute("Discount");
totalOtherCharges += oTotals.getDoubleAttribute("Charges");

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
String stotalOtherCharges = getLocalizedStringFromDouble(locale,totalOtherCharges);
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
<td colspan="4" sortable="no">
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
<td colspan="5">
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
<td class="numerictablecolumnheader" onmouseover="style.cursor='auto'"><yfc:i18n>Total_Charges</yfc:i18n></td>
<td class="numerictablecolumnheader" onmouseover="style.cursor='auto'"><yfc:i18n>Taxes</yfc:i18n></td>
<td class="numerictablecolumnheader" onmouseover="style.cursor='auto'"><yfc:i18n>Totals</yfc:i18n></td>
</tr>
</thead>
<tbody>
<%	String sHdrChargesMinusHdrDiscounts = "0";
double dblHdrDiscount = getDoubleFromLocalizedString(locale,resolveValue(buildBinding("xml:/Order/",chargeType,total,"/@HdrDiscount")));
double dblHdrCharges = getDoubleFromLocalizedString(locale,resolveValue(buildBinding("xml:/Order/",chargeType,total,"/@HdrCharges")));
double dblHdrChargesMinusHdrDiscounts = dblHdrCharges - dblHdrDiscount;
sHdrChargesMinusHdrDiscounts = getLocalizedStringFromDouble(locale,dblHdrChargesMinusHdrDiscounts);

String sTotalHeaderAndLineCharges = "0";
double dblTotalHeaderAndLineCharges = dblHdrChargesMinusHdrDiscounts + totalOtherCharges;
sTotalHeaderAndLineCharges = getLocalizedStringFromDouble(locale,dblTotalHeaderAndLineCharges);
%>
<tr>
<td class="numerictablecolumnheader" style="width:100px" onmouseover="style.cursor='auto'"><yfc:i18n>Header</yfc:i18n></td>
<td class="numerictablecolumn">-</td>
<% if (equals(displayInEnterpriseCurrency,"Y")) { %>
<td class="numerictablecolumn"><%=sHdrChargesMinusHdrDiscounts%></td>
<td class="numerictablecolumn"><yfc:getXMLValue name="Order" binding='<%=buildBinding("xml:/Order/",chargeType,total,"/@HdrTax")%>'/></td>
<%} else {%>
<td class="numerictablecolumn"><a <%=getDetailHrefOptions(ChargeViewID, "", "")%>><%=sHdrChargesMinusHdrDiscounts%></a></td>
<td class="numerictablecolumn"><a <%=getDetailHrefOptions(TaxViewID, "", "")%>><yfc:getXMLValue name="Order" binding='<%=buildBinding("xml:/Order/",chargeType,"Totals/@HdrTax")%>'/></a></td>
<%}%>
<td class="totaltext"><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<yfc:getXMLValue name="Order" binding='<%=buildBinding("xml:/Order/",chargeType,total,"/@HdrTotal")%>'/>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/></td>
</tr>
<tr>
<td class="numerictablecolumnheader" style="width:100px" onmouseover="style.cursor='auto'"><yfc:i18n>Line</yfc:i18n></td>
<td class="numerictablecolumn"><%=sTotalLineExtendedPrice%></td>
<td class="numerictablecolumn"><%=stotalOtherCharges%></td>
<td class="numerictablecolumn"><%=sTotalLineTax%></td>
<td class="totaltext"><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<%=sTotalLineTotal%>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/></td>
</tr>
<tr>
<td class="numerictablecolumnheader" style="width:100px" onmouseover="style.cursor='auto'"><yfc:i18n>Totals</yfc:i18n></td>
<td class="totaltext"><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<%=sTotalLineExtendedPrice%>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/></td>
<td class="totaltext"><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<%=sTotalHeaderAndLineCharges%>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/></td>
<td class="totaltext"><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<yfc:getXMLValue name="Order" binding='<%=buildBinding("xml:/Order/",chargeType,total,"/@GrandTax")%>'/>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/></td>
<td class="totaltext"><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<yfc:getXMLValue name="Order" binding='<%=buildBinding("xml:/Order/",chargeType,total,"/@GrandTotal")%>'/>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/></td>
</tr>
</tbody>
</table>
