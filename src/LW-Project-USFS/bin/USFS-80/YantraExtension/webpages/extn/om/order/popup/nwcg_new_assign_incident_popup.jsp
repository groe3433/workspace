<%@include file="/yfsjspcommon/yfsutil.jspf" %>
<!--
<script language="javascript" src="/yantra/console/scripts/exceptionutils.js"></script>

-->



<script language="javascript">
window.attachEvent("onload",setFocus);
function setFocus()
{
    var tmp = document.all("xml:/NWCGIncidentOrder/@IncidentNo");
    tmp.focus();
}

function OKClicked() {
	var retVal = new Object();
	retVal["xml:/NWCGIncidentOrder/@IncidentNo"] = document.all["xml:/NWCGIncidentOrder/@IncidentNo"].value;
	retVal["xml:/NWCGIncidentOrder/@Year"] = document.all["xml:/NWCGIncidentOrder/@Year"].value;
	window.dialogArguments["OMReturnValue"] = retVal;
        window.dialogArguments["OKClicked"] = "YES";
		window.doNotCheck=true;
        window.close();
}

</script>
<table width="100%" class="view">
    <tr>
        <td class="detaillabel">
            <yfc:i18n>Incident_Year</yfc:i18n> 
        </td>
        <td>
            <!-- <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@Year")%> />	!-->
			<input class=unprotectedinput maxLength=4 size=4 name=xml:/NWCGIncidentOrder/@Year dataType="STRING">
		</td>

        <td class="detaillabel">
            <yfc:i18n>Assign_To_Incident</yfc:i18n> 
        </td>
        <td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@IncidentNo")%> >	
			<img class="lookupicon" onclick="callIncidentLookup('xml:/NWCGIncidentOrder/@IncidentNo','xml:/NWCGIncidentOrder/@Year','NWCGIncidentLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Assign_To_Incident") %> />
		</td>
    </tr>
</table>
