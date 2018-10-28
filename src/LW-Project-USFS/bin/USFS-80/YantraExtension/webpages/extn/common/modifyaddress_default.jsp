<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/modificationutils.jspf"%>

<script language=javascript src="/yantra/extn/console/scripts/address.js"></script>
<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script>
<table class="view" height="100%">
<tr>
    <td>
        <input type="hidden" name="xml:/Order/@ModificationReasonCode"/>
        <input type="hidden" name="xml:/Order/@ModificationReasonText"/>
        <input type="hidden" name="xml:/Order/@Override" value="N"/>
		<input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
        <input type="hidden" name="xml:/OrderRelease/@ModificationReasonCode"/>
        <input type="hidden" name="xml:/OrderRelease/@ModificationReasonText"/>
        <input type="hidden" name="xml:/OrderRelease/@Override" value="N"/>
        <input type="hidden" name="xml:/WorkOrder/@ModificationReasonCode"/>
        <input type="hidden" name="xml:/WorkOrder/@ModificationReasonText"/>
        <input type="hidden" name="xml:/WorkOrder/@OverrideModificationRules" value="N"/>
        <input type="hidden" name="hiddenDraftOrderFlag" value="N"/>
        <input type="hidden" class="unprotectedinput" <%=getTextOptions("AddressType", "")%> />
        <input type="hidden" <%=getTextOptions("Beeper", "")%> >
        <input type="hidden" <%=getTextOptions("Department", "")%> >
        <input type="hidden" <%=getTextOptions("EveningFaxNo", "")%> >
        <input type="hidden" <%=getTextOptions("JobTitle", "")%> >
        <input type="hidden" <%=getTextOptions("OtherPhone", "")%> >        
        <input type="hidden" <%=getTextOptions("Suffix", "")%> >
        <input type="hidden" <%=getTextOptions("Title", "")%> >
        <input type="hidden" <%=getTextOptions("ErrorTxt", "")%> />
        <input type="hidden" <%=getTextOptions("HttpUrl", "")%> />
        <input type="hidden" <%=getTextOptions("PersonID", "")%> />
        <input type="hidden" <%=getTextOptions("PreferredShipAddress", "")%> />
        <input type="hidden" <%=getTextOptions("UseCount", "")%> />
        <input type="hidden" <%=getTextOptions("VerificationStatus", "")%> /> 
    </td>
</tr>
<tr id="AddressTypeRow">
    <td valign="top" height="100%" >
        <table>
            <tr>
                <td class="detaillabel" ><yfc:i18n>Address_Type</yfc:i18n></td>
                <td>
                    <input type="text" class="protectedinput" <%=getTextOptions("AddressTypeLabel", "")%>>
                </td>
            </tr>
        </table>
    </td>
</tr>
<tr>
	<td valign="top" height="100%" >
        <table class="view"  ID="ModifyAddressLeft" >
		
		<tr>
            <td class="detaillabel" ><yfc:i18n>Address_Line_1</yfc:i18n></td>
            <td>
                <input type="text" class="unprotectedinput" <%=getTextOptions("AddressLine1", "")%> >&nbsp;
            </td>
        </tr>
        <tr>
            <td class="detaillabel" ><yfc:i18n>Address_Line_2</yfc:i18n></td>
            <td>
                <input type="text" class="unprotectedinput" <%=getTextOptions("AddressLine2", "")%>>&nbsp;
            </td>
        </tr>
        <tr>
            <td class="detaillabel" ><yfc:i18n>Address_Line_3</yfc:i18n></td>
            <td>
                <input type="text" class="unprotectedinput" <%=getTextOptions("AddressLine3", "")%> >&nbsp;
            </td>
        </tr>
        <tr>
            <td class="detaillabel" ><yfc:i18n>Address_Line_4</yfc:i18n></td>
            <td>
                <input type="text" class="unprotectedinput" <%=getTextOptions("AddressLine4", "")%> >&nbsp;
            </td>
        </tr>
        <tr>
            <td class="detaillabel" ><yfc:i18n>Address_Line_5</yfc:i18n></td>
            <td>
                <input type="text" class="unprotectedinput" <%=getTextOptions("AddressLine5", "")%> >&nbsp;
            </td>
        </tr>
        <tr>
            <td class="detaillabel" ><yfc:i18n>Address_Line_6</yfc:i18n></td>
            <td>
                <input type="text" class="unprotectedinput" <%=getTextOptions("AddressLine6", "")%> >&nbsp;
            </td>
        </tr>
        <tr>
            <td class="detaillabel" ><yfc:i18n>City</yfc:i18n></td>
            <td><input type="text" class="unprotectedinput" <%=getTextOptions("City", "")%> ></td>
        </tr>
        <tr>
            <td class="detaillabel" ><yfc:i18n>State</yfc:i18n></td>
            <td>
				<select class="combobox" <%=getComboOptions("State")%>>
					<yfc:loopOptions binding="xml:CommonStateCodeList:/CommonCodeList/@CommonCode" 
					name="CodeShortDescription" value="CodeValue" isLocalized="Y"
					selected='<%=resolveValue("State")%>'/>
				</select>
            </td>
        </tr>
        <tr>
            <td class="detaillabel" ><yfc:i18n>Postal_Code</yfc:i18n></td>
            <td><input type="text" class="unprotectedinput" <%=getTextOptions("ZipCode", "")%> ></td>
        </tr>
        <tr>
			<td class="detaillabel" ><yfc:i18n>Country</yfc:i18n></td>
		    <td>
				<select id="CountryCodeObj" <%=getComboOptions("Country")%>>
					<yfc:loopOptions binding="xml:CommonCountryCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
					value="CodeValue" isLocalized="Y"/>
				</select>			
	        </td>
        </tr>
	    </table>
    </td>
	<td valign="top" height="100%" >
        <table class="view"  ID="ModifyAddressRight" >
        <tr>
            <td class="detaillabel" ><yfc:i18n>First_Name</yfc:i18n></td>
            <td><input class="unprotectedinput" <%=getTextOptions("FirstName", "")%>></td>
        <!--</tr>
        <tr>
            <td class="detaillabel" ><yfc:i18n>Middle_Name</yfc:i18n></td>-->
            <td><input type="hidden" class="unprotectedinput" <%=getTextOptions("MiddleName", "")%> ></td>
        </tr>
        <tr>
            <td class="detaillabel" ><yfc:i18n>Last_Name</yfc:i18n></td>
            <td><input class="unprotectedinput" <%=getTextOptions("LastName", "")%> ></td>
        </tr>
        <tr>
            <td class="detaillabel" ><yfc:i18n>Day_Time_Phone</yfc:i18n></td>
            <td><input class="unprotectedinput" <%=getTextOptions("DayPhone", "")%> ></td>
        <!--</tr>
        <tr>
            <td class="detaillabel" ><yfc:i18n>Evening_Phone</yfc:i18n></td>-->
            <td><input type="hidden" class="unprotectedinput" <%=getTextOptions("EveningPhone", "")%> ></td>
        </tr>
        <tr>
            <td class="detaillabel" ><yfc:i18n>Mobile_Phone</yfc:i18n></td>
            <td><input class="unprotectedinput" <%=getTextOptions("MobilePhone", "")%> ></td>
        </tr>
        <tr>
            <td class="detaillabel" ><yfc:i18n>Fax</yfc:i18n></td>
            <td><input class="unprotectedinput" <%=getTextOptions("DayFaxNo", "")%> ></td>
        </tr>
        <tr>
            <td class="detaillabel" ><yfc:i18n>E_Mail</yfc:i18n></td>
            <td><input class="unprotectedinput" <%=getTextOptions("Emailid", "")%> ></td>
        </tr>
        <tr>
                <td class="detaillabel" ><yfc:i18n>Unit ID</yfc:i18n></td>
                <td><input type="text" class="unprotectedinput" maxLength='30' size='30'  <%=getTextOptions("AlternateEmailID")%> ></td>
        </tr>
		<span id="RadioAddress" >
		<tr >
		 <td  class="detaillabel" ><yfc:i18n>Address</yfc:i18n></td>
			<td><input type="radio" value="NWCG" onClick="setValue(this)" name="PseudoCompany">Business</td>
		<tr>
		</tr>
			<td>&nbsp;</td>
			<td><input type="radio" value="" name="PseudoCompany" onClick="setValue(this)">Residential</td>
			<input class="unprotectedinput" type="hidden" <%=getTextOptions("Company", "")%>>
			</td>
        </tr> 
		</span>
    	</table>
    </td>
</tr>
</table>
<script>
function setValue(elem)
{
	document.containerform.Company.value = elem.value;
}
function checkAdress()
{	
	var vAddressLine1 = document.containerform.AddressLine1.value ;
	var vCity = document.containerform.City.value ;
	var vState = document.containerform.State.value ;
	var vCountry = document.containerform.Country.value ;
	if(vAddressLine1 == "")
	{
		alert("Please enter address");
		return false;
	}
	else if(vCity=="")
	{
		alert("Please enter city");
		return false;
	}
	else if(vState=="")
	{
		alert("Please enter state");
		return false;
	}
	else if(vCountry=="")
	{
		alert("Please enter country");
		return false;
	}
	else
	{
		return true;
	}
	
}
</script>