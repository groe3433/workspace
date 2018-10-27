<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/orderentry.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.dom.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/extn.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>

<script language="Javascript" >
IgnoreChangeNames();
yfcDoNotPromptForChanges(true);
</script>

<%
String customerKeyVal = resolveValue("xml:/Customer/Consumer/BillingPersonInfo/@PersonInfoKey");
String iCreateNewCustomer = resolveValue("xml:/newCustomer/@iCreateNewCustomer");
%>

<script language="javascript">
<%{
	if (!isVoid(customerKeyVal)) {
		YFCDocument customerDoc = YFCDocument.createDocument("Customer");
		customerDoc.getDocumentElement().setAttribute("CustomerKey",resolveValue("xml:/Customer/@CustomerKey"));
		%>
		function showCustomerDetail() {
			showDetailFor('<%=customerDoc.getDocumentElement().getString(false)%>');
		}
		window.attachEvent("onload", showCustomerDetail);
	<%}
}%>
</script>

<table class="view" width="100%">
<tr>
	<td>
	<% if (iCreateNewCustomer.equals("Y")){ %>
		<input type="hidden" name="xml:/newCustomer/@iCreateNewCustomer" value="Y"/>
	<% } %>
	</td>
</tr>
<tr>
	<td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
	<!-- customer will always belong to NWCG organization -->
	<td class="protectedtext">NWCG<input type="hidden" name="xml:/Customer/@OrganizationCode" value="NWCG"/>
	</td>

	<td class="detaillabel" ><yfc:i18n>Customer_Type</yfc:i18n></td>
	<% if (iCreateNewCustomer.equals("Y")){ %>
		<!-- CR 60 -- default it to NWCG customer type -->
		<td>
			<select disabled="true" onChange="markMandatoryFields(this)" class="combobox" <%=getComboOptions("xml:/Customer/Extn/@ExtnCustomerType")%>>
			<yfc:loopOptions binding="xml:CustomerType:/CommonCodeList/@CommonCode" selected="01" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
			</select>
		</td>
		<td><input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Customer/Extn/@ExtnCustomerType","01")%> ></td>
	<%} else {%>
		<td>
			<select onChange="markMandatoryFields(this)" class="combobox" <%=getComboOptions("xml:/Customer/Extn/@ExtnCustomerType")%>>
			<yfc:loopOptions binding="xml:CustomerType:/CommonCodeList/@CommonCode" selected="xml:/Customer/Extn/@ExtnCustomerType" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
			</select>
		</td>
	<% } %>

	<!--CR 349 -->
	<td class="detaillabel" id="CUSTOMER_LABEL"><yfc:i18n>Customer_ID_Unit_ID</yfc:i18n><font color="orange">*</font></td>
	<!-- end of CR 349 -->
	<td>
		<select onChange="copyComboBoxToTextField(this)" sortable="true" style="display: none;" class="combobox" id="CUSTOMER_PREFIX_DROPDOWN" >
		<yfc:loopOptions binding="xml:StateCode:/CommonCodeList/@CommonCode" name="CodeValue" value="CodeValue" isLocalized="Y"/>
		<yfc:loopOptions binding="xml:CommonNWCGCountryCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
		</select>
		<input type="text" class="unprotectedinput" id="xml:/CUSTOMER_PREFIX_ID" maxLength=35 size=25 
		onblur="fetchDataFromServer(this,'getCustomerListForCreation',checkCustomerName);makeUppercase(this);"
		<%=getTextOptions("xml:/Customer/Consumer/BillingPersonInfo/@PersonID")%>/>
	</td>

	<td class="detaillabel"><yfc:i18n>Customer_Name</yfc:i18n><font color="orange">*</font></td>
	<td>
		<input type="text" class="unprotectedinput" size="40" maxLength=40 name="xml:/Customer/Extn/@ExtnCustomerName" onblur="makeUppercase(this);" <%=getTextOptions("xml:/Customer/Extn/@ExtnCustomerName")%>/>
	</td>
</tr>

<tr>
	<td class="detaillabel" id="VALID_1"><yfc:i18n>Unit_Type</yfc:i18n></td>
	<td>
		<select class="combobox" <%=getComboOptions("xml:/Customer/Extn/@ExtnUnitType")%>>
		<yfc:loopOptions binding="xml:NWCGUnitType:/CommonCodeList/@CommonCode" selected="xml:/Customer/Extn/@ExtnUnitType" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
		</select>
	</td>
	<td class="detaillabel" id="VALID_2"><yfc:i18n>Department</yfc:i18n></td>
	<td>
		<select class="combobox" <%=getComboOptions("xml:/Customer/Extn/@ExtnDepartment")%>>
		<yfc:loopOptions binding="xml:NWCGDepartment:/CommonCodeList/@CommonCode" selected="xml:/Customer/Extn/@ExtnDepartment" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
		</select>
	</td>
	<td class="detaillabel" id="VALID_3"><yfc:i18n>Agency</yfc:i18n></td>
	<td>
		<select class="combobox" <%=getComboOptions("xml:/Customer/Extn/@ExtnAgency")%>>
		<yfc:loopOptions binding="xml:NWCGAgency:/CommonCodeList/@CommonCode" selected="xml:/Customer/Extn/@ExtnAgency" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
		</select>
	</td>
</tr>

<tr>
	<td class="detaillabel" id="VALID_4" ><yfc:i18n>GACC</yfc:i18n></td>
	<td colspan="5">
		<select class="combobox" <%=getComboOptions("xml:/Customer/Extn/@ExtnGACC")%>>
		<yfc:loopOptions binding="xml:NWCGGACCValues:/CommonCodeList/@CommonCode" selected="xml:/Customer/Extn/@ExtnGACC" name="CodeLongDescription" value="CodeValue" isLocalized="Y"/>
		</select>
	</td>
	<td class="detaillabel"><yfc:i18n>Active_Flag</yfc:i18n></td>
	<td class="checkbox" sortable="no"><input type="checkbox" <%=getCheckBoxOptions("xml:/Customer/Extn/@ExtnActiveFlag")%>/>
	</td>
</tr>
</table>