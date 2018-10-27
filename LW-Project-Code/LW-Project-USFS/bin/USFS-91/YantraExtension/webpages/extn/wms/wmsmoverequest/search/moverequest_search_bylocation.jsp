<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/moverequest.js"></script>
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
		//String sActivityGroup = activityElem.getAttribute("ActivityGroupId");
		//if( sActivityGroup.equals("RETRIEVAL")|| sActivityGroup.equals("PUTAWAY")) {
		//	activityListElem.removeChild(activityElem);
		//}
	}
	request.setAttribute("ActivityGroupList",activityListElem);
%>
<tr>
  <td>
	<input type="hidden" name="xml:/MoveRequest/ComplexQuery/Or/Exp_0/@Name" value="Status"/>
	<input type="hidden" name="xml:/MoveRequest/ComplexQuery/Or/Exp_0/@Value" value="<%=resolveValue("xml:/MoveRequest/@StatusCreated")%>"/>  
	<input type="hidden" name="xml:/MoveRequest/ComplexQuery/Or/Exp_1/@Name" value="Status"/>
	<input type="hidden" name="xml:/MoveRequest/ComplexQuery/Or/Exp_1/@Value" value="<%=resolveValue("xml:/MoveRequest/@StatusReleased")%>"/>
	<input type="hidden" name="xml:/MoveRequest/ComplexQuery/Or/Exp_2/@Name" value="Status"/>
	<input type="hidden" name="xml:/MoveRequest/ComplexQuery/Or/Exp_2/@Value" value="<%=resolveValue("xml:/MoveRequest/@StatusClosed")%>"/>
	<input name="xml:yfcSearchCriteria:/MoveRequest/@Mode" type="hidden" value="byitem"/>
	<input type="hidden" name="xml:/MoveRequest/@StatusQryType" value=""/>
  </td>
</tr>

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
    <td class="searchlabel" ><yfc:i18n>Source_Location</yfc:i18n></td>
</tr>
<tr >
    <td class="searchcriteriacell" nowrap="true">
        <select <%=getComboOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@SourceLocationIdQryType" ) %> class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@SourceLocationIdQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@SourceLocationId") %> />
        <img class="lookupicon" onclick="callLookup(this,'location','xml:/Location/@Node=' +  document.all['xml:/MoveRequest/@Node'].value )" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Location") %> />
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Target_Location</yfc:i18n></td>
</tr>
<tr >
    <td class="searchcriteriacell" nowrap="true">
        <select <%=getComboOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@TargetLocationIdQryType" ) %> class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@TargetLocationIdQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@TargetLocationId") %> />
        <img class="lookupicon" onclick="callLookup(this,'location','xml:/Location/@Node=' +  document.all['xml:/MoveRequest/@Node'].value )" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Location") %> />
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Pallet_ID</yfc:i18n></td>    
</tr>
<tr>
    <td>
		<select name="xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@PalletIdQryType" class="combobox">
			<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
			value="QueryType" selected="xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@PalletIdQryType"/>
		</select>
      <input type="text" class="unprotectedinput" <%= getTextOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@PalletId") %> />
    </td>
</tr>
<tr>
    <td  class="searchlabel" ><yfc:i18n>Case_ID</yfc:i18n></td>
</tr>
<tr>
    <td>
		<select name="xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@CaseIdQryType" class="combobox">
			<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
			value="QueryType" selected="xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@CaseIdQryType"/>
		</select>
      <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@CaseId") %> />
    </td>
    <td>
		<input name="xml:yfcSearchCriteria:/GetLPNDetails/@Mode"  type="hidden" value="bylpn"/>
    </td>
</tr>
<tr>
    <td class="searchlabel" >
        <yfc:i18n>Move_Request_Status</yfc:i18n>
    </td>
</tr>
<tr>
    <td class="searchcriteriacell">
	<input type="checkbox"   <%=getCheckBoxOptions("xml:/MoveRequest/@StatusCreated","xml:/MoveRequest/@StatusCreated", "CREATED")%>                     		
		yfcCheckedValue='CREATED' yfcUnCheckedValue=' ' onclick="populateComplexQryValueCreated(this, 'xml:/MoveRequest/')"/>	
	<yfc:i18n>Created</yfc:i18n>
	</td>
</tr>
<tr>
    <td class="searchcriteriacell">
	<input type="checkbox"   <%=getCheckBoxOptions("xml:/MoveRequest/@StatusReleased","xml:/MoveRequest/@StatusReleased", "RELEASED")%>                     		
		yfcCheckedValue='RELEASED' yfcUnCheckedValue=' ' onclick="populateComplexQryValueReleased(this, 'xml:/MoveRequest/')"  />	
	<yfc:i18n>Released</yfc:i18n>
	</td>
</tr>
<tr>
    <td class="searchcriteriacell">
	<input type="checkbox"   <%=getCheckBoxOptions("xml:/MoveRequest/@StatusClosed","xml:/MoveRequest/@StatusClosed", "CLOSED")%>                     		
		yfcCheckedValue='CLOSED' yfcUnCheckedValue=' ' onclick="populateComplexQryValueClosed(this, 'xml:/MoveRequest/')" />	
	<yfc:i18n>Closed</yfc:i18n>
	</td>
</tr>

</table>