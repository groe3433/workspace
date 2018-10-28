<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<table class="table" editable="false">
<thead>
<td class="tablecolumnheader" sortable="no">&nbsp;</td>
<td class="tablecolumnheader"><yfc:i18n>Reservation ID</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Item ID</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Description</yfc:i18n></td>
</tr>
</thead>
<tbody>
<yfc:loopXML binding="xml:/ReservationItemList/@Item" id="Item">
<yfc:makeXMLInput name="ResKey">
  <yfc:makeXMLKey binding="xml:/Item/@ReservationID" value="xml:/Item/@ReservationID"/>
</yfc:makeXMLInput>
<tr>
<td class="tablecolumn">
<img class="icon" onClick="setLookupValue(this.value)"  value="<%=resolveValue("xml:/Item/@ReservationID")%>" <%=getImageOptions(YFSUIBackendConsts.GO_ICON,"Click_to_Select")%> />
</td>

<td class="tablecolumn">
<yfc:getXMLValue binding="xml:/Item/@ReservationID"/>
</a>
</td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/Item/@ItemID"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/Item/@Description"/></td>
</tr>
</yfc:loopXML>
</tbody>
</table>
