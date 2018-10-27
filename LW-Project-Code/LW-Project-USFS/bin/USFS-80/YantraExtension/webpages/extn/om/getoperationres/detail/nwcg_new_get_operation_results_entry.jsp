<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/extn/scripts/extn.js"></script>
<script language="javascript">
	IgnoreChangeNames();
	yfcDoNotPromptForChanges(true);
</script>

<table class="view" width="100%">
<td>

</td>
<%	String strName = resolveValue("xml:/GetOperationResultsReq/@DistributionID");%>
<tr>

</td>
<%	if(strName != null  && (!strName.equals(""))  )
						{
						%>


<td class="detaillabel"><yfc:i18n>Distribution_ID</yfc:i18n></td>

<td class="protectedtext"><yfc:getXMLValue binding="xml:/GetOperationResultsReq/@DistributionID"/></td>

<td class="detaillabel" ><yfc:i18n>Description_From_Ross</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/GetOperationResultsReq/@DescriptionFromRoss"/></td>
</tr>

<tr>
<td class="detaillabel" ><yfc:i18n>Latest_Message_Status</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/GetOperationResultsReq/@LatestMessageStatus"/></td>
<td class="detaillabel" ><yfc:i18n>Return_Code</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/GetOperationResultsReq/@ReturnCode"/></td>
<td class="hidden"><yfc:getXMLValue binding="xml:/GetOperationResultsReq/@LatestMessageKey"/></td>
<tr>
<td class="detaillabel" ><yfc:i18n>Returned_Message</yfc:i18n></td>
<td class="protectedtext" style="HEIGHT:100px;WIDTH:170px;"><yfc:getXMLValue binding="xml:/GetOperationResultsReq/@ReturnedMessage"/></td>

<td class="detaillabel" ><yfc:i18n>Delivery_Message_Will_Be</yfc:i18n></td>
<td class="protectedtext" style="HEIGHT:100px;WIDTH:170px;"><yfc:getXMLValue binding="xml:/GetOperationResultsReq/@DeliverToRossMessage"/></td>

</tr>




							
<%}
else { %>

<td class="detaillabel"><yfc:i18n>Distribution_ID</yfc:i18n></td>
<td>
<input type="text" binding="xml:/GetOperationResultsReq/@DistributionID" size="40" maxLength="40" class="unprotectedinput" <%=getTextOptions("xml:/GetOperationResultsReq/@DistributionID") %> />

<%}%>

</tr>
<td>
</td>
</table>
