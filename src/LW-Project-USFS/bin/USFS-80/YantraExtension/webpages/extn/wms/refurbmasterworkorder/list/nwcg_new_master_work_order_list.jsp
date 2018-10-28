<%@taglib  prefix="yfc" uri="/WEB-INF/yfc.tld" %>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="/yantra/console/scripts/dm.js"></script>
<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>

<table class="table" editable="false" width="100%" cellspacing="0">
	<thead>
		<td sortable="no" class="checkboxheader">
			<input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
		</td>
		<td class="tablecolumnheader"><yfc:i18n>Master Work Order/Return #</yfc:i18n></td>
		<td class="tablecolumnheader"><yfc:i18n>Cache_ID</yfc:i18n></td>
		<td class="tablecolumnheader"><yfc:i18n>Incident_Number</yfc:i18n></td>
		<td class="tablecolumnheader"><yfc:i18n>Incident Year</yfc:i18n></td>
		<td class="tablecolumnheader"><yfc:i18n>Incident_Name</yfc:i18n></td>
		<td class="tablecolumnheader"><yfc:i18n>FS Account Code</yfc:i18n></td>
		<td class="tablecolumnheader"><yfc:i18n>Override Code</yfc:i18n></td>
		<td class="tablecolumnheader"><yfc:i18n>BLM Acount Code</yfc:i18n></td>
		<td class="tablecolumnheader"><yfc:i18n>Other Account Code</yfc:i18n></td>
		<td class="tablecolumnheader"><yfc:i18n>Status</yfc:i18n></td>
		
		<!-- BEGIN - CR 835 - Feb 26, 2013 -->
		<td class="tablecolumnheader"><yfc:i18n>MWO Created Date</yfc:i18n></td>
		<!-- END - CR 835 - Feb 26, 2013 -->		
		
		</tr>
	</thead>
	<tbody>
		<yfc:loopXML binding="xml:/NWCGMasterWorkOrderList/@NWCGMasterWorkOrder" id="NWCGMasterWorkOrder">
		<yfc:makeXMLInput name="MWOKey">
  		<yfc:makeXMLKey binding="xml:/NWCGMasterWorkOrder/@MasterWorkOrderKey" value="xml:/NWCGMasterWorkOrder/@MasterWorkOrderKey"/>
  		<yfc:makeXMLKey binding="xml:/NWCGMasterWorkOrder/@MasterWorkOrderNo" value="xml:/NWCGMasterWorkOrder/@MasterWorkOrderNo"/>
 		</yfc:makeXMLInput>
		<tr>
			<td class="checkboxcolumn">
				<input type="checkbox" value='<%=getParameter("MWOKey")%>' name="EntityKey"/>
			</td>
			<td class="tablecolumn">
				<a href="javascript:showDetailFor('<%=getParameter("MWOKey")%>');">
				<yfc:getXMLValue binding="xml:/NWCGMasterWorkOrder/@MasterWorkOrderNo"/>
				</a>
			</td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGMasterWorkOrder/@Node"/></td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGMasterWorkOrder/@IncidentNo"/></td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGMasterWorkOrder/@IncidentYear"/></td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGMasterWorkOrder/@IncidentName"/></td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGMasterWorkOrder/@FSAccountCode"/></td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGMasterWorkOrder/@OverrideCode"/></td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGMasterWorkOrder/@BLMAccountCode"/></td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGMasterWorkOrder/@OtherAccountCode"/></td>
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGMasterWorkOrder/@MWOStatus"/></td> 
			
			<!-- BEGIN - CR 835 - Feb 26, 2013 -->
			<td class="tablecolumn"><yfc:getXMLValue binding="xml:/NWCGMasterWorkOrder/@Createts"/></td> 
			<!-- END - CR 835 - Feb 26, 2013 -->
			
		</tr>
		</yfc:loopXML>
	</tbody>
</table>
