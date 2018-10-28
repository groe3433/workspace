<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%  
	String one=getTodayDate();
	YFCDate oStartDate = new YFCDate(); 
	oStartDate.setBeginOfDay();
	YFCDate oEndDate = new YFCDate(); 
	oEndDate.setEndOfDay();
%>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/wmsim.js"></script>
<table width="100%" class="view">
<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
		<jsp:param name="ShowDocumentType" value="false"/>
		<jsp:param name="ShowNode" value="true"/>
		<jsp:param name="EnterpriseCodeBinding" value="xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@EnterpriseCode"/>
		<jsp:param name="NodeBinding" value="xml:/MoveRequest/@Node"/>
        <jsp:param name="RefreshOnNode" value="true"/>
        <jsp:param name="EnterpriseListForNodeField" value="true"/>
		<jsp:param name="RefreshOnEnterpriseCode" value="true"/>
		<jsp:param name="ScreenType" value="search"/>
</jsp:include>
<yfc:callAPI apiID="AP2"/>
<% 
	YFCElement activityListElem = (YFCElement) request.getAttribute("ActivityGroupList");
	for(Iterator listItr = activityListElem.getChildren();listItr.hasNext();) {
		YFCElement activityElem = (YFCElement)listItr.next();
		String sActivityGroup = activityElem.getAttribute("ActivityGroupId");
		if( sActivityGroup.equals("RETRIEVAL")|| sActivityGroup.equals("PUTAWAY")) {
			activityListElem.removeChild(activityElem);
		}
	}
	request.setAttribute("ActivityGroupList",activityListElem);
%>
<tr>
    <td class="searchlabel" ><yfc:i18n>Activity_Group</yfc:i18n></td>
</tr>
<tr >
     <td class="searchcriteriacell" nowrap="true">
       <select class="combobox"  <%=getComboOptions("xml:/MoveRequest/@FromActivityGroup")%> >
            <yfc:loopOptions binding="xml:ActivityGroupList:/BaseActivityGroupList/@BaseActivityGroup" 
                name="ActivityGroupName" isLocalized="Y" value="ActivityGroupId" selected="xml:/MoveRequest/@FromActivityGroup"/>
        </select>   
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>For_Activity_Code</yfc:i18n></td>
</tr>
<tr >
     <td class="searchcriteriacell" nowrap="true">
       <select class="combobox"  <%=getComboOptions("xml:/MoveRequest/@ForActivityCode")%> >
            <yfc:loopOptions binding="xml:ActivityCodeList:/Activities/@Activity" 
                name="ActivityCode" value="ActivityCode" selected="xml:/MoveRequest/@ForActivityCode"/>
        </select>   
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Requesting_User_ID</yfc:i18n></td>
</tr>
<tr >
    <td class="searchcriteriacell" nowrap="true">   
		<select <%=getComboOptions("xml:/MoveRequest/@RequestUserIdQryType" ) %> class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/MoveRequest/@RequestUserIdQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/@RequestUserId") %> />
        <img class="lookupicon" onclick="callLookup(this,'user')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_User") %> />
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Wave_#</yfc:i18n></td>
</tr>
<tr >
    <td class="searchcriteriacell" nowrap="true">   
		<select <%=getComboOptions("xml:/MoveRequest/Wave/@WaveNoQryType" ) %> class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/MoveRequest/Wave/@WaveNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/Wave/@WaveNo") %> />
        <img class="lookupicon" onclick="callLookup(this,'wavelookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Wave") %> />
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>ShipmentNo</yfc:i18n></td>
</tr>
<tr >
    <td class="searchcriteriacell" nowrap="true">
		<select <%=getComboOptions("xml:/MoveRequest/Shipment/@ShipmentNoQryType" ) %> class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/MoveRequest/Shipment/@ShipmentNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/Shipment/@ShipmentNo") %> />
        <img class="lookupicon" onclick="callShipmentLookup('xml:/MoveRequest/Shipment/@ShipmentNo','xml:/MoveRequest/Shipment/@ShipmentKey','shipmentlookup')"
		<%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Shipment_No") %> />
		
		<input type="hidden" name="xml:/MoveRequest/Shipment/@ShipmentKey" value='<%=resolveValue("xml:/MoveRequest/Shipment/@ShipmentKey")%>'/> 
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Work_Order_#</yfc:i18n></td>
</tr>
<tr >
    <td class="searchcriteriacell" nowrap="true">        
		<select <%=getComboOptions("xml:/MoveRequest/WorkOrder/@WorkOrderNoQryType" ) %> class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/MoveRequest/WorkOrder/@WorkOrderNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/WorkOrder/@WorkOrderNo") %> />
        <img class="lookupicon" onclick="callLookup(this,'workorderlookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Work_Order") %> />
    </td>
</tr>
<input name="xml:yfcSearchCriteria:/MoveRequest/@Mode"  type="hidden" value="byreferences"/>
</table>