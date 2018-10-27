<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>

<%
String draftOrderFlag = request.getParameter("DraftOrderFlag");
String listViewId = request.getParameter("yfcListViewGroupId");
if (isVoid(draftOrderFlag)) {
draftOrderFlag = "N";
}
%>

<table class="view">
<tr>
<td>
<input type="hidden" name="xml:/Order/@DraftOrderFlag" value="<%=draftOrderFlag%>"/>
<input type="hidden" name="yfcListViewGroupId" value="<%=listViewId%>"/>
</td>
</tr>

<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
<jsp:param name="ScreenType" value="search"/>
</jsp:include>

<tr>
    <td class="searchlabel" ><yfc:i18n>Shipping_Method</yfc:i18n></td>
</tr>

<tr>

	<td class="searchcriteriacell" nowrap="true">
		<select name="xml:/Order/Extn/@ExtnNavInfo" class="combobox">
			<yfc:loopOptions binding="xml:ShippingMethods:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/Order/Extn/@ExtnNavInfo" isLocalized="Y"/>
		</select>
	</td>

</tr>
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
<img class="lookupicon" name="search" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCode%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
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
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Incident_Number</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true">
<% 
String strQryType = resolveValue("xml:/Order/Extn/@ExtnIncidentNoQryType");
if(strQryType == null)
	strQryType = "" ;
boolean isNullSelected = false, isNotNullSelected = false ;
if("ISNULL".equals(strQryType))
	isNullSelected = true ;
else if ("NOTNULL".equals(strQryType))
	isNotNullSelected = true ;

%>
<select name="xml:/Order/Extn/@ExtnIncidentNoQryType" onchange="makeIncidentNumberReadOnly(this,'xml:/Order/Extn/@ExtnIncidentNo')" class="combobox">
<option value="" selected> </option>
<%
YFCElement elem = (YFCElement) request.getAttribute("QueryTypeList");
YFCElement elemStringQryType = (YFCElement) elem.getChildElement("StringQueryTypes")  ;
Iterator elemQryType = elemStringQryType.getChildren()  ;
while(elemQryType.hasNext())
{	
	YFCElement child = (YFCElement) elemQryType.next();
	String strType = 	child.getAttribute("QueryType");
	String strTypeDesc = 	child.getAttribute("QueryTypeDesc");
%>
<option value="<%=strType%>" <%=strQryType.equals(strType) ? "selected":""%> > <%=strTypeDesc%> </option>
<%}%>
<option value="ISNULL" <%= isNullSelected == true ? "selected":"" %> ><yfc:i18n>Is_Null</yfc:i18n></option>
<option value="NOTNULL" <%= isNotNullSelected == true ? "selected":"" %> ><yfc:i18n>Not_Null</yfc:i18n></option>
</select>

<input class="unprotectedinput" <%=isNullSelected || isNotNullSelected ? "readonly":""%>  type="text" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentNo")%>/>
<img class="lookupicon" onclick="callLookup(this,'NWCGIncidentLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Incident") %> />

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
<img class="lookupicon" onclick="callLookup(this,'NWCGIncidentLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Incident") %> />

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
</table>