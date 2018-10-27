<%@taglib  prefix="yfc" uri="/WEB-INF/yfc.tld" %>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<table class="table" editable="false" width="100%" cellspacing="0">
<thead>
<tr>
<td sortable="no" class="checkboxheader">
<input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
<input type="hidden" name="xml:/Order/@Override" value="N"/>
<input type="hidden" name="ResetDetailPageDocumentType" value="Y"/>	<%-- cr 35413 --%>
<input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
</td>
<td class="tablecolumnheader"><yfc:i18n>Order_#</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Status</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Enterprise</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Shipping_Cache</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Receiving_Cache</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Buyer</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Order_Date</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Total_Amount</yfc:i18n></td>
</tr>
</thead>
<tbody>
<yfc:loopXML binding="xml:/OrderList/@Order" id="Order">
<tr>
<yfc:makeXMLInput name="IssueTransferPrintKey">
<yfc:makeXMLKey binding="xml:/Print/ITOrder/@OrderKey" value="xml:/Order/@OrderHeaderKey" />
</yfc:makeXMLInput>
<yfc:makeXMLInput name="orderKey">
<yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey" />
</yfc:makeXMLInput>
<td class="checkboxcolumn">
<input type="checkbox" value='<%=getParameter("orderKey")%>' name="EntityKey" isHistory='<%=getValue("Order","xml:/Order/@isHistory")%>' PrintEntityKey='<%=getParameter("IssueTransferPrintKey")%>'/>
</td>
<td class="tablecolumn">
<a href="javascript:showDetailFor('<%=getParameter("orderKey")%>');">
<yfc:getXMLValue binding="xml:/Order/@OrderNo"/>
</a>
</td>
<td class="tablecolumn">
<% if (isVoid(getValue("Order", "xml:/Order/@Status"))) { %>
[<yfc:i18n>Draft</yfc:i18n>]
<% } else { %>
<%=displayOrderStatus(getValue("Order","xml:/Order/@MultipleStatusesExist"),getValue("Order","xml:/Order/@MaxOrderStatusDesc"))%>
<% } %>
<% if (equals("Y", getValue("Order", "xml:/Order/@HoldFlag"))) { %>
<img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_order_is_held")%>/>
<% } %>
<% if(equals("Y", getValue("Order","xml:/Order/@isHistory") )){ %>
<img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.HISTORY_ORDER, "This_is_an_archived_order")%>/>
<% } %>
</td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/Order/@EnterpriseCode"/></td>
<!-- CR 134 add Shipping/Receiving Caches-->
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/Order/@ShipNode"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/Order/@ReceivingNode"/></td>
<!-- CR 134 -->
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/Order/@BuyerOrganizationCode"/></td>
<td class="tablecolumn" sortValue="<%=getDateValue("xml:/Order/@OrderDate")%>"><yfc:getXMLValue binding="xml:/Order/@OrderDate"/></td>
<td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:/Order/PriceInfo/@TotalAmount")%>">
<%=displayAmount(getValue("Order", "xml:/Order/PriceInfo/@TotalAmount"), (YFCElement) request.getAttribute("CurrencyList"), getValue("Order", "xml:/Order/PriceInfo/@Currency"))%>
</td>
</tr>
</yfc:loopXML>
</tbody>
</table>
