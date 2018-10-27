<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>

<script language="javascript">

function setOrderCompleteFlag(value){
var oOrderComplete = document.all("xml:/Order/@OrderComplete");
if (oOrderComplete != null)
oOrderComplete.value = value;
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

%>


<table class="view" >
<tr>
<td>
<input type="hidden" name="xml:/Order/@StatusQryType" value="BETWEEN" OldValue=""/>
<input type="hidden" name="xml:/Order/@OrderDateQryType" value="DATERANGE" OldValue=""/>
<input type="hidden" name="xml:/Order/@DraftOrderFlag" value="N" OldValue=""/>

<input type="hidden" name="xml:/Order/OrderHoldType/@Status" value=""/>
<input type="hidden" name="xml:/Order/OrderHoldType/@StatusQryType" value="" />
</td>
</tr>

<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
<jsp:param name="RefreshOnDocumentType" value="true"/>
<jsp:param name="RefreshOnEnterpriseCode" value="true"/>
</jsp:include>
<% // Now call the APIs that are dependent on the common fields (Doc Type & Enterprise Code)
// Statuses and Hold Reason Codes are refreshed. %>
<yfc:callAPI apiID="AP2"/>
<yfc:callAPI apiID="AP3"/>

<%
if(!isTrue("xml:/Rules/@RuleSetValue") )	{
%>
<yfc:callAPI apiID="AP5"/>
<%
YFCElement listElement = (YFCElement)request.getAttribute("HoldTypeList");

YFCDocument document = listElement.getOwnerDocument();
YFCElement newElement = document.createElement("HoldType");
newElement.setAttribute("HoldType", " ");
newElement.setAttribute("HoldTypeDescription", getI18N("All_Held_Orders"));

YFCElement eFirst = listElement.getFirstChildElement();
if(eFirst != null)	{
listElement.insertBefore(newElement, eFirst);
}	else	{
listElement.appendChild(newElement);
}
request.setAttribute("defaultHoldType", newElement);
}
%>

<tr>
<td class="searchlabel" >
<yfc:i18n>Order_#</yfc:i18n>
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
<yfc:i18n>X_Order_#</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Order/OrderLine/DerivedFrom/@OrderNoQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/OrderLine/DerivedFrom/@OrderNoQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/OrderLine/DerivedFrom/@OrderNo")%>/>
<img class="lookupicon" onclick="callOrderNoLookup('xml:/Order/OrderLine/DerivedFrom/@OrderNo', null,'order', 'yfcListViewGroupId=YOML011')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Order")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Order_Date</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true">
<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/@FromOrderDate")%>/>
<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
<yfc:i18n>To</yfc:i18n>
<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/@ToOrderDate")%>/>
<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
</td>
</tr>
<tr>
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
<img class="lookupicon" onclick="callLookupForOrder(this,'BUYER','<%=enterpriseCode%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
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
<img class="lookupicon" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCode%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Order_Line_Status</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true">
<select name="xml:/Order/@FromStatus" class="combobox">
<yfc:loopOptions binding="xml:/StatusList/@Status" name="Description" value="Status" selected="xml:/Order/@FromStatus" isLocalized="Y"/>
</select>
<span class="searchlabel" ><yfc:i18n>To</yfc:i18n></span>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/Order/@ToStatus" class="combobox">
<yfc:loopOptions binding="xml:/StatusList/@Status" name="Description" value="Status" selected="xml:/Order/@ToStatus" isLocalized="Y"/>
</select>
</td>
</tr>
<%	if(isTrue("xml:/Rules/@RuleSetValue") )	{	%>
<tr>
<td class="searchcriteriacell">
<input type="checkbox" onclick="manageHoldOpts(this)" <%=getCheckBoxOptions("xml:/Order/@HoldFlag", "xml:/Order/@HoldFlag", "Y")%> yfcCheckedValue='Y' yfcUnCheckedValue='N'><yfc:i18n>Held_Orders</yfc:i18n></input>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Hold_Reason_Code</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell">
<select name="xml:/Order/@HoldReasonCode" class="combobox">
<yfc:loopOptions binding="xml:HoldReasonCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/Order/@HoldReasonCode" isLocalized="Y"/>
</select>
</td>
</tr>
<%	}	else	{	%>
<tr>
<td class="searchcriteriacell">
<input type="checkbox" <%=getCheckBoxOptions("xml:/Order/@HoldFlag", "xml:/Order/@HoldFlag", "Y")%> yfcCheckedValue='Y' yfcUnCheckedValue=' ' onclick="manageHoldOpts(this)" ><yfc:i18n>Held_Orders_With_Hold_Type</yfc:i18n></input>
</td>
</tr>
<tr>
<td>
<select resetName="<%=getI18N("All_Held_Orders")%>" onchange="resetObjName(this, 'xml:/Order/OrderHoldType/@HoldType')" name="xml:/Order/OrderHoldType/@HoldType" class="combobox" <%if(isTrue("xml:/Order/@HoldFlag") ) {%> ENABLED <%} else {%> disabled="true" <%}%> >
<yfc:loopOptions binding="xml:/HoldTypeList/@HoldType" name="HoldTypeDescription" value="HoldType" suppressBlank="Y" selected="xml:/Order/OrderHoldType/@HoldType" isLocalized="Y"/>
</select>
</td>
</tr>
<%	}	%>

<tr>
<td class="searchlabel" >
<yfc:i18n>Order_State</yfc:i18n>
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
