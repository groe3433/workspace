<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>

<table class="view" width="100%">
<yfc:makeXMLInput name="itemKey">
<yfc:makeXMLKey binding="xml:/Item/@ItemKey" value="xml:/Item/@ItemKey"/>
</yfc:makeXMLInput>
<tr>
<td>
<input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
<input type="hidden" name="chkCopyItemEntityKey" value='<%=getParameter("itemKey")%>' />
<input type="hidden" name="xml:/Item/@OrganizationCode" value='<%=getValue("Item", "xml:/Item/@OrganizationCode")%>'/>
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/@OrganizationCode"/></td>
<td class="detaillabel" ><yfc:i18n>Item_ID</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/@ItemID"/></td>
<td class="detaillabel" ><yfc:i18n>Global_Item_ID</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/@GlobalItemID"/></td>
<td class="detaillabel" ><yfc:i18n>Description</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@Description"/></td>
<td class="detaillabel" ><yfc:i18n>UOM</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/@UnitOfMeasure"/></td>

</tr>
</table>
