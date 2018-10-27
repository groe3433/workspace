<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/chargeutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="../console/scripts/om.js"></script>
<script language="javascript">
window.attachEvent("onload", IgnoreChangeNames);
document.body.attachEvent("onunload", processSaveOrderLineRecordsForCharges);
</script>

<%
String chargeType=request.getParameter("chargeType");
String chargeTypeAttributePrefix = "";
String chargeQty = "";
String quantity = "";
boolean editable = false;

if (isVoid(chargeType)) {
chargeType="Overall";
chargeQty="OrderedPricing";
quantity = "OrderedQty";
}
else if (equals(chargeType,"Remaining")) {
chargeTypeAttributePrefix = "Remaining";
chargeQty="RemainingPricing";
editable = true;
quantity = "Open_Quantity";
}
else if (equals(chargeType,"Invoiced")) {
chargeTypeAttributePrefix = "Invoiced";
chargeQty="InvoicedPricing";
quantity = "Invoiced_Quantity";
}

// Reset the editable flag if modifications are not allowed
editable = editable & isModificationAllowed("xml:/@ChargePerLine","xml:/OrderLine/AllowedModifications");

Map cChargeCategory = getChargeCategoryMap((YFCElement)request.getAttribute("ChargeCategoryList"), (YFCElement)request.getAttribute("ChargeNameList"));

Set cChargeCat = cChargeCategory.keySet();
for(Iterator it2 = cChargeCat.iterator(); it2.hasNext(); )	{
String sChargeCat = (String)it2.next();
YFCElement e = (YFCElement)cChargeCategory.get(sChargeCat);
request.setAttribute(sChargeCat, e);
}

%>

<table class="view" width="100%">
<tr>
<td><yfc:i18n><%=quantity%></yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue name="OrderLine" binding='<%=buildBinding("xml:/OrderLine/Line",chargeType,"Totals/@PricingQty")%>'/></td>
<td class="detaillabel" ><yfc:i18n>Pricing_UOM</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue name="OrderLine" binding="xml:/OrderLine/LinePriceInfo/@PricingUOM"/></td>
<td><yfc:i18n>Unit_Price</yfc:i18n></td>
<%if (equals(chargeType,"Remaining")) {%>
<td>
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>
<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/LinePriceInfo/@UnitPrice","xml:/OrderLine/Line" + chargeType + "Totals/@UnitPrice","xml:/OrderLine/AllowedModifications")%>/>
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
<%} else {%>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<yfc:getXMLValue name="OrderLine" binding='<%=buildBinding("xml:/OrderLine/Line",chargeType,"Totals/@UnitPrice")%>'/>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/></td>
<%}%>
<td><yfc:i18n>Extended_Price</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;<yfc:getXMLValue name="OrderLine" binding='<%=buildBinding("xml:/OrderLine/Line",chargeType,"Totals/@ExtendedPrice")%>'/>&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/></td>
</tr>
</table>
<table class="table" ID="ChargeBreakup" cellspacing="0" width="100%" <%if (editable) {%> initialRows="3" <%} %>>
<thead>
<tr>
<td class="checkboxheader">&nbsp;</td>
<td class="tablecolumnheader"><yfc:i18n>Charge_Category</yfc:i18n></td>
<td class="tablecolumnheader"><yfc:i18n>Charge_Name</yfc:i18n></td>
<td class="numerictablecolumnheader"  <%if (equals(chargeType,"Remaining")) {%> sortable="no" <%}%>><yfc:i18n>Per_Unit</yfc:i18n></td>
<td class="numerictablecolumnheader"  <%if (equals(chargeType,"Remaining")) {%> sortable="no" <%}%>><yfc:i18n>Per_Line</yfc:i18n></td>
<td class="numerictablecolumnheader"><yfc:i18n>Charge_Amount</yfc:i18n></td>
</tr>
</thead>
<tbody>
<yfc:loopXML name="OrderLine" binding="xml:/OrderLine/LineCharges/@LineCharge" id="LineCharge">
<tr>
<td class="checkboxcolumn">&nbsp;</td>

<td class="tablecolumn">
<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/LineCharges/LineCharge_" + LineChargeCounter + "/@ChargeName", "xml:/LineCharge/@ChargeName")%>/>
<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/LineCharges/LineCharge_" + LineChargeCounter + "/@ChargeCategory", "xml:/LineCharge/@ChargeCategory")%>/>

<%=getComboText("xml:/ChargeCategoryList/@ChargeCategory", "Description", "ChargeCategory", "xml:/LineCharge/@ChargeCategory", true)%>

<% if (equals(isChargeBillable(getValue("LineCharge","xml:/LineCharge/@ChargeCategory"), (YFCElement) request.getAttribute("ChargeCategoryList")),"N")) { %>
<img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.INFO_CHARGE, "Informational_Charge")%>>
<%}%>
</td>

<td class="tablecolumn">
<%=displayChargeNameDesc(resolveValue("xml:/LineCharge/@ChargeCategory"), resolveValue("xml:/LineCharge/@ChargeName"), (YFCElement)request.getAttribute("ChargeNameList") )%>
</td>

<%if (equals(chargeType,"Remaining")) {%>
<td class="numerictablecolumn">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;
<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/LineCharges/LineCharge_" + LineChargeCounter + "/@ChargePerUnit","xml:/LineCharge/@RemainingChargePerUnit","xml:/OrderLine/AllowedModifications")%>/>
&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
<td class="numerictablecolumn">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;
<input type="text" ChargePerLine='<%=resolveValue("xml:/LineCharge/@ChargePerLine")%>'  <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/LineCharges/LineCharge_" + LineChargeCounter + "/@ChargePerLine","xml:/LineCharge/@RemainingChargePerLine","xml:/OrderLine/AllowedModifications")%>/>
&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
<%} else {%>
<td class="numerictablecolumn" sortValue="<%=getNumericValue(buildBinding("xml:LineCharge:/LineCharge/@",chargeTypeAttributePrefix,"ChargePerUnit"))%>">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;
<yfc:getXMLValue name="LineCharge" binding='<%=buildBinding("xml:/LineCharge/@",chargeTypeAttributePrefix,"ChargePerUnit")%>'/>
&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
<td class="numerictablecolumn" sortValue="<%=getNumericValue(buildBinding("xml:LineCharge:/LineCharge/@",chargeTypeAttributePrefix,"ChargePerLine"))%>">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;
<yfc:getXMLValue name="LineCharge" binding='<%=buildBinding("xml:/LineCharge/@",chargeTypeAttributePrefix,"ChargePerLine")%>'/>
&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
<%} %>
<td class="numerictablecolumn" sortValue="<%=getNumericValue(buildBinding("xml:LineCharge:/LineCharge/@",chargeTypeAttributePrefix,"ChargeAmount"))%>">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;
<% if (equals(isChargeCategoryDiscount(getValue("LineCharge","xml:/LineCharge/@ChargeCategory"), (YFCElement) request.getAttribute("ChargeCategoryList")),"Y")) { %> <yfc:i18n>(-)</yfc:i18n> <%}%>
<yfc:getXMLValue name="LineCharge" binding='<%=buildBinding("xml:/LineCharge/@",chargeTypeAttributePrefix,"ChargeAmount")%>'/>
&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
</tr>
</yfc:loopXML>
</tbody>
<%	//	show only valid charge names.  if charge name is already used delete it from list.
YFCElement eChargeNameList = (YFCElement)request.getAttribute("ChargeNameList");
YFCElement eOrderLine = ((YFCElement)request.getAttribute("OrderLine"));

modifyChargeNameList(eChargeNameList, eOrderLine.getChildElement("LineCharges") );
%>
<tfoot>
<tr style='display:none' TemplateRow="true">
<td class="checkboxcolumn" ID="DONTHIDE">&nbsp;</td>

<td class="tablecolumn" ID="DONTHIDE" >
<select  onChange="displayChargeNameDropDown(this)" <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/LineCharges/LineCharge_/@ChargeCategory", "xml:/OrderLine/AllowedModifications", "PRICE", "combo")%>>
<yfc:loopOptions binding="xml:/ChargeCategoryList/@ChargeCategory" name="Description" isLocalized="Y" value="ChargeCategory"/>
</select>
</td>
<%
Set cCC = cChargeCategory.keySet();
boolean bShowEmptyTD = true;
for(Iterator it2 = cCC.iterator(); it2.hasNext(); )	{
String sChargeCat = (String) it2.next();
String loopBinding = "xml:" + sChargeCat + ":/ChargeNameList/@ChargeName";
if(bShowEmptyTD)	{
%>			<td></td>
<%			bShowEmptyTD = false;
}

if(isTrue("xml:/Rules/@RuleSetValue") )	{		%>
<td class="tablecolumn" style='display:none' ID="<%=sChargeCat%>" >

<select  <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/LineCharges/LineCharge_/@ChargeName", "xml:/OrderLine/AllowedModifications", "PRICE", "combo")%>>
<yfc:loopOptions binding="<%=loopBinding%>" name="Description" value="ChargeName" isLocalized="Y"/>
</select>
</td>
<%	}	else	{	%>
<td ID="<%=sChargeCat%>" style='display:none'>
<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Order/OrderLines/OrderLine/LineCharges/LineCharge_/@ChargeName")%>	/>
</td>
<%		}
}	%>

<td class="numerictablecolumn" ID="DONTHIDE">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;
<input type="text" <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/LineCharges/LineCharge_/@ChargePerUnit", "xml:/OrderLine/AllowedModifications", "PRICE", "text")%>/>
&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
<td class="numerictablecolumn" ID="DONTHIDE">
<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PrefixSymbol"/>&nbsp;
<input type="text" <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/LineCharges/LineCharge_/@ChargePerLine", "xml:/OrderLine/AllowedModifications", "PRICE", "text")%>/>
&nbsp;<yfc:getXMLValue binding="xml:/CurrencyList/Currency/@PostfixSymbol"/>
</td>
<td class="numerictablecolumn" ID="DONTHIDE"></td>
</tr>
<%if (editable) {%>
<tr>
<td nowrap="true" colspan="6">
<jsp:include page="/common/editabletbl.jsp" flush="true">
</jsp:include>
</td>
</tr>
<%}%>
</tfoot>
</table>
