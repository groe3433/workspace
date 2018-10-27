<%@taglib  prefix="yfc" uri="/WEB-INF/yfc.tld" %>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>

<table class="table" editable="false" width="100%" cellspacing="0">
<thead>
<td class="tablecolumnheader"><yfc:i18n>Incident_Number</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Incident_Name</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Incident_Host</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Incident_Type</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Incident_Source</yfc:i18n></td>
</tr>
</thead>
<tbody>
<yfc:loopXML binding="xml:/NWCGIncidentOrderList/@NWCGIncidentOrder" id="NWCGIncidentOrder">
<tr>
<yfc:makeXMLInput name="incidentKey">
<yfc:makeXMLKey binding="xml:/NWCGIncidentOrder/@IncidentKey" value="xml:/NWCGIncidentOrder/@IncidentKey"/>
</yfc:makeXMLInput>
<td class="tablecolumn">
<a href="javascript:showDetailFor('<%=getParameter("incidentKey")%>');">
<yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@IncidentNo"/>
</a>
</td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@IncidentName"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@IncidentHost"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@IncidentType"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@IncidentSource"/></td>
</tr>
</yfc:loopXML>
</tbody>
</table>
