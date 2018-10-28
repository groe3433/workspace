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
		<select <%=getComboOptions("xml:/MoveRequest/@RequestUserIdQryType" ) %> class="combobox" 
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType"              name="QueryTypeDesc" value="QueryType" selected="xml:/MoveRequest/@RequestUserIdQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/@RequestUserId") %> />
        <img class="lookupicon" onclick="callLookup(this,'user')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_User") %> />
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Start_No_Earlier_Than</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" >
        <input class="dateinput" type="text" <%=getTextOptions("xml:/MoveRequest/@FromStartNoEarlierThan_YFCDATE","xml:/MoveRequest/@FromStartNoEarlierThan_YFCDATE",one)%>/>
        <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
		<input class="dateinput" type="text" <%=getTextOptions("xml:/MoveRequest/@FromStartNoEarlierThan_YFCTIME", "xml:/MoveRequest/@FromStartNoEarlierThan_YFCTIME", oStartDate.getString(getLocale().getTimeFormat()))%>/>
            <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
	    <yfc:i18n>To</yfc:i18n>
	</td>
</tr>
<tr>
    <td nowrap="true" >
		<input class="dateinput" type="text" <%=getTextOptions("xml:/MoveRequest/@ToStartNoEarlierThan_YFCDATE","xml:/MoveRequest/@ToStartNoEarlierThan_YFCDATE",one)%> />
        <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
		<input class="dateinput" type="text" <%=getTextOptions("xml:/MoveRequest/@ToStartNoEarlierThan_YFCTIME", "xml:/MoveRequest/@ToStartNoEarlierThan_YFCTIME", oEndDate.getString(getLocale().getTimeFormat()))%>/>
            <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false"	<%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
	</td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Finish_No_Later_Than</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" >
        <input class="dateinput" type="text" <%=getTextOptions("xml:/MoveRequest/@FromFinishNoLaterThan_YFCDATE","xml:/MoveRequest/@FromFinishNoLaterThan_YFCDATE",one)%>/>
        <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />		
		<input class="dateinput" type="text" <%=getTextOptions("xml:/MoveRequest/@FromFinishNoLaterThan_YFCTIME", "xml:/MoveRequest/@FromFinishNoLaterThan_YFCTIME", oStartDate.getString(getLocale().getTimeFormat()))%>/>
            <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
	    <yfc:i18n>To</yfc:i18n>
	</td>
</tr>
<tr>
    <td nowrap="true" >
		<input class="dateinput" type="text" <%=getTextOptions("xml:/MoveRequest/@ToFinishNoLaterThan_YFCDATE","xml:/MoveRequest/@ToFinishNoLaterThan_YFCDATE",one)%> />
        <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />		
		<input class="dateinput" type="text" <%=getTextOptions("xml:/MoveRequest/@ToFinishNoLaterThan_YFCTIME", "xml:/MoveRequest/@ToFinishNoLaterThan_YFCTIME", oEndDate.getString(getLocale().getTimeFormat()))%>/>
            <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false"	<%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
	</td>
</tr>
<tr>
    <td class="searchlabel" >
        <yfc:i18n>Priority</yfc:i18n>
    </td>
</tr>
<tr>
	<td class="searchcriteriacell" nowrap="true">
        <select class="combobox"  <%=getComboOptions("xml:/MoveRequest/@Priority")%> >
            <yfc:loopOptions binding="xml:Priority:/CommonCodeList/@CommonCode" 
                name="CodeShortDescription" value="CodeValue" selected="3" isLocalized="Y"/>
        </select>    
    </td>
</tr>
<input name="xml:yfcSearchCriteria:/MoveRequest/@Mode"  type="hidden" value="bydate"/>
<input type="hidden" name="xml:/MoveRequest/@FinishNoLaterThanQryType" value="BETWEEN"/>
<input type="hidden" name="xml:/MoveRequest/@StartNoEarlierThanQryType" value="BETWEEN"/>
</table>