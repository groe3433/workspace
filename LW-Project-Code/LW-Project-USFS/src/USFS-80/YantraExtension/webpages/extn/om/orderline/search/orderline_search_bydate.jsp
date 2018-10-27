<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>

<table class="view">
<tr>
<td>
<input type="hidden" name="xml:/OrderLine/Order/@OrderDateQryType" value="DATERANGE"/>
<input type="hidden" name="xml:/OrderLine/@ReqDeliveryDateQryType" value="DATERANGE"/>
<input type="hidden" name="xml:/OrderLine/@ReqShipDateQryType" value="DATERANGE"/>
<input type="hidden" name="xml:/OrderLine/Order/@DraftOrderFlag" value="N"/>
<input type="hidden" name='xml:/OrderLine/@ItemGroupCode' value='PROD'  />
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
<td class="searchcriteriacell" nowrap="true">
<select name="xml:/OrderLine/Order/@OrderNoQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/OrderLine/Order/@OrderNoQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/OrderLine/Order/@OrderNo")%>/>
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Buyer</yfc:i18n>
</td>
</tr>
<tr>
<td class="searchcriteriacell" nowrap="true">
<select name="xml:/OrderLine/Order/@BuyerOrganizationCodeQryType" class="combobox">
<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
value="QueryType" selected="xml:/OrderLine/Order/@BuyerOrganizationCodeQryType"/>
</select>
<input class="unprotectedinput" type="text" <%=getTextOptions("xml:/OrderLine/Order/@BuyerOrganizationCode")%>/>
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
<td  class="searchlabel" >
<yfc:i18n>Order_Date</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" >
<input class="dateinput" type="text" <%=getTextOptions("xml:/OrderLine/Order/@FromOrderDate")%>/>
<img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
<yfc:i18n>To</yfc:i18n>
<input class="dateinput" type="text" <%=getTextOptions("xml:/OrderLine/Order/@ToOrderDate")%>/>
<img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Requested_Ship_Date</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true" class="searchcriteriacell">
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderLine/@FromReqShipDate")%> >
<img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
<yfc:i18n>To</yfc:i18n>
<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderLine/@ToReqShipDate")%> >
<img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
</td>
</tr>
<tr>
<td class="searchlabel" >
<yfc:i18n>Requested_Delivery_Date</yfc:i18n>
</td>
</tr>
<tr>
<td nowrap="true">
<input class="dateinput" type="text" <%=getTextOptions("xml:/OrderLine/@FromReqDeliveryDate")%>/>
<img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
<yfc:i18n>To</yfc:i18n>
<input class="dateinput" type="text" <%=getTextOptions("xml:/OrderLine/@ToReqDeliveryDate")%>/>
<img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
</td>
</tr>
</table>
