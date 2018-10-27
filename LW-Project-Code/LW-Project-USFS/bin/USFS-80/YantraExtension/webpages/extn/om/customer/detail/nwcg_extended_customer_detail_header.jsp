<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="org.w3c.dom.*" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/extn/scripts/extn.js"></script>
<script>

yfcDoNotPromptForChanges(true);

function checkFieldActiveStatus(){
	
	var objActiveFlagCheckBox = document.getElementById("ActiveFlagCheckBox");

	if (objActiveFlagCheckBox.checked)
	{		
		enableFields();
	}
	else{	

		disableFieldsOnInactiveCustomer();
	}
}

function checkForRequiredFields() {
	var objCustomerType = document.getElementById("xml:/Customer/Extn/@ExtnCustomerType");
	var objSelOption = eval(objCustomerType.options[objCustomerType.selectedIndex]);

	if (objSelOption.value == '01')
	{
		for (index = 1 ; index <= 5 ; index ++)
		{
			var objOrgType = document.getElementById("VALID_"+index);
			if(objOrgType != null)
				setMandatary(objOrgType);
		}
	}

}
window.attachEvent("onload", checkForRequiredFields);
window.attachEvent("onload", checkFieldActiveStatus);
</script>


<table class="view" width="100%">
<yfc:makeXMLInput name="customerKey">
<yfc:makeXMLKey binding="xml:/Customer/Consumer/BillingPersonInfo/@PersonInfoKey" value="xml:/Customer/Consumer/BillingPersonInfo/@PersonInfoKey"
binding="xml:/Customer/@CustomerKey" value="xml:/Customer/@CustomerKey"/>
</yfc:makeXMLInput>
<tr>
<td>
<input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
<input type="hidden" name="chkCopyCustomerEntityKey" value='<%=getParameter("customerKey")%>' />
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Customer/@OrganizationCode"/></td>
<td class="detaillabel" ><yfc:i18n>Customer_Type</yfc:i18n></td>
	<td>
		<select disabled="true" onChange="markMandatoryFields(this)" class="combobox" <%=getComboOptions("xml:/Customer/Extn/@ExtnCustomerType")%>>
<yfc:loopOptions binding="xml:CustomerType:/CommonCodeList/@CommonCode" selected="xml:/Customer/Extn/@ExtnCustomerType" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
</select>
<input type="hidden" <%=getTextOptions("xml:/Customer/Extn/@ExtnCustomerType")%> />
</td>
<td class="detaillabel" id="CUSTOMER_LABEL"><yfc:i18n>Customer_ID_Unit_ID</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Customer/@CustomerID"/></td>

</td>
<td class="detaillabel"><yfc:i18n>Customer_Unit_Name</yfc:i18n><font color="orange">*</font></td>
<td>
<input type="text" class="unprotectedinput" size="40"  maxLength=40 <%=getTextOptions("xml:/Customer/Extn/@ExtnCustomerName")%>/>
</td>
</tr>
<tr>
	<td class="detaillabel"  id="VALID_1" ><yfc:i18n>Unit_Type</yfc:i18n></td>
	<td>
		<select class="combobox" <%=getComboOptions("xml:/Customer/Extn/@ExtnUnitType")%>>
<yfc:loopOptions binding="xml:NWCGUnitType:/CommonCodeList/@CommonCode" selected="xml:/Customer/Extn/@ExtnUnitType" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
</select>
</td>

	<td class="detaillabel" id="VALID_2"><yfc:i18n>Department</yfc:i18n></td>
	<td  >
		<select class="combobox" <%=getComboOptions("xml:/Customer/Extn/@ExtnDepartment")%>>
<yfc:loopOptions binding="xml:NWCGDepartment:/CommonCodeList/@CommonCode" selected="xml:/Customer/Extn/@ExtnDepartment" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
</select>
</td>
<td class="detaillabel" id="VALID_3" ><yfc:i18n>Agency</yfc:i18n></td>
	<td>
		<select class="combobox" <%=getComboOptions("xml:/Customer/Extn/@ExtnAgency")%>>
<yfc:loopOptions binding="xml:NWCGAgency:/CommonCodeList/@CommonCode" selected="xml:/Customer/Extn/@ExtnAgency" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
</select>
</td>
</tr>
<tr>
	
</tr>
<tr>
<td class="detaillabel" id="VALID_4" ><yfc:i18n>GACC</yfc:i18n></td>
	<td colspan="5">
	<%
	String str = resolveValue("xml:/Customer/Extn/@ExtnGACC");
	if(str != null )
		str = str.replaceAll("&#40;","(").replaceAll("&#41;",")");
	%>
		<select class="combobox" <%=getComboOptions("xml:/Customer/Extn/@ExtnGACC")%>>
<yfc:loopOptions binding="xml:NWCGGACCValues:/CommonCodeList/@CommonCode" selected="<%=str%>" name="CodeLongDescription" value="CodeValue" isLocalized="Y"/>
</select>
	</td>
	<td class="detaillabel" ><yfc:i18n>Active_Flag</yfc:i18n></td>
	<td>
		<input class="checkbox" type="checkbox" id = "ActiveFlagCheckBox" onclick = "checkFieldActiveStatus()" <%=getCheckBoxOptions("xml:/Customer/Extn/@ExtnActiveFlag", "xml:/Customer/Extn/@ExtnActiveFlag", "Y")%>/>
	</td>
</tr>
</table>