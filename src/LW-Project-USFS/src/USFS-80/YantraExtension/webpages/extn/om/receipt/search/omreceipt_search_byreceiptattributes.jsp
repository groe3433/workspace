<%@include file="/yfsjspcommon/yfsutil.jspf"%> 
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.StringUtil" %>
<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<table class="view">
<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
  <jsp:param name="ShowDocumentType" value="true"/>
  <jsp:param name="DocumentTypeBinding" value="xml:/Receipt/Shipment/@DocumentType"/>
  <jsp:param name="RefreshOnDocumentType" value="true"/>
  <jsp:param name="EnterpriseCodeBinding" value="xml:/Receipt/Shipment/@EnterpriseCode"/>
  <jsp:param name="RefreshOnEnterpriseCode" value="true"/>
</jsp:include>

<yfc:callAPI apiID="AP2"/>

<!-- Begin CR383 -->
     <yfc:callAPI apiID="AP3"/>
<!-- End CR383 -->
<!--Begin CR383 <input type="hidden" <%=getTextOptions("xml:/Receipt/Extn/@ExtnIsReturnReceipt","N")%>/>  End CR383-->

<tr> 
    <td>
        <input type="hidden" name="xml:yfcSearchCriteria:/Receipt/@ReceiptDateQryType" value="DATERANGE"/>
    </td>
</tr> 
<tr> 
	<td class="searchlabel" ><yfc:i18n>Receiving_Node</yfc:i18n></td>
</tr>
<tr>
	<td class="protectedtext" nowrap="true">
		<% if(isShipNodeUser()) {%>
			<%=getValue("CurrentUser","xml:/User/@Node")%>
			<input type="hidden" <%=getTextOptions("xml:/Receipt/@ReceivingNode", "xml:CurrentUser:/User/@Node")%>/>
		<%} else { %>
			<select name="xml:yfcSearchCriteria:/Receipt/@ReceivingNodeQryType" class="combobox" >
				<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
					name="QueryTypeDesc" value="QueryType" selected="xml:yfcSearchCriteria:/Receipt/@ReceivingNodeQryType"/>
	        </select>
	 	    <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@ReceivingNode")%>/>
			<% String extraParams = getExtraParamsForTargetBinding("xml:/Organization/EnterpriseOrgList/OrgEnterprise/@EnterpriseOrganizationKey", getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>
			<img class="lookupicon" onclick="callLookup(this,'shipnode','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Receiving_Node") %>/>
		<%}%>
    </td>
</tr>

<tr> 
    <td class="searchlabel" ><yfc:i18n>Receiving_Dock</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@ReceivingDock")%> />
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Receipt_#</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" <%=getComboOptions("xml:/Receipt/@ReceiptNoQryType")%>>
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Receipt/@ReceiptNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@ReceiptNo")%> />
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Buyer</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" <%=getComboOptions("xml:/Receipt/Shipment/@BillToCustomerIdQryType")%>>
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Receipt/Shipment/@BillToCustomerIdQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@BillToCustomerId")%> />
	    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Seller</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" <%=getComboOptions("xml:/Receipt/Shipment/@SellerOrganizationCodeQryType")%>>
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Receipt/Shipment/@SellerOrganizationCodeQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@SellerOrganizationCode")%> />
		<% String enterpriseCode = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
		<% String sDocType = getValue("CommonFields", "xml:/CommonFields/@DocumentType");%>
        <img class="lookupicon" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCode%>','<%=sDocType%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Incident Number</yfc:i18n></td>
</tr>    
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" <%=getComboOptions("xml:/Receipt/Extn/@ExtnIncidentNoQryType")%>>
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Receipt/Extn/@ExtnIncidentNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" maxlength=40 size=20 <%=getTextOptions("xml:/Receipt/Extn/@ExtnIncidentNo")%>  />
		<img class="lookupicon" onclick="callLookup(this,'NWCGIncidentLookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Incident") %> />
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Order_#</yfc:i18n></td>
</tr>    
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" <%=getComboOptions("xml:/Receipt/Shipment/@OrderNoQryType")%>>
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Receipt/Shipment/@OrderNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@OrderNo")%> />
    </td>
</tr>

<!-- Begin CR383 -->
<tr>
    <td class="searchlabel" ><yfc:i18n>Issue_Type</yfc:i18n></td>
</tr>    
<tr>
   <td class="searchcriteriacell">
          <select class=combobox <%=getComboOptions("xml:/Receipt/Shipment/@OrderType")%>>
	     <yfc:loopOptions binding="xml:IROTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/Receipt/Shipment/@OrderType" isLocalized="Y" />
          </select>
  </td>
</tr> 
<!-- End CR383 -->

<tr>
    <td class="searchlabel" ><yfc:i18n>Shipment_#</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" <%=getComboOptions("xml:/Receipt/Shipment/@ShipmentNoQryType")%>>
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Receipt/Shipment/@ShipmentNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@ShipmentNo")%> />
    </td>
</tr>
<tr>
    <td class="searchlabel" >
        <yfc:i18n>Receipt_Date</yfc:i18n>
    </td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <input class="dateinput" type="text" <%=getTextOptions("xml:/Receipt/@FromReceiptDate")%> />
        <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
        <yfc:i18n>To</yfc:i18n>
        <input class="dateinput" type="text" <%=getTextOptions("xml:/Receipt/@ToReceiptDate")%> />
        <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
    </td>
</tr>
<tr>
    <td  class="searchlabel" ><yfc:i18n>Receipt_Status</yfc:i18n></td>
</tr>
<tr>
	<td class="searchcriteriacell">
		<select  class="combobox" <%=getComboOptions("xml:/Receipt/@Status")%>>
			<yfc:loopOptions binding="xml:Status:/StatusList/@Status" name="StatusName" value="Status" selected="xml:/Receipt/@Status" isLocalized="Y"/>
        </select>
	</td>
</tr>
<tr>
<td class="searchcriteriacell">
	<input type="checkbox" <%=getCheckBoxOptions("xml:/Receipt/@OpenReceiptFlag", "xml:/Receipt/@OpenReceiptFlag", "N")%> yfcCheckedValue='N' yfcUnCheckedValue=''><yfc:i18n>Closed_Receipts_Only</yfc:i18n></input>
</td>
</tr>

</table>