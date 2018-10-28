<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/moverequest.js"></script>
<script language="javascript" src="/yantra/console/scripts/wmsim.js"></script>
<yfc:callAPI apiID="AP1"/>
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
<yfc:callAPI apiID="AP2"/>

<%  	
	YFCDate highDate = new YFCDate().HIGH_DATE;
	YFCDate nowDate = new YFCDate();	
	
%>
 <table class="view" width="100%">	
	<tr>		
		<td class="detaillabel" ><yfc:i18n>Activity_Group</yfc:i18n></td>
		
		<td>
			<select class="combobox"  <%=getComboOptions("xml:/NodeInventory/MoveRequest/@FromActivityGroup")%> >
            <yfc:loopOptions binding="xml:ActivityGroupList:/BaseActivityGroupList/@BaseActivityGroup"                name="ActivityGroupName" value="ActivityGroupId" isLocalized="Y" selected="xml:/NodeInventory/MoveRequest/@FromActivityGroup"/>
	        </select>			 
		</td>			
		<td class="detaillabel" ><yfc:i18n>For_Activity_Code</yfc:i18n></td>
		<td> 
			<select class="combobox"  <%=getComboOptions("xml:/NodeInventory/MoveRequest/@ForActivityCode")%> >
                 <yfc:loopOptions binding="xml:ActivityCodeList:/Activities/@Activity" isLocalized="Y"
                name="Description" value="ActivityCode" selected="xml:/NodeInventory/MoveRequest/@ForActivityCode"/>
	        </select>
		</td>			 
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Move_Request_#</yfc:i18n></td>
		<td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NodeInventory/MoveRequest/@MoveRequestNo")%>/>
        </td>
		<td class="detaillabel" ><yfc:i18n>Priority</yfc:i18n></td>
		<td>
            <select class="combobox"  <%=getComboOptions("xml:/NodeInventory/MoveRequest/@Priority")%> >
            <yfc:loopOptions binding="xml:Priority:/CommonCodeList/@CommonCode" 
                name="CodeShortDescription" value="CodeValue" selected="3" isLocalized="Y"/>
			</select>
        </td>
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Target_Location</yfc:i18n></td>
		<td nowrap="true">
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NodeInventory/MoveRequest/@TargetLocationId") %> />
			<img class="lookupicon" onclick="callLookup(this,'location','xml:/Location/@Node=<%=resolveValue("xml:/NodeInventory/@Node")%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Location") %> />
		</td>
		<td class="detaillabel" ><yfc:i18n>Requested_By</yfc:i18n></td>
		<td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NodeInventory/MoveRequest/@RequestUserId", resolveValue("xml:CurrentUser:/User/@Loginid"))%> />	
			<img class="lookupicon" onclick="callLookup(this,'user')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_User") %> />
        </td>		
	</tr>
	<tr>
		<td class="detaillabel"><yfc:i18n>Start_No_Earlier_Than</yfc:i18n></td>
		<td nowrap="true" >
			<input class="dateinput" type="text" <%=getTextOptions("xml:/NodeInventory/MoveRequest/@StartNoEarlierThan_YFCDATE","xml:/NodeInventory/MoveRequest/@StartNoEarlierThan_YFCDATE",nowDate.getString(getLocale().getDateFormat()))%>/>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="text" <%=getTextOptions("xml:/NodeInventory/MoveRequest/@StartNoEarlierThan_YFCTIME", "xml:/NodeInventory/MoveRequest/@StartNoEarlierThan_YFCTIME")%>/>
				<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
		</td>		
		<td class="detaillabel"><yfc:i18n>Finish_No_Later_Than</yfc:i18n></td>
		<td nowrap="true" >
			<input class="dateinput" type="text" <%=getTextOptions("xml:/NodeInventory/MoveRequest/@FinishNoLaterThan_YFCDATE","xml:/NodeInventory/MoveRequest/@FinishNoLaterThan_YFCDATE")%>/>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />		
			<input class="dateinput" type="text" <%=getTextOptions("xml:/NodeInventory/MoveRequest/@FinishNoLaterThan_YFCTIME", "xml:/NodeInventory/MoveRequest/@FinishNoLaterThan_YFCTIME")%>/>
				<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
		</td>
	</tr>	 
	<tr>
		<td class="detaillabel" ><yfc:i18n>Release_Immediately</yfc:i18n></td>
		<td>
			<input type="checkbox"   <%=getCheckBoxOptions("xml:/NodeInventory/MoveRequest/@Release","Y", "Y")%>  yfcCheckedValue='Y' yfcUnCheckedValue='N'/>
		</td>
		<td/>
		<td/>
	</tr>
	 <tr>
        <td></td>
        <td colspan="2" align="center">
            <input type="button" class="button" value='<%=getI18N("__OK__")%>' onclick="setOKClickedAttribute();return false;"/>
           
        <td>
		<td></td>
    <tr>
</table>