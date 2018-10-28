<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

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
<td class="detaillabel" ><yfc:i18n>UnitWeight</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@UnitWeight"/></td>
<td class="detaillabel" ><yfc:i18n>UnitWeightUOM</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@UnitWeightUOM"/></td>
<td class="detaillabel" ><yfc:i18n>UnitLength</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@UnitLength"/></td>
<td class="detaillabel" ><yfc:i18n>UnitLengthUOM</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@UnitLengthUOM"/></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>UnitWidth</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@UnitWidth"/></td>
<td class="detaillabel" ><yfc:i18n>UnitWidthUOM</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@UnitWidthUOM"/></td>
<td class="detaillabel" ><yfc:i18n>UnitHeight</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@UnitHeight"/></td>
<td class="detaillabel" ><yfc:i18n>UnitHeightUOM</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@UnitHeightUOM"/></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Unit_Volume</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="Xml:/Item/PrimaryInformation/@UnitVolume"/></td>
<td class="detaillabel" ><yfc:i18n>Volume_UOM</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="Xml:/Item/PrimaryInformation/@UnitVolumeUOM"/></td>
</tr>
</table>

