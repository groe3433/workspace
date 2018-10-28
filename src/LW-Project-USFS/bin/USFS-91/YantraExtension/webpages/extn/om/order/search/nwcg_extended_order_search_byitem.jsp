<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<table class="view">
<tr>
<td>
<input type="hidden" name="xml:/Order/@DraftOrderFlag" value="N"/>
</td>
</tr>
<!-- CR 387 KJS -->
<jsp:include page="/extn/yfsjspcommon/nwcg_common_fields.jsp" flush="true">
<jsp:param name="RefreshOnEnterpriseCode" value="true"/>
</jsp:include>
<% // Now call the APIs that are dependent on the common fields (Enterprise Code) %>
<yfc:callAPI apiID="AP2"/>
<yfc:callAPI apiID="AP3"/>
<yfc:callAPI apiID="AP5"/>
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
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/@OrderNo")%>/>
</td>
</tr>



<tr>
	<td class="searchlabel" >
		<yfc:i18n>Issue_Type</yfc:i18n>
	</td>
</tr>
<tr>
	<td nowrap="true" class="searchcriteriacell">
		<select name="xml:/Order/@OrderType" class="combobox">
			<yfc:loopOptions binding="xml:IssueTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
					value="CodeValue" selected="xml:/Order/@OrderType" isLocalized="Y"/>
		</select>
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
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/@BillToID")%>/>
<img class="lookupicon" name="search" onclick="callLookup(this,'NWCGCustomerLookUp')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Customer")%>/>
</td>
</tr>


<% String enterpriseCode = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
<tr>
<td class="searchlabel" >
<!--Jay: Commenting as per CR # 297 -->
<%--<yfc:i18n>Seller</yfc:i18n>
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
</tr>--%>
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
<!--Jay: Commenting as per CR # 297 -->
<%--
<tr>
<td class="searchlabel" >
<yfc:i18n>Product_Class</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true" >
<select name="xml:/Order/OrderLine/Item/@ProductClass" class="combobox">
<yfc:loopOptions binding="xml:ProductClassList:/CommonCodeList/@CommonCode" name="CodeValue"
value="CodeValue" selected="xml:/Order/OrderLine/Item/@ProductClass"/>
</select>
</td>
</tr>
--%>
<tr>
<td class="searchlabel" >
<yfc:i18n>Unit_Of_Measure</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true" >
<select name="xml:/Order/OrderLine/@OrderingUOM" class="combobox">
<yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure"
value="UnitOfMeasure" selected="xml:/Order/OrderLine/@OrderingUOM"/>
</select>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Item_Description</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true" >
<select name="xml:/Order/OrderLine/Item/@ItemDescQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/OrderLine/Item/@ItemDescQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/OrderLine/Item/@ItemDesc")%>/>
</td>
</tr>
<!--Jay: Commenting as per CR # 297 -->
<%--
<tr>
<td class="searchlabel" >
<yfc:i18n>Customer_Item_ID</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true">
<select name="xml:/Order/OrderLine/Item/@CustomerItemQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/OrderLine/Item/@CustomerItemQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/OrderLine/Item/@CustomerItem")%>/>
</td>
</tr>
--%>
<tr>
<td class="searchlabel" >
<yfc:i18n>PO#</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true" >
<select name="xml:/Order/Extn/@ExtnPoNoQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/Order/Extn/@ExtnPoNoQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/Extn/@ExtnPoNo")%>/>
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
<img class="lookupicon" onclick="callIncidentLookup('xml:/Order/Extn/@ExtnIncidentNo','xml:/Order/Extn/@ExtnIncidentYear','NWCGIncidentLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Incident") %> />
</td>
</tr>

<tr>
<td class="searchlabel" >
<yfc:i18n>Year</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/Extn/@ExtnIncidentYear")%>/>
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
</table>