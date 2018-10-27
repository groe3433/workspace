<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<table class="view" width="100%">
<tr>
<td class="detaillabel" ><yfc:i18n>Line_Sub_Total</yfc:i18n></td>
<td class="protectednumber"><span class="protectednumber" ><%=getValue("Order","xml:/Order/OverallTotals/@LineSubTotal")%>&nbsp;</span><span class="protectedtext"  style="width:8px"><yfc:i18n>Plus</yfc:i18n></span></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Total_Charges</yfc:i18n></td>
<td class="protectednumber"><span class="protectednumber" ><%=getValue("Order","xml:/Order/OverallTotals/@GrandCharges")%>&nbsp;</span><span class="protectedtext"  style="width:8px"><yfc:i18n>Plus</yfc:i18n></span></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Total_Tax</yfc:i18n></td>
<td class="protectednumber"><span class="protectednumber" ><%=getValue("Order","xml:/Order/OverallTotals/@GrandTax")%>&nbsp;</span><span class="protectedtext"  style="width:8px"><yfc:i18n>Plus</yfc:i18n></span></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Total_Discount</yfc:i18n></td>
<td class="protectednumber"><span class="protectednumber" ><%=getValue("Order","xml:/Order/OverallTotals/@GrandDiscount")%>&nbsp;</span><span class="protectedtext"  style="width:8px"><yfc:i18n>Minus</yfc:i18n></span></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Grand_Total</yfc:i18n></td>
<td class="totaltext"><span class="protectednumber" ><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp; <%=getValue("Order","xml:/Order/OverallTotals/@GrandTotal")%>&nbsp; <yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/></span><span class="protectedtext"  style="width:8px">&nbsp;</span></td>
</tr>
</table>
