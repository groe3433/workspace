<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>

<%

String sRequestDOM = request.getParameter("getRequestDOM");
String modifyView = request.getParameter("ModifyView");
modifyView = modifyView == null ? "" : modifyView;

String sHiddenDraftOrderFlag = getValue("Order", "xml:/Order/@DraftOrderFlag");
String driverDate = getValue("Order", "xml:/Order/@DriverDate");
String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("Order", "xml:/Order/@EnterpriseCode"));
extraParams += "&" + getExtraParamsForTargetBinding("xml:/Order/@OrderHeaderKey", resolveValue("xml:/Order/@OrderHeaderKey"));
extraParams += "&" + getExtraParamsForTargetBinding("IsStandaloneService", "Y");
extraParams += "&" + getExtraParamsForTargetBinding("hiddenDraftOrderFlag", sHiddenDraftOrderFlag);
%>

<script language="javascript">
// this method is used by 'Add Service Request' action on order header detail innerpanel
function callPSItemLookup()	{
yfcShowSearchPopupWithParams('','itemlookup',900,550,new Object(), 'psItemLookup', '<%=extraParams%>');
}
</script>

<table class="view" width="100%">
<yfc:makeXMLInput name="orderKey">
<yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey"/>
</yfc:makeXMLInput>
<tr>
<td>
<input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
<input type="hidden" name="xml:/Order/@ModificationReasonCode" />
<input type="hidden" name="xml:/Order/@ModificationReasonText"/>
<input type="hidden" name="xml:/Order/@Override" value="N"/>
<input type="hidden" name="hiddenDraftOrderFlag" value='<%=sHiddenDraftOrderFlag%>'/>
<input type="hidden" name="chkWOEntityKey" value='<%=getParameter("orderKey")%>'/>
<input type="hidden" name="chkCopyOrderEntityKey" value='<%=getParameter("orderKey")%>' />
<input type="hidden" name="xml:/Order/@EnterpriseCode" value='<%=getValue("Order", "xml:/Order/@EnterpriseCode")%>'/>
<input type="hidden" name="xml:/Order/@DocumentType" value='<%=getValue("Order", "xml:/Order/@DocumentType")%>'/>
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@EnterpriseCode"/></td>
<td class="detaillabel" ><yfc:i18n>Seller</yfc:i18n></td>
<input type='hidden' name='SUPPLIER' value="<%=resolveValue("xml:/Order/@SellerOrganizationCode")%>"/>
<%if (isModificationAllowed("xml:/Order/@SellerOrganizationCode","xml:/Order/AllowedModifications") && !isVoid(modifyView)) { %>
<td nowrap="true">
<input type="text"   <% if (equals(sRequestDOM,"Y")) {%>  OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@SellerOrganizationCode")%>"  <%}%> <%=yfsGetTextOptions("xml:/Order/@SellerOrganizationCode", "xml:/Order/AllowedModifications")%>/>
<img class="lookupicon" name="search" onclick="callLookupForOrder(this,'SELLER','xml:/Order/@EnterpriseCode','xml:/Order/@DocumentType')" <%=yfsGetImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization", "xml:/Order/@SellerOrganizationCode", "xml:/Order/AllowedModifications")%>/>
</td>
<% } else { %>
<td class="protectedtext">
<yfc:makeXMLInput name="OrganizationKey" >
<yfc:makeXMLKey binding="xml:/Organization/@OrganizationKey" value="xml:/Order/@SellerOrganizationCode" />
</yfc:makeXMLInput>
<a <%=getDetailHrefOptions("L03",getParameter("OrganizationKey"),"")%> >
<yfc:getXMLValue binding="xml:/Order/@SellerOrganizationCode"/>
</a>
</td>
<% } %>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>PO_Number</yfc:i18n></td>
<td class="protectedtext"><a <%=getDetailHrefOptions("L06", resolveValue("xml:/Order/@DocumentType"), getParameter("orderKey"),"")%> ><yfc:getXMLValue binding="xml:/Order/@OrderNo"/></a></td>

<td class="detaillabel" ><yfc:i18n>Status</yfc:i18n></td>
<td class="protectedtext">
<% if (isVoid(getValue("Order", "xml:/Order/@Status"))) {%>
[<yfc:i18n>Draft</yfc:i18n>]
<% } else { %>
<a <%=getDetailHrefOptions("L01", getParameter("orderKey"), "ShowReleaseNo=Y")%>><%=displayOrderStatus(getValue("Order","xml:/Order/@MultipleStatusesExist"),getValue("Order","xml:/Order/@MaxOrderStatusDesc"))%></a>
<% } %>
<% if (equals("Y", getValue("Order", "xml:/Order/@HoldFlag"))) { %>

<% if (isVoid(modifyView) || isTrue("xml:/Rules/@RuleSetValue")) {%>
<img onmouseover="this.style.cursor='default'" class="columnicon" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_order_is_held")%>>
<%	}	else	{	%>
<a <%=getDetailHrefOptions("L05", getParameter("orderKey"), "")%>><img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_order_is_held\nclick_to_add/remove_hold")%>></a>
<%	}	%>

<% } %>
<% if (equals("Y", getValue("Order", "xml:/Order/@SaleVoided"))) { %>
<img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.SALE_VOIDED, "This_sale_is_voided")%>/>
<% } %>
<% if (equals("Y", getValue("Order","xml:/Order/@isHistory") )){ %>
<img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.HISTORY_ORDER, "This_is_an_archived_order")%>/>
<% } %>
</td>
<td class="detaillabel" ><yfc:i18n>Order_Date</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@OrderDate"/></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Order Type</yfc:i18n></td>
<td class="protectedtext"><%=getComboText("xml:OrderTypeList:/CommonCodeList/@CommonCode","CodeShortDescription","CodeValue","xml:/Order/@OrderType",true)%></td>
<td class="detaillabel" ><yfc:i18n>Carrier_Service</yfc:i18n></td>
<td>
<select  <% if (isVoid(modifyView)) {%> <%=getProtectedComboOptions()%> <%}  if (equals(sRequestDOM,"Y")) {%> OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@ScacAndServiceKey")%>"  <%}%> <%=yfsGetComboOptions("xml:/Order/@ScacAndServiceKey", "xml:/Order/AllowedModifications")%>>
<yfc:loopOptions binding="xml:/ScacAndServiceList/@ScacAndService" name="ScacAndServiceDesc"
value="ScacAndServiceKey" selected="xml:/Order/@ScacAndServiceKey" isLocalized="Y"/>
</select>
</td>
<% // Show ReqDeliveryDate if that is the driver date (determined by the "DriverDate" attribute output by getOrderDetails)
// else show ReqShipDate as the driver date.
String strSDF = new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date()) ;
if (equals(driverDate, "02")) { %>
<td class="detaillabel" ><yfc:i18n>Requested_Delivery_Date</yfc:i18n></td>
<% if (!isVoid(modifyView)) {%>
<td nowrap="true">
<input type="text"   <% if (equals(sRequestDOM,"Y")) {%>  OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@ReqDeliveryDate")%>"  <%}%> <%=yfsGetTextOptions("xml:/Order/@ReqDeliveryDate","xml:/Order/@ReqDeliveryDate",strSDF, "xml:/Order/AllowedModifications")%>/>
<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar", "xml:/Order/@ReqDeliveryDate", "xml:/Order/AllowedModifications")%>/>
</td>
<% } else { %>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@ReqDeliveryDate"/><td>
<% } %>
<% } else { %>
<td class="detaillabel" ><yfc:i18n>Requested_Ship_Date</yfc:i18n></td>
<% if (!isVoid(modifyView)) {%>
<td nowrap="true">
<input type="text"   <% if (equals(sRequestDOM,"Y")) {%>  OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@ReqShipDate")%>"  <%}%> <%=yfsGetTextOptions("xml:/Order/@ReqShipDate","xml:/Order/@ReqShipDate",strSDF, "xml:/Order/AllowedModifications")%>/>
<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar", "xml:/Order/@ReqShipDate", "xml:/Order/AllowedModifications")%>/>
</td>
<% } else { %>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@ReqShipDate"/><td>
<% } %>
<% } %>
</tr>

<tr>
<%
if(equals("true", modifyView))
{
%>
<td class="detaillabel" >
<yfc:i18n>Document_Type</yfc:i18n>
</td>
<td class="protectedtext">
<yfc:getXMLValueI18NDB binding="xml:DocumentParamsList:/DocumentParamsList/DocumentParams/@Description"></yfc:getXMLValueI18NDB>
</td>
<%  }	%>

<td class="detaillabel" >
<yfc:i18n>Requisition_#</yfc:i18n>
</td>
<td class="protectedtext">
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnRequisitionNo","xml:/Order/Extn/@ExtnRequisitionNo")%>/>
</td>
<%--<td class="detaillabel" ><yfc:i18n>Priority_Code</yfc:i18n></td>
<td nowrap="true">
<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/Extn/@ExtnPriorityCode")%>/>
</td>--%>
<tr>
<td class="detaillabel"><yfc:i18n>Receiving_Cache</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@ReceivingNode")%>/>
<img class="lookupicon" onclick="callLookup(this,'shipnode')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Receiving_Node")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Shipping_Account_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnShipAcctCode")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Receiving_Account_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnRecvAcctCode")%>/>
</td>
</tr>
<tr>
<td class="detaillabel" >
<yfc:i18n>Contact_Name</yfc:i18n>
</td>
<td class="protectedtext">
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnRequisitionerName")%>/>
</td>
<td class="detaillabel" >
<yfc:i18n>Contact_Phone</yfc:i18n>
</td>
<td class="protectedtext">
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnRequisitionerNo")%>/>
</td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>PO_Shipping_Amount</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/ExtnShippingAmount")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Customer_Account_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnCustAcctCode")%>/>
</td>

<td/>
<TD class=protectedtext><label id='GSA_LABEL'><A onclick='showHideGSA();return false;' href="">Show FSS Order Details</A></label> </TD>
</tr>
<%
String exchangeType = "";
if (equals(sRequestDOM,"Y")) {
exchangeType = getValue("OrigAPIOutput", "xml:/Order/@ExchangeType");
}else{
exchangeType = getValue("Order", "xml:/Order/@ExchangeType");
}
if(!isVoid(exchangeType)){

//call master data for exchange type

// Call API to get the data for the Document Type field.
String exchangeTypeStr = "<CommonCode CodeType=\"EXCHANGE_TYPE\"/>";

YFCElement exchangeTypeInput = YFCDocument.parse(exchangeTypeStr).getDocumentElement();
YFCElement exchangeTypeTemplate = YFCDocument.parse("<CommonCodeList TotalNumberOfRecords=\"\"><CommonCode CodeType=\"\" CodeShortDescription=\"\" CodeValue=\"\" CommonCodeKey=\"\"/></CommonCodeList>").getDocumentElement();
%>

<yfc:callAPI apiName="getCommonCodeList" inputElement="<%=exchangeTypeInput%>" templateElement="<%=exchangeTypeTemplate%>" outputNamespace="ExchangeTypeList"/>

<td class="detaillabel" ><yfc:i18n>Exchange_Type</yfc:i18n></td>
<td>
<select  <% if (isVoid(modifyView)) {%> <%=getProtectedComboOptions()%> <%}  if (equals(sRequestDOM,"Y")) {%> OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@ExchangeType")%>"  <%}%> <%=yfsGetComboOptions("xml:/Order/@ExchangeType", "xml:/Order/AllowedModifications")%>>
<yfc:loopOptions binding="xml:ExchangeTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
value="CodeValue" selected="xml:/Order/@ExchangeType" isLocalized="Y"/>
</select>
</td>
<% } %>

<% if(!isVoid(getValue("Order", "xml:/Order/@ReturnOrderHeaderKeyForExchange"))){ %>
<yfc:makeXMLInput name="ReturnOHKeyForExchange">
<yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Order/ReturnOrdersForExchange/ReturnOrderForExchange/@OrderHeaderKey" />
</yfc:makeXMLInput>
<td class="detaillabel" ><yfc:i18n>Created_For_Return_#</yfc:i18n></td>
<td class="protectedtext">
<a <%=getDetailHrefOptions("L04",getParameter("ReturnOHKeyForExchange"),"")%> >
<yfc:getXMLValue binding="xml:/Order/ReturnOrdersForExchange/ReturnOrderForExchange/@OrderNo"/>
</a>
</td>
<% } %>
</tr>

<!-- GSA FEDSTRIP -->
<tr id=GSA1 style="display:none">
<td  class="detaillabel" >
<yfc:i18n>DIC</yfc:i18n>
</td>

<td class="protectedtext">
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnDocumentIdentifierCode")%>/>
</td>
<td class="detaillabel" >
<yfc:i18n>RIC</yfc:i18n>
</td>
<td class="protectedtext">
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnRoutingIdentifierCode")%>/>
</td>
<td class="detaillabel" >
<yfc:i18n>MS</yfc:i18n>
</td>
<td class="protectedtext">
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnMediaAndStatusCode")%>/>
</td>
</tr>

<tr id=GSA2 style="display:none">
<td class="detaillabel" >
<yfc:i18n>AAC</yfc:i18n>
</td>
<td class="protectedtext">
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnActivityAddressCode")%>/>
</td>
<td class="detaillabel" >
<yfc:i18n>JDATE</yfc:i18n>
</td>
<td class="protectedtext">
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnJDate")%>/>
</td>
<td class="detaillabel" >
<yfc:i18n>SUPADD</yfc:i18n>
</td>
<td class="protectedtext">
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnSupAddressCode")%>/>
</td>
</tr>

<tr id=GSA3 style="display:none">
<td class="detaillabel" >
<yfc:i18n>SIG</yfc:i18n>
</td>
<td class="protectedtext">
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnSignalCode")%>/>
</td>
<td class="detaillabel" >
<yfc:i18n>FUND</yfc:i18n>
</td>
<td class="protectedtext">
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnFundCode")%>/>
</td>
<td class="detaillabel" >
<yfc:i18n>DIST</yfc:i18n>
</td>
<td class="protectedtext">
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnDistributionCode")%>/>
</td>
</tr>

<tr id=GSA4 style="display:none">
<td class="detaillabel" >
<yfc:i18n>PRI</yfc:i18n>
</td>
<td class="protectedtext">
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnPriorityDesignatorCode")%>/>
</td>
<td class="detaillabel" >
<yfc:i18n>RDD</yfc:i18n>
</td>
<td class="protectedtext">
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnRequiredDeliveryDate")%>/>
</td>
<td class="detaillabel" >
<yfc:i18n>ADVICE</yfc:i18n>
</td>
<td class="protectedtext">
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnAdviceCode")%>/>
</td>
</tr>

<tr id=GSA5 style="display:none">
<td class="detaillabel" >
<yfc:i18n>PROJ</yfc:i18n>
</td>
<td class="protectedtext">
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnProjectCode")%>/>
</td>
</tr>
<tr id=GSA6 style="display:none">
<td class="detaillabel" >
<yfc:i18n>DRN</yfc:i18n>
</td>
<td class="protectedtext">
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnDRNCode")%>/>
</td>
</tr>
<!-- END GSA FEDSTRIP -->

</table>
<script>
var gsaRow1 = document.getElementById('GSA1') ;
var label = document.getElementById('GSA_LABEL');
function showHideGSA()
{
	var status = gsaRow1.style.display ;
	if( status == '')
	{
		for(index = 1 ; index <= 6 ; index++)
		{
			gsaRow = document.getElementById('GSA'+index) ;
			gsaRow.style.display = 'none';
		}
		label.innerHTML = "<A onclick='showHideGSA();return false;' href=''>Show FSS Order Details</A>";

	}
	else
	{
		for(index = 1 ; index <= 6 ; index++)
		{
			gsaRow = document.getElementById('GSA'+index) ;
			gsaRow.style.display = '';
		}
		label.innerHTML =  "<A onclick='showHideGSA();return false;' href=''>Hide FSS Order Details</A>";
	}
}
</script>