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
<td class="detaillabel" ><yfc:i18n>TimeSensitive</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/InventoryParameters/@TimeSensitive"/></td>
<td class="detaillabel" ><yfc:i18n>Default_Expiration_Days</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/InventoryParameters/@DefaultExpirationDays"/></td>
<td class="detaillabel" ><yfc:i18n>Is_FIFO_Required_and_Item_Tracked</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/InventoryParameters/@IsFIFOTracked"/></td>
<td class="detaillabel" ><yfc:i18n>Primary_Supplier</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@IsPrimarySupplier"/></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Serial_Tracked</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/InventoryParameters@IsSerialTracked"/></td>
<td class="detaillabel" ><yfc:i18n>Tag_Controlled</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/InventoryParameters@TagControlFlag"/></td>
</tr>
</table>

