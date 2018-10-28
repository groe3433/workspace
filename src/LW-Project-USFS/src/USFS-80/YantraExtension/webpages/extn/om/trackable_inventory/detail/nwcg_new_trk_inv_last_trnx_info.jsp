<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>

<% String TDate = resolveValue("xml:/NWCGTrackableItem/@LastTransactionDate"); 
		   String TDateStr1 = TDate.substring(0,4);
		   String TDateStr2 = TDate.substring(5,7);
           String TDateStr3 = TDate.substring(8,10);
           TDate = TDateStr2+"/"+TDateStr3+"/"+TDateStr1;
		   //System.out.println("TDate " + TDate); 
%>

<table class="view" width="100%">
<tr>
<td class="detaillabel" ><yfc:i18n>Order_Number</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@StatusIncidentNo"/></td>
<td class="detaillabel" ><yfc:i18n>Year</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@StatusIncidentYear"/></td>
<td class="detaillabel" ><yfc:i18n>Customer_Unit_ID</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@StatusBuyerOrganizationCode"/></td>
<td class="detaillabel" ><yfc:i18n>Type</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@Type"/></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Document_Number</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@LastDocumentNo"/></td>
<td class="detaillabel" ><yfc:i18n>Date</yfc:i18n></td>
<!--<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@LastTransactionDate"/></td>-->
<td class="protectedtext"><%=TDate%></td>

</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>FS_Acct_Code</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@FSAccountCode"/></td>
<td class="detaillabel" ><yfc:i18n>Override_Code</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@OverrideCode"/></td>
<td class="detaillabel" ><yfc:i18n>BLM_Acct_Code</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@BLMAccountCode"/></td>
<td class="detaillabel" ><yfc:i18n>Other_Acct_Code</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@OtherAccountCode"/></td>
</tr>
</table>

