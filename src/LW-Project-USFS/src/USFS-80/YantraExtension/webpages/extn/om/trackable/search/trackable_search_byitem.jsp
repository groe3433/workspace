<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/paymentutils.jspf" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>

<table class="view">
<tr>
<td class="searchlabel" >
<fieldset>
<legend class="searchlabel"><yfc:i18n>General</yfc:i18n>
</legend>
<span class="protectedtext"/>
<tr>
<td class="searchlabel">
<yfc:i18n>Trackable_ID</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@SerialNoQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@SerialNoQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@SerialNo")%>/>
</td>
</tr>
<tr>
<td class="searchlabel">
<yfc:i18n>Cache_Item</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Shipment/ShipmentLines/ShipmentLine/@ItemIDQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Shipment/ShipmentLines/ShipmentLine/@ItemIDQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine/@ItemID")%>/>
</td>
</tr>
<tr>
<td class="searchlabel">
<yfc:i18n>Manufacturer_Serial</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute2QryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute2QryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute2")%>/>
</td>
</tr>
<tr>
<td class="searchlabel">
<yfc:i18n>Acquisition_Cost</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute2QryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute2QryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute2")%>/>
</td>
</tr>
<tr>
<td nowrap="true">
<yfc:i18n>Acquisition_Date</yfc:i18n>
<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/@FromOrderDate")%>/>
<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
</td>
</tr>
<tr>
<td class="searchlabel">
<yfc:i18n>Manufacturer_Name</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1QryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1QryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1")%>/>
</td>
</tr>
</fieldset>
</td>
</tr>
<tr>
<td class="searchlabel" >
<fieldset>
<legend class="searchlabel"><yfc:i18n>Issued_To_Info</yfc:i18n>
</legend>
<span class="protectedtext"/>
<tr>
<td class="searchlabel">
<yfc:i18n>Order_Number</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1QryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1QryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1")%>/>
</td>
</tr>
<tr>
<td class="searchlabel">
<yfc:i18n>Year</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1QryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1QryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1")%>/>
</td>
</tr>
<tr>
<td class="searchlabel">
<yfc:i18n>Customer_Unit_ID</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1QryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1QryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1")%>/>
</td>
</tr>
</fieldset>
</td>
</tr>
<tr>
<td class="searchlabel" >
<fieldset>
<legend class="searchlabel"><yfc:i18n>Last_Transaction_Info</yfc:i18n>
</legend>
<span class="protectedtext"/>
<tr>
<td class="searchlabel">
<yfc:i18n>Last_Transaction_Type</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1QryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1QryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1")%>/>
</td>
</tr>
<tr>
<td class="searchlabel">
<yfc:i18n>Document_Number</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1QryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1QryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1")%>/>
</td>
</tr>
<tr>
<td class="searchlabel">
<yfc:i18n>Date</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1QryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1QryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1")%>/>
</td>
</tr>
<tr>
</fieldset>
</td>
</tr>
<tr>
<td class="searchlabel" >
<fieldset>
<legend class="searchlabel"><yfc:i18n>Kit_Information</yfc:i18n>
</legend>
<span class="protectedtext"/>
<tr>
<td class="searchlabel">
<yfc:i18n>Kit_Item</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1QryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1QryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1")%>/>
</td>
</tr>
<tr>
<td class="searchlabel">
<yfc:i18n>Kit_Property_ID</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1QryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1QryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@LotAttribute1")%>/>
</td>
</tr>
</fieldset>
</td>
</tr>
</table>
