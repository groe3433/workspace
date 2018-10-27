<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/orderentry.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.dom.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<table class="view" width="100%">

<yfc:makeXMLInput name="orderKey">
	<yfc:makeXMLKey binding="xml:/NWCGTrackableItem/@TrackableItemKey" value="xml:/NWCGTrackableItem/@TrackableItemKey" />
</yfc:makeXMLInput>    

<tr>
<td class="detaillabel" ><yfc:i18n>Trackable_ID</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@SerialNo"/></td>
<td class="detaillabel" ><yfc:i18n>Cache_Item</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@ItemID"/></td>
<td class="detaillabel" ><yfc:i18n>Item_Description</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@ItemShortDescription"/></td>
<td class="detaillabel" ><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@UnitOfMeasure"/></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Manufacturer_Serial</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@SecondarySerial"/></td>
<td class="detaillabel" ><yfc:i18n>Manufacturer</yfc:i18n></td>
<!-- <td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@LotAttribute1"/></td> -->
<td><input type="text" class="unprotectedinput" size=15 <%=getTextOptions("xml:/NWCGTrackableItem/@LotAttribute1")%>/></td>
<td class="detaillabel" ><yfc:i18n>Model_Name_Number</yfc:i18n></td>
<!--<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@LotAttribute3"/></td>-->
<td><input type="text" class="unprotectedinput" size=15 <%=getTextOptions("xml:/NWCGTrackableItem/@LotAttribute3")%>/></td>
<td class="detaillabel" ><yfc:i18n>Acquisition_Cost</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@AcquisitionCost"/></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Acquisition_Date</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@AcquisitionDate"/></td>
<td class="detaillabel" ><yfc:i18n>Owner_Unit_ID</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@OwnerUnitID"/></td>
<td class="detaillabel" ><yfc:i18n>Name_of_Owner_Unit_ID</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@OwnerUnitName"/></td>
<td class="detaillabel" ><yfc:i18n>Date_Last_Tested</yfc:i18n></td>
<!--<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@RevisionNo"/></td>-->
<td>
<input class="dateinput" type="text" <%=getTextOptions("xml:/NWCGTrackableItem/@RevisionNo")%>/>
<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
</td>
</tr>
<tr>
<!-- CR 368 KJS -->
<td class="detaillabel" ><yfc:i18n>Manufacturing_Date</yfc:i18n></td>
<!--<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGTrackableItem/@ManufacturingDate"/></td>-->
<td>
<input class="dateinput" type="text" <%=getTextOptions("xml:/NWCGTrackableItem/@ManufacturingDate")%>/>
<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
</td>
<td><input type="hidden" <%=getTextOptions("xml:/NWCGTrackableItem/@UnitOfMeasure","xml:/NWCGTrackableItem/@UnitOfMeasure")%>/></td>
<td><input type="hidden" <%=getTextOptions("xml:/NWCGTrackableItem/@SerialStatus","xml:/NWCGTrackableItem/@SerialStatus")%>/></td>
<td><input type="hidden" <%=getTextOptions("xml:/NWCGTrackableItem/@ReasonForCancellation","")%>/></td>
</tr>
</table>

