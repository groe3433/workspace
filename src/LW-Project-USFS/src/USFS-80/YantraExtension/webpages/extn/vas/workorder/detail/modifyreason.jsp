<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<script language="javascript" src="/yantra/console/scripts/cancelreasonpopup.js"></script>
<script language="javascript">
	yfcDoNotPromptForChanges(true);
</script>
<script language="javascript">
  
	

	function setOKClickedAttribute() {
	
		var myObject = new Object();
		myObject = dialogArguments;
		var parentWindow = myObject.currentWindow;
				
		var parentReasonCode = myObject.reasonCodeInput;
		var parentReasonText = myObject.reasonTextInput;
		
		if(validateControlValues()) {
			
			parentWindow.document.documentElement.setAttribute("OKClicked", "true");
			window.dialogArguments["OKClicked"] = "YES";

			var reasonCodeIp = document.all("xml:/ModificationReason/@ReasonCode");
			parentReasonCode.value = reasonCodeIp.value;

			var reasonTextIp = document.all("xml:/ModificationReason/@ReasonText");
			parentReasonText.value = reasonTextIp.value;

			window.close();
        }
   }
</script>
<table width="100%" class="view">
    <tr>
        <td align="right">
            <yfc:i18n>Cancellation_Reason_Code</yfc:i18n>
        </td>
        <td>
            <select name="xml:/ModificationReason/@ReasonCode" class="combobox">
                <yfc:loopOptions binding="xml:ReasonCodeList:/CommonCodeList/@CommonCode" 
                    name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
            </select>
        </td>
	</tr>	
	<tr>
        <td align="right">
            <yfc:i18n>Reason_Text</yfc:i18n>
        </td>
        <td>
            <textarea class="unprotectedtextareainput" rows="3" cols="50" <%=getTextAreaOptions("xml:/ModificationReason/@ReasonText")%>></textarea>
        </td>
    </tr>

	<tr>
		<td></td>
		<td align="right">
			<input type="button" class="button" value='<%=getI18N("__OK__")%>' onclick="setOKClickedAttribute();return true;"/>
		</td>
		
	</tr>
</table>