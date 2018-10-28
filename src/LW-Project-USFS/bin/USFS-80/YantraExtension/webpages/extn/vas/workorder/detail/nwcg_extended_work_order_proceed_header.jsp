<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@include file="/console/jsp/currencyutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.util.*" %>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/workorder.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_workorderDetails.js"></script>
<script>
	function showMoveRequestSearch(sViewID,sMoveRequestNodeKey,sMoveRequestEnterpriseCode,sMoveRequestNo)
	{   
		   var MoveRequestNodeKey = document.all(sMoveRequestNodeKey).value;
		   var MoveRequestEnterpriseCode = document.all(sMoveRequestEnterpriseCode).value;
		   var MoveRequestNo = document.all(sMoveRequestNo).value;
		   var entity="wmsmoverequest";
		   var sAddnParams = "&xml:yfcSearchCriteria:/MoveRequest/@Node="+MoveRequestNodeKey;
		   sAddnParams = sAddnParams + "&xml:yfcSearchCriteria:/MoveRequest/MoveRequestLines/MoveRequestLine/@EnterpriseCode="+MoveRequestEnterpriseCode;
		   sAddnParams = sAddnParams + "&xml:/MoveRequest/WorkOrder/@WorkOrderNo="+MoveRequestNo;
		   yfcShowListPopupWithParams(sViewID,"",'1300', '900','',entity, sAddnParams);
	}
	function showTaskSearch(sViewID,sTaskNodeKey,sTaskEnterpriseCode,sTaskNo)
	{   
		   var TaskNodeKey = document.all(sTaskNodeKey).value;
		   var TaskEnterpriseCode = document.all(sTaskEnterpriseCode).value;
		   var TaskNo = document.all(sTaskNo).value;
		   var entity="task";
		   var sAddnParams = "&xml:/Task/@Node="+TaskNodeKey;
		   sAddnParams = sAddnParams + "&xml:/Task/@EnterpriseKey="+TaskEnterpriseCode;
		   sAddnParams = sAddnParams + "&xml:/Task/TaskReferences/@WorkOrderNo="+TaskNo;
		   yfcShowListPopupWithParams(sViewID,"",'1300', '900','',entity, sAddnParams);
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

	String strSIGC=resolveValue("xml:/WorkOrder/@ServiceItemGroupCode");
%>

<input type="hidden" name="xml:/WorkOrder/@ReasonCode" />
<input type="hidden" name="xml:/WorkOrder/@ReasonText" />
<yfc:makeXMLInput name="WorkOrderKey">
	<yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderKey" value="xml:/WorkOrder/@WorkOrderKey" />
</yfc:makeXMLInput>
<yfc:makeXMLInput name="shipmentKey">
	<yfc:makeXMLKey binding="xml:/WorkOrder/@ShipmentKey" value="xml:/WorkOrder/@ShipmentKey" />
</yfc:makeXMLInput>
<input type="hidden" name="shipmentKey" value='<%=getParameter("shipmentKey")%>'/>

<table class="view" width="100%">
	<% if(modifyView != "" || createView != "") {%> 
	<tr>
			<td class="detaillabel" ><yfc:i18n>Node</yfc:i18n></td>
			<td class="protectedtext"><%=resolveValue("xml:/WorkOrder/@NodeKey")%></td>
			<Input type="hidden" name="xml:/WorkOrder/@NodeKey" value="<%=resolveValue("xml:/WorkOrder/@NodeKey")%>"/>
			<td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
			<td class="protectedtext"><%=resolveValue("xml:/WorkOrder/@EnterpriseCode")%></td>
			<Input type="hidden" name="xml:/WorkOrder/@EnterpriseCode" value="<%=resolveValue("xml:/WorkOrder/@EnterpriseCode")%>"/>
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

		<%
			// Top of CR 271 - jsk //
			YFCElement commonCodeInput = null;
			commonCodeInput = YFCDocument.parse("<CommonCode CallingOrganizationCode=\"" + resolveValue("xml:/WorkOrder/@EnterpriseCode") + "\" CodeType=\"SERVICE_ITEM_GROUP\" />").getDocumentElement();
			YFCElement commonCodeTemplate = YFCDocument.parse("<CommonCodeList> <CommonCode CodeShortDescription=\"\" CodeType=\"\" CodeValue=\"\"/> </CommonCodeList>").getDocumentElement(); 
		%>
			<yfc:callAPI apiName="getCommonCodeList" inputElement="<%=commonCodeInput%>" templateElement="<%=commonCodeTemplate%>"/>
		<%
			YFCElement commonCodeListElem = (YFCElement) request.getAttribute("CommonCodeList");
			if (commonCodeListElem != null){
				for(Iterator listItr = commonCodeListElem.getChildren();listItr.hasNext();) {
					YFCElement commonCodeElem = (YFCElement)listItr.next();
					String sCodeValue = commonCodeElem.getAttribute("CodeValue");
					if( !sCodeValue.equals("KIT") && !sCodeValue.equals("DKIT")) {
						commonCodeListElem.removeChild(commonCodeElem);
					}
				}
			}
			// Bottom of CR 271 - jsk //
		%>

		<td class="detaillabel" ><yfc:i18n>Service_Item_Group_Code</yfc:i18n></td>

		<% if(modifyView != "" || createView != "") {%> 
			<td class="protectedtext"><%=resolveValue("xml:/WorkOrder/@ServiceItemGroupCode")%></td>
			<Input type="hidden" name="xml:/WorkOrder/@ServiceItemGroupCode" value="<%=resolveValue("xml:/WorkOrder/@ServiceItemGroupCode")%>"/>
		<%} else {%>
			<td class="searchcriteriacell">
            <select <%=getComboOptions("xml:/WorkOrder/@ServiceItemGroupCode")%> class="combobox" onchange="refreshpage()">
                <yfc:loopOptions binding="xml:/CommonCodeList/@CommonCode" name="CodeShortDescription"
                value="CodeValue" selected="xml:/WorkOrder/@ServiceItemGroupCode" isLocalized="Y"/>
            </select>
			</td>
		<%}%>

		<%if(createView != ""){%>
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
		<%} else {%>
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
		<td class="searchcriteriacell">
			<select <%=getComboOptions("xml:/WorkOrder/@Priority")%> class="combobox">
				<yfc:loopOptions binding="xml:WorkOrderPriorityList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
				value="CodeValue" selected="3" isLocalized="Y"/>
			</select>
			<Input type="hidden" name="xml:/WorkOrder/@Priority" value="<%=resolveValue("xml:/WorkOrder/@Priority")%>"/>
		</td>
		<td  class="detaillabel" ><yfc:i18n>Start_No_Earlier_Than</yfc:i18n></td>
		<td nowrap="true" >
			<input class="dateinput" type="text" <%=getTextOptions("xml:/WorkOrder/@StartNoEarlierThan_YFCDATE","xml:/WorkOrder/@StartNoEarlierThan_YFCDATE", getTodayDate())%>/>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="text" <%=getTextOptions("xml:/WorkOrder/@StartNoEarlierThan_YFCTIME","xml:/WorkOrder/@StartNoEarlierThan_YFCTIME", getCurrentTime())%>/>
			<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %>/>
		</td>
		<td  class="detaillabel" ><yfc:i18n>Finish_No_Later_Than</yfc:i18n></td>
		<td nowrap="true" >
			<input class="dateinput" type="text" <%=getTextOptions("xml:/WorkOrder/@FinishNoLaterThan_YFCDATE","xml:/WorkOrder/@FinishNoLaterThan_YFCDATE")%>/>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="text" <%=getTextOptions("xml:/WorkOrder/@FinishNoLaterThan_YFCTIME","xml:/WorkOrder/@FinishNoLaterThan_YFCTIME")%>/>
			<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %>/>
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
			<% if(strSIGC.equals("KIT")) {%>
				<td><input type="text" class="protectedinput" readonly="true" <%=getTextOptions("xml:/WorkOrder/@ServiceItemID","KITTING")%> ></td>
			<%} else if(strSIGC.equals("DKIT")) {%>
				<td><input type="text" class="protectedinput" readonly="true" <%=getTextOptions("xml:/WorkOrder/@ServiceItemID","DEKITTING")%> ></td>
			<%} else {%>
				<td class="searchcriteriacell" nowrap="true" >
				<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/WorkOrder/@ServiceItemID")%>/>
				<% String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", resolveValue("xml:/WorkOrder/@EnterpriseCode")); %>
				<img class="lookupicon" name="search" onclick="callServiceItemLookup('xml:/WorkOrder/@ServiceItemID','xml:/WorkOrder/@ProductClass','xml:/WorkOrder/@Uom', 'item','<%=resolveValue("xml:/WorkOrder/@ServiceItemGroupCode")%>','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Service_Item")%> />
				</td>
			<%}%>
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
			<input type="hidden" name="xml:/WorkOrder/@Purpose" value="<%=resolveValue("xml:/WorkOrder/@Purpose")%>"/>
		<%}%>

		<!-- Top of CR 562 - jsk -->
		<%
		String strMultiVas = "";
		YFCDocument ccDoc = YFCDocument.createDocument("CommonCode");
		YFCElement ccDocEle = ccDoc.getDocumentElement();
		ccDocEle.setAttribute("CodeType","NWCG_MULTI_VAS");
		ccDocEle.setAttribute("CodeValue",resolveValue("xml:CurrentUser:/User/@Node"));
		%>
		<yfc:callAPI apiName="getCommonCodeList" inputElement="<%=ccDocEle%>" outputNamespace="multiVasCode"/>
		<%
		strMultiVas = resolveValue("xml:multiVasCode:/CommonCodeList/CommonCode/@CodeShortDescription");

		if (strMultiVas.equals("YES")) {
			// - list locations in VAS-ZONE -
			YFCDocument equipDoc = YFCDocument.createDocument("Equipment");
			YFCElement el_equipDoc = equipDoc.getDocumentElement();
			el_equipDoc.setAttribute("IgnoreOrdering","N");
			el_equipDoc.setAttribute("Node",resolveValue("xml:CurrentUser:/User/@Node"));

			YFCElement el_EquipmentDetails=equipDoc.createElement("EquipmentDetails");
			el_equipDoc.appendChild(el_EquipmentDetails);

			YFCElement el_EquipmentDetail=equipDoc.createElement("EquipmentDetail");
			el_EquipmentDetail.setAttribute("EquipmentType","VAS Station");
			el_EquipmentDetails.appendChild(el_EquipmentDetail);
			//System.out.println("equipDoc "+equipDoc);

			YFCElement locTemplate = YFCDocument.parse("<Equipment EquipmentId=\"\" Node=\"\"> </Equipment>").getDocumentElement();
			%>
			<yfc:callAPI apiName="getEquipmentList" inputElement="<%=el_equipDoc%>" templateElement="<%=locTemplate%>" outputNamespace="vasLocations"/>
			<td class="detaillabel" ><yfc:i18n>Location</yfc:i18n></td>
			<td class="searchcriteriacell">
				<select <%=getComboOptions("xml:/WorkOrder/Extn/@ExtnLocationId")%> class="combobox">
					<yfc:loopOptions binding="xml:vasLocations:/Equipments/@Equipment" name="EquipmentId" value="EquipmentId" selected="xml:/WorkOrder/Extn/@ExtnLocationId" isLocalized="Y"/>
				</select>
				<input type="hidden" name="xml:/WorkOrder/Extn/@ExtnLocationId" value="<%=resolveValue("xml:/WorkOrder/Extn/@ExtnLocationId")%>"/>
			</td>
		<% } %>
		<!-- Bottom of CR 562 - jsk -->
	</tr>
	<tr>
        <td class="detaillabel" ></td>
		<%if(modifyView != ""){%>
			<td class="searchcriteriacell">
				<input type="checkbox" <%=getCheckBoxOptions("xml:/WorkOrder/@IgnoreRunQuantity", "xml:/WorkOrder/@IgnoreRunQuantity", "Y")%> yfcCheckedValue='Y' yfcUnCheckedValue=' ' ><yfc:i18n>Ignore_Run_Quantity</yfc:i18n></input>
			</td>
		<%}else if(createView != ""){%>
			<td class="searchcriteriacell">
				<input type="checkbox" disabled="true"  <%=getCheckBoxOptions("xml:/WorkOrder/@IgnoreRunQuantity", "xml:/WorkOrder/@IgnoreRunQuantity", "Y")%> yfcCheckedValue='Y' yfcUnCheckedValue=' ' ><yfc:i18n>Ignore_Run_Quantity</yfc:i18n></input>
			</td>
		<%}else{%>
			<td class="searchcriteriacell">
				<input type="checkbox" <%=getCheckBoxOptions("xml:/WorkOrder/@IgnoreRunQuantity", "xml:/WorkOrder/@IgnoreRunQuantity", "Y")%> yfcCheckedValue='Y' yfcUnCheckedValue=' ' ><yfc:i18n>Ignore_Run_Quantity</yfc:i18n></input>
			</td>
		<%}%>
        <td class="detaillabel" ></td>
		<td class="searchcriteriacell">
			<input type="checkbox" <%=getCheckBoxOptions("xml:/WorkOrder/@AutoRelease", "xml:/WorkOrder/@AutoRelease", "Y")%> yfcCheckedValue='Y' yfcUnCheckedValue=' ' ><yfc:i18n>Release_Immediately</yfc:i18n></input>
		</td>
<!--
        <td class="detaillabel" ></td>
		<td></td>
-->		
		<!--<%if(modifyView != ""){%>
	        <td class="detaillabel" ></td>
			<td class="searchcriteriacell">
				<input type="checkbox" <%=getCheckBoxOptions("xml:/WorkOrder/@OverrideModificationRules", "xml:/WorkOrder/@OverrideModificationRules", "Y")%> yfcCheckedValue='Y' yfcUnCheckedValue=' ' ><yfc:i18n>Work_Order_Override_Modifications</yfc:i18n></input>
			</td>
		<%}else{%>-->
<!--
	        <td class="detaillabel" ></td>
			<td></td>
-->
		<!--<%}%>-->

		<td class="detaillabel" ></td>
		<%if(strSIGC.equals("KIT")){%>
			<td class="searchcriteriacell">
				<input type="checkbox" <%=getCheckBoxOptions("xml:/WorkOrder/Extn/@ExtnPrintReport", "xml:/WorkOrder/Extn/@ExtnPrintReport", "Y")%> yfcCheckedValue='Y' yfcUnCheckedValue='N' ><yfc:i18n>Print_Report</yfc:i18n></input>
			</td>
		<%}%>

	</tr> 
	<yfc:makeXMLInput name="workOrderKey">
		<yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderNo" value="xml:/WorkOrder/@WorkOrderNo"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/@NodeKey" value="xml:/WorkOrder/@NodeKey"/>

		<yfc:makeXMLKey binding="xml:/WorkOrder/@EnterpriseCode" value="xml:/WorkOrder/@EnterpriseCode"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/@ServiceItemGroupCode" value="xml:/WorkOrder/@ServiceItemGroupCode"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/@Purpose" value="xml:/WorkOrder/@Purpose"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/@Priority" value="xml:/WorkOrder/@Priority"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/@StartNoEarlierThan_YFCDATE" value="xml:/WorkOrder/@StartNoEarlierThan_YFCDATE"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/@StartNoEarlierThan_YFCTIME" value="xml:/WorkOrder/@StartNoEarlierThan_YFCTIME"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/@FinishNoLaterThan_YFCDATE" value="xml:/WorkOrder/@FinishNoLaterThan_YFCDATE"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/@FinishNoLaterThan_YFCTIME" value="xml:/WorkOrder/@FinishNoLaterThan_YFCTIME"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/@ItemID" value="xml:/WorkOrder/@ItemID"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/@IgnoreRunQuantity" value="xml:/WorkOrder/@IgnoreRunQuantity"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/@AutoRelease" value="xml:/WorkOrder/@AutoRelease"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/@Uom" value="xml:/WorkOrder/@Uom"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/@ProductClass" value="xml:/WorkOrder/@ProductClass"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/@SegmentType" value="xml:/WorkOrder/@SegmentType"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/@Segment" value="xml:/WorkOrder/@Segment"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/@QuantityRequested" value="xml:/WorkOrder/@QuantityRequested"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/@ServiceItemID" value="xml:/WorkOrder/@ServiceItemID"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderMode" value="menu"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/Extn/@ExtnIncidentNo" value="xml:/WorkOrder/Extn/@ExtnIncidentNo"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/Extn/@ExtnFsAcctCode" value="xml:/WorkOrder/Extn/@ExtnFsAcctCode"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/Extn/@ExtnBlmAcctCode" value="xml:/WorkOrder/Extn/@ExtnBlmAcctCode"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/Extn/@ExtnOtherAcctCode" value="xml:/WorkOrder/Extn/@ExtnOtherAcctCode"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/Extn/@ExtnLocationId" value="xml:/WorkOrder/Extn/@ExtnLocationId"/>
		<yfc:makeXMLKey binding="xml:/WorkOrder/Extn/@ExtnPrintReport" value="xml:/WorkOrder/Extn/@ExtnPrintReport"/>
	</yfc:makeXMLInput>
	<input name="myEntityKey" type="hidden" value='<%=getParameter("workOrderKey")%>'/>
</table>
