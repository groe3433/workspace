<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.nwcg.icbs.yantra.constant.common.NWCGConstants" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.StringUtil" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/extn.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/extn.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_orderDetails.js"></script>

<script language="Javascript" >
function setToIncidentTransferParam(elemYear) {
	var elemIncidentNo = document.getElementById("xml:/Order/Extn/@ExtnIncidentNo");
	var returnArray = new Object();
	// replacing them as the command will understand only incident no and year not to incident number and to year
	returnArray["xml:/Order/Extn/@ExtnIncidentNo"] = elemIncidentNo.value
	returnArray["xml:/Order/Extn/@ExtnIncidentYear"] = elemYear.value;
	return returnArray;
}

function setFromIncidentTransferParam(elemYear) {
	var elemIncidentNo = document.getElementById("xml:/Order/Extn/@ExtnToIncidentNo");
	var returnArray = new Object();
	// replacing them as the command will understand only incident no and year not to incident number and to year
	returnArray["xml:/Order/Extn/@ExtnIncidentNo"] = elemIncidentNo.value
	returnArray["xml:/Order/Extn/@ExtnIncidentYear"] = elemYear.value;
	return returnArray;
}
</script>

<%
	String strDocType = resolveValue("xml:/Order/@DocumentType");
	boolean bOtherOrder = false ;
	if(strDocType != null && (!strDocType.equals("0001"))) {
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
	String blmAcctCode = resolveValue("xml:/Order/Extn/@ExtnToBlmAcctCode");
	String fsAccountCode = resolveValue("xml:/Order/Extn/@ExtnToFsAcctCode");
	String overrideCode = resolveValue("xml:/Order/Extn/@ExtnToOverrideCode");
	String otherAcctCode = resolveValue("xml:/Order/Extn/@ExtnToOtherAcctCode");
	String shipAcctCode = resolveValue("xml:/Order/Extn/@ExtnShipAcctCode");
	String shipAcctOverrideCode = resolveValue("xml:/Order/Extn/@ExtnSAOverrideCode");
	String holdFlag = resolveValue("xml:/Order/@HoldFlag");
	String extnNavInfo = resolveValue("xml:/Order/Extn/@ExtnNavInfo");
	String onWillPickUpTitleTxt = "The Requested Delivery Date is set in the Will Pick Up box below for the Will Pick Up Shipping Method";
	String costCenter = resolveValue("xml:/Order/Extn/@ExtnCostCenter");
	String functionalArea = resolveValue("xml:/Order/Extn/@ExtnFunctionalArea");
	String wbs = resolveValue("xml:/Order/Extn/@ExtnWBS");
	String toBLMAcctCode = resolveValue("xml:/Order/Extn/@ExtnToBlmAcctCode");
	String toFSAcctCode = resolveValue("xml:/Order/Extn/@ExtnToFsAcctCode");
	String toOverrideCode = resolveValue("xml:/Order/Extn/@ExtnToOverrideCode");
	String toOtherAcctCode = resolveValue("xml:/Order/Extn/@ExtnToOtherAcctCode");
	boolean hasValidFS = false;
	boolean hasValidBLM = false;
	boolean hasValidOther = false;
	if (StringUtil.isEmpty(fsAccountCode) && StringUtil.isEmpty(overrideCode)) {
		hasValidFS = false;
	}
	else { 
		hasValidFS = true;
	}
	if ((StringUtil.isEmpty(blmAcctCode) || blmAcctCode.length() < 5)) {
		hasValidBLM = false;	
	} else {
		hasValidBLM = true;
	}
	if (StringUtil.isEmpty(otherAcctCode) || StringUtil.isEmpty(overrideCode)) {
		hasValidOther = false;
	} else {
		hasValidOther = true;
	}
	if (!hasValidFS && !hasValidBLM && !hasValidOther) {
%>
		<yfc:callAPI apiID="A12"/>
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
	temp = getValue("icbsCodes", "xml:/Order/Extn/@ExtnToBlmAcctCode");
	toBLMAcctCode = StringUtil.isEmpty(temp) ? toBLMAcctCode : new String(temp);
	temp = getValue("icbsCodes", "xml:/Order/Extn/@ExtnToFsAcctCode");
	toFSAcctCode = StringUtil.isEmpty(temp) ? toFSAcctCode : new String(temp);
	temp = getValue("icbsCodes", "xml:/Order/Extn/@ExtnToOverrideCode");
	toOverrideCode = StringUtil.isEmpty(temp) ? toOverrideCode : new String(temp);	
	temp = getValue("icbsCodes", "xml:/Order/Extn/@ExtnToOtherAcctCode");
	toOtherAcctCode = StringUtil.isEmpty(temp) ? toOtherAcctCode : new String(temp);	
%>

<script language="javascript">
function callPSItemLookup()	{
	// this method is used by 'Add Service Request' action on order header detail innerpanel
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
		<!--
		<td class="detaillabel" ><yfc:i18n>Buyer</yfc:i18n></td>
		<%if (isModificationAllowed("xml:/Order/@BillToID","xml:/Order/AllowedModifications") && !isVoid(modifyView)) { %>
		<td nowrap="true">
		<input type="text"   <% if (equals(sRequestDOM,"Y")) {%>  OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@BillToID")%>"  <%}%> <%=yfsGetTextOptions("xml:/Order/@BillToID", "xml:/Order/AllowedModifications")%>/>
		<img class="lookupicon" name="search" onclick="callLookup(this,'NWCGCustomerLookUp')" <%=yfsGetImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization", "xml:/Order/@BillToID", "xml:/Order/AllowedModifications")%>/>
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
		-->
		<td class="detaillabel" ><yfc:i18n>Seller</yfc:i18n></td>
<%
			if (isModificationAllowed("xml:/Order/@SellerOrganizationCode","xml:/Order/AllowedModifications") && !isVoid(modifyView)) { 
%>
				<td nowrap="true">
					<input readonly type="text"   <% if (equals(sRequestDOM,"Y")) {%>  OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@SellerOrganizationCode")%>"  <%}%> <%=yfsGetTextOptions("xml:/Order/@SellerOrganizationCode", "xml:/Order/AllowedModifications")%>/>
				</td>
<% 
			} else { 
%>
				<td class="protectedtext">
					<yfc:makeXMLInput name="OrganizationKey" >
						<yfc:makeXMLKey binding="xml:/Organization/@OrganizationKey" value="xml:/Order/@SellerOrganizationCode" />
					</yfc:makeXMLInput>
					<a <%=getDetailHrefOptions("L03",getParameter("OrganizationKey"),"")%> >
						<yfc:getXMLValue binding="xml:/Order/@SellerOrganizationCode"/>
					</a>
				</td>
<% 
			} 
%>
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Order_#</yfc:i18n></td>
		<td class="protectedtext"><a <%=getDetailHrefOptions("L06", resolveValue("xml:/Order/@DocumentType"), getParameter("orderKey"),"")%> ><yfc:getXMLValue binding="xml:/Order/@OrderNo"/></a></td>
		<td class="detaillabel" ><yfc:i18n>Status</yfc:i18n></td>
		<td class="protectedtext">
<% 
			if (isVoid(getValue("Order", "xml:/Order/@Status"))) {
%>
				[<yfc:i18n>Draft</yfc:i18n>]
<% 
			} else { 
%>
				<a <%=getDetailHrefOptions("L01", getParameter("orderKey"), "ShowReleaseNo=Y")%>><%=displayOrderStatus(getValue("Order","xml:/Order/@MultipleStatusesExist"),getValue("Order","xml:/Order/@MaxOrderStatusDesc"))%></a>
<% 
			}  
			if (equals("Y", holdFlag)) { 
				if (isVoid(modifyView) || isTrue("xml:/Rules/@RuleSetValue")) {
%>
					<img onmouseover="this.style.cursor='default'" class="columnicon" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_order_is_held")%>>
<%
				} else {	
%>
					<a <%=getDetailHrefOptions("L05", getParameter("orderKey"), "")%>><img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_order_is_held\nclick_to_add/remove_hold")%>></a>
<%	
				}	
			} 
			if (equals("Y", getValue("Order", "xml:/Order/@SaleVoided"))) { 
%>
				<img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.SALE_VOIDED, "This_sale_is_voided")%>/>
<% 
			} 
			if (equals("Y", getValue("Order","xml:/Order/@isHistory") )) { 
%>
				<img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.HISTORY_ORDER, "This_is_an_archived_order")%>/>
<% 
			} 
%>
		</td>
		<td class="detaillabel" ><yfc:i18n>Issue_Date</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@OrderDate"/></td>
	</tr>
	<tr>
<%
		if(!bOtherOrder) {
%>
			<td class="detaillabel" ><yfc:i18n>Order_Type</yfc:i18n></td>
			<td class="protectedtext"><%=getComboText("xml:OrderTypeList:/CommonCodeList/@CommonCode","CodeShortDescription","CodeValue","xml:/Order/@OrderType",true)%></td>
<%
		} 
		if(equals("true", modifyView)) {
%>
			<td class="detaillabel" >
				<yfc:i18n>Document_Type</yfc:i18n>
			</td>
			<td class="protectedtext">
				<yfc:getXMLValueI18NDB binding="xml:DocumentParamsList:/DocumentParamsList/DocumentParams/@Description"></yfc:getXMLValueI18NDB>
			</td>
<% 
		} 
		// Show ReqDeliveryDate if that is the driver date (determined by the "DriverDate" attribute output by getOrderDetails) else show ReqShipDate as the driver date.
		String strSDF = new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date()) ;
		if (equals(driverDate, "02")) { 
%>
			<td class="detaillabel" ><yfc:i18n>Requested_Delivery_Date</yfc:i18n></td>
<% 
			if (!isVoid(modifyView)) {
%>
				<td nowrap="true">
					<input type="text"   <% if (equals(sRequestDOM,"Y")) {%>  OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@ReqDeliveryDate")%>"  <%}%> <%=yfsGetTextOptions("xml:/Order/@ReqDeliveryDate","xml:/Order/@ReqDeliveryDate",strSDF, "xml:/Order/AllowedModifications")%>/>
					<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar", "xml:/Order/@ReqDeliveryDate", "xml:/Order/AllowedModifications")%>/>
				</td>
<% 
			} else { 
%>
				<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@ReqDeliveryDate"/><td>
<% 
			} 
		} else { 
%>
			<td class="detaillabel" ><yfc:i18n>Requested_Ship_Date</yfc:i18n></td>
<% 
			if (!isVoid(modifyView)) {
%>
				<td nowrap="true">
				<input type="text"   
<% 			
					if (equals(sRequestDOM,"Y")) {
%>  
						OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@ReqShipDate")%>"  
<%					
					}
%> 					<%=yfsGetTextOptions("xml:/Order/@ReqShipDate","xml:/Order/@ReqShipDate",strSDF, "xml:/Order/AllowedModifications")%>/>
					<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar", "xml:/Order/@ReqShipDate", "xml:/Order/AllowedModifications")%>/>
				</td>
<% 
			} else { 
%>
				<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@ReqShipDate"/><td>
<% 
			} 
		} 
		String exchangeType = "";
		if (equals(sRequestDOM,"Y")) {
			exchangeType = getValue("OrigAPIOutput", "xml:/Order/@ExchangeType");
		} else {
			exchangeType = getValue("Order", "xml:/Order/@ExchangeType");
		}
		if(!isVoid(exchangeType)) {
			//call master data for exchange type. Call API to get the data for the Document Type field.
			String exchangeTypeStr = "<CommonCode CodeType=\"EXCHANGE_TYPE\"/>";
			YFCElement exchangeTypeInput = YFCDocument.parse(exchangeTypeStr).getDocumentElement();
			YFCElement exchangeTypeTemplate = YFCDocument.parse("<CommonCodeList TotalNumberOfRecords=\"\"><CommonCode CodeType=\"\" CodeShortDescription=\"\" CodeValue=\"\" CommonCodeKey=\"\"/></CommonCodeList>").getDocumentElement();
%>
			<yfc:callAPI apiName="getCommonCodeList" inputElement="<%=exchangeTypeInput%>" templateElement="<%=exchangeTypeTemplate%>" outputNamespace="ExchangeTypeList"/>
			<td class="detaillabel" ><yfc:i18n>Exchange_Type</yfc:i18n></td>
			<td>
				<select  
<% 
					if (isVoid(modifyView)) {
%> 
						<%=getProtectedComboOptions()%> 
<%
					} if (equals(sRequestDOM,"Y")) {
%> 
						OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@ExchangeType")%>"  
<%
					}
%> 
					<%=yfsGetComboOptions("xml:/Order/@ExchangeType", "xml:/Order/AllowedModifications")%>>
					<yfc:loopOptions binding="xml:ExchangeTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/Order/@ExchangeType" isLocalized="Y"/>
				</select>
			</td>
<% 
		} 
		if(!isVoid(getValue("Order", "xml:/Order/@ReturnOrderHeaderKeyForExchange"))) { 
%>
			<yfc:makeXMLInput name="ReturnOHKeyForExchange">
				<yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Order/ReturnOrdersForExchange/ReturnOrderForExchange/@OrderHeaderKey" />
			</yfc:makeXMLInput>
			<td class="detaillabel" ><yfc:i18n>Created_For_Return_#</yfc:i18n></td>
			<td class="protectedtext">
				<a <%=getDetailHrefOptions("L04",getParameter("ReturnOHKeyForExchange"),"")%> >
					<yfc:getXMLValue binding="xml:/Order/ReturnOrdersForExchange/ReturnOrderForExchange/@OrderNo"/>
				</a>
			</td>
<% 
		} 
%>
	</tr>
	<tr>
        <td class="detaillabel" ><yfc:i18n>From_Incident_No</yfc:i18n></td>
        <td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentNo")%> id="xml:/Order/Extn/@ExtnIncidentNo"/>
            <img class="lookupicon" onclick="callIncidentLookup('xml:/Order/Extn/@ExtnIncidentNo','xml:/Order/Extn/@ExtnIncidentYear','NWCGIncidentLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Incident") %> />
        </td>
		<td class="detaillabel" ><yfc:i18n>From_Incident_Year</yfc:i18n></td>
        <td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentYear")%> id="xml:/Order/Extn/@ExtnIncidentYear" 
            onblur="javascript:if(document.getElementById('xml:/Order/Extn/@ExtnIncidentYear').value=='' || document.getElementById('xml:/Order/Extn/@ExtnIncidentNo').value=='') {
						alert('Please enter Incident# and Year');
						return false; 
					} 
            		fetchDataWithParams(document.getElementById('xml:/Order/Extn/@ExtnIncidentNo'), 'getIncidentDetails', updateFromIncidentDetailsWithoutCustDetail, setToIncidentTransferParam(this));"/>
        </td>
		<!-- other incident details -->
		<td class="detaillabel"><yfc:i18n>From_Incident_Name</yfc:i18n></td>
		<td>
			<input type="text" class="protectedinput" readonly="true" size="50" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentName")%>/>
		</td>
	</tr>
		<td class="detaillabel"><yfc:i18n>From_Incident_Type</yfc:i18n></td>
		<td>
			<!--CR 207 KS -->
<%
			YFCElement commonCodeInput = YFCDocument.parse("<CommonCode CodeValue=\"" + resolveValue("xml:/Order/Extn/@ExtnIncidentType") + "\"  />").getDocumentElement();
			YFCElement commonCodeTemplate = YFCDocument.parse("<CommonCodeList> <CommonCode CodeShortDescription=\"\"/> </CommonCodeList>").getDocumentElement();
%>
			<yfc:callAPI apiName="getCommonCodeList" inputElement="<%=commonCodeInput%>" templateElement="<%=commonCodeTemplate%>" outputNamespace="CommonCodeList"/>
			<!-- displays the value from returned API call -->
			<input type="text" class="unprotectedinput" readonly="true" <%=getTextOptions("xml:CommonCodeList:/CommonCodeList/CommonCode/@CodeShortDescription")%>/>
			<input type="hidden" class="unprotectedinput" readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentType")%>/>
		</td>
		<!--
			<input type="text" class="unprotectedinput" readonly="true" <%=getTextOptions("xml:/CommonCodeList/CommonCode/@CodeShortDescription")%>/>
			<input type="hidden" class="unprotectedinput" readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnToIncidentType")%>/>
		-->
		<td class="detaillabel"><yfc:i18n>From_Fs_Acct_Code</yfc:i18n></td>
		<td>
			<input type="text" class="protectedinput" readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnFsAcctCode")%>/>
		</td>
		<td class="detaillabel" ><yfc:i18n>From_Incident_OverrideCode</yfc:i18n></td>
        <td>
            <input type="text" class="protectedinput" readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnOverrideCode")%>/>
        </td>
	</tr>
		<td class="detaillabel"><yfc:i18n>From_Blm_Acct_Code</yfc:i18n></td>
		<td>
			<input type="text" class="protectedinput" size="50" readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnBlmAcctCode")%>/>
		</td>
		<td class="detaillabel"><yfc:i18n>From_Other_Acct_Code</yfc:i18n></td>
		<td>
			<input type="text" class="protectedinput" readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnOtherAcctCode")%>/>
		</td>
		<td class="detaillabel"><yfc:i18n>From_Incident_Fs_Amount</yfc:i18n></td>
		<td>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnFsAmount")%>/>
		</td>
	</tr>
		<td class="detaillabel"><yfc:i18n>From_Incident_Blm_Amount</yfc:i18n></td>
		<td>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnBlmAmount")%>/>
		</td>
		<td class="detaillabel"><yfc:i18n>From_Incident_Other_Amount</yfc:i18n></td>
		<td>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnOtherAmount")%>/>
		</td>
	</tr>
 		<td class="detaillabel" ><yfc:i18n>To_Incident_No</yfc:i18n></td>
   		<td>
    		<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnToIncidentNo")%> id="xml:/Order/Extn/@ExtnToIncidentNo" />
    		<img class="lookupicon" onclick="callIncidentLookup('xml:/Order/Extn/@ExtnToIncidentNo','xml:/Order/Extn/@ExtnToIncidentYear','NWCGIncidentLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Incident") %> />
    	</td>
    	<td class="detaillabel" ><yfc:i18n>To_Incident_Year</yfc:i18n></td>
    	<td>
    		<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnToIncidentYear")%> id="xml:/Order/Extn/@ExtnToIncidentYear" 
    			   onblur="javascript:if(document.getElementById('xml:/Order/Extn/@ExtnToIncidentYear').value=='' || document.getElementById('xml:/Order/Extn/@ExtnToIncidentNo').value=='') {
								alert('Please enter Incident# and Year');
						   		return false;
				  		   }
						   fetchDataWithParams(document.getElementById('xml:/Order/Extn/@ExtnToIncidentNo'), 'getIncidentDetails', updateToIncidentDetailsWithoutCustDetail, setFromIncidentTransferParam(this));"/>
 		</td>
		<!-- other incident details -->
		<td class="detaillabel"><yfc:i18n>To_Incident_Name</yfc:i18n></td>
		<td>
			<input type="text" class="protectedinput" readonly="true" size="50" <%=getTextOptions("xml:/Order/Extn/@ExtnToIncidentName")%>/>
		</td>
	</tr>
	<tr>
		<td class="detaillabel"><yfc:i18n>To_Incident_Type</yfc:i18n></td>
		<td>
			<!--CR 207 KS -->
<%
			YFCElement commonCodeToInput = YFCDocument.parse("<CommonCode CodeValue=\"" + resolveValue("xml:/Order/Extn/@ExtnToIncidentType") + "\"  />").getDocumentElement();
			YFCElement commonCodeToTemplate = YFCDocument.parse("<CommonCodeList> <CommonCode CodeShortDescription=\"\"/> </CommonCodeList>").getDocumentElement();
%>
			<yfc:callAPI apiName="getCommonCodeList" inputElement="<%=commonCodeToInput%>" templateElement="<%=commonCodeToTemplate%>" outputNamespace="CommonCodeList"/>
			<!-- displays the value from returned API call -->
			<input type="text" class="unprotectedinput" readonly="true" <%=getTextOptions("xml:/CommonCodeList/CommonCode/@CodeShortDescription")%>/>
			<input type="hidden" class="unprotectedinput" readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnToIncidentType")%>/>
		</td>
		<td class="detaillabel"><yfc:i18n>To_Fs_Acct_Code</yfc:i18n></td>
		<td>
			<INPUT class=protectedinput readOnly maxLength=40 size=50 <%=getTextOptions("xml:/Order/Extn/@ExtnToFsAcctCode")%> dataType="STRING"/>
		</td>
		<td class="detaillabel"><yfc:i18n>To_Incident_OverrideCode</yfc:i18n></td>
        <td>
            <INPUT class=protectedinput readOnly maxLength=40 size=50 value="<%=overrideCode%>" name="xml:/Order/Extn/@ExtnToOverrideCode" dataType="STRING"/>
        </td>
	</tr>
	<tr>
		<td class="detaillabel"><yfc:i18n>To_Blm_Acct_Code</yfc:i18n></td>
		<td>
			<INPUT class=protectedinput readOnly maxLength=40 size=40 value="<%=toBLMAcctCode%>" name="xml:/Order/Extn/@ExtnToBlmAcctCode" dataType="STRING"/>
		</td>
		<td class="detaillabel"><yfc:i18n>To_Other_Acct_Code</yfc:i18n></td>
		<td>
			<INPUT class=protectedinput readOnly maxLength=40 size=40 value="<%=toOtherAcctCode%>" name="xml:/Order/Extn/@ExtnToOtherAcctCode" dataType="STRING"/>
		</td>
		<td class="detaillabel"><yfc:i18n>Incident_Fs_Amount</yfc:i18n></td>
		<td>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnToFsAmount")%>/>
		</td>
	</tr>
	<tr>
		<td class="detaillabel"><yfc:i18n>Incident_Blm_Amount</yfc:i18n></td>
		<td>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnToBlmAmount")%>/>
		</td>	
		<td class="detaillabel"><yfc:i18n>Incident_Other_Amount</yfc:i18n></td>
		<td>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnToOtherAmount")%>/>
		</td>
<% 
		String strUserNode = resolveValue("xml:CurrentUser:/User/@Node"); 
%>
		<td class="detaillabel"><yfc:i18n>Ship_Cache</yfc:i18n></td>
		<td>
			<!-- <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@ShipNode",strUserNode)%>/> !-->
			<input type="text" class="unprotectedinput" name="xml:/Order/@ShipNode" value="<%=strUserNode%>"/>
			<img class="lookupicon" onclick="callLookup(this,'shipnode')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Ship_Node")%>/>
		</td>
	</tr>
	<!-- FBMS elements -->
	<tr>
		<td class="detaillabel"><yfc:i18n>Cost_Center</yfc:i18n></td>
		<td><input type="text" class="protectedinput" size="20" maxLength="10" <%=getTextOptions("xml:/Order/Extn/@ExtnCostCenter","xml:/Order/Extn/@ExtnCostCenter",costCenter)%>/></td>
		<td class="detaillabel"><yfc:i18n>Functional_Area</yfc:i18n></td>
		<td><input type="text" class="protectedinput" size="20" maxLength="16" <%=getTextOptions("xml:/Order/Extn/@ExtnFunctionalArea","xml:/Order/Extn/@ExtnFunctionalArea",functionalArea)%>/></td>
		<td class="detaillabel"><yfc:i18n>Work_Breakdown_Structure</yfc:i18n></td>
		<td><input type="text" class="protectedinput" size="20" maxLength="12" <%=getTextOptions("xml:/Order/Extn/@ExtnWBS","xml:/Order/Extn/@ExtnWBS",wbs)%>/></td>
		<td/>
	</tr>
	<!-- FBMS elements -->
	<input type="hidden" name="IncidentKey" value=<%=resolveValue("xml:/NWCGIncidentOrder/@IncidentKey")%> id="IncidentKey"/>
</table>