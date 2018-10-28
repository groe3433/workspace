<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/moverequest.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/workorder.js"></script>
<yfc:callAPI apiID="AP1"/>
<yfc:callAPI apiID="AP3"/>
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
 <table class="view" width="100%">
	<tr>
		<td class="detaillabel" ><yfc:i18n>Activity_Group</yfc:i18n></td>

        <% if(equals("CREATED",resolveValue("xml:/MoveRequest/@Status"))&&equals("N",resolveValue("xml:/MoveRequest/@HasExceptions"))) { %>
           <td> <select class="combobox"  <%=getComboOptions("xml:/MoveRequest/@FromActivityGroup")%> >
                 <yfc:loopOptions binding="xml:ActivityGroupList:/BaseActivityGroupList/@BaseActivityGroup"                name="ActivityGroupName" isLocalized="Y" value="ActivityGroupId" selected="xml:/MoveRequest/@FromActivityGroup"/>
	        </select> </td>			 
        <% } else { %>
			<td class="protectedtext"><yfc:getXMLValueI18NDB binding="xml:/MoveRequest/FromActivityGroup/@Description" name="MoveRequest"></yfc:getXMLValueI18NDB></td>
        <% } %>
		<td class="detaillabel" ><yfc:i18n>For_Activity_Code</yfc:i18n></td>
        <% if(equals("CREATED",resolveValue("xml:/MoveRequest/@Status"))&&equals("N",resolveValue("xml:/MoveRequest/@HasExceptions"))) { %>
           <td> <select class="combobox"  <%=getComboOptions("xml:/MoveRequest/@ForActivityCode")%> >
                 <yfc:loopOptions binding="xml:ActivityCodeList:/Activities/@Activity" isLocalized="Y"
                name="ActivityCode" value="ActivityCode" selected="xml:/MoveRequest/@ForActivityCode"/>
	        </select> </td>			 
        <% } else { %>
			<td class="protectedtext"><yfc:getXMLValueI18NDB binding="xml:/MoveRequest/ForActivityCode/@Description" name="MoveRequest"></yfc:getXMLValueI18NDB></td>
        <% } %>
			<yfc:makeXMLInput name="EntityKey">
			<yfc:makeXMLKey binding="xml:/MoveRequest/@MoveRequestKey" value="xml:/MoveRequest/@MoveRequestKey" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/@MoveRequestNo" value="xml:/MoveRequest/@MoveRequestNo" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/@Node" value="xml:/MoveRequest/@Node" />
			<yfc:makeXMLKey binding="xml:/MoveRequest/@UIMode" value="Audit" />	
			</yfc:makeXMLInput>
			<input type="hidden" name="xml:/MoveRequest/@MoveRequestNo" value='<%=resolveValue("xml:/MoveRequest/@MoveRequestNo")%>' />
			<input type="hidden" <%=getTextOptions("xml:/MoveRequest/@FromActivityGroup")%> />
			<input type="hidden" <%=getTextOptions("xml:/MoveRequest/@Node")%> />
			<input type="hidden" <%=getTextOptions("xml:/MoveRequest/@HasExceptions")%> />
			<input type="hidden" <%=getTextOptions("xml:/MoveRequest/@Status")%> />
			<input type="hidden" name="xml:/MoveRequest/@ReasonCode" />
			<input type="hidden" name="xml:/MoveRequest/@ReasonText"/>
			<input type="hidden" name="xml:/MoveRequest/@MoveRequestKey" value='<%=resolveValue("xml:/MoveRequest/@MoveRequestKey")%>'/>	
			<yfc:makeXMLInput name="shipmentKey">
				<yfc:makeXMLKey binding="xml:/Shipment/@ShipmentKey" 
					value="xml:/MoveRequest/@ShipmentKey" />
			</yfc:makeXMLInput>
			<input type="hidden" name="shipmentKey" value='<%=getParameter("shipmentKey")%>'/>	
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Node</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequest/@Node" name="MoveRequest"></yfc:getXMLValue></td>
		<td class="detaillabel" ><yfc:i18n>Move_Request_#</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequest/@MoveRequestNo" name="MoveRequest"></yfc:getXMLValue></td>
	</tr>
	<tr>
		<td class="detaillabel"><yfc:i18n>Requested_By</yfc:i18n></td>
		<td>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/@RequestUserId","xml:/MoveRequest/@RequestUserId") %> />
		</td>
		<td class="detaillabel"><yfc:i18n>Status</yfc:i18n></td>
		<td class="protectedtext">
			<yfc:getXMLValueI18NDB binding="xml:/MoveRequest/Status/@Description" name="MoveRequest"></yfc:getXMLValueI18NDB>
			<input type="hidden" value='<%=getParameter("EntityKey")%>' name="RequestKey"/>
		</td>

	</tr>	
	<tr>
		<td class="detaillabel"><yfc:i18n>Has_Exceptions</yfc:i18n></td>
		<td class="protectedtext" ><yfc:getXMLValue binding="xml:/MoveRequest/@HasExceptions" name="MoveRequest"></yfc:getXMLValue></td>		
		<td class="detaillabel" ><yfc:i18n>Priority</yfc:i18n></td>
		<%if(equals("CREATED",resolveValue("xml:/MoveRequest/@Status"))){
			if(equals("Y", resolveValue("xml:/MoveRequest/@HasExceptions")) &&
				!equals("0", resolveValue("xml:/MoveRequest/@HasTasks"))){%>
				<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequest/Priority/@Description" name="MoveRequest"></yfc:getXMLValue></td>
			<%}else{%>
				<td nowrap="true">
					<select class="combobox"  <%=getComboOptions("xml:/MoveRequest/@Priority")%>  >
					<yfc:loopOptions binding="xml:Priority:/CommonCodeList/@CommonCode" 
					name="CodeShortDescription" value="CodeValue" selected="xml:/MoveRequest/@Priority" isLocalized="Y"/>
					</select>
				</td>
			<%}%>
        <%}else{%>
            <td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequest/Priority/@Description" name="MoveRequest"></yfc:getXMLValue></td>
        <%}%>
	</tr>	
	<%String sStatus = resolveValue("xml:/MoveRequest/@Status");
	if(equals("CLOSED",sStatus))
	{	
		String sReasonCode = resolveValue("xml:/MoveRequest/@ReasonCode");
		String sReasonCodeDesc = "";
		YFCElement reasonCodeDoc = (YFCElement)request.getAttribute("ReasonCodeList");
		if (reasonCodeDoc != null)
		{
			for (Iterator i=reasonCodeDoc.getChildren();i.hasNext();)
			{
				YFCElement oElem = (YFCElement)i.next();
				if(YFCCommon.equals(oElem.getAttribute("CodeValue"),sReasonCode))
				{
					sReasonCodeDesc = oElem.getAttribute("CodeShortDescription");
					break;
				}
			}
		}
		if (YFCCommon.isVoid(sReasonCodeDesc))
			sReasonCodeDesc = sReasonCode;%>
		<tr>
			<td class="detaillabel" ><yfc:i18n>Cancellation_Reason_Code</yfc:i18n></td>
			<td class="protectedtext">
				<%=sReasonCodeDesc%>
			</td>
			<td class="detaillabel" ><yfc:i18n>Cancellation_Reason</yfc:i18n></td>
			<td class="protectedtext"><yfc:getXMLValue binding="xml:/MoveRequest/@ReasonText" name="MoveRequest"></yfc:getXMLValue></td>

		</tr>
	<%}%>
</table>