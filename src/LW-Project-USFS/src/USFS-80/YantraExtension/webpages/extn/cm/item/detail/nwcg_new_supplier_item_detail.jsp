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

<%	String SupplierItemkey = 	resolveValue("xml:/NWCGSupplierItem/@SupplierItemkey");
YFCElement organizationInput = null;
		organizationInput = YFCDocument.parse("<Organization OrganizationCode=\"" + resolveValue("xml:/NWCGSupplierItem/@SupplierID") + "\" />").getDocumentElement();

		YFCElement organizationTemplate = YFCDocument.parse("<OrganizationList> <Organization OrganizationName=\"\"/> </OrganizationList>").getDocumentElement();
%>

<yfc:callAPI apiName="getOrganizationList" inputElement="<%=organizationInput%>" templateElement="<%=organizationTemplate%>" outputNamespace="OrganizationList"/>

<% String organizationID = 	resolveValue("xml:/OrganizationList/Organization/@OrganizationName"); 
		//System.out.println("ML =======> : "+ SupplierItemkey);
%>
<!-- ML CR 632 SUPERCEDES cr 440 ks -->
<% 
YFCElement UOMMasterInput = YFCDocument.parse("<ItemUOMMaster	CallingOrganizationCode=\"NWCG\" />").getDocumentElement();
 
YFCElement UOMMasterTemplate = YFCDocument.parse("<ItemUOMMasterList>  <ItemUOMMaster AllowFractionsInConversion=\"\" Description=\"\" IsInventoryUOM=\"\" IsOrderingUOM=\"\" ItemGroupCode=\"\" ItemUOMMasterKey=\"\" LPNType=\"\" OrganizationCode=\"\" PackagingIndicator=\"\" UOMType=\"\" UnitOfMeasure=\"\" /> </ItemUOMMasterList>").getDocumentElement();
%>

<yfc:callAPI apiName="getItemUOMMasterList" inputElement="<%=UOMMasterInput%>" templateElement="<%=UOMMasterTemplate%>" outputNamespace="UOMList"/>
<!--END CR 632 -->


<table class="view" width="100%">
<yfc:makeXMLInput name="SupplierItemkey">
	<yfc:makeXMLKey binding="xml:/NWCGSupplierItem/@SupplierItemkey" value="xml:/NWCGSupplierItem/@SupplierItemkey" />
</yfc:makeXMLInput> 
	<tr>
	  <td class="detaillabel" ><yfc:i18n>Seller</yfc:i18n></td>
      <td class="protectedtext">
			<input type='hidden' <%=getTextOptions("xml:/NWCGSupplierItem/@SupplierID")%>/>
            <yfc:getXMLValue binding="xml:/NWCGSupplierItem/@SupplierID"/> 
      </td>
		<td class="detaillabel" ><yfc:i18n>Item</yfc:i18n></td>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:/NWCGSupplierItem/@ItemID"/>
		</td>
<!-- Supplier Name 2.1 enchancement -->
		<td class="detaillabel" ><yfc:i18n>Supplier_Name</yfc:i18n></td>
		<td class="protectedtext" nowrap="true">
			<yfc:getXMLValue binding="xml:/OrganizationList/Organization/@OrganizationName"/>
<!--		<input type="hidden"  
			  <%=getTextOptions("xml:/NWCGSupplierItem/@SupplierName", "xml:/OrganizationList/Organization/@OrganizationName")%> />-->
		</td>
<!-- end of enhancement-->		
<td class="detaillabel" ><yfc:i18n>Product_Class</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:/NWCGSupplierItem/@ProductClass"/>
		</td>
		<td  class="detaillabel" ><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:/NWCGSupplierItem/@UnitOfMeasure" />
		</td>
		</tr>
		<tr>

		<td class="detaillabel" ><yfc:i18n>Supplier_Part_Number</yfc:i18n></td>
		<td class="protectedtext">
		<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGSupplierItem/@SupplierPartNo")%>/>
		</td>

		<td class="detaillabel" ><yfc:i18n>Supplier_Standard_Pack</yfc:i18n></td>
		<td class="protectedtext">
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGSupplierItem/@SupplierStandardPack")%>/>
		</td>
				<td class="detaillabel" ><yfc:i18n>Supplier_UOM</yfc:i18n></td>
		<!-- ML CR 632 SUPERCEDES cr 440 ks -->
		<td class="searchcriteriacell" nowrap="true">
        <select name="xml:/NWCGSupplierItem/@SupplierUOM" class="combobox" >
            <yfc:loopOptions binding="xml:UOMList:/ItemUOMMasterList/@ItemUOMMaster" 
                name="UnitOfMeasure" value="UnitOfMeasure" selected="xml:/NWCGSupplierItem/@SupplierUOM"/>
        </select>
        </td>
		<!--END CR 632 -->

		<td class="detaillabel" ><yfc:i18n>Unit_Cost</yfc:i18n></td>
		<td class="protectedtext">
		<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGSupplierItem/@UnitCost")%>/>
		</td>

		<td class="detaillabel" ><yfc:i18n>Preferred_Indicator</yfc:i18n></td>
		<td nowrap="true" >
			<% String strIndicator = resolveValue("xml:/NWCGSupplierItem/@Preferred");
			   if(strIndicator != null && strIndicator.equals("Y"))
			   {
				   strIndicator = "checked";
			   }
			%>
			<input type="checkbox" class="unprotectedinput" <%=strIndicator%> <%=getTextOptions("xml:/NWCGSupplierItem/@Preferred", "xml:/NWCGSupplierItem/@Preferred")%>/>
		</td>
		<tr>
		<!-- CR 582 ML - Add NSN and populate-->
		<td class="detaillabel" ><yfc:i18n>NSN</yfc:i18n></td>
		<td class="protectedtext" >
		<input type="text" class="unprotectedinput"
		<%=getTextOptions("xml:/NWCGSupplierItem/@GlobalItemID")%>/>
		</td>
		<!-- END CR 582 ML - Add NSN and populate-->
		<!-- CR 581 ML -->	
		<td class="detaillabel" ><yfc:i18n>Cache_Item_Description</yfc:i18n></td>
		<td class="protectedtext" colspan="3">
			<input type="text" size="60" class="unprotectedinput" <%=getTextOptions("xml:/NWCGSupplierItem/@ShortDescription")%>/>
		</td>
		<!-- END CR 581 ML -->
		</tr>
	</tr>
</table>