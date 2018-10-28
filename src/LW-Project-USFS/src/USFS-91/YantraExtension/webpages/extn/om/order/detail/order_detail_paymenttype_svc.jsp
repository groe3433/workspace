<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<%  String incomplete = request.getParameter("Incomplete");
String storedValueCardEncrypted = request.getParameter("StoredValueCardEncrypted");

String path = request.getParameter("Path");
if (isVoid(path)) {
path = "xml:/Order/PaymentMethods/PaymentMethod";
}
%>

<% if (!equals(incomplete,"Y")) {%>

<tr>
<td class="detaillabel" ><yfc:i18n>Stored_Value_Card_#</yfc:i18n></td>
<% if (userHasDecryptedPaymentAttributesPermissions()) {%>
<% if (equals(storedValueCardEncrypted,"Y")) {%>
<%
String encryptedString = getValue("PaymentMethod", "xml:/PaymentMethod/@SvcNo");
YFCElement inputElem = YFCDocument.parse("<GetDecryptedString StringToDecrypt=\""+encryptedString+"\"/>").getDocumentElement();
YFCElement templateElem = YFCDocument.parse("<GetDecryptedString DecryptedString=\"\"/>").getDocumentElement();
%>

<yfc:callAPI apiName="getDecryptedString" inputElement="<%=inputElem%>" templateElement="<%=templateElem%>" outputNamespace="GetDecryptedString"/>
<td>
<input class="protectedinput" contenteditable="false" <%=getTextOptions( "xml:/GetDecryptedString/@DecryptedString", "xml:/GetDecryptedString/@DecryptedString")%>/>
</td>
<% } else { %>
<td>
<input class="protectedinput" contenteditable="false" <%=getTextOptions( "xml:/PaymentMethod/@SvcNo", "xml:/PaymentMethod/@SvcNo")%>/>
</td>
<% } %>
<% } else { %>
<td class="protectedtext">
<%=showEncryptedString(getValue("PaymentMethod", "xml:/PaymentMethod/@DisplaySvcNo"))%>&nbsp;
</td>
<% } %>
</tr>



<tr>
<td class="detaillabel" ><yfc:i18n>Payment_Reference_#1</yfc:i18n></td>

<% if (userHasDecryptedPaymentAttributesPermissions()) {%>
<% if (equals(storedValueCardEncrypted,"Y")) {%>
<%
String encryptedString = getValue("PaymentMethod", "xml:/PaymentMethod/@PaymentReference1");
YFCElement inputElem = YFCDocument.parse("<GetDecryptedString StringToDecrypt=\""+encryptedString+"\"/>").getDocumentElement();
YFCElement templateElem = YFCDocument.parse("<GetDecryptedString DecryptedString=\"\"/>").getDocumentElement();
%>

<yfc:callAPI apiName="getDecryptedString" inputElement="<%=inputElem%>" templateElement="<%=templateElem%>" outputNamespace="GetDecryptedString"/>
<td>
<input class="protectedinput" contenteditable="false" <%=getTextOptions( "xml:/GetDecryptedString/@DecryptedString", "xml:/GetDecryptedString/@DecryptedString")%>/>
</td>
<% } else { %>
<td>
<input class="protectedinput" contenteditable="false" <%=getTextOptions( "xml:/PaymentMethod/@PaymentReference1", "xml:/PaymentMethod/@PaymentReference1")%>/>
</td>
<% } %>
<% } else { %>
<td class="protectedtext">
<%=showEncryptedString(getValue("PaymentMethod", "xml:/PaymentMethod/@DisplayPaymentReference1"))%>&nbsp;
</td>
<% } %>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Payment_Reference_#2</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue name="PaymentMethod" binding="xml:/PaymentMethod/@PaymentReference2"/></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Payment_Reference_#3</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue name="PaymentMethod" binding="xml:/PaymentMethod/@PaymentReference3"/></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Funds_Available</yfc:i18n></td>
<td class="protectedtext">
<% if(equals(getValue("PaymentMethod", "xml:/PaymentMethod/@GetFundsAvailableUserExitInvoked"), "Y")) {%>

<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/> <yfc:getXMLValue name="PaymentMethod" binding="xml:/PaymentMethod/@FundsAvailable"/>
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
<% } else {%>
<yfc:i18n>Not_Applicable</yfc:i18n>
<%} %>
</td>
</tr>

<% } else { %>

<tr>
<td class="detaillabel" ><yfc:i18n>Stored_Value_Card_#</yfc:i18n></td>
<td>
<input <%=yfsGetTextOptions(path + "/@SvcNo","xml:/PaymentMethod/@SvcNo", "xml:/Order/AllowedModifications")%>>
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Payment_Reference_#1</yfc:i18n></td>
<td>
<input <%=yfsGetTextOptions(path + "/@PaymentReference1","xml:/PaymentMethod/@PaymentReference1", "xml:/Order/AllowedModifications")%>>
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Payment_Reference_#2</yfc:i18n></td>
<td>
<input <%=yfsGetTextOptions(path + "/@PaymentReference2","xml:/PaymentMethod/@PaymentReference2", "xml:/Order/AllowedModifications")%>>
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Payment_Reference_#3</yfc:i18n></td>
<td>
<input <%=yfsGetTextOptions(path + "/@PaymentReference3","xml:/PaymentMethod/@PaymentReference3", "xml:/Order/AllowedModifications")%>>
</td>
</tr>
<% } %>
