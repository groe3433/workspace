<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<%
String incomplete = request.getParameter("Incomplete");
String creditCardEncrypted = request.getParameter("CreditCardEncrypted");

String path = request.getParameter("Path");
if (isVoid(path)) {
path="xml:/Order/PaymentMethods/PaymentMethod";
}
%>

<% if (!equals(incomplete,"Y")) {%>
<tr>
<td class="detaillabel" ><yfc:i18n>Credit_Card_#</yfc:i18n></td>
<% if (userHasDecryptedPaymentAttributesPermissions()) {%>
<% if (equals(creditCardEncrypted,"Y")) {%>
<%
String encryptedString = getValue("PaymentMethod", "xml:/PaymentMethod/@CreditCardNo");
YFCElement inputElem = YFCDocument.parse("<GetDecryptedString StringToDecrypt=\""+encryptedString+"\"/>").getDocumentElement();
YFCElement templateElem = YFCDocument.parse("<GetDecryptedString DecryptedString=\"\"/>").getDocumentElement();
%>

<yfc:callAPI apiName="getDecryptedString" inputElement="<%=inputElem%>" templateElement="<%=templateElem%>" outputNamespace="GetDecryptedString"/>
<td>
<input class="protectedinput"  contenteditable="false" <%=getTextOptions( "xml:/GetDecryptedString/@DecryptedString", "xml:/GetDecryptedString/@DecryptedString")%>/>
</td>
<% } else { %>
<td>
<input class="protectedinput" contenteditable="false" <%=getTextOptions( "xml:/PaymentMethod/@CreditCardNo", "xml:/PaymentMethod/@CreditCardNo")%>/>
</td>
<% } %>
<% } else { %>
<td class="protectedtext">
<%=showEncryptedCreditCardNo(getValue("PaymentMethod", "xml:/PaymentMethod/@DisplayCreditCardNo"))%>&nbsp;
</td>
<% } %>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Expiration_Date</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue name="PaymentMethod" binding="xml:/PaymentMethod/@CreditCardExpDate"/></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Credit_Card_Type</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue name="PaymentMethod" binding="xml:/PaymentMethod/@CreditCardType"/></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Name_On_Card</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue name="PaymentMethod" binding="xml:/PaymentMethod/@CreditCardName"/></td>
</tr>

<% } else { %>

<tr>
<td class="detaillabel" ><yfc:i18n>Credit_Card_#</yfc:i18n></td>
<td>
<input <%=yfsGetTextOptions(path + "/@CreditCardNo","xml:/PaymentMethod/@DisplayCreditCardNo", "xml:/Order/AllowedModifications")%>>
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Expiration_Date</yfc:i18n></td>
<td>
<input <%=yfsGetTextOptions(path + "/@CreditCardExpDate","xml:/PaymentMethod/@CreditCardExpDate", "xml:/Order/AllowedModifications")%>>
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Credit_Card_Type</yfc:i18n></td>
<td>
<input <%=yfsGetTextOptions(path + "/@CreditCardType","xml:/PaymentMethod/@CreditCardType", "xml:/Order/AllowedModifications")%>>
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Name_On_Card</yfc:i18n></td>
<td>
<input <%=yfsGetTextOptions(path + "/@CreditCardName","xml:/PaymentMethod/@CreditCardName", "xml:/Order/AllowedModifications")%>>
</td>
</tr>

<% } %>

