<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<table class="view">
<tr>
<td class="searchlabel" ><yfc:i18n>Customer_ID_Unit_ID</yfc:i18n></td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Customer/@CustomerIDQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Customer/@CustomerIDQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Customer/@CustomerID")%>/>
</td>
</tr>
<!-- CR 151 ks -->
<tr>
<td class="searchlabel" ><yfc:i18n>Customer_Name</yfc:i18n></td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Customer/Extn/@ExtnCustomerNameQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Customer/Extn/@ExtnCustomerNameQryType"/>
</select>
<input type="text" class="unprotectedinput" size="30" <%=getTextOptions("xml:/Customer/Extn/@ExtnCustomerName")%> />
</td>
</tr>

<!-- end of CR -->
<tr>
<td class="searchlabel" ><yfc:i18n>Customer_Type</yfc:i18n></td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Customer/Extn/@ExtnCustomerType" class="combobox" <%=getComboOptions("xml:/Customer/Extn/@ExtnCustomerType")%>>
<yfc:loopOptions binding="xml:CustomerType:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/Customer/Extn/@ExtnCustomerType" isLocalized="Y"/>
</select>
</td>
</tr>

<tr>
<td class="searchlabel" >
<yfc:i18n>City</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Customer/Consumer/BillingPersonInfo/@CityQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Customer/Consumer/BillingPersonInfo/@CityQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Customer/Consumer/BillingPersonInfo/@City")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Zip_Code</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Customer/Consumer/BillingPersonInfo/@ZipCodeQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Customer/Consumer/BillingPersonInfo/@ZipCodeQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Customer/Consumer/BillingPersonInfo/@ZipCode")%>/>
</td>
</tr>
<!-- CR 106 ks  -->
<tr>
<td nowrap="true" class="searchcriteriacell">
<yfc:i18n>Is_Active</yfc:i18n>
<input type="checkbox" 		<%=getCheckBoxOptions("xml:/Customer/Extn/@ExtnActiveFlag")%>/>
</td>
</tr>
<!-- end CR 106 -->
</table>
