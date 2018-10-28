<%@include file="/yfsjspcommon/yfsutil.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/extn.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_orderDetails.js"></script>
<script>
yfcDoNotPromptForChanges(true);
</script>

<table class="view" cellSpacing="0" cellPadding="0" height="80%">
<tr>    
    <td class="detaillabel">
        <yfc:i18n>Name</yfc:i18n>
    </td>
	<td>
		<input size="40" MaxLength="40" type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnWillPickUpName")%> >
		<input type="hidden" <%=getTextOptions("xml:/Order/Extn/@ExtnWillPickUpName")%> />
	</td>

</tr>

<tr>    
    <td class="detaillabel">
        <yfc:i18n>Info</yfc:i18n>
    </td>
	<td align="left">
	<textarea class="unprotectedtextareainput" rows="5" cols="51" style="word-wrap:break-word;" MaxLength="356"  <%=getTextAreaOptions("xml:/Order/Extn/@ExtnWillPickUpInfo")%>></textarea>
		<input type="hidden" <%=getTextAreaOptions("xml:/Order/Extn/@ExtnWillPickUpInfo")%> />
	</td>    
</tr>

<tr>    

	<td class="detaillabel"><yfc:i18n>Time</yfc:i18n></td>

		<td>
		<input type="text" class="unprotectedinput" onBlur="setRequestDeliverDate(this);setExtnReqDelDateInDiffPanels(this, 'xml:/Order/Extn/@ExtnReqDeliveryDate_YFCDATE')" <%=getTextOptions("xml:/Order/Extn/@TestExtnReqDeliveryDate_YFCDATE")%>/>
		<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
		<input type="text" class="unprotectedinput" onBlur="setExtnReqDelDateInDiffPanels(this, 'xml:/Order/Extn/@ExtnReqDeliveryDate_YFCTIME')"  <%=getTextOptions("xml:/Order/Extn/@TestExtnReqDeliveryDate_YFCTIME")%>/>
		<img class="lookupicon" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
		</td>
</tr>

</table>
