<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/orderentry.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.dom.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<table class="view" width="100%">
<tr>
<td class="detaillabel" ><yfc:i18n>Status</yfc:i18n></td>
<td class="protectedtext">(<yfc:getXMLValue binding="xml:/NWCGTrackableItem/@SerialStatus"/>)&nbsp;&nbsp;&nbsp;<yfc:getXMLValue binding="xml:/NWCGTrackableItem/@SerialStatusDesc"/></td>
<td class="detaillabel" ><yfc:i18n>Cache_ID</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@StatusCacheID"/></td>
<td class="detaillabel" ><yfc:i18n>Order_Number</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@StatusIncidentNo"/></td>
<td class="detaillabel" ><yfc:i18n>Year</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@StatusIncidentYear"/></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Customer_Unit_ID</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@StatusBuyerOrganizationCode"/></td>
<td class="detaillabel" ><yfc:i18n>System_Number</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@SystemNo"/></td>
</tr>
</table>

