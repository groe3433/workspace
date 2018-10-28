<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/orderentry.jspf" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.dom.*" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.StringUtil" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="/yantra/extn/scripts/extn.js"></script>
<script language="javascript" src="/yantra/extn/scripts/nwcg_new_orderDetails.js"></script>

<script language="Javascript" >
// adding this variable, will be used later for code formatting
var OwnerAgency = '';
OwnerAgency = '<%=resolveValue("xml:EnterpriseParticipationList:/OrganizationList/Organization/Extn/@ExtnOwnerAgency")%>' ;

IgnoreChangeNames();
yfcDoNotPromptForChanges(true);

function updateAccountCodes(elem,xmlDoc)
{
	//xmlDoc will have Output xml of NWCGUpdateAccountCodes Service
	nodes=xmlDoc.getElementsByTagName("Extn");
	if(nodes != null && nodes.length > 0)
	{
		var extn = nodes(0);
		var sreRecAccCode  = extn.getAttribute("ExtnRecvAcctCode");
		var sreShipAccCode = extn.getAttribute("ExtnShipAcctCode") ;

		if (sreRecAccCode == null)
			sreRecAccCode = '';

		if (sreShipAccCode == null)
			sreShipAccCode = '';

		document.getElementById("xml:/Order/Extn/@ExtnRecvAcctCode").value = sreRecAccCode ;
        document.getElementById("xml:/Order/Extn/@ExtnShipAcctCode").value = sreShipAccCode ;
	}
	updateAddress(elem,xmlDoc,"xml:/Order/PersonInfoBillTo","BillingPersonInfo");
}
</script>

<%
Date now = new Date();
SimpleDateFormat formatDate = 
            new SimpleDateFormat("MM/dd/yyyy");

SimpleDateFormat formatTime = 
            new SimpleDateFormat("HH:mm:ss");

String strNode = StringUtil.nonNull(resolveValue("xml:CurrentUser:/User/@Node")) ;

YFCDocument inputDoc = YFCDocument.parse("<Organization OrganizationCode=\""+strNode+"\"/>");

YFCDocument templateDoc = YFCDocument.parse("<Organization OrganizationCode=\"\"> <Extn/> <BillingPersonInfo/> </Organization>");

if(!strNode.equals("")) {%>
<yfc:callAPI apiName='getOrganizationList' inputElement='<%=inputDoc.getDocumentElement()%>' templateElement='<%=templateDoc.getDocumentElement()%>' outputNamespace='OrganizationList'/>

<% }// end if

YFCElement elemOrganizationList = (YFCElement) request.getAttribute("OrganizationList");
YFCNodeList nlExtn = null;

if(elemOrganizationList != null) {
	nlExtn = elemOrganizationList.getElementsByTagName("Extn");
}

YFCElement elemExtn = null;

if(nlExtn != null && nlExtn.getLength() == 1) {
	elemExtn = (YFCElement) nlExtn.item(0);
} else {
	// assigning the list output just to avoid the null pointer check all the time
	// we can also create and assign a new element
	elemExtn = elemOrganizationList ;
}

// Default the enterprise code if it is not passed
String enterpriseCode = (String) request.getParameter("xml:/Order/@EnterpriseCode");
if (isVoid(enterpriseCode)) {
enterpriseCode = getValue("CurrentOrganization", "xml:CurrentOrganization:/Organization/@PrimaryEnterpriseKey");
request.setAttribute("xml:/Order/@EnterpriseCode", enterpriseCode);
}

// Default the buyer to logged in organization if it plays a role of buyer
String buyerOrgCode = (String) request.getParameter("xml:/Order/@BuyerOrganizationCode");
if (isVoid(buyerOrgCode)) {
	if(isRoleDefaultingRequired((YFCElement) request.getAttribute("CurrentOrgRoleList"))){
		buyerOrgCode = getValue("CurrentOrganization", "xml:CurrentOrganization:/Organization/@OrganizationCode");
	}
}

String orderHeaderKeyVal = resolveValue("xml:/Order/@OrderHeaderKey");
%>

<script language="javascript">
<% if (!isVoid(orderHeaderKeyVal)) {
YFCDocument orderDoc = YFCDocument.createDocument("Order");
orderDoc.getDocumentElement().setAttribute("OrderHeaderKey",resolveValue("xml:/Order/@OrderHeaderKey"));
%>
showDetailFor('<%=orderDoc.getDocumentElement().getString(false)%>');
<% } %>
</script>

<table class="view" width="100%">
<tr>
	<td>
		<input type="hidden" name="xml:/Order/@DraftOrderFlag" value="Y"/>
		<input type="hidden" name="xml:/Order/@EnteredBy" value="<%=resolveValue("xml:CurrentUser:/User/@Loginid")%>"/>
	</td>
</tr>

<jsp:include page="/extn/yfsjspcommon/common_fields.jsp" flush="true">
<jsp:param name="ScreenType" value="detail"/>
<jsp:param name="RefreshOnDocumentType" value="true"/>
<jsp:param name="RefreshOnEnterpriseCode" value="true"/>
<jsp:param name="ExcludeCTDocumentType" value="true"/>
</jsp:include>

<% // Now call the APIs that are dependent on the common fields (Doc Type & Enterprise Code) %>
<yfc:callAPI apiID="AP1"/>
<yfc:callAPI apiID="AP2"/>

<% String enterpriseCodeFromCommon = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>

<tr>
<td class="detaillabel" ><yfc:i18n>Seller</yfc:i18n></td>
<td nowrap="true" >
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@SellerOrganizationCode")%>/>
<img class="lookupicon" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCodeFromCommon%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
</td>
<td class="detaillabel"><yfc:i18n>PO_Number</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@OrderNo")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Requisition_#</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" size="32" <%=getTextOptions("xml:/Order/Extn/@ExtnRequisitionNo","xml:/Order/Extn/@ExtnRequisitionNo")%>/>
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Order_Date</yfc:i18n></td>
	<td nowrap="true">
	<!--<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/@OrderDate",formatDate.format(now))%>/>
	<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/> -->
	<!-- Timestamp added for 8.0 Upgrade - GN - 04/13/09 -->
	<!--
	<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/@OrderDate_YFCDATE",formatDate.format(now))%>/>
	<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
	<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/@OrderDate_YFCTIME",formatTime.format(now))%>/>
	<img class="lookupicon" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
	-->
	<!-- TOP of CR 578 User Locale Consideration -->
	<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/@OrderDate_YFCDATE", getTodayDate())%>/>
	<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
	<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/@OrderDate_YFCTIME", getCurrentTime())%>/>
	<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %>/>
	<!-- BOTTOM of CR 578 User Locale Consideration -->
</td>
<td class="detaillabel"><yfc:i18n>OrderName</yfc:i18n></td>
<td>
	<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@OrderName")%>/>
</td>
<td class="detaillabel" ><yfc:i18n>Currency</yfc:i18n></td>
<td>
	<%
		String selectedValue = resolveValue("xml:/CurrencyList/@Currency");
		if(selectedValue == null || selectedValue.equals("")) {
			selectedValue= "USD";
		}
	%>
	<!-- CR 539: remove the combobox and default currency to USD -->
		<input type="text" class="protectedinput" <%=getTextOptions("xml:/Order/PriceInfo/@Currency","xml:/CurrencyList/@Currency",selectedValue)%>/>

	<!--<select class="combobox" <%=getComboOptions("xml:/Order/PriceInfo/@Currency")%>>
	<yfc:loopOptions binding="xml:/CurrencyList/@Currency" name="CurrencyDescription" value="Currency" selected="<%=selectedValue%>" isLocalized="Y"/>
	</select>-->
</td>
</tr>

<%
YFCElement elem = (YFCElement) request.getAttribute("AccountCodeCommonCodeList");
String strFundCode = "", strObjectClass = "";
if(elem != null) {
	Iterator itr = elem.getChildren();
	if(itr != null)	{
		while(itr.hasNext()) {
			YFCElement child = (YFCElement) itr.next();
			String strDesc = child.getAttribute("CodeShortDescription");
			if(strDesc.equals("FUND_CODE"))	{
				strFundCode = child.getAttribute("CodeValue");
			} else if(strDesc.equals("OBJECT_CLASS")) {
				strObjectClass = child.getAttribute("CodeValue");
			}
		}
	}
}
%>

<tr>
<td class="detaillabel" ><yfc:i18n>Priority_Code</yfc:i18n></td>
<td nowrap="true">
	<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/Extn/@ExtnPriorityCode")%>/>
</td>
<td class="detaillabel"><yfc:i18n>Receiving_Cache</yfc:i18n></td>
<td>
	<input type="text" class="unprotectedinput" onblur="javascript:fetchDataFromServer(this,'getOrganizationAccountCode',updateAccountCodes);"   <%=getTextOptions("xml:/Order/@ReceivingNode","xml:CurrentUser:/User/@Node")%>/>
	<img class="lookupicon" onclick="callLookup(this,'shipnode')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Receiving_Node")%>/>
</td>
</td>
<td class="detaillabel"><yfc:i18n>Shipping_Account_Code</yfc:i18n></td>
<td>
	<input type="text" class="unprotectedinput" size="50" maxLength=40 <%=getTextOptions("xml:/Order/Extn/@ExtnShipAcctCode",StringUtil.nonNull(elemExtn.getAttribute("ExtnShipAcctCode")))%>/>
</td>
</tr>

<tr>
<td class="detaillabel"><yfc:i18n>SA_Override_Code</yfc:i18n></td>
<td>
	<input type="text" class="unprotectedinput" size=10 maxLength=4 onBlur="validateOverrideCodeLength(this);" <%=getTextOptions("xml:/Order/Extn/@ExtnSAOverrideCode",StringUtil.nonNull(elemExtn.getAttribute("ExtnSAOverrideCode")))%>/>
</td>

<td class="detaillabel"><yfc:i18n>Receiving_Account_Code</yfc:i18n></td>
<td>
	<!--<input type="text" class="unprotectedinput" size=50 maxLength=40 onblur="javascript:if(OwnerAgency =='BLM'){formatAccountCode(this,true,'<%=strFundCode%>','<%=strObjectClass%>');return true;}"
	<%=getTextOptions("xml:/Order/Extn/@ExtnRecvAcctCode",StringUtil.nonNull(elemExtn.getAttribute("ExtnRecvAcctCode")))%>/>-->
	<input type="text" class="unprotectedinput" size="50" maxLength="40" <%=getTextOptions("xml:/Order/Extn/@ExtnRecvAcctCode",StringUtil.nonNull(elemExtn.getAttribute("ExtnRecvAcctCode")))%>/>
</td>

<td class="detaillabel"><yfc:i18n>RA_Override_Code</yfc:i18n></td>
<td>
<input type="text"  class="unprotectedinput" size='10' onBlur="validateOverrideCodeLength(this);" <%=getTextOptions("xml:/Order/Extn/@ExtnRAOverrideCode",StringUtil.nonNull(elemExtn.getAttribute("ExtnRAOverrideCode")))%>/>
</td>
</tr>
<tr>

<td class="detaillabel"><yfc:i18n>Customer_Account_Code</yfc:i18n></td>
<td>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnCustAcctCode",StringUtil.nonNull(elemExtn.getAttribute("ExtnCustAcctCode")))%>/>
</td>

<td class="detaillabel" ><yfc:i18n>Order Purpose</yfc:i18n></td>
<td>
<select class="combobox" 
<%=getComboOptions("xml:/Order/Extn/@ExtnOrderPurpose")%>>
<yfc:loopOptions
binding="xml:OrderPurpose:/CommonCodeList/@CommonCode"
name="CodeValue"
value="CodeValue" selected="xml:/Order/Extn/@ExtnOrderPurpose"/>
</select>
</td>
<td class="detaillabel" ><yfc:i18n>Transportation Requirements</yfc:i18n></td>
<td>
<select class="combobox" 
<%=getComboOptions("xml:/Order/Extn/@ExtnTransportationMethod")%>>
<yfc:loopOptions
binding="xml:TransportReq:/CommonCodeList/@CommonCode"
name="CodeValue"
value="CodeValue" selected="xml:/Order/Extn/@ExtnTransportationMethod"/>
</select>
</td>

<tr>
<td class="detaillabel" ><yfc:i18n>Requested_Delivery_Date</yfc:i18n></td>
<td>
<input type="text" class="dateinput" <%=getTextOptions("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCDATE",getTodayDate())%>/>
<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
<input type="text" class="dateinput" <%=getTextOptions("xml:/Order/Extn/@ExtnReqDeliveryDate_YFCTIME",getCurrentTime())%>/>
<img class="lookupicon" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup")%>/>
</td>
</tr>

<input type="hidden" name="xml:/Order/Extn/@ExtnOtherAmount" value="0.0"/>
<input type="hidden" name="xml:/Order/Extn/@ExtnBlmAmount" value="0.0"/>
<input type="hidden" name="xml:/Order/Extn/@ExtnFsAmount" value="0.0"/>

</tr>
</table>