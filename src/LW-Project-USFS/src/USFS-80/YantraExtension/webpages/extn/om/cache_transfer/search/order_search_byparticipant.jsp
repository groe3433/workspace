<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@include file="/console/jsp/order.jspf" %>
<%@ include file="/console/jsp/paymentutils.jspf" %>
<!-- CR 566 ML This Page is completely revamped to match bystatus searching -->
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript">

function setOrderCompleteFlag(value){
var oOrderComplete = document.all("xml:/Order/@OrderComplete");
if (oOrderComplete != null)
oOrderComplete.value = value;
}

function processExchangeType(checkboxObj) {
var exchangeType = document.all("xml:/Order/@ExchangeType");
if(!checkboxObj.checked){
exchangeType.value = " ";
exchangeType.disabled = true;
exchangeType.selectedIndex = -1;
}else{
exchangeType.disabled = false;
}
}
</script>
<%
String draftOrderFlag = request.getParameter("DraftOrderFlag");
String listViewId = request.getParameter("yfcListViewGroupId");
if (isVoid(draftOrderFlag)) {
draftOrderFlag = "N";
}

String bReadFromHistory = resolveValue("xml:/Order/@ReadFromHistory");
if (isVoid(bReadFromHistory) ) {
bReadFromHistory = "N";
}
String isExchange = " ";
String orderPurpose = resolveValue("xml:/Order/@OrderPurpose");
if (equals(orderPurpose, "EXCHANGE")) {
isExchange = "Y";
}
String sOrderComplete = resolveValue("xml:/Order/@OrderComplete");
if (isVoid(sOrderComplete) && "N".equals(bReadFromHistory)   ) { // If values of radio buttons gets changed, this condition need to be revisited.
sOrderComplete = "N";
}

preparePaymentStatusList(getValue("Order", "xml:/Order/@PaymentStatus"), (YFCElement) request.getAttribute("PaymentStatusList"));

%>

<table class="view">
<tr>
<td>
<input type="hidden" name="xml:/Order/@DraftOrderFlag" value="<%=draftOrderFlag%>"/>
<input type="hidden" name="yfcListViewGroupId" value="<%=listViewId%>"/>
<input type="hidden" name="xml:/Order/OrderHoldType/@Status" value=""/>
<input type="hidden" name="xml:/Order/OrderHoldType/@StatusQryType" value="" />

</td>
</tr>

<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
<jsp:param name="ScreenType" value="search"/>
<jsp:param name="RefreshOnDocumentType" value="true"/>
<jsp:param name="RefreshOnEnterpriseCode" value="true"/>
<jsp:param name="HardCodeDocumentType" value="0006"/>
</jsp:include>

<yfc:callAPI apiID="AP2"/>
<yfc:callAPI apiID="AP4"/>
<yfc:callAPI apiID="AP5"/>
<%
if(!isTrue("xml:/Rules/@RuleSetValue") )	{
%>
<yfc:callAPI apiID="AP7"/>
<%}%>

<!-- ROW START -->
<tr>
<td class="searchlabel" >
<yfc:i18n>Incident_Number</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Order/Extn/@ExtnIncidentNumQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/Extn/@ExtnIncidentNumQryType"/>
</select>
<input type="text" size=25 class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentNo")%>/>
<img class="lookupicon" onclick="callIncidentLookup('xml:/Order/Extn/@ExtnIncidentNo','xml:/Order/Extn/@ExtnIncidentYear','NWCGIncidentLookup')" name="search" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Incident") %>/>&nbsp;
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Issue_#</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Order/@OrderNoQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/@OrderNoQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@OrderNo")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Year</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentYear")%>/>
</td>
</tr>

<!-- ShipNode -->
<tr>
<td class="searchlabel" >
<yfc:i18n>Shipping_Cache</yfc:i18n>
</td>
</tr>

<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Order/@ShipNodeQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/@ShipNodeNumQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@ShipNode","xml:/Order/@ShipNode","xml:CurrentUser:/User/@Node")%>/>
<img class="lookupicon" onclick="callLookup(this,'organization','xml:/Organization/OrgRoleList/OrgRole/@RoleKey=NODE')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
</td>
</tr>
<!-- ReceivingNode -->
<tr>
<td class="searchlabel" >
<yfc:i18n>Receiving_Cache</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Order/@ReceivingNodeQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/@ReceivingNodeNumQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@ReceivingNode")%>/>
<img class="lookupicon" onclick="callLookup(this,'organization','xml:/Organization/OrgRoleList/OrgRole/@RoleKey=NODE')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Issue_#</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Order/@OrderNoQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/@OrderNoQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@OrderNo")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Shipping_AC_Code</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Order/Extn/@ExtnShipAcctCodeQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/Extn/@ExtnShipAcctCodeQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnShipAcctCode")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Receiving_AC_Code</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Order/Extn/@ExtnRecvAcctCodeQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/Extn/@ExtnRecvAcctCodeQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnRecvAcctCode")%>/>
</td>
</tr>
<!--<tr>
<td class="searchlabel" >
<yfc:i18n>Buyer</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Order/@BuyerOrganizationCodeQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/@BuyerOrganizationCodeQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@BuyerOrganizationCode")%>/>
<% String enterpriseCode = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
<img class="lookupicon" name="search" onclick="callLookupForOrder(this,'BUYER','<%=enterpriseCode%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Seller</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Order/@SellerOrganizationCodeQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/@SellerOrganizationCodeQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@SellerOrganizationCode")%>/>
<img class="lookupicon" name="search" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCode%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization") %> />
</td>
</tr>
<tr>
<td class="searchcriteriacell">
<input type="checkbox" <%=getCheckBoxOptions("xml:/Order/@OrderPurpose", "EXCHANGE", "xml:/Order/@OrderPurpose")%> onClick="processExchangeType(this)" yfcCheckedValue='EXCHANGE' yfcUnCheckedValue=' '> <yfc:i18n>Exchange_Order_With_Type</yfc:i18n></input>&nbsp;
</td>
</tr>
<tr>
<td class="searchcriteriacell">
<select name="xml:/Order/@ExchangeType" class="combobox"  <% if(!equals(isExchange, "Y")){ %> disabled="true" <% } %> >
<yfc:loopOptions binding="xml:ExchangeTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
value="CodeValue" selected="xml:/Order/@ExchangeType" isLocalized="Y"/>
</select>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Buyer_Account_#</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Order/PaymentMethod/@CustomerAccountNoQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/PaymentMethod/@CustomerAccountNoQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/PaymentMethod/@CustomerAccountNo")%>/>
</td>
</tr>-->
</table>

