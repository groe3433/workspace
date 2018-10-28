<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@include file="/console/jsp/order.jspf" %>
<%@ include file="/console/jsp/paymentutils.jspf" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript">
function setOrderCompleteFlag(value){
	var oOrderComplete = document.all("xml:/Order/@OrderComplete");
	if (oOrderComplete != null)
		oOrderComplete.value = value;
}
function refreshSearchScreen(controlObj) {
    if (yfcHasControlChanged(controlObj)) {
        changeSearchView(getCurrentSearchViewId());
	}
}
function setByAddressSearchCriteria(){
    
    var radAddress = document.all("RadAddress");
    var billToKey = document.all("xml:/Order/SearchByAddress/@BillToKey");
    var shipToKey = document.all("xml:/Order/SearchByAddress/@ShipToKey");
    var personInfoKey = document.all("xml:/PersonInfo/@PersonInfoKey");
	var addressValue = "B";
	if(radAddress[1].checked)
		addressValue = "S";
	if(radAddress[2].checked)
		addressValue = "E";

	billToKey.value = "";
	shipToKey.value = "";
	if(personInfoKey.value != null){
		if (addressValue == "B" || addressValue == "E") {
			billToKey.value = personInfoKey.value;
		}
		if (addressValue == "S" || addressValue == "E") {
			shipToKey.value = personInfoKey.value;
		}
	}
}
</script>

<%
	String bReadFromHistory = resolveValue("xml:/Order/@ReadFromHistory");
    if (isVoid(bReadFromHistory) ) {
        bReadFromHistory = "N";
    }
	String sOrderComplete = resolveValue("xml:/Order/@OrderComplete");
    if (isVoid(sOrderComplete) && "N".equals(bReadFromHistory)   ) { // If values of radio buttons gets changed, this condition need to be revisited.
        sOrderComplete = "N";
    }

	//processing for the address lookup and address keys
	
	String billToKey = resolveValue("xml:/Order/SearchByAddress/@BillToKey");
	String shipToKey = resolveValue("xml:/Order/SearchByAddress/@ShipToKey");
	String radAddress = "";

	String personInfoKey ="";
	if(!isVoid(billToKey) && !isVoid(shipToKey)){
		radAddress = "E";
		personInfoKey = billToKey; 	
	}
	else if(!isVoid(billToKey)){
		radAddress = "B";
		personInfoKey = billToKey; 	
	}
	else if(!isVoid(shipToKey)){
		radAddress = "S";
		personInfoKey = shipToKey; 	
	}

	//set the address radiobuttons
	if(isVoid(radAddress)){
		radAddress = "E";
	}
	
	YFCElement personInfoElement = (YFCElement)request.getAttribute("PersonInfo");
	if(personInfoElement == null){
		personInfoElement = YFCDocument.createDocument("PersonInfo").getDocumentElement();
		request.setAttribute("PersonInfo",personInfoElement);
	}
	personInfoElement.setAttribute("PersonInfoKey", personInfoKey);
%>
<table class="view">
    <tr>
        <td>
            <input type="hidden" name="xml:/Order/@StatusQryType" value="BETWEEN"/>
            <input type="hidden" name="xml:/Order/@DraftOrderFlag" value="N"/>
            <input type="hidden" <%=getTextOptions("xml:/PersonInfo/@PersonInfoKey", personInfoKey)%>/>
            <input type="hidden" name="xml:/Order/SearchByAddress/@BillToKey" value="<%=billToKey%>"/>
            <input type="hidden" name="xml:/Order/SearchByAddress/@ShipToKey" value="<%=shipToKey%>"/>
        </td>
    </tr>

    <jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
        <jsp:param name="RefreshOnDocumentType" value="true"/>
        <jsp:param name="RefreshOnEnterpriseCode" value="true"/>
    </jsp:include>
    <% 
		if(!isVoid(personInfoKey)){
	%>
			<yfc:callAPI apiID="AP2"/>
    <%
		}
    %>
<!-- CR 285 -->
<tr>
    <td class="searchlabel" ><yfc:i18n>Cache_ID</yfc:i18n></td>
</tr>

<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/Location/@NodeQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/Location/@NodeQryType"/>
        </select>
		<!--  default cache id when page loads -->
			<input value='<%=getValue("CurrentUser","xml:/User/@Node")%>' type="text" class="unprotectedinput" 
			<%=getTextOptions("xml:/Order/@ShipNode")%> />
		<!-- END default cache id when page loads END -->
		<img class="lookupicon" onclick="callLookup(this,'shipnode')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Node")%>/>
    </td>
</tr>
<!-- CR 285 END -->

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
			<yfc:i18n>Buyer</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td nowrap="true" class="searchcriteriacell">
			<select name="xml:/Order/@BillToIDQryType" class="combobox">
				<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
				value="QueryType" selected="xml:/Order/@BillToIDQryType"/>
			</select>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@BillToID")%>/>
			<img class="lookupicon" name="search" onclick="callLookup(this,'NWCGCustomerLookUp')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Customer")%>/>
		</td>
	</tr>
	<% String enterpriseCode = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
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
        <td class="searchlabel" >
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell" nowrap="true" >
            <select name="xml:/Order/OrderLine/Item/@ItemIDQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/OrderLine/Item/@ItemIDQryType"/>
            </select>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/OrderLine/Item/@ItemID")%>/>
            <% String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>
            <img class="lookupicon" name="search" 
			onclick="callItemLookup('xml:/Order/OrderLine/Item/@ItemID','xml:/Order/OrderLine/Item/@ProductClass','xml:/Order/OrderLine/@OrderingUOM',
			'item','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item") %> />
        </td>
    </tr>
	<tr>
		<td class="searchlabel">
			<fieldset>
				<legend class="searchlabel"><yfc:i18n>Address</yfc:i18n>
					<img class="lookupicon" onclick="callAddressLookup('xml:/PersonInfo/@PersonInfoKey','addresslookup','');setByAddressSearchCriteria();refreshSearchScreen(document.all('xml:/PersonInfo/@PersonInfoKey'))" <%=getImageOptions(YFSUIBackendConsts.ADDRESS_DETAILS, "Search_for_Address") %> />
				</legend>
			<span class="protectedtext">
			<%			
			if(!isVoid(personInfoKey)){
			%>
			<% //display the address details %>
				<jsp:include page="/yfsjspcommon/address.jsp" flush="true">
					<jsp:param name="Path" value="xml:/PersonInfoList/PersonInfo"/>
					<jsp:param name="DataXML" value="PersonInfoList"/>
		        </jsp:include>
			</span>
			<% }else{ %>
			<br/>
			<%}%>
			<br/>
            <input type="radio" onclick="setByAddressSearchCriteria()" <%=getRadioOptions("RadAddress", radAddress, "B")%>>
                <yfc:i18n>Bill_To</yfc:i18n>
            </input>
            <input type="radio" onclick="setByAddressSearchCriteria()" <%=getRadioOptions("RadAddress", radAddress, "S")%>>
                <yfc:i18n>Ship_To</yfc:i18n>
            </input>
            <input type="radio" onclick="setByAddressSearchCriteria()" <%=getRadioOptions("RadAddress", radAddress, "E")%>>
                <yfc:i18n>Either</yfc:i18n>
            </input>
			</fieldset>
		</td>
	</tr>
	<tr>
<td class="searchlabel" >
<yfc:i18n>Incident_Name</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true">
<select name="xml:/Order/Extn/@ExtnIncidentNameQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/Extn/@ExtnIncidentNameQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentName")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Incident_FsAcct_Code</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true">
<select name="xml:/Order/Extn/@ExtnFsAcctCodeQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/Extn/@ExtnFsAcctCodeQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/Extn/@ExtnFsAcctCode")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Incident_BlmAcct_Code</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true">
<select name="xml:/Order/Extn/@ExtnBlmAcctCodeQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/Extn/@ExtnBlmAcctCodeQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/Extn/@ExtnBlmAcctCode")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Incident_OtherAcct_Code</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true">
<select name="xml:/Order/Extn/@ExtnOtherAcctCodeQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/Extn/@ExtnOtherAcctCodeQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/Extn/@ExtnOtherAcctCode")%>/>
</td>
</tr>

    <tr>
        <td  class="searchlabel">
			<yfc:i18n>CarrierService</yfc:i18n>
		</td>
	</tr>
    <tr>
        <td class="searchcriteriacell">
            <select class="combobox" name="xml:/Order/OrderLine/@CarrierServiceCode">
                <yfc:loopOptions binding="xml:/CarrierServiceList/@CarrierService" 
                    name="CarrierServiceDesc" value="CarrierServiceCode" selected="xml:/Order/OrderLine/@CarrierServiceCode" isLocalized="Y"/>
            </select>
        </td>
    </tr>
	<tr>
        <td class="searchlabel" >
            <yfc:i18n>Issue_State</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell">
			<input type="radio" onclick="setOrderCompleteFlag('N')" <%=getRadioOptions("xml:/Order/@ReadFromHistory", bReadFromHistory, "N")%>><yfc:i18n>Open</yfc:i18n> 
			<input type="radio" onclick="setOrderCompleteFlag(' ')"  <%=getRadioOptions("xml:/Order/@ReadFromHistory", bReadFromHistory, "NO")%>><yfc:i18n>Recent</yfc:i18n><!-- The use of 'NO' is done intentionally, getOrderList API returns history orders only if ReadFromHistory =='Y'  -->
			<input type="radio" onclick="setOrderCompleteFlag(' ')" <%=getRadioOptions("xml:/Order/@ReadFromHistory", bReadFromHistory, "Y")%>><yfc:i18n>History</yfc:i18n>
			<input type="radio" onclick="setOrderCompleteFlag(' ')" <%=getRadioOptions("xml:/Order/@ReadFromHistory", bReadFromHistory, "B")%>><yfc:i18n>All</yfc:i18n>
			<input type="hidden" name="xml:/Order/@OrderComplete" value="<%=sOrderComplete%>"/>
        </td>
    </tr>
	<tr>
		<td class="searchlabel">
			<yfc:i18n>Selecting_All_may_be_slow</yfc:i18n>
		</td>
	</tr>
</table>