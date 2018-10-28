<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/moverequest.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/wmsim.js"></script>
<table class="view" width="100%">
	<tr><td class="detaillabel"/><td/><td class="detaillabel"/><td/><td class="detaillabel"/><td/></tr>	
	<tr>
		<td class="detaillabel" ><yfc:i18n>Shipment_#</yfc:i18n></td>
		<td nowrap="true">
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/Shipment/@ShipmentNo") %> />
			<input type="hidden" <%=getTextOptions("xml:/MoveRequest/Shipment/@ShipmentKey") %> />
			 <img class="lookupicon" onclick="callShipmentLookup('xml:/MoveRequest/Shipment/@ShipmentNo',
				'xml:/MoveRequest/Shipment/@ShipmentKey','shipmentlookup')"
			<%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Shipment_No") %> />
		</td>
		<td class="detaillabel" ><yfc:i18n>Wave_#</yfc:i18n></td>
		<td nowrap="true">
			 <input type="text" class="unprotectedinput" 									<%=getTextOptions("xml:/MoveRequest/Wave/@WaveNo") %> />
			 <img class="lookupicon" onclick="callLookup(this,'wavelookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Wave") %> />
		</td>		
		<td class="detaillabel" ><yfc:i18n>Work_Order_#</yfc:i18n></td>
		<td nowrap="true">
			 <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/WorkOrder/@WorkOrderNo") %> />
			<img class="lookupicon" onclick="callLookup(this,'workorderlookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Work_Order") %> />
		</td>	
	</tr>
</table>