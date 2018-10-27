<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/moverequest.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/wmsim.js"></script>
<script language="Javascript" >
	yfcDoNotPromptForChanges(true);
</script>

<%
    String moveRequestKeyVal = resolveValue("xml:/MoveRequest/@MoveRequestKey");	
%>

<script language="javascript">
    <% if (!isVoid(moveRequestKeyVal)) { 
        YFCDocument moveRequestDoc = YFCDocument.createDocument("MoveRequest");
        moveRequestDoc.getDocumentElement().setAttribute("MoveRequestKey",moveRequestKeyVal);
    %>
     function moveToDetail(){ 
		showDetailFor('<%=moveRequestDoc.getDocumentElement().getString(false)%>');
	}
	window.attachEvent("onload", moveToDetail);	
  <%}%>
</script>
<%  	
	YFCDate highDate = new YFCDate().HIGH_DATE;
	YFCDate nowDate = new YFCDate();	
%>

 <table class="view" width="100%">
	<tr><td class="detaillabel"/><td/><td class="detaillabel"/><td/><td class="detaillabel"/><td/></tr>
	<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
	<jsp:param name="ScreenType" value="detail"/>
		<jsp:param name="ShowDocumentType" value="false"/>
		<jsp:param name="ShowNode" value="true"/> 
		<jsp:param name="EnterpriseCodeBinding" value="xml:/MoveRequest/@EnterpriseCode"/>
		<jsp:param name="NodeBinding" value="xml:/MoveRequest/@Node"/>
        <jsp:param name="RefreshOnNode" value="true"/>
		<jsp:param name="RefreshOnEnterpriseCode" value="true"/>
        <jsp:param name="EnterpriseListForNodeField" value="true"/>		
	</jsp:include>
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
<yfc:callAPI apiID="AP3"/>
<yfc:callAPI apiID="AP4"/>
<% 
	YFCElement activityCodeListElem = (YFCElement) request.getAttribute("ActivityCodeList");
	for(Iterator listItr = activityCodeListElem.getChildren();listItr.hasNext();) {
		YFCElement activityCodeElem = (YFCElement)listItr.next();
		//String sActivityGroup = activityCodeElem.getAttribute("ActivityGroupId");
		//if( sActivityGroup.equals("RETRIEVAL")|| sActivityGroup.equals("PUTAWAY")) {
			//activityCodeListElem.removeChild(activityCodeElem);
		//}
	}
	request.setAttribute("ActivityCodeList",activityCodeListElem);
%>
	<tr>		
		<td class="detaillabel" ><yfc:i18n>Activity_Group</yfc:i18n></td>
		<td>
			 <select class="combobox"  <%=getComboOptions("xml:/MoveRequest/@FromActivityGroup")%> >
            <yfc:loopOptions binding="xml:ActivityGroupList:/BaseActivityGroupList/@BaseActivityGroup" 
                name="ActivityGroupName" isLocalized="Y" value="ActivityGroupId" selected="xml:/MoveRequest/@FromActivityGroup"/>
			 </select> 
		</td>	
		<td class="detaillabel" ><yfc:i18n>For_Activity_Code</yfc:i18n></td>
		<td>
			 <select class="combobox"  <%=getComboOptions("xml:/MoveRequest/@ForActivityCode")%> >
            <yfc:loopOptions binding="xml:ActivityCodeList:/Activities/@Activity" 
                name="Description" value="ActivityCode" selected="xml:/MoveRequest/@ForActivityCode" isLocalized="Y"/>
			 </select> 
		</td>	
		<td class="detaillabel" ><yfc:i18n>Move_Request_#</yfc:i18n></td>
		<td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/@MoveRequestNo")%>/>
        </td>
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Source_Location</yfc:i18n></td>
		<td nowrap="true">
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/@SourceLocationId") %> />
			<img class="lookupicon" onclick="callLookup(this,'location','xml:/Location/@Node=<%=resolveValue("xml:/MoveRequest/@Node")%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Location") %> />
		</td>
		<td class="detaillabel" ><yfc:i18n>Target_Location</yfc:i18n></td>
		<td nowrap="true">
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/@TargetLocationId") %> />
			<img class="lookupicon" onclick="callLookup(this,'location','xml:/Location/@Node=<%=resolveValue("xml:/MoveRequest/@Node")%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Location") %> />
		</td>		
		<td class="detaillabel" ><yfc:i18n>Requested_By</yfc:i18n></td>
		<td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/@RequestUserId",resolveValue("xml:CurrentUser:/User/@Loginid"))%>/>
			<img class="lookupicon" onclick="callLookup(this,'user')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_User") %> />
        </td>		
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Receipt_#</yfc:i18n></td>
		<td nowrap="true">
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/@ReceiptNo") %> />	
			<img class="lookupicon" onclick="callLookup(this,'receiptlookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Receipt") %> />
		</td>
		<td class="detaillabel" ><yfc:i18n>Segment_Type</yfc:i18n></td>
		<td>
			<select class="combobox"  <%=getComboOptions("xml:/MoveRequest/@SegmentType")%> >
				<yfc:loopOptions binding="xml:SegmentType:/CommonCodeList/@CommonCode" 
					name="CodeShortDescription" value="CodeValue" selected="xml:/MoveRequest/@SegmentType" isLocalized="Y"/>
			</select>
		</td>		
		<td class="detaillabel" ><yfc:i18n>Segment_#</yfc:i18n></td>
		<td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/@Segment")%>/>
        </td>		
	</tr>
	<tr>
		<td class="detaillabel"><yfc:i18n>Start_No_Earlier_Than</yfc:i18n></td>
		<td nowrap="true" >
			<input class="dateinput" type="text" <%=getTextOptions("xml:/MoveRequest/@StartNoEarlierThan_YFCDATE","xml:/MoveRequest/@StartNoEarlierThan_YFCDATE",nowDate.getString(getLocale().getDateFormat()))%>/>
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
	</tr>
		<td class="detaillabel" ><yfc:i18n>Priority</yfc:i18n></td>
		<td>
            <select class="combobox"  <%=getComboOptions("xml:/MoveRequest/@Priority")%> >
            <yfc:loopOptions binding="xml:Priority:/CommonCodeList/@CommonCode" 
                name="CodeShortDescription" value="CodeValue" selected="3" isLocalized="Y"/>
			</select>
        </td>
		<td class="detaillabel" ><yfc:i18n>Release_Immediately</yfc:i18n></td>
		<td>
			<input type="checkbox"   <%=getCheckBoxOptions("xml:/MoveRequest/@Release","Y", "Y")%>  yfcCheckedValue='Y' yfcUnCheckedValue='N'/>							
		</td>
	<tr>
	</tr>
</table>