<%@taglib  prefix="yfc" uri="/WEB-INF/yfc.tld" %>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="../console/scripts/om.js"></script>
<script>
function createIssueFromBackOrderLines(){
yfcMultiSelectToSingleAPIOnAction('EntityKey', 'yfcMultiSelectCounter', 'yfcMultiSelectValue1', 'OrderLineKey', 'xml:/OrderLineList/OrderLine',null);
yfcMultiSelectToSingleAPIOnAction('EntityKey', 'yfcMultiSelectCounter', 'yfcMultiSelectValue2', 'NewIssueQty', 'xml:/OrderLineList/OrderLine',null);
yfcMultiSelectToSingleAPIOnAction('EntityKey', 'yfcMultiSelectCounter', 'yfcMultiSelectValue3', 'IncidentNo', 'xml:/OrderLineList/OrderLine',null);
yfcMultiSelectToSingleAPIOnAction('EntityKey', 'yfcMultiSelectCounter', 'yfcMultiSelectValue4', 'IncidentYear', 'xml:/OrderLineList/OrderLine',null);
return true;
}
function createCacheTransferFromBackOrderLines(){
yfcMultiSelectToSingleAPIOnAction('EntityKey', 'yfcMultiSelectCounter', 'yfcMultiSelectValue1', 'OrderLineKey', 'xml:/OrderLineList/OrderLine',null);
yfcMultiSelectToSingleAPIOnAction('EntityKey', 'yfcMultiSelectCounter', 'yfcMultiSelectValue2', 'NewIssueQty', 'xml:/OrderLineList/OrderLine',null);
yfcMultiSelectToSingleAPIOnAction('EntityKey', 'yfcMultiSelectCounter', 'yfcMultiSelectValue3', 'IncidentNo', 'xml:/OrderLineList/OrderLine',null);
yfcMultiSelectToSingleAPIOnAction('EntityKey', 'yfcMultiSelectCounter', 'yfcMultiSelectValue4', 'IncidentYear', 'xml:/OrderLineList/OrderLine',null);
return true;
}
function cancelBOFromBackOrderLines(){
yfcMultiSelectToSingleAPIOnAction('EntityKey', 'yfcMultiSelectCounter', 'yfcMultiSelectValue1', 'OrderLineKey', 'xml:/OrderLineList/OrderLine',null);
yfcMultiSelectToSingleAPIOnAction('EntityKey', 'yfcMultiSelectCounter', 'yfcMultiSelectValue5', 'NewIssueQty', 'xml:/OrderLineList/OrderLine',null);
yfcMultiSelectToSingleAPIOnAction('EntityKey', 'yfcMultiSelectCounter', 'yfcMultiSelectValue3', 'IncidentNo', 'xml:/OrderLineList/OrderLine',null);
yfcMultiSelectToSingleAPIOnAction('EntityKey', 'yfcMultiSelectCounter', 'yfcMultiSelectValue4', 'IncidentYear', 'xml:/OrderLineList/OrderLine',null);
return true;
}
function setMultiSelectvalue(elem,counter){
  if(elem.value==null){return;}
  var entities = document.all("EntityKey");
  if(typeof entities.length =='undefined'){
	  entities.yfcMultiSelectValue2 = elem.value;
  }else{
	    entities[counter-1].yfcMultiSelectValue2 = elem.value;
  }
}
</script>
<table class="table" cellpadding="0" cellspacing="0" width="100%">
<thead>
<tr>
<td sortable="no" class="checkboxheader">
<input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
</td>
<td class="tablecolumnheader"><yfc:i18n>Ship_Cache</yfc:i18n></td>

<% if(!equals("0006", resolveValue("xml:OrderLine:/OrderLine/Order/@DocumentType"))) {%>
<td class="tablecolumnheader"><yfc:i18n>Incident_Other_Order_No</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Incident_Year</yfc:i18n></td>
<%}%>
<td class="tablecolumnheader"><yfc:i18n>Issue_No</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Request_No</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Item_ID</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>PC</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>UOM</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Item_Description</yfc:i18n></td>
<%--
<td class="tablecolumnheader"><yfc:i18n>Recv_Node</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Ship_Node</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Ship_Date</yfc:i18n></td>
--%>
<td class="numerictablecolumnheader"><yfc:i18n>Line_Qty</yfc:i18n></td>
<td class="numerictablecolumnheader"><yfc:i18n>Back_Order_Qty</yfc:i18n></td>
<td class="numerictablecolumnheader"><yfc:i18n>Issue_Qty</yfc:i18n></td>
</tr>
</thead>
<tbody>
<yfc:loopXML name="OrderLineList" binding="xml:/OrderLineList/@OrderLine" id="OrderLine">
<% if(!isVoid(resolveValue("xml:OrderLine:/OrderLine/@Status"))) {%>
<yfc:makeXMLInput name="orderLineKey">
<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey" />
<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderHeaderKey" value="xml:/OrderLine/@OrderHeaderKey" />
</yfc:makeXMLInput>
<tr>
<td class="checkboxcolumn" >

<input type="checkbox" value='<%=getParameter("orderLineKey")%>' name="EntityKey"
yfcMultiSelectCounter='<%=OrderLineCounter%>' yfcMultiSelectValue1='<%=resolveValue("xml:/OrderLine/@OrderLineKey")%>'
yfcMultiSelectValue2='' yfcMultiSelectValue3='<%=resolveValue("xml:/OrderLine/Order/Extn/@ExtnIncidentNo")%>' yfcMultiSelectValue4='<%=resolveValue("xml:/OrderLine/Order/Extn/@ExtnIncidentYear")%>' yfcMultiSelectValue5='<%=resolveValue("xml:/OrderLine/Extn/@ExtnBackorderedQty")%>'/>

<input type="hidden" name='OrderLineKey_<%=OrderLineCounter%>' value='<%=resolveValue("xml:/OrderLine/@OrderLineKey")%>' />
<input type="hidden" name='OrderHeaderKey_<%=OrderLineCounter%>' value='<%=resolveValue("xml:/OrderLine/@OrderHeaderKey")%>' />

</td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/@ShipNode"/></td>

<% if(!equals("0006", resolveValue("xml:OrderLine:/OrderLine/Order/@DocumentType"))) {%>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Order/Extn/@ExtnIncidentNo"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Order/Extn/@ExtnIncidentYear"/></td>
<%} %>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Order/@OrderNo"/></td>
<td class="tablecolumn">
<yfc:getXMLValue binding="xml:/OrderLine/Extn/@ExtnRequestNo"/>
<%--
<a href="javascript:showDetailFor('<%=getParameter("orderLineKey")%>');">
</a> --%>
</td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ProductClass"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/OrderLineTranQuantity/@TransactionalUOM"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemDesc"/></td>
<%--
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/@ReceivingNode"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/@ShipNode"/></td>
<td class="tablecolumn" sortValue="<%=getDateValue("xml:OrderLine:/OrderLine/@ReqShipDate")%>">
<yfc:getXMLValue binding="xml:/OrderLine/@ReqShipDate"/>
</td>
--%>

<td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/OrderLineTranQuantity/@OrderedQty")%>">
<yfc:getXMLValue binding="xml:/OrderLine/OrderLineTranQuantity/@OrderedQty"/>
</td>

<!-- the backordered qty-->
<td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/Extn/@ExtnBackorderedQty")%>">
<yfc:getXMLValue binding="xml:OrderLine:/OrderLine/Extn/@ExtnBackorderedQty"/>
</td>

<!--the input value for issue -->
<td>
<input class="unprotectedtext" type="text" size="4" name="ExtnIssueQty_"+<%=OrderLineCounter%> onblur="setMultiSelectvalue(this,'<%=OrderLineCounter%>');"/>
</td>
<%--
<td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/@LineTotal")%>">
<%=displayAmount(getValue("OrderLine", "xml:/OrderLine/LinePriceInfo/@LineTotal"), (YFCElement) request.getAttribute("CurrencyList"), getValue("OrderLine", "xml:/OrderLine/Order/PriceInfo/@Currency"))%>
</td>
--%>
<% if (equals("Y", getValue("OrderLine", "xml:/OrderLine/@HoldFlag"))) { %>
<img class="icon" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_order_line_is_held")%>/>
<% } %>

</tr>
<%} else {%>
<yfc:makeXMLInput name="orderLineKey">
<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey" />
<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderHeaderKey" value="xml:/OrderLine/@OrderHeaderKey" />
</yfc:makeXMLInput>
<tr>
<td>
<input type="checkbox" value='<%=getParameter("orderLineKey")%>' name="EntityKey"
yfcMultiSelectCounter='<%=OrderLineCounter%>' yfcMultiSelectValue1='<%=resolveValue("xml:/OrderLine/@OrderLineKey")%>'
yfcMultiSelectValue2='' yfcMultiSelectValue3='<%=resolveValue("xml:/OrderLine/Order/Extn/@ExtnIncidentNo")%>' yfcMultiSelectValue4='<%=resolveValue("xml:/OrderLine/Order/Extn/@ExtnIncidentYear")%>' yfcMultiSelectValue5='<%=resolveValue("xml:/OrderLine/Extn/@ExtnBackorderedQty")%>'/>

<input type="hidden" name='OrderLineKey_<%=OrderLineCounter%>' value='<%=resolveValue("xml:/OrderLine/@OrderLineKey")%>' />
<input type="hidden" name='OrderHeaderKey_<%=OrderLineCounter%>' value='<%=resolveValue("xml:/OrderLine/@OrderHeaderKey")%>' />
</td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/@ShipNode"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Order/Extn/@ExtnIncidentNo"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Order/Extn/@ExtnIncidentYear"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Order/@OrderNo"/></td>
<td class="tablecolumn">
<yfc:getXMLValue binding="xml:/OrderLine/Extn/@ExtnRequestNo"/>
<%--
<a href="javascript:showDetailFor('<%=getParameter("orderLineKey")%>');">
</a> --%>
</td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ProductClass"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/OrderLineTranQuantity/@TransactionalUOM"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemDesc"/></td>
<%--
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/@ReceivingNode"/></td>
<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/@ShipNode"/></td>
<td class="tablecolumn" sortValue="<%=getDateValue("xml:OrderLine:/OrderLine/@ReqShipDate")%>">
<yfc:getXMLValue binding="xml:/OrderLine/@ReqShipDate"/>
</td>
--%>

<td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/OrderLineTranQuantity/@OrderedQty")%>">
<yfc:getXMLValue binding="xml:/OrderLine/OrderLineTranQuantity/@OrderedQty"/>
</td>

<!-- the backordered qty-->
<td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/Extn/@ExtnBackorderedQty")%>">
<yfc:getXMLValue binding="xml:OrderLine:/OrderLine/Extn/@ExtnBackorderedQty"/>
</td>

<!--the input value for issue -->
<td>
<input class="unprotectedtext" type="text" size="4" name="ExtnIssueQty_"+<%=OrderLineCounter%> onblur="setMultiSelectvalue(this,'<%=OrderLineCounter%>');"/>
</td>
<%--
<td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/@LineTotal")%>">
<%=displayAmount(getValue("OrderLine", "xml:/OrderLine/LinePriceInfo/@LineTotal"), (YFCElement) request.getAttribute("CurrencyList"), getValue("OrderLine", "xml:/OrderLine/Order/PriceInfo/@Currency"))%>
</td>
--%>

<% if (equals("Y", getValue("OrderLine", "xml:/OrderLine/@HoldFlag"))) { %>
<img class="icon" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_order_line_is_held")%>/>
<% } %>
<% if (equals("Y", getValue("OrderLine", "xml:/OrderLine/@isHistory") )){ %>
<img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.HISTORY_ORDER, "This_is_an_archived_order_line")%>/>
<% } %>

</tr>
<%}%>
</yfc:loopXML>
</tbody>
</table>
