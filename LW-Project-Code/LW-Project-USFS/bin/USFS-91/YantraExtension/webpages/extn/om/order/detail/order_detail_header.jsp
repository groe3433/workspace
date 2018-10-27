<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.nwcg.icbs.yantra.constant.common.NWCGConstants" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/extn.js"></script>
<%
String strDocType = resolveValue("xml:/Order/@DocumentType");
boolean bOtherOrder = false ;
if(strDocType != null && (!strDocType.equals("0001")))
{
	bOtherOrder = true ;
}

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
<%if (isModificationAllowed("xml:/Order/@SellerOrganizationCode","xml:/Order/AllowedModifications") && !isVoid(modifyView)) { %>
<td nowrap="true">
<input readonly type="text"   <% if (equals(sRequestDOM,"Y")) {%>  OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@SellerOrganizationCode")%>"  <%}%> <%=yfsGetTextOptions("xml:/Order/@SellerOrganizationCode", "xml:/Order/AllowedModifications")%>/>
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
<td class="detaillabel" ><yfc:i18n>Order_#</yfc:i18n></td>
<td class="protectedtext"><a <%=getDetailHrefOptions("L06", resolveValue("xml:/Order/@DocumentType"), getParameter("orderKey"),"")%> ><yfc:getXMLValue binding="xml:/Order/@OrderNo"/></a></td>

<td class="detaillabel" ><yfc:i18n>Status</yfc:i18n></td>
<td class="protectedtext">
<% if (isVoid(getValue("Order", "xml:/Order/@Status"))) {%>
	[<yfc:i18n>Draft</yfc:i18n>]
<% } else { %>
	<a <%=getDetailHrefOptions("L01", getParameter("orderKey"), "ShowReleaseNo=Y")%>> <% if(equals("Partially Shipped", getValue("Order","xml:/Order/@MaxOrderStatusDesc"))) { %> <%=displayOrderStatus("N",getValue("Order","xml:/Order/@MaxOrderStatusDesc"))%> <% } else { %> <%=displayOrderStatus(getValue("Order","xml:/Order/@MultipleStatusesExist"),getValue("Order","xml:/Order/@MaxOrderStatusDesc"))%> <% } %>
	</a>
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
<td class="detaillabel" ><yfc:i18n>Issue_Date</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@OrderDate"/></td>
</tr>
<tr>
<%if(!bOtherOrder){%>
<td class="detaillabel" ><yfc:i18n>Order_Type</yfc:i18n></td>
<td class="protectedtext"><%=getComboText("xml:OrderTypeList:/CommonCodeList/@CommonCode","CodeShortDescription","CodeValue","xml:/Order/@OrderType",true)%></td>
<%}%>
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
<% }
if(bOtherOrder){%>
<td class="detaillabel" >
<yfc:i18n>Other_Order_Number</yfc:i18n>
</td>
<%}else{%>
<td class="detaillabel" >
<yfc:i18n>Incident_Number</yfc:i18n>
</td>
<%}%>
<td class="protectedtext">
<input type="text" readonly="true" class="unprotectedinput" style="width:175px" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentNo")%>/>
<%--<img class="lookupicon" onclick="callLookup(this,'NWCGIncidentLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Incident") %> />--%>
</td>
<%if(bOtherOrder){%>
<td class="detaillabel"><yfc:i18n>Customer_PO</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnPoNo","xml:/Order/Extn/@ExtnPoNo")%>/>
</td>
<%
}
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
<tr>
<!-- other incident details -->
<%if(bOtherOrder){%>
<td class="detaillabel"><yfc:i18n>Other_Order_Name</yfc:i18n></td>
<%}else{%>
<td class="detaillabel"><yfc:i18n>Incident_Name</yfc:i18n></td>
<%}%>
<td>
<input type="text" class="unprotectedinput" readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentName")%>/>

</td>
<%if(!bOtherOrder){%>
<td class="detaillabel"><yfc:i18n>Incident_Type</yfc:i18n></td>
<td>
<!--CR 207 KS -->
<%
YFCElement commonCodeInput = YFCDocument.parse("<CommonCode CodeValue=\"" + resolveValue("xml:/Order/Extn/@ExtnIncidentType") + "\"  />").getDocumentElement();
YFCElement commonCodeTemplate = YFCDocument.parse("<CommonCodeList> <CommonCode CodeShortDescription=\"\"/> </CommonCodeList>").getDocumentElement();
%>
<yfc:callAPI apiName="getCommonCodeList" inputElement="<%=commonCodeInput%>" templateElement="<%=commonCodeTemplate%>" outputNamespace="CommonCodeList"/>
<!-- displays the value from returned API call -->
<input type="text" class="unprotectedinput" readonly="true" <%=getTextOptions("xml:CommonCodeList:/CommonCodeList/CommonCode/@CodeShortDescription")%>/>
</td>
<%}%>
<%{%>
<td class="detaillabel" ><yfc:i18n>Override_Code</yfc:i18n></td>
<td >
<input type="text" class="unprotectedinput" readonly='true' <%=getTextOptions("xml:/Order/Extn/@ExtnOverrideCode")%>/>
</td>
<%}%>
</tr>
<tr>
<%if(bOtherOrder){%>
<td class="detaillabel"><yfc:i18n>Fs_Acct_Code</yfc:i18n></td>
<%}else{%>
<td class="detaillabel"><yfc:i18n>Incident_FsAcct_Code</yfc:i18n></td>
<%}%>
<td>
<input type="text" class="unprotectedinput" readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnFsAcctCode")%>/>
</td>
<%
YFCElement elem = (YFCElement) request.getAttribute("AccountCodeCommonCodeList");
String strFundCode = "" , strObjectClass = "" ; 
if(elem != null)
{
	Iterator itr = elem.getChildren();

	if(itr != null)
	{
		while(itr.hasNext())
		{
			YFCElement child = (YFCElement) itr.next();
			String strDesc = child.getAttribute("CodeShortDescription");
			if(strDesc.equals("FUND_CODE"))
			{
				strFundCode = child.getAttribute("CodeValue");
			}
			else if(strDesc.equals("OBJECT_CLASS"))
			{
				strObjectClass = child.getAttribute("CodeValue");
			}

		}
	}
}
%>
<input type="hidden" class="unprotectedinput" size="15" <%=getTextOptions("xml:/Order/Extn/@ExtnCostCenter")%>/>
<input type="hidden" class="unprotectedinput" size="20" <%=getTextOptions("xml:/Order/Extn/@ExtnFunctionalArea")%>/>
<input type="hidden" class="unprotectedinput" size="15" <%=getTextOptions("xml:/Order/Extn/@ExtnWBS")%>/>

<%if(bOtherOrder){%>
<td class="detaillabel"><yfc:i18n>Blm_Acct_Code</yfc:i18n></td>
<%}else{%>
<td class="detaillabel"><yfc:i18n>Incident_BlmAcct_Code</yfc:i18n></td>
<%}%>
<td>
<input readonly="true" type="text" class="unprotectedinput" size="50" <%=getTextOptions("xml:/Order/Extn/@ExtnBlmAcctCode")%>/>
</td>
<%if(bOtherOrder){%>
<td class="detaillabel"><yfc:i18n>Other_Acct_Code</yfc:i18n></td>
<%}else{%>
<td class="detaillabel"><yfc:i18n>Incident_OtherAcct_Code</yfc:i18n></td>
<%}%>
<td>
<input readonly="true" type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnOtherAcctCode")%>/>
</td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Incident_Fs_Amount</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnFsAmount")%>/>
</td>

<td class="detaillabel"><yfc:i18n>Incident_Blm_Amount</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnBlmAmount")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Incident_Other_Amount</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnOtherAmount")%>/>
</td>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>Ship_Cache</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@ShipNode")%>/>
<img class="lookupicon" onclick="callLookup(this,'shipnode')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Ship_Node")%>/>
</td>
<%if(!bOtherOrder){%>
<td class="detaillabel"><yfc:i18n>Recent_Incident</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" readonly="true"  style="width:175px" <%=getTextOptions("xml:/Order/Extn/@ExtnLastIncidentNo1")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Previous_Incident</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" readonly="true"  style="width:175px" <%=getTextOptions("xml:/Order/Extn/@ExtnLastIncidentNo2")%>/>
</td>
<%}%>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Buyer</yfc:i18n></td>
<%if (isModificationAllowed("xml:/Order/@BillToID","xml:/Order/AllowedModifications") && !isVoid(modifyView)) { %>
<td nowrap="true">
<input type="text"  onblur="fetchDataFromServer(this,'getCustomerDetails',updateCustShipAddress);"  <% if (equals(sRequestDOM,"Y")) {%>  OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@BillToID")%>"  <%}%> <%=yfsGetTextOptions("xml:/Order/@BillToID", "xml:/Order/AllowedModifications")%>/>
<img class="lookupicon" onclick="callLookup(this,'NWCGCustomerLookUp')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
</td>
<% } else { %>
<td class="protectedtext">
<yfc:makeXMLInput name="OrganizationKey" >
<yfc:makeXMLKey binding="xml:/Organization/@OrganizationKey" value="xml:/Order/@BillToID" />
</yfc:makeXMLInput>
<a <%=getDetailHrefOptions("L02",getParameter("OrganizationKey"),"")%> >
<yfc:getXMLValue binding="xml:/Order/@BillToID"/>
</a>
</td>
<% } %>
<td class="detaillabel" ><yfc:i18n>Customer_Name</yfc:i18n></td>
<td>
<label id="xml:/Order/Extn/@ExtnCustomerName" class=protectedtext><%=resolveValue("xml:/Order/Extn/@ExtnCustomerName")%></label>
<input type="hidden" <%=getTextOptions("xml:/Order/Extn/@ExtnCustomerName")%>/>
</td>
<td class="detaillabel" ><yfc:i18n>Shipping_Acct_Code</yfc:i18n></td>
<td >
<input type="text" readonly="true" class="unprotectedinput" size="50" <%=getTextOptions("xml:/Order/Extn/@ExtnShipAcctCode")%>/>
</td>
</tr>
<tr>
<%if(!bOtherOrder){%>
<td class="detaillabel" ><yfc:i18n>Actual_Delivery_Date</yfc:i18n></td>
<td>
<input type="hidden" name="xml:/Order/OrderDates/OrderDate/@DateTypeId" value="NWCG_DATE"/>
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ActualDate_YFCDATE")%>/>
<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ActualDate_YFCTIME")%>/>
<img class="lookupicon" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
</td>

<td class="detaillabel" ><yfc:i18n>Estimated_Delivery_Date</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ExpectedDate_YFCDATE")%>/>
<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ExpectedDate_YFCTIME")%>/>
<img class="lookupicon" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
</td>
<td class="detaillabel" ><yfc:i18n>Requested_Delivery_Date</yfc:i18n></td>
<td>
<input type="hidden" <%=getTextOptions("xml:/Order/@ReqDeliveryDate")%>/>
<input type="text" class="unprotectedinput" onBlur="setRequestDeliverDate(this)" <%=getTextOptions("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCDATE")%>/>
<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCTIME")%>/>
<img class="lookupicon" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
</td>
<%}%>
</tr>
<tr>
<td class="detaillabel"><yfc:i18n>SA_Override_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" style="width:175px" <%=getTextOptions("xml:/Order/Extn/@ExtnSAOverrideCode")%>/>
</td>
</tr>
</table>
