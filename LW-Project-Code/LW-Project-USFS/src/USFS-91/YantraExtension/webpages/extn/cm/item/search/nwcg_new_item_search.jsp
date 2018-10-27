<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/im.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<%
	String orgCode = resolveValue("xml:CurrentOrganization:/Organization/@OrganizationCode");
	String currSelectedOrg = resolveValue("xml:/Item/@CallingOrganizationCode");

	if (!isVoid(currSelectedOrg)) { 
		setCommonFieldAttribute("OrganizationCode",currSelectedOrg);
	} else {
		setCommonFieldAttribute("OrganizationCode",orgCode);
	}
    String sItemID=getValue("Item","xml:/Item/@ItemID");
    
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
	String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>


<table class="view">
	<tr>
		<td class="searchlabel" ><yfc:i18n>Organization</yfc:i18n></td>
    </tr>
	<tr>
		<td nowrap="true">
            <select id="OrgCombo" onchange="changeSearchView(getCurrentSearchViewId());" <%=getComboOptions("xml:/Item/@CallingOrganizationCode")%> class="combobox">
				<option value="" selected="true"/>
            <% for (int i=0;i<invOrgList.size();i++) {
                String invOrgCode = (String) invOrgList.get(i); %>
                <option value="<%=invOrgCode%>" <%if(equals(invOrgCode,currSelectedOrg)) {%> selected <%}%>><%=invOrgCode%></option>
            <% } %>
			</select>
		</td>
    </tr> 
    <% // Now call the APIs that are dependent on the calling organization code %>
	<yfc:callAPI apiID="AP2"/>
    <yfc:callAPI apiID="AP3"/>

<tr>
    <td class="searchlabel" ><yfc:i18n>Item_ID</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/Item/@ItemIDQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/Item/@ItemIDQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Item/@ItemID") %> />
		<img class="lookupicon" name="search"
onclick="callItemLookup('xml:/Item/@ItemID','xml:/Item/@ProductClass','xml:/Item/@UnitOfMeasure',
'item','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item") %> />

    </td>
</tr>


<tr>
    <td class="searchlabel" ><yfc:i18n>NSN</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/Item/@GlobalItemIDQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/Item/@GlobalItemIDQryType"/>
        </select>
        <input type="text" size="20" class="unprotectedinput" maxlength="13" <%=getTextOptions("xml:/Item/@GlobalItemID") %> />            
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select name="xml:/Item/@UnitOfMeasure" class="combobox" >
            <yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure" value="UnitOfMeasure" selected="xml:/Item/@UnitOfMeasure"/>
        </select>
    </td>
</tr>
<tr>
    <td class="searchlabel" ><yfc:i18n>Short_Description</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell" >
        <select name="xml:/Item/PrimaryInformation/@ShortDescriptionQryType" class="combobox" >
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
                name="QueryTypeDesc" value="QueryType" selected="xml:/Item/PrimaryInformation/@ShortDescriptionQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Item/PrimaryInformation/@ShortDescription") %> />
    </td>
</tr>
</table>