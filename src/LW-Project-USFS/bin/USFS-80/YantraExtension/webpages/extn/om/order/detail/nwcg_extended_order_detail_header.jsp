<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.nwcg.icbs.yantra.constant.common.NWCGConstants" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.StringUtil" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>

<script language="javascript" src="/yantra/console/scripts/dm.js"></script>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/console/scripts/extn.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_orderDetails.js"></script>
<%
String strDocType = resolveValue("xml:/Order/@DocumentType");
boolean bOtherOrder = false ;
if(strDocType != null && (!strDocType.equals(NWCGConstants.ORDER_DOCUMENT_TYPE)))
{
	bOtherOrder = true ;
}

String dateActual = resolveValue("xml:/Order/OrderDates/OrderDate/@ActualDate");
boolean bDateActual = false ;
if(dateActual == null || (dateActual.equals(NWCGConstants.EMPTY_STRING)))
{
	bDateActual = true ;
}
String dateExpected = resolveValue("xml:/Order/OrderDates/OrderDate/@ExpectedDate");
boolean bDateExpected = false ;
if(dateExpected == null || (dateExpected.equals(NWCGConstants.EMPTY_STRING)))
{
	bDateExpected = true ;
}

String sRequestDOM = request.getParameter("getRequestDOM");
String modifyView = request.getParameter("ModifyView");
modifyView = modifyView == null ? NWCGConstants.EMPTY_STRING : modifyView;

String sHiddenDraftOrderFlag = getValue("Order", "xml:/Order/@DraftOrderFlag");
String driverDate = getValue("Order", "xml:/Order/@DriverDate");
String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("Order", "xml:/Order/@EnterpriseCode"));
extraParams += "&" + getExtraParamsForTargetBinding("xml:/Order/@OrderHeaderKey", resolveValue("xml:/Order/@OrderHeaderKey"));
extraParams += "&" + getExtraParamsForTargetBinding("IsStandaloneService", NWCGConstants.YES);
extraParams += "&" + getExtraParamsForTargetBinding("hiddenDraftOrderFlag", sHiddenDraftOrderFlag);

//CR 21 - Copy ICBSR Account Codes from Incident to Issue/Order on page load
String blmAcctCode = resolveValue("xml:/Order/Extn/@ExtnBlmAcctCode");
String fsAccountCode = resolveValue("xml:/Order/Extn/@ExtnFsAcctCode");
String overrideCode = resolveValue("xml:/Order/Extn/@ExtnOverrideCode");
String otherAcctCode = resolveValue("xml:/Order/Extn/@ExtnOtherAcctCode");
String shipAcctCode = resolveValue("xml:/Order/Extn/@ExtnShipAcctCode");
String shipAcctOverrideCode = resolveValue("xml:/Order/Extn/@ExtnSAOverrideCode");
String holdFlag = resolveValue("xml:/Order/@HoldFlag");
String extnNavInfo = resolveValue("xml:/Order/Extn/@ExtnNavInfo");
String onWillPickUpTitleTxt = "The Requested Delivery Date is set in the Will Pick Up box below for the Will Pick Up Shipping Method";

String costCenter = resolveValue("xml:/Order/Extn/@ExtnCostCenter");
String functionalArea = resolveValue("xml:/Order/Extn/@ExtnFunctionalArea");
String wbs = resolveValue("xml:/Order/Extn/@ExtnWBS");

boolean hasValidFS = false;
boolean hasValidBLM = false;
boolean hasValidOther = false;
if (StringUtil.isEmpty(fsAccountCode)) {
	hasValidFS = false;
}
else 
	hasValidFS = true;

if ((StringUtil.isEmpty(blmAcctCode) || blmAcctCode.length() < 5)) {
	hasValidBLM = false;	
}
else {
	hasValidBLM = true;
}

if (StringUtil.isEmpty(otherAcctCode) || StringUtil.isEmpty(overrideCode)) {
	hasValidOther = false;
}
else {
	hasValidOther = true;
}

if (!hasValidFS && !hasValidBLM && !hasValidOther) 
	{
%>
	<yfc:callAPI apiID="A21"/>
<% 
	}

	String temp = getValue("icbsCodes","xml:/Order/Extn/@ExtnBlmAcctCode");
	blmAcctCode = StringUtil.isEmpty(temp) ? blmAcctCode : new String(temp);
	
	temp = getValue("icbsCodes","xml:/Order/Extn/@ExtnFsAcctCode");
	fsAccountCode = StringUtil.isEmpty(temp) ? fsAccountCode : new String(temp);
	
	temp = getValue("icbsCodes","xml:/Order/Extn/@ExtnOverrideCode");
	overrideCode = StringUtil.isEmpty(temp) ? overrideCode : new String(temp);
	
	temp = getValue("icbsCodes","xml:/Order/Extn/@ExtnOtherAcctCode");
	otherAcctCode = StringUtil.isEmpty(temp) ? otherAcctCode : new String(temp);
	
	temp = getValue("icbsCodes","xml:/Order/Extn/@ExtnShipAcctCode");
	shipAcctCode = StringUtil.isEmpty(temp) ? shipAcctCode : new String(temp);
	
	temp = getValue("icbsCodes","xml:/Order/Extn/@ExtnSAOverrideCode");
	shipAcctOverrideCode = StringUtil.isEmpty(temp) ? shipAcctOverrideCode : new String(temp);

	temp = getValue("icbsCodes","xml:/Order/@HoldFlag");
	holdFlag = StringUtil.isEmpty(temp) ? holdFlag : new String(temp);
	
	temp = getValue("icbsCodes","xml:/Order/Extn/@ExtnCostCenter");
	costCenter = StringUtil.isEmpty(temp) ? costCenter : new String(temp);
	
	temp = getValue("icbsCodes","xml:/Order/Extn/@ExtnFunctionalArea");
	functionalArea = StringUtil.isEmpty(temp) ? functionalArea : new String(temp);
	
	temp = getValue("icbsCodes", "xml:/Order/Extn/@ExtnWBS");
	wbs = StringUtil.isEmpty(temp) ? wbs : new String(temp);	
%>

<script language="javascript">
// this method is used by 'Add Service Request' action on order header detail innerpanel
function callPSItemLookup()	{
	yfcShowSearchPopupWithParams('','itemlookup',900,550,new Object(), 'psItemLookup', '<%=extraParams%>');
}
</script>

<%	
YFCElement curUsr = (YFCElement)session.getAttribute("CurrentUser"); 
String UserNode = curUsr.getAttribute("Node");
YFCElement organizationInput = null;
organizationInput = YFCDocument.parse("<Organization OrganizationCode=\"" + UserNode + "\" />").getDocumentElement();

YFCElement organizationTemplate = YFCDocument.parse("<OrganizationList> <Organization OrganizationCode=\"\" > <Extn ExtnOwnerAgency=\"\" /> </Organization> </OrganizationList>").getDocumentElement();
%>

<yfc:callAPI apiName="getOrganizationList" inputElement="<%=organizationInput%>" templateElement="<%=organizationTemplate%>" outputNamespace="OrganizationList"/>

<table class="view" width="100%">
<yfc:makeXMLInput name="orderKey">
<yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey"/>
</yfc:makeXMLInput>
<tr>
<td>
	<yfc:makeXMLInput name="IssuePrintKey">
		 <yfc:makeXMLKey binding="xml:/Print/IOrder/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey" />
	</yfc:makeXMLInput>
	<input type="hidden" value='<%=getParameter("IssuePrintKey")%>' name="PrintEntityKey"/>

	<input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
	<input type="hidden" name="xml:/Order/@ModificationReasonCode" />
	<input type="hidden" name="xml:/Order/@ModificationReasonText"/>
	<input type="hidden" name="xml:/Order/@Override" value="N"/>
	<input type="hidden" name="hiddenDraftOrderFlag" value='<%=sHiddenDraftOrderFlag%>'/>
	<input type="hidden" name="chkWOEntityKey" value='<%=getParameter("orderKey")%>'/>
	<!-- <input type="hidden" name="EntityKey" value='<%=getParameter("orderKey")%>'/> CR 503 jk -->
	<input type="hidden" name="chkCopyOrderEntityKey" value='<%=getParameter("orderKey")%>' />
	<input type="hidden" name="xml:/Order/@EnterpriseCode" value='<%=getValue("Order", "xml:/Order/@EnterpriseCode")%>'/>
	<input type="hidden" name="xml:/Order/@DocumentType" value='<%=getValue("Order", "xml:/Order/@DocumentType")%>' id="DocumentType"/>
	<input type="hidden" name="xml:/Extn/@OwnerAgency" value='<%=resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnOwnerAgency")%>'/>
	<input type="hidden" name="xml:/Order/@OwnerAgency" value='<%=resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnOwnerAgency")%>'/>
	<input type="hidden" <%=getTextOptions("xml:/Order/Extn/@ExtnAcctCode1")%> value="" />
	<input type="hidden" <%=getTextOptions("xml:/Order/Extn/@ExtnOverrideCode1")%> value="" />
	<input type="hidden" name="xml:/Order/@OrderHeaderKey" value='<%=resolveValue("xml:/Order/@OrderHeaderKey")%>' id="OrderHeaderKey"/>
    <input type="hidden" name="xml:/Order/Extn/@ExtnYear" value='<%=resolveValue("xml:/Order/Extn/@ExtnIncidentYear")%>'/>
	<input type="hidden" name="xml:/Order/@Status" value='<%=resolveValue("xml:/Order/@Status")%>'/>
    
<%
	String extnIncidentYear = resolveValue("xml:/Order/Extn/@ExtnIncidentYear");
	String extnIncidentNo = resolveValue("xml:/Order/Extn/@ExtnIncidentNo");
%>
	<input type="hidden" name="xml:/Shipment/Extn/@ExtnYear" value='<%=extnIncidentYear%>'/>
	<input type="hidden" name="xml:/Shipment/Extn/@ExtnIncidentNum" value='<%=extnIncidentNo%>'/>
	<input type="hidden" name="xml:/Order/@HoldFlag" value='<%=holdFlag%>'/>
</td>
</tr>

<tr>
	<td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
	<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@EnterpriseCode"/></td>

	<td class="detaillabel" ><yfc:i18n>Seller</yfc:i18n></td>
	<%if (isModificationAllowed("xml:/Order/@SellerOrganizationCode","xml:/Order/AllowedModifications") && !isVoid(modifyView)) { %>
		<td nowrap="true">
		<input readonly type="text" <% if(equals(sRequestDOM,"Y")) {%>  OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@SellerOrganizationCode")%>" <%}%> <%=yfsGetTextOptions("xml:/Order/@SellerOrganizationCode", "xml:/Order/AllowedModifications")%>/>
		</td>
	<% } else { %>
		<td class="protectedtext">
		<yfc:makeXMLInput name="OrganizationKey" >
		<yfc:makeXMLKey binding="xml:/Organization/@OrganizationKey" value="xml:/Order/@SellerOrganizationCode" />
		</yfc:makeXMLInput>
		<a <%=getDetailHrefOptions("L03",getParameter("OrganizationKey"),NWCGConstants.EMPTY_STRING)%> >
		<yfc:getXMLValue binding="xml:/Order/@SellerOrganizationCode"/>
		</a>
		</td>
	<% } %>
	<td class="detaillabel" ><yfc:i18n>Incident_Year</yfc:i18n></td>
	<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/Extn/@ExtnIncidentYear"/></td>
</tr>

<tr>
	<td class="detaillabel" ><yfc:i18n>Order_#</yfc:i18n></td>
	<td class="protectedtext"><a <%=getDetailHrefOptions("L06", resolveValue("xml:/Order/@DocumentType"), getParameter("orderKey"),NWCGConstants.EMPTY_STRING)%> ><yfc:getXMLValue binding="xml:/Order/@OrderNo"/></a></td>

	<td class="detaillabel" ><yfc:i18n>Status</yfc:i18n></td>
	<td class="protectedtext">
		<% if (isVoid(getValue("Order", "xml:/Order/@Status"))) {%>
			[<yfc:i18n>Draft</yfc:i18n>]
		<% } else { %>
			<a <%=getDetailHrefOptions("L01", getParameter("orderKey"), "ShowReleaseNo=Y")%>> <% if(equals("Partially Shipped", getValue("Order","xml:/Order/@MaxOrderStatusDesc"))) { %> <%=displayOrderStatus("N",getValue("Order","xml:/Order/@MaxOrderStatusDesc"))%> <% } else { %> <%=displayOrderStatus(getValue("Order","xml:/Order/@MultipleStatusesExist"),getValue("Order","xml:/Order/@MaxOrderStatusDesc"))%> <% } %>
			</a>
		<% } %>

		<% if (equals("Y", holdFlag)) { %>
			<% if (isVoid(modifyView) || isTrue("xml:/Rules/@RuleSetValue")) {%>
				<img onmouseover="this.style.cursor='default'" class="columnicon" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_order_is_held")%>>
			<%} else {%>
				<a <%=getDetailHrefOptions("L05", getParameter("orderKey"), NWCGConstants.EMPTY_STRING)%>><img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_order_is_held\nclick_to_add/remove_hold")%>></a>
			<% } %>
			
			<%
			java.util.ArrayList list = getLoopingElementList("xml:/Order/OrderHoldTypes/@OrderHoldType");
			boolean processHoldCheck = true;
			for(int i=0; i < list.size(); i++) {
			    com.yantra.yfc.dom.YFCElement elem = (com.yantra.yfc.dom.YFCElement)list.get(i);
			    String holdType = elem.getAttribute("HoldType");
			    if (holdType.equalsIgnoreCase("NULL_PRIM_FIN_CODE")){
			    	
					%>
					<input type="hidden" name="HoldType" value=<%=holdType%> id="HoldType"/>
					<yfc:callAPI apiID="A17"/>
				<%
			    }
			    if (holdType.equalsIgnoreCase("INCIDENT_INACTIVE")) {

			%>
				<input type="hidden" name="HoldType" value=<%=holdType%> id="HoldType"/>
				<yfc:callAPI apiID="A19"/>
			<%	
				}//if holdtype is INCIDENT_INACTIVE
			}//for loop
			%>
			
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
	<% String strOrderType = getComboText("xml:OrderTypeList:/CommonCodeList/@CommonCode","CodeShortDescription","CodeValue","xml:/Order/@OrderType",true);
	
	   if(!bOtherOrder) {
	%>
		<td class="detaillabel" ><yfc:i18n>Order_Type</yfc:i18n></td>
		<td class="protectedtext"><%=strOrderType%></td>
	<%	}
	%>
	<td class="detaillabel" ><yfc:i18n>Carrier_Service</yfc:i18n></td>
	<td>
		<select  <% if(isVoid(modifyView)) {%> <%=getProtectedComboOptions()%> <%} if(equals(sRequestDOM,"Y")) {%> OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@ScacAndServiceKey")%>" <%}%> <%=yfsGetComboOptions("xml:/Order/@ScacAndServiceKey", "xml:/Order/AllowedModifications")%>>
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
				<input type="text"  <% if(equals(sRequestDOM,"Y")) {%>  OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@ReqDeliveryDate")%>"  <%}%> <%=yfsGetTextOptions("xml:/Order/@ReqDeliveryDate","xml:/Order/@ReqDeliveryDate",strSDF, "xml:/Order/AllowedModifications")%>/>
				<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar", "xml:/Order/@ReqDeliveryDate", "xml:/Order/AllowedModifications")%>/>
			</td>
		<% } else { %>
			<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@ReqDeliveryDate"/><td>
		<% } %>
	<% } else { %>
		<td class="detaillabel" ><yfc:i18n>Requested_Ship_Date</yfc:i18n></td>
		<% if (!isVoid(modifyView)) {%>
			<td nowrap="true">
				<input type="text" <% if (equals(sRequestDOM,"Y")) {%>  OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@ReqShipDate")%>"  <%}%> <%=yfsGetTextOptions("xml:/Order/@ReqShipDate","xml:/Order/@ReqShipDate",strSDF, "xml:/Order/AllowedModifications")%>/>
				<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar", "xml:/Order/@ReqShipDate", "xml:/Order/AllowedModifications")%>/>
			</td>
		<% } else { %>
			<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@ReqShipDate"/><td>
		<% } %>
	<% } %>
	<%if(bOtherOrder){%>
		<td class="detaillabel"><yfc:i18n>Customer_PO</yfc:i18n></td>
		<td>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnPoNo","xml:/Order/Extn/@ExtnPoNo")%>/>
		</td>
	<% } else { %>
		<input type="hidden" class="unprotectedinput" readonly="true"  <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentYear","xml:/Order/Extn/@ExtnIncidentYear")%>/>
	<%}
	String exchangeType = NWCGConstants.EMPTY_STRING;
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
	
</tr>

<tr>
	<% if(equals("true", modifyView)) { %>
		<td class="detaillabel" ><yfc:i18n>Document_Type</yfc:i18n></td>
		<td class="protectedtext">
		<yfc:getXMLValueI18NDB binding="xml:DocumentParamsList:/DocumentParamsList/DocumentParams/@Description"></yfc:getXMLValueI18NDB>
		</td>
	<% }
	if(bOtherOrder){%>
		<td class="detaillabel" ><yfc:i18n>Other_Order_Number</yfc:i18n></td>
	<%}else{%>
		<td class="detaillabel" ><yfc:i18n>Incident_No</yfc:i18n></td>
	<%}%>
	<td class="protectedtext">
		<input type="text" readonly="true" class="protectedinput" style="width:175px" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentNo")%>/>
		<%--<img class="lookupicon" onclick="callLookup(this,'NWCGIncidentLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Incident") %> />--%>
	</td>

	<% if(!isVoid(getValue("Order", "xml:/Order/@ReturnOrderHeaderKeyForExchange"))){ %>
		<yfc:makeXMLInput name="ReturnOHKeyForExchange">
		<yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Order/ReturnOrdersForExchange/ReturnOrderForExchange/@OrderHeaderKey" />
		</yfc:makeXMLInput>
		<td class="detaillabel" ><yfc:i18n>Created_For_Return_#</yfc:i18n></td>
		<td class="protectedtext">
		<a <%=getDetailHrefOptions("L04",getParameter("ReturnOHKeyForExchange"),NWCGConstants.EMPTY_STRING)%> >
		<yfc:getXMLValue binding="xml:/Order/ReturnOrdersForExchange/ReturnOrderForExchange/@OrderNo"/>
		</a>
		</td>
	<% } %>
	<td>&nbsp;</td>
	<td>&nbsp;</td>

</tr>

<tr>
	<%if(!bOtherOrder){%>
		<td class="detaillabel"><yfc:i18n>Incident_Type</yfc:i18n></td>
		<td>
			<%
			//input element for API call
			YFCElement commonCodeInput = YFCDocument.parse("<CommonCode CodeValue=\"" + resolveValue("xml:/Order/Extn/@ExtnIncidentType") + "\" />").getDocumentElement();
			//templement element for API call
			YFCElement commonCodeTemplate = YFCDocument.parse("<CommonCodeList> <CommonCode CodeShortDescription=\"\"/> </CommonCodeList>").getDocumentElement();%>

			<yfc:callAPI apiName="getCommonCodeList" inputElement="<%=commonCodeInput%>" templateElement="<%=commonCodeTemplate%>" outputNamespace="CommonCodeList"/>
			<input type="text" class="protectedinput" readonly="true" <%=getTextOptions("xml:/CommonCodeList/CommonCode/@CodeShortDescription")%> />
		</td>
	<%}%>

	<%
	YFCElement elem = (YFCElement) request.getAttribute("AccountCodeCommonCodeList");
	String strFundCode = NWCGConstants.EMPTY_STRING , strObjectClass = NWCGConstants.EMPTY_STRING ; 
	if(elem != null) {
		Iterator itr = elem.getChildren();
		if(itr != null) {
			while(itr.hasNext())
			{
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
	
	<%if(bOtherOrder){%>
		<td class="detaillabel"><yfc:i18n>Other_Order_Name</yfc:i18n></td>
	<%}else{%>
		<td class="detaillabel"><yfc:i18n>Incident_Name</yfc:i18n></td>
	<%}%>
	<td colspan="2">
		<input type="text" class="protectedinput" readonly="true" size="70" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentName")%>/>
	</td>
	
	<td>&nbsp;</td>
	<td>&nbsp;</td>

	</tr>
	<tr>
	<%if(bOtherOrder){%>
		<td tabindex="-1" class="detaillabel"><yfc:i18n>Blm_Acct_Code</yfc:i18n></td>
	<%}else{%>
		<td tabindex="-1" class="detaillabel"><yfc:i18n>Incident_BlmAcct_Code</yfc:i18n></td>
	<%}%>

	<td>
		<input size="50" type="text" class="protectedinput" readonly="true" <%=yfsGetTextOptions("xml:/Order/Extn/@ExtnBlmAcctCode", blmAcctCode ,"xml:/Order/AllowedModifications")%> />		
	</td>
	<%if(bOtherOrder){%>
		<td class="detaillabel"><yfc:i18n>Fs_Acct_Code</yfc:i18n></td>
	<%}else{%>
		<td class="detaillabel"><yfc:i18n>Incident_FsAcct_Code</yfc:i18n></td>
	<%}%>
	<td>
		<input type="text" size="50" <%=yfsGetTextOptions("xml:/Order/Extn/@ExtnFsAcctCode", fsAccountCode ,"xml:/Order/AllowedModifications")%>/>
	</td>

	<td class="detaillabel" ><yfc:i18n>Override_Code</yfc:i18n></td>
	<td >
		<input type="text" maxLength=4 onBlur="javascript:if(document.getElementById('xml:/Order/Extn/@ExtnFsAcctCode').value!=''){validateOverrideCodeLength(this);populateShippingAndSACode(this);}" <%=yfsGetTextOptions("xml:/Order/Extn/@ExtnOverrideCode", overrideCode,"xml:/Order/AllowedModifications")%>/>
	</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
</tr>

<tr>
	<%if(bOtherOrder){%>
		<td class="detaillabel"><yfc:i18n>Other_Acct_Code</yfc:i18n></td>
	<%}else{%>
		<td class="detaillabel"><yfc:i18n>Incident_OtherAcct_Code</yfc:i18n></td>
	<%}%>
	<td>
		<input type="text" size="50" onBlur="populateShippingAndSACode(this)"  <%=yfsGetTextOptions("xml:/Order/Extn/@ExtnOtherAcctCode", otherAcctCode ,"xml:/Order/AllowedModifications")%>/>
	</td>
	<td class="detaillabel" ><yfc:i18n>Shipping_Acct_Code</yfc:i18n></td>
	<td >
		<input type="text" class="unprotectedinput" size="50" maxLength="50" <%=yfsGetTextOptions("xml:/Order/Extn/@ExtnShipAcctCode", shipAcctCode ,"xml:/Order/AllowedModifications")%>/>
	</td>
	<td class="detaillabel"><yfc:i18n>SA_Override_Code</yfc:i18n></td>
	<td>
		<input type="text" class="unprotectedinput" size="20" maxLength="50" readonly="true" <%=yfsGetTextOptions("xml:/Order/Extn/@ExtnSAOverrideCode", shipAcctOverrideCode, "xml:/Order/AllowedModifications")%>/>
	</td>
</tr>
</table>

<!-- FBMS elements -->
<table class="view" width="100%">
<tbody>
<tr>	
	<td/>
	<td><FIELDSET><LEGEND><B>FBMS</B></LEGEND>
	<table align="center">		
	<tbody>
	<tr>
		<td align="right" class="detaillabel"><yfc:i18n>Cost_Center</yfc:i18n></td>
		<td alignt="left"><input type="text" size="30" maxLength="10" onBlur="checkExtnCostCenter()" <%=yfsGetTextOptions("xml:/Order/Extn/@ExtnCostCenter", costCenter,"xml:/Order/AllowedModifications")%>/></td>
		<td align="right" class="detaillabel"><yfc:i18n>Functional_Area</yfc:i18n></td>
		<td alignt="left"><input type="text" size="30" maxLength="16" onBlur="checkExtnFunctionalArea()" <%=yfsGetTextOptions("xml:/Order/Extn/@ExtnFunctionalArea", functionalArea,"xml:/Order/AllowedModifications")%>/></td>
		<td align="right" class="detaillabel"><yfc:i18n>Work_Breakdown_Structure</yfc:i18n></td>
		<td alignt="left"><input type="text" size="30" maxLength="12" onBlur="checkExtnWBS()" <%=yfsGetTextOptions("xml:/Order/Extn/@ExtnWBS", wbs,"xml:/Order/AllowedModifications")%>/></td>				
	</tr>
	</tbody>
	</table>
	</FIELDSET>
	</td>
	<td>&nbsp;</td>
</tr>
</tbody>
</table>
<!-- FBMS elements -->

<table class="view" width="100%">
<tbody>
<tr>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td class="detaillabel"><yfc:i18n>Ship_Cache</yfc:i18n></td>
	<td>
		<!-- 2.1 Enhancement READ ONLY SHIP CACHE ID -->
		<input type="text" class="protectedinput" readonly="true" <%=getTextOptions("xml:/Order/@ShipNode")%>/>
		<!-- END 2.1 Enhancement READ ONLY SHIP CACHE ID -->
	</td>
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
			<a <%=getDetailHrefOptions("L02",getParameter("OrganizationKey"),NWCGConstants.EMPTY_STRING)%> >
			<yfc:getXMLValue binding="xml:/Order/@BillToID"/>
			</a>
		</td>
	<% } %>
</tr>

<tr>
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
	<td class="detaillabel" ><yfc:i18n>Customer_Name</yfc:i18n></td>
	<td>
		<label id="xml:/Order/Extn/@ExtnCustomerName" class=protectedtext><%=resolveValue("xml:/Order/Extn/@ExtnCustomerName")%></label>
		<input type="hidden" <%=getTextOptions("xml:/Order/Extn/@ExtnCustomerName")%>/>
	</td>
</tr>

<tr>
</tr>

<tr>
	<%if(!bOtherOrder){%>
		<td class="detaillabel" ><yfc:i18n>Actual_Delivery_Date</yfc:i18n></td>
		<td>
			<input type="hidden" name="xml:/Order/OrderDates/OrderDate/@DateTypeId" value="NWCG_DATE"/>
			<%if(bDateActual){%>
				<!-- TOP of CR 578 User Locale Consideration -->
				<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ActualDate_YFCDATE", getTodayDate())%>/>
				<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
				<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ActualDate_YFCTIME", getCurrentTime())%>/>
				<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %>/>
				<!-- BOTTOM of CR 578 User Locale Consideration -->
			<%} else {%>
			        <input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ActualDate_YFCDATE")%>/>
			        <img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
			        <input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ActualDate_YFCTIME")%>/>
			        <img class="lookupicon" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
			<%}%>
		</td>

		<td class="detaillabel" ><yfc:i18n>Estimated_Delivery_Date</yfc:i18n></td>
		<td>
			<%if(bDateExpected){%>
				<!-- TOP of CR 578 User Locale Consideration -->
				<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ExpectedDate_YFCDATE", getTodayDate())%>/>
				<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
				<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ExpectedDate_YFCTIME", getCurrentTime())%>/>
				<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %>/>
				<!-- BOTTOM of CR 578 User Locale Consideration -->
			<%} else {%>
			        <input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ExpectedDate_YFCDATE")%>/>
			        <img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
			        <input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ExpectedDate_YFCTIME")%>/>
			        <img class="lookupicon" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
			<%}%>
		</td>
		<td class="detaillabel" ><yfc:i18n>Requested_Delivery_Date</yfc:i18n></td>
		<td>
			<% if (extnNavInfo != null && extnNavInfo.equalsIgnoreCase("WILL_PICK_UP")) { %>
				<input size=8 dataType="DATE" type="text" title="<%=onWillPickUpTitleTxt%>" disabled='disabled' class="unprotectedinput" value="<%=resolveValue("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCDATE")%>"/>
				<img class="lookupicon" disabled='disabled' <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
				<input size=8 dataType="TIME" type="text" title="<%=onWillPickUpTitleTxt%>" disabled='disabled' class="unprotectedinput" value="<%=resolveValue("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCTIME")%>"/>
				<img class="lookupicon" disabled='disabled' <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
			<% } else { %>
				<input type="hidden" <%=getTextOptions("xml:/Order/@ReqDeliveryDate")%>/>
				<input type="text" class="unprotectedinput" onBlur="setRequestDeliverDate(this)" <%=getTextOptions("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCDATE")%>/>
				<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
				<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCTIME")%>/>
				<img class="lookupicon" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
			<% }%>
		</td>
	<%}%>
</tr>

<tr>
	<% String OrderStatus = resolveValue("xml:/Order/@Status"); 
	if( (bOtherOrder) && !OrderStatus.equals("Draft Order Created") && !OrderStatus.equals(NWCGConstants.EMPTY_STRING) && !OrderStatus.equals("Cancelled"))
	{%>
		<td class=protectedtext><label id='SPLIT_LABEL'><A onclick='yfcShowDetailPopup("ISUNWOSPL010",
		" ","900","300"," ");return false;' href="">Split Account Codes</A></label> 
		</td> 
		<% String AcctCode2 = NWCGConstants.EMPTY_STRING;
		AcctCode2 = resolveValue("xml:/Order/Extn/@ExtnAcctCode2");

		if (!AcctCode2.equals(NWCGConstants.EMPTY_STRING)) { %>
			<td>
				<input type="checkbox" disabled="true"
				<%=getCheckBoxOptions("xml:/Extn/@CBox","Y","xml:/Extn/@CBox")%> />
			</td>
		<%}%>
	<%}%>

	<%if (strOrderType.equals("Refurbishment")){%>
		<td class="detaillabel" ><yfc:i18n>Master_Work_Order_No</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/Extn/@ExtnMasterWorkOrderNo"/></td>
	<%}%>
</tr>
<tr>
	<%if(!bOtherOrder) {%>
	<td class="detaillabel"><yfc:i18n>Shipping_Contact_Name</yfc:i18n></td>
	<td>
		<input type="text" class="unprotectedinput" style="width:175px" MaxLength=40 <%=getTextOptions("xml:/Order/Extn/@ExtnShippingContactName")%>/>
	</td>
	<td class="detaillabel"><yfc:i18n>Shipping_Contact_Phone</yfc:i18n></td>
	<td>
		<input type="text" class="unprotectedinput" style="width:175px" MaxLength=356 <%=getTextOptions("xml:/Order/Extn/@ExtnShippingContactPhone")%>/>
	</td>
	<%} %>
	<td class="detaillabel" ><yfc:i18n>Shipping_Method</yfc:i18n></td>
	<td>
		<select class="combobox" onchange="confirm_shipMethodChange()"
				<% if(isVoid(modifyView)) {%> 
					<%=getProtectedComboOptions()%> 
				<%}%> 
					<%=yfsGetComboOptions("xml:/Order/Extn/@ExtnNavInfo", "xml:/Order/Extn/@ExtnNavInfo", "xml:/Order/AllowedModifications")%>>
				
				<yfc:loopOptions binding="xml:ShippingMethods:/CommonCodeList/@CommonCode" name="CodeShortDescription"
				value="CodeValue" selected="xml:/Order/Extn/@ExtnNavInfo" isLocalized="Y"/>
		</select>
	</td>
</tr>	
<%if(equals(strDocType, "0001")){%>
<tr>
	<td class="detaillabel"><yfc:i18n>ROSS_Special_Needs</yfc:i18n></td>
	<td>
		<!-- Begin CR830 01252013 -->
          <!-- <textarea class="unprotectedtextareainput" rows="4" cols="60" style="word-wrap:break-word;" MaxLength="1500" <%=getTextAreaOptions("xml:/Order/@ROSSSpecialNeeds")%>></textarea> -->
            <textarea class="unprotectedtextareainput" rows="4" cols="60" style="word-wrap:break-word;" MaxLength="1500"><%=resolveValue("xml:/Order/@ROSSSpecialNeeds")%></textarea>
        <!-- End CR830 01252013 -->
	</td>
</tr>
<%}%>
	<input type="hidden" name="RequestNoBlockStart" value=<%=resolveValue("xml:/NWCGIncidentOrder/@RequestNoBlockStart")%> id="RequestNoBlockStart"/>
	<input type="hidden" name="RequestNoBlockEnd" value=<%=resolveValue("xml:/NWCGIncidentOrder/@RequestNoBlockEnd")%> id="RequestNoBlockEnd"/>
	<input type="hidden" name="IncidentKey" value=<%=resolveValue("xml:/NWCGIncidentOrder/@IncidentKey")%> id="IncidentKey"/>
	<input type="hidden" name="OrderType" value=<%=strOrderType%> id="OrderType"/>
	<input type="hidden" name="xml:/Order/Extn/@ExtnNavInfo" value=<%=resolveValue("xml:/Order/Extn/@ExtnNavInfo")%>>
	<input type="hidden" name="xml:/Order/Extn/@OldExtnNavInfo" value=<%=resolveValue("xml:/Order/Extn/@ExtnNavInfo")%>>
</table>
