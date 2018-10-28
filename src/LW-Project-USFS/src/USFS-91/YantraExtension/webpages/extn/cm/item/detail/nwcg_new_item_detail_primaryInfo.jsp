<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>



<table class="view" width="100%">
<yfc:makeXMLInput name="itemKey">
<yfc:makeXMLKey binding="xml:/Item/@ItemKey" value="xml:/Item/@ItemKey"/>
</yfc:makeXMLInput>
<tr>
<td>
<input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
<input type="hidden" name="chkCopyItemEntityKey" value='<%=getParameter("itemKey")%>' />
<input type="hidden" name="xml:/Item/@OrganizationCode" value='<%=getValue("Item", "xml:/Item/@OrganizationCode")%>'/>
</td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Master_Catalog</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@MasterCatalogID"/></td>
<td class="detaillabel" ><yfc:i18n>Short_Description</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@ShortDescription"/></td>

<td class="detaillabel" ><yfc:i18n>Status</yfc:i18n></td>
<td class="protectedtext"><%
String statusVal = resolveValue("xml:/Item/PrimaryInformation/@Status");
if (statusVal.equals("3000")){
	%>Published<%}
	else if(statusVal.equals("2000")){
	%>Held(UnPublished)<%}
	else{%>
		<yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@Status"/><%
	}%>

</td>

<td class="detaillabel" ><yfc:i18n>Kit_Code</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@KitCode"/></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Min_Quantity</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@MinOrderQuantity"/></td>
<td class="detaillabel" ><yfc:i18n>Max_Quantity</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@MaxOrderQuantity"/></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Shipping_Allowed</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@IsShippingAllowed"/></td>
<td class="detaillabel" ><yfc:i18n>Delivery_Allowed</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@IsDeliveryAllowed"/></td>
<td class="detaillabel" ><yfc:i18n>Pickup_Allowed</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@IsPickupAllowed"/></td>
<td class="detaillabel" ><yfc:i18n>Parcel_Shipping_Allowed</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@IsParcelShippingAllowed"/></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Manufacturer_Name</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@ManufacturerName"/></td>
<td class="detaillabel" ><yfc:i18n>Manufacturers_Item_Description</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@ManufacturerItemDesc"/></td>
<td class="detaillabel" ><yfc:i18n>Manufacturers_Item</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@ManufacturerItem"/></td>
<td class="detaillabel" ><yfc:i18n>Country_Of_Origin</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@CountryOfOrigin"/></td>
</tr>
<tr>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Reverse_Logistics</yfc:i18n></td>
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Returnable</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@IsReturnable"/></td>
<td class="detaillabel" ><yfc:i18n>Credit_WO_Receipt</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@CreditWOReceipt"/></td>
<td class="detaillabel" ><yfc:i18n>Return_Window</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@ReturnWindow"/></td>
</tr>

<tr>
<td class="detaillabel" ><yfc:i18n>Pricing</yfc:i18n></td>
</tr>
<tr>

<td class="detaillabel" ><yfc:i18n>List_Price</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:ItemPrices:/ComputePriceForItem/@ListPrice"/></td>
<td class="detaillabel" ><yfc:i18n>Retail_Price</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:ItemPrices:/ComputePriceForItem/@RetailPrice"/></td>
<td class="detaillabel" ><yfc:i18n>Unit_Price</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@UnitCost"/></td>
<td class="detaillabel" ><yfc:i18n>Cost_Currency</yfc:i18n></td>
<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@CostCurrency"/></td>
</tr>
</table>