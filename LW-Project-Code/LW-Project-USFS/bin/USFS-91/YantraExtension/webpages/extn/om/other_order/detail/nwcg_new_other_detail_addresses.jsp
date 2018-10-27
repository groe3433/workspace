<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/extn.js"></script>

<%
String Path = (String)request.getParameter("Path");
%>

<table class="view" height="100%">
<tr>
<td>
<input type="hidden" <%=getTextOptions(Path+"/@AddressType", "")%> />
<input type="hidden" <%=getTextOptions(Path+"/@AlternateEmailID", "")%> >
<input type="hidden" <%=getTextOptions(Path+"/@Beeper", "")%> >
<input type="hidden" <%=getTextOptions(Path+"/@Department", "")%> >
<input type="hidden" <%=getTextOptions(Path+"/@EveningFaxNo", "")%> >
<input type="hidden" <%=getTextOptions(Path+"/@JobTitle", "")%> >
<input type="hidden" <%=getTextOptions(Path+"/@OtherPhone", "")%> >
<input type="hidden" <%=getTextOptions(Path+"/@Suffix", "")%> >
<input type="hidden" <%=getTextOptions(Path+"/@Title", "")%> >
<input type="hidden" <%=getTextOptions(Path+"/@ErrorTxt", "")%> />
<input type="hidden" <%=getTextOptions(Path+"/@HttpUrl", "")%> />
<input type="hidden" <%=getTextOptions(Path+"/@PersonID", "")%> />
<input type="hidden" <%=getTextOptions(Path+"/@PreferredShipAddress", "")%> />
<input type="hidden" <%=getTextOptions(Path+"/@UseCount", "")%> />
<input type="hidden" <%=getTextOptions(Path+"/@VerificationStatus", "")%> />
</td>
</tr>

<tr>
<td valign="top" height="100%" >
<table class="view"  ID="ModifyAddressLeft" >
<tr>
<td class="detaillabel" ><yfc:i18n>Address_Line_1</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" onblur="makeUppercase(this);" <%=getTextOptions(Path+"/@AddressLine1")%> >&nbsp;
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Address_Line_2</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" onblur="makeUppercase(this);" <%=getTextOptions(Path+"/@AddressLine2")%>>&nbsp;
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Address_Line_3</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" onblur="makeUppercase(this);" <%=getTextOptions(Path+"/@AddressLine3")%> >&nbsp;
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Address_Line_4</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" onblur="makeUppercase(this);" <%=getTextOptions(Path+"/@AddressLine4")%> >&nbsp;
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Address_Line_5</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" onblur="makeUppercase(this);" <%=getTextOptions(Path+"/@AddressLine5")%> >&nbsp;
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Address_Line_6</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" onblur="makeUppercase(this);" <%=getTextOptions(Path+"/@AddressLine6")%> >&nbsp;
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>City</yfc:i18n></td>
<td><input type="text" class="unprotectedinput" onblur="makeUppercase(this);" <%=getTextOptions(Path+"/@City")%> ></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>State</yfc:i18n></td>
<td><input type="text" class="unprotectedinput" onblur="makeUppercase(this);" <%=getTextOptions(Path+"/@State")%> ></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Postal_Code</yfc:i18n></td>
<td><input type="text" class="unprotectedinput" <%=getTextOptions(Path+"/@ZipCode")%> ></td>
</tr>
<%
String selectedCombo = Path+"/@Country";
String strCountry = resolveValue(selectedCombo);
if(strCountry == null || strCountry.equals(""))
{
	strCountry = "US" ;
}
%>
<tr>
<td class="detaillabel" ><yfc:i18n>Country</yfc:i18n></td>
<td>
<select class="combobox" <%=getComboOptions(Path+"/@Country")%>>
<yfc:loopOptions binding="xml:CommonCountryCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected='<%=strCountry%>' isLocalized="Y"/>
</select>
</td>
</tr>

</table>
</td>
<td valign="top" height="100%" >
<table class="view"  ID="ModifyAddressRight" >
<tr>
<td class="detaillabel" ><yfc:i18n>First_Name</yfc:i18n></td>
<td><input class="unprotectedinput" onblur="makeUppercase(this);" <%=getTextOptions(Path+"/@FirstName")%>></td>
</tr>

<tr>
<td class="detaillabel" ><yfc:i18n>Last_Name</yfc:i18n></td>
<td><input class="unprotectedinput" onblur="makeUppercase(this);" <%=getTextOptions(Path+"/@LastName")%> ></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Day_Time_Phone</yfc:i18n></td>
<td><input class="unprotectedinput" <%=getTextOptions(Path+"/@DayPhone")%> ></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Evening_Phone</yfc:i18n></td>
<td><input class="unprotectedinput" <%=getTextOptions(Path+"/@EveningPhone")%> ></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Mobile_Phone</yfc:i18n></td>
<td><input class="unprotectedinput" <%=getTextOptions(Path+"/@MobilePhone")%> ></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Fax</yfc:i18n></td>
<td><input class="unprotectedinput" <%=getTextOptions(Path+"/@DayFaxNo")%> ></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>E_Mail</yfc:i18n></td>
<td><input class="unprotectedinput" <%=getTextOptions(Path+"/@EMailID")%> ></td>
</tr>
<span id="<%=Path%>RadioAddress" >
<tr>
<%
String strCompany = resolveValue(Path+"/@Company");
String strNWCGChecked = "" ;
String strBlankChecked = "" ;
if(strCompany == null || strCompany.equals(""))
{
	strBlankChecked = "CHECKED" ;
}
else
{
	strNWCGChecked="CHECKED";
}
%>
<td type="text" class="detaillabel" ><yfc:i18n>Address</yfc:i18n></td>
<td><INPUT type=radio value="NWCG" name="<%=Path+"/@Company"%>" <%=strNWCGChecked%> >Business</Input>
</tr>
<tr>
<td>
&nbsp;
</td>
<td>
<INPUT type=radio value="" name="<%=Path+"/@Company"%>" <%=strBlankChecked%> >Residential</Input>
</td>
</tr>
</span>
<tr>
<td>&nbsp;</td>
</tr>
<tr>
<td>&nbsp;</td>
</tr>
<tr>
<td>&nbsp;</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Customer_Id</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" name="xml:/Customer/@CustomerID"  id="xml:/Customer/@CustomerID" onblur="makeUppercase(this);fetchDataFromServer(this,'getCustomerList',displayCustomerList<%=Path.substring(Path.lastIndexOf("/")+1,Path.length())%>)"/>
<img class="lookupicon" onclick="callLookup(this,'NWCGCustomerLookUp');" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Customer") %> />
</td>
</tr>
</table>
</td>
</tr>
</table>
<script>
function displayCustomerList<%=Path.substring(Path.lastIndexOf("/")+1,Path.length())%>(elem,xmlDoc){
	return updateAddress(elem,xmlDoc,'<%=Path%>',"BillingPersonInfo");
}
</script>