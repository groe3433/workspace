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
<td class="detaillabel" ><yfc:i18n>Symbols</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/HazmatInformation/@Symbols"/></td>
<td class="detaillabel" ><yfc:i18n>Proper_Shipping_Name</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/HazmatInformation/@ProperShippingName"/></td>
<td class="detaillabel" ><yfc:i18n>Hazard_Class</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/HazmatInformation/@HazardClass"/></td>
<td class="detaillabel" ><yfc:i18n>UN_Number</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/HazmatInformation/@UNNumber"/></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Packing_Group</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/HazmatInformation/@PackingGroup"/></td>
<td class="detaillabel" ><yfc:i18n>Label_Code</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/HazmatInformation/@Label"/></td>
<td class="detaillabel" ><yfc:i18n>Special_Provision</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/HazmatInformation/@SpecialProvisions"/></td>
<td class="detaillabel" ><yfc:i18n>Exception</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/HazmatInformation/@Exception"/></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Cargo_Air</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/HazmatInformation/@CargoAir"/></td>
<td class="detaillabel" ><yfc:i18n>Hazmat_Compliance_Key</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/HazmatInformation/@HazmatComplianceKey"/></td>
<td class="detaillabel" ><yfc:i18n>Passenger_Air</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/HazmatInformation/@PassengerAir"/></td>
<!--<td class="detaillabel" ><yfc:i18n>Proper_Shipping_Name</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/HazmatInformation/@ProperShippingName"/></td>-->
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Qty_Bulk</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/HazmatInformation/@QtyBulk"/></td>
<td class="detaillabel" ><yfc:i18n>Vessel</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/HazmatInformation/@Vessel"/></td>
<td class="detaillabel" ><yfc:i18n>Qty_Non_Bulk</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/HazmatInformation/@QtyNonBulk"/></td>
<td class="detaillabel" ><yfc:i18n>Sort_Order</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/HazmatInformation/@SortOrder"/></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>IATA_Hazard_Class</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/Extn/@IATAHazardClass"/></td>
<td class="detaillabel" ><yfc:i18n>IATA_Packing_Group</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/Extn/@IATAPackingGroup"/></td>
<td class="detaillabel" ><yfc:i18n>IATA_Proper_Shipping_Name</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/Extn/@IATAProperShippingName"/></td>
<td class="detaillabel" ><yfc:i18n>IATA_UN_Number</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/Extn/@IATAUNNumber"/></td>
</tr>

</table>