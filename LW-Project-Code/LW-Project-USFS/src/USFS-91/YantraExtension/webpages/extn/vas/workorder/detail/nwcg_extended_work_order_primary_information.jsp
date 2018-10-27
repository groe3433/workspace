<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@include file="/console/jsp/currencyutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.util.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/workorder.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_workorderDetails.js"></script>

<script>
	function checkNumberOfActivities(numOfActivities){
		var numberOfActivities = document.all(numOfActivities).value;
		if(numberOfActivities <= 0){
			alert(YFCMSG097);//Cannot release as there are no activities
			return false;
		}
		return true;
	}
	function validateWorkOrder(numOfActivities)
	{
		var numberOfActivities = document.all(numOfActivities).value;
		if(numberOfActivities <= 0){
			alert(YFCMSG098);//Cannot confirm as there are no activities
			return false;
		}
		return true;
	}
	function showMoveRequestSearch(sViewID,sMoveRequestNodeKey,sMoveRequestEnterpriseCode,sMoveRequestNo)
	{   
		   var MoveRequestNodeKey = document.all(sMoveRequestNodeKey).value;
		   var MoveRequestEnterpriseCode = document.all(sMoveRequestEnterpriseCode).value;
		   var MoveRequestNo = document.all(sMoveRequestNo).value;
		   var entity="wmsmoverequest";
		   var sAddnParams = "&xml:yfcSearchCriteria:/MoveRequest/@Node="+MoveRequestNodeKey;
		   sAddnParams = sAddnParams + "&xml:yfcSearchCriteria:/MoveRequest/MoveRequestLines/MoveRequestLine/@EnterpriseCode="+MoveRequestEnterpriseCode;
		   sAddnParams = sAddnParams + "&xml:/MoveRequest/WorkOrder/@WorkOrderNo="+MoveRequestNo;
		   yfcShowListPopupWithParams(sViewID,"",'900', '550','',entity, sAddnParams);
	}
	function showTaskSearch(sViewID,sTaskNodeKey,sTaskEnterpriseCode,sTaskNo)
	{   
		   var TaskNodeKey = document.all(sTaskNodeKey).value;
		   var TaskEnterpriseCode = document.all(sTaskEnterpriseCode).value;
		   var TaskNo = document.all(sTaskNo).value;
		   var entity="task";
		   var sAddnParams = "&xml:/Task/@Node="+TaskNodeKey;		   
		   sAddnParams = sAddnParams + "&xml:/Task/TaskReferences/@WorkOrderNo="+TaskNo;
		   yfcShowListPopupWithParams(sViewID,"",'900', '550','',entity, sAddnParams);
	}	
	function refreshpage() {
		yfcChangeDetailView(getCurrentViewId());
	}
	function enterActionCancellationReason(cancelReasonViewID, cancelReasonCodeBinding, cancelReasonTextBinding) {

		var myObject = new Object();
		myObject.currentWindow = window;
		myObject.reasonCodeInput = document.all(cancelReasonCodeBinding);
		myObject.reasonTextInput = document.all(cancelReasonTextBinding);       
		
		yfcShowDetailPopup(cancelReasonViewID, "", "550", "255", myObject, "workorder", "<WorkOrder />");

		var returnValue = myObject["OKClicked"];
		if ( "YES" == returnValue ) {
			return true;
		} else {
			return false;
		}
	}
</script>
<%
	YFCElement organizationInput = null;

	organizationInput = YFCDocument.parse("<Organization OrganizationCode=\"" + resolveValue("xml:/WorkOrder/@EnterpriseCode") + "\" />").getDocumentElement();

	YFCElement organizationTemplate = YFCDocument.parse("<OrganizationList> <Organization InventoryOrganizationCode=\"\"/> </OrganizationList>").getDocumentElement(); 
%>
	<yfc:callAPI apiName="getOrganizationList" inputElement="<%=organizationInput%>" templateElement="<%=organizationTemplate%>" outputNamespace="OrganizationList"/>
	<Input type="hidden" name="xml:/WorkOrder/@EnterpriseInvOrg" value="<%=resolveValue("xml:OrganizationList:/OrganizationList/Organization/@InventoryOrganizationCode")%>"/>
<%	
	String modifyView = resolveValue("xml:/WorkOrder/@WorkOrderKey");
    modifyView = modifyView == null ? "" : modifyView;

	String createView = resolveValue("xml:/WorkOrder/@WorkOrderMode");
    createView = createView == null ? "" : createView;

	String startDate = request.getParameter("xml:/WorkOrder/@StartNoEarlierThan_YFCDATE");
	String startTime = request.getParameter("xml:/WorkOrder/@StartNoEarlierThan_YFCTIME");
	String finishDate = request.getParameter("xml:/WorkOrder/@FinishNoLaterThan_YFCDATE");
	String finishTime = request.getParameter("xml:/WorkOrder/@FinishNoLaterThan_YFCTIME");
%>
	<Input type="hidden" name="xml:/WorkOrder/@ReasonCode" value="<%=resolveValue("xml:/WorkOrder/@ReasonCode")%>"/>
	<Input type="hidden" name="xml:/WorkOrder/@ReasonText" value="<%=resolveValue("xml:/WorkOrder/@ReasonText")%>"/>
	<Input type="hidden" name="xml:/WorkOrder/@NumberOfActivities" value="<%=resolveValue("xml:/WorkOrder/@NumberOfActivities")%>"/>

	<yfc:makeXMLInput name="WorkOrderKey">
		<yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderKey" value="xml:/WorkOrder/@WorkOrderKey" />
	</yfc:makeXMLInput>
	
	<yfc:makeXMLInput name="releaseWorkOrderKey">
		<yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderKey" value="xml:/WorkOrder/@WorkOrderKey" />
	</yfc:makeXMLInput>

<yfc:makeXMLInput name="shipmentKey">
	<yfc:makeXMLKey binding="xml:/Shipment/@ShipmentKey" value="xml:/WorkOrder/@ShipmentKey" />
</yfc:makeXMLInput>
<input type="hidden" name="shipmentKey" value='<%=getParameter("shipmentKey")%>'/>

<input type="hidden" name="releaseWorkOrderKey" value='<%=getParameter("releaseWorkOrderKey")%>'/>

<table class="view" width="100%">
	<% if(modifyView != "" || createView != "") {%> 
	<tr>
			<td class="detaillabel" ><yfc:i18n>Node</yfc:i18n></td>
			<td class="protectedtext"><%=resolveValue("xml:/WorkOrder/@NodeKey")%></td>
			<Input type="hidden" name="xml:/WorkOrder/@NodeKey" value="<%=resolveValue("xml:/WorkOrder/@NodeKey")%>"/>
			<td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
			<td class="protectedtext"><%=resolveValue("xml:/WorkOrder/@EnterpriseCode")%></td>
			<Input type="hidden" name="xml:/WorkOrder/@EnterpriseCode" value="<%=resolveValue("xml:/WorkOrder/@EnterpriseCode")%>"/>
			<Input type="hidden" name="xml:/WorkOrder/@EnterpriseCodeForComponent" value="<%=resolveValue("xml:/WorkOrder/@EnterpriseCode")%>"/>
			<td/><td/>
	</tr>
		<% } 
	else {%>
	<tr><td/><td/><td/><td/><td/><td/></tr>
		<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
				<jsp:param name="ShowDocumentType" value="false"/>
				<jsp:param name="ScreenType" value="detail"/>
				<jsp:param name="ShowNode" value="true"/>
				<jsp:param name="EnterpriseCodeBinding" value="xml:/WorkOrder/@EnterpriseCode"/>
				<jsp:param name="NodeBinding" value="xml:/WorkOrder/@NodeKey"/>
				<jsp:param name="RefreshOnNode" value="true"/>
				<jsp:param name="EnterpriseListForNodeField" value="false"/>
				 <jsp:param name="RefreshOnEnterpriseCode" value="true"/>
		</jsp:include>
	<% } %>
	<tr>
        <td class="detaillabel" ><yfc:i18n>Work_Order_#</yfc:i18n></td>
		<% if(modifyView != "" || createView != "") {%> 
			<td class="protectedtext"><%=resolveValue("xml:/WorkOrder/@WorkOrderNo")%></td>
			<input type="hidden" name="xml:/WorkOrder/@WorkOrderNo" value="<%=resolveValue("xml:/WorkOrder/@WorkOrderNo")%>"/>
		<%}
		else
		{%>
			<td nowrap="true" >
				<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/WorkOrder/@WorkOrderNo")%>/>
			</td>
		<%}%>
		<td class="detaillabel" ><yfc:i18n>Service_Item_Group_Code</yfc:i18n></td>
		<% if(modifyView != "") {
			if (!YFCCommon.isVoid(resolveValue("xml:/WorkOrder/@ServiceItemGroupCode"))) { 
				YFCDocument oDoc = YFCDocument.createDocument("CommonCode");
				YFCElement oTemplate = YFCDocument.parse("<CommonCodeList> <CommonCode CodeType=\"\" CodeValue=\"\" CodeShortDescription=\"\"/> </CommonCodeList>").getDocumentElement();
				YFCElement oCommonCode = oDoc.getDocumentElement();
				oCommonCode.setAttribute("CodeType","SERVICE_ITEM_GROUP");
				oCommonCode.setAttribute("CodeValue",resolveValue("xml:/WorkOrder/@ServiceItemGroupCode")); %>
				<yfc:callAPI apiName="getCommonCodeList" inputElement="<%=oCommonCode%>" templateElement="<%=oTemplate%>" outputNamespace="ServiceItemGroupDesc"/>
				<td class="protectedtext">
					<yfc:getXMLValueI18NDB binding="xml:ServiceItemGroupDesc:/CommonCodeList/CommonCode/@CodeShortDescription"/>	
				</td>
<%			}%> 
			<Input type="hidden" name="xml:/WorkOrder/@ServiceItemGroupCode" value="<%=resolveValue("xml:/WorkOrder/@ServiceItemGroupCode")%>"/>
		<%}else if(createView !=""){
			String selectedServiceItemGroup = "";%>			
			<yfc:loopXML binding="xml:ServiceItemGroupList:/CommonCodeList/@CommonCode" id="ServiceItemGroupList">
				<%if(equals(resolveValue("xml:ServiceItemGroupList:/CommonCode/@CodeValue"), resolveValue("xml:/WorkOrder/@ServiceItemGroupCode"))){
					selectedServiceItemGroup=resolveValue("xml:ServiceItemGroupList:/CommonCode/@CodeShortDescription");
				}%>
			</yfc:loopXML>
			<td class="protectedtext"><%=selectedServiceItemGroup%></td>
			<Input type="hidden" name="xml:/WorkOrder/@ServiceItemGroupCode" value="<%=resolveValue("xml:/WorkOrder/@ServiceItemGroupCode")%>"/>
		<%}else{%>
			<td class="searchcriteriacell">
            <select <%=getComboOptions("xml:/WorkOrder/@ServiceItemGroupCode")%> class="combobox" onchange="refreshpage()">
                <yfc:loopOptions binding="xml:ServiceItemGroupList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
                value="CodeValue" selected="xml:/WorkOrder/@ServiceItemGroupCode"  isLocalized="Y"/>
            </select>
			</td>
		<%}%>

		<% if(modifyView != "") {%> 
			<td class="detaillabel" ><yfc:i18n>Status</yfc:i18n></td>
			<td class="protectedtext">
				<yfc:getXMLValueI18NDB binding="xml:/WorkOrder/Status/@Description"/>
				<% if (isTrue("xml:/WorkOrder/@HoldFlag")) { %>
    	            <% if (isVoid(modifyView) )	{	%>
    					<img onmouseover="this.style.cursor='default'" class="columnicon" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_work_order_is_held")%>>
    				<%	}	else	{	%>
    					<a <%=getDetailHrefOptions("L01", getParameter("WorkOrderKey"), "")%>><img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_work_order_is_held\nclick_to_add/remove_hold")%>></a>
    				<%	}	%>
    
                <% } %>
			</td>
			<Input type="hidden" name="xml:/WorkOrder/@Status" value="<%=resolveValue("xml:/WorkOrder/@Status")%>"/>
			<Input type="hidden" name="xml:/WorkOrder/@HoldFlag" value="<%=resolveValue("xml:/WorkOrder/@HoldFlag")%>"/>
		<%} else 
		if(createView != ""){%>
			<td class="detaillabel" ><yfc:i18n>Purpose</yfc:i18n></td>
				<%YFCDocument oDoc = YFCDocument.createDocument("CommonCode");
				YFCElement oTemplate = YFCDocument.parse("<CommonCodeList> <CommonCode CodeType=\"\" CodeValue=\"\" CodeShortDescription=\"\"/> </CommonCodeList>").getDocumentElement();
				YFCElement oCommonCode = oDoc.getDocumentElement();
				oCommonCode.setAttribute("CodeType","WORK_ORDER_PURPOSE");
				oCommonCode.setAttribute("CodeValue",resolveValue("xml:/WorkOrder/@Purpose")); %>
				<yfc:callAPI apiName="getCommonCodeList" inputElement="<%=oCommonCode%>" templateElement="<%=oTemplate%>" outputNamespace="PurposeDesc"/>
				<td class="protectedtext">
					<yfc:getXMLValueI18NDB binding="xml:PurposeDesc:/CommonCodeList/CommonCode/@CodeShortDescription"/>	
				</td>
				<Input type="hidden" name="xml:/WorkOrder/@Purpose" value="<%=resolveValue("xml:/WorkOrder/@Purpose")%>"/>
		<%}
		else
		{%>
			<td class="detaillabel" ><yfc:i18n>Purpose</yfc:i18n></td>
			<td class="searchcriteriacell">
				<select <%=getComboOptions("xml:/WorkOrder/@Purpose")%> class="combobox">
					<yfc:loopOptions binding="xml:WorkOrderPurposeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
					value="CodeValue" selected="xml:/WorkOrder/@Purpose" isLocalized="Y"/>
				</select>
			</td>
		<%}%>
	</tr>
	
	<tr>
        <td class="detaillabel" ><yfc:i18n>Priority</yfc:i18n></td>
		<%if(equals("Y",resolveValue("xml:/WorkOrder/@WorkOrderNotCompleted")) || isVoid(resolveValue("xml:/WorkOrder/@WorkOrderKey"))){%>	
		<td class="searchcriteriacell">
			<select <%=getComboOptions("xml:/WorkOrder/@Priority")%> class="combobox">
				<yfc:loopOptions binding="xml:WorkOrderPriorityList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
				value="CodeValue" selected="xml:/WorkOrder/@Priority" isLocalized="Y"/>
			</select>
			<Input type="hidden" name="xml:/WorkOrder/@Priority" value="<%=resolveValue("xml:/WorkOrder/@Priority")%>"/>
		</td>
		<%}else{
			String selectedPriority = "";
		%>			
				<yfc:loopXML binding="xml:WorkOrderPriorityList:/CommonCodeList/@CommonCode" id="PriorityList">
					<%if(equals(resolveValue("xml:PriorityList:/CommonCode/@CodeValue"), resolveValue("xml:/WorkOrder/@Priority"))){
						selectedPriority=resolveValue("xml:PriorityList:/CommonCode/@CodeShortDescription");
					}%>
				</yfc:loopXML>
			<td class="protectedtext">
				<%=selectedPriority%>
			</td>
		<%}%>
		<Input type="hidden" name="xml:/WorkOrder/@Priority" value="<%=resolveValue("xml:/WorkOrder/@Priority")%>"/>

		<td  class="detaillabel" ><yfc:i18n>Start_No_Earlier_Than</yfc:i18n></td>
		<td nowrap="true" >
			<%if(isVoid(resolveValue("xml:/WorkOrder/@WorkOrderKey"))){%>	
				<input class="dateinput" type="text" <%=getTextOptions("xml:/WorkOrder/@StartNoEarlierThan_YFCDATE", startDate)%>/>
				<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
				<input class="dateinput" type="text" <%=getTextOptions("xml:/WorkOrder/@StartNoEarlierThan_YFCTIME", startTime)%>/>
				<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %>/>
			<%}else if(equals("Y",resolveValue("xml:/WorkOrder/@WorkOrderNotCompleted"))){%>
				<input class="dateinput" type="text" <%=getTextOptions("xml:/WorkOrder/@StartNoEarlierThan_YFCDATE", "xml:/WorkOrder/@StartNoEarlierThan_YFCDATE")%>/>
				<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
				<input class="dateinput" type="text" <%=getTextOptions("xml:/WorkOrder/@StartNoEarlierThan_YFCTIME", "xml:/WorkOrder/@StartNoEarlierThan_YFCTIME")%>/>
				<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %>/>
			<%}else{%>			
				<input class="dateinput" type="text" <%=getTextOptions("xml:/WorkOrder/@StartNoEarlierThan_YFCDATE","xml:/WorkOrder/@StartNoEarlierThan_YFCDATE")%> DISABLED/>
				<input class="dateinput" type="text" <%=getTextOptions("xml:/WorkOrder/@StartNoEarlierThan_YFCTIME","xml:/WorkOrder/@StartNoEarlierThan_YFCTIME")%> DISABLED/>
			<%}%>
		</td>
		<td  class="detaillabel" ><yfc:i18n>Finish_No_Later_Than</yfc:i18n></td>
		<td nowrap="true" >
			<%if(isVoid(resolveValue("xml:/WorkOrder/@WorkOrderKey"))){%>	
				<input class="dateinput" type="text" <%=getTextOptions("xml:/WorkOrder/@FinishNoLaterThan_YFCDATE", finishDate)%>/>
				<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
				<input class="dateinput" type="text" <%=getTextOptions("xml:/WorkOrder/@FinishNoLaterThan_YFCTIME", finishTime)%>/>
				<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %>/>
			<%}else if(equals("Y",resolveValue("xml:/WorkOrder/@WorkOrderNotCompleted"))){%>	
				<input class="dateinput" type="text" <%=getTextOptions("xml:/WorkOrder/@FinishNoLaterThan_YFCDATE", "xml:/WorkOrder/@FinishNoLaterThan_YFCDATE")%>/>
				<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
				<input class="dateinput" type="text" <%=getTextOptions("xml:/WorkOrder/@FinishNoLaterThan_YFCTIME", "xml:/WorkOrder/@FinishNoLaterThan_YFCTIME")%>/>
				<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %>/>
			<%}else{%>			
				<input class="dateinput" type="text" <%=getTextOptions("xml:/WorkOrder/@FinishNoLaterThan_YFCDATE","xml:/WorkOrder/@FinishNoLaterThan_YFCDATE")%> DISABLED/>
				<input class="dateinput" type="text" <%=getTextOptions("xml:/WorkOrder/@FinishNoLaterThan_YFCTIME","xml:/WorkOrder/@FinishNoLaterThan_YFCTIME")%> DISABLED/>
			<%}%>
		</td>
	</tr>
	<tr>
		
		<td class="detaillabel" ><yfc:i18n>Service_Item_ID</yfc:i18n></td>
		<% if(modifyView != "" || createView != "") {%> 
			<td class="protectedtext">
				<!--<a <%=getDetailHrefOptions("L01", getParameter("orderLineKey"),"ShowReleaseNo=Y")%>>--><%=resolveValue("xml:/WorkOrder/@ServiceItemID")%><!--</a>--></td>
			<Input type="hidden" name="xml:/WorkOrder/@ServiceItemID" value="<%=resolveValue("xml:/WorkOrder/@ServiceItemID")%>"/>

		<%}
		else
		{%>
			<td class="searchcriteriacell" nowrap="true" >
				<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/WorkOrder/@ServiceItemID")%>/>
	            <% String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", resolveValue("xml:/WorkOrder/@EnterpriseCode")); %>
				<img class="lookupicon" name="search" onclick="callServiceItemLookup('xml:/WorkOrder/@ServiceItemID','xml:/WorkOrder/@ProductClass','xml:/WorkOrder/@Uom', 'item','<%=resolveValue("xml:/WorkOrder/@ServiceItemGroupCode")%>','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item")%> />
			</td>
		<%}
		if(modifyView != ""){%>
			<td class="detaillabel" ><yfc:i18n>Purpose</yfc:i18n></td>
				<%YFCDocument oDoc = YFCDocument.createDocument("CommonCode");
				YFCElement oTemplate = YFCDocument.parse("<CommonCodeList> <CommonCode CodeType=\"\" CodeValue=\"\" CodeShortDescription=\"\"/> </CommonCodeList>").getDocumentElement();
				YFCElement oCommonCode = oDoc.getDocumentElement();
				oCommonCode.setAttribute("CodeType","WORK_ORDER_PURPOSE");
				oCommonCode.setAttribute("CodeValue",resolveValue("xml:/WorkOrder/@Purpose")); %>
				<yfc:callAPI apiName="getCommonCodeList" inputElement="<%=oCommonCode%>" templateElement="<%=oTemplate%>" outputNamespace="PurposeDesc"/>
				<td class="protectedtext">
					<yfc:getXMLValueI18NDB binding="xml:PurposeDesc:/CommonCodeList/CommonCode/@CodeShortDescription"/>		
				</td>
			<Input type="hidden" name="xml:/WorkOrder/@Purpose" value="<%=resolveValue("xml:/WorkOrder/@Purpose")%>"/>
		<%}%>
		<!-- Top of CR 562 - jsk -->
		<%
		String strVasLocationId = resolveValue("xml:/WorkOrder/Extn/@ExtnLocationId");
		if(strVasLocationId != null && (!strVasLocationId.equals(""))) {
		%>
			<td class="detaillabel" ><yfc:i18n>Location</yfc:i18n></td>
			<td class="searchcriteriacell">
				<input class="protectedinput" type="text" <%=getTextOptions("xml:/WorkOrder/Extn/@ExtnLocationId")%>/>
			</td>
		<%}%>
		<!-- Bottom of CR 562 - jsk -->
	</tr>
	<tr>
		<%if(createView != ""){%>
	        <td class="detaillabel" ></td>
			<td class="searchcriteriacell">
				<input type="checkbox"  <%=getCheckBoxOptions("xml:/WorkOrder/@IgnoreRunQuantity", "xml:/WorkOrder/@IgnoreRunQuantity", "Y")%> yfcCheckedValue='Y' yfcUnCheckedValue=' ' ><yfc:i18n>Ignore_Run_Quantity</yfc:i18n></input>
			</td>
	        <td class="detaillabel" ></td>
			<td class="searchcriteriacell">
				<input type="checkbox" <%=getCheckBoxOptions("xml:/WorkOrder/@AutoRelease", "xml:/WorkOrder/@AutoRelease", "Y")%> yfcCheckedValue='Y' yfcUnCheckedValue=' ' ><yfc:i18n>Release_Immediately</yfc:i18n></input>
			</td>
		<%}else if(modifyView == ""){%>
	        <td class="detaillabel" ></td>
			<td class="searchcriteriacell">
				<input type="checkbox" <%=getCheckBoxOptions("xml:/WorkOrder/@IgnoreRunQuantity", "xml:/WorkOrder/@IgnoreRunQuantity", "Y")%> yfcCheckedValue='Y' yfcUnCheckedValue=' ' ><yfc:i18n>Ignore_Run_Quantity</yfc:i18n></input>
			</td>
	        <td class="detaillabel" ></td>
			<td class="searchcriteriacell">
				<input type="checkbox" <%=getCheckBoxOptions("xml:/WorkOrder/@AutoRelease", "xml:/WorkOrder/@AutoRelease", "Y")%> yfcCheckedValue='Y' yfcUnCheckedValue=' ' ><yfc:i18n>Release_Immediately</yfc:i18n></input>
			</td>
		<%}%>
<!--
        <td class="detaillabel" ></td>
		<td></td>
-->

        <%
		String strPrintReport = resolveValue("xml:/WorkOrder/Extn/@ExtnPrintReport");
		if(strPrintReport != null && (!strPrintReport.equals(""))) {
		%>
			<td class="detaillabel" ><yfc:i18n>Print_Report</yfc:i18n></td>
			<td class="searchcriteriacell">
				<input class="protectedinput" type="text" <%=getTextOptions("xml:/WorkOrder/Extn/@ExtnPrintReport")%>/>
			</td>
		<%}%>

	</tr>
	<%if(modifyView != "") {
		String sReasonCode = resolveValue("xml:/WorkOrder/@ReasonCode");
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
			sReasonCodeDesc = sReasonCode;

		String sReasonText = resolveValue("xml:/WorkOrder/@ReasonText");
		if(!isVoid(sReasonCode) || !isVoid(sReasonText)){%> 
			<tr>
				<td class="detaillabel" ><yfc:i18n>Reason_Code</yfc:i18n></td>
				<td class="protectedtext" nowrap="true">
					<%=sReasonCodeDesc%>
				</td>	
				<td class="detaillabel" ><yfc:i18n>Reason_Text</yfc:i18n></td>
				<td class="protectedtext" nowrap="true">
					<%=sReasonText%>
				</td>	
				<td/>
				<td/>
			</tr>
		<%}
	}%>
</table>
