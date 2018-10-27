<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/orderentry.jspf" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.dom.*" %>
<%@ page import="com.nwcg.icbs.yantra.constant.common.NWCGConstants" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.StringUtil" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_cache_transfer.js"></script>

<script language="javascript">
	function verifyUser() {
		var recvNode = document.getElementById("xml:/Order/@ReceivingNode").value;
		var shipNode = document.getElementById("xml:/Order/@ShipNode").value;
		var isChecked = checkNodes(this);
		if(isChecked == true) {
			alert("Shipping and Receiving Nodes cannot be the same and/or BOTH cannot be blank!!!");
		} else if(recvNode == "" || shipNode == "") {
			alert('Please enter BOTH Shipping and Receiving Nodes!!!');
		} else {
			window.print();
		}
	}
</script>

<!-- ReceivingNode dependent information -->
<%
	String strNode = StringUtil.nonNull(resolveValue("xml:CurrentUser:/User/@Node"));
	if(strNode.equals("")) {
		strNode = "NWCG";
	}
	System.out.println("@@@@@ strNode : " + strNode);
	
	YFCDocument inputDoc = YFCDocument.parse("<Organization OrganizationCode=\""+strNode+"\"/>");
	YFCDocument templateDoc = YFCDocument.parse("<Organization OrganizationCode=\"\"> <Extn/> </Organization>");
	if(!strNode.equals("")) {
%>
		<yfc:callAPI apiName='getOrganizationList' inputElement='<%=inputDoc.getDocumentElement()%>' templateElement='<%=templateDoc.getDocumentElement()%>' outputNamespace='OrganizationList'/>
<%
	}
	YFCElement elemOrganizationList = (YFCElement) request.getAttribute("OrganizationList");
	YFCNodeList nlExtn = null;
	if(elemOrganizationList != null) {
		nlExtn = elemOrganizationList.getElementsByTagName("Extn");
	}
	YFCElement elemExtn = null;
	if(nlExtn != null && nlExtn.getLength() == 1) {
		elemExtn = (YFCElement) nlExtn.item(0);
	} else {
		// assigning the list output just to avoid the null pointer check all the time we can also create and assign a new element
		elemExtn = elemOrganizationList ;
	}
	
%>

<!-- ShipNode dependent information -->
<script language="Javascript" >
	IgnoreChangeNames();
	yfcDoNotPromptForChanges(true);
</script>

<%
	String OwnerAgency = resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnOwnerAgency");
	System.out.println("OwnerAgency=["+ OwnerAgency +"]");
%>
	<input type="hidden" name="xml:/Order/@ShippingOwnerAgency" value="<%=resolveValue("xml:/OrganizationList/Organization/Extn/@ExtnOwnerAgency")%>"/>

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
		if(isRoleDefaultingRequired((YFCElement) request.getAttribute("CurrentOrgRoleList"))) {
			sellerOrgCode = getValue("CurrentOrganization", "xml:CurrentOrganization:/Organization/@OrganizationCode");
		}
	}
	String exchangeOrderForReturn = resolveValue("xml:/ReturnOrder/@ReturnOrderHeaderKeyForExchange");
	String orderHeaderKeyVal = resolveValue("xml:/Order/@OrderHeaderKey");
	String sLookup="NWCGCustomerLookUp";
	String strAgencyCode = resolveValue("xml:OrganizationOwnerAgency:/Organization/Extn/@ExtnOwnerAgency");
	System.out.println("strAgencyCode " + strAgencyCode);
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
					callPopupWithEntity('NWCorder', '<%=orderDoc.getDocumentElement().getString(false)%>');
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
			<input type="hidden" name="xml:/Order/@EnteredBy" value="<%=resolveValue("xml:CurrentUser:/User/@Loginid")%>"/>
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
		</td>
	</tr>
	<!-- defaulting the document type and enterprise code -->
	<input type="hidden" name="xml:/Order/@DocumentType" value='0006'/>
	<input type="hidden" name="xml:/Order/@EnterpriseCode" value='NWCG'/>
	<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnValidIncident")%> id="xml:/Order/Extn/@ExtnValidIncident" value='true'/>
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
		<jsp:include page="/extn/om/cache_transfer/detail/common_fields_transfer_issue.jsp" flush="true">
			<jsp:param name="ScreenType" value="detail"/>
			<jsp:param name="HardCodeDocumentType" value="0006"/>
			<jsp:param name="RefreshOnDocumentType" value="true"/>
			<jsp:param name="RefreshOnEnterpriseCode" value="true"/>
			<jsp:param name="ShowDocumentType" value="false"/>
			<jsp:param name="ShowEnterpriseCode" value="false"/>
		</jsp:include>
<% 
	} 
%>
<% 
	// Now call the APIs that are dependent on the common fields (Doc Type & Enterprise Code) %>
	<yfc:callAPI apiID="AP1"/>
	<yfc:callAPI apiID="AP2"/>
	<yfc:callAPI apiID="AP4"/>
	<tr>
<% 
		String enterpriseCodeFromCommon = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");
%>
		<!--<td class="detaillabel" ><yfc:i18n>Buyer</yfc:i18n></td>-->
<% 
		if (!isExchangeOrderCreation) { 
%>
			<!--
			<td nowrap="true" >
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@BillToID")%>/>
			<img class="lookupicon" onclick="callLookup(this,'<%=sLookup%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Customer") %> />
			</td>
			-->
<% 		
		} else { 
%>
			<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@BuyerOrganizationCode"/></td>
			<input type="hidden" name="xml:/Order/@BuyerOrganizationCode" value="<%=getValue("Order", "xml:/Order/@BuyerOrganizationCode")%>"/>
<% 
		} 
%>
		<!--
		<td class="detaillabel" ><yfc:i18n>Seller</yfc:i18n></td>
		-->
<% 
		String enterpriseCodeForCommon = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
<% 
		if (!isExchangeOrderCreation) { 
%>
			<td nowrap="true" >
				<input type="hidden" class="unprotectedinput" name="xml:/Order/@SellerOrganizationCode" value="<%=getValue("CurrentOrganization","xml:CurrentOrganization:/Organization/@PrimaryEnterpriseKey")%>">
				<!--
				<img class="lookupicon" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCodeForCommon%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
				-->
			</td>
<% 
		} else { 
%>
			<td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@SellerOrganizationCode"/></td>
			<input type="hidden" name="xml:/Order/@SellerOrganizationCode" value="<%=getValue("Order", "xml:/Order/@SellerOrganizationCode")%>"/>
<% 
		} 
%>
	</tr>
	<tr>
		<td/>
		<td/>
		<td/>
		<td>
			<input type="button" style="width:80px; height:32px; font-size:22px" value="Print" name="Print" onclick="verifyUser()" />
		</td>
	</tr>
	<tr>
		<td class="detaillabel" style="font-size:18px"><yfc:i18n>Order_Date</yfc:i18n></td>
		<td>
			<input type="hidden" name="xml:/Order/@OrderDate"/>
			<input class="dateinput" style="height:24px; font-size:18px" type="text" <%=getTextOptions("xml:/Order/@OrderDate_YFCDATE","xml:/Order/@OrderDate_YFCDATE", getTodayDate())%>/>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" style="height:24px; font-size:18px" type="text" <%=getTextOptions("xml:/Order/@OrderDate_YFCTIME","xml:/Order/@OrderDate_YFCTIME", getCurrentTime())%>/>
			<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %>/>
		</td>
		<td class="detaillabel" style="font-size:18px"><yfc:i18n>Requesting_Cache</yfc:i18n></td>
		<%
			if(strNode.equals("NWCG")) {
		%>
				<td nowrap="true">
					<input type="text" style="height:24px; font-size:18px" class="unprotectedinput" <%=getTextOptions("xml:/Order/@ShipNode")%> id="xml:/Order/@ShipNode" onblur="javascript:fetchDataWithParams(this,'getShipNodeAccountCodes',updateAccountCodesUsingRequestingNode,setParam2(document.getElementById('xml:/Order/@ShipNode')));"/>
					<img class="lookupicon" onclick="callLookup(this,'organization','xml:/Organization/OrgRoleList/OrgRole/@RoleKey=NODE')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
				</td>
		<%
			} else {
		%>
				<td nowrap="true">
					<input class="protectedinput" readonly="true" style="height:24px; font-size:18px" <%=getTextOptions("xml:/Order/@ShipNode","xml:CurrentUser:/User/@Node")%>/>
				</td>
		<%
			}
		%>
	</tr>
	<tr>
		<td class="detaillabel" style="font-size:18px"><yfc:i18n>Shipping_Cache</yfc:i18n></td>
		<td nowrap="true">
			<input type="text" style="height:24px; font-size:18px" class="unprotectedinput" <%=getTextOptions("xml:/Order/@ReceivingNode")%> id="xml:/Order/@ReceivingNode" onblur="javascript:fetchDataWithParams(this,'getAccountCodes',updateAccountCodesUsingShipNode,setParam(document.getElementById('xml:/Order/@ReceivingNode')));"/>
			<img class="lookupicon" onclick="callLookup(this,'organization','xml:/Organization/OrgRoleList/OrgRole/@RoleKey=NODE')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
		</td>
		<td class="detaillabel" style="font-size:18px"><yfc:i18n>Shipping_Account_Code</yfc:i18n></td>
		<td>
	   		<input type="text" style="height:24px; font-size:18px" class="protectedinput" size=45 maxLength=40 readonly="true" id="xml:/Order/Extn/@ExtnShipAcctCode" <%=getTextOptions("xml:/Order/Extn/@ExtnShipAcctCode")%>/>
		</td>
	</tr>
	<tr>
		<!--<td class="detaillabel"><yfc:i18n>Incident_Year</yfc:i18n></td>-->
		<td>
	   		<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentYear")%> id="xml:/Order/Extn/@ExtnIncidentYear"/>
		</td>
		<!--<td class="detaillabel"><yfc:i18n>Incident_Number</yfc:i18n></td>-->
		<td>
   			<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentNo")%> id="xml:/Order/Extn/@ExtnIncidentNo" onblur="javascript:if(document.getElementById('xml:/Order/Extn/@ExtnIncidentNo').value != '' || document.getElementById('xml:/Order/Extn/@ExtnIncidentYear').value != ''){fetchDataWithParams(this,'getAccountCodes',updateAccountCodesUsingShipNode,setIncidentParamCT(this));}"/>
		</td>
	</tr>
	<tr>
		<td class="detaillabel" style="font-size:18px"><yfc:i18n>SA_Override_Code</yfc:i18n></td>
		<td>
			<input type="text" style="height:24px; font-size:18px" class="protectedinput" readonly="true" size=4 maxLength=4 <%=getTextOptions("xml:/Order/Extn/@ExtnSAOverrideCode")%>/>
		</td>
		<td class="detaillabel" style="font-size:18px"><yfc:i18n>BLM_Order_Acct_Code</yfc:i18n></td>
		<%
			if(strNode.equals("NWCG")) {
		%>
				<td nowrap="true">
	   				<input type="text" style="height:24px; font-size:18px" class="protectedinput" size=45 maxLength=40 readonly="true" id="xml:/OrganizationList/Organization/Extn/@ExtnBLMTransferOrderAcctCode" <%=getTextOptions("xml:/OrganizationList/Organization/Extn/@ExtnBLMTransferOrderAcctCode")%>/>
				</td>
		<%
			} else {
		%>
				<td>
					<input type="text" style="height:24px; font-size:18px" class="protectedinput" readonly="true" size=45 maxLength=40 <%=getTextOptions("xml:/OrganizationList/Organization/Extn/@ExtnBLMTransferOrderAcctCode",StringUtil.nonNull(elemExtn.getAttribute("ExtnBLMTransferOrderAcctCode")))%>/>
				</td>
		<%
			}
		%>
	</tr>
	<tr>
		<td class="detaillabel" style="font-size:18px"><yfc:i18n>Receiving_Account_Code</yfc:i18n></td>
		<%
			if(strNode.equals("NWCG")) {
		%>
				<td nowrap="true">
			   		<input type="text" style="height:24px; font-size:18px" class="protectedinput" size=45 maxLength=40 readonly="true" id="xml:/OrganizationList/Organization/Extn/@ExtnRecvAcctCode" <%=getTextOptions("xml:/OrganizationList/Organization/Extn/@ExtnRecvAcctCode")%>/>
				</td>
		<%
			} else {
		%>
				<td>
	   				<input type="text" style="height:24px; font-size:18px" class="unprotectedinput" size=45 maxLength=40 <%=getTextOptions("xml:/OrganizationList/Organization/Extn/@ExtnRecvAcctCode",StringUtil.nonNull(elemExtn.getAttribute("ExtnRecvAcctCode")))%>/>
				</td>
		<%
			}
		%>
		<td class="detaillabel" style="font-size:18px"><yfc:i18n>RA_Override_Code</yfc:i18n></td>
		<%
			if(strNode.equals("NWCG")) {
		%>
				<td nowrap="true">
					<input type="text" style="height:24px; font-size:18px" class="protectedinput" size=45 maxLength=40 readonly="true" id="xml:/OrganizationList/Organization/Extn/@ExtnRAOverrideCode" <%=getTextOptions("xml:/OrganizationList/Organization/Extn/@ExtnRAOverrideCode")%>/>
				</td>
		<%
			} else {
		%>
				<td>
					<input type="text" style="height:24px; font-size:18px" class="unprotectedinput" size=4 maxLength=4 <%=getTextOptions("xml:/OrganizationList/Organization/Extn/@ExtnRAOverrideCode",StringUtil.nonNull(elemExtn.getAttribute("ExtnRAOverrideCode")))%>/>
				</td>
		<%
			}
		%>
	</tr>
	<tr>
		<td class="detaillabel" style="font-size:18px"><yfc:i18n>OTHER_Order_Acct_Code</yfc:i18n></td>
		<%
			if(strNode.equals("NWCG")) {
		%>
				<td nowrap="true">
					<input type="text" style="height:24px; font-size:18px" class="protectedinput" size=45 maxLength=40 readonly="true" id="xml:/OrganizationList/Organization/Extn/@ExtnOtherTransferOrderAcctCode" <%=getTextOptions("xml:/OrganizationList/Organization/Extn/@ExtnOtherTransferOrderAcctCode")%>/>
				</td>
		<%
			} else {
		%>
				<td>
					<input type="text" style="height:24px; font-size:18px" readonly="true" class="protectedinput" size=45 maxLength=40 <%=getTextOptions("xml:/OrganizationList/Organization/Extn/@ExtnOtherTransferOrderAcctCode",StringUtil.nonNull(elemExtn.getAttribute("ExtnOtherTransferOrderAcctCode")))%>/>
				</td>
		<%
			}
		%>
		<td class="detaillabel" style="font-size:18px"><yfc:i18n>FS_Order_Acct_Code</yfc:i18n></td>
		<%
			if(strNode.equals("NWCG")) {
		%>
				<td nowrap="true">
					<input type="text" style="height:24px; font-size:18px" class="protectedinput" size=45 maxLength=40 readonly="true" id="xml:/OrganizationList/Organization/Extn/@ExtnFSTransferOrderAcctCode" <%=getTextOptions("xml:/OrganizationList/Organization/Extn/@ExtnFSTransferOrderAcctCode")%>/>
				</td>
		<%
			} else {
		%>
				<td>
					<input type="text" style="height:24px; font-size:18px" readonly="true" class="protectedinput" size=45 maxLength=40 <%=getTextOptions("xml:/OrganizationList/Organization/Extn/@ExtnFSTransferOrderAcctCode",StringUtil.nonNull(elemExtn.getAttribute("ExtnFSTransferOrderAcctCode")))%>/>
				</td>
		<%
			}
		%>
	</tr>
	<tr>
		<td class="detaillabel" style="font-size:18px"><yfc:i18n>FS_Order_Override_Code</yfc:i18n></td>
		<%
			if(strNode.equals("NWCG")) {
		%>
				<td nowrap="true">
					<input type="text" style="height:24px; font-size:18px" class="protectedinput" size=45 maxLength=40 readonly="true" id="xml:/OrganizationList/Organization/Extn/@ExtnFSTransferOrderOverrideCode" <%=getTextOptions("xml:/OrganizationList/Organization/Extn/@ExtnFSTransferOrderOverrideCode")%>/>
				</td>
		<%
			} else {
		%>
				<td>
					<input type="text" style="height:24px; font-size:18px" readonly="true" class="protectedinput" size=4 maxLength=4 <%=getTextOptions("xml:/OrganizationList/Organization/Extn/@ExtnFSTransferOrderOverrideCode",StringUtil.nonNull(elemExtn.getAttribute("ExtnFSTransferOrderOverrideCode")))%>/>
				</td>	
		<%
			}
		%>	
		<td class="detaillabel" style="font-size:18px"><yfc:i18n>Requested_Delivery_Date</yfc:i18n></td>
		<td>
			<input type="hidden" <%=getTextOptions("xml:/Order/@ReqDeliveryDate")%>/>
			<input type="text" style="height:24px; font-size:18px" class="unprotectedinput" onBlur="setRequestDeliverDate(this);setExtnReqDelDateInDiffPanels(this, 'xml:/Order/Extn/@TestExtnReqDeliveryDate_YFCDATE')" <%=getTextOptions("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCDATE")%>/>
			<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
			<input type="text" style="height:24px; font-size:18px" class="unprotectedinput"  onBlur="setExtnReqDelDateInDiffPanels(this, 'xml:/Order/Extn/@TestExtnReqDeliveryDate_YFCTIME')" <%=getTextOptions("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCTIME")%>/>
			<img class="lookupicon" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
			<input type="hidden" <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ExpectedDate_YFCDATE", getTodayDate())%>/>
			<input type="hidden" <%=getTextOptions("xml:/Order/OrderDates/OrderDate/@ExpectedDate_YFCTIME", getCurrentTime())%>/>
		</td>
	</tr>
<% 
		if(isExchangeOrderCreation) { 
%>
			<td class="detaillabel" ><yfc:i18n>Exchange_Type</yfc:i18n></td>
			<td>
				<select name="xml:/Order/@ExchangeType" class="combobox">
					<yfc:loopOptions binding="xml:ExchangeTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/Order/@ExchangeType" isLocalized="Y"/>
				</select>
			</td>
<% 
		} 
%>
	</tr>
</table>