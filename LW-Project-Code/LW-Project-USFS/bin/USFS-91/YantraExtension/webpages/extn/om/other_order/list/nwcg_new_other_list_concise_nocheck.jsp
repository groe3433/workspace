<%@taglib  prefix="yfc" uri="/WEB-INF/yfc.tld" %>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<table class="table" editable="false" width="100%" cellspacing="0">
<thead>
<td sortable="no" class="checkboxheader">
<input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
</td>
<td class="lookupiconheader"><br /></td>
<td class="tablecolumnheader"><yfc:i18n>Other_Order_Number</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Other_Order_Name</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Other_Order_Type</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Customer_PO_No</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Customer_Id</yfc:i18n></td>
</tr>
</thead>
<tbody>
<yfc:loopXML binding="xml:/NWCGIncidentOrderList/@NWCGIncidentOrder" id="NWCGIncidentOrder">
<yfc:makeXMLInput name="incidentKey">
  <yfc:makeXMLKey binding="xml:/NWCGIncidentOrder/@IncidentKey" value="xml:/NWCGIncidentOrder/@IncidentKey"/>
  <yfc:makeXMLKey binding="xml:/NWCGIncidentOrder/@IncidentNo" value="xml:/NWCGIncidentOrder/@IncidentNo"/>
</yfc:makeXMLInput>
<tr>
<yfc:makeXMLInput name="otherListPrintKey">
<yfc:makeXMLKey binding="xml:/Print/Incident/@IncidentKey" value="xml:/NWCGIncidentOrder/@IncidentKey" />
<yfc:makeXMLKey binding="xml:/Print/Incident/@IncidentNo" value="xml:/NWCGIncidentOrder/@IncidentNo" />
<yfc:makeXMLKey binding="xml:/Print/Incident/@Year" value="xml:/NWCGIncidentOrder/@Year" />
</yfc:makeXMLInput>
<td class="checkboxcolumn">
<input type="checkbox" value='<%=getParameter("incidentKey")%>' name="EntityKey" PrintEntityKey='<%=getParameter("otherListPrintKey")%>' />
</td>

<td class="tablecolumn">
</td>
<td class="tablecolumn">
<a href="javascript:showDetailFor('<%=getParameter("incidentKey")%>');">
<yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@IncidentNo"/>
</a>
</td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@IncidentName"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@OtherOrderType"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@CustomerPONo"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGIncidentOrder/@CustomerId"/></td>
</tr>
</yfc:loopXML>
</tbody>
</table>