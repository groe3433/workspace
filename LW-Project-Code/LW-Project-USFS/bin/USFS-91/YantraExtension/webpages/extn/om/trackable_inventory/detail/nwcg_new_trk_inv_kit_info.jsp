<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/orderentry.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.dom.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<table class="view" width="100%">
<tr>
<td class="detaillabel" ><yfc:i18n>Kit_Cache_Item</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@KitItemID"/></td>
<!-- CR 114 ks 2009-09-30 -->
<td class="detaillabel" ><yfc:i18n>Trackable_Kit_ID_Number</yfc:i18n></td>
<!-- end CR 114 -->
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@KitSerialNo"/></td>
<td class="detaillabel" ><yfc:i18n>Primary_Kit_Cache_Item</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@KitPrimaryItemID"/></td>
<!-- CR 114 ks 2009-09-30 -->
<td class="detaillabel" ><yfc:i18n>Primary_Kit_Trackable_ID_Number</yfc:i18n></td>
<!-- end CR 114  -->
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@KitPrimarySerialNo"/></td>
</tr>
</table>

