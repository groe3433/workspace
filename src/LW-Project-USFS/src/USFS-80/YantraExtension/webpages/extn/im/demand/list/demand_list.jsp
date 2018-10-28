<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/order.jspf" %>

<table class="table" width="100%">
    <thead>
        <tr>
            <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/DemandDetailsList/DemandDetails/@OrderNo")%>"><yfc:i18n>Order_#</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/DemandDetailsList/DemandDetails/@DocumentType")%>"><yfc:i18n>Document_Type</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/DemandDetailsList/DemandDetails/@PrimeLineNo")%>"><yfc:i18n>Line_#</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/DemandDetailsList/DemandDetails/@BuyerOrganizationCode")%>"><yfc:i18n>Buyer</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/DemandDetailsList/DemandDetails/@SellerOrganizationCode")%>"><yfc:i18n>Seller</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/DemandDetailsList/DemandDetails/@Quantity")%>"><yfc:i18n>Quantity</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML binding="xml:/DemandDetailsList/@DemandDetails" id="DemandDetails">
            <tr>
                <yfc:makeXMLInput name="orderKey">
                    <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/DemandDetails/@OrderHeaderKey"/>
                    <yfc:makeXMLKey binding="xml:/Order/@DocumentType" value="xml:/DemandDetails/@DocumentType"/>
                </yfc:makeXMLInput>
                <% String orderKeyString = java.net.URLEncoder.encode(getParameter("orderKey")); %>
                <yfc:makeXMLInput name="orderLineKey">
                    <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/DemandDetails/@OrderLineKey"/>
                    <yfc:makeXMLKey binding="xml:/OrderLineDetail/@DocumentType" value="xml:/DemandDetails/@DocumentType"/>
                </yfc:makeXMLInput> 
                <% String orderLineKeyString = java.net.URLEncoder.encode(getParameter("orderLineKey")); %>
                <td class="tablecolumn">
					<% if(showOrderNo("DemandDetails","DemandDetails")) {%>
		                <a href="javascript:callPopupWithEntity('NWCdemand', '<%=orderKeyString%>');">
							<yfc:getXMLValue binding="xml:/DemandDetails/@OrderNo"/>
						</a>
					<%} else {%>
						<yfc:getXMLValue binding="xml:/DemandDetails/@OrderNo"/>
					<%}%>
				</td>
                <td class="tablecolumn">
                    <%=displayDocumentDescription(getValue("DemandDetails", "xml:/DemandDetails/@DocumentType"), (YFCElement) request.getAttribute("DocumentParamsList"))%>
                </td>
                <td class="tablecolumn" sortValue="<%=getNumericValue("xml:/DemandDetails/@PrimeLineNo")%>">
					<% if(showOrderLineNo("DemandDetails","DemandDetails")) {%>
	                    <a href="javascript:callPopupWithEntity('NWCdemand', '<%=orderLineKeyString%>');">
		                    <yfc:getXMLValue binding="xml:/DemandDetails/@PrimeLineNo"/>
						</a>
					<% } else { %>
						<yfc:getXMLValue binding="xml:/DemandDetails/@PrimeLineNo"/>
					<%}%>
                </td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/DemandDetails/@BuyerOrganizationCode"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/DemandDetails/@SellerOrganizationCode"/></td>
                <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:/DemandDetails/@Quantity")%>">
                    <yfc:getXMLValue binding="xml:/DemandDetails/@Quantity"/>
                </td>
            </tr>
        </yfc:loopXML>
    </tbody>
</table>
