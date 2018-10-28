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
<yfc:callAPI apiID="AP4"/>
<yfc:callAPI apiID="AP5"/>
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
    <td class="searchlabel" ><yfc:i18n>Move_Request_#</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
		<select <%=getComboOptions("xml:/MoveRequest/@MoveRequestNoQryType" ) %> class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/MoveRequest/@MoveRequestNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/@MoveRequestNo")%> />
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Item_ID</yfc:i18n></td>
</tr>
<tr >
    <td class="searchcriteriacell" nowrap="true">
        <select class="combobox"  <%=getComboOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@ItemIdQryType") %> >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@ItemIdQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@ItemId") %> />
       	 <% String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>
		<img class="lookupicon" onclick="callItemLookup('xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@ItemId','ProductClass', 'UnitOfMeasure','item' ,'<%=extraParams%>')"  <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item") %> />
		<input type="hidden" value="" name="ProductClass"/>
		<input type="hidden" value="" name="UnitOfMeasure"/>
    </td>
</tr>
<tr>
	<td class="searchlabel" ><yfc:i18n>Priority</yfc:i18n></td>
</tr>
<tr>	
	<td>
		<select class="combobox"  <%=getComboOptions("xml:/MoveRequest/@Priority")%> >
			<yfc:loopOptions binding="xml:Priority:/CommonCodeList/@CommonCode"   isLocalized="Y"   name="CodeShortDescription" value="CodeValue" selected="xml:/MoveRequest/@Priority"/>
		</select>
	</td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Segment_Type</yfc:i18n></td>
</tr>
<tr >
     <td class="searchcriteriacell" nowrap="true">
        <select class="combobox"  <%=getComboOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@SegmentType")%> >
            <yfc:loopOptions binding="xml:SegmentType:/CommonCodeList/@CommonCode" isLocalized="Y"
                name="CodeShortDescription" value="CodeValue" selected="xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@SegmentType"/>
        </select>    
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Segment_#</yfc:i18n></td>
</tr>
<tr>
    <td class="searchcriteriacell" nowrap="true">    
		<select <%=getComboOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@SegmentQryType" ) %> class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@SegmentQryType"/>
        </select>   
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine/@Segment") %> />
    </td>
</tr>

<tr>
    <td class="searchlabel" >
        <yfc:i18n>Move_Request_Status</yfc:i18n>
    </td>
</tr>
<tr>
    <td class="searchcriteriacell">
	<input type="checkbox"   <%=getCheckBoxOptions("xml:/MoveRequest/@StatusCreated","xml:/MoveRequest/@StatusCreated", "CREATED")%>           yfcCheckedValue="CREATED" yfcUnCheckedValue=" " onclick="populateComplexQryValueCreated(this, 'xml:/MoveRequest/')" />	
	<yfc:i18n>Created</yfc:i18n>
	</td>
</tr>
<tr>
    <td class="searchcriteriacell">
	<input type="checkbox"   <%=getCheckBoxOptions("xml:/MoveRequest/@StatusReleased","xml:/MoveRequest/@StatusReleased", "RELEASED")%>
		yfcCheckedValue='RELEASED' yfcUnCheckedValue=' ' onclick="populateComplexQryValueReleased(this, 'xml:/MoveRequest/')" />	
	<yfc:i18n>Released</yfc:i18n>
	</td>
</tr>
<tr>
    <td class="searchcriteriacell">
	<input type="checkbox"   <%=getCheckBoxOptions("xml:/MoveRequest/@StatusClosed","xml:/MoveRequest/@StatusClosed", "CLOSED")%>            yfcCheckedValue='CLOSED' yfcUnCheckedValue=' ' onclick="populateComplexQryValueClosed(this, 'xml:/MoveRequest/')" />	
	<yfc:i18n>Closed</yfc:i18n>
	</td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Receipt_#</yfc:i18n></td>
</tr>
<tr>
    <td class="searchcriteriacell" nowrap="true">        
		<select <%=getComboOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine/Receipt/@ReceiptNoQryType" ) %> class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/MoveRequest/MoveRequestLines/MoveRequestLine/Receipt/@ReceiptNoQryType"/>
        </select>   
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/MoveRequest/MoveRequestLines/MoveRequestLine/Receipt/@ReceiptNo") %> />		
		<img class="lookupicon" onclick="callLookup(this,'receiptlookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Receipt") %> />		
		
    </td>
</tr>
<tr>
    <td class="searchcriteriacell">
	<input type="checkbox"   <%=getCheckBoxOptions("xml:/MoveRequest/@HasExceptions","xml:/MoveRequest/@HasExceptions", "Y")%>                     		
		yfcCheckedValue='Y' yfcUnCheckedValue=' '/>	
	<yfc:i18n>Has_Exceptions</yfc:i18n>
	</td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Start_No_Earlier_Than</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" >
        <input class="dateinput" type="text" <%=getTextOptions("xml:/MoveRequest/@FromStartNoEarlierThan_YFCDATE","xml:/MoveRequest/@FromStartNoEarlierThan_YFCDATE","")%>/>
        <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
		<input class="dateinput" type="text" <%=getTextOptions("xml:/MoveRequest/@FromStartNoEarlierThan_YFCTIME", "xml:/MoveRequest/@FromStartNoEarlierThan_YFCTIME", "")%>/>
            <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
	    <yfc:i18n>To</yfc:i18n>
	</td>
</tr>
<tr>
    <td nowrap="true" >
		<input class="dateinput" type="text" <%=getTextOptions("xml:/MoveRequest/@ToStartNoEarlierThan_YFCDATE","xml:/MoveRequest/@ToStartNoEarlierThan_YFCDATE","")%> />
        <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
		<input class="dateinput" type="text" <%=getTextOptions("xml:/MoveRequest/@ToStartNoEarlierThan_YFCTIME", "xml:/MoveRequest/@ToStartNoEarlierThan_YFCTIME", "")%>/>
            <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false"	<%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
	</td>
</tr>
<input type="hidden" name="xml:/MoveRequest/@StartNoEarlierThanQryType" value="BETWEEN"/>
</table>