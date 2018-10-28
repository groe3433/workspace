<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/orderentry.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.yantra.yfc.dom.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/extn.js"></script>
<script language="Javascript" >	

IgnoreChangeNames();
yfcDoNotPromptForChanges(true);

function validateIncidentNoAndYear() {
	var sFROMIncidentNo = document.getElementById("xml:/Order/Extn/@ExtnIncidentNo").value.replace(/(^\s*)|(\s*$)/gi, "");	
	var sFROMIncidentYear = document.getElementById("xml:/Order/Extn/@ExtnIncidentYear").value.replace(/(^\s*)|(\s*$)/gi, "");	
	var sTOIncidentNo = document.getElementById("xml:/Order/Extn/@ExtnToIncidentNo").value.replace(/(^\s*)|(\s*$)/gi, "");	
	var sTOIncidentYear = document.getElementById("xml:/Order/Extn/@ExtnToIncidentYear").value.replace(/(^\s*)|(\s*$)/gi, "");
	if(sFROMIncidentNo == "" || sFROMIncidentYear == "") {
		alert("Please Enter FROM Incident Number and Year!!!");
		return false;
	}
	if(sTOIncidentNo == "" || sTOIncidentYear == "") {
		alert("Please Enter TO Incident Number and Year!!!");
		return false;
	}
	var sFROMIncidentName = document.getElementById("xml:/Order/Extn/@ExtnIncidentName").value.replace(/(^\s*)|(\s*$)/gi, "");
	if(sFROMIncidentName == "") {
		alert("After entering or selecting a FROM Incident Number, TABOUT of the FROM Incident Number field!!!");
		return false;
	}
	var sTOIncidentName = document.getElementById("xml:/Order/Extn/@ExtnToIncidentName").value.replace(/(^\s*)|(\s*$)/gi, "");
	if(sTOIncidentName == "") {
		alert("After entering or selecting a TO Incident Number, TABOUT of the TO Incident Number field!!!");
		return false;
	}
	
	<!-- BEGIN - CR 1750 - Aug 12, 2015 -->
	var sOrganizationKey = document.getElementById("xml:/Order/@ShipNode").value.replace(/(^\s*)|(\s*$)/gi, "");
	if(sOrganizationKey == 'IDGBK' || sOrganizationKey == 'AKAKK' || sOrganizationKey == 'MTBFK') {
		var sToBLMAccCode = document.getElementById("xml:/Order/Extn/@ExtnToBlmAcctCode").value.replace(/(^\s*)|(\s*$)/gi, "");
		if(sToBLMAccCode == null || sToBLMAccCode.replace(/(^\s*)|(\s*$)/gi, "") == ".." || sToBLMAccCode.replace(/(^\s*)|(\s*$)/gi, "") == "") {
			alert('You are logged in at a BLM Cache (' + sOrganizationKey + ') and the TO Incident Number/Year (' + sTOIncidentNo + '/' + sTOIncidentYear + ') that you selected does not have a valid BLM Account Code! \nIf you need to transfer items using this incident number/year you will need to make sure it has a valid BLM Account Code (and/or update it appropriately).');
			return false;
		} 
		// Validate BLM Account Code
		// 40 = LL00000009.L00005000.000005.LFSPH0KS0000 (with Cost Center and Functional Area and WBS)
		// 28 = LL00000009.L00005000.000005. (with Cost Center and Functional Area)
		if(sToBLMAccCode.length != 40 && sToBLMAccCode.length != 28) {
			alert ("The BLM Account Code on this incident is incorrect. Either make sure it has (Cost Center + Functional Area), OR (Cost Center + Functional Area + WBS). ");
			return false;
		} 
	} 
	if(sOrganizationKey == 'IDCDK') {
		var sToOtherAccCode = document.getElementById("xml:/Order/Extn/@ExtnToOtherAcctCode").value.replace(/(^\s*)|(\s*$)/gi, "");
		if(sToOtherAccCode == null || sToOtherAccCode.replace(/(^\s*)|(\s*$)/gi, "") == "") {
			alert('You are logged in at a State Cache (' + sOrganizationKey + ') and the TO Incident Number/Year (' + sTOIncidentNo + '/' + sTOIncidentYear + ') that you selected does not have a valid Other Account Code! \nIf you need to transfer items using this incident number/year you will need to make sure it has a valid Other Account Code (and/or update it appropriately).');
			return false;
		} 			
	}
	if(sOrganizationKey == 'MTNRK' || sOrganizationKey == 'ORNWK' || sOrganizationKey == 'ORLGK' || sOrganizationKey == 'WAWFK' || sOrganizationKey == 'CALSK' || sOrganizationKey == 'CANCK' || sOrganizationKey == 'CORMK' || sOrganizationKey == 'NMSFK' || sOrganizationKey == 'AZPFK' || sOrganizationKey == 'KYSAK' || sOrganizationKey == 'MNNEK') {
		var sToFSAccCode = document.getElementById("xml:/Order/Extn/@ExtnToFsAcctCode").value.replace(/(^\s*)|(\s*$)/gi, "");
		var sToFSOverrideAccCode = document.getElementById("xml:/Order/Extn/@ExtnToOverrideCode").value.replace(/(^\s*)|(\s*$)/gi, "");	
		if((sToFSAccCode == null || sToFSAccCode.replace(/(^\s*)|(\s*$)/gi, "") == "") && (sToFSOverrideAccCode == null || sToFSOverrideAccCode.replace(/(^\s*)|(\s*$)/gi, "") == "")) {
			alert('You are logged in at a FS Cache (' + sOrganizationKey + ') and the TO Incident Number/Year (' + sTOIncidentNo + '/' + sTOIncidentYear + ') that you selected does not have a valid FS Account Code or Override Code! \nIf you need to transfer items using this incident number/year you will need to make sure it has a valid FS Account Code and Override Code (and/or update it appropriately).');
			return false;
		} 
	}
	<!-- END - CR 1750 - Aug 12, 2015 -->
	
	return true;
}

function setToIncidetnTransferParam(elemIncidentNo) {
	var elemYear = document.getElementById("xml:/Order/Extn/@ExtnToIncidentYear");
	var returnArray = new Object();
	// replacing them as the command will understand only incident no and year not to incident number and to year
	returnArray["xml:/Order/Extn/@ExtnIncidentNo"] = elemIncidentNo.value
	returnArray["xml:/Order/Extn/@ExtnIncidentYear"] = elemYear.value;
	return returnArray;
}

function setFromIncidetnTransferParam(elemIncidentNo) {
	var elemYear = document.getElementById("xml:/Order/Extn/@ExtnIncidentYear");
	var returnArray = new Object();
	// replacing them as the command will understand only incident no and year not to incident number and to year
	returnArray["xml:/Order/Extn/@ExtnIncidentNo"] = elemIncidentNo.value
	returnArray["xml:/Order/Extn/@ExtnIncidentYear"] = elemYear.value;
	return returnArray;
}

function check_in_cache_t_fields() {
	var elemToIncidentNo = document.getElementById("xml:/Order/Extn/@ExtnToIncidentNo");
	var elemIncidentNo = document.getElementById("xml:/Order/Extn/@ExtnIncidentNo");
	if(elemIncidentNo.value == "" || elemToIncidentNo.value == "") {
		alert("Incident No Cannot be Null !!!");
		return false;
	}
	return true;
}

</script>
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
		if(sellerOrgCode == null) {
			sellerOrgCode = "NWCG";
		}
    }
	String orderHeaderKeyVal = resolveValue("xml:/Order/@OrderHeaderKey");	
%>
<script language="javascript">
<%
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
%>
</script>
<%
	Date now = new Date();
	SimpleDateFormat formatDate = new SimpleDateFormat("MM/dd/yyyy");
	SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
%>
<table class="view" width="100%">
	<tr>
		<td>
			<!--TODO Move this value to Constants file-->
			<input type="hidden" name="xml:/Order/@DocumentType" value="0008.ex"/> 
			<input type="hidden" name="xml:/Order/@DraftOrderFlag" value="Y"/>
			<input type="hidden" name="xml:/Order/@EnteredBy" value="<%=resolveValue("xml:CurrentUser:/User/@Loginid")%>"/>
			
			<!-- BEGIN - CR 1750 - Aug 12, 2015 -->
			<input type="hidden" name="xml:/Order/@ShipNode" value="<%=resolveValue("xml:CurrentUser:/User/@OrganizationKey")%>"/>
			<!-- END - CR 1750 - Aug 12, 2015 -->
		</td>
	</tr>
	<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
		<jsp:param name="ScreenType" value="detail"/>
		<jsp:param name="ShowDocumentType" value="false"/>
		<jsp:param name="RefreshOnEnterpriseCode" value="true"/>
	</jsp:include>
	<%-- Now call the APIs that are dependent on the common fields (Doc Type & Enterprise Code) --%>
	<yfc:callAPI apiID="AP1"/>
	<yfc:callAPI apiID="AP2"/>
	<yfc:callAPI apiID="AP4"/>
	<tr>
<% 
		String enterpriseCodeFromCommon = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");
		String sLookup = "NWCGCustomerLookUp";
%>
		<td class="detaillabel" ><yfc:i18n>Seller</yfc:i18n></td>
		<td nowrap="true" >
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@SellerOrganizationCode", "xml:/Order/@SellerOrganizationCode", sellerOrgCode)%>/>
			<img class="lookupicon" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCodeFromCommon%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
		</td>
		<td class="detaillabel" ><yfc:i18n>Billing_Doc</yfc:i18n></td>
		<td nowrap="true" >
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnBillingDoc")%>/>
		</td>
	</tr>
	<tr>
		<td class="detaillabel" ><yfc:i18n>Order_Type</yfc:i18n></td>
		<td>
			<select class="combobox" <%=getComboOptions("xml:/Order/@OrderType")%>>
				<yfc:loopOptions binding="xml:OrderTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="ICBSR_INIT_TRANSFER" isLocalized="Y"/>
			</select>
		</td>
		<!-- CR 321 ks 2008-09-29 -->
		<td class="detaillabel" ><yfc:i18n>Order_Date</yfc:i18n></td>
		<td>
			<input type="hidden" name="xml:/Order/@OrderDate"/>
			<!-- TOP of CR 578 User Locale Consideration -->
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/@OrderDate_YFCDATE","xml:/Order/@OrderDate_YFCDATE", getTodayDate())%>/>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/@OrderDate_YFCTIME","xml:/Order/@OrderDate_YFCTIME", getCurrentTime())%>/>
			<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %>/>
			<!-- BOTTOM of CR 578 User Locale Consideration -->
		</td>
		<!-- end of CR 321 -->
	</tr>
	<%-- From Incident --%>
	<tr>
		<td class="detaillabel" ><yfc:i18n>From_Incident_Year</yfc:i18n></td>
		<td>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentYear")%> id="xml:/Order/Extn/@ExtnIncidentYear" />
		</td>
		<td class="detaillabel" ><yfc:i18n>From_Incident_No</yfc:i18n></td>
		<td>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentNo")%> id="xml:/Order/Extn/@ExtnIncidentNo" onblur="javascript:fetchDataWithParams(document.getElementById('xml:/Order/Extn/@ExtnIncidentNo'),'getIncidentDetails',updateFromIncidentDetails,setFromIncidetnTransferParam(this));"/>
			<img class="lookupicon" onclick="callIncidentLookup('xml:/Order/Extn/@ExtnIncidentNo','xml:/Order/Extn/@ExtnIncidentYear','NWCGIncidentLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Incident") %> />
		</td>
		<td class="detaillabel" ><yfc:i18n>From_Incident_Name</yfc:i18n></td>
		<td>
			<input type="text" class="protectedinput" readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentName")%> />
		</td>
	</tr>
	<%-- The account codes --%>
	<tr>
		<td class="detaillabel" ><yfc:i18n>From_Incident_Phone_No</yfc:i18n></td>
		<td nowrap="true">
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/Extn/@ExtnPhoneNo")%>/>
		</td>
		<td class="detaillabel" ><yfc:i18n>From_Incident_FsAcctCode</yfc:i18n></td>
		<td>
			<input type="text" class="protectedinput"  readonly="true"  <%=getTextOptions("xml:/Order/Extn/@ExtnFsAcctCode")%>/>
		</td>
		<td class="detaillabel" ><yfc:i18n>From_Incident_OverrideCode</yfc:i18n></td>
		<td>
			<input type="text" class="protectedinput"  readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnOverrideCode")%>/>
		</td>
	</tr>
	<tr>       
		<td class="detaillabel" ><yfc:i18n>From_Incident_BlmAcctCode</yfc:i18n></td>
		<td>
			<input type="text" readonly="true" size="50" class="protectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnBlmAcctCode")%>/>
		</td>
		<td class="detaillabel" ><yfc:i18n>From_Incident_OtherAcctCode</yfc:i18n></td>
		<td nowrap="true">
			<input class="protectedinput" type="text"  readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnOtherAcctCode")%>/>
		</td>
		<!-- The From Incident primary cache --> 
		<input type="hidden" name="xml:/Order/Extn/@ExtnIncidentCacheId"/>
		<!--  CR fix kjs 2008-08-07 -->
		<td class="detaillabel"><yfc:i18n>From_Incident_Type</yfc:i18n></td>
		<td> 
			<input type="hidden" class="protectedinput" readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentType")%>/>
			<input class="protectedinput" type="text" readonly="true" Size="40" <%=getTextOptions("xml:/Order/Extn/@FromIncidentType")%> />
		</td>
	</tr>
		</td>
	</tr>
	<%-- End From Incident --%>	
	<%-- To Incident --%>
	<tr>
		<td class="detaillabel" ><yfc:i18n>To_Incident_Year</yfc:i18n></td>
        <td>
            <input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/Extn/@ExtnToIncidentYear")%> id="xml:/Order/Extn/@ExtnToIncidentYear" />
        </td>
        <td class="detaillabel" ><yfc:i18n>To_Incident_No</yfc:i18n></td>
        <td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnToIncidentNo")%> id="xml:/Order/Extn/@ExtnToIncidentNo" onblur="javascript:if(this.value !=''){fetchDataWithParams(document.getElementById('xml:/Order/Extn/@ExtnToIncidentNo'),'getIncidentDetails',updateToIncidentDetails,setToIncidetnTransferParam(this));}"/>
            <img class="lookupicon" onclick="callIncidentLookup('xml:/Order/Extn/@ExtnToIncidentNo','xml:/Order/Extn/@ExtnToIncidentYear','NWCGIncidentLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Incident") %> />
        </td>
        <td class="detaillabel" ><yfc:i18n>To_Incident_Name</yfc:i18n></td>
        <td>
            <input type="text" class="protectedinput" readonly="true"  <%=getTextOptions("xml:/Order/Extn/@ExtnToIncidentName")%>/>
        </td>
    </tr>
    <%-- The account codes --%>
    <tr>
        <td class="detaillabel" ><yfc:i18n>To_Incident_Phone_No</yfc:i18n></td>
        <td nowrap="true">
            <input class="dateinput" type="text" <%=getTextOptions("xml:/Order/Extn/@ExtnToPhoneNo")%>/>
        </td>
        <td class="detaillabel" ><yfc:i18n>To_Incident_FsAcctCode</yfc:i18n></td>
        <td>
            <input type="text" class="protectedinput" readonly="true"  <%=getTextOptions("xml:/Order/Extn/@ExtnToFsAcctCode")%>/>
        </td>
		<td class="detaillabel" ><yfc:i18n>To_Incident_OverrideCode</yfc:i18n></td>
		<td>
			<input type="text" class="protectedinput"  readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnToOverrideCode")%>/>
		</td>
    </tr>
	<tr>
        <td class="detaillabel" ><yfc:i18n>To_Incident_BlmAcctCode</yfc:i18n></td>
        <td>
            <input type="text" readonly="true" size="50" class="protectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnToBlmAcctCode")%>/>
        </td>
        <td class="detaillabel" ><yfc:i18n>To_Incident_OtherAcctCode</yfc:i18n></td>
        <td nowrap="true">
            <input class="protectedinput" type="text" readonly="true"  <%=getTextOptions("xml:/Order/Extn/@ExtnToOtherAcctCode")%>/>
        </td>
    	<!-- The To Incident primary cache --> 
    	<input type="hidden" name="xml:/Order/Extn/@ExtnToIncidentCacheId"/>
		<td class="detaillabel"><yfc:i18n>To_Incident_Type</yfc:i18n></td>
		<td>
			<input type="hidden" class="protectedinput" readonly="true" <%=getTextOptions("xml:/Order/Extn/@ExtnToIncidentType")%>/>
			<input class="protectedinput" type="text" readonly="true" Size="40" <%=getTextOptions("xml:/Order/Extn/@ToIncidentType")%> /></td>
		</td>
	</tr>
	<%-- End To Incident --%>	
</table>