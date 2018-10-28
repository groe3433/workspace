<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/orderentry.jspf" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.dom.*" %>
<%@ page import="com.nwcg.icbs.yantra.constant.common.NWCGConstants" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.StringUtil" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_cache_transfer.js"></script>

<script language="Javascript" >
IgnoreChangeNames();
yfcDoNotPromptForChanges(true);
</script>

<%
String strNode = StringUtil.nonNull(resolveValue("xml:CurrentUser:/User/@Node")) ;
YFCDocument inputDoc = YFCDocument.parse("<Organization OrganizationCode=\""+strNode+"\"/>");
YFCDocument templateDoc = YFCDocument.parse("<Organization OrganizationCode=\"\"> <Extn/> </Organization>");
if(!strNode.equals(""))
{%>
<yfc:callAPI apiName='getOrganizationList' inputElement='<%=inputDoc.getDocumentElement()%>' templateElement='<%=templateDoc.getDocumentElement()%>' outputNamespace='OrganizationList'/>
<%}%>

<%
String OwnerAgency = resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnOwnerAgency");
%>
<input type="hidden" name="xml:/Order/@ShippingOwnerAgency" value="<%=resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnOwnerAgency")%>"/>
<!--<input type="hidden" name="xml:/Order/@ShippingOwnerAgency" value="<%=OwnerAgency%>"/> -->

<%
Date now = new Date();
SimpleDateFormat formatDate = new SimpleDateFormat("MM/dd/yyyy");
SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");

// Default the enterprise code if it is not passed
String enterpriseCode = (String) request.getParameter("xml:/Order/@EnterpriseCode");
if (isVoid(enterpriseCode)) {
	enterpriseCode = getValue("CurrentOrganization", "xml:CurrentOrganization:/Organization/@PrimaryEnterpriseKey");
	request.setAttribute("xml:/Order/@EnterpriseCode", enterpriseCode);
}

// Default the seller to logged in organization if it plays a role of seller
String sellerOrgCode = (String) request.getParameter("xml:/Order/@SellerOrganizationCode");
if (isVoid(sellerOrgCode)) {
	if(isRoleDefaultingRequired((YFCElement) request.getAttribute("CurrentOrgRoleList"))){
		sellerOrgCode = getValue("CurrentOrganization", "xml:CurrentOrganization:/Organization/@OrganizationCode");
		//request.setAttribute("xml:/Order/@SellerOrganizationCode", orgCode);
	}
}

//prepareMasterDataElements(enterpriseCode, (YFCElement) request.getAttribute("OrganizationList"),
//                        (YFCElement) request.getAttribute("EnterpriseParticipationList"),
//                        (YFCElement) request.getAttribute("CurrencyList"),
//                        (YFCElement) request.getAttribute("OrderTypeList"),getValue("CurrentOrganization", "xml:CurrentOrganization:/Organization/@IsHubOrganization"));

String exchangeOrderForReturn = resolveValue("xml:/ReturnOrder/@ReturnOrderHeaderKeyForExchange");
String orderHeaderKeyVal = resolveValue("xml:/Order/@OrderHeaderKey");
String sLookup="NWCGCustomerLookUp";
String strAgencyCode = resolveValue("xml:OrganizationOwnerAgency:/Organization/Extn/@ExtnOwnerAgency");

%>

<script language="javascript">
<% if (!equals(exchangeOrderForReturn, "Y")) {
	if (!isVoid(orderHeaderKeyVal)) {
		YFCDocument orderDoc = YFCDocument.createDocument("Order");
		orderDoc.getDocumentElement().setAttribute("OrderHeaderKey",resolveValue("xml:/Order/@OrderHeaderKey"));

		// If this screen is shown as a popup, then open the order detail view for the new order
		// as a popup as well (instead of refreshing the same screen).
		if (equals(request.getParameter(YFCUIBackendConsts.YFC_IN_POPUP), "Y")) {
		%>
			function showOrderDetailPopup() {
				window.CloseOnRefresh = "Y";
				callPopupWithEntity('NWCorder', '<%=orderDoc.getDocumentElement().getString(false)%>');
				window.close();
			}
			window.attachEvent("onload", showOrderDetailPopup);
		<% } else { %>
			function showOrderDetail() {
				showDetailFor('<%=orderDoc.getDocumentElement().getString(false)%>');
			}
			window.attachEvent("onload", showOrderDetail);
		<% }
	}
} %>
</script>

<%
//exchange order processing
boolean isExchangeOrderCreation = false;
if(!isVoid(exchangeOrderForReturn)){
	isExchangeOrderCreation = true;
	//call getOrderDetails api for defaulting information onto exchange order.
	%>
	<yfc:callAPI apiID="AP5"/>
<% } %>

<table class="view" width="100%">
<tr>
<td>
<input type="hidden" name="xml:/Order/@DraftOrderFlag" value="Y"/>
<input type="hidden" name="xml:/Order/@EnteredBy" value="<%=resolveValue("xml:CurrentUser:/User/@Loginid")%>"/>
<% if(isExchangeOrderCreation){ %>
	<input type="hidden" name="xml:/Order/@ReturnOrderHeaderKeyForExchange" value='<%=exchangeOrderForReturn%>'/>
	<input type="hidden" name="xml:/Order/@OrderPurpose" value='EXCHANGE'/>
	<input type="hidden" name="xml:/Order/@DocumentType" value='0001'/>
	<input type="hidden" name="xml:/Order/@ShipToKey" value="<%=resolveValue("xml:/Order/@ShipToKey")%>"/>
	<input type="hidden" name="xml:/Order/@BillToKey" value="<%=resolveValue("xml:/Order/@BillToKey")%>"/>
<% } %>
</td>
</tr>
<!-- defaulting the document type and enterprise code -->
<input type="hidden" name="xml:/Order/@DocumentType" value='0006'/>
<input type="hidden" name="xml:/Order/@EnterpriseCode" value='NWCG'/>
<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnValidIncident")%> 
id="xml:/Order/Extn/@ExtnValidIncident" value='true'
/>


<% if(isExchangeOrderCreation){	%>
	<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
	<jsp:param name="ScreenType" value="detail"/>
	<jsp:param name="HardCodeDocumentType" value="0001"/>
	<jsp:param name="ApplicationCode" value="omd"/>
	<jsp:param name="DisableEnterprise" value="Y"/>
	</jsp:include>
<% } else {%>
	<jsp:include page="/extn/om/cache_transfer/detail/common_fields_transfer_issue.jsp" flush="true">
	<jsp:param name="ScreenType" value="detail"/>
	<jsp:param name="HardCodeDocumentType" value="0006"/>
	<jsp:param name="RefreshOnDocumentType" value="true"/>
	<jsp:param name="RefreshOnEnterpriseCode" value="true"/>
	<jsp:param name="ShowDocumentType" value="false"/>
	<jsp:param name="ShowEnterpriseCode" value="false"/>
	</jsp:include>
<% } %>

<% // Now call the APIs that are dependent on the common fields (Doc Type & Enterprise Code) %>
<yfc:callAPI apiID="AP1"/>
<yfc:callAPI apiID="AP2"/>
<yfc:callAPI apiID="AP4"/>

<tr>
<% String enterpriseCodeFromCommon = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>

<!--
<td class="detaillabel" ><yfc:i18n>Buyer</yfc:i18n></td>
-->

<% if (!isExchangeOrderCreation){ %>
	<!--
	<td nowrap="true" >
	<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@BillToID")%>/>
	<img class="lookupicon" onclick="callLookup(this,'<%=sLookup%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Customer") %> />
	</td>
	-->
<% } else { %>
	<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@BuyerOrganizationCode"/></td>
	<input type="hidden" name="xml:/Order/@BuyerOrganizationCode" value="<%=getValue("Order", "xml:/Order/@BuyerOrganizationCode")%>"/>
<% } %>

<!--
<td class="detaillabel" ><yfc:i18n>Seller</yfc:i18n></td>
-->

<% String enterpriseCodeForCommon = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
<% if (!isExchangeOrderCreation){ %>
	<td nowrap="true" >
	<input type="hidden" class="unprotectedinput" name="xml:/Order/@SellerOrganizationCode" value="<%=getValue("CurrentOrganization","xml:CurrentOrganization:/Organization/@PrimaryEnterpriseKey")%>">
	<!--
	<img class="lookupicon" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCodeForCommon%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
	-->
	</td>
<% } else { %>
	<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@SellerOrganizationCode"/></td>
	<input type="hidden" name="xml:/Order/@SellerOrganizationCode" value="<%=getValue("Order", "xml:/Order/@SellerOrganizationCode")%>"/>
<% } %>

</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Transfer_#</yfc:i18n></td>
<td>
<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Order/@OrderNo")%>/>
</td>
<!--<td class="detaillabel" ><yfc:i18n>Order_Type</yfc:i18n></td>
<td>
<select class="combobox" <%=getComboOptions("xml:/Order/@OrderType")%>>
<yfc:loopOptions binding="xml:OrderTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
value="CodeValue" isLocalized="Y"/>
</select>
</td>-->

<!-- CR 321 ks 2008-09-29 -->
<td class="detaillabel" ><yfc:i18n>Order_Date</yfc:i18n></td>
<td>
<input type="hidden" name="xml:/Order/@OrderDate"/>
<!--<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/@OrderDate",formatDate.format(now))%>/>
<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/> -->
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

<td class="detaillabel" ><yfc:i18n>Shipping_Cache</yfc:i18n></td>
<td nowrap="true">
<!-- 2.1 Enhancement READ ONLY SHIP CACHE ID -->
<input class="protectedtext" readonly="true" <%=getTextOptions("xml:/Order/@ShipNode","xml:CurrentUser:/User/@Node")%>/>
<!-- END 2.1 Enhancement READ ONLY SHIP CACHE ID --></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Receiving_Cache</yfc:i18n></td>
<td nowrap="true">
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@ReceivingNode")%> id="xml:/Order/@ReceivingNode" onblur="javascript:fetchDataWithParams(this,'getAccountCodes',updateAccountCodes,setParam(document.getElementById('xml:/Order/@ReceivingNode')));checkNodes(this);"/>
<img class="lookupicon" onclick="callLookup(this,'organization','xml:/Organization/OrgRoleList/OrgRole/@RoleKey=NODE')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
</td>

<!--<td class="detaillabel"><yfc:i18n>Incident_Year</yfc:i18n></td>-->
<td>
<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentYear")%> id="xml:/Order/Extn/@ExtnIncidentYear" 
/>
</td>



<!--<td class="detaillabel"><yfc:i18n>Incident_Number</yfc:i18n></td>-->
<td>
<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentNo")%> id="xml:/Order/Extn/@ExtnIncidentNo"
onblur="javascript:if(document.getElementById('xml:/Order/Extn/@ExtnIncidentNo').value != '' || document.getElementById('xml:/Order/Extn/@ExtnIncidentYear').value != ''){fetchDataWithParams(this,'getAccountCodes',updateAccountCodes,setIncidentParamCT(this));}"/>
<!--<img class="lookupicon" onclick="callIncidentLookup('xml:/Order/Extn/@ExtnIncidentNo','xml:/Order/Extn/@ExtnIncidentYear','NWCGIncidentLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Incident") %> />-->
</td>

</tr>
<!--
<tr>
<td class="detaillabel"><yfc:i18n>Incident_Name</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentName")%>/>
</td>
</tr>
-->
<tr>
<td class="detaillabel"><yfc:i18n>Receiving_Account_Code</yfc:i18n></td>
<td>
<input type="text"  class="protectedinput" size=45 maxLength=40 readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnRecvAcctCode")%>/>
</td>
<td class="detaillabel"><yfc:i18n>RA_Override_Code</yfc:i18n></td>
<td>
<input type="text"  class="protectedinput" size=4 maxLength=4 readonly="true"<%=getTextOptions("xml:/Order/Extn/@ExtnRAOverrideCode")%>/>
</td>

</td>
<td class="detaillabel"><yfc:i18n>BLM_Order_Acct_Code</yfc:i18n></td>
<td>
<input type="text" class="protectedinput" size=45 maxLength=40 readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnBlmAcctCode")%>/>
</td>

</tr>

<tr>
<td class="detaillabel"><yfc:i18n>Shipping_Account_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" size=45 maxLength=40 <%=getTextOptions("xml:/Order/Extn/@ExtnShipAcctCode")%>/>
</td>
<td class="detaillabel"><yfc:i18n>SA_Override_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" size=4 maxLength=4 <%=getTextOptions("xml:/Order/Extn/@ExtnSAOverrideCode")%>/>
</td>

</td>
<td class="detaillabel"><yfc:i18n>OTHER_Order_Acct_Code</yfc:i18n></td>
<td>
<input type="text" class="protectedinput" size=45 maxLength=40 readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnOtherAcctCode")%>/>
</td>

</tr>

<tr>

<td class="detaillabel"><yfc:i18n>FS_Order_Acct_Code</yfc:i18n></td>
<td>
<input type="text" class="protectedinput" size=45 maxLength=40 <%=getTextOptions("xml:/Order/Extn/@ExtnFsAcctCode")%>/>
</td>
<td class="detaillabel"><yfc:i18n>FS_Order_Override_Code</yfc:i18n></td>
<td>
<input type="text" class="protectedinput" size=4 maxLength=4 readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnOverrideCode")%>/>
</td>

<td class="detaillabel" ><yfc:i18n>Requested_Delivery_Date</yfc:i18n></td>
<td>
<input type="hidden" <%=getTextOptions("xml:/Order/@ReqDeliveryDate")%>/>
<input type="text" class="unprotectedinput" onChange="setRequestDeliverDate(this);setExtnReqDelDateInDiffPanels(this, 'xml:/Order/Extn/@TestExtnReqDeliveryDate_YFCDATE')" <%=getTextOptions("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCDATE")%>/>
<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
<input type="text" class="unprotectedinput"  onChange="setExtnReqDelDateInDiffPanels(this, 'xml:/Order/Extn/@TestExtnReqDeliveryDate_YFCTIME')" <%=getTextOptions("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCTIME")%>/>
<img class="lookupicon" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
<input type="hidden" <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ExpectedDate_YFCDATE", getTodayDate())%>/>
<input type="hidden" <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ExpectedDate_YFCTIME", getCurrentTime())%>/>
</td>

</tr>

<!--<td class="detaillabel" ><yfc:i18n>Currency</yfc:i18n></td>
<td>
<select class="combobox" <%=getComboOptions("xml:/Order/PriceInfo/@Currency")%>>
<yfc:loopOptions binding="xml:/CurrencyList/@Currency" name="CurrencyDescription"
value="Currency" selected="xml:/Order/PriceInfo/@Currency" isLocalized="Y"/>
</select>
</td>-->
<% if(isExchangeOrderCreation){ %>
<td class="detaillabel" ><yfc:i18n>Exchange_Type</yfc:i18n></td>
<td>
<select name="xml:/Order/@ExchangeType" class="combobox">
<yfc:loopOptions binding="xml:ExchangeTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
value="CodeValue" selected="xml:/Order/@ExchangeType" isLocalized="Y"/>
</select>
</td>
<% } %>
</tr>
</table>