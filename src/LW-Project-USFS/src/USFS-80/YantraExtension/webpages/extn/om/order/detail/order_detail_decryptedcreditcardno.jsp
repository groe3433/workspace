<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<table class="view" width="100%">
<tr>
<td class="detaillabel" ><yfc:i18n>Credit_Card_#</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding='xml:/GetDecryptedCreditCardNumber/@DecryptedCCNo'/>&nbsp;</td
</tr>
</table>
