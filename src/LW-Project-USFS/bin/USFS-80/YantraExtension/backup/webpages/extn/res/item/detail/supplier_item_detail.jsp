<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/orderentry.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.dom.*" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>

<script language="Javascript" >
IgnoreChangeNames();
yfcDoNotPromptForChanges(true);
yfcDoNotPromptForChangesForActions(true);
</script>

<yfc:callAPI apiID="AP1"/>
<table class="view" width="100%">
		<tr>
		<td class="detaillabel" ><yfc:i18n>Seller</yfc:i18n></td>
       <td class="protectedtext">
            <yfc:getXMLValue binding="xml:/NWCGSupplierItem/@SupplierID"/>
        </td>
		<td class="detaillabel" ><yfc:i18n>Item</yfc:i18n></td>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:/NWCGSupplierItem/@ItemID"/>
		</td>
		<td class="searchlabel" ><yfc:i18n>Product_Class</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:/NWCGSupplierItem/@ProductClass"/>
		</td>
		<td  class="searchlabel" ><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:/NWCGSupplierItem/@UnitOfMeasure" />
		</td>
		</tr>
		<tr>
		<td class="detaillabel" ><yfc:i18n>Supplier_Part_Number</yfc:i18n></td>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:/NWCGSupplierItem/@SupplierPartNo"/>
		</td>
		<td class="detaillabel" ><yfc:i18n>Supplier_Standar_Pack</yfc:i18n></td>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:/NWCGSupplierItem/@SupplierStandardPack"/>
		</td>
		<td class="detaillabel" ><yfc:i18n>Unit_Cost</yfc:i18n></td>
		<td class="protectedtext">
		<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGSupplierItem/@UnitCost", "xml:/NWCGSupplierItem/@UnitCost")%>/>
		</td>
		<td class="detaillabel" ><yfc:i18n>Preferred_Indicator</yfc:i18n></td>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:/NWCGSupplierItem/@Preferred"/>
		</td>
	</tr>
</table>
