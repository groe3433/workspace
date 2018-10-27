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
<td class="detaillabel" ><yfc:i18n>Harmonized_Code</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/ClassificationCodes/@HarmonizedCode"/></td>
<td class="detaillabel" ><yfc:i18n>Commodity_Code</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/ClassificationCodes/@CommodityCode"/></td>
<td class="detaillabel" ><yfc:i18n>ECCN_No</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/ClassificationCodes/@ECCNNo"/></td>
<td class="detaillabel" ><yfc:i18n>UNSPSC</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/ClassificationCodes/@UNSPSC"/></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>NMFCClass</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/ClassificationCodes/@NMFCClass"/></td>
<td class="detaillabel" ><yfc:i18n>NMFCCode</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/ClassificationCodes/@NMFCCode"/></td>
<td class="detaillabel" ><yfc:i18n>NAICS_Code</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/ClassificationCodes/@NAICSCode"/></td>
<td class="detaillabel" ><yfc:i18n>Hazmat_Class</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/ClassificationCodes/@HazmatClass"/></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Product_Line</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@ProductLine"/></td>
<td class="detaillabel" ><yfc:i18n>Item_Type</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@ItemType"/></td>
<td class="detaillabel" ><yfc:i18n>Velocity_Code</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/ClassificationCodes/@VelocityCode"/></td>
<td class="detaillabel" ><yfc:i18n>Operational_Configuration_Complete</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/ClassificationCodes/@OperationalConfigurationComplete"/></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Item_Class</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/ClassificationCodes/@TaxProductCode"/></td>
</tr>
</table>

