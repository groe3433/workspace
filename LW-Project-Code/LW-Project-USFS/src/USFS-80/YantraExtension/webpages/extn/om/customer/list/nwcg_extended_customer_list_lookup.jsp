<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<table  editable="false" class="table">
<thead>
<tr>
<td class="tablecolumnheader" sortable="no">&nbsp;</td>
<td class="tablecolumnheader"><yfc:i18n>Customer_ID</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Customer_Name</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>City</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Zip_Code</yfc:i18n></td>
</tr>
</thead>
<tbody>
<yfc:loopXML binding="xml:/CustomerList/@Customer" id="Customer">
<tr>
<td class="tablecolumn">
<yfc:makeXMLInput name="customerKey">
<yfc:makeXMLKey binding="xml:/Customer/@CustomerID" value="xml:/Customer/@CustomerID" />
</yfc:makeXMLInput>
<img class="icon"  onClick="setLookupValue(this.value)"  value='<%=resolveValue("xml:/Customer/@CustomerID")%>' <%=getImageOptions(YFSUIBackendConsts.GO_ICON, "Click_to_Select")%>/>
</td>
<td class="tablecolumn">
<yfc:getXMLValue binding="xml:/Customer/@CustomerID"/>
</td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/Customer/Extn/@ExtnCustomerName"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/Customer/Consumer/BillingPersonInfo/@City"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/Customer/Consumer/BillingPersonInfo/@ZipCode"/></td>
</tr>
</yfc:loopXML>
</tbody>
</table>
