<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>

<table class="view" width="100%">
<yfc:makeXMLInput name="SeqKey">
<yfc:makeXMLKey binding="xml:/NWCGBillingTransaction/@SequenceKey" value="xml:/NWCGBillingTransaction/@SequenceKey"/>
</yfc:makeXMLInput>
<tr>
<td class="detaillabel" ><yfc:i18n>FS_Acct_Code</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@IncidentFsAcctCode"/></td>
<td class="detaillabel" ><yfc:i18n>Override_Code</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@IncidentFsOverrideCode"/></td>
<td class="detaillabel" ><yfc:i18n>BLM_Acct_Code</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@IncidentBlmAcctCode"/></td>
<td class="detaillabel" ><yfc:i18n>Other_Acct_Code</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@IncidentOtherAcctCode"/></td>
</tr>

<tr>
<td class="detaillabel" ><yfc:i18n>Last_FS_Acct_Code</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@LastFsAcctCode"/></td>
<td class="detaillabel" ><yfc:i18n>Last_Override_Code</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@LastFsOverrideCode"/></td>
<td class="detaillabel" ><yfc:i18n>Last_BLM_Acct_Code</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@LastBlmAcctCode"/></td>
<td class="detaillabel" ><yfc:i18n>Last_Other_Acct_Code</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@LastOtherAcctCode"/></td>
</tr>

<tr>
<td class="detaillabel" ><yfc:i18n>Account_Split</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@IsAccountSplit"/></td>
</tr>

</table>

