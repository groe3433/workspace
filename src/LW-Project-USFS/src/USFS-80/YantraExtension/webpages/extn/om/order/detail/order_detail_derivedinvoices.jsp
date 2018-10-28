<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/order.jspf" %>

<table class="table" editable="false" width="100%" cellpadding="0" cellspacing="0">
<thead>
<tr>
<td sortable="no" class="checkboxheader">
<input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
</td>
<td class="tablecolumnheader" nowrap="true"><yfc:i18n>Invoice_#</yfc:i18n></td>
<td class="tablecolumnheader" nowrap="true"><yfc:i18n>Order_#</yfc:i18n></td>
<td class="tablecolumnheader" nowrap="true"><yfc:i18n>Document_Type</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Invoice_Type</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Total_Amount</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Amount_Collected</yfc:i18n></td>
</tr>
</thead>
<tbody>
<yfc:loopXML binding="xml:DerivedOrderInvoiceList:/OrderInvoiceList/@OrderInvoice" id="DerivedOrderInvoice">
<tr>
<yfc:makeXMLInput name="invoiceKey">
<yfc:makeXMLKey binding="xml:/GetOrderInvoiceDetails/@InvoiceKey" value="xml:DerivedOrderInvoice:/OrderInvoice/@OrderInvoiceKey" />
</yfc:makeXMLInput>
<yfc:makeXMLInput name="orderKey">
<yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:DerivedOrderInvoice:/OrderInvoice/Order/@OrderHeaderKey" />
</yfc:makeXMLInput>
<td>
<input type="checkbox" value='<%=getParameter("invoiceKey")%>' name="chkEntityKey" <% if(!showOrderNo("DerivedOrderInvoice","Order")) {%> disabled="true" <%}%>/>
</td>
<td class="tablecolumn"><a <%=getDetailHrefOptions("L01",getParameter("invoiceKey"),"")%>>
<yfc:getXMLValue binding="xml:DerivedOrderInvoice:/OrderInvoice/@InvoiceNo"/></a>
</td>
<td class="tablecolumn">
<% if(showOrderNo("DerivedOrderInvoice","Order")) {%>
<a <%=getDetailHrefOptions("L02", getValue("DerivedOrderInvoice", "xml:/OrderInvoice/Order/@DocumentType"), getParameter("orderKey"), "")%>>
<yfc:getXMLValue binding="xml:DerivedOrderInvoice:/OrderInvoice/Order/@OrderNo"/>
</a>
<%} else {%>
<yfc:getXMLValue binding="xml:DerivedOrderInvoice:/OrderInvoice/Order/@OrderNo"/>
<%}%>
</td>
<td class="tablecolumn">
<%=displayDocumentDescription(getValue("DerivedOrderInvoice", "xml:/OrderInvoice/Order/@DocumentType"), (YFCElement) request.getAttribute("DocumentParamsList"))%>
</td>
<td class="tablecolumn"><%=getI18N(getValue("DerivedOrderInvoice", "xml:/OrderInvoice/@InvoiceType"))%></td>
<td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:DerivedOrderInvoice:/OrderInvoice/@TotalAmount")%>">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<yfc:getXMLValue binding="xml:DerivedOrderInvoice:/OrderInvoice/@TotalAmount"/>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
<td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:DerivedOrderInvoice:/OrderInvoice/@AmountCollected")%>">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<yfc:getXMLValue binding="xml:DerivedOrderInvoice:/OrderInvoice/@AmountCollected"/>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
</tr>
</yfc:loopXML>
</tbody>
</table>
