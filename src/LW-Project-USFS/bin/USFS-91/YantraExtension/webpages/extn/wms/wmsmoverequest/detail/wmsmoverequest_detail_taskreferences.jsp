<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/wmsim.js"></script>
 <table class="view" width="100%">
	<tr>
		<td class="detaillabel" ><yfc:i18n>ShipmentNo</yfc:i18n></td>
		<td class="protectedtext">
			<yfc:makeXMLInput name="ShipmentKey" >
			    <yfc:makeXMLKey binding="xml:/Shipment/@ShipmentKey" value="xml:/MoveRequest/@ShipmentKey" />
	        </yfc:makeXMLInput>	  
            <a <%=getDetailHrefOptions("L01", resolveValue("xml:/MoveRequest/Shipment/@DocumentType"), getParameter("ShipmentKey"), "")%>>	   
				<yfc:getXMLValue binding="xml:/MoveRequest/Shipment/@ShipmentNo" name="MoveRequest"></yfc:getXMLValue>
            </a>
		</td>
		<td class="detaillabel" ><yfc:i18n>Wave_#</yfc:i18n></td>
        <td class="protectedtext">
            <yfc:makeXMLInput name="WaveKey" >
                <yfc:makeXMLKey binding="xml:/WaveSummary/WaveList/Wave/@WaveKey" value="xml:/MoveRequest/@WaveKey" />
            </yfc:makeXMLInput>  
            <a onClick="yfcShowDetailPopup('','',900,600,new Object(),'wave','<%=getParameter("WaveKey")%>');return false;" href="">
                <yfc:getXMLValue binding="xml:/MoveRequest/Wave/@WaveNo" name="MoveRequest">
                </yfc:getXMLValue>
            </a>
        </td>
		<td></td>
	</tr>
	<tr>		
		<td class="detaillabel" ><yfc:i18n>Work_Order_#</yfc:i18n></td>
		<td class="protectedtext">
			<yfc:makeXMLInput name="WorkOrderKey" >
			    <yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderKey" value="xml:/MoveRequest/@WorkOrderKey" />
	        </yfc:makeXMLInput>	  
            <a <%=getDetailHrefOptions("L01", "0007", getParameter("WorkOrderKey"), "")%>>	   
		
				<yfc:getXMLValue binding="xml:/MoveRequest/WorkOrder/@WorkOrderNo" name="MoveRequest">
				</yfc:getXMLValue>
			</a>
		</td>		
		<td></td>
		<td></td>
		<td></td>		
	</tr>
	<tr>
		<td class="detaillabel"><yfc:i18n>Start_No_Earlier_Than</yfc:i18n></td>
		<td nowrap="true" >
			<input class="dateinput" type="text" <%=getTextOptions("xml:/MoveRequest/@StartNoEarlierThan_YFCDATE","xml:/MoveRequest/@StartNoEarlierThan_YFCDATE")%>/>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="text" <%=getTextOptions("xml:/MoveRequest/@StartNoEarlierThan_YFCTIME", "xml:/MoveRequest/@StartNoEarlierThan_YFCTIME")%>/>
				<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
		</td>		
		<td class="detaillabel"><yfc:i18n>Finish_No_Later_Than</yfc:i18n></td>
		<td nowrap="true" >
			<input class="dateinput" type="text" <%=getTextOptions("xml:/MoveRequest/@FinishNoLaterThan_YFCDATE","xml:/MoveRequest/@FinishNoLaterThan_YFCDATE")%>/>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />		
			<input class="dateinput" type="text" <%=getTextOptions("xml:/MoveRequest/@FinishNoLaterThan_YFCTIME", "xml:/MoveRequest/@FinishNoLaterThan_YFCTIME")%>/>
				<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
		</td>
		<td></td>
	</tr>		
</table>