<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%
// Call API to get the data for the Document Type field.
YFCElement documentTypeInput = YFCDocument.parse("<CommonCode CodeType=\"COUNTRY\" />").getDocumentElement();

YFCElement documentTypeTemplate = YFCDocument.parse("<CommonCode CodeName=\"\" CodeShortDescription=\"\" CodeType=\"\" CodeValue=\"\" CommonCodeKey=\"\" />").getDocumentElement();
%>

<yfc:callAPI apiName="getCommonCodeList" inputElement="<%=documentTypeInput%>" templateElement="<%=documentTypeTemplate%>" outputNamespace="CommonCountryCodeList"/>

<%
String sFilename="modifyaddress_" + getLocale().getCountry()+".jsp";
java.net.URL oURL=pageContext.getServletContext().getResource("/common/"+sFilename);
%>

<script language="javascript">
function setAddressOptionRadio(radioObj) {

var retainOptionObj = document.all("RetainAnswersOptionRetain");
if (null != retainOptionObj) {
yfcSetControlAsUnchanged(retainOptionObj);
}
var clearOptionObj = document.all("RetainAnswersOptionClear");
if (null != clearOptionObj) {
yfcSetControlAsUnchanged(clearOptionObj)
}
}
</script>

<table width="100%" cellpadding="0" cellspacing="0">
<tr>
<td>
<% if (oURL != null) { %>
<jsp:include page="<%=sFilename%>" flush="true" />
<% } else { %>
<jsp:include page="/extn/common/modifyaddress_default.jsp" flush="true" />
<% } %>
</td>
</tr>
</table>
<table class="view" width="100%" cellpadding="0" id="answerOptionsTable" style="display:none">
<tr>
<td class="searchlabel" colspan="2">
<yfc:i18n>Address_questions_and_or_permit_questions_have_been_answered</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true">
<input class="combobox" type="radio" checked="true" name="RetainAnswersOption" id="RetainAnswersOptionRetain" value="Y" onclick="setAddressOptionRadio(this)"/>
<yfc:i18n>Retain_existing_answers</yfc:i18n>
</td>
<td class="searchcriteriacell" nowrap="true">
<input class="combobox" type="radio" name="RetainAnswersOption" id="RetainAnswersOptionClear" value="N" onclick="setAddressOptionRadio(this)"/>
<yfc:i18n>Clear_existing_answers</yfc:i18n>
</td>
</tr>
</table>
