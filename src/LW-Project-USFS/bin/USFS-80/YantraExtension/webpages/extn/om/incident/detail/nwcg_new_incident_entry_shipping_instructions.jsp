<%@include file="/yfsjspcommon/yfsutil.jspf" %>
<script language="javascript" src="/yantra/extn/scripts/extn.js"></script>
<script>
yfcDoNotPromptForChanges(true);
</script>
<%
String str = resolveValue("xml:/NWCGIncidentOrder/@ShipInstrState");
%>

<table height="100%" class="view" cellSpacing=0 cellPadding=0 >
	<tr>    
		<td class="detaillabel">
			<yfc:i18n>Information</yfc:i18n>
		</td>		
		<td width="100%">
		<textarea class="unprotectedtextareainput" style="HEIGHT:100px;WIDTH:450px;" name="xml:/NWCGIncidentOrder/@ShippingInstructions">
 <%=resolveValue("xml:/NWCGIncidentOrder/@ShippingInstructions")%></textarea>
		</td>
		<td></td>
	</tr>
	<tr>    
		<td class="detaillabel">
			<yfc:i18n>City</yfc:i18n>
		</td>
		<td>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGIncidentOrder/@ShipInstrCity")%> >
			<input type="hidden" <%=getTextOptions("xml:/NWCGIncidentOrder/@ShipInstrCity")%> />
		</td>
		<td></td>
	</tr>
	<tr>    
		<td class="detaillabel">	
			<yfc:i18n>State</yfc:i18n>
		</td>
		<td>
			<select class="combobox" <%=getComboOptions("xml:/NWCGIncidentOrder/@ShipInstrState")%>>
				<yfc:loopOptions binding="xml:CommonStateCodeList:/CommonCodeList/@CommonCode" selected="<%=str%>" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
			</select>
		</td>
		<td></td>
	</tr>
</table>
