<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/chargeutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript">
function negateAmount(oText, sAmountText)
{
onBlurHandler();
var oAmountText= document.all(sAmountText);
if(oText.value != null){
oAmountText.value= "-" + oText.value;
}
}
</script>

<table cellspacing="0" cellpadding="0" border="0" width="100%" >
<tr>
<td>
<input type="hidden" name="hiddenDraftOrderFlag" value='<%=getValue("Order", "xml:OrderDetail:/Order/@DraftOrderFlag")%>'/>
<input type="hidden" <%=getTextOptions("xml:/RecordExternalCharges/PaymentMethod/@PaymentKey","xml:/Order/PaymentMethods/PaymentMethod/@PaymentKey")%>/>
</td>
</tr>
<tr>
<td valign="top">
<table class="view" width="100%">
<tr>
<td class="detaillabel" ><yfc:i18n>Authorization_ID</yfc:i18n></td>
<td>
<input <%=yfsGetTextOptions("xml:/RecordExternalCharges/PaymentMethod/PaymentDetails/@AuthorizationID","xml:/RecordExternalCharges/PaymentMethod/PaymentDetails/@AuthorizationID","xml:OrderDetail:/Order/AllowedModifications")%>>
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Code</yfc:i18n></td>
<td>
<input <%=yfsGetTextOptions("xml:/RecordExternalCharges/PaymentMethod/PaymentDetails/@AuthCode","xml:/RecordExternalCharges/PaymentMethod/PaymentDetails/@AuthCode","xml:OrderDetail:/Order/AllowedModifications")%>>
</td>
</tr>
<tr >
<td class="detaillabel" ><yfc:i18n>Expiration_Date</yfc:i18n></td>
<td>
<input type="text" <%=yfsGetTextOptions("xml:/RecordExternalCharges/PaymentMethod/PaymentDetails/@AuthorizationExpirationDate","xml:/RecordExternalCharges/PaymentMethod/PaymentDetails/@AuthorizationExpirationDate","xml:OrderDetail:/Order/AllowedModifications")%> />
<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar","xml:/RecordExternalCharges/PaymentMethod/PaymentDetails/@AuthorizationExpirationDate", "xml:OrderDetail:/Order/AllowedModifications") %> />
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Amount</yfc:i18n></td>
<td>
<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/RecordExternalCharges/PaymentMethod/PaymentDetails/@ProcessedAmount")%> value=""/>
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>
<input type="text"  onblur="negateAmount(this, 'xml:/RecordExternalCharges/PaymentMethod/PaymentDetails/@ProcessedAmount');" <%=yfsGetTextOptions("xml:/Temp/@ProcessedAmount","xml:/RecordExternalCharges/PaymentMethod/PaymentDetails/@ProcessedAmount", "xml:OrderDetail:/Order/AllowedModifications")%>>
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
</tr>
</table>
<td>
</tr>
</table>
