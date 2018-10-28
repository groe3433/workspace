<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/orderentry.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.dom.*" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>

<script language="Javascript" >
IgnoreChangeNames();
yfcDoNotPromptForChanges(true);
</script>

<%
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
//System.out.println("org:" + orgCode);
//request.setAttribute("xml:/Order/@SellerOrganizationCode", orgCode);
}
}

//prepareMasterDataElements(enterpriseCode, (YFCElement) request.getAttribute("OrganizationList"),
//                        (YFCElement) request.getAttribute("EnterpriseParticipationList"),
//                        (YFCElement) request.getAttribute("CurrencyList"),
//                        (YFCElement) request.getAttribute("OrderTypeList"),getValue("CurrentOrganization", "xml:CurrentOrganization:/Organization/@IsHubOrganization"));

String exchangeOrderForReturn = resolveValue("xml:/ReturnOrder/@ReturnOrderHeaderKeyForExchange");
String orderHeaderKeyVal = resolveValue("xml:/Order/@OrderHeaderKey");
String sLookup = "NWCGCustomerLookUp";
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
<% }
}
}
%>
</script>
<%
//exchange order processing
boolean isExchangeOrderCreation = false;
if(!isVoid(exchangeOrderForReturn)){
isExchangeOrderCreation = true;
//call getOrderDetails api for defaulting information onto exchange order.
%>
<yfc:callAPI apiID="AP5"/>
<%	} %>


<table class="view" width="100%">
<tr>
<td>
<input type="hidden" name="xml:/Order/@DraftOrderFlag" value="Y"/>
<input type="hidden" name="xml:/Order/@EnteredBy" value="<%=resolveValue("xml:CurrentUser:/User/@Loginid")%>"/>
<% if(isExchangeOrderCreation){ %>
<input type="hidden" name="xml:/Order/@ReturnOrderHeaderKeyForExchange" value='<%=exchangeOrderForReturn%>'/>
<input type="hidden" name="xml:/Order/@OrderPurpose" value='EXCHANGE'/>
<input type="hidden" name="xml:/Order/@DocumentType" value='0001'/>
<input type="hidden" name="xml:/Order/@BillToKey" value="<%=resolveValue("xml:/Order/@BillToKey")%>"/>
<input type="hidden" name="xml:/Order/@ShipToKey" value="<%=resolveValue("xml:/Order/@ShipToKey")%>"/>
<% } %>
</td>
</tr>

<% if(isExchangeOrderCreation){	%>
<jsp:include page="/extn/om/nwcg_incident_issue/detail/common_fields_incident_issue.jsp" flush="true">
<jsp:param name="ScreenType" value="detail"/>
<jsp:param name="HardCodeDocumentType" value="0001"/>
<jsp:param name="ApplicationCode" value="omd"/>
<jsp:param name="DisableEnterprise" value="Y"/>
</jsp:include>
<% } else {%>
<jsp:include page="/extn/om/nwcg_incident_issue/detail/common_fields_incident_issue.jsp" flush="true">
<jsp:param name="ScreenType" value="detail"/>
<jsp:param name="RefreshOnDocumentType" value="true"/>
<jsp:param name="RefreshOnEnterpriseCode" value="true"/>
</jsp:include>
<% } %>
<% // Now call the APIs that are dependent on the common fields (Doc Type & Enterprise Code) %>
<yfc:callAPI apiID="AP1"/>
<yfc:callAPI apiID="AP2"/>
<yfc:callAPI apiID="AP4"/>
<yfc:callAPI apiID="AP6"/>

<tr>
<% String enterpriseCodeFromCommon = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>

<td class="detaillabel" ><yfc:i18n>Buyer</yfc:i18n></td>
<td class="searchcriteriacell"  nowrap="true">
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/@BillToID")%> />
<img class="lookupicon" onclick="callLookup(this,'<%=sLookup%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Customer") %> />
</td>
<td class="detaillabel" ><yfc:i18n>Seller</yfc:i18n></td>
<% if (!isExchangeOrderCreation){ %>
<td nowrap="true" >
<input type="text" class="unprotectedinput" name="xml:/Order/@SellerOrganizationCode" value="<%=getValue("CurrentOrganization","xml:CurrentOrganization:/Organization/@PrimaryEnterpriseKey")%>">
<img class="lookupicon" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCodeFromCommon%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
</td>
<% } else { %>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@SellerOrganizationCode"/></td>
<input type="hidden" name="xml:/Order/@SellerOrganizationCode" value="<%=getValue("Order", "xml:/Order/@SellerOrganizationCode")%>"/>
<% } %>
<td class="detaillabel"><yfc:i18n>Billing_Doc</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnBillingDoc")%>/>
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Order_#</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@OrderNo")%>/>
</td>
<td class="detaillabel" ><yfc:i18n>Order_Type</yfc:i18n></td>
<td>
<select class="combobox" <%=getComboOptions("xml:/Order/@OrderType")%>>
<yfc:loopOptions binding="xml:OrderTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
value="CodeValue" isLocalized="Y"/>
</select>
</td>
<td class="detaillabel" ><yfc:i18n>Order_Date</yfc:i18n></td>
<td nowrap="true">
<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/@OrderDate")%>/>
<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
</td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Incident_Number</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentNum")%>/>
<img class="lookupicon" onclick="callLookup(this,'NWCGIncident')" name="search" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_NWCG_Incident") %>/>&nbsp;
</td>
<td class="detaillabel"><yfc:i18n>Order_Name</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@OrderName")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Phone_Number</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnPhoneNumber")%>/>
</td>
<!--<td class="detaillabel" ><yfc:i18n>Currency</yfc:i18n></td>
<td>
<select class="combobox" <%=getComboOptions("xml:/Order/PriceInfo/@Currency")%>>
<yfc:loopOptions binding="xml:/CurrencyList/@Currency" name="CurrencyDescription"
value="Currency" selected="xml:/Order/PriceInfo/@Currency" isLocalized="Y"/>
</select>
</td>-->
<% if(isExchangeOrderCreation){ %>
<td class="detaillabel" >
<yfc:i18n>Exchange_Type</yfc:i18n>
</td>
<td>
<select name="xml:/Order/@ExchangeType" class="combobox">
<yfc:loopOptions binding="xml:ExchangeTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
value="CodeValue" selected="xml:/Order/@ExchangeType" isLocalized="Y"/>
</select>
</td>
<% } %>
</tr>
<!--<td class="detaillabel"><yfc:i18n>Incident_Type</yfc:i18n></td>
<td>
<select class="combobox" <%=getComboOptions("xml:/NWCGIncidentTable/Incident/@IncidentType")%>>
<yfc:loopOptions binding="xml:CommonIncidentCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
</select>
</td>
<td class="detaillabel"><yfc:i18n>Incident_Team</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentTeam")%>/>
</td>-->
<tr>
<td class="detaillabel"><yfc:i18n>Fs_Acct_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnFsAcctCode")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Blm_Acct_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnBlmAcctCode")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Other_Acct_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnOtherAcctCode")%>/>
</td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Fs_Amount</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnFsAmount")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Blm_Amount</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnBlmAmount")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Other_Amount</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnOtherAmount")%>/>
</td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Ship_Node</yfc:i18n></td>
<td nowrap="true">
<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/@ShipNode")%>/>
<img class="lookupicon" onclick="callLookup(this,'organization','xml:/Organization/OrgRoleList/OrgRole/@RoleKey=NODE')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
</td>
</tr>
</table>
