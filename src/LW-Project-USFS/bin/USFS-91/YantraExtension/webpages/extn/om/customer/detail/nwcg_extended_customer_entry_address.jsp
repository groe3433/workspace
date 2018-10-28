<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/extn.js"></script>

<%
String Path = (String)request.getParameter("Path");
%>

<table class="view" height="100%">

<tr>
<td valign="top" height="100%" >
<table class="view"  ID="ModifyAddressLeft" >
<tr>
<td class="detaillabel" ><yfc:i18n>Address_Line_1</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" OldValue='' maxlength="30" onblur="makeUppercase(this);"
<%=getTextOptions("xml:/Customer/Consumer/BillingPersonInfo/@AddressLine1")%> >&nbsp;
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Address_Line_2</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" OldValue='' maxlength="30" onblur="makeUppercase(this);"
<%=getTextOptions("xml:/Customer/Consumer/BillingPersonInfo/@AddressLine2")%>>&nbsp;
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Address_Line_3</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" OldValue='' maxlength="30" onblur="makeUppercase(this);"
<%=getTextOptions("xml:/Customer/Consumer/BillingPersonInfo/@AddressLine3")%> >&nbsp;
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Address_Line_4</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" OldValue='' maxlength="30" onblur="makeUppercase(this);"
<%=getTextOptions("xml:/Customer/Consumer/BillingPersonInfo/@AddressLine4")%> >&nbsp;
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Address_Line_5</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" OldValue='' maxlength="30" onblur="makeUppercase(this);"
<%=getTextOptions("xml:/Customer/Consumer/BillingPersonInfo/@AddressLine5")%> >&nbsp;
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Address_Line_6</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" OldValue='' maxlength="30" onblur="makeUppercase(this);"
<%=getTextOptions("xml:/Customer/Consumer/BillingPersonInfo/@AddressLine6")%> >&nbsp;
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>City</yfc:i18n><font color="orange">*</font></td>
<td><input type="text" class="unprotectedinput" OldValue='' onblur="makeUppercase(this);"
<%=getTextOptions("xml:/Customer/Consumer/BillingPersonInfo/@City")%> ></td>
</tr>

<tr>
	<td class="detaillabel" ><yfc:i18n>State</yfc:i18n></td>
	<td>
		<select class="combobox" <%=getComboOptions("xml:/Customer/Consumer/BillingPersonInfo/@State")%>>
			<yfc:loopOptions binding="xml:StateCode:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
		</select>
	</td>
</tr>

<tr>
<td class="detaillabel" ><yfc:i18n>Postal_Code</yfc:i18n><font color="orange">*</font></td>
<td><input type="text" class="unprotectedinput" OldValue=''
<%=getTextOptions("xml:/Customer/Consumer/BillingPersonInfo/@ZipCode")%> ></td>
</tr>

<tr>
<td class="detaillabel" ><yfc:i18n>Country</yfc:i18n><font color="orange">*</font></td>
<td>
<%
// Call API to get the data for the Document Type field.
YFCElement documentTypeInput1 = YFCDocument.parse("<CommonCode CodeType=\"COUNTRY\" />").getDocumentElement();

YFCElement documentTypeTemplate1 = YFCDocument.parse("<CommonCode CodeName=\"\" CodeShortDescription=\"\" CodeType=\"\" CodeValue=\"\" CommonCodeKey=\"\" />").getDocumentElement();
%>

<yfc:callAPI apiName="getCommonCodeList" inputElement="<%=documentTypeInput1%>" templateElement="<%=documentTypeTemplate1%>" outputNamespace="CommonCountryCodeList"/>

<%
String strCountry = resolveValue("xml:/Customer/Consumer/BillingPersonInfo/@Country");
if(strCountry == null || strCountry.equals(""))
{
	strCountry = "US" ;
}
%>

<select class="combobox" id="CountryCodeObj" name="xml:/Customer/Consumer/BillingPersonInfo/@Country" <%=getComboOptions("US")%>>
					<yfc:loopOptions binding="xml:CommonCountryCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
					value="CodeValue" selected="<%=strCountry%>" isLocalized="Y"/>

</select>
</td>
</tr>

</table>
</td>
<td valign="top" height="100%" >
<table class="view"  ID="ModifyAddressRight" >
<tr>
<td class="detaillabel" ><yfc:i18n>First_Name</yfc:i18n></td>
<td><input type="text" class="unprotectedinput" OldValue='' onblur="makeUppercase(this);" 
<%=getTextOptions("xml:/Customer/Consumer/BillingPersonInfo/@FirstName")%>></td>
<!--</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Middle_Name</yfc:i18n></td>-->
<td><input type="hidden" class="unprotectedinput" OldValue=''
<%=getTextOptions("xml:/Customer/Consumer/BillingPersonInfo/@MiddleName")%> ></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Last_Name</yfc:i18n></td>
<td><input type="text" class="unprotectedinput" OldValue='' onblur="makeUppercase(this);"
<%=getTextOptions("xml:/Customer/Consumer/BillingPersonInfo/@LastName")%> ></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Day_Time_Phone</yfc:i18n></td>
<td><input type="text" class="unprotectedinput" OldValue=''
<%=getTextOptions("xml:/Customer/Consumer/BillingPersonInfo/@DayPhone")%> ></td>
<!--</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Evening_Phone</yfc:i18n></td>-->
<td><input type="hidden" class="unprotectedinput" OldValue=''
<%=getTextOptions("xml:/Customer/Consumer/BillingPersonInfo/@EveningPhone")%> ></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Mobile_Phone</yfc:i18n></td>
<td><input type="text" class="unprotectedinput" OldValue=''
<%=getTextOptions("xml:/Customer/Consumer/BillingPersonInfo/@MobilePhone")%> ></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Fax</yfc:i18n></td>
<td><input type="text" class="unprotectedinput" OldValue=''
<%=getTextOptions("xml:/Customer/Consumer/BillingPersonInfo/@DayFaxNo")%> ></td>
</tr>
<tr>
<td type="text" class="detaillabel" ><yfc:i18n>E_Mail</yfc:i18n></td>
<td><input class="unprotectedinput" OldValue=''
<%=getTextOptions("xml:/Customer/Consumer/BillingPersonInfo/@EMailID")%> ></td>
</tr>
<tr>
<td type="text" class="detaillabel" ><yfc:i18n>Address</yfc:i18n></td>
<td><INPUT type=radio value="NWCG" id="BusinessRadio" name="xml:/Customer/Consumer/BillingPersonInfo/@Company" checked >Business</Input>
</tr>
<tr>
<td>
&nbsp;
</td>
<td>
<INPUT type=radio value="" id="ResidentialRadio" name="xml:/Customer/Consumer/BillingPersonInfo/@Company">Residential</Input>
</td>
</tr>
</table>
</td>
</tr>
</table>