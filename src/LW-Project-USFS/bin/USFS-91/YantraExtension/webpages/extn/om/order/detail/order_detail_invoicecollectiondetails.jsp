<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<%
String prePathId = getParameter("PrePathId");
%>
<table class="table" editable="false" width="100%" cellspacing="0" SuppressRowColoring="true">
<thead>
<tr>
<td class="tablecolumnheader"><yfc:i18n>Invoice_#</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Invoice_Date</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Amount_Collected</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Total_Invoice_Amount</yfc:i18n></td>
</tr>
</thead>
<tbody>
<yfc:loopXML binding='<%=buildBinding("xml:/",prePathId,"/@InvoiceCollectionDetail")%>' id="InvoiceCollectionDetail">
<tr>
<yfc:makeXMLInput name="invoiceKey">
<yfc:makeXMLKey binding="xml:/GetOrderInvoiceDetails/@InvoiceKey" value="xml:/InvoiceCollectionDetail/@OrderInvoiceKey" />
</yfc:makeXMLInput>
<td class="tablecolumn">
<a <%=getDetailHrefOptions("L03",getParameter("invoiceKey"),"")%>>
<yfc:getXMLValue binding="xml:/InvoiceCollectionDetail/@InvoiceNo"/>
</a>
</td>
<td class="tablecolumn" sortValue="<%=getDateValue("xml:/InvoiceCollectionDetail/@DateInvoiced")%>">
<yfc:getXMLValue binding="xml:/InvoiceCollectionDetail/@DateInvoiced"/>
</td>
<td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:InvoiceCollectionDetail:/InvoiceCollectionDetail/@AmountCollected")%>">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/> <%=getValue("InvoiceCollectionDetail","xml:/InvoiceCollectionDetail/@AmountCollected")%> <yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
<td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:InvoiceCollectionDetail:/InvoiceCollectionDetail/@TotalAmount")%>">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/> <%=getValue("InvoiceCollectionDetail","xml:/InvoiceCollectionDetail/@TotalAmount")%> <yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
</tr>
</yfc:loopXML>
</tbody>
</table>
