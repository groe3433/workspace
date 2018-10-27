<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>

<table class="view" width="100%">
<tr>
<td>
<input type="hidden" name="xml:/RecordExternalCharges/@ModificationReasonCode"/>
<input type="hidden" name="xml:/RecordExternalCharges/@ModificationReasonText"/>
<input type="hidden" name="hiddenDraftOrderFlag" value='<%=getValue("Order", "xml:/Order/@DraftOrderFlag")%>'/>
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Amount</yfc:i18n></td>
<td>
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>
<input type="text" onblur="updateAPIQty(this, 'xml:/RecordExternalCharges/Memo/@ChargeAmount');" <%=yfsGetTextOptions("xml:/Temp/@ChargeAmount","xml:/Order/AllowedModifications")%>
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Reference</yfc:i18n></td>
<td>
<input <%=yfsGetTextOptions("xml:/RecordExternalCharges/Memo/@Reference1", "xml:/Order/AllowedModifications")%>>
</td>
</tr>
<tr>
<td>
<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/RecordExternalCharges/Memo/@ChargeAmount")%> />
</td>
</tr>
</table>
