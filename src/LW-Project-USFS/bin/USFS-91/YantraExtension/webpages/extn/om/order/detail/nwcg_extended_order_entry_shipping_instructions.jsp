<%@include file="/yfsjspcommon/yfsutil.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/extn.js"></script>
<script>
yfcDoNotPromptForChanges(true);
</script>


<table height="100%" class="view" cellSpacing=0 cellPadding=0 >
	<tr>    
		<td class="detaillabel">
			<yfc:i18n>Information</yfc:i18n>
		</td>		
		<td width="100%">
		<textarea class="unprotectedtextareainput" rows="5" cols="75" style="word-wrap:break-word;" MaxLength="1500"  <%=getTextAreaOptions("xml:/Order/Extn/@ExtnShippingInstructions")%>></textarea>
			<input type="hidden" <%=getTextAreaOptions("xml:/Order/Extn/@ExtnShippingInstructions")%> />
		</td>
		<td></td>
	</tr>
	<tr>    
		<td class="detaillabel">
			<yfc:i18n>City</yfc:i18n>
		</td>
		<td>
			<input type="text" class="unprotectedinput" size="30" MaxLength="35" <%=getTextOptions("xml:/Order/Extn/@ExtnShipInstrCity")%>>
			<input type="hidden" <%=getTextOptions("xml:/Order/Extn/@ExtnShipInstrCity")%> />
		</td>
		<td></td>
	</tr>
	<tr>    
		<td class="detaillabel">
			<yfc:i18n>State</yfc:i18n>
		</td>
		<td>
			<select class="combobox" <%=getComboOptions("xml:/Order/Extn/@ExtnShipInstrState")%>>
				<yfc:loopOptions binding="xml:CommonStateCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
			</select>
			<input type="hidden" <%=getTextOptions("xml:/Order/Extn/@ExtnShipInstrState")%> />
		</td>
		<td></td>
	</tr>
</table>
