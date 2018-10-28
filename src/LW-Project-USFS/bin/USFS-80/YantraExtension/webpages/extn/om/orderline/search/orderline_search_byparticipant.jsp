<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@page import="com.yantra.shared.inv.INVConstants"%>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>

<%
String draftOrderFlag = request.getParameter("DraftOrderFlag");
if (isVoid(draftOrderFlag)) {
draftOrderFlag = "N";
}

String sItemGroupCode = request.getParameter("ItemGroupCode");
if (isVoid(sItemGroupCode) ) {
sItemGroupCode = INVConstants.ITEM_GROUP_CODE_SHIPPING;
}
%>

<table class="view">
<tr>
<td>
<input type="hidden" name="xml:/OrderLine/Order/@DraftOrderFlag" value="<%=draftOrderFlag%>"/>
<input type="hidden" name='xml:/OrderLine/@ItemGroupCode' value="<%=sItemGroupCode%>"  />
</td>
</tr>

<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
<jsp:param name="DocumentTypeBinding" value="xml:/OrderLine/Order/@DocumentType"/>
<jsp:param name="EnterpriseCodeBinding" value="xml:/OrderLine/Order/@EnterpriseCode"/>
</jsp:include>

<tr>
<td class="searchlabel" >
<yfc:i18n>Order_#</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/OrderLine/Order/@OrderNoQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/OrderLine/Order/@OrderNoQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderLine/Order/@OrderNo")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Buyer</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/OrderLine/Order/@BuyerOrganizationCodeQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/OrderLine/Order/@BuyerOrganizationCodeQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderLine/Order/@BuyerOrganizationCode")%>/>
<% String enterpriseCode = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
<img class="lookupicon" onclick="callLookupForOrder(this,'BUYER','<%=enterpriseCode%>','xml:/OrderLine/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Seller</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/OrderLine/Order/@SellerOrganizationCodeQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/OrderLine/Order/@SellerOrganizationCodeQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderLine/Order/@SellerOrganizationCode")%>/>
<img class="lookupicon" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCode%>','xml:/OrderLine/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Buyer_Account_#</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<select name="xml:/OrderLine/Order/PaymentMethod/@CustomerAccountNoQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/OrderLine/Order/PaymentMethod/@CustomerAccountNoQryType"/>
</select>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderLine/Order/PaymentMethod/@CustomerAccountNo")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>S_Number</yfc:i18n>
</td>
</tr>
<td class="searchcriteriacell" nowrap="true">
<select name="xml:/OrderLine/Extn/@ExtnRequestNoQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/OrderLine/Extn/@ExtnRequestNoQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/OrderLine/Extn/@ExtnRequestNo")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Incident_#</yfc:i18n>
</td>
</tr>
<td class="searchcriteriacell" nowrap="true">
<select name="xml:/OrderLine/Order/Extn/@ExtnIncidentNoQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/OrderLine/Order/Extn/@ExtnIncidentNoQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/OrderLine/Order/Extn/@ExtnIncidentNo")%>/>
</td>
</tr>
</table>
