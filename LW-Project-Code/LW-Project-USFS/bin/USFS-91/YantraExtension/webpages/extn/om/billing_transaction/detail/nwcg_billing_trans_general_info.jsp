<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/orderentry.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.dom.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<table class="view" width="100%">
<yfc:makeXMLInput name="SeqKey">
<yfc:makeXMLKey binding="xml:/NWCGBillingTransaction/@SequenceKey" value="xml:/NWCGBillingTransaction/@SequenceKey"/>
</yfc:makeXMLInput>

<% String TDate = resolveValue("xml:NWCGBillingTransaction:/NWCGBillingTransaction/@TransDate"); 
		   String TDateStr1 = TDate.substring(0,4);
		   String TDateStr2 = TDate.substring(5,7);
           String TDateStr3 = TDate.substring(8,10);
           TDate = TDateStr2+"/"+TDateStr3+"/"+TDateStr1;
%>
<tr>

<td class="detaillabel" ><yfc:i18n>Transaction_No</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@TransactionNo"/></td>
<td class="detaillabel" ><yfc:i18n>Trans_Type</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@TransType"/></td>
<td class="detaillabel" ><yfc:i18n>Document_Type</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@DocumentType"/></td>
<td class="detaillabel" ><yfc:i18n>Trans_CreateUserId</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@TransCreateUserId"/></td>
</tr>

<tr>
<td class="detaillabel" ><yfc:i18n>Trans_Date</yfc:i18n></td>
<td class="protectedtext"><%=TDate%></td>
<td class="detaillabel" ><yfc:i18n>Trans_Amount</yfc:i18n></td>
<td class="protectedtext">$<yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@TransAmount"/></td>
<td class="detaillabel" ><yfc:i18n>Trans_Qty</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@TransQty"/></td>
<td class="detaillabel" ><yfc:i18n>Unit_Price</yfc:i18n></td>
<td class="protectedtext">$<yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@UnitCost"/></td>
<!-- <td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@TransDate"/></td> !-->
</tr>

<tr>
<td class="detaillabel" ><yfc:i18n>Cache_Item</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@ItemId"/></td>
<td class="detaillabel" ><yfc:i18n>Item_Description</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@ItemDescription"/></td>
<td class="detaillabel" ><yfc:i18n>Item_Product_Line</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@ItemProductLine"/></td>
<td class="detaillabel" ><yfc:i18n>UOM</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@UOM"/></td>
</tr>

<tr>

<td class="detaillabel" ><yfc:i18n>Incident_No</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@IncidentNo"/></td>
<td class="detaillabel" ><yfc:i18n>Incident_Year</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@IncidentYear"/></td>
<td class="detaillabel" ><yfc:i18n>Incident_Name</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@IncidentName"/></td>
<td class="detaillabel" ><yfc:i18n>Trans_ModifyUserId</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@TransModifyUserId"/></td>
</tr>

<tr>
<td class="detaillabel" ><yfc:i18n>Enterprise_Code</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@EnterpriseCode"/></td>
<td class="detaillabel" ><yfc:i18n>Cache_Id</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@CacheId"/></td>
<td class="detaillabel" ><yfc:i18n>Extracted</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@IsExtracted"/></td>
<td class="detaillabel" ><yfc:i18n>Reviewed</yfc:i18n></td>
<td class=tablecolumn noWrap>
<input class="checkbox" type="checkbox" <%=getCheckBoxOptions("xml:/NWCGBillingTransaction/@IsReviewed", "xml:/NWCGBillingTransaction/@IsReviewed", "Y")%>/>
</tr>

<% String TransType = resolveValue("xml:/NWCGBillingTransaction/@TransType"); %>

<% 
if ( (TransType.indexOf("INVENTORY") != -1) 
  || (TransType.indexOf("RETURNS") != -1) 
  || (TransType.indexOf("RECEIVE") != -1) 
  || (TransType.indexOf("REFURB") != -1) )
  {
%>
<tr>
<td class="detaillabel" ><yfc:i18n>Location_Id</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@LocationId"/></td>
<td class="detaillabel" ><yfc:i18n>Disposition_Code</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@DispositionCode"/></td>
<!-- cr 489 -->
<td class="detaillabel" ><yfc:i18n>Reason_Code</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@ReasonCode"/></td>
<td class="detaillabel" ><yfc:i18n>Reason_Code_Text</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/NWCGBillingTransaction/@ReasonCodeText"/></td>
<!-- cr 489 -->
</tr>
<% } %>

</table>

