<%@include file="/yfsjspcommon/yfsutil.jspf" %>

<script language="JavaScript">
function setMyOKClickedAttribute() 
{
	var myObject = new Object();
	myObject = dialogArguments;

	var parentWindow = myObject.currentWindow;
	var parentReasonCodeInput = myObject.cancelReasonCodeInput;

	var reasonForCancellation = document.getElementById('xml:/NWCGTrackableItem/@ReasonForCancellation');
	parentReasonCodeInput.value = reasonForCancellation.value;
	myObject.OKClicked = true;
	window.close();
}
</script>

<table width="100%" class="view">
	<tr>
        <td class="detaillabel">
            <yfc:i18n>Reason_for_Cancellation</yfc:i18n>
        </td>
		<td>
		   <select class=combobox name="xml:/NWCGTrackableItem/@ReasonForCancellation"> 
			 <option value="" Selected></option> 
			 <option value="L">Lost/Stolen</option> 
			 <option value="X">Destroyed</option>
			 <option value="R">Transferred</option>
			 <option value="E">Issued</option>
			 <option value="O">Other</option>
		   </select> 
		</td>
	</tr>
	<tr>
		<td></td>
		<td align="right">
			<input type="button" class="button" value='<%=getI18N("__OK__")%>' onclick="setMyOKClickedAttribute();"/>
			<input type="button" class="button" value='<%=getI18N("Cancel")%>' onclick="window.close();"/>
		<td>
	<tr>
</table>
