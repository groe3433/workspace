<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/orderentry.jspf" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.dom.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.nwcg.icbs.yantra.constant.common.NWCGConstants" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/extn.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_orderDetails.js"></script>
<script language="Javascript" >
	IgnoreChangeNames();
	yfcDoNotPromptForChanges(true);
</script>

<%
	Date now = new Date();
	SimpleDateFormat formatDate = new SimpleDateFormat("MM/dd/yyyy");
	SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
	String strUserNode = resolveValue("xml:CurrentUser:/User/@Node");
	// user node is null dont invoke the api
	if(strUserNode != null && (!strUserNode.equals(""))) {
%>
		<yfc:callAPI apiID="AP6"/>	
<%
	}
	String strAgencyCode = resolveValue("xml:OrganizationOwnerAgency:/Organization/Extn/@ExtnOwnerAgency");
	String strDocumentType = resolveValue("xml:/Order/@DocumentType");
	String otherOrderNo = resolveValue("xml:/NWCGIncidentOrder/@IncidentNo");
	strDocumentType = "0007.ex";
	boolean bOtherOrder = false ;
	if(strDocumentType != null && (!strDocumentType.equals("0001"))) {
		bOtherOrder = true ;
	}
	
	// Default the enterprise code if it is not passed
	String enterpriseCode = (String) request.getParameter("xml:/Order/@EnterpriseCode");
	if (isVoid(enterpriseCode)) {
		enterpriseCode = getValue("CurrentOrganization", "xml:CurrentOrganization:/Organization/@PrimaryEnterpriseKey");
		request.setAttribute("xml:/Order/@EnterpriseCode", enterpriseCode);
	}

	// Default the seller to logged in organization if it plays a role of seller
	String sellerOrgCode = (String) request.getParameter("xml:/Order/@SellerOrganizationCode");
	if (isVoid(sellerOrgCode)) {
		if(isRoleDefaultingRequired((YFCElement) request.getAttribute("CurrentOrgRoleList"))) {
			sellerOrgCode = getValue("CurrentOrganization", "xml:CurrentOrganization:/Organization/@OrganizationCode");
		}
	}

	//prepareMasterDataElements(enterpriseCode, (YFCElement) request.getAttribute("OrganizationList"), (YFCElement) request.getAttribute("EnterpriseParticipationList"), (YFCElement) request.getAttribute("CurrencyList"), (YFCElement) request.getAttribute("OrderTypeList"),getValue("CurrentOrganization", "xml:CurrentOrganization:/Organization/@IsHubOrganization"));
	String exchangeOrderForReturn = resolveValue("xml:/ReturnOrder/@ReturnOrderHeaderKeyForExchange");
	String orderHeaderKeyVal = resolveValue("xml:/Order/@OrderHeaderKey");
	String newOtherOrderNumber = "";
	YFCElement otherOrderSession = (YFCElement)session.getAttribute("otherOrderNumber");

	if (otherOrderSession != null) {
		YFCNodeList orderValues = otherOrderSession.getElementsByTagName("NWCGIncidentOrder");
		for(int i=0; i<orderValues.getLength(); i++) {
			YFCElement NWCGOrder = (YFCElement)orderValues.item(i);
			newOtherOrderNumber = NWCGOrder.getAttribute("IncidentNo");
		}
	}
%>

<script language="javascript">

<% 
	if (!equals(exchangeOrderForReturn, "Y")) {
		if (!isVoid(orderHeaderKeyVal)) {
			YFCDocument orderDoc = YFCDocument.createDocument("Order");
			orderDoc.getDocumentElement().setAttribute("OrderHeaderKey",resolveValue("xml:/Order/@OrderHeaderKey"));
			// If this screen is shown as a popup, then open the order detail view for the new order as a popup as well (instead of refreshing the same screen).
			if (equals(request.getParameter(YFCUIBackendConsts.YFC_IN_POPUP), "Y")) {
%>

function showOrderDetailPopup() {
	window.CloseOnRefresh = "Y";
	callPopupWithEntity('order', '<%=orderDoc.getDocumentElement().getString(false)%>');
	window.close();
}
window.attachEvent("onload", showOrderDetailPopup);

<%
			} else {
%>

function showOrderDetail() {
	showDetailFor('<%=orderDoc.getDocumentElement().getString(false)%>');
}
window.attachEvent("onload", showOrderDetail);

<% 
			}
		}
	}
%>

function checkForDocumentType() {
	var docType = '<%=resolveValue("xml:/Order/@DocumentType")%>';
	if(docType == '')
		updateCurrentView();
}
//comment out this below line so we can avoid refresh/update view
//window.attachEvent("onload", checkForDocumentType);

function setShippingType(value) {
	var extnNavInfo = document.getElementById("xml:/Order/Extn/@ExtnNavInfo");
	if (extnNavInfo != null){
		extnNavInfo.value = value;
		//extnNavInfo.checked = true;
	} else {
		alert('ExtnNavInfo is null');
	}
	document.documentElement.setAttribute("SelectedShippingType", value);
	if (value == 'WILL_PICK_UP') {
		setExtnReqDelDateInDiffPanels(document.all("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCDATE"), 'xml:/Order/Extn/@TestExtnReqDeliveryDate_YFCDATE');
		setExtnReqDelDateInDiffPanels(document.all("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCTIME"), 'xml:/Order/Extn/@TestExtnReqDeliveryDate_YFCTIME');
	}
}
</script>

<%
	//exchange order processing
	boolean isExchangeOrderCreation = false;
	if(!isVoid(exchangeOrderForReturn)) {
		isExchangeOrderCreation = true;
		//call getOrderDetails api for defaulting information onto exchange order.
%>

		<yfc:callAPI apiID="AP5"/>

<%	
	} 
%>

<table class="view" width="100%">
	<tr>
		<td>
			<input type="hidden" name="xml:/Order/@DraftOrderFlag" value="Y"/>
			<input type="hidden" name="xml:/Order/AdditionalAddresses/AdditionalAddress/@AddressType" value='<%=NWCGConstants.ADDRESS_TYPE_DELIVER%>'/>
			<input type="hidden" name="xml:/Order/@EnteredBy" value="<%=resolveValue("xml:CurrentUser:/User/@Loginid")%>"/>
			<input type="hidden" name="xml:/Order/Extn/@ExtnSystemOfOrigin" value="ICBSR"/>
<% 
			if(isExchangeOrderCreation) { 
%>
				<input type="hidden" name="xml:/Order/@ReturnOrderHeaderKeyForExchange" value='<%=exchangeOrderForReturn%>'/>
				<input type="hidden" name="xml:/Order/@OrderPurpose" value='EXCHANGE'/>
				<input type="hidden" name="xml:/Order/@DocumentType" value='0001'/>
				<input type="hidden" name="xml:/Order/@ShipToKey" value="<%=resolveValue("xml:/Order/@ShipToKey")%>"/>
				<input type="hidden" name="xml:/Order/@BillToKey" value="<%=resolveValue("xml:/Order/@BillToKey")%>"/>
<% 
			} 
%>
			<!-- defaulting the document type to Other Order -->
			<input type="hidden" name="xml:/Order/@DocumentType" value='0007.ex'/>
		</td>
	</tr>
	<!-- defaulting the document type and enterprise code -->
	<input type="hidden" name="xml:/Order/@EnterpriseCode" value='NWCG'/>
	<input type="hidden" name="xml:/Order/@OwnerAgency" value="<%=strAgencyCode%>"/>
<% 
	if(isExchangeOrderCreation) {	
%>
		<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
			<jsp:param name="ScreenType" value="detail"/>
			<jsp:param name="HardCodeDocumentType" value="0001"/>
			<jsp:param name="ApplicationCode" value="omd"/>
			<jsp:param name="DisableEnterprise" value="Y"/>
		</jsp:include>
<% 
	} else {
%>
		<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
			<jsp:param name="ScreenType" value="detail"/>
			<jsp:param name="ShowEnterpriseCode" value="false"/>
			<jsp:param name="RefreshOnDocumentType" value="true"/>
			<jsp:param name="ShowDocumentType" value="false"/>
		</jsp:include>
<% 
	} 
	//Now call the APIs that are dependent on the common fields (Doc Type & Enterprise Code)
%>
	<yfc:callAPI apiID="AP1"/>
	<yfc:callAPI apiID="AP2"/>
	<yfc:callAPI apiID="AP4"/>
	<tr>
<% 
	String enterpriseCodeFromCommon = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");
	if(enterpriseCodeFromCommon == null || enterpriseCodeFromCommon.equals("")) {
		enterpriseCodeFromCommon = "NWCG";
	}
%>
		</td>
<%
		YFCElement sessionValue = (YFCElement)session.getAttribute("ReservedItems");
		if (sessionValue != null) {
			YFCNodeList itemlist = sessionValue.getElementsByTagName("ItemList");
			if (itemlist != null) {
				for(int i=0; i<itemlist.getLength(); i++) {
					YFCElement EItemList = (YFCElement)itemlist.item(i);
					YFCNodeList items = EItemList.getElementsByTagName("Item");
					for (int j=0; j<items.getLength();j++) {
	 					YFCElement curItemList = (YFCElement)items.item(j);
						//String ItemID = curItemList.getAttribute("ItemID");
						String TDate = curItemList.getAttribute("ShipDate"); 
    					String TDateStr1 = TDate.substring(0,4);
    					String TDateStr2 = TDate.substring(5,7);
    					String TDateStr3 = TDate.substring(8,10);
    					TDate = TDateStr2+"/"+TDateStr3+"/"+TDateStr1;
						curItemList.setAttribute("ShipDate",TDate);   
						//<!-- Top of CR-44 Changes - JK !-->	
						String strItemId = curItemList.getAttribute("ItemID");
						String strShipNode = curItemList.getAttribute("ShipNode");
						strItemId = strItemId.trim(); //trim whitespaces
						strShipNode = strShipNode.trim(); //trim whitespaces
						YFCElement elePopItem = YFCDocument.createDocument("Item").getDocumentElement(); 
						elePopItem.setAttribute("ItemID",strItemId);
						elePopItem.setAttribute("Node",strShipNode);
%>
						<yfc:callAPI serviceName="NWCGPopulateItemDetailsService" inputElement='<%=elePopItem%>'/>
<%
						YFCElement record = (YFCElement) request.getAttribute("Item");
						String strRFIQty = "";
						if(record != null)
							strRFIQty = record.getAttribute("AvailableQty");
%>
						<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_"+j+"/Extn/@ExtnOrigReqQty",curItemList.getAttribute("SelectedQTY"))%>/>
						<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_"+j+"/Extn/@ExtnQtyRfi",strRFIQty)%>/>
						<!-- Bottom of CR-44 Changes - JK !-->
						<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_"+j+"/@OrderedQty",curItemList.getAttribute("SelectedQTY"))%>/>
						<!-- CR-530 Changes - GN !-->
						<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_"+j+"/@ReservationMandatory","Y")%>/>
						<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_"+j+"/@ReservationID",curItemList.getAttribute("ReservationID"))%>/> 
						<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_"+j+"/@ReqShipDate",curItemList.getAttribute("ShipDate"))%>/> 
						<!-- CR-530 Changes - GN !-->
						<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_"+j+"/@ShipNode",curItemList.getAttribute("ShipNode"))%>/>
						<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_"+j+"/Item/@ItemID",curItemList.getAttribute("ItemID"))%>/>
						<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_"+j+"/Item/@UnitOfMeasure",curItemList.getAttribute("UnitOfMeasure"))%>/>
						<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine_"+j+"/Item/@ProductClass",curItemList.getAttribute("ProductClass"))%>/>
<% 
					}
 			 	}
			}
		}
%>
		<!-- The incident Nuber -->
		<!-- CR 79 Fix -->
		<td class="detaillabel" ><yfc:i18n>Other_Order_#</yfc:i18n></td>
		<!-- CR 79 End fix -->
		<td nowrap="true" >
			<!--<%=getTextOptions("xml:/Order/Extn/@IncidentNo") %> --> 
			<!-- CR 61 k.spellazza 2009-01-11 -->
			<input type="text" class="unprotectedinput" name="xml:/Order/Extn/@ExtnIncidentNo" dataType="STRING" value="<%=newOtherOrderNumber%>" onblur="fetchDataWithParams(this,'NWCGGetIncidentOrderList',updateIncidentDetails,setIncidentParams(this));"/>
			<!--END OF CR 61 -->
			<!-- OTHNWCGIncident vs NWCGIncidentLookup -->
			<img class="lookupicon" onclick="callOrderLookup('xml:/Order/Extn/@ExtnIncidentNo','xml:/Order/Extn/@ExtnIncidentYear','<%=strDocumentType%>','NWCGOtherOrderLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Other") %> />
		</td>
		<td class="detaillabel"><yfc:i18n>Ship_Cache</yfc:i18n></td>
		<td>
			<input type="text" class="protectedinput" readonly="true" <%=getTextOptions("xml:/Order/@ShipNode",strUserNode)%>/><!-- Suryasnat: Change made here for issue 518-->
		</td>
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Customer_Id</yfc:i18n>
			<input type="hidden" value='Y' name="IS_ACTIVE"/>
		</td>
<% 
		if (!isExchangeOrderCreation) { 
%>
		<td nowrap="true" >
			<input type="text" onblur="makeUppercase(this);fetchDataFromServer(this,'getCustomerDetails',updateCustDetailsForOtherIssue);" class="unprotectedinput"  <%=getTextOptions("xml:/Order/@BillToID")%>/>
<%
%>
			<img class="lookupicon" onclick="callLookup(this,'NWCGCustomerLookUp')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Customer")%>/>
<% 
		} else { 
%>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@BuyerOrganizationCode"/></td>
		<input type="hidden" name="xml:/Order/@BuyerOrganizationCode" value="<%=getValue("Order", "xml:/Order/@BuyerOrganizationCode")%>"/>
<% 
		} 
%>
		<td class="detaillabel" ><yfc:i18n>Customer_Name</yfc:i18n></td>
		<td>
			<input type="text" class="unprotectedinput" size="30" readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnCustomerName")%>/>
		</td>
	</tr>
	<tr>
<%
		if(bOtherOrder) {
%>
		<td class="detaillabel"><yfc:i18n>Customer_PO</yfc:i18n></td>
		<td>
			<input type="text" class="unprotectedinput" size="30" maxLength="40" <%=getTextOptions("xml:/Order/Extn/@ExtnPoNo","xml:/Order/Extn/@ExtnPoNo")%>/>
		</td>
<%
		}
		if(!bOtherOrder) {
%>
			<td class="detaillabel" ><yfc:i18n>Order_Type</yfc:i18n></td>
			<td>
				<select class="combobox" <%=getComboOptions("xml:/Order/@OrderType")%>>
					<yfc:loopOptions binding="xml:OrderTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
				</select>
			</td>
<%
		}
%>
		<!-- CR 321 ks 2008-09-29 -->
		<td class="detaillabel" ><yfc:i18n>Issue_Date</yfc:i18n></td>
		<td>
			<!-- CR 543 - comenting out this line: <input type="hidden" name="xml:/Order/@OrderDate"/> -->
			<!-- <input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/@OrderDate",formatDate.format(now))%>/>
			<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/> -->
			<!-- Timestamp added for 8.0 Upgrade - GN - 04/13/09 -->
			<!--
				<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/@OrderDate_YFCDATE",formatDate.format(now))%>/>
				<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
				<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/@OrderDate_YFCTIME",formatTime.format(now))%>/>
				<img class="lookupicon" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
			-->
			<!-- TOP of CR 578 User Locale Consideration -->
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/@OrderDate_YFCDATE","xml:/Order/@OrderDate_YFCDATE", getTodayDate())%>/>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/@OrderDate_YFCTIME","xml:/Order/@OrderDate_YFCTIME", getCurrentTime())%>/>
			<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %>/>
			<!-- BOTTOM of CR 578 User Locale Consideration -->
		</td>
		<!-- end of CR 321 -->
	</tr>
	<tr>
<!-- other incident details -->
<%
		if(bOtherOrder) {
%>
			<td class="detaillabel"><yfc:i18n>Other_Order_Name</yfc:i18n></td>
<%
		} else {
%>
			<td class="detaillabel"><yfc:i18n>Incident_Name</yfc:i18n></td>
<%
		}
%>
		<td>
			<input type="text" class="unprotectedinput" readonly="true" size="30" maxLength="50" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentName")%>/>
		</td>
<%
		if(bOtherOrder) {
%>
			<td class="detaillabel"><yfc:i18n>Other_Order_Type</yfc:i18n></td>
			<td>
				<input type="text" class="unprotectedinput" readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentType")%>/>
			</td>
<%
		} else {
%>
			<td class="detaillabel"><yfc:i18n>Incident_Type</yfc:i18n></td>
			<td>
				<input type="text" class="unprotectedinput" readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentType")%>/>
			</td>
<%
		}
%>
<%
		if(!bOtherOrder) {
%>
		<td class="detaillabel"><yfc:i18n>Incident_Team_Type</yfc:i18n></td>
		<td>
			<input type="text" class="unprotectedinput" readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentTeamType")%>/>
		</td>
<%
		}
%>
	</tr>
	<tr>
		<td class="detaillabel"><yfc:i18n>Cost_Center</yfc:i18n></td>
		<td><input type="text" class="unprotectedinput" size="30" maxLength="10" onBlur="checkExtnCostCenter(this)" <%=getTextOptions("xml:/Order/Extn/@ExtnCostCenter")%>/></td>
		<td class="detaillabel"><yfc:i18n>Functional_Area</yfc:i18n></td>
		<td><input type="text" class="unprotectedinput" size="30" maxLength="16" onBlur="checkExtnFunctionalArea(this)" <%=getTextOptions("xml:/Order/Extn/@ExtnFunctionalArea")%>/></td>
		<td class="detaillabel"><yfc:i18n>Work_Breakdown_Structure</yfc:i18n></td>
		<td><input type="text" class="unprotectedinput" size="30" maxLength="12" onBlur="checkExtnWBS(this)" <%=getTextOptions("xml:/Order/Extn/@ExtnWBS")%>/></td>
	</tr>
	<!-- FBMS elements -->
	<tr>
		<td class="detaillabel"><yfc:i18n>Fs_Acct_Code</yfc:i18n></td>
		<td>
			<!--<input type="text" class="protectedinput" readonly="true" onBlur="populateShippingAndSACode(this)" <%=getTextOptions("xml:/Order/Extn/@ExtnFsAcctCode")%>/>-->
			<input type="text" class="unprotectedinput"  onBlur="populateShippingAndSACode(this)"  <%=getTextOptions("xml:/Order/Extn/@ExtnFsAcctCode")%>/>
		</td>
		<td class="detaillabel"><yfc:i18n>Override_Code</yfc:i18n></td>
		<td>
			<input type="text" class="unprotectedinput" maxLength=4 onBlur="validateOverrideCodeLength(this);populateShippingAndSACode(this);" <%=getTextOptions("xml:/Order/Extn/@ExtnOverrideCode")%>/>
		</td>
		<td class="detaillabel"><yfc:i18n>Other_Acct_Code</yfc:i18n></td>
		<td>
			<input type="text" class="unprotectedinput" onBlur="populateShippingAndSACode(this)"  <%=getTextOptions("xml:/Order/Extn/@ExtnOtherAcctCode")%>/>
		</td>
	</tr>
<% 
		if(isExchangeOrderCreation) { 
%>
			<td class="detaillabel" >
				<yfc:i18n>Exchange_Type</yfc:i18n>
			</td>
			<td>
				<select name="xml:/Order/@ExchangeType" class="combobox">
					<yfc:loopOptions binding="xml:ExchangeTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/Order/@ExchangeType" isLocalized="Y"/>
				</select>
			</td>
<% 
		} 
%>
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Currency</yfc:i18n></td>
		<td>
<%
			String selectedValue = resolveValue("xml:/CurrencyList/@Currency");
			if(selectedValue == null || selectedValue.equals("")) {
				selectedValue= "USD";
			}
%>
			<!-- Top of CR 488: remove the combobox and default currency to USD -->
			<input type="text" class="protectedinput" <%=getTextOptions("xml:/Order/PriceInfo/@Currency","xml:/CurrencyList/@Currency",selectedValue)%>/>
			<!--
				<select class="combobox" <%=getComboOptions("xml:/Order/PriceInfo/@Currency")%>>
					<yfc:loopOptions binding="xml:/CurrencyList/@Currency" name="CurrencyDescription" value="Currency" selected="<%=selectedValue%>" isLocalized="Y"/>
				</select>
			-->
			<!-- Bottom of CR 488 -->
		</td>
<%
		YFCElement elem = (YFCElement) request.getAttribute("AccountCodeCommonCodeList");
		String strFundCode = "" , strObjectClass = "" ;
		if(elem != null) {
			Iterator itr = elem.getChildren();
			if(itr != null) {
				while(itr.hasNext()) {
					YFCElement child = (YFCElement) itr.next();
					String strDesc = child.getAttribute("CodeShortDescription");
					if(strDesc.equals("FUND_CODE")) {
						strFundCode = child.getAttribute("CodeValue");
					} else if(strDesc.equals("OBJECT_CLASS")) {
						strObjectClass = child.getAttribute("CodeValue");
					}
				}
			}
		}
%>
		<td class="detaillabel"><yfc:i18n>Blm_Acct_Code</yfc:i18n></td>
		<td>
			<input type="text" class="protectedinput" size="50" readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnBlmAcctCode")%>/>
		</td>
<%	
		String strShipFundCode = "" , strShipObjectClass = "" ;
		if(elem != null) {
			Iterator itr = elem.getChildren();
			if(itr != null) {
				while(itr.hasNext()) {
					YFCElement child = (YFCElement) itr.next();
					String strDesc = child.getAttribute("CodeShortDescription");
					if(strDesc.equals("FUND_CODE")) {
						strShipFundCode = child.getAttribute("CodeValue");
					} else if(strDesc.equals("OBJECT_CLASS")) {
						strShipObjectClass = child.getAttribute("CodeValue");
					}
				}
			}
		}
		YFCElement organizationInput = null;
		organizationInput = YFCDocument.parse("<Organization OrganizationCode=\"" + strUserNode + "\" />").getDocumentElement();
		YFCElement organizationTemplate = YFCDocument.parse("<OrganizationList><Organization><Extn ExtnOwnerAgency=\"\"/> </Organization></OrganizationList>").getDocumentElement();
%>
		<yfc:callAPI apiName="getOrganizationList" inputElement="<%=organizationInput%>" templateElement="<%=organizationTemplate%>" outputNamespace="OrganizationList"/>
<% 
		String strBLMacct = resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnOwnerAgency"); 
%>
		<!-- 2.1 enhancement kjs 2008*07*10  -->
		<td class="detaillabel"><yfc:i18n>Shipping_Acct_Code</yfc:i18n></td>
		<td>
<% 
			if (strBLMacct.equals("BLM")) {
%>
				<input type="text" class="unprotectedinput" size="50" maxlength="40" <%=getTextOptions("xml:/Order/Extn/@ExtnShipAcctCode")%>/>
<%
			} else {
%>
				<input type="text" class="unprotectedinput" maxLength="50" size="50" <%=getTextOptions("xml:/Order/Extn/@ExtnShipAcctCode")%>/>
<%
			}
%>
		</td>
	</tr>
	<tr>
		<td class="detaillabel"><yfc:i18n>SA_Override_Code</yfc:i18n></td>
		<td>
			<input type="text" class="unprotectedinput" style="width:175px" <%=getTextOptions("xml:/Order/Extn/@ExtnSAOverrideCode")%>/>
		</td>
		
		<% 
		if( strUserNode.equals("IDGBK") ) {
			
			String othOrderTypeStr = "<CommonCode CodeType=\"OTH_ORDER_TYPE\"/>";
			YFCElement othOrderTypeInput = YFCDocument.parse(othOrderTypeStr).getDocumentElement();
			YFCElement othOrderTypeTemplate = YFCDocument.parse("<CommonCodeList TotalNumberOfRecords=\"\"><CommonCode CodeType=\"\" CodeShortDescription=\"\" CodeValue=\"\" CommonCodeKey=\"\"/></CommonCodeList>").getDocumentElement();
		%>
		<yfc:callAPI apiName="getCommonCodeList" inputElement="<%=othOrderTypeInput%>" templateElement="<%=othOrderTypeTemplate%>" outputNamespace="OthOrderTypeList"/>
		
		
		<td class="detaillabel"><yfc:i18n>Order_Type</yfc:i18n></td>
	    <td class="searchcriteriacell" nowrap="true">
	       <select class="combobox" name="xml:/Order/@OrderType" id="orderTypeCB">

				<option value=''></option> 
				<%
				
				com.yantra.yfc.dom.YFCElement elemOrderList = (com.yantra.yfc.dom.YFCElement) request.getAttribute("OthOrderTypeList");
				com.yantra.yfc.dom.YFCNodeList nlOrderType = elemOrderList.getElementsByTagName("CommonCode");

				for(int indexOrderType = 0 ; indexOrderType < nlOrderType.getLength() ; indexOrderType++)
				{
					com.yantra.yfc.dom.YFCElement elemOrderType = (com.yantra.yfc.dom.YFCElement) nlOrderType.item(indexOrderType);
					String shortDesc = elemOrderType.getAttribute("CodeShortDescription");
					if( shortDesc.equals("Normal") )
					{
				%>
					
					<%} 
					else 
					{%>
					<option value='<%=elemOrderType.getAttribute("CodeValue")%>'> <%=elemOrderType.getAttribute("CodeShortDescription")%></option>
				<%
					}
				}
				%>

			</select>
	    </td>
	    <%} %>
		
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Actual_Delivery_Date</yfc:i18n></td>
		<td>
			<input type="hidden" name="xml:/Order/OrderDates/OrderDate/@DateTypeId" value="NWCG_DATE"/>
			<!--
				<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ActualDate_YFCDATE",formatDate.format(now))%>/>
				<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
				<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ActualDate_YFCTIME",formatTime.format(now))%>/>
				<img class="lookupicon" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
			-->
			<!-- TOP of CR 578 User Locale Consideration -->
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ActualDate_YFCDATE", getTodayDate())%>/>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ActualDate_YFCTIME", getCurrentTime())%>/>
			<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %>/>
			<!-- BOTTOM of CR 578 User Locale Consideration -->
		</td>
		<td class="detaillabel" ><yfc:i18n>Estimated_Delivery_Date</yfc:i18n></td>
		<td>
			<!--
				<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ExpectedDate_YFCDATE",formatDate.format(now))%>/>
				<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
				<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ExpectedDate_YFCTIME",formatTime.format(now))%>/>
				<img class="lookupicon" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
			-->
			<!-- TOP of CR 578 User Locale Consideration -->
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ExpectedDate_YFCDATE", getTodayDate())%>/>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ExpectedDate_YFCTIME", getCurrentTime())%>/>
			<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %>/>
			<!-- BOTTOM of CR 578 User Locale Consideration -->
		</td>
		<td class="detaillabel" ><yfc:i18n>Requested_Delivery_Date</yfc:i18n></td>
		<td>
			<input type="hidden" <%=getTextOptions("xml:/Order/@ReqDeliveryDate")%>/>
			<input type="text" class="unprotectedinput" onChange="setRequestDeliverDate(this)" <%=getTextOptions("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCDATE")%>/>
			<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
			<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCTIME")%>/>
			<img class="lookupicon" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
			<input type="hidden" name="xml:/Order/Extn/@ExtnOtherAmount" value="0.0"/>
			<input type="hidden" name="xml:/Order/Extn/@ExtnBlmAmount" value="0.0"/>
			<input type="hidden" name="xml:/Order/Extn/@ExtnFsAmount" value="0.0"/>
		</td>
	</tr>
<%
		String extnNavInfo = resolveValue("xml:/Order/Extn/@ExtnNavInfo");
		String shipAddrChecked = "'N'";
		String navInstrChecked = "'N'"; 
		String willPickUpChecked = "'N'";
		if (extnNavInfo != null && extnNavInfo.equals("SHIP_ADDRESS")) {
			shipAddrChecked = "checked='Y'";
		} else if (extnNavInfo != null && extnNavInfo.equals("NAV_INST")) {
			navInstrChecked = "checked='Y'";
		} else if (extnNavInfo != null && extnNavInfo.equals("WILL_PICK_UP")) {
			willPickUpChecked = "checked='Y'";
		}
%>
	<tr>
		<td class="detaillabel">Shipping Type</td>
		<td colspan="3">
			<input type="radio" onclick=setShippingType("SHIP_ADDRESS") value="SHIP_ADDRESS" name="xml:/Order/Extn/@ExtnNavInfo" id="xml:/Order/Extn/@ExtnNavInfo" <%=shipAddrChecked%> >Ship To</input>
			<input type="radio" onclick=setShippingType("NAV_INST") value="NAV_INST" name="xml:/Order/Extn/@ExtnNavInfo" id="xml:/Order/Extn/@ExtnNavInfo" <%=navInstrChecked%> >Shipping Instructions</input>
			<input type="radio" onclick=setShippingType("WILL_PICK_UP") value="WILL_PICK_UP" name="xml:/Order/Extn/@ExtnNavInfo" id="xml:/Order/Extn/@ExtnNavInfo" <%=willPickUpChecked%> >Will Pick Up</input>	
		</td>
		<input type="hidden" name="xml:/Order/Extn/@ExtnNavInfo" value="<%=resolveValue("xml:/Order/Extn/@ExtnNavInfo")%>"/>
	</tr>
</table>
<% 
	session.setAttribute("otherOrderNumber",null); 
%>