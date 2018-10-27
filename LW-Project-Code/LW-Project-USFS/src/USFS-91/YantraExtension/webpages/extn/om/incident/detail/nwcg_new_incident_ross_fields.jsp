<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>


<table class="view" width="100%" editable="false">
   <tr> 
   		<td class="detaillabel"><yfc:i18n>ROSS_Incident_Status</yfc:i18n></td>
		<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@ROSSIncidentStatus")%></td>

   		<td class="detaillabel"><yfc:i18n>ROSS_Dispatch_ID</yfc:i18n></td>
		<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@ROSSDispatchID")%></td>

   		<td class="detaillabel"><yfc:i18n>Is_Complex_Indicator</yfc:i18n></td>
		<td  class="checkbox" >
			<input type="checkbox" onclick="this.checked=!this.checked"
				<%=getCheckBoxOptions("xml:/NWCGIncidentOrder/@IsComplexIndicator","xml:/NWCGIncidentOrder/@IsComplexIndicator","Y")%> yfsCheckValue='Y' yfsUncheckedValue='N'/>
		</td>
   </tr>
   <tr>
   		<td class="detaillabel"><yfc:i18n>Request_Number_Block_Start</yfc:i18n></td>
		<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@RequestNoBlockStart")%></td>

   		<td class="detaillabel"><yfc:i18n>Request_Number_Block_End</yfc:i18n></td>
		<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@RequestNoBlockEnd")%></td>
   		<td class="detaillabel"><yfc:i18n>Last_Updated_From_ROSS</yfc:i18n></td>
		<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@LastUpdatedFromROSS")%></td>
   </tr>
   <tr>
   		<td class="detaillabel"><yfc:i18n>Geographic_Coordinate_Latitude</yfc:i18n></td>
		<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@GeographicCoordinateLatitude")%></td>

   		<td class="detaillabel"><yfc:i18n>Geographic_Coordinate_Longitude</yfc:i18n></td>
		<td class="protectedtext"><%=resolveValue("xml:/NWCGIncidentOrder/@GeographicCoordinateLongitude")%></td>
   		<td/><td/>
   </tr>
</table>
