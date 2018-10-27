<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.nwcg.icbs.yantra.util.common.StringUtil" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<script language="javascript" src="/yantra/extn/scripts/extn.js"></script>

<table class="view">
    <tr>
        <td>
            <input type="hidden" name="xml:/Order/@OrderDateQryType" value="DATERANGE"/>
            <input type="hidden" name="xml:/Order/@ReqDeliveryDateQryType" value="DATERANGE"/>
            <input type="hidden" name="xml:/Order/@ReqShipDateQryType" value="DATERANGE"/>
            <input type="hidden" name="xml:/Order/@DraftOrderFlag" value="N"/>
            
            <input type="hidden" id="FromOrderDate" name="xml:/Order/@FromOrderDate" />
			<input type="hidden" id="ToOrderDate" name="xml:/Order/@ToOrderDate" />
			<input type="hidden" id="FromShipDate" name="xml:/Order/@FromReqShipDate" />
			<input type="hidden" id="ToShipDate" name="xml:/Order/@ToReqShipDate" />
			<input type="hidden" id="FromDeliveryDate" name="xml:/Order/@FromReqDeliveryDate" />
			<input type="hidden" id="ToDeliveryDate" name="xml:/Order/@ToReqDeliveryDate" />
        </td>
    </tr>

    <jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
    </jsp:include>

	<!-- begin modification for CR 569 -->
	<%
	String strNode = StringUtil.nonNull(resolveValue("xml:CurrentUser:/User/@Node"));
	%>
	<tr>
		<td/>
	</tr>
	<tr>
		<td class="searchlabel" >
			<yfc:i18n>Cache</yfc:i18n>
		</td>
	</tr>
	<tr>
		<td>
			<select name="xml:/Location/@NodeQryType" class="combobox" >
				<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
				name="QueryTypeDesc" value="QueryType" selected="xml:/Location/@NodeQryType"/>
			</select>
			<input type="text" name="xml:/Order/@ReceivingNode" class="unprotectedinput" value="<%=strNode%>"/>
			<img class="lookupicon" onclick="callLookup(this,'shipnode')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Node")%>/>
		</td>
	</tr>
	<!-- end modification for CR 569 -->

    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Order_#</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell" nowrap="true">
            <select name="xml:/Order/@OrderNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/@OrderNoQryType"/>
            </select>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/@OrderNo")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Buyer</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell" nowrap="true">
            <select name="xml:/Order/@BuyerOrganizationCodeQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/@BuyerOrganizationCodeQryType"/>
            </select>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/@BuyerOrganizationCode")%>/>
			<% String enterpriseCode = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
            <img class="lookupicon" name="search" onclick="callLookupForOrder(this,'BUYER','<%=enterpriseCode%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
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
        <td class="searchlabel" ><yfc:i18n>Currency</yfc:i18n></td>
    </tr>
    <tr> 
        <td nowrap="true" class="searchcriteriacell">
            <select class="combobox" name="xml:/Order/PriceInfo/@Currency">
                <yfc:loopOptions binding="xml:/CurrencyList/@Currency" name="CurrencyDescription" value="Currency" selected="xml:/Order/PriceInfo/@Currency" isLocalized="Y"/>
            </select>
        </td>
    </tr>

    <tr> 
        <td class="searchlabel" ><yfc:i18n>Total_Amount</yfc:i18n></td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select class="combobox" name="xml:/Order/PriceInfo/@TotalAmountQryType">
                <yfc:loopOptions binding="xml:/QueryTypeList/NumericQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Order/PriceInfo/@TotalAmountQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/PriceInfo/@TotalAmount")%> />
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Order_Date</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
			<input class="dateinput" id="fieldFromOrderDate" type="text" maxlength=20 size=10 onblur="addTimeStamp(this,'fieldFromOrderDate','FromOrderDate')"/>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<yfc:i18n>To</yfc:i18n>
			<input class="dateinput" id="fieldToOrderDate" type="text"  maxlength=20 size=10 onblur="addTimeStamp(this,'fieldToOrderDate','ToOrderDate')"/>
			<img class="lookupicon" name="search" onclick=invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
		</td>
    </tr>
    <tr>
	    <td class="searchlabel" >
	        <yfc:i18n>Requested_Ship_Date</yfc:i18n>
	    </td>
	</tr>
	<tr>
	    <td nowrap="true" class="searchcriteriacell">
			<input type="text" id="fieldFromShipDate" class="unprotectedinput"  maxlength=20 size=10  onblur="addTimeStamp(this,'fieldFromShipDate','FromShipDate')">
			<img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<yfc:i18n>To</yfc:i18n>
			<input type="text" id="fieldToShipDate" class="unprotectedinput"  maxlength=20 size=10 onblur="addTimeStamp(this,'fieldToShipDate','ToShipDate')" >
			<img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
		</td>
	</tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Requested_Delivery_Date</yfc:i18n>
        </td>
    </tr>
    <tr>
       <td nowrap="true" class="searchcriteriacell" >
		<input class="dateinput" id="fieldFromDeliveryDate" type="text"  maxlength=20 size=10 onblur="addTimeStamp(this,'fieldFromDeliveryDate','FromDeliveryDate')"/>
		<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
		<yfc:i18n>To</yfc:i18n>
		<input class="dateinput" id="fieldToDeliveryDate" type="text" maxlength=20 size=10  onblur="addTimeStamp(this,'fieldToDeliveryDate','ToDeliveryDate')"/>
		<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
		</td>
    </tr>
</table>
