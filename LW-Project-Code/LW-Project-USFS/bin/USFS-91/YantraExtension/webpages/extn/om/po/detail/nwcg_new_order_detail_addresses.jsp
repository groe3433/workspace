<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.nwcg.icbs.yantra.util.common.StringUtil" %>
<%

String Path = (String)request.getParameter("Path");

YFCElement elemOrganizationList = (YFCElement) request.getAttribute("OrganizationList");
YFCNodeList nlBillingPersonInfo = null ;

if(elemOrganizationList != null)
{
	nlBillingPersonInfo = elemOrganizationList.getElementsByTagName("BillingPersonInfo");
}

YFCElement elemBillingPersonInfo = null ;

if(nlBillingPersonInfo != null && nlBillingPersonInfo.getLength() == 1)
{
	elemBillingPersonInfo = (YFCElement) nlBillingPersonInfo.item(0);
}
else
{
	// assigning the list output just to avoid the null pointer check all the time
	// we can also create and assign a new element
	elemBillingPersonInfo = elemOrganizationList ;
}
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
<td class="detaillabel" ><yfc:i18n>Company</yfc:i18n></td>
<td><input class="unprotectedinput" <%=getTextOptions(Path+"/@Company",StringUtil.nonNull(elemBillingPersonInfo.getAttribute("Company")))%>></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Address_Line_1</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions(Path+"/@AddressLine1",StringUtil.nonNull(elemBillingPersonInfo.getAttribute("AddressLine1")))%> >&nbsp;
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Address_Line_2</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions(Path+"/@AddressLine2",StringUtil.nonNull(elemBillingPersonInfo.getAttribute("AddressLine2")))%>>&nbsp;
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Address_Line_3</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions(Path+"/@AddressLine3",StringUtil.nonNull(elemBillingPersonInfo.getAttribute("AddressLine3")))%> >&nbsp;
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Address_Line_4</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions(Path+"/@AddressLine4",StringUtil.nonNull(elemBillingPersonInfo.getAttribute("AddressLine4")))%> >&nbsp;
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Address_Line_5</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions(Path+"/@AddressLine5",StringUtil.nonNull(elemBillingPersonInfo.getAttribute("AddressLine5")))%> >&nbsp;
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Address_Line_6</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions(Path+"/@AddressLine6",StringUtil.nonNull(elemBillingPersonInfo.getAttribute("AddressLine6")))%> >&nbsp;
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>City</yfc:i18n></td>
<td><input type="text" class="unprotectedinput" <%=getTextOptions(Path+"/@City",StringUtil.nonNull(elemBillingPersonInfo.getAttribute("City")))%> ></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>State</yfc:i18n></td>
<td><input type="text" class="unprotectedinput" <%=getTextOptions(Path+"/@State",StringUtil.nonNull(elemBillingPersonInfo.getAttribute("State")))%> ></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Postal_Code</yfc:i18n></td>
<td><input type="text" class="unprotectedinput" <%=getTextOptions(Path+"/@ZipCode",StringUtil.nonNull(elemBillingPersonInfo.getAttribute("ZipCode")))%> ></td>
</tr>
<%
String selectedCombo = StringUtil.nonNull(elemBillingPersonInfo.getAttribute("Country"));//Path+"/@Country";
if(selectedCombo == null || selectedCombo.equals(""))
{
	selectedCombo = "US" ;
}
%>
<tr>
<td class="detaillabel" ><yfc:i18n>Country</yfc:i18n></td>
<td>
<select class="combobox" <%=getComboOptions(Path+"/@Country")%>>
<yfc:loopOptions binding="xml:CommonCountryCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected='<%=selectedCombo%>' isLocalized="Y"/>
</select>
</td>
</tr>

</table>
</td>
<td valign="top" height="100%" >
<table class="view"  ID="ModifyAddressRight" >
<tr>
<td class="detaillabel" ><yfc:i18n>First_Name</yfc:i18n></td>
<td><input class="unprotectedinput" <%=getTextOptions(Path+"/@FirstName",StringUtil.nonNull(elemBillingPersonInfo.getAttribute("FirstName")))%>></td>
<!--</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Middle_Name</yfc:i18n></td>-->
<td><input type="hidden" class="unprotectedinput" <%=getTextOptions(Path+"/@MiddleName",StringUtil.nonNull(elemBillingPersonInfo.getAttribute("MiddleName")))%> ></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Last_Name</yfc:i18n></td>
<td><input class="unprotectedinput" <%=getTextOptions(Path+"/@LastName",StringUtil.nonNull(elemBillingPersonInfo.getAttribute("LastName")))%> ></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Day_Time_Phone</yfc:i18n></td>
<td><input class="unprotectedinput" <%=getTextOptions(Path+"/@DayPhone",StringUtil.nonNull(elemBillingPersonInfo.getAttribute("DayPhone")))%> ></td>
<!--</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Evening_Phone</yfc:i18n></td>-->
<td><input type="hidden" class="unprotectedinput" <%=getTextOptions(Path+"/@EveningPhone",StringUtil.nonNull(elemBillingPersonInfo.getAttribute("EveningPhone")))%> ></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Mobile_Phone</yfc:i18n></td>
<td><input class="unprotectedinput" <%=getTextOptions(Path+"/@MobilePhone",StringUtil.nonNull(elemBillingPersonInfo.getAttribute("MobilePhone")))%> ></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Fax</yfc:i18n></td>
<td><input class="unprotectedinput" <%=getTextOptions(Path+"/@DayFaxNo",StringUtil.nonNull(elemBillingPersonInfo.getAttribute("DayFaxNo")))%> ></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>E_Mail</yfc:i18n></td>
<td><input class="unprotectedinput" <%=getTextOptions(Path+"/@EMailID",StringUtil.nonNull(elemBillingPersonInfo.getAttribute("EMailID")))%> ></td>
</tr>
</table>
</td>
</tr>
</table>
