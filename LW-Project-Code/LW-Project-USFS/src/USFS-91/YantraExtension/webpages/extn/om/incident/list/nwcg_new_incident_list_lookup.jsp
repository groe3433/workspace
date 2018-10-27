<%@include file="/yfsjspcommon/yfsutil.jspf"%>


<table class="table" editable="false">
<thead>
<td class="tablecolumnheader" sortable="no">&nbsp;</td>
<td class="tablecolumnheader"><yfc:i18n>Incident_Number</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Year</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Incident_Name</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Incident_Host</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Incident_Type</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Incident_Source</yfc:i18n></td>
</tr>
</thead>
<tbody>
<yfc:loopXML binding="xml:/NWCGIncidentOrderList/@NWCGIncidentOrder" id="NWCGIncidentOrder">
<yfc:makeXMLInput name="incidentKey">
  <yfc:makeXMLKey binding="xml:/NWCGIncidentOrder/@IncidentKey" value="xml:/NWCGIncidentOrder/@IncidentKey"/>
</yfc:makeXMLInput>
<tr>
<td class="tablecolumn">
<!-- Commented out by Jeremy
<img class="icon" onClick="setLookupValue(this.value)"  value="<%=resolveValue("xml:/NWCGIncidentOrder/@IncidentNo")%>" <%=getImageOptions(YFSUIBackendConsts.GO_ICON,"Click_to_Select")%> />
</td> -->

<% String otherOrder 		= resolveValue("xml:/NWCGIncidentOrder/@IsOtherOrder");
%>


<% if(otherOrder.equalsIgnoreCase("N"))  { %>
<img class="icon" onClick="setIncidentLookupValue('<%=resolveValue("xml:/NWCGIncidentOrder/@IncidentNo")%>','<%=resolveValue("xml:/NWCGIncidentOrder/@Year")%>')" <%=getImageOptions(YFSUIBackendConsts.GO_ICON,"Click_to_Select")%> />

<% }  else {
%>

<img class="icon" onClick="setOtherLookupValue('<%=resolveValue("xml:/NWCGIncidentOrder/@IncidentNo")%>')" <%=getImageOptions(YFSUIBackendConsts.GO_ICON,"Click_to_Select")%> />

<% } %>



</td>

<td class="tablecolumn">
<yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@IncidentNo"/>
</td>

<!-- Jeremy Added -->
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@Year"/></td>


<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@IncidentName"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@IncidentHost"/></td>
<td class="tablecolumn">
<%
YFCElement commonCodeInput = YFCDocument.parse("<CommonCode CodeValue=\"" + resolveValue("xml:/NWCGIncidentOrder/@IncidentType") + "\"  />").getDocumentElement();
YFCElement commonCodeTemplate = YFCDocument.parse("<CommonCodeList> <CommonCode CodeShortDescription=\"\"/> </CommonCodeList>").getDocumentElement();

%>
<yfc:callAPI apiName="getCommonCodeList" inputElement="<%=commonCodeInput%>" templateElement="<%=commonCodeTemplate%>" outputNamespace="CommonCodeList"/>
<!-- displays the value from returned API call -->

<% if(otherOrder.equalsIgnoreCase("N"))  { %>

<yfc:getXMLValue binding="xml:CommonCodeList:/CommonCodeList/CommonCode/@CodeShortDescription"/>

<%}%>

</td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@IncidentSource"/></td>
</tr>
</yfc:loopXML>
</tbody>
</table>
