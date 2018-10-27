<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>

<table class="view" width="100%">
<tr>
<td>
<table class="view" width="100%">
<tr>
<td class="detaillabel" ><yfc:i18n>Order_Name</yfc:i18n></td>
<td>
<input type="text" <%=yfsGetTextOptions("xml:/Order/@OrderName","xml:/Order/AllowedModifications")%>/>
</td>
<td class="detaillabel" ><yfc:i18n>Entered_By</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue name="Order" binding="xml:/Order/@EnteredBy"/></td>
<td class="detaillabel" ><yfc:i18n>Source</yfc:i18n></td>
<td class="protectedtext">
<select <%=yfsGetComboOptions("xml:/Order/@EntryType", "xml:/Order/AllowedModifications")%>>
<yfc:loopOptions binding="xml:SourceList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/Order/@EntryType" isLocalized="Y"/>
</select>
</td>
</tr>
<tr>
<td  class="detaillabel" ><yfc:i18n>Created_By_Node</yfc:i18n></td>
<td class="protectedtext"><%=displayFlagAttribute(getValue("Order","xml:/Order/@CreatedAtNode"))%></td>
<td  class="detaillabel" ><yfc:i18n>Ship_Node</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue name="Order" binding="xml:/Order/@ShipNode"/></td>
<td class="detaillabel" ><yfc:i18n>Price_Program</yfc:i18n></td>
<td>
<select <%=yfsGetComboOptions("xml:/Order/@PriceProgramKey", "xml:/Order/AllowedModifications")%>>
<yfc:loopOptions binding="xml:/PriceProgramList/@PriceProgram" name="PriceProgramDescription"
value="PriceProgramKey" selected="xml:/Order/@PriceProgramKey"/>
</select>
</td>
</tr>
<tr>
<td class="detaillabel" >
<yfc:i18n>Return_By_Gift_Recipient</yfc:i18n>
</td>
<td>
<input class="checkbox" type="checkbox" <%=yfsGetCheckBoxOptions("xml:/Order/@ReturnByGiftRecipient","xml:/Order/@ReturnByGiftRecipient","Y","xml:/Order/AllowedModifications")%>/>
</td>
<td colspan="4">&nbsp;</td>
</tr>
</table>
</td>
</tr>
</table>
