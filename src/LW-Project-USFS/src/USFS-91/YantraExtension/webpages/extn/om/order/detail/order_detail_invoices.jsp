<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<table class="table" editable="false" width="100%" cellpadding="0" cellspacing="0">
<thead>
<tr>
<td sortable="no" class="checkboxheader">
<input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
</td>
<td class="tablecolumnheader" nowrap="true"><yfc:i18n>Invoice_#</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Invoice_Type</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Total_Amount</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Amount_Collected</yfc:i18n></td>
</tr>
</thead>
<tbody>
<yfc:loopXML binding="xml:/OrderInvoiceList/@OrderInvoice" id="OrderInvoice">
<tr>
<yfc:makeXMLInput name="invoiceKey">
<yfc:makeXMLKey binding="xml:/GetOrderInvoiceDetails/@InvoiceKey" value="xml:/OrderInvoice/@OrderInvoiceKey" />
</yfc:makeXMLInput>
<td>
<input type="checkbox" value='<%=getParameter("invoiceKey")%>' name="chkEntityKey"/>
</td>
<td class="tablecolumn"><a <%=getDetailHrefOptions("L01",getParameter("invoiceKey"),"")%>>
<yfc:getXMLValue binding="xml:/OrderInvoice/@InvoiceNo"/></a>
</td>
<td class="tablecolumn"><%=getI18N(getValue("OrderInvoice", "xml:/OrderInvoice/@InvoiceType"))%></td>
<td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:OrderInvoice:/OrderInvoice/@TotalAmount")%>">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<yfc:getXMLValue binding="xml:/OrderInvoice/@TotalAmount"/>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
<td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:OrderInvoice:/OrderInvoice/@AmountCollected")%>">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<yfc:getXMLValue binding="xml:/OrderInvoice/@AmountCollected"/>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
</tr>
</yfc:loopXML>
</tbody>
</table>
