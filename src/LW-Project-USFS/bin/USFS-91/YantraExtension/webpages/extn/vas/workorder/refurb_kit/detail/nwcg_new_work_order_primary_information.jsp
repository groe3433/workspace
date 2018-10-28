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

	function processWOForDisposition(viewID, itemId, serviceItemId, workOrderStatus, qtyRequested, qtyAllocated, isTrackableItem) {
		// 1400 -- WORK ORDER COMPLETED STATUS
		var item = document.all(itemId).value;

		var svcItemId = document.all(serviceItemId).value;

		var wOStatus = document.all(workOrderStatus).value;

		var qtyReq = document.all(qtyRequested).value;

		var qtyAllc = document.all(qtyAllocated).value;

		var isTrack = document.all(isTrackableItem).value;
		var workOrderKey = document.all("xml:/WorkOrder/@WorkOrderKey").value;
		var actLocId = document.all("xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity/WorkOrderActivityDtls/WorkOrderActivityDtl/@ActivityLocationId").value;

		if (("REFURBISHMENT" == svcItemId) && ("1400" == wOStatus || qtyReq == 0) && (qtyReq - qtyAllc != 0)) {
		//if (("DEKITTING" == svcItemId)) {
			var diffQty = qtyAllc - qtyReq;
			if (diffQty > 0){
				var sAddnParams = "&xml:/WorkOrderDisposition/@ItemID="+item;
				sAddnParams = sAddnParams + "&xml:/WorkOrderDisposition/@IsTracked=" + isTrack;
				sAddnParams = sAddnParams + "&xml:/WorkOrderDisposition/@Quantity=" + diffQty;
				sAddnParams = sAddnParams + "&xml:/WorkOrderDisposition/@EnterpriseCode=" + document.all("xml:/WorkOrder/@EnterpriseCode").value;
				sAddnParams = sAddnParams + "&xml:/WorkOrderDisposition/@NodeKey=" + document.all("xml:/WorkOrder/@NodeKey").value;
				sAddnParams = sAddnParams + "&xml:/WorkOrderDisposition/@ProductClass=" + document.all("xml:/WorkOrder/@ProductClass").value;
				sAddnParams = sAddnParams + "&xml:/WorkOrderDisposition/@Uom=" + document.all("xml:/WorkOrder/@Uom").value;
				sAddnParams = sAddnParams + "&xml:/WorkOrderDisposition/@WorkOrderKey=" + workOrderKey;
				sAddnParams = sAddnParams + "&xml:/WorkOrderDisposition/@LocationId=" + actLocId;
				yfcShowDetailPopupWithParams(viewID, "", "900", "550", sAddnParams);
			}
			//return true;
		}
		else {
			if ("REFURBISHMENT" != svcItemId){
				alert("Service Item ID is not REFURBISHMENT");
			}
			else if ("1400" != wOStatus){
				alert("Work Order has to be in Completed Status");
			}
			else if (qtyReq == qtyAllc){
				alert("There are no items to change the disposition");
			}
			//alert("Work Order is not completed or quantity is not equal to 0");
			return false;
		}
	}


	function createRefurbIssue(viewID, workOrderKey, workOrderStatus){
		var woStatus = document.all(workOrderStatus).value;
		var woKey = document.all(workOrderKey).value;

		var totRecords = document.all("xml:/OrderList/@TotalNumberOfRecords").value;
		var orderNo = document.all("xml:/OrderList/Order/@OrderNo").value;

		// NOTE: This function is used only for serial trackable items. There is a binding for the action
		if (("0" == totRecords) && ("1400" == woStatus)){
			var sAddnParams = "&xml:/WorkOrder/@WorkOrderKey=" + woKey;
			yfcShowDetailPopupWithParams(viewID, "", "900", "500", sAddnParams);
		}
		else {
			if ("1400" != woStatus){
				alert("Order has to be in Completed Status");
			}
			else if ("0" != totRecords){
				alert("Issue No " + orderNo + " is already created for this work order");
			}
		}
	}

	function adjustSerItemLocInventory(viewID) {
		// NOTE: This function is used only for serial trackable items. There is a binding for the action
		var enterpriseCode = document.all("xml:/WorkOrder/@EnterpriseCode").value;

		var nodeKey = document.all("xml:/WorkOrder/@NodeKey").value;

		var prodClass = document.all("xml:/WorkOrder/@ProductClass").value;

		var svcItemId = document.all("xml:/WorkOrder/@ServiceItemID").value;

		var svcGrpCode = document.all("xml:/WorkOrder/@ServiceItemGroupCode").value;

		var qtyReq = document.all("xml:/WorkOrder/@QuantityRequested").value;

		var qtyCmpl = document.all("xml:/WorkOrder/@QuantityCompleted").value;

		var itemId = document.all("xml:/WorkOrder/@ItemID").value;

		var uom = document.all("xml:/WorkOrder/@Uom").value;

		var desc = document.all("xml:/WorkOrder/Item/PrimaryInformation/@Description").value;

		var kitCode = document.all("xml:WOItem:/Item/PrimaryInformation/@KitCode").value;

		var actLocId = document.all("xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity/WorkOrderActivityDtls/WorkOrderActivityDtl/@ActivityLocationId").value;

		//REFURBISHMENT and KITTING and TRACKABLE and NOT a KIT -
		if ("REFURBISHMENT" == svcItemId && "KIT" == svcGrpCode && "" == kitCode) {
			var diffQty = qtyReq - qtyCmpl;
			if (diffQty > 0){
				var sAddnParams = "&xml:/WOAdjustInv/@ItemID=" + itemId;
				sAddnParams = sAddnParams + "&xml:/WOAdjustInv/@Description=" + desc;
				sAddnParams = sAddnParams + "&xml:/WOAdjustInv/@EnterpriseCode=" + enterpriseCode;
				sAddnParams = sAddnParams + "&xml:/WOAdjustInv/@NodeKey=" + nodeKey;
				sAddnParams = sAddnParams + "&xml:/WOAdjustInv/@ActivityLocationId=" + actLocId;
				sAddnParams = sAddnParams + "&xml:/WOAdjustInv/@ProductClass=" + prodClass;
				sAddnParams = sAddnParams + "&xml:/WOAdjustInv/@Uom=" + uom;
				sAddnParams = sAddnParams + "&xml:/WOAdjustInv/@Quantity=" + diffQty;
				yfcShowDetailPopupWithParams(viewID, "", "900", "550", sAddnParams);
			}
		}
		else {
			if ("REFURBISHMENT" != svcItemId){
				alert("Service Item ID is not REFURBISHMENT");
			}
			else if ("KIT" != svcGrpCode){
				alert("Service Group Code is not KIT");
			}
			else if ("" != kitCode){
				alert("Item should not be a kit");
			}
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

	if (resolveValue("xml:/WorkOrder/@Status").equals("1400") &&
		resolveValue("xml:WOItem:/Item/InventoryParameters/@IsSerialTracked").equals("Y")){
%>
		<yfc:callAPI apiID='AP5'/>
<%
	}
%>
	<Input type="hidden" name="xml:/WorkOrder/@ReasonCode" value="<%=resolveValue("xml:/WorkOrder/@ReasonCode")%>"/>
	<Input type="hidden" name="xml:/WorkOrder/@ReasonText" value="<%=resolveValue("xml:/WorkOrder/@ReasonText")%>"/>
	<Input type="hidden" name="xml:/WorkOrder/@NumberOfActivities" value="<%=resolveValue("xml:/WorkOrder/@NumberOfActivities")%>"/>
	<Input type="hidden" name="xml:WOItem:/Item/InventoryParameters/@IsSerialTracked" value="<%=resolveValue("xml:WOItem:/Item/InventoryParameters/@IsSerialTracked")%>"/>
	<Input type="hidden" name="xml:WOItem:/Item/PrimaryInformation/@KitCode" value="<%=resolveValue("xml:WOItem:/Item/PrimaryInformation/@KitCode")%>"/>
	<Input type="hidden" name="xml:/WorkOrder/@WorkOrderKey" value="<%=resolveValue("xml:/WorkOrder/@WorkOrderKey")%>"/>
	<input type="hidden" name="xml:/WorkOrder/Extn/@ExtnIncidentNo" value="<%=resolveValue("xml:/WorkOrder/Extn/@ExtnIncidentNo")%>"/>
	<input type="hidden" name="xml:/OrderList/@TotalNumberOfRecords" value="<%= resolveValue("xml:OrderList:/OrderList/@TotalNumberOfRecords")%>"/>
	<input type="hidden" name="xml:/OrderList/Order/@OrderNo" value="<%= resolveValue("xml:OrderList:/OrderList/Order/@OrderNo")%>"/>
	<Input type="hidden" name="xml:/WorkOrder/@QuantityAllocated" value="<%=resolveValue("xml:/WorkOrder/@QuantityAllocated")%>"/>


	<Input type="hidden" name="xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity/WorkOrderActivityDtls/WorkOrderActivityDtl/@ActivityLocationId" value="<%=resolveValue("xml:/WorkOrder/WorkOrderActivities/WorkOrderActivity/WorkOrderActivityDtls/WorkOrderActivityDtl/@ActivityLocationId")%>"/>
	<Input type="hidden" name="xml:/WorkOrder/Extn/@ExtnIsRefurb" value="Y"/>



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
<!--		<td class="detaillabel" ><yfc:i18n>Service_Item_Group_Code</yfc:i18n></td>
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
-->
<% if(modifyView != "") { %>
			<Input type="hidden" name="xml:/WorkOrder/@ServiceItemGroupCode" value="<%=resolveValue("xml:/WorkOrder/@ServiceItemGroupCode")%>"/>
<%}%>
		<% if(createView !=""){
			String selectedServiceItemGroup = "";%>
			<yfc:loopXML binding="xml:ServiceItemGroupList:/CommonCodeList/@CommonCode" id="ServiceItemGroupList">
				<%if(equals(resolveValue("xml:ServiceItemGroupList:/CommonCode/@CodeValue"), resolveValue("xml:/WorkOrder/@ServiceItemGroupCode"))){
					selectedServiceItemGroup=resolveValue("xml:ServiceItemGroupList:/CommonCode/@CodeShortDescription");
				}%>
			</yfc:loopXML>
			<Input type="hidden" name="xml:/WorkOrder/@ServiceItemGroupCode" value="<%=resolveValue("xml:/WorkOrder/@ServiceItemGroupCode")%>"/>
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
			<td class="detaillabel"><yfc:i18n>Refurb Cost</yfc:i18n></td>
			<td class="protectedtext"><%=resolveValue("xml:/WorkOrder/Extn/@ExtnRefurbCost")%></td>
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

<%--			<td class="detaillabel"><yfc:i18n>Incident_Number</yfc:i18n></td>
			<td>
		<input type="text"	class="unprotectedinput"
		<%=getTextOptions("xml:/WorkOrder/Extn/@ExtnIncidentNo")%>/>
		<img class="lookupicon" onclick="callLookup(this,'NWCGIncidentLookup')" name="search" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_NWCG_Incident") %>/>&nbsp;
		</td>
			<Input type="hidden" name="xml:/WorkOrder/Extn/@ExtnIncidentNo" value="<%=resolveValue("xml:/WorkOrder/Extn/@ExtnIncidentNo")%>"/>

			</tr>
			<tr>
			<td class="detaillabel"><yfc:i18n>Fs_Acct_Code</yfc:i18n></td>
			<td>
				<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/WorkOrder/Extn/@ExtnFsAcctCode")%>/>
				</td>
			<td class="detaillabel"><yfc:i18n>Blm_Acct_Code</yfc:i18n></td>
			<td>
				<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/WorkOrder/Extn/@ExtnBlmAcctCode")%>/>
				</td>

			<td class="detaillabel"><yfc:i18n>Other_Acct_Code</yfc:i18n></td>
			<td>
				<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/WorkOrder/Extn/@ExtnOtherAcctCode")%>/>
			</td>
			</tr>
--%>
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
			<td class="detaillabel"><yfc:i18n>Refurb Cost</yfc:i18n></td>
			<td class="protectedtext"><%=resolveValue("xml:/WorkOrder/Extn/@ExtnRefurbCost")%></td>
			<Input type="hidden" name="xml:/WorkOrder/@Purpose" value="<%=resolveValue("xml:/WorkOrder/@Purpose")%>"/>
		<%}%>
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
        <td class="detaillabel" ></td>
		<td></td>
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
