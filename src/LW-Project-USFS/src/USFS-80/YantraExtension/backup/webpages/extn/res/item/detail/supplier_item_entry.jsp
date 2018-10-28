<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/orderentry.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.dom.*" %>

<script language="javascript" src="/yantra/console/scripts/tools.js"></script>
<script language="javascript" src="/yantra/console/scripts/om.js"></script>
<%
	String orgCode = resolveValue("xml:CurrentOrganization:/Organization/@OrganizationCode");
	String currSelectedOrg = resolveValue("xml:/NWCGSupplierItem/@OrganizationCode");
//	System.out.println("	currSelectedOrg ==> "+currSelectedOrg);
	if (!isVoid(currSelectedOrg)) { 
		setCommonFieldAttribute("OrganizationCode",currSelectedOrg);
	} else {
		setCommonFieldAttribute("OrganizationCode",orgCode);
	}
    String sItemID=getValue("InventoryItem","xml:/InventoryItem/@ItemID");
    
    //Get a list of all Inventory Orgs of all Enterprises in which the User's Org participates.
    List invOrgList = new ArrayList();
	YFCElement oEnterpriseList = (YFCElement) request.getAttribute("OrganizationList");
	if (oEnterpriseList !=null ){
        for (Iterator i = oEnterpriseList.getChildren(); i.hasNext();) {
            YFCElement oEnterprise = (YFCElement)i.next();
			String inventoryOrganizationCode = oEnterprise.getAttribute("InventoryOrganizationCode");
			if(!invOrgList.contains(inventoryOrganizationCode)) {
				invOrgList.add(inventoryOrganizationCode);
			}
        }
	}
%>

<script language="Javascript" >

IgnoreChangeNames();
yfcDoNotPromptForChanges(false);

function proceedClicked()
{
	////alert('***** 1 *****');
	
	var orgCodeObj = document.all("xml:/NWCGSupplierItem/@OrganizationCode");
	var proceedClicked = document.all("ProceedClicked");
	alert("proceed clicked "+proceedClicked.value);
	proceedClicked.value="CLICKED";
	if (orgCodeObj != null) {
		var orgCodeVal = orgCodeObj.value;
		if (orgCodeVal == "") {
			// Need to popup an error here instead of defaulting.
			orgCodeObj.value = '<%=orgCode%>';
		}
	}
	//alert('***** 2 *****');
	 var itemInput = document.all("xml:/NWCGSupplierItem/@ItemID");
	 //alert('***** 2.1 *****' + orgCodeObj);
	 if (orgCodeObj != null) {
		var itemInputVal = itemInput.value;
		//alert('***** 2.2 *****');
		itemInputVal = itemInputVal.replace(/^\s*/, '').replace(/\s*$/, '');
		 if (itemInputVal != "") {
			if(validateControlValues()) {			
				yfcChangeDetailView(getCurrentViewId());
			}
		 } else {
				//alert(YFCMSG037);
		 }
	 }
	 //alert('***** 3 *****');
}

function selectClicked()
{
	var proceed = document.all("ProceedClicked");
	if(proceed != null)
	{
		proceed.value="NOTCLICKED";
	}
	yfcChangeDetailView(getCurrentViewId());
}
</script>

<yfc:callAPI apiID="AP1"/>

<%
		YFCElement myElement = getRequestDOM();
		YFCElement ele = myElement.getChildElement("NWCGSupplierItem");
		String clicked = "" ;
		if(ele != null)
		{
			clicked =  request.getParameter("ProceedClicked");
			if(clicked == null)
				clicked="";
			//System.out.println("PROCEED CLICKED ++++++ ==== >>> "+clicked);
		}
		//System.out.println("ele==> JAY "+ele);
		if(ele != null && (!clicked.equals("NOTCLICKED")) )
		{%>
			<yfc:callAPI serviceName="createNWCGSupplierItem" inputElement='<%=ele%>'/>
		<%}
%>
        

<table class="view" width="100%">
		<tr>
		<input type="hidden" name="ProceedClicked" value="NOTCLICKED"/>
		<td class="detaillabel" ><yfc:i18n>Organization</yfc:i18n></td>
        <td nowrap="true">
            <select id="OrgCombo" <%=getComboOptions("xml:/NWCGSupplierItem/@OrganizationCode")%> class="combobox" onchange='selectClicked()'>
			<%if(invOrgList.size() != 1) {%>
				<option value="" selected="true"/>
			<%}%>
            <% for (int i=0;i<invOrgList.size();i++) {
                String invOrgCode = (String) invOrgList.get(i); %>
                <option value="<%=invOrgCode%>" <%if(equals(invOrgCode,currSelectedOrg)) {%> selected <%}%>><%=invOrgCode%></option>
            <% } %>
			</select>
        </td>
		<td class="detaillabel" ><yfc:i18n>Seller</yfc:i18n></td>
	       <td nowrap="true" >
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGSupplierItem/@SupplierID", "xml:/NWCGSupplierItem/@SupplierID")%>/>
            <img class="lookupicon" onclick="callLookup(this,'organization','xml:/Organization/OrgRoleList/OrgRole/@RoleKey=SELLER')" name="search" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Seller") %> />
        </td>
		<td class="detaillabel" ><yfc:i18n>Item_ID</yfc:i18n></td>
		<td nowrap="true" >
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGSupplierItem/@ItemID", "xml:/NWCGSupplierItem/@ItemID")%>/>
			<% String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("CommonFields", "xml:/CommonFields/@OrganizationCode")); 
			//System.out.println("extraParams==> JAY "+extraParams);
			%>
                    <img class="lookupicon" onclick="callItemLookup('xml:/NWCGSupplierItem/@ItemID','xml:/NWCGSupplierItem/@ProductClass','xml:/NWCGSupplierItem/@UnitOfMeasure','item','<%=extraParams%>')" 			<%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item") %> />
		</td>
		<td class="searchlabel" ><yfc:i18n>Product_Class</yfc:i18n></td>
		<td class="searchcriteriacell" nowrap="true">
        <select name="xml:/NWCGSupplierItem/@ProductClass" class="combobox" >
            <yfc:loopOptions binding="xml:ProductClass:/CommonCodeList/@CommonCode" 
                name="CodeValue" value="CodeValue" selected="xml:/NWCGSupplierItem/@ProductClass"/>
        </select>
		</td>
		<td  class="searchlabel" ><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
		<td class="searchcriteriacell" nowrap="true">
        <select name="xml:/NWCGSupplierItem/@UnitOfMeasure" class="combobox" >
            <yfc:loopOptions binding="xml:UnitOfMeasure:/ItemUOMMasterList/@ItemUOMMaster" 
                name="UnitOfMeasure" value="UnitOfMeasure" selected="xml:/NWCGSupplierItem/@UnitOfMeasure"/>
        </select>
		</td>
		</tr>
		<tr>
		<td class="detaillabel" ><yfc:i18n>Supplier_Part_Number</yfc:i18n></td>
		<td >
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGSupplierItem/@SupplierPartNo", "xml:/NWCGSupplierItem/@SupplierPartNo")%>/>
		</td>
		<td class="detaillabel" ><yfc:i18n>Supplier_Standard_Pack</yfc:i18n></td>
		<td>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGSupplierItem/@SupplierStandardPack", "xml:/NWCGSupplierItem/@SupplierStandardPack")%>/>
		</td>
		<td class="detaillabel" ><yfc:i18n>Unit_Cost</yfc:i18n></td>
		<td nowrap="true" >
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/NWCGSupplierItem/@UnitCost", "xml:/NWCGSupplierItem/@UnitCost")%>/>
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
	</tr>
	<tr>
    <td colspan="8" align="center">
		<input class="button" type="button" value="<%=getI18N("Proceed")%>" onclick="proceedClicked();" />
    </td>
</tr>

</table>
